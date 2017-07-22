import net.dv8tion.jda.core.utils.SimpleLog;

import java.util.Arrays;
import java.util.List;

public class Test {

    public static void main(String[] args) throws Exception {

        List<Object> a = Arrays.asList(null);
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
