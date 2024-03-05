package com.android.internal.statusbar;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.UserHandle;

/* loaded from: StatusBarIcon.class */
public class StatusBarIcon implements Parcelable {
    public String iconPackage;
    public UserHandle user;
    public int iconId;
    public int iconLevel;
    public boolean visible = true;
    public int number;
    public CharSequence contentDescription;
    public static final Parcelable.Creator<StatusBarIcon> CREATOR = new Parcelable.Creator<StatusBarIcon>() { // from class: com.android.internal.statusbar.StatusBarIcon.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public StatusBarIcon createFromParcel(Parcel parcel) {
            return new StatusBarIcon(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public StatusBarIcon[] newArray(int size) {
            return new StatusBarIcon[size];
        }
    };

    public StatusBarIcon(String iconPackage, UserHandle user, int iconId, int iconLevel, int number, CharSequence contentDescription) {
        this.iconPackage = iconPackage;
        this.user = user;
        this.iconId = iconId;
        this.iconLevel = iconLevel;
        this.number = number;
        this.contentDescription = contentDescription;
    }

    public String toString() {
        return "StatusBarIcon(pkg=" + this.iconPackage + "user=" + this.user.getIdentifier() + " id=0x" + Integer.toHexString(this.iconId) + " level=" + this.iconLevel + " visible=" + this.visible + " num=" + this.number + " )";
    }

    /* renamed from: clone */
    public StatusBarIcon m1106clone() {
        StatusBarIcon that = new StatusBarIcon(this.iconPackage, this.user, this.iconId, this.iconLevel, this.number, this.contentDescription);
        that.visible = this.visible;
        return that;
    }

    public StatusBarIcon(Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        this.iconPackage = in.readString();
        this.user = (UserHandle) in.readParcelable(null);
        this.iconId = in.readInt();
        this.iconLevel = in.readInt();
        this.visible = in.readInt() != 0;
        this.number = in.readInt();
        this.contentDescription = in.readCharSequence();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.iconPackage);
        out.writeParcelable(this.user, 0);
        out.writeInt(this.iconId);
        out.writeInt(this.iconLevel);
        out.writeInt(this.visible ? 1 : 0);
        out.writeInt(this.number);
        out.writeCharSequence(this.contentDescription);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }
}