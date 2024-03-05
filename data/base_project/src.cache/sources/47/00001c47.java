package com.android.server;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.DropBoxManager;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Slog;
import android.util.TimedRemoteCaller;
import com.android.internal.os.IDropBoxManagerService;
import gov.nist.core.Separators;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/* loaded from: DropBoxManagerService.class */
public final class DropBoxManagerService extends IDropBoxManagerService.Stub {
    private static final String TAG = "DropBoxManagerService";
    private static final int DEFAULT_AGE_SECONDS = 259200;
    private static final int DEFAULT_MAX_FILES = 1000;
    private static final int DEFAULT_QUOTA_KB = 5120;
    private static final int DEFAULT_QUOTA_PERCENT = 10;
    private static final int DEFAULT_RESERVE_PERCENT = 10;
    private static final int QUOTA_RESCAN_MILLIS = 5000;
    private static final int MSG_SEND_BROADCAST = 1;
    private static final boolean PROFILE_DUMP = false;
    private final Context mContext;
    private final ContentResolver mContentResolver;
    private final File mDropBoxDir;
    private final Handler mHandler;
    private FileList mAllFiles = null;
    private HashMap<String, FileList> mFilesByTag = null;
    private StatFs mStatFs = null;
    private int mBlockSize = 0;
    private int mCachedQuotaBlocks = 0;
    private long mCachedQuotaUptimeMillis = 0;
    private volatile boolean mBooted = false;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.android.server.DropBoxManagerService.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent == null || !Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
                DropBoxManagerService.this.mCachedQuotaUptimeMillis = 0L;
                new Thread() { // from class: com.android.server.DropBoxManagerService.1.1
                    @Override // java.lang.Thread, java.lang.Runnable
                    public void run() {
                        try {
                            DropBoxManagerService.this.init();
                            DropBoxManagerService.this.trimToFit();
                        } catch (IOException e) {
                            Slog.e(DropBoxManagerService.TAG, "Can't init", e);
                        }
                    }
                }.start();
                return;
            }
            DropBoxManagerService.this.mBooted = true;
        }
    };

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.DropBoxManagerService.add(android.os.DropBoxManager$Entry):void, file: DropBoxManagerService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    @Override // com.android.internal.os.IDropBoxManagerService
    public void add(android.os.DropBoxManager.Entry r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.DropBoxManagerService.add(android.os.DropBoxManager$Entry):void, file: DropBoxManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.DropBoxManagerService.add(android.os.DropBoxManager$Entry):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.DropBoxManagerService.isTagEnabled(java.lang.String):boolean, file: DropBoxManagerService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    @Override // com.android.internal.os.IDropBoxManagerService
    public boolean isTagEnabled(java.lang.String r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.DropBoxManagerService.isTagEnabled(java.lang.String):boolean, file: DropBoxManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.DropBoxManagerService.isTagEnabled(java.lang.String):boolean");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.DropBoxManagerService.dump(java.io.FileDescriptor, java.io.PrintWriter, java.lang.String[]):void, file: DropBoxManagerService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    @Override // android.os.Binder
    public synchronized void dump(java.io.FileDescriptor r1, java.io.PrintWriter r2, java.lang.String[] r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.DropBoxManagerService.dump(java.io.FileDescriptor, java.io.PrintWriter, java.lang.String[]):void, file: DropBoxManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.DropBoxManagerService.dump(java.io.FileDescriptor, java.io.PrintWriter, java.lang.String[]):void");
    }

    public DropBoxManagerService(final Context context, File path) {
        this.mDropBoxDir = path;
        this.mContext = context;
        this.mContentResolver = context.getContentResolver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_DEVICE_STORAGE_LOW);
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);
        context.registerReceiver(this.mReceiver, filter);
        this.mContentResolver.registerContentObserver(Settings.Global.CONTENT_URI, true, new ContentObserver(new Handler()) { // from class: com.android.server.DropBoxManagerService.2
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange) {
                DropBoxManagerService.this.mReceiver.onReceive(context, null);
            }
        });
        this.mHandler = new Handler() { // from class: com.android.server.DropBoxManagerService.3
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    DropBoxManagerService.this.mContext.sendBroadcastAsUser((Intent) msg.obj, UserHandle.OWNER, Manifest.permission.READ_LOGS);
                }
            }
        };
    }

    public void stop() {
        this.mContext.unregisterReceiver(this.mReceiver);
    }

    @Override // com.android.internal.os.IDropBoxManagerService
    public synchronized DropBoxManager.Entry getNextEntry(String tag, long millis) {
        if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.READ_LOGS) != 0) {
            throw new SecurityException("READ_LOGS permission required");
        }
        try {
            init();
            FileList list = tag == null ? this.mAllFiles : this.mFilesByTag.get(tag);
            if (list == null) {
                return null;
            }
            for (EntryFile entry : list.contents.tailSet(new EntryFile(millis + 1))) {
                if (entry.tag != null) {
                    if ((entry.flags & 1) != 0) {
                        return new DropBoxManager.Entry(entry.tag, entry.timestampMillis);
                    }
                    try {
                        return new DropBoxManager.Entry(entry.tag, entry.timestampMillis, entry.file, entry.flags);
                    } catch (IOException e) {
                        Slog.e(TAG, "Can't read: " + entry.file, e);
                    }
                }
            }
            return null;
        } catch (IOException e2) {
            Slog.e(TAG, "Can't init", e2);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: DropBoxManagerService$FileList.class */
    public static final class FileList implements Comparable<FileList> {
        public int blocks;
        public final TreeSet<EntryFile> contents;

        private FileList() {
            this.blocks = 0;
            this.contents = new TreeSet<>();
        }

        @Override // java.lang.Comparable
        public final int compareTo(FileList o) {
            if (this.blocks != o.blocks) {
                return o.blocks - this.blocks;
            }
            if (this == o) {
                return 0;
            }
            if (hashCode() < o.hashCode()) {
                return -1;
            }
            return hashCode() > o.hashCode() ? 1 : 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: DropBoxManagerService$EntryFile.class */
    public static final class EntryFile implements Comparable<EntryFile> {
        public final String tag;
        public final long timestampMillis;
        public final int flags;
        public final File file;
        public final int blocks;

        @Override // java.lang.Comparable
        public final int compareTo(EntryFile o) {
            if (this.timestampMillis < o.timestampMillis) {
                return -1;
            }
            if (this.timestampMillis > o.timestampMillis) {
                return 1;
            }
            if (this.file == null || o.file == null) {
                if (o.file != null) {
                    return -1;
                }
                if (this.file != null) {
                    return 1;
                }
                if (this == o) {
                    return 0;
                }
                if (hashCode() < o.hashCode()) {
                    return -1;
                }
                return hashCode() > o.hashCode() ? 1 : 0;
            }
            return this.file.compareTo(o.file);
        }

        public EntryFile(File temp, File dir, String tag, long timestampMillis, int flags, int blockSize) throws IOException {
            if ((flags & 1) != 0) {
                throw new IllegalArgumentException();
            }
            this.tag = tag;
            this.timestampMillis = timestampMillis;
            this.flags = flags;
            this.file = new File(dir, Uri.encode(tag) + Separators.AT + timestampMillis + ((flags & 2) != 0 ? ".txt" : ".dat") + ((flags & 4) != 0 ? ".gz" : ""));
            if (!temp.renameTo(this.file)) {
                throw new IOException("Can't rename " + temp + " to " + this.file);
            }
            this.blocks = (int) (((this.file.length() + blockSize) - 1) / blockSize);
        }

        public EntryFile(File dir, String tag, long timestampMillis) throws IOException {
            this.tag = tag;
            this.timestampMillis = timestampMillis;
            this.flags = 1;
            this.file = new File(dir, Uri.encode(tag) + Separators.AT + timestampMillis + ".lost");
            this.blocks = 0;
            new FileOutputStream(this.file).close();
        }

        public EntryFile(File file, int blockSize) {
            String name;
            long millis;
            this.file = file;
            this.blocks = (int) (((this.file.length() + blockSize) - 1) / blockSize);
            String name2 = file.getName();
            int at = name2.lastIndexOf(64);
            if (at < 0) {
                this.tag = null;
                this.timestampMillis = 0L;
                this.flags = 1;
                return;
            }
            int flags = 0;
            this.tag = Uri.decode(name2.substring(0, at));
            if (name2.endsWith(".gz")) {
                flags = 0 | 4;
                name2 = name2.substring(0, name2.length() - 3);
            }
            if (name2.endsWith(".lost")) {
                flags |= 1;
                name = name2.substring(at + 1, name2.length() - 5);
            } else if (name2.endsWith(".txt")) {
                flags |= 2;
                name = name2.substring(at + 1, name2.length() - 4);
            } else if (name2.endsWith(".dat")) {
                name = name2.substring(at + 1, name2.length() - 4);
            } else {
                this.flags = 1;
                this.timestampMillis = 0L;
                return;
            }
            this.flags = flags;
            try {
                millis = Long.valueOf(name).longValue();
            } catch (NumberFormatException e) {
                millis = 0;
            }
            this.timestampMillis = millis;
        }

        public EntryFile(long millis) {
            this.tag = null;
            this.timestampMillis = millis;
            this.flags = 1;
            this.file = null;
            this.blocks = 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void init() throws IOException {
        if (this.mStatFs == null) {
            if (!this.mDropBoxDir.isDirectory() && !this.mDropBoxDir.mkdirs()) {
                throw new IOException("Can't mkdir: " + this.mDropBoxDir);
            }
            try {
                this.mStatFs = new StatFs(this.mDropBoxDir.getPath());
                this.mBlockSize = this.mStatFs.getBlockSize();
            } catch (IllegalArgumentException e) {
                throw new IOException("Can't statfs: " + this.mDropBoxDir);
            }
        }
        if (this.mAllFiles == null) {
            File[] files = this.mDropBoxDir.listFiles();
            if (files == null) {
                throw new IOException("Can't list files: " + this.mDropBoxDir);
            }
            this.mAllFiles = new FileList();
            this.mFilesByTag = new HashMap<>();
            for (File file : files) {
                if (file.getName().endsWith(".tmp")) {
                    Slog.i(TAG, "Cleaning temp file: " + file);
                    file.delete();
                } else {
                    EntryFile entry = new EntryFile(file, this.mBlockSize);
                    if (entry.tag == null) {
                        Slog.w(TAG, "Unrecognized file: " + file);
                    } else if (entry.timestampMillis == 0) {
                        Slog.w(TAG, "Invalid filename: " + file);
                        file.delete();
                    } else {
                        enrollEntry(entry);
                    }
                }
            }
        }
    }

    private synchronized void enrollEntry(EntryFile entry) {
        this.mAllFiles.contents.add(entry);
        this.mAllFiles.blocks += entry.blocks;
        if (entry.tag != null && entry.file != null && entry.blocks > 0) {
            FileList tagFiles = this.mFilesByTag.get(entry.tag);
            if (tagFiles == null) {
                tagFiles = new FileList();
                this.mFilesByTag.put(entry.tag, tagFiles);
            }
            tagFiles.contents.add(entry);
            tagFiles.blocks += entry.blocks;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r5v2, types: [long, java.lang.String] */
    private synchronized long createEntry(File temp, String tag, int flags) throws IOException {
        long t = System.currentTimeMillis();
        SortedSet<EntryFile> tail = this.mAllFiles.contents.tailSet(new EntryFile(t + 10000));
        EntryFile[] future = null;
        if (!tail.isEmpty()) {
            future = (EntryFile[]) tail.toArray(new EntryFile[tail.size()]);
            tail.clear();
        }
        if (!this.mAllFiles.contents.isEmpty()) {
            t = Math.max(t, this.mAllFiles.contents.last().timestampMillis + 1);
        }
        if (future != null) {
            EntryFile[] arr$ = future;
            for (EntryFile late : arr$) {
                this.mAllFiles.blocks -= late.blocks;
                FileList tagFiles = this.mFilesByTag.get(late.tag);
                if (tagFiles != null && tagFiles.contents.remove(late)) {
                    tagFiles.blocks -= late.blocks;
                }
                if ((late.flags & 1) == 0) {
                    long j = t;
                    t = j + 1;
                    new EntryFile(late.file, this.mDropBoxDir, late.tag, j, late.flags, this.mBlockSize);
                    enrollEntry(this);
                } else {
                    File file = this.mDropBoxDir;
                    String str = late.tag;
                    ?? r5 = t;
                    t = r5 + 1;
                    enrollEntry(new EntryFile(file, r5, r5));
                }
            }
        }
        if (temp == null) {
            enrollEntry(new EntryFile(this.mDropBoxDir, tag, t));
        } else {
            enrollEntry(new EntryFile(temp, this.mDropBoxDir, tag, t, flags, this.mBlockSize));
        }
        return t;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized long trimToFit() {
        int ageSeconds = Settings.Global.getInt(this.mContentResolver, Settings.Global.DROPBOX_AGE_SECONDS, DEFAULT_AGE_SECONDS);
        int maxFiles = Settings.Global.getInt(this.mContentResolver, Settings.Global.DROPBOX_MAX_FILES, 1000);
        long cutoffMillis = System.currentTimeMillis() - (ageSeconds * 1000);
        while (!this.mAllFiles.contents.isEmpty()) {
            EntryFile entry = this.mAllFiles.contents.first();
            if (entry.timestampMillis > cutoffMillis && this.mAllFiles.contents.size() < maxFiles) {
                break;
            }
            FileList tag = this.mFilesByTag.get(entry.tag);
            if (tag != null && tag.contents.remove(entry)) {
                tag.blocks -= entry.blocks;
            }
            if (this.mAllFiles.contents.remove(entry)) {
                this.mAllFiles.blocks -= entry.blocks;
            }
            if (entry.file != null) {
                entry.file.delete();
            }
        }
        long uptimeMillis = SystemClock.uptimeMillis();
        if (uptimeMillis > this.mCachedQuotaUptimeMillis + TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS) {
            int quotaPercent = Settings.Global.getInt(this.mContentResolver, Settings.Global.DROPBOX_QUOTA_PERCENT, 10);
            int reservePercent = Settings.Global.getInt(this.mContentResolver, Settings.Global.DROPBOX_RESERVE_PERCENT, 10);
            int quotaKb = Settings.Global.getInt(this.mContentResolver, Settings.Global.DROPBOX_QUOTA_KB, 5120);
            this.mStatFs.restat(this.mDropBoxDir.getPath());
            int available = this.mStatFs.getAvailableBlocks();
            int nonreserved = available - ((this.mStatFs.getBlockCount() * reservePercent) / 100);
            int maximum = (quotaKb * 1024) / this.mBlockSize;
            this.mCachedQuotaBlocks = Math.min(maximum, Math.max(0, (nonreserved * quotaPercent) / 100));
            this.mCachedQuotaUptimeMillis = uptimeMillis;
        }
        if (this.mAllFiles.blocks > this.mCachedQuotaBlocks) {
            int unsqueezed = this.mAllFiles.blocks;
            int squeezed = 0;
            TreeSet<FileList> tags = new TreeSet<>(this.mFilesByTag.values());
            Iterator i$ = tags.iterator();
            while (i$.hasNext()) {
                FileList tag2 = i$.next();
                if (squeezed > 0 && tag2.blocks <= (this.mCachedQuotaBlocks - unsqueezed) / squeezed) {
                    break;
                }
                unsqueezed -= tag2.blocks;
                squeezed++;
            }
            int tagQuota = (this.mCachedQuotaBlocks - unsqueezed) / squeezed;
            Iterator i$2 = tags.iterator();
            while (i$2.hasNext()) {
                FileList tag3 = i$2.next();
                if (this.mAllFiles.blocks < this.mCachedQuotaBlocks) {
                    break;
                }
                while (tag3.blocks > tagQuota && !tag3.contents.isEmpty()) {
                    EntryFile entry2 = tag3.contents.first();
                    if (tag3.contents.remove(entry2)) {
                        tag3.blocks -= entry2.blocks;
                    }
                    if (this.mAllFiles.contents.remove(entry2)) {
                        this.mAllFiles.blocks -= entry2.blocks;
                    }
                    try {
                        if (entry2.file != null) {
                            entry2.file.delete();
                        }
                        enrollEntry(new EntryFile(this.mDropBoxDir, entry2.tag, entry2.timestampMillis));
                    } catch (IOException e) {
                        Slog.e(TAG, "Can't write tombstone file", e);
                    }
                }
            }
        }
        return this.mCachedQuotaBlocks * this.mBlockSize;
    }
}