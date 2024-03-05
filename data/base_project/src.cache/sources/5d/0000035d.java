package android.content.pm;

import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Printer;
import com.android.internal.R;
import gov.nist.core.Separators;
import java.text.Collator;
import java.util.Comparator;

/* loaded from: ApplicationInfo.class */
public class ApplicationInfo extends PackageItemInfo implements Parcelable {
    public String taskAffinity;
    public String permission;
    public String processName;
    public String className;
    public int descriptionRes;
    public int theme;
    public String manageSpaceActivityName;
    public String backupAgentName;
    public int uiOptions;
    public static final int FLAG_SYSTEM = 1;
    public static final int FLAG_DEBUGGABLE = 2;
    public static final int FLAG_HAS_CODE = 4;
    public static final int FLAG_PERSISTENT = 8;
    public static final int FLAG_FACTORY_TEST = 16;
    public static final int FLAG_ALLOW_TASK_REPARENTING = 32;
    public static final int FLAG_ALLOW_CLEAR_USER_DATA = 64;
    public static final int FLAG_UPDATED_SYSTEM_APP = 128;
    public static final int FLAG_TEST_ONLY = 256;
    public static final int FLAG_SUPPORTS_SMALL_SCREENS = 512;
    public static final int FLAG_SUPPORTS_NORMAL_SCREENS = 1024;
    public static final int FLAG_SUPPORTS_LARGE_SCREENS = 2048;
    public static final int FLAG_RESIZEABLE_FOR_SCREENS = 4096;
    public static final int FLAG_SUPPORTS_SCREEN_DENSITIES = 8192;
    public static final int FLAG_VM_SAFE_MODE = 16384;
    public static final int FLAG_ALLOW_BACKUP = 32768;
    public static final int FLAG_KILL_AFTER_RESTORE = 65536;
    public static final int FLAG_RESTORE_ANY_VERSION = 131072;
    public static final int FLAG_EXTERNAL_STORAGE = 262144;
    public static final int FLAG_SUPPORTS_XLARGE_SCREENS = 524288;
    public static final int FLAG_LARGE_HEAP = 1048576;
    public static final int FLAG_STOPPED = 2097152;
    public static final int FLAG_SUPPORTS_RTL = 4194304;
    public static final int FLAG_INSTALLED = 8388608;
    public static final int FLAG_IS_DATA_ONLY = 16777216;
    public static final int FLAG_PRIVILEGED = 1073741824;
    public static final int FLAG_FORWARD_LOCK = 536870912;
    public static final int FLAG_CANT_SAVE_STATE = 268435456;
    public static final int FLAG_BLOCKED = 134217728;
    public int flags;
    public int requiresSmallestWidthDp;
    public int compatibleWidthLimitDp;
    public int largestWidthLimitDp;
    public String sourceDir;
    public String publicSourceDir;
    public String[] resourceDirs;
    public String seinfo;
    public String[] sharedLibraryFiles;
    public String dataDir;
    public String nativeLibraryDir;
    public int uid;
    public int targetSdkVersion;
    public boolean enabled;
    public int enabledSetting;
    public int installLocation;
    public static final Parcelable.Creator<ApplicationInfo> CREATOR = new Parcelable.Creator<ApplicationInfo>() { // from class: android.content.pm.ApplicationInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ApplicationInfo createFromParcel(Parcel source) {
            return new ApplicationInfo(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ApplicationInfo[] newArray(int size) {
            return new ApplicationInfo[size];
        }
    };

    public void dump(Printer pw, String prefix) {
        super.dumpFront(pw, prefix);
        if (this.className != null) {
            pw.println(prefix + "className=" + this.className);
        }
        if (this.permission != null) {
            pw.println(prefix + "permission=" + this.permission);
        }
        pw.println(prefix + "processName=" + this.processName);
        pw.println(prefix + "taskAffinity=" + this.taskAffinity);
        pw.println(prefix + "uid=" + this.uid + " flags=0x" + Integer.toHexString(this.flags) + " theme=0x" + Integer.toHexString(this.theme));
        pw.println(prefix + "requiresSmallestWidthDp=" + this.requiresSmallestWidthDp + " compatibleWidthLimitDp=" + this.compatibleWidthLimitDp + " largestWidthLimitDp=" + this.largestWidthLimitDp);
        pw.println(prefix + "sourceDir=" + this.sourceDir);
        if (this.sourceDir == null) {
            if (this.publicSourceDir != null) {
                pw.println(prefix + "publicSourceDir=" + this.publicSourceDir);
            }
        } else if (!this.sourceDir.equals(this.publicSourceDir)) {
            pw.println(prefix + "publicSourceDir=" + this.publicSourceDir);
        }
        if (this.resourceDirs != null) {
            pw.println(prefix + "resourceDirs=" + this.resourceDirs);
        }
        if (this.seinfo != null) {
            pw.println(prefix + "seinfo=" + this.seinfo);
        }
        pw.println(prefix + "dataDir=" + this.dataDir);
        if (this.sharedLibraryFiles != null) {
            pw.println(prefix + "sharedLibraryFiles=" + this.sharedLibraryFiles);
        }
        pw.println(prefix + "enabled=" + this.enabled + " targetSdkVersion=" + this.targetSdkVersion);
        if (this.manageSpaceActivityName != null) {
            pw.println(prefix + "manageSpaceActivityName=" + this.manageSpaceActivityName);
        }
        if (this.descriptionRes != 0) {
            pw.println(prefix + "description=0x" + Integer.toHexString(this.descriptionRes));
        }
        if (this.uiOptions != 0) {
            pw.println(prefix + "uiOptions=0x" + Integer.toHexString(this.uiOptions));
        }
        pw.println(prefix + "supportsRtl=" + (hasRtlSupport() ? "true" : "false"));
        super.dumpBack(pw, prefix);
    }

    public boolean hasRtlSupport() {
        return (this.flags & 4194304) == 4194304;
    }

    /* loaded from: ApplicationInfo$DisplayNameComparator.class */
    public static class DisplayNameComparator implements Comparator<ApplicationInfo> {
        private final Collator sCollator = Collator.getInstance();
        private PackageManager mPM;

        public DisplayNameComparator(PackageManager pm) {
            this.mPM = pm;
        }

        @Override // java.util.Comparator
        public final int compare(ApplicationInfo aa, ApplicationInfo ab) {
            CharSequence sa = this.mPM.getApplicationLabel(aa);
            if (sa == null) {
                sa = aa.packageName;
            }
            CharSequence sb = this.mPM.getApplicationLabel(ab);
            if (sb == null) {
                sb = ab.packageName;
            }
            return this.sCollator.compare(sa.toString(), sb.toString());
        }
    }

    public ApplicationInfo() {
        this.uiOptions = 0;
        this.flags = 0;
        this.requiresSmallestWidthDp = 0;
        this.compatibleWidthLimitDp = 0;
        this.largestWidthLimitDp = 0;
        this.enabled = true;
        this.enabledSetting = 0;
        this.installLocation = -1;
    }

    public ApplicationInfo(ApplicationInfo orig) {
        super(orig);
        this.uiOptions = 0;
        this.flags = 0;
        this.requiresSmallestWidthDp = 0;
        this.compatibleWidthLimitDp = 0;
        this.largestWidthLimitDp = 0;
        this.enabled = true;
        this.enabledSetting = 0;
        this.installLocation = -1;
        this.taskAffinity = orig.taskAffinity;
        this.permission = orig.permission;
        this.processName = orig.processName;
        this.className = orig.className;
        this.theme = orig.theme;
        this.flags = orig.flags;
        this.requiresSmallestWidthDp = orig.requiresSmallestWidthDp;
        this.compatibleWidthLimitDp = orig.compatibleWidthLimitDp;
        this.largestWidthLimitDp = orig.largestWidthLimitDp;
        this.sourceDir = orig.sourceDir;
        this.publicSourceDir = orig.publicSourceDir;
        this.nativeLibraryDir = orig.nativeLibraryDir;
        this.resourceDirs = orig.resourceDirs;
        this.seinfo = orig.seinfo;
        this.sharedLibraryFiles = orig.sharedLibraryFiles;
        this.dataDir = orig.dataDir;
        this.uid = orig.uid;
        this.targetSdkVersion = orig.targetSdkVersion;
        this.enabled = orig.enabled;
        this.enabledSetting = orig.enabledSetting;
        this.installLocation = orig.installLocation;
        this.manageSpaceActivityName = orig.manageSpaceActivityName;
        this.descriptionRes = orig.descriptionRes;
        this.uiOptions = orig.uiOptions;
        this.backupAgentName = orig.backupAgentName;
    }

    public String toString() {
        return "ApplicationInfo{" + Integer.toHexString(System.identityHashCode(this)) + Separators.SP + this.packageName + "}";
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.content.pm.PackageItemInfo, android.os.Parcelable
    public void writeToParcel(Parcel dest, int parcelableFlags) {
        super.writeToParcel(dest, parcelableFlags);
        dest.writeString(this.taskAffinity);
        dest.writeString(this.permission);
        dest.writeString(this.processName);
        dest.writeString(this.className);
        dest.writeInt(this.theme);
        dest.writeInt(this.flags);
        dest.writeInt(this.requiresSmallestWidthDp);
        dest.writeInt(this.compatibleWidthLimitDp);
        dest.writeInt(this.largestWidthLimitDp);
        dest.writeString(this.sourceDir);
        dest.writeString(this.publicSourceDir);
        dest.writeString(this.nativeLibraryDir);
        dest.writeStringArray(this.resourceDirs);
        dest.writeString(this.seinfo);
        dest.writeStringArray(this.sharedLibraryFiles);
        dest.writeString(this.dataDir);
        dest.writeInt(this.uid);
        dest.writeInt(this.targetSdkVersion);
        dest.writeInt(this.enabled ? 1 : 0);
        dest.writeInt(this.enabledSetting);
        dest.writeInt(this.installLocation);
        dest.writeString(this.manageSpaceActivityName);
        dest.writeString(this.backupAgentName);
        dest.writeInt(this.descriptionRes);
        dest.writeInt(this.uiOptions);
    }

    private ApplicationInfo(Parcel source) {
        super(source);
        this.uiOptions = 0;
        this.flags = 0;
        this.requiresSmallestWidthDp = 0;
        this.compatibleWidthLimitDp = 0;
        this.largestWidthLimitDp = 0;
        this.enabled = true;
        this.enabledSetting = 0;
        this.installLocation = -1;
        this.taskAffinity = source.readString();
        this.permission = source.readString();
        this.processName = source.readString();
        this.className = source.readString();
        this.theme = source.readInt();
        this.flags = source.readInt();
        this.requiresSmallestWidthDp = source.readInt();
        this.compatibleWidthLimitDp = source.readInt();
        this.largestWidthLimitDp = source.readInt();
        this.sourceDir = source.readString();
        this.publicSourceDir = source.readString();
        this.nativeLibraryDir = source.readString();
        this.resourceDirs = source.readStringArray();
        this.seinfo = source.readString();
        this.sharedLibraryFiles = source.readStringArray();
        this.dataDir = source.readString();
        this.uid = source.readInt();
        this.targetSdkVersion = source.readInt();
        this.enabled = source.readInt() != 0;
        this.enabledSetting = source.readInt();
        this.installLocation = source.readInt();
        this.manageSpaceActivityName = source.readString();
        this.backupAgentName = source.readString();
        this.descriptionRes = source.readInt();
        this.uiOptions = source.readInt();
    }

    public CharSequence loadDescription(PackageManager pm) {
        CharSequence label;
        if (this.descriptionRes != 0 && (label = pm.getText(this.packageName, this.descriptionRes, this)) != null) {
            return label;
        }
        return null;
    }

    public void disableCompatibilityMode() {
        this.flags |= 540160;
    }

    @Override // android.content.pm.PackageItemInfo
    protected Drawable loadDefaultIcon(PackageManager pm) {
        if ((this.flags & 262144) != 0 && isPackageUnavailable(pm)) {
            return Resources.getSystem().getDrawable(R.drawable.sym_app_on_sd_unavailable_icon);
        }
        return pm.getDefaultActivityIcon();
    }

    private boolean isPackageUnavailable(PackageManager pm) {
        try {
            return pm.getPackageInfo(this.packageName, 0) == null;
        } catch (PackageManager.NameNotFoundException e) {
            return true;
        }
    }

    @Override // android.content.pm.PackageItemInfo
    protected ApplicationInfo getApplicationInfo() {
        return this;
    }
}