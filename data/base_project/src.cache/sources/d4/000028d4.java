package libcore.icu;

import java.util.Locale;

/* loaded from: NativePluralRules.class */
public final class NativePluralRules {
    public static final int ZERO = 0;
    public static final int ONE = 1;
    public static final int TWO = 2;
    public static final int FEW = 3;
    public static final int MANY = 4;
    public static final int OTHER = 5;
    private final long address;

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: libcore.icu.NativePluralRules.finalize():void, file: NativePluralRules.class
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
    protected void finalize() throws java.lang.Throwable {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: libcore.icu.NativePluralRules.finalize():void, file: NativePluralRules.class
        */
        throw new UnsupportedOperationException("Method not decompiled: libcore.icu.NativePluralRules.finalize():void");
    }

    private static native void finalizeImpl(long j);

    private static native long forLocaleImpl(String str);

    private static native int quantityForIntImpl(long j, int i);

    private NativePluralRules(long address) {
        this.address = address;
    }

    public static NativePluralRules forLocale(Locale locale) {
        return new NativePluralRules(forLocaleImpl(locale.toString()));
    }

    public int quantityForInt(int value) {
        return quantityForIntImpl(this.address, value);
    }
}