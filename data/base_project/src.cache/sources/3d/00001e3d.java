package com.android.server.display;

import android.content.Context;
import android.os.Handler;
import android.util.DisplayMetrics;
import com.android.internal.R;
import com.android.server.display.DisplayAdapter;
import com.android.server.display.DisplayManagerService;

/* loaded from: HeadlessDisplayAdapter.class */
final class HeadlessDisplayAdapter extends DisplayAdapter {
    private static final String TAG = "HeadlessDisplayAdapter";

    public HeadlessDisplayAdapter(DisplayManagerService.SyncRoot syncRoot, Context context, Handler handler, DisplayAdapter.Listener listener) {
        super(syncRoot, context, handler, listener, TAG);
    }

    @Override // com.android.server.display.DisplayAdapter
    public void registerLocked() {
        super.registerLocked();
        sendDisplayDeviceEventLocked(new HeadlessDisplayDevice(), 1);
    }

    /* loaded from: HeadlessDisplayAdapter$HeadlessDisplayDevice.class */
    private final class HeadlessDisplayDevice extends DisplayDevice {
        private DisplayDeviceInfo mInfo;

        public HeadlessDisplayDevice() {
            super(HeadlessDisplayAdapter.this, null);
        }

        @Override // com.android.server.display.DisplayDevice
        public DisplayDeviceInfo getDisplayDeviceInfoLocked() {
            if (this.mInfo == null) {
                this.mInfo = new DisplayDeviceInfo();
                this.mInfo.name = HeadlessDisplayAdapter.this.getContext().getResources().getString(R.string.display_manager_built_in_display_name);
                this.mInfo.width = DisplayMetrics.DENSITY_XXXHIGH;
                this.mInfo.height = 480;
                this.mInfo.refreshRate = 60.0f;
                this.mInfo.densityDpi = 160;
                this.mInfo.xDpi = 160.0f;
                this.mInfo.yDpi = 160.0f;
                this.mInfo.flags = 13;
                this.mInfo.type = 1;
                this.mInfo.touch = 0;
            }
            return this.mInfo;
        }
    }
}