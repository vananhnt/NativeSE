package android.app;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.ContainerEncryptionParams;
import android.content.pm.FeatureInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.IPackageManager;
import android.content.pm.IPackageMoveObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.InstrumentationInfo;
import android.content.pm.ManifestDigest;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ParceledListSlice;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.VerificationParams;
import android.content.pm.VerifierDeviceIdentity;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Process;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.Log;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: ApplicationPackageManager.class */
public final class ApplicationPackageManager extends PackageManager {
    private static final String TAG = "ApplicationPackageManager";
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_ICONS = false;
    int mCachedSafeMode = -1;
    private final ContextImpl mContext;
    private final IPackageManager mPM;
    private static final Object sSync = new Object();
    private static ArrayMap<ResourceName, WeakReference<Drawable.ConstantState>> sIconCache = new ArrayMap<>();
    private static ArrayMap<ResourceName, WeakReference<CharSequence>> sStringCache = new ArrayMap<>();

    @Override // android.content.pm.PackageManager
    public PackageInfo getPackageInfo(String packageName, int flags) throws PackageManager.NameNotFoundException {
        try {
            PackageInfo pi = this.mPM.getPackageInfo(packageName, flags, this.mContext.getUserId());
            if (pi != null) {
                return pi;
            }
            throw new PackageManager.NameNotFoundException(packageName);
        } catch (RemoteException e) {
            throw new RuntimeException("Package manager has died", e);
        }
    }

    @Override // android.content.pm.PackageManager
    public String[] currentToCanonicalPackageNames(String[] names) {
        try {
            return this.mPM.currentToCanonicalPackageNames(names);
        } catch (RemoteException e) {
            throw new RuntimeException("Package manager has died", e);
        }
    }

    @Override // android.content.pm.PackageManager
    public String[] canonicalToCurrentPackageNames(String[] names) {
        try {
            return this.mPM.canonicalToCurrentPackageNames(names);
        } catch (RemoteException e) {
            throw new RuntimeException("Package manager has died", e);
        }
    }

    @Override // android.content.pm.PackageManager
    public Intent getLaunchIntentForPackage(String packageName) {
        Intent intentToResolve = new Intent(Intent.ACTION_MAIN);
        intentToResolve.addCategory(Intent.CATEGORY_INFO);
        intentToResolve.setPackage(packageName);
        List<ResolveInfo> ris = queryIntentActivities(intentToResolve, 0);
        if (ris == null || ris.size() <= 0) {
            intentToResolve.removeCategory(Intent.CATEGORY_INFO);
            intentToResolve.addCategory(Intent.CATEGORY_LAUNCHER);
            intentToResolve.setPackage(packageName);
            ris = queryIntentActivities(intentToResolve, 0);
        }
        if (ris == null || ris.size() <= 0) {
            return null;
        }
        Intent intent = new Intent(intentToResolve);
        intent.setFlags(268435456);
        intent.setClassName(ris.get(0).activityInfo.packageName, ris.get(0).activityInfo.name);
        return intent;
    }

    @Override // android.content.pm.PackageManager
    public int[] getPackageGids(String packageName) throws PackageManager.NameNotFoundException {
        try {
            int[] gids = this.mPM.getPackageGids(packageName);
            if (gids != null) {
                if (gids.length <= 0) {
                    throw new PackageManager.NameNotFoundException(packageName);
                }
            }
            return gids;
        } catch (RemoteException e) {
            throw new RuntimeException("Package manager has died", e);
        }
    }

    @Override // android.content.pm.PackageManager
    public int getPackageUid(String packageName, int userHandle) throws PackageManager.NameNotFoundException {
        try {
            int uid = this.mPM.getPackageUid(packageName, userHandle);
            if (uid >= 0) {
                return uid;
            }
            throw new PackageManager.NameNotFoundException(packageName);
        } catch (RemoteException e) {
            throw new RuntimeException("Package manager has died", e);
        }
    }

    @Override // android.content.pm.PackageManager
    public PermissionInfo getPermissionInfo(String name, int flags) throws PackageManager.NameNotFoundException {
        try {
            PermissionInfo pi = this.mPM.getPermissionInfo(name, flags);
            if (pi != null) {
                return pi;
            }
            throw new PackageManager.NameNotFoundException(name);
        } catch (RemoteException e) {
            throw new RuntimeException("Package manager has died", e);
        }
    }

    @Override // android.content.pm.PackageManager
    public List<PermissionInfo> queryPermissionsByGroup(String group, int flags) throws PackageManager.NameNotFoundException {
        try {
            List<PermissionInfo> pi = this.mPM.queryPermissionsByGroup(group, flags);
            if (pi != null) {
                return pi;
            }
            throw new PackageManager.NameNotFoundException(group);
        } catch (RemoteException e) {
            throw new RuntimeException("Package manager has died", e);
        }
    }

    @Override // android.content.pm.PackageManager
    public PermissionGroupInfo getPermissionGroupInfo(String name, int flags) throws PackageManager.NameNotFoundException {
        try {
            PermissionGroupInfo pgi = this.mPM.getPermissionGroupInfo(name, flags);
            if (pgi != null) {
                return pgi;
            }
            throw new PackageManager.NameNotFoundException(name);
        } catch (RemoteException e) {
            throw new RuntimeException("Package manager has died", e);
        }
    }

    @Override // android.content.pm.PackageManager
    public List<PermissionGroupInfo> getAllPermissionGroups(int flags) {
        try {
            return this.mPM.getAllPermissionGroups(flags);
        } catch (RemoteException e) {
            throw new RuntimeException("Package manager has died", e);
        }
    }

    @Override // android.content.pm.PackageManager
    public ApplicationInfo getApplicationInfo(String packageName, int flags) throws PackageManager.NameNotFoundException {
        try {
            ApplicationInfo ai = this.mPM.getApplicationInfo(packageName, flags, this.mContext.getUserId());
            if (ai != null) {
                return ai;
            }
            throw new PackageManager.NameNotFoundException(packageName);
        } catch (RemoteException e) {
            throw new RuntimeException("Package manager has died", e);
        }
    }

    @Override // android.content.pm.PackageManager
    public ActivityInfo getActivityInfo(ComponentName className, int flags) throws PackageManager.NameNotFoundException {
        try {
            ActivityInfo ai = this.mPM.getActivityInfo(className, flags, this.mContext.getUserId());
            if (ai != null) {
                return ai;
            }
            throw new PackageManager.NameNotFoundException(className.toString());
        } catch (RemoteException e) {
            throw new RuntimeException("Package manager has died", e);
        }
    }

    @Override // android.content.pm.PackageManager
    public ActivityInfo getReceiverInfo(ComponentName className, int flags) throws PackageManager.NameNotFoundException {
        try {
            ActivityInfo ai = this.mPM.getReceiverInfo(className, flags, this.mContext.getUserId());
            if (ai != null) {
                return ai;
            }
            throw new PackageManager.NameNotFoundException(className.toString());
        } catch (RemoteException e) {
            throw new RuntimeException("Package manager has died", e);
        }
    }

    @Override // android.content.pm.PackageManager
    public ServiceInfo getServiceInfo(ComponentName className, int flags) throws PackageManager.NameNotFoundException {
        try {
            ServiceInfo si = this.mPM.getServiceInfo(className, flags, this.mContext.getUserId());
            if (si != null) {
                return si;
            }
            throw new PackageManager.NameNotFoundException(className.toString());
        } catch (RemoteException e) {
            throw new RuntimeException("Package manager has died", e);
        }
    }

    @Override // android.content.pm.PackageManager
    public ProviderInfo getProviderInfo(ComponentName className, int flags) throws PackageManager.NameNotFoundException {
        try {
            ProviderInfo pi = this.mPM.getProviderInfo(className, flags, this.mContext.getUserId());
            if (pi != null) {
                return pi;
            }
            throw new PackageManager.NameNotFoundException(className.toString());
        } catch (RemoteException e) {
            throw new RuntimeException("Package manager has died", e);
        }
    }

    @Override // android.content.pm.PackageManager
    public String[] getSystemSharedLibraryNames() {
        try {
            return this.mPM.getSystemSharedLibraryNames();
        } catch (RemoteException e) {
            throw new RuntimeException("Package manager has died", e);
        }
    }

    @Override // android.content.pm.PackageManager
    public FeatureInfo[] getSystemAvailableFeatures() {
        try {
            return this.mPM.getSystemAvailableFeatures();
        } catch (RemoteException e) {
            throw new RuntimeException("Package manager has died", e);
        }
    }

    @Override // android.content.pm.PackageManager
    public boolean hasSystemFeature(String name) {
        try {
            return this.mPM.hasSystemFeature(name);
        } catch (RemoteException e) {
            throw new RuntimeException("Package manager has died", e);
        }
    }

    @Override // android.content.pm.PackageManager
    public int checkPermission(String permName, String pkgName) {
        try {
            return this.mPM.checkPermission(permName, pkgName);
        } catch (RemoteException e) {
            throw new RuntimeException("Package manager has died", e);
        }
    }

    @Override // android.content.pm.PackageManager
    public boolean addPermission(PermissionInfo info) {
        try {
            return this.mPM.addPermission(info);
        } catch (RemoteException e) {
            throw new RuntimeException("Package manager has died", e);
        }
    }

    @Override // android.content.pm.PackageManager
    public boolean addPermissionAsync(PermissionInfo info) {
        try {
            return this.mPM.addPermissionAsync(info);
        } catch (RemoteException e) {
            throw new RuntimeException("Package manager has died", e);
        }
    }

    @Override // android.content.pm.PackageManager
    public void removePermission(String name) {
        try {
            this.mPM.removePermission(name);
        } catch (RemoteException e) {
            throw new RuntimeException("Package manager has died", e);
        }
    }

    @Override // android.content.pm.PackageManager
    public void grantPermission(String packageName, String permissionName) {
        try {
            this.mPM.grantPermission(packageName, permissionName);
        } catch (RemoteException e) {
            throw new RuntimeException("Package manager has died", e);
        }
    }

    @Override // android.content.pm.PackageManager
    public void revokePermission(String packageName, String permissionName) {
        try {
            this.mPM.revokePermission(packageName, permissionName);
        } catch (RemoteException e) {
            throw new RuntimeException("Package manager has died", e);
        }
    }

    @Override // android.content.pm.PackageManager
    public int checkSignatures(String pkg1, String pkg2) {
        try {
            return this.mPM.checkSignatures(pkg1, pkg2);
        } catch (RemoteException e) {
            throw new RuntimeException("Package manager has died", e);
        }
    }

    @Override // android.content.pm.PackageManager
    public int checkSignatures(int uid1, int uid2) {
        try {
            return this.mPM.checkUidSignatures(uid1, uid2);
        } catch (RemoteException e) {
            throw new RuntimeException("Package manager has died", e);
        }
    }

    @Override // android.content.pm.PackageManager
    public String[] getPackagesForUid(int uid) {
        try {
            return this.mPM.getPackagesForUid(uid);
        } catch (RemoteException e) {
            throw new RuntimeException("Package manager has died", e);
        }
    }

    @Override // android.content.pm.PackageManager
    public String getNameForUid(int uid) {
        try {
            return this.mPM.getNameForUid(uid);
        } catch (RemoteException e) {
            throw new RuntimeException("Package manager has died", e);
        }
    }

    @Override // android.content.pm.PackageManager
    public int getUidForSharedUser(String sharedUserName) throws PackageManager.NameNotFoundException {
        try {
            int uid = this.mPM.getUidForSharedUser(sharedUserName);
            if (uid != -1) {
                return uid;
            }
            throw new PackageManager.NameNotFoundException("No shared userid for user:" + sharedUserName);
        } catch (RemoteException e) {
            throw new RuntimeException("Package manager has died", e);
        }
    }

    @Override // android.content.pm.PackageManager
    public List<PackageInfo> getInstalledPackages(int flags) {
        return getInstalledPackages(flags, this.mContext.getUserId());
    }

    @Override // android.content.pm.PackageManager
    public List<PackageInfo> getInstalledPackages(int flags, int userId) {
        try {
            ParceledListSlice<PackageInfo> slice = this.mPM.getInstalledPackages(flags, userId);
            return slice.getList();
        } catch (RemoteException e) {
            throw new RuntimeException("Package manager has died", e);
        }
    }

    @Override // android.content.pm.PackageManager
    public List<PackageInfo> getPackagesHoldingPermissions(String[] permissions, int flags) {
        int userId = this.mContext.getUserId();
        try {
            ParceledListSlice<PackageInfo> slice = this.mPM.getPackagesHoldingPermissions(permissions, flags, userId);
            return slice.getList();
        } catch (RemoteException e) {
            throw new RuntimeException("Package manager has died", e);
        }
    }

    @Override // android.content.pm.PackageManager
    public List<ApplicationInfo> getInstalledApplications(int flags) {
        int userId = this.mContext.getUserId();
        try {
            ParceledListSlice<ApplicationInfo> slice = this.mPM.getInstalledApplications(flags, userId);
            return slice.getList();
        } catch (RemoteException e) {
            throw new RuntimeException("Package manager has died", e);
        }
    }

    @Override // android.content.pm.PackageManager
    public ResolveInfo resolveActivity(Intent intent, int flags) {
        return resolveActivityAsUser(intent, flags, this.mContext.getUserId());
    }

    @Override // android.content.pm.PackageManager
    public ResolveInfo resolveActivityAsUser(Intent intent, int flags, int userId) {
        try {
            return this.mPM.resolveIntent(intent, intent.resolveTypeIfNeeded(this.mContext.getContentResolver()), flags, userId);
        } catch (RemoteException e) {
            throw new RuntimeException("Package manager has died", e);
        }
    }

    @Override // android.content.pm.PackageManager
    public List<ResolveInfo> queryIntentActivities(Intent intent, int flags) {
        return queryIntentActivitiesAsUser(intent, flags, this.mContext.getUserId());
    }

    @Override // android.content.pm.PackageManager
    public List<ResolveInfo> queryIntentActivitiesAsUser(Intent intent, int flags, int userId) {
        try {
            return this.mPM.queryIntentActivities(intent, intent.resolveTypeIfNeeded(this.mContext.getContentResolver()), flags, userId);
        } catch (RemoteException e) {
            throw new RuntimeException("Package manager has died", e);
        }
    }

    @Override // android.content.pm.PackageManager
    public List<ResolveInfo> queryIntentActivityOptions(ComponentName caller, Intent[] specifics, Intent intent, int flags) {
        String t;
        ContentResolver resolver = this.mContext.getContentResolver();
        String[] specificTypes = null;
        if (specifics != null) {
            int N = specifics.length;
            for (int i = 0; i < N; i++) {
                Intent sp = specifics[i];
                if (sp != null && (t = sp.resolveTypeIfNeeded(resolver)) != null) {
                    if (specificTypes == null) {
                        specificTypes = new String[N];
                    }
                    specificTypes[i] = t;
                }
            }
        }
        try {
            return this.mPM.queryIntentActivityOptions(caller, specifics, specificTypes, intent, intent.resolveTypeIfNeeded(resolver), flags, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw new RuntimeException("Package manager has died", e);
        }
    }

    @Override // android.content.pm.PackageManager
    public List<ResolveInfo> queryBroadcastReceivers(Intent intent, int flags, int userId) {
        try {
            return this.mPM.queryIntentReceivers(intent, intent.resolveTypeIfNeeded(this.mContext.getContentResolver()), flags, userId);
        } catch (RemoteException e) {
            throw new RuntimeException("Package manager has died", e);
        }
    }

    @Override // android.content.pm.PackageManager
    public List<ResolveInfo> queryBroadcastReceivers(Intent intent, int flags) {
        return queryBroadcastReceivers(intent, flags, this.mContext.getUserId());
    }

    @Override // android.content.pm.PackageManager
    public ResolveInfo resolveService(Intent intent, int flags) {
        try {
            return this.mPM.resolveService(intent, intent.resolveTypeIfNeeded(this.mContext.getContentResolver()), flags, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw new RuntimeException("Package manager has died", e);
        }
    }

    @Override // android.content.pm.PackageManager
    public List<ResolveInfo> queryIntentServicesAsUser(Intent intent, int flags, int userId) {
        try {
            return this.mPM.queryIntentServices(intent, intent.resolveTypeIfNeeded(this.mContext.getContentResolver()), flags, userId);
        } catch (RemoteException e) {
            throw new RuntimeException("Package manager has died", e);
        }
    }

    @Override // android.content.pm.PackageManager
    public List<ResolveInfo> queryIntentServices(Intent intent, int flags) {
        return queryIntentServicesAsUser(intent, flags, this.mContext.getUserId());
    }

    @Override // android.content.pm.PackageManager
    public List<ResolveInfo> queryIntentContentProvidersAsUser(Intent intent, int flags, int userId) {
        try {
            return this.mPM.queryIntentContentProviders(intent, intent.resolveTypeIfNeeded(this.mContext.getContentResolver()), flags, userId);
        } catch (RemoteException e) {
            throw new RuntimeException("Package manager has died", e);
        }
    }

    @Override // android.content.pm.PackageManager
    public List<ResolveInfo> queryIntentContentProviders(Intent intent, int flags) {
        return queryIntentContentProvidersAsUser(intent, flags, this.mContext.getUserId());
    }

    @Override // android.content.pm.PackageManager
    public ProviderInfo resolveContentProvider(String name, int flags) {
        try {
            return this.mPM.resolveContentProvider(name, flags, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw new RuntimeException("Package manager has died", e);
        }
    }

    @Override // android.content.pm.PackageManager
    public List<ProviderInfo> queryContentProviders(String processName, int uid, int flags) {
        try {
            return this.mPM.queryContentProviders(processName, uid, flags);
        } catch (RemoteException e) {
            throw new RuntimeException("Package manager has died", e);
        }
    }

    @Override // android.content.pm.PackageManager
    public InstrumentationInfo getInstrumentationInfo(ComponentName className, int flags) throws PackageManager.NameNotFoundException {
        try {
            InstrumentationInfo ii = this.mPM.getInstrumentationInfo(className, flags);
            if (ii != null) {
                return ii;
            }
            throw new PackageManager.NameNotFoundException(className.toString());
        } catch (RemoteException e) {
            throw new RuntimeException("Package manager has died", e);
        }
    }

    @Override // android.content.pm.PackageManager
    public List<InstrumentationInfo> queryInstrumentation(String targetPackage, int flags) {
        try {
            return this.mPM.queryInstrumentation(targetPackage, flags);
        } catch (RemoteException e) {
            throw new RuntimeException("Package manager has died", e);
        }
    }

    @Override // android.content.pm.PackageManager
    public Drawable getDrawable(String packageName, int resid, ApplicationInfo appInfo) {
        ResourceName name = new ResourceName(packageName, resid);
        Drawable dr = getCachedIcon(name);
        if (dr != null) {
            return dr;
        }
        if (appInfo == null) {
            try {
                appInfo = getApplicationInfo(packageName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                return null;
            }
        }
        try {
            Resources r = getResourcesForApplication(appInfo);
            Drawable dr2 = r.getDrawable(resid);
            putCachedIcon(name, dr2);
            return dr2;
        } catch (PackageManager.NameNotFoundException e2) {
            Log.w("PackageManager", "Failure retrieving resources for" + appInfo.packageName);
            return null;
        } catch (Resources.NotFoundException e3) {
            Log.w("PackageManager", "Failure retrieving resources for" + appInfo.packageName + ": " + e3.getMessage());
            return null;
        } catch (RuntimeException e4) {
            Log.w("PackageManager", "Failure retrieving icon 0x" + Integer.toHexString(resid) + " in package " + packageName, e4);
            return null;
        }
    }

    @Override // android.content.pm.PackageManager
    public Drawable getActivityIcon(ComponentName activityName) throws PackageManager.NameNotFoundException {
        return getActivityInfo(activityName, 0).loadIcon(this);
    }

    @Override // android.content.pm.PackageManager
    public Drawable getActivityIcon(Intent intent) throws PackageManager.NameNotFoundException {
        if (intent.getComponent() != null) {
            return getActivityIcon(intent.getComponent());
        }
        ResolveInfo info = resolveActivity(intent, 65536);
        if (info != null) {
            return info.activityInfo.loadIcon(this);
        }
        throw new PackageManager.NameNotFoundException(intent.toUri(0));
    }

    @Override // android.content.pm.PackageManager
    public Drawable getDefaultActivityIcon() {
        return Resources.getSystem().getDrawable(17301651);
    }

    @Override // android.content.pm.PackageManager
    public Drawable getApplicationIcon(ApplicationInfo info) {
        return info.loadIcon(this);
    }

    @Override // android.content.pm.PackageManager
    public Drawable getApplicationIcon(String packageName) throws PackageManager.NameNotFoundException {
        return getApplicationIcon(getApplicationInfo(packageName, 0));
    }

    @Override // android.content.pm.PackageManager
    public Drawable getActivityLogo(ComponentName activityName) throws PackageManager.NameNotFoundException {
        return getActivityInfo(activityName, 0).loadLogo(this);
    }

    @Override // android.content.pm.PackageManager
    public Drawable getActivityLogo(Intent intent) throws PackageManager.NameNotFoundException {
        if (intent.getComponent() != null) {
            return getActivityLogo(intent.getComponent());
        }
        ResolveInfo info = resolveActivity(intent, 65536);
        if (info != null) {
            return info.activityInfo.loadLogo(this);
        }
        throw new PackageManager.NameNotFoundException(intent.toUri(0));
    }

    @Override // android.content.pm.PackageManager
    public Drawable getApplicationLogo(ApplicationInfo info) {
        return info.loadLogo(this);
    }

    @Override // android.content.pm.PackageManager
    public Drawable getApplicationLogo(String packageName) throws PackageManager.NameNotFoundException {
        return getApplicationLogo(getApplicationInfo(packageName, 0));
    }

    @Override // android.content.pm.PackageManager
    public Resources getResourcesForActivity(ComponentName activityName) throws PackageManager.NameNotFoundException {
        return getResourcesForApplication(getActivityInfo(activityName, 0).applicationInfo);
    }

    @Override // android.content.pm.PackageManager
    public Resources getResourcesForApplication(ApplicationInfo app) throws PackageManager.NameNotFoundException {
        if (app.packageName.equals("system")) {
            return this.mContext.mMainThread.getSystemContext().getResources();
        }
        Resources r = this.mContext.mMainThread.getTopLevelResources(app.uid == Process.myUid() ? app.sourceDir : app.publicSourceDir, 0, null, this.mContext.mPackageInfo);
        if (r != null) {
            return r;
        }
        throw new PackageManager.NameNotFoundException("Unable to open " + app.publicSourceDir);
    }

    @Override // android.content.pm.PackageManager
    public Resources getResourcesForApplication(String appPackageName) throws PackageManager.NameNotFoundException {
        return getResourcesForApplication(getApplicationInfo(appPackageName, 0));
    }

    @Override // android.content.pm.PackageManager
    public Resources getResourcesForApplicationAsUser(String appPackageName, int userId) throws PackageManager.NameNotFoundException {
        if (userId < 0) {
            throw new IllegalArgumentException("Call does not support special user #" + userId);
        }
        if ("system".equals(appPackageName)) {
            return this.mContext.mMainThread.getSystemContext().getResources();
        }
        try {
            ApplicationInfo ai = this.mPM.getApplicationInfo(appPackageName, 0, userId);
            if (ai != null) {
                return getResourcesForApplication(ai);
            }
            throw new PackageManager.NameNotFoundException("Package " + appPackageName + " doesn't exist");
        } catch (RemoteException e) {
            throw new RuntimeException("Package manager has died", e);
        }
    }

    @Override // android.content.pm.PackageManager
    public boolean isSafeMode() {
        try {
            if (this.mCachedSafeMode < 0) {
                this.mCachedSafeMode = this.mPM.isSafeMode() ? 1 : 0;
            }
            return this.mCachedSafeMode != 0;
        } catch (RemoteException e) {
            throw new RuntimeException("Package manager has died", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void configurationChanged() {
        synchronized (sSync) {
            sIconCache.clear();
            sStringCache.clear();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ApplicationPackageManager(ContextImpl context, IPackageManager pm) {
        this.mContext = context;
        this.mPM = pm;
    }

    private Drawable getCachedIcon(ResourceName name) {
        synchronized (sSync) {
            WeakReference<Drawable.ConstantState> wr = sIconCache.get(name);
            if (wr != null) {
                Drawable.ConstantState state = wr.get();
                if (state != null) {
                    return state.newDrawable();
                }
                sIconCache.remove(name);
            }
            return null;
        }
    }

    private void putCachedIcon(ResourceName name, Drawable dr) {
        synchronized (sSync) {
            sIconCache.put(name, new WeakReference<>(dr.getConstantState()));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void handlePackageBroadcast(int cmd, String[] pkgList, boolean hasPkgInfo) {
        boolean immediateGc = false;
        if (cmd == 1) {
            immediateGc = true;
        }
        if (pkgList != null && pkgList.length > 0) {
            boolean needCleanup = false;
            for (String ssp : pkgList) {
                synchronized (sSync) {
                    for (int i = sIconCache.size() - 1; i >= 0; i--) {
                        ResourceName nm = sIconCache.keyAt(i);
                        if (nm.packageName.equals(ssp)) {
                            sIconCache.removeAt(i);
                            needCleanup = true;
                        }
                    }
                    for (int i2 = sStringCache.size() - 1; i2 >= 0; i2--) {
                        ResourceName nm2 = sStringCache.keyAt(i2);
                        if (nm2.packageName.equals(ssp)) {
                            sStringCache.removeAt(i2);
                            needCleanup = true;
                        }
                    }
                }
            }
            if (needCleanup || hasPkgInfo) {
                if (immediateGc) {
                    Runtime.getRuntime().gc();
                } else {
                    ActivityThread.currentActivityThread().scheduleGcIdler();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ApplicationPackageManager$ResourceName.class */
    public static final class ResourceName {
        final String packageName;
        final int iconId;

        ResourceName(String _packageName, int _iconId) {
            this.packageName = _packageName;
            this.iconId = _iconId;
        }

        ResourceName(ApplicationInfo aInfo, int _iconId) {
            this(aInfo.packageName, _iconId);
        }

        ResourceName(ComponentInfo cInfo, int _iconId) {
            this(cInfo.applicationInfo.packageName, _iconId);
        }

        ResourceName(ResolveInfo rInfo, int _iconId) {
            this(rInfo.activityInfo.applicationInfo.packageName, _iconId);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ResourceName that = (ResourceName) o;
            if (this.iconId != that.iconId) {
                return false;
            }
            return this.packageName == null ? that.packageName == null : this.packageName.equals(that.packageName);
        }

        public int hashCode() {
            int result = this.packageName.hashCode();
            return (31 * result) + this.iconId;
        }

        public String toString() {
            return "{ResourceName " + this.packageName + " / " + this.iconId + "}";
        }
    }

    private CharSequence getCachedString(ResourceName name) {
        synchronized (sSync) {
            WeakReference<CharSequence> wr = sStringCache.get(name);
            if (wr != null) {
                CharSequence cs = wr.get();
                if (cs != null) {
                    return cs;
                }
                sStringCache.remove(name);
            }
            return null;
        }
    }

    private void putCachedString(ResourceName name, CharSequence cs) {
        synchronized (sSync) {
            sStringCache.put(name, new WeakReference<>(cs));
        }
    }

    @Override // android.content.pm.PackageManager
    public CharSequence getText(String packageName, int resid, ApplicationInfo appInfo) {
        ResourceName name = new ResourceName(packageName, resid);
        CharSequence text = getCachedString(name);
        if (text != null) {
            return text;
        }
        if (appInfo == null) {
            try {
                appInfo = getApplicationInfo(packageName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                return null;
            }
        }
        try {
            Resources r = getResourcesForApplication(appInfo);
            CharSequence text2 = r.getText(resid);
            putCachedString(name, text2);
            return text2;
        } catch (PackageManager.NameNotFoundException e2) {
            Log.w("PackageManager", "Failure retrieving resources for" + appInfo.packageName);
            return null;
        } catch (RuntimeException e3) {
            Log.w("PackageManager", "Failure retrieving text 0x" + Integer.toHexString(resid) + " in package " + packageName, e3);
            return null;
        }
    }

    @Override // android.content.pm.PackageManager
    public XmlResourceParser getXml(String packageName, int resid, ApplicationInfo appInfo) {
        if (appInfo == null) {
            try {
                appInfo = getApplicationInfo(packageName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                return null;
            }
        }
        try {
            Resources r = getResourcesForApplication(appInfo);
            return r.getXml(resid);
        } catch (PackageManager.NameNotFoundException e2) {
            Log.w("PackageManager", "Failure retrieving resources for " + appInfo.packageName);
            return null;
        } catch (RuntimeException e3) {
            Log.w("PackageManager", "Failure retrieving xml 0x" + Integer.toHexString(resid) + " in package " + packageName, e3);
            return null;
        }
    }

    @Override // android.content.pm.PackageManager
    public CharSequence getApplicationLabel(ApplicationInfo info) {
        return info.loadLabel(this);
    }

    @Override // android.content.pm.PackageManager
    public void installPackage(Uri packageURI, IPackageInstallObserver observer, int flags, String installerPackageName) {
        try {
            this.mPM.installPackage(packageURI, observer, flags, installerPackageName);
        } catch (RemoteException e) {
        }
    }

    @Override // android.content.pm.PackageManager
    public void installPackageWithVerification(Uri packageURI, IPackageInstallObserver observer, int flags, String installerPackageName, Uri verificationURI, ManifestDigest manifestDigest, ContainerEncryptionParams encryptionParams) {
        try {
            this.mPM.installPackageWithVerification(packageURI, observer, flags, installerPackageName, verificationURI, manifestDigest, encryptionParams);
        } catch (RemoteException e) {
        }
    }

    @Override // android.content.pm.PackageManager
    public void installPackageWithVerificationAndEncryption(Uri packageURI, IPackageInstallObserver observer, int flags, String installerPackageName, VerificationParams verificationParams, ContainerEncryptionParams encryptionParams) {
        try {
            this.mPM.installPackageWithVerificationAndEncryption(packageURI, observer, flags, installerPackageName, verificationParams, encryptionParams);
        } catch (RemoteException e) {
        }
    }

    @Override // android.content.pm.PackageManager
    public int installExistingPackage(String packageName) throws PackageManager.NameNotFoundException {
        try {
            int res = this.mPM.installExistingPackageAsUser(packageName, UserHandle.myUserId());
            if (res == -3) {
                throw new PackageManager.NameNotFoundException("Package " + packageName + " doesn't exist");
            }
            return res;
        } catch (RemoteException e) {
            throw new PackageManager.NameNotFoundException("Package " + packageName + " doesn't exist");
        }
    }

    @Override // android.content.pm.PackageManager
    public void verifyPendingInstall(int id, int response) {
        try {
            this.mPM.verifyPendingInstall(id, response);
        } catch (RemoteException e) {
        }
    }

    @Override // android.content.pm.PackageManager
    public void extendVerificationTimeout(int id, int verificationCodeAtTimeout, long millisecondsToDelay) {
        try {
            this.mPM.extendVerificationTimeout(id, verificationCodeAtTimeout, millisecondsToDelay);
        } catch (RemoteException e) {
        }
    }

    @Override // android.content.pm.PackageManager
    public void setInstallerPackageName(String targetPackage, String installerPackageName) {
        try {
            this.mPM.setInstallerPackageName(targetPackage, installerPackageName);
        } catch (RemoteException e) {
        }
    }

    @Override // android.content.pm.PackageManager
    public void movePackage(String packageName, IPackageMoveObserver observer, int flags) {
        try {
            this.mPM.movePackage(packageName, observer, flags);
        } catch (RemoteException e) {
        }
    }

    @Override // android.content.pm.PackageManager
    public String getInstallerPackageName(String packageName) {
        try {
            return this.mPM.getInstallerPackageName(packageName);
        } catch (RemoteException e) {
            return null;
        }
    }

    @Override // android.content.pm.PackageManager
    public void deletePackage(String packageName, IPackageDeleteObserver observer, int flags) {
        try {
            this.mPM.deletePackageAsUser(packageName, observer, UserHandle.myUserId(), flags);
        } catch (RemoteException e) {
        }
    }

    @Override // android.content.pm.PackageManager
    public void clearApplicationUserData(String packageName, IPackageDataObserver observer) {
        try {
            this.mPM.clearApplicationUserData(packageName, observer, this.mContext.getUserId());
        } catch (RemoteException e) {
        }
    }

    @Override // android.content.pm.PackageManager
    public void deleteApplicationCacheFiles(String packageName, IPackageDataObserver observer) {
        try {
            this.mPM.deleteApplicationCacheFiles(packageName, observer);
        } catch (RemoteException e) {
        }
    }

    @Override // android.content.pm.PackageManager
    public void freeStorageAndNotify(long idealStorageSize, IPackageDataObserver observer) {
        try {
            this.mPM.freeStorageAndNotify(idealStorageSize, observer);
        } catch (RemoteException e) {
        }
    }

    @Override // android.content.pm.PackageManager
    public void freeStorage(long freeStorageSize, IntentSender pi) {
        try {
            this.mPM.freeStorage(freeStorageSize, pi);
        } catch (RemoteException e) {
        }
    }

    @Override // android.content.pm.PackageManager
    public void getPackageSizeInfo(String packageName, int userHandle, IPackageStatsObserver observer) {
        try {
            this.mPM.getPackageSizeInfo(packageName, userHandle, observer);
        } catch (RemoteException e) {
        }
    }

    @Override // android.content.pm.PackageManager
    public void addPackageToPreferred(String packageName) {
        try {
            this.mPM.addPackageToPreferred(packageName);
        } catch (RemoteException e) {
        }
    }

    @Override // android.content.pm.PackageManager
    public void removePackageFromPreferred(String packageName) {
        try {
            this.mPM.removePackageFromPreferred(packageName);
        } catch (RemoteException e) {
        }
    }

    @Override // android.content.pm.PackageManager
    public List<PackageInfo> getPreferredPackages(int flags) {
        try {
            return this.mPM.getPreferredPackages(flags);
        } catch (RemoteException e) {
            return new ArrayList();
        }
    }

    @Override // android.content.pm.PackageManager
    public void addPreferredActivity(IntentFilter filter, int match, ComponentName[] set, ComponentName activity) {
        try {
            this.mPM.addPreferredActivity(filter, match, set, activity, this.mContext.getUserId());
        } catch (RemoteException e) {
        }
    }

    @Override // android.content.pm.PackageManager
    public void addPreferredActivity(IntentFilter filter, int match, ComponentName[] set, ComponentName activity, int userId) {
        try {
            this.mPM.addPreferredActivity(filter, match, set, activity, userId);
        } catch (RemoteException e) {
        }
    }

    @Override // android.content.pm.PackageManager
    public void replacePreferredActivity(IntentFilter filter, int match, ComponentName[] set, ComponentName activity) {
        try {
            this.mPM.replacePreferredActivity(filter, match, set, activity);
        } catch (RemoteException e) {
        }
    }

    @Override // android.content.pm.PackageManager
    public void clearPackagePreferredActivities(String packageName) {
        try {
            this.mPM.clearPackagePreferredActivities(packageName);
        } catch (RemoteException e) {
        }
    }

    @Override // android.content.pm.PackageManager
    public int getPreferredActivities(List<IntentFilter> outFilters, List<ComponentName> outActivities, String packageName) {
        try {
            return this.mPM.getPreferredActivities(outFilters, outActivities, packageName);
        } catch (RemoteException e) {
            return 0;
        }
    }

    @Override // android.content.pm.PackageManager
    public ComponentName getHomeActivities(List<ResolveInfo> outActivities) {
        try {
            return this.mPM.getHomeActivities(outActivities);
        } catch (RemoteException e) {
            return null;
        }
    }

    @Override // android.content.pm.PackageManager
    public void setComponentEnabledSetting(ComponentName componentName, int newState, int flags) {
        try {
            this.mPM.setComponentEnabledSetting(componentName, newState, flags, this.mContext.getUserId());
        } catch (RemoteException e) {
        }
    }

    @Override // android.content.pm.PackageManager
    public int getComponentEnabledSetting(ComponentName componentName) {
        try {
            return this.mPM.getComponentEnabledSetting(componentName, this.mContext.getUserId());
        } catch (RemoteException e) {
            return 0;
        }
    }

    @Override // android.content.pm.PackageManager
    public void setApplicationEnabledSetting(String packageName, int newState, int flags) {
        try {
            this.mPM.setApplicationEnabledSetting(packageName, newState, flags, this.mContext.getUserId(), this.mContext.getOpPackageName());
        } catch (RemoteException e) {
        }
    }

    @Override // android.content.pm.PackageManager
    public int getApplicationEnabledSetting(String packageName) {
        try {
            return this.mPM.getApplicationEnabledSetting(packageName, this.mContext.getUserId());
        } catch (RemoteException e) {
            return 0;
        }
    }

    @Override // android.content.pm.PackageManager
    public boolean setApplicationBlockedSettingAsUser(String packageName, boolean blocked, UserHandle user) {
        try {
            return this.mPM.setApplicationBlockedSettingAsUser(packageName, blocked, user.getIdentifier());
        } catch (RemoteException e) {
            return false;
        }
    }

    @Override // android.content.pm.PackageManager
    public boolean getApplicationBlockedSettingAsUser(String packageName, UserHandle user) {
        try {
            return this.mPM.getApplicationBlockedSettingAsUser(packageName, user.getIdentifier());
        } catch (RemoteException e) {
            return false;
        }
    }

    @Override // android.content.pm.PackageManager
    public VerifierDeviceIdentity getVerifierDeviceIdentity() {
        try {
            return this.mPM.getVerifierDeviceIdentity();
        } catch (RemoteException e) {
            return null;
        }
    }
}