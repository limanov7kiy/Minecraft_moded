// Разбор профиля JFR серверного потока: JfrTop <файл.jfr> [от_epoch_мс до_epoch_мс] [тред=Server thread]
// Печатает: топ «самих» методов (верхний кадр стека) и долю по пространствам имён/модам во всём стеке (inclusive).
// Профиль снимается JVM-флагом -XX:StartFlightRecording=filename=…,settings=profile (scripts/test-server.sh: SERVER_JVM_EXTRA).
import jdk.jfr.consumer.*;
import java.nio.file.*;
import java.util.*;

public class JfrTop {
  static String ns(String cls) {
    String[] p = cls.split("\\.");
    if (cls.startsWith("net.minecraft.") || cls.startsWith("net.minecraftforge.")) return p.length > 3 ? p[0] + "." + p[1] + "." + p[2] : cls;
    return p.length > 3 ? p[0] + "." + p[1] + "." + p[2] + "." + p[3] : cls;
  }
  public static void main(String[] a) throws Exception {
    long from = a.length > 2 ? Long.parseLong(a[1]) : 0, to = a.length > 2 ? Long.parseLong(a[2]) : Long.MAX_VALUE;
    String thread = a.length > 3 ? a[3] : "Server thread";
    Map<String, Integer> self = new HashMap<>(), incl = new HashMap<>(), selfNs = new HashMap<>();
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
        if (st == null || st.getFrames().isEmpty()) continue;
        total++;
        List<RecordedFrame> fr = st.getFrames();
        RecordedMethod m0 = fr.get(0).getMethod();
        self.merge(m0.getType().getName() + "." + m0.getName(), 1, Integer::sum);
        selfNs.merge(ns(m0.getType().getName()), 1, Integer::sum);
        Set<String> seen = new HashSet<>();
        for (RecordedFrame f : fr) seen.add(ns(f.getMethod().getType().getName()));
        for (String s : seen) incl.merge(s, 1, Integer::sum);
      }
    }
    System.out.println("samples(" + thread + ")=" + total);
    print("INCLUSIVE по пространствам имён (доля тиков, где пакет есть в стеке)", incl, total, 30);
    print("SELF по пространствам имён (верхний кадр)", selfNs, total, 20);
    print("SELF по методам", self, total, 25);
  }
  static void print(String title, Map<String, Integer> m, int total, int n) {
    System.out.println("\n== " + title);
    m.entrySet().stream().sorted((x, y) -> y.getValue() - x.getValue()).limit(n)
      .forEach(e -> System.out.printf("%6.2f%%  %6d  %s%n", 100.0 * e.getValue() / Math.max(total, 1), e.getValue(), e.getKey()));
  }
}
