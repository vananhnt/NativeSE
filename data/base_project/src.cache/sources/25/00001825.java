package android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.Pools;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.RemotableViewMethod;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.RemoteViews;
import com.android.internal.R;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

@RemoteViews.RemoteView
/* loaded from: RelativeLayout.class */
public class RelativeLayout extends ViewGroup {
    public static final int TRUE = -1;
    public static final int LEFT_OF = 0;
    public static final int RIGHT_OF = 1;
    public static final int ABOVE = 2;
    public static final int BELOW = 3;
    public static final int ALIGN_BASELINE = 4;
    public static final int ALIGN_LEFT = 5;
    public static final int ALIGN_TOP = 6;
    public static final int ALIGN_RIGHT = 7;
    public static final int ALIGN_BOTTOM = 8;
    public static final int ALIGN_PARENT_LEFT = 9;
    public static final int ALIGN_PARENT_TOP = 10;
    public static final int ALIGN_PARENT_RIGHT = 11;
    public static final int ALIGN_PARENT_BOTTOM = 12;
    public static final int CENTER_IN_PARENT = 13;
    public static final int CENTER_HORIZONTAL = 14;
    public static final int CENTER_VERTICAL = 15;
    public static final int START_OF = 16;
    public static final int END_OF = 17;
    public static final int ALIGN_START = 18;
    public static final int ALIGN_END = 19;
    public static final int ALIGN_PARENT_START = 20;
    public static final int ALIGN_PARENT_END = 21;
    private static final int VERB_COUNT = 22;
    private static final int[] RULES_VERTICAL = {2, 3, 4, 6, 8};
    private static final int[] RULES_HORIZONTAL = {0, 1, 5, 7, 16, 17, 18, 19};
    private View mBaselineView;
    private boolean mHasBaselineAlignedChild;
    private int mGravity;
    private final Rect mContentBounds;
    private final Rect mSelfBounds;
    private int mIgnoreGravity;
    private SortedSet<View> mTopToBottomLeftToRightSet;
    private boolean mDirtyHierarchy;
    private View[] mSortedHorizontalChildren;
    private View[] mSortedVerticalChildren;
    private final DependencyGraph mGraph;
    private boolean mAllowBrokenMeasureSpecs;
    private boolean mMeasureVerticalWithPaddingMargin;
    private static final int DEFAULT_WIDTH = 65536;

    public RelativeLayout(Context context) {
        super(context);
        this.mBaselineView = null;
        this.mGravity = 8388659;
        this.mContentBounds = new Rect();
        this.mSelfBounds = new Rect();
        this.mTopToBottomLeftToRightSet = null;
        this.mGraph = new DependencyGraph();
        this.mAllowBrokenMeasureSpecs = false;
        this.mMeasureVerticalWithPaddingMargin = false;
        queryCompatibilityModes(context);
    }

    public RelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mBaselineView = null;
        this.mGravity = 8388659;
        this.mContentBounds = new Rect();
        this.mSelfBounds = new Rect();
        this.mTopToBottomLeftToRightSet = null;
        this.mGraph = new DependencyGraph();
        this.mAllowBrokenMeasureSpecs = false;
        this.mMeasureVerticalWithPaddingMargin = false;
        initFromAttributes(context, attrs);
        queryCompatibilityModes(context);
    }

    public RelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mBaselineView = null;
        this.mGravity = 8388659;
        this.mContentBounds = new Rect();
        this.mSelfBounds = new Rect();
        this.mTopToBottomLeftToRightSet = null;
        this.mGraph = new DependencyGraph();
        this.mAllowBrokenMeasureSpecs = false;
        this.mMeasureVerticalWithPaddingMargin = false;
        initFromAttributes(context, attrs);
        queryCompatibilityModes(context);
    }

    private void initFromAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RelativeLayout);
        this.mIgnoreGravity = a.getResourceId(1, -1);
        this.mGravity = a.getInt(0, this.mGravity);
        a.recycle();
    }

    private void queryCompatibilityModes(Context context) {
        int version = context.getApplicationInfo().targetSdkVersion;
        this.mAllowBrokenMeasureSpecs = version <= 17;
        this.mMeasureVerticalWithPaddingMargin = version >= 18;
    }

    @Override // android.view.ViewGroup
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    @RemotableViewMethod
    public void setIgnoreGravity(int viewId) {
        this.mIgnoreGravity = viewId;
    }

    public int getGravity() {
        return this.mGravity;
    }

    @RemotableViewMethod
    public void setGravity(int gravity) {
        if (this.mGravity != gravity) {
            if ((gravity & 8388615) == 0) {
                gravity |= 8388611;
            }
            if ((gravity & 112) == 0) {
                gravity |= 48;
            }
            this.mGravity = gravity;
            requestLayout();
        }
    }

    @RemotableViewMethod
    public void setHorizontalGravity(int horizontalGravity) {
        int gravity = horizontalGravity & 8388615;
        if ((this.mGravity & 8388615) != gravity) {
            this.mGravity = (this.mGravity & (-8388616)) | gravity;
            requestLayout();
        }
    }

    @RemotableViewMethod
    public void setVerticalGravity(int verticalGravity) {
        int gravity = verticalGravity & 112;
        if ((this.mGravity & 112) != gravity) {
            this.mGravity = (this.mGravity & (-113)) | gravity;
            requestLayout();
        }
    }

    @Override // android.view.View
    public int getBaseline() {
        return this.mBaselineView != null ? this.mBaselineView.getBaseline() : super.getBaseline();
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestLayout() {
        super.requestLayout();
        this.mDirtyHierarchy = true;
    }

    private void sortChildren() {
        int count = getChildCount();
        if (this.mSortedVerticalChildren == null || this.mSortedVerticalChildren.length != count) {
            this.mSortedVerticalChildren = new View[count];
        }
        if (this.mSortedHorizontalChildren == null || this.mSortedHorizontalChildren.length != count) {
            this.mSortedHorizontalChildren = new View[count];
        }
        DependencyGraph graph = this.mGraph;
        graph.clear();
        for (int i = 0; i < count; i++) {
            graph.add(getChildAt(i));
        }
        graph.getSortedViews(this.mSortedVerticalChildren, RULES_VERTICAL);
        graph.getSortedViews(this.mSortedHorizontalChildren, RULES_HORIZONTAL);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        View[] views;
        if (this.mDirtyHierarchy) {
            this.mDirtyHierarchy = false;
            sortChildren();
        }
        int myWidth = -1;
        int myHeight = -1;
        int width = 0;
        int height = 0;
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode != 0) {
            myWidth = widthSize;
        }
        if (heightMode != 0) {
            myHeight = heightSize;
        }
        if (widthMode == 1073741824) {
            width = myWidth;
        }
        if (heightMode == 1073741824) {
            height = myHeight;
        }
        this.mHasBaselineAlignedChild = false;
        View ignore = null;
        int gravity = this.mGravity & 8388615;
        boolean horizontalGravity = (gravity == 8388611 || gravity == 0) ? false : true;
        int gravity2 = this.mGravity & 112;
        boolean verticalGravity = (gravity2 == 48 || gravity2 == 0) ? false : true;
        int left = Integer.MAX_VALUE;
        int top = Integer.MAX_VALUE;
        int right = Integer.MIN_VALUE;
        int bottom = Integer.MIN_VALUE;
        boolean offsetHorizontalAxis = false;
        boolean offsetVerticalAxis = false;
        if ((horizontalGravity || verticalGravity) && this.mIgnoreGravity != -1) {
            ignore = findViewById(this.mIgnoreGravity);
        }
        boolean isWrapContentWidth = widthMode != 1073741824;
        boolean isWrapContentHeight = heightMode != 1073741824;
        int layoutDirection = getLayoutDirection();
        if (isLayoutRtl() && myWidth == -1) {
            myWidth = 65536;
        }
        for (View child : this.mSortedHorizontalChildren) {
            if (child.getVisibility() != 8) {
                LayoutParams params = (LayoutParams) child.getLayoutParams();
                applyHorizontalSizeRules(params, myWidth, params.getRules(layoutDirection));
                measureChildHorizontal(child, params, myWidth, myHeight);
                if (positionChildHorizontal(child, params, myWidth, isWrapContentWidth)) {
                    offsetHorizontalAxis = true;
                }
            }
        }
        View[] views2 = this.mSortedVerticalChildren;
        int count = views2.length;
        int targetSdkVersion = getContext().getApplicationInfo().targetSdkVersion;
        for (View child2 : views2) {
            if (child2.getVisibility() != 8) {
                LayoutParams params2 = (LayoutParams) child2.getLayoutParams();
                applyVerticalSizeRules(params2, myHeight);
                measureChild(child2, params2, myWidth, myHeight);
                if (positionChildVertical(child2, params2, myHeight, isWrapContentHeight)) {
                    offsetVerticalAxis = true;
                }
                if (isWrapContentWidth) {
                    width = isLayoutRtl() ? targetSdkVersion < 19 ? Math.max(width, myWidth - params2.mLeft) : Math.max(width, (myWidth - params2.mLeft) - params2.leftMargin) : targetSdkVersion < 19 ? Math.max(width, params2.mRight) : Math.max(width, params2.mRight + params2.rightMargin);
                }
                if (isWrapContentHeight) {
                    height = targetSdkVersion < 19 ? Math.max(height, params2.mBottom) : Math.max(height, params2.mBottom + params2.bottomMargin);
                }
                if (child2 != ignore || verticalGravity) {
                    left = Math.min(left, params2.mLeft - params2.leftMargin);
                    top = Math.min(top, params2.mTop - params2.topMargin);
                }
                if (child2 != ignore || horizontalGravity) {
                    right = Math.max(right, params2.mRight + params2.rightMargin);
                    bottom = Math.max(bottom, params2.mBottom + params2.bottomMargin);
                }
            }
        }
        if (this.mHasBaselineAlignedChild) {
            for (int i = 0; i < count; i++) {
                View child3 = getChildAt(i);
                if (child3.getVisibility() != 8) {
                    LayoutParams params3 = (LayoutParams) child3.getLayoutParams();
                    alignBaseline(child3, params3);
                    if (child3 != ignore || verticalGravity) {
                        left = Math.min(left, params3.mLeft - params3.leftMargin);
                        top = Math.min(top, params3.mTop - params3.topMargin);
                    }
                    if (child3 != ignore || horizontalGravity) {
                        right = Math.max(right, params3.mRight + params3.rightMargin);
                        bottom = Math.max(bottom, params3.mBottom + params3.bottomMargin);
                    }
                }
            }
        }
        if (isWrapContentWidth) {
            int width2 = width + this.mPaddingRight;
            if (this.mLayoutParams != null && this.mLayoutParams.width >= 0) {
                width2 = Math.max(width2, this.mLayoutParams.width);
            }
            width = resolveSize(Math.max(width2, getSuggestedMinimumWidth()), widthMeasureSpec);
            if (offsetHorizontalAxis) {
                for (int i2 = 0; i2 < count; i2++) {
                    View child4 = getChildAt(i2);
                    if (child4.getVisibility() != 8) {
                        LayoutParams params4 = (LayoutParams) child4.getLayoutParams();
                        int[] rules = params4.getRules(layoutDirection);
                        if (rules[13] != 0 || rules[14] != 0) {
                            centerHorizontal(child4, params4, width);
                        } else if (rules[11] != 0) {
                            int childWidth = child4.getMeasuredWidth();
                            params4.mLeft = (width - this.mPaddingRight) - childWidth;
                            params4.mRight = params4.mLeft + childWidth;
                        }
                    }
                }
            }
        }
        if (isWrapContentHeight) {
            int height2 = height + this.mPaddingBottom;
            if (this.mLayoutParams != null && this.mLayoutParams.height >= 0) {
                height2 = Math.max(height2, this.mLayoutParams.height);
            }
            height = resolveSize(Math.max(height2, getSuggestedMinimumHeight()), heightMeasureSpec);
            if (offsetVerticalAxis) {
                for (int i3 = 0; i3 < count; i3++) {
                    View child5 = getChildAt(i3);
                    if (child5.getVisibility() != 8) {
                        LayoutParams params5 = (LayoutParams) child5.getLayoutParams();
                        int[] rules2 = params5.getRules(layoutDirection);
                        if (rules2[13] != 0 || rules2[15] != 0) {
                            centerVertical(child5, params5, height);
                        } else if (rules2[12] != 0) {
                            int childHeight = child5.getMeasuredHeight();
                            params5.mTop = (height - this.mPaddingBottom) - childHeight;
                            params5.mBottom = params5.mTop + childHeight;
                        }
                    }
                }
            }
        }
        if (horizontalGravity || verticalGravity) {
            Rect selfBounds = this.mSelfBounds;
            selfBounds.set(this.mPaddingLeft, this.mPaddingTop, width - this.mPaddingRight, height - this.mPaddingBottom);
            Rect contentBounds = this.mContentBounds;
            Gravity.apply(this.mGravity, right - left, bottom - top, selfBounds, contentBounds, layoutDirection);
            int horizontalOffset = contentBounds.left - left;
            int verticalOffset = contentBounds.top - top;
            if (horizontalOffset != 0 || verticalOffset != 0) {
                for (int i4 = 0; i4 < count; i4++) {
                    View child6 = getChildAt(i4);
                    if (child6.getVisibility() != 8 && child6 != ignore) {
                        LayoutParams params6 = (LayoutParams) child6.getLayoutParams();
                        if (horizontalGravity) {
                            LayoutParams.access$112(params6, horizontalOffset);
                            LayoutParams.access$212(params6, horizontalOffset);
                        }
                        if (verticalGravity) {
                            LayoutParams.access$412(params6, verticalOffset);
                            LayoutParams.access$312(params6, verticalOffset);
                        }
                    }
                }
            }
        }
        if (isLayoutRtl()) {
            int offsetWidth = myWidth - width;
            for (int i5 = 0; i5 < count; i5++) {
                View child7 = getChildAt(i5);
                if (child7.getVisibility() != 8) {
                    LayoutParams params7 = (LayoutParams) child7.getLayoutParams();
                    LayoutParams.access$120(params7, offsetWidth);
                    LayoutParams.access$220(params7, offsetWidth);
                }
            }
        }
        setMeasuredDimension(width, height);
    }

    private void alignBaseline(View child, LayoutParams params) {
        LayoutParams anchorParams;
        int layoutDirection = getLayoutDirection();
        int[] rules = params.getRules(layoutDirection);
        int anchorBaseline = getRelatedViewBaseline(rules, 4);
        if (anchorBaseline != -1 && (anchorParams = getRelatedViewParams(rules, 4)) != null) {
            int offset = anchorParams.mTop + anchorBaseline;
            int baseline = child.getBaseline();
            if (baseline != -1) {
                offset -= baseline;
            }
            int height = params.mBottom - params.mTop;
            params.mTop = offset;
            params.mBottom = params.mTop + height;
        }
        if (this.mBaselineView == null) {
            this.mBaselineView = child;
            return;
        }
        LayoutParams lp = (LayoutParams) this.mBaselineView.getLayoutParams();
        if (params.mTop < lp.mTop || (params.mTop == lp.mTop && params.mLeft < lp.mLeft)) {
            this.mBaselineView = child;
        }
    }

    private void measureChild(View child, LayoutParams params, int myWidth, int myHeight) {
        int childWidthMeasureSpec = getChildMeasureSpec(params.mLeft, params.mRight, params.width, params.leftMargin, params.rightMargin, this.mPaddingLeft, this.mPaddingRight, myWidth);
        int childHeightMeasureSpec = getChildMeasureSpec(params.mTop, params.mBottom, params.height, params.topMargin, params.bottomMargin, this.mPaddingTop, this.mPaddingBottom, myHeight);
        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    private void measureChildHorizontal(View child, LayoutParams params, int myWidth, int myHeight) {
        int childHeightMeasureSpec;
        int childWidthMeasureSpec = getChildMeasureSpec(params.mLeft, params.mRight, params.width, params.leftMargin, params.rightMargin, this.mPaddingLeft, this.mPaddingRight, myWidth);
        int maxHeight = myHeight;
        if (this.mMeasureVerticalWithPaddingMargin) {
            maxHeight = Math.max(0, (((myHeight - this.mPaddingTop) - this.mPaddingBottom) - params.topMargin) - params.bottomMargin);
        }
        if (myHeight < 0 && !this.mAllowBrokenMeasureSpecs) {
            if (params.height >= 0) {
                childHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(params.height, 1073741824);
            } else {
                childHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
            }
        } else if (params.width == -1) {
            childHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(maxHeight, 1073741824);
        } else {
            childHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(maxHeight, Integer.MIN_VALUE);
        }
        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    private int getChildMeasureSpec(int childStart, int childEnd, int childSize, int startMargin, int endMargin, int startPadding, int endPadding, int mySize) {
        if (mySize < 0 && !this.mAllowBrokenMeasureSpecs) {
            if (childSize >= 0) {
                return View.MeasureSpec.makeMeasureSpec(childSize, 1073741824);
            }
            return View.MeasureSpec.makeMeasureSpec(0, 0);
        }
        int childSpecMode = 0;
        int childSpecSize = 0;
        int tempStart = childStart;
        int tempEnd = childEnd;
        if (tempStart < 0) {
            tempStart = startPadding + startMargin;
        }
        if (tempEnd < 0) {
            tempEnd = (mySize - endPadding) - endMargin;
        }
        int maxAvailable = tempEnd - tempStart;
        if (childStart >= 0 && childEnd >= 0) {
            childSpecMode = 1073741824;
            childSpecSize = maxAvailable;
        } else if (childSize >= 0) {
            childSpecMode = 1073741824;
            if (maxAvailable >= 0) {
                childSpecSize = Math.min(maxAvailable, childSize);
            } else {
                childSpecSize = childSize;
            }
        } else if (childSize == -1) {
            childSpecMode = 1073741824;
            childSpecSize = maxAvailable;
        } else if (childSize == -2) {
            if (maxAvailable >= 0) {
                childSpecMode = Integer.MIN_VALUE;
                childSpecSize = maxAvailable;
            } else {
                childSpecMode = 0;
                childSpecSize = 0;
            }
        }
        return View.MeasureSpec.makeMeasureSpec(childSpecSize, childSpecMode);
    }

    private boolean positionChildHorizontal(View child, LayoutParams params, int myWidth, boolean wrapContent) {
        int layoutDirection = getLayoutDirection();
        int[] rules = params.getRules(layoutDirection);
        if (params.mLeft >= 0 || params.mRight < 0) {
            if (params.mLeft < 0 || params.mRight >= 0) {
                if (params.mLeft < 0 && params.mRight < 0) {
                    if (rules[13] != 0 || rules[14] != 0) {
                        if (!wrapContent) {
                            centerHorizontal(child, params, myWidth);
                            return true;
                        }
                        params.mLeft = this.mPaddingLeft + params.leftMargin;
                        params.mRight = params.mLeft + child.getMeasuredWidth();
                        return true;
                    } else if (isLayoutRtl()) {
                        params.mRight = (myWidth - this.mPaddingRight) - params.rightMargin;
                        params.mLeft = params.mRight - child.getMeasuredWidth();
                    } else {
                        params.mLeft = this.mPaddingLeft + params.leftMargin;
                        params.mRight = params.mLeft + child.getMeasuredWidth();
                    }
                }
            } else {
                params.mRight = params.mLeft + child.getMeasuredWidth();
            }
        } else {
            params.mLeft = params.mRight - child.getMeasuredWidth();
        }
        return rules[21] != 0;
    }

    private boolean positionChildVertical(View child, LayoutParams params, int myHeight, boolean wrapContent) {
        int[] rules = params.getRules();
        if (params.mTop >= 0 || params.mBottom < 0) {
            if (params.mTop < 0 || params.mBottom >= 0) {
                if (params.mTop < 0 && params.mBottom < 0) {
                    if (rules[13] != 0 || rules[15] != 0) {
                        if (!wrapContent) {
                            centerVertical(child, params, myHeight);
                            return true;
                        }
                        params.mTop = this.mPaddingTop + params.topMargin;
                        params.mBottom = params.mTop + child.getMeasuredHeight();
                        return true;
                    }
                    params.mTop = this.mPaddingTop + params.topMargin;
                    params.mBottom = params.mTop + child.getMeasuredHeight();
                }
            } else {
                params.mBottom = params.mTop + child.getMeasuredHeight();
            }
        } else {
            params.mTop = params.mBottom - child.getMeasuredHeight();
        }
        return rules[12] != 0;
    }

    private void applyHorizontalSizeRules(LayoutParams childParams, int myWidth, int[] rules) {
        childParams.mLeft = -1;
        childParams.mRight = -1;
        LayoutParams anchorParams = getRelatedViewParams(rules, 0);
        if (anchorParams == null) {
            if (childParams.alignWithParent && rules[0] != 0 && myWidth >= 0) {
                childParams.mRight = (myWidth - this.mPaddingRight) - childParams.rightMargin;
            }
        } else {
            childParams.mRight = anchorParams.mLeft - (anchorParams.leftMargin + childParams.rightMargin);
        }
        LayoutParams anchorParams2 = getRelatedViewParams(rules, 1);
        if (anchorParams2 == null) {
            if (childParams.alignWithParent && rules[1] != 0) {
                childParams.mLeft = this.mPaddingLeft + childParams.leftMargin;
            }
        } else {
            childParams.mLeft = anchorParams2.mRight + anchorParams2.rightMargin + childParams.leftMargin;
        }
        LayoutParams anchorParams3 = getRelatedViewParams(rules, 5);
        if (anchorParams3 == null) {
            if (childParams.alignWithParent && rules[5] != 0) {
                childParams.mLeft = this.mPaddingLeft + childParams.leftMargin;
            }
        } else {
            childParams.mLeft = anchorParams3.mLeft + childParams.leftMargin;
        }
        LayoutParams anchorParams4 = getRelatedViewParams(rules, 7);
        if (anchorParams4 == null) {
            if (childParams.alignWithParent && rules[7] != 0 && myWidth >= 0) {
                childParams.mRight = (myWidth - this.mPaddingRight) - childParams.rightMargin;
            }
        } else {
            childParams.mRight = anchorParams4.mRight - childParams.rightMargin;
        }
        if (0 != rules[9]) {
            childParams.mLeft = this.mPaddingLeft + childParams.leftMargin;
        }
        if (0 == rules[11] || myWidth < 0) {
            return;
        }
        childParams.mRight = (myWidth - this.mPaddingRight) - childParams.rightMargin;
    }

    private void applyVerticalSizeRules(LayoutParams childParams, int myHeight) {
        int[] rules = childParams.getRules();
        childParams.mTop = -1;
        childParams.mBottom = -1;
        LayoutParams anchorParams = getRelatedViewParams(rules, 2);
        if (anchorParams == null) {
            if (childParams.alignWithParent && rules[2] != 0 && myHeight >= 0) {
                childParams.mBottom = (myHeight - this.mPaddingBottom) - childParams.bottomMargin;
            }
        } else {
            childParams.mBottom = anchorParams.mTop - (anchorParams.topMargin + childParams.bottomMargin);
        }
        LayoutParams anchorParams2 = getRelatedViewParams(rules, 3);
        if (anchorParams2 == null) {
            if (childParams.alignWithParent && rules[3] != 0) {
                childParams.mTop = this.mPaddingTop + childParams.topMargin;
            }
        } else {
            childParams.mTop = anchorParams2.mBottom + anchorParams2.bottomMargin + childParams.topMargin;
        }
        LayoutParams anchorParams3 = getRelatedViewParams(rules, 6);
        if (anchorParams3 == null) {
            if (childParams.alignWithParent && rules[6] != 0) {
                childParams.mTop = this.mPaddingTop + childParams.topMargin;
            }
        } else {
            childParams.mTop = anchorParams3.mTop + childParams.topMargin;
        }
        LayoutParams anchorParams4 = getRelatedViewParams(rules, 8);
        if (anchorParams4 == null) {
            if (childParams.alignWithParent && rules[8] != 0 && myHeight >= 0) {
                childParams.mBottom = (myHeight - this.mPaddingBottom) - childParams.bottomMargin;
            }
        } else {
            childParams.mBottom = anchorParams4.mBottom - childParams.bottomMargin;
        }
        if (0 != rules[10]) {
            childParams.mTop = this.mPaddingTop + childParams.topMargin;
        }
        if (0 != rules[12] && myHeight >= 0) {
            childParams.mBottom = (myHeight - this.mPaddingBottom) - childParams.bottomMargin;
        }
        if (rules[4] != 0) {
            this.mHasBaselineAlignedChild = true;
        }
    }

    private View getRelatedView(int[] rules, int relation) {
        DependencyGraph.Node node;
        int id = rules[relation];
        if (id == 0 || (node = (DependencyGraph.Node) this.mGraph.mKeyNodes.get(id)) == null) {
            return null;
        }
        View view = node.view;
        while (true) {
            View v = view;
            if (v.getVisibility() == 8) {
                int[] rules2 = ((LayoutParams) v.getLayoutParams()).getRules(v.getLayoutDirection());
                DependencyGraph.Node node2 = (DependencyGraph.Node) this.mGraph.mKeyNodes.get(rules2[relation]);
                if (node2 == null) {
                    return null;
                }
                view = node2.view;
            } else {
                return v;
            }
        }
    }

    private LayoutParams getRelatedViewParams(int[] rules, int relation) {
        View v = getRelatedView(rules, relation);
        if (v != null) {
            ViewGroup.LayoutParams params = v.getLayoutParams();
            if (params instanceof LayoutParams) {
                return (LayoutParams) v.getLayoutParams();
            }
            return null;
        }
        return null;
    }

    private int getRelatedViewBaseline(int[] rules, int relation) {
        View v = getRelatedView(rules, relation);
        if (v != null) {
            return v.getBaseline();
        }
        return -1;
    }

    private static void centerHorizontal(View child, LayoutParams params, int myWidth) {
        int childWidth = child.getMeasuredWidth();
        int left = (myWidth - childWidth) / 2;
        params.mLeft = left;
        params.mRight = left + childWidth;
    }

    private static void centerVertical(View child, LayoutParams params, int myHeight) {
        int childHeight = child.getMeasuredHeight();
        int top = (myHeight - childHeight) / 2;
        params.mTop = top;
        params.mBottom = top + childHeight;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8) {
                LayoutParams st = (LayoutParams) child.getLayoutParams();
                child.layout(st.mLeft, st.mTop, st.mRight, st.mBottom);
            }
        }
    }

    @Override // android.view.ViewGroup
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-2, -2);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override // android.view.View
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        if (this.mTopToBottomLeftToRightSet == null) {
            this.mTopToBottomLeftToRightSet = new TreeSet(new TopToBottomLeftToRightComparator());
        }
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            this.mTopToBottomLeftToRightSet.add(getChildAt(i));
        }
        for (View view : this.mTopToBottomLeftToRightSet) {
            if (view.getVisibility() == 0 && view.dispatchPopulateAccessibilityEvent(event)) {
                this.mTopToBottomLeftToRightSet.clear();
                return true;
            }
        }
        this.mTopToBottomLeftToRightSet.clear();
        return false;
    }

    @Override // android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(RelativeLayout.class.getName());
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(RelativeLayout.class.getName());
    }

    /* loaded from: RelativeLayout$TopToBottomLeftToRightComparator.class */
    private class TopToBottomLeftToRightComparator implements Comparator<View> {
        private TopToBottomLeftToRightComparator() {
        }

        @Override // java.util.Comparator
        public int compare(View first, View second) {
            int topDifference = first.getTop() - second.getTop();
            if (topDifference != 0) {
                return topDifference;
            }
            int leftDifference = first.getLeft() - second.getLeft();
            if (leftDifference != 0) {
                return leftDifference;
            }
            int heightDiference = first.getHeight() - second.getHeight();
            if (heightDiference != 0) {
                return heightDiference;
            }
            int widthDiference = first.getWidth() - second.getWidth();
            if (widthDiference != 0) {
                return widthDiference;
            }
            return 0;
        }
    }

    /* loaded from: RelativeLayout$LayoutParams.class */
    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        @ViewDebug.ExportedProperty(category = "layout", resolveId = true, indexMapping = {@ViewDebug.IntToString(from = 2, to = "above"), @ViewDebug.IntToString(from = 4, to = "alignBaseline"), @ViewDebug.IntToString(from = 8, to = "alignBottom"), @ViewDebug.IntToString(from = 5, to = "alignLeft"), @ViewDebug.IntToString(from = 12, to = "alignParentBottom"), @ViewDebug.IntToString(from = 9, to = "alignParentLeft"), @ViewDebug.IntToString(from = 11, to = "alignParentRight"), @ViewDebug.IntToString(from = 10, to = "alignParentTop"), @ViewDebug.IntToString(from = 7, to = "alignRight"), @ViewDebug.IntToString(from = 6, to = "alignTop"), @ViewDebug.IntToString(from = 3, to = "below"), @ViewDebug.IntToString(from = 14, to = "centerHorizontal"), @ViewDebug.IntToString(from = 13, to = "center"), @ViewDebug.IntToString(from = 15, to = "centerVertical"), @ViewDebug.IntToString(from = 0, to = "leftOf"), @ViewDebug.IntToString(from = 1, to = "rightOf"), @ViewDebug.IntToString(from = 18, to = "alignStart"), @ViewDebug.IntToString(from = 19, to = "alignEnd"), @ViewDebug.IntToString(from = 20, to = "alignParentStart"), @ViewDebug.IntToString(from = 21, to = "alignParentEnd"), @ViewDebug.IntToString(from = 16, to = "startOf"), @ViewDebug.IntToString(from = 17, to = "endOf")}, mapping = {@ViewDebug.IntToString(from = -1, to = "true"), @ViewDebug.IntToString(from = 0, to = "false/NO_ID")})
        private int[] mRules;
        private int[] mInitialRules;
        private int mLeft;
        private int mTop;
        private int mRight;
        private int mBottom;
        private int mStart;
        private int mEnd;
        private boolean mRulesChanged;
        private boolean mIsRtlCompatibilityMode;
        @ViewDebug.ExportedProperty(category = "layout")
        public boolean alignWithParent;

        static /* synthetic */ int access$112(LayoutParams x0, int x1) {
            int i = x0.mLeft + x1;
            x0.mLeft = i;
            return i;
        }

        static /* synthetic */ int access$212(LayoutParams x0, int x1) {
            int i = x0.mRight + x1;
            x0.mRight = i;
            return i;
        }

        static /* synthetic */ int access$412(LayoutParams x0, int x1) {
            int i = x0.mTop + x1;
            x0.mTop = i;
            return i;
        }

        static /* synthetic */ int access$312(LayoutParams x0, int x1) {
            int i = x0.mBottom + x1;
            x0.mBottom = i;
            return i;
        }

        static /* synthetic */ int access$120(LayoutParams x0, int x1) {
            int i = x0.mLeft - x1;
            x0.mLeft = i;
            return i;
        }

        static /* synthetic */ int access$220(LayoutParams x0, int x1) {
            int i = x0.mRight - x1;
            x0.mRight = i;
            return i;
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            this.mRules = new int[22];
            this.mInitialRules = new int[22];
            this.mStart = Integer.MIN_VALUE;
            this.mEnd = Integer.MIN_VALUE;
            this.mRulesChanged = false;
            this.mIsRtlCompatibilityMode = false;
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.RelativeLayout_Layout);
            int targetSdkVersion = c.getApplicationInfo().targetSdkVersion;
            this.mIsRtlCompatibilityMode = targetSdkVersion < 17 || !c.getApplicationInfo().hasRtlSupport();
            int[] rules = this.mRules;
            int[] initialRules = this.mInitialRules;
            int N = a.getIndexCount();
            for (int i = 0; i < N; i++) {
                int attr = a.getIndex(i);
                switch (attr) {
                    case 0:
                        rules[0] = a.getResourceId(attr, 0);
                        break;
                    case 1:
                        rules[1] = a.getResourceId(attr, 0);
                        break;
                    case 2:
                        rules[2] = a.getResourceId(attr, 0);
                        break;
                    case 3:
                        rules[3] = a.getResourceId(attr, 0);
                        break;
                    case 4:
                        rules[4] = a.getResourceId(attr, 0);
                        break;
                    case 5:
                        rules[5] = a.getResourceId(attr, 0);
                        break;
                    case 6:
                        rules[6] = a.getResourceId(attr, 0);
                        break;
                    case 7:
                        rules[7] = a.getResourceId(attr, 0);
                        break;
                    case 8:
                        rules[8] = a.getResourceId(attr, 0);
                        break;
                    case 9:
                        rules[9] = a.getBoolean(attr, false) ? -1 : 0;
                        break;
                    case 10:
                        rules[10] = a.getBoolean(attr, false) ? -1 : 0;
                        break;
                    case 11:
                        rules[11] = a.getBoolean(attr, false) ? -1 : 0;
                        break;
                    case 12:
                        rules[12] = a.getBoolean(attr, false) ? -1 : 0;
                        break;
                    case 13:
                        rules[13] = a.getBoolean(attr, false) ? -1 : 0;
                        break;
                    case 14:
                        rules[14] = a.getBoolean(attr, false) ? -1 : 0;
                        break;
                    case 15:
                        rules[15] = a.getBoolean(attr, false) ? -1 : 0;
                        break;
                    case 16:
                        this.alignWithParent = a.getBoolean(attr, false);
                        break;
                    case 17:
                        rules[16] = a.getResourceId(attr, 0);
                        break;
                    case 18:
                        rules[17] = a.getResourceId(attr, 0);
                        break;
                    case 19:
                        rules[18] = a.getResourceId(attr, 0);
                        break;
                    case 20:
                        rules[19] = a.getResourceId(attr, 0);
                        break;
                    case 21:
                        rules[20] = a.getBoolean(attr, false) ? -1 : 0;
                        break;
                    case 22:
                        rules[21] = a.getBoolean(attr, false) ? -1 : 0;
                        break;
                }
            }
            this.mRulesChanged = true;
            System.arraycopy(rules, 0, initialRules, 0, 22);
            a.recycle();
        }

        public LayoutParams(int w, int h) {
            super(w, h);
            this.mRules = new int[22];
            this.mInitialRules = new int[22];
            this.mStart = Integer.MIN_VALUE;
            this.mEnd = Integer.MIN_VALUE;
            this.mRulesChanged = false;
            this.mIsRtlCompatibilityMode = false;
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
            this.mRules = new int[22];
            this.mInitialRules = new int[22];
            this.mStart = Integer.MIN_VALUE;
            this.mEnd = Integer.MIN_VALUE;
            this.mRulesChanged = false;
            this.mIsRtlCompatibilityMode = false;
        }

        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
            this.mRules = new int[22];
            this.mInitialRules = new int[22];
            this.mStart = Integer.MIN_VALUE;
            this.mEnd = Integer.MIN_VALUE;
            this.mRulesChanged = false;
            this.mIsRtlCompatibilityMode = false;
        }

        public LayoutParams(LayoutParams source) {
            super((ViewGroup.MarginLayoutParams) source);
            this.mRules = new int[22];
            this.mInitialRules = new int[22];
            this.mStart = Integer.MIN_VALUE;
            this.mEnd = Integer.MIN_VALUE;
            this.mRulesChanged = false;
            this.mIsRtlCompatibilityMode = false;
            this.mIsRtlCompatibilityMode = source.mIsRtlCompatibilityMode;
            this.mRulesChanged = source.mRulesChanged;
            this.alignWithParent = source.alignWithParent;
            System.arraycopy(source.mRules, 0, this.mRules, 0, 22);
            System.arraycopy(source.mInitialRules, 0, this.mInitialRules, 0, 22);
        }

        @Override // android.view.ViewGroup.LayoutParams
        public String debug(String output) {
            return output + "ViewGroup.LayoutParams={ width=" + sizeToString(this.width) + ", height=" + sizeToString(this.height) + " }";
        }

        public void addRule(int verb) {
            this.mRules[verb] = -1;
            this.mInitialRules[verb] = -1;
            this.mRulesChanged = true;
        }

        public void addRule(int verb, int anchor) {
            this.mRules[verb] = anchor;
            this.mInitialRules[verb] = anchor;
            this.mRulesChanged = true;
        }

        public void removeRule(int verb) {
            this.mRules[verb] = 0;
            this.mInitialRules[verb] = 0;
            this.mRulesChanged = true;
        }

        private boolean hasRelativeRules() {
            return (this.mInitialRules[16] == 0 && this.mInitialRules[17] == 0 && this.mInitialRules[18] == 0 && this.mInitialRules[19] == 0 && this.mInitialRules[20] == 0 && this.mInitialRules[21] == 0) ? false : true;
        }

        private void resolveRules(int layoutDirection) {
            boolean isLayoutRtl = layoutDirection == 1;
            System.arraycopy(this.mInitialRules, 0, this.mRules, 0, 22);
            if (this.mIsRtlCompatibilityMode) {
                if (this.mRules[18] != 0) {
                    if (this.mRules[5] == 0) {
                        this.mRules[5] = this.mRules[18];
                    }
                    this.mRules[18] = 0;
                }
                if (this.mRules[19] != 0) {
                    if (this.mRules[7] == 0) {
                        this.mRules[7] = this.mRules[19];
                    }
                    this.mRules[19] = 0;
                }
                if (this.mRules[16] != 0) {
                    if (this.mRules[0] == 0) {
                        this.mRules[0] = this.mRules[16];
                    }
                    this.mRules[16] = 0;
                }
                if (this.mRules[17] != 0) {
                    if (this.mRules[1] == 0) {
                        this.mRules[1] = this.mRules[17];
                    }
                    this.mRules[17] = 0;
                }
                if (this.mRules[20] != 0) {
                    if (this.mRules[9] == 0) {
                        this.mRules[9] = this.mRules[20];
                    }
                    this.mRules[20] = 0;
                }
                if (this.mRules[11] == 0) {
                    if (this.mRules[11] == 0) {
                        this.mRules[11] = this.mRules[21];
                    }
                    this.mRules[21] = 0;
                }
            } else {
                if ((this.mRules[18] != 0 || this.mRules[19] != 0) && (this.mRules[5] != 0 || this.mRules[7] != 0)) {
                    this.mRules[5] = 0;
                    this.mRules[7] = 0;
                }
                if (this.mRules[18] != 0) {
                    this.mRules[isLayoutRtl ? (char) 7 : (char) 5] = this.mRules[18];
                    this.mRules[18] = 0;
                }
                if (this.mRules[19] != 0) {
                    this.mRules[isLayoutRtl ? (char) 5 : (char) 7] = this.mRules[19];
                    this.mRules[19] = 0;
                }
                if ((this.mRules[16] != 0 || this.mRules[17] != 0) && (this.mRules[0] != 0 || this.mRules[1] != 0)) {
                    this.mRules[0] = 0;
                    this.mRules[1] = 0;
                }
                if (this.mRules[16] != 0) {
                    this.mRules[isLayoutRtl ? (char) 1 : (char) 0] = this.mRules[16];
                    this.mRules[16] = 0;
                }
                if (this.mRules[17] != 0) {
                    this.mRules[isLayoutRtl ? (char) 0 : (char) 1] = this.mRules[17];
                    this.mRules[17] = 0;
                }
                if ((this.mRules[20] != 0 || this.mRules[21] != 0) && (this.mRules[9] != 0 || this.mRules[11] != 0)) {
                    this.mRules[9] = 0;
                    this.mRules[11] = 0;
                }
                if (this.mRules[20] != 0) {
                    this.mRules[isLayoutRtl ? (char) 11 : '\t'] = this.mRules[20];
                    this.mRules[20] = 0;
                }
                if (this.mRules[21] != 0) {
                    this.mRules[isLayoutRtl ? '\t' : (char) 11] = this.mRules[21];
                    this.mRules[21] = 0;
                }
            }
            this.mRulesChanged = false;
        }

        public int[] getRules(int layoutDirection) {
            if (hasRelativeRules() && (this.mRulesChanged || layoutDirection != getLayoutDirection())) {
                resolveRules(layoutDirection);
                if (layoutDirection != getLayoutDirection()) {
                    setLayoutDirection(layoutDirection);
                }
            }
            return this.mRules;
        }

        public int[] getRules() {
            return this.mRules;
        }

        @Override // android.view.ViewGroup.MarginLayoutParams, android.view.ViewGroup.LayoutParams
        public void resolveLayoutDirection(int layoutDirection) {
            boolean isLayoutRtl = isLayoutRtl();
            if (isLayoutRtl) {
                if (this.mStart != Integer.MIN_VALUE) {
                    this.mRight = this.mStart;
                }
                if (this.mEnd != Integer.MIN_VALUE) {
                    this.mLeft = this.mEnd;
                }
            } else {
                if (this.mStart != Integer.MIN_VALUE) {
                    this.mLeft = this.mStart;
                }
                if (this.mEnd != Integer.MIN_VALUE) {
                    this.mRight = this.mEnd;
                }
            }
            if (hasRelativeRules() && layoutDirection != getLayoutDirection()) {
                resolveRules(layoutDirection);
            }
            super.resolveLayoutDirection(layoutDirection);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: RelativeLayout$DependencyGraph.class */
    public static class DependencyGraph {
        private ArrayList<Node> mNodes;
        private SparseArray<Node> mKeyNodes;
        private ArrayDeque<Node> mRoots;

        private DependencyGraph() {
            this.mNodes = new ArrayList<>();
            this.mKeyNodes = new SparseArray<>();
            this.mRoots = new ArrayDeque<>();
        }

        void clear() {
            ArrayList<Node> nodes = this.mNodes;
            int count = nodes.size();
            for (int i = 0; i < count; i++) {
                nodes.get(i).release();
            }
            nodes.clear();
            this.mKeyNodes.clear();
            this.mRoots.clear();
        }

        void add(View view) {
            int id = view.getId();
            Node node = Node.acquire(view);
            if (id != -1) {
                this.mKeyNodes.put(id, node);
            }
            this.mNodes.add(node);
        }

        void getSortedViews(View[] sorted, int... rules) {
            ArrayDeque<Node> roots = findRoots(rules);
            int index = 0;
            while (true) {
                Node node = roots.pollLast();
                if (node == null) {
                    break;
                }
                View view = node.view;
                int key = view.getId();
                int i = index;
                index++;
                sorted[i] = view;
                ArrayMap<Node, DependencyGraph> dependents = node.dependents;
                int count = dependents.size();
                for (int i2 = 0; i2 < count; i2++) {
                    Node dependent = dependents.keyAt(i2);
                    SparseArray<Node> dependencies = dependent.dependencies;
                    dependencies.remove(key);
                    if (dependencies.size() == 0) {
                        roots.add(dependent);
                    }
                }
            }
            if (index < sorted.length) {
                throw new IllegalStateException("Circular dependencies cannot exist in RelativeLayout");
            }
        }

        private ArrayDeque<Node> findRoots(int[] rulesFilter) {
            Node dependency;
            SparseArray<Node> keyNodes = this.mKeyNodes;
            ArrayList<Node> nodes = this.mNodes;
            int count = nodes.size();
            for (int i = 0; i < count; i++) {
                Node node = nodes.get(i);
                node.dependents.clear();
                node.dependencies.clear();
            }
            for (int i2 = 0; i2 < count; i2++) {
                Node node2 = nodes.get(i2);
                LayoutParams layoutParams = (LayoutParams) node2.view.getLayoutParams();
                int[] rules = layoutParams.mRules;
                for (int i3 : rulesFilter) {
                    int rule = rules[i3];
                    if (rule > 0 && (dependency = keyNodes.get(rule)) != null && dependency != node2) {
                        dependency.dependents.put(node2, this);
                        node2.dependencies.put(rule, dependency);
                    }
                }
            }
            ArrayDeque<Node> roots = this.mRoots;
            roots.clear();
            for (int i4 = 0; i4 < count; i4++) {
                Node node3 = nodes.get(i4);
                if (node3.dependencies.size() == 0) {
                    roots.addLast(node3);
                }
            }
            return roots;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: RelativeLayout$DependencyGraph$Node.class */
        public static class Node {
            View view;
            final ArrayMap<Node, DependencyGraph> dependents = new ArrayMap<>();
            final SparseArray<Node> dependencies = new SparseArray<>();
            private static final int POOL_LIMIT = 100;
            private static final Pools.SynchronizedPool<Node> sPool = new Pools.SynchronizedPool<>(100);

            Node() {
            }

            static Node acquire(View view) {
                Node node = sPool.acquire();
                if (node == null) {
                    node = new Node();
                }
                node.view = view;
                return node;
            }

            void release() {
                this.view = null;
                this.dependents.clear();
                this.dependencies.clear();
                sPool.release(this);
            }
        }
    }
}