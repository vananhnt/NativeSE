package android.hardware;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import dalvik.system.CloseGuard;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* loaded from: SystemSensorManager.class */
public class SystemSensorManager extends SensorManager {
    private static boolean sSensorModuleInitialized = false;
    private static final Object sSensorModuleLock = new Object();
    private static final ArrayList<Sensor> sFullSensorsList = new ArrayList<>();
    private static final SparseArray<Sensor> sHandleToSensor = new SparseArray<>();
    private final HashMap<SensorEventListener, SensorEventQueue> mSensorListeners = new HashMap<>();
    private final HashMap<TriggerEventListener, TriggerEventQueue> mTriggerListeners = new HashMap<>();
    private final Looper mMainLooper;
    private final int mTargetSdkLevel;

    private static native void nativeClassInit();

    private static native int nativeGetNextSensor(Sensor sensor, int i);

    public SystemSensorManager(Context context, Looper mainLooper) {
        this.mMainLooper = mainLooper;
        this.mTargetSdkLevel = context.getApplicationInfo().targetSdkVersion;
        synchronized (sSensorModuleLock) {
            if (!sSensorModuleInitialized) {
                sSensorModuleInitialized = true;
                nativeClassInit();
                ArrayList<Sensor> fullList = sFullSensorsList;
                int i = 0;
                do {
                    Sensor sensor = new Sensor();
                    i = nativeGetNextSensor(sensor, i);
                    if (i >= 0) {
                        fullList.add(sensor);
                        sHandleToSensor.append(sensor.getHandle(), sensor);
                    }
                } while (i > 0);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.hardware.SensorManager
    public List<Sensor> getFullSensorList() {
        return sFullSensorsList;
    }

    @Override // android.hardware.SensorManager
    protected boolean registerListenerImpl(SensorEventListener listener, Sensor sensor, int delayUs, Handler handler, int maxBatchReportLatencyUs, int reservedFlags) {
        if (listener == null || sensor == null) {
            Log.e("SensorManager", "sensor or listener is null");
            return false;
        } else if (Sensor.getReportingMode(sensor) == Sensor.REPORTING_MODE_ONE_SHOT) {
            Log.e("SensorManager", "Trigger Sensors should use the requestTriggerSensor.");
            return false;
        } else if (maxBatchReportLatencyUs < 0 || delayUs < 0) {
            Log.e("SensorManager", "maxBatchReportLatencyUs and delayUs should be non-negative");
            return false;
        } else {
            synchronized (this.mSensorListeners) {
                SensorEventQueue queue = this.mSensorListeners.get(listener);
                if (queue == null) {
                    Looper looper = handler != null ? handler.getLooper() : this.mMainLooper;
                    SensorEventQueue queue2 = new SensorEventQueue(listener, looper, this);
                    if (!queue2.addSensor(sensor, delayUs, maxBatchReportLatencyUs, reservedFlags)) {
                        queue2.dispose();
                        return false;
                    }
                    this.mSensorListeners.put(listener, queue2);
                    return true;
                }
                return queue.addSensor(sensor, delayUs, maxBatchReportLatencyUs, reservedFlags);
            }
        }
    }

    @Override // android.hardware.SensorManager
    protected void unregisterListenerImpl(SensorEventListener listener, Sensor sensor) {
        boolean result;
        if (sensor != null && Sensor.getReportingMode(sensor) == Sensor.REPORTING_MODE_ONE_SHOT) {
            return;
        }
        synchronized (this.mSensorListeners) {
            SensorEventQueue queue = this.mSensorListeners.get(listener);
            if (queue != null) {
                if (sensor == null) {
                    result = queue.removeAllSensors();
                } else {
                    result = queue.removeSensor(sensor, true);
                }
                if (result && !queue.hasSensors()) {
                    this.mSensorListeners.remove(listener);
                    queue.dispose();
                }
            }
        }
    }

    @Override // android.hardware.SensorManager
    protected boolean requestTriggerSensorImpl(TriggerEventListener listener, Sensor sensor) {
        if (sensor == null) {
            throw new IllegalArgumentException("sensor cannot be null");
        }
        if (Sensor.getReportingMode(sensor) != Sensor.REPORTING_MODE_ONE_SHOT) {
            return false;
        }
        synchronized (this.mTriggerListeners) {
            TriggerEventQueue queue = this.mTriggerListeners.get(listener);
            if (queue == null) {
                TriggerEventQueue queue2 = new TriggerEventQueue(listener, this.mMainLooper, this);
                if (!queue2.addSensor(sensor, 0, 0, 0)) {
                    queue2.dispose();
                    return false;
                }
                this.mTriggerListeners.put(listener, queue2);
                return true;
            }
            return queue.addSensor(sensor, 0, 0, 0);
        }
    }

    @Override // android.hardware.SensorManager
    protected boolean cancelTriggerSensorImpl(TriggerEventListener listener, Sensor sensor, boolean disable) {
        boolean result;
        if (sensor != null && Sensor.getReportingMode(sensor) != Sensor.REPORTING_MODE_ONE_SHOT) {
            return false;
        }
        synchronized (this.mTriggerListeners) {
            TriggerEventQueue queue = this.mTriggerListeners.get(listener);
            if (queue != null) {
                if (sensor == null) {
                    result = queue.removeAllSensors();
                } else {
                    result = queue.removeSensor(sensor, disable);
                }
                if (result && !queue.hasSensors()) {
                    this.mTriggerListeners.remove(listener);
                    queue.dispose();
                }
                return result;
            }
            return false;
        }
    }

    @Override // android.hardware.SensorManager
    protected boolean flushImpl(SensorEventListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener cannot be null");
        }
        synchronized (this.mSensorListeners) {
            SensorEventQueue queue = this.mSensorListeners.get(listener);
            if (queue == null) {
                return false;
            }
            return queue.flush() == 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: SystemSensorManager$BaseEventQueue.class */
    public static abstract class BaseEventQueue {
        private int nSensorEventQueue;
        private final SparseBooleanArray mActiveSensors = new SparseBooleanArray();
        protected final SparseIntArray mSensorAccuracies = new SparseIntArray();
        protected final SparseBooleanArray mFirstEvent = new SparseBooleanArray();
        private final CloseGuard mCloseGuard = CloseGuard.get();
        private final float[] mScratch = new float[16];
        protected final SystemSensorManager mManager;

        private native int nativeInitBaseEventQueue(BaseEventQueue baseEventQueue, MessageQueue messageQueue, float[] fArr);

        private static native int nativeEnableSensor(int i, int i2, int i3, int i4, int i5);

        private static native int nativeDisableSensor(int i, int i2);

        private static native void nativeDestroySensorEventQueue(int i);

        private static native int nativeFlushSensor(int i);

        protected abstract void dispatchSensorEvent(int i, float[] fArr, int i2, long j);

        protected abstract void dispatchFlushCompleteEvent(int i);

        protected abstract void addSensorEvent(Sensor sensor);

        protected abstract void removeSensorEvent(Sensor sensor);

        BaseEventQueue(Looper looper, SystemSensorManager manager) {
            this.nSensorEventQueue = nativeInitBaseEventQueue(this, looper.getQueue(), this.mScratch);
            this.mCloseGuard.open("dispose");
            this.mManager = manager;
        }

        public void dispose() {
            dispose(false);
        }

        public boolean addSensor(Sensor sensor, int delayUs, int maxBatchReportLatencyUs, int reservedFlags) {
            int handle = sensor.getHandle();
            if (this.mActiveSensors.get(handle)) {
                return false;
            }
            this.mActiveSensors.put(handle, true);
            addSensorEvent(sensor);
            if (enableSensor(sensor, delayUs, maxBatchReportLatencyUs, reservedFlags) != 0) {
                if (maxBatchReportLatencyUs == 0 || (maxBatchReportLatencyUs > 0 && enableSensor(sensor, delayUs, 0, 0) != 0)) {
                    removeSensor(sensor, false);
                    return false;
                }
                return true;
            }
            return true;
        }

        public boolean removeAllSensors() {
            for (int i = 0; i < this.mActiveSensors.size(); i++) {
                if (this.mActiveSensors.valueAt(i)) {
                    int handle = this.mActiveSensors.keyAt(i);
                    Sensor sensor = (Sensor) SystemSensorManager.sHandleToSensor.get(handle);
                    if (sensor != null) {
                        disableSensor(sensor);
                        this.mActiveSensors.put(handle, false);
                        removeSensorEvent(sensor);
                    }
                }
            }
            return true;
        }

        public boolean removeSensor(Sensor sensor, boolean disable) {
            int handle = sensor.getHandle();
            if (this.mActiveSensors.get(handle)) {
                if (disable) {
                    disableSensor(sensor);
                }
                this.mActiveSensors.put(sensor.getHandle(), false);
                removeSensorEvent(sensor);
                return true;
            }
            return false;
        }

        public int flush() {
            if (this.nSensorEventQueue == 0) {
                throw new NullPointerException();
            }
            return nativeFlushSensor(this.nSensorEventQueue);
        }

        public boolean hasSensors() {
            return this.mActiveSensors.indexOfValue(true) >= 0;
        }

        protected void finalize() throws Throwable {
            try {
                dispose(true);
                super.finalize();
            } catch (Throwable th) {
                super.finalize();
                throw th;
            }
        }

        private void dispose(boolean finalized) {
            if (this.mCloseGuard != null) {
                if (finalized) {
                    this.mCloseGuard.warnIfOpen();
                }
                this.mCloseGuard.close();
            }
            if (this.nSensorEventQueue != 0) {
                nativeDestroySensorEventQueue(this.nSensorEventQueue);
                this.nSensorEventQueue = 0;
            }
        }

        private int enableSensor(Sensor sensor, int rateUs, int maxBatchReportLatencyUs, int reservedFlags) {
            if (this.nSensorEventQueue == 0) {
                throw new NullPointerException();
            }
            if (sensor == null) {
                throw new NullPointerException();
            }
            return nativeEnableSensor(this.nSensorEventQueue, sensor.getHandle(), rateUs, maxBatchReportLatencyUs, reservedFlags);
        }

        private int disableSensor(Sensor sensor) {
            if (this.nSensorEventQueue == 0) {
                throw new NullPointerException();
            }
            if (sensor == null) {
                throw new NullPointerException();
            }
            return nativeDisableSensor(this.nSensorEventQueue, sensor.getHandle());
        }
    }

    /* loaded from: SystemSensorManager$SensorEventQueue.class */
    static final class SensorEventQueue extends BaseEventQueue {
        private final SensorEventListener mListener;
        private final SparseArray<SensorEvent> mSensorsEvents;

        public SensorEventQueue(SensorEventListener listener, Looper looper, SystemSensorManager manager) {
            super(looper, manager);
            this.mSensorsEvents = new SparseArray<>();
            this.mListener = listener;
        }

        @Override // android.hardware.SystemSensorManager.BaseEventQueue
        public void addSensorEvent(Sensor sensor) {
            SensorEvent t = new SensorEvent(Sensor.getMaxLengthValuesArray(sensor, this.mManager.mTargetSdkLevel));
            this.mSensorsEvents.put(sensor.getHandle(), t);
        }

        @Override // android.hardware.SystemSensorManager.BaseEventQueue
        public void removeSensorEvent(Sensor sensor) {
            this.mSensorsEvents.delete(sensor.getHandle());
        }

        @Override // android.hardware.SystemSensorManager.BaseEventQueue
        protected void dispatchSensorEvent(int handle, float[] values, int inAccuracy, long timestamp) {
            Sensor sensor = (Sensor) SystemSensorManager.sHandleToSensor.get(handle);
            SensorEvent t = this.mSensorsEvents.get(handle);
            if (t == null) {
                Log.e("SensorManager", "Error: Sensor Event is null for Sensor: " + sensor);
                return;
            }
            System.arraycopy(values, 0, t.values, 0, t.values.length);
            t.timestamp = timestamp;
            t.accuracy = inAccuracy;
            t.sensor = sensor;
            switch (t.sensor.getType()) {
                case 2:
                case 3:
                    int accuracy = this.mSensorAccuracies.get(handle);
                    if (t.accuracy >= 0 && accuracy != t.accuracy) {
                        this.mSensorAccuracies.put(handle, t.accuracy);
                        this.mListener.onAccuracyChanged(t.sensor, t.accuracy);
                        break;
                    }
                    break;
                default:
                    if (!this.mFirstEvent.get(handle)) {
                        this.mFirstEvent.put(handle, true);
                        this.mListener.onAccuracyChanged(t.sensor, 3);
                        break;
                    }
                    break;
            }
            this.mListener.onSensorChanged(t);
        }

        @Override // android.hardware.SystemSensorManager.BaseEventQueue
        protected void dispatchFlushCompleteEvent(int handle) {
            if (this.mListener instanceof SensorEventListener2) {
                Sensor sensor = (Sensor) SystemSensorManager.sHandleToSensor.get(handle);
                ((SensorEventListener2) this.mListener).onFlushCompleted(sensor);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: SystemSensorManager$TriggerEventQueue.class */
    public static final class TriggerEventQueue extends BaseEventQueue {
        private final TriggerEventListener mListener;
        private final SparseArray<TriggerEvent> mTriggerEvents;

        public TriggerEventQueue(TriggerEventListener listener, Looper looper, SystemSensorManager manager) {
            super(looper, manager);
            this.mTriggerEvents = new SparseArray<>();
            this.mListener = listener;
        }

        @Override // android.hardware.SystemSensorManager.BaseEventQueue
        public void addSensorEvent(Sensor sensor) {
            TriggerEvent t = new TriggerEvent(Sensor.getMaxLengthValuesArray(sensor, this.mManager.mTargetSdkLevel));
            this.mTriggerEvents.put(sensor.getHandle(), t);
        }

        @Override // android.hardware.SystemSensorManager.BaseEventQueue
        public void removeSensorEvent(Sensor sensor) {
            this.mTriggerEvents.delete(sensor.getHandle());
        }

        @Override // android.hardware.SystemSensorManager.BaseEventQueue
        protected void dispatchSensorEvent(int handle, float[] values, int accuracy, long timestamp) {
            Sensor sensor = (Sensor) SystemSensorManager.sHandleToSensor.get(handle);
            TriggerEvent t = this.mTriggerEvents.get(handle);
            if (t == null) {
                Log.e("SensorManager", "Error: Trigger Event is null for Sensor: " + sensor);
                return;
            }
            System.arraycopy(values, 0, t.values, 0, t.values.length);
            t.timestamp = timestamp;
            t.sensor = sensor;
            this.mManager.cancelTriggerSensorImpl(this.mListener, sensor, false);
            this.mListener.onTrigger(t);
        }

        @Override // android.hardware.SystemSensorManager.BaseEventQueue
        protected void dispatchFlushCompleteEvent(int handle) {
        }
    }
}