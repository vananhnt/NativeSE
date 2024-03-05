package android.media;

import android.Manifest;
import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.app.AppOpsManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothInputDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.database.ContentObserver;
import android.hardware.usb.UsbManager;
import android.media.AudioSystem;
import android.media.IAudioService;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.TimedRemoteCaller;
import android.view.KeyEvent;
import android.view.VolumePanel;
import android.view.WindowManager;
import com.android.internal.R;
import com.android.internal.telephony.ITelephony;
import com.android.internal.util.XmlUtils;
import gov.nist.core.Separators;
import gov.nist.javax.sip.header.ParameterNames;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: AudioService.class */
public class AudioService extends IAudioService.Stub {
    private static final String TAG = "AudioService";
    protected static final boolean DEBUG_RC = false;
    protected static final boolean DEBUG_VOL = false;
    private static final int PERSIST_DELAY = 500;
    private final Context mContext;
    private final ContentResolver mContentResolver;
    private final AppOpsManager mAppOps;
    private final boolean mVoiceCapable;
    private VolumePanel mVolumePanel;
    private static final int SENDMSG_REPLACE = 0;
    private static final int SENDMSG_NOOP = 1;
    private static final int SENDMSG_QUEUE = 2;
    private static final int MSG_SET_DEVICE_VOLUME = 0;
    private static final int MSG_PERSIST_VOLUME = 1;
    private static final int MSG_PERSIST_MASTER_VOLUME = 2;
    private static final int MSG_PERSIST_RINGER_MODE = 3;
    private static final int MSG_MEDIA_SERVER_DIED = 4;
    private static final int MSG_PLAY_SOUND_EFFECT = 5;
    private static final int MSG_BTA2DP_DOCK_TIMEOUT = 6;
    private static final int MSG_LOAD_SOUND_EFFECTS = 7;
    private static final int MSG_SET_FORCE_USE = 8;
    private static final int MSG_BT_HEADSET_CNCT_FAILED = 9;
    private static final int MSG_SET_ALL_VOLUMES = 10;
    private static final int MSG_PERSIST_MASTER_VOLUME_MUTE = 11;
    private static final int MSG_REPORT_NEW_ROUTES = 12;
    private static final int MSG_SET_FORCE_BT_A2DP_USE = 13;
    private static final int MSG_CHECK_MUSIC_ACTIVE = 14;
    private static final int MSG_BROADCAST_AUDIO_BECOMING_NOISY = 15;
    private static final int MSG_CONFIGURE_SAFE_MEDIA_VOLUME = 16;
    private static final int MSG_CONFIGURE_SAFE_MEDIA_VOLUME_FORCED = 17;
    private static final int MSG_PERSIST_SAFE_VOLUME_STATE = 18;
    private static final int MSG_BROADCAST_BT_CONNECTION_STATE = 19;
    private static final int MSG_UNLOAD_SOUND_EFFECTS = 20;
    private static final int MSG_SET_WIRED_DEVICE_CONNECTION_STATE = 100;
    private static final int MSG_SET_A2DP_CONNECTION_STATE = 101;
    private static final int BTA2DP_DOCK_TIMEOUT_MILLIS = 8000;
    private static final int BT_HEADSET_CNCT_TIMEOUT_MS = 3000;
    private AudioSystemThread mAudioSystemThread;
    private AudioHandler mAudioHandler;
    private VolumeStreamState[] mStreamStates;
    private SettingsObserver mSettingsObserver;
    private SoundPool mSoundPool;
    private static final int NUM_SOUNDPOOL_CHANNELS = 4;
    private static final int MAX_MASTER_VOLUME = 100;
    private static final int MAX_BATCH_VOLUME_ADJUST_STEPS = 4;
    private static final String SOUND_EFFECTS_PATH = "/media/audio/ui/";
    private int[] mStreamVolumeAlias;
    private final boolean mUseFixedVolume;
    private int mRingerMode;
    private int mRingerModeMutedStreams;
    private int mMuteAffectedStreams;
    private int mVibrateSetting;
    private final boolean mHasVibrator;
    private int mForcedUseForComm;
    private final boolean mUseMasterVolume;
    private final int[] mMasterVolumeRamp;
    private BluetoothHeadset mBluetoothHeadset;
    private BluetoothDevice mBluetoothHeadsetDevice;
    private int mScoAudioState;
    private static final int SCO_STATE_INACTIVE = 0;
    private static final int SCO_STATE_ACTIVATE_REQ = 1;
    private static final int SCO_STATE_ACTIVE_INTERNAL = 3;
    private static final int SCO_STATE_DEACTIVATE_REQ = 5;
    private static final int SCO_STATE_ACTIVE_EXTERNAL = 2;
    private static final int SCO_STATE_DEACTIVATE_EXT_REQ = 4;
    private int mScoAudioMode;
    private static final int SCO_MODE_VIRTUAL_CALL = 0;
    private static final int SCO_MODE_RAW = 1;
    private int mScoConnectionState;
    private boolean mBootCompleted;
    private SoundPoolCallback mSoundPoolCallBack;
    private SoundPoolListenerThread mSoundPoolListenerThread;
    private static int sSoundEffectVolumeDb;
    private static final int DEFAULT_STREAM_TYPE_OVERRIDE_DELAY_MS = 5000;
    private KeyguardManager mKeyguardManager;
    private volatile IRingtonePlayer mRingtonePlayer;
    private int mDeviceRotation;
    private boolean mBluetoothA2dpEnabled;
    public static final int STREAM_REMOTE_MUSIC = -200;
    private final boolean mMonitorOrientation;
    private final boolean mMonitorRotation;
    private StreamVolumeCommand mPendingVolumeCommand;
    private PowerManager.WakeLock mAudioEventWakeLock;
    private final MediaFocusControl mMediaFocusControl;
    private BluetoothA2dp mA2dp;
    private static final String TAG_AUDIO_ASSETS = "audio_assets";
    private static final String ATTR_VERSION = "version";
    private static final String TAG_GROUP = "group";
    private static final String ATTR_GROUP_NAME = "name";
    private static final String TAG_ASSET = "asset";
    private static final String ATTR_ASSET_ID = "id";
    private static final String ATTR_ASSET_FILE = "file";
    private static final String ASSET_FILE_VERSION = "1.0";
    private static final String GROUP_TOUCH_SOUNDS = "touch_sounds";
    private static final int SOUND_EFECTS_LOAD_TIMEOUT_MS = 5000;
    private String mDockAddress;
    private Integer mSafeMediaVolumeState;
    private int mSafeMediaVolumeIndex;
    private int mMusicActiveMs;
    private static final int UNSAFE_VOLUME_MUSIC_ACTIVE_MS_MAX = 72000000;
    private static final int MUSIC_ACTIVE_POLL_PERIOD_MS = 60000;
    private static final int SAFE_VOLUME_CONFIGURE_TIMEOUT_MS = 30000;
    private Boolean mCameraSoundForced;
    private static final List<String> SOUND_EFFECT_FILES = new ArrayList();
    private static final int[] MAX_STREAM_VOLUME = {5, 7, 7, 15, 7, 7, 15, 7, 15, 15};
    private static final int[] STEAM_VOLUME_OPS = {34, 36, 35, 36, 37, 38, 39, 36, 36, 36};
    private static final String[] RINGER_MODE_NAMES = {"SILENT", "VIBRATE", "NORMAL"};
    private int mMode = 0;
    private final Object mSettingsLock = new Object();
    private final Object mSoundEffectsLock = new Object();
    private final int[][] SOUND_EFFECT_FILES_MAP = new int[10][2];
    private final int[] STREAM_VOLUME_ALIAS = {0, 2, 2, 3, 4, 2, 6, 2, 2, 3};
    private final int[] STREAM_VOLUME_ALIAS_NON_VOICE = {0, 3, 2, 3, 4, 2, 6, 3, 3, 3};
    private final String[] STREAM_NAMES = {"STREAM_VOICE_CALL", "STREAM_SYSTEM", "STREAM_RING", "STREAM_MUSIC", "STREAM_ALARM", "STREAM_NOTIFICATION", "STREAM_BLUETOOTH_SCO", "STREAM_SYSTEM_ENFORCED", "STREAM_DTMF", "STREAM_TTS"};
    private final AudioSystem.ErrorCallback mAudioSystemCallback = new AudioSystem.ErrorCallback() { // from class: android.media.AudioService.1
        @Override // android.media.AudioSystem.ErrorCallback
        public void onError(int error) {
            switch (error) {
                case 100:
                    AudioService.sendMsg(AudioService.this.mAudioHandler, 4, 1, 0, 0, null, 0);
                    return;
                default:
                    return;
            }
        }
    };
    private int mRingerModeAffectedStreams = 0;
    private final BroadcastReceiver mReceiver = new AudioServiceBroadcastReceiver();
    private final HashMap<Integer, String> mConnectedDevices = new HashMap<>();
    private final ArrayList<SetModeDeathHandler> mSetModeDeathHandlers = new ArrayList<>();
    private final ArrayList<ScoClient> mScoClients = new ArrayList<>();
    private Looper mSoundPoolLooper = null;
    private int mPrevVolDirection = 0;
    private int mVolumeControlStream = -1;
    private final Object mForceControlStreamLock = new Object();
    private ForceControlStreamClient mForceControlStreamClient = null;
    private int mDeviceOrientation = 0;
    private final Object mBluetoothA2dpEnabledLock = new Object();
    final AudioRoutesInfo mCurAudioRoutes = new AudioRoutesInfo();
    final RemoteCallbackList<IAudioRoutesObserver> mRoutesObservers = new RemoteCallbackList<>();
    final int mFixedVolumeDevices = 31744;
    private boolean mDockAudioMediaEnabled = true;
    private int mDockState = 0;
    private final Object mA2dpAvrcpLock = new Object();
    private boolean mAvrcpAbsVolSupported = false;
    private BluetoothProfile.ServiceListener mBluetoothProfileServiceListener = new BluetoothProfile.ServiceListener() { // from class: android.media.AudioService.2
        @Override // android.bluetooth.BluetoothProfile.ServiceListener
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            switch (profile) {
                case 1:
                    synchronized (AudioService.this.mScoClients) {
                        AudioService.this.mAudioHandler.removeMessages(9);
                        AudioService.this.mBluetoothHeadset = (BluetoothHeadset) proxy;
                        List<BluetoothDevice> deviceList = AudioService.this.mBluetoothHeadset.getConnectedDevices();
                        if (deviceList.size() > 0) {
                            AudioService.this.mBluetoothHeadsetDevice = deviceList.get(0);
                        } else {
                            AudioService.this.mBluetoothHeadsetDevice = null;
                        }
                        AudioService.this.checkScoAudioState();
                        if (AudioService.this.mScoAudioState == 1 || AudioService.this.mScoAudioState == 5 || AudioService.this.mScoAudioState == 4) {
                            boolean status = false;
                            if (AudioService.this.mBluetoothHeadsetDevice != null) {
                                switch (AudioService.this.mScoAudioState) {
                                    case 1:
                                        AudioService.this.mScoAudioState = 3;
                                        if (AudioService.this.mScoAudioMode == 1) {
                                            status = AudioService.this.mBluetoothHeadset.connectAudio();
                                            break;
                                        } else {
                                            status = AudioService.this.mBluetoothHeadset.startScoUsingVirtualVoiceCall(AudioService.this.mBluetoothHeadsetDevice);
                                            break;
                                        }
                                    case 4:
                                        status = AudioService.this.mBluetoothHeadset.stopVoiceRecognition(AudioService.this.mBluetoothHeadsetDevice);
                                        break;
                                    case 5:
                                        if (AudioService.this.mScoAudioMode == 1) {
                                            status = AudioService.this.mBluetoothHeadset.disconnectAudio();
                                            break;
                                        } else {
                                            status = AudioService.this.mBluetoothHeadset.stopScoUsingVirtualVoiceCall(AudioService.this.mBluetoothHeadsetDevice);
                                            break;
                                        }
                                }
                            }
                            if (!status) {
                                AudioService.sendMsg(AudioService.this.mAudioHandler, 9, 0, 0, 0, null, 0);
                            }
                        }
                    }
                    return;
                case 2:
                    synchronized (AudioService.this.mA2dpAvrcpLock) {
                        AudioService.this.mA2dp = (BluetoothA2dp) proxy;
                        List<BluetoothDevice> deviceList2 = AudioService.this.mA2dp.getConnectedDevices();
                        if (deviceList2.size() > 0) {
                            BluetoothDevice btDevice = deviceList2.get(0);
                            synchronized (AudioService.this.mConnectedDevices) {
                                int state = AudioService.this.mA2dp.getConnectionState(btDevice);
                                int delay = AudioService.this.checkSendBecomingNoisyIntent(128, state == 2 ? 1 : 0);
                                AudioService.this.queueMsgUnderWakeLock(AudioService.this.mAudioHandler, 101, state, 0, btDevice, delay);
                            }
                        }
                    }
                    return;
                default:
                    return;
            }
        }

        @Override // android.bluetooth.BluetoothProfile.ServiceListener
        public void onServiceDisconnected(int profile) {
            switch (profile) {
                case 1:
                    synchronized (AudioService.this.mScoClients) {
                        AudioService.this.mBluetoothHeadset = null;
                    }
                    return;
                case 2:
                    synchronized (AudioService.this.mA2dpAvrcpLock) {
                        AudioService.this.mA2dp = null;
                        synchronized (AudioService.this.mConnectedDevices) {
                            if (AudioService.this.mConnectedDevices.containsKey(128)) {
                                AudioService.this.makeA2dpDeviceUnavailableNow((String) AudioService.this.mConnectedDevices.get(128));
                            }
                        }
                    }
                    return;
                default:
                    return;
            }
        }
    };
    int mBecomingNoisyIntentDevices = 32652;
    private final int SAFE_MEDIA_VOLUME_NOT_CONFIGURED = 0;
    private final int SAFE_MEDIA_VOLUME_DISABLED = 1;
    private final int SAFE_MEDIA_VOLUME_INACTIVE = 2;
    private final int SAFE_MEDIA_VOLUME_ACTIVE = 3;
    private int mMcc = 0;
    private final int mSafeMediaVolumeDevices = 12;

    public AudioService(Context context) {
        this.mDeviceRotation = 0;
        this.mContext = context;
        this.mContentResolver = context.getContentResolver();
        this.mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        this.mVoiceCapable = this.mContext.getResources().getBoolean(R.bool.config_voice_capable);
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        this.mAudioEventWakeLock = pm.newWakeLock(1, "handleAudioEvent");
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        this.mHasVibrator = vibrator == null ? false : vibrator.hasVibrator();
        MAX_STREAM_VOLUME[0] = SystemProperties.getInt("ro.config.vc_call_vol_steps", MAX_STREAM_VOLUME[0]);
        sSoundEffectVolumeDb = context.getResources().getInteger(R.integer.config_soundEffectVolumeDb);
        this.mVolumePanel = new VolumePanel(context, this);
        this.mForcedUseForComm = 0;
        createAudioSystemThread();
        this.mMediaFocusControl = new MediaFocusControl(this.mAudioHandler.getLooper(), this.mContext, this.mVolumePanel, this);
        AudioSystem.setErrorCallback(this.mAudioSystemCallback);
        boolean cameraSoundForced = this.mContext.getResources().getBoolean(R.bool.config_camera_sound_forced);
        this.mCameraSoundForced = new Boolean(cameraSoundForced);
        sendMsg(this.mAudioHandler, 8, 2, 4, cameraSoundForced ? 11 : 0, null, 0);
        this.mSafeMediaVolumeState = new Integer(Settings.Global.getInt(this.mContentResolver, Settings.Global.AUDIO_SAFE_VOLUME_STATE, 0));
        this.mSafeMediaVolumeIndex = this.mContext.getResources().getInteger(R.integer.config_safe_media_volume_index) * 10;
        this.mUseFixedVolume = this.mContext.getResources().getBoolean(R.bool.config_useFixedVolume);
        updateStreamVolumeAlias(false);
        readPersistedSettings();
        this.mSettingsObserver = new SettingsObserver();
        createStreamStates();
        readAndSetLowRamDevice();
        this.mRingerModeMutedStreams = 0;
        setRingerModeInt(getRingerMode(), false);
        IntentFilter intentFilter = new IntentFilter(BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED);
        intentFilter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
        intentFilter.addAction(Intent.ACTION_DOCK_EVENT);
        intentFilter.addAction(Intent.ACTION_USB_AUDIO_ACCESSORY_PLUG);
        intentFilter.addAction(Intent.ACTION_USB_AUDIO_DEVICE_PLUG);
        intentFilter.addAction(Intent.ACTION_BOOT_COMPLETED);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_USER_SWITCHED);
        intentFilter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
        this.mMonitorOrientation = SystemProperties.getBoolean("ro.audio.monitorOrientation", false);
        if (this.mMonitorOrientation) {
            Log.v(TAG, "monitoring device orientation");
            setOrientationForAudioSystem();
        }
        this.mMonitorRotation = SystemProperties.getBoolean("ro.audio.monitorRotation", false);
        if (this.mMonitorRotation) {
            this.mDeviceRotation = ((WindowManager) this.mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
            Log.v(TAG, "monitoring device rotation, initial=" + this.mDeviceRotation);
            setRotationForAudioSystem();
        }
        context.registerReceiver(this.mReceiver, intentFilter);
        this.mUseMasterVolume = context.getResources().getBoolean(R.bool.config_useMasterVolume);
        restoreMasterVolume();
        this.mMasterVolumeRamp = context.getResources().getIntArray(R.array.config_masterVolumeRamp);
    }

    private void createAudioSystemThread() {
        this.mAudioSystemThread = new AudioSystemThread();
        this.mAudioSystemThread.start();
        waitForAudioHandlerCreation();
    }

    private void waitForAudioHandlerCreation() {
        synchronized (this) {
            while (this.mAudioHandler == null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    Log.e(TAG, "Interrupted while waiting on volume handler.");
                }
            }
        }
    }

    private void checkAllAliasStreamVolumes() {
        int numStreamTypes = AudioSystem.getNumStreamTypes();
        for (int streamType = 0; streamType < numStreamTypes; streamType++) {
            if (streamType != this.mStreamVolumeAlias[streamType]) {
                this.mStreamStates[streamType].setAllIndexes(this.mStreamStates[this.mStreamVolumeAlias[streamType]]);
            }
            if (!this.mStreamStates[streamType].isMuted()) {
                this.mStreamStates[streamType].applyAllVolumes();
            }
        }
    }

    private void createStreamStates() {
        int numStreamTypes = AudioSystem.getNumStreamTypes();
        VolumeStreamState[] streams = new VolumeStreamState[numStreamTypes];
        this.mStreamStates = streams;
        for (int i = 0; i < numStreamTypes; i++) {
            streams[i] = new VolumeStreamState(Settings.System.VOLUME_SETTINGS[this.mStreamVolumeAlias[i]], i);
        }
        checkAllAliasStreamVolumes();
    }

    private void dumpStreamStates(PrintWriter pw) {
        pw.println("\nStream volumes (device: index)");
        int numStreamTypes = AudioSystem.getNumStreamTypes();
        for (int i = 0; i < numStreamTypes; i++) {
            pw.println("- " + this.STREAM_NAMES[i] + Separators.COLON);
            this.mStreamStates[i].dump(pw);
            pw.println("");
        }
        pw.print("\n- mute affected streams = 0x");
        pw.println(Integer.toHexString(this.mMuteAffectedStreams));
    }

    private void updateStreamVolumeAlias(boolean updateVolumes) {
        int dtmfStreamAlias;
        if (this.mVoiceCapable) {
            this.mStreamVolumeAlias = this.STREAM_VOLUME_ALIAS;
            dtmfStreamAlias = 2;
        } else {
            this.mStreamVolumeAlias = this.STREAM_VOLUME_ALIAS_NON_VOICE;
            dtmfStreamAlias = 3;
        }
        if (isInCommunication()) {
            dtmfStreamAlias = 0;
            this.mRingerModeAffectedStreams &= -257;
        } else {
            this.mRingerModeAffectedStreams |= 256;
        }
        this.mStreamVolumeAlias[8] = dtmfStreamAlias;
        if (updateVolumes) {
            this.mStreamStates[8].setAllIndexes(this.mStreamStates[dtmfStreamAlias]);
            setRingerModeInt(getRingerMode(), false);
            sendMsg(this.mAudioHandler, 10, 2, 0, 0, this.mStreamStates[8], 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void readDockAudioSettings(ContentResolver cr) {
        this.mDockAudioMediaEnabled = Settings.Global.getInt(cr, Settings.Global.DOCK_AUDIO_MEDIA_ENABLED, 0) == 1;
        if (this.mDockAudioMediaEnabled) {
            this.mBecomingNoisyIntentDevices |= 2048;
        } else {
            this.mBecomingNoisyIntentDevices &= -2049;
        }
        sendMsg(this.mAudioHandler, 8, 2, 3, this.mDockAudioMediaEnabled ? 8 : 0, null, 0);
    }

    private void readPersistedSettings() {
        ContentResolver cr = this.mContentResolver;
        int ringerModeFromSettings = Settings.Global.getInt(cr, "mode_ringer", 2);
        int ringerMode = ringerModeFromSettings;
        if (!AudioManager.isValidRingerMode(ringerMode)) {
            ringerMode = 2;
        }
        if (ringerMode == 1 && !this.mHasVibrator) {
            ringerMode = 0;
        }
        if (ringerMode != ringerModeFromSettings) {
            Settings.Global.putInt(cr, "mode_ringer", ringerMode);
        }
        if (this.mUseFixedVolume) {
            ringerMode = 2;
        }
        synchronized (this.mSettingsLock) {
            this.mRingerMode = ringerMode;
            this.mVibrateSetting = getValueForVibrateSetting(0, 1, this.mHasVibrator ? 2 : 0);
            this.mVibrateSetting = getValueForVibrateSetting(this.mVibrateSetting, 0, this.mHasVibrator ? 2 : 0);
            updateRingerModeAffectedStreams();
            readDockAudioSettings(cr);
        }
        this.mMuteAffectedStreams = Settings.System.getIntForUser(cr, Settings.System.MUTE_STREAMS_AFFECTED, 14, -2);
        boolean masterMute = Settings.System.getIntForUser(cr, Settings.System.VOLUME_MASTER_MUTE, 0, -2) == 1;
        if (this.mUseFixedVolume) {
            masterMute = false;
            AudioSystem.setMasterVolume(1.0f);
        }
        AudioSystem.setMasterMute(masterMute);
        broadcastMasterMuteStatus(masterMute);
        broadcastRingerMode(ringerMode);
        broadcastVibrateSetting(0);
        broadcastVibrateSetting(1);
        this.mMediaFocusControl.restoreMediaButtonReceiver();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int rescaleIndex(int index, int srcStream, int dstStream) {
        return ((index * this.mStreamStates[dstStream].getMaxIndex()) + (this.mStreamStates[srcStream].getMaxIndex() / 2)) / this.mStreamStates[srcStream].getMaxIndex();
    }

    @Override // android.media.IAudioService
    public boolean isLocalOrRemoteMusicActive() {
        if (AudioSystem.isStreamActive(3, 0) || this.mMediaFocusControl.checkUpdateRemoteStateIfActive(3) || AudioSystem.isStreamActiveRemotely(3, 0)) {
            return true;
        }
        return false;
    }

    @Override // android.media.IAudioService
    public void adjustVolume(int direction, int flags, String callingPackage) {
        adjustSuggestedStreamVolume(direction, Integer.MIN_VALUE, flags, callingPackage);
    }

    @Override // android.media.IAudioService
    public void adjustLocalOrRemoteStreamVolume(int streamType, int direction, String callingPackage) {
        if (AudioSystem.isStreamActive(3, 0)) {
            adjustStreamVolume(3, direction, 0, callingPackage);
        } else if (this.mMediaFocusControl.checkUpdateRemoteStateIfActive(3)) {
            this.mMediaFocusControl.adjustRemoteVolume(3, direction, 0);
        }
    }

    @Override // android.media.IAudioService
    public void adjustSuggestedStreamVolume(int direction, int suggestedStreamType, int flags, String callingPackage) {
        int streamType;
        if (this.mVolumeControlStream != -1) {
            streamType = this.mVolumeControlStream;
        } else {
            streamType = getActiveStreamType(suggestedStreamType);
        }
        if (streamType != -200 && (flags & 4) != 0 && (this.mStreamVolumeAlias[streamType] != 2 || (this.mKeyguardManager != null && this.mKeyguardManager.isKeyguardLocked()))) {
            flags &= -5;
        }
        if (streamType == -200) {
            this.mMediaFocusControl.adjustRemoteVolume(3, direction, flags & (-37));
            return;
        }
        adjustStreamVolume(streamType, direction, flags, callingPackage);
    }

    @Override // android.media.IAudioService
    public void adjustStreamVolume(int streamType, int direction, int flags, String callingPackage) {
        int step;
        if (this.mUseFixedVolume) {
            return;
        }
        ensureValidDirection(direction);
        ensureValidStreamType(streamType);
        int streamTypeAlias = this.mStreamVolumeAlias[streamType];
        VolumeStreamState streamState = this.mStreamStates[streamTypeAlias];
        int device = getDeviceForStream(streamTypeAlias);
        int aliasIndex = streamState.getIndex(device);
        boolean adjustVolume = true;
        if (((device & AudioSystem.DEVICE_OUT_ALL_A2DP) == 0 && (flags & 64) != 0) || this.mAppOps.noteOp(STEAM_VOLUME_OPS[streamTypeAlias], Binder.getCallingUid(), callingPackage) != 0) {
            return;
        }
        synchronized (this.mSafeMediaVolumeState) {
            this.mPendingVolumeCommand = null;
        }
        int flags2 = flags & (-33);
        if (streamTypeAlias == 3 && (device & 31744) != 0) {
            flags2 |= 32;
            if (this.mSafeMediaVolumeState.intValue() == 3 && (device & 12) != 0) {
                step = this.mSafeMediaVolumeIndex;
            } else {
                step = streamState.getMaxIndex();
            }
            if (aliasIndex != 0) {
                aliasIndex = step;
            }
        } else {
            step = rescaleIndex(10, streamType, streamTypeAlias);
        }
        if ((flags2 & 2) != 0 || streamTypeAlias == getMasterStreamType()) {
            int ringerMode = getRingerMode();
            if (ringerMode == 1) {
                flags2 &= -17;
            }
            adjustVolume = checkForRingerModeChange(aliasIndex, direction, step);
        }
        int oldIndex = this.mStreamStates[streamType].getIndex(device);
        if (adjustVolume && direction != 0) {
            if (streamTypeAlias == 3 && (device & AudioSystem.DEVICE_OUT_ALL_A2DP) != 0 && (flags2 & 64) == 0) {
                synchronized (this.mA2dpAvrcpLock) {
                    if (this.mA2dp != null && this.mAvrcpAbsVolSupported) {
                        this.mA2dp.adjustAvrcpAbsoluteVolume(direction);
                    }
                }
            }
            if (direction == 1 && !checkSafeMediaVolume(streamTypeAlias, aliasIndex + step, device)) {
                Log.e(TAG, "adjustStreamVolume() safe volume index = " + oldIndex);
                this.mVolumePanel.postDisplaySafeVolumeWarning(flags2);
            } else if (streamState.adjustIndex(direction * step, device)) {
                sendMsg(this.mAudioHandler, 0, 2, device, 0, streamState, 0);
            }
        }
        int index = this.mStreamStates[streamType].getIndex(device);
        sendVolumeUpdate(streamType, oldIndex, index, flags2);
    }

    @Override // android.media.IAudioService
    public void adjustMasterVolume(int steps, int flags, String callingPackage) {
        if (this.mUseFixedVolume) {
            return;
        }
        ensureValidSteps(steps);
        int volume = Math.round(AudioSystem.getMasterVolume() * 100.0f);
        int numSteps = Math.abs(steps);
        int direction = steps > 0 ? 1 : -1;
        for (int i = 0; i < numSteps; i++) {
            int delta = findVolumeDelta(direction, volume);
            volume += delta;
        }
        setMasterVolume(volume, flags, callingPackage);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: AudioService$StreamVolumeCommand.class */
    public class StreamVolumeCommand {
        public final int mStreamType;
        public final int mIndex;
        public final int mFlags;
        public final int mDevice;

        StreamVolumeCommand(int streamType, int index, int flags, int device) {
            this.mStreamType = streamType;
            this.mIndex = index;
            this.mFlags = flags;
            this.mDevice = device;
        }
    }

    private void onSetStreamVolume(int streamType, int index, int flags, int device) {
        int newRingerMode;
        setStreamVolumeInt(this.mStreamVolumeAlias[streamType], index, device, false);
        if ((flags & 2) != 0 || this.mStreamVolumeAlias[streamType] == getMasterStreamType()) {
            if (index == 0) {
                newRingerMode = this.mHasVibrator ? 1 : 0;
            } else {
                newRingerMode = 2;
            }
            setRingerMode(newRingerMode);
        }
    }

    @Override // android.media.IAudioService
    public void setStreamVolume(int streamType, int index, int flags, String callingPackage) {
        int oldIndex;
        int index2;
        int flags2;
        if (this.mUseFixedVolume) {
            return;
        }
        ensureValidStreamType(streamType);
        int streamTypeAlias = this.mStreamVolumeAlias[streamType];
        VolumeStreamState streamState = this.mStreamStates[streamTypeAlias];
        int device = getDeviceForStream(streamType);
        if (((device & AudioSystem.DEVICE_OUT_ALL_A2DP) == 0 && (flags & 64) != 0) || this.mAppOps.noteOp(STEAM_VOLUME_OPS[streamTypeAlias], Binder.getCallingUid(), callingPackage) != 0) {
            return;
        }
        synchronized (this.mSafeMediaVolumeState) {
            this.mPendingVolumeCommand = null;
            oldIndex = streamState.getIndex(device);
            index2 = rescaleIndex(index * 10, streamType, streamTypeAlias);
            if (streamTypeAlias == 3 && (device & AudioSystem.DEVICE_OUT_ALL_A2DP) != 0 && (flags & 64) == 0) {
                synchronized (this.mA2dpAvrcpLock) {
                    if (this.mA2dp != null && this.mAvrcpAbsVolSupported) {
                        this.mA2dp.setAvrcpAbsoluteVolume(index2);
                    }
                }
            }
            flags2 = flags & (-33);
            if (streamTypeAlias == 3 && (device & 31744) != 0) {
                flags2 |= 32;
                if (index2 != 0) {
                    index2 = (this.mSafeMediaVolumeState.intValue() != 3 || (device & 12) == 0) ? streamState.getMaxIndex() : this.mSafeMediaVolumeIndex;
                }
            }
            if (!checkSafeMediaVolume(streamTypeAlias, index2, device)) {
                this.mVolumePanel.postDisplaySafeVolumeWarning(flags2);
                this.mPendingVolumeCommand = new StreamVolumeCommand(streamType, index2, flags2, device);
            } else {
                onSetStreamVolume(streamType, index2, flags2, device);
                index2 = this.mStreamStates[streamType].getIndex(device);
            }
        }
        sendVolumeUpdate(streamType, oldIndex, index2, flags2);
    }

    @Override // android.media.IAudioService
    public void forceVolumeControlStream(int streamType, IBinder cb) {
        synchronized (this.mForceControlStreamLock) {
            this.mVolumeControlStream = streamType;
            if (this.mVolumeControlStream == -1) {
                if (this.mForceControlStreamClient != null) {
                    this.mForceControlStreamClient.release();
                    this.mForceControlStreamClient = null;
                }
            } else {
                this.mForceControlStreamClient = new ForceControlStreamClient(cb);
            }
        }
    }

    /* loaded from: AudioService$ForceControlStreamClient.class */
    private class ForceControlStreamClient implements IBinder.DeathRecipient {
        private IBinder mCb;

        ForceControlStreamClient(IBinder cb) {
            if (cb != null) {
                try {
                    cb.linkToDeath(this, 0);
                } catch (RemoteException e) {
                    Log.w(AudioService.TAG, "ForceControlStreamClient() could not link to " + cb + " binder death");
                    cb = null;
                }
            }
            this.mCb = cb;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            synchronized (AudioService.this.mForceControlStreamLock) {
                Log.w(AudioService.TAG, "SCO client died");
                if (AudioService.this.mForceControlStreamClient == this) {
                    AudioService.this.mForceControlStreamClient = null;
                    AudioService.this.mVolumeControlStream = -1;
                } else {
                    Log.w(AudioService.TAG, "unregistered control stream client died");
                }
            }
        }

        public void release() {
            if (this.mCb != null) {
                this.mCb.unlinkToDeath(this, 0);
                this.mCb = null;
            }
        }
    }

    private int findVolumeDelta(int direction, int volume) {
        int delta = 0;
        if (direction == 1) {
            if (volume == 100) {
                return 0;
            }
            delta = this.mMasterVolumeRamp[1];
            int i = this.mMasterVolumeRamp.length - 1;
            while (true) {
                if (i <= 1) {
                    break;
                } else if (volume < this.mMasterVolumeRamp[i - 1]) {
                    i -= 2;
                } else {
                    delta = this.mMasterVolumeRamp[i];
                    break;
                }
            }
        } else if (direction == -1) {
            if (volume == 0) {
                return 0;
            }
            int length = this.mMasterVolumeRamp.length;
            delta = -this.mMasterVolumeRamp[length - 1];
            int i2 = 2;
            while (true) {
                if (i2 >= length) {
                    break;
                } else if (volume > this.mMasterVolumeRamp[i2]) {
                    i2 += 2;
                } else {
                    delta = -this.mMasterVolumeRamp[i2 - 1];
                    break;
                }
            }
        }
        return delta;
    }

    private void sendBroadcastToAll(Intent intent) {
        long ident = Binder.clearCallingIdentity();
        try {
            this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
            Binder.restoreCallingIdentity(ident);
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(ident);
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendStickyBroadcastToAll(Intent intent) {
        long ident = Binder.clearCallingIdentity();
        try {
            this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
            Binder.restoreCallingIdentity(ident);
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(ident);
            throw th;
        }
    }

    private void sendVolumeUpdate(int streamType, int oldIndex, int index, int flags) {
        if (!this.mVoiceCapable && streamType == 2) {
            streamType = 5;
        }
        this.mVolumePanel.postVolumeChanged(streamType, flags);
        if ((flags & 32) == 0) {
            Intent intent = new Intent(AudioManager.VOLUME_CHANGED_ACTION);
            intent.putExtra(AudioManager.EXTRA_VOLUME_STREAM_TYPE, streamType);
            intent.putExtra(AudioManager.EXTRA_VOLUME_STREAM_VALUE, (index + 5) / 10);
            intent.putExtra(AudioManager.EXTRA_PREV_VOLUME_STREAM_VALUE, (oldIndex + 5) / 10);
            sendBroadcastToAll(intent);
        }
    }

    private void sendMasterVolumeUpdate(int flags, int oldVolume, int newVolume) {
        this.mVolumePanel.postMasterVolumeChanged(flags);
        Intent intent = new Intent(AudioManager.MASTER_VOLUME_CHANGED_ACTION);
        intent.putExtra(AudioManager.EXTRA_PREV_MASTER_VOLUME_VALUE, oldVolume);
        intent.putExtra(AudioManager.EXTRA_MASTER_VOLUME_VALUE, newVolume);
        sendBroadcastToAll(intent);
    }

    private void sendMasterMuteUpdate(boolean muted, int flags) {
        this.mVolumePanel.postMasterMuteChanged(flags);
        broadcastMasterMuteStatus(muted);
    }

    private void broadcastMasterMuteStatus(boolean muted) {
        Intent intent = new Intent(AudioManager.MASTER_MUTE_CHANGED_ACTION);
        intent.putExtra(AudioManager.EXTRA_MASTER_VOLUME_MUTED, muted);
        intent.addFlags(603979776);
        sendStickyBroadcastToAll(intent);
    }

    private void setStreamVolumeInt(int streamType, int index, int device, boolean force) {
        VolumeStreamState streamState = this.mStreamStates[streamType];
        if (streamState.setIndex(index, device) || force) {
            sendMsg(this.mAudioHandler, 0, 2, device, 0, streamState, 0);
        }
    }

    @Override // android.media.IAudioService
    public void setStreamSolo(int streamType, boolean state, IBinder cb) {
        if (this.mUseFixedVolume) {
            return;
        }
        for (int stream = 0; stream < this.mStreamStates.length; stream++) {
            if (isStreamAffectedByMute(stream) && stream != streamType) {
                this.mStreamStates[stream].mute(cb, state);
            }
        }
    }

    @Override // android.media.IAudioService
    public void setStreamMute(int streamType, boolean state, IBinder cb) {
        if (!this.mUseFixedVolume && isStreamAffectedByMute(streamType)) {
            this.mStreamStates[streamType].mute(cb, state);
        }
    }

    @Override // android.media.IAudioService
    public boolean isStreamMute(int streamType) {
        return this.mStreamStates[streamType].isMuted();
    }

    @Override // android.media.IAudioService
    public void setMasterMute(boolean state, int flags, IBinder cb) {
        if (!this.mUseFixedVolume && state != AudioSystem.getMasterMute()) {
            AudioSystem.setMasterMute(state);
            sendMsg(this.mAudioHandler, 11, 0, state ? 1 : 0, 0, null, 500);
            sendMasterMuteUpdate(state, flags);
        }
    }

    @Override // android.media.IAudioService
    public boolean isMasterMute() {
        return AudioSystem.getMasterMute();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static int getMaxStreamVolume(int streamType) {
        return MAX_STREAM_VOLUME[streamType];
    }

    @Override // android.media.IAudioService
    public int getStreamVolume(int streamType) {
        ensureValidStreamType(streamType);
        int device = getDeviceForStream(streamType);
        int index = this.mStreamStates[streamType].getIndex(device);
        if (this.mStreamStates[streamType].isMuted()) {
            index = 0;
        }
        if (index != 0 && this.mStreamVolumeAlias[streamType] == 3 && (device & 31744) != 0) {
            index = this.mStreamStates[streamType].getMaxIndex();
        }
        return (index + 5) / 10;
    }

    @Override // android.media.IAudioService
    public int getMasterVolume() {
        if (isMasterMute()) {
            return 0;
        }
        return getLastAudibleMasterVolume();
    }

    @Override // android.media.IAudioService
    public void setMasterVolume(int volume, int flags, String callingPackage) {
        if (this.mUseFixedVolume || this.mAppOps.noteOp(33, Binder.getCallingUid(), callingPackage) != 0) {
            return;
        }
        if (volume < 0) {
            volume = 0;
        } else if (volume > 100) {
            volume = 100;
        }
        doSetMasterVolume(volume / 100.0f, flags);
    }

    private void doSetMasterVolume(float volume, int flags) {
        if (!AudioSystem.getMasterMute()) {
            int oldVolume = getMasterVolume();
            AudioSystem.setMasterVolume(volume);
            int newVolume = getMasterVolume();
            if (newVolume != oldVolume) {
                sendMsg(this.mAudioHandler, 2, 0, Math.round(volume * 1000.0f), 0, null, 500);
            }
            sendMasterVolumeUpdate(flags, oldVolume, newVolume);
        }
    }

    @Override // android.media.IAudioService
    public int getStreamMaxVolume(int streamType) {
        ensureValidStreamType(streamType);
        return (this.mStreamStates[streamType].getMaxIndex() + 5) / 10;
    }

    @Override // android.media.IAudioService
    public int getMasterMaxVolume() {
        return 100;
    }

    @Override // android.media.IAudioService
    public int getLastAudibleStreamVolume(int streamType) {
        ensureValidStreamType(streamType);
        int device = getDeviceForStream(streamType);
        return (this.mStreamStates[streamType].getIndex(device) + 5) / 10;
    }

    @Override // android.media.IAudioService
    public int getLastAudibleMasterVolume() {
        return Math.round(AudioSystem.getMasterVolume() * 100.0f);
    }

    @Override // android.media.IAudioService
    public int getMasterStreamType() {
        if (this.mVoiceCapable) {
            return 2;
        }
        return 3;
    }

    @Override // android.media.IAudioService
    public int getRingerMode() {
        int i;
        synchronized (this.mSettingsLock) {
            i = this.mRingerMode;
        }
        return i;
    }

    private void ensureValidRingerMode(int ringerMode) {
        if (!AudioManager.isValidRingerMode(ringerMode)) {
            throw new IllegalArgumentException("Bad ringer mode " + ringerMode);
        }
    }

    @Override // android.media.IAudioService
    public void setRingerMode(int ringerMode) {
        if (this.mUseFixedVolume) {
            return;
        }
        if (ringerMode == 1 && !this.mHasVibrator) {
            ringerMode = 0;
        }
        if (ringerMode != getRingerMode()) {
            setRingerModeInt(ringerMode, true);
            broadcastRingerMode(ringerMode);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setRingerModeInt(int ringerMode, boolean persist) {
        synchronized (this.mSettingsLock) {
            this.mRingerMode = ringerMode;
        }
        int numStreamTypes = AudioSystem.getNumStreamTypes();
        for (int streamType = numStreamTypes - 1; streamType >= 0; streamType--) {
            if (isStreamMutedByRingerMode(streamType)) {
                if (!isStreamAffectedByRingerMode(streamType) || ringerMode == 2) {
                    if (this.mVoiceCapable && this.mStreamVolumeAlias[streamType] == 2) {
                        synchronized (this.mStreamStates[streamType]) {
                            Set<Map.Entry> set = this.mStreamStates[streamType].mIndex.entrySet();
                            for (Map.Entry entry : set) {
                                if (((Integer) entry.getValue()).intValue() == 0) {
                                    entry.setValue(10);
                                }
                            }
                        }
                    }
                    this.mStreamStates[streamType].mute(null, false);
                    this.mRingerModeMutedStreams &= (1 << streamType) ^ (-1);
                }
            } else if (isStreamAffectedByRingerMode(streamType) && ringerMode != 2) {
                this.mStreamStates[streamType].mute(null, true);
                this.mRingerModeMutedStreams |= 1 << streamType;
            }
        }
        if (persist) {
            sendMsg(this.mAudioHandler, 3, 0, 0, 0, null, 500);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void restoreMasterVolume() {
        if (this.mUseFixedVolume) {
            AudioSystem.setMasterVolume(1.0f);
        } else if (this.mUseMasterVolume) {
            float volume = Settings.System.getFloatForUser(this.mContentResolver, Settings.System.VOLUME_MASTER, -1.0f, -2);
            if (volume >= 0.0f) {
                AudioSystem.setMasterVolume(volume);
            }
        }
    }

    @Override // android.media.IAudioService
    public boolean shouldVibrate(int vibrateType) {
        if (this.mHasVibrator) {
            switch (getVibrateSetting(vibrateType)) {
                case 0:
                    return false;
                case 1:
                    return getRingerMode() != 0;
                case 2:
                    return getRingerMode() == 1;
                default:
                    return false;
            }
        }
        return false;
    }

    @Override // android.media.IAudioService
    public int getVibrateSetting(int vibrateType) {
        if (this.mHasVibrator) {
            return (this.mVibrateSetting >> (vibrateType * 2)) & 3;
        }
        return 0;
    }

    @Override // android.media.IAudioService
    public void setVibrateSetting(int vibrateType, int vibrateSetting) {
        if (this.mHasVibrator) {
            this.mVibrateSetting = getValueForVibrateSetting(this.mVibrateSetting, vibrateType, vibrateSetting);
            broadcastVibrateSetting(vibrateType);
        }
    }

    public static int getValueForVibrateSetting(int existingValue, int vibrateType, int vibrateSetting) {
        return (existingValue & ((3 << (vibrateType * 2)) ^ (-1))) | ((vibrateSetting & 3) << (vibrateType * 2));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: AudioService$SetModeDeathHandler.class */
    public class SetModeDeathHandler implements IBinder.DeathRecipient {
        private IBinder mCb;
        private int mPid;
        private int mMode = 0;

        SetModeDeathHandler(IBinder cb, int pid) {
            this.mCb = cb;
            this.mPid = pid;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            int newModeOwnerPid = 0;
            synchronized (AudioService.this.mSetModeDeathHandlers) {
                Log.w(AudioService.TAG, "setMode() client died");
                int index = AudioService.this.mSetModeDeathHandlers.indexOf(this);
                if (index < 0) {
                    Log.w(AudioService.TAG, "unregistered setMode() client died");
                } else {
                    newModeOwnerPid = AudioService.this.setModeInt(0, this.mCb, this.mPid);
                }
            }
            if (newModeOwnerPid != 0) {
                long ident = Binder.clearCallingIdentity();
                AudioService.this.disconnectBluetoothSco(newModeOwnerPid);
                Binder.restoreCallingIdentity(ident);
            }
        }

        public int getPid() {
            return this.mPid;
        }

        public void setMode(int mode) {
            this.mMode = mode;
        }

        public int getMode() {
            return this.mMode;
        }

        public IBinder getBinder() {
            return this.mCb;
        }
    }

    @Override // android.media.IAudioService
    public void setMode(int mode, IBinder cb) {
        int newModeOwnerPid;
        if (!checkAudioSettingsPermission("setMode()") || mode < -1 || mode >= 4) {
            return;
        }
        synchronized (this.mSetModeDeathHandlers) {
            if (mode == -1) {
                mode = this.mMode;
            }
            newModeOwnerPid = setModeInt(mode, cb, Binder.getCallingPid());
        }
        if (newModeOwnerPid != 0) {
            disconnectBluetoothSco(newModeOwnerPid);
        }
    }

    int setModeInt(int mode, IBinder cb, int pid) {
        int status;
        int newModeOwnerPid = 0;
        if (cb == null) {
            Log.e(TAG, "setModeInt() called with null binder");
            return 0;
        }
        SetModeDeathHandler hdlr = null;
        Iterator iter = this.mSetModeDeathHandlers.iterator();
        while (true) {
            if (!iter.hasNext()) {
                break;
            }
            SetModeDeathHandler h = iter.next();
            if (h.getPid() == pid) {
                hdlr = h;
                iter.remove();
                hdlr.getBinder().unlinkToDeath(hdlr, 0);
                break;
            }
        }
        do {
            if (mode == 0) {
                if (!this.mSetModeDeathHandlers.isEmpty()) {
                    hdlr = this.mSetModeDeathHandlers.get(0);
                    cb = hdlr.getBinder();
                    mode = hdlr.getMode();
                }
            } else {
                if (hdlr == null) {
                    hdlr = new SetModeDeathHandler(cb, pid);
                }
                try {
                    cb.linkToDeath(hdlr, 0);
                } catch (RemoteException e) {
                    Log.w(TAG, "setMode() could not link to " + cb + " binder death");
                }
                this.mSetModeDeathHandlers.add(0, hdlr);
                hdlr.setMode(mode);
            }
            if (mode != this.mMode) {
                status = AudioSystem.setPhoneState(mode);
                if (status == 0) {
                    this.mMode = mode;
                } else {
                    if (hdlr != null) {
                        this.mSetModeDeathHandlers.remove(hdlr);
                        cb.unlinkToDeath(hdlr, 0);
                    }
                    mode = 0;
                }
            } else {
                status = 0;
            }
            if (status == 0) {
                break;
            }
        } while (!this.mSetModeDeathHandlers.isEmpty());
        if (status == 0) {
            if (mode != 0) {
                if (this.mSetModeDeathHandlers.isEmpty()) {
                    Log.e(TAG, "setMode() different from MODE_NORMAL with empty mode client stack");
                } else {
                    newModeOwnerPid = this.mSetModeDeathHandlers.get(0).getPid();
                }
            }
            int streamType = getActiveStreamType(Integer.MIN_VALUE);
            if (streamType == -200) {
                streamType = 3;
            }
            int device = getDeviceForStream(streamType);
            int index = this.mStreamStates[this.mStreamVolumeAlias[streamType]].getIndex(device);
            setStreamVolumeInt(this.mStreamVolumeAlias[streamType], index, device, true);
            updateStreamVolumeAlias(true);
        }
        return newModeOwnerPid;
    }

    @Override // android.media.IAudioService
    public int getMode() {
        return this.mMode;
    }

    /* loaded from: AudioService$LoadSoundEffectReply.class */
    class LoadSoundEffectReply {
        public int mStatus = 1;

        LoadSoundEffectReply() {
        }
    }

    private void loadTouchSoundAssetDefaults() {
        SOUND_EFFECT_FILES.add("Effect_Tick.ogg");
        for (int i = 0; i < 10; i++) {
            this.SOUND_EFFECT_FILES_MAP[i][0] = 0;
            this.SOUND_EFFECT_FILES_MAP[i][1] = -1;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadTouchSoundAssets() {
        XmlResourceParser parser = null;
        if (SOUND_EFFECT_FILES.isEmpty()) {
            loadTouchSoundAssetDefaults();
            try {
                try {
                    try {
                        try {
                            parser = this.mContext.getResources().getXml(R.xml.audio_assets);
                            XmlUtils.beginDocument(parser, TAG_AUDIO_ASSETS);
                            String version = parser.getAttributeValue(null, "version");
                            boolean inTouchSoundsGroup = false;
                            if (ASSET_FILE_VERSION.equals(version)) {
                                while (true) {
                                    XmlUtils.nextElement(parser);
                                    String element = parser.getName();
                                    if (element == null) {
                                        break;
                                    } else if (element.equals("group")) {
                                        String name = parser.getAttributeValue(null, "name");
                                        if (GROUP_TOUCH_SOUNDS.equals(name)) {
                                            inTouchSoundsGroup = true;
                                            break;
                                        }
                                    }
                                }
                                while (inTouchSoundsGroup) {
                                    XmlUtils.nextElement(parser);
                                    String element2 = parser.getName();
                                    if (element2 == null || !element2.equals(TAG_ASSET)) {
                                        break;
                                    }
                                    String id = parser.getAttributeValue(null, "id");
                                    String file = parser.getAttributeValue(null, "file");
                                    try {
                                        Field field = AudioManager.class.getField(id);
                                        int fx = field.getInt(null);
                                        int i = SOUND_EFFECT_FILES.indexOf(file);
                                        if (i == -1) {
                                            i = SOUND_EFFECT_FILES.size();
                                            SOUND_EFFECT_FILES.add(file);
                                        }
                                        this.SOUND_EFFECT_FILES_MAP[fx][0] = i;
                                    } catch (Exception e) {
                                        Log.w(TAG, "Invalid touch sound ID: " + id);
                                    }
                                }
                            }
                            if (parser != null) {
                                parser.close();
                            }
                        } catch (IOException e2) {
                            Log.w(TAG, "I/O exception reading touch sound assets", e2);
                            if (parser != null) {
                                parser.close();
                            }
                        }
                    } catch (XmlPullParserException e3) {
                        Log.w(TAG, "XML parser exception reading touch sound assets", e3);
                        if (parser != null) {
                            parser.close();
                        }
                    }
                } catch (Resources.NotFoundException e4) {
                    Log.w(TAG, "audio assets file not found", e4);
                    if (parser != null) {
                        parser.close();
                    }
                }
            } catch (Throwable th) {
                if (parser != null) {
                    parser.close();
                }
                throw th;
            }
        }
    }

    @Override // android.media.IAudioService
    public void playSoundEffect(int effectType) {
        playSoundEffectVolume(effectType, -1.0f);
    }

    @Override // android.media.IAudioService
    public void playSoundEffectVolume(int effectType, float volume) {
        sendMsg(this.mAudioHandler, 5, 2, effectType, (int) (volume * 1000.0f), null, 0);
    }

    @Override // android.media.IAudioService
    public boolean loadSoundEffects() {
        int attempts = 3;
        LoadSoundEffectReply reply = new LoadSoundEffectReply();
        synchronized (reply) {
            sendMsg(this.mAudioHandler, 7, 2, 0, 0, reply, 0);
            while (reply.mStatus == 1) {
                int i = attempts;
                attempts--;
                if (i <= 0) {
                    break;
                }
                try {
                    reply.wait(TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS);
                } catch (InterruptedException e) {
                    Log.w(TAG, "loadSoundEffects Interrupted while waiting sound pool loaded.");
                }
            }
        }
        return reply.mStatus == 0;
    }

    @Override // android.media.IAudioService
    public void unloadSoundEffects() {
        sendMsg(this.mAudioHandler, 20, 2, 0, 0, null, 0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: AudioService$SoundPoolListenerThread.class */
    public class SoundPoolListenerThread extends Thread {
        public SoundPoolListenerThread() {
            super("SoundPoolListenerThread");
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            Looper.prepare();
            AudioService.this.mSoundPoolLooper = Looper.myLooper();
            synchronized (AudioService.this.mSoundEffectsLock) {
                if (AudioService.this.mSoundPool != null) {
                    AudioService.this.mSoundPoolCallBack = new SoundPoolCallback();
                    AudioService.this.mSoundPool.setOnLoadCompleteListener(AudioService.this.mSoundPoolCallBack);
                }
                AudioService.this.mSoundEffectsLock.notify();
            }
            Looper.loop();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: AudioService$SoundPoolCallback.class */
    public final class SoundPoolCallback implements SoundPool.OnLoadCompleteListener {
        int mStatus;
        List<Integer> mSamples;

        private SoundPoolCallback() {
            this.mStatus = 1;
            this.mSamples = new ArrayList();
        }

        public int status() {
            return this.mStatus;
        }

        public void setSamples(int[] samples) {
            for (int i = 0; i < samples.length; i++) {
                if (samples[i] > 0) {
                    this.mSamples.add(Integer.valueOf(samples[i]));
                }
            }
        }

        @Override // android.media.SoundPool.OnLoadCompleteListener
        public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
            synchronized (AudioService.this.mSoundEffectsLock) {
                int i = this.mSamples.indexOf(Integer.valueOf(sampleId));
                if (i >= 0) {
                    this.mSamples.remove(i);
                }
                if (status != 0 || this.mSamples.isEmpty()) {
                    this.mStatus = status;
                    AudioService.this.mSoundEffectsLock.notify();
                }
            }
        }
    }

    @Override // android.media.IAudioService
    public void reloadAudioSettings() {
        readAudioSettings(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void readAudioSettings(boolean userSwitch) {
        readPersistedSettings();
        int numStreamTypes = AudioSystem.getNumStreamTypes();
        for (int streamType = 0; streamType < numStreamTypes; streamType++) {
            VolumeStreamState streamState = this.mStreamStates[streamType];
            if (!userSwitch || this.mStreamVolumeAlias[streamType] != 3) {
                synchronized (streamState) {
                    streamState.readSettings();
                    if (streamState.isMuted() && ((!isStreamAffectedByMute(streamType) && !isStreamMutedByRingerMode(streamType)) || this.mUseFixedVolume)) {
                        int size = streamState.mDeathHandlers.size();
                        for (int i = 0; i < size; i++) {
                            ((VolumeStreamState.VolumeDeathHandler) streamState.mDeathHandlers.get(i)).mMuteCount = 1;
                            ((VolumeStreamState.VolumeDeathHandler) streamState.mDeathHandlers.get(i)).mute(false);
                        }
                    }
                }
            }
        }
        setRingerModeInt(getRingerMode(), false);
        checkAllAliasStreamVolumes();
        synchronized (this.mSafeMediaVolumeState) {
            if (this.mSafeMediaVolumeState.intValue() == 3) {
                enforceSafeMediaVolume();
            }
        }
    }

    @Override // android.media.IAudioService
    public void setSpeakerphoneOn(boolean on) {
        if (!checkAudioSettingsPermission("setSpeakerphoneOn()")) {
            return;
        }
        if (on) {
            if (this.mForcedUseForComm == 3) {
                sendMsg(this.mAudioHandler, 8, 2, 2, 0, null, 0);
            }
            this.mForcedUseForComm = 1;
        } else if (this.mForcedUseForComm == 1) {
            this.mForcedUseForComm = 0;
        }
        sendMsg(this.mAudioHandler, 8, 2, 0, this.mForcedUseForComm, null, 0);
    }

    @Override // android.media.IAudioService
    public boolean isSpeakerphoneOn() {
        return this.mForcedUseForComm == 1;
    }

    @Override // android.media.IAudioService
    public void setBluetoothScoOn(boolean on) {
        if (!checkAudioSettingsPermission("setBluetoothScoOn()")) {
            return;
        }
        if (on) {
            this.mForcedUseForComm = 3;
        } else if (this.mForcedUseForComm == 3) {
            this.mForcedUseForComm = 0;
        }
        sendMsg(this.mAudioHandler, 8, 2, 0, this.mForcedUseForComm, null, 0);
        sendMsg(this.mAudioHandler, 8, 2, 2, this.mForcedUseForComm, null, 0);
    }

    @Override // android.media.IAudioService
    public boolean isBluetoothScoOn() {
        return this.mForcedUseForComm == 3;
    }

    @Override // android.media.IAudioService
    public void setBluetoothA2dpOn(boolean on) {
        synchronized (this.mBluetoothA2dpEnabledLock) {
            this.mBluetoothA2dpEnabled = on;
            sendMsg(this.mAudioHandler, 13, 2, 1, this.mBluetoothA2dpEnabled ? 0 : 10, null, 0);
        }
    }

    @Override // android.media.IAudioService
    public boolean isBluetoothA2dpOn() {
        boolean z;
        synchronized (this.mBluetoothA2dpEnabledLock) {
            z = this.mBluetoothA2dpEnabled;
        }
        return z;
    }

    @Override // android.media.IAudioService
    public void startBluetoothSco(IBinder cb, int targetSdkVersion) {
        if (!checkAudioSettingsPermission("startBluetoothSco()") || !this.mBootCompleted) {
            return;
        }
        ScoClient client = getScoClient(cb, true);
        long ident = Binder.clearCallingIdentity();
        client.incCount(targetSdkVersion);
        Binder.restoreCallingIdentity(ident);
    }

    @Override // android.media.IAudioService
    public void stopBluetoothSco(IBinder cb) {
        if (!checkAudioSettingsPermission("stopBluetoothSco()") || !this.mBootCompleted) {
            return;
        }
        ScoClient client = getScoClient(cb, false);
        long ident = Binder.clearCallingIdentity();
        if (client != null) {
            client.decCount();
        }
        Binder.restoreCallingIdentity(ident);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: AudioService$ScoClient.class */
    public class ScoClient implements IBinder.DeathRecipient {
        private IBinder mCb;
        private int mCreatorPid = Binder.getCallingPid();
        private int mStartcount = 0;

        ScoClient(IBinder cb) {
            this.mCb = cb;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            synchronized (AudioService.this.mScoClients) {
                Log.w(AudioService.TAG, "SCO client died");
                int index = AudioService.this.mScoClients.indexOf(this);
                if (index < 0) {
                    Log.w(AudioService.TAG, "unregistered SCO client died");
                } else {
                    clearCount(true);
                    AudioService.this.mScoClients.remove(this);
                }
            }
        }

        public void incCount(int targetSdkVersion) {
            synchronized (AudioService.this.mScoClients) {
                requestScoState(12, targetSdkVersion);
                if (this.mStartcount == 0) {
                    try {
                        this.mCb.linkToDeath(this, 0);
                    } catch (RemoteException e) {
                        Log.w(AudioService.TAG, "ScoClient  incCount() could not link to " + this.mCb + " binder death");
                    }
                }
                this.mStartcount++;
            }
        }

        public void decCount() {
            synchronized (AudioService.this.mScoClients) {
                if (this.mStartcount == 0) {
                    Log.w(AudioService.TAG, "ScoClient.decCount() already 0");
                } else {
                    this.mStartcount--;
                    if (this.mStartcount == 0) {
                        try {
                            this.mCb.unlinkToDeath(this, 0);
                        } catch (NoSuchElementException e) {
                            Log.w(AudioService.TAG, "decCount() going to 0 but not registered to binder");
                        }
                    }
                    requestScoState(10, 0);
                }
            }
        }

        public void clearCount(boolean stopSco) {
            synchronized (AudioService.this.mScoClients) {
                if (this.mStartcount != 0) {
                    try {
                        this.mCb.unlinkToDeath(this, 0);
                    } catch (NoSuchElementException e) {
                        Log.w(AudioService.TAG, "clearCount() mStartcount: " + this.mStartcount + " != 0 but not registered to binder");
                    }
                }
                this.mStartcount = 0;
                if (stopSco) {
                    requestScoState(10, 0);
                }
            }
        }

        public int getCount() {
            return this.mStartcount;
        }

        public IBinder getBinder() {
            return this.mCb;
        }

        public int getPid() {
            return this.mCreatorPid;
        }

        public int totalCount() {
            int i;
            synchronized (AudioService.this.mScoClients) {
                int count = 0;
                int size = AudioService.this.mScoClients.size();
                for (int i2 = 0; i2 < size; i2++) {
                    count += ((ScoClient) AudioService.this.mScoClients.get(i2)).getCount();
                }
                i = count;
            }
            return i;
        }

        private void requestScoState(int state, int targetSdkVersion) {
            AudioService.this.checkScoAudioState();
            if (totalCount() == 0) {
                if (state == 12) {
                    AudioService.this.broadcastScoConnectionState(2);
                    synchronized (AudioService.this.mSetModeDeathHandlers) {
                        if ((AudioService.this.mSetModeDeathHandlers.isEmpty() || ((SetModeDeathHandler) AudioService.this.mSetModeDeathHandlers.get(0)).getPid() == this.mCreatorPid) && (AudioService.this.mScoAudioState == 0 || AudioService.this.mScoAudioState == 5)) {
                            if (AudioService.this.mScoAudioState != 0) {
                                AudioService.this.mScoAudioState = 3;
                                AudioService.this.broadcastScoConnectionState(1);
                            } else {
                                AudioService.this.mScoAudioMode = targetSdkVersion < 18 ? 0 : 1;
                                if (AudioService.this.mBluetoothHeadset == null || AudioService.this.mBluetoothHeadsetDevice == null) {
                                    if (AudioService.this.getBluetoothHeadset()) {
                                        AudioService.this.mScoAudioState = 1;
                                    }
                                } else {
                                    boolean status = AudioService.this.mScoAudioMode == 1 ? AudioService.this.mBluetoothHeadset.connectAudio() : AudioService.this.mBluetoothHeadset.startScoUsingVirtualVoiceCall(AudioService.this.mBluetoothHeadsetDevice);
                                    if (status) {
                                        AudioService.this.mScoAudioState = 3;
                                    } else {
                                        AudioService.this.broadcastScoConnectionState(0);
                                    }
                                }
                            }
                        } else {
                            AudioService.this.broadcastScoConnectionState(0);
                        }
                    }
                } else if (state == 10) {
                    if (AudioService.this.mScoAudioState == 3 || AudioService.this.mScoAudioState == 1) {
                        if (AudioService.this.mScoAudioState == 3) {
                            if (AudioService.this.mBluetoothHeadset == null || AudioService.this.mBluetoothHeadsetDevice == null) {
                                if (AudioService.this.getBluetoothHeadset()) {
                                    AudioService.this.mScoAudioState = 5;
                                    return;
                                }
                                return;
                            }
                            boolean status2 = AudioService.this.mScoAudioMode == 1 ? AudioService.this.mBluetoothHeadset.disconnectAudio() : AudioService.this.mBluetoothHeadset.stopScoUsingVirtualVoiceCall(AudioService.this.mBluetoothHeadsetDevice);
                            if (!status2) {
                                AudioService.this.mScoAudioState = 0;
                                AudioService.this.broadcastScoConnectionState(0);
                                return;
                            }
                            return;
                        }
                        AudioService.this.mScoAudioState = 0;
                        AudioService.this.broadcastScoConnectionState(0);
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkScoAudioState() {
        if (this.mBluetoothHeadset != null && this.mBluetoothHeadsetDevice != null && this.mScoAudioState == 0 && this.mBluetoothHeadset.getAudioState(this.mBluetoothHeadsetDevice) != 10) {
            this.mScoAudioState = 2;
        }
    }

    private ScoClient getScoClient(IBinder cb, boolean create) {
        synchronized (this.mScoClients) {
            ScoClient client = null;
            int size = this.mScoClients.size();
            for (int i = 0; i < size; i++) {
                client = this.mScoClients.get(i);
                if (client.getBinder() == cb) {
                    return client;
                }
            }
            if (create) {
                client = new ScoClient(cb);
                this.mScoClients.add(client);
            }
            return client;
        }
    }

    public void clearAllScoClients(int exceptPid, boolean stopSco) {
        synchronized (this.mScoClients) {
            ScoClient savedClient = null;
            int size = this.mScoClients.size();
            for (int i = 0; i < size; i++) {
                ScoClient cl = this.mScoClients.get(i);
                if (cl.getPid() != exceptPid) {
                    cl.clearCount(stopSco);
                } else {
                    savedClient = cl;
                }
            }
            this.mScoClients.clear();
            if (savedClient != null) {
                this.mScoClients.add(savedClient);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean getBluetoothHeadset() {
        boolean result = false;
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter != null) {
            result = adapter.getProfileProxy(this.mContext, this.mBluetoothProfileServiceListener, 1);
        }
        sendMsg(this.mAudioHandler, 9, 0, 0, 0, null, result ? 3000 : 0);
        return result;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void disconnectBluetoothSco(int exceptPid) {
        synchronized (this.mScoClients) {
            checkScoAudioState();
            if (this.mScoAudioState == 2 || this.mScoAudioState == 4) {
                if (this.mBluetoothHeadsetDevice != null) {
                    if (this.mBluetoothHeadset != null) {
                        if (!this.mBluetoothHeadset.stopVoiceRecognition(this.mBluetoothHeadsetDevice)) {
                            sendMsg(this.mAudioHandler, 9, 0, 0, 0, null, 0);
                        }
                    } else if (this.mScoAudioState == 2 && getBluetoothHeadset()) {
                        this.mScoAudioState = 4;
                    }
                }
            } else {
                clearAllScoClients(exceptPid, true);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetBluetoothSco() {
        synchronized (this.mScoClients) {
            clearAllScoClients(0, false);
            this.mScoAudioState = 0;
            broadcastScoConnectionState(0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void broadcastScoConnectionState(int state) {
        sendMsg(this.mAudioHandler, 19, 2, state, 0, null, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onBroadcastScoConnectionState(int state) {
        if (state != this.mScoConnectionState) {
            Intent newIntent = new Intent(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED);
            newIntent.putExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, state);
            newIntent.putExtra(AudioManager.EXTRA_SCO_AUDIO_PREVIOUS_STATE, this.mScoConnectionState);
            sendStickyBroadcastToAll(newIntent);
            this.mScoConnectionState = state;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onCheckMusicActive() {
        synchronized (this.mSafeMediaVolumeState) {
            if (this.mSafeMediaVolumeState.intValue() == 2) {
                int device = getDeviceForStream(3);
                if ((device & 12) != 0) {
                    sendMsg(this.mAudioHandler, 14, 0, 0, 0, null, 60000);
                    int index = this.mStreamStates[3].getIndex(device);
                    if (AudioSystem.isStreamActive(3, 0) && index > this.mSafeMediaVolumeIndex) {
                        this.mMusicActiveMs += 60000;
                        if (this.mMusicActiveMs > UNSAFE_VOLUME_MUSIC_ACTIVE_MS_MAX) {
                            setSafeMediaVolumeEnabled(true);
                            this.mMusicActiveMs = 0;
                        }
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onConfigureSafeVolume(boolean force) {
        int persistedState;
        synchronized (this.mSafeMediaVolumeState) {
            int mcc = this.mContext.getResources().getConfiguration().mcc;
            if (this.mMcc != mcc || (this.mMcc == 0 && force)) {
                this.mSafeMediaVolumeIndex = this.mContext.getResources().getInteger(R.integer.config_safe_media_volume_index) * 10;
                boolean safeMediaVolumeEnabled = this.mContext.getResources().getBoolean(R.bool.config_safe_media_volume_enabled);
                if (safeMediaVolumeEnabled) {
                    persistedState = 3;
                    if (this.mSafeMediaVolumeState.intValue() != 2) {
                        this.mSafeMediaVolumeState = 3;
                        enforceSafeMediaVolume();
                    }
                } else {
                    persistedState = 1;
                    this.mSafeMediaVolumeState = 1;
                }
                this.mMcc = mcc;
                sendMsg(this.mAudioHandler, 18, 2, persistedState, 0, null, 0);
            }
        }
    }

    private boolean checkForRingerModeChange(int oldIndex, int direction, int step) {
        boolean adjustVolumeIndex = true;
        int ringerMode = getRingerMode();
        switch (ringerMode) {
            case 0:
                if (direction == 1) {
                    if (this.mHasVibrator) {
                        ringerMode = 1;
                    } else {
                        ringerMode = 2;
                    }
                }
                adjustVolumeIndex = false;
                break;
            case 1:
                if (!this.mHasVibrator) {
                    Log.e(TAG, "checkForRingerModeChange() current ringer mode is vibratebut no vibrator is present");
                    break;
                } else {
                    if (direction == -1) {
                        if (this.mPrevVolDirection != -1) {
                            ringerMode = 0;
                        }
                    } else if (direction == 1) {
                        ringerMode = 2;
                    }
                    adjustVolumeIndex = false;
                    break;
                }
            case 2:
                if (direction == -1) {
                    if (this.mHasVibrator) {
                        if (step <= oldIndex && oldIndex < 2 * step) {
                            ringerMode = 1;
                            break;
                        }
                    } else if (oldIndex < step && this.mPrevVolDirection != -1) {
                        ringerMode = 0;
                        break;
                    }
                }
                break;
            default:
                Log.e(TAG, "checkForRingerModeChange() wrong ringer mode: " + ringerMode);
                break;
        }
        setRingerMode(ringerMode);
        this.mPrevVolDirection = direction;
        return adjustVolumeIndex;
    }

    public boolean isStreamAffectedByRingerMode(int streamType) {
        return (this.mRingerModeAffectedStreams & (1 << streamType)) != 0;
    }

    private boolean isStreamMutedByRingerMode(int streamType) {
        return (this.mRingerModeMutedStreams & (1 << streamType)) != 0;
    }

    boolean updateRingerModeAffectedStreams() {
        int ringerModeAffectedStreams;
        int ringerModeAffectedStreams2;
        int ringerModeAffectedStreams3;
        int ringerModeAffectedStreams4 = Settings.System.getIntForUser(this.mContentResolver, Settings.System.MODE_RINGER_STREAMS_AFFECTED, 166, -2) | 38;
        if (this.mVoiceCapable) {
            ringerModeAffectedStreams = ringerModeAffectedStreams4 & (-9);
        } else {
            ringerModeAffectedStreams = ringerModeAffectedStreams4 | 8;
        }
        synchronized (this.mCameraSoundForced) {
            if (this.mCameraSoundForced.booleanValue()) {
                ringerModeAffectedStreams2 = ringerModeAffectedStreams & (-129);
            } else {
                ringerModeAffectedStreams2 = ringerModeAffectedStreams | 128;
            }
        }
        if (this.mStreamVolumeAlias[8] == 2) {
            ringerModeAffectedStreams3 = ringerModeAffectedStreams2 | 256;
        } else {
            ringerModeAffectedStreams3 = ringerModeAffectedStreams2 & (-257);
        }
        if (ringerModeAffectedStreams3 != this.mRingerModeAffectedStreams) {
            Settings.System.putIntForUser(this.mContentResolver, Settings.System.MODE_RINGER_STREAMS_AFFECTED, ringerModeAffectedStreams3, -2);
            this.mRingerModeAffectedStreams = ringerModeAffectedStreams3;
            return true;
        }
        return false;
    }

    public boolean isStreamAffectedByMute(int streamType) {
        return (this.mMuteAffectedStreams & (1 << streamType)) != 0;
    }

    private void ensureValidDirection(int direction) {
        if (direction < -1 || direction > 1) {
            throw new IllegalArgumentException("Bad direction " + direction);
        }
    }

    private void ensureValidSteps(int steps) {
        if (Math.abs(steps) > 4) {
            throw new IllegalArgumentException("Bad volume adjust steps " + steps);
        }
    }

    private void ensureValidStreamType(int streamType) {
        if (streamType < 0 || streamType >= this.mStreamStates.length) {
            throw new IllegalArgumentException("Bad stream type " + streamType);
        }
    }

    private boolean isInCommunication() {
        boolean isOffhook = false;
        if (this.mVoiceCapable) {
            try {
                ITelephony phone = ITelephony.Stub.asInterface(ServiceManager.checkService("phone"));
                if (phone != null) {
                    isOffhook = phone.isOffhook();
                }
            } catch (RemoteException e) {
                Log.w(TAG, "Couldn't connect to phone service", e);
            }
        }
        return isOffhook || getMode() == 3;
    }

    private boolean isAfMusicActiveRecently(int delay_ms) {
        return AudioSystem.isStreamActive(3, delay_ms) || AudioSystem.isStreamActiveRemotely(3, delay_ms);
    }

    private int getActiveStreamType(int suggestedStreamType) {
        if (this.mVoiceCapable) {
            if (isInCommunication()) {
                if (AudioSystem.getForceUse(0) == 3) {
                    return 6;
                }
                return 0;
            } else if (suggestedStreamType == Integer.MIN_VALUE) {
                if (isAfMusicActiveRecently(BluetoothInputDevice.INPUT_DISCONNECT_FAILED_NOT_CONNECTED)) {
                    return 3;
                }
                if (this.mMediaFocusControl.checkUpdateRemoteStateIfActive(3)) {
                    return STREAM_REMOTE_MUSIC;
                }
                return 2;
            } else if (isAfMusicActiveRecently(0)) {
                return 3;
            } else {
                return suggestedStreamType;
            }
        } else if (isInCommunication()) {
            if (AudioSystem.getForceUse(0) == 3) {
                return 6;
            }
            return 0;
        } else if (AudioSystem.isStreamActive(5, BluetoothInputDevice.INPUT_DISCONNECT_FAILED_NOT_CONNECTED) || AudioSystem.isStreamActive(2, BluetoothInputDevice.INPUT_DISCONNECT_FAILED_NOT_CONNECTED)) {
            return 5;
        } else {
            if (suggestedStreamType == Integer.MIN_VALUE) {
                if (!isAfMusicActiveRecently(BluetoothInputDevice.INPUT_DISCONNECT_FAILED_NOT_CONNECTED) && this.mMediaFocusControl.checkUpdateRemoteStateIfActive(3)) {
                    return STREAM_REMOTE_MUSIC;
                }
                return 3;
            }
            return suggestedStreamType;
        }
    }

    private void broadcastRingerMode(int ringerMode) {
        Intent broadcast = new Intent(AudioManager.RINGER_MODE_CHANGED_ACTION);
        broadcast.putExtra(AudioManager.EXTRA_RINGER_MODE, ringerMode);
        broadcast.addFlags(603979776);
        sendStickyBroadcastToAll(broadcast);
    }

    private void broadcastVibrateSetting(int vibrateType) {
        if (ActivityManagerNative.isSystemReady()) {
            Intent broadcast = new Intent(AudioManager.VIBRATE_SETTING_CHANGED_ACTION);
            broadcast.putExtra(AudioManager.EXTRA_VIBRATE_TYPE, vibrateType);
            broadcast.putExtra(AudioManager.EXTRA_VIBRATE_SETTING, getVibrateSetting(vibrateType));
            sendBroadcastToAll(broadcast);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void queueMsgUnderWakeLock(Handler handler, int msg, int arg1, int arg2, Object obj, int delay) {
        long ident = Binder.clearCallingIdentity();
        this.mAudioEventWakeLock.acquire();
        Binder.restoreCallingIdentity(ident);
        sendMsg(handler, msg, 2, arg1, arg2, obj, delay);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void sendMsg(Handler handler, int msg, int existingMsgPolicy, int arg1, int arg2, Object obj, int delay) {
        if (existingMsgPolicy == 0) {
            handler.removeMessages(msg);
        } else if (existingMsgPolicy == 1 && handler.hasMessages(msg)) {
            return;
        }
        handler.sendMessageDelayed(handler.obtainMessage(msg, arg1, arg2, obj), delay);
    }

    boolean checkAudioSettingsPermission(String method) {
        if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.MODIFY_AUDIO_SETTINGS) == 0) {
            return true;
        }
        String msg = "Audio Settings Permission Denial: " + method + " from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid();
        Log.w(TAG, msg);
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getDeviceForStream(int stream) {
        int device = AudioSystem.getDevicesForStream(stream);
        if ((device & (device - 1)) != 0) {
            if ((device & 2) != 0) {
                device = 2;
            } else {
                device &= AudioSystem.DEVICE_OUT_ALL_A2DP;
            }
        }
        return device;
    }

    @Override // android.media.IAudioService
    public void setWiredDeviceConnectionState(int device, int state, String name) {
        synchronized (this.mConnectedDevices) {
            int delay = checkSendBecomingNoisyIntent(device, state);
            queueMsgUnderWakeLock(this.mAudioHandler, 100, device, state, name, delay);
        }
    }

    @Override // android.media.IAudioService
    public int setBluetoothA2dpDeviceConnectionState(BluetoothDevice device, int state) {
        int delay;
        synchronized (this.mConnectedDevices) {
            delay = checkSendBecomingNoisyIntent(128, state == 2 ? 1 : 0);
            queueMsgUnderWakeLock(this.mAudioHandler, 101, state, 0, device, delay);
        }
        return delay;
    }

    /* loaded from: AudioService$VolumeStreamState.class */
    public class VolumeStreamState {
        private final int mStreamType;
        private String mVolumeIndexSettingName;
        private int mIndexMax;
        private final ConcurrentHashMap<Integer, Integer> mIndex;
        private ArrayList<VolumeDeathHandler> mDeathHandlers;

        private VolumeStreamState(String settingName, int streamType) {
            this.mIndex = new ConcurrentHashMap<>(8, 0.75f, 4);
            this.mVolumeIndexSettingName = settingName;
            this.mStreamType = streamType;
            this.mIndexMax = AudioService.MAX_STREAM_VOLUME[streamType];
            AudioSystem.initStreamVolume(streamType, 0, this.mIndexMax);
            this.mIndexMax *= 10;
            this.mDeathHandlers = new ArrayList<>();
            readSettings();
        }

        public String getSettingNameForDevice(int device) {
            String name = this.mVolumeIndexSettingName;
            String suffix = AudioSystem.getDeviceName(device);
            if (suffix.isEmpty()) {
                return name;
            }
            return name + "_" + suffix;
        }

        public synchronized void readSettings() {
            if (AudioService.this.mUseFixedVolume) {
                this.mIndex.put(1073741824, Integer.valueOf(this.mIndexMax));
            } else if (this.mStreamType == 1 || this.mStreamType == 7) {
                int index = 10 * AudioManager.DEFAULT_STREAM_VOLUME[this.mStreamType];
                synchronized (AudioService.this.mCameraSoundForced) {
                    if (AudioService.this.mCameraSoundForced.booleanValue()) {
                        index = this.mIndexMax;
                    }
                }
                this.mIndex.put(1073741824, Integer.valueOf(index));
            } else {
                int remainingDevices = 1073807359;
                int i = 0;
                while (remainingDevices != 0) {
                    int device = 1 << i;
                    if ((device & remainingDevices) != 0) {
                        remainingDevices &= device ^ (-1);
                        String name = getSettingNameForDevice(device);
                        int defaultIndex = device == 1073741824 ? AudioManager.DEFAULT_STREAM_VOLUME[this.mStreamType] : -1;
                        int index2 = Settings.System.getIntForUser(AudioService.this.mContentResolver, name, defaultIndex, -2);
                        if (index2 != -1) {
                            if (AudioService.this.mStreamVolumeAlias[this.mStreamType] == 3 && (device & 31744) != 0) {
                                this.mIndex.put(Integer.valueOf(device), Integer.valueOf(index2 != 0 ? this.mIndexMax : 0));
                            } else {
                                this.mIndex.put(Integer.valueOf(device), Integer.valueOf(getValidIndex(10 * index2)));
                            }
                        }
                    }
                    i++;
                }
            }
        }

        public void applyDeviceVolume(int device) {
            int index;
            if (!isMuted()) {
                if (AudioService.this.mStreamVolumeAlias[this.mStreamType] == 3 && (device & AudioSystem.DEVICE_OUT_ALL_A2DP) != 0 && AudioService.this.mAvrcpAbsVolSupported) {
                    index = (this.mIndexMax + 5) / 10;
                } else {
                    index = (getIndex(device) + 5) / 10;
                }
            } else {
                index = 0;
            }
            AudioSystem.setStreamVolumeIndex(this.mStreamType, index, device);
        }

        public synchronized void applyAllVolumes() {
            int index;
            int intValue;
            if (isMuted()) {
                index = 0;
            } else {
                index = (getIndex(1073741824) + 5) / 10;
            }
            AudioSystem.setStreamVolumeIndex(this.mStreamType, index, 1073741824);
            Set set = this.mIndex.entrySet();
            for (Map.Entry<Integer, Integer> entry : set) {
                int device = entry.getKey().intValue();
                if (device != 1073741824) {
                    if (isMuted()) {
                        intValue = 0;
                    } else {
                        intValue = (entry.getValue().intValue() + 5) / 10;
                    }
                    int index2 = intValue;
                    AudioSystem.setStreamVolumeIndex(this.mStreamType, index2, device);
                }
            }
        }

        public boolean adjustIndex(int deltaIndex, int device) {
            return setIndex(getIndex(device) + deltaIndex, device);
        }

        public synchronized boolean setIndex(int index, int device) {
            int oldIndex = getIndex(device);
            int index2 = getValidIndex(index);
            synchronized (AudioService.this.mCameraSoundForced) {
                if (this.mStreamType == 7 && AudioService.this.mCameraSoundForced.booleanValue()) {
                    index2 = this.mIndexMax;
                }
            }
            this.mIndex.put(Integer.valueOf(device), Integer.valueOf(index2));
            if (oldIndex != index2) {
                boolean currentDevice = device == AudioService.this.getDeviceForStream(this.mStreamType);
                int numStreamTypes = AudioSystem.getNumStreamTypes();
                for (int streamType = numStreamTypes - 1; streamType >= 0; streamType--) {
                    if (streamType != this.mStreamType && AudioService.this.mStreamVolumeAlias[streamType] == this.mStreamType) {
                        int scaledIndex = AudioService.this.rescaleIndex(index2, this.mStreamType, streamType);
                        AudioService.this.mStreamStates[streamType].setIndex(scaledIndex, device);
                        if (currentDevice) {
                            AudioService.this.mStreamStates[streamType].setIndex(scaledIndex, AudioService.this.getDeviceForStream(streamType));
                        }
                    }
                }
                return true;
            }
            return false;
        }

        public synchronized int getIndex(int device) {
            Integer index = this.mIndex.get(Integer.valueOf(device));
            if (index == null) {
                index = this.mIndex.get(1073741824);
            }
            return index.intValue();
        }

        public int getMaxIndex() {
            return this.mIndexMax;
        }

        public synchronized void setAllIndexes(VolumeStreamState srcStream) {
            int srcStreamType = srcStream.getStreamType();
            int index = srcStream.getIndex(1073741824);
            int index2 = AudioService.this.rescaleIndex(index, srcStreamType, this.mStreamType);
            Set<Map.Entry<Integer, Integer>> set = this.mIndex.entrySet();
            for (Map.Entry<Integer, Integer> entry : set) {
                entry.setValue(Integer.valueOf(index2));
            }
            Set set2 = srcStream.mIndex.entrySet();
            for (Map.Entry<Integer, Integer> entry2 : set2) {
                int device = entry2.getKey().intValue();
                int index3 = entry2.getValue().intValue();
                setIndex(AudioService.this.rescaleIndex(index3, srcStreamType, this.mStreamType), device);
            }
        }

        public synchronized void setAllIndexesToMax() {
            Set set = this.mIndex.entrySet();
            for (Map.Entry<Integer, Integer> entry : set) {
                entry.setValue(Integer.valueOf(this.mIndexMax));
            }
        }

        public synchronized void mute(IBinder cb, boolean state) {
            VolumeDeathHandler handler = getDeathHandler(cb, state);
            if (handler == null) {
                Log.e(AudioService.TAG, "Could not get client death handler for stream: " + this.mStreamType);
            } else {
                handler.mute(state);
            }
        }

        public int getStreamType() {
            return this.mStreamType;
        }

        private int getValidIndex(int index) {
            if (index >= 0) {
                if (AudioService.this.mUseFixedVolume || index > this.mIndexMax) {
                    return this.mIndexMax;
                }
                return index;
            }
            return 0;
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: AudioService$VolumeStreamState$VolumeDeathHandler.class */
        public class VolumeDeathHandler implements IBinder.DeathRecipient {
            private IBinder mICallback;
            private int mMuteCount;

            VolumeDeathHandler(IBinder cb) {
                this.mICallback = cb;
            }

            public void mute(boolean state) {
                boolean updateVolume = false;
                if (state) {
                    if (this.mMuteCount == 0) {
                        try {
                            if (this.mICallback != null) {
                                this.mICallback.linkToDeath(this, 0);
                            }
                            VolumeStreamState.this.mDeathHandlers.add(this);
                            if (!VolumeStreamState.this.isMuted()) {
                                updateVolume = true;
                            }
                        } catch (RemoteException e) {
                            binderDied();
                            return;
                        }
                    } else {
                        Log.w(AudioService.TAG, "stream: " + VolumeStreamState.this.mStreamType + " was already muted by this client");
                    }
                    this.mMuteCount++;
                } else if (this.mMuteCount == 0) {
                    Log.e(AudioService.TAG, "unexpected unmute for stream: " + VolumeStreamState.this.mStreamType);
                } else {
                    this.mMuteCount--;
                    if (this.mMuteCount == 0) {
                        VolumeStreamState.this.mDeathHandlers.remove(this);
                        if (this.mICallback != null) {
                            this.mICallback.unlinkToDeath(this, 0);
                        }
                        if (!VolumeStreamState.this.isMuted()) {
                            updateVolume = true;
                        }
                    }
                }
                if (updateVolume) {
                    AudioService.sendMsg(AudioService.this.mAudioHandler, 10, 2, 0, 0, VolumeStreamState.this, 0);
                }
            }

            @Override // android.os.IBinder.DeathRecipient
            public void binderDied() {
                Log.w(AudioService.TAG, "Volume service client died for stream: " + VolumeStreamState.this.mStreamType);
                if (this.mMuteCount != 0) {
                    this.mMuteCount = 1;
                    mute(false);
                }
            }
        }

        private synchronized int muteCount() {
            int count = 0;
            int size = this.mDeathHandlers.size();
            for (int i = 0; i < size; i++) {
                count += this.mDeathHandlers.get(i).mMuteCount;
            }
            return count;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public synchronized boolean isMuted() {
            return muteCount() != 0;
        }

        private VolumeDeathHandler getDeathHandler(IBinder cb, boolean state) {
            VolumeDeathHandler handler;
            int size = this.mDeathHandlers.size();
            for (int i = 0; i < size; i++) {
                VolumeDeathHandler handler2 = this.mDeathHandlers.get(i);
                if (cb == handler2.mICallback) {
                    return handler2;
                }
            }
            if (state) {
                handler = new VolumeDeathHandler(cb);
            } else {
                Log.w(AudioService.TAG, "stream was not muted by this client");
                handler = null;
            }
            return handler;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void dump(PrintWriter pw) {
            pw.print("   Mute count: ");
            pw.println(muteCount());
            pw.print("   Current: ");
            Set set = this.mIndex.entrySet();
            for (Map.Entry<Integer, Integer> entry : set) {
                pw.print(Integer.toHexString(entry.getKey().intValue()) + ": " + ((entry.getValue().intValue() + 5) / 10) + ", ");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: AudioService$AudioSystemThread.class */
    public class AudioSystemThread extends Thread {
        AudioSystemThread() {
            super(AudioService.TAG);
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            Looper.prepare();
            synchronized (AudioService.this) {
                AudioService.this.mAudioHandler = new AudioHandler();
                AudioService.this.notify();
            }
            Looper.loop();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: AudioService$AudioHandler.class */
    public class AudioHandler extends Handler {
        private AudioHandler() {
        }

        private void setDeviceVolume(VolumeStreamState streamState, int device) {
            streamState.applyDeviceVolume(device);
            int numStreamTypes = AudioSystem.getNumStreamTypes();
            for (int streamType = numStreamTypes - 1; streamType >= 0; streamType--) {
                if (streamType != streamState.mStreamType && AudioService.this.mStreamVolumeAlias[streamType] == streamState.mStreamType) {
                    AudioService.this.mStreamStates[streamType].applyDeviceVolume(AudioService.this.getDeviceForStream(streamType));
                }
            }
            AudioService.sendMsg(AudioService.this.mAudioHandler, 1, 2, device, 0, streamState, 500);
        }

        private void setAllVolumes(VolumeStreamState streamState) {
            streamState.applyAllVolumes();
            int numStreamTypes = AudioSystem.getNumStreamTypes();
            for (int streamType = numStreamTypes - 1; streamType >= 0; streamType--) {
                if (streamType != streamState.mStreamType && AudioService.this.mStreamVolumeAlias[streamType] == streamState.mStreamType) {
                    AudioService.this.mStreamStates[streamType].applyAllVolumes();
                }
            }
        }

        private void persistVolume(VolumeStreamState streamState, int device) {
            if (!AudioService.this.mUseFixedVolume) {
                Settings.System.putIntForUser(AudioService.this.mContentResolver, streamState.getSettingNameForDevice(device), (streamState.getIndex(device) + 5) / 10, -2);
            }
        }

        private void persistRingerMode(int ringerMode) {
            if (!AudioService.this.mUseFixedVolume) {
                Settings.Global.putInt(AudioService.this.mContentResolver, "mode_ringer", ringerMode);
            }
        }

        private boolean onLoadSoundEffects() {
            int status;
            synchronized (AudioService.this.mSoundEffectsLock) {
                if (AudioService.this.mBootCompleted) {
                    if (AudioService.this.mSoundPool == null) {
                        AudioService.this.loadTouchSoundAssets();
                        AudioService.this.mSoundPool = new SoundPool(4, 1, 0);
                        AudioService.this.mSoundPoolCallBack = null;
                        AudioService.this.mSoundPoolListenerThread = new SoundPoolListenerThread();
                        AudioService.this.mSoundPoolListenerThread.start();
                        int attempts = 3;
                        while (AudioService.this.mSoundPoolCallBack == null) {
                            int i = attempts;
                            attempts--;
                            if (i <= 0) {
                                break;
                            }
                            try {
                                AudioService.this.mSoundEffectsLock.wait(TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS);
                            } catch (InterruptedException e) {
                                Log.w(AudioService.TAG, "Interrupted while waiting sound pool listener thread.");
                            }
                        }
                        if (AudioService.this.mSoundPoolCallBack != null) {
                            int[] poolId = new int[AudioService.SOUND_EFFECT_FILES.size()];
                            for (int fileIdx = 0; fileIdx < AudioService.SOUND_EFFECT_FILES.size(); fileIdx++) {
                                poolId[fileIdx] = -1;
                            }
                            int numSamples = 0;
                            for (int effect = 0; effect < 10; effect++) {
                                if (AudioService.this.SOUND_EFFECT_FILES_MAP[effect][1] != 0) {
                                    if (poolId[AudioService.this.SOUND_EFFECT_FILES_MAP[effect][0]] == -1) {
                                        String filePath = Environment.getRootDirectory() + AudioService.SOUND_EFFECTS_PATH + ((String) AudioService.SOUND_EFFECT_FILES.get(AudioService.this.SOUND_EFFECT_FILES_MAP[effect][0]));
                                        int sampleId = AudioService.this.mSoundPool.load(filePath, 0);
                                        if (sampleId > 0) {
                                            AudioService.this.SOUND_EFFECT_FILES_MAP[effect][1] = sampleId;
                                            poolId[AudioService.this.SOUND_EFFECT_FILES_MAP[effect][0]] = sampleId;
                                            numSamples++;
                                        } else {
                                            Log.w(AudioService.TAG, "Soundpool could not load file: " + filePath);
                                        }
                                    } else {
                                        AudioService.this.SOUND_EFFECT_FILES_MAP[effect][1] = poolId[AudioService.this.SOUND_EFFECT_FILES_MAP[effect][0]];
                                    }
                                }
                            }
                            if (numSamples > 0) {
                                AudioService.this.mSoundPoolCallBack.setSamples(poolId);
                                int attempts2 = 3;
                                status = 1;
                                while (status == 1) {
                                    int i2 = attempts2;
                                    attempts2--;
                                    if (i2 <= 0) {
                                        break;
                                    }
                                    try {
                                        AudioService.this.mSoundEffectsLock.wait(TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS);
                                        status = AudioService.this.mSoundPoolCallBack.status();
                                    } catch (InterruptedException e2) {
                                        Log.w(AudioService.TAG, "Interrupted while waiting sound pool callback.");
                                    }
                                }
                            } else {
                                status = -1;
                            }
                            if (AudioService.this.mSoundPoolLooper != null) {
                                AudioService.this.mSoundPoolLooper.quit();
                                AudioService.this.mSoundPoolLooper = null;
                            }
                            AudioService.this.mSoundPoolListenerThread = null;
                            if (status != 0) {
                                Log.w(AudioService.TAG, "onLoadSoundEffects(), Error " + status + " while loading samples");
                                for (int effect2 = 0; effect2 < 10; effect2++) {
                                    if (AudioService.this.SOUND_EFFECT_FILES_MAP[effect2][1] > 0) {
                                        AudioService.this.SOUND_EFFECT_FILES_MAP[effect2][1] = -1;
                                    }
                                }
                                AudioService.this.mSoundPool.release();
                                AudioService.this.mSoundPool = null;
                            }
                            return status == 0;
                        }
                        Log.w(AudioService.TAG, "onLoadSoundEffects() SoundPool listener or thread creation error");
                        if (AudioService.this.mSoundPoolLooper != null) {
                            AudioService.this.mSoundPoolLooper.quit();
                            AudioService.this.mSoundPoolLooper = null;
                        }
                        AudioService.this.mSoundPoolListenerThread = null;
                        AudioService.this.mSoundPool.release();
                        AudioService.this.mSoundPool = null;
                        return false;
                    }
                    return true;
                }
                Log.w(AudioService.TAG, "onLoadSoundEffects() called before boot complete");
                return false;
            }
        }

        private void onUnloadSoundEffects() {
            synchronized (AudioService.this.mSoundEffectsLock) {
                if (AudioService.this.mSoundPool == null) {
                    return;
                }
                int[] poolId = new int[AudioService.SOUND_EFFECT_FILES.size()];
                for (int fileIdx = 0; fileIdx < AudioService.SOUND_EFFECT_FILES.size(); fileIdx++) {
                    poolId[fileIdx] = 0;
                }
                for (int effect = 0; effect < 10; effect++) {
                    if (AudioService.this.SOUND_EFFECT_FILES_MAP[effect][1] > 0 && poolId[AudioService.this.SOUND_EFFECT_FILES_MAP[effect][0]] == 0) {
                        AudioService.this.mSoundPool.unload(AudioService.this.SOUND_EFFECT_FILES_MAP[effect][1]);
                        AudioService.this.SOUND_EFFECT_FILES_MAP[effect][1] = -1;
                        poolId[AudioService.this.SOUND_EFFECT_FILES_MAP[effect][0]] = -1;
                    }
                }
                AudioService.this.mSoundPool.release();
                AudioService.this.mSoundPool = null;
            }
        }

        private void onPlaySoundEffect(int effectType, int volume) {
            float volFloat;
            synchronized (AudioService.this.mSoundEffectsLock) {
                onLoadSoundEffects();
                if (AudioService.this.mSoundPool == null) {
                    return;
                }
                if (volume < 0) {
                    volFloat = (float) Math.pow(10.0d, AudioService.sSoundEffectVolumeDb / 20.0f);
                } else {
                    volFloat = volume / 1000.0f;
                }
                if (AudioService.this.SOUND_EFFECT_FILES_MAP[effectType][1] > 0) {
                    AudioService.this.mSoundPool.play(AudioService.this.SOUND_EFFECT_FILES_MAP[effectType][1], volFloat, volFloat, 0, 0, 1.0f);
                } else {
                    MediaPlayer mediaPlayer = new MediaPlayer();
                    try {
                        try {
                            String filePath = Environment.getRootDirectory() + AudioService.SOUND_EFFECTS_PATH + ((String) AudioService.SOUND_EFFECT_FILES.get(AudioService.this.SOUND_EFFECT_FILES_MAP[effectType][0]));
                            mediaPlayer.setDataSource(filePath);
                            mediaPlayer.setAudioStreamType(1);
                            mediaPlayer.prepare();
                            mediaPlayer.setVolume(volFloat);
                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { // from class: android.media.AudioService.AudioHandler.1
                                @Override // android.media.MediaPlayer.OnCompletionListener
                                public void onCompletion(MediaPlayer mp) {
                                    AudioHandler.this.cleanupPlayer(mp);
                                }
                            });
                            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() { // from class: android.media.AudioService.AudioHandler.2
                                @Override // android.media.MediaPlayer.OnErrorListener
                                public boolean onError(MediaPlayer mp, int what, int extra) {
                                    AudioHandler.this.cleanupPlayer(mp);
                                    return true;
                                }
                            });
                            mediaPlayer.start();
                        } catch (IOException ex) {
                            Log.w(AudioService.TAG, "MediaPlayer IOException: " + ex);
                        } catch (IllegalArgumentException ex2) {
                            Log.w(AudioService.TAG, "MediaPlayer IllegalArgumentException: " + ex2);
                        }
                    } catch (IllegalStateException ex3) {
                        Log.w(AudioService.TAG, "MediaPlayer IllegalStateException: " + ex3);
                    }
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void cleanupPlayer(MediaPlayer mp) {
            if (mp != null) {
                try {
                    mp.stop();
                    mp.release();
                } catch (IllegalStateException ex) {
                    Log.w(AudioService.TAG, "MediaPlayer IllegalStateException: " + ex);
                }
            }
        }

        private void setForceUse(int usage, int config) {
            AudioSystem.setForceUse(usage, config);
        }

        private void onPersistSafeVolumeState(int state) {
            Settings.Global.putInt(AudioService.this.mContentResolver, Settings.Global.AUDIO_SAFE_VOLUME_STATE, state);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            AudioRoutesInfo routes;
            switch (msg.what) {
                case 0:
                    setDeviceVolume((VolumeStreamState) msg.obj, msg.arg1);
                    return;
                case 1:
                    persistVolume((VolumeStreamState) msg.obj, msg.arg1);
                    return;
                case 2:
                    if (!AudioService.this.mUseFixedVolume) {
                        Settings.System.putFloatForUser(AudioService.this.mContentResolver, Settings.System.VOLUME_MASTER, msg.arg1 / 1000.0f, -2);
                        return;
                    }
                    return;
                case 3:
                    persistRingerMode(AudioService.this.getRingerMode());
                    return;
                case 4:
                    if (AudioSystem.checkAudioFlinger() != 0) {
                        Log.e(AudioService.TAG, "Media server died.");
                        AudioService.sendMsg(AudioService.this.mAudioHandler, 4, 1, 0, 0, null, 500);
                        return;
                    }
                    Log.e(AudioService.TAG, "Media server started.");
                    AudioSystem.setParameters("restarting=true");
                    AudioService.readAndSetLowRamDevice();
                    synchronized (AudioService.this.mConnectedDevices) {
                        Set<Map.Entry> set = AudioService.this.mConnectedDevices.entrySet();
                        for (Map.Entry device : set) {
                            AudioSystem.setDeviceConnectionState(((Integer) device.getKey()).intValue(), 1, (String) device.getValue());
                        }
                    }
                    AudioSystem.setPhoneState(AudioService.this.mMode);
                    AudioSystem.setForceUse(0, AudioService.this.mForcedUseForComm);
                    AudioSystem.setForceUse(2, AudioService.this.mForcedUseForComm);
                    AudioSystem.setForceUse(4, AudioService.this.mCameraSoundForced.booleanValue() ? 11 : 0);
                    int numStreamTypes = AudioSystem.getNumStreamTypes();
                    for (int streamType = numStreamTypes - 1; streamType >= 0; streamType--) {
                        VolumeStreamState streamState = AudioService.this.mStreamStates[streamType];
                        AudioSystem.initStreamVolume(streamType, 0, (streamState.mIndexMax + 5) / 10);
                        streamState.applyAllVolumes();
                    }
                    AudioService.this.setRingerModeInt(AudioService.this.getRingerMode(), false);
                    AudioService.this.restoreMasterVolume();
                    if (AudioService.this.mMonitorOrientation) {
                        AudioService.this.setOrientationForAudioSystem();
                    }
                    if (AudioService.this.mMonitorRotation) {
                        AudioService.this.setRotationForAudioSystem();
                    }
                    synchronized (AudioService.this.mBluetoothA2dpEnabledLock) {
                        AudioSystem.setForceUse(1, AudioService.this.mBluetoothA2dpEnabled ? 0 : 10);
                    }
                    synchronized (AudioService.this.mSettingsLock) {
                        AudioSystem.setForceUse(3, AudioService.this.mDockAudioMediaEnabled ? 8 : 0);
                    }
                    AudioSystem.setParameters("restarting=false");
                    return;
                case 5:
                    onPlaySoundEffect(msg.arg1, msg.arg2);
                    return;
                case 6:
                    synchronized (AudioService.this.mConnectedDevices) {
                        AudioService.this.makeA2dpDeviceUnavailableNow((String) msg.obj);
                    }
                    return;
                case 7:
                    boolean loaded = onLoadSoundEffects();
                    if (msg.obj != null) {
                        LoadSoundEffectReply reply = (LoadSoundEffectReply) msg.obj;
                        synchronized (reply) {
                            reply.mStatus = loaded ? 0 : -1;
                            reply.notify();
                        }
                        return;
                    }
                    return;
                case 8:
                case 13:
                    setForceUse(msg.arg1, msg.arg2);
                    return;
                case 9:
                    AudioService.this.resetBluetoothSco();
                    return;
                case 10:
                    setAllVolumes((VolumeStreamState) msg.obj);
                    return;
                case 11:
                    if (!AudioService.this.mUseFixedVolume) {
                        Settings.System.putIntForUser(AudioService.this.mContentResolver, Settings.System.VOLUME_MASTER_MUTE, msg.arg1, -2);
                        return;
                    }
                    return;
                case 12:
                    int N = AudioService.this.mRoutesObservers.beginBroadcast();
                    if (N > 0) {
                        synchronized (AudioService.this.mCurAudioRoutes) {
                            routes = new AudioRoutesInfo(AudioService.this.mCurAudioRoutes);
                        }
                        while (N > 0) {
                            N--;
                            IAudioRoutesObserver obs = AudioService.this.mRoutesObservers.getBroadcastItem(N);
                            try {
                                obs.dispatchAudioRoutesChanged(routes);
                            } catch (RemoteException e) {
                            }
                        }
                    }
                    AudioService.this.mRoutesObservers.finishBroadcast();
                    return;
                case 14:
                    AudioService.this.onCheckMusicActive();
                    return;
                case 15:
                    AudioService.this.onSendBecomingNoisyIntent();
                    return;
                case 16:
                case 17:
                    AudioService.this.onConfigureSafeVolume(msg.what == 17);
                    return;
                case 18:
                    onPersistSafeVolumeState(msg.arg1);
                    return;
                case 19:
                    AudioService.this.onBroadcastScoConnectionState(msg.arg1);
                    return;
                case 20:
                    onUnloadSoundEffects();
                    return;
                case 21:
                case 22:
                case 23:
                case 24:
                case 25:
                case 26:
                case 27:
                case 28:
                case 29:
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57:
                case 58:
                case 59:
                case 60:
                case 61:
                case 62:
                case 63:
                case 64:
                case 65:
                case 66:
                case 67:
                case 68:
                case 69:
                case 70:
                case 71:
                case 72:
                case 73:
                case 74:
                case 75:
                case 76:
                case 77:
                case 78:
                case 79:
                case 80:
                case 81:
                case 82:
                case 83:
                case 84:
                case 85:
                case 86:
                case 87:
                case 88:
                case 89:
                case 90:
                case 91:
                case 92:
                case 93:
                case 94:
                case 95:
                case 96:
                case 97:
                case 98:
                case 99:
                default:
                    return;
                case 100:
                    AudioService.this.onSetWiredDeviceConnectionState(msg.arg1, msg.arg2, (String) msg.obj);
                    AudioService.this.mAudioEventWakeLock.release();
                    return;
                case 101:
                    AudioService.this.onSetA2dpConnectionState((BluetoothDevice) msg.obj, msg.arg1);
                    AudioService.this.mAudioEventWakeLock.release();
                    return;
            }
        }
    }

    /* loaded from: AudioService$SettingsObserver.class */
    private class SettingsObserver extends ContentObserver {
        SettingsObserver() {
            super(new Handler());
            AudioService.this.mContentResolver.registerContentObserver(Settings.System.getUriFor(Settings.System.MODE_RINGER_STREAMS_AFFECTED), false, this);
            AudioService.this.mContentResolver.registerContentObserver(Settings.Global.getUriFor(Settings.Global.DOCK_AUDIO_MEDIA_ENABLED), false, this);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            synchronized (AudioService.this.mSettingsLock) {
                if (AudioService.this.updateRingerModeAffectedStreams()) {
                    AudioService.this.setRingerModeInt(AudioService.this.getRingerMode(), false);
                }
                AudioService.this.readDockAudioSettings(AudioService.this.mContentResolver);
            }
        }
    }

    private void makeA2dpDeviceAvailable(String address) {
        VolumeStreamState streamState = this.mStreamStates[3];
        sendMsg(this.mAudioHandler, 0, 2, 128, 0, streamState, 0);
        setBluetoothA2dpOnInt(true);
        AudioSystem.setDeviceConnectionState(128, 1, address);
        AudioSystem.setParameters("A2dpSuspended=false");
        this.mConnectedDevices.put(new Integer(128), address);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSendBecomingNoisyIntent() {
        sendBroadcastToAll(new Intent(AudioManager.ACTION_AUDIO_BECOMING_NOISY));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void makeA2dpDeviceUnavailableNow(String address) {
        synchronized (this.mA2dpAvrcpLock) {
            this.mAvrcpAbsVolSupported = false;
        }
        AudioSystem.setDeviceConnectionState(128, 0, address);
        this.mConnectedDevices.remove(128);
    }

    private void makeA2dpDeviceUnavailableLater(String address) {
        AudioSystem.setParameters("A2dpSuspended=true");
        this.mConnectedDevices.remove(128);
        Message msg = this.mAudioHandler.obtainMessage(6, address);
        this.mAudioHandler.sendMessageDelayed(msg, 8000L);
    }

    private void cancelA2dpDeviceTimeout() {
        this.mAudioHandler.removeMessages(6);
    }

    private boolean hasScheduledA2dpDockTimeout() {
        return this.mAudioHandler.hasMessages(6);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSetA2dpConnectionState(BluetoothDevice btDevice, int state) {
        if (btDevice == null) {
            return;
        }
        String address = btDevice.getAddress();
        if (!BluetoothAdapter.checkBluetoothAddress(address)) {
            address = "";
        }
        synchronized (this.mConnectedDevices) {
            boolean isConnected = this.mConnectedDevices.containsKey(128) && this.mConnectedDevices.get(128).equals(address);
            if (isConnected && state != 2) {
                if (btDevice.isBluetoothDock()) {
                    if (state == 0) {
                        makeA2dpDeviceUnavailableLater(address);
                    }
                } else {
                    makeA2dpDeviceUnavailableNow(address);
                }
                synchronized (this.mCurAudioRoutes) {
                    if (this.mCurAudioRoutes.mBluetoothName != null) {
                        this.mCurAudioRoutes.mBluetoothName = null;
                        sendMsg(this.mAudioHandler, 12, 1, 0, 0, null, 0);
                    }
                }
            } else if (!isConnected && state == 2) {
                if (btDevice.isBluetoothDock()) {
                    cancelA2dpDeviceTimeout();
                    this.mDockAddress = address;
                } else if (hasScheduledA2dpDockTimeout()) {
                    cancelA2dpDeviceTimeout();
                    makeA2dpDeviceUnavailableNow(this.mDockAddress);
                }
                makeA2dpDeviceAvailable(address);
                synchronized (this.mCurAudioRoutes) {
                    String name = btDevice.getAliasName();
                    if (!TextUtils.equals(this.mCurAudioRoutes.mBluetoothName, name)) {
                        this.mCurAudioRoutes.mBluetoothName = name;
                        sendMsg(this.mAudioHandler, 12, 1, 0, 0, null, 0);
                    }
                }
            }
        }
    }

    @Override // android.media.IAudioService
    public void avrcpSupportsAbsoluteVolume(String address, boolean support) {
        synchronized (this.mA2dpAvrcpLock) {
            this.mAvrcpAbsVolSupported = support;
            VolumeStreamState streamState = this.mStreamStates[3];
            sendMsg(this.mAudioHandler, 0, 2, 128, 0, streamState, 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean handleDeviceConnection(boolean connected, int device, String params) {
        synchronized (this.mConnectedDevices) {
            boolean isConnected = this.mConnectedDevices.containsKey(Integer.valueOf(device)) && (params.isEmpty() || this.mConnectedDevices.get(Integer.valueOf(device)).equals(params));
            if (isConnected && !connected) {
                AudioSystem.setDeviceConnectionState(device, 0, this.mConnectedDevices.get(Integer.valueOf(device)));
                this.mConnectedDevices.remove(Integer.valueOf(device));
                return true;
            } else if (!isConnected && connected) {
                AudioSystem.setDeviceConnectionState(device, 1, params);
                this.mConnectedDevices.put(new Integer(device), params);
                return true;
            } else {
                return false;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int checkSendBecomingNoisyIntent(int device, int state) {
        int delay = 0;
        if (state == 0 && (device & this.mBecomingNoisyIntentDevices) != 0) {
            int devices = 0;
            for (Integer num : this.mConnectedDevices.keySet()) {
                int dev = num.intValue();
                if ((dev & this.mBecomingNoisyIntentDevices) != 0) {
                    devices |= dev;
                }
            }
            if (devices == device) {
                sendMsg(this.mAudioHandler, 15, 0, 0, 0, null, 0);
                delay = 1000;
            }
        }
        delay = (this.mAudioHandler.hasMessages(101) || this.mAudioHandler.hasMessages(100)) ? 1000 : 1000;
        return delay;
    }

    private void sendDeviceConnectionIntent(int device, int state, String name) {
        int newConn;
        Intent intent = new Intent();
        intent.putExtra("state", state);
        intent.putExtra("name", name);
        intent.addFlags(1073741824);
        int connType = 0;
        if (device == 4) {
            connType = 1;
            intent.setAction(Intent.ACTION_HEADSET_PLUG);
            intent.putExtra("microphone", 1);
        } else if (device == 8) {
            connType = 2;
            intent.setAction(Intent.ACTION_HEADSET_PLUG);
            intent.putExtra("microphone", 0);
        } else if (device == 2048) {
            connType = 4;
            intent.setAction(Intent.ACTION_ANALOG_AUDIO_DOCK_PLUG);
        } else if (device == 4096) {
            connType = 4;
            intent.setAction(Intent.ACTION_DIGITAL_AUDIO_DOCK_PLUG);
        } else if (device == 1024) {
            connType = 8;
            intent.setAction(Intent.ACTION_HDMI_AUDIO_PLUG);
        }
        synchronized (this.mCurAudioRoutes) {
            if (connType != 0) {
                int newConn2 = this.mCurAudioRoutes.mMainType;
                if (state != 0) {
                    newConn = newConn2 | connType;
                } else {
                    newConn = newConn2 & (connType ^ (-1));
                }
                if (newConn != this.mCurAudioRoutes.mMainType) {
                    this.mCurAudioRoutes.mMainType = newConn;
                    sendMsg(this.mAudioHandler, 12, 1, 0, 0, null, 0);
                }
            }
        }
        long ident = Binder.clearCallingIdentity();
        try {
            ActivityManagerNative.broadcastStickyIntent(intent, null, -1);
            Binder.restoreCallingIdentity(ident);
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(ident);
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSetWiredDeviceConnectionState(int device, int state, String name) {
        synchronized (this.mConnectedDevices) {
            if (state == 0 && (device == 4 || device == 8)) {
                setBluetoothA2dpOnInt(true);
            }
            boolean isUsb = (device & AudioSystem.DEVICE_OUT_ALL_USB) != 0;
            handleDeviceConnection(state == 1, device, isUsb ? name : "");
            if (state != 0) {
                if (device == 4 || device == 8) {
                    setBluetoothA2dpOnInt(false);
                }
                if ((device & 12) != 0) {
                    sendMsg(this.mAudioHandler, 14, 0, 0, 0, null, 60000);
                }
            }
            if (!isUsb) {
                sendDeviceConnectionIntent(device, state, name);
            }
        }
    }

    /* loaded from: AudioService$AudioServiceBroadcastReceiver.class */
    private class AudioServiceBroadcastReceiver extends BroadcastReceiver {
        private AudioServiceBroadcastReceiver() {
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            int config;
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_DOCK_EVENT)) {
                int dockState = intent.getIntExtra(Intent.EXTRA_DOCK_STATE, 0);
                switch (dockState) {
                    case 0:
                    default:
                        config = 0;
                        break;
                    case 1:
                        config = 7;
                        break;
                    case 2:
                        config = 6;
                        break;
                    case 3:
                        config = 8;
                        break;
                    case 4:
                        config = 9;
                        break;
                }
                if (dockState != 3 && (dockState != 0 || AudioService.this.mDockState != 3)) {
                    AudioSystem.setForceUse(3, config);
                }
                AudioService.this.mDockState = dockState;
            } else if (action.equals(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, 0);
                int device = 16;
                BluetoothDevice btDevice = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (btDevice == null) {
                    return;
                }
                String address = btDevice.getAddress();
                BluetoothClass btClass = btDevice.getBluetoothClass();
                if (btClass != null) {
                    switch (btClass.getDeviceClass()) {
                        case 1028:
                        case 1032:
                            device = 32;
                            break;
                        case BluetoothClass.Device.AUDIO_VIDEO_CAR_AUDIO /* 1056 */:
                            device = 64;
                            break;
                    }
                }
                if (!BluetoothAdapter.checkBluetoothAddress(address)) {
                    address = "";
                }
                boolean connected = state == 2;
                if (AudioService.this.handleDeviceConnection(connected, device, address)) {
                    synchronized (AudioService.this.mScoClients) {
                        if (connected) {
                            AudioService.this.mBluetoothHeadsetDevice = btDevice;
                        } else {
                            AudioService.this.mBluetoothHeadsetDevice = null;
                            AudioService.this.resetBluetoothSco();
                        }
                    }
                }
            } else if (action.equals(Intent.ACTION_USB_AUDIO_ACCESSORY_PLUG) || action.equals(Intent.ACTION_USB_AUDIO_DEVICE_PLUG)) {
                int state2 = intent.getIntExtra("state", 0);
                int alsaCard = intent.getIntExtra(ParameterNames.CARD, -1);
                int alsaDevice = intent.getIntExtra(UsbManager.EXTRA_DEVICE, -1);
                String params = (alsaCard == -1 && alsaDevice == -1) ? "" : "card=" + alsaCard + ";device=" + alsaDevice;
                int device2 = action.equals(Intent.ACTION_USB_AUDIO_ACCESSORY_PLUG) ? 8192 : 16384;
                Log.v(AudioService.TAG, "Broadcast Receiver: Got " + (action.equals(Intent.ACTION_USB_AUDIO_ACCESSORY_PLUG) ? "ACTION_USB_AUDIO_ACCESSORY_PLUG" : "ACTION_USB_AUDIO_DEVICE_PLUG") + ", state = " + state2 + ", card: " + alsaCard + ", device: " + alsaDevice);
                AudioService.this.setWiredDeviceConnectionState(device2, state2, params);
            } else if (!action.equals(BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED)) {
                if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
                    AudioService.this.mBootCompleted = true;
                    AudioService.sendMsg(AudioService.this.mAudioHandler, 7, 2, 0, 0, null, 0);
                    AudioService.this.mKeyguardManager = (KeyguardManager) AudioService.this.mContext.getSystemService(Context.KEYGUARD_SERVICE);
                    AudioService.this.mScoConnectionState = -1;
                    AudioService.this.resetBluetoothSco();
                    AudioService.this.getBluetoothHeadset();
                    Intent newIntent = new Intent(AudioManager.ACTION_SCO_AUDIO_STATE_CHANGED);
                    newIntent.putExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, 0);
                    AudioService.this.sendStickyBroadcastToAll(newIntent);
                    BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                    if (adapter != null) {
                        adapter.getProfileProxy(AudioService.this.mContext, AudioService.this.mBluetoothProfileServiceListener, 2);
                    }
                    AudioService.sendMsg(AudioService.this.mAudioHandler, 17, 0, 0, 0, null, 30000);
                } else if (action.equals(Intent.ACTION_SCREEN_ON)) {
                    AudioSystem.setParameters("screen_state=on");
                } else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                    AudioSystem.setParameters("screen_state=off");
                } else if (action.equals(Intent.ACTION_CONFIGURATION_CHANGED)) {
                    AudioService.this.handleConfigurationChanged(context);
                } else if (action.equals(Intent.ACTION_USER_SWITCHED)) {
                    AudioService.sendMsg(AudioService.this.mAudioHandler, 15, 0, 0, 0, null, 0);
                    AudioService.this.mMediaFocusControl.discardAudioFocusOwner();
                    AudioService.this.readAudioSettings(true);
                    AudioService.sendMsg(AudioService.this.mAudioHandler, 10, 2, 0, 0, AudioService.this.mStreamStates[3], 0);
                }
            } else {
                boolean broadcast = false;
                int scoAudioState = -1;
                synchronized (AudioService.this.mScoClients) {
                    int btState = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, -1);
                    if (!AudioService.this.mScoClients.isEmpty() && (AudioService.this.mScoAudioState == 3 || AudioService.this.mScoAudioState == 1 || AudioService.this.mScoAudioState == 5)) {
                        broadcast = true;
                    }
                    switch (btState) {
                        case 10:
                            scoAudioState = 0;
                            AudioService.this.mScoAudioState = 0;
                            AudioService.this.clearAllScoClients(0, false);
                            break;
                        case 11:
                            if (AudioService.this.mScoAudioState != 3 && AudioService.this.mScoAudioState != 5 && AudioService.this.mScoAudioState != 4) {
                                AudioService.this.mScoAudioState = 2;
                            }
                            broadcast = false;
                            break;
                        case 12:
                            scoAudioState = 1;
                            if (AudioService.this.mScoAudioState != 3 && AudioService.this.mScoAudioState != 5 && AudioService.this.mScoAudioState != 4) {
                                AudioService.this.mScoAudioState = 2;
                                break;
                            }
                            break;
                        default:
                            broadcast = false;
                            break;
                    }
                }
                if (broadcast) {
                    AudioService.this.broadcastScoConnectionState(scoAudioState);
                    Intent newIntent2 = new Intent(AudioManager.ACTION_SCO_AUDIO_STATE_CHANGED);
                    newIntent2.putExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, scoAudioState);
                    AudioService.this.sendStickyBroadcastToAll(newIntent2);
                }
            }
        }
    }

    @Override // android.media.IAudioService
    public boolean registerRemoteController(IRemoteControlDisplay rcd, int w, int h, ComponentName listenerComp) {
        return this.mMediaFocusControl.registerRemoteController(rcd, w, h, listenerComp);
    }

    @Override // android.media.IAudioService
    public boolean registerRemoteControlDisplay(IRemoteControlDisplay rcd, int w, int h) {
        return this.mMediaFocusControl.registerRemoteControlDisplay(rcd, w, h);
    }

    @Override // android.media.IAudioService
    public void unregisterRemoteControlDisplay(IRemoteControlDisplay rcd) {
        this.mMediaFocusControl.unregisterRemoteControlDisplay(rcd);
    }

    @Override // android.media.IAudioService
    public void remoteControlDisplayUsesBitmapSize(IRemoteControlDisplay rcd, int w, int h) {
        this.mMediaFocusControl.remoteControlDisplayUsesBitmapSize(rcd, w, h);
    }

    @Override // android.media.IAudioService
    public void remoteControlDisplayWantsPlaybackPositionSync(IRemoteControlDisplay rcd, boolean wantsSync) {
        this.mMediaFocusControl.remoteControlDisplayWantsPlaybackPositionSync(rcd, wantsSync);
    }

    @Override // android.media.IAudioService
    public void registerMediaButtonEventReceiverForCalls(ComponentName c) {
        this.mMediaFocusControl.registerMediaButtonEventReceiverForCalls(c);
    }

    @Override // android.media.IAudioService
    public void unregisterMediaButtonEventReceiverForCalls() {
        this.mMediaFocusControl.unregisterMediaButtonEventReceiverForCalls();
    }

    @Override // android.media.IAudioService
    public void registerMediaButtonIntent(PendingIntent pi, ComponentName c, IBinder token) {
        this.mMediaFocusControl.registerMediaButtonIntent(pi, c, token);
    }

    @Override // android.media.IAudioService
    public void unregisterMediaButtonIntent(PendingIntent pi) {
        this.mMediaFocusControl.unregisterMediaButtonIntent(pi);
    }

    @Override // android.media.IAudioService
    public int registerRemoteControlClient(PendingIntent mediaIntent, IRemoteControlClient rcClient, String callingPckg) {
        return this.mMediaFocusControl.registerRemoteControlClient(mediaIntent, rcClient, callingPckg);
    }

    @Override // android.media.IAudioService
    public void unregisterRemoteControlClient(PendingIntent mediaIntent, IRemoteControlClient rcClient) {
        this.mMediaFocusControl.unregisterRemoteControlClient(mediaIntent, rcClient);
    }

    @Override // android.media.IAudioService
    public void setRemoteControlClientPlaybackPosition(int generationId, long timeMs) {
        this.mMediaFocusControl.setRemoteControlClientPlaybackPosition(generationId, timeMs);
    }

    @Override // android.media.IAudioService
    public void updateRemoteControlClientMetadata(int generationId, int key, Rating value) {
        this.mMediaFocusControl.updateRemoteControlClientMetadata(generationId, key, value);
    }

    @Override // android.media.IAudioService
    public void registerRemoteVolumeObserverForRcc(int rccId, IRemoteVolumeObserver rvo) {
        this.mMediaFocusControl.registerRemoteVolumeObserverForRcc(rccId, rvo);
    }

    @Override // android.media.IAudioService
    public int getRemoteStreamVolume() {
        return this.mMediaFocusControl.getRemoteStreamVolume();
    }

    @Override // android.media.IAudioService
    public int getRemoteStreamMaxVolume() {
        return this.mMediaFocusControl.getRemoteStreamMaxVolume();
    }

    @Override // android.media.IAudioService
    public void setRemoteStreamVolume(int index) {
        this.mMediaFocusControl.setRemoteStreamVolume(index);
    }

    @Override // android.media.IAudioService
    public void setPlaybackStateForRcc(int rccId, int state, long timeMs, float speed) {
        this.mMediaFocusControl.setPlaybackStateForRcc(rccId, state, timeMs, speed);
    }

    @Override // android.media.IAudioService
    public void setPlaybackInfoForRcc(int rccId, int what, int value) {
        this.mMediaFocusControl.setPlaybackInfoForRcc(rccId, what, value);
    }

    @Override // android.media.IAudioService
    public void dispatchMediaKeyEvent(KeyEvent keyEvent) {
        this.mMediaFocusControl.dispatchMediaKeyEvent(keyEvent);
    }

    @Override // android.media.IAudioService
    public void dispatchMediaKeyEventUnderWakelock(KeyEvent keyEvent) {
        this.mMediaFocusControl.dispatchMediaKeyEventUnderWakelock(keyEvent);
    }

    @Override // android.media.IAudioService
    public int requestAudioFocus(int mainStreamType, int durationHint, IBinder cb, IAudioFocusDispatcher fd, String clientId, String callingPackageName) {
        return this.mMediaFocusControl.requestAudioFocus(mainStreamType, durationHint, cb, fd, clientId, callingPackageName);
    }

    @Override // android.media.IAudioService
    public int abandonAudioFocus(IAudioFocusDispatcher fd, String clientId) {
        return this.mMediaFocusControl.abandonAudioFocus(fd, clientId);
    }

    @Override // android.media.IAudioService
    public void unregisterAudioFocusClient(String clientId) {
        this.mMediaFocusControl.unregisterAudioFocusClient(clientId);
    }

    @Override // android.media.IAudioService
    public int getCurrentAudioFocus() {
        return this.mMediaFocusControl.getCurrentAudioFocus();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleConfigurationChanged(Context context) {
        int newRotation;
        int newOrientation;
        try {
            Configuration config = context.getResources().getConfiguration();
            if (this.mMonitorOrientation && (newOrientation = config.orientation) != this.mDeviceOrientation) {
                this.mDeviceOrientation = newOrientation;
                setOrientationForAudioSystem();
            }
            if (this.mMonitorRotation && (newRotation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation()) != this.mDeviceRotation) {
                this.mDeviceRotation = newRotation;
                setRotationForAudioSystem();
            }
            sendMsg(this.mAudioHandler, 16, 0, 0, 0, null, 0);
            boolean cameraSoundForced = this.mContext.getResources().getBoolean(R.bool.config_camera_sound_forced);
            synchronized (this.mSettingsLock) {
                synchronized (this.mCameraSoundForced) {
                    if (cameraSoundForced != this.mCameraSoundForced.booleanValue()) {
                        this.mCameraSoundForced = Boolean.valueOf(cameraSoundForced);
                        VolumeStreamState s = this.mStreamStates[7];
                        if (cameraSoundForced) {
                            s.setAllIndexesToMax();
                            this.mRingerModeAffectedStreams &= -129;
                        } else {
                            s.setAllIndexes(this.mStreamStates[1]);
                            this.mRingerModeAffectedStreams |= 128;
                        }
                        setRingerModeInt(getRingerMode(), false);
                        sendMsg(this.mAudioHandler, 8, 2, 4, cameraSoundForced ? 11 : 0, null, 0);
                        sendMsg(this.mAudioHandler, 10, 2, 0, 0, this.mStreamStates[7], 0);
                    }
                }
            }
            this.mVolumePanel.setLayoutDirection(config.getLayoutDirection());
        } catch (Exception e) {
            Log.e(TAG, "Error handling configuration change: ", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setOrientationForAudioSystem() {
        switch (this.mDeviceOrientation) {
            case 0:
                AudioSystem.setParameters("orientation=undefined");
                return;
            case 1:
                AudioSystem.setParameters("orientation=portrait");
                return;
            case 2:
                AudioSystem.setParameters("orientation=landscape");
                return;
            case 3:
                AudioSystem.setParameters("orientation=square");
                return;
            default:
                Log.e(TAG, "Unknown orientation");
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setRotationForAudioSystem() {
        switch (this.mDeviceRotation) {
            case 0:
                AudioSystem.setParameters("rotation=0");
                return;
            case 1:
                AudioSystem.setParameters("rotation=90");
                return;
            case 2:
                AudioSystem.setParameters("rotation=180");
                return;
            case 3:
                AudioSystem.setParameters("rotation=270");
                return;
            default:
                Log.e(TAG, "Unknown device rotation");
                return;
        }
    }

    public void setBluetoothA2dpOnInt(boolean on) {
        synchronized (this.mBluetoothA2dpEnabledLock) {
            this.mBluetoothA2dpEnabled = on;
            this.mAudioHandler.removeMessages(13);
            AudioSystem.setForceUse(1, this.mBluetoothA2dpEnabled ? 0 : 10);
        }
    }

    @Override // android.media.IAudioService
    public void setRingtonePlayer(IRingtonePlayer player) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.REMOTE_AUDIO_PLAYBACK, null);
        this.mRingtonePlayer = player;
    }

    @Override // android.media.IAudioService
    public IRingtonePlayer getRingtonePlayer() {
        return this.mRingtonePlayer;
    }

    @Override // android.media.IAudioService
    public AudioRoutesInfo startWatchingRoutes(IAudioRoutesObserver observer) {
        AudioRoutesInfo routes;
        synchronized (this.mCurAudioRoutes) {
            routes = new AudioRoutesInfo(this.mCurAudioRoutes);
            this.mRoutesObservers.register(observer);
        }
        return routes;
    }

    private void setSafeMediaVolumeEnabled(boolean on) {
        synchronized (this.mSafeMediaVolumeState) {
            if (this.mSafeMediaVolumeState.intValue() != 0 && this.mSafeMediaVolumeState.intValue() != 1) {
                if (on && this.mSafeMediaVolumeState.intValue() == 2) {
                    this.mSafeMediaVolumeState = 3;
                    enforceSafeMediaVolume();
                } else if (!on && this.mSafeMediaVolumeState.intValue() == 3) {
                    this.mSafeMediaVolumeState = 2;
                    this.mMusicActiveMs = 0;
                    sendMsg(this.mAudioHandler, 14, 0, 0, 0, null, 60000);
                }
            }
        }
    }

    private void enforceSafeMediaVolume() {
        VolumeStreamState streamState = this.mStreamStates[3];
        int devices = 12;
        int i = 0;
        while (devices != 0) {
            int i2 = i;
            i++;
            int device = 1 << i2;
            if ((device & devices) != 0) {
                int index = streamState.getIndex(device);
                if (index > this.mSafeMediaVolumeIndex) {
                    streamState.setIndex(this.mSafeMediaVolumeIndex, device);
                    sendMsg(this.mAudioHandler, 0, 2, device, 0, streamState, 0);
                }
                devices &= device ^ (-1);
            }
        }
    }

    private boolean checkSafeMediaVolume(int streamType, int index, int device) {
        synchronized (this.mSafeMediaVolumeState) {
            if (this.mSafeMediaVolumeState.intValue() == 3 && this.mStreamVolumeAlias[streamType] == 3 && (device & 12) != 0 && index > this.mSafeMediaVolumeIndex) {
                return false;
            }
            return true;
        }
    }

    public void disableSafeMediaVolume() {
        synchronized (this.mSafeMediaVolumeState) {
            setSafeMediaVolumeEnabled(false);
            if (this.mPendingVolumeCommand != null) {
                onSetStreamVolume(this.mPendingVolumeCommand.mStreamType, this.mPendingVolumeCommand.mIndex, this.mPendingVolumeCommand.mFlags, this.mPendingVolumeCommand.mDevice);
                this.mPendingVolumeCommand = null;
            }
        }
    }

    @Override // android.media.IAudioService
    public boolean isCameraSoundForced() {
        boolean booleanValue;
        synchronized (this.mCameraSoundForced) {
            booleanValue = this.mCameraSoundForced.booleanValue();
        }
        return booleanValue;
    }

    private void dumpRingerMode(PrintWriter pw) {
        pw.println("\nRinger mode: ");
        pw.println("- mode: " + RINGER_MODE_NAMES[this.mRingerMode]);
        pw.print("- ringer mode affected streams = 0x");
        pw.println(Integer.toHexString(this.mRingerModeAffectedStreams));
        pw.print("- ringer mode muted streams = 0x");
        pw.println(Integer.toHexString(this.mRingerModeMutedStreams));
    }

    @Override // android.os.Binder
    protected void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.DUMP, TAG);
        this.mMediaFocusControl.dump(pw);
        dumpStreamStates(pw);
        dumpRingerMode(pw);
        pw.println("\nAudio routes:");
        pw.print("  mMainType=0x");
        pw.println(Integer.toHexString(this.mCurAudioRoutes.mMainType));
        pw.print("  mBluetoothName=");
        pw.println(this.mCurAudioRoutes.mBluetoothName);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void readAndSetLowRamDevice() {
        int status = AudioSystem.setLowRamDevice(ActivityManager.isLowRamDeviceStatic());
        if (status != 0) {
            Log.w(TAG, "AudioFlinger informed of device's low RAM attribute; status " + status);
        }
    }
}