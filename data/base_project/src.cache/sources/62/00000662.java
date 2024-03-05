package android.hardware.input;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: KeyboardLayout.class */
public final class KeyboardLayout implements Parcelable, Comparable<KeyboardLayout> {
    private final String mDescriptor;
    private final String mLabel;
    private final String mCollection;
    public static final Parcelable.Creator<KeyboardLayout> CREATOR = new Parcelable.Creator<KeyboardLayout>() { // from class: android.hardware.input.KeyboardLayout.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public KeyboardLayout createFromParcel(Parcel source) {
            return new KeyboardLayout(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public KeyboardLayout[] newArray(int size) {
            return new KeyboardLayout[size];
        }
    };

    public KeyboardLayout(String descriptor, String label, String collection) {
        this.mDescriptor = descriptor;
        this.mLabel = label;
        this.mCollection = collection;
    }

    private KeyboardLayout(Parcel source) {
        this.mDescriptor = source.readString();
        this.mLabel = source.readString();
        this.mCollection = source.readString();
    }

    public String getDescriptor() {
        return this.mDescriptor;
    }

    public String getLabel() {
        return this.mLabel;
    }

    public String getCollection() {
        return this.mCollection;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mDescriptor);
        dest.writeString(this.mLabel);
        dest.writeString(this.mCollection);
    }

    @Override // java.lang.Comparable
    public int compareTo(KeyboardLayout another) {
        int result = this.mLabel.compareToIgnoreCase(another.mLabel);
        if (result == 0) {
            result = this.mCollection.compareToIgnoreCase(another.mCollection);
        }
        return result;
    }

    public String toString() {
        if (this.mCollection.isEmpty()) {
            return this.mLabel;
        }
        return this.mLabel + " - " + this.mCollection;
    }
}