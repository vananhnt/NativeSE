package android.hardware.camera2;

import android.hardware.camera2.impl.CameraMetadataNative;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* loaded from: CameraMetadata.class */
public abstract class CameraMetadata {
    public static final int LENS_FACING_FRONT = 0;
    public static final int LENS_FACING_BACK = 1;
    public static final int LED_AVAILABLE_LEDS_TRANSMIT = 0;
    public static final int INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED = 0;
    public static final int INFO_SUPPORTED_HARDWARE_LEVEL_FULL = 1;
    public static final int COLOR_CORRECTION_MODE_TRANSFORM_MATRIX = 0;
    public static final int COLOR_CORRECTION_MODE_FAST = 1;
    public static final int COLOR_CORRECTION_MODE_HIGH_QUALITY = 2;
    public static final int CONTROL_AE_ANTIBANDING_MODE_OFF = 0;
    public static final int CONTROL_AE_ANTIBANDING_MODE_50HZ = 1;
    public static final int CONTROL_AE_ANTIBANDING_MODE_60HZ = 2;
    public static final int CONTROL_AE_ANTIBANDING_MODE_AUTO = 3;
    public static final int CONTROL_AE_MODE_OFF = 0;
    public static final int CONTROL_AE_MODE_ON = 1;
    public static final int CONTROL_AE_MODE_ON_AUTO_FLASH = 2;
    public static final int CONTROL_AE_MODE_ON_ALWAYS_FLASH = 3;
    public static final int CONTROL_AE_MODE_ON_AUTO_FLASH_REDEYE = 4;
    public static final int CONTROL_AE_PRECAPTURE_TRIGGER_IDLE = 0;
    public static final int CONTROL_AE_PRECAPTURE_TRIGGER_START = 1;
    public static final int CONTROL_AF_MODE_OFF = 0;
    public static final int CONTROL_AF_MODE_AUTO = 1;
    public static final int CONTROL_AF_MODE_MACRO = 2;
    public static final int CONTROL_AF_MODE_CONTINUOUS_VIDEO = 3;
    public static final int CONTROL_AF_MODE_CONTINUOUS_PICTURE = 4;
    public static final int CONTROL_AF_MODE_EDOF = 5;
    public static final int CONTROL_AF_TRIGGER_IDLE = 0;
    public static final int CONTROL_AF_TRIGGER_START = 1;
    public static final int CONTROL_AF_TRIGGER_CANCEL = 2;
    public static final int CONTROL_AWB_MODE_OFF = 0;
    public static final int CONTROL_AWB_MODE_AUTO = 1;
    public static final int CONTROL_AWB_MODE_INCANDESCENT = 2;
    public static final int CONTROL_AWB_MODE_FLUORESCENT = 3;
    public static final int CONTROL_AWB_MODE_WARM_FLUORESCENT = 4;
    public static final int CONTROL_AWB_MODE_DAYLIGHT = 5;
    public static final int CONTROL_AWB_MODE_CLOUDY_DAYLIGHT = 6;
    public static final int CONTROL_AWB_MODE_TWILIGHT = 7;
    public static final int CONTROL_AWB_MODE_SHADE = 8;
    public static final int CONTROL_CAPTURE_INTENT_CUSTOM = 0;
    public static final int CONTROL_CAPTURE_INTENT_PREVIEW = 1;
    public static final int CONTROL_CAPTURE_INTENT_STILL_CAPTURE = 2;
    public static final int CONTROL_CAPTURE_INTENT_VIDEO_RECORD = 3;
    public static final int CONTROL_CAPTURE_INTENT_VIDEO_SNAPSHOT = 4;
    public static final int CONTROL_CAPTURE_INTENT_ZERO_SHUTTER_LAG = 5;
    public static final int CONTROL_EFFECT_MODE_OFF = 0;
    public static final int CONTROL_EFFECT_MODE_MONO = 1;
    public static final int CONTROL_EFFECT_MODE_NEGATIVE = 2;
    public static final int CONTROL_EFFECT_MODE_SOLARIZE = 3;
    public static final int CONTROL_EFFECT_MODE_SEPIA = 4;
    public static final int CONTROL_EFFECT_MODE_POSTERIZE = 5;
    public static final int CONTROL_EFFECT_MODE_WHITEBOARD = 6;
    public static final int CONTROL_EFFECT_MODE_BLACKBOARD = 7;
    public static final int CONTROL_EFFECT_MODE_AQUA = 8;
    public static final int CONTROL_MODE_OFF = 0;
    public static final int CONTROL_MODE_AUTO = 1;
    public static final int CONTROL_MODE_USE_SCENE_MODE = 2;
    public static final int CONTROL_SCENE_MODE_UNSUPPORTED = 0;
    public static final int CONTROL_SCENE_MODE_FACE_PRIORITY = 1;
    public static final int CONTROL_SCENE_MODE_ACTION = 2;
    public static final int CONTROL_SCENE_MODE_PORTRAIT = 3;
    public static final int CONTROL_SCENE_MODE_LANDSCAPE = 4;
    public static final int CONTROL_SCENE_MODE_NIGHT = 5;
    public static final int CONTROL_SCENE_MODE_NIGHT_PORTRAIT = 6;
    public static final int CONTROL_SCENE_MODE_THEATRE = 7;
    public static final int CONTROL_SCENE_MODE_BEACH = 8;
    public static final int CONTROL_SCENE_MODE_SNOW = 9;
    public static final int CONTROL_SCENE_MODE_SUNSET = 10;
    public static final int CONTROL_SCENE_MODE_STEADYPHOTO = 11;
    public static final int CONTROL_SCENE_MODE_FIREWORKS = 12;
    public static final int CONTROL_SCENE_MODE_SPORTS = 13;
    public static final int CONTROL_SCENE_MODE_PARTY = 14;
    public static final int CONTROL_SCENE_MODE_CANDLELIGHT = 15;
    public static final int CONTROL_SCENE_MODE_BARCODE = 16;
    public static final int EDGE_MODE_OFF = 0;
    public static final int EDGE_MODE_FAST = 1;
    public static final int EDGE_MODE_HIGH_QUALITY = 2;
    public static final int FLASH_MODE_OFF = 0;
    public static final int FLASH_MODE_SINGLE = 1;
    public static final int FLASH_MODE_TORCH = 2;
    public static final int LENS_OPTICAL_STABILIZATION_MODE_OFF = 0;
    public static final int LENS_OPTICAL_STABILIZATION_MODE_ON = 1;
    public static final int NOISE_REDUCTION_MODE_OFF = 0;
    public static final int NOISE_REDUCTION_MODE_FAST = 1;
    public static final int NOISE_REDUCTION_MODE_HIGH_QUALITY = 2;
    public static final int STATISTICS_FACE_DETECT_MODE_OFF = 0;
    public static final int STATISTICS_FACE_DETECT_MODE_SIMPLE = 1;
    public static final int STATISTICS_FACE_DETECT_MODE_FULL = 2;
    public static final int STATISTICS_LENS_SHADING_MAP_MODE_OFF = 0;
    public static final int STATISTICS_LENS_SHADING_MAP_MODE_ON = 1;
    public static final int TONEMAP_MODE_CONTRAST_CURVE = 0;
    public static final int TONEMAP_MODE_FAST = 1;
    public static final int TONEMAP_MODE_HIGH_QUALITY = 2;
    public static final int CONTROL_AE_STATE_INACTIVE = 0;
    public static final int CONTROL_AE_STATE_SEARCHING = 1;
    public static final int CONTROL_AE_STATE_CONVERGED = 2;
    public static final int CONTROL_AE_STATE_LOCKED = 3;
    public static final int CONTROL_AE_STATE_FLASH_REQUIRED = 4;
    public static final int CONTROL_AE_STATE_PRECAPTURE = 5;
    public static final int CONTROL_AF_STATE_INACTIVE = 0;
    public static final int CONTROL_AF_STATE_PASSIVE_SCAN = 1;
    public static final int CONTROL_AF_STATE_PASSIVE_FOCUSED = 2;
    public static final int CONTROL_AF_STATE_ACTIVE_SCAN = 3;
    public static final int CONTROL_AF_STATE_FOCUSED_LOCKED = 4;
    public static final int CONTROL_AF_STATE_NOT_FOCUSED_LOCKED = 5;
    public static final int CONTROL_AF_STATE_PASSIVE_UNFOCUSED = 6;
    public static final int CONTROL_AWB_STATE_INACTIVE = 0;
    public static final int CONTROL_AWB_STATE_SEARCHING = 1;
    public static final int CONTROL_AWB_STATE_CONVERGED = 2;
    public static final int CONTROL_AWB_STATE_LOCKED = 3;
    public static final int FLASH_STATE_UNAVAILABLE = 0;
    public static final int FLASH_STATE_CHARGING = 1;
    public static final int FLASH_STATE_READY = 2;
    public static final int FLASH_STATE_FIRED = 3;
    public static final int LENS_STATE_STATIONARY = 0;
    public static final int LENS_STATE_MOVING = 1;
    public static final int STATISTICS_SCENE_FLICKER_NONE = 0;
    public static final int STATISTICS_SCENE_FLICKER_50HZ = 1;
    public static final int STATISTICS_SCENE_FLICKER_60HZ = 2;

    public abstract <T> T get(Key<T> key);

    public List<Key<?>> getKeys() {
        return Collections.unmodifiableList(getKeysStatic(getClass(), this));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ArrayList<Key<?>> getKeysStatic(Class<? extends CameraMetadata> type, CameraMetadata instance) {
        ArrayList<Key<?>> keyList = new ArrayList<>();
        Field[] fields = type.getDeclaredFields();
        for (Field field : fields) {
            if (field.getType().isAssignableFrom(Key.class) && (field.getModifiers() & 1) != 0) {
                try {
                    Key<?> key = (Key) field.get(instance);
                    if (instance == null || instance.get(key) != null) {
                        keyList.add(key);
                    }
                } catch (IllegalAccessException e) {
                    throw new AssertionError("Can't get IllegalAccessException", e);
                } catch (IllegalArgumentException e2) {
                    throw new AssertionError("Can't get IllegalArgumentException", e2);
                }
            }
        }
        return keyList;
    }

    /* loaded from: CameraMetadata$Key.class */
    public static class Key<T> {
        private boolean mHasTag;
        private int mTag;
        private final Class<T> mType;
        private final String mName;

        public Key(String name, Class<T> type) {
            if (name == null) {
                throw new NullPointerException("Key needs a valid name");
            }
            if (type == null) {
                throw new NullPointerException("Type needs to be non-null");
            }
            this.mName = name;
            this.mType = type;
        }

        public final String getName() {
            return this.mName;
        }

        public final int hashCode() {
            return this.mName.hashCode();
        }

        public final boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Key)) {
                return false;
            }
            Key lhs = (Key) o;
            return this.mName.equals(lhs.mName);
        }

        public final int getTag() {
            if (!this.mHasTag) {
                this.mTag = CameraMetadataNative.getTag(this.mName);
                this.mHasTag = true;
            }
            return this.mTag;
        }

        public final Class<T> getType() {
            return this.mType;
        }
    }
}