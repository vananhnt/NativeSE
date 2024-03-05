package com.android.server;

import android.Manifest;
import android.app.AppOpsManager;
import android.app.backup.FullBackup;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.ObbInfo;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.storage.IMountService;
import android.os.storage.IMountServiceListener;
import android.os.storage.IMountShutdownObserver;
import android.os.storage.IObbActionListener;
import android.os.storage.StorageVolume;
import android.provider.Telephony;
import android.text.TextUtils;
import android.util.Slog;
import android.util.TimedRemoteCaller;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.app.IMediaContainerService;
import com.android.internal.util.Preconditions;
import com.android.server.NativeDaemonConnector;
import com.android.server.Watchdog;
import com.android.server.am.ActivityManagerService;
import com.android.server.pm.PackageManagerService;
import com.google.android.collect.Lists;
import com.google.android.collect.Maps;
import gov.nist.core.Separators;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: MountService.class */
public class MountService extends IMountService.Stub implements INativeDaemonConnectorCallbacks, Watchdog.Monitor {
    private static final boolean LOCAL_LOGD = false;
    private static final boolean DEBUG_UNMOUNT = false;
    private static final boolean DEBUG_EVENTS = false;
    private static final boolean DEBUG_OBB = false;
    private static final boolean WATCHDOG_ENABLE = false;
    private static final String TAG = "MountService";
    private static final String VOLD_TAG = "VoldConnector";
    private static final int MAX_CONTAINERS = 250;
    private Context mContext;
    private NativeDaemonConnector mConnector;
    private StorageVolume mEmulatedTemplate;
    private PackageManagerService mPms;
    private boolean mUmsEnabling;
    private static final int CRYPTO_ALGORITHM_KEY_SIZE = 128;
    private static final int PBKDF2_HASH_ROUNDS = 1024;
    private final ObbActionHandler mObbActionHandler;
    private static final int OBB_RUN_ACTION = 1;
    private static final int OBB_MCS_BOUND = 2;
    private static final int OBB_MCS_UNBIND = 3;
    private static final int OBB_MCS_RECONNECT = 4;
    private static final int OBB_FLUSH_MOUNT_STATE = 5;
    static final ComponentName DEFAULT_CONTAINER_COMPONENT = new ComponentName("com.android.defcontainer", "com.android.defcontainer.DefaultContainerService");
    private static final int H_UNMOUNT_PM_UPDATE = 1;
    private static final int H_UNMOUNT_PM_DONE = 2;
    private static final int H_UNMOUNT_MS = 3;
    private static final int H_SYSTEM_READY = 4;
    private static final int RETRY_UNMOUNT_DELAY = 30;
    private static final int MAX_UNMOUNT_RETRIES = 4;
    private final Handler mHandler;
    private static final String TAG_STORAGE_LIST = "StorageList";
    private static final String TAG_STORAGE = "storage";
    private final Object mVolumesLock = new Object();
    @GuardedBy("mVolumesLock")
    private final ArrayList<StorageVolume> mVolumes = Lists.newArrayList();
    @GuardedBy("mVolumesLock")
    private final HashMap<String, StorageVolume> mVolumesByPath = Maps.newHashMap();
    @GuardedBy("mVolumesLock")
    private final HashMap<String, String> mVolumeStates = Maps.newHashMap();
    private volatile boolean mSystemReady = false;
    private boolean mUmsAvailable = false;
    private final ArrayList<MountServiceBinderListener> mListeners = new ArrayList<>();
    private final CountDownLatch mConnectedSignal = new CountDownLatch(1);
    private final CountDownLatch mAsecsScanned = new CountDownLatch(1);
    private boolean mSendUmsConnectedOnBoot = false;
    private final HashSet<String> mAsecMountSet = new HashSet<>();
    private final Map<IBinder, List<ObbState>> mObbMounts = new HashMap();
    private final Map<String, ObbState> mObbPathToStateMap = new HashMap();
    private final DefaultContainerConnection mDefContainerConn = new DefaultContainerConnection();
    private IMediaContainerService mContainerService = null;
    private final BroadcastReceiver mUserReceiver = new BroadcastReceiver() { // from class: com.android.server.MountService.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            int userId = intent.getIntExtra(Intent.EXTRA_USER_HANDLE, -1);
            if (userId == -1) {
                return;
            }
            UserHandle user = new UserHandle(userId);
            String action = intent.getAction();
            if (Intent.ACTION_USER_ADDED.equals(action)) {
                synchronized (MountService.this.mVolumesLock) {
                    MountService.this.createEmulatedVolumeForUserLocked(user);
                }
            } else if (Intent.ACTION_USER_REMOVED.equals(action)) {
                synchronized (MountService.this.mVolumesLock) {
                    List<StorageVolume> toRemove = Lists.newArrayList();
                    Iterator i$ = MountService.this.mVolumes.iterator();
                    while (i$.hasNext()) {
                        StorageVolume volume = (StorageVolume) i$.next();
                        if (user.equals(volume.getOwner())) {
                            toRemove.add(volume);
                        }
                    }
                    for (StorageVolume volume2 : toRemove) {
                        MountService.this.removeVolumeLocked(volume2);
                    }
                }
            }
        }
    };
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() { // from class: com.android.server.MountService.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            boolean available = intent.getBooleanExtra("connected", false) && intent.getBooleanExtra(UsbManager.USB_FUNCTION_MASS_STORAGE, false);
            MountService.this.notifyShareAvailabilityChange(available);
        }
    };
    private final BroadcastReceiver mIdleMaintenanceReceiver = new BroadcastReceiver() { // from class: com.android.server.MountService.3
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            MountService.this.waitForReady();
            String action = intent.getAction();
            if (Intent.ACTION_IDLE_MAINTENANCE_START.equals(action)) {
                try {
                    MountService.this.mConnector.execute("fstrim", "dotrim");
                    EventLogTags.writeFstrimStart(SystemClock.elapsedRealtime());
                } catch (NativeDaemonConnectorException e) {
                    Slog.e(MountService.TAG, "Failed to run fstrim!");
                }
            }
        }
    };

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.MountService.readStorageListLocked():void, file: MountService.class
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
    private void readStorageListLocked() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.MountService.readStorageListLocked():void, file: MountService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.MountService.readStorageListLocked():void");
    }

    /* loaded from: MountService$VolumeState.class */
    class VolumeState {
        public static final int Init = -1;
        public static final int NoMedia = 0;
        public static final int Idle = 1;
        public static final int Pending = 2;
        public static final int Checking = 3;
        public static final int Mounted = 4;
        public static final int Unmounting = 5;
        public static final int Formatting = 6;
        public static final int Shared = 7;
        public static final int SharedMnt = 8;

        VolumeState() {
        }
    }

    /* loaded from: MountService$VoldResponseCode.class */
    class VoldResponseCode {
        public static final int VolumeListResult = 110;
        public static final int AsecListResult = 111;
        public static final int StorageUsersListResult = 112;
        public static final int ShareStatusResult = 210;
        public static final int AsecPathResult = 211;
        public static final int ShareEnabledResult = 212;
        public static final int OpFailedNoMedia = 401;
        public static final int OpFailedMediaBlank = 402;
        public static final int OpFailedMediaCorrupt = 403;
        public static final int OpFailedVolNotMounted = 404;
        public static final int OpFailedStorageBusy = 405;
        public static final int OpFailedStorageNotFound = 406;
        public static final int VolumeStateChange = 605;
        public static final int VolumeDiskInserted = 630;
        public static final int VolumeDiskRemoved = 631;
        public static final int VolumeBadRemoval = 632;
        public static final int FstrimCompleted = 700;

        VoldResponseCode() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: MountService$ObbState.class */
    public class ObbState implements IBinder.DeathRecipient {
        final String rawPath;
        final String canonicalPath;
        final String ownerPath;
        final String voldPath;
        final int ownerGid;
        final IObbActionListener token;
        final int nonce;

        public ObbState(String rawPath, String canonicalPath, int callingUid, IObbActionListener token, int nonce) {
            this.rawPath = rawPath;
            this.canonicalPath = canonicalPath.toString();
            int userId = UserHandle.getUserId(callingUid);
            this.ownerPath = MountService.buildObbPath(canonicalPath, userId, false);
            this.voldPath = MountService.buildObbPath(canonicalPath, userId, true);
            this.ownerGid = UserHandle.getSharedAppGid(callingUid);
            this.token = token;
            this.nonce = nonce;
        }

        public IBinder getBinder() {
            return this.token.asBinder();
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            ObbAction action = new UnmountObbAction(this, true);
            MountService.this.mObbActionHandler.sendMessage(MountService.this.mObbActionHandler.obtainMessage(1, action));
        }

        public void link() throws RemoteException {
            getBinder().linkToDeath(this, 0);
        }

        public void unlink() {
            getBinder().unlinkToDeath(this, 0);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("ObbState{");
            sb.append("rawPath=").append(this.rawPath);
            sb.append(",canonicalPath=").append(this.canonicalPath);
            sb.append(",ownerPath=").append(this.ownerPath);
            sb.append(",voldPath=").append(this.voldPath);
            sb.append(",ownerGid=").append(this.ownerGid);
            sb.append(",token=").append(this.token);
            sb.append(",binder=").append(getBinder());
            sb.append('}');
            return sb.toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: MountService$DefaultContainerConnection.class */
    public class DefaultContainerConnection implements ServiceConnection {
        DefaultContainerConnection() {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName name, IBinder service) {
            IMediaContainerService imcs = IMediaContainerService.Stub.asInterface(service);
            MountService.this.mObbActionHandler.sendMessage(MountService.this.mObbActionHandler.obtainMessage(2, imcs));
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName name) {
        }
    }

    /* loaded from: MountService$UnmountCallBack.class */
    class UnmountCallBack {
        final String path;
        final boolean force;
        final boolean removeEncryption;
        int retries = 0;

        UnmountCallBack(String path, boolean force, boolean removeEncryption) {
            this.path = path;
            this.force = force;
            this.removeEncryption = removeEncryption;
        }

        void handleFinished() {
            MountService.this.doUnmountVolume(this.path, true, this.removeEncryption);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: MountService$UmsEnableCallBack.class */
    public class UmsEnableCallBack extends UnmountCallBack {
        final String method;

        UmsEnableCallBack(String path, String method, boolean force) {
            super(path, force, false);
            this.method = method;
        }

        @Override // com.android.server.MountService.UnmountCallBack
        void handleFinished() {
            super.handleFinished();
            MountService.this.doShareUnshareVolume(this.path, this.method, true);
        }
    }

    /* loaded from: MountService$ShutdownCallBack.class */
    class ShutdownCallBack extends UnmountCallBack {
        IMountShutdownObserver observer;

        ShutdownCallBack(String path, IMountShutdownObserver observer) {
            super(path, true, false);
            this.observer = observer;
        }

        @Override // com.android.server.MountService.UnmountCallBack
        void handleFinished() {
            int ret = MountService.this.doUnmountVolume(this.path, true, this.removeEncryption);
            if (this.observer != null) {
                try {
                    this.observer.onShutDownComplete(ret);
                } catch (RemoteException e) {
                    Slog.w(MountService.TAG, "RemoteException when shutting down");
                }
            }
        }
    }

    /* loaded from: MountService$MountServiceHandler.class */
    class MountServiceHandler extends Handler {
        ArrayList<UnmountCallBack> mForceUnmounts;
        boolean mUpdatingStatus;

        MountServiceHandler(Looper l) {
            super(l);
            this.mForceUnmounts = new ArrayList<>();
            this.mUpdatingStatus = false;
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    this.mForceUnmounts.add((UnmountCallBack) msg.obj);
                    if (!this.mUpdatingStatus) {
                        this.mUpdatingStatus = true;
                        MountService.this.mPms.updateExternalMediaStatus(false, true);
                        return;
                    }
                    return;
                case 2:
                    this.mUpdatingStatus = false;
                    int size = this.mForceUnmounts.size();
                    int[] sizeArr = new int[size];
                    int sizeArrN = 0;
                    ActivityManagerService ams = ServiceManager.getService(Context.ACTIVITY_SERVICE);
                    for (int i = 0; i < size; i++) {
                        UnmountCallBack ucb = this.mForceUnmounts.get(i);
                        String path = ucb.path;
                        boolean done = false;
                        if (!ucb.force) {
                            done = true;
                        } else {
                            int[] pids = MountService.this.getStorageUsers(path);
                            if (pids == null || pids.length == 0) {
                                done = true;
                            } else {
                                ams.killPids(pids, "unmount media", true);
                                int[] pids2 = MountService.this.getStorageUsers(path);
                                if (pids2 == null || pids2.length == 0) {
                                    done = true;
                                }
                            }
                        }
                        if (!done && ucb.retries < 4) {
                            Slog.i(MountService.TAG, "Retrying to kill storage users again");
                            Handler handler = MountService.this.mHandler;
                            Handler handler2 = MountService.this.mHandler;
                            int i2 = ucb.retries;
                            ucb.retries = i2 + 1;
                            handler.sendMessageDelayed(handler2.obtainMessage(2, Integer.valueOf(i2)), 30L);
                        } else {
                            if (ucb.retries >= 4) {
                                Slog.i(MountService.TAG, "Failed to unmount media inspite of 4 retries. Forcibly killing processes now");
                            }
                            int i3 = sizeArrN;
                            sizeArrN++;
                            sizeArr[i3] = i;
                            MountService.this.mHandler.sendMessage(MountService.this.mHandler.obtainMessage(3, ucb));
                        }
                    }
                    for (int i4 = sizeArrN - 1; i4 >= 0; i4--) {
                        this.mForceUnmounts.remove(sizeArr[i4]);
                    }
                    return;
                case 3:
                    ((UnmountCallBack) msg.obj).handleFinished();
                    return;
                case 4:
                    try {
                        MountService.this.handleSystemReady();
                        return;
                    } catch (Exception ex) {
                        Slog.e(MountService.TAG, "Boot-time mount exception", ex);
                        return;
                    }
                default:
                    return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void waitForAsecScan() {
        waitForLatch(this.mAsecsScanned);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void waitForReady() {
        waitForLatch(this.mConnectedSignal);
    }

    private void waitForLatch(CountDownLatch latch) {
        while (!latch.await(TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
            try {
                Slog.w(TAG, "Thread " + Thread.currentThread().getName() + " still waiting for MountService ready...");
            } catch (InterruptedException e) {
                Slog.w(TAG, "Interrupt while waiting for MountService to be ready.");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleSystemReady() {
        HashMap<String, String> snapshot;
        synchronized (this.mVolumesLock) {
            snapshot = new HashMap<>(this.mVolumeStates);
        }
        for (Map.Entry<String, String> entry : snapshot.entrySet()) {
            String path = entry.getKey();
            String state = entry.getValue();
            if (state.equals(Environment.MEDIA_UNMOUNTED)) {
                int rc = doMountVolume(path);
                if (rc != 0) {
                    Slog.e(TAG, String.format("Boot-time mount failed (%d)", Integer.valueOf(rc)));
                }
            } else if (state.equals("shared")) {
                notifyVolumeStateChange(null, path, 0, 7);
            }
        }
        synchronized (this.mVolumesLock) {
            Iterator i$ = this.mVolumes.iterator();
            while (i$.hasNext()) {
                StorageVolume volume = i$.next();
                if (volume.isEmulated()) {
                    updatePublicVolumeState(volume, Environment.MEDIA_MOUNTED);
                }
            }
        }
        if (this.mSendUmsConnectedOnBoot) {
            sendUmsIntent(true);
            this.mSendUmsConnectedOnBoot = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: MountService$MountServiceBinderListener.class */
    public final class MountServiceBinderListener implements IBinder.DeathRecipient {
        final IMountServiceListener mListener;

        MountServiceBinderListener(IMountServiceListener listener) {
            this.mListener = listener;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            synchronized (MountService.this.mListeners) {
                MountService.this.mListeners.remove(this);
                this.mListener.asBinder().unlinkToDeath(this, 0);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doShareUnshareVolume(String path, String method, boolean enable) {
        if (!method.equals("ums")) {
            throw new IllegalArgumentException(String.format("Method %s not supported", method));
        }
        try {
            NativeDaemonConnector nativeDaemonConnector = this.mConnector;
            Object[] objArr = new Object[3];
            objArr[0] = enable ? "share" : "unshare";
            objArr[1] = path;
            objArr[2] = method;
            nativeDaemonConnector.execute("volume", objArr);
        } catch (NativeDaemonConnectorException e) {
            Slog.e(TAG, "Failed to share/unshare", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePublicVolumeState(StorageVolume volume, String state) {
        String oldState;
        String path = volume.getPath();
        synchronized (this.mVolumesLock) {
            oldState = this.mVolumeStates.put(path, state);
        }
        if (state.equals(oldState)) {
            Slog.w(TAG, String.format("Duplicate state transition (%s -> %s) for %s", state, state, path));
            return;
        }
        Slog.d(TAG, "volume state changed for " + path + " (" + oldState + " -> " + state + Separators.RPAREN);
        if (volume.isPrimary() && !volume.isEmulated()) {
            if (Environment.MEDIA_UNMOUNTED.equals(state)) {
                this.mPms.updateExternalMediaStatus(false, false);
                this.mObbActionHandler.sendMessage(this.mObbActionHandler.obtainMessage(5, path));
            } else if (Environment.MEDIA_MOUNTED.equals(state)) {
                this.mPms.updateExternalMediaStatus(true, false);
            }
        }
        synchronized (this.mListeners) {
            for (int i = this.mListeners.size() - 1; i >= 0; i--) {
                MountServiceBinderListener bl = this.mListeners.get(i);
                try {
                    bl.mListener.onStorageStateChanged(path, oldState, state);
                } catch (RemoteException e) {
                    Slog.e(TAG, "Listener dead");
                    this.mListeners.remove(i);
                } catch (Exception ex) {
                    Slog.e(TAG, "Listener failed", ex);
                }
            }
        }
    }

    @Override // com.android.server.INativeDaemonConnectorCallbacks
    public void onDaemonConnected() {
        new Thread("MountService#onDaemonConnected") { // from class: com.android.server.MountService.4
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                StorageVolume volume;
                String state;
                try {
                    String[] vols = NativeDaemonEvent.filterMessageList(MountService.this.mConnector.executeForList("volume", "list"), 110);
                    for (String volstr : vols) {
                        String[] tok = volstr.split(Separators.SP);
                        String path = tok[1];
                        synchronized (MountService.this.mVolumesLock) {
                            volume = (StorageVolume) MountService.this.mVolumesByPath.get(path);
                        }
                        int st = Integer.parseInt(tok[2]);
                        if (st == 0) {
                            state = Environment.MEDIA_REMOVED;
                        } else if (st == 1) {
                            state = Environment.MEDIA_UNMOUNTED;
                        } else if (st == 4) {
                            state = Environment.MEDIA_MOUNTED;
                            Slog.i(MountService.TAG, "Media already mounted on daemon connection");
                        } else if (st == 7) {
                            state = "shared";
                            Slog.i(MountService.TAG, "Media shared on daemon connection");
                        } else {
                            throw new Exception(String.format("Unexpected state %d", Integer.valueOf(st)));
                        }
                        if (state != null) {
                            MountService.this.updatePublicVolumeState(volume, state);
                        }
                    }
                } catch (Exception e) {
                    Slog.e(MountService.TAG, "Error processing initial volume state", e);
                    StorageVolume primary = MountService.this.getPrimaryPhysicalVolume();
                    if (primary != null) {
                        MountService.this.updatePublicVolumeState(primary, Environment.MEDIA_REMOVED);
                    }
                }
                MountService.this.mConnectedSignal.countDown();
                MountService.this.mPms.scanAvailableAsecs();
                MountService.this.mAsecsScanned.countDown();
            }
        }.start();
    }

    @Override // com.android.server.INativeDaemonConnectorCallbacks
    public boolean onEvent(int code, String raw, String[] cooked) {
        StorageVolume volume;
        if (code == 605) {
            notifyVolumeStateChange(cooked[2], cooked[3], Integer.parseInt(cooked[7]), Integer.parseInt(cooked[10]));
            return true;
        } else if (code == 630 || code == 631 || code == 632) {
            String action = null;
            String str = cooked[2];
            final String path = cooked[3];
            try {
                String devComp = cooked[6].substring(1, cooked[6].length() - 1);
                String[] devTok = devComp.split(Separators.COLON);
                Integer.parseInt(devTok[0]);
                Integer.parseInt(devTok[1]);
            } catch (Exception ex) {
                Slog.e(TAG, "Failed to parse major/minor", ex);
            }
            synchronized (this.mVolumesLock) {
                volume = this.mVolumesByPath.get(path);
                this.mVolumeStates.get(path);
            }
            if (code == 630) {
                new Thread("MountService#VolumeDiskInserted") { // from class: com.android.server.MountService.5
                    @Override // java.lang.Thread, java.lang.Runnable
                    public void run() {
                        try {
                            int rc = MountService.this.doMountVolume(path);
                            if (rc != 0) {
                                Slog.w(MountService.TAG, String.format("Insertion mount failed (%d)", Integer.valueOf(rc)));
                            }
                        } catch (Exception ex2) {
                            Slog.w(MountService.TAG, "Failed to mount media on insertion", ex2);
                        }
                    }
                }.start();
            } else if (code == 631) {
                if (getVolumeState(path).equals(Environment.MEDIA_BAD_REMOVAL)) {
                    return true;
                }
                updatePublicVolumeState(volume, Environment.MEDIA_UNMOUNTED);
                sendStorageIntent(Environment.MEDIA_UNMOUNTED, volume, UserHandle.ALL);
                updatePublicVolumeState(volume, Environment.MEDIA_REMOVED);
                action = Intent.ACTION_MEDIA_REMOVED;
            } else if (code == 632) {
                updatePublicVolumeState(volume, Environment.MEDIA_UNMOUNTED);
                sendStorageIntent(Intent.ACTION_MEDIA_UNMOUNTED, volume, UserHandle.ALL);
                updatePublicVolumeState(volume, Environment.MEDIA_BAD_REMOVAL);
                action = Intent.ACTION_MEDIA_BAD_REMOVAL;
            } else if (code == 700) {
                EventLogTags.writeFstrimFinish(SystemClock.elapsedRealtime());
            } else {
                Slog.e(TAG, String.format("Unknown code {%d}", Integer.valueOf(code)));
            }
            if (action != null) {
                sendStorageIntent(action, volume, UserHandle.ALL);
                return true;
            }
            return true;
        } else {
            return false;
        }
    }

    private void notifyVolumeStateChange(String label, String path, int oldState, int newState) {
        StorageVolume volume;
        String state;
        synchronized (this.mVolumesLock) {
            volume = this.mVolumesByPath.get(path);
            state = getVolumeState(path);
        }
        String action = null;
        if (oldState == 7 && newState != oldState) {
            sendStorageIntent(Intent.ACTION_MEDIA_UNSHARED, volume, UserHandle.ALL);
        }
        if (newState != -1 && newState != 0) {
            if (newState == 1) {
                if (!state.equals(Environment.MEDIA_BAD_REMOVAL) && !state.equals(Environment.MEDIA_NOFS) && !state.equals(Environment.MEDIA_UNMOUNTABLE) && !getUmsEnabling()) {
                    updatePublicVolumeState(volume, Environment.MEDIA_UNMOUNTED);
                    action = Intent.ACTION_MEDIA_UNMOUNTED;
                }
            } else if (newState != 2) {
                if (newState == 3) {
                    updatePublicVolumeState(volume, Environment.MEDIA_CHECKING);
                    action = Intent.ACTION_MEDIA_CHECKING;
                } else if (newState == 4) {
                    updatePublicVolumeState(volume, Environment.MEDIA_MOUNTED);
                    action = Intent.ACTION_MEDIA_MOUNTED;
                } else if (newState == 5) {
                    action = Intent.ACTION_MEDIA_EJECT;
                } else if (newState != 6) {
                    if (newState == 7) {
                        updatePublicVolumeState(volume, Environment.MEDIA_UNMOUNTED);
                        sendStorageIntent(Intent.ACTION_MEDIA_UNMOUNTED, volume, UserHandle.ALL);
                        updatePublicVolumeState(volume, "shared");
                        action = Intent.ACTION_MEDIA_SHARED;
                    } else if (newState == 8) {
                        Slog.e(TAG, "Live shared mounts not supported yet!");
                        return;
                    } else {
                        Slog.e(TAG, "Unhandled VolumeState {" + newState + "}");
                    }
                }
            }
        }
        if (action != null) {
            sendStorageIntent(action, volume, UserHandle.ALL);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int doMountVolume(String path) {
        StorageVolume volume;
        int rc = 0;
        synchronized (this.mVolumesLock) {
            volume = this.mVolumesByPath.get(path);
        }
        try {
            this.mConnector.execute("volume", "mount", path);
        } catch (NativeDaemonConnectorException e) {
            String action = null;
            int code = e.getCode();
            if (code == 401) {
                rc = -2;
            } else if (code == 402) {
                updatePublicVolumeState(volume, Environment.MEDIA_NOFS);
                action = Intent.ACTION_MEDIA_NOFS;
                rc = -3;
            } else if (code == 403) {
                updatePublicVolumeState(volume, Environment.MEDIA_UNMOUNTABLE);
                action = Intent.ACTION_MEDIA_UNMOUNTABLE;
                rc = -4;
            } else {
                rc = -1;
            }
            if (action != null) {
                sendStorageIntent(action, volume, UserHandle.ALL);
            }
        }
        return rc;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int doUnmountVolume(String path, boolean force, boolean removeEncryption) {
        if (!getVolumeState(path).equals(Environment.MEDIA_MOUNTED)) {
            return 404;
        }
        Runtime.getRuntime().gc();
        this.mPms.updateExternalMediaStatus(false, false);
        try {
            NativeDaemonConnector.Command cmd = new NativeDaemonConnector.Command("volume", "unmount", path);
            if (removeEncryption) {
                cmd.appendArg("force_and_revert");
            } else if (force) {
                cmd.appendArg("force");
            }
            this.mConnector.execute(cmd);
            synchronized (this.mAsecMountSet) {
                this.mAsecMountSet.clear();
            }
            return 0;
        } catch (NativeDaemonConnectorException e) {
            int code = e.getCode();
            if (code == 404) {
                return -5;
            }
            if (code == 405) {
                return -7;
            }
            return -1;
        }
    }

    private int doFormatVolume(String path) {
        try {
            this.mConnector.execute("volume", "format", path);
            return 0;
        } catch (NativeDaemonConnectorException e) {
            int code = e.getCode();
            if (code == 401) {
                return -2;
            }
            if (code == 403) {
                return -4;
            }
            return -1;
        }
    }

    private boolean doGetVolumeShared(String path, String method) {
        try {
            NativeDaemonEvent event = this.mConnector.execute("volume", "shared", path, method);
            if (event.getCode() == 212) {
                return event.getMessage().endsWith("enabled");
            }
            return false;
        } catch (NativeDaemonConnectorException e) {
            Slog.e(TAG, "Failed to read response to volume shared " + path + Separators.SP + method);
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyShareAvailabilityChange(boolean avail) {
        synchronized (this.mListeners) {
            this.mUmsAvailable = avail;
            for (int i = this.mListeners.size() - 1; i >= 0; i--) {
                MountServiceBinderListener bl = this.mListeners.get(i);
                try {
                    try {
                        bl.mListener.onUsbMassStorageConnectionChanged(avail);
                    } catch (RemoteException e) {
                        Slog.e(TAG, "Listener dead");
                        this.mListeners.remove(i);
                    }
                } catch (Exception ex) {
                    Slog.e(TAG, "Listener failed", ex);
                }
            }
        }
        if (this.mSystemReady) {
            sendUmsIntent(avail);
        } else {
            this.mSendUmsConnectedOnBoot = avail;
        }
        StorageVolume primary = getPrimaryPhysicalVolume();
        if (!avail && primary != null && "shared".equals(getVolumeState(primary.getPath()))) {
            final String path = primary.getPath();
            new Thread("MountService#AvailabilityChange") { // from class: com.android.server.MountService.6
                @Override // java.lang.Thread, java.lang.Runnable
                public void run() {
                    try {
                        Slog.w(MountService.TAG, "Disabling UMS after cable disconnect");
                        MountService.this.doShareUnshareVolume(path, "ums", false);
                        int rc = MountService.this.doMountVolume(path);
                        if (rc != 0) {
                            Slog.e(MountService.TAG, String.format("Failed to remount {%s} on UMS enabled-disconnect (%d)", path, Integer.valueOf(rc)));
                        }
                    } catch (Exception ex2) {
                        Slog.w(MountService.TAG, "Failed to mount media on UMS enabled-disconnect", ex2);
                    }
                }
            }.start();
        }
    }

    private void sendStorageIntent(String action, StorageVolume volume, UserHandle user) {
        Intent intent = new Intent(action, Uri.parse("file://" + volume.getPath()));
        intent.putExtra(StorageVolume.EXTRA_STORAGE_VOLUME, volume);
        Slog.d(TAG, "sendStorageIntent " + intent + " to " + user);
        this.mContext.sendBroadcastAsUser(intent, user);
    }

    private void sendUmsIntent(boolean c) {
        this.mContext.sendBroadcastAsUser(new Intent(c ? Intent.ACTION_UMS_CONNECTED : Intent.ACTION_UMS_DISCONNECTED), UserHandle.ALL);
    }

    private void validatePermission(String perm) {
        if (this.mContext.checkCallingOrSelfPermission(perm) != 0) {
            throw new SecurityException(String.format("Requires %s permission", perm));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createEmulatedVolumeForUserLocked(UserHandle user) {
        if (this.mEmulatedTemplate == null) {
            throw new IllegalStateException("Missing emulated volume multi-user template");
        }
        Environment.UserEnvironment userEnv = new Environment.UserEnvironment(user.getIdentifier());
        File path = userEnv.getExternalStorageDirectory();
        StorageVolume volume = StorageVolume.fromTemplate(this.mEmulatedTemplate, path, user);
        volume.setStorageId(0);
        addVolumeLocked(volume);
        if (this.mSystemReady) {
            updatePublicVolumeState(volume, Environment.MEDIA_MOUNTED);
        } else {
            this.mVolumeStates.put(volume.getPath(), Environment.MEDIA_MOUNTED);
        }
    }

    private void addVolumeLocked(StorageVolume volume) {
        Slog.d(TAG, "addVolumeLocked() " + volume);
        this.mVolumes.add(volume);
        StorageVolume existing = this.mVolumesByPath.put(volume.getPath(), volume);
        if (existing != null) {
            throw new IllegalStateException("Volume at " + volume.getPath() + " already exists: " + existing);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeVolumeLocked(StorageVolume volume) {
        Slog.d(TAG, "removeVolumeLocked() " + volume);
        this.mVolumes.remove(volume);
        this.mVolumesByPath.remove(volume.getPath());
        this.mVolumeStates.remove(volume.getPath());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public StorageVolume getPrimaryPhysicalVolume() {
        synchronized (this.mVolumesLock) {
            Iterator i$ = this.mVolumes.iterator();
            while (i$.hasNext()) {
                StorageVolume volume = i$.next();
                if (volume.isPrimary() && !volume.isEmulated()) {
                    return volume;
                }
            }
            return null;
        }
    }

    public MountService(Context context) {
        this.mContext = context;
        synchronized (this.mVolumesLock) {
            readStorageListLocked();
        }
        this.mPms = (PackageManagerService) ServiceManager.getService(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME);
        HandlerThread hthread = new HandlerThread(TAG);
        hthread.start();
        this.mHandler = new MountServiceHandler(hthread.getLooper());
        IntentFilter userFilter = new IntentFilter();
        userFilter.addAction(Intent.ACTION_USER_ADDED);
        userFilter.addAction(Intent.ACTION_USER_REMOVED);
        this.mContext.registerReceiver(this.mUserReceiver, userFilter, null, this.mHandler);
        StorageVolume primary = getPrimaryPhysicalVolume();
        if (primary != null && primary.allowMassStorage()) {
            this.mContext.registerReceiver(this.mUsbReceiver, new IntentFilter(UsbManager.ACTION_USB_STATE), null, this.mHandler);
        }
        IntentFilter idleMaintenanceFilter = new IntentFilter();
        idleMaintenanceFilter.addAction(Intent.ACTION_IDLE_MAINTENANCE_START);
        this.mContext.registerReceiverAsUser(this.mIdleMaintenanceReceiver, UserHandle.ALL, idleMaintenanceFilter, null, this.mHandler);
        this.mObbActionHandler = new ObbActionHandler(IoThread.get().getLooper());
        this.mConnector = new NativeDaemonConnector(this, "vold", 500, VOLD_TAG, 25);
        Thread thread = new Thread(this.mConnector, VOLD_TAG);
        thread.start();
    }

    public void systemReady() {
        this.mSystemReady = true;
        this.mHandler.obtainMessage(4).sendToTarget();
    }

    @Override // android.os.storage.IMountService
    public void registerListener(IMountServiceListener listener) {
        synchronized (this.mListeners) {
            MountServiceBinderListener bl = new MountServiceBinderListener(listener);
            try {
                listener.asBinder().linkToDeath(bl, 0);
                this.mListeners.add(bl);
            } catch (RemoteException e) {
                Slog.e(TAG, "Failed to link to listener death");
            }
        }
    }

    @Override // android.os.storage.IMountService
    public void unregisterListener(IMountServiceListener listener) {
        synchronized (this.mListeners) {
            Iterator i$ = this.mListeners.iterator();
            while (i$.hasNext()) {
                MountServiceBinderListener bl = i$.next();
                if (bl.mListener == listener) {
                    this.mListeners.remove(this.mListeners.indexOf(bl));
                    listener.asBinder().unlinkToDeath(bl, 0);
                    return;
                }
            }
        }
    }

    @Override // android.os.storage.IMountService
    public void shutdown(IMountShutdownObserver observer) {
        validatePermission(Manifest.permission.SHUTDOWN);
        Slog.i(TAG, "Shutting down");
        synchronized (this.mVolumesLock) {
            for (String path : this.mVolumeStates.keySet()) {
                String state = this.mVolumeStates.get(path);
                if (state.equals("shared")) {
                    setUsbMassStorageEnabled(false);
                } else if (state.equals(Environment.MEDIA_CHECKING)) {
                    int retries = 30;
                    while (state.equals(Environment.MEDIA_CHECKING)) {
                        int i = retries;
                        retries--;
                        if (i < 0) {
                            break;
                        }
                        try {
                            Thread.sleep(1000L);
                            state = Environment.getExternalStorageState();
                        } catch (InterruptedException iex) {
                            Slog.e(TAG, "Interrupted while waiting for media", iex);
                        }
                    }
                    if (retries == 0) {
                        Slog.e(TAG, "Timed out waiting for media to check");
                    }
                }
                if (state.equals(Environment.MEDIA_MOUNTED)) {
                    ShutdownCallBack ucb = new ShutdownCallBack(path, observer);
                    this.mHandler.sendMessage(this.mHandler.obtainMessage(1, ucb));
                } else if (observer != null) {
                    try {
                        observer.onShutDownComplete(0);
                    } catch (RemoteException e) {
                        Slog.w(TAG, "RemoteException when shutting down");
                    }
                }
            }
        }
    }

    private boolean getUmsEnabling() {
        boolean z;
        synchronized (this.mListeners) {
            z = this.mUmsEnabling;
        }
        return z;
    }

    private void setUmsEnabling(boolean enable) {
        synchronized (this.mListeners) {
            this.mUmsEnabling = enable;
        }
    }

    @Override // android.os.storage.IMountService
    public boolean isUsbMassStorageConnected() {
        boolean z;
        waitForReady();
        if (getUmsEnabling()) {
            return true;
        }
        synchronized (this.mListeners) {
            z = this.mUmsAvailable;
        }
        return z;
    }

    @Override // android.os.storage.IMountService
    public void setUsbMassStorageEnabled(boolean enable) {
        waitForReady();
        validatePermission(Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS);
        StorageVolume primary = getPrimaryPhysicalVolume();
        if (primary == null) {
            return;
        }
        String path = primary.getPath();
        String vs = getVolumeState(path);
        if (enable && vs.equals(Environment.MEDIA_MOUNTED)) {
            setUmsEnabling(enable);
            UmsEnableCallBack umscb = new UmsEnableCallBack(path, "ums", true);
            this.mHandler.sendMessage(this.mHandler.obtainMessage(1, umscb));
            setUmsEnabling(false);
        }
        if (!enable) {
            doShareUnshareVolume(path, "ums", enable);
            if (doMountVolume(path) != 0) {
                Slog.e(TAG, "Failed to remount " + path + " after disabling share method ums");
            }
        }
    }

    @Override // android.os.storage.IMountService
    public boolean isUsbMassStorageEnabled() {
        waitForReady();
        StorageVolume primary = getPrimaryPhysicalVolume();
        if (primary != null) {
            return doGetVolumeShared(primary.getPath(), "ums");
        }
        return false;
    }

    @Override // android.os.storage.IMountService
    public String getVolumeState(String mountPoint) {
        String str;
        synchronized (this.mVolumesLock) {
            String state = this.mVolumeStates.get(mountPoint);
            if (state == null) {
                Slog.w(TAG, "getVolumeState(" + mountPoint + "): Unknown volume");
                if (SystemProperties.get("vold.encrypt_progress").length() != 0) {
                    state = Environment.MEDIA_REMOVED;
                } else {
                    throw new IllegalArgumentException();
                }
            }
            str = state;
        }
        return str;
    }

    @Override // android.os.storage.IMountService
    public boolean isExternalStorageEmulated() {
        return this.mEmulatedTemplate != null;
    }

    @Override // android.os.storage.IMountService
    public int mountVolume(String path) {
        validatePermission(Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS);
        waitForReady();
        return doMountVolume(path);
    }

    @Override // android.os.storage.IMountService
    public void unmountVolume(String path, boolean force, boolean removeEncryption) {
        validatePermission(Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS);
        waitForReady();
        String volState = getVolumeState(path);
        if (Environment.MEDIA_UNMOUNTED.equals(volState) || Environment.MEDIA_REMOVED.equals(volState) || "shared".equals(volState) || Environment.MEDIA_UNMOUNTABLE.equals(volState)) {
            return;
        }
        UnmountCallBack ucb = new UnmountCallBack(path, force, removeEncryption);
        this.mHandler.sendMessage(this.mHandler.obtainMessage(1, ucb));
    }

    @Override // android.os.storage.IMountService
    public int formatVolume(String path) {
        validatePermission(Manifest.permission.MOUNT_FORMAT_FILESYSTEMS);
        waitForReady();
        return doFormatVolume(path);
    }

    @Override // android.os.storage.IMountService
    public int[] getStorageUsers(String path) {
        validatePermission(Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS);
        waitForReady();
        try {
            String[] r = NativeDaemonEvent.filterMessageList(this.mConnector.executeForList("storage", "users", path), 112);
            int[] data = new int[r.length];
            for (int i = 0; i < r.length; i++) {
                String[] tok = r[i].split(Separators.SP);
                try {
                    data[i] = Integer.parseInt(tok[0]);
                } catch (NumberFormatException e) {
                    Slog.e(TAG, String.format("Error parsing pid %s", tok[0]));
                    return new int[0];
                }
            }
            return data;
        } catch (NativeDaemonConnectorException e2) {
            Slog.e(TAG, "Failed to retrieve storage users list", e2);
            return new int[0];
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void warnOnNotMounted() {
        StorageVolume primary = getPrimaryPhysicalVolume();
        if (primary != null) {
            boolean mounted = false;
            try {
                mounted = Environment.MEDIA_MOUNTED.equals(getVolumeState(primary.getPath()));
            } catch (IllegalArgumentException e) {
            }
            if (!mounted) {
                Slog.w(TAG, "getSecureContainerList() called when storage not mounted");
            }
        }
    }

    @Override // android.os.storage.IMountService
    public String[] getSecureContainerList() {
        validatePermission(Manifest.permission.ASEC_ACCESS);
        waitForReady();
        warnOnNotMounted();
        try {
            return NativeDaemonEvent.filterMessageList(this.mConnector.executeForList("asec", "list"), 111);
        } catch (NativeDaemonConnectorException e) {
            return new String[0];
        }
    }

    @Override // android.os.storage.IMountService
    public int createSecureContainer(String id, int sizeMb, String fstype, String key, int ownerUid, boolean external) {
        validatePermission(Manifest.permission.ASEC_CREATE);
        waitForReady();
        warnOnNotMounted();
        int rc = 0;
        try {
            NativeDaemonConnector nativeDaemonConnector = this.mConnector;
            Object[] objArr = new Object[7];
            objArr[0] = "create";
            objArr[1] = id;
            objArr[2] = Integer.valueOf(sizeMb);
            objArr[3] = fstype;
            objArr[4] = new NativeDaemonConnector.SensitiveArg(key);
            objArr[5] = Integer.valueOf(ownerUid);
            objArr[6] = external ? "1" : "0";
            nativeDaemonConnector.execute("asec", objArr);
        } catch (NativeDaemonConnectorException e) {
            rc = -1;
        }
        if (rc == 0) {
            synchronized (this.mAsecMountSet) {
                this.mAsecMountSet.add(id);
            }
        }
        return rc;
    }

    @Override // android.os.storage.IMountService
    public int finalizeSecureContainer(String id) {
        validatePermission(Manifest.permission.ASEC_CREATE);
        warnOnNotMounted();
        int rc = 0;
        try {
            this.mConnector.execute("asec", "finalize", id);
        } catch (NativeDaemonConnectorException e) {
            rc = -1;
        }
        return rc;
    }

    @Override // android.os.storage.IMountService
    public int fixPermissionsSecureContainer(String id, int gid, String filename) {
        validatePermission(Manifest.permission.ASEC_CREATE);
        warnOnNotMounted();
        int rc = 0;
        try {
            this.mConnector.execute("asec", "fixperms", id, Integer.valueOf(gid), filename);
        } catch (NativeDaemonConnectorException e) {
            rc = -1;
        }
        return rc;
    }

    @Override // android.os.storage.IMountService
    public int destroySecureContainer(String id, boolean force) {
        validatePermission(Manifest.permission.ASEC_DESTROY);
        waitForReady();
        warnOnNotMounted();
        Runtime.getRuntime().gc();
        int rc = 0;
        try {
            NativeDaemonConnector.Command cmd = new NativeDaemonConnector.Command("asec", "destroy", id);
            if (force) {
                cmd.appendArg("force");
            }
            this.mConnector.execute(cmd);
        } catch (NativeDaemonConnectorException e) {
            int code = e.getCode();
            if (code == 405) {
                rc = -7;
            } else {
                rc = -1;
            }
        }
        if (rc == 0) {
            synchronized (this.mAsecMountSet) {
                if (this.mAsecMountSet.contains(id)) {
                    this.mAsecMountSet.remove(id);
                }
            }
        }
        return rc;
    }

    @Override // android.os.storage.IMountService
    public int mountSecureContainer(String id, String key, int ownerUid) {
        validatePermission(Manifest.permission.ASEC_MOUNT_UNMOUNT);
        waitForReady();
        warnOnNotMounted();
        synchronized (this.mAsecMountSet) {
            if (this.mAsecMountSet.contains(id)) {
                return -6;
            }
            int rc = 0;
            try {
                this.mConnector.execute("asec", "mount", id, new NativeDaemonConnector.SensitiveArg(key), Integer.valueOf(ownerUid));
            } catch (NativeDaemonConnectorException e) {
                int code = e.getCode();
                if (code != 405) {
                    rc = -1;
                }
            }
            if (rc == 0) {
                synchronized (this.mAsecMountSet) {
                    this.mAsecMountSet.add(id);
                }
            }
            return rc;
        }
    }

    @Override // android.os.storage.IMountService
    public int unmountSecureContainer(String id, boolean force) {
        validatePermission(Manifest.permission.ASEC_MOUNT_UNMOUNT);
        waitForReady();
        warnOnNotMounted();
        synchronized (this.mAsecMountSet) {
            if (!this.mAsecMountSet.contains(id)) {
                return -5;
            }
            Runtime.getRuntime().gc();
            int rc = 0;
            try {
                NativeDaemonConnector.Command cmd = new NativeDaemonConnector.Command("asec", "unmount", id);
                if (force) {
                    cmd.appendArg("force");
                }
                this.mConnector.execute(cmd);
            } catch (NativeDaemonConnectorException e) {
                int code = e.getCode();
                if (code == 405) {
                    rc = -7;
                } else {
                    rc = -1;
                }
            }
            if (rc == 0) {
                synchronized (this.mAsecMountSet) {
                    this.mAsecMountSet.remove(id);
                }
            }
            return rc;
        }
    }

    @Override // android.os.storage.IMountService
    public boolean isSecureContainerMounted(String id) {
        boolean contains;
        validatePermission(Manifest.permission.ASEC_ACCESS);
        waitForReady();
        warnOnNotMounted();
        synchronized (this.mAsecMountSet) {
            contains = this.mAsecMountSet.contains(id);
        }
        return contains;
    }

    @Override // android.os.storage.IMountService
    public int renameSecureContainer(String oldId, String newId) {
        validatePermission(Manifest.permission.ASEC_RENAME);
        waitForReady();
        warnOnNotMounted();
        synchronized (this.mAsecMountSet) {
            if (this.mAsecMountSet.contains(oldId) || this.mAsecMountSet.contains(newId)) {
                return -6;
            }
            int rc = 0;
            try {
                this.mConnector.execute("asec", "rename", oldId, newId);
            } catch (NativeDaemonConnectorException e) {
                rc = -1;
            }
            return rc;
        }
    }

    @Override // android.os.storage.IMountService
    public String getSecureContainerPath(String id) {
        validatePermission(Manifest.permission.ASEC_ACCESS);
        waitForReady();
        warnOnNotMounted();
        try {
            NativeDaemonEvent event = this.mConnector.execute("asec", "path", id);
            event.checkCode(211);
            return event.getMessage();
        } catch (NativeDaemonConnectorException e) {
            int code = e.getCode();
            if (code == 406) {
                Slog.i(TAG, String.format("Container '%s' not found", id));
                return null;
            }
            throw new IllegalStateException(String.format("Unexpected response code %d", Integer.valueOf(code)));
        }
    }

    @Override // android.os.storage.IMountService
    public String getSecureContainerFilesystemPath(String id) {
        validatePermission(Manifest.permission.ASEC_ACCESS);
        waitForReady();
        warnOnNotMounted();
        try {
            NativeDaemonEvent event = this.mConnector.execute("asec", "fspath", id);
            event.checkCode(211);
            return event.getMessage();
        } catch (NativeDaemonConnectorException e) {
            int code = e.getCode();
            if (code == 406) {
                Slog.i(TAG, String.format("Container '%s' not found", id));
                return null;
            }
            throw new IllegalStateException(String.format("Unexpected response code %d", Integer.valueOf(code)));
        }
    }

    @Override // android.os.storage.IMountService
    public void finishMediaUpdate() {
        this.mHandler.sendEmptyMessage(2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isUidOwnerOfPackageOrSystem(String packageName, int callerUid) {
        if (callerUid == 1000) {
            return true;
        }
        if (packageName == null) {
            return false;
        }
        int packageUid = this.mPms.getPackageUid(packageName, UserHandle.getUserId(callerUid));
        return callerUid == packageUid;
    }

    @Override // android.os.storage.IMountService
    public String getMountedObbPath(String rawPath) {
        ObbState state;
        Preconditions.checkNotNull(rawPath, "rawPath cannot be null");
        waitForReady();
        warnOnNotMounted();
        synchronized (this.mObbPathToStateMap) {
            state = this.mObbPathToStateMap.get(rawPath);
        }
        if (state == null) {
            Slog.w(TAG, "Failed to find OBB mounted at " + rawPath);
            return null;
        }
        try {
            NativeDaemonEvent event = this.mConnector.execute(FullBackup.OBB_TREE_TOKEN, "path", state.voldPath);
            event.checkCode(211);
            return event.getMessage();
        } catch (NativeDaemonConnectorException e) {
            int code = e.getCode();
            if (code == 406) {
                return null;
            }
            throw new IllegalStateException(String.format("Unexpected response code %d", Integer.valueOf(code)));
        }
    }

    @Override // android.os.storage.IMountService
    public boolean isObbMounted(String rawPath) {
        boolean containsKey;
        Preconditions.checkNotNull(rawPath, "rawPath cannot be null");
        synchronized (this.mObbMounts) {
            containsKey = this.mObbPathToStateMap.containsKey(rawPath);
        }
        return containsKey;
    }

    @Override // android.os.storage.IMountService
    public void mountObb(String rawPath, String canonicalPath, String key, IObbActionListener token, int nonce) {
        Preconditions.checkNotNull(rawPath, "rawPath cannot be null");
        Preconditions.checkNotNull(canonicalPath, "canonicalPath cannot be null");
        Preconditions.checkNotNull(token, "token cannot be null");
        int callingUid = Binder.getCallingUid();
        ObbState obbState = new ObbState(rawPath, canonicalPath, callingUid, token, nonce);
        ObbAction action = new MountObbAction(obbState, key, callingUid);
        this.mObbActionHandler.sendMessage(this.mObbActionHandler.obtainMessage(1, action));
    }

    @Override // android.os.storage.IMountService
    public void unmountObb(String rawPath, boolean force, IObbActionListener token, int nonce) {
        ObbState existingState;
        Preconditions.checkNotNull(rawPath, "rawPath cannot be null");
        synchronized (this.mObbPathToStateMap) {
            existingState = this.mObbPathToStateMap.get(rawPath);
        }
        if (existingState != null) {
            int callingUid = Binder.getCallingUid();
            ObbState newState = new ObbState(rawPath, existingState.canonicalPath, callingUid, token, nonce);
            ObbAction action = new UnmountObbAction(newState, force);
            this.mObbActionHandler.sendMessage(this.mObbActionHandler.obtainMessage(1, action));
            return;
        }
        Slog.w(TAG, "Unknown OBB mount at " + rawPath);
    }

    @Override // android.os.storage.IMountService
    public int getEncryptionState() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CRYPT_KEEPER, "no permission to access the crypt keeper");
        waitForReady();
        try {
            NativeDaemonEvent event = this.mConnector.execute("cryptfs", "cryptocomplete");
            return Integer.parseInt(event.getMessage());
        } catch (NativeDaemonConnectorException e) {
            Slog.w(TAG, "Error in communicating with cryptfs in validating");
            return -1;
        } catch (NumberFormatException e2) {
            Slog.w(TAG, "Unable to parse result from cryptfs cryptocomplete");
            return -1;
        }
    }

    @Override // android.os.storage.IMountService
    public int decryptStorage(String password) {
        if (TextUtils.isEmpty(password)) {
            throw new IllegalArgumentException("password cannot be empty");
        }
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CRYPT_KEEPER, "no permission to access the crypt keeper");
        waitForReady();
        try {
            NativeDaemonEvent event = this.mConnector.execute("cryptfs", "checkpw", new NativeDaemonConnector.SensitiveArg(password));
            int code = Integer.parseInt(event.getMessage());
            if (code == 0) {
                this.mHandler.postDelayed(new Runnable() { // from class: com.android.server.MountService.7
                    @Override // java.lang.Runnable
                    public void run() {
                        try {
                            MountService.this.mConnector.execute("cryptfs", "restart");
                        } catch (NativeDaemonConnectorException e) {
                            Slog.e(MountService.TAG, "problem executing in background", e);
                        }
                    }
                }, 1000L);
            }
            return code;
        } catch (NativeDaemonConnectorException e) {
            return e.getCode();
        }
    }

    @Override // android.os.storage.IMountService
    public int encryptStorage(String password) {
        if (TextUtils.isEmpty(password)) {
            throw new IllegalArgumentException("password cannot be empty");
        }
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CRYPT_KEEPER, "no permission to access the crypt keeper");
        waitForReady();
        try {
            this.mConnector.execute("cryptfs", "enablecrypto", "inplace", new NativeDaemonConnector.SensitiveArg(password));
            return 0;
        } catch (NativeDaemonConnectorException e) {
            return e.getCode();
        }
    }

    @Override // android.os.storage.IMountService
    public int changeEncryptionPassword(String password) {
        if (TextUtils.isEmpty(password)) {
            throw new IllegalArgumentException("password cannot be empty");
        }
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CRYPT_KEEPER, "no permission to access the crypt keeper");
        waitForReady();
        try {
            NativeDaemonEvent event = this.mConnector.execute("cryptfs", "changepw", new NativeDaemonConnector.SensitiveArg(password));
            return Integer.parseInt(event.getMessage());
        } catch (NativeDaemonConnectorException e) {
            return e.getCode();
        }
    }

    @Override // android.os.storage.IMountService
    public int verifyEncryptionPassword(String password) throws RemoteException {
        if (Binder.getCallingUid() != 1000) {
            throw new SecurityException("no permission to access the crypt keeper");
        }
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CRYPT_KEEPER, "no permission to access the crypt keeper");
        if (TextUtils.isEmpty(password)) {
            throw new IllegalArgumentException("password cannot be empty");
        }
        waitForReady();
        try {
            NativeDaemonEvent event = this.mConnector.execute("cryptfs", "verifypw", new NativeDaemonConnector.SensitiveArg(password));
            Slog.i(TAG, "cryptfs verifypw => " + event.getMessage());
            return Integer.parseInt(event.getMessage());
        } catch (NativeDaemonConnectorException e) {
            return e.getCode();
        }
    }

    @Override // android.os.storage.IMountService
    public int mkdirs(String callingPkg, String appPath) {
        int userId = UserHandle.getUserId(Binder.getCallingUid());
        Environment.UserEnvironment userEnv = new Environment.UserEnvironment(userId);
        AppOpsManager appOps = (AppOpsManager) this.mContext.getSystemService(Context.APP_OPS_SERVICE);
        appOps.checkPackage(Binder.getCallingUid(), callingPkg);
        try {
            appPath = new File(appPath).getCanonicalPath();
            if (!appPath.endsWith(Separators.SLASH)) {
                appPath = appPath + Separators.SLASH;
            }
            String voldPath = maybeTranslatePathForVold(appPath, userEnv.buildExternalStorageAppDataDirs(callingPkg), userEnv.buildExternalStorageAppDataDirsForVold(callingPkg));
            if (voldPath != null) {
                try {
                    this.mConnector.execute("volume", "mkdirs", voldPath);
                    return 0;
                } catch (NativeDaemonConnectorException e) {
                    return e.getCode();
                }
            }
            String voldPath2 = maybeTranslatePathForVold(appPath, userEnv.buildExternalStorageAppObbDirs(callingPkg), userEnv.buildExternalStorageAppObbDirsForVold(callingPkg));
            if (voldPath2 != null) {
                try {
                    this.mConnector.execute("volume", "mkdirs", voldPath2);
                    return 0;
                } catch (NativeDaemonConnectorException e2) {
                    return e2.getCode();
                }
            }
            throw new SecurityException("Invalid mkdirs path: " + appPath);
        } catch (IOException e3) {
            Slog.e(TAG, "Failed to resolve " + appPath + ": " + e3);
            return -1;
        }
    }

    public static String maybeTranslatePathForVold(String path, File[] appPaths, File[] voldPaths) {
        if (appPaths.length != voldPaths.length) {
            throw new IllegalStateException("Paths must be 1:1 mapping");
        }
        for (int i = 0; i < appPaths.length; i++) {
            String appPath = appPaths[i].getAbsolutePath() + Separators.SLASH;
            if (path.startsWith(appPath)) {
                String path2 = new File(voldPaths[i], path.substring(appPath.length())).getAbsolutePath();
                if (!path2.endsWith(Separators.SLASH)) {
                    path2 = path2 + Separators.SLASH;
                }
                return path2;
            }
        }
        return null;
    }

    @Override // android.os.storage.IMountService
    public StorageVolume[] getVolumeList() {
        StorageVolume[] storageVolumeArr;
        int callingUserId = UserHandle.getCallingUserId();
        boolean accessAll = this.mContext.checkPermission(Manifest.permission.ACCESS_ALL_EXTERNAL_STORAGE, Binder.getCallingPid(), Binder.getCallingUid()) == 0;
        synchronized (this.mVolumesLock) {
            ArrayList<StorageVolume> filtered = Lists.newArrayList();
            Iterator i$ = this.mVolumes.iterator();
            while (i$.hasNext()) {
                StorageVolume volume = i$.next();
                UserHandle owner = volume.getOwner();
                boolean ownerMatch = owner == null || owner.getIdentifier() == callingUserId;
                if (accessAll || ownerMatch) {
                    filtered.add(volume);
                }
            }
            storageVolumeArr = (StorageVolume[]) filtered.toArray(new StorageVolume[filtered.size()]);
        }
        return storageVolumeArr;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addObbStateLocked(ObbState obbState) throws RemoteException {
        IBinder binder = obbState.getBinder();
        List<ObbState> obbStates = this.mObbMounts.get(binder);
        if (obbStates == null) {
            obbStates = new ArrayList<>();
            this.mObbMounts.put(binder, obbStates);
        } else {
            for (ObbState o : obbStates) {
                if (o.rawPath.equals(obbState.rawPath)) {
                    throw new IllegalStateException("Attempt to add ObbState twice. This indicates an error in the MountService logic.");
                }
            }
        }
        obbStates.add(obbState);
        try {
            obbState.link();
            this.mObbPathToStateMap.put(obbState.rawPath, obbState);
        } catch (RemoteException e) {
            obbStates.remove(obbState);
            if (obbStates.isEmpty()) {
                this.mObbMounts.remove(binder);
            }
            throw e;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeObbStateLocked(ObbState obbState) {
        IBinder binder = obbState.getBinder();
        List<ObbState> obbStates = this.mObbMounts.get(binder);
        if (obbStates != null) {
            if (obbStates.remove(obbState)) {
                obbState.unlink();
            }
            if (obbStates.isEmpty()) {
                this.mObbMounts.remove(binder);
            }
        }
        this.mObbPathToStateMap.remove(obbState.rawPath);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: MountService$ObbActionHandler.class */
    public class ObbActionHandler extends Handler {
        private boolean mBound;
        private final List<ObbAction> mActions;

        ObbActionHandler(Looper l) {
            super(l);
            this.mBound = false;
            this.mActions = new LinkedList();
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    ObbAction action = (ObbAction) msg.obj;
                    if (!this.mBound && !connectToService()) {
                        Slog.e(MountService.TAG, "Failed to bind to media container service");
                        action.handleError();
                        return;
                    }
                    this.mActions.add(action);
                    return;
                case 2:
                    if (msg.obj != null) {
                        MountService.this.mContainerService = (IMediaContainerService) msg.obj;
                    }
                    if (MountService.this.mContainerService == null) {
                        Slog.e(MountService.TAG, "Cannot bind to media container service");
                        for (ObbAction action2 : this.mActions) {
                            action2.handleError();
                        }
                        this.mActions.clear();
                        return;
                    } else if (this.mActions.size() > 0) {
                        ObbAction action3 = this.mActions.get(0);
                        if (action3 != null) {
                            action3.execute(this);
                            return;
                        }
                        return;
                    } else {
                        Slog.w(MountService.TAG, "Empty queue");
                        return;
                    }
                case 3:
                    if (this.mActions.size() > 0) {
                        this.mActions.remove(0);
                    }
                    if (this.mActions.size() != 0) {
                        MountService.this.mObbActionHandler.sendEmptyMessage(2);
                        return;
                    } else if (this.mBound) {
                        disconnectService();
                        return;
                    } else {
                        return;
                    }
                case 4:
                    if (this.mActions.size() > 0) {
                        if (this.mBound) {
                            disconnectService();
                        }
                        if (!connectToService()) {
                            Slog.e(MountService.TAG, "Failed to bind to media container service");
                            for (ObbAction action4 : this.mActions) {
                                action4.handleError();
                            }
                            this.mActions.clear();
                            return;
                        }
                        return;
                    }
                    return;
                case 5:
                    String path = (String) msg.obj;
                    synchronized (MountService.this.mObbMounts) {
                        List<ObbState> obbStatesToRemove = new LinkedList<>();
                        for (ObbState state : MountService.this.mObbPathToStateMap.values()) {
                            if (state.canonicalPath.startsWith(path)) {
                                obbStatesToRemove.add(state);
                            }
                        }
                        for (ObbState obbState : obbStatesToRemove) {
                            MountService.this.removeObbStateLocked(obbState);
                            try {
                                obbState.token.onObbResult(obbState.rawPath, obbState.nonce, 2);
                            } catch (RemoteException e) {
                                Slog.i(MountService.TAG, "Couldn't send unmount notification for  OBB: " + obbState.rawPath);
                            }
                        }
                    }
                    return;
                default:
                    return;
            }
        }

        private boolean connectToService() {
            Intent service = new Intent().setComponent(MountService.DEFAULT_CONTAINER_COMPONENT);
            if (MountService.this.mContext.bindService(service, MountService.this.mDefContainerConn, 1)) {
                this.mBound = true;
                return true;
            }
            return false;
        }

        private void disconnectService() {
            MountService.this.mContainerService = null;
            this.mBound = false;
            MountService.this.mContext.unbindService(MountService.this.mDefContainerConn);
        }
    }

    /* loaded from: MountService$ObbAction.class */
    abstract class ObbAction {
        private static final int MAX_RETRIES = 3;
        private int mRetries;
        ObbState mObbState;

        abstract void handleExecute() throws RemoteException, IOException;

        abstract void handleError();

        ObbAction(ObbState obbState) {
            this.mObbState = obbState;
        }

        public void execute(ObbActionHandler handler) {
            try {
                this.mRetries++;
                if (this.mRetries > 3) {
                    Slog.w(MountService.TAG, "Failed to invoke remote methods on default container service. Giving up");
                    MountService.this.mObbActionHandler.sendEmptyMessage(3);
                    handleError();
                    return;
                }
                handleExecute();
                MountService.this.mObbActionHandler.sendEmptyMessage(3);
            } catch (RemoteException e) {
                MountService.this.mObbActionHandler.sendEmptyMessage(4);
            } catch (Exception e2) {
                handleError();
                MountService.this.mObbActionHandler.sendEmptyMessage(3);
            }
        }

        protected ObbInfo getObbInfo() throws IOException {
            ObbInfo obbInfo;
            try {
                obbInfo = MountService.this.mContainerService.getObbInfo(this.mObbState.ownerPath);
            } catch (RemoteException e) {
                Slog.d(MountService.TAG, "Couldn't call DefaultContainerService to fetch OBB info for " + this.mObbState.ownerPath);
                obbInfo = null;
            }
            if (obbInfo == null) {
                throw new IOException("Couldn't read OBB file: " + this.mObbState.ownerPath);
            }
            return obbInfo;
        }

        protected void sendNewStatusOrIgnore(int status) {
            if (this.mObbState == null || this.mObbState.token == null) {
                return;
            }
            try {
                this.mObbState.token.onObbResult(this.mObbState.rawPath, this.mObbState.nonce, status);
            } catch (RemoteException e) {
                Slog.w(MountService.TAG, "MountServiceListener went away while calling onObbStateChanged");
            }
        }
    }

    /* loaded from: MountService$MountObbAction.class */
    class MountObbAction extends ObbAction {
        private final String mKey;
        private final int mCallingUid;

        MountObbAction(ObbState obbState, String key, int callingUid) {
            super(obbState);
            this.mKey = key;
            this.mCallingUid = callingUid;
        }

        @Override // com.android.server.MountService.ObbAction
        public void handleExecute() throws IOException, RemoteException {
            boolean isMounted;
            String hashedKey;
            MountService.this.waitForReady();
            MountService.this.warnOnNotMounted();
            ObbInfo obbInfo = getObbInfo();
            if (MountService.this.isUidOwnerOfPackageOrSystem(obbInfo.packageName, this.mCallingUid)) {
                synchronized (MountService.this.mObbMounts) {
                    isMounted = MountService.this.mObbPathToStateMap.containsKey(this.mObbState.rawPath);
                }
                if (isMounted) {
                    Slog.w(MountService.TAG, "Attempt to mount OBB which is already mounted: " + obbInfo.filename);
                    sendNewStatusOrIgnore(24);
                    return;
                }
                if (this.mKey == null) {
                    hashedKey = "none";
                } else {
                    try {
                        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
                        KeySpec ks = new PBEKeySpec(this.mKey.toCharArray(), obbInfo.salt, 1024, 128);
                        SecretKey key = factory.generateSecret(ks);
                        BigInteger bi = new BigInteger(key.getEncoded());
                        hashedKey = bi.toString(16);
                    } catch (NoSuchAlgorithmException e) {
                        Slog.e(MountService.TAG, "Could not load PBKDF2 algorithm", e);
                        sendNewStatusOrIgnore(20);
                        return;
                    } catch (InvalidKeySpecException e2) {
                        Slog.e(MountService.TAG, "Invalid key spec when loading PBKDF2 algorithm", e2);
                        sendNewStatusOrIgnore(20);
                        return;
                    }
                }
                int rc = 0;
                try {
                    MountService.this.mConnector.execute(FullBackup.OBB_TREE_TOKEN, "mount", this.mObbState.voldPath, new NativeDaemonConnector.SensitiveArg(hashedKey), Integer.valueOf(this.mObbState.ownerGid));
                } catch (NativeDaemonConnectorException e3) {
                    int code = e3.getCode();
                    if (code != 405) {
                        rc = -1;
                    }
                }
                if (rc == 0) {
                    synchronized (MountService.this.mObbMounts) {
                        MountService.this.addObbStateLocked(this.mObbState);
                    }
                    sendNewStatusOrIgnore(1);
                    return;
                }
                Slog.e(MountService.TAG, "Couldn't mount OBB file: " + rc);
                sendNewStatusOrIgnore(21);
                return;
            }
            Slog.w(MountService.TAG, "Denied attempt to mount OBB " + obbInfo.filename + " which is owned by " + obbInfo.packageName);
            sendNewStatusOrIgnore(25);
        }

        @Override // com.android.server.MountService.ObbAction
        public void handleError() {
            sendNewStatusOrIgnore(20);
        }

        public String toString() {
            return "MountObbAction{" + this.mObbState + '}';
        }
    }

    /* loaded from: MountService$UnmountObbAction.class */
    class UnmountObbAction extends ObbAction {
        private final boolean mForceUnmount;

        UnmountObbAction(ObbState obbState, boolean force) {
            super(obbState);
            this.mForceUnmount = force;
        }

        @Override // com.android.server.MountService.ObbAction
        public void handleExecute() throws IOException {
            ObbState existingState;
            MountService.this.waitForReady();
            MountService.this.warnOnNotMounted();
            getObbInfo();
            synchronized (MountService.this.mObbMounts) {
                existingState = (ObbState) MountService.this.mObbPathToStateMap.get(this.mObbState.rawPath);
            }
            if (existingState == null) {
                sendNewStatusOrIgnore(23);
            } else if (existingState.ownerGid != this.mObbState.ownerGid) {
                Slog.w(MountService.TAG, "Permission denied attempting to unmount OBB " + existingState.rawPath + " (owned by GID " + existingState.ownerGid + Separators.RPAREN);
                sendNewStatusOrIgnore(25);
            } else {
                int rc = 0;
                try {
                    NativeDaemonConnector.Command cmd = new NativeDaemonConnector.Command(FullBackup.OBB_TREE_TOKEN, "unmount", this.mObbState.voldPath);
                    if (this.mForceUnmount) {
                        cmd.appendArg("force");
                    }
                    MountService.this.mConnector.execute(cmd);
                } catch (NativeDaemonConnectorException e) {
                    int code = e.getCode();
                    if (code == 405) {
                        rc = -7;
                    } else if (code == 406) {
                        rc = 0;
                    } else {
                        rc = -1;
                    }
                }
                if (rc == 0) {
                    synchronized (MountService.this.mObbMounts) {
                        MountService.this.removeObbStateLocked(existingState);
                    }
                    sendNewStatusOrIgnore(2);
                    return;
                }
                Slog.w(MountService.TAG, "Could not unmount OBB: " + existingState);
                sendNewStatusOrIgnore(22);
            }
        }

        @Override // com.android.server.MountService.ObbAction
        public void handleError() {
            sendNewStatusOrIgnore(20);
        }

        public String toString() {
            return "UnmountObbAction{" + this.mObbState + ",force=" + this.mForceUnmount + '}';
        }
    }

    public static String buildObbPath(String canonicalPath, int userId, boolean forVold) {
        String path;
        if (!Environment.isExternalStorageEmulated()) {
            return canonicalPath;
        }
        String path2 = canonicalPath.toString();
        Environment.UserEnvironment userEnv = new Environment.UserEnvironment(userId);
        String externalPath = userEnv.getExternalStorageDirectory().getAbsolutePath();
        String legacyExternalPath = Environment.getLegacyExternalStorageDirectory().getAbsolutePath();
        if (path2.startsWith(externalPath)) {
            path = path2.substring(externalPath.length() + 1);
        } else if (path2.startsWith(legacyExternalPath)) {
            path = path2.substring(legacyExternalPath.length() + 1);
        } else {
            return canonicalPath;
        }
        if (path.startsWith("Android/obb")) {
            String path3 = path.substring("Android/obb".length() + 1);
            if (forVold) {
                return new File(Environment.getEmulatedStorageObbSource(), path3).getAbsolutePath();
            }
            Environment.UserEnvironment ownerEnv = new Environment.UserEnvironment(0);
            return new File(ownerEnv.buildExternalStorageAndroidObbDirs()[0], path3).getAbsolutePath();
        } else if (forVold) {
            return new File(Environment.getEmulatedStorageSource(userId), path).getAbsolutePath();
        } else {
            return new File(userEnv.getExternalDirsForApp()[0], path).getAbsolutePath();
        }
    }

    @Override // android.os.Binder
    protected void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.DUMP) != 0) {
            pw.println("Permission Denial: can't dump ActivityManager from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " without permission " + Manifest.permission.DUMP);
            return;
        }
        synchronized (this.mObbMounts) {
            pw.println("  mObbMounts:");
            for (Map.Entry<IBinder, List<ObbState>> e : this.mObbMounts.entrySet()) {
                pw.print("    Key=");
                pw.println(e.getKey().toString());
                List<ObbState> obbStates = e.getValue();
                for (ObbState obbState : obbStates) {
                    pw.print("      ");
                    pw.println(obbState.toString());
                }
            }
            pw.println("");
            pw.println("  mObbPathToStateMap:");
            for (Map.Entry<String, ObbState> e2 : this.mObbPathToStateMap.entrySet()) {
                pw.print("    ");
                pw.print(e2.getKey());
                pw.print(" -> ");
                pw.println(e2.getValue().toString());
            }
        }
        pw.println("");
        synchronized (this.mVolumesLock) {
            pw.println("  mVolumes:");
            int N = this.mVolumes.size();
            for (int i = 0; i < N; i++) {
                StorageVolume v = this.mVolumes.get(i);
                pw.print("    ");
                pw.println(v.toString());
                pw.println("      state=" + this.mVolumeStates.get(v.getPath()));
            }
        }
        pw.println();
        pw.println("  mConnection:");
        this.mConnector.dump(fd, pw, args);
    }

    @Override // com.android.server.Watchdog.Monitor
    public void monitor() {
        if (this.mConnector != null) {
            this.mConnector.monitor();
        }
    }
}