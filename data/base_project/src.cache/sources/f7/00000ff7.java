package android.support.v4.media;

import android.os.Build;
import android.support.v4.media.VolumeProviderCompatApi21;

/* loaded from: VolumeProviderCompat.class */
public abstract class VolumeProviderCompat {
    public static final int VOLUME_CONTROL_ABSOLUTE = 2;
    public static final int VOLUME_CONTROL_FIXED = 0;
    public static final int VOLUME_CONTROL_RELATIVE = 1;
    private Callback mCallback;
    private final int mControlType;
    private int mCurrentVolume;
    private final int mMaxVolume;
    private Object mVolumeProviderObj;

    /* loaded from: VolumeProviderCompat$Callback.class */
    public static abstract class Callback {
        public abstract void onVolumeChanged(VolumeProviderCompat volumeProviderCompat);
    }

    public VolumeProviderCompat(int i, int i2, int i3) {
        this.mControlType = i;
        this.mMaxVolume = i2;
        this.mCurrentVolume = i3;
    }

    public final int getCurrentVolume() {
        return this.mCurrentVolume;
    }

    public final int getMaxVolume() {
        return this.mMaxVolume;
    }

    public final int getVolumeControl() {
        return this.mControlType;
    }

    public Object getVolumeProvider() {
        if (this.mVolumeProviderObj != null || Build.VERSION.SDK_INT < 21) {
            return this.mVolumeProviderObj;
        }
        this.mVolumeProviderObj = VolumeProviderCompatApi21.createVolumeProvider(this.mControlType, this.mMaxVolume, this.mCurrentVolume, new VolumeProviderCompatApi21.Delegate(this) { // from class: android.support.v4.media.VolumeProviderCompat.1
            final VolumeProviderCompat this$0;

            {
                this.this$0 = this;
            }

            @Override // android.support.v4.media.VolumeProviderCompatApi21.Delegate
            public void onAdjustVolume(int i) {
                this.this$0.onAdjustVolume(i);
            }

            @Override // android.support.v4.media.VolumeProviderCompatApi21.Delegate
            public void onSetVolumeTo(int i) {
                this.this$0.onSetVolumeTo(i);
            }
        });
        return this.mVolumeProviderObj;
    }

    public void onAdjustVolume(int i) {
    }

    public void onSetVolumeTo(int i) {
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    public final void setCurrentVolume(int i) {
        Callback callback = this.mCallback;
        if (callback != null) {
            callback.onVolumeChanged(this);
        }
    }
}