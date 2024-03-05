package android.media;

/* loaded from: VolumeController.class */
public interface VolumeController {
    void postHasNewRemotePlaybackInfo();

    void postRemoteVolumeChanged(int i, int i2);

    void postRemoteSliderVisibility(boolean z);
}