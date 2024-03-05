package android.hardware.location;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/* loaded from: GeofenceHardwareRequestParcelable.class */
public final class GeofenceHardwareRequestParcelable implements Parcelable {
    private GeofenceHardwareRequest mRequest;
    private int mId;
    public static final Parcelable.Creator<GeofenceHardwareRequestParcelable> CREATOR = new Parcelable.Creator<GeofenceHardwareRequestParcelable>() { // from class: android.hardware.location.GeofenceHardwareRequestParcelable.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public GeofenceHardwareRequestParcelable createFromParcel(Parcel parcel) {
            int geofenceType = parcel.readInt();
            if (geofenceType != 0) {
                Log.e("GeofenceHardwareRequest", String.format("Invalid Geofence type: %d", Integer.valueOf(geofenceType)));
                return null;
            }
            GeofenceHardwareRequest request = GeofenceHardwareRequest.createCircularGeofence(parcel.readDouble(), parcel.readDouble(), parcel.readDouble());
            request.setLastTransition(parcel.readInt());
            request.setMonitorTransitions(parcel.readInt());
            request.setUnknownTimer(parcel.readInt());
            request.setNotificationResponsiveness(parcel.readInt());
            int id = parcel.readInt();
            return new GeofenceHardwareRequestParcelable(id, request);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public GeofenceHardwareRequestParcelable[] newArray(int size) {
            return new GeofenceHardwareRequestParcelable[size];
        }
    };

    public GeofenceHardwareRequestParcelable(int id, GeofenceHardwareRequest request) {
        this.mId = id;
        this.mRequest = request;
    }

    public int getId() {
        return this.mId;
    }

    public double getLatitude() {
        return this.mRequest.getLatitude();
    }

    public double getLongitude() {
        return this.mRequest.getLongitude();
    }

    public double getRadius() {
        return this.mRequest.getRadius();
    }

    public int getMonitorTransitions() {
        return this.mRequest.getMonitorTransitions();
    }

    public int getUnknownTimer() {
        return this.mRequest.getUnknownTimer();
    }

    public int getNotificationResponsiveness() {
        return this.mRequest.getNotificationResponsiveness();
    }

    public int getLastTransition() {
        return this.mRequest.getLastTransition();
    }

    int getType() {
        return this.mRequest.getType();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(getType());
        parcel.writeDouble(getLatitude());
        parcel.writeDouble(getLongitude());
        parcel.writeDouble(getRadius());
        parcel.writeInt(getLastTransition());
        parcel.writeInt(getMonitorTransitions());
        parcel.writeInt(getUnknownTimer());
        parcel.writeInt(getNotificationResponsiveness());
        parcel.writeInt(getId());
    }
}