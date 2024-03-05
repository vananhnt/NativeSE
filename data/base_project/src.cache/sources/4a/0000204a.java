package com.android.webview.chromium;

import android.util.Log;

/* loaded from: UnimplementedWebViewApi.class */
public class UnimplementedWebViewApi {
    private static String TAG = "UnimplementedWebViewApi";
    private static boolean THROW = false;
    private static boolean FULL_TRACE = false;

    /* loaded from: UnimplementedWebViewApi$UnimplementedWebViewApiException.class */
    private static class UnimplementedWebViewApiException extends UnsupportedOperationException {
    }

    public static void invoke() throws UnimplementedWebViewApiException {
        if (THROW) {
            throw new UnimplementedWebViewApiException();
        }
        if (FULL_TRACE) {
            Log.w(TAG, "Unimplemented WebView method called in: " + Log.getStackTraceString(new Throwable()));
            return;
        }
        StackTraceElement[] trace = new Throwable().getStackTrace();
        StackTraceElement unimplementedMethod = trace[1];
        StackTraceElement caller = trace[2];
        Log.w(TAG, "Unimplemented WebView method " + unimplementedMethod.getMethodName() + " called from: " + caller.toString());
    }
}