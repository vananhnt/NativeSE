package com.android.server.am;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
import com.android.internal.R;

/* loaded from: AppErrorDialog.class */
final class AppErrorDialog extends BaseErrorDialog {
    private final ActivityManagerService mService;
    private final AppErrorResult mResult;
    private final ProcessRecord mProc;
    static final int FORCE_QUIT = 0;
    static final int FORCE_QUIT_AND_REPORT = 1;
    static final long DISMISS_TIMEOUT = 300000;
    private final Handler mHandler;

    public AppErrorDialog(Context context, ActivityManagerService service, AppErrorResult result, ProcessRecord app) {
        super(context);
        CharSequence name;
        this.mHandler = new Handler() { // from class: com.android.server.am.AppErrorDialog.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                synchronized (AppErrorDialog.this.mService) {
                    if (AppErrorDialog.this.mProc != null && AppErrorDialog.this.mProc.crashDialog == AppErrorDialog.this) {
                        AppErrorDialog.this.mProc.crashDialog = null;
                    }
                }
                AppErrorDialog.this.mResult.set(msg.what);
                AppErrorDialog.this.dismiss();
            }
        };
        Resources res = context.getResources();
        this.mService = service;
        this.mProc = app;
        this.mResult = result;
        if (app.pkgList.size() == 1 && (name = context.getPackageManager().getApplicationLabel(app.info)) != null) {
            setMessage(res.getString(R.string.aerr_application, name.toString(), app.info.processName));
        } else {
            setMessage(res.getString(R.string.aerr_process, app.processName.toString()));
        }
        setCancelable(false);
        setButton(-1, res.getText(R.string.force_close), this.mHandler.obtainMessage(0));
        if (app.errorReportReceiver != null) {
            setButton(-2, res.getText(R.string.report), this.mHandler.obtainMessage(1));
        }
        setTitle(res.getText(R.string.aerr_title));
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.setTitle("Application Error: " + app.info.processName);
        attrs.privateFlags |= 272;
        getWindow().setAttributes(attrs);
        if (app.persistent) {
            getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ERROR);
        }
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(0), DISMISS_TIMEOUT);
    }
}