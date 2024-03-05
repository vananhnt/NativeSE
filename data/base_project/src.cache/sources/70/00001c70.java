package com.android.server;

import android.Manifest;
import android.content.Context;
import android.os.Handler;
import android.os.IHardwareService;
import android.os.Message;
import android.os.ServiceManager;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/* loaded from: LightsService.class */
public class LightsService {
    private static final String TAG = "LightsService";
    private static final boolean DEBUG = false;
    public static final int LIGHT_ID_BACKLIGHT = 0;
    public static final int LIGHT_ID_KEYBOARD = 1;
    public static final int LIGHT_ID_BUTTONS = 2;
    public static final int LIGHT_ID_BATTERY = 3;
    public static final int LIGHT_ID_NOTIFICATIONS = 4;
    public static final int LIGHT_ID_ATTENTION = 5;
    public static final int LIGHT_ID_BLUETOOTH = 6;
    public static final int LIGHT_ID_WIFI = 7;
    public static final int LIGHT_ID_COUNT = 8;
    public static final int LIGHT_FLASH_NONE = 0;
    public static final int LIGHT_FLASH_TIMED = 1;
    public static final int LIGHT_FLASH_HARDWARE = 2;
    public static final int BRIGHTNESS_MODE_USER = 0;
    public static final int BRIGHTNESS_MODE_SENSOR = 1;
    private final Context mContext;
    private final Light[] mLights = new Light[8];
    private final IHardwareService.Stub mLegacyFlashlightHack = new IHardwareService.Stub() { // from class: com.android.server.LightsService.1
        private static final String FLASHLIGHT_FILE = "/sys/class/leds/spotlight/brightness";

        @Override // android.os.IHardwareService
        public boolean getFlashlightEnabled() {
            try {
                FileInputStream fis = new FileInputStream(FLASHLIGHT_FILE);
                int result = fis.read();
                fis.close();
                return result != 48;
            } catch (Exception e) {
                return false;
            }
        }

        @Override // android.os.IHardwareService
        public void setFlashlightEnabled(boolean on) {
            if (LightsService.this.mContext.checkCallingOrSelfPermission(Manifest.permission.FLASHLIGHT) != 0 && LightsService.this.mContext.checkCallingOrSelfPermission(Manifest.permission.HARDWARE_TEST) != 0) {
                throw new SecurityException("Requires FLASHLIGHT or HARDWARE_TEST permission");
            }
            try {
                FileOutputStream fos = new FileOutputStream(FLASHLIGHT_FILE);
                byte[] bytes = new byte[2];
                bytes[0] = (byte) (on ? 49 : 48);
                bytes[1] = 10;
                fos.write(bytes);
                fos.close();
            } catch (Exception e) {
            }
        }
    };
    private Handler mH = new Handler() { // from class: com.android.server.LightsService.2
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            Light light = (Light) msg.obj;
            light.stopFlashing();
        }
    };
    private int mNativePointer = init_native();

    private static native int init_native();

    private static native void finalize_native(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void setLight_native(int i, int i2, int i3, int i4, int i5, int i6, int i7);

    /* loaded from: LightsService$Light.class */
    public final class Light {
        private int mId;
        private int mColor;
        private int mMode;
        private int mOnMS;
        private int mOffMS;
        private boolean mFlashing;

        private Light(int id) {
            this.mId = id;
        }

        public void setBrightness(int brightness) {
            setBrightness(brightness, 0);
        }

        public void setBrightness(int brightness, int brightnessMode) {
            synchronized (this) {
                int color = brightness & 255;
                setLightLocked((-16777216) | (color << 16) | (color << 8) | color, 0, 0, 0, brightnessMode);
            }
        }

        public void setColor(int color) {
            synchronized (this) {
                setLightLocked(color, 0, 0, 0, 0);
            }
        }

        public void setFlashing(int color, int mode, int onMS, int offMS) {
            synchronized (this) {
                setLightLocked(color, mode, onMS, offMS, 0);
            }
        }

        public void pulse() {
            pulse(16777215, 7);
        }

        public void pulse(int color, int onMS) {
            synchronized (this) {
                if (this.mColor == 0 && !this.mFlashing) {
                    setLightLocked(color, 2, onMS, 1000, 0);
                    LightsService.this.mH.sendMessageDelayed(Message.obtain(LightsService.this.mH, 1, this), onMS);
                }
            }
        }

        public void turnOff() {
            synchronized (this) {
                setLightLocked(0, 0, 0, 0, 0);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void stopFlashing() {
            synchronized (this) {
                setLightLocked(this.mColor, 0, 0, 0, 0);
            }
        }

        private void setLightLocked(int color, int mode, int onMS, int offMS, int brightnessMode) {
            if (color != this.mColor || mode != this.mMode || onMS != this.mOnMS || offMS != this.mOffMS) {
                this.mColor = color;
                this.mMode = mode;
                this.mOnMS = onMS;
                this.mOffMS = offMS;
                LightsService.setLight_native(LightsService.this.mNativePointer, this.mId, color, mode, onMS, offMS, brightnessMode);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public LightsService(Context context) {
        this.mContext = context;
        ServiceManager.addService("hardware", this.mLegacyFlashlightHack);
        for (int i = 0; i < 8; i++) {
            this.mLights[i] = new Light(i);
        }
    }

    protected void finalize() throws Throwable {
        finalize_native(this.mNativePointer);
        super.finalize();
    }

    public Light getLight(int id) {
        return this.mLights[id];
    }
}