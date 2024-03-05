package android.content.pm;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Printer;
import gov.nist.core.Separators;

/* loaded from: ActivityInfo.class */
public class ActivityInfo extends ComponentInfo implements Parcelable {
    public int theme;
    public static final int LAUNCH_MULTIPLE = 0;
    public static final int LAUNCH_SINGLE_TOP = 1;
    public static final int LAUNCH_SINGLE_TASK = 2;
    public static final int LAUNCH_SINGLE_INSTANCE = 3;
    public int launchMode;
    public String permission;
    public String taskAffinity;
    public String targetActivity;
    public static final int FLAG_MULTIPROCESS = 1;
    public static final int FLAG_FINISH_ON_TASK_LAUNCH = 2;
    public static final int FLAG_CLEAR_TASK_ON_LAUNCH = 4;
    public static final int FLAG_ALWAYS_RETAIN_TASK_STATE = 8;
    public static final int FLAG_STATE_NOT_NEEDED = 16;
    public static final int FLAG_EXCLUDE_FROM_RECENTS = 32;
    public static final int FLAG_ALLOW_TASK_REPARENTING = 64;
    public static final int FLAG_NO_HISTORY = 128;
    public static final int FLAG_FINISH_ON_CLOSE_SYSTEM_DIALOGS = 256;
    public static final int FLAG_HARDWARE_ACCELERATED = 512;
    public static final int FLAG_SHOW_ON_LOCK_SCREEN = 1024;
    public static final int FLAG_IMMERSIVE = 2048;
    public static final int FLAG_PRIMARY_USER_ONLY = 536870912;
    public static final int FLAG_SINGLE_USER = 1073741824;
    public int flags;
    public static final int SCREEN_ORIENTATION_UNSPECIFIED = -1;
    public static final int SCREEN_ORIENTATION_LANDSCAPE = 0;
    public static final int SCREEN_ORIENTATION_PORTRAIT = 1;
    public static final int SCREEN_ORIENTATION_USER = 2;
    public static final int SCREEN_ORIENTATION_BEHIND = 3;
    public static final int SCREEN_ORIENTATION_SENSOR = 4;
    public static final int SCREEN_ORIENTATION_NOSENSOR = 5;
    public static final int SCREEN_ORIENTATION_SENSOR_LANDSCAPE = 6;
    public static final int SCREEN_ORIENTATION_SENSOR_PORTRAIT = 7;
    public static final int SCREEN_ORIENTATION_REVERSE_LANDSCAPE = 8;
    public static final int SCREEN_ORIENTATION_REVERSE_PORTRAIT = 9;
    public static final int SCREEN_ORIENTATION_FULL_SENSOR = 10;
    public static final int SCREEN_ORIENTATION_USER_LANDSCAPE = 11;
    public static final int SCREEN_ORIENTATION_USER_PORTRAIT = 12;
    public static final int SCREEN_ORIENTATION_FULL_USER = 13;
    public static final int SCREEN_ORIENTATION_LOCKED = 14;
    public int screenOrientation;
    public static final int CONFIG_MCC = 1;
    public static final int CONFIG_MNC = 2;
    public static final int CONFIG_LOCALE = 4;
    public static final int CONFIG_TOUCHSCREEN = 8;
    public static final int CONFIG_KEYBOARD = 16;
    public static final int CONFIG_KEYBOARD_HIDDEN = 32;
    public static final int CONFIG_NAVIGATION = 64;
    public static final int CONFIG_ORIENTATION = 128;
    public static final int CONFIG_SCREEN_LAYOUT = 256;
    public static final int CONFIG_UI_MODE = 512;
    public static final int CONFIG_SCREEN_SIZE = 1024;
    public static final int CONFIG_SMALLEST_SCREEN_SIZE = 2048;
    public static final int CONFIG_DENSITY = 4096;
    public static final int CONFIG_LAYOUT_DIRECTION = 8192;
    public static final int CONFIG_FONT_SCALE = 1073741824;
    public int configChanges;
    public int softInputMode;
    public int uiOptions;
    public static final int UIOPTION_SPLIT_ACTION_BAR_WHEN_NARROW = 1;
    public String parentActivityName;
    public static int[] CONFIG_NATIVE_BITS = {2, 1, 4, 8, 16, 32, 64, 128, 2048, 4096, 512, 8192, 256, 16384};
    public static final Parcelable.Creator<ActivityInfo> CREATOR = new Parcelable.Creator<ActivityInfo>() { // from class: android.content.pm.ActivityInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ActivityInfo createFromParcel(Parcel source) {
            return new ActivityInfo(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ActivityInfo[] newArray(int size) {
            return new ActivityInfo[size];
        }
    };

    public static int activityInfoConfigToNative(int input) {
        int output = 0;
        for (int i = 0; i < CONFIG_NATIVE_BITS.length; i++) {
            if ((input & (1 << i)) != 0) {
                output |= CONFIG_NATIVE_BITS[i];
            }
        }
        return output;
    }

    public int getRealConfigChanged() {
        return this.applicationInfo.targetSdkVersion < 13 ? this.configChanges | 1024 | 2048 : this.configChanges;
    }

    public ActivityInfo() {
        this.screenOrientation = -1;
        this.uiOptions = 0;
    }

    public ActivityInfo(ActivityInfo orig) {
        super(orig);
        this.screenOrientation = -1;
        this.uiOptions = 0;
        this.theme = orig.theme;
        this.launchMode = orig.launchMode;
        this.permission = orig.permission;
        this.taskAffinity = orig.taskAffinity;
        this.targetActivity = orig.targetActivity;
        this.flags = orig.flags;
        this.screenOrientation = orig.screenOrientation;
        this.configChanges = orig.configChanges;
        this.softInputMode = orig.softInputMode;
        this.uiOptions = orig.uiOptions;
        this.parentActivityName = orig.parentActivityName;
    }

    public final int getThemeResource() {
        return this.theme != 0 ? this.theme : this.applicationInfo.theme;
    }

    public void dump(Printer pw, String prefix) {
        super.dumpFront(pw, prefix);
        if (this.permission != null) {
            pw.println(prefix + "permission=" + this.permission);
        }
        pw.println(prefix + "taskAffinity=" + this.taskAffinity + " targetActivity=" + this.targetActivity);
        if (this.launchMode != 0 || this.flags != 0 || this.theme != 0) {
            pw.println(prefix + "launchMode=" + this.launchMode + " flags=0x" + Integer.toHexString(this.flags) + " theme=0x" + Integer.toHexString(this.theme));
        }
        if (this.screenOrientation != -1 || this.configChanges != 0 || this.softInputMode != 0) {
            pw.println(prefix + "screenOrientation=" + this.screenOrientation + " configChanges=0x" + Integer.toHexString(this.configChanges) + " softInputMode=0x" + Integer.toHexString(this.softInputMode));
        }
        if (this.uiOptions != 0) {
            pw.println(prefix + " uiOptions=0x" + Integer.toHexString(this.uiOptions));
        }
        super.dumpBack(pw, prefix);
    }

    public String toString() {
        return "ActivityInfo{" + Integer.toHexString(System.identityHashCode(this)) + Separators.SP + this.name + "}";
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.content.pm.ComponentInfo, android.content.pm.PackageItemInfo, android.os.Parcelable
    public void writeToParcel(Parcel dest, int parcelableFlags) {
        super.writeToParcel(dest, parcelableFlags);
        dest.writeInt(this.theme);
        dest.writeInt(this.launchMode);
        dest.writeString(this.permission);
        dest.writeString(this.taskAffinity);
        dest.writeString(this.targetActivity);
        dest.writeInt(this.flags);
        dest.writeInt(this.screenOrientation);
        dest.writeInt(this.configChanges);
        dest.writeInt(this.softInputMode);
        dest.writeInt(this.uiOptions);
        dest.writeString(this.parentActivityName);
    }

    private ActivityInfo(Parcel source) {
        super(source);
        this.screenOrientation = -1;
        this.uiOptions = 0;
        this.theme = source.readInt();
        this.launchMode = source.readInt();
        this.permission = source.readString();
        this.taskAffinity = source.readString();
        this.targetActivity = source.readString();
        this.flags = source.readInt();
        this.screenOrientation = source.readInt();
        this.configChanges = source.readInt();
        this.softInputMode = source.readInt();
        this.uiOptions = source.readInt();
        this.parentActivityName = source.readString();
    }
}