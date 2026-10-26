// «Чьё время» в потоке (поток 67): JfrMods <файл.jfr> [от_epoch_мс до_epoch_мс] [поток=Server thread]
// Каждый сэмпл приписывается первому (от вершины стека) кадру мода — классу не из JDK/Minecraft/Forge/библиотек;
// если такого кадра нет — «vanilla/forge». Плюс топ «самых верхних кадров мода» (метод мода, ближайший к вершине) и
// сколько стоят события Forge по типу события (кадр net.minecraftforge.eventbus.EventBus.post с аргументом не виден —
// берём класс события из имени слушателя-обёртки ASM: __Класс_метод_ИмяСобытия).
// Запуск: java -Dstdout.encoding=UTF-8 -cp <папка с классом> JfrMods rec.jfr <от> <до>
import jdk.jfr.consumer.*;
import java.nio.file.*;
import java.util.*;

public class JfrMods {
  static boolean lib(String c) {
    return c.startsWith("java.") || c.startsWith("jdk.") || c.startsWith("sun.") || c.startsWith("net.minecraft.") || c.startsWith("net.minecraftforge.")
      || c.startsWith("com.mojang.") || c.startsWith("it.unimi.") || c.startsWith("com.google.") || c.startsWith("org.spongepowered.")
      || c.startsWith("cpw.mods.") || c.startsWith("org.objectweb.") || c.startsWith("kotlin.") || c.startsWith("org.apache.") || c.startsWith("io.netty.");
  }
  static String mod(String c) {
    String[] p = c.split("\\.");
    return p.length > 2 ? p[0] + "." + p[1] + "." + p[2] : c;
  }
  public static void main(String[] a) throws Exception {
    long from = a.length > 2 ? Long.parseLong(a[1]) : 0, to = a.length > 2 ? Long.parseLong(a[2]) : Long.MAX_VALUE;
    String thread = a.length > 3 ? a[3] : "Server thread";
    Map<String, Integer> own = new HashMap<>(), meth = new HashMap<>(), ev = new HashMap<>();
    int total = 0;
    try (RecordingFile rf = new RecordingFile(Path.of(a[0]))) {
      while (rf.hasMoreEvents()) {
        RecordedEvent e = rf.readEvent();
        if (!e.getEventType().getName().equals("jdk.ExecutionSample")) continue;
        long t = e.getStartTime().toEpochMilli();
        if (t < from || t > to) continue;
        RecordedThread th = e.getThread("sampledThread");
        if (th == null || !thread.equals(th.getJavaName())) continue;
        RecordedStackTrace st = e.getStackTrace();
        if (st == null) continue;
        total++;
        String who = "vanilla/forge", m = null;
        for (RecordedFrame f : st.getFrames()) {
          String c = f.getMethod().getType().getName();
          if (c.contains("$$Lambda")) continue;
          if (!lib(c)) { who = mod(c); m = c + "." + f.getMethod().getName(); break; }
        }
        own.merge(who, 1, Integer::sum);
        if (m != null) meth.merge(m, 1, Integer::sum);
        // событие Forge: ближайший к низу стека (самый внешний) кадр слушателя-обёртки
        String evName = null;
        for (RecordedFrame f : st.getFrames()) {
          String c = f.getMethod().getType().getName();
          if (c.startsWith("net.minecraftforge.eventbus.ASMEventHandler") || c.startsWith("net.minecraftforge.eventbus.EventBus")) continue;
          int i = c.lastIndexOf('_');
          if (c.startsWith("net.minecraftforge.eventbus.") || !c.contains(".__")) continue;
          evName = i > 0 ? c.substring(i + 1) : c;
        }
        if (evName != null) ev.merge(evName, 1, Integer::sum);
      }
    }
    System.out.println("samples(" + thread + ")=" + total);
    print("Чьё время (первый кадр мода от вершины стека)", own, total, 35);
    print("Методы модов (верхний кадр мода)", meth, total, 30);
    print("События Forge по типу (сэмплы внутри слушателей)", ev, total, 15);
  }
  static void print(String title, Map<String, Integer> m, int total, int n) {
    System.out.println("\n== " + title);
    m.entrySet().stream().sorted((x, y) -> y.getValue() - x.getValue()).limit(n)
      .forEach(e -> System.out.printf("%6.2f%%  %6d  %s%n", 100.0 * e.getValue() / Math.max(total, 1), e.getValue(), e.getKey()));
  }
}
