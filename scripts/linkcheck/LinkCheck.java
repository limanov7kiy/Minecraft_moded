import org.objectweb.asm.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

/**
 * Проверка связывания мода с остальной сборкой без запуска сервера: какие классы, методы и поля
 * других модов jar использует, а их нет (мод собран под другую версию зависимости).
 * Использование (см. scripts/linkcheck/linkcheck.sh):
 *   java -cp asm.jar LinkCheck.java <проверяемый.jar> <папка mods> <папка libraries> [префикс пакета...]
 * Без префиксов проверяются все ссылки, кроме java.*, javax.*, jdk.*, sun.*.
 * -Dlinkcheck.members=true — для отсутствующих классов печатать и используемые члены (что нужно переходнику).
 */
public class LinkCheck {
    record Member(String name, String desc) {}
    static class Cls { String sup; List<String> itf = new ArrayList<>(); Set<Member> members = new HashSet<>(); }
    static final Map<String, Cls> index = new HashMap<>();

    static void indexJar(InputStream in, boolean nestedToo) throws IOException {
        ZipInputStream z = new ZipInputStream(in);
        for (ZipEntry e; (e = z.getNextEntry()) != null; ) {
            String n = e.getName();
            byte[] b = z.readAllBytes();
            if (n.endsWith(".class") && !n.startsWith("META-INF/versions/")) indexClass(b);
            else if (nestedToo && n.endsWith(".jar")) indexJar(new ByteArrayInputStream(b), true);
        }
    }

    static void indexClass(byte[] b) {
        try {
            new ClassReader(b).accept(new ClassVisitor(Opcodes.ASM9) {
                Cls c;
                public void visit(int v, int a, String name, String sig, String sup, String[] itf) {
                    c = index.computeIfAbsent(name, k -> new Cls()); c.sup = sup; if (itf != null) c.itf.addAll(List.of(itf));
                }
                public FieldVisitor visitField(int a, String n, String d, String s, Object v) { c.members.add(new Member(n, d)); return null; }
                public MethodVisitor visitMethod(int a, String n, String d, String s, String[] e) { c.members.add(new Member(n, d)); return null; }
            }, ClassReader.SKIP_CODE);
        } catch (RuntimeException ignored) {}
    }

    /** у своего класса в цепочке предков есть класс, которого нет нигде (тогда член не проверить) */
    static boolean external(String owner) {
        for (Cls c = index.get(owner); c != null; c = index.get(c.sup)) {
            if (c.sup == null) return false;
            if (!index.containsKey(c.sup) && !c.sup.startsWith("java/")) return true;
        }
        return false;
    }

    static boolean has(String owner, Member m, Set<String> seen) {
        if (owner == null || !seen.add(owner)) return false;
        if (owner.startsWith("java/") || owner.startsWith("javax/") || owner.startsWith("jdk/")) return jdkHas(owner, m);
        Cls c = index.get(owner);
        if (c == null) return false;
        if (c.members.contains(m)) return true;
        if (has(c.sup, m, seen)) return true;
        for (String i : c.itf) if (has(i, m, seen)) return true;
        return false;
    }

    static final boolean MEMBERS = Boolean.getBoolean("linkcheck.members");

    /** член класса JDK (или его предков) — через reflection */
    static boolean jdkHas(String owner, Member m) {
        try {
            for (Class<?> c = Class.forName(owner.replace('/', '.'), false, ClassLoader.getSystemClassLoader()); c != null; c = c.getSuperclass()) {
                if (jdkDeclares(c, m)) return true;
                for (Class<?> i : allInterfaces(c)) if (jdkDeclares(i, m)) return true;
            }
            return false;
        } catch (ClassNotFoundException e) { return false; }
    }
    static List<Class<?>> allInterfaces(Class<?> c) {
        List<Class<?>> r = new ArrayList<>();
        for (Class<?> i : c.getInterfaces()) { r.add(i); r.addAll(allInterfaces(i)); }
        return r;
    }
    static boolean jdkDeclares(Class<?> c, Member m) {
        for (var x : c.getDeclaredMethods()) if (x.getName().equals(m.name()) && Type.getMethodDescriptor(x).equals(m.desc())) return true;
        for (var x : c.getDeclaredConstructors()) if (m.name().equals("<init>") && Type.getConstructorDescriptor(x).equals(m.desc())) return true;
        for (var x : c.getDeclaredFields()) if (x.getName().equals(m.name()) && Type.getDescriptor(x.getType()).equals(m.desc())) return true;
        return false;
    }

    public static void main(String[] a) throws Exception {
        Path target = Path.of(a[0]);
        List<String> prefixes = new ArrayList<>(List.of(a).subList(3, a.length));
        try (var s = Files.walk(Path.of(a[1]), FileVisitOption.FOLLOW_LINKS)) {
            for (Path p : s.filter(p -> p.toString().endsWith(".jar") && !p.equals(target)).toList())
                try (InputStream in = Files.newInputStream(p)) { indexJar(in, true); }
        }
        try (var s = Files.walk(Path.of(a[2]), FileVisitOption.FOLLOW_LINKS)) {
            for (Path p : s.filter(p -> p.toString().endsWith(".jar")).toList())
                try (InputStream in = Files.newInputStream(p)) { indexJar(in, false); }
        }
        Set<String> own = new HashSet<>();
        Map<String, byte[]> classes = new LinkedHashMap<>();
        try (ZipInputStream z = new ZipInputStream(Files.newInputStream(target))) {
            for (ZipEntry e; (e = z.getNextEntry()) != null; ) {
                byte[] b = z.readAllBytes();
                if (e.getName().endsWith(".class")) { indexClass(b); classes.put(e.getName(), b); }
                else if (e.getName().endsWith(".jar")) indexJar(new ByteArrayInputStream(b), true);
            }
        }
        for (byte[] b : classes.values()) own.add(new ClassReader(b).getClassName());

        Map<String, Set<String>> missing = new TreeMap<>();
        for (byte[] b : classes.values()) {
            ClassReader r = new ClassReader(b);
            String self = r.getClassName();
            r.accept(new ClassVisitor(Opcodes.ASM9) {
                void ref(String owner, String what) {
                    if (owner == null || owner.startsWith("[")) return;
                    if (own.contains(owner)) {
                        // свои классы есть всегда, но унаследованный от чужого класса член мог пропасть
                        if (what != null) {
                            String[] nd = what.split(" ", 2);
                            if (!has(owner, new Member(nd[0], nd[1]), new HashSet<>()) && !external(owner))
                                missing.computeIfAbsent(owner + "." + nd[0] + nd[1], k -> new TreeSet<>()).add(self);
                        }
                        return;
                    }
                    if (owner.startsWith("java/") || owner.startsWith("javax/") || owner.startsWith("jdk/") || owner.startsWith("sun/")) return;
                    if (!prefixes.isEmpty() && prefixes.stream().noneMatch(owner::startsWith)) return;
                    if (!index.containsKey(owner)) {
                        missing.computeIfAbsent("class " + owner, k -> new TreeSet<>()).add(self);
                        if (what != null && MEMBERS) missing.computeIfAbsent("  " + owner + "." + what.replace(" ", ""), k -> new TreeSet<>()).add(self);
                    }
                    else if (what != null) {
                        String[] nd = what.split(" ", 2);
                        if (!has(owner, new Member(nd[0], nd[1]), new HashSet<>()))
                            missing.computeIfAbsent(owner + "." + nd[0] + nd[1], k -> new TreeSet<>()).add(self);
                    }
                }
                void type(Type t) { while (t.getSort() == Type.ARRAY) t = t.getElementType(); if (t.getSort() == Type.OBJECT) ref(t.getInternalName(), null); }
                public void visit(int v, int acc, String n, String sig, String sup, String[] itf) {
                    ref(sup, null); if (itf != null) for (String i : itf) ref(i, null);
                }
                public MethodVisitor visitMethod(int acc, String n, String d, String s, String[] ex) {
                    return new MethodVisitor(Opcodes.ASM9) {
                        public void visitMethodInsn(int op, String o, String mn, String md, boolean itf) {
                            if (o.startsWith("[")) return; ref(o, mn + " " + md);
                        }
                        public void visitFieldInsn(int op, String o, String fn, String fd) { ref(o, fn + " " + fd); }
                        public void visitTypeInsn(int op, String t) { if (!t.startsWith("[")) ref(t, null); else type(Type.getType(t)); }
                        public void visitLdcInsn(Object v) {
                            if (v instanceof Type t && t.getSort() == Type.OBJECT) ref(t.getInternalName(), null);
                            if (v instanceof Handle h) handle(h);
                        }
                        // ссылки на методы (Foo::bar) и лямбды живут в аргументах invokedynamic
                        public void visitInvokeDynamicInsn(String n, String d, Handle bsm, Object... args) {
                            for (Object x : args) if (x instanceof Handle h) handle(h);
                        }
                        void handle(Handle h) { ref(h.getOwner(), h.getName() + " " + h.getDesc()); }
                    };
                }
            }, 0);
        }
        missing.forEach((k, v) -> System.out.println(k + "    <- " + String.join(", ", v.stream().limit(3).toList()) + (v.size() > 3 ? " …" : "")));
        System.out.println("missing: " + missing.size());
    }
}
