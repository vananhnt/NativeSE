package android.database;

import android.database.IContentObserver;
import android.net.Uri;
import android.os.Handler;

/* loaded from: ContentObserver.class */
public abstract class ContentObserver {
    private final Object mLock = new Object();
    private Transport mTransport;
    Handler mHandler;

    public ContentObserver(Handler handler) {
        this.mHandler = handler;
    }

    public IContentObserver getContentObserver() {
        Transport transport;
        synchronized (this.mLock) {
            if (this.mTransport == null) {
                this.mTransport = new Transport(this);
            }
            transport = this.mTransport;
        }
        return transport;
    }

    public IContentObserver releaseContentObserver() {
        Transport oldTransport;
        synchronized (this.mLock) {
            oldTransport = this.mTransport;
            if (oldTransport != null) {
                oldTransport.releaseContentObserver();
                this.mTransport = null;
            }
        }
        return oldTransport;
    }

    public boolean deliverSelfNotifications() {
        return false;
    }

    public void onChange(boolean selfChange) {
    }

    public void onChange(boolean selfChange, Uri uri) {
        onChange(selfChange);
    }

    @Deprecated
    public final void dispatchChange(boolean selfChange) {
        dispatchChange(selfChange, null);
    }

    public final void dispatchChange(boolean selfChange, Uri uri) {
        if (this.mHandler == null) {
            onChange(selfChange, uri);
        } else {
            this.mHandler.post(new NotificationRunnable(selfChange, uri));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ContentObserver$NotificationRunnable.class */
    public final class NotificationRunnable implements Runnable {
        private final boolean mSelfChange;
        private final Uri mUri;

        public NotificationRunnable(boolean selfChange, Uri uri) {
            this.mSelfChange = selfChange;
            this.mUri = uri;
        }

        @Override // java.lang.Runnable
        public void run() {
            ContentObserver.this.onChange(this.mSelfChange, this.mUri);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ContentObserver$Transport.class */
    public static final class Transport extends IContentObserver.Stub {
        private ContentObserver mContentObserver;

        public Transport(ContentObserver contentObserver) {
            this.mContentObserver = contentObserver;
        }

        @Override // android.database.IContentObserver
        public void onChange(boolean selfChange, Uri uri) {
            ContentObserver contentObserver = this.mContentObserver;
            if (contentObserver != null) {
                contentObserver.dispatchChange(selfChange, uri);
            }
        }

        public void releaseContentObserver() {
            this.mContentObserver = null;
        }
    }
}