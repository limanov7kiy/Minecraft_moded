import org.objectweb.asm.*;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.SimpleRemapper;
import org.objectweb.asm.tree.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

/**
 * Порт jar мода под новые версии зависимостей без исходников: переписывает ссылки в байткоде по
 * файлу правил и добавляет классы-переходники. Код мода не меняется по смыслу — только адреса.
 * Что осталось несвязанным, показывает scripts/linkcheck.
 *
 * Правила (по одному в строке, # — комментарий; имена — как в байткоде, до переименований):
 *   class   old/Owner                 new/Owner             — все ссылки на тип (в т.ч. в дескрипторах)
 *   field   old/Owner.name            new/Owner.name        — чтение/запись поля (дескриптор тот же)
 *   factory old/Owner(DESC)V          new/Factory.method    — `new old/Owner(args)` → `new/Factory.method(args)`
 *                                                             (static, те же аргументы, возвращает новый тип)
 *   call    old/Owner.name(DESC)R     new/Owner.name(DESC)R — вызов метода/конструктора с новым адресом;
 *   scall   old/Owner.name(DESC)R     new/Owner.name(DESC)R — то же, но в статический метод (для вызова
 *                                                             на объекте получатель — первый аргумент).
 *           Если старый результат — объект, а новый другой, после вызова ставится checkcast к старому.
 *   bridge  name(OLDDESC)R  (NEWDESC)R2                     — каждому классу (и переходнику), где объявлен
 *           name(OLDDESC)R, добавить мост name(NEWDESC)R2, который его вызывает (аргументы приводятся).
 *   ownerinit old/Projectile  type/Registry.FIELD            — снаряд потерял конструктор (Level, LivingEntity):
 *           `super(level, owner)` → `super(FIELD.get(), level); setOwner(owner)` (FIELD — RegistryObject типа).
 *   prefix  old/pkg/                  new/pkg/              — все типы пакета (и подпакетов): перенос встроенной
 *           копии библиотеки в свой пакет (файлы классов переименовываются тоже), см. source.txt `embed`.
 * Порядок применения: factory → ownerinit → call/scall → field → bridge → class/prefix.
 *
 * Использование: java -cp asm.jar:asm-tree.jar:asm-commons.jar JarPort.java <in.jar> <out.jar> <rules.txt>
 *                [папка или jar с переходниками и ресурсами, добавляемыми в результат...]
 */
public class JarPort {
    record Factory(String owner, String desc, String fOwner, String fName) {}
    record Call(String owner, String name, String desc, String nOwner, String nName, String nDesc, boolean toStatic) {}
    record Bridge(String name, String oldDesc, String newDesc) {}
    record OwnerInit(String owner, String typeOwner, String typeField) {}

    static final Map<String, String> classes = new HashMap<>(), fields = new HashMap<>();
    static final List<Factory> factories = new ArrayList<>();
    static final List<Call> calls = new ArrayList<>();
    static final List<Bridge> bridges = new ArrayList<>();
    static final List<OwnerInit> ownerInits = new ArrayList<>();
    static final Map<String, Integer> used = new TreeMap<>();
    static final Map<String, String> prefixes = new LinkedHashMap<>();
    static SimpleRemapper remapper;

    static String[] member(String s) {   // owner.name(desc) → {owner, name, desc}
        int p = s.indexOf('('), d = s.lastIndexOf('.', p);
        return new String[]{s.substring(0, d), s.substring(d + 1, p), s.substring(p)};
    }

    public static void main(String[] a) throws Exception {
        for (String line : Files.readAllLines(Path.of(a[2]))) {
            line = line.replaceAll("#.*", "").trim();
            if (line.isEmpty()) continue;
            String[] p = line.split("\\s+");
            switch (p[0]) {
                case "class" -> classes.put(p[1], p[2]);
                case "field" -> fields.put(p[1], p[2]);
                case "prefix" -> prefixes.put(p[1], p[2]);
                case "factory" -> {
                    int i = p[1].indexOf('('), d = p[2].lastIndexOf('.');
                    factories.add(new Factory(p[1].substring(0, i), p[1].substring(i), p[2].substring(0, d), p[2].substring(d + 1)));
                }
                case "call", "scall" -> {
                    String[] o = member(p[1]), n = member(p[2]);
                    calls.add(new Call(o[0], o[1], o[2], n[0], n[1], n[2], p[0].equals("scall")));
                }
                case "bridge" -> {
                    int i = p[1].indexOf('(');
                    bridges.add(new Bridge(p[1].substring(0, i), p[1].substring(i), p[2]));
                }
                case "ownerinit" -> {
                    int d = p[2].lastIndexOf('.');
                    ownerInits.add(new OwnerInit(p[1], p[2].substring(0, d), p[2].substring(d + 1)));
                }
                default -> throw new IllegalArgumentException("неизвестное правило: " + line);
            }
        }
        remapper = new SimpleRemapper(classes) {
            @Override public String map(String key) {
                String v = super.map(key);
                if (v != null || key.indexOf('.') >= 0) return v;   // ключи членов («owner.name») не трогаем
                for (var e : prefixes.entrySet()) if (key.startsWith(e.getKey())) return e.getValue() + key.substring(e.getKey().length());
                return null;
            }
        };

        try (ZipInputStream in = new ZipInputStream(Files.newInputStream(Path.of(a[0])));
             ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(Path.of(a[1])))) {
            Set<String> written = new HashSet<>();
            for (ZipEntry e; (e = in.getNextEntry()) != null; ) {
                byte[] b = in.readAllBytes();
                String n = e.getName();
                if (n.startsWith("META-INF/") && (n.endsWith(".SF") || n.endsWith(".RSA") || n.endsWith(".DSA"))) continue;
                if (n.endsWith(".class")) b = transform(b);
                put(out, written, n, b);
            }
            for (int i = 3; i < a.length; i++) addAll(out, written, Path.of(a[i]));
        }
        used.forEach((k, v) -> System.out.println("  " + k + ": " + v));
    }

    static byte[] transform(byte[] b) {
        ClassNode cn = new ClassNode();
        new ClassReader(b).accept(cn, 0);
        for (MethodNode m : cn.methods) {
            applyFactories(cn, m);
            applyOwnerInits(m);
            for (AbstractInsnNode insn = m.instructions.getFirst(); insn != null; insn = insn.getNext()) {
                if (insn instanceof MethodInsnNode mi) insn = applyCall(m, mi);
                else if (insn instanceof FieldInsnNode fi) {
                    String to = fields.get(fi.owner + "." + fi.name);
                    if (to != null) {
                        int d = to.lastIndexOf('.');
                        fi.owner = to.substring(0, d); fi.name = to.substring(d + 1);
                        used.merge("field " + to, 1, Integer::sum);
                    }
                }
            }
            m.maxStack += 2;   // переходники не требуют больше, но checkcast/аргументы мостов — с запасом
        }
        applyBridges(cn);
        ClassWriter cw = new ClassWriter(0);
        cn.accept(new ClassRemapper(cw, remapper));
        return cw.toByteArray();
    }

    // factory: new X; dup; <args>; invokespecial X.<init>(desc) → <args>; invokestatic F.m(desc)T
    static void applyFactories(ClassNode cn, MethodNode m) {
        for (Factory f : factories) {
            Deque<TypeInsnNode> news = new ArrayDeque<>();
            for (AbstractInsnNode insn = m.instructions.getFirst(); insn != null; insn = insn.getNext()) {
                if (insn instanceof TypeInsnNode t && t.getOpcode() == Opcodes.NEW && t.desc.equals(f.owner)) news.push(t);
                else if (insn instanceof MethodInsnNode mi && mi.getOpcode() == Opcodes.INVOKESPECIAL
                         && mi.owner.equals(f.owner) && mi.name.equals("<init>") && !news.isEmpty()) {
                    TypeInsnNode t = news.pop();
                    if (!mi.desc.equals(f.desc)) continue;
                    // кадр стека между new и <init> ссылался бы на удаляемый new — такой случай не поддерживаем
                    for (AbstractInsnNode x = t; x != mi; x = x.getNext())
                        if (x instanceof FrameNode) throw new IllegalStateException(cn.name + "." + m.name + ": ветвление в аргументах " + f.owner);
                    AbstractInsnNode dup = t.getNext();
                    if (dup.getOpcode() != Opcodes.DUP) throw new IllegalStateException(cn.name + "." + m.name + ": new без dup");
                    m.instructions.remove(dup);
                    m.instructions.remove(t);
                    String ret = Optional.ofNullable(remapper.map(f.owner)).orElse(f.owner);
                    MethodInsnNode call = new MethodInsnNode(Opcodes.INVOKESTATIC, f.fOwner, f.fName,
                        f.desc.substring(0, f.desc.length() - 1) + "L" + ret + ";", false);
                    m.instructions.set(mi, call);
                    insn = call;
                    used.merge("factory " + f.owner, 1, Integer::sum);
                }
            }
        }
    }

    static final String LEVEL = "Lnet/minecraft/world/level/Level;", LIVING = "Lnet/minecraft/world/entity/LivingEntity;";

    static void applyOwnerInits(MethodNode m) {
        for (OwnerInit oi : ownerInits) {
            for (AbstractInsnNode insn = m.instructions.getFirst(); insn != null; insn = insn.getNext()) {
                if (!(insn instanceof MethodInsnNode mi) || mi.getOpcode() != Opcodes.INVOKESPECIAL || !mi.owner.equals(oi.owner)
                    || !mi.name.equals("<init>") || !mi.desc.equals("(" + LEVEL + LIVING + ")V")) continue;
                int tmp = m.maxLocals++;
                InsnList l = new InsnList();                                  // стек: this, level, owner
                l.add(new VarInsnNode(Opcodes.ASTORE, tmp));                  // this, level
                l.add(new FieldInsnNode(Opcodes.GETSTATIC, oi.typeOwner, oi.typeField, "Lnet/minecraftforge/registries/RegistryObject;"));
                l.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/registries/RegistryObject", "get", "()Ljava/lang/Object;", false));
                l.add(new TypeInsnNode(Opcodes.CHECKCAST, "net/minecraft/world/entity/EntityType"));
                l.add(new InsnNode(Opcodes.SWAP));                            // this, type, level
                MethodInsnNode init = new MethodInsnNode(Opcodes.INVOKESPECIAL, oi.owner, "<init>", "(Lnet/minecraft/world/entity/EntityType;" + LEVEL + ")V", false);
                l.add(init);
                l.add(new VarInsnNode(Opcodes.ALOAD, 0));
                l.add(new VarInsnNode(Opcodes.ALOAD, tmp));
                l.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, oi.owner, "m_5602_", "(Lnet/minecraft/world/entity/Entity;)V", false)); // Projectile.setOwner
                AbstractInsnNode last = l.getLast();
                m.instructions.insert(mi, l);
                m.instructions.remove(mi);
                insn = last;
                used.merge("ownerinit " + oi.owner, 1, Integer::sum);
            }
        }
    }

    static AbstractInsnNode applyCall(MethodNode m, MethodInsnNode mi) {
        for (Call c : calls) {
            if (!mi.owner.equals(c.owner) || !mi.name.equals(c.name) || !mi.desc.equals(c.desc)) continue;
            if (c.name.equals("<init>") && c.toStatic) throw new IllegalArgumentException("конструктор — правило factory");
            if (c.toStatic) { mi.setOpcode(Opcodes.INVOKESTATIC); mi.itf = false; }
            mi.owner = c.nOwner; mi.name = c.nName; mi.desc = c.nDesc;
            used.merge((c.toStatic ? "scall " : "call ") + c.owner + "." + c.name, 1, Integer::sum);
            Type oldRet = Type.getReturnType(c.desc), newRet = Type.getReturnType(c.nDesc);
            if (!oldRet.equals(newRet)) {
                if (oldRet.getSort() != Type.OBJECT) throw new IllegalArgumentException("разный примитивный результат: " + c);
                TypeInsnNode cast = new TypeInsnNode(Opcodes.CHECKCAST, oldRet.getInternalName());
                m.instructions.insert(mi, cast);
                return cast;
            }
            return mi;
        }
        return mi;
    }

    static void applyBridges(ClassNode cn) {
        boolean itf = (cn.access & Opcodes.ACC_INTERFACE) != 0;
        for (Bridge br : bridges) {
            MethodNode target = null;
            for (MethodNode m : cn.methods)
                if (m.name.equals(br.name) && m.desc.equals(br.oldDesc) && (m.access & Opcodes.ACC_STATIC) == 0) target = m;
            if (target == null || cn.methods.stream().anyMatch(m -> m.name.equals(br.name) && m.desc.equals(br.newDesc))) continue;
            MethodNode mb = new MethodNode(Opcodes.ACC_PUBLIC | Opcodes.ACC_SYNTHETIC | Opcodes.ACC_BRIDGE, br.name, br.newDesc, null, null);
            InsnList l = mb.instructions;
            l.add(new VarInsnNode(Opcodes.ALOAD, 0));
            Type[] nArgs = Type.getArgumentTypes(br.newDesc), oArgs = Type.getArgumentTypes(br.oldDesc);
            int slot = 1;
            for (int i = 0; i < nArgs.length; i++) {
                l.add(new VarInsnNode(nArgs[i].getOpcode(Opcodes.ILOAD), slot));
                if (!nArgs[i].equals(oArgs[i])) l.add(new TypeInsnNode(Opcodes.CHECKCAST, oArgs[i].getInternalName()));
                slot += nArgs[i].getSize();
            }
            l.add(new MethodInsnNode(itf ? Opcodes.INVOKEINTERFACE : Opcodes.INVOKEVIRTUAL, cn.name, br.name, br.oldDesc, itf));
            l.add(new InsnNode(Type.getReturnType(br.newDesc).getOpcode(Opcodes.IRETURN)));
            mb.maxLocals = slot;
            mb.maxStack = slot + 1;
            cn.methods.add(mb);
            used.merge("bridge " + br.name + br.newDesc, 1, Integer::sum);
        }
    }

    static void put(ZipOutputStream out, Set<String> written, String name, byte[] b) throws IOException {
        if (name.endsWith(".class")) {   // перенесённый класс — и файл под новым именем
            String to = remapper.map(name.substring(0, name.length() - 6));
            if (to != null) name = to + ".class";
        }
        if (!written.add(name)) return;   // первым пишется оригинал; переходники не перекрывают классы мода
        ZipEntry e = new ZipEntry(name);
        e.setTime(0);
        out.putNextEntry(e);
        out.write(b);
        out.closeEntry();
    }

    static void addAll(ZipOutputStream out, Set<String> written, Path src) throws IOException {
        if (Files.isDirectory(src)) {
            try (var s = Files.walk(src)) {
                for (Path p : s.filter(Files::isRegularFile).sorted().toList()) {
                    String n = src.relativize(p).toString().replace(File.separatorChar, '/');
                    byte[] b = Files.readAllBytes(p);
                    put(out, written, n, n.endsWith(".class") ? transform(b) : b);
                }
            }
        } else {
            try (ZipInputStream in = new ZipInputStream(Files.newInputStream(src))) {
                for (ZipEntry e; (e = in.getNextEntry()) != null; ) {
                    if (e.isDirectory() || e.getName().startsWith("META-INF/")) continue;
                    byte[] b = in.readAllBytes();
                    put(out, written, e.getName(), e.getName().endsWith(".class") ? transform(b) : b);
                }
            }
        }
    }
}
