package com.android.server.usb;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.IUsbManager;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.UserHandle;
import android.util.SparseArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.IndentingPrintWriter;
import gov.nist.core.Separators;
import java.io.File;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* loaded from: UsbService.class */
public class UsbService extends IUsbManager.Stub {
    private static final String TAG = "UsbService";
    private final Context mContext;
    private UsbDeviceManager mDeviceManager;
    private UsbHostManager mHostManager;
    private final Object mLock = new Object();
    @GuardedBy("mLock")
    private final SparseArray<UsbSettingsManager> mSettingsByUser = new SparseArray<>();
    private BroadcastReceiver mUserReceiver = new BroadcastReceiver() { // from class: com.android.server.usb.UsbService.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            int userId = intent.getIntExtra(Intent.EXTRA_USER_HANDLE, -1);
            String action = intent.getAction();
            if (Intent.ACTION_USER_SWITCHED.equals(action)) {
                UsbService.this.setCurrentUser(userId);
            } else if (Intent.ACTION_USER_STOPPED.equals(action)) {
                synchronized (UsbService.this.mLock) {
                    UsbService.this.mSettingsByUser.remove(userId);
                }
            }
        }
    };

    private UsbSettingsManager getSettingsForUser(int userId) {
        UsbSettingsManager usbSettingsManager;
        synchronized (this.mLock) {
            UsbSettingsManager settings = this.mSettingsByUser.get(userId);
            if (settings == null) {
                settings = new UsbSettingsManager(this.mContext, new UserHandle(userId));
                this.mSettingsByUser.put(userId, settings);
            }
            usbSettingsManager = settings;
        }
        return usbSettingsManager;
    }

    public UsbService(Context context) {
        this.mContext = context;
        PackageManager pm = this.mContext.getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_USB_HOST)) {
            this.mHostManager = new UsbHostManager(context);
        }
        if (new File("/sys/class/android_usb").exists()) {
            this.mDeviceManager = new UsbDeviceManager(context);
        }
        setCurrentUser(0);
        IntentFilter userFilter = new IntentFilter();
        userFilter.addAction(Intent.ACTION_USER_SWITCHED);
        userFilter.addAction(Intent.ACTION_USER_STOPPED);
        this.mContext.registerReceiver(this.mUserReceiver, userFilter, null, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCurrentUser(int userId) {
        UsbSettingsManager userSettings = getSettingsForUser(userId);
        if (this.mHostManager != null) {
            this.mHostManager.setCurrentSettings(userSettings);
        }
        if (this.mDeviceManager != null) {
            this.mDeviceManager.setCurrentSettings(userSettings);
        }
    }

    public void systemReady() {
        if (this.mDeviceManager != null) {
            this.mDeviceManager.systemReady();
        }
        if (this.mHostManager != null) {
            this.mHostManager.systemReady();
        }
    }

    @Override // android.hardware.usb.IUsbManager
    public void getDeviceList(Bundle devices) {
        if (this.mHostManager != null) {
            this.mHostManager.getDeviceList(devices);
        }
    }

    @Override // android.hardware.usb.IUsbManager
    public ParcelFileDescriptor openDevice(String deviceName) {
        if (this.mHostManager != null) {
            return this.mHostManager.openDevice(deviceName);
        }
        return null;
    }

    @Override // android.hardware.usb.IUsbManager
    public UsbAccessory getCurrentAccessory() {
        if (this.mDeviceManager != null) {
            return this.mDeviceManager.getCurrentAccessory();
        }
        return null;
    }

    @Override // android.hardware.usb.IUsbManager
    public ParcelFileDescriptor openAccessory(UsbAccessory accessory) {
        if (this.mDeviceManager != null) {
            return this.mDeviceManager.openAccessory(accessory);
        }
        return null;
    }

    @Override // android.hardware.usb.IUsbManager
    public void setDevicePackage(UsbDevice device, String packageName, int userId) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.MANAGE_USB, null);
        getSettingsForUser(userId).setDevicePackage(device, packageName);
    }

    @Override // android.hardware.usb.IUsbManager
    public void setAccessoryPackage(UsbAccessory accessory, String packageName, int userId) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.MANAGE_USB, null);
        getSettingsForUser(userId).setAccessoryPackage(accessory, packageName);
    }

    @Override // android.hardware.usb.IUsbManager
    public boolean hasDevicePermission(UsbDevice device) {
        int userId = UserHandle.getCallingUserId();
        return getSettingsForUser(userId).hasPermission(device);
    }

    @Override // android.hardware.usb.IUsbManager
    public boolean hasAccessoryPermission(UsbAccessory accessory) {
        int userId = UserHandle.getCallingUserId();
        return getSettingsForUser(userId).hasPermission(accessory);
    }

    @Override // android.hardware.usb.IUsbManager
    public void requestDevicePermission(UsbDevice device, String packageName, PendingIntent pi) {
        int userId = UserHandle.getCallingUserId();
        getSettingsForUser(userId).requestPermission(device, packageName, pi);
    }

    @Override // android.hardware.usb.IUsbManager
    public void requestAccessoryPermission(UsbAccessory accessory, String packageName, PendingIntent pi) {
        int userId = UserHandle.getCallingUserId();
        getSettingsForUser(userId).requestPermission(accessory, packageName, pi);
    }

    @Override // android.hardware.usb.IUsbManager
    public void grantDevicePermission(UsbDevice device, int uid) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.MANAGE_USB, null);
        int userId = UserHandle.getUserId(uid);
        getSettingsForUser(userId).grantDevicePermission(device, uid);
    }

    @Override // android.hardware.usb.IUsbManager
    public void grantAccessoryPermission(UsbAccessory accessory, int uid) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.MANAGE_USB, null);
        int userId = UserHandle.getUserId(uid);
        getSettingsForUser(userId).grantAccessoryPermission(accessory, uid);
    }

    @Override // android.hardware.usb.IUsbManager
    public boolean hasDefaults(String packageName, int userId) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.MANAGE_USB, null);
        return getSettingsForUser(userId).hasDefaults(packageName);
    }

    @Override // android.hardware.usb.IUsbManager
    public void clearDefaults(String packageName, int userId) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.MANAGE_USB, null);
        getSettingsForUser(userId).clearDefaults(packageName);
    }

    @Override // android.hardware.usb.IUsbManager
    public void setCurrentFunction(String function, boolean makeDefault) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.MANAGE_USB, null);
        if (this.mDeviceManager != null) {
            this.mDeviceManager.setCurrentFunctions(function, makeDefault);
            return;
        }
        throw new IllegalStateException("USB device mode not supported");
    }

    @Override // android.hardware.usb.IUsbManager
    public void setMassStorageBackingFile(String path) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.MANAGE_USB, null);
        if (this.mDeviceManager != null) {
            this.mDeviceManager.setMassStorageBackingFile(path);
            return;
        }
        throw new IllegalStateException("USB device mode not supported");
    }

    @Override // android.hardware.usb.IUsbManager
    public void allowUsbDebugging(boolean alwaysAllow, String publicKey) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.MANAGE_USB, null);
        this.mDeviceManager.allowUsbDebugging(alwaysAllow, publicKey);
    }

    @Override // android.hardware.usb.IUsbManager
    public void denyUsbDebugging() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.MANAGE_USB, null);
        this.mDeviceManager.denyUsbDebugging();
    }

    @Override // android.hardware.usb.IUsbManager
    public void clearUsbDebuggingKeys() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.MANAGE_USB, null);
        this.mDeviceManager.clearUsbDebuggingKeys();
    }

    @Override // android.os.Binder
    public void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.DUMP, TAG);
        IndentingPrintWriter pw = new IndentingPrintWriter(writer, "  ");
        pw.println("USB Manager State:");
        if (this.mDeviceManager != null) {
            this.mDeviceManager.dump(fd, pw);
        }
        if (this.mHostManager != null) {
            this.mHostManager.dump(fd, pw);
        }
        synchronized (this.mLock) {
            for (int i = 0; i < this.mSettingsByUser.size(); i++) {
                int userId = this.mSettingsByUser.keyAt(i);
                UsbSettingsManager settings = this.mSettingsByUser.valueAt(i);
                pw.increaseIndent();
                pw.println("Settings for user " + userId + Separators.COLON);
                settings.dump(fd, pw);
                pw.decreaseIndent();
            }
        }
        pw.decreaseIndent();
    }
}