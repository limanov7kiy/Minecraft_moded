// Доля сэмплов потока, в стеке которых есть кадр «Класс.метод», содержащий подстроку (поток 67).
// JfrGrep <файл.jfr> <от|0> <до|0> <поток> <подстрока>... ; подстрока вида «ServerChunkCache.getChunkBlocking» или «Entity.handler$».
// Для каждой подстроки печатает долю и топ-5 кадров-«родителей» (кадр под совпавшим) — кто вызывает.
import jdk.jfr.consumer.*;
import java.nio.file.*;
import java.util.*;

public class JfrGrep {
  public static void main(String[] a) throws Exception {
    long from = Long.parseLong(a[1]), to = Long.parseLong(a[2]); if (to == 0) to = Long.MAX_VALUE;
    String thread = a[3];
    List<String> pats = Arrays.asList(a).subList(4, a.length);
    int total = 0; int[] hit = new int[pats.size()];
    List<Map<String, Integer>> callers = new ArrayList<>();
    for (String p : pats) callers.add(new HashMap<>());
    try (RecordingFile rf = new RecordingFile(Path.of(a[0]))) {
      while (rf.hasMoreEvents()) {
        RecordedEvent e = rf.readEvent();
        if (!e.getEventType().getName().equals("jdk.ExecutionSample")) continue;
        long t = e.getStartTime().toEpochMilli();
        if (t < from || t > to) continue;
        RecordedThread th = e.getThread("sampledThread");
        if (th == null || !thread.equals(th.getJavaName())) continue;
        RecordedStackTrace st = e.getStackTrace(); if (st == null) continue;
        total++;
        List<RecordedFrame> fr = st.getFrames();
        for (int i = 0; i < pats.size(); i++) {
          for (int k = fr.size() - 1; k >= 0; k--) {   // самый внешний совпавший кадр
            RecordedFrame f = fr.get(k);
            String n = f.getMethod().getType().getName() + "." + f.getMethod().getName();
            if (n.contains(pats.get(i))) {
              hit[i]++;
              if (k + 1 < fr.size()) { RecordedFrame c = fr.get(k + 1); callers.get(i).merge(c.getMethod().getType().getName() + "." + c.getMethod().getName() + ":" + c.getLineNumber(), 1, Integer::sum); }
              break;
            }
          }
        }
      }
    }
    System.out.println("samples(" + thread + ")=" + total);
    for (int i = 0; i < pats.size(); i++) {
      System.out.printf("%6.2f%%  %6d  %s%n", 100.0 * hit[i] / Math.max(total, 1), hit[i], pats.get(i));
      final int T = Math.max(hit[i], 1);
      callers.get(i).entrySet().stream().sorted((x, y) -> y.getValue() - x.getValue()).limit(5)
        .forEach(x -> System.out.printf("          %5.1f%% от совпавших  %s%n", 100.0 * x.getValue() / T, x.getKey()));
    }
  }
}
