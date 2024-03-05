package android.content;

import android.content.IClipboard;
import android.content.IOnPrimaryClipChangedListener;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import java.util.ArrayList;

/* loaded from: ClipboardManager.class */
public class ClipboardManager extends android.text.ClipboardManager {
    private static final Object sStaticLock = new Object();
    private static IClipboard sService;
    private final Context mContext;
    static final int MSG_REPORT_PRIMARY_CLIP_CHANGED = 1;
    private final ArrayList<OnPrimaryClipChangedListener> mPrimaryClipChangedListeners = new ArrayList<>();
    private final IOnPrimaryClipChangedListener.Stub mPrimaryClipChangedServiceListener = new IOnPrimaryClipChangedListener.Stub() { // from class: android.content.ClipboardManager.1
        @Override // android.content.IOnPrimaryClipChangedListener
        public void dispatchPrimaryClipChanged() {
            ClipboardManager.this.mHandler.sendEmptyMessage(1);
        }
    };
    private final Handler mHandler = new Handler() { // from class: android.content.ClipboardManager.2
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    ClipboardManager.this.reportPrimaryClipChanged();
                    return;
                default:
                    return;
            }
        }
    };

    /* loaded from: ClipboardManager$OnPrimaryClipChangedListener.class */
    public interface OnPrimaryClipChangedListener {
        void onPrimaryClipChanged();
    }

    private static IClipboard getService() {
        synchronized (sStaticLock) {
            if (sService != null) {
                return sService;
            }
            IBinder b = ServiceManager.getService(Context.CLIPBOARD_SERVICE);
            sService = IClipboard.Stub.asInterface(b);
            return sService;
        }
    }

    public ClipboardManager(Context context, Handler handler) {
        this.mContext = context;
    }

    public void setPrimaryClip(ClipData clip) {
        if (clip != null) {
            try {
                clip.prepareToLeaveProcess();
            } catch (RemoteException e) {
                return;
            }
        }
        getService().setPrimaryClip(clip, this.mContext.getOpPackageName());
    }

    public ClipData getPrimaryClip() {
        try {
            return getService().getPrimaryClip(this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            return null;
        }
    }

    public ClipDescription getPrimaryClipDescription() {
        try {
            return getService().getPrimaryClipDescription(this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            return null;
        }
    }

    public boolean hasPrimaryClip() {
        try {
            return getService().hasPrimaryClip(this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            return false;
        }
    }

    public void addPrimaryClipChangedListener(OnPrimaryClipChangedListener what) {
        synchronized (this.mPrimaryClipChangedListeners) {
            if (this.mPrimaryClipChangedListeners.size() == 0) {
                try {
                    getService().addPrimaryClipChangedListener(this.mPrimaryClipChangedServiceListener, this.mContext.getOpPackageName());
                } catch (RemoteException e) {
                }
            }
            this.mPrimaryClipChangedListeners.add(what);
        }
    }

    public void removePrimaryClipChangedListener(OnPrimaryClipChangedListener what) {
        synchronized (this.mPrimaryClipChangedListeners) {
            this.mPrimaryClipChangedListeners.remove(what);
            if (this.mPrimaryClipChangedListeners.size() == 0) {
                try {
                    getService().removePrimaryClipChangedListener(this.mPrimaryClipChangedServiceListener);
                } catch (RemoteException e) {
                }
            }
        }
    }

    @Override // android.text.ClipboardManager
    public CharSequence getText() {
        ClipData clip = getPrimaryClip();
        if (clip != null && clip.getItemCount() > 0) {
            return clip.getItemAt(0).coerceToText(this.mContext);
        }
        return null;
    }

    @Override // android.text.ClipboardManager
    public void setText(CharSequence text) {
        setPrimaryClip(ClipData.newPlainText(null, text));
    }

    @Override // android.text.ClipboardManager
    public boolean hasText() {
        try {
            return getService().hasClipboardText(this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            return false;
        }
    }

    void reportPrimaryClipChanged() {
        synchronized (this.mPrimaryClipChangedListeners) {
            int N = this.mPrimaryClipChangedListeners.size();
            if (N <= 0) {
                return;
            }
            Object[] listeners = this.mPrimaryClipChangedListeners.toArray();
            for (Object obj : listeners) {
                ((OnPrimaryClipChangedListener) obj).onPrimaryClipChanged();
            }
        }
    }
}