package android.security;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.security.IKeyChainAliasCallback;
import android.security.IKeyChainService;
import com.android.org.conscrypt.OpenSSLEngine;
import com.android.org.conscrypt.TrustedCertificateStore;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.security.InvalidKeyException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/* loaded from: KeyChain.class */
public final class KeyChain {
    private static final String TAG = "KeyChain";
    public static final String ACCOUNT_TYPE = "com.android.keychain";
    private static final String ACTION_CHOOSER = "com.android.keychain.CHOOSER";
    public static final String EXTRA_RESPONSE = "response";
    public static final String EXTRA_HOST = "host";
    public static final String EXTRA_PORT = "port";
    public static final String EXTRA_ALIAS = "alias";
    public static final String EXTRA_SENDER = "sender";
    private static final String ACTION_INSTALL = "android.credentials.INSTALL";
    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_CERTIFICATE = "CERT";
    public static final String EXTRA_PKCS12 = "PKCS12";
    public static final String ACTION_STORAGE_CHANGED = "android.security.STORAGE_CHANGED";

    public static Intent createInstallIntent() {
        Intent intent = new Intent("android.credentials.INSTALL");
        intent.setClassName("com.android.certinstaller", "com.android.certinstaller.CertInstallerMain");
        return intent;
    }

    public static void choosePrivateKeyAlias(Activity activity, KeyChainAliasCallback response, String[] keyTypes, Principal[] issuers, String host, int port, String alias) {
        if (activity == null) {
            throw new NullPointerException("activity == null");
        }
        if (response == null) {
            throw new NullPointerException("response == null");
        }
        Intent intent = new Intent(ACTION_CHOOSER);
        intent.putExtra("response", new AliasResponse(response));
        intent.putExtra(EXTRA_HOST, host);
        intent.putExtra("port", port);
        intent.putExtra(EXTRA_ALIAS, alias);
        intent.putExtra(EXTRA_SENDER, PendingIntent.getActivity(activity, 0, new Intent(), 0));
        activity.startActivity(intent);
    }

    /* loaded from: KeyChain$AliasResponse.class */
    private static class AliasResponse extends IKeyChainAliasCallback.Stub {
        private final KeyChainAliasCallback keyChainAliasResponse;

        private AliasResponse(KeyChainAliasCallback keyChainAliasResponse) {
            this.keyChainAliasResponse = keyChainAliasResponse;
        }

        @Override // android.security.IKeyChainAliasCallback
        public void alias(String alias) {
            this.keyChainAliasResponse.alias(alias);
        }
    }

    public static PrivateKey getPrivateKey(Context context, String alias) throws KeyChainException, InterruptedException {
        if (alias == null) {
            throw new NullPointerException("alias == null");
        }
        KeyChainConnection keyChainConnection = bind(context);
        try {
            try {
                try {
                    try {
                        IKeyChainService keyChainService = keyChainConnection.getService();
                        String keyId = keyChainService.requestPrivateKey(alias);
                        if (keyId == null) {
                            throw new KeyChainException("keystore had a problem");
                        }
                        OpenSSLEngine engine = OpenSSLEngine.getInstance("keystore");
                        PrivateKey privateKeyById = engine.getPrivateKeyById(keyId);
                        keyChainConnection.close();
                        return privateKeyById;
                    } catch (RemoteException e) {
                        throw new KeyChainException(e);
                    }
                } catch (InvalidKeyException e2) {
                    throw new KeyChainException(e2);
                }
            } catch (RuntimeException e3) {
                throw new KeyChainException(e3);
            }
        } catch (Throwable th) {
            keyChainConnection.close();
            throw th;
        }
    }

    public static X509Certificate[] getCertificateChain(Context context, String alias) throws KeyChainException, InterruptedException {
        if (alias == null) {
            throw new NullPointerException("alias == null");
        }
        KeyChainConnection keyChainConnection = bind(context);
        try {
            try {
                try {
                    try {
                        IKeyChainService keyChainService = keyChainConnection.getService();
                        byte[] certificateBytes = keyChainService.getCertificate(alias);
                        if (certificateBytes == null) {
                            return null;
                        }
                        TrustedCertificateStore store = new TrustedCertificateStore();
                        List<X509Certificate> chain = store.getCertificateChain(toCertificate(certificateBytes));
                        X509Certificate[] x509CertificateArr = (X509Certificate[]) chain.toArray(new X509Certificate[chain.size()]);
                        keyChainConnection.close();
                        return x509CertificateArr;
                    } catch (CertificateException e) {
                        throw new KeyChainException(e);
                    }
                } catch (RemoteException e2) {
                    throw new KeyChainException(e2);
                }
            } catch (RuntimeException e3) {
                throw new KeyChainException(e3);
            }
        } finally {
            keyChainConnection.close();
        }
    }

    public static boolean isKeyAlgorithmSupported(String algorithm) {
        String algUpper = algorithm.toUpperCase(Locale.US);
        return "DSA".equals(algUpper) || "EC".equals(algUpper) || "RSA".equals(algUpper);
    }

    public static boolean isBoundKeyAlgorithm(String algorithm) {
        if (!isKeyAlgorithmSupported(algorithm)) {
            return false;
        }
        return KeyStore.getInstance().isHardwareBacked(algorithm);
    }

    private static X509Certificate toCertificate(byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("bytes == null");
        }
        try {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            Certificate cert = certFactory.generateCertificate(new ByteArrayInputStream(bytes));
            return (X509Certificate) cert;
        } catch (CertificateException e) {
            throw new AssertionError(e);
        }
    }

    /* loaded from: KeyChain$KeyChainConnection.class */
    public static final class KeyChainConnection implements Closeable {
        private final Context context;
        private final ServiceConnection serviceConnection;
        private final IKeyChainService service;

        private KeyChainConnection(Context context, ServiceConnection serviceConnection, IKeyChainService service) {
            this.context = context;
            this.serviceConnection = serviceConnection;
            this.service = service;
        }

        @Override // java.io.Closeable
        public void close() {
            this.context.unbindService(this.serviceConnection);
        }

        public IKeyChainService getService() {
            return this.service;
        }
    }

    public static KeyChainConnection bind(Context context) throws InterruptedException {
        if (context == null) {
            throw new NullPointerException("context == null");
        }
        ensureNotOnMainThread(context);
        final BlockingQueue<IKeyChainService> q = new LinkedBlockingQueue<>(1);
        ServiceConnection keyChainServiceConnection = new ServiceConnection() { // from class: android.security.KeyChain.1
            volatile boolean mConnectedAtLeastOnce = false;

            @Override // android.content.ServiceConnection
            public void onServiceConnected(ComponentName name, IBinder service) {
                if (!this.mConnectedAtLeastOnce) {
                    this.mConnectedAtLeastOnce = true;
                    try {
                        BlockingQueue.this.put(IKeyChainService.Stub.asInterface(service));
                    } catch (InterruptedException e) {
                    }
                }
            }

            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName name) {
            }
        };
        Intent intent = new Intent(IKeyChainService.class.getName());
        ComponentName comp = intent.resolveSystemService(context.getPackageManager(), 0);
        intent.setComponent(comp);
        boolean isBound = context.bindService(intent, keyChainServiceConnection, 1);
        if (!isBound) {
            throw new AssertionError("could not bind to KeyChainService");
        }
        return new KeyChainConnection(context, keyChainServiceConnection, q.take());
    }

    private static void ensureNotOnMainThread(Context context) {
        Looper looper = Looper.myLooper();
        if (looper != null && looper == context.getMainLooper()) {
            throw new IllegalStateException("calling this from your main thread can lead to deadlock");
        }
    }
}