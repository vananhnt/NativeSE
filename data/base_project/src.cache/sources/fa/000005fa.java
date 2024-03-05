package android.hardware;

import android.content.Context;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.view.IRotationWatcher;
import android.view.IWindowManager;
import java.util.HashMap;
import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: LegacySensorManager.class */
public final class LegacySensorManager {
    private static boolean sInitialized;
    private static IWindowManager sWindowManager;
    private static int sRotation = 0;
    private final SensorManager mSensorManager;
    private final HashMap<SensorListener, LegacyListener> mLegacyListenersMap = new HashMap<>();

    public LegacySensorManager(SensorManager sensorManager) {
        this.mSensorManager = sensorManager;
        synchronized (SensorManager.class) {
            if (!sInitialized) {
                sWindowManager = IWindowManager.Stub.asInterface(ServiceManager.getService(Context.WINDOW_SERVICE));
                if (sWindowManager != null) {
                    try {
                        sRotation = sWindowManager.watchRotation(new IRotationWatcher.Stub() { // from class: android.hardware.LegacySensorManager.1
                            @Override // android.view.IRotationWatcher
                            public void onRotationChanged(int rotation) {
                                LegacySensorManager.onRotationChanged(rotation);
                            }
                        });
                    } catch (RemoteException e) {
                    }
                }
            }
        }
    }

    public int getSensors() {
        int result = 0;
        List<Sensor> fullList = this.mSensorManager.getFullSensorList();
        for (Sensor i : fullList) {
            switch (i.getType()) {
                case 1:
                    result |= 2;
                    break;
                case 2:
                    result |= 8;
                    break;
                case 3:
                    result |= 129;
                    break;
            }
        }
        return result;
    }

    public boolean registerListener(SensorListener listener, int sensors, int rate) {
        if (listener == null) {
            return false;
        }
        boolean result = registerLegacyListener(2, 1, listener, sensors, rate) || 0 != 0;
        boolean result2 = registerLegacyListener(8, 2, listener, sensors, rate) || result;
        boolean result3 = registerLegacyListener(128, 3, listener, sensors, rate) || result2;
        boolean result4 = registerLegacyListener(1, 3, listener, sensors, rate) || result3;
        boolean result5 = registerLegacyListener(4, 7, listener, sensors, rate) || result4;
        return result5;
    }

    private boolean registerLegacyListener(int legacyType, int type, SensorListener listener, int sensors, int rate) {
        Sensor sensor;
        boolean result = false;
        if ((sensors & legacyType) != 0 && (sensor = this.mSensorManager.getDefaultSensor(type)) != null) {
            synchronized (this.mLegacyListenersMap) {
                LegacyListener legacyListener = this.mLegacyListenersMap.get(listener);
                if (legacyListener == null) {
                    legacyListener = new LegacyListener(listener);
                    this.mLegacyListenersMap.put(listener, legacyListener);
                }
                if (legacyListener.registerSensor(legacyType)) {
                    result = this.mSensorManager.registerListener(legacyListener, sensor, rate);
                } else {
                    result = true;
                }
            }
        }
        return result;
    }

    public void unregisterListener(SensorListener listener, int sensors) {
        if (listener == null) {
            return;
        }
        unregisterLegacyListener(2, 1, listener, sensors);
        unregisterLegacyListener(8, 2, listener, sensors);
        unregisterLegacyListener(128, 3, listener, sensors);
        unregisterLegacyListener(1, 3, listener, sensors);
        unregisterLegacyListener(4, 7, listener, sensors);
    }

    private void unregisterLegacyListener(int legacyType, int type, SensorListener listener, int sensors) {
        Sensor sensor;
        if ((sensors & legacyType) != 0 && (sensor = this.mSensorManager.getDefaultSensor(type)) != null) {
            synchronized (this.mLegacyListenersMap) {
                LegacyListener legacyListener = this.mLegacyListenersMap.get(listener);
                if (legacyListener != null && legacyListener.unregisterSensor(legacyType)) {
                    this.mSensorManager.unregisterListener(legacyListener, sensor);
                    if (!legacyListener.hasSensors()) {
                        this.mLegacyListenersMap.remove(listener);
                    }
                }
            }
        }
    }

    static void onRotationChanged(int rotation) {
        synchronized (SensorManager.class) {
            sRotation = rotation;
        }
    }

    static int getRotation() {
        int i;
        synchronized (SensorManager.class) {
            i = sRotation;
        }
        return i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: LegacySensorManager$LegacyListener.class */
    public static final class LegacyListener implements SensorEventListener {
        private SensorListener mTarget;
        private float[] mValues = new float[6];
        private final LmsFilter mYawfilter = new LmsFilter();
        private int mSensors = 0;

        LegacyListener(SensorListener target) {
            this.mTarget = target;
        }

        boolean registerSensor(int legacyType) {
            if ((this.mSensors & legacyType) != 0) {
                return false;
            }
            boolean alreadyHasOrientationSensor = hasOrientationSensor(this.mSensors);
            this.mSensors |= legacyType;
            if (alreadyHasOrientationSensor && hasOrientationSensor(legacyType)) {
                return false;
            }
            return true;
        }

        boolean unregisterSensor(int legacyType) {
            if ((this.mSensors & legacyType) == 0) {
                return false;
            }
            this.mSensors &= legacyType ^ (-1);
            if (hasOrientationSensor(legacyType) && hasOrientationSensor(this.mSensors)) {
                return false;
            }
            return true;
        }

        boolean hasSensors() {
            return this.mSensors != 0;
        }

        private static boolean hasOrientationSensor(int sensors) {
            return (sensors & 129) != 0;
        }

        @Override // android.hardware.SensorEventListener
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            try {
                this.mTarget.onAccuracyChanged(getLegacySensorType(sensor.getType()), accuracy);
            } catch (AbstractMethodError e) {
            }
        }

        @Override // android.hardware.SensorEventListener
        public void onSensorChanged(SensorEvent event) {
            float[] v = this.mValues;
            v[0] = event.values[0];
            v[1] = event.values[1];
            v[2] = event.values[2];
            int type = event.sensor.getType();
            int legacyType = getLegacySensorType(type);
            mapSensorDataToWindow(legacyType, v, LegacySensorManager.getRotation());
            if (type == 3) {
                if ((this.mSensors & 128) != 0) {
                    this.mTarget.onSensorChanged(128, v);
                }
                if ((this.mSensors & 1) != 0) {
                    v[0] = this.mYawfilter.filter(event.timestamp, v[0]);
                    this.mTarget.onSensorChanged(1, v);
                    return;
                }
                return;
            }
            this.mTarget.onSensorChanged(legacyType, v);
        }

        private void mapSensorDataToWindow(int sensor, float[] values, int orientation) {
            float x = values[0];
            float y = values[1];
            float z = values[2];
            switch (sensor) {
                case 1:
                case 128:
                    z = -z;
                    break;
                case 2:
                    x = -x;
                    y = -y;
                    z = -z;
                    break;
                case 8:
                    x = -x;
                    y = -y;
                    break;
            }
            values[0] = x;
            values[1] = y;
            values[2] = z;
            values[3] = x;
            values[4] = y;
            values[5] = z;
            if ((orientation & 1) != 0) {
                switch (sensor) {
                    case 1:
                    case 128:
                        values[0] = x + (x < 270.0f ? 90 : -270);
                        values[1] = z;
                        values[2] = y;
                        break;
                    case 2:
                    case 8:
                        values[0] = -y;
                        values[1] = x;
                        values[2] = z;
                        break;
                }
            }
            if ((orientation & 2) != 0) {
                float x2 = values[0];
                float y2 = values[1];
                float z2 = values[2];
                switch (sensor) {
                    case 1:
                    case 128:
                        values[0] = x2 >= 180.0f ? x2 - 180.0f : x2 + 180.0f;
                        values[1] = -y2;
                        values[2] = -z2;
                        return;
                    case 2:
                    case 8:
                        values[0] = -x2;
                        values[1] = -y2;
                        values[2] = z2;
                        return;
                    default:
                        return;
                }
            }
        }

        private static int getLegacySensorType(int type) {
            switch (type) {
                case 1:
                    return 2;
                case 2:
                    return 8;
                case 3:
                    return 128;
                case 4:
                case 5:
                case 6:
                default:
                    return 0;
                case 7:
                    return 4;
            }
        }
    }

    /* loaded from: LegacySensorManager$LmsFilter.class */
    private static final class LmsFilter {
        private static final int SENSORS_RATE_MS = 20;
        private static final int COUNT = 12;
        private static final float PREDICTION_RATIO = 0.33333334f;
        private static final float PREDICTION_TIME = 0.08f;
        private float[] mV = new float[24];
        private long[] mT = new long[24];
        private int mIndex = 12;

        public float filter(long time, float in) {
            float v = in;
            float v1 = this.mV[this.mIndex];
            if (v - v1 > 180.0f) {
                v -= 360.0f;
            } else if (v1 - v > 180.0f) {
                v += 360.0f;
            }
            this.mIndex++;
            if (this.mIndex >= 24) {
                this.mIndex = 12;
            }
            this.mV[this.mIndex] = v;
            this.mT[this.mIndex] = time;
            this.mV[this.mIndex - 12] = v;
            this.mT[this.mIndex - 12] = time;
            float E = 0.0f;
            float D = 0.0f;
            float C = 0.0f;
            float B = 0.0f;
            float A = 0.0f;
            for (int i = 0; i < 11; i++) {
                int j = (this.mIndex - 1) - i;
                float Z = this.mV[j];
                float T = ((float) (((this.mT[j] / 2) + (this.mT[j + 1] / 2)) - time)) * 1.0E-9f;
                float dT = ((float) (this.mT[j] - this.mT[j + 1])) * 1.0E-9f;
                float dT2 = dT * dT;
                A += Z * dT2;
                B += T * T * dT2;
                C += T * dT2;
                D += Z * T * dT2;
                E += dT2;
            }
            float b = ((A * B) + (C * D)) / ((E * B) + (C * C));
            float a = ((E * b) - A) / C;
            float f = (b + (PREDICTION_TIME * a)) * 0.0027777778f;
            if ((f >= 0.0f ? f : -f) >= 0.5f) {
                f = (f - ((float) Math.ceil(f + 0.5f))) + 1.0f;
            }
            if (f < 0.0f) {
                f += 1.0f;
            }
            return f * 360.0f;
        }
    }
}