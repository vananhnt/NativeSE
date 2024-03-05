package android.content.pm;

import android.content.ComponentName;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Printer;
import android.util.Slog;
import java.text.Collator;
import java.util.Comparator;

/* loaded from: ResolveInfo.class */
public class ResolveInfo implements Parcelable {
    private static final String TAG = "ResolveInfo";
    public ActivityInfo activityInfo;
    public ServiceInfo serviceInfo;
    public ProviderInfo providerInfo;
    public IntentFilter filter;
    public int priority;
    public int preferredOrder;
    public int match;
    public int specificIndex;
    public boolean isDefault;
    public int labelRes;
    public CharSequence nonLocalizedLabel;
    public int icon;
    public String resolvePackageName;
    public boolean system;
    public static final Parcelable.Creator<ResolveInfo> CREATOR = new Parcelable.Creator<ResolveInfo>() { // from class: android.content.pm.ResolveInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ResolveInfo createFromParcel(Parcel source) {
            return new ResolveInfo(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ResolveInfo[] newArray(int size) {
            return new ResolveInfo[size];
        }
    };

    private ComponentInfo getComponentInfo() {
        if (this.activityInfo != null) {
            return this.activityInfo;
        }
        if (this.serviceInfo != null) {
            return this.serviceInfo;
        }
        if (this.providerInfo != null) {
            return this.providerInfo;
        }
        throw new IllegalStateException("Missing ComponentInfo!");
    }

    public CharSequence loadLabel(PackageManager pm) {
        CharSequence label;
        CharSequence label2;
        if (this.nonLocalizedLabel != null) {
            return this.nonLocalizedLabel;
        }
        if (this.resolvePackageName != null && this.labelRes != 0 && (label2 = pm.getText(this.resolvePackageName, this.labelRes, null)) != null) {
            return label2.toString().trim();
        }
        ComponentInfo ci = getComponentInfo();
        ApplicationInfo ai = ci.applicationInfo;
        if (this.labelRes != 0 && (label = pm.getText(ci.packageName, this.labelRes, ai)) != null) {
            return label.toString().trim();
        }
        CharSequence data = ci.loadLabel(pm);
        if (data != null) {
            data = data.toString().trim();
        }
        return data;
    }

    public Drawable loadIcon(PackageManager pm) {
        Drawable dr;
        Drawable dr2;
        if (this.resolvePackageName != null && this.icon != 0 && (dr2 = pm.getDrawable(this.resolvePackageName, this.icon, null)) != null) {
            return dr2;
        }
        ComponentInfo ci = getComponentInfo();
        ApplicationInfo ai = ci.applicationInfo;
        if (this.icon != 0 && (dr = pm.getDrawable(ci.packageName, this.icon, ai)) != null) {
            return dr;
        }
        return ci.loadIcon(pm);
    }

    public final int getIconResource() {
        if (this.icon != 0) {
            return this.icon;
        }
        ComponentInfo ci = getComponentInfo();
        if (ci != null) {
            return ci.getIconResource();
        }
        return 0;
    }

    public void dump(Printer pw, String prefix) {
        if (this.filter != null) {
            pw.println(prefix + "Filter:");
            this.filter.dump(pw, prefix + "  ");
        }
        pw.println(prefix + "priority=" + this.priority + " preferredOrder=" + this.preferredOrder + " match=0x" + Integer.toHexString(this.match) + " specificIndex=" + this.specificIndex + " isDefault=" + this.isDefault);
        if (this.resolvePackageName != null) {
            pw.println(prefix + "resolvePackageName=" + this.resolvePackageName);
        }
        if (this.labelRes != 0 || this.nonLocalizedLabel != null || this.icon != 0) {
            pw.println(prefix + "labelRes=0x" + Integer.toHexString(this.labelRes) + " nonLocalizedLabel=" + ((Object) this.nonLocalizedLabel) + " icon=0x" + Integer.toHexString(this.icon));
        }
        if (this.activityInfo != null) {
            pw.println(prefix + "ActivityInfo:");
            this.activityInfo.dump(pw, prefix + "  ");
        } else if (this.serviceInfo != null) {
            pw.println(prefix + "ServiceInfo:");
            this.serviceInfo.dump(pw, prefix + "  ");
        } else if (this.providerInfo != null) {
            pw.println(prefix + "ProviderInfo:");
            this.providerInfo.dump(pw, prefix + "  ");
        }
    }

    public ResolveInfo() {
        this.specificIndex = -1;
    }

    public ResolveInfo(ResolveInfo orig) {
        this.specificIndex = -1;
        this.activityInfo = orig.activityInfo;
        this.serviceInfo = orig.serviceInfo;
        this.providerInfo = orig.providerInfo;
        this.filter = orig.filter;
        this.priority = orig.priority;
        this.preferredOrder = orig.preferredOrder;
        this.match = orig.match;
        this.specificIndex = orig.specificIndex;
        this.labelRes = orig.labelRes;
        this.nonLocalizedLabel = orig.nonLocalizedLabel;
        this.icon = orig.icon;
        this.resolvePackageName = orig.resolvePackageName;
        this.system = orig.system;
    }

    public String toString() {
        ComponentInfo ci = getComponentInfo();
        StringBuilder sb = new StringBuilder(128);
        sb.append("ResolveInfo{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(' ');
        ComponentName.appendShortString(sb, ci.packageName, ci.name);
        if (this.priority != 0) {
            sb.append(" p=");
            sb.append(this.priority);
        }
        if (this.preferredOrder != 0) {
            sb.append(" o=");
            sb.append(this.preferredOrder);
        }
        sb.append(" m=0x");
        sb.append(Integer.toHexString(this.match));
        sb.append('}');
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int parcelableFlags) {
        if (this.activityInfo != null) {
            dest.writeInt(1);
            this.activityInfo.writeToParcel(dest, parcelableFlags);
        } else if (this.serviceInfo != null) {
            dest.writeInt(2);
            this.serviceInfo.writeToParcel(dest, parcelableFlags);
        } else if (this.providerInfo != null) {
            dest.writeInt(3);
            this.providerInfo.writeToParcel(dest, parcelableFlags);
        } else {
            dest.writeInt(0);
        }
        if (this.filter != null) {
            dest.writeInt(1);
            this.filter.writeToParcel(dest, parcelableFlags);
        } else {
            dest.writeInt(0);
        }
        dest.writeInt(this.priority);
        dest.writeInt(this.preferredOrder);
        dest.writeInt(this.match);
        dest.writeInt(this.specificIndex);
        dest.writeInt(this.labelRes);
        TextUtils.writeToParcel(this.nonLocalizedLabel, dest, parcelableFlags);
        dest.writeInt(this.icon);
        dest.writeString(this.resolvePackageName);
        dest.writeInt(this.system ? 1 : 0);
    }

    private ResolveInfo(Parcel source) {
        this.specificIndex = -1;
        this.activityInfo = null;
        this.serviceInfo = null;
        this.providerInfo = null;
        switch (source.readInt()) {
            case 1:
                this.activityInfo = ActivityInfo.CREATOR.createFromParcel(source);
                break;
            case 2:
                this.serviceInfo = ServiceInfo.CREATOR.createFromParcel(source);
                break;
            case 3:
                this.providerInfo = ProviderInfo.CREATOR.createFromParcel(source);
                break;
            default:
                Slog.w(TAG, "Missing ComponentInfo!");
                break;
        }
        if (source.readInt() != 0) {
            this.filter = IntentFilter.CREATOR.createFromParcel(source);
        }
        this.priority = source.readInt();
        this.preferredOrder = source.readInt();
        this.match = source.readInt();
        this.specificIndex = source.readInt();
        this.labelRes = source.readInt();
        this.nonLocalizedLabel = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(source);
        this.icon = source.readInt();
        this.resolvePackageName = source.readString();
        this.system = source.readInt() != 0;
    }

    /* loaded from: ResolveInfo$DisplayNameComparator.class */
    public static class DisplayNameComparator implements Comparator<ResolveInfo> {
        private final Collator mCollator = Collator.getInstance();
        private PackageManager mPM;

        public DisplayNameComparator(PackageManager pm) {
            this.mPM = pm;
            this.mCollator.setStrength(0);
        }

        @Override // java.util.Comparator
        public final int compare(ResolveInfo a, ResolveInfo b) {
            CharSequence sa = a.loadLabel(this.mPM);
            if (sa == null) {
                sa = a.activityInfo.name;
            }
            CharSequence sb = b.loadLabel(this.mPM);
            if (sb == null) {
                sb = b.activityInfo.name;
            }
            return this.mCollator.compare(sa.toString(), sb.toString());
        }
    }
}