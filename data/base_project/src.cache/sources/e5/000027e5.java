package javax.net.ssl;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: TrustManagerFactorySpi.class */
public abstract class TrustManagerFactorySpi {
    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void engineInit(KeyStore keyStore) throws KeyStoreException;

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void engineInit(ManagerFactoryParameters managerFactoryParameters) throws InvalidAlgorithmParameterException;

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract TrustManager[] engineGetTrustManagers();
}