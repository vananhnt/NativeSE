package android.support.v4.view;

import android.os.Build;
import android.view.VelocityTracker;

/* loaded from: VelocityTrackerCompat.class */
public class VelocityTrackerCompat {
    static final VelocityTrackerVersionImpl IMPL;

    /* loaded from: VelocityTrackerCompat$BaseVelocityTrackerVersionImpl.class */
    static class BaseVelocityTrackerVersionImpl implements VelocityTrackerVersionImpl {
        BaseVelocityTrackerVersionImpl() {
        }

        @Override // android.support.v4.view.VelocityTrackerCompat.VelocityTrackerVersionImpl
        public float getXVelocity(VelocityTracker velocityTracker, int i) {
            return velocityTracker.getXVelocity();
        }

        @Override // android.support.v4.view.VelocityTrackerCompat.VelocityTrackerVersionImpl
        public float getYVelocity(VelocityTracker velocityTracker, int i) {
            return velocityTracker.getYVelocity();
        }
    }

    /* loaded from: VelocityTrackerCompat$HoneycombVelocityTrackerVersionImpl.class */
    static class HoneycombVelocityTrackerVersionImpl implements VelocityTrackerVersionImpl {
        HoneycombVelocityTrackerVersionImpl() {
        }

        @Override // android.support.v4.view.VelocityTrackerCompat.VelocityTrackerVersionImpl
        public float getXVelocity(VelocityTracker velocityTracker, int i) {
            return VelocityTrackerCompatHoneycomb.getXVelocity(velocityTracker, i);
        }

        @Override // android.support.v4.view.VelocityTrackerCompat.VelocityTrackerVersionImpl
        public float getYVelocity(VelocityTracker velocityTracker, int i) {
            return VelocityTrackerCompatHoneycomb.getYVelocity(velocityTracker, i);
        }
    }

    /* loaded from: VelocityTrackerCompat$VelocityTrackerVersionImpl.class */
    interface VelocityTrackerVersionImpl {
        float getXVelocity(VelocityTracker velocityTracker, int i);

        float getYVelocity(VelocityTracker velocityTracker, int i);
    }

    static {
        if (Build.VERSION.SDK_INT >= 11) {
            IMPL = new HoneycombVelocityTrackerVersionImpl();
        } else {
            IMPL = new BaseVelocityTrackerVersionImpl();
        }
    }

    public static float getXVelocity(VelocityTracker velocityTracker, int i) {
        return IMPL.getXVelocity(velocityTracker, i);
    }

    public static float getYVelocity(VelocityTracker velocityTracker, int i) {
        return IMPL.getYVelocity(velocityTracker, i);
    }
}