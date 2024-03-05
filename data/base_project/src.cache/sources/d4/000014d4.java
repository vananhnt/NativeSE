package android.view;

/* loaded from: FallbackEventHandler.class */
public interface FallbackEventHandler {
    void setView(View view);

    void preDispatchKeyEvent(KeyEvent keyEvent);

    boolean dispatchKeyEvent(KeyEvent keyEvent);
}