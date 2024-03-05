package android.hardware.camera2;

import android.graphics.Rect;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.impl.CameraMetadataNative;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.Surface;
import java.util.HashSet;
import java.util.Objects;

/* loaded from: CaptureRequest.class */
public final class CaptureRequest extends CameraMetadata implements Parcelable {
    private final HashSet<Surface> mSurfaceSet;
    private final CameraMetadataNative mSettings;
    private Object mUserTag;
    public static final Parcelable.Creator<CaptureRequest> CREATOR = new Parcelable.Creator<CaptureRequest>() { // from class: android.hardware.camera2.CaptureRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CaptureRequest createFromParcel(Parcel in) {
            CaptureRequest request = new CaptureRequest();
            request.readFromParcel(in);
            return request;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CaptureRequest[] newArray(int size) {
            return new CaptureRequest[size];
        }
    };
    public static final CameraMetadata.Key<Integer> COLOR_CORRECTION_MODE = new CameraMetadata.Key<>("android.colorCorrection.mode", Integer.TYPE);
    public static final CameraMetadata.Key<Rational[]> COLOR_CORRECTION_TRANSFORM = new CameraMetadata.Key<>("android.colorCorrection.transform", Rational[].class);
    public static final CameraMetadata.Key<float[]> COLOR_CORRECTION_GAINS = new CameraMetadata.Key<>("android.colorCorrection.gains", float[].class);
    public static final CameraMetadata.Key<Integer> CONTROL_AE_ANTIBANDING_MODE = new CameraMetadata.Key<>("android.control.aeAntibandingMode", Integer.TYPE);
    public static final CameraMetadata.Key<Integer> CONTROL_AE_EXPOSURE_COMPENSATION = new CameraMetadata.Key<>("android.control.aeExposureCompensation", Integer.TYPE);
    public static final CameraMetadata.Key<Boolean> CONTROL_AE_LOCK = new CameraMetadata.Key<>("android.control.aeLock", Boolean.TYPE);
    public static final CameraMetadata.Key<Integer> CONTROL_AE_MODE = new CameraMetadata.Key<>("android.control.aeMode", Integer.TYPE);
    public static final CameraMetadata.Key<int[]> CONTROL_AE_REGIONS = new CameraMetadata.Key<>("android.control.aeRegions", int[].class);
    public static final CameraMetadata.Key<int[]> CONTROL_AE_TARGET_FPS_RANGE = new CameraMetadata.Key<>("android.control.aeTargetFpsRange", int[].class);
    public static final CameraMetadata.Key<Integer> CONTROL_AE_PRECAPTURE_TRIGGER = new CameraMetadata.Key<>("android.control.aePrecaptureTrigger", Integer.TYPE);
    public static final CameraMetadata.Key<Integer> CONTROL_AF_MODE = new CameraMetadata.Key<>("android.control.afMode", Integer.TYPE);
    public static final CameraMetadata.Key<int[]> CONTROL_AF_REGIONS = new CameraMetadata.Key<>("android.control.afRegions", int[].class);
    public static final CameraMetadata.Key<Integer> CONTROL_AF_TRIGGER = new CameraMetadata.Key<>("android.control.afTrigger", Integer.TYPE);
    public static final CameraMetadata.Key<Boolean> CONTROL_AWB_LOCK = new CameraMetadata.Key<>("android.control.awbLock", Boolean.TYPE);
    public static final CameraMetadata.Key<Integer> CONTROL_AWB_MODE = new CameraMetadata.Key<>("android.control.awbMode", Integer.TYPE);
    public static final CameraMetadata.Key<int[]> CONTROL_AWB_REGIONS = new CameraMetadata.Key<>("android.control.awbRegions", int[].class);
    public static final CameraMetadata.Key<Integer> CONTROL_CAPTURE_INTENT = new CameraMetadata.Key<>("android.control.captureIntent", Integer.TYPE);
    public static final CameraMetadata.Key<Integer> CONTROL_EFFECT_MODE = new CameraMetadata.Key<>("android.control.effectMode", Integer.TYPE);
    public static final CameraMetadata.Key<Integer> CONTROL_MODE = new CameraMetadata.Key<>("android.control.mode", Integer.TYPE);
    public static final CameraMetadata.Key<Integer> CONTROL_SCENE_MODE = new CameraMetadata.Key<>("android.control.sceneMode", Integer.TYPE);
    public static final CameraMetadata.Key<Boolean> CONTROL_VIDEO_STABILIZATION_MODE = new CameraMetadata.Key<>("android.control.videoStabilizationMode", Boolean.TYPE);
    public static final CameraMetadata.Key<Integer> EDGE_MODE = new CameraMetadata.Key<>("android.edge.mode", Integer.TYPE);
    public static final CameraMetadata.Key<Integer> FLASH_MODE = new CameraMetadata.Key<>("android.flash.mode", Integer.TYPE);
    public static final CameraMetadata.Key<double[]> JPEG_GPS_COORDINATES = new CameraMetadata.Key<>("android.jpeg.gpsCoordinates", double[].class);
    public static final CameraMetadata.Key<String> JPEG_GPS_PROCESSING_METHOD = new CameraMetadata.Key<>("android.jpeg.gpsProcessingMethod", String.class);
    public static final CameraMetadata.Key<Long> JPEG_GPS_TIMESTAMP = new CameraMetadata.Key<>("android.jpeg.gpsTimestamp", Long.TYPE);
    public static final CameraMetadata.Key<Integer> JPEG_ORIENTATION = new CameraMetadata.Key<>("android.jpeg.orientation", Integer.TYPE);
    public static final CameraMetadata.Key<Byte> JPEG_QUALITY = new CameraMetadata.Key<>("android.jpeg.quality", Byte.TYPE);
    public static final CameraMetadata.Key<Byte> JPEG_THUMBNAIL_QUALITY = new CameraMetadata.Key<>("android.jpeg.thumbnailQuality", Byte.TYPE);
    public static final CameraMetadata.Key<Size> JPEG_THUMBNAIL_SIZE = new CameraMetadata.Key<>("android.jpeg.thumbnailSize", Size.class);
    public static final CameraMetadata.Key<Float> LENS_APERTURE = new CameraMetadata.Key<>("android.lens.aperture", Float.TYPE);
    public static final CameraMetadata.Key<Float> LENS_FILTER_DENSITY = new CameraMetadata.Key<>("android.lens.filterDensity", Float.TYPE);
    public static final CameraMetadata.Key<Float> LENS_FOCAL_LENGTH = new CameraMetadata.Key<>("android.lens.focalLength", Float.TYPE);
    public static final CameraMetadata.Key<Float> LENS_FOCUS_DISTANCE = new CameraMetadata.Key<>("android.lens.focusDistance", Float.TYPE);
    public static final CameraMetadata.Key<Integer> LENS_OPTICAL_STABILIZATION_MODE = new CameraMetadata.Key<>("android.lens.opticalStabilizationMode", Integer.TYPE);
    public static final CameraMetadata.Key<Integer> NOISE_REDUCTION_MODE = new CameraMetadata.Key<>("android.noiseReduction.mode", Integer.TYPE);
    public static final CameraMetadata.Key<Integer> REQUEST_ID = new CameraMetadata.Key<>("android.request.id", Integer.TYPE);
    public static final CameraMetadata.Key<Rect> SCALER_CROP_REGION = new CameraMetadata.Key<>("android.scaler.cropRegion", Rect.class);
    public static final CameraMetadata.Key<Long> SENSOR_EXPOSURE_TIME = new CameraMetadata.Key<>("android.sensor.exposureTime", Long.TYPE);
    public static final CameraMetadata.Key<Long> SENSOR_FRAME_DURATION = new CameraMetadata.Key<>("android.sensor.frameDuration", Long.TYPE);
    public static final CameraMetadata.Key<Integer> SENSOR_SENSITIVITY = new CameraMetadata.Key<>("android.sensor.sensitivity", Integer.TYPE);
    public static final CameraMetadata.Key<Integer> STATISTICS_FACE_DETECT_MODE = new CameraMetadata.Key<>("android.statistics.faceDetectMode", Integer.TYPE);
    public static final CameraMetadata.Key<Integer> STATISTICS_LENS_SHADING_MAP_MODE = new CameraMetadata.Key<>("android.statistics.lensShadingMapMode", Integer.TYPE);
    public static final CameraMetadata.Key<float[]> TONEMAP_CURVE_BLUE = new CameraMetadata.Key<>("android.tonemap.curveBlue", float[].class);
    public static final CameraMetadata.Key<float[]> TONEMAP_CURVE_GREEN = new CameraMetadata.Key<>("android.tonemap.curveGreen", float[].class);
    public static final CameraMetadata.Key<float[]> TONEMAP_CURVE_RED = new CameraMetadata.Key<>("android.tonemap.curveRed", float[].class);
    public static final CameraMetadata.Key<Integer> TONEMAP_MODE = new CameraMetadata.Key<>("android.tonemap.mode", Integer.TYPE);
    public static final CameraMetadata.Key<Boolean> LED_TRANSMIT = new CameraMetadata.Key<>("android.led.transmit", Boolean.TYPE);
    public static final CameraMetadata.Key<Boolean> BLACK_LEVEL_LOCK = new CameraMetadata.Key<>("android.blackLevel.lock", Boolean.TYPE);

    private CaptureRequest() {
        this.mSettings = new CameraMetadataNative();
        this.mSurfaceSet = new HashSet<>();
    }

    private CaptureRequest(CaptureRequest source) {
        this.mSettings = new CameraMetadataNative(source.mSettings);
        this.mSurfaceSet = (HashSet) source.mSurfaceSet.clone();
        this.mUserTag = source.mUserTag;
    }

    private CaptureRequest(CameraMetadataNative settings) {
        this.mSettings = settings;
        this.mSurfaceSet = new HashSet<>();
    }

    @Override // android.hardware.camera2.CameraMetadata
    public <T> T get(CameraMetadata.Key<T> key) {
        return (T) this.mSettings.get(key);
    }

    public Object getTag() {
        return this.mUserTag;
    }

    public boolean equals(Object other) {
        return (other instanceof CaptureRequest) && equals((CaptureRequest) other);
    }

    private boolean equals(CaptureRequest other) {
        return other != null && Objects.equals(this.mUserTag, other.mUserTag) && this.mSurfaceSet.equals(other.mSurfaceSet) && this.mSettings.equals(other.mSettings);
    }

    public int hashCode() {
        return this.mSettings.hashCode();
    }

    public void readFromParcel(Parcel in) {
        this.mSettings.readFromParcel(in);
        this.mSurfaceSet.clear();
        Parcelable[] parcelableArray = in.readParcelableArray(Surface.class.getClassLoader());
        if (parcelableArray == null) {
            return;
        }
        for (Parcelable p : parcelableArray) {
            Surface s = (Surface) p;
            this.mSurfaceSet.add(s);
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        this.mSettings.writeToParcel(dest, flags);
        dest.writeParcelableArray((Parcelable[]) this.mSurfaceSet.toArray(new Surface[this.mSurfaceSet.size()]), flags);
    }

    /* loaded from: CaptureRequest$Builder.class */
    public static final class Builder {
        private final CaptureRequest mRequest;

        public Builder(CameraMetadataNative template) {
            this.mRequest = new CaptureRequest(template);
        }

        public void addTarget(Surface outputTarget) {
            this.mRequest.mSurfaceSet.add(outputTarget);
        }

        public void removeTarget(Surface outputTarget) {
            this.mRequest.mSurfaceSet.remove(outputTarget);
        }

        public <T> void set(CameraMetadata.Key<T> key, T value) {
            this.mRequest.mSettings.set(key, value);
        }

        public <T> T get(CameraMetadata.Key<T> key) {
            return (T) this.mRequest.mSettings.get(key);
        }

        public void setTag(Object tag) {
            this.mRequest.mUserTag = tag;
        }

        public CaptureRequest build() {
            return new CaptureRequest();
        }

        public boolean isEmpty() {
            return this.mRequest.mSettings.isEmpty();
        }
    }
}