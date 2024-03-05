package android.hardware.camera2;

import android.hardware.camera2.CaptureRequest;
import android.os.Handler;
import android.view.Surface;
import java.util.List;

/* loaded from: CameraDevice.class */
public interface CameraDevice extends AutoCloseable {
    public static final int TEMPLATE_PREVIEW = 1;
    public static final int TEMPLATE_STILL_CAPTURE = 2;
    public static final int TEMPLATE_RECORD = 3;
    public static final int TEMPLATE_VIDEO_SNAPSHOT = 4;
    public static final int TEMPLATE_ZERO_SHUTTER_LAG = 5;
    public static final int TEMPLATE_MANUAL = 6;

    String getId();

    void configureOutputs(List<Surface> list) throws CameraAccessException;

    CaptureRequest.Builder createCaptureRequest(int i) throws CameraAccessException;

    int capture(CaptureRequest captureRequest, CaptureListener captureListener, Handler handler) throws CameraAccessException;

    int captureBurst(List<CaptureRequest> list, CaptureListener captureListener, Handler handler) throws CameraAccessException;

    int setRepeatingRequest(CaptureRequest captureRequest, CaptureListener captureListener, Handler handler) throws CameraAccessException;

    int setRepeatingBurst(List<CaptureRequest> list, CaptureListener captureListener, Handler handler) throws CameraAccessException;

    void stopRepeating() throws CameraAccessException;

    void waitUntilIdle() throws CameraAccessException;

    void flush() throws CameraAccessException;

    @Override // java.lang.AutoCloseable
    void close();

    /* loaded from: CameraDevice$CaptureListener.class */
    public static abstract class CaptureListener {
        public void onCaptureStarted(CameraDevice camera, CaptureRequest request, long timestamp) {
        }

        public void onCaptureCompleted(CameraDevice camera, CaptureRequest request, CaptureResult result) {
        }

        public void onCaptureFailed(CameraDevice camera, CaptureRequest request, CaptureFailure failure) {
        }

        public void onCaptureSequenceCompleted(CameraDevice camera, int sequenceId, int frameNumber) {
        }
    }

    /* loaded from: CameraDevice$StateListener.class */
    public static abstract class StateListener {
        public static final int ERROR_CAMERA_IN_USE = 1;
        public static final int ERROR_MAX_CAMERAS_IN_USE = 2;
        public static final int ERROR_CAMERA_DISABLED = 3;
        public static final int ERROR_CAMERA_DEVICE = 4;
        public static final int ERROR_CAMERA_SERVICE = 5;

        public abstract void onOpened(CameraDevice cameraDevice);

        public abstract void onDisconnected(CameraDevice cameraDevice);

        public abstract void onError(CameraDevice cameraDevice, int i);

        public void onUnconfigured(CameraDevice camera) {
        }

        public void onActive(CameraDevice camera) {
        }

        public void onBusy(CameraDevice camera) {
        }

        public void onClosed(CameraDevice camera) {
        }

        public void onIdle(CameraDevice camera) {
        }
    }
}