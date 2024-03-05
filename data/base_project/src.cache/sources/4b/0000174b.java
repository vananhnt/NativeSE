package android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewDebug;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import com.android.internal.R;

/* loaded from: CompoundButton.class */
public abstract class CompoundButton extends Button implements Checkable {
    private boolean mChecked;
    private int mButtonResource;
    private boolean mBroadcasting;
    private Drawable mButtonDrawable;
    private OnCheckedChangeListener mOnCheckedChangeListener;
    private OnCheckedChangeListener mOnCheckedChangeWidgetListener;
    private static final int[] CHECKED_STATE_SET = {16842912};

    /* loaded from: CompoundButton$OnCheckedChangeListener.class */
    public interface OnCheckedChangeListener {
        void onCheckedChanged(CompoundButton compoundButton, boolean z);
    }

    public CompoundButton(Context context) {
        this(context, null);
    }

    public CompoundButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CompoundButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CompoundButton, defStyle, 0);
        Drawable d = a.getDrawable(1);
        if (d != null) {
            setButtonDrawable(d);
        }
        boolean checked = a.getBoolean(0, false);
        setChecked(checked);
        a.recycle();
    }

    public void toggle() {
        setChecked(!this.mChecked);
    }

    @Override // android.view.View
    public boolean performClick() {
        toggle();
        return super.performClick();
    }

    @Override // android.widget.Checkable
    @ViewDebug.ExportedProperty
    public boolean isChecked() {
        return this.mChecked;
    }

    public void setChecked(boolean checked) {
        if (this.mChecked != checked) {
            this.mChecked = checked;
            refreshDrawableState();
            notifyViewAccessibilityStateChangedIfNeeded(0);
            if (this.mBroadcasting) {
                return;
            }
            this.mBroadcasting = true;
            if (this.mOnCheckedChangeListener != null) {
                this.mOnCheckedChangeListener.onCheckedChanged(this, this.mChecked);
            }
            if (this.mOnCheckedChangeWidgetListener != null) {
                this.mOnCheckedChangeWidgetListener.onCheckedChanged(this, this.mChecked);
            }
            this.mBroadcasting = false;
        }
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        this.mOnCheckedChangeListener = listener;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setOnCheckedChangeWidgetListener(OnCheckedChangeListener listener) {
        this.mOnCheckedChangeWidgetListener = listener;
    }

    public void setButtonDrawable(int resid) {
        if (resid != 0 && resid == this.mButtonResource) {
            return;
        }
        this.mButtonResource = resid;
        Drawable d = null;
        if (this.mButtonResource != 0) {
            d = getResources().getDrawable(this.mButtonResource);
        }
        setButtonDrawable(d);
    }

    public void setButtonDrawable(Drawable d) {
        if (d != null) {
            if (this.mButtonDrawable != null) {
                this.mButtonDrawable.setCallback(null);
                unscheduleDrawable(this.mButtonDrawable);
            }
            d.setCallback(this);
            d.setVisible(getVisibility() == 0, false);
            this.mButtonDrawable = d;
            setMinHeight(this.mButtonDrawable.getIntrinsicHeight());
        }
        refreshDrawableState();
    }

    @Override // android.widget.Button, android.widget.TextView, android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(CompoundButton.class.getName());
        event.setChecked(this.mChecked);
    }

    @Override // android.widget.Button, android.widget.TextView, android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(CompoundButton.class.getName());
        info.setCheckable(true);
        info.setChecked(this.mChecked);
    }

    @Override // android.widget.TextView
    public int getCompoundPaddingLeft() {
        Drawable buttonDrawable;
        int padding = super.getCompoundPaddingLeft();
        if (!isLayoutRtl() && (buttonDrawable = this.mButtonDrawable) != null) {
            padding += buttonDrawable.getIntrinsicWidth();
        }
        return padding;
    }

    @Override // android.widget.TextView
    public int getCompoundPaddingRight() {
        Drawable buttonDrawable;
        int padding = super.getCompoundPaddingRight();
        if (isLayoutRtl() && (buttonDrawable = this.mButtonDrawable) != null) {
            padding += buttonDrawable.getIntrinsicWidth();
        }
        return padding;
    }

    @Override // android.widget.TextView
    public int getHorizontalOffsetForDrawables() {
        Drawable buttonDrawable = this.mButtonDrawable;
        if (buttonDrawable != null) {
            return buttonDrawable.getIntrinsicWidth();
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.TextView, android.view.View
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Drawable buttonDrawable = this.mButtonDrawable;
        if (buttonDrawable != null) {
            int verticalGravity = getGravity() & 112;
            int drawableHeight = buttonDrawable.getIntrinsicHeight();
            int drawableWidth = buttonDrawable.getIntrinsicWidth();
            int top = 0;
            switch (verticalGravity) {
                case 16:
                    top = (getHeight() - drawableHeight) / 2;
                    break;
                case 80:
                    top = getHeight() - drawableHeight;
                    break;
            }
            int bottom = top + drawableHeight;
            int left = isLayoutRtl() ? getWidth() - drawableWidth : 0;
            int right = isLayoutRtl() ? getWidth() : drawableWidth;
            buttonDrawable.setBounds(left, top, right, bottom);
            buttonDrawable.draw(canvas);
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
        if (this.mButtonDrawable != null) {
            int[] myDrawableState = getDrawableState();
            this.mButtonDrawable.setState(myDrawableState);
            invalidate();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.TextView, android.view.View
    public boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || who == this.mButtonDrawable;
    }

    @Override // android.widget.TextView, android.view.View
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        if (this.mButtonDrawable != null) {
            this.mButtonDrawable.jumpToCurrentState();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: CompoundButton$SavedState.class */
    public static class SavedState extends View.BaseSavedState {
        boolean checked;
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: android.widget.CompoundButton.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.checked = ((Boolean) in.readValue(null)).booleanValue();
        }

        @Override // android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeValue(Boolean.valueOf(this.checked));
        }

        public String toString() {
            return "CompoundButton.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " checked=" + this.checked + "}";
        }
    }

    @Override // android.widget.TextView, android.view.View
    public Parcelable onSaveInstanceState() {
        setFreezesText(true);
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.checked = isChecked();
        return ss;
    }

    @Override // android.widget.TextView, android.view.View
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setChecked(ss.checked);
        requestLayout();
    }
}