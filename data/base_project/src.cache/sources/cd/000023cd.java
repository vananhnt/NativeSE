package java.nio;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: FloatBuffer.class */
public abstract class FloatBuffer extends Buffer implements Comparable<FloatBuffer> {
    public abstract FloatBuffer asReadOnlyBuffer();

    public abstract FloatBuffer compact();

    public abstract FloatBuffer duplicate();

    public abstract float get();

    public abstract float get(int i);

    @Override // java.nio.Buffer
    public abstract boolean isDirect();

    public abstract ByteOrder order();

    public abstract FloatBuffer put(float f);

    public abstract FloatBuffer put(int i, float f);

    public abstract FloatBuffer slice();

    FloatBuffer() {
        throw new RuntimeException("Stub!");
    }

    public static FloatBuffer allocate(int capacity) {
        throw new RuntimeException("Stub!");
    }

    public static FloatBuffer wrap(float[] array) {
        throw new RuntimeException("Stub!");
    }

    public static FloatBuffer wrap(float[] array, int start, int floatCount) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.nio.Buffer
    public final float[] array() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.nio.Buffer
    public final int arrayOffset() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Comparable
    public int compareTo(FloatBuffer otherBuffer) {
        throw new RuntimeException("Stub!");
    }

    public boolean equals(Object other) {
        throw new RuntimeException("Stub!");
    }

    public FloatBuffer get(float[] dst) {
        throw new RuntimeException("Stub!");
    }

    public FloatBuffer get(float[] dst, int dstOffset, int floatCount) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.nio.Buffer
    public final boolean hasArray() {
        throw new RuntimeException("Stub!");
    }

    public int hashCode() {
        throw new RuntimeException("Stub!");
    }

    public final FloatBuffer put(float[] src) {
        throw new RuntimeException("Stub!");
    }

    public FloatBuffer put(float[] src, int srcOffset, int floatCount) {
        throw new RuntimeException("Stub!");
    }

    public FloatBuffer put(FloatBuffer src) {
        throw new RuntimeException("Stub!");
    }
}