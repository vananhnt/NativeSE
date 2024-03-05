package android.content.pm;

import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.text.TextUtils;
import android.util.Printer;
import java.text.Collator;
import java.util.Comparator;

/* loaded from: PackageItemInfo.class */
public class PackageItemInfo {
    public String name;
    public String packageName;
    public int labelRes;
    public CharSequence nonLocalizedLabel;
    public int icon;
    public int logo;
    public Bundle metaData;

    public PackageItemInfo() {
    }

    public PackageItemInfo(PackageItemInfo orig) {
        this.name = orig.name;
        if (this.name != null) {
            this.name = this.name.trim();
        }
        this.packageName = orig.packageName;
        this.labelRes = orig.labelRes;
        this.nonLocalizedLabel = orig.nonLocalizedLabel;
        if (this.nonLocalizedLabel != null) {
            this.nonLocalizedLabel = this.nonLocalizedLabel.toString().trim();
        }
        this.icon = orig.icon;
        this.logo = orig.logo;
        this.metaData = orig.metaData;
    }

    public CharSequence loadLabel(PackageManager pm) {
        CharSequence label;
        if (this.nonLocalizedLabel != null) {
            return this.nonLocalizedLabel;
        }
        if (this.labelRes != 0 && (label = pm.getText(this.packageName, this.labelRes, getApplicationInfo())) != null) {
            return label.toString().trim();
        }
        if (this.name != null) {
            return this.name;
        }
        return this.packageName;
    }

    public Drawable loadIcon(PackageManager pm) {
        Drawable dr;
        if (this.icon != 0 && (dr = pm.getDrawable(this.packageName, this.icon, getApplicationInfo())) != null) {
            return dr;
        }
        return loadDefaultIcon(pm);
    }

    protected Drawable loadDefaultIcon(PackageManager pm) {
        return pm.getDefaultActivityIcon();
    }

    public Drawable loadLogo(PackageManager pm) {
        Drawable d;
        if (this.logo != 0 && (d = pm.getDrawable(this.packageName, this.logo, getApplicationInfo())) != null) {
            return d;
        }
        return loadDefaultLogo(pm);
    }

    protected Drawable loadDefaultLogo(PackageManager pm) {
        return null;
    }

    public XmlResourceParser loadXmlMetaData(PackageManager pm, String name) {
        int resid;
        if (this.metaData != null && (resid = this.metaData.getInt(name)) != 0) {
            return pm.getXml(this.packageName, resid, getApplicationInfo());
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void dumpFront(Printer pw, String prefix) {
        if (this.name != null) {
            pw.println(prefix + "name=" + this.name);
        }
        pw.println(prefix + "packageName=" + this.packageName);
        if (this.labelRes != 0 || this.nonLocalizedLabel != null || this.icon != 0) {
            pw.println(prefix + "labelRes=0x" + Integer.toHexString(this.labelRes) + " nonLocalizedLabel=" + ((Object) this.nonLocalizedLabel) + " icon=0x" + Integer.toHexString(this.icon));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void dumpBack(Printer pw, String prefix) {
    }

    public void writeToParcel(Parcel dest, int parcelableFlags) {
        dest.writeString(this.name);
        dest.writeString(this.packageName);
        dest.writeInt(this.labelRes);
        TextUtils.writeToParcel(this.nonLocalizedLabel, dest, parcelableFlags);
        dest.writeInt(this.icon);
        dest.writeInt(this.logo);
        dest.writeBundle(this.metaData);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public PackageItemInfo(Parcel source) {
        this.name = source.readString();
        this.packageName = source.readString();
        this.labelRes = source.readInt();
        this.nonLocalizedLabel = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(source);
        this.icon = source.readInt();
        this.logo = source.readInt();
        this.metaData = source.readBundle();
    }

    protected ApplicationInfo getApplicationInfo() {
        return null;
    }

    /* loaded from: PackageItemInfo$DisplayNameComparator.class */
    public static class DisplayNameComparator implements Comparator<PackageItemInfo> {
        private final Collator sCollator = Collator.getInstance();
        private PackageManager mPM;

        public DisplayNameComparator(PackageManager pm) {
            this.mPM = pm;
        }

        @Override // java.util.Comparator
        public final int compare(PackageItemInfo aa, PackageItemInfo ab) {
            CharSequence sa = aa.loadLabel(this.mPM);
            if (sa == null) {
                sa = aa.name;
            }
            CharSequence sb = ab.loadLabel(this.mPM);
            if (sb == null) {
                sb = ab.name;
            }
            return this.sCollator.compare(sa.toString(), sb.toString());
        }
    }
}