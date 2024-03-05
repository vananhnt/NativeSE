package android.support.v4.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;

/* loaded from: EdgeEffectCompat.class */
public class EdgeEffectCompat {
    private static final EdgeEffectImpl IMPL;
    private Object mEdgeEffect;

    /* loaded from: EdgeEffectCompat$BaseEdgeEffectImpl.class */
    static class BaseEdgeEffectImpl implements EdgeEffectImpl {
        BaseEdgeEffectImpl() {
        }

        @Override // android.support.v4.widget.EdgeEffectCompat.EdgeEffectImpl
        public boolean draw(Object obj, Canvas canvas) {
            return false;
        }

        @Override // android.support.v4.widget.EdgeEffectCompat.EdgeEffectImpl
        public void finish(Object obj) {
        }

        @Override // android.support.v4.widget.EdgeEffectCompat.EdgeEffectImpl
        public boolean isFinished(Object obj) {
            return true;
        }

        @Override // android.support.v4.widget.EdgeEffectCompat.EdgeEffectImpl
        public Object newEdgeEffect(Context context) {
            return null;
        }

        @Override // android.support.v4.widget.EdgeEffectCompat.EdgeEffectImpl
        public boolean onAbsorb(Object obj, int i) {
            return false;
        }

        @Override // android.support.v4.widget.EdgeEffectCompat.EdgeEffectImpl
        public boolean onPull(Object obj, float f) {
            return false;
        }

        @Override // android.support.v4.widget.EdgeEffectCompat.EdgeEffectImpl
        public boolean onRelease(Object obj) {
            return false;
        }

        @Override // android.support.v4.widget.EdgeEffectCompat.EdgeEffectImpl
        public void setSize(Object obj, int i, int i2) {
        }
    }

    /* loaded from: EdgeEffectCompat$EdgeEffectIcsImpl.class */
    static class EdgeEffectIcsImpl implements EdgeEffectImpl {
        EdgeEffectIcsImpl() {
        }

        @Override // android.support.v4.widget.EdgeEffectCompat.EdgeEffectImpl
        public boolean draw(Object obj, Canvas canvas) {
            return EdgeEffectCompatIcs.draw(obj, canvas);
        }

        @Override // android.support.v4.widget.EdgeEffectCompat.EdgeEffectImpl
        public void finish(Object obj) {
            EdgeEffectCompatIcs.finish(obj);
        }

        @Override // android.support.v4.widget.EdgeEffectCompat.EdgeEffectImpl
        public boolean isFinished(Object obj) {
            return EdgeEffectCompatIcs.isFinished(obj);
        }

        @Override // android.support.v4.widget.EdgeEffectCompat.EdgeEffectImpl
        public Object newEdgeEffect(Context context) {
            return EdgeEffectCompatIcs.newEdgeEffect(context);
        }

        @Override // android.support.v4.widget.EdgeEffectCompat.EdgeEffectImpl
        public boolean onAbsorb(Object obj, int i) {
            return EdgeEffectCompatIcs.onAbsorb(obj, i);
        }

        @Override // android.support.v4.widget.EdgeEffectCompat.EdgeEffectImpl
        public boolean onPull(Object obj, float f) {
            return EdgeEffectCompatIcs.onPull(obj, f);
        }

        @Override // android.support.v4.widget.EdgeEffectCompat.EdgeEffectImpl
        public boolean onRelease(Object obj) {
            return EdgeEffectCompatIcs.onRelease(obj);
        }

        @Override // android.support.v4.widget.EdgeEffectCompat.EdgeEffectImpl
        public void setSize(Object obj, int i, int i2) {
            EdgeEffectCompatIcs.setSize(obj, i, i2);
        }
    }

    /* loaded from: EdgeEffectCompat$EdgeEffectImpl.class */
    interface EdgeEffectImpl {
        boolean draw(Object obj, Canvas canvas);

        void finish(Object obj);

        boolean isFinished(Object obj);

        Object newEdgeEffect(Context context);

        boolean onAbsorb(Object obj, int i);

        boolean onPull(Object obj, float f);

        boolean onRelease(Object obj);

        void setSize(Object obj, int i, int i2);
    }

    static {
        if (Build.VERSION.SDK_INT >= 14) {
            IMPL = new EdgeEffectIcsImpl();
        } else {
            IMPL = new BaseEdgeEffectImpl();
        }
    }

    public EdgeEffectCompat(Context context) {
        this.mEdgeEffect = IMPL.newEdgeEffect(context);
    }

    public boolean draw(Canvas canvas) {
        return IMPL.draw(this.mEdgeEffect, canvas);
    }

    public void finish() {
        IMPL.finish(this.mEdgeEffect);
    }

    public boolean isFinished() {
        return IMPL.isFinished(this.mEdgeEffect);
    }

    public boolean onAbsorb(int i) {
        return IMPL.onAbsorb(this.mEdgeEffect, i);
    }

    public boolean onPull(float f) {
        return IMPL.onPull(this.mEdgeEffect, f);
    }

    public boolean onRelease() {
        return IMPL.onRelease(this.mEdgeEffect);
    }

    public void setSize(int i, int i2) {
        IMPL.setSize(this.mEdgeEffect, i, i2);
    }
}