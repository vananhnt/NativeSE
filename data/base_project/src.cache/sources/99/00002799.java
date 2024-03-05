package javax.crypto;

import java.security.GeneralSecurityException;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ShortBufferException.class */
public class ShortBufferException extends GeneralSecurityException {
    private static final long serialVersionUID = 8427718640832943747L;

    public ShortBufferException(String msg) {
        super(msg);
    }

    public ShortBufferException() {
    }
}