package java.nio;

import java.io.Closeable;
import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.IoVec;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.NonReadableChannelException;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.OverlappingFileLockException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import libcore.io.ErrnoException;
import libcore.io.Libcore;
import libcore.io.OsConstants;
import libcore.io.StructFlock;

/* loaded from: FileChannelImpl.class */
final class FileChannelImpl extends FileChannel {
    private static final Comparator<FileLock> LOCK_COMPARATOR = new Comparator<FileLock>() { // from class: java.nio.FileChannelImpl.1
        @Override // java.util.Comparator
        public int compare(FileLock lock1, FileLock lock2) {
            long position1 = lock1.position();
            long position2 = lock2.position();
            if (position1 > position2) {
                return 1;
            }
            return position1 < position2 ? -1 : 0;
        }
    };
    private final Object stream;
    private final FileDescriptor fd;
    private final int mode;
    private final SortedSet<FileLock> locks = new TreeSet(LOCK_COMPARATOR);

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.nio.FileChannelImpl.basicLock(long, long, boolean, boolean):java.nio.channels.FileLock, file: FileChannelImpl.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private java.nio.channels.FileLock basicLock(long r1, long r3, boolean r5, boolean r6) throws java.io.IOException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.nio.FileChannelImpl.basicLock(long, long, boolean, boolean):java.nio.channels.FileLock, file: FileChannelImpl.class
        */
        throw new UnsupportedOperationException("Method not decompiled: java.nio.FileChannelImpl.basicLock(long, long, boolean, boolean):java.nio.channels.FileLock");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.nio.FileChannelImpl.lock(long, long, boolean):java.nio.channels.FileLock, file: FileChannelImpl.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    @Override // java.nio.channels.FileChannel
    public final java.nio.channels.FileLock lock(long r1, long r3, boolean r5) throws java.io.IOException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.nio.FileChannelImpl.lock(long, long, boolean):java.nio.channels.FileLock, file: FileChannelImpl.class
        */
        throw new UnsupportedOperationException("Method not decompiled: java.nio.FileChannelImpl.lock(long, long, boolean):java.nio.channels.FileLock");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.nio.FileChannelImpl.readImpl(java.nio.ByteBuffer, long):int, file: FileChannelImpl.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private int readImpl(java.nio.ByteBuffer r1, long r2) throws java.io.IOException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.nio.FileChannelImpl.readImpl(java.nio.ByteBuffer, long):int, file: FileChannelImpl.class
        */
        throw new UnsupportedOperationException("Method not decompiled: java.nio.FileChannelImpl.readImpl(java.nio.ByteBuffer, long):int");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.nio.FileChannelImpl.transferIoVec(java.nio.IoVec):int, file: FileChannelImpl.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private int transferIoVec(java.nio.IoVec r1) throws java.io.IOException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.nio.FileChannelImpl.transferIoVec(java.nio.IoVec):int, file: FileChannelImpl.class
        */
        throw new UnsupportedOperationException("Method not decompiled: java.nio.FileChannelImpl.transferIoVec(java.nio.IoVec):int");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.nio.FileChannelImpl.transferFrom(java.nio.channels.ReadableByteChannel, long, long):long, file: FileChannelImpl.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    @Override // java.nio.channels.FileChannel
    public long transferFrom(java.nio.channels.ReadableByteChannel r1, long r2, long r4) throws java.io.IOException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.nio.FileChannelImpl.transferFrom(java.nio.channels.ReadableByteChannel, long, long):long, file: FileChannelImpl.class
        */
        throw new UnsupportedOperationException("Method not decompiled: java.nio.FileChannelImpl.transferFrom(java.nio.channels.ReadableByteChannel, long, long):long");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.nio.FileChannelImpl.transferTo(long, long, java.nio.channels.WritableByteChannel):long, file: FileChannelImpl.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    @Override // java.nio.channels.FileChannel
    public long transferTo(long r1, long r3, java.nio.channels.WritableByteChannel r5) throws java.io.IOException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.nio.FileChannelImpl.transferTo(long, long, java.nio.channels.WritableByteChannel):long, file: FileChannelImpl.class
        */
        throw new UnsupportedOperationException("Method not decompiled: java.nio.FileChannelImpl.transferTo(long, long, java.nio.channels.WritableByteChannel):long");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.nio.FileChannelImpl.writeImpl(java.nio.ByteBuffer, long):int, file: FileChannelImpl.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private int writeImpl(java.nio.ByteBuffer r1, long r2) throws java.io.IOException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.nio.FileChannelImpl.writeImpl(java.nio.ByteBuffer, long):int, file: FileChannelImpl.class
        */
        throw new UnsupportedOperationException("Method not decompiled: java.nio.FileChannelImpl.writeImpl(java.nio.ByteBuffer, long):int");
    }

    public FileChannelImpl(Object stream, FileDescriptor fd, int mode) {
        this.fd = fd;
        this.stream = stream;
        this.mode = mode;
    }

    private void checkOpen() throws ClosedChannelException {
        if (!isOpen()) {
            throw new ClosedChannelException();
        }
    }

    private void checkReadable() {
        if ((this.mode & OsConstants.O_ACCMODE) == OsConstants.O_WRONLY) {
            throw new NonReadableChannelException();
        }
    }

    private void checkWritable() {
        if ((this.mode & OsConstants.O_ACCMODE) == OsConstants.O_RDONLY) {
            throw new NonWritableChannelException();
        }
    }

    @Override // java.nio.channels.spi.AbstractInterruptibleChannel
    protected void implCloseChannel() throws IOException {
        if (this.stream instanceof Closeable) {
            ((Closeable) this.stream).close();
        }
    }

    private static long translateLockLength(long byteCount) {
        if (byteCount == Long.MAX_VALUE) {
            return 0L;
        }
        return byteCount;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: FileChannelImpl$FileLockImpl.class */
    public static final class FileLockImpl extends FileLock {
        private boolean isReleased;

        public FileLockImpl(FileChannel channel, long position, long size, boolean shared) {
            super(channel, position, size, shared);
            this.isReleased = false;
        }

        @Override // java.nio.channels.FileLock
        public boolean isValid() {
            return !this.isReleased && channel().isOpen();
        }

        @Override // java.nio.channels.FileLock
        public void release() throws IOException {
            if (!channel().isOpen()) {
                throw new ClosedChannelException();
            }
            if (!this.isReleased) {
                ((FileChannelImpl) channel()).release(this);
                this.isReleased = true;
            }
        }
    }

    @Override // java.nio.channels.FileChannel
    public final FileLock tryLock(long position, long size, boolean shared) throws IOException {
        checkOpen();
        return basicLock(position, size, shared, false);
    }

    public void release(FileLock lock) throws IOException {
        checkOpen();
        StructFlock flock = new StructFlock();
        flock.l_type = (short) OsConstants.F_UNLCK;
        flock.l_whence = (short) OsConstants.SEEK_SET;
        flock.l_start = lock.position();
        flock.l_len = translateLockLength(lock.size());
        try {
            Libcore.os.fcntlFlock(this.fd, OsConstants.F_SETLKW64, flock);
            removeLock(lock);
        } catch (ErrnoException errnoException) {
            throw errnoException.rethrowAsIOException();
        }
    }

    @Override // java.nio.channels.FileChannel
    public void force(boolean metadata) throws IOException {
        checkOpen();
        if ((this.mode & OsConstants.O_ACCMODE) != OsConstants.O_RDONLY) {
            try {
                if (metadata) {
                    Libcore.os.fsync(this.fd);
                } else {
                    Libcore.os.fdatasync(this.fd);
                }
            } catch (ErrnoException errnoException) {
                throw errnoException.rethrowAsIOException();
            }
        }
    }

    @Override // java.nio.channels.FileChannel
    public final MappedByteBuffer map(FileChannel.MapMode mapMode, long position, long size) throws IOException {
        checkOpen();
        if (mapMode == null) {
            throw new NullPointerException("mapMode == null");
        }
        if (position < 0 || size < 0 || size > 2147483647L) {
            throw new IllegalArgumentException("position=" + position + " size=" + size);
        }
        int accessMode = this.mode & OsConstants.O_ACCMODE;
        if (accessMode == OsConstants.O_RDONLY) {
            if (mapMode != FileChannel.MapMode.READ_ONLY) {
                throw new NonWritableChannelException();
            }
        } else if (accessMode == OsConstants.O_WRONLY) {
            throw new NonReadableChannelException();
        }
        if (position + size > size()) {
            try {
                Libcore.os.ftruncate(this.fd, position + size);
            } catch (ErrnoException ftruncateException) {
                try {
                    if (OsConstants.S_ISREG(Libcore.os.fstat(this.fd).st_mode) || ftruncateException.errno != OsConstants.EINVAL) {
                        throw ftruncateException.rethrowAsIOException();
                    }
                } catch (ErrnoException fstatException) {
                    throw fstatException.rethrowAsIOException();
                }
            }
        }
        long alignment = position - (position % Libcore.os.sysconf(OsConstants._SC_PAGE_SIZE));
        int offset = (int) (position - alignment);
        MemoryBlock block = MemoryBlock.mmap(this.fd, alignment, size + offset, mapMode);
        return new DirectByteBuffer(block, (int) size, offset, mapMode == FileChannel.MapMode.READ_ONLY, mapMode);
    }

    @Override // java.nio.channels.FileChannel
    public long position() throws IOException {
        checkOpen();
        try {
            return Libcore.os.lseek(this.fd, 0L, OsConstants.SEEK_CUR);
        } catch (ErrnoException errnoException) {
            throw errnoException.rethrowAsIOException();
        }
    }

    @Override // java.nio.channels.FileChannel
    public FileChannel position(long newPosition) throws IOException {
        checkOpen();
        if (newPosition < 0) {
            throw new IllegalArgumentException("position: " + newPosition);
        }
        try {
            Libcore.os.lseek(this.fd, newPosition, OsConstants.SEEK_SET);
            return this;
        } catch (ErrnoException errnoException) {
            throw errnoException.rethrowAsIOException();
        }
    }

    @Override // java.nio.channels.FileChannel
    public int read(ByteBuffer buffer, long position) throws IOException {
        if (position < 0) {
            throw new IllegalArgumentException("position: " + position);
        }
        return readImpl(buffer, position);
    }

    @Override // java.nio.channels.FileChannel, java.nio.channels.ReadableByteChannel
    public int read(ByteBuffer buffer) throws IOException {
        return readImpl(buffer, -1L);
    }

    @Override // java.nio.channels.FileChannel, java.nio.channels.ScatteringByteChannel
    public long read(ByteBuffer[] buffers, int offset, int length) throws IOException {
        Arrays.checkOffsetAndCount(buffers.length, offset, length);
        checkOpen();
        checkReadable();
        return transferIoVec(new IoVec(buffers, offset, length, IoVec.Direction.READV));
    }

    @Override // java.nio.channels.FileChannel
    public long size() throws IOException {
        checkOpen();
        try {
            return Libcore.os.fstat(this.fd).st_size;
        } catch (ErrnoException errnoException) {
            throw errnoException.rethrowAsIOException();
        }
    }

    @Override // java.nio.channels.FileChannel
    public FileChannel truncate(long size) throws IOException {
        checkOpen();
        if (size < 0) {
            throw new IllegalArgumentException("size < 0: " + size);
        }
        checkWritable();
        if (size < size()) {
            try {
                Libcore.os.ftruncate(this.fd, size);
            } catch (ErrnoException errnoException) {
                throw errnoException.rethrowAsIOException();
            }
        }
        return this;
    }

    @Override // java.nio.channels.FileChannel
    public int write(ByteBuffer buffer, long position) throws IOException {
        if (position < 0) {
            throw new IllegalArgumentException("position < 0: " + position);
        }
        return writeImpl(buffer, position);
    }

    @Override // java.nio.channels.FileChannel, java.nio.channels.WritableByteChannel
    public int write(ByteBuffer buffer) throws IOException {
        return writeImpl(buffer, -1L);
    }

    @Override // java.nio.channels.FileChannel, java.nio.channels.GatheringByteChannel
    public long write(ByteBuffer[] buffers, int offset, int length) throws IOException {
        Arrays.checkOffsetAndCount(buffers.length, offset, length);
        checkOpen();
        checkWritable();
        return transferIoVec(new IoVec(buffers, offset, length, IoVec.Direction.WRITEV));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int calculateTotalRemaining(ByteBuffer[] buffers, int offset, int length, boolean copyingIn) {
        int count = 0;
        for (int i = offset; i < offset + length; i++) {
            count += buffers[i].remaining();
            if (copyingIn) {
                buffers[i].checkWritable();
            }
        }
        return count;
    }

    public FileDescriptor getFD() {
        return this.fd;
    }

    private synchronized void addLock(FileLock lock) throws OverlappingFileLockException {
        long lockEnd = lock.position() + lock.size();
        for (FileLock existingLock : this.locks) {
            if (existingLock.position() > lockEnd) {
                break;
            } else if (existingLock.overlaps(lock.position(), lock.size())) {
                throw new OverlappingFileLockException();
            }
        }
        this.locks.add(lock);
    }

    private synchronized void removeLock(FileLock lock) {
        this.locks.remove(lock);
    }
}