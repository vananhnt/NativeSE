package com.android.server.wifi;

import android.Manifest;
import android.app.AppOpsManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.DhcpInfo;
import android.net.DhcpResults;
import android.net.LinkAddress;
import android.net.NetworkUtils;
import android.net.RouteInfo;
import android.net.wifi.BatchedScanSettings;
import android.net.wifi.IWifiManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiStateMachine;
import android.net.wifi.WifiWatchdogStateMachine;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.WorkSource;
import android.provider.Settings;
import android.util.Slog;
import com.android.internal.R;
import com.android.internal.app.IBatteryStats;
import com.android.internal.telephony.PhoneConstants;
import com.android.internal.telephony.TelephonyIntents;
import com.android.internal.util.AsyncChannel;
import com.android.server.am.BatteryStatsService;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: WifiService.class */
public final class WifiService extends IWifiManager.Stub {
    private static final String TAG = "WifiService";
    private static final boolean DBG = false;
    final WifiStateMachine mWifiStateMachine;
    private final Context mContext;
    private int mFullHighPerfLocksAcquired;
    private int mFullHighPerfLocksReleased;
    private int mFullLocksAcquired;
    private int mFullLocksReleased;
    private int mScanLocksAcquired;
    private int mScanLocksReleased;
    private int mMulticastEnabled;
    private int mMulticastDisabled;
    private final IBatteryStats mBatteryStats;
    private final AppOpsManager mAppOps;
    private WifiNotificationController mNotificationController;
    private WifiTrafficPoller mTrafficPoller;
    final WifiSettingsStore mSettingsStore;
    final boolean mBatchedScanSupported;
    private AsyncChannel mWifiStateMachineChannel;
    private ClientHandler mClientHandler;
    WifiStateMachineHandler mWifiStateMachineHandler;
    private WifiWatchdogStateMachine mWifiWatchdogStateMachine;
    private WifiController mWifiController;
    final LockList mLocks = new LockList();
    private final List<Multicaster> mMulticasters = new ArrayList();
    private final List<BatchedScanRequest> mBatchedScanners = new ArrayList();
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.android.server.wifi.WifiService.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_SCREEN_ON)) {
                WifiService.this.mWifiController.sendMessage(155650);
            } else if (action.equals(Intent.ACTION_USER_PRESENT)) {
                WifiService.this.mWifiController.sendMessage(155660);
            } else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                WifiService.this.mWifiController.sendMessage(155651);
            } else if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                int pluggedType = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
                WifiService.this.mWifiController.sendMessage(155652, pluggedType, 0, null);
            } else if (action.equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, 0);
                WifiService.this.mWifiStateMachine.sendBluetoothAdapterStateChange(state);
            } else if (action.equals(TelephonyIntents.ACTION_EMERGENCY_CALLBACK_MODE_CHANGED)) {
                boolean emergencyMode = intent.getBooleanExtra(PhoneConstants.PHONE_IN_ECM_STATE, false);
                WifiService.this.mWifiController.sendMessage(155649, emergencyMode ? 1 : 0, 0);
            }
        }
    };
    private String mInterfaceName = SystemProperties.get("wifi.interface", "wlan0");

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wifi.WifiService.getBatchedScanResults(java.lang.String):java.util.List<android.net.wifi.BatchedScanResult>, file: WifiService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    @Override // android.net.wifi.IWifiManager
    public java.util.List<android.net.wifi.BatchedScanResult> getBatchedScanResults(java.lang.String r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wifi.WifiService.getBatchedScanResults(java.lang.String):java.util.List<android.net.wifi.BatchedScanResult>, file: WifiService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.WifiService.getBatchedScanResults(java.lang.String):java.util.List");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wifi.WifiService.setWifiEnabled(boolean):boolean, file: WifiService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    @Override // android.net.wifi.IWifiManager
    public synchronized boolean setWifiEnabled(boolean r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wifi.WifiService.setWifiEnabled(boolean):boolean, file: WifiService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.WifiService.setWifiEnabled(boolean):boolean");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wifi.WifiService.getScanResults(java.lang.String):java.util.List<android.net.wifi.ScanResult>, file: WifiService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    @Override // android.net.wifi.IWifiManager
    public java.util.List<android.net.wifi.ScanResult> getScanResults(java.lang.String r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wifi.WifiService.getScanResults(java.lang.String):java.util.List<android.net.wifi.ScanResult>, file: WifiService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.WifiService.getScanResults(java.lang.String):java.util.List");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wifi.WifiService.setCountryCode(java.lang.String, boolean):void, file: WifiService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    @Override // android.net.wifi.IWifiManager
    public void setCountryCode(java.lang.String r1, boolean r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wifi.WifiService.setCountryCode(java.lang.String, boolean):void, file: WifiService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.WifiService.setCountryCode(java.lang.String, boolean):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wifi.WifiService.setFrequencyBand(int, boolean):void, file: WifiService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    @Override // android.net.wifi.IWifiManager
    public void setFrequencyBand(int r1, boolean r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wifi.WifiService.setFrequencyBand(int, boolean):void, file: WifiService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.WifiService.setFrequencyBand(int, boolean):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wifi.WifiService.acquireWifiLockLocked(com.android.server.wifi.WifiService$WifiLock):boolean, file: WifiService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private boolean acquireWifiLockLocked(com.android.server.wifi.WifiService.WifiLock r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wifi.WifiService.acquireWifiLockLocked(com.android.server.wifi.WifiService$WifiLock):boolean, file: WifiService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.WifiService.acquireWifiLockLocked(com.android.server.wifi.WifiService$WifiLock):boolean");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wifi.WifiService.updateWifiLockWorkSource(android.os.IBinder, android.os.WorkSource):void, file: WifiService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    @Override // android.net.wifi.IWifiManager
    public void updateWifiLockWorkSource(android.os.IBinder r1, android.os.WorkSource r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wifi.WifiService.updateWifiLockWorkSource(android.os.IBinder, android.os.WorkSource):void, file: WifiService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.WifiService.updateWifiLockWorkSource(android.os.IBinder, android.os.WorkSource):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wifi.WifiService.releaseWifiLockLocked(android.os.IBinder):boolean, file: WifiService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    /* JADX INFO: Access modifiers changed from: private */
    public boolean releaseWifiLockLocked(android.os.IBinder r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wifi.WifiService.releaseWifiLockLocked(android.os.IBinder):boolean, file: WifiService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.WifiService.releaseWifiLockLocked(android.os.IBinder):boolean");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wifi.WifiService.acquireMulticastLock(android.os.IBinder, java.lang.String):void, file: WifiService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    @Override // android.net.wifi.IWifiManager
    public void acquireMulticastLock(android.os.IBinder r1, java.lang.String r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wifi.WifiService.acquireMulticastLock(android.os.IBinder, java.lang.String):void, file: WifiService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.WifiService.acquireMulticastLock(android.os.IBinder, java.lang.String):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wifi.WifiService.removeMulticasterLocked(int, int):void, file: WifiService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    /* JADX INFO: Access modifiers changed from: private */
    public void removeMulticasterLocked(int r1, int r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wifi.WifiService.removeMulticasterLocked(int, int):void, file: WifiService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.WifiService.removeMulticasterLocked(int, int):void");
    }

    /* loaded from: WifiService$ClientHandler.class */
    private class ClientHandler extends Handler {
        ClientHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 69632:
                    if (msg.arg1 == 0) {
                        WifiService.this.mTrafficPoller.addClient(msg.replyTo);
                        return;
                    } else {
                        Slog.e(WifiService.TAG, "Client connection failure, error=" + msg.arg1);
                        return;
                    }
                case AsyncChannel.CMD_CHANNEL_FULL_CONNECTION /* 69633 */:
                    AsyncChannel ac = new AsyncChannel();
                    ac.connect(WifiService.this.mContext, this, msg.replyTo);
                    return;
                case AsyncChannel.CMD_CHANNEL_DISCONNECTED /* 69636 */:
                    if (msg.arg1 == 2) {
                    }
                    WifiService.this.mTrafficPoller.removeClient(msg.replyTo);
                    return;
                case WifiManager.CONNECT_NETWORK /* 151553 */:
                case WifiManager.SAVE_NETWORK /* 151559 */:
                    WifiConfiguration config = (WifiConfiguration) msg.obj;
                    int networkId = msg.arg1;
                    if (config != null && config.isValid()) {
                        if (config.proxySettings != WifiConfiguration.ProxySettings.PAC) {
                            WifiService.this.mWifiStateMachine.sendMessage(Message.obtain(msg));
                            return;
                        }
                        Slog.e(WifiService.TAG, "ClientHandler.handleMessage cannot process msg with PAC");
                        if (msg.what == 151553) {
                            replyFailed(msg, WifiManager.CONNECT_NETWORK_FAILED);
                            return;
                        } else {
                            replyFailed(msg, WifiManager.SAVE_NETWORK_FAILED);
                            return;
                        }
                    } else if (config == null && networkId != -1) {
                        WifiService.this.mWifiStateMachine.sendMessage(Message.obtain(msg));
                        return;
                    } else {
                        Slog.e(WifiService.TAG, "ClientHandler.handleMessage ignoring invalid msg=" + msg);
                        if (msg.what == 151553) {
                            replyFailed(msg, WifiManager.CONNECT_NETWORK_FAILED);
                            return;
                        } else {
                            replyFailed(msg, WifiManager.SAVE_NETWORK_FAILED);
                            return;
                        }
                    }
                case WifiManager.FORGET_NETWORK /* 151556 */:
                case WifiManager.START_WPS /* 151562 */:
                case WifiManager.CANCEL_WPS /* 151566 */:
                case WifiManager.DISABLE_NETWORK /* 151569 */:
                case WifiManager.RSSI_PKTCNT_FETCH /* 151572 */:
                    WifiService.this.mWifiStateMachine.sendMessage(Message.obtain(msg));
                    return;
                default:
                    Slog.d(WifiService.TAG, "ClientHandler.handleMessage ignoring msg=" + msg);
                    return;
            }
        }

        private void replyFailed(Message msg, int what) {
            Message reply = Message.obtain();
            reply.what = what;
            reply.arg1 = 8;
            try {
                msg.replyTo.send(reply);
            } catch (RemoteException e) {
            }
        }
    }

    /* loaded from: WifiService$WifiStateMachineHandler.class */
    private class WifiStateMachineHandler extends Handler {
        private AsyncChannel mWsmChannel;

        WifiStateMachineHandler(Looper looper) {
            super(looper);
            this.mWsmChannel = new AsyncChannel();
            this.mWsmChannel.connect(WifiService.this.mContext, this, WifiService.this.mWifiStateMachine.getHandler());
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 69632:
                    if (msg.arg1 == 0) {
                        WifiService.this.mWifiStateMachineChannel = this.mWsmChannel;
                        return;
                    }
                    Slog.e(WifiService.TAG, "WifiStateMachine connection failure, error=" + msg.arg1);
                    WifiService.this.mWifiStateMachineChannel = null;
                    return;
                case AsyncChannel.CMD_CHANNEL_DISCONNECTED /* 69636 */:
                    Slog.e(WifiService.TAG, "WifiStateMachine channel lost, msg.arg1 =" + msg.arg1);
                    WifiService.this.mWifiStateMachineChannel = null;
                    this.mWsmChannel.connect(WifiService.this.mContext, this, WifiService.this.mWifiStateMachine.getHandler());
                    return;
                default:
                    Slog.d(WifiService.TAG, "WifiStateMachineHandler.handleMessage ignoring msg=" + msg);
                    return;
            }
        }
    }

    public WifiService(Context context) {
        this.mContext = context;
        this.mWifiStateMachine = new WifiStateMachine(this.mContext, this.mInterfaceName);
        this.mWifiStateMachine.enableRssiPolling(true);
        this.mBatteryStats = BatteryStatsService.getService();
        this.mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        this.mNotificationController = new WifiNotificationController(this.mContext, this.mWifiStateMachine);
        this.mTrafficPoller = new WifiTrafficPoller(this.mContext, this.mInterfaceName);
        this.mSettingsStore = new WifiSettingsStore(this.mContext);
        HandlerThread wifiThread = new HandlerThread(TAG);
        wifiThread.start();
        this.mClientHandler = new ClientHandler(wifiThread.getLooper());
        this.mWifiStateMachineHandler = new WifiStateMachineHandler(wifiThread.getLooper());
        this.mWifiController = new WifiController(this.mContext, this, wifiThread.getLooper());
        this.mWifiController.start();
        this.mBatchedScanSupported = this.mContext.getResources().getBoolean(R.bool.config_wifi_batched_scan_supported);
        registerForScanModeChange();
        this.mContext.registerReceiver(new BroadcastReceiver() { // from class: com.android.server.wifi.WifiService.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if (WifiService.this.mSettingsStore.handleAirplaneModeToggled()) {
                    WifiService.this.mWifiController.sendMessage(155657);
                }
            }
        }, new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED));
        registerForBroadcasts();
    }

    public void checkAndStartWifi() {
        boolean wifiEnabled = this.mSettingsStore.isWifiToggleEnabled();
        Slog.i(TAG, "WifiService starting up with Wi-Fi " + (wifiEnabled ? "enabled" : "disabled"));
        if (wifiEnabled) {
            setWifiEnabled(wifiEnabled);
        }
        this.mWifiWatchdogStateMachine = WifiWatchdogStateMachine.makeWifiWatchdogStateMachine(this.mContext);
    }

    @Override // android.net.wifi.IWifiManager
    public boolean pingSupplicant() {
        enforceAccessPermission();
        if (this.mWifiStateMachineChannel != null) {
            return this.mWifiStateMachine.syncPingSupplicant(this.mWifiStateMachineChannel);
        }
        Slog.e(TAG, "mWifiStateMachineChannel is not initialized");
        return false;
    }

    @Override // android.net.wifi.IWifiManager
    public void startScan(WorkSource workSource) {
        enforceChangePermission();
        if (workSource != null) {
            enforceWorkSourcePermission();
            workSource.clearNames();
        }
        this.mWifiStateMachine.startScan(Binder.getCallingUid(), workSource);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: WifiService$BatchedScanRequest.class */
    public class BatchedScanRequest extends DeathRecipient {
        BatchedScanSettings settings;
        int uid;
        int pid;

        BatchedScanRequest(BatchedScanSettings settings, IBinder binder) {
            super(0, null, binder, null);
            this.settings = settings;
            this.uid = Binder.getCallingUid();
            this.pid = Binder.getCallingPid();
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            WifiService.this.stopBatchedScan(this.settings, this.uid, this.pid);
        }

        public String toString() {
            return "BatchedScanRequest{settings=" + this.settings + ", binder=" + this.mBinder + "}";
        }

        public boolean isSameApp(int uid, int pid) {
            return this.uid == uid && this.pid == pid;
        }
    }

    @Override // android.net.wifi.IWifiManager
    public boolean isBatchedScanSupported() {
        return this.mBatchedScanSupported;
    }

    @Override // android.net.wifi.IWifiManager
    public void pollBatchedScan() {
        enforceChangePermission();
        if (this.mBatchedScanSupported) {
            this.mWifiStateMachine.requestBatchedScanPoll();
        }
    }

    @Override // android.net.wifi.IWifiManager
    public boolean requestBatchedScan(BatchedScanSettings requested, IBinder binder) {
        enforceChangePermission();
        if (this.mBatchedScanSupported) {
            BatchedScanSettings requested2 = new BatchedScanSettings(requested);
            if (requested2.isInvalid()) {
                return false;
            }
            BatchedScanRequest r = new BatchedScanRequest(requested2, binder);
            synchronized (this.mBatchedScanners) {
                this.mBatchedScanners.add(r);
                resolveBatchedScannersLocked();
            }
            return true;
        }
        return false;
    }

    @Override // android.net.wifi.IWifiManager
    public void stopBatchedScan(BatchedScanSettings settings) {
        enforceChangePermission();
        if (this.mBatchedScanSupported) {
            stopBatchedScan(settings, getCallingUid(), getCallingPid());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopBatchedScan(BatchedScanSettings settings, int uid, int pid) {
        ArrayList<BatchedScanRequest> found = new ArrayList<>();
        synchronized (this.mBatchedScanners) {
            for (BatchedScanRequest r : this.mBatchedScanners) {
                if (r.isSameApp(uid, pid) && (settings == null || settings.equals(r.settings))) {
                    found.add(r);
                    if (settings != null) {
                        break;
                    }
                }
            }
            Iterator i$ = found.iterator();
            while (i$.hasNext()) {
                this.mBatchedScanners.remove(i$.next());
            }
            if (found.size() != 0) {
                resolveBatchedScannersLocked();
            }
        }
    }

    private void resolveBatchedScannersLocked() {
        BatchedScanSettings setting = new BatchedScanSettings();
        int responsibleUid = 0;
        if (this.mBatchedScanners.size() == 0) {
            this.mWifiStateMachine.setBatchedScanSettings(null, 0);
            return;
        }
        for (BatchedScanRequest r : this.mBatchedScanners) {
            BatchedScanSettings s = r.settings;
            if (s.maxScansPerBatch != Integer.MAX_VALUE && s.maxScansPerBatch < setting.maxScansPerBatch) {
                setting.maxScansPerBatch = s.maxScansPerBatch;
                responsibleUid = r.uid;
            }
            if (s.maxApPerScan != Integer.MAX_VALUE && (setting.maxApPerScan == Integer.MAX_VALUE || s.maxApPerScan > setting.maxApPerScan)) {
                setting.maxApPerScan = s.maxApPerScan;
            }
            if (s.scanIntervalSec != Integer.MAX_VALUE && s.scanIntervalSec < setting.scanIntervalSec) {
                setting.scanIntervalSec = s.scanIntervalSec;
                responsibleUid = r.uid;
            }
            if (s.maxApForDistance != Integer.MAX_VALUE && (setting.maxApForDistance == Integer.MAX_VALUE || s.maxApForDistance > setting.maxApForDistance)) {
                setting.maxApForDistance = s.maxApForDistance;
            }
            if (s.channelSet != null && s.channelSet.size() != 0) {
                if (setting.channelSet == null || setting.channelSet.size() != 0) {
                    if (setting.channelSet == null) {
                        setting.channelSet = new ArrayList();
                    }
                    for (String i : s.channelSet) {
                        if (!setting.channelSet.contains(i)) {
                            setting.channelSet.add(i);
                        }
                    }
                }
            } else if (setting.channelSet == null || setting.channelSet.size() != 0) {
                setting.channelSet = new ArrayList();
            }
        }
        setting.constrain();
        this.mWifiStateMachine.setBatchedScanSettings(setting, responsibleUid);
    }

    private void enforceAccessPermission() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.ACCESS_WIFI_STATE, TAG);
    }

    private void enforceChangePermission() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CHANGE_WIFI_STATE, TAG);
    }

    private void enforceWorkSourcePermission() {
        this.mContext.enforceCallingPermission(Manifest.permission.UPDATE_DEVICE_STATS, TAG);
    }

    private void enforceMulticastChangePermission() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CHANGE_WIFI_MULTICAST_STATE, TAG);
    }

    private void enforceConnectivityInternalPermission() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, "ConnectivityService");
    }

    @Override // android.net.wifi.IWifiManager
    public int getWifiEnabledState() {
        enforceAccessPermission();
        return this.mWifiStateMachine.syncGetWifiState();
    }

    @Override // android.net.wifi.IWifiManager
    public void setWifiApEnabled(WifiConfiguration wifiConfig, boolean enabled) {
        enforceChangePermission();
        if (wifiConfig != null && !wifiConfig.isValid()) {
            Slog.e(TAG, "Invalid WifiConfiguration");
        } else {
            this.mWifiController.obtainMessage(155658, enabled ? 1 : 0, 0, wifiConfig).sendToTarget();
        }
    }

    @Override // android.net.wifi.IWifiManager
    public int getWifiApEnabledState() {
        enforceAccessPermission();
        return this.mWifiStateMachine.syncGetWifiApState();
    }

    @Override // android.net.wifi.IWifiManager
    public WifiConfiguration getWifiApConfiguration() {
        enforceAccessPermission();
        return this.mWifiStateMachine.syncGetWifiApConfiguration();
    }

    @Override // android.net.wifi.IWifiManager
    public void setWifiApConfiguration(WifiConfiguration wifiConfig) {
        enforceChangePermission();
        if (wifiConfig == null) {
            return;
        }
        if (wifiConfig.isValid()) {
            this.mWifiStateMachine.setWifiApConfiguration(wifiConfig);
        } else {
            Slog.e(TAG, "Invalid WifiConfiguration");
        }
    }

    @Override // android.net.wifi.IWifiManager
    public boolean isScanAlwaysAvailable() {
        enforceAccessPermission();
        return this.mSettingsStore.isScanAlwaysAvailable();
    }

    @Override // android.net.wifi.IWifiManager
    public void disconnect() {
        enforceChangePermission();
        this.mWifiStateMachine.disconnectCommand();
    }

    @Override // android.net.wifi.IWifiManager
    public void reconnect() {
        enforceChangePermission();
        this.mWifiStateMachine.reconnectCommand();
    }

    @Override // android.net.wifi.IWifiManager
    public void reassociate() {
        enforceChangePermission();
        this.mWifiStateMachine.reassociateCommand();
    }

    @Override // android.net.wifi.IWifiManager
    public List<WifiConfiguration> getConfiguredNetworks() {
        enforceAccessPermission();
        if (this.mWifiStateMachineChannel != null) {
            return this.mWifiStateMachine.syncGetConfiguredNetworks(this.mWifiStateMachineChannel);
        }
        Slog.e(TAG, "mWifiStateMachineChannel is not initialized");
        return null;
    }

    @Override // android.net.wifi.IWifiManager
    public int addOrUpdateNetwork(WifiConfiguration config) {
        enforceChangePermission();
        if (config.proxySettings == WifiConfiguration.ProxySettings.PAC) {
            enforceConnectivityInternalPermission();
        }
        if (config.isValid()) {
            if (this.mWifiStateMachineChannel != null) {
                return this.mWifiStateMachine.syncAddOrUpdateNetwork(this.mWifiStateMachineChannel, config);
            }
            Slog.e(TAG, "mWifiStateMachineChannel is not initialized");
            return -1;
        }
        Slog.e(TAG, "bad network configuration");
        return -1;
    }

    @Override // android.net.wifi.IWifiManager
    public boolean removeNetwork(int netId) {
        enforceChangePermission();
        if (this.mWifiStateMachineChannel != null) {
            return this.mWifiStateMachine.syncRemoveNetwork(this.mWifiStateMachineChannel, netId);
        }
        Slog.e(TAG, "mWifiStateMachineChannel is not initialized");
        return false;
    }

    @Override // android.net.wifi.IWifiManager
    public boolean enableNetwork(int netId, boolean disableOthers) {
        enforceChangePermission();
        if (this.mWifiStateMachineChannel != null) {
            return this.mWifiStateMachine.syncEnableNetwork(this.mWifiStateMachineChannel, netId, disableOthers);
        }
        Slog.e(TAG, "mWifiStateMachineChannel is not initialized");
        return false;
    }

    @Override // android.net.wifi.IWifiManager
    public boolean disableNetwork(int netId) {
        enforceChangePermission();
        if (this.mWifiStateMachineChannel != null) {
            return this.mWifiStateMachine.syncDisableNetwork(this.mWifiStateMachineChannel, netId);
        }
        Slog.e(TAG, "mWifiStateMachineChannel is not initialized");
        return false;
    }

    @Override // android.net.wifi.IWifiManager
    public WifiInfo getConnectionInfo() {
        enforceAccessPermission();
        return this.mWifiStateMachine.syncRequestConnectionInfo();
    }

    @Override // android.net.wifi.IWifiManager
    public boolean saveConfiguration() {
        enforceChangePermission();
        if (this.mWifiStateMachineChannel != null) {
            return this.mWifiStateMachine.syncSaveConfig(this.mWifiStateMachineChannel);
        }
        Slog.e(TAG, "mWifiStateMachineChannel is not initialized");
        return false;
    }

    @Override // android.net.wifi.IWifiManager
    public int getFrequencyBand() {
        enforceAccessPermission();
        return this.mWifiStateMachine.getFrequencyBand();
    }

    @Override // android.net.wifi.IWifiManager
    public boolean isDualBandSupported() {
        return this.mContext.getResources().getBoolean(R.bool.config_wifi_dual_band_support);
    }

    @Override // android.net.wifi.IWifiManager
    public DhcpInfo getDhcpInfo() {
        enforceAccessPermission();
        DhcpResults dhcpResults = this.mWifiStateMachine.syncGetDhcpResults();
        if (dhcpResults.linkProperties == null) {
            return null;
        }
        DhcpInfo info = new DhcpInfo();
        Iterator i$ = dhcpResults.linkProperties.getLinkAddresses().iterator();
        while (true) {
            if (!i$.hasNext()) {
                break;
            }
            LinkAddress la = i$.next();
            InetAddress addr = la.getAddress();
            if (addr instanceof Inet4Address) {
                info.ipAddress = NetworkUtils.inetAddressToInt((Inet4Address) addr);
                break;
            }
        }
        for (RouteInfo r : dhcpResults.linkProperties.getRoutes()) {
            if (r.isDefaultRoute()) {
                InetAddress gateway = r.getGateway();
                if (gateway instanceof Inet4Address) {
                    info.gateway = NetworkUtils.inetAddressToInt((Inet4Address) gateway);
                }
            } else if (!r.hasGateway()) {
                LinkAddress dest = r.getDestination();
                if (dest.getAddress() instanceof Inet4Address) {
                    info.netmask = NetworkUtils.prefixLengthToNetmaskInt(dest.getNetworkPrefixLength());
                }
            }
        }
        int dnsFound = 0;
        for (InetAddress dns : dhcpResults.linkProperties.getDnses()) {
            if (dns instanceof Inet4Address) {
                if (dnsFound == 0) {
                    info.dns1 = NetworkUtils.inetAddressToInt((Inet4Address) dns);
                } else {
                    info.dns2 = NetworkUtils.inetAddressToInt((Inet4Address) dns);
                }
                dnsFound++;
                if (dnsFound > 1) {
                    break;
                }
            }
        }
        InetAddress serverAddress = dhcpResults.serverAddress;
        if (serverAddress instanceof Inet4Address) {
            info.serverAddress = NetworkUtils.inetAddressToInt((Inet4Address) serverAddress);
        }
        info.leaseDuration = dhcpResults.leaseDuration;
        return info;
    }

    @Override // android.net.wifi.IWifiManager
    public void startWifi() {
        enforceConnectivityInternalPermission();
        this.mWifiStateMachine.setDriverStart(true);
        this.mWifiStateMachine.reconnectCommand();
    }

    @Override // android.net.wifi.IWifiManager
    public void captivePortalCheckComplete() {
        enforceConnectivityInternalPermission();
        this.mWifiStateMachine.captivePortalCheckComplete();
    }

    @Override // android.net.wifi.IWifiManager
    public void stopWifi() {
        enforceConnectivityInternalPermission();
        this.mWifiStateMachine.setDriverStart(false);
    }

    @Override // android.net.wifi.IWifiManager
    public void addToBlacklist(String bssid) {
        enforceChangePermission();
        this.mWifiStateMachine.addToBlacklist(bssid);
    }

    @Override // android.net.wifi.IWifiManager
    public void clearBlacklist() {
        enforceChangePermission();
        this.mWifiStateMachine.clearBlacklist();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: WifiService$TdlsTaskParams.class */
    public class TdlsTaskParams {
        public String remoteIpAddress;
        public boolean enable;

        TdlsTaskParams() {
        }
    }

    /* loaded from: WifiService$TdlsTask.class */
    class TdlsTask extends AsyncTask<TdlsTaskParams, Integer, Integer> {
        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wifi.WifiService.TdlsTask.doInBackground(com.android.server.wifi.WifiService$TdlsTaskParams[]):java.lang.Integer, file: WifiService$TdlsTask.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public java.lang.Integer doInBackground(com.android.server.wifi.WifiService.TdlsTaskParams... r1) {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wifi.WifiService.TdlsTask.doInBackground(com.android.server.wifi.WifiService$TdlsTaskParams[]):java.lang.Integer, file: WifiService$TdlsTask.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.WifiService.TdlsTask.doInBackground(com.android.server.wifi.WifiService$TdlsTaskParams[]):java.lang.Integer");
        }

        TdlsTask() {
        }
    }

    @Override // android.net.wifi.IWifiManager
    public void enableTdls(String remoteAddress, boolean enable) {
        TdlsTaskParams params = new TdlsTaskParams();
        params.remoteIpAddress = remoteAddress;
        params.enable = enable;
        new TdlsTask().execute(params);
    }

    @Override // android.net.wifi.IWifiManager
    public void enableTdlsWithMacAddress(String remoteMacAddress, boolean enable) {
        this.mWifiStateMachine.enableTdls(remoteMacAddress, enable);
    }

    @Override // android.net.wifi.IWifiManager
    public Messenger getWifiServiceMessenger() {
        enforceAccessPermission();
        enforceChangePermission();
        return new Messenger(this.mClientHandler);
    }

    @Override // android.net.wifi.IWifiManager
    public Messenger getWifiStateMachineMessenger() {
        enforceAccessPermission();
        enforceChangePermission();
        return this.mWifiStateMachine.getMessenger();
    }

    @Override // android.net.wifi.IWifiManager
    public String getConfigFile() {
        enforceAccessPermission();
        return this.mWifiStateMachine.getConfigFile();
    }

    private void registerForScanModeChange() {
        ContentObserver contentObserver = new ContentObserver(null) { // from class: com.android.server.wifi.WifiService.3
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange) {
                WifiService.this.mSettingsStore.handleWifiScanAlwaysAvailableToggled();
                WifiService.this.mWifiController.sendMessage(155655);
            }
        };
        this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor(Settings.Global.WIFI_SCAN_ALWAYS_AVAILABLE), false, contentObserver);
    }

    private void registerForBroadcasts() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_USER_PRESENT);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        intentFilter.addAction(TelephonyIntents.ACTION_EMERGENCY_CALLBACK_MODE_CHANGED);
        this.mContext.registerReceiver(this.mReceiver, intentFilter);
    }

    @Override // android.os.Binder
    protected void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.DUMP) != 0) {
            pw.println("Permission Denial: can't dump WifiService from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
            return;
        }
        pw.println("Wi-Fi is " + this.mWifiStateMachine.syncGetWifiStateByName());
        pw.println("Stay-awake conditions: " + Settings.Global.getInt(this.mContext.getContentResolver(), "stay_on_while_plugged_in", 0));
        pw.println("mMulticastEnabled " + this.mMulticastEnabled);
        pw.println("mMulticastDisabled " + this.mMulticastDisabled);
        this.mWifiController.dump(fd, pw, args);
        this.mSettingsStore.dump(fd, pw, args);
        this.mNotificationController.dump(fd, pw, args);
        this.mTrafficPoller.dump(fd, pw, args);
        pw.println("Latest scan results:");
        List<ScanResult> scanResults = this.mWifiStateMachine.syncGetScanResultsList();
        if (scanResults != null && scanResults.size() != 0) {
            pw.println("  BSSID              Frequency   RSSI  Flags             SSID");
            for (ScanResult r : scanResults) {
                Object[] objArr = new Object[5];
                objArr[0] = r.BSSID;
                objArr[1] = Integer.valueOf(r.frequency);
                objArr[2] = Integer.valueOf(r.level);
                objArr[3] = r.capabilities;
                objArr[4] = r.SSID == null ? "" : r.SSID;
                pw.printf("  %17s  %9d  %5d  %-16s  %s%n", objArr);
            }
        }
        pw.println();
        pw.println("Locks acquired: " + this.mFullLocksAcquired + " full, " + this.mFullHighPerfLocksAcquired + " full high perf, " + this.mScanLocksAcquired + " scan");
        pw.println("Locks released: " + this.mFullLocksReleased + " full, " + this.mFullHighPerfLocksReleased + " full high perf, " + this.mScanLocksReleased + " scan");
        pw.println();
        pw.println("Locks held:");
        this.mLocks.dump(pw);
        this.mWifiWatchdogStateMachine.dump(fd, pw, args);
        pw.println();
        this.mWifiStateMachine.dump(fd, pw, args);
        pw.println();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: WifiService$WifiLock.class */
    public class WifiLock extends DeathRecipient {
        WifiLock(int lockMode, String tag, IBinder binder, WorkSource ws) {
            super(lockMode, tag, binder, ws);
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            synchronized (WifiService.this.mLocks) {
                WifiService.this.releaseWifiLockLocked(this.mBinder);
            }
        }

        public String toString() {
            return "WifiLock{" + this.mTag + " type=" + this.mMode + " binder=" + this.mBinder + "}";
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: WifiService$LockList.class */
    public class LockList {
        private List<WifiLock> mList;

        static /* synthetic */ void access$1200(LockList x0, WifiLock x1) {
            x0.addLock(x1);
        }

        static /* synthetic */ int access$1300(LockList x0, IBinder x1) {
            return x0.findLockByBinder(x1);
        }

        static /* synthetic */ List access$1400(LockList x0) {
            return x0.mList;
        }

        static /* synthetic */ WifiLock access$1500(LockList x0, IBinder x1) {
            return x0.removeLock(x1);
        }

        private LockList() {
            this.mList = new ArrayList();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public synchronized boolean hasLocks() {
            return !this.mList.isEmpty();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public synchronized int getStrongestLockMode() {
            if (!this.mList.isEmpty()) {
                if (WifiService.this.mFullHighPerfLocksAcquired <= WifiService.this.mFullHighPerfLocksReleased) {
                    if (WifiService.this.mFullLocksAcquired > WifiService.this.mFullLocksReleased) {
                        return 1;
                    }
                    return 2;
                }
                return 3;
            }
            return 1;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public synchronized void updateWorkSource(WorkSource ws) {
            for (int i = 0; i < WifiService.this.mLocks.mList.size(); i++) {
                ws.add(WifiService.this.mLocks.mList.get(i).mWorkSource);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void addLock(WifiLock lock) {
            if (findLockByBinder(lock.mBinder) < 0) {
                this.mList.add(lock);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public WifiLock removeLock(IBinder binder) {
            int index = findLockByBinder(binder);
            if (index >= 0) {
                WifiLock ret = this.mList.remove(index);
                ret.unlinkDeathRecipient();
                return ret;
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int findLockByBinder(IBinder binder) {
            int size = this.mList.size();
            for (int i = size - 1; i >= 0; i--) {
                if (this.mList.get(i).mBinder == binder) {
                    return i;
                }
            }
            return -1;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void dump(PrintWriter pw) {
            for (WifiLock l : this.mList) {
                pw.print("    ");
                pw.println(l);
            }
        }
    }

    void enforceWakeSourcePermission(int uid, int pid) {
        if (uid == Process.myUid()) {
            return;
        }
        this.mContext.enforcePermission(Manifest.permission.UPDATE_DEVICE_STATS, pid, uid, null);
    }

    @Override // android.net.wifi.IWifiManager
    public boolean acquireWifiLock(IBinder binder, int lockMode, String tag, WorkSource ws) {
        boolean acquireWifiLockLocked;
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.WAKE_LOCK, null);
        if (lockMode != 1 && lockMode != 2 && lockMode != 3) {
            Slog.e(TAG, "Illegal argument, lockMode= " + lockMode);
            return false;
        }
        if (ws != null && ws.size() == 0) {
            ws = null;
        }
        if (ws != null) {
            enforceWakeSourcePermission(Binder.getCallingUid(), Binder.getCallingPid());
        }
        if (ws == null) {
            ws = new WorkSource(Binder.getCallingUid());
        }
        WifiLock wifiLock = new WifiLock(lockMode, tag, binder, ws);
        synchronized (this.mLocks) {
            acquireWifiLockLocked = acquireWifiLockLocked(wifiLock);
        }
        return acquireWifiLockLocked;
    }

    private void noteAcquireWifiLock(WifiLock wifiLock) throws RemoteException {
        switch (wifiLock.mMode) {
            case 1:
            case 2:
            case 3:
                this.mBatteryStats.noteFullWifiLockAcquiredFromSource(wifiLock.mWorkSource);
                return;
            default:
                return;
        }
    }

    private void noteReleaseWifiLock(WifiLock wifiLock) throws RemoteException {
        switch (wifiLock.mMode) {
            case 1:
            case 2:
            case 3:
                this.mBatteryStats.noteFullWifiLockReleasedFromSource(wifiLock.mWorkSource);
                return;
            default:
                return;
        }
    }

    @Override // android.net.wifi.IWifiManager
    public boolean releaseWifiLock(IBinder lock) {
        boolean releaseWifiLockLocked;
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.WAKE_LOCK, null);
        synchronized (this.mLocks) {
            releaseWifiLockLocked = releaseWifiLockLocked(lock);
        }
        return releaseWifiLockLocked;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: WifiService$DeathRecipient.class */
    public abstract class DeathRecipient implements IBinder.DeathRecipient {
        String mTag;
        int mMode;
        IBinder mBinder;
        WorkSource mWorkSource;

        DeathRecipient(int mode, String tag, IBinder binder, WorkSource ws) {
            this.mTag = tag;
            this.mMode = mode;
            this.mBinder = binder;
            this.mWorkSource = ws;
            try {
                this.mBinder.linkToDeath(this, 0);
            } catch (RemoteException e) {
                binderDied();
            }
        }

        void unlinkDeathRecipient() {
            this.mBinder.unlinkToDeath(this, 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: WifiService$Multicaster.class */
    public class Multicaster extends DeathRecipient {
        Multicaster(String tag, IBinder binder) {
            super(Binder.getCallingUid(), tag, binder, null);
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            Slog.e(WifiService.TAG, "Multicaster binderDied");
            synchronized (WifiService.this.mMulticasters) {
                int i = WifiService.this.mMulticasters.indexOf(this);
                if (i != -1) {
                    WifiService.this.removeMulticasterLocked(i, this.mMode);
                }
            }
        }

        public String toString() {
            return "Multicaster{" + this.mTag + " binder=" + this.mBinder + "}";
        }

        public int getUid() {
            return this.mMode;
        }
    }

    @Override // android.net.wifi.IWifiManager
    public void initializeMulticastFiltering() {
        enforceMulticastChangePermission();
        synchronized (this.mMulticasters) {
            if (this.mMulticasters.size() != 0) {
                return;
            }
            this.mWifiStateMachine.startFilteringMulticastV4Packets();
        }
    }

    @Override // android.net.wifi.IWifiManager
    public void releaseMulticastLock() {
        enforceMulticastChangePermission();
        int uid = Binder.getCallingUid();
        synchronized (this.mMulticasters) {
            this.mMulticastDisabled++;
            int size = this.mMulticasters.size();
            for (int i = size - 1; i >= 0; i--) {
                Multicaster m = this.mMulticasters.get(i);
                if (m != null && m.getUid() == uid) {
                    removeMulticasterLocked(i, uid);
                }
            }
        }
    }

    @Override // android.net.wifi.IWifiManager
    public boolean isMulticastEnabled() {
        boolean z;
        enforceAccessPermission();
        synchronized (this.mMulticasters) {
            z = this.mMulticasters.size() > 0;
        }
        return z;
    }
}