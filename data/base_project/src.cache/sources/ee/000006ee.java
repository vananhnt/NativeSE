package android.location;

import android.os.Bundle;

/* loaded from: LocationListener.class */
public interface LocationListener {
    void onLocationChanged(Location location);

    void onStatusChanged(String str, int i, Bundle bundle);

    void onProviderEnabled(String str);

    void onProviderDisabled(String str);
}