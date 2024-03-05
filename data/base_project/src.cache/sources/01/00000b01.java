package android.os;

import android.util.Log;
import android.util.Slog;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Pattern;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import libcore.io.ErrnoException;
import libcore.io.Libcore;

/* loaded from: FileUtils.class */
public class FileUtils {
    private static final String TAG = "FileUtils";
    public static final int S_IRWXU = 448;
    public static final int S_IRUSR = 256;
    public static final int S_IWUSR = 128;
    public static final int S_IXUSR = 64;
    public static final int S_IRWXG = 56;
    public static final int S_IRGRP = 32;
    public static final int S_IWGRP = 16;
    public static final int S_IXGRP = 8;
    public static final int S_IRWXO = 7;
    public static final int S_IROTH = 4;
    public static final int S_IWOTH = 2;
    public static final int S_IXOTH = 1;
    private static final Pattern SAFE_FILENAME_PATTERN = Pattern.compile("[\\w%+,./=_-]+");

    public static native int getFatVolumeId(String str);

    public static int setPermissions(File path, int mode, int uid, int gid) {
        return setPermissions(path.getAbsolutePath(), mode, uid, gid);
    }

    public static int setPermissions(String path, int mode, int uid, int gid) {
        try {
            Libcore.os.chmod(path, mode);
            if (uid >= 0 || gid >= 0) {
                try {
                    Libcore.os.chown(path, uid, gid);
                    return 0;
                } catch (ErrnoException e) {
                    Slog.w(TAG, "Failed to chown(" + path + "): " + e);
                    return e.errno;
                }
            }
            return 0;
        } catch (ErrnoException e2) {
            Slog.w(TAG, "Failed to chmod(" + path + "): " + e2);
            return e2.errno;
        }
    }

    public static int setPermissions(FileDescriptor fd, int mode, int uid, int gid) {
        try {
            Libcore.os.fchmod(fd, mode);
            if (uid >= 0 || gid >= 0) {
                try {
                    Libcore.os.fchown(fd, uid, gid);
                    return 0;
                } catch (ErrnoException e) {
                    Slog.w(TAG, "Failed to fchown(): " + e);
                    return e.errno;
                }
            }
            return 0;
        } catch (ErrnoException e2) {
            Slog.w(TAG, "Failed to fchmod(): " + e2);
            return e2.errno;
        }
    }

    public static int getUid(String path) {
        try {
            return Libcore.os.stat(path).st_uid;
        } catch (ErrnoException e) {
            return -1;
        }
    }

    public static boolean sync(FileOutputStream stream) {
        if (stream != null) {
            try {
                stream.getFD().sync();
                return true;
            } catch (IOException e) {
                return false;
            }
        }
        return true;
    }

    public static boolean copyFile(File srcFile, File destFile) {
        boolean result;
        try {
            InputStream in = new FileInputStream(srcFile);
            result = copyToFile(in, destFile);
            in.close();
        } catch (IOException e) {
            result = false;
        }
        return result;
    }

    public static boolean copyToFile(InputStream inputStream, File destFile) {
        try {
            if (destFile.exists()) {
                destFile.delete();
            }
            FileOutputStream out = new FileOutputStream(destFile);
            byte[] buffer = new byte[4096];
            while (true) {
                int bytesRead = inputStream.read(buffer);
                if (bytesRead < 0) {
                    break;
                }
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
            try {
                out.getFD().sync();
            } catch (IOException e) {
            }
            out.close();
            return true;
        } catch (IOException e2) {
            return false;
        }
    }

    public static boolean isFilenameSafe(File file) {
        return SAFE_FILENAME_PATTERN.matcher(file.getPath()).matches();
    }

    public static String readTextFile(File file, int max, String ellipsis) throws IOException {
        int len;
        int len2;
        InputStream input = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(input);
        try {
            long size = file.length();
            if (max > 0 || (size > 0 && max == 0)) {
                if (size > 0 && (max == 0 || size < max)) {
                    max = (int) size;
                }
                byte[] data = new byte[max + 1];
                int length = bis.read(data);
                if (length <= 0) {
                    return "";
                }
                if (length <= max) {
                    String str = new String(data, 0, length);
                    bis.close();
                    input.close();
                    return str;
                } else if (ellipsis == null) {
                    String str2 = new String(data, 0, max);
                    bis.close();
                    input.close();
                    return str2;
                } else {
                    String str3 = new String(data, 0, max) + ellipsis;
                    bis.close();
                    input.close();
                    return str3;
                }
            } else if (max >= 0) {
                ByteArrayOutputStream contents = new ByteArrayOutputStream();
                byte[] data2 = new byte[1024];
                do {
                    len = bis.read(data2);
                    if (len > 0) {
                        contents.write(data2, 0, len);
                    }
                } while (len == data2.length);
                String byteArrayOutputStream = contents.toString();
                bis.close();
                input.close();
                return byteArrayOutputStream;
            } else {
                boolean rolled = false;
                byte[] last = null;
                byte[] data3 = null;
                do {
                    if (last != null) {
                        rolled = true;
                    }
                    byte[] tmp = last;
                    last = data3;
                    data3 = tmp;
                    if (data3 == null) {
                        data3 = new byte[-max];
                    }
                    len2 = bis.read(data3);
                } while (len2 == data3.length);
                if (last == null && len2 <= 0) {
                    bis.close();
                    input.close();
                    return "";
                } else if (last == null) {
                    String str4 = new String(data3, 0, len2);
                    bis.close();
                    input.close();
                    return str4;
                } else {
                    if (len2 > 0) {
                        rolled = true;
                        System.arraycopy(last, len2, last, 0, last.length - len2);
                        System.arraycopy(data3, 0, last, last.length - len2, len2);
                    }
                    if (ellipsis == null || !rolled) {
                        String str5 = new String(last);
                        bis.close();
                        input.close();
                        return str5;
                    }
                    String str6 = ellipsis + new String(last);
                    bis.close();
                    input.close();
                    return str6;
                }
            }
        } finally {
            bis.close();
            input.close();
        }
    }

    public static void stringToFile(String filename, String string) throws IOException {
        FileWriter out = new FileWriter(filename);
        try {
            out.write(string);
            out.close();
        } catch (Throwable th) {
            out.close();
            throw th;
        }
    }

    public static long checksumCrc32(File file) throws FileNotFoundException, IOException {
        CRC32 checkSummer = new CRC32();
        CheckedInputStream cis = null;
        try {
            cis = new CheckedInputStream(new FileInputStream(file), checkSummer);
            byte[] buf = new byte[128];
            while (cis.read(buf) >= 0) {
            }
            long value = checkSummer.getValue();
            if (cis != null) {
                try {
                    cis.close();
                } catch (IOException e) {
                }
            }
            return value;
        } catch (Throwable th) {
            if (cis != null) {
                try {
                    cis.close();
                } catch (IOException e2) {
                }
            }
            throw th;
        }
    }

    public static void deleteOlderFiles(File dir, int minCount, long minAge) {
        if (minCount < 0 || minAge < 0) {
            throw new IllegalArgumentException("Constraints must be positive or 0");
        }
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        Arrays.sort(files, new Comparator<File>() { // from class: android.os.FileUtils.1
            @Override // java.util.Comparator
            public int compare(File lhs, File rhs) {
                return (int) (rhs.lastModified() - lhs.lastModified());
            }
        });
        for (int i = minCount; i < files.length; i++) {
            File file = files[i];
            long age = System.currentTimeMillis() - file.lastModified();
            if (age > minAge) {
                Log.d(TAG, "Deleting old file " + file);
                file.delete();
            }
        }
    }
}