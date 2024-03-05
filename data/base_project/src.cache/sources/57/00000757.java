package android.media;

import android.media.SoundPool;
import android.util.Log;
import gov.nist.core.Separators;

/* loaded from: MediaActionSound.class */
public class MediaActionSound {
    private static final int NUM_MEDIA_SOUND_STREAMS = 1;
    private int[] mSoundIds;
    private int mSoundIdToPlay;
    private static final String[] SOUND_FILES = {"/system/media/audio/ui/camera_click.ogg", "/system/media/audio/ui/camera_focus.ogg", "/system/media/audio/ui/VideoRecord.ogg", "/system/media/audio/ui/VideoRecord.ogg"};
    private static final String TAG = "MediaActionSound";
    public static final int SHUTTER_CLICK = 0;
    public static final int FOCUS_COMPLETE = 1;
    public static final int START_VIDEO_RECORDING = 2;
    public static final int STOP_VIDEO_RECORDING = 3;
    private static final int SOUND_NOT_LOADED = -1;
    private SoundPool.OnLoadCompleteListener mLoadCompleteListener = new SoundPool.OnLoadCompleteListener() { // from class: android.media.MediaActionSound.1
        @Override // android.media.SoundPool.OnLoadCompleteListener
        public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
            if (status == 0) {
                if (MediaActionSound.this.mSoundIdToPlay == sampleId) {
                    soundPool.play(sampleId, 1.0f, 1.0f, 0, 0, 1.0f);
                    MediaActionSound.this.mSoundIdToPlay = -1;
                    return;
                }
                return;
            }
            Log.e(MediaActionSound.TAG, "Unable to load sound for playback (status: " + status + Separators.RPAREN);
        }
    };
    private SoundPool mSoundPool = new SoundPool(1, 7, 0);

    public MediaActionSound() {
        this.mSoundPool.setOnLoadCompleteListener(this.mLoadCompleteListener);
        this.mSoundIds = new int[SOUND_FILES.length];
        for (int i = 0; i < this.mSoundIds.length; i++) {
            this.mSoundIds[i] = -1;
        }
        this.mSoundIdToPlay = -1;
    }

    public synchronized void load(int soundName) {
        if (soundName < 0 || soundName >= SOUND_FILES.length) {
            throw new RuntimeException("Unknown sound requested: " + soundName);
        }
        if (this.mSoundIds[soundName] == -1) {
            this.mSoundIds[soundName] = this.mSoundPool.load(SOUND_FILES[soundName], 1);
        }
    }

    public synchronized void play(int soundName) {
        if (soundName < 0 || soundName >= SOUND_FILES.length) {
            throw new RuntimeException("Unknown sound requested: " + soundName);
        }
        if (this.mSoundIds[soundName] == -1) {
            this.mSoundIdToPlay = this.mSoundPool.load(SOUND_FILES[soundName], 1);
            this.mSoundIds[soundName] = this.mSoundIdToPlay;
            return;
        }
        this.mSoundPool.play(this.mSoundIds[soundName], 1.0f, 1.0f, 0, 0, 1.0f);
    }

    public void release() {
        if (this.mSoundPool != null) {
            this.mSoundPool.release();
            this.mSoundPool = null;
        }
    }
}