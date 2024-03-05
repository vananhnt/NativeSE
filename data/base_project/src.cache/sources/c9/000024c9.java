package java.security.interfaces;

import java.security.PublicKey;
import java.security.spec.ECPoint;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ECPublicKey.class */
public interface ECPublicKey extends PublicKey, ECKey {
    public static final long serialVersionUID = -3314988629879632826L;

    ECPoint getW();
}