package android.content.res;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Telephony;
import android.text.TextUtils;
import gov.nist.core.Separators;
import java.util.Locale;

/* loaded from: Configuration.class */
public final class Configuration implements Parcelable, Comparable<Configuration> {
    public float fontScale;
    public int mcc;
    public int mnc;
    public static final int MNC_ZERO = 65535;
    public Locale locale;
    public boolean userSetLocale;
    public static final int SCREENLAYOUT_SIZE_MASK = 15;
    public static final int SCREENLAYOUT_SIZE_UNDEFINED = 0;
    public static final int SCREENLAYOUT_SIZE_SMALL = 1;
    public static final int SCREENLAYOUT_SIZE_NORMAL = 2;
    public static final int SCREENLAYOUT_SIZE_LARGE = 3;
    public static final int SCREENLAYOUT_SIZE_XLARGE = 4;
    public static final int SCREENLAYOUT_LONG_MASK = 48;
    public static final int SCREENLAYOUT_LONG_UNDEFINED = 0;
    public static final int SCREENLAYOUT_LONG_NO = 16;
    public static final int SCREENLAYOUT_LONG_YES = 32;
    public static final int SCREENLAYOUT_LAYOUTDIR_MASK = 192;
    public static final int SCREENLAYOUT_LAYOUTDIR_SHIFT = 6;
    public static final int SCREENLAYOUT_LAYOUTDIR_UNDEFINED = 0;
    public static final int SCREENLAYOUT_LAYOUTDIR_LTR = 64;
    public static final int SCREENLAYOUT_LAYOUTDIR_RTL = 128;
    public static final int SCREENLAYOUT_UNDEFINED = 0;
    public static final int SCREENLAYOUT_COMPAT_NEEDED = 268435456;
    public int screenLayout;
    public static final int TOUCHSCREEN_UNDEFINED = 0;
    public static final int TOUCHSCREEN_NOTOUCH = 1;
    @Deprecated
    public static final int TOUCHSCREEN_STYLUS = 2;
    public static final int TOUCHSCREEN_FINGER = 3;
    public int touchscreen;
    public static final int KEYBOARD_UNDEFINED = 0;
    public static final int KEYBOARD_NOKEYS = 1;
    public static final int KEYBOARD_QWERTY = 2;
    public static final int KEYBOARD_12KEY = 3;
    public int keyboard;
    public static final int KEYBOARDHIDDEN_UNDEFINED = 0;
    public static final int KEYBOARDHIDDEN_NO = 1;
    public static final int KEYBOARDHIDDEN_YES = 2;
    public static final int KEYBOARDHIDDEN_SOFT = 3;
    public int keyboardHidden;
    public static final int HARDKEYBOARDHIDDEN_UNDEFINED = 0;
    public static final int HARDKEYBOARDHIDDEN_NO = 1;
    public static final int HARDKEYBOARDHIDDEN_YES = 2;
    public int hardKeyboardHidden;
    public static final int NAVIGATION_UNDEFINED = 0;
    public static final int NAVIGATION_NONAV = 1;
    public static final int NAVIGATION_DPAD = 2;
    public static final int NAVIGATION_TRACKBALL = 3;
    public static final int NAVIGATION_WHEEL = 4;
    public int navigation;
    public static final int NAVIGATIONHIDDEN_UNDEFINED = 0;
    public static final int NAVIGATIONHIDDEN_NO = 1;
    public static final int NAVIGATIONHIDDEN_YES = 2;
    public int navigationHidden;
    public static final int ORIENTATION_UNDEFINED = 0;
    public static final int ORIENTATION_PORTRAIT = 1;
    public static final int ORIENTATION_LANDSCAPE = 2;
    @Deprecated
    public static final int ORIENTATION_SQUARE = 3;
    public int orientation;
    public static final int UI_MODE_TYPE_MASK = 15;
    public static final int UI_MODE_TYPE_UNDEFINED = 0;
    public static final int UI_MODE_TYPE_NORMAL = 1;
    public static final int UI_MODE_TYPE_DESK = 2;
    public static final int UI_MODE_TYPE_CAR = 3;
    public static final int UI_MODE_TYPE_TELEVISION = 4;
    public static final int UI_MODE_TYPE_APPLIANCE = 5;
    public static final int UI_MODE_NIGHT_MASK = 48;
    public static final int UI_MODE_NIGHT_UNDEFINED = 0;
    public static final int UI_MODE_NIGHT_NO = 16;
    public static final int UI_MODE_NIGHT_YES = 32;
    public int uiMode;
    public static final int SCREEN_WIDTH_DP_UNDEFINED = 0;
    public int screenWidthDp;
    public static final int SCREEN_HEIGHT_DP_UNDEFINED = 0;
    public int screenHeightDp;
    public static final int SMALLEST_SCREEN_WIDTH_DP_UNDEFINED = 0;
    public int smallestScreenWidthDp;
    public static final int DENSITY_DPI_UNDEFINED = 0;
    public int densityDpi;
    public int compatScreenWidthDp;
    public int compatScreenHeightDp;
    public int compatSmallestScreenWidthDp;
    public int seq;
    public static final int NATIVE_CONFIG_MCC = 1;
    public static final int NATIVE_CONFIG_MNC = 2;
    public static final int NATIVE_CONFIG_LOCALE = 4;
    public static final int NATIVE_CONFIG_TOUCHSCREEN = 8;
    public static final int NATIVE_CONFIG_KEYBOARD = 16;
    public static final int NATIVE_CONFIG_KEYBOARD_HIDDEN = 32;
    public static final int NATIVE_CONFIG_NAVIGATION = 64;
    public static final int NATIVE_CONFIG_ORIENTATION = 128;
    public static final int NATIVE_CONFIG_DENSITY = 256;
    public static final int NATIVE_CONFIG_SCREEN_SIZE = 512;
    public static final int NATIVE_CONFIG_VERSION = 1024;
    public static final int NATIVE_CONFIG_SCREEN_LAYOUT = 2048;
    public static final int NATIVE_CONFIG_UI_MODE = 4096;
    public static final int NATIVE_CONFIG_SMALLEST_SCREEN_SIZE = 8192;
    public static final int NATIVE_CONFIG_LAYOUTDIR = 16384;
    public static final Configuration EMPTY = new Configuration();
    public static final Parcelable.Creator<Configuration> CREATOR = new Parcelable.Creator<Configuration>() { // from class: android.content.res.Configuration.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Configuration createFromParcel(Parcel source) {
            return new Configuration(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Configuration[] newArray(int size) {
            return new Configuration[size];
        }
    };

    public static int resetScreenLayout(int curLayout) {
        return (curLayout & (-268435520)) | 36;
    }

    public static int reduceScreenLayout(int curLayout, int longSizeDp, int shortSizeDp) {
        int screenLayoutSize;
        boolean screenLayoutCompatNeeded;
        boolean screenLayoutLong;
        if (longSizeDp < 470) {
            screenLayoutSize = 1;
            screenLayoutLong = false;
            screenLayoutCompatNeeded = false;
        } else {
            if (longSizeDp >= 960 && shortSizeDp >= 720) {
                screenLayoutSize = 4;
            } else if (longSizeDp >= 640 && shortSizeDp >= 480) {
                screenLayoutSize = 3;
            } else {
                screenLayoutSize = 2;
            }
            if (shortSizeDp > 321 || longSizeDp > 570) {
                screenLayoutCompatNeeded = true;
            } else {
                screenLayoutCompatNeeded = false;
            }
            if ((longSizeDp * 3) / 5 >= shortSizeDp - 1) {
                screenLayoutLong = true;
            } else {
                screenLayoutLong = false;
            }
        }
        if (!screenLayoutLong) {
            curLayout = (curLayout & (-49)) | 16;
        }
        if (screenLayoutCompatNeeded) {
            curLayout |= 268435456;
        }
        int curSize = curLayout & 15;
        if (screenLayoutSize < curSize) {
            curLayout = (curLayout & (-16)) | screenLayoutSize;
        }
        return curLayout;
    }

    public boolean isLayoutSizeAtLeast(int size) {
        int cur = this.screenLayout & 15;
        return cur != 0 && cur >= size;
    }

    public Configuration() {
        setToDefaults();
    }

    public Configuration(Configuration o) {
        setTo(o);
    }

    public void setTo(Configuration o) {
        this.fontScale = o.fontScale;
        this.mcc = o.mcc;
        this.mnc = o.mnc;
        if (o.locale != null) {
            this.locale = (Locale) o.locale.clone();
        }
        this.userSetLocale = o.userSetLocale;
        this.touchscreen = o.touchscreen;
        this.keyboard = o.keyboard;
        this.keyboardHidden = o.keyboardHidden;
        this.hardKeyboardHidden = o.hardKeyboardHidden;
        this.navigation = o.navigation;
        this.navigationHidden = o.navigationHidden;
        this.orientation = o.orientation;
        this.screenLayout = o.screenLayout;
        this.uiMode = o.uiMode;
        this.screenWidthDp = o.screenWidthDp;
        this.screenHeightDp = o.screenHeightDp;
        this.smallestScreenWidthDp = o.smallestScreenWidthDp;
        this.densityDpi = o.densityDpi;
        this.compatScreenWidthDp = o.compatScreenWidthDp;
        this.compatScreenHeightDp = o.compatScreenHeightDp;
        this.compatSmallestScreenWidthDp = o.compatSmallestScreenWidthDp;
        this.seq = o.seq;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("{");
        sb.append(this.fontScale);
        sb.append(Separators.SP);
        if (this.mcc != 0) {
            sb.append(this.mcc);
            sb.append(Telephony.Carriers.MCC);
        } else {
            sb.append("?mcc");
        }
        if (this.mnc != 0) {
            sb.append(this.mnc);
            sb.append(Telephony.Carriers.MNC);
        } else {
            sb.append("?mnc");
        }
        if (this.locale != null) {
            sb.append(Separators.SP);
            sb.append(this.locale);
        } else {
            sb.append(" ?locale");
        }
        int layoutDir = this.screenLayout & 192;
        switch (layoutDir) {
            case 0:
                sb.append(" ?layoutDir");
                break;
            case 64:
                sb.append(" ldltr");
                break;
            case 128:
                sb.append(" ldrtl");
                break;
            default:
                sb.append(" layoutDir=");
                sb.append(layoutDir >> 6);
                break;
        }
        if (this.smallestScreenWidthDp != 0) {
            sb.append(" sw");
            sb.append(this.smallestScreenWidthDp);
            sb.append("dp");
        } else {
            sb.append(" ?swdp");
        }
        if (this.screenWidthDp != 0) {
            sb.append(" w");
            sb.append(this.screenWidthDp);
            sb.append("dp");
        } else {
            sb.append(" ?wdp");
        }
        if (this.screenHeightDp != 0) {
            sb.append(" h");
            sb.append(this.screenHeightDp);
            sb.append("dp");
        } else {
            sb.append(" ?hdp");
        }
        if (this.densityDpi != 0) {
            sb.append(Separators.SP);
            sb.append(this.densityDpi);
            sb.append("dpi");
        } else {
            sb.append(" ?density");
        }
        switch (this.screenLayout & 15) {
            case 0:
                sb.append(" ?lsize");
                break;
            case 1:
                sb.append(" smll");
                break;
            case 2:
                sb.append(" nrml");
                break;
            case 3:
                sb.append(" lrg");
                break;
            case 4:
                sb.append(" xlrg");
                break;
            default:
                sb.append(" layoutSize=");
                sb.append(this.screenLayout & 15);
                break;
        }
        switch (this.screenLayout & 48) {
            case 0:
                sb.append(" ?long");
                break;
            case 16:
                break;
            case 32:
                sb.append(" long");
                break;
            default:
                sb.append(" layoutLong=");
                sb.append(this.screenLayout & 48);
                break;
        }
        switch (this.orientation) {
            case 0:
                sb.append(" ?orien");
                break;
            case 1:
                sb.append(" port");
                break;
            case 2:
                sb.append(" land");
                break;
            default:
                sb.append(" orien=");
                sb.append(this.orientation);
                break;
        }
        switch (this.uiMode & 15) {
            case 0:
                sb.append(" ?uimode");
                break;
            case 1:
                break;
            case 2:
                sb.append(" desk");
                break;
            case 3:
                sb.append(" car");
                break;
            case 4:
                sb.append(" television");
                break;
            case 5:
                sb.append(" appliance");
                break;
            default:
                sb.append(" uimode=");
                sb.append(this.uiMode & 15);
                break;
        }
        switch (this.uiMode & 48) {
            case 0:
                sb.append(" ?night");
                break;
            case 16:
                break;
            case 32:
                sb.append(" night");
                break;
            default:
                sb.append(" night=");
                sb.append(this.uiMode & 48);
                break;
        }
        switch (this.touchscreen) {
            case 0:
                sb.append(" ?touch");
                break;
            case 1:
                sb.append(" -touch");
                break;
            case 2:
                sb.append(" stylus");
                break;
            case 3:
                sb.append(" finger");
                break;
            default:
                sb.append(" touch=");
                sb.append(this.touchscreen);
                break;
        }
        switch (this.keyboard) {
            case 0:
                sb.append(" ?keyb");
                break;
            case 1:
                sb.append(" -keyb");
                break;
            case 2:
                sb.append(" qwerty");
                break;
            case 3:
                sb.append(" 12key");
                break;
            default:
                sb.append(" keys=");
                sb.append(this.keyboard);
                break;
        }
        switch (this.keyboardHidden) {
            case 0:
                sb.append("/?");
                break;
            case 1:
                sb.append("/v");
                break;
            case 2:
                sb.append("/h");
                break;
            case 3:
                sb.append("/s");
                break;
            default:
                sb.append(Separators.SLASH);
                sb.append(this.keyboardHidden);
                break;
        }
        switch (this.hardKeyboardHidden) {
            case 0:
                sb.append("/?");
                break;
            case 1:
                sb.append("/v");
                break;
            case 2:
                sb.append("/h");
                break;
            default:
                sb.append(Separators.SLASH);
                sb.append(this.hardKeyboardHidden);
                break;
        }
        switch (this.navigation) {
            case 0:
                sb.append(" ?nav");
                break;
            case 1:
                sb.append(" -nav");
                break;
            case 2:
                sb.append(" dpad");
                break;
            case 3:
                sb.append(" tball");
                break;
            case 4:
                sb.append(" wheel");
                break;
            default:
                sb.append(" nav=");
                sb.append(this.navigation);
                break;
        }
        switch (this.navigationHidden) {
            case 0:
                sb.append("/?");
                break;
            case 1:
                sb.append("/v");
                break;
            case 2:
                sb.append("/h");
                break;
            default:
                sb.append(Separators.SLASH);
                sb.append(this.navigationHidden);
                break;
        }
        if (this.seq != 0) {
            sb.append(" s.");
            sb.append(this.seq);
        }
        sb.append('}');
        return sb.toString();
    }

    public void setToDefaults() {
        this.fontScale = 1.0f;
        this.mnc = 0;
        this.mcc = 0;
        this.locale = null;
        this.userSetLocale = false;
        this.touchscreen = 0;
        this.keyboard = 0;
        this.keyboardHidden = 0;
        this.hardKeyboardHidden = 0;
        this.navigation = 0;
        this.navigationHidden = 0;
        this.orientation = 0;
        this.screenLayout = 0;
        this.uiMode = 0;
        this.compatScreenWidthDp = 0;
        this.screenWidthDp = 0;
        this.compatScreenHeightDp = 0;
        this.screenHeightDp = 0;
        this.compatSmallestScreenWidthDp = 0;
        this.smallestScreenWidthDp = 0;
        this.densityDpi = 0;
        this.seq = 0;
    }

    @Deprecated
    public void makeDefault() {
        setToDefaults();
    }

    public int updateFrom(Configuration delta) {
        int changed = 0;
        if (delta.fontScale > 0.0f && this.fontScale != delta.fontScale) {
            changed = 0 | 1073741824;
            this.fontScale = delta.fontScale;
        }
        if (delta.mcc != 0 && this.mcc != delta.mcc) {
            changed |= 1;
            this.mcc = delta.mcc;
        }
        if (delta.mnc != 0 && this.mnc != delta.mnc) {
            changed |= 2;
            this.mnc = delta.mnc;
        }
        if (delta.locale != null && (this.locale == null || !this.locale.equals(delta.locale))) {
            int changed2 = changed | 4;
            this.locale = delta.locale != null ? (Locale) delta.locale.clone() : null;
            changed = changed2 | 8192;
            setLayoutDirection(this.locale);
        }
        int deltaScreenLayoutDir = delta.screenLayout & 192;
        if (deltaScreenLayoutDir != 0 && deltaScreenLayoutDir != (this.screenLayout & 192)) {
            this.screenLayout = (this.screenLayout & (-193)) | deltaScreenLayoutDir;
            changed |= 8192;
        }
        if (delta.userSetLocale && (!this.userSetLocale || (changed & 4) != 0)) {
            changed |= 4;
            this.userSetLocale = true;
        }
        if (delta.touchscreen != 0 && this.touchscreen != delta.touchscreen) {
            changed |= 8;
            this.touchscreen = delta.touchscreen;
        }
        if (delta.keyboard != 0 && this.keyboard != delta.keyboard) {
            changed |= 16;
            this.keyboard = delta.keyboard;
        }
        if (delta.keyboardHidden != 0 && this.keyboardHidden != delta.keyboardHidden) {
            changed |= 32;
            this.keyboardHidden = delta.keyboardHidden;
        }
        if (delta.hardKeyboardHidden != 0 && this.hardKeyboardHidden != delta.hardKeyboardHidden) {
            changed |= 32;
            this.hardKeyboardHidden = delta.hardKeyboardHidden;
        }
        if (delta.navigation != 0 && this.navigation != delta.navigation) {
            changed |= 64;
            this.navigation = delta.navigation;
        }
        if (delta.navigationHidden != 0 && this.navigationHidden != delta.navigationHidden) {
            changed |= 32;
            this.navigationHidden = delta.navigationHidden;
        }
        if (delta.orientation != 0 && this.orientation != delta.orientation) {
            changed |= 128;
            this.orientation = delta.orientation;
        }
        if (getScreenLayoutNoDirection(delta.screenLayout) != 0 && getScreenLayoutNoDirection(this.screenLayout) != getScreenLayoutNoDirection(delta.screenLayout)) {
            changed |= 256;
            if ((delta.screenLayout & 192) == 0) {
                this.screenLayout = (this.screenLayout & 192) | delta.screenLayout;
            } else {
                this.screenLayout = delta.screenLayout;
            }
        }
        if (delta.uiMode != 0 && this.uiMode != delta.uiMode) {
            changed |= 512;
            if ((delta.uiMode & 15) != 0) {
                this.uiMode = (this.uiMode & (-16)) | (delta.uiMode & 15);
            }
            if ((delta.uiMode & 48) != 0) {
                this.uiMode = (this.uiMode & (-49)) | (delta.uiMode & 48);
            }
        }
        if (delta.screenWidthDp != 0 && this.screenWidthDp != delta.screenWidthDp) {
            changed |= 1024;
            this.screenWidthDp = delta.screenWidthDp;
        }
        if (delta.screenHeightDp != 0 && this.screenHeightDp != delta.screenHeightDp) {
            changed |= 1024;
            this.screenHeightDp = delta.screenHeightDp;
        }
        if (delta.smallestScreenWidthDp != 0 && this.smallestScreenWidthDp != delta.smallestScreenWidthDp) {
            changed |= 2048;
            this.smallestScreenWidthDp = delta.smallestScreenWidthDp;
        }
        if (delta.densityDpi != 0 && this.densityDpi != delta.densityDpi) {
            changed |= 4096;
            this.densityDpi = delta.densityDpi;
        }
        if (delta.compatScreenWidthDp != 0) {
            this.compatScreenWidthDp = delta.compatScreenWidthDp;
        }
        if (delta.compatScreenHeightDp != 0) {
            this.compatScreenHeightDp = delta.compatScreenHeightDp;
        }
        if (delta.compatSmallestScreenWidthDp != 0) {
            this.compatSmallestScreenWidthDp = delta.compatSmallestScreenWidthDp;
        }
        if (delta.seq != 0) {
            this.seq = delta.seq;
        }
        return changed;
    }

    public int diff(Configuration delta) {
        int changed = 0;
        if (delta.fontScale > 0.0f && this.fontScale != delta.fontScale) {
            changed = 0 | 1073741824;
        }
        if (delta.mcc != 0 && this.mcc != delta.mcc) {
            changed |= 1;
        }
        if (delta.mnc != 0 && this.mnc != delta.mnc) {
            changed |= 2;
        }
        if (delta.locale != null && (this.locale == null || !this.locale.equals(delta.locale))) {
            changed = changed | 4 | 8192;
        }
        int deltaScreenLayoutDir = delta.screenLayout & 192;
        if (deltaScreenLayoutDir != 0 && deltaScreenLayoutDir != (this.screenLayout & 192)) {
            changed |= 8192;
        }
        if (delta.touchscreen != 0 && this.touchscreen != delta.touchscreen) {
            changed |= 8;
        }
        if (delta.keyboard != 0 && this.keyboard != delta.keyboard) {
            changed |= 16;
        }
        if (delta.keyboardHidden != 0 && this.keyboardHidden != delta.keyboardHidden) {
            changed |= 32;
        }
        if (delta.hardKeyboardHidden != 0 && this.hardKeyboardHidden != delta.hardKeyboardHidden) {
            changed |= 32;
        }
        if (delta.navigation != 0 && this.navigation != delta.navigation) {
            changed |= 64;
        }
        if (delta.navigationHidden != 0 && this.navigationHidden != delta.navigationHidden) {
            changed |= 32;
        }
        if (delta.orientation != 0 && this.orientation != delta.orientation) {
            changed |= 128;
        }
        if (getScreenLayoutNoDirection(delta.screenLayout) != 0 && getScreenLayoutNoDirection(this.screenLayout) != getScreenLayoutNoDirection(delta.screenLayout)) {
            changed |= 256;
        }
        if (delta.uiMode != 0 && this.uiMode != delta.uiMode) {
            changed |= 512;
        }
        if (delta.screenWidthDp != 0 && this.screenWidthDp != delta.screenWidthDp) {
            changed |= 1024;
        }
        if (delta.screenHeightDp != 0 && this.screenHeightDp != delta.screenHeightDp) {
            changed |= 1024;
        }
        if (delta.smallestScreenWidthDp != 0 && this.smallestScreenWidthDp != delta.smallestScreenWidthDp) {
            changed |= 2048;
        }
        if (delta.densityDpi != 0 && this.densityDpi != delta.densityDpi) {
            changed |= 4096;
        }
        return changed;
    }

    public static boolean needNewResources(int configChanges, int interestingChanges) {
        return (configChanges & (interestingChanges | 1073741824)) != 0;
    }

    public boolean isOtherSeqNewer(Configuration other) {
        if (other == null) {
            return false;
        }
        if (other.seq == 0 || this.seq == 0) {
            return true;
        }
        int diff = other.seq - this.seq;
        return diff <= 65536 && diff > 0;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.fontScale);
        dest.writeInt(this.mcc);
        dest.writeInt(this.mnc);
        if (this.locale == null) {
            dest.writeInt(0);
        } else {
            dest.writeInt(1);
            dest.writeString(this.locale.getLanguage());
            dest.writeString(this.locale.getCountry());
            dest.writeString(this.locale.getVariant());
        }
        if (this.userSetLocale) {
            dest.writeInt(1);
        } else {
            dest.writeInt(0);
        }
        dest.writeInt(this.touchscreen);
        dest.writeInt(this.keyboard);
        dest.writeInt(this.keyboardHidden);
        dest.writeInt(this.hardKeyboardHidden);
        dest.writeInt(this.navigation);
        dest.writeInt(this.navigationHidden);
        dest.writeInt(this.orientation);
        dest.writeInt(this.screenLayout);
        dest.writeInt(this.uiMode);
        dest.writeInt(this.screenWidthDp);
        dest.writeInt(this.screenHeightDp);
        dest.writeInt(this.smallestScreenWidthDp);
        dest.writeInt(this.densityDpi);
        dest.writeInt(this.compatScreenWidthDp);
        dest.writeInt(this.compatScreenHeightDp);
        dest.writeInt(this.compatSmallestScreenWidthDp);
        dest.writeInt(this.seq);
    }

    public void readFromParcel(Parcel source) {
        this.fontScale = source.readFloat();
        this.mcc = source.readInt();
        this.mnc = source.readInt();
        if (source.readInt() != 0) {
            this.locale = new Locale(source.readString(), source.readString(), source.readString());
        }
        this.userSetLocale = source.readInt() == 1;
        this.touchscreen = source.readInt();
        this.keyboard = source.readInt();
        this.keyboardHidden = source.readInt();
        this.hardKeyboardHidden = source.readInt();
        this.navigation = source.readInt();
        this.navigationHidden = source.readInt();
        this.orientation = source.readInt();
        this.screenLayout = source.readInt();
        this.uiMode = source.readInt();
        this.screenWidthDp = source.readInt();
        this.screenHeightDp = source.readInt();
        this.smallestScreenWidthDp = source.readInt();
        this.densityDpi = source.readInt();
        this.compatScreenWidthDp = source.readInt();
        this.compatScreenHeightDp = source.readInt();
        this.compatSmallestScreenWidthDp = source.readInt();
        this.seq = source.readInt();
    }

    private Configuration(Parcel source) {
        readFromParcel(source);
    }

    @Override // java.lang.Comparable
    public int compareTo(Configuration that) {
        float a = this.fontScale;
        float b = that.fontScale;
        if (a < b) {
            return -1;
        }
        if (a > b) {
            return 1;
        }
        int n = this.mcc - that.mcc;
        if (n != 0) {
            return n;
        }
        int n2 = this.mnc - that.mnc;
        if (n2 != 0) {
            return n2;
        }
        if (this.locale == null) {
            if (that.locale != null) {
                return 1;
            }
        } else if (that.locale == null) {
            return -1;
        } else {
            int n3 = this.locale.getLanguage().compareTo(that.locale.getLanguage());
            if (n3 != 0) {
                return n3;
            }
            int n4 = this.locale.getCountry().compareTo(that.locale.getCountry());
            if (n4 != 0) {
                return n4;
            }
            int n5 = this.locale.getVariant().compareTo(that.locale.getVariant());
            if (n5 != 0) {
                return n5;
            }
        }
        int n6 = this.touchscreen - that.touchscreen;
        if (n6 != 0) {
            return n6;
        }
        int n7 = this.keyboard - that.keyboard;
        if (n7 != 0) {
            return n7;
        }
        int n8 = this.keyboardHidden - that.keyboardHidden;
        if (n8 != 0) {
            return n8;
        }
        int n9 = this.hardKeyboardHidden - that.hardKeyboardHidden;
        if (n9 != 0) {
            return n9;
        }
        int n10 = this.navigation - that.navigation;
        if (n10 != 0) {
            return n10;
        }
        int n11 = this.navigationHidden - that.navigationHidden;
        if (n11 != 0) {
            return n11;
        }
        int n12 = this.orientation - that.orientation;
        if (n12 != 0) {
            return n12;
        }
        int n13 = this.screenLayout - that.screenLayout;
        if (n13 != 0) {
            return n13;
        }
        int n14 = this.uiMode - that.uiMode;
        if (n14 != 0) {
            return n14;
        }
        int n15 = this.screenWidthDp - that.screenWidthDp;
        if (n15 != 0) {
            return n15;
        }
        int n16 = this.screenHeightDp - that.screenHeightDp;
        if (n16 != 0) {
            return n16;
        }
        int n17 = this.smallestScreenWidthDp - that.smallestScreenWidthDp;
        return n17 != 0 ? n17 : this.densityDpi - that.densityDpi;
    }

    public boolean equals(Configuration that) {
        if (that == null) {
            return false;
        }
        return that == this || compareTo(that) == 0;
    }

    public boolean equals(Object that) {
        try {
            return equals((Configuration) that);
        } catch (ClassCastException e) {
            return false;
        }
    }

    public int hashCode() {
        int result = (31 * 17) + Float.floatToIntBits(this.fontScale);
        return (31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * result) + this.mcc)) + this.mnc)) + (this.locale != null ? this.locale.hashCode() : 0))) + this.touchscreen)) + this.keyboard)) + this.keyboardHidden)) + this.hardKeyboardHidden)) + this.navigation)) + this.navigationHidden)) + this.orientation)) + this.screenLayout)) + this.uiMode)) + this.screenWidthDp)) + this.screenHeightDp)) + this.smallestScreenWidthDp)) + this.densityDpi;
    }

    public void setLocale(Locale loc) {
        this.locale = loc;
        this.userSetLocale = true;
        setLayoutDirection(this.locale);
    }

    public int getLayoutDirection() {
        return (this.screenLayout & 192) == 128 ? 1 : 0;
    }

    public void setLayoutDirection(Locale locale) {
        int layoutDirection = 1 + TextUtils.getLayoutDirectionFromLocale(locale);
        this.screenLayout = (this.screenLayout & (-193)) | (layoutDirection << 6);
    }

    private static int getScreenLayoutNoDirection(int screenLayout) {
        return screenLayout & (-193);
    }
}