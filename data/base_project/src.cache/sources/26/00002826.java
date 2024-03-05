package javax.sip.address;

import java.io.Serializable;

/* loaded from: URI.class */
public interface URI extends Cloneable, Serializable {
    String getScheme();

    boolean isSipURI();

    Object clone();

    String toString();
}