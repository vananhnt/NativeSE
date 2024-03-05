package android.inputmethodservice;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

/* loaded from: ExtractButton.class */
class ExtractButton extends Button {
    public ExtractButton(Context context) {
        super(context, null);
    }

    public ExtractButton(Context context, AttributeSet attrs) {
        super(context, attrs, 16842824);
    }

    public ExtractButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override // android.view.View
    public boolean hasWindowFocus() {
        return isEnabled() && getVisibility() == 0;
    }
}