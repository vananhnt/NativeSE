package com.android.internal.location;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: ProviderProperties.class */
public final class ProviderProperties implements Parcelable {
    public final boolean mRequiresNetwork;
    public final boolean mRequiresSatellite;
    public final boolean mRequiresCell;
    public final boolean mHasMonetaryCost;
    public final boolean mSupportsAltitude;
    public final boolean mSupportsSpeed;
    public final boolean mSupportsBearing;
    public final int mPowerRequirement;
    public final int mAccuracy;
    public static final Parcelable.Creator<ProviderProperties> CREATOR = new Parcelable.Creator<ProviderProperties>() { // from class: com.android.internal.location.ProviderProperties.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ProviderProperties createFromParcel(Parcel in) {
            boolean requiresNetwork = in.readInt() == 1;
            boolean requiresSatellite = in.readInt() == 1;
            boolean requiresCell = in.readInt() == 1;
            boolean hasMonetaryCost = in.readInt() == 1;
            boolean supportsAltitude = in.readInt() == 1;
            boolean supportsSpeed = in.readInt() == 1;
            boolean supportsBearing = in.readInt() == 1;
            int powerRequirement = in.readInt();
            int accuracy = in.readInt();
            return new ProviderProperties(requiresNetwork, requiresSatellite, requiresCell, hasMonetaryCost, supportsAltitude, supportsSpeed, supportsBearing, powerRequirement, accuracy);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ProviderProperties[] newArray(int size) {
            return new ProviderProperties[size];
        }
    };

    public ProviderProperties(boolean mRequiresNetwork, boolean mRequiresSatellite, boolean mRequiresCell, boolean mHasMonetaryCost, boolean mSupportsAltitude, boolean mSupportsSpeed, boolean mSupportsBearing, int mPowerRequirement, int mAccuracy) {
        this.mRequiresNetwork = mRequiresNetwork;
        this.mRequiresSatellite = mRequiresSatellite;
        this.mRequiresCell = mRequiresCell;
        this.mHasMonetaryCost = mHasMonetaryCost;
        this.mSupportsAltitude = mSupportsAltitude;
        this.mSupportsSpeed = mSupportsSpeed;
        this.mSupportsBearing = mSupportsBearing;
        this.mPowerRequirement = mPowerRequirement;
        this.mAccuracy = mAccuracy;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(this.mRequiresNetwork ? 1 : 0);
        parcel.writeInt(this.mRequiresSatellite ? 1 : 0);
        parcel.writeInt(this.mRequiresCell ? 1 : 0);
        parcel.writeInt(this.mHasMonetaryCost ? 1 : 0);
        parcel.writeInt(this.mSupportsAltitude ? 1 : 0);
        parcel.writeInt(this.mSupportsSpeed ? 1 : 0);
        parcel.writeInt(this.mSupportsBearing ? 1 : 0);
        parcel.writeInt(this.mPowerRequirement);
        parcel.writeInt(this.mAccuracy);
    }
}