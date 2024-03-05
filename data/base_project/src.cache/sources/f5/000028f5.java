package libcore.io;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

/* loaded from: Streams.class */
public final class Streams {
    private static AtomicReference<byte[]> skipBuffer = new AtomicReference<>();

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: libcore.io.Streams.readFully(java.io.InputStream):byte[], file: Streams.class
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
    public static byte[] readFully(java.io.InputStream r0) throws java.io.IOException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: libcore.io.Streams.readFully(java.io.InputStream):byte[], file: Streams.class
        */
        throw new UnsupportedOperationException("Method not decompiled: libcore.io.Streams.readFully(java.io.InputStream):byte[]");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: libcore.io.Streams.readFully(java.io.Reader):java.lang.String, file: Streams.class
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
    public static java.lang.String readFully(java.io.Reader r0) throws java.io.IOException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: libcore.io.Streams.readFully(java.io.Reader):java.lang.String, file: Streams.class
        */
        throw new UnsupportedOperationException("Method not decompiled: libcore.io.Streams.readFully(java.io.Reader):java.lang.String");
    }

    private Streams() {
    }

    public static int readSingleByte(InputStream in) throws IOException {
        byte[] buffer = new byte[1];
        int result = in.read(buffer, 0, 1);
        if (result != -1) {
            return buffer[0] & 255;
        }
        return -1;
    }

    public static void writeSingleByte(OutputStream out, int b) throws IOException {
        byte[] buffer = {(byte) (b & 255)};
        out.write(buffer);
    }

    public static void readFully(InputStream in, byte[] dst) throws IOException {
        readFully(in, dst, 0, dst.length);
    }

    public static void readFully(InputStream in, byte[] dst, int offset, int byteCount) throws IOException {
        if (byteCount == 0) {
            return;
        }
        if (in == null) {
            throw new NullPointerException("in == null");
        }
        if (dst == null) {
            throw new NullPointerException("dst == null");
        }
        Arrays.checkOffsetAndCount(dst.length, offset, byteCount);
        while (byteCount > 0) {
            int bytesRead = in.read(dst, offset, byteCount);
            if (bytesRead < 0) {
                throw new EOFException();
            }
            offset += bytesRead;
            byteCount -= bytesRead;
        }
    }

    public static byte[] readFullyNoClose(InputStream in) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        while (true) {
            int count = in.read(buffer);
            if (count != -1) {
                bytes.write(buffer, 0, count);
            } else {
                return bytes.toByteArray();
            }
        }
    }

    public static void skipAll(InputStream in) throws IOException {
        do {
            in.skip(Long.MAX_VALUE);
        } while (in.read() != -1);
    }

    public static long skipByReading(InputStream in, long byteCount) throws IOException {
        int toRead;
        int read;
        byte[] buffer = skipBuffer.getAndSet(null);
        if (buffer == null) {
            buffer = new byte[4096];
        }
        long skipped = 0;
        while (skipped < byteCount && (read = in.read(buffer, 0, (toRead = (int) Math.min(byteCount - skipped, buffer.length)))) != -1) {
            skipped += read;
            if (read < toRead) {
                break;
            }
        }
        skipBuffer.set(buffer);
        return skipped;
    }

    public static int copy(InputStream in, OutputStream out) throws IOException {
        int total = 0;
        byte[] buffer = new byte[8192];
        while (true) {
            int c = in.read(buffer);
            if (c != -1) {
                total += c;
                out.write(buffer, 0, c);
            } else {
                return total;
            }
        }
    }

    public static String readAsciiLine(InputStream in) throws IOException {
        StringBuilder result = new StringBuilder(80);
        while (true) {
            int c = in.read();
            if (c == -1) {
                throw new EOFException();
            }
            if (c != 10) {
                result.append((char) c);
            } else {
                int length = result.length();
                if (length > 0 && result.charAt(length - 1) == '\r') {
                    result.setLength(length - 1);
                }
                return result.toString();
            }
        }
    }
}