package com.android.internal.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Slog;
import java.util.Stack;

/* loaded from: AsyncChannel.class */
public class AsyncChannel {
    private static final String TAG = "AsyncChannel";
    private static final boolean DBG = false;
    private static final int BASE = 69632;
    public static final int CMD_CHANNEL_HALF_CONNECTED = 69632;
    public static final int CMD_CHANNEL_FULL_CONNECTION = 69633;
    public static final int CMD_CHANNEL_FULLY_CONNECTED = 69634;
    public static final int CMD_CHANNEL_DISCONNECT = 69635;
    public static final int CMD_CHANNEL_DISCONNECTED = 69636;
    private static final int CMD_TO_STRING_COUNT = 5;
    private static String[] sCmdToString = new String[5];
    public static final int STATUS_SUCCESSFUL = 0;
    public static final int STATUS_BINDING_UNSUCCESSFUL = 1;
    public static final int STATUS_SEND_UNSUCCESSFUL = 2;
    public static final int STATUS_FULL_CONNECTION_REFUSED_ALREADY_CONNECTED = 3;
    public static final int STATUS_REMOTE_DISCONNECTION = 4;
    private AsyncChannelConnection mConnection;
    private Context mSrcContext;
    private Handler mSrcHandler;
    private Messenger mSrcMessenger;
    private Messenger mDstMessenger;
    private DeathMonitor mDeathMonitor;

    static {
        sCmdToString[0] = "CMD_CHANNEL_HALF_CONNECTED";
        sCmdToString[1] = "CMD_CHANNEL_FULL_CONNECTION";
        sCmdToString[2] = "CMD_CHANNEL_FULLY_CONNECTED";
        sCmdToString[3] = "CMD_CHANNEL_DISCONNECT";
        sCmdToString[4] = "CMD_CHANNEL_DISCONNECTED";
    }

    protected static String cmdToString(int cmd) {
        int cmd2 = cmd - 69632;
        if (cmd2 >= 0 && cmd2 < sCmdToString.length) {
            return sCmdToString[cmd2];
        }
        return null;
    }

    public int connectSrcHandlerToPackageSync(Context srcContext, Handler srcHandler, String dstPackageName, String dstClassName) {
        this.mConnection = new AsyncChannelConnection();
        this.mSrcContext = srcContext;
        this.mSrcHandler = srcHandler;
        this.mSrcMessenger = new Messenger(srcHandler);
        this.mDstMessenger = null;
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName(dstPackageName, dstClassName);
        boolean result = srcContext.bindService(intent, this.mConnection, 1);
        return result ? 0 : 1;
    }

    public int connectSync(Context srcContext, Handler srcHandler, Messenger dstMessenger) {
        connected(srcContext, srcHandler, dstMessenger);
        return 0;
    }

    public int connectSync(Context srcContext, Handler srcHandler, Handler dstHandler) {
        return connectSync(srcContext, srcHandler, new Messenger(dstHandler));
    }

    public int fullyConnectSync(Context srcContext, Handler srcHandler, Handler dstHandler) {
        int status = connectSync(srcContext, srcHandler, dstHandler);
        if (status == 0) {
            Message response = sendMessageSynchronously(CMD_CHANNEL_FULL_CONNECTION);
            status = response.arg1;
        }
        return status;
    }

    public void connect(Context srcContext, Handler srcHandler, String dstPackageName, String dstClassName) {
        new Thread(new Runnable(srcContext, srcHandler, dstPackageName, dstClassName) { // from class: com.android.internal.util.AsyncChannel.1ConnectAsync
            Context mSrcCtx;
            Handler mSrcHdlr;
            String mDstPackageName;
            String mDstClassName;

            {
                this.mSrcCtx = srcContext;
                this.mSrcHdlr = srcHandler;
                this.mDstPackageName = dstPackageName;
                this.mDstClassName = dstClassName;
            }

            @Override // java.lang.Runnable
            public void run() {
                int result = AsyncChannel.this.connectSrcHandlerToPackageSync(this.mSrcCtx, this.mSrcHdlr, this.mDstPackageName, this.mDstClassName);
                AsyncChannel.this.replyHalfConnected(result);
            }
        }).start();
    }

    public void connect(Context srcContext, Handler srcHandler, Class<?> klass) {
        connect(srcContext, srcHandler, klass.getPackage().getName(), klass.getName());
    }

    public void connect(Context srcContext, Handler srcHandler, Messenger dstMessenger) {
        connected(srcContext, srcHandler, dstMessenger);
        replyHalfConnected(0);
    }

    public void connected(Context srcContext, Handler srcHandler, Messenger dstMessenger) {
        this.mSrcContext = srcContext;
        this.mSrcHandler = srcHandler;
        this.mSrcMessenger = new Messenger(this.mSrcHandler);
        this.mDstMessenger = dstMessenger;
    }

    public void connect(Context srcContext, Handler srcHandler, Handler dstHandler) {
        connect(srcContext, srcHandler, new Messenger(dstHandler));
    }

    public void connect(AsyncService srcAsyncService, Messenger dstMessenger) {
        connect(srcAsyncService, srcAsyncService.getHandler(), dstMessenger);
    }

    public void disconnected() {
        this.mSrcContext = null;
        this.mSrcHandler = null;
        this.mSrcMessenger = null;
        this.mDstMessenger = null;
        this.mDeathMonitor = null;
        this.mConnection = null;
    }

    public void disconnect() {
        if (this.mConnection != null && this.mSrcContext != null) {
            this.mSrcContext.unbindService(this.mConnection);
        }
        try {
            Message msg = Message.obtain();
            msg.what = CMD_CHANNEL_DISCONNECTED;
            msg.replyTo = this.mSrcMessenger;
            this.mDstMessenger.send(msg);
        } catch (Exception e) {
        }
        if (this.mSrcHandler != null) {
            replyDisconnected(0);
        }
        if (this.mConnection == null && this.mDstMessenger != null && this.mDeathMonitor != null) {
            this.mDstMessenger.getBinder().unlinkToDeath(this.mDeathMonitor, 0);
        }
    }

    public void sendMessage(Message msg) {
        msg.replyTo = this.mSrcMessenger;
        try {
            this.mDstMessenger.send(msg);
        } catch (RemoteException e) {
            replyDisconnected(2);
        }
    }

    public void sendMessage(int what) {
        Message msg = Message.obtain();
        msg.what = what;
        sendMessage(msg);
    }

    public void sendMessage(int what, int arg1) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.arg1 = arg1;
        sendMessage(msg);
    }

    public void sendMessage(int what, int arg1, int arg2) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        sendMessage(msg);
    }

    public void sendMessage(int what, int arg1, int arg2, Object obj) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        msg.obj = obj;
        sendMessage(msg);
    }

    public void sendMessage(int what, Object obj) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = obj;
        sendMessage(msg);
    }

    public void replyToMessage(Message srcMsg, Message dstMsg) {
        try {
            dstMsg.replyTo = this.mSrcMessenger;
            srcMsg.replyTo.send(dstMsg);
        } catch (RemoteException e) {
            log("TODO: handle replyToMessage RemoteException" + e);
            e.printStackTrace();
        }
    }

    public void replyToMessage(Message srcMsg, int what) {
        Message msg = Message.obtain();
        msg.what = what;
        replyToMessage(srcMsg, msg);
    }

    public void replyToMessage(Message srcMsg, int what, int arg1) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.arg1 = arg1;
        replyToMessage(srcMsg, msg);
    }

    public void replyToMessage(Message srcMsg, int what, int arg1, int arg2) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        replyToMessage(srcMsg, msg);
    }

    public void replyToMessage(Message srcMsg, int what, int arg1, int arg2, Object obj) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        msg.obj = obj;
        replyToMessage(srcMsg, msg);
    }

    public void replyToMessage(Message srcMsg, int what, Object obj) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = obj;
        replyToMessage(srcMsg, msg);
    }

    public Message sendMessageSynchronously(Message msg) {
        Message resultMsg = SyncMessenger.sendMessageSynchronously(this.mDstMessenger, msg);
        return resultMsg;
    }

    public Message sendMessageSynchronously(int what) {
        Message msg = Message.obtain();
        msg.what = what;
        Message resultMsg = sendMessageSynchronously(msg);
        return resultMsg;
    }

    public Message sendMessageSynchronously(int what, int arg1) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.arg1 = arg1;
        Message resultMsg = sendMessageSynchronously(msg);
        return resultMsg;
    }

    public Message sendMessageSynchronously(int what, int arg1, int arg2) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        Message resultMsg = sendMessageSynchronously(msg);
        return resultMsg;
    }

    public Message sendMessageSynchronously(int what, int arg1, int arg2, Object obj) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        msg.obj = obj;
        Message resultMsg = sendMessageSynchronously(msg);
        return resultMsg;
    }

    public Message sendMessageSynchronously(int what, Object obj) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = obj;
        Message resultMsg = sendMessageSynchronously(msg);
        return resultMsg;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: AsyncChannel$SyncMessenger.class */
    public static class SyncMessenger {
        private static Stack<SyncMessenger> sStack = new Stack<>();
        private static int sCount = 0;
        private HandlerThread mHandlerThread;
        private SyncHandler mHandler;
        private Messenger mMessenger;

        private SyncMessenger() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: AsyncChannel$SyncMessenger$SyncHandler.class */
        public class SyncHandler extends Handler {
            private Object mLockObject;
            private Message mResultMsg;

            private SyncHandler(Looper looper) {
                super(looper);
                this.mLockObject = new Object();
            }

            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                this.mResultMsg = Message.obtain();
                this.mResultMsg.copyFrom(msg);
                synchronized (this.mLockObject) {
                    this.mLockObject.notify();
                }
            }
        }

        private static SyncMessenger obtain() {
            SyncMessenger sm;
            synchronized (sStack) {
                if (sStack.isEmpty()) {
                    sm = new SyncMessenger();
                    StringBuilder append = new StringBuilder().append("SyncHandler-");
                    int i = sCount;
                    sCount = i + 1;
                    sm.mHandlerThread = new HandlerThread(append.append(i).toString());
                    sm.mHandlerThread.start();
                    sm.getClass();
                    sm.mHandler = new SyncHandler(sm.mHandlerThread.getLooper());
                    sm.mMessenger = new Messenger(sm.mHandler);
                } else {
                    sm = sStack.pop();
                }
            }
            return sm;
        }

        private void recycle() {
            synchronized (sStack) {
                sStack.push(this);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static Message sendMessageSynchronously(Messenger dstMessenger, Message msg) {
            SyncMessenger sm = obtain();
            try {
                if (dstMessenger == null || msg == null) {
                    sm.mHandler.mResultMsg = null;
                } else {
                    msg.replyTo = sm.mMessenger;
                    synchronized (sm.mHandler.mLockObject) {
                        dstMessenger.send(msg);
                        sm.mHandler.mLockObject.wait();
                    }
                }
            } catch (RemoteException e) {
                sm.mHandler.mResultMsg = null;
            } catch (InterruptedException e2) {
                sm.mHandler.mResultMsg = null;
            }
            Message resultMsg = sm.mHandler.mResultMsg;
            sm.recycle();
            return resultMsg;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void replyHalfConnected(int status) {
        Message msg = this.mSrcHandler.obtainMessage(69632);
        msg.arg1 = status;
        msg.obj = this;
        msg.replyTo = this.mDstMessenger;
        if (this.mConnection == null) {
            this.mDeathMonitor = new DeathMonitor();
            try {
                this.mDstMessenger.getBinder().linkToDeath(this.mDeathMonitor, 0);
            } catch (RemoteException e) {
                this.mDeathMonitor = null;
                msg.arg1 = 1;
            }
        }
        this.mSrcHandler.sendMessage(msg);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void replyDisconnected(int status) {
        Message msg = this.mSrcHandler.obtainMessage(CMD_CHANNEL_DISCONNECTED);
        msg.arg1 = status;
        msg.obj = this;
        msg.replyTo = this.mDstMessenger;
        this.mSrcHandler.sendMessage(msg);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: AsyncChannel$AsyncChannelConnection.class */
    public class AsyncChannelConnection implements ServiceConnection {
        AsyncChannelConnection() {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName className, IBinder service) {
            AsyncChannel.this.mDstMessenger = new Messenger(service);
            AsyncChannel.this.replyHalfConnected(0);
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName className) {
            AsyncChannel.this.replyDisconnected(0);
        }
    }

    private static void log(String s) {
        Slog.d(TAG, s);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: AsyncChannel$DeathMonitor.class */
    public final class DeathMonitor implements IBinder.DeathRecipient {
        DeathMonitor() {
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            AsyncChannel.this.replyDisconnected(4);
        }
    }
}