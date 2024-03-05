package android.inputmethodservice;

import android.content.Context;
import android.util.AttributeSet;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/* loaded from: ExtractEditText.class */
public class ExtractEditText extends EditText {
    private InputMethodService mIME;
    private int mSettingExtractedText;

    public ExtractEditText(Context context) {
        super(context, null);
    }

    public ExtractEditText(Context context, AttributeSet attrs) {
        super(context, attrs, 16842862);
    }

    public ExtractEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setIME(InputMethodService ime) {
        this.mIME = ime;
    }

    public void startInternalChanges() {
        this.mSettingExtractedText++;
    }

    public void finishInternalChanges() {
        this.mSettingExtractedText--;
    }

    @Override // android.widget.TextView
    public void setExtractedText(ExtractedText text) {
        try {
            this.mSettingExtractedText++;
            super.setExtractedText(text);
            this.mSettingExtractedText--;
        } catch (Throwable th) {
            this.mSettingExtractedText--;
            throw th;
        }
    }

    @Override // android.widget.TextView
    protected void onSelectionChanged(int selStart, int selEnd) {
        if (this.mSettingExtractedText == 0 && this.mIME != null && selStart >= 0 && selEnd >= 0) {
            this.mIME.onExtractedSelectionChanged(selStart, selEnd);
        }
    }

    @Override // android.view.View
    public boolean performClick() {
        if (!super.performClick() && this.mIME != null) {
            this.mIME.onExtractedTextClicked();
            return true;
        }
        return false;
    }

    @Override // android.widget.TextView
    public boolean onTextContextMenuItem(int id) {
        if (this.mIME != null && this.mIME.onExtractTextContextMenuItem(id)) {
            if (id == 16908321) {
                stopSelectionActionMode();
                return true;
            }
            return true;
        }
        return super.onTextContextMenuItem(id);
    }

    @Override // android.widget.TextView
    public boolean isInputMethodTarget() {
        return true;
    }

    public boolean hasVerticalScrollBar() {
        return computeVerticalScrollRange() > computeVerticalScrollExtent();
    }

    @Override // android.view.View
    public boolean hasWindowFocus() {
        return isEnabled();
    }

    @Override // android.view.View
    public boolean isFocused() {
        return isEnabled();
    }

    @Override // android.view.View
    public boolean hasFocus() {
        return isEnabled();
    }

    @Override // android.widget.TextView
    protected void viewClicked(InputMethodManager imm) {
        if (this.mIME != null) {
            this.mIME.onViewClicked(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.TextView
    public void deleteText_internal(int start, int end) {
        this.mIME.onExtractedDeleteText(start, end);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.TextView
    public void replaceText_internal(int start, int end, CharSequence text) {
        this.mIME.onExtractedReplaceText(start, end, text);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.TextView
    public void setSpan_internal(Object span, int start, int end, int flags) {
        this.mIME.onExtractedSetSpan(span, start, end, flags);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.TextView
    public void setCursorPosition_internal(int start, int end) {
        this.mIME.onExtractedSelectionChanged(start, end);
    }
}