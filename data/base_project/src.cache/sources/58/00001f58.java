package com.android.server.power;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.FloatMath;
import android.util.Slog;
import android.util.Spline;
import android.util.TimeUtils;
import com.android.internal.R;
import com.android.server.LightsService;
import com.android.server.TwilightService;
import com.android.server.display.DisplayManagerService;
import java.io.PrintWriter;
import javax.sip.header.SubscriptionStateHeader;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: DisplayPowerController.class */
public final class DisplayPowerController {
    private static final String TAG = "DisplayPowerController";
    private static final boolean DEBUG_PRETEND_PROXIMITY_SENSOR_ABSENT = false;
    private static final boolean DEBUG_PRETEND_LIGHT_SENSOR_ABSENT = false;
    private static final boolean USE_ELECTRON_BEAM_ON_ANIMATION = false;
    private static final float SCREEN_AUTO_BRIGHTNESS_ADJUSTMENT_MAX_GAMMA = 3.0f;
    private static final int SCREEN_DIM_MINIMUM_REDUCTION = 10;
    private static final float TWILIGHT_ADJUSTMENT_MAX_GAMMA = 1.5f;
    private static final long TWILIGHT_ADJUSTMENT_TIME = 7200000;
    private static final int ELECTRON_BEAM_ON_ANIMATION_DURATION_MILLIS = 250;
    private static final int ELECTRON_BEAM_OFF_ANIMATION_DURATION_MILLIS = 400;
    private static final int MSG_UPDATE_POWER_STATE = 1;
    private static final int MSG_PROXIMITY_SENSOR_DEBOUNCED = 2;
    private static final int MSG_LIGHT_SENSOR_DEBOUNCED = 3;
    private static final int PROXIMITY_UNKNOWN = -1;
    private static final int PROXIMITY_NEGATIVE = 0;
    private static final int PROXIMITY_POSITIVE = 1;
    private static final int PROXIMITY_SENSOR_POSITIVE_DEBOUNCE_DELAY = 0;
    private static final int PROXIMITY_SENSOR_NEGATIVE_DEBOUNCE_DELAY = 250;
    private static final float TYPICAL_PROXIMITY_THRESHOLD = 5.0f;
    private static final int LIGHT_SENSOR_RATE_MILLIS = 1000;
    private static final int SYNTHETIC_LIGHT_SENSOR_RATE_MILLIS = 2000;
    private static final int BRIGHTNESS_RAMP_RATE_FAST = 200;
    private static final int BRIGHTNESS_RAMP_RATE_SLOW = 40;
    private static final long SHORT_TERM_AVERAGE_LIGHT_TIME_CONSTANT = 1000;
    private static final long LONG_TERM_AVERAGE_LIGHT_TIME_CONSTANT = 5000;
    private static final long BRIGHTENING_LIGHT_DEBOUNCE = 4000;
    private static final long DARKENING_LIGHT_DEBOUNCE = 8000;
    private static final float BRIGHTENING_LIGHT_HYSTERESIS = 0.1f;
    private static final float DARKENING_LIGHT_HYSTERESIS = 0.2f;
    private final Notifier mNotifier;
    private final SuspendBlocker mDisplaySuspendBlocker;
    private final DisplayBlanker mDisplayBlanker;
    private final DisplayControllerHandler mHandler;
    private final Callbacks mCallbacks;
    private Handler mCallbackHandler;
    private final LightsService mLights;
    private final TwilightService mTwilight;
    private final DisplayManagerService mDisplayManager;
    private final SensorManager mSensorManager;
    private Sensor mProximitySensor;
    private Sensor mLightSensor;
    private final int mScreenBrightnessDimConfig;
    private final int mScreenBrightnessRangeMinimum;
    private final int mScreenBrightnessRangeMaximum;
    private boolean mUseSoftwareAutoBrightnessConfig;
    private Spline mScreenAutoBrightnessSpline;
    private int mLightSensorWarmUpTimeConfig;
    private boolean mElectronBeamFadesConfig;
    private DisplayPowerRequest mPendingRequestLocked;
    private boolean mPendingWaitForNegativeProximityLocked;
    private boolean mPendingRequestChangedLocked;
    private boolean mDisplayReadyLocked;
    private boolean mPendingUpdatePowerStateLocked;
    private DisplayPowerRequest mPowerRequest;
    private DisplayPowerState mPowerState;
    private boolean mWaitingForNegativeProximity;
    private float mProximityThreshold;
    private boolean mProximitySensorEnabled;
    private boolean mScreenOffBecauseOfProximity;
    private boolean mScreenOnWasBlocked;
    private long mScreenOnBlockStartRealTime;
    private boolean mLightSensorEnabled;
    private long mLightSensorEnableTime;
    private float mAmbientLux;
    private boolean mAmbientLuxValid;
    private float mLastObservedLux;
    private long mLastObservedLuxTime;
    private int mRecentLightSamples;
    private float mRecentShortTermAverageLux;
    private float mRecentLongTermAverageLux;
    private int mDebounceLuxDirection;
    private long mDebounceLuxTime;
    private boolean mUsingScreenAutoBrightness;
    private ObjectAnimator mElectronBeamOnAnimator;
    private ObjectAnimator mElectronBeamOffAnimator;
    private RampAnimator<DisplayPowerState> mScreenBrightnessRampAnimator;
    private boolean mTwilightChanged;
    private static boolean DEBUG = false;
    private static final boolean USE_SCREEN_AUTO_BRIGHTNESS_ADJUSTMENT = PowerManager.useScreenAutoBrightnessAdjustmentFeature();
    private static final boolean USE_TWILIGHT_ADJUSTMENT = PowerManager.useTwilightAdjustmentFeature();
    private final Object mLock = new Object();
    private int mProximity = -1;
    private int mPendingProximity = -1;
    private long mPendingProximityDebounceTime = -1;
    private int mScreenAutoBrightness = -1;
    private float mLastScreenAutoBrightnessGamma = 1.0f;
    private final Animator.AnimatorListener mAnimatorListener = new Animator.AnimatorListener() { // from class: com.android.server.power.DisplayPowerController.1
        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationStart(Animator animation) {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animation) {
            DisplayPowerController.this.sendUpdatePowerState();
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationRepeat(Animator animation) {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationCancel(Animator animation) {
        }
    };
    private final Runnable mCleanListener = new Runnable() { // from class: com.android.server.power.DisplayPowerController.2
        @Override // java.lang.Runnable
        public void run() {
            DisplayPowerController.this.sendUpdatePowerState();
        }
    };
    private final Runnable mOnStateChangedRunnable = new Runnable() { // from class: com.android.server.power.DisplayPowerController.3
        @Override // java.lang.Runnable
        public void run() {
            DisplayPowerController.this.mCallbacks.onStateChanged();
            DisplayPowerController.this.mDisplaySuspendBlocker.release();
        }
    };
    private final Runnable mOnProximityPositiveRunnable = new Runnable() { // from class: com.android.server.power.DisplayPowerController.4
        @Override // java.lang.Runnable
        public void run() {
            DisplayPowerController.this.mCallbacks.onProximityPositive();
            DisplayPowerController.this.mDisplaySuspendBlocker.release();
        }
    };
    private final Runnable mOnProximityNegativeRunnable = new Runnable() { // from class: com.android.server.power.DisplayPowerController.5
        @Override // java.lang.Runnable
        public void run() {
            DisplayPowerController.this.mCallbacks.onProximityNegative();
            DisplayPowerController.this.mDisplaySuspendBlocker.release();
        }
    };
    private final SensorEventListener mProximitySensorListener = new SensorEventListener() { // from class: com.android.server.power.DisplayPowerController.7
        @Override // android.hardware.SensorEventListener
        public void onSensorChanged(SensorEvent event) {
            if (DisplayPowerController.this.mProximitySensorEnabled) {
                long time = SystemClock.uptimeMillis();
                float distance = event.values[0];
                boolean positive = distance >= 0.0f && distance < DisplayPowerController.this.mProximityThreshold;
                DisplayPowerController.this.handleProximitySensorEvent(time, positive);
            }
        }

        @Override // android.hardware.SensorEventListener
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };
    private final SensorEventListener mLightSensorListener = new SensorEventListener() { // from class: com.android.server.power.DisplayPowerController.8
        @Override // android.hardware.SensorEventListener
        public void onSensorChanged(SensorEvent event) {
            if (DisplayPowerController.this.mLightSensorEnabled) {
                long time = SystemClock.uptimeMillis();
                float lux = event.values[0];
                DisplayPowerController.this.handleLightSensorEvent(time, lux);
            }
        }

        @Override // android.hardware.SensorEventListener
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };
    private final TwilightService.TwilightListener mTwilightListener = new TwilightService.TwilightListener() { // from class: com.android.server.power.DisplayPowerController.9
        @Override // com.android.server.TwilightService.TwilightListener
        public void onTwilightStateChanged() {
            DisplayPowerController.this.mTwilightChanged = true;
            DisplayPowerController.this.updatePowerState();
        }
    };

    /* loaded from: DisplayPowerController$Callbacks.class */
    public interface Callbacks {
        void onStateChanged();

        void onProximityPositive();

        void onProximityNegative();
    }

    public DisplayPowerController(Looper looper, Context context, Notifier notifier, LightsService lights, TwilightService twilight, SensorManager sensorManager, DisplayManagerService displayManager, SuspendBlocker displaySuspendBlocker, DisplayBlanker displayBlanker, Callbacks callbacks, Handler callbackHandler) {
        this.mHandler = new DisplayControllerHandler(looper);
        this.mNotifier = notifier;
        this.mDisplaySuspendBlocker = displaySuspendBlocker;
        this.mDisplayBlanker = displayBlanker;
        this.mCallbacks = callbacks;
        this.mCallbackHandler = callbackHandler;
        this.mLights = lights;
        this.mTwilight = twilight;
        this.mSensorManager = sensorManager;
        this.mDisplayManager = displayManager;
        Resources resources = context.getResources();
        this.mScreenBrightnessDimConfig = clampAbsoluteBrightness(resources.getInteger(R.integer.config_screenBrightnessDim));
        int screenBrightnessMinimum = Math.min(resources.getInteger(R.integer.config_screenBrightnessSettingMinimum), this.mScreenBrightnessDimConfig);
        this.mUseSoftwareAutoBrightnessConfig = resources.getBoolean(R.bool.config_automatic_brightness_available);
        if (this.mUseSoftwareAutoBrightnessConfig) {
            int[] lux = resources.getIntArray(R.array.config_autoBrightnessLevels);
            int[] screenBrightness = resources.getIntArray(R.array.config_autoBrightnessLcdBacklightValues);
            this.mScreenAutoBrightnessSpline = createAutoBrightnessSpline(lux, screenBrightness);
            if (this.mScreenAutoBrightnessSpline == null) {
                Slog.e(TAG, "Error in config.xml.  config_autoBrightnessLcdBacklightValues (size " + screenBrightness.length + ") must be monotic and have exactly one more entry than config_autoBrightnessLevels (size " + lux.length + ") which must be strictly increasing.  Auto-brightness will be disabled.");
                this.mUseSoftwareAutoBrightnessConfig = false;
            } else if (screenBrightness[0] < screenBrightnessMinimum) {
                screenBrightnessMinimum = screenBrightness[0];
            }
            this.mLightSensorWarmUpTimeConfig = resources.getInteger(R.integer.config_lightSensorWarmupTime);
        }
        this.mScreenBrightnessRangeMinimum = clampAbsoluteBrightness(screenBrightnessMinimum);
        this.mScreenBrightnessRangeMaximum = 255;
        this.mElectronBeamFadesConfig = resources.getBoolean(R.bool.config_animateScreenLights);
        this.mProximitySensor = this.mSensorManager.getDefaultSensor(8);
        if (this.mProximitySensor != null) {
            this.mProximityThreshold = Math.min(this.mProximitySensor.getMaximumRange(), (float) TYPICAL_PROXIMITY_THRESHOLD);
        }
        if (this.mUseSoftwareAutoBrightnessConfig) {
            this.mLightSensor = this.mSensorManager.getDefaultSensor(5);
        }
        if (this.mUseSoftwareAutoBrightnessConfig && USE_TWILIGHT_ADJUSTMENT) {
            this.mTwilight.registerListener(this.mTwilightListener, this.mHandler);
        }
    }

    private static Spline createAutoBrightnessSpline(int[] lux, int[] brightness) {
        try {
            int n = brightness.length;
            float[] x = new float[n];
            float[] y = new float[n];
            y[0] = normalizeAbsoluteBrightness(brightness[0]);
            for (int i = 1; i < n; i++) {
                x[i] = lux[i - 1];
                y[i] = normalizeAbsoluteBrightness(brightness[i]);
            }
            Spline spline = Spline.createMonotoneCubicSpline(x, y);
            if (DEBUG) {
                Slog.d(TAG, "Auto-brightness spline: " + spline);
                for (float v = 1.0f; v < lux[lux.length - 1] * 1.25f; v *= 1.25f) {
                    Slog.d(TAG, String.format("  %7.1f: %7.1f", Float.valueOf(v), Float.valueOf(spline.interpolate(v))));
                }
            }
            return spline;
        } catch (IllegalArgumentException ex) {
            Slog.e(TAG, "Could not create auto-brightness spline.", ex);
            return null;
        }
    }

    public boolean isProximitySensorAvailable() {
        return this.mProximitySensor != null;
    }

    public boolean requestPowerState(DisplayPowerRequest request, boolean waitForNegativeProximity) {
        boolean z;
        if (DEBUG) {
            Slog.d(TAG, "requestPowerState: " + request + ", waitForNegativeProximity=" + waitForNegativeProximity);
        }
        synchronized (this.mLock) {
            boolean changed = false;
            if (waitForNegativeProximity) {
                if (!this.mPendingWaitForNegativeProximityLocked) {
                    this.mPendingWaitForNegativeProximityLocked = true;
                    changed = true;
                }
            }
            if (this.mPendingRequestLocked == null) {
                this.mPendingRequestLocked = new DisplayPowerRequest(request);
                changed = true;
            } else if (!this.mPendingRequestLocked.equals(request)) {
                this.mPendingRequestLocked.copyFrom(request);
                changed = true;
            }
            if (changed) {
                this.mDisplayReadyLocked = false;
            }
            if (changed && !this.mPendingRequestChangedLocked) {
                this.mPendingRequestChangedLocked = true;
                sendUpdatePowerStateLocked();
            }
            z = this.mDisplayReadyLocked;
        }
        return z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendUpdatePowerState() {
        synchronized (this.mLock) {
            sendUpdatePowerStateLocked();
        }
    }

    private void sendUpdatePowerStateLocked() {
        if (!this.mPendingUpdatePowerStateLocked) {
            this.mPendingUpdatePowerStateLocked = true;
            Message msg = this.mHandler.obtainMessage(1);
            msg.setAsynchronous(true);
            this.mHandler.sendMessage(msg);
        }
    }

    private void initialize() {
        this.mPowerState = new DisplayPowerState(new ElectronBeam(this.mDisplayManager), this.mDisplayBlanker, this.mLights.getLight(0));
        this.mElectronBeamOnAnimator = ObjectAnimator.ofFloat(this.mPowerState, DisplayPowerState.ELECTRON_BEAM_LEVEL, 0.0f, 1.0f);
        this.mElectronBeamOnAnimator.setDuration(250L);
        this.mElectronBeamOnAnimator.addListener(this.mAnimatorListener);
        this.mElectronBeamOffAnimator = ObjectAnimator.ofFloat(this.mPowerState, DisplayPowerState.ELECTRON_BEAM_LEVEL, 1.0f, 0.0f);
        this.mElectronBeamOffAnimator.setDuration(400L);
        this.mElectronBeamOffAnimator.addListener(this.mAnimatorListener);
        this.mScreenBrightnessRampAnimator = new RampAnimator<>(this.mPowerState, DisplayPowerState.SCREEN_BRIGHTNESS);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePowerState() {
        int target;
        boolean slow;
        boolean mustInitialize = false;
        boolean updateAutoBrightness = this.mTwilightChanged;
        boolean wasDim = false;
        this.mTwilightChanged = false;
        synchronized (this.mLock) {
            this.mPendingUpdatePowerStateLocked = false;
            if (this.mPendingRequestLocked == null) {
                return;
            }
            if (this.mPowerRequest == null) {
                this.mPowerRequest = new DisplayPowerRequest(this.mPendingRequestLocked);
                this.mWaitingForNegativeProximity = this.mPendingWaitForNegativeProximityLocked;
                this.mPendingWaitForNegativeProximityLocked = false;
                this.mPendingRequestChangedLocked = false;
                mustInitialize = true;
            } else if (this.mPendingRequestChangedLocked) {
                if (this.mPowerRequest.screenAutoBrightnessAdjustment != this.mPendingRequestLocked.screenAutoBrightnessAdjustment) {
                    updateAutoBrightness = true;
                }
                wasDim = this.mPowerRequest.screenState == 1;
                this.mPowerRequest.copyFrom(this.mPendingRequestLocked);
                this.mWaitingForNegativeProximity |= this.mPendingWaitForNegativeProximityLocked;
                this.mPendingWaitForNegativeProximityLocked = false;
                this.mPendingRequestChangedLocked = false;
                this.mDisplayReadyLocked = false;
            }
            boolean mustNotify = !this.mDisplayReadyLocked;
            if (mustInitialize) {
                initialize();
            }
            if (this.mProximitySensor != null) {
                if (this.mPowerRequest.useProximitySensor && this.mPowerRequest.screenState != 0) {
                    setProximitySensorEnabled(true);
                    if (!this.mScreenOffBecauseOfProximity && this.mProximity == 1) {
                        this.mScreenOffBecauseOfProximity = true;
                        sendOnProximityPositiveWithWakelock();
                        setScreenOn(false);
                    }
                } else if (this.mWaitingForNegativeProximity && this.mScreenOffBecauseOfProximity && this.mProximity == 1 && this.mPowerRequest.screenState != 0) {
                    setProximitySensorEnabled(true);
                } else {
                    setProximitySensorEnabled(false);
                    this.mWaitingForNegativeProximity = false;
                }
                if (this.mScreenOffBecauseOfProximity && this.mProximity != 1) {
                    this.mScreenOffBecauseOfProximity = false;
                    sendOnProximityNegativeWithWakelock();
                }
            } else {
                this.mWaitingForNegativeProximity = false;
            }
            if (this.mLightSensor != null) {
                setLightSensorEnabled(this.mPowerRequest.useAutoBrightness && wantScreenOn(this.mPowerRequest.screenState), updateAutoBrightness);
            }
            if (wantScreenOn(this.mPowerRequest.screenState)) {
                if (this.mScreenAutoBrightness >= 0 && this.mLightSensorEnabled) {
                    target = this.mScreenAutoBrightness;
                    slow = this.mUsingScreenAutoBrightness;
                    this.mUsingScreenAutoBrightness = true;
                } else {
                    target = this.mPowerRequest.screenBrightness;
                    slow = false;
                    this.mUsingScreenAutoBrightness = false;
                }
                if (this.mPowerRequest.screenState == 1) {
                    target = Math.min(target - 10, this.mScreenBrightnessDimConfig);
                    slow = false;
                } else if (wasDim) {
                    slow = false;
                }
                animateScreenBrightness(clampScreenBrightness(target), slow ? 40 : 200);
            } else {
                this.mUsingScreenAutoBrightness = false;
            }
            if (!this.mScreenOffBecauseOfProximity) {
                if (wantScreenOn(this.mPowerRequest.screenState)) {
                    if (!this.mElectronBeamOffAnimator.isStarted()) {
                        setScreenOn(true);
                        if (this.mPowerRequest.blockScreenOn && this.mPowerState.getElectronBeamLevel() == 0.0f) {
                            blockScreenOn();
                        } else {
                            unblockScreenOn();
                            this.mPowerState.setElectronBeamLevel(1.0f);
                            this.mPowerState.dismissElectronBeam();
                        }
                    }
                } else if (!this.mElectronBeamOnAnimator.isStarted() && !this.mElectronBeamOffAnimator.isStarted()) {
                    if (this.mPowerState.getElectronBeamLevel() == 0.0f) {
                        setScreenOn(false);
                    } else {
                        if (this.mPowerState.prepareElectronBeam(this.mElectronBeamFadesConfig ? 2 : 1) && this.mPowerState.isScreenOn()) {
                            this.mElectronBeamOffAnimator.start();
                        } else {
                            this.mElectronBeamOffAnimator.end();
                        }
                    }
                }
            }
            if (mustNotify && !this.mScreenOnWasBlocked && !this.mElectronBeamOnAnimator.isStarted() && !this.mElectronBeamOffAnimator.isStarted() && this.mPowerState.waitUntilClean(this.mCleanListener)) {
                synchronized (this.mLock) {
                    if (!this.mPendingRequestChangedLocked) {
                        this.mDisplayReadyLocked = true;
                        if (DEBUG) {
                            Slog.d(TAG, "Display ready!");
                        }
                    }
                }
                sendOnStateChangedWithWakelock();
            }
        }
    }

    private void blockScreenOn() {
        if (!this.mScreenOnWasBlocked) {
            this.mScreenOnWasBlocked = true;
            if (DEBUG) {
                Slog.d(TAG, "Blocked screen on.");
                this.mScreenOnBlockStartRealTime = SystemClock.elapsedRealtime();
            }
        }
    }

    private void unblockScreenOn() {
        if (this.mScreenOnWasBlocked) {
            this.mScreenOnWasBlocked = false;
            if (DEBUG) {
                Slog.d(TAG, "Unblocked screen on after " + (SystemClock.elapsedRealtime() - this.mScreenOnBlockStartRealTime) + " ms");
            }
        }
    }

    private void setScreenOn(boolean on) {
        if ((!this.mPowerState.isScreenOn()) == on) {
            this.mPowerState.setScreenOn(on);
            if (on) {
                this.mNotifier.onScreenOn();
            } else {
                this.mNotifier.onScreenOff();
            }
        }
    }

    private int clampScreenBrightness(int value) {
        return clamp(value, this.mScreenBrightnessRangeMinimum, this.mScreenBrightnessRangeMaximum);
    }

    private static int clampAbsoluteBrightness(int value) {
        return clamp(value, 0, 255);
    }

    private static int clamp(int value, int min, int max) {
        if (value <= min) {
            return min;
        }
        if (value >= max) {
            return max;
        }
        return value;
    }

    private static float normalizeAbsoluteBrightness(int value) {
        return clampAbsoluteBrightness(value) / 255.0f;
    }

    private void animateScreenBrightness(int target, int rate) {
        if (this.mScreenBrightnessRampAnimator.animateTo(target, rate)) {
            this.mNotifier.onScreenBrightness(target);
        }
    }

    private void setProximitySensorEnabled(boolean enable) {
        if (enable) {
            if (!this.mProximitySensorEnabled) {
                this.mProximitySensorEnabled = true;
                this.mSensorManager.registerListener(this.mProximitySensorListener, this.mProximitySensor, 3, this.mHandler);
            }
        } else if (this.mProximitySensorEnabled) {
            this.mProximitySensorEnabled = false;
            this.mProximity = -1;
            this.mPendingProximity = -1;
            this.mHandler.removeMessages(2);
            this.mSensorManager.unregisterListener(this.mProximitySensorListener);
            clearPendingProximityDebounceTime();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleProximitySensorEvent(long time, boolean positive) {
        if (this.mProximitySensorEnabled) {
            if (this.mPendingProximity == 0 && !positive) {
                return;
            }
            if (this.mPendingProximity == 1 && positive) {
                return;
            }
            this.mHandler.removeMessages(2);
            if (positive) {
                this.mPendingProximity = 1;
                setPendingProximityDebounceTime(time + 0);
            } else {
                this.mPendingProximity = 0;
                setPendingProximityDebounceTime(time + 250);
            }
            debounceProximitySensor();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void debounceProximitySensor() {
        if (this.mProximitySensorEnabled && this.mPendingProximity != -1 && this.mPendingProximityDebounceTime >= 0) {
            long now = SystemClock.uptimeMillis();
            if (this.mPendingProximityDebounceTime <= now) {
                this.mProximity = this.mPendingProximity;
                updatePowerState();
                clearPendingProximityDebounceTime();
                return;
            }
            Message msg = this.mHandler.obtainMessage(2);
            msg.setAsynchronous(true);
            this.mHandler.sendMessageAtTime(msg, this.mPendingProximityDebounceTime);
        }
    }

    private void clearPendingProximityDebounceTime() {
        if (this.mPendingProximityDebounceTime >= 0) {
            this.mPendingProximityDebounceTime = -1L;
            this.mDisplaySuspendBlocker.release();
        }
    }

    private void setPendingProximityDebounceTime(long debounceTime) {
        if (this.mPendingProximityDebounceTime < 0) {
            this.mDisplaySuspendBlocker.acquire();
        }
        this.mPendingProximityDebounceTime = debounceTime;
    }

    private void setLightSensorEnabled(boolean enable, boolean updateAutoBrightness) {
        if (enable) {
            if (!this.mLightSensorEnabled) {
                updateAutoBrightness = true;
                this.mLightSensorEnabled = true;
                this.mLightSensorEnableTime = SystemClock.uptimeMillis();
                this.mSensorManager.registerListener(this.mLightSensorListener, this.mLightSensor, 1000000, this.mHandler);
            }
        } else if (this.mLightSensorEnabled) {
            this.mLightSensorEnabled = false;
            this.mAmbientLuxValid = false;
            this.mRecentLightSamples = 0;
            this.mHandler.removeMessages(3);
            this.mSensorManager.unregisterListener(this.mLightSensorListener);
        }
        if (updateAutoBrightness) {
            updateAutoBrightness(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleLightSensorEvent(long time, float lux) {
        this.mHandler.removeMessages(3);
        applyLightSensorMeasurement(time, lux);
        updateAmbientLux(time);
    }

    private void applyLightSensorMeasurement(long time, float lux) {
        this.mRecentLightSamples++;
        if (this.mRecentLightSamples == 1) {
            this.mRecentShortTermAverageLux = lux;
            this.mRecentLongTermAverageLux = lux;
        } else {
            long timeDelta = time - this.mLastObservedLuxTime;
            this.mRecentShortTermAverageLux += ((lux - this.mRecentShortTermAverageLux) * ((float) timeDelta)) / ((float) (1000 + timeDelta));
            this.mRecentLongTermAverageLux += ((lux - this.mRecentLongTermAverageLux) * ((float) timeDelta)) / ((float) (5000 + timeDelta));
        }
        this.mLastObservedLux = lux;
        this.mLastObservedLuxTime = time;
    }

    private void updateAmbientLux(long time) {
        if (!this.mAmbientLuxValid || time - this.mLightSensorEnableTime < this.mLightSensorWarmUpTimeConfig) {
            this.mAmbientLux = this.mRecentShortTermAverageLux;
            this.mAmbientLuxValid = true;
            this.mDebounceLuxDirection = 0;
            this.mDebounceLuxTime = time;
            if (DEBUG) {
                Slog.d(TAG, "updateAmbientLux: Initializing: , mRecentShortTermAverageLux=" + this.mRecentShortTermAverageLux + ", mRecentLongTermAverageLux=" + this.mRecentLongTermAverageLux + ", mAmbientLux=" + this.mAmbientLux);
            }
            updateAutoBrightness(true);
            return;
        }
        float brighteningLuxThreshold = this.mAmbientLux * 1.1f;
        if (this.mRecentShortTermAverageLux > brighteningLuxThreshold && this.mRecentLongTermAverageLux > brighteningLuxThreshold) {
            if (this.mDebounceLuxDirection <= 0) {
                this.mDebounceLuxDirection = 1;
                this.mDebounceLuxTime = time;
                if (DEBUG) {
                    Slog.d(TAG, "updateAmbientLux: Possibly brightened, waiting for 4000 ms: brighteningLuxThreshold=" + brighteningLuxThreshold + ", mRecentShortTermAverageLux=" + this.mRecentShortTermAverageLux + ", mRecentLongTermAverageLux=" + this.mRecentLongTermAverageLux + ", mAmbientLux=" + this.mAmbientLux);
                }
            }
            long debounceTime = this.mDebounceLuxTime + BRIGHTENING_LIGHT_DEBOUNCE;
            if (time >= debounceTime) {
                this.mAmbientLux = this.mRecentShortTermAverageLux;
                if (DEBUG) {
                    Slog.d(TAG, "updateAmbientLux: Brightened: brighteningLuxThreshold=" + brighteningLuxThreshold + ", mRecentShortTermAverageLux=" + this.mRecentShortTermAverageLux + ", mRecentLongTermAverageLux=" + this.mRecentLongTermAverageLux + ", mAmbientLux=" + this.mAmbientLux);
                }
                updateAutoBrightness(true);
                return;
            }
            this.mHandler.sendEmptyMessageAtTime(3, debounceTime);
            return;
        }
        float darkeningLuxThreshold = this.mAmbientLux * 0.8f;
        if (this.mRecentShortTermAverageLux < darkeningLuxThreshold && this.mRecentLongTermAverageLux < darkeningLuxThreshold) {
            if (this.mDebounceLuxDirection >= 0) {
                this.mDebounceLuxDirection = -1;
                this.mDebounceLuxTime = time;
                if (DEBUG) {
                    Slog.d(TAG, "updateAmbientLux: Possibly darkened, waiting for 8000 ms: darkeningLuxThreshold=" + darkeningLuxThreshold + ", mRecentShortTermAverageLux=" + this.mRecentShortTermAverageLux + ", mRecentLongTermAverageLux=" + this.mRecentLongTermAverageLux + ", mAmbientLux=" + this.mAmbientLux);
                }
            }
            long debounceTime2 = this.mDebounceLuxTime + DARKENING_LIGHT_DEBOUNCE;
            if (time >= debounceTime2) {
                this.mAmbientLux = Math.max(this.mRecentShortTermAverageLux, this.mRecentLongTermAverageLux);
                if (DEBUG) {
                    Slog.d(TAG, "updateAmbientLux: Darkened: darkeningLuxThreshold=" + darkeningLuxThreshold + ", mRecentShortTermAverageLux=" + this.mRecentShortTermAverageLux + ", mRecentLongTermAverageLux=" + this.mRecentLongTermAverageLux + ", mAmbientLux=" + this.mAmbientLux);
                }
                updateAutoBrightness(true);
                return;
            }
            this.mHandler.sendEmptyMessageAtTime(3, debounceTime2);
            return;
        }
        if (this.mDebounceLuxDirection != 0) {
            this.mDebounceLuxDirection = 0;
            this.mDebounceLuxTime = time;
            if (DEBUG) {
                Slog.d(TAG, "updateAmbientLux: Canceled debounce: brighteningLuxThreshold=" + brighteningLuxThreshold + ", darkeningLuxThreshold=" + darkeningLuxThreshold + ", mRecentShortTermAverageLux=" + this.mRecentShortTermAverageLux + ", mRecentLongTermAverageLux=" + this.mRecentLongTermAverageLux + ", mAmbientLux=" + this.mAmbientLux);
            }
        }
        if (this.mLastObservedLux > brighteningLuxThreshold || this.mLastObservedLux < darkeningLuxThreshold) {
            this.mHandler.sendEmptyMessageAtTime(3, time + 2000);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void debounceLightSensor() {
        if (this.mLightSensorEnabled) {
            long time = SystemClock.uptimeMillis();
            if (time >= this.mLastObservedLuxTime + 2000) {
                if (DEBUG) {
                    Slog.d(TAG, "debounceLightSensor: Synthesizing light sensor measurement after " + (time - this.mLastObservedLuxTime) + " ms.");
                }
                applyLightSensorMeasurement(time, this.mLastObservedLux);
            }
            updateAmbientLux(time);
        }
    }

    private void updateAutoBrightness(boolean sendUpdate) {
        TwilightService.TwilightState state;
        if (!this.mAmbientLuxValid) {
            return;
        }
        float value = this.mScreenAutoBrightnessSpline.interpolate(this.mAmbientLux);
        float gamma = 1.0f;
        if (USE_SCREEN_AUTO_BRIGHTNESS_ADJUSTMENT && this.mPowerRequest.screenAutoBrightnessAdjustment != 0.0f) {
            float adjGamma = FloatMath.pow(SCREEN_AUTO_BRIGHTNESS_ADJUSTMENT_MAX_GAMMA, Math.min(1.0f, Math.max(-1.0f, -this.mPowerRequest.screenAutoBrightnessAdjustment)));
            gamma = 1.0f * adjGamma;
            if (DEBUG) {
                Slog.d(TAG, "updateAutoBrightness: adjGamma=" + adjGamma);
            }
        }
        if (USE_TWILIGHT_ADJUSTMENT && (state = this.mTwilight.getCurrentState()) != null && state.isNight()) {
            long now = System.currentTimeMillis();
            float earlyGamma = getTwilightGamma(now, state.getYesterdaySunset(), state.getTodaySunrise());
            float lateGamma = getTwilightGamma(now, state.getTodaySunset(), state.getTomorrowSunrise());
            gamma *= earlyGamma * lateGamma;
            if (DEBUG) {
                Slog.d(TAG, "updateAutoBrightness: earlyGamma=" + earlyGamma + ", lateGamma=" + lateGamma);
            }
        }
        if (gamma != 1.0f) {
            value = FloatMath.pow(value, gamma);
            if (DEBUG) {
                Slog.d(TAG, "updateAutoBrightness: gamma=" + gamma + ", in=" + value + ", out=" + value);
            }
        }
        int newScreenAutoBrightness = clampScreenBrightness(Math.round(value * 255.0f));
        if (this.mScreenAutoBrightness != newScreenAutoBrightness) {
            if (DEBUG) {
                Slog.d(TAG, "updateAutoBrightness: mScreenAutoBrightness=" + this.mScreenAutoBrightness + ", newScreenAutoBrightness=" + newScreenAutoBrightness);
            }
            this.mScreenAutoBrightness = newScreenAutoBrightness;
            this.mLastScreenAutoBrightnessGamma = gamma;
            if (sendUpdate) {
                sendUpdatePowerState();
            }
        }
    }

    private static float getTwilightGamma(long now, long lastSunset, long nextSunrise) {
        if (lastSunset < 0 || nextSunrise < 0 || now < lastSunset || now > nextSunrise) {
            return 1.0f;
        }
        if (now < lastSunset + TWILIGHT_ADJUSTMENT_TIME) {
            return lerp(1.0f, TWILIGHT_ADJUSTMENT_MAX_GAMMA, ((float) (now - lastSunset)) / 7200000.0f);
        }
        if (now > nextSunrise - TWILIGHT_ADJUSTMENT_TIME) {
            return lerp(1.0f, TWILIGHT_ADJUSTMENT_MAX_GAMMA, ((float) (nextSunrise - now)) / 7200000.0f);
        }
        return TWILIGHT_ADJUSTMENT_MAX_GAMMA;
    }

    private static float lerp(float x, float y, float alpha) {
        return x + ((y - x) * alpha);
    }

    private void sendOnStateChangedWithWakelock() {
        this.mDisplaySuspendBlocker.acquire();
        this.mCallbackHandler.post(this.mOnStateChangedRunnable);
    }

    private void sendOnProximityPositiveWithWakelock() {
        this.mDisplaySuspendBlocker.acquire();
        this.mCallbackHandler.post(this.mOnProximityPositiveRunnable);
    }

    private void sendOnProximityNegativeWithWakelock() {
        this.mDisplaySuspendBlocker.acquire();
        this.mCallbackHandler.post(this.mOnProximityNegativeRunnable);
    }

    public void dump(final PrintWriter pw) {
        synchronized (this.mLock) {
            pw.println();
            pw.println("Display Controller Locked State:");
            pw.println("  mDisplayReadyLocked=" + this.mDisplayReadyLocked);
            pw.println("  mPendingRequestLocked=" + this.mPendingRequestLocked);
            pw.println("  mPendingRequestChangedLocked=" + this.mPendingRequestChangedLocked);
            pw.println("  mPendingWaitForNegativeProximityLocked=" + this.mPendingWaitForNegativeProximityLocked);
            pw.println("  mPendingUpdatePowerStateLocked=" + this.mPendingUpdatePowerStateLocked);
        }
        pw.println();
        pw.println("Display Controller Configuration:");
        pw.println("  mScreenBrightnessDimConfig=" + this.mScreenBrightnessDimConfig);
        pw.println("  mScreenBrightnessRangeMinimum=" + this.mScreenBrightnessRangeMinimum);
        pw.println("  mScreenBrightnessRangeMaximum=" + this.mScreenBrightnessRangeMaximum);
        pw.println("  mUseSoftwareAutoBrightnessConfig=" + this.mUseSoftwareAutoBrightnessConfig);
        pw.println("  mScreenAutoBrightnessSpline=" + this.mScreenAutoBrightnessSpline);
        pw.println("  mLightSensorWarmUpTimeConfig=" + this.mLightSensorWarmUpTimeConfig);
        this.mHandler.runWithScissors(new Runnable() { // from class: com.android.server.power.DisplayPowerController.6
            @Override // java.lang.Runnable
            public void run() {
                DisplayPowerController.this.dumpLocal(pw);
            }
        }, 1000L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dumpLocal(PrintWriter pw) {
        pw.println();
        pw.println("Display Controller Thread State:");
        pw.println("  mPowerRequest=" + this.mPowerRequest);
        pw.println("  mWaitingForNegativeProximity=" + this.mWaitingForNegativeProximity);
        pw.println("  mProximitySensor=" + this.mProximitySensor);
        pw.println("  mProximitySensorEnabled=" + this.mProximitySensorEnabled);
        pw.println("  mProximityThreshold=" + this.mProximityThreshold);
        pw.println("  mProximity=" + proximityToString(this.mProximity));
        pw.println("  mPendingProximity=" + proximityToString(this.mPendingProximity));
        pw.println("  mPendingProximityDebounceTime=" + TimeUtils.formatUptime(this.mPendingProximityDebounceTime));
        pw.println("  mScreenOffBecauseOfProximity=" + this.mScreenOffBecauseOfProximity);
        pw.println("  mLightSensor=" + this.mLightSensor);
        pw.println("  mLightSensorEnabled=" + this.mLightSensorEnabled);
        pw.println("  mLightSensorEnableTime=" + TimeUtils.formatUptime(this.mLightSensorEnableTime));
        pw.println("  mAmbientLux=" + this.mAmbientLux);
        pw.println("  mAmbientLuxValid=" + this.mAmbientLuxValid);
        pw.println("  mLastObservedLux=" + this.mLastObservedLux);
        pw.println("  mLastObservedLuxTime=" + TimeUtils.formatUptime(this.mLastObservedLuxTime));
        pw.println("  mRecentLightSamples=" + this.mRecentLightSamples);
        pw.println("  mRecentShortTermAverageLux=" + this.mRecentShortTermAverageLux);
        pw.println("  mRecentLongTermAverageLux=" + this.mRecentLongTermAverageLux);
        pw.println("  mDebounceLuxDirection=" + this.mDebounceLuxDirection);
        pw.println("  mDebounceLuxTime=" + TimeUtils.formatUptime(this.mDebounceLuxTime));
        pw.println("  mScreenAutoBrightness=" + this.mScreenAutoBrightness);
        pw.println("  mUsingScreenAutoBrightness=" + this.mUsingScreenAutoBrightness);
        pw.println("  mLastScreenAutoBrightnessGamma=" + this.mLastScreenAutoBrightnessGamma);
        pw.println("  mTwilight.getCurrentState()=" + this.mTwilight.getCurrentState());
        if (this.mElectronBeamOnAnimator != null) {
            pw.println("  mElectronBeamOnAnimator.isStarted()=" + this.mElectronBeamOnAnimator.isStarted());
        }
        if (this.mElectronBeamOffAnimator != null) {
            pw.println("  mElectronBeamOffAnimator.isStarted()=" + this.mElectronBeamOffAnimator.isStarted());
        }
        if (this.mPowerState != null) {
            this.mPowerState.dump(pw);
        }
    }

    private static String proximityToString(int state) {
        switch (state) {
            case -1:
                return SubscriptionStateHeader.UNKNOWN;
            case 0:
                return "Negative";
            case 1:
                return "Positive";
            default:
                return Integer.toString(state);
        }
    }

    private static boolean wantScreenOn(int state) {
        switch (state) {
            case 1:
            case 2:
                return true;
            default:
                return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: DisplayPowerController$DisplayControllerHandler.class */
    public final class DisplayControllerHandler extends Handler {
        public DisplayControllerHandler(Looper looper) {
            super(looper, null, true);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    DisplayPowerController.this.updatePowerState();
                    return;
                case 2:
                    DisplayPowerController.this.debounceProximitySensor();
                    return;
                case 3:
                    DisplayPowerController.this.debounceLightSensor();
                    return;
                default:
                    return;
            }
        }
    }
}