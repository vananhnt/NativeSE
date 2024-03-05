package android.media;

import java.nio.ByteBuffer;

/* loaded from: Image.class */
public abstract class Image implements AutoCloseable {

    /* loaded from: Image$Plane.class */
    public static abstract class Plane {
        public abstract int getRowStride();

        public abstract int getPixelStride();

        public abstract ByteBuffer getBuffer();
    }

    public abstract int getFormat();

    public abstract int getWidth();

    public abstract int getHeight();

    public abstract long getTimestamp();

    public abstract Plane[] getPlanes();

    @Override // java.lang.AutoCloseable
    public abstract void close();
}