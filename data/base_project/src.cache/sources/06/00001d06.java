package com.android.server;

import android.Manifest;
import android.content.Context;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.IUpdateLock;
import android.os.RemoteException;
import android.os.TokenWatcher;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* loaded from: UpdateLockService.class */
public class UpdateLockService extends IUpdateLock.Stub {
    static final boolean DEBUG = false;
    static final String TAG = "UpdateLockService";
    static final String PERMISSION = "android.permission.UPDATE_LOCK";
    Context mContext;
    LockWatcher mLocks = new LockWatcher(new Handler(), "UpdateLocks");

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.UpdateLockService.sendLockChangedBroadcast(boolean):void, file: UpdateLockService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    void sendLockChangedBroadcast(boolean r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.UpdateLockService.sendLockChangedBroadcast(boolean):void, file: UpdateLockService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.UpdateLockService.sendLockChangedBroadcast(boolean):void");
    }

    /* loaded from: UpdateLockService$LockWatcher.class */
    class LockWatcher extends TokenWatcher {
        LockWatcher(Handler h, String tag) {
            super(h, tag);
        }

        @Override // android.os.TokenWatcher
        public void acquired() {
            UpdateLockService.this.sendLockChangedBroadcast(false);
        }

        @Override // android.os.TokenWatcher
        public void released() {
            UpdateLockService.this.sendLockChangedBroadcast(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public UpdateLockService(Context context) {
        this.mContext = context;
        sendLockChangedBroadcast(true);
    }

    @Override // android.os.IUpdateLock
    public void acquireUpdateLock(IBinder token, String tag) throws RemoteException {
        this.mContext.enforceCallingOrSelfPermission("android.permission.UPDATE_LOCK", "acquireUpdateLock");
        this.mLocks.acquire(token, makeTag(tag));
    }

    @Override // android.os.IUpdateLock
    public void releaseUpdateLock(IBinder token) throws RemoteException {
        this.mContext.enforceCallingOrSelfPermission("android.permission.UPDATE_LOCK", "releaseUpdateLock");
        this.mLocks.release(token);
    }

    private String makeTag(String tag) {
        return "{tag=" + tag + " uid=" + Binder.getCallingUid() + " pid=" + Binder.getCallingPid() + '}';
    }

    @Override // android.os.Binder
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.DUMP) != 0) {
            pw.println("Permission Denial: can't dump update lock service from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
        } else {
            this.mLocks.dump(pw);
        }
    }
}