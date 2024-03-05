package android.content;

import android.accounts.Account;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: PeriodicSync.class */
public class PeriodicSync implements Parcelable {
    public final Account account;
    public final String authority;
    public final Bundle extras;
    public final long period;
    public final long flexTime;
    public static final Parcelable.Creator<PeriodicSync> CREATOR = new Parcelable.Creator<PeriodicSync>() { // from class: android.content.PeriodicSync.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PeriodicSync createFromParcel(Parcel source) {
            return new PeriodicSync(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PeriodicSync[] newArray(int size) {
            return new PeriodicSync[size];
        }
    };

    public PeriodicSync(Account account, String authority, Bundle extras, long periodInSeconds) {
        this.account = account;
        this.authority = authority;
        if (extras == null) {
            this.extras = new Bundle();
        } else {
            this.extras = new Bundle(extras);
        }
        this.period = periodInSeconds;
        this.flexTime = 0L;
    }

    public PeriodicSync(PeriodicSync other) {
        this.account = other.account;
        this.authority = other.authority;
        this.extras = new Bundle(other.extras);
        this.period = other.period;
        this.flexTime = other.flexTime;
    }

    public PeriodicSync(Account account, String authority, Bundle extras, long period, long flexTime) {
        this.account = account;
        this.authority = authority;
        this.extras = new Bundle(extras);
        this.period = period;
        this.flexTime = flexTime;
    }

    private PeriodicSync(Parcel in) {
        this.account = (Account) in.readParcelable(null);
        this.authority = in.readString();
        this.extras = in.readBundle();
        this.period = in.readLong();
        this.flexTime = in.readLong();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.account, flags);
        dest.writeString(this.authority);
        dest.writeBundle(this.extras);
        dest.writeLong(this.period);
        dest.writeLong(this.flexTime);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PeriodicSync)) {
            return false;
        }
        PeriodicSync other = (PeriodicSync) o;
        return this.account.equals(other.account) && this.authority.equals(other.authority) && this.period == other.period && syncExtrasEquals(this.extras, other.extras);
    }

    /* JADX WARN: Removed duplicated region for block: B:13:0x0029  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static boolean syncExtrasEquals(android.os.Bundle r4, android.os.Bundle r5) {
        /*
            r0 = r4
            int r0 = r0.size()
            r1 = r5
            int r1 = r1.size()
            if (r0 == r1) goto Ld
            r0 = 0
            return r0
        Ld:
            r0 = r4
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L16
            r0 = 1
            return r0
        L16:
            r0 = r4
            java.util.Set r0 = r0.keySet()
            java.util.Iterator r0 = r0.iterator()
            r6 = r0
        L20:
            r0 = r6
            boolean r0 = r0.hasNext()
            if (r0 == 0) goto L52
            r0 = r6
            java.lang.Object r0 = r0.next()
            java.lang.String r0 = (java.lang.String) r0
            r7 = r0
            r0 = r5
            r1 = r7
            boolean r0 = r0.containsKey(r1)
            if (r0 != 0) goto L3d
            r0 = 0
            return r0
        L3d:
            r0 = r4
            r1 = r7
            java.lang.Object r0 = r0.get(r1)
            r1 = r5
            r2 = r7
            java.lang.Object r1 = r1.get(r2)
            boolean r0 = r0.equals(r1)
            if (r0 != 0) goto L4f
            r0 = 0
            return r0
        L4f:
            goto L20
        L52:
            r0 = 1
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: android.content.PeriodicSync.syncExtrasEquals(android.os.Bundle, android.os.Bundle):boolean");
    }

    public String toString() {
        return "account: " + this.account + ", authority: " + this.authority + ". period: " + this.period + "s , flex: " + this.flexTime;
    }
}