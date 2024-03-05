package java.nio;

import dalvik.system.VMRuntime;
import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.channels.FileChannel;
import libcore.io.ErrnoException;
import libcore.io.Libcore;
import libcore.io.Memory;
import libcore.io.OsConstants;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: MemoryBlock.class */
public class MemoryBlock {
    protected long address;
    protected final long size;

    /* loaded from: MemoryBlock$MemoryMappedBlock.class */
    private static class MemoryMappedBlock extends MemoryBlock {
        private MemoryMappedBlock(long address, long byteCount) {
            super(address, byteCount);
        }

        @Override // java.nio.MemoryBlock
        public void free() {
            if (this.address != 0) {
                try {
                    Libcore.os.munmap(this.address, this.size);
                    this.address = 0L;
                } catch (ErrnoException errnoException) {
                    throw new AssertionError(errnoException);
                }
            }
        }

        protected void finalize() throws Throwable {
            free();
        }
    }

    /* loaded from: MemoryBlock$NonMovableHeapBlock.class */
    private static class NonMovableHeapBlock extends MemoryBlock {
        private byte[] array;

        private NonMovableHeapBlock(byte[] array, long address, long byteCount) {
            super(address, byteCount);
            this.array = array;
        }

        @Override // java.nio.MemoryBlock
        public byte[] array() {
            return this.array;
        }

        @Override // java.nio.MemoryBlock
        public void free() {
            this.array = null;
            this.address = 0L;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: MemoryBlock$UnmanagedBlock.class */
    public static class UnmanagedBlock extends MemoryBlock {
        private UnmanagedBlock(long address, long byteCount) {
            super(address, byteCount);
        }
    }

    public static MemoryBlock mmap(FileDescriptor fd, long offset, long size, FileChannel.MapMode mapMode) throws IOException {
        int prot;
        int flags;
        if (size == 0) {
            return new MemoryBlock(0L, 0L);
        }
        if (offset < 0 || size < 0 || offset > 2147483647L || size > 2147483647L) {
            throw new IllegalArgumentException("offset=" + offset + " size=" + size);
        }
        if (mapMode == FileChannel.MapMode.PRIVATE) {
            prot = OsConstants.PROT_READ | OsConstants.PROT_WRITE;
            flags = OsConstants.MAP_PRIVATE;
        } else if (mapMode == FileChannel.MapMode.READ_ONLY) {
            prot = OsConstants.PROT_READ;
            flags = OsConstants.MAP_SHARED;
        } else {
            prot = OsConstants.PROT_READ | OsConstants.PROT_WRITE;
            flags = OsConstants.MAP_SHARED;
        }
        try {
            long address = Libcore.os.mmap(0L, size, prot, flags, fd, offset);
            return new MemoryMappedBlock(address, size);
        } catch (ErrnoException errnoException) {
            throw errnoException.rethrowAsIOException();
        }
    }

    public static MemoryBlock allocate(int byteCount) {
        VMRuntime runtime = VMRuntime.getRuntime();
        byte[] array = (byte[]) runtime.newNonMovableArray(Byte.TYPE, byteCount);
        long address = runtime.addressOf(array);
        return new NonMovableHeapBlock(array, address, byteCount);
    }

    public static MemoryBlock wrapFromJni(long address, long byteCount) {
        return new UnmanagedBlock(address, byteCount);
    }

    private MemoryBlock(long address, long size) {
        this.address = address;
        this.size = size;
    }

    public byte[] array() {
        return null;
    }

    public void free() {
    }

    public final void pokeByte(int offset, byte value) {
        Memory.pokeByte(this.address + offset, value);
    }

    public final void pokeByteArray(int offset, byte[] src, int srcOffset, int byteCount) {
        Memory.pokeByteArray(this.address + offset, src, srcOffset, byteCount);
    }

    public final void pokeCharArray(int offset, char[] src, int srcOffset, int charCount, boolean swap) {
        Memory.pokeCharArray(this.address + offset, src, srcOffset, charCount, swap);
    }

    public final void pokeDoubleArray(int offset, double[] src, int srcOffset, int doubleCount, boolean swap) {
        Memory.pokeDoubleArray(this.address + offset, src, srcOffset, doubleCount, swap);
    }

    public final void pokeFloatArray(int offset, float[] src, int srcOffset, int floatCount, boolean swap) {
        Memory.pokeFloatArray(this.address + offset, src, srcOffset, floatCount, swap);
    }

    public final void pokeIntArray(int offset, int[] src, int srcOffset, int intCount, boolean swap) {
        Memory.pokeIntArray(this.address + offset, src, srcOffset, intCount, swap);
    }

    public final void pokeLongArray(int offset, long[] src, int srcOffset, int longCount, boolean swap) {
        Memory.pokeLongArray(this.address + offset, src, srcOffset, longCount, swap);
    }

    public final void pokeShortArray(int offset, short[] src, int srcOffset, int shortCount, boolean swap) {
        Memory.pokeShortArray(this.address + offset, src, srcOffset, shortCount, swap);
    }

    public final byte peekByte(int offset) {
        return Memory.peekByte(this.address + offset);
    }

    public final void peekByteArray(int offset, byte[] dst, int dstOffset, int byteCount) {
        Memory.peekByteArray(this.address + offset, dst, dstOffset, byteCount);
    }

    public final void peekCharArray(int offset, char[] dst, int dstOffset, int charCount, boolean swap) {
        Memory.peekCharArray(this.address + offset, dst, dstOffset, charCount, swap);
    }

    public final void peekDoubleArray(int offset, double[] dst, int dstOffset, int doubleCount, boolean swap) {
        Memory.peekDoubleArray(this.address + offset, dst, dstOffset, doubleCount, swap);
    }

    public final void peekFloatArray(int offset, float[] dst, int dstOffset, int floatCount, boolean swap) {
        Memory.peekFloatArray(this.address + offset, dst, dstOffset, floatCount, swap);
    }

    public final void peekIntArray(int offset, int[] dst, int dstOffset, int intCount, boolean swap) {
        Memory.peekIntArray(this.address + offset, dst, dstOffset, intCount, swap);
    }

    public final void peekLongArray(int offset, long[] dst, int dstOffset, int longCount, boolean swap) {
        Memory.peekLongArray(this.address + offset, dst, dstOffset, longCount, swap);
    }

    public final void peekShortArray(int offset, short[] dst, int dstOffset, int shortCount, boolean swap) {
        Memory.peekShortArray(this.address + offset, dst, dstOffset, shortCount, swap);
    }

    public final void pokeShort(int offset, short value, ByteOrder order) {
        Memory.pokeShort(this.address + offset, value, order.needsSwap);
    }

    public final short peekShort(int offset, ByteOrder order) {
        return Memory.peekShort(this.address + offset, order.needsSwap);
    }

    public final void pokeInt(int offset, int value, ByteOrder order) {
        Memory.pokeInt(this.address + offset, value, order.needsSwap);
    }

    public final int peekInt(int offset, ByteOrder order) {
        return Memory.peekInt(this.address + offset, order.needsSwap);
    }

    public final void pokeLong(int offset, long value, ByteOrder order) {
        Memory.pokeLong(this.address + offset, value, order.needsSwap);
    }

    public final long peekLong(int offset, ByteOrder order) {
        return Memory.peekLong(this.address + offset, order.needsSwap);
    }

    public final long toLong() {
        return this.address;
    }

    public final String toString() {
        return getClass().getName() + "[" + this.address + "]";
    }

    public final long getSize() {
        return this.size;
    }
}