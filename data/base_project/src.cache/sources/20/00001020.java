package android.support.v4.media.session;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.RatingCompat;
import android.support.v4.media.VolumeProviderCompat;
import android.support.v4.media.session.MediaSessionCompatApi21;
import android.text.TextUtils;

/* loaded from: MediaSessionCompat.class */
public class MediaSessionCompat {
    public static final int FLAG_HANDLES_MEDIA_BUTTONS = 1;
    public static final int FLAG_HANDLES_TRANSPORT_CONTROLS = 2;
    private final MediaSessionImpl mImpl;

    /* loaded from: MediaSessionCompat$Callback.class */
    public static abstract class Callback {
        final Object mCallbackObj;

        /* loaded from: MediaSessionCompat$Callback$StubApi21.class */
        private class StubApi21 implements MediaSessionCompatApi21.Callback {
            final Callback this$0;

            private StubApi21(Callback callback) {
                this.this$0 = callback;
            }

            @Override // android.support.v4.media.session.MediaSessionCompatApi21.Callback
            public void onCommand(String str, Bundle bundle, ResultReceiver resultReceiver) {
                this.this$0.onCommand(str, bundle, resultReceiver);
            }

            @Override // android.support.v4.media.session.MediaSessionCompatApi21.Callback
            public void onFastForward() {
                this.this$0.onFastForward();
            }

            @Override // android.support.v4.media.session.MediaSessionCompatApi21.Callback
            public boolean onMediaButtonEvent(Intent intent) {
                return this.this$0.onMediaButtonEvent(intent);
            }

            @Override // android.support.v4.media.session.MediaSessionCompatApi21.Callback
            public void onPause() {
                this.this$0.onPause();
            }

            @Override // android.support.v4.media.session.MediaSessionCompatApi21.Callback
            public void onPlay() {
                this.this$0.onPlay();
            }

            @Override // android.support.v4.media.session.MediaSessionCompatApi21.Callback
            public void onRewind() {
                this.this$0.onRewind();
            }

            @Override // android.support.v4.media.session.MediaSessionCompatApi21.Callback
            public void onSeekTo(long j) {
                this.this$0.onSeekTo(j);
            }

            @Override // android.support.v4.media.session.MediaSessionCompatApi21.Callback
            public void onSetRating(Object obj) {
                this.this$0.onSetRating(RatingCompat.fromRating(obj));
            }

            @Override // android.support.v4.media.session.MediaSessionCompatApi21.Callback
            public void onSkipToNext() {
                this.this$0.onSkipToNext();
            }

            @Override // android.support.v4.media.session.MediaSessionCompatApi21.Callback
            public void onSkipToPrevious() {
                this.this$0.onSkipToPrevious();
            }

            @Override // android.support.v4.media.session.MediaSessionCompatApi21.Callback
            public void onStop() {
                this.this$0.onStop();
            }
        }

        public Callback() {
            if (Build.VERSION.SDK_INT >= 21) {
                this.mCallbackObj = MediaSessionCompatApi21.createCallback(new StubApi21());
            } else {
                this.mCallbackObj = null;
            }
        }

        public void onCommand(String str, Bundle bundle, ResultReceiver resultReceiver) {
        }

        public void onFastForward() {
        }

        public boolean onMediaButtonEvent(Intent intent) {
            return false;
        }

        public void onPause() {
        }

        public void onPlay() {
        }

        public void onRewind() {
        }

        public void onSeekTo(long j) {
        }

        public void onSetRating(RatingCompat ratingCompat) {
        }

        public void onSkipToNext() {
        }

        public void onSkipToPrevious() {
        }

        public void onStop() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: MediaSessionCompat$MediaSessionImpl.class */
    public interface MediaSessionImpl {
        Object getMediaSession();

        Token getSessionToken();

        boolean isActive();

        void release();

        void sendSessionEvent(String str, Bundle bundle);

        void setActive(boolean z);

        void setCallback(Callback callback, Handler handler);

        void setFlags(int i);

        void setMetadata(MediaMetadataCompat mediaMetadataCompat);

        void setPlaybackState(PlaybackStateCompat playbackStateCompat);

        void setPlaybackToLocal(int i);

        void setPlaybackToRemote(VolumeProviderCompat volumeProviderCompat);
    }

    /* loaded from: MediaSessionCompat$MediaSessionImplApi21.class */
    static class MediaSessionImplApi21 implements MediaSessionImpl {
        private final Object mSessionObj;
        private final Token mToken;

        public MediaSessionImplApi21(Context context, String str) {
            this.mSessionObj = MediaSessionCompatApi21.createSession(context, str);
            this.mToken = new Token(MediaSessionCompatApi21.getSessionToken(this.mSessionObj));
        }

        public MediaSessionImplApi21(Object obj) {
            this.mSessionObj = MediaSessionCompatApi21.verifySession(obj);
            this.mToken = new Token(MediaSessionCompatApi21.getSessionToken(this.mSessionObj));
        }

        @Override // android.support.v4.media.session.MediaSessionCompat.MediaSessionImpl
        public Object getMediaSession() {
            return this.mSessionObj;
        }

        @Override // android.support.v4.media.session.MediaSessionCompat.MediaSessionImpl
        public Token getSessionToken() {
            return this.mToken;
        }

        @Override // android.support.v4.media.session.MediaSessionCompat.MediaSessionImpl
        public boolean isActive() {
            return MediaSessionCompatApi21.isActive(this.mSessionObj);
        }

        @Override // android.support.v4.media.session.MediaSessionCompat.MediaSessionImpl
        public void release() {
            MediaSessionCompatApi21.release(this.mSessionObj);
        }

        @Override // android.support.v4.media.session.MediaSessionCompat.MediaSessionImpl
        public void sendSessionEvent(String str, Bundle bundle) {
            MediaSessionCompatApi21.sendSessionEvent(this.mSessionObj, str, bundle);
        }

        @Override // android.support.v4.media.session.MediaSessionCompat.MediaSessionImpl
        public void setActive(boolean z) {
            MediaSessionCompatApi21.setActive(this.mSessionObj, z);
        }

        @Override // android.support.v4.media.session.MediaSessionCompat.MediaSessionImpl
        public void setCallback(Callback callback, Handler handler) {
            MediaSessionCompatApi21.setCallback(this.mSessionObj, callback.mCallbackObj, handler);
        }

        @Override // android.support.v4.media.session.MediaSessionCompat.MediaSessionImpl
        public void setFlags(int i) {
            MediaSessionCompatApi21.setFlags(this.mSessionObj, i);
        }

        @Override // android.support.v4.media.session.MediaSessionCompat.MediaSessionImpl
        public void setMetadata(MediaMetadataCompat mediaMetadataCompat) {
            MediaSessionCompatApi21.setMetadata(this.mSessionObj, mediaMetadataCompat.getMediaMetadata());
        }

        @Override // android.support.v4.media.session.MediaSessionCompat.MediaSessionImpl
        public void setPlaybackState(PlaybackStateCompat playbackStateCompat) {
            MediaSessionCompatApi21.setPlaybackState(this.mSessionObj, playbackStateCompat.getPlaybackState());
        }

        @Override // android.support.v4.media.session.MediaSessionCompat.MediaSessionImpl
        public void setPlaybackToLocal(int i) {
            MediaSessionCompatApi21.setPlaybackToLocal(this.mSessionObj, i);
        }

        @Override // android.support.v4.media.session.MediaSessionCompat.MediaSessionImpl
        public void setPlaybackToRemote(VolumeProviderCompat volumeProviderCompat) {
            MediaSessionCompatApi21.setPlaybackToRemote(this.mSessionObj, volumeProviderCompat.getVolumeProvider());
        }
    }

    /* loaded from: MediaSessionCompat$MediaSessionImplBase.class */
    static class MediaSessionImplBase implements MediaSessionImpl {
        MediaSessionImplBase() {
        }

        @Override // android.support.v4.media.session.MediaSessionCompat.MediaSessionImpl
        public Object getMediaSession() {
            return null;
        }

        @Override // android.support.v4.media.session.MediaSessionCompat.MediaSessionImpl
        public Token getSessionToken() {
            return null;
        }

        @Override // android.support.v4.media.session.MediaSessionCompat.MediaSessionImpl
        public boolean isActive() {
            return false;
        }

        @Override // android.support.v4.media.session.MediaSessionCompat.MediaSessionImpl
        public void release() {
        }

        @Override // android.support.v4.media.session.MediaSessionCompat.MediaSessionImpl
        public void sendSessionEvent(String str, Bundle bundle) {
        }

        @Override // android.support.v4.media.session.MediaSessionCompat.MediaSessionImpl
        public void setActive(boolean z) {
        }

        @Override // android.support.v4.media.session.MediaSessionCompat.MediaSessionImpl
        public void setCallback(Callback callback, Handler handler) {
        }

        @Override // android.support.v4.media.session.MediaSessionCompat.MediaSessionImpl
        public void setFlags(int i) {
        }

        @Override // android.support.v4.media.session.MediaSessionCompat.MediaSessionImpl
        public void setMetadata(MediaMetadataCompat mediaMetadataCompat) {
        }

        @Override // android.support.v4.media.session.MediaSessionCompat.MediaSessionImpl
        public void setPlaybackState(PlaybackStateCompat playbackStateCompat) {
        }

        @Override // android.support.v4.media.session.MediaSessionCompat.MediaSessionImpl
        public void setPlaybackToLocal(int i) {
        }

        @Override // android.support.v4.media.session.MediaSessionCompat.MediaSessionImpl
        public void setPlaybackToRemote(VolumeProviderCompat volumeProviderCompat) {
        }
    }

    /* loaded from: MediaSessionCompat$Token.class */
    public static final class Token implements Parcelable {
        public static final Parcelable.Creator<Token> CREATOR = new Parcelable.Creator<Token>() { // from class: android.support.v4.media.session.MediaSessionCompat.Token.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public Token createFromParcel(Parcel parcel) {
                return new Token(parcel.readParcelable(null));
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public Token[] newArray(int i) {
                return new Token[i];
            }
        };
        private final Parcelable mInner;

        Token(Parcelable parcelable) {
            this.mInner = parcelable;
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return this.mInner.describeContents();
        }

        public Object getToken() {
            return this.mInner;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeParcelable(this.mInner, i);
        }
    }

    public MediaSessionCompat(Context context, String str) {
        if (context == null) {
            throw new IllegalArgumentException("context must not be null");
        }
        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("tag must not be null or empty");
        }
        if (Build.VERSION.SDK_INT >= 21) {
            this.mImpl = new MediaSessionImplApi21(context, str);
        } else {
            this.mImpl = new MediaSessionImplBase();
        }
    }

    private MediaSessionCompat(MediaSessionImpl mediaSessionImpl) {
        this.mImpl = mediaSessionImpl;
    }

    public static MediaSessionCompat obtain(Object obj) {
        return new MediaSessionCompat(new MediaSessionImplApi21(obj));
    }

    public Object getMediaSession() {
        return this.mImpl.getMediaSession();
    }

    public Token getSessionToken() {
        return this.mImpl.getSessionToken();
    }

    public boolean isActive() {
        return this.mImpl.isActive();
    }

    public void release() {
        this.mImpl.release();
    }

    public void sendSessionEvent(String str, Bundle bundle) {
        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("event cannot be null or empty");
        }
        this.mImpl.sendSessionEvent(str, bundle);
    }

    public void setActive(boolean z) {
        this.mImpl.setActive(z);
    }

    public void setCallback(Callback callback) {
        setCallback(callback, null);
    }

    public void setCallback(Callback callback, Handler handler) {
        MediaSessionImpl mediaSessionImpl = this.mImpl;
        if (handler == null) {
            handler = new Handler();
        }
        mediaSessionImpl.setCallback(callback, handler);
    }

    public void setFlags(int i) {
        this.mImpl.setFlags(i);
    }

    public void setMetadata(MediaMetadataCompat mediaMetadataCompat) {
        this.mImpl.setMetadata(mediaMetadataCompat);
    }

    public void setPlaybackState(PlaybackStateCompat playbackStateCompat) {
        this.mImpl.setPlaybackState(playbackStateCompat);
    }

    public void setPlaybackToLocal(int i) {
        this.mImpl.setPlaybackToLocal(i);
    }

    public void setPlaybackToRemote(VolumeProviderCompat volumeProviderCompat) {
        if (volumeProviderCompat == null) {
            throw new IllegalArgumentException("volumeProvider may not be null!");
        }
        this.mImpl.setPlaybackToRemote(volumeProviderCompat);
    }
}