package org.apache.harmony.dalvik.ddmc;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/* loaded from: ChunkHandler.class */
public abstract class ChunkHandler {
    public static final ByteOrder CHUNK_ORDER = ByteOrder.BIG_ENDIAN;
    public static final int CHUNK_FAIL = type("FAIL");

    public abstract void connected();

    public abstract void disconnected();

    public abstract Chunk handleChunk(Chunk chunk);

    public static Chunk createFailChunk(int errorCode, String msg) {
        if (msg == null) {
            msg = "";
        }
        ByteBuffer out = ByteBuffer.allocate(8 + (msg.length() * 2));
        out.order(CHUNK_ORDER);
        out.putInt(errorCode);
        out.putInt(msg.length());
        putString(out, msg);
        return new Chunk(CHUNK_FAIL, out);
    }

    public static ByteBuffer wrapChunk(Chunk request) {
        ByteBuffer in = ByteBuffer.wrap(request.data, request.offset, request.length);
        in.order(CHUNK_ORDER);
        return in;
    }

    public static String getString(ByteBuffer buf, int len) {
        char[] data = new char[len];
        for (int i = 0; i < len; i++) {
            data[i] = buf.getChar();
        }
        return new String(data);
    }

    public static void putString(ByteBuffer buf, String str) {
        int len = str.length();
        for (int i = 0; i < len; i++) {
            buf.putChar(str.charAt(i));
        }
    }

    public static int type(String typeName) {
        if (typeName.length() != 4) {
            throw new IllegalArgumentException("Bad type name: " + typeName);
        }
        int result = 0;
        for (int i = 0; i < 4; i++) {
            result = (result << 8) | (typeName.charAt(i) & 255);
        }
        return result;
    }

    public static String name(int type) {
        char[] ascii = {(char) ((type >> 24) & 255), (char) ((type >> 16) & 255), (char) ((type >> 8) & 255), (char) (type & 255)};
        return new String(ascii);
    }
}