package android.hardware.camera2;

import android.graphics.Rect;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.impl.CameraMetadataNative;

/* loaded from: CaptureResult.class */
public final class CaptureResult extends CameraMetadata {
    private final CameraMetadataNative mResults;
    private final CaptureRequest mRequest;
    private final int mSequenceId;
    public static final CameraMetadata.Key<Rational[]> COLOR_CORRECTION_TRANSFORM = new CameraMetadata.Key<>("android.colorCorrection.transform", Rational[].class);
    public static final CameraMetadata.Key<float[]> COLOR_CORRECTION_GAINS = new CameraMetadata.Key<>("android.colorCorrection.gains", float[].class);
    public static final CameraMetadata.Key<Integer> CONTROL_AE_PRECAPTURE_ID = new CameraMetadata.Key<>("android.control.aePrecaptureId", Integer.TYPE);
    public static final CameraMetadata.Key<int[]> CONTROL_AE_REGIONS = new CameraMetadata.Key<>("android.control.aeRegions", int[].class);
    public static final CameraMetadata.Key<Integer> CONTROL_AE_STATE = new CameraMetadata.Key<>("android.control.aeState", Integer.TYPE);
    public static final CameraMetadata.Key<Integer> CONTROL_AF_MODE = new CameraMetadata.Key<>("android.control.afMode", Integer.TYPE);
    public static final CameraMetadata.Key<int[]> CONTROL_AF_REGIONS = new CameraMetadata.Key<>("android.control.afRegions", int[].class);
    public static final CameraMetadata.Key<Integer> CONTROL_AF_STATE = new CameraMetadata.Key<>("android.control.afState", Integer.TYPE);
    public static final CameraMetadata.Key<Integer> CONTROL_AF_TRIGGER_ID = new CameraMetadata.Key<>("android.control.afTriggerId", Integer.TYPE);
    public static final CameraMetadata.Key<Integer> CONTROL_AWB_MODE = new CameraMetadata.Key<>("android.control.awbMode", Integer.TYPE);
    public static final CameraMetadata.Key<int[]> CONTROL_AWB_REGIONS = new CameraMetadata.Key<>("android.control.awbRegions", int[].class);
    public static final CameraMetadata.Key<Integer> CONTROL_AWB_STATE = new CameraMetadata.Key<>("android.control.awbState", Integer.TYPE);
    public static final CameraMetadata.Key<Integer> CONTROL_MODE = new CameraMetadata.Key<>("android.control.mode", Integer.TYPE);
    public static final CameraMetadata.Key<Integer> EDGE_MODE = new CameraMetadata.Key<>("android.edge.mode", Integer.TYPE);
    public static final CameraMetadata.Key<Integer> FLASH_MODE = new CameraMetadata.Key<>("android.flash.mode", Integer.TYPE);
    public static final CameraMetadata.Key<Integer> FLASH_STATE = new CameraMetadata.Key<>("android.flash.state", Integer.TYPE);
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
    public static final CameraMetadata.Key<float[]> LENS_FOCUS_RANGE = new CameraMetadata.Key<>("android.lens.focusRange", float[].class);
    public static final CameraMetadata.Key<Integer> LENS_OPTICAL_STABILIZATION_MODE = new CameraMetadata.Key<>("android.lens.opticalStabilizationMode", Integer.TYPE);
    public static final CameraMetadata.Key<Integer> LENS_STATE = new CameraMetadata.Key<>("android.lens.state", Integer.TYPE);
    public static final CameraMetadata.Key<Integer> NOISE_REDUCTION_MODE = new CameraMetadata.Key<>("android.noiseReduction.mode", Integer.TYPE);
    public static final CameraMetadata.Key<Integer> REQUEST_FRAME_COUNT = new CameraMetadata.Key<>("android.request.frameCount", Integer.TYPE);
    public static final CameraMetadata.Key<Integer> REQUEST_ID = new CameraMetadata.Key<>("android.request.id", Integer.TYPE);
    public static final CameraMetadata.Key<Rect> SCALER_CROP_REGION = new CameraMetadata.Key<>("android.scaler.cropRegion", Rect.class);
    public static final CameraMetadata.Key<Long> SENSOR_EXPOSURE_TIME = new CameraMetadata.Key<>("android.sensor.exposureTime", Long.TYPE);
    public static final CameraMetadata.Key<Long> SENSOR_FRAME_DURATION = new CameraMetadata.Key<>("android.sensor.frameDuration", Long.TYPE);
    public static final CameraMetadata.Key<Integer> SENSOR_SENSITIVITY = new CameraMetadata.Key<>("android.sensor.sensitivity", Integer.TYPE);
    public static final CameraMetadata.Key<Long> SENSOR_TIMESTAMP = new CameraMetadata.Key<>("android.sensor.timestamp", Long.TYPE);
    public static final CameraMetadata.Key<Float> SENSOR_TEMPERATURE = new CameraMetadata.Key<>("android.sensor.temperature", Float.TYPE);
    public static final CameraMetadata.Key<Integer> STATISTICS_FACE_DETECT_MODE = new CameraMetadata.Key<>("android.statistics.faceDetectMode", Integer.TYPE);
    public static final CameraMetadata.Key<int[]> STATISTICS_FACE_IDS = new CameraMetadata.Key<>("android.statistics.faceIds", int[].class);
    public static final CameraMetadata.Key<int[]> STATISTICS_FACE_LANDMARKS = new CameraMetadata.Key<>("android.statistics.faceLandmarks", int[].class);
    public static final CameraMetadata.Key<Rect[]> STATISTICS_FACE_RECTANGLES = new CameraMetadata.Key<>("android.statistics.faceRectangles", Rect[].class);
    public static final CameraMetadata.Key<byte[]> STATISTICS_FACE_SCORES = new CameraMetadata.Key<>("android.statistics.faceScores", byte[].class);
    public static final CameraMetadata.Key<float[]> STATISTICS_LENS_SHADING_MAP = new CameraMetadata.Key<>("android.statistics.lensShadingMap", float[].class);
    public static final CameraMetadata.Key<float[]> STATISTICS_PREDICTED_COLOR_GAINS = new CameraMetadata.Key<>("android.statistics.predictedColorGains", float[].class);
    public static final CameraMetadata.Key<Rational[]> STATISTICS_PREDICTED_COLOR_TRANSFORM = new CameraMetadata.Key<>("android.statistics.predictedColorTransform", Rational[].class);
    public static final CameraMetadata.Key<Integer> STATISTICS_SCENE_FLICKER = new CameraMetadata.Key<>("android.statistics.sceneFlicker", Integer.TYPE);
    public static final CameraMetadata.Key<float[]> TONEMAP_CURVE_BLUE = new CameraMetadata.Key<>("android.tonemap.curveBlue", float[].class);
    public static final CameraMetadata.Key<float[]> TONEMAP_CURVE_GREEN = new CameraMetadata.Key<>("android.tonemap.curveGreen", float[].class);
    public static final CameraMetadata.Key<float[]> TONEMAP_CURVE_RED = new CameraMetadata.Key<>("android.tonemap.curveRed", float[].class);
    public static final CameraMetadata.Key<Integer> TONEMAP_MODE = new CameraMetadata.Key<>("android.tonemap.mode", Integer.TYPE);
    public static final CameraMetadata.Key<Boolean> LED_TRANSMIT = new CameraMetadata.Key<>("android.led.transmit", Boolean.TYPE);
    public static final CameraMetadata.Key<Boolean> BLACK_LEVEL_LOCK = new CameraMetadata.Key<>("android.blackLevel.lock", Boolean.TYPE);
    public static final CameraMetadata.Key<Face[]> STATISTICS_FACES = new CameraMetadata.Key<>("android.statistics.faces", Face[].class);

    public CaptureResult(CameraMetadataNative results, CaptureRequest parent, int sequenceId) {
        if (results == null) {
            throw new IllegalArgumentException("results was null");
        }
        if (parent == null) {
            throw new IllegalArgumentException("parent was null");
        }
        this.mResults = results;
        this.mRequest = parent;
        this.mSequenceId = sequenceId;
    }

    @Override // android.hardware.camera2.CameraMetadata
    public <T> T get(CameraMetadata.Key<T> key) {
        return (T) this.mResults.get(key);
    }

    public CaptureRequest getRequest() {
        return this.mRequest;
    }

    public int getFrameNumber() {
        return ((Integer) get(REQUEST_FRAME_COUNT)).intValue();
    }

    public int getSequenceId() {
        return this.mSequenceId;
    }
}