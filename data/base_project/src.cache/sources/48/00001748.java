package android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.RemotableViewMethod;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.RemoteViews;
import com.android.internal.R;
import java.util.Formatter;
import java.util.IllegalFormatException;
import java.util.Locale;

@RemoteViews.RemoteView
/* loaded from: Chronometer.class */
public class Chronometer extends TextView {
    private static final String TAG = "Chronometer";
    private long mBase;
    private boolean mVisible;
    private boolean mStarted;
    private boolean mRunning;
    private boolean mLogged;
    private String mFormat;
    private Formatter mFormatter;
    private Locale mFormatterLocale;
    private Object[] mFormatterArgs;
    private StringBuilder mFormatBuilder;
    private OnChronometerTickListener mOnChronometerTickListener;
    private StringBuilder mRecycle;
    private static final int TICK_WHAT = 2;
    private Handler mHandler;

    /* loaded from: Chronometer$OnChronometerTickListener.class */
    public interface OnChronometerTickListener {
        void onChronometerTick(Chronometer chronometer);
    }

    public Chronometer(Context context) {
        this(context, null, 0);
    }

    public Chronometer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Chronometer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mFormatterArgs = new Object[1];
        this.mRecycle = new StringBuilder(8);
        this.mHandler = new Handler() { // from class: android.widget.Chronometer.1
            @Override // android.os.Handler
            public void handleMessage(Message m) {
                if (Chronometer.this.mRunning) {
                    Chronometer.this.updateText(SystemClock.elapsedRealtime());
                    Chronometer.this.dispatchChronometerTick();
                    sendMessageDelayed(Message.obtain(this, 2), 1000L);
                }
            }
        };
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Chronometer, defStyle, 0);
        setFormat(a.getString(0));
        a.recycle();
        init();
    }

    private void init() {
        this.mBase = SystemClock.elapsedRealtime();
        updateText(this.mBase);
    }

    @RemotableViewMethod
    public void setBase(long base) {
        this.mBase = base;
        dispatchChronometerTick();
        updateText(SystemClock.elapsedRealtime());
    }

    public long getBase() {
        return this.mBase;
    }

    @RemotableViewMethod
    public void setFormat(String format) {
        this.mFormat = format;
        if (format != null && this.mFormatBuilder == null) {
            this.mFormatBuilder = new StringBuilder(format.length() * 2);
        }
    }

    public String getFormat() {
        return this.mFormat;
    }

    public void setOnChronometerTickListener(OnChronometerTickListener listener) {
        this.mOnChronometerTickListener = listener;
    }

    public OnChronometerTickListener getOnChronometerTickListener() {
        return this.mOnChronometerTickListener;
    }

    public void start() {
        this.mStarted = true;
        updateRunning();
    }

    public void stop() {
        this.mStarted = false;
        updateRunning();
    }

    @RemotableViewMethod
    public void setStarted(boolean started) {
        this.mStarted = started;
        updateRunning();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.TextView, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mVisible = false;
        updateRunning();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        this.mVisible = visibility == 0;
        updateRunning();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void updateText(long now) {
        long seconds = now - this.mBase;
        String text = DateUtils.formatElapsedTime(this.mRecycle, seconds / 1000);
        if (this.mFormat != null) {
            Locale loc = Locale.getDefault();
            if (this.mFormatter == null || !loc.equals(this.mFormatterLocale)) {
                this.mFormatterLocale = loc;
                this.mFormatter = new Formatter(this.mFormatBuilder, loc);
            }
            this.mFormatBuilder.setLength(0);
            this.mFormatterArgs[0] = text;
            try {
                this.mFormatter.format(this.mFormat, this.mFormatterArgs);
                text = this.mFormatBuilder.toString();
            } catch (IllegalFormatException e) {
                if (!this.mLogged) {
                    Log.w(TAG, "Illegal format string: " + this.mFormat);
                    this.mLogged = true;
                }
            }
        }
        setText(text);
    }

    private void updateRunning() {
        boolean running = this.mVisible && this.mStarted;
        if (running != this.mRunning) {
            if (running) {
                updateText(SystemClock.elapsedRealtime());
                dispatchChronometerTick();
                this.mHandler.sendMessageDelayed(Message.obtain(this.mHandler, 2), 1000L);
            } else {
                this.mHandler.removeMessages(2);
            }
            this.mRunning = running;
        }
    }

    void dispatchChronometerTick() {
        if (this.mOnChronometerTickListener != null) {
            this.mOnChronometerTickListener.onChronometerTick(this);
        }
    }

    @Override // android.widget.TextView, android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(Chronometer.class.getName());
    }

    @Override // android.widget.TextView, android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(Chronometer.class.getName());
    }
}