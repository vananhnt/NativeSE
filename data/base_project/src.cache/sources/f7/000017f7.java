package android.widget;

import android.content.Context;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.QwertyKeyListener;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.AutoCompleteTextView;

/* loaded from: MultiAutoCompleteTextView.class */
public class MultiAutoCompleteTextView extends AutoCompleteTextView {
    private Tokenizer mTokenizer;

    /* loaded from: MultiAutoCompleteTextView$Tokenizer.class */
    public interface Tokenizer {
        int findTokenStart(CharSequence charSequence, int i);

        int findTokenEnd(CharSequence charSequence, int i);

        CharSequence terminateToken(CharSequence charSequence);
    }

    public MultiAutoCompleteTextView(Context context) {
        this(context, null);
    }

    public MultiAutoCompleteTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 16842859);
    }

    public MultiAutoCompleteTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    void finishInit() {
    }

    public void setTokenizer(Tokenizer t) {
        this.mTokenizer = t;
    }

    @Override // android.widget.AutoCompleteTextView
    protected void performFiltering(CharSequence text, int keyCode) {
        if (enoughToFilter()) {
            int end = getSelectionEnd();
            int start = this.mTokenizer.findTokenStart(text, end);
            performFiltering(text, start, end, keyCode);
            return;
        }
        dismissDropDown();
        Filter f = getFilter();
        if (f != null) {
            f.filter(null);
        }
    }

    @Override // android.widget.AutoCompleteTextView
    public boolean enoughToFilter() {
        Editable text = getText();
        int end = getSelectionEnd();
        if (end < 0 || this.mTokenizer == null) {
            return false;
        }
        int start = this.mTokenizer.findTokenStart(text, end);
        if (end - start >= getThreshold()) {
            return true;
        }
        return false;
    }

    @Override // android.widget.AutoCompleteTextView
    public void performValidation() {
        AutoCompleteTextView.Validator v = getValidator();
        if (v == null || this.mTokenizer == null) {
            return;
        }
        Editable e = getText();
        int length = getText().length();
        while (true) {
            int i = length;
            if (i > 0) {
                int start = this.mTokenizer.findTokenStart(e, i);
                int end = this.mTokenizer.findTokenEnd(e, start);
                CharSequence sub = e.subSequence(start, end);
                if (TextUtils.isEmpty(sub)) {
                    e.replace(start, i, "");
                } else if (!v.isValid(sub)) {
                    e.replace(start, i, this.mTokenizer.terminateToken(v.fixText(sub)));
                }
                length = start;
            } else {
                return;
            }
        }
    }

    protected void performFiltering(CharSequence text, int start, int end, int keyCode) {
        getFilter().filter(text.subSequence(start, end), this);
    }

    @Override // android.widget.AutoCompleteTextView
    protected void replaceText(CharSequence text) {
        clearComposingText();
        int end = getSelectionEnd();
        int start = this.mTokenizer.findTokenStart(getText(), end);
        Editable editable = getText();
        String original = TextUtils.substring(editable, start, end);
        QwertyKeyListener.markAsReplaced(editable, start, end, original);
        editable.replace(start, end, this.mTokenizer.terminateToken(text));
    }

    @Override // android.widget.EditText, android.widget.TextView, android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(MultiAutoCompleteTextView.class.getName());
    }

    @Override // android.widget.EditText, android.widget.TextView, android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(MultiAutoCompleteTextView.class.getName());
    }

    /* loaded from: MultiAutoCompleteTextView$CommaTokenizer.class */
    public static class CommaTokenizer implements Tokenizer {
        @Override // android.widget.MultiAutoCompleteTextView.Tokenizer
        public int findTokenStart(CharSequence text, int cursor) {
            int i = cursor;
            while (i > 0 && text.charAt(i - 1) != ',') {
                i--;
            }
            while (i < cursor && text.charAt(i) == ' ') {
                i++;
            }
            return i;
        }

        @Override // android.widget.MultiAutoCompleteTextView.Tokenizer
        public int findTokenEnd(CharSequence text, int cursor) {
            int len = text.length();
            for (int i = cursor; i < len; i++) {
                if (text.charAt(i) == ',') {
                    return i;
                }
            }
            return len;
        }

        @Override // android.widget.MultiAutoCompleteTextView.Tokenizer
        public CharSequence terminateToken(CharSequence text) {
            int i = text.length();
            while (i > 0 && text.charAt(i - 1) == ' ') {
                i--;
            }
            if (i > 0 && text.charAt(i - 1) == ',') {
                return text;
            }
            if (text instanceof Spanned) {
                SpannableString sp = new SpannableString(((Object) text) + ", ");
                TextUtils.copySpansFrom((Spanned) text, 0, text.length(), Object.class, sp, 0);
                return sp;
            }
            return ((Object) text) + ", ";
        }
    }
}