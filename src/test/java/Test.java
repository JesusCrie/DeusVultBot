import java.util.Timer;
import java.util.TimerTask;

public class Test {

    public static void main(String[] args) {
        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                print("First call");
            }
        }, 3000);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                print("Second call");
            }
        }, 4000);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                print("Cleanup");
                timer.cancel();
            }
        }, 2000);
    }

    private static void print(Object o) {
        System.out.println(o);
    }
}
