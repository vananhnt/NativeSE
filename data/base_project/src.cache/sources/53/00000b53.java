package android.os;

import android.os.Parcelable;

/* loaded from: ParcelableParcel.class */
public class ParcelableParcel implements Parcelable {
    final Parcel mParcel = Parcel.obtain();
    final ClassLoader mClassLoader;
    public static final Parcelable.ClassLoaderCreator<ParcelableParcel> CREATOR = new Parcelable.ClassLoaderCreator<ParcelableParcel>() { // from class: android.os.ParcelableParcel.1
        @Override // android.os.Parcelable.Creator
        public ParcelableParcel createFromParcel(Parcel in) {
            return new ParcelableParcel(in, null);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.ClassLoaderCreator
        public ParcelableParcel createFromParcel(Parcel in, ClassLoader loader) {
            return new ParcelableParcel(in, loader);
        }

        @Override // android.os.Parcelable.Creator
        public ParcelableParcel[] newArray(int size) {
            return new ParcelableParcel[size];
        }
    };

    public ParcelableParcel(ClassLoader loader) {
        this.mClassLoader = loader;
    }

    public ParcelableParcel(Parcel src, ClassLoader loader) {
        this.mClassLoader = loader;
        int size = src.readInt();
        int pos = src.dataPosition();
        this.mParcel.appendFrom(src, src.dataPosition(), size);
        src.setDataPosition(pos + size);
    }

    public Parcel getParcel() {
        this.mParcel.setDataPosition(0);
        return this.mParcel;
    }

    public ClassLoader getClassLoader() {
        return this.mClassLoader;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mParcel.dataSize());
        dest.appendFrom(this.mParcel, 0, this.mParcel.dataSize());
    }
}