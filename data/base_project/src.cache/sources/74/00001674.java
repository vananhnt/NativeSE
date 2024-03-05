package android.view.inputmethod;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;

/* loaded from: InputMethodSession.class */
public interface InputMethodSession {

    /* loaded from: InputMethodSession$EventCallback.class */
    public interface EventCallback {
        void finishedEvent(int i, boolean z);
    }

    void finishInput();

    void updateSelection(int i, int i2, int i3, int i4, int i5, int i6);

    void viewClicked(boolean z);

    void updateCursor(Rect rect);

    void displayCompletions(CompletionInfo[] completionInfoArr);

    void updateExtractedText(int i, ExtractedText extractedText);

    void dispatchKeyEvent(int i, KeyEvent keyEvent, EventCallback eventCallback);

    void dispatchTrackballEvent(int i, MotionEvent motionEvent, EventCallback eventCallback);

    void dispatchGenericMotionEvent(int i, MotionEvent motionEvent, EventCallback eventCallback);

    void appPrivateCommand(String str, Bundle bundle);

    void toggleSoftInput(int i, int i2);
}