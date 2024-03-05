package com.android.internal.content;

import android.os.FileUtils;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.storage.IMountService;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import libcore.io.IoUtils;

/* loaded from: PackageHelper.class */
public class PackageHelper {
    public static final int RECOMMEND_INSTALL_INTERNAL = 1;
    public static final int RECOMMEND_INSTALL_EXTERNAL = 2;
    public static final int RECOMMEND_FAILED_INSUFFICIENT_STORAGE = -1;
    public static final int RECOMMEND_FAILED_INVALID_APK = -2;
    public static final int RECOMMEND_FAILED_INVALID_LOCATION = -3;
    public static final int RECOMMEND_FAILED_ALREADY_EXISTS = -4;
    public static final int RECOMMEND_MEDIA_UNAVAILABLE = -5;
    public static final int RECOMMEND_FAILED_INVALID_URI = -6;
    public static final int RECOMMEND_FAILED_VERSION_DOWNGRADE = -7;
    private static final boolean localLOGV = false;
    private static final String TAG = "PackageHelper";
    public static final int APP_INSTALL_AUTO = 0;
    public static final int APP_INSTALL_INTERNAL = 1;
    public static final int APP_INSTALL_EXTERNAL = 2;

    public static IMountService getMountService() throws RemoteException {
        IBinder service = ServiceManager.getService("mount");
        if (service != null) {
            return IMountService.Stub.asInterface(service);
        }
        Log.e(TAG, "Can't get mount service");
        throw new RemoteException("Could not contact mount service");
    }

    public static String createSdDir(int sizeMb, String cid, String sdEncKey, int uid, boolean isExternal) {
        try {
            IMountService mountService = getMountService();
            int rc = mountService.createSecureContainer(cid, sizeMb, "ext4", sdEncKey, uid, isExternal);
            if (rc != 0) {
                Log.e(TAG, "Failed to create secure container " + cid);
                return null;
            }
            String cachePath = mountService.getSecureContainerPath(cid);
            return cachePath;
        } catch (RemoteException e) {
            Log.e(TAG, "MountService running?");
            return null;
        }
    }

    public static String mountSdDir(String cid, String key, int ownerUid) {
        try {
            int rc = getMountService().mountSecureContainer(cid, key, ownerUid);
            if (rc != 0) {
                Log.i(TAG, "Failed to mount container " + cid + " rc : " + rc);
                return null;
            }
            return getMountService().getSecureContainerPath(cid);
        } catch (RemoteException e) {
            Log.e(TAG, "MountService running?");
            return null;
        }
    }

    public static boolean unMountSdDir(String cid) {
        try {
            int rc = getMountService().unmountSecureContainer(cid, true);
            if (rc != 0) {
                Log.e(TAG, "Failed to unmount " + cid + " with rc " + rc);
                return false;
            }
            return true;
        } catch (RemoteException e) {
            Log.e(TAG, "MountService running?");
            return false;
        }
    }

    public static boolean renameSdDir(String oldId, String newId) {
        try {
            int rc = getMountService().renameSecureContainer(oldId, newId);
            if (rc != 0) {
                Log.e(TAG, "Failed to rename " + oldId + " to " + newId + "with rc " + rc);
                return false;
            }
            return true;
        } catch (RemoteException e) {
            Log.i(TAG, "Failed ot rename  " + oldId + " to " + newId + " with exception : " + e);
            return false;
        }
    }

    public static String getSdDir(String cid) {
        try {
            return getMountService().getSecureContainerPath(cid);
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to get container path for " + cid + " with exception " + e);
            return null;
        }
    }

    public static String getSdFilesystem(String cid) {
        try {
            return getMountService().getSecureContainerFilesystemPath(cid);
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to get container path for " + cid + " with exception " + e);
            return null;
        }
    }

    public static boolean finalizeSdDir(String cid) {
        try {
            int rc = getMountService().finalizeSecureContainer(cid);
            if (rc != 0) {
                Log.i(TAG, "Failed to finalize container " + cid);
                return false;
            }
            return true;
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to finalize container " + cid + " with exception " + e);
            return false;
        }
    }

    public static boolean destroySdDir(String cid) {
        try {
            int rc = getMountService().destroySecureContainer(cid, true);
            if (rc != 0) {
                Log.i(TAG, "Failed to destroy container " + cid);
                return false;
            }
            return true;
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to destroy container " + cid + " with exception " + e);
            return false;
        }
    }

    public static String[] getSecureContainerList() {
        try {
            return getMountService().getSecureContainerList();
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to get secure container list with exception" + e);
            return null;
        }
    }

    public static boolean isContainerMounted(String cid) {
        try {
            return getMountService().isSecureContainerMounted(cid);
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to find out if container " + cid + " mounted");
            return false;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v15, types: [java.lang.AutoCloseable] */
    /* JADX WARN: Type inference failed for: r0v4, types: [java.lang.AutoCloseable] */
    public static int extractPublicFiles(String packagePath, File publicZipFile) throws IOException {
        FileOutputStream fstr;
        ZipOutputStream publicZipOutStream;
        if (publicZipFile == null) {
            fstr = null;
            publicZipOutStream = null;
        } else {
            fstr = new FileOutputStream(publicZipFile);
            publicZipOutStream = new ZipOutputStream(fstr);
        }
        int size = 0;
        try {
            ZipFile privateZip = new ZipFile(packagePath);
            Iterator i$ = Collections.list(privateZip.entries()).iterator();
            while (i$.hasNext()) {
                ZipEntry zipEntry = (ZipEntry) i$.next();
                String zipEntryName = zipEntry.getName();
                if ("AndroidManifest.xml".equals(zipEntryName) || "resources.arsc".equals(zipEntryName) || zipEntryName.startsWith("res/")) {
                    size = (int) (size + zipEntry.getSize());
                    if (publicZipFile != null) {
                        copyZipEntry(zipEntry, privateZip, publicZipOutStream);
                    }
                }
            }
            try {
                privateZip.close();
            } catch (IOException e) {
            }
            if (publicZipFile != null) {
                publicZipOutStream.finish();
                publicZipOutStream.flush();
                FileUtils.sync(fstr);
                publicZipOutStream.close();
                FileUtils.setPermissions(publicZipFile.getAbsolutePath(), 420, -1, -1);
            }
            return size;
        } finally {
            IoUtils.closeQuietly((AutoCloseable) publicZipOutStream);
        }
    }

    /* JADX WARN: Type inference failed for: r0v7, types: [java.lang.AutoCloseable, java.io.InputStream] */
    private static void copyZipEntry(ZipEntry zipEntry, ZipFile inZipFile, ZipOutputStream outZipStream) throws IOException {
        ZipEntry newEntry;
        byte[] buffer = new byte[4096];
        if (zipEntry.getMethod() == 0) {
            newEntry = new ZipEntry(zipEntry);
        } else {
            newEntry = new ZipEntry(zipEntry.getName());
        }
        outZipStream.putNextEntry(newEntry);
        ?? inputStream = inZipFile.getInputStream(zipEntry);
        while (true) {
            try {
                int num = inputStream.read(buffer);
                if (num > 0) {
                    outZipStream.write(buffer, 0, num);
                } else {
                    outZipStream.flush();
                    IoUtils.closeQuietly((AutoCloseable) inputStream);
                    return;
                }
            } catch (Throwable th) {
                IoUtils.closeQuietly((AutoCloseable) inputStream);
                throw th;
            }
        }
    }

    public static boolean fixSdPermissions(String cid, int gid, String filename) {
        try {
            int rc = getMountService().fixPermissionsSecureContainer(cid, gid, filename);
            if (rc != 0) {
                Log.i(TAG, "Failed to fixperms container " + cid);
                return false;
            }
            return true;
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to fixperms container " + cid + " with exception " + e);
            return false;
        }
    }
}