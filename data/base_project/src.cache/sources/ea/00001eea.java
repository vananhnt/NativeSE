package com.android.server.location;

import android.content.Context;
import android.location.Address;
import android.location.Country;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Slog;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/* loaded from: LocationBasedCountryDetector.class */
public class LocationBasedCountryDetector extends CountryDetectorBase {
    private static final String TAG = "LocationBasedCountryDetector";
    private static final long QUERY_LOCATION_TIMEOUT = 300000;
    protected Timer mTimer;
    protected Thread mQueryThread;
    protected List<LocationListener> mLocationListeners;
    private LocationManager mLocationManager;
    private List<String> mEnabledProviders;

    public LocationBasedCountryDetector(Context ctx) {
        super(ctx);
        this.mLocationManager = (LocationManager) ctx.getSystemService("location");
    }

    protected String getCountryFromLocation(Location location) {
        String country = null;
        Geocoder geoCoder = new Geocoder(this.mContext);
        try {
            List<Address> addresses = geoCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && addresses.size() > 0) {
                country = addresses.get(0).getCountryCode();
            }
        } catch (IOException e) {
            Slog.w(TAG, "Exception occurs when getting country from location");
        }
        return country;
    }

    protected boolean isAcceptableProvider(String provider) {
        return LocationManager.PASSIVE_PROVIDER.equals(provider);
    }

    protected void registerListener(String provider, LocationListener listener) {
        this.mLocationManager.requestLocationUpdates(provider, 0L, 0.0f, listener);
    }

    protected void unregisterListener(LocationListener listener) {
        this.mLocationManager.removeUpdates(listener);
    }

    protected Location getLastKnownLocation() {
        List<String> providers = this.mLocationManager.getAllProviders();
        Location bestLocation = null;
        for (String provider : providers) {
            Location lastKnownLocation = this.mLocationManager.getLastKnownLocation(provider);
            if (lastKnownLocation != null && (bestLocation == null || bestLocation.getElapsedRealtimeNanos() < lastKnownLocation.getElapsedRealtimeNanos())) {
                bestLocation = lastKnownLocation;
            }
        }
        return bestLocation;
    }

    protected long getQueryLocationTimeout() {
        return QUERY_LOCATION_TIMEOUT;
    }

    protected List<String> getEnabledProviders() {
        if (this.mEnabledProviders == null) {
            this.mEnabledProviders = this.mLocationManager.getProviders(true);
        }
        return this.mEnabledProviders;
    }

    @Override // com.android.server.location.CountryDetectorBase
    public synchronized Country detectCountry() {
        if (this.mLocationListeners != null) {
            throw new IllegalStateException();
        }
        List<String> enabledProviders = getEnabledProviders();
        int totalProviders = enabledProviders.size();
        if (totalProviders > 0) {
            this.mLocationListeners = new ArrayList(totalProviders);
            for (int i = 0; i < totalProviders; i++) {
                String provider = enabledProviders.get(i);
                if (isAcceptableProvider(provider)) {
                    LocationListener listener = new LocationListener() { // from class: com.android.server.location.LocationBasedCountryDetector.1
                        @Override // android.location.LocationListener
                        public void onLocationChanged(Location location) {
                            if (location != null) {
                                LocationBasedCountryDetector.this.stop();
                                LocationBasedCountryDetector.this.queryCountryCode(location);
                            }
                        }

                        @Override // android.location.LocationListener
                        public void onProviderDisabled(String provider2) {
                        }

                        @Override // android.location.LocationListener
                        public void onProviderEnabled(String provider2) {
                        }

                        @Override // android.location.LocationListener
                        public void onStatusChanged(String provider2, int status, Bundle extras) {
                        }
                    };
                    this.mLocationListeners.add(listener);
                    registerListener(provider, listener);
                }
            }
            this.mTimer = new Timer();
            this.mTimer.schedule(new TimerTask() { // from class: com.android.server.location.LocationBasedCountryDetector.2
                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() {
                    LocationBasedCountryDetector.this.mTimer = null;
                    LocationBasedCountryDetector.this.stop();
                    LocationBasedCountryDetector.this.queryCountryCode(LocationBasedCountryDetector.this.getLastKnownLocation());
                }
            }, getQueryLocationTimeout());
        } else {
            queryCountryCode(getLastKnownLocation());
        }
        return this.mDetectedCountry;
    }

    @Override // com.android.server.location.CountryDetectorBase
    public synchronized void stop() {
        if (this.mLocationListeners != null) {
            for (LocationListener listener : this.mLocationListeners) {
                unregisterListener(listener);
            }
            this.mLocationListeners = null;
        }
        if (this.mTimer != null) {
            this.mTimer.cancel();
            this.mTimer = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void queryCountryCode(final Location location) {
        if (location == null) {
            notifyListener(null);
        } else if (this.mQueryThread != null) {
        } else {
            this.mQueryThread = new Thread(new Runnable() { // from class: com.android.server.location.LocationBasedCountryDetector.3
                @Override // java.lang.Runnable
                public void run() {
                    String countryIso = null;
                    if (location != null) {
                        countryIso = LocationBasedCountryDetector.this.getCountryFromLocation(location);
                    }
                    if (countryIso != null) {
                        LocationBasedCountryDetector.this.mDetectedCountry = new Country(countryIso, 1);
                    } else {
                        LocationBasedCountryDetector.this.mDetectedCountry = null;
                    }
                    LocationBasedCountryDetector.this.notifyListener(LocationBasedCountryDetector.this.mDetectedCountry);
                    LocationBasedCountryDetector.this.mQueryThread = null;
                }
            });
            this.mQueryThread.start();
        }
    }
}