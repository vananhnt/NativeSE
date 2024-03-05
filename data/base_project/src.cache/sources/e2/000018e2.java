package android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import com.android.internal.R;

@Deprecated
/* loaded from: TwoLineListItem.class */
public class TwoLineListItem extends RelativeLayout {
    private TextView mText1;
    private TextView mText2;

    public TwoLineListItem(Context context) {
        this(context, null, 0);
    }

    public TwoLineListItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TwoLineListItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TwoLineListItem, defStyle, 0);
        a.recycle();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mText1 = (TextView) findViewById(16908308);
        this.mText2 = (TextView) findViewById(16908309);
    }

    public TextView getText1() {
        return this.mText1;
    }

    public TextView getText2() {
        return this.mText2;
    }

    @Override // android.widget.RelativeLayout, android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(TwoLineListItem.class.getName());
    }

    @Override // android.widget.RelativeLayout, android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(TwoLineListItem.class.getName());
    }
}