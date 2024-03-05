package com.android.internal.telephony;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Message;
import android.os.PowerManager;
import android.telephony.Rlog;
import com.android.internal.util.State;
import com.android.internal.util.StateMachine;

/* loaded from: WakeLockStateMachine.class */
public abstract class WakeLockStateMachine extends StateMachine {
    protected static final boolean DBG = true;
    private final PowerManager.WakeLock mWakeLock;
    public static final int EVENT_NEW_SMS_MESSAGE = 1;
    protected static final int EVENT_BROADCAST_COMPLETE = 2;
    static final int EVENT_RELEASE_WAKE_LOCK = 3;
    private static final int WAKE_LOCK_TIMEOUT = 3000;
    private final DefaultState mDefaultState;
    private final IdleState mIdleState;
    private final WaitingState mWaitingState;
    protected final BroadcastReceiver mReceiver;

    protected abstract boolean handleSmsMessage(Message message);

    /* JADX INFO: Access modifiers changed from: protected */
    public WakeLockStateMachine(String debugTag, Context context) {
        super(debugTag);
        this.mDefaultState = new DefaultState();
        this.mIdleState = new IdleState();
        this.mWaitingState = new WaitingState();
        this.mReceiver = new BroadcastReceiver() { // from class: com.android.internal.telephony.WakeLockStateMachine.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                WakeLockStateMachine.this.sendMessage(2);
            }
        };
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(1, debugTag);
        this.mWakeLock.acquire();
        addState(this.mDefaultState);
        addState(this.mIdleState, this.mDefaultState);
        addState(this.mWaitingState, this.mDefaultState);
        setInitialState(this.mIdleState);
    }

    public final void dispose() {
        quit();
    }

    @Override // com.android.internal.util.StateMachine
    protected void onQuitting() {
        while (this.mWakeLock.isHeld()) {
            this.mWakeLock.release();
        }
    }

    public final void dispatchSmsMessage(Object obj) {
        sendMessage(1, obj);
    }

    /* loaded from: WakeLockStateMachine$DefaultState.class */
    class DefaultState extends State {
        DefaultState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message msg) {
            String errorText = "processMessage: unhandled message type " + msg.what;
            if (Build.IS_DEBUGGABLE) {
                throw new RuntimeException(errorText);
            }
            WakeLockStateMachine.this.loge(errorText);
            return true;
        }
    }

    /* loaded from: WakeLockStateMachine$IdleState.class */
    class IdleState extends State {
        IdleState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void enter() {
            WakeLockStateMachine.this.sendMessageDelayed(3, 3000L);
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void exit() {
            WakeLockStateMachine.this.mWakeLock.acquire();
            WakeLockStateMachine.this.log("acquired wakelock, leaving Idle state");
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (WakeLockStateMachine.this.handleSmsMessage(msg)) {
                        WakeLockStateMachine.this.transitionTo(WakeLockStateMachine.this.mWaitingState);
                        return true;
                    }
                    return true;
                case 3:
                    WakeLockStateMachine.this.mWakeLock.release();
                    if (WakeLockStateMachine.this.mWakeLock.isHeld()) {
                        WakeLockStateMachine.this.log("mWakeLock is still held after release");
                        return true;
                    }
                    WakeLockStateMachine.this.log("mWakeLock released");
                    return true;
                default:
                    return false;
            }
        }
    }

    /* loaded from: WakeLockStateMachine$WaitingState.class */
    class WaitingState extends State {
        WaitingState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    WakeLockStateMachine.this.log("deferring message until return to idle");
                    WakeLockStateMachine.this.deferMessage(msg);
                    return true;
                case 2:
                    WakeLockStateMachine.this.log("broadcast complete, returning to idle");
                    WakeLockStateMachine.this.transitionTo(WakeLockStateMachine.this.mIdleState);
                    return true;
                case 3:
                    WakeLockStateMachine.this.mWakeLock.release();
                    if (!WakeLockStateMachine.this.mWakeLock.isHeld()) {
                        WakeLockStateMachine.this.loge("mWakeLock released while still in WaitingState!");
                        return true;
                    }
                    return true;
                default:
                    return false;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.util.StateMachine
    public void log(String s) {
        Rlog.d(getName(), s);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.util.StateMachine
    public void loge(String s) {
        Rlog.e(getName(), s);
    }

    @Override // com.android.internal.util.StateMachine
    protected void loge(String s, Throwable e) {
        Rlog.e(getName(), s, e);
    }
}