package com.android.server.am;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import com.android.internal.R;

/* loaded from: StrictModeViolationDialog.class */
final class StrictModeViolationDialog extends BaseErrorDialog {
    private static final String TAG = "StrictModeViolationDialog";
    private final ActivityManagerService mService;
    private final AppErrorResult mResult;
    private final ProcessRecord mProc;
    static final int ACTION_OK = 0;
    static final int ACTION_OK_AND_REPORT = 1;
    static final long DISMISS_TIMEOUT = 60000;
    private final Handler mHandler;

    public StrictModeViolationDialog(Context context, ActivityManagerService service, AppErrorResult result, ProcessRecord app) {
        super(context);
        CharSequence name;
        this.mHandler = new Handler() { // from class: com.android.server.am.StrictModeViolationDialog.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                synchronized (StrictModeViolationDialog.this.mService) {
                    if (StrictModeViolationDialog.this.mProc != null && StrictModeViolationDialog.this.mProc.crashDialog == StrictModeViolationDialog.this) {
                        StrictModeViolationDialog.this.mProc.crashDialog = null;
                    }
                }
                StrictModeViolationDialog.this.mResult.set(msg.what);
                StrictModeViolationDialog.this.dismiss();
            }
        };
        Resources res = context.getResources();
        this.mService = service;
        this.mProc = app;
        this.mResult = result;
        if (app.pkgList.size() == 1 && (name = context.getPackageManager().getApplicationLabel(app.info)) != null) {
            setMessage(res.getString(R.string.smv_application, name.toString(), app.info.processName));
        } else {
            setMessage(res.getString(R.string.smv_process, app.processName.toString()));
        }
        setCancelable(false);
        setButton(-1, res.getText(R.string.dlg_ok), this.mHandler.obtainMessage(0));
        if (app.errorReportReceiver != null) {
            setButton(-2, res.getText(R.string.report), this.mHandler.obtainMessage(1));
        }
        setTitle(res.getText(R.string.aerr_title));
        getWindow().addPrivateFlags(256);
        getWindow().setTitle("Strict Mode Violation: " + app.info.processName);
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(0), 60000L);
    }
}