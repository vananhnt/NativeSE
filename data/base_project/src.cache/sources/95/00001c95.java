package com.android.server;

import android.net.LocalSocketAddress;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.LocalLog;
import android.util.Slog;
import android.util.TimedRemoteCaller;
import com.android.server.Watchdog;
import com.google.android.collect.Lists;
import gov.nist.core.Separators;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: NativeDaemonConnector.class */
public final class NativeDaemonConnector implements Runnable, Handler.Callback, Watchdog.Monitor {
    private static final boolean LOGD = false;
    private final String TAG;
    private String mSocket;
    private OutputStream mOutputStream;
    private LocalLog mLocalLog;
    private final ResponseQueue mResponseQueue;
    private INativeDaemonConnectorCallbacks mCallbacks;
    private Handler mCallbackHandler;
    private static final int DEFAULT_TIMEOUT = 60000;
    private static final long WARN_EXECUTE_DELAY_MS = 500;
    private final Object mDaemonLock = new Object();
    private final int BUFFER_SIZE = 4096;
    private AtomicInteger mSequenceNumber = new AtomicInteger(0);

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.NativeDaemonConnector.listenToSocket():void, file: NativeDaemonConnector.class
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
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.NativeDaemonConnector.listenToSocket():void, file: NativeDaemonConnector.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.NativeDaemonConnector.listenToSocket():void");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public NativeDaemonConnector(INativeDaemonConnectorCallbacks callbacks, String socket, int responseQueueSize, String logTag, int maxLogSize) {
        this.mCallbacks = callbacks;
        this.mSocket = socket;
        this.mResponseQueue = new ResponseQueue(responseQueueSize);
        this.TAG = logTag != null ? logTag : "NativeDaemonConnector";
        this.mLocalLog = new LocalLog(maxLogSize);
    }

    @Override // java.lang.Runnable
    public void run() {
        this.mCallbackHandler = new Handler(FgThread.get().getLooper(), this);
        while (true) {
            try {
                listenToSocket();
            } catch (Exception e) {
                loge("Error in NativeDaemonConnector: " + e);
                SystemClock.sleep(TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS);
            }
        }
    }

    @Override // android.os.Handler.Callback
    public boolean handleMessage(Message msg) {
        String event = (String) msg.obj;
        try {
            if (!this.mCallbacks.onEvent(msg.what, event, NativeDaemonEvent.unescapeArgs(event))) {
                log(String.format("Unhandled event '%s'", event));
            }
            return true;
        } catch (Exception e) {
            loge("Error handling '" + event + "': " + e);
            return true;
        }
    }

    private LocalSocketAddress determineSocketAddress() {
        if (this.mSocket.startsWith("__test__") && Build.IS_DEBUGGABLE) {
            return new LocalSocketAddress(this.mSocket);
        }
        return new LocalSocketAddress(this.mSocket, LocalSocketAddress.Namespace.RESERVED);
    }

    /* loaded from: NativeDaemonConnector$SensitiveArg.class */
    public static class SensitiveArg {
        private final Object mArg;

        public SensitiveArg(Object arg) {
            this.mArg = arg;
        }

        public String toString() {
            return String.valueOf(this.mArg);
        }
    }

    static void makeCommand(StringBuilder rawBuilder, StringBuilder logBuilder, int sequenceNumber, String cmd, Object... args) {
        if (cmd.indexOf(0) >= 0) {
            throw new IllegalArgumentException("Unexpected command: " + cmd);
        }
        if (cmd.indexOf(32) >= 0) {
            throw new IllegalArgumentException("Arguments must be separate from command");
        }
        rawBuilder.append(sequenceNumber).append(' ').append(cmd);
        logBuilder.append(sequenceNumber).append(' ').append(cmd);
        for (Object arg : args) {
            String argString = String.valueOf(arg);
            if (argString.indexOf(0) >= 0) {
                throw new IllegalArgumentException("Unexpected argument: " + arg);
            }
            rawBuilder.append(' ');
            logBuilder.append(' ');
            appendEscaped(rawBuilder, argString);
            if (arg instanceof SensitiveArg) {
                logBuilder.append("[scrubbed]");
            } else {
                appendEscaped(logBuilder, argString);
            }
        }
        rawBuilder.append((char) 0);
    }

    public NativeDaemonEvent execute(Command cmd) throws NativeDaemonConnectorException {
        return execute(cmd.mCmd, cmd.mArguments.toArray());
    }

    public NativeDaemonEvent execute(String cmd, Object... args) throws NativeDaemonConnectorException {
        NativeDaemonEvent[] events = executeForList(cmd, args);
        if (events.length != 1) {
            throw new NativeDaemonConnectorException("Expected exactly one response, but received " + events.length);
        }
        return events[0];
    }

    public NativeDaemonEvent[] executeForList(Command cmd) throws NativeDaemonConnectorException {
        return executeForList(cmd.mCmd, cmd.mArguments.toArray());
    }

    public NativeDaemonEvent[] executeForList(String cmd, Object... args) throws NativeDaemonConnectorException {
        return execute(60000, cmd, args);
    }

    public NativeDaemonEvent[] execute(int timeout, String cmd, Object... args) throws NativeDaemonConnectorException {
        NativeDaemonEvent event;
        long startTime = SystemClock.elapsedRealtime();
        ArrayList<NativeDaemonEvent> events = Lists.newArrayList();
        StringBuilder rawBuilder = new StringBuilder();
        StringBuilder logBuilder = new StringBuilder();
        int sequenceNumber = this.mSequenceNumber.incrementAndGet();
        makeCommand(rawBuilder, logBuilder, sequenceNumber, cmd, args);
        String rawCmd = rawBuilder.toString();
        String logCmd = logBuilder.toString();
        log("SND -> {" + logCmd + "}");
        synchronized (this.mDaemonLock) {
            if (this.mOutputStream == null) {
                throw new NativeDaemonConnectorException("missing output stream");
            }
            try {
                this.mOutputStream.write(rawCmd.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new NativeDaemonConnectorException("problem sending command", e);
            }
        }
        do {
            event = this.mResponseQueue.remove(sequenceNumber, timeout, logCmd);
            if (event == null) {
                loge("timed-out waiting for response to " + logCmd);
                throw new NativeDaemonFailureException(logCmd, event);
            }
            log("RMV <- {" + event + "}");
            events.add(event);
        } while (event.isClassContinue());
        long endTime = SystemClock.elapsedRealtime();
        if (endTime - startTime > WARN_EXECUTE_DELAY_MS) {
            loge("NDC Command {" + logCmd + "} took too long (" + (endTime - startTime) + "ms)");
        }
        if (event.isClassClientError()) {
            throw new NativeDaemonArgumentException(logCmd, event);
        }
        if (event.isClassServerError()) {
            throw new NativeDaemonFailureException(logCmd, event);
        }
        return (NativeDaemonEvent[]) events.toArray(new NativeDaemonEvent[events.size()]);
    }

    static void appendEscaped(StringBuilder builder, String arg) {
        boolean hasSpaces = arg.indexOf(32) >= 0;
        if (hasSpaces) {
            builder.append('\"');
        }
        int length = arg.length();
        for (int i = 0; i < length; i++) {
            char c = arg.charAt(i);
            if (c == '\"') {
                builder.append("\\\"");
            } else if (c == '\\') {
                builder.append("\\\\");
            } else {
                builder.append(c);
            }
        }
        if (hasSpaces) {
            builder.append('\"');
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: NativeDaemonConnector$NativeDaemonArgumentException.class */
    public static class NativeDaemonArgumentException extends NativeDaemonConnectorException {
        public NativeDaemonArgumentException(String command, NativeDaemonEvent event) {
            super(command, event);
        }

        @Override // com.android.server.NativeDaemonConnectorException
        public IllegalArgumentException rethrowAsParcelableException() {
            throw new IllegalArgumentException(getMessage(), this);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: NativeDaemonConnector$NativeDaemonFailureException.class */
    public static class NativeDaemonFailureException extends NativeDaemonConnectorException {
        public NativeDaemonFailureException(String command, NativeDaemonEvent event) {
            super(command, event);
        }
    }

    /* loaded from: NativeDaemonConnector$Command.class */
    public static class Command {
        private String mCmd;
        private ArrayList<Object> mArguments = Lists.newArrayList();

        public Command(String cmd, Object... args) {
            this.mCmd = cmd;
            for (Object arg : args) {
                appendArg(arg);
            }
        }

        public Command appendArg(Object arg) {
            this.mArguments.add(arg);
            return this;
        }
    }

    @Override // com.android.server.Watchdog.Monitor
    public void monitor() {
        synchronized (this.mDaemonLock) {
        }
    }

    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        this.mLocalLog.dump(fd, pw, args);
        pw.println();
        this.mResponseQueue.dump(fd, pw, args);
    }

    private void log(String logstring) {
        this.mLocalLog.log(logstring);
    }

    private void loge(String logstring) {
        Slog.e(this.TAG, logstring);
        this.mLocalLog.log(logstring);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: NativeDaemonConnector$ResponseQueue.class */
    public static class ResponseQueue {
        private final LinkedList<PendingCmd> mPendingCmds = new LinkedList<>();
        private int mMaxCount;

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: NativeDaemonConnector$ResponseQueue$PendingCmd.class */
        public static class PendingCmd {
            public final int cmdNum;
            public final String logCmd;
            public BlockingQueue<NativeDaemonEvent> responses = new ArrayBlockingQueue(10);
            public int availableResponseCount;

            public PendingCmd(int cmdNum, String logCmd) {
                this.cmdNum = cmdNum;
                this.logCmd = logCmd;
            }
        }

        ResponseQueue(int maxCount) {
            this.mMaxCount = maxCount;
        }

        public void add(int cmdNum, NativeDaemonEvent response) {
            PendingCmd found = null;
            synchronized (this.mPendingCmds) {
                Iterator i$ = this.mPendingCmds.iterator();
                while (true) {
                    if (!i$.hasNext()) {
                        break;
                    }
                    PendingCmd pendingCmd = i$.next();
                    if (pendingCmd.cmdNum == cmdNum) {
                        found = pendingCmd;
                        break;
                    }
                }
                if (found == null) {
                    while (this.mPendingCmds.size() >= this.mMaxCount) {
                        Slog.e("NativeDaemonConnector.ResponseQueue", "more buffered than allowed: " + this.mPendingCmds.size() + " >= " + this.mMaxCount);
                        PendingCmd pendingCmd2 = this.mPendingCmds.remove();
                        Slog.e("NativeDaemonConnector.ResponseQueue", "Removing request: " + pendingCmd2.logCmd + " (" + pendingCmd2.cmdNum + Separators.RPAREN);
                    }
                    found = new PendingCmd(cmdNum, null);
                    this.mPendingCmds.add(found);
                }
                found.availableResponseCount++;
                if (found.availableResponseCount == 0) {
                    this.mPendingCmds.remove(found);
                }
            }
            try {
                found.responses.put(response);
            } catch (InterruptedException e) {
            }
        }

        public NativeDaemonEvent remove(int cmdNum, int timeoutMs, String logCmd) {
            PendingCmd found = null;
            synchronized (this.mPendingCmds) {
                Iterator i$ = this.mPendingCmds.iterator();
                while (true) {
                    if (!i$.hasNext()) {
                        break;
                    }
                    PendingCmd pendingCmd = i$.next();
                    if (pendingCmd.cmdNum == cmdNum) {
                        found = pendingCmd;
                        break;
                    }
                }
                if (found == null) {
                    found = new PendingCmd(cmdNum, logCmd);
                    this.mPendingCmds.add(found);
                }
                found.availableResponseCount--;
                if (found.availableResponseCount == 0) {
                    this.mPendingCmds.remove(found);
                }
            }
            NativeDaemonEvent result = null;
            try {
                result = found.responses.poll(timeoutMs, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
            }
            if (result == null) {
                Slog.e("NativeDaemonConnector.ResponseQueue", "Timeout waiting for response");
            }
            return result;
        }

        public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
            pw.println("Pending requests:");
            synchronized (this.mPendingCmds) {
                Iterator i$ = this.mPendingCmds.iterator();
                while (i$.hasNext()) {
                    PendingCmd pendingCmd = i$.next();
                    pw.println("  Cmd " + pendingCmd.cmdNum + " - " + pendingCmd.logCmd);
                }
            }
        }
    }
}