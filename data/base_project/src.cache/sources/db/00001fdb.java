package com.android.server.usb;

import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Binder;
import android.os.Environment;
import android.os.UserHandle;
import android.provider.Telephony;
import android.util.AtomicFile;
import android.util.Slog;
import android.util.SparseBooleanArray;
import com.android.internal.content.PackageMonitor;
import com.android.internal.util.FastXmlSerializer;
import com.android.internal.util.XmlUtils;
import gov.nist.core.Separators;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: UsbSettingsManager.class */
public class UsbSettingsManager {
    private static final String TAG = "UsbSettingsManager";
    private static final boolean DEBUG = false;
    private static final File sSingleUserSettingsFile = new File("/data/system/usb_device_manager.xml");
    private final UserHandle mUser;
    private final AtomicFile mSettingsFile;
    private final Context mContext;
    private final Context mUserContext;
    private final PackageManager mPackageManager;
    private final HashMap<String, SparseBooleanArray> mDevicePermissionMap = new HashMap<>();
    private final HashMap<UsbAccessory, SparseBooleanArray> mAccessoryPermissionMap = new HashMap<>();
    private final HashMap<DeviceFilter, String> mDevicePreferenceMap = new HashMap<>();
    private final HashMap<AccessoryFilter, String> mAccessoryPreferenceMap = new HashMap<>();
    private final Object mLock = new Object();
    MyPackageMonitor mPackageMonitor = new MyPackageMonitor();

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.usb.UsbSettingsManager.upgradeSingleUserLocked():void, file: UsbSettingsManager.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private void upgradeSingleUserLocked() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.usb.UsbSettingsManager.upgradeSingleUserLocked():void, file: UsbSettingsManager.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.usb.UsbSettingsManager.upgradeSingleUserLocked():void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.usb.UsbSettingsManager.readSettingsLocked():void, file: UsbSettingsManager.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private void readSettingsLocked() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.usb.UsbSettingsManager.readSettingsLocked():void, file: UsbSettingsManager.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.usb.UsbSettingsManager.readSettingsLocked():void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.usb.UsbSettingsManager.packageMatchesLocked(android.content.pm.ResolveInfo, java.lang.String, android.hardware.usb.UsbDevice, android.hardware.usb.UsbAccessory):boolean, file: UsbSettingsManager.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private boolean packageMatchesLocked(android.content.pm.ResolveInfo r1, java.lang.String r2, android.hardware.usb.UsbDevice r3, android.hardware.usb.UsbAccessory r4) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.usb.UsbSettingsManager.packageMatchesLocked(android.content.pm.ResolveInfo, java.lang.String, android.hardware.usb.UsbDevice, android.hardware.usb.UsbAccessory):boolean, file: UsbSettingsManager.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.usb.UsbSettingsManager.packageMatchesLocked(android.content.pm.ResolveInfo, java.lang.String, android.hardware.usb.UsbDevice, android.hardware.usb.UsbAccessory):boolean");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.usb.UsbSettingsManager.handlePackageUpdateLocked(java.lang.String, android.content.pm.ActivityInfo, java.lang.String):boolean, file: UsbSettingsManager.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private boolean handlePackageUpdateLocked(java.lang.String r1, android.content.pm.ActivityInfo r2, java.lang.String r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.usb.UsbSettingsManager.handlePackageUpdateLocked(java.lang.String, android.content.pm.ActivityInfo, java.lang.String):boolean, file: UsbSettingsManager.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.usb.UsbSettingsManager.handlePackageUpdateLocked(java.lang.String, android.content.pm.ActivityInfo, java.lang.String):boolean");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.usb.UsbSettingsManager.requestPermissionDialog(android.content.Intent, java.lang.String, android.app.PendingIntent):void, file: UsbSettingsManager.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private void requestPermissionDialog(android.content.Intent r1, java.lang.String r2, android.app.PendingIntent r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.usb.UsbSettingsManager.requestPermissionDialog(android.content.Intent, java.lang.String, android.app.PendingIntent):void, file: UsbSettingsManager.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.usb.UsbSettingsManager.requestPermissionDialog(android.content.Intent, java.lang.String, android.app.PendingIntent):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: UsbSettingsManager$DeviceFilter.class */
    public static class DeviceFilter {
        public final int mVendorId;
        public final int mProductId;
        public final int mClass;
        public final int mSubclass;
        public final int mProtocol;

        public DeviceFilter(int vid, int pid, int clasz, int subclass, int protocol) {
            this.mVendorId = vid;
            this.mProductId = pid;
            this.mClass = clasz;
            this.mSubclass = subclass;
            this.mProtocol = protocol;
        }

        public DeviceFilter(UsbDevice device) {
            this.mVendorId = device.getVendorId();
            this.mProductId = device.getProductId();
            this.mClass = device.getDeviceClass();
            this.mSubclass = device.getDeviceSubclass();
            this.mProtocol = device.getDeviceProtocol();
        }

        public static DeviceFilter read(XmlPullParser parser) throws XmlPullParserException, IOException {
            int vendorId = -1;
            int productId = -1;
            int deviceClass = -1;
            int deviceSubclass = -1;
            int deviceProtocol = -1;
            int count = parser.getAttributeCount();
            for (int i = 0; i < count; i++) {
                String name = parser.getAttributeName(i);
                int value = Integer.parseInt(parser.getAttributeValue(i));
                if ("vendor-id".equals(name)) {
                    vendorId = value;
                } else if ("product-id".equals(name)) {
                    productId = value;
                } else if ("class".equals(name)) {
                    deviceClass = value;
                } else if ("subclass".equals(name)) {
                    deviceSubclass = value;
                } else if ("protocol".equals(name)) {
                    deviceProtocol = value;
                }
            }
            return new DeviceFilter(vendorId, productId, deviceClass, deviceSubclass, deviceProtocol);
        }

        public void write(XmlSerializer serializer) throws IOException {
            serializer.startTag(null, "usb-device");
            if (this.mVendorId != -1) {
                serializer.attribute(null, "vendor-id", Integer.toString(this.mVendorId));
            }
            if (this.mProductId != -1) {
                serializer.attribute(null, "product-id", Integer.toString(this.mProductId));
            }
            if (this.mClass != -1) {
                serializer.attribute(null, "class", Integer.toString(this.mClass));
            }
            if (this.mSubclass != -1) {
                serializer.attribute(null, "subclass", Integer.toString(this.mSubclass));
            }
            if (this.mProtocol != -1) {
                serializer.attribute(null, "protocol", Integer.toString(this.mProtocol));
            }
            serializer.endTag(null, "usb-device");
        }

        private boolean matches(int clasz, int subclass, int protocol) {
            return (this.mClass == -1 || clasz == this.mClass) && (this.mSubclass == -1 || subclass == this.mSubclass) && (this.mProtocol == -1 || protocol == this.mProtocol);
        }

        public boolean matches(UsbDevice device) {
            if (this.mVendorId == -1 || device.getVendorId() == this.mVendorId) {
                if (this.mProductId == -1 || device.getProductId() == this.mProductId) {
                    if (matches(device.getDeviceClass(), device.getDeviceSubclass(), device.getDeviceProtocol())) {
                        return true;
                    }
                    int count = device.getInterfaceCount();
                    for (int i = 0; i < count; i++) {
                        UsbInterface intf = device.getInterface(i);
                        if (matches(intf.getInterfaceClass(), intf.getInterfaceSubclass(), intf.getInterfaceProtocol())) {
                            return true;
                        }
                    }
                    return false;
                }
                return false;
            }
            return false;
        }

        public boolean matches(DeviceFilter f) {
            if (this.mVendorId == -1 || f.mVendorId == this.mVendorId) {
                if (this.mProductId == -1 || f.mProductId == this.mProductId) {
                    return matches(f.mClass, f.mSubclass, f.mProtocol);
                }
                return false;
            }
            return false;
        }

        public boolean equals(Object obj) {
            if (this.mVendorId == -1 || this.mProductId == -1 || this.mClass == -1 || this.mSubclass == -1 || this.mProtocol == -1) {
                return false;
            }
            if (obj instanceof DeviceFilter) {
                DeviceFilter filter = (DeviceFilter) obj;
                return filter.mVendorId == this.mVendorId && filter.mProductId == this.mProductId && filter.mClass == this.mClass && filter.mSubclass == this.mSubclass && filter.mProtocol == this.mProtocol;
            } else if (obj instanceof UsbDevice) {
                UsbDevice device = (UsbDevice) obj;
                return device.getVendorId() == this.mVendorId && device.getProductId() == this.mProductId && device.getDeviceClass() == this.mClass && device.getDeviceSubclass() == this.mSubclass && device.getDeviceProtocol() == this.mProtocol;
            } else {
                return false;
            }
        }

        public int hashCode() {
            return ((this.mVendorId << 16) | this.mProductId) ^ (((this.mClass << 16) | (this.mSubclass << 8)) | this.mProtocol);
        }

        public String toString() {
            return "DeviceFilter[mVendorId=" + this.mVendorId + ",mProductId=" + this.mProductId + ",mClass=" + this.mClass + ",mSubclass=" + this.mSubclass + ",mProtocol=" + this.mProtocol + "]";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: UsbSettingsManager$AccessoryFilter.class */
    public static class AccessoryFilter {
        public final String mManufacturer;
        public final String mModel;
        public final String mVersion;

        public AccessoryFilter(String manufacturer, String model, String version) {
            this.mManufacturer = manufacturer;
            this.mModel = model;
            this.mVersion = version;
        }

        public AccessoryFilter(UsbAccessory accessory) {
            this.mManufacturer = accessory.getManufacturer();
            this.mModel = accessory.getModel();
            this.mVersion = accessory.getVersion();
        }

        public static AccessoryFilter read(XmlPullParser parser) throws XmlPullParserException, IOException {
            String manufacturer = null;
            String model = null;
            String version = null;
            int count = parser.getAttributeCount();
            for (int i = 0; i < count; i++) {
                String name = parser.getAttributeName(i);
                String value = parser.getAttributeValue(i);
                if ("manufacturer".equals(name)) {
                    manufacturer = value;
                } else if ("model".equals(name)) {
                    model = value;
                } else if ("version".equals(name)) {
                    version = value;
                }
            }
            return new AccessoryFilter(manufacturer, model, version);
        }

        public void write(XmlSerializer serializer) throws IOException {
            serializer.startTag(null, "usb-accessory");
            if (this.mManufacturer != null) {
                serializer.attribute(null, "manufacturer", this.mManufacturer);
            }
            if (this.mModel != null) {
                serializer.attribute(null, "model", this.mModel);
            }
            if (this.mVersion != null) {
                serializer.attribute(null, "version", this.mVersion);
            }
            serializer.endTag(null, "usb-accessory");
        }

        public boolean matches(UsbAccessory acc) {
            if (this.mManufacturer == null || acc.getManufacturer().equals(this.mManufacturer)) {
                if (this.mModel == null || acc.getModel().equals(this.mModel)) {
                    return this.mVersion == null || acc.getVersion().equals(this.mVersion);
                }
                return false;
            }
            return false;
        }

        public boolean matches(AccessoryFilter f) {
            if (this.mManufacturer == null || f.mManufacturer.equals(this.mManufacturer)) {
                if (this.mModel == null || f.mModel.equals(this.mModel)) {
                    return this.mVersion == null || f.mVersion.equals(this.mVersion);
                }
                return false;
            }
            return false;
        }

        public boolean equals(Object obj) {
            if (this.mManufacturer == null || this.mModel == null || this.mVersion == null) {
                return false;
            }
            if (obj instanceof AccessoryFilter) {
                AccessoryFilter filter = (AccessoryFilter) obj;
                return this.mManufacturer.equals(filter.mManufacturer) && this.mModel.equals(filter.mModel) && this.mVersion.equals(filter.mVersion);
            } else if (obj instanceof UsbAccessory) {
                UsbAccessory accessory = (UsbAccessory) obj;
                return this.mManufacturer.equals(accessory.getManufacturer()) && this.mModel.equals(accessory.getModel()) && this.mVersion.equals(accessory.getVersion());
            } else {
                return false;
            }
        }

        public int hashCode() {
            return ((this.mManufacturer == null ? 0 : this.mManufacturer.hashCode()) ^ (this.mModel == null ? 0 : this.mModel.hashCode())) ^ (this.mVersion == null ? 0 : this.mVersion.hashCode());
        }

        public String toString() {
            return "AccessoryFilter[mManufacturer=\"" + this.mManufacturer + "\", mModel=\"" + this.mModel + "\", mVersion=\"" + this.mVersion + "\"]";
        }
    }

    /* loaded from: UsbSettingsManager$MyPackageMonitor.class */
    private class MyPackageMonitor extends PackageMonitor {
        private MyPackageMonitor() {
        }

        @Override // com.android.internal.content.PackageMonitor
        public void onPackageAdded(String packageName, int uid) {
            UsbSettingsManager.this.handlePackageUpdate(packageName);
        }

        @Override // com.android.internal.content.PackageMonitor
        public boolean onPackageChanged(String packageName, int uid, String[] components) {
            UsbSettingsManager.this.handlePackageUpdate(packageName);
            return false;
        }

        @Override // com.android.internal.content.PackageMonitor
        public void onPackageRemoved(String packageName, int uid) {
            UsbSettingsManager.this.clearDefaults(packageName);
        }
    }

    public UsbSettingsManager(Context context, UserHandle user) {
        try {
            this.mUserContext = context.createPackageContextAsUser("android", 0, user);
            this.mContext = context;
            this.mPackageManager = this.mUserContext.getPackageManager();
            this.mUser = user;
            this.mSettingsFile = new AtomicFile(new File(Environment.getUserSystemDirectory(user.getIdentifier()), "usb_device_manager.xml"));
            synchronized (this.mLock) {
                if (UserHandle.OWNER.equals(user)) {
                    upgradeSingleUserLocked();
                }
                readSettingsLocked();
            }
            this.mPackageMonitor.register(this.mUserContext, null, true);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Missing android package");
        }
    }

    private void readPreference(XmlPullParser parser) throws XmlPullParserException, IOException {
        String packageName = null;
        int count = parser.getAttributeCount();
        int i = 0;
        while (true) {
            if (i >= count) {
                break;
            } else if (!Telephony.Sms.Intents.EXTRA_PACKAGE_NAME.equals(parser.getAttributeName(i))) {
                i++;
            } else {
                packageName = parser.getAttributeValue(i);
                break;
            }
        }
        XmlUtils.nextElement(parser);
        if ("usb-device".equals(parser.getName())) {
            DeviceFilter filter = DeviceFilter.read(parser);
            this.mDevicePreferenceMap.put(filter, packageName);
        } else if ("usb-accessory".equals(parser.getName())) {
            AccessoryFilter filter2 = AccessoryFilter.read(parser);
            this.mAccessoryPreferenceMap.put(filter2, packageName);
        }
        XmlUtils.nextElement(parser);
    }

    private void writeSettingsLocked() {
        FileOutputStream fos = null;
        try {
            fos = this.mSettingsFile.startWrite();
            FastXmlSerializer serializer = new FastXmlSerializer();
            serializer.setOutput(fos, "utf-8");
            serializer.startDocument(null, true);
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            serializer.startTag(null, "settings");
            for (DeviceFilter filter : this.mDevicePreferenceMap.keySet()) {
                serializer.startTag(null, "preference");
                serializer.attribute(null, Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, this.mDevicePreferenceMap.get(filter));
                filter.write(serializer);
                serializer.endTag(null, "preference");
            }
            for (AccessoryFilter filter2 : this.mAccessoryPreferenceMap.keySet()) {
                serializer.startTag(null, "preference");
                serializer.attribute(null, Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, this.mAccessoryPreferenceMap.get(filter2));
                filter2.write(serializer);
                serializer.endTag(null, "preference");
            }
            serializer.endTag(null, "settings");
            serializer.endDocument();
            this.mSettingsFile.finishWrite(fos);
        } catch (IOException e) {
            Slog.e(TAG, "Failed to write settings", e);
            if (fos != null) {
                this.mSettingsFile.failWrite(fos);
            }
        }
    }

    private final ArrayList<ResolveInfo> getDeviceMatchesLocked(UsbDevice device, Intent intent) {
        ArrayList<ResolveInfo> matches = new ArrayList<>();
        List<ResolveInfo> resolveInfos = this.mPackageManager.queryIntentActivities(intent, 128);
        int count = resolveInfos.size();
        for (int i = 0; i < count; i++) {
            ResolveInfo resolveInfo = resolveInfos.get(i);
            if (packageMatchesLocked(resolveInfo, intent.getAction(), device, null)) {
                matches.add(resolveInfo);
            }
        }
        return matches;
    }

    private final ArrayList<ResolveInfo> getAccessoryMatchesLocked(UsbAccessory accessory, Intent intent) {
        ArrayList<ResolveInfo> matches = new ArrayList<>();
        List<ResolveInfo> resolveInfos = this.mPackageManager.queryIntentActivities(intent, 128);
        int count = resolveInfos.size();
        for (int i = 0; i < count; i++) {
            ResolveInfo resolveInfo = resolveInfos.get(i);
            if (packageMatchesLocked(resolveInfo, intent.getAction(), null, accessory)) {
                matches.add(resolveInfo);
            }
        }
        return matches;
    }

    public void deviceAttached(UsbDevice device) {
        ArrayList<ResolveInfo> matches;
        String defaultPackage;
        Intent intent = new Intent(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        intent.putExtra(UsbManager.EXTRA_DEVICE, device);
        intent.addFlags(268435456);
        synchronized (this.mLock) {
            matches = getDeviceMatchesLocked(device, intent);
            defaultPackage = this.mDevicePreferenceMap.get(new DeviceFilter(device));
        }
        this.mUserContext.sendBroadcast(intent);
        resolveActivity(intent, matches, defaultPackage, device, null);
    }

    public void deviceDetached(UsbDevice device) {
        this.mDevicePermissionMap.remove(device.getDeviceName());
        Intent intent = new Intent(UsbManager.ACTION_USB_DEVICE_DETACHED);
        intent.putExtra(UsbManager.EXTRA_DEVICE, device);
        this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
    }

    public void accessoryAttached(UsbAccessory accessory) {
        ArrayList<ResolveInfo> matches;
        String defaultPackage;
        Intent intent = new Intent(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
        intent.putExtra("accessory", accessory);
        intent.addFlags(268435456);
        synchronized (this.mLock) {
            matches = getAccessoryMatchesLocked(accessory, intent);
            defaultPackage = this.mAccessoryPreferenceMap.get(new AccessoryFilter(accessory));
        }
        resolveActivity(intent, matches, defaultPackage, null, accessory);
    }

    public void accessoryDetached(UsbAccessory accessory) {
        this.mAccessoryPermissionMap.remove(accessory);
        Intent intent = new Intent(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
        intent.putExtra("accessory", accessory);
        this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
    }

    private void resolveActivity(Intent intent, ArrayList<ResolveInfo> matches, String defaultPackage, UsbDevice device, UsbAccessory accessory) {
        String uri;
        int count = matches.size();
        if (count == 0) {
            if (accessory != null && (uri = accessory.getUri()) != null && uri.length() > 0) {
                Intent dialogIntent = new Intent();
                dialogIntent.setClassName("com.android.systemui", "com.android.systemui.usb.UsbAccessoryUriActivity");
                dialogIntent.addFlags(268435456);
                dialogIntent.putExtra("accessory", accessory);
                dialogIntent.putExtra("uri", uri);
                try {
                    this.mUserContext.startActivityAsUser(dialogIntent, this.mUser);
                    return;
                } catch (ActivityNotFoundException e) {
                    Slog.e(TAG, "unable to start UsbAccessoryUriActivity");
                    return;
                }
            }
            return;
        }
        ResolveInfo defaultRI = null;
        if (count == 1 && defaultPackage == null) {
            ResolveInfo rInfo = matches.get(0);
            if (rInfo.activityInfo != null && rInfo.activityInfo.applicationInfo != null && (rInfo.activityInfo.applicationInfo.flags & 1) != 0) {
                defaultRI = rInfo;
            }
        }
        if (defaultRI == null && defaultPackage != null) {
            int i = 0;
            while (true) {
                if (i >= count) {
                    break;
                }
                ResolveInfo rInfo2 = matches.get(i);
                if (rInfo2.activityInfo == null || !defaultPackage.equals(rInfo2.activityInfo.packageName)) {
                    i++;
                } else {
                    defaultRI = rInfo2;
                    break;
                }
            }
        }
        if (defaultRI != null) {
            if (device != null) {
                grantDevicePermission(device, defaultRI.activityInfo.applicationInfo.uid);
            } else if (accessory != null) {
                grantAccessoryPermission(accessory, defaultRI.activityInfo.applicationInfo.uid);
            }
            try {
                intent.setComponent(new ComponentName(defaultRI.activityInfo.packageName, defaultRI.activityInfo.name));
                this.mUserContext.startActivityAsUser(intent, this.mUser);
                return;
            } catch (ActivityNotFoundException e2) {
                Slog.e(TAG, "startActivity failed", e2);
                return;
            }
        }
        Intent resolverIntent = new Intent();
        resolverIntent.addFlags(268435456);
        if (count == 1) {
            resolverIntent.setClassName("com.android.systemui", "com.android.systemui.usb.UsbConfirmActivity");
            resolverIntent.putExtra("rinfo", matches.get(0));
            if (device != null) {
                resolverIntent.putExtra(UsbManager.EXTRA_DEVICE, device);
            } else {
                resolverIntent.putExtra("accessory", accessory);
            }
        } else {
            resolverIntent.setClassName("com.android.systemui", "com.android.systemui.usb.UsbResolverActivity");
            resolverIntent.putParcelableArrayListExtra("rlist", matches);
            resolverIntent.putExtra(Intent.EXTRA_INTENT, intent);
        }
        try {
            this.mUserContext.startActivityAsUser(resolverIntent, this.mUser);
        } catch (ActivityNotFoundException e3) {
            Slog.e(TAG, "unable to start activity " + resolverIntent);
        }
    }

    private boolean clearCompatibleMatchesLocked(String packageName, DeviceFilter filter) {
        boolean changed = false;
        for (DeviceFilter test : this.mDevicePreferenceMap.keySet()) {
            if (filter.matches(test)) {
                this.mDevicePreferenceMap.remove(test);
                changed = true;
            }
        }
        return changed;
    }

    private boolean clearCompatibleMatchesLocked(String packageName, AccessoryFilter filter) {
        boolean changed = false;
        for (AccessoryFilter test : this.mAccessoryPreferenceMap.keySet()) {
            if (filter.matches(test)) {
                this.mAccessoryPreferenceMap.remove(test);
                changed = true;
            }
        }
        return changed;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handlePackageUpdate(String packageName) {
        synchronized (this.mLock) {
            boolean changed = false;
            try {
                PackageInfo info = this.mPackageManager.getPackageInfo(packageName, 129);
                ActivityInfo[] activities = info.activities;
                if (activities == null) {
                    return;
                }
                for (int i = 0; i < activities.length; i++) {
                    if (handlePackageUpdateLocked(packageName, activities[i], UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                        changed = true;
                    }
                    if (handlePackageUpdateLocked(packageName, activities[i], UsbManager.ACTION_USB_ACCESSORY_ATTACHED)) {
                        changed = true;
                    }
                }
                if (changed) {
                    writeSettingsLocked();
                }
            } catch (PackageManager.NameNotFoundException e) {
                Slog.e(TAG, "handlePackageUpdate could not find package " + packageName, e);
            }
        }
    }

    public boolean hasPermission(UsbDevice device) {
        synchronized (this.mLock) {
            int uid = Binder.getCallingUid();
            if (uid == 1000) {
                return true;
            }
            SparseBooleanArray uidList = this.mDevicePermissionMap.get(device.getDeviceName());
            if (uidList == null) {
                return false;
            }
            return uidList.get(uid);
        }
    }

    public boolean hasPermission(UsbAccessory accessory) {
        synchronized (this.mLock) {
            int uid = Binder.getCallingUid();
            if (uid == 1000) {
                return true;
            }
            SparseBooleanArray uidList = this.mAccessoryPermissionMap.get(accessory);
            if (uidList == null) {
                return false;
            }
            return uidList.get(uid);
        }
    }

    public void checkPermission(UsbDevice device) {
        if (!hasPermission(device)) {
            throw new SecurityException("User has not given permission to device " + device);
        }
    }

    public void checkPermission(UsbAccessory accessory) {
        if (!hasPermission(accessory)) {
            throw new SecurityException("User has not given permission to accessory " + accessory);
        }
    }

    public void requestPermission(UsbDevice device, String packageName, PendingIntent pi) {
        Intent intent = new Intent();
        if (hasPermission(device)) {
            intent.putExtra(UsbManager.EXTRA_DEVICE, device);
            intent.putExtra(UsbManager.EXTRA_PERMISSION_GRANTED, true);
            try {
                pi.send(this.mUserContext, 0, intent);
                return;
            } catch (PendingIntent.CanceledException e) {
                return;
            }
        }
        intent.putExtra(UsbManager.EXTRA_DEVICE, device);
        requestPermissionDialog(intent, packageName, pi);
    }

    public void requestPermission(UsbAccessory accessory, String packageName, PendingIntent pi) {
        Intent intent = new Intent();
        if (hasPermission(accessory)) {
            intent.putExtra("accessory", accessory);
            intent.putExtra(UsbManager.EXTRA_PERMISSION_GRANTED, true);
            try {
                pi.send(this.mUserContext, 0, intent);
                return;
            } catch (PendingIntent.CanceledException e) {
                return;
            }
        }
        intent.putExtra("accessory", accessory);
        requestPermissionDialog(intent, packageName, pi);
    }

    public void setDevicePackage(UsbDevice device, String packageName) {
        boolean changed;
        DeviceFilter filter = new DeviceFilter(device);
        synchronized (this.mLock) {
            if (packageName == null) {
                changed = this.mDevicePreferenceMap.remove(filter) != null;
            } else {
                changed = !packageName.equals(this.mDevicePreferenceMap.get(filter));
                if (changed) {
                    this.mDevicePreferenceMap.put(filter, packageName);
                }
            }
            if (changed) {
                writeSettingsLocked();
            }
        }
    }

    public void setAccessoryPackage(UsbAccessory accessory, String packageName) {
        boolean changed;
        AccessoryFilter filter = new AccessoryFilter(accessory);
        synchronized (this.mLock) {
            if (packageName == null) {
                changed = this.mAccessoryPreferenceMap.remove(filter) != null;
            } else {
                changed = !packageName.equals(this.mAccessoryPreferenceMap.get(filter));
                if (changed) {
                    this.mAccessoryPreferenceMap.put(filter, packageName);
                }
            }
            if (changed) {
                writeSettingsLocked();
            }
        }
    }

    public void grantDevicePermission(UsbDevice device, int uid) {
        synchronized (this.mLock) {
            String deviceName = device.getDeviceName();
            SparseBooleanArray uidList = this.mDevicePermissionMap.get(deviceName);
            if (uidList == null) {
                uidList = new SparseBooleanArray(1);
                this.mDevicePermissionMap.put(deviceName, uidList);
            }
            uidList.put(uid, true);
        }
    }

    public void grantAccessoryPermission(UsbAccessory accessory, int uid) {
        synchronized (this.mLock) {
            SparseBooleanArray uidList = this.mAccessoryPermissionMap.get(accessory);
            if (uidList == null) {
                uidList = new SparseBooleanArray(1);
                this.mAccessoryPermissionMap.put(accessory, uidList);
            }
            uidList.put(uid, true);
        }
    }

    public boolean hasDefaults(String packageName) {
        synchronized (this.mLock) {
            if (this.mDevicePreferenceMap.values().contains(packageName)) {
                return true;
            }
            return this.mAccessoryPreferenceMap.values().contains(packageName);
        }
    }

    public void clearDefaults(String packageName) {
        synchronized (this.mLock) {
            if (clearPackageDefaultsLocked(packageName)) {
                writeSettingsLocked();
            }
        }
    }

    private boolean clearPackageDefaultsLocked(String packageName) {
        boolean z;
        boolean cleared = false;
        synchronized (this.mLock) {
            if (this.mDevicePreferenceMap.containsValue(packageName)) {
                Object[] keys = this.mDevicePreferenceMap.keySet().toArray();
                for (Object key : keys) {
                    if (packageName.equals(this.mDevicePreferenceMap.get(key))) {
                        this.mDevicePreferenceMap.remove(key);
                        cleared = true;
                    }
                }
            }
            if (this.mAccessoryPreferenceMap.containsValue(packageName)) {
                Object[] keys2 = this.mAccessoryPreferenceMap.keySet().toArray();
                for (Object key2 : keys2) {
                    if (packageName.equals(this.mAccessoryPreferenceMap.get(key2))) {
                        this.mAccessoryPreferenceMap.remove(key2);
                        cleared = true;
                    }
                }
            }
            z = cleared;
        }
        return z;
    }

    public void dump(FileDescriptor fd, PrintWriter pw) {
        synchronized (this.mLock) {
            pw.println("  Device permissions:");
            for (String deviceName : this.mDevicePermissionMap.keySet()) {
                pw.print("    " + deviceName + ": ");
                SparseBooleanArray uidList = this.mDevicePermissionMap.get(deviceName);
                int count = uidList.size();
                for (int i = 0; i < count; i++) {
                    pw.print(Integer.toString(uidList.keyAt(i)) + Separators.SP);
                }
                pw.println("");
            }
            pw.println("  Accessory permissions:");
            for (UsbAccessory accessory : this.mAccessoryPermissionMap.keySet()) {
                pw.print("    " + accessory + ": ");
                SparseBooleanArray uidList2 = this.mAccessoryPermissionMap.get(accessory);
                int count2 = uidList2.size();
                for (int i2 = 0; i2 < count2; i2++) {
                    pw.print(Integer.toString(uidList2.keyAt(i2)) + Separators.SP);
                }
                pw.println("");
            }
            pw.println("  Device preferences:");
            for (DeviceFilter filter : this.mDevicePreferenceMap.keySet()) {
                pw.println("    " + filter + ": " + this.mDevicePreferenceMap.get(filter));
            }
            pw.println("  Accessory preferences:");
            for (AccessoryFilter filter2 : this.mAccessoryPreferenceMap.keySet()) {
                pw.println("    " + filter2 + ": " + this.mAccessoryPreferenceMap.get(filter2));
            }
        }
    }
}