package android.hardware.camera2;

import android.content.Context;
import android.hardware.ICameraService;
import android.hardware.ICameraServiceListener;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.ICameraDeviceUser;
import android.hardware.camera2.impl.CameraMetadataNative;
import android.hardware.camera2.utils.BinderHolder;
import android.hardware.camera2.utils.CameraBinderDecorator;
import android.hardware.camera2.utils.CameraRuntimeException;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.ArrayMap;
import android.util.Log;
import java.util.ArrayList;

/* loaded from: CameraManager.class */
public final class CameraManager {
    private static final String CAMERA_SERVICE_BINDER_NAME = "media.camera";
    private static final int USE_CALLING_UID = -1;
    private final ICameraService mCameraService;
    private ArrayList<String> mDeviceIdList;
    private final Context mContext;
    private final ArrayMap<AvailabilityListener, Handler> mListenerMap = new ArrayMap<>();
    private final Object mLock = new Object();

    public CameraManager(Context context) {
        this.mContext = context;
        IBinder cameraServiceBinder = ServiceManager.getService(CAMERA_SERVICE_BINDER_NAME);
        ICameraService cameraServiceRaw = ICameraService.Stub.asInterface(cameraServiceBinder);
        this.mCameraService = (ICameraService) CameraBinderDecorator.newInstance(cameraServiceRaw);
        try {
            this.mCameraService.addListener(new CameraServiceListener());
        } catch (CameraRuntimeException e) {
            throw new IllegalStateException("Failed to register a camera service listener", e.asChecked());
        } catch (RemoteException e2) {
        }
    }

    public String[] getCameraIdList() throws CameraAccessException {
        String[] strArr;
        synchronized (this.mLock) {
            try {
                strArr = (String[]) getOrCreateDeviceIdListLocked().toArray(new String[0]);
            } catch (CameraAccessException e) {
                throw new IllegalStateException("Failed to query camera service for device ID list", e);
            }
        }
        return strArr;
    }

    public void addAvailabilityListener(AvailabilityListener listener, Handler handler) {
        if (handler == null) {
            Looper looper = Looper.myLooper();
            if (looper == null) {
                throw new IllegalArgumentException("No handler given, and current thread has no looper!");
            }
            handler = new Handler(looper);
        }
        synchronized (this.mLock) {
            this.mListenerMap.put(listener, handler);
        }
    }

    public void removeAvailabilityListener(AvailabilityListener listener) {
        synchronized (this.mLock) {
            this.mListenerMap.remove(listener);
        }
    }

    public CameraCharacteristics getCameraCharacteristics(String cameraId) throws CameraAccessException {
        synchronized (this.mLock) {
            if (!getOrCreateDeviceIdListLocked().contains(cameraId)) {
                throw new IllegalArgumentException(String.format("Camera id %s does not match any currently connected camera device", cameraId));
            }
        }
        CameraMetadataNative info = new CameraMetadataNative();
        try {
            this.mCameraService.getCameraCharacteristics(Integer.valueOf(cameraId).intValue(), info);
            return new CameraCharacteristics(info);
        } catch (CameraRuntimeException e) {
            throw e.asChecked();
        } catch (RemoteException e2) {
            return null;
        }
    }

    private void openCameraDeviceUserAsync(String cameraId, CameraDevice.StateListener listener, Handler handler) throws CameraAccessException {
        try {
            synchronized (this.mLock) {
                android.hardware.camera2.impl.CameraDevice device = new android.hardware.camera2.impl.CameraDevice(cameraId, listener, handler);
                BinderHolder holder = new BinderHolder();
                this.mCameraService.connectDevice(device.getCallbacks(), Integer.parseInt(cameraId), this.mContext.getPackageName(), -1, holder);
                ICameraDeviceUser cameraUser = ICameraDeviceUser.Stub.asInterface(holder.getBinder());
                device.setRemoteDevice(cameraUser);
            }
        } catch (CameraRuntimeException e) {
            throw e.asChecked();
        } catch (RemoteException e2) {
        } catch (NumberFormatException e3) {
            throw new IllegalArgumentException("Expected cameraId to be numeric, but it was: " + cameraId);
        }
    }

    public void openCamera(String cameraId, CameraDevice.StateListener listener, Handler handler) throws CameraAccessException {
        if (cameraId == null) {
            throw new IllegalArgumentException("cameraId was null");
        }
        if (listener == null) {
            throw new IllegalArgumentException("listener was null");
        }
        if (handler == null) {
            if (Looper.myLooper() != null) {
                handler = new Handler();
            } else {
                throw new IllegalArgumentException("Looper doesn't exist in the calling thread");
            }
        }
        openCameraDeviceUserAsync(cameraId, listener, handler);
    }

    /* loaded from: CameraManager$AvailabilityListener.class */
    public static abstract class AvailabilityListener {
        public void onCameraAvailable(String cameraId) {
        }

        public void onCameraUnavailable(String cameraId) {
        }
    }

    private ArrayList<String> getOrCreateDeviceIdListLocked() throws CameraAccessException {
        if (this.mDeviceIdList == null) {
            try {
                int numCameras = this.mCameraService.getNumberOfCameras();
                this.mDeviceIdList = new ArrayList<>();
                CameraMetadataNative info = new CameraMetadataNative();
                for (int i = 0; i < numCameras; i++) {
                    boolean isDeviceSupported = false;
                    try {
                        this.mCameraService.getCameraCharacteristics(i, info);
                    } catch (CameraRuntimeException e) {
                        throw e.asChecked();
                    } catch (RemoteException e2) {
                    } catch (IllegalArgumentException e3) {
                    }
                    if (!info.isEmpty()) {
                        isDeviceSupported = true;
                        if (isDeviceSupported) {
                            this.mDeviceIdList.add(String.valueOf(i));
                        }
                    } else {
                        throw new AssertionError("Expected to get non-empty characteristics");
                        break;
                    }
                }
            } catch (CameraRuntimeException e4) {
                throw e4.asChecked();
            } catch (RemoteException e5) {
                return null;
            }
        }
        return this.mDeviceIdList;
    }

    /* loaded from: CameraManager$CameraServiceListener.class */
    private class CameraServiceListener extends ICameraServiceListener.Stub {
        public static final int STATUS_NOT_PRESENT = 0;
        public static final int STATUS_PRESENT = 1;
        public static final int STATUS_ENUMERATING = 2;
        public static final int STATUS_NOT_AVAILABLE = Integer.MIN_VALUE;
        private final ArrayMap<String, Integer> mDeviceStatus;
        private static final String TAG = "CameraServiceListener";

        private CameraServiceListener() {
            this.mDeviceStatus = new ArrayMap<>();
        }

        @Override // android.hardware.ICameraServiceListener.Stub, android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        private boolean isAvailable(int status) {
            switch (status) {
                case 1:
                    return true;
                default:
                    return false;
            }
        }

        private boolean validStatus(int status) {
            switch (status) {
                case Integer.MIN_VALUE:
                case 0:
                case 1:
                case 2:
                    return true;
                default:
                    return false;
            }
        }

        @Override // android.hardware.ICameraServiceListener
        public void onStatusChanged(int status, int cameraId) throws RemoteException {
            synchronized (CameraManager.this.mLock) {
                Log.v(TAG, String.format("Camera id %d has status changed to 0x%x", Integer.valueOf(cameraId), Integer.valueOf(status)));
                final String id = String.valueOf(cameraId);
                if (!validStatus(status)) {
                    Log.e(TAG, String.format("Ignoring invalid device %d status 0x%x", Integer.valueOf(cameraId), Integer.valueOf(status)));
                    return;
                }
                Integer oldStatus = this.mDeviceStatus.put(id, Integer.valueOf(status));
                if (oldStatus != null && oldStatus.intValue() == status) {
                    Log.v(TAG, String.format("Device status changed to 0x%x, which is what it already was", Integer.valueOf(status)));
                } else if (oldStatus == null || isAvailable(status) != isAvailable(oldStatus.intValue())) {
                    int listenerCount = CameraManager.this.mListenerMap.size();
                    for (int i = 0; i < listenerCount; i++) {
                        Handler handler = (Handler) CameraManager.this.mListenerMap.valueAt(i);
                        final AvailabilityListener listener = (AvailabilityListener) CameraManager.this.mListenerMap.keyAt(i);
                        if (isAvailable(status)) {
                            handler.post(new Runnable() { // from class: android.hardware.camera2.CameraManager.CameraServiceListener.1
                                @Override // java.lang.Runnable
                                public void run() {
                                    listener.onCameraAvailable(id);
                                }
                            });
                        } else {
                            handler.post(new Runnable() { // from class: android.hardware.camera2.CameraManager.CameraServiceListener.2
                                @Override // java.lang.Runnable
                                public void run() {
                                    listener.onCameraUnavailable(id);
                                }
                            });
                        }
                    }
                } else {
                    Log.v(TAG, String.format("Device status was previously available (%d),  and is now again available (%d)so no new client visible update will be sent", Boolean.valueOf(isAvailable(status)), Boolean.valueOf(isAvailable(status))));
                }
            }
        }
    }
}