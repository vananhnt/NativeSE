package com.android.server.input;

/* loaded from: InputApplicationHandle.class */
public final class InputApplicationHandle {
    private int ptr;
    public final Object appWindowToken;
    public String name;
    public long dispatchingTimeoutNanos;

    private native void nativeDispose();

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.input.InputApplicationHandle.finalize():void, file: InputApplicationHandle.class
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
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.input.InputApplicationHandle.finalize():void, file: InputApplicationHandle.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.input.InputApplicationHandle.finalize():void");
    }

    public InputApplicationHandle(Object appWindowToken) {
        this.appWindowToken = appWindowToken;
    }
}