package android.support.v4.media;

import android.media.VolumeProvider;

/* loaded from: VolumeProviderCompatApi21.class */
class VolumeProviderCompatApi21 {

    /* loaded from: VolumeProviderCompatApi21$Delegate.class */
    public interface Delegate {
        void onAdjustVolume(int i);

        void onSetVolumeTo(int i);
    }

    VolumeProviderCompatApi21() {
    }

    public static Object createVolumeProvider(int i, int i2, int i3, Delegate delegate) {
        return new VolumeProvider(i, i2, i3, delegate) { // from class: android.support.v4.media.VolumeProviderCompatApi21.1
            final Delegate val$delegate;

            {
                this.val$delegate = delegate;
            }

            @Override // android.media.VolumeProvider
            public void onAdjustVolume(int i4) {
                this.val$delegate.onAdjustVolume(i4);
            }

            @Override // android.media.VolumeProvider
            public void onSetVolumeTo(int i4) {
                this.val$delegate.onSetVolumeTo(i4);
            }
        };
    }

    public static void setCurrentVolume(Object obj, int i) {
        ((VolumeProvider) obj).setCurrentVolume(i);
    }
}