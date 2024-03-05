package com.android.server;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.BatchedScanSettings;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.text.format.Time;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import libcore.util.Objects;

/* loaded from: TwilightService.class */
public final class TwilightService {
    private static final String TAG = "TwilightService";
    private static final boolean DEBUG = false;
    private static final String ACTION_UPDATE_TWILIGHT_STATE = "com.android.server.action.UPDATE_TWILIGHT_STATE";
    private final Context mContext;
    private final AlarmManager mAlarmManager;
    private final LocationManager mLocationManager;
    private boolean mSystemReady;
    private TwilightState mTwilightState;
    private final Object mLock = new Object();
    private final ArrayList<TwilightListenerRecord> mListeners = new ArrayList<>();
    private final BroadcastReceiver mUpdateLocationReceiver = new BroadcastReceiver() { // from class: com.android.server.TwilightService.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (!Intent.ACTION_AIRPLANE_MODE_CHANGED.equals(intent.getAction()) || intent.getBooleanExtra("state", false)) {
                TwilightService.this.mLocationHandler.requestTwilightUpdate();
            } else {
                TwilightService.this.mLocationHandler.requestLocationUpdate();
            }
        }
    };
    private final LocationListener mEmptyLocationListener = new LocationListener() { // from class: com.android.server.TwilightService.2
        @Override // android.location.LocationListener
        public void onLocationChanged(Location location) {
        }

        @Override // android.location.LocationListener
        public void onProviderDisabled(String provider) {
        }

        @Override // android.location.LocationListener
        public void onProviderEnabled(String provider) {
        }

        @Override // android.location.LocationListener
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };
    private final LocationListener mLocationListener = new LocationListener() { // from class: com.android.server.TwilightService.3
        @Override // android.location.LocationListener
        public void onLocationChanged(Location location) {
            TwilightService.this.mLocationHandler.processNewLocation(location);
        }

        @Override // android.location.LocationListener
        public void onProviderDisabled(String provider) {
        }

        @Override // android.location.LocationListener
        public void onProviderEnabled(String provider) {
        }

        @Override // android.location.LocationListener
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };
    private final LocationHandler mLocationHandler = new LocationHandler();

    /* loaded from: TwilightService$TwilightListener.class */
    public interface TwilightListener {
        void onTwilightStateChanged();
    }

    public TwilightService(Context context) {
        this.mContext = context;
        this.mAlarmManager = (AlarmManager) this.mContext.getSystemService("alarm");
        this.mLocationManager = (LocationManager) this.mContext.getSystemService("location");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void systemReady() {
        synchronized (this.mLock) {
            this.mSystemReady = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            filter.addAction(Intent.ACTION_TIME_CHANGED);
            filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
            filter.addAction(ACTION_UPDATE_TWILIGHT_STATE);
            this.mContext.registerReceiver(this.mUpdateLocationReceiver, filter);
            if (!this.mListeners.isEmpty()) {
                this.mLocationHandler.enableLocationUpdates();
            }
        }
    }

    public TwilightState getCurrentState() {
        TwilightState twilightState;
        synchronized (this.mLock) {
            twilightState = this.mTwilightState;
        }
        return twilightState;
    }

    public void registerListener(TwilightListener listener, Handler handler) {
        synchronized (this.mLock) {
            this.mListeners.add(new TwilightListenerRecord(listener, handler));
            if (this.mSystemReady && this.mListeners.size() == 1) {
                this.mLocationHandler.enableLocationUpdates();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setTwilightState(TwilightState state) {
        synchronized (this.mLock) {
            if (!Objects.equal(this.mTwilightState, state)) {
                this.mTwilightState = state;
                int count = this.mListeners.size();
                for (int i = 0; i < count; i++) {
                    this.mListeners.get(i).post();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean hasMoved(Location from, Location to) {
        if (to == null) {
            return false;
        }
        if (from == null) {
            return true;
        }
        if (to.getElapsedRealtimeNanos() < from.getElapsedRealtimeNanos()) {
            return false;
        }
        float distance = from.distanceTo(to);
        float totalAccuracy = from.getAccuracy() + to.getAccuracy();
        return distance >= totalAccuracy;
    }

    /* loaded from: TwilightService$TwilightState.class */
    public static final class TwilightState {
        private final boolean mIsNight;
        private final long mYesterdaySunset;
        private final long mTodaySunrise;
        private final long mTodaySunset;
        private final long mTomorrowSunrise;

        TwilightState(boolean isNight, long yesterdaySunset, long todaySunrise, long todaySunset, long tomorrowSunrise) {
            this.mIsNight = isNight;
            this.mYesterdaySunset = yesterdaySunset;
            this.mTodaySunrise = todaySunrise;
            this.mTodaySunset = todaySunset;
            this.mTomorrowSunrise = tomorrowSunrise;
        }

        public boolean isNight() {
            return this.mIsNight;
        }

        public long getYesterdaySunset() {
            return this.mYesterdaySunset;
        }

        public long getTodaySunrise() {
            return this.mTodaySunrise;
        }

        public long getTodaySunset() {
            return this.mTodaySunset;
        }

        public long getTomorrowSunrise() {
            return this.mTomorrowSunrise;
        }

        public boolean equals(Object o) {
            return (o instanceof TwilightState) && equals((TwilightState) o);
        }

        public boolean equals(TwilightState other) {
            return other != null && this.mIsNight == other.mIsNight && this.mYesterdaySunset == other.mYesterdaySunset && this.mTodaySunrise == other.mTodaySunrise && this.mTodaySunset == other.mTodaySunset && this.mTomorrowSunrise == other.mTomorrowSunrise;
        }

        public int hashCode() {
            return 0;
        }

        public String toString() {
            DateFormat f = DateFormat.getDateTimeInstance();
            return "{TwilightState: isNight=" + this.mIsNight + ", mYesterdaySunset=" + f.format(new Date(this.mYesterdaySunset)) + ", mTodaySunrise=" + f.format(new Date(this.mTodaySunrise)) + ", mTodaySunset=" + f.format(new Date(this.mTodaySunset)) + ", mTomorrowSunrise=" + f.format(new Date(this.mTomorrowSunrise)) + "}";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: TwilightService$TwilightListenerRecord.class */
    public static final class TwilightListenerRecord implements Runnable {
        private final TwilightListener mListener;
        private final Handler mHandler;

        public TwilightListenerRecord(TwilightListener listener, Handler handler) {
            this.mListener = listener;
            this.mHandler = handler;
        }

        public void post() {
            this.mHandler.post(this);
        }

        @Override // java.lang.Runnable
        public void run() {
            this.mListener.onTwilightStateChanged();
        }
    }

    /* loaded from: TwilightService$LocationHandler.class */
    private final class LocationHandler extends Handler {
        private static final int MSG_ENABLE_LOCATION_UPDATES = 1;
        private static final int MSG_GET_NEW_LOCATION_UPDATE = 2;
        private static final int MSG_PROCESS_NEW_LOCATION = 3;
        private static final int MSG_DO_TWILIGHT_UPDATE = 4;
        private static final long LOCATION_UPDATE_MS = 86400000;
        private static final long MIN_LOCATION_UPDATE_MS = 1800000;
        private static final float LOCATION_UPDATE_DISTANCE_METER = 20000.0f;
        private static final long LOCATION_UPDATE_ENABLE_INTERVAL_MIN = 5000;
        private static final long LOCATION_UPDATE_ENABLE_INTERVAL_MAX = 900000;
        private static final double FACTOR_GMT_OFFSET_LONGITUDE = 0.004166666666666667d;
        private boolean mPassiveListenerEnabled;
        private boolean mNetworkListenerEnabled;
        private boolean mDidFirstInit;
        private long mLastNetworkRegisterTime;
        private long mLastUpdateInterval;
        private Location mLocation;
        private final TwilightCalculator mTwilightCalculator;

        private LocationHandler() {
            this.mLastNetworkRegisterTime = -1800000L;
            this.mTwilightCalculator = new TwilightCalculator();
        }

        public void processNewLocation(Location location) {
            Message msg = obtainMessage(3, location);
            sendMessage(msg);
        }

        public void enableLocationUpdates() {
            sendEmptyMessage(1);
        }

        public void requestLocationUpdate() {
            sendEmptyMessage(2);
        }

        public void requestTwilightUpdate() {
            sendEmptyMessage(4);
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            boolean networkLocationEnabled;
            boolean passiveLocationEnabled;
            switch (msg.what) {
                case 1:
                    break;
                case 2:
                    if (!this.mNetworkListenerEnabled || this.mLastNetworkRegisterTime + 1800000 >= SystemClock.elapsedRealtime()) {
                        return;
                    }
                    this.mNetworkListenerEnabled = false;
                    TwilightService.this.mLocationManager.removeUpdates(TwilightService.this.mEmptyLocationListener);
                    break;
                    break;
                case 3:
                    Location location = (Location) msg.obj;
                    boolean hasMoved = TwilightService.hasMoved(this.mLocation, location);
                    boolean hasBetterAccuracy = this.mLocation == null || location.getAccuracy() < this.mLocation.getAccuracy();
                    if (hasMoved || hasBetterAccuracy) {
                        setLocation(location);
                        return;
                    }
                    return;
                case 4:
                    updateTwilightState();
                    return;
                default:
                    return;
            }
            try {
                networkLocationEnabled = TwilightService.this.mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception e) {
                networkLocationEnabled = false;
            }
            if (!this.mNetworkListenerEnabled && networkLocationEnabled) {
                this.mNetworkListenerEnabled = true;
                this.mLastNetworkRegisterTime = SystemClock.elapsedRealtime();
                TwilightService.this.mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 86400000L, 0.0f, TwilightService.this.mEmptyLocationListener);
                if (!this.mDidFirstInit) {
                    this.mDidFirstInit = true;
                    if (this.mLocation == null) {
                        retrieveLocation();
                    }
                }
            }
            try {
                passiveLocationEnabled = TwilightService.this.mLocationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);
            } catch (Exception e2) {
                passiveLocationEnabled = false;
            }
            if (!this.mPassiveListenerEnabled && passiveLocationEnabled) {
                this.mPassiveListenerEnabled = true;
                TwilightService.this.mLocationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0L, 20000.0f, TwilightService.this.mLocationListener);
            }
            if (!this.mNetworkListenerEnabled || !this.mPassiveListenerEnabled) {
                this.mLastUpdateInterval = (long) (this.mLastUpdateInterval * 1.5d);
                if (this.mLastUpdateInterval == 0) {
                    this.mLastUpdateInterval = 5000L;
                } else if (this.mLastUpdateInterval > 900000) {
                    this.mLastUpdateInterval = 900000L;
                }
                sendEmptyMessageDelayed(1, this.mLastUpdateInterval);
            }
        }

        private void retrieveLocation() {
            Location location = null;
            for (String str : TwilightService.this.mLocationManager.getProviders(new Criteria(), true)) {
                Location lastKnownLocation = TwilightService.this.mLocationManager.getLastKnownLocation(str);
                if (location == null || (lastKnownLocation != null && location.getElapsedRealtimeNanos() < lastKnownLocation.getElapsedRealtimeNanos())) {
                    location = lastKnownLocation;
                }
            }
            if (location == null) {
                Time currentTime = new Time();
                currentTime.set(System.currentTimeMillis());
                double lngOffset = FACTOR_GMT_OFFSET_LONGITUDE * (currentTime.gmtoff - (currentTime.isDst > 0 ? BatchedScanSettings.MAX_INTERVAL_SEC : 0));
                location = new Location("fake");
                location.setLongitude(lngOffset);
                location.setLatitude(0.0d);
                location.setAccuracy(417000.0f);
                location.setTime(System.currentTimeMillis());
                location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
            }
            setLocation(location);
        }

        private void setLocation(Location location) {
            this.mLocation = location;
            updateTwilightState();
        }

        private void updateTwilightState() {
            long nextUpdate;
            if (this.mLocation == null) {
                TwilightService.this.setTwilightState(null);
                return;
            }
            long now = System.currentTimeMillis();
            this.mTwilightCalculator.calculateTwilight(now - 86400000, this.mLocation.getLatitude(), this.mLocation.getLongitude());
            long yesterdaySunset = this.mTwilightCalculator.mSunset;
            this.mTwilightCalculator.calculateTwilight(now, this.mLocation.getLatitude(), this.mLocation.getLongitude());
            boolean isNight = this.mTwilightCalculator.mState == 1;
            long todaySunrise = this.mTwilightCalculator.mSunrise;
            long todaySunset = this.mTwilightCalculator.mSunset;
            this.mTwilightCalculator.calculateTwilight(now + 86400000, this.mLocation.getLatitude(), this.mLocation.getLongitude());
            long tomorrowSunrise = this.mTwilightCalculator.mSunrise;
            TwilightState state = new TwilightState(isNight, yesterdaySunset, todaySunrise, todaySunset, tomorrowSunrise);
            TwilightService.this.setTwilightState(state);
            if (todaySunrise == -1 || todaySunset == -1) {
                nextUpdate = now + AlarmManager.INTERVAL_HALF_DAY;
            } else {
                long nextUpdate2 = 0 + DateUtils.MINUTE_IN_MILLIS;
                if (now > todaySunset) {
                    nextUpdate = nextUpdate2 + tomorrowSunrise;
                } else if (now > todaySunrise) {
                    nextUpdate = nextUpdate2 + todaySunset;
                } else {
                    nextUpdate = nextUpdate2 + todaySunrise;
                }
            }
            Intent updateIntent = new Intent(TwilightService.ACTION_UPDATE_TWILIGHT_STATE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(TwilightService.this.mContext, 0, updateIntent, 0);
            TwilightService.this.mAlarmManager.cancel(pendingIntent);
            TwilightService.this.mAlarmManager.setExact(1, nextUpdate, pendingIntent);
        }
    }
}