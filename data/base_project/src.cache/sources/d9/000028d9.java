package libcore.icu;

/* loaded from: Transliterator.class */
public final class Transliterator {
    private long peer;

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: libcore.icu.Transliterator.finalize():void, file: Transliterator.class
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
    protected synchronized void finalize() throws java.lang.Throwable {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: libcore.icu.Transliterator.finalize():void, file: Transliterator.class
        */
        throw new UnsupportedOperationException("Method not decompiled: libcore.icu.Transliterator.finalize():void");
    }

    public static native String[] getAvailableIDs();

    private static native long create(String str);

    private static native void destroy(long j);

    private static native String transliterate(long j, String str);

    public Transliterator(String id) {
        this.peer = create(id);
    }

    public String transliterate(String s) {
        return transliterate(this.peer, s);
    }
}