package java.nio;

/* loaded from: NIOAccess.class */
final class NIOAccess {
    NIOAccess() {
    }

    static long getBasePointer(Buffer b) {
        long address = b.effectiveDirectAddress;
        if (address == 0) {
            return 0L;
        }
        return address + (b.position << b._elementSizeShift);
    }

    static Object getBaseArray(Buffer b) {
        if (b.hasArray()) {
            return b.array();
        }
        return null;
    }

    static int getBaseArrayOffset(Buffer b) {
        if (b.hasArray()) {
            return (b.arrayOffset() + b.position) << b._elementSizeShift;
        }
        return 0;
    }
}