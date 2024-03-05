package android.ddm;

import java.nio.ByteBuffer;
import org.apache.harmony.dalvik.ddmc.Chunk;
import org.apache.harmony.dalvik.ddmc.ChunkHandler;
import org.apache.harmony.dalvik.ddmc.DdmServer;
import org.apache.harmony.dalvik.ddmc.DdmVmInternal;

/* loaded from: DdmHandleThread.class */
public class DdmHandleThread extends ChunkHandler {
    public static final int CHUNK_THEN = type("THEN");
    public static final int CHUNK_THCR = type("THCR");
    public static final int CHUNK_THDE = type("THDE");
    public static final int CHUNK_THST = type("THST");
    public static final int CHUNK_STKL = type("STKL");
    private static DdmHandleThread mInstance = new DdmHandleThread();

    private DdmHandleThread() {
    }

    public static void register() {
        DdmServer.registerHandler(CHUNK_THEN, mInstance);
        DdmServer.registerHandler(CHUNK_THST, mInstance);
        DdmServer.registerHandler(CHUNK_STKL, mInstance);
    }

    @Override // org.apache.harmony.dalvik.ddmc.ChunkHandler
    public void connected() {
    }

    @Override // org.apache.harmony.dalvik.ddmc.ChunkHandler
    public void disconnected() {
    }

    @Override // org.apache.harmony.dalvik.ddmc.ChunkHandler
    public Chunk handleChunk(Chunk request) {
        int type = request.type;
        if (type == CHUNK_THEN) {
            return handleTHEN(request);
        }
        if (type == CHUNK_THST) {
            return handleTHST(request);
        }
        if (type == CHUNK_STKL) {
            return handleSTKL(request);
        }
        throw new RuntimeException("Unknown packet " + ChunkHandler.name(type));
    }

    private Chunk handleTHEN(Chunk request) {
        ByteBuffer in = wrapChunk(request);
        boolean enable = in.get() != 0;
        DdmVmInternal.threadNotify(enable);
        return null;
    }

    private Chunk handleTHST(Chunk request) {
        wrapChunk(request);
        byte[] status = DdmVmInternal.getThreadStats();
        if (status != null) {
            return new Chunk(CHUNK_THST, status, 0, status.length);
        }
        return createFailChunk(1, "Can't build THST chunk");
    }

    private Chunk handleSTKL(Chunk request) {
        ByteBuffer in = wrapChunk(request);
        int threadId = in.getInt();
        StackTraceElement[] trace = DdmVmInternal.getStackTraceById(threadId);
        if (trace == null) {
            return createFailChunk(1, "Stack trace unavailable");
        }
        return createStackChunk(trace, threadId);
    }

    private Chunk createStackChunk(StackTraceElement[] trace, int threadId) {
        int bufferSize = 0 + 4;
        int bufferSize2 = bufferSize + 4 + 4;
        for (StackTraceElement elem : trace) {
            int bufferSize3 = bufferSize2 + 4 + (elem.getClassName().length() * 2) + 4 + (elem.getMethodName().length() * 2) + 4;
            if (elem.getFileName() != null) {
                bufferSize3 += elem.getFileName().length() * 2;
            }
            bufferSize2 = bufferSize3 + 4;
        }
        ByteBuffer out = ByteBuffer.allocate(bufferSize2);
        out.putInt(0);
        out.putInt(threadId);
        out.putInt(trace.length);
        for (StackTraceElement elem2 : trace) {
            out.putInt(elem2.getClassName().length());
            putString(out, elem2.getClassName());
            out.putInt(elem2.getMethodName().length());
            putString(out, elem2.getMethodName());
            if (elem2.getFileName() != null) {
                out.putInt(elem2.getFileName().length());
                putString(out, elem2.getFileName());
            } else {
                out.putInt(0);
            }
            out.putInt(elem2.getLineNumber());
        }
        return new Chunk(CHUNK_STKL, out);
    }
}