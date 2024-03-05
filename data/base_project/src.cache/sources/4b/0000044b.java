package android.debug;

/* loaded from: JNITest.class */
public class JNITest {
    private native int part1(int i, double d, String str, int[] iArr);

    private static native int part3(String str);

    public int test(int intArg, double doubleArg, String stringArg) {
        int[] intArray = {42, 53, 65, 127};
        return part1(intArg, doubleArg, stringArg, intArray);
    }

    private int part2(double doubleArg, int fromArray, String stringArg) {
        System.out.println(stringArg + " : " + ((float) doubleArg) + " : " + fromArray);
        int result = part3(stringArg);
        return result + 6;
    }
}