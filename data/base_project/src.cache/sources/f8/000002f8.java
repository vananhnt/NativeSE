package android.content;

import android.content.IntentSender;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.view.Display;
import android.view.DisplayAdjustments;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: ContextWrapper.class */
public class ContextWrapper extends Context {
    Context mBase;

    public ContextWrapper(Context base) {
        this.mBase = base;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void attachBaseContext(Context base) {
        if (this.mBase != null) {
            throw new IllegalStateException("Base context already set");
        }
        this.mBase = base;
    }

    public Context getBaseContext() {
        return this.mBase;
    }

    @Override // android.content.Context
    public AssetManager getAssets() {
        return this.mBase.getAssets();
    }

    @Override // android.content.Context
    public Resources getResources() {
        return this.mBase.getResources();
    }

    @Override // android.content.Context
    public PackageManager getPackageManager() {
        return this.mBase.getPackageManager();
    }

    @Override // android.content.Context
    public ContentResolver getContentResolver() {
        return this.mBase.getContentResolver();
    }

    @Override // android.content.Context
    public Looper getMainLooper() {
        return this.mBase.getMainLooper();
    }

    @Override // android.content.Context
    public Context getApplicationContext() {
        return this.mBase.getApplicationContext();
    }

    @Override // android.content.Context
    public void setTheme(int resid) {
        this.mBase.setTheme(resid);
    }

    @Override // android.content.Context
    public int getThemeResId() {
        return this.mBase.getThemeResId();
    }

    @Override // android.content.Context
    public Resources.Theme getTheme() {
        return this.mBase.getTheme();
    }

    @Override // android.content.Context
    public ClassLoader getClassLoader() {
        return this.mBase.getClassLoader();
    }

    @Override // android.content.Context
    public String getPackageName() {
        return this.mBase.getPackageName();
    }

    @Override // android.content.Context
    public String getBasePackageName() {
        return this.mBase.getBasePackageName();
    }

    @Override // android.content.Context
    public String getOpPackageName() {
        return this.mBase.getOpPackageName();
    }

    @Override // android.content.Context
    public ApplicationInfo getApplicationInfo() {
        return this.mBase.getApplicationInfo();
    }

    @Override // android.content.Context
    public String getPackageResourcePath() {
        return this.mBase.getPackageResourcePath();
    }

    @Override // android.content.Context
    public String getPackageCodePath() {
        return this.mBase.getPackageCodePath();
    }

    @Override // android.content.Context
    public File getSharedPrefsFile(String name) {
        return this.mBase.getSharedPrefsFile(name);
    }

    @Override // android.content.Context
    public SharedPreferences getSharedPreferences(String name, int mode) {
        return this.mBase.getSharedPreferences(name, mode);
    }

    @Override // android.content.Context
    public FileInputStream openFileInput(String name) throws FileNotFoundException {
        return this.mBase.openFileInput(name);
    }

    @Override // android.content.Context
    public FileOutputStream openFileOutput(String name, int mode) throws FileNotFoundException {
        return this.mBase.openFileOutput(name, mode);
    }

    @Override // android.content.Context
    public boolean deleteFile(String name) {
        return this.mBase.deleteFile(name);
    }

    @Override // android.content.Context
    public File getFileStreamPath(String name) {
        return this.mBase.getFileStreamPath(name);
    }

    @Override // android.content.Context
    public String[] fileList() {
        return this.mBase.fileList();
    }

    @Override // android.content.Context
    public File getFilesDir() {
        return this.mBase.getFilesDir();
    }

    @Override // android.content.Context
    public File getExternalFilesDir(String type) {
        return this.mBase.getExternalFilesDir(type);
    }

    @Override // android.content.Context
    public File[] getExternalFilesDirs(String type) {
        return this.mBase.getExternalFilesDirs(type);
    }

    @Override // android.content.Context
    public File getObbDir() {
        return this.mBase.getObbDir();
    }

    @Override // android.content.Context
    public File[] getObbDirs() {
        return this.mBase.getObbDirs();
    }

    @Override // android.content.Context
    public File getCacheDir() {
        return this.mBase.getCacheDir();
    }

    @Override // android.content.Context
    public File getExternalCacheDir() {
        return this.mBase.getExternalCacheDir();
    }

    @Override // android.content.Context
    public File[] getExternalCacheDirs() {
        return this.mBase.getExternalCacheDirs();
    }

    @Override // android.content.Context
    public File getDir(String name, int mode) {
        return this.mBase.getDir(name, mode);
    }

    @Override // android.content.Context
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
        return this.mBase.openOrCreateDatabase(name, mode, factory);
    }

    @Override // android.content.Context
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        return this.mBase.openOrCreateDatabase(name, mode, factory, errorHandler);
    }

    @Override // android.content.Context
    public boolean deleteDatabase(String name) {
        return this.mBase.deleteDatabase(name);
    }

    @Override // android.content.Context
    public File getDatabasePath(String name) {
        return this.mBase.getDatabasePath(name);
    }

    @Override // android.content.Context
    public String[] databaseList() {
        return this.mBase.databaseList();
    }

    @Override // android.content.Context
    public Drawable getWallpaper() {
        return this.mBase.getWallpaper();
    }

    @Override // android.content.Context
    public Drawable peekWallpaper() {
        return this.mBase.peekWallpaper();
    }

    @Override // android.content.Context
    public int getWallpaperDesiredMinimumWidth() {
        return this.mBase.getWallpaperDesiredMinimumWidth();
    }

    @Override // android.content.Context
    public int getWallpaperDesiredMinimumHeight() {
        return this.mBase.getWallpaperDesiredMinimumHeight();
    }

    @Override // android.content.Context
    public void setWallpaper(Bitmap bitmap) throws IOException {
        this.mBase.setWallpaper(bitmap);
    }

    @Override // android.content.Context
    public void setWallpaper(InputStream data) throws IOException {
        this.mBase.setWallpaper(data);
    }

    @Override // android.content.Context
    public void clearWallpaper() throws IOException {
        this.mBase.clearWallpaper();
    }

    @Override // android.content.Context
    public void startActivity(Intent intent) {
        this.mBase.startActivity(intent);
    }

    @Override // android.content.Context
    public void startActivityAsUser(Intent intent, UserHandle user) {
        this.mBase.startActivityAsUser(intent, user);
    }

    @Override // android.content.Context
    public void startActivity(Intent intent, Bundle options) {
        this.mBase.startActivity(intent, options);
    }

    @Override // android.content.Context
    public void startActivityAsUser(Intent intent, Bundle options, UserHandle user) {
        this.mBase.startActivityAsUser(intent, options, user);
    }

    @Override // android.content.Context
    public void startActivities(Intent[] intents) {
        this.mBase.startActivities(intents);
    }

    @Override // android.content.Context
    public void startActivities(Intent[] intents, Bundle options) {
        this.mBase.startActivities(intents, options);
    }

    @Override // android.content.Context
    public void startActivitiesAsUser(Intent[] intents, Bundle options, UserHandle userHandle) {
        this.mBase.startActivitiesAsUser(intents, options, userHandle);
    }

    @Override // android.content.Context
    public void startIntentSender(IntentSender intent, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags) throws IntentSender.SendIntentException {
        this.mBase.startIntentSender(intent, fillInIntent, flagsMask, flagsValues, extraFlags);
    }

    @Override // android.content.Context
    public void startIntentSender(IntentSender intent, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, Bundle options) throws IntentSender.SendIntentException {
        this.mBase.startIntentSender(intent, fillInIntent, flagsMask, flagsValues, extraFlags, options);
    }

    @Override // android.content.Context
    public void sendBroadcast(Intent intent) {
        this.mBase.sendBroadcast(intent);
    }

    @Override // android.content.Context
    public void sendBroadcast(Intent intent, String receiverPermission) {
        this.mBase.sendBroadcast(intent, receiverPermission);
    }

    @Override // android.content.Context
    public void sendBroadcast(Intent intent, String receiverPermission, int appOp) {
        this.mBase.sendBroadcast(intent, receiverPermission, appOp);
    }

    @Override // android.content.Context
    public void sendOrderedBroadcast(Intent intent, String receiverPermission) {
        this.mBase.sendOrderedBroadcast(intent, receiverPermission);
    }

    @Override // android.content.Context
    public void sendOrderedBroadcast(Intent intent, String receiverPermission, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras) {
        this.mBase.sendOrderedBroadcast(intent, receiverPermission, resultReceiver, scheduler, initialCode, initialData, initialExtras);
    }

    @Override // android.content.Context
    public void sendOrderedBroadcast(Intent intent, String receiverPermission, int appOp, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras) {
        this.mBase.sendOrderedBroadcast(intent, receiverPermission, appOp, resultReceiver, scheduler, initialCode, initialData, initialExtras);
    }

    @Override // android.content.Context
    public void sendBroadcastAsUser(Intent intent, UserHandle user) {
        this.mBase.sendBroadcastAsUser(intent, user);
    }

    @Override // android.content.Context
    public void sendBroadcastAsUser(Intent intent, UserHandle user, String receiverPermission) {
        this.mBase.sendBroadcastAsUser(intent, user, receiverPermission);
    }

    @Override // android.content.Context
    public void sendOrderedBroadcastAsUser(Intent intent, UserHandle user, String receiverPermission, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras) {
        this.mBase.sendOrderedBroadcastAsUser(intent, user, receiverPermission, resultReceiver, scheduler, initialCode, initialData, initialExtras);
    }

    @Override // android.content.Context
    public void sendStickyBroadcast(Intent intent) {
        this.mBase.sendStickyBroadcast(intent);
    }

    @Override // android.content.Context
    public void sendStickyOrderedBroadcast(Intent intent, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras) {
        this.mBase.sendStickyOrderedBroadcast(intent, resultReceiver, scheduler, initialCode, initialData, initialExtras);
    }

    @Override // android.content.Context
    public void removeStickyBroadcast(Intent intent) {
        this.mBase.removeStickyBroadcast(intent);
    }

    @Override // android.content.Context
    public void sendStickyBroadcastAsUser(Intent intent, UserHandle user) {
        this.mBase.sendStickyBroadcastAsUser(intent, user);
    }

    @Override // android.content.Context
    public void sendStickyOrderedBroadcastAsUser(Intent intent, UserHandle user, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras) {
        this.mBase.sendStickyOrderedBroadcastAsUser(intent, user, resultReceiver, scheduler, initialCode, initialData, initialExtras);
    }

    @Override // android.content.Context
    public void removeStickyBroadcastAsUser(Intent intent, UserHandle user) {
        this.mBase.removeStickyBroadcastAsUser(intent, user);
    }

    @Override // android.content.Context
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        return this.mBase.registerReceiver(receiver, filter);
    }

    @Override // android.content.Context
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, String broadcastPermission, Handler scheduler) {
        return this.mBase.registerReceiver(receiver, filter, broadcastPermission, scheduler);
    }

    @Override // android.content.Context
    public Intent registerReceiverAsUser(BroadcastReceiver receiver, UserHandle user, IntentFilter filter, String broadcastPermission, Handler scheduler) {
        return this.mBase.registerReceiverAsUser(receiver, user, filter, broadcastPermission, scheduler);
    }

    @Override // android.content.Context
    public void unregisterReceiver(BroadcastReceiver receiver) {
        this.mBase.unregisterReceiver(receiver);
    }

    @Override // android.content.Context
    public ComponentName startService(Intent service) {
        return this.mBase.startService(service);
    }

    @Override // android.content.Context
    public boolean stopService(Intent name) {
        return this.mBase.stopService(name);
    }

    @Override // android.content.Context
    public ComponentName startServiceAsUser(Intent service, UserHandle user) {
        return this.mBase.startServiceAsUser(service, user);
    }

    @Override // android.content.Context
    public boolean stopServiceAsUser(Intent name, UserHandle user) {
        return this.mBase.stopServiceAsUser(name, user);
    }

    @Override // android.content.Context
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        return this.mBase.bindService(service, conn, flags);
    }

    @Override // android.content.Context
    public boolean bindServiceAsUser(Intent service, ServiceConnection conn, int flags, UserHandle user) {
        return this.mBase.bindServiceAsUser(service, conn, flags, user);
    }

    @Override // android.content.Context
    public void unbindService(ServiceConnection conn) {
        this.mBase.unbindService(conn);
    }

    @Override // android.content.Context
    public boolean startInstrumentation(ComponentName className, String profileFile, Bundle arguments) {
        return this.mBase.startInstrumentation(className, profileFile, arguments);
    }

    @Override // android.content.Context
    public Object getSystemService(String name) {
        return this.mBase.getSystemService(name);
    }

    @Override // android.content.Context
    public int checkPermission(String permission, int pid, int uid) {
        return this.mBase.checkPermission(permission, pid, uid);
    }

    @Override // android.content.Context
    public int checkCallingPermission(String permission) {
        return this.mBase.checkCallingPermission(permission);
    }

    @Override // android.content.Context
    public int checkCallingOrSelfPermission(String permission) {
        return this.mBase.checkCallingOrSelfPermission(permission);
    }

    @Override // android.content.Context
    public void enforcePermission(String permission, int pid, int uid, String message) {
        this.mBase.enforcePermission(permission, pid, uid, message);
    }

    @Override // android.content.Context
    public void enforceCallingPermission(String permission, String message) {
        this.mBase.enforceCallingPermission(permission, message);
    }

    @Override // android.content.Context
    public void enforceCallingOrSelfPermission(String permission, String message) {
        this.mBase.enforceCallingOrSelfPermission(permission, message);
    }

    @Override // android.content.Context
    public void grantUriPermission(String toPackage, Uri uri, int modeFlags) {
        this.mBase.grantUriPermission(toPackage, uri, modeFlags);
    }

    @Override // android.content.Context
    public void revokeUriPermission(Uri uri, int modeFlags) {
        this.mBase.revokeUriPermission(uri, modeFlags);
    }

    @Override // android.content.Context
    public int checkUriPermission(Uri uri, int pid, int uid, int modeFlags) {
        return this.mBase.checkUriPermission(uri, pid, uid, modeFlags);
    }

    @Override // android.content.Context
    public int checkCallingUriPermission(Uri uri, int modeFlags) {
        return this.mBase.checkCallingUriPermission(uri, modeFlags);
    }

    @Override // android.content.Context
    public int checkCallingOrSelfUriPermission(Uri uri, int modeFlags) {
        return this.mBase.checkCallingOrSelfUriPermission(uri, modeFlags);
    }

    @Override // android.content.Context
    public int checkUriPermission(Uri uri, String readPermission, String writePermission, int pid, int uid, int modeFlags) {
        return this.mBase.checkUriPermission(uri, readPermission, writePermission, pid, uid, modeFlags);
    }

    @Override // android.content.Context
    public void enforceUriPermission(Uri uri, int pid, int uid, int modeFlags, String message) {
        this.mBase.enforceUriPermission(uri, pid, uid, modeFlags, message);
    }

    @Override // android.content.Context
    public void enforceCallingUriPermission(Uri uri, int modeFlags, String message) {
        this.mBase.enforceCallingUriPermission(uri, modeFlags, message);
    }

    @Override // android.content.Context
    public void enforceCallingOrSelfUriPermission(Uri uri, int modeFlags, String message) {
        this.mBase.enforceCallingOrSelfUriPermission(uri, modeFlags, message);
    }

    @Override // android.content.Context
    public void enforceUriPermission(Uri uri, String readPermission, String writePermission, int pid, int uid, int modeFlags, String message) {
        this.mBase.enforceUriPermission(uri, readPermission, writePermission, pid, uid, modeFlags, message);
    }

    @Override // android.content.Context
    public Context createPackageContext(String packageName, int flags) throws PackageManager.NameNotFoundException {
        return this.mBase.createPackageContext(packageName, flags);
    }

    @Override // android.content.Context
    public Context createPackageContextAsUser(String packageName, int flags, UserHandle user) throws PackageManager.NameNotFoundException {
        return this.mBase.createPackageContextAsUser(packageName, flags, user);
    }

    @Override // android.content.Context
    public int getUserId() {
        return this.mBase.getUserId();
    }

    @Override // android.content.Context
    public Context createConfigurationContext(Configuration overrideConfiguration) {
        return this.mBase.createConfigurationContext(overrideConfiguration);
    }

    @Override // android.content.Context
    public Context createDisplayContext(Display display) {
        return this.mBase.createDisplayContext(display);
    }

    @Override // android.content.Context
    public boolean isRestricted() {
        return this.mBase.isRestricted();
    }

    @Override // android.content.Context
    public DisplayAdjustments getDisplayAdjustments(int displayId) {
        return this.mBase.getDisplayAdjustments(displayId);
    }
}