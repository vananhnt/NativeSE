package android.provider;

import android.app.backup.FullBackup;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MiniThumbFile;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.Contacts;
import android.util.Log;
import gov.nist.core.Separators;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* loaded from: MediaStore.class */
public final class MediaStore {
    private static final String TAG = "MediaStore";
    public static final String AUTHORITY = "media";
    private static final String CONTENT_AUTHORITY_SLASH = "content://media/";
    public static final String ACTION_MTP_SESSION_END = "android.provider.action.MTP_SESSION_END";
    public static final String UNHIDE_CALL = "unhide";
    public static final String PARAM_DELETE_DATA = "deletedata";
    @Deprecated
    public static final String INTENT_ACTION_MUSIC_PLAYER = "android.intent.action.MUSIC_PLAYER";
    public static final String INTENT_ACTION_MEDIA_SEARCH = "android.intent.action.MEDIA_SEARCH";
    public static final String INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH = "android.media.action.MEDIA_PLAY_FROM_SEARCH";
    public static final String INTENT_ACTION_TEXT_OPEN_FROM_SEARCH = "android.media.action.TEXT_OPEN_FROM_SEARCH";
    public static final String INTENT_ACTION_VIDEO_PLAY_FROM_SEARCH = "android.media.action.VIDEO_PLAY_FROM_SEARCH";
    public static final String EXTRA_MEDIA_ARTIST = "android.intent.extra.artist";
    public static final String EXTRA_MEDIA_ALBUM = "android.intent.extra.album";
    public static final String EXTRA_MEDIA_TITLE = "android.intent.extra.title";
    public static final String EXTRA_MEDIA_FOCUS = "android.intent.extra.focus";
    public static final String EXTRA_SCREEN_ORIENTATION = "android.intent.extra.screenOrientation";
    public static final String EXTRA_FULL_SCREEN = "android.intent.extra.fullScreen";
    public static final String EXTRA_SHOW_ACTION_ICONS = "android.intent.extra.showActionIcons";
    public static final String EXTRA_FINISH_ON_COMPLETION = "android.intent.extra.finishOnCompletion";
    public static final String INTENT_ACTION_STILL_IMAGE_CAMERA = "android.media.action.STILL_IMAGE_CAMERA";
    public static final String INTENT_ACTION_STILL_IMAGE_CAMERA_SECURE = "android.media.action.STILL_IMAGE_CAMERA_SECURE";
    public static final String INTENT_ACTION_VIDEO_CAMERA = "android.media.action.VIDEO_CAMERA";
    public static final String ACTION_IMAGE_CAPTURE = "android.media.action.IMAGE_CAPTURE";
    public static final String ACTION_IMAGE_CAPTURE_SECURE = "android.media.action.IMAGE_CAPTURE_SECURE";
    public static final String ACTION_VIDEO_CAPTURE = "android.media.action.VIDEO_CAPTURE";
    public static final String EXTRA_VIDEO_QUALITY = "android.intent.extra.videoQuality";
    public static final String EXTRA_SIZE_LIMIT = "android.intent.extra.sizeLimit";
    public static final String EXTRA_DURATION_LIMIT = "android.intent.extra.durationLimit";
    public static final String EXTRA_OUTPUT = "output";
    public static final String UNKNOWN_STRING = "<unknown>";
    public static final String MEDIA_SCANNER_VOLUME = "volume";
    public static final String MEDIA_IGNORE_FILENAME = ".nomedia";

    /* loaded from: MediaStore$MediaColumns.class */
    public interface MediaColumns extends BaseColumns {
        public static final String DATA = "_data";
        public static final String SIZE = "_size";
        public static final String DISPLAY_NAME = "_display_name";
        public static final String TITLE = "title";
        public static final String DATE_ADDED = "date_added";
        public static final String DATE_MODIFIED = "date_modified";
        public static final String MIME_TYPE = "mime_type";
        public static final String MEDIA_SCANNER_NEW_OBJECT_ID = "media_scanner_new_object_id";
        public static final String IS_DRM = "is_drm";
        public static final String WIDTH = "width";
        public static final String HEIGHT = "height";
    }

    /* loaded from: MediaStore$Files.class */
    public static final class Files {

        /* loaded from: MediaStore$Files$FileColumns.class */
        public interface FileColumns extends MediaColumns {
            public static final String STORAGE_ID = "storage_id";
            public static final String FORMAT = "format";
            public static final String PARENT = "parent";
            public static final String MIME_TYPE = "mime_type";
            public static final String TITLE = "title";
            public static final String MEDIA_TYPE = "media_type";
            public static final int MEDIA_TYPE_NONE = 0;
            public static final int MEDIA_TYPE_IMAGE = 1;
            public static final int MEDIA_TYPE_AUDIO = 2;
            public static final int MEDIA_TYPE_VIDEO = 3;
            public static final int MEDIA_TYPE_PLAYLIST = 4;
        }

        public static Uri getContentUri(String volumeName) {
            return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + volumeName + "/file");
        }

        public static final Uri getContentUri(String volumeName, long rowId) {
            return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + volumeName + "/file/" + rowId);
        }

        public static Uri getMtpObjectsUri(String volumeName) {
            return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + volumeName + "/object");
        }

        public static final Uri getMtpObjectsUri(String volumeName, long fileId) {
            return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + volumeName + "/object/" + fileId);
        }

        public static final Uri getMtpReferencesUri(String volumeName, long fileId) {
            return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + volumeName + "/object/" + fileId + "/references");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: MediaStore$InternalThumbnails.class */
    public static class InternalThumbnails implements BaseColumns {
        private static final int MINI_KIND = 1;
        private static final int FULL_SCREEN_KIND = 2;
        private static final int MICRO_KIND = 3;
        static final int DEFAULT_GROUP_ID = 0;
        private static byte[] sThumbBuf;
        private static final String[] PROJECTION = {"_id", "_data"};
        private static final Object sThumbBufLock = new Object();

        private InternalThumbnails() {
        }

        private static Bitmap getMiniThumbFromFile(Cursor c, Uri baseUri, ContentResolver cr, BitmapFactory.Options options) {
            Bitmap bitmap = null;
            Uri thumbUri = null;
            try {
                long thumbId = c.getLong(0);
                c.getString(1);
                thumbUri = ContentUris.withAppendedId(baseUri, thumbId);
                ParcelFileDescriptor pfdInput = cr.openFileDescriptor(thumbUri, FullBackup.ROOT_TREE_TOKEN);
                bitmap = BitmapFactory.decodeFileDescriptor(pfdInput.getFileDescriptor(), null, options);
                pfdInput.close();
            } catch (FileNotFoundException ex) {
                Log.e(MediaStore.TAG, "couldn't open thumbnail " + thumbUri + "; " + ex);
            } catch (IOException ex2) {
                Log.e(MediaStore.TAG, "couldn't open thumbnail " + thumbUri + "; " + ex2);
            } catch (OutOfMemoryError ex3) {
                Log.e(MediaStore.TAG, "failed to allocate memory for thumbnail " + thumbUri + "; " + ex3);
            }
            return bitmap;
        }

        static void cancelThumbnailRequest(ContentResolver cr, long origId, Uri baseUri, long groupId) {
            Uri cancelUri = baseUri.buildUpon().appendQueryParameter("cancel", "1").appendQueryParameter("orig_id", String.valueOf(origId)).appendQueryParameter(Contacts.GroupMembership.GROUP_ID, String.valueOf(groupId)).build();
            Cursor c = null;
            try {
                c = cr.query(cancelUri, PROJECTION, null, null, null);
                if (c != null) {
                    c.close();
                }
            } catch (Throwable th) {
                if (c != null) {
                    c.close();
                }
                throw th;
            }
        }

        static Bitmap getThumbnail(ContentResolver cr, long origId, long groupId, int kind, BitmapFactory.Options options, Uri baseUri, boolean isVideo) {
            Cursor c;
            Bitmap bitmap = null;
            String filePath = null;
            MiniThumbFile thumbFile = new MiniThumbFile(isVideo ? Video.Media.EXTERNAL_CONTENT_URI : Images.Media.EXTERNAL_CONTENT_URI);
            Cursor c2 = null;
            try {
                try {
                    long magic = thumbFile.getMagic(origId);
                    if (magic != 0) {
                        if (kind == 3) {
                            synchronized (sThumbBufLock) {
                                if (sThumbBuf == null) {
                                    sThumbBuf = new byte[10000];
                                }
                                if (thumbFile.getMiniThumbFromFile(origId, sThumbBuf) != null) {
                                    bitmap = BitmapFactory.decodeByteArray(sThumbBuf, 0, sThumbBuf.length);
                                    if (bitmap == null) {
                                        Log.w(MediaStore.TAG, "couldn't decode byte array.");
                                    }
                                }
                            }
                            Bitmap bitmap2 = bitmap;
                            if (0 != 0) {
                                c2.close();
                            }
                            thumbFile.deactivate();
                            return bitmap2;
                        } else if (kind == 1) {
                            String column = isVideo ? "video_id=" : "image_id=";
                            c2 = cr.query(baseUri, PROJECTION, column + origId, null, null);
                            if (c2 != null && c2.moveToFirst()) {
                                bitmap = getMiniThumbFromFile(c2, baseUri, cr, options);
                                if (bitmap != null) {
                                    if (c2 != null) {
                                        c2.close();
                                    }
                                    thumbFile.deactivate();
                                    return bitmap;
                                }
                            }
                        }
                    }
                    Uri blockingUri = baseUri.buildUpon().appendQueryParameter("blocking", "1").appendQueryParameter("orig_id", String.valueOf(origId)).appendQueryParameter(Contacts.GroupMembership.GROUP_ID, String.valueOf(groupId)).build();
                    if (c2 != null) {
                        c2.close();
                    }
                    c = cr.query(blockingUri, PROJECTION, null, null, null);
                } catch (SQLiteException ex) {
                    Log.w(MediaStore.TAG, ex);
                    if (0 != 0) {
                        c2.close();
                    }
                    thumbFile.deactivate();
                }
                if (c == null) {
                    if (c != null) {
                        c.close();
                    }
                    thumbFile.deactivate();
                    return null;
                }
                if (kind == 3) {
                    synchronized (sThumbBufLock) {
                        if (sThumbBuf == null) {
                            sThumbBuf = new byte[10000];
                        }
                        if (thumbFile.getMiniThumbFromFile(origId, sThumbBuf) != null) {
                            bitmap = BitmapFactory.decodeByteArray(sThumbBuf, 0, sThumbBuf.length);
                            if (bitmap == null) {
                                Log.w(MediaStore.TAG, "couldn't decode byte array.");
                            }
                        }
                    }
                } else if (kind != 1) {
                    throw new IllegalArgumentException("Unsupported kind: " + kind);
                } else {
                    if (c.moveToFirst()) {
                        bitmap = getMiniThumbFromFile(c, baseUri, cr, options);
                    }
                }
                if (bitmap == null) {
                    Log.v(MediaStore.TAG, "Create the thumbnail in memory: origId=" + origId + ", kind=" + kind + ", isVideo=" + isVideo);
                    Uri uri = Uri.parse(baseUri.buildUpon().appendPath(String.valueOf(origId)).toString().replaceFirst("thumbnails", MediaStore.AUTHORITY));
                    if (0 == 0) {
                        if (c != null) {
                            c.close();
                        }
                        c = cr.query(uri, PROJECTION, null, null, null);
                        if (c == null || !c.moveToFirst()) {
                            if (c != null) {
                                c.close();
                            }
                            thumbFile.deactivate();
                            return null;
                        }
                        filePath = c.getString(1);
                    }
                    bitmap = isVideo ? ThumbnailUtils.createVideoThumbnail(filePath, kind) : ThumbnailUtils.createImageThumbnail(filePath, kind);
                }
                if (c != null) {
                    c.close();
                }
                thumbFile.deactivate();
                return bitmap;
            } catch (Throwable th) {
                if (0 != 0) {
                    c2.close();
                }
                thumbFile.deactivate();
                throw th;
            }
        }
    }

    /* loaded from: MediaStore$Images.class */
    public static final class Images {

        /* loaded from: MediaStore$Images$ImageColumns.class */
        public interface ImageColumns extends MediaColumns {
            public static final String DESCRIPTION = "description";
            public static final String PICASA_ID = "picasa_id";
            public static final String IS_PRIVATE = "isprivate";
            public static final String LATITUDE = "latitude";
            public static final String LONGITUDE = "longitude";
            public static final String DATE_TAKEN = "datetaken";
            public static final String ORIENTATION = "orientation";
            public static final String MINI_THUMB_MAGIC = "mini_thumb_magic";
            public static final String BUCKET_ID = "bucket_id";
            public static final String BUCKET_DISPLAY_NAME = "bucket_display_name";
        }

        /* loaded from: MediaStore$Images$Media.class */
        public static final class Media implements ImageColumns {
            public static final Uri INTERNAL_CONTENT_URI = getContentUri("internal");
            public static final Uri EXTERNAL_CONTENT_URI = getContentUri("external");
            public static final String CONTENT_TYPE = "vnd.android.cursor.dir/image";
            public static final String DEFAULT_SORT_ORDER = "bucket_display_name";

            public static final Cursor query(ContentResolver cr, Uri uri, String[] projection) {
                return cr.query(uri, projection, null, null, "bucket_display_name");
            }

            public static final Cursor query(ContentResolver cr, Uri uri, String[] projection, String where, String orderBy) {
                return cr.query(uri, projection, where, null, orderBy == null ? "bucket_display_name" : orderBy);
            }

            public static final Cursor query(ContentResolver cr, Uri uri, String[] projection, String selection, String[] selectionArgs, String orderBy) {
                return cr.query(uri, projection, selection, selectionArgs, orderBy == null ? "bucket_display_name" : orderBy);
            }

            public static final Bitmap getBitmap(ContentResolver cr, Uri url) throws FileNotFoundException, IOException {
                InputStream input = cr.openInputStream(url);
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                input.close();
                return bitmap;
            }

            public static final String insertImage(ContentResolver cr, String imagePath, String name, String description) throws FileNotFoundException {
                FileInputStream stream = new FileInputStream(imagePath);
                try {
                    Bitmap bm = BitmapFactory.decodeFile(imagePath);
                    String ret = insertImage(cr, bm, name, description);
                    bm.recycle();
                    return ret;
                } finally {
                    try {
                        stream.close();
                    } catch (IOException e) {
                    }
                }
            }

            private static final Bitmap StoreThumbnail(ContentResolver cr, Bitmap source, long id, float width, float height, int kind) {
                Matrix matrix = new Matrix();
                float scaleX = width / source.getWidth();
                float scaleY = height / source.getHeight();
                matrix.setScale(scaleX, scaleY);
                Bitmap thumb = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
                ContentValues values = new ContentValues(4);
                values.put("kind", Integer.valueOf(kind));
                values.put("image_id", Integer.valueOf((int) id));
                values.put("height", Integer.valueOf(thumb.getHeight()));
                values.put("width", Integer.valueOf(thumb.getWidth()));
                Uri url = cr.insert(Thumbnails.EXTERNAL_CONTENT_URI, values);
                try {
                    OutputStream thumbOut = cr.openOutputStream(url);
                    thumb.compress(Bitmap.CompressFormat.JPEG, 100, thumbOut);
                    thumbOut.close();
                    return thumb;
                } catch (FileNotFoundException e) {
                    return null;
                } catch (IOException e2) {
                    return null;
                }
            }

            public static final String insertImage(ContentResolver cr, Bitmap source, String title, String description) {
                ContentValues values = new ContentValues();
                values.put("title", title);
                values.put("description", description);
                values.put("mime_type", "image/jpeg");
                Uri url = null;
                String stringUrl = null;
                try {
                    url = cr.insert(EXTERNAL_CONTENT_URI, values);
                    if (source != null) {
                        OutputStream imageOut = cr.openOutputStream(url);
                        source.compress(Bitmap.CompressFormat.JPEG, 50, imageOut);
                        imageOut.close();
                        long id = ContentUris.parseId(url);
                        Bitmap miniThumb = Thumbnails.getThumbnail(cr, id, 1, null);
                        StoreThumbnail(cr, miniThumb, id, 50.0f, 50.0f, 3);
                    } else {
                        Log.e(MediaStore.TAG, "Failed to create thumbnail, removing original");
                        cr.delete(url, null, null);
                        url = null;
                    }
                } catch (Exception e) {
                    Log.e(MediaStore.TAG, "Failed to insert image", e);
                    if (0 != 0) {
                        cr.delete(null, null, null);
                        url = null;
                    }
                }
                if (url != null) {
                    stringUrl = url.toString();
                }
                return stringUrl;
            }

            public static Uri getContentUri(String volumeName) {
                return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + volumeName + "/images/media");
            }
        }

        /* loaded from: MediaStore$Images$Thumbnails.class */
        public static class Thumbnails implements BaseColumns {
            public static final Uri INTERNAL_CONTENT_URI = getContentUri("internal");
            public static final Uri EXTERNAL_CONTENT_URI = getContentUri("external");
            public static final String DEFAULT_SORT_ORDER = "image_id ASC";
            public static final String DATA = "_data";
            public static final String IMAGE_ID = "image_id";
            public static final String KIND = "kind";
            public static final int MINI_KIND = 1;
            public static final int FULL_SCREEN_KIND = 2;
            public static final int MICRO_KIND = 3;
            public static final String THUMB_DATA = "thumb_data";
            public static final String WIDTH = "width";
            public static final String HEIGHT = "height";

            public static final Cursor query(ContentResolver cr, Uri uri, String[] projection) {
                return cr.query(uri, projection, null, null, DEFAULT_SORT_ORDER);
            }

            public static final Cursor queryMiniThumbnails(ContentResolver cr, Uri uri, int kind, String[] projection) {
                return cr.query(uri, projection, "kind = " + kind, null, DEFAULT_SORT_ORDER);
            }

            public static final Cursor queryMiniThumbnail(ContentResolver cr, long origId, int kind, String[] projection) {
                return cr.query(EXTERNAL_CONTENT_URI, projection, "image_id = " + origId + " AND kind = " + kind, null, null);
            }

            public static void cancelThumbnailRequest(ContentResolver cr, long origId) {
                InternalThumbnails.cancelThumbnailRequest(cr, origId, EXTERNAL_CONTENT_URI, 0L);
            }

            public static Bitmap getThumbnail(ContentResolver cr, long origId, int kind, BitmapFactory.Options options) {
                return InternalThumbnails.getThumbnail(cr, origId, 0L, kind, options, EXTERNAL_CONTENT_URI, false);
            }

            public static void cancelThumbnailRequest(ContentResolver cr, long origId, long groupId) {
                InternalThumbnails.cancelThumbnailRequest(cr, origId, EXTERNAL_CONTENT_URI, groupId);
            }

            public static Bitmap getThumbnail(ContentResolver cr, long origId, long groupId, int kind, BitmapFactory.Options options) {
                return InternalThumbnails.getThumbnail(cr, origId, groupId, kind, options, EXTERNAL_CONTENT_URI, false);
            }

            public static Uri getContentUri(String volumeName) {
                return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + volumeName + "/images/thumbnails");
            }
        }
    }

    /* loaded from: MediaStore$Audio.class */
    public static final class Audio {

        /* loaded from: MediaStore$Audio$AlbumColumns.class */
        public interface AlbumColumns {
            public static final String ALBUM_ID = "album_id";
            public static final String ALBUM = "album";
            public static final String ARTIST = "artist";
            public static final String NUMBER_OF_SONGS = "numsongs";
            public static final String NUMBER_OF_SONGS_FOR_ARTIST = "numsongs_by_artist";
            public static final String FIRST_YEAR = "minyear";
            public static final String LAST_YEAR = "maxyear";
            public static final String ALBUM_KEY = "album_key";
            public static final String ALBUM_ART = "album_art";
        }

        /* loaded from: MediaStore$Audio$ArtistColumns.class */
        public interface ArtistColumns {
            public static final String ARTIST = "artist";
            public static final String ARTIST_KEY = "artist_key";
            public static final String NUMBER_OF_ALBUMS = "number_of_albums";
            public static final String NUMBER_OF_TRACKS = "number_of_tracks";
        }

        /* loaded from: MediaStore$Audio$AudioColumns.class */
        public interface AudioColumns extends MediaColumns {
            public static final String TITLE_KEY = "title_key";
            public static final String DURATION = "duration";
            public static final String BOOKMARK = "bookmark";
            public static final String ARTIST_ID = "artist_id";
            public static final String ARTIST = "artist";
            public static final String ALBUM_ARTIST = "album_artist";
            public static final String COMPILATION = "compilation";
            public static final String ARTIST_KEY = "artist_key";
            public static final String COMPOSER = "composer";
            public static final String ALBUM_ID = "album_id";
            public static final String ALBUM = "album";
            public static final String ALBUM_KEY = "album_key";
            public static final String TRACK = "track";
            public static final String YEAR = "year";
            public static final String IS_MUSIC = "is_music";
            public static final String IS_PODCAST = "is_podcast";
            public static final String IS_RINGTONE = "is_ringtone";
            public static final String IS_ALARM = "is_alarm";
            public static final String IS_NOTIFICATION = "is_notification";
            public static final String GENRE = "genre";
        }

        /* loaded from: MediaStore$Audio$GenresColumns.class */
        public interface GenresColumns {
            public static final String NAME = "name";
        }

        /* loaded from: MediaStore$Audio$PlaylistsColumns.class */
        public interface PlaylistsColumns {
            public static final String NAME = "name";
            public static final String DATA = "_data";
            public static final String DATE_ADDED = "date_added";
            public static final String DATE_MODIFIED = "date_modified";
        }

        public static String keyFor(String name) {
            if (name != null) {
                boolean sortfirst = false;
                if (name.equals(MediaStore.UNKNOWN_STRING)) {
                    return "\u0001";
                }
                if (name.startsWith("\u0001")) {
                    sortfirst = true;
                }
                String name2 = name.trim().toLowerCase();
                if (name2.startsWith("the ")) {
                    name2 = name2.substring(4);
                }
                if (name2.startsWith("an ")) {
                    name2 = name2.substring(3);
                }
                if (name2.startsWith("a ")) {
                    name2 = name2.substring(2);
                }
                if (name2.endsWith(", the") || name2.endsWith(",the") || name2.endsWith(", an") || name2.endsWith(",an") || name2.endsWith(", a") || name2.endsWith(",a")) {
                    name2 = name2.substring(0, name2.lastIndexOf(44));
                }
                String name3 = name2.replaceAll("[\\[\\]\\(\\)\"'.,?!]", "").trim();
                if (name3.length() > 0) {
                    StringBuilder b = new StringBuilder();
                    b.append('.');
                    int nl = name3.length();
                    for (int i = 0; i < nl; i++) {
                        b.append(name3.charAt(i));
                        b.append('.');
                    }
                    String key = DatabaseUtils.getCollationKey(b.toString());
                    if (sortfirst) {
                        key = "\u0001" + key;
                    }
                    return key;
                }
                return "";
            }
            return null;
        }

        /* loaded from: MediaStore$Audio$Media.class */
        public static final class Media implements AudioColumns {
            private static final String[] EXTERNAL_PATHS;
            public static final Uri INTERNAL_CONTENT_URI;
            public static final Uri EXTERNAL_CONTENT_URI;
            public static final String CONTENT_TYPE = "vnd.android.cursor.dir/audio";
            public static final String DEFAULT_SORT_ORDER = "title_key";
            public static final String RECORD_SOUND_ACTION = "android.provider.MediaStore.RECORD_SOUND";
            public static final String EXTRA_MAX_BYTES = "android.provider.MediaStore.extra.MAX_BYTES";

            static {
                String secondary_storage = System.getenv("SECONDARY_STORAGE");
                if (secondary_storage != null) {
                    EXTERNAL_PATHS = secondary_storage.split(Separators.COLON);
                } else {
                    EXTERNAL_PATHS = new String[0];
                }
                INTERNAL_CONTENT_URI = getContentUri("internal");
                EXTERNAL_CONTENT_URI = getContentUri("external");
            }

            public static Uri getContentUri(String volumeName) {
                return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + volumeName + "/audio/media");
            }

            public static Uri getContentUriForPath(String path) {
                String[] arr$ = EXTERNAL_PATHS;
                for (String ep : arr$) {
                    if (path.startsWith(ep)) {
                        return EXTERNAL_CONTENT_URI;
                    }
                }
                return path.startsWith(Environment.getExternalStorageDirectory().getPath()) ? EXTERNAL_CONTENT_URI : INTERNAL_CONTENT_URI;
            }
        }

        /* loaded from: MediaStore$Audio$Genres.class */
        public static final class Genres implements BaseColumns, GenresColumns {
            public static final Uri INTERNAL_CONTENT_URI = getContentUri("internal");
            public static final Uri EXTERNAL_CONTENT_URI = getContentUri("external");
            public static final String CONTENT_TYPE = "vnd.android.cursor.dir/genre";
            public static final String ENTRY_CONTENT_TYPE = "vnd.android.cursor.item/genre";
            public static final String DEFAULT_SORT_ORDER = "name";

            public static Uri getContentUri(String volumeName) {
                return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + volumeName + "/audio/genres");
            }

            public static Uri getContentUriForAudioId(String volumeName, int audioId) {
                return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + volumeName + "/audio/media/" + audioId + "/genres");
            }

            /* loaded from: MediaStore$Audio$Genres$Members.class */
            public static final class Members implements AudioColumns {
                public static final String CONTENT_DIRECTORY = "members";
                public static final String DEFAULT_SORT_ORDER = "title_key";
                public static final String AUDIO_ID = "audio_id";
                public static final String GENRE_ID = "genre_id";

                public static final Uri getContentUri(String volumeName, long genreId) {
                    return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + volumeName + "/audio/genres/" + genreId + "/members");
                }
            }
        }

        /* loaded from: MediaStore$Audio$Playlists.class */
        public static final class Playlists implements BaseColumns, PlaylistsColumns {
            public static final Uri INTERNAL_CONTENT_URI = getContentUri("internal");
            public static final Uri EXTERNAL_CONTENT_URI = getContentUri("external");
            public static final String CONTENT_TYPE = "vnd.android.cursor.dir/playlist";
            public static final String ENTRY_CONTENT_TYPE = "vnd.android.cursor.item/playlist";
            public static final String DEFAULT_SORT_ORDER = "name";

            public static Uri getContentUri(String volumeName) {
                return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + volumeName + "/audio/playlists");
            }

            /* loaded from: MediaStore$Audio$Playlists$Members.class */
            public static final class Members implements AudioColumns {
                public static final String _ID = "_id";
                public static final String CONTENT_DIRECTORY = "members";
                public static final String AUDIO_ID = "audio_id";
                public static final String PLAYLIST_ID = "playlist_id";
                public static final String PLAY_ORDER = "play_order";
                public static final String DEFAULT_SORT_ORDER = "play_order";

                public static final Uri getContentUri(String volumeName, long playlistId) {
                    return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + volumeName + "/audio/playlists/" + playlistId + "/members");
                }

                public static final boolean moveItem(ContentResolver res, long playlistId, int from, int to) {
                    Uri uri = getContentUri("external", playlistId).buildUpon().appendEncodedPath(String.valueOf(from)).appendQueryParameter("move", "true").build();
                    ContentValues values = new ContentValues();
                    values.put("play_order", Integer.valueOf(to));
                    return res.update(uri, values, null, null) != 0;
                }
            }
        }

        /* loaded from: MediaStore$Audio$Artists.class */
        public static final class Artists implements BaseColumns, ArtistColumns {
            public static final Uri INTERNAL_CONTENT_URI = getContentUri("internal");
            public static final Uri EXTERNAL_CONTENT_URI = getContentUri("external");
            public static final String CONTENT_TYPE = "vnd.android.cursor.dir/artists";
            public static final String ENTRY_CONTENT_TYPE = "vnd.android.cursor.item/artist";
            public static final String DEFAULT_SORT_ORDER = "artist_key";

            public static Uri getContentUri(String volumeName) {
                return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + volumeName + "/audio/artists");
            }

            /* loaded from: MediaStore$Audio$Artists$Albums.class */
            public static final class Albums implements AlbumColumns {
                public static final Uri getContentUri(String volumeName, long artistId) {
                    return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + volumeName + "/audio/artists/" + artistId + "/albums");
                }
            }
        }

        /* loaded from: MediaStore$Audio$Albums.class */
        public static final class Albums implements BaseColumns, AlbumColumns {
            public static final Uri INTERNAL_CONTENT_URI = getContentUri("internal");
            public static final Uri EXTERNAL_CONTENT_URI = getContentUri("external");
            public static final String CONTENT_TYPE = "vnd.android.cursor.dir/albums";
            public static final String ENTRY_CONTENT_TYPE = "vnd.android.cursor.item/album";
            public static final String DEFAULT_SORT_ORDER = "album_key";

            public static Uri getContentUri(String volumeName) {
                return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + volumeName + "/audio/albums");
            }
        }
    }

    /* loaded from: MediaStore$Video.class */
    public static final class Video {
        public static final String DEFAULT_SORT_ORDER = "_display_name";

        /* loaded from: MediaStore$Video$VideoColumns.class */
        public interface VideoColumns extends MediaColumns {
            public static final String DURATION = "duration";
            public static final String ARTIST = "artist";
            public static final String ALBUM = "album";
            public static final String RESOLUTION = "resolution";
            public static final String DESCRIPTION = "description";
            public static final String IS_PRIVATE = "isprivate";
            public static final String TAGS = "tags";
            public static final String CATEGORY = "category";
            public static final String LANGUAGE = "language";
            public static final String LATITUDE = "latitude";
            public static final String LONGITUDE = "longitude";
            public static final String DATE_TAKEN = "datetaken";
            public static final String MINI_THUMB_MAGIC = "mini_thumb_magic";
            public static final String BUCKET_ID = "bucket_id";
            public static final String BUCKET_DISPLAY_NAME = "bucket_display_name";
            public static final String BOOKMARK = "bookmark";
        }

        public static final Cursor query(ContentResolver cr, Uri uri, String[] projection) {
            return cr.query(uri, projection, null, null, "_display_name");
        }

        /* loaded from: MediaStore$Video$Media.class */
        public static final class Media implements VideoColumns {
            public static final Uri INTERNAL_CONTENT_URI = getContentUri("internal");
            public static final Uri EXTERNAL_CONTENT_URI = getContentUri("external");
            public static final String CONTENT_TYPE = "vnd.android.cursor.dir/video";
            public static final String DEFAULT_SORT_ORDER = "title";

            public static Uri getContentUri(String volumeName) {
                return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + volumeName + "/video/media");
            }
        }

        /* loaded from: MediaStore$Video$Thumbnails.class */
        public static class Thumbnails implements BaseColumns {
            public static final Uri INTERNAL_CONTENT_URI = getContentUri("internal");
            public static final Uri EXTERNAL_CONTENT_URI = getContentUri("external");
            public static final String DEFAULT_SORT_ORDER = "video_id ASC";
            public static final String DATA = "_data";
            public static final String VIDEO_ID = "video_id";
            public static final String KIND = "kind";
            public static final int MINI_KIND = 1;
            public static final int FULL_SCREEN_KIND = 2;
            public static final int MICRO_KIND = 3;
            public static final String WIDTH = "width";
            public static final String HEIGHT = "height";

            public static void cancelThumbnailRequest(ContentResolver cr, long origId) {
                InternalThumbnails.cancelThumbnailRequest(cr, origId, EXTERNAL_CONTENT_URI, 0L);
            }

            public static Bitmap getThumbnail(ContentResolver cr, long origId, int kind, BitmapFactory.Options options) {
                return InternalThumbnails.getThumbnail(cr, origId, 0L, kind, options, EXTERNAL_CONTENT_URI, true);
            }

            public static Bitmap getThumbnail(ContentResolver cr, long origId, long groupId, int kind, BitmapFactory.Options options) {
                return InternalThumbnails.getThumbnail(cr, origId, groupId, kind, options, EXTERNAL_CONTENT_URI, true);
            }

            public static void cancelThumbnailRequest(ContentResolver cr, long origId, long groupId) {
                InternalThumbnails.cancelThumbnailRequest(cr, origId, EXTERNAL_CONTENT_URI, groupId);
            }

            public static Uri getContentUri(String volumeName) {
                return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + volumeName + "/video/thumbnails");
            }
        }
    }

    public static Uri getMediaScannerUri() {
        return Uri.parse("content://media/none/media_scanner");
    }

    public static String getVersion(Context context) {
        Cursor c = context.getContentResolver().query(Uri.parse("content://media/none/version"), null, null, null, null);
        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    String string = c.getString(0);
                    c.close();
                    return string;
                }
                c.close();
                return null;
            } catch (Throwable th) {
                c.close();
                throw th;
            }
        }
        return null;
    }
}