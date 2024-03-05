package android.hardware;

@Deprecated
/* loaded from: SensorListener.class */
public interface SensorListener {
    void onSensorChanged(int i, float[] fArr);

    void onAccuracyChanged(int i, int i2);
}