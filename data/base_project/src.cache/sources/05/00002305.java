package java.lang;

import java.io.Serializable;

/* loaded from: Number.class */
public abstract class Number implements Serializable {
    public abstract double doubleValue();

    public abstract float floatValue();

    public abstract int intValue();

    public abstract long longValue();

    public Number() {
        throw new RuntimeException("Stub!");
    }

    public byte byteValue() {
        throw new RuntimeException("Stub!");
    }

    public short shortValue() {
        throw new RuntimeException("Stub!");
    }
}