package java.util;

/* loaded from: ComparableTimSort.class */
class ComparableTimSort {
    private static final int MIN_MERGE = 32;
    private final Object[] a;
    private static final int MIN_GALLOP = 7;
    private static final int INITIAL_TMP_STORAGE_LENGTH = 256;
    private Object[] tmp;
    private final int[] runBase;
    private final int[] runLen;
    private static final boolean DEBUG = false;
    private int minGallop = 7;
    private int stackSize = 0;

    private ComparableTimSort(Object[] a) {
        this.a = a;
        int len = a.length;
        Object[] newArray = new Object[len < 512 ? len >>> 1 : 256];
        this.tmp = newArray;
        int stackLen = len < 120 ? 5 : len < 1542 ? 10 : len < 119151 ? 19 : 40;
        this.runBase = new int[stackLen];
        this.runLen = new int[stackLen];
    }

    static void sort(Object[] a) {
        sort(a, 0, a.length);
    }

    static void sort(Object[] a, int lo, int hi) {
        Arrays.checkStartAndEnd(a.length, lo, hi);
        int nRemaining = hi - lo;
        if (nRemaining < 2) {
            return;
        }
        if (nRemaining < 32) {
            int initRunLen = countRunAndMakeAscending(a, lo, hi);
            binarySort(a, lo, hi, lo + initRunLen);
            return;
        }
        ComparableTimSort ts = new ComparableTimSort(a);
        int minRun = minRunLength(nRemaining);
        do {
            int runLen = countRunAndMakeAscending(a, lo, hi);
            if (runLen < minRun) {
                int force = nRemaining <= minRun ? nRemaining : minRun;
                binarySort(a, lo, lo + force, lo + runLen);
                runLen = force;
            }
            ts.pushRun(lo, runLen);
            ts.mergeCollapse();
            lo += runLen;
            nRemaining -= runLen;
        } while (nRemaining != 0);
        ts.mergeForceCollapse();
    }

    private static void binarySort(Object[] a, int lo, int hi, int start) {
        if (start == lo) {
            start++;
        }
        while (start < hi) {
            Comparable<Object> pivot = (Comparable) a[start];
            int left = lo;
            int right = start;
            while (left < right) {
                int mid = (left + right) >>> 1;
                if (pivot.compareTo(a[mid]) < 0) {
                    right = mid;
                } else {
                    left = mid + 1;
                }
            }
            int n = start - left;
            switch (n) {
                case 1:
                    break;
                case 2:
                    a[left + 2] = a[left + 1];
                    break;
                default:
                    System.arraycopy(a, left, a, left + 1, n);
                    continue;
                    a[left] = pivot;
                    start++;
            }
            a[left + 1] = a[left];
            a[left] = pivot;
            start++;
        }
    }

    private static int countRunAndMakeAscending(Object[] a, int lo, int hi) {
        int runHi = lo + 1;
        if (runHi == hi) {
            return 1;
        }
        int runHi2 = runHi + 1;
        if (((Comparable) a[runHi]).compareTo(a[lo]) < 0) {
            while (runHi2 < hi && ((Comparable) a[runHi2]).compareTo(a[runHi2 - 1]) < 0) {
                runHi2++;
            }
            reverseRange(a, lo, runHi2);
        } else {
            while (runHi2 < hi && ((Comparable) a[runHi2]).compareTo(a[runHi2 - 1]) >= 0) {
                runHi2++;
            }
        }
        return runHi2 - lo;
    }

    private static void reverseRange(Object[] a, int lo, int hi) {
        int hi2 = hi - 1;
        while (lo < hi2) {
            Object t = a[lo];
            int i = lo;
            lo++;
            a[i] = a[hi2];
            int i2 = hi2;
            hi2--;
            a[i2] = t;
        }
    }

    private static int minRunLength(int n) {
        int r = 0;
        while (n >= 32) {
            r |= n & 1;
            n >>= 1;
        }
        return n + r;
    }

    private void pushRun(int runBase, int runLen) {
        this.runBase[this.stackSize] = runBase;
        this.runLen[this.stackSize] = runLen;
        this.stackSize++;
    }

    private void mergeCollapse() {
        while (this.stackSize > 1) {
            int n = this.stackSize - 2;
            if (n > 0 && this.runLen[n - 1] <= this.runLen[n] + this.runLen[n + 1]) {
                if (this.runLen[n - 1] < this.runLen[n + 1]) {
                    n--;
                }
                mergeAt(n);
            } else if (this.runLen[n] <= this.runLen[n + 1]) {
                mergeAt(n);
            } else {
                return;
            }
        }
    }

    private void mergeForceCollapse() {
        while (this.stackSize > 1) {
            int n = this.stackSize - 2;
            if (n > 0 && this.runLen[n - 1] < this.runLen[n + 1]) {
                n--;
            }
            mergeAt(n);
        }
    }

    private void mergeAt(int i) {
        int len2;
        int base1 = this.runBase[i];
        int len1 = this.runLen[i];
        int base2 = this.runBase[i + 1];
        int len22 = this.runLen[i + 1];
        this.runLen[i] = len1 + len22;
        if (i == this.stackSize - 3) {
            this.runBase[i + 1] = this.runBase[i + 2];
            this.runLen[i + 1] = this.runLen[i + 2];
        }
        this.stackSize--;
        int k = gallopRight((Comparable) this.a[base2], this.a, base1, len1, 0);
        int base12 = base1 + k;
        int len12 = len1 - k;
        if (len12 == 0 || (len2 = gallopLeft((Comparable) this.a[(base12 + len12) - 1], this.a, base2, len22, len22 - 1)) == 0) {
            return;
        }
        if (len12 <= len2) {
            mergeLo(base12, len12, base2, len2);
        } else {
            mergeHi(base12, len12, base2, len2);
        }
    }

    private static int gallopLeft(Comparable<Object> key, Object[] a, int base, int len, int hint) {
        int lastOfs;
        int ofs;
        int lastOfs2 = 0;
        int ofs2 = 1;
        if (key.compareTo(a[base + hint]) > 0) {
            int maxOfs = len - hint;
            while (ofs2 < maxOfs && key.compareTo(a[base + hint + ofs2]) > 0) {
                lastOfs2 = ofs2;
                ofs2 = (ofs2 << 1) + 1;
                if (ofs2 <= 0) {
                    ofs2 = maxOfs;
                }
            }
            if (ofs2 > maxOfs) {
                ofs2 = maxOfs;
            }
            lastOfs = lastOfs2 + hint;
            ofs = ofs2 + hint;
        } else {
            int maxOfs2 = hint + 1;
            while (ofs2 < maxOfs2 && key.compareTo(a[(base + hint) - ofs2]) <= 0) {
                lastOfs2 = ofs2;
                ofs2 = (ofs2 << 1) + 1;
                if (ofs2 <= 0) {
                    ofs2 = maxOfs2;
                }
            }
            if (ofs2 > maxOfs2) {
                ofs2 = maxOfs2;
            }
            int tmp = lastOfs2;
            lastOfs = hint - ofs2;
            ofs = hint - tmp;
        }
        int lastOfs3 = lastOfs + 1;
        while (lastOfs3 < ofs) {
            int m = lastOfs3 + ((ofs - lastOfs3) >>> 1);
            if (key.compareTo(a[base + m]) > 0) {
                lastOfs3 = m + 1;
            } else {
                ofs = m;
            }
        }
        return ofs;
    }

    private static int gallopRight(Comparable<Object> key, Object[] a, int base, int len, int hint) {
        int lastOfs;
        int ofs;
        int ofs2 = 1;
        int lastOfs2 = 0;
        if (key.compareTo(a[base + hint]) < 0) {
            int maxOfs = hint + 1;
            while (ofs2 < maxOfs && key.compareTo(a[(base + hint) - ofs2]) < 0) {
                lastOfs2 = ofs2;
                ofs2 = (ofs2 << 1) + 1;
                if (ofs2 <= 0) {
                    ofs2 = maxOfs;
                }
            }
            if (ofs2 > maxOfs) {
                ofs2 = maxOfs;
            }
            int tmp = lastOfs2;
            lastOfs = hint - ofs2;
            ofs = hint - tmp;
        } else {
            int maxOfs2 = len - hint;
            while (ofs2 < maxOfs2 && key.compareTo(a[base + hint + ofs2]) >= 0) {
                lastOfs2 = ofs2;
                ofs2 = (ofs2 << 1) + 1;
                if (ofs2 <= 0) {
                    ofs2 = maxOfs2;
                }
            }
            if (ofs2 > maxOfs2) {
                ofs2 = maxOfs2;
            }
            lastOfs = lastOfs2 + hint;
            ofs = ofs2 + hint;
        }
        int lastOfs3 = lastOfs + 1;
        while (lastOfs3 < ofs) {
            int m = lastOfs3 + ((ofs - lastOfs3) >>> 1);
            if (key.compareTo(a[base + m]) < 0) {
                ofs = m;
            } else {
                lastOfs3 = m + 1;
            }
        }
        return ofs;
    }

    /* JADX WARN: Code restructure failed: missing block: B:22:0x00d2, code lost:
        r0 = gallopRight((java.lang.Comparable) r0[r14], r0, r13, r8, 0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x00e7, code lost:
        if (r0 == 0) goto L38;
     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x00ea, code lost:
        java.lang.System.arraycopy(r0, r13, r0, r15, r0);
        r15 = r15 + r0;
        r13 = r13 + r0;
        r8 = r8 - r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x010c, code lost:
        if (r8 > 1) goto L38;
     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x0112, code lost:
        r1 = r15;
        r15 = r15 + 1;
        r3 = r14;
        r14 = r14 + 1;
        r0[r1] = r0[r3];
        r10 = r10 - 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x0127, code lost:
        if (r10 != 0) goto L40;
     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x012d, code lost:
        r0 = gallopLeft((java.lang.Comparable) r0[r13], r0, r14, r10, 0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x0143, code lost:
        if (r0 == 0) goto L45;
     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x0146, code lost:
        java.lang.System.arraycopy(r0, r14, r0, r15, r0);
        r15 = r15 + r0;
        r14 = r14 + r0;
        r10 = r10 - r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x016a, code lost:
        if (r10 != 0) goto L45;
     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x0170, code lost:
        r1 = r15;
        r15 = r15 + 1;
        r3 = r13;
        r13 = r13 + 1;
        r0[r1] = r0[r3];
        r8 = r8 - 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x0185, code lost:
        if (r8 != 1) goto L47;
     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x018b, code lost:
        r16 = r16 - 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:39:0x0192, code lost:
        if (r0 < 7) goto L63;
     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x0195, code lost:
        r0 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:41:0x0199, code lost:
        r0 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x019e, code lost:
        if (r0 < 7) goto L62;
     */
    /* JADX WARN: Code restructure failed: missing block: B:44:0x01a1, code lost:
        r1 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:45:0x01a5, code lost:
        r1 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:47:0x01a7, code lost:
        if ((r0 | r1) != false) goto L19;
     */
    /* JADX WARN: Code restructure failed: missing block: B:49:0x01ac, code lost:
        if (r16 >= 0) goto L61;
     */
    /* JADX WARN: Code restructure failed: missing block: B:50:0x01af, code lost:
        r16 = 0;
     */
    /* JADX WARN: Removed duplicated region for block: B:73:0x00d2 A[EDGE_INSN: B:73:0x00d2->B:22:0x00d2 ?: BREAK  , SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void mergeLo(int r7, int r8, int r9, int r10) {
        /*
            Method dump skipped, instructions count: 517
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.ComparableTimSort.mergeLo(int, int, int, int):void");
    }

    /* JADX WARN: Code restructure failed: missing block: B:22:0x00f1, code lost:
        r0 = r10 - gallopRight((java.lang.Comparable) r0[r16], r0, r9, r10, r10 - 1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x0109, code lost:
        if (r0 == 0) goto L38;
     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x010c, code lost:
        r17 = r17 - r0;
        r15 = r15 - r0;
        r10 = r10 - r0;
        java.lang.System.arraycopy(r0, r15 + 1, r0, r17 + 1, r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x0131, code lost:
        if (r10 != 0) goto L38;
     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x0137, code lost:
        r1 = r17;
        r17 = r17 - 1;
        r3 = r16;
        r16 = r16 - 1;
        r0[r1] = r0[r3];
        r12 = r12 - 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x014d, code lost:
        if (r12 != 1) goto L40;
     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x0153, code lost:
        r0 = r12 - gallopLeft((java.lang.Comparable) r0[r15], r0, 0, r12, r12 - 1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x016e, code lost:
        if (r0 == 0) goto L45;
     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x0171, code lost:
        r17 = r17 - r0;
        r16 = r16 - r0;
        r12 = r12 - r0;
        java.lang.System.arraycopy(r0, r16 + 1, r0, r17 + 1, r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x019a, code lost:
        if (r12 > 1) goto L45;
     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x01a0, code lost:
        r1 = r17;
        r17 = r17 - 1;
        r3 = r15;
        r15 = r15 - 1;
        r0[r1] = r0[r3];
        r10 = r10 - 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x01b4, code lost:
        if (r10 != 0) goto L47;
     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x01ba, code lost:
        r18 = r18 - 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:39:0x01c1, code lost:
        if (r0 < 7) goto L63;
     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x01c4, code lost:
        r0 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:41:0x01c8, code lost:
        r0 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x01cd, code lost:
        if (r0 < 7) goto L62;
     */
    /* JADX WARN: Code restructure failed: missing block: B:44:0x01d0, code lost:
        r1 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:45:0x01d4, code lost:
        r1 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:47:0x01d6, code lost:
        if ((r0 | r1) != false) goto L19;
     */
    /* JADX WARN: Code restructure failed: missing block: B:49:0x01db, code lost:
        if (r18 >= 0) goto L61;
     */
    /* JADX WARN: Code restructure failed: missing block: B:50:0x01de, code lost:
        r18 = 0;
     */
    /* JADX WARN: Removed duplicated region for block: B:73:0x00f1 A[EDGE_INSN: B:73:0x00f1->B:22:0x00f1 ?: BREAK  , SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void mergeHi(int r9, int r10, int r11, int r12) {
        /*
            Method dump skipped, instructions count: 583
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.ComparableTimSort.mergeHi(int, int, int, int):void");
    }

    private Object[] ensureCapacity(int minCapacity) {
        int newSize;
        if (this.tmp.length < minCapacity) {
            int newSize2 = minCapacity | (minCapacity >> 1);
            int newSize3 = newSize2 | (newSize2 >> 2);
            int newSize4 = newSize3 | (newSize3 >> 4);
            int newSize5 = newSize4 | (newSize4 >> 8);
            int newSize6 = (newSize5 | (newSize5 >> 16)) + 1;
            if (newSize6 < 0) {
                newSize = minCapacity;
            } else {
                newSize = Math.min(newSize6, this.a.length >>> 1);
            }
            Object[] newArray = new Object[newSize];
            this.tmp = newArray;
        }
        return this.tmp;
    }
}