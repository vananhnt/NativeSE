package com.android.server;

import android.app.ActivityManagerNative;
import android.app.AppOpsManager;
import android.app.IActivityManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.IClipboard;
import android.content.IOnPrimaryClipChangedListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Slog;
import android.util.SparseArray;
import java.util.HashSet;

/* loaded from: ClipboardService.class */
public class ClipboardService extends IClipboard.Stub {
    private static final String TAG = "ClipboardService";
    private final Context mContext;
    private final PackageManager mPm;
    private final AppOpsManager mAppOps;
    private final IBinder mPermissionOwner;
    private SparseArray<PerUserClipboard> mClipboards = new SparseArray<>();
    private final IActivityManager mAm = ActivityManagerNative.getDefault();

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.ClipboardService.setPrimaryClip(android.content.ClipData, java.lang.String):void, file: ClipboardService.class
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
    @Override // android.content.IClipboard
    public void setPrimaryClip(android.content.ClipData r1, java.lang.String r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.ClipboardService.setPrimaryClip(android.content.ClipData, java.lang.String):void, file: ClipboardService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.ClipboardService.setPrimaryClip(android.content.ClipData, java.lang.String):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.ClipboardService.checkUriOwnerLocked(android.net.Uri, int):void, file: ClipboardService.class
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
    private final void checkUriOwnerLocked(android.net.Uri r1, int r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.ClipboardService.checkUriOwnerLocked(android.net.Uri, int):void, file: ClipboardService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.ClipboardService.checkUriOwnerLocked(android.net.Uri, int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.ClipboardService.grantUriLocked(android.net.Uri, java.lang.String):void, file: ClipboardService.class
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
    private final void grantUriLocked(android.net.Uri r1, java.lang.String r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.ClipboardService.grantUriLocked(android.net.Uri, java.lang.String):void, file: ClipboardService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.ClipboardService.grantUriLocked(android.net.Uri, java.lang.String):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.ClipboardService.addActiveOwnerLocked(int, java.lang.String):void, file: ClipboardService.class
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
    private final void addActiveOwnerLocked(int r1, java.lang.String r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.ClipboardService.addActiveOwnerLocked(int, java.lang.String):void, file: ClipboardService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.ClipboardService.addActiveOwnerLocked(int, java.lang.String):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.ClipboardService.revokeUriLocked(android.net.Uri):void, file: ClipboardService.class
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
    private final void revokeUriLocked(android.net.Uri r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.ClipboardService.revokeUriLocked(android.net.Uri):void, file: ClipboardService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.ClipboardService.revokeUriLocked(android.net.Uri):void");
    }

    /* loaded from: ClipboardService$ListenerInfo.class */
    private class ListenerInfo {
        final int mUid;
        final String mPackageName;

        ListenerInfo(int uid, String packageName) {
            this.mUid = uid;
            this.mPackageName = packageName;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ClipboardService$PerUserClipboard.class */
    public class PerUserClipboard {
        final int userId;
        ClipData primaryClip;
        final RemoteCallbackList<IOnPrimaryClipChangedListener> primaryClipListeners = new RemoteCallbackList<>();
        final HashSet<String> activePermissionOwners = new HashSet<>();

        PerUserClipboard(int userId) {
            this.userId = userId;
        }
    }

    public ClipboardService(Context context) {
        this.mContext = context;
        this.mPm = context.getPackageManager();
        this.mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        IBinder permOwner = null;
        try {
            permOwner = this.mAm.newUriPermissionOwner(Context.CLIPBOARD_SERVICE);
        } catch (RemoteException e) {
            Slog.w(Context.CLIPBOARD_SERVICE, "AM dead", e);
        }
        this.mPermissionOwner = permOwner;
        IntentFilter userFilter = new IntentFilter();
        userFilter.addAction(Intent.ACTION_USER_REMOVED);
        this.mContext.registerReceiver(new BroadcastReceiver() { // from class: com.android.server.ClipboardService.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                String action = intent.getAction();
                if (Intent.ACTION_USER_REMOVED.equals(action)) {
                    ClipboardService.this.removeClipboard(intent.getIntExtra(Intent.EXTRA_USER_HANDLE, 0));
                }
            }
        }, userFilter);
    }

    @Override // android.content.IClipboard.Stub, android.os.Binder
    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        try {
            return super.onTransact(code, data, reply, flags);
        } catch (RuntimeException e) {
            if (!(e instanceof SecurityException)) {
                Slog.wtf(Context.CLIPBOARD_SERVICE, "Exception: ", e);
            }
            throw e;
        }
    }

    private PerUserClipboard getClipboard() {
        return getClipboard(UserHandle.getCallingUserId());
    }

    private PerUserClipboard getClipboard(int userId) {
        PerUserClipboard perUserClipboard;
        synchronized (this.mClipboards) {
            PerUserClipboard puc = this.mClipboards.get(userId);
            if (puc == null) {
                puc = new PerUserClipboard(userId);
                this.mClipboards.put(userId, puc);
            }
            perUserClipboard = puc;
        }
        return perUserClipboard;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeClipboard(int userId) {
        synchronized (this.mClipboards) {
            this.mClipboards.remove(userId);
        }
    }

    @Override // android.content.IClipboard
    public ClipData getPrimaryClip(String pkg) {
        synchronized (this) {
            if (this.mAppOps.noteOp(29, Binder.getCallingUid(), pkg) != 0) {
                return null;
            }
            addActiveOwnerLocked(Binder.getCallingUid(), pkg);
            return getClipboard().primaryClip;
        }
    }

    @Override // android.content.IClipboard
    public ClipDescription getPrimaryClipDescription(String callingPackage) {
        synchronized (this) {
            if (this.mAppOps.checkOp(29, Binder.getCallingUid(), callingPackage) != 0) {
                return null;
            }
            PerUserClipboard clipboard = getClipboard();
            return clipboard.primaryClip != null ? clipboard.primaryClip.getDescription() : null;
        }
    }

    @Override // android.content.IClipboard
    public boolean hasPrimaryClip(String callingPackage) {
        synchronized (this) {
            if (this.mAppOps.checkOp(29, Binder.getCallingUid(), callingPackage) != 0) {
                return false;
            }
            return getClipboard().primaryClip != null;
        }
    }

    @Override // android.content.IClipboard
    public void addPrimaryClipChangedListener(IOnPrimaryClipChangedListener listener, String callingPackage) {
        synchronized (this) {
            getClipboard().primaryClipListeners.register(listener, new ListenerInfo(Binder.getCallingUid(), callingPackage));
        }
    }

    @Override // android.content.IClipboard
    public void removePrimaryClipChangedListener(IOnPrimaryClipChangedListener listener) {
        synchronized (this) {
            getClipboard().primaryClipListeners.unregister(listener);
        }
    }

    @Override // android.content.IClipboard
    public boolean hasClipboardText(String callingPackage) {
        synchronized (this) {
            if (this.mAppOps.checkOp(29, Binder.getCallingUid(), callingPackage) != 0) {
                return false;
            }
            PerUserClipboard clipboard = getClipboard();
            if (clipboard.primaryClip != null) {
                CharSequence text = clipboard.primaryClip.getItemAt(0).getText();
                return text != null && text.length() > 0;
            }
            return false;
        }
    }

    private final void checkItemOwnerLocked(ClipData.Item item, int uid) {
        if (item.getUri() != null) {
            checkUriOwnerLocked(item.getUri(), uid);
        }
        Intent intent = item.getIntent();
        if (intent != null && intent.getData() != null) {
            checkUriOwnerLocked(intent.getData(), uid);
        }
    }

    private final void checkDataOwnerLocked(ClipData data, int uid) {
        int N = data.getItemCount();
        for (int i = 0; i < N; i++) {
            checkItemOwnerLocked(data.getItemAt(i), uid);
        }
    }

    private final void grantItemLocked(ClipData.Item item, String pkg) {
        if (item.getUri() != null) {
            grantUriLocked(item.getUri(), pkg);
        }
        Intent intent = item.getIntent();
        if (intent != null && intent.getData() != null) {
            grantUriLocked(intent.getData(), pkg);
        }
    }

    private final void revokeItemLocked(ClipData.Item item) {
        if (item.getUri() != null) {
            revokeUriLocked(item.getUri());
        }
        Intent intent = item.getIntent();
        if (intent != null && intent.getData() != null) {
            revokeUriLocked(intent.getData());
        }
    }

    private final void clearActiveOwnersLocked() {
        PerUserClipboard clipboard = getClipboard();
        clipboard.activePermissionOwners.clear();
        if (clipboard.primaryClip == null) {
            return;
        }
        int N = clipboard.primaryClip.getItemCount();
        for (int i = 0; i < N; i++) {
            revokeItemLocked(clipboard.primaryClip.getItemAt(i));
        }
    }
}