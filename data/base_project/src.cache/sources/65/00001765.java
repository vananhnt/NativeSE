package android.widget;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.Calendar;

@Deprecated
/* loaded from: DigitalClock.class */
public class DigitalClock extends TextView {
    Calendar mCalendar;
    private FormatChangeObserver mFormatChangeObserver;
    private Runnable mTicker;
    private Handler mHandler;
    private boolean mTickerStopped;
    String mFormat;

    public DigitalClock(Context context) {
        super(context);
        this.mTickerStopped = false;
        initClock();
    }

    public DigitalClock(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTickerStopped = false;
        initClock();
    }

    private void initClock() {
        if (this.mCalendar == null) {
            this.mCalendar = Calendar.getInstance();
        }
        this.mFormatChangeObserver = new FormatChangeObserver();
        getContext().getContentResolver().registerContentObserver(Settings.System.CONTENT_URI, true, this.mFormatChangeObserver);
        setFormat();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.TextView, android.view.View
    public void onAttachedToWindow() {
        this.mTickerStopped = false;
        super.onAttachedToWindow();
        this.mHandler = new Handler();
        this.mTicker = new Runnable() { // from class: android.widget.DigitalClock.1
            @Override // java.lang.Runnable
            public void run() {
                if (DigitalClock.this.mTickerStopped) {
                    return;
                }
                DigitalClock.this.mCalendar.setTimeInMillis(System.currentTimeMillis());
                DigitalClock.this.setText(DateFormat.format(DigitalClock.this.mFormat, DigitalClock.this.mCalendar));
                DigitalClock.this.invalidate();
                long now = SystemClock.uptimeMillis();
                long next = now + (1000 - (now % 1000));
                DigitalClock.this.mHandler.postAtTime(DigitalClock.this.mTicker, next);
            }
        };
        this.mTicker.run();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.TextView, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mTickerStopped = true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setFormat() {
        this.mFormat = DateFormat.getTimeFormatString(getContext());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: DigitalClock$FormatChangeObserver.class */
    public class FormatChangeObserver extends ContentObserver {
        public FormatChangeObserver() {
            super(new Handler());
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            DigitalClock.this.setFormat();
        }
    }

    @Override // android.widget.TextView, android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(DigitalClock.class.getName());
    }

    @Override // android.widget.TextView, android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(DigitalClock.class.getName());
    }
}