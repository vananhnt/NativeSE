package com.android.server;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.IConsumerIrService;
import android.os.PowerManager;
import android.util.Slog;

/* loaded from: ConsumerIrService.class */
public class ConsumerIrService extends IConsumerIrService.Stub {
    private static final String TAG = "ConsumerIrService";
    private static final int MAX_XMIT_TIME = 2000000;
    private final Context mContext;
    private final PowerManager.WakeLock mWakeLock;
    private final int mHal;
    private final Object mHalLock = new Object();

    private static native int halOpen();

    private static native int halTransmit(int i, int i2, int[] iArr);

    private static native int[] halGetCarrierFrequencies(int i);

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConsumerIrService(Context context) {
        this.mContext = context;
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(1, TAG);
        this.mWakeLock.setReferenceCounted(true);
        this.mHal = halOpen();
        if (this.mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CONSUMER_IR)) {
            if (this.mHal == 0) {
                throw new RuntimeException("FEATURE_CONSUMER_IR present, but no IR HAL loaded!");
            }
        } else if (this.mHal != 0) {
            throw new RuntimeException("IR HAL present, but FEATURE_CONSUMER_IR is not set!");
        }
    }

    @Override // android.hardware.IConsumerIrService
    public boolean hasIrEmitter() {
        return this.mHal != 0;
    }

    private void throwIfNoIrEmitter() {
        if (this.mHal == 0) {
            throw new UnsupportedOperationException("IR emitter not available");
        }
    }

    @Override // android.hardware.IConsumerIrService
    public void transmit(String packageName, int carrierFrequency, int[] pattern) {
        if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.TRANSMIT_IR) != 0) {
            throw new SecurityException("Requires TRANSMIT_IR permission");
        }
        long totalXmitTime = 0;
        for (int slice : pattern) {
            if (slice <= 0) {
                throw new IllegalArgumentException("Non-positive IR slice");
            }
            totalXmitTime += slice;
        }
        if (totalXmitTime > 2000000) {
            throw new IllegalArgumentException("IR pattern too long");
        }
        throwIfNoIrEmitter();
        synchronized (this.mHalLock) {
            int err = halTransmit(this.mHal, carrierFrequency, pattern);
            if (err < 0) {
                Slog.e(TAG, "Error transmitting: " + err);
            }
        }
    }

    @Override // android.hardware.IConsumerIrService
    public int[] getCarrierFrequencies() {
        int[] halGetCarrierFrequencies;
        if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.TRANSMIT_IR) != 0) {
            throw new SecurityException("Requires TRANSMIT_IR permission");
        }
        throwIfNoIrEmitter();
        synchronized (this.mHalLock) {
            halGetCarrierFrequencies = halGetCarrierFrequencies(this.mHal);
        }
        return halGetCarrierFrequencies;
    }
}