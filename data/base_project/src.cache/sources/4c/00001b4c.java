package com.android.internal.view;

import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.CorrectionInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import com.android.internal.view.IInputContextCallback;

/* loaded from: InputConnectionWrapper.class */
public class InputConnectionWrapper implements InputConnection {
    private static final int MAX_WAIT_TIME_MILLIS = 2000;
    private final IInputContext mIInputContext;

    /* loaded from: InputConnectionWrapper$InputContextCallback.class */
    static class InputContextCallback extends IInputContextCallback.Stub {
        private static final String TAG = "InputConnectionWrapper.ICC";
        public int mSeq;
        public boolean mHaveValue;
        public CharSequence mTextBeforeCursor;
        public CharSequence mTextAfterCursor;
        public CharSequence mSelectedText;
        public ExtractedText mExtractedText;
        public int mCursorCapsMode;
        private static InputContextCallback sInstance = new InputContextCallback();
        private static int sSequenceNumber = 1;

        InputContextCallback() {
        }

        static /* synthetic */ InputContextCallback access$000() {
            return getInstance();
        }

        private static InputContextCallback getInstance() {
            InputContextCallback callback;
            InputContextCallback inputContextCallback;
            synchronized (InputContextCallback.class) {
                if (sInstance != null) {
                    callback = sInstance;
                    sInstance = null;
                    callback.mHaveValue = false;
                } else {
                    callback = new InputContextCallback();
                }
                int i = sSequenceNumber;
                sSequenceNumber = i + 1;
                callback.mSeq = i;
                inputContextCallback = callback;
            }
            return inputContextCallback;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void dispose() {
            synchronized (InputContextCallback.class) {
                if (sInstance == null) {
                    this.mTextAfterCursor = null;
                    this.mTextBeforeCursor = null;
                    this.mExtractedText = null;
                    sInstance = this;
                }
            }
        }

        @Override // com.android.internal.view.IInputContextCallback
        public void setTextBeforeCursor(CharSequence textBeforeCursor, int seq) {
            synchronized (this) {
                if (seq == this.mSeq) {
                    this.mTextBeforeCursor = textBeforeCursor;
                    this.mHaveValue = true;
                    notifyAll();
                } else {
                    Log.i(TAG, "Got out-of-sequence callback " + seq + " (expected " + this.mSeq + ") in setTextBeforeCursor, ignoring.");
                }
            }
        }

        @Override // com.android.internal.view.IInputContextCallback
        public void setTextAfterCursor(CharSequence textAfterCursor, int seq) {
            synchronized (this) {
                if (seq == this.mSeq) {
                    this.mTextAfterCursor = textAfterCursor;
                    this.mHaveValue = true;
                    notifyAll();
                } else {
                    Log.i(TAG, "Got out-of-sequence callback " + seq + " (expected " + this.mSeq + ") in setTextAfterCursor, ignoring.");
                }
            }
        }

        @Override // com.android.internal.view.IInputContextCallback
        public void setSelectedText(CharSequence selectedText, int seq) {
            synchronized (this) {
                if (seq == this.mSeq) {
                    this.mSelectedText = selectedText;
                    this.mHaveValue = true;
                    notifyAll();
                } else {
                    Log.i(TAG, "Got out-of-sequence callback " + seq + " (expected " + this.mSeq + ") in setSelectedText, ignoring.");
                }
            }
        }

        @Override // com.android.internal.view.IInputContextCallback
        public void setCursorCapsMode(int capsMode, int seq) {
            synchronized (this) {
                if (seq == this.mSeq) {
                    this.mCursorCapsMode = capsMode;
                    this.mHaveValue = true;
                    notifyAll();
                } else {
                    Log.i(TAG, "Got out-of-sequence callback " + seq + " (expected " + this.mSeq + ") in setCursorCapsMode, ignoring.");
                }
            }
        }

        @Override // com.android.internal.view.IInputContextCallback
        public void setExtractedText(ExtractedText extractedText, int seq) {
            synchronized (this) {
                if (seq == this.mSeq) {
                    this.mExtractedText = extractedText;
                    this.mHaveValue = true;
                    notifyAll();
                } else {
                    Log.i(TAG, "Got out-of-sequence callback " + seq + " (expected " + this.mSeq + ") in setExtractedText, ignoring.");
                }
            }
        }

        void waitForResultLocked() {
            long startTime = SystemClock.uptimeMillis();
            long endTime = startTime + 2000;
            while (!this.mHaveValue) {
                long remainingTime = endTime - SystemClock.uptimeMillis();
                if (remainingTime <= 0) {
                    Log.w(TAG, "Timed out waiting on IInputContextCallback");
                    return;
                }
                try {
                    wait(remainingTime);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    public InputConnectionWrapper(IInputContext inputContext) {
        this.mIInputContext = inputContext;
    }

    @Override // android.view.inputmethod.InputConnection
    public CharSequence getTextAfterCursor(int length, int flags) {
        CharSequence value = null;
        try {
            InputContextCallback callback = InputContextCallback.access$000();
            this.mIInputContext.getTextAfterCursor(length, flags, callback.mSeq, callback);
            synchronized (callback) {
                callback.waitForResultLocked();
                if (callback.mHaveValue) {
                    value = callback.mTextAfterCursor;
                }
            }
            callback.dispose();
            return value;
        } catch (RemoteException e) {
            return null;
        }
    }

    @Override // android.view.inputmethod.InputConnection
    public CharSequence getTextBeforeCursor(int length, int flags) {
        CharSequence value = null;
        try {
            InputContextCallback callback = InputContextCallback.access$000();
            this.mIInputContext.getTextBeforeCursor(length, flags, callback.mSeq, callback);
            synchronized (callback) {
                callback.waitForResultLocked();
                if (callback.mHaveValue) {
                    value = callback.mTextBeforeCursor;
                }
            }
            callback.dispose();
            return value;
        } catch (RemoteException e) {
            return null;
        }
    }

    @Override // android.view.inputmethod.InputConnection
    public CharSequence getSelectedText(int flags) {
        CharSequence value = null;
        try {
            InputContextCallback callback = InputContextCallback.access$000();
            this.mIInputContext.getSelectedText(flags, callback.mSeq, callback);
            synchronized (callback) {
                callback.waitForResultLocked();
                if (callback.mHaveValue) {
                    value = callback.mSelectedText;
                }
            }
            callback.dispose();
            return value;
        } catch (RemoteException e) {
            return null;
        }
    }

    @Override // android.view.inputmethod.InputConnection
    public int getCursorCapsMode(int reqModes) {
        int value = 0;
        try {
            InputContextCallback callback = InputContextCallback.access$000();
            this.mIInputContext.getCursorCapsMode(reqModes, callback.mSeq, callback);
            synchronized (callback) {
                callback.waitForResultLocked();
                if (callback.mHaveValue) {
                    value = callback.mCursorCapsMode;
                }
            }
            callback.dispose();
            return value;
        } catch (RemoteException e) {
            return 0;
        }
    }

    @Override // android.view.inputmethod.InputConnection
    public ExtractedText getExtractedText(ExtractedTextRequest request, int flags) {
        ExtractedText value = null;
        try {
            InputContextCallback callback = InputContextCallback.access$000();
            this.mIInputContext.getExtractedText(request, flags, callback.mSeq, callback);
            synchronized (callback) {
                callback.waitForResultLocked();
                if (callback.mHaveValue) {
                    value = callback.mExtractedText;
                }
            }
            callback.dispose();
            return value;
        } catch (RemoteException e) {
            return null;
        }
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean commitText(CharSequence text, int newCursorPosition) {
        try {
            this.mIInputContext.commitText(text, newCursorPosition);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean commitCompletion(CompletionInfo text) {
        try {
            this.mIInputContext.commitCompletion(text);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean commitCorrection(CorrectionInfo correctionInfo) {
        try {
            this.mIInputContext.commitCorrection(correctionInfo);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean setSelection(int start, int end) {
        try {
            this.mIInputContext.setSelection(start, end);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean performEditorAction(int actionCode) {
        try {
            this.mIInputContext.performEditorAction(actionCode);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean performContextMenuAction(int id) {
        try {
            this.mIInputContext.performContextMenuAction(id);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean setComposingRegion(int start, int end) {
        try {
            this.mIInputContext.setComposingRegion(start, end);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean setComposingText(CharSequence text, int newCursorPosition) {
        try {
            this.mIInputContext.setComposingText(text, newCursorPosition);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean finishComposingText() {
        try {
            this.mIInputContext.finishComposingText();
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean beginBatchEdit() {
        try {
            this.mIInputContext.beginBatchEdit();
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean endBatchEdit() {
        try {
            this.mIInputContext.endBatchEdit();
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean sendKeyEvent(KeyEvent event) {
        try {
            this.mIInputContext.sendKeyEvent(event);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean clearMetaKeyStates(int states) {
        try {
            this.mIInputContext.clearMetaKeyStates(states);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean deleteSurroundingText(int beforeLength, int afterLength) {
        try {
            this.mIInputContext.deleteSurroundingText(beforeLength, afterLength);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean reportFullscreenMode(boolean enabled) {
        try {
            this.mIInputContext.reportFullscreenMode(enabled);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean performPrivateCommand(String action, Bundle data) {
        try {
            this.mIInputContext.performPrivateCommand(action, data);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }
}