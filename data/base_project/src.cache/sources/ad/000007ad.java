package android.media;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.IContentProvider;
import android.database.Cursor;
import android.database.SQLException;
import android.drm.DrmManagerClient;
import android.graphics.BitmapFactory;
import android.media.MediaFile;
import android.mtp.MtpConstants;
import android.net.Uri;
import android.os.Environment;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.Telephony;
import android.sax.Element;
import android.sax.ElementListener;
import android.sax.RootElement;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import gov.nist.core.Separators;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import libcore.io.ErrnoException;
import libcore.io.Libcore;
import libcore.io.OsConstants;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/* loaded from: MediaScanner.class */
public class MediaScanner {
    private static final String TAG = "MediaScanner";
    private static final String[] FILES_PRESCAN_PROJECTION;
    private static final String[] ID_PROJECTION;
    private static final int FILES_PRESCAN_ID_COLUMN_INDEX = 0;
    private static final int FILES_PRESCAN_PATH_COLUMN_INDEX = 1;
    private static final int FILES_PRESCAN_FORMAT_COLUMN_INDEX = 2;
    private static final int FILES_PRESCAN_DATE_MODIFIED_COLUMN_INDEX = 3;
    private static final String[] PLAYLIST_MEMBERS_PROJECTION;
    private static final int ID_PLAYLISTS_COLUMN_INDEX = 0;
    private static final int PATH_PLAYLISTS_COLUMN_INDEX = 1;
    private static final int DATE_MODIFIED_PLAYLISTS_COLUMN_INDEX = 2;
    private static final String RINGTONES_DIR = "/ringtones/";
    private static final String NOTIFICATIONS_DIR = "/notifications/";
    private static final String ALARMS_DIR = "/alarms/";
    private static final String MUSIC_DIR = "/music/";
    private static final String PODCAST_DIR = "/podcasts/";
    private static final String[] ID3_GENRES;
    private int mNativeContext;
    private Context mContext;
    private String mPackageName;
    private IContentProvider mMediaProvider;
    private Uri mAudioUri;
    private Uri mVideoUri;
    private Uri mImagesUri;
    private Uri mThumbsUri;
    private Uri mPlaylistsUri;
    private Uri mFilesUri;
    private Uri mFilesUriNoNotify;
    private boolean mProcessPlaylists;
    private boolean mProcessGenres;
    private int mMtpObjectHandle;
    private final String mExternalStoragePath;
    private final boolean mExternalIsEmulated;
    private static final boolean ENABLE_BULK_INSERTS = true;
    private int mOriginalCount;
    private boolean mDefaultRingtoneSet;
    private boolean mDefaultNotificationSet;
    private boolean mDefaultAlarmSet;
    private String mDefaultRingtoneFilename;
    private String mDefaultNotificationFilename;
    private String mDefaultAlarmAlertFilename;
    private static final String DEFAULT_RINGTONE_PROPERTY_PREFIX = "ro.config.";
    private boolean mCaseInsensitivePaths;
    private MediaInserter mMediaInserter;
    private ArrayList<FileEntry> mPlayLists;
    private boolean mWasEmptyPriorToScan = false;
    private final BitmapFactory.Options mBitmapOptions = new BitmapFactory.Options();
    private ArrayList<PlaylistEntry> mPlaylistEntries = new ArrayList<>();
    private DrmManagerClient mDrmManagerClient = null;
    private final MyMediaScannerClient mClient = new MyMediaScannerClient();

    private native void processDirectory(String str, MediaScannerClient mediaScannerClient);

    /* JADX INFO: Access modifiers changed from: private */
    public native void processFile(String str, String str2, MediaScannerClient mediaScannerClient);

    public native void setLocale(String str);

    public native byte[] extractAlbumArt(FileDescriptor fileDescriptor);

    private static final native void native_init();

    private final native void native_setup();

    private final native void native_finalize();

    static {
        System.loadLibrary("media_jni");
        native_init();
        FILES_PRESCAN_PROJECTION = new String[]{"_id", "_data", "format", "date_modified"};
        ID_PROJECTION = new String[]{"_id"};
        PLAYLIST_MEMBERS_PROJECTION = new String[]{MediaStore.Audio.Playlists.Members.PLAYLIST_ID};
        ID3_GENRES = new String[]{"Blues", "Classic Rock", "Country", "Dance", "Disco", "Funk", "Grunge", "Hip-Hop", "Jazz", "Metal", "New Age", "Oldies", "Other", "Pop", "R&B", "Rap", "Reggae", "Rock", "Techno", "Industrial", "Alternative", "Ska", "Death Metal", "Pranks", "Soundtrack", "Euro-Techno", "Ambient", "Trip-Hop", "Vocal", "Jazz+Funk", "Fusion", "Trance", "Classical", "Instrumental", "Acid", "House", "Game", "Sound Clip", "Gospel", "Noise", "AlternRock", "Bass", "Soul", "Punk", "Space", "Meditative", "Instrumental Pop", "Instrumental Rock", "Ethnic", "Gothic", "Darkwave", "Techno-Industrial", "Electronic", "Pop-Folk", "Eurodance", "Dream", "Southern Rock", "Comedy", "Cult", "Gangsta", "Top 40", "Christian Rap", "Pop/Funk", "Jungle", "Native American", "Cabaret", "New Wave", "Psychadelic", "Rave", "Showtunes", "Trailer", "Lo-Fi", "Tribal", "Acid Punk", "Acid Jazz", "Polka", "Retro", "Musical", "Rock & Roll", "Hard Rock", "Folk", "Folk-Rock", "National Folk", "Swing", "Fast Fusion", "Bebob", "Latin", "Revival", "Celtic", "Bluegrass", "Avantgarde", "Gothic Rock", "Progressive Rock", "Psychedelic Rock", "Symphonic Rock", "Slow Rock", "Big Band", "Chorus", "Easy Listening", "Acoustic", "Humour", "Speech", "Chanson", "Opera", "Chamber Music", "Sonata", "Symphony", "Booty Bass", "Primus", "Porn Groove", "Satire", "Slow Jam", "Club", "Tango", "Samba", "Folklore", "Ballad", "Power Ballad", "Rhythmic Soul", "Freestyle", "Duet", "Punk Rock", "Drum Solo", "A capella", "Euro-House", "Dance Hall", "Goa", "Drum & Bass", "Club-House", "Hardcore", "Terror", "Indie", "Britpop", null, "Polsk Punk", "Beat", "Christian Gangsta", "Heavy Metal", "Black Metal", "Crossover", "Contemporary Christian", "Christian Rock", "Merengue", "Salsa", "Thrash Metal", "Anime", "JPop", "Synthpop"};
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: MediaScanner$FileEntry.class */
    public static class FileEntry {
        long mRowId;
        String mPath;
        long mLastModified;
        int mFormat;
        boolean mLastModifiedChanged = false;

        FileEntry(long rowId, String path, long lastModified, int format) {
            this.mRowId = rowId;
            this.mPath = path;
            this.mLastModified = lastModified;
            this.mFormat = format;
        }

        public String toString() {
            return this.mPath + " mRowId: " + this.mRowId;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: MediaScanner$PlaylistEntry.class */
    public static class PlaylistEntry {
        String path;
        long bestmatchid;
        int bestmatchlevel;

        private PlaylistEntry() {
        }
    }

    public MediaScanner(Context c) {
        native_setup();
        this.mContext = c;
        this.mPackageName = c.getPackageName();
        this.mBitmapOptions.inSampleSize = 1;
        this.mBitmapOptions.inJustDecodeBounds = true;
        setDefaultRingtoneFileNames();
        this.mExternalStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        this.mExternalIsEmulated = Environment.isExternalStorageEmulated();
    }

    private void setDefaultRingtoneFileNames() {
        this.mDefaultRingtoneFilename = SystemProperties.get("ro.config.ringtone");
        this.mDefaultNotificationFilename = SystemProperties.get("ro.config.notification_sound");
        this.mDefaultAlarmAlertFilename = SystemProperties.get("ro.config.alarm_alert");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isDrmEnabled() {
        String prop = SystemProperties.get("drm.service.enabled");
        return prop != null && prop.equals("true");
    }

    /* loaded from: MediaScanner$MyMediaScannerClient.class */
    private class MyMediaScannerClient implements MediaScannerClient {
        private String mArtist;
        private String mAlbumArtist;
        private String mAlbum;
        private String mTitle;
        private String mComposer;
        private String mGenre;
        private String mMimeType;
        private int mFileType;
        private int mTrack;
        private int mYear;
        private int mDuration;
        private String mPath;
        private long mLastModified;
        private long mFileSize;
        private String mWriter;
        private int mCompilation;
        private boolean mIsDrm;
        private boolean mNoMedia;
        private int mWidth;
        private int mHeight;

        private MyMediaScannerClient() {
        }

        public FileEntry beginFile(String path, String mimeType, long lastModified, long fileSize, boolean isDirectory, boolean noMedia) {
            MediaFile.MediaFileType mediaFileType;
            this.mMimeType = mimeType;
            this.mFileType = 0;
            this.mFileSize = fileSize;
            this.mIsDrm = false;
            if (!isDirectory) {
                if (!noMedia && MediaScanner.isNoMediaFile(path)) {
                    noMedia = true;
                }
                this.mNoMedia = noMedia;
                if (mimeType != null) {
                    this.mFileType = MediaFile.getFileTypeForMimeType(mimeType);
                }
                if (this.mFileType == 0 && (mediaFileType = MediaFile.getFileType(path)) != null) {
                    this.mFileType = mediaFileType.fileType;
                    if (this.mMimeType == null) {
                        this.mMimeType = mediaFileType.mimeType;
                    }
                }
                if (MediaScanner.this.isDrmEnabled() && MediaFile.isDrmFileType(this.mFileType)) {
                    this.mFileType = getFileTypeFromDrm(path);
                }
            }
            FileEntry entry = MediaScanner.this.makeEntryFor(path);
            long delta = entry != null ? lastModified - entry.mLastModified : 0L;
            boolean wasModified = delta > 1 || delta < -1;
            if (entry == null || wasModified) {
                if (wasModified) {
                    entry.mLastModified = lastModified;
                } else {
                    entry = new FileEntry(0L, path, lastModified, isDirectory ? 12289 : 0);
                }
                entry.mLastModifiedChanged = true;
            }
            if (MediaScanner.this.mProcessPlaylists && MediaFile.isPlayListFileType(this.mFileType)) {
                MediaScanner.this.mPlayLists.add(entry);
                return null;
            }
            this.mArtist = null;
            this.mAlbumArtist = null;
            this.mAlbum = null;
            this.mTitle = null;
            this.mComposer = null;
            this.mGenre = null;
            this.mTrack = 0;
            this.mYear = 0;
            this.mDuration = 0;
            this.mPath = path;
            this.mLastModified = lastModified;
            this.mWriter = null;
            this.mCompilation = 0;
            this.mWidth = 0;
            this.mHeight = 0;
            return entry;
        }

        @Override // android.media.MediaScannerClient
        public void scanFile(String path, long lastModified, long fileSize, boolean isDirectory, boolean noMedia) {
            doScanFile(path, null, lastModified, fileSize, isDirectory, false, noMedia);
        }

        public Uri doScanFile(String path, String mimeType, long lastModified, long fileSize, boolean isDirectory, boolean scanAlways, boolean noMedia) {
            Uri result = null;
            try {
                FileEntry entry = beginFile(path, mimeType, lastModified, fileSize, isDirectory, noMedia);
                if (MediaScanner.this.mMtpObjectHandle != 0) {
                    entry.mRowId = 0L;
                }
                if (entry != null && (entry.mLastModifiedChanged || scanAlways)) {
                    if (noMedia) {
                        result = endFile(entry, false, false, false, false, false);
                    } else {
                        String lowpath = path.toLowerCase(Locale.ROOT);
                        boolean ringtones = lowpath.indexOf(MediaScanner.RINGTONES_DIR) > 0;
                        boolean notifications = lowpath.indexOf(MediaScanner.NOTIFICATIONS_DIR) > 0;
                        boolean alarms = lowpath.indexOf(MediaScanner.ALARMS_DIR) > 0;
                        boolean podcasts = lowpath.indexOf(MediaScanner.PODCAST_DIR) > 0;
                        boolean music = lowpath.indexOf(MediaScanner.MUSIC_DIR) > 0 || !(ringtones || notifications || alarms || podcasts);
                        boolean isaudio = MediaFile.isAudioFileType(this.mFileType);
                        boolean isvideo = MediaFile.isVideoFileType(this.mFileType);
                        boolean isimage = MediaFile.isImageFileType(this.mFileType);
                        if ((isaudio || isvideo || isimage) && MediaScanner.this.mExternalIsEmulated && path.startsWith(MediaScanner.this.mExternalStoragePath)) {
                            String directPath = Environment.getMediaStorageDirectory() + path.substring(MediaScanner.this.mExternalStoragePath.length());
                            File f = new File(directPath);
                            if (f.exists()) {
                                path = directPath;
                            }
                        }
                        if (isaudio || isvideo) {
                            MediaScanner.this.processFile(path, mimeType, this);
                        }
                        if (isimage) {
                            processImageFile(path);
                        }
                        result = endFile(entry, ringtones, notifications, alarms, music, podcasts);
                    }
                }
            } catch (RemoteException e) {
                Log.e(MediaScanner.TAG, "RemoteException in MediaScanner.scanFile()", e);
            }
            return result;
        }

        private int parseSubstring(String s, int start, int defaultValue) {
            int result;
            int length = s.length();
            if (start == length) {
                return defaultValue;
            }
            int start2 = start + 1;
            char ch = s.charAt(start);
            if (ch < '0' || ch > '9') {
                return defaultValue;
            }
            int i = ch - '0';
            while (true) {
                result = i;
                if (start2 < length) {
                    int i2 = start2;
                    start2++;
                    char ch2 = s.charAt(i2);
                    if (ch2 < '0' || ch2 > '9') {
                        break;
                    }
                    i = (result * 10) + (ch2 - '0');
                } else {
                    return result;
                }
            }
            return result;
        }

        @Override // android.media.MediaScannerClient
        public void handleStringTag(String name, String value) {
            if (name.equalsIgnoreCase("title") || name.startsWith("title;")) {
                this.mTitle = value;
            } else if (name.equalsIgnoreCase("artist") || name.startsWith("artist;")) {
                this.mArtist = value.trim();
            } else if (name.equalsIgnoreCase("albumartist") || name.startsWith("albumartist;") || name.equalsIgnoreCase("band") || name.startsWith("band;")) {
                this.mAlbumArtist = value.trim();
            } else if (name.equalsIgnoreCase("album") || name.startsWith("album;")) {
                this.mAlbum = value.trim();
            } else if (!name.equalsIgnoreCase(MediaStore.Audio.AudioColumns.COMPOSER) && !name.startsWith("composer;")) {
                if (MediaScanner.this.mProcessGenres && (name.equalsIgnoreCase(MediaStore.Audio.AudioColumns.GENRE) || name.startsWith("genre;"))) {
                    this.mGenre = getGenreName(value);
                } else if (name.equalsIgnoreCase(MediaStore.Audio.AudioColumns.YEAR) || name.startsWith("year;")) {
                    this.mYear = parseSubstring(value, 0, 0);
                } else if (name.equalsIgnoreCase("tracknumber") || name.startsWith("tracknumber;")) {
                    int num = parseSubstring(value, 0, 0);
                    this.mTrack = ((this.mTrack / 1000) * 1000) + num;
                } else if (name.equalsIgnoreCase("discnumber") || name.equals("set") || name.startsWith("set;")) {
                    int num2 = parseSubstring(value, 0, 0);
                    this.mTrack = (num2 * 1000) + (this.mTrack % 1000);
                } else if (name.equalsIgnoreCase("duration")) {
                    this.mDuration = parseSubstring(value, 0, 0);
                } else if (name.equalsIgnoreCase("writer") || name.startsWith("writer;")) {
                    this.mWriter = value.trim();
                } else if (name.equalsIgnoreCase(MediaStore.Audio.AudioColumns.COMPILATION)) {
                    this.mCompilation = parseSubstring(value, 0, 0);
                } else if (name.equalsIgnoreCase("isdrm")) {
                    this.mIsDrm = parseSubstring(value, 0, 0) == 1;
                } else if (name.equalsIgnoreCase("width")) {
                    this.mWidth = parseSubstring(value, 0, 0);
                } else if (name.equalsIgnoreCase("height")) {
                    this.mHeight = parseSubstring(value, 0, 0);
                }
            } else {
                this.mComposer = value.trim();
            }
        }

        private boolean convertGenreCode(String input, String expected) {
            String output = getGenreName(input);
            if (output.equals(expected)) {
                return true;
            }
            Log.d(MediaScanner.TAG, Separators.QUOTE + input + "' -> '" + output + "', expected '" + expected + Separators.QUOTE);
            return false;
        }

        private void testGenreNameConverter() {
            convertGenreCode("2", "Country");
            convertGenreCode("(2)", "Country");
            convertGenreCode("(2", "(2");
            convertGenreCode("2 Foo", "Country");
            convertGenreCode("(2) Foo", "Country");
            convertGenreCode("(2 Foo", "(2 Foo");
            convertGenreCode("2Foo", "2Foo");
            convertGenreCode("(2)Foo", "Country");
            convertGenreCode("200 Foo", "Foo");
            convertGenreCode("(200) Foo", "Foo");
            convertGenreCode("200Foo", "200Foo");
            convertGenreCode("(200)Foo", "Foo");
            convertGenreCode("200)Foo", "200)Foo");
            convertGenreCode("200) Foo", "200) Foo");
        }

        public String getGenreName(String genreTagValue) {
            if (genreTagValue == null) {
                return null;
            }
            int length = genreTagValue.length();
            if (length > 0) {
                boolean parenthesized = false;
                StringBuffer number = new StringBuffer();
                int i = 0;
                while (i < length) {
                    char c = genreTagValue.charAt(i);
                    if (i == 0 && c == '(') {
                        parenthesized = true;
                    } else if (!Character.isDigit(c)) {
                        break;
                    } else {
                        number.append(c);
                    }
                    i++;
                }
                char charAfterNumber = i < length ? genreTagValue.charAt(i) : ' ';
                if ((parenthesized && charAfterNumber == ')') || (!parenthesized && Character.isWhitespace(charAfterNumber))) {
                    try {
                        short genreIndex = Short.parseShort(number.toString());
                        if (genreIndex >= 0) {
                            if (genreIndex < MediaScanner.ID3_GENRES.length && MediaScanner.ID3_GENRES[genreIndex] != null) {
                                return MediaScanner.ID3_GENRES[genreIndex];
                            }
                            if (genreIndex == 255) {
                                return null;
                            }
                            if (genreIndex < 255 && i + 1 < length) {
                                if (parenthesized && charAfterNumber == ')') {
                                    i++;
                                }
                                String ret = genreTagValue.substring(i).trim();
                                if (ret.length() != 0) {
                                    return ret;
                                }
                            } else {
                                return number.toString();
                            }
                        }
                    } catch (NumberFormatException e) {
                    }
                }
            }
            return genreTagValue;
        }

        private void processImageFile(String path) {
            try {
                MediaScanner.this.mBitmapOptions.outWidth = 0;
                MediaScanner.this.mBitmapOptions.outHeight = 0;
                BitmapFactory.decodeFile(path, MediaScanner.this.mBitmapOptions);
                this.mWidth = MediaScanner.this.mBitmapOptions.outWidth;
                this.mHeight = MediaScanner.this.mBitmapOptions.outHeight;
            } catch (Throwable th) {
            }
        }

        @Override // android.media.MediaScannerClient
        public void setMimeType(String mimeType) {
            if ("audio/mp4".equals(this.mMimeType) && mimeType.startsWith("video")) {
                return;
            }
            this.mMimeType = mimeType;
            this.mFileType = MediaFile.getFileTypeForMimeType(mimeType);
        }

        private ContentValues toValues() {
            ContentValues map = new ContentValues();
            map.put("_data", this.mPath);
            map.put("title", this.mTitle);
            map.put("date_modified", Long.valueOf(this.mLastModified));
            map.put("_size", Long.valueOf(this.mFileSize));
            map.put("mime_type", this.mMimeType);
            map.put(MediaStore.MediaColumns.IS_DRM, Boolean.valueOf(this.mIsDrm));
            String resolution = null;
            if (this.mWidth > 0 && this.mHeight > 0) {
                map.put("width", Integer.valueOf(this.mWidth));
                map.put("height", Integer.valueOf(this.mHeight));
                resolution = this.mWidth + "x" + this.mHeight;
            }
            if (!this.mNoMedia) {
                if (MediaFile.isVideoFileType(this.mFileType)) {
                    map.put("artist", (this.mArtist == null || this.mArtist.length() <= 0) ? MediaStore.UNKNOWN_STRING : this.mArtist);
                    map.put("album", (this.mAlbum == null || this.mAlbum.length() <= 0) ? MediaStore.UNKNOWN_STRING : this.mAlbum);
                    map.put("duration", Integer.valueOf(this.mDuration));
                    if (resolution != null) {
                        map.put(MediaStore.Video.VideoColumns.RESOLUTION, resolution);
                    }
                } else if (!MediaFile.isImageFileType(this.mFileType) && MediaFile.isAudioFileType(this.mFileType)) {
                    map.put("artist", (this.mArtist == null || this.mArtist.length() <= 0) ? MediaStore.UNKNOWN_STRING : this.mArtist);
                    map.put(MediaStore.Audio.AudioColumns.ALBUM_ARTIST, (this.mAlbumArtist == null || this.mAlbumArtist.length() <= 0) ? null : this.mAlbumArtist);
                    map.put("album", (this.mAlbum == null || this.mAlbum.length() <= 0) ? MediaStore.UNKNOWN_STRING : this.mAlbum);
                    map.put(MediaStore.Audio.AudioColumns.COMPOSER, this.mComposer);
                    map.put(MediaStore.Audio.AudioColumns.GENRE, this.mGenre);
                    if (this.mYear != 0) {
                        map.put(MediaStore.Audio.AudioColumns.YEAR, Integer.valueOf(this.mYear));
                    }
                    map.put(MediaStore.Audio.AudioColumns.TRACK, Integer.valueOf(this.mTrack));
                    map.put("duration", Integer.valueOf(this.mDuration));
                    map.put(MediaStore.Audio.AudioColumns.COMPILATION, Integer.valueOf(this.mCompilation));
                }
            }
            return map;
        }

        private Uri endFile(FileEntry entry, boolean ringtones, boolean notifications, boolean alarms, boolean music, boolean podcasts) throws RemoteException {
            int degree;
            String album;
            int lastSlash;
            int previousSlash;
            if (this.mArtist == null || this.mArtist.length() == 0) {
                this.mArtist = this.mAlbumArtist;
            }
            ContentValues values = toValues();
            String title = values.getAsString("title");
            if (title == null || TextUtils.isEmpty(title.trim())) {
                values.put("title", MediaFile.getFileTitle(values.getAsString("_data")));
            }
            if (MediaStore.UNKNOWN_STRING.equals(values.getAsString("album")) && (lastSlash = (album = values.getAsString("_data")).lastIndexOf(47)) >= 0) {
                int i = 0;
                while (true) {
                    previousSlash = i;
                    int idx = album.indexOf(47, previousSlash + 1);
                    if (idx < 0 || idx >= lastSlash) {
                        break;
                    }
                    i = idx;
                }
                if (previousSlash != 0) {
                    values.put("album", album.substring(previousSlash + 1, lastSlash));
                }
            }
            long rowId = entry.mRowId;
            if (MediaFile.isAudioFileType(this.mFileType) && (rowId == 0 || MediaScanner.this.mMtpObjectHandle != 0)) {
                values.put(MediaStore.Audio.AudioColumns.IS_RINGTONE, Boolean.valueOf(ringtones));
                values.put(MediaStore.Audio.AudioColumns.IS_NOTIFICATION, Boolean.valueOf(notifications));
                values.put(MediaStore.Audio.AudioColumns.IS_ALARM, Boolean.valueOf(alarms));
                values.put(MediaStore.Audio.AudioColumns.IS_MUSIC, Boolean.valueOf(music));
                values.put(MediaStore.Audio.AudioColumns.IS_PODCAST, Boolean.valueOf(podcasts));
            } else if (this.mFileType == 31 && !this.mNoMedia) {
                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(entry.mPath);
                } catch (IOException e) {
                }
                if (exif != null) {
                    float[] latlng = new float[2];
                    if (exif.getLatLong(latlng)) {
                        values.put("latitude", Float.valueOf(latlng[0]));
                        values.put("longitude", Float.valueOf(latlng[1]));
                    }
                    long time = exif.getGpsDateTime();
                    if (time != -1) {
                        values.put("datetaken", Long.valueOf(time));
                    } else {
                        long time2 = exif.getDateTime();
                        if (time2 != -1 && Math.abs((this.mLastModified * 1000) - time2) >= 86400000) {
                            values.put("datetaken", Long.valueOf(time2));
                        }
                    }
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
                    if (orientation != -1) {
                        switch (orientation) {
                            case 3:
                                degree = 180;
                                break;
                            case 6:
                                degree = 90;
                                break;
                            case 8:
                                degree = 270;
                                break;
                            default:
                                degree = 0;
                                break;
                        }
                        values.put(MediaStore.Images.ImageColumns.ORIENTATION, Integer.valueOf(degree));
                    }
                }
            }
            Uri tableUri = MediaScanner.this.mFilesUri;
            MediaInserter inserter = MediaScanner.this.mMediaInserter;
            if (!this.mNoMedia) {
                if (MediaFile.isVideoFileType(this.mFileType)) {
                    tableUri = MediaScanner.this.mVideoUri;
                } else if (MediaFile.isImageFileType(this.mFileType)) {
                    tableUri = MediaScanner.this.mImagesUri;
                } else if (MediaFile.isAudioFileType(this.mFileType)) {
                    tableUri = MediaScanner.this.mAudioUri;
                }
            }
            Uri result = null;
            boolean needToSetSettings = false;
            if (rowId == 0) {
                if (MediaScanner.this.mMtpObjectHandle != 0) {
                    values.put(MediaStore.MediaColumns.MEDIA_SCANNER_NEW_OBJECT_ID, Integer.valueOf(MediaScanner.this.mMtpObjectHandle));
                }
                if (tableUri == MediaScanner.this.mFilesUri) {
                    int format = entry.mFormat;
                    if (format == 0) {
                        format = MediaFile.getFormatCode(entry.mPath, this.mMimeType);
                    }
                    values.put("format", Integer.valueOf(format));
                }
                if (MediaScanner.this.mWasEmptyPriorToScan) {
                    if (notifications && !MediaScanner.this.mDefaultNotificationSet) {
                        if (TextUtils.isEmpty(MediaScanner.this.mDefaultNotificationFilename) || doesPathHaveFilename(entry.mPath, MediaScanner.this.mDefaultNotificationFilename)) {
                            needToSetSettings = true;
                        }
                    } else if (ringtones && !MediaScanner.this.mDefaultRingtoneSet) {
                        if (TextUtils.isEmpty(MediaScanner.this.mDefaultRingtoneFilename) || doesPathHaveFilename(entry.mPath, MediaScanner.this.mDefaultRingtoneFilename)) {
                            needToSetSettings = true;
                        }
                    } else if (alarms && !MediaScanner.this.mDefaultAlarmSet && (TextUtils.isEmpty(MediaScanner.this.mDefaultAlarmAlertFilename) || doesPathHaveFilename(entry.mPath, MediaScanner.this.mDefaultAlarmAlertFilename))) {
                        needToSetSettings = true;
                    }
                }
                if (inserter == null || needToSetSettings) {
                    if (inserter != null) {
                        inserter.flushAll();
                    }
                    result = MediaScanner.this.mMediaProvider.insert(MediaScanner.this.mPackageName, tableUri, values);
                } else if (entry.mFormat == 12289) {
                    inserter.insertwithPriority(tableUri, values);
                } else {
                    inserter.insert(tableUri, values);
                }
                if (result != null) {
                    rowId = ContentUris.parseId(result);
                    entry.mRowId = rowId;
                }
            } else {
                result = ContentUris.withAppendedId(tableUri, rowId);
                values.remove("_data");
                int mediaType = 0;
                if (!MediaScanner.isNoMediaPath(entry.mPath)) {
                    int fileType = MediaFile.getFileTypeForMimeType(this.mMimeType);
                    if (MediaFile.isAudioFileType(fileType)) {
                        mediaType = 2;
                    } else if (MediaFile.isVideoFileType(fileType)) {
                        mediaType = 3;
                    } else if (MediaFile.isImageFileType(fileType)) {
                        mediaType = 1;
                    } else if (MediaFile.isPlayListFileType(fileType)) {
                        mediaType = 4;
                    }
                    values.put("media_type", Integer.valueOf(mediaType));
                }
                MediaScanner.this.mMediaProvider.update(MediaScanner.this.mPackageName, result, values, null, null);
            }
            if (needToSetSettings) {
                if (notifications) {
                    setSettingIfNotSet(Settings.System.NOTIFICATION_SOUND, tableUri, rowId);
                    MediaScanner.this.mDefaultNotificationSet = true;
                } else if (ringtones) {
                    setSettingIfNotSet(Settings.System.RINGTONE, tableUri, rowId);
                    MediaScanner.this.mDefaultRingtoneSet = true;
                } else if (alarms) {
                    setSettingIfNotSet(Settings.System.ALARM_ALERT, tableUri, rowId);
                    MediaScanner.this.mDefaultAlarmSet = true;
                }
            }
            return result;
        }

        private boolean doesPathHaveFilename(String path, String filename) {
            int pathFilenameStart = path.lastIndexOf(File.separatorChar) + 1;
            int filenameLength = filename.length();
            return path.regionMatches(pathFilenameStart, filename, 0, filenameLength) && pathFilenameStart + filenameLength == path.length();
        }

        private void setSettingIfNotSet(String settingName, Uri uri, long rowId) {
            String existingSettingValue = Settings.System.getString(MediaScanner.this.mContext.getContentResolver(), settingName);
            if (TextUtils.isEmpty(existingSettingValue)) {
                Settings.System.putString(MediaScanner.this.mContext.getContentResolver(), settingName, ContentUris.withAppendedId(uri, rowId).toString());
            }
        }

        private int getFileTypeFromDrm(String path) {
            if (!MediaScanner.this.isDrmEnabled()) {
                return 0;
            }
            int resultFileType = 0;
            if (MediaScanner.this.mDrmManagerClient == null) {
                MediaScanner.this.mDrmManagerClient = new DrmManagerClient(MediaScanner.this.mContext);
            }
            if (MediaScanner.this.mDrmManagerClient.canHandle(path, (String) null)) {
                this.mIsDrm = true;
                String drmMimetype = MediaScanner.this.mDrmManagerClient.getOriginalMimeType(path);
                if (drmMimetype != null) {
                    this.mMimeType = drmMimetype;
                    resultFileType = MediaFile.getFileTypeForMimeType(drmMimetype);
                }
            }
            return resultFileType;
        }
    }

    private void prescan(String filePath, boolean prescanFiles) throws RemoteException {
        String where;
        String[] selectionArgs;
        Cursor c = null;
        if (this.mPlayLists == null) {
            this.mPlayLists = new ArrayList<>();
        } else {
            this.mPlayLists.clear();
        }
        if (filePath != null) {
            where = "_id>? AND _data=?";
            selectionArgs = new String[]{"", filePath};
        } else {
            where = "_id>?";
            selectionArgs = new String[]{""};
        }
        Uri.Builder builder = this.mFilesUri.buildUpon();
        builder.appendQueryParameter(MediaStore.PARAM_DELETE_DATA, "false");
        MediaBulkDeleter deleter = new MediaBulkDeleter(this.mMediaProvider, this.mPackageName, builder.build());
        if (prescanFiles) {
            try {
                long lastId = Long.MIN_VALUE;
                Uri limitUri = this.mFilesUri.buildUpon().appendQueryParameter("limit", "1000").build();
                this.mWasEmptyPriorToScan = true;
                while (true) {
                    selectionArgs[0] = "" + lastId;
                    if (c != null) {
                        c.close();
                    }
                    c = this.mMediaProvider.query(this.mPackageName, limitUri, FILES_PRESCAN_PROJECTION, where, selectionArgs, "_id", null);
                    if (c == null) {
                        break;
                    }
                    int num = c.getCount();
                    if (num == 0) {
                        break;
                    }
                    this.mWasEmptyPriorToScan = false;
                    while (c.moveToNext()) {
                        long rowId = c.getLong(0);
                        String path = c.getString(1);
                        int format = c.getInt(2);
                        c.getLong(3);
                        lastId = rowId;
                        if (path != null && path.startsWith(Separators.SLASH)) {
                            boolean exists = false;
                            try {
                                exists = Libcore.os.access(path, OsConstants.F_OK);
                            } catch (ErrnoException e) {
                            }
                            if (!exists && !MtpConstants.isAbstractObject(format)) {
                                MediaFile.MediaFileType mediaFileType = MediaFile.getFileType(path);
                                int fileType = mediaFileType == null ? 0 : mediaFileType.fileType;
                                if (!MediaFile.isPlayListFileType(fileType)) {
                                    deleter.delete(rowId);
                                    if (path.toLowerCase(Locale.US).endsWith("/.nomedia")) {
                                        deleter.flush();
                                        String parent = new File(path).getParent();
                                        this.mMediaProvider.call(this.mPackageName, MediaStore.UNHIDE_CALL, parent, null);
                                    }
                                }
                            }
                        }
                    }
                }
            } finally {
                if (c != null) {
                    c.close();
                }
                deleter.flush();
            }
        }
        this.mOriginalCount = 0;
        Cursor c2 = this.mMediaProvider.query(this.mPackageName, this.mImagesUri, ID_PROJECTION, null, null, null, null);
        if (c2 != null) {
            this.mOriginalCount = c2.getCount();
            c2.close();
        }
    }

    private boolean inScanDirectory(String path, String[] directories) {
        for (String directory : directories) {
            if (path.startsWith(directory)) {
                return true;
            }
        }
        return false;
    }

    /* JADX WARN: Removed duplicated region for block: B:18:0x00c7 A[Catch: RemoteException -> 0x0111, TryCatch #0 {RemoteException -> 0x0111, blocks: (B:9:0x0053, B:11:0x0092, B:13:0x009c, B:15:0x00b7, B:16:0x00bd, B:18:0x00c7, B:19:0x00d3, B:22:0x00e8, B:24:0x0107), top: B:28:0x0053 }] */
    /* JADX WARN: Removed duplicated region for block: B:24:0x0107 A[Catch: RemoteException -> 0x0111, TryCatch #0 {RemoteException -> 0x0111, blocks: (B:9:0x0053, B:11:0x0092, B:13:0x009c, B:15:0x00b7, B:16:0x00bd, B:18:0x00c7, B:19:0x00d3, B:22:0x00e8, B:24:0x0107), top: B:28:0x0053 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void pruneDeadThumbnailFiles() {
        /*
            Method dump skipped, instructions count: 276
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: android.media.MediaScanner.pruneDeadThumbnailFiles():void");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: MediaScanner$MediaBulkDeleter.class */
    public static class MediaBulkDeleter {
        StringBuilder whereClause = new StringBuilder();
        ArrayList<String> whereArgs = new ArrayList<>(100);
        final IContentProvider mProvider;
        final String mPackageName;
        final Uri mBaseUri;

        public MediaBulkDeleter(IContentProvider provider, String packageName, Uri baseUri) {
            this.mProvider = provider;
            this.mPackageName = packageName;
            this.mBaseUri = baseUri;
        }

        public void delete(long id) throws RemoteException {
            if (this.whereClause.length() != 0) {
                this.whereClause.append(Separators.COMMA);
            }
            this.whereClause.append(Separators.QUESTION);
            this.whereArgs.add("" + id);
            if (this.whereArgs.size() > 100) {
                flush();
            }
        }

        public void flush() throws RemoteException {
            int size = this.whereArgs.size();
            if (size > 0) {
                String[] foo = new String[size];
                this.mProvider.delete(this.mPackageName, this.mBaseUri, "_id IN (" + this.whereClause.toString() + Separators.RPAREN, (String[]) this.whereArgs.toArray(foo));
                this.whereClause.setLength(0);
                this.whereArgs.clear();
            }
        }
    }

    private void postscan(String[] directories) throws RemoteException {
        if (this.mProcessPlaylists) {
            processPlayLists();
        }
        if (this.mOriginalCount == 0 && this.mImagesUri.equals(MediaStore.Images.Media.getContentUri("external"))) {
            pruneDeadThumbnailFiles();
        }
        this.mPlayLists = null;
        this.mMediaProvider = null;
    }

    private void initialize(String volumeName) {
        this.mMediaProvider = this.mContext.getContentResolver().acquireProvider(MediaStore.AUTHORITY);
        this.mAudioUri = MediaStore.Audio.Media.getContentUri(volumeName);
        this.mVideoUri = MediaStore.Video.Media.getContentUri(volumeName);
        this.mImagesUri = MediaStore.Images.Media.getContentUri(volumeName);
        this.mThumbsUri = MediaStore.Images.Thumbnails.getContentUri(volumeName);
        this.mFilesUri = MediaStore.Files.getContentUri(volumeName);
        this.mFilesUriNoNotify = this.mFilesUri.buildUpon().appendQueryParameter("nonotify", "1").build();
        if (!volumeName.equals("internal")) {
            this.mProcessPlaylists = true;
            this.mProcessGenres = true;
            this.mPlaylistsUri = MediaStore.Audio.Playlists.getContentUri(volumeName);
            this.mCaseInsensitivePaths = true;
        }
    }

    public void scanDirectories(String[] directories, String volumeName) {
        try {
            System.currentTimeMillis();
            initialize(volumeName);
            prescan(null, true);
            System.currentTimeMillis();
            this.mMediaInserter = new MediaInserter(this.mMediaProvider, this.mPackageName, 500);
            for (String str : directories) {
                processDirectory(str, this.mClient);
            }
            this.mMediaInserter.flushAll();
            this.mMediaInserter = null;
            System.currentTimeMillis();
            postscan(directories);
            System.currentTimeMillis();
        } catch (SQLException e) {
            Log.e(TAG, "SQLException in MediaScanner.scan()", e);
        } catch (RemoteException e2) {
            Log.e(TAG, "RemoteException in MediaScanner.scan()", e2);
        } catch (UnsupportedOperationException e3) {
            Log.e(TAG, "UnsupportedOperationException in MediaScanner.scan()", e3);
        }
    }

    public Uri scanSingleFile(String path, String volumeName, String mimeType) {
        try {
            initialize(volumeName);
            prescan(path, true);
            File file = new File(path);
            if (!file.exists()) {
                return null;
            }
            long lastModifiedSeconds = file.lastModified() / 1000;
            return this.mClient.doScanFile(path, mimeType, lastModifiedSeconds, file.length(), false, true, isNoMediaPath(path));
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException in MediaScanner.scanFile()", e);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isNoMediaFile(String path) {
        int lastSlash;
        File file = new File(path);
        if (!file.isDirectory() && (lastSlash = path.lastIndexOf(47)) >= 0 && lastSlash + 2 < path.length()) {
            if (path.regionMatches(lastSlash + 1, "._", 0, 2)) {
                return true;
            }
            if (path.regionMatches(true, path.length() - 4, ".jpg", 0, 4)) {
                if (path.regionMatches(true, lastSlash + 1, "AlbumArt_{", 0, 10) || path.regionMatches(true, lastSlash + 1, "AlbumArt.", 0, 9)) {
                    return true;
                }
                int length = (path.length() - lastSlash) - 1;
                if (length == 17 && path.regionMatches(true, lastSlash + 1, "AlbumArtSmall", 0, 13)) {
                    return true;
                }
                if (length == 10 && path.regionMatches(true, lastSlash + 1, "Folder", 0, 6)) {
                    return true;
                }
                return false;
            }
            return false;
        }
        return false;
    }

    public static boolean isNoMediaPath(String path) {
        if (path == null) {
            return false;
        }
        if (path.indexOf("/.") >= 0) {
            return true;
        }
        int i = 1;
        while (true) {
            int offset = i;
            if (offset >= 0) {
                int slashIndex = path.indexOf(47, offset);
                if (slashIndex > offset) {
                    slashIndex++;
                    File file = new File(path.substring(0, slashIndex) + MediaStore.MEDIA_IGNORE_FILENAME);
                    if (file.exists()) {
                        return true;
                    }
                }
                i = slashIndex;
            } else {
                return isNoMediaFile(path);
            }
        }
    }

    public void scanMtpFile(String path, String volumeName, int objectHandle, int format) {
        initialize(volumeName);
        MediaFile.MediaFileType mediaFileType = MediaFile.getFileType(path);
        int fileType = mediaFileType == null ? 0 : mediaFileType.fileType;
        File file = new File(path);
        long lastModifiedSeconds = file.lastModified() / 1000;
        if (!MediaFile.isAudioFileType(fileType) && !MediaFile.isVideoFileType(fileType) && !MediaFile.isImageFileType(fileType) && !MediaFile.isPlayListFileType(fileType) && !MediaFile.isDrmFileType(fileType)) {
            ContentValues values = new ContentValues();
            values.put("_size", Long.valueOf(file.length()));
            values.put("date_modified", Long.valueOf(lastModifiedSeconds));
            try {
                String[] whereArgs = {Integer.toString(objectHandle)};
                this.mMediaProvider.update(this.mPackageName, MediaStore.Files.getMtpObjectsUri(volumeName), values, "_id=?", whereArgs);
                return;
            } catch (RemoteException e) {
                Log.e(TAG, "RemoteException in scanMtpFile", e);
                return;
            }
        }
        this.mMtpObjectHandle = objectHandle;
        Cursor fileList = null;
        try {
            try {
                if (MediaFile.isPlayListFileType(fileType)) {
                    prescan(null, true);
                    FileEntry entry = makeEntryFor(path);
                    if (entry != null) {
                        fileList = this.mMediaProvider.query(this.mPackageName, this.mFilesUri, FILES_PRESCAN_PROJECTION, null, null, null, null);
                        processPlayList(entry, fileList);
                    }
                } else {
                    prescan(path, false);
                    this.mClient.doScanFile(path, mediaFileType.mimeType, lastModifiedSeconds, file.length(), format == 12289, true, isNoMediaPath(path));
                }
                this.mMtpObjectHandle = 0;
                if (fileList != null) {
                    fileList.close();
                }
            } catch (RemoteException e2) {
                Log.e(TAG, "RemoteException in MediaScanner.scanFile()", e2);
                this.mMtpObjectHandle = 0;
                if (0 != 0) {
                    fileList.close();
                }
            }
        } catch (Throwable th) {
            this.mMtpObjectHandle = 0;
            if (0 != 0) {
                fileList.close();
            }
            throw th;
        }
    }

    FileEntry makeEntryFor(String path) {
        Cursor c = null;
        try {
            String[] selectionArgs = {path};
            c = this.mMediaProvider.query(this.mPackageName, this.mFilesUriNoNotify, FILES_PRESCAN_PROJECTION, "_data=?", selectionArgs, null, null);
            if (!c.moveToFirst()) {
                if (c != null) {
                    c.close();
                    return null;
                }
                return null;
            }
            long rowId = c.getLong(0);
            int format = c.getInt(2);
            long lastModified = c.getLong(3);
            FileEntry fileEntry = new FileEntry(rowId, path, lastModified, format);
            if (c != null) {
                c.close();
            }
            return fileEntry;
        } catch (RemoteException e) {
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

    private int matchPaths(String path1, String path2) {
        int result = 0;
        int end1 = path1.length();
        int length = path2.length();
        while (true) {
            int end2 = length;
            if (end1 <= 0 || end2 <= 0) {
                break;
            }
            int slash1 = path1.lastIndexOf(47, end1 - 1);
            int slash2 = path2.lastIndexOf(47, end2 - 1);
            int backSlash1 = path1.lastIndexOf(92, end1 - 1);
            int backSlash2 = path2.lastIndexOf(92, end2 - 1);
            int start1 = slash1 > backSlash1 ? slash1 : backSlash1;
            int start2 = slash2 > backSlash2 ? slash2 : backSlash2;
            int start12 = start1 < 0 ? 0 : start1 + 1;
            int start22 = start2 < 0 ? 0 : start2 + 1;
            int length2 = end1 - start12;
            if (end2 - start22 != length2 || !path1.regionMatches(true, start12, path2, start22, length2)) {
                break;
            }
            result++;
            end1 = start12 - 1;
            length = start22 - 1;
        }
        return result;
    }

    private boolean matchEntries(long rowId, String data) {
        int len = this.mPlaylistEntries.size();
        boolean done = true;
        for (int i = 0; i < len; i++) {
            PlaylistEntry entry = this.mPlaylistEntries.get(i);
            if (entry.bestmatchlevel != Integer.MAX_VALUE) {
                done = false;
                if (data.equalsIgnoreCase(entry.path)) {
                    entry.bestmatchid = rowId;
                    entry.bestmatchlevel = Integer.MAX_VALUE;
                } else {
                    int matchLength = matchPaths(data, entry.path);
                    if (matchLength > entry.bestmatchlevel) {
                        entry.bestmatchid = rowId;
                        entry.bestmatchlevel = matchLength;
                    }
                }
            }
        }
        return done;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cachePlaylistEntry(String line, String playListDirectory) {
        PlaylistEntry entry = new PlaylistEntry();
        int entryLength = line.length();
        while (entryLength > 0 && Character.isWhitespace(line.charAt(entryLength - 1))) {
            entryLength--;
        }
        if (entryLength < 3) {
            return;
        }
        if (entryLength < line.length()) {
            line = line.substring(0, entryLength);
        }
        char ch1 = line.charAt(0);
        boolean fullPath = ch1 == '/' || (Character.isLetter(ch1) && line.charAt(1) == ':' && line.charAt(2) == '\\');
        if (!fullPath) {
            line = playListDirectory + line;
        }
        entry.path = line;
        this.mPlaylistEntries.add(entry);
    }

    private void processCachedPlaylist(Cursor fileList, ContentValues values, Uri playlistUri) {
        fileList.moveToPosition(-1);
        while (fileList.moveToNext()) {
            long rowId = fileList.getLong(0);
            String data = fileList.getString(1);
            if (matchEntries(rowId, data)) {
                break;
            }
        }
        int len = this.mPlaylistEntries.size();
        int index = 0;
        for (int i = 0; i < len; i++) {
            PlaylistEntry entry = this.mPlaylistEntries.get(i);
            if (entry.bestmatchlevel > 0) {
                try {
                    values.clear();
                    values.put("play_order", Integer.valueOf(index));
                    values.put("audio_id", Long.valueOf(entry.bestmatchid));
                    this.mMediaProvider.insert(this.mPackageName, playlistUri, values);
                    index++;
                } catch (RemoteException e) {
                    Log.e(TAG, "RemoteException in MediaScanner.processCachedPlaylist()", e);
                    return;
                }
            }
        }
        this.mPlaylistEntries.clear();
    }

    private void processM3uPlayList(String path, String playListDirectory, Uri uri, ContentValues values, Cursor fileList) {
        BufferedReader reader = null;
        try {
            try {
                File f = new File(path);
                if (f.exists()) {
                    reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)), 8192);
                    this.mPlaylistEntries.clear();
                    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                        if (line.length() > 0 && line.charAt(0) != '#') {
                            cachePlaylistEntry(line, playListDirectory);
                        }
                    }
                    processCachedPlaylist(fileList, values, uri);
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(TAG, "IOException in MediaScanner.processM3uPlayList()", e);
                    }
                }
            } catch (IOException e2) {
                Log.e(TAG, "IOException in MediaScanner.processM3uPlayList()", e2);
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e3) {
                        Log.e(TAG, "IOException in MediaScanner.processM3uPlayList()", e3);
                    }
                }
            }
        } catch (Throwable th) {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e4) {
                    Log.e(TAG, "IOException in MediaScanner.processM3uPlayList()", e4);
                    throw th;
                }
            }
            throw th;
        }
    }

    private void processPlsPlayList(String path, String playListDirectory, Uri uri, ContentValues values, Cursor fileList) {
        int equals;
        BufferedReader reader = null;
        try {
            try {
                File f = new File(path);
                if (f.exists()) {
                    reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)), 8192);
                    this.mPlaylistEntries.clear();
                    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                        if (line.startsWith("File") && (equals = line.indexOf(61)) > 0) {
                            cachePlaylistEntry(line.substring(equals + 1), playListDirectory);
                        }
                    }
                    processCachedPlaylist(fileList, values, uri);
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(TAG, "IOException in MediaScanner.processPlsPlayList()", e);
                    }
                }
            } catch (Throwable th) {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e2) {
                        Log.e(TAG, "IOException in MediaScanner.processPlsPlayList()", e2);
                        throw th;
                    }
                }
                throw th;
            }
        } catch (IOException e3) {
            Log.e(TAG, "IOException in MediaScanner.processPlsPlayList()", e3);
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e4) {
                    Log.e(TAG, "IOException in MediaScanner.processPlsPlayList()", e4);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: MediaScanner$WplHandler.class */
    public class WplHandler implements ElementListener {
        final ContentHandler handler;
        String playListDirectory;

        public WplHandler(String playListDirectory, Uri uri, Cursor fileList) {
            this.playListDirectory = playListDirectory;
            RootElement root = new RootElement("smil");
            Element body = root.getChild("body");
            Element seq = body.getChild(Telephony.Mms.Part.SEQ);
            Element media = seq.getChild(MediaStore.AUTHORITY);
            media.setElementListener(this);
            this.handler = root.getContentHandler();
        }

        @Override // android.sax.StartElementListener
        public void start(Attributes attributes) {
            String path = attributes.getValue("", "src");
            if (path != null) {
                MediaScanner.this.cachePlaylistEntry(path, this.playListDirectory);
            }
        }

        @Override // android.sax.EndElementListener
        public void end() {
        }

        ContentHandler getContentHandler() {
            return this.handler;
        }
    }

    private void processWplPlayList(String path, String playListDirectory, Uri uri, ContentValues values, Cursor fileList) {
        FileInputStream fis = null;
        try {
            try {
                File f = new File(path);
                if (f.exists()) {
                    fis = new FileInputStream(f);
                    this.mPlaylistEntries.clear();
                    Xml.parse(fis, Xml.findEncodingByName("UTF-8"), new WplHandler(playListDirectory, uri, fileList).getContentHandler());
                    processCachedPlaylist(fileList, values, uri);
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        Log.e(TAG, "IOException in MediaScanner.processWplPlayList()", e);
                    }
                }
            } catch (IOException e2) {
                e2.printStackTrace();
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e3) {
                        Log.e(TAG, "IOException in MediaScanner.processWplPlayList()", e3);
                    }
                }
            } catch (SAXException e4) {
                e4.printStackTrace();
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e5) {
                        Log.e(TAG, "IOException in MediaScanner.processWplPlayList()", e5);
                    }
                }
            }
        } catch (Throwable th) {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e6) {
                    Log.e(TAG, "IOException in MediaScanner.processWplPlayList()", e6);
                    throw th;
                }
            }
            throw th;
        }
    }

    private void processPlayList(FileEntry entry, Cursor fileList) throws RemoteException {
        Uri membersUri;
        String path = entry.mPath;
        ContentValues values = new ContentValues();
        int lastSlash = path.lastIndexOf(47);
        if (lastSlash < 0) {
            throw new IllegalArgumentException("bad path " + path);
        }
        long rowId = entry.mRowId;
        String name = values.getAsString("name");
        if (name == null) {
            name = values.getAsString("title");
            if (name == null) {
                int lastDot = path.lastIndexOf(46);
                name = lastDot < 0 ? path.substring(lastSlash + 1) : path.substring(lastSlash + 1, lastDot);
            }
        }
        values.put("name", name);
        values.put("date_modified", Long.valueOf(entry.mLastModified));
        if (rowId == 0) {
            values.put("_data", path);
            Uri uri = this.mMediaProvider.insert(this.mPackageName, this.mPlaylistsUri, values);
            ContentUris.parseId(uri);
            membersUri = Uri.withAppendedPath(uri, "members");
        } else {
            Uri uri2 = ContentUris.withAppendedId(this.mPlaylistsUri, rowId);
            this.mMediaProvider.update(this.mPackageName, uri2, values, null, null);
            membersUri = Uri.withAppendedPath(uri2, "members");
            this.mMediaProvider.delete(this.mPackageName, membersUri, null, null);
        }
        String playListDirectory = path.substring(0, lastSlash + 1);
        MediaFile.MediaFileType mediaFileType = MediaFile.getFileType(path);
        int fileType = mediaFileType == null ? 0 : mediaFileType.fileType;
        if (fileType == 41) {
            processM3uPlayList(path, playListDirectory, membersUri, values, fileList);
        } else if (fileType == 42) {
            processPlsPlayList(path, playListDirectory, membersUri, values, fileList);
        } else if (fileType == 43) {
            processWplPlayList(path, playListDirectory, membersUri, values, fileList);
        }
    }

    private void processPlayLists() throws RemoteException {
        Iterator<FileEntry> iterator = this.mPlayLists.iterator();
        Cursor fileList = null;
        try {
            fileList = this.mMediaProvider.query(this.mPackageName, this.mFilesUri, FILES_PRESCAN_PROJECTION, "media_type=2", null, null, null);
            while (iterator.hasNext()) {
                FileEntry entry = iterator.next();
                if (entry.mLastModifiedChanged) {
                    processPlayList(entry, fileList);
                }
            }
            if (fileList != null) {
                fileList.close();
            }
        } catch (RemoteException e) {
            if (fileList != null) {
                fileList.close();
            }
        } catch (Throwable th) {
            if (fileList != null) {
                fileList.close();
            }
            throw th;
        }
    }

    public void release() {
        native_finalize();
    }

    protected void finalize() {
        this.mContext.getContentResolver().releaseProvider(this.mMediaProvider);
        native_finalize();
    }
}