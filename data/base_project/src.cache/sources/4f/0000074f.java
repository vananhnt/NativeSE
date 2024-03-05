package android.media;

import android.graphics.ImageFormat;
import android.media.Image;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Surface;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/* loaded from: ImageReader.class */
public class ImageReader implements AutoCloseable {
    private static final int ACQUIRE_SUCCESS = 0;
    private static final int ACQUIRE_NO_BUFS = 1;
    private static final int ACQUIRE_MAX_IMAGES = 2;
    private final int mWidth;
    private final int mHeight;
    private final int mFormat;
    private final int mMaxImages;
    private final int mNumPlanes;
    private final Surface mSurface;
    private final Object mListenerLock = new Object();
    private OnImageAvailableListener mListener;
    private ListenerHandler mListenerHandler;
    private long mNativeContext;

    /* loaded from: ImageReader$OnImageAvailableListener.class */
    public interface OnImageAvailableListener {
        void onImageAvailable(ImageReader imageReader);
    }

    private native synchronized void nativeInit(Object obj, int i, int i2, int i3, int i4);

    private native synchronized void nativeClose();

    private native synchronized void nativeReleaseImage(Image image);

    private native synchronized Surface nativeGetSurface();

    private native synchronized int nativeImageSetup(Image image);

    private static native void nativeClassInit();

    public static ImageReader newInstance(int width, int height, int format, int maxImages) {
        return new ImageReader(width, height, format, maxImages);
    }

    protected ImageReader(int width, int height, int format, int maxImages) {
        this.mWidth = width;
        this.mHeight = height;
        this.mFormat = format;
        this.mMaxImages = maxImages;
        if (width < 1 || height < 1) {
            throw new IllegalArgumentException("The image dimensions must be positive");
        }
        if (this.mMaxImages < 1) {
            throw new IllegalArgumentException("Maximum outstanding image count must be at least 1");
        }
        if (format == 17) {
            throw new IllegalArgumentException("NV21 format is not supported");
        }
        this.mNumPlanes = getNumPlanesFromFormat();
        nativeInit(new WeakReference(this), width, height, format, maxImages);
        this.mSurface = nativeGetSurface();
    }

    public int getWidth() {
        return this.mWidth;
    }

    public int getHeight() {
        return this.mHeight;
    }

    public int getImageFormat() {
        return this.mFormat;
    }

    public int getMaxImages() {
        return this.mMaxImages;
    }

    public Surface getSurface() {
        return this.mSurface;
    }

    public Image acquireLatestImage() {
        Image image = acquireNextImage();
        if (image == null) {
            return null;
        }
        while (true) {
            try {
                Image next = acquireNextImageNoThrowISE();
                if (next == null) {
                    break;
                }
                image.close();
                image = next;
            } catch (Throwable th) {
                if (image != null) {
                    image.close();
                }
                throw th;
            }
        }
        Image result = image;
        Image image2 = null;
        if (0 != 0) {
            image2.close();
        }
        return result;
    }

    public Image acquireNextImageNoThrowISE() {
        SurfaceImage si = new SurfaceImage();
        if (acquireNextSurfaceImage(si) == 0) {
            return si;
        }
        return null;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private int acquireNextSurfaceImage(SurfaceImage si) {
        int status = nativeImageSetup(si);
        switch (status) {
            case 0:
                si.createSurfacePlanes();
                si.setImageValid(true);
                break;
            case 1:
            case 2:
                break;
            default:
                throw new AssertionError("Unknown nativeImageSetup return code " + status);
        }
        return status;
    }

    public Image acquireNextImage() {
        SurfaceImage si = new SurfaceImage();
        int status = acquireNextSurfaceImage(si);
        switch (status) {
            case 0:
                return si;
            case 1:
                return null;
            case 2:
                throw new IllegalStateException(String.format("maxImages (%d) has already been acquired, call #close before acquiring more.", Integer.valueOf(this.mMaxImages)));
            default:
                throw new AssertionError("Unknown nativeImageSetup return code " + status);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void releaseImage(Image i) {
        if (!(i instanceof SurfaceImage)) {
            throw new IllegalArgumentException("This image was not produced by an ImageReader");
        }
        SurfaceImage si = (SurfaceImage) i;
        if (si.getReader() != this) {
            throw new IllegalArgumentException("This image was not produced by this ImageReader");
        }
        si.clearSurfacePlanes();
        nativeReleaseImage(i);
        si.setImageValid(false);
    }

    public void setOnImageAvailableListener(OnImageAvailableListener listener, Handler handler) {
        synchronized (this.mListenerLock) {
            if (listener != null) {
                Looper looper = handler != null ? handler.getLooper() : Looper.myLooper();
                if (looper == null) {
                    throw new IllegalArgumentException("handler is null but the current thread is not a looper");
                }
                if (this.mListenerHandler == null || this.mListenerHandler.getLooper() != looper) {
                    this.mListenerHandler = new ListenerHandler(looper);
                }
                this.mListener = listener;
            } else {
                this.mListener = null;
                this.mListenerHandler = null;
            }
        }
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        setOnImageAvailableListener(null, null);
        nativeClose();
    }

    protected void finalize() throws Throwable {
        try {
            close();
            super.finalize();
        } catch (Throwable th) {
            super.finalize();
            throw th;
        }
    }

    private int getNumPlanesFromFormat() {
        switch (this.mFormat) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 20:
            case 32:
            case 256:
            case ImageFormat.Y8 /* 538982489 */:
            case ImageFormat.Y16 /* 540422489 */:
                return 1;
            case 16:
                return 2;
            case 17:
            case 35:
            case ImageFormat.YV12 /* 842094169 */:
                return 3;
            default:
                throw new UnsupportedOperationException(String.format("Invalid format specified %d", Integer.valueOf(this.mFormat)));
        }
    }

    private static void postEventFromNative(Object selfRef) {
        Handler handler;
        WeakReference<ImageReader> weakSelf = (WeakReference) selfRef;
        ImageReader ir = weakSelf.get();
        if (ir == null) {
            return;
        }
        synchronized (ir.mListenerLock) {
            handler = ir.mListenerHandler;
        }
        if (handler != null) {
            handler.sendEmptyMessage(0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ImageReader$ListenerHandler.class */
    public final class ListenerHandler extends Handler {
        public ListenerHandler(Looper looper) {
            super(looper, null, true);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            OnImageAvailableListener listener;
            synchronized (ImageReader.this.mListenerLock) {
                listener = ImageReader.this.mListener;
            }
            if (listener != null) {
                listener.onImageAvailable(ImageReader.this);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ImageReader$SurfaceImage.class */
    public class SurfaceImage extends Image {
        private long mLockedBuffer;
        private long mTimestamp;
        private SurfacePlane[] mPlanes;
        private boolean mIsImageValid = false;

        /* JADX INFO: Access modifiers changed from: private */
        public native synchronized ByteBuffer nativeImageGetBuffer(int i);

        private native synchronized SurfacePlane nativeCreatePlane(int i);

        public SurfaceImage() {
        }

        @Override // android.media.Image, java.lang.AutoCloseable
        public void close() {
            if (this.mIsImageValid) {
                ImageReader.this.releaseImage(this);
            }
        }

        public ImageReader getReader() {
            return ImageReader.this;
        }

        @Override // android.media.Image
        public int getFormat() {
            if (this.mIsImageValid) {
                return ImageReader.this.mFormat;
            }
            throw new IllegalStateException("Image is already released");
        }

        @Override // android.media.Image
        public int getWidth() {
            if (this.mIsImageValid) {
                return ImageReader.this.mWidth;
            }
            throw new IllegalStateException("Image is already released");
        }

        @Override // android.media.Image
        public int getHeight() {
            if (this.mIsImageValid) {
                return ImageReader.this.mHeight;
            }
            throw new IllegalStateException("Image is already released");
        }

        @Override // android.media.Image
        public long getTimestamp() {
            if (this.mIsImageValid) {
                return this.mTimestamp;
            }
            throw new IllegalStateException("Image is already released");
        }

        @Override // android.media.Image
        public Image.Plane[] getPlanes() {
            if (this.mIsImageValid) {
                return (Image.Plane[]) this.mPlanes.clone();
            }
            throw new IllegalStateException("Image is already released");
        }

        protected final void finalize() throws Throwable {
            try {
                close();
                super.finalize();
            } catch (Throwable th) {
                super.finalize();
                throw th;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setImageValid(boolean isValid) {
            this.mIsImageValid = isValid;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isImageValid() {
            return this.mIsImageValid;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearSurfacePlanes() {
            if (this.mIsImageValid) {
                for (int i = 0; i < this.mPlanes.length; i++) {
                    if (this.mPlanes[i] != null) {
                        this.mPlanes[i].clearBuffer();
                        this.mPlanes[i] = null;
                    }
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void createSurfacePlanes() {
            this.mPlanes = new SurfacePlane[ImageReader.this.mNumPlanes];
            for (int i = 0; i < ImageReader.this.mNumPlanes; i++) {
                this.mPlanes[i] = nativeCreatePlane(i);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: ImageReader$SurfaceImage$SurfacePlane.class */
        public class SurfacePlane extends Image.Plane {
            private final int mIndex;
            private final int mPixelStride;
            private final int mRowStride;
            private ByteBuffer mBuffer;

            private SurfacePlane(int index, int rowStride, int pixelStride) {
                this.mIndex = index;
                this.mRowStride = rowStride;
                this.mPixelStride = pixelStride;
            }

            @Override // android.media.Image.Plane
            public ByteBuffer getBuffer() {
                if (!SurfaceImage.this.isImageValid()) {
                    throw new IllegalStateException("Image is already released");
                }
                if (this.mBuffer != null) {
                    return this.mBuffer;
                }
                this.mBuffer = SurfaceImage.this.nativeImageGetBuffer(this.mIndex);
                return this.mBuffer.order(ByteOrder.nativeOrder());
            }

            @Override // android.media.Image.Plane
            public int getPixelStride() {
                if (SurfaceImage.this.isImageValid()) {
                    return this.mPixelStride;
                }
                throw new IllegalStateException("Image is already released");
            }

            @Override // android.media.Image.Plane
            public int getRowStride() {
                if (SurfaceImage.this.isImageValid()) {
                    return this.mRowStride;
                }
                throw new IllegalStateException("Image is already released");
            }

            /* JADX INFO: Access modifiers changed from: private */
            public void clearBuffer() {
                this.mBuffer = null;
            }
        }
    }

    static {
        System.loadLibrary("media_jni");
        nativeClassInit();
    }
}