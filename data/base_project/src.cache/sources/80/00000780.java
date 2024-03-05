package android.media;

import android.app.backup.FullBackup;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.media.MediaTimeProvider;
import android.media.SubtitleController;
import android.net.Proxy;
import android.net.ProxyProperties;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.PowerManager;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import gov.nist.core.Separators;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Vector;

/* loaded from: MediaPlayer.class */
public class MediaPlayer implements SubtitleController.Listener {
    public static final boolean METADATA_UPDATE_ONLY = true;
    public static final boolean METADATA_ALL = false;
    public static final boolean APPLY_METADATA_FILTER = true;
    public static final boolean BYPASS_METADATA_FILTER = false;
    private static final String TAG = "MediaPlayer";
    private static final String IMEDIA_PLAYER = "android.media.IMediaPlayer";
    private int mNativeContext;
    private int mNativeSurfaceTexture;
    private int mListenerContext;
    private SurfaceHolder mSurfaceHolder;
    private EventHandler mEventHandler;
    private boolean mScreenOnWhilePlaying;
    private boolean mStayAwake;
    private static final int INVOKE_ID_GET_TRACK_INFO = 1;
    private static final int INVOKE_ID_ADD_EXTERNAL_SOURCE = 2;
    private static final int INVOKE_ID_ADD_EXTERNAL_SOURCE_FD = 3;
    private static final int INVOKE_ID_SELECT_TRACK = 4;
    private static final int INVOKE_ID_DESELECT_TRACK = 5;
    private static final int INVOKE_ID_SET_VIDEO_SCALE_MODE = 6;
    public static final int VIDEO_SCALING_MODE_SCALE_TO_FIT = 1;
    public static final int VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING = 2;
    public static final String MEDIA_MIMETYPE_TEXT_SUBRIP = "application/x-subrip";
    public static final String MEDIA_MIMETYPE_TEXT_VTT = "text/vtt";
    private SubtitleController mSubtitleController;
    private SubtitleTrack[] mInbandSubtitleTracks;
    private Vector<SubtitleTrack> mOutOfBandSubtitleTracks;
    private Vector<InputStream> mOpenSubtitleSources;
    private static final int MEDIA_NOP = 0;
    private static final int MEDIA_PREPARED = 1;
    private static final int MEDIA_PLAYBACK_COMPLETE = 2;
    private static final int MEDIA_BUFFERING_UPDATE = 3;
    private static final int MEDIA_SEEK_COMPLETE = 4;
    private static final int MEDIA_SET_VIDEO_SIZE = 5;
    private static final int MEDIA_STARTED = 6;
    private static final int MEDIA_PAUSED = 7;
    private static final int MEDIA_STOPPED = 8;
    private static final int MEDIA_SKIPPED = 9;
    private static final int MEDIA_TIMED_TEXT = 99;
    private static final int MEDIA_ERROR = 100;
    private static final int MEDIA_INFO = 200;
    private static final int MEDIA_SUBTITLE_DATA = 201;
    private TimeProvider mTimeProvider;
    private OnPreparedListener mOnPreparedListener;
    private OnCompletionListener mOnCompletionListener;
    private OnBufferingUpdateListener mOnBufferingUpdateListener;
    private OnSeekCompleteListener mOnSeekCompleteListener;
    private OnVideoSizeChangedListener mOnVideoSizeChangedListener;
    private OnTimedTextListener mOnTimedTextListener;
    private OnSubtitleDataListener mOnSubtitleDataListener;
    public static final int MEDIA_ERROR_UNKNOWN = 1;
    public static final int MEDIA_ERROR_SERVER_DIED = 100;
    public static final int MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK = 200;
    public static final int MEDIA_ERROR_IO = -1004;
    public static final int MEDIA_ERROR_MALFORMED = -1007;
    public static final int MEDIA_ERROR_UNSUPPORTED = -1010;
    public static final int MEDIA_ERROR_TIMED_OUT = -110;
    private OnErrorListener mOnErrorListener;
    public static final int MEDIA_INFO_UNKNOWN = 1;
    public static final int MEDIA_INFO_STARTED_AS_NEXT = 2;
    public static final int MEDIA_INFO_VIDEO_RENDERING_START = 3;
    public static final int MEDIA_INFO_VIDEO_TRACK_LAGGING = 700;
    public static final int MEDIA_INFO_BUFFERING_START = 701;
    public static final int MEDIA_INFO_BUFFERING_END = 702;
    public static final int MEDIA_INFO_BAD_INTERLEAVING = 800;
    public static final int MEDIA_INFO_NOT_SEEKABLE = 801;
    public static final int MEDIA_INFO_METADATA_UPDATE = 802;
    public static final int MEDIA_INFO_EXTERNAL_METADATA_UPDATE = 803;
    public static final int MEDIA_INFO_TIMED_TEXT_ERROR = 900;
    public static final int MEDIA_INFO_UNSUPPORTED_SUBTITLE = 901;
    public static final int MEDIA_INFO_SUBTITLE_TIMED_OUT = 902;
    private OnInfoListener mOnInfoListener;
    private PowerManager.WakeLock mWakeLock = null;
    private int mSelectedSubtitleTrackIndex = -1;
    private OnSubtitleDataListener mSubtitleDataListener = new OnSubtitleDataListener() { // from class: android.media.MediaPlayer.1
        @Override // android.media.MediaPlayer.OnSubtitleDataListener
        public void onSubtitleData(MediaPlayer mp, SubtitleData data) {
            SubtitleTrack track;
            int index = data.getTrackIndex();
            if (index < MediaPlayer.this.mInbandSubtitleTracks.length && (track = MediaPlayer.this.mInbandSubtitleTracks[index]) != null) {
                try {
                    long runID = data.getStartTimeUs() + 1;
                    track.onData(new String(data.getData(), "UTF-8"), true, runID);
                    track.setRunDiscardTimeMs(runID, (data.getStartTimeUs() + data.getDurationUs()) / 1000);
                } catch (UnsupportedEncodingException e) {
                    Log.w(MediaPlayer.TAG, "subtitle data for track " + index + " is not UTF-8 encoded: " + e);
                }
            }
        }
    };
    private Context mProxyContext = null;
    private ProxyReceiver mProxyReceiver = null;

    /* loaded from: MediaPlayer$OnBufferingUpdateListener.class */
    public interface OnBufferingUpdateListener {
        void onBufferingUpdate(MediaPlayer mediaPlayer, int i);
    }

    /* loaded from: MediaPlayer$OnCompletionListener.class */
    public interface OnCompletionListener {
        void onCompletion(MediaPlayer mediaPlayer);
    }

    /* loaded from: MediaPlayer$OnErrorListener.class */
    public interface OnErrorListener {
        boolean onError(MediaPlayer mediaPlayer, int i, int i2);
    }

    /* loaded from: MediaPlayer$OnInfoListener.class */
    public interface OnInfoListener {
        boolean onInfo(MediaPlayer mediaPlayer, int i, int i2);
    }

    /* loaded from: MediaPlayer$OnPreparedListener.class */
    public interface OnPreparedListener {
        void onPrepared(MediaPlayer mediaPlayer);
    }

    /* loaded from: MediaPlayer$OnSeekCompleteListener.class */
    public interface OnSeekCompleteListener {
        void onSeekComplete(MediaPlayer mediaPlayer);
    }

    /* loaded from: MediaPlayer$OnSubtitleDataListener.class */
    public interface OnSubtitleDataListener {
        void onSubtitleData(MediaPlayer mediaPlayer, SubtitleData subtitleData);
    }

    /* loaded from: MediaPlayer$OnTimedTextListener.class */
    public interface OnTimedTextListener {
        void onTimedText(MediaPlayer mediaPlayer, TimedText timedText);
    }

    /* loaded from: MediaPlayer$OnVideoSizeChangedListener.class */
    public interface OnVideoSizeChangedListener {
        void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i2);
    }

    private native void _setVideoSurface(Surface surface);

    private native void _setDataSource(String str, String[] strArr, String[] strArr2) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException;

    private native void _setDataSource(FileDescriptor fileDescriptor, long j, long j2) throws IOException, IllegalArgumentException, IllegalStateException;

    public native void prepare() throws IOException, IllegalStateException;

    public native void prepareAsync() throws IllegalStateException;

    private native void _start() throws IllegalStateException;

    private native void _stop() throws IllegalStateException;

    private native void _pause() throws IllegalStateException;

    public native int getVideoWidth();

    public native int getVideoHeight();

    public native boolean isPlaying();

    public native void seekTo(int i) throws IllegalStateException;

    public native int getCurrentPosition();

    public native int getDuration();

    public native void setNextMediaPlayer(MediaPlayer mediaPlayer);

    private native void _release();

    private native void _reset();

    public native void setAudioStreamType(int i);

    public native void setLooping(boolean z);

    public native boolean isLooping();

    public native void setVolume(float f, float f2);

    public native void setAudioSessionId(int i) throws IllegalArgumentException, IllegalStateException;

    public native int getAudioSessionId();

    public native void attachAuxEffect(int i);

    public native void setAuxEffectSendLevel(float f);

    private final native int native_invoke(Parcel parcel, Parcel parcel2);

    private final native boolean native_getMetadata(boolean z, boolean z2, Parcel parcel);

    private final native int native_setMetadataFilter(Parcel parcel);

    private static final native void native_init();

    private final native void native_setup(Object obj);

    private final native void native_finalize();

    public static native int native_pullBatteryData(Parcel parcel);

    private final native int native_setRetransmitEndpoint(String str, int i);

    private native void updateProxyConfig(ProxyProperties proxyProperties);

    static {
        System.loadLibrary("media_jni");
        native_init();
    }

    public MediaPlayer() {
        Looper looper = Looper.myLooper();
        if (looper != null) {
            this.mEventHandler = new EventHandler(this, looper);
        } else {
            Looper looper2 = Looper.getMainLooper();
            if (looper2 != null) {
                this.mEventHandler = new EventHandler(this, looper2);
            } else {
                this.mEventHandler = null;
            }
        }
        this.mTimeProvider = new TimeProvider(this);
        this.mOutOfBandSubtitleTracks = new Vector<>();
        this.mOpenSubtitleSources = new Vector<>();
        this.mInbandSubtitleTracks = new SubtitleTrack[0];
        native_setup(new WeakReference(this));
    }

    public Parcel newRequest() {
        Parcel parcel = Parcel.obtain();
        parcel.writeInterfaceToken(IMEDIA_PLAYER);
        return parcel;
    }

    public void invoke(Parcel request, Parcel reply) {
        int retcode = native_invoke(request, reply);
        reply.setDataPosition(0);
        if (retcode != 0) {
            throw new RuntimeException("failure code: " + retcode);
        }
    }

    public void setDisplay(SurfaceHolder sh) {
        Surface surface;
        this.mSurfaceHolder = sh;
        if (sh != null) {
            surface = sh.getSurface();
        } else {
            surface = null;
        }
        _setVideoSurface(surface);
        updateSurfaceScreenOn();
    }

    public void setSurface(Surface surface) {
        if (this.mScreenOnWhilePlaying && surface != null) {
            Log.w(TAG, "setScreenOnWhilePlaying(true) is ineffective for Surface");
        }
        this.mSurfaceHolder = null;
        _setVideoSurface(surface);
        updateSurfaceScreenOn();
    }

    public void setVideoScalingMode(int mode) {
        if (!isVideoScalingModeSupported(mode)) {
            String msg = "Scaling mode " + mode + " is not supported";
            throw new IllegalArgumentException(msg);
        }
        Parcel request = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            request.writeInterfaceToken(IMEDIA_PLAYER);
            request.writeInt(6);
            request.writeInt(mode);
            invoke(request, reply);
            request.recycle();
            reply.recycle();
        } catch (Throwable th) {
            request.recycle();
            reply.recycle();
            throw th;
        }
    }

    public static MediaPlayer create(Context context, Uri uri) {
        return create(context, uri, null);
    }

    public static MediaPlayer create(Context context, Uri uri, SurfaceHolder holder) {
        try {
            MediaPlayer mp = new MediaPlayer();
            mp.setDataSource(context, uri);
            if (holder != null) {
                mp.setDisplay(holder);
            }
            mp.prepare();
            return mp;
        } catch (IOException ex) {
            Log.d(TAG, "create failed:", ex);
            return null;
        } catch (IllegalArgumentException ex2) {
            Log.d(TAG, "create failed:", ex2);
            return null;
        } catch (SecurityException ex3) {
            Log.d(TAG, "create failed:", ex3);
            return null;
        }
    }

    public static MediaPlayer create(Context context, int resid) {
        try {
            AssetFileDescriptor afd = context.getResources().openRawResourceFd(resid);
            if (afd == null) {
                return null;
            }
            MediaPlayer mp = new MediaPlayer();
            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            mp.prepare();
            return mp;
        } catch (IOException ex) {
            Log.d(TAG, "create failed:", ex);
            return null;
        } catch (IllegalArgumentException ex2) {
            Log.d(TAG, "create failed:", ex2);
            return null;
        } catch (SecurityException ex3) {
            Log.d(TAG, "create failed:", ex3);
            return null;
        }
    }

    public void setDataSource(Context context, Uri uri) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        setDataSource(context, uri, (Map<String, String>) null);
    }

    public void setDataSource(Context context, Uri uri, Map<String, String> headers) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        disableProxyListener();
        String scheme = uri.getScheme();
        if (scheme == null || scheme.equals(ContentResolver.SCHEME_FILE)) {
            setDataSource(uri.getPath());
            return;
        }
        AssetFileDescriptor fd = null;
        try {
            ContentResolver resolver = context.getContentResolver();
            AssetFileDescriptor fd2 = resolver.openAssetFileDescriptor(uri, FullBackup.ROOT_TREE_TOKEN);
            if (fd2 == null) {
                if (fd2 != null) {
                    fd2.close();
                    return;
                }
                return;
            }
            if (fd2.getDeclaredLength() < 0) {
                setDataSource(fd2.getFileDescriptor());
            } else {
                setDataSource(fd2.getFileDescriptor(), fd2.getStartOffset(), fd2.getDeclaredLength());
            }
            if (fd2 != null) {
                fd2.close();
            }
        } catch (IOException e) {
            if (0 != 0) {
                fd.close();
            }
            Log.d(TAG, "Couldn't open file on client side, trying server side");
            setDataSource(uri.toString(), headers);
            if (!scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https")) {
                setupProxyListener(context);
            }
        } catch (SecurityException e2) {
            if (0 != 0) {
                fd.close();
            }
            Log.d(TAG, "Couldn't open file on client side, trying server side");
            setDataSource(uri.toString(), headers);
            if (scheme.equalsIgnoreCase("http")) {
            }
            setupProxyListener(context);
        } catch (Throwable th) {
            if (0 != 0) {
                fd.close();
            }
            throw th;
        }
    }

    public void setDataSource(String path) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        setDataSource(path, (String[]) null, (String[]) null);
    }

    public void setDataSource(String path, Map<String, String> headers) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        String[] keys = null;
        String[] values = null;
        if (headers != null) {
            keys = new String[headers.size()];
            values = new String[headers.size()];
            int i = 0;
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                keys[i] = entry.getKey();
                values[i] = entry.getValue();
                i++;
            }
        }
        setDataSource(path, keys, values);
    }

    private void setDataSource(String path, String[] keys, String[] values) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        disableProxyListener();
        Uri uri = Uri.parse(path);
        if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
            path = uri.getPath();
        }
        File file = new File(path);
        if (file.exists()) {
            FileInputStream is = new FileInputStream(file);
            FileDescriptor fd = is.getFD();
            setDataSource(fd);
            is.close();
            return;
        }
        _setDataSource(path, keys, values);
    }

    public void setDataSource(FileDescriptor fd) throws IOException, IllegalArgumentException, IllegalStateException {
        setDataSource(fd, 0L, 576460752303423487L);
    }

    public void setDataSource(FileDescriptor fd, long offset, long length) throws IOException, IllegalArgumentException, IllegalStateException {
        disableProxyListener();
        _setDataSource(fd, offset, length);
    }

    public void start() throws IllegalStateException {
        stayAwake(true);
        _start();
    }

    public void stop() throws IllegalStateException {
        stayAwake(false);
        _stop();
    }

    public void pause() throws IllegalStateException {
        stayAwake(false);
        _pause();
    }

    public void setWakeMode(Context context, int mode) {
        boolean washeld = false;
        if (this.mWakeLock != null) {
            if (this.mWakeLock.isHeld()) {
                washeld = true;
                this.mWakeLock.release();
            }
            this.mWakeLock = null;
        }
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(mode | 536870912, MediaPlayer.class.getName());
        this.mWakeLock.setReferenceCounted(false);
        if (washeld) {
            this.mWakeLock.acquire();
        }
    }

    public void setScreenOnWhilePlaying(boolean screenOn) {
        if (this.mScreenOnWhilePlaying != screenOn) {
            if (screenOn && this.mSurfaceHolder == null) {
                Log.w(TAG, "setScreenOnWhilePlaying(true) is ineffective without a SurfaceHolder");
            }
            this.mScreenOnWhilePlaying = screenOn;
            updateSurfaceScreenOn();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stayAwake(boolean awake) {
        if (this.mWakeLock != null) {
            if (awake && !this.mWakeLock.isHeld()) {
                this.mWakeLock.acquire();
            } else if (!awake && this.mWakeLock.isHeld()) {
                this.mWakeLock.release();
            }
        }
        this.mStayAwake = awake;
        updateSurfaceScreenOn();
    }

    private void updateSurfaceScreenOn() {
        if (this.mSurfaceHolder != null) {
            this.mSurfaceHolder.setKeepScreenOn(this.mScreenOnWhilePlaying && this.mStayAwake);
        }
    }

    public Metadata getMetadata(boolean update_only, boolean apply_filter) {
        Parcel reply = Parcel.obtain();
        Metadata data = new Metadata();
        if (!native_getMetadata(update_only, apply_filter, reply)) {
            reply.recycle();
            return null;
        } else if (!data.parse(reply)) {
            reply.recycle();
            return null;
        } else {
            return data;
        }
    }

    public int setMetadataFilter(Set<Integer> allow, Set<Integer> block) {
        Parcel request = newRequest();
        int capacity = request.dataSize() + (4 * (1 + allow.size() + 1 + block.size()));
        if (request.dataCapacity() < capacity) {
            request.setDataCapacity(capacity);
        }
        request.writeInt(allow.size());
        for (Integer t : allow) {
            request.writeInt(t.intValue());
        }
        request.writeInt(block.size());
        for (Integer t2 : block) {
            request.writeInt(t2.intValue());
        }
        return native_setMetadataFilter(request);
    }

    public void release() {
        stayAwake(false);
        updateSurfaceScreenOn();
        this.mOnPreparedListener = null;
        this.mOnBufferingUpdateListener = null;
        this.mOnCompletionListener = null;
        this.mOnSeekCompleteListener = null;
        this.mOnErrorListener = null;
        this.mOnInfoListener = null;
        this.mOnVideoSizeChangedListener = null;
        this.mOnTimedTextListener = null;
        if (this.mTimeProvider != null) {
            this.mTimeProvider.close();
            this.mTimeProvider = null;
        }
        this.mOnSubtitleDataListener = null;
        _release();
    }

    public void reset() {
        this.mSelectedSubtitleTrackIndex = -1;
        synchronized (this.mOpenSubtitleSources) {
            Iterator i$ = this.mOpenSubtitleSources.iterator();
            while (i$.hasNext()) {
                InputStream is = i$.next();
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
            this.mOpenSubtitleSources.clear();
        }
        this.mOutOfBandSubtitleTracks.clear();
        this.mInbandSubtitleTracks = new SubtitleTrack[0];
        if (this.mSubtitleController != null) {
            this.mSubtitleController.reset();
        }
        if (this.mTimeProvider != null) {
            this.mTimeProvider.close();
            this.mTimeProvider = null;
        }
        stayAwake(false);
        _reset();
        if (this.mEventHandler != null) {
            this.mEventHandler.removeCallbacksAndMessages(null);
        }
        disableProxyListener();
    }

    public void setVolume(float volume) {
        setVolume(volume, volume);
    }

    /* loaded from: MediaPlayer$TrackInfo.class */
    public static class TrackInfo implements Parcelable {
        public static final int MEDIA_TRACK_TYPE_UNKNOWN = 0;
        public static final int MEDIA_TRACK_TYPE_VIDEO = 1;
        public static final int MEDIA_TRACK_TYPE_AUDIO = 2;
        public static final int MEDIA_TRACK_TYPE_TIMEDTEXT = 3;
        public static final int MEDIA_TRACK_TYPE_SUBTITLE = 4;
        final int mTrackType;
        final MediaFormat mFormat;
        static final Parcelable.Creator<TrackInfo> CREATOR = new Parcelable.Creator<TrackInfo>() { // from class: android.media.MediaPlayer.TrackInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public TrackInfo createFromParcel(Parcel in) {
                return new TrackInfo(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public TrackInfo[] newArray(int size) {
                return new TrackInfo[size];
            }
        };

        public int getTrackType() {
            return this.mTrackType;
        }

        public String getLanguage() {
            String language = this.mFormat.getString("language");
            return language == null ? "und" : language;
        }

        public MediaFormat getFormat() {
            if (this.mTrackType == 3 || this.mTrackType == 4) {
                return this.mFormat;
            }
            return null;
        }

        TrackInfo(Parcel in) {
            this.mTrackType = in.readInt();
            String language = in.readString();
            if (this.mTrackType == 3) {
                this.mFormat = MediaFormat.createSubtitleFormat(MediaPlayer.MEDIA_MIMETYPE_TEXT_SUBRIP, language);
            } else if (this.mTrackType == 4) {
                this.mFormat = MediaFormat.createSubtitleFormat(MediaPlayer.MEDIA_MIMETYPE_TEXT_VTT, language);
                this.mFormat.setInteger(MediaFormat.KEY_IS_AUTOSELECT, in.readInt());
                this.mFormat.setInteger(MediaFormat.KEY_IS_DEFAULT, in.readInt());
                this.mFormat.setInteger(MediaFormat.KEY_IS_FORCED_SUBTITLE, in.readInt());
            } else {
                this.mFormat = new MediaFormat();
                this.mFormat.setString("language", language);
            }
        }

        TrackInfo(int type, MediaFormat format) {
            this.mTrackType = type;
            this.mFormat = format;
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.mTrackType);
            dest.writeString(getLanguage());
            if (this.mTrackType == 4) {
                dest.writeInt(this.mFormat.getInteger(MediaFormat.KEY_IS_AUTOSELECT));
                dest.writeInt(this.mFormat.getInteger(MediaFormat.KEY_IS_DEFAULT));
                dest.writeInt(this.mFormat.getInteger(MediaFormat.KEY_IS_FORCED_SUBTITLE));
            }
        }
    }

    public TrackInfo[] getTrackInfo() throws IllegalStateException {
        TrackInfo[] trackInfo = getInbandTrackInfo();
        TrackInfo[] allTrackInfo = new TrackInfo[trackInfo.length + this.mOutOfBandSubtitleTracks.size()];
        System.arraycopy(trackInfo, 0, allTrackInfo, 0, trackInfo.length);
        int i = trackInfo.length;
        Iterator i$ = this.mOutOfBandSubtitleTracks.iterator();
        while (i$.hasNext()) {
            SubtitleTrack track = i$.next();
            allTrackInfo[i] = new TrackInfo(4, track.getFormat());
            i++;
        }
        return allTrackInfo;
    }

    private TrackInfo[] getInbandTrackInfo() throws IllegalStateException {
        Parcel request = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            request.writeInterfaceToken(IMEDIA_PLAYER);
            request.writeInt(1);
            invoke(request, reply);
            TrackInfo[] trackInfo = (TrackInfo[]) reply.createTypedArray(TrackInfo.CREATOR);
            request.recycle();
            reply.recycle();
            return trackInfo;
        } catch (Throwable th) {
            request.recycle();
            reply.recycle();
            throw th;
        }
    }

    private static boolean availableMimeTypeForExternalSource(String mimeType) {
        if (mimeType == MEDIA_MIMETYPE_TEXT_SUBRIP) {
            return true;
        }
        return false;
    }

    public void setSubtitleAnchor(SubtitleController controller, SubtitleController.Anchor anchor) {
        this.mSubtitleController = controller;
        this.mSubtitleController.setAnchor(anchor);
    }

    @Override // android.media.SubtitleController.Listener
    public void onSubtitleTrackSelected(SubtitleTrack track) {
        if (this.mSelectedSubtitleTrackIndex >= 0) {
            try {
                selectOrDeselectInbandTrack(this.mSelectedSubtitleTrackIndex, false);
            } catch (IllegalStateException e) {
            }
            this.mSelectedSubtitleTrackIndex = -1;
        }
        setOnSubtitleDataListener(null);
        if (track == null) {
            return;
        }
        for (int i = 0; i < this.mInbandSubtitleTracks.length; i++) {
            if (this.mInbandSubtitleTracks[i] == track) {
                Log.v(TAG, "Selecting subtitle track " + i);
                this.mSelectedSubtitleTrackIndex = i;
                try {
                    selectOrDeselectInbandTrack(this.mSelectedSubtitleTrackIndex, true);
                } catch (IllegalStateException e2) {
                }
                setOnSubtitleDataListener(this.mSubtitleDataListener);
                return;
            }
        }
    }

    public void addSubtitleSource(final InputStream is, final MediaFormat format) throws IllegalStateException {
        synchronized (this.mOpenSubtitleSources) {
            this.mOpenSubtitleSources.add(is);
        }
        final HandlerThread thread = new HandlerThread("SubtitleReadThread", 9);
        thread.start();
        Handler handler = new Handler(thread.getLooper());
        handler.post(new Runnable() { // from class: android.media.MediaPlayer.2
            private int addTrack() {
                if (is != null && MediaPlayer.this.mSubtitleController != null) {
                    SubtitleTrack track = MediaPlayer.this.mSubtitleController.addTrack(format);
                    if (track == null) {
                        return MediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE;
                    }
                    Scanner scanner = new Scanner(is, "UTF-8");
                    String contents = scanner.useDelimiter("\\A").next();
                    synchronized (MediaPlayer.this.mOpenSubtitleSources) {
                        MediaPlayer.this.mOpenSubtitleSources.remove(is);
                    }
                    scanner.close();
                    MediaPlayer.this.mOutOfBandSubtitleTracks.add(track);
                    track.onData(contents, true, -1L);
                    return MediaPlayer.MEDIA_INFO_EXTERNAL_METADATA_UPDATE;
                }
                return MediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE;
            }

            @Override // java.lang.Runnable
            public void run() {
                int res = addTrack();
                if (MediaPlayer.this.mEventHandler != null) {
                    Message m = MediaPlayer.this.mEventHandler.obtainMessage(200, res, 0, null);
                    MediaPlayer.this.mEventHandler.sendMessage(m);
                }
                thread.getLooper().quitSafely();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void scanInternalSubtitleTracks() {
        if (this.mSubtitleController == null) {
            Log.e(TAG, "Should have subtitle controller already set");
            return;
        }
        TrackInfo[] tracks = getInbandTrackInfo();
        SubtitleTrack[] inbandTracks = new SubtitleTrack[tracks.length];
        for (int i = 0; i < tracks.length; i++) {
            if (tracks[i].getTrackType() == 4) {
                if (i < this.mInbandSubtitleTracks.length) {
                    inbandTracks[i] = this.mInbandSubtitleTracks[i];
                } else {
                    SubtitleTrack track = this.mSubtitleController.addTrack(tracks[i].getFormat());
                    inbandTracks[i] = track;
                }
            }
        }
        this.mInbandSubtitleTracks = inbandTracks;
        this.mSubtitleController.selectDefaultTrack();
    }

    public void addTimedTextSource(String path, String mimeType) throws IOException, IllegalArgumentException, IllegalStateException {
        if (!availableMimeTypeForExternalSource(mimeType)) {
            String msg = "Illegal mimeType for timed text source: " + mimeType;
            throw new IllegalArgumentException(msg);
        }
        File file = new File(path);
        if (file.exists()) {
            FileInputStream is = new FileInputStream(file);
            FileDescriptor fd = is.getFD();
            addTimedTextSource(fd, mimeType);
            is.close();
            return;
        }
        throw new IOException(path);
    }

    public void addTimedTextSource(Context context, Uri uri, String mimeType) throws IOException, IllegalArgumentException, IllegalStateException {
        String scheme = uri.getScheme();
        if (scheme == null || scheme.equals(ContentResolver.SCHEME_FILE)) {
            addTimedTextSource(uri.getPath(), mimeType);
            return;
        }
        AssetFileDescriptor fd = null;
        try {
            ContentResolver resolver = context.getContentResolver();
            fd = resolver.openAssetFileDescriptor(uri, FullBackup.ROOT_TREE_TOKEN);
            if (fd == null) {
                if (fd != null) {
                    fd.close();
                    return;
                }
                return;
            }
            addTimedTextSource(fd.getFileDescriptor(), mimeType);
            if (fd != null) {
                fd.close();
            }
        } catch (IOException e) {
            if (fd != null) {
                fd.close();
            }
        } catch (SecurityException e2) {
            if (fd != null) {
                fd.close();
            }
        } catch (Throwable th) {
            if (fd != null) {
                fd.close();
            }
            throw th;
        }
    }

    public void addTimedTextSource(FileDescriptor fd, String mimeType) throws IllegalArgumentException, IllegalStateException {
        addTimedTextSource(fd, 0L, 576460752303423487L, mimeType);
    }

    public void addTimedTextSource(FileDescriptor fd, long offset, long length, String mimeType) throws IllegalArgumentException, IllegalStateException {
        if (!availableMimeTypeForExternalSource(mimeType)) {
            throw new IllegalArgumentException("Illegal mimeType for timed text source: " + mimeType);
        }
        Parcel request = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            request.writeInterfaceToken(IMEDIA_PLAYER);
            request.writeInt(3);
            request.writeFileDescriptor(fd);
            request.writeLong(offset);
            request.writeLong(length);
            request.writeString(mimeType);
            invoke(request, reply);
            request.recycle();
            reply.recycle();
        } catch (Throwable th) {
            request.recycle();
            reply.recycle();
            throw th;
        }
    }

    public void selectTrack(int index) throws IllegalStateException {
        selectOrDeselectTrack(index, true);
    }

    public void deselectTrack(int index) throws IllegalStateException {
        selectOrDeselectTrack(index, false);
    }

    private void selectOrDeselectTrack(int index, boolean select) throws IllegalStateException {
        SubtitleTrack track = null;
        if (index < this.mInbandSubtitleTracks.length) {
            track = this.mInbandSubtitleTracks[index];
        } else if (index < this.mInbandSubtitleTracks.length + this.mOutOfBandSubtitleTracks.size()) {
            track = this.mOutOfBandSubtitleTracks.get(index - this.mInbandSubtitleTracks.length);
        }
        if (this.mSubtitleController != null && track != null) {
            if (select) {
                this.mSubtitleController.selectTrack(track);
                return;
            } else if (this.mSubtitleController.getSelectedTrack() == track) {
                this.mSubtitleController.selectTrack(null);
                return;
            } else {
                Log.w(TAG, "trying to deselect track that was not selected");
                return;
            }
        }
        selectOrDeselectInbandTrack(index, select);
    }

    private void selectOrDeselectInbandTrack(int index, boolean select) throws IllegalStateException {
        Parcel request = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            request.writeInterfaceToken(IMEDIA_PLAYER);
            request.writeInt(select ? 4 : 5);
            request.writeInt(index);
            invoke(request, reply);
            request.recycle();
            reply.recycle();
        } catch (Throwable th) {
            request.recycle();
            reply.recycle();
            throw th;
        }
    }

    public void setRetransmitEndpoint(InetSocketAddress endpoint) throws IllegalStateException, IllegalArgumentException {
        String addrString = null;
        int port = 0;
        if (null != endpoint) {
            addrString = endpoint.getAddress().getHostAddress();
            port = endpoint.getPort();
        }
        int ret = native_setRetransmitEndpoint(addrString, port);
        if (ret != 0) {
            throw new IllegalArgumentException("Illegal re-transmit endpoint; native ret " + ret);
        }
    }

    protected void finalize() {
        native_finalize();
    }

    public MediaTimeProvider getMediaTimeProvider() {
        if (this.mTimeProvider == null) {
            this.mTimeProvider = new TimeProvider(this);
        }
        return this.mTimeProvider;
    }

    /* loaded from: MediaPlayer$EventHandler.class */
    private class EventHandler extends Handler {
        private MediaPlayer mMediaPlayer;

        public EventHandler(MediaPlayer mp, Looper looper) {
            super(looper);
            this.mMediaPlayer = mp;
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            if (this.mMediaPlayer.mNativeContext == 0) {
                Log.w(MediaPlayer.TAG, "mediaplayer went away with unhandled events");
                return;
            }
            switch (msg.what) {
                case 0:
                    return;
                case 1:
                    MediaPlayer.this.scanInternalSubtitleTracks();
                    if (MediaPlayer.this.mOnPreparedListener != null) {
                        MediaPlayer.this.mOnPreparedListener.onPrepared(this.mMediaPlayer);
                        return;
                    }
                    return;
                case 2:
                    if (MediaPlayer.this.mOnCompletionListener != null) {
                        MediaPlayer.this.mOnCompletionListener.onCompletion(this.mMediaPlayer);
                    }
                    MediaPlayer.this.stayAwake(false);
                    return;
                case 3:
                    if (MediaPlayer.this.mOnBufferingUpdateListener != null) {
                        MediaPlayer.this.mOnBufferingUpdateListener.onBufferingUpdate(this.mMediaPlayer, msg.arg1);
                        return;
                    }
                    return;
                case 4:
                    if (MediaPlayer.this.mOnSeekCompleteListener != null) {
                        MediaPlayer.this.mOnSeekCompleteListener.onSeekComplete(this.mMediaPlayer);
                        break;
                    }
                    break;
                case 5:
                    if (MediaPlayer.this.mOnVideoSizeChangedListener != null) {
                        MediaPlayer.this.mOnVideoSizeChangedListener.onVideoSizeChanged(this.mMediaPlayer, msg.arg1, msg.arg2);
                        return;
                    }
                    return;
                case 6:
                case 7:
                    if (MediaPlayer.this.mTimeProvider != null) {
                        MediaPlayer.this.mTimeProvider.onPaused(msg.what == 7);
                        return;
                    }
                    return;
                case 8:
                    if (MediaPlayer.this.mTimeProvider != null) {
                        MediaPlayer.this.mTimeProvider.onStopped();
                        return;
                    }
                    return;
                case 9:
                    break;
                case 99:
                    if (MediaPlayer.this.mOnTimedTextListener == null) {
                        return;
                    }
                    if (msg.obj == null) {
                        MediaPlayer.this.mOnTimedTextListener.onTimedText(this.mMediaPlayer, null);
                        return;
                    } else if (msg.obj instanceof Parcel) {
                        Parcel parcel = (Parcel) msg.obj;
                        TimedText text = new TimedText(parcel);
                        parcel.recycle();
                        MediaPlayer.this.mOnTimedTextListener.onTimedText(this.mMediaPlayer, text);
                        return;
                    } else {
                        return;
                    }
                case 100:
                    Log.e(MediaPlayer.TAG, "Error (" + msg.arg1 + Separators.COMMA + msg.arg2 + Separators.RPAREN);
                    boolean error_was_handled = false;
                    if (MediaPlayer.this.mOnErrorListener != null) {
                        error_was_handled = MediaPlayer.this.mOnErrorListener.onError(this.mMediaPlayer, msg.arg1, msg.arg2);
                    }
                    if (MediaPlayer.this.mOnCompletionListener != null && !error_was_handled) {
                        MediaPlayer.this.mOnCompletionListener.onCompletion(this.mMediaPlayer);
                    }
                    MediaPlayer.this.stayAwake(false);
                    return;
                case 200:
                    switch (msg.arg1) {
                        case 700:
                            Log.i(MediaPlayer.TAG, "Info (" + msg.arg1 + Separators.COMMA + msg.arg2 + Separators.RPAREN);
                            break;
                        case MediaPlayer.MEDIA_INFO_METADATA_UPDATE /* 802 */:
                            MediaPlayer.this.scanInternalSubtitleTracks();
                        case MediaPlayer.MEDIA_INFO_EXTERNAL_METADATA_UPDATE /* 803 */:
                            msg.arg1 = MediaPlayer.MEDIA_INFO_METADATA_UPDATE;
                            MediaPlayer.this.mSubtitleController.selectDefaultTrack();
                            break;
                    }
                    if (MediaPlayer.this.mOnInfoListener != null) {
                        MediaPlayer.this.mOnInfoListener.onInfo(this.mMediaPlayer, msg.arg1, msg.arg2);
                        return;
                    }
                    return;
                case 201:
                    if (MediaPlayer.this.mOnSubtitleDataListener != null && (msg.obj instanceof Parcel)) {
                        Parcel parcel2 = (Parcel) msg.obj;
                        SubtitleData data = new SubtitleData(parcel2);
                        parcel2.recycle();
                        MediaPlayer.this.mOnSubtitleDataListener.onSubtitleData(this.mMediaPlayer, data);
                        return;
                    }
                    return;
                default:
                    Log.e(MediaPlayer.TAG, "Unknown message type " + msg.what);
                    return;
            }
            if (MediaPlayer.this.mTimeProvider != null) {
                MediaPlayer.this.mTimeProvider.onSeekComplete(this.mMediaPlayer);
            }
        }
    }

    private static void postEventFromNative(Object mediaplayer_ref, int what, int arg1, int arg2, Object obj) {
        MediaPlayer mp = (MediaPlayer) ((WeakReference) mediaplayer_ref).get();
        if (mp == null) {
            return;
        }
        if (what == 200 && arg1 == 2) {
            mp.start();
        }
        if (mp.mEventHandler != null) {
            Message m = mp.mEventHandler.obtainMessage(what, arg1, arg2, obj);
            mp.mEventHandler.sendMessage(m);
        }
    }

    public void setOnPreparedListener(OnPreparedListener listener) {
        this.mOnPreparedListener = listener;
    }

    public void setOnCompletionListener(OnCompletionListener listener) {
        this.mOnCompletionListener = listener;
    }

    public void setOnBufferingUpdateListener(OnBufferingUpdateListener listener) {
        this.mOnBufferingUpdateListener = listener;
    }

    public void setOnSeekCompleteListener(OnSeekCompleteListener listener) {
        this.mOnSeekCompleteListener = listener;
    }

    public void setOnVideoSizeChangedListener(OnVideoSizeChangedListener listener) {
        this.mOnVideoSizeChangedListener = listener;
    }

    public void setOnTimedTextListener(OnTimedTextListener listener) {
        this.mOnTimedTextListener = listener;
    }

    public void setOnSubtitleDataListener(OnSubtitleDataListener listener) {
        this.mOnSubtitleDataListener = listener;
    }

    public void setOnErrorListener(OnErrorListener listener) {
        this.mOnErrorListener = listener;
    }

    public void setOnInfoListener(OnInfoListener listener) {
        this.mOnInfoListener = listener;
    }

    private boolean isVideoScalingModeSupported(int mode) {
        return mode == 1 || mode == 2;
    }

    private void setupProxyListener(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Proxy.PROXY_CHANGE_ACTION);
        this.mProxyReceiver = new ProxyReceiver();
        this.mProxyContext = context;
        Intent currentProxy = context.getApplicationContext().registerReceiver(this.mProxyReceiver, filter);
        if (currentProxy != null) {
            handleProxyBroadcast(currentProxy);
        }
    }

    private void disableProxyListener() {
        if (this.mProxyReceiver == null) {
            return;
        }
        Context appContext = this.mProxyContext.getApplicationContext();
        if (appContext != null) {
            appContext.unregisterReceiver(this.mProxyReceiver);
        }
        this.mProxyReceiver = null;
        this.mProxyContext = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleProxyBroadcast(Intent intent) {
        ProxyProperties props = (ProxyProperties) intent.getExtra("proxy");
        if (props == null || props.getHost() == null) {
            updateProxyConfig(null);
        } else {
            updateProxyConfig(props);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: MediaPlayer$ProxyReceiver.class */
    public class ProxyReceiver extends BroadcastReceiver {
        private ProxyReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Proxy.PROXY_CHANGE_ACTION)) {
                MediaPlayer.this.handleProxyBroadcast(intent);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: MediaPlayer$TimeProvider.class */
    public static class TimeProvider implements OnSeekCompleteListener, MediaTimeProvider {
        private static final String TAG = "MTP";
        private static final long MAX_NS_WITHOUT_POSITION_CHECK = 5000000000L;
        private static final long MAX_EARLY_CALLBACK_US = 1000;
        private static final long TIME_ADJUSTMENT_RATE = 2;
        private long mLastTimeUs;
        private MediaPlayer mPlayer;
        private long mLastReportedTime;
        private long mTimeAdjustment;
        private MediaTimeProvider.OnMediaTimeListener[] mListeners;
        private long[] mTimes;
        private long mLastNanoTime;
        private Handler mEventHandler;
        private boolean mRefresh;
        private static final int NOTIFY = 1;
        private static final int NOTIFY_TIME = 0;
        private static final int REFRESH_AND_NOTIFY_TIME = 1;
        private static final int NOTIFY_STOP = 2;
        private static final int NOTIFY_SEEK = 3;
        private HandlerThread mHandlerThread;
        private boolean mPaused = true;
        private boolean mStopped = true;
        private boolean mPausing = false;
        private boolean mSeeking = false;
        public boolean DEBUG = false;

        public TimeProvider(MediaPlayer mp) {
            this.mLastTimeUs = 0L;
            this.mRefresh = false;
            this.mPlayer = mp;
            try {
                getCurrentTimeUs(true, false);
            } catch (IllegalStateException e) {
                this.mRefresh = true;
            }
            Looper myLooper = Looper.myLooper();
            Looper looper = myLooper;
            if (myLooper == null) {
                Looper mainLooper = Looper.getMainLooper();
                looper = mainLooper;
                if (mainLooper == null) {
                    this.mHandlerThread = new HandlerThread("MediaPlayerMTPEventThread", -2);
                    this.mHandlerThread.start();
                    looper = this.mHandlerThread.getLooper();
                }
            }
            this.mEventHandler = new EventHandler(looper);
            this.mListeners = new MediaTimeProvider.OnMediaTimeListener[0];
            this.mTimes = new long[0];
            this.mLastTimeUs = 0L;
            this.mTimeAdjustment = 0L;
        }

        private void scheduleNotification(int type, long delayUs) {
            if (this.mSeeking && (type == 0 || type == 1)) {
                return;
            }
            if (this.DEBUG) {
                Log.v(TAG, "scheduleNotification " + type + " in " + delayUs);
            }
            this.mStopped = type == 2;
            this.mSeeking = type == 3;
            this.mEventHandler.removeMessages(1);
            Message msg = this.mEventHandler.obtainMessage(1, type, 0);
            this.mEventHandler.sendMessageDelayed(msg, (int) (delayUs / 1000));
        }

        public void close() {
            this.mEventHandler.removeMessages(1);
            if (this.mHandlerThread != null) {
                this.mHandlerThread.quitSafely();
                this.mHandlerThread = null;
            }
        }

        protected void finalize() {
            if (this.mHandlerThread != null) {
                this.mHandlerThread.quitSafely();
            }
        }

        public void onPaused(boolean paused) {
            synchronized (this) {
                if (this.DEBUG) {
                    Log.d(TAG, "onPaused: " + paused);
                }
                if (this.mStopped) {
                    scheduleNotification(3, 0L);
                } else {
                    this.mPausing = paused;
                    scheduleNotification(1, 0L);
                }
            }
        }

        public void onStopped() {
            synchronized (this) {
                if (this.DEBUG) {
                    Log.d(TAG, "onStopped");
                }
                this.mPaused = true;
                scheduleNotification(2, 0L);
            }
        }

        @Override // android.media.MediaPlayer.OnSeekCompleteListener
        public void onSeekComplete(MediaPlayer mp) {
            synchronized (this) {
                scheduleNotification(3, 0L);
            }
        }

        public void onNewPlayer() {
            if (this.mRefresh) {
                synchronized (this) {
                    scheduleNotification(3, 0L);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public synchronized void notifySeek() {
            this.mSeeking = false;
            try {
                long timeUs = getCurrentTimeUs(true, false);
                if (this.DEBUG) {
                    Log.d(TAG, "onSeekComplete at " + timeUs);
                }
                MediaTimeProvider.OnMediaTimeListener[] arr$ = this.mListeners;
                for (MediaTimeProvider.OnMediaTimeListener listener : arr$) {
                    if (listener == null) {
                        break;
                    }
                    listener.onSeek(timeUs);
                }
            } catch (IllegalStateException e) {
                if (this.DEBUG) {
                    Log.d(TAG, "onSeekComplete but no player");
                }
                this.mPausing = true;
                notifyTimedEvent(false);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public synchronized void notifyStop() {
            MediaTimeProvider.OnMediaTimeListener listener;
            MediaTimeProvider.OnMediaTimeListener[] arr$ = this.mListeners;
            int len$ = arr$.length;
            for (int i$ = 0; i$ < len$ && (listener = arr$[i$]) != null; i$++) {
                listener.onStop();
            }
        }

        private int registerListener(MediaTimeProvider.OnMediaTimeListener listener) {
            int i = 0;
            while (i < this.mListeners.length && this.mListeners[i] != listener && this.mListeners[i] != null) {
                i++;
            }
            if (i >= this.mListeners.length) {
                MediaTimeProvider.OnMediaTimeListener[] newListeners = new MediaTimeProvider.OnMediaTimeListener[i + 1];
                long[] newTimes = new long[i + 1];
                System.arraycopy(this.mListeners, 0, newListeners, 0, this.mListeners.length);
                System.arraycopy(this.mTimes, 0, newTimes, 0, this.mTimes.length);
                this.mListeners = newListeners;
                this.mTimes = newTimes;
            }
            if (this.mListeners[i] == null) {
                this.mListeners[i] = listener;
                this.mTimes[i] = -1;
            }
            return i;
        }

        @Override // android.media.MediaTimeProvider
        public void notifyAt(long timeUs, MediaTimeProvider.OnMediaTimeListener listener) {
            synchronized (this) {
                if (this.DEBUG) {
                    Log.d(TAG, "notifyAt " + timeUs);
                }
                this.mTimes[registerListener(listener)] = timeUs;
                scheduleNotification(0, 0L);
            }
        }

        @Override // android.media.MediaTimeProvider
        public void scheduleUpdate(MediaTimeProvider.OnMediaTimeListener listener) {
            synchronized (this) {
                if (this.DEBUG) {
                    Log.d(TAG, "scheduleUpdate");
                }
                int i = registerListener(listener);
                if (this.mStopped) {
                    scheduleNotification(2, 0L);
                } else {
                    this.mTimes[i] = 0;
                    scheduleNotification(0, 0L);
                }
            }
        }

        @Override // android.media.MediaTimeProvider
        public void cancelNotifications(MediaTimeProvider.OnMediaTimeListener listener) {
            synchronized (this) {
                int i = 0;
                while (true) {
                    if (i >= this.mListeners.length) {
                        break;
                    } else if (this.mListeners[i] == listener) {
                        System.arraycopy(this.mListeners, i + 1, this.mListeners, i, (this.mListeners.length - i) - 1);
                        System.arraycopy(this.mTimes, i + 1, this.mTimes, i, (this.mTimes.length - i) - 1);
                        this.mListeners[this.mListeners.length - 1] = null;
                        this.mTimes[this.mTimes.length - 1] = -1;
                        break;
                    } else if (this.mListeners[i] == null) {
                        break;
                    } else {
                        i++;
                    }
                }
                scheduleNotification(0, 0L);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public synchronized void notifyTimedEvent(boolean refreshTime) {
            long nowUs;
            try {
                nowUs = getCurrentTimeUs(refreshTime, true);
            } catch (IllegalStateException e) {
                this.mRefresh = true;
                this.mPausing = true;
                nowUs = getCurrentTimeUs(refreshTime, true);
            }
            long nextTimeUs = nowUs;
            if (this.mSeeking) {
                return;
            }
            if (this.DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("notifyTimedEvent(").append(this.mLastTimeUs).append(" -> ").append(nowUs).append(") from {");
                boolean first = true;
                long[] arr$ = this.mTimes;
                for (long time : arr$) {
                    if (time != -1) {
                        if (!first) {
                            sb.append(", ");
                        }
                        sb.append(time);
                        first = false;
                    }
                }
                sb.append("}");
                Log.d(TAG, sb.toString());
            }
            Vector<MediaTimeProvider.OnMediaTimeListener> activatedListeners = new Vector<>();
            for (int ix = 0; ix < this.mTimes.length && this.mListeners[ix] != null; ix++) {
                if (this.mTimes[ix] > -1) {
                    if (this.mTimes[ix] <= nowUs + 1000) {
                        activatedListeners.add(this.mListeners[ix]);
                        if (this.DEBUG) {
                            Log.d(TAG, Environment.MEDIA_REMOVED);
                        }
                        this.mTimes[ix] = -1;
                    } else if (nextTimeUs == nowUs || this.mTimes[ix] < nextTimeUs) {
                        nextTimeUs = this.mTimes[ix];
                    }
                }
            }
            if (nextTimeUs > nowUs && !this.mPaused) {
                if (this.DEBUG) {
                    Log.d(TAG, "scheduling for " + nextTimeUs + " and " + nowUs);
                }
                scheduleNotification(0, nextTimeUs - nowUs);
            } else {
                this.mEventHandler.removeMessages(1);
            }
            Iterator i$ = activatedListeners.iterator();
            while (i$.hasNext()) {
                MediaTimeProvider.OnMediaTimeListener listener = i$.next();
                listener.onTimedEvent(nowUs);
            }
        }

        private long getEstimatedTime(long nanoTime, boolean monotonic) {
            if (this.mPaused) {
                this.mLastReportedTime = this.mLastTimeUs + this.mTimeAdjustment;
            } else {
                long timeSinceRead = (nanoTime - this.mLastNanoTime) / 1000;
                this.mLastReportedTime = this.mLastTimeUs + timeSinceRead;
                if (this.mTimeAdjustment > 0) {
                    long adjustment = this.mTimeAdjustment - (timeSinceRead / 2);
                    if (adjustment <= 0) {
                        this.mTimeAdjustment = 0L;
                    } else {
                        this.mLastReportedTime += adjustment;
                    }
                }
            }
            return this.mLastReportedTime;
        }

        @Override // android.media.MediaTimeProvider
        public long getCurrentTimeUs(boolean refreshTime, boolean monotonic) throws IllegalStateException {
            synchronized (this) {
                if (this.mPaused && !refreshTime) {
                    return this.mLastReportedTime;
                }
                long nanoTime = System.nanoTime();
                if (refreshTime || nanoTime >= this.mLastNanoTime + MAX_NS_WITHOUT_POSITION_CHECK) {
                    try {
                        this.mLastTimeUs = this.mPlayer.getCurrentPosition() * 1000;
                        this.mPaused = !this.mPlayer.isPlaying();
                        if (this.DEBUG) {
                            Log.v(TAG, (this.mPaused ? "paused" : "playing") + " at " + this.mLastTimeUs);
                        }
                        this.mLastNanoTime = nanoTime;
                        if (monotonic && this.mLastTimeUs < this.mLastReportedTime) {
                            this.mTimeAdjustment = this.mLastReportedTime - this.mLastTimeUs;
                            if (this.mTimeAdjustment > 1000000) {
                                scheduleNotification(3, 0L);
                            }
                        } else {
                            this.mTimeAdjustment = 0L;
                        }
                    } catch (IllegalStateException e) {
                        if (this.mPausing) {
                            this.mPausing = false;
                            getEstimatedTime(nanoTime, monotonic);
                            this.mPaused = true;
                            if (this.DEBUG) {
                                Log.d(TAG, "illegal state, but pausing: estimating at " + this.mLastReportedTime);
                            }
                            return this.mLastReportedTime;
                        }
                        throw e;
                    }
                }
                return getEstimatedTime(nanoTime, monotonic);
            }
        }

        /* loaded from: MediaPlayer$TimeProvider$EventHandler.class */
        private class EventHandler extends Handler {
            public EventHandler(Looper looper) {
                super(looper);
            }

            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    switch (msg.arg1) {
                        case 0:
                            TimeProvider.this.notifyTimedEvent(false);
                            return;
                        case 1:
                            TimeProvider.this.notifyTimedEvent(true);
                            return;
                        case 2:
                            TimeProvider.this.notifyStop();
                            return;
                        case 3:
                            TimeProvider.this.notifySeek();
                            return;
                        default:
                            return;
                    }
                }
            }
        }
    }
}