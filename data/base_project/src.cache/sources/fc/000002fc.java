package android.content;

import android.view.KeyEvent;

/* loaded from: DialogInterface.class */
public interface DialogInterface {
    public static final int BUTTON_POSITIVE = -1;
    public static final int BUTTON_NEGATIVE = -2;
    public static final int BUTTON_NEUTRAL = -3;
    @Deprecated
    public static final int BUTTON1 = -1;
    @Deprecated
    public static final int BUTTON2 = -2;
    @Deprecated
    public static final int BUTTON3 = -3;

    /* loaded from: DialogInterface$OnCancelListener.class */
    public interface OnCancelListener {
        void onCancel(DialogInterface dialogInterface);
    }

    /* loaded from: DialogInterface$OnClickListener.class */
    public interface OnClickListener {
        void onClick(DialogInterface dialogInterface, int i);
    }

    /* loaded from: DialogInterface$OnDismissListener.class */
    public interface OnDismissListener {
        void onDismiss(DialogInterface dialogInterface);
    }

    /* loaded from: DialogInterface$OnKeyListener.class */
    public interface OnKeyListener {
        boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent);
    }

    /* loaded from: DialogInterface$OnMultiChoiceClickListener.class */
    public interface OnMultiChoiceClickListener {
        void onClick(DialogInterface dialogInterface, int i, boolean z);
    }

    /* loaded from: DialogInterface$OnShowListener.class */
    public interface OnShowListener {
        void onShow(DialogInterface dialogInterface);
    }

    void cancel();

    void dismiss();
}