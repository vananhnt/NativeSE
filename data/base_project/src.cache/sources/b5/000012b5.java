package android.support.v7.internal.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/* loaded from: TintImageView.class */
public class TintImageView extends ImageView {
    private static final int[] TINT_ATTRS = {16842964, 16843033};
    private final TintManager mTintManager;

    public TintImageView(Context context) {
        this(context, null);
    }

    public TintImageView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public TintImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        TintTypedArray obtainStyledAttributes = TintTypedArray.obtainStyledAttributes(context, attributeSet, TINT_ATTRS, i, 0);
        if (obtainStyledAttributes.length() > 0) {
            if (obtainStyledAttributes.hasValue(0)) {
                setBackgroundDrawable(obtainStyledAttributes.getDrawable(0));
            }
            if (obtainStyledAttributes.hasValue(1)) {
                setImageDrawable(obtainStyledAttributes.getDrawable(1));
            }
        }
        obtainStyledAttributes.recycle();
        this.mTintManager = obtainStyledAttributes.getTintManager();
    }

    @Override // android.widget.ImageView
    public void setImageResource(int i) {
        setImageDrawable(this.mTintManager.getDrawable(i));
    }
}