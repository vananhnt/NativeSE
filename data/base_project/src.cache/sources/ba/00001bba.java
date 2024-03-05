package com.android.server;

import android.Manifest;
import android.accounts.GrantCredentialsPermissionActivity;
import android.app.AppOpsManager;
import android.app.backup.FullBackup;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.provider.MediaStore;
import android.util.ArrayMap;
import android.util.AtomicFile;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.util.TimeUtils;
import android.util.Xml;
import com.android.internal.app.IAppOpsCallback;
import com.android.internal.app.IAppOpsService;
import com.android.internal.util.FastXmlSerializer;
import com.android.internal.util.XmlUtils;
import gov.nist.core.Separators;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

/* loaded from: AppOpsService.class */
public class AppOpsService extends IAppOpsService.Stub {
    static final String TAG = "AppOps";
    static final boolean DEBUG = false;
    static final long WRITE_DELAY = 1800000;
    Context mContext;
    final AtomicFile mFile;
    boolean mWriteScheduled;
    final Runnable mWriteRunner = new Runnable() { // from class: com.android.server.AppOpsService.1
        @Override // java.lang.Runnable
        public void run() {
            synchronized (AppOpsService.this) {
                AppOpsService.this.mWriteScheduled = false;
                AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() { // from class: com.android.server.AppOpsService.1.1
                    /* JADX INFO: Access modifiers changed from: protected */
                    @Override // android.os.AsyncTask
                    public Void doInBackground(Void... params) {
                        AppOpsService.this.writeState();
                        return null;
                    }
                };
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
            }
        }
    };
    final SparseArray<HashMap<String, Ops>> mUidOps = new SparseArray<>();
    final SparseArray<ArrayList<Callback>> mOpModeWatchers = new SparseArray<>();
    final ArrayMap<String, ArrayList<Callback>> mPackageModeWatchers = new ArrayMap<>();
    final ArrayMap<IBinder, Callback> mModeWatchers = new ArrayMap<>();
    final ArrayMap<IBinder, ClientState> mClients = new ArrayMap<>();
    final Handler mHandler = new Handler();

    /* loaded from: AppOpsService$Ops.class */
    public static final class Ops extends SparseArray<Op> {
        public final String packageName;
        public final int uid;

        public Ops(String _packageName, int _uid) {
            this.packageName = _packageName;
            this.uid = _uid;
        }
    }

    /* loaded from: AppOpsService$Op.class */
    public static final class Op {
        public final int uid;
        public final String packageName;
        public final int op;
        public int mode;
        public int duration;
        public long time;
        public long rejectTime;
        public int nesting;

        public Op(int _uid, String _packageName, int _op) {
            this.uid = _uid;
            this.packageName = _packageName;
            this.op = _op;
            this.mode = AppOpsManager.opToDefaultMode(this.op);
        }
    }

    /* loaded from: AppOpsService$Callback.class */
    public final class Callback implements IBinder.DeathRecipient {
        final IAppOpsCallback mCallback;

        public Callback(IAppOpsCallback callback) {
            this.mCallback = callback;
            try {
                this.mCallback.asBinder().linkToDeath(this, 0);
            } catch (RemoteException e) {
            }
        }

        public void unlinkToDeath() {
            this.mCallback.asBinder().unlinkToDeath(this, 0);
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            AppOpsService.this.stopWatchingMode(this.mCallback);
        }
    }

    /* loaded from: AppOpsService$ClientState.class */
    public final class ClientState extends Binder implements IBinder.DeathRecipient {
        final IBinder mAppToken;
        final int mPid = Binder.getCallingPid();
        final ArrayList<Op> mStartedOps;

        public ClientState(IBinder appToken) {
            this.mAppToken = appToken;
            if (appToken instanceof Binder) {
                this.mStartedOps = null;
                return;
            }
            this.mStartedOps = new ArrayList<>();
            try {
                this.mAppToken.linkToDeath(this, 0);
            } catch (RemoteException e) {
            }
        }

        public String toString() {
            return "ClientState{mAppToken=" + this.mAppToken + ", " + (this.mStartedOps != null ? "pid=" + this.mPid : "local") + '}';
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            synchronized (AppOpsService.this) {
                for (int i = this.mStartedOps.size() - 1; i >= 0; i--) {
                    AppOpsService.this.finishOperationLocked(this.mStartedOps.get(i));
                }
                AppOpsService.this.mClients.remove(this.mAppToken);
            }
        }
    }

    public AppOpsService(File storagePath) {
        this.mFile = new AtomicFile(storagePath);
        readState();
    }

    public void publish(Context context) {
        this.mContext = context;
        ServiceManager.addService(Context.APP_OPS_SERVICE, asBinder());
    }

    public void systemReady() {
        int curUid;
        synchronized (this) {
            boolean changed = false;
            for (int i = 0; i < this.mUidOps.size(); i++) {
                HashMap<String, Ops> pkgs = this.mUidOps.valueAt(i);
                Iterator<Ops> it = pkgs.values().iterator();
                while (it.hasNext()) {
                    Ops ops = it.next();
                    try {
                        curUid = this.mContext.getPackageManager().getPackageUid(ops.packageName, UserHandle.getUserId(ops.uid));
                    } catch (PackageManager.NameNotFoundException e) {
                        curUid = -1;
                    }
                    if (curUid != ops.uid) {
                        Slog.i(TAG, "Pruning old package " + ops.packageName + Separators.SLASH + ops.uid + ": new uid=" + curUid);
                        it.remove();
                        changed = true;
                    }
                }
                if (pkgs.size() <= 0) {
                    this.mUidOps.removeAt(i);
                }
            }
            if (changed) {
                scheduleWriteLocked();
            }
        }
    }

    public void packageRemoved(int uid, String packageName) {
        synchronized (this) {
            HashMap<String, Ops> pkgs = this.mUidOps.get(uid);
            if (pkgs != null && pkgs.remove(packageName) != null) {
                if (pkgs.size() <= 0) {
                    this.mUidOps.remove(uid);
                }
                scheduleWriteLocked();
            }
        }
    }

    public void uidRemoved(int uid) {
        synchronized (this) {
            if (this.mUidOps.indexOfKey(uid) >= 0) {
                this.mUidOps.remove(uid);
                scheduleWriteLocked();
            }
        }
    }

    public void shutdown() {
        Slog.w(TAG, "Writing app ops before shutdown...");
        boolean doWrite = false;
        synchronized (this) {
            if (this.mWriteScheduled) {
                this.mWriteScheduled = false;
                doWrite = true;
            }
        }
        if (doWrite) {
            writeState();
        }
    }

    private ArrayList<AppOpsManager.OpEntry> collectOps(Ops pkgOps, int[] ops) {
        ArrayList<AppOpsManager.OpEntry> resOps = null;
        if (ops == null) {
            resOps = new ArrayList<>();
            for (int j = 0; j < pkgOps.size(); j++) {
                Op curOp = pkgOps.valueAt(j);
                resOps.add(new AppOpsManager.OpEntry(curOp.op, curOp.mode, curOp.time, curOp.rejectTime, curOp.duration));
            }
        } else {
            for (int i : ops) {
                Op curOp2 = pkgOps.get(i);
                if (curOp2 != null) {
                    if (resOps == null) {
                        resOps = new ArrayList<>();
                    }
                    resOps.add(new AppOpsManager.OpEntry(curOp2.op, curOp2.mode, curOp2.time, curOp2.rejectTime, curOp2.duration));
                }
            }
        }
        return resOps;
    }

    @Override // com.android.internal.app.IAppOpsService
    public List<AppOpsManager.PackageOps> getPackagesForOps(int[] ops) {
        this.mContext.enforcePermission(Manifest.permission.GET_APP_OPS_STATS, Binder.getCallingPid(), Binder.getCallingUid(), null);
        ArrayList<AppOpsManager.PackageOps> res = null;
        synchronized (this) {
            for (int i = 0; i < this.mUidOps.size(); i++) {
                HashMap<String, Ops> packages = this.mUidOps.valueAt(i);
                for (Ops pkgOps : packages.values()) {
                    ArrayList<AppOpsManager.OpEntry> resOps = collectOps(pkgOps, ops);
                    if (resOps != null) {
                        if (res == null) {
                            res = new ArrayList<>();
                        }
                        AppOpsManager.PackageOps resPackage = new AppOpsManager.PackageOps(pkgOps.packageName, pkgOps.uid, resOps);
                        res.add(resPackage);
                    }
                }
            }
        }
        return res;
    }

    @Override // com.android.internal.app.IAppOpsService
    public List<AppOpsManager.PackageOps> getOpsForPackage(int uid, String packageName, int[] ops) {
        this.mContext.enforcePermission(Manifest.permission.GET_APP_OPS_STATS, Binder.getCallingPid(), Binder.getCallingUid(), null);
        synchronized (this) {
            Ops pkgOps = getOpsLocked(uid, packageName, false);
            if (pkgOps == null) {
                return null;
            }
            ArrayList<AppOpsManager.OpEntry> resOps = collectOps(pkgOps, ops);
            if (resOps == null) {
                return null;
            }
            ArrayList<AppOpsManager.PackageOps> res = new ArrayList<>();
            AppOpsManager.PackageOps resPackage = new AppOpsManager.PackageOps(pkgOps.packageName, pkgOps.uid, resOps);
            res.add(resPackage);
            return res;
        }
    }

    private void pruneOp(Op op, int uid, String packageName) {
        Ops ops;
        HashMap<String, Ops> pkgOps;
        if (op.time == 0 && op.rejectTime == 0 && (ops = getOpsLocked(uid, packageName, false)) != null) {
            ops.remove(op.op);
            if (ops.size() <= 0 && (pkgOps = this.mUidOps.get(uid)) != null) {
                pkgOps.remove(ops.packageName);
                if (pkgOps.size() <= 0) {
                    this.mUidOps.remove(uid);
                }
            }
        }
    }

    @Override // com.android.internal.app.IAppOpsService
    public void setMode(int code, int uid, String packageName, int mode) {
        verifyIncomingUid(uid);
        verifyIncomingOp(code);
        ArrayList<Callback> repCbs = null;
        int code2 = AppOpsManager.opToSwitch(code);
        synchronized (this) {
            Op op = getOpLocked(code2, uid, packageName, true);
            if (op != null && op.mode != mode) {
                op.mode = mode;
                ArrayList<Callback> cbs = this.mOpModeWatchers.get(code2);
                if (cbs != null) {
                    if (0 == 0) {
                        repCbs = new ArrayList<>();
                    }
                    repCbs.addAll(cbs);
                }
                ArrayList<Callback> cbs2 = this.mPackageModeWatchers.get(packageName);
                if (cbs2 != null) {
                    if (repCbs == null) {
                        repCbs = new ArrayList<>();
                    }
                    repCbs.addAll(cbs2);
                }
                if (mode == AppOpsManager.opToDefaultMode(op.op)) {
                    pruneOp(op, uid, packageName);
                }
                scheduleWriteNowLocked();
            }
        }
        if (repCbs != null) {
            for (int i = 0; i < repCbs.size(); i++) {
                try {
                    repCbs.get(i).mCallback.opChanged(code2, packageName);
                } catch (RemoteException e) {
                }
            }
        }
    }

    private static HashMap<Callback, ArrayList<Pair<String, Integer>>> addCallbacks(HashMap<Callback, ArrayList<Pair<String, Integer>>> callbacks, String packageName, int op, ArrayList<Callback> cbs) {
        if (cbs == null) {
            return callbacks;
        }
        if (callbacks == null) {
            callbacks = new HashMap<>();
        }
        for (int i = 0; i < cbs.size(); i++) {
            Callback cb = cbs.get(i);
            ArrayList<Pair<String, Integer>> reports = callbacks.get(cb);
            if (reports == null) {
                reports = new ArrayList<>();
                callbacks.put(cb, reports);
            }
            reports.add(new Pair<>(packageName, Integer.valueOf(op)));
        }
        return callbacks;
    }

    @Override // com.android.internal.app.IAppOpsService
    public void resetAllModes() {
        this.mContext.enforcePermission(Manifest.permission.UPDATE_APP_OPS_STATS, Binder.getCallingPid(), Binder.getCallingUid(), null);
        HashMap<Callback, ArrayList<Pair<String, Integer>>> callbacks = null;
        synchronized (this) {
            boolean changed = false;
            for (int i = this.mUidOps.size() - 1; i >= 0; i--) {
                HashMap<String, Ops> packages = this.mUidOps.valueAt(i);
                Iterator<Map.Entry<String, Ops>> it = packages.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, Ops> ent = it.next();
                    String packageName = ent.getKey();
                    Ops pkgOps = ent.getValue();
                    for (int j = pkgOps.size() - 1; j >= 0; j--) {
                        Op curOp = pkgOps.valueAt(j);
                        if (AppOpsManager.opAllowsReset(curOp.op) && curOp.mode != AppOpsManager.opToDefaultMode(curOp.op)) {
                            curOp.mode = AppOpsManager.opToDefaultMode(curOp.op);
                            changed = true;
                            callbacks = addCallbacks(addCallbacks(callbacks, packageName, curOp.op, this.mOpModeWatchers.get(curOp.op)), packageName, curOp.op, this.mPackageModeWatchers.get(packageName));
                            if (curOp.time == 0 && curOp.rejectTime == 0) {
                                pkgOps.removeAt(j);
                            }
                        }
                    }
                    if (pkgOps.size() == 0) {
                        it.remove();
                    }
                }
                if (packages.size() == 0) {
                    this.mUidOps.removeAt(i);
                }
            }
            if (changed) {
                scheduleWriteNowLocked();
            }
        }
        if (callbacks != null) {
            for (Map.Entry<Callback, ArrayList<Pair<String, Integer>>> ent2 : callbacks.entrySet()) {
                Callback cb = ent2.getKey();
                ArrayList<Pair<String, Integer>> reports = ent2.getValue();
                for (int i2 = 0; i2 < reports.size(); i2++) {
                    Pair<String, Integer> rep = reports.get(i2);
                    try {
                        cb.mCallback.opChanged(rep.second.intValue(), rep.first);
                    } catch (RemoteException e) {
                    }
                }
            }
        }
    }

    @Override // com.android.internal.app.IAppOpsService
    public void startWatchingMode(int op, String packageName, IAppOpsCallback callback) {
        synchronized (this) {
            int op2 = AppOpsManager.opToSwitch(op);
            Callback cb = this.mModeWatchers.get(callback.asBinder());
            if (cb == null) {
                cb = new Callback(callback);
                this.mModeWatchers.put(callback.asBinder(), cb);
            }
            if (op2 != -1) {
                ArrayList<Callback> cbs = this.mOpModeWatchers.get(op2);
                if (cbs == null) {
                    cbs = new ArrayList<>();
                    this.mOpModeWatchers.put(op2, cbs);
                }
                cbs.add(cb);
            }
            if (packageName != null) {
                ArrayList<Callback> cbs2 = this.mPackageModeWatchers.get(packageName);
                if (cbs2 == null) {
                    cbs2 = new ArrayList<>();
                    this.mPackageModeWatchers.put(packageName, cbs2);
                }
                cbs2.add(cb);
            }
        }
    }

    @Override // com.android.internal.app.IAppOpsService
    public void stopWatchingMode(IAppOpsCallback callback) {
        synchronized (this) {
            Callback cb = this.mModeWatchers.remove(callback.asBinder());
            if (cb != null) {
                cb.unlinkToDeath();
                for (int i = this.mOpModeWatchers.size() - 1; i >= 0; i--) {
                    ArrayList<Callback> cbs = this.mOpModeWatchers.valueAt(i);
                    cbs.remove(cb);
                    if (cbs.size() <= 0) {
                        this.mOpModeWatchers.removeAt(i);
                    }
                }
                for (int i2 = this.mPackageModeWatchers.size() - 1; i2 >= 0; i2--) {
                    ArrayList<Callback> cbs2 = this.mPackageModeWatchers.valueAt(i2);
                    cbs2.remove(cb);
                    if (cbs2.size() <= 0) {
                        this.mPackageModeWatchers.removeAt(i2);
                    }
                }
            }
        }
    }

    @Override // com.android.internal.app.IAppOpsService
    public IBinder getToken(IBinder clientToken) {
        ClientState clientState;
        synchronized (this) {
            ClientState cs = this.mClients.get(clientToken);
            if (cs == null) {
                cs = new ClientState(clientToken);
                this.mClients.put(clientToken, cs);
            }
            clientState = cs;
        }
        return clientState;
    }

    @Override // com.android.internal.app.IAppOpsService
    public int checkOperation(int code, int uid, String packageName) {
        verifyIncomingUid(uid);
        verifyIncomingOp(code);
        synchronized (this) {
            Op op = getOpLocked(AppOpsManager.opToSwitch(code), uid, packageName, false);
            if (op == null) {
                return AppOpsManager.opToDefaultMode(code);
            }
            return op.mode;
        }
    }

    @Override // com.android.internal.app.IAppOpsService
    public int checkPackage(int uid, String packageName) {
        synchronized (this) {
            if (getOpsLocked(uid, packageName, true) != null) {
                return 0;
            }
            return 2;
        }
    }

    @Override // com.android.internal.app.IAppOpsService
    public int noteOperation(int code, int uid, String packageName) {
        verifyIncomingUid(uid);
        verifyIncomingOp(code);
        synchronized (this) {
            Ops ops = getOpsLocked(uid, packageName, true);
            if (ops == null) {
                return 2;
            }
            Op op = getOpLocked(ops, code, true);
            if (op.duration == -1) {
                Slog.w(TAG, "Noting op not finished: uid " + uid + " pkg " + packageName + " code " + code + " time=" + op.time + " duration=" + op.duration);
            }
            op.duration = 0;
            int switchCode = AppOpsManager.opToSwitch(code);
            Op switchOp = switchCode != code ? getOpLocked(ops, switchCode, true) : op;
            if (switchOp.mode != 0) {
                op.rejectTime = System.currentTimeMillis();
                return switchOp.mode;
            }
            op.time = System.currentTimeMillis();
            op.rejectTime = 0L;
            return 0;
        }
    }

    @Override // com.android.internal.app.IAppOpsService
    public int startOperation(IBinder token, int code, int uid, String packageName) {
        verifyIncomingUid(uid);
        verifyIncomingOp(code);
        ClientState client = (ClientState) token;
        synchronized (this) {
            Ops ops = getOpsLocked(uid, packageName, true);
            if (ops == null) {
                return 2;
            }
            Op op = getOpLocked(ops, code, true);
            int switchCode = AppOpsManager.opToSwitch(code);
            Op switchOp = switchCode != code ? getOpLocked(ops, switchCode, true) : op;
            if (switchOp.mode != 0) {
                op.rejectTime = System.currentTimeMillis();
                return switchOp.mode;
            }
            if (op.nesting == 0) {
                op.time = System.currentTimeMillis();
                op.rejectTime = 0L;
                op.duration = -1;
            }
            op.nesting++;
            if (client.mStartedOps != null) {
                client.mStartedOps.add(op);
            }
            return 0;
        }
    }

    @Override // com.android.internal.app.IAppOpsService
    public void finishOperation(IBinder token, int code, int uid, String packageName) {
        verifyIncomingUid(uid);
        verifyIncomingOp(code);
        ClientState client = (ClientState) token;
        synchronized (this) {
            Op op = getOpLocked(code, uid, packageName, true);
            if (op == null) {
                return;
            }
            if (client.mStartedOps != null && !client.mStartedOps.remove(op)) {
                throw new IllegalStateException("Operation not started: uid" + op.uid + " pkg=" + op.packageName + " op=" + op.op);
            }
            finishOperationLocked(op);
        }
    }

    void finishOperationLocked(Op op) {
        if (op.nesting <= 1) {
            if (op.nesting == 1) {
                op.duration = (int) (System.currentTimeMillis() - op.time);
                op.time += op.duration;
            } else {
                Slog.w(TAG, "Finishing op nesting under-run: uid " + op.uid + " pkg " + op.packageName + " code " + op.op + " time=" + op.time + " duration=" + op.duration + " nesting=" + op.nesting);
            }
            op.nesting = 0;
            return;
        }
        op.nesting--;
    }

    private void verifyIncomingUid(int uid) {
        if (uid == Binder.getCallingUid() || Binder.getCallingPid() == Process.myPid()) {
            return;
        }
        this.mContext.enforcePermission(Manifest.permission.UPDATE_APP_OPS_STATS, Binder.getCallingPid(), Binder.getCallingUid(), null);
    }

    private void verifyIncomingOp(int op) {
        if (op >= 0 && op < 43) {
            return;
        }
        throw new IllegalArgumentException("Bad operation #" + op);
    }

    private Ops getOpsLocked(int uid, String packageName, boolean edit) {
        HashMap<String, Ops> pkgOps = this.mUidOps.get(uid);
        if (pkgOps == null) {
            if (!edit) {
                return null;
            }
            pkgOps = new HashMap<>();
            this.mUidOps.put(uid, pkgOps);
        }
        if (uid == 0) {
            packageName = "root";
        } else if (uid == 2000) {
            packageName = "com.android.shell";
        }
        Ops ops = pkgOps.get(packageName);
        if (ops == null) {
            if (!edit) {
                return null;
            }
            if (uid != 0) {
                long ident = Binder.clearCallingIdentity();
                int pkgUid = -1;
                try {
                    try {
                        pkgUid = this.mContext.getPackageManager().getPackageUid(packageName, UserHandle.getUserId(uid));
                    } catch (PackageManager.NameNotFoundException e) {
                        if (MediaStore.AUTHORITY.equals(packageName)) {
                            pkgUid = 1013;
                        }
                    }
                    if (pkgUid != uid) {
                        Slog.w(TAG, "Bad call: specified package " + packageName + " under uid " + uid + " but it is really " + pkgUid);
                        Binder.restoreCallingIdentity(ident);
                        return null;
                    }
                    Binder.restoreCallingIdentity(ident);
                } catch (Throwable th) {
                    Binder.restoreCallingIdentity(ident);
                    throw th;
                }
            }
            ops = new Ops(packageName, uid);
            pkgOps.put(packageName, ops);
        }
        return ops;
    }

    private void scheduleWriteLocked() {
        if (!this.mWriteScheduled) {
            this.mWriteScheduled = true;
            this.mHandler.postDelayed(this.mWriteRunner, 1800000L);
        }
    }

    private void scheduleWriteNowLocked() {
        if (!this.mWriteScheduled) {
            this.mWriteScheduled = true;
        }
        this.mHandler.removeCallbacks(this.mWriteRunner);
        this.mHandler.post(this.mWriteRunner);
    }

    private Op getOpLocked(int code, int uid, String packageName, boolean edit) {
        Ops ops = getOpsLocked(uid, packageName, edit);
        if (ops == null) {
            return null;
        }
        return getOpLocked(ops, code, edit);
    }

    private Op getOpLocked(Ops ops, int code, boolean edit) {
        Op op = ops.get(code);
        if (op == null) {
            if (!edit) {
                return null;
            }
            op = new Op(ops.uid, ops.packageName, code);
            ops.put(code, op);
        }
        if (edit) {
            scheduleWriteLocked();
        }
        return op;
    }

    void readState() {
        XmlPullParser parser;
        int type;
        synchronized (this.mFile) {
            synchronized (this) {
                try {
                    FileInputStream stream = this.mFile.openRead();
                    boolean success = false;
                    try {
                        try {
                            parser = Xml.newPullParser();
                            parser.setInput(stream, null);
                            while (true) {
                                type = parser.next();
                                if (type == 2 || type == 1) {
                                    break;
                                }
                            }
                        } catch (IOException e) {
                            Slog.w(TAG, "Failed parsing " + e);
                            if (!success) {
                                this.mUidOps.clear();
                            }
                            try {
                                stream.close();
                            } catch (IOException e2) {
                            }
                        } catch (IllegalStateException e3) {
                            Slog.w(TAG, "Failed parsing " + e3);
                            if (!success) {
                                this.mUidOps.clear();
                            }
                            try {
                                stream.close();
                            } catch (IOException e4) {
                            }
                        } catch (IndexOutOfBoundsException e5) {
                            Slog.w(TAG, "Failed parsing " + e5);
                            if (!success) {
                                this.mUidOps.clear();
                            }
                            try {
                                stream.close();
                            } catch (IOException e6) {
                            }
                        } catch (NullPointerException e7) {
                            Slog.w(TAG, "Failed parsing " + e7);
                            if (!success) {
                                this.mUidOps.clear();
                            }
                            try {
                                stream.close();
                            } catch (IOException e8) {
                            }
                        } catch (NumberFormatException e9) {
                            Slog.w(TAG, "Failed parsing " + e9);
                            if (!success) {
                                this.mUidOps.clear();
                            }
                            try {
                                stream.close();
                            } catch (IOException e10) {
                            }
                        } catch (XmlPullParserException e11) {
                            Slog.w(TAG, "Failed parsing " + e11);
                            if (!success) {
                                this.mUidOps.clear();
                            }
                            try {
                                stream.close();
                            } catch (IOException e12) {
                            }
                        }
                        if (type != 2) {
                            throw new IllegalStateException("no start tag found");
                        }
                        int outerDepth = parser.getDepth();
                        while (true) {
                            int type2 = parser.next();
                            if (type2 == 1 || (type2 == 3 && parser.getDepth() <= outerDepth)) {
                                break;
                            } else if (type2 != 3 && type2 != 4) {
                                String tagName = parser.getName();
                                if (tagName.equals("pkg")) {
                                    readPackage(parser);
                                } else {
                                    Slog.w(TAG, "Unknown element under <app-ops>: " + parser.getName());
                                    XmlUtils.skipCurrentTag(parser);
                                }
                            }
                        }
                        success = true;
                    } finally {
                        if (!success) {
                            this.mUidOps.clear();
                        }
                        try {
                            stream.close();
                        } catch (IOException e13) {
                        }
                    }
                } catch (FileNotFoundException e14) {
                    Slog.i(TAG, "No existing app ops " + this.mFile.getBaseFile() + "; starting empty");
                }
            }
        }
    }

    void readPackage(XmlPullParser parser) throws NumberFormatException, XmlPullParserException, IOException {
        String pkgName = parser.getAttributeValue(null, "n");
        int outerDepth = parser.getDepth();
        while (true) {
            int type = parser.next();
            if (type == 1) {
                return;
            }
            if (type != 3 || parser.getDepth() > outerDepth) {
                if (type != 3 && type != 4) {
                    String tagName = parser.getName();
                    if (tagName.equals(GrantCredentialsPermissionActivity.EXTRAS_REQUESTING_UID)) {
                        readUid(parser, pkgName);
                    } else {
                        Slog.w(TAG, "Unknown element under <pkg>: " + parser.getName());
                        XmlUtils.skipCurrentTag(parser);
                    }
                }
            } else {
                return;
            }
        }
    }

    void readUid(XmlPullParser parser, String pkgName) throws NumberFormatException, XmlPullParserException, IOException {
        int uid = Integer.parseInt(parser.getAttributeValue(null, "n"));
        int outerDepth = parser.getDepth();
        while (true) {
            int type = parser.next();
            if (type == 1) {
                return;
            }
            if (type != 3 || parser.getDepth() > outerDepth) {
                if (type != 3 && type != 4) {
                    String tagName = parser.getName();
                    if (tagName.equals("op")) {
                        Op op = new Op(uid, pkgName, Integer.parseInt(parser.getAttributeValue(null, "n")));
                        String mode = parser.getAttributeValue(null, "m");
                        if (mode != null) {
                            op.mode = Integer.parseInt(mode);
                        }
                        String time = parser.getAttributeValue(null, "t");
                        if (time != null) {
                            op.time = Long.parseLong(time);
                        }
                        String time2 = parser.getAttributeValue(null, FullBackup.ROOT_TREE_TOKEN);
                        if (time2 != null) {
                            op.rejectTime = Long.parseLong(time2);
                        }
                        String dur = parser.getAttributeValue(null, "d");
                        if (dur != null) {
                            op.duration = Integer.parseInt(dur);
                        }
                        HashMap<String, Ops> pkgOps = this.mUidOps.get(uid);
                        if (pkgOps == null) {
                            pkgOps = new HashMap<>();
                            this.mUidOps.put(uid, pkgOps);
                        }
                        Ops ops = pkgOps.get(pkgName);
                        if (ops == null) {
                            ops = new Ops(pkgName, uid);
                            pkgOps.put(pkgName, ops);
                        }
                        ops.put(op.op, op);
                    } else {
                        Slog.w(TAG, "Unknown element under <pkg>: " + parser.getName());
                        XmlUtils.skipCurrentTag(parser);
                    }
                }
            } else {
                return;
            }
        }
    }

    void writeState() {
        synchronized (this.mFile) {
            List<AppOpsManager.PackageOps> allOps = getPackagesForOps(null);
            try {
                FileOutputStream stream = this.mFile.startWrite();
                try {
                    XmlSerializer out = new FastXmlSerializer();
                    out.setOutput(stream, "utf-8");
                    out.startDocument(null, true);
                    out.startTag(null, "app-ops");
                    if (allOps != null) {
                        String lastPkg = null;
                        for (int i = 0; i < allOps.size(); i++) {
                            AppOpsManager.PackageOps pkg = allOps.get(i);
                            if (!pkg.getPackageName().equals(lastPkg)) {
                                if (lastPkg != null) {
                                    out.endTag(null, "pkg");
                                }
                                lastPkg = pkg.getPackageName();
                                out.startTag(null, "pkg");
                                out.attribute(null, "n", lastPkg);
                            }
                            out.startTag(null, GrantCredentialsPermissionActivity.EXTRAS_REQUESTING_UID);
                            out.attribute(null, "n", Integer.toString(pkg.getUid()));
                            List<AppOpsManager.OpEntry> ops = pkg.getOps();
                            for (int j = 0; j < ops.size(); j++) {
                                AppOpsManager.OpEntry op = ops.get(j);
                                out.startTag(null, "op");
                                out.attribute(null, "n", Integer.toString(op.getOp()));
                                if (op.getMode() != AppOpsManager.opToDefaultMode(op.getOp())) {
                                    out.attribute(null, "m", Integer.toString(op.getMode()));
                                }
                                long time = op.getTime();
                                if (time != 0) {
                                    out.attribute(null, "t", Long.toString(time));
                                }
                                long time2 = op.getRejectTime();
                                if (time2 != 0) {
                                    out.attribute(null, FullBackup.ROOT_TREE_TOKEN, Long.toString(time2));
                                }
                                int dur = op.getDuration();
                                if (dur != 0) {
                                    out.attribute(null, "d", Integer.toString(dur));
                                }
                                out.endTag(null, "op");
                            }
                            out.endTag(null, GrantCredentialsPermissionActivity.EXTRAS_REQUESTING_UID);
                        }
                        if (lastPkg != null) {
                            out.endTag(null, "pkg");
                        }
                    }
                    out.endTag(null, "app-ops");
                    out.endDocument();
                    this.mFile.finishWrite(stream);
                } catch (IOException e) {
                    Slog.w(TAG, "Failed to write state, restoring backup.", e);
                    this.mFile.failWrite(stream);
                }
            } catch (IOException e2) {
                Slog.w(TAG, "Failed to write state: " + e2);
            }
        }
    }

    @Override // android.os.Binder
    protected void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.DUMP) != 0) {
            pw.println("Permission Denial: can't dump ApOps service from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
            return;
        }
        synchronized (this) {
            pw.println("Current AppOps Service state:");
            long now = System.currentTimeMillis();
            boolean needSep = false;
            if (this.mOpModeWatchers.size() > 0) {
                needSep = true;
                pw.println("  Op mode watchers:");
                for (int i = 0; i < this.mOpModeWatchers.size(); i++) {
                    pw.print("    Op ");
                    pw.print(AppOpsManager.opToName(this.mOpModeWatchers.keyAt(i)));
                    pw.println(Separators.COLON);
                    ArrayList<Callback> callbacks = this.mOpModeWatchers.valueAt(i);
                    for (int j = 0; j < callbacks.size(); j++) {
                        pw.print("      #");
                        pw.print(j);
                        pw.print(": ");
                        pw.println(callbacks.get(j));
                    }
                }
            }
            if (this.mPackageModeWatchers.size() > 0) {
                needSep = true;
                pw.println("  Package mode watchers:");
                for (int i2 = 0; i2 < this.mPackageModeWatchers.size(); i2++) {
                    pw.print("    Pkg ");
                    pw.print(this.mPackageModeWatchers.keyAt(i2));
                    pw.println(Separators.COLON);
                    ArrayList<Callback> callbacks2 = this.mPackageModeWatchers.valueAt(i2);
                    for (int j2 = 0; j2 < callbacks2.size(); j2++) {
                        pw.print("      #");
                        pw.print(j2);
                        pw.print(": ");
                        pw.println(callbacks2.get(j2));
                    }
                }
            }
            if (this.mModeWatchers.size() > 0) {
                needSep = true;
                pw.println("  All mode watchers:");
                for (int i3 = 0; i3 < this.mModeWatchers.size(); i3++) {
                    pw.print("    ");
                    pw.print(this.mModeWatchers.keyAt(i3));
                    pw.print(" -> ");
                    pw.println(this.mModeWatchers.valueAt(i3));
                }
            }
            if (this.mClients.size() > 0) {
                needSep = true;
                pw.println("  Clients:");
                for (int i4 = 0; i4 < this.mClients.size(); i4++) {
                    pw.print("    ");
                    pw.print(this.mClients.keyAt(i4));
                    pw.println(Separators.COLON);
                    ClientState cs = this.mClients.valueAt(i4);
                    pw.print("      ");
                    pw.println(cs);
                    if (cs.mStartedOps != null && cs.mStartedOps.size() > 0) {
                        pw.println("      Started ops:");
                        for (int j3 = 0; j3 < cs.mStartedOps.size(); j3++) {
                            Op op = cs.mStartedOps.get(j3);
                            pw.print("        ");
                            pw.print("uid=");
                            pw.print(op.uid);
                            pw.print(" pkg=");
                            pw.print(op.packageName);
                            pw.print(" op=");
                            pw.println(AppOpsManager.opToName(op.op));
                        }
                    }
                }
            }
            if (needSep) {
                pw.println();
            }
            for (int i5 = 0; i5 < this.mUidOps.size(); i5++) {
                pw.print("  Uid ");
                UserHandle.formatUid(pw, this.mUidOps.keyAt(i5));
                pw.println(Separators.COLON);
                HashMap<String, Ops> pkgOps = this.mUidOps.valueAt(i5);
                for (Ops ops : pkgOps.values()) {
                    pw.print("    Package ");
                    pw.print(ops.packageName);
                    pw.println(Separators.COLON);
                    for (int j4 = 0; j4 < ops.size(); j4++) {
                        Op op2 = ops.valueAt(j4);
                        pw.print("      ");
                        pw.print(AppOpsManager.opToName(op2.op));
                        pw.print(": mode=");
                        pw.print(op2.mode);
                        if (op2.time != 0) {
                            pw.print("; time=");
                            TimeUtils.formatDuration(now - op2.time, pw);
                            pw.print(" ago");
                        }
                        if (op2.rejectTime != 0) {
                            pw.print("; rejectTime=");
                            TimeUtils.formatDuration(now - op2.rejectTime, pw);
                            pw.print(" ago");
                        }
                        if (op2.duration == -1) {
                            pw.println(" (running)");
                        } else {
                            pw.print("; duration=");
                            TimeUtils.formatDuration(op2.duration, pw);
                            pw.println();
                        }
                    }
                }
            }
        }
    }
}