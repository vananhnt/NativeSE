package com.android.server.connectivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ProxyProperties;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import com.android.net.IProxyCallback;
import com.android.net.IProxyPortListener;
import com.android.net.IProxyService;
import com.android.server.IoThread;
import gov.nist.core.Separators;
import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import libcore.io.Streams;

/* loaded from: PacManager.class */
public class PacManager {
    public static final String PAC_PACKAGE = "com.android.pacprocessor";
    public static final String PAC_SERVICE = "com.android.pacprocessor.PacService";
    public static final String PAC_SERVICE_NAME = "com.android.net.IProxyService";
    public static final String PROXY_PACKAGE = "com.android.proxyhandler";
    public static final String PROXY_SERVICE = "com.android.proxyhandler.ProxyService";
    private static final String TAG = "PacManager";
    private static final String ACTION_PAC_REFRESH = "android.net.proxy.PAC_REFRESH";
    private static final String DEFAULT_DELAYS = "8 32 120 14400 43200";
    private static final int DELAY_1 = 0;
    private static final int DELAY_4 = 3;
    private static final int DELAY_LONG = 4;
    public static final String KEY_PROXY = "keyProxy";
    private String mCurrentPac;
    @GuardedBy("mProxyLock")
    private String mPacUrl;
    private AlarmManager mAlarmManager;
    @GuardedBy("mProxyLock")
    private IProxyService mProxyService;
    private PendingIntent mPacRefreshIntent;
    private ServiceConnection mConnection;
    private ServiceConnection mProxyConnection;
    private Context mContext;
    private int mCurrentDelay;
    private boolean mHasSentBroadcast;
    private boolean mHasDownloaded;
    private Handler mConnectivityHandler;
    private int mProxyMessage;
    private final Object mProxyLock = new Object();
    private Runnable mPacDownloader = new Runnable() { // from class: com.android.server.connectivity.PacManager.1
        @Override // java.lang.Runnable
        public void run() {
            String file;
            synchronized (PacManager.this.mProxyLock) {
                if (PacManager.this.mPacUrl == null) {
                    return;
                }
                try {
                    file = PacManager.get(PacManager.this.mPacUrl);
                } catch (IOException ioe) {
                    file = null;
                    Log.w(PacManager.TAG, "Failed to load PAC file: " + ioe);
                }
                if (file != null) {
                    synchronized (PacManager.this.mProxyLock) {
                        if (!file.equals(PacManager.this.mCurrentPac)) {
                            PacManager.this.setCurrentProxyScript(file);
                        }
                    }
                    PacManager.this.mHasDownloaded = true;
                    PacManager.this.sendProxyIfNeeded();
                    PacManager.this.longSchedule();
                    return;
                }
                PacManager.this.reschedule();
            }
        }
    };
    private int mLastPort = -1;

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.connectivity.PacManager.setCurrentProxyScriptUrl(android.net.ProxyProperties):boolean, file: PacManager.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:110)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    public synchronized boolean setCurrentProxyScriptUrl(android.net.ProxyProperties r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.connectivity.PacManager.setCurrentProxyScriptUrl(android.net.ProxyProperties):boolean, file: PacManager.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.connectivity.PacManager.setCurrentProxyScriptUrl(android.net.ProxyProperties):boolean");
    }

    /* loaded from: PacManager$PacRefreshIntentReceiver.class */
    class PacRefreshIntentReceiver extends BroadcastReceiver {
        PacRefreshIntentReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            IoThread.getHandler().post(PacManager.this.mPacDownloader);
        }
    }

    public PacManager(Context context, Handler handler, int proxyMessage) {
        this.mContext = context;
        this.mPacRefreshIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_PAC_REFRESH), 0);
        context.registerReceiver(new PacRefreshIntentReceiver(), new IntentFilter(ACTION_PAC_REFRESH));
        this.mConnectivityHandler = handler;
        this.mProxyMessage = proxyMessage;
    }

    private AlarmManager getAlarmManager() {
        if (this.mAlarmManager == null) {
            this.mAlarmManager = (AlarmManager) this.mContext.getSystemService("alarm");
        }
        return this.mAlarmManager;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String get(String urlString) throws IOException {
        URL url = new URL(urlString);
        URLConnection urlConnection = url.openConnection(Proxy.NO_PROXY);
        return new String(Streams.readFully(urlConnection.getInputStream()));
    }

    private int getNextDelay(int currentDelay) {
        int currentDelay2 = currentDelay + 1;
        if (currentDelay2 > 3) {
            return 3;
        }
        return currentDelay2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void longSchedule() {
        this.mCurrentDelay = 0;
        setDownloadIn(4);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void reschedule() {
        this.mCurrentDelay = getNextDelay(this.mCurrentDelay);
        setDownloadIn(this.mCurrentDelay);
    }

    private String getPacChangeDelay() {
        ContentResolver cr = this.mContext.getContentResolver();
        String defaultDelay = SystemProperties.get("conn.pac_change_delay", DEFAULT_DELAYS);
        String val = Settings.Global.getString(cr, Settings.Global.PAC_CHANGE_DELAY);
        return val == null ? defaultDelay : val;
    }

    private long getDownloadDelay(int delayIndex) {
        String[] list = getPacChangeDelay().split(Separators.SP);
        if (delayIndex < list.length) {
            return Long.parseLong(list[delayIndex]);
        }
        return 0L;
    }

    private void setDownloadIn(int delayIndex) {
        long delay = getDownloadDelay(delayIndex);
        long timeTillTrigger = (1000 * delay) + SystemClock.elapsedRealtime();
        getAlarmManager().set(3, timeTillTrigger, this.mPacRefreshIntent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean setCurrentProxyScript(String script) {
        if (this.mProxyService == null) {
            Log.e(TAG, "setCurrentProxyScript: no proxy service");
            return false;
        }
        try {
            this.mProxyService.setPacFile(script);
            this.mCurrentPac = script;
            return true;
        } catch (RemoteException e) {
            Log.e(TAG, "Unable to set PAC file", e);
            return true;
        }
    }

    private void bind() {
        if (this.mContext == null) {
            Log.e(TAG, "No context for binding");
            return;
        }
        Intent intent = new Intent();
        intent.setClassName(PAC_PACKAGE, PAC_SERVICE);
        if (this.mProxyConnection != null && this.mConnection != null) {
            if (this.mLastPort != -1) {
                sendPacBroadcast(new ProxyProperties(this.mPacUrl, this.mLastPort));
                return;
            } else {
                Log.e(TAG, "Received invalid port from Local Proxy, PAC will not be operational");
                return;
            }
        }
        this.mConnection = new ServiceConnection() { // from class: com.android.server.connectivity.PacManager.2
            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName component) {
                synchronized (PacManager.this.mProxyLock) {
                    PacManager.this.mProxyService = null;
                }
            }

            @Override // android.content.ServiceConnection
            public void onServiceConnected(ComponentName component, IBinder binder) {
                synchronized (PacManager.this.mProxyLock) {
                    try {
                        Log.d(PacManager.TAG, "Adding service com.android.net.IProxyService " + binder.getInterfaceDescriptor());
                    } catch (RemoteException e1) {
                        Log.e(PacManager.TAG, "Remote Exception", e1);
                    }
                    ServiceManager.addService("com.android.net.IProxyService", binder);
                    PacManager.this.mProxyService = IProxyService.Stub.asInterface(binder);
                    if (PacManager.this.mProxyService != null) {
                        try {
                            PacManager.this.mProxyService.startPacSystem();
                        } catch (RemoteException e) {
                            Log.e(PacManager.TAG, "Unable to reach ProxyService - PAC will not be started", e);
                        }
                        IoThread.getHandler().post(PacManager.this.mPacDownloader);
                    } else {
                        Log.e(PacManager.TAG, "No proxy service");
                    }
                }
            }
        };
        this.mContext.bindService(intent, this.mConnection, 1073741829);
        Intent intent2 = new Intent();
        intent2.setClassName(PROXY_PACKAGE, PROXY_SERVICE);
        this.mProxyConnection = new ServiceConnection() { // from class: com.android.server.connectivity.PacManager.3
            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName component) {
            }

            @Override // android.content.ServiceConnection
            public void onServiceConnected(ComponentName component, IBinder binder) {
                IProxyCallback callbackService = IProxyCallback.Stub.asInterface(binder);
                if (callbackService != null) {
                    try {
                        callbackService.getProxyPort(new IProxyPortListener.Stub() { // from class: com.android.server.connectivity.PacManager.3.1
                            @Override // com.android.net.IProxyPortListener
                            public void setProxyPort(int port) throws RemoteException {
                                if (PacManager.this.mLastPort != -1) {
                                    PacManager.this.mHasSentBroadcast = false;
                                }
                                PacManager.this.mLastPort = port;
                                if (port != -1) {
                                    Log.d(PacManager.TAG, "Local proxy is bound on " + port);
                                    PacManager.this.sendProxyIfNeeded();
                                    return;
                                }
                                Log.e(PacManager.TAG, "Received invalid port from Local Proxy, PAC will not be operational");
                            }
                        });
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        this.mContext.bindService(intent2, this.mProxyConnection, 1073741829);
    }

    private void unbind() {
        if (this.mConnection != null) {
            this.mContext.unbindService(this.mConnection);
            this.mConnection = null;
        }
        if (this.mProxyConnection != null) {
            this.mContext.unbindService(this.mProxyConnection);
            this.mProxyConnection = null;
        }
        this.mProxyService = null;
        this.mLastPort = -1;
    }

    private void sendPacBroadcast(ProxyProperties proxy) {
        this.mConnectivityHandler.sendMessage(this.mConnectivityHandler.obtainMessage(this.mProxyMessage, proxy));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void sendProxyIfNeeded() {
        if (this.mHasDownloaded && this.mLastPort != -1 && !this.mHasSentBroadcast) {
            sendPacBroadcast(new ProxyProperties(this.mPacUrl, this.mLastPort));
            this.mHasSentBroadcast = true;
        }
    }
}