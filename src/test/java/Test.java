import java.util.ArrayList;
import java.util.List;

public class Test {

    public static void main(String[] args) {
        Tester a = new Tester(0, 42);
        Tester b = new Tester(0, 666);
        Tester c = new Tester(1, 42);
        Tester d = new Tester(0, 42);

        List<Tester> ttt = new ArrayList<>();
        ttt.add(a);
        ttt.add(c);

        print(ttt.size());
        print(ttt.contains(b));
        print(ttt.contains(a));
        ttt.remove(b);
        print(ttt.size());
    }

    private static void print(Object o) {
        System.out.println(o);
    }

    private static class Tester {
        int id;
        int bite;

        public Tester(int id, int bite) {
            this.id = id;
            this.bite = bite;
        }

        @Override
        public boolean equals(Object obj) {
            return ((Tester) obj).id == id;
        }
    }
}
