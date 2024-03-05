package android.hardware.camera2.impl;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.ICameraDeviceCallbacks;
import android.hardware.camera2.ICameraDeviceUser;
import android.hardware.camera2.utils.CameraBinderDecorator;
import android.hardware.camera2.utils.CameraRuntimeException;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.util.SparseArray;
import android.view.Surface;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

/* loaded from: CameraDevice.class */
public class CameraDevice implements android.hardware.camera2.CameraDevice {
    private final String TAG;
    private final boolean DEBUG;
    private ICameraDeviceUser mRemoteDevice;
    private final CameraDevice.StateListener mDeviceListener;
    private final Handler mDeviceHandler;
    private final String mCameraId;
    private final Object mLock = new Object();
    private final CameraDeviceCallbacks mCallbacks = new CameraDeviceCallbacks();
    private boolean mIdle = true;
    private final SparseArray<CaptureListenerHolder> mCaptureListenerMap = new SparseArray<>();
    private final Stack<Integer> mRepeatingRequestIdStack = new Stack<>();
    private final SparseArray<Surface> mConfiguredOutputs = new SparseArray<>();
    private final Runnable mCallOnOpened = new Runnable() { // from class: android.hardware.camera2.impl.CameraDevice.1
        @Override // java.lang.Runnable
        public void run() {
            if (!CameraDevice.this.isClosed()) {
                CameraDevice.this.mDeviceListener.onOpened(CameraDevice.this);
            }
        }
    };
    private final Runnable mCallOnUnconfigured = new Runnable() { // from class: android.hardware.camera2.impl.CameraDevice.2
        @Override // java.lang.Runnable
        public void run() {
            if (!CameraDevice.this.isClosed()) {
                CameraDevice.this.mDeviceListener.onUnconfigured(CameraDevice.this);
            }
        }
    };
    private final Runnable mCallOnActive = new Runnable() { // from class: android.hardware.camera2.impl.CameraDevice.3
        @Override // java.lang.Runnable
        public void run() {
            if (!CameraDevice.this.isClosed()) {
                CameraDevice.this.mDeviceListener.onActive(CameraDevice.this);
            }
        }
    };
    private final Runnable mCallOnBusy = new Runnable() { // from class: android.hardware.camera2.impl.CameraDevice.4
        @Override // java.lang.Runnable
        public void run() {
            if (!CameraDevice.this.isClosed()) {
                CameraDevice.this.mDeviceListener.onBusy(CameraDevice.this);
            }
        }
    };
    private final Runnable mCallOnClosed = new Runnable() { // from class: android.hardware.camera2.impl.CameraDevice.5
        @Override // java.lang.Runnable
        public void run() {
            if (!CameraDevice.this.isClosed()) {
                CameraDevice.this.mDeviceListener.onClosed(CameraDevice.this);
            }
        }
    };
    private final Runnable mCallOnIdle = new Runnable() { // from class: android.hardware.camera2.impl.CameraDevice.6
        @Override // java.lang.Runnable
        public void run() {
            if (!CameraDevice.this.isClosed()) {
                CameraDevice.this.mDeviceListener.onIdle(CameraDevice.this);
            }
        }
    };
    private final Runnable mCallOnDisconnected = new Runnable() { // from class: android.hardware.camera2.impl.CameraDevice.7
        @Override // java.lang.Runnable
        public void run() {
            if (!CameraDevice.this.isClosed()) {
                CameraDevice.this.mDeviceListener.onDisconnected(CameraDevice.this);
            }
        }
    };

    public CameraDevice(String cameraId, CameraDevice.StateListener listener, Handler handler) {
        if (cameraId == null || listener == null || handler == null) {
            throw new IllegalArgumentException("Null argument given");
        }
        this.mCameraId = cameraId;
        this.mDeviceListener = listener;
        this.mDeviceHandler = handler;
        this.TAG = String.format("CameraDevice-%s-JV", this.mCameraId);
        this.DEBUG = Log.isLoggable(this.TAG, 3);
    }

    public CameraDeviceCallbacks getCallbacks() {
        return this.mCallbacks;
    }

    public void setRemoteDevice(ICameraDeviceUser remoteDevice) {
        synchronized (this.mLock) {
            this.mRemoteDevice = (ICameraDeviceUser) CameraBinderDecorator.newInstance(remoteDevice);
            this.mDeviceHandler.post(this.mCallOnOpened);
            this.mDeviceHandler.post(this.mCallOnUnconfigured);
        }
    }

    @Override // android.hardware.camera2.CameraDevice
    public String getId() {
        return this.mCameraId;
    }

    @Override // android.hardware.camera2.CameraDevice
    public void configureOutputs(List<Surface> outputs) throws CameraAccessException {
        if (outputs == null) {
            outputs = new ArrayList();
        }
        synchronized (this.mLock) {
            checkIfCameraClosed();
            HashSet<Surface> addSet = new HashSet<>(outputs);
            List<Integer> deleteList = new ArrayList<>();
            for (int i = 0; i < this.mConfiguredOutputs.size(); i++) {
                int streamId = this.mConfiguredOutputs.keyAt(i);
                Surface s = this.mConfiguredOutputs.valueAt(i);
                if (!outputs.contains(s)) {
                    deleteList.add(Integer.valueOf(streamId));
                } else {
                    addSet.remove(s);
                }
            }
            this.mDeviceHandler.post(this.mCallOnBusy);
            stopRepeating();
            try {
                this.mRemoteDevice.waitUntilIdle();
                for (Integer streamId2 : deleteList) {
                    this.mRemoteDevice.deleteStream(streamId2.intValue());
                    this.mConfiguredOutputs.delete(streamId2.intValue());
                }
                Iterator i$ = addSet.iterator();
                while (i$.hasNext()) {
                    Surface s2 = i$.next();
                    this.mConfiguredOutputs.put(this.mRemoteDevice.createStream(0, 0, 0, s2), s2);
                }
                if (outputs.size() > 0) {
                    this.mDeviceHandler.post(this.mCallOnIdle);
                } else {
                    this.mDeviceHandler.post(this.mCallOnUnconfigured);
                }
            } catch (CameraRuntimeException e) {
                if (e.getReason() == 4) {
                    throw new IllegalStateException("The camera is currently busy. You must wait until the previous operation completes.");
                }
                throw e.asChecked();
            } catch (RemoteException e2) {
            }
        }
    }

    @Override // android.hardware.camera2.CameraDevice
    public CaptureRequest.Builder createCaptureRequest(int templateType) throws CameraAccessException {
        CaptureRequest.Builder builder;
        synchronized (this.mLock) {
            checkIfCameraClosed();
            CameraMetadataNative templatedRequest = new CameraMetadataNative();
            try {
                this.mRemoteDevice.createDefaultRequest(templateType, templatedRequest);
                builder = new CaptureRequest.Builder(templatedRequest);
            } catch (CameraRuntimeException e) {
                throw e.asChecked();
            } catch (RemoteException e2) {
                return null;
            }
        }
        return builder;
    }

    @Override // android.hardware.camera2.CameraDevice
    public int capture(CaptureRequest request, CameraDevice.CaptureListener listener, Handler handler) throws CameraAccessException {
        return submitCaptureRequest(request, listener, handler, false);
    }

    @Override // android.hardware.camera2.CameraDevice
    public int captureBurst(List<CaptureRequest> requests, CameraDevice.CaptureListener listener, Handler handler) throws CameraAccessException {
        if (requests.isEmpty()) {
            Log.w(this.TAG, "Capture burst request list is empty, do nothing!");
            return -1;
        }
        throw new UnsupportedOperationException("Burst capture implemented yet");
    }

    private int submitCaptureRequest(CaptureRequest request, CameraDevice.CaptureListener listener, Handler handler, boolean repeating) throws CameraAccessException {
        int requestId;
        if (listener != null) {
            handler = checkHandler(handler);
        }
        synchronized (this.mLock) {
            checkIfCameraClosed();
            try {
                requestId = this.mRemoteDevice.submitRequest(request, repeating);
                if (listener != null) {
                    this.mCaptureListenerMap.put(requestId, new CaptureListenerHolder(listener, request, handler, repeating));
                }
                if (repeating) {
                    this.mRepeatingRequestIdStack.add(Integer.valueOf(requestId));
                }
                if (this.mIdle) {
                    this.mDeviceHandler.post(this.mCallOnActive);
                }
                this.mIdle = false;
            } catch (CameraRuntimeException e) {
                throw e.asChecked();
            } catch (RemoteException e2) {
                return -1;
            }
        }
        return requestId;
    }

    @Override // android.hardware.camera2.CameraDevice
    public int setRepeatingRequest(CaptureRequest request, CameraDevice.CaptureListener listener, Handler handler) throws CameraAccessException {
        return submitCaptureRequest(request, listener, handler, true);
    }

    @Override // android.hardware.camera2.CameraDevice
    public int setRepeatingBurst(List<CaptureRequest> requests, CameraDevice.CaptureListener listener, Handler handler) throws CameraAccessException {
        if (requests.isEmpty()) {
            Log.w(this.TAG, "Set Repeating burst request list is empty, do nothing!");
            return -1;
        }
        throw new UnsupportedOperationException("Burst capture implemented yet");
    }

    @Override // android.hardware.camera2.CameraDevice
    public void stopRepeating() throws CameraAccessException {
        synchronized (this.mLock) {
            checkIfCameraClosed();
            while (!this.mRepeatingRequestIdStack.isEmpty()) {
                int requestId = this.mRepeatingRequestIdStack.pop().intValue();
                try {
                    try {
                        this.mRemoteDevice.cancelRequest(requestId);
                    } catch (RemoteException e) {
                        return;
                    }
                } catch (CameraRuntimeException e2) {
                    throw e2.asChecked();
                }
            }
        }
    }

    @Override // android.hardware.camera2.CameraDevice
    public void waitUntilIdle() throws CameraAccessException {
        synchronized (this.mLock) {
            checkIfCameraClosed();
            if (!this.mRepeatingRequestIdStack.isEmpty()) {
                throw new IllegalStateException("Active repeating request ongoing");
            }
            try {
                this.mRemoteDevice.waitUntilIdle();
            } catch (CameraRuntimeException e) {
                throw e.asChecked();
            } catch (RemoteException e2) {
            }
        }
    }

    @Override // android.hardware.camera2.CameraDevice
    public void flush() throws CameraAccessException {
        synchronized (this.mLock) {
            checkIfCameraClosed();
            this.mDeviceHandler.post(this.mCallOnBusy);
            try {
                try {
                    this.mRemoteDevice.flush();
                } catch (CameraRuntimeException e) {
                    throw e.asChecked();
                }
            } catch (RemoteException e2) {
            }
        }
    }

    @Override // android.hardware.camera2.CameraDevice, java.lang.AutoCloseable
    public void close() {
        synchronized (this.mLock) {
            try {
                if (this.mRemoteDevice != null) {
                    this.mRemoteDevice.disconnect();
                }
            } catch (CameraRuntimeException e) {
                Log.e(this.TAG, "Exception while closing: ", e.asChecked());
            } catch (RemoteException e2) {
            }
            if (this.mRemoteDevice != null) {
                this.mDeviceHandler.post(this.mCallOnClosed);
            }
            this.mRemoteDevice = null;
        }
    }

    protected void finalize() throws Throwable {
        try {
            close();
            super.finalize();
        } catch (Throwable th) {
            super.finalize();
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: CameraDevice$CaptureListenerHolder.class */
    public static class CaptureListenerHolder {
        private final boolean mRepeating;
        private final CameraDevice.CaptureListener mListener;
        private final CaptureRequest mRequest;
        private final Handler mHandler;

        CaptureListenerHolder(CameraDevice.CaptureListener listener, CaptureRequest request, Handler handler, boolean repeating) {
            if (listener == null || handler == null) {
                throw new UnsupportedOperationException("Must have a valid handler and a valid listener");
            }
            this.mRepeating = repeating;
            this.mHandler = handler;
            this.mRequest = request;
            this.mListener = listener;
        }

        public boolean isRepeating() {
            return this.mRepeating;
        }

        public CameraDevice.CaptureListener getListener() {
            return this.mListener;
        }

        public CaptureRequest getRequest() {
            return this.mRequest;
        }

        public Handler getHandler() {
            return this.mHandler;
        }
    }

    /* loaded from: CameraDevice$CameraDeviceCallbacks.class */
    public class CameraDeviceCallbacks extends ICameraDeviceCallbacks.Stub {
        static final int ERROR_CAMERA_DISCONNECTED = 0;
        static final int ERROR_CAMERA_DEVICE = 1;
        static final int ERROR_CAMERA_SERVICE = 2;

        public CameraDeviceCallbacks() {
        }

        @Override // android.hardware.camera2.ICameraDeviceCallbacks.Stub, android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.hardware.camera2.ICameraDeviceCallbacks
        public void onCameraError(final int errorCode) {
            Runnable r;
            if (CameraDevice.this.isClosed()) {
                return;
            }
            synchronized (CameraDevice.this.mLock) {
                switch (errorCode) {
                    case 0:
                        r = CameraDevice.this.mCallOnDisconnected;
                        break;
                    default:
                        Log.e(CameraDevice.this.TAG, "Unknown error from camera device: " + errorCode);
                    case 1:
                    case 2:
                        r = new Runnable() { // from class: android.hardware.camera2.impl.CameraDevice.CameraDeviceCallbacks.1
                            @Override // java.lang.Runnable
                            public void run() {
                                if (!CameraDevice.this.isClosed()) {
                                    CameraDevice.this.mDeviceListener.onError(CameraDevice.this, errorCode);
                                }
                            }
                        };
                        break;
                }
                CameraDevice.this.mDeviceHandler.post(r);
            }
        }

        @Override // android.hardware.camera2.ICameraDeviceCallbacks
        public void onCameraIdle() {
            if (CameraDevice.this.isClosed()) {
                return;
            }
            if (CameraDevice.this.DEBUG) {
                Log.d(CameraDevice.this.TAG, "Camera now idle");
            }
            synchronized (CameraDevice.this.mLock) {
                if (!CameraDevice.this.mIdle) {
                    CameraDevice.this.mDeviceHandler.post(CameraDevice.this.mCallOnIdle);
                }
                CameraDevice.this.mIdle = true;
            }
        }

        @Override // android.hardware.camera2.ICameraDeviceCallbacks
        public void onCaptureStarted(int requestId, final long timestamp) {
            final CaptureListenerHolder holder;
            if (CameraDevice.this.DEBUG) {
                Log.d(CameraDevice.this.TAG, "Capture started for id " + requestId);
            }
            synchronized (CameraDevice.this.mLock) {
                holder = (CaptureListenerHolder) CameraDevice.this.mCaptureListenerMap.get(requestId);
            }
            if (holder == null || CameraDevice.this.isClosed()) {
                return;
            }
            holder.getHandler().post(new Runnable() { // from class: android.hardware.camera2.impl.CameraDevice.CameraDeviceCallbacks.2
                @Override // java.lang.Runnable
                public void run() {
                    if (!CameraDevice.this.isClosed()) {
                        holder.getListener().onCaptureStarted(CameraDevice.this, holder.getRequest(), timestamp);
                    }
                }
            });
        }

        @Override // android.hardware.camera2.ICameraDeviceCallbacks
        public void onResultReceived(int requestId, CameraMetadataNative result) throws RemoteException {
            final CaptureListenerHolder holder;
            if (CameraDevice.this.DEBUG) {
                Log.d(CameraDevice.this.TAG, "Received result for id " + requestId);
            }
            synchronized (CameraDevice.this.mLock) {
                holder = (CaptureListenerHolder) CameraDevice.this.mCaptureListenerMap.get(requestId);
                if (holder != null && !holder.isRepeating()) {
                    CameraDevice.this.mCaptureListenerMap.remove(requestId);
                }
            }
            if (holder == null || CameraDevice.this.isClosed()) {
                return;
            }
            final CaptureRequest request = holder.getRequest();
            final CaptureResult resultAsCapture = new CaptureResult(result, request, requestId);
            holder.getHandler().post(new Runnable() { // from class: android.hardware.camera2.impl.CameraDevice.CameraDeviceCallbacks.3
                @Override // java.lang.Runnable
                public void run() {
                    if (!CameraDevice.this.isClosed()) {
                        holder.getListener().onCaptureCompleted(CameraDevice.this, request, resultAsCapture);
                    }
                }
            });
        }
    }

    private Handler checkHandler(Handler handler) {
        if (handler == null) {
            Looper looper = Looper.myLooper();
            if (looper == null) {
                throw new IllegalArgumentException("No handler given, and current thread has no looper!");
            }
            handler = new Handler(looper);
        }
        return handler;
    }

    private void checkIfCameraClosed() {
        if (this.mRemoteDevice == null) {
            throw new IllegalStateException("CameraDevice was already closed");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isClosed() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mRemoteDevice == null;
        }
        return z;
    }
}