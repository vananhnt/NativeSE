package android.os;

import android.os.Parcelable;
import java.io.PrintWriter;

/* loaded from: UserHandle.class */
public final class UserHandle implements Parcelable {
    public static final int PER_USER_RANGE = 100000;
    public static final int USER_ALL = -1;
    public static final int USER_CURRENT = -2;
    public static final int USER_CURRENT_OR_SELF = -3;
    public static final int USER_NULL = -10000;
    public static final int USER_OWNER = 0;
    public static final boolean MU_ENABLED = true;
    final int mHandle;
    public static final UserHandle ALL = new UserHandle(-1);
    public static final UserHandle CURRENT = new UserHandle(-2);
    public static final UserHandle CURRENT_OR_SELF = new UserHandle(-3);
    public static final UserHandle OWNER = new UserHandle(0);
    public static final Parcelable.Creator<UserHandle> CREATOR = new Parcelable.Creator<UserHandle>() { // from class: android.os.UserHandle.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public UserHandle createFromParcel(Parcel in) {
            return new UserHandle(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public UserHandle[] newArray(int size) {
            return new UserHandle[size];
        }
    };

    public static final boolean isSameUser(int uid1, int uid2) {
        return getUserId(uid1) == getUserId(uid2);
    }

    public static final boolean isSameApp(int uid1, int uid2) {
        return getAppId(uid1) == getAppId(uid2);
    }

    public static final boolean isIsolated(int uid) {
        int appId;
        return uid > 0 && (appId = getAppId(uid)) >= 99000 && appId <= 99999;
    }

    public static boolean isApp(int uid) {
        int appId;
        return uid > 0 && (appId = getAppId(uid)) >= 10000 && appId <= 19999;
    }

    public static final int getUserId(int uid) {
        return uid / PER_USER_RANGE;
    }

    public static final int getCallingUserId() {
        return getUserId(Binder.getCallingUid());
    }

    public static final int getUid(int userId, int appId) {
        return (userId * PER_USER_RANGE) + (appId % PER_USER_RANGE);
    }

    public static final int getAppId(int uid) {
        return uid % PER_USER_RANGE;
    }

    public static final int getSharedAppGid(int id) {
        return (50000 + (id % PER_USER_RANGE)) - 10000;
    }

    public static void formatUid(StringBuilder sb, int uid) {
        if (uid < 10000) {
            sb.append(uid);
            return;
        }
        sb.append('u');
        sb.append(getUserId(uid));
        int appId = getAppId(uid);
        if (appId >= 99000 && appId <= 99999) {
            sb.append('i');
            sb.append(appId - Process.FIRST_ISOLATED_UID);
        } else if (appId >= 10000) {
            sb.append('a');
            sb.append(appId - 10000);
        } else {
            sb.append('s');
            sb.append(appId);
        }
    }

    public static void formatUid(PrintWriter pw, int uid) {
        if (uid < 10000) {
            pw.print(uid);
            return;
        }
        pw.print('u');
        pw.print(getUserId(uid));
        int appId = getAppId(uid);
        if (appId >= 99000 && appId <= 99999) {
            pw.print('i');
            pw.print(appId - Process.FIRST_ISOLATED_UID);
        } else if (appId >= 10000) {
            pw.print('a');
            pw.print(appId - 10000);
        } else {
            pw.print('s');
            pw.print(appId);
        }
    }

    public static final int myUserId() {
        return getUserId(Process.myUid());
    }

    public UserHandle(int h) {
        this.mHandle = h;
    }

    public int getIdentifier() {
        return this.mHandle;
    }

    public String toString() {
        return "UserHandle{" + this.mHandle + "}";
    }

    public boolean equals(Object obj) {
        if (obj != null) {
            try {
                UserHandle other = (UserHandle) obj;
                return this.mHandle == other.mHandle;
            } catch (ClassCastException e) {
                return false;
            }
        }
        return false;
    }

    public int hashCode() {
        return this.mHandle;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.mHandle);
    }

    public static void writeToParcel(UserHandle h, Parcel out) {
        if (h != null) {
            h.writeToParcel(out, 0);
        } else {
            out.writeInt(-10000);
        }
    }

    public static UserHandle readFromParcel(Parcel in) {
        int h = in.readInt();
        if (h != -10000) {
            return new UserHandle(h);
        }
        return null;
    }

    public UserHandle(Parcel in) {
        this.mHandle = in.readInt();
    }
}