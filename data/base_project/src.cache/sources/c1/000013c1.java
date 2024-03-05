package android.text;

/* loaded from: TextWatcher.class */
public interface TextWatcher extends NoCopySpan {
    void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3);

    void onTextChanged(CharSequence charSequence, int i, int i2, int i3);

    void afterTextChanged(Editable editable);
}