package java.util;

/* loaded from: DualPivotQuicksort.class */
final class DualPivotQuicksort {
    private static final int INSERTION_SORT_THRESHOLD = 32;
    private static final int COUNTING_SORT_THRESHOLD_FOR_BYTE = 128;
    private static final int COUNTING_SORT_THRESHOLD_FOR_SHORT_OR_CHAR = 32768;
    private static final int NUM_SHORT_VALUES = 65536;
    private static final int NUM_CHAR_VALUES = 65536;
    private static final int NUM_BYTE_VALUES = 256;

    private DualPivotQuicksort() {
    }

    public static void sort(int[] a) {
        doSort(a, 0, a.length - 1);
    }

    public static void sort(int[] a, int fromIndex, int toIndex) {
        Arrays.checkStartAndEnd(a.length, fromIndex, toIndex);
        doSort(a, fromIndex, toIndex - 1);
    }

    private static void doSort(int[] a, int left, int right) {
        if ((right - left) + 1 < 32) {
            for (int i = left + 1; i <= right; i++) {
                int ai = a[i];
                int j = i - 1;
                while (j >= left && ai < a[j]) {
                    a[j + 1] = a[j];
                    j--;
                }
                a[j + 1] = ai;
            }
            return;
        }
        dualPivotQuicksort(a, left, right);
    }

    private static void dualPivotQuicksort(int[] a, int left, int right) {
        int sixth = ((right - left) + 1) / 6;
        int e1 = left + sixth;
        int e5 = right - sixth;
        int e3 = (left + right) >>> 1;
        int e4 = e3 + sixth;
        int e2 = e3 - sixth;
        int ae1 = a[e1];
        int ae2 = a[e2];
        int ae3 = a[e3];
        int ae4 = a[e4];
        int ae5 = a[e5];
        if (ae1 > ae2) {
            ae1 = ae2;
            ae2 = ae1;
        }
        if (ae4 > ae5) {
            ae4 = ae5;
            ae5 = ae4;
        }
        if (ae1 > ae3) {
            int t = ae1;
            ae1 = ae3;
            ae3 = t;
        }
        if (ae2 > ae3) {
            int t2 = ae2;
            ae2 = ae3;
            ae3 = t2;
        }
        if (ae1 > ae4) {
            int t3 = ae1;
            ae1 = ae4;
            ae4 = t3;
        }
        if (ae3 > ae4) {
            int t4 = ae3;
            ae3 = ae4;
            ae4 = t4;
        }
        if (ae2 > ae5) {
            int t5 = ae2;
            ae2 = ae5;
            ae5 = t5;
        }
        if (ae2 > ae3) {
            int t6 = ae2;
            ae2 = ae3;
            ae3 = t6;
        }
        if (ae4 > ae5) {
            int t7 = ae4;
            ae4 = ae5;
            ae5 = t7;
        }
        a[e1] = ae1;
        a[e3] = ae3;
        a[e5] = ae5;
        int pivot1 = ae2;
        a[e2] = a[left];
        int pivot2 = ae4;
        a[e4] = a[right];
        int less = left + 1;
        int great = right - 1;
        boolean pivotsDiffer = pivot1 != pivot2;
        if (pivotsDiffer) {
            loop0: for (int k = less; k <= great; k++) {
                int ak = a[k];
                if (ak < pivot1) {
                    if (k != less) {
                        a[k] = a[less];
                        a[less] = ak;
                    }
                    less++;
                } else if (ak > pivot2) {
                    while (a[great] > pivot2) {
                        int i = great;
                        great--;
                        if (i == k) {
                            break loop0;
                        }
                    }
                    if (a[great] < pivot1) {
                        a[k] = a[less];
                        int i2 = less;
                        less++;
                        a[i2] = a[great];
                        int i3 = great;
                        great--;
                        a[i3] = ak;
                    } else {
                        a[k] = a[great];
                        int i4 = great;
                        great--;
                        a[i4] = ak;
                    }
                } else {
                    continue;
                }
            }
        } else {
            for (int k2 = less; k2 <= great; k2++) {
                int ak2 = a[k2];
                if (ak2 != pivot1) {
                    if (ak2 < pivot1) {
                        if (k2 != less) {
                            a[k2] = a[less];
                            a[less] = ak2;
                        }
                        less++;
                    } else {
                        while (a[great] > pivot1) {
                            great--;
                        }
                        if (a[great] < pivot1) {
                            a[k2] = a[less];
                            int i5 = less;
                            less++;
                            a[i5] = a[great];
                            int i6 = great;
                            great--;
                            a[i6] = ak2;
                        } else {
                            a[k2] = pivot1;
                            int i7 = great;
                            great--;
                            a[i7] = ak2;
                        }
                    }
                }
            }
        }
        a[left] = a[less - 1];
        a[less - 1] = pivot1;
        a[right] = a[great + 1];
        a[great + 1] = pivot2;
        doSort(a, left, less - 2);
        doSort(a, great + 2, right);
        if (!pivotsDiffer) {
            return;
        }
        if (less < e1 && great > e5) {
            while (a[less] == pivot1) {
                less++;
            }
            while (a[great] == pivot2) {
                great--;
            }
            loop4: for (int k3 = less; k3 <= great; k3++) {
                int ak3 = a[k3];
                if (ak3 == pivot2) {
                    while (a[great] == pivot2) {
                        int i8 = great;
                        great--;
                        if (i8 == k3) {
                            break loop4;
                        }
                    }
                    if (a[great] == pivot1) {
                        a[k3] = a[less];
                        int i9 = less;
                        less++;
                        a[i9] = pivot1;
                    } else {
                        a[k3] = a[great];
                    }
                    int i10 = great;
                    great--;
                    a[i10] = pivot2;
                } else if (ak3 == pivot1) {
                    a[k3] = a[less];
                    int i11 = less;
                    less++;
                    a[i11] = pivot1;
                }
            }
        }
        doSort(a, less, great);
    }

    public static void sort(long[] a) {
        doSort(a, 0, a.length - 1);
    }

    public static void sort(long[] a, int fromIndex, int toIndex) {
        Arrays.checkStartAndEnd(a.length, fromIndex, toIndex);
        doSort(a, fromIndex, toIndex - 1);
    }

    private static void doSort(long[] a, int left, int right) {
        if ((right - left) + 1 < 32) {
            for (int i = left + 1; i <= right; i++) {
                long ai = a[i];
                int j = i - 1;
                while (j >= left && ai < a[j]) {
                    a[j + 1] = a[j];
                    j--;
                }
                a[j + 1] = ai;
            }
            return;
        }
        dualPivotQuicksort(a, left, right);
    }

    private static void dualPivotQuicksort(long[] a, int left, int right) {
        int sixth = ((right - left) + 1) / 6;
        int e1 = left + sixth;
        int e5 = right - sixth;
        int e3 = (left + right) >>> 1;
        int e4 = e3 + sixth;
        int e2 = e3 - sixth;
        long ae1 = a[e1];
        long ae2 = a[e2];
        long ae3 = a[e3];
        long ae4 = a[e4];
        long ae5 = a[e5];
        if (ae1 > ae2) {
            ae1 = ae2;
            ae2 = ae1;
        }
        if (ae4 > ae5) {
            ae4 = ae5;
            ae5 = ae4;
        }
        if (ae1 > ae3) {
            long t = ae1;
            ae1 = ae3;
            ae3 = t;
        }
        if (ae2 > ae3) {
            long t2 = ae2;
            ae2 = ae3;
            ae3 = t2;
        }
        if (ae1 > ae4) {
            long t3 = ae1;
            ae1 = ae4;
            ae4 = t3;
        }
        if (ae3 > ae4) {
            long t4 = ae3;
            ae3 = ae4;
            ae4 = t4;
        }
        if (ae2 > ae5) {
            long t5 = ae2;
            ae2 = ae5;
            ae5 = t5;
        }
        if (ae2 > ae3) {
            long t6 = ae2;
            ae2 = ae3;
            ae3 = t6;
        }
        if (ae4 > ae5) {
            long t7 = ae4;
            ae4 = ae5;
            ae5 = t7;
        }
        a[e1] = ae1;
        a[e3] = ae3;
        a[e5] = ae5;
        long pivot1 = ae2;
        a[e2] = a[left];
        long pivot2 = ae4;
        a[e4] = a[right];
        int less = left + 1;
        int great = right - 1;
        boolean pivotsDiffer = pivot1 != pivot2;
        if (pivotsDiffer) {
            loop0: for (int k = less; k <= great; k++) {
                long ak = a[k];
                if (ak < pivot1) {
                    if (k != less) {
                        a[k] = a[less];
                        a[less] = ak;
                    }
                    less++;
                } else if (ak > pivot2) {
                    while (a[great] > pivot2) {
                        int i = great;
                        great--;
                        if (i == k) {
                            break loop0;
                        }
                    }
                    if (a[great] < pivot1) {
                        a[k] = a[less];
                        int i2 = less;
                        less++;
                        a[i2] = a[great];
                        int i3 = great;
                        great--;
                        a[i3] = ak;
                    } else {
                        a[k] = a[great];
                        int i4 = great;
                        great--;
                        a[i4] = ak;
                    }
                } else {
                    continue;
                }
            }
        } else {
            for (int k2 = less; k2 <= great; k2++) {
                long ak2 = a[k2];
                if (ak2 != pivot1) {
                    if (ak2 < pivot1) {
                        if (k2 != less) {
                            a[k2] = a[less];
                            a[less] = ak2;
                        }
                        less++;
                    } else {
                        while (a[great] > pivot1) {
                            great--;
                        }
                        if (a[great] < pivot1) {
                            a[k2] = a[less];
                            int i5 = less;
                            less++;
                            a[i5] = a[great];
                            int i6 = great;
                            great--;
                            a[i6] = ak2;
                        } else {
                            a[k2] = pivot1;
                            int i7 = great;
                            great--;
                            a[i7] = ak2;
                        }
                    }
                }
            }
        }
        a[left] = a[less - 1];
        a[less - 1] = pivot1;
        a[right] = a[great + 1];
        a[great + 1] = pivot2;
        doSort(a, left, less - 2);
        doSort(a, great + 2, right);
        if (!pivotsDiffer) {
            return;
        }
        if (less < e1 && great > e5) {
            while (a[less] == pivot1) {
                less++;
            }
            while (a[great] == pivot2) {
                great--;
            }
            loop4: for (int k3 = less; k3 <= great; k3++) {
                long ak3 = a[k3];
                if (ak3 == pivot2) {
                    while (a[great] == pivot2) {
                        int i8 = great;
                        great--;
                        if (i8 == k3) {
                            break loop4;
                        }
                    }
                    if (a[great] == pivot1) {
                        a[k3] = a[less];
                        int i9 = less;
                        less++;
                        a[i9] = pivot1;
                    } else {
                        a[k3] = a[great];
                    }
                    int i10 = great;
                    great--;
                    a[i10] = pivot2;
                } else if (ak3 == pivot1) {
                    a[k3] = a[less];
                    int i11 = less;
                    less++;
                    a[i11] = pivot1;
                }
            }
        }
        doSort(a, less, great);
    }

    public static void sort(short[] a) {
        doSort(a, 0, a.length - 1);
    }

    public static void sort(short[] a, int fromIndex, int toIndex) {
        Arrays.checkStartAndEnd(a.length, fromIndex, toIndex);
        doSort(a, fromIndex, toIndex - 1);
    }

    private static void doSort(short[] a, int left, int right) {
        if ((right - left) + 1 < 32) {
            for (int i = left + 1; i <= right; i++) {
                short ai = a[i];
                int j = i - 1;
                while (j >= left && ai < a[j]) {
                    a[j + 1] = a[j];
                    j--;
                }
                a[j + 1] = ai;
            }
        } else if ((right - left) + 1 > 32768) {
            int[] count = new int[65536];
            for (int i2 = left; i2 <= right; i2++) {
                int i3 = a[i2] - Short.MIN_VALUE;
                count[i3] = count[i3] + 1;
            }
            int k = left;
            for (int i4 = 0; i4 < count.length && k <= right; i4++) {
                short value = (short) (i4 + Short.MIN_VALUE);
                for (int s = count[i4]; s > 0; s--) {
                    int i5 = k;
                    k++;
                    a[i5] = value;
                }
            }
        } else {
            dualPivotQuicksort(a, left, right);
        }
    }

    private static void dualPivotQuicksort(short[] a, int left, int right) {
        int sixth = ((right - left) + 1) / 6;
        int e1 = left + sixth;
        int e5 = right - sixth;
        int e3 = (left + right) >>> 1;
        int e4 = e3 + sixth;
        int e2 = e3 - sixth;
        short ae1 = a[e1];
        short ae2 = a[e2];
        short ae3 = a[e3];
        short ae4 = a[e4];
        short ae5 = a[e5];
        if (ae1 > ae2) {
            ae1 = ae2;
            ae2 = ae1;
        }
        if (ae4 > ae5) {
            ae4 = ae5;
            ae5 = ae4;
        }
        if (ae1 > ae3) {
            short t = ae1;
            ae1 = ae3;
            ae3 = t;
        }
        if (ae2 > ae3) {
            short t2 = ae2;
            ae2 = ae3;
            ae3 = t2;
        }
        if (ae1 > ae4) {
            short t3 = ae1;
            ae1 = ae4;
            ae4 = t3;
        }
        if (ae3 > ae4) {
            short t4 = ae3;
            ae3 = ae4;
            ae4 = t4;
        }
        if (ae2 > ae5) {
            short t5 = ae2;
            ae2 = ae5;
            ae5 = t5;
        }
        if (ae2 > ae3) {
            short t6 = ae2;
            ae2 = ae3;
            ae3 = t6;
        }
        if (ae4 > ae5) {
            short t7 = ae4;
            ae4 = ae5;
            ae5 = t7;
        }
        a[e1] = ae1;
        a[e3] = ae3;
        a[e5] = ae5;
        short pivot1 = ae2;
        a[e2] = a[left];
        short pivot2 = ae4;
        a[e4] = a[right];
        int less = left + 1;
        int great = right - 1;
        boolean pivotsDiffer = pivot1 != pivot2;
        if (pivotsDiffer) {
            loop0: for (int k = less; k <= great; k++) {
                short ak = a[k];
                if (ak < pivot1) {
                    if (k != less) {
                        a[k] = a[less];
                        a[less] = ak;
                    }
                    less++;
                } else if (ak > pivot2) {
                    while (a[great] > pivot2) {
                        int i = great;
                        great--;
                        if (i == k) {
                            break loop0;
                        }
                    }
                    if (a[great] < pivot1) {
                        a[k] = a[less];
                        int i2 = less;
                        less++;
                        a[i2] = a[great];
                        int i3 = great;
                        great--;
                        a[i3] = ak;
                    } else {
                        a[k] = a[great];
                        int i4 = great;
                        great--;
                        a[i4] = ak;
                    }
                } else {
                    continue;
                }
            }
        } else {
            for (int k2 = less; k2 <= great; k2++) {
                short ak2 = a[k2];
                if (ak2 != pivot1) {
                    if (ak2 < pivot1) {
                        if (k2 != less) {
                            a[k2] = a[less];
                            a[less] = ak2;
                        }
                        less++;
                    } else {
                        while (a[great] > pivot1) {
                            great--;
                        }
                        if (a[great] < pivot1) {
                            a[k2] = a[less];
                            int i5 = less;
                            less++;
                            a[i5] = a[great];
                            int i6 = great;
                            great--;
                            a[i6] = ak2;
                        } else {
                            a[k2] = pivot1;
                            int i7 = great;
                            great--;
                            a[i7] = ak2;
                        }
                    }
                }
            }
        }
        a[left] = a[less - 1];
        a[less - 1] = pivot1;
        a[right] = a[great + 1];
        a[great + 1] = pivot2;
        doSort(a, left, less - 2);
        doSort(a, great + 2, right);
        if (!pivotsDiffer) {
            return;
        }
        if (less < e1 && great > e5) {
            while (a[less] == pivot1) {
                less++;
            }
            while (a[great] == pivot2) {
                great--;
            }
            loop4: for (int k3 = less; k3 <= great; k3++) {
                short ak3 = a[k3];
                if (ak3 == pivot2) {
                    while (a[great] == pivot2) {
                        int i8 = great;
                        great--;
                        if (i8 == k3) {
                            break loop4;
                        }
                    }
                    if (a[great] == pivot1) {
                        a[k3] = a[less];
                        int i9 = less;
                        less++;
                        a[i9] = pivot1;
                    } else {
                        a[k3] = a[great];
                    }
                    int i10 = great;
                    great--;
                    a[i10] = pivot2;
                } else if (ak3 == pivot1) {
                    a[k3] = a[less];
                    int i11 = less;
                    less++;
                    a[i11] = pivot1;
                }
            }
        }
        doSort(a, less, great);
    }

    public static void sort(char[] a) {
        doSort(a, 0, a.length - 1);
    }

    public static void sort(char[] a, int fromIndex, int toIndex) {
        Arrays.checkStartAndEnd(a.length, fromIndex, toIndex);
        doSort(a, fromIndex, toIndex - 1);
    }

    private static void doSort(char[] a, int left, int right) {
        if ((right - left) + 1 < 32) {
            for (int i = left + 1; i <= right; i++) {
                char ai = a[i];
                int j = i - 1;
                while (j >= left && ai < a[j]) {
                    a[j + 1] = a[j];
                    j--;
                }
                a[j + 1] = ai;
            }
        } else if ((right - left) + 1 > 32768) {
            int[] count = new int[65536];
            for (int i2 = left; i2 <= right; i2++) {
                char c = a[i2];
                count[c] = count[c] + 1;
            }
            int k = left;
            for (int i3 = 0; i3 < count.length && k <= right; i3++) {
                for (int s = count[i3]; s > 0; s--) {
                    int i4 = k;
                    k++;
                    a[i4] = (char) i3;
                }
            }
        } else {
            dualPivotQuicksort(a, left, right);
        }
    }

    private static void dualPivotQuicksort(char[] a, int left, int right) {
        int sixth = ((right - left) + 1) / 6;
        int e1 = left + sixth;
        int e5 = right - sixth;
        int e3 = (left + right) >>> 1;
        int e4 = e3 + sixth;
        int e2 = e3 - sixth;
        char ae1 = a[e1];
        char ae2 = a[e2];
        char ae3 = a[e3];
        char ae4 = a[e4];
        char ae5 = a[e5];
        if (ae1 > ae2) {
            ae1 = ae2;
            ae2 = ae1;
        }
        if (ae4 > ae5) {
            ae4 = ae5;
            ae5 = ae4;
        }
        if (ae1 > ae3) {
            char t = ae1;
            ae1 = ae3;
            ae3 = t;
        }
        if (ae2 > ae3) {
            char t2 = ae2;
            ae2 = ae3;
            ae3 = t2;
        }
        if (ae1 > ae4) {
            char t3 = ae1;
            ae1 = ae4;
            ae4 = t3;
        }
        if (ae3 > ae4) {
            char t4 = ae3;
            ae3 = ae4;
            ae4 = t4;
        }
        if (ae2 > ae5) {
            char t5 = ae2;
            ae2 = ae5;
            ae5 = t5;
        }
        if (ae2 > ae3) {
            char t6 = ae2;
            ae2 = ae3;
            ae3 = t6;
        }
        if (ae4 > ae5) {
            char t7 = ae4;
            ae4 = ae5;
            ae5 = t7;
        }
        a[e1] = ae1;
        a[e3] = ae3;
        a[e5] = ae5;
        char pivot1 = ae2;
        a[e2] = a[left];
        char pivot2 = ae4;
        a[e4] = a[right];
        int less = left + 1;
        int great = right - 1;
        boolean pivotsDiffer = pivot1 != pivot2;
        if (pivotsDiffer) {
            loop0: for (int k = less; k <= great; k++) {
                char ak = a[k];
                if (ak < pivot1) {
                    if (k != less) {
                        a[k] = a[less];
                        a[less] = ak;
                    }
                    less++;
                } else if (ak > pivot2) {
                    while (a[great] > pivot2) {
                        int i = great;
                        great--;
                        if (i == k) {
                            break loop0;
                        }
                    }
                    if (a[great] < pivot1) {
                        a[k] = a[less];
                        int i2 = less;
                        less++;
                        a[i2] = a[great];
                        int i3 = great;
                        great--;
                        a[i3] = ak;
                    } else {
                        a[k] = a[great];
                        int i4 = great;
                        great--;
                        a[i4] = ak;
                    }
                } else {
                    continue;
                }
            }
        } else {
            for (int k2 = less; k2 <= great; k2++) {
                char ak2 = a[k2];
                if (ak2 != pivot1) {
                    if (ak2 < pivot1) {
                        if (k2 != less) {
                            a[k2] = a[less];
                            a[less] = ak2;
                        }
                        less++;
                    } else {
                        while (a[great] > pivot1) {
                            great--;
                        }
                        if (a[great] < pivot1) {
                            a[k2] = a[less];
                            int i5 = less;
                            less++;
                            a[i5] = a[great];
                            int i6 = great;
                            great--;
                            a[i6] = ak2;
                        } else {
                            a[k2] = pivot1;
                            int i7 = great;
                            great--;
                            a[i7] = ak2;
                        }
                    }
                }
            }
        }
        a[left] = a[less - 1];
        a[less - 1] = pivot1;
        a[right] = a[great + 1];
        a[great + 1] = pivot2;
        doSort(a, left, less - 2);
        doSort(a, great + 2, right);
        if (!pivotsDiffer) {
            return;
        }
        if (less < e1 && great > e5) {
            while (a[less] == pivot1) {
                less++;
            }
            while (a[great] == pivot2) {
                great--;
            }
            loop4: for (int k3 = less; k3 <= great; k3++) {
                char ak3 = a[k3];
                if (ak3 == pivot2) {
                    while (a[great] == pivot2) {
                        int i8 = great;
                        great--;
                        if (i8 == k3) {
                            break loop4;
                        }
                    }
                    if (a[great] == pivot1) {
                        a[k3] = a[less];
                        int i9 = less;
                        less++;
                        a[i9] = pivot1;
                    } else {
                        a[k3] = a[great];
                    }
                    int i10 = great;
                    great--;
                    a[i10] = pivot2;
                } else if (ak3 == pivot1) {
                    a[k3] = a[less];
                    int i11 = less;
                    less++;
                    a[i11] = pivot1;
                }
            }
        }
        doSort(a, less, great);
    }

    public static void sort(byte[] a) {
        doSort(a, 0, a.length - 1);
    }

    public static void sort(byte[] a, int fromIndex, int toIndex) {
        Arrays.checkStartAndEnd(a.length, fromIndex, toIndex);
        doSort(a, fromIndex, toIndex - 1);
    }

    private static void doSort(byte[] a, int left, int right) {
        if ((right - left) + 1 < 32) {
            for (int i = left + 1; i <= right; i++) {
                byte ai = a[i];
                int j = i - 1;
                while (j >= left && ai < a[j]) {
                    a[j + 1] = a[j];
                    j--;
                }
                a[j + 1] = ai;
            }
        } else if ((right - left) + 1 > 128) {
            int[] count = new int[256];
            for (int i2 = left; i2 <= right; i2++) {
                int i3 = a[i2] - Byte.MIN_VALUE;
                count[i3] = count[i3] + 1;
            }
            int k = left;
            for (int i4 = 0; i4 < count.length && k <= right; i4++) {
                byte value = (byte) (i4 + Byte.MIN_VALUE);
                for (int s = count[i4]; s > 0; s--) {
                    int i5 = k;
                    k++;
                    a[i5] = value;
                }
            }
        } else {
            dualPivotQuicksort(a, left, right);
        }
    }

    private static void dualPivotQuicksort(byte[] a, int left, int right) {
        int sixth = ((right - left) + 1) / 6;
        int e1 = left + sixth;
        int e5 = right - sixth;
        int e3 = (left + right) >>> 1;
        int e4 = e3 + sixth;
        int e2 = e3 - sixth;
        byte ae1 = a[e1];
        byte ae2 = a[e2];
        byte ae3 = a[e3];
        byte ae4 = a[e4];
        byte ae5 = a[e5];
        if (ae1 > ae2) {
            ae1 = ae2;
            ae2 = ae1;
        }
        if (ae4 > ae5) {
            ae4 = ae5;
            ae5 = ae4;
        }
        if (ae1 > ae3) {
            byte t = ae1;
            ae1 = ae3;
            ae3 = t;
        }
        if (ae2 > ae3) {
            byte t2 = ae2;
            ae2 = ae3;
            ae3 = t2;
        }
        if (ae1 > ae4) {
            byte t3 = ae1;
            ae1 = ae4;
            ae4 = t3;
        }
        if (ae3 > ae4) {
            byte t4 = ae3;
            ae3 = ae4;
            ae4 = t4;
        }
        if (ae2 > ae5) {
            byte t5 = ae2;
            ae2 = ae5;
            ae5 = t5;
        }
        if (ae2 > ae3) {
            byte t6 = ae2;
            ae2 = ae3;
            ae3 = t6;
        }
        if (ae4 > ae5) {
            byte t7 = ae4;
            ae4 = ae5;
            ae5 = t7;
        }
        a[e1] = ae1;
        a[e3] = ae3;
        a[e5] = ae5;
        byte pivot1 = ae2;
        a[e2] = a[left];
        byte pivot2 = ae4;
        a[e4] = a[right];
        int less = left + 1;
        int great = right - 1;
        boolean pivotsDiffer = pivot1 != pivot2;
        if (pivotsDiffer) {
            loop0: for (int k = less; k <= great; k++) {
                byte ak = a[k];
                if (ak < pivot1) {
                    if (k != less) {
                        a[k] = a[less];
                        a[less] = ak;
                    }
                    less++;
                } else if (ak > pivot2) {
                    while (a[great] > pivot2) {
                        int i = great;
                        great--;
                        if (i == k) {
                            break loop0;
                        }
                    }
                    if (a[great] < pivot1) {
                        a[k] = a[less];
                        int i2 = less;
                        less++;
                        a[i2] = a[great];
                        int i3 = great;
                        great--;
                        a[i3] = ak;
                    } else {
                        a[k] = a[great];
                        int i4 = great;
                        great--;
                        a[i4] = ak;
                    }
                } else {
                    continue;
                }
            }
        } else {
            for (int k2 = less; k2 <= great; k2++) {
                byte ak2 = a[k2];
                if (ak2 != pivot1) {
                    if (ak2 < pivot1) {
                        if (k2 != less) {
                            a[k2] = a[less];
                            a[less] = ak2;
                        }
                        less++;
                    } else {
                        while (a[great] > pivot1) {
                            great--;
                        }
                        if (a[great] < pivot1) {
                            a[k2] = a[less];
                            int i5 = less;
                            less++;
                            a[i5] = a[great];
                            int i6 = great;
                            great--;
                            a[i6] = ak2;
                        } else {
                            a[k2] = pivot1;
                            int i7 = great;
                            great--;
                            a[i7] = ak2;
                        }
                    }
                }
            }
        }
        a[left] = a[less - 1];
        a[less - 1] = pivot1;
        a[right] = a[great + 1];
        a[great + 1] = pivot2;
        doSort(a, left, less - 2);
        doSort(a, great + 2, right);
        if (!pivotsDiffer) {
            return;
        }
        if (less < e1 && great > e5) {
            while (a[less] == pivot1) {
                less++;
            }
            while (a[great] == pivot2) {
                great--;
            }
            loop4: for (int k3 = less; k3 <= great; k3++) {
                byte ak3 = a[k3];
                if (ak3 == pivot2) {
                    while (a[great] == pivot2) {
                        int i8 = great;
                        great--;
                        if (i8 == k3) {
                            break loop4;
                        }
                    }
                    if (a[great] == pivot1) {
                        a[k3] = a[less];
                        int i9 = less;
                        less++;
                        a[i9] = pivot1;
                    } else {
                        a[k3] = a[great];
                    }
                    int i10 = great;
                    great--;
                    a[i10] = pivot2;
                } else if (ak3 == pivot1) {
                    a[k3] = a[less];
                    int i11 = less;
                    less++;
                    a[i11] = pivot1;
                }
            }
        }
        doSort(a, less, great);
    }

    public static void sort(float[] a) {
        sortNegZeroAndNaN(a, 0, a.length - 1);
    }

    public static void sort(float[] a, int fromIndex, int toIndex) {
        Arrays.checkStartAndEnd(a.length, fromIndex, toIndex);
        sortNegZeroAndNaN(a, fromIndex, toIndex - 1);
    }

    private static void sortNegZeroAndNaN(float[] a, int left, int right) {
        int NEGATIVE_ZERO = Float.floatToIntBits(-0.0f);
        int numNegativeZeros = 0;
        int n = right;
        int k = left;
        while (k <= n) {
            float ak = a[k];
            if (ak == 0.0f && NEGATIVE_ZERO == Float.floatToRawIntBits(ak)) {
                a[k] = 0.0f;
                numNegativeZeros++;
            } else if (ak != ak) {
                int i = k;
                k--;
                a[i] = a[n];
                int i2 = n;
                n--;
                a[i2] = Float.NaN;
            }
            k++;
        }
        doSort(a, left, n);
        if (numNegativeZeros == 0) {
            return;
        }
        int zeroIndex = findAnyZero(a, left, n);
        for (int i3 = zeroIndex - 1; i3 >= left && a[i3] == 0.0f; i3--) {
            zeroIndex = i3;
        }
        int m = zeroIndex + numNegativeZeros;
        for (int i4 = zeroIndex; i4 < m; i4++) {
            a[i4] = -0.0f;
        }
    }

    private static int findAnyZero(float[] a, int low, int high) {
        while (true) {
            int middle = (low + high) >>> 1;
            float middleValue = a[middle];
            if (middleValue < 0.0f) {
                low = middle + 1;
            } else if (middleValue > 0.0f) {
                high = middle - 1;
            } else {
                return middle;
            }
        }
    }

    private static void doSort(float[] a, int left, int right) {
        if ((right - left) + 1 < 32) {
            for (int i = left + 1; i <= right; i++) {
                float ai = a[i];
                int j = i - 1;
                while (j >= left && ai < a[j]) {
                    a[j + 1] = a[j];
                    j--;
                }
                a[j + 1] = ai;
            }
            return;
        }
        dualPivotQuicksort(a, left, right);
    }

    private static void dualPivotQuicksort(float[] a, int left, int right) {
        int sixth = ((right - left) + 1) / 6;
        int e1 = left + sixth;
        int e5 = right - sixth;
        int e3 = (left + right) >>> 1;
        int e4 = e3 + sixth;
        int e2 = e3 - sixth;
        float ae1 = a[e1];
        float ae2 = a[e2];
        float ae3 = a[e3];
        float ae4 = a[e4];
        float ae5 = a[e5];
        if (ae1 > ae2) {
            ae1 = ae2;
            ae2 = ae1;
        }
        if (ae4 > ae5) {
            ae4 = ae5;
            ae5 = ae4;
        }
        if (ae1 > ae3) {
            float t = ae1;
            ae1 = ae3;
            ae3 = t;
        }
        if (ae2 > ae3) {
            float t2 = ae2;
            ae2 = ae3;
            ae3 = t2;
        }
        if (ae1 > ae4) {
            float t3 = ae1;
            ae1 = ae4;
            ae4 = t3;
        }
        if (ae3 > ae4) {
            float t4 = ae3;
            ae3 = ae4;
            ae4 = t4;
        }
        if (ae2 > ae5) {
            float t5 = ae2;
            ae2 = ae5;
            ae5 = t5;
        }
        if (ae2 > ae3) {
            float t6 = ae2;
            ae2 = ae3;
            ae3 = t6;
        }
        if (ae4 > ae5) {
            float t7 = ae4;
            ae4 = ae5;
            ae5 = t7;
        }
        a[e1] = ae1;
        a[e3] = ae3;
        a[e5] = ae5;
        float pivot1 = ae2;
        a[e2] = a[left];
        float pivot2 = ae4;
        a[e4] = a[right];
        int less = left + 1;
        int great = right - 1;
        boolean pivotsDiffer = pivot1 != pivot2;
        if (pivotsDiffer) {
            loop0: for (int k = less; k <= great; k++) {
                float ak = a[k];
                if (ak < pivot1) {
                    if (k != less) {
                        a[k] = a[less];
                        a[less] = ak;
                    }
                    less++;
                } else if (ak > pivot2) {
                    while (a[great] > pivot2) {
                        int i = great;
                        great--;
                        if (i == k) {
                            break loop0;
                        }
                    }
                    if (a[great] < pivot1) {
                        a[k] = a[less];
                        int i2 = less;
                        less++;
                        a[i2] = a[great];
                        int i3 = great;
                        great--;
                        a[i3] = ak;
                    } else {
                        a[k] = a[great];
                        int i4 = great;
                        great--;
                        a[i4] = ak;
                    }
                } else {
                    continue;
                }
            }
        } else {
            for (int k2 = less; k2 <= great; k2++) {
                float ak2 = a[k2];
                if (ak2 != pivot1) {
                    if (ak2 < pivot1) {
                        if (k2 != less) {
                            a[k2] = a[less];
                            a[less] = ak2;
                        }
                        less++;
                    } else {
                        while (a[great] > pivot1) {
                            great--;
                        }
                        if (a[great] < pivot1) {
                            a[k2] = a[less];
                            int i5 = less;
                            less++;
                            a[i5] = a[great];
                            int i6 = great;
                            great--;
                            a[i6] = ak2;
                        } else {
                            a[k2] = pivot1;
                            int i7 = great;
                            great--;
                            a[i7] = ak2;
                        }
                    }
                }
            }
        }
        a[left] = a[less - 1];
        a[less - 1] = pivot1;
        a[right] = a[great + 1];
        a[great + 1] = pivot2;
        doSort(a, left, less - 2);
        doSort(a, great + 2, right);
        if (!pivotsDiffer) {
            return;
        }
        if (less < e1 && great > e5) {
            while (a[less] == pivot1) {
                less++;
            }
            while (a[great] == pivot2) {
                great--;
            }
            loop4: for (int k3 = less; k3 <= great; k3++) {
                float ak3 = a[k3];
                if (ak3 == pivot2) {
                    while (a[great] == pivot2) {
                        int i8 = great;
                        great--;
                        if (i8 == k3) {
                            break loop4;
                        }
                    }
                    if (a[great] == pivot1) {
                        a[k3] = a[less];
                        int i9 = less;
                        less++;
                        a[i9] = pivot1;
                    } else {
                        a[k3] = a[great];
                    }
                    int i10 = great;
                    great--;
                    a[i10] = pivot2;
                } else if (ak3 == pivot1) {
                    a[k3] = a[less];
                    int i11 = less;
                    less++;
                    a[i11] = pivot1;
                }
            }
        }
        doSort(a, less, great);
    }

    public static void sort(double[] a) {
        sortNegZeroAndNaN(a, 0, a.length - 1);
    }

    public static void sort(double[] a, int fromIndex, int toIndex) {
        Arrays.checkStartAndEnd(a.length, fromIndex, toIndex);
        sortNegZeroAndNaN(a, fromIndex, toIndex - 1);
    }

    private static void sortNegZeroAndNaN(double[] a, int left, int right) {
        long NEGATIVE_ZERO = Double.doubleToLongBits(-0.0d);
        int numNegativeZeros = 0;
        int n = right;
        int k = left;
        while (k <= n) {
            double ak = a[k];
            if (ak == 0.0d && NEGATIVE_ZERO == Double.doubleToRawLongBits(ak)) {
                a[k] = 0.0d;
                numNegativeZeros++;
            } else if (ak != ak) {
                int i = k;
                k--;
                a[i] = a[n];
                int i2 = n;
                n--;
                a[i2] = Double.NaN;
            }
            k++;
        }
        doSort(a, left, n);
        if (numNegativeZeros == 0) {
            return;
        }
        int zeroIndex = findAnyZero(a, left, n);
        for (int i3 = zeroIndex - 1; i3 >= left && a[i3] == 0.0d; i3--) {
            zeroIndex = i3;
        }
        int m = zeroIndex + numNegativeZeros;
        for (int i4 = zeroIndex; i4 < m; i4++) {
            a[i4] = -0.0d;
        }
    }

    private static int findAnyZero(double[] a, int low, int high) {
        while (true) {
            int middle = (low + high) >>> 1;
            double middleValue = a[middle];
            if (middleValue < 0.0d) {
                low = middle + 1;
            } else if (middleValue > 0.0d) {
                high = middle - 1;
            } else {
                return middle;
            }
        }
    }

    private static void doSort(double[] a, int left, int right) {
        if ((right - left) + 1 < 32) {
            for (int i = left + 1; i <= right; i++) {
                double ai = a[i];
                int j = i - 1;
                while (j >= left && ai < a[j]) {
                    a[j + 1] = a[j];
                    j--;
                }
                a[j + 1] = ai;
            }
            return;
        }
        dualPivotQuicksort(a, left, right);
    }

    private static void dualPivotQuicksort(double[] a, int left, int right) {
        int sixth = ((right - left) + 1) / 6;
        int e1 = left + sixth;
        int e5 = right - sixth;
        int e3 = (left + right) >>> 1;
        int e4 = e3 + sixth;
        int e2 = e3 - sixth;
        double ae1 = a[e1];
        double ae2 = a[e2];
        double ae3 = a[e3];
        double ae4 = a[e4];
        double ae5 = a[e5];
        if (ae1 > ae2) {
            ae1 = ae2;
            ae2 = ae1;
        }
        if (ae4 > ae5) {
            ae4 = ae5;
            ae5 = ae4;
        }
        if (ae1 > ae3) {
            double t = ae1;
            ae1 = ae3;
            ae3 = t;
        }
        if (ae2 > ae3) {
            double t2 = ae2;
            ae2 = ae3;
            ae3 = t2;
        }
        if (ae1 > ae4) {
            double t3 = ae1;
            ae1 = ae4;
            ae4 = t3;
        }
        if (ae3 > ae4) {
            double t4 = ae3;
            ae3 = ae4;
            ae4 = t4;
        }
        if (ae2 > ae5) {
            double t5 = ae2;
            ae2 = ae5;
            ae5 = t5;
        }
        if (ae2 > ae3) {
            double t6 = ae2;
            ae2 = ae3;
            ae3 = t6;
        }
        if (ae4 > ae5) {
            double t7 = ae4;
            ae4 = ae5;
            ae5 = t7;
        }
        a[e1] = ae1;
        a[e3] = ae3;
        a[e5] = ae5;
        double pivot1 = ae2;
        a[e2] = a[left];
        double pivot2 = ae4;
        a[e4] = a[right];
        int less = left + 1;
        int great = right - 1;
        boolean pivotsDiffer = pivot1 != pivot2;
        if (pivotsDiffer) {
            loop0: for (int k = less; k <= great; k++) {
                double ak = a[k];
                if (ak < pivot1) {
                    if (k != less) {
                        a[k] = a[less];
                        a[less] = ak;
                    }
                    less++;
                } else if (ak > pivot2) {
                    while (a[great] > pivot2) {
                        int i = great;
                        great--;
                        if (i == k) {
                            break loop0;
                        }
                    }
                    if (a[great] < pivot1) {
                        a[k] = a[less];
                        int i2 = less;
                        less++;
                        a[i2] = a[great];
                        int i3 = great;
                        great--;
                        a[i3] = ak;
                    } else {
                        a[k] = a[great];
                        int i4 = great;
                        great--;
                        a[i4] = ak;
                    }
                } else {
                    continue;
                }
            }
        } else {
            for (int k2 = less; k2 <= great; k2++) {
                double ak2 = a[k2];
                if (ak2 != pivot1) {
                    if (ak2 < pivot1) {
                        if (k2 != less) {
                            a[k2] = a[less];
                            a[less] = ak2;
                        }
                        less++;
                    } else {
                        while (a[great] > pivot1) {
                            great--;
                        }
                        if (a[great] < pivot1) {
                            a[k2] = a[less];
                            int i5 = less;
                            less++;
                            a[i5] = a[great];
                            int i6 = great;
                            great--;
                            a[i6] = ak2;
                        } else {
                            a[k2] = pivot1;
                            int i7 = great;
                            great--;
                            a[i7] = ak2;
                        }
                    }
                }
            }
        }
        a[left] = a[less - 1];
        a[less - 1] = pivot1;
        a[right] = a[great + 1];
        a[great + 1] = pivot2;
        doSort(a, left, less - 2);
        doSort(a, great + 2, right);
        if (!pivotsDiffer) {
            return;
        }
        if (less < e1 && great > e5) {
            while (a[less] == pivot1) {
                less++;
            }
            while (a[great] == pivot2) {
                great--;
            }
            loop4: for (int k3 = less; k3 <= great; k3++) {
                double ak3 = a[k3];
                if (ak3 == pivot2) {
                    while (a[great] == pivot2) {
                        int i8 = great;
                        great--;
                        if (i8 == k3) {
                            break loop4;
                        }
                    }
                    if (a[great] == pivot1) {
                        a[k3] = a[less];
                        int i9 = less;
                        less++;
                        a[i9] = pivot1;
                    } else {
                        a[k3] = a[great];
                    }
                    int i10 = great;
                    great--;
                    a[i10] = pivot2;
                } else if (ak3 == pivot1) {
                    a[k3] = a[less];
                    int i11 = less;
                    less++;
                    a[i11] = pivot1;
                }
            }
        }
        doSort(a, less, great);
    }
}