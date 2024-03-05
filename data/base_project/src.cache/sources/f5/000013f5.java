package android.text.style;

import android.text.TextPaint;
import android.view.View;

/* loaded from: ClickableSpan.class */
public abstract class ClickableSpan extends CharacterStyle implements UpdateAppearance {
    public abstract void onClick(View view);

    @Override // android.text.style.CharacterStyle
    public void updateDrawState(TextPaint ds) {
        ds.setColor(ds.linkColor);
        ds.setUnderlineText(true);
    }
}