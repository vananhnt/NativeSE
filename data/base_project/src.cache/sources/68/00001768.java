package android.widget;

import android.os.Handler;

/* loaded from: DoubleDigitManager.class */
class DoubleDigitManager {
    private final long timeoutInMillis;
    private final CallBack mCallBack;
    private Integer intermediateDigit;

    /* loaded from: DoubleDigitManager$CallBack.class */
    interface CallBack {
        boolean singleDigitIntermediate(int i);

        void singleDigitFinal(int i);

        boolean twoDigitsFinal(int i, int i2);
    }

    public DoubleDigitManager(long timeoutInMillis, CallBack callBack) {
        this.timeoutInMillis = timeoutInMillis;
        this.mCallBack = callBack;
    }

    public void reportDigit(int digit) {
        if (this.intermediateDigit == null) {
            this.intermediateDigit = Integer.valueOf(digit);
            new Handler().postDelayed(new Runnable() { // from class: android.widget.DoubleDigitManager.1
                @Override // java.lang.Runnable
                public void run() {
                    if (DoubleDigitManager.this.intermediateDigit != null) {
                        DoubleDigitManager.this.mCallBack.singleDigitFinal(DoubleDigitManager.this.intermediateDigit.intValue());
                        DoubleDigitManager.this.intermediateDigit = null;
                    }
                }
            }, this.timeoutInMillis);
            if (!this.mCallBack.singleDigitIntermediate(digit)) {
                this.intermediateDigit = null;
                this.mCallBack.singleDigitFinal(digit);
            }
        } else if (this.mCallBack.twoDigitsFinal(this.intermediateDigit.intValue(), digit)) {
            this.intermediateDigit = null;
        }
    }
}