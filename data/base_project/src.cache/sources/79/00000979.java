package android.net.nsd;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.util.AsyncChannel;
import java.util.concurrent.CountDownLatch;

/* loaded from: NsdManager.class */
public final class NsdManager {
    private static final String TAG = "NsdManager";
    INsdManager mService;
    public static final String ACTION_NSD_STATE_CHANGED = "android.net.nsd.STATE_CHANGED";
    public static final String EXTRA_NSD_STATE = "nsd_state";
    public static final int NSD_STATE_DISABLED = 1;
    public static final int NSD_STATE_ENABLED = 2;
    private static final int BASE = 393216;
    public static final int DISCOVER_SERVICES = 393217;
    public static final int DISCOVER_SERVICES_STARTED = 393218;
    public static final int DISCOVER_SERVICES_FAILED = 393219;
    public static final int SERVICE_FOUND = 393220;
    public static final int SERVICE_LOST = 393221;
    public static final int STOP_DISCOVERY = 393222;
    public static final int STOP_DISCOVERY_FAILED = 393223;
    public static final int STOP_DISCOVERY_SUCCEEDED = 393224;
    public static final int REGISTER_SERVICE = 393225;
    public static final int REGISTER_SERVICE_FAILED = 393226;
    public static final int REGISTER_SERVICE_SUCCEEDED = 393227;
    public static final int UNREGISTER_SERVICE = 393228;
    public static final int UNREGISTER_SERVICE_FAILED = 393229;
    public static final int UNREGISTER_SERVICE_SUCCEEDED = 393230;
    public static final int RESOLVE_SERVICE = 393234;
    public static final int RESOLVE_SERVICE_FAILED = 393235;
    public static final int RESOLVE_SERVICE_SUCCEEDED = 393236;
    public static final int ENABLE = 393240;
    public static final int DISABLE = 393241;
    public static final int NATIVE_DAEMON_EVENT = 393242;
    public static final int PROTOCOL_DNS_SD = 1;
    private Context mContext;
    private static final int INVALID_LISTENER_KEY = 0;
    private ServiceHandler mHandler;
    public static final int FAILURE_INTERNAL_ERROR = 0;
    public static final int FAILURE_ALREADY_ACTIVE = 3;
    public static final int FAILURE_MAX_LIMIT = 4;
    private int mListenerKey = 1;
    private final SparseArray mListenerMap = new SparseArray();
    private final SparseArray<NsdServiceInfo> mServiceMap = new SparseArray<>();
    private final Object mMapLock = new Object();
    private final AsyncChannel mAsyncChannel = new AsyncChannel();
    private final CountDownLatch mConnected = new CountDownLatch(1);

    /* loaded from: NsdManager$DiscoveryListener.class */
    public interface DiscoveryListener {
        void onStartDiscoveryFailed(String str, int i);

        void onStopDiscoveryFailed(String str, int i);

        void onDiscoveryStarted(String str);

        void onDiscoveryStopped(String str);

        void onServiceFound(NsdServiceInfo nsdServiceInfo);

        void onServiceLost(NsdServiceInfo nsdServiceInfo);
    }

    /* loaded from: NsdManager$RegistrationListener.class */
    public interface RegistrationListener {
        void onRegistrationFailed(NsdServiceInfo nsdServiceInfo, int i);

        void onUnregistrationFailed(NsdServiceInfo nsdServiceInfo, int i);

        void onServiceRegistered(NsdServiceInfo nsdServiceInfo);

        void onServiceUnregistered(NsdServiceInfo nsdServiceInfo);
    }

    /* loaded from: NsdManager$ResolveListener.class */
    public interface ResolveListener {
        void onResolveFailed(NsdServiceInfo nsdServiceInfo, int i);

        void onServiceResolved(NsdServiceInfo nsdServiceInfo);
    }

    public NsdManager(Context context, INsdManager service) {
        this.mService = service;
        this.mContext = context;
        init();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: NsdManager$ServiceHandler.class */
    public class ServiceHandler extends Handler {
        ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            Object listener = NsdManager.this.getListener(message.arg2);
            boolean listenerRemove = true;
            switch (message.what) {
                case 69632:
                    NsdManager.this.mAsyncChannel.sendMessage(AsyncChannel.CMD_CHANNEL_FULL_CONNECTION);
                    break;
                case AsyncChannel.CMD_CHANNEL_FULLY_CONNECTED /* 69634 */:
                    NsdManager.this.mConnected.countDown();
                    break;
                case AsyncChannel.CMD_CHANNEL_DISCONNECTED /* 69636 */:
                    Log.e(NsdManager.TAG, "Channel lost");
                    break;
                case NsdManager.DISCOVER_SERVICES_STARTED /* 393218 */:
                    String s = ((NsdServiceInfo) message.obj).getServiceType();
                    ((DiscoveryListener) listener).onDiscoveryStarted(s);
                    listenerRemove = false;
                    break;
                case NsdManager.DISCOVER_SERVICES_FAILED /* 393219 */:
                    ((DiscoveryListener) listener).onStartDiscoveryFailed(NsdManager.this.getNsdService(message.arg2).getServiceType(), message.arg1);
                    break;
                case NsdManager.SERVICE_FOUND /* 393220 */:
                    ((DiscoveryListener) listener).onServiceFound((NsdServiceInfo) message.obj);
                    listenerRemove = false;
                    break;
                case NsdManager.SERVICE_LOST /* 393221 */:
                    ((DiscoveryListener) listener).onServiceLost((NsdServiceInfo) message.obj);
                    listenerRemove = false;
                    break;
                case NsdManager.STOP_DISCOVERY_FAILED /* 393223 */:
                    ((DiscoveryListener) listener).onStopDiscoveryFailed(NsdManager.this.getNsdService(message.arg2).getServiceType(), message.arg1);
                    break;
                case NsdManager.STOP_DISCOVERY_SUCCEEDED /* 393224 */:
                    ((DiscoveryListener) listener).onDiscoveryStopped(NsdManager.this.getNsdService(message.arg2).getServiceType());
                    break;
                case NsdManager.REGISTER_SERVICE_FAILED /* 393226 */:
                    ((RegistrationListener) listener).onRegistrationFailed(NsdManager.this.getNsdService(message.arg2), message.arg1);
                    break;
                case NsdManager.REGISTER_SERVICE_SUCCEEDED /* 393227 */:
                    ((RegistrationListener) listener).onServiceRegistered((NsdServiceInfo) message.obj);
                    listenerRemove = false;
                    break;
                case NsdManager.UNREGISTER_SERVICE_FAILED /* 393229 */:
                    ((RegistrationListener) listener).onUnregistrationFailed(NsdManager.this.getNsdService(message.arg2), message.arg1);
                    break;
                case NsdManager.UNREGISTER_SERVICE_SUCCEEDED /* 393230 */:
                    ((RegistrationListener) listener).onServiceUnregistered(NsdManager.this.getNsdService(message.arg2));
                    break;
                case NsdManager.RESOLVE_SERVICE_FAILED /* 393235 */:
                    ((ResolveListener) listener).onResolveFailed(NsdManager.this.getNsdService(message.arg2), message.arg1);
                    break;
                case NsdManager.RESOLVE_SERVICE_SUCCEEDED /* 393236 */:
                    ((ResolveListener) listener).onServiceResolved((NsdServiceInfo) message.obj);
                    break;
                default:
                    Log.d(NsdManager.TAG, "Ignored " + message);
                    break;
            }
            if (listenerRemove) {
                NsdManager.this.removeListener(message.arg2);
            }
        }
    }

    private int putListener(Object listener, NsdServiceInfo s) {
        int key;
        if (listener == null) {
            return 0;
        }
        synchronized (this.mMapLock) {
            do {
                key = this.mListenerKey;
                this.mListenerKey = key + 1;
            } while (key == 0);
            this.mListenerMap.put(key, listener);
            this.mServiceMap.put(key, s);
        }
        return key;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Object getListener(int key) {
        Object obj;
        if (key == 0) {
            return null;
        }
        synchronized (this.mMapLock) {
            obj = this.mListenerMap.get(key);
        }
        return obj;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public NsdServiceInfo getNsdService(int key) {
        NsdServiceInfo nsdServiceInfo;
        synchronized (this.mMapLock) {
            nsdServiceInfo = this.mServiceMap.get(key);
        }
        return nsdServiceInfo;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeListener(int key) {
        if (key == 0) {
            return;
        }
        synchronized (this.mMapLock) {
            this.mListenerMap.remove(key);
            this.mServiceMap.remove(key);
        }
    }

    private int getListenerKey(Object listener) {
        synchronized (this.mMapLock) {
            int valueIndex = this.mListenerMap.indexOfValue(listener);
            if (valueIndex != -1) {
                return this.mListenerMap.keyAt(valueIndex);
            }
            return 0;
        }
    }

    private void init() {
        Messenger messenger = getMessenger();
        if (messenger == null) {
            throw new RuntimeException("Failed to initialize");
        }
        HandlerThread t = new HandlerThread(TAG);
        t.start();
        this.mHandler = new ServiceHandler(t.getLooper());
        this.mAsyncChannel.connect(this.mContext, this.mHandler, messenger);
        try {
            this.mConnected.await();
        } catch (InterruptedException e) {
            Log.e(TAG, "interrupted wait at init");
        }
    }

    public void registerService(NsdServiceInfo serviceInfo, int protocolType, RegistrationListener listener) {
        if (TextUtils.isEmpty(serviceInfo.getServiceName()) || TextUtils.isEmpty(serviceInfo.getServiceType())) {
            throw new IllegalArgumentException("Service name or type cannot be empty");
        }
        if (serviceInfo.getPort() <= 0) {
            throw new IllegalArgumentException("Invalid port number");
        }
        if (listener == null) {
            throw new IllegalArgumentException("listener cannot be null");
        }
        if (protocolType != 1) {
            throw new IllegalArgumentException("Unsupported protocol");
        }
        this.mAsyncChannel.sendMessage(REGISTER_SERVICE, 0, putListener(listener, serviceInfo), serviceInfo);
    }

    public void unregisterService(RegistrationListener listener) {
        int id = getListenerKey(listener);
        if (id == 0) {
            throw new IllegalArgumentException("listener not registered");
        }
        if (listener == null) {
            throw new IllegalArgumentException("listener cannot be null");
        }
        this.mAsyncChannel.sendMessage(UNREGISTER_SERVICE, 0, id);
    }

    public void discoverServices(String serviceType, int protocolType, DiscoveryListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener cannot be null");
        }
        if (TextUtils.isEmpty(serviceType)) {
            throw new IllegalArgumentException("Service type cannot be empty");
        }
        if (protocolType != 1) {
            throw new IllegalArgumentException("Unsupported protocol");
        }
        NsdServiceInfo s = new NsdServiceInfo();
        s.setServiceType(serviceType);
        this.mAsyncChannel.sendMessage(DISCOVER_SERVICES, 0, putListener(listener, s), s);
    }

    public void stopServiceDiscovery(DiscoveryListener listener) {
        int id = getListenerKey(listener);
        if (id == 0) {
            throw new IllegalArgumentException("service discovery not active on listener");
        }
        if (listener == null) {
            throw new IllegalArgumentException("listener cannot be null");
        }
        this.mAsyncChannel.sendMessage(STOP_DISCOVERY, 0, id);
    }

    public void resolveService(NsdServiceInfo serviceInfo, ResolveListener listener) {
        if (TextUtils.isEmpty(serviceInfo.getServiceName()) || TextUtils.isEmpty(serviceInfo.getServiceType())) {
            throw new IllegalArgumentException("Service name or type cannot be empty");
        }
        if (listener == null) {
            throw new IllegalArgumentException("listener cannot be null");
        }
        this.mAsyncChannel.sendMessage(RESOLVE_SERVICE, 0, putListener(listener, serviceInfo), serviceInfo);
    }

    public void setEnabled(boolean enabled) {
        try {
            this.mService.setEnabled(enabled);
        } catch (RemoteException e) {
        }
    }

    private Messenger getMessenger() {
        try {
            return this.mService.getMessenger();
        } catch (RemoteException e) {
            return null;
        }
    }
}