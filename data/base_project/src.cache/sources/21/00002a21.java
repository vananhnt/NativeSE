package org.apache.harmony.security.x509.tsp;

import java.security.InvalidParameterException;

/* loaded from: PKIFailureInfo.class */
public enum PKIFailureInfo {
    BAD_ALG(0),
    BAD_REQUEST(2),
    BAD_DATA_FORMAT(5),
    TIME_NOT_AVAILABLE(14),
    UNACCEPTED_POLICY(15),
    UNACCEPTED_EXTENSION(16),
    ADD_INFO_NOT_AVAILABLE(17),
    SYSTEM_FAILURE(25);
    
    private final int value;
    private static int maxValue;

    PKIFailureInfo(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static int getMaxValue() {
        if (maxValue == 0) {
            PKIFailureInfo[] arr$ = values();
            for (PKIFailureInfo cur : arr$) {
                if (cur.value > maxValue) {
                    maxValue = cur.value;
                }
            }
        }
        return maxValue;
    }

    public static PKIFailureInfo getInstance(int value) {
        PKIFailureInfo[] arr$ = values();
        for (PKIFailureInfo info : arr$) {
            if (value == info.value) {
                return info;
            }
        }
        throw new InvalidParameterException("Unknown PKIFailureInfo value");
    }
}