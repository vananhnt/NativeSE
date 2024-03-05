package android.hardware;

/* loaded from: TriggerEvent.class */
public final class TriggerEvent {
    public final float[] values;
    public Sensor sensor;
    public long timestamp;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TriggerEvent(int size) {
        this.values = new float[size];
    }
}