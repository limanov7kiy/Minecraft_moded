import java.lang.instrument.Instrumentation;

/**
 * Java-агент для scripts/test-client.sh: печатает стек каждого System.exit / Runtime.halt.
 * Некоторые моды завершают игру сами, без краш-репорта (так нашли ImmediatelyFast × Oculus 1.8:
 * IrisCompat.init → System.exit(-1)). Нужен -Djava.security.manager=allow (Java 17).
 */
public class ExitTrace {
    public static void premain(String args, Instrumentation inst) {
        System.setSecurityManager(new SecurityManager() {
            @Override public void checkPermission(java.security.Permission p) {}
            @Override public void checkPermission(java.security.Permission p, Object ctx) {}
            @Override public void checkExit(int status) {
                new Throwable("EXIT TRACE status=" + status + " thread=" + Thread.currentThread().getName()).printStackTrace();
                System.err.flush();
            }
        });
    }
}
