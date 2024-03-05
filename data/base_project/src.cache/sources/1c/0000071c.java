package android.media;

/* loaded from: AudioSystem.class */
public class AudioSystem {
    public static final int STREAM_VOICE_CALL = 0;
    public static final int STREAM_SYSTEM = 1;
    public static final int STREAM_RING = 2;
    public static final int STREAM_MUSIC = 3;
    public static final int STREAM_ALARM = 4;
    public static final int STREAM_NOTIFICATION = 5;
    public static final int STREAM_BLUETOOTH_SCO = 6;
    public static final int STREAM_SYSTEM_ENFORCED = 7;
    public static final int STREAM_DTMF = 8;
    public static final int STREAM_TTS = 9;
    public static final int NUM_STREAMS = 5;
    private static final int NUM_STREAM_TYPES = 10;
    public static final int MODE_INVALID = -2;
    public static final int MODE_CURRENT = -1;
    public static final int MODE_NORMAL = 0;
    public static final int MODE_RINGTONE = 1;
    public static final int MODE_IN_CALL = 2;
    public static final int MODE_IN_COMMUNICATION = 3;
    public static final int NUM_MODES = 4;
    @Deprecated
    public static final int ROUTE_EARPIECE = 1;
    @Deprecated
    public static final int ROUTE_SPEAKER = 2;
    @Deprecated
    public static final int ROUTE_BLUETOOTH = 4;
    @Deprecated
    public static final int ROUTE_BLUETOOTH_SCO = 4;
    @Deprecated
    public static final int ROUTE_HEADSET = 8;
    @Deprecated
    public static final int ROUTE_BLUETOOTH_A2DP = 16;
    @Deprecated
    public static final int ROUTE_ALL = -1;
    public static final int AUDIO_STATUS_OK = 0;
    public static final int AUDIO_STATUS_ERROR = 1;
    public static final int AUDIO_STATUS_SERVER_DIED = 100;
    private static ErrorCallback mErrorCallback;
    public static final int DEVICE_BIT_IN = Integer.MIN_VALUE;
    public static final int DEVICE_BIT_DEFAULT = 1073741824;
    public static final int DEVICE_OUT_EARPIECE = 1;
    public static final int DEVICE_OUT_SPEAKER = 2;
    public static final int DEVICE_OUT_WIRED_HEADSET = 4;
    public static final int DEVICE_OUT_WIRED_HEADPHONE = 8;
    public static final int DEVICE_OUT_BLUETOOTH_SCO = 16;
    public static final int DEVICE_OUT_BLUETOOTH_SCO_HEADSET = 32;
    public static final int DEVICE_OUT_BLUETOOTH_SCO_CARKIT = 64;
    public static final int DEVICE_OUT_BLUETOOTH_A2DP = 128;
    public static final int DEVICE_OUT_BLUETOOTH_A2DP_HEADPHONES = 256;
    public static final int DEVICE_OUT_BLUETOOTH_A2DP_SPEAKER = 512;
    public static final int DEVICE_OUT_AUX_DIGITAL = 1024;
    public static final int DEVICE_OUT_ANLG_DOCK_HEADSET = 2048;
    public static final int DEVICE_OUT_DGTL_DOCK_HEADSET = 4096;
    public static final int DEVICE_OUT_USB_ACCESSORY = 8192;
    public static final int DEVICE_OUT_USB_DEVICE = 16384;
    public static final int DEVICE_OUT_REMOTE_SUBMIX = 32768;
    public static final int DEVICE_OUT_DEFAULT = 1073741824;
    public static final int DEVICE_OUT_ALL = 1073807359;
    public static final int DEVICE_OUT_ALL_A2DP = 896;
    public static final int DEVICE_OUT_ALL_SCO = 112;
    public static final int DEVICE_OUT_ALL_USB = 24576;
    public static final int DEVICE_IN_COMMUNICATION = -2147483647;
    public static final int DEVICE_IN_AMBIENT = -2147483646;
    public static final int DEVICE_IN_BUILTIN_MIC = -2147483644;
    public static final int DEVICE_IN_BLUETOOTH_SCO_HEADSET = -2147483640;
    public static final int DEVICE_IN_WIRED_HEADSET = -2147483632;
    public static final int DEVICE_IN_AUX_DIGITAL = -2147483616;
    public static final int DEVICE_IN_VOICE_CALL = -2147483584;
    public static final int DEVICE_IN_BACK_MIC = -2147483520;
    public static final int DEVICE_IN_REMOTE_SUBMIX = -2147483392;
    public static final int DEVICE_IN_ANLG_DOCK_HEADSET = -2147483136;
    public static final int DEVICE_IN_DGTL_DOCK_HEADSET = -2147482624;
    public static final int DEVICE_IN_USB_ACCESSORY = -2147481600;
    public static final int DEVICE_IN_USB_DEVICE = -2147479552;
    public static final int DEVICE_IN_DEFAULT = -1073741824;
    public static final int DEVICE_IN_ALL = -1073733633;
    public static final int DEVICE_IN_ALL_SCO = -2147483640;
    public static final int DEVICE_STATE_UNAVAILABLE = 0;
    public static final int DEVICE_STATE_AVAILABLE = 1;
    private static final int NUM_DEVICE_STATES = 1;
    public static final String DEVICE_OUT_EARPIECE_NAME = "earpiece";
    public static final String DEVICE_OUT_SPEAKER_NAME = "speaker";
    public static final String DEVICE_OUT_WIRED_HEADSET_NAME = "headset";
    public static final String DEVICE_OUT_WIRED_HEADPHONE_NAME = "headphone";
    public static final String DEVICE_OUT_BLUETOOTH_SCO_NAME = "bt_sco";
    public static final String DEVICE_OUT_BLUETOOTH_SCO_HEADSET_NAME = "bt_sco_hs";
    public static final String DEVICE_OUT_BLUETOOTH_SCO_CARKIT_NAME = "bt_sco_carkit";
    public static final String DEVICE_OUT_BLUETOOTH_A2DP_NAME = "bt_a2dp";
    public static final String DEVICE_OUT_BLUETOOTH_A2DP_HEADPHONES_NAME = "bt_a2dp_hp";
    public static final String DEVICE_OUT_BLUETOOTH_A2DP_SPEAKER_NAME = "bt_a2dp_spk";
    public static final String DEVICE_OUT_AUX_DIGITAL_NAME = "aux_digital";
    public static final String DEVICE_OUT_ANLG_DOCK_HEADSET_NAME = "analog_dock";
    public static final String DEVICE_OUT_DGTL_DOCK_HEADSET_NAME = "digital_dock";
    public static final String DEVICE_OUT_USB_ACCESSORY_NAME = "usb_accessory";
    public static final String DEVICE_OUT_USB_DEVICE_NAME = "usb_device";
    public static final String DEVICE_OUT_REMOTE_SUBMIX_NAME = "remote_submix";
    public static final int PHONE_STATE_OFFCALL = 0;
    public static final int PHONE_STATE_RINGING = 1;
    public static final int PHONE_STATE_INCALL = 2;
    public static final int FORCE_NONE = 0;
    public static final int FORCE_SPEAKER = 1;
    public static final int FORCE_HEADPHONES = 2;
    public static final int FORCE_BT_SCO = 3;
    public static final int FORCE_BT_A2DP = 4;
    public static final int FORCE_WIRED_ACCESSORY = 5;
    public static final int FORCE_BT_CAR_DOCK = 6;
    public static final int FORCE_BT_DESK_DOCK = 7;
    public static final int FORCE_ANALOG_DOCK = 8;
    public static final int FORCE_DIGITAL_DOCK = 9;
    public static final int FORCE_NO_BT_A2DP = 10;
    public static final int FORCE_SYSTEM_ENFORCED = 11;
    private static final int NUM_FORCE_CONFIG = 12;
    public static final int FORCE_DEFAULT = 0;
    public static final int FOR_COMMUNICATION = 0;
    public static final int FOR_MEDIA = 1;
    public static final int FOR_RECORD = 2;
    public static final int FOR_DOCK = 3;
    public static final int FOR_SYSTEM = 4;
    private static final int NUM_FORCE_USE = 5;
    public static final int SYNC_EVENT_NONE = 0;
    public static final int SYNC_EVENT_PRESENTATION_COMPLETE = 1;

    /* loaded from: AudioSystem$ErrorCallback.class */
    public interface ErrorCallback {
        void onError(int i);
    }

    public static native int muteMicrophone(boolean z);

    public static native boolean isMicrophoneMuted();

    public static native boolean isStreamActive(int i, int i2);

    public static native boolean isStreamActiveRemotely(int i, int i2);

    public static native boolean isSourceActive(int i);

    public static native int setParameters(String str);

    public static native String getParameters(String str);

    public static native int setDeviceConnectionState(int i, int i2, String str);

    public static native int getDeviceConnectionState(int i, String str);

    public static native int setPhoneState(int i);

    public static native int setForceUse(int i, int i2);

    public static native int getForceUse(int i);

    public static native int initStreamVolume(int i, int i2, int i3);

    public static native int setStreamVolumeIndex(int i, int i2, int i3);

    public static native int getStreamVolumeIndex(int i, int i2);

    public static native int setMasterVolume(float f);

    public static native float getMasterVolume();

    public static native int setMasterMute(boolean z);

    public static native boolean getMasterMute();

    public static native int getDevicesForStream(int i);

    public static native int getPrimaryOutputSamplingRate();

    public static native int getPrimaryOutputFrameCount();

    public static native int getOutputLatency(int i);

    public static native int setLowRamDevice(boolean z);

    public static native int checkAudioFlinger();

    public static final int getNumStreamTypes() {
        return 10;
    }

    public static void setErrorCallback(ErrorCallback cb) {
        synchronized (AudioSystem.class) {
            mErrorCallback = cb;
            if (cb != null) {
                cb.onError(checkAudioFlinger());
            }
        }
    }

    private static void errorCallbackFromNative(int error) {
        ErrorCallback errorCallback = null;
        synchronized (AudioSystem.class) {
            if (mErrorCallback != null) {
                errorCallback = mErrorCallback;
            }
        }
        if (errorCallback != null) {
            errorCallback.onError(error);
        }
    }

    public static String getDeviceName(int device) {
        switch (device) {
            case 1:
                return DEVICE_OUT_EARPIECE_NAME;
            case 2:
                return DEVICE_OUT_SPEAKER_NAME;
            case 4:
                return DEVICE_OUT_WIRED_HEADSET_NAME;
            case 8:
                return DEVICE_OUT_WIRED_HEADPHONE_NAME;
            case 16:
                return DEVICE_OUT_BLUETOOTH_SCO_NAME;
            case 32:
                return DEVICE_OUT_BLUETOOTH_SCO_HEADSET_NAME;
            case 64:
                return DEVICE_OUT_BLUETOOTH_SCO_CARKIT_NAME;
            case 128:
                return DEVICE_OUT_BLUETOOTH_A2DP_NAME;
            case 256:
                return DEVICE_OUT_BLUETOOTH_A2DP_HEADPHONES_NAME;
            case 512:
                return DEVICE_OUT_BLUETOOTH_A2DP_SPEAKER_NAME;
            case 1024:
                return DEVICE_OUT_AUX_DIGITAL_NAME;
            case 2048:
                return DEVICE_OUT_ANLG_DOCK_HEADSET_NAME;
            case 4096:
                return DEVICE_OUT_DGTL_DOCK_HEADSET_NAME;
            case 8192:
                return DEVICE_OUT_USB_ACCESSORY_NAME;
            case 16384:
                return DEVICE_OUT_USB_DEVICE_NAME;
            case 32768:
                return DEVICE_OUT_REMOTE_SUBMIX_NAME;
            case 1073741824:
            default:
                return "";
        }
    }
}