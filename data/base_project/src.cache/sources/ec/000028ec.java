package libcore.io;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Random;

/* loaded from: IoUtils.class */
public final class IoUtils {
    private static final Random TEMPORARY_DIRECTORY_PRNG = new Random();

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: libcore.io.IoUtils.readFileAsBytes(java.lang.String):java.lang.UnsafeByteSequence, file: IoUtils.class
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
    private static java.lang.UnsafeByteSequence readFileAsBytes(java.lang.String r0) throws java.io.IOException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: libcore.io.IoUtils.readFileAsBytes(java.lang.String):java.lang.UnsafeByteSequence, file: IoUtils.class
        */
        throw new UnsupportedOperationException("Method not decompiled: libcore.io.IoUtils.readFileAsBytes(java.lang.String):java.lang.UnsafeByteSequence");
    }

    private IoUtils() {
    }

    public static void close(FileDescriptor fd) throws IOException {
        if (fd != null) {
            try {
                if (fd.valid()) {
                    Libcore.os.close(fd);
                }
            } catch (ErrnoException errnoException) {
                throw errnoException.rethrowAsIOException();
            }
        }
    }

    public static void closeQuietly(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception e) {
            }
        }
    }

    public static void closeQuietly(FileDescriptor fd) {
        try {
            close(fd);
        } catch (IOException e) {
        }
    }

    public static void closeQuietly(Socket socket) {
        if (socket != null) {
            try {
                socket.close();
            } catch (Exception e) {
            }
        }
    }

    public static void setBlocking(FileDescriptor fd, boolean blocking) throws IOException {
        int flags;
        try {
            int flags2 = Libcore.os.fcntlVoid(fd, OsConstants.F_GETFL);
            if (!blocking) {
                flags = flags2 | OsConstants.O_NONBLOCK;
            } else {
                flags = flags2 & (OsConstants.O_NONBLOCK ^ (-1));
            }
            Libcore.os.fcntlLong(fd, OsConstants.F_SETFL, flags);
        } catch (ErrnoException errnoException) {
            throw errnoException.rethrowAsIOException();
        }
    }

    public static byte[] readFileAsByteArray(String path) throws IOException {
        return readFileAsBytes(path).toByteArray();
    }

    public static String readFileAsString(String path) throws IOException {
        return readFileAsBytes(path).toString(StandardCharsets.UTF_8);
    }

    public static void deleteContents(File dir) throws IOException {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteContents(file);
                }
                file.delete();
            }
        }
    }

    public static File createTemporaryDirectory(String prefix) {
        File result;
        do {
            String candidateName = prefix + TEMPORARY_DIRECTORY_PRNG.nextInt();
            result = new File(System.getProperty("java.io.tmpdir"), candidateName);
        } while (!result.mkdir());
        return result;
    }

    public static boolean canOpenReadOnly(String path) {
        try {
            FileDescriptor fd = Libcore.os.open(path, OsConstants.O_RDONLY, 0);
            Libcore.os.close(fd);
            return true;
        } catch (ErrnoException e) {
            return false;
        }
    }

    public static void throwInterruptedIoException() throws InterruptedIOException {
        Thread.currentThread().interrupt();
        throw new InterruptedIOException();
    }
}