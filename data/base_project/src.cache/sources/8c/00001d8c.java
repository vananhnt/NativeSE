package com.android.server.am;

import com.android.server.firewall.IntentFirewall;

/* loaded from: ActivityManagerService$IntentFirewallInterface.class */
class ActivityManagerService$IntentFirewallInterface implements IntentFirewall.AMSInterface {
    final /* synthetic */ ActivityManagerService this$0;

    ActivityManagerService$IntentFirewallInterface(ActivityManagerService activityManagerService) {
        this.this$0 = activityManagerService;
    }

    @Override // com.android.server.firewall.IntentFirewall.AMSInterface
    public int checkComponentPermission(String permission, int pid, int uid, int owningUid, boolean exported) {
        return this.this$0.checkComponentPermission(permission, pid, uid, owningUid, exported);
    }

    @Override // com.android.server.firewall.IntentFirewall.AMSInterface
    public Object getAMSLock() {
        return this.this$0;
    }
}