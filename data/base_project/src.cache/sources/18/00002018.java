package com.android.server.wm;

import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Point;
import android.graphics.Region;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.util.Slog;
import android.view.Display;
import android.view.DragEvent;
import android.view.InputChannel;
import android.view.SurfaceControl;
import android.view.WindowManager;
import com.android.server.input.InputApplicationHandle;
import com.android.server.input.InputWindowHandle;
import com.android.server.wm.WindowManagerService;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: DragState.class */
public class DragState {
    final WindowManagerService mService;
    IBinder mToken;
    SurfaceControl mSurfaceControl;
    int mFlags;
    IBinder mLocalWin;
    ClipData mData;
    ClipDescription mDataDescription;
    boolean mDragResult;
    float mCurrentX;
    float mCurrentY;
    float mThumbOffsetX;
    float mThumbOffsetY;
    InputChannel mServerChannel;
    InputChannel mClientChannel;
    WindowManagerService.DragInputEventReceiver mInputEventReceiver;
    InputApplicationHandle mDragApplicationHandle;
    InputWindowHandle mDragWindowHandle;
    WindowState mTargetWindow;
    boolean mDragInProgress;
    Display mDisplay;
    private final Region mTmpRegion = new Region();
    ArrayList<WindowState> mNotifiedWindows = new ArrayList<>();

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.DragState.sendDragStartedLw(com.android.server.wm.WindowState, float, float, android.content.ClipDescription):void, file: DragState.class
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
    private void sendDragStartedLw(com.android.server.wm.WindowState r1, float r2, float r3, android.content.ClipDescription r4) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.DragState.sendDragStartedLw(com.android.server.wm.WindowState, float, float, android.content.ClipDescription):void, file: DragState.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.DragState.sendDragStartedLw(com.android.server.wm.WindowState, float, float, android.content.ClipDescription):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.DragState.notifyMoveLw(float, float):void, file: DragState.class
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
    void notifyMoveLw(float r1, float r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.DragState.notifyMoveLw(float, float):void, file: DragState.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.DragState.notifyMoveLw(float, float):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.DragState.notifyDropLw(float, float):boolean, file: DragState.class
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
    boolean notifyDropLw(float r1, float r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.DragState.notifyDropLw(float, float):boolean, file: DragState.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.DragState.notifyDropLw(float, float):boolean");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DragState(WindowManagerService service, IBinder token, SurfaceControl surface, int flags, IBinder localWin) {
        this.mService = service;
        this.mToken = token;
        this.mSurfaceControl = surface;
        this.mFlags = flags;
        this.mLocalWin = localWin;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void reset() {
        if (this.mSurfaceControl != null) {
            this.mSurfaceControl.destroy();
        }
        this.mSurfaceControl = null;
        this.mFlags = 0;
        this.mLocalWin = null;
        this.mToken = null;
        this.mData = null;
        this.mThumbOffsetY = 0.0f;
        this.mThumbOffsetX = 0.0f;
        this.mNotifiedWindows = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void register(Display display) {
        this.mDisplay = display;
        if (this.mClientChannel != null) {
            Slog.e("WindowManager", "Duplicate register of drag input channel");
            return;
        }
        InputChannel[] channels = InputChannel.openInputChannelPair("drag");
        this.mServerChannel = channels[0];
        this.mClientChannel = channels[1];
        this.mService.mInputManager.registerInputChannel(this.mServerChannel, null);
        WindowManagerService windowManagerService = this.mService;
        windowManagerService.getClass();
        this.mInputEventReceiver = new WindowManagerService.DragInputEventReceiver(this.mClientChannel, this.mService.mH.getLooper());
        this.mDragApplicationHandle = new InputApplicationHandle(null);
        this.mDragApplicationHandle.name = "drag";
        this.mDragApplicationHandle.dispatchingTimeoutNanos = 5000000000L;
        this.mDragWindowHandle = new InputWindowHandle(this.mDragApplicationHandle, null, this.mDisplay.getDisplayId());
        this.mDragWindowHandle.name = "drag";
        this.mDragWindowHandle.inputChannel = this.mServerChannel;
        this.mDragWindowHandle.layer = getDragLayerLw();
        this.mDragWindowHandle.layoutParamsFlags = 0;
        this.mDragWindowHandle.layoutParamsPrivateFlags = 0;
        this.mDragWindowHandle.layoutParamsType = WindowManager.LayoutParams.TYPE_DRAG;
        this.mDragWindowHandle.dispatchingTimeoutNanos = 5000000000L;
        this.mDragWindowHandle.visible = true;
        this.mDragWindowHandle.canReceiveKeys = false;
        this.mDragWindowHandle.hasFocus = true;
        this.mDragWindowHandle.hasWallpaper = false;
        this.mDragWindowHandle.paused = false;
        this.mDragWindowHandle.ownerPid = Process.myPid();
        this.mDragWindowHandle.ownerUid = Process.myUid();
        this.mDragWindowHandle.inputFeatures = 0;
        this.mDragWindowHandle.scaleFactor = 1.0f;
        this.mDragWindowHandle.touchableRegion.setEmpty();
        this.mDragWindowHandle.frameLeft = 0;
        this.mDragWindowHandle.frameTop = 0;
        Point p = new Point();
        this.mDisplay.getRealSize(p);
        this.mDragWindowHandle.frameRight = p.x;
        this.mDragWindowHandle.frameBottom = p.y;
        this.mService.pauseRotationLocked();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void unregister() {
        if (this.mClientChannel == null) {
            Slog.e("WindowManager", "Unregister of nonexistent drag input channel");
            return;
        }
        this.mService.mInputManager.unregisterInputChannel(this.mServerChannel);
        this.mInputEventReceiver.dispose();
        this.mInputEventReceiver = null;
        this.mClientChannel.dispose();
        this.mServerChannel.dispose();
        this.mClientChannel = null;
        this.mServerChannel = null;
        this.mDragWindowHandle = null;
        this.mDragApplicationHandle = null;
        this.mService.resumeRotationLocked();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getDragLayerLw() {
        return (this.mService.mPolicy.windowTypeToLayerLw(WindowManager.LayoutParams.TYPE_DRAG) * 10000) + 1000;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void broadcastDragStartedLw(float touchX, float touchY) {
        this.mDataDescription = this.mData != null ? this.mData.getDescription() : null;
        this.mNotifiedWindows.clear();
        this.mDragInProgress = true;
        WindowList windows = this.mService.getWindowListLocked(this.mDisplay);
        if (windows != null) {
            int N = windows.size();
            for (int i = 0; i < N; i++) {
                sendDragStartedLw(windows.get(i), touchX, touchY, this.mDataDescription);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void sendDragStartedIfNeededLw(WindowState newWin) {
        if (this.mDragInProgress) {
            Iterator i$ = this.mNotifiedWindows.iterator();
            while (i$.hasNext()) {
                WindowState ws = i$.next();
                if (ws == newWin) {
                    return;
                }
            }
            sendDragStartedLw(newWin, this.mCurrentX, this.mCurrentY, this.mDataDescription);
        }
    }

    void broadcastDragEndedLw() {
        DragEvent evt = DragEvent.obtain(4, 0.0f, 0.0f, null, null, null, this.mDragResult);
        Iterator i$ = this.mNotifiedWindows.iterator();
        while (i$.hasNext()) {
            WindowState ws = i$.next();
            try {
                ws.mClient.dispatchDragEvent(evt);
            } catch (RemoteException e) {
                Slog.w("WindowManager", "Unable to drag-end window " + ws);
            }
        }
        this.mNotifiedWindows.clear();
        this.mDragInProgress = false;
        evt.recycle();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void endDragLw() {
        this.mService.mDragState.broadcastDragEndedLw();
        this.mService.mDragState.unregister();
        this.mService.mInputMonitor.updateInputWindowsLw(true);
        this.mService.mDragState.reset();
        this.mService.mDragState = null;
    }

    private WindowState getTouchedWinAtPointLw(float xf, float yf) {
        WindowState touchedWin = null;
        int x = (int) xf;
        int y = (int) yf;
        WindowList windows = this.mService.getWindowListLocked(this.mDisplay);
        if (windows == null) {
            return null;
        }
        int N = windows.size();
        for (int i = N - 1; i >= 0; i--) {
            WindowState child = windows.get(i);
            int flags = child.mAttrs.flags;
            if (child.isVisibleLw() && (flags & 16) == 0) {
                child.getTouchableRegion(this.mTmpRegion);
                int touchFlags = flags & 40;
                if (this.mTmpRegion.contains(x, y) || touchFlags == 0) {
                    touchedWin = child;
                    break;
                }
            }
        }
        return touchedWin;
    }

    private static DragEvent obtainDragEvent(WindowState win, int action, float x, float y, Object localState, ClipDescription description, ClipData data, boolean result) {
        float winX = x - win.mFrame.left;
        float winY = y - win.mFrame.top;
        if (win.mEnforceSizeCompat) {
            winX *= win.mGlobalScale;
            winY *= win.mGlobalScale;
        }
        return DragEvent.obtain(action, winX, winY, localState, description, data, result);
    }
}