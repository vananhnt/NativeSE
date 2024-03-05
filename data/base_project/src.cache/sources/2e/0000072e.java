package android.media;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.util.Log;

/* loaded from: FaceDetector.class */
public class FaceDetector {
    private static boolean sInitialized;
    private int mFD;
    private int mSDK;
    private int mDCR;
    private int mWidth;
    private int mHeight;
    private int mMaxFaces;
    private byte[] mBWBuffer;

    private static native void nativeClassInit();

    private native int fft_initialize(int i, int i2, int i3);

    private native int fft_detect(Bitmap bitmap);

    private native void fft_get_face(Face face, int i);

    private native void fft_destroy();

    /* loaded from: FaceDetector$Face.class */
    public class Face {
        public static final float CONFIDENCE_THRESHOLD = 0.4f;
        public static final int EULER_X = 0;
        public static final int EULER_Y = 1;
        public static final int EULER_Z = 2;
        private float mConfidence;
        private float mMidPointX;
        private float mMidPointY;
        private float mEyesDist;
        private float mPoseEulerX;
        private float mPoseEulerY;
        private float mPoseEulerZ;

        public float confidence() {
            return this.mConfidence;
        }

        public void getMidPoint(PointF point) {
            point.set(this.mMidPointX, this.mMidPointY);
        }

        public float eyesDistance() {
            return this.mEyesDist;
        }

        public float pose(int euler) {
            if (euler == 0) {
                return this.mPoseEulerX;
            }
            if (euler == 1) {
                return this.mPoseEulerY;
            }
            if (euler == 2) {
                return this.mPoseEulerZ;
            }
            throw new IllegalArgumentException();
        }

        private Face() {
        }
    }

    public FaceDetector(int width, int height, int maxFaces) {
        if (!sInitialized) {
            return;
        }
        fft_initialize(width, height, maxFaces);
        this.mWidth = width;
        this.mHeight = height;
        this.mMaxFaces = maxFaces;
        this.mBWBuffer = new byte[width * height];
    }

    public int findFaces(Bitmap bitmap, Face[] faces) {
        if (!sInitialized) {
            return 0;
        }
        if (bitmap.getWidth() != this.mWidth || bitmap.getHeight() != this.mHeight) {
            throw new IllegalArgumentException("bitmap size doesn't match initialization");
        }
        if (faces.length < this.mMaxFaces) {
            throw new IllegalArgumentException("faces[] smaller than maxFaces");
        }
        int numFaces = fft_detect(bitmap);
        if (numFaces >= this.mMaxFaces) {
            numFaces = this.mMaxFaces;
        }
        for (int i = 0; i < numFaces; i++) {
            if (faces[i] == null) {
                faces[i] = new Face();
            }
            fft_get_face(faces[i], i);
        }
        return numFaces;
    }

    protected void finalize() throws Throwable {
        fft_destroy();
    }

    static {
        sInitialized = false;
        try {
            System.loadLibrary("FFTEm");
            nativeClassInit();
            sInitialized = true;
        } catch (UnsatisfiedLinkError e) {
            Log.d("FFTEm", "face detection library not found!");
        }
    }
}