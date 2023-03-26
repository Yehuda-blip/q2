import javax.annotation.processing.SupportedSourceVersion;
import java.net.UnknownHostException;
import java.util.Scanner;

import static java.lang.System.exit;

public class BigIntCalculator {
    static final String USAGE_STRING = "" +
        "Usage: Enter two integer numbers consecutively. The addition, subtraction, multiplication and truncating division " +
            "of these will be outputted to the screen.";

    public void start() {
        Scanner scan = new Scanner(System.in);
        BigInt a = null;
        BigInt b = null;
        System.out.println(USAGE_STRING);
        while (a == null) {
            try {
                a = new BigInt(scan.nextLine());
            }
            catch (IllegalArgumentException iae) {
                System.out.println("Not a number");
            }
        }
        while (b == null) {
            try {
                b = new BigInt(scan.nextLine());
            }
            catch (IllegalArgumentException iae) {
                System.out.println("Not a number");
            }
        }
        System.out.println(a + " + " + b + " = " + a.plus(b));
        System.out.println(a + " - " + b + " = " + a.minus(b));
        System.out.println(a + " * " + b + " = " + a.multiply(b));
        if (!b.equals(BigInt.ZERO)) { // I realize that I can catch the ArithmeticException, but come on...
            System.out.println(a + " / " + b + " = " + a.divide(b));
        }
        else {
            System.out.println("Division by 0 is undefined");
        }
    }
}
