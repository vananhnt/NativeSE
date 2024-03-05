package android.view;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: InputChannel.class */
public final class InputChannel implements Parcelable {
    private static final String TAG = "InputChannel";
    private static final boolean DEBUG = false;
    public static final Parcelable.Creator<InputChannel> CREATOR = new Parcelable.Creator<InputChannel>() { // from class: android.view.InputChannel.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public InputChannel createFromParcel(Parcel source) {
            InputChannel result = new InputChannel();
            result.readFromParcel(source);
            return result;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public InputChannel[] newArray(int size) {
            return new InputChannel[size];
        }
    };
    private int mPtr;

    private static native InputChannel[] nativeOpenInputChannelPair(String str);

    private native void nativeDispose(boolean z);

    private native void nativeTransferTo(InputChannel inputChannel);

    private native void nativeReadFromParcel(Parcel parcel);

    private native void nativeWriteToParcel(Parcel parcel);

    private native void nativeDup(InputChannel inputChannel);

    private native String nativeGetName();

    protected void finalize() throws Throwable {
        try {
            nativeDispose(true);
            super.finalize();
        } catch (Throwable th) {
            super.finalize();
            throw th;
        }
    }

    public static InputChannel[] openInputChannelPair(String name) {
        if (name == null) {
            throw new IllegalArgumentException("name must not be null");
        }
        return nativeOpenInputChannelPair(name);
    }

    public String getName() {
        String name = nativeGetName();
        return name != null ? name : "uninitialized";
    }

    public void dispose() {
        nativeDispose(false);
    }

    public void transferTo(InputChannel outParameter) {
        if (outParameter == null) {
            throw new IllegalArgumentException("outParameter must not be null");
        }
        nativeTransferTo(outParameter);
    }

    public InputChannel dup() {
        InputChannel target = new InputChannel();
        nativeDup(target);
        return target;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 1;
    }

    public void readFromParcel(Parcel in) {
        if (in == null) {
            throw new IllegalArgumentException("in must not be null");
        }
        nativeReadFromParcel(in);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        if (out == null) {
            throw new IllegalArgumentException("out must not be null");
        }
        nativeWriteToParcel(out);
        if ((flags & 1) != 0) {
            dispose();
        }
    }

    public String toString() {
        return getName();
    }
}