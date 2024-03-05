package android.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.AudioService;
import android.media.AudioSystem;
import android.media.RingtoneManager;
import android.media.ToneGenerator;
import android.media.VolumeController;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import com.android.internal.R;
import gov.nist.core.Separators;
import java.util.HashMap;

/* loaded from: VolumePanel.class */
public class VolumePanel extends Handler implements SeekBar.OnSeekBarChangeListener, View.OnClickListener, VolumeController {
    private static final String TAG = "VolumePanel";
    public static final int PLAY_SOUND_DELAY = 300;
    public static final int VIBRATE_DELAY = 300;
    private static final int VIBRATE_DURATION = 300;
    private static final int BEEP_DURATION = 150;
    private static final int MAX_VOLUME = 100;
    private static final int FREE_DELAY = 10000;
    private static final int TIMEOUT_DELAY = 3000;
    private static final int MSG_VOLUME_CHANGED = 0;
    private static final int MSG_FREE_RESOURCES = 1;
    private static final int MSG_PLAY_SOUND = 2;
    private static final int MSG_STOP_SOUNDS = 3;
    private static final int MSG_VIBRATE = 4;
    private static final int MSG_TIMEOUT = 5;
    private static final int MSG_RINGER_MODE_CHANGED = 6;
    private static final int MSG_MUTE_CHANGED = 7;
    private static final int MSG_REMOTE_VOLUME_CHANGED = 8;
    private static final int MSG_REMOTE_VOLUME_UPDATE_IF_SHOWN = 9;
    private static final int MSG_SLIDER_VISIBILITY_CHANGED = 10;
    private static final int MSG_DISPLAY_SAFE_VOLUME_WARNING = 11;
    private static final int STREAM_MASTER = -100;
    protected Context mContext;
    private AudioManager mAudioManager;
    protected AudioService mAudioService;
    private boolean mRingIsSilent;
    private boolean mShowCombinedVolumes;
    private boolean mVoiceCapable;
    private final boolean mPlayMasterStreamTones;
    private final Dialog mDialog;
    private final View mView;
    private final ViewGroup mPanel;
    private final ViewGroup mSliderGroup;
    private final View mMoreButton;
    private final View mDivider;
    private int mActiveStreamType = -1;
    private HashMap<Integer, StreamControl> mStreamControls;
    private ToneGenerator[] mToneGenerators;
    private Vibrator mVibrator;
    private static AlertDialog sConfirmSafeVolumeDialog;
    private static boolean LOGD = false;
    private static final StreamResources[] STREAMS = {StreamResources.BluetoothSCOStream, StreamResources.RingerStream, StreamResources.VoiceStream, StreamResources.MediaStream, StreamResources.NotificationStream, StreamResources.AlarmStream, StreamResources.MasterStream, StreamResources.RemoteStream};
    private static Object sConfirmSafeVolumeLock = new Object();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: VolumePanel$StreamResources.class */
    public enum StreamResources {
        BluetoothSCOStream(6, R.string.volume_icon_description_bluetooth, R.drawable.ic_audio_bt, R.drawable.ic_audio_bt, false),
        RingerStream(2, R.string.volume_icon_description_ringer, R.drawable.ic_audio_ring_notif, R.drawable.ic_audio_ring_notif_mute, false),
        VoiceStream(0, R.string.volume_icon_description_incall, R.drawable.ic_audio_phone, R.drawable.ic_audio_phone, false),
        AlarmStream(4, R.string.volume_alarm, R.drawable.ic_audio_alarm, R.drawable.ic_audio_alarm_mute, false),
        MediaStream(3, R.string.volume_icon_description_media, R.drawable.ic_audio_vol, R.drawable.ic_audio_vol_mute, true),
        NotificationStream(5, R.string.volume_icon_description_notification, R.drawable.ic_audio_notification, R.drawable.ic_audio_notification_mute, true),
        MasterStream(-100, R.string.volume_icon_description_media, R.drawable.ic_audio_vol, R.drawable.ic_audio_vol_mute, false),
        RemoteStream(AudioService.STREAM_REMOTE_MUSIC, R.string.volume_icon_description_media, R.drawable.ic_media_route_on_holo_dark, R.drawable.ic_media_route_disabled_holo_dark, false);
        
        int streamType;
        int descRes;
        int iconRes;
        int iconMuteRes;
        boolean show;

        StreamResources(int streamType, int descRes, int iconRes, int iconMuteRes, boolean show) {
            this.streamType = streamType;
            this.descRes = descRes;
            this.iconRes = iconRes;
            this.iconMuteRes = iconMuteRes;
            this.show = show;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: VolumePanel$StreamControl.class */
    public class StreamControl {
        int streamType;
        ViewGroup group;
        ImageView icon;
        SeekBar seekbarView;
        int iconRes;
        int iconMuteRes;

        private StreamControl() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: VolumePanel$WarningDialogReceiver.class */
    public static class WarningDialogReceiver extends BroadcastReceiver implements DialogInterface.OnDismissListener {
        private final Context mContext;
        private final Dialog mDialog;
        private final VolumePanel mVolumePanel;

        WarningDialogReceiver(Context context, Dialog dialog, VolumePanel volumePanel) {
            this.mContext = context;
            this.mDialog = dialog;
            this.mVolumePanel = volumePanel;
            IntentFilter filter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            context.registerReceiver(this, filter);
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            this.mDialog.cancel();
            cleanUp();
        }

        @Override // android.content.DialogInterface.OnDismissListener
        public void onDismiss(DialogInterface unused) {
            this.mContext.unregisterReceiver(this);
            cleanUp();
        }

        private void cleanUp() {
            synchronized (VolumePanel.sConfirmSafeVolumeLock) {
                AlertDialog unused = VolumePanel.sConfirmSafeVolumeDialog = null;
            }
            this.mVolumePanel.forceTimeout();
            this.mVolumePanel.updateStates();
        }
    }

    public VolumePanel(Context context, AudioService volumeService) {
        this.mContext = context;
        this.mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        this.mAudioService = volumeService;
        boolean useMasterVolume = context.getResources().getBoolean(R.bool.config_useMasterVolume);
        if (useMasterVolume) {
            for (int i = 0; i < STREAMS.length; i++) {
                StreamResources streamRes = STREAMS[i];
                streamRes.show = streamRes.streamType == -100;
            }
        }
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mView = inflater.inflate(R.layout.volume_adjust, (ViewGroup) null);
        this.mView.setOnTouchListener(new View.OnTouchListener() { // from class: android.view.VolumePanel.1
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                VolumePanel.this.resetTimeout();
                return false;
            }
        });
        this.mPanel = (ViewGroup) this.mView.findViewById(R.id.visible_panel);
        this.mSliderGroup = (ViewGroup) this.mView.findViewById(R.id.slider_group);
        this.mMoreButton = (ImageView) this.mView.findViewById(R.id.expand_button);
        this.mDivider = (ImageView) this.mView.findViewById(R.id.expand_button_divider);
        this.mDialog = new Dialog(context, R.style.Theme_Panel_Volume) { // from class: android.view.VolumePanel.2
            @Override // android.app.Dialog
            public boolean onTouchEvent(MotionEvent event) {
                if (isShowing() && event.getAction() == 4 && VolumePanel.sConfirmSafeVolumeDialog == null) {
                    VolumePanel.this.forceTimeout();
                    return true;
                }
                return false;
            }
        };
        this.mDialog.setTitle("Volume control");
        this.mDialog.setContentView(this.mView);
        this.mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: android.view.VolumePanel.3
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialog) {
                VolumePanel.this.mActiveStreamType = -1;
                VolumePanel.this.mAudioManager.forceVolumeControlStream(VolumePanel.this.mActiveStreamType);
            }
        });
        Window window = this.mDialog.getWindow();
        window.setGravity(48);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.token = null;
        lp.y = this.mContext.getResources().getDimensionPixelOffset(R.dimen.volume_panel_top);
        lp.type = WindowManager.LayoutParams.TYPE_VOLUME_OVERLAY;
        lp.width = -2;
        lp.height = -2;
        lp.privateFlags |= 32;
        window.setAttributes(lp);
        window.addFlags(262184);
        this.mToneGenerators = new ToneGenerator[AudioSystem.getNumStreamTypes()];
        this.mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        this.mVoiceCapable = context.getResources().getBoolean(R.bool.config_voice_capable);
        this.mShowCombinedVolumes = (this.mVoiceCapable || useMasterVolume) ? false : true;
        if (!this.mShowCombinedVolumes) {
            this.mMoreButton.setVisibility(8);
            this.mDivider.setVisibility(8);
        } else {
            this.mMoreButton.setOnClickListener(this);
        }
        boolean masterVolumeOnly = context.getResources().getBoolean(R.bool.config_useMasterVolume);
        boolean masterVolumeKeySounds = this.mContext.getResources().getBoolean(R.bool.config_useVolumeKeySounds);
        this.mPlayMasterStreamTones = masterVolumeOnly && masterVolumeKeySounds;
        listenToRingerMode();
    }

    public void setLayoutDirection(int layoutDirection) {
        this.mPanel.setLayoutDirection(layoutDirection);
        updateStates();
    }

    private void listenToRingerMode() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(AudioManager.RINGER_MODE_CHANGED_ACTION);
        this.mContext.registerReceiver(new BroadcastReceiver() { // from class: android.view.VolumePanel.4
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (AudioManager.RINGER_MODE_CHANGED_ACTION.equals(action)) {
                    VolumePanel.this.removeMessages(6);
                    VolumePanel.this.sendMessage(VolumePanel.this.obtainMessage(6));
                }
            }
        }, filter);
    }

    private boolean isMuted(int streamType) {
        if (streamType == -100) {
            return this.mAudioManager.isMasterMute();
        }
        if (streamType == -200) {
            return this.mAudioService.getRemoteStreamVolume() <= 0;
        }
        return this.mAudioManager.isStreamMute(streamType);
    }

    private int getStreamMaxVolume(int streamType) {
        if (streamType == -100) {
            return this.mAudioManager.getMasterMaxVolume();
        }
        if (streamType == -200) {
            return this.mAudioService.getRemoteStreamMaxVolume();
        }
        return this.mAudioManager.getStreamMaxVolume(streamType);
    }

    private int getStreamVolume(int streamType) {
        if (streamType == -100) {
            return this.mAudioManager.getMasterVolume();
        }
        if (streamType == -200) {
            return this.mAudioService.getRemoteStreamVolume();
        }
        return this.mAudioManager.getStreamVolume(streamType);
    }

    private void setStreamVolume(int streamType, int index, int flags) {
        if (streamType == -100) {
            this.mAudioManager.setMasterVolume(index, flags);
        } else if (streamType == -200) {
            this.mAudioService.setRemoteStreamVolume(index);
        } else {
            this.mAudioManager.setStreamVolume(streamType, index, flags);
        }
    }

    private void createSliders() {
        LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mStreamControls = new HashMap<>(STREAMS.length);
        Resources res = this.mContext.getResources();
        for (int i = 0; i < STREAMS.length; i++) {
            StreamResources streamRes = STREAMS[i];
            int streamType = streamRes.streamType;
            if (this.mVoiceCapable && streamRes == StreamResources.NotificationStream) {
                streamRes = StreamResources.RingerStream;
            }
            StreamControl sc = new StreamControl();
            sc.streamType = streamType;
            sc.group = (ViewGroup) inflater.inflate(R.layout.volume_adjust_item, (ViewGroup) null);
            sc.group.setTag(sc);
            sc.icon = (ImageView) sc.group.findViewById(R.id.stream_icon);
            sc.icon.setTag(sc);
            sc.icon.setContentDescription(res.getString(streamRes.descRes));
            sc.iconRes = streamRes.iconRes;
            sc.iconMuteRes = streamRes.iconMuteRes;
            sc.icon.setImageResource(sc.iconRes);
            sc.seekbarView = (SeekBar) sc.group.findViewById(R.id.seekbar);
            int plusOne = (streamType == 6 || streamType == 0) ? 1 : 0;
            sc.seekbarView.setMax(getStreamMaxVolume(streamType) + plusOne);
            sc.seekbarView.setOnSeekBarChangeListener(this);
            sc.seekbarView.setTag(sc);
            this.mStreamControls.put(Integer.valueOf(streamType), sc);
        }
    }

    private void reorderSliders(int activeStreamType) {
        this.mSliderGroup.removeAllViews();
        StreamControl active = this.mStreamControls.get(Integer.valueOf(activeStreamType));
        if (active == null) {
            Log.e(TAG, "Missing stream type! - " + activeStreamType);
            this.mActiveStreamType = -1;
        } else {
            this.mSliderGroup.addView(active.group);
            this.mActiveStreamType = activeStreamType;
            active.group.setVisibility(0);
            updateSlider(active);
        }
        addOtherVolumes();
    }

    private void addOtherVolumes() {
        if (this.mShowCombinedVolumes) {
            for (int i = 0; i < STREAMS.length; i++) {
                int streamType = STREAMS[i].streamType;
                if (STREAMS[i].show && streamType != this.mActiveStreamType) {
                    StreamControl sc = this.mStreamControls.get(Integer.valueOf(streamType));
                    this.mSliderGroup.addView(sc.group);
                    updateSlider(sc);
                }
            }
        }
    }

    private void updateSlider(StreamControl sc) {
        sc.seekbarView.setProgress(getStreamVolume(sc.streamType));
        boolean muted = isMuted(sc.streamType);
        sc.icon.setImageDrawable(null);
        sc.icon.setImageResource(muted ? sc.iconMuteRes : sc.iconRes);
        if ((sc.streamType == 2 || sc.streamType == 5) && this.mAudioManager.getRingerMode() == 1) {
            sc.icon.setImageResource(R.drawable.ic_audio_ring_notif_vibrate);
        }
        if (sc.streamType == -200) {
            sc.seekbarView.setEnabled(true);
        } else if ((sc.streamType != this.mAudioManager.getMasterStreamType() && muted) || sConfirmSafeVolumeDialog != null) {
            sc.seekbarView.setEnabled(false);
        } else {
            sc.seekbarView.setEnabled(true);
        }
    }

    private boolean isExpanded() {
        return this.mMoreButton.getVisibility() != 0;
    }

    private void expand() {
        int count = this.mSliderGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            this.mSliderGroup.getChildAt(i).setVisibility(0);
        }
        this.mMoreButton.setVisibility(4);
        this.mDivider.setVisibility(4);
    }

    private void collapse() {
        this.mMoreButton.setVisibility(0);
        this.mDivider.setVisibility(0);
        int count = this.mSliderGroup.getChildCount();
        for (int i = 1; i < count; i++) {
            this.mSliderGroup.getChildAt(i).setVisibility(8);
        }
    }

    public void updateStates() {
        int count = this.mSliderGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            StreamControl sc = (StreamControl) this.mSliderGroup.getChildAt(i).getTag();
            updateSlider(sc);
        }
    }

    public void postVolumeChanged(int streamType, int flags) {
        if (hasMessages(0)) {
            return;
        }
        synchronized (this) {
            if (this.mStreamControls == null) {
                createSliders();
            }
        }
        removeMessages(1);
        obtainMessage(0, streamType, flags).sendToTarget();
    }

    @Override // android.media.VolumeController
    public void postRemoteVolumeChanged(int streamType, int flags) {
        if (hasMessages(8)) {
            return;
        }
        synchronized (this) {
            if (this.mStreamControls == null) {
                createSliders();
            }
        }
        removeMessages(1);
        obtainMessage(8, streamType, flags).sendToTarget();
    }

    @Override // android.media.VolumeController
    public void postRemoteSliderVisibility(boolean visible) {
        obtainMessage(10, AudioService.STREAM_REMOTE_MUSIC, visible ? 1 : 0).sendToTarget();
    }

    @Override // android.media.VolumeController
    public void postHasNewRemotePlaybackInfo() {
        if (hasMessages(9)) {
            return;
        }
        obtainMessage(9).sendToTarget();
    }

    public void postMasterVolumeChanged(int flags) {
        postVolumeChanged(-100, flags);
    }

    public void postMuteChanged(int streamType, int flags) {
        if (hasMessages(0)) {
            return;
        }
        synchronized (this) {
            if (this.mStreamControls == null) {
                createSliders();
            }
        }
        removeMessages(1);
        obtainMessage(7, streamType, flags).sendToTarget();
    }

    public void postMasterMuteChanged(int flags) {
        postMuteChanged(-100, flags);
    }

    public void postDisplaySafeVolumeWarning(int flags) {
        if (hasMessages(11)) {
            return;
        }
        obtainMessage(11, flags, 0).sendToTarget();
    }

    protected void onVolumeChanged(int streamType, int flags) {
        if (LOGD) {
            Log.d(TAG, "onVolumeChanged(streamType: " + streamType + ", flags: " + flags + Separators.RPAREN);
        }
        if ((flags & 1) != 0) {
            synchronized (this) {
                if (this.mActiveStreamType != streamType) {
                    reorderSliders(streamType);
                }
                onShowVolumeChanged(streamType, flags);
            }
        }
        if ((flags & 4) != 0 && !this.mRingIsSilent) {
            removeMessages(2);
            sendMessageDelayed(obtainMessage(2, streamType, flags), 300L);
        }
        if ((flags & 8) != 0) {
            removeMessages(2);
            removeMessages(4);
            onStopSounds();
        }
        removeMessages(1);
        sendMessageDelayed(obtainMessage(1), 10000L);
        resetTimeout();
    }

    protected void onMuteChanged(int streamType, int flags) {
        if (LOGD) {
            Log.d(TAG, "onMuteChanged(streamType: " + streamType + ", flags: " + flags + Separators.RPAREN);
        }
        StreamControl sc = this.mStreamControls.get(Integer.valueOf(streamType));
        if (sc != null) {
            sc.icon.setImageResource(isMuted(sc.streamType) ? sc.iconMuteRes : sc.iconRes);
        }
        onVolumeChanged(streamType, flags);
    }

    protected void onShowVolumeChanged(int streamType, int flags) {
        int index = getStreamVolume(streamType);
        this.mRingIsSilent = false;
        if (LOGD) {
            Log.d(TAG, "onShowVolumeChanged(streamType: " + streamType + ", flags: " + flags + "), index: " + index);
        }
        int max = getStreamMaxVolume(streamType);
        switch (streamType) {
            case AudioService.STREAM_REMOTE_MUSIC /* -200 */:
                if (LOGD) {
                    Log.d(TAG, "showing remote volume " + index + " over " + max);
                    break;
                }
                break;
            case 0:
                index++;
                max++;
                break;
            case 2:
                Uri ringuri = RingtoneManager.getActualDefaultRingtoneUri(this.mContext, 1);
                if (ringuri == null) {
                    this.mRingIsSilent = true;
                    break;
                }
                break;
            case 3:
                if ((this.mAudioManager.getDevicesForStream(3) & AudioSystem.DEVICE_OUT_ALL_A2DP) != 0) {
                    setMusicIcon(R.drawable.ic_audio_bt, R.drawable.ic_audio_bt_mute);
                    break;
                } else {
                    setMusicIcon(R.drawable.ic_audio_vol, R.drawable.ic_audio_vol_mute);
                    break;
                }
            case 5:
                Uri ringuri2 = RingtoneManager.getActualDefaultRingtoneUri(this.mContext, 2);
                if (ringuri2 == null) {
                    this.mRingIsSilent = true;
                    break;
                }
                break;
            case 6:
                index++;
                max++;
                break;
        }
        StreamControl sc = this.mStreamControls.get(Integer.valueOf(streamType));
        if (sc != null) {
            if (sc.seekbarView.getMax() != max) {
                sc.seekbarView.setMax(max);
            }
            sc.seekbarView.setProgress(index);
            if ((flags & 32) != 0 || ((streamType != this.mAudioManager.getMasterStreamType() && streamType != -200 && isMuted(streamType)) || sConfirmSafeVolumeDialog != null)) {
                sc.seekbarView.setEnabled(false);
            } else {
                sc.seekbarView.setEnabled(true);
            }
        }
        if (!this.mDialog.isShowing()) {
            int stream = streamType == -200 ? -1 : streamType;
            this.mAudioManager.forceVolumeControlStream(stream);
            this.mDialog.setContentView(this.mView);
            if (this.mShowCombinedVolumes) {
                collapse();
            }
            this.mDialog.show();
        }
        if (streamType != -200 && (flags & 16) != 0 && this.mAudioService.isStreamAffectedByRingerMode(streamType) && this.mAudioManager.getRingerMode() == 1) {
            sendMessageDelayed(obtainMessage(4), 300L);
        }
    }

    protected void onPlaySound(int streamType, int flags) {
        if (hasMessages(3)) {
            removeMessages(3);
            onStopSounds();
        }
        synchronized (this) {
            ToneGenerator toneGen = getOrCreateToneGenerator(streamType);
            if (toneGen != null) {
                toneGen.startTone(24);
                sendMessageDelayed(obtainMessage(3), 150L);
            }
        }
    }

    protected void onStopSounds() {
        synchronized (this) {
            int numStreamTypes = AudioSystem.getNumStreamTypes();
            for (int i = numStreamTypes - 1; i >= 0; i--) {
                ToneGenerator toneGen = this.mToneGenerators[i];
                if (toneGen != null) {
                    toneGen.stopTone();
                }
            }
        }
    }

    protected void onVibrate() {
        if (this.mAudioManager.getRingerMode() != 1) {
            return;
        }
        this.mVibrator.vibrate(300L);
    }

    protected void onRemoteVolumeChanged(int streamType, int flags) {
        if (LOGD) {
            Log.d(TAG, "onRemoteVolumeChanged(stream:" + streamType + ", flags: " + flags + Separators.RPAREN);
        }
        if ((flags & 1) != 0 || this.mDialog.isShowing()) {
            synchronized (this) {
                if (this.mActiveStreamType != -200) {
                    reorderSliders(AudioService.STREAM_REMOTE_MUSIC);
                }
                onShowVolumeChanged(AudioService.STREAM_REMOTE_MUSIC, flags);
            }
        } else if (LOGD) {
            Log.d(TAG, "not calling onShowVolumeChanged(), no FLAG_SHOW_UI or no UI");
        }
        if ((flags & 4) != 0 && !this.mRingIsSilent) {
            removeMessages(2);
            sendMessageDelayed(obtainMessage(2, streamType, flags), 300L);
        }
        if ((flags & 8) != 0) {
            removeMessages(2);
            removeMessages(4);
            onStopSounds();
        }
        removeMessages(1);
        sendMessageDelayed(obtainMessage(1), 10000L);
        resetTimeout();
    }

    protected void onRemoteVolumeUpdateIfShown() {
        if (LOGD) {
            Log.d(TAG, "onRemoteVolumeUpdateIfShown()");
        }
        if (this.mDialog.isShowing() && this.mActiveStreamType == -200 && this.mStreamControls != null) {
            onShowVolumeChanged(AudioService.STREAM_REMOTE_MUSIC, 0);
        }
    }

    protected synchronized void onSliderVisibilityChanged(int streamType, int visible) {
        if (LOGD) {
            Log.d(TAG, "onSliderVisibilityChanged(stream=" + streamType + ", visi=" + visible + Separators.RPAREN);
        }
        boolean isVisible = visible == 1;
        for (int i = STREAMS.length - 1; i >= 0; i--) {
            StreamResources streamRes = STREAMS[i];
            if (streamRes.streamType == streamType) {
                streamRes.show = isVisible;
                if (!isVisible && this.mActiveStreamType == streamType) {
                    this.mActiveStreamType = -1;
                    return;
                }
                return;
            }
        }
    }

    protected void onDisplaySafeVolumeWarning(int flags) {
        if ((flags & 1) != 0 || this.mDialog.isShowing()) {
            synchronized (sConfirmSafeVolumeLock) {
                if (sConfirmSafeVolumeDialog != null) {
                    return;
                }
                sConfirmSafeVolumeDialog = new AlertDialog.Builder(this.mContext).setMessage(R.string.safe_media_volume_warning).setPositiveButton(17039379, new DialogInterface.OnClickListener() { // from class: android.view.VolumePanel.5
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        VolumePanel.this.mAudioService.disableSafeMediaVolume();
                    }
                }).setNegativeButton(17039369, (DialogInterface.OnClickListener) null).setIconAttribute(16843605).create();
                WarningDialogReceiver warning = new WarningDialogReceiver(this.mContext, sConfirmSafeVolumeDialog, this);
                sConfirmSafeVolumeDialog.setOnDismissListener(warning);
                sConfirmSafeVolumeDialog.getWindow().setType(2009);
                sConfirmSafeVolumeDialog.show();
                updateStates();
            }
        }
        resetTimeout();
    }

    private ToneGenerator getOrCreateToneGenerator(int streamType) {
        ToneGenerator toneGenerator;
        if (streamType == -100) {
            if (this.mPlayMasterStreamTones) {
                streamType = 1;
            } else {
                return null;
            }
        }
        synchronized (this) {
            if (this.mToneGenerators[streamType] == null) {
                try {
                    this.mToneGenerators[streamType] = new ToneGenerator(streamType, 100);
                } catch (RuntimeException e) {
                    if (LOGD) {
                        Log.d(TAG, "ToneGenerator constructor failed with RuntimeException: " + e);
                    }
                }
            }
            toneGenerator = this.mToneGenerators[streamType];
        }
        return toneGenerator;
    }

    private void setMusicIcon(int resId, int resMuteId) {
        StreamControl sc = this.mStreamControls.get(3);
        if (sc != null) {
            sc.iconRes = resId;
            sc.iconMuteRes = resMuteId;
            sc.icon.setImageResource(isMuted(sc.streamType) ? sc.iconMuteRes : sc.iconRes);
        }
    }

    protected void onFreeResources() {
        synchronized (this) {
            for (int i = this.mToneGenerators.length - 1; i >= 0; i--) {
                if (this.mToneGenerators[i] != null) {
                    this.mToneGenerators[i].release();
                }
                this.mToneGenerators[i] = null;
            }
        }
    }

    @Override // android.os.Handler
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case 0:
                onVolumeChanged(msg.arg1, msg.arg2);
                return;
            case 1:
                onFreeResources();
                return;
            case 2:
                onPlaySound(msg.arg1, msg.arg2);
                return;
            case 3:
                onStopSounds();
                return;
            case 4:
                onVibrate();
                return;
            case 5:
                if (this.mDialog.isShowing()) {
                    this.mDialog.dismiss();
                    this.mActiveStreamType = -1;
                }
                synchronized (sConfirmSafeVolumeLock) {
                    if (sConfirmSafeVolumeDialog != null) {
                        sConfirmSafeVolumeDialog.dismiss();
                    }
                }
                return;
            case 6:
                if (this.mDialog.isShowing()) {
                    updateStates();
                    return;
                }
                return;
            case 7:
                onMuteChanged(msg.arg1, msg.arg2);
                return;
            case 8:
                onRemoteVolumeChanged(msg.arg1, msg.arg2);
                return;
            case 9:
                onRemoteVolumeUpdateIfShown();
                return;
            case 10:
                onSliderVisibilityChanged(msg.arg1, msg.arg2);
                return;
            case 11:
                onDisplaySafeVolumeWarning(msg.arg1);
                return;
            default:
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetTimeout() {
        removeMessages(5);
        sendMessageDelayed(obtainMessage(5), 3000L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void forceTimeout() {
        removeMessages(5);
        sendMessage(obtainMessage(5));
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Object tag = seekBar.getTag();
        if (fromUser && (tag instanceof StreamControl)) {
            StreamControl sc = (StreamControl) tag;
            if (getStreamVolume(sc.streamType) != progress) {
                setStreamVolume(sc.streamType, progress, 0);
            }
        }
        resetTimeout();
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onStopTrackingTouch(SeekBar seekBar) {
        Object tag = seekBar.getTag();
        if (tag instanceof StreamControl) {
            StreamControl sc = (StreamControl) tag;
            if (sc.streamType == -200) {
                seekBar.setProgress(getStreamVolume(AudioService.STREAM_REMOTE_MUSIC));
            }
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        if (v == this.mMoreButton) {
            expand();
        }
        resetTimeout();
    }
}