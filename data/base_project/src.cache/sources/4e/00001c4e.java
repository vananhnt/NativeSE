package com.android.server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Slog;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/* loaded from: EntropyMixer.class */
public class EntropyMixer extends Binder {
    private static final String TAG = "EntropyMixer";
    private static final int ENTROPY_WHAT = 1;
    private static final int ENTROPY_WRITE_PERIOD = 10800000;
    private static final long START_TIME = System.currentTimeMillis();
    private static final long START_NANOTIME = System.nanoTime();
    private final String randomDevice;
    private final String entropyFile;
    private final Handler mHandler;
    private final BroadcastReceiver mBroadcastReceiver;

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.EntropyMixer.addDeviceSpecificEntropy():void, file: EntropyMixer.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private void addDeviceSpecificEntropy() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.EntropyMixer.addDeviceSpecificEntropy():void, file: EntropyMixer.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.EntropyMixer.addDeviceSpecificEntropy():void");
    }

    public EntropyMixer(Context context) {
        this(context, getSystemDir() + "/entropy.dat", "/dev/urandom");
    }

    public EntropyMixer(Context context, String entropyFile, String randomDevice) {
        this.mHandler = new Handler() { // from class: com.android.server.EntropyMixer.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    EntropyMixer.this.writeEntropy();
                    EntropyMixer.this.scheduleEntropyWriter();
                    return;
                }
                Slog.e(EntropyMixer.TAG, "Will not process invalid message");
            }
        };
        this.mBroadcastReceiver = new BroadcastReceiver() { // from class: com.android.server.EntropyMixer.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                EntropyMixer.this.writeEntropy();
            }
        };
        if (randomDevice == null) {
            throw new NullPointerException("randomDevice");
        }
        if (entropyFile == null) {
            throw new NullPointerException("entropyFile");
        }
        this.randomDevice = randomDevice;
        this.entropyFile = entropyFile;
        loadInitialEntropy();
        addDeviceSpecificEntropy();
        writeEntropy();
        scheduleEntropyWriter();
        IntentFilter broadcastFilter = new IntentFilter(Intent.ACTION_SHUTDOWN);
        broadcastFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        broadcastFilter.addAction(Intent.ACTION_REBOOT);
        context.registerReceiver(this.mBroadcastReceiver, broadcastFilter);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void scheduleEntropyWriter() {
        this.mHandler.removeMessages(1);
        this.mHandler.sendEmptyMessageDelayed(1, 10800000L);
    }

    private void loadInitialEntropy() {
        try {
            RandomBlock.fromFile(this.entropyFile).toFile(this.randomDevice, false);
        } catch (FileNotFoundException e) {
            Slog.w(TAG, "No existing entropy file -- first boot?");
        } catch (IOException e2) {
            Slog.w(TAG, "Failure loading existing entropy file", e2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void writeEntropy() {
        try {
            Slog.i(TAG, "Writing entropy...");
            RandomBlock.fromFile(this.randomDevice).toFile(this.entropyFile, true);
        } catch (IOException e) {
            Slog.w(TAG, "Unable to write entropy", e);
        }
    }

    private static String getSystemDir() {
        File dataDir = Environment.getDataDirectory();
        File systemDir = new File(dataDir, "system");
        systemDir.mkdirs();
        return systemDir.toString();
    }
}