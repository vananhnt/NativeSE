package com.android.internal.widget;

import android.content.Context;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;

/* loaded from: PasswordEntryKeyboardView.class */
public class PasswordEntryKeyboardView extends KeyboardView {
    static final int KEYCODE_OPTIONS = -100;
    static final int KEYCODE_SHIFT_LONGPRESS = -101;
    static final int KEYCODE_VOICE = -102;
    static final int KEYCODE_F1 = -103;
    static final int KEYCODE_NEXT_LANGUAGE = -104;

    public PasswordEntryKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PasswordEntryKeyboardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override // android.inputmethodservice.KeyboardView
    public boolean setShifted(boolean shifted) {
        boolean result = super.setShifted(shifted);
        int[] indices = getKeyboard().getShiftKeyIndices();
        for (int index : indices) {
            invalidateKey(index);
        }
        return result;
    }
}