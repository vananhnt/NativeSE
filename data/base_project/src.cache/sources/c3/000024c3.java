package java.security.interfaces;

import java.security.InvalidParameterException;
import java.security.SecureRandom;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: DSAKeyPairGenerator.class */
public interface DSAKeyPairGenerator {
    void initialize(DSAParams dSAParams, SecureRandom secureRandom) throws InvalidParameterException;

    void initialize(int i, boolean z, SecureRandom secureRandom) throws InvalidParameterException;
}