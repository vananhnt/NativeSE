package com.android.internal.telephony;

import android.telephony.Rlog;
import android.text.TextUtils;
import android.util.Pair;
import gov.nist.core.Separators;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/* loaded from: RetryManager.class */
public class RetryManager {
    public static final String LOG_TAG = "RetryManager";
    public static final boolean DBG = false;
    public static final boolean VDBG = false;
    private boolean mRetryForever;
    private int mMaxRetryCount;
    private int mCurMaxRetryCount;
    private int mRetryCount;
    private String mConfig;
    private ArrayList<RetryRec> mRetryArray = new ArrayList<>();
    private Random mRng = new Random();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: RetryManager$RetryRec.class */
    public static class RetryRec {
        int mDelayTime;
        int mRandomizationTime;

        RetryRec(int delayTime, int randomizationTime) {
            this.mDelayTime = delayTime;
            this.mRandomizationTime = randomizationTime;
        }
    }

    public String toString() {
        String ret = "RetryManager: { forever=" + this.mRetryForever + " maxRetry=" + this.mMaxRetryCount + " curMaxRetry=" + this.mCurMaxRetryCount + " retry=" + this.mRetryCount + " config={" + this.mConfig + "} retryArray={";
        Iterator i$ = this.mRetryArray.iterator();
        while (i$.hasNext()) {
            RetryRec r = i$.next();
            ret = ret + r.mDelayTime + Separators.COLON + r.mRandomizationTime + Separators.SP;
        }
        return ret + "}}";
    }

    public boolean configure(int maxRetryCount, int retryTime, int randomizationTime) {
        if (!validateNonNegativeInt("maxRetryCount", maxRetryCount) || !validateNonNegativeInt("retryTime", retryTime) || !validateNonNegativeInt("randomizationTime", randomizationTime)) {
            return false;
        }
        this.mMaxRetryCount = maxRetryCount;
        this.mCurMaxRetryCount = this.mMaxRetryCount;
        resetRetryCount();
        this.mRetryArray.clear();
        this.mRetryArray.add(new RetryRec(retryTime, randomizationTime));
        return true;
    }

    public boolean configure(String configStr) {
        if (configStr.startsWith(Separators.DOUBLE_QUOTE) && configStr.endsWith(Separators.DOUBLE_QUOTE)) {
            configStr = configStr.substring(1, configStr.length() - 1);
        }
        this.mConfig = configStr;
        if (!TextUtils.isEmpty(configStr)) {
            int defaultRandomization = 0;
            this.mMaxRetryCount = 0;
            resetRetryCount();
            this.mRetryArray.clear();
            String[] strArray = configStr.split(Separators.COMMA);
            for (int i = 0; i < strArray.length; i++) {
                String[] splitStr = strArray[i].split(Separators.EQUALS, 2);
                splitStr[0] = splitStr[0].trim();
                if (splitStr.length > 1) {
                    splitStr[1] = splitStr[1].trim();
                    if (TextUtils.equals(splitStr[0], "default_randomization")) {
                        Pair<Boolean, Integer> value = parseNonNegativeInt(splitStr[0], splitStr[1]);
                        if (!value.first.booleanValue()) {
                            return false;
                        }
                        defaultRandomization = value.second.intValue();
                    } else if (TextUtils.equals(splitStr[0], "max_retries")) {
                        if (TextUtils.equals("infinite", splitStr[1])) {
                            this.mRetryForever = true;
                        } else {
                            Pair<Boolean, Integer> value2 = parseNonNegativeInt(splitStr[0], splitStr[1]);
                            if (!value2.first.booleanValue()) {
                                return false;
                            }
                            this.mMaxRetryCount = value2.second.intValue();
                        }
                    } else {
                        Rlog.e(LOG_TAG, "Unrecognized configuration name value pair: " + strArray[i]);
                        return false;
                    }
                } else {
                    String[] splitStr2 = strArray[i].split(Separators.COLON, 2);
                    splitStr2[0] = splitStr2[0].trim();
                    RetryRec rr = new RetryRec(0, 0);
                    Pair<Boolean, Integer> value3 = parseNonNegativeInt("delayTime", splitStr2[0]);
                    if (!value3.first.booleanValue()) {
                        return false;
                    }
                    rr.mDelayTime = value3.second.intValue();
                    if (splitStr2.length > 1) {
                        splitStr2[1] = splitStr2[1].trim();
                        Pair<Boolean, Integer> value4 = parseNonNegativeInt("randomizationTime", splitStr2[1]);
                        if (!value4.first.booleanValue()) {
                            return false;
                        }
                        rr.mRandomizationTime = value4.second.intValue();
                    } else {
                        rr.mRandomizationTime = defaultRandomization;
                    }
                    this.mRetryArray.add(rr);
                }
            }
            if (this.mRetryArray.size() > this.mMaxRetryCount) {
                this.mMaxRetryCount = this.mRetryArray.size();
            }
            this.mCurMaxRetryCount = this.mMaxRetryCount;
            return true;
        }
        return false;
    }

    public boolean isRetryNeeded() {
        boolean retVal = this.mRetryForever || this.mRetryCount < this.mCurMaxRetryCount;
        return retVal;
    }

    public int getRetryTimer() {
        int index;
        int retVal;
        if (this.mRetryCount < this.mRetryArray.size()) {
            index = this.mRetryCount;
        } else {
            index = this.mRetryArray.size() - 1;
        }
        if (index >= 0 && index < this.mRetryArray.size()) {
            retVal = this.mRetryArray.get(index).mDelayTime + nextRandomizationTime(index);
        } else {
            retVal = 0;
        }
        return retVal;
    }

    public int getRetryCount() {
        return this.mRetryCount;
    }

    public void increaseRetryCount() {
        this.mRetryCount++;
        if (this.mRetryCount > this.mCurMaxRetryCount) {
            this.mRetryCount = this.mCurMaxRetryCount;
        }
    }

    public void setRetryCount(int count) {
        this.mRetryCount = count;
        if (this.mRetryCount > this.mCurMaxRetryCount) {
            this.mRetryCount = this.mCurMaxRetryCount;
        }
        if (this.mRetryCount < 0) {
            this.mRetryCount = 0;
        }
    }

    public void setCurMaxRetryCount(int count) {
        this.mCurMaxRetryCount = count;
        if (this.mCurMaxRetryCount < 0) {
            this.mCurMaxRetryCount = 0;
        }
        setRetryCount(this.mRetryCount);
    }

    public void restoreCurMaxRetryCount() {
        this.mCurMaxRetryCount = this.mMaxRetryCount;
        setRetryCount(this.mRetryCount);
    }

    public void setRetryForever(boolean retryForever) {
        this.mRetryForever = retryForever;
    }

    public void resetRetryCount() {
        this.mRetryCount = 0;
    }

    public void retryForeverUsingLastTimeout() {
        this.mRetryCount = this.mCurMaxRetryCount;
        this.mRetryForever = true;
    }

    public boolean isRetryForever() {
        return this.mRetryForever;
    }

    private Pair<Boolean, Integer> parseNonNegativeInt(String name, String stringValue) {
        Pair<Boolean, Integer> retVal;
        try {
            int value = Integer.parseInt(stringValue);
            retVal = new Pair<>(Boolean.valueOf(validateNonNegativeInt(name, value)), Integer.valueOf(value));
        } catch (NumberFormatException e) {
            Rlog.e(LOG_TAG, name + " bad value: " + stringValue, e);
            retVal = new Pair<>(false, 0);
        }
        return retVal;
    }

    private boolean validateNonNegativeInt(String name, int value) {
        boolean retVal;
        if (value < 0) {
            Rlog.e(LOG_TAG, name + " bad value: is < 0");
            retVal = false;
        } else {
            retVal = true;
        }
        return retVal;
    }

    private int nextRandomizationTime(int index) {
        int randomTime = this.mRetryArray.get(index).mRandomizationTime;
        if (randomTime == 0) {
            return 0;
        }
        return this.mRng.nextInt(randomTime);
    }

    private void log(String s) {
        Rlog.d(LOG_TAG, "[RM] " + s);
    }
}