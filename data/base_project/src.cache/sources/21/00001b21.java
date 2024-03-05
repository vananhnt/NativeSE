package com.android.internal.util;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import gov.nist.core.Separators;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/* loaded from: StateMachine.class */
public class StateMachine {
    private String mName;
    private static final int SM_QUIT_CMD = -1;
    private static final int SM_INIT_CMD = -2;
    public static final boolean HANDLED = true;
    public static final boolean NOT_HANDLED = false;
    private SmHandler mSmHandler;
    private HandlerThread mSmThread;

    /* loaded from: StateMachine$LogRec.class */
    public static class LogRec {
        private StateMachine mSm;
        private long mTime;
        private int mWhat;
        private String mInfo;
        private IState mState;
        private IState mOrgState;
        private IState mDstState;

        LogRec(StateMachine sm, Message msg, String info, IState state, IState orgState, IState transToState) {
            update(sm, msg, info, state, orgState, transToState);
        }

        public void update(StateMachine sm, Message msg, String info, IState state, IState orgState, IState dstState) {
            this.mSm = sm;
            this.mTime = System.currentTimeMillis();
            this.mWhat = msg != null ? msg.what : 0;
            this.mInfo = info;
            this.mState = state;
            this.mOrgState = orgState;
            this.mDstState = dstState;
        }

        public long getTime() {
            return this.mTime;
        }

        public long getWhat() {
            return this.mWhat;
        }

        public String getInfo() {
            return this.mInfo;
        }

        public IState getState() {
            return this.mState;
        }

        public IState getDestState() {
            return this.mDstState;
        }

        public IState getOriginalState() {
            return this.mOrgState;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("time=");
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(this.mTime);
            sb.append(String.format("%tm-%td %tH:%tM:%tS.%tL", c, c, c, c, c, c));
            sb.append(" processed=");
            sb.append(this.mState == null ? "<null>" : this.mState.getName());
            sb.append(" org=");
            sb.append(this.mOrgState == null ? "<null>" : this.mOrgState.getName());
            sb.append(" dest=");
            sb.append(this.mDstState == null ? "<null>" : this.mDstState.getName());
            sb.append(" what=");
            String what = this.mSm != null ? this.mSm.getWhatToString(this.mWhat) : "";
            if (TextUtils.isEmpty(what)) {
                sb.append(this.mWhat);
                sb.append("(0x");
                sb.append(Integer.toHexString(this.mWhat));
                sb.append(Separators.RPAREN);
            } else {
                sb.append(what);
            }
            if (!TextUtils.isEmpty(this.mInfo)) {
                sb.append(Separators.SP);
                sb.append(this.mInfo);
            }
            return sb.toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: StateMachine$LogRecords.class */
    public static class LogRecords {
        private static final int DEFAULT_SIZE = 20;
        private Vector<LogRec> mLogRecVector;
        private int mMaxSize;
        private int mOldestIndex;
        private int mCount;
        private boolean mLogOnlyTransitions;

        private LogRecords() {
            this.mLogRecVector = new Vector<>();
            this.mMaxSize = 20;
            this.mOldestIndex = 0;
            this.mCount = 0;
            this.mLogOnlyTransitions = false;
        }

        synchronized void setSize(int maxSize) {
            this.mMaxSize = maxSize;
            this.mCount = 0;
            this.mLogRecVector.clear();
        }

        synchronized void setLogOnlyTransitions(boolean enable) {
            this.mLogOnlyTransitions = enable;
        }

        synchronized boolean logOnlyTransitions() {
            return this.mLogOnlyTransitions;
        }

        synchronized int size() {
            return this.mLogRecVector.size();
        }

        synchronized int count() {
            return this.mCount;
        }

        synchronized void cleanup() {
            this.mLogRecVector.clear();
        }

        synchronized LogRec get(int index) {
            int nextIndex = this.mOldestIndex + index;
            if (nextIndex >= this.mMaxSize) {
                nextIndex -= this.mMaxSize;
            }
            if (nextIndex >= size()) {
                return null;
            }
            return this.mLogRecVector.get(nextIndex);
        }

        synchronized void add(StateMachine sm, Message msg, String messageInfo, IState state, IState orgState, IState transToState) {
            this.mCount++;
            if (this.mLogRecVector.size() < this.mMaxSize) {
                this.mLogRecVector.add(new LogRec(sm, msg, messageInfo, state, orgState, transToState));
                return;
            }
            LogRec pmi = this.mLogRecVector.get(this.mOldestIndex);
            this.mOldestIndex++;
            if (this.mOldestIndex >= this.mMaxSize) {
                this.mOldestIndex = 0;
            }
            pmi.update(sm, msg, messageInfo, state, orgState, transToState);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: StateMachine$SmHandler.class */
    public static class SmHandler extends Handler {
        private boolean mHasQuit;
        private boolean mDbg;
        private static final Object mSmHandlerObj = new Object();
        private Message mMsg;
        private LogRecords mLogRecords;
        private boolean mIsConstructionCompleted;
        private StateInfo[] mStateStack;
        private int mStateStackTopIndex;
        private StateInfo[] mTempStateStack;
        private int mTempStateStackCount;
        private HaltingState mHaltingState;
        private QuittingState mQuittingState;
        private StateMachine mSm;
        private HashMap<State, StateInfo> mStateInfo;
        private State mInitialState;
        private State mDestState;
        private ArrayList<Message> mDeferredMessages;

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: StateMachine$SmHandler$StateInfo.class */
        public class StateInfo {
            State state;
            StateInfo parentStateInfo;
            boolean active;

            private StateInfo() {
            }

            public String toString() {
                return "state=" + this.state.getName() + ",active=" + this.active + ",parent=" + (this.parentStateInfo == null ? "null" : this.parentStateInfo.state.getName());
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: StateMachine$SmHandler$HaltingState.class */
        public class HaltingState extends State {
            private HaltingState() {
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public boolean processMessage(Message msg) {
                SmHandler.this.mSm.haltedProcessMessage(msg);
                return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: StateMachine$SmHandler$QuittingState.class */
        public class QuittingState extends State {
            private QuittingState() {
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public boolean processMessage(Message msg) {
                return false;
            }
        }

        @Override // android.os.Handler
        public final void handleMessage(Message msg) {
            if (!this.mHasQuit) {
                if (this.mDbg) {
                    this.mSm.log("handleMessage: E msg.what=" + msg.what);
                }
                this.mMsg = msg;
                State msgProcessedState = null;
                if (this.mIsConstructionCompleted) {
                    msgProcessedState = processMsg(msg);
                } else if (!this.mIsConstructionCompleted && this.mMsg.what == -2 && this.mMsg.obj == mSmHandlerObj) {
                    this.mIsConstructionCompleted = true;
                    invokeEnterMethods(0);
                } else {
                    throw new RuntimeException("StateMachine.handleMessage: The start method not called, received msg: " + msg);
                }
                performTransitions(msgProcessedState, msg);
                if (!this.mDbg || this.mSm == null) {
                    return;
                }
                this.mSm.log("handleMessage: X");
            }
        }

        private void performTransitions(State msgProcessedState, Message msg) {
            State orgState = this.mStateStack[this.mStateStackTopIndex].state;
            boolean recordLogMsg = this.mSm.recordLogRec(this.mMsg) && msg.obj != mSmHandlerObj;
            if (this.mLogRecords.logOnlyTransitions()) {
                if (this.mDestState != null) {
                    this.mLogRecords.add(this.mSm, this.mMsg, this.mSm.getLogRecString(this.mMsg), msgProcessedState, orgState, this.mDestState);
                }
            } else if (recordLogMsg) {
                this.mLogRecords.add(this.mSm, this.mMsg, this.mSm.getLogRecString(this.mMsg), msgProcessedState, orgState, this.mDestState);
            }
            State destState = this.mDestState;
            if (destState != null) {
                while (true) {
                    if (this.mDbg) {
                        this.mSm.log("handleMessage: new destination call exit/enter");
                    }
                    StateInfo commonStateInfo = setupTempStateStackWithStatesToEnter(destState);
                    invokeExitMethods(commonStateInfo);
                    int stateStackEnteringIndex = moveTempStateStackToStateStack();
                    invokeEnterMethods(stateStackEnteringIndex);
                    moveDeferredMessageAtFrontOfQueue();
                    if (destState == this.mDestState) {
                        break;
                    }
                    destState = this.mDestState;
                }
                this.mDestState = null;
            }
            if (destState != null) {
                if (destState == this.mQuittingState) {
                    this.mSm.onQuitting();
                    cleanupAfterQuitting();
                } else if (destState == this.mHaltingState) {
                    this.mSm.onHalting();
                }
            }
        }

        private final void cleanupAfterQuitting() {
            if (this.mSm.mSmThread != null) {
                getLooper().quit();
                this.mSm.mSmThread = null;
            }
            this.mSm.mSmHandler = null;
            this.mSm = null;
            this.mMsg = null;
            this.mLogRecords.cleanup();
            this.mStateStack = null;
            this.mTempStateStack = null;
            this.mStateInfo.clear();
            this.mInitialState = null;
            this.mDestState = null;
            this.mDeferredMessages.clear();
            this.mHasQuit = true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final void completeConstruction() {
            if (this.mDbg) {
                this.mSm.log("completeConstruction: E");
            }
            int maxDepth = 0;
            for (StateInfo si : this.mStateInfo.values()) {
                int depth = 0;
                StateInfo i = si;
                while (i != null) {
                    i = i.parentStateInfo;
                    depth++;
                }
                if (maxDepth < depth) {
                    maxDepth = depth;
                }
            }
            if (this.mDbg) {
                this.mSm.log("completeConstruction: maxDepth=" + maxDepth);
            }
            this.mStateStack = new StateInfo[maxDepth];
            this.mTempStateStack = new StateInfo[maxDepth];
            setupInitialStateStack();
            sendMessageAtFrontOfQueue(obtainMessage(-2, mSmHandlerObj));
            if (this.mDbg) {
                this.mSm.log("completeConstruction: X");
            }
        }

        private final State processMsg(Message msg) {
            StateInfo curStateInfo = this.mStateStack[this.mStateStackTopIndex];
            if (this.mDbg) {
                this.mSm.log("processMsg: " + curStateInfo.state.getName());
            }
            if (!isQuit(msg)) {
                while (true) {
                    if (curStateInfo.state.processMessage(msg)) {
                        break;
                    }
                    curStateInfo = curStateInfo.parentStateInfo;
                    if (curStateInfo == null) {
                        this.mSm.unhandledMessage(msg);
                        break;
                    } else if (this.mDbg) {
                        this.mSm.log("processMsg: " + curStateInfo.state.getName());
                    }
                }
            } else {
                transitionTo(this.mQuittingState);
            }
            if (curStateInfo != null) {
                return curStateInfo.state;
            }
            return null;
        }

        private final void invokeExitMethods(StateInfo commonStateInfo) {
            while (this.mStateStackTopIndex >= 0 && this.mStateStack[this.mStateStackTopIndex] != commonStateInfo) {
                State curState = this.mStateStack[this.mStateStackTopIndex].state;
                if (this.mDbg) {
                    this.mSm.log("invokeExitMethods: " + curState.getName());
                }
                curState.exit();
                this.mStateStack[this.mStateStackTopIndex].active = false;
                this.mStateStackTopIndex--;
            }
        }

        private final void invokeEnterMethods(int stateStackEnteringIndex) {
            for (int i = stateStackEnteringIndex; i <= this.mStateStackTopIndex; i++) {
                if (this.mDbg) {
                    this.mSm.log("invokeEnterMethods: " + this.mStateStack[i].state.getName());
                }
                this.mStateStack[i].state.enter();
                this.mStateStack[i].active = true;
            }
        }

        private final void moveDeferredMessageAtFrontOfQueue() {
            for (int i = this.mDeferredMessages.size() - 1; i >= 0; i--) {
                Message curMsg = this.mDeferredMessages.get(i);
                if (this.mDbg) {
                    this.mSm.log("moveDeferredMessageAtFrontOfQueue; what=" + curMsg.what);
                }
                sendMessageAtFrontOfQueue(curMsg);
            }
            this.mDeferredMessages.clear();
        }

        private final int moveTempStateStackToStateStack() {
            int startingIndex = this.mStateStackTopIndex + 1;
            int j = startingIndex;
            for (int i = this.mTempStateStackCount - 1; i >= 0; i--) {
                if (this.mDbg) {
                    this.mSm.log("moveTempStackToStateStack: i=" + i + ",j=" + j);
                }
                this.mStateStack[j] = this.mTempStateStack[i];
                j++;
            }
            this.mStateStackTopIndex = j - 1;
            if (this.mDbg) {
                this.mSm.log("moveTempStackToStateStack: X mStateStackTop=" + this.mStateStackTopIndex + ",startingIndex=" + startingIndex + ",Top=" + this.mStateStack[this.mStateStackTopIndex].state.getName());
            }
            return startingIndex;
        }

        private final StateInfo setupTempStateStackWithStatesToEnter(State destState) {
            this.mTempStateStackCount = 0;
            StateInfo curStateInfo = this.mStateInfo.get(destState);
            do {
                StateInfo[] stateInfoArr = this.mTempStateStack;
                int i = this.mTempStateStackCount;
                this.mTempStateStackCount = i + 1;
                stateInfoArr[i] = curStateInfo;
                curStateInfo = curStateInfo.parentStateInfo;
                if (curStateInfo == null) {
                    break;
                }
            } while (!curStateInfo.active);
            if (this.mDbg) {
                this.mSm.log("setupTempStateStackWithStatesToEnter: X mTempStateStackCount=" + this.mTempStateStackCount + ",curStateInfo: " + curStateInfo);
            }
            return curStateInfo;
        }

        private final void setupInitialStateStack() {
            if (this.mDbg) {
                this.mSm.log("setupInitialStateStack: E mInitialState=" + this.mInitialState.getName());
            }
            StateInfo curStateInfo = this.mStateInfo.get(this.mInitialState);
            this.mTempStateStackCount = 0;
            while (curStateInfo != null) {
                this.mTempStateStack[this.mTempStateStackCount] = curStateInfo;
                curStateInfo = curStateInfo.parentStateInfo;
                this.mTempStateStackCount++;
            }
            this.mStateStackTopIndex = -1;
            moveTempStateStackToStateStack();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final Message getCurrentMessage() {
            return this.mMsg;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final IState getCurrentState() {
            return this.mStateStack[this.mStateStackTopIndex].state;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final StateInfo addState(State state, State parent) {
            if (this.mDbg) {
                this.mSm.log("addStateInternal: E state=" + state.getName() + ",parent=" + (parent == null ? "" : parent.getName()));
            }
            StateInfo parentStateInfo = null;
            if (parent != null) {
                parentStateInfo = this.mStateInfo.get(parent);
                if (parentStateInfo == null) {
                    parentStateInfo = addState(parent, null);
                }
            }
            StateInfo stateInfo = this.mStateInfo.get(state);
            if (stateInfo == null) {
                stateInfo = new StateInfo();
                this.mStateInfo.put(state, stateInfo);
            }
            if (stateInfo.parentStateInfo != null && stateInfo.parentStateInfo != parentStateInfo) {
                throw new RuntimeException("state already added");
            }
            stateInfo.state = state;
            stateInfo.parentStateInfo = parentStateInfo;
            stateInfo.active = false;
            if (this.mDbg) {
                this.mSm.log("addStateInternal: X stateInfo: " + stateInfo);
            }
            return stateInfo;
        }

        private SmHandler(Looper looper, StateMachine sm) {
            super(looper);
            this.mHasQuit = false;
            this.mDbg = false;
            this.mLogRecords = new LogRecords();
            this.mStateStackTopIndex = -1;
            this.mHaltingState = new HaltingState();
            this.mQuittingState = new QuittingState();
            this.mStateInfo = new HashMap<>();
            this.mDeferredMessages = new ArrayList<>();
            this.mSm = sm;
            addState(this.mHaltingState, null);
            addState(this.mQuittingState, null);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final void setInitialState(State initialState) {
            if (this.mDbg) {
                this.mSm.log("setInitialState: initialState=" + initialState.getName());
            }
            this.mInitialState = initialState;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final void transitionTo(IState destState) {
            this.mDestState = (State) destState;
            if (this.mDbg) {
                this.mSm.log("transitionTo: destState=" + this.mDestState.getName());
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final void deferMessage(Message msg) {
            if (this.mDbg) {
                this.mSm.log("deferMessage: msg=" + msg.what);
            }
            Message newMsg = obtainMessage();
            newMsg.copyFrom(msg);
            this.mDeferredMessages.add(newMsg);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final void quit() {
            if (this.mDbg) {
                this.mSm.log("quit:");
            }
            sendMessage(obtainMessage(-1, mSmHandlerObj));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final void quitNow() {
            if (this.mDbg) {
                this.mSm.log("quitNow:");
            }
            sendMessageAtFrontOfQueue(obtainMessage(-1, mSmHandlerObj));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final boolean isQuit(Message msg) {
            return msg.what == -1 && msg.obj == mSmHandlerObj;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final boolean isDbg() {
            return this.mDbg;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final void setDbg(boolean dbg) {
            this.mDbg = dbg;
        }
    }

    private void initStateMachine(String name, Looper looper) {
        this.mName = name;
        this.mSmHandler = new SmHandler(looper, this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public StateMachine(String name) {
        this.mSmThread = new HandlerThread(name);
        this.mSmThread.start();
        Looper looper = this.mSmThread.getLooper();
        initStateMachine(name, looper);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public StateMachine(String name, Looper looper) {
        initStateMachine(name, looper);
    }

    protected StateMachine(String name, Handler handler) {
        initStateMachine(name, handler.getLooper());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void addState(State state, State parent) {
        this.mSmHandler.addState(state, parent);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void addState(State state) {
        this.mSmHandler.addState(state, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void setInitialState(State initialState) {
        this.mSmHandler.setInitialState(initialState);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final Message getCurrentMessage() {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return null;
        }
        return smh.getCurrentMessage();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final IState getCurrentState() {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return null;
        }
        return smh.getCurrentState();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void transitionTo(IState destState) {
        this.mSmHandler.transitionTo(destState);
    }

    protected final void transitionToHaltingState() {
        this.mSmHandler.transitionTo(this.mSmHandler.mHaltingState);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void deferMessage(Message msg) {
        this.mSmHandler.deferMessage(msg);
    }

    protected void unhandledMessage(Message msg) {
        if (this.mSmHandler.mDbg) {
            loge(" - unhandledMessage: msg.what=" + msg.what);
        }
    }

    protected void haltedProcessMessage(Message msg) {
    }

    protected void onHalting() {
    }

    protected void onQuitting() {
    }

    public final String getName() {
        return this.mName;
    }

    public final void setLogRecSize(int maxSize) {
        this.mSmHandler.mLogRecords.setSize(maxSize);
    }

    public final void setLogOnlyTransitions(boolean enable) {
        this.mSmHandler.mLogRecords.setLogOnlyTransitions(enable);
    }

    public final int getLogRecSize() {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return 0;
        }
        return smh.mLogRecords.size();
    }

    public final int getLogRecCount() {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return 0;
        }
        return smh.mLogRecords.count();
    }

    public final LogRec getLogRec(int index) {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return null;
        }
        return smh.mLogRecords.get(index);
    }

    public final Collection<LogRec> copyLogRecs() {
        Vector<LogRec> vlr = new Vector<>();
        SmHandler smh = this.mSmHandler;
        if (smh != null) {
            Iterator i$ = smh.mLogRecords.mLogRecVector.iterator();
            while (i$.hasNext()) {
                LogRec lr = (LogRec) i$.next();
                vlr.add(lr);
            }
        }
        return vlr;
    }

    protected void addLogRec(String string) {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return;
        }
        smh.mLogRecords.add(this, smh.getCurrentMessage(), string, smh.getCurrentState(), smh.mStateStack[smh.mStateStackTopIndex].state, smh.mDestState);
    }

    protected boolean recordLogRec(Message msg) {
        return true;
    }

    protected String getLogRecString(Message msg) {
        return "";
    }

    protected String getWhatToString(int what) {
        return null;
    }

    public final Handler getHandler() {
        return this.mSmHandler;
    }

    public final Message obtainMessage() {
        return Message.obtain(this.mSmHandler);
    }

    public final Message obtainMessage(int what) {
        return Message.obtain(this.mSmHandler, what);
    }

    public final Message obtainMessage(int what, Object obj) {
        return Message.obtain(this.mSmHandler, what, obj);
    }

    public final Message obtainMessage(int what, int arg1) {
        return Message.obtain(this.mSmHandler, what, arg1, 0);
    }

    public final Message obtainMessage(int what, int arg1, int arg2) {
        return Message.obtain(this.mSmHandler, what, arg1, arg2);
    }

    public final Message obtainMessage(int what, int arg1, int arg2, Object obj) {
        return Message.obtain(this.mSmHandler, what, arg1, arg2, obj);
    }

    public final void sendMessage(int what) {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return;
        }
        smh.sendMessage(obtainMessage(what));
    }

    public final void sendMessage(int what, Object obj) {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return;
        }
        smh.sendMessage(obtainMessage(what, obj));
    }

    public final void sendMessage(int what, int arg1) {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return;
        }
        smh.sendMessage(obtainMessage(what, arg1));
    }

    public final void sendMessage(int what, int arg1, int arg2) {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return;
        }
        smh.sendMessage(obtainMessage(what, arg1, arg2));
    }

    public final void sendMessage(int what, int arg1, int arg2, Object obj) {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return;
        }
        smh.sendMessage(obtainMessage(what, arg1, arg2, obj));
    }

    public final void sendMessage(Message msg) {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return;
        }
        smh.sendMessage(msg);
    }

    public final void sendMessageDelayed(int what, long delayMillis) {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return;
        }
        smh.sendMessageDelayed(obtainMessage(what), delayMillis);
    }

    public final void sendMessageDelayed(int what, Object obj, long delayMillis) {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return;
        }
        smh.sendMessageDelayed(obtainMessage(what, obj), delayMillis);
    }

    public final void sendMessageDelayed(int what, int arg1, long delayMillis) {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return;
        }
        smh.sendMessageDelayed(obtainMessage(what, arg1), delayMillis);
    }

    public final void sendMessageDelayed(int what, int arg1, int arg2, long delayMillis) {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return;
        }
        smh.sendMessageDelayed(obtainMessage(what, arg1, arg2), delayMillis);
    }

    public final void sendMessageDelayed(int what, int arg1, int arg2, Object obj, long delayMillis) {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return;
        }
        smh.sendMessageDelayed(obtainMessage(what, arg1, arg2, obj), delayMillis);
    }

    public final void sendMessageDelayed(Message msg, long delayMillis) {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return;
        }
        smh.sendMessageDelayed(msg, delayMillis);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void sendMessageAtFrontOfQueue(int what) {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return;
        }
        smh.sendMessageAtFrontOfQueue(obtainMessage(what));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void sendMessageAtFrontOfQueue(int what, Object obj) {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return;
        }
        smh.sendMessageAtFrontOfQueue(obtainMessage(what, obj));
    }

    protected final void sendMessageAtFrontOfQueue(int what, int arg1) {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return;
        }
        smh.sendMessageAtFrontOfQueue(obtainMessage(what, arg1));
    }

    protected final void sendMessageAtFrontOfQueue(int what, int arg1, int arg2) {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return;
        }
        smh.sendMessageAtFrontOfQueue(obtainMessage(what, arg1, arg2));
    }

    protected final void sendMessageAtFrontOfQueue(int what, int arg1, int arg2, Object obj) {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return;
        }
        smh.sendMessageAtFrontOfQueue(obtainMessage(what, arg1, arg2, obj));
    }

    protected final void sendMessageAtFrontOfQueue(Message msg) {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return;
        }
        smh.sendMessageAtFrontOfQueue(msg);
    }

    protected final void removeMessages(int what) {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return;
        }
        smh.removeMessages(what);
    }

    protected final boolean isQuit(Message msg) {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return msg.what == -1;
        }
        return smh.isQuit(msg);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void quit() {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return;
        }
        smh.quit();
    }

    protected final void quitNow() {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return;
        }
        smh.quitNow();
    }

    public boolean isDbg() {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return false;
        }
        return smh.isDbg();
    }

    public void setDbg(boolean dbg) {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return;
        }
        smh.setDbg(dbg);
    }

    public void start() {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return;
        }
        smh.completeConstruction();
    }

    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println(getName() + Separators.COLON);
        pw.println(" total records=" + getLogRecCount());
        for (int i = 0; i < getLogRecSize(); i++) {
            pw.printf(" rec[%d]: %s\n", Integer.valueOf(i), getLogRec(i).toString());
            pw.flush();
        }
        pw.println("curState=" + getCurrentState().getName());
    }

    protected void logAndAddLogRec(String s) {
        addLogRec(s);
        log(s);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void log(String s) {
        Log.d(this.mName, s);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void logd(String s) {
        Log.d(this.mName, s);
    }

    protected void logv(String s) {
        Log.v(this.mName, s);
    }

    protected void logi(String s) {
        Log.i(this.mName, s);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void logw(String s) {
        Log.w(this.mName, s);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void loge(String s) {
        Log.e(this.mName, s);
    }

    protected void loge(String s, Throwable e) {
        Log.e(this.mName, s, e);
    }
}