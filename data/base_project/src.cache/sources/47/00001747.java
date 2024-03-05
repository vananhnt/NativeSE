package android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewDebug;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import com.android.internal.R;

/* loaded from: CheckedTextView.class */
public class CheckedTextView extends TextView implements Checkable {
    private boolean mChecked;
    private int mCheckMarkResource;
    private Drawable mCheckMarkDrawable;
    private int mBasePadding;
    private int mCheckMarkWidth;
    private boolean mNeedRequestlayout;
    private static final int[] CHECKED_STATE_SET = {16842912};

    public CheckedTextView(Context context) {
        this(context, null);
    }

    public CheckedTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 16843720);
    }

    public CheckedTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CheckedTextView, defStyle, 0);
        Drawable d = a.getDrawable(1);
        if (d != null) {
            setCheckMarkDrawable(d);
        }
        boolean checked = a.getBoolean(0, false);
        setChecked(checked);
        a.recycle();
    }

    @Override // android.widget.Checkable
    public void toggle() {
        setChecked(!this.mChecked);
    }

    @Override // android.widget.Checkable
    @ViewDebug.ExportedProperty
    public boolean isChecked() {
        return this.mChecked;
    }

    @Override // android.widget.Checkable
    public void setChecked(boolean checked) {
        if (this.mChecked != checked) {
            this.mChecked = checked;
            refreshDrawableState();
            notifyViewAccessibilityStateChangedIfNeeded(0);
        }
    }

    public void setCheckMarkDrawable(int resid) {
        if (resid != 0 && resid == this.mCheckMarkResource) {
            return;
        }
        this.mCheckMarkResource = resid;
        Drawable d = null;
        if (this.mCheckMarkResource != 0) {
            d = getResources().getDrawable(this.mCheckMarkResource);
        }
        setCheckMarkDrawable(d);
    }

    public void setCheckMarkDrawable(Drawable d) {
        if (this.mCheckMarkDrawable != null) {
            this.mCheckMarkDrawable.setCallback(null);
            unscheduleDrawable(this.mCheckMarkDrawable);
        }
        this.mNeedRequestlayout = d != this.mCheckMarkDrawable;
        if (d != null) {
            d.setCallback(this);
            d.setVisible(getVisibility() == 0, false);
            d.setState(CHECKED_STATE_SET);
            setMinHeight(d.getIntrinsicHeight());
            this.mCheckMarkWidth = d.getIntrinsicWidth();
            d.setState(getDrawableState());
        } else {
            this.mCheckMarkWidth = 0;
        }
        this.mCheckMarkDrawable = d;
        resolvePadding();
    }

    public Drawable getCheckMarkDrawable() {
        return this.mCheckMarkDrawable;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void internalSetPadding(int left, int top, int right, int bottom) {
        super.internalSetPadding(left, top, right, bottom);
        setBasePadding(isLayoutRtl());
    }

    @Override // android.widget.TextView, android.view.View
    public void onRtlPropertiesChanged(int layoutDirection) {
        super.onRtlPropertiesChanged(layoutDirection);
        updatePadding();
    }

    private void updatePadding() {
        resetPaddingToInitialValues();
        int newPadding = this.mCheckMarkDrawable != null ? this.mCheckMarkWidth + this.mBasePadding : this.mBasePadding;
        if (isLayoutRtl()) {
            this.mNeedRequestlayout |= this.mPaddingLeft != newPadding;
            this.mPaddingLeft = newPadding;
        } else {
            this.mNeedRequestlayout |= this.mPaddingRight != newPadding;
            this.mPaddingRight = newPadding;
        }
        if (this.mNeedRequestlayout) {
            requestLayout();
            this.mNeedRequestlayout = false;
        }
    }

    private void setBasePadding(boolean isLayoutRtl) {
        if (isLayoutRtl) {
            this.mBasePadding = this.mPaddingLeft;
        } else {
            this.mBasePadding = this.mPaddingRight;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.TextView, android.view.View
    public void onDraw(Canvas canvas) {
        int right;
        int left;
        super.onDraw(canvas);
        Drawable checkMarkDrawable = this.mCheckMarkDrawable;
        if (checkMarkDrawable != null) {
            int verticalGravity = getGravity() & 112;
            int height = checkMarkDrawable.getIntrinsicHeight();
            int y = 0;
            switch (verticalGravity) {
                case 16:
                    y = (getHeight() - height) / 2;
                    break;
                case 80:
                    y = getHeight() - height;
                    break;
            }
            boolean isLayoutRtl = isLayoutRtl();
            int width = getWidth();
            int top = y;
            int bottom = top + height;
            if (isLayoutRtl) {
                left = this.mBasePadding;
                right = left + this.mCheckMarkWidth;
            } else {
                right = width - this.mBasePadding;
                left = right - this.mCheckMarkWidth;
            }
            checkMarkDrawable.setBounds(this.mScrollX + left, top, this.mScrollX + right, bottom);
            checkMarkDrawable.draw(canvas);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.TextView, android.view.View
    public int[] onCreateDrawableState(int extraSpace) {
        int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.TextView, android.view.View
    public void drawableStateChanged() {
        super.drawableStateChanged();
        if (this.mCheckMarkDrawable != null) {
            int[] myDrawableState = getDrawableState();
            this.mCheckMarkDrawable.setState(myDrawableState);
            invalidate();
        }
    }

    @Override // android.widget.TextView, android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(CheckedTextView.class.getName());
        event.setChecked(this.mChecked);
    }

    @Override // android.widget.TextView, android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(CheckedTextView.class.getName());
        info.setCheckable(true);
        info.setChecked(this.mChecked);
    }
}