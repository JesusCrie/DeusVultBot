import net.dv8tion.jda.core.utils.SimpleLog;

public class Test {

    public static void main(String[] args) throws Exception {

        // 0011
        // 0101
    }

    public static class DiscordLogListner implements SimpleLog.LogListener {

        @Override
        public void onLog(SimpleLog log, SimpleLog.Level logLevel, Object message) {
            print(String.format("-> %1$s || %2$s", message, logLevel.getTag()));
        }

        @Override
        public void onError(SimpleLog log, Throwable err) {
            print("=>> ERROR " + err);
        }
    }

    private static void print(Object o) {
        System.out.println(o);
    }
}
