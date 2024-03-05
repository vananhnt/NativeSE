package com.android.server;

import android.util.Slog;
import java.io.Closeable;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/* loaded from: RandomBlock.class */
class RandomBlock {
    private static final String TAG = "RandomBlock";
    private static final boolean DEBUG = false;
    private static final int BLOCK_SIZE = 4096;
    private byte[] block = new byte[4096];

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.RandomBlock.fromFile(java.lang.String):com.android.server.RandomBlock, file: RandomBlock.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    static com.android.server.RandomBlock fromFile(java.lang.String r0) throws java.io.IOException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.RandomBlock.fromFile(java.lang.String):com.android.server.RandomBlock, file: RandomBlock.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.RandomBlock.fromFile(java.lang.String):com.android.server.RandomBlock");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.RandomBlock.toFile(java.lang.String, boolean):void, file: RandomBlock.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    void toFile(java.lang.String r1, boolean r2) throws java.io.IOException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.RandomBlock.toFile(java.lang.String, boolean):void, file: RandomBlock.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.RandomBlock.toFile(java.lang.String, boolean):void");
    }

    private RandomBlock() {
    }

    private static RandomBlock fromStream(InputStream in) throws IOException {
        RandomBlock retval = new RandomBlock();
        int i = 0;
        while (true) {
            int total = i;
            if (total < 4096) {
                int result = in.read(retval.block, total, 4096 - total);
                if (result == -1) {
                    throw new EOFException();
                }
                i = total + result;
            } else {
                return retval;
            }
        }
    }

    private static void truncateIfPossible(RandomAccessFile f) {
        try {
            f.setLength(4096L);
        } catch (IOException e) {
        }
    }

    private void toDataOut(DataOutput out) throws IOException {
        out.write(this.block);
    }

    private static void close(Closeable c) {
        if (c == null) {
            return;
        }
        try {
            c.close();
        } catch (IOException e) {
            Slog.w(TAG, "IOException thrown while closing Closeable", e);
        }
    }
}