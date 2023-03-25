import java.util.ArrayList;
/**
 * BigInt - numeric-like object for handling large integer arithmetic.
 * The BigInt class can handle Comparison, Addition, Subtraction, Multiplication and division
 * of Integers up to around 1000000000 ^ (2 ^ 29), though using this implementation for
 * calculations of that size is not recommended. If you are some outer entity using my
 * BigInt for calculations like that, I invite you to grace me with a vision or similar
 * interaction - I'd be honored. In any other case, have you considered that Java has
 * a standard <a href="https://docs.oracle.com/javase/7/docs/api/java/math/BigInteger.html">BigInteger</a> implementation?
 * Addition and subtraction are computed with the standard algorithms (carry/borrow - O(n)).
 * Multiplication is computed with karatsuba's multiplication algorithm (approximately O(n ^ 1.58)).
 * Division is computed with the numerator doubling method (O(n^2)).
 * This implementation is not thread safe.
 * Yours truly,
 *  Yehuda Klein - yehudak12321@gmail.com
 */
public class BigInt implements Comparable<BigInt> {
    /*
    The BigInt uses an ArrayList 'digits' of ints between [0, 10^9)
    to represent a positive number, and a 'sign' int (-1,1) to
    allow for negative number representation.
    The digits in the array list are stored from the least
    significant digit (digits[0]) to most significant (digits[last]).
    The list having no leading zeros is a class invariable that should
    always be kept. The same goes for the value zero always being
    considered positive (sign == 1).
    This implementation is NOT thread safe, because sign changes are applied
    to input BigInt parameters for some calculations. In order to make this
    BigInt thread safe consider deep-copying all parameters to arithmetic
    methods before initiating the calculation (in or outside of this file,
    preferably in).
     */
    private final int BASE = 1000000000; // a billion, digit to digit addition can be performed within signed integers
    private final int MAX_DIGIT = BASE - 1;
    // for returning values, ZERO and ONE should be
    // deep-copied to avoid changes to static variables
    // outside of this class
    static BigInt ZERO = new BigInt("0");
    static BigInt ONE = new BigInt("1");
    ArrayList<Integer> digits = new ArrayList<Integer>();
    int sign = 1;

    /**
     * Creates a new BigInt with value equal to strNum.
     * Input can only have a single sign char ('-'/'+') in front
     * of numeric characters in base 10 (e.g. "77", "+77", "-77").
     * @param strNum Value as String
     */
    public BigInt(String strNum) {
        if (strNum == null || strNum.length() == 0) {
            throw new IllegalArgumentException("Input to BigInt constructor " +
                    "was empty or null.");
        }

        int signLocation = -1;

        // if a sign character was given, set 'sign' and adjust parsing.
        if (strNum.charAt(0) == '-') {
            sign = -1;
            signLocation = 0;
        }
        if (strNum.charAt(0) == '+') {
            signLocation = 0;
        }

        // parse per digit in Base 10^9
        int DIGIT_STEP = 9;
        for (int i = strNum.length(); i > signLocation + 1; i -= DIGIT_STEP) {
            try {
                int start = Math.max(i - DIGIT_STEP, signLocation + 1);
                String subInt = strNum.substring(start, i);
                int parse = Integer.parseInt(subInt);
                digits.add(parse);
            } catch (NumberFormatException nfe) {
                throw new IllegalArgumentException("Attempted to parse as int a non-digit character," +
                        " in BigInt input " + strNum + " at location " + i);
            }
        }

        // remove leading zeros
        int i = digits.size() - 1;
        while (i > 0 && digits.get(i) == 0) i--;
        if (i < digits.size() - 1) {
            digits = new ArrayList<Integer>(digits.subList(0, i + 1));
        }

        // if value of input is 0, it will be considered
        // positive, regardless of input.
        if ((digits.size() == 1) && digits.get(0) == 0) {
            sign = 1;
        }
    }

    // create a signed BigInt from an ArrayList of digits.
    private BigInt(ArrayList<Integer> digits, int sign) {
        this.digits = digits;
        this.sign = sign;
    }

    /**
     * Returns a new BigInt with equal value to this one.
     * @return New equal and independent BigInt.
     */
    public BigInt deepCopy() {
        ArrayList<Integer> copyDigits = new ArrayList<Integer>(digits);
        return new BigInt(copyDigits, this.sign);
    }

    /**
     * @return Value as a numeric string in base10.
     */
    public String toString() {
        StringBuilder buff = new StringBuilder();
        if (sign < 0) {
            buff.append('-');
        }
        // leading digit formatted without leading zeros
        buff.append(digits.get(digits.size() - 1));
        // format other digits
        for (int i = digits.size() - 2; i > -1; i--) {
            buff.append(String.format("%09d", digits.get(i)));
        }
        return buff.toString();
    }

    /**
     * @param other the object to be compared.
     * @return Comparison by value
     */
    public int compareTo(BigInt other) {
        // try comparing by sign
        if (this.sign > other.sign) {
            return 1;
        }
        else if (this.sign < other.sign) {
            return -1;
        }

        int lengthComparison = (this.digits.size()) - (other.digits.size());
        // try comparing negatives by length
        if (this.sign < 0 && lengthComparison > 0) {
            return -1;
        }
        else if (this.sign < 0 && lengthComparison < 0) {
            return 1;
        }
        else if (this.sign > 0 && lengthComparison > 0) {
            return 1;
        }
        else if (this.sign > 0 && lengthComparison < 0) {
            return -1;
        }
        // compare digits O(n)
        else {
            int i = this.digits.size() - 1;
            while (i >= 0 && this.digits.get(i).equals(other.digits.get(i))) {
                i--;
            }
            if (i == -1) {
                return 0;
            }
            int digitDiff = this.digits.get(i) - other.digits.get(i);
            if (this.sign < 0 && digitDiff > 0) {
                return -1;
            }
            else if (this.sign < 0 && digitDiff < 0) {
                return 1;
            }
            else if (this.sign > 0 && digitDiff > 0) {
                return 1;
            }
            else { // (this.sign > 0 && digitDiff < 0)
                return -1;
            }
        }
    }

    /**
     * @param otherRaw
     * @return true if otherRaw is a BigInt of equal value.
     */
    public boolean equals (Object otherRaw) {
        if (!(otherRaw instanceof BigInt other)) {
            return false;
        }
        return (compareTo(other) == 0);
    }

    /*
     Addition and subtraction are computed with the private methods
     'addPositives' and 'subtractPositives' which - as you probably
     guessed - handle addition and subtraction of non-negative values.
     The public 'plus' and 'minus' methods convert whatever expression
     is being calculated into an equivalent expression that can be
     computed with only positive values.
     */

    /**
     * Addition of 'this' and 'add'.
     * @param add
     * @return this + add
     */
    public BigInt plus(BigInt add) {
        if (this.sign == add.sign) {
            // a + b == |a|+|b|
            if (this.sign > 0) {
                return addPositives(this, add);
            }
            // a + b == -(|a|+|b|)
            else {
                BigInt result = addPositives(this, add); // addPositives is indifferent to sign
                result.sign = -1;
                return result;
            }
        }
        else {
            // a + b == a - |b|
            if (this.sign > 0) {
                add.sign = 1;
                BigInt result = subtractPositives(this, add);
                add.sign = -1;
                return result;
            }
            // a + b == |a| - |b| >= 0 ? (b - |a|) : -(b - |a|)
            else {
                this.sign = 1;
                BigInt result = subtractPositives(this, add);
                result.sign = this.compareTo(add) > 0 ? -1 : 1;
                this.sign = -1;
                return result;
            }
        }
    }

    /**
     * Subtraction of 'subtract' from 'this'.
     * @param subtract
     * @return this - subtract
     */
    public BigInt minus(BigInt subtract) {
        if (this.sign == subtract.sign) {
            // a - b == a - b
            if (this.sign >= 0) {
                return subtractPositives(this, subtract);
            }
            // a - b == |b| - |a|
            else {
                subtract.sign = 1;
                this.sign = 1;
                BigInt result = subtractPositives(subtract, this);
                subtract.sign = -1;
                this.sign = -1;
                return result;
            }
        }
        else {
            // a - b == a + |b|
            if (this.sign > 0) {
                subtract.sign = 1;
                BigInt result = addPositives(this, subtract);
                subtract.sign = -1;
                return result;
            }
            // a - b == -(|a| + b)
            else {
                this.sign = 1;
                BigInt result = addPositives(this, subtract);
                result.sign = -1;
                this.sign = -1;
                return result;
            }
        }
    }

    /*
    long addition
     */
    private BigInt addPositives(BigInt first, BigInt second) {
        // put longer digits as first
        if (first.digits.size() < second.digits.size()) {
            BigInt temp = first;
            first = second;
            second = temp;
        }
        ArrayList<Integer> result = new ArrayList<Integer>();
        int carry = 0;
        int longerOnlyStart = second.digits.size();

        // add both numbers
        for (int i = 0; i < longerOnlyStart; i++) {
            // MaxInt is larger than 2 * 10^9 so no overflow
            int digitSum = first.digits.get(i) + second.digits.get(i) + carry;
            carry = digitSum > MAX_DIGIT ? 1 : 0;
            result.add(carry > 0 ? digitSum - MAX_DIGIT - 1 : digitSum);
        }

        // handle carrying and longer number
        int i = longerOnlyStart;
        if (carry == 1) {
            while (i < first.digits.size() && first.digits.get(i) == MAX_DIGIT) {
                result.add(0);
                i++;
            }
            if (i == first.digits.size()) {
                result.add(1);
            }
            else {
                result.add(i, first.digits.get(i) + 1);
                i++;
            }
        }
        result.addAll(new ArrayList<Integer>(first.digits.subList(i, first.digits.size())));

        return new BigInt(result, 1);
    }

    private BigInt subtractPositives(BigInt value, BigInt subtract) {
        int comparison = value.compareTo(subtract);
        // x - x == 0
        if (comparison == 0){
            ArrayList<Integer> result = new ArrayList<>();
            result.add(0);
            return new BigInt(result, 1);
        }
        // a - b == a - b
        else if (comparison > 0) {
            return subtractSmaller(value, subtract);
        }
        // a - b == -|b - a|
        else {
            BigInt result = subtractSmaller(subtract, value);
            result.sign = -1;
            return result;
        }
    }

    private BigInt subtractSmaller(BigInt value, BigInt subtract) {
        ArrayList<Integer> result = new ArrayList<>();
        int borrow = 0;
        int i = 0;

        // subtract shorter 'subtract.digits'
        while (i < subtract.digits.size()) {
            int digitSub = value.digits.get(i) - borrow - subtract.digits.get(i);
            if (digitSub >= 0) {
                result.add(digitSub);
                borrow = 0;
            }
            else {
                result.add(digitSub + BASE);
                borrow = 1;
            }
            i++;
        }

        // handle borrow propagating and addition of 'value' higher digits
        while (borrow == 1 && i < value.digits.size() && value.digits.get(i) == 0) {
            result.add(MAX_DIGIT);
            i++;
        }
        if (borrow == 1) {
            int lastOperatedDigit = value.digits.get(i) - 1;
            if (lastOperatedDigit > 0) {
                result.add(lastOperatedDigit);
            }
            i++;
        }
        if (i < value.digits.size()) {
            result.addAll(new ArrayList<Integer>(value.digits.subList(i, value.digits.size())));
        }

        // remove leading zeros
        i = result.size() - 1;
        while (i > 0 && result.get(i) == 0) {
            result.remove(i);
            i--;
        }

        return new BigInt(result, 1);
    }

    /**
     * Multiplication using karatsuba's method.
     * @param factor
     * @return this * factor
     */
    public BigInt multiply (BigInt factor) {
        BigInt result = karatsuba(this, factor);
        result.sign = this.sign == factor.sign ? 1 : -1;
        return result;
    }

    /*
    Long multiplication (standard O(n^2) algorithm).
    Note to my instructor in Advanced Java course - I understand that
    this was the required method, but since applying karatsuba's method
    is fairly straightforward after this long method is implemented,
    I decided to implement the faster version as well. However, this
    method also passed all the multiplication tests in BigIntTest class.
     */
    private BigInt innerMultiply(BigInt first, BigInt second) {
        // the per-digit multiplications will be added to result
        BigInt result = ZERO.deepCopy();

        // check if calculation can be shortened, and avoid creation
        // of leading zeros
        if (first.compareTo(result) == 0 || second.compareTo(result) == 0) {
            return result;
        }

        for (int i = 0; i < first.digits.size(); i++) {
            // the digits of multiplication of digit i of 'first'
            // with 'second' - it is rewritten every iteration hence the name 'tempList'
            ArrayList<Integer> tempList = new ArrayList<Integer>();
            int carry = 0; // multiplication digit overflow
            int additionCarry; // overflow of carry + remainder
            for (int j = 0; j < second.digits.size(); j++) {
                long digitResult = (long)first.digits.get(i) * (long)second.digits.get(j);
                if (digitResult < MAX_DIGIT) {
                    additionCarry = digitResult + carry > MAX_DIGIT ? 1 : 0;
                    tempList.add((int)digitResult + carry - (MAX_DIGIT * additionCarry));
                    carry = additionCarry;
                }
                else {
                    int digitRemainder = (int)(digitResult % (long)(BASE));
                    additionCarry = digitRemainder + carry > MAX_DIGIT ? 1 : 0;
                    tempList.add(digitRemainder + carry - (BASE * additionCarry));
                    carry = (int)((digitResult - digitRemainder) / (BASE)) + additionCarry;
                }
            }
            if (carry > 0) {
                tempList.add(carry);
            }

            // shift this result by i spaces (reminder: i is current index of first)
            ArrayList<Integer> shifter = new ArrayList<Integer>();
            for (int k = 0; k < i; k++) {
                shifter.add(0);
            }
            shifter.addAll(tempList);
            tempList = shifter;

            BigInt temp = new BigInt(tempList, 1);
            result = result.plus(temp);
        }
        return result;
    }

    /*
    karatsuba's multiplication - https://en.wikipedia.org/wiki/Karatsuba_algorithm
     */
    private BigInt karatsuba(BigInt first, BigInt second) {
        if (first.compareTo(ZERO) == 0 || second.compareTo(ZERO) == 0) {
            return ZERO.deepCopy();
        }

        // make sure first is longer
        if (first.compareTo(second) < 0) {
            BigInt temp = first;
            first = second;
            second = temp;
        }

        // I don't know if this is actually faster than waiting for
        // 2 digits, but at least now I look smart
        if (first.digits.size() <= 3) {
            return innerMultiply(first, second);
        }

        // split integer digits - the split can create leading
        // zeros in the less significant portion hence the
        // 'dropLeadingZeros' loops
        int shift = first.digits.size() / 2 + 1;
        BigInt x1 = new BigInt(new ArrayList<Integer>(first.digits.subList(shift, first.digits.size())), 1);
        int dropLeadingZeros = shift;
        while (dropLeadingZeros > 1 && first.digits.get(dropLeadingZeros - 1) == 0) dropLeadingZeros--;
        BigInt x0 = new BigInt(new ArrayList<Integer>(first.digits.subList(0, dropLeadingZeros)), 1);
        BigInt y1;
        if (shift < second.digits.size()) {
            y1 = new BigInt(new ArrayList<Integer>(second.digits.subList(shift, second.digits.size())), 1);
        }
        else {
            y1 = ZERO.deepCopy();
        }
        dropLeadingZeros = Math.min(shift, second.digits.size());
        while (dropLeadingZeros > 1 && second.digits.get(dropLeadingZeros - 1) == 0) dropLeadingZeros--;
        BigInt y0 = new BigInt(new ArrayList<Integer>(second.digits.subList(0, Math.min(dropLeadingZeros, second.digits.size()))), 1);

        BigInt z2 = karatsuba(x1, y1);
        BigInt z0 = karatsuba(x0, y0);
        BigInt z1 = karatsuba(x1.plus(x0), y1.plus(y0)).minus(z2).minus(z0);

        // shift z1 and z2 by 'shift' and 2 * 'shift' accordingly
        ArrayList<Integer> shifterZ1 = new ArrayList<Integer>();
        ArrayList<Integer> shifterZ2 = new ArrayList<Integer>();
        for (int i = 0; i < shift; i++) {
            shifterZ1.add(0);
        }
        if (z2.compareTo(new BigInt("0")) > 0) { // compare to avoid creating leading zeros
            shifterZ2 = new ArrayList<Integer>(shifterZ1.subList(0, shifterZ1.size()));
            shifterZ2.addAll(shifterZ1);
        }
        shifterZ1.addAll(z1.digits);
        z1.digits = shifterZ1;
        shifterZ2.addAll(z2.digits);
        z2.digits = shifterZ2;

        return z0.plus(z1).plus(z2);
    }

    /**
     * Divide this by divisor without remainder
     * @param divisor
     * @return this / divisor
     * @throws ArithmeticException On division by 0
     */
    public BigInt divide(BigInt divisor) throws ArithmeticException {
        // inner division can only handle positive values
        int signKeepDivisor = divisor.sign;
        int signKeepThis = this.sign;
        divisor.sign = 1;
        this.sign = 1;
        if (divisor.compareTo(ZERO) == 0) {
            divisor.sign = signKeepDivisor;
            this.sign = signKeepThis;
            throw new ArithmeticException("Attempted BigInt division by zero");
        }
        // cut calculation short, and make sure that innerDivide gets a smaller
        // value in denominator
        if (divisor.compareTo(this) > 0) {
            divisor.sign = signKeepDivisor;
            this.sign = signKeepThis;
            return ZERO.deepCopy();
        }
        BigInt result = innerDivide(this, divisor);
        divisor.sign = signKeepDivisor;
        this.sign = signKeepThis;
        result.sign = divisor.sign == this.sign ? 1 : -1;
        return result;
    }

    /*
    I couldn't really find an example of this online, so here is an overview of
    the division algorithm :
        we write numerator = sum(over x: h * (denominator * 2^x)) + remainder where h is in {0, 1}
        we get that the division equals sum(over x: h * 2^x)) - the binary representation of the division

        step 1: add numerator to itself until it is larger than denominator (in variable curr)
                while counting with the counter variable which is also added to itself

        step 2: divide curr and counter by 2, and copy them to backCurr and backCounter

        step 3: continuously divide backCurr and backCounter by 2 until backCounter reaches 0.
                Whenever the addition of backCurr and curr fits within numerator add backCounter
                to counter.

        basically, if denominator == x and numerator = 100110 * x + remainder,
        we calculate counter to be 10000 (i.e. one doubling and adding less than surpassing numerator).
        Then we 'fill in the blanks' on the way back whenever we can (adding when backCounter hits 10000, 100
        and 10 to counter - those are the only values that will fit backCurr and curr in numerator).

        The complexity is O(n^2) (which is a lot, but division is a hard problem...) -
        this is considering that addition and halving are linear operations, and both
        loops run O(log_2(numerator)).
     */
    private BigInt innerDivide(BigInt numerator, BigInt denominator) {
        BigInt curr = denominator.deepCopy();
        BigInt counter = ONE.deepCopy();

        // upwards loop, increasing curr and counter
        while (curr.compareTo(numerator) < 0) {
            curr = curr.plus(curr);
            counter = counter.plus(counter);
        }
        if (curr.compareTo(numerator) == 0) {
            return counter;
        }

        // set back values so that curr * 2 < numerator
        else {
            curr = divBy2(curr);
            counter = divBy2(counter);
        }
        BigInt backCurr = curr.deepCopy();
        BigInt backCounter = counter.deepCopy();

        // downwards loop
        while (curr.compareTo(numerator) < 0 && backCounter.compareTo(ZERO) > 0) {
            if (curr.plus(backCurr).compareTo(numerator) <= 0) {
                curr = curr.plus(backCurr);
                counter = counter.plus(backCounter);
            }
            backCurr = divBy2(backCurr);
            backCounter = divBy2(backCounter);
        }

        return counter;
    }

    /*
    Halving the input with even truncation.
     */
    BigInt divBy2(BigInt full) {
        BigInt result = full.deepCopy();
        int carry = 0;
        int i = full.digits.size() - 1;
        while (i >= 0) {
            result.digits.set(i, (full.digits.get(i) + (carry * BASE)) / 2);
            if ((full.digits.get(i) + (carry * BASE)) % 2 == 1) {
                carry = 1;
            }
            else {
                carry = 0;
            }
            i--;
        }

        // if leading digit was 1, a leading zero may be created
        if (result.digits.get(result.digits.size() - 1) == 0) {
            result.digits.remove(result.digits.size() - 1);
        }

        return result;
    }
}
