import java.util.ArrayList;
import java.util.List;

public class Test {

    public static void main(String[] args) throws Exception {
        List<Tester> t = new ArrayList<>();
        t.add(new Tester(10));
        t.add(new Tester(453));
        t.add(new Tester(12));
        t.add(new Tester(0));
        t.add(new Tester(5));

        t.sort(Tester::compareTo);

        print(t);
    }

    public static class Tester implements Comparable<Tester> {

        private int id;
        public Tester(int i) {
            id = i;
        }

        public int getId() {
            return id;
        }

        @Override
        public int compareTo(Tester o) {
            if (o.id == id)
                return 0;
            return id > o.id ? 1 : -1;
        }

        @Override
        public String toString() {
            return String.valueOf(id);
        }
    }


    private static void print(Object o) {
        System.out.println(o);
    }
}
