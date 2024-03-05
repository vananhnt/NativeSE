package android.view;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.view.IInputFilter;

/* loaded from: InputFilter.class */
public abstract class InputFilter extends IInputFilter.Stub {
    private static final int MSG_INSTALL = 1;
    private static final int MSG_UNINSTALL = 2;
    private static final int MSG_INPUT_EVENT = 3;
    private final InputEventConsistencyVerifier mInboundInputEventConsistencyVerifier;
    private final InputEventConsistencyVerifier mOutboundInputEventConsistencyVerifier;
    private final H mH;
    private IInputFilterHost mHost;

    public InputFilter(Looper looper) {
        this.mInboundInputEventConsistencyVerifier = InputEventConsistencyVerifier.isInstrumentationEnabled() ? new InputEventConsistencyVerifier(this, 1, "InputFilter#InboundInputEventConsistencyVerifier") : null;
        this.mOutboundInputEventConsistencyVerifier = InputEventConsistencyVerifier.isInstrumentationEnabled() ? new InputEventConsistencyVerifier(this, 1, "InputFilter#OutboundInputEventConsistencyVerifier") : null;
        this.mH = new H(looper);
    }

    @Override // android.view.IInputFilter
    public final void install(IInputFilterHost host) {
        this.mH.obtainMessage(1, host).sendToTarget();
    }

    @Override // android.view.IInputFilter
    public final void uninstall() {
        this.mH.obtainMessage(2).sendToTarget();
    }

    @Override // android.view.IInputFilter
    public final void filterInputEvent(InputEvent event, int policyFlags) {
        this.mH.obtainMessage(3, policyFlags, 0, event).sendToTarget();
    }

    public void sendInputEvent(InputEvent event, int policyFlags) {
        if (event == null) {
            throw new IllegalArgumentException("event must not be null");
        }
        if (this.mHost == null) {
            throw new IllegalStateException("Cannot send input event because the input filter is not installed.");
        }
        if (this.mOutboundInputEventConsistencyVerifier != null) {
            this.mOutboundInputEventConsistencyVerifier.onInputEvent(event, 0);
        }
        try {
            this.mHost.sendInputEvent(event, policyFlags);
        } catch (RemoteException e) {
        }
    }

    public void onInputEvent(InputEvent event, int policyFlags) {
        sendInputEvent(event, policyFlags);
    }

    public void onInstalled() {
    }

    public void onUninstalled() {
    }

    /* loaded from: InputFilter$H.class */
    private final class H extends Handler {
        public H(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    InputFilter.this.mHost = (IInputFilterHost) msg.obj;
                    if (InputFilter.this.mInboundInputEventConsistencyVerifier != null) {
                        InputFilter.this.mInboundInputEventConsistencyVerifier.reset();
                    }
                    if (InputFilter.this.mOutboundInputEventConsistencyVerifier != null) {
                        InputFilter.this.mOutboundInputEventConsistencyVerifier.reset();
                    }
                    InputFilter.this.onInstalled();
                    return;
                case 2:
                    try {
                        InputFilter.this.onUninstalled();
                        InputFilter.this.mHost = null;
                        return;
                    } catch (Throwable th) {
                        InputFilter.this.mHost = null;
                        throw th;
                    }
                case 3:
                    InputEvent event = (InputEvent) msg.obj;
                    try {
                        if (InputFilter.this.mInboundInputEventConsistencyVerifier != null) {
                            InputFilter.this.mInboundInputEventConsistencyVerifier.onInputEvent(event, 0);
                        }
                        InputFilter.this.onInputEvent(event, msg.arg1);
                        event.recycle();
                        return;
                    } catch (Throwable th2) {
                        event.recycle();
                        throw th2;
                    }
                default:
                    return;
            }
        }
    }
}