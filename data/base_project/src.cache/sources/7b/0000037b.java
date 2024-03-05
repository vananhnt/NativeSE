package android.content.pm;

import android.os.Binder;

/* loaded from: KeySet.class */
public class KeySet {
    private Binder token;

    public KeySet(Binder token) {
        this.token = token;
    }

    Binder getToken() {
        return this.token;
    }
}