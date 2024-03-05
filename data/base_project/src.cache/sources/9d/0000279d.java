package javax.crypto.interfaces;

import javax.crypto.SecretKey;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: PBEKey.class */
public interface PBEKey extends SecretKey {
    public static final long serialVersionUID = -1430015993304333921L;

    int getIterationCount();

    byte[] getSalt();

    char[] getPassword();
}