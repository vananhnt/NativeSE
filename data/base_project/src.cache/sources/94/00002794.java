package javax.crypto;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.SecureRandom;
import org.apache.harmony.crypto.internal.NullCipherSpi;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: NullCipher.class */
public class NullCipher extends Cipher {
    public NullCipher() {
        super(new NullCipherSpi(), null, null);
        try {
            init(1, (Key) null, (SecureRandom) null);
        } catch (InvalidKeyException e) {
        }
    }
}