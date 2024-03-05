package com.android.server.power;

/* loaded from: DisplayPowerRequest.class */
final class DisplayPowerRequest {
    public static final int SCREEN_STATE_OFF = 0;
    public static final int SCREEN_STATE_DIM = 1;
    public static final int SCREEN_STATE_BRIGHT = 2;
    public int screenState;
    public boolean useProximitySensor;
    public int screenBrightness;
    public float screenAutoBrightnessAdjustment;
    public boolean useAutoBrightness;
    public boolean blockScreenOn;

    public DisplayPowerRequest() {
        this.screenState = 2;
        this.useProximitySensor = false;
        this.screenBrightness = 255;
        this.screenAutoBrightnessAdjustment = 0.0f;
        this.useAutoBrightness = false;
        this.blockScreenOn = false;
    }

    public DisplayPowerRequest(DisplayPowerRequest other) {
        copyFrom(other);
    }

    public void copyFrom(DisplayPowerRequest other) {
        this.screenState = other.screenState;
        this.useProximitySensor = other.useProximitySensor;
        this.screenBrightness = other.screenBrightness;
        this.screenAutoBrightnessAdjustment = other.screenAutoBrightnessAdjustment;
        this.useAutoBrightness = other.useAutoBrightness;
        this.blockScreenOn = other.blockScreenOn;
    }

    public boolean equals(Object o) {
        return (o instanceof DisplayPowerRequest) && equals((DisplayPowerRequest) o);
    }

    public boolean equals(DisplayPowerRequest other) {
        return other != null && this.screenState == other.screenState && this.useProximitySensor == other.useProximitySensor && this.screenBrightness == other.screenBrightness && this.screenAutoBrightnessAdjustment == other.screenAutoBrightnessAdjustment && this.useAutoBrightness == other.useAutoBrightness && this.blockScreenOn == other.blockScreenOn;
    }

    public int hashCode() {
        return 0;
    }

    public String toString() {
        return "screenState=" + this.screenState + ", useProximitySensor=" + this.useProximitySensor + ", screenBrightness=" + this.screenBrightness + ", screenAutoBrightnessAdjustment=" + this.screenAutoBrightnessAdjustment + ", useAutoBrightness=" + this.useAutoBrightness + ", blockScreenOn=" + this.blockScreenOn;
    }
}