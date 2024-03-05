package org.apache.harmony.dalvik.ddmc;

import java.nio.ByteBuffer;

/* loaded from: Chunk.class */
public class Chunk {
    public int type;
    public byte[] data;
    public int offset;
    public int length;

    public Chunk() {
    }

    public Chunk(int type, byte[] data, int offset, int length) {
        this.type = type;
        this.data = data;
        this.offset = offset;
        this.length = length;
    }

    public Chunk(int type, ByteBuffer buf) {
        this.type = type;
        this.data = buf.array();
        this.offset = buf.arrayOffset();
        this.length = buf.position();
    }
}