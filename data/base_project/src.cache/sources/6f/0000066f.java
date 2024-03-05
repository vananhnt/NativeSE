package android.hardware.location;

import android.bluetooth.BluetoothInputDevice;
import android.view.Window;

/* loaded from: GeofenceHardwareRequest.class */
public final class GeofenceHardwareRequest {
    static final int GEOFENCE_TYPE_CIRCLE = 0;
    private int mType;
    private double mLatitude;
    private double mLongitude;
    private double mRadius;
    private int mLastTransition = 4;
    private int mUnknownTimer = Window.PROGRESS_SECONDARY_END;
    private int mMonitorTransitions = 7;
    private int mNotificationResponsiveness = BluetoothInputDevice.INPUT_DISCONNECT_FAILED_NOT_CONNECTED;

    private void setCircularGeofence(double latitude, double longitude, double radius) {
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mRadius = radius;
        this.mType = 0;
    }

    public static GeofenceHardwareRequest createCircularGeofence(double latitude, double longitude, double radius) {
        GeofenceHardwareRequest geofenceRequest = new GeofenceHardwareRequest();
        geofenceRequest.setCircularGeofence(latitude, longitude, radius);
        return geofenceRequest;
    }

    public void setLastTransition(int lastTransition) {
        this.mLastTransition = lastTransition;
    }

    public void setUnknownTimer(int unknownTimer) {
        this.mUnknownTimer = unknownTimer;
    }

    public void setMonitorTransitions(int monitorTransitions) {
        this.mMonitorTransitions = monitorTransitions;
    }

    public void setNotificationResponsiveness(int notificationResponsiveness) {
        this.mNotificationResponsiveness = notificationResponsiveness;
    }

    public double getLatitude() {
        return this.mLatitude;
    }

    public double getLongitude() {
        return this.mLongitude;
    }

    public double getRadius() {
        return this.mRadius;
    }

    public int getMonitorTransitions() {
        return this.mMonitorTransitions;
    }

    public int getUnknownTimer() {
        return this.mUnknownTimer;
    }

    public int getNotificationResponsiveness() {
        return this.mNotificationResponsiveness;
    }

    public int getLastTransition() {
        return this.mLastTransition;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getType() {
        return this.mType;
    }
}