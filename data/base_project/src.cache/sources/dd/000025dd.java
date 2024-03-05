package java.util;

import java.io.Serializable;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: IllegalFormatConversionException.class */
public class IllegalFormatConversionException extends IllegalFormatException implements Serializable {
    public IllegalFormatConversionException(char c, Class<?> arg) {
        throw new RuntimeException("Stub!");
    }

    public Class<?> getArgumentClass() {
        throw new RuntimeException("Stub!");
    }

    public char getConversion() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        throw new RuntimeException("Stub!");
    }
}