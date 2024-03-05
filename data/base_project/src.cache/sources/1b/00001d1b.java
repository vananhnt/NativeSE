package com.android.server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.UEventObserver;
import android.util.Log;
import android.util.Slog;
import com.android.internal.R;
import com.android.server.input.InputManagerService;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: WiredAccessoryManager.class */
public final class WiredAccessoryManager implements InputManagerService.WiredAccessoryCallbacks {
    private static final String TAG = WiredAccessoryManager.class.getSimpleName();
    private static final boolean LOG = true;
    private static final int BIT_HEADSET = 1;
    private static final int BIT_HEADSET_NO_MIC = 2;
    private static final int BIT_USB_HEADSET_ANLG = 4;
    private static final int BIT_USB_HEADSET_DGTL = 8;
    private static final int BIT_HDMI_AUDIO = 16;
    private static final int SUPPORTED_HEADSETS = 31;
    private static final String NAME_H2W = "h2w";
    private static final String NAME_USB_AUDIO = "usb_audio";
    private static final String NAME_HDMI_AUDIO = "hdmi_audio";
    private static final String NAME_HDMI = "hdmi";
    private static final int MSG_NEW_DEVICE_STATE = 1;
    private final PowerManager.WakeLock mWakeLock;
    private final AudioManager mAudioManager;
    private int mHeadsetState;
    private int mSwitchValues;
    private final WiredAccessoryObserver mObserver;
    private final InputManagerService mInputManager;
    private final boolean mUseDevInputEventForAudioJack;
    private final Object mLock = new Object();
    private final Handler mHandler = new Handler(Looper.myLooper(), null, true) { // from class: com.android.server.WiredAccessoryManager.2
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    WiredAccessoryManager.this.setDevicesState(msg.arg1, msg.arg2, (String) msg.obj);
                    WiredAccessoryManager.this.mWakeLock.release();
                    return;
                default:
                    return;
            }
        }
    };

    public WiredAccessoryManager(Context context, InputManagerService inputManager) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(1, "WiredAccessoryManager");
        this.mWakeLock.setReferenceCounted(false);
        this.mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        this.mInputManager = inputManager;
        this.mUseDevInputEventForAudioJack = context.getResources().getBoolean(R.bool.config_useDevInputEventForAudioJack);
        this.mObserver = new WiredAccessoryObserver();
        context.registerReceiver(new BroadcastReceiver() { // from class: com.android.server.WiredAccessoryManager.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context ctx, Intent intent) {
                WiredAccessoryManager.this.bootCompleted();
            }
        }, new IntentFilter(Intent.ACTION_BOOT_COMPLETED), null, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void bootCompleted() {
        if (this.mUseDevInputEventForAudioJack) {
            int switchValues = 0;
            if (this.mInputManager.getSwitchState(-1, -256, 2) == 1) {
                switchValues = 0 | 4;
            }
            if (this.mInputManager.getSwitchState(-1, -256, 4) == 1) {
                switchValues |= 16;
            }
            notifyWiredAccessoryChanged(0L, switchValues, 20);
        }
        this.mObserver.init();
    }

    @Override // com.android.server.input.InputManagerService.WiredAccessoryCallbacks
    public void notifyWiredAccessoryChanged(long whenNanos, int switchValues, int switchMask) {
        int headset;
        Slog.v(TAG, "notifyWiredAccessoryChanged: when=" + whenNanos + " bits=" + switchCodeToString(switchValues, switchMask) + " mask=" + Integer.toHexString(switchMask));
        synchronized (this.mLock) {
            this.mSwitchValues = (this.mSwitchValues & (switchMask ^ (-1))) | switchValues;
            switch (this.mSwitchValues & 20) {
                case 0:
                    headset = 0;
                    break;
                case 4:
                    headset = 2;
                    break;
                case 16:
                    headset = 1;
                    break;
                case 20:
                    headset = 1;
                    break;
                default:
                    headset = 0;
                    break;
            }
            updateLocked(NAME_H2W, (this.mHeadsetState & (-4)) | headset);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateLocked(String newName, int newState) {
        int headsetState = newState & 31;
        int usb_headset_anlg = headsetState & 4;
        int usb_headset_dgtl = headsetState & 8;
        int h2w_headset = headsetState & 3;
        boolean h2wStateChange = true;
        boolean usbStateChange = true;
        Slog.v(TAG, "newName=" + newName + " newState=" + newState + " headsetState=" + headsetState + " prev headsetState=" + this.mHeadsetState);
        if (this.mHeadsetState == headsetState) {
            Log.e(TAG, "No state change.");
            return;
        }
        if (h2w_headset == 3) {
            Log.e(TAG, "Invalid combination, unsetting h2w flag");
            h2wStateChange = false;
        }
        if (usb_headset_anlg == 4 && usb_headset_dgtl == 8) {
            Log.e(TAG, "Invalid combination, unsetting usb flag");
            usbStateChange = false;
        }
        if (!h2wStateChange && !usbStateChange) {
            Log.e(TAG, "invalid transition, returning ...");
            return;
        }
        this.mWakeLock.acquire();
        Message msg = this.mHandler.obtainMessage(1, headsetState, this.mHeadsetState, newName);
        this.mHandler.sendMessage(msg);
        this.mHeadsetState = headsetState;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setDevicesState(int headsetState, int prevHeadsetState, String headsetName) {
        synchronized (this.mLock) {
            int allHeadsets = 31;
            int curHeadset = 1;
            while (allHeadsets != 0) {
                if ((curHeadset & allHeadsets) != 0) {
                    setDeviceStateLocked(curHeadset, headsetState, prevHeadsetState, headsetName);
                    allHeadsets &= curHeadset ^ (-1);
                }
                curHeadset <<= 1;
            }
        }
    }

    private void setDeviceStateLocked(int headset, int headsetState, int prevHeadsetState, String headsetName) {
        int state;
        int device;
        if ((headsetState & headset) != (prevHeadsetState & headset)) {
            if ((headsetState & headset) != 0) {
                state = 1;
            } else {
                state = 0;
            }
            if (headset == 1) {
                device = 4;
            } else if (headset == 2) {
                device = 8;
            } else if (headset == 4) {
                device = 2048;
            } else if (headset == 8) {
                device = 4096;
            } else if (headset == 16) {
                device = 1024;
            } else {
                Slog.e(TAG, "setDeviceState() invalid headset type: " + headset);
                return;
            }
            Slog.v(TAG, "device " + headsetName + (state == 1 ? " connected" : " disconnected"));
            this.mAudioManager.setWiredDeviceConnectionState(device, state, headsetName);
        }
    }

    private String switchCodeToString(int switchValues, int switchMask) {
        StringBuffer sb = new StringBuffer();
        if ((switchMask & 4) != 0 && (switchValues & 4) != 0) {
            sb.append("SW_HEADPHONE_INSERT ");
        }
        if ((switchMask & 16) != 0 && (switchValues & 16) != 0) {
            sb.append("SW_MICROPHONE_INSERT");
        }
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: WiredAccessoryManager$WiredAccessoryObserver.class */
    public class WiredAccessoryObserver extends UEventObserver {
        private final List<UEventInfo> mUEventInfo = makeObservedUEventList();

        public WiredAccessoryObserver() {
        }

        void init() {
            synchronized (WiredAccessoryManager.this.mLock) {
                Slog.v(WiredAccessoryManager.TAG, "init()");
                char[] buffer = new char[1024];
                for (int i = 0; i < this.mUEventInfo.size(); i++) {
                    UEventInfo uei = this.mUEventInfo.get(i);
                    try {
                        FileReader file = new FileReader(uei.getSwitchStatePath());
                        int len = file.read(buffer, 0, 1024);
                        file.close();
                        int curState = Integer.valueOf(new String(buffer, 0, len).trim()).intValue();
                        if (curState > 0) {
                            updateStateLocked(uei.getDevPath(), uei.getDevName(), curState);
                        }
                    } catch (FileNotFoundException e) {
                        Slog.w(WiredAccessoryManager.TAG, uei.getSwitchStatePath() + " not found while attempting to determine initial switch state");
                    } catch (Exception e2) {
                        Slog.e(WiredAccessoryManager.TAG, "", e2);
                    }
                }
            }
            for (int i2 = 0; i2 < this.mUEventInfo.size(); i2++) {
                startObserving("DEVPATH=" + this.mUEventInfo.get(i2).getDevPath());
            }
        }

        private List<UEventInfo> makeObservedUEventList() {
            List<UEventInfo> retVal = new ArrayList<>();
            if (!WiredAccessoryManager.this.mUseDevInputEventForAudioJack) {
                UEventInfo uei = new UEventInfo(WiredAccessoryManager.NAME_H2W, 1, 2);
                if (!uei.checkSwitchExists()) {
                    Slog.w(WiredAccessoryManager.TAG, "This kernel does not have wired headset support");
                } else {
                    retVal.add(uei);
                }
            }
            UEventInfo uei2 = new UEventInfo(WiredAccessoryManager.NAME_USB_AUDIO, 4, 8);
            if (!uei2.checkSwitchExists()) {
                Slog.w(WiredAccessoryManager.TAG, "This kernel does not have usb audio support");
            } else {
                retVal.add(uei2);
            }
            UEventInfo uei3 = new UEventInfo(WiredAccessoryManager.NAME_HDMI_AUDIO, 16, 0);
            if (uei3.checkSwitchExists()) {
                retVal.add(uei3);
            } else {
                UEventInfo uei4 = new UEventInfo(WiredAccessoryManager.NAME_HDMI, 16, 0);
                if (!uei4.checkSwitchExists()) {
                    Slog.w(WiredAccessoryManager.TAG, "This kernel does not have HDMI audio support");
                } else {
                    retVal.add(uei4);
                }
            }
            return retVal;
        }

        @Override // android.os.UEventObserver
        public void onUEvent(UEventObserver.UEvent event) {
            Slog.v(WiredAccessoryManager.TAG, "Headset UEVENT: " + event.toString());
            try {
                String devPath = event.get("DEVPATH");
                String name = event.get("SWITCH_NAME");
                int state = Integer.parseInt(event.get("SWITCH_STATE"));
                synchronized (WiredAccessoryManager.this.mLock) {
                    updateStateLocked(devPath, name, state);
                }
            } catch (NumberFormatException e) {
                Slog.e(WiredAccessoryManager.TAG, "Could not parse switch state from event " + event);
            }
        }

        private void updateStateLocked(String devPath, String name, int state) {
            for (int i = 0; i < this.mUEventInfo.size(); i++) {
                UEventInfo uei = this.mUEventInfo.get(i);
                if (devPath.equals(uei.getDevPath())) {
                    WiredAccessoryManager.this.updateLocked(name, uei.computeNewHeadsetState(WiredAccessoryManager.this.mHeadsetState, state));
                    return;
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: WiredAccessoryManager$WiredAccessoryObserver$UEventInfo.class */
        public final class UEventInfo {
            private final String mDevName;
            private final int mState1Bits;
            private final int mState2Bits;

            public UEventInfo(String devName, int state1Bits, int state2Bits) {
                this.mDevName = devName;
                this.mState1Bits = state1Bits;
                this.mState2Bits = state2Bits;
            }

            public String getDevName() {
                return this.mDevName;
            }

            public String getDevPath() {
                return String.format(Locale.US, "/devices/virtual/switch/%s", this.mDevName);
            }

            public String getSwitchStatePath() {
                return String.format(Locale.US, "/sys/class/switch/%s/state", this.mDevName);
            }

            public boolean checkSwitchExists() {
                File f = new File(getSwitchStatePath());
                return f.exists();
            }

            public int computeNewHeadsetState(int headsetState, int switchState) {
                int preserveMask = (this.mState1Bits | this.mState2Bits) ^ (-1);
                int setBits = switchState == 1 ? this.mState1Bits : switchState == 2 ? this.mState2Bits : 0;
                return (headsetState & preserveMask) | setBits;
            }
        }
    }
}