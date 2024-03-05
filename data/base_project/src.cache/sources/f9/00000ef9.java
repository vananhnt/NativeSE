package android.support.v4.app;

import android.app.Activity;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: ActivityCompatHoneycomb.class */
public class ActivityCompatHoneycomb {
    ActivityCompatHoneycomb() {
    }

    static void dump(Activity activity, String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        activity.dump(str, fileDescriptor, printWriter, strArr);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void invalidateOptionsMenu(Activity activity) {
        activity.invalidateOptionsMenu();
    }
}