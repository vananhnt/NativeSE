package com.android.server.display;

import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemProperties;
import android.util.SparseArray;
import android.view.DisplayEventReceiver;
import android.view.SurfaceControl;
import com.android.internal.R;
import com.android.server.display.DisplayAdapter;
import com.android.server.display.DisplayManagerService;
import java.io.PrintWriter;

/* loaded from: LocalDisplayAdapter.class */
final class LocalDisplayAdapter extends DisplayAdapter {
    private static final String TAG = "LocalDisplayAdapter";
    private static final int[] BUILT_IN_DISPLAY_IDS_TO_SCAN = {0, 1};
    private final SparseArray<LocalDisplayDevice> mDevices;
    private HotplugDisplayEventReceiver mHotplugReceiver;
    private final SurfaceControl.PhysicalDisplayInfo mTempPhys;

    public LocalDisplayAdapter(DisplayManagerService.SyncRoot syncRoot, Context context, Handler handler, DisplayAdapter.Listener listener) {
        super(syncRoot, context, handler, listener, TAG);
        this.mDevices = new SparseArray<>();
        this.mTempPhys = new SurfaceControl.PhysicalDisplayInfo();
    }

    @Override // com.android.server.display.DisplayAdapter
    public void registerLocked() {
        super.registerLocked();
        this.mHotplugReceiver = new HotplugDisplayEventReceiver(getHandler().getLooper());
        int[] arr$ = BUILT_IN_DISPLAY_IDS_TO_SCAN;
        for (int builtInDisplayId : arr$) {
            tryConnectDisplayLocked(builtInDisplayId);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void tryConnectDisplayLocked(int builtInDisplayId) {
        IBinder displayToken = SurfaceControl.getBuiltInDisplay(builtInDisplayId);
        if (displayToken != null && SurfaceControl.getDisplayInfo(displayToken, this.mTempPhys)) {
            LocalDisplayDevice device = this.mDevices.get(builtInDisplayId);
            if (device == null) {
                LocalDisplayDevice device2 = new LocalDisplayDevice(displayToken, builtInDisplayId, this.mTempPhys);
                this.mDevices.put(builtInDisplayId, device2);
                sendDisplayDeviceEventLocked(device2, 1);
            } else if (device.updatePhysicalDisplayInfoLocked(this.mTempPhys)) {
                sendDisplayDeviceEventLocked(device, 2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void tryDisconnectDisplayLocked(int builtInDisplayId) {
        LocalDisplayDevice device = this.mDevices.get(builtInDisplayId);
        if (device != null) {
            this.mDevices.remove(builtInDisplayId);
            sendDisplayDeviceEventLocked(device, 3);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: LocalDisplayAdapter$LocalDisplayDevice.class */
    public final class LocalDisplayDevice extends DisplayDevice {
        private final int mBuiltInDisplayId;
        private final SurfaceControl.PhysicalDisplayInfo mPhys;
        private DisplayDeviceInfo mInfo;
        private boolean mHavePendingChanges;
        private boolean mBlanked;

        public LocalDisplayDevice(IBinder displayToken, int builtInDisplayId, SurfaceControl.PhysicalDisplayInfo phys) {
            super(LocalDisplayAdapter.this, displayToken);
            this.mBuiltInDisplayId = builtInDisplayId;
            this.mPhys = new SurfaceControl.PhysicalDisplayInfo(phys);
        }

        public boolean updatePhysicalDisplayInfoLocked(SurfaceControl.PhysicalDisplayInfo phys) {
            if (!this.mPhys.equals(phys)) {
                this.mPhys.copyFrom(phys);
                this.mHavePendingChanges = true;
                return true;
            }
            return false;
        }

        @Override // com.android.server.display.DisplayDevice
        public void applyPendingDisplayDeviceInfoChangesLocked() {
            if (this.mHavePendingChanges) {
                this.mInfo = null;
                this.mHavePendingChanges = false;
            }
        }

        @Override // com.android.server.display.DisplayDevice
        public DisplayDeviceInfo getDisplayDeviceInfoLocked() {
            if (this.mInfo == null) {
                this.mInfo = new DisplayDeviceInfo();
                this.mInfo.width = this.mPhys.width;
                this.mInfo.height = this.mPhys.height;
                this.mInfo.refreshRate = this.mPhys.refreshRate;
                if (this.mPhys.secure) {
                    this.mInfo.flags = 12;
                }
                if (this.mBuiltInDisplayId == 0) {
                    this.mInfo.name = LocalDisplayAdapter.this.getContext().getResources().getString(R.string.display_manager_built_in_display_name);
                    this.mInfo.flags |= 3;
                    this.mInfo.type = 1;
                    this.mInfo.densityDpi = (int) ((this.mPhys.density * 160.0f) + 0.5f);
                    this.mInfo.xDpi = this.mPhys.xDpi;
                    this.mInfo.yDpi = this.mPhys.yDpi;
                    this.mInfo.touch = 1;
                } else {
                    this.mInfo.type = 2;
                    this.mInfo.flags |= 64;
                    this.mInfo.name = LocalDisplayAdapter.this.getContext().getResources().getString(R.string.display_manager_hdmi_display_name);
                    this.mInfo.touch = 2;
                    this.mInfo.setAssumedDensityForExternalDisplay(this.mPhys.width, this.mPhys.height);
                    if (Camera.Parameters.SCENE_MODE_PORTRAIT.equals(SystemProperties.get("persist.demo.hdmirotation"))) {
                        this.mInfo.rotation = 3;
                    }
                }
            }
            return this.mInfo;
        }

        @Override // com.android.server.display.DisplayDevice
        public void blankLocked() {
            this.mBlanked = true;
            SurfaceControl.blankDisplay(getDisplayTokenLocked());
        }

        @Override // com.android.server.display.DisplayDevice
        public void unblankLocked() {
            this.mBlanked = false;
            SurfaceControl.unblankDisplay(getDisplayTokenLocked());
        }

        @Override // com.android.server.display.DisplayDevice
        public void dumpLocked(PrintWriter pw) {
            super.dumpLocked(pw);
            pw.println("mBuiltInDisplayId=" + this.mBuiltInDisplayId);
            pw.println("mPhys=" + this.mPhys);
            pw.println("mBlanked=" + this.mBlanked);
        }
    }

    /* loaded from: LocalDisplayAdapter$HotplugDisplayEventReceiver.class */
    private final class HotplugDisplayEventReceiver extends DisplayEventReceiver {
        public HotplugDisplayEventReceiver(Looper looper) {
            super(looper);
        }

        @Override // android.view.DisplayEventReceiver
        public void onHotplug(long timestampNanos, int builtInDisplayId, boolean connected) {
            synchronized (LocalDisplayAdapter.this.getSyncRoot()) {
                if (connected) {
                    LocalDisplayAdapter.this.tryConnectDisplayLocked(builtInDisplayId);
                } else {
                    LocalDisplayAdapter.this.tryDisconnectDisplayLocked(builtInDisplayId);
                }
            }
        }
    }
}