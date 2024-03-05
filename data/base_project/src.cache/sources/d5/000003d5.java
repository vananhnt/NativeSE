package android.content.res;

import android.content.pm.ActivityInfo;
import android.content.res.XmlBlock;
import android.graphics.Movie;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.nfc.cardemulation.CardEmulation;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Trace;
import android.provider.CalendarContract;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.TypedValue;
import com.android.internal.R;
import com.android.internal.util.XmlUtils;
import gov.nist.core.Separators;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Locale;
import libcore.icu.NativePluralRules;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: Resources.class */
public class Resources {
    static final String TAG = "Resources";
    private static final boolean DEBUG_LOAD = false;
    private static final boolean DEBUG_CONFIG = false;
    private static final boolean DEBUG_ATTRIBUTES_CACHE = false;
    private static final boolean TRACE_FOR_PRELOAD = false;
    private static final boolean TRACE_FOR_MISS_PRELOAD = false;
    private static final int ID_OTHER = 16777220;
    private static boolean sPreloaded;
    private static int sPreloadedDensity;
    final Object mAccessLock;
    final Configuration mTmpConfig;
    TypedValue mTmpValue;
    final LongSparseArray<WeakReference<Drawable.ConstantState>> mDrawableCache;
    final LongSparseArray<WeakReference<ColorStateList>> mColorStateListCache;
    final LongSparseArray<WeakReference<Drawable.ConstantState>> mColorDrawableCache;
    boolean mPreloading;
    TypedArray mCachedStyledAttributes;
    RuntimeException mLastRetrievedAttrs;
    private int mLastCachedXmlBlockIndex;
    private final int[] mCachedXmlBlockIds;
    private final XmlBlock[] mCachedXmlBlocks;
    final AssetManager mAssets;
    private final Configuration mConfiguration;
    final DisplayMetrics mMetrics;
    private NativePluralRules mPluralRule;
    private CompatibilityInfo mCompatibilityInfo;
    private WeakReference<IBinder> mToken;
    private static final int LAYOUT_DIR_CONFIG;
    private static final Object sSync = new Object();
    static Resources mSystem = null;
    private static final LongSparseArray<Drawable.ConstantState> sPreloadedColorDrawables = new LongSparseArray<>();
    private static final LongSparseArray<ColorStateList> sPreloadedColorStateLists = new LongSparseArray<>();
    private static final LongSparseArray<Drawable.ConstantState>[] sPreloadedDrawables = new LongSparseArray[2];

    static {
        sPreloadedDrawables[0] = new LongSparseArray<>();
        sPreloadedDrawables[1] = new LongSparseArray<>();
        LAYOUT_DIR_CONFIG = ActivityInfo.activityInfoConfigToNative(8192);
    }

    public static int selectDefaultTheme(int curTheme, int targetSdkVersion) {
        return selectSystemTheme(curTheme, targetSdkVersion, 16973829, 16973931, 16974120);
    }

    public static int selectSystemTheme(int curTheme, int targetSdkVersion, int orig, int holo, int deviceDefault) {
        if (curTheme != 0) {
            return curTheme;
        }
        if (targetSdkVersion < 11) {
            return orig;
        }
        if (targetSdkVersion < 14) {
            return holo;
        }
        return deviceDefault;
    }

    /* loaded from: Resources$NotFoundException.class */
    public static class NotFoundException extends RuntimeException {
        public NotFoundException() {
        }

        public NotFoundException(String name) {
            super(name);
        }
    }

    public Resources(AssetManager assets, DisplayMetrics metrics, Configuration config) {
        this(assets, metrics, config, CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO, null);
    }

    public Resources(AssetManager assets, DisplayMetrics metrics, Configuration config, CompatibilityInfo compatInfo, IBinder token) {
        this.mAccessLock = new Object();
        this.mTmpConfig = new Configuration();
        this.mTmpValue = new TypedValue();
        this.mDrawableCache = new LongSparseArray<>(0);
        this.mColorStateListCache = new LongSparseArray<>(0);
        this.mColorDrawableCache = new LongSparseArray<>(0);
        this.mCachedStyledAttributes = null;
        this.mLastRetrievedAttrs = null;
        this.mLastCachedXmlBlockIndex = -1;
        this.mCachedXmlBlockIds = new int[]{0, 0, 0, 0};
        this.mCachedXmlBlocks = new XmlBlock[4];
        this.mConfiguration = new Configuration();
        this.mMetrics = new DisplayMetrics();
        this.mCompatibilityInfo = CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO;
        this.mAssets = assets;
        this.mMetrics.setToDefaults();
        if (compatInfo != null) {
            this.mCompatibilityInfo = compatInfo;
        }
        this.mToken = new WeakReference<>(token);
        updateConfiguration(config, metrics);
        assets.ensureStringBlocks();
    }

    public static Resources getSystem() {
        Resources resources;
        synchronized (sSync) {
            Resources ret = mSystem;
            if (ret == null) {
                ret = new Resources();
                mSystem = ret;
            }
            resources = ret;
        }
        return resources;
    }

    public CharSequence getText(int id) throws NotFoundException {
        CharSequence res = this.mAssets.getResourceText(id);
        if (res != null) {
            return res;
        }
        throw new NotFoundException("String resource ID #0x" + Integer.toHexString(id));
    }

    public CharSequence getQuantityText(int id, int quantity) throws NotFoundException {
        NativePluralRules rule = getPluralRule();
        CharSequence res = this.mAssets.getResourceBagText(id, attrForQuantityCode(rule.quantityForInt(quantity)));
        if (res != null) {
            return res;
        }
        CharSequence res2 = this.mAssets.getResourceBagText(id, ID_OTHER);
        if (res2 != null) {
            return res2;
        }
        throw new NotFoundException("Plural resource ID #0x" + Integer.toHexString(id) + " quantity=" + quantity + " item=" + stringForQuantityCode(rule.quantityForInt(quantity)));
    }

    private NativePluralRules getPluralRule() {
        NativePluralRules nativePluralRules;
        synchronized (sSync) {
            if (this.mPluralRule == null) {
                this.mPluralRule = NativePluralRules.forLocale(this.mConfiguration.locale);
            }
            nativePluralRules = this.mPluralRule;
        }
        return nativePluralRules;
    }

    private static int attrForQuantityCode(int quantityCode) {
        switch (quantityCode) {
            case 0:
                return 16777221;
            case 1:
                return 16777222;
            case 2:
                return 16777223;
            case 3:
                return 16777224;
            case 4:
                return 16777225;
            default:
                return ID_OTHER;
        }
    }

    private static String stringForQuantityCode(int quantityCode) {
        switch (quantityCode) {
            case 0:
                return "zero";
            case 1:
                return "one";
            case 2:
                return "two";
            case 3:
                return "few";
            case 4:
                return "many";
            default:
                return CardEmulation.CATEGORY_OTHER;
        }
    }

    public String getString(int id) throws NotFoundException {
        CharSequence res = getText(id);
        if (res != null) {
            return res.toString();
        }
        throw new NotFoundException("String resource ID #0x" + Integer.toHexString(id));
    }

    public String getString(int id, Object... formatArgs) throws NotFoundException {
        String raw = getString(id);
        return String.format(this.mConfiguration.locale, raw, formatArgs);
    }

    public String getQuantityString(int id, int quantity, Object... formatArgs) throws NotFoundException {
        String raw = getQuantityText(id, quantity).toString();
        return String.format(this.mConfiguration.locale, raw, formatArgs);
    }

    public String getQuantityString(int id, int quantity) throws NotFoundException {
        return getQuantityText(id, quantity).toString();
    }

    public CharSequence getText(int id, CharSequence def) {
        CharSequence res = id != 0 ? this.mAssets.getResourceText(id) : null;
        return res != null ? res : def;
    }

    public CharSequence[] getTextArray(int id) throws NotFoundException {
        CharSequence[] res = this.mAssets.getResourceTextArray(id);
        if (res != null) {
            return res;
        }
        throw new NotFoundException("Text array resource ID #0x" + Integer.toHexString(id));
    }

    public String[] getStringArray(int id) throws NotFoundException {
        String[] res = this.mAssets.getResourceStringArray(id);
        if (res != null) {
            return res;
        }
        throw new NotFoundException("String array resource ID #0x" + Integer.toHexString(id));
    }

    public int[] getIntArray(int id) throws NotFoundException {
        int[] res = this.mAssets.getArrayIntResource(id);
        if (res != null) {
            return res;
        }
        throw new NotFoundException("Int array resource ID #0x" + Integer.toHexString(id));
    }

    public TypedArray obtainTypedArray(int id) throws NotFoundException {
        int len = this.mAssets.getArraySize(id);
        if (len < 0) {
            throw new NotFoundException("Array resource ID #0x" + Integer.toHexString(id));
        }
        TypedArray array = getCachedStyledAttributes(len);
        array.mLength = this.mAssets.retrieveArray(id, array.mData);
        array.mIndices[0] = 0;
        return array;
    }

    public float getDimension(int id) throws NotFoundException {
        float complexToDimension;
        synchronized (this.mAccessLock) {
            TypedValue value = this.mTmpValue;
            if (value == null) {
                TypedValue typedValue = new TypedValue();
                value = typedValue;
                this.mTmpValue = typedValue;
            }
            getValue(id, value, true);
            if (value.type == 5) {
                complexToDimension = TypedValue.complexToDimension(value.data, this.mMetrics);
            } else {
                throw new NotFoundException("Resource ID #0x" + Integer.toHexString(id) + " type #0x" + Integer.toHexString(value.type) + " is not valid");
            }
        }
        return complexToDimension;
    }

    public int getDimensionPixelOffset(int id) throws NotFoundException {
        int complexToDimensionPixelOffset;
        synchronized (this.mAccessLock) {
            TypedValue value = this.mTmpValue;
            if (value == null) {
                TypedValue typedValue = new TypedValue();
                value = typedValue;
                this.mTmpValue = typedValue;
            }
            getValue(id, value, true);
            if (value.type == 5) {
                complexToDimensionPixelOffset = TypedValue.complexToDimensionPixelOffset(value.data, this.mMetrics);
            } else {
                throw new NotFoundException("Resource ID #0x" + Integer.toHexString(id) + " type #0x" + Integer.toHexString(value.type) + " is not valid");
            }
        }
        return complexToDimensionPixelOffset;
    }

    public int getDimensionPixelSize(int id) throws NotFoundException {
        int complexToDimensionPixelSize;
        synchronized (this.mAccessLock) {
            TypedValue value = this.mTmpValue;
            if (value == null) {
                TypedValue typedValue = new TypedValue();
                value = typedValue;
                this.mTmpValue = typedValue;
            }
            getValue(id, value, true);
            if (value.type == 5) {
                complexToDimensionPixelSize = TypedValue.complexToDimensionPixelSize(value.data, this.mMetrics);
            } else {
                throw new NotFoundException("Resource ID #0x" + Integer.toHexString(id) + " type #0x" + Integer.toHexString(value.type) + " is not valid");
            }
        }
        return complexToDimensionPixelSize;
    }

    public float getFraction(int id, int base, int pbase) {
        float complexToFraction;
        synchronized (this.mAccessLock) {
            TypedValue value = this.mTmpValue;
            if (value == null) {
                TypedValue typedValue = new TypedValue();
                value = typedValue;
                this.mTmpValue = typedValue;
            }
            getValue(id, value, true);
            if (value.type == 6) {
                complexToFraction = TypedValue.complexToFraction(value.data, base, pbase);
            } else {
                throw new NotFoundException("Resource ID #0x" + Integer.toHexString(id) + " type #0x" + Integer.toHexString(value.type) + " is not valid");
            }
        }
        return complexToFraction;
    }

    public Drawable getDrawable(int id) throws NotFoundException {
        TypedValue value;
        synchronized (this.mAccessLock) {
            value = this.mTmpValue;
            if (value == null) {
                value = new TypedValue();
            } else {
                this.mTmpValue = null;
            }
            getValue(id, value, true);
        }
        Drawable res = loadDrawable(value, id);
        synchronized (this.mAccessLock) {
            if (this.mTmpValue == null) {
                this.mTmpValue = value;
            }
        }
        return res;
    }

    public Drawable getDrawableForDensity(int id, int density) throws NotFoundException {
        TypedValue value;
        synchronized (this.mAccessLock) {
            value = this.mTmpValue;
            if (value == null) {
                value = new TypedValue();
            } else {
                this.mTmpValue = null;
            }
            getValueForDensity(id, density, value, true);
            if (value.density > 0 && value.density != 65535) {
                if (value.density == density) {
                    value.density = this.mMetrics.densityDpi;
                } else {
                    value.density = (value.density * this.mMetrics.densityDpi) / density;
                }
            }
        }
        Drawable res = loadDrawable(value, id);
        synchronized (this.mAccessLock) {
            if (this.mTmpValue == null) {
                this.mTmpValue = value;
            }
        }
        return res;
    }

    public Movie getMovie(int id) throws NotFoundException {
        InputStream is = openRawResource(id);
        Movie movie = Movie.decodeStream(is);
        try {
            is.close();
        } catch (IOException e) {
        }
        return movie;
    }

    public int getColor(int id) throws NotFoundException {
        synchronized (this.mAccessLock) {
            TypedValue value = this.mTmpValue;
            if (value == null) {
                value = new TypedValue();
            }
            getValue(id, value, true);
            if (value.type >= 16 && value.type <= 31) {
                this.mTmpValue = value;
                return value.data;
            } else if (value.type != 3) {
                throw new NotFoundException("Resource ID #0x" + Integer.toHexString(id) + " type #0x" + Integer.toHexString(value.type) + " is not valid");
            } else {
                this.mTmpValue = null;
                ColorStateList csl = loadColorStateList(value, id);
                synchronized (this.mAccessLock) {
                    if (this.mTmpValue == null) {
                        this.mTmpValue = value;
                    }
                }
                return csl.getDefaultColor();
            }
        }
    }

    public ColorStateList getColorStateList(int id) throws NotFoundException {
        TypedValue value;
        synchronized (this.mAccessLock) {
            value = this.mTmpValue;
            if (value == null) {
                value = new TypedValue();
            } else {
                this.mTmpValue = null;
            }
            getValue(id, value, true);
        }
        ColorStateList res = loadColorStateList(value, id);
        synchronized (this.mAccessLock) {
            if (this.mTmpValue == null) {
                this.mTmpValue = value;
            }
        }
        return res;
    }

    public boolean getBoolean(int id) throws NotFoundException {
        boolean z;
        synchronized (this.mAccessLock) {
            TypedValue value = this.mTmpValue;
            if (value == null) {
                TypedValue typedValue = new TypedValue();
                value = typedValue;
                this.mTmpValue = typedValue;
            }
            getValue(id, value, true);
            if (value.type >= 16 && value.type <= 31) {
                z = value.data != 0;
            } else {
                throw new NotFoundException("Resource ID #0x" + Integer.toHexString(id) + " type #0x" + Integer.toHexString(value.type) + " is not valid");
            }
        }
        return z;
    }

    public int getInteger(int id) throws NotFoundException {
        int i;
        synchronized (this.mAccessLock) {
            TypedValue value = this.mTmpValue;
            if (value == null) {
                TypedValue typedValue = new TypedValue();
                value = typedValue;
                this.mTmpValue = typedValue;
            }
            getValue(id, value, true);
            if (value.type >= 16 && value.type <= 31) {
                i = value.data;
            } else {
                throw new NotFoundException("Resource ID #0x" + Integer.toHexString(id) + " type #0x" + Integer.toHexString(value.type) + " is not valid");
            }
        }
        return i;
    }

    public XmlResourceParser getLayout(int id) throws NotFoundException {
        return loadXmlResourceParser(id, "layout");
    }

    public XmlResourceParser getAnimation(int id) throws NotFoundException {
        return loadXmlResourceParser(id, "anim");
    }

    public XmlResourceParser getXml(int id) throws NotFoundException {
        return loadXmlResourceParser(id, "xml");
    }

    public InputStream openRawResource(int id) throws NotFoundException {
        TypedValue value;
        synchronized (this.mAccessLock) {
            value = this.mTmpValue;
            if (value == null) {
                value = new TypedValue();
            } else {
                this.mTmpValue = null;
            }
        }
        InputStream res = openRawResource(id, value);
        synchronized (this.mAccessLock) {
            if (this.mTmpValue == null) {
                this.mTmpValue = value;
            }
        }
        return res;
    }

    public InputStream openRawResource(int id, TypedValue value) throws NotFoundException {
        getValue(id, value, true);
        try {
            return this.mAssets.openNonAsset(value.assetCookie, value.string.toString(), 2);
        } catch (Exception e) {
            NotFoundException rnf = new NotFoundException("File " + value.string.toString() + " from drawable resource ID #0x" + Integer.toHexString(id));
            rnf.initCause(e);
            throw rnf;
        }
    }

    public AssetFileDescriptor openRawResourceFd(int id) throws NotFoundException {
        TypedValue value;
        synchronized (this.mAccessLock) {
            value = this.mTmpValue;
            if (value == null) {
                value = new TypedValue();
            } else {
                this.mTmpValue = null;
            }
            getValue(id, value, true);
        }
        try {
            try {
                AssetFileDescriptor openNonAssetFd = this.mAssets.openNonAssetFd(value.assetCookie, value.string.toString());
                synchronized (this.mAccessLock) {
                    if (this.mTmpValue == null) {
                        this.mTmpValue = value;
                    }
                }
                return openNonAssetFd;
            } catch (Exception e) {
                NotFoundException rnf = new NotFoundException("File " + value.string.toString() + " from drawable resource ID #0x" + Integer.toHexString(id));
                rnf.initCause(e);
                throw rnf;
            }
        } catch (Throwable th) {
            synchronized (this.mAccessLock) {
                if (this.mTmpValue == null) {
                    this.mTmpValue = value;
                }
                throw th;
            }
        }
    }

    public void getValue(int id, TypedValue outValue, boolean resolveRefs) throws NotFoundException {
        boolean found = this.mAssets.getResourceValue(id, 0, outValue, resolveRefs);
        if (found) {
            return;
        }
        throw new NotFoundException("Resource ID #0x" + Integer.toHexString(id));
    }

    public void getValueForDensity(int id, int density, TypedValue outValue, boolean resolveRefs) throws NotFoundException {
        boolean found = this.mAssets.getResourceValue(id, density, outValue, resolveRefs);
        if (found) {
            return;
        }
        throw new NotFoundException("Resource ID #0x" + Integer.toHexString(id));
    }

    public void getValue(String name, TypedValue outValue, boolean resolveRefs) throws NotFoundException {
        int id = getIdentifier(name, "string", null);
        if (id != 0) {
            getValue(id, outValue, resolveRefs);
            return;
        }
        throw new NotFoundException("String resource name " + name);
    }

    /* loaded from: Resources$Theme.class */
    public final class Theme {
        private final AssetManager mAssets;
        private final int mTheme;

        public void applyStyle(int resid, boolean force) {
            AssetManager.applyThemeStyle(this.mTheme, resid, force);
        }

        public void setTo(Theme other) {
            AssetManager.copyTheme(this.mTheme, other.mTheme);
        }

        public TypedArray obtainStyledAttributes(int[] attrs) {
            int len = attrs.length;
            TypedArray array = Resources.this.getCachedStyledAttributes(len);
            array.mRsrcs = attrs;
            AssetManager.applyStyle(this.mTheme, 0, 0, 0, attrs, array.mData, array.mIndices);
            return array;
        }

        public TypedArray obtainStyledAttributes(int resid, int[] attrs) throws NotFoundException {
            int len = attrs.length;
            TypedArray array = Resources.this.getCachedStyledAttributes(len);
            array.mRsrcs = attrs;
            AssetManager.applyStyle(this.mTheme, 0, resid, 0, attrs, array.mData, array.mIndices);
            return array;
        }

        public TypedArray obtainStyledAttributes(AttributeSet set, int[] attrs, int defStyleAttr, int defStyleRes) {
            int len = attrs.length;
            TypedArray array = Resources.this.getCachedStyledAttributes(len);
            XmlBlock.Parser parser = (XmlBlock.Parser) set;
            AssetManager.applyStyle(this.mTheme, defStyleAttr, defStyleRes, parser != null ? parser.mParseState : 0, attrs, array.mData, array.mIndices);
            array.mRsrcs = attrs;
            array.mXml = parser;
            return array;
        }

        public boolean resolveAttribute(int resid, TypedValue outValue, boolean resolveRefs) {
            boolean got = this.mAssets.getThemeValue(this.mTheme, resid, outValue, resolveRefs);
            return got;
        }

        public void dump(int priority, String tag, String prefix) {
            AssetManager.dumpTheme(this.mTheme, priority, tag, prefix);
        }

        protected void finalize() throws Throwable {
            super.finalize();
            this.mAssets.releaseTheme(this.mTheme);
        }

        Theme() {
            this.mAssets = Resources.this.mAssets;
            this.mTheme = this.mAssets.createTheme();
        }
    }

    public final Theme newTheme() {
        return new Theme();
    }

    public TypedArray obtainAttributes(AttributeSet set, int[] attrs) {
        int len = attrs.length;
        TypedArray array = getCachedStyledAttributes(len);
        XmlBlock.Parser parser = (XmlBlock.Parser) set;
        this.mAssets.retrieveAttributes(parser.mParseState, attrs, array.mData, array.mIndices);
        array.mRsrcs = attrs;
        array.mXml = parser;
        return array;
    }

    public void updateConfiguration(Configuration config, DisplayMetrics metrics) {
        updateConfiguration(config, metrics, null);
    }

    public void updateConfiguration(Configuration config, DisplayMetrics metrics, CompatibilityInfo compat) {
        int width;
        int height;
        synchronized (this.mAccessLock) {
            if (compat != null) {
                this.mCompatibilityInfo = compat;
            }
            if (metrics != null) {
                this.mMetrics.setTo(metrics);
            }
            this.mCompatibilityInfo.applyToDisplayMetrics(this.mMetrics);
            int configChanges = 268435455;
            if (config != null) {
                this.mTmpConfig.setTo(config);
                int density = config.densityDpi;
                if (density == 0) {
                    density = this.mMetrics.noncompatDensityDpi;
                }
                this.mCompatibilityInfo.applyToConfiguration(density, this.mTmpConfig);
                if (this.mTmpConfig.locale == null) {
                    this.mTmpConfig.locale = Locale.getDefault();
                    this.mTmpConfig.setLayoutDirection(this.mTmpConfig.locale);
                }
                int configChanges2 = this.mConfiguration.updateFrom(this.mTmpConfig);
                configChanges = ActivityInfo.activityInfoConfigToNative(configChanges2);
            }
            if (this.mConfiguration.locale == null) {
                this.mConfiguration.locale = Locale.getDefault();
                this.mConfiguration.setLayoutDirection(this.mConfiguration.locale);
            }
            if (this.mConfiguration.densityDpi != 0) {
                this.mMetrics.densityDpi = this.mConfiguration.densityDpi;
                this.mMetrics.density = this.mConfiguration.densityDpi * 0.00625f;
            }
            this.mMetrics.scaledDensity = this.mMetrics.density * this.mConfiguration.fontScale;
            String locale = null;
            if (this.mConfiguration.locale != null) {
                locale = this.mConfiguration.locale.getLanguage();
                if (this.mConfiguration.locale.getCountry() != null) {
                    locale = locale + "-" + this.mConfiguration.locale.getCountry();
                }
            }
            if (this.mMetrics.widthPixels >= this.mMetrics.heightPixels) {
                width = this.mMetrics.widthPixels;
                height = this.mMetrics.heightPixels;
            } else {
                width = this.mMetrics.heightPixels;
                height = this.mMetrics.widthPixels;
            }
            int keyboardHidden = this.mConfiguration.keyboardHidden;
            if (keyboardHidden == 1 && this.mConfiguration.hardKeyboardHidden == 2) {
                keyboardHidden = 3;
            }
            this.mAssets.setConfiguration(this.mConfiguration.mcc, this.mConfiguration.mnc, locale, this.mConfiguration.orientation, this.mConfiguration.touchscreen, this.mConfiguration.densityDpi, this.mConfiguration.keyboard, keyboardHidden, this.mConfiguration.navigation, width, height, this.mConfiguration.smallestScreenWidthDp, this.mConfiguration.screenWidthDp, this.mConfiguration.screenHeightDp, this.mConfiguration.screenLayout, this.mConfiguration.uiMode, Build.VERSION.RESOURCES_SDK_INT);
            clearDrawableCacheLocked(this.mDrawableCache, configChanges);
            clearDrawableCacheLocked(this.mColorDrawableCache, configChanges);
            this.mColorStateListCache.clear();
            flushLayoutCache();
        }
        synchronized (sSync) {
            if (this.mPluralRule != null) {
                this.mPluralRule = NativePluralRules.forLocale(config.locale);
            }
        }
    }

    private void clearDrawableCacheLocked(LongSparseArray<WeakReference<Drawable.ConstantState>> cache, int configChanges) {
        Drawable.ConstantState cs;
        int N = cache.size();
        for (int i = 0; i < N; i++) {
            WeakReference<Drawable.ConstantState> ref = cache.valueAt(i);
            if (ref != null && (cs = ref.get()) != null && Configuration.needNewResources(configChanges, cs.getChangingConfigurations())) {
                cache.setValueAt(i, null);
            }
        }
    }

    public static void updateSystemConfiguration(Configuration config, DisplayMetrics metrics, CompatibilityInfo compat) {
        if (mSystem != null) {
            mSystem.updateConfiguration(config, metrics, compat);
        }
    }

    public DisplayMetrics getDisplayMetrics() {
        return this.mMetrics;
    }

    public Configuration getConfiguration() {
        return this.mConfiguration;
    }

    public CompatibilityInfo getCompatibilityInfo() {
        return this.mCompatibilityInfo;
    }

    public void setCompatibilityInfo(CompatibilityInfo ci) {
        if (ci != null) {
            this.mCompatibilityInfo = ci;
            updateConfiguration(this.mConfiguration, this.mMetrics);
        }
    }

    public int getIdentifier(String name, String defType, String defPackage) {
        if (name == null) {
            throw new NullPointerException("name is null");
        }
        try {
            return Integer.parseInt(name);
        } catch (Exception e) {
            return this.mAssets.getResourceIdentifier(name, defType, defPackage);
        }
    }

    public static boolean resourceHasPackage(int resid) {
        return (resid >>> 24) != 0;
    }

    public String getResourceName(int resid) throws NotFoundException {
        String str = this.mAssets.getResourceName(resid);
        if (str != null) {
            return str;
        }
        throw new NotFoundException("Unable to find resource ID #0x" + Integer.toHexString(resid));
    }

    public String getResourcePackageName(int resid) throws NotFoundException {
        String str = this.mAssets.getResourcePackageName(resid);
        if (str != null) {
            return str;
        }
        throw new NotFoundException("Unable to find resource ID #0x" + Integer.toHexString(resid));
    }

    public String getResourceTypeName(int resid) throws NotFoundException {
        String str = this.mAssets.getResourceTypeName(resid);
        if (str != null) {
            return str;
        }
        throw new NotFoundException("Unable to find resource ID #0x" + Integer.toHexString(resid));
    }

    public String getResourceEntryName(int resid) throws NotFoundException {
        String str = this.mAssets.getResourceEntryName(resid);
        if (str != null) {
            return str;
        }
        throw new NotFoundException("Unable to find resource ID #0x" + Integer.toHexString(resid));
    }

    public void parseBundleExtras(XmlResourceParser parser, Bundle outBundle) throws XmlPullParserException, IOException {
        int outerDepth = parser.getDepth();
        while (true) {
            int type = parser.next();
            if (type == 1) {
                return;
            }
            if (type != 3 || parser.getDepth() > outerDepth) {
                if (type != 3 && type != 4) {
                    String nodeName = parser.getName();
                    if (nodeName.equals("extra")) {
                        parseBundleExtra("extra", parser, outBundle);
                        XmlUtils.skipCurrentTag(parser);
                    } else {
                        XmlUtils.skipCurrentTag(parser);
                    }
                }
            } else {
                return;
            }
        }
    }

    public void parseBundleExtra(String tagName, AttributeSet attrs, Bundle outBundle) throws XmlPullParserException {
        TypedArray sa = obtainAttributes(attrs, R.styleable.Extra);
        String name = sa.getString(0);
        if (name == null) {
            sa.recycle();
            throw new XmlPullParserException(Separators.LESS_THAN + tagName + "> requires an android:name attribute at " + attrs.getPositionDescription());
        }
        TypedValue v = sa.peekValue(1);
        if (v != null) {
            if (v.type == 3) {
                CharSequence cs = v.coerceToString();
                outBundle.putCharSequence(name, cs);
            } else if (v.type == 18) {
                outBundle.putBoolean(name, v.data != 0);
            } else if (v.type >= 16 && v.type <= 31) {
                outBundle.putInt(name, v.data);
            } else if (v.type == 4) {
                outBundle.putFloat(name, v.getFloat());
            } else {
                sa.recycle();
                throw new XmlPullParserException(Separators.LESS_THAN + tagName + "> only supports string, integer, float, color, and boolean at " + attrs.getPositionDescription());
            }
            sa.recycle();
            return;
        }
        sa.recycle();
        throw new XmlPullParserException(Separators.LESS_THAN + tagName + "> requires an android:value or android:resource attribute at " + attrs.getPositionDescription());
    }

    public final AssetManager getAssets() {
        return this.mAssets;
    }

    public final void flushLayoutCache() {
        synchronized (this.mCachedXmlBlockIds) {
            int num = this.mCachedXmlBlockIds.length;
            for (int i = 0; i < num; i++) {
                this.mCachedXmlBlockIds[i] = 0;
                XmlBlock oldBlock = this.mCachedXmlBlocks[i];
                if (oldBlock != null) {
                    oldBlock.close();
                }
                this.mCachedXmlBlocks[i] = null;
            }
        }
    }

    public final void startPreloading() {
        synchronized (sSync) {
            if (sPreloaded) {
                throw new IllegalStateException("Resources already preloaded");
            }
            sPreloaded = true;
            this.mPreloading = true;
            sPreloadedDensity = DisplayMetrics.DENSITY_DEVICE;
            this.mConfiguration.densityDpi = sPreloadedDensity;
            updateConfiguration(null, null);
        }
    }

    public final void finishPreloading() {
        if (this.mPreloading) {
            this.mPreloading = false;
            flushLayoutCache();
        }
    }

    public LongSparseArray<Drawable.ConstantState> getPreloadedDrawables() {
        return sPreloadedDrawables[0];
    }

    private boolean verifyPreloadConfig(int changingConfigurations, int allowVarying, int resourceId, String name) {
        String resName;
        if ((changingConfigurations & (-1073745921) & (allowVarying ^ (-1))) != 0) {
            try {
                resName = getResourceName(resourceId);
            } catch (NotFoundException e) {
                resName = Separators.QUESTION;
            }
            Log.w(TAG, "Preloaded " + name + " resource #0x" + Integer.toHexString(resourceId) + " (" + resName + ") that varies with configuration!!");
            return false;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Drawable loadDrawable(TypedValue value, int id) throws NotFoundException {
        Drawable.ConstantState cs;
        boolean isColorDrawable = false;
        if (value.type >= 28 && value.type <= 31) {
            isColorDrawable = true;
        }
        long key = isColorDrawable ? value.data : (value.assetCookie << 32) | value.data;
        Drawable dr = getCachedDrawable(isColorDrawable ? this.mColorDrawableCache : this.mDrawableCache, key);
        if (dr != null) {
            return dr;
        }
        if (isColorDrawable) {
            cs = sPreloadedColorDrawables.get(key);
        } else {
            cs = sPreloadedDrawables[this.mConfiguration.getLayoutDirection()].get(key);
        }
        if (cs != null) {
            dr = cs.newDrawable(this);
        } else {
            if (isColorDrawable) {
                dr = new ColorDrawable(value.data);
            }
            if (dr == null) {
                if (value.string == null) {
                    throw new NotFoundException("Resource is not a Drawable (color or path): " + value);
                }
                String file = value.string.toString();
                if (file.endsWith(".xml")) {
                    Trace.traceBegin(Trace.TRACE_TAG_RESOURCES, file);
                    try {
                        XmlResourceParser rp = loadXmlResourceParser(file, id, value.assetCookie, "drawable");
                        dr = Drawable.createFromXml(this, rp);
                        rp.close();
                        Trace.traceEnd(Trace.TRACE_TAG_RESOURCES);
                    } catch (Exception e) {
                        Trace.traceEnd(Trace.TRACE_TAG_RESOURCES);
                        NotFoundException rnf = new NotFoundException("File " + file + " from drawable resource ID #0x" + Integer.toHexString(id));
                        rnf.initCause(e);
                        throw rnf;
                    }
                } else {
                    Trace.traceBegin(Trace.TRACE_TAG_RESOURCES, file);
                    try {
                        InputStream is = this.mAssets.openNonAsset(value.assetCookie, file, 2);
                        dr = Drawable.createFromResourceStream(this, value, is, file, null);
                        is.close();
                        Trace.traceEnd(Trace.TRACE_TAG_RESOURCES);
                    } catch (Exception e2) {
                        Trace.traceEnd(Trace.TRACE_TAG_RESOURCES);
                        NotFoundException rnf2 = new NotFoundException("File " + file + " from drawable resource ID #0x" + Integer.toHexString(id));
                        rnf2.initCause(e2);
                        throw rnf2;
                    }
                }
            }
        }
        if (dr != null) {
            dr.setChangingConfigurations(value.changingConfigurations);
            Drawable.ConstantState cs2 = dr.getConstantState();
            if (cs2 != null) {
                if (this.mPreloading) {
                    int changingConfigs = cs2.getChangingConfigurations();
                    if (isColorDrawable) {
                        if (verifyPreloadConfig(changingConfigs, 0, value.resourceId, "drawable")) {
                            sPreloadedColorDrawables.put(key, cs2);
                        }
                    } else if (verifyPreloadConfig(changingConfigs, LAYOUT_DIR_CONFIG, value.resourceId, "drawable")) {
                        if ((changingConfigs & LAYOUT_DIR_CONFIG) == 0) {
                            sPreloadedDrawables[0].put(key, cs2);
                            sPreloadedDrawables[1].put(key, cs2);
                        } else {
                            LongSparseArray<Drawable.ConstantState> preloads = sPreloadedDrawables[this.mConfiguration.getLayoutDirection()];
                            preloads.put(key, cs2);
                        }
                    }
                } else {
                    synchronized (this.mAccessLock) {
                        if (isColorDrawable) {
                            this.mColorDrawableCache.put(key, new WeakReference<>(cs2));
                        } else {
                            this.mDrawableCache.put(key, new WeakReference<>(cs2));
                        }
                    }
                }
            }
        }
        return dr;
    }

    private Drawable getCachedDrawable(LongSparseArray<WeakReference<Drawable.ConstantState>> drawableCache, long key) {
        synchronized (this.mAccessLock) {
            WeakReference<Drawable.ConstantState> wr = drawableCache.get(key);
            if (wr != null) {
                Drawable.ConstantState entry = wr.get();
                if (entry != null) {
                    return entry.newDrawable(this);
                }
                drawableCache.delete(key);
            }
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ColorStateList loadColorStateList(TypedValue value, int id) throws NotFoundException {
        long key = (value.assetCookie << 32) | value.data;
        if (value.type >= 28 && value.type <= 31) {
            ColorStateList csl = sPreloadedColorStateLists.get(key);
            if (csl != null) {
                return csl;
            }
            ColorStateList csl2 = ColorStateList.valueOf(value.data);
            if (this.mPreloading && verifyPreloadConfig(value.changingConfigurations, 0, value.resourceId, CalendarContract.ColorsColumns.COLOR)) {
                sPreloadedColorStateLists.put(key, csl2);
            }
            return csl2;
        }
        ColorStateList csl3 = getCachedColorStateList(key);
        if (csl3 != null) {
            return csl3;
        }
        ColorStateList csl4 = sPreloadedColorStateLists.get(key);
        if (csl4 != null) {
            return csl4;
        }
        if (value.string == null) {
            throw new NotFoundException("Resource is not a ColorStateList (color or path): " + value);
        }
        String file = value.string.toString();
        if (file.endsWith(".xml")) {
            Trace.traceBegin(Trace.TRACE_TAG_RESOURCES, file);
            try {
                XmlResourceParser rp = loadXmlResourceParser(file, id, value.assetCookie, "colorstatelist");
                ColorStateList csl5 = ColorStateList.createFromXml(this, rp);
                rp.close();
                Trace.traceEnd(Trace.TRACE_TAG_RESOURCES);
                if (csl5 != null) {
                    if (this.mPreloading) {
                        if (verifyPreloadConfig(value.changingConfigurations, 0, value.resourceId, CalendarContract.ColorsColumns.COLOR)) {
                            sPreloadedColorStateLists.put(key, csl5);
                        }
                    } else {
                        synchronized (this.mAccessLock) {
                            this.mColorStateListCache.put(key, new WeakReference<>(csl5));
                        }
                    }
                }
                return csl5;
            } catch (Exception e) {
                Trace.traceEnd(Trace.TRACE_TAG_RESOURCES);
                NotFoundException rnf = new NotFoundException("File " + file + " from color state list resource ID #0x" + Integer.toHexString(id));
                rnf.initCause(e);
                throw rnf;
            }
        }
        throw new NotFoundException("File " + file + " from drawable resource ID #0x" + Integer.toHexString(id) + ": .xml extension required");
    }

    private ColorStateList getCachedColorStateList(long key) {
        synchronized (this.mAccessLock) {
            WeakReference<ColorStateList> wr = this.mColorStateListCache.get(key);
            if (wr != null) {
                ColorStateList entry = wr.get();
                if (entry != null) {
                    return entry;
                }
                this.mColorStateListCache.delete(key);
            }
            return null;
        }
    }

    XmlResourceParser loadXmlResourceParser(int id, String type) throws NotFoundException {
        XmlResourceParser loadXmlResourceParser;
        synchronized (this.mAccessLock) {
            TypedValue value = this.mTmpValue;
            if (value == null) {
                TypedValue typedValue = new TypedValue();
                value = typedValue;
                this.mTmpValue = typedValue;
            }
            getValue(id, value, true);
            if (value.type == 3) {
                loadXmlResourceParser = loadXmlResourceParser(value.string.toString(), id, value.assetCookie, type);
            } else {
                throw new NotFoundException("Resource ID #0x" + Integer.toHexString(id) + " type #0x" + Integer.toHexString(value.type) + " is not valid");
            }
        }
        return loadXmlResourceParser;
    }

    XmlResourceParser loadXmlResourceParser(String file, int id, int assetCookie, String type) throws NotFoundException {
        if (id != 0) {
            try {
                synchronized (this.mCachedXmlBlockIds) {
                    int num = this.mCachedXmlBlockIds.length;
                    for (int i = 0; i < num; i++) {
                        if (this.mCachedXmlBlockIds[i] == id) {
                            return this.mCachedXmlBlocks[i].newParser();
                        }
                    }
                    XmlBlock block = this.mAssets.openXmlBlockAsset(assetCookie, file);
                    if (block != null) {
                        int pos = this.mLastCachedXmlBlockIndex + 1;
                        if (pos >= num) {
                            pos = 0;
                        }
                        this.mLastCachedXmlBlockIndex = pos;
                        XmlBlock oldBlock = this.mCachedXmlBlocks[pos];
                        if (oldBlock != null) {
                            oldBlock.close();
                        }
                        this.mCachedXmlBlockIds[pos] = id;
                        this.mCachedXmlBlocks[pos] = block;
                        return block.newParser();
                    }
                }
            } catch (Exception e) {
                NotFoundException rnf = new NotFoundException("File " + file + " from xml type " + type + " resource ID #0x" + Integer.toHexString(id));
                rnf.initCause(e);
                throw rnf;
            }
        }
        throw new NotFoundException("File " + file + " from xml type " + type + " resource ID #0x" + Integer.toHexString(id));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public TypedArray getCachedStyledAttributes(int len) {
        synchronized (this.mAccessLock) {
            TypedArray attrs = this.mCachedStyledAttributes;
            if (attrs != null) {
                this.mCachedStyledAttributes = null;
                attrs.mLength = len;
                int fullLen = len * 6;
                if (attrs.mData.length >= fullLen) {
                    return attrs;
                }
                attrs.mData = new int[fullLen];
                attrs.mIndices = new int[1 + len];
                return attrs;
            }
            return new TypedArray(this, new int[len * 6], new int[1 + len], len);
        }
    }

    private Resources() {
        this.mAccessLock = new Object();
        this.mTmpConfig = new Configuration();
        this.mTmpValue = new TypedValue();
        this.mDrawableCache = new LongSparseArray<>(0);
        this.mColorStateListCache = new LongSparseArray<>(0);
        this.mColorDrawableCache = new LongSparseArray<>(0);
        this.mCachedStyledAttributes = null;
        this.mLastRetrievedAttrs = null;
        this.mLastCachedXmlBlockIndex = -1;
        this.mCachedXmlBlockIds = new int[]{0, 0, 0, 0};
        this.mCachedXmlBlocks = new XmlBlock[4];
        this.mConfiguration = new Configuration();
        this.mMetrics = new DisplayMetrics();
        this.mCompatibilityInfo = CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO;
        this.mAssets = AssetManager.getSystem();
        this.mConfiguration.setToDefaults();
        this.mMetrics.setToDefaults();
        updateConfiguration(null, null);
        this.mAssets.ensureStringBlocks();
    }
}