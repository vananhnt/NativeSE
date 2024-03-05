package android.accounts;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/* loaded from: Account.class */
public class Account implements Parcelable {
    public final String name;
    public final String type;
    public static final Parcelable.Creator<Account> CREATOR = new Parcelable.Creator<Account>() { // from class: android.accounts.Account.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Account createFromParcel(Parcel source) {
            return new Account(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Account) {
            Account other = (Account) o;
            return this.name.equals(other.name) && this.type.equals(other.type);
        }
        return false;
    }

    public int hashCode() {
        int result = (31 * 17) + this.name.hashCode();
        return (31 * result) + this.type.hashCode();
    }

    public Account(String name, String type) {
        if (TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("the name must not be empty: " + name);
        }
        if (TextUtils.isEmpty(type)) {
            throw new IllegalArgumentException("the type must not be empty: " + type);
        }
        this.name = name;
        this.type = type;
    }

    public Account(Parcel in) {
        this.name = in.readString();
        this.type = in.readString();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.type);
    }

    public String toString() {
        return "Account {name=" + this.name + ", type=" + this.type + "}";
    }
}