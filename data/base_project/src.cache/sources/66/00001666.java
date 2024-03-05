package android.view.inputmethod;

import android.os.Bundle;
import android.view.KeyEvent;

/* loaded from: InputConnection.class */
public interface InputConnection {
    public static final int GET_TEXT_WITH_STYLES = 1;
    public static final int GET_EXTRACTED_TEXT_MONITOR = 1;

    CharSequence getTextBeforeCursor(int i, int i2);

    CharSequence getTextAfterCursor(int i, int i2);

    CharSequence getSelectedText(int i);

    int getCursorCapsMode(int i);

    ExtractedText getExtractedText(ExtractedTextRequest extractedTextRequest, int i);

    boolean deleteSurroundingText(int i, int i2);

    boolean setComposingText(CharSequence charSequence, int i);

    boolean setComposingRegion(int i, int i2);

    boolean finishComposingText();

    boolean commitText(CharSequence charSequence, int i);

    boolean commitCompletion(CompletionInfo completionInfo);

    boolean commitCorrection(CorrectionInfo correctionInfo);

    boolean setSelection(int i, int i2);

    boolean performEditorAction(int i);

    boolean performContextMenuAction(int i);

    boolean beginBatchEdit();

    boolean endBatchEdit();

    boolean sendKeyEvent(KeyEvent keyEvent);

    boolean clearMetaKeyStates(int i);

    boolean reportFullscreenMode(boolean z);

    boolean performPrivateCommand(String str, Bundle bundle);
}