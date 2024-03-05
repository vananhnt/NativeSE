package android.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RemoteViews;
import com.android.internal.R;
import java.util.TimeZone;

@RemoteViews.RemoteView
/* loaded from: AnalogClock.class */
public class AnalogClock extends View {
    private Time mCalendar;
    private Drawable mHourHand;
    private Drawable mMinuteHand;
    private Drawable mDial;
    private int mDialWidth;
    private int mDialHeight;
    private boolean mAttached;
    private final Handler mHandler;
    private float mMinutes;
    private float mHour;
    private boolean mChanged;
    private final BroadcastReceiver mIntentReceiver;

    public AnalogClock(Context context) {
        this(context, null);
    }

    public AnalogClock(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnalogClock(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mHandler = new Handler();
        this.mIntentReceiver = new BroadcastReceiver() { // from class: android.widget.AnalogClock.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if (intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)) {
                    String tz = intent.getStringExtra("time-zone");
                    AnalogClock.this.mCalendar = new Time(TimeZone.getTimeZone(tz).getID());
                }
                AnalogClock.this.onTimeChanged();
                AnalogClock.this.invalidate();
            }
        };
        Resources r = this.mContext.getResources();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AnalogClock, defStyle, 0);
        this.mDial = a.getDrawable(0);
        if (this.mDial == null) {
            this.mDial = r.getDrawable(R.drawable.clock_dial);
        }
        this.mHourHand = a.getDrawable(1);
        if (this.mHourHand == null) {
            this.mHourHand = r.getDrawable(R.drawable.clock_hand_hour);
        }
        this.mMinuteHand = a.getDrawable(2);
        if (this.mMinuteHand == null) {
            this.mMinuteHand = r.getDrawable(R.drawable.clock_hand_minute);
        }
        this.mCalendar = new Time();
        this.mDialWidth = this.mDial.getIntrinsicWidth();
        this.mDialHeight = this.mDial.getIntrinsicHeight();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!this.mAttached) {
            this.mAttached = true;
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_TIME_TICK);
            filter.addAction(Intent.ACTION_TIME_CHANGED);
            filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
            getContext().registerReceiver(this.mIntentReceiver, filter, null, this.mHandler);
        }
        this.mCalendar = new Time();
        onTimeChanged();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mAttached) {
            getContext().unregisterReceiver(this.mIntentReceiver);
            this.mAttached = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        float hScale = 1.0f;
        float vScale = 1.0f;
        if (widthMode != 0 && widthSize < this.mDialWidth) {
            hScale = widthSize / this.mDialWidth;
        }
        if (heightMode != 0 && heightSize < this.mDialHeight) {
            vScale = heightSize / this.mDialHeight;
        }
        float scale = Math.min(hScale, vScale);
        setMeasuredDimension(resolveSizeAndState((int) (this.mDialWidth * scale), widthMeasureSpec, 0), resolveSizeAndState((int) (this.mDialHeight * scale), heightMeasureSpec, 0));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mChanged = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        boolean changed = this.mChanged;
        if (changed) {
            this.mChanged = false;
        }
        int availableWidth = this.mRight - this.mLeft;
        int availableHeight = this.mBottom - this.mTop;
        int x = availableWidth / 2;
        int y = availableHeight / 2;
        Drawable dial = this.mDial;
        int w = dial.getIntrinsicWidth();
        int h = dial.getIntrinsicHeight();
        boolean scaled = false;
        if (availableWidth < w || availableHeight < h) {
            scaled = true;
            float scale = Math.min(availableWidth / w, availableHeight / h);
            canvas.save();
            canvas.scale(scale, scale, x, y);
        }
        if (changed) {
            dial.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
        }
        dial.draw(canvas);
        canvas.save();
        canvas.rotate((this.mHour / 12.0f) * 360.0f, x, y);
        Drawable hourHand = this.mHourHand;
        if (changed) {
            int w2 = hourHand.getIntrinsicWidth();
            int h2 = hourHand.getIntrinsicHeight();
            hourHand.setBounds(x - (w2 / 2), y - (h2 / 2), x + (w2 / 2), y + (h2 / 2));
        }
        hourHand.draw(canvas);
        canvas.restore();
        canvas.save();
        canvas.rotate((this.mMinutes / 60.0f) * 360.0f, x, y);
        Drawable minuteHand = this.mMinuteHand;
        if (changed) {
            int w3 = minuteHand.getIntrinsicWidth();
            int h3 = minuteHand.getIntrinsicHeight();
            minuteHand.setBounds(x - (w3 / 2), y - (h3 / 2), x + (w3 / 2), y + (h3 / 2));
        }
        minuteHand.draw(canvas);
        canvas.restore();
        if (scaled) {
            canvas.restore();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onTimeChanged() {
        this.mCalendar.setToNow();
        int hour = this.mCalendar.hour;
        int minute = this.mCalendar.minute;
        int second = this.mCalendar.second;
        this.mMinutes = minute + (second / 60.0f);
        this.mHour = hour + (this.mMinutes / 60.0f);
        this.mChanged = true;
        updateContentDescription(this.mCalendar);
    }

    private void updateContentDescription(Time time) {
        String contentDescription = DateUtils.formatDateTime(this.mContext, time.toMillis(false), 129);
        setContentDescription(contentDescription);
    }
}