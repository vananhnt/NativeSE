package android.nfc;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: ApduList.class */
public class ApduList implements Parcelable {
    private ArrayList<byte[]> commands;
    public static final Parcelable.Creator<ApduList> CREATOR = new Parcelable.Creator<ApduList>() { // from class: android.nfc.ApduList.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ApduList createFromParcel(Parcel in) {
            return new ApduList(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ApduList[] newArray(int size) {
            return new ApduList[size];
        }
    };

    public ApduList() {
        this.commands = new ArrayList<>();
    }

    public void add(byte[] command) {
        this.commands.add(command);
    }

    public List<byte[]> get() {
        return this.commands;
    }

    private ApduList(Parcel in) {
        this.commands = new ArrayList<>();
        int count = in.readInt();
        for (int i = 0; i < count; i++) {
            int length = in.readInt();
            byte[] cmd = new byte[length];
            in.readByteArray(cmd);
            this.commands.add(cmd);
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.commands.size());
        Iterator i$ = this.commands.iterator();
        while (i$.hasNext()) {
            byte[] cmd = i$.next();
            dest.writeInt(cmd.length);
            dest.writeByteArray(cmd);
        }
    }
}