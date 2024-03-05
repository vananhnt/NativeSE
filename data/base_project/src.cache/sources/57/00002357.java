package java.math;

import android.widget.ExpandableListView;

/* loaded from: Division.class */
class Division {
    Division() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int divideArrayByInt(int[] quotient, int[] dividend, int dividendLength, int divisor) {
        long quot;
        long rem = 0;
        long bLong = divisor & ExpandableListView.PACKED_POSITION_VALUE_NULL;
        for (int i = dividendLength - 1; i >= 0; i--) {
            long temp = (rem << 32) | (dividend[i] & ExpandableListView.PACKED_POSITION_VALUE_NULL);
            if (temp >= 0) {
                quot = temp / bLong;
                rem = temp % bLong;
            } else {
                long aPos = temp >>> 1;
                long bPos = divisor >>> 1;
                quot = aPos / bPos;
                long rem2 = aPos % bPos;
                rem = (rem2 << 1) + (temp & 1);
                if ((divisor & 1) != 0) {
                    if (quot <= rem) {
                        rem -= quot;
                    } else if (quot - rem <= bLong) {
                        rem += bLong - quot;
                        quot--;
                    } else {
                        rem += (bLong << 1) - quot;
                        quot -= 2;
                    }
                }
            }
            quotient[i] = (int) (quot & ExpandableListView.PACKED_POSITION_VALUE_NULL);
        }
        return (int) rem;
    }
}