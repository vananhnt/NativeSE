package com.android.server.wm;

import android.util.Slog;
import com.android.server.wm.WindowManagerService;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* loaded from: ViewServer.class */
class ViewServer implements Runnable {
    public static final int VIEW_SERVER_DEFAULT_PORT = 4939;
    private static final int VIEW_SERVER_MAX_CONNECTIONS = 10;
    private static final String LOG_TAG = "ViewServer";
    private static final String VALUE_PROTOCOL_VERSION = "4";
    private static final String VALUE_SERVER_VERSION = "4";
    private static final String COMMAND_PROTOCOL_VERSION = "PROTOCOL";
    private static final String COMMAND_SERVER_VERSION = "SERVER";
    private static final String COMMAND_WINDOW_MANAGER_LIST = "LIST";
    private static final String COMMAND_WINDOW_MANAGER_AUTOLIST = "AUTOLIST";
    private static final String COMMAND_WINDOW_MANAGER_GET_FOCUS = "GET_FOCUS";
    private ServerSocket mServer;
    private Thread mThread;
    private final WindowManagerService mWindowManager;
    private final int mPort;
    private ExecutorService mThreadPool;

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.ViewServer.writeValue(java.net.Socket, java.lang.String):boolean, file: ViewServer.class
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
    /* JADX INFO: Access modifiers changed from: private */
    public static boolean writeValue(java.net.Socket r0, java.lang.String r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.ViewServer.writeValue(java.net.Socket, java.lang.String):boolean, file: ViewServer.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ViewServer.writeValue(java.net.Socket, java.lang.String):boolean");
    }

    static /* synthetic */ boolean access$000(Socket x0, String x1) {
        return writeValue(x0, x1);
    }

    static /* synthetic */ WindowManagerService access$100(ViewServer x0) {
        return x0.mWindowManager;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ViewServer(WindowManagerService windowManager, int port) {
        this.mWindowManager = windowManager;
        this.mPort = port;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean start() throws IOException {
        if (this.mThread != null) {
            return false;
        }
        this.mServer = new ServerSocket(this.mPort, 10, InetAddress.getLocalHost());
        this.mThread = new Thread(this, "Remote View Server [port=" + this.mPort + "]");
        this.mThreadPool = Executors.newFixedThreadPool(10);
        this.mThread.start();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean stop() {
        if (this.mThread != null) {
            this.mThread.interrupt();
            if (this.mThreadPool != null) {
                try {
                    this.mThreadPool.shutdownNow();
                } catch (SecurityException e) {
                    Slog.w(LOG_TAG, "Could not stop all view server threads");
                }
            }
            this.mThreadPool = null;
            this.mThread = null;
            try {
                this.mServer.close();
                this.mServer = null;
                return true;
            } catch (IOException e2) {
                Slog.w(LOG_TAG, "Could not close the view server");
                return false;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isRunning() {
        return this.mThread != null && this.mThread.isAlive();
    }

    @Override // java.lang.Runnable
    public void run() {
        while (Thread.currentThread() == this.mThread) {
            try {
                Socket client = this.mServer.accept();
                if (this.mThreadPool != null) {
                    this.mThreadPool.submit(new ViewServerWorker(client));
                } else {
                    try {
                        client.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e2) {
                Slog.w(LOG_TAG, "Connection error: ", e2);
            }
        }
    }

    /* loaded from: ViewServer$ViewServerWorker.class */
    class ViewServerWorker implements Runnable, WindowManagerService.WindowChangeListener {
        private Socket mClient;
        private boolean mNeedWindowListUpdate = false;
        private boolean mNeedFocusedWindowUpdate = false;

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.ViewServer.ViewServerWorker.run():void, file: ViewServer$ViewServerWorker.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        @Override // java.lang.Runnable
        public void run() {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.ViewServer.ViewServerWorker.run():void, file: ViewServer$ViewServerWorker.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ViewServer.ViewServerWorker.run():void");
        }

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.ViewServer.ViewServerWorker.windowManagerAutolistLoop():boolean, file: ViewServer$ViewServerWorker.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        private boolean windowManagerAutolistLoop() {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.ViewServer.ViewServerWorker.windowManagerAutolistLoop():boolean, file: ViewServer$ViewServerWorker.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ViewServer.ViewServerWorker.windowManagerAutolistLoop():boolean");
        }

        public ViewServerWorker(Socket client) {
            this.mClient = client;
        }

        @Override // com.android.server.wm.WindowManagerService.WindowChangeListener
        public void windowsChanged() {
            synchronized (this) {
                this.mNeedWindowListUpdate = true;
                notifyAll();
            }
        }

        @Override // com.android.server.wm.WindowManagerService.WindowChangeListener
        public void focusChanged() {
            synchronized (this) {
                this.mNeedFocusedWindowUpdate = true;
                notifyAll();
            }
        }
    }
}