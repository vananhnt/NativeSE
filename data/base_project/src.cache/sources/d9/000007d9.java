package android.media;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.SystemProperties;
import android.util.AndroidRuntimeException;
import android.util.Log;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.ref.WeakReference;

/* loaded from: SoundPool.class */
public class SoundPool {
    private final SoundPoolDelegate mImpl;

    /* loaded from: SoundPool$OnLoadCompleteListener.class */
    public interface OnLoadCompleteListener {
        void onLoadComplete(SoundPool soundPool, int i, int i2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: SoundPool$SoundPoolDelegate.class */
    public interface SoundPoolDelegate {
        int load(String str, int i);

        int load(Context context, int i, int i2);

        int load(AssetFileDescriptor assetFileDescriptor, int i);

        int load(FileDescriptor fileDescriptor, long j, long j2, int i);

        boolean unload(int i);

        int play(int i, float f, float f2, int i2, int i3, float f3);

        void pause(int i);

        void resume(int i);

        void autoPause();

        void autoResume();

        void stop(int i);

        void setVolume(int i, float f, float f2);

        void setVolume(int i, float f);

        void setPriority(int i, int i2);

        void setLoop(int i, int i2);

        void setRate(int i, float f);

        void setOnLoadCompleteListener(OnLoadCompleteListener onLoadCompleteListener);

        void release();
    }

    public SoundPool(int maxStreams, int streamType, int srcQuality) {
        if (SystemProperties.getBoolean("config.disable_media", false)) {
            this.mImpl = new SoundPoolStub();
        } else {
            this.mImpl = new SoundPoolImpl(this, maxStreams, streamType, srcQuality);
        }
    }

    public int load(String path, int priority) {
        return this.mImpl.load(path, priority);
    }

    public int load(Context context, int resId, int priority) {
        return this.mImpl.load(context, resId, priority);
    }

    public int load(AssetFileDescriptor afd, int priority) {
        return this.mImpl.load(afd, priority);
    }

    public int load(FileDescriptor fd, long offset, long length, int priority) {
        return this.mImpl.load(fd, offset, length, priority);
    }

    public final boolean unload(int soundID) {
        return this.mImpl.unload(soundID);
    }

    public final int play(int soundID, float leftVolume, float rightVolume, int priority, int loop, float rate) {
        return this.mImpl.play(soundID, leftVolume, rightVolume, priority, loop, rate);
    }

    public final void pause(int streamID) {
        this.mImpl.pause(streamID);
    }

    public final void resume(int streamID) {
        this.mImpl.resume(streamID);
    }

    public final void autoPause() {
        this.mImpl.autoPause();
    }

    public final void autoResume() {
        this.mImpl.autoResume();
    }

    public final void stop(int streamID) {
        this.mImpl.stop(streamID);
    }

    public final void setVolume(int streamID, float leftVolume, float rightVolume) {
        this.mImpl.setVolume(streamID, leftVolume, rightVolume);
    }

    public void setVolume(int streamID, float volume) {
        setVolume(streamID, volume, volume);
    }

    public final void setPriority(int streamID, int priority) {
        this.mImpl.setPriority(streamID, priority);
    }

    public final void setLoop(int streamID, int loop) {
        this.mImpl.setLoop(streamID, loop);
    }

    public final void setRate(int streamID, float rate) {
        this.mImpl.setRate(streamID, rate);
    }

    public void setOnLoadCompleteListener(OnLoadCompleteListener listener) {
        this.mImpl.setOnLoadCompleteListener(listener);
    }

    public final void release() {
        this.mImpl.release();
    }

    /* loaded from: SoundPool$SoundPoolImpl.class */
    static class SoundPoolImpl implements SoundPoolDelegate {
        private static final String TAG = "SoundPool";
        private static final boolean DEBUG = false;
        private int mNativeContext;
        private EventHandler mEventHandler;
        private OnLoadCompleteListener mOnLoadCompleteListener;
        private SoundPool mProxy;
        private final Object mLock;
        private static final int SAMPLE_LOADED = 1;

        private final native int _load(String str, int i);

        private final native int _load(FileDescriptor fileDescriptor, long j, long j2, int i);

        @Override // android.media.SoundPool.SoundPoolDelegate
        public final native boolean unload(int i);

        @Override // android.media.SoundPool.SoundPoolDelegate
        public final native int play(int i, float f, float f2, int i2, int i3, float f3);

        @Override // android.media.SoundPool.SoundPoolDelegate
        public final native void pause(int i);

        @Override // android.media.SoundPool.SoundPoolDelegate
        public final native void resume(int i);

        @Override // android.media.SoundPool.SoundPoolDelegate
        public final native void autoPause();

        @Override // android.media.SoundPool.SoundPoolDelegate
        public final native void autoResume();

        @Override // android.media.SoundPool.SoundPoolDelegate
        public final native void stop(int i);

        @Override // android.media.SoundPool.SoundPoolDelegate
        public final native void setVolume(int i, float f, float f2);

        @Override // android.media.SoundPool.SoundPoolDelegate
        public final native void setPriority(int i, int i2);

        @Override // android.media.SoundPool.SoundPoolDelegate
        public final native void setLoop(int i, int i2);

        @Override // android.media.SoundPool.SoundPoolDelegate
        public final native void setRate(int i, float f);

        @Override // android.media.SoundPool.SoundPoolDelegate
        public final native void release();

        private final native int native_setup(Object obj, int i, int i2, int i3);

        static {
            System.loadLibrary("soundpool");
        }

        public SoundPoolImpl(SoundPool proxy, int maxStreams, int streamType, int srcQuality) {
            if (native_setup(new WeakReference(this), maxStreams, streamType, srcQuality) != 0) {
                throw new RuntimeException("Native setup failed");
            }
            this.mLock = new Object();
            this.mProxy = proxy;
        }

        @Override // android.media.SoundPool.SoundPoolDelegate
        public int load(String path, int priority) {
            if (path.startsWith("http:")) {
                return _load(path, priority);
            }
            int id = 0;
            try {
                File f = new File(path);
                ParcelFileDescriptor fd = ParcelFileDescriptor.open(f, 268435456);
                if (fd != null) {
                    id = _load(fd.getFileDescriptor(), 0L, f.length(), priority);
                    fd.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "error loading " + path);
            }
            return id;
        }

        @Override // android.media.SoundPool.SoundPoolDelegate
        public int load(Context context, int resId, int priority) {
            AssetFileDescriptor afd = context.getResources().openRawResourceFd(resId);
            int id = 0;
            if (afd != null) {
                id = _load(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength(), priority);
                try {
                    afd.close();
                } catch (IOException e) {
                }
            }
            return id;
        }

        @Override // android.media.SoundPool.SoundPoolDelegate
        public int load(AssetFileDescriptor afd, int priority) {
            if (afd != null) {
                long len = afd.getLength();
                if (len < 0) {
                    throw new AndroidRuntimeException("no length for fd");
                }
                return _load(afd.getFileDescriptor(), afd.getStartOffset(), len, priority);
            }
            return 0;
        }

        @Override // android.media.SoundPool.SoundPoolDelegate
        public int load(FileDescriptor fd, long offset, long length, int priority) {
            return _load(fd, offset, length, priority);
        }

        @Override // android.media.SoundPool.SoundPoolDelegate
        public void setVolume(int streamID, float volume) {
            setVolume(streamID, volume, volume);
        }

        @Override // android.media.SoundPool.SoundPoolDelegate
        public void setOnLoadCompleteListener(OnLoadCompleteListener listener) {
            synchronized (this.mLock) {
                if (listener != null) {
                    Looper looper = Looper.myLooper();
                    if (looper != null) {
                        this.mEventHandler = new EventHandler(this.mProxy, looper);
                    } else {
                        Looper looper2 = Looper.getMainLooper();
                        if (looper2 != null) {
                            this.mEventHandler = new EventHandler(this.mProxy, looper2);
                        } else {
                            this.mEventHandler = null;
                        }
                    }
                } else {
                    this.mEventHandler = null;
                }
                this.mOnLoadCompleteListener = listener;
            }
        }

        /* loaded from: SoundPool$SoundPoolImpl$EventHandler.class */
        private class EventHandler extends Handler {
            private SoundPool mSoundPool;

            public EventHandler(SoundPool soundPool, Looper looper) {
                super(looper);
                this.mSoundPool = soundPool;
            }

            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        synchronized (SoundPoolImpl.this.mLock) {
                            if (SoundPoolImpl.this.mOnLoadCompleteListener != null) {
                                SoundPoolImpl.this.mOnLoadCompleteListener.onLoadComplete(this.mSoundPool, msg.arg1, msg.arg2);
                            }
                        }
                        return;
                    default:
                        Log.e(SoundPoolImpl.TAG, "Unknown message type " + msg.what);
                        return;
                }
            }
        }

        private static void postEventFromNative(Object weakRef, int msg, int arg1, int arg2, Object obj) {
            SoundPoolImpl soundPoolImpl = (SoundPoolImpl) ((WeakReference) weakRef).get();
            if (soundPoolImpl != null && soundPoolImpl.mEventHandler != null) {
                Message m = soundPoolImpl.mEventHandler.obtainMessage(msg, arg1, arg2, obj);
                soundPoolImpl.mEventHandler.sendMessage(m);
            }
        }

        protected void finalize() {
            release();
        }
    }

    /* loaded from: SoundPool$SoundPoolStub.class */
    static class SoundPoolStub implements SoundPoolDelegate {
        @Override // android.media.SoundPool.SoundPoolDelegate
        public int load(String path, int priority) {
            return 0;
        }

        @Override // android.media.SoundPool.SoundPoolDelegate
        public int load(Context context, int resId, int priority) {
            return 0;
        }

        @Override // android.media.SoundPool.SoundPoolDelegate
        public int load(AssetFileDescriptor afd, int priority) {
            return 0;
        }

        @Override // android.media.SoundPool.SoundPoolDelegate
        public int load(FileDescriptor fd, long offset, long length, int priority) {
            return 0;
        }

        @Override // android.media.SoundPool.SoundPoolDelegate
        public final boolean unload(int soundID) {
            return true;
        }

        @Override // android.media.SoundPool.SoundPoolDelegate
        public final int play(int soundID, float leftVolume, float rightVolume, int priority, int loop, float rate) {
            return 0;
        }

        @Override // android.media.SoundPool.SoundPoolDelegate
        public final void pause(int streamID) {
        }

        @Override // android.media.SoundPool.SoundPoolDelegate
        public final void resume(int streamID) {
        }

        @Override // android.media.SoundPool.SoundPoolDelegate
        public final void autoPause() {
        }

        @Override // android.media.SoundPool.SoundPoolDelegate
        public final void autoResume() {
        }

        @Override // android.media.SoundPool.SoundPoolDelegate
        public final void stop(int streamID) {
        }

        @Override // android.media.SoundPool.SoundPoolDelegate
        public final void setVolume(int streamID, float leftVolume, float rightVolume) {
        }

        @Override // android.media.SoundPool.SoundPoolDelegate
        public void setVolume(int streamID, float volume) {
        }

        @Override // android.media.SoundPool.SoundPoolDelegate
        public final void setPriority(int streamID, int priority) {
        }

        @Override // android.media.SoundPool.SoundPoolDelegate
        public final void setLoop(int streamID, int loop) {
        }

        @Override // android.media.SoundPool.SoundPoolDelegate
        public final void setRate(int streamID, float rate) {
        }

        @Override // android.media.SoundPool.SoundPoolDelegate
        public void setOnLoadCompleteListener(OnLoadCompleteListener listener) {
        }

        @Override // android.media.SoundPool.SoundPoolDelegate
        public final void release() {
        }
    }
}