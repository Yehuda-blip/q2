public class BigIntTest {
    final static String FAIL = "FAIL";
    static String[][] constructorTests = {
            //  { input, result.tosString() }

            // one digit tests
            { "0", "0" },
            { "+0", "0" },
            { "-0", "0" },
            { "1", "1" },
            { "-1", "-1"},
            { "+1", "1"},
            { "9", "9" },
            { "-9", "-9"},
            { "+9", "9"},
            { "10", "10" },
            { "-10", "-10"},
            { "+10", "10"},
            { "999999999", "999999999" },
            { "-999999999", "-999999999"},
            { "+999999999", "999999999"},

            // 2 or more digit tests
            { "1000000000", "1000000000" },
            { "-1000000000", "-1000000000"},
            { "+1000000000", "1000000000"},
            { "1000000001", "1000000001" },
            { "-1000000001", "-1000000001"},
            { "+1000000001", "1000000001"},
            { "21283648485736", "21283648485736" },
            { "-21283648485736", "-21283648485736"},
            { "+21283648485736", "21283648485736"},
            { "21283600000736", "21283600000736" },
            { "-21283600000736", "-21283600000736"},
            { "+21283600000736", "21283600000736"},
            { "0000000000836736", "836736" },
            { "-0000000000836736", "-836736"},
            { "+0000000000836736", "836736"},
            { "0000000000836736342857961856", "836736342857961856" },
            { "-0000000000836736342857961856", "-836736342857961856"},
            { "+0000000000836736342857961856", "836736342857961856"},

            // failure tests
            { "******", FAIL },
            { "432576(34756", FAIL },
            { "4aa76(34756", FAIL },
            { "4aa76(34756", FAIL },
            { "", FAIL },
            { null, FAIL },

    };

    static void testConstructor() throws Exception {
        for (int i = 0; i < constructorTests.length; i++) {
            System.out.print("constructor test " + i + ": ");
            String[] test = constructorTests[i];
            if (test[1] == FAIL) {
                boolean failed = false;
                try {
                    new BigInt(test[0]);
                } catch (IllegalArgumentException e) {
                    failed = true;
                }
                if (!failed) throw new Exception("should fail but did not");
            }
            else {
                BigInt result = new BigInt(test[0]);
                if (!(result.toString().equals(test[1]))) throw new Exception("result not equal");
            }
            System.out.println("PASSED");
        }
    }

    static final String EQUAL = "EQUAL";
    static final String LARGER = "LARGER";
    static final String SMALLER = "SMALLER";

    static String[][] compareTests = {
            //  { this, other, result }

            { "0", "0", EQUAL},
            { "0", "1", SMALLER},
            { "1", "0", LARGER},
            { "-1", "-1", EQUAL},
            { "1", "1", EQUAL},
            { "-1", "1", SMALLER},
            { "1", "-1", LARGER},
            { "1000000000", "999999999", LARGER},
            { "-1000000000", "999999999", SMALLER},
            { "1000000000", "-999999999", LARGER},
            { "2345897634587298769325", "2345897634587298760025", LARGER},
            { "2345897634587298769325", "2345897634007298769325", LARGER},
            { "-2345897634587298769325", "2345897634007298769325", SMALLER},
            { "-2345897634587298769325", "2345897634587298760025", SMALLER}
    };

    static void compareTests() throws Exception {
        for (int i = 0; i < compareTests.length; i++) {
            System.out.print("compareTo test " + i + ": ");
            String[] test = compareTests[i];
            BigInt first = new BigInt(test[0]);
            BigInt second = new BigInt(test[1]);
            int result = first.compareTo(second);
            if ((result > 0 && !test[2].equals(LARGER))  ||
                    (result < 0 && !test[2].equals(SMALLER)) ||
                    (result == 0 && !test[2].equals(EQUAL)))  throw new Exception("result not equal");
            System.out.println("PASSED");
        }
    }

    static String[][] plusTestsSimple = {
            //  { value, add, result }

            { "0", "0", "0"},
            { "0", "1", "1"},
            { "1", "0", "1"},
            { "-1", "-1", "-2"},
            { "1", "1", "2"},
            { "-1", "1", "0"},
            { "1", "-1", "0"},
            { "1000000000", "999999999", "1999999999"},
            { "1", "999999999", "1000000000"},
            { "-1000000000", "999999999", "-1"},
            { "1000000000", "-999999999", "1"},
            { "1000000000000000000", "999999999999999999", "1999999999999999999"},
            { "1", "999999999999999999999999999", "1000000000000000000000000000"},
            { "-1000000000000000000", "999999999999999999", "-1"},
            { "1000000000000000000", "-999999999999999999", "1"},
            { "1000000000000000000000000000", "-999999999999999999999999999", "1"},
            { "-1000000000000000000000000000", "999999999999999999999999999", "-1"},
            { "1000000000000000000", "1000000000000000000", "2000000000000000000"},
            { "900000000000000000", "900000000000000000", "1800000000000000000"},
            { "333333333333444444444444", "777777777777888888888888", "1111111111111333333333332"},
            { "333333333333444444444444", "-777777777777888888888888", "-444444444444444444444444"},
            { "777777777777888888888888", "333333333333444444444444", "1111111111111333333333332"},
            {  "-777777777777888888888888", "333333333333444444444444", "-444444444444444444444444"},
            { "333333333333444444444444", "666666666666777777777777888888888888", "666666666667111111111111333333333332"},
            { "333333333333444444444444", "-666666666666777777777777888888888888", "-666666666666444444444444444444444444"},
            { "666666666666777777777777888888888888", "333333333333444444444444", "666666666667111111111111333333333332"},
            {  "-666666666666777777777777888888888888", "333333333333444444444444", "-666666666666444444444444444444444444"},
            { "33333333333344444444444400", "666666666666777777777777888888888888", "666666666700111111111122333333333288"},
            { "33333333333344444444444400", "-666666666666777777777777888888888888", "-666666666633444444444433444444444488"},
            { "999999998999999999000000001", "999999998999999999000000001000000000", "999999999999999998000000000000000001"},
            { "999999998999999999000000001", "-999999998999999999000000001000000000", "-999999998000000000000000001999999999"},
            { "10000000000999999990000000000000000001", "89999999999999999991000000000000000000", "100000000000999999981000000000000000001"},
            { "2345897634587298769325", "-2345897634587298769325", "0"},
            { "-2345897634587298769325", "2345897634587298769325", "0"},
            { "8345897634587298769325", "8345897634587298769325", "16691795269174597538650"},
            { "2384572698475238475264264", "34967520520948720982845982342934734", "34967520523333293681321220818198998"},
            { "34925847562348756298347562984375623847564927568456", "49320587483967834928760984572349", "34925847562348756347668150468343458776325912140805"},
            { "49320587483967834928760984572349", "34925847562348756298347562984375623847564927568456", "34925847562348756347668150468343458776325912140805"},
            { "49320587483967834928760984572349", "-34925847562348756298347562984375623847564927568456", "-34925847562348756249026975500407788918803942996107"},
            { "-49320587483967834928760984572349", "-34925847562348756298347562984375623847564927568456", "-34925847562348756347668150468343458776325912140805"},
            { "-34925847562348756298347562984375623847564927568456", "49320587483967834928760984572349", "-34925847562348756249026975500407788918803942996107"},
            { "-34925847562348756298347562984375623847564927568456", "-49320587483967834928760984572349", "-34925847562348756347668150468343458776325912140805"}
    };

    static void plusTests() throws Exception {
        for (int i = 0; i < plusTestsSimple.length; i++) {
            System.out.print("plus test simple " + i + ": ");
            String[] test = plusTestsSimple[i];
            BigInt first = new BigInt(test[0]);
            BigInt second = new BigInt(test[1]);
            BigInt expected = new BigInt(test[2]);
            BigInt result = first.plus(second);
            if (!result.equals(expected)) throw new Exception("expected " + test[2] + ", got " + result);
            System.out.println("PASSED");
        }
    }

    static String[][] minusTestsSimple = {
            //  { value, add, result }

            { "0", "0", "0"},
            { "0", "1", "-1"},
            { "1", "0", "1"},
            { "-1", "-1", "0"},
            { "1", "1", "0"},
            { "-1", "1", "-2"},
            { "1", "-1", "2"},
            { "1000000000", "999999999", "1"},
            { "-1000000000", "999999999", "-1999999999"},
            { "1000000000", "-999999999", "1999999999"},
            { "1000000000000000000", "999999999999999999", "1"},
            { "-1000000000000000000", "999999999999999999", "-1999999999999999999"},
            { "1000000000000000000", "-999999999999999999", "1999999999999999999"},
            { "1000000000000000000", "1000000000000000000", "0"},
            { "900000000000000000", "900000000000000000", "0"},
            { "2345897634587298769325", "-2345897634587298769325", "4691795269174597538650"},
            { "-2345897634587298769325", "2345897634587298769325", "-4691795269174597538650"},
            { "2384572698475238475264264", "34967520520948720982845982342934734", "-34967520518564148284370743867670470"}
    };

    static void minusTests() throws Exception {
        for (int i = 0; i < minusTestsSimple.length; i++) {
            System.out.print("minus test simple " + i + ": ");
            String[] test = minusTestsSimple[i];
            BigInt first = new BigInt(test[0]);
            BigInt second = new BigInt(test[1]);
            BigInt expected = new BigInt(test[2]);
            BigInt result = first.minus(second);
            if (!expected.equals(result)) throw new Exception("expected " + expected + ", got " + result);
            System.out.println("PASSED");
        }
    }

    static String[][] multiplyTestsSimple = {
            //  { value, add, result }

            { "0", "0", "0"},
            { "0", "1234564333465", "0"},
            { "1", "0", "0"},
            { "-1", "-1", "1"},
            { "1", "1", "1"},
            { "-1", "1", "-1"},
            { "1", "-1", "-1"},
            { "1", "111122223333444455556666777788889999", "111122223333444455556666777788889999"},
            { "3", "999988887777666655554444333322221111", "2999966663332999966663332999966663333"},
            { "1", "4387529684764836294576340762534975623487", "4387529684764836294576340762534975623487"},
            { "3", "4387529684764836294576340762534975623487", "13162589054294508883729022287604926870461"},
            { "9999", "9999", "99980001"},
            { "9999", "99999", "999890001"},
            { "99999", "99999", "9999800001"},
            { "1000000000", "999999999", "999999999000000000"},
            { "-1000000000", "999999999", "-999999999000000000"},
            { "1000000000000000000", "999999999999999999", "999999999999999999000000000000000000"},
            { "-999999999999999999", "999999999999999999", "-999999999999999998000000000000000001"},
            { "11111111111111111111111111111111111111111111111111111111", "1111111111111111111111111111111111111", "12345679012345679012345679012345679011111111111111111110987654320987654320987654320987654321"},
            { "10101010101010101010101010101010101010", "101010101010101010101010101010101010101", "1020304050607080910111213141516171819191817161514131211100908070605040302010"},
            { "9090909090909090", "90909090909090", "826446280991727190082644628100"},
            { "9090909090909090", "909090909090909", "8264462809917353719008264462810"},
            { "5555555555555555", "5555555555555555", "30864197530864191358024691358025"},
            { "999999999999999999", "999999999999999999", "999999999999999998000000000000000001"},
            { "9999999999999999999", "9999999999999999999", "99999999999999999980000000000000000001"},
            { "99999999999999999999999999", "99999999999999999999999999", "9999999999999999999999999800000000000000000000000001"},
            { "999999999999999999999999999", "999999999999999999999999999", "999999999999999999999999998000000000000000000000000001"},
            { "99999999999999999999999999999999", "99999999999999999999999999999999", "9999999999999999999999999999999800000000000000000000000000000001"},
            { "55555555555555555555555555555555", "55555555555555555555555555555555", "3086419753086419753086419753086358024691358024691358024691358025"},
            { "4387529684764836294576340762534975623487", "234558364309853742094587234863704968275094856723094587349229687", "1029131786219368217359117779022938857716221441984868183907004638862030608940295036497830628411994858569"},
            { "1029131786219368217359117779022938857716221441984868183907004638862030608940295036497830628411994858569", "1029131786219368217359117779022938857716221441984868183907004638862030608940295036497830628411994858569", "1059112233407067406692972581407777946104079485367582281840582337001040018820488549650736705266754488400358738452491369237476684619042795869299042110022853606958724471172151228987229486092666151290312727761"},
            { "4858569", "1029131786219368217359117779022938857716221441984868183907004638862030608940295036497830628411994858569", "5000107793440049620446271508509701022995444295162979027416871621231257193648440315182228458453037448002727761"},
            { "-4858569", "1029131786219368217359117779022938857716221441984868183907004638862030608940295036497830628411994858569", "-5000107793440049620446271508509701022995444295162979027416871621231257193648440315182228458453037448002727761"},
            { "-4858569", "-10000000000000000000000000000000000000000000000000000000000", "48585690000000000000000000000000000000000000000000000000000000000"}
    };

    static void multiplyTests() throws Exception {
        for (int i = 0; i < multiplyTestsSimple.length; i++) {
            System.out.print("multiply test simple " + i + ": ");
            String[] test = multiplyTestsSimple[i];
            BigInt first = new BigInt(test[0]);
            BigInt second = new BigInt(test[1]);
            BigInt expected = new BigInt(test[2]);
            BigInt result = first.multiply(second);
            if (!result.equals(expected)) throw new Exception("expected " + expected + ", got " + result);
            System.out.println("PASSED");
        }
    }

    static String[][] div2tests = {
            //  { value, result }
            { "5", "2"},
            { "4", "2"},
            { "100", "50"},
            { "103", "51"},
            { "999999999", "499999999"},
            { "1000000000", "500000000"},
    };

    static void divBy2Tests() throws Exception {
        for (int i = 0; i < div2tests.length; i++) {
            System.out.print("div2 test " + i + ": ");
            String[] test = div2tests[i];
            BigInt value = new BigInt(test[0]);
            BigInt expected = new BigInt(test[1]);
            BigInt result = value.divBy2(value);
            if (!result.equals(expected)) throw new Exception("expected " + expected + ", got " + result);
            System.out.println("PASSED");
        }
    }

    static String[][] divideTestsSimple = {
            //  { value, add, result }

            { "0", "0", FAIL},
            { "0", "1234564333465", "0"},
            { "1", "0", FAIL},
            { "-1", "-1", "1"},
            { "1", "1", "1"},
            { "-1", "1", "-1"},
            { "1", "-1", "-1"},
            { "9", "3", "3"},
            { "10", "3", "3"},
            { "11", "3", "3"},
            { "12", "3", "4"},
            { "3", "12", "0"},
            { "12", "12", "1"},
            { "300", "1", "300"},
            { "999999999", "10000000", "99"},
            { "999999999", "10000", "99999"},
            { "999999999", "1000", "999999"},
            { "999999999", "1", "999999999"},
            { "1000000000", "1", "1000000000"},
            { "1", "111122223333444455556666777788889999", "0"},
            { "111122223333444455556666777788889999", "111122223333444455556666777788889999", "1"},
            { "3", "999988887777666655554444333322221111", "0"},
            { "111122223333444455556666777788889999", "2", "55561111666722227778333388894444999"},
            { "4387529684764836294576340762534975623487", "1", "4387529684764836294576340762534975623487"},
            { "13162589054294508883729022287604926870461", "4387529684764836294576340762534975623487", "3"},
            { "13162589054294508883729022287604926870461", "3", "4387529684764836294576340762534975623487"},
            { "99980001", "9999", "9999"},
            { "999890001", "99999", "9999"},
            { "999890001", "9999", "99999"},
            { "9999800001", "99999", "99999"},
            { "999999999000000000", "1000000000", "999999999"},
            { "999999999000000000", "1000000000", "999999999"},
            { "-999999999000000000", "999999999", "-1000000000"},
            { "999999999999999999000000000000000000", "1000000000000000000", "999999999999999999"},
            { "-999999999999999998000000000000000001", "-999999999999999999", "999999999999999999"},
            { "12345679012345679012345679012345679011111111111111111110987654320987654320987654320987654321", "11111111111111111111111111111111111111111111111111111111", "1111111111111111111111111111111111111"},
            { "1020304050607080910111213141516171819191817161514131211100908070605040302010", "10101010101010101010101010101010101010", "101010101010101010101010101010101010101"},
            { "826446280991727190082644628100", "9090909090909090", "90909090909090"},
            { "8264462809917353719008264462810", "9090909090909090", "909090909090909"},
            { "5555555555555555", "5555555555555555", "1"},
            { "9999999999999999999999999999999800000000000000000000000000000001", "99999999999999999999999999999999", "99999999999999999999999999999999"},
            { "3086419753086419753086419753086358024691358024691358024691358025", "55555555555555555555555555555555", "55555555555555555555555555555555"},
            { "1059112233407067406692972581407777946104079485367582281840582337001040018820488549650736705266754488400358738452491369237476684619042795869299042110022853606958724471172151228987229486092666151290312727761", "1", "1059112233407067406692972581407777946104079485367582281840582337001040018820488549650736705266754488400358738452491369237476684619042795869299042110022853606958724471172151228987229486092666151290312727761"},
            { "1029131786219368217359117779022938857716221441984868183907004638862030608940295036497830628411994858569", "4387529684764836294576340762534975623487", "234558364309853742094587234863704968275094856723094587349229687"},
            { "1059112233407067406692972581407777946104079485367582281840582337001040018820488549650736705266754488400358738452491369237476684619042795869299042110022853606958724471172151228987229486092666151290312727761", "1029131786219368217359117779022938857716221441984868183907004638862030608940295036497830628411994858569", "1029131786219368217359117779022938857716221441984868183907004638862030608940295036497830628411994858569"},
            { "5000107793440049620446271508509701022995444295162979027416871621231257193648440315182228458453037448002727761", "4858569", "1029131786219368217359117779022938857716221441984868183907004638862030608940295036497830628411994858569"},
            {  "4858569", "5000107793440049620446271508509701022995444295162979027416871621231257193648440315182228458453037448002727761", "0"}
    };

    static void divideTests() throws Exception {
        for (int i = 0; i < 999; i++) {
            BigInt I = new BigInt(Integer.toString(i));
            for (int j = 1; j < 99; j++) {
                BigInt J = new BigInt(Integer.toString(j));
                BigInt result = I.divide(J);
                if (!result.toString().equals(Integer.toString(i / j))) {
                    throw new Exception("expected " + Integer.toString(i / j) + ", got " + result);
                }
            }
        }
        System.out.println("small integers division test - PASSED");

        for (int i = 0; i < divideTestsSimple.length; i++) {
            System.out.print("divide test simple " + i + ": ");
            String[] test = divideTestsSimple[i];
            BigInt first = new BigInt(test[0]);
            BigInt second = new BigInt(test[1]);
            if (test[2].equals(FAIL)) {
                boolean failed = false;
                try {
                    BigInt result = first.divide(second);
                }
                catch (ArithmeticException ae) {
                    failed = true;
                }
                if (!failed) {
                    throw new Exception("should fail, but did not.");
                }
                System.out.println("PASSED");
            }
            else {
                BigInt expected = new BigInt(test[2]);
                BigInt result = first.divide(second);
                if (!result.equals(expected)) throw new Exception("expected " + expected + ", got " + result);
                System.out.println("PASSED");
            }
        }
    }

    static String[][] sideEffectsTest = {
            {"1", "3", "-1", "1", "1", "1" },
            {"-1", "-3", "1", "-1", "-1", "-1" },
            {"100000000000000000", "300000000000000000", "-100000000000000000", "1000000000000000000000000000000000000000000000000000", "100000000000000000", "0" },
            {"999999999", "2999999997", "-999999999", "999999997000000002999999999", "999999999", "0" },
            {"555555555555555555555555555555555555555", "1666666666666666666666666666666666666665", "-555555555555555555555555555555555555555", "171467764060356652949245541838134430726508916323731138545953360768175582990398319615912208504801097393689986282578875", "555555555555555555555555555555555555555", "0" }
    };

    public static void expressionTests() throws Exception {
        for (int i = 0; i < sideEffectsTest.length; i++) {
            System.out.println("side effects test " + i + ":");
            String[] test = sideEffectsTest[i];
            BigInt val = new BigInt(test[0]);
            for (int j = 1; j <= 2; j++) {
                System.out.print(" Round " + j + " - ");
                BigInt addExpect = new BigInt(test[1]);
                BigInt addResult = val.plus(val).plus(val);
                if (!addExpect.equals(addResult)) {
                    throw new Exception("Addition: expected " + addExpect + ", but got " + addResult);
                }
                BigInt subtractExpect = new BigInt(test[2]);
                BigInt subtractResult = val.minus(val).minus(val);
                if (!subtractExpect.equals(subtractResult)) {
                    throw new Exception("Subtraction: expected " + subtractExpect + ", but got " + subtractResult);
                }
                BigInt multExpect = new BigInt(test[3]);
                BigInt multResult = val.multiply(val).multiply(val);
                if (!multExpect.equals(multResult)) {
                    throw new Exception("Multiplication: expected " + multExpect + ", but got " + multResult);
                }
                BigInt div1Expect = new BigInt(test[4]);
                BigInt div1Result = val.divide(val.divide(val));
                if (!div1Expect.equals(div1Result)) {
                    throw new Exception("Division 1: expected " + div1Expect + ", but got " + div1Result);
                }
                BigInt div2Expect = new BigInt(test[5]);
                BigInt div2Result = val.divide(val).divide(val);
                if (!div2Expect.equals(div2Result)) {
                    throw new Exception("Division 1: expected " + div2Expect + ", but got " + div2Result);
                }
                System.out.println("PASSED");
            }
        }
    }

    public static void start() throws Exception {
        testConstructor();
        compareTests();
        plusTests();
        minusTests();
        multiplyTests();
        divBy2Tests();
        divideTests();
        expressionTests();
    }
}