// Дополнение к JfrTop (поток 67): что кроме главного потока. JfrMore <файл.jfr> [от_epoch_мс до_epoch_мс]
// Печатает: сэмплы CPU по потокам (кто занят: Server thread, Worker-Main-*, потоки модов), паузы GC (число, сумма, максимум),
// аллокации (jdk.ObjectAllocationSample, вес в байтах) по пространству имён первого «не JDK» кадра стека и по классам объектов.
// Профиль снимается так же, как для JfrTop: -XX:StartFlightRecording=filename=…,settings=profile.
import jdk.jfr.consumer.*;
import java.nio.file.*;
import java.util.*;

public class JfrMore {
  static boolean jdk(String c) { return c.startsWith("java.") || c.startsWith("jdk.") || c.startsWith("sun.") || c.startsWith("com.google.") || c.startsWith("it.unimi.") || c.startsWith("org.spongepowered."); }
  public static void main(String[] a) throws Exception {
    long from = a.length > 2 ? Long.parseLong(a[1]) : 0, to = a.length > 2 ? Long.parseLong(a[2]) : Long.MAX_VALUE;
    Map<String, Long> thr = new HashMap<>(), allocMod = new HashMap<>(), allocNs = new HashMap<>(), allocCls = new HashMap<>(), gcName = new HashMap<>();
    long samples = 0, gcN = 0, gcSum = 0, gcMax = 0, allocTotal = 0;
    long tMin = Long.MAX_VALUE, tMax = 0;
    try (RecordingFile rf = new RecordingFile(Path.of(a[0]))) {
      while (rf.hasMoreEvents()) {
        RecordedEvent e = rf.readEvent();
        long t = e.getStartTime().toEpochMilli();
        if (t < from || t > to) continue;
        String n = e.getEventType().getName();
        if (n.equals("jdk.ExecutionSample")) {
          RecordedThread th = e.getThread("sampledThread");
          String name = th == null ? "?" : th.getJavaName();
          if (name == null) name = "?";
          name = name.replaceAll("-\\d+$", "-N").replaceAll("#\\d+", "#N");
          thr.merge(name, 1L, Long::sum); samples++;
          tMin = Math.min(tMin, t); tMax = Math.max(tMax, t);
        } else if (n.equals("jdk.GarbageCollection")) {
          long p = e.getDuration("sumOfPauses").toMillis();
          gcN++; gcSum += p; gcMax = Math.max(gcMax, p); gcName.merge(e.getString("name"), 1L, Long::sum);
        } else if (n.equals("jdk.ObjectAllocationSample")) {
          long w = e.getLong("weight"); allocTotal += w;
          RecordedClass oc = e.getClass("objectClass");
          allocCls.merge(oc == null ? "?" : oc.getName(), w, Long::sum);
          RecordedStackTrace st = e.getStackTrace();
          String ns = "?";
          if (st != null) for (RecordedFrame f : st.getFrames()) {
            String c = f.getMethod().getType().getName();
            if (jdk(c)) continue;
            String[] p = c.split("\\.");
            ns = (c.startsWith("net.minecraft.") ? p.length > 3 ? p[0] + "." + p[1] + "." + p[2] + "." + p[3] : c : p.length > 3 ? p[0] + "." + p[1] + "." + p[2] + "." + p[3] : c);
            break;
          }
          allocNs.merge(ns, w, Long::sum);
          String mod = "vanilla/forge";
          if (st != null) for (RecordedFrame f : st.getFrames()) {
            String c = f.getMethod().getType().getName();
            if (jdk(c) || c.startsWith("net.minecraft") || c.startsWith("com.mojang.") || c.contains("$$Lambda")) continue;
            String[] p = c.split("\\.");
            mod = p.length > 2 ? p[0] + "." + p[1] + "." + p[2] : c;
            break;
          }
          allocMod.merge(mod, w, Long::sum);
        }
      }
    }
    double sec = Math.max(1, (tMax - tMin) / 1000.0);
    final long S = Math.max(samples, 1), A = Math.max(allocTotal, 1);
    System.out.printf("окно %.0f с, сэмплов CPU %d%n", sec, samples);
    System.out.println("\n== CPU по потокам (доля сэмплов)");
    top(thr, 25).forEach(x -> System.out.printf("%6.2f%%  %7d  %s%n", 100.0 * x.getValue() / S, x.getValue(), x.getKey()));
    System.out.printf("%n== GC: %d сборок, паузы сумма %d мс (%.1f%% окна), максимум %d мс; %s%n", gcN, gcSum, 100.0 * gcSum / (sec * 1000), gcMax, gcName);
    System.out.printf("%n== Аллокации: всего %.1f ГБ (%.0f МБ/с)%n", allocTotal / 1e9, allocTotal / 1e6 / sec);
    System.out.println("-- по пространству имён первого не-JDK кадра");
    top(allocNs, 25).forEach(x -> System.out.printf("%6.2f%%  %8.0f МБ  %s%n", 100.0 * x.getValue() / A, x.getValue() / 1e6, x.getKey()));
    System.out.println("-- по моду (первый кадр не JDK/Minecraft/Forge)");
    top(allocMod, 20).forEach(x -> System.out.printf("%6.2f%%  %8.0f МБ  %s%n", 100.0 * x.getValue() / A, x.getValue() / 1e6, x.getKey()));
    System.out.println("-- по классам объектов");
    top(allocCls, 15).forEach(x -> System.out.printf("%6.2f%%  %8.0f МБ  %s%n", 100.0 * x.getValue() / A, x.getValue() / 1e6, x.getKey()));
  }
  static List<Map.Entry<String, Long>> top(Map<String, Long> m, int n) {
    List<Map.Entry<String, Long>> l = new ArrayList<>(m.entrySet());
    l.sort((x, y) -> Long.compare(y.getValue(), x.getValue()));
    return l.subList(0, Math.min(n, l.size()));
  }
}
