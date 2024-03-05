package android.mtp;

import android.content.IContentProvider;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import java.util.ArrayList;

/* loaded from: MtpPropertyGroup.class */
class MtpPropertyGroup {
    private static final String TAG = "MtpPropertyGroup";
    private final MtpDatabase mDatabase;
    private final IContentProvider mProvider;
    private final String mPackageName;
    private final String mVolumeName;
    private final Uri mUri;
    private final Property[] mProperties;
    private String[] mColumns;
    private static final String ID_WHERE = "_id=?";
    private static final String FORMAT_WHERE = "format=?";
    private static final String ID_FORMAT_WHERE = "_id=? AND format=?";
    private static final String PARENT_WHERE = "parent=?";
    private static final String PARENT_FORMAT_WHERE = "parent=? AND format=?";

    private native String format_date_time(long j);

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: MtpPropertyGroup$Property.class */
    public class Property {
        int code;
        int type;
        int column;

        Property(int code, int type, int column) {
            this.code = code;
            this.type = type;
            this.column = column;
        }
    }

    public MtpPropertyGroup(MtpDatabase database, IContentProvider provider, String packageName, String volume, int[] properties) {
        this.mDatabase = database;
        this.mProvider = provider;
        this.mPackageName = packageName;
        this.mVolumeName = volume;
        this.mUri = MediaStore.Files.getMtpObjectsUri(volume);
        int count = properties.length;
        ArrayList<String> columns = new ArrayList<>(count);
        columns.add("_id");
        this.mProperties = new Property[count];
        for (int i = 0; i < count; i++) {
            this.mProperties[i] = createProperty(properties[i], columns);
        }
        int count2 = columns.size();
        this.mColumns = new String[count2];
        for (int i2 = 0; i2 < count2; i2++) {
            this.mColumns[i2] = columns.get(i2);
        }
    }

    private Property createProperty(int code, ArrayList<String> columns) {
        int type;
        String column = null;
        switch (code) {
            case MtpConstants.PROPERTY_STORAGE_ID /* 56321 */:
                column = MediaStore.Files.FileColumns.STORAGE_ID;
                type = 6;
                break;
            case MtpConstants.PROPERTY_OBJECT_FORMAT /* 56322 */:
                column = "format";
                type = 4;
                break;
            case MtpConstants.PROPERTY_PROTECTION_STATUS /* 56323 */:
                type = 4;
                break;
            case MtpConstants.PROPERTY_OBJECT_SIZE /* 56324 */:
                column = "_size";
                type = 8;
                break;
            case MtpConstants.PROPERTY_OBJECT_FILE_NAME /* 56327 */:
                column = "_data";
                type = 65535;
                break;
            case MtpConstants.PROPERTY_DATE_MODIFIED /* 56329 */:
                column = "date_modified";
                type = 65535;
                break;
            case MtpConstants.PROPERTY_PARENT_OBJECT /* 56331 */:
                column = "parent";
                type = 6;
                break;
            case MtpConstants.PROPERTY_PERSISTENT_UID /* 56385 */:
                column = MediaStore.Files.FileColumns.STORAGE_ID;
                type = 10;
                break;
            case MtpConstants.PROPERTY_NAME /* 56388 */:
                column = "title";
                type = 65535;
                break;
            case MtpConstants.PROPERTY_ARTIST /* 56390 */:
                type = 65535;
                break;
            case MtpConstants.PROPERTY_DESCRIPTION /* 56392 */:
                column = "description";
                type = 65535;
                break;
            case MtpConstants.PROPERTY_DATE_ADDED /* 56398 */:
                column = "date_added";
                type = 65535;
                break;
            case MtpConstants.PROPERTY_DURATION /* 56457 */:
                column = "duration";
                type = 6;
                break;
            case MtpConstants.PROPERTY_TRACK /* 56459 */:
                column = MediaStore.Audio.AudioColumns.TRACK;
                type = 4;
                break;
            case MtpConstants.PROPERTY_GENRE /* 56460 */:
                type = 65535;
                break;
            case MtpConstants.PROPERTY_COMPOSER /* 56470 */:
                column = MediaStore.Audio.AudioColumns.COMPOSER;
                type = 65535;
                break;
            case MtpConstants.PROPERTY_ORIGINAL_RELEASE_DATE /* 56473 */:
                column = MediaStore.Audio.AudioColumns.YEAR;
                type = 65535;
                break;
            case MtpConstants.PROPERTY_ALBUM_NAME /* 56474 */:
                type = 65535;
                break;
            case MtpConstants.PROPERTY_ALBUM_ARTIST /* 56475 */:
                column = MediaStore.Audio.AudioColumns.ALBUM_ARTIST;
                type = 65535;
                break;
            case MtpConstants.PROPERTY_DISPLAY_NAME /* 56544 */:
                column = "_display_name";
                type = 65535;
                break;
            default:
                type = 0;
                Log.e(TAG, "unsupported property " + code);
                break;
        }
        if (column != null) {
            columns.add(column);
            return new Property(code, type, columns.size() - 1);
        }
        return new Property(code, type, -1);
    }

    private String queryString(int id, String column) {
        Cursor c = null;
        try {
            c = this.mProvider.query(this.mPackageName, this.mUri, new String[]{"_id", column}, ID_WHERE, new String[]{Integer.toString(id)}, null, null);
            if (c == null || !c.moveToNext()) {
                if (c != null) {
                    c.close();
                }
                return "";
            }
            String string = c.getString(1);
            if (c != null) {
                c.close();
            }
            return string;
        } catch (Exception e) {
            if (c != null) {
                c.close();
            }
            return null;
        } catch (Throwable th) {
            if (c != null) {
                c.close();
            }
            throw th;
        }
    }

    private String queryAudio(int id, String column) {
        Cursor c = null;
        try {
            c = this.mProvider.query(this.mPackageName, MediaStore.Audio.Media.getContentUri(this.mVolumeName), new String[]{"_id", column}, ID_WHERE, new String[]{Integer.toString(id)}, null, null);
            if (c == null || !c.moveToNext()) {
                if (c != null) {
                    c.close();
                }
                return "";
            }
            String string = c.getString(1);
            if (c != null) {
                c.close();
            }
            return string;
        } catch (Exception e) {
            if (c != null) {
                c.close();
            }
            return null;
        } catch (Throwable th) {
            if (c != null) {
                c.close();
            }
            throw th;
        }
    }

    private String queryGenre(int id) {
        Cursor c = null;
        try {
            try {
                Uri uri = MediaStore.Audio.Genres.getContentUriForAudioId(this.mVolumeName, id);
                c = this.mProvider.query(this.mPackageName, uri, new String[]{"_id", "name"}, null, null, null, null);
                if (c == null || !c.moveToNext()) {
                    if (c != null) {
                        c.close();
                    }
                    return "";
                }
                String string = c.getString(1);
                if (c != null) {
                    c.close();
                }
                return string;
            } catch (Exception e) {
                Log.e(TAG, "queryGenre exception", e);
                if (c != null) {
                    c.close();
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

    private Long queryLong(int id, String column) {
        Cursor c = null;
        try {
            c = this.mProvider.query(this.mPackageName, this.mUri, new String[]{"_id", column}, ID_WHERE, new String[]{Integer.toString(id)}, null, null);
            if (c == null || !c.moveToNext()) {
                if (c != null) {
                    c.close();
                    return null;
                }
                return null;
            }
            Long l = new Long(c.getLong(1));
            if (c != null) {
                c.close();
            }
            return l;
        } catch (Exception e) {
            if (c != null) {
                c.close();
                return null;
            }
            return null;
        } catch (Throwable th) {
            if (c != null) {
                c.close();
            }
            throw th;
        }
    }

    private static String nameFromPath(String path) {
        int start = 0;
        int lastSlash = path.lastIndexOf(47);
        if (lastSlash >= 0) {
            start = lastSlash + 1;
        }
        int end = path.length();
        if (end - start > 255) {
            end = start + 255;
        }
        return path.substring(start, end);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x0091, code lost:
        if (r9.mColumns.length > 1) goto L100;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public android.mtp.MtpPropertyList getPropertyList(int r10, int r11, int r12) {
        /*
            Method dump skipped, instructions count: 919
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: android.mtp.MtpPropertyGroup.getPropertyList(int, int, int):android.mtp.MtpPropertyList");
    }
}