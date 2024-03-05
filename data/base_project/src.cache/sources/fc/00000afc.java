package android.os;

import android.os.storage.IMountService;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import com.google.android.collect.Lists;
import gov.nist.core.Separators;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/* loaded from: Environment.class */
public class Environment {
    private static final String TAG = "Environment";
    private static final String ENV_EXTERNAL_STORAGE = "EXTERNAL_STORAGE";
    private static final String ENV_EMULATED_STORAGE_SOURCE = "EMULATED_STORAGE_SOURCE";
    private static final String ENV_SECONDARY_STORAGE = "SECONDARY_STORAGE";
    public static final String DIR_ANDROID = "Android";
    private static final String DIR_DATA = "data";
    private static final String DIR_MEDIA = "media";
    private static final String DIR_OBB = "obb";
    private static final String DIR_FILES = "files";
    private static final String DIR_CACHE = "cache";
    @Deprecated
    public static final String DIRECTORY_ANDROID = "Android";
    private static final String SYSTEM_PROPERTY_EFS_ENABLED = "persist.security.efs.enabled";
    private static UserEnvironment sCurrentUser;
    private static boolean sUserRequired;
    @GuardedBy("sLock")
    private static volatile StorageVolume sPrimaryVolume;
    private static final File DATA_DIRECTORY;
    private static final File SECURE_DATA_DIRECTORY;
    private static final File DOWNLOAD_CACHE_DIRECTORY;
    public static String DIRECTORY_MUSIC;
    public static String DIRECTORY_PODCASTS;
    public static String DIRECTORY_RINGTONES;
    public static String DIRECTORY_ALARMS;
    public static String DIRECTORY_NOTIFICATIONS;
    public static String DIRECTORY_PICTURES;
    public static String DIRECTORY_MOVIES;
    public static String DIRECTORY_DOWNLOADS;
    public static String DIRECTORY_DCIM;
    public static String DIRECTORY_DOCUMENTS;
    public static final String MEDIA_UNKNOWN = "unknown";
    public static final String MEDIA_REMOVED = "removed";
    public static final String MEDIA_UNMOUNTED = "unmounted";
    public static final String MEDIA_CHECKING = "checking";
    public static final String MEDIA_NOFS = "nofs";
    public static final String MEDIA_MOUNTED = "mounted";
    public static final String MEDIA_MOUNTED_READ_ONLY = "mounted_ro";
    public static final String MEDIA_SHARED = "shared";
    public static final String MEDIA_BAD_REMOVAL = "bad_removal";
    public static final String MEDIA_UNMOUNTABLE = "unmountable";
    private static final String ENV_ANDROID_ROOT = "ANDROID_ROOT";
    private static final File DIR_ANDROID_ROOT = getDirectory(ENV_ANDROID_ROOT, "/system");
    private static final String ENV_MEDIA_STORAGE = "MEDIA_STORAGE";
    private static final File DIR_MEDIA_STORAGE = getDirectory(ENV_MEDIA_STORAGE, "/data/media");
    private static final String ENV_EMULATED_STORAGE_TARGET = "EMULATED_STORAGE_TARGET";
    private static final String CANONCIAL_EMULATED_STORAGE_TARGET = getCanonicalPathOrNull(ENV_EMULATED_STORAGE_TARGET);
    private static final Object sLock = new Object();

    static {
        initForCurrentUser();
        DATA_DIRECTORY = getDirectory("ANDROID_DATA", "/data");
        SECURE_DATA_DIRECTORY = getDirectory("ANDROID_SECURE_DATA", "/data/secure");
        DOWNLOAD_CACHE_DIRECTORY = getDirectory("DOWNLOAD_CACHE", "/cache");
        DIRECTORY_MUSIC = "Music";
        DIRECTORY_PODCASTS = "Podcasts";
        DIRECTORY_RINGTONES = "Ringtones";
        DIRECTORY_ALARMS = "Alarms";
        DIRECTORY_NOTIFICATIONS = "Notifications";
        DIRECTORY_PICTURES = "Pictures";
        DIRECTORY_MOVIES = "Movies";
        DIRECTORY_DOWNLOADS = "Download";
        DIRECTORY_DCIM = "DCIM";
        DIRECTORY_DOCUMENTS = "Documents";
    }

    private static StorageVolume getPrimaryVolume() {
        if (SystemProperties.getBoolean("config.disable_storage", false)) {
            return null;
        }
        if (sPrimaryVolume == null) {
            synchronized (sLock) {
                if (sPrimaryVolume == null) {
                    try {
                        IMountService mountService = IMountService.Stub.asInterface(ServiceManager.getService("mount"));
                        StorageVolume[] volumes = mountService.getVolumeList();
                        sPrimaryVolume = StorageManager.getPrimaryVolume(volumes);
                    } catch (Exception e) {
                        Log.e(TAG, "couldn't talk to MountService", e);
                    }
                }
            }
        }
        return sPrimaryVolume;
    }

    public static void initForCurrentUser() {
        int userId = UserHandle.myUserId();
        sCurrentUser = new UserEnvironment(userId);
        synchronized (sLock) {
            sPrimaryVolume = null;
        }
    }

    /* loaded from: Environment$UserEnvironment.class */
    public static class UserEnvironment {
        private final File[] mExternalDirsForVold;
        private final File[] mExternalDirsForApp;
        private final File mEmulatedDirForDirect;

        public UserEnvironment(int userId) {
            String rawExternalStorage = System.getenv(Environment.ENV_EXTERNAL_STORAGE);
            String rawEmulatedSource = System.getenv(Environment.ENV_EMULATED_STORAGE_SOURCE);
            String rawEmulatedTarget = System.getenv(Environment.ENV_EMULATED_STORAGE_TARGET);
            String rawMediaStorage = System.getenv(Environment.ENV_MEDIA_STORAGE);
            rawMediaStorage = TextUtils.isEmpty(rawMediaStorage) ? "/data/media" : rawMediaStorage;
            ArrayList<File> externalForVold = Lists.newArrayList();
            ArrayList<File> externalForApp = Lists.newArrayList();
            if (!TextUtils.isEmpty(rawEmulatedTarget)) {
                String rawUserId = Integer.toString(userId);
                File emulatedSourceBase = new File(rawEmulatedSource);
                File emulatedTargetBase = new File(rawEmulatedTarget);
                File mediaBase = new File(rawMediaStorage);
                externalForVold.add(Environment.buildPath(emulatedSourceBase, rawUserId));
                externalForApp.add(Environment.buildPath(emulatedTargetBase, rawUserId));
                this.mEmulatedDirForDirect = Environment.buildPath(mediaBase, rawUserId);
            } else {
                if (TextUtils.isEmpty(rawExternalStorage)) {
                    Log.w(Environment.TAG, "EXTERNAL_STORAGE undefined; falling back to default");
                    rawExternalStorage = "/storage/sdcard0";
                }
                externalForVold.add(new File(rawExternalStorage));
                externalForApp.add(new File(rawExternalStorage));
                this.mEmulatedDirForDirect = new File(rawMediaStorage);
            }
            String rawSecondaryStorage = System.getenv(Environment.ENV_SECONDARY_STORAGE);
            if (!TextUtils.isEmpty(rawSecondaryStorage) && userId == 0) {
                String[] arr$ = rawSecondaryStorage.split(Separators.COLON);
                for (String secondaryPath : arr$) {
                    externalForVold.add(new File(secondaryPath));
                    externalForApp.add(new File(secondaryPath));
                }
            }
            this.mExternalDirsForVold = (File[]) externalForVold.toArray(new File[externalForVold.size()]);
            this.mExternalDirsForApp = (File[]) externalForApp.toArray(new File[externalForApp.size()]);
        }

        @Deprecated
        public File getExternalStorageDirectory() {
            return this.mExternalDirsForApp[0];
        }

        @Deprecated
        public File getExternalStoragePublicDirectory(String type) {
            return buildExternalStoragePublicDirs(type)[0];
        }

        public File[] getExternalDirsForVold() {
            return this.mExternalDirsForVold;
        }

        public File[] getExternalDirsForApp() {
            return this.mExternalDirsForApp;
        }

        public File getMediaDir() {
            return this.mEmulatedDirForDirect;
        }

        public File[] buildExternalStoragePublicDirs(String type) {
            return Environment.buildPaths(this.mExternalDirsForApp, type);
        }

        public File[] buildExternalStorageAndroidDataDirs() {
            return Environment.buildPaths(this.mExternalDirsForApp, "Android", "data");
        }

        public File[] buildExternalStorageAndroidObbDirs() {
            return Environment.buildPaths(this.mExternalDirsForApp, "Android", "obb");
        }

        public File[] buildExternalStorageAppDataDirs(String packageName) {
            return Environment.buildPaths(this.mExternalDirsForApp, "Android", "data", packageName);
        }

        public File[] buildExternalStorageAppDataDirsForVold(String packageName) {
            return Environment.buildPaths(this.mExternalDirsForVold, "Android", "data", packageName);
        }

        public File[] buildExternalStorageAppMediaDirs(String packageName) {
            return Environment.buildPaths(this.mExternalDirsForApp, "Android", "media", packageName);
        }

        public File[] buildExternalStorageAppObbDirs(String packageName) {
            return Environment.buildPaths(this.mExternalDirsForApp, "Android", "obb", packageName);
        }

        public File[] buildExternalStorageAppObbDirsForVold(String packageName) {
            return Environment.buildPaths(this.mExternalDirsForVold, "Android", "obb", packageName);
        }

        public File[] buildExternalStorageAppFilesDirs(String packageName) {
            return Environment.buildPaths(this.mExternalDirsForApp, "Android", "data", packageName, Environment.DIR_FILES);
        }

        public File[] buildExternalStorageAppCacheDirs(String packageName) {
            return Environment.buildPaths(this.mExternalDirsForApp, "Android", "data", packageName, Environment.DIR_CACHE);
        }
    }

    public static File getRootDirectory() {
        return DIR_ANDROID_ROOT;
    }

    public static File getSystemSecureDirectory() {
        if (isEncryptedFilesystemEnabled()) {
            return new File(SECURE_DATA_DIRECTORY, "system");
        }
        return new File(DATA_DIRECTORY, "system");
    }

    public static File getSecureDataDirectory() {
        if (isEncryptedFilesystemEnabled()) {
            return SECURE_DATA_DIRECTORY;
        }
        return DATA_DIRECTORY;
    }

    public static File getMediaStorageDirectory() {
        throwIfUserRequired();
        return sCurrentUser.getMediaDir();
    }

    public static File getUserSystemDirectory(int userId) {
        return new File(new File(getSystemSecureDirectory(), "users"), Integer.toString(userId));
    }

    public static boolean isEncryptedFilesystemEnabled() {
        return SystemProperties.getBoolean(SYSTEM_PROPERTY_EFS_ENABLED, false);
    }

    public static File getDataDirectory() {
        return DATA_DIRECTORY;
    }

    public static File getExternalStorageDirectory() {
        throwIfUserRequired();
        return sCurrentUser.getExternalDirsForApp()[0];
    }

    public static File getLegacyExternalStorageDirectory() {
        return new File(System.getenv(ENV_EXTERNAL_STORAGE));
    }

    public static File getLegacyExternalStorageObbDirectory() {
        return buildPath(getLegacyExternalStorageDirectory(), "Android", "obb");
    }

    public static File getEmulatedStorageSource(int userId) {
        return new File(System.getenv(ENV_EMULATED_STORAGE_SOURCE), String.valueOf(userId));
    }

    public static File getEmulatedStorageObbSource() {
        return new File(System.getenv(ENV_EMULATED_STORAGE_SOURCE), "obb");
    }

    public static File getExternalStoragePublicDirectory(String type) {
        throwIfUserRequired();
        return sCurrentUser.buildExternalStoragePublicDirs(type)[0];
    }

    public static File[] buildExternalStorageAndroidDataDirs() {
        throwIfUserRequired();
        return sCurrentUser.buildExternalStorageAndroidDataDirs();
    }

    public static File[] buildExternalStorageAppDataDirs(String packageName) {
        throwIfUserRequired();
        return sCurrentUser.buildExternalStorageAppDataDirs(packageName);
    }

    public static File[] buildExternalStorageAppMediaDirs(String packageName) {
        throwIfUserRequired();
        return sCurrentUser.buildExternalStorageAppMediaDirs(packageName);
    }

    public static File[] buildExternalStorageAppObbDirs(String packageName) {
        throwIfUserRequired();
        return sCurrentUser.buildExternalStorageAppObbDirs(packageName);
    }

    public static File[] buildExternalStorageAppFilesDirs(String packageName) {
        throwIfUserRequired();
        return sCurrentUser.buildExternalStorageAppFilesDirs(packageName);
    }

    public static File[] buildExternalStorageAppCacheDirs(String packageName) {
        throwIfUserRequired();
        return sCurrentUser.buildExternalStorageAppCacheDirs(packageName);
    }

    public static File getDownloadCacheDirectory() {
        return DOWNLOAD_CACHE_DIRECTORY;
    }

    public static String getExternalStorageState() {
        File externalDir = sCurrentUser.getExternalDirsForApp()[0];
        return getStorageState(externalDir);
    }

    public static String getStorageState(File path) {
        try {
            String rawPath = path.getCanonicalPath();
            try {
                IMountService mountService = IMountService.Stub.asInterface(ServiceManager.getService("mount"));
                StorageVolume[] volumes = mountService.getVolumeList();
                for (StorageVolume volume : volumes) {
                    if (rawPath.startsWith(volume.getPath())) {
                        return mountService.getVolumeState(volume.getPath());
                    }
                }
                return "unknown";
            } catch (RemoteException e) {
                Log.w(TAG, "Failed to find external storage state: " + e);
                return "unknown";
            }
        } catch (IOException e2) {
            Log.w(TAG, "Failed to resolve target path: " + e2);
            return "unknown";
        }
    }

    public static boolean isExternalStorageRemovable() {
        StorageVolume primary = getPrimaryVolume();
        return primary != null && primary.isRemovable();
    }

    public static boolean isExternalStorageEmulated() {
        StorageVolume primary = getPrimaryVolume();
        return primary != null && primary.isEmulated();
    }

    static File getDirectory(String variableName, String defaultPath) {
        String path = System.getenv(variableName);
        return path == null ? new File(defaultPath) : new File(path);
    }

    private static String getCanonicalPathOrNull(String variableName) {
        String path = System.getenv(variableName);
        if (path == null) {
            return null;
        }
        try {
            return new File(path).getCanonicalPath();
        } catch (IOException e) {
            Log.w(TAG, "Unable to resolve canonical path for " + path);
            return null;
        }
    }

    public static void setUserRequired(boolean userRequired) {
        sUserRequired = userRequired;
    }

    private static void throwIfUserRequired() {
        if (sUserRequired) {
            Log.wtf(TAG, "Path requests must specify a user by using UserEnvironment", new Throwable());
        }
    }

    public static File[] buildPaths(File[] base, String... segments) {
        File[] result = new File[base.length];
        for (int i = 0; i < base.length; i++) {
            result[i] = buildPath(base[i], segments);
        }
        return result;
    }

    public static File buildPath(File base, String... segments) {
        File file;
        File cur = base;
        for (String segment : segments) {
            if (cur == null) {
                file = new File(segment);
            } else {
                file = new File(cur, segment);
            }
            cur = file;
        }
        return cur;
    }

    public static File maybeTranslateEmulatedPathToInternal(File path) {
        if (!isExternalStorageEmulated() || CANONCIAL_EMULATED_STORAGE_TARGET == null) {
            return path;
        }
        try {
            String rawPath = path.getCanonicalPath();
            if (rawPath.startsWith(CANONCIAL_EMULATED_STORAGE_TARGET)) {
                File internalPath = new File(DIR_MEDIA_STORAGE, rawPath.substring(CANONCIAL_EMULATED_STORAGE_TARGET.length()));
                if (internalPath.exists()) {
                    return internalPath;
                }
            }
        } catch (IOException e) {
            Log.w(TAG, "Failed to resolve canonical path for " + path);
        }
        return path;
    }
}