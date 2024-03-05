package com.android.server.am;

import android.net.Uri;
import java.util.ArrayList;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: ActivityManagerService$NeededUriGrants.class */
public class ActivityManagerService$NeededUriGrants extends ArrayList<Uri> {
    final String targetPkg;
    final int targetUid;
    final int flags;

    ActivityManagerService$NeededUriGrants(String targetPkg, int targetUid, int flags) {
        this.targetPkg = targetPkg;
        this.targetUid = targetUid;
        this.flags = flags;
    }
}