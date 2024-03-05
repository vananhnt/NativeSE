package android.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.RemotableViewMethod;
import android.widget.RemoteViews;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@RemoteViews.RemoteView
/* loaded from: DateTimeView.class */
public class DateTimeView extends TextView {
    private static final String TAG = "DateTimeView";
    private static final long TWELVE_HOURS_IN_MINUTES = 720;
    private static final long TWENTY_FOUR_HOURS_IN_MILLIS = 86400000;
    private static final int SHOW_TIME = 0;
    private static final int SHOW_MONTH_DAY_YEAR = 1;
    Date mTime;
    long mTimeMillis;
    int mLastDisplay;
    DateFormat mLastFormat;
    private boolean mAttachedToWindow;
    private long mUpdateTimeMillis;
    private BroadcastReceiver mBroadcastReceiver;
    private ContentObserver mContentObserver;

    public DateTimeView(Context context) {
        super(context);
        this.mLastDisplay = -1;
        this.mBroadcastReceiver = new BroadcastReceiver() { // from class: android.widget.DateTimeView.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                String action = intent.getAction();
                if (Intent.ACTION_TIME_TICK.equals(action) && System.currentTimeMillis() < DateTimeView.this.mUpdateTimeMillis) {
                    return;
                }
                DateTimeView.this.mLastFormat = null;
                DateTimeView.this.update();
            }
        };
        this.mContentObserver = new ContentObserver(new Handler()) { // from class: android.widget.DateTimeView.2
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange) {
                DateTimeView.this.mLastFormat = null;
                DateTimeView.this.update();
            }
        };
    }

    public DateTimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mLastDisplay = -1;
        this.mBroadcastReceiver = new BroadcastReceiver() { // from class: android.widget.DateTimeView.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                String action = intent.getAction();
                if (Intent.ACTION_TIME_TICK.equals(action) && System.currentTimeMillis() < DateTimeView.this.mUpdateTimeMillis) {
                    return;
                }
                DateTimeView.this.mLastFormat = null;
                DateTimeView.this.update();
            }
        };
        this.mContentObserver = new ContentObserver(new Handler()) { // from class: android.widget.DateTimeView.2
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange) {
                DateTimeView.this.mLastFormat = null;
                DateTimeView.this.update();
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.TextView, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        registerReceivers();
        this.mAttachedToWindow = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.TextView, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        unregisterReceivers();
        this.mAttachedToWindow = false;
    }

    @RemotableViewMethod
    public void setTime(long time) {
        Time t = new Time();
        t.set(time);
        t.second = 0;
        this.mTimeMillis = t.toMillis(false);
        this.mTime = new Date(t.year - 1900, t.month, t.monthDay, t.hour, t.minute, 0);
        update();
    }

    void update() {
        int display;
        DateFormat format;
        if (this.mTime == null) {
            return;
        }
        System.nanoTime();
        Date date = this.mTime;
        Time t = new Time();
        t.set(this.mTimeMillis);
        t.second = 0;
        t.hour -= 12;
        long twelveHoursBefore = t.toMillis(false);
        t.hour += 12;
        long twelveHoursAfter = t.toMillis(false);
        t.hour = 0;
        t.minute = 0;
        long midnightBefore = t.toMillis(false);
        t.monthDay++;
        long midnightAfter = t.toMillis(false);
        t.set(System.currentTimeMillis());
        t.second = 0;
        long nowMillis = t.normalize(false);
        if ((nowMillis >= midnightBefore && nowMillis < midnightAfter) || (nowMillis >= twelveHoursBefore && nowMillis < twelveHoursAfter)) {
            display = 0;
        } else {
            display = 1;
        }
        if (display == this.mLastDisplay && this.mLastFormat != null) {
            format = this.mLastFormat;
        } else {
            switch (display) {
                case 0:
                    format = getTimeFormat();
                    break;
                case 1:
                    format = getDateFormat();
                    break;
                default:
                    throw new RuntimeException("unknown display value: " + display);
            }
            this.mLastFormat = format;
        }
        String text = format.format(this.mTime);
        setText(text);
        if (display == 0) {
            this.mUpdateTimeMillis = twelveHoursAfter > midnightAfter ? twelveHoursAfter : midnightAfter;
        } else if (this.mTimeMillis < nowMillis) {
            this.mUpdateTimeMillis = 0L;
        } else {
            this.mUpdateTimeMillis = twelveHoursBefore < midnightBefore ? twelveHoursBefore : midnightBefore;
        }
        System.nanoTime();
    }

    private DateFormat getTimeFormat() {
        return android.text.format.DateFormat.getTimeFormat(getContext());
    }

    private DateFormat getDateFormat() {
        String format = Settings.System.getString(getContext().getContentResolver(), Settings.System.DATE_FORMAT);
        if (format == null || "".equals(format)) {
            return DateFormat.getDateInstance(3);
        }
        try {
            return new SimpleDateFormat(format);
        } catch (IllegalArgumentException e) {
            return DateFormat.getDateInstance(3);
        }
    }

    private void registerReceivers() {
        Context context = getContext();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        context.registerReceiver(this.mBroadcastReceiver, filter);
        Uri uri = Settings.System.getUriFor(Settings.System.DATE_FORMAT);
        context.getContentResolver().registerContentObserver(uri, true, this.mContentObserver);
    }

    private void unregisterReceivers() {
        Context context = getContext();
        context.unregisterReceiver(this.mBroadcastReceiver);
        context.getContentResolver().unregisterContentObserver(this.mContentObserver);
    }
}