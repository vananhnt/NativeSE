package com.android.server;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.LinkCapabilities;
import android.net.LinkProperties;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.UserHandle;
import android.telephony.CellInfo;
import android.telephony.CellLocation;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import com.android.internal.app.IBatteryStats;
import com.android.internal.telephony.DefaultPhoneNotifier;
import com.android.internal.telephony.IPhoneStateListener;
import com.android.internal.telephony.ITelephonyRegistry;
import com.android.internal.telephony.PhoneConstants;
import com.android.internal.telephony.TelephonyIntents;
import com.android.server.am.BatteryStatsService;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: TelephonyRegistry.class */
class TelephonyRegistry extends ITelephonyRegistry.Stub {
    private static final String TAG = "TelephonyRegistry";
    private static final boolean DBG = false;
    private static final boolean DBG_LOC = false;
    private final Context mContext;
    private final IBatteryStats mBatteryStats;
    private ArrayList<String> mConnectedApns;
    private LinkProperties mDataConnectionLinkProperties;
    private LinkCapabilities mDataConnectionLinkCapabilities;
    private int mDataConnectionNetworkType;
    static final int PHONE_STATE_PERMISSION_MASK = 236;
    private static final int MSG_USER_SWITCHED = 1;
    private final ArrayList<IBinder> mRemoveList = new ArrayList<>();
    private final ArrayList<Record> mRecords = new ArrayList<>();
    private int mCallState = 0;
    private String mCallIncomingNumber = "";
    private ServiceState mServiceState = new ServiceState();
    private SignalStrength mSignalStrength = new SignalStrength();
    private boolean mMessageWaiting = false;
    private boolean mCallForwarding = false;
    private int mDataActivity = 0;
    private int mDataConnectionState = -1;
    private boolean mDataConnectionPossible = false;
    private String mDataConnectionReason = "";
    private String mDataConnectionApn = "";
    private Bundle mCellLocation = new Bundle();
    private int mOtaspMode = 1;
    private List<CellInfo> mCellInfo = null;
    private final Handler mHandler = new Handler() { // from class: com.android.server.TelephonyRegistry.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    TelephonyRegistry.this.notifyCellLocation(TelephonyRegistry.this.mCellLocation);
                    return;
                default:
                    return;
            }
        }
    };
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() { // from class: com.android.server.TelephonyRegistry.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_USER_SWITCHED.equals(action)) {
                TelephonyRegistry.this.mHandler.sendMessage(TelephonyRegistry.this.mHandler.obtainMessage(1, intent.getIntExtra(Intent.EXTRA_USER_HANDLE, 0), 0));
            }
        }
    };

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.TelephonyRegistry.broadcastServiceStateChanged(android.telephony.ServiceState):void, file: TelephonyRegistry.class
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
    private void broadcastServiceStateChanged(android.telephony.ServiceState r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.TelephonyRegistry.broadcastServiceStateChanged(android.telephony.ServiceState):void, file: TelephonyRegistry.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.TelephonyRegistry.broadcastServiceStateChanged(android.telephony.ServiceState):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.TelephonyRegistry.broadcastSignalStrengthChanged(android.telephony.SignalStrength):void, file: TelephonyRegistry.class
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
    private void broadcastSignalStrengthChanged(android.telephony.SignalStrength r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.TelephonyRegistry.broadcastSignalStrengthChanged(android.telephony.SignalStrength):void, file: TelephonyRegistry.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.TelephonyRegistry.broadcastSignalStrengthChanged(android.telephony.SignalStrength):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.TelephonyRegistry.broadcastCallStateChanged(int, java.lang.String):void, file: TelephonyRegistry.class
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
    private void broadcastCallStateChanged(int r1, java.lang.String r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.TelephonyRegistry.broadcastCallStateChanged(int, java.lang.String):void, file: TelephonyRegistry.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.TelephonyRegistry.broadcastCallStateChanged(int, java.lang.String):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.TelephonyRegistry.validateEventsAndUserLocked(com.android.server.TelephonyRegistry$Record, int):boolean, file: TelephonyRegistry.class
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
    private boolean validateEventsAndUserLocked(com.android.server.TelephonyRegistry.Record r1, int r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.TelephonyRegistry.validateEventsAndUserLocked(com.android.server.TelephonyRegistry$Record, int):boolean, file: TelephonyRegistry.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.TelephonyRegistry.validateEventsAndUserLocked(com.android.server.TelephonyRegistry$Record, int):boolean");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: TelephonyRegistry$Record.class */
    public static class Record {
        String pkgForDebug;
        IBinder binder;
        IPhoneStateListener callback;
        int callerUid;
        int events;

        private Record() {
        }

        public String toString() {
            return "{pkgForDebug=" + this.pkgForDebug + " callerUid=" + this.callerUid + " events=" + Integer.toHexString(this.events) + "}";
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public TelephonyRegistry(Context context) {
        CellLocation location = CellLocation.getEmpty();
        if (location != null) {
            location.fillInNotifierBundle(this.mCellLocation);
        }
        this.mContext = context;
        this.mBatteryStats = BatteryStatsService.getService();
        this.mConnectedApns = new ArrayList<>();
    }

    public void systemRunning() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_USER_SWITCHED);
        filter.addAction(Intent.ACTION_USER_REMOVED);
        this.mContext.registerReceiver(this.mBroadcastReceiver, filter);
    }

    @Override // com.android.internal.telephony.ITelephonyRegistry
    public void listen(String pkgForDebug, IPhoneStateListener callback, int events, boolean notifyNow) {
        Record r;
        int callerUid = UserHandle.getCallingUserId();
        UserHandle.myUserId();
        if (events != 0) {
            checkListenerPermission(events);
            synchronized (this.mRecords) {
                IBinder b = callback.asBinder();
                int N = this.mRecords.size();
                int i = 0;
                while (true) {
                    if (i < N) {
                        r = this.mRecords.get(i);
                        if (b == r.binder) {
                            break;
                        }
                        i++;
                    } else {
                        r = new Record();
                        r.binder = b;
                        r.callback = callback;
                        r.pkgForDebug = pkgForDebug;
                        r.callerUid = callerUid;
                        this.mRecords.add(r);
                        break;
                    }
                }
                int i2 = events & (events ^ r.events);
                r.events = events;
                if (notifyNow) {
                    if ((events & 1) != 0) {
                        try {
                            r.callback.onServiceStateChanged(new ServiceState(this.mServiceState));
                        } catch (RemoteException e) {
                            remove(r.binder);
                        }
                    }
                    if ((events & 2) != 0) {
                        try {
                            int gsmSignalStrength = this.mSignalStrength.getGsmSignalStrength();
                            r.callback.onSignalStrengthChanged(gsmSignalStrength == 99 ? -1 : gsmSignalStrength);
                        } catch (RemoteException e2) {
                            remove(r.binder);
                        }
                    }
                    if ((events & 4) != 0) {
                        try {
                            r.callback.onMessageWaitingIndicatorChanged(this.mMessageWaiting);
                        } catch (RemoteException e3) {
                            remove(r.binder);
                        }
                    }
                    if ((events & 8) != 0) {
                        try {
                            r.callback.onCallForwardingIndicatorChanged(this.mCallForwarding);
                        } catch (RemoteException e4) {
                            remove(r.binder);
                        }
                    }
                    if (validateEventsAndUserLocked(r, 16)) {
                        try {
                            r.callback.onCellLocationChanged(new Bundle(this.mCellLocation));
                        } catch (RemoteException e5) {
                            remove(r.binder);
                        }
                    }
                    if ((events & 32) != 0) {
                        try {
                            r.callback.onCallStateChanged(this.mCallState, this.mCallIncomingNumber);
                        } catch (RemoteException e6) {
                            remove(r.binder);
                        }
                    }
                    if ((events & 64) != 0) {
                        try {
                            r.callback.onDataConnectionStateChanged(this.mDataConnectionState, this.mDataConnectionNetworkType);
                        } catch (RemoteException e7) {
                            remove(r.binder);
                        }
                    }
                    if ((events & 128) != 0) {
                        try {
                            r.callback.onDataActivity(this.mDataActivity);
                        } catch (RemoteException e8) {
                            remove(r.binder);
                        }
                    }
                    if ((events & 256) != 0) {
                        try {
                            r.callback.onSignalStrengthsChanged(this.mSignalStrength);
                        } catch (RemoteException e9) {
                            remove(r.binder);
                        }
                    }
                    if ((events & 512) != 0) {
                        try {
                            r.callback.onOtaspChanged(this.mOtaspMode);
                        } catch (RemoteException e10) {
                            remove(r.binder);
                        }
                    }
                    if (validateEventsAndUserLocked(r, 1024)) {
                        try {
                            r.callback.onCellInfoChanged(this.mCellInfo);
                        } catch (RemoteException e11) {
                            remove(r.binder);
                        }
                    }
                }
            }
            return;
        }
        remove(callback.asBinder());
    }

    private void remove(IBinder binder) {
        synchronized (this.mRecords) {
            int recordCount = this.mRecords.size();
            for (int i = 0; i < recordCount; i++) {
                if (this.mRecords.get(i).binder == binder) {
                    this.mRecords.remove(i);
                    return;
                }
            }
        }
    }

    @Override // com.android.internal.telephony.ITelephonyRegistry
    public void notifyCallState(int state, String incomingNumber) {
        if (!checkNotifyPermission("notifyCallState()")) {
            return;
        }
        synchronized (this.mRecords) {
            this.mCallState = state;
            this.mCallIncomingNumber = incomingNumber;
            Iterator i$ = this.mRecords.iterator();
            while (i$.hasNext()) {
                Record r = i$.next();
                if ((r.events & 32) != 0) {
                    try {
                        r.callback.onCallStateChanged(state, incomingNumber);
                    } catch (RemoteException e) {
                        this.mRemoveList.add(r.binder);
                    }
                }
            }
            handleRemoveListLocked();
        }
        broadcastCallStateChanged(state, incomingNumber);
    }

    @Override // com.android.internal.telephony.ITelephonyRegistry
    public void notifyServiceState(ServiceState state) {
        if (!checkNotifyPermission("notifyServiceState()")) {
            return;
        }
        synchronized (this.mRecords) {
            this.mServiceState = state;
            Iterator i$ = this.mRecords.iterator();
            while (i$.hasNext()) {
                Record r = i$.next();
                if ((r.events & 1) != 0) {
                    try {
                        r.callback.onServiceStateChanged(new ServiceState(state));
                    } catch (RemoteException e) {
                        this.mRemoveList.add(r.binder);
                    }
                }
            }
            handleRemoveListLocked();
        }
        broadcastServiceStateChanged(state);
    }

    @Override // com.android.internal.telephony.ITelephonyRegistry
    public void notifySignalStrength(SignalStrength signalStrength) {
        if (!checkNotifyPermission("notifySignalStrength()")) {
            return;
        }
        synchronized (this.mRecords) {
            this.mSignalStrength = signalStrength;
            Iterator i$ = this.mRecords.iterator();
            while (i$.hasNext()) {
                Record r = i$.next();
                if ((r.events & 256) != 0) {
                    try {
                        r.callback.onSignalStrengthsChanged(new SignalStrength(signalStrength));
                    } catch (RemoteException e) {
                        this.mRemoveList.add(r.binder);
                    }
                }
                if ((r.events & 2) != 0) {
                    try {
                        int gsmSignalStrength = signalStrength.getGsmSignalStrength();
                        r.callback.onSignalStrengthChanged(gsmSignalStrength == 99 ? -1 : gsmSignalStrength);
                    } catch (RemoteException e2) {
                        this.mRemoveList.add(r.binder);
                    }
                }
            }
            handleRemoveListLocked();
        }
        broadcastSignalStrengthChanged(signalStrength);
    }

    @Override // com.android.internal.telephony.ITelephonyRegistry
    public void notifyCellInfo(List<CellInfo> cellInfo) {
        if (!checkNotifyPermission("notifyCellInfo()")) {
            return;
        }
        synchronized (this.mRecords) {
            this.mCellInfo = cellInfo;
            Iterator i$ = this.mRecords.iterator();
            while (i$.hasNext()) {
                Record r = i$.next();
                if (validateEventsAndUserLocked(r, 1024)) {
                    try {
                        r.callback.onCellInfoChanged(cellInfo);
                    } catch (RemoteException e) {
                        this.mRemoveList.add(r.binder);
                    }
                }
            }
            handleRemoveListLocked();
        }
    }

    @Override // com.android.internal.telephony.ITelephonyRegistry
    public void notifyMessageWaitingChanged(boolean mwi) {
        if (!checkNotifyPermission("notifyMessageWaitingChanged()")) {
            return;
        }
        synchronized (this.mRecords) {
            this.mMessageWaiting = mwi;
            Iterator i$ = this.mRecords.iterator();
            while (i$.hasNext()) {
                Record r = i$.next();
                if ((r.events & 4) != 0) {
                    try {
                        r.callback.onMessageWaitingIndicatorChanged(mwi);
                    } catch (RemoteException e) {
                        this.mRemoveList.add(r.binder);
                    }
                }
            }
            handleRemoveListLocked();
        }
    }

    @Override // com.android.internal.telephony.ITelephonyRegistry
    public void notifyCallForwardingChanged(boolean cfi) {
        if (!checkNotifyPermission("notifyCallForwardingChanged()")) {
            return;
        }
        synchronized (this.mRecords) {
            this.mCallForwarding = cfi;
            Iterator i$ = this.mRecords.iterator();
            while (i$.hasNext()) {
                Record r = i$.next();
                if ((r.events & 8) != 0) {
                    try {
                        r.callback.onCallForwardingIndicatorChanged(cfi);
                    } catch (RemoteException e) {
                        this.mRemoveList.add(r.binder);
                    }
                }
            }
            handleRemoveListLocked();
        }
    }

    @Override // com.android.internal.telephony.ITelephonyRegistry
    public void notifyDataActivity(int state) {
        if (!checkNotifyPermission("notifyDataActivity()")) {
            return;
        }
        synchronized (this.mRecords) {
            this.mDataActivity = state;
            Iterator i$ = this.mRecords.iterator();
            while (i$.hasNext()) {
                Record r = i$.next();
                if ((r.events & 128) != 0) {
                    try {
                        r.callback.onDataActivity(state);
                    } catch (RemoteException e) {
                        this.mRemoveList.add(r.binder);
                    }
                }
            }
            handleRemoveListLocked();
        }
    }

    @Override // com.android.internal.telephony.ITelephonyRegistry
    public void notifyDataConnection(int state, boolean isDataConnectivityPossible, String reason, String apn, String apnType, LinkProperties linkProperties, LinkCapabilities linkCapabilities, int networkType, boolean roaming) {
        if (!checkNotifyPermission("notifyDataConnection()")) {
            return;
        }
        synchronized (this.mRecords) {
            boolean modified = false;
            if (state == 2) {
                if (!this.mConnectedApns.contains(apnType)) {
                    this.mConnectedApns.add(apnType);
                    if (this.mDataConnectionState != state) {
                        this.mDataConnectionState = state;
                        modified = true;
                    }
                }
            } else if (this.mConnectedApns.remove(apnType) && this.mConnectedApns.isEmpty()) {
                this.mDataConnectionState = state;
                modified = true;
            }
            this.mDataConnectionPossible = isDataConnectivityPossible;
            this.mDataConnectionReason = reason;
            this.mDataConnectionLinkProperties = linkProperties;
            this.mDataConnectionLinkCapabilities = linkCapabilities;
            if (this.mDataConnectionNetworkType != networkType) {
                this.mDataConnectionNetworkType = networkType;
                modified = true;
            }
            if (modified) {
                Iterator i$ = this.mRecords.iterator();
                while (i$.hasNext()) {
                    Record r = i$.next();
                    if ((r.events & 64) != 0) {
                        try {
                            r.callback.onDataConnectionStateChanged(this.mDataConnectionState, this.mDataConnectionNetworkType);
                        } catch (RemoteException e) {
                            this.mRemoveList.add(r.binder);
                        }
                    }
                }
                handleRemoveListLocked();
            }
        }
        broadcastDataConnectionStateChanged(state, isDataConnectivityPossible, reason, apn, apnType, linkProperties, linkCapabilities, roaming);
    }

    @Override // com.android.internal.telephony.ITelephonyRegistry
    public void notifyDataConnectionFailed(String reason, String apnType) {
        if (!checkNotifyPermission("notifyDataConnectionFailed()")) {
            return;
        }
        broadcastDataConnectionFailed(reason, apnType);
    }

    @Override // com.android.internal.telephony.ITelephonyRegistry
    public void notifyCellLocation(Bundle cellLocation) {
        if (!checkNotifyPermission("notifyCellLocation()")) {
            return;
        }
        synchronized (this.mRecords) {
            this.mCellLocation = cellLocation;
            Iterator i$ = this.mRecords.iterator();
            while (i$.hasNext()) {
                Record r = i$.next();
                if (validateEventsAndUserLocked(r, 16)) {
                    try {
                        r.callback.onCellLocationChanged(new Bundle(cellLocation));
                    } catch (RemoteException e) {
                        this.mRemoveList.add(r.binder);
                    }
                }
            }
            handleRemoveListLocked();
        }
    }

    @Override // com.android.internal.telephony.ITelephonyRegistry
    public void notifyOtaspChanged(int otaspMode) {
        if (!checkNotifyPermission("notifyOtaspChanged()")) {
            return;
        }
        synchronized (this.mRecords) {
            this.mOtaspMode = otaspMode;
            Iterator i$ = this.mRecords.iterator();
            while (i$.hasNext()) {
                Record r = i$.next();
                if ((r.events & 512) != 0) {
                    try {
                        r.callback.onOtaspChanged(otaspMode);
                    } catch (RemoteException e) {
                        this.mRemoveList.add(r.binder);
                    }
                }
            }
            handleRemoveListLocked();
        }
    }

    @Override // android.os.Binder
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.DUMP) != 0) {
            pw.println("Permission Denial: can't dump telephony.registry from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
            return;
        }
        synchronized (this.mRecords) {
            int recordCount = this.mRecords.size();
            pw.println("last known state:");
            pw.println("  mCallState=" + this.mCallState);
            pw.println("  mCallIncomingNumber=" + this.mCallIncomingNumber);
            pw.println("  mServiceState=" + this.mServiceState);
            pw.println("  mSignalStrength=" + this.mSignalStrength);
            pw.println("  mMessageWaiting=" + this.mMessageWaiting);
            pw.println("  mCallForwarding=" + this.mCallForwarding);
            pw.println("  mDataActivity=" + this.mDataActivity);
            pw.println("  mDataConnectionState=" + this.mDataConnectionState);
            pw.println("  mDataConnectionPossible=" + this.mDataConnectionPossible);
            pw.println("  mDataConnectionReason=" + this.mDataConnectionReason);
            pw.println("  mDataConnectionApn=" + this.mDataConnectionApn);
            pw.println("  mDataConnectionLinkProperties=" + this.mDataConnectionLinkProperties);
            pw.println("  mDataConnectionLinkCapabilities=" + this.mDataConnectionLinkCapabilities);
            pw.println("  mCellLocation=" + this.mCellLocation);
            pw.println("  mCellInfo=" + this.mCellInfo);
            pw.println("registrations: count=" + recordCount);
            Iterator i$ = this.mRecords.iterator();
            while (i$.hasNext()) {
                Record r = i$.next();
                pw.println("  " + r.pkgForDebug + " 0x" + Integer.toHexString(r.events));
            }
        }
    }

    private void broadcastDataConnectionStateChanged(int state, boolean isDataConnectivityPossible, String reason, String apn, String apnType, LinkProperties linkProperties, LinkCapabilities linkCapabilities, boolean roaming) {
        Intent intent = new Intent(TelephonyIntents.ACTION_ANY_DATA_CONNECTION_STATE_CHANGED);
        intent.putExtra("state", DefaultPhoneNotifier.convertDataState(state).toString());
        if (!isDataConnectivityPossible) {
            intent.putExtra(PhoneConstants.NETWORK_UNAVAILABLE_KEY, true);
        }
        if (reason != null) {
            intent.putExtra("reason", reason);
        }
        if (linkProperties != null) {
            intent.putExtra("linkProperties", linkProperties);
            String iface = linkProperties.getInterfaceName();
            if (iface != null) {
                intent.putExtra(PhoneConstants.DATA_IFACE_NAME_KEY, iface);
            }
        }
        if (linkCapabilities != null) {
            intent.putExtra("linkCapabilities", linkCapabilities);
        }
        if (roaming) {
            intent.putExtra(PhoneConstants.DATA_NETWORK_ROAMING_KEY, true);
        }
        intent.putExtra("apn", apn);
        intent.putExtra("apnType", apnType);
        this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
    }

    private void broadcastDataConnectionFailed(String reason, String apnType) {
        Intent intent = new Intent(TelephonyIntents.ACTION_DATA_CONNECTION_FAILED);
        intent.putExtra("reason", reason);
        intent.putExtra("apnType", apnType);
        this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
    }

    private boolean checkNotifyPermission(String method) {
        if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.MODIFY_PHONE_STATE) == 0) {
            return true;
        }
        String str = "Modify Phone State Permission Denial: " + method + " from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid();
        return false;
    }

    private void checkListenerPermission(int events) {
        if ((events & 16) != 0) {
            this.mContext.enforceCallingOrSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION, null);
        }
        if ((events & 1024) != 0) {
            this.mContext.enforceCallingOrSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION, null);
        }
        if ((events & 236) != 0) {
            this.mContext.enforceCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE, null);
        }
    }

    private void handleRemoveListLocked() {
        if (this.mRemoveList.size() > 0) {
            Iterator i$ = this.mRemoveList.iterator();
            while (i$.hasNext()) {
                IBinder b = i$.next();
                remove(b);
            }
            this.mRemoveList.clear();
        }
    }
}