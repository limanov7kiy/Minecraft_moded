// Разбор профиля JFR: кто тратит время в обработчиках событий Forge. JfrHandlers <файл.jfr> [от_epoch_мс до_epoch_мс] [тред=Server thread]
// Для каждого сэмпла берётся самый глубокий кадр ASMEventHandler.invoke/EventBus.post, под ним — первый кадр чужого кода (обработчик мода).
// Печатает: доли по модам и по классам-обработчикам (inclusive: включая всё, что обработчик вызвал), и долю сэмплов без eventbus вообще.
import jdk.jfr.consumer.*;
import java.nio.file.*;
import java.util.*;

public class JfrHandlers {
  static String mod(String cls) {
    String[] p = cls.split("\\.");
    if (cls.startsWith("net.minecraft.") || cls.startsWith("net.minecraftforge.")) return p.length > 2 ? p[0] + "." + p[1] + "." + p[2] : cls;
    return p.length > 3 ? p[0] + "." + p[1] + "." + p[2] + "." + p[3] : cls;
  }
  static boolean bus(String c) { return c.startsWith("net.minecraftforge.eventbus.") || c.startsWith("cpw.mods.modlauncher") || c.contains("ASMEventHandler") || c.startsWith("java.") || c.startsWith("jdk.") || c.startsWith("sun."); }
  public static void main(String[] a) throws Exception {
    long from = a.length > 2 ? Long.parseLong(a[1]) : 0, to = a.length > 2 ? Long.parseLong(a[2]) : Long.MAX_VALUE;
    String thread = a.length > 3 ? a[3] : "Server thread";
    Map<String, Integer> byMod = new HashMap<>(), byCls = new HashMap<>(), byEvent = new HashMap<>();
    int total = 0, inBus = 0;
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
        List<RecordedFrame> fr = st.getFrames();
        total++;
        // самый внешний ASMEventHandler.invoke в стеке (кадры идут от вершины к основанию)
        int inv = -1;
        for (int i = 0; i < fr.size(); i++) if (fr.get(i).getMethod().getType().getName().contains("ASMEventHandler") && fr.get(i).getMethod().getName().equals("invoke")) inv = i;
        if (inv < 0) continue;
        inBus++;
        // обработчик: первый кадр ниже invoke (ближе к вершине), который не служебный
        String h = null; String hm = null;
        for (int i = inv - 1; i >= 0; i--) { String c = fr.get(i).getMethod().getType().getName(); if (!bus(c)) { h = c; hm = fr.get(i).getMethod().getName(); break; } }
        if (h == null) { h = "(сам eventbus/ядро)"; hm = ""; }
        byMod.merge(mod(h), 1, Integer::sum);
        byCls.merge(h + "." + hm, 1, Integer::sum);
        // тип события: внешний кадр выше invoke (post) и ещё выше — вызвавший код игры
        for (int i = inv + 1; i < fr.size(); i++) { String c = fr.get(i).getMethod().getType().getName(); if (!bus(c)) { byEvent.merge(c + "." + fr.get(i).getMethod().getName(), 1, Integer::sum); break; } }
      }
    }
    System.out.println("samples(" + thread + ")=" + total + "  из них внутри обработчиков eventbus=" + inBus + " (" + String.format("%.1f", 100.0 * inBus / Math.max(1, total)) + " %)");
    print("МОДЫ-обработчики (доля потока сервера, inclusive)", byMod, total, 25);
    print("ОБРАБОТЧИКИ (класс.метод)", byCls, total, 40);
    print("ОТКУДА ПОСТИТСЯ (код игры, вызвавший post)", byEvent, total, 25);
  }
  static void print(String title, Map<String, Integer> m, int total, int n) {
    System.out.println("\n== " + title);
    m.entrySet().stream().sorted((x, y) -> y.getValue() - x.getValue()).limit(n)
      .forEach(en -> System.out.printf("%6.2f %%  %s%n", 100.0 * en.getValue() / Math.max(1, total), en.getKey()));
  }
}
