import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Test {

    public static void main(String[] args) throws Exception {
        List<Tester> t = new ArrayList<>();
        t.add(new Tester(0));
        t.add(new Tester(20));
        t.add(new Tester(3));
        t.add(new Tester(5));
        t.add(new Tester(15));
        t.add(new Tester(55));
        t.add(new Tester(12));
        t.add(new Tester(1));

        List<Tester> top10 = t.stream()
                .sorted((p, n) -> -p.compareTo(n))
                .limit(3)
                .collect(Collectors.toList());

        top10.forEach(Test::print);
    }

    public static class Tester implements Comparable<Tester> {
        private int id;

        public Tester(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Tester && ((Tester) obj).id == id;
        }

        @Override
        public String toString() {
            return String.valueOf(id);
        }

        @Override
        public int compareTo(Tester o) {
            if (equals(o))
                return 0;
            return id > o.id ? 1 : -1;
        }
    }

    private static void print(Object o) {
        System.out.println(o);
    }
}
