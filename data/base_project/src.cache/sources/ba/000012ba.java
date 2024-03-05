package android.support.v7.internal.widget;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Spinner;

/* loaded from: TintSpinner.class */
public class TintSpinner extends Spinner {
    private static final int[] TINT_ATTRS = {16842964, 16843126};

    public TintSpinner(Context context) {
        this(context, null);
    }

    public TintSpinner(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 16842881);
    }

    public TintSpinner(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        TintTypedArray obtainStyledAttributes = TintTypedArray.obtainStyledAttributes(context, attributeSet, TINT_ATTRS, i, 0);
        setBackgroundDrawable(obtainStyledAttributes.getDrawable(0));
        if (Build.VERSION.SDK_INT >= 16 && obtainStyledAttributes.hasValue(1)) {
            setPopupBackgroundDrawable(obtainStyledAttributes.getDrawable(1));
        }
        obtainStyledAttributes.recycle();
    }
}