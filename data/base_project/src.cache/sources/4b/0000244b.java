package java.security;

import java.io.Serializable;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Key.class */
public interface Key extends Serializable {
    public static final long serialVersionUID = 6603384152749567654L;

    String getAlgorithm();

    String getFormat();

    byte[] getEncoded();
}