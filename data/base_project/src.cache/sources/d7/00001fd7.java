package com.android.server.usb;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.util.Slog;
import com.android.internal.R;
import com.android.internal.annotations.GuardedBy;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.HashMap;

/* loaded from: UsbHostManager.class */
public class UsbHostManager {
    private static final String TAG = UsbHostManager.class.getSimpleName();
    private static final boolean LOG = false;
    private final String[] mHostBlacklist;
    private final Context mContext;
    @GuardedBy("mLock")
    private UsbSettingsManager mCurrentSettings;
    private final HashMap<String, UsbDevice> mDevices = new HashMap<>();
    private final Object mLock = new Object();

    /* JADX INFO: Access modifiers changed from: private */
    public native void monitorUsbHostBus();

    private native ParcelFileDescriptor nativeOpenDevice(String str);

    public UsbHostManager(Context context) {
        this.mContext = context;
        this.mHostBlacklist = context.getResources().getStringArray(R.array.config_usbHostBlacklist);
    }

    public void setCurrentSettings(UsbSettingsManager settings) {
        synchronized (this.mLock) {
            this.mCurrentSettings = settings;
        }
    }

    private UsbSettingsManager getCurrentSettings() {
        UsbSettingsManager usbSettingsManager;
        synchronized (this.mLock) {
            usbSettingsManager = this.mCurrentSettings;
        }
        return usbSettingsManager;
    }

    private boolean isBlackListed(String deviceName) {
        int count = this.mHostBlacklist.length;
        for (int i = 0; i < count; i++) {
            if (deviceName.startsWith(this.mHostBlacklist[i])) {
                return true;
            }
        }
        return false;
    }

    private boolean isBlackListed(int clazz, int subClass, int protocol) {
        if (clazz == 9) {
            return true;
        }
        if (clazz == 3 && subClass == 1) {
            return true;
        }
        return false;
    }

    private void usbDeviceAdded(String deviceName, int vendorID, int productID, int deviceClass, int deviceSubclass, int deviceProtocol, int[] interfaceValues, int[] endpointValues) {
        if (isBlackListed(deviceName) || isBlackListed(deviceClass, deviceSubclass, deviceProtocol)) {
            return;
        }
        synchronized (this.mLock) {
            if (this.mDevices.get(deviceName) != null) {
                Slog.w(TAG, "device already on mDevices list: " + deviceName);
                return;
            }
            int numInterfaces = interfaceValues.length / 5;
            Parcelable[] interfaces = new UsbInterface[numInterfaces];
            int ival = 0;
            int eval = 0;
            for (int intf = 0; intf < numInterfaces; intf++) {
                try {
                    int i = ival;
                    int ival2 = ival + 1;
                    int interfaceId = interfaceValues[i];
                    int ival3 = ival2 + 1;
                    int interfaceClass = interfaceValues[ival2];
                    int ival4 = ival3 + 1;
                    int interfaceSubclass = interfaceValues[ival3];
                    int ival5 = ival4 + 1;
                    int interfaceProtocol = interfaceValues[ival4];
                    ival = ival5 + 1;
                    int numEndpoints = interfaceValues[ival5];
                    Parcelable[] endpoints = new UsbEndpoint[numEndpoints];
                    for (int endp = 0; endp < numEndpoints; endp++) {
                        int i2 = eval;
                        int eval2 = eval + 1;
                        int address = endpointValues[i2];
                        int eval3 = eval2 + 1;
                        int attributes = endpointValues[eval2];
                        int eval4 = eval3 + 1;
                        int maxPacketSize = endpointValues[eval3];
                        eval = eval4 + 1;
                        int interval = endpointValues[eval4];
                        endpoints[endp] = new UsbEndpoint(address, attributes, maxPacketSize, interval);
                    }
                    if (isBlackListed(interfaceClass, interfaceSubclass, interfaceProtocol)) {
                        return;
                    }
                    interfaces[intf] = new UsbInterface(interfaceId, interfaceClass, interfaceSubclass, interfaceProtocol, endpoints);
                } catch (Exception e) {
                    Slog.e(TAG, "error parsing USB descriptors", e);
                    return;
                }
            }
            UsbDevice device = new UsbDevice(deviceName, vendorID, productID, deviceClass, deviceSubclass, deviceProtocol, interfaces);
            this.mDevices.put(deviceName, device);
            getCurrentSettings().deviceAttached(device);
        }
    }

    private void usbDeviceRemoved(String deviceName) {
        synchronized (this.mLock) {
            UsbDevice device = this.mDevices.remove(deviceName);
            if (device != null) {
                getCurrentSettings().deviceDetached(device);
            }
        }
    }

    public void systemReady() {
        synchronized (this.mLock) {
            Runnable runnable = new Runnable() { // from class: com.android.server.usb.UsbHostManager.1
                @Override // java.lang.Runnable
                public void run() {
                    UsbHostManager.this.monitorUsbHostBus();
                }
            };
            new Thread(null, runnable, "UsbService host thread").start();
        }
    }

    public void getDeviceList(Bundle devices) {
        synchronized (this.mLock) {
            for (String name : this.mDevices.keySet()) {
                devices.putParcelable(name, this.mDevices.get(name));
            }
        }
    }

    public ParcelFileDescriptor openDevice(String deviceName) {
        ParcelFileDescriptor nativeOpenDevice;
        synchronized (this.mLock) {
            if (isBlackListed(deviceName)) {
                throw new SecurityException("USB device is on a restricted bus");
            }
            UsbDevice device = this.mDevices.get(deviceName);
            if (device == null) {
                throw new IllegalArgumentException("device " + deviceName + " does not exist or is restricted");
            }
            getCurrentSettings().checkPermission(device);
            nativeOpenDevice = nativeOpenDevice(deviceName);
        }
        return nativeOpenDevice;
    }

    public void dump(FileDescriptor fd, PrintWriter pw) {
        synchronized (this.mLock) {
            pw.println("  USB Host State:");
            for (String name : this.mDevices.keySet()) {
                pw.println("    " + name + ": " + this.mDevices.get(name));
            }
        }
    }
}