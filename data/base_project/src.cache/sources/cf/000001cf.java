package android.app;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.BadParcelableException;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;
import com.android.internal.R;
import gov.nist.core.Separators;
import java.text.NumberFormat;
import java.util.ArrayList;

/* loaded from: Notification.class */
public class Notification implements Parcelable {
    private static final String TAG = "Notification";
    public static final int DEFAULT_ALL = -1;
    public static final int DEFAULT_SOUND = 1;
    public static final int DEFAULT_VIBRATE = 2;
    public static final int DEFAULT_LIGHTS = 4;
    public long when;
    public int icon;
    public int iconLevel;
    public int number;
    public PendingIntent contentIntent;
    public PendingIntent deleteIntent;
    public PendingIntent fullScreenIntent;
    public CharSequence tickerText;
    public RemoteViews tickerView;
    public RemoteViews contentView;
    public RemoteViews bigContentView;
    public Bitmap largeIcon;
    public Uri sound;
    public static final int STREAM_DEFAULT = -1;
    public int audioStreamType;
    public long[] vibrate;
    public int ledARGB;
    public int ledOnMS;
    public int ledOffMS;
    public int defaults;
    public static final int FLAG_SHOW_LIGHTS = 1;
    public static final int FLAG_ONGOING_EVENT = 2;
    public static final int FLAG_INSISTENT = 4;
    public static final int FLAG_ONLY_ALERT_ONCE = 8;
    public static final int FLAG_AUTO_CANCEL = 16;
    public static final int FLAG_NO_CLEAR = 32;
    public static final int FLAG_FOREGROUND_SERVICE = 64;
    public static final int FLAG_HIGH_PRIORITY = 128;
    public int flags;
    public static final int PRIORITY_DEFAULT = 0;
    public static final int PRIORITY_LOW = -1;
    public static final int PRIORITY_MIN = -2;
    public static final int PRIORITY_HIGH = 1;
    public static final int PRIORITY_MAX = 2;
    public int priority;
    public static final String KIND_CALL = "android.call";
    public static final String KIND_MESSAGE = "android.message";
    public static final String KIND_EMAIL = "android.email";
    public static final String KIND_EVENT = "android.event";
    public static final String KIND_PROMO = "android.promo";
    public String[] kind;
    public Bundle extras;
    public static final String EXTRA_TITLE = "android.title";
    public static final String EXTRA_TITLE_BIG = "android.title.big";
    public static final String EXTRA_TEXT = "android.text";
    public static final String EXTRA_SUB_TEXT = "android.subText";
    public static final String EXTRA_INFO_TEXT = "android.infoText";
    public static final String EXTRA_SUMMARY_TEXT = "android.summaryText";
    public static final String EXTRA_SMALL_ICON = "android.icon";
    public static final String EXTRA_LARGE_ICON = "android.largeIcon";
    public static final String EXTRA_LARGE_ICON_BIG = "android.largeIcon.big";
    public static final String EXTRA_PROGRESS = "android.progress";
    public static final String EXTRA_PROGRESS_MAX = "android.progressMax";
    public static final String EXTRA_PROGRESS_INDETERMINATE = "android.progressIndeterminate";
    public static final String EXTRA_SHOW_CHRONOMETER = "android.showChronometer";
    public static final String EXTRA_SHOW_WHEN = "android.showWhen";
    public static final String EXTRA_PICTURE = "android.picture";
    public static final String EXTRA_TEXT_LINES = "android.textLines";
    public static final String EXTRA_PEOPLE = "android.people";
    public static final String EXTRA_SCORE_MODIFIED = "android.scoreModified";
    public static final String EXTRA_AS_HEADS_UP = "headsup";
    public static final int HEADS_UP_NEVER = 0;
    public static final int HEADS_UP_ALLOWED = 1;
    public static final int HEADS_UP_REQUESTED = 2;
    public Action[] actions;
    public static final Parcelable.Creator<Notification> CREATOR = new Parcelable.Creator<Notification>() { // from class: android.app.Notification.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Notification createFromParcel(Parcel parcel) {
            return new Notification(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Notification[] newArray(int size) {
            return new Notification[size];
        }
    };

    /* loaded from: Notification$Action.class */
    public static class Action implements Parcelable {
        public int icon;
        public CharSequence title;
        public PendingIntent actionIntent;
        public static final Parcelable.Creator<Action> CREATOR = new Parcelable.Creator<Action>() { // from class: android.app.Notification.Action.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public Action createFromParcel(Parcel in) {
                return new Action(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public Action[] newArray(int size) {
                return new Action[size];
            }
        };

        private Action() {
        }

        private Action(Parcel in) {
            this.icon = in.readInt();
            this.title = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
            if (in.readInt() == 1) {
                this.actionIntent = PendingIntent.CREATOR.createFromParcel(in);
            }
        }

        public Action(int icon, CharSequence title, PendingIntent intent) {
            this.icon = icon;
            this.title = title;
            this.actionIntent = intent;
        }

        /* renamed from: clone */
        public Action m54clone() {
            return new Action(this.icon, this.title, this.actionIntent);
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            out.writeInt(this.icon);
            TextUtils.writeToParcel(this.title, out, flags);
            if (this.actionIntent != null) {
                out.writeInt(1);
                this.actionIntent.writeToParcel(out, flags);
                return;
            }
            out.writeInt(0);
        }
    }

    public Notification() {
        this.audioStreamType = -1;
        this.extras = new Bundle();
        this.when = System.currentTimeMillis();
        this.priority = 0;
    }

    public Notification(Context context, int icon, CharSequence tickerText, long when, CharSequence contentTitle, CharSequence contentText, Intent contentIntent) {
        this.audioStreamType = -1;
        this.extras = new Bundle();
        this.when = when;
        this.icon = icon;
        this.tickerText = tickerText;
        setLatestEventInfo(context, contentTitle, contentText, PendingIntent.getActivity(context, 0, contentIntent, 0));
    }

    @Deprecated
    public Notification(int icon, CharSequence tickerText, long when) {
        this.audioStreamType = -1;
        this.extras = new Bundle();
        this.icon = icon;
        this.tickerText = tickerText;
        this.when = when;
    }

    public Notification(Parcel parcel) {
        this.audioStreamType = -1;
        this.extras = new Bundle();
        parcel.readInt();
        this.when = parcel.readLong();
        this.icon = parcel.readInt();
        this.number = parcel.readInt();
        if (parcel.readInt() != 0) {
            this.contentIntent = PendingIntent.CREATOR.createFromParcel(parcel);
        }
        if (parcel.readInt() != 0) {
            this.deleteIntent = PendingIntent.CREATOR.createFromParcel(parcel);
        }
        if (parcel.readInt() != 0) {
            this.tickerText = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
        }
        if (parcel.readInt() != 0) {
            this.tickerView = RemoteViews.CREATOR.createFromParcel(parcel);
        }
        if (parcel.readInt() != 0) {
            this.contentView = RemoteViews.CREATOR.createFromParcel(parcel);
        }
        if (parcel.readInt() != 0) {
            this.largeIcon = Bitmap.CREATOR.createFromParcel(parcel);
        }
        this.defaults = parcel.readInt();
        this.flags = parcel.readInt();
        if (parcel.readInt() != 0) {
            this.sound = Uri.CREATOR.createFromParcel(parcel);
        }
        this.audioStreamType = parcel.readInt();
        this.vibrate = parcel.createLongArray();
        this.ledARGB = parcel.readInt();
        this.ledOnMS = parcel.readInt();
        this.ledOffMS = parcel.readInt();
        this.iconLevel = parcel.readInt();
        if (parcel.readInt() != 0) {
            this.fullScreenIntent = PendingIntent.CREATOR.createFromParcel(parcel);
        }
        this.priority = parcel.readInt();
        this.kind = parcel.createStringArray();
        this.extras = parcel.readBundle();
        this.actions = (Action[]) parcel.createTypedArray(Action.CREATOR);
        if (parcel.readInt() != 0) {
            this.bigContentView = RemoteViews.CREATOR.createFromParcel(parcel);
        }
    }

    /* renamed from: clone */
    public Notification m52clone() {
        Notification that = new Notification();
        cloneInto(that, true);
        return that;
    }

    public void cloneInto(Notification that, boolean heavy) {
        that.when = this.when;
        that.icon = this.icon;
        that.number = this.number;
        that.contentIntent = this.contentIntent;
        that.deleteIntent = this.deleteIntent;
        that.fullScreenIntent = this.fullScreenIntent;
        if (this.tickerText != null) {
            that.tickerText = this.tickerText.toString();
        }
        if (heavy && this.tickerView != null) {
            that.tickerView = this.tickerView.m1045clone();
        }
        if (heavy && this.contentView != null) {
            that.contentView = this.contentView.m1045clone();
        }
        if (heavy && this.largeIcon != null) {
            that.largeIcon = Bitmap.createBitmap(this.largeIcon);
        }
        that.iconLevel = this.iconLevel;
        that.sound = this.sound;
        that.audioStreamType = this.audioStreamType;
        long[] vibrate = this.vibrate;
        if (vibrate != null) {
            int N = vibrate.length;
            long[] vib = new long[N];
            that.vibrate = vib;
            System.arraycopy(vibrate, 0, vib, 0, N);
        }
        that.ledARGB = this.ledARGB;
        that.ledOnMS = this.ledOnMS;
        that.ledOffMS = this.ledOffMS;
        that.defaults = this.defaults;
        that.flags = this.flags;
        that.priority = this.priority;
        String[] thiskind = this.kind;
        if (thiskind != null) {
            int N2 = thiskind.length;
            String[] thatkind = new String[N2];
            that.kind = thatkind;
            System.arraycopy(thiskind, 0, thatkind, 0, N2);
        }
        if (this.extras != null) {
            try {
                that.extras = new Bundle(this.extras);
                that.extras.size();
            } catch (BadParcelableException e) {
                Log.e(TAG, "could not unparcel extras from notification: " + this, e);
                that.extras = null;
            }
        }
        if (this.actions != null) {
            that.actions = new Action[this.actions.length];
            for (int i = 0; i < this.actions.length; i++) {
                that.actions[i] = this.actions[i].m54clone();
            }
        }
        if (heavy && this.bigContentView != null) {
            that.bigContentView = this.bigContentView.m1045clone();
        }
        if (!heavy) {
            that.lightenPayload();
        }
    }

    public final void lightenPayload() {
        this.tickerView = null;
        this.contentView = null;
        this.bigContentView = null;
        this.largeIcon = null;
        if (this.extras != null) {
            this.extras.remove("android.largeIcon");
            this.extras.remove("android.largeIcon.big");
            this.extras.remove("android.picture");
        }
    }

    public static CharSequence safeCharSequence(CharSequence cs) {
        if (cs instanceof Parcelable) {
            Log.e(TAG, "warning: " + cs.getClass().getCanonicalName() + " instance is a custom Parcelable and not allowed in Notification");
            return cs.toString();
        }
        return cs;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(1);
        parcel.writeLong(this.when);
        parcel.writeInt(this.icon);
        parcel.writeInt(this.number);
        if (this.contentIntent != null) {
            parcel.writeInt(1);
            this.contentIntent.writeToParcel(parcel, 0);
        } else {
            parcel.writeInt(0);
        }
        if (this.deleteIntent != null) {
            parcel.writeInt(1);
            this.deleteIntent.writeToParcel(parcel, 0);
        } else {
            parcel.writeInt(0);
        }
        if (this.tickerText != null) {
            parcel.writeInt(1);
            TextUtils.writeToParcel(this.tickerText, parcel, flags);
        } else {
            parcel.writeInt(0);
        }
        if (this.tickerView != null) {
            parcel.writeInt(1);
            this.tickerView.writeToParcel(parcel, 0);
        } else {
            parcel.writeInt(0);
        }
        if (this.contentView != null) {
            parcel.writeInt(1);
            this.contentView.writeToParcel(parcel, 0);
        } else {
            parcel.writeInt(0);
        }
        if (this.largeIcon != null) {
            parcel.writeInt(1);
            this.largeIcon.writeToParcel(parcel, 0);
        } else {
            parcel.writeInt(0);
        }
        parcel.writeInt(this.defaults);
        parcel.writeInt(this.flags);
        if (this.sound != null) {
            parcel.writeInt(1);
            this.sound.writeToParcel(parcel, 0);
        } else {
            parcel.writeInt(0);
        }
        parcel.writeInt(this.audioStreamType);
        parcel.writeLongArray(this.vibrate);
        parcel.writeInt(this.ledARGB);
        parcel.writeInt(this.ledOnMS);
        parcel.writeInt(this.ledOffMS);
        parcel.writeInt(this.iconLevel);
        if (this.fullScreenIntent != null) {
            parcel.writeInt(1);
            this.fullScreenIntent.writeToParcel(parcel, 0);
        } else {
            parcel.writeInt(0);
        }
        parcel.writeInt(this.priority);
        parcel.writeStringArray(this.kind);
        parcel.writeBundle(this.extras);
        parcel.writeTypedArray(this.actions, 0);
        if (this.bigContentView != null) {
            parcel.writeInt(1);
            this.bigContentView.writeToParcel(parcel, 0);
            return;
        }
        parcel.writeInt(0);
    }

    @Deprecated
    public void setLatestEventInfo(Context context, CharSequence contentTitle, CharSequence contentText, PendingIntent contentIntent) {
        Builder builder = new Builder(context);
        builder.setWhen(this.when);
        builder.setSmallIcon(this.icon);
        builder.setPriority(this.priority);
        builder.setTicker(this.tickerText);
        builder.setNumber(this.number);
        builder.mFlags = this.flags;
        builder.setSound(this.sound, this.audioStreamType);
        builder.setDefaults(this.defaults);
        builder.setVibrate(this.vibrate);
        if (contentTitle != null) {
            builder.setContentTitle(contentTitle);
        }
        if (contentText != null) {
            builder.setContentText(contentText);
        }
        builder.setContentIntent(contentIntent);
        builder.buildInto(this);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Notification(pri=");
        sb.append(this.priority);
        sb.append(" contentView=");
        if (this.contentView != null) {
            sb.append(this.contentView.getPackage());
            sb.append("/0x");
            sb.append(Integer.toHexString(this.contentView.getLayoutId()));
        } else {
            sb.append("null");
        }
        sb.append(" vibrate=");
        if ((this.defaults & 2) != 0) {
            sb.append("default");
        } else if (this.vibrate != null) {
            int N = this.vibrate.length - 1;
            sb.append("[");
            for (int i = 0; i < N; i++) {
                sb.append(this.vibrate[i]);
                sb.append(',');
            }
            if (N != -1) {
                sb.append(this.vibrate[N]);
            }
            sb.append("]");
        } else {
            sb.append("null");
        }
        sb.append(" sound=");
        if ((this.defaults & 1) != 0) {
            sb.append("default");
        } else if (this.sound != null) {
            sb.append(this.sound.toString());
        } else {
            sb.append("null");
        }
        sb.append(" defaults=0x");
        sb.append(Integer.toHexString(this.defaults));
        sb.append(" flags=0x");
        sb.append(Integer.toHexString(this.flags));
        sb.append(" kind=[");
        if (this.kind == null) {
            sb.append("null");
        } else {
            for (int i2 = 0; i2 < this.kind.length; i2++) {
                if (i2 > 0) {
                    sb.append(Separators.COMMA);
                }
                sb.append(this.kind[i2]);
            }
        }
        sb.append("]");
        if (this.actions != null) {
            sb.append(Separators.SP);
            sb.append(this.actions.length);
            sb.append(" action");
            if (this.actions.length > 1) {
                sb.append("s");
            }
        }
        sb.append(Separators.RPAREN);
        return sb.toString();
    }

    public void setUser(UserHandle user) {
        if (user.getIdentifier() == -1) {
            user = UserHandle.OWNER;
        }
        if (this.tickerView != null) {
            this.tickerView.setUser(user);
        }
        if (this.contentView != null) {
            this.contentView.setUser(user);
        }
        if (this.bigContentView != null) {
            this.bigContentView.setUser(user);
        }
    }

    /* loaded from: Notification$Builder.class */
    public static class Builder {
        private static final int MAX_ACTION_BUTTONS = 3;
        private Context mContext;
        private int mSmallIcon;
        private int mSmallIconLevel;
        private int mNumber;
        private CharSequence mContentTitle;
        private CharSequence mContentText;
        private CharSequence mContentInfo;
        private CharSequence mSubText;
        private PendingIntent mContentIntent;
        private RemoteViews mContentView;
        private PendingIntent mDeleteIntent;
        private PendingIntent mFullScreenIntent;
        private CharSequence mTickerText;
        private RemoteViews mTickerView;
        private Bitmap mLargeIcon;
        private Uri mSound;
        private long[] mVibrate;
        private int mLedArgb;
        private int mLedOnMs;
        private int mLedOffMs;
        private int mDefaults;
        private int mFlags;
        private int mProgressMax;
        private int mProgress;
        private boolean mProgressIndeterminate;
        private Bundle mExtras;
        private boolean mUseChronometer;
        private Style mStyle;
        private ArrayList<String> mKindList = new ArrayList<>(1);
        private ArrayList<Action> mActions = new ArrayList<>(3);
        private boolean mShowWhen = true;
        private long mWhen = System.currentTimeMillis();
        private int mAudioStreamType = -1;
        private int mPriority = 0;

        public Builder(Context context) {
            this.mContext = context;
        }

        public Builder setWhen(long when) {
            this.mWhen = when;
            return this;
        }

        public Builder setShowWhen(boolean show) {
            this.mShowWhen = show;
            return this;
        }

        public Builder setUsesChronometer(boolean b) {
            this.mUseChronometer = b;
            return this;
        }

        public Builder setSmallIcon(int icon) {
            this.mSmallIcon = icon;
            return this;
        }

        public Builder setSmallIcon(int icon, int level) {
            this.mSmallIcon = icon;
            this.mSmallIconLevel = level;
            return this;
        }

        public Builder setContentTitle(CharSequence title) {
            this.mContentTitle = Notification.safeCharSequence(title);
            return this;
        }

        public Builder setContentText(CharSequence text) {
            this.mContentText = Notification.safeCharSequence(text);
            return this;
        }

        public Builder setSubText(CharSequence text) {
            this.mSubText = Notification.safeCharSequence(text);
            return this;
        }

        public Builder setNumber(int number) {
            this.mNumber = number;
            return this;
        }

        public Builder setContentInfo(CharSequence info) {
            this.mContentInfo = Notification.safeCharSequence(info);
            return this;
        }

        public Builder setProgress(int max, int progress, boolean indeterminate) {
            this.mProgressMax = max;
            this.mProgress = progress;
            this.mProgressIndeterminate = indeterminate;
            return this;
        }

        public Builder setContent(RemoteViews views) {
            this.mContentView = views;
            return this;
        }

        public Builder setContentIntent(PendingIntent intent) {
            this.mContentIntent = intent;
            return this;
        }

        public Builder setDeleteIntent(PendingIntent intent) {
            this.mDeleteIntent = intent;
            return this;
        }

        public Builder setFullScreenIntent(PendingIntent intent, boolean highPriority) {
            this.mFullScreenIntent = intent;
            setFlag(128, highPriority);
            return this;
        }

        public Builder setTicker(CharSequence tickerText) {
            this.mTickerText = Notification.safeCharSequence(tickerText);
            return this;
        }

        public Builder setTicker(CharSequence tickerText, RemoteViews views) {
            this.mTickerText = Notification.safeCharSequence(tickerText);
            this.mTickerView = views;
            return this;
        }

        public Builder setLargeIcon(Bitmap icon) {
            this.mLargeIcon = icon;
            return this;
        }

        public Builder setSound(Uri sound) {
            this.mSound = sound;
            this.mAudioStreamType = -1;
            return this;
        }

        public Builder setSound(Uri sound, int streamType) {
            this.mSound = sound;
            this.mAudioStreamType = streamType;
            return this;
        }

        public Builder setVibrate(long[] pattern) {
            this.mVibrate = pattern;
            return this;
        }

        public Builder setLights(int argb, int onMs, int offMs) {
            this.mLedArgb = argb;
            this.mLedOnMs = onMs;
            this.mLedOffMs = offMs;
            return this;
        }

        public Builder setOngoing(boolean ongoing) {
            setFlag(2, ongoing);
            return this;
        }

        public Builder setOnlyAlertOnce(boolean onlyAlertOnce) {
            setFlag(8, onlyAlertOnce);
            return this;
        }

        public Builder setAutoCancel(boolean autoCancel) {
            setFlag(16, autoCancel);
            return this;
        }

        public Builder setDefaults(int defaults) {
            this.mDefaults = defaults;
            return this;
        }

        public Builder setPriority(int pri) {
            this.mPriority = pri;
            return this;
        }

        public Builder addKind(String k) {
            this.mKindList.add(k);
            return this;
        }

        public Builder setExtras(Bundle bag) {
            this.mExtras = bag;
            return this;
        }

        public Builder addAction(int icon, CharSequence title, PendingIntent intent) {
            this.mActions.add(new Action(icon, Notification.safeCharSequence(title), intent));
            return this;
        }

        public Builder setStyle(Style style) {
            if (this.mStyle != style) {
                this.mStyle = style;
                if (this.mStyle != null) {
                    this.mStyle.setBuilder(this);
                }
            }
            return this;
        }

        private void setFlag(int mask, boolean value) {
            if (value) {
                this.mFlags |= mask;
            } else {
                this.mFlags &= mask ^ (-1);
            }
        }

        private RemoteViews applyStandardTemplate(int resId, boolean fitIn1U) {
            RemoteViews contentView = new RemoteViews(this.mContext.getPackageName(), resId);
            boolean showLine3 = false;
            boolean showLine2 = false;
            int smallIconImageViewId = 16908294;
            if (this.mLargeIcon != null) {
                contentView.setImageViewBitmap(16908294, this.mLargeIcon);
                smallIconImageViewId = 16908883;
            }
            if (this.mPriority < -1) {
                contentView.setInt(16908294, "setBackgroundResource", R.drawable.notification_template_icon_low_bg);
                contentView.setInt(R.id.status_bar_latest_event_content, "setBackgroundResource", R.drawable.notification_bg_low);
            }
            if (this.mSmallIcon != 0) {
                contentView.setImageViewResource(smallIconImageViewId, this.mSmallIcon);
                contentView.setViewVisibility(smallIconImageViewId, 0);
            } else {
                contentView.setViewVisibility(smallIconImageViewId, 8);
            }
            if (this.mContentTitle != null) {
                contentView.setTextViewText(16908310, this.mContentTitle);
            }
            if (this.mContentText != null) {
                contentView.setTextViewText(R.id.text, this.mContentText);
                showLine3 = true;
            }
            if (this.mContentInfo != null) {
                contentView.setTextViewText(R.id.info, this.mContentInfo);
                contentView.setViewVisibility(R.id.info, 0);
                showLine3 = true;
            } else if (this.mNumber > 0) {
                int tooBig = this.mContext.getResources().getInteger(17694723);
                if (this.mNumber > tooBig) {
                    contentView.setTextViewText(R.id.info, this.mContext.getResources().getString(17039383));
                } else {
                    NumberFormat f = NumberFormat.getIntegerInstance();
                    contentView.setTextViewText(R.id.info, f.format(this.mNumber));
                }
                contentView.setViewVisibility(R.id.info, 0);
                showLine3 = true;
            } else {
                contentView.setViewVisibility(R.id.info, 8);
            }
            if (this.mSubText != null) {
                contentView.setTextViewText(R.id.text, this.mSubText);
                if (this.mContentText != null) {
                    contentView.setTextViewText(16908309, this.mContentText);
                    contentView.setViewVisibility(16908309, 0);
                    showLine2 = true;
                } else {
                    contentView.setViewVisibility(16908309, 8);
                }
            } else {
                contentView.setViewVisibility(16908309, 8);
                if (this.mProgressMax != 0 || this.mProgressIndeterminate) {
                    contentView.setProgressBar(16908301, this.mProgressMax, this.mProgress, this.mProgressIndeterminate);
                    contentView.setViewVisibility(16908301, 0);
                    showLine2 = true;
                } else {
                    contentView.setViewVisibility(16908301, 8);
                }
            }
            if (showLine2) {
                if (fitIn1U) {
                    Resources res = this.mContext.getResources();
                    float subTextSize = res.getDimensionPixelSize(R.dimen.notification_subtext_size);
                    contentView.setTextViewTextSize(R.id.text, 0, subTextSize);
                }
                contentView.setViewPadding(R.id.line1, 0, 0, 0, 0);
            }
            if (this.mWhen != 0 && this.mShowWhen) {
                if (this.mUseChronometer) {
                    contentView.setViewVisibility(R.id.chronometer, 0);
                    contentView.setLong(R.id.chronometer, "setBase", this.mWhen + (SystemClock.elapsedRealtime() - System.currentTimeMillis()));
                    contentView.setBoolean(R.id.chronometer, "setStarted", true);
                } else {
                    contentView.setViewVisibility(R.id.time, 0);
                    contentView.setLong(R.id.time, "setTime", this.mWhen);
                }
            } else {
                contentView.setViewVisibility(R.id.time, 8);
            }
            contentView.setViewVisibility(R.id.line3, showLine3 ? 0 : 8);
            contentView.setViewVisibility(R.id.overflow_divider, showLine3 ? 0 : 8);
            return contentView;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public RemoteViews applyStandardTemplateWithActions(int layoutId) {
            RemoteViews big = applyStandardTemplate(layoutId, false);
            int N = this.mActions.size();
            if (N > 0) {
                big.setViewVisibility(R.id.actions, 0);
                big.setViewVisibility(R.id.action_divider, 0);
                if (N > 3) {
                    N = 3;
                }
                big.removeAllViews(R.id.actions);
                for (int i = 0; i < N; i++) {
                    RemoteViews button = generateActionButton(this.mActions.get(i));
                    big.addView(R.id.actions, button);
                }
            }
            return big;
        }

        private RemoteViews makeContentView() {
            if (this.mContentView != null) {
                return this.mContentView;
            }
            return applyStandardTemplate(R.layout.notification_template_base, true);
        }

        private RemoteViews makeTickerView() {
            if (this.mTickerView != null) {
                return this.mTickerView;
            }
            if (this.mContentView == null) {
                return applyStandardTemplate(this.mLargeIcon == null ? R.layout.status_bar_latest_event_ticker : R.layout.status_bar_latest_event_ticker_large_icon, true);
            }
            return null;
        }

        private RemoteViews makeBigContentView() {
            if (this.mActions.size() == 0) {
                return null;
            }
            return applyStandardTemplateWithActions(R.layout.notification_template_big_base);
        }

        private RemoteViews generateActionButton(Action action) {
            boolean tombstone = action.actionIntent == null;
            RemoteViews button = new RemoteViews(this.mContext.getPackageName(), tombstone ? R.layout.notification_action_tombstone : R.layout.notification_action);
            button.setTextViewCompoundDrawables(R.id.action0, action.icon, 0, 0, 0);
            button.setTextViewText(R.id.action0, action.title);
            if (!tombstone) {
                button.setOnClickPendingIntent(R.id.action0, action.actionIntent);
            }
            button.setContentDescription(R.id.action0, action.title);
            return button;
        }

        public Notification buildUnstyled() {
            Notification n = new Notification();
            n.when = this.mWhen;
            n.icon = this.mSmallIcon;
            n.iconLevel = this.mSmallIconLevel;
            n.number = this.mNumber;
            n.contentView = makeContentView();
            n.contentIntent = this.mContentIntent;
            n.deleteIntent = this.mDeleteIntent;
            n.fullScreenIntent = this.mFullScreenIntent;
            n.tickerText = this.mTickerText;
            n.tickerView = makeTickerView();
            n.largeIcon = this.mLargeIcon;
            n.sound = this.mSound;
            n.audioStreamType = this.mAudioStreamType;
            n.vibrate = this.mVibrate;
            n.ledARGB = this.mLedArgb;
            n.ledOnMS = this.mLedOnMs;
            n.ledOffMS = this.mLedOffMs;
            n.defaults = this.mDefaults;
            n.flags = this.mFlags;
            n.bigContentView = makeBigContentView();
            if (this.mLedOnMs != 0 || this.mLedOffMs != 0) {
                n.flags |= 1;
            }
            if ((this.mDefaults & 4) != 0) {
                n.flags |= 1;
            }
            if (this.mKindList.size() > 0) {
                n.kind = new String[this.mKindList.size()];
                this.mKindList.toArray(n.kind);
            } else {
                n.kind = null;
            }
            n.priority = this.mPriority;
            if (this.mActions.size() > 0) {
                n.actions = new Action[this.mActions.size()];
                this.mActions.toArray(n.actions);
            }
            return n;
        }

        public void addExtras(Bundle extras) {
            extras.putCharSequence("android.title", this.mContentTitle);
            extras.putCharSequence("android.text", this.mContentText);
            extras.putCharSequence("android.subText", this.mSubText);
            extras.putCharSequence("android.infoText", this.mContentInfo);
            extras.putInt("android.icon", this.mSmallIcon);
            extras.putInt("android.progress", this.mProgress);
            extras.putInt("android.progressMax", this.mProgressMax);
            extras.putBoolean("android.progressIndeterminate", this.mProgressIndeterminate);
            extras.putBoolean("android.showChronometer", this.mUseChronometer);
            extras.putBoolean("android.showWhen", this.mShowWhen);
            if (this.mLargeIcon != null) {
                extras.putParcelable("android.largeIcon", this.mLargeIcon);
            }
        }

        @Deprecated
        public Notification getNotification() {
            return build();
        }

        public Notification build() {
            Notification n = buildUnstyled();
            if (this.mStyle != null) {
                n = this.mStyle.buildStyled(n);
            }
            n.extras = this.mExtras != null ? new Bundle(this.mExtras) : new Bundle();
            addExtras(n.extras);
            if (this.mStyle != null) {
                this.mStyle.addExtras(n.extras);
            }
            return n;
        }

        public Notification buildInto(Notification n) {
            build().cloneInto(n, true);
            return n;
        }
    }

    /* loaded from: Notification$Style.class */
    public static abstract class Style {
        private CharSequence mBigContentTitle;
        private CharSequence mSummaryText = null;
        private boolean mSummaryTextSet = false;
        protected Builder mBuilder;

        public abstract Notification buildStyled(Notification notification);

        protected void internalSetBigContentTitle(CharSequence title) {
            this.mBigContentTitle = title;
        }

        protected void internalSetSummaryText(CharSequence cs) {
            this.mSummaryText = cs;
            this.mSummaryTextSet = true;
        }

        public void setBuilder(Builder builder) {
            if (this.mBuilder != builder) {
                this.mBuilder = builder;
                if (this.mBuilder != null) {
                    this.mBuilder.setStyle(this);
                }
            }
        }

        protected void checkBuilder() {
            if (this.mBuilder == null) {
                throw new IllegalArgumentException("Style requires a valid Builder object");
            }
        }

        protected RemoteViews getStandardView(int layoutId) {
            checkBuilder();
            if (this.mBigContentTitle != null) {
                this.mBuilder.setContentTitle(this.mBigContentTitle);
            }
            RemoteViews contentView = this.mBuilder.applyStandardTemplateWithActions(layoutId);
            if (this.mBigContentTitle != null && this.mBigContentTitle.equals("")) {
                contentView.setViewVisibility(R.id.line1, 8);
            } else {
                contentView.setViewVisibility(R.id.line1, 0);
            }
            CharSequence overflowText = this.mSummaryTextSet ? this.mSummaryText : this.mBuilder.mSubText;
            if (overflowText != null) {
                contentView.setTextViewText(R.id.text, overflowText);
                contentView.setViewVisibility(R.id.overflow_divider, 0);
                contentView.setViewVisibility(R.id.line3, 0);
            } else {
                contentView.setViewVisibility(R.id.overflow_divider, 8);
                contentView.setViewVisibility(R.id.line3, 8);
            }
            return contentView;
        }

        public void addExtras(Bundle extras) {
            if (this.mSummaryTextSet) {
                extras.putCharSequence("android.summaryText", this.mSummaryText);
            }
            if (this.mBigContentTitle != null) {
                extras.putCharSequence("android.title.big", this.mBigContentTitle);
            }
        }

        public Notification build() {
            checkBuilder();
            return this.mBuilder.build();
        }
    }

    /* loaded from: Notification$BigPictureStyle.class */
    public static class BigPictureStyle extends Style {
        private Bitmap mPicture;
        private Bitmap mBigLargeIcon;
        private boolean mBigLargeIconSet = false;

        public BigPictureStyle() {
        }

        public BigPictureStyle(Builder builder) {
            setBuilder(builder);
        }

        public BigPictureStyle setBigContentTitle(CharSequence title) {
            internalSetBigContentTitle(Notification.safeCharSequence(title));
            return this;
        }

        public BigPictureStyle setSummaryText(CharSequence cs) {
            internalSetSummaryText(Notification.safeCharSequence(cs));
            return this;
        }

        public BigPictureStyle bigPicture(Bitmap b) {
            this.mPicture = b;
            return this;
        }

        public BigPictureStyle bigLargeIcon(Bitmap b) {
            this.mBigLargeIconSet = true;
            this.mBigLargeIcon = b;
            return this;
        }

        private RemoteViews makeBigContentView() {
            RemoteViews contentView = getStandardView(R.layout.notification_template_big_picture);
            contentView.setImageViewBitmap(R.id.big_picture, this.mPicture);
            return contentView;
        }

        @Override // android.app.Notification.Style
        public void addExtras(Bundle extras) {
            super.addExtras(extras);
            if (this.mBigLargeIconSet) {
                extras.putParcelable("android.largeIcon.big", this.mBigLargeIcon);
            }
            extras.putParcelable("android.picture", this.mPicture);
        }

        @Override // android.app.Notification.Style
        public Notification buildStyled(Notification wip) {
            if (this.mBigLargeIconSet) {
                this.mBuilder.mLargeIcon = this.mBigLargeIcon;
            }
            wip.bigContentView = makeBigContentView();
            return wip;
        }
    }

    /* loaded from: Notification$BigTextStyle.class */
    public static class BigTextStyle extends Style {
        private CharSequence mBigText;

        public BigTextStyle() {
        }

        public BigTextStyle(Builder builder) {
            setBuilder(builder);
        }

        public BigTextStyle setBigContentTitle(CharSequence title) {
            internalSetBigContentTitle(Notification.safeCharSequence(title));
            return this;
        }

        public BigTextStyle setSummaryText(CharSequence cs) {
            internalSetSummaryText(Notification.safeCharSequence(cs));
            return this;
        }

        public BigTextStyle bigText(CharSequence cs) {
            this.mBigText = Notification.safeCharSequence(cs);
            return this;
        }

        @Override // android.app.Notification.Style
        public void addExtras(Bundle extras) {
            super.addExtras(extras);
            extras.putCharSequence("android.text", this.mBigText);
        }

        private RemoteViews makeBigContentView() {
            boolean hadThreeLines = (this.mBuilder.mContentText == null || this.mBuilder.mSubText == null) ? false : true;
            this.mBuilder.mContentText = null;
            RemoteViews contentView = getStandardView(R.layout.notification_template_big_text);
            if (hadThreeLines) {
                contentView.setViewPadding(R.id.line1, 0, 0, 0, 0);
            }
            contentView.setTextViewText(R.id.big_text, this.mBigText);
            contentView.setViewVisibility(R.id.big_text, 0);
            contentView.setViewVisibility(16908309, 8);
            return contentView;
        }

        @Override // android.app.Notification.Style
        public Notification buildStyled(Notification wip) {
            wip.bigContentView = makeBigContentView();
            wip.extras.putCharSequence("android.text", this.mBigText);
            return wip;
        }
    }

    /* loaded from: Notification$InboxStyle.class */
    public static class InboxStyle extends Style {
        private ArrayList<CharSequence> mTexts = new ArrayList<>(5);

        public InboxStyle() {
        }

        public InboxStyle(Builder builder) {
            setBuilder(builder);
        }

        public InboxStyle setBigContentTitle(CharSequence title) {
            internalSetBigContentTitle(Notification.safeCharSequence(title));
            return this;
        }

        public InboxStyle setSummaryText(CharSequence cs) {
            internalSetSummaryText(Notification.safeCharSequence(cs));
            return this;
        }

        public InboxStyle addLine(CharSequence cs) {
            this.mTexts.add(Notification.safeCharSequence(cs));
            return this;
        }

        @Override // android.app.Notification.Style
        public void addExtras(Bundle extras) {
            super.addExtras(extras);
            CharSequence[] a = new CharSequence[this.mTexts.size()];
            extras.putCharSequenceArray("android.textLines", (CharSequence[]) this.mTexts.toArray(a));
        }

        private RemoteViews makeBigContentView() {
            this.mBuilder.mContentText = null;
            RemoteViews contentView = getStandardView(R.layout.notification_template_inbox);
            contentView.setViewVisibility(16908309, 8);
            int[] rowIds = {R.id.inbox_text0, R.id.inbox_text1, R.id.inbox_text2, R.id.inbox_text3, R.id.inbox_text4, R.id.inbox_text5, R.id.inbox_text6};
            for (int rowId : rowIds) {
                contentView.setViewVisibility(rowId, 8);
            }
            for (int i = 0; i < this.mTexts.size() && i < rowIds.length; i++) {
                CharSequence str = this.mTexts.get(i);
                if (str != null && !str.equals("")) {
                    contentView.setViewVisibility(rowIds[i], 0);
                    contentView.setTextViewText(rowIds[i], str);
                }
            }
            contentView.setViewVisibility(R.id.inbox_end_pad, this.mTexts.size() > 0 ? 0 : 8);
            contentView.setViewVisibility(R.id.inbox_more, this.mTexts.size() > rowIds.length ? 0 : 8);
            return contentView;
        }

        @Override // android.app.Notification.Style
        public Notification buildStyled(Notification wip) {
            wip.bigContentView = makeBigContentView();
            return wip;
        }
    }
}