package javax.sip.header;

import java.io.Serializable;

/* loaded from: Header.class */
public interface Header extends Cloneable, Serializable {
    String getName();

    Object clone();

    boolean equals(Object obj);

    int hashCode();

    String toString();
}