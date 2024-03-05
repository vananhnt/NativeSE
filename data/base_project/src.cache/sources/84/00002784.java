package javax.crypto;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import libcore.io.Streams;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: CipherOutputStream.class */
public class CipherOutputStream extends FilterOutputStream {
    private final Cipher cipher;

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: javax.crypto.CipherOutputStream.close():void, file: CipherOutputStream.class
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
    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable
    public void close() throws java.io.IOException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: javax.crypto.CipherOutputStream.close():void, file: CipherOutputStream.class
        */
        throw new UnsupportedOperationException("Method not decompiled: javax.crypto.CipherOutputStream.close():void");
    }

    public CipherOutputStream(OutputStream os, Cipher c) {
        super(os);
        this.cipher = c;
    }

    protected CipherOutputStream(OutputStream os) {
        this(os, new NullCipher());
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(int b) throws IOException {
        Streams.writeSingleByte(this, b);
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] b, int off, int len) throws IOException {
        byte[] result;
        if (len != 0 && (result = this.cipher.update(b, off, len)) != null) {
            this.out.write(result);
        }
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Flushable
    public void flush() throws IOException {
        this.out.flush();
    }
}