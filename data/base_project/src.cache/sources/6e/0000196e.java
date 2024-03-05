package com.android.internal.app;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import com.android.internal.app.AlertController;

/* loaded from: AlertActivity.class */
public abstract class AlertActivity extends Activity implements DialogInterface {
    protected AlertController mAlert;
    protected AlertController.AlertParams mAlertParams;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mAlert = new AlertController(this, this, getWindow());
        this.mAlertParams = new AlertController.AlertParams(this);
    }

    @Override // android.content.DialogInterface
    public void cancel() {
        finish();
    }

    @Override // android.content.DialogInterface
    public void dismiss() {
        if (!isFinishing()) {
            finish();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setupAlert() {
        this.mAlertParams.apply(this.mAlert);
        this.mAlert.installContent();
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (this.mAlert.onKeyDown(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (this.mAlert.onKeyUp(keyCode, event)) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
}