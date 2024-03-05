package android.support.v4.view;

import android.view.KeyEvent;
import android.view.View;

/* loaded from: KeyEventCompatEclair.class */
class KeyEventCompatEclair {
    KeyEventCompatEclair() {
    }

    public static boolean dispatch(KeyEvent keyEvent, KeyEvent.Callback callback, Object obj, Object obj2) {
        return keyEvent.dispatch(callback, (KeyEvent.DispatcherState) obj, obj2);
    }

    public static Object getKeyDispatcherState(View view) {
        return view.getKeyDispatcherState();
    }

    public static boolean isTracking(KeyEvent keyEvent) {
        return keyEvent.isTracking();
    }

    public static void startTracking(KeyEvent keyEvent) {
        keyEvent.startTracking();
    }
}