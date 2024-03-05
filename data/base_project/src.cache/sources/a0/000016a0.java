package android.webkit;

import android.webkit.JsResult;

/* loaded from: JsPromptResult.class */
public class JsPromptResult extends JsResult {
    private String mStringResult;

    public void confirm(String result) {
        this.mStringResult = result;
        confirm();
    }

    public JsPromptResult(JsResult.ResultReceiver receiver) {
        super(receiver);
    }

    public String getStringResult() {
        return this.mStringResult;
    }
}