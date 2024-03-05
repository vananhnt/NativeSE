package android.app;

import android.content.SharedPreferences;
import android.os.FileUtils;
import android.os.Looper;
import android.util.Log;
import com.android.internal.util.XmlUtils;
import com.google.android.collect.Maps;
import dalvik.system.BlockGuard;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.CountDownLatch;
import libcore.io.ErrnoException;
import libcore.io.IoUtils;
import libcore.io.Libcore;
import libcore.io.StructStat;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: SharedPreferencesImpl.class */
public final class SharedPreferencesImpl implements SharedPreferences {
    private static final String TAG = "SharedPreferencesImpl";
    private static final boolean DEBUG = false;
    private final File mFile;
    private final File mBackupFile;
    private final int mMode;
    private boolean mLoaded;
    private long mStatTimestamp;
    private long mStatSize;
    private static final Object mContent = new Object();
    private int mDiskWritesInFlight = 0;
    private final Object mWritingToDiskLock = new Object();
    private final WeakHashMap<SharedPreferences.OnSharedPreferenceChangeListener, Object> mListeners = new WeakHashMap<>();
    private Map<String, Object> mMap = null;

    static /* synthetic */ int access$308(SharedPreferencesImpl x0) {
        int i = x0.mDiskWritesInFlight;
        x0.mDiskWritesInFlight = i + 1;
        return i;
    }

    static /* synthetic */ int access$310(SharedPreferencesImpl x0) {
        int i = x0.mDiskWritesInFlight;
        x0.mDiskWritesInFlight = i - 1;
        return i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SharedPreferencesImpl(File file, int mode) {
        this.mLoaded = false;
        this.mFile = file;
        this.mBackupFile = makeBackupFile(file);
        this.mMode = mode;
        this.mLoaded = false;
        startLoadFromDisk();
    }

    private void startLoadFromDisk() {
        synchronized (this) {
            this.mLoaded = false;
        }
        new Thread("SharedPreferencesImpl-load") { // from class: android.app.SharedPreferencesImpl.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                synchronized (SharedPreferencesImpl.this) {
                    SharedPreferencesImpl.this.loadFromDiskLocked();
                }
            }
        }.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r9v1 */
    /* JADX WARN: Type inference failed for: r9v2 */
    /* JADX WARN: Type inference failed for: r9v3 */
    /* JADX WARN: Type inference failed for: r9v4 */
    /* JADX WARN: Type inference failed for: r9v5, types: [java.lang.AutoCloseable, java.io.InputStream] */
    public void loadFromDiskLocked() {
        if (this.mLoaded) {
            return;
        }
        if (this.mBackupFile.exists()) {
            this.mFile.delete();
            this.mBackupFile.renameTo(this.mFile);
        }
        if (this.mFile.exists() && !this.mFile.canRead()) {
            Log.w(TAG, "Attempt to read preferences file " + this.mFile + " without permission");
        }
        Map map = null;
        StructStat stat = null;
        try {
            stat = Libcore.os.stat(this.mFile.getPath());
            if (this.mFile.canRead()) {
                ?? r9 = 0;
                try {
                    r9 = new BufferedInputStream(new FileInputStream(this.mFile), 16384);
                    map = XmlUtils.readMapXml(r9);
                    IoUtils.closeQuietly((AutoCloseable) r9);
                } catch (FileNotFoundException e) {
                    Log.w(TAG, "getSharedPreferences", e);
                    IoUtils.closeQuietly((AutoCloseable) r9);
                } catch (IOException e2) {
                    Log.w(TAG, "getSharedPreferences", e2);
                    IoUtils.closeQuietly((AutoCloseable) r9);
                } catch (XmlPullParserException e3) {
                    Log.w(TAG, "getSharedPreferences", e3);
                    IoUtils.closeQuietly((AutoCloseable) r9);
                }
            }
        } catch (ErrnoException e4) {
        }
        this.mLoaded = true;
        if (map != null) {
            this.mMap = map;
            this.mStatTimestamp = stat.st_mtime;
            this.mStatSize = stat.st_size;
        } else {
            this.mMap = new HashMap();
        }
        notifyAll();
    }

    private static File makeBackupFile(File prefsFile) {
        return new File(prefsFile.getPath() + ".bak");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void startReloadIfChangedUnexpectedly() {
        synchronized (this) {
            if (hasFileChangedUnexpectedly()) {
                startLoadFromDisk();
            }
        }
    }

    private boolean hasFileChangedUnexpectedly() {
        boolean z;
        synchronized (this) {
            if (this.mDiskWritesInFlight > 0) {
                return false;
            }
            try {
                BlockGuard.getThreadPolicy().onReadFromDisk();
                StructStat stat = Libcore.os.stat(this.mFile.getPath());
                synchronized (this) {
                    z = (this.mStatTimestamp == stat.st_mtime && this.mStatSize == stat.st_size) ? false : true;
                }
                return z;
            } catch (ErrnoException e) {
                return true;
            }
        }
    }

    @Override // android.content.SharedPreferences
    public void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        synchronized (this) {
            this.mListeners.put(listener, mContent);
        }
    }

    @Override // android.content.SharedPreferences
    public void unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        synchronized (this) {
            this.mListeners.remove(listener);
        }
    }

    private void awaitLoadedLocked() {
        if (!this.mLoaded) {
            BlockGuard.getThreadPolicy().onReadFromDisk();
        }
        while (!this.mLoaded) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
    }

    @Override // android.content.SharedPreferences
    public Map<String, ?> getAll() {
        HashMap hashMap;
        synchronized (this) {
            awaitLoadedLocked();
            hashMap = new HashMap(this.mMap);
        }
        return hashMap;
    }

    @Override // android.content.SharedPreferences
    public String getString(String key, String defValue) {
        String str;
        synchronized (this) {
            awaitLoadedLocked();
            String v = (String) this.mMap.get(key);
            str = v != null ? v : defValue;
        }
        return str;
    }

    @Override // android.content.SharedPreferences
    public Set<String> getStringSet(String key, Set<String> defValues) {
        Set<String> set;
        synchronized (this) {
            awaitLoadedLocked();
            Set<String> v = (Set) this.mMap.get(key);
            set = v != null ? v : defValues;
        }
        return set;
    }

    @Override // android.content.SharedPreferences
    public int getInt(String key, int defValue) {
        int intValue;
        synchronized (this) {
            awaitLoadedLocked();
            Integer v = (Integer) this.mMap.get(key);
            intValue = v != null ? v.intValue() : defValue;
        }
        return intValue;
    }

    @Override // android.content.SharedPreferences
    public long getLong(String key, long defValue) {
        long longValue;
        synchronized (this) {
            awaitLoadedLocked();
            Long v = (Long) this.mMap.get(key);
            longValue = v != null ? v.longValue() : defValue;
        }
        return longValue;
    }

    @Override // android.content.SharedPreferences
    public float getFloat(String key, float defValue) {
        float floatValue;
        synchronized (this) {
            awaitLoadedLocked();
            Float v = (Float) this.mMap.get(key);
            floatValue = v != null ? v.floatValue() : defValue;
        }
        return floatValue;
    }

    @Override // android.content.SharedPreferences
    public boolean getBoolean(String key, boolean defValue) {
        boolean booleanValue;
        synchronized (this) {
            awaitLoadedLocked();
            Boolean v = (Boolean) this.mMap.get(key);
            booleanValue = v != null ? v.booleanValue() : defValue;
        }
        return booleanValue;
    }

    @Override // android.content.SharedPreferences
    public boolean contains(String key) {
        boolean containsKey;
        synchronized (this) {
            awaitLoadedLocked();
            containsKey = this.mMap.containsKey(key);
        }
        return containsKey;
    }

    @Override // android.content.SharedPreferences
    public SharedPreferences.Editor edit() {
        synchronized (this) {
            awaitLoadedLocked();
        }
        return new EditorImpl();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: SharedPreferencesImpl$MemoryCommitResult.class */
    public static class MemoryCommitResult {
        public boolean changesMade;
        public List<String> keysModified;
        public Set<SharedPreferences.OnSharedPreferenceChangeListener> listeners;
        public Map<?, ?> mapToWriteToDisk;
        public final CountDownLatch writtenToDiskLatch;
        public volatile boolean writeToDiskResult;

        private MemoryCommitResult() {
            this.writtenToDiskLatch = new CountDownLatch(1);
            this.writeToDiskResult = false;
        }

        public void setDiskWriteResult(boolean result) {
            this.writeToDiskResult = result;
            this.writtenToDiskLatch.countDown();
        }
    }

    /* loaded from: SharedPreferencesImpl$EditorImpl.class */
    public final class EditorImpl implements SharedPreferences.Editor {
        private final Map<String, Object> mModified = Maps.newHashMap();
        private boolean mClear = false;

        public EditorImpl() {
        }

        @Override // android.content.SharedPreferences.Editor
        public SharedPreferences.Editor putString(String key, String value) {
            synchronized (this) {
                this.mModified.put(key, value);
            }
            return this;
        }

        @Override // android.content.SharedPreferences.Editor
        public SharedPreferences.Editor putStringSet(String key, Set<String> values) {
            synchronized (this) {
                this.mModified.put(key, values == null ? null : new HashSet(values));
            }
            return this;
        }

        @Override // android.content.SharedPreferences.Editor
        public SharedPreferences.Editor putInt(String key, int value) {
            synchronized (this) {
                this.mModified.put(key, Integer.valueOf(value));
            }
            return this;
        }

        @Override // android.content.SharedPreferences.Editor
        public SharedPreferences.Editor putLong(String key, long value) {
            synchronized (this) {
                this.mModified.put(key, Long.valueOf(value));
            }
            return this;
        }

        @Override // android.content.SharedPreferences.Editor
        public SharedPreferences.Editor putFloat(String key, float value) {
            synchronized (this) {
                this.mModified.put(key, Float.valueOf(value));
            }
            return this;
        }

        @Override // android.content.SharedPreferences.Editor
        public SharedPreferences.Editor putBoolean(String key, boolean value) {
            synchronized (this) {
                this.mModified.put(key, Boolean.valueOf(value));
            }
            return this;
        }

        @Override // android.content.SharedPreferences.Editor
        public SharedPreferences.Editor remove(String key) {
            synchronized (this) {
                this.mModified.put(key, this);
            }
            return this;
        }

        @Override // android.content.SharedPreferences.Editor
        public SharedPreferences.Editor clear() {
            synchronized (this) {
                this.mClear = true;
            }
            return this;
        }

        @Override // android.content.SharedPreferences.Editor
        public void apply() {
            final MemoryCommitResult mcr = commitToMemory();
            final Runnable awaitCommit = new Runnable() { // from class: android.app.SharedPreferencesImpl.EditorImpl.1
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        mcr.writtenToDiskLatch.await();
                    } catch (InterruptedException e) {
                    }
                }
            };
            QueuedWork.add(awaitCommit);
            Runnable postWriteRunnable = new Runnable() { // from class: android.app.SharedPreferencesImpl.EditorImpl.2
                @Override // java.lang.Runnable
                public void run() {
                    awaitCommit.run();
                    QueuedWork.remove(awaitCommit);
                }
            };
            SharedPreferencesImpl.this.enqueueDiskWrite(mcr, postWriteRunnable);
            notifyListeners(mcr);
        }

        /* JADX WARN: Removed duplicated region for block: B:41:0x0162 A[Catch: all -> 0x0180, all -> 0x018d, TryCatch #1 {, blocks: (B:4:0x0010, B:6:0x001a, B:7:0x0030, B:13:0x005a, B:15:0x007e, B:16:0x007f, B:18:0x0086, B:20:0x0095, B:21:0x00a6, B:22:0x00ab, B:23:0x00bb, B:25:0x00c5, B:27:0x00ec, B:30:0x0100, B:39:0x0159, B:41:0x0162, B:31:0x0112, B:33:0x0126, B:35:0x013b, B:38:0x0148, B:43:0x0171, B:44:0x017c, B:52:0x0189), top: B:61:0x0010 }] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        private android.app.SharedPreferencesImpl.MemoryCommitResult commitToMemory() {
            /*
                Method dump skipped, instructions count: 406
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: android.app.SharedPreferencesImpl.EditorImpl.commitToMemory():android.app.SharedPreferencesImpl$MemoryCommitResult");
        }

        @Override // android.content.SharedPreferences.Editor
        public boolean commit() {
            MemoryCommitResult mcr = commitToMemory();
            SharedPreferencesImpl.this.enqueueDiskWrite(mcr, null);
            try {
                mcr.writtenToDiskLatch.await();
                notifyListeners(mcr);
                return mcr.writeToDiskResult;
            } catch (InterruptedException e) {
                return false;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void notifyListeners(final MemoryCommitResult mcr) {
            if (mcr.listeners == null || mcr.keysModified == null || mcr.keysModified.size() == 0) {
                return;
            }
            if (Looper.myLooper() == Looper.getMainLooper()) {
                for (int i = mcr.keysModified.size() - 1; i >= 0; i--) {
                    String key = mcr.keysModified.get(i);
                    for (SharedPreferences.OnSharedPreferenceChangeListener listener : mcr.listeners) {
                        if (listener != null) {
                            listener.onSharedPreferenceChanged(SharedPreferencesImpl.this, key);
                        }
                    }
                }
                return;
            }
            ActivityThread.sMainThreadHandler.post(new Runnable() { // from class: android.app.SharedPreferencesImpl.EditorImpl.3
                @Override // java.lang.Runnable
                public void run() {
                    EditorImpl.this.notifyListeners(mcr);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enqueueDiskWrite(final MemoryCommitResult mcr, final Runnable postWriteRunnable) {
        boolean wasEmpty;
        Runnable writeToDiskRunnable = new Runnable() { // from class: android.app.SharedPreferencesImpl.2
            @Override // java.lang.Runnable
            public void run() {
                synchronized (SharedPreferencesImpl.this.mWritingToDiskLock) {
                    SharedPreferencesImpl.this.writeToFile(mcr);
                }
                synchronized (SharedPreferencesImpl.this) {
                    SharedPreferencesImpl.access$310(SharedPreferencesImpl.this);
                }
                if (postWriteRunnable != null) {
                    postWriteRunnable.run();
                }
            }
        };
        boolean isFromSyncCommit = postWriteRunnable == null;
        if (isFromSyncCommit) {
            synchronized (this) {
                wasEmpty = this.mDiskWritesInFlight == 1;
            }
            if (wasEmpty) {
                writeToDiskRunnable.run();
                return;
            }
        }
        QueuedWork.singleThreadExecutor().execute(writeToDiskRunnable);
    }

    private static FileOutputStream createFileOutputStream(File file) {
        FileOutputStream str = null;
        try {
            str = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            File parent = file.getParentFile();
            if (!parent.mkdir()) {
                Log.e(TAG, "Couldn't create directory for SharedPreferences file " + file);
                return null;
            }
            FileUtils.setPermissions(parent.getPath(), 505, -1, -1);
            try {
                str = new FileOutputStream(file);
            } catch (FileNotFoundException e2) {
                Log.e(TAG, "Couldn't create SharedPreferences file " + file, e2);
            }
        }
        return str;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void writeToFile(MemoryCommitResult mcr) {
        if (this.mFile.exists()) {
            if (!mcr.changesMade) {
                mcr.setDiskWriteResult(true);
                return;
            } else if (!this.mBackupFile.exists()) {
                if (!this.mFile.renameTo(this.mBackupFile)) {
                    Log.e(TAG, "Couldn't rename file " + this.mFile + " to backup file " + this.mBackupFile);
                    mcr.setDiskWriteResult(false);
                    return;
                }
            } else {
                this.mFile.delete();
            }
        }
        try {
            FileOutputStream str = createFileOutputStream(this.mFile);
            if (str == null) {
                mcr.setDiskWriteResult(false);
                return;
            }
            XmlUtils.writeMapXml(mcr.mapToWriteToDisk, str);
            FileUtils.sync(str);
            str.close();
            ContextImpl.setFilePermissionsFromMode(this.mFile.getPath(), this.mMode, 0);
            try {
                StructStat stat = Libcore.os.stat(this.mFile.getPath());
                synchronized (this) {
                    this.mStatTimestamp = stat.st_mtime;
                    this.mStatSize = stat.st_size;
                }
            } catch (ErrnoException e) {
            }
            this.mBackupFile.delete();
            mcr.setDiskWriteResult(true);
        } catch (IOException e2) {
            Log.w(TAG, "writeToFile: Got exception:", e2);
            if (this.mFile.exists() && !this.mFile.delete()) {
                Log.e(TAG, "Couldn't clean up partially-written file " + this.mFile);
            }
            mcr.setDiskWriteResult(false);
        } catch (XmlPullParserException e3) {
            Log.w(TAG, "writeToFile: Got exception:", e3);
            if (this.mFile.exists()) {
                Log.e(TAG, "Couldn't clean up partially-written file " + this.mFile);
            }
            mcr.setDiskWriteResult(false);
        }
    }
}