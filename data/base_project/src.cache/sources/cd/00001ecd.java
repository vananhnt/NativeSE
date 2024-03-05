package com.android.server.location;

import android.content.Context;
import android.location.Country;
import android.location.CountryListener;
import android.os.Handler;

/* loaded from: CountryDetectorBase.class */
public abstract class CountryDetectorBase {
    protected final Handler mHandler = new Handler();
    protected final Context mContext;
    protected CountryListener mListener;
    protected Country mDetectedCountry;

    public abstract Country detectCountry();

    public abstract void stop();

    public CountryDetectorBase(Context ctx) {
        this.mContext = ctx;
    }

    public void setCountryListener(CountryListener listener) {
        this.mListener = listener;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void notifyListener(Country country) {
        if (this.mListener != null) {
            this.mListener.onCountryDetected(country);
        }
    }
}