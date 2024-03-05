package com.android.server.location;

import android.content.Context;
import android.net.Proxy;
import android.util.Log;
import java.util.Properties;
import java.util.Random;

/* loaded from: GpsXtraDownloader.class */
public class GpsXtraDownloader {
    private static final String TAG = "GpsXtraDownloader";
    static final boolean DEBUG = false;
    private Context mContext;
    private String[] mXtraServers;
    private int mNextServerIndex;

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.location.GpsXtraDownloader.doDownload(java.lang.String, boolean, java.lang.String, int):byte[], file: GpsXtraDownloader.class
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
    protected static byte[] doDownload(java.lang.String r0, boolean r1, java.lang.String r2, int r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.location.GpsXtraDownloader.doDownload(java.lang.String, boolean, java.lang.String, int):byte[], file: GpsXtraDownloader.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.location.GpsXtraDownloader.doDownload(java.lang.String, boolean, java.lang.String, int):byte[]");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public GpsXtraDownloader(Context context, Properties properties) {
        this.mContext = context;
        int count = 0;
        String server1 = properties.getProperty("XTRA_SERVER_1");
        String server2 = properties.getProperty("XTRA_SERVER_2");
        String server3 = properties.getProperty("XTRA_SERVER_3");
        count = server1 != null ? 0 + 1 : count;
        count = server2 != null ? count + 1 : count;
        count = server3 != null ? count + 1 : count;
        if (count == 0) {
            Log.e(TAG, "No XTRA servers were specified in the GPS configuration");
            return;
        }
        this.mXtraServers = new String[count];
        int count2 = 0;
        if (server1 != null) {
            count2 = 0 + 1;
            this.mXtraServers[0] = server1;
        }
        if (server2 != null) {
            int i = count2;
            count2++;
            this.mXtraServers[i] = server2;
        }
        if (server3 != null) {
            int i2 = count2;
            count2++;
            this.mXtraServers[i2] = server3;
        }
        Random random = new Random();
        this.mNextServerIndex = random.nextInt(count2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public byte[] downloadXtraData() {
        String proxyHost = Proxy.getHost(this.mContext);
        int proxyPort = Proxy.getPort(this.mContext);
        boolean useProxy = (proxyHost == null || proxyPort == -1) ? false : true;
        byte[] result = null;
        int startIndex = this.mNextServerIndex;
        if (this.mXtraServers == null) {
            return null;
        }
        while (result == null) {
            result = doDownload(this.mXtraServers[this.mNextServerIndex], useProxy, proxyHost, proxyPort);
            this.mNextServerIndex++;
            if (this.mNextServerIndex == this.mXtraServers.length) {
                this.mNextServerIndex = 0;
            }
            if (this.mNextServerIndex == startIndex) {
                break;
            }
        }
        return result;
    }
}