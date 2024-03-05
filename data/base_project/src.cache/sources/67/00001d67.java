package com.android.server.am;

import android.os.IBinder;
import com.android.server.IntentResolver;
import java.util.List;

/* loaded from: ActivityManagerService$1.class */
class ActivityManagerService$1 extends IntentResolver<BroadcastFilter, BroadcastFilter> {
    final /* synthetic */ ActivityManagerService this$0;

    ActivityManagerService$1(ActivityManagerService activityManagerService) {
        this.this$0 = activityManagerService;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.server.IntentResolver
    public boolean allowFilterResult(BroadcastFilter filter, List<BroadcastFilter> dest) {
        IBinder target = filter.receiverList.receiver.asBinder();
        for (int i = dest.size() - 1; i >= 0; i--) {
            if (dest.get(i).receiverList.receiver.asBinder() == target) {
                return false;
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.server.IntentResolver
    public BroadcastFilter newResult(BroadcastFilter filter, int match, int userId) {
        if (userId == -1 || filter.owningUserId == -1 || userId == filter.owningUserId) {
            return (BroadcastFilter) super.newResult((ActivityManagerService$1) filter, match, userId);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.android.server.IntentResolver
    public BroadcastFilter[] newArray(int size) {
        return new BroadcastFilter[size];
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.server.IntentResolver
    public boolean isPackageForFilter(String packageName, BroadcastFilter filter) {
        return packageName.equals(filter.packageName);
    }
}