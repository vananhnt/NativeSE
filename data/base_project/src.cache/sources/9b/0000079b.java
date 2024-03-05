package android.media;

import android.app.ActivityThread;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.hardware.display.DisplayManager;
import android.hardware.display.WifiDisplay;
import android.hardware.display.WifiDisplayStatus;
import android.media.IAudioRoutesObserver;
import android.media.IAudioService;
import android.media.IRemoteVolumeObserver;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import com.android.internal.R;
import gov.nist.core.Separators;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/* loaded from: MediaRouter.class */
public class MediaRouter {
    private static final String TAG = "MediaRouter";
    static Static sStatic;
    public static final int ROUTE_TYPE_LIVE_AUDIO = 1;
    public static final int ROUTE_TYPE_LIVE_VIDEO = 2;
    public static final int ROUTE_TYPE_USER = 8388608;
    public static final int CALLBACK_FLAG_PERFORM_ACTIVE_SCAN = 1;
    public static final int CALLBACK_FLAG_UNFILTERED_EVENTS = 2;
    static final HashMap<Context, MediaRouter> sRouters = new HashMap<>();

    /* loaded from: MediaRouter$VolumeCallback.class */
    public static abstract class VolumeCallback {
        public abstract void onVolumeUpdateRequest(RouteInfo routeInfo, int i);

        public abstract void onVolumeSetRequest(RouteInfo routeInfo, int i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: MediaRouter$Static.class */
    public static class Static implements DisplayManager.DisplayListener {
        private static final int WIFI_DISPLAY_SCAN_INTERVAL = 15000;
        final IAudioService mAudioService;
        final DisplayManager mDisplayService;
        final Handler mHandler;
        final RouteCategory mSystemCategory;
        RouteInfo mDefaultAudioVideo;
        RouteInfo mBluetoothA2dpRoute;
        RouteInfo mSelectedRoute;
        WifiDisplayStatus mLastKnownWifiDisplayStatus;
        boolean mActivelyScanningWifiDisplays;
        final CopyOnWriteArrayList<CallbackInfo> mCallbacks = new CopyOnWriteArrayList<>();
        final ArrayList<RouteInfo> mRoutes = new ArrayList<>();
        final ArrayList<RouteCategory> mCategories = new ArrayList<>();
        final AudioRoutesInfo mCurAudioRoutesInfo = new AudioRoutesInfo();
        final IAudioRoutesObserver.Stub mAudioRoutesObserver = new IAudioRoutesObserver.Stub() { // from class: android.media.MediaRouter.Static.1
            @Override // android.media.IAudioRoutesObserver
            public void dispatchAudioRoutesChanged(final AudioRoutesInfo newRoutes) {
                Static.this.mHandler.post(new Runnable() { // from class: android.media.MediaRouter.Static.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        Static.this.updateAudioRoutes(newRoutes);
                    }
                });
            }
        };
        final Runnable mScanWifiDisplays = new Runnable() { // from class: android.media.MediaRouter.Static.2
            @Override // java.lang.Runnable
            public void run() {
                if (Static.this.mActivelyScanningWifiDisplays) {
                    Static.this.mDisplayService.scanWifiDisplays();
                    Static.this.mHandler.postDelayed(this, 15000L);
                }
            }
        };
        final Resources mResources = Resources.getSystem();

        Static(Context appContext) {
            this.mHandler = new Handler(appContext.getMainLooper());
            IBinder b = ServiceManager.getService(Context.AUDIO_SERVICE);
            this.mAudioService = IAudioService.Stub.asInterface(b);
            this.mDisplayService = (DisplayManager) appContext.getSystemService(Context.DISPLAY_SERVICE);
            this.mSystemCategory = new RouteCategory((int) R.string.default_audio_route_category_name, 3, false);
            this.mSystemCategory.mIsSystem = true;
        }

        void startMonitoringRoutes(Context appContext) {
            this.mDefaultAudioVideo = new RouteInfo(this.mSystemCategory);
            this.mDefaultAudioVideo.mNameResId = R.string.default_audio_route_name;
            this.mDefaultAudioVideo.mSupportedTypes = 3;
            this.mDefaultAudioVideo.mPresentationDisplay = MediaRouter.choosePresentationDisplayForRoute(this.mDefaultAudioVideo, getAllPresentationDisplays());
            MediaRouter.addRouteStatic(this.mDefaultAudioVideo);
            MediaRouter.updateWifiDisplayStatus(this.mDisplayService.getWifiDisplayStatus());
            appContext.registerReceiver(new WifiDisplayStatusChangedReceiver(), new IntentFilter(DisplayManager.ACTION_WIFI_DISPLAY_STATUS_CHANGED));
            appContext.registerReceiver(new VolumeChangeReceiver(), new IntentFilter(AudioManager.VOLUME_CHANGED_ACTION));
            this.mDisplayService.registerDisplayListener(this, this.mHandler);
            AudioRoutesInfo newAudioRoutes = null;
            try {
                newAudioRoutes = this.mAudioService.startWatchingRoutes(this.mAudioRoutesObserver);
            } catch (RemoteException e) {
            }
            if (newAudioRoutes != null) {
                updateAudioRoutes(newAudioRoutes);
            }
            if (this.mSelectedRoute == null) {
                MediaRouter.selectRouteStatic(this.mDefaultAudioVideo.getSupportedTypes(), this.mDefaultAudioVideo);
            }
        }

        void updateAudioRoutes(AudioRoutesInfo newRoutes) {
            boolean a2dpEnabled;
            int name;
            if (newRoutes.mMainType != this.mCurAudioRoutesInfo.mMainType) {
                this.mCurAudioRoutesInfo.mMainType = newRoutes.mMainType;
                if ((newRoutes.mMainType & 2) != 0 || (newRoutes.mMainType & 1) != 0) {
                    name = 17040700;
                } else if ((newRoutes.mMainType & 4) != 0) {
                    name = 17040701;
                } else if ((newRoutes.mMainType & 8) != 0) {
                    name = 17040702;
                } else {
                    name = 17040699;
                }
                MediaRouter.sStatic.mDefaultAudioVideo.mNameResId = name;
                MediaRouter.dispatchRouteChanged(MediaRouter.sStatic.mDefaultAudioVideo);
            }
            int mainType = this.mCurAudioRoutesInfo.mMainType;
            try {
                a2dpEnabled = this.mAudioService.isBluetoothA2dpOn();
            } catch (RemoteException e) {
                Log.e(MediaRouter.TAG, "Error querying Bluetooth A2DP state", e);
                a2dpEnabled = false;
            }
            if (!TextUtils.equals(newRoutes.mBluetoothName, this.mCurAudioRoutesInfo.mBluetoothName)) {
                this.mCurAudioRoutesInfo.mBluetoothName = newRoutes.mBluetoothName;
                if (this.mCurAudioRoutesInfo.mBluetoothName != null) {
                    if (MediaRouter.sStatic.mBluetoothA2dpRoute == null) {
                        RouteInfo info = new RouteInfo(MediaRouter.sStatic.mSystemCategory);
                        info.mName = this.mCurAudioRoutesInfo.mBluetoothName;
                        info.mDescription = MediaRouter.sStatic.mResources.getText(R.string.bluetooth_a2dp_audio_route_name);
                        info.mSupportedTypes = 1;
                        MediaRouter.sStatic.mBluetoothA2dpRoute = info;
                        MediaRouter.addRouteStatic(MediaRouter.sStatic.mBluetoothA2dpRoute);
                    } else {
                        MediaRouter.sStatic.mBluetoothA2dpRoute.mName = this.mCurAudioRoutesInfo.mBluetoothName;
                        MediaRouter.dispatchRouteChanged(MediaRouter.sStatic.mBluetoothA2dpRoute);
                    }
                } else if (MediaRouter.sStatic.mBluetoothA2dpRoute != null) {
                    MediaRouter.removeRoute(MediaRouter.sStatic.mBluetoothA2dpRoute);
                    MediaRouter.sStatic.mBluetoothA2dpRoute = null;
                }
            }
            if (this.mBluetoothA2dpRoute != null) {
                if (mainType != 0 && this.mSelectedRoute == this.mBluetoothA2dpRoute && !a2dpEnabled) {
                    MediaRouter.selectRouteStatic(1, this.mDefaultAudioVideo);
                } else if ((this.mSelectedRoute == this.mDefaultAudioVideo || this.mSelectedRoute == null) && a2dpEnabled) {
                    MediaRouter.selectRouteStatic(1, this.mBluetoothA2dpRoute);
                }
            }
        }

        void updateActiveScan() {
            if (hasActiveScanCallbackOfType(2)) {
                if (!this.mActivelyScanningWifiDisplays) {
                    this.mActivelyScanningWifiDisplays = true;
                    this.mHandler.post(this.mScanWifiDisplays);
                }
            } else if (this.mActivelyScanningWifiDisplays) {
                this.mActivelyScanningWifiDisplays = false;
                this.mHandler.removeCallbacks(this.mScanWifiDisplays);
            }
        }

        private boolean hasActiveScanCallbackOfType(int type) {
            int count = this.mCallbacks.size();
            for (int i = 0; i < count; i++) {
                CallbackInfo cbi = this.mCallbacks.get(i);
                if ((cbi.flags & 1) != 0 && (cbi.type & type) != 0) {
                    return true;
                }
            }
            return false;
        }

        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayAdded(int displayId) {
            updatePresentationDisplays(displayId);
        }

        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayChanged(int displayId) {
            updatePresentationDisplays(displayId);
        }

        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayRemoved(int displayId) {
            updatePresentationDisplays(displayId);
        }

        public Display[] getAllPresentationDisplays() {
            return this.mDisplayService.getDisplays("android.hardware.display.category.PRESENTATION");
        }

        private void updatePresentationDisplays(int changedDisplayId) {
            Display[] displays = getAllPresentationDisplays();
            int count = this.mRoutes.size();
            for (int i = 0; i < count; i++) {
                RouteInfo info = this.mRoutes.get(i);
                Display display = MediaRouter.choosePresentationDisplayForRoute(info, displays);
                if (display != info.mPresentationDisplay || (display != null && display.getDisplayId() == changedDisplayId)) {
                    info.mPresentationDisplay = display;
                    MediaRouter.dispatchRoutePresentationDisplayChanged(info);
                }
            }
        }
    }

    static String typesToString(int types) {
        StringBuilder result = new StringBuilder();
        if ((types & 1) != 0) {
            result.append("ROUTE_TYPE_LIVE_AUDIO ");
        }
        if ((types & 2) != 0) {
            result.append("ROUTE_TYPE_LIVE_VIDEO ");
        }
        if ((types & 8388608) != 0) {
            result.append("ROUTE_TYPE_USER ");
        }
        return result.toString();
    }

    public MediaRouter(Context context) {
        synchronized (Static.class) {
            if (sStatic == null) {
                Context appContext = context.getApplicationContext();
                sStatic = new Static(appContext);
                sStatic.startMonitoringRoutes(appContext);
            }
        }
    }

    public RouteInfo getDefaultRoute() {
        return sStatic.mDefaultAudioVideo;
    }

    public RouteCategory getSystemCategory() {
        return sStatic.mSystemCategory;
    }

    public RouteInfo getSelectedRoute(int type) {
        if (sStatic.mSelectedRoute != null && (sStatic.mSelectedRoute.mSupportedTypes & type) != 0) {
            return sStatic.mSelectedRoute;
        }
        if (type == 8388608) {
            return null;
        }
        return sStatic.mDefaultAudioVideo;
    }

    public void addCallback(int types, Callback cb) {
        addCallback(types, cb, 0);
    }

    public void addCallback(int types, Callback cb, int flags) {
        CallbackInfo info;
        int index = findCallbackInfo(cb);
        if (index >= 0) {
            info = sStatic.mCallbacks.get(index);
            info.type |= types;
            info.flags |= flags;
        } else {
            info = new CallbackInfo(cb, types, flags, this);
            sStatic.mCallbacks.add(info);
        }
        if ((info.flags & 1) != 0) {
            sStatic.updateActiveScan();
        }
    }

    public void removeCallback(Callback cb) {
        int index = findCallbackInfo(cb);
        if (index >= 0) {
            CallbackInfo info = sStatic.mCallbacks.remove(index);
            if ((info.flags & 1) != 0) {
                sStatic.updateActiveScan();
                return;
            }
            return;
        }
        Log.w(TAG, "removeCallback(" + cb + "): callback not registered");
    }

    private int findCallbackInfo(Callback cb) {
        int count = sStatic.mCallbacks.size();
        for (int i = 0; i < count; i++) {
            CallbackInfo info = sStatic.mCallbacks.get(i);
            if (info.cb == cb) {
                return i;
            }
        }
        return -1;
    }

    public void selectRoute(int types, RouteInfo route) {
        selectRouteStatic(types, route);
    }

    public void selectRouteInt(int types, RouteInfo route) {
        selectRouteStatic(types, route);
    }

    static void selectRouteStatic(int types, RouteInfo route) {
        RouteInfo oldRoute = sStatic.mSelectedRoute;
        if (oldRoute == route) {
            return;
        }
        if ((route.getSupportedTypes() & types) == 0) {
            Log.w(TAG, "selectRoute ignored; cannot select route with supported types " + typesToString(route.getSupportedTypes()) + " into route types " + typesToString(types));
            return;
        }
        RouteInfo btRoute = sStatic.mBluetoothA2dpRoute;
        if (btRoute != null && (types & 1) != 0 && (route == btRoute || route == sStatic.mDefaultAudioVideo)) {
            try {
                sStatic.mAudioService.setBluetoothA2dpOn(route == btRoute);
            } catch (RemoteException e) {
                Log.e(TAG, "Error changing Bluetooth A2DP state", e);
            }
        }
        WifiDisplay activeDisplay = sStatic.mDisplayService.getWifiDisplayStatus().getActiveDisplay();
        boolean oldRouteHasAddress = (oldRoute == null || oldRoute.mDeviceAddress == null) ? false : true;
        boolean newRouteHasAddress = (route == null || route.mDeviceAddress == null) ? false : true;
        if (activeDisplay != null || oldRouteHasAddress || newRouteHasAddress) {
            if (newRouteHasAddress && !matchesDeviceAddress(activeDisplay, route)) {
                sStatic.mDisplayService.connectWifiDisplay(route.mDeviceAddress);
            } else if (activeDisplay != null && !newRouteHasAddress) {
                sStatic.mDisplayService.disconnectWifiDisplay();
            }
        }
        if (oldRoute != null) {
            dispatchRouteUnselected(types & oldRoute.getSupportedTypes(), oldRoute);
        }
        sStatic.mSelectedRoute = route;
        if (route != null) {
            dispatchRouteSelected(types & route.getSupportedTypes(), route);
        }
    }

    static boolean matchesDeviceAddress(WifiDisplay display, RouteInfo info) {
        boolean routeHasAddress = (info == null || info.mDeviceAddress == null) ? false : true;
        if (display == null && !routeHasAddress) {
            return true;
        }
        if (display != null && routeHasAddress) {
            return display.getDeviceAddress().equals(info.mDeviceAddress);
        }
        return false;
    }

    public void addUserRoute(UserRouteInfo info) {
        addRouteStatic(info);
    }

    public void addRouteInt(RouteInfo info) {
        addRouteStatic(info);
    }

    static void addRouteStatic(RouteInfo info) {
        RouteCategory cat = info.getCategory();
        if (!sStatic.mCategories.contains(cat)) {
            sStatic.mCategories.add(cat);
        }
        if (cat.isGroupable() && !(info instanceof RouteGroup)) {
            RouteGroup group = new RouteGroup(info.getCategory());
            group.mSupportedTypes = info.mSupportedTypes;
            sStatic.mRoutes.add(group);
            dispatchRouteAdded(group);
            group.addRoute(info);
            return;
        }
        sStatic.mRoutes.add(info);
        dispatchRouteAdded(info);
    }

    public void removeUserRoute(UserRouteInfo info) {
        removeRoute(info);
    }

    public void clearUserRoutes() {
        int i = 0;
        while (i < sStatic.mRoutes.size()) {
            RouteInfo info = sStatic.mRoutes.get(i);
            if ((info instanceof UserRouteInfo) || (info instanceof RouteGroup)) {
                removeRouteAt(i);
                i--;
            }
            i++;
        }
    }

    public void removeRouteInt(RouteInfo info) {
        removeRoute(info);
    }

    static void removeRoute(RouteInfo info) {
        if (sStatic.mRoutes.remove(info)) {
            RouteCategory removingCat = info.getCategory();
            int count = sStatic.mRoutes.size();
            boolean found = false;
            int i = 0;
            while (true) {
                if (i >= count) {
                    break;
                }
                RouteCategory cat = sStatic.mRoutes.get(i).getCategory();
                if (removingCat != cat) {
                    i++;
                } else {
                    found = true;
                    break;
                }
            }
            if (info == sStatic.mSelectedRoute) {
                if (info != sStatic.mBluetoothA2dpRoute && sStatic.mBluetoothA2dpRoute != null) {
                    selectRouteStatic(8388609, sStatic.mBluetoothA2dpRoute);
                } else {
                    selectRouteStatic(8388609, sStatic.mDefaultAudioVideo);
                }
            }
            if (!found) {
                sStatic.mCategories.remove(removingCat);
            }
            dispatchRouteRemoved(info);
        }
    }

    void removeRouteAt(int routeIndex) {
        if (routeIndex >= 0 && routeIndex < sStatic.mRoutes.size()) {
            RouteInfo info = sStatic.mRoutes.remove(routeIndex);
            RouteCategory removingCat = info.getCategory();
            int count = sStatic.mRoutes.size();
            boolean found = false;
            int i = 0;
            while (true) {
                if (i >= count) {
                    break;
                }
                RouteCategory cat = sStatic.mRoutes.get(i).getCategory();
                if (removingCat != cat) {
                    i++;
                } else {
                    found = true;
                    break;
                }
            }
            if (info == sStatic.mSelectedRoute) {
                selectRouteStatic(8388611, sStatic.mDefaultAudioVideo);
            }
            if (!found) {
                sStatic.mCategories.remove(removingCat);
            }
            dispatchRouteRemoved(info);
        }
    }

    public int getCategoryCount() {
        return sStatic.mCategories.size();
    }

    public RouteCategory getCategoryAt(int index) {
        return sStatic.mCategories.get(index);
    }

    public int getRouteCount() {
        return sStatic.mRoutes.size();
    }

    public RouteInfo getRouteAt(int index) {
        return sStatic.mRoutes.get(index);
    }

    static int getRouteCountStatic() {
        return sStatic.mRoutes.size();
    }

    static RouteInfo getRouteAtStatic(int index) {
        return sStatic.mRoutes.get(index);
    }

    public UserRouteInfo createUserRoute(RouteCategory category) {
        return new UserRouteInfo(category);
    }

    public RouteCategory createRouteCategory(CharSequence name, boolean isGroupable) {
        return new RouteCategory(name, 8388608, isGroupable);
    }

    public RouteCategory createRouteCategory(int nameResId, boolean isGroupable) {
        return new RouteCategory(nameResId, 8388608, isGroupable);
    }

    static void updateRoute(RouteInfo info) {
        dispatchRouteChanged(info);
    }

    static void dispatchRouteSelected(int type, RouteInfo info) {
        Iterator i$ = sStatic.mCallbacks.iterator();
        while (i$.hasNext()) {
            CallbackInfo cbi = i$.next();
            if (cbi.filterRouteEvent(info)) {
                cbi.cb.onRouteSelected(cbi.router, type, info);
            }
        }
    }

    static void dispatchRouteUnselected(int type, RouteInfo info) {
        Iterator i$ = sStatic.mCallbacks.iterator();
        while (i$.hasNext()) {
            CallbackInfo cbi = i$.next();
            if (cbi.filterRouteEvent(info)) {
                cbi.cb.onRouteUnselected(cbi.router, type, info);
            }
        }
    }

    static void dispatchRouteChanged(RouteInfo info) {
        Iterator i$ = sStatic.mCallbacks.iterator();
        while (i$.hasNext()) {
            CallbackInfo cbi = i$.next();
            if (cbi.filterRouteEvent(info)) {
                cbi.cb.onRouteChanged(cbi.router, info);
            }
        }
    }

    static void dispatchRouteAdded(RouteInfo info) {
        Iterator i$ = sStatic.mCallbacks.iterator();
        while (i$.hasNext()) {
            CallbackInfo cbi = i$.next();
            if (cbi.filterRouteEvent(info)) {
                cbi.cb.onRouteAdded(cbi.router, info);
            }
        }
    }

    static void dispatchRouteRemoved(RouteInfo info) {
        Iterator i$ = sStatic.mCallbacks.iterator();
        while (i$.hasNext()) {
            CallbackInfo cbi = i$.next();
            if (cbi.filterRouteEvent(info)) {
                cbi.cb.onRouteRemoved(cbi.router, info);
            }
        }
    }

    static void dispatchRouteGrouped(RouteInfo info, RouteGroup group, int index) {
        Iterator i$ = sStatic.mCallbacks.iterator();
        while (i$.hasNext()) {
            CallbackInfo cbi = i$.next();
            if (cbi.filterRouteEvent(group)) {
                cbi.cb.onRouteGrouped(cbi.router, info, group, index);
            }
        }
    }

    static void dispatchRouteUngrouped(RouteInfo info, RouteGroup group) {
        Iterator i$ = sStatic.mCallbacks.iterator();
        while (i$.hasNext()) {
            CallbackInfo cbi = i$.next();
            if (cbi.filterRouteEvent(group)) {
                cbi.cb.onRouteUngrouped(cbi.router, info, group);
            }
        }
    }

    static void dispatchRouteVolumeChanged(RouteInfo info) {
        Iterator i$ = sStatic.mCallbacks.iterator();
        while (i$.hasNext()) {
            CallbackInfo cbi = i$.next();
            if (cbi.filterRouteEvent(info)) {
                cbi.cb.onRouteVolumeChanged(cbi.router, info);
            }
        }
    }

    static void dispatchRoutePresentationDisplayChanged(RouteInfo info) {
        Iterator i$ = sStatic.mCallbacks.iterator();
        while (i$.hasNext()) {
            CallbackInfo cbi = i$.next();
            if (cbi.filterRouteEvent(info)) {
                cbi.cb.onRoutePresentationDisplayChanged(cbi.router, info);
            }
        }
    }

    static void systemVolumeChanged(int newValue) {
        RouteInfo selectedRoute = sStatic.mSelectedRoute;
        if (selectedRoute == null) {
            return;
        }
        if (selectedRoute == sStatic.mBluetoothA2dpRoute || selectedRoute == sStatic.mDefaultAudioVideo) {
            dispatchRouteVolumeChanged(selectedRoute);
        } else if (sStatic.mBluetoothA2dpRoute != null) {
            try {
                dispatchRouteVolumeChanged(sStatic.mAudioService.isBluetoothA2dpOn() ? sStatic.mBluetoothA2dpRoute : sStatic.mDefaultAudioVideo);
            } catch (RemoteException e) {
                Log.e(TAG, "Error checking Bluetooth A2DP state to report volume change", e);
            }
        } else {
            dispatchRouteVolumeChanged(sStatic.mDefaultAudioVideo);
        }
    }

    static void updateWifiDisplayStatus(WifiDisplayStatus newStatus) {
        WifiDisplay[] newDisplays;
        WifiDisplay activeDisplay;
        WifiDisplay newDisplay;
        WifiDisplayStatus oldStatus = sStatic.mLastKnownWifiDisplayStatus;
        boolean wantScan = false;
        boolean blockScan = false;
        WifiDisplay[] oldDisplays = oldStatus != null ? oldStatus.getDisplays() : WifiDisplay.EMPTY_ARRAY;
        if (newStatus.getFeatureState() == 3) {
            newDisplays = newStatus.getDisplays();
            activeDisplay = newStatus.getActiveDisplay();
        } else {
            newDisplays = WifiDisplay.EMPTY_ARRAY;
            activeDisplay = null;
        }
        for (WifiDisplay d : newDisplays) {
            if (d.isRemembered()) {
                RouteInfo route = findWifiDisplayRoute(d);
                if (route == null) {
                    route = makeWifiDisplayRoute(d, newStatus);
                    addRouteStatic(route);
                    wantScan = true;
                } else {
                    updateWifiDisplayRoute(route, d, newStatus);
                }
                if (d.equals(activeDisplay)) {
                    selectRouteStatic(route.getSupportedTypes(), route);
                    blockScan = true;
                }
            }
        }
        for (WifiDisplay d2 : oldDisplays) {
            if (d2.isRemembered() && ((newDisplay = findMatchingDisplay(d2, newDisplays)) == null || !newDisplay.isRemembered())) {
                removeRoute(findWifiDisplayRoute(d2));
            }
        }
        if (wantScan && !blockScan) {
            sStatic.mDisplayService.scanWifiDisplays();
        }
        sStatic.mLastKnownWifiDisplayStatus = newStatus;
    }

    static int getWifiDisplayStatusCode(WifiDisplay d, WifiDisplayStatus wfdStatus) {
        int newStatus;
        if (wfdStatus.getScanState() == 1) {
            newStatus = 1;
        } else if (d.isAvailable()) {
            newStatus = d.canConnect() ? 3 : 5;
        } else {
            newStatus = 4;
        }
        if (d.equals(wfdStatus.getActiveDisplay())) {
            int activeState = wfdStatus.getActiveDisplayState();
            switch (activeState) {
                case 0:
                    Log.e(TAG, "Active display is not connected!");
                    break;
                case 1:
                    newStatus = 2;
                    break;
                case 2:
                    newStatus = 0;
                    break;
            }
        }
        return newStatus;
    }

    static boolean isWifiDisplayEnabled(WifiDisplay d, WifiDisplayStatus wfdStatus) {
        return d.isAvailable() && (d.canConnect() || d.equals(wfdStatus.getActiveDisplay()));
    }

    static RouteInfo makeWifiDisplayRoute(WifiDisplay display, WifiDisplayStatus wfdStatus) {
        RouteInfo newRoute = new RouteInfo(sStatic.mSystemCategory);
        newRoute.mDeviceAddress = display.getDeviceAddress();
        newRoute.mSupportedTypes = 3;
        newRoute.mVolumeHandling = 0;
        newRoute.mPlaybackType = 1;
        newRoute.setStatusCode(getWifiDisplayStatusCode(display, wfdStatus));
        newRoute.mEnabled = isWifiDisplayEnabled(display, wfdStatus);
        newRoute.mName = display.getFriendlyDisplayName();
        newRoute.mDescription = sStatic.mResources.getText(R.string.wireless_display_route_description);
        newRoute.mPresentationDisplay = choosePresentationDisplayForRoute(newRoute, sStatic.getAllPresentationDisplays());
        return newRoute;
    }

    private static void updateWifiDisplayRoute(RouteInfo route, WifiDisplay display, WifiDisplayStatus wfdStatus) {
        boolean changed = false;
        String newName = display.getFriendlyDisplayName();
        if (!route.getName().equals(newName)) {
            route.mName = newName;
            changed = true;
        }
        boolean enabled = isWifiDisplayEnabled(display, wfdStatus);
        boolean changed2 = changed | (route.mEnabled != enabled);
        route.mEnabled = enabled;
        if (changed2 | route.setStatusCode(getWifiDisplayStatusCode(display, wfdStatus))) {
            dispatchRouteChanged(route);
        }
        if (!enabled && route == sStatic.mSelectedRoute) {
            RouteInfo defaultRoute = sStatic.mDefaultAudioVideo;
            selectRouteStatic(defaultRoute.getSupportedTypes(), defaultRoute);
        }
    }

    private static WifiDisplay findMatchingDisplay(WifiDisplay d, WifiDisplay[] displays) {
        for (WifiDisplay other : displays) {
            if (d.hasSameAddress(other)) {
                return other;
            }
        }
        return null;
    }

    private static RouteInfo findWifiDisplayRoute(WifiDisplay d) {
        int count = sStatic.mRoutes.size();
        for (int i = 0; i < count; i++) {
            RouteInfo info = sStatic.mRoutes.get(i);
            if (d.getDeviceAddress().equals(info.mDeviceAddress)) {
                return info;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Display choosePresentationDisplayForRoute(RouteInfo route, Display[] displays) {
        if ((route.mSupportedTypes & 2) != 0) {
            if (route.mDeviceAddress != null) {
                for (Display display : displays) {
                    if (display.getType() == 3 && route.mDeviceAddress.equals(display.getAddress())) {
                        return display;
                    }
                }
                return null;
            } else if (route == sStatic.mDefaultAudioVideo && displays.length > 0) {
                return displays[0];
            } else {
                return null;
            }
        }
        return null;
    }

    /* loaded from: MediaRouter$RouteInfo.class */
    public static class RouteInfo {
        CharSequence mName;
        int mNameResId;
        CharSequence mDescription;
        private CharSequence mStatus;
        int mSupportedTypes;
        RouteGroup mGroup;
        final RouteCategory mCategory;
        Drawable mIcon;
        VolumeCallbackInfo mVcb;
        Display mPresentationDisplay;
        String mDeviceAddress;
        private int mStatusCode;
        public static final int STATUS_NONE = 0;
        public static final int STATUS_SCANNING = 1;
        public static final int STATUS_CONNECTING = 2;
        public static final int STATUS_AVAILABLE = 3;
        public static final int STATUS_NOT_AVAILABLE = 4;
        public static final int STATUS_IN_USE = 5;
        private Object mTag;
        public static final int PLAYBACK_TYPE_LOCAL = 0;
        public static final int PLAYBACK_TYPE_REMOTE = 1;
        public static final int PLAYBACK_VOLUME_FIXED = 0;
        public static final int PLAYBACK_VOLUME_VARIABLE = 1;
        int mPlaybackType = 0;
        int mVolumeMax = 15;
        int mVolume = 15;
        int mVolumeHandling = 1;
        int mPlaybackStream = 3;
        boolean mEnabled = true;
        final IRemoteVolumeObserver.Stub mRemoteVolObserver = new IRemoteVolumeObserver.Stub() { // from class: android.media.MediaRouter.RouteInfo.1
            @Override // android.media.IRemoteVolumeObserver
            public void dispatchRemoteVolumeUpdate(final int direction, final int value) {
                MediaRouter.sStatic.mHandler.post(new Runnable() { // from class: android.media.MediaRouter.RouteInfo.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (RouteInfo.this.mVcb != null) {
                            if (direction != 0) {
                                RouteInfo.this.mVcb.vcb.onVolumeUpdateRequest(RouteInfo.this.mVcb.route, direction);
                            } else {
                                RouteInfo.this.mVcb.vcb.onVolumeSetRequest(RouteInfo.this.mVcb.route, value);
                            }
                        }
                    }
                });
            }
        };

        RouteInfo(RouteCategory category) {
            this.mCategory = category;
        }

        public CharSequence getName() {
            return getName(MediaRouter.sStatic.mResources);
        }

        public CharSequence getName(Context context) {
            return getName(context.getResources());
        }

        CharSequence getName(Resources res) {
            if (this.mNameResId != 0) {
                CharSequence text = res.getText(this.mNameResId);
                this.mName = text;
                return text;
            }
            return this.mName;
        }

        public CharSequence getDescription() {
            return this.mDescription;
        }

        public CharSequence getStatus() {
            return this.mStatus;
        }

        boolean setStatusCode(int statusCode) {
            if (statusCode != this.mStatusCode) {
                this.mStatusCode = statusCode;
                int resId = 0;
                switch (statusCode) {
                    case 1:
                        resId = 17040708;
                        break;
                    case 2:
                        resId = 17040709;
                        break;
                    case 3:
                        resId = 17040710;
                        break;
                    case 4:
                        resId = 17040711;
                        break;
                    case 5:
                        resId = 17040712;
                        break;
                }
                this.mStatus = resId != 0 ? MediaRouter.sStatic.mResources.getText(resId) : null;
                return true;
            }
            return false;
        }

        public int getStatusCode() {
            return this.mStatusCode;
        }

        public int getSupportedTypes() {
            return this.mSupportedTypes;
        }

        public RouteGroup getGroup() {
            return this.mGroup;
        }

        public RouteCategory getCategory() {
            return this.mCategory;
        }

        public Drawable getIconDrawable() {
            return this.mIcon;
        }

        public void setTag(Object tag) {
            this.mTag = tag;
            routeUpdated();
        }

        public Object getTag() {
            return this.mTag;
        }

        public int getPlaybackType() {
            return this.mPlaybackType;
        }

        public int getPlaybackStream() {
            return this.mPlaybackStream;
        }

        public int getVolume() {
            if (this.mPlaybackType == 0) {
                int vol = 0;
                try {
                    vol = MediaRouter.sStatic.mAudioService.getStreamVolume(this.mPlaybackStream);
                } catch (RemoteException e) {
                    Log.e(MediaRouter.TAG, "Error getting local stream volume", e);
                }
                return vol;
            }
            return this.mVolume;
        }

        public void requestSetVolume(int volume) {
            if (this.mPlaybackType == 0) {
                try {
                    MediaRouter.sStatic.mAudioService.setStreamVolume(this.mPlaybackStream, volume, 0, ActivityThread.currentPackageName());
                    return;
                } catch (RemoteException e) {
                    Log.e(MediaRouter.TAG, "Error setting local stream volume", e);
                    return;
                }
            }
            Log.e(MediaRouter.TAG, getClass().getSimpleName() + ".requestSetVolume(): Non-local volume playback on system route? Could not request volume change.");
        }

        public void requestUpdateVolume(int direction) {
            if (this.mPlaybackType == 0) {
                try {
                    int volume = Math.max(0, Math.min(getVolume() + direction, getVolumeMax()));
                    MediaRouter.sStatic.mAudioService.setStreamVolume(this.mPlaybackStream, volume, 0, ActivityThread.currentPackageName());
                    return;
                } catch (RemoteException e) {
                    Log.e(MediaRouter.TAG, "Error setting local stream volume", e);
                    return;
                }
            }
            Log.e(MediaRouter.TAG, getClass().getSimpleName() + ".requestChangeVolume(): Non-local volume playback on system route? Could not request volume change.");
        }

        public int getVolumeMax() {
            if (this.mPlaybackType == 0) {
                int volMax = 0;
                try {
                    volMax = MediaRouter.sStatic.mAudioService.getStreamMaxVolume(this.mPlaybackStream);
                } catch (RemoteException e) {
                    Log.e(MediaRouter.TAG, "Error getting local stream volume", e);
                }
                return volMax;
            }
            return this.mVolumeMax;
        }

        public int getVolumeHandling() {
            return this.mVolumeHandling;
        }

        public Display getPresentationDisplay() {
            return this.mPresentationDisplay;
        }

        public boolean isEnabled() {
            return this.mEnabled;
        }

        public boolean isConnecting() {
            return this.mStatusCode == 2;
        }

        void setStatusInt(CharSequence status) {
            if (!status.equals(this.mStatus)) {
                this.mStatus = status;
                if (this.mGroup != null) {
                    this.mGroup.memberStatusChanged(this, status);
                }
                routeUpdated();
            }
        }

        void routeUpdated() {
            MediaRouter.updateRoute(this);
        }

        public String toString() {
            String supportedTypes = MediaRouter.typesToString(getSupportedTypes());
            return getClass().getSimpleName() + "{ name=" + ((Object) getName()) + ", description=" + ((Object) getDescription()) + ", status=" + ((Object) getStatus()) + ", category=" + getCategory() + ", supportedTypes=" + supportedTypes + ", presentationDisplay=" + this.mPresentationDisplay + "}";
        }
    }

    /* loaded from: MediaRouter$UserRouteInfo.class */
    public static class UserRouteInfo extends RouteInfo {
        RemoteControlClient mRcc;

        UserRouteInfo(RouteCategory category) {
            super(category);
            this.mSupportedTypes = 8388608;
            this.mPlaybackType = 1;
            this.mVolumeHandling = 0;
        }

        public void setName(CharSequence name) {
            this.mName = name;
            routeUpdated();
        }

        public void setName(int resId) {
            this.mNameResId = resId;
            this.mName = null;
            routeUpdated();
        }

        public void setDescription(CharSequence description) {
            this.mDescription = description;
            routeUpdated();
        }

        public void setStatus(CharSequence status) {
            setStatusInt(status);
        }

        public void setRemoteControlClient(RemoteControlClient rcc) {
            this.mRcc = rcc;
            updatePlaybackInfoOnRcc();
        }

        public RemoteControlClient getRemoteControlClient() {
            return this.mRcc;
        }

        public void setIconDrawable(Drawable icon) {
            this.mIcon = icon;
        }

        public void setIconResource(int resId) {
            setIconDrawable(MediaRouter.sStatic.mResources.getDrawable(resId));
        }

        public void setVolumeCallback(VolumeCallback vcb) {
            this.mVcb = new VolumeCallbackInfo(vcb, this);
        }

        public void setPlaybackType(int type) {
            if (this.mPlaybackType != type) {
                this.mPlaybackType = type;
                setPlaybackInfoOnRcc(1, type);
            }
        }

        public void setVolumeHandling(int volumeHandling) {
            if (this.mVolumeHandling != volumeHandling) {
                this.mVolumeHandling = volumeHandling;
                setPlaybackInfoOnRcc(4, volumeHandling);
            }
        }

        public void setVolume(int volume) {
            int volume2 = Math.max(0, Math.min(volume, getVolumeMax()));
            if (this.mVolume != volume2) {
                this.mVolume = volume2;
                setPlaybackInfoOnRcc(2, volume2);
                MediaRouter.dispatchRouteVolumeChanged(this);
                if (this.mGroup != null) {
                    this.mGroup.memberVolumeChanged(this);
                }
            }
        }

        @Override // android.media.MediaRouter.RouteInfo
        public void requestSetVolume(int volume) {
            if (this.mVolumeHandling == 1) {
                if (this.mVcb == null) {
                    Log.e(MediaRouter.TAG, "Cannot requestSetVolume on user route - no volume callback set");
                } else {
                    this.mVcb.vcb.onVolumeSetRequest(this, volume);
                }
            }
        }

        @Override // android.media.MediaRouter.RouteInfo
        public void requestUpdateVolume(int direction) {
            if (this.mVolumeHandling == 1) {
                if (this.mVcb == null) {
                    Log.e(MediaRouter.TAG, "Cannot requestChangeVolume on user route - no volumec callback set");
                } else {
                    this.mVcb.vcb.onVolumeUpdateRequest(this, direction);
                }
            }
        }

        public void setVolumeMax(int volumeMax) {
            if (this.mVolumeMax != volumeMax) {
                this.mVolumeMax = volumeMax;
                setPlaybackInfoOnRcc(3, volumeMax);
            }
        }

        public void setPlaybackStream(int stream) {
            if (this.mPlaybackStream != stream) {
                this.mPlaybackStream = stream;
                setPlaybackInfoOnRcc(5, stream);
            }
        }

        private void updatePlaybackInfoOnRcc() {
            if (this.mRcc != null && this.mRcc.getRcseId() != -1) {
                this.mRcc.setPlaybackInformation(3, this.mVolumeMax);
                this.mRcc.setPlaybackInformation(2, this.mVolume);
                this.mRcc.setPlaybackInformation(4, this.mVolumeHandling);
                this.mRcc.setPlaybackInformation(5, this.mPlaybackStream);
                this.mRcc.setPlaybackInformation(1, this.mPlaybackType);
                try {
                    MediaRouter.sStatic.mAudioService.registerRemoteVolumeObserverForRcc(this.mRcc.getRcseId(), this.mRemoteVolObserver);
                } catch (RemoteException e) {
                    Log.e(MediaRouter.TAG, "Error registering remote volume observer", e);
                }
            }
        }

        private void setPlaybackInfoOnRcc(int what, int value) {
            if (this.mRcc != null) {
                this.mRcc.setPlaybackInformation(what, value);
            }
        }
    }

    /* loaded from: MediaRouter$RouteGroup.class */
    public static class RouteGroup extends RouteInfo {
        final ArrayList<RouteInfo> mRoutes;
        private boolean mUpdateName;

        RouteGroup(RouteCategory category) {
            super(category);
            this.mRoutes = new ArrayList<>();
            this.mGroup = this;
            this.mVolumeHandling = 0;
        }

        @Override // android.media.MediaRouter.RouteInfo
        CharSequence getName(Resources res) {
            if (this.mUpdateName) {
                updateName();
            }
            return super.getName(res);
        }

        public void addRoute(RouteInfo route) {
            if (route.getGroup() != null) {
                throw new IllegalStateException("Route " + route + " is already part of a group.");
            }
            if (route.getCategory() != this.mCategory) {
                throw new IllegalArgumentException("Route cannot be added to a group with a different category. (Route category=" + route.getCategory() + " group category=" + this.mCategory + Separators.RPAREN);
            }
            int at = this.mRoutes.size();
            this.mRoutes.add(route);
            route.mGroup = this;
            this.mUpdateName = true;
            updateVolume();
            routeUpdated();
            MediaRouter.dispatchRouteGrouped(route, this, at);
        }

        public void addRoute(RouteInfo route, int insertAt) {
            if (route.getGroup() != null) {
                throw new IllegalStateException("Route " + route + " is already part of a group.");
            }
            if (route.getCategory() != this.mCategory) {
                throw new IllegalArgumentException("Route cannot be added to a group with a different category. (Route category=" + route.getCategory() + " group category=" + this.mCategory + Separators.RPAREN);
            }
            this.mRoutes.add(insertAt, route);
            route.mGroup = this;
            this.mUpdateName = true;
            updateVolume();
            routeUpdated();
            MediaRouter.dispatchRouteGrouped(route, this, insertAt);
        }

        public void removeRoute(RouteInfo route) {
            if (route.getGroup() != this) {
                throw new IllegalArgumentException("Route " + route + " is not a member of this group.");
            }
            this.mRoutes.remove(route);
            route.mGroup = null;
            this.mUpdateName = true;
            updateVolume();
            MediaRouter.dispatchRouteUngrouped(route, this);
            routeUpdated();
        }

        public void removeRoute(int index) {
            RouteInfo route = this.mRoutes.remove(index);
            route.mGroup = null;
            this.mUpdateName = true;
            updateVolume();
            MediaRouter.dispatchRouteUngrouped(route, this);
            routeUpdated();
        }

        public int getRouteCount() {
            return this.mRoutes.size();
        }

        public RouteInfo getRouteAt(int index) {
            return this.mRoutes.get(index);
        }

        public void setIconDrawable(Drawable icon) {
            this.mIcon = icon;
        }

        public void setIconResource(int resId) {
            setIconDrawable(MediaRouter.sStatic.mResources.getDrawable(resId));
        }

        @Override // android.media.MediaRouter.RouteInfo
        public void requestSetVolume(int volume) {
            int maxVol = getVolumeMax();
            if (maxVol == 0) {
                return;
            }
            float scaledVolume = volume / maxVol;
            int routeCount = getRouteCount();
            for (int i = 0; i < routeCount; i++) {
                RouteInfo route = getRouteAt(i);
                int routeVol = (int) (scaledVolume * route.getVolumeMax());
                route.requestSetVolume(routeVol);
            }
            if (volume != this.mVolume) {
                this.mVolume = volume;
                MediaRouter.dispatchRouteVolumeChanged(this);
            }
        }

        @Override // android.media.MediaRouter.RouteInfo
        public void requestUpdateVolume(int direction) {
            int maxVol = getVolumeMax();
            if (maxVol == 0) {
                return;
            }
            int routeCount = getRouteCount();
            int volume = 0;
            for (int i = 0; i < routeCount; i++) {
                RouteInfo route = getRouteAt(i);
                route.requestUpdateVolume(direction);
                int routeVol = route.getVolume();
                if (routeVol > volume) {
                    volume = routeVol;
                }
            }
            if (volume != this.mVolume) {
                this.mVolume = volume;
                MediaRouter.dispatchRouteVolumeChanged(this);
            }
        }

        void memberNameChanged(RouteInfo info, CharSequence name) {
            this.mUpdateName = true;
            routeUpdated();
        }

        void memberStatusChanged(RouteInfo info, CharSequence status) {
            setStatusInt(status);
        }

        void memberVolumeChanged(RouteInfo info) {
            updateVolume();
        }

        void updateVolume() {
            int routeCount = getRouteCount();
            int volume = 0;
            for (int i = 0; i < routeCount; i++) {
                int routeVol = getRouteAt(i).getVolume();
                if (routeVol > volume) {
                    volume = routeVol;
                }
            }
            if (volume != this.mVolume) {
                this.mVolume = volume;
                MediaRouter.dispatchRouteVolumeChanged(this);
            }
        }

        @Override // android.media.MediaRouter.RouteInfo
        void routeUpdated() {
            int types = 0;
            int count = this.mRoutes.size();
            if (count == 0) {
                MediaRouter.removeRoute(this);
                return;
            }
            int maxVolume = 0;
            boolean isLocal = true;
            boolean isFixedVolume = true;
            for (int i = 0; i < count; i++) {
                RouteInfo route = this.mRoutes.get(i);
                types |= route.mSupportedTypes;
                int routeMaxVolume = route.getVolumeMax();
                if (routeMaxVolume > maxVolume) {
                    maxVolume = routeMaxVolume;
                }
                isLocal &= route.getPlaybackType() == 0;
                isFixedVolume &= route.getVolumeHandling() == 0;
            }
            this.mPlaybackType = isLocal ? 0 : 1;
            this.mVolumeHandling = isFixedVolume ? 0 : 1;
            this.mSupportedTypes = types;
            this.mVolumeMax = maxVolume;
            this.mIcon = count == 1 ? this.mRoutes.get(0).getIconDrawable() : null;
            super.routeUpdated();
        }

        void updateName() {
            StringBuilder sb = new StringBuilder();
            int count = this.mRoutes.size();
            for (int i = 0; i < count; i++) {
                RouteInfo info = this.mRoutes.get(i);
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(info.mName);
            }
            this.mName = sb.toString();
            this.mUpdateName = false;
        }

        @Override // android.media.MediaRouter.RouteInfo
        public String toString() {
            StringBuilder sb = new StringBuilder(super.toString());
            sb.append('[');
            int count = this.mRoutes.size();
            for (int i = 0; i < count; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(this.mRoutes.get(i));
            }
            sb.append(']');
            return sb.toString();
        }
    }

    /* loaded from: MediaRouter$RouteCategory.class */
    public static class RouteCategory {
        CharSequence mName;
        int mNameResId;
        int mTypes;
        final boolean mGroupable;
        boolean mIsSystem;

        RouteCategory(CharSequence name, int types, boolean groupable) {
            this.mName = name;
            this.mTypes = types;
            this.mGroupable = groupable;
        }

        RouteCategory(int nameResId, int types, boolean groupable) {
            this.mNameResId = nameResId;
            this.mTypes = types;
            this.mGroupable = groupable;
        }

        public CharSequence getName() {
            return getName(MediaRouter.sStatic.mResources);
        }

        public CharSequence getName(Context context) {
            return getName(context.getResources());
        }

        CharSequence getName(Resources res) {
            if (this.mNameResId != 0) {
                return res.getText(this.mNameResId);
            }
            return this.mName;
        }

        public List<RouteInfo> getRoutes(List<RouteInfo> out) {
            if (out == null) {
                out = new ArrayList();
            } else {
                out.clear();
            }
            int count = MediaRouter.getRouteCountStatic();
            for (int i = 0; i < count; i++) {
                RouteInfo route = MediaRouter.getRouteAtStatic(i);
                if (route.mCategory == this) {
                    out.add(route);
                }
            }
            return out;
        }

        public int getSupportedTypes() {
            return this.mTypes;
        }

        public boolean isGroupable() {
            return this.mGroupable;
        }

        public boolean isSystem() {
            return this.mIsSystem;
        }

        public String toString() {
            return "RouteCategory{ name=" + ((Object) this.mName) + " types=" + MediaRouter.typesToString(this.mTypes) + " groupable=" + this.mGroupable + " }";
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: MediaRouter$CallbackInfo.class */
    public static class CallbackInfo {
        public int type;
        public int flags;
        public final Callback cb;
        public final MediaRouter router;

        public CallbackInfo(Callback cb, int type, int flags, MediaRouter router) {
            this.cb = cb;
            this.type = type;
            this.flags = flags;
            this.router = router;
        }

        public boolean filterRouteEvent(RouteInfo route) {
            return ((this.flags & 2) == 0 && (this.type & route.mSupportedTypes) == 0) ? false : true;
        }
    }

    /* loaded from: MediaRouter$Callback.class */
    public static abstract class Callback {
        public abstract void onRouteSelected(MediaRouter mediaRouter, int i, RouteInfo routeInfo);

        public abstract void onRouteUnselected(MediaRouter mediaRouter, int i, RouteInfo routeInfo);

        public abstract void onRouteAdded(MediaRouter mediaRouter, RouteInfo routeInfo);

        public abstract void onRouteRemoved(MediaRouter mediaRouter, RouteInfo routeInfo);

        public abstract void onRouteChanged(MediaRouter mediaRouter, RouteInfo routeInfo);

        public abstract void onRouteGrouped(MediaRouter mediaRouter, RouteInfo routeInfo, RouteGroup routeGroup, int i);

        public abstract void onRouteUngrouped(MediaRouter mediaRouter, RouteInfo routeInfo, RouteGroup routeGroup);

        public abstract void onRouteVolumeChanged(MediaRouter mediaRouter, RouteInfo routeInfo);

        public void onRoutePresentationDisplayChanged(MediaRouter router, RouteInfo info) {
        }
    }

    /* loaded from: MediaRouter$SimpleCallback.class */
    public static class SimpleCallback extends Callback {
        @Override // android.media.MediaRouter.Callback
        public void onRouteSelected(MediaRouter router, int type, RouteInfo info) {
        }

        @Override // android.media.MediaRouter.Callback
        public void onRouteUnselected(MediaRouter router, int type, RouteInfo info) {
        }

        @Override // android.media.MediaRouter.Callback
        public void onRouteAdded(MediaRouter router, RouteInfo info) {
        }

        @Override // android.media.MediaRouter.Callback
        public void onRouteRemoved(MediaRouter router, RouteInfo info) {
        }

        @Override // android.media.MediaRouter.Callback
        public void onRouteChanged(MediaRouter router, RouteInfo info) {
        }

        @Override // android.media.MediaRouter.Callback
        public void onRouteGrouped(MediaRouter router, RouteInfo info, RouteGroup group, int index) {
        }

        @Override // android.media.MediaRouter.Callback
        public void onRouteUngrouped(MediaRouter router, RouteInfo info, RouteGroup group) {
        }

        @Override // android.media.MediaRouter.Callback
        public void onRouteVolumeChanged(MediaRouter router, RouteInfo info) {
        }
    }

    /* loaded from: MediaRouter$VolumeCallbackInfo.class */
    static class VolumeCallbackInfo {
        public final VolumeCallback vcb;
        public final RouteInfo route;

        public VolumeCallbackInfo(VolumeCallback vcb, RouteInfo route) {
            this.vcb = vcb;
            this.route = route;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: MediaRouter$VolumeChangeReceiver.class */
    public static class VolumeChangeReceiver extends BroadcastReceiver {
        VolumeChangeReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(AudioManager.VOLUME_CHANGED_ACTION)) {
                int streamType = intent.getIntExtra(AudioManager.EXTRA_VOLUME_STREAM_TYPE, -1);
                if (streamType != 3) {
                    return;
                }
                int newVolume = intent.getIntExtra(AudioManager.EXTRA_VOLUME_STREAM_VALUE, 0);
                int oldVolume = intent.getIntExtra(AudioManager.EXTRA_PREV_VOLUME_STREAM_VALUE, 0);
                if (newVolume != oldVolume) {
                    MediaRouter.systemVolumeChanged(newVolume);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: MediaRouter$WifiDisplayStatusChangedReceiver.class */
    public static class WifiDisplayStatusChangedReceiver extends BroadcastReceiver {
        WifiDisplayStatusChangedReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DisplayManager.ACTION_WIFI_DISPLAY_STATUS_CHANGED)) {
                MediaRouter.updateWifiDisplayStatus((WifiDisplayStatus) intent.getParcelableExtra(DisplayManager.EXTRA_WIFI_DISPLAY_STATUS));
            }
        }
    }
}