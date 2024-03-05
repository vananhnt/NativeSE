package android.inputmethodservice;

import android.Manifest;
import android.content.Context;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.InputChannel;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputBinding;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodSession;
import android.view.inputmethod.InputMethodSubtype;
import com.android.internal.os.HandlerCaller;
import com.android.internal.os.SomeArgs;
import com.android.internal.view.IInputContext;
import com.android.internal.view.IInputMethod;
import com.android.internal.view.IInputMethodSession;
import com.android.internal.view.IInputSessionCallback;
import com.android.internal.view.InputConnectionWrapper;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/* loaded from: IInputMethodWrapper.class */
class IInputMethodWrapper extends IInputMethod.Stub implements HandlerCaller.Callback {
    private static final String TAG = "InputMethodWrapper";
    private static final int DO_DUMP = 1;
    private static final int DO_ATTACH_TOKEN = 10;
    private static final int DO_SET_INPUT_CONTEXT = 20;
    private static final int DO_UNSET_INPUT_CONTEXT = 30;
    private static final int DO_START_INPUT = 32;
    private static final int DO_RESTART_INPUT = 34;
    private static final int DO_CREATE_SESSION = 40;
    private static final int DO_SET_SESSION_ENABLED = 45;
    private static final int DO_REVOKE_SESSION = 50;
    private static final int DO_SHOW_SOFT_INPUT = 60;
    private static final int DO_HIDE_SOFT_INPUT = 70;
    private static final int DO_CHANGE_INPUTMETHOD_SUBTYPE = 80;
    final WeakReference<AbstractInputMethodService> mTarget;
    final HandlerCaller mCaller;
    final WeakReference<InputMethod> mInputMethod;
    final int mTargetSdkVersion;

    /* loaded from: IInputMethodWrapper$Notifier.class */
    static class Notifier {
        boolean notified;

        Notifier() {
        }
    }

    /* loaded from: IInputMethodWrapper$InputMethodSessionCallbackWrapper.class */
    static final class InputMethodSessionCallbackWrapper implements InputMethod.SessionCallback {
        final Context mContext;
        final InputChannel mChannel;
        final IInputSessionCallback mCb;

        InputMethodSessionCallbackWrapper(Context context, InputChannel channel, IInputSessionCallback cb) {
            this.mContext = context;
            this.mChannel = channel;
            this.mCb = cb;
        }

        @Override // android.view.inputmethod.InputMethod.SessionCallback
        public void sessionCreated(InputMethodSession session) {
            try {
                if (session != null) {
                    IInputMethodSessionWrapper wrap = new IInputMethodSessionWrapper(this.mContext, session, this.mChannel);
                    this.mCb.sessionCreated(wrap);
                } else {
                    if (this.mChannel != null) {
                        this.mChannel.dispose();
                    }
                    this.mCb.sessionCreated(null);
                }
            } catch (RemoteException e) {
            }
        }
    }

    public IInputMethodWrapper(AbstractInputMethodService context, InputMethod inputMethod) {
        this.mTarget = new WeakReference<>(context);
        this.mCaller = new HandlerCaller(context.getApplicationContext(), null, this, true);
        this.mInputMethod = new WeakReference<>(inputMethod);
        this.mTargetSdkVersion = context.getApplicationInfo().targetSdkVersion;
    }

    public InputMethod getInternalInputMethod() {
        return this.mInputMethod.get();
    }

    @Override // com.android.internal.os.HandlerCaller.Callback
    public void executeMessage(Message msg) {
        InputMethod inputMethod = this.mInputMethod.get();
        if (inputMethod == null && msg.what != 1) {
            Log.w(TAG, "Input method reference was null, ignoring message: " + msg.what);
            return;
        }
        switch (msg.what) {
            case 1:
                AbstractInputMethodService target = this.mTarget.get();
                if (target == null) {
                    return;
                }
                SomeArgs args = (SomeArgs) msg.obj;
                try {
                    target.dump((FileDescriptor) args.arg1, (PrintWriter) args.arg2, (String[]) args.arg3);
                } catch (RuntimeException e) {
                    ((PrintWriter) args.arg2).println("Exception: " + e);
                }
                synchronized (args.arg4) {
                    ((CountDownLatch) args.arg4).countDown();
                }
                args.recycle();
                return;
            case 10:
                inputMethod.attachToken((IBinder) msg.obj);
                return;
            case 20:
                inputMethod.bindInput((InputBinding) msg.obj);
                return;
            case 30:
                inputMethod.unbindInput();
                return;
            case 32:
                SomeArgs args2 = (SomeArgs) msg.obj;
                IInputContext inputContext = (IInputContext) args2.arg1;
                InputConnection ic = inputContext != null ? new InputConnectionWrapper(inputContext) : null;
                EditorInfo info = (EditorInfo) args2.arg2;
                info.makeCompatible(this.mTargetSdkVersion);
                inputMethod.startInput(ic, info);
                args2.recycle();
                return;
            case 34:
                SomeArgs args3 = (SomeArgs) msg.obj;
                IInputContext inputContext2 = (IInputContext) args3.arg1;
                InputConnection ic2 = inputContext2 != null ? new InputConnectionWrapper(inputContext2) : null;
                EditorInfo info2 = (EditorInfo) args3.arg2;
                info2.makeCompatible(this.mTargetSdkVersion);
                inputMethod.restartInput(ic2, info2);
                args3.recycle();
                return;
            case 40:
                SomeArgs args4 = (SomeArgs) msg.obj;
                inputMethod.createSession(new InputMethodSessionCallbackWrapper(this.mCaller.mContext, (InputChannel) args4.arg1, (IInputSessionCallback) args4.arg2));
                args4.recycle();
                return;
            case 45:
                inputMethod.setSessionEnabled((InputMethodSession) msg.obj, msg.arg1 != 0);
                return;
            case 50:
                inputMethod.revokeSession((InputMethodSession) msg.obj);
                return;
            case 60:
                inputMethod.showSoftInput(msg.arg1, (ResultReceiver) msg.obj);
                return;
            case 70:
                inputMethod.hideSoftInput(msg.arg1, (ResultReceiver) msg.obj);
                return;
            case 80:
                inputMethod.changeInputMethodSubtype((InputMethodSubtype) msg.obj);
                return;
            default:
                Log.w(TAG, "Unhandled message code: " + msg.what);
                return;
        }
    }

    @Override // android.os.Binder
    protected void dump(FileDescriptor fd, PrintWriter fout, String[] args) {
        AbstractInputMethodService target = this.mTarget.get();
        if (target == null) {
            return;
        }
        if (target.checkCallingOrSelfPermission(Manifest.permission.DUMP) != 0) {
            fout.println("Permission Denial: can't dump InputMethodManager from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
            return;
        }
        CountDownLatch latch = new CountDownLatch(1);
        this.mCaller.executeOrSendMessage(this.mCaller.obtainMessageOOOO(1, fd, fout, args, latch));
        try {
            if (!latch.await(5L, TimeUnit.SECONDS)) {
                fout.println("Timeout waiting for dump");
            }
        } catch (InterruptedException e) {
            fout.println("Interrupted waiting for dump");
        }
    }

    @Override // com.android.internal.view.IInputMethod
    public void attachToken(IBinder token) {
        this.mCaller.executeOrSendMessage(this.mCaller.obtainMessageO(10, token));
    }

    @Override // com.android.internal.view.IInputMethod
    public void bindInput(InputBinding binding) {
        InputConnection ic = new InputConnectionWrapper(IInputContext.Stub.asInterface(binding.getConnectionToken()));
        InputBinding nu = new InputBinding(ic, binding);
        this.mCaller.executeOrSendMessage(this.mCaller.obtainMessageO(20, nu));
    }

    @Override // com.android.internal.view.IInputMethod
    public void unbindInput() {
        this.mCaller.executeOrSendMessage(this.mCaller.obtainMessage(30));
    }

    @Override // com.android.internal.view.IInputMethod
    public void startInput(IInputContext inputContext, EditorInfo attribute) {
        this.mCaller.executeOrSendMessage(this.mCaller.obtainMessageOO(32, inputContext, attribute));
    }

    @Override // com.android.internal.view.IInputMethod
    public void restartInput(IInputContext inputContext, EditorInfo attribute) {
        this.mCaller.executeOrSendMessage(this.mCaller.obtainMessageOO(34, inputContext, attribute));
    }

    @Override // com.android.internal.view.IInputMethod
    public void createSession(InputChannel channel, IInputSessionCallback callback) {
        this.mCaller.executeOrSendMessage(this.mCaller.obtainMessageOO(40, channel, callback));
    }

    @Override // com.android.internal.view.IInputMethod
    public void setSessionEnabled(IInputMethodSession session, boolean enabled) {
        try {
            InputMethodSession ls = ((IInputMethodSessionWrapper) session).getInternalInputMethodSession();
            if (ls == null) {
                Log.w(TAG, "Session is already finished: " + session);
            } else {
                this.mCaller.executeOrSendMessage(this.mCaller.obtainMessageIO(45, enabled ? 1 : 0, ls));
            }
        } catch (ClassCastException e) {
            Log.w(TAG, "Incoming session not of correct type: " + session, e);
        }
    }

    @Override // com.android.internal.view.IInputMethod
    public void revokeSession(IInputMethodSession session) {
        try {
            InputMethodSession ls = ((IInputMethodSessionWrapper) session).getInternalInputMethodSession();
            if (ls == null) {
                Log.w(TAG, "Session is already finished: " + session);
            } else {
                this.mCaller.executeOrSendMessage(this.mCaller.obtainMessageO(50, ls));
            }
        } catch (ClassCastException e) {
            Log.w(TAG, "Incoming session not of correct type: " + session, e);
        }
    }

    @Override // com.android.internal.view.IInputMethod
    public void showSoftInput(int flags, ResultReceiver resultReceiver) {
        this.mCaller.executeOrSendMessage(this.mCaller.obtainMessageIO(60, flags, resultReceiver));
    }

    @Override // com.android.internal.view.IInputMethod
    public void hideSoftInput(int flags, ResultReceiver resultReceiver) {
        this.mCaller.executeOrSendMessage(this.mCaller.obtainMessageIO(70, flags, resultReceiver));
    }

    @Override // com.android.internal.view.IInputMethod
    public void changeInputMethodSubtype(InputMethodSubtype subtype) {
        this.mCaller.executeOrSendMessage(this.mCaller.obtainMessageO(80, subtype));
    }
}