package java.security;

import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: KeyFactory.class */
public class KeyFactory {
    protected KeyFactory(KeyFactorySpi keyFacSpi, Provider provider, String algorithm) {
        throw new RuntimeException("Stub!");
    }

    public static KeyFactory getInstance(String algorithm) throws NoSuchAlgorithmException {
        throw new RuntimeException("Stub!");
    }

    public static KeyFactory getInstance(String algorithm, String provider) throws NoSuchAlgorithmException, NoSuchProviderException {
        throw new RuntimeException("Stub!");
    }

    public static KeyFactory getInstance(String algorithm, Provider provider) throws NoSuchAlgorithmException {
        throw new RuntimeException("Stub!");
    }

    public final Provider getProvider() {
        throw new RuntimeException("Stub!");
    }

    public final String getAlgorithm() {
        throw new RuntimeException("Stub!");
    }

    public final PublicKey generatePublic(KeySpec keySpec) throws InvalidKeySpecException {
        throw new RuntimeException("Stub!");
    }

    public final PrivateKey generatePrivate(KeySpec keySpec) throws InvalidKeySpecException {
        throw new RuntimeException("Stub!");
    }

    public final <T extends KeySpec> T getKeySpec(Key key, Class<T> keySpec) throws InvalidKeySpecException {
        throw new RuntimeException("Stub!");
    }

    public final Key translateKey(Key key) throws InvalidKeyException {
        throw new RuntimeException("Stub!");
    }
}