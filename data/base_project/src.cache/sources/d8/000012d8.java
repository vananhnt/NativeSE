package android.support.v7.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.appcompat.R;
import android.support.v7.internal.widget.TintTypedArray;
import android.support.v7.internal.widget.ViewUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import gov.nist.core.Separators;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* loaded from: LinearLayoutCompat.class */
public class LinearLayoutCompat extends ViewGroup {
    public static final int HORIZONTAL = 0;
    private static final int INDEX_BOTTOM = 2;
    private static final int INDEX_CENTER_VERTICAL = 0;
    private static final int INDEX_FILL = 3;
    private static final int INDEX_TOP = 1;
    public static final int SHOW_DIVIDER_BEGINNING = 1;
    public static final int SHOW_DIVIDER_END = 4;
    public static final int SHOW_DIVIDER_MIDDLE = 2;
    public static final int SHOW_DIVIDER_NONE = 0;
    public static final int VERTICAL = 1;
    private static final int VERTICAL_GRAVITY_COUNT = 4;
    private boolean mBaselineAligned;
    private int mBaselineAlignedChildIndex;
    private int mBaselineChildTop;
    private Drawable mDivider;
    private int mDividerHeight;
    private int mDividerPadding;
    private int mDividerWidth;
    private int mGravity;
    private int[] mMaxAscent;
    private int[] mMaxDescent;
    private int mOrientation;
    private int mShowDividers;
    private int mTotalLength;
    private boolean mUseLargestChild;
    private float mWeightSum;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(flag = true, value = {0, 1, 2, 4})
    /* loaded from: LinearLayoutCompat$DividerMode.class */
    public @interface DividerMode {
    }

    /* loaded from: LinearLayoutCompat$LayoutParams.class */
    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        public int gravity;
        public float weight;

        public LayoutParams(int i, int i2) {
            super(i, i2);
            this.gravity = -1;
            this.weight = 0.0f;
        }

        public LayoutParams(int i, int i2, float f) {
            super(i, i2);
            this.gravity = -1;
            this.weight = f;
        }

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            this.gravity = -1;
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.LinearLayoutCompat_Layout);
            this.weight = obtainStyledAttributes.getFloat(R.styleable.LinearLayoutCompat_Layout_android_layout_weight, 0.0f);
            this.gravity = obtainStyledAttributes.getInt(R.styleable.LinearLayoutCompat_Layout_android_layout_gravity, -1);
            obtainStyledAttributes.recycle();
        }

        public LayoutParams(LayoutParams layoutParams) {
            super((ViewGroup.MarginLayoutParams) layoutParams);
            this.gravity = -1;
            this.weight = layoutParams.weight;
            this.gravity = layoutParams.gravity;
        }

        public LayoutParams(ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
            this.gravity = -1;
        }

        public LayoutParams(ViewGroup.MarginLayoutParams marginLayoutParams) {
            super(marginLayoutParams);
            this.gravity = -1;
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({0, 1})
    /* loaded from: LinearLayoutCompat$OrientationMode.class */
    public @interface OrientationMode {
    }

    public LinearLayoutCompat(Context context) {
        this(context, null);
    }

    public LinearLayoutCompat(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public LinearLayoutCompat(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mBaselineAligned = true;
        this.mBaselineAlignedChildIndex = -1;
        this.mBaselineChildTop = 0;
        this.mGravity = 8388659;
        TintTypedArray obtainStyledAttributes = TintTypedArray.obtainStyledAttributes(context, attributeSet, R.styleable.LinearLayoutCompat, i, 0);
        int i2 = obtainStyledAttributes.getInt(R.styleable.LinearLayoutCompat_android_orientation, -1);
        if (i2 >= 0) {
            setOrientation(i2);
        }
        int i3 = obtainStyledAttributes.getInt(R.styleable.LinearLayoutCompat_android_gravity, -1);
        if (i3 >= 0) {
            setGravity(i3);
        }
        boolean z = obtainStyledAttributes.getBoolean(R.styleable.LinearLayoutCompat_android_baselineAligned, true);
        if (!z) {
            setBaselineAligned(z);
        }
        this.mWeightSum = obtainStyledAttributes.getFloat(R.styleable.LinearLayoutCompat_android_weightSum, -1.0f);
        this.mBaselineAlignedChildIndex = obtainStyledAttributes.getInt(R.styleable.LinearLayoutCompat_android_baselineAlignedChildIndex, -1);
        this.mUseLargestChild = obtainStyledAttributes.getBoolean(R.styleable.LinearLayoutCompat_measureWithLargestChild, false);
        setDividerDrawable(obtainStyledAttributes.getDrawable(R.styleable.LinearLayoutCompat_divider));
        this.mShowDividers = obtainStyledAttributes.getInt(R.styleable.LinearLayoutCompat_showDividers, 0);
        this.mDividerPadding = obtainStyledAttributes.getDimensionPixelSize(R.styleable.LinearLayoutCompat_dividerPadding, 0);
        obtainStyledAttributes.recycle();
    }

    private void forceUniformHeight(int i, int i2) {
        int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824);
        for (int i3 = 0; i3 < i; i3++) {
            View virtualChildAt = getVirtualChildAt(i3);
            if (virtualChildAt.getVisibility() != 8) {
                LayoutParams layoutParams = (LayoutParams) virtualChildAt.getLayoutParams();
                if (layoutParams.height == -1) {
                    int i4 = layoutParams.width;
                    layoutParams.width = virtualChildAt.getMeasuredWidth();
                    measureChildWithMargins(virtualChildAt, i2, 0, makeMeasureSpec, 0);
                    layoutParams.width = i4;
                }
            }
        }
    }

    private void forceUniformWidth(int i, int i2) {
        int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 1073741824);
        for (int i3 = 0; i3 < i; i3++) {
            View virtualChildAt = getVirtualChildAt(i3);
            if (virtualChildAt.getVisibility() != 8) {
                LayoutParams layoutParams = (LayoutParams) virtualChildAt.getLayoutParams();
                if (layoutParams.width == -1) {
                    int i4 = layoutParams.height;
                    layoutParams.height = virtualChildAt.getMeasuredHeight();
                    measureChildWithMargins(virtualChildAt, makeMeasureSpec, 0, i2, 0);
                    layoutParams.height = i4;
                }
            }
        }
    }

    private void setChildFrame(View view, int i, int i2, int i3, int i4) {
        view.layout(i, i2, i + i3, i2 + i4);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public boolean checkLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return layoutParams instanceof LayoutParams;
    }

    void drawDividersHorizontal(Canvas canvas) {
        int left;
        int virtualChildCount = getVirtualChildCount();
        boolean isLayoutRtl = ViewUtils.isLayoutRtl(this);
        for (int i = 0; i < virtualChildCount; i++) {
            View virtualChildAt = getVirtualChildAt(i);
            if (virtualChildAt != null && virtualChildAt.getVisibility() != 8 && hasDividerBeforeChildAt(i)) {
                LayoutParams layoutParams = (LayoutParams) virtualChildAt.getLayoutParams();
                drawVerticalDivider(canvas, isLayoutRtl ? virtualChildAt.getRight() + layoutParams.rightMargin : (virtualChildAt.getLeft() - layoutParams.leftMargin) - this.mDividerWidth);
            }
        }
        if (hasDividerBeforeChildAt(virtualChildCount)) {
            View virtualChildAt2 = getVirtualChildAt(virtualChildCount - 1);
            if (virtualChildAt2 == null) {
                left = isLayoutRtl ? getPaddingLeft() : (getWidth() - getPaddingRight()) - this.mDividerWidth;
            } else {
                LayoutParams layoutParams2 = (LayoutParams) virtualChildAt2.getLayoutParams();
                left = isLayoutRtl ? (virtualChildAt2.getLeft() - layoutParams2.leftMargin) - this.mDividerWidth : virtualChildAt2.getRight() + layoutParams2.rightMargin;
            }
            drawVerticalDivider(canvas, left);
        }
    }

    void drawDividersVertical(Canvas canvas) {
        int virtualChildCount = getVirtualChildCount();
        for (int i = 0; i < virtualChildCount; i++) {
            View virtualChildAt = getVirtualChildAt(i);
            if (virtualChildAt != null && virtualChildAt.getVisibility() != 8 && hasDividerBeforeChildAt(i)) {
                drawHorizontalDivider(canvas, (virtualChildAt.getTop() - ((LayoutParams) virtualChildAt.getLayoutParams()).topMargin) - this.mDividerHeight);
            }
        }
        if (hasDividerBeforeChildAt(virtualChildCount)) {
            View virtualChildAt2 = getVirtualChildAt(virtualChildCount - 1);
            drawHorizontalDivider(canvas, virtualChildAt2 == null ? (getHeight() - getPaddingBottom()) - this.mDividerHeight : virtualChildAt2.getBottom() + ((LayoutParams) virtualChildAt2.getLayoutParams()).bottomMargin);
        }
    }

    void drawHorizontalDivider(Canvas canvas, int i) {
        this.mDivider.setBounds(getPaddingLeft() + this.mDividerPadding, i, (getWidth() - getPaddingRight()) - this.mDividerPadding, this.mDividerHeight + i);
        this.mDivider.draw(canvas);
    }

    void drawVerticalDivider(Canvas canvas, int i) {
        this.mDivider.setBounds(i, getPaddingTop() + this.mDividerPadding, this.mDividerWidth + i, (getHeight() - getPaddingBottom()) - this.mDividerPadding);
        this.mDivider.draw(canvas);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public LayoutParams generateDefaultLayoutParams() {
        int i = this.mOrientation;
        if (i == 0) {
            return new LayoutParams(-2, -2);
        }
        if (i == 1) {
            return new LayoutParams(-1, -2);
        }
        return null;
    }

    @Override // android.view.ViewGroup
    public LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LayoutParams(getContext(), attributeSet);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return new LayoutParams(layoutParams);
    }

    @Override // android.view.View
    public int getBaseline() {
        int i;
        if (this.mBaselineAlignedChildIndex < 0) {
            return super.getBaseline();
        }
        int childCount = getChildCount();
        int i2 = this.mBaselineAlignedChildIndex;
        if (childCount > i2) {
            View childAt = getChildAt(i2);
            int baseline = childAt.getBaseline();
            if (baseline == -1) {
                if (this.mBaselineAlignedChildIndex == 0) {
                    return -1;
                }
                throw new RuntimeException("mBaselineAlignedChildIndex of LinearLayout points to a View that doesn't know how to get its baseline.");
            }
            int i3 = this.mBaselineChildTop;
            if (this.mOrientation == 1 && (i = this.mGravity & 112) != 48) {
                if (i == 16) {
                    i3 += ((((getBottom() - getTop()) - getPaddingTop()) - getPaddingBottom()) - this.mTotalLength) / 2;
                } else if (i == 80) {
                    i3 = ((getBottom() - getTop()) - getPaddingBottom()) - this.mTotalLength;
                }
            }
            return ((LayoutParams) childAt.getLayoutParams()).topMargin + i3 + baseline;
        }
        throw new RuntimeException("mBaselineAlignedChildIndex of LinearLayout set to an index that is out of bounds.");
    }

    public int getBaselineAlignedChildIndex() {
        return this.mBaselineAlignedChildIndex;
    }

    int getChildrenSkipCount(View view, int i) {
        return 0;
    }

    public Drawable getDividerDrawable() {
        return this.mDivider;
    }

    public int getDividerPadding() {
        return this.mDividerPadding;
    }

    public int getDividerWidth() {
        return this.mDividerWidth;
    }

    int getLocationOffset(View view) {
        return 0;
    }

    int getNextLocationOffset(View view) {
        return 0;
    }

    public int getOrientation() {
        return this.mOrientation;
    }

    public int getShowDividers() {
        return this.mShowDividers;
    }

    View getVirtualChildAt(int i) {
        return getChildAt(i);
    }

    int getVirtualChildCount() {
        return getChildCount();
    }

    public float getWeightSum() {
        return this.mWeightSum;
    }

    protected boolean hasDividerBeforeChildAt(int i) {
        boolean z;
        boolean z2 = false;
        if (i == 0) {
            if ((this.mShowDividers & 1) != 0) {
                z2 = true;
            }
            return z2;
        } else if (i == getChildCount()) {
            boolean z3 = false;
            if ((this.mShowDividers & 4) != 0) {
                z3 = true;
            }
            return z3;
        } else if ((this.mShowDividers & 2) != 0) {
            while (true) {
                i--;
                z = false;
                if (i < 0) {
                    break;
                } else if (getChildAt(i).getVisibility() != 8) {
                    z = true;
                    break;
                }
            }
            return z;
        } else {
            return false;
        }
    }

    public boolean isBaselineAligned() {
        return this.mBaselineAligned;
    }

    public boolean isMeasureWithLargestChildEnabled() {
        return this.mUseLargestChild;
    }

    void layoutHorizontal(int i, int i2, int i3, int i4) {
        int i5;
        int i6;
        int i7;
        boolean isLayoutRtl = ViewUtils.isLayoutRtl(this);
        int paddingTop = getPaddingTop();
        int i8 = i4 - i2;
        int paddingBottom = getPaddingBottom();
        int paddingBottom2 = getPaddingBottom();
        int virtualChildCount = getVirtualChildCount();
        int i9 = this.mGravity;
        boolean z = this.mBaselineAligned;
        int[] iArr = this.mMaxAscent;
        int[] iArr2 = this.mMaxDescent;
        int absoluteGravity = GravityCompat.getAbsoluteGravity(i9 & 8388615, ViewCompat.getLayoutDirection(this));
        int paddingLeft = absoluteGravity != 1 ? absoluteGravity != 5 ? getPaddingLeft() : ((getPaddingLeft() + i3) - i) - this.mTotalLength : getPaddingLeft() + (((i3 - i) - this.mTotalLength) / 2);
        if (isLayoutRtl) {
            i5 = virtualChildCount - 1;
            i6 = -1;
        } else {
            i5 = 0;
            i6 = 1;
        }
        int i10 = 0;
        int i11 = paddingLeft;
        while (i10 < virtualChildCount) {
            int i12 = i5 + (i6 * i10);
            View virtualChildAt = getVirtualChildAt(i12);
            if (virtualChildAt == null) {
                i11 += measureNullChild(i12);
            } else if (virtualChildAt.getVisibility() != 8) {
                int measuredWidth = virtualChildAt.getMeasuredWidth();
                int measuredHeight = virtualChildAt.getMeasuredHeight();
                LayoutParams layoutParams = (LayoutParams) virtualChildAt.getLayoutParams();
                int baseline = (!z || layoutParams.height == -1) ? -1 : virtualChildAt.getBaseline();
                int i13 = layoutParams.gravity;
                if (i13 < 0) {
                    i13 = i9 & 112;
                }
                int i14 = i13 & 112;
                if (i14 == 16) {
                    i7 = ((paddingTop + ((((i8 - paddingTop) - paddingBottom2) - measuredHeight) / 2)) + layoutParams.topMargin) - layoutParams.bottomMargin;
                } else if (i14 == 48) {
                    int i15 = layoutParams.topMargin + paddingTop;
                    i7 = baseline != -1 ? i15 + (iArr[1] - baseline) : i15;
                } else if (i14 != 80) {
                    i7 = paddingTop;
                } else {
                    int i16 = ((i8 - paddingBottom) - measuredHeight) - layoutParams.bottomMargin;
                    i7 = baseline != -1 ? i16 - (iArr2[2] - (virtualChildAt.getMeasuredHeight() - baseline)) : i16;
                }
                if (hasDividerBeforeChildAt(i12)) {
                    i11 += this.mDividerWidth;
                }
                int i17 = i11 + layoutParams.leftMargin;
                setChildFrame(virtualChildAt, i17 + getLocationOffset(virtualChildAt), i7, measuredWidth, measuredHeight);
                int i18 = layoutParams.rightMargin;
                int nextLocationOffset = getNextLocationOffset(virtualChildAt);
                i10 += getChildrenSkipCount(virtualChildAt, i12);
                i11 = i17 + measuredWidth + i18 + nextLocationOffset;
            }
            i10++;
        }
    }

    void layoutVertical(int i, int i2, int i3, int i4) {
        int paddingLeft = getPaddingLeft();
        int i5 = i3 - i;
        int paddingRight = getPaddingRight();
        int paddingRight2 = getPaddingRight();
        int virtualChildCount = getVirtualChildCount();
        int i6 = this.mGravity;
        int i7 = i6 & 112;
        int paddingTop = i7 != 16 ? i7 != 80 ? getPaddingTop() : ((getPaddingTop() + i4) - i2) - this.mTotalLength : getPaddingTop() + (((i4 - i2) - this.mTotalLength) / 2);
        int i8 = 0;
        int i9 = paddingLeft;
        while (true) {
            int i10 = i9;
            if (i8 >= virtualChildCount) {
                return;
            }
            View virtualChildAt = getVirtualChildAt(i8);
            if (virtualChildAt == null) {
                paddingTop += measureNullChild(i8);
            } else if (virtualChildAt.getVisibility() != 8) {
                int measuredWidth = virtualChildAt.getMeasuredWidth();
                int measuredHeight = virtualChildAt.getMeasuredHeight();
                LayoutParams layoutParams = (LayoutParams) virtualChildAt.getLayoutParams();
                int i11 = layoutParams.gravity;
                if (i11 < 0) {
                    i11 = i6 & 8388615;
                }
                int absoluteGravity = GravityCompat.getAbsoluteGravity(i11, ViewCompat.getLayoutDirection(this)) & 7;
                int i12 = absoluteGravity != 1 ? absoluteGravity != 5 ? layoutParams.leftMargin + i10 : ((i5 - paddingRight) - measuredWidth) - layoutParams.rightMargin : ((((((i5 - paddingLeft) - paddingRight2) - measuredWidth) / 2) + i10) + layoutParams.leftMargin) - layoutParams.rightMargin;
                if (hasDividerBeforeChildAt(i8)) {
                    paddingTop += this.mDividerHeight;
                }
                int i13 = paddingTop + layoutParams.topMargin;
                setChildFrame(virtualChildAt, i12, i13 + getLocationOffset(virtualChildAt), measuredWidth, measuredHeight);
                int i14 = layoutParams.bottomMargin;
                int nextLocationOffset = getNextLocationOffset(virtualChildAt);
                i8 += getChildrenSkipCount(virtualChildAt, i8);
                paddingTop = i13 + measuredHeight + i14 + nextLocationOffset;
            }
            i8++;
            i9 = i10;
        }
    }

    void measureChildBeforeLayout(View view, int i, int i2, int i3, int i4, int i5) {
        measureChildWithMargins(view, i2, i3, i4, i5);
    }

    void measureHorizontal(int i, int i2) {
        int i3;
        int i4;
        int max;
        int i5;
        int i6;
        int i7;
        int baseline;
        int i8;
        int max2;
        int i9;
        int baseline2;
        this.mTotalLength = 0;
        int virtualChildCount = getVirtualChildCount();
        int mode = View.MeasureSpec.getMode(i);
        int mode2 = View.MeasureSpec.getMode(i2);
        if (this.mMaxAscent == null || this.mMaxDescent == null) {
            this.mMaxAscent = new int[4];
            this.mMaxDescent = new int[4];
        }
        int[] iArr = this.mMaxAscent;
        int[] iArr2 = this.mMaxDescent;
        iArr[3] = -1;
        iArr[2] = -1;
        iArr[1] = -1;
        iArr[0] = -1;
        iArr2[3] = -1;
        iArr2[2] = -1;
        iArr2[1] = -1;
        iArr2[0] = -1;
        boolean z = this.mBaselineAligned;
        boolean z2 = false;
        boolean z3 = this.mUseLargestChild;
        boolean z4 = mode == 1073741824;
        int i10 = 0;
        int i11 = Integer.MIN_VALUE;
        boolean z5 = false;
        boolean z6 = true;
        int i12 = 0;
        float f = 0.0f;
        int i13 = 0;
        int i14 = 0;
        int i15 = 0;
        while (i13 < virtualChildCount) {
            View virtualChildAt = getVirtualChildAt(i13);
            if (virtualChildAt == null) {
                this.mTotalLength += measureNullChild(i13);
                i9 = i10;
                max2 = i14;
            } else if (virtualChildAt.getVisibility() == 8) {
                i13 += getChildrenSkipCount(virtualChildAt, i13);
                i9 = i10;
                max2 = i14;
            } else {
                if (hasDividerBeforeChildAt(i13)) {
                    this.mTotalLength += this.mDividerWidth;
                }
                LayoutParams layoutParams = (LayoutParams) virtualChildAt.getLayoutParams();
                f += layoutParams.weight;
                if (mode == 1073741824 && layoutParams.width == 0 && layoutParams.weight > 0.0f) {
                    if (z4) {
                        this.mTotalLength += layoutParams.leftMargin + layoutParams.rightMargin;
                    } else {
                        int i16 = this.mTotalLength;
                        this.mTotalLength = Math.max(i16, layoutParams.leftMargin + i16 + layoutParams.rightMargin);
                    }
                    if (z) {
                        int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
                        virtualChildAt.measure(makeMeasureSpec, makeMeasureSpec);
                    } else {
                        z2 = true;
                    }
                } else {
                    if (layoutParams.width != 0 || layoutParams.weight <= 0.0f) {
                        i8 = Integer.MIN_VALUE;
                    } else {
                        layoutParams.width = -2;
                        i8 = 0;
                    }
                    measureChildBeforeLayout(virtualChildAt, i13, i, f == 0.0f ? this.mTotalLength : 0, i2, 0);
                    if (i8 != Integer.MIN_VALUE) {
                        layoutParams.width = i8;
                    }
                    int measuredWidth = virtualChildAt.getMeasuredWidth();
                    if (z4) {
                        this.mTotalLength += layoutParams.leftMargin + measuredWidth + layoutParams.rightMargin + getNextLocationOffset(virtualChildAt);
                    } else {
                        int i17 = this.mTotalLength;
                        this.mTotalLength = Math.max(i17, i17 + measuredWidth + layoutParams.leftMargin + layoutParams.rightMargin + getNextLocationOffset(virtualChildAt));
                    }
                    if (z3) {
                        i11 = Math.max(measuredWidth, i11);
                    }
                }
                int i18 = i14;
                boolean z7 = false;
                if (mode2 != 1073741824 && layoutParams.height == -1) {
                    z5 = true;
                    z7 = true;
                }
                int i19 = layoutParams.topMargin + layoutParams.bottomMargin;
                int measuredHeight = virtualChildAt.getMeasuredHeight() + i19;
                int combineMeasuredStates = ViewUtils.combineMeasuredStates(i10, ViewCompat.getMeasuredState(virtualChildAt));
                if (z && (baseline2 = virtualChildAt.getBaseline()) != -1) {
                    int i20 = ((((layoutParams.gravity < 0 ? this.mGravity : layoutParams.gravity) & 112) >> 4) & (-2)) >> 1;
                    iArr[i20] = Math.max(iArr[i20], baseline2);
                    iArr2[i20] = Math.max(iArr2[i20], measuredHeight - baseline2);
                }
                i12 = Math.max(i12, measuredHeight);
                boolean z8 = z6 && layoutParams.height == -1;
                if (layoutParams.weight > 0.0f) {
                    i15 = Math.max(i15, z7 ? i19 : measuredHeight);
                    max2 = i18;
                } else {
                    if (!z7) {
                        i19 = measuredHeight;
                    }
                    max2 = Math.max(i18, i19);
                }
                i9 = combineMeasuredStates;
                i13 += getChildrenSkipCount(virtualChildAt, i13);
                z6 = z8;
            }
            i13++;
            i14 = max2;
            i10 = i9;
        }
        int i21 = i15;
        int i22 = i12;
        int i23 = i11;
        if (this.mTotalLength > 0 && hasDividerBeforeChildAt(virtualChildCount)) {
            this.mTotalLength += this.mDividerWidth;
        }
        if (iArr[1] != -1 || iArr[0] != -1 || iArr[2] != -1 || iArr[3] != -1) {
            i22 = Math.max(i22, Math.max(iArr[3], Math.max(iArr[0], Math.max(iArr[1], iArr[2]))) + Math.max(iArr2[3], Math.max(iArr2[0], Math.max(iArr2[1], iArr2[2]))));
        }
        if (!z3) {
            i3 = i22;
        } else if (mode == Integer.MIN_VALUE || mode == 0) {
            this.mTotalLength = 0;
            int i24 = 0;
            while (i24 < virtualChildCount) {
                View virtualChildAt2 = getVirtualChildAt(i24);
                if (virtualChildAt2 == null) {
                    this.mTotalLength += measureNullChild(i24);
                } else if (virtualChildAt2.getVisibility() == 8) {
                    i24 += getChildrenSkipCount(virtualChildAt2, i24);
                } else {
                    LayoutParams layoutParams2 = (LayoutParams) virtualChildAt2.getLayoutParams();
                    if (z4) {
                        this.mTotalLength += layoutParams2.leftMargin + i23 + layoutParams2.rightMargin + getNextLocationOffset(virtualChildAt2);
                    } else {
                        int i25 = this.mTotalLength;
                        this.mTotalLength = Math.max(i25, i25 + i23 + layoutParams2.leftMargin + layoutParams2.rightMargin + getNextLocationOffset(virtualChildAt2));
                    }
                }
                i24++;
            }
            i3 = i22;
        } else {
            i3 = i22;
        }
        this.mTotalLength += getPaddingLeft() + getPaddingRight();
        int resolveSizeAndState = ViewCompat.resolveSizeAndState(Math.max(this.mTotalLength, getSuggestedMinimumWidth()), i, 0);
        int i26 = (resolveSizeAndState & 16777215) - this.mTotalLength;
        if (z2 || (i26 != 0 && f > 0.0f)) {
            float f2 = this.mWeightSum;
            if (f2 > 0.0f) {
                f = f2;
            }
            iArr[3] = -1;
            iArr[2] = -1;
            iArr[1] = -1;
            iArr[0] = -1;
            iArr2[3] = -1;
            iArr2[2] = -1;
            iArr2[1] = -1;
            iArr2[0] = -1;
            this.mTotalLength = 0;
            int i27 = i14;
            int i28 = i10;
            int i29 = i26;
            int i30 = -1;
            int i31 = i27;
            for (int i32 = 0; i32 < virtualChildCount; i32++) {
                View virtualChildAt3 = getVirtualChildAt(i32);
                if (virtualChildAt3 != null && virtualChildAt3.getVisibility() != 8) {
                    LayoutParams layoutParams3 = (LayoutParams) virtualChildAt3.getLayoutParams();
                    float f3 = layoutParams3.weight;
                    if (f3 > 0.0f) {
                        int i33 = (int) ((i29 * f3) / f);
                        int childMeasureSpec = getChildMeasureSpec(i2, getPaddingTop() + getPaddingBottom() + layoutParams3.topMargin + layoutParams3.bottomMargin, layoutParams3.height);
                        if (layoutParams3.width == 0 && mode == 1073741824) {
                            virtualChildAt3.measure(View.MeasureSpec.makeMeasureSpec(i33 > 0 ? i33 : 0, 1073741824), childMeasureSpec);
                        } else {
                            int measuredWidth2 = virtualChildAt3.getMeasuredWidth() + i33;
                            if (measuredWidth2 < 0) {
                                measuredWidth2 = 0;
                            }
                            virtualChildAt3.measure(View.MeasureSpec.makeMeasureSpec(measuredWidth2, 1073741824), childMeasureSpec);
                        }
                        i28 = ViewUtils.combineMeasuredStates(i28, ViewCompat.getMeasuredState(virtualChildAt3) & (-16777216));
                        f -= f3;
                        i29 -= i33;
                    }
                    if (z4) {
                        this.mTotalLength += virtualChildAt3.getMeasuredWidth() + layoutParams3.leftMargin + layoutParams3.rightMargin + getNextLocationOffset(virtualChildAt3);
                    } else {
                        int i34 = this.mTotalLength;
                        this.mTotalLength = Math.max(i34, virtualChildAt3.getMeasuredWidth() + i34 + layoutParams3.leftMargin + layoutParams3.rightMargin + getNextLocationOffset(virtualChildAt3));
                    }
                    boolean z9 = mode2 != 1073741824 && layoutParams3.height == -1;
                    int i35 = layoutParams3.topMargin + layoutParams3.bottomMargin;
                    int measuredHeight2 = virtualChildAt3.getMeasuredHeight() + i35;
                    int max3 = Math.max(i30, measuredHeight2);
                    int max4 = Math.max(i31, z9 ? i35 : measuredHeight2);
                    z6 = z6 && layoutParams3.height == -1;
                    if (z && (baseline = virtualChildAt3.getBaseline()) != -1) {
                        int i36 = ((((layoutParams3.gravity < 0 ? this.mGravity : layoutParams3.gravity) & 112) >> 4) & (-2)) >> 1;
                        iArr[i36] = Math.max(iArr[i36], baseline);
                        iArr2[i36] = Math.max(iArr2[i36], measuredHeight2 - baseline);
                    }
                    i31 = max4;
                    i30 = max3;
                }
            }
            i4 = virtualChildCount;
            this.mTotalLength += getPaddingLeft() + getPaddingRight();
            max = (iArr[1] == -1 && iArr[0] == -1 && iArr[2] == -1 && iArr[3] == -1) ? i30 : Math.max(i30, Math.max(iArr[3], Math.max(iArr[0], Math.max(iArr[1], iArr[2]))) + Math.max(iArr2[3], Math.max(iArr2[0], Math.max(iArr2[1], iArr2[2]))));
            i5 = resolveSizeAndState;
            i6 = i31;
            i7 = i28;
        } else {
            int max5 = Math.max(i14, i21);
            if (z3 && mode != 1073741824) {
                for (int i37 = 0; i37 < virtualChildCount; i37++) {
                    View virtualChildAt4 = getVirtualChildAt(i37);
                    if (virtualChildAt4 != null && virtualChildAt4.getVisibility() != 8 && ((LayoutParams) virtualChildAt4.getLayoutParams()).weight > 0.0f) {
                        virtualChildAt4.measure(View.MeasureSpec.makeMeasureSpec(i23, 1073741824), View.MeasureSpec.makeMeasureSpec(virtualChildAt4.getMeasuredHeight(), 1073741824));
                    }
                }
            }
            i7 = i10;
            max = i3;
            i6 = max5;
            i5 = resolveSizeAndState;
            i4 = virtualChildCount;
        }
        if (!z6 && mode2 != 1073741824) {
            max = i6;
        }
        setMeasuredDimension(i5 | ((-16777216) & i7), ViewCompat.resolveSizeAndState(Math.max(max + getPaddingTop() + getPaddingBottom(), getSuggestedMinimumHeight()), i2, i7 << 16));
        if (z5) {
            forceUniformHeight(i4, i);
        }
    }

    int measureNullChild(int i) {
        return 0;
    }

    void measureVertical(int i, int i2) {
        int i3;
        int i4;
        int i5;
        int i6;
        int i7;
        int i8;
        int i9;
        this.mTotalLength = 0;
        int i10 = 0;
        float f = 0.0f;
        int virtualChildCount = getVirtualChildCount();
        int mode = View.MeasureSpec.getMode(i);
        int mode2 = View.MeasureSpec.getMode(i2);
        int i11 = this.mBaselineAlignedChildIndex;
        boolean z = this.mUseLargestChild;
        boolean z2 = false;
        int i12 = 0;
        int i13 = 0;
        int i14 = 0;
        int i15 = Integer.MIN_VALUE;
        boolean z3 = false;
        int i16 = 0;
        boolean z4 = true;
        while (i16 < virtualChildCount) {
            View virtualChildAt = getVirtualChildAt(i16);
            if (virtualChildAt == null) {
                this.mTotalLength += measureNullChild(i16);
            } else if (virtualChildAt.getVisibility() == 8) {
                i16 += getChildrenSkipCount(virtualChildAt, i16);
            } else {
                if (hasDividerBeforeChildAt(i16)) {
                    this.mTotalLength += this.mDividerHeight;
                }
                LayoutParams layoutParams = (LayoutParams) virtualChildAt.getLayoutParams();
                f += layoutParams.weight;
                if (mode2 == 1073741824 && layoutParams.height == 0 && layoutParams.weight > 0.0f) {
                    int i17 = this.mTotalLength;
                    this.mTotalLength = Math.max(i17, layoutParams.topMargin + i17 + layoutParams.bottomMargin);
                    z3 = true;
                } else {
                    if (layoutParams.height != 0 || layoutParams.weight <= 0.0f) {
                        i7 = Integer.MIN_VALUE;
                    } else {
                        layoutParams.height = -2;
                        i7 = 0;
                    }
                    measureChildBeforeLayout(virtualChildAt, i16, i, 0, i2, f == 0.0f ? this.mTotalLength : 0);
                    if (i7 != Integer.MIN_VALUE) {
                        layoutParams.height = i7;
                    }
                    int measuredHeight = virtualChildAt.getMeasuredHeight();
                    int i18 = this.mTotalLength;
                    this.mTotalLength = Math.max(i18, i18 + measuredHeight + layoutParams.topMargin + layoutParams.bottomMargin + getNextLocationOffset(virtualChildAt));
                    if (z) {
                        i15 = Math.max(measuredHeight, i15);
                    }
                }
                if (i11 >= 0 && i11 == i16 + 1) {
                    this.mBaselineChildTop = this.mTotalLength;
                }
                if (i16 < i11 && layoutParams.weight > 0.0f) {
                    throw new RuntimeException("A child of LinearLayout with index less than mBaselineAlignedChildIndex has weight > 0, which won't work.  Either remove the weight, or don't set mBaselineAlignedChildIndex.");
                }
                boolean z5 = false;
                if (mode != 1073741824 && layoutParams.width == -1) {
                    z2 = true;
                    z5 = true;
                }
                int i19 = layoutParams.leftMargin + layoutParams.rightMargin;
                int measuredWidth = virtualChildAt.getMeasuredWidth() + i19;
                int max = Math.max(i13, measuredWidth);
                i10 = ViewUtils.combineMeasuredStates(i10, ViewCompat.getMeasuredState(virtualChildAt));
                z4 = z4 && layoutParams.width == -1;
                if (layoutParams.weight > 0.0f) {
                    if (z5) {
                        measuredWidth = i19;
                    }
                    int max2 = Math.max(i14, measuredWidth);
                    i9 = i12;
                    i8 = max2;
                } else {
                    if (z5) {
                        measuredWidth = i19;
                    }
                    int max3 = Math.max(i12, measuredWidth);
                    i8 = i14;
                    i9 = max3;
                }
                i16 += getChildrenSkipCount(virtualChildAt, i16);
                int i20 = i8;
                i12 = i9;
                i14 = i20;
                i13 = max;
            }
            i16++;
        }
        int i21 = i15;
        int i22 = i13;
        int i23 = i14;
        if (this.mTotalLength > 0 && hasDividerBeforeChildAt(virtualChildCount)) {
            this.mTotalLength += this.mDividerHeight;
        }
        if (z && (mode2 == Integer.MIN_VALUE || mode2 == 0)) {
            this.mTotalLength = 0;
            int i24 = 0;
            while (i24 < virtualChildCount) {
                View virtualChildAt2 = getVirtualChildAt(i24);
                if (virtualChildAt2 == null) {
                    this.mTotalLength += measureNullChild(i24);
                } else if (virtualChildAt2.getVisibility() == 8) {
                    i24 += getChildrenSkipCount(virtualChildAt2, i24);
                } else {
                    LayoutParams layoutParams2 = (LayoutParams) virtualChildAt2.getLayoutParams();
                    int i25 = this.mTotalLength;
                    this.mTotalLength = Math.max(i25, i25 + i21 + layoutParams2.topMargin + layoutParams2.bottomMargin + getNextLocationOffset(virtualChildAt2));
                }
                i24++;
            }
        }
        this.mTotalLength += getPaddingTop() + getPaddingBottom();
        int resolveSizeAndState = ViewCompat.resolveSizeAndState(Math.max(this.mTotalLength, getSuggestedMinimumHeight()), i2, 0);
        int i26 = (resolveSizeAndState & 16777215) - this.mTotalLength;
        if (z3 || (i26 != 0 && f > 0.0f)) {
            float f2 = this.mWeightSum;
            if (f2 > 0.0f) {
                f = f2;
            }
            this.mTotalLength = 0;
            int i27 = i12;
            int i28 = i13;
            int i29 = i10;
            int i30 = i26;
            for (int i31 = 0; i31 < virtualChildCount; i31++) {
                View virtualChildAt3 = getVirtualChildAt(i31);
                if (virtualChildAt3.getVisibility() != 8) {
                    LayoutParams layoutParams3 = (LayoutParams) virtualChildAt3.getLayoutParams();
                    float f3 = layoutParams3.weight;
                    if (f3 > 0.0f) {
                        int i32 = (int) ((i30 * f3) / f);
                        int paddingLeft = getPaddingLeft();
                        int paddingRight = getPaddingRight();
                        f -= f3;
                        int i33 = layoutParams3.leftMargin;
                        int i34 = layoutParams3.rightMargin;
                        int i35 = i30 - i32;
                        int childMeasureSpec = getChildMeasureSpec(i, paddingLeft + paddingRight + i33 + i34, layoutParams3.width);
                        if (layoutParams3.height == 0 && mode2 == 1073741824) {
                            virtualChildAt3.measure(childMeasureSpec, View.MeasureSpec.makeMeasureSpec(i32 > 0 ? i32 : 0, 1073741824));
                        } else {
                            int measuredHeight2 = virtualChildAt3.getMeasuredHeight() + i32;
                            if (measuredHeight2 < 0) {
                                measuredHeight2 = 0;
                            }
                            virtualChildAt3.measure(childMeasureSpec, View.MeasureSpec.makeMeasureSpec(measuredHeight2, 1073741824));
                        }
                        i29 = ViewUtils.combineMeasuredStates(i29, ViewCompat.getMeasuredState(virtualChildAt3) & (-256));
                        i30 = i35;
                    }
                    int i36 = layoutParams3.leftMargin + layoutParams3.rightMargin;
                    int measuredWidth2 = virtualChildAt3.getMeasuredWidth() + i36;
                    int max4 = Math.max(i28, measuredWidth2);
                    int max5 = Math.max(i27, mode != 1073741824 && layoutParams3.width == -1 ? i36 : measuredWidth2);
                    boolean z6 = z4 && layoutParams3.width == -1;
                    int i37 = this.mTotalLength;
                    this.mTotalLength = Math.max(i37, i37 + virtualChildAt3.getMeasuredHeight() + layoutParams3.topMargin + layoutParams3.bottomMargin + getNextLocationOffset(virtualChildAt3));
                    i28 = max4;
                    z4 = z6;
                    i27 = max5;
                }
            }
            this.mTotalLength += getPaddingTop() + getPaddingBottom();
            int i38 = i28;
            i3 = i27;
            i4 = i38;
            i10 = i29;
        } else {
            int max6 = Math.max(i12, i23);
            if (!z) {
                i5 = i22;
                i6 = max6;
            } else if (mode2 != 1073741824) {
                for (int i39 = 0; i39 < virtualChildCount; i39++) {
                    View virtualChildAt4 = getVirtualChildAt(i39);
                    if (virtualChildAt4 != null && virtualChildAt4.getVisibility() != 8 && ((LayoutParams) virtualChildAt4.getLayoutParams()).weight > 0.0f) {
                        virtualChildAt4.measure(View.MeasureSpec.makeMeasureSpec(virtualChildAt4.getMeasuredWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(i21, 1073741824));
                    }
                }
                i6 = max6;
                i5 = i22;
            } else {
                i5 = i22;
                i6 = max6;
            }
            i3 = i6;
            i4 = i5;
        }
        if (!z4 && mode != 1073741824) {
            i4 = i3;
        }
        setMeasuredDimension(ViewCompat.resolveSizeAndState(Math.max(i4 + getPaddingLeft() + getPaddingRight(), getSuggestedMinimumWidth()), i, i10), resolveSizeAndState);
        if (z2) {
            forceUniformWidth(virtualChildCount, i2);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        if (this.mDivider == null) {
            return;
        }
        if (this.mOrientation == 1) {
            drawDividersVertical(canvas);
        } else {
            drawDividersHorizontal(canvas);
        }
    }

    @Override // android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if (Build.VERSION.SDK_INT >= 14) {
            super.onInitializeAccessibilityEvent(accessibilityEvent);
            accessibilityEvent.setClassName(LinearLayoutCompat.class.getName());
        }
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        if (Build.VERSION.SDK_INT >= 14) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setClassName(LinearLayoutCompat.class.getName());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (this.mOrientation == 1) {
            layoutVertical(i, i2, i3, i4);
        } else {
            layoutHorizontal(i, i2, i3, i4);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int i, int i2) {
        if (this.mOrientation == 1) {
            measureVertical(i, i2);
        } else {
            measureHorizontal(i, i2);
        }
    }

    public void setBaselineAligned(boolean z) {
        this.mBaselineAligned = z;
    }

    public void setBaselineAlignedChildIndex(int i) {
        if (i >= 0 && i < getChildCount()) {
            this.mBaselineAlignedChildIndex = i;
            return;
        }
        throw new IllegalArgumentException("base aligned child index out of range (0, " + getChildCount() + Separators.RPAREN);
    }

    public void setDividerDrawable(Drawable drawable) {
        if (drawable == this.mDivider) {
            return;
        }
        this.mDivider = drawable;
        boolean z = false;
        if (drawable != null) {
            this.mDividerWidth = drawable.getIntrinsicWidth();
            this.mDividerHeight = drawable.getIntrinsicHeight();
        } else {
            this.mDividerWidth = 0;
            this.mDividerHeight = 0;
        }
        if (drawable == null) {
            z = true;
        }
        setWillNotDraw(z);
        requestLayout();
    }

    public void setDividerPadding(int i) {
        this.mDividerPadding = i;
    }

    public void setGravity(int i) {
        if (this.mGravity != i) {
            if ((8388615 & i) == 0) {
                i |= 8388611;
            }
            if ((i & 112) == 0) {
                i |= 48;
            }
            this.mGravity = i;
            requestLayout();
        }
    }

    public void setHorizontalGravity(int i) {
        int i2 = i & 8388615;
        int i3 = this.mGravity;
        if ((8388615 & i3) != i2) {
            this.mGravity = ((-8388616) & i3) | i2;
            requestLayout();
        }
    }

    public void setMeasureWithLargestChildEnabled(boolean z) {
        this.mUseLargestChild = z;
    }

    public void setOrientation(int i) {
        if (this.mOrientation != i) {
            this.mOrientation = i;
            requestLayout();
        }
    }

    public void setShowDividers(int i) {
        if (i != this.mShowDividers) {
            requestLayout();
        }
        this.mShowDividers = i;
    }

    public void setVerticalGravity(int i) {
        int i2 = i & 112;
        int i3 = this.mGravity;
        if ((i3 & 112) != i2) {
            this.mGravity = (i3 & (-113)) | i2;
            requestLayout();
        }
    }

    public void setWeightSum(float f) {
        this.mWeightSum = Math.max(0.0f, f);
    }

    @Override // android.view.ViewGroup
    public boolean shouldDelayChildPressedState() {
        return false;
    }
}