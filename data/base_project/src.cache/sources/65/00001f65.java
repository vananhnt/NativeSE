package com.android.server.power;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.FloatProperty;
import android.util.IntProperty;
import android.util.Slog;
import android.view.Choreographer;
import com.android.server.LightsService;
import java.io.PrintWriter;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: DisplayPowerState.class */
public final class DisplayPowerState {
    private static final String TAG = "DisplayPowerState";
    private final ElectronBeam mElectronBeam;
    private final DisplayBlanker mDisplayBlanker;
    private final LightsService.Light mBacklight;
    private boolean mScreenReady;
    private boolean mScreenUpdatePending;
    private boolean mElectronBeamPrepared;
    private float mElectronBeamLevel;
    private boolean mElectronBeamReady;
    private boolean mElectronBeamDrawPending;
    private Runnable mCleanListener;
    private static boolean DEBUG = false;
    public static final FloatProperty<DisplayPowerState> ELECTRON_BEAM_LEVEL = new FloatProperty<DisplayPowerState>("electronBeamLevel") { // from class: com.android.server.power.DisplayPowerState.1
        @Override // android.util.FloatProperty
        public void setValue(DisplayPowerState object, float value) {
            object.setElectronBeamLevel(value);
        }

        @Override // android.util.Property
        public Float get(DisplayPowerState object) {
            return Float.valueOf(object.getElectronBeamLevel());
        }
    };
    public static final IntProperty<DisplayPowerState> SCREEN_BRIGHTNESS = new IntProperty<DisplayPowerState>("screenBrightness") { // from class: com.android.server.power.DisplayPowerState.2
        @Override // android.util.IntProperty
        public void setValue(DisplayPowerState object, int value) {
            object.setScreenBrightness(value);
        }

        @Override // android.util.Property
        public Integer get(DisplayPowerState object) {
            return Integer.valueOf(object.getScreenBrightness());
        }
    };
    private final Runnable mScreenUpdateRunnable = new Runnable() { // from class: com.android.server.power.DisplayPowerState.3
        @Override // java.lang.Runnable
        public void run() {
            DisplayPowerState.this.mScreenUpdatePending = false;
            int brightness = (!DisplayPowerState.this.mScreenOn || DisplayPowerState.this.mElectronBeamLevel <= 0.0f) ? 0 : DisplayPowerState.this.mScreenBrightness;
            if (DisplayPowerState.this.mPhotonicModulator.setState(DisplayPowerState.this.mScreenOn, brightness)) {
                DisplayPowerState.this.mScreenReady = true;
                DisplayPowerState.this.invokeCleanListenerIfNeeded();
            }
        }
    };
    private final Runnable mElectronBeamDrawRunnable = new Runnable() { // from class: com.android.server.power.DisplayPowerState.4
        @Override // java.lang.Runnable
        public void run() {
            DisplayPowerState.this.mElectronBeamDrawPending = false;
            if (DisplayPowerState.this.mElectronBeamPrepared) {
                DisplayPowerState.this.mElectronBeam.draw(DisplayPowerState.this.mElectronBeamLevel);
            }
            DisplayPowerState.this.mElectronBeamReady = true;
            DisplayPowerState.this.invokeCleanListenerIfNeeded();
        }
    };
    private final Handler mHandler = new Handler(true);
    private final Choreographer mChoreographer = Choreographer.getInstance();
    private final PhotonicModulator mPhotonicModulator = new PhotonicModulator();
    private boolean mScreenOn = true;
    private int mScreenBrightness = 255;

    public DisplayPowerState(ElectronBeam electronBean, DisplayBlanker displayBlanker, LightsService.Light backlight) {
        this.mElectronBeam = electronBean;
        this.mDisplayBlanker = displayBlanker;
        this.mBacklight = backlight;
        scheduleScreenUpdate();
        this.mElectronBeamPrepared = false;
        this.mElectronBeamLevel = 1.0f;
        this.mElectronBeamReady = true;
    }

    public void setScreenOn(boolean on) {
        if (this.mScreenOn != on) {
            if (DEBUG) {
                Slog.d(TAG, "setScreenOn: on=" + on);
            }
            this.mScreenOn = on;
            this.mScreenReady = false;
            scheduleScreenUpdate();
        }
    }

    public boolean isScreenOn() {
        return this.mScreenOn;
    }

    public void setScreenBrightness(int brightness) {
        if (this.mScreenBrightness != brightness) {
            if (DEBUG) {
                Slog.d(TAG, "setScreenBrightness: brightness=" + brightness);
            }
            this.mScreenBrightness = brightness;
            if (this.mScreenOn) {
                this.mScreenReady = false;
                scheduleScreenUpdate();
            }
        }
    }

    public int getScreenBrightness() {
        return this.mScreenBrightness;
    }

    public boolean prepareElectronBeam(int mode) {
        if (!this.mElectronBeam.prepare(mode)) {
            this.mElectronBeamPrepared = false;
            this.mElectronBeamReady = true;
            return false;
        }
        this.mElectronBeamPrepared = true;
        this.mElectronBeamReady = false;
        scheduleElectronBeamDraw();
        return true;
    }

    public void dismissElectronBeam() {
        this.mElectronBeam.dismiss();
        this.mElectronBeamPrepared = false;
        this.mElectronBeamReady = true;
    }

    public void setElectronBeamLevel(float level) {
        if (this.mElectronBeamLevel != level) {
            if (DEBUG) {
                Slog.d(TAG, "setElectronBeamLevel: level=" + level);
            }
            this.mElectronBeamLevel = level;
            if (this.mScreenOn) {
                this.mScreenReady = false;
                scheduleScreenUpdate();
            }
            if (this.mElectronBeamPrepared) {
                this.mElectronBeamReady = false;
                scheduleElectronBeamDraw();
            }
        }
    }

    public float getElectronBeamLevel() {
        return this.mElectronBeamLevel;
    }

    public boolean waitUntilClean(Runnable listener) {
        if (!this.mScreenReady || !this.mElectronBeamReady) {
            this.mCleanListener = listener;
            return false;
        }
        this.mCleanListener = null;
        return true;
    }

    public void dump(PrintWriter pw) {
        pw.println();
        pw.println("Display Power State:");
        pw.println("  mScreenOn=" + this.mScreenOn);
        pw.println("  mScreenBrightness=" + this.mScreenBrightness);
        pw.println("  mScreenReady=" + this.mScreenReady);
        pw.println("  mScreenUpdatePending=" + this.mScreenUpdatePending);
        pw.println("  mElectronBeamPrepared=" + this.mElectronBeamPrepared);
        pw.println("  mElectronBeamLevel=" + this.mElectronBeamLevel);
        pw.println("  mElectronBeamReady=" + this.mElectronBeamReady);
        pw.println("  mElectronBeamDrawPending=" + this.mElectronBeamDrawPending);
        this.mPhotonicModulator.dump(pw);
        this.mElectronBeam.dump(pw);
    }

    private void scheduleScreenUpdate() {
        if (!this.mScreenUpdatePending) {
            this.mScreenUpdatePending = true;
            postScreenUpdateThreadSafe();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void postScreenUpdateThreadSafe() {
        this.mHandler.removeCallbacks(this.mScreenUpdateRunnable);
        this.mHandler.post(this.mScreenUpdateRunnable);
    }

    private void scheduleElectronBeamDraw() {
        if (!this.mElectronBeamDrawPending) {
            this.mElectronBeamDrawPending = true;
            this.mChoreographer.postCallback(2, this.mElectronBeamDrawRunnable, null);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void invokeCleanListenerIfNeeded() {
        Runnable listener = this.mCleanListener;
        if (listener != null && this.mScreenReady && this.mElectronBeamReady) {
            this.mCleanListener = null;
            listener.run();
        }
    }

    /* loaded from: DisplayPowerState$PhotonicModulator.class */
    private final class PhotonicModulator {
        private static final boolean INITIAL_SCREEN_ON = false;
        private static final int INITIAL_BACKLIGHT = -1;
        private final Object mLock;
        private boolean mPendingOn;
        private int mPendingBacklight;
        private boolean mActualOn;
        private int mActualBacklight;
        private boolean mChangeInProgress;
        private final Runnable mTask;

        private PhotonicModulator() {
            this.mLock = new Object();
            this.mPendingOn = false;
            this.mPendingBacklight = -1;
            this.mActualOn = false;
            this.mActualBacklight = -1;
            this.mTask = new Runnable() { // from class: com.android.server.power.DisplayPowerState.PhotonicModulator.1
                @Override // java.lang.Runnable
                public void run() {
                    boolean on;
                    boolean onChanged;
                    int backlight;
                    boolean backlightChanged;
                    while (true) {
                        synchronized (PhotonicModulator.this.mLock) {
                            on = PhotonicModulator.this.mPendingOn;
                            onChanged = on != PhotonicModulator.this.mActualOn;
                            backlight = PhotonicModulator.this.mPendingBacklight;
                            backlightChanged = backlight != PhotonicModulator.this.mActualBacklight;
                            if (onChanged || backlightChanged) {
                                PhotonicModulator.this.mActualOn = on;
                                PhotonicModulator.this.mActualBacklight = backlight;
                            } else {
                                PhotonicModulator.this.mChangeInProgress = false;
                                DisplayPowerState.this.postScreenUpdateThreadSafe();
                                return;
                            }
                        }
                        if (DisplayPowerState.DEBUG) {
                            Slog.d(DisplayPowerState.TAG, "Updating screen state: on=" + on + ", backlight=" + backlight);
                        }
                        if (onChanged && on) {
                            DisplayPowerState.this.mDisplayBlanker.unblankAllDisplays();
                        }
                        if (backlightChanged) {
                            DisplayPowerState.this.mBacklight.setBrightness(backlight);
                        }
                        if (onChanged && !on) {
                            DisplayPowerState.this.mDisplayBlanker.blankAllDisplays();
                        }
                    }
                }
            };
        }

        public boolean setState(boolean on, int backlight) {
            boolean z;
            synchronized (this.mLock) {
                if (on != this.mPendingOn || backlight != this.mPendingBacklight) {
                    if (DisplayPowerState.DEBUG) {
                        Slog.d(DisplayPowerState.TAG, "Requesting new screen state: on=" + on + ", backlight=" + backlight);
                    }
                    this.mPendingOn = on;
                    this.mPendingBacklight = backlight;
                    if (!this.mChangeInProgress) {
                        this.mChangeInProgress = true;
                        AsyncTask.THREAD_POOL_EXECUTOR.execute(this.mTask);
                    }
                }
                z = this.mChangeInProgress;
            }
            return z;
        }

        public void dump(PrintWriter pw) {
            pw.println();
            pw.println("Photonic Modulator State:");
            pw.println("  mPendingOn=" + this.mPendingOn);
            pw.println("  mPendingBacklight=" + this.mPendingBacklight);
            pw.println("  mActualOn=" + this.mActualOn);
            pw.println("  mActualBacklight=" + this.mActualBacklight);
            pw.println("  mChangeInProgress=" + this.mChangeInProgress);
        }
    }
}