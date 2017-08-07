import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Test {

    public static void main(String[] args) throws Exception {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.schedule(new Tester(0), 10, TimeUnit.SECONDS);
        service.schedule(new Tester(1), 10, TimeUnit.SECONDS);
        service.schedule(new Tester(2), 10, TimeUnit.SECONDS);
        service.schedule(new Tester(3), 10, TimeUnit.SECONDS);
        service.schedule(new Tester(4), 10, TimeUnit.SECONDS);
        service.schedule(new Tester(5), 10, TimeUnit.SECONDS);
        service.schedule(new Tester(6), 10, TimeUnit.SECONDS);
        service.schedule(new Tester(7), 10, TimeUnit.SECONDS);
        service.schedule(new Tester(8), 10, TimeUnit.SECONDS);
        service.schedule(new Tester(9), 10, TimeUnit.SECONDS);
        service.awaitTermination(15, TimeUnit.SECONDS);
        print(service.isShutdown());
        print(service.isTerminated());
    }

    private static class Tester implements Runnable {

        private final int id;

        public Tester(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            print(id);
        }
    }

    private static void print(Object o) {
        System.out.println(o);
    }
}
