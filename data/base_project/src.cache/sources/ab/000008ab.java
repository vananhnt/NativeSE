package android.mtp;

/* loaded from: MtpDeviceInfo.class */
public class MtpDeviceInfo {
    private String mManufacturer;
    private String mModel;
    private String mVersion;
    private String mSerialNumber;

    private MtpDeviceInfo() {
    }

    public final String getManufacturer() {
        return this.mManufacturer;
    }

    public final String getModel() {
        return this.mModel;
    }

    public final String getVersion() {
        return this.mVersion;
    }

    public final String getSerialNumber() {
        return this.mSerialNumber;
    }
}