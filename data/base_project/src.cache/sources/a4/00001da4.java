package com.android.server.am;

import android.os.Handler;

/* loaded from: AppNotRespondingDialog.class */
final class AppNotRespondingDialog extends BaseErrorDialog {
    private static final String TAG = "AppNotRespondingDialog";
    static final int FORCE_CLOSE = 1;
    static final int WAIT = 2;
    static final int WAIT_AND_REPORT = 3;
    private final ActivityManagerService mService;
    private final ProcessRecord mProc;
    private final Handler mHandler;

    /* JADX WARN: Removed duplicated region for block: B:20:0x009b  */
    /* JADX WARN: Removed duplicated region for block: B:21:0x00b9  */
    /* JADX WARN: Removed duplicated region for block: B:24:0x00ff  */
    /* JADX WARN: Removed duplicated region for block: B:27:0x0124  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public AppNotRespondingDialog(com.android.server.am.ActivityManagerService r9, android.content.Context r10, com.android.server.am.ProcessRecord r11, com.android.server.am.ActivityRecord r12, boolean r13) {
        /*
            Method dump skipped, instructions count: 359
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.AppNotRespondingDialog.<init>(com.android.server.am.ActivityManagerService, android.content.Context, com.android.server.am.ProcessRecord, com.android.server.am.ActivityRecord, boolean):void");
    }

    @Override // android.app.Dialog
    public void onStop() {
    }
}