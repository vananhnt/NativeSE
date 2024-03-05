package android.emoji;

import android.graphics.Bitmap;
import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.Map;

/* loaded from: EmojiFactory.class */
public final class EmojiFactory {
    private int mNativeEmojiFactory;
    private String mName;
    private int sCacheSize = 100;
    private Map<Integer, WeakReference<Bitmap>> mCache = new CustomLinkedHashMap();

    public static native EmojiFactory newInstance(String str);

    public static native EmojiFactory newAvailableInstance();

    private native void nativeDestructor(int i);

    private native Bitmap nativeGetBitmapFromAndroidPua(int i, int i2);

    private native int nativeGetAndroidPuaFromVendorSpecificSjis(int i, char c);

    private native int nativeGetVendorSpecificSjisFromAndroidPua(int i, int i2);

    private native int nativeGetAndroidPuaFromVendorSpecificPua(int i, int i2);

    private native int nativeGetVendorSpecificPuaFromAndroidPua(int i, int i2);

    private native int nativeGetMaximumVendorSpecificPua(int i);

    private native int nativeGetMinimumVendorSpecificPua(int i);

    private native int nativeGetMaximumAndroidPua(int i);

    private native int nativeGetMinimumAndroidPua(int i);

    /* loaded from: EmojiFactory$CustomLinkedHashMap.class */
    private class CustomLinkedHashMap<K, V> extends LinkedHashMap<K, V> {
        public CustomLinkedHashMap() {
            super(16, 0.75f, true);
        }

        @Override // java.util.LinkedHashMap
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > EmojiFactory.this.sCacheSize;
        }
    }

    private EmojiFactory(int nativeEmojiFactory, String name) {
        this.mNativeEmojiFactory = nativeEmojiFactory;
        this.mName = name;
    }

    protected void finalize() throws Throwable {
        try {
            nativeDestructor(this.mNativeEmojiFactory);
            super.finalize();
        } catch (Throwable th) {
            super.finalize();
            throw th;
        }
    }

    public String name() {
        return this.mName;
    }

    public synchronized Bitmap getBitmapFromAndroidPua(int pua) {
        WeakReference<Bitmap> cache = this.mCache.get(Integer.valueOf(pua));
        if (cache == null) {
            Bitmap ret = nativeGetBitmapFromAndroidPua(this.mNativeEmojiFactory, pua);
            if (ret != null) {
                this.mCache.put(Integer.valueOf(pua), new WeakReference<>(ret));
            }
            return ret;
        }
        Bitmap tmp = cache.get();
        if (tmp == null) {
            Bitmap ret2 = nativeGetBitmapFromAndroidPua(this.mNativeEmojiFactory, pua);
            this.mCache.put(Integer.valueOf(pua), new WeakReference<>(ret2));
            return ret2;
        }
        return tmp;
    }

    public synchronized Bitmap getBitmapFromVendorSpecificSjis(char sjis) {
        return getBitmapFromAndroidPua(getAndroidPuaFromVendorSpecificSjis(sjis));
    }

    public synchronized Bitmap getBitmapFromVendorSpecificPua(int vsp) {
        return getBitmapFromAndroidPua(getAndroidPuaFromVendorSpecificPua(vsp));
    }

    public int getAndroidPuaFromVendorSpecificSjis(char sjis) {
        return nativeGetAndroidPuaFromVendorSpecificSjis(this.mNativeEmojiFactory, sjis);
    }

    public int getVendorSpecificSjisFromAndroidPua(int pua) {
        return nativeGetVendorSpecificSjisFromAndroidPua(this.mNativeEmojiFactory, pua);
    }

    public int getAndroidPuaFromVendorSpecificPua(int vsp) {
        return nativeGetAndroidPuaFromVendorSpecificPua(this.mNativeEmojiFactory, vsp);
    }

    public String getAndroidPuaFromVendorSpecificPua(String vspString) {
        int newCodePoint;
        if (vspString == null) {
            return null;
        }
        int minVsp = nativeGetMinimumVendorSpecificPua(this.mNativeEmojiFactory);
        int maxVsp = nativeGetMaximumVendorSpecificPua(this.mNativeEmojiFactory);
        int len = vspString.length();
        int[] codePoints = new int[vspString.codePointCount(0, len)];
        int new_len = 0;
        int i = 0;
        while (i < len) {
            int codePoint = vspString.codePointAt(i);
            if (minVsp <= codePoint && codePoint <= maxVsp && (newCodePoint = getAndroidPuaFromVendorSpecificPua(codePoint)) > 0) {
                codePoints[new_len] = newCodePoint;
            } else {
                codePoints[new_len] = codePoint;
            }
            i = vspString.offsetByCodePoints(i, 1);
            new_len++;
        }
        return new String(codePoints, 0, new_len);
    }

    public int getVendorSpecificPuaFromAndroidPua(int pua) {
        return nativeGetVendorSpecificPuaFromAndroidPua(this.mNativeEmojiFactory, pua);
    }

    public String getVendorSpecificPuaFromAndroidPua(String puaString) {
        int newCodePoint;
        if (puaString == null) {
            return null;
        }
        int minVsp = nativeGetMinimumAndroidPua(this.mNativeEmojiFactory);
        int maxVsp = nativeGetMaximumAndroidPua(this.mNativeEmojiFactory);
        int len = puaString.length();
        int[] codePoints = new int[puaString.codePointCount(0, len)];
        int new_len = 0;
        int i = 0;
        while (i < len) {
            int codePoint = puaString.codePointAt(i);
            if (minVsp <= codePoint && codePoint <= maxVsp && (newCodePoint = getVendorSpecificPuaFromAndroidPua(codePoint)) > 0) {
                codePoints[new_len] = newCodePoint;
            } else {
                codePoints[new_len] = codePoint;
            }
            i = puaString.offsetByCodePoints(i, 1);
            new_len++;
        }
        return new String(codePoints, 0, new_len);
    }

    public int getMinimumAndroidPua() {
        return nativeGetMinimumAndroidPua(this.mNativeEmojiFactory);
    }

    public int getMaximumAndroidPua() {
        return nativeGetMaximumAndroidPua(this.mNativeEmojiFactory);
    }
}