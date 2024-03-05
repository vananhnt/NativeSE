package android.webkit;

/* loaded from: JsResult.class */
public class JsResult {
    private final ResultReceiver mReceiver;
    private boolean mResult;

    /* loaded from: JsResult$ResultReceiver.class */
    public interface ResultReceiver {
        void onJsResultComplete(JsResult jsResult);
    }

    public final void cancel() {
        this.mResult = false;
        wakeUp();
    }

    public final void confirm() {
        this.mResult = true;
        wakeUp();
    }

    public JsResult(ResultReceiver receiver) {
        this.mReceiver = receiver;
    }

    public final boolean getResult() {
        return this.mResult;
    }

    private final void wakeUp() {
        this.mReceiver.onJsResultComplete(this);
    }
}