package android.content.pm;

import android.accounts.GrantCredentialsPermissionActivity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Environment;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.Telephony;
import android.util.AtomicFile;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.Xml;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.FastXmlSerializer;
import com.google.android.collect.Lists;
import com.google.android.collect.Maps;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

/* loaded from: RegisteredServicesCache.class */
public abstract class RegisteredServicesCache<V> {
    private static final String TAG = "PackageManager";
    private static final boolean DEBUG = false;
    public final Context mContext;
    private final String mInterfaceName;
    private final String mMetaDataName;
    private final String mAttributesName;
    private final XmlSerializerAndParser<V> mSerializerAndParser;
    @GuardedBy("mServicesLock")
    private boolean mPersistentServicesFileDidNotExist;
    private final AtomicFile mPersistentServicesFile;
    private RegisteredServicesCacheListener<V> mListener;
    private Handler mHandler;
    private final Object mServicesLock = new Object();
    @GuardedBy("mServicesLock")
    private final SparseArray<UserServices<V>> mUserServices = new SparseArray<>(2);
    private final BroadcastReceiver mPackageReceiver = new BroadcastReceiver() { // from class: android.content.pm.RegisteredServicesCache.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            int uid = intent.getIntExtra(Intent.EXTRA_UID, -1);
            if (uid != -1) {
                RegisteredServicesCache.this.generateServicesMap(UserHandle.getUserId(uid));
            }
        }
    };
    private final BroadcastReceiver mExternalReceiver = new BroadcastReceiver() { // from class: android.content.pm.RegisteredServicesCache.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            RegisteredServicesCache.this.generateServicesMap(0);
        }
    };

    public abstract V parseServiceAttributes(Resources resources, String str, AttributeSet attributeSet);

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: RegisteredServicesCache$UserServices.class */
    public static class UserServices<V> {
        @GuardedBy("mServicesLock")
        public final Map<V, Integer> persistentServices;
        @GuardedBy("mServicesLock")
        public Map<V, ServiceInfo<V>> services;

        private UserServices() {
            this.persistentServices = Maps.newHashMap();
            this.services = null;
        }
    }

    private UserServices<V> findOrCreateUserLocked(int userId) {
        UserServices<V> services = this.mUserServices.get(userId);
        if (services == null) {
            services = new UserServices<>();
            this.mUserServices.put(userId, services);
        }
        return services;
    }

    public RegisteredServicesCache(Context context, String interfaceName, String metaDataName, String attributeName, XmlSerializerAndParser<V> serializerAndParser) {
        this.mContext = context;
        this.mInterfaceName = interfaceName;
        this.mMetaDataName = metaDataName;
        this.mAttributesName = attributeName;
        this.mSerializerAndParser = serializerAndParser;
        File dataDir = Environment.getDataDirectory();
        File systemDir = new File(dataDir, "system");
        File syncDir = new File(systemDir, "registered_services");
        this.mPersistentServicesFile = new AtomicFile(new File(syncDir, interfaceName + ".xml"));
        readPersistentServicesLocked();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME);
        this.mContext.registerReceiverAsUser(this.mPackageReceiver, UserHandle.ALL, intentFilter, null, null);
        IntentFilter sdFilter = new IntentFilter();
        sdFilter.addAction("android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE");
        sdFilter.addAction("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE");
        this.mContext.registerReceiver(this.mExternalReceiver, sdFilter);
    }

    public void invalidateCache(int userId) {
        synchronized (this.mServicesLock) {
            UserServices<V> user = findOrCreateUserLocked(userId);
            user.services = null;
        }
    }

    public void dump(FileDescriptor fd, PrintWriter fout, String[] args, int userId) {
        synchronized (this.mServicesLock) {
            UserServices<V> user = findOrCreateUserLocked(userId);
            if (user.services != null) {
                fout.println("RegisteredServicesCache: " + user.services.size() + " services");
                Iterator i$ = user.services.values().iterator();
                while (i$.hasNext()) {
                    fout.println("  " + i$.next());
                }
            } else {
                fout.println("RegisteredServicesCache: services not loaded");
            }
        }
    }

    public RegisteredServicesCacheListener<V> getListener() {
        RegisteredServicesCacheListener<V> registeredServicesCacheListener;
        synchronized (this) {
            registeredServicesCacheListener = this.mListener;
        }
        return registeredServicesCacheListener;
    }

    public void setListener(RegisteredServicesCacheListener<V> listener, Handler handler) {
        if (handler == null) {
            handler = new Handler(this.mContext.getMainLooper());
        }
        synchronized (this) {
            this.mHandler = handler;
            this.mListener = listener;
        }
    }

    private void notifyListener(final V type, final int userId, final boolean removed) {
        final RegisteredServicesCacheListener<V> listener;
        Handler handler;
        synchronized (this) {
            listener = this.mListener;
            handler = this.mHandler;
        }
        if (listener == null) {
            return;
        }
        handler.post(new Runnable() { // from class: android.content.pm.RegisteredServicesCache.3
            /* JADX WARN: Multi-variable type inference failed */
            @Override // java.lang.Runnable
            public void run() {
                listener.onServiceChanged(type, userId, removed);
            }
        });
    }

    /* loaded from: RegisteredServicesCache$ServiceInfo.class */
    public static class ServiceInfo<V> {
        public final V type;
        public final ComponentName componentName;
        public final int uid;

        public ServiceInfo(V type, ComponentName componentName, int uid) {
            this.type = type;
            this.componentName = componentName;
            this.uid = uid;
        }

        public String toString() {
            return "ServiceInfo: " + this.type + ", " + this.componentName + ", uid " + this.uid;
        }
    }

    public ServiceInfo<V> getServiceInfo(V type, int userId) {
        ServiceInfo<V> serviceInfo;
        synchronized (this.mServicesLock) {
            UserServices<V> user = findOrCreateUserLocked(userId);
            if (user.services == null) {
                generateServicesMap(userId);
            }
            serviceInfo = user.services.get(type);
        }
        return serviceInfo;
    }

    public Collection<ServiceInfo<V>> getAllServices(int userId) {
        Collection<ServiceInfo<V>> unmodifiableCollection;
        synchronized (this.mServicesLock) {
            UserServices<V> user = findOrCreateUserLocked(userId);
            if (user.services == null) {
                generateServicesMap(userId);
            }
            unmodifiableCollection = Collections.unmodifiableCollection(new ArrayList(user.services.values()));
        }
        return unmodifiableCollection;
    }

    private boolean inSystemImage(int callerUid) {
        String[] packages = this.mContext.getPackageManager().getPackagesForUid(callerUid);
        for (String name : packages) {
            try {
                PackageInfo packageInfo = this.mContext.getPackageManager().getPackageInfo(name, 0);
                if ((packageInfo.applicationInfo.flags & 1) != 0) {
                    return true;
                }
            } catch (PackageManager.NameNotFoundException e) {
                return false;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    public void generateServicesMap(int userId) {
        PackageManager pm = this.mContext.getPackageManager();
        ArrayList<ServiceInfo<V>> serviceInfos = new ArrayList<>();
        List<ResolveInfo> resolveInfos = pm.queryIntentServicesAsUser(new Intent(this.mInterfaceName), 128, userId);
        for (ResolveInfo resolveInfo : resolveInfos) {
            try {
                ServiceInfo<V> info = parseServiceInfo(resolveInfo);
                if (info == null) {
                    Log.w(TAG, "Unable to load service info " + resolveInfo.toString());
                } else {
                    serviceInfos.add(info);
                }
            } catch (IOException e) {
                Log.w(TAG, "Unable to load service info " + resolveInfo.toString(), e);
            } catch (XmlPullParserException e2) {
                Log.w(TAG, "Unable to load service info " + resolveInfo.toString(), e2);
            }
        }
        synchronized (this.mServicesLock) {
            UserServices<V> user = findOrCreateUserLocked(userId);
            boolean firstScan = user.services == null;
            if (firstScan) {
                user.services = Maps.newHashMap();
            } else {
                user.services.clear();
            }
            new StringBuilder();
            boolean changed = false;
            Iterator i$ = serviceInfos.iterator();
            while (i$.hasNext()) {
                ServiceInfo<V> info2 = i$.next();
                Integer previousUid = user.persistentServices.get(info2.type);
                if (previousUid == null) {
                    changed = true;
                    user.services.put(info2.type, info2);
                    user.persistentServices.put(info2.type, Integer.valueOf(info2.uid));
                    if (!this.mPersistentServicesFileDidNotExist || !firstScan) {
                        notifyListener(info2.type, userId, false);
                    }
                } else if (previousUid.intValue() == info2.uid) {
                    user.services.put(info2.type, info2);
                } else if (inSystemImage(info2.uid) || !containsTypeAndUid(serviceInfos, info2.type, previousUid.intValue())) {
                    changed = true;
                    user.services.put(info2.type, info2);
                    user.persistentServices.put(info2.type, Integer.valueOf(info2.uid));
                    notifyListener(info2.type, userId, false);
                }
            }
            ArrayList<V> toBeRemoved = Lists.newArrayList();
            for (V v1 : user.persistentServices.keySet()) {
                if (!containsType(serviceInfos, v1)) {
                    toBeRemoved.add(v1);
                }
            }
            Iterator i$2 = toBeRemoved.iterator();
            while (i$2.hasNext()) {
                V v12 = i$2.next();
                changed = true;
                user.persistentServices.remove(v12);
                notifyListener(v12, userId, true);
            }
            if (changed) {
                writePersistentServicesLocked();
            }
        }
    }

    private boolean containsType(ArrayList<ServiceInfo<V>> serviceInfos, V type) {
        int N = serviceInfos.size();
        for (int i = 0; i < N; i++) {
            if (serviceInfos.get(i).type.equals(type)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsTypeAndUid(ArrayList<ServiceInfo<V>> serviceInfos, V type, int uid) {
        int N = serviceInfos.size();
        for (int i = 0; i < N; i++) {
            ServiceInfo<V> serviceInfo = serviceInfos.get(i);
            if (serviceInfo.type.equals(type) && serviceInfo.uid == uid) {
                return true;
            }
        }
        return false;
    }

    private ServiceInfo<V> parseServiceInfo(ResolveInfo service) throws XmlPullParserException, IOException {
        android.content.pm.ServiceInfo si = service.serviceInfo;
        ComponentName componentName = new ComponentName(si.packageName, si.name);
        PackageManager pm = this.mContext.getPackageManager();
        XmlResourceParser parser = null;
        try {
            try {
                parser = si.loadXmlMetaData(pm, this.mMetaDataName);
                if (parser == null) {
                    throw new XmlPullParserException("No " + this.mMetaDataName + " meta-data");
                }
                AttributeSet attrs = Xml.asAttributeSet(parser);
                while (true) {
                    int type = parser.next();
                    if (type == 1 || type == 2) {
                        break;
                    }
                }
                String nodeName = parser.getName();
                if (!this.mAttributesName.equals(nodeName)) {
                    throw new XmlPullParserException("Meta-data does not start with " + this.mAttributesName + " tag");
                }
                V v = parseServiceAttributes(pm.getResourcesForApplication(si.applicationInfo), si.packageName, attrs);
                if (v == null) {
                    return null;
                }
                android.content.pm.ServiceInfo serviceInfo = service.serviceInfo;
                ApplicationInfo applicationInfo = serviceInfo.applicationInfo;
                int uid = applicationInfo.uid;
                ServiceInfo<V> serviceInfo2 = new ServiceInfo<>(v, componentName, uid);
                if (parser != null) {
                    parser.close();
                }
                return serviceInfo2;
            } catch (PackageManager.NameNotFoundException e) {
                throw new XmlPullParserException("Unable to load resources for pacakge " + si.packageName);
            }
        } finally {
            if (parser != null) {
                parser.close();
            }
        }
    }

    private void readPersistentServicesLocked() {
        this.mUserServices.clear();
        if (this.mSerializerAndParser == null) {
            return;
        }
        FileInputStream fis = null;
        try {
            try {
                this.mPersistentServicesFileDidNotExist = !this.mPersistentServicesFile.getBaseFile().exists();
                if (this.mPersistentServicesFileDidNotExist) {
                    if (0 != 0) {
                        try {
                            fis.close();
                            return;
                        } catch (IOException e) {
                            return;
                        }
                    }
                    return;
                }
                fis = this.mPersistentServicesFile.openRead();
                XmlPullParser parser = Xml.newPullParser();
                parser.setInput(fis, null);
                for (int eventType = parser.getEventType(); eventType != 2 && eventType != 1; eventType = parser.next()) {
                }
                String tagName = parser.getName();
                if ("services".equals(tagName)) {
                    int eventType2 = parser.next();
                    do {
                        if (eventType2 == 2) {
                            if (parser.getDepth() == 2) {
                                String tagName2 = parser.getName();
                                if ("service".equals(tagName2)) {
                                    V service = this.mSerializerAndParser.createFromXml(parser);
                                    if (service == null) {
                                        break;
                                    }
                                    String uidString = parser.getAttributeValue(null, GrantCredentialsPermissionActivity.EXTRAS_REQUESTING_UID);
                                    int uid = Integer.parseInt(uidString);
                                    int userId = UserHandle.getUserId(uid);
                                    UserServices<V> user = findOrCreateUserLocked(userId);
                                    user.persistentServices.put(service, Integer.valueOf(uid));
                                }
                            }
                        }
                        eventType2 = parser.next();
                    } while (eventType2 != 1);
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e2) {
                    }
                }
            } catch (Throwable th) {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e3) {
                    }
                }
                throw th;
            }
        } catch (Exception e4) {
            Log.w(TAG, "Error reading persistent services, starting from scratch", e4);
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e5) {
                }
            }
        }
    }

    private void writePersistentServicesLocked() {
        if (this.mSerializerAndParser == null) {
            return;
        }
        FileOutputStream fos = null;
        try {
            fos = this.mPersistentServicesFile.startWrite();
            XmlSerializer out = new FastXmlSerializer();
            out.setOutput(fos, "utf-8");
            out.startDocument(null, true);
            out.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            out.startTag(null, "services");
            for (int i = 0; i < this.mUserServices.size(); i++) {
                UserServices<V> user = this.mUserServices.valueAt(i);
                for (Map.Entry<V, Integer> service : user.persistentServices.entrySet()) {
                    out.startTag(null, "service");
                    out.attribute(null, GrantCredentialsPermissionActivity.EXTRAS_REQUESTING_UID, Integer.toString(service.getValue().intValue()));
                    this.mSerializerAndParser.writeAsXml(service.getKey(), out);
                    out.endTag(null, "service");
                }
            }
            out.endTag(null, "services");
            out.endDocument();
            this.mPersistentServicesFile.finishWrite(fos);
        } catch (IOException e1) {
            Log.w(TAG, "Error writing accounts", e1);
            if (fos != null) {
                this.mPersistentServicesFile.failWrite(fos);
            }
        }
    }
}