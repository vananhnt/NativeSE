package android.view;

import android.content.Context;
import android.hardware.SensorListener;

@Deprecated
/* loaded from: OrientationListener.class */
public abstract class OrientationListener implements SensorListener {
    private OrientationEventListener mOrientationEventLis;
    public static final int ORIENTATION_UNKNOWN = -1;

    public abstract void onOrientationChanged(int i);

    public OrientationListener(Context context) {
        this.mOrientationEventLis = new OrientationEventListenerInternal(context);
    }

    public OrientationListener(Context context, int rate) {
        this.mOrientationEventLis = new OrientationEventListenerInternal(context, rate);
    }

    /* loaded from: OrientationListener$OrientationEventListenerInternal.class */
    class OrientationEventListenerInternal extends OrientationEventListener {
        OrientationEventListenerInternal(Context context) {
            super(context);
        }

        OrientationEventListenerInternal(Context context, int rate) {
            super(context, rate);
            registerListener(OrientationListener.this);
        }

        @Override // android.view.OrientationEventListener
        public void onOrientationChanged(int orientation) {
            OrientationListener.this.onOrientationChanged(orientation);
        }
    }

    public void enable() {
        this.mOrientationEventLis.enable();
    }

    public void disable() {
        this.mOrientationEventLis.disable();
    }

    @Override // android.hardware.SensorListener
    public void onAccuracyChanged(int sensor, int accuracy) {
    }

    @Override // android.hardware.SensorListener
    public void onSensorChanged(int sensor, float[] values) {
    }
}