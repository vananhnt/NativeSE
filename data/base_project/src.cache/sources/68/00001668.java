package android.view.inputmethod;

import android.os.IBinder;
import android.os.ResultReceiver;

/* loaded from: InputMethod.class */
public interface InputMethod {
    public static final String SERVICE_INTERFACE = "android.view.InputMethod";
    public static final String SERVICE_META_DATA = "android.view.im";
    public static final int SHOW_EXPLICIT = 1;
    public static final int SHOW_FORCED = 2;

    /* loaded from: InputMethod$SessionCallback.class */
    public interface SessionCallback {
        void sessionCreated(InputMethodSession inputMethodSession);
    }

    void attachToken(IBinder iBinder);

    void bindInput(InputBinding inputBinding);

    void unbindInput();

    void startInput(InputConnection inputConnection, EditorInfo editorInfo);

    void restartInput(InputConnection inputConnection, EditorInfo editorInfo);

    void createSession(SessionCallback sessionCallback);

    void setSessionEnabled(InputMethodSession inputMethodSession, boolean z);

    void revokeSession(InputMethodSession inputMethodSession);

    void showSoftInput(int i, ResultReceiver resultReceiver);

    void hideSoftInput(int i, ResultReceiver resultReceiver);

    void changeInputMethodSubtype(InputMethodSubtype inputMethodSubtype);
}