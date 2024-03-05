package com.android.server.input;

import android.graphics.Region;
import android.view.InputChannel;

/* loaded from: InputWindowHandle.class */
public final class InputWindowHandle {
    private int ptr;
    public final InputApplicationHandle inputApplicationHandle;
    public final Object windowState;
    public InputChannel inputChannel;
    public String name;
    public int layoutParamsFlags;
    public int layoutParamsPrivateFlags;
    public int layoutParamsType;
    public long dispatchingTimeoutNanos;
    public int frameLeft;
    public int frameTop;
    public int frameRight;
    public int frameBottom;
    public float scaleFactor;
    public final Region touchableRegion = new Region();
    public boolean visible;
    public boolean canReceiveKeys;
    public boolean hasFocus;
    public boolean hasWallpaper;
    public boolean paused;
    public int layer;
    public int ownerPid;
    public int ownerUid;
    public int inputFeatures;
    public final int displayId;

    private native void nativeDispose();

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.input.InputWindowHandle.finalize():void, file: InputWindowHandle.class
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
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.input.InputWindowHandle.finalize():void, file: InputWindowHandle.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.input.InputWindowHandle.finalize():void");
    }

    public InputWindowHandle(InputApplicationHandle inputApplicationHandle, Object windowState, int displayId) {
        this.inputApplicationHandle = inputApplicationHandle;
        this.windowState = windowState;
        this.displayId = displayId;
    }
}