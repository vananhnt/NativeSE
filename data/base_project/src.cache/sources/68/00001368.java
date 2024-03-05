package android.text;

import android.text.Layout;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: AndroidBidi.class */
public class AndroidBidi {
    private static native int runBidi(int i, char[] cArr, byte[] bArr, int i2, boolean z);

    AndroidBidi() {
    }

    public static int bidi(int dir, char[] chs, byte[] chInfo, int n, boolean haveInfo) {
        int dir2;
        if (chs == null || chInfo == null) {
            throw new NullPointerException();
        }
        if (n < 0 || chs.length < n || chInfo.length < n) {
            throw new IndexOutOfBoundsException();
        }
        switch (dir) {
            case -2:
                dir2 = -1;
                break;
            case -1:
                dir2 = 1;
                break;
            case 0:
            default:
                dir2 = 0;
                break;
            case 1:
                dir2 = 0;
                break;
            case 2:
                dir2 = -2;
                break;
        }
        int result = runBidi(dir2, chs, chInfo, n, haveInfo);
        int result2 = (result & 1) == 0 ? 1 : -1;
        return result2;
    }

    public static Layout.Directions directions(int dir, byte[] levels, int lstart, char[] chars, int cstart, int len) {
        boolean swap;
        if (len == 0) {
            return Layout.DIRS_ALL_LEFT_TO_RIGHT;
        }
        int baseLevel = dir == 1 ? 0 : 1;
        int curLevel = levels[lstart];
        int minLevel = curLevel;
        int runCount = 1;
        int e = lstart + len;
        for (int i = lstart + 1; i < e; i++) {
            byte b = levels[i];
            if (b != curLevel) {
                curLevel = b;
                runCount++;
            }
        }
        int visLen = len;
        if ((curLevel & 1) != (baseLevel & 1)) {
            while (true) {
                visLen--;
                if (visLen < 0) {
                    break;
                }
                char ch = chars[cstart + visLen];
                if (ch == '\n') {
                    visLen--;
                    break;
                } else if (ch != ' ' && ch != '\t') {
                    break;
                }
            }
            visLen++;
            if (visLen != len) {
                runCount++;
            }
        }
        if (runCount == 1 && minLevel == baseLevel) {
            if ((minLevel & 1) != 0) {
                return Layout.DIRS_ALL_RIGHT_TO_LEFT;
            }
            return Layout.DIRS_ALL_LEFT_TO_RIGHT;
        }
        int[] ld = new int[runCount * 2];
        int maxLevel = minLevel;
        int levelBits = minLevel << 26;
        int n = 1;
        int prev = lstart;
        int curLevel2 = minLevel;
        int e2 = lstart + visLen;
        for (int i2 = lstart; i2 < e2; i2++) {
            byte b2 = levels[i2];
            if (b2 != curLevel2) {
                curLevel2 = b2;
                if (b2 > maxLevel) {
                    maxLevel = b2;
                } else if (b2 < minLevel) {
                    minLevel = b2;
                }
                int i3 = n;
                int n2 = n + 1;
                ld[i3] = (i2 - prev) | levelBits;
                n = n2 + 1;
                ld[n2] = i2 - lstart;
                levelBits = curLevel2 << 26;
                prev = i2;
            }
        }
        ld[n] = ((lstart + visLen) - prev) | levelBits;
        if (visLen < len) {
            int n3 = n + 1;
            ld[n3] = visLen;
            ld[n3 + 1] = (len - visLen) | (baseLevel << 26);
        }
        if ((minLevel & 1) == baseLevel) {
            minLevel++;
            swap = maxLevel > minLevel;
        } else {
            swap = runCount > 1;
        }
        if (swap) {
            for (int level = maxLevel - 1; level >= minLevel; level--) {
                int i4 = 0;
                while (i4 < ld.length) {
                    if (levels[ld[i4]] >= level) {
                        int e3 = i4 + 2;
                        while (e3 < ld.length && levels[ld[e3]] >= level) {
                            e3 += 2;
                        }
                        int low = i4;
                        for (int hi = e3 - 2; low < hi; hi -= 2) {
                            int x = ld[low];
                            ld[low] = ld[hi];
                            ld[hi] = x;
                            int x2 = ld[low + 1];
                            ld[low + 1] = ld[hi + 1];
                            ld[hi + 1] = x2;
                            low += 2;
                        }
                        i4 = e3 + 2;
                    }
                    i4 += 2;
                }
            }
        }
        return new Layout.Directions(ld);
    }
}