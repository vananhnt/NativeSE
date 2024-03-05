package android.hardware.usb;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: UsbAccessory.class */
public class UsbAccessory implements Parcelable {
    private static final String TAG = "UsbAccessory";
    private final String mManufacturer;
    private final String mModel;
    private final String mDescription;
    private final String mVersion;
    private final String mUri;
    private final String mSerial;
    public static final int MANUFACTURER_STRING = 0;
    public static final int MODEL_STRING = 1;
    public static final int DESCRIPTION_STRING = 2;
    public static final int VERSION_STRING = 3;
    public static final int URI_STRING = 4;
    public static final int SERIAL_STRING = 5;
    public static final Parcelable.Creator<UsbAccessory> CREATOR = new Parcelable.Creator<UsbAccessory>() { // from class: android.hardware.usb.UsbAccessory.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public UsbAccessory createFromParcel(Parcel in) {
            String manufacturer = in.readString();
            String model = in.readString();
            String description = in.readString();
            String version = in.readString();
            String uri = in.readString();
            String serial = in.readString();
            return new UsbAccessory(manufacturer, model, description, version, uri, serial);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public UsbAccessory[] newArray(int size) {
            return new UsbAccessory[size];
        }
    };

    public UsbAccessory(String manufacturer, String model, String description, String version, String uri, String serial) {
        this.mManufacturer = manufacturer;
        this.mModel = model;
        this.mDescription = description;
        this.mVersion = version;
        this.mUri = uri;
        this.mSerial = serial;
    }

    public UsbAccessory(String[] strings) {
        this.mManufacturer = strings[0];
        this.mModel = strings[1];
        this.mDescription = strings[2];
        this.mVersion = strings[3];
        this.mUri = strings[4];
        this.mSerial = strings[5];
    }

    public String getManufacturer() {
        return this.mManufacturer;
    }

    public String getModel() {
        return this.mModel;
    }

    public String getDescription() {
        return this.mDescription;
    }

    public String getVersion() {
        return this.mVersion;
    }

    public String getUri() {
        return this.mUri;
    }

    public String getSerial() {
        return this.mSerial;
    }

    private static boolean compare(String s1, String s2) {
        if (s1 == null) {
            return s2 == null;
        }
        return s1.equals(s2);
    }

    public boolean equals(Object obj) {
        if (obj instanceof UsbAccessory) {
            UsbAccessory accessory = (UsbAccessory) obj;
            return compare(this.mManufacturer, accessory.getManufacturer()) && compare(this.mModel, accessory.getModel()) && compare(this.mDescription, accessory.getDescription()) && compare(this.mVersion, accessory.getVersion()) && compare(this.mUri, accessory.getUri()) && compare(this.mSerial, accessory.getSerial());
        }
        return false;
    }

    public int hashCode() {
        return (((((this.mManufacturer == null ? 0 : this.mManufacturer.hashCode()) ^ (this.mModel == null ? 0 : this.mModel.hashCode())) ^ (this.mDescription == null ? 0 : this.mDescription.hashCode())) ^ (this.mVersion == null ? 0 : this.mVersion.hashCode())) ^ (this.mUri == null ? 0 : this.mUri.hashCode())) ^ (this.mSerial == null ? 0 : this.mSerial.hashCode());
    }

    public String toString() {
        return "UsbAccessory[mManufacturer=" + this.mManufacturer + ", mModel=" + this.mModel + ", mDescription=" + this.mDescription + ", mVersion=" + this.mVersion + ", mUri=" + this.mUri + ", mSerial=" + this.mSerial + "]";
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.mManufacturer);
        parcel.writeString(this.mModel);
        parcel.writeString(this.mDescription);
        parcel.writeString(this.mVersion);
        parcel.writeString(this.mUri);
        parcel.writeString(this.mSerial);
    }
}