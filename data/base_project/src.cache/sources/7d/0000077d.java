package android.media;

import android.app.backup.FullBackup;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.net.Uri;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

/* loaded from: MediaMetadataRetriever.class */
public class MediaMetadataRetriever {
    private int mNativeContext;
    private static final int EMBEDDED_PICTURE_TYPE_ANY = 65535;
    public static final int OPTION_PREVIOUS_SYNC = 0;
    public static final int OPTION_NEXT_SYNC = 1;
    public static final int OPTION_CLOSEST_SYNC = 2;
    public static final int OPTION_CLOSEST = 3;
    public static final int METADATA_KEY_CD_TRACK_NUMBER = 0;
    public static final int METADATA_KEY_ALBUM = 1;
    public static final int METADATA_KEY_ARTIST = 2;
    public static final int METADATA_KEY_AUTHOR = 3;
    public static final int METADATA_KEY_COMPOSER = 4;
    public static final int METADATA_KEY_DATE = 5;
    public static final int METADATA_KEY_GENRE = 6;
    public static final int METADATA_KEY_TITLE = 7;
    public static final int METADATA_KEY_YEAR = 8;
    public static final int METADATA_KEY_DURATION = 9;
    public static final int METADATA_KEY_NUM_TRACKS = 10;
    public static final int METADATA_KEY_WRITER = 11;
    public static final int METADATA_KEY_MIMETYPE = 12;
    public static final int METADATA_KEY_ALBUMARTIST = 13;
    public static final int METADATA_KEY_DISC_NUMBER = 14;
    public static final int METADATA_KEY_COMPILATION = 15;
    public static final int METADATA_KEY_HAS_AUDIO = 16;
    public static final int METADATA_KEY_HAS_VIDEO = 17;
    public static final int METADATA_KEY_VIDEO_WIDTH = 18;
    public static final int METADATA_KEY_VIDEO_HEIGHT = 19;
    public static final int METADATA_KEY_BITRATE = 20;
    public static final int METADATA_KEY_TIMED_TEXT_LANGUAGES = 21;
    public static final int METADATA_KEY_IS_DRM = 22;
    public static final int METADATA_KEY_LOCATION = 23;
    public static final int METADATA_KEY_VIDEO_ROTATION = 24;

    private native void _setDataSource(String str, String[] strArr, String[] strArr2) throws IllegalArgumentException;

    public native void setDataSource(FileDescriptor fileDescriptor, long j, long j2) throws IllegalArgumentException;

    public native String extractMetadata(int i);

    private native Bitmap _getFrameAtTime(long j, int i);

    private native byte[] getEmbeddedPicture(int i);

    public native void release();

    private native void native_setup();

    private static native void native_init();

    private final native void native_finalize();

    static {
        System.loadLibrary("media_jni");
        native_init();
    }

    public MediaMetadataRetriever() {
        native_setup();
    }

    public void setDataSource(String path) throws IllegalArgumentException {
        if (path == null) {
            throw new IllegalArgumentException();
        }
        try {
            FileInputStream is = new FileInputStream(path);
            FileDescriptor fd = is.getFD();
            setDataSource(fd, 0L, 576460752303423487L);
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                }
            }
        } catch (FileNotFoundException e2) {
            throw new IllegalArgumentException();
        } catch (IOException e3) {
            throw new IllegalArgumentException();
        }
    }

    public void setDataSource(String uri, Map<String, String> headers) throws IllegalArgumentException {
        int i = 0;
        String[] keys = new String[headers.size()];
        String[] values = new String[headers.size()];
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            keys[i] = entry.getKey();
            values[i] = entry.getValue();
            i++;
        }
        _setDataSource(uri, keys, values);
    }

    public void setDataSource(FileDescriptor fd) throws IllegalArgumentException {
        setDataSource(fd, 0L, 576460752303423487L);
    }

    public void setDataSource(Context context, Uri uri) throws IllegalArgumentException, SecurityException {
        if (uri == null) {
            throw new IllegalArgumentException();
        }
        String scheme = uri.getScheme();
        if (scheme == null || scheme.equals(ContentResolver.SCHEME_FILE)) {
            setDataSource(uri.getPath());
            return;
        }
        AssetFileDescriptor fd = null;
        try {
            ContentResolver resolver = context.getContentResolver();
            try {
                AssetFileDescriptor fd2 = resolver.openAssetFileDescriptor(uri, FullBackup.ROOT_TREE_TOKEN);
                if (fd2 == null) {
                    throw new IllegalArgumentException();
                }
                FileDescriptor descriptor = fd2.getFileDescriptor();
                if (!descriptor.valid()) {
                    throw new IllegalArgumentException();
                }
                if (fd2.getDeclaredLength() < 0) {
                    setDataSource(descriptor);
                } else {
                    setDataSource(descriptor, fd2.getStartOffset(), fd2.getDeclaredLength());
                }
                if (fd2 != null) {
                    try {
                        fd2.close();
                    } catch (IOException e) {
                    }
                }
            } catch (FileNotFoundException e2) {
                throw new IllegalArgumentException();
            }
        } catch (SecurityException e3) {
            if (0 != 0) {
                try {
                    fd.close();
                } catch (IOException e4) {
                    setDataSource(uri.toString());
                }
            }
            setDataSource(uri.toString());
        } catch (Throwable th) {
            if (0 != 0) {
                try {
                    fd.close();
                } catch (IOException e5) {
                    throw th;
                }
            }
            throw th;
        }
    }

    public Bitmap getFrameAtTime(long timeUs, int option) {
        if (option < 0 || option > 3) {
            throw new IllegalArgumentException("Unsupported option: " + option);
        }
        return _getFrameAtTime(timeUs, option);
    }

    public Bitmap getFrameAtTime(long timeUs) {
        return getFrameAtTime(timeUs, 2);
    }

    public Bitmap getFrameAtTime() {
        return getFrameAtTime(-1L, 2);
    }

    public byte[] getEmbeddedPicture() {
        return getEmbeddedPicture(65535);
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
}