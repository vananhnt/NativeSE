package android.service.notification;

import android.app.Notification;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.UserHandle;

/* loaded from: StatusBarNotification.class */
public class StatusBarNotification implements Parcelable {
    private final String pkg;
    private final int id;
    private final String tag;
    private final int uid;
    private final String basePkg;
    private final int initialPid;
    private final Notification notification;
    private final UserHandle user;
    private final long postTime;
    private final int score;
    public static final Parcelable.Creator<StatusBarNotification> CREATOR = new Parcelable.Creator<StatusBarNotification>() { // from class: android.service.notification.StatusBarNotification.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public StatusBarNotification createFromParcel(Parcel parcel) {
            return new StatusBarNotification(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public StatusBarNotification[] newArray(int size) {
            return new StatusBarNotification[size];
        }
    };

    public StatusBarNotification(String pkg, int id, String tag, int uid, int initialPid, int score, Notification notification, UserHandle user) {
        this(pkg, null, id, tag, uid, initialPid, score, notification, user);
    }

    public StatusBarNotification(String pkg, String basePkg, int id, String tag, int uid, int initialPid, int score, Notification notification, UserHandle user) {
        this(pkg, basePkg, id, tag, uid, initialPid, score, notification, user, System.currentTimeMillis());
    }

    public StatusBarNotification(String pkg, String basePkg, int id, String tag, int uid, int initialPid, int score, Notification notification, UserHandle user, long postTime) {
        if (pkg == null) {
            throw new NullPointerException();
        }
        if (notification == null) {
            throw new NullPointerException();
        }
        this.pkg = pkg;
        this.basePkg = pkg;
        this.id = id;
        this.tag = tag;
        this.uid = uid;
        this.initialPid = initialPid;
        this.score = score;
        this.notification = notification;
        this.user = user;
        this.notification.setUser(user);
        this.postTime = postTime;
    }

    public StatusBarNotification(Parcel in) {
        this.pkg = in.readString();
        this.basePkg = in.readString();
        this.id = in.readInt();
        if (in.readInt() != 0) {
            this.tag = in.readString();
        } else {
            this.tag = null;
        }
        this.uid = in.readInt();
        this.initialPid = in.readInt();
        this.score = in.readInt();
        this.notification = new Notification(in);
        this.user = UserHandle.readFromParcel(in);
        this.notification.setUser(this.user);
        this.postTime = in.readLong();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.pkg);
        out.writeString(this.basePkg);
        out.writeInt(this.id);
        if (this.tag != null) {
            out.writeInt(1);
            out.writeString(this.tag);
        } else {
            out.writeInt(0);
        }
        out.writeInt(this.uid);
        out.writeInt(this.initialPid);
        out.writeInt(this.score);
        this.notification.writeToParcel(out, flags);
        this.user.writeToParcel(out, flags);
        out.writeLong(this.postTime);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public StatusBarNotification cloneLight() {
        Notification no = new Notification();
        this.notification.cloneInto(no, false);
        return new StatusBarNotification(this.pkg, this.basePkg, this.id, this.tag, this.uid, this.initialPid, this.score, no, this.user, this.postTime);
    }

    /* renamed from: clone */
    public StatusBarNotification m644clone() {
        return new StatusBarNotification(this.pkg, this.basePkg, this.id, this.tag, this.uid, this.initialPid, this.score, this.notification.m52clone(), this.user, this.postTime);
    }

    public String toString() {
        return String.format("StatusBarNotification(pkg=%s user=%s id=%d tag=%s score=%d: %s)", this.pkg, this.user, Integer.valueOf(this.id), this.tag, Integer.valueOf(this.score), this.notification);
    }

    public boolean isOngoing() {
        return (this.notification.flags & 2) != 0;
    }

    public boolean isClearable() {
        return (this.notification.flags & 2) == 0 && (this.notification.flags & 32) == 0;
    }

    public int getUserId() {
        return this.user.getIdentifier();
    }

    public String getPackageName() {
        return this.pkg;
    }

    public int getId() {
        return this.id;
    }

    public String getTag() {
        return this.tag;
    }

    public int getUid() {
        return this.uid;
    }

    public String getBasePkg() {
        return this.basePkg;
    }

    public int getInitialPid() {
        return this.initialPid;
    }

    public Notification getNotification() {
        return this.notification;
    }

    public UserHandle getUser() {
        return this.user;
    }

    public long getPostTime() {
        return this.postTime;
    }

    public int getScore() {
        return this.score;
    }
}