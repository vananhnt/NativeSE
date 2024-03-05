package libcore.util;

import gov.nist.core.Separators;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;
import libcore.io.BufferIterator;

/* loaded from: ZoneInfo.class */
public final class ZoneInfo extends TimeZone {
    private static final long MILLISECONDS_PER_DAY = 86400000;
    private static final long MILLISECONDS_PER_400_YEARS = 12622780800000L;
    private static final long UNIX_OFFSET = 62167219200000L;
    private static final int[] NORMAL = {0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334};
    private static final int[] LEAP = {0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335};
    private int mRawOffset;
    private final int mEarliestRawOffset;
    private final boolean mUseDst;
    private final int mDstSavings;
    private final int[] mTransitions;
    private final int[] mOffsets;
    private final byte[] mTypes;
    private final byte[] mIsDsts;

    public static TimeZone makeTimeZone(String id, BufferIterator it) {
        if (it.readInt() != 1415211366) {
            return null;
        }
        it.skip(28);
        int tzh_timecnt = it.readInt();
        int tzh_typecnt = it.readInt();
        it.skip(4);
        int[] transitions = new int[tzh_timecnt];
        it.readIntArray(transitions, 0, transitions.length);
        byte[] type = new byte[tzh_timecnt];
        it.readByteArray(type, 0, type.length);
        int[] gmtOffsets = new int[tzh_typecnt];
        byte[] isDsts = new byte[tzh_typecnt];
        for (int i = 0; i < tzh_typecnt; i++) {
            gmtOffsets[i] = it.readInt();
            isDsts[i] = it.readByte();
            it.skip(1);
        }
        return new ZoneInfo(id, transitions, type, gmtOffsets, isDsts);
    }

    private ZoneInfo(String name, int[] transitions, byte[] types, int[] gmtOffsets, byte[] isDsts) {
        this.mTransitions = transitions;
        this.mTypes = types;
        this.mIsDsts = isDsts;
        setID(name);
        int lastStd = 0;
        boolean haveStd = false;
        int lastDst = 0;
        boolean haveDst = false;
        int i = this.mTransitions.length - 1;
        while (true) {
            if ((!haveStd || !haveDst) && i >= 0) {
                int type = this.mTypes[i] & 255;
                if (!haveStd && this.mIsDsts[type] == 0) {
                    haveStd = true;
                    lastStd = i;
                }
                if (!haveDst && this.mIsDsts[type] != 0) {
                    haveDst = true;
                    lastDst = i;
                }
                i--;
            }
        }
        if (lastStd >= this.mTypes.length) {
            this.mRawOffset = gmtOffsets[0];
        } else {
            this.mRawOffset = gmtOffsets[this.mTypes[lastStd] & 255];
        }
        if (lastDst >= this.mTypes.length) {
            this.mDstSavings = 0;
        } else {
            this.mDstSavings = Math.abs(gmtOffsets[this.mTypes[lastStd] & 255] - gmtOffsets[this.mTypes[lastDst] & 255]) * 1000;
        }
        int firstStd = -1;
        int i2 = 0;
        while (true) {
            if (i2 < this.mTransitions.length) {
                if (this.mIsDsts[this.mTypes[i2] & 255] != 0) {
                    i2++;
                } else {
                    firstStd = i2;
                    break;
                }
            } else {
                break;
            }
        }
        int earliestRawOffset = firstStd != -1 ? gmtOffsets[this.mTypes[firstStd] & 255] : this.mRawOffset;
        this.mOffsets = gmtOffsets;
        for (int i3 = 0; i3 < this.mOffsets.length; i3++) {
            int[] iArr = this.mOffsets;
            int i4 = i3;
            iArr[i4] = iArr[i4] - this.mRawOffset;
        }
        boolean usesDst = false;
        long currentUnixTime = System.currentTimeMillis() / 1000;
        if (this.mTransitions.length > 0) {
            long latestScheduleTime = this.mTransitions[this.mTransitions.length - 1] & (-1);
            if (currentUnixTime < latestScheduleTime) {
                usesDst = true;
            }
        }
        this.mUseDst = usesDst;
        this.mRawOffset *= 1000;
        this.mEarliestRawOffset = earliestRawOffset * 1000;
    }

    @Override // java.util.TimeZone
    public int getOffset(int era, int year, int month, int day, int dayOfWeek, int millis) {
        int year2 = year % 400;
        long calc = ((year / 400) * MILLISECONDS_PER_400_YEARS) + (year2 * 31536000000L) + (((year2 + 3) / 4) * 86400000);
        if (year2 > 0) {
            calc -= ((year2 - 1) / 100) * 86400000;
        }
        boolean isLeap = year2 == 0 || (year2 % 4 == 0 && year2 % 100 != 0);
        int[] mlen = isLeap ? LEAP : NORMAL;
        return getOffset(((((calc + (mlen[month] * 86400000)) + ((day - 1) * 86400000)) + millis) - this.mRawOffset) - UNIX_OFFSET);
    }

    @Override // java.util.TimeZone
    public int getOffset(long when) {
        int unix = (int) (when / 1000);
        int transition = Arrays.binarySearch(this.mTransitions, unix);
        if (transition < 0) {
            transition = (transition ^ (-1)) - 1;
            if (transition < 0) {
                return this.mEarliestRawOffset;
            }
        }
        return this.mRawOffset + (this.mOffsets[this.mTypes[transition] & 255] * 1000);
    }

    @Override // java.util.TimeZone
    public boolean inDaylightTime(Date time) {
        long when = time.getTime();
        int unix = (int) (when / 1000);
        int transition = Arrays.binarySearch(this.mTransitions, unix);
        if (transition < 0) {
            transition = (transition ^ (-1)) - 1;
            if (transition < 0) {
                return false;
            }
        }
        return this.mIsDsts[this.mTypes[transition] & 255] == 1;
    }

    @Override // java.util.TimeZone
    public int getRawOffset() {
        return this.mRawOffset;
    }

    @Override // java.util.TimeZone
    public void setRawOffset(int off) {
        this.mRawOffset = off;
    }

    @Override // java.util.TimeZone
    public int getDSTSavings() {
        if (this.mUseDst) {
            return this.mDstSavings;
        }
        return 0;
    }

    @Override // java.util.TimeZone
    public boolean useDaylightTime() {
        return this.mUseDst;
    }

    @Override // java.util.TimeZone
    public boolean hasSameRules(TimeZone timeZone) {
        if (!(timeZone instanceof ZoneInfo)) {
            return false;
        }
        ZoneInfo other = (ZoneInfo) timeZone;
        if (this.mUseDst != other.mUseDst) {
            return false;
        }
        return !this.mUseDst ? this.mRawOffset == other.mRawOffset : this.mRawOffset == other.mRawOffset && Arrays.equals(this.mOffsets, other.mOffsets) && Arrays.equals(this.mIsDsts, other.mIsDsts) && Arrays.equals(this.mTypes, other.mTypes) && Arrays.equals(this.mTransitions, other.mTransitions);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ZoneInfo)) {
            return false;
        }
        ZoneInfo other = (ZoneInfo) obj;
        return getID().equals(other.getID()) && hasSameRules(other);
    }

    public int hashCode() {
        int result = (31 * 1) + getID().hashCode();
        return (31 * ((31 * ((31 * ((31 * ((31 * ((31 * result) + Arrays.hashCode(this.mOffsets))) + Arrays.hashCode(this.mIsDsts))) + this.mRawOffset)) + Arrays.hashCode(this.mTransitions))) + Arrays.hashCode(this.mTypes))) + (this.mUseDst ? 1231 : 1237);
    }

    public String toString() {
        return getClass().getName() + "[id=\"" + getID() + Separators.DOUBLE_QUOTE + ",mRawOffset=" + this.mRawOffset + ",mEarliestRawOffset=" + this.mEarliestRawOffset + ",mUseDst=" + this.mUseDst + ",mDstSavings=" + this.mDstSavings + ",transitions=" + this.mTransitions.length + "]";
    }
}