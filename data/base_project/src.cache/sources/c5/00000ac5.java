package android.os;

import android.os.Parcelable;

/* loaded from: BatteryProperties.class */
public class BatteryProperties implements Parcelable {
    public boolean chargerAcOnline;
    public boolean chargerUsbOnline;
    public boolean chargerWirelessOnline;
    public int batteryStatus;
    public int batteryHealth;
    public boolean batteryPresent;
    public int batteryLevel;
    public int batteryVoltage;
    public int batteryCurrentNow;
    public int batteryChargeCounter;
    public int batteryTemperature;
    public String batteryTechnology;
    public static final Parcelable.Creator<BatteryProperties> CREATOR = new Parcelable.Creator<BatteryProperties>() { // from class: android.os.BatteryProperties.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BatteryProperties createFromParcel(Parcel p) {
            return new BatteryProperties(p);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BatteryProperties[] newArray(int size) {
            return new BatteryProperties[size];
        }
    };

    private BatteryProperties(Parcel p) {
        this.chargerAcOnline = p.readInt() == 1;
        this.chargerUsbOnline = p.readInt() == 1;
        this.chargerWirelessOnline = p.readInt() == 1;
        this.batteryStatus = p.readInt();
        this.batteryHealth = p.readInt();
        this.batteryPresent = p.readInt() == 1;
        this.batteryLevel = p.readInt();
        this.batteryVoltage = p.readInt();
        this.batteryCurrentNow = p.readInt();
        this.batteryChargeCounter = p.readInt();
        this.batteryTemperature = p.readInt();
        this.batteryTechnology = p.readString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel p, int flags) {
        p.writeInt(this.chargerAcOnline ? 1 : 0);
        p.writeInt(this.chargerUsbOnline ? 1 : 0);
        p.writeInt(this.chargerWirelessOnline ? 1 : 0);
        p.writeInt(this.batteryStatus);
        p.writeInt(this.batteryHealth);
        p.writeInt(this.batteryPresent ? 1 : 0);
        p.writeInt(this.batteryLevel);
        p.writeInt(this.batteryVoltage);
        p.writeInt(this.batteryCurrentNow);
        p.writeInt(this.batteryChargeCounter);
        p.writeInt(this.batteryTemperature);
        p.writeString(this.batteryTechnology);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }
}