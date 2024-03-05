package java.nio.charset;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: CoderResult.class */
public class CoderResult {
    public static final CoderResult UNDERFLOW = null;
    public static final CoderResult OVERFLOW = null;

    CoderResult() {
        throw new RuntimeException("Stub!");
    }

    public static synchronized CoderResult malformedForLength(int length) throws IllegalArgumentException {
        throw new RuntimeException("Stub!");
    }

    public static synchronized CoderResult unmappableForLength(int length) throws IllegalArgumentException {
        throw new RuntimeException("Stub!");
    }

    public boolean isUnderflow() {
        throw new RuntimeException("Stub!");
    }

    public boolean isError() {
        throw new RuntimeException("Stub!");
    }

    public boolean isMalformed() {
        throw new RuntimeException("Stub!");
    }

    public boolean isOverflow() {
        throw new RuntimeException("Stub!");
    }

    public boolean isUnmappable() {
        throw new RuntimeException("Stub!");
    }

    public int length() throws UnsupportedOperationException {
        throw new RuntimeException("Stub!");
    }

    public void throwException() throws BufferUnderflowException, BufferOverflowException, UnmappableCharacterException, MalformedInputException, CharacterCodingException {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }
}