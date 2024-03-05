package android.speech.tts;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.ConditionVariable;
import android.speech.tts.TextToSpeechService;
import android.util.Log;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: AudioPlaybackQueueItem.class */
public class AudioPlaybackQueueItem extends PlaybackQueueItem {
    private static final String TAG = "TTS.AudioQueueItem";
    private final Context mContext;
    private final Uri mUri;
    private final int mStreamType;
    private final ConditionVariable mDone;
    private MediaPlayer mPlayer;
    private volatile boolean mFinished;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AudioPlaybackQueueItem(TextToSpeechService.UtteranceProgressDispatcher dispatcher, Object callerIdentity, Context context, Uri uri, int streamType) {
        super(dispatcher, callerIdentity);
        this.mContext = context;
        this.mUri = uri;
        this.mStreamType = streamType;
        this.mDone = new ConditionVariable();
        this.mPlayer = null;
        this.mFinished = false;
    }

    @Override // android.speech.tts.PlaybackQueueItem, java.lang.Runnable
    public void run() {
        TextToSpeechService.UtteranceProgressDispatcher dispatcher = getDispatcher();
        dispatcher.dispatchOnStart();
        this.mPlayer = MediaPlayer.create(this.mContext, this.mUri);
        if (this.mPlayer == null) {
            dispatcher.dispatchOnError();
            return;
        }
        try {
            this.mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() { // from class: android.speech.tts.AudioPlaybackQueueItem.1
                @Override // android.media.MediaPlayer.OnErrorListener
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Log.w(AudioPlaybackQueueItem.TAG, "Audio playback error: " + what + ", " + extra);
                    AudioPlaybackQueueItem.this.mDone.open();
                    return true;
                }
            });
            this.mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { // from class: android.speech.tts.AudioPlaybackQueueItem.2
                @Override // android.media.MediaPlayer.OnCompletionListener
                public void onCompletion(MediaPlayer mp) {
                    AudioPlaybackQueueItem.this.mFinished = true;
                    AudioPlaybackQueueItem.this.mDone.open();
                }
            });
            this.mPlayer.setAudioStreamType(this.mStreamType);
            this.mPlayer.start();
            this.mDone.block();
            finish();
        } catch (IllegalArgumentException ex) {
            Log.w(TAG, "MediaPlayer failed", ex);
            this.mDone.open();
        }
        if (this.mFinished) {
            dispatcher.dispatchOnDone();
        } else {
            dispatcher.dispatchOnError();
        }
    }

    private void finish() {
        try {
            this.mPlayer.stop();
        } catch (IllegalStateException e) {
        }
        this.mPlayer.release();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.speech.tts.PlaybackQueueItem
    public void stop(boolean isError) {
        this.mDone.open();
    }
}