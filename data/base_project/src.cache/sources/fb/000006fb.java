package android.media;

import android.content.Context;
import android.net.Uri;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import java.util.LinkedList;

/* loaded from: AsyncPlayer.class */
public class AsyncPlayer {
    private static final int PLAY = 1;
    private static final int STOP = 2;
    private static final boolean mDebug = false;
    private String mTag;
    private Thread mThread;
    private MediaPlayer mPlayer;
    private PowerManager.WakeLock mWakeLock;
    private final LinkedList<Command> mCmdQueue = new LinkedList<>();
    private int mState = 2;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: AsyncPlayer$Command.class */
    public static final class Command {
        int code;
        Context context;
        Uri uri;
        boolean looping;
        int stream;
        long requestTime;

        private Command() {
        }

        public String toString() {
            return "{ code=" + this.code + " looping=" + this.looping + " stream=" + this.stream + " uri=" + this.uri + " }";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startSound(Command cmd) {
        try {
            MediaPlayer player = new MediaPlayer();
            player.setAudioStreamType(cmd.stream);
            player.setDataSource(cmd.context, cmd.uri);
            player.setLooping(cmd.looping);
            player.prepare();
            player.start();
            if (this.mPlayer != null) {
                this.mPlayer.release();
            }
            this.mPlayer = player;
            long delay = SystemClock.uptimeMillis() - cmd.requestTime;
            if (delay > 1000) {
                Log.w(this.mTag, "Notification sound delayed by " + delay + "msecs");
            }
        } catch (Exception e) {
            Log.w(this.mTag, "error loading sound for " + cmd.uri, e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: AsyncPlayer$Thread.class */
    public final class Thread extends java.lang.Thread {
        Thread() {
            super("AsyncPlayer-" + AsyncPlayer.this.mTag);
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            Command cmd;
            while (true) {
                synchronized (AsyncPlayer.this.mCmdQueue) {
                    cmd = (Command) AsyncPlayer.this.mCmdQueue.removeFirst();
                }
                switch (cmd.code) {
                    case 1:
                        AsyncPlayer.this.startSound(cmd);
                        break;
                    case 2:
                        if (AsyncPlayer.this.mPlayer == null) {
                            Log.w(AsyncPlayer.this.mTag, "STOP command without a player");
                            break;
                        } else {
                            long delay = SystemClock.uptimeMillis() - cmd.requestTime;
                            if (delay > 1000) {
                                Log.w(AsyncPlayer.this.mTag, "Notification stop delayed by " + delay + "msecs");
                            }
                            AsyncPlayer.this.mPlayer.stop();
                            AsyncPlayer.this.mPlayer.release();
                            AsyncPlayer.this.mPlayer = null;
                            break;
                        }
                }
                synchronized (AsyncPlayer.this.mCmdQueue) {
                    if (AsyncPlayer.this.mCmdQueue.size() == 0) {
                        AsyncPlayer.this.mThread = null;
                        AsyncPlayer.this.releaseWakeLock();
                        return;
                    }
                }
            }
        }
    }

    public AsyncPlayer(String tag) {
        if (tag != null) {
            this.mTag = tag;
        } else {
            this.mTag = "AsyncPlayer";
        }
    }

    public void play(Context context, Uri uri, boolean looping, int stream) {
        Command cmd = new Command();
        cmd.requestTime = SystemClock.uptimeMillis();
        cmd.code = 1;
        cmd.context = context;
        cmd.uri = uri;
        cmd.looping = looping;
        cmd.stream = stream;
        synchronized (this.mCmdQueue) {
            enqueueLocked(cmd);
            this.mState = 1;
        }
    }

    public void stop() {
        synchronized (this.mCmdQueue) {
            if (this.mState != 2) {
                Command cmd = new Command();
                cmd.requestTime = SystemClock.uptimeMillis();
                cmd.code = 2;
                enqueueLocked(cmd);
                this.mState = 2;
            }
        }
    }

    private void enqueueLocked(Command cmd) {
        this.mCmdQueue.add(cmd);
        if (this.mThread == null) {
            acquireWakeLock();
            this.mThread = new Thread();
            this.mThread.start();
        }
    }

    public void setUsesWakeLock(Context context) {
        if (this.mWakeLock != null || this.mThread != null) {
            throw new RuntimeException("assertion failed mWakeLock=" + this.mWakeLock + " mThread=" + this.mThread);
        }
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(1, this.mTag);
    }

    private void acquireWakeLock() {
        if (this.mWakeLock != null) {
            this.mWakeLock.acquire();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void releaseWakeLock() {
        if (this.mWakeLock != null) {
            this.mWakeLock.release();
        }
    }
}