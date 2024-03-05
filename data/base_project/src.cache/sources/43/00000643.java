package android.hardware.display;

import android.content.Context;
import android.os.Handler;
import android.util.SparseArray;
import android.view.Display;
import android.view.Surface;
import java.util.ArrayList;

/* loaded from: DisplayManager.class */
public final class DisplayManager {
    private static final String TAG = "DisplayManager";
    private static final boolean DEBUG = false;
    private final Context mContext;
    public static final String ACTION_WIFI_DISPLAY_STATUS_CHANGED = "android.hardware.display.action.WIFI_DISPLAY_STATUS_CHANGED";
    public static final String EXTRA_WIFI_DISPLAY_STATUS = "android.hardware.display.extra.WIFI_DISPLAY_STATUS";
    public static final String DISPLAY_CATEGORY_PRESENTATION = "android.hardware.display.category.PRESENTATION";
    public static final int VIRTUAL_DISPLAY_FLAG_PUBLIC = 1;
    public static final int VIRTUAL_DISPLAY_FLAG_PRESENTATION = 2;
    public static final int VIRTUAL_DISPLAY_FLAG_SECURE = 4;
    private final Object mLock = new Object();
    private final SparseArray<Display> mDisplays = new SparseArray<>();
    private final ArrayList<Display> mTempDisplays = new ArrayList<>();
    private final DisplayManagerGlobal mGlobal = DisplayManagerGlobal.getInstance();

    /* loaded from: DisplayManager$DisplayListener.class */
    public interface DisplayListener {
        void onDisplayAdded(int i);

        void onDisplayRemoved(int i);

        void onDisplayChanged(int i);
    }

    public DisplayManager(Context context) {
        this.mContext = context;
    }

    public Display getDisplay(int displayId) {
        Display orCreateDisplayLocked;
        synchronized (this.mLock) {
            orCreateDisplayLocked = getOrCreateDisplayLocked(displayId, false);
        }
        return orCreateDisplayLocked;
    }

    public Display[] getDisplays() {
        return getDisplays(null);
    }

    public Display[] getDisplays(String category) {
        Display[] displayArr;
        int[] displayIds = this.mGlobal.getDisplayIds();
        synchronized (this.mLock) {
            if (category == null) {
                addAllDisplaysLocked(this.mTempDisplays, displayIds);
            } else if (category.equals("android.hardware.display.category.PRESENTATION")) {
                addPresentationDisplaysLocked(this.mTempDisplays, displayIds, 3);
                addPresentationDisplaysLocked(this.mTempDisplays, displayIds, 2);
                addPresentationDisplaysLocked(this.mTempDisplays, displayIds, 4);
                addPresentationDisplaysLocked(this.mTempDisplays, displayIds, 5);
            }
            displayArr = (Display[]) this.mTempDisplays.toArray(new Display[this.mTempDisplays.size()]);
            this.mTempDisplays.clear();
        }
        return displayArr;
    }

    private void addAllDisplaysLocked(ArrayList<Display> displays, int[] displayIds) {
        for (int i : displayIds) {
            Display display = getOrCreateDisplayLocked(i, true);
            if (display != null) {
                displays.add(display);
            }
        }
    }

    private void addPresentationDisplaysLocked(ArrayList<Display> displays, int[] displayIds, int matchType) {
        for (int i : displayIds) {
            Display display = getOrCreateDisplayLocked(i, true);
            if (display != null && (display.getFlags() & 8) != 0 && display.getType() == matchType) {
                displays.add(display);
            }
        }
    }

    private Display getOrCreateDisplayLocked(int displayId, boolean assumeValid) {
        Display display = this.mDisplays.get(displayId);
        if (display == null) {
            display = this.mGlobal.getCompatibleDisplay(displayId, this.mContext.getDisplayAdjustments(displayId));
            if (display != null) {
                this.mDisplays.put(displayId, display);
            }
        } else if (!assumeValid && !display.isValid()) {
            display = null;
        }
        return display;
    }

    public void registerDisplayListener(DisplayListener listener, Handler handler) {
        this.mGlobal.registerDisplayListener(listener, handler);
    }

    public void unregisterDisplayListener(DisplayListener listener) {
        this.mGlobal.unregisterDisplayListener(listener);
    }

    public void scanWifiDisplays() {
        this.mGlobal.scanWifiDisplays();
    }

    public void connectWifiDisplay(String deviceAddress) {
        this.mGlobal.connectWifiDisplay(deviceAddress);
    }

    public void pauseWifiDisplay() {
        this.mGlobal.pauseWifiDisplay();
    }

    public void resumeWifiDisplay() {
        this.mGlobal.resumeWifiDisplay();
    }

    public void disconnectWifiDisplay() {
        this.mGlobal.disconnectWifiDisplay();
    }

    public void renameWifiDisplay(String deviceAddress, String alias) {
        this.mGlobal.renameWifiDisplay(deviceAddress, alias);
    }

    public void forgetWifiDisplay(String deviceAddress) {
        this.mGlobal.forgetWifiDisplay(deviceAddress);
    }

    public WifiDisplayStatus getWifiDisplayStatus() {
        return this.mGlobal.getWifiDisplayStatus();
    }

    public VirtualDisplay createVirtualDisplay(String name, int width, int height, int densityDpi, Surface surface, int flags) {
        return this.mGlobal.createVirtualDisplay(this.mContext, name, width, height, densityDpi, surface, flags);
    }
}