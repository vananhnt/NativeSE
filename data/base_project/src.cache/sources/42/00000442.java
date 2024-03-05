package android.ddm;

import java.nio.ByteBuffer;
import org.apache.harmony.dalvik.ddmc.Chunk;
import org.apache.harmony.dalvik.ddmc.ChunkHandler;
import org.apache.harmony.dalvik.ddmc.DdmServer;

/* loaded from: DdmHandleExit.class */
public class DdmHandleExit extends ChunkHandler {
    public static final int CHUNK_EXIT = type("EXIT");
    private static DdmHandleExit mInstance = new DdmHandleExit();

    private DdmHandleExit() {
    }

    public static void register() {
        DdmServer.registerHandler(CHUNK_EXIT, mInstance);
    }

    @Override // org.apache.harmony.dalvik.ddmc.ChunkHandler
    public void connected() {
    }

    @Override // org.apache.harmony.dalvik.ddmc.ChunkHandler
    public void disconnected() {
    }

    @Override // org.apache.harmony.dalvik.ddmc.ChunkHandler
    public Chunk handleChunk(Chunk request) {
        ByteBuffer in = wrapChunk(request);
        int statusCode = in.getInt();
        Runtime.getRuntime().halt(statusCode);
        return null;
    }
}