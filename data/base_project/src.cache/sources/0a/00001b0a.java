package com.android.internal.util;

import android.os.FileUtils;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import libcore.io.IoUtils;
import libcore.io.Streams;

/* loaded from: FileRotator.class */
public class FileRotator {
    private static final String TAG = "FileRotator";
    private static final boolean LOGD = false;
    private final File mBasePath;
    private final String mPrefix;
    private final long mRotateAgeMillis;
    private final long mDeleteAgeMillis;
    private static final String SUFFIX_BACKUP = ".backup";
    private static final String SUFFIX_NO_BACKUP = ".no_backup";

    /* loaded from: FileRotator$Reader.class */
    public interface Reader {
        void read(InputStream inputStream) throws IOException;
    }

    /* loaded from: FileRotator$Rewriter.class */
    public interface Rewriter extends Reader, Writer {
        void reset();

        boolean shouldWrite();
    }

    /* loaded from: FileRotator$Writer.class */
    public interface Writer {
        void write(OutputStream outputStream) throws IOException;
    }

    public FileRotator(File basePath, String prefix, long rotateAgeMillis, long deleteAgeMillis) {
        this.mBasePath = (File) Preconditions.checkNotNull(basePath);
        this.mPrefix = (String) Preconditions.checkNotNull(prefix);
        this.mRotateAgeMillis = rotateAgeMillis;
        this.mDeleteAgeMillis = deleteAgeMillis;
        this.mBasePath.mkdirs();
        String[] arr$ = this.mBasePath.list();
        for (String name : arr$) {
            if (name.startsWith(this.mPrefix)) {
                if (name.endsWith(SUFFIX_BACKUP)) {
                    File backupFile = new File(this.mBasePath, name);
                    File file = new File(this.mBasePath, name.substring(0, name.length() - SUFFIX_BACKUP.length()));
                    backupFile.renameTo(file);
                } else if (name.endsWith(SUFFIX_NO_BACKUP)) {
                    File noBackupFile = new File(this.mBasePath, name);
                    File file2 = new File(this.mBasePath, name.substring(0, name.length() - SUFFIX_NO_BACKUP.length()));
                    noBackupFile.delete();
                    file2.delete();
                }
            }
        }
    }

    public void deleteAll() {
        FileInfo info = new FileInfo(this.mPrefix);
        String[] arr$ = this.mBasePath.list();
        for (String name : arr$) {
            if (info.parse(name)) {
                new File(this.mBasePath, name).delete();
            }
        }
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [java.util.zip.ZipOutputStream, java.io.OutputStream, java.lang.AutoCloseable] */
    /* JADX WARN: Type inference failed for: r0v19, types: [java.io.FileInputStream, java.lang.AutoCloseable, java.io.InputStream] */
    public void dumpAll(OutputStream os) throws IOException {
        ?? zipOutputStream = new ZipOutputStream(os);
        try {
            FileInfo info = new FileInfo(this.mPrefix);
            String[] arr$ = this.mBasePath.list();
            for (String name : arr$) {
                if (info.parse(name)) {
                    ZipEntry entry = new ZipEntry(name);
                    zipOutputStream.putNextEntry(entry);
                    File file = new File(this.mBasePath, name);
                    ?? fileInputStream = new FileInputStream(file);
                    Streams.copy(fileInputStream, zipOutputStream);
                    IoUtils.closeQuietly((AutoCloseable) fileInputStream);
                    zipOutputStream.closeEntry();
                }
            }
        } finally {
            IoUtils.closeQuietly((AutoCloseable) zipOutputStream);
        }
    }

    public void rewriteActive(Rewriter rewriter, long currentTimeMillis) throws IOException {
        String activeName = getActiveName(currentTimeMillis);
        rewriteSingle(rewriter, activeName);
    }

    @Deprecated
    public void combineActive(final Reader reader, final Writer writer, long currentTimeMillis) throws IOException {
        rewriteActive(new Rewriter() { // from class: com.android.internal.util.FileRotator.1
            @Override // com.android.internal.util.FileRotator.Rewriter
            public void reset() {
            }

            @Override // com.android.internal.util.FileRotator.Reader
            public void read(InputStream in) throws IOException {
                reader.read(in);
            }

            @Override // com.android.internal.util.FileRotator.Rewriter
            public boolean shouldWrite() {
                return true;
            }

            @Override // com.android.internal.util.FileRotator.Writer
            public void write(OutputStream out) throws IOException {
                writer.write(out);
            }
        }, currentTimeMillis);
    }

    public void rewriteAll(Rewriter rewriter) throws IOException {
        FileInfo info = new FileInfo(this.mPrefix);
        String[] arr$ = this.mBasePath.list();
        for (String name : arr$) {
            if (info.parse(name)) {
                rewriteSingle(rewriter, name);
            }
        }
    }

    private void rewriteSingle(Rewriter rewriter, String name) throws IOException {
        File file = new File(this.mBasePath, name);
        rewriter.reset();
        if (file.exists()) {
            readFile(file, rewriter);
            if (rewriter.shouldWrite()) {
                File backupFile = new File(this.mBasePath, name + SUFFIX_BACKUP);
                file.renameTo(backupFile);
                try {
                    writeFile(file, rewriter);
                    backupFile.delete();
                    return;
                } catch (Throwable t) {
                    file.delete();
                    backupFile.renameTo(file);
                    throw rethrowAsIoException(t);
                }
            }
            return;
        }
        File backupFile2 = new File(this.mBasePath, name + SUFFIX_NO_BACKUP);
        backupFile2.createNewFile();
        try {
            writeFile(file, rewriter);
            backupFile2.delete();
        } catch (Throwable t2) {
            file.delete();
            backupFile2.delete();
            throw rethrowAsIoException(t2);
        }
    }

    public void readMatching(Reader reader, long matchStartMillis, long matchEndMillis) throws IOException {
        FileInfo info = new FileInfo(this.mPrefix);
        String[] arr$ = this.mBasePath.list();
        for (String name : arr$) {
            if (info.parse(name) && info.startMillis <= matchEndMillis && matchStartMillis <= info.endMillis) {
                File file = new File(this.mBasePath, name);
                readFile(file, reader);
            }
        }
    }

    private String getActiveName(long currentTimeMillis) {
        String oldestActiveName = null;
        long oldestActiveStart = Long.MAX_VALUE;
        FileInfo info = new FileInfo(this.mPrefix);
        String[] arr$ = this.mBasePath.list();
        for (String name : arr$) {
            if (info.parse(name) && info.isActive() && info.startMillis < currentTimeMillis && info.startMillis < oldestActiveStart) {
                oldestActiveName = name;
                oldestActiveStart = info.startMillis;
            }
        }
        if (oldestActiveName != null) {
            return oldestActiveName;
        }
        info.startMillis = currentTimeMillis;
        info.endMillis = Long.MAX_VALUE;
        return info.build();
    }

    public void maybeRotate(long currentTimeMillis) {
        long rotateBefore = currentTimeMillis - this.mRotateAgeMillis;
        long deleteBefore = currentTimeMillis - this.mDeleteAgeMillis;
        FileInfo info = new FileInfo(this.mPrefix);
        String[] arr$ = this.mBasePath.list();
        for (String name : arr$) {
            if (info.parse(name)) {
                if (info.isActive()) {
                    if (info.startMillis <= rotateBefore) {
                        info.endMillis = currentTimeMillis;
                        File file = new File(this.mBasePath, name);
                        File destFile = new File(this.mBasePath, info.build());
                        file.renameTo(destFile);
                    }
                } else if (info.endMillis <= deleteBefore) {
                    File file2 = new File(this.mBasePath, name);
                    file2.delete();
                }
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v1, types: [java.io.BufferedInputStream, java.lang.AutoCloseable, java.io.InputStream] */
    private static void readFile(File file, Reader reader) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        ?? bufferedInputStream = new BufferedInputStream(fis);
        try {
            reader.read(bufferedInputStream);
            IoUtils.closeQuietly((AutoCloseable) bufferedInputStream);
        } catch (Throwable th) {
            IoUtils.closeQuietly((AutoCloseable) bufferedInputStream);
            throw th;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v1, types: [java.io.OutputStream, java.lang.AutoCloseable, java.io.BufferedOutputStream] */
    private static void writeFile(File file, Writer writer) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        ?? bufferedOutputStream = new BufferedOutputStream(fos);
        try {
            writer.write(bufferedOutputStream);
            bufferedOutputStream.flush();
            FileUtils.sync(fos);
            IoUtils.closeQuietly((AutoCloseable) bufferedOutputStream);
        } catch (Throwable th) {
            FileUtils.sync(fos);
            IoUtils.closeQuietly((AutoCloseable) bufferedOutputStream);
            throw th;
        }
    }

    private static IOException rethrowAsIoException(Throwable t) throws IOException {
        if (t instanceof IOException) {
            throw ((IOException) t);
        }
        throw new IOException(t.getMessage(), t);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: FileRotator$FileInfo.class */
    public static class FileInfo {
        public final String prefix;
        public long startMillis;
        public long endMillis;

        public FileInfo(String prefix) {
            this.prefix = (String) Preconditions.checkNotNull(prefix);
        }

        public boolean parse(String name) {
            this.endMillis = -1L;
            this.startMillis = -1L;
            int dotIndex = name.lastIndexOf(46);
            int dashIndex = name.lastIndexOf(45);
            if (dotIndex == -1 || dashIndex == -1 || !this.prefix.equals(name.substring(0, dotIndex))) {
                return false;
            }
            try {
                this.startMillis = Long.parseLong(name.substring(dotIndex + 1, dashIndex));
                if (name.length() - dashIndex == 1) {
                    this.endMillis = Long.MAX_VALUE;
                    return true;
                }
                this.endMillis = Long.parseLong(name.substring(dashIndex + 1));
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        public String build() {
            StringBuilder name = new StringBuilder();
            name.append(this.prefix).append('.').append(this.startMillis).append('-');
            if (this.endMillis != Long.MAX_VALUE) {
                name.append(this.endMillis);
            }
            return name.toString();
        }

        public boolean isActive() {
            return this.endMillis == Long.MAX_VALUE;
        }
    }
}