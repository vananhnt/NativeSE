package com.android.server.usb;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.LocalSocket;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Base64;
import android.util.Slog;
import com.android.server.FgThread;
import gov.nist.core.Separators;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.MessageDigest;

/* loaded from: UsbDebuggingManager.class */
public class UsbDebuggingManager implements Runnable {
    private static final String TAG = "UsbDebuggingManager";
    private static final boolean DEBUG = false;
    private final Context mContext;
    private Thread mThread;
    private String mFingerprints;
    private final String ADBD_SOCKET = "adbd";
    private final String ADB_DIRECTORY = "misc/adb";
    private final String ADB_KEYS_FILE = "adb_keys";
    private final int BUFFER_SIZE = 4096;
    private boolean mAdbEnabled = false;
    private LocalSocket mSocket = null;
    private OutputStream mOutputStream = null;
    private final Handler mHandler = new UsbDebuggingHandler(FgThread.get().getLooper());

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.usb.UsbDebuggingManager.listenToSocket():void, file: UsbDebuggingManager.class
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
    private void listenToSocket() throws java.io.IOException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.usb.UsbDebuggingManager.listenToSocket():void, file: UsbDebuggingManager.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.usb.UsbDebuggingManager.listenToSocket():void");
    }

    public UsbDebuggingManager(Context context) {
        this.mContext = context;
    }

    @Override // java.lang.Runnable
    public void run() {
        while (this.mAdbEnabled) {
            try {
                listenToSocket();
            } catch (Exception e) {
                SystemClock.sleep(1000L);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void closeSocket() {
        try {
            this.mOutputStream.close();
        } catch (IOException e) {
            Slog.e(TAG, "Failed closing output stream: " + e);
        }
        try {
            this.mSocket.close();
        } catch (IOException ex) {
            Slog.e(TAG, "Failed closing socket: " + ex);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendResponse(String msg) {
        if (this.mOutputStream != null) {
            try {
                this.mOutputStream.write(msg.getBytes());
            } catch (IOException ex) {
                Slog.e(TAG, "Failed to write response:", ex);
            }
        }
    }

    /* loaded from: UsbDebuggingManager$UsbDebuggingHandler.class */
    class UsbDebuggingHandler extends Handler {
        private static final int MESSAGE_ADB_ENABLED = 1;
        private static final int MESSAGE_ADB_DISABLED = 2;
        private static final int MESSAGE_ADB_ALLOW = 3;
        private static final int MESSAGE_ADB_DENY = 4;
        private static final int MESSAGE_ADB_CONFIRM = 5;
        private static final int MESSAGE_ADB_CLEAR = 6;

        public UsbDebuggingHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (!UsbDebuggingManager.this.mAdbEnabled) {
                        UsbDebuggingManager.this.mAdbEnabled = true;
                        UsbDebuggingManager.this.mThread = new Thread(UsbDebuggingManager.this, UsbDebuggingManager.TAG);
                        UsbDebuggingManager.this.mThread.start();
                        return;
                    }
                    return;
                case 2:
                    if (UsbDebuggingManager.this.mAdbEnabled) {
                        UsbDebuggingManager.this.mAdbEnabled = false;
                        UsbDebuggingManager.this.closeSocket();
                        try {
                            UsbDebuggingManager.this.mThread.join();
                        } catch (Exception e) {
                        }
                        UsbDebuggingManager.this.mThread = null;
                        UsbDebuggingManager.this.mOutputStream = null;
                        UsbDebuggingManager.this.mSocket = null;
                        return;
                    }
                    return;
                case 3:
                    String key = (String) msg.obj;
                    String fingerprints = UsbDebuggingManager.this.getFingerprints(key);
                    if (!fingerprints.equals(UsbDebuggingManager.this.mFingerprints)) {
                        Slog.e(UsbDebuggingManager.TAG, "Fingerprints do not match. Got " + fingerprints + ", expected " + UsbDebuggingManager.this.mFingerprints);
                        return;
                    }
                    if (msg.arg1 == 1) {
                        UsbDebuggingManager.this.writeKey(key);
                    }
                    UsbDebuggingManager.this.sendResponse("OK");
                    return;
                case 4:
                    UsbDebuggingManager.this.sendResponse("NO");
                    return;
                case 5:
                    String key2 = (String) msg.obj;
                    UsbDebuggingManager.this.mFingerprints = UsbDebuggingManager.this.getFingerprints(key2);
                    UsbDebuggingManager.this.showConfirmationDialog(key2, UsbDebuggingManager.this.mFingerprints);
                    return;
                case 6:
                    UsbDebuggingManager.this.deleteKeyFile();
                    return;
                default:
                    return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getFingerprints(String key) {
        StringBuilder sb = new StringBuilder();
        try {
            MessageDigest digester = MessageDigest.getInstance("MD5");
            byte[] base64_data = key.split("\\s+")[0].getBytes();
            byte[] digest = digester.digest(Base64.decode(base64_data, 0));
            for (int i = 0; i < digest.length; i++) {
                sb.append("0123456789ABCDEF".charAt((digest[i] >> 4) & 15));
                sb.append("0123456789ABCDEF".charAt(digest[i] & 15));
                if (i < digest.length - 1) {
                    sb.append(Separators.COLON);
                }
            }
            return sb.toString();
        } catch (Exception ex) {
            Slog.e(TAG, "Error getting digester: " + ex);
            return "";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showConfirmationDialog(String key, String fingerprints) {
        Intent dialogIntent = new Intent();
        dialogIntent.setClassName("com.android.systemui", "com.android.systemui.usb.UsbDebuggingActivity");
        dialogIntent.addFlags(268435456);
        dialogIntent.putExtra("key", key);
        dialogIntent.putExtra("fingerprints", fingerprints);
        try {
            this.mContext.startActivity(dialogIntent);
        } catch (ActivityNotFoundException e) {
            Slog.e(TAG, "unable to start UsbDebuggingActivity");
        }
    }

    private File getUserKeyFile() {
        File dataDir = Environment.getDataDirectory();
        File adbDir = new File(dataDir, "misc/adb");
        if (!adbDir.exists()) {
            Slog.e(TAG, "ADB data directory does not exist");
            return null;
        }
        return new File(adbDir, "adb_keys");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void writeKey(String key) {
        try {
            File keyFile = getUserKeyFile();
            if (keyFile == null) {
                return;
            }
            if (!keyFile.exists()) {
                keyFile.createNewFile();
                FileUtils.setPermissions(keyFile.toString(), 416, -1, -1);
            }
            FileOutputStream fo = new FileOutputStream(keyFile, true);
            fo.write(key.getBytes());
            fo.write(10);
            fo.close();
        } catch (IOException ex) {
            Slog.e(TAG, "Error writing key:" + ex);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void deleteKeyFile() {
        File keyFile = getUserKeyFile();
        if (keyFile != null) {
            keyFile.delete();
        }
    }

    public void setAdbEnabled(boolean enabled) {
        this.mHandler.sendEmptyMessage(enabled ? 1 : 2);
    }

    public void allowUsbDebugging(boolean alwaysAllow, String publicKey) {
        Message msg = this.mHandler.obtainMessage(3);
        msg.arg1 = alwaysAllow ? 1 : 0;
        msg.obj = publicKey;
        this.mHandler.sendMessage(msg);
    }

    public void denyUsbDebugging() {
        this.mHandler.sendEmptyMessage(4);
    }

    public void clearUsbDebuggingKeys() {
        this.mHandler.sendEmptyMessage(6);
    }

    public void dump(FileDescriptor fd, PrintWriter pw) {
        pw.println("  USB Debugging State:");
        pw.println("    Connected to adbd: " + (this.mOutputStream != null));
        pw.println("    Last key received: " + this.mFingerprints);
        pw.println("    User keys:");
        try {
            pw.println(FileUtils.readTextFile(new File("/data/misc/adb/adb_keys"), 0, null));
        } catch (IOException e) {
            pw.println("IOException: " + e);
        }
        pw.println("    System keys:");
        try {
            pw.println(FileUtils.readTextFile(new File("/adb_keys"), 0, null));
        } catch (IOException e2) {
            pw.println("IOException: " + e2);
        }
    }
}