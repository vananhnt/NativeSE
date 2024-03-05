package android.hardware;

/* loaded from: SensorEventListener.class */
public interface SensorEventListener {
    void onSensorChanged(SensorEvent sensorEvent);

    void onAccuracyChanged(Sensor sensor, int i);
}