package android.mtp;

import android.content.ContentValues;
import android.content.Context;
import android.content.IContentProvider;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaScanner;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import gov.nist.core.Separators;
import java.io.File;
import java.util.HashMap;
import java.util.Locale;

/* loaded from: MtpDatabase.class */
public class MtpDatabase {
    private static final String TAG = "MtpDatabase";
    private final Context mContext;
    private final String mPackageName;
    private final IContentProvider mMediaProvider;
    private final String mVolumeName;
    private final Uri mObjectsUri;
    private final String mMediaStoragePath;
    private final String[] mSubDirectories;
    private String mSubDirectoriesWhere;
    private String[] mSubDirectoriesWhereArgs;
    private final HashMap<String, MtpStorage> mStorageMap = new HashMap<>();
    private final HashMap<Integer, MtpPropertyGroup> mPropertyGroupsByProperty = new HashMap<>();
    private final HashMap<Integer, MtpPropertyGroup> mPropertyGroupsByFormat = new HashMap<>();
    private boolean mDatabaseModified;
    private SharedPreferences mDeviceProperties;
    private static final int DEVICE_PROPERTIES_DATABASE_VERSION = 1;
    private static final String[] ID_PROJECTION = {"_id"};
    private static final String[] PATH_PROJECTION = {"_id", "_data"};
    private static final String[] PATH_FORMAT_PROJECTION = {"_id", "_data", "format"};
    private static final String[] OBJECT_INFO_PROJECTION = {"_id", MediaStore.Files.FileColumns.STORAGE_ID, "format", "parent", "_data", "date_added", "date_modified"};
    private static final String ID_WHERE = "_id=?";
    private static final String PATH_WHERE = "_data=?";
    private static final String STORAGE_WHERE = "storage_id=?";
    private static final String FORMAT_WHERE = "format=?";
    private static final String PARENT_WHERE = "parent=?";
    private static final String STORAGE_FORMAT_WHERE = "storage_id=? AND format=?";
    private static final String STORAGE_PARENT_WHERE = "storage_id=? AND parent=?";
    private static final String FORMAT_PARENT_WHERE = "format=? AND parent=?";
    private static final String STORAGE_FORMAT_PARENT_WHERE = "storage_id=? AND format=? AND parent=?";
    private final MediaScanner mMediaScanner;
    static final int[] FILE_PROPERTIES;
    static final int[] AUDIO_PROPERTIES;
    static final int[] VIDEO_PROPERTIES;
    static final int[] IMAGE_PROPERTIES;
    static final int[] ALL_PROPERTIES;
    private int mNativeContext;

    private final native void native_setup();

    private final native void native_finalize();

    static {
        System.loadLibrary("media_jni");
        FILE_PROPERTIES = new int[]{MtpConstants.PROPERTY_STORAGE_ID, MtpConstants.PROPERTY_OBJECT_FORMAT, MtpConstants.PROPERTY_PROTECTION_STATUS, MtpConstants.PROPERTY_OBJECT_SIZE, MtpConstants.PROPERTY_OBJECT_FILE_NAME, MtpConstants.PROPERTY_DATE_MODIFIED, MtpConstants.PROPERTY_PARENT_OBJECT, MtpConstants.PROPERTY_PERSISTENT_UID, MtpConstants.PROPERTY_NAME, MtpConstants.PROPERTY_DATE_ADDED};
        AUDIO_PROPERTIES = new int[]{MtpConstants.PROPERTY_STORAGE_ID, MtpConstants.PROPERTY_OBJECT_FORMAT, MtpConstants.PROPERTY_PROTECTION_STATUS, MtpConstants.PROPERTY_OBJECT_SIZE, MtpConstants.PROPERTY_OBJECT_FILE_NAME, MtpConstants.PROPERTY_DATE_MODIFIED, MtpConstants.PROPERTY_PARENT_OBJECT, MtpConstants.PROPERTY_PERSISTENT_UID, MtpConstants.PROPERTY_NAME, MtpConstants.PROPERTY_DISPLAY_NAME, MtpConstants.PROPERTY_DATE_ADDED, MtpConstants.PROPERTY_ARTIST, MtpConstants.PROPERTY_ALBUM_NAME, MtpConstants.PROPERTY_ALBUM_ARTIST, MtpConstants.PROPERTY_TRACK, MtpConstants.PROPERTY_ORIGINAL_RELEASE_DATE, MtpConstants.PROPERTY_DURATION, MtpConstants.PROPERTY_GENRE, MtpConstants.PROPERTY_COMPOSER};
        VIDEO_PROPERTIES = new int[]{MtpConstants.PROPERTY_STORAGE_ID, MtpConstants.PROPERTY_OBJECT_FORMAT, MtpConstants.PROPERTY_PROTECTION_STATUS, MtpConstants.PROPERTY_OBJECT_SIZE, MtpConstants.PROPERTY_OBJECT_FILE_NAME, MtpConstants.PROPERTY_DATE_MODIFIED, MtpConstants.PROPERTY_PARENT_OBJECT, MtpConstants.PROPERTY_PERSISTENT_UID, MtpConstants.PROPERTY_NAME, MtpConstants.PROPERTY_DISPLAY_NAME, MtpConstants.PROPERTY_DATE_ADDED, MtpConstants.PROPERTY_ARTIST, MtpConstants.PROPERTY_ALBUM_NAME, MtpConstants.PROPERTY_DURATION, MtpConstants.PROPERTY_DESCRIPTION};
        IMAGE_PROPERTIES = new int[]{MtpConstants.PROPERTY_STORAGE_ID, MtpConstants.PROPERTY_OBJECT_FORMAT, MtpConstants.PROPERTY_PROTECTION_STATUS, MtpConstants.PROPERTY_OBJECT_SIZE, MtpConstants.PROPERTY_OBJECT_FILE_NAME, MtpConstants.PROPERTY_DATE_MODIFIED, MtpConstants.PROPERTY_PARENT_OBJECT, MtpConstants.PROPERTY_PERSISTENT_UID, MtpConstants.PROPERTY_NAME, MtpConstants.PROPERTY_DISPLAY_NAME, MtpConstants.PROPERTY_DATE_ADDED, MtpConstants.PROPERTY_DESCRIPTION};
        ALL_PROPERTIES = new int[]{MtpConstants.PROPERTY_STORAGE_ID, MtpConstants.PROPERTY_OBJECT_FORMAT, MtpConstants.PROPERTY_PROTECTION_STATUS, MtpConstants.PROPERTY_OBJECT_SIZE, MtpConstants.PROPERTY_OBJECT_FILE_NAME, MtpConstants.PROPERTY_DATE_MODIFIED, MtpConstants.PROPERTY_PARENT_OBJECT, MtpConstants.PROPERTY_PERSISTENT_UID, MtpConstants.PROPERTY_NAME, MtpConstants.PROPERTY_DISPLAY_NAME, MtpConstants.PROPERTY_DATE_ADDED, MtpConstants.PROPERTY_DESCRIPTION, MtpConstants.PROPERTY_ARTIST, MtpConstants.PROPERTY_ALBUM_NAME, MtpConstants.PROPERTY_ALBUM_ARTIST, MtpConstants.PROPERTY_TRACK, MtpConstants.PROPERTY_ORIGINAL_RELEASE_DATE, MtpConstants.PROPERTY_DURATION, MtpConstants.PROPERTY_GENRE, MtpConstants.PROPERTY_COMPOSER, MtpConstants.PROPERTY_ARTIST, MtpConstants.PROPERTY_ALBUM_NAME, MtpConstants.PROPERTY_DURATION, MtpConstants.PROPERTY_DESCRIPTION, MtpConstants.PROPERTY_DESCRIPTION};
    }

    public MtpDatabase(Context context, String volumeName, String storagePath, String[] subDirectories) {
        native_setup();
        this.mContext = context;
        this.mPackageName = context.getPackageName();
        this.mMediaProvider = context.getContentResolver().acquireProvider(MediaStore.AUTHORITY);
        this.mVolumeName = volumeName;
        this.mMediaStoragePath = storagePath;
        this.mObjectsUri = MediaStore.Files.getMtpObjectsUri(volumeName);
        this.mMediaScanner = new MediaScanner(context);
        this.mSubDirectories = subDirectories;
        if (subDirectories != null) {
            StringBuilder builder = new StringBuilder();
            builder.append(Separators.LPAREN);
            int count = subDirectories.length;
            for (int i = 0; i < count; i++) {
                builder.append("_data=? OR _data LIKE ?");
                if (i != count - 1) {
                    builder.append(" OR ");
                }
            }
            builder.append(Separators.RPAREN);
            this.mSubDirectoriesWhere = builder.toString();
            this.mSubDirectoriesWhereArgs = new String[count * 2];
            int j = 0;
            for (String path : subDirectories) {
                int i2 = j;
                int j2 = j + 1;
                this.mSubDirectoriesWhereArgs[i2] = path;
                j = j2 + 1;
                this.mSubDirectoriesWhereArgs[j2] = path + "/%";
            }
        }
        Locale locale = context.getResources().getConfiguration().locale;
        if (locale != null) {
            String language = locale.getLanguage();
            String country = locale.getCountry();
            if (language != null) {
                if (country != null) {
                    this.mMediaScanner.setLocale(language + "_" + country);
                } else {
                    this.mMediaScanner.setLocale(language);
                }
            }
        }
        initDeviceProperties(context);
    }

    protected void finalize() throws Throwable {
        try {
            native_finalize();
            super.finalize();
        } catch (Throwable th) {
            super.finalize();
            throw th;
        }
    }

    public void addStorage(MtpStorage storage) {
        this.mStorageMap.put(storage.getPath(), storage);
    }

    public void removeStorage(MtpStorage storage) {
        this.mStorageMap.remove(storage.getPath());
    }

    private void initDeviceProperties(Context context) {
        this.mDeviceProperties = context.getSharedPreferences("device-properties", 0);
        File databaseFile = context.getDatabasePath("device-properties");
        if (databaseFile.exists()) {
            SQLiteDatabase db = null;
            Cursor c = null;
            try {
                try {
                    db = context.openOrCreateDatabase("device-properties", 0, null);
                    if (db != null) {
                        c = db.query("properties", new String[]{"_id", "code", "value"}, null, null, null, null, null);
                        if (c != null) {
                            SharedPreferences.Editor e = this.mDeviceProperties.edit();
                            while (c.moveToNext()) {
                                String name = c.getString(1);
                                String value = c.getString(2);
                                e.putString(name, value);
                            }
                            e.commit();
                        }
                    }
                    if (c != null) {
                        c.close();
                    }
                    if (db != null) {
                        db.close();
                    }
                } catch (Exception e2) {
                    Log.e(TAG, "failed to migrate device properties", e2);
                    if (c != null) {
                        c.close();
                    }
                    if (db != null) {
                        db.close();
                    }
                }
                context.deleteDatabase("device-properties");
            } catch (Throwable th) {
                if (c != null) {
                    c.close();
                }
                if (db != null) {
                    db.close();
                }
                throw th;
            }
        }
    }

    private boolean inStorageSubDirectory(String path) {
        if (this.mSubDirectories == null) {
            return true;
        }
        if (path == null) {
            return false;
        }
        boolean allowed = false;
        int pathLength = path.length();
        for (int i = 0; i < this.mSubDirectories.length && !allowed; i++) {
            String subdir = this.mSubDirectories[i];
            int subdirLength = subdir.length();
            if (subdirLength < pathLength && path.charAt(subdirLength) == '/' && path.startsWith(subdir)) {
                allowed = true;
            }
        }
        return allowed;
    }

    private boolean isStorageSubDirectory(String path) {
        if (this.mSubDirectories == null) {
            return false;
        }
        for (int i = 0; i < this.mSubDirectories.length; i++) {
            if (path.equals(this.mSubDirectories[i])) {
                return true;
            }
        }
        return false;
    }

    private int beginSendObject(String path, int format, int parent, int storageId, long size, long modified) {
        if (inStorageSubDirectory(path)) {
            if (path != null) {
                Cursor c = null;
                try {
                    try {
                        c = this.mMediaProvider.query(this.mPackageName, this.mObjectsUri, ID_PROJECTION, PATH_WHERE, new String[]{path}, null, null);
                        if (c != null && c.getCount() > 0) {
                            Log.w(TAG, "file already exists in beginSendObject: " + path);
                            if (c != null) {
                                c.close();
                            }
                            return -1;
                        } else if (c != null) {
                            c.close();
                        }
                    } catch (RemoteException e) {
                        Log.e(TAG, "RemoteException in beginSendObject", e);
                        if (c != null) {
                            c.close();
                        }
                    }
                } catch (Throwable th) {
                    if (c != null) {
                        c.close();
                    }
                    throw th;
                }
            }
            this.mDatabaseModified = true;
            ContentValues values = new ContentValues();
            values.put("_data", path);
            values.put("format", Integer.valueOf(format));
            values.put("parent", Integer.valueOf(parent));
            values.put(MediaStore.Files.FileColumns.STORAGE_ID, Integer.valueOf(storageId));
            values.put("_size", Long.valueOf(size));
            values.put("date_modified", Long.valueOf(modified));
            try {
                Uri uri = this.mMediaProvider.insert(this.mPackageName, this.mObjectsUri, values);
                if (uri != null) {
                    return Integer.parseInt(uri.getPathSegments().get(2));
                }
                return -1;
            } catch (RemoteException e2) {
                Log.e(TAG, "RemoteException in beginSendObject", e2);
                return -1;
            }
        }
        return -1;
    }

    private void endSendObject(String path, int handle, int format, boolean succeeded) {
        if (succeeded) {
            if (format == 47621) {
                String name = path;
                int lastSlash = name.lastIndexOf(47);
                if (lastSlash >= 0) {
                    name = name.substring(lastSlash + 1);
                }
                if (name.endsWith(".pla")) {
                    name = name.substring(0, name.length() - 4);
                }
                ContentValues values = new ContentValues(1);
                values.put("_data", path);
                values.put("name", name);
                values.put("format", Integer.valueOf(format));
                values.put("date_modified", Long.valueOf(System.currentTimeMillis() / 1000));
                values.put(MediaStore.MediaColumns.MEDIA_SCANNER_NEW_OBJECT_ID, Integer.valueOf(handle));
                try {
                    this.mMediaProvider.insert(this.mPackageName, MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, values);
                    return;
                } catch (RemoteException e) {
                    Log.e(TAG, "RemoteException in endSendObject", e);
                    return;
                }
            }
            this.mMediaScanner.scanMtpFile(path, this.mVolumeName, handle, format);
            return;
        }
        deleteFile(handle);
    }

    private Cursor createObjectQuery(int storageID, int format, int parent) throws RemoteException {
        String where;
        String[] whereArgs;
        if (storageID == -1) {
            if (format == 0) {
                if (parent == 0) {
                    where = null;
                    whereArgs = null;
                } else {
                    if (parent == -1) {
                        parent = 0;
                    }
                    where = PARENT_WHERE;
                    whereArgs = new String[]{Integer.toString(parent)};
                }
            } else if (parent == 0) {
                where = FORMAT_WHERE;
                whereArgs = new String[]{Integer.toString(format)};
            } else {
                if (parent == -1) {
                    parent = 0;
                }
                where = FORMAT_PARENT_WHERE;
                whereArgs = new String[]{Integer.toString(format), Integer.toString(parent)};
            }
        } else if (format == 0) {
            if (parent == 0) {
                where = STORAGE_WHERE;
                whereArgs = new String[]{Integer.toString(storageID)};
            } else {
                if (parent == -1) {
                    parent = 0;
                }
                where = STORAGE_PARENT_WHERE;
                whereArgs = new String[]{Integer.toString(storageID), Integer.toString(parent)};
            }
        } else if (parent == 0) {
            where = STORAGE_FORMAT_WHERE;
            whereArgs = new String[]{Integer.toString(storageID), Integer.toString(format)};
        } else {
            if (parent == -1) {
                parent = 0;
            }
            where = STORAGE_FORMAT_PARENT_WHERE;
            whereArgs = new String[]{Integer.toString(storageID), Integer.toString(format), Integer.toString(parent)};
        }
        if (this.mSubDirectoriesWhere != null) {
            if (where == null) {
                where = this.mSubDirectoriesWhere;
                whereArgs = this.mSubDirectoriesWhereArgs;
            } else {
                where = where + " AND " + this.mSubDirectoriesWhere;
                String[] newWhereArgs = new String[whereArgs.length + this.mSubDirectoriesWhereArgs.length];
                int i = 0;
                while (i < whereArgs.length) {
                    newWhereArgs[i] = whereArgs[i];
                    i++;
                }
                for (int j = 0; j < this.mSubDirectoriesWhereArgs.length; j++) {
                    newWhereArgs[i] = this.mSubDirectoriesWhereArgs[j];
                    i++;
                }
                whereArgs = newWhereArgs;
            }
        }
        return this.mMediaProvider.query(this.mPackageName, this.mObjectsUri, ID_PROJECTION, where, whereArgs, null, null);
    }

    private int[] getObjectList(int storageID, int format, int parent) {
        Cursor c = null;
        try {
            try {
                c = createObjectQuery(storageID, format, parent);
                if (c == null) {
                    if (c != null) {
                        c.close();
                    }
                    return null;
                }
                int count = c.getCount();
                if (count <= 0) {
                    if (c != null) {
                        c.close();
                        return null;
                    }
                    return null;
                }
                int[] result = new int[count];
                for (int i = 0; i < count; i++) {
                    c.moveToNext();
                    result[i] = c.getInt(0);
                }
                if (c != null) {
                    c.close();
                }
                return result;
            } catch (RemoteException e) {
                Log.e(TAG, "RemoteException in getObjectList", e);
                if (c != null) {
                    c.close();
                    return null;
                }
                return null;
            }
        } catch (Throwable th) {
            if (c != null) {
                c.close();
            }
            throw th;
        }
    }

    private int getNumObjects(int storageID, int format, int parent) {
        Cursor c = null;
        try {
            try {
                c = createObjectQuery(storageID, format, parent);
                if (c != null) {
                    int count = c.getCount();
                    if (c != null) {
                        c.close();
                    }
                    return count;
                } else if (c != null) {
                    c.close();
                    return -1;
                } else {
                    return -1;
                }
            } catch (RemoteException e) {
                Log.e(TAG, "RemoteException in getNumObjects", e);
                if (c != null) {
                    c.close();
                    return -1;
                }
                return -1;
            }
        } catch (Throwable th) {
            if (c != null) {
                c.close();
            }
            throw th;
        }
    }

    private int[] getSupportedPlaybackFormats() {
        return new int[]{12288, 12289, 12292, 12293, 12296, 12297, 12299, MtpConstants.FORMAT_EXIF_JPEG, MtpConstants.FORMAT_TIFF_EP, MtpConstants.FORMAT_BMP, MtpConstants.FORMAT_GIF, MtpConstants.FORMAT_JFIF, MtpConstants.FORMAT_PNG, MtpConstants.FORMAT_TIFF, MtpConstants.FORMAT_WMA, MtpConstants.FORMAT_OGG, MtpConstants.FORMAT_AAC, MtpConstants.FORMAT_MP4_CONTAINER, MtpConstants.FORMAT_MP2, MtpConstants.FORMAT_3GP_CONTAINER, MtpConstants.FORMAT_ABSTRACT_AV_PLAYLIST, MtpConstants.FORMAT_WPL_PLAYLIST, MtpConstants.FORMAT_M3U_PLAYLIST, MtpConstants.FORMAT_PLS_PLAYLIST, MtpConstants.FORMAT_XML_DOCUMENT, MtpConstants.FORMAT_FLAC};
    }

    private int[] getSupportedCaptureFormats() {
        return null;
    }

    private int[] getSupportedObjectProperties(int format) {
        switch (format) {
            case 0:
                return ALL_PROPERTIES;
            case 12296:
            case 12297:
            case MtpConstants.FORMAT_WMA /* 47361 */:
            case MtpConstants.FORMAT_OGG /* 47362 */:
            case MtpConstants.FORMAT_AAC /* 47363 */:
                return AUDIO_PROPERTIES;
            case 12299:
            case MtpConstants.FORMAT_WMV /* 47489 */:
            case MtpConstants.FORMAT_3GP_CONTAINER /* 47492 */:
                return VIDEO_PROPERTIES;
            case MtpConstants.FORMAT_EXIF_JPEG /* 14337 */:
            case MtpConstants.FORMAT_BMP /* 14340 */:
            case MtpConstants.FORMAT_GIF /* 14343 */:
            case MtpConstants.FORMAT_PNG /* 14347 */:
                return IMAGE_PROPERTIES;
            default:
                return FILE_PROPERTIES;
        }
    }

    private int[] getSupportedDeviceProperties() {
        return new int[]{MtpConstants.DEVICE_PROPERTY_SYNCHRONIZATION_PARTNER, MtpConstants.DEVICE_PROPERTY_DEVICE_FRIENDLY_NAME, MtpConstants.DEVICE_PROPERTY_IMAGE_SIZE};
    }

    private MtpPropertyList getObjectPropertyList(long handle, int format, long property, int groupCode, int depth) {
        MtpPropertyGroup propertyGroup;
        if (groupCode != 0) {
            return new MtpPropertyList(0, MtpConstants.RESPONSE_SPECIFICATION_BY_GROUP_UNSUPPORTED);
        }
        if (property == ExpandableListView.PACKED_POSITION_VALUE_NULL) {
            propertyGroup = this.mPropertyGroupsByFormat.get(Integer.valueOf(format));
            if (propertyGroup == null) {
                int[] propertyList = getSupportedObjectProperties(format);
                propertyGroup = new MtpPropertyGroup(this, this.mMediaProvider, this.mPackageName, this.mVolumeName, propertyList);
                this.mPropertyGroupsByFormat.put(new Integer(format), propertyGroup);
            }
        } else {
            propertyGroup = this.mPropertyGroupsByProperty.get(Long.valueOf(property));
            if (propertyGroup == null) {
                int[] propertyList2 = {(int) property};
                propertyGroup = new MtpPropertyGroup(this, this.mMediaProvider, this.mPackageName, this.mVolumeName, propertyList2);
                this.mPropertyGroupsByProperty.put(new Integer((int) property), propertyGroup);
            }
        }
        return propertyGroup.getPropertyList((int) handle, format, depth);
    }

    private int renameFile(int handle, String newName) {
        Cursor c = null;
        String path = null;
        String[] whereArgs = {Integer.toString(handle)};
        try {
            try {
                c = this.mMediaProvider.query(this.mPackageName, this.mObjectsUri, PATH_PROJECTION, ID_WHERE, whereArgs, null, null);
                if (c != null && c.moveToNext()) {
                    path = c.getString(1);
                }
                if (c != null) {
                    c.close();
                }
                if (path == null) {
                    return 8201;
                }
                if (isStorageSubDirectory(path)) {
                    return MtpConstants.RESPONSE_OBJECT_WRITE_PROTECTED;
                }
                File oldFile = new File(path);
                int lastSlash = path.lastIndexOf(47);
                if (lastSlash <= 1) {
                    return 8194;
                }
                String newPath = path.substring(0, lastSlash + 1) + newName;
                File newFile = new File(newPath);
                boolean success = oldFile.renameTo(newFile);
                if (!success) {
                    Log.w(TAG, "renaming " + path + " to " + newPath + " failed");
                    return 8194;
                }
                ContentValues values = new ContentValues();
                values.put("_data", newPath);
                int updated = 0;
                try {
                    updated = this.mMediaProvider.update(this.mPackageName, this.mObjectsUri, values, ID_WHERE, whereArgs);
                } catch (RemoteException e) {
                    Log.e(TAG, "RemoteException in mMediaProvider.update", e);
                }
                if (updated == 0) {
                    Log.e(TAG, "Unable to update path for " + path + " to " + newPath);
                    newFile.renameTo(oldFile);
                    return 8194;
                } else if (newFile.isDirectory()) {
                    if (oldFile.getName().startsWith(Separators.DOT) && !newPath.startsWith(Separators.DOT)) {
                        try {
                            this.mMediaProvider.call(this.mPackageName, MediaStore.UNHIDE_CALL, newPath, null);
                            return MtpConstants.RESPONSE_OK;
                        } catch (RemoteException e2) {
                            Log.e(TAG, "failed to unhide/rescan for " + newPath);
                            return MtpConstants.RESPONSE_OK;
                        }
                    }
                    return MtpConstants.RESPONSE_OK;
                } else if (oldFile.getName().toLowerCase(Locale.US).equals(MediaStore.MEDIA_IGNORE_FILENAME) && !newPath.toLowerCase(Locale.US).equals(MediaStore.MEDIA_IGNORE_FILENAME)) {
                    try {
                        this.mMediaProvider.call(this.mPackageName, MediaStore.UNHIDE_CALL, oldFile.getParent(), null);
                        return MtpConstants.RESPONSE_OK;
                    } catch (RemoteException e3) {
                        Log.e(TAG, "failed to unhide/rescan for " + newPath);
                        return MtpConstants.RESPONSE_OK;
                    }
                } else {
                    return MtpConstants.RESPONSE_OK;
                }
            } catch (Throwable th) {
                if (c != null) {
                    c.close();
                }
                throw th;
            }
        } catch (RemoteException e4) {
            Log.e(TAG, "RemoteException in getObjectFilePath", e4);
            if (c != null) {
                c.close();
            }
            return 8194;
        }
    }

    private int setObjectProperty(int handle, int property, long intValue, String stringValue) {
        switch (property) {
            case MtpConstants.PROPERTY_OBJECT_FILE_NAME /* 56327 */:
                return renameFile(handle, stringValue);
            default:
                return MtpConstants.RESPONSE_OBJECT_PROP_NOT_SUPPORTED;
        }
    }

    private int getDeviceProperty(int property, long[] outIntValue, char[] outStringValue) {
        switch (property) {
            case MtpConstants.DEVICE_PROPERTY_IMAGE_SIZE /* 20483 */:
                Display display = ((WindowManager) this.mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                int width = display.getMaximumSizeDimension();
                int height = display.getMaximumSizeDimension();
                String imageSize = Integer.toString(width) + "x" + Integer.toString(height);
                imageSize.getChars(0, imageSize.length(), outStringValue, 0);
                outStringValue[imageSize.length()] = 0;
                return MtpConstants.RESPONSE_OK;
            case MtpConstants.DEVICE_PROPERTY_SYNCHRONIZATION_PARTNER /* 54273 */:
            case MtpConstants.DEVICE_PROPERTY_DEVICE_FRIENDLY_NAME /* 54274 */:
                String value = this.mDeviceProperties.getString(Integer.toString(property), "");
                int length = value.length();
                if (length > 255) {
                    length = 255;
                }
                value.getChars(0, length, outStringValue, 0);
                outStringValue[length] = 0;
                return MtpConstants.RESPONSE_OK;
            default:
                return MtpConstants.RESPONSE_DEVICE_PROP_NOT_SUPPORTED;
        }
    }

    private int setDeviceProperty(int property, long intValue, String stringValue) {
        switch (property) {
            case MtpConstants.DEVICE_PROPERTY_SYNCHRONIZATION_PARTNER /* 54273 */:
            case MtpConstants.DEVICE_PROPERTY_DEVICE_FRIENDLY_NAME /* 54274 */:
                SharedPreferences.Editor e = this.mDeviceProperties.edit();
                e.putString(Integer.toString(property), stringValue);
                if (e.commit()) {
                    return MtpConstants.RESPONSE_OK;
                }
                return 8194;
            default:
                return MtpConstants.RESPONSE_DEVICE_PROP_NOT_SUPPORTED;
        }
    }

    private boolean getObjectInfo(int handle, int[] outStorageFormatParent, char[] outName, long[] outCreatedModified) {
        Cursor c = null;
        try {
            try {
                c = this.mMediaProvider.query(this.mPackageName, this.mObjectsUri, OBJECT_INFO_PROJECTION, ID_WHERE, new String[]{Integer.toString(handle)}, null, null);
                if (c == null || !c.moveToNext()) {
                    if (c != null) {
                        c.close();
                        return false;
                    }
                    return false;
                }
                outStorageFormatParent[0] = c.getInt(1);
                outStorageFormatParent[1] = c.getInt(2);
                outStorageFormatParent[2] = c.getInt(3);
                String path = c.getString(4);
                int lastSlash = path.lastIndexOf(47);
                int start = lastSlash >= 0 ? lastSlash + 1 : 0;
                int end = path.length();
                if (end - start > 255) {
                    end = start + 255;
                }
                path.getChars(start, end, outName, 0);
                outName[end - start] = 0;
                outCreatedModified[0] = c.getLong(5);
                outCreatedModified[1] = c.getLong(6);
                if (outCreatedModified[0] == 0) {
                    outCreatedModified[0] = outCreatedModified[1];
                }
                if (c != null) {
                    c.close();
                }
                return true;
            } catch (RemoteException e) {
                Log.e(TAG, "RemoteException in getObjectInfo", e);
                if (c != null) {
                    c.close();
                    return false;
                }
                return false;
            }
        } catch (Throwable th) {
            if (c != null) {
                c.close();
            }
            throw th;
        }
    }

    private int getObjectFilePath(int handle, char[] outFilePath, long[] outFileLengthFormat) {
        if (handle == 0) {
            this.mMediaStoragePath.getChars(0, this.mMediaStoragePath.length(), outFilePath, 0);
            outFilePath[this.mMediaStoragePath.length()] = 0;
            outFileLengthFormat[0] = 0;
            outFileLengthFormat[1] = 12289;
            return MtpConstants.RESPONSE_OK;
        }
        Cursor c = null;
        try {
            try {
                c = this.mMediaProvider.query(this.mPackageName, this.mObjectsUri, PATH_FORMAT_PROJECTION, ID_WHERE, new String[]{Integer.toString(handle)}, null, null);
                if (c == null || !c.moveToNext()) {
                    if (c != null) {
                        c.close();
                    }
                    return 8201;
                }
                String path = c.getString(1);
                path.getChars(0, path.length(), outFilePath, 0);
                outFilePath[path.length()] = 0;
                outFileLengthFormat[0] = new File(path).length();
                outFileLengthFormat[1] = c.getLong(2);
                if (c != null) {
                    c.close();
                }
                return MtpConstants.RESPONSE_OK;
            } catch (RemoteException e) {
                Log.e(TAG, "RemoteException in getObjectFilePath", e);
                if (c != null) {
                    c.close();
                }
                return 8194;
            }
        } catch (Throwable th) {
            if (c != null) {
                c.close();
            }
            throw th;
        }
    }

    private int deleteFile(int handle) {
        this.mDatabaseModified = true;
        Cursor c = null;
        try {
            try {
                c = this.mMediaProvider.query(this.mPackageName, this.mObjectsUri, PATH_FORMAT_PROJECTION, ID_WHERE, new String[]{Integer.toString(handle)}, null, null);
                if (c == null || !c.moveToNext()) {
                    if (c != null) {
                        c.close();
                    }
                    return 8201;
                }
                String path = c.getString(1);
                int format = c.getInt(2);
                if (path == null || format == 0) {
                    if (c != null) {
                        c.close();
                    }
                    return 8194;
                } else if (isStorageSubDirectory(path)) {
                    if (c != null) {
                        c.close();
                    }
                    return MtpConstants.RESPONSE_OBJECT_WRITE_PROTECTED;
                } else {
                    if (format == 12289) {
                        Uri uri = MediaStore.Files.getMtpObjectsUri(this.mVolumeName);
                        this.mMediaProvider.delete(this.mPackageName, uri, "_data LIKE ?1 AND lower(substr(_data,1,?2))=lower(?3)", new String[]{path + "/%", Integer.toString(path.length() + 1), path + Separators.SLASH});
                    }
                    Uri uri2 = MediaStore.Files.getMtpObjectsUri(this.mVolumeName, handle);
                    if (this.mMediaProvider.delete(this.mPackageName, uri2, null, null) <= 0) {
                        if (c != null) {
                            c.close();
                        }
                        return 8201;
                    }
                    if (format != 12289 && path.toLowerCase(Locale.US).endsWith("/.nomedia")) {
                        try {
                            String parentPath = path.substring(0, path.lastIndexOf(Separators.SLASH));
                            this.mMediaProvider.call(this.mPackageName, MediaStore.UNHIDE_CALL, parentPath, null);
                        } catch (RemoteException e) {
                            Log.e(TAG, "failed to unhide/rescan for " + path);
                        }
                    }
                    if (c != null) {
                        c.close();
                    }
                    return MtpConstants.RESPONSE_OK;
                }
            } catch (RemoteException e2) {
                Log.e(TAG, "RemoteException in deleteFile", e2);
                if (c != null) {
                    c.close();
                }
                return 8194;
            }
        } catch (Throwable th) {
            if (c != null) {
                c.close();
            }
            throw th;
        }
    }

    private int[] getObjectReferences(int handle) {
        Uri uri = MediaStore.Files.getMtpReferencesUri(this.mVolumeName, handle);
        Cursor c = null;
        try {
            try {
                c = this.mMediaProvider.query(this.mPackageName, uri, ID_PROJECTION, null, null, null, null);
                if (c == null) {
                    if (c != null) {
                        c.close();
                    }
                    return null;
                }
                int count = c.getCount();
                if (count <= 0) {
                    if (c != null) {
                        c.close();
                        return null;
                    }
                    return null;
                }
                int[] result = new int[count];
                for (int i = 0; i < count; i++) {
                    c.moveToNext();
                    result[i] = c.getInt(0);
                }
                if (c != null) {
                    c.close();
                }
                return result;
            } catch (RemoteException e) {
                Log.e(TAG, "RemoteException in getObjectList", e);
                if (c != null) {
                    c.close();
                    return null;
                }
                return null;
            }
        } catch (Throwable th) {
            if (c != null) {
                c.close();
            }
            throw th;
        }
    }

    private int setObjectReferences(int handle, int[] references) {
        this.mDatabaseModified = true;
        Uri uri = MediaStore.Files.getMtpReferencesUri(this.mVolumeName, handle);
        int count = references.length;
        ContentValues[] valuesList = new ContentValues[count];
        for (int i = 0; i < count; i++) {
            ContentValues values = new ContentValues();
            values.put("_id", Integer.valueOf(references[i]));
            valuesList[i] = values;
        }
        try {
            if (this.mMediaProvider.bulkInsert(this.mPackageName, uri, valuesList) > 0) {
                return MtpConstants.RESPONSE_OK;
            }
            return 8194;
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException in setObjectReferences", e);
            return 8194;
        }
    }

    private void sessionStarted() {
        this.mDatabaseModified = false;
    }

    private void sessionEnded() {
        if (this.mDatabaseModified) {
            this.mContext.sendBroadcast(new Intent(MediaStore.ACTION_MTP_SESSION_END));
            this.mDatabaseModified = false;
        }
    }
}