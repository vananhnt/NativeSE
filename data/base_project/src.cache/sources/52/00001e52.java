package com.android.server.display;

import android.content.Context;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.ArrayMap;
import android.util.Slog;
import android.view.Surface;
import android.view.SurfaceControl;
import com.android.server.display.DisplayAdapter;
import com.android.server.display.DisplayManagerService;

/* loaded from: VirtualDisplayAdapter.class */
final class VirtualDisplayAdapter extends DisplayAdapter {
    static final String TAG = "VirtualDisplayAdapter";
    static final boolean DEBUG = false;
    private final ArrayMap<IBinder, VirtualDisplayDevice> mVirtualDisplayDevices;

    public VirtualDisplayAdapter(DisplayManagerService.SyncRoot syncRoot, Context context, Handler handler, DisplayAdapter.Listener listener) {
        super(syncRoot, context, handler, listener, TAG);
        this.mVirtualDisplayDevices = new ArrayMap<>();
    }

    public DisplayDevice createVirtualDisplayLocked(IBinder appToken, int ownerUid, String ownerPackageName, String name, int width, int height, int densityDpi, Surface surface, int flags) {
        boolean secure = (flags & 4) != 0;
        IBinder displayToken = SurfaceControl.createDisplay(name, secure);
        VirtualDisplayDevice device = new VirtualDisplayDevice(displayToken, appToken, ownerUid, ownerPackageName, name, width, height, densityDpi, surface, flags);
        try {
            appToken.linkToDeath(device, 0);
            this.mVirtualDisplayDevices.put(appToken, device);
            return device;
        } catch (RemoteException e) {
            device.destroyLocked();
            return null;
        }
    }

    public DisplayDevice releaseVirtualDisplayLocked(IBinder appToken) {
        VirtualDisplayDevice device = this.mVirtualDisplayDevices.remove(appToken);
        if (device != null) {
            device.destroyLocked();
            appToken.unlinkToDeath(device, 0);
        }
        return device;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleBinderDiedLocked(IBinder appToken) {
        VirtualDisplayDevice device = this.mVirtualDisplayDevices.remove(appToken);
        if (device != null) {
            Slog.i(TAG, "Virtual display device released because application token died: " + device.mOwnerPackageName);
            device.destroyLocked();
            sendDisplayDeviceEventLocked(device, 3);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: VirtualDisplayAdapter$VirtualDisplayDevice.class */
    public final class VirtualDisplayDevice extends DisplayDevice implements IBinder.DeathRecipient {
        private final IBinder mAppToken;
        private final int mOwnerUid;
        final String mOwnerPackageName;
        private final String mName;
        private final int mWidth;
        private final int mHeight;
        private final int mDensityDpi;
        private final int mFlags;
        private Surface mSurface;
        private DisplayDeviceInfo mInfo;

        public VirtualDisplayDevice(IBinder displayToken, IBinder appToken, int ownerUid, String ownerPackageName, String name, int width, int height, int densityDpi, Surface surface, int flags) {
            super(VirtualDisplayAdapter.this, displayToken);
            this.mAppToken = appToken;
            this.mOwnerUid = ownerUid;
            this.mOwnerPackageName = ownerPackageName;
            this.mName = name;
            this.mWidth = width;
            this.mHeight = height;
            this.mDensityDpi = densityDpi;
            this.mSurface = surface;
            this.mFlags = flags;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            synchronized (VirtualDisplayAdapter.this.getSyncRoot()) {
                if (this.mSurface != null) {
                    VirtualDisplayAdapter.this.handleBinderDiedLocked(this.mAppToken);
                }
            }
        }

        public void destroyLocked() {
            if (this.mSurface != null) {
                this.mSurface.release();
                this.mSurface = null;
            }
            SurfaceControl.destroyDisplay(getDisplayTokenLocked());
        }

        @Override // com.android.server.display.DisplayDevice
        public void performTraversalInTransactionLocked() {
            if (this.mSurface != null) {
                setSurfaceInTransactionLocked(this.mSurface);
            }
        }

        @Override // com.android.server.display.DisplayDevice
        public DisplayDeviceInfo getDisplayDeviceInfoLocked() {
            if (this.mInfo == null) {
                this.mInfo = new DisplayDeviceInfo();
                this.mInfo.name = this.mName;
                this.mInfo.width = this.mWidth;
                this.mInfo.height = this.mHeight;
                this.mInfo.refreshRate = 60.0f;
                this.mInfo.densityDpi = this.mDensityDpi;
                this.mInfo.xDpi = this.mDensityDpi;
                this.mInfo.yDpi = this.mDensityDpi;
                this.mInfo.flags = 0;
                if ((this.mFlags & 1) == 0) {
                    this.mInfo.flags |= 48;
                }
                if ((this.mFlags & 4) != 0) {
                    this.mInfo.flags |= 4;
                }
                if ((this.mFlags & 2) != 0) {
                    this.mInfo.flags |= 64;
                }
                this.mInfo.type = 5;
                this.mInfo.touch = 0;
                this.mInfo.ownerUid = this.mOwnerUid;
                this.mInfo.ownerPackageName = this.mOwnerPackageName;
            }
            return this.mInfo;
        }
    }
}