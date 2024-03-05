package android.drm;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.drm.DrmStore;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import dalvik.system.CloseGuard;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

/* loaded from: DrmManagerClient.class */
public class DrmManagerClient {
    public static final int ERROR_NONE = 0;
    public static final int ERROR_UNKNOWN = -2000;
    public static final int INVALID_SESSION = -1;
    HandlerThread mInfoThread;
    HandlerThread mEventThread;
    private static final String TAG = "DrmManagerClient";
    private final CloseGuard mCloseGuard = CloseGuard.get();
    private static final int ACTION_REMOVE_ALL_RIGHTS = 1001;
    private static final int ACTION_PROCESS_DRM_INFO = 1002;
    private int mUniqueId;
    private int mNativeContext;
    private volatile boolean mReleased;
    private Context mContext;
    private InfoHandler mInfoHandler;
    private EventHandler mEventHandler;
    private OnInfoListener mOnInfoListener;
    private OnEventListener mOnEventListener;
    private OnErrorListener mOnErrorListener;

    /* loaded from: DrmManagerClient$OnErrorListener.class */
    public interface OnErrorListener {
        void onError(DrmManagerClient drmManagerClient, DrmErrorEvent drmErrorEvent);
    }

    /* loaded from: DrmManagerClient$OnEventListener.class */
    public interface OnEventListener {
        void onEvent(DrmManagerClient drmManagerClient, DrmEvent drmEvent);
    }

    /* loaded from: DrmManagerClient$OnInfoListener.class */
    public interface OnInfoListener {
        void onInfo(DrmManagerClient drmManagerClient, DrmInfoEvent drmInfoEvent);
    }

    private native int _initialize();

    private native void _setListeners(int i, Object obj);

    private native void _release(int i);

    private native void _installDrmEngine(int i, String str);

    private native ContentValues _getConstraints(int i, String str, int i2);

    private native ContentValues _getMetadata(int i, String str);

    private native boolean _canHandle(int i, String str, String str2);

    /* JADX INFO: Access modifiers changed from: private */
    public native DrmInfoStatus _processDrmInfo(int i, DrmInfo drmInfo);

    private native DrmInfo _acquireDrmInfo(int i, DrmInfoRequest drmInfoRequest);

    private native int _saveRights(int i, DrmRights drmRights, String str, String str2);

    private native int _getDrmObjectType(int i, String str, String str2);

    private native String _getOriginalMimeType(int i, String str, FileDescriptor fileDescriptor);

    private native int _checkRightsStatus(int i, String str, int i2);

    private native int _removeRights(int i, String str);

    /* JADX INFO: Access modifiers changed from: private */
    public native int _removeAllRights(int i);

    private native int _openConvertSession(int i, String str);

    private native DrmConvertedStatus _convertData(int i, int i2, byte[] bArr);

    private native DrmConvertedStatus _closeConvertSession(int i, int i2);

    private native DrmSupportInfo[] _getAllSupportInfo(int i);

    static {
        System.loadLibrary("drmframework_jni");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: DrmManagerClient$EventHandler.class */
    public class EventHandler extends Handler {
        public EventHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            DrmEvent event = null;
            DrmErrorEvent error = null;
            HashMap<String, Object> attributes = new HashMap<>();
            switch (msg.what) {
                case 1001:
                    if (0 == DrmManagerClient.this._removeAllRights(DrmManagerClient.this.mUniqueId)) {
                        event = new DrmEvent(DrmManagerClient.this.mUniqueId, 1001, null);
                        break;
                    } else {
                        error = new DrmErrorEvent(DrmManagerClient.this.mUniqueId, 2007, null);
                        break;
                    }
                case 1002:
                    DrmInfo drmInfo = (DrmInfo) msg.obj;
                    DrmInfoStatus status = DrmManagerClient.this._processDrmInfo(DrmManagerClient.this.mUniqueId, drmInfo);
                    attributes.put(DrmEvent.DRM_INFO_STATUS_OBJECT, status);
                    attributes.put(DrmEvent.DRM_INFO_OBJECT, drmInfo);
                    if (null != status && 1 == status.statusCode) {
                        event = new DrmEvent(DrmManagerClient.this.mUniqueId, DrmManagerClient.this.getEventType(status.infoType), null, attributes);
                        break;
                    } else {
                        int infoType = null != status ? status.infoType : drmInfo.getInfoType();
                        error = new DrmErrorEvent(DrmManagerClient.this.mUniqueId, DrmManagerClient.this.getErrorType(infoType), null, attributes);
                        break;
                    }
                    break;
                default:
                    Log.e(DrmManagerClient.TAG, "Unknown message type " + msg.what);
                    return;
            }
            if (null != DrmManagerClient.this.mOnEventListener && null != event) {
                DrmManagerClient.this.mOnEventListener.onEvent(DrmManagerClient.this, event);
            }
            if (null != DrmManagerClient.this.mOnErrorListener && null != error) {
                DrmManagerClient.this.mOnErrorListener.onError(DrmManagerClient.this, error);
            }
        }
    }

    public static void notify(Object thisReference, int uniqueId, int infoType, String message) {
        DrmManagerClient instance = (DrmManagerClient) ((WeakReference) thisReference).get();
        if (null != instance && null != instance.mInfoHandler) {
            Message m = instance.mInfoHandler.obtainMessage(1, uniqueId, infoType, message);
            instance.mInfoHandler.sendMessage(m);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: DrmManagerClient$InfoHandler.class */
    public class InfoHandler extends Handler {
        public static final int INFO_EVENT_TYPE = 1;

        public InfoHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            DrmInfoEvent info = null;
            DrmErrorEvent error = null;
            switch (msg.what) {
                case 1:
                    int uniqueId = msg.arg1;
                    int infoType = msg.arg2;
                    String message = msg.obj.toString();
                    switch (infoType) {
                        case 1:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                            info = new DrmInfoEvent(uniqueId, infoType, message);
                            break;
                        case 2:
                            try {
                                DrmUtils.removeFile(message);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            info = new DrmInfoEvent(uniqueId, infoType, message);
                            break;
                        default:
                            error = new DrmErrorEvent(uniqueId, infoType, message);
                            break;
                    }
                    if (null != DrmManagerClient.this.mOnInfoListener && null != info) {
                        DrmManagerClient.this.mOnInfoListener.onInfo(DrmManagerClient.this, info);
                    }
                    if (null != DrmManagerClient.this.mOnErrorListener && null != error) {
                        DrmManagerClient.this.mOnErrorListener.onError(DrmManagerClient.this, error);
                        return;
                    }
                    return;
                default:
                    Log.e(DrmManagerClient.TAG, "Unknown message type " + msg.what);
                    return;
            }
        }
    }

    public DrmManagerClient(Context context) {
        this.mContext = context;
        createEventThreads();
        this.mUniqueId = _initialize();
        this.mCloseGuard.open("release");
    }

    protected void finalize() throws Throwable {
        try {
            if (this.mCloseGuard != null) {
                this.mCloseGuard.warnIfOpen();
            }
            release();
            super.finalize();
        } catch (Throwable th) {
            super.finalize();
            throw th;
        }
    }

    public void release() {
        if (this.mReleased) {
            return;
        }
        this.mReleased = true;
        if (this.mEventHandler != null) {
            this.mEventThread.quit();
            this.mEventThread = null;
        }
        if (this.mInfoHandler != null) {
            this.mInfoThread.quit();
            this.mInfoThread = null;
        }
        this.mEventHandler = null;
        this.mInfoHandler = null;
        this.mOnEventListener = null;
        this.mOnInfoListener = null;
        this.mOnErrorListener = null;
        _release(this.mUniqueId);
        this.mCloseGuard.close();
    }

    public synchronized void setOnInfoListener(OnInfoListener infoListener) {
        this.mOnInfoListener = infoListener;
        if (null != infoListener) {
            createListeners();
        }
    }

    public synchronized void setOnEventListener(OnEventListener eventListener) {
        this.mOnEventListener = eventListener;
        if (null != eventListener) {
            createListeners();
        }
    }

    public synchronized void setOnErrorListener(OnErrorListener errorListener) {
        this.mOnErrorListener = errorListener;
        if (null != errorListener) {
            createListeners();
        }
    }

    public String[] getAvailableDrmEngines() {
        DrmSupportInfo[] supportInfos = _getAllSupportInfo(this.mUniqueId);
        ArrayList<String> descriptions = new ArrayList<>();
        for (DrmSupportInfo drmSupportInfo : supportInfos) {
            descriptions.add(drmSupportInfo.getDescriprition());
        }
        String[] drmEngines = new String[descriptions.size()];
        return (String[]) descriptions.toArray(drmEngines);
    }

    public ContentValues getConstraints(String path, int action) {
        if (null == path || path.equals("") || !DrmStore.Action.isValid(action)) {
            throw new IllegalArgumentException("Given usage or path is invalid/null");
        }
        return _getConstraints(this.mUniqueId, path, action);
    }

    public ContentValues getMetadata(String path) {
        if (null == path || path.equals("")) {
            throw new IllegalArgumentException("Given path is invalid/null");
        }
        return _getMetadata(this.mUniqueId, path);
    }

    public ContentValues getConstraints(Uri uri, int action) {
        if (null == uri || Uri.EMPTY == uri) {
            throw new IllegalArgumentException("Uri should be non null");
        }
        return getConstraints(convertUriToPath(uri), action);
    }

    public ContentValues getMetadata(Uri uri) {
        if (null == uri || Uri.EMPTY == uri) {
            throw new IllegalArgumentException("Uri should be non null");
        }
        return getMetadata(convertUriToPath(uri));
    }

    public int saveRights(DrmRights drmRights, String rightsPath, String contentPath) throws IOException {
        if (null == drmRights || !drmRights.isValid()) {
            throw new IllegalArgumentException("Given drmRights or contentPath is not valid");
        }
        if (null != rightsPath && !rightsPath.equals("")) {
            DrmUtils.writeToFile(rightsPath, drmRights.getData());
        }
        return _saveRights(this.mUniqueId, drmRights, rightsPath, contentPath);
    }

    public void installDrmEngine(String engineFilePath) {
        if (null == engineFilePath || engineFilePath.equals("")) {
            throw new IllegalArgumentException("Given engineFilePath: " + engineFilePath + "is not valid");
        }
        _installDrmEngine(this.mUniqueId, engineFilePath);
    }

    public boolean canHandle(String path, String mimeType) {
        if ((null == path || path.equals("")) && (null == mimeType || mimeType.equals(""))) {
            throw new IllegalArgumentException("Path or the mimetype should be non null");
        }
        return _canHandle(this.mUniqueId, path, mimeType);
    }

    public boolean canHandle(Uri uri, String mimeType) {
        if ((null == uri || Uri.EMPTY == uri) && (null == mimeType || mimeType.equals(""))) {
            throw new IllegalArgumentException("Uri or the mimetype should be non null");
        }
        return canHandle(convertUriToPath(uri), mimeType);
    }

    public int processDrmInfo(DrmInfo drmInfo) {
        if (null == drmInfo || !drmInfo.isValid()) {
            throw new IllegalArgumentException("Given drmInfo is invalid/null");
        }
        int result = -2000;
        if (null != this.mEventHandler) {
            Message msg = this.mEventHandler.obtainMessage(1002, drmInfo);
            result = this.mEventHandler.sendMessage(msg) ? 0 : -2000;
        }
        return result;
    }

    public DrmInfo acquireDrmInfo(DrmInfoRequest drmInfoRequest) {
        if (null == drmInfoRequest || !drmInfoRequest.isValid()) {
            throw new IllegalArgumentException("Given drmInfoRequest is invalid/null");
        }
        return _acquireDrmInfo(this.mUniqueId, drmInfoRequest);
    }

    public int acquireRights(DrmInfoRequest drmInfoRequest) {
        DrmInfo drmInfo = acquireDrmInfo(drmInfoRequest);
        if (null == drmInfo) {
            return ERROR_UNKNOWN;
        }
        return processDrmInfo(drmInfo);
    }

    public int getDrmObjectType(String path, String mimeType) {
        if ((null == path || path.equals("")) && (null == mimeType || mimeType.equals(""))) {
            throw new IllegalArgumentException("Path or the mimetype should be non null");
        }
        return _getDrmObjectType(this.mUniqueId, path, mimeType);
    }

    public int getDrmObjectType(Uri uri, String mimeType) {
        if ((null == uri || Uri.EMPTY == uri) && (null == mimeType || mimeType.equals(""))) {
            throw new IllegalArgumentException("Uri or the mimetype should be non null");
        }
        String path = "";
        try {
            path = convertUriToPath(uri);
        } catch (Exception e) {
            Log.w(TAG, "Given Uri could not be found in media store");
        }
        return getDrmObjectType(path, mimeType);
    }

    public String getOriginalMimeType(String path) {
        if (null == path || path.equals("")) {
            throw new IllegalArgumentException("Given path should be non null");
        }
        String mime = null;
        FileInputStream is = null;
        try {
            FileDescriptor fd = null;
            File file = new File(path);
            if (file.exists()) {
                is = new FileInputStream(file);
                fd = is.getFD();
            }
            mime = _getOriginalMimeType(this.mUniqueId, path, fd);
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        } catch (IOException e2) {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e3) {
                }
            }
        } catch (Throwable th) {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e4) {
                }
            }
            throw th;
        }
        return mime;
    }

    public String getOriginalMimeType(Uri uri) {
        if (null == uri || Uri.EMPTY == uri) {
            throw new IllegalArgumentException("Given uri is not valid");
        }
        return getOriginalMimeType(convertUriToPath(uri));
    }

    public int checkRightsStatus(String path) {
        return checkRightsStatus(path, 0);
    }

    public int checkRightsStatus(Uri uri) {
        if (null == uri || Uri.EMPTY == uri) {
            throw new IllegalArgumentException("Given uri is not valid");
        }
        return checkRightsStatus(convertUriToPath(uri));
    }

    public int checkRightsStatus(String path, int action) {
        if (null == path || path.equals("") || !DrmStore.Action.isValid(action)) {
            throw new IllegalArgumentException("Given path or action is not valid");
        }
        return _checkRightsStatus(this.mUniqueId, path, action);
    }

    public int checkRightsStatus(Uri uri, int action) {
        if (null == uri || Uri.EMPTY == uri) {
            throw new IllegalArgumentException("Given uri is not valid");
        }
        return checkRightsStatus(convertUriToPath(uri), action);
    }

    public int removeRights(String path) {
        if (null == path || path.equals("")) {
            throw new IllegalArgumentException("Given path should be non null");
        }
        return _removeRights(this.mUniqueId, path);
    }

    public int removeRights(Uri uri) {
        if (null == uri || Uri.EMPTY == uri) {
            throw new IllegalArgumentException("Given uri is not valid");
        }
        return removeRights(convertUriToPath(uri));
    }

    public int removeAllRights() {
        int result = -2000;
        if (null != this.mEventHandler) {
            Message msg = this.mEventHandler.obtainMessage(1001);
            result = this.mEventHandler.sendMessage(msg) ? 0 : -2000;
        }
        return result;
    }

    public int openConvertSession(String mimeType) {
        if (null == mimeType || mimeType.equals("")) {
            throw new IllegalArgumentException("Path or the mimeType should be non null");
        }
        return _openConvertSession(this.mUniqueId, mimeType);
    }

    public DrmConvertedStatus convertData(int convertId, byte[] inputData) {
        if (null == inputData || 0 >= inputData.length) {
            throw new IllegalArgumentException("Given inputData should be non null");
        }
        return _convertData(this.mUniqueId, convertId, inputData);
    }

    public DrmConvertedStatus closeConvertSession(int convertId) {
        return _closeConvertSession(this.mUniqueId, convertId);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getEventType(int infoType) {
        int eventType = -1;
        switch (infoType) {
            case 1:
            case 2:
            case 3:
                eventType = 1002;
                break;
        }
        return eventType;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getErrorType(int infoType) {
        int error = -1;
        switch (infoType) {
            case 1:
            case 2:
            case 3:
                error = 2006;
                break;
        }
        return error;
    }

    private String convertUriToPath(Uri uri) {
        String path = null;
        if (null != uri) {
            String scheme = uri.getScheme();
            if (null == scheme || scheme.equals("") || scheme.equals(ContentResolver.SCHEME_FILE)) {
                path = uri.getPath();
            } else if (scheme.equals("http")) {
                path = uri.toString();
            } else if (scheme.equals("content")) {
                String[] projection = {"_data"};
                try {
                    try {
                        Cursor cursor = this.mContext.getContentResolver().query(uri, projection, null, null, null);
                        if (null == cursor || 0 == cursor.getCount() || !cursor.moveToFirst()) {
                            throw new IllegalArgumentException("Given Uri could not be found in media store");
                        }
                        int pathIndex = cursor.getColumnIndexOrThrow("_data");
                        path = cursor.getString(pathIndex);
                        if (null != cursor) {
                            cursor.close();
                        }
                    } catch (SQLiteException e) {
                        throw new IllegalArgumentException("Given Uri is not formatted in a way so that it can be found in media store.");
                    }
                } finally {
                    if (null != cursor) {
                        cursor.close();
                    }
                }
            } else {
                throw new IllegalArgumentException("Given Uri scheme is not supported");
            }
        }
        return path;
    }

    private void createEventThreads() {
        if (this.mEventHandler == null && this.mInfoHandler == null) {
            this.mInfoThread = new HandlerThread("DrmManagerClient.InfoHandler");
            this.mInfoThread.start();
            this.mInfoHandler = new InfoHandler(this.mInfoThread.getLooper());
            this.mEventThread = new HandlerThread("DrmManagerClient.EventHandler");
            this.mEventThread.start();
            this.mEventHandler = new EventHandler(this.mEventThread.getLooper());
        }
    }

    private void createListeners() {
        _setListeners(this.mUniqueId, new WeakReference(this));
    }
}