package android.view.inputmethod;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ServiceManager;
import android.os.Trace;
import android.text.style.SuggestionSpan;
import android.util.Log;
import android.util.Pools;
import android.util.PrintWriterPrinter;
import android.util.Printer;
import android.util.SparseArray;
import android.view.InputChannel;
import android.view.InputEvent;
import android.view.InputEventSender;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewRootImpl;
import com.android.internal.os.SomeArgs;
import com.android.internal.view.IInputConnectionWrapper;
import com.android.internal.view.IInputContext;
import com.android.internal.view.IInputMethodClient;
import com.android.internal.view.IInputMethodManager;
import com.android.internal.view.IInputMethodSession;
import com.android.internal.view.InputBindResult;
import gov.nist.core.Separators;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/* loaded from: InputMethodManager.class */
public final class InputMethodManager {
    static final boolean DEBUG = false;
    static final String TAG = "InputMethodManager";
    static final String PENDING_EVENT_COUNTER = "aq:imm";
    static InputMethodManager sInstance;
    public static final int CONTROL_WINDOW_VIEW_HAS_FOCUS = 1;
    public static final int CONTROL_WINDOW_IS_TEXT_EDITOR = 2;
    public static final int CONTROL_WINDOW_FIRST = 4;
    public static final int CONTROL_START_INITIAL = 256;
    static final long INPUT_METHOD_NOT_RESPONDING_TIMEOUT = 2500;
    public static final int DISPATCH_IN_PROGRESS = -1;
    public static final int DISPATCH_NOT_HANDLED = 0;
    public static final int DISPATCH_HANDLED = 1;
    final IInputMethodManager mService;
    final Looper mMainLooper;
    final H mH;
    final IInputContext mIInputContext;
    boolean mFullscreenMode;
    View mCurRootView;
    View mServedView;
    View mNextServedView;
    boolean mServedConnecting;
    EditorInfo mCurrentTextBoxAttribute;
    InputConnection mServedInputConnection;
    ControlledInputConnectionWrapper mServedInputConnectionWrapper;
    CompletionInfo[] mCompletions;
    int mCursorSelStart;
    int mCursorSelEnd;
    int mCursorCandStart;
    int mCursorCandEnd;
    String mCurId;
    IInputMethodSession mCurMethod;
    InputChannel mCurChannel;
    ImeInputEventSender mCurSender;
    static final int MSG_DUMP = 1;
    static final int MSG_BIND = 2;
    static final int MSG_UNBIND = 3;
    static final int MSG_SET_ACTIVE = 4;
    static final int MSG_SEND_INPUT_EVENT = 5;
    static final int MSG_TIMEOUT_INPUT_EVENT = 6;
    static final int MSG_FLUSH_INPUT_EVENT = 7;
    public static final int SHOW_IMPLICIT = 1;
    public static final int SHOW_FORCED = 2;
    public static final int RESULT_UNCHANGED_SHOWN = 0;
    public static final int RESULT_UNCHANGED_HIDDEN = 1;
    public static final int RESULT_SHOWN = 2;
    public static final int RESULT_HIDDEN = 3;
    public static final int HIDE_IMPLICIT_ONLY = 1;
    public static final int HIDE_NOT_ALWAYS = 2;
    boolean mActive = false;
    boolean mHasBeenInactive = true;
    Rect mTmpCursorRect = new Rect();
    Rect mCursorRect = new Rect();
    int mBindSequence = -1;
    final Pools.Pool<PendingEvent> mPendingEventPool = new Pools.SimplePool(20);
    final SparseArray<PendingEvent> mPendingEvents = new SparseArray<>(20);
    final IInputMethodClient.Stub mClient = new IInputMethodClient.Stub() { // from class: android.view.inputmethod.InputMethodManager.1
        @Override // android.os.Binder
        protected void dump(FileDescriptor fd, PrintWriter fout, String[] args) {
            CountDownLatch latch = new CountDownLatch(1);
            SomeArgs sargs = SomeArgs.obtain();
            sargs.arg1 = fd;
            sargs.arg2 = fout;
            sargs.arg3 = args;
            sargs.arg4 = latch;
            InputMethodManager.this.mH.sendMessage(InputMethodManager.this.mH.obtainMessage(1, sargs));
            try {
                if (!latch.await(5L, TimeUnit.SECONDS)) {
                    fout.println("Timeout waiting for dump");
                }
            } catch (InterruptedException e) {
                fout.println("Interrupted waiting for dump");
            }
        }

        @Override // com.android.internal.view.IInputMethodClient
        public void setUsingInputMethod(boolean state) {
        }

        @Override // com.android.internal.view.IInputMethodClient
        public void onBindMethod(InputBindResult res) {
            InputMethodManager.this.mH.sendMessage(InputMethodManager.this.mH.obtainMessage(2, res));
        }

        @Override // com.android.internal.view.IInputMethodClient
        public void onUnbindMethod(int sequence) {
            InputMethodManager.this.mH.sendMessage(InputMethodManager.this.mH.obtainMessage(3, sequence, 0));
        }

        @Override // com.android.internal.view.IInputMethodClient
        public void setActive(boolean active) {
            InputMethodManager.this.mH.sendMessage(InputMethodManager.this.mH.obtainMessage(4, active ? 1 : 0, 0));
        }
    };
    final InputConnection mDummyInputConnection = new BaseInputConnection(this, false);

    /* loaded from: InputMethodManager$FinishedInputEventCallback.class */
    public interface FinishedInputEventCallback {
        void onFinishedInputEvent(Object obj, boolean z);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: InputMethodManager$H.class */
    public class H extends Handler {
        H(Looper looper) {
            super(looper, null, true);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    SomeArgs args = (SomeArgs) msg.obj;
                    try {
                        InputMethodManager.this.doDump((FileDescriptor) args.arg1, (PrintWriter) args.arg2, (String[]) args.arg3);
                    } catch (RuntimeException e) {
                        ((PrintWriter) args.arg2).println("Exception: " + e);
                    }
                    synchronized (args.arg4) {
                        ((CountDownLatch) args.arg4).countDown();
                    }
                    args.recycle();
                    return;
                case 2:
                    InputBindResult res = (InputBindResult) msg.obj;
                    synchronized (InputMethodManager.this.mH) {
                        if (InputMethodManager.this.mBindSequence < 0 || InputMethodManager.this.mBindSequence != res.sequence) {
                            Log.w(InputMethodManager.TAG, "Ignoring onBind: cur seq=" + InputMethodManager.this.mBindSequence + ", given seq=" + res.sequence);
                            if (res.channel != null && res.channel != InputMethodManager.this.mCurChannel) {
                                res.channel.dispose();
                            }
                            return;
                        }
                        InputMethodManager.this.setInputChannelLocked(res.channel);
                        InputMethodManager.this.mCurMethod = res.method;
                        InputMethodManager.this.mCurId = res.id;
                        InputMethodManager.this.mBindSequence = res.sequence;
                        InputMethodManager.this.startInputInner(null, 0, 0, 0);
                        return;
                    }
                case 3:
                    int sequence = msg.arg1;
                    boolean startInput = false;
                    synchronized (InputMethodManager.this.mH) {
                        if (InputMethodManager.this.mBindSequence == sequence) {
                            InputMethodManager.this.clearBindingLocked();
                            if (InputMethodManager.this.mServedView != null && InputMethodManager.this.mServedView.isFocused()) {
                                InputMethodManager.this.mServedConnecting = true;
                            }
                            if (InputMethodManager.this.mActive) {
                                startInput = true;
                            }
                        }
                    }
                    if (startInput) {
                        InputMethodManager.this.startInputInner(null, 0, 0, 0);
                        return;
                    }
                    return;
                case 4:
                    boolean active = msg.arg1 != 0;
                    synchronized (InputMethodManager.this.mH) {
                        InputMethodManager.this.mActive = active;
                        InputMethodManager.this.mFullscreenMode = false;
                        if (!active) {
                            InputMethodManager.this.mHasBeenInactive = true;
                            try {
                                InputMethodManager.this.mIInputContext.finishComposingText();
                            } catch (RemoteException e2) {
                            }
                            if (InputMethodManager.this.mServedView != null && InputMethodManager.this.mServedView.hasWindowFocus() && InputMethodManager.this.checkFocusNoStartInput(InputMethodManager.this.mHasBeenInactive, false)) {
                                InputMethodManager.this.startInputInner(null, 0, 0, 0);
                            }
                        }
                    }
                    return;
                case 5:
                    InputMethodManager.this.sendInputEventAndReportResultOnMainLooper((PendingEvent) msg.obj);
                    return;
                case 6:
                    InputMethodManager.this.finishedInputEvent(msg.arg1, false, true);
                    return;
                case 7:
                    InputMethodManager.this.finishedInputEvent(msg.arg1, false, false);
                    return;
                default:
                    return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: InputMethodManager$ControlledInputConnectionWrapper.class */
    public static class ControlledInputConnectionWrapper extends IInputConnectionWrapper {
        private final InputMethodManager mParentInputMethodManager;
        private boolean mActive;

        public ControlledInputConnectionWrapper(Looper mainLooper, InputConnection conn, InputMethodManager inputMethodManager) {
            super(mainLooper, conn);
            this.mParentInputMethodManager = inputMethodManager;
            this.mActive = true;
        }

        @Override // com.android.internal.view.IInputConnectionWrapper
        public boolean isActive() {
            return this.mParentInputMethodManager.mActive && this.mActive;
        }

        void deactivate() {
            this.mActive = false;
        }
    }

    InputMethodManager(IInputMethodManager service, Looper looper) {
        this.mService = service;
        this.mMainLooper = looper;
        this.mH = new H(looper);
        this.mIInputContext = new ControlledInputConnectionWrapper(looper, this.mDummyInputConnection, this);
    }

    public static InputMethodManager getInstance() {
        InputMethodManager inputMethodManager;
        synchronized (InputMethodManager.class) {
            if (sInstance == null) {
                IBinder b = ServiceManager.getService(Context.INPUT_METHOD_SERVICE);
                IInputMethodManager service = IInputMethodManager.Stub.asInterface(b);
                sInstance = new InputMethodManager(service, Looper.getMainLooper());
            }
            inputMethodManager = sInstance;
        }
        return inputMethodManager;
    }

    public static InputMethodManager peekInstance() {
        return sInstance;
    }

    public IInputMethodClient getClient() {
        return this.mClient;
    }

    public IInputContext getInputContext() {
        return this.mIInputContext;
    }

    public List<InputMethodInfo> getInputMethodList() {
        try {
            return this.mService.getInputMethodList();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public List<InputMethodInfo> getEnabledInputMethodList() {
        try {
            return this.mService.getEnabledInputMethodList();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public List<InputMethodSubtype> getEnabledInputMethodSubtypeList(InputMethodInfo imi, boolean allowsImplicitlySelectedSubtypes) {
        try {
            return this.mService.getEnabledInputMethodSubtypeList(imi == null ? null : imi.getId(), allowsImplicitlySelectedSubtypes);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void showStatusIcon(IBinder imeToken, String packageName, int iconId) {
        try {
            this.mService.updateStatusIcon(imeToken, packageName, iconId);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void hideStatusIcon(IBinder imeToken) {
        try {
            this.mService.updateStatusIcon(imeToken, null, 0);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void setImeWindowStatus(IBinder imeToken, int vis, int backDisposition) {
        try {
            this.mService.setImeWindowStatus(imeToken, vis, backDisposition);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void setFullscreenMode(boolean fullScreen) {
        this.mFullscreenMode = fullScreen;
    }

    public void registerSuggestionSpansForNotification(SuggestionSpan[] spans) {
        try {
            this.mService.registerSuggestionSpansForNotification(spans);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void notifySuggestionPicked(SuggestionSpan span, String originalString, int index) {
        try {
            this.mService.notifySuggestionPicked(span, originalString, index);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isFullscreenMode() {
        return this.mFullscreenMode;
    }

    public boolean isActive(View view) {
        boolean z;
        checkFocus();
        synchronized (this.mH) {
            z = (this.mServedView == view || (this.mServedView != null && this.mServedView.checkInputConnectionProxy(view))) && this.mCurrentTextBoxAttribute != null;
        }
        return z;
    }

    public boolean isActive() {
        boolean z;
        checkFocus();
        synchronized (this.mH) {
            z = (this.mServedView == null || this.mCurrentTextBoxAttribute == null) ? false : true;
        }
        return z;
    }

    public boolean isAcceptingText() {
        checkFocus();
        return this.mServedInputConnection != null;
    }

    void clearBindingLocked() {
        clearConnectionLocked();
        setInputChannelLocked(null);
        this.mBindSequence = -1;
        this.mCurId = null;
        this.mCurMethod = null;
    }

    void setInputChannelLocked(InputChannel channel) {
        if (this.mCurChannel != channel) {
            if (this.mCurSender != null) {
                flushPendingEventsLocked();
                this.mCurSender.dispose();
                this.mCurSender = null;
            }
            if (this.mCurChannel != null) {
                this.mCurChannel.dispose();
            }
            this.mCurChannel = channel;
        }
    }

    void clearConnectionLocked() {
        this.mCurrentTextBoxAttribute = null;
        this.mServedInputConnection = null;
        if (this.mServedInputConnectionWrapper != null) {
            this.mServedInputConnectionWrapper.deactivate();
            this.mServedInputConnectionWrapper = null;
        }
    }

    void finishInputLocked() {
        this.mCurRootView = null;
        this.mNextServedView = null;
        if (this.mServedView != null) {
            if (this.mCurrentTextBoxAttribute != null) {
                try {
                    this.mService.finishInput(this.mClient);
                } catch (RemoteException e) {
                }
            }
            notifyInputConnectionFinished();
            this.mServedView = null;
            this.mCompletions = null;
            this.mServedConnecting = false;
            clearConnectionLocked();
        }
    }

    private void notifyInputConnectionFinished() {
        ViewRootImpl viewRootImpl;
        if (this.mServedView != null && this.mServedInputConnection != null && (viewRootImpl = this.mServedView.getViewRootImpl()) != null) {
            viewRootImpl.dispatchFinishInputConnection(this.mServedInputConnection);
        }
    }

    public void reportFinishInputConnection(InputConnection ic) {
        if (this.mServedInputConnection != ic) {
            ic.finishComposingText();
            if (ic instanceof BaseInputConnection) {
                ((BaseInputConnection) ic).reportFinish();
            }
        }
    }

    public void displayCompletions(View view, CompletionInfo[] completions) {
        checkFocus();
        synchronized (this.mH) {
            if (this.mServedView == view || (this.mServedView != null && this.mServedView.checkInputConnectionProxy(view))) {
                this.mCompletions = completions;
                if (this.mCurMethod != null) {
                    try {
                        this.mCurMethod.displayCompletions(this.mCompletions);
                    } catch (RemoteException e) {
                    }
                }
            }
        }
    }

    public void updateExtractedText(View view, int token, ExtractedText text) {
        checkFocus();
        synchronized (this.mH) {
            if (this.mServedView == view || (this.mServedView != null && this.mServedView.checkInputConnectionProxy(view))) {
                if (this.mCurMethod != null) {
                    try {
                        this.mCurMethod.updateExtractedText(token, text);
                    } catch (RemoteException e) {
                    }
                }
            }
        }
    }

    public boolean showSoftInput(View view, int flags) {
        return showSoftInput(view, flags, null);
    }

    public boolean showSoftInput(View view, int flags, ResultReceiver resultReceiver) {
        checkFocus();
        synchronized (this.mH) {
            if (this.mServedView != view && (this.mServedView == null || !this.mServedView.checkInputConnectionProxy(view))) {
                return false;
            }
            try {
                return this.mService.showSoftInput(this.mClient, flags, resultReceiver);
            } catch (RemoteException e) {
                return false;
            }
        }
    }

    public void showSoftInputUnchecked(int flags, ResultReceiver resultReceiver) {
        try {
            this.mService.showSoftInput(this.mClient, flags, resultReceiver);
        } catch (RemoteException e) {
        }
    }

    public boolean hideSoftInputFromWindow(IBinder windowToken, int flags) {
        return hideSoftInputFromWindow(windowToken, flags, null);
    }

    public boolean hideSoftInputFromWindow(IBinder windowToken, int flags, ResultReceiver resultReceiver) {
        checkFocus();
        synchronized (this.mH) {
            if (this.mServedView == null || this.mServedView.getWindowToken() != windowToken) {
                return false;
            }
            try {
                return this.mService.hideSoftInput(this.mClient, flags, resultReceiver);
            } catch (RemoteException e) {
                return false;
            }
        }
    }

    public void toggleSoftInputFromWindow(IBinder windowToken, int showFlags, int hideFlags) {
        synchronized (this.mH) {
            if (this.mServedView == null || this.mServedView.getWindowToken() != windowToken) {
                return;
            }
            if (this.mCurMethod != null) {
                try {
                    this.mCurMethod.toggleSoftInput(showFlags, hideFlags);
                } catch (RemoteException e) {
                }
            }
        }
    }

    public void toggleSoftInput(int showFlags, int hideFlags) {
        if (this.mCurMethod != null) {
            try {
                this.mCurMethod.toggleSoftInput(showFlags, hideFlags);
            } catch (RemoteException e) {
            }
        }
    }

    public void restartInput(View view) {
        checkFocus();
        synchronized (this.mH) {
            if (this.mServedView == view || (this.mServedView != null && this.mServedView.checkInputConnectionProxy(view))) {
                this.mServedConnecting = true;
                startInputInner(null, 0, 0, 0);
            }
        }
    }

    boolean startInputInner(IBinder windowGainingFocus, int controlFlags, int softInputMode, int windowFlags) {
        ControlledInputConnectionWrapper servedContext;
        InputBindResult res;
        synchronized (this.mH) {
            View view = this.mServedView;
            if (view == null) {
                return false;
            }
            Handler vh = view.getHandler();
            if (vh == null) {
                closeCurrentInput();
                return false;
            } else if (vh.getLooper() != Looper.myLooper()) {
                vh.post(new Runnable() { // from class: android.view.inputmethod.InputMethodManager.2
                    @Override // java.lang.Runnable
                    public void run() {
                        InputMethodManager.this.startInputInner(null, 0, 0, 0);
                    }
                });
                return false;
            } else {
                EditorInfo tba = new EditorInfo();
                tba.packageName = view.getContext().getPackageName();
                tba.fieldId = view.getId();
                InputConnection ic = view.onCreateInputConnection(tba);
                synchronized (this.mH) {
                    if (this.mServedView != view || !this.mServedConnecting) {
                        return false;
                    }
                    if (this.mCurrentTextBoxAttribute == null) {
                        controlFlags |= 256;
                    }
                    this.mCurrentTextBoxAttribute = tba;
                    this.mServedConnecting = false;
                    notifyInputConnectionFinished();
                    this.mServedInputConnection = ic;
                    if (ic != null) {
                        this.mCursorSelStart = tba.initialSelStart;
                        this.mCursorSelEnd = tba.initialSelEnd;
                        this.mCursorCandStart = -1;
                        this.mCursorCandEnd = -1;
                        this.mCursorRect.setEmpty();
                        servedContext = new ControlledInputConnectionWrapper(vh.getLooper(), ic, this);
                    } else {
                        servedContext = null;
                    }
                    if (this.mServedInputConnectionWrapper != null) {
                        this.mServedInputConnectionWrapper.deactivate();
                    }
                    this.mServedInputConnectionWrapper = servedContext;
                    try {
                        if (windowGainingFocus != null) {
                            res = this.mService.windowGainedFocus(this.mClient, windowGainingFocus, controlFlags, softInputMode, windowFlags, tba, servedContext);
                        } else {
                            res = this.mService.startInput(this.mClient, servedContext, tba, controlFlags);
                        }
                        if (res != null) {
                            if (res.id != null) {
                                setInputChannelLocked(res.channel);
                                this.mBindSequence = res.sequence;
                                this.mCurMethod = res.method;
                                this.mCurId = res.id;
                            } else {
                                if (res.channel != null && res.channel != this.mCurChannel) {
                                    res.channel.dispose();
                                }
                                if (this.mCurMethod == null) {
                                    return true;
                                }
                            }
                        }
                        if (this.mCurMethod != null && this.mCompletions != null) {
                            try {
                                this.mCurMethod.displayCompletions(this.mCompletions);
                            } catch (RemoteException e) {
                            }
                        }
                    } catch (RemoteException e2) {
                        Log.w(TAG, "IME died: " + this.mCurId, e2);
                    }
                    return true;
                }
            }
        }
    }

    public void windowDismissed(IBinder appWindowToken) {
        checkFocus();
        synchronized (this.mH) {
            if (this.mServedView != null && this.mServedView.getWindowToken() == appWindowToken) {
                finishInputLocked();
            }
        }
    }

    public void focusIn(View view) {
        synchronized (this.mH) {
            focusInLocked(view);
        }
    }

    void focusInLocked(View view) {
        if (this.mCurRootView != view.getRootView()) {
            return;
        }
        this.mNextServedView = view;
        scheduleCheckFocusLocked(view);
    }

    public void focusOut(View view) {
        synchronized (this.mH) {
            if (this.mServedView != view) {
            }
        }
    }

    static void scheduleCheckFocusLocked(View view) {
        ViewRootImpl viewRootImpl = view.getViewRootImpl();
        if (viewRootImpl != null) {
            viewRootImpl.dispatchCheckFocus();
        }
    }

    public void checkFocus() {
        if (checkFocusNoStartInput(false, true)) {
            startInputInner(null, 0, 0, 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean checkFocusNoStartInput(boolean forceNewFocus, boolean finishComposingText) {
        if (this.mServedView == this.mNextServedView && !forceNewFocus) {
            return false;
        }
        synchronized (this.mH) {
            if (this.mServedView == this.mNextServedView && !forceNewFocus) {
                return false;
            }
            if (this.mNextServedView == null) {
                finishInputLocked();
                closeCurrentInput();
                return false;
            }
            InputConnection ic = this.mServedInputConnection;
            this.mServedView = this.mNextServedView;
            this.mCurrentTextBoxAttribute = null;
            this.mCompletions = null;
            this.mServedConnecting = true;
            if (finishComposingText && ic != null) {
                ic.finishComposingText();
                return true;
            }
            return true;
        }
    }

    void closeCurrentInput() {
        try {
            this.mService.hideSoftInput(this.mClient, 2, null);
        } catch (RemoteException e) {
        }
    }

    public void onWindowFocus(View rootView, View focusedView, int softInputMode, boolean first, int windowFlags) {
        boolean forceNewFocus = false;
        synchronized (this.mH) {
            if (this.mHasBeenInactive) {
                this.mHasBeenInactive = false;
                forceNewFocus = true;
            }
            focusInLocked(focusedView != null ? focusedView : rootView);
        }
        int controlFlags = 0;
        if (focusedView != null) {
            controlFlags = 0 | 1;
            if (focusedView.onCheckIsTextEditor()) {
                controlFlags |= 2;
            }
        }
        if (first) {
            controlFlags |= 4;
        }
        if (checkFocusNoStartInput(forceNewFocus, true) && startInputInner(rootView.getWindowToken(), controlFlags, softInputMode, windowFlags)) {
            return;
        }
        synchronized (this.mH) {
            try {
                this.mService.windowGainedFocus(this.mClient, rootView.getWindowToken(), controlFlags, softInputMode, windowFlags, null, null);
            } catch (RemoteException e) {
            }
        }
    }

    public void startGettingWindowFocus(View rootView) {
        synchronized (this.mH) {
            this.mCurRootView = rootView;
        }
    }

    public void updateSelection(View view, int selStart, int selEnd, int candidatesStart, int candidatesEnd) {
        checkFocus();
        synchronized (this.mH) {
            if ((this.mServedView != view && (this.mServedView == null || !this.mServedView.checkInputConnectionProxy(view))) || this.mCurrentTextBoxAttribute == null || this.mCurMethod == null) {
                return;
            }
            if (this.mCursorSelStart != selStart || this.mCursorSelEnd != selEnd || this.mCursorCandStart != candidatesStart || this.mCursorCandEnd != candidatesEnd) {
                try {
                    int oldSelStart = this.mCursorSelStart;
                    int oldSelEnd = this.mCursorSelEnd;
                    this.mCursorSelStart = selStart;
                    this.mCursorSelEnd = selEnd;
                    this.mCursorCandStart = candidatesStart;
                    this.mCursorCandEnd = candidatesEnd;
                    this.mCurMethod.updateSelection(oldSelStart, oldSelEnd, selStart, selEnd, candidatesStart, candidatesEnd);
                } catch (RemoteException e) {
                    Log.w(TAG, "IME died: " + this.mCurId, e);
                }
            }
        }
    }

    public void viewClicked(View view) {
        boolean focusChanged = this.mServedView != this.mNextServedView;
        checkFocus();
        synchronized (this.mH) {
            if ((this.mServedView != view && (this.mServedView == null || !this.mServedView.checkInputConnectionProxy(view))) || this.mCurrentTextBoxAttribute == null || this.mCurMethod == null) {
                return;
            }
            try {
                this.mCurMethod.viewClicked(focusChanged);
            } catch (RemoteException e) {
                Log.w(TAG, "IME died: " + this.mCurId, e);
            }
        }
    }

    public boolean isWatchingCursor(View view) {
        return false;
    }

    public void updateCursor(View view, int left, int top, int right, int bottom) {
        checkFocus();
        synchronized (this.mH) {
            if ((this.mServedView != view && (this.mServedView == null || !this.mServedView.checkInputConnectionProxy(view))) || this.mCurrentTextBoxAttribute == null || this.mCurMethod == null) {
                return;
            }
            this.mTmpCursorRect.set(left, top, right, bottom);
            if (!this.mCursorRect.equals(this.mTmpCursorRect)) {
                try {
                    this.mCurMethod.updateCursor(this.mTmpCursorRect);
                    this.mCursorRect.set(this.mTmpCursorRect);
                } catch (RemoteException e) {
                    Log.w(TAG, "IME died: " + this.mCurId, e);
                }
            }
        }
    }

    public void sendAppPrivateCommand(View view, String action, Bundle data) {
        checkFocus();
        synchronized (this.mH) {
            if ((this.mServedView != view && (this.mServedView == null || !this.mServedView.checkInputConnectionProxy(view))) || this.mCurrentTextBoxAttribute == null || this.mCurMethod == null) {
                return;
            }
            try {
                this.mCurMethod.appPrivateCommand(action, data);
            } catch (RemoteException e) {
                Log.w(TAG, "IME died: " + this.mCurId, e);
            }
        }
    }

    public void setInputMethod(IBinder token, String id) {
        try {
            this.mService.setInputMethod(token, id);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void setInputMethodAndSubtype(IBinder token, String id, InputMethodSubtype subtype) {
        try {
            this.mService.setInputMethodAndSubtype(token, id, subtype);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void hideSoftInputFromInputMethod(IBinder token, int flags) {
        try {
            this.mService.hideMySoftInput(token, flags);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void showSoftInputFromInputMethod(IBinder token, int flags) {
        try {
            this.mService.showMySoftInput(token, flags);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public int dispatchInputEvent(InputEvent event, Object token, FinishedInputEventCallback callback, Handler handler) {
        synchronized (this.mH) {
            if (this.mCurMethod != null) {
                if (event instanceof KeyEvent) {
                    KeyEvent keyEvent = (KeyEvent) event;
                    if (keyEvent.getAction() == 0 && keyEvent.getKeyCode() == 63 && keyEvent.getRepeatCount() == 0) {
                        showInputMethodPickerLocked();
                        return 1;
                    }
                }
                PendingEvent p = obtainPendingEventLocked(event, token, this.mCurId, callback, handler);
                if (this.mMainLooper.isCurrentThread()) {
                    return sendInputEventOnMainLooperLocked(p);
                }
                Message msg = this.mH.obtainMessage(5, p);
                msg.setAsynchronous(true);
                this.mH.sendMessage(msg);
                return -1;
            }
            return 0;
        }
    }

    void sendInputEventAndReportResultOnMainLooper(PendingEvent p) {
        synchronized (this.mH) {
            int result = sendInputEventOnMainLooperLocked(p);
            if (result == -1) {
                return;
            }
            boolean handled = result == 1;
            invokeFinishedInputEventCallback(p, handled);
        }
    }

    int sendInputEventOnMainLooperLocked(PendingEvent p) {
        if (this.mCurChannel != null) {
            if (this.mCurSender == null) {
                this.mCurSender = new ImeInputEventSender(this.mCurChannel, this.mH.getLooper());
            }
            InputEvent event = p.mEvent;
            int seq = event.getSequenceNumber();
            if (this.mCurSender.sendInputEvent(seq, event)) {
                this.mPendingEvents.put(seq, p);
                Trace.traceCounter(4L, PENDING_EVENT_COUNTER, this.mPendingEvents.size());
                Message msg = this.mH.obtainMessage(6, p);
                msg.setAsynchronous(true);
                this.mH.sendMessageDelayed(msg, INPUT_METHOD_NOT_RESPONDING_TIMEOUT);
                return -1;
            }
            Log.w(TAG, "Unable to send input event to IME: " + this.mCurId + " dropping: " + event);
            return 0;
        }
        return 0;
    }

    void finishedInputEvent(int seq, boolean handled, boolean timeout) {
        synchronized (this.mH) {
            int index = this.mPendingEvents.indexOfKey(seq);
            if (index < 0) {
                return;
            }
            PendingEvent p = this.mPendingEvents.valueAt(index);
            this.mPendingEvents.removeAt(index);
            Trace.traceCounter(4L, PENDING_EVENT_COUNTER, this.mPendingEvents.size());
            if (timeout) {
                Log.w(TAG, "Timeout waiting for IME to handle input event after 2500 ms: " + p.mInputMethodId);
            } else {
                this.mH.removeMessages(6, p);
            }
            invokeFinishedInputEventCallback(p, handled);
        }
    }

    void invokeFinishedInputEventCallback(PendingEvent p, boolean handled) {
        p.mHandled = handled;
        if (p.mHandler.getLooper().isCurrentThread()) {
            p.run();
            return;
        }
        Message msg = Message.obtain(p.mHandler, p);
        msg.setAsynchronous(true);
        msg.sendToTarget();
    }

    private void flushPendingEventsLocked() {
        this.mH.removeMessages(7);
        int count = this.mPendingEvents.size();
        for (int i = 0; i < count; i++) {
            int seq = this.mPendingEvents.keyAt(i);
            Message msg = this.mH.obtainMessage(7, seq, 0);
            msg.setAsynchronous(true);
            msg.sendToTarget();
        }
    }

    private PendingEvent obtainPendingEventLocked(InputEvent event, Object token, String inputMethodId, FinishedInputEventCallback callback, Handler handler) {
        PendingEvent p = this.mPendingEventPool.acquire();
        if (p == null) {
            p = new PendingEvent();
        }
        p.mEvent = event;
        p.mToken = token;
        p.mInputMethodId = inputMethodId;
        p.mCallback = callback;
        p.mHandler = handler;
        return p;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void recyclePendingEventLocked(PendingEvent p) {
        p.recycle();
        this.mPendingEventPool.release(p);
    }

    public void showInputMethodPicker() {
        synchronized (this.mH) {
            showInputMethodPickerLocked();
        }
    }

    private void showInputMethodPickerLocked() {
        try {
            this.mService.showInputMethodPickerFromClient(this.mClient);
        } catch (RemoteException e) {
            Log.w(TAG, "IME died: " + this.mCurId, e);
        }
    }

    public void showInputMethodAndSubtypeEnabler(String imiId) {
        synchronized (this.mH) {
            try {
                this.mService.showInputMethodAndSubtypeEnablerFromClient(this.mClient, imiId);
            } catch (RemoteException e) {
                Log.w(TAG, "IME died: " + this.mCurId, e);
            }
        }
    }

    public InputMethodSubtype getCurrentInputMethodSubtype() {
        InputMethodSubtype currentInputMethodSubtype;
        synchronized (this.mH) {
            try {
                currentInputMethodSubtype = this.mService.getCurrentInputMethodSubtype();
            } catch (RemoteException e) {
                Log.w(TAG, "IME died: " + this.mCurId, e);
                return null;
            }
        }
        return currentInputMethodSubtype;
    }

    public boolean setCurrentInputMethodSubtype(InputMethodSubtype subtype) {
        boolean currentInputMethodSubtype;
        synchronized (this.mH) {
            try {
                currentInputMethodSubtype = this.mService.setCurrentInputMethodSubtype(subtype);
            } catch (RemoteException e) {
                Log.w(TAG, "IME died: " + this.mCurId, e);
                return false;
            }
        }
        return currentInputMethodSubtype;
    }

    /* JADX WARN: Code restructure failed: missing block: B:16:0x0052, code lost:
        android.util.Log.e(android.view.inputmethod.InputMethodManager.TAG, "IMI list already contains the same InputMethod.");
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public java.util.Map<android.view.inputmethod.InputMethodInfo, java.util.List<android.view.inputmethod.InputMethodSubtype>> getShortcutInputMethodsAndSubtypes() {
        /*
            r4 = this;
            r0 = r4
            android.view.inputmethod.InputMethodManager$H r0 = r0.mH
            r1 = r0
            r5 = r1
            monitor-enter(r0)
            java.util.HashMap r0 = new java.util.HashMap     // Catch: java.lang.Throwable -> Lb8
            r1 = r0
            r1.<init>()     // Catch: java.lang.Throwable -> Lb8
            r6 = r0
            r0 = r4
            com.android.internal.view.IInputMethodManager r0 = r0.mService     // Catch: android.os.RemoteException -> L96 java.lang.Throwable -> Lb8
            java.util.List r0 = r0.getShortcutInputMethodsAndSubtypes()     // Catch: android.os.RemoteException -> L96 java.lang.Throwable -> Lb8
            r7 = r0
            r0 = 0
            r8 = r0
            r0 = r7
            int r0 = r0.size()     // Catch: android.os.RemoteException -> L96 java.lang.Throwable -> Lb8
            r9 = r0
            r0 = r7
            if (r0 == 0) goto L93
            r0 = r9
            if (r0 <= 0) goto L93
            r0 = 0
            r10 = r0
        L30:
            r0 = r10
            r1 = r9
            if (r0 >= r1) goto L93
            r0 = r7
            r1 = r10
            java.lang.Object r0 = r0.get(r1)     // Catch: android.os.RemoteException -> L96 java.lang.Throwable -> Lb8
            r11 = r0
            r0 = r11
            boolean r0 = r0 instanceof android.view.inputmethod.InputMethodInfo     // Catch: android.os.RemoteException -> L96 java.lang.Throwable -> Lb8
            if (r0 == 0) goto L75
            r0 = r6
            r1 = r11
            boolean r0 = r0.containsKey(r1)     // Catch: android.os.RemoteException -> L96 java.lang.Throwable -> Lb8
            if (r0 == 0) goto L5d
            java.lang.String r0 = "InputMethodManager"
            java.lang.String r1 = "IMI list already contains the same InputMethod."
            int r0 = android.util.Log.e(r0, r1)     // Catch: android.os.RemoteException -> L96 java.lang.Throwable -> Lb8
            goto L93
        L5d:
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch: android.os.RemoteException -> L96 java.lang.Throwable -> Lb8
            r1 = r0
            r1.<init>()     // Catch: android.os.RemoteException -> L96 java.lang.Throwable -> Lb8
            r8 = r0
            r0 = r6
            r1 = r11
            android.view.inputmethod.InputMethodInfo r1 = (android.view.inputmethod.InputMethodInfo) r1     // Catch: android.os.RemoteException -> L96 java.lang.Throwable -> Lb8
            r2 = r8
            java.lang.Object r0 = r0.put(r1, r2)     // Catch: android.os.RemoteException -> L96 java.lang.Throwable -> Lb8
            goto L8d
        L75:
            r0 = r8
            if (r0 == 0) goto L8d
            r0 = r11
            boolean r0 = r0 instanceof android.view.inputmethod.InputMethodSubtype     // Catch: android.os.RemoteException -> L96 java.lang.Throwable -> Lb8
            if (r0 == 0) goto L8d
            r0 = r8
            r1 = r11
            android.view.inputmethod.InputMethodSubtype r1 = (android.view.inputmethod.InputMethodSubtype) r1     // Catch: android.os.RemoteException -> L96 java.lang.Throwable -> Lb8
            boolean r0 = r0.add(r1)     // Catch: android.os.RemoteException -> L96 java.lang.Throwable -> Lb8
        L8d:
            int r10 = r10 + 1
            goto L30
        L93:
            goto Lb4
        L96:
            r7 = move-exception
            java.lang.String r0 = "InputMethodManager"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> Lb8
            r2 = r1
            r2.<init>()     // Catch: java.lang.Throwable -> Lb8
            java.lang.String r2 = "IME died: "
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch: java.lang.Throwable -> Lb8
            r2 = r4
            java.lang.String r2 = r2.mCurId     // Catch: java.lang.Throwable -> Lb8
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch: java.lang.Throwable -> Lb8
            java.lang.String r1 = r1.toString()     // Catch: java.lang.Throwable -> Lb8
            r2 = r7
            int r0 = android.util.Log.w(r0, r1, r2)     // Catch: java.lang.Throwable -> Lb8
        Lb4:
            r0 = r6
            r1 = r5
            monitor-exit(r1)     // Catch: java.lang.Throwable -> Lb8
            return r0
        Lb8:
            r12 = move-exception
            r0 = r5
            monitor-exit(r0)     // Catch: java.lang.Throwable -> Lb8
            r0 = r12
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: android.view.inputmethod.InputMethodManager.getShortcutInputMethodsAndSubtypes():java.util.Map");
    }

    public boolean switchToLastInputMethod(IBinder imeToken) {
        boolean switchToLastInputMethod;
        synchronized (this.mH) {
            try {
                switchToLastInputMethod = this.mService.switchToLastInputMethod(imeToken);
            } catch (RemoteException e) {
                Log.w(TAG, "IME died: " + this.mCurId, e);
                return false;
            }
        }
        return switchToLastInputMethod;
    }

    public boolean switchToNextInputMethod(IBinder imeToken, boolean onlyCurrentIme) {
        boolean switchToNextInputMethod;
        synchronized (this.mH) {
            try {
                switchToNextInputMethod = this.mService.switchToNextInputMethod(imeToken, onlyCurrentIme);
            } catch (RemoteException e) {
                Log.w(TAG, "IME died: " + this.mCurId, e);
                return false;
            }
        }
        return switchToNextInputMethod;
    }

    public boolean shouldOfferSwitchingToNextInputMethod(IBinder imeToken) {
        boolean shouldOfferSwitchingToNextInputMethod;
        synchronized (this.mH) {
            try {
                shouldOfferSwitchingToNextInputMethod = this.mService.shouldOfferSwitchingToNextInputMethod(imeToken);
            } catch (RemoteException e) {
                Log.w(TAG, "IME died: " + this.mCurId, e);
                return false;
            }
        }
        return shouldOfferSwitchingToNextInputMethod;
    }

    public void setAdditionalInputMethodSubtypes(String imiId, InputMethodSubtype[] subtypes) {
        synchronized (this.mH) {
            try {
                this.mService.setAdditionalInputMethodSubtypes(imiId, subtypes);
            } catch (RemoteException e) {
                Log.w(TAG, "IME died: " + this.mCurId, e);
            }
        }
    }

    public InputMethodSubtype getLastInputMethodSubtype() {
        InputMethodSubtype lastInputMethodSubtype;
        synchronized (this.mH) {
            try {
                lastInputMethodSubtype = this.mService.getLastInputMethodSubtype();
            } catch (RemoteException e) {
                Log.w(TAG, "IME died: " + this.mCurId, e);
                return null;
            }
        }
        return lastInputMethodSubtype;
    }

    void doDump(FileDescriptor fd, PrintWriter fout, String[] args) {
        Printer p = new PrintWriterPrinter(fout);
        p.println("Input method client state for " + this + Separators.COLON);
        p.println("  mService=" + this.mService);
        p.println("  mMainLooper=" + this.mMainLooper);
        p.println("  mIInputContext=" + this.mIInputContext);
        p.println("  mActive=" + this.mActive + " mHasBeenInactive=" + this.mHasBeenInactive + " mBindSequence=" + this.mBindSequence + " mCurId=" + this.mCurId);
        p.println("  mCurMethod=" + this.mCurMethod);
        p.println("  mCurRootView=" + this.mCurRootView);
        p.println("  mServedView=" + this.mServedView);
        p.println("  mNextServedView=" + this.mNextServedView);
        p.println("  mServedConnecting=" + this.mServedConnecting);
        if (this.mCurrentTextBoxAttribute != null) {
            p.println("  mCurrentTextBoxAttribute:");
            this.mCurrentTextBoxAttribute.dump(p, "    ");
        } else {
            p.println("  mCurrentTextBoxAttribute: null");
        }
        p.println("  mServedInputConnection=" + this.mServedInputConnection);
        p.println("  mCompletions=" + this.mCompletions);
        p.println("  mCursorRect=" + this.mCursorRect);
        p.println("  mCursorSelStart=" + this.mCursorSelStart + " mCursorSelEnd=" + this.mCursorSelEnd + " mCursorCandStart=" + this.mCursorCandStart + " mCursorCandEnd=" + this.mCursorCandEnd);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: InputMethodManager$ImeInputEventSender.class */
    public final class ImeInputEventSender extends InputEventSender {
        public ImeInputEventSender(InputChannel inputChannel, Looper looper) {
            super(inputChannel, looper);
        }

        @Override // android.view.InputEventSender
        public void onInputEventFinished(int seq, boolean handled) {
            InputMethodManager.this.finishedInputEvent(seq, handled, false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: InputMethodManager$PendingEvent.class */
    public final class PendingEvent implements Runnable {
        public InputEvent mEvent;
        public Object mToken;
        public String mInputMethodId;
        public FinishedInputEventCallback mCallback;
        public Handler mHandler;
        public boolean mHandled;

        private PendingEvent() {
        }

        public void recycle() {
            this.mEvent = null;
            this.mToken = null;
            this.mInputMethodId = null;
            this.mCallback = null;
            this.mHandler = null;
            this.mHandled = false;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.mCallback.onFinishedInputEvent(this.mToken, this.mHandled);
            synchronized (InputMethodManager.this.mH) {
                InputMethodManager.this.recyclePendingEventLocked(this);
            }
        }
    }
}