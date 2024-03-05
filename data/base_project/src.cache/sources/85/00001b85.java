package com.android.internal.widget;

import android.content.Context;
import android.content.res.Resources;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewRootImpl;
import com.android.internal.R;

/* loaded from: PasswordEntryKeyboardHelper.class */
public class PasswordEntryKeyboardHelper implements KeyboardView.OnKeyboardActionListener {
    public static final int KEYBOARD_MODE_ALPHA = 0;
    public static final int KEYBOARD_MODE_NUMERIC = 1;
    private static final int KEYBOARD_STATE_NORMAL = 0;
    private static final int KEYBOARD_STATE_SHIFTED = 1;
    private static final int KEYBOARD_STATE_CAPSLOCK = 2;
    private static final String TAG = "PasswordEntryKeyboardHelper";
    private int mKeyboardMode;
    private int mKeyboardState;
    private PasswordEntryKeyboard mQwertyKeyboard;
    private PasswordEntryKeyboard mQwertyKeyboardShifted;
    private PasswordEntryKeyboard mSymbolsKeyboard;
    private PasswordEntryKeyboard mSymbolsKeyboardShifted;
    private PasswordEntryKeyboard mNumericKeyboard;
    private final Context mContext;
    private final View mTargetView;
    private final KeyboardView mKeyboardView;
    private long[] mVibratePattern;
    private boolean mEnableHaptics;
    private static final int NUMERIC = 0;
    private static final int QWERTY = 1;
    private static final int QWERTY_SHIFTED = 2;
    private static final int SYMBOLS = 3;
    private static final int SYMBOLS_SHIFTED = 4;
    int[] mLayouts;
    private boolean mUsingScreenWidth;

    public PasswordEntryKeyboardHelper(Context context, KeyboardView keyboardView, View targetView) {
        this(context, keyboardView, targetView, true, null);
    }

    public PasswordEntryKeyboardHelper(Context context, KeyboardView keyboardView, View targetView, boolean useFullScreenWidth) {
        this(context, keyboardView, targetView, useFullScreenWidth, null);
    }

    public PasswordEntryKeyboardHelper(Context context, KeyboardView keyboardView, View targetView, boolean useFullScreenWidth, int[] layouts) {
        this.mKeyboardMode = 0;
        this.mKeyboardState = 0;
        this.mEnableHaptics = false;
        this.mLayouts = new int[]{R.xml.password_kbd_numeric, R.xml.password_kbd_qwerty, R.xml.password_kbd_qwerty_shifted, R.xml.password_kbd_symbols, R.xml.password_kbd_symbols_shift};
        this.mContext = context;
        this.mTargetView = targetView;
        this.mKeyboardView = keyboardView;
        this.mKeyboardView.setOnKeyboardActionListener(this);
        this.mUsingScreenWidth = useFullScreenWidth;
        if (layouts != null) {
            if (layouts.length != this.mLayouts.length) {
                throw new RuntimeException("Wrong number of layouts");
            }
            for (int i = 0; i < this.mLayouts.length; i++) {
                this.mLayouts[i] = layouts[i];
            }
        }
        createKeyboards();
    }

    public void createKeyboards() {
        ViewGroup.LayoutParams lp = this.mKeyboardView.getLayoutParams();
        if (this.mUsingScreenWidth || lp.width == -1) {
            createKeyboardsWithDefaultWidth();
        } else {
            createKeyboardsWithSpecificSize(lp.width, lp.height);
        }
    }

    public void setEnableHaptics(boolean enabled) {
        this.mEnableHaptics = enabled;
    }

    public boolean isAlpha() {
        return this.mKeyboardMode == 0;
    }

    private void createKeyboardsWithSpecificSize(int width, int height) {
        this.mNumericKeyboard = new PasswordEntryKeyboard(this.mContext, this.mLayouts[0], width, height);
        this.mQwertyKeyboard = new PasswordEntryKeyboard(this.mContext, this.mLayouts[1], (int) R.id.mode_normal, width, height);
        this.mQwertyKeyboard.enableShiftLock();
        this.mQwertyKeyboardShifted = new PasswordEntryKeyboard(this.mContext, this.mLayouts[2], (int) R.id.mode_normal, width, height);
        this.mQwertyKeyboardShifted.enableShiftLock();
        this.mQwertyKeyboardShifted.setShifted(true);
        this.mSymbolsKeyboard = new PasswordEntryKeyboard(this.mContext, this.mLayouts[3], width, height);
        this.mSymbolsKeyboard.enableShiftLock();
        this.mSymbolsKeyboardShifted = new PasswordEntryKeyboard(this.mContext, this.mLayouts[4], width, height);
        this.mSymbolsKeyboardShifted.enableShiftLock();
        this.mSymbolsKeyboardShifted.setShifted(true);
    }

    private void createKeyboardsWithDefaultWidth() {
        this.mNumericKeyboard = new PasswordEntryKeyboard(this.mContext, this.mLayouts[0]);
        this.mQwertyKeyboard = new PasswordEntryKeyboard(this.mContext, this.mLayouts[1], R.id.mode_normal);
        this.mQwertyKeyboard.enableShiftLock();
        this.mQwertyKeyboardShifted = new PasswordEntryKeyboard(this.mContext, this.mLayouts[2], R.id.mode_normal);
        this.mQwertyKeyboardShifted.enableShiftLock();
        this.mQwertyKeyboardShifted.setShifted(true);
        this.mSymbolsKeyboard = new PasswordEntryKeyboard(this.mContext, this.mLayouts[3]);
        this.mSymbolsKeyboard.enableShiftLock();
        this.mSymbolsKeyboardShifted = new PasswordEntryKeyboard(this.mContext, this.mLayouts[4]);
        this.mSymbolsKeyboardShifted.enableShiftLock();
        this.mSymbolsKeyboardShifted.setShifted(true);
    }

    public void setKeyboardMode(int mode) {
        switch (mode) {
            case 0:
                this.mKeyboardView.setKeyboard(this.mQwertyKeyboard);
                this.mKeyboardState = 0;
                boolean visiblePassword = Settings.System.getInt(this.mContext.getContentResolver(), Settings.System.TEXT_SHOW_PASSWORD, 1) != 0;
                KeyboardView keyboardView = this.mKeyboardView;
                if (visiblePassword) {
                }
                keyboardView.setPreviewEnabled(false);
                break;
            case 1:
                this.mKeyboardView.setKeyboard(this.mNumericKeyboard);
                this.mKeyboardState = 0;
                this.mKeyboardView.setPreviewEnabled(false);
                break;
        }
        this.mKeyboardMode = mode;
    }

    private void sendKeyEventsToTarget(int character) {
        ViewRootImpl viewRootImpl = this.mTargetView.getViewRootImpl();
        KeyEvent[] events = KeyCharacterMap.load(-1).getEvents(new char[]{(char) character});
        if (events != null) {
            for (KeyEvent event : events) {
                viewRootImpl.dispatchInputEvent(KeyEvent.changeFlags(event, event.getFlags() | 2 | 4));
            }
        }
    }

    public void sendDownUpKeyEvents(int keyEventCode) {
        long eventTime = SystemClock.uptimeMillis();
        ViewRootImpl viewRootImpl = this.mTargetView.getViewRootImpl();
        viewRootImpl.dispatchKeyFromIme(new KeyEvent(eventTime, eventTime, 0, keyEventCode, 0, 0, -1, 0, 6));
        viewRootImpl.dispatchKeyFromIme(new KeyEvent(eventTime, eventTime, 1, keyEventCode, 0, 0, -1, 0, 6));
    }

    @Override // android.inputmethodservice.KeyboardView.OnKeyboardActionListener
    public void onKey(int primaryCode, int[] keyCodes) {
        if (primaryCode == -5) {
            handleBackspace();
        } else if (primaryCode == -1) {
            handleShift();
        } else if (primaryCode == -3) {
            handleClose();
        } else if (primaryCode == -2 && this.mKeyboardView != null) {
            handleModeChange();
        } else {
            handleCharacter(primaryCode, keyCodes);
            if (this.mKeyboardState == 1) {
                this.mKeyboardState = 2;
                handleShift();
            }
        }
    }

    public void setVibratePattern(int id) {
        int[] tmpArray = null;
        try {
            tmpArray = this.mContext.getResources().getIntArray(id);
        } catch (Resources.NotFoundException e) {
            if (id != 0) {
                Log.e(TAG, "Vibrate pattern missing", e);
            }
        }
        if (tmpArray == null) {
            this.mVibratePattern = null;
            return;
        }
        this.mVibratePattern = new long[tmpArray.length];
        for (int i = 0; i < tmpArray.length; i++) {
            this.mVibratePattern[i] = tmpArray[i];
        }
    }

    private void handleModeChange() {
        Keyboard current = this.mKeyboardView.getKeyboard();
        Keyboard next = null;
        if (current == this.mQwertyKeyboard || current == this.mQwertyKeyboardShifted) {
            next = this.mSymbolsKeyboard;
        } else if (current == this.mSymbolsKeyboard || current == this.mSymbolsKeyboardShifted) {
            next = this.mQwertyKeyboard;
        }
        if (next != null) {
            this.mKeyboardView.setKeyboard(next);
            this.mKeyboardState = 0;
        }
    }

    public void handleBackspace() {
        sendDownUpKeyEvents(67);
        performHapticFeedback();
    }

    private void handleShift() {
        if (this.mKeyboardView == null) {
            return;
        }
        Keyboard current = this.mKeyboardView.getKeyboard();
        PasswordEntryKeyboard next = null;
        boolean isAlphaMode = current == this.mQwertyKeyboard || current == this.mQwertyKeyboardShifted;
        if (this.mKeyboardState == 0) {
            this.mKeyboardState = isAlphaMode ? 1 : 2;
            next = isAlphaMode ? this.mQwertyKeyboardShifted : this.mSymbolsKeyboardShifted;
        } else if (this.mKeyboardState == 1) {
            this.mKeyboardState = 2;
            next = isAlphaMode ? this.mQwertyKeyboardShifted : this.mSymbolsKeyboardShifted;
        } else if (this.mKeyboardState == 2) {
            this.mKeyboardState = 0;
            next = isAlphaMode ? this.mQwertyKeyboard : this.mSymbolsKeyboard;
        }
        if (next != null) {
            if (next != current) {
                this.mKeyboardView.setKeyboard(next);
            }
            next.setShiftLocked(this.mKeyboardState == 2);
            this.mKeyboardView.setShifted(this.mKeyboardState != 0);
        }
    }

    private void handleCharacter(int primaryCode, int[] keyCodes) {
        if (this.mKeyboardView.isShifted() && primaryCode != 32 && primaryCode != 10) {
            primaryCode = Character.toUpperCase(primaryCode);
        }
        sendKeyEventsToTarget(primaryCode);
    }

    private void handleClose() {
    }

    @Override // android.inputmethodservice.KeyboardView.OnKeyboardActionListener
    public void onPress(int primaryCode) {
        performHapticFeedback();
    }

    private void performHapticFeedback() {
        if (this.mEnableHaptics) {
            this.mKeyboardView.performHapticFeedback(1, 3);
        }
    }

    @Override // android.inputmethodservice.KeyboardView.OnKeyboardActionListener
    public void onRelease(int primaryCode) {
    }

    @Override // android.inputmethodservice.KeyboardView.OnKeyboardActionListener
    public void onText(CharSequence text) {
    }

    @Override // android.inputmethodservice.KeyboardView.OnKeyboardActionListener
    public void swipeDown() {
    }

    @Override // android.inputmethodservice.KeyboardView.OnKeyboardActionListener
    public void swipeLeft() {
    }

    @Override // android.inputmethodservice.KeyboardView.OnKeyboardActionListener
    public void swipeRight() {
    }

    @Override // android.inputmethodservice.KeyboardView.OnKeyboardActionListener
    public void swipeUp() {
    }
}