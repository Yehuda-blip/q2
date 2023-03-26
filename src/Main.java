public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length > 0 && args[0].equals("test")) {
            BigIntTest.start();
            return;
        }

        BigIntCalculator calculator = new BigIntCalculator();
        calculator.start();
    }
}