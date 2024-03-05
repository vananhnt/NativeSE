package android.preference;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.SeekBar;
import com.android.internal.R;

/* loaded from: VolumePreference.class */
public class VolumePreference extends SeekBarDialogPreference implements PreferenceManager.OnActivityStopListener, View.OnKeyListener {
    private static final String TAG = "VolumePreference";
    private int mStreamType;
    private SeekBarVolumizer mSeekBarVolumizer;

    /* loaded from: VolumePreference$VolumeStore.class */
    public static class VolumeStore {
        public int volume = -1;
        public int originalVolume = -1;
    }

    public VolumePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.VolumePreference, 0, 0);
        this.mStreamType = a.getInt(0, 0);
        a.recycle();
    }

    public void setStreamType(int streamType) {
        this.mStreamType = streamType;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.preference.SeekBarDialogPreference, android.preference.DialogPreference
    public void onBindDialogView(View view) {
        super.onBindDialogView(view);
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekbar);
        this.mSeekBarVolumizer = new SeekBarVolumizer(this, getContext(), seekBar, this.mStreamType);
        getPreferenceManager().registerOnActivityStopListener(this);
        view.setOnKeyListener(this);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
    }

    @Override // android.preference.Preference
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (this.mSeekBarVolumizer == null) {
            return true;
        }
        boolean isdown = event.getAction() == 0;
        switch (keyCode) {
            case 24:
                if (isdown) {
                    this.mSeekBarVolumizer.changeVolumeBy(1);
                    return true;
                }
                return true;
            case 25:
                if (isdown) {
                    this.mSeekBarVolumizer.changeVolumeBy(-1);
                    return true;
                }
                return true;
            case 164:
                if (isdown) {
                    this.mSeekBarVolumizer.muteVolume();
                    return true;
                }
                return true;
            default:
                return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.preference.DialogPreference
    public void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (!positiveResult && this.mSeekBarVolumizer != null) {
            this.mSeekBarVolumizer.revertVolume();
        }
        cleanup();
    }

    @Override // android.preference.PreferenceManager.OnActivityStopListener
    public void onActivityStop() {
        if (this.mSeekBarVolumizer == null) {
            return;
        }
        this.mSeekBarVolumizer.postStopSample();
    }

    private void cleanup() {
        getPreferenceManager().unregisterOnActivityStopListener(this);
        if (this.mSeekBarVolumizer != null) {
            Dialog dialog = getDialog();
            if (dialog != null && dialog.isShowing()) {
                View view = dialog.getWindow().getDecorView().findViewById(R.id.seekbar);
                if (view != null) {
                    view.setOnKeyListener(null);
                }
                this.mSeekBarVolumizer.revertVolume();
            }
            this.mSeekBarVolumizer.stop();
            this.mSeekBarVolumizer = null;
        }
    }

    protected void onSampleStarting(SeekBarVolumizer volumizer) {
        if (this.mSeekBarVolumizer != null && volumizer != this.mSeekBarVolumizer) {
            this.mSeekBarVolumizer.stopSample();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.preference.DialogPreference, android.preference.Preference
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        if (isPersistent()) {
            return superState;
        }
        SavedState myState = new SavedState(superState);
        if (this.mSeekBarVolumizer != null) {
            this.mSeekBarVolumizer.onSaveInstanceState(myState.getVolumeStore());
        }
        return myState;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.preference.DialogPreference, android.preference.Preference
    public void onRestoreInstanceState(Parcelable state) {
        if (state == null || !state.getClass().equals(SavedState.class)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        if (this.mSeekBarVolumizer != null) {
            this.mSeekBarVolumizer.onRestoreInstanceState(myState.getVolumeStore());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: VolumePreference$SavedState.class */
    public static class SavedState extends Preference.BaseSavedState {
        VolumeStore mVolumeStore;
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: android.preference.VolumePreference.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

        public SavedState(Parcel source) {
            super(source);
            this.mVolumeStore = new VolumeStore();
            this.mVolumeStore.volume = source.readInt();
            this.mVolumeStore.originalVolume = source.readInt();
        }

        @Override // android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(this.mVolumeStore.volume);
            dest.writeInt(this.mVolumeStore.originalVolume);
        }

        VolumeStore getVolumeStore() {
            return this.mVolumeStore;
        }

        public SavedState(Parcelable superState) {
            super(superState);
            this.mVolumeStore = new VolumeStore();
        }
    }

    /* loaded from: VolumePreference$SeekBarVolumizer.class */
    public class SeekBarVolumizer implements SeekBar.OnSeekBarChangeListener, Handler.Callback {
        private Context mContext;
        private Handler mHandler;
        private AudioManager mAudioManager;
        private int mStreamType;
        private int mOriginalStreamVolume;
        private Ringtone mRingtone;
        private int mLastProgress;
        private SeekBar mSeekBar;
        private int mVolumeBeforeMute;
        private static final int MSG_SET_STREAM_VOLUME = 0;
        private static final int MSG_START_SAMPLE = 1;
        private static final int MSG_STOP_SAMPLE = 2;
        private static final int CHECK_RINGTONE_PLAYBACK_DELAY_MS = 1000;
        private ContentObserver mVolumeObserver;

        public SeekBarVolumizer(VolumePreference volumePreference, Context context, SeekBar seekBar, int streamType) {
            this(context, seekBar, streamType, null);
        }

        public SeekBarVolumizer(Context context, SeekBar seekBar, int streamType, Uri defaultUri) {
            this.mLastProgress = -1;
            this.mVolumeBeforeMute = -1;
            this.mVolumeObserver = new ContentObserver(this.mHandler) { // from class: android.preference.VolumePreference.SeekBarVolumizer.1
                @Override // android.database.ContentObserver
                public void onChange(boolean selfChange) {
                    super.onChange(selfChange);
                    if (SeekBarVolumizer.this.mSeekBar != null && SeekBarVolumizer.this.mAudioManager != null) {
                        int volume = SeekBarVolumizer.this.mAudioManager.getStreamVolume(SeekBarVolumizer.this.mStreamType);
                        SeekBarVolumizer.this.mSeekBar.setProgress(volume);
                    }
                }
            };
            this.mContext = context;
            this.mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            this.mStreamType = streamType;
            this.mSeekBar = seekBar;
            HandlerThread thread = new HandlerThread("VolumePreference.CallbackHandler");
            thread.start();
            this.mHandler = new Handler(thread.getLooper(), this);
            initSeekBar(seekBar, defaultUri);
        }

        private void initSeekBar(SeekBar seekBar, Uri defaultUri) {
            seekBar.setMax(this.mAudioManager.getStreamMaxVolume(this.mStreamType));
            this.mOriginalStreamVolume = this.mAudioManager.getStreamVolume(this.mStreamType);
            seekBar.setProgress(this.mOriginalStreamVolume);
            seekBar.setOnSeekBarChangeListener(this);
            this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(Settings.System.VOLUME_SETTINGS[this.mStreamType]), false, this.mVolumeObserver);
            if (defaultUri == null) {
                if (this.mStreamType == 2) {
                    defaultUri = Settings.System.DEFAULT_RINGTONE_URI;
                } else if (this.mStreamType == 5) {
                    defaultUri = Settings.System.DEFAULT_NOTIFICATION_URI;
                } else {
                    defaultUri = Settings.System.DEFAULT_ALARM_ALERT_URI;
                }
            }
            this.mRingtone = RingtoneManager.getRingtone(this.mContext, defaultUri);
            if (this.mRingtone != null) {
                this.mRingtone.setStreamType(this.mStreamType);
            }
        }

        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    this.mAudioManager.setStreamVolume(this.mStreamType, this.mLastProgress, 0);
                    return true;
                case 1:
                    onStartSample();
                    return true;
                case 2:
                    onStopSample();
                    return true;
                default:
                    Log.e(VolumePreference.TAG, "invalid SeekBarVolumizer message: " + msg.what);
                    return true;
            }
        }

        private void postStartSample() {
            this.mHandler.removeMessages(1);
            this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(1), isSamplePlaying() ? 1000L : 0L);
        }

        private void onStartSample() {
            if (!isSamplePlaying()) {
                VolumePreference.this.onSampleStarting(this);
                if (this.mRingtone != null) {
                    this.mRingtone.play();
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void postStopSample() {
            this.mHandler.removeMessages(1);
            this.mHandler.removeMessages(2);
            this.mHandler.sendMessage(this.mHandler.obtainMessage(2));
        }

        private void onStopSample() {
            if (this.mRingtone != null) {
                this.mRingtone.stop();
            }
        }

        public void stop() {
            postStopSample();
            this.mContext.getContentResolver().unregisterContentObserver(this.mVolumeObserver);
            this.mSeekBar.setOnSeekBarChangeListener(null);
        }

        public void revertVolume() {
            this.mAudioManager.setStreamVolume(this.mStreamType, this.mOriginalStreamVolume, 0);
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
            if (!fromTouch) {
                return;
            }
            postSetVolume(progress);
        }

        void postSetVolume(int progress) {
            this.mLastProgress = progress;
            this.mHandler.removeMessages(0);
            this.mHandler.sendMessage(this.mHandler.obtainMessage(0));
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onStopTrackingTouch(SeekBar seekBar) {
            postStartSample();
        }

        public boolean isSamplePlaying() {
            return this.mRingtone != null && this.mRingtone.isPlaying();
        }

        public void startSample() {
            postStartSample();
        }

        public void stopSample() {
            postStopSample();
        }

        public SeekBar getSeekBar() {
            return this.mSeekBar;
        }

        public void changeVolumeBy(int amount) {
            this.mSeekBar.incrementProgressBy(amount);
            postSetVolume(this.mSeekBar.getProgress());
            postStartSample();
            this.mVolumeBeforeMute = -1;
        }

        public void muteVolume() {
            if (this.mVolumeBeforeMute != -1) {
                this.mSeekBar.setProgress(this.mVolumeBeforeMute);
                postSetVolume(this.mVolumeBeforeMute);
                postStartSample();
                this.mVolumeBeforeMute = -1;
                return;
            }
            this.mVolumeBeforeMute = this.mSeekBar.getProgress();
            this.mSeekBar.setProgress(0);
            postStopSample();
            postSetVolume(0);
        }

        public void onSaveInstanceState(VolumeStore volumeStore) {
            if (this.mLastProgress >= 0) {
                volumeStore.volume = this.mLastProgress;
                volumeStore.originalVolume = this.mOriginalStreamVolume;
            }
        }

        public void onRestoreInstanceState(VolumeStore volumeStore) {
            if (volumeStore.volume != -1) {
                this.mOriginalStreamVolume = volumeStore.originalVolume;
                this.mLastProgress = volumeStore.volume;
                postSetVolume(this.mLastProgress);
            }
        }
    }
}