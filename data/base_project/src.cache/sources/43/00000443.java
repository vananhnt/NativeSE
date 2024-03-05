package android.ddm;

import android.os.Debug;
import android.util.Log;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.harmony.dalvik.ddmc.Chunk;
import org.apache.harmony.dalvik.ddmc.ChunkHandler;
import org.apache.harmony.dalvik.ddmc.DdmServer;
import org.apache.harmony.dalvik.ddmc.DdmVmInternal;

/* loaded from: DdmHandleHeap.class */
public class DdmHandleHeap extends ChunkHandler {
    public static final int CHUNK_HPIF = type("HPIF");
    public static final int CHUNK_HPSG = type("HPSG");
    public static final int CHUNK_HPDU = type("HPDU");
    public static final int CHUNK_HPDS = type("HPDS");
    public static final int CHUNK_NHSG = type("NHSG");
    public static final int CHUNK_HPGC = type("HPGC");
    public static final int CHUNK_REAE = type("REAE");
    public static final int CHUNK_REAQ = type("REAQ");
    public static final int CHUNK_REAL = type("REAL");
    private static DdmHandleHeap mInstance = new DdmHandleHeap();

    private DdmHandleHeap() {
    }

    public static void register() {
        DdmServer.registerHandler(CHUNK_HPIF, mInstance);
        DdmServer.registerHandler(CHUNK_HPSG, mInstance);
        DdmServer.registerHandler(CHUNK_HPDU, mInstance);
        DdmServer.registerHandler(CHUNK_HPDS, mInstance);
        DdmServer.registerHandler(CHUNK_NHSG, mInstance);
        DdmServer.registerHandler(CHUNK_HPGC, mInstance);
        DdmServer.registerHandler(CHUNK_REAE, mInstance);
        DdmServer.registerHandler(CHUNK_REAQ, mInstance);
        DdmServer.registerHandler(CHUNK_REAL, mInstance);
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
        if (type == CHUNK_HPIF) {
            return handleHPIF(request);
        }
        if (type == CHUNK_HPSG) {
            return handleHPSGNHSG(request, false);
        }
        if (type == CHUNK_HPDU) {
            return handleHPDU(request);
        }
        if (type == CHUNK_HPDS) {
            return handleHPDS(request);
        }
        if (type == CHUNK_NHSG) {
            return handleHPSGNHSG(request, true);
        }
        if (type == CHUNK_HPGC) {
            return handleHPGC(request);
        }
        if (type == CHUNK_REAE) {
            return handleREAE(request);
        }
        if (type == CHUNK_REAQ) {
            return handleREAQ(request);
        }
        if (type == CHUNK_REAL) {
            return handleREAL(request);
        }
        throw new RuntimeException("Unknown packet " + ChunkHandler.name(type));
    }

    private Chunk handleHPIF(Chunk request) {
        ByteBuffer in = wrapChunk(request);
        int when = in.get();
        boolean ok = DdmVmInternal.heapInfoNotify(when);
        if (!ok) {
            return createFailChunk(1, "Unsupported HPIF what");
        }
        return null;
    }

    private Chunk handleHPSGNHSG(Chunk request, boolean isNative) {
        ByteBuffer in = wrapChunk(request);
        int when = in.get();
        int what = in.get();
        boolean ok = DdmVmInternal.heapSegmentNotify(when, what, isNative);
        if (!ok) {
            return createFailChunk(1, "Unsupported HPSG what/when");
        }
        return null;
    }

    private Chunk handleHPDU(Chunk request) {
        byte result;
        ByteBuffer in = wrapChunk(request);
        int len = in.getInt();
        String fileName = getString(in, len);
        try {
            Debug.dumpHprofData(fileName);
            result = 0;
        } catch (IOException e) {
            result = -1;
        } catch (UnsupportedOperationException e2) {
            Log.w("ddm-heap", "hprof dumps not supported in this VM");
            result = -1;
        } catch (RuntimeException e3) {
            result = -1;
        }
        byte[] reply = {result};
        return new Chunk(CHUNK_HPDU, reply, 0, reply.length);
    }

    private Chunk handleHPDS(Chunk request) {
        wrapChunk(request);
        String failMsg = null;
        try {
            Debug.dumpHprofDataDdms();
        } catch (UnsupportedOperationException e) {
            failMsg = "hprof dumps not supported in this VM";
        } catch (RuntimeException re) {
            failMsg = "Exception: " + re.getMessage();
        }
        if (failMsg != null) {
            Log.w("ddm-heap", failMsg);
            return createFailChunk(1, failMsg);
        }
        return null;
    }

    private Chunk handleHPGC(Chunk request) {
        System.gc();
        return null;
    }

    private Chunk handleREAE(Chunk request) {
        ByteBuffer in = wrapChunk(request);
        boolean enable = in.get() != 0;
        DdmVmInternal.enableRecentAllocations(enable);
        return null;
    }

    private Chunk handleREAQ(Chunk request) {
        byte[] reply = new byte[1];
        reply[0] = DdmVmInternal.getRecentAllocationStatus() ? (byte) 1 : (byte) 0;
        return new Chunk(CHUNK_REAQ, reply, 0, reply.length);
    }

    private Chunk handleREAL(Chunk request) {
        byte[] reply = DdmVmInternal.getRecentAllocations();
        return new Chunk(CHUNK_REAL, reply, 0, reply.length);
    }
}