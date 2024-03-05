package android.content.pm;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/* loaded from: LabeledIntent.class */
public class LabeledIntent extends Intent {
    private String mSourcePackage;
    private int mLabelRes;
    private CharSequence mNonLocalizedLabel;
    private int mIcon;
    public static final Parcelable.Creator<LabeledIntent> CREATOR = new Parcelable.Creator<LabeledIntent>() { // from class: android.content.pm.LabeledIntent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public LabeledIntent createFromParcel(Parcel source) {
            return new LabeledIntent(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public LabeledIntent[] newArray(int size) {
            return new LabeledIntent[size];
        }
    };

    public LabeledIntent(Intent origIntent, String sourcePackage, int labelRes, int icon) {
        super(origIntent);
        this.mSourcePackage = sourcePackage;
        this.mLabelRes = labelRes;
        this.mNonLocalizedLabel = null;
        this.mIcon = icon;
    }

    public LabeledIntent(Intent origIntent, String sourcePackage, CharSequence nonLocalizedLabel, int icon) {
        super(origIntent);
        this.mSourcePackage = sourcePackage;
        this.mLabelRes = 0;
        this.mNonLocalizedLabel = nonLocalizedLabel;
        this.mIcon = icon;
    }

    public LabeledIntent(String sourcePackage, int labelRes, int icon) {
        this.mSourcePackage = sourcePackage;
        this.mLabelRes = labelRes;
        this.mNonLocalizedLabel = null;
        this.mIcon = icon;
    }

    public LabeledIntent(String sourcePackage, CharSequence nonLocalizedLabel, int icon) {
        this.mSourcePackage = sourcePackage;
        this.mLabelRes = 0;
        this.mNonLocalizedLabel = nonLocalizedLabel;
        this.mIcon = icon;
    }

    public String getSourcePackage() {
        return this.mSourcePackage;
    }

    public int getLabelResource() {
        return this.mLabelRes;
    }

    public CharSequence getNonLocalizedLabel() {
        return this.mNonLocalizedLabel;
    }

    public int getIconResource() {
        return this.mIcon;
    }

    public CharSequence loadLabel(PackageManager pm) {
        CharSequence label;
        if (this.mNonLocalizedLabel != null) {
            return this.mNonLocalizedLabel;
        }
        if (this.mLabelRes != 0 && this.mSourcePackage != null && (label = pm.getText(this.mSourcePackage, this.mLabelRes, null)) != null) {
            return label;
        }
        return null;
    }

    public Drawable loadIcon(PackageManager pm) {
        Drawable icon;
        if (this.mIcon != 0 && this.mSourcePackage != null && (icon = pm.getDrawable(this.mSourcePackage, this.mIcon, null)) != null) {
            return icon;
        }
        return null;
    }

    @Override // android.content.Intent, android.os.Parcelable
    public void writeToParcel(Parcel dest, int parcelableFlags) {
        super.writeToParcel(dest, parcelableFlags);
        dest.writeString(this.mSourcePackage);
        dest.writeInt(this.mLabelRes);
        TextUtils.writeToParcel(this.mNonLocalizedLabel, dest, parcelableFlags);
        dest.writeInt(this.mIcon);
    }

    protected LabeledIntent(Parcel in) {
        readFromParcel(in);
    }

    @Override // android.content.Intent
    public void readFromParcel(Parcel in) {
        super.readFromParcel(in);
        this.mSourcePackage = in.readString();
        this.mLabelRes = in.readInt();
        this.mNonLocalizedLabel = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
        this.mIcon = in.readInt();
    }
}