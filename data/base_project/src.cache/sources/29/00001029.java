package android.support.v4.media.session;

import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaMetadata;
import android.media.Rating;
import android.media.VolumeProvider;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.ResultReceiver;

/* loaded from: MediaSessionCompatApi21.class */
class MediaSessionCompatApi21 {

    /* loaded from: MediaSessionCompatApi21$Callback.class */
    public interface Callback {
        void onCommand(String str, Bundle bundle, ResultReceiver resultReceiver);

        void onFastForward();

        boolean onMediaButtonEvent(Intent intent);

        void onPause();

        void onPlay();

        void onRewind();

        void onSeekTo(long j);

        void onSetRating(Object obj);

        void onSkipToNext();

        void onSkipToPrevious();

        void onStop();
    }

    /* loaded from: MediaSessionCompatApi21$CallbackProxy.class */
    static class CallbackProxy<T extends Callback> extends MediaSession.Callback {
        protected final T mCallback;

        public CallbackProxy(T t) {
            this.mCallback = t;
        }

        @Override // android.media.session.MediaSession.Callback
        public void onCommand(String str, Bundle bundle, ResultReceiver resultReceiver) {
            this.mCallback.onCommand(str, bundle, resultReceiver);
        }

        @Override // android.media.session.MediaSession.Callback
        public void onFastForward() {
            this.mCallback.onFastForward();
        }

        @Override // android.media.session.MediaSession.Callback
        public boolean onMediaButtonEvent(Intent intent) {
            return this.mCallback.onMediaButtonEvent(intent);
        }

        @Override // android.media.session.MediaSession.Callback
        public void onPause() {
            this.mCallback.onPause();
        }

        @Override // android.media.session.MediaSession.Callback
        public void onPlay() {
            this.mCallback.onPlay();
        }

        @Override // android.media.session.MediaSession.Callback
        public void onRewind() {
            this.mCallback.onRewind();
        }

        @Override // android.media.session.MediaSession.Callback
        public void onSeekTo(long j) {
            this.mCallback.onSeekTo(j);
        }

        @Override // android.media.session.MediaSession.Callback
        public void onSetRating(Rating rating) {
            this.mCallback.onSetRating(rating);
        }

        @Override // android.media.session.MediaSession.Callback
        public void onSkipToNext() {
            this.mCallback.onSkipToNext();
        }

        @Override // android.media.session.MediaSession.Callback
        public void onSkipToPrevious() {
            this.mCallback.onSkipToPrevious();
        }

        @Override // android.media.session.MediaSession.Callback
        public void onStop() {
            this.mCallback.onStop();
        }
    }

    MediaSessionCompatApi21() {
    }

    public static Object createCallback(Callback callback) {
        return new CallbackProxy(callback);
    }

    public static Object createSession(Context context, String str) {
        return new MediaSession(context, str);
    }

    public static Parcelable getSessionToken(Object obj) {
        return ((MediaSession) obj).getSessionToken();
    }

    public static boolean isActive(Object obj) {
        return ((MediaSession) obj).isActive();
    }

    public static void release(Object obj) {
        ((MediaSession) obj).release();
    }

    public static void sendSessionEvent(Object obj, String str, Bundle bundle) {
        ((MediaSession) obj).sendSessionEvent(str, bundle);
    }

    public static void setActive(Object obj, boolean z) {
        ((MediaSession) obj).setActive(z);
    }

    public static void setCallback(Object obj, Object obj2, Handler handler) {
        ((MediaSession) obj).setCallback((MediaSession.Callback) obj2, handler);
    }

    public static void setFlags(Object obj, int i) {
        ((MediaSession) obj).setFlags(i);
    }

    public static void setMetadata(Object obj, Object obj2) {
        ((MediaSession) obj).setMetadata((MediaMetadata) obj2);
    }

    public static void setPlaybackState(Object obj, Object obj2) {
        ((MediaSession) obj).setPlaybackState((PlaybackState) obj2);
    }

    public static void setPlaybackToLocal(Object obj, int i) {
        AudioAttributes.Builder builder = new AudioAttributes.Builder();
        builder.setLegacyStreamType(i);
        ((MediaSession) obj).setPlaybackToLocal(builder.build());
    }

    public static void setPlaybackToRemote(Object obj, Object obj2) {
        ((MediaSession) obj).setPlaybackToRemote((VolumeProvider) obj2);
    }

    public static Object verifySession(Object obj) {
        if (obj instanceof MediaSession) {
            return obj;
        }
        throw new IllegalArgumentException("mediaSession is not a valid MediaSession object");
    }
}