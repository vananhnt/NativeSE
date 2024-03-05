package android.hardware;

/* loaded from: Sensor.class */
public final class Sensor {
    public static final int TYPE_ACCELEROMETER = 1;
    public static final int TYPE_MAGNETIC_FIELD = 2;
    @Deprecated
    public static final int TYPE_ORIENTATION = 3;
    public static final int TYPE_GYROSCOPE = 4;
    public static final int TYPE_LIGHT = 5;
    public static final int TYPE_PRESSURE = 6;
    @Deprecated
    public static final int TYPE_TEMPERATURE = 7;
    public static final int TYPE_PROXIMITY = 8;
    public static final int TYPE_GRAVITY = 9;
    public static final int TYPE_LINEAR_ACCELERATION = 10;
    public static final int TYPE_ROTATION_VECTOR = 11;
    public static final int TYPE_RELATIVE_HUMIDITY = 12;
    public static final int TYPE_AMBIENT_TEMPERATURE = 13;
    public static final int TYPE_MAGNETIC_FIELD_UNCALIBRATED = 14;
    public static final int TYPE_GAME_ROTATION_VECTOR = 15;
    public static final int TYPE_GYROSCOPE_UNCALIBRATED = 16;
    public static final int TYPE_SIGNIFICANT_MOTION = 17;
    public static final int TYPE_STEP_DETECTOR = 18;
    public static final int TYPE_STEP_COUNTER = 19;
    public static final int TYPE_GEOMAGNETIC_ROTATION_VECTOR = 20;
    public static final int TYPE_ALL = -1;
    static int REPORTING_MODE_CONTINUOUS = 1;
    static int REPORTING_MODE_ON_CHANGE = 2;
    static int REPORTING_MODE_ONE_SHOT = 3;
    private static final int[] sSensorReportingModes = {0, 0, REPORTING_MODE_CONTINUOUS, 3, REPORTING_MODE_CONTINUOUS, 3, REPORTING_MODE_CONTINUOUS, 3, REPORTING_MODE_CONTINUOUS, 3, REPORTING_MODE_ON_CHANGE, 3, REPORTING_MODE_CONTINUOUS, 3, REPORTING_MODE_ON_CHANGE, 3, REPORTING_MODE_ON_CHANGE, 3, REPORTING_MODE_CONTINUOUS, 3, REPORTING_MODE_CONTINUOUS, 3, REPORTING_MODE_CONTINUOUS, 5, REPORTING_MODE_ON_CHANGE, 3, REPORTING_MODE_ON_CHANGE, 3, REPORTING_MODE_CONTINUOUS, 6, REPORTING_MODE_CONTINUOUS, 4, REPORTING_MODE_CONTINUOUS, 6, REPORTING_MODE_ONE_SHOT, 1, REPORTING_MODE_ON_CHANGE, 1, REPORTING_MODE_ON_CHANGE, 1, REPORTING_MODE_CONTINUOUS, 5};
    private String mName;
    private String mVendor;
    private int mVersion;
    private int mHandle;
    private int mType;
    private float mMaxRange;
    private float mResolution;
    private float mPower;
    private int mMinDelay;
    private int mFifoReservedEventCount;
    private int mFifoMaxEventCount;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getReportingMode(Sensor sensor) {
        int offset = sensor.mType * 2;
        if (offset >= sSensorReportingModes.length) {
            int minDelay = sensor.mMinDelay;
            if (minDelay == 0) {
                return REPORTING_MODE_ON_CHANGE;
            }
            if (minDelay < 0) {
                return REPORTING_MODE_ONE_SHOT;
            }
            return REPORTING_MODE_CONTINUOUS;
        }
        return sSensorReportingModes[offset];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getMaxLengthValuesArray(Sensor sensor, int sdkLevel) {
        int type = sensor.mType;
        if (type == 11 && sdkLevel <= 17) {
            return 3;
        }
        int offset = (type * 2) + 1;
        if (offset >= sSensorReportingModes.length) {
            return 16;
        }
        return sSensorReportingModes[offset];
    }

    public String getName() {
        return this.mName;
    }

    public String getVendor() {
        return this.mVendor;
    }

    public int getType() {
        return this.mType;
    }

    public int getVersion() {
        return this.mVersion;
    }

    public float getMaximumRange() {
        return this.mMaxRange;
    }

    public float getResolution() {
        return this.mResolution;
    }

    public float getPower() {
        return this.mPower;
    }

    public int getMinDelay() {
        return this.mMinDelay;
    }

    public int getFifoReservedEventCount() {
        return this.mFifoReservedEventCount;
    }

    public int getFifoMaxEventCount() {
        return this.mFifoMaxEventCount;
    }

    public int getHandle() {
        return this.mHandle;
    }

    void setRange(float max, float res) {
        this.mMaxRange = max;
        this.mResolution = res;
    }

    public String toString() {
        return "{Sensor name=\"" + this.mName + "\", vendor=\"" + this.mVendor + "\", version=" + this.mVersion + ", type=" + this.mType + ", maxRange=" + this.mMaxRange + ", resolution=" + this.mResolution + ", power=" + this.mPower + ", minDelay=" + this.mMinDelay + "}";
    }
}