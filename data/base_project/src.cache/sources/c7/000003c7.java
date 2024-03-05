package android.content.res;

import android.os.ParcelFileDescriptor;
import android.util.TypedValue;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/* loaded from: AssetManager.class */
public final class AssetManager {
    public static final int ACCESS_UNKNOWN = 0;
    public static final int ACCESS_RANDOM = 1;
    public static final int ACCESS_STREAMING = 2;
    public static final int ACCESS_BUFFER = 3;
    private static final String TAG = "AssetManager";
    private static final boolean localLOGV = false;
    private static final boolean DEBUG_REFS = false;
    private static final Object sSync = new Object();
    static AssetManager sSystem = null;
    private final TypedValue mValue;
    private final long[] mOffsets;
    private int mObject;
    private int mNObject;
    private StringBlock[] mStringBlocks;
    private int mNumRefs;
    private boolean mOpen;
    private HashMap<Integer, RuntimeException> mRefStacks;
    static final int STYLE_NUM_ENTRIES = 6;
    static final int STYLE_TYPE = 0;
    static final int STYLE_DATA = 1;
    static final int STYLE_ASSET_COOKIE = 2;
    static final int STYLE_RESOURCE_ID = 3;
    static final int STYLE_CHANGING_CONFIGURATIONS = 4;
    static final int STYLE_DENSITY = 5;

    public final native String[] list(String str) throws IOException;

    private final native int addAssetPathNative(String str);

    public final native boolean isUpToDate();

    public final native void setLocale(String str);

    public final native String[] getLocales();

    public final native void setConfiguration(int i, int i2, String str, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12, int i13, int i14, int i15, int i16);

    /* JADX INFO: Access modifiers changed from: package-private */
    public final native int getResourceIdentifier(String str, String str2, String str3);

    /* JADX INFO: Access modifiers changed from: package-private */
    public final native String getResourceName(int i);

    /* JADX INFO: Access modifiers changed from: package-private */
    public final native String getResourcePackageName(int i);

    /* JADX INFO: Access modifiers changed from: package-private */
    public final native String getResourceTypeName(int i);

    /* JADX INFO: Access modifiers changed from: package-private */
    public final native String getResourceEntryName(int i);

    private final native int openAsset(String str, int i);

    private final native ParcelFileDescriptor openAssetFd(String str, long[] jArr) throws IOException;

    private final native int openNonAssetNative(int i, String str, int i2);

    private native ParcelFileDescriptor openNonAssetFdNative(int i, String str, long[] jArr) throws IOException;

    /* JADX INFO: Access modifiers changed from: private */
    public final native void destroyAsset(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public final native int readAssetChar(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public final native int readAsset(int i, byte[] bArr, int i2, int i3);

    /* JADX INFO: Access modifiers changed from: private */
    public final native long seekAsset(int i, long j, int i2);

    /* JADX INFO: Access modifiers changed from: private */
    public final native long getAssetLength(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public final native long getAssetRemainingLength(int i);

    private final native int loadResourceValue(int i, short s, TypedValue typedValue, boolean z);

    private final native int loadResourceBagValue(int i, int i2, TypedValue typedValue, boolean z);

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final native boolean applyStyle(int i, int i2, int i3, int i4, int[] iArr, int[] iArr2, int[] iArr3);

    /* JADX INFO: Access modifiers changed from: package-private */
    public final native boolean retrieveAttributes(int i, int[] iArr, int[] iArr2, int[] iArr3);

    /* JADX INFO: Access modifiers changed from: package-private */
    public final native int getArraySize(int i);

    /* JADX INFO: Access modifiers changed from: package-private */
    public final native int retrieveArray(int i, int[] iArr);

    private final native int getStringBlockCount();

    private final native int getNativeStringBlock(int i);

    public final native String getCookieName(int i);

    public static final native int getGlobalAssetCount();

    public static final native String getAssetAllocations();

    public static final native int getGlobalAssetManagerCount();

    private final native int newTheme();

    private final native void deleteTheme(int i);

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final native void applyThemeStyle(int i, int i2, boolean z);

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final native void copyTheme(int i, int i2);

    static final native int loadThemeAttributeValue(int i, int i2, TypedValue typedValue, boolean z);

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final native void dumpTheme(int i, int i2, String str, String str2);

    private final native int openXmlAssetNative(int i, String str);

    private final native String[] getArrayStringResource(int i);

    private final native int[] getArrayStringInfo(int i);

    /* JADX INFO: Access modifiers changed from: package-private */
    public final native int[] getArrayIntResource(int i);

    private final native void init();

    private final native void destroy();

    public AssetManager() {
        this.mValue = new TypedValue();
        this.mOffsets = new long[2];
        this.mStringBlocks = null;
        this.mNumRefs = 1;
        this.mOpen = true;
        synchronized (this) {
            init();
            ensureSystemAssets();
        }
    }

    private static void ensureSystemAssets() {
        synchronized (sSync) {
            if (sSystem == null) {
                AssetManager system = new AssetManager(true);
                system.makeStringBlocks(false);
                sSystem = system;
            }
        }
    }

    private AssetManager(boolean isSystem) {
        this.mValue = new TypedValue();
        this.mOffsets = new long[2];
        this.mStringBlocks = null;
        this.mNumRefs = 1;
        this.mOpen = true;
        init();
    }

    public static AssetManager getSystem() {
        ensureSystemAssets();
        return sSystem;
    }

    public void close() {
        synchronized (this) {
            if (this.mOpen) {
                this.mOpen = false;
                decRefsLocked(hashCode());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final CharSequence getResourceText(int ident) {
        synchronized (this) {
            TypedValue tmpValue = this.mValue;
            int block = loadResourceValue(ident, (short) 0, tmpValue, true);
            if (block >= 0) {
                if (tmpValue.type == 3) {
                    return this.mStringBlocks[block].get(tmpValue.data);
                }
                return tmpValue.coerceToString();
            }
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final CharSequence getResourceBagText(int ident, int bagEntryId) {
        synchronized (this) {
            TypedValue tmpValue = this.mValue;
            int block = loadResourceBagValue(ident, bagEntryId, tmpValue, true);
            if (block >= 0) {
                if (tmpValue.type == 3) {
                    return this.mStringBlocks[block].get(tmpValue.data);
                }
                return tmpValue.coerceToString();
            }
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final String[] getResourceStringArray(int id) {
        String[] retArray = getArrayStringResource(id);
        return retArray;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean getResourceValue(int ident, int density, TypedValue outValue, boolean resolveRefs) {
        int block = loadResourceValue(ident, (short) density, outValue, resolveRefs);
        if (block >= 0) {
            if (outValue.type != 3) {
                return true;
            }
            outValue.string = this.mStringBlocks[block].get(outValue.data);
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final CharSequence[] getResourceTextArray(int id) {
        int[] rawInfoArray = getArrayStringInfo(id);
        int rawInfoArrayLen = rawInfoArray.length;
        int infoArrayLen = rawInfoArrayLen / 2;
        CharSequence[] retArray = new CharSequence[infoArrayLen];
        int i = 0;
        int j = 0;
        while (i < rawInfoArrayLen) {
            int block = rawInfoArray[i];
            int index = rawInfoArray[i + 1];
            retArray[j] = index >= 0 ? this.mStringBlocks[block].get(index) : null;
            i += 2;
            j++;
        }
        return retArray;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean getThemeValue(int theme, int ident, TypedValue outValue, boolean resolveRefs) {
        int block = loadThemeAttributeValue(theme, ident, outValue, resolveRefs);
        if (block >= 0) {
            if (outValue.type != 3) {
                return true;
            }
            StringBlock[] blocks = this.mStringBlocks;
            if (blocks == null) {
                ensureStringBlocks();
                blocks = this.mStringBlocks;
            }
            outValue.string = blocks[block].get(outValue.data);
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void ensureStringBlocks() {
        if (this.mStringBlocks == null) {
            synchronized (this) {
                if (this.mStringBlocks == null) {
                    makeStringBlocks(true);
                }
            }
        }
    }

    final void makeStringBlocks(boolean copyFromSystem) {
        int sysNum = copyFromSystem ? sSystem.mStringBlocks.length : 0;
        int num = getStringBlockCount();
        this.mStringBlocks = new StringBlock[num];
        for (int i = 0; i < num; i++) {
            if (i < sysNum) {
                this.mStringBlocks[i] = sSystem.mStringBlocks[i];
            } else {
                this.mStringBlocks[i] = new StringBlock(getNativeStringBlock(i), true);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final CharSequence getPooledString(int block, int id) {
        return this.mStringBlocks[block - 1].get(id);
    }

    public final InputStream open(String fileName) throws IOException {
        return open(fileName, 2);
    }

    public final InputStream open(String fileName, int accessMode) throws IOException {
        synchronized (this) {
            if (!this.mOpen) {
                throw new RuntimeException("Assetmanager has been closed");
            }
            int asset = openAsset(fileName, accessMode);
            if (asset != 0) {
                AssetInputStream res = new AssetInputStream(asset);
                incRefsLocked(res.hashCode());
                return res;
            }
            throw new FileNotFoundException("Asset file: " + fileName);
        }
    }

    public final AssetFileDescriptor openFd(String fileName) throws IOException {
        synchronized (this) {
            if (!this.mOpen) {
                throw new RuntimeException("Assetmanager has been closed");
            }
            ParcelFileDescriptor pfd = openAssetFd(fileName, this.mOffsets);
            if (pfd != null) {
                return new AssetFileDescriptor(pfd, this.mOffsets[0], this.mOffsets[1]);
            }
            throw new FileNotFoundException("Asset file: " + fileName);
        }
    }

    public final InputStream openNonAsset(String fileName) throws IOException {
        return openNonAsset(0, fileName, 2);
    }

    public final InputStream openNonAsset(String fileName, int accessMode) throws IOException {
        return openNonAsset(0, fileName, accessMode);
    }

    public final InputStream openNonAsset(int cookie, String fileName) throws IOException {
        return openNonAsset(cookie, fileName, 2);
    }

    public final InputStream openNonAsset(int cookie, String fileName, int accessMode) throws IOException {
        synchronized (this) {
            if (!this.mOpen) {
                throw new RuntimeException("Assetmanager has been closed");
            }
            int asset = openNonAssetNative(cookie, fileName, accessMode);
            if (asset != 0) {
                AssetInputStream res = new AssetInputStream(asset);
                incRefsLocked(res.hashCode());
                return res;
            }
            throw new FileNotFoundException("Asset absolute file: " + fileName);
        }
    }

    public final AssetFileDescriptor openNonAssetFd(String fileName) throws IOException {
        return openNonAssetFd(0, fileName);
    }

    public final AssetFileDescriptor openNonAssetFd(int cookie, String fileName) throws IOException {
        synchronized (this) {
            if (!this.mOpen) {
                throw new RuntimeException("Assetmanager has been closed");
            }
            ParcelFileDescriptor pfd = openNonAssetFdNative(cookie, fileName, this.mOffsets);
            if (pfd != null) {
                return new AssetFileDescriptor(pfd, this.mOffsets[0], this.mOffsets[1]);
            }
            throw new FileNotFoundException("Asset absolute file: " + fileName);
        }
    }

    public final XmlResourceParser openXmlResourceParser(String fileName) throws IOException {
        return openXmlResourceParser(0, fileName);
    }

    public final XmlResourceParser openXmlResourceParser(int cookie, String fileName) throws IOException {
        XmlBlock block = openXmlBlockAsset(cookie, fileName);
        XmlResourceParser rp = block.newParser();
        block.close();
        return rp;
    }

    final XmlBlock openXmlBlockAsset(String fileName) throws IOException {
        return openXmlBlockAsset(0, fileName);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final XmlBlock openXmlBlockAsset(int cookie, String fileName) throws IOException {
        synchronized (this) {
            if (!this.mOpen) {
                throw new RuntimeException("Assetmanager has been closed");
            }
            int xmlBlock = openXmlAssetNative(cookie, fileName);
            if (xmlBlock != 0) {
                XmlBlock res = new XmlBlock(this, xmlBlock);
                incRefsLocked(res.hashCode());
                return res;
            }
            throw new FileNotFoundException("Asset XML file: " + fileName);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void xmlBlockGone(int id) {
        synchronized (this) {
            decRefsLocked(id);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int createTheme() {
        int res;
        synchronized (this) {
            if (!this.mOpen) {
                throw new RuntimeException("Assetmanager has been closed");
            }
            res = newTheme();
            incRefsLocked(res);
        }
        return res;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void releaseTheme(int theme) {
        synchronized (this) {
            deleteTheme(theme);
            decRefsLocked(theme);
        }
    }

    protected void finalize() throws Throwable {
        try {
            destroy();
            super.finalize();
        } catch (Throwable th) {
            super.finalize();
            throw th;
        }
    }

    /* loaded from: AssetManager$AssetInputStream.class */
    public final class AssetInputStream extends InputStream {
        private int mAsset;
        private long mLength;
        private long mMarkPos;

        public final int getAssetInt() {
            return this.mAsset;
        }

        private AssetInputStream(int asset) {
            this.mAsset = asset;
            this.mLength = AssetManager.this.getAssetLength(asset);
        }

        @Override // java.io.InputStream
        public final int read() throws IOException {
            return AssetManager.this.readAssetChar(this.mAsset);
        }

        @Override // java.io.InputStream
        public final boolean markSupported() {
            return true;
        }

        @Override // java.io.InputStream
        public final int available() throws IOException {
            long len = AssetManager.this.getAssetRemainingLength(this.mAsset);
            if (len > 2147483647L) {
                return Integer.MAX_VALUE;
            }
            return (int) len;
        }

        @Override // java.io.InputStream, java.io.Closeable
        public final void close() throws IOException {
            synchronized (AssetManager.this) {
                if (this.mAsset != 0) {
                    AssetManager.this.destroyAsset(this.mAsset);
                    this.mAsset = 0;
                    AssetManager.this.decRefsLocked(hashCode());
                }
            }
        }

        @Override // java.io.InputStream
        public final void mark(int readlimit) {
            this.mMarkPos = AssetManager.this.seekAsset(this.mAsset, 0L, 0);
        }

        @Override // java.io.InputStream
        public final void reset() throws IOException {
            AssetManager.this.seekAsset(this.mAsset, this.mMarkPos, -1);
        }

        @Override // java.io.InputStream
        public final int read(byte[] b) throws IOException {
            return AssetManager.this.readAsset(this.mAsset, b, 0, b.length);
        }

        @Override // java.io.InputStream
        public final int read(byte[] b, int off, int len) throws IOException {
            return AssetManager.this.readAsset(this.mAsset, b, off, len);
        }

        @Override // java.io.InputStream
        public final long skip(long n) throws IOException {
            long pos = AssetManager.this.seekAsset(this.mAsset, 0L, 0);
            if (pos + n > this.mLength) {
                n = this.mLength - pos;
            }
            if (n > 0) {
                AssetManager.this.seekAsset(this.mAsset, n, 0);
            }
            return n;
        }

        protected void finalize() throws Throwable {
            close();
        }
    }

    public final int addAssetPath(String path) {
        int res = addAssetPathNative(path);
        return res;
    }

    public final int[] addAssetPaths(String[] paths) {
        if (paths == null) {
            return null;
        }
        int[] cookies = new int[paths.length];
        for (int i = 0; i < paths.length; i++) {
            cookies[i] = addAssetPath(paths[i]);
        }
        return cookies;
    }

    private final void incRefsLocked(int id) {
        this.mNumRefs++;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void decRefsLocked(int id) {
        this.mNumRefs--;
        if (this.mNumRefs == 0) {
            destroy();
        }
    }
}