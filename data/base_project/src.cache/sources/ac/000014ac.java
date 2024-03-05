package android.view;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: AbsSavedState.class */
public abstract class AbsSavedState implements Parcelable {
    private final Parcelable mSuperState;
    public static final AbsSavedState EMPTY_STATE = new AbsSavedState() { // from class: android.view.AbsSavedState.1
    };
    public static final Parcelable.Creator<AbsSavedState> CREATOR = new Parcelable.Creator<AbsSavedState>() { // from class: android.view.AbsSavedState.2
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AbsSavedState createFromParcel(Parcel in) {
            Parcelable superState = in.readParcelable(null);
            if (superState != null) {
                throw new IllegalStateException("superState must be null");
            }
            return AbsSavedState.EMPTY_STATE;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AbsSavedState[] newArray(int size) {
            return new AbsSavedState[size];
        }
    };

    private AbsSavedState() {
        this.mSuperState = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbsSavedState(Parcelable superState) {
        if (superState == null) {
            throw new IllegalArgumentException("superState must not be null");
        }
        this.mSuperState = superState != EMPTY_STATE ? superState : null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbsSavedState(Parcel source) {
        Parcelable superState = source.readParcelable(null);
        this.mSuperState = superState != null ? superState : EMPTY_STATE;
    }

    public final Parcelable getSuperState() {
        return this.mSuperState;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mSuperState, flags);
    }
}