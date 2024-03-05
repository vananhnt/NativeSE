package java.security;

import java.io.IOException;
import java.io.Serializable;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: SignedObject.class */
public final class SignedObject implements Serializable {
    public SignedObject(Serializable object, PrivateKey signingKey, Signature signingEngine) throws IOException, InvalidKeyException, SignatureException {
        throw new RuntimeException("Stub!");
    }

    public Object getObject() throws IOException, ClassNotFoundException {
        throw new RuntimeException("Stub!");
    }

    public byte[] getSignature() {
        throw new RuntimeException("Stub!");
    }

    public String getAlgorithm() {
        throw new RuntimeException("Stub!");
    }

    public boolean verify(PublicKey verificationKey, Signature verificationEngine) throws InvalidKeyException, SignatureException {
        throw new RuntimeException("Stub!");
    }
}