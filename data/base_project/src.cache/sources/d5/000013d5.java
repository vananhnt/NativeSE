package android.text.method;

import android.text.Spannable;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.TextView;

/* loaded from: MovementMethod.class */
public interface MovementMethod {
    void initialize(TextView textView, Spannable spannable);

    boolean onKeyDown(TextView textView, Spannable spannable, int i, KeyEvent keyEvent);

    boolean onKeyUp(TextView textView, Spannable spannable, int i, KeyEvent keyEvent);

    boolean onKeyOther(TextView textView, Spannable spannable, KeyEvent keyEvent);

    void onTakeFocus(TextView textView, Spannable spannable, int i);

    boolean onTrackballEvent(TextView textView, Spannable spannable, MotionEvent motionEvent);

    boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent motionEvent);

    boolean onGenericMotionEvent(TextView textView, Spannable spannable, MotionEvent motionEvent);

    boolean canSelectArbitrarily();
}