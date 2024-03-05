package com.android.server.am;

import android.app.IStopUserCallback;
import android.os.UserHandle;
import java.io.PrintWriter;
import java.util.ArrayList;

/* loaded from: UserStartedState.class */
public final class UserStartedState {
    public static final int STATE_BOOTING = 0;
    public static final int STATE_RUNNING = 1;
    public static final int STATE_STOPPING = 2;
    public static final int STATE_SHUTDOWN = 3;
    public final UserHandle mHandle;
    public final ArrayList<IStopUserCallback> mStopCallbacks = new ArrayList<>();
    public int mState = 0;
    public boolean switching;
    public boolean initializing;

    public UserStartedState(UserHandle handle, boolean initial) {
        this.mHandle = handle;
    }

    void dump(String prefix, PrintWriter pw) {
        pw.print(prefix);
        pw.print("mState=");
        switch (this.mState) {
            case 0:
                pw.print("BOOTING");
                break;
            case 1:
                pw.print("RUNNING");
                break;
            case 2:
                pw.print("STOPPING");
                break;
            case 3:
                pw.print("SHUTDOWN");
                break;
            default:
                pw.print(this.mState);
                break;
        }
        if (this.switching) {
            pw.print(" SWITCHING");
        }
        if (this.initializing) {
            pw.print(" INITIALIZING");
        }
        pw.println();
    }
}