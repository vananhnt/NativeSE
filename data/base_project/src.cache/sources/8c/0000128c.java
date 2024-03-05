package android.support.v7.internal.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.appcompat.R;
import android.support.v7.internal.text.AllCapsTransformationMethod;
import android.util.AttributeSet;
import android.widget.TextView;

/* loaded from: CompatTextView.class */
public class CompatTextView extends TextView {
    public CompatTextView(Context context) {
        this(context, null);
    }

    public CompatTextView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public CompatTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.CompatTextView, i, 0);
        boolean z = obtainStyledAttributes.getBoolean(R.styleable.CompatTextView_textAllCaps, false);
        obtainStyledAttributes.recycle();
        if (z) {
            setTransformationMethod(new AllCapsTransformationMethod(context));
        }
    }
}