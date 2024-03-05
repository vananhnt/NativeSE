package android.os;

import com.android.internal.telephony.TelephonyProperties;

/* loaded from: Build.class */
public class Build {
    public static final String UNKNOWN = "unknown";
    public static final String ID = getString("ro.build.id");
    public static final String DISPLAY = getString("ro.build.display.id");
    public static final String PRODUCT = getString("ro.product.name");
    public static final String DEVICE = getString("ro.product.device");
    public static final String BOARD = getString("ro.product.board");
    public static final String CPU_ABI = getString("ro.product.cpu.abi");
    public static final String CPU_ABI2 = getString("ro.product.cpu.abi2");
    public static final String MANUFACTURER = getString("ro.product.manufacturer");
    public static final String BRAND = getString("ro.product.brand");
    public static final String MODEL = getString("ro.product.model");
    public static final String BOOTLOADER = getString("ro.bootloader");
    @Deprecated
    public static final String RADIO = getString(TelephonyProperties.PROPERTY_BASEBAND_VERSION);
    public static final String HARDWARE = getString("ro.hardware");
    public static final String SERIAL = getString("ro.serialno");
    public static final String TYPE = getString("ro.build.type");
    public static final String TAGS = getString("ro.build.tags");
    public static final String FINGERPRINT = getString("ro.build.fingerprint");
    public static final long TIME = getLong("ro.build.date.utc") * 1000;
    public static final String USER = getString("ro.build.user");
    public static final String HOST = getString("ro.build.host");
    public static final boolean IS_DEBUGGABLE;

    /* loaded from: Build$VERSION_CODES.class */
    public static class VERSION_CODES {
        public static final int CUR_DEVELOPMENT = 10000;
        public static final int BASE = 1;
        public static final int BASE_1_1 = 2;
        public static final int CUPCAKE = 3;
        public static final int DONUT = 4;
        public static final int ECLAIR = 5;
        public static final int ECLAIR_0_1 = 6;
        public static final int ECLAIR_MR1 = 7;
        public static final int FROYO = 8;
        public static final int GINGERBREAD = 9;
        public static final int GINGERBREAD_MR1 = 10;
        public static final int HONEYCOMB = 11;
        public static final int HONEYCOMB_MR1 = 12;
        public static final int HONEYCOMB_MR2 = 13;
        public static final int ICE_CREAM_SANDWICH = 14;
        public static final int ICE_CREAM_SANDWICH_MR1 = 15;
        public static final int JELLY_BEAN = 16;
        public static final int JELLY_BEAN_MR1 = 17;
        public static final int JELLY_BEAN_MR2 = 18;
        public static final int KITKAT = 19;
    }

    static {
        IS_DEBUGGABLE = SystemProperties.getInt("ro.debuggable", 0) == 1;
    }

    /* loaded from: Build$VERSION.class */
    public static class VERSION {
        public static final String INCREMENTAL = Build.getString("ro.build.version.incremental");
        public static final String RELEASE = Build.getString("ro.build.version.release");
        @Deprecated
        public static final String SDK = Build.getString("ro.build.version.sdk");
        public static final int SDK_INT = SystemProperties.getInt("ro.build.version.sdk", 0);
        public static final String CODENAME = Build.getString("ro.build.version.codename");
        public static final int RESOURCES_SDK_INT;

        static {
            RESOURCES_SDK_INT = SDK_INT + ("REL".equals(CODENAME) ? 0 : 1);
        }
    }

    public static String getRadioVersion() {
        return SystemProperties.get(TelephonyProperties.PROPERTY_BASEBAND_VERSION, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String getString(String property) {
        return SystemProperties.get(property, "unknown");
    }

    private static long getLong(String property) {
        try {
            return Long.parseLong(SystemProperties.get(property));
        } catch (NumberFormatException e) {
            return -1L;
        }
    }
}