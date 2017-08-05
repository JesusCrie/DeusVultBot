import java.io.BufferedReader;
import java.io.FileReader;

public class Test {

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(Test.class.getResource("test/test.txt").getFile()));
        String line;

        while ((line = br.readLine()) != null) {
            print(line);
        }
    }

    private static void print(Object o) {
        System.out.println(o);
    }
}
