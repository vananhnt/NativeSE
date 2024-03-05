package android.net.wifi.p2p;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import gov.nist.core.Separators;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

/* loaded from: WifiP2pDeviceList.class */
public class WifiP2pDeviceList implements Parcelable {
    private final HashMap<String, WifiP2pDevice> mDevices = new HashMap<>();
    public static final Parcelable.Creator<WifiP2pDeviceList> CREATOR = new Parcelable.Creator<WifiP2pDeviceList>() { // from class: android.net.wifi.p2p.WifiP2pDeviceList.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WifiP2pDeviceList createFromParcel(Parcel in) {
            WifiP2pDeviceList deviceList = new WifiP2pDeviceList();
            int deviceCount = in.readInt();
            for (int i = 0; i < deviceCount; i++) {
                deviceList.update((WifiP2pDevice) in.readParcelable(null));
            }
            return deviceList;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WifiP2pDeviceList[] newArray(int size) {
            return new WifiP2pDeviceList[size];
        }
    };

    public WifiP2pDeviceList() {
    }

    public WifiP2pDeviceList(WifiP2pDeviceList source) {
        if (source != null) {
            for (WifiP2pDevice d : source.getDeviceList()) {
                this.mDevices.put(d.deviceAddress, new WifiP2pDevice(d));
            }
        }
    }

    public WifiP2pDeviceList(ArrayList<WifiP2pDevice> devices) {
        Iterator i$ = devices.iterator();
        while (i$.hasNext()) {
            WifiP2pDevice device = i$.next();
            if (device.deviceAddress != null) {
                this.mDevices.put(device.deviceAddress, new WifiP2pDevice(device));
            }
        }
    }

    private void validateDevice(WifiP2pDevice device) {
        if (device == null) {
            throw new IllegalArgumentException("Null device");
        }
        if (TextUtils.isEmpty(device.deviceAddress)) {
            throw new IllegalArgumentException("Empty deviceAddress");
        }
    }

    private void validateDeviceAddress(String deviceAddress) {
        if (TextUtils.isEmpty(deviceAddress)) {
            throw new IllegalArgumentException("Empty deviceAddress");
        }
    }

    public boolean clear() {
        if (this.mDevices.isEmpty()) {
            return false;
        }
        this.mDevices.clear();
        return true;
    }

    public void update(WifiP2pDevice device) {
        updateSupplicantDetails(device);
        this.mDevices.get(device.deviceAddress).status = device.status;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateSupplicantDetails(WifiP2pDevice device) {
        validateDevice(device);
        WifiP2pDevice d = this.mDevices.get(device.deviceAddress);
        if (d != null) {
            d.deviceName = device.deviceName;
            d.primaryDeviceType = device.primaryDeviceType;
            d.secondaryDeviceType = device.secondaryDeviceType;
            d.wpsConfigMethodsSupported = device.wpsConfigMethodsSupported;
            d.deviceCapability = device.deviceCapability;
            d.groupCapability = device.groupCapability;
            d.wfdInfo = device.wfdInfo;
            return;
        }
        this.mDevices.put(device.deviceAddress, device);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateGroupCapability(String deviceAddress, int groupCapab) {
        validateDeviceAddress(deviceAddress);
        WifiP2pDevice d = this.mDevices.get(deviceAddress);
        if (d != null) {
            d.groupCapability = groupCapab;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateStatus(String deviceAddress, int status) {
        validateDeviceAddress(deviceAddress);
        WifiP2pDevice d = this.mDevices.get(deviceAddress);
        if (d != null) {
            d.status = status;
        }
    }

    public WifiP2pDevice get(String deviceAddress) {
        validateDeviceAddress(deviceAddress);
        return this.mDevices.get(deviceAddress);
    }

    public boolean remove(WifiP2pDevice device) {
        validateDevice(device);
        return this.mDevices.remove(device.deviceAddress) != null;
    }

    public WifiP2pDevice remove(String deviceAddress) {
        validateDeviceAddress(deviceAddress);
        return this.mDevices.remove(deviceAddress);
    }

    public boolean remove(WifiP2pDeviceList list) {
        boolean ret = false;
        for (WifiP2pDevice d : list.mDevices.values()) {
            if (remove(d)) {
                ret = true;
            }
        }
        return ret;
    }

    public Collection<WifiP2pDevice> getDeviceList() {
        return Collections.unmodifiableCollection(this.mDevices.values());
    }

    public boolean isGroupOwner(String deviceAddress) {
        validateDeviceAddress(deviceAddress);
        WifiP2pDevice device = this.mDevices.get(deviceAddress);
        if (device == null) {
            throw new IllegalArgumentException("Device not found " + deviceAddress);
        }
        return device.isGroupOwner();
    }

    public String toString() {
        StringBuffer sbuf = new StringBuffer();
        for (WifiP2pDevice device : this.mDevices.values()) {
            sbuf.append(Separators.RETURN).append(device);
        }
        return sbuf.toString();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mDevices.size());
        for (WifiP2pDevice device : this.mDevices.values()) {
            dest.writeParcelable(device, flags);
        }
    }
}