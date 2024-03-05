package android.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.AllCapsTransformationMethod;
import android.text.method.TransformationMethod2;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import com.android.internal.R;

/* loaded from: Switch.class */
public class Switch extends CompoundButton {
    private static final int TOUCH_MODE_IDLE = 0;
    private static final int TOUCH_MODE_DOWN = 1;
    private static final int TOUCH_MODE_DRAGGING = 2;
    private static final int SANS = 1;
    private static final int SERIF = 2;
    private static final int MONOSPACE = 3;
    private Drawable mThumbDrawable;
    private Drawable mTrackDrawable;
    private int mThumbTextPadding;
    private int mSwitchMinWidth;
    private int mSwitchPadding;
    private CharSequence mTextOn;
    private CharSequence mTextOff;
    private int mTouchMode;
    private int mTouchSlop;
    private float mTouchX;
    private float mTouchY;
    private VelocityTracker mVelocityTracker;
    private int mMinFlingVelocity;
    private float mThumbPosition;
    private int mSwitchWidth;
    private int mSwitchHeight;
    private int mThumbWidth;
    private int mSwitchLeft;
    private int mSwitchTop;
    private int mSwitchRight;
    private int mSwitchBottom;
    private TextPaint mTextPaint;
    private ColorStateList mTextColors;
    private Layout mOnLayout;
    private Layout mOffLayout;
    private TransformationMethod2 mSwitchTransformationMethod;
    private final Rect mTempRect;
    private static final int[] CHECKED_STATE_SET = {16842912};

    public Switch(Context context) {
        this(context, null);
    }

    public Switch(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.switchStyle);
    }

    public Switch(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mVelocityTracker = VelocityTracker.obtain();
        this.mTempRect = new Rect();
        this.mTextPaint = new TextPaint(1);
        Resources res = getResources();
        this.mTextPaint.density = res.getDisplayMetrics().density;
        this.mTextPaint.setCompatibilityScaling(res.getCompatibilityInfo().applicationScale);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Switch, defStyle, 0);
        this.mThumbDrawable = a.getDrawable(2);
        this.mTrackDrawable = a.getDrawable(4);
        this.mTextOn = a.getText(0);
        this.mTextOff = a.getText(1);
        this.mThumbTextPadding = a.getDimensionPixelSize(7, 0);
        this.mSwitchMinWidth = a.getDimensionPixelSize(5, 0);
        this.mSwitchPadding = a.getDimensionPixelSize(6, 0);
        int appearance = a.getResourceId(3, 0);
        if (appearance != 0) {
            setSwitchTextAppearance(context, appearance);
        }
        a.recycle();
        ViewConfiguration config = ViewConfiguration.get(context);
        this.mTouchSlop = config.getScaledTouchSlop();
        this.mMinFlingVelocity = config.getScaledMinimumFlingVelocity();
        refreshDrawableState();
        setChecked(isChecked());
    }

    public void setSwitchTextAppearance(Context context, int resid) {
        TypedArray appearance = context.obtainStyledAttributes(resid, R.styleable.TextAppearance);
        ColorStateList colors = appearance.getColorStateList(3);
        if (colors != null) {
            this.mTextColors = colors;
        } else {
            this.mTextColors = getTextColors();
        }
        int ts = appearance.getDimensionPixelSize(0, 0);
        if (ts != 0 && ts != this.mTextPaint.getTextSize()) {
            this.mTextPaint.setTextSize(ts);
            requestLayout();
        }
        int typefaceIndex = appearance.getInt(1, -1);
        int styleIndex = appearance.getInt(2, -1);
        setSwitchTypefaceByIndex(typefaceIndex, styleIndex);
        boolean allCaps = appearance.getBoolean(11, false);
        if (allCaps) {
            this.mSwitchTransformationMethod = new AllCapsTransformationMethod(getContext());
            this.mSwitchTransformationMethod.setLengthChangesAllowed(true);
        } else {
            this.mSwitchTransformationMethod = null;
        }
        appearance.recycle();
    }

    private void setSwitchTypefaceByIndex(int typefaceIndex, int styleIndex) {
        Typeface tf = null;
        switch (typefaceIndex) {
            case 1:
                tf = Typeface.SANS_SERIF;
                break;
            case 2:
                tf = Typeface.SERIF;
                break;
            case 3:
                tf = Typeface.MONOSPACE;
                break;
        }
        setSwitchTypeface(tf, styleIndex);
    }

    public void setSwitchTypeface(Typeface tf, int style) {
        Typeface tf2;
        if (style > 0) {
            if (tf == null) {
                tf2 = Typeface.defaultFromStyle(style);
            } else {
                tf2 = Typeface.create(tf, style);
            }
            setSwitchTypeface(tf2);
            int typefaceStyle = tf2 != null ? tf2.getStyle() : 0;
            int need = style & (typefaceStyle ^ (-1));
            this.mTextPaint.setFakeBoldText((need & 1) != 0);
            this.mTextPaint.setTextSkewX((need & 2) != 0 ? -0.25f : 0.0f);
            return;
        }
        this.mTextPaint.setFakeBoldText(false);
        this.mTextPaint.setTextSkewX(0.0f);
        setSwitchTypeface(tf);
    }

    public void setSwitchTypeface(Typeface tf) {
        if (this.mTextPaint.getTypeface() != tf) {
            this.mTextPaint.setTypeface(tf);
            requestLayout();
            invalidate();
        }
    }

    public void setSwitchPadding(int pixels) {
        this.mSwitchPadding = pixels;
        requestLayout();
    }

    public int getSwitchPadding() {
        return this.mSwitchPadding;
    }

    public void setSwitchMinWidth(int pixels) {
        this.mSwitchMinWidth = pixels;
        requestLayout();
    }

    public int getSwitchMinWidth() {
        return this.mSwitchMinWidth;
    }

    public void setThumbTextPadding(int pixels) {
        this.mThumbTextPadding = pixels;
        requestLayout();
    }

    public int getThumbTextPadding() {
        return this.mThumbTextPadding;
    }

    public void setTrackDrawable(Drawable track) {
        this.mTrackDrawable = track;
        requestLayout();
    }

    public void setTrackResource(int resId) {
        setTrackDrawable(getContext().getResources().getDrawable(resId));
    }

    public Drawable getTrackDrawable() {
        return this.mTrackDrawable;
    }

    public void setThumbDrawable(Drawable thumb) {
        this.mThumbDrawable = thumb;
        requestLayout();
    }

    public void setThumbResource(int resId) {
        setThumbDrawable(getContext().getResources().getDrawable(resId));
    }

    public Drawable getThumbDrawable() {
        return this.mThumbDrawable;
    }

    public CharSequence getTextOn() {
        return this.mTextOn;
    }

    public void setTextOn(CharSequence textOn) {
        this.mTextOn = textOn;
        requestLayout();
    }

    public CharSequence getTextOff() {
        return this.mTextOff;
    }

    public void setTextOff(CharSequence textOff) {
        this.mTextOff = textOff;
        requestLayout();
    }

    @Override // android.widget.TextView, android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.mOnLayout == null) {
            this.mOnLayout = makeLayout(this.mTextOn);
        }
        if (this.mOffLayout == null) {
            this.mOffLayout = makeLayout(this.mTextOff);
        }
        this.mTrackDrawable.getPadding(this.mTempRect);
        int maxTextWidth = Math.max(this.mOnLayout.getWidth(), this.mOffLayout.getWidth());
        int switchWidth = Math.max(this.mSwitchMinWidth, (maxTextWidth * 2) + (this.mThumbTextPadding * 4) + this.mTempRect.left + this.mTempRect.right);
        int switchHeight = this.mTrackDrawable.getIntrinsicHeight();
        this.mThumbWidth = maxTextWidth + (this.mThumbTextPadding * 2);
        this.mSwitchWidth = switchWidth;
        this.mSwitchHeight = switchHeight;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredHeight = getMeasuredHeight();
        if (measuredHeight < switchHeight) {
            setMeasuredDimension(getMeasuredWidthAndState(), switchHeight);
        }
    }

    @Override // android.widget.TextView, android.view.View
    public void onPopulateAccessibilityEvent(AccessibilityEvent event) {
        super.onPopulateAccessibilityEvent(event);
        Layout layout = isChecked() ? this.mOnLayout : this.mOffLayout;
        if (layout != null && !TextUtils.isEmpty(layout.getText())) {
            event.getText().add(layout.getText());
        }
    }

    private Layout makeLayout(CharSequence text) {
        CharSequence transformed = this.mSwitchTransformationMethod != null ? this.mSwitchTransformationMethod.getTransformation(text, this) : text;
        return new StaticLayout(transformed, this.mTextPaint, (int) Math.ceil(Layout.getDesiredWidth(transformed, this.mTextPaint)), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
    }

    private boolean hitThumb(float x, float y) {
        this.mThumbDrawable.getPadding(this.mTempRect);
        int thumbTop = this.mSwitchTop - this.mTouchSlop;
        int thumbLeft = (this.mSwitchLeft + ((int) (this.mThumbPosition + 0.5f))) - this.mTouchSlop;
        int thumbRight = thumbLeft + this.mThumbWidth + this.mTempRect.left + this.mTempRect.right + this.mTouchSlop;
        int thumbBottom = this.mSwitchBottom + this.mTouchSlop;
        return x > ((float) thumbLeft) && x < ((float) thumbRight) && y > ((float) thumbTop) && y < ((float) thumbBottom);
    }

    @Override // android.widget.TextView, android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        this.mVelocityTracker.addMovement(ev);
        int action = ev.getActionMasked();
        switch (action) {
            case 0:
                float x = ev.getX();
                float y = ev.getY();
                if (isEnabled() && hitThumb(x, y)) {
                    this.mTouchMode = 1;
                    this.mTouchX = x;
                    this.mTouchY = y;
                    break;
                }
                break;
            case 1:
            case 3:
                if (this.mTouchMode == 2) {
                    stopDrag(ev);
                    return true;
                }
                this.mTouchMode = 0;
                this.mVelocityTracker.clear();
                break;
            case 2:
                switch (this.mTouchMode) {
                    case 2:
                        float x2 = ev.getX();
                        float dx = x2 - this.mTouchX;
                        float newPos = Math.max(0.0f, Math.min(this.mThumbPosition + dx, getThumbScrollRange()));
                        if (newPos != this.mThumbPosition) {
                            this.mThumbPosition = newPos;
                            this.mTouchX = x2;
                            invalidate();
                            return true;
                        }
                        return true;
                    case 1:
                        float x3 = ev.getX();
                        float y2 = ev.getY();
                        if (Math.abs(x3 - this.mTouchX) > this.mTouchSlop || Math.abs(y2 - this.mTouchY) > this.mTouchSlop) {
                            this.mTouchMode = 2;
                            getParent().requestDisallowInterceptTouchEvent(true);
                            this.mTouchX = x3;
                            this.mTouchY = y2;
                            return true;
                        }
                        break;
                }
        }
        return super.onTouchEvent(ev);
    }

    private void cancelSuperTouch(MotionEvent ev) {
        MotionEvent cancel = MotionEvent.obtain(ev);
        cancel.setAction(3);
        super.onTouchEvent(cancel);
        cancel.recycle();
    }

    private void stopDrag(MotionEvent ev) {
        boolean newState;
        this.mTouchMode = 0;
        boolean commitChange = ev.getAction() == 1 && isEnabled();
        cancelSuperTouch(ev);
        if (commitChange) {
            this.mVelocityTracker.computeCurrentVelocity(1000);
            float xvel = this.mVelocityTracker.getXVelocity();
            if (Math.abs(xvel) > this.mMinFlingVelocity) {
                newState = isLayoutRtl() ? xvel < 0.0f : xvel > 0.0f;
            } else {
                newState = getTargetCheckedState();
            }
            animateThumbToCheckedState(newState);
            return;
        }
        animateThumbToCheckedState(isChecked());
    }

    private void animateThumbToCheckedState(boolean newCheckedState) {
        setChecked(newCheckedState);
    }

    private boolean getTargetCheckedState() {
        return isLayoutRtl() ? this.mThumbPosition <= ((float) (getThumbScrollRange() / 2)) : this.mThumbPosition >= ((float) (getThumbScrollRange() / 2));
    }

    private void setThumbPosition(boolean checked) {
        if (isLayoutRtl()) {
            this.mThumbPosition = checked ? 0.0f : getThumbScrollRange();
        } else {
            this.mThumbPosition = checked ? getThumbScrollRange() : 0.0f;
        }
    }

    @Override // android.widget.CompoundButton, android.widget.Checkable
    public void setChecked(boolean checked) {
        super.setChecked(checked);
        setThumbPosition(isChecked());
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.TextView, android.view.View
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int switchRight;
        int switchLeft;
        int switchBottom;
        int switchTop;
        super.onLayout(changed, left, top, right, bottom);
        setThumbPosition(isChecked());
        if (isLayoutRtl()) {
            switchLeft = getPaddingLeft();
            switchRight = switchLeft + this.mSwitchWidth;
        } else {
            switchRight = getWidth() - getPaddingRight();
            switchLeft = switchRight - this.mSwitchWidth;
        }
        switch (getGravity() & 112) {
            case 16:
                switchTop = (((getPaddingTop() + getHeight()) - getPaddingBottom()) / 2) - (this.mSwitchHeight / 2);
                switchBottom = switchTop + this.mSwitchHeight;
                break;
            case 48:
            default:
                switchTop = getPaddingTop();
                switchBottom = switchTop + this.mSwitchHeight;
                break;
            case 80:
                switchBottom = getHeight() - getPaddingBottom();
                switchTop = switchBottom - this.mSwitchHeight;
                break;
        }
        this.mSwitchLeft = switchLeft;
        this.mSwitchTop = switchTop;
        this.mSwitchBottom = switchBottom;
        this.mSwitchRight = switchRight;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.CompoundButton, android.widget.TextView, android.view.View
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int switchLeft = this.mSwitchLeft;
        int switchTop = this.mSwitchTop;
        int switchRight = this.mSwitchRight;
        int switchBottom = this.mSwitchBottom;
        this.mTrackDrawable.setBounds(switchLeft, switchTop, switchRight, switchBottom);
        this.mTrackDrawable.draw(canvas);
        canvas.save();
        this.mTrackDrawable.getPadding(this.mTempRect);
        int switchInnerLeft = switchLeft + this.mTempRect.left;
        int switchInnerTop = switchTop + this.mTempRect.top;
        int switchInnerRight = switchRight - this.mTempRect.right;
        int switchInnerBottom = switchBottom - this.mTempRect.bottom;
        canvas.clipRect(switchInnerLeft, switchTop, switchInnerRight, switchBottom);
        this.mThumbDrawable.getPadding(this.mTempRect);
        int thumbPos = (int) (this.mThumbPosition + 0.5f);
        int thumbLeft = (switchInnerLeft - this.mTempRect.left) + thumbPos;
        int thumbRight = switchInnerLeft + thumbPos + this.mThumbWidth + this.mTempRect.right;
        this.mThumbDrawable.setBounds(thumbLeft, switchTop, thumbRight, switchBottom);
        this.mThumbDrawable.draw(canvas);
        if (this.mTextColors != null) {
            this.mTextPaint.setColor(this.mTextColors.getColorForState(getDrawableState(), this.mTextColors.getDefaultColor()));
        }
        this.mTextPaint.drawableState = getDrawableState();
        Layout switchText = getTargetCheckedState() ? this.mOnLayout : this.mOffLayout;
        if (switchText != null) {
            canvas.translate(((thumbLeft + thumbRight) / 2) - (switchText.getWidth() / 2), ((switchInnerTop + switchInnerBottom) / 2) - (switchText.getHeight() / 2));
            switchText.draw(canvas);
        }
        canvas.restore();
    }

    @Override // android.widget.CompoundButton, android.widget.TextView
    public int getCompoundPaddingLeft() {
        if (!isLayoutRtl()) {
            return super.getCompoundPaddingLeft();
        }
        int padding = super.getCompoundPaddingLeft() + this.mSwitchWidth;
        if (!TextUtils.isEmpty(getText())) {
            padding += this.mSwitchPadding;
        }
        return padding;
    }

    @Override // android.widget.CompoundButton, android.widget.TextView
    public int getCompoundPaddingRight() {
        if (isLayoutRtl()) {
            return super.getCompoundPaddingRight();
        }
        int padding = super.getCompoundPaddingRight() + this.mSwitchWidth;
        if (!TextUtils.isEmpty(getText())) {
            padding += this.mSwitchPadding;
        }
        return padding;
    }

    private int getThumbScrollRange() {
        if (this.mTrackDrawable == null) {
            return 0;
        }
        this.mTrackDrawable.getPadding(this.mTempRect);
        return ((this.mSwitchWidth - this.mThumbWidth) - this.mTempRect.left) - this.mTempRect.right;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.CompoundButton, android.widget.TextView, android.view.View
    public int[] onCreateDrawableState(int extraSpace) {
        int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.CompoundButton, android.widget.TextView, android.view.View
    public void drawableStateChanged() {
        super.drawableStateChanged();
        int[] myDrawableState = getDrawableState();
        if (this.mThumbDrawable != null) {
            this.mThumbDrawable.setState(myDrawableState);
        }
        if (this.mTrackDrawable != null) {
            this.mTrackDrawable.setState(myDrawableState);
        }
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.CompoundButton, android.widget.TextView, android.view.View
    public boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || who == this.mThumbDrawable || who == this.mTrackDrawable;
    }

    @Override // android.widget.CompoundButton, android.widget.TextView, android.view.View
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        this.mThumbDrawable.jumpToCurrentState();
        this.mTrackDrawable.jumpToCurrentState();
    }

    @Override // android.widget.CompoundButton, android.widget.Button, android.widget.TextView, android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(Switch.class.getName());
    }

    @Override // android.widget.CompoundButton, android.widget.Button, android.widget.TextView, android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(Switch.class.getName());
        CharSequence switchText = isChecked() ? this.mTextOn : this.mTextOff;
        if (!TextUtils.isEmpty(switchText)) {
            CharSequence oldText = info.getText();
            if (TextUtils.isEmpty(oldText)) {
                info.setText(switchText);
                return;
            }
            StringBuilder newText = new StringBuilder();
            newText.append(oldText).append(' ').append(switchText);
            info.setText(newText);
        }
    }
}