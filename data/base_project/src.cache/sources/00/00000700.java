package android.media;

import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.IAudioFocusDispatcher;
import android.media.IAudioService;
import android.media.RemoteController;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import com.android.internal.R;
import gov.nist.core.Separators;
import java.util.HashMap;

/* loaded from: AudioManager.class */
public class AudioManager {
    private final Context mContext;
    private long mVolumeKeyUpTime;
    private final boolean mUseMasterVolume;
    private final boolean mUseVolumeKeySounds;
    public static final String ACTION_AUDIO_BECOMING_NOISY = "android.media.AUDIO_BECOMING_NOISY";
    public static final String RINGER_MODE_CHANGED_ACTION = "android.media.RINGER_MODE_CHANGED";
    public static final String EXTRA_RINGER_MODE = "android.media.EXTRA_RINGER_MODE";
    public static final String VIBRATE_SETTING_CHANGED_ACTION = "android.media.VIBRATE_SETTING_CHANGED";
    public static final String VOLUME_CHANGED_ACTION = "android.media.VOLUME_CHANGED_ACTION";
    public static final String MASTER_VOLUME_CHANGED_ACTION = "android.media.MASTER_VOLUME_CHANGED_ACTION";
    public static final String MASTER_MUTE_CHANGED_ACTION = "android.media.MASTER_MUTE_CHANGED_ACTION";
    public static final String EXTRA_VIBRATE_SETTING = "android.media.EXTRA_VIBRATE_SETTING";
    public static final String EXTRA_VIBRATE_TYPE = "android.media.EXTRA_VIBRATE_TYPE";
    public static final String EXTRA_VOLUME_STREAM_TYPE = "android.media.EXTRA_VOLUME_STREAM_TYPE";
    public static final String EXTRA_VOLUME_STREAM_VALUE = "android.media.EXTRA_VOLUME_STREAM_VALUE";
    public static final String EXTRA_PREV_VOLUME_STREAM_VALUE = "android.media.EXTRA_PREV_VOLUME_STREAM_VALUE";
    public static final String EXTRA_MASTER_VOLUME_VALUE = "android.media.EXTRA_MASTER_VOLUME_VALUE";
    public static final String EXTRA_PREV_MASTER_VOLUME_VALUE = "android.media.EXTRA_PREV_MASTER_VOLUME_VALUE";
    public static final String EXTRA_MASTER_VOLUME_MUTED = "android.media.EXTRA_MASTER_VOLUME_MUTED";
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
    @Deprecated
    public static final int NUM_STREAMS = 5;
    public static final int ADJUST_RAISE = 1;
    public static final int ADJUST_LOWER = -1;
    public static final int ADJUST_SAME = 0;
    public static final int FLAG_SHOW_UI = 1;
    public static final int FLAG_ALLOW_RINGER_MODES = 2;
    public static final int FLAG_PLAY_SOUND = 4;
    public static final int FLAG_REMOVE_SOUND_AND_VIBRATE = 8;
    public static final int FLAG_VIBRATE = 16;
    public static final int FLAG_FIXED_VOLUME = 32;
    public static final int FLAG_BLUETOOTH_ABS_VOLUME = 64;
    public static final int RINGER_MODE_SILENT = 0;
    public static final int RINGER_MODE_VIBRATE = 1;
    public static final int RINGER_MODE_NORMAL = 2;
    private static final int RINGER_MODE_MAX = 2;
    public static final int VIBRATE_TYPE_RINGER = 0;
    public static final int VIBRATE_TYPE_NOTIFICATION = 1;
    public static final int VIBRATE_SETTING_OFF = 0;
    public static final int VIBRATE_SETTING_ON = 1;
    public static final int VIBRATE_SETTING_ONLY_SILENT = 2;
    public static final int USE_DEFAULT_STREAM_TYPE = Integer.MIN_VALUE;
    private static IAudioService sService;
    @Deprecated
    public static final String ACTION_SCO_AUDIO_STATE_CHANGED = "android.media.SCO_AUDIO_STATE_CHANGED";
    public static final String ACTION_SCO_AUDIO_STATE_UPDATED = "android.media.ACTION_SCO_AUDIO_STATE_UPDATED";
    public static final String EXTRA_SCO_AUDIO_STATE = "android.media.extra.SCO_AUDIO_STATE";
    public static final String EXTRA_SCO_AUDIO_PREVIOUS_STATE = "android.media.extra.SCO_AUDIO_PREVIOUS_STATE";
    public static final int SCO_AUDIO_STATE_DISCONNECTED = 0;
    public static final int SCO_AUDIO_STATE_CONNECTED = 1;
    public static final int SCO_AUDIO_STATE_CONNECTING = 2;
    public static final int SCO_AUDIO_STATE_ERROR = -1;
    public static final int MODE_INVALID = -2;
    public static final int MODE_CURRENT = -1;
    public static final int MODE_NORMAL = 0;
    public static final int MODE_RINGTONE = 1;
    public static final int MODE_IN_CALL = 2;
    public static final int MODE_IN_COMMUNICATION = 3;
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
    public static final int FX_KEY_CLICK = 0;
    public static final int FX_FOCUS_NAVIGATION_UP = 1;
    public static final int FX_FOCUS_NAVIGATION_DOWN = 2;
    public static final int FX_FOCUS_NAVIGATION_LEFT = 3;
    public static final int FX_FOCUS_NAVIGATION_RIGHT = 4;
    public static final int FX_KEYPRESS_STANDARD = 5;
    public static final int FX_KEYPRESS_SPACEBAR = 6;
    public static final int FX_KEYPRESS_DELETE = 7;
    public static final int FX_KEYPRESS_RETURN = 8;
    public static final int FX_KEYPRESS_INVALID = 9;
    public static final int NUM_SOUND_EFFECTS = 10;
    public static final int AUDIOFOCUS_NONE = 0;
    public static final int AUDIOFOCUS_GAIN = 1;
    public static final int AUDIOFOCUS_GAIN_TRANSIENT = 2;
    public static final int AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK = 3;
    public static final int AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE = 4;
    public static final int AUDIOFOCUS_LOSS = -1;
    public static final int AUDIOFOCUS_LOSS_TRANSIENT = -2;
    public static final int AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK = -3;
    public static final int AUDIOFOCUS_REQUEST_FAILED = 0;
    public static final int AUDIOFOCUS_REQUEST_GRANTED = 1;
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
    public static final int DEVICE_OUT_DEFAULT = 1073741824;
    public static final String PROPERTY_OUTPUT_SAMPLE_RATE = "android.media.property.OUTPUT_SAMPLE_RATE";
    public static final String PROPERTY_OUTPUT_FRAMES_PER_BUFFER = "android.media.property.OUTPUT_FRAMES_PER_BUFFER";
    private static String TAG = "AudioManager";
    public static final int[] DEFAULT_STREAM_VOLUME = {4, 7, 5, 11, 6, 5, 7, 7, 11, 11};
    private final Binder mToken = new Binder();
    private final HashMap<String, OnAudioFocusChangeListener> mAudioFocusIdListenerMap = new HashMap<>();
    private final Object mFocusListenerLock = new Object();
    private final FocusEventHandlerDelegate mAudioFocusEventHandlerDelegate = new FocusEventHandlerDelegate();
    private final IAudioFocusDispatcher mAudioFocusDispatcher = new IAudioFocusDispatcher.Stub() { // from class: android.media.AudioManager.1
        @Override // android.media.IAudioFocusDispatcher
        public void dispatchAudioFocusChange(int focusChange, String id) {
            Message m = AudioManager.this.mAudioFocusEventHandlerDelegate.getHandler().obtainMessage(focusChange, id);
            AudioManager.this.mAudioFocusEventHandlerDelegate.getHandler().sendMessage(m);
        }
    };
    private final IBinder mICallBack = new Binder();

    /* loaded from: AudioManager$OnAudioFocusChangeListener.class */
    public interface OnAudioFocusChangeListener {
        void onAudioFocusChange(int i);
    }

    public AudioManager(Context context) {
        this.mContext = context;
        this.mUseMasterVolume = this.mContext.getResources().getBoolean(R.bool.config_useMasterVolume);
        this.mUseVolumeKeySounds = this.mContext.getResources().getBoolean(R.bool.config_useVolumeKeySounds);
    }

    private static IAudioService getService() {
        if (sService != null) {
            return sService;
        }
        IBinder b = ServiceManager.getService(Context.AUDIO_SERVICE);
        sService = IAudioService.Stub.asInterface(b);
        return sService;
    }

    public void dispatchMediaKeyEvent(KeyEvent keyEvent) {
        IAudioService service = getService();
        try {
            service.dispatchMediaKeyEvent(keyEvent);
        } catch (RemoteException e) {
            Log.e(TAG, "dispatchMediaKeyEvent threw exception ", e);
        }
    }

    public void preDispatchKeyEvent(KeyEvent event, int stream) {
        int keyCode = event.getKeyCode();
        if (keyCode != 25 && keyCode != 24 && keyCode != 164 && this.mVolumeKeyUpTime + 300 > SystemClock.uptimeMillis()) {
            if (this.mUseMasterVolume) {
                adjustMasterVolume(0, 8);
            } else {
                adjustSuggestedStreamVolume(0, stream, 8);
            }
        }
    }

    public void handleKeyDown(KeyEvent event, int stream) {
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case 24:
            case 25:
                if (this.mUseMasterVolume) {
                    adjustMasterVolume(keyCode == 24 ? 1 : -1, 17);
                    return;
                } else {
                    adjustSuggestedStreamVolume(keyCode == 24 ? 1 : -1, stream, 17);
                    return;
                }
            case 164:
                if (event.getRepeatCount() == 0 && this.mUseMasterVolume) {
                    setMasterMute(!isMasterMute());
                    return;
                }
                return;
            default:
                return;
        }
    }

    public void handleKeyUp(KeyEvent event, int stream) {
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case 24:
            case 25:
                if (this.mUseVolumeKeySounds) {
                    if (this.mUseMasterVolume) {
                        adjustMasterVolume(0, 4);
                    } else {
                        adjustSuggestedStreamVolume(0, stream, 4);
                    }
                }
                this.mVolumeKeyUpTime = SystemClock.uptimeMillis();
                return;
            default:
                return;
        }
    }

    public void adjustStreamVolume(int streamType, int direction, int flags) {
        IAudioService service = getService();
        try {
            if (this.mUseMasterVolume) {
                service.adjustMasterVolume(direction, flags, this.mContext.getOpPackageName());
            } else {
                service.adjustStreamVolume(streamType, direction, flags, this.mContext.getOpPackageName());
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in adjustStreamVolume", e);
        }
    }

    public void adjustVolume(int direction, int flags) {
        IAudioService service = getService();
        try {
            if (this.mUseMasterVolume) {
                service.adjustMasterVolume(direction, flags, this.mContext.getOpPackageName());
            } else {
                service.adjustVolume(direction, flags, this.mContext.getOpPackageName());
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in adjustVolume", e);
        }
    }

    public void adjustSuggestedStreamVolume(int direction, int suggestedStreamType, int flags) {
        IAudioService service = getService();
        try {
            if (this.mUseMasterVolume) {
                service.adjustMasterVolume(direction, flags, this.mContext.getOpPackageName());
            } else {
                service.adjustSuggestedStreamVolume(direction, suggestedStreamType, flags, this.mContext.getOpPackageName());
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in adjustSuggestedStreamVolume", e);
        }
    }

    public void adjustMasterVolume(int steps, int flags) {
        IAudioService service = getService();
        try {
            service.adjustMasterVolume(steps, flags, this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in adjustMasterVolume", e);
        }
    }

    public int getRingerMode() {
        IAudioService service = getService();
        try {
            return service.getRingerMode();
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in getRingerMode", e);
            return 2;
        }
    }

    public static boolean isValidRingerMode(int ringerMode) {
        if (ringerMode < 0 || ringerMode > 2) {
            return false;
        }
        return true;
    }

    public int getStreamMaxVolume(int streamType) {
        IAudioService service = getService();
        try {
            if (this.mUseMasterVolume) {
                return service.getMasterMaxVolume();
            }
            return service.getStreamMaxVolume(streamType);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in getStreamMaxVolume", e);
            return 0;
        }
    }

    public int getStreamVolume(int streamType) {
        IAudioService service = getService();
        try {
            if (this.mUseMasterVolume) {
                return service.getMasterVolume();
            }
            return service.getStreamVolume(streamType);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in getStreamVolume", e);
            return 0;
        }
    }

    public int getLastAudibleStreamVolume(int streamType) {
        IAudioService service = getService();
        try {
            if (this.mUseMasterVolume) {
                return service.getLastAudibleMasterVolume();
            }
            return service.getLastAudibleStreamVolume(streamType);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in getLastAudibleStreamVolume", e);
            return 0;
        }
    }

    public int getMasterStreamType() {
        IAudioService service = getService();
        try {
            return service.getMasterStreamType();
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in getMasterStreamType", e);
            return 2;
        }
    }

    public void setRingerMode(int ringerMode) {
        if (!isValidRingerMode(ringerMode)) {
            return;
        }
        IAudioService service = getService();
        try {
            service.setRingerMode(ringerMode);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in setRingerMode", e);
        }
    }

    public void setStreamVolume(int streamType, int index, int flags) {
        IAudioService service = getService();
        try {
            if (this.mUseMasterVolume) {
                service.setMasterVolume(index, flags, this.mContext.getOpPackageName());
            } else {
                service.setStreamVolume(streamType, index, flags, this.mContext.getOpPackageName());
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in setStreamVolume", e);
        }
    }

    public int getMasterMaxVolume() {
        IAudioService service = getService();
        try {
            return service.getMasterMaxVolume();
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in getMasterMaxVolume", e);
            return 0;
        }
    }

    public int getMasterVolume() {
        IAudioService service = getService();
        try {
            return service.getMasterVolume();
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in getMasterVolume", e);
            return 0;
        }
    }

    public int getLastAudibleMasterVolume() {
        IAudioService service = getService();
        try {
            return service.getLastAudibleMasterVolume();
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in getLastAudibleMasterVolume", e);
            return 0;
        }
    }

    public void setMasterVolume(int index, int flags) {
        IAudioService service = getService();
        try {
            service.setMasterVolume(index, flags, this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in setMasterVolume", e);
        }
    }

    public void setStreamSolo(int streamType, boolean state) {
        IAudioService service = getService();
        try {
            service.setStreamSolo(streamType, state, this.mICallBack);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in setStreamSolo", e);
        }
    }

    public void setStreamMute(int streamType, boolean state) {
        IAudioService service = getService();
        try {
            service.setStreamMute(streamType, state, this.mICallBack);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in setStreamMute", e);
        }
    }

    public boolean isStreamMute(int streamType) {
        IAudioService service = getService();
        try {
            return service.isStreamMute(streamType);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in isStreamMute", e);
            return false;
        }
    }

    public void setMasterMute(boolean state) {
        setMasterMute(state, 1);
    }

    public void setMasterMute(boolean state, int flags) {
        IAudioService service = getService();
        try {
            service.setMasterMute(state, flags, this.mICallBack);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in setMasterMute", e);
        }
    }

    public boolean isMasterMute() {
        IAudioService service = getService();
        try {
            return service.isMasterMute();
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in isMasterMute", e);
            return false;
        }
    }

    public void forceVolumeControlStream(int streamType) {
        IAudioService service = getService();
        try {
            service.forceVolumeControlStream(streamType, this.mICallBack);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in forceVolumeControlStream", e);
        }
    }

    public boolean shouldVibrate(int vibrateType) {
        IAudioService service = getService();
        try {
            return service.shouldVibrate(vibrateType);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in shouldVibrate", e);
            return false;
        }
    }

    public int getVibrateSetting(int vibrateType) {
        IAudioService service = getService();
        try {
            return service.getVibrateSetting(vibrateType);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in getVibrateSetting", e);
            return 0;
        }
    }

    public void setVibrateSetting(int vibrateType, int vibrateSetting) {
        IAudioService service = getService();
        try {
            service.setVibrateSetting(vibrateType, vibrateSetting);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in setVibrateSetting", e);
        }
    }

    public void setSpeakerphoneOn(boolean on) {
        IAudioService service = getService();
        try {
            service.setSpeakerphoneOn(on);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in setSpeakerphoneOn", e);
        }
    }

    public boolean isSpeakerphoneOn() {
        IAudioService service = getService();
        try {
            return service.isSpeakerphoneOn();
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in isSpeakerphoneOn", e);
            return false;
        }
    }

    public boolean isBluetoothScoAvailableOffCall() {
        return this.mContext.getResources().getBoolean(R.bool.config_bluetooth_sco_off_call);
    }

    public void startBluetoothSco() {
        IAudioService service = getService();
        try {
            service.startBluetoothSco(this.mICallBack, this.mContext.getApplicationInfo().targetSdkVersion);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in startBluetoothSco", e);
        }
    }

    public void stopBluetoothSco() {
        IAudioService service = getService();
        try {
            service.stopBluetoothSco(this.mICallBack);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in stopBluetoothSco", e);
        }
    }

    public void setBluetoothScoOn(boolean on) {
        IAudioService service = getService();
        try {
            service.setBluetoothScoOn(on);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in setBluetoothScoOn", e);
        }
    }

    public boolean isBluetoothScoOn() {
        IAudioService service = getService();
        try {
            return service.isBluetoothScoOn();
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in isBluetoothScoOn", e);
            return false;
        }
    }

    @Deprecated
    public void setBluetoothA2dpOn(boolean on) {
    }

    public boolean isBluetoothA2dpOn() {
        if (AudioSystem.getDeviceConnectionState(128, "") == 0) {
            return false;
        }
        return true;
    }

    @Deprecated
    public void setWiredHeadsetOn(boolean on) {
    }

    public boolean isWiredHeadsetOn() {
        if (AudioSystem.getDeviceConnectionState(4, "") == 0 && AudioSystem.getDeviceConnectionState(8, "") == 0) {
            return false;
        }
        return true;
    }

    public void setMicrophoneMute(boolean on) {
        AudioSystem.muteMicrophone(on);
    }

    public boolean isMicrophoneMute() {
        return AudioSystem.isMicrophoneMuted();
    }

    public void setMode(int mode) {
        IAudioService service = getService();
        try {
            service.setMode(mode, this.mICallBack);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in setMode", e);
        }
    }

    public int getMode() {
        IAudioService service = getService();
        try {
            return service.getMode();
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in getMode", e);
            return -2;
        }
    }

    @Deprecated
    public void setRouting(int mode, int routes, int mask) {
    }

    @Deprecated
    public int getRouting(int mode) {
        return -1;
    }

    public boolean isMusicActive() {
        return AudioSystem.isStreamActive(3, 0);
    }

    public boolean isMusicActiveRemotely() {
        return AudioSystem.isStreamActiveRemotely(3, 0);
    }

    public boolean isLocalOrRemoteMusicActive() {
        IAudioService service = getService();
        try {
            return service.isLocalOrRemoteMusicActive();
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in isLocalOrRemoteMusicActive()", e);
            return false;
        }
    }

    public boolean isAudioFocusExclusive() {
        IAudioService service = getService();
        try {
            return service.getCurrentAudioFocus() == 4;
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in isAudioFocusExclusive()", e);
            return false;
        }
    }

    public void adjustLocalOrRemoteStreamVolume(int streamType, int direction) {
        if (streamType != 3) {
            Log.w(TAG, "adjustLocalOrRemoteStreamVolume() doesn't support stream " + streamType);
        }
        IAudioService service = getService();
        try {
            service.adjustLocalOrRemoteStreamVolume(streamType, direction, this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in adjustLocalOrRemoteStreamVolume", e);
        }
    }

    @Deprecated
    public void setParameter(String key, String value) {
        setParameters(key + Separators.EQUALS + value);
    }

    public void setParameters(String keyValuePairs) {
        AudioSystem.setParameters(keyValuePairs);
    }

    public String getParameters(String keys) {
        return AudioSystem.getParameters(keys);
    }

    public void playSoundEffect(int effectType) {
        if (effectType < 0 || effectType >= 10 || !querySoundEffectsEnabled()) {
            return;
        }
        IAudioService service = getService();
        try {
            service.playSoundEffect(effectType);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in playSoundEffect" + e);
        }
    }

    public void playSoundEffect(int effectType, float volume) {
        if (effectType < 0 || effectType >= 10) {
            return;
        }
        IAudioService service = getService();
        try {
            service.playSoundEffectVolume(effectType, volume);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in playSoundEffect" + e);
        }
    }

    private boolean querySoundEffectsEnabled() {
        return Settings.System.getInt(this.mContext.getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, 0) != 0;
    }

    public void loadSoundEffects() {
        IAudioService service = getService();
        try {
            service.loadSoundEffects();
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in loadSoundEffects" + e);
        }
    }

    public void unloadSoundEffects() {
        IAudioService service = getService();
        try {
            service.unloadSoundEffects();
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in unloadSoundEffects" + e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public OnAudioFocusChangeListener findFocusListener(String id) {
        return this.mAudioFocusIdListenerMap.get(id);
    }

    /* loaded from: AudioManager$FocusEventHandlerDelegate.class */
    private class FocusEventHandlerDelegate {
        private final Handler mHandler;

        FocusEventHandlerDelegate() {
            Looper myLooper = Looper.myLooper();
            Looper looper = myLooper;
            looper = myLooper == null ? Looper.getMainLooper() : looper;
            if (looper != null) {
                this.mHandler = new Handler(looper) { // from class: android.media.AudioManager.FocusEventHandlerDelegate.1
                    @Override // android.os.Handler
                    public void handleMessage(Message msg) {
                        OnAudioFocusChangeListener listener;
                        synchronized (AudioManager.this.mFocusListenerLock) {
                            listener = AudioManager.this.findFocusListener((String) msg.obj);
                        }
                        if (listener != null) {
                            listener.onAudioFocusChange(msg.what);
                        }
                    }
                };
            } else {
                this.mHandler = null;
            }
        }

        Handler getHandler() {
            return this.mHandler;
        }
    }

    private String getIdForAudioFocusListener(OnAudioFocusChangeListener l) {
        if (l == null) {
            return new String(toString());
        }
        return new String(toString() + l.toString());
    }

    public void registerAudioFocusListener(OnAudioFocusChangeListener l) {
        synchronized (this.mFocusListenerLock) {
            if (this.mAudioFocusIdListenerMap.containsKey(getIdForAudioFocusListener(l))) {
                return;
            }
            this.mAudioFocusIdListenerMap.put(getIdForAudioFocusListener(l), l);
        }
    }

    public void unregisterAudioFocusListener(OnAudioFocusChangeListener l) {
        synchronized (this.mFocusListenerLock) {
            this.mAudioFocusIdListenerMap.remove(getIdForAudioFocusListener(l));
        }
    }

    public int requestAudioFocus(OnAudioFocusChangeListener l, int streamType, int durationHint) {
        int status = 0;
        if (durationHint < 1 || durationHint > 4) {
            Log.e(TAG, "Invalid duration hint, audio focus request denied");
            return 0;
        }
        registerAudioFocusListener(l);
        IAudioService service = getService();
        try {
            status = service.requestAudioFocus(streamType, durationHint, this.mICallBack, this.mAudioFocusDispatcher, getIdForAudioFocusListener(l), this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            Log.e(TAG, "Can't call requestAudioFocus() on AudioService due to " + e);
        }
        return status;
    }

    public void requestAudioFocusForCall(int streamType, int durationHint) {
        IAudioService service = getService();
        try {
            service.requestAudioFocus(streamType, durationHint, this.mICallBack, null, "AudioFocus_For_Phone_Ring_And_Calls", this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            Log.e(TAG, "Can't call requestAudioFocusForCall() on AudioService due to " + e);
        }
    }

    public void abandonAudioFocusForCall() {
        IAudioService service = getService();
        try {
            service.abandonAudioFocus(null, "AudioFocus_For_Phone_Ring_And_Calls");
        } catch (RemoteException e) {
            Log.e(TAG, "Can't call abandonAudioFocusForCall() on AudioService due to " + e);
        }
    }

    public int abandonAudioFocus(OnAudioFocusChangeListener l) {
        int status = 0;
        unregisterAudioFocusListener(l);
        IAudioService service = getService();
        try {
            status = service.abandonAudioFocus(this.mAudioFocusDispatcher, getIdForAudioFocusListener(l));
        } catch (RemoteException e) {
            Log.e(TAG, "Can't call abandonAudioFocus() on AudioService due to " + e);
        }
        return status;
    }

    public void registerMediaButtonEventReceiver(ComponentName eventReceiver) {
        if (eventReceiver == null) {
            return;
        }
        if (!eventReceiver.getPackageName().equals(this.mContext.getPackageName())) {
            Log.e(TAG, "registerMediaButtonEventReceiver() error: receiver and context package names don't match");
            return;
        }
        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setComponent(eventReceiver);
        PendingIntent pi = PendingIntent.getBroadcast(this.mContext, 0, mediaButtonIntent, 0);
        registerMediaButtonIntent(pi, eventReceiver);
    }

    public void registerMediaButtonEventReceiver(PendingIntent eventReceiver) {
        if (eventReceiver == null) {
            return;
        }
        registerMediaButtonIntent(eventReceiver, null);
    }

    public void registerMediaButtonIntent(PendingIntent pi, ComponentName eventReceiver) {
        Binder binder;
        if (pi == null) {
            Log.e(TAG, "Cannot call registerMediaButtonIntent() with a null parameter");
            return;
        }
        IAudioService service = getService();
        if (eventReceiver == null) {
            try {
                binder = this.mToken;
            } catch (RemoteException e) {
                Log.e(TAG, "Dead object in registerMediaButtonIntent" + e);
                return;
            }
        } else {
            binder = null;
        }
        service.registerMediaButtonIntent(pi, eventReceiver, binder);
    }

    public void registerMediaButtonEventReceiverForCalls(ComponentName eventReceiver) {
        if (eventReceiver == null) {
            return;
        }
        IAudioService service = getService();
        try {
            service.registerMediaButtonEventReceiverForCalls(eventReceiver);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in registerMediaButtonEventReceiverForCalls", e);
        }
    }

    public void unregisterMediaButtonEventReceiverForCalls() {
        IAudioService service = getService();
        try {
            service.unregisterMediaButtonEventReceiverForCalls();
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in unregisterMediaButtonEventReceiverForCalls", e);
        }
    }

    public void unregisterMediaButtonEventReceiver(ComponentName eventReceiver) {
        if (eventReceiver == null) {
            return;
        }
        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setComponent(eventReceiver);
        PendingIntent pi = PendingIntent.getBroadcast(this.mContext, 0, mediaButtonIntent, 0);
        unregisterMediaButtonIntent(pi);
    }

    public void unregisterMediaButtonEventReceiver(PendingIntent eventReceiver) {
        if (eventReceiver == null) {
            return;
        }
        unregisterMediaButtonIntent(eventReceiver);
    }

    public void unregisterMediaButtonIntent(PendingIntent pi) {
        IAudioService service = getService();
        try {
            service.unregisterMediaButtonIntent(pi);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in unregisterMediaButtonIntent" + e);
        }
    }

    public void registerRemoteControlClient(RemoteControlClient rcClient) {
        if (rcClient == null || rcClient.getRcMediaIntent() == null) {
            return;
        }
        IAudioService service = getService();
        try {
            int rcseId = service.registerRemoteControlClient(rcClient.getRcMediaIntent(), rcClient.getIRemoteControlClient(), this.mContext.getPackageName());
            rcClient.setRcseId(rcseId);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in registerRemoteControlClient" + e);
        }
    }

    public void unregisterRemoteControlClient(RemoteControlClient rcClient) {
        if (rcClient == null || rcClient.getRcMediaIntent() == null) {
            return;
        }
        IAudioService service = getService();
        try {
            service.unregisterRemoteControlClient(rcClient.getRcMediaIntent(), rcClient.getIRemoteControlClient());
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in unregisterRemoteControlClient" + e);
        }
    }

    public boolean registerRemoteController(RemoteController rctlr) {
        if (rctlr == null) {
            return false;
        }
        IAudioService service = getService();
        RemoteController.OnClientUpdateListener l = rctlr.getUpdateListener();
        ComponentName listenerComponent = new ComponentName(this.mContext, l.getClass());
        try {
            int[] artworkDimensions = rctlr.getArtworkSize();
            boolean reg = service.registerRemoteController(rctlr.getRcDisplay(), artworkDimensions[0], artworkDimensions[1], listenerComponent);
            rctlr.setIsRegistered(reg);
            return reg;
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in registerRemoteController " + e);
            return false;
        }
    }

    public void unregisterRemoteController(RemoteController rctlr) {
        if (rctlr == null) {
            return;
        }
        IAudioService service = getService();
        try {
            service.unregisterRemoteControlDisplay(rctlr.getRcDisplay());
            rctlr.setIsRegistered(false);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in unregisterRemoteControlDisplay " + e);
        }
    }

    public void registerRemoteControlDisplay(IRemoteControlDisplay rcd) {
        registerRemoteControlDisplay(rcd, -1, -1);
    }

    public void registerRemoteControlDisplay(IRemoteControlDisplay rcd, int w, int h) {
        if (rcd == null) {
            return;
        }
        IAudioService service = getService();
        try {
            service.registerRemoteControlDisplay(rcd, w, h);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in registerRemoteControlDisplay " + e);
        }
    }

    public void unregisterRemoteControlDisplay(IRemoteControlDisplay rcd) {
        if (rcd == null) {
            return;
        }
        IAudioService service = getService();
        try {
            service.unregisterRemoteControlDisplay(rcd);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in unregisterRemoteControlDisplay " + e);
        }
    }

    public void remoteControlDisplayUsesBitmapSize(IRemoteControlDisplay rcd, int w, int h) {
        if (rcd == null) {
            return;
        }
        IAudioService service = getService();
        try {
            service.remoteControlDisplayUsesBitmapSize(rcd, w, h);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in remoteControlDisplayUsesBitmapSize " + e);
        }
    }

    public void remoteControlDisplayWantsPlaybackPositionSync(IRemoteControlDisplay rcd, boolean wantsSync) {
        if (rcd == null) {
            return;
        }
        IAudioService service = getService();
        try {
            service.remoteControlDisplayWantsPlaybackPositionSync(rcd, wantsSync);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in remoteControlDisplayWantsPlaybackPositionSync " + e);
        }
    }

    public void setRemoteControlClientPlaybackPosition(int generationId, long timeMs) {
        if (timeMs < 0) {
            return;
        }
        IAudioService service = getService();
        try {
            service.setRemoteControlClientPlaybackPosition(generationId, timeMs);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in setRccPlaybackPosition(" + generationId + ", " + timeMs + Separators.RPAREN, e);
        }
    }

    public void updateRemoteControlClientMetadata(int generationId, int key, Rating value) {
        IAudioService service = getService();
        try {
            service.updateRemoteControlClientMetadata(generationId, key, value);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in updateRemoteControlClientMetadata(" + generationId + ", " + key + ", " + value + Separators.RPAREN, e);
        }
    }

    public void reloadAudioSettings() {
        IAudioService service = getService();
        try {
            service.reloadAudioSettings();
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in reloadAudioSettings" + e);
        }
    }

    public void avrcpSupportsAbsoluteVolume(String address, boolean support) {
        IAudioService service = getService();
        try {
            service.avrcpSupportsAbsoluteVolume(address, support);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in avrcpSupportsAbsoluteVolume", e);
        }
    }

    public boolean isSilentMode() {
        int ringerMode = getRingerMode();
        boolean silentMode = ringerMode == 0 || ringerMode == 1;
        return silentMode;
    }

    public int getDevicesForStream(int streamType) {
        switch (streamType) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 8:
                return AudioSystem.getDevicesForStream(streamType);
            case 6:
            case 7:
            default:
                return 0;
        }
    }

    public void setWiredDeviceConnectionState(int device, int state, String name) {
        IAudioService service = getService();
        try {
            service.setWiredDeviceConnectionState(device, state, name);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in setWiredDeviceConnectionState " + e);
        }
    }

    public int setBluetoothA2dpDeviceConnectionState(BluetoothDevice device, int state) {
        IAudioService service = getService();
        int delay = 0;
        try {
            try {
                delay = service.setBluetoothA2dpDeviceConnectionState(device, state);
                return delay;
            } catch (RemoteException e) {
                Log.e(TAG, "Dead object in setBluetoothA2dpDeviceConnectionState " + e);
                return delay;
            }
        } catch (Throwable th) {
            return delay;
        }
    }

    public IRingtonePlayer getRingtonePlayer() {
        try {
            return getService().getRingtonePlayer();
        } catch (RemoteException e) {
            return null;
        }
    }

    public String getProperty(String key) {
        int outputFramesPerBuffer;
        if (PROPERTY_OUTPUT_SAMPLE_RATE.equals(key)) {
            int outputSampleRate = AudioSystem.getPrimaryOutputSamplingRate();
            if (outputSampleRate > 0) {
                return Integer.toString(outputSampleRate);
            }
            return null;
        } else if (!PROPERTY_OUTPUT_FRAMES_PER_BUFFER.equals(key) || (outputFramesPerBuffer = AudioSystem.getPrimaryOutputFrameCount()) <= 0) {
            return null;
        } else {
            return Integer.toString(outputFramesPerBuffer);
        }
    }

    public int getOutputLatency(int streamType) {
        return AudioSystem.getOutputLatency(streamType);
    }
}