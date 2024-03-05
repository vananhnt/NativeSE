package javax.net.ssl;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: KeyManagerFactorySpi.class */
public abstract class KeyManagerFactorySpi {
    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void engineInit(KeyStore keyStore, char[] cArr) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException;

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void engineInit(ManagerFactoryParameters managerFactoryParameters) throws InvalidAlgorithmParameterException;

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract KeyManager[] engineGetKeyManagers();
}