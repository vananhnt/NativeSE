package android.media;

import android.media.MediaFocusControl;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import java.io.PrintWriter;
import java.util.NoSuchElementException;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: FocusRequester.class */
public class FocusRequester {
    private static final String TAG = "MediaFocusControl";
    private static final boolean DEBUG = false;
    private MediaFocusControl.AudioFocusDeathHandler mDeathHandler;
    private final IAudioFocusDispatcher mFocusDispatcher;
    private final IBinder mSourceRef;
    private final String mClientId;
    private final String mPackageName;
    private final int mCallingUid;
    private final int mFocusGainRequest;
    private int mFocusLossReceived = 0;
    private final int mStreamType;

    /* JADX INFO: Access modifiers changed from: package-private */
    public FocusRequester(int streamType, int focusRequest, IAudioFocusDispatcher afl, IBinder source, String id, MediaFocusControl.AudioFocusDeathHandler hdlr, String pn, int uid) {
        this.mStreamType = streamType;
        this.mFocusDispatcher = afl;
        this.mSourceRef = source;
        this.mClientId = id;
        this.mDeathHandler = hdlr;
        this.mPackageName = pn;
        this.mCallingUid = uid;
        this.mFocusGainRequest = focusRequest;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasSameClient(String otherClient) {
        try {
            return this.mClientId.compareTo(otherClient) == 0;
        } catch (NullPointerException e) {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasSameBinder(IBinder ib) {
        return this.mSourceRef != null && this.mSourceRef.equals(ib);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasSamePackage(String pack) {
        try {
            return this.mPackageName.compareTo(pack) == 0;
        } catch (NullPointerException e) {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasSameUid(int uid) {
        return this.mCallingUid == uid;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getGainRequest() {
        return this.mFocusGainRequest;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getStreamType() {
        return this.mStreamType;
    }

    private static String focusChangeToString(int focus) {
        switch (focus) {
            case -3:
                return "LOSS_TRANSIENT_CAN_DUCK";
            case -2:
                return "LOSS_TRANSIENT";
            case -1:
                return "LOSS";
            case 0:
                return "none";
            case 1:
                return "GAIN";
            case 2:
                return "GAIN_TRANSIENT";
            case 3:
                return "GAIN_TRANSIENT_MAY_DUCK";
            case 4:
                return "GAIN_TRANSIENT_EXCLUSIVE";
            default:
                return "[invalid focus change" + focus + "]";
        }
    }

    private String focusGainToString() {
        return focusChangeToString(this.mFocusGainRequest);
    }

    private String focusLossToString() {
        return focusChangeToString(this.mFocusLossReceived);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dump(PrintWriter pw) {
        pw.println("  source:" + this.mSourceRef + " -- pack: " + this.mPackageName + " -- client: " + this.mClientId + " -- gain: " + focusGainToString() + " -- loss: " + focusLossToString() + " -- uid: " + this.mCallingUid + " -- stream: " + this.mStreamType);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void release() {
        try {
            if (this.mSourceRef != null && this.mDeathHandler != null) {
                this.mSourceRef.unlinkToDeath(this.mDeathHandler, 0);
                this.mDeathHandler = null;
            }
        } catch (NoSuchElementException e) {
            Log.e(TAG, "FocusRequester.release() hit ", e);
        }
    }

    protected void finalize() throws Throwable {
        release();
        super.finalize();
    }

    /* JADX WARN: Removed duplicated region for block: B:10:0x0068 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:12:0x006b A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:16:0x0090 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:18:0x0093 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:20:0x0096 A[RETURN] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private int focusLossForGainRequest(int r5) {
        /*
            r4 = this;
            r0 = r5
            switch(r0) {
                case 1: goto L20;
                case 2: goto L46;
                case 3: goto L6d;
                case 4: goto L46;
                default: goto L98;
            }
        L20:
            r0 = r4
            int r0 = r0.mFocusLossReceived
            switch(r0) {
                case -3: goto L44;
                case -2: goto L44;
                case -1: goto L44;
                case 0: goto L44;
                default: goto L46;
            }
        L44:
            r0 = -1
            return r0
        L46:
            r0 = r4
            int r0 = r0.mFocusLossReceived
            switch(r0) {
                case -3: goto L68;
                case -2: goto L68;
                case -1: goto L6b;
                case 0: goto L68;
                default: goto L6d;
            }
        L68:
            r0 = -2
            return r0
        L6b:
            r0 = -1
            return r0
        L6d:
            r0 = r4
            int r0 = r0.mFocusLossReceived
            switch(r0) {
                case -3: goto L90;
                case -2: goto L93;
                case -1: goto L96;
                case 0: goto L90;
                default: goto L98;
            }
        L90:
            r0 = -3
            return r0
        L93:
            r0 = -2
            return r0
        L96:
            r0 = -1
            return r0
        L98:
            java.lang.String r0 = "MediaFocusControl"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r2 = r1
            r2.<init>()
            java.lang.String r2 = "focusLossForGainRequest() for invalid focus request "
            java.lang.StringBuilder r1 = r1.append(r2)
            r2 = r5
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r1 = r1.toString()
            int r0 = android.util.Log.e(r0, r1)
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: android.media.FocusRequester.focusLossForGainRequest(int):int");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void handleExternalFocusGain(int focusGain) {
        int focusLoss = focusLossForGainRequest(focusGain);
        handleFocusLoss(focusLoss);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void handleFocusGain(int focusGain) {
        try {
            if (this.mFocusDispatcher != null) {
                this.mFocusDispatcher.dispatchAudioFocusChange(focusGain, this.mClientId);
            }
            this.mFocusLossReceived = 0;
        } catch (RemoteException e) {
            Log.e(TAG, "Failure to signal gain of audio focus due to: ", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void handleFocusLoss(int focusLoss) {
        try {
            if (focusLoss != this.mFocusLossReceived) {
                if (this.mFocusDispatcher != null) {
                    this.mFocusDispatcher.dispatchAudioFocusChange(focusLoss, this.mClientId);
                }
                this.mFocusLossReceived = focusLoss;
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Failure to signal loss of audio focus due to:", e);
        }
    }
}