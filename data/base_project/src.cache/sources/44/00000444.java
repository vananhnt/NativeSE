package android.ddm;

import android.os.Debug;
import android.os.Process;
import android.os.UserHandle;
import gov.nist.core.Separators;
import java.nio.ByteBuffer;
import org.apache.harmony.dalvik.ddmc.Chunk;
import org.apache.harmony.dalvik.ddmc.ChunkHandler;
import org.apache.harmony.dalvik.ddmc.DdmServer;

/* loaded from: DdmHandleHello.class */
public class DdmHandleHello extends ChunkHandler {
    public static final int CHUNK_HELO = type("HELO");
    public static final int CHUNK_WAIT = type("WAIT");
    public static final int CHUNK_FEAT = type("FEAT");
    private static DdmHandleHello mInstance = new DdmHandleHello();
    private static final String[] FRAMEWORK_FEATURES = {"opengl-tracing", "view-hierarchy"};

    private DdmHandleHello() {
    }

    public static void register() {
        DdmServer.registerHandler(CHUNK_HELO, mInstance);
        DdmServer.registerHandler(CHUNK_FEAT, mInstance);
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
        if (type == CHUNK_HELO) {
            return handleHELO(request);
        }
        if (type == CHUNK_FEAT) {
            return handleFEAT(request);
        }
        throw new RuntimeException("Unknown packet " + ChunkHandler.name(type));
    }

    private Chunk handleHELO(Chunk request) {
        ByteBuffer in = wrapChunk(request);
        in.getInt();
        String vmName = System.getProperty("java.vm.name", Separators.QUESTION);
        String vmVersion = System.getProperty("java.vm.version", Separators.QUESTION);
        String vmIdent = vmName + " v" + vmVersion;
        String appName = DdmHandleAppName.getAppName();
        ByteBuffer out = ByteBuffer.allocate(20 + (vmIdent.length() * 2) + (appName.length() * 2));
        out.order(ChunkHandler.CHUNK_ORDER);
        out.putInt(1);
        out.putInt(Process.myPid());
        out.putInt(vmIdent.length());
        out.putInt(appName.length());
        putString(out, vmIdent);
        putString(out, appName);
        out.putInt(UserHandle.myUserId());
        Chunk reply = new Chunk(CHUNK_HELO, out);
        if (Debug.waitingForDebugger()) {
            sendWAIT(0);
        }
        return reply;
    }

    private Chunk handleFEAT(Chunk request) {
        String[] vmFeatures = Debug.getVmFeatureList();
        int size = 4 + (4 * (vmFeatures.length + FRAMEWORK_FEATURES.length));
        for (int i = vmFeatures.length - 1; i >= 0; i--) {
            size += vmFeatures[i].length() * 2;
        }
        for (int i2 = FRAMEWORK_FEATURES.length - 1; i2 >= 0; i2--) {
            size += FRAMEWORK_FEATURES[i2].length() * 2;
        }
        ByteBuffer out = ByteBuffer.allocate(size);
        out.order(ChunkHandler.CHUNK_ORDER);
        out.putInt(vmFeatures.length + FRAMEWORK_FEATURES.length);
        for (int i3 = vmFeatures.length - 1; i3 >= 0; i3--) {
            out.putInt(vmFeatures[i3].length());
            putString(out, vmFeatures[i3]);
        }
        for (int i4 = FRAMEWORK_FEATURES.length - 1; i4 >= 0; i4--) {
            out.putInt(FRAMEWORK_FEATURES[i4].length());
            putString(out, FRAMEWORK_FEATURES[i4]);
        }
        return new Chunk(CHUNK_FEAT, out);
    }

    public static void sendWAIT(int reason) {
        byte[] data = {(byte) reason};
        Chunk waitChunk = new Chunk(CHUNK_WAIT, data, 0, 1);
        DdmServer.sendChunk(waitChunk);
    }
}