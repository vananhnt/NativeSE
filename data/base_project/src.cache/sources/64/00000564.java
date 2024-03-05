package android.graphics;

import java.io.InputStream;
import java.io.OutputStream;

/* loaded from: Picture.class */
public class Picture {
    private Canvas mRecordingCanvas;
    private final int mNativePicture;
    public final boolean createdFromStream;
    private static final int WORKING_STREAM_STORAGE = 16384;

    public native int getWidth();

    public native int getHeight();

    private static native int nativeConstructor(int i);

    private static native int nativeCreateFromStream(InputStream inputStream, byte[] bArr);

    private static native int nativeBeginRecording(int i, int i2, int i3);

    private static native void nativeEndRecording(int i);

    private static native void nativeDraw(int i, int i2);

    private static native boolean nativeWriteToStream(int i, OutputStream outputStream, byte[] bArr);

    private static native void nativeDestructor(int i);

    public Picture() {
        this(nativeConstructor(0), false);
    }

    public Picture(Picture src) {
        this(nativeConstructor(src != null ? src.mNativePicture : 0), false);
    }

    public Canvas beginRecording(int width, int height) {
        int ni = nativeBeginRecording(this.mNativePicture, width, height);
        this.mRecordingCanvas = new RecordingCanvas(this, ni);
        return this.mRecordingCanvas;
    }

    public void endRecording() {
        if (this.mRecordingCanvas != null) {
            this.mRecordingCanvas = null;
            nativeEndRecording(this.mNativePicture);
        }
    }

    public void draw(Canvas canvas) {
        if (this.mRecordingCanvas != null) {
            endRecording();
        }
        nativeDraw(canvas.mNativeCanvas, this.mNativePicture);
    }

    @Deprecated
    public static Picture createFromStream(InputStream stream) {
        return new Picture(nativeCreateFromStream(stream, new byte[16384]), true);
    }

    @Deprecated
    public void writeToStream(OutputStream stream) {
        if (stream == null) {
            throw new NullPointerException();
        }
        if (!nativeWriteToStream(this.mNativePicture, stream, new byte[16384])) {
            throw new RuntimeException();
        }
    }

    protected void finalize() throws Throwable {
        try {
            nativeDestructor(this.mNativePicture);
            super.finalize();
        } catch (Throwable th) {
            super.finalize();
            throw th;
        }
    }

    final int ni() {
        return this.mNativePicture;
    }

    private Picture(int nativePicture, boolean fromStream) {
        if (nativePicture == 0) {
            throw new RuntimeException();
        }
        this.mNativePicture = nativePicture;
        this.createdFromStream = fromStream;
    }

    /* loaded from: Picture$RecordingCanvas.class */
    private static class RecordingCanvas extends Canvas {
        private final Picture mPicture;

        public RecordingCanvas(Picture pict, int nativeCanvas) {
            super(nativeCanvas);
            this.mPicture = pict;
        }

        @Override // android.graphics.Canvas
        public void setBitmap(Bitmap bitmap) {
            throw new RuntimeException("Cannot call setBitmap on a picture canvas");
        }

        @Override // android.graphics.Canvas
        public void drawPicture(Picture picture) {
            if (this.mPicture == picture) {
                throw new RuntimeException("Cannot draw a picture into its recording canvas");
            }
            super.drawPicture(picture);
        }
    }
}