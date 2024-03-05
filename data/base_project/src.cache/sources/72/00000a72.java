package android.nfc;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: TechListParcel.class */
public class TechListParcel implements Parcelable {
    private String[][] mTechLists;
    public static final Parcelable.Creator<TechListParcel> CREATOR = new Parcelable.Creator<TechListParcel>() { // from class: android.nfc.TechListParcel.1
        /* JADX WARN: Can't rename method to resolve collision */
        /* JADX WARN: Type inference failed for: r0v3, types: [java.lang.String[], java.lang.String[][]] */
        @Override // android.os.Parcelable.Creator
        public TechListParcel createFromParcel(Parcel source) {
            int count = source.readInt();
            ?? r0 = new String[count];
            for (int i = 0; i < count; i++) {
                r0[i] = source.readStringArray();
            }
            return new TechListParcel(r0);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public TechListParcel[] newArray(int size) {
            return new TechListParcel[size];
        }
    };

    public TechListParcel(String[]... strings) {
        this.mTechLists = strings;
    }

    public String[][] getTechLists() {
        return this.mTechLists;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        int count = this.mTechLists.length;
        dest.writeInt(count);
        for (int i = 0; i < count; i++) {
            String[] techList = this.mTechLists[i];
            dest.writeStringArray(techList);
        }
    }
}