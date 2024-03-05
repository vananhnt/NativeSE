package org.apache.harmony.dalvik.ddmc;

import java.util.Collection;
import java.util.HashMap;

/* loaded from: DdmServer.class */
public class DdmServer {
    public static final int CLIENT_PROTOCOL_VERSION = 1;
    private static final int CONNECTED = 1;
    private static final int DISCONNECTED = 2;
    private static HashMap<Integer, ChunkHandler> mHandlerMap = new HashMap<>();
    private static volatile boolean mRegistrationComplete = false;
    private static boolean mRegistrationTimedOut = false;

    private static native void nativeSendChunk(int i, byte[] bArr, int i2, int i3);

    private DdmServer() {
    }

    public static void registerHandler(int type, ChunkHandler handler) {
        if (handler == null) {
            throw new NullPointerException("handler == null");
        }
        synchronized (mHandlerMap) {
            if (mHandlerMap.get(Integer.valueOf(type)) != null) {
                throw new RuntimeException("type " + Integer.toHexString(type) + " already registered");
            }
            mHandlerMap.put(Integer.valueOf(type), handler);
        }
    }

    public static ChunkHandler unregisterHandler(int type) {
        ChunkHandler remove;
        synchronized (mHandlerMap) {
            remove = mHandlerMap.remove(Integer.valueOf(type));
        }
        return remove;
    }

    public static void registrationComplete() {
        synchronized (mHandlerMap) {
            mRegistrationComplete = true;
            mHandlerMap.notifyAll();
        }
    }

    public static void sendChunk(Chunk chunk) {
        nativeSendChunk(chunk.type, chunk.data, chunk.offset, chunk.length);
    }

    private static void broadcast(int event) {
        synchronized (mHandlerMap) {
            Collection values = mHandlerMap.values();
            for (ChunkHandler handler : values) {
                switch (event) {
                    case 1:
                        handler.connected();
                        break;
                    case 2:
                        handler.disconnected();
                        break;
                    default:
                        throw new UnsupportedOperationException();
                }
            }
        }
    }

    private static Chunk dispatch(int type, byte[] data, int offset, int length) {
        ChunkHandler handler;
        synchronized (mHandlerMap) {
            while (!mRegistrationComplete && !mRegistrationTimedOut) {
                try {
                    mHandlerMap.wait(1000L);
                    if (!mRegistrationComplete) {
                        mRegistrationTimedOut = true;
                    }
                } catch (InterruptedException e) {
                }
            }
            handler = mHandlerMap.get(Integer.valueOf(type));
        }
        if (handler == null) {
            return null;
        }
        Chunk chunk = new Chunk(type, data, offset, length);
        return handler.handleChunk(chunk);
    }
}