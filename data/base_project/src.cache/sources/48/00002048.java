package com.android.webview.chromium;

/* loaded from: GraphicsUtils.class */
abstract class GraphicsUtils {
    private static native int nativeGetDrawSWFunctionTable();

    private static native int nativeGetDrawGLFunctionTable();

    GraphicsUtils() {
    }

    public static int getDrawSWFunctionTable() {
        return nativeGetDrawSWFunctionTable();
    }

    public static int getDrawGLFunctionTable() {
        return nativeGetDrawGLFunctionTable();
    }
}