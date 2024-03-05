package com.android.internal.view;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.InputChannel;
import com.android.internal.view.IInputMethodSession;
import gov.nist.core.Separators;

/* loaded from: InputBindResult.class */
public final class InputBindResult implements Parcelable {
    static final String TAG = "InputBindResult";
    public final IInputMethodSession method;
    public final InputChannel channel;
    public final String id;
    public final int sequence;
    public static final Parcelable.Creator<InputBindResult> CREATOR = new Parcelable.Creator<InputBindResult>() { // from class: com.android.internal.view.InputBindResult.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public InputBindResult createFromParcel(Parcel source) {
            return new InputBindResult(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public InputBindResult[] newArray(int size) {
            return new InputBindResult[size];
        }
    };

    public InputBindResult(IInputMethodSession _method, InputChannel _channel, String _id, int _sequence) {
        this.method = _method;
        this.channel = _channel;
        this.id = _id;
        this.sequence = _sequence;
    }

    InputBindResult(Parcel source) {
        this.method = IInputMethodSession.Stub.asInterface(source.readStrongBinder());
        if (source.readInt() != 0) {
            this.channel = InputChannel.CREATOR.createFromParcel(source);
        } else {
            this.channel = null;
        }
        this.id = source.readString();
        this.sequence = source.readInt();
    }

    public String toString() {
        return "InputBindResult{" + this.method + Separators.SP + this.id + " #" + this.sequence + "}";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStrongInterface(this.method);
        if (this.channel != null) {
            dest.writeInt(1);
            this.channel.writeToParcel(dest, flags);
        } else {
            dest.writeInt(0);
        }
        dest.writeString(this.id);
        dest.writeInt(this.sequence);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        if (this.channel != null) {
            return this.channel.describeContents();
        }
        return 0;
    }
}