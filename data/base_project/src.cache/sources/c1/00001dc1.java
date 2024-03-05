package com.android.server.am;

import android.app.ApplicationErrorReport;
import android.util.Slog;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import libcore.io.ErrnoException;
import libcore.io.Libcore;
import libcore.io.OsConstants;
import libcore.io.StructTimeval;

/* loaded from: NativeCrashListener.class */
final class NativeCrashListener extends Thread {
    static final String TAG = "NativeCrashListener";
    static final boolean DEBUG = false;
    static final boolean MORE_DEBUG = false;
    static final String DEBUGGERD_SOCKET_PATH = "/data/system/ndebugsocket";
    static final long SOCKET_TIMEOUT_MILLIS = 2000;
    final ActivityManagerService mAm = ActivityManagerService.self();

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.NativeCrashListener.run():void, file: NativeCrashListener.class
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
    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.NativeCrashListener.run():void, file: NativeCrashListener.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.NativeCrashListener.run():void");
    }

    /* loaded from: NativeCrashListener$NativeCrashReporter.class */
    class NativeCrashReporter extends Thread {
        ProcessRecord mApp;
        int mSignal;
        String mCrashReport;

        NativeCrashReporter(ProcessRecord app, int signal, String report) {
            super("NativeCrashReport");
            this.mApp = app;
            this.mSignal = signal;
            this.mCrashReport = report;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            try {
                ApplicationErrorReport.CrashInfo ci = new ApplicationErrorReport.CrashInfo();
                ci.exceptionClassName = "Native crash";
                ci.exceptionMessage = Libcore.os.strsignal(this.mSignal);
                ci.throwFileName = "unknown";
                ci.throwClassName = "unknown";
                ci.throwMethodName = "unknown";
                ci.stackTrace = this.mCrashReport;
                NativeCrashListener.this.mAm.handleApplicationCrashInner("native_crash", this.mApp, this.mApp.processName, ci);
            } catch (Exception e) {
                Slog.e(NativeCrashListener.TAG, "Unable to report native crash", e);
            }
        }
    }

    NativeCrashListener() {
    }

    static int unpackInt(byte[] buf, int offset) {
        int b0 = buf[offset] & 255;
        int b1 = buf[offset + 1] & 255;
        int b2 = buf[offset + 2] & 255;
        int b3 = buf[offset + 3] & 255;
        return (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;
    }

    static int readExactly(FileDescriptor fd, byte[] buffer, int offset, int numBytes) throws ErrnoException {
        int i = 0;
        while (true) {
            int totalRead = i;
            if (numBytes > 0) {
                int n = Libcore.os.read(fd, buffer, offset + totalRead, numBytes);
                if (n <= 0) {
                    return -1;
                }
                numBytes -= n;
                i = totalRead + n;
            } else {
                return totalRead;
            }
        }
    }

    void consumeNativeCrashData(FileDescriptor fd) {
        ProcessRecord pr;
        byte[] buf = new byte[4096];
        ByteArrayOutputStream os = new ByteArrayOutputStream(4096);
        try {
            StructTimeval timeout = StructTimeval.fromMillis(SOCKET_TIMEOUT_MILLIS);
            Libcore.os.setsockoptTimeval(fd, OsConstants.SOL_SOCKET, OsConstants.SO_RCVTIMEO, timeout);
            Libcore.os.setsockoptTimeval(fd, OsConstants.SOL_SOCKET, OsConstants.SO_SNDTIMEO, timeout);
            int headerBytes = readExactly(fd, buf, 0, 8);
            if (headerBytes != 8) {
                Slog.e(TAG, "Unable to read from debuggerd");
                return;
            }
            int pid = unpackInt(buf, 0);
            int signal = unpackInt(buf, 4);
            if (pid > 0) {
                synchronized (this.mAm.mPidsSelfLocked) {
                    pr = (ProcessRecord) this.mAm.mPidsSelfLocked.get(pid);
                }
                if (pr != null) {
                    if (pr.persistent) {
                        return;
                    }
                    while (true) {
                        int bytes = Libcore.os.read(fd, buf, 0, buf.length);
                        if (bytes > 0) {
                            if (buf[bytes - 1] == 0) {
                                os.write(buf, 0, bytes - 1);
                                break;
                            }
                            os.write(buf, 0, bytes);
                        }
                        if (bytes <= 0) {
                            break;
                        }
                    }
                    synchronized (this.mAm) {
                        pr.crashing = true;
                        pr.forceCrashReport = true;
                    }
                    String reportString = new String(os.toByteArray(), "UTF-8");
                    new NativeCrashReporter(pr, signal, reportString).start();
                } else {
                    Slog.w(TAG, "Couldn't find ProcessRecord for pid " + pid);
                }
            } else {
                Slog.e(TAG, "Bogus pid!");
            }
        } catch (Exception e) {
            Slog.e(TAG, "Exception dealing with report", e);
        }
    }
}