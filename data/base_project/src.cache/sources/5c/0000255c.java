package java.util;

import java.io.Serializable;
import java.lang.reflect.Array;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Arrays.class */
public class Arrays {
    Arrays() {
        throw new RuntimeException("Stub!");
    }

    public static <T> List<T> asList(T... array) {
        throw new RuntimeException("Stub!");
    }

    public static int binarySearch(byte[] array, byte value) {
        throw new RuntimeException("Stub!");
    }

    public static int binarySearch(byte[] array, int startIndex, int endIndex, byte value) {
        throw new RuntimeException("Stub!");
    }

    public static int binarySearch(char[] array, char value) {
        throw new RuntimeException("Stub!");
    }

    public static int binarySearch(char[] array, int startIndex, int endIndex, char value) {
        throw new RuntimeException("Stub!");
    }

    public static int binarySearch(double[] array, double value) {
        throw new RuntimeException("Stub!");
    }

    public static int binarySearch(double[] array, int startIndex, int endIndex, double value) {
        throw new RuntimeException("Stub!");
    }

    public static int binarySearch(float[] array, float value) {
        throw new RuntimeException("Stub!");
    }

    public static int binarySearch(float[] array, int startIndex, int endIndex, float value) {
        throw new RuntimeException("Stub!");
    }

    public static int binarySearch(int[] array, int value) {
        throw new RuntimeException("Stub!");
    }

    public static int binarySearch(int[] array, int startIndex, int endIndex, int value) {
        throw new RuntimeException("Stub!");
    }

    public static int binarySearch(long[] array, long value) {
        throw new RuntimeException("Stub!");
    }

    public static int binarySearch(long[] array, int startIndex, int endIndex, long value) {
        throw new RuntimeException("Stub!");
    }

    public static int binarySearch(Object[] array, Object value) {
        throw new RuntimeException("Stub!");
    }

    public static int binarySearch(Object[] array, int startIndex, int endIndex, Object value) {
        throw new RuntimeException("Stub!");
    }

    public static <T> int binarySearch(T[] array, T value, Comparator<? super T> comparator) {
        throw new RuntimeException("Stub!");
    }

    public static <T> int binarySearch(T[] array, int startIndex, int endIndex, T value, Comparator<? super T> comparator) {
        throw new RuntimeException("Stub!");
    }

    public static int binarySearch(short[] array, short value) {
        throw new RuntimeException("Stub!");
    }

    public static int binarySearch(short[] array, int startIndex, int endIndex, short value) {
        throw new RuntimeException("Stub!");
    }

    public static void fill(byte[] array, byte value) {
        throw new RuntimeException("Stub!");
    }

    public static void fill(byte[] array, int start, int end, byte value) {
        throw new RuntimeException("Stub!");
    }

    public static void fill(short[] array, short value) {
        throw new RuntimeException("Stub!");
    }

    public static void fill(short[] array, int start, int end, short value) {
        throw new RuntimeException("Stub!");
    }

    public static void fill(char[] array, char value) {
        throw new RuntimeException("Stub!");
    }

    public static void fill(char[] array, int start, int end, char value) {
        throw new RuntimeException("Stub!");
    }

    public static void fill(int[] array, int value) {
        throw new RuntimeException("Stub!");
    }

    public static void fill(int[] array, int start, int end, int value) {
        throw new RuntimeException("Stub!");
    }

    public static void fill(long[] array, long value) {
        throw new RuntimeException("Stub!");
    }

    public static void fill(long[] array, int start, int end, long value) {
        throw new RuntimeException("Stub!");
    }

    public static void fill(float[] array, float value) {
        throw new RuntimeException("Stub!");
    }

    /* loaded from: Arrays$ArrayList.class */
    private static class ArrayList<E> extends AbstractList<E> implements List<E>, Serializable, RandomAccess {
        private static final long serialVersionUID = -2764017481108945198L;
        private final E[] a;

        ArrayList(E[] storage) {
            if (storage == null) {
                throw new NullPointerException("storage == null");
            }
            this.a = storage;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean contains(Object object) {
            if (object != null) {
                E[] arr$ = this.a;
                for (E element : arr$) {
                    if (object.equals(element)) {
                        return true;
                    }
                }
                return false;
            }
            E[] arr$2 = this.a;
            for (E element2 : arr$2) {
                if (element2 == null) {
                    return true;
                }
            }
            return false;
        }

        @Override // java.util.AbstractList, java.util.List
        public E get(int location) {
            try {
                return this.a[location];
            } catch (ArrayIndexOutOfBoundsException e) {
                throw java.util.ArrayList.throwIndexOutOfBoundsException(location, this.a.length);
            }
        }

        @Override // java.util.AbstractList, java.util.List
        public int indexOf(Object object) {
            if (object != null) {
                for (int i = 0; i < this.a.length; i++) {
                    if (object.equals(this.a[i])) {
                        return i;
                    }
                }
                return -1;
            }
            for (int i2 = 0; i2 < this.a.length; i2++) {
                if (this.a[i2] == null) {
                    return i2;
                }
            }
            return -1;
        }

        @Override // java.util.AbstractList, java.util.List
        public int lastIndexOf(Object object) {
            if (object != null) {
                for (int i = this.a.length - 1; i >= 0; i--) {
                    if (object.equals(this.a[i])) {
                        return i;
                    }
                }
                return -1;
            }
            for (int i2 = this.a.length - 1; i2 >= 0; i2--) {
                if (this.a[i2] == null) {
                    return i2;
                }
            }
            return -1;
        }

        @Override // java.util.AbstractList, java.util.List
        public E set(int location, E object) {
            E result = this.a[location];
            this.a[location] = object;
            return result;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return this.a.length;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public Object[] toArray() {
            return (Object[]) this.a.clone();
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r0v14, types: [java.lang.Object[]] */
        @Override // java.util.AbstractCollection, java.util.Collection
        public <T> T[] toArray(T[] contents) {
            int size = size();
            if (size > contents.length) {
                Class<?> ct = contents.getClass().getComponentType();
                contents = (Object[]) Array.newInstance(ct, size);
            }
            System.arraycopy(this.a, 0, contents, 0, size);
            if (size < contents.length) {
                contents[size] = null;
            }
            return contents;
        }
    }

    public static void fill(float[] array, int start, int end, float value) {
        throw new RuntimeException("Stub!");
    }

    public static void fill(double[] array, double value) {
        throw new RuntimeException("Stub!");
    }

    public static void fill(double[] array, int start, int end, double value) {
        throw new RuntimeException("Stub!");
    }

    public static void fill(boolean[] array, boolean value) {
        throw new RuntimeException("Stub!");
    }

    public static void fill(boolean[] array, int start, int end, boolean value) {
        throw new RuntimeException("Stub!");
    }

    public static void fill(Object[] array, Object value) {
        throw new RuntimeException("Stub!");
    }

    public static void fill(Object[] array, int start, int end, Object value) {
        throw new RuntimeException("Stub!");
    }

    public static int hashCode(boolean[] array) {
        throw new RuntimeException("Stub!");
    }

    public static int hashCode(int[] array) {
        throw new RuntimeException("Stub!");
    }

    public static int hashCode(short[] array) {
        throw new RuntimeException("Stub!");
    }

    public static int hashCode(char[] array) {
        throw new RuntimeException("Stub!");
    }

    public static int hashCode(byte[] array) {
        throw new RuntimeException("Stub!");
    }

    public static int hashCode(long[] array) {
        throw new RuntimeException("Stub!");
    }

    public static int hashCode(float[] array) {
        throw new RuntimeException("Stub!");
    }

    public static int hashCode(double[] array) {
        throw new RuntimeException("Stub!");
    }

    public static int hashCode(Object[] array) {
        throw new RuntimeException("Stub!");
    }

    public static int deepHashCode(Object[] array) {
        throw new RuntimeException("Stub!");
    }

    public static boolean equals(byte[] array1, byte[] array2) {
        throw new RuntimeException("Stub!");
    }

    public static boolean equals(short[] array1, short[] array2) {
        throw new RuntimeException("Stub!");
    }

    public static boolean equals(char[] array1, char[] array2) {
        throw new RuntimeException("Stub!");
    }

    public static boolean equals(int[] array1, int[] array2) {
        throw new RuntimeException("Stub!");
    }

    public static boolean equals(long[] array1, long[] array2) {
        throw new RuntimeException("Stub!");
    }

    public static boolean equals(float[] array1, float[] array2) {
        throw new RuntimeException("Stub!");
    }

    public static boolean equals(double[] array1, double[] array2) {
        throw new RuntimeException("Stub!");
    }

    public static boolean equals(boolean[] array1, boolean[] array2) {
        throw new RuntimeException("Stub!");
    }

    public static boolean equals(Object[] array1, Object[] array2) {
        throw new RuntimeException("Stub!");
    }

    public static boolean deepEquals(Object[] array1, Object[] array2) {
        throw new RuntimeException("Stub!");
    }

    public static void sort(byte[] array) {
        throw new RuntimeException("Stub!");
    }

    public static void sort(byte[] array, int start, int end) {
        throw new RuntimeException("Stub!");
    }

    public static void sort(char[] array) {
        throw new RuntimeException("Stub!");
    }

    public static void sort(char[] array, int start, int end) {
        throw new RuntimeException("Stub!");
    }

    public static void sort(double[] array) {
        throw new RuntimeException("Stub!");
    }

    public static void sort(double[] array, int start, int end) {
        throw new RuntimeException("Stub!");
    }

    public static void sort(float[] array) {
        throw new RuntimeException("Stub!");
    }

    public static void sort(float[] array, int start, int end) {
        throw new RuntimeException("Stub!");
    }

    public static void sort(int[] array) {
        throw new RuntimeException("Stub!");
    }

    public static void sort(int[] array, int start, int end) {
        throw new RuntimeException("Stub!");
    }

    public static void sort(long[] array) {
        throw new RuntimeException("Stub!");
    }

    public static void sort(long[] array, int start, int end) {
        throw new RuntimeException("Stub!");
    }

    public static void sort(short[] array) {
        throw new RuntimeException("Stub!");
    }

    public static void sort(short[] array, int start, int end) {
        throw new RuntimeException("Stub!");
    }

    public static void sort(Object[] array) {
        throw new RuntimeException("Stub!");
    }

    public static void sort(Object[] array, int start, int end) {
        throw new RuntimeException("Stub!");
    }

    public static <T> void sort(T[] array, int start, int end, Comparator<? super T> comparator) {
        throw new RuntimeException("Stub!");
    }

    public static <T> void sort(T[] array, Comparator<? super T> comparator) {
        throw new RuntimeException("Stub!");
    }

    public static String toString(boolean[] array) {
        throw new RuntimeException("Stub!");
    }

    public static String toString(byte[] array) {
        throw new RuntimeException("Stub!");
    }

    public static String toString(char[] array) {
        throw new RuntimeException("Stub!");
    }

    public static String toString(double[] array) {
        throw new RuntimeException("Stub!");
    }

    public static String toString(float[] array) {
        throw new RuntimeException("Stub!");
    }

    public static String toString(int[] array) {
        throw new RuntimeException("Stub!");
    }

    public static String toString(long[] array) {
        throw new RuntimeException("Stub!");
    }

    public static String toString(short[] array) {
        throw new RuntimeException("Stub!");
    }

    public static String toString(Object[] array) {
        throw new RuntimeException("Stub!");
    }

    public static String deepToString(Object[] array) {
        throw new RuntimeException("Stub!");
    }

    public static boolean[] copyOf(boolean[] original, int newLength) {
        throw new RuntimeException("Stub!");
    }

    public static byte[] copyOf(byte[] original, int newLength) {
        throw new RuntimeException("Stub!");
    }

    public static char[] copyOf(char[] original, int newLength) {
        throw new RuntimeException("Stub!");
    }

    public static double[] copyOf(double[] original, int newLength) {
        throw new RuntimeException("Stub!");
    }

    public static float[] copyOf(float[] original, int newLength) {
        throw new RuntimeException("Stub!");
    }

    public static int[] copyOf(int[] original, int newLength) {
        throw new RuntimeException("Stub!");
    }

    public static long[] copyOf(long[] original, int newLength) {
        throw new RuntimeException("Stub!");
    }

    public static short[] copyOf(short[] original, int newLength) {
        throw new RuntimeException("Stub!");
    }

    public static <T> T[] copyOf(T[] original, int newLength) {
        throw new RuntimeException("Stub!");
    }

    public static <T, U> T[] copyOf(U[] original, int newLength, Class<? extends T[]> newType) {
        throw new RuntimeException("Stub!");
    }

    public static boolean[] copyOfRange(boolean[] original, int start, int end) {
        throw new RuntimeException("Stub!");
    }

    public static byte[] copyOfRange(byte[] original, int start, int end) {
        throw new RuntimeException("Stub!");
    }

    public static char[] copyOfRange(char[] original, int start, int end) {
        throw new RuntimeException("Stub!");
    }

    public static double[] copyOfRange(double[] original, int start, int end) {
        throw new RuntimeException("Stub!");
    }

    public static float[] copyOfRange(float[] original, int start, int end) {
        throw new RuntimeException("Stub!");
    }

    public static int[] copyOfRange(int[] original, int start, int end) {
        throw new RuntimeException("Stub!");
    }

    public static long[] copyOfRange(long[] original, int start, int end) {
        throw new RuntimeException("Stub!");
    }

    public static short[] copyOfRange(short[] original, int start, int end) {
        throw new RuntimeException("Stub!");
    }

    public static <T> T[] copyOfRange(T[] original, int start, int end) {
        throw new RuntimeException("Stub!");
    }

    public static <T, U> T[] copyOfRange(U[] original, int start, int end, Class<? extends T[]> newType) {
        throw new RuntimeException("Stub!");
    }
}