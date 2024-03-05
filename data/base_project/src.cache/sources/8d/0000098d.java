package android.net.wifi;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: SupplicantState.class */
public enum SupplicantState implements Parcelable {
    DISCONNECTED,
    INTERFACE_DISABLED,
    INACTIVE,
    SCANNING,
    AUTHENTICATING,
    ASSOCIATING,
    ASSOCIATED,
    FOUR_WAY_HANDSHAKE,
    GROUP_HANDSHAKE,
    COMPLETED,
    DORMANT,
    UNINITIALIZED,
    INVALID;
    
    public static final Parcelable.Creator<SupplicantState> CREATOR = new Parcelable.Creator<SupplicantState>() { // from class: android.net.wifi.SupplicantState.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public SupplicantState createFromParcel(Parcel in) {
            return SupplicantState.valueOf(in.readString());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public SupplicantState[] newArray(int size) {
            return new SupplicantState[size];
        }
    };

    public static boolean isValidState(SupplicantState state) {
        return (state == UNINITIALIZED || state == INVALID) ? false : true;
    }

    public static boolean isHandshakeState(SupplicantState state) {
        switch (state) {
            case AUTHENTICATING:
            case ASSOCIATING:
            case ASSOCIATED:
            case FOUR_WAY_HANDSHAKE:
            case GROUP_HANDSHAKE:
                return true;
            case COMPLETED:
            case DISCONNECTED:
            case INTERFACE_DISABLED:
            case INACTIVE:
            case SCANNING:
            case DORMANT:
            case UNINITIALIZED:
            case INVALID:
                return false;
            default:
                throw new IllegalArgumentException("Unknown supplicant state");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isConnecting(SupplicantState state) {
        switch (state) {
            case AUTHENTICATING:
            case ASSOCIATING:
            case ASSOCIATED:
            case FOUR_WAY_HANDSHAKE:
            case GROUP_HANDSHAKE:
            case COMPLETED:
                return true;
            case DISCONNECTED:
            case INTERFACE_DISABLED:
            case INACTIVE:
            case SCANNING:
            case DORMANT:
            case UNINITIALIZED:
            case INVALID:
                return false;
            default:
                throw new IllegalArgumentException("Unknown supplicant state");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isDriverActive(SupplicantState state) {
        switch (state) {
            case AUTHENTICATING:
            case ASSOCIATING:
            case ASSOCIATED:
            case FOUR_WAY_HANDSHAKE:
            case GROUP_HANDSHAKE:
            case COMPLETED:
            case DISCONNECTED:
            case INACTIVE:
            case SCANNING:
            case DORMANT:
                return true;
            case INTERFACE_DISABLED:
            case UNINITIALIZED:
            case INVALID:
                return false;
            default:
                throw new IllegalArgumentException("Unknown supplicant state");
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name());
    }
}