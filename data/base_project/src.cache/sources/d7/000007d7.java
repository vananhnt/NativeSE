package android.media;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;
import com.android.internal.R;
import java.io.IOException;

/* loaded from: Ringtone.class */
public class Ringtone {
    private static final String TAG = "Ringtone";
    private static final boolean LOGD = true;
    private static final String[] MEDIA_COLUMNS = {"_id", "_data", "title"};
    private final Context mContext;
    private final AudioManager mAudioManager;
    private final boolean mAllowRemote;
    private final IRingtonePlayer mRemotePlayer;
    private final Binder mRemoteToken;
    private MediaPlayer mLocalPlayer;
    private Uri mUri;
    private String mTitle;
    private int mStreamType = 2;

    public Ringtone(Context context, boolean allowRemote) {
        this.mContext = context;
        this.mAudioManager = (AudioManager) this.mContext.getSystemService(Context.AUDIO_SERVICE);
        this.mAllowRemote = allowRemote;
        this.mRemotePlayer = allowRemote ? this.mAudioManager.getRingtonePlayer() : null;
        this.mRemoteToken = allowRemote ? new Binder() : null;
    }

    public void setStreamType(int streamType) {
        this.mStreamType = streamType;
        setUri(this.mUri);
    }

    public int getStreamType() {
        return this.mStreamType;
    }

    public String getTitle(Context context) {
        if (this.mTitle != null) {
            return this.mTitle;
        }
        String title = getTitle(context, this.mUri, true);
        this.mTitle = title;
        return title;
    }

    private static String getTitle(Context context, Uri uri, boolean followSettingsUri) {
        Cursor cursor = null;
        ContentResolver res = context.getContentResolver();
        String title = null;
        if (uri != null) {
            String authority = uri.getAuthority();
            if ("settings".equals(authority)) {
                if (followSettingsUri) {
                    Uri actualUri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.getDefaultType(uri));
                    String actualTitle = getTitle(context, actualUri, false);
                    title = context.getString(R.string.ringtone_default_with_actual, actualTitle);
                }
            } else {
                try {
                    if (MediaStore.AUTHORITY.equals(authority)) {
                        cursor = res.query(uri, MEDIA_COLUMNS, null, null, null);
                    }
                } catch (SecurityException e) {
                }
                if (cursor != null) {
                    try {
                        if (cursor.getCount() == 1) {
                            cursor.moveToFirst();
                            String string = cursor.getString(2);
                            if (cursor != null) {
                                cursor.close();
                            }
                            return string;
                        }
                    } catch (Throwable th) {
                        if (cursor != null) {
                            cursor.close();
                        }
                        throw th;
                    }
                }
                title = uri.getLastPathSegment();
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (title == null) {
            title = context.getString(R.string.ringtone_unknown);
            if (title == null) {
                title = "";
            }
        }
        return title;
    }

    public void setUri(Uri uri) {
        destroyLocalPlayer();
        this.mUri = uri;
        if (this.mUri == null) {
            return;
        }
        this.mLocalPlayer = new MediaPlayer();
        try {
            this.mLocalPlayer.setDataSource(this.mContext, this.mUri);
            this.mLocalPlayer.setAudioStreamType(this.mStreamType);
            this.mLocalPlayer.prepare();
        } catch (IOException e) {
            destroyLocalPlayer();
            if (!this.mAllowRemote) {
                Log.w(TAG, "Remote playback not allowed: " + e);
            }
        } catch (SecurityException e2) {
            destroyLocalPlayer();
            if (!this.mAllowRemote) {
                Log.w(TAG, "Remote playback not allowed: " + e2);
            }
        }
        if (this.mLocalPlayer != null) {
            Log.d(TAG, "Successfully created local player");
        } else {
            Log.d(TAG, "Problem opening; delegating to remote player");
        }
    }

    public Uri getUri() {
        return this.mUri;
    }

    public void play() {
        if (this.mLocalPlayer != null) {
            if (this.mAudioManager.getStreamVolume(this.mStreamType) != 0) {
                this.mLocalPlayer.start();
            }
        } else if (this.mAllowRemote) {
            Uri canonicalUri = this.mUri.getCanonicalUri();
            try {
                this.mRemotePlayer.play(this.mRemoteToken, canonicalUri, this.mStreamType);
            } catch (RemoteException e) {
                if (!playFallbackRingtone()) {
                    Log.w(TAG, "Problem playing ringtone: " + e);
                }
            }
        } else if (!playFallbackRingtone()) {
            Log.w(TAG, "Neither local nor remote playback available");
        }
    }

    public void stop() {
        if (this.mLocalPlayer != null) {
            destroyLocalPlayer();
        } else if (this.mAllowRemote) {
            try {
                this.mRemotePlayer.stop(this.mRemoteToken);
            } catch (RemoteException e) {
                Log.w(TAG, "Problem stopping ringtone: " + e);
            }
        }
    }

    private void destroyLocalPlayer() {
        if (this.mLocalPlayer != null) {
            this.mLocalPlayer.reset();
            this.mLocalPlayer.release();
            this.mLocalPlayer = null;
        }
    }

    public boolean isPlaying() {
        if (this.mLocalPlayer != null) {
            return this.mLocalPlayer.isPlaying();
        }
        if (this.mAllowRemote) {
            try {
                return this.mRemotePlayer.isPlaying(this.mRemoteToken);
            } catch (RemoteException e) {
                Log.w(TAG, "Problem checking ringtone: " + e);
                return false;
            }
        }
        Log.w(TAG, "Neither local nor remote playback available");
        return false;
    }

    private boolean playFallbackRingtone() {
        if (this.mAudioManager.getStreamVolume(this.mStreamType) != 0) {
            int ringtoneType = RingtoneManager.getDefaultType(this.mUri);
            if (ringtoneType == -1 || RingtoneManager.getActualDefaultRingtoneUri(this.mContext, ringtoneType) != null) {
                try {
                    AssetFileDescriptor afd = this.mContext.getResources().openRawResourceFd(R.raw.fallbackring);
                    if (afd != null) {
                        this.mLocalPlayer = new MediaPlayer();
                        if (afd.getDeclaredLength() < 0) {
                            this.mLocalPlayer.setDataSource(afd.getFileDescriptor());
                        } else {
                            this.mLocalPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getDeclaredLength());
                        }
                        this.mLocalPlayer.setAudioStreamType(this.mStreamType);
                        this.mLocalPlayer.prepare();
                        this.mLocalPlayer.start();
                        afd.close();
                        return true;
                    }
                    Log.e(TAG, "Could not load fallback ringtone");
                    return false;
                } catch (Resources.NotFoundException e) {
                    Log.e(TAG, "Fallback ringtone does not exist");
                    return false;
                } catch (IOException e2) {
                    destroyLocalPlayer();
                    Log.e(TAG, "Failed to open fallback ringtone");
                    return false;
                }
            }
            Log.w(TAG, "not playing fallback for " + this.mUri);
            return false;
        }
        return false;
    }

    void setTitle(String title) {
        this.mTitle = title;
    }
}