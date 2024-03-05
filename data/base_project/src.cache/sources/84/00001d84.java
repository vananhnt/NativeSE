package com.android.server.am;

import android.os.IBinder;

/* loaded from: ActivityManagerService$9.class */
class ActivityManagerService$9 extends ActivityManagerService$ForegroundToken {
    final /* synthetic */ ActivityManagerService this$0;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    ActivityManagerService$9(final ActivityManagerService activityManagerService) {
        new IBinder.DeathRecipient() { // from class: com.android.server.am.ActivityManagerService$ForegroundToken
            int pid;
            IBinder token;
        };
        this.this$0 = activityManagerService;
    }

    @Override // android.os.IBinder.DeathRecipient
    public void binderDied() {
        this.this$0.foregroundTokenDied(this);
    }
}