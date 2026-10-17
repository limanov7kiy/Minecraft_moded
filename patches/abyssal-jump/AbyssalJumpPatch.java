import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

/**
 * Уменьшает усиление «Прыгучести» у «Охотников Бездны» в arknights_weapon (MCreator, всё зашито в байткоде):
 * в трёх классах находит `new MobEffectInstance(MobEffects.JUMP, длительность, УСИЛЕНИЕ, ...)` и заменяет УСИЛЕНИЕ
 * на константу (по умолчанию 2 = Прыгучесть III ≈ 3,3 блока). Остальные эффекты и классы не трогаются.
 *   AbyssalHuntersMobEffect (эффект «Глубинный охотник», игла): усиление 10 (+уровень) → 2 (+уровень), две копии (себе и союзникам рядом)
 *   AbyssalHuntersSuitboots1Procedure (ботинки образца «Агор»): 6 → 2
 *   AbyssalHuntersSuitleggings1Procedure (поножи образца «Агор»): 3 → 2
 * Использование: java -cp asm.jar:asm-tree.jar AbyssalJumpPatch.java <in.jar> <out.jar> [усиление]
 */
public class AbyssalJumpPatch {
    static final String JUMP = "f_19603_";   // MobEffects.JUMP (SRG)
    static final Set<String> TARGETS = Set.of(
        "net/mcreator/arknightsweaopn/potion/AbyssalHuntersMobEffect.class",
        "net/mcreator/arknightsweaopn/procedures/AbyssalHuntersSuitboots1Procedure.class",
        "net/mcreator/arknightsweaopn/procedures/AbyssalHuntersSuitleggings1Procedure.class");

    public static void main(String[] a) throws Exception {
        int amp = a.length > 2 ? Integer.parseInt(a[2]) : 2;
        int total = 0;
        try (ZipFile in = new ZipFile(a[0]); ZipOutputStream out = new ZipOutputStream(new FileOutputStream(a[1]))) {
            for (Enumeration<? extends ZipEntry> e = in.entries(); e.hasMoreElements(); ) {
                ZipEntry ze = e.nextElement();
                byte[] data = in.getInputStream(ze).readAllBytes();
                if (TARGETS.contains(ze.getName())) {
                    int[] n = {0};
                    data = patch(data, amp, n);
                    System.out.println(ze.getName() + ": заменено " + n[0]);
                    total += n[0];
                }
                ZipEntry ne = new ZipEntry(ze.getName());
                ne.setTime(ze.getTime());
                out.putNextEntry(ne); out.write(data); out.closeEntry();
            }
        }
        if (total != 4) throw new IllegalStateException("ожидалось 4 замены (2 в эффекте, ботинки, поножи), получено " + total);
    }

    static byte[] patch(byte[] cls, int amp, int[] count) {
        ClassNode cn = new ClassNode();
        new ClassReader(cls).accept(cn, 0);
        for (MethodNode m : cn.methods) {
            for (AbstractInsnNode i = m.instructions.getFirst(); i != null; i = i.getNext()) {
                if (!(i instanceof FieldInsnNode f) || f.getOpcode() != Opcodes.GETSTATIC || !f.name.equals(JUMP)) continue;
                AbstractInsnNode dur = i.getNext();                 // длительность
                AbstractInsnNode ampInsn = dur == null ? null : dur.getNext();   // усиление
                if (ampInsn == null) continue;
                int op = ampInsn.getOpcode();
                if (op != Opcodes.BIPUSH && !(op >= Opcodes.ICONST_0 && op <= Opcodes.ICONST_5)) continue;
                m.instructions.set(ampInsn, new IntInsnNode(Opcodes.BIPUSH, amp));
                count[0]++;
            }
        }
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        return cw.toByteArray();
    }
}
