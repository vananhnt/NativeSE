package android.text;

@Deprecated
/* loaded from: ClipboardManager.class */
public abstract class ClipboardManager {
    public abstract CharSequence getText();

    public abstract void setText(CharSequence charSequence);

    public abstract boolean hasText();
}