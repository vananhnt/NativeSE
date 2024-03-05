package com.android.server;

import android.Manifest;
import android.content.Context;
import android.location.Country;
import android.location.CountryListener;
import android.location.ICountryDetector;
import android.location.ICountryListener;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Slog;
import com.android.internal.os.BackgroundThread;
import com.android.server.location.ComprehensiveCountryDetector;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.HashMap;

/* loaded from: CountryDetectorService.class */
public class CountryDetectorService extends ICountryDetector.Stub implements Runnable {
    private static final String TAG = "CountryDetector";
    private static final boolean DEBUG = false;
    private final HashMap<IBinder, Receiver> mReceivers = new HashMap<>();
    private final Context mContext;
    private ComprehensiveCountryDetector mCountryDetector;
    private boolean mSystemReady;
    private Handler mHandler;
    private CountryListener mLocationBasedDetectorListener;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: CountryDetectorService$Receiver.class */
    public final class Receiver implements IBinder.DeathRecipient {
        private final ICountryListener mListener;
        private final IBinder mKey;

        public Receiver(ICountryListener listener) {
            this.mListener = listener;
            this.mKey = listener.asBinder();
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            CountryDetectorService.this.removeListener(this.mKey);
        }

        public boolean equals(Object otherObj) {
            if (otherObj instanceof Receiver) {
                return this.mKey.equals(((Receiver) otherObj).mKey);
            }
            return false;
        }

        public int hashCode() {
            return this.mKey.hashCode();
        }

        public ICountryListener getListener() {
            return this.mListener;
        }
    }

    public CountryDetectorService(Context context) {
        this.mContext = context;
    }

    @Override // android.location.ICountryDetector
    public Country detectCountry() {
        if (!this.mSystemReady) {
            return null;
        }
        return this.mCountryDetector.detectCountry();
    }

    @Override // android.location.ICountryDetector
    public void addCountryListener(ICountryListener listener) throws RemoteException {
        if (!this.mSystemReady) {
            throw new RemoteException();
        }
        addListener(listener);
    }

    @Override // android.location.ICountryDetector
    public void removeCountryListener(ICountryListener listener) throws RemoteException {
        if (!this.mSystemReady) {
            throw new RemoteException();
        }
        removeListener(listener.asBinder());
    }

    private void addListener(ICountryListener listener) {
        synchronized (this.mReceivers) {
            Receiver r = new Receiver(listener);
            try {
                listener.asBinder().linkToDeath(r, 0);
                this.mReceivers.put(listener.asBinder(), r);
                if (this.mReceivers.size() == 1) {
                    Slog.d(TAG, "The first listener is added");
                    setCountryListener(this.mLocationBasedDetectorListener);
                }
            } catch (RemoteException e) {
                Slog.e(TAG, "linkToDeath failed:", e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeListener(IBinder key) {
        synchronized (this.mReceivers) {
            this.mReceivers.remove(key);
            if (this.mReceivers.isEmpty()) {
                setCountryListener(null);
                Slog.d(TAG, "No listener is left");
            }
        }
    }

    protected void notifyReceivers(Country country) {
        synchronized (this.mReceivers) {
            for (Receiver receiver : this.mReceivers.values()) {
                try {
                    receiver.getListener().onCountryDetected(country);
                } catch (RemoteException e) {
                    Slog.e(TAG, "notifyReceivers failed:", e);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void systemRunning() {
        BackgroundThread.getHandler().post(this);
    }

    private void initialize() {
        this.mCountryDetector = new ComprehensiveCountryDetector(this.mContext);
        this.mLocationBasedDetectorListener = new CountryListener() { // from class: com.android.server.CountryDetectorService.1
            @Override // android.location.CountryListener
            public void onCountryDetected(final Country country) {
                CountryDetectorService.this.mHandler.post(new Runnable() { // from class: com.android.server.CountryDetectorService.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        CountryDetectorService.this.notifyReceivers(country);
                    }
                });
            }
        };
    }

    @Override // java.lang.Runnable
    public void run() {
        this.mHandler = new Handler();
        initialize();
        this.mSystemReady = true;
    }

    protected void setCountryListener(final CountryListener listener) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.CountryDetectorService.2
            @Override // java.lang.Runnable
            public void run() {
                CountryDetectorService.this.mCountryDetector.setCountryListener(listener);
            }
        });
    }

    boolean isSystemReady() {
        return this.mSystemReady;
    }

    @Override // android.os.Binder
    protected void dump(FileDescriptor fd, PrintWriter fout, String[] args) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.DUMP, TAG);
    }
}