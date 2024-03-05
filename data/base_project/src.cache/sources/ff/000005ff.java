package android.hardware;

/* loaded from: SensorEvent.class */
public class SensorEvent {
    public final float[] values;
    public Sensor sensor;
    public int accuracy;
    public long timestamp;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SensorEvent(int valueSize) {
        this.values = new float[valueSize];
    }
}