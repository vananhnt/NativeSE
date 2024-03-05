package org.w3c.dom.ls;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: LSException.class */
public class LSException extends RuntimeException {
    public short code;
    public static final short PARSE_ERR = 81;
    public static final short SERIALIZE_ERR = 82;

    public LSException(short code, String message) {
        throw new RuntimeException("Stub!");
    }
}