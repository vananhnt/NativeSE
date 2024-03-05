package com.android.internal.statusbar;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.PrintWriter;

/* loaded from: StatusBarIconList.class */
public class StatusBarIconList implements Parcelable {
    private String[] mSlots;
    private StatusBarIcon[] mIcons;
    public static final Parcelable.Creator<StatusBarIconList> CREATOR = new Parcelable.Creator<StatusBarIconList>() { // from class: com.android.internal.statusbar.StatusBarIconList.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public StatusBarIconList createFromParcel(Parcel parcel) {
            return new StatusBarIconList(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public StatusBarIconList[] newArray(int size) {
            return new StatusBarIconList[size];
        }
    };

    public StatusBarIconList() {
    }

    public StatusBarIconList(Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        this.mSlots = in.readStringArray();
        int N = in.readInt();
        if (N < 0) {
            this.mIcons = null;
            return;
        }
        this.mIcons = new StatusBarIcon[N];
        for (int i = 0; i < N; i++) {
            if (in.readInt() != 0) {
                this.mIcons[i] = new StatusBarIcon(in);
            }
        }
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeStringArray(this.mSlots);
        if (this.mIcons == null) {
            out.writeInt(-1);
            return;
        }
        int N = this.mIcons.length;
        out.writeInt(N);
        for (int i = 0; i < N; i++) {
            StatusBarIcon ic = this.mIcons[i];
            if (ic == null) {
                out.writeInt(0);
            } else {
                out.writeInt(1);
                ic.writeToParcel(out, flags);
            }
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public void defineSlots(String[] slots) {
        int N = slots.length;
        String[] s = new String[N];
        this.mSlots = s;
        for (int i = 0; i < N; i++) {
            s[i] = slots[i];
        }
        this.mIcons = new StatusBarIcon[N];
    }

    public int getSlotIndex(String slot) {
        int N = this.mSlots.length;
        for (int i = 0; i < N; i++) {
            if (slot.equals(this.mSlots[i])) {
                return i;
            }
        }
        return -1;
    }

    public int size() {
        return this.mSlots.length;
    }

    public void setIcon(int index, StatusBarIcon icon) {
        this.mIcons[index] = icon.m1106clone();
    }

    public void removeIcon(int index) {
        this.mIcons[index] = null;
    }

    public String getSlot(int index) {
        return this.mSlots[index];
    }

    public StatusBarIcon getIcon(int index) {
        return this.mIcons[index];
    }

    public int getViewIndex(int index) {
        int count = 0;
        for (int i = 0; i < index; i++) {
            if (this.mIcons[i] != null) {
                count++;
            }
        }
        return count;
    }

    public void copyFrom(StatusBarIconList that) {
        if (that.mSlots == null) {
            this.mSlots = null;
            this.mIcons = null;
            return;
        }
        int N = that.mSlots.length;
        this.mSlots = new String[N];
        this.mIcons = new StatusBarIcon[N];
        for (int i = 0; i < N; i++) {
            this.mSlots[i] = that.mSlots[i];
            this.mIcons[i] = that.mIcons[i] != null ? that.mIcons[i].m1106clone() : null;
        }
    }

    public void dump(PrintWriter pw) {
        int N = this.mSlots.length;
        pw.println("Icon list:");
        for (int i = 0; i < N; i++) {
            pw.printf("  %2d: (%s) %s\n", Integer.valueOf(i), this.mSlots[i], this.mIcons[i]);
        }
    }
}