public class Test {

    public static void main(String[] args) throws Exception {

        try {
            throw new Exception();
        } catch (NumberFormatException e) {
            print("Yo");
        }
    }


    private static void print(Object o) {
        System.out.println(o);
    }
}
