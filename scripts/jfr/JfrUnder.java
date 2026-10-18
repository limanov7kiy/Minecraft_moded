// Что делает метод внутри: JfrUnder <файл.jfr> <подстрока_класса.метода> [от до] — по сэмплам, где в стеке есть кадр с подстрокой,
// печатает топ «вершин» (самих методов) и топ вызываемых из него методов на 1–3 уровня ниже. Тред — Server thread.
import jdk.jfr.consumer.*;
import java.nio.file.*;
import java.util.*;

public class JfrUnder {
  public static void main(String[] a) throws Exception {
    String key = a[1]; long from = a.length > 3 ? Long.parseLong(a[2]) : 0, to = a.length > 3 ? Long.parseLong(a[3]) : Long.MAX_VALUE;
    Map<String, Integer> top = new HashMap<>(), c1 = new HashMap<>(), c2 = new HashMap<>(); int n = 0, total = 0;
    try (RecordingFile rf = new RecordingFile(Path.of(a[0]))) {
      while (rf.hasMoreEvents()) {
        RecordedEvent e = rf.readEvent();
        if (!e.getEventType().getName().equals("jdk.ExecutionSample")) continue;
        long t = e.getStartTime().toEpochMilli(); if (t < from || t > to) continue;
        RecordedThread th = e.getThread("sampledThread"); if (th == null || !"Server thread".equals(th.getJavaName())) continue;
        total++;
        List<RecordedFrame> fr = e.getStackTrace().getFrames(); int idx = -1;
        for (int i = fr.size() - 1; i >= 0; i--) { RecordedMethod m = fr.get(i).getMethod(); if ((m.getType().getName() + "." + m.getName()).contains(key)) { idx = i; break; } }
        if (idx < 0) continue; n++;
        top.merge(nm(fr.get(0)), 1, Integer::sum);
        if (idx >= 1) c1.merge(nm(fr.get(idx - 1)), 1, Integer::sum);
        if (idx >= 2) c2.merge(nm(fr.get(idx - 2)), 1, Integer::sum);
      }
    }
    System.out.println("сэмплов с '" + key + "': " + n + " из " + total + " (" + String.format("%.1f", 100.0 * n / Math.max(1, total)) + " %)");
    p("ВЕРШИНА стека", top, n); p("вызывает (ур.1)", c1, n); p("вызывает (ур.2)", c2, n);
  }
  static String nm(RecordedFrame f) { return f.getMethod().getType().getName() + "." + f.getMethod().getName() + ":" + f.getLineNumber(); }
  static void p(String t, Map<String, Integer> m, int n) { System.out.println("\n== " + t); m.entrySet().stream().sorted((x, y) -> y.getValue() - x.getValue()).limit(10).forEach(e -> System.out.printf("%6.1f %%  %s%n", 100.0 * e.getValue() / Math.max(1, n), e.getKey())); }
}
