package com.android.server.display;

import android.content.Context;
import android.database.ContentObserver;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Slog;
import android.view.Surface;
import android.view.SurfaceControl;
import com.android.internal.R;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.display.DisplayAdapter;
import com.android.server.display.DisplayManagerService;
import com.android.server.display.OverlayDisplayWindow;
import gov.nist.core.Separators;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: OverlayDisplayAdapter.class */
public final class OverlayDisplayAdapter extends DisplayAdapter {
    static final String TAG = "OverlayDisplayAdapter";
    static final boolean DEBUG = false;
    private static final int MIN_WIDTH = 100;
    private static final int MIN_HEIGHT = 100;
    private static final int MAX_WIDTH = 4096;
    private static final int MAX_HEIGHT = 4096;
    private static final Pattern SETTING_PATTERN = Pattern.compile("(\\d+)x(\\d+)/(\\d+)(,[a-z]+)*");
    private final Handler mUiHandler;
    private final ArrayList<OverlayDisplayHandle> mOverlays;
    private String mCurrentOverlaySetting;

    public OverlayDisplayAdapter(DisplayManagerService.SyncRoot syncRoot, Context context, Handler handler, DisplayAdapter.Listener listener, Handler uiHandler) {
        super(syncRoot, context, handler, listener, TAG);
        this.mOverlays = new ArrayList<>();
        this.mCurrentOverlaySetting = "";
        this.mUiHandler = uiHandler;
    }

    @Override // com.android.server.display.DisplayAdapter
    public void dumpLocked(PrintWriter pw) {
        super.dumpLocked(pw);
        pw.println("mCurrentOverlaySetting=" + this.mCurrentOverlaySetting);
        pw.println("mOverlays: size=" + this.mOverlays.size());
        Iterator i$ = this.mOverlays.iterator();
        while (i$.hasNext()) {
            OverlayDisplayHandle overlay = i$.next();
            overlay.dumpLocked(pw);
        }
    }

    @Override // com.android.server.display.DisplayAdapter
    public void registerLocked() {
        super.registerLocked();
        getHandler().post(new Runnable() { // from class: com.android.server.display.OverlayDisplayAdapter.1
            @Override // java.lang.Runnable
            public void run() {
                OverlayDisplayAdapter.this.getContext().getContentResolver().registerContentObserver(Settings.Global.getUriFor(Settings.Global.OVERLAY_DISPLAY_DEVICES), true, new ContentObserver(OverlayDisplayAdapter.this.getHandler()) { // from class: com.android.server.display.OverlayDisplayAdapter.1.1
                    @Override // android.database.ContentObserver
                    public void onChange(boolean selfChange) {
                        OverlayDisplayAdapter.this.updateOverlayDisplayDevices();
                    }
                });
                OverlayDisplayAdapter.this.updateOverlayDisplayDevices();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateOverlayDisplayDevices() {
        synchronized (getSyncRoot()) {
            updateOverlayDisplayDevicesLocked();
        }
    }

    private void updateOverlayDisplayDevicesLocked() {
        String value = Settings.Global.getString(getContext().getContentResolver(), Settings.Global.OVERLAY_DISPLAY_DEVICES);
        if (value == null) {
            value = "";
        }
        if (value.equals(this.mCurrentOverlaySetting)) {
            return;
        }
        this.mCurrentOverlaySetting = value;
        if (!this.mOverlays.isEmpty()) {
            Slog.i(TAG, "Dismissing all overlay display devices.");
            Iterator i$ = this.mOverlays.iterator();
            while (i$.hasNext()) {
                OverlayDisplayHandle overlay = i$.next();
                overlay.dismissLocked();
            }
            this.mOverlays.clear();
        }
        int count = 0;
        String[] arr$ = value.split(Separators.SEMICOLON);
        for (String part : arr$) {
            Matcher matcher = SETTING_PATTERN.matcher(part);
            if (matcher.matches()) {
                if (count >= 4) {
                    Slog.w(TAG, "Too many overlay display devices specified: " + value);
                    return;
                }
                try {
                    int width = Integer.parseInt(matcher.group(1), 10);
                    int height = Integer.parseInt(matcher.group(2), 10);
                    int densityDpi = Integer.parseInt(matcher.group(3), 10);
                    String flagString = matcher.group(4);
                    if (width >= 100 && width <= 4096 && height >= 100 && height <= 4096 && densityDpi >= 120 && densityDpi <= 480) {
                        count++;
                        String name = getContext().getResources().getString(R.string.display_manager_overlay_display_name, Integer.valueOf(count));
                        int gravity = chooseOverlayGravity(count);
                        boolean secure = flagString != null && flagString.contains(",secure");
                        Slog.i(TAG, "Showing overlay display device #" + count + ": name=" + name + ", width=" + width + ", height=" + height + ", densityDpi=" + densityDpi + ", secure=" + secure);
                        this.mOverlays.add(new OverlayDisplayHandle(name, width, height, densityDpi, gravity, secure));
                    }
                } catch (NumberFormatException e) {
                }
                Slog.w(TAG, "Malformed overlay display devices setting: " + value);
            } else {
                if (part.isEmpty()) {
                }
                Slog.w(TAG, "Malformed overlay display devices setting: " + value);
            }
        }
    }

    private static int chooseOverlayGravity(int overlayNumber) {
        switch (overlayNumber) {
            case 1:
                return 51;
            case 2:
                return 85;
            case 3:
                return 53;
            case 4:
            default:
                return 83;
        }
    }

    /* loaded from: OverlayDisplayAdapter$OverlayDisplayDevice.class */
    private final class OverlayDisplayDevice extends DisplayDevice {
        private final String mName;
        private final int mWidth;
        private final int mHeight;
        private final float mRefreshRate;
        private final int mDensityDpi;
        private final boolean mSecure;
        private Surface mSurface;
        private SurfaceTexture mSurfaceTexture;
        private DisplayDeviceInfo mInfo;

        public OverlayDisplayDevice(IBinder displayToken, String name, int width, int height, float refreshRate, int densityDpi, boolean secure, SurfaceTexture surfaceTexture) {
            super(OverlayDisplayAdapter.this, displayToken);
            this.mName = name;
            this.mWidth = width;
            this.mHeight = height;
            this.mRefreshRate = refreshRate;
            this.mDensityDpi = densityDpi;
            this.mSecure = secure;
            this.mSurfaceTexture = surfaceTexture;
        }

        public void destroyLocked() {
            this.mSurfaceTexture = null;
            if (this.mSurface != null) {
                this.mSurface.release();
                this.mSurface = null;
            }
            SurfaceControl.destroyDisplay(getDisplayTokenLocked());
        }

        @Override // com.android.server.display.DisplayDevice
        public void performTraversalInTransactionLocked() {
            if (this.mSurfaceTexture != null) {
                if (this.mSurface == null) {
                    this.mSurface = new Surface(this.mSurfaceTexture);
                }
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
                this.mInfo.refreshRate = this.mRefreshRate;
                this.mInfo.densityDpi = this.mDensityDpi;
                this.mInfo.xDpi = this.mDensityDpi;
                this.mInfo.yDpi = this.mDensityDpi;
                this.mInfo.flags = 64;
                if (this.mSecure) {
                    this.mInfo.flags |= 4;
                }
                this.mInfo.type = 4;
                this.mInfo.touch = 0;
            }
            return this.mInfo;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: OverlayDisplayAdapter$OverlayDisplayHandle.class */
    public final class OverlayDisplayHandle implements OverlayDisplayWindow.Listener {
        private final String mName;
        private final int mWidth;
        private final int mHeight;
        private final int mDensityDpi;
        private final int mGravity;
        private final boolean mSecure;
        private OverlayDisplayWindow mWindow;
        private OverlayDisplayDevice mDevice;
        private final Runnable mShowRunnable = new Runnable() { // from class: com.android.server.display.OverlayDisplayAdapter.OverlayDisplayHandle.1
            @Override // java.lang.Runnable
            public void run() {
                OverlayDisplayWindow window = new OverlayDisplayWindow(OverlayDisplayAdapter.this.getContext(), OverlayDisplayHandle.this.mName, OverlayDisplayHandle.this.mWidth, OverlayDisplayHandle.this.mHeight, OverlayDisplayHandle.this.mDensityDpi, OverlayDisplayHandle.this.mGravity, OverlayDisplayHandle.this.mSecure, OverlayDisplayHandle.this);
                window.show();
                synchronized (OverlayDisplayAdapter.this.getSyncRoot()) {
                    OverlayDisplayHandle.this.mWindow = window;
                }
            }
        };
        private final Runnable mDismissRunnable = new Runnable() { // from class: com.android.server.display.OverlayDisplayAdapter.OverlayDisplayHandle.2
            @Override // java.lang.Runnable
            public void run() {
                OverlayDisplayWindow window;
                synchronized (OverlayDisplayAdapter.this.getSyncRoot()) {
                    window = OverlayDisplayHandle.this.mWindow;
                    OverlayDisplayHandle.this.mWindow = null;
                }
                if (window != null) {
                    window.dismiss();
                }
            }
        };

        public OverlayDisplayHandle(String name, int width, int height, int densityDpi, int gravity, boolean secure) {
            this.mName = name;
            this.mWidth = width;
            this.mHeight = height;
            this.mDensityDpi = densityDpi;
            this.mGravity = gravity;
            this.mSecure = secure;
            OverlayDisplayAdapter.this.mUiHandler.post(this.mShowRunnable);
        }

        public void dismissLocked() {
            OverlayDisplayAdapter.this.mUiHandler.removeCallbacks(this.mShowRunnable);
            OverlayDisplayAdapter.this.mUiHandler.post(this.mDismissRunnable);
        }

        @Override // com.android.server.display.OverlayDisplayWindow.Listener
        public void onWindowCreated(SurfaceTexture surfaceTexture, float refreshRate) {
            synchronized (OverlayDisplayAdapter.this.getSyncRoot()) {
                IBinder displayToken = SurfaceControl.createDisplay(this.mName, this.mSecure);
                this.mDevice = new OverlayDisplayDevice(displayToken, this.mName, this.mWidth, this.mHeight, refreshRate, this.mDensityDpi, this.mSecure, surfaceTexture);
                OverlayDisplayAdapter.this.sendDisplayDeviceEventLocked(this.mDevice, 1);
            }
        }

        @Override // com.android.server.display.OverlayDisplayWindow.Listener
        public void onWindowDestroyed() {
            synchronized (OverlayDisplayAdapter.this.getSyncRoot()) {
                if (this.mDevice != null) {
                    this.mDevice.destroyLocked();
                    OverlayDisplayAdapter.this.sendDisplayDeviceEventLocked(this.mDevice, 3);
                }
            }
        }

        public void dumpLocked(PrintWriter pw) {
            pw.println("  " + this.mName + Separators.COLON);
            pw.println("    mWidth=" + this.mWidth);
            pw.println("    mHeight=" + this.mHeight);
            pw.println("    mDensityDpi=" + this.mDensityDpi);
            pw.println("    mGravity=" + this.mGravity);
            pw.println("    mSecure=" + this.mSecure);
            if (this.mWindow != null) {
                IndentingPrintWriter ipw = new IndentingPrintWriter(pw, "    ");
                ipw.increaseIndent();
                DumpUtils.dumpAsync(OverlayDisplayAdapter.this.mUiHandler, this.mWindow, ipw, 200L);
            }
        }
    }
}