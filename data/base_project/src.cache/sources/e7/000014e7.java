package android.view;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: GraphicBuffer.class */
public class GraphicBuffer implements Parcelable {
    public static final int USAGE_SW_READ_NEVER = 0;
    public static final int USAGE_SW_READ_RARELY = 2;
    public static final int USAGE_SW_READ_OFTEN = 3;
    public static final int USAGE_SW_READ_MASK = 15;
    public static final int USAGE_SW_WRITE_NEVER = 0;
    public static final int USAGE_SW_WRITE_RARELY = 32;
    public static final int USAGE_SW_WRITE_OFTEN = 48;
    public static final int USAGE_SW_WRITE_MASK = 240;
    public static final int USAGE_SOFTWARE_MASK = 255;
    public static final int USAGE_PROTECTED = 16384;
    public static final int USAGE_HW_TEXTURE = 256;
    public static final int USAGE_HW_RENDER = 512;
    public static final int USAGE_HW_2D = 1024;
    public static final int USAGE_HW_COMPOSER = 2048;
    public static final int USAGE_HW_VIDEO_ENCODER = 65536;
    public static final int USAGE_HW_MASK = 466688;
    private final int mWidth;
    private final int mHeight;
    private final int mFormat;
    private final int mUsage;
    private final int mNativeObject;
    private Canvas mCanvas;
    private int mSaveCount;
    private boolean mDestroyed;
    public static final Parcelable.Creator<GraphicBuffer> CREATOR = new Parcelable.Creator<GraphicBuffer>() { // from class: android.view.GraphicBuffer.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public GraphicBuffer createFromParcel(Parcel in) {
            int width = in.readInt();
            int height = in.readInt();
            int format = in.readInt();
            int usage = in.readInt();
            int nativeObject = GraphicBuffer.nReadGraphicBufferFromParcel(in);
            if (nativeObject != 0) {
                return new GraphicBuffer(width, height, format, usage, nativeObject);
            }
            return null;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public GraphicBuffer[] newArray(int size) {
            return new GraphicBuffer[size];
        }
    };

    private static native int nCreateGraphicBuffer(int i, int i2, int i3, int i4);

    private static native void nDestroyGraphicBuffer(int i);

    private static native void nWriteGraphicBufferToParcel(int i, Parcel parcel);

    /* JADX INFO: Access modifiers changed from: private */
    public static native int nReadGraphicBufferFromParcel(Parcel parcel);

    private static native boolean nLockCanvas(int i, Canvas canvas, Rect rect);

    private static native boolean nUnlockCanvasAndPost(int i, Canvas canvas);

    public static GraphicBuffer create(int width, int height, int format, int usage) {
        int nativeObject = nCreateGraphicBuffer(width, height, format, usage);
        if (nativeObject != 0) {
            return new GraphicBuffer(width, height, format, usage, nativeObject);
        }
        return null;
    }

    private GraphicBuffer(int width, int height, int format, int usage, int nativeObject) {
        this.mWidth = width;
        this.mHeight = height;
        this.mFormat = format;
        this.mUsage = usage;
        this.mNativeObject = nativeObject;
    }

    public int getWidth() {
        return this.mWidth;
    }

    public int getHeight() {
        return this.mHeight;
    }

    public int getFormat() {
        return this.mFormat;
    }

    public int getUsage() {
        return this.mUsage;
    }

    public Canvas lockCanvas() {
        return lockCanvas(null);
    }

    public Canvas lockCanvas(Rect dirty) {
        if (this.mDestroyed) {
            return null;
        }
        if (this.mCanvas == null) {
            this.mCanvas = new Canvas();
        }
        if (nLockCanvas(this.mNativeObject, this.mCanvas, dirty)) {
            this.mSaveCount = this.mCanvas.save();
            return this.mCanvas;
        }
        return null;
    }

    public void unlockCanvasAndPost(Canvas canvas) {
        if (!this.mDestroyed && this.mCanvas != null && canvas == this.mCanvas) {
            canvas.restoreToCount(this.mSaveCount);
            this.mSaveCount = 0;
            nUnlockCanvasAndPost(this.mNativeObject, this.mCanvas);
        }
    }

    public void destroy() {
        if (!this.mDestroyed) {
            this.mDestroyed = true;
            nDestroyGraphicBuffer(this.mNativeObject);
        }
    }

    public boolean isDestroyed() {
        return this.mDestroyed;
    }

    protected void finalize() throws Throwable {
        try {
            if (!this.mDestroyed) {
                nDestroyGraphicBuffer(this.mNativeObject);
            }
        } finally {
            super.finalize();
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        if (this.mDestroyed) {
            throw new IllegalStateException("This GraphicBuffer has been destroyed and cannot be written to a parcel.");
        }
        dest.writeInt(this.mWidth);
        dest.writeInt(this.mHeight);
        dest.writeInt(this.mFormat);
        dest.writeInt(this.mUsage);
        nWriteGraphicBufferToParcel(this.mNativeObject, dest);
    }
}