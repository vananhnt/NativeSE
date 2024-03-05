package android.webkit;

import android.os.Handler;

/* loaded from: HttpAuthHandler.class */
public class HttpAuthHandler extends Handler {
    public boolean useHttpAuthUsernamePassword() {
        return false;
    }

    public void cancel() {
    }

    public void proceed(String username, String password) {
    }

    public boolean suppressDialog() {
        return false;
    }
}