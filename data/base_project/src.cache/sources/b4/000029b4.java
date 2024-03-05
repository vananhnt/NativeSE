package org.apache.harmony.security.provider.crypto;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactorySpi;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.DSAPrivateKeySpec;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/* loaded from: DSAKeyFactoryImpl.class */
public class DSAKeyFactoryImpl extends KeyFactorySpi {
    @Override // java.security.KeyFactorySpi
    protected PrivateKey engineGeneratePrivate(KeySpec keySpec) throws InvalidKeySpecException {
        if (keySpec != null) {
            if (keySpec instanceof DSAPrivateKeySpec) {
                return new DSAPrivateKeyImpl((DSAPrivateKeySpec) keySpec);
            }
            if (keySpec instanceof PKCS8EncodedKeySpec) {
                return new DSAPrivateKeyImpl((PKCS8EncodedKeySpec) keySpec);
            }
        }
        throw new InvalidKeySpecException("'keySpec' is neither DSAPrivateKeySpec nor PKCS8EncodedKeySpec");
    }

    @Override // java.security.KeyFactorySpi
    protected PublicKey engineGeneratePublic(KeySpec keySpec) throws InvalidKeySpecException {
        if (keySpec != null) {
            if (keySpec instanceof DSAPublicKeySpec) {
                return new DSAPublicKeyImpl((DSAPublicKeySpec) keySpec);
            }
            if (keySpec instanceof X509EncodedKeySpec) {
                return new DSAPublicKeyImpl((X509EncodedKeySpec) keySpec);
            }
        }
        throw new InvalidKeySpecException("'keySpec' is neither DSAPublicKeySpec nor X509EncodedKeySpec");
    }

    @Override // java.security.KeyFactorySpi
    protected <T extends KeySpec> T engineGetKeySpec(Key key, Class<T> keySpec) throws InvalidKeySpecException {
        if (key != null) {
            if (keySpec == null) {
                throw new NullPointerException("keySpec == null");
            }
            if (key instanceof DSAPrivateKey) {
                DSAPrivateKey privateKey = (DSAPrivateKey) key;
                if (keySpec.equals(DSAPrivateKeySpec.class)) {
                    BigInteger x = privateKey.getX();
                    DSAParams params = privateKey.getParams();
                    BigInteger p = params.getP();
                    BigInteger q = params.getQ();
                    BigInteger g = params.getG();
                    return new DSAPrivateKeySpec(x, p, q, g);
                } else if (keySpec.equals(PKCS8EncodedKeySpec.class)) {
                    return new PKCS8EncodedKeySpec(key.getEncoded());
                } else {
                    throw new InvalidKeySpecException("'keySpec' is neither DSAPrivateKeySpec nor PKCS8EncodedKeySpec");
                }
            } else if (key instanceof DSAPublicKey) {
                DSAPublicKey publicKey = (DSAPublicKey) key;
                if (keySpec.equals(DSAPublicKeySpec.class)) {
                    BigInteger y = publicKey.getY();
                    DSAParams params2 = publicKey.getParams();
                    BigInteger p2 = params2.getP();
                    BigInteger q2 = params2.getQ();
                    BigInteger g2 = params2.getG();
                    return new DSAPublicKeySpec(y, p2, q2, g2);
                } else if (keySpec.equals(X509EncodedKeySpec.class)) {
                    return new X509EncodedKeySpec(key.getEncoded());
                } else {
                    throw new InvalidKeySpecException("'keySpec' is neither DSAPublicKeySpec nor X509EncodedKeySpec");
                }
            }
        }
        throw new InvalidKeySpecException("'key' is neither DSAPublicKey nor DSAPrivateKey");
    }

    @Override // java.security.KeyFactorySpi
    protected Key engineTranslateKey(Key key) throws InvalidKeyException {
        if (key != null) {
            if (key instanceof DSAPrivateKey) {
                DSAPrivateKey privateKey = (DSAPrivateKey) key;
                DSAParams params = privateKey.getParams();
                try {
                    return engineGeneratePrivate(new DSAPrivateKeySpec(privateKey.getX(), params.getP(), params.getQ(), params.getG()));
                } catch (InvalidKeySpecException e) {
                    throw new InvalidKeyException("ATTENTION: InvalidKeySpecException: " + e);
                }
            } else if (key instanceof DSAPublicKey) {
                DSAPublicKey publicKey = (DSAPublicKey) key;
                DSAParams params2 = publicKey.getParams();
                try {
                    return engineGeneratePublic(new DSAPublicKeySpec(publicKey.getY(), params2.getP(), params2.getQ(), params2.getG()));
                } catch (InvalidKeySpecException e2) {
                    throw new InvalidKeyException("ATTENTION: InvalidKeySpecException: " + e2);
                }
            }
        }
        throw new InvalidKeyException("'key' is neither DSAPublicKey nor DSAPrivateKey");
    }
}