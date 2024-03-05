package com.android.server.wm;

import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Slog;
import android.view.IWindow;
import android.view.IWindowId;
import android.view.IWindowSession;
import android.view.InputChannel;
import android.view.Surface;
import android.view.SurfaceSession;
import android.view.WindowManager;
import com.android.internal.view.IInputContext;
import com.android.internal.view.IInputMethodClient;
import java.io.PrintWriter;

/*  JADX ERROR: NullPointerException in pass: ExtractFieldInit
    java.lang.NullPointerException
    	at jadx.core.utils.BlockUtils.isAllBlocksEmpty(BlockUtils.java:977)
    	at jadx.core.dex.visitors.ExtractFieldInit.getConstructorsList(ExtractFieldInit.java:379)
    	at jadx.core.dex.visitors.ExtractFieldInit.moveCommonFieldsInit(ExtractFieldInit.java:128)
    	at jadx.core.dex.visitors.ExtractFieldInit.visit(ExtractFieldInit.java:51)
    */
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: Session.class */
public final class Session extends IWindowSession.Stub implements IBinder.DeathRecipient {
    final WindowManagerService mService;
    final IInputMethodClient mClient;
    final IInputContext mInputContext;
    final int mUid;
    final int mPid;
    final String mStringName;
    SurfaceSession mSurfaceSession;
    int mNumWindow;
    boolean mClientDead;

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.Session.<init>(com.android.server.wm.WindowManagerService, com.android.internal.view.IInputMethodClient, com.android.internal.view.IInputContext):void, file: Session.class
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
    public Session(com.android.server.wm.WindowManagerService r1, com.android.internal.view.IInputMethodClient r2, com.android.internal.view.IInputContext r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.Session.<init>(com.android.server.wm.WindowManagerService, com.android.internal.view.IInputMethodClient, com.android.internal.view.IInputContext):void, file: Session.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.Session.<init>(com.android.server.wm.WindowManagerService, com.android.internal.view.IInputMethodClient, com.android.internal.view.IInputContext):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.Session.performHapticFeedback(android.view.IWindow, int, boolean):boolean, file: Session.class
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
    @Override // android.view.IWindowSession
    public boolean performHapticFeedback(android.view.IWindow r1, int r2, boolean r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.Session.performHapticFeedback(android.view.IWindow, int, boolean):boolean, file: Session.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.Session.performHapticFeedback(android.view.IWindow, int, boolean):boolean");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.Session.performDrag(android.view.IWindow, android.os.IBinder, float, float, float, float, android.content.ClipData):boolean, file: Session.class
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
    @Override // android.view.IWindowSession
    public boolean performDrag(android.view.IWindow r1, android.os.IBinder r2, float r3, float r4, float r5, float r6, android.content.ClipData r7) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.Session.performDrag(android.view.IWindow, android.os.IBinder, float, float, float, float, android.content.ClipData):boolean, file: Session.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.Session.performDrag(android.view.IWindow, android.os.IBinder, float, float, float, float, android.content.ClipData):boolean");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.Session.reportDropResult(android.view.IWindow, boolean):void, file: Session.class
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
    @Override // android.view.IWindowSession
    public void reportDropResult(android.view.IWindow r1, boolean r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.Session.reportDropResult(android.view.IWindow, boolean):void, file: Session.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.Session.reportDropResult(android.view.IWindow, boolean):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.Session.setWallpaperPosition(android.os.IBinder, float, float, float, float):void, file: Session.class
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
    @Override // android.view.IWindowSession
    public void setWallpaperPosition(android.os.IBinder r1, float r2, float r3, float r4, float r5) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.Session.setWallpaperPosition(android.os.IBinder, float, float, float, float):void, file: Session.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.Session.setWallpaperPosition(android.os.IBinder, float, float, float, float):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.Session.sendWallpaperCommand(android.os.IBinder, java.lang.String, int, int, int, android.os.Bundle, boolean):android.os.Bundle, file: Session.class
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
    @Override // android.view.IWindowSession
    public android.os.Bundle sendWallpaperCommand(android.os.IBinder r1, java.lang.String r2, int r3, int r4, int r5, android.os.Bundle r6, boolean r7) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.Session.sendWallpaperCommand(android.os.IBinder, java.lang.String, int, int, int, android.os.Bundle, boolean):android.os.Bundle, file: Session.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.Session.sendWallpaperCommand(android.os.IBinder, java.lang.String, int, int, int, android.os.Bundle, boolean):android.os.Bundle");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.Session.setUniverseTransform(android.os.IBinder, float, float, float, float, float, float, float):void, file: Session.class
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
    @Override // android.view.IWindowSession
    public void setUniverseTransform(android.os.IBinder r1, float r2, float r3, float r4, float r5, float r6, float r7, float r8) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.Session.setUniverseTransform(android.os.IBinder, float, float, float, float, float, float, float):void, file: Session.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.Session.setUniverseTransform(android.os.IBinder, float, float, float, float, float, float, float):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.Session.onRectangleOnScreenRequested(android.os.IBinder, android.graphics.Rect, boolean):void, file: Session.class
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
    @Override // android.view.IWindowSession
    public void onRectangleOnScreenRequested(android.os.IBinder r1, android.graphics.Rect r2, boolean r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.Session.onRectangleOnScreenRequested(android.os.IBinder, android.graphics.Rect, boolean):void, file: Session.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.Session.onRectangleOnScreenRequested(android.os.IBinder, android.graphics.Rect, boolean):void");
    }

    @Override // android.view.IWindowSession.Stub, android.os.Binder
    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        try {
            return super.onTransact(code, data, reply, flags);
        } catch (RuntimeException e) {
            if (!(e instanceof SecurityException)) {
                Slog.wtf("WindowManager", "Window Session Crash", e);
            }
            throw e;
        }
    }

    @Override // android.os.IBinder.DeathRecipient
    public void binderDied() {
        try {
            if (this.mService.mInputMethodManager != null) {
                this.mService.mInputMethodManager.removeClient(this.mClient);
            }
        } catch (RemoteException e) {
        }
        synchronized (this.mService.mWindowMap) {
            this.mClient.asBinder().unlinkToDeath(this, 0);
            this.mClientDead = true;
            killSessionLocked();
        }
    }

    @Override // android.view.IWindowSession
    public int add(IWindow window, int seq, WindowManager.LayoutParams attrs, int viewVisibility, Rect outContentInsets, InputChannel outInputChannel) {
        return addToDisplay(window, seq, attrs, viewVisibility, 0, outContentInsets, outInputChannel);
    }

    @Override // android.view.IWindowSession
    public int addToDisplay(IWindow window, int seq, WindowManager.LayoutParams attrs, int viewVisibility, int displayId, Rect outContentInsets, InputChannel outInputChannel) {
        return this.mService.addWindow(this, window, seq, attrs, viewVisibility, displayId, outContentInsets, outInputChannel);
    }

    @Override // android.view.IWindowSession
    public int addWithoutInputChannel(IWindow window, int seq, WindowManager.LayoutParams attrs, int viewVisibility, Rect outContentInsets) {
        return addToDisplayWithoutInputChannel(window, seq, attrs, viewVisibility, 0, outContentInsets);
    }

    @Override // android.view.IWindowSession
    public int addToDisplayWithoutInputChannel(IWindow window, int seq, WindowManager.LayoutParams attrs, int viewVisibility, int displayId, Rect outContentInsets) {
        return this.mService.addWindow(this, window, seq, attrs, viewVisibility, displayId, outContentInsets, null);
    }

    @Override // android.view.IWindowSession
    public void remove(IWindow window) {
        this.mService.removeWindow(this, window);
    }

    @Override // android.view.IWindowSession
    public int relayout(IWindow window, int seq, WindowManager.LayoutParams attrs, int requestedWidth, int requestedHeight, int viewFlags, int flags, Rect outFrame, Rect outOverscanInsets, Rect outContentInsets, Rect outVisibleInsets, Configuration outConfig, Surface outSurface) {
        int res = this.mService.relayoutWindow(this, window, seq, attrs, requestedWidth, requestedHeight, viewFlags, flags, outFrame, outOverscanInsets, outContentInsets, outVisibleInsets, outConfig, outSurface);
        return res;
    }

    @Override // android.view.IWindowSession
    public void performDeferredDestroy(IWindow window) {
        this.mService.performDeferredDestroyWindow(this, window);
    }

    @Override // android.view.IWindowSession
    public boolean outOfMemory(IWindow window) {
        return this.mService.outOfMemoryWindow(this, window);
    }

    @Override // android.view.IWindowSession
    public void setTransparentRegion(IWindow window, Region region) {
        this.mService.setTransparentRegionWindow(this, window, region);
    }

    @Override // android.view.IWindowSession
    public void setInsets(IWindow window, int touchableInsets, Rect contentInsets, Rect visibleInsets, Region touchableArea) {
        this.mService.setInsetsWindow(this, window, touchableInsets, contentInsets, visibleInsets, touchableArea);
    }

    @Override // android.view.IWindowSession
    public void getDisplayFrame(IWindow window, Rect outDisplayFrame) {
        this.mService.getWindowDisplayFrame(this, window, outDisplayFrame);
    }

    @Override // android.view.IWindowSession
    public void finishDrawing(IWindow window) {
        this.mService.finishDrawingWindow(this, window);
    }

    @Override // android.view.IWindowSession
    public void setInTouchMode(boolean mode) {
        synchronized (this.mService.mWindowMap) {
            this.mService.mInTouchMode = mode;
        }
    }

    @Override // android.view.IWindowSession
    public boolean getInTouchMode() {
        boolean z;
        synchronized (this.mService.mWindowMap) {
            z = this.mService.mInTouchMode;
        }
        return z;
    }

    @Override // android.view.IWindowSession
    public IBinder prepareDrag(IWindow window, int flags, int width, int height, Surface outSurface) {
        return this.mService.prepareDragSurface(window, this.mSurfaceSession, flags, width, height, outSurface);
    }

    @Override // android.view.IWindowSession
    public void dragRecipientEntered(IWindow window) {
    }

    @Override // android.view.IWindowSession
    public void dragRecipientExited(IWindow window) {
    }

    @Override // android.view.IWindowSession
    public void wallpaperOffsetsComplete(IBinder window) {
        this.mService.wallpaperOffsetsComplete(window);
    }

    @Override // android.view.IWindowSession
    public void wallpaperCommandComplete(IBinder window, Bundle result) {
        this.mService.wallpaperCommandComplete(window, result);
    }

    @Override // android.view.IWindowSession
    public IWindowId getWindowId(IBinder window) {
        return this.mService.getWindowId(window);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void windowAddedLocked() {
        if (this.mSurfaceSession == null) {
            this.mSurfaceSession = new SurfaceSession();
            this.mService.mSessions.add(this);
        }
        this.mNumWindow++;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void windowRemovedLocked() {
        this.mNumWindow--;
        killSessionLocked();
    }

    void killSessionLocked() {
        if (this.mNumWindow <= 0 && this.mClientDead) {
            this.mService.mSessions.remove(this);
            if (this.mSurfaceSession != null) {
                try {
                    this.mSurfaceSession.kill();
                } catch (Exception e) {
                    Slog.w("WindowManager", "Exception thrown when killing surface session " + this.mSurfaceSession + " in session " + this + ": " + e.toString());
                }
                this.mSurfaceSession = null;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dump(PrintWriter pw, String prefix) {
        pw.print(prefix);
        pw.print("mNumWindow=");
        pw.print(this.mNumWindow);
        pw.print(" mClientDead=");
        pw.print(this.mClientDead);
        pw.print(" mSurfaceSession=");
        pw.println(this.mSurfaceSession);
    }

    public String toString() {
        return this.mStringName;
    }
}