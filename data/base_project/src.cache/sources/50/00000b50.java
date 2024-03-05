package android.os;

/* loaded from: Parcelable.class */
public interface Parcelable {
    public static final int PARCELABLE_WRITE_RETURN_VALUE = 1;
    public static final int CONTENTS_FILE_DESCRIPTOR = 1;

    /* loaded from: Parcelable$ClassLoaderCreator.class */
    public interface ClassLoaderCreator<T> extends Creator<T> {
        T createFromParcel(Parcel parcel, ClassLoader classLoader);
    }

    /* loaded from: Parcelable$Creator.class */
    public interface Creator<T> {
        T createFromParcel(Parcel parcel);

        T[] newArray(int i);
    }

    int describeContents();

    void writeToParcel(Parcel parcel, int i);
}