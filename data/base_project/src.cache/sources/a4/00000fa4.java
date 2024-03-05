package android.support.v4.content;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: ContextCompatJellybean.class */
public class ContextCompatJellybean {
    ContextCompatJellybean() {
    }

    public static void startActivities(Context context, Intent[] intentArr, Bundle bundle) {
        context.startActivities(intentArr, bundle);
    }
}