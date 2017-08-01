import java.util.regex.Pattern;

public class Test {

    public static void main(String[] args) throws Exception {
        Pattern p = Pattern.compile("fuck this shit");
        String test = "Ilé bô l'bateau!";

        print(test.split(p.pattern()));
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
