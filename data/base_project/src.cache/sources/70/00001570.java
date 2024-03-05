package android.view;

import android.util.Pools;

/* loaded from: VelocityTracker.class */
public final class VelocityTracker {
    private static final Pools.SynchronizedPool<VelocityTracker> sPool = new Pools.SynchronizedPool<>(2);
    private static final int ACTIVE_POINTER_ID = -1;
    private int mPtr;
    private final String mStrategy;

    private static native int nativeInitialize(String str);

    private static native void nativeDispose(int i);

    private static native void nativeClear(int i);

    private static native void nativeAddMovement(int i, MotionEvent motionEvent);

    private static native void nativeComputeCurrentVelocity(int i, int i2, float f);

    private static native float nativeGetXVelocity(int i, int i2);

    private static native float nativeGetYVelocity(int i, int i2);

    private static native boolean nativeGetEstimator(int i, int i2, Estimator estimator);

    public static VelocityTracker obtain() {
        VelocityTracker instance = sPool.acquire();
        return instance != null ? instance : new VelocityTracker(null);
    }

    public static VelocityTracker obtain(String strategy) {
        if (strategy == null) {
            return obtain();
        }
        return new VelocityTracker(strategy);
    }

    public void recycle() {
        if (this.mStrategy == null) {
            clear();
            sPool.release(this);
        }
    }

    private VelocityTracker(String strategy) {
        this.mPtr = nativeInitialize(strategy);
        this.mStrategy = strategy;
    }

    protected void finalize() throws Throwable {
        try {
            if (this.mPtr != 0) {
                nativeDispose(this.mPtr);
                this.mPtr = 0;
            }
        } finally {
            super.finalize();
        }
    }

    public void clear() {
        nativeClear(this.mPtr);
    }

    public void addMovement(MotionEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("event must not be null");
        }
        nativeAddMovement(this.mPtr, event);
    }

    public void computeCurrentVelocity(int units) {
        nativeComputeCurrentVelocity(this.mPtr, units, Float.MAX_VALUE);
    }

    public void computeCurrentVelocity(int units, float maxVelocity) {
        nativeComputeCurrentVelocity(this.mPtr, units, maxVelocity);
    }

    public float getXVelocity() {
        return nativeGetXVelocity(this.mPtr, -1);
    }

    public float getYVelocity() {
        return nativeGetYVelocity(this.mPtr, -1);
    }

    public float getXVelocity(int id) {
        return nativeGetXVelocity(this.mPtr, id);
    }

    public float getYVelocity(int id) {
        return nativeGetYVelocity(this.mPtr, id);
    }

    public boolean getEstimator(int id, Estimator outEstimator) {
        if (outEstimator == null) {
            throw new IllegalArgumentException("outEstimator must not be null");
        }
        return nativeGetEstimator(this.mPtr, id, outEstimator);
    }

    /* loaded from: VelocityTracker$Estimator.class */
    public static final class Estimator {
        private static final int MAX_DEGREE = 4;
        public final float[] xCoeff = new float[5];
        public final float[] yCoeff = new float[5];
        public int degree;
        public float confidence;

        public float estimateX(float time) {
            return estimate(time, this.xCoeff);
        }

        public float estimateY(float time) {
            return estimate(time, this.yCoeff);
        }

        public float getXCoeff(int index) {
            if (index <= this.degree) {
                return this.xCoeff[index];
            }
            return 0.0f;
        }

        public float getYCoeff(int index) {
            if (index <= this.degree) {
                return this.yCoeff[index];
            }
            return 0.0f;
        }

        private float estimate(float time, float[] c) {
            float a = 0.0f;
            float scale = 1.0f;
            for (int i = 0; i <= this.degree; i++) {
                a += c[i] * scale;
                scale *= time;
            }
            return a;
        }
    }
}