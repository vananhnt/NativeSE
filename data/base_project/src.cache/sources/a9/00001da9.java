package com.android.server.am;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Button;
import com.android.internal.R;

/* loaded from: BaseErrorDialog.class */
class BaseErrorDialog extends AlertDialog {
    private Handler mHandler;
    private boolean mConsuming;

    public BaseErrorDialog(Context context) {
        super(context, R.style.Theme_Dialog_AppError);
        this.mHandler = new Handler() { // from class: com.android.server.am.BaseErrorDialog.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    BaseErrorDialog.this.mConsuming = false;
                    BaseErrorDialog.this.setEnabled(true);
                }
            }
        };
        this.mConsuming = true;
        getWindow().setType(2003);
        getWindow().setFlags(131072, 131072);
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.setTitle("Error Dialog");
        getWindow().setAttributes(attrs);
        setIconAttribute(16843605);
    }

    @Override // android.app.Dialog
    public void onStart() {
        super.onStart();
        setEnabled(false);
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(0), 1000L);
    }

    @Override // android.app.Dialog, android.view.Window.Callback
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (this.mConsuming) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setEnabled(boolean enabled) {
        Button b = (Button) findViewById(16908313);
        if (b != null) {
            b.setEnabled(enabled);
        }
        Button b2 = (Button) findViewById(16908314);
        if (b2 != null) {
            b2.setEnabled(enabled);
        }
        Button b3 = (Button) findViewById(16908315);
        if (b3 != null) {
            b3.setEnabled(enabled);
        }
    }
}