package android.widget;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Trace;
import android.util.AttributeSet;
import android.util.MathUtils;
import android.util.SparseBooleanArray;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.RemotableViewMethod;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewRootImpl;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.AbsListView;
import android.widget.RemoteViews;
import com.android.internal.R;
import com.android.internal.util.Predicate;
import com.google.android.collect.Lists;
import java.util.ArrayList;

@RemoteViews.RemoteView
/* loaded from: ListView.class */
public class ListView extends AbsListView {
    static final int NO_POSITION = -1;
    private static final float MAX_SCROLL_FACTOR = 0.33f;
    private static final int MIN_SCROLL_PREVIEW_PIXELS = 2;
    private ArrayList<FixedViewInfo> mHeaderViewInfos;
    private ArrayList<FixedViewInfo> mFooterViewInfos;
    Drawable mDivider;
    int mDividerHeight;
    Drawable mOverScrollHeader;
    Drawable mOverScrollFooter;
    private boolean mIsCacheColorOpaque;
    private boolean mDividerIsOpaque;
    private boolean mHeaderDividersEnabled;
    private boolean mFooterDividersEnabled;
    private boolean mAreAllItemsSelectable;
    private boolean mItemsCanFocus;
    private final Rect mTempRect;
    private Paint mDividerPaint;
    private final ArrowScrollFocusResult mArrowScrollFocusResult;
    private FocusSelector mFocusSelector;

    /* loaded from: ListView$FixedViewInfo.class */
    public class FixedViewInfo {
        public View view;
        public Object data;
        public boolean isSelectable;

        public FixedViewInfo() {
        }
    }

    public ListView(Context context) {
        this(context, null);
    }

    public ListView(Context context, AttributeSet attrs) {
        this(context, attrs, 16842868);
    }

    public ListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mHeaderViewInfos = Lists.newArrayList();
        this.mFooterViewInfos = Lists.newArrayList();
        this.mAreAllItemsSelectable = true;
        this.mItemsCanFocus = false;
        this.mTempRect = new Rect();
        this.mArrowScrollFocusResult = new ArrowScrollFocusResult();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ListView, defStyle, 0);
        CharSequence[] entries = a.getTextArray(0);
        if (entries != null) {
            setAdapter((ListAdapter) new ArrayAdapter(context, 17367043, entries));
        }
        Drawable d = a.getDrawable(1);
        if (d != null) {
            setDivider(d);
        }
        Drawable osHeader = a.getDrawable(5);
        if (osHeader != null) {
            setOverscrollHeader(osHeader);
        }
        Drawable osFooter = a.getDrawable(6);
        if (osFooter != null) {
            setOverscrollFooter(osFooter);
        }
        int dividerHeight = a.getDimensionPixelSize(2, 0);
        if (dividerHeight != 0) {
            setDividerHeight(dividerHeight);
        }
        this.mHeaderDividersEnabled = a.getBoolean(3, true);
        this.mFooterDividersEnabled = a.getBoolean(4, true);
        a.recycle();
    }

    public int getMaxScrollAmount() {
        return (int) (MAX_SCROLL_FACTOR * (this.mBottom - this.mTop));
    }

    private void adjustViewsUpOrDown() {
        int delta;
        int childCount = getChildCount();
        if (childCount > 0) {
            if (!this.mStackFromBottom) {
                View child = getChildAt(0);
                delta = child.getTop() - this.mListPadding.top;
                if (this.mFirstPosition != 0) {
                    delta -= this.mDividerHeight;
                }
                if (delta < 0) {
                    delta = 0;
                }
            } else {
                View child2 = getChildAt(childCount - 1);
                delta = child2.getBottom() - (getHeight() - this.mListPadding.bottom);
                if (this.mFirstPosition + childCount < this.mItemCount) {
                    delta += this.mDividerHeight;
                }
                if (delta > 0) {
                    delta = 0;
                }
            }
            if (delta != 0) {
                offsetChildrenTopAndBottom(-delta);
            }
        }
    }

    public void addHeaderView(View v, Object data, boolean isSelectable) {
        FixedViewInfo info = new FixedViewInfo();
        info.view = v;
        info.data = data;
        info.isSelectable = isSelectable;
        this.mHeaderViewInfos.add(info);
        if (this.mAdapter != null) {
            if (!(this.mAdapter instanceof HeaderViewListAdapter)) {
                this.mAdapter = new HeaderViewListAdapter(this.mHeaderViewInfos, this.mFooterViewInfos, this.mAdapter);
            }
            if (this.mDataSetObserver != null) {
                this.mDataSetObserver.onChanged();
            }
        }
    }

    public void addHeaderView(View v) {
        addHeaderView(v, null, true);
    }

    @Override // android.widget.AbsListView
    public int getHeaderViewsCount() {
        return this.mHeaderViewInfos.size();
    }

    public boolean removeHeaderView(View v) {
        if (this.mHeaderViewInfos.size() > 0) {
            boolean result = false;
            if (this.mAdapter != null && ((HeaderViewListAdapter) this.mAdapter).removeHeader(v)) {
                if (this.mDataSetObserver != null) {
                    this.mDataSetObserver.onChanged();
                }
                result = true;
            }
            removeFixedViewInfo(v, this.mHeaderViewInfos);
            return result;
        }
        return false;
    }

    private void removeFixedViewInfo(View v, ArrayList<FixedViewInfo> where) {
        int len = where.size();
        for (int i = 0; i < len; i++) {
            FixedViewInfo info = where.get(i);
            if (info.view == v) {
                where.remove(i);
                return;
            }
        }
    }

    public void addFooterView(View v, Object data, boolean isSelectable) {
        FixedViewInfo info = new FixedViewInfo();
        info.view = v;
        info.data = data;
        info.isSelectable = isSelectable;
        this.mFooterViewInfos.add(info);
        if (this.mAdapter != null) {
            if (!(this.mAdapter instanceof HeaderViewListAdapter)) {
                this.mAdapter = new HeaderViewListAdapter(this.mHeaderViewInfos, this.mFooterViewInfos, this.mAdapter);
            }
            if (this.mDataSetObserver != null) {
                this.mDataSetObserver.onChanged();
            }
        }
    }

    public void addFooterView(View v) {
        addFooterView(v, null, true);
    }

    @Override // android.widget.AbsListView
    public int getFooterViewsCount() {
        return this.mFooterViewInfos.size();
    }

    public boolean removeFooterView(View v) {
        if (this.mFooterViewInfos.size() > 0) {
            boolean result = false;
            if (this.mAdapter != null && ((HeaderViewListAdapter) this.mAdapter).removeFooter(v)) {
                if (this.mDataSetObserver != null) {
                    this.mDataSetObserver.onChanged();
                }
                result = true;
            }
            removeFixedViewInfo(v, this.mFooterViewInfos);
            return result;
        }
        return false;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // android.widget.AdapterView
    public ListAdapter getAdapter() {
        return this.mAdapter;
    }

    @Override // android.widget.AbsListView
    @RemotableViewMethod
    public void setRemoteViewsAdapter(Intent intent) {
        super.setRemoteViewsAdapter(intent);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // android.widget.AbsListView, android.widget.AdapterView
    public void setAdapter(ListAdapter adapter) {
        int position;
        if (this.mAdapter != null && this.mDataSetObserver != null) {
            this.mAdapter.unregisterDataSetObserver(this.mDataSetObserver);
        }
        resetList();
        this.mRecycler.clear();
        if (this.mHeaderViewInfos.size() > 0 || this.mFooterViewInfos.size() > 0) {
            this.mAdapter = new HeaderViewListAdapter(this.mHeaderViewInfos, this.mFooterViewInfos, adapter);
        } else {
            this.mAdapter = adapter;
        }
        this.mOldSelectedPosition = -1;
        this.mOldSelectedRowId = Long.MIN_VALUE;
        super.setAdapter(adapter);
        if (this.mAdapter != null) {
            this.mAreAllItemsSelectable = this.mAdapter.areAllItemsEnabled();
            this.mOldItemCount = this.mItemCount;
            this.mItemCount = this.mAdapter.getCount();
            checkFocus();
            this.mDataSetObserver = new AbsListView.AdapterDataSetObserver();
            this.mAdapter.registerDataSetObserver(this.mDataSetObserver);
            this.mRecycler.setViewTypeCount(this.mAdapter.getViewTypeCount());
            if (this.mStackFromBottom) {
                position = lookForSelectablePosition(this.mItemCount - 1, false);
            } else {
                position = lookForSelectablePosition(0, true);
            }
            setSelectedPositionInt(position);
            setNextSelectedPositionInt(position);
            if (this.mItemCount == 0) {
                checkSelectionChanged();
            }
        } else {
            this.mAreAllItemsSelectable = true;
            checkFocus();
            checkSelectionChanged();
        }
        requestLayout();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.widget.AbsListView
    public void resetList() {
        clearRecycledState(this.mHeaderViewInfos);
        clearRecycledState(this.mFooterViewInfos);
        super.resetList();
        this.mLayoutMode = 0;
    }

    private void clearRecycledState(ArrayList<FixedViewInfo> infos) {
        if (infos != null) {
            int count = infos.size();
            for (int i = 0; i < count; i++) {
                View child = infos.get(i).view;
                AbsListView.LayoutParams p = (AbsListView.LayoutParams) child.getLayoutParams();
                if (p != null) {
                    p.recycledHeaderFooter = false;
                }
            }
        }
    }

    private boolean showingTopFadingEdge() {
        int listTop = this.mScrollY + this.mListPadding.top;
        return this.mFirstPosition > 0 || getChildAt(0).getTop() > listTop;
    }

    private boolean showingBottomFadingEdge() {
        int childCount = getChildCount();
        int bottomOfBottomChild = getChildAt(childCount - 1).getBottom();
        int lastVisiblePosition = (this.mFirstPosition + childCount) - 1;
        int listBottom = (this.mScrollY + getHeight()) - this.mListPadding.bottom;
        return lastVisiblePosition < this.mItemCount - 1 || bottomOfBottomChild < listBottom;
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public boolean requestChildRectangleOnScreen(View child, Rect rect, boolean immediate) {
        int scrollYDelta;
        int scrollYDelta2;
        int rectTopWithinChild = rect.top;
        rect.offset(child.getLeft(), child.getTop());
        rect.offset(-child.getScrollX(), -child.getScrollY());
        int height = getHeight();
        int listUnfadedTop = getScrollY();
        int listUnfadedBottom = listUnfadedTop + height;
        int fadingEdge = getVerticalFadingEdgeLength();
        if (showingTopFadingEdge() && (this.mSelectedPosition > 0 || rectTopWithinChild > fadingEdge)) {
            listUnfadedTop += fadingEdge;
        }
        int childCount = getChildCount();
        int bottomOfBottomChild = getChildAt(childCount - 1).getBottom();
        if (showingBottomFadingEdge() && (this.mSelectedPosition < this.mItemCount - 1 || rect.bottom < bottomOfBottomChild - fadingEdge)) {
            listUnfadedBottom -= fadingEdge;
        }
        int scrollYDelta3 = 0;
        if (rect.bottom > listUnfadedBottom && rect.top > listUnfadedTop) {
            if (rect.height() > height) {
                scrollYDelta2 = 0 + (rect.top - listUnfadedTop);
            } else {
                scrollYDelta2 = 0 + (rect.bottom - listUnfadedBottom);
            }
            int distanceToBottom = bottomOfBottomChild - listUnfadedBottom;
            scrollYDelta3 = Math.min(scrollYDelta2, distanceToBottom);
        } else if (rect.top < listUnfadedTop && rect.bottom < listUnfadedBottom) {
            if (rect.height() > height) {
                scrollYDelta = 0 - (listUnfadedBottom - rect.bottom);
            } else {
                scrollYDelta = 0 - (listUnfadedTop - rect.top);
            }
            int top = getChildAt(0).getTop();
            int deltaToTop = top - listUnfadedTop;
            scrollYDelta3 = Math.max(scrollYDelta, deltaToTop);
        }
        boolean scroll = scrollYDelta3 != 0;
        if (scroll) {
            scrollListItemsBy(-scrollYDelta3);
            positionSelector(-1, child);
            this.mSelectedTop = child.getTop();
            invalidate();
        }
        return scroll;
    }

    @Override // android.widget.AbsListView
    void fillGap(boolean down) {
        int count = getChildCount();
        if (down) {
            int paddingTop = 0;
            if ((this.mGroupFlags & 34) == 34) {
                paddingTop = getListPaddingTop();
            }
            int startOffset = count > 0 ? getChildAt(count - 1).getBottom() + this.mDividerHeight : paddingTop;
            fillDown(this.mFirstPosition + count, startOffset);
            correctTooHigh(getChildCount());
            return;
        }
        int paddingBottom = 0;
        if ((this.mGroupFlags & 34) == 34) {
            paddingBottom = getListPaddingBottom();
        }
        int startOffset2 = count > 0 ? getChildAt(0).getTop() - this.mDividerHeight : getHeight() - paddingBottom;
        fillUp(this.mFirstPosition - 1, startOffset2);
        correctTooLow(getChildCount());
    }

    private View fillDown(int pos, int nextTop) {
        View selectedView = null;
        int end = this.mBottom - this.mTop;
        if ((this.mGroupFlags & 34) == 34) {
            end -= this.mListPadding.bottom;
        }
        while (nextTop < end && pos < this.mItemCount) {
            boolean selected = pos == this.mSelectedPosition;
            View child = makeAndAddView(pos, nextTop, true, this.mListPadding.left, selected);
            nextTop = child.getBottom() + this.mDividerHeight;
            if (selected) {
                selectedView = child;
            }
            pos++;
        }
        setVisibleRangeHint(this.mFirstPosition, (this.mFirstPosition + getChildCount()) - 1);
        return selectedView;
    }

    private View fillUp(int pos, int nextBottom) {
        View selectedView = null;
        int end = 0;
        if ((this.mGroupFlags & 34) == 34) {
            end = this.mListPadding.top;
        }
        while (nextBottom > end && pos >= 0) {
            boolean selected = pos == this.mSelectedPosition;
            View child = makeAndAddView(pos, nextBottom, false, this.mListPadding.left, selected);
            nextBottom = child.getTop() - this.mDividerHeight;
            if (selected) {
                selectedView = child;
            }
            pos--;
        }
        this.mFirstPosition = pos + 1;
        setVisibleRangeHint(this.mFirstPosition, (this.mFirstPosition + getChildCount()) - 1);
        return selectedView;
    }

    private View fillFromTop(int nextTop) {
        this.mFirstPosition = Math.min(this.mFirstPosition, this.mSelectedPosition);
        this.mFirstPosition = Math.min(this.mFirstPosition, this.mItemCount - 1);
        if (this.mFirstPosition < 0) {
            this.mFirstPosition = 0;
        }
        return fillDown(this.mFirstPosition, nextTop);
    }

    private View fillFromMiddle(int childrenTop, int childrenBottom) {
        int height = childrenBottom - childrenTop;
        int position = reconcileSelectedPosition();
        View sel = makeAndAddView(position, childrenTop, true, this.mListPadding.left, true);
        this.mFirstPosition = position;
        int selHeight = sel.getMeasuredHeight();
        if (selHeight <= height) {
            sel.offsetTopAndBottom((height - selHeight) / 2);
        }
        fillAboveAndBelow(sel, position);
        if (!this.mStackFromBottom) {
            correctTooHigh(getChildCount());
        } else {
            correctTooLow(getChildCount());
        }
        return sel;
    }

    private void fillAboveAndBelow(View sel, int position) {
        int dividerHeight = this.mDividerHeight;
        if (!this.mStackFromBottom) {
            fillUp(position - 1, sel.getTop() - dividerHeight);
            adjustViewsUpOrDown();
            fillDown(position + 1, sel.getBottom() + dividerHeight);
            return;
        }
        fillDown(position + 1, sel.getBottom() + dividerHeight);
        adjustViewsUpOrDown();
        fillUp(position - 1, sel.getTop() - dividerHeight);
    }

    private View fillFromSelection(int selectedTop, int childrenTop, int childrenBottom) {
        int fadingEdgeLength = getVerticalFadingEdgeLength();
        int selectedPosition = this.mSelectedPosition;
        int topSelectionPixel = getTopSelectionPixel(childrenTop, fadingEdgeLength, selectedPosition);
        int bottomSelectionPixel = getBottomSelectionPixel(childrenBottom, fadingEdgeLength, selectedPosition);
        View sel = makeAndAddView(selectedPosition, selectedTop, true, this.mListPadding.left, true);
        if (sel.getBottom() > bottomSelectionPixel) {
            int spaceAbove = sel.getTop() - topSelectionPixel;
            int spaceBelow = sel.getBottom() - bottomSelectionPixel;
            int offset = Math.min(spaceAbove, spaceBelow);
            sel.offsetTopAndBottom(-offset);
        } else if (sel.getTop() < topSelectionPixel) {
            int spaceAbove2 = topSelectionPixel - sel.getTop();
            int spaceBelow2 = bottomSelectionPixel - sel.getBottom();
            int offset2 = Math.min(spaceAbove2, spaceBelow2);
            sel.offsetTopAndBottom(offset2);
        }
        fillAboveAndBelow(sel, selectedPosition);
        if (!this.mStackFromBottom) {
            correctTooHigh(getChildCount());
        } else {
            correctTooLow(getChildCount());
        }
        return sel;
    }

    private int getBottomSelectionPixel(int childrenBottom, int fadingEdgeLength, int selectedPosition) {
        int bottomSelectionPixel = childrenBottom;
        if (selectedPosition != this.mItemCount - 1) {
            bottomSelectionPixel -= fadingEdgeLength;
        }
        return bottomSelectionPixel;
    }

    private int getTopSelectionPixel(int childrenTop, int fadingEdgeLength, int selectedPosition) {
        int topSelectionPixel = childrenTop;
        if (selectedPosition > 0) {
            topSelectionPixel += fadingEdgeLength;
        }
        return topSelectionPixel;
    }

    @Override // android.widget.AbsListView
    @RemotableViewMethod
    public void smoothScrollToPosition(int position) {
        super.smoothScrollToPosition(position);
    }

    @Override // android.widget.AbsListView
    @RemotableViewMethod
    public void smoothScrollByOffset(int offset) {
        super.smoothScrollByOffset(offset);
    }

    private View moveSelection(View oldSel, View newSel, int delta, int childrenTop, int childrenBottom) {
        View sel;
        int fadingEdgeLength = getVerticalFadingEdgeLength();
        int selectedPosition = this.mSelectedPosition;
        int topSelectionPixel = getTopSelectionPixel(childrenTop, fadingEdgeLength, selectedPosition);
        int bottomSelectionPixel = getBottomSelectionPixel(childrenTop, fadingEdgeLength, selectedPosition);
        if (delta > 0) {
            View oldSel2 = makeAndAddView(selectedPosition - 1, oldSel.getTop(), true, this.mListPadding.left, false);
            int dividerHeight = this.mDividerHeight;
            sel = makeAndAddView(selectedPosition, oldSel2.getBottom() + dividerHeight, true, this.mListPadding.left, true);
            if (sel.getBottom() > bottomSelectionPixel) {
                int spaceAbove = sel.getTop() - topSelectionPixel;
                int spaceBelow = sel.getBottom() - bottomSelectionPixel;
                int halfVerticalSpace = (childrenBottom - childrenTop) / 2;
                int offset = Math.min(spaceAbove, spaceBelow);
                int offset2 = Math.min(offset, halfVerticalSpace);
                oldSel2.offsetTopAndBottom(-offset2);
                sel.offsetTopAndBottom(-offset2);
            }
            if (!this.mStackFromBottom) {
                fillUp(this.mSelectedPosition - 2, sel.getTop() - dividerHeight);
                adjustViewsUpOrDown();
                fillDown(this.mSelectedPosition + 1, sel.getBottom() + dividerHeight);
            } else {
                fillDown(this.mSelectedPosition + 1, sel.getBottom() + dividerHeight);
                adjustViewsUpOrDown();
                fillUp(this.mSelectedPosition - 2, sel.getTop() - dividerHeight);
            }
        } else if (delta < 0) {
            if (newSel != null) {
                sel = makeAndAddView(selectedPosition, newSel.getTop(), true, this.mListPadding.left, true);
            } else {
                sel = makeAndAddView(selectedPosition, oldSel.getTop(), false, this.mListPadding.left, true);
            }
            if (sel.getTop() < topSelectionPixel) {
                int spaceAbove2 = topSelectionPixel - sel.getTop();
                int spaceBelow2 = bottomSelectionPixel - sel.getBottom();
                int halfVerticalSpace2 = (childrenBottom - childrenTop) / 2;
                int offset3 = Math.min(spaceAbove2, spaceBelow2);
                sel.offsetTopAndBottom(Math.min(offset3, halfVerticalSpace2));
            }
            fillAboveAndBelow(sel, selectedPosition);
        } else {
            int oldTop = oldSel.getTop();
            sel = makeAndAddView(selectedPosition, oldTop, true, this.mListPadding.left, true);
            if (oldTop < childrenTop) {
                int newBottom = sel.getBottom();
                if (newBottom < childrenTop + 20) {
                    sel.offsetTopAndBottom(childrenTop - sel.getTop());
                }
            }
            fillAboveAndBelow(sel, selectedPosition);
        }
        return sel;
    }

    /* loaded from: ListView$FocusSelector.class */
    private class FocusSelector implements Runnable {
        private int mPosition;
        private int mPositionTop;

        private FocusSelector() {
        }

        public FocusSelector setup(int position, int top) {
            this.mPosition = position;
            this.mPositionTop = top;
            return this;
        }

        @Override // java.lang.Runnable
        public void run() {
            ListView.this.setSelectionFromTop(this.mPosition, this.mPositionTop);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.AbsListView, android.view.View
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        View focusedChild;
        if (getChildCount() > 0 && (focusedChild = getFocusedChild()) != null) {
            int childPosition = this.mFirstPosition + indexOfChild(focusedChild);
            int childBottom = focusedChild.getBottom();
            int offset = Math.max(0, childBottom - (h - this.mPaddingTop));
            int top = focusedChild.getTop() - offset;
            if (this.mFocusSelector == null) {
                this.mFocusSelector = new FocusSelector();
            }
            post(this.mFocusSelector.setup(childPosition, top));
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.AbsListView, android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int widthSize2 = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        int childWidth = 0;
        int childHeight = 0;
        int childState = 0;
        this.mItemCount = this.mAdapter == null ? 0 : this.mAdapter.getCount();
        if (this.mItemCount > 0 && (widthMode == 0 || heightMode == 0)) {
            View child = obtainView(0, this.mIsScrap);
            measureScrapChild(child, 0, widthMeasureSpec);
            childWidth = child.getMeasuredWidth();
            childHeight = child.getMeasuredHeight();
            childState = combineMeasuredStates(0, child.getMeasuredState());
            if (recycleOnMeasure() && this.mRecycler.shouldRecycleViewType(((AbsListView.LayoutParams) child.getLayoutParams()).viewType)) {
                this.mRecycler.addScrapView(child, -1);
            }
        }
        if (widthMode == 0) {
            widthSize = this.mListPadding.left + this.mListPadding.right + childWidth + getVerticalScrollbarWidth();
        } else {
            widthSize = widthSize2 | (childState & (-16777216));
        }
        if (heightMode == 0) {
            heightSize = this.mListPadding.top + this.mListPadding.bottom + childHeight + (getVerticalFadingEdgeLength() * 2);
        }
        if (heightMode == Integer.MIN_VALUE) {
            heightSize = measureHeightOfChildren(widthMeasureSpec, 0, -1, heightSize, -1);
        }
        setMeasuredDimension(widthSize, heightSize);
        this.mWidthMeasureSpec = widthMeasureSpec;
    }

    private void measureScrapChild(View child, int position, int widthMeasureSpec) {
        int childHeightSpec;
        AbsListView.LayoutParams p = (AbsListView.LayoutParams) child.getLayoutParams();
        if (p == null) {
            p = (AbsListView.LayoutParams) generateDefaultLayoutParams();
            child.setLayoutParams(p);
        }
        p.viewType = this.mAdapter.getItemViewType(position);
        p.forceAdd = true;
        int childWidthSpec = ViewGroup.getChildMeasureSpec(widthMeasureSpec, this.mListPadding.left + this.mListPadding.right, p.width);
        int lpHeight = p.height;
        if (lpHeight > 0) {
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(lpHeight, 1073741824);
        } else {
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    @ViewDebug.ExportedProperty(category = "list")
    protected boolean recycleOnMeasure() {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int measureHeightOfChildren(int widthMeasureSpec, int startPosition, int endPosition, int maxHeight, int disallowPartialChildPosition) {
        ListAdapter adapter = this.mAdapter;
        if (adapter == null) {
            return this.mListPadding.top + this.mListPadding.bottom;
        }
        int returnedHeight = this.mListPadding.top + this.mListPadding.bottom;
        int dividerHeight = (this.mDividerHeight <= 0 || this.mDivider == null) ? 0 : this.mDividerHeight;
        int prevHeightWithoutPartialChild = 0;
        int endPosition2 = endPosition == -1 ? adapter.getCount() - 1 : endPosition;
        AbsListView.RecycleBin recycleBin = this.mRecycler;
        boolean recyle = recycleOnMeasure();
        boolean[] isScrap = this.mIsScrap;
        int i = startPosition;
        while (i <= endPosition2) {
            View child = obtainView(i, isScrap);
            measureScrapChild(child, i, widthMeasureSpec);
            if (i > 0) {
                returnedHeight += dividerHeight;
            }
            if (recyle && recycleBin.shouldRecycleViewType(((AbsListView.LayoutParams) child.getLayoutParams()).viewType)) {
                recycleBin.addScrapView(child, -1);
            }
            returnedHeight += child.getMeasuredHeight();
            if (returnedHeight >= maxHeight) {
                return (disallowPartialChildPosition < 0 || i <= disallowPartialChildPosition || prevHeightWithoutPartialChild <= 0 || returnedHeight == maxHeight) ? maxHeight : prevHeightWithoutPartialChild;
            }
            if (disallowPartialChildPosition >= 0 && i >= disallowPartialChildPosition) {
                prevHeightWithoutPartialChild = returnedHeight;
            }
            i++;
        }
        return returnedHeight;
    }

    @Override // android.widget.AbsListView
    int findMotionRow(int y) {
        int childCount = getChildCount();
        if (childCount > 0) {
            if (!this.mStackFromBottom) {
                for (int i = 0; i < childCount; i++) {
                    View v = getChildAt(i);
                    if (y <= v.getBottom()) {
                        return this.mFirstPosition + i;
                    }
                }
                return -1;
            }
            for (int i2 = childCount - 1; i2 >= 0; i2--) {
                View v2 = getChildAt(i2);
                if (y >= v2.getTop()) {
                    return this.mFirstPosition + i2;
                }
            }
            return -1;
        }
        return -1;
    }

    private View fillSpecific(int position, int top) {
        View below;
        View above;
        boolean tempIsSelected = position == this.mSelectedPosition;
        View temp = makeAndAddView(position, top, true, this.mListPadding.left, tempIsSelected);
        this.mFirstPosition = position;
        int dividerHeight = this.mDividerHeight;
        if (!this.mStackFromBottom) {
            above = fillUp(position - 1, temp.getTop() - dividerHeight);
            adjustViewsUpOrDown();
            below = fillDown(position + 1, temp.getBottom() + dividerHeight);
            int childCount = getChildCount();
            if (childCount > 0) {
                correctTooHigh(childCount);
            }
        } else {
            below = fillDown(position + 1, temp.getBottom() + dividerHeight);
            adjustViewsUpOrDown();
            above = fillUp(position - 1, temp.getTop() - dividerHeight);
            int childCount2 = getChildCount();
            if (childCount2 > 0) {
                correctTooLow(childCount2);
            }
        }
        if (tempIsSelected) {
            return temp;
        }
        if (above != null) {
            return above;
        }
        return below;
    }

    private void correctTooHigh(int childCount) {
        int lastPosition = (this.mFirstPosition + childCount) - 1;
        if (lastPosition == this.mItemCount - 1 && childCount > 0) {
            View lastChild = getChildAt(childCount - 1);
            int lastBottom = lastChild.getBottom();
            int end = (this.mBottom - this.mTop) - this.mListPadding.bottom;
            int bottomOffset = end - lastBottom;
            View firstChild = getChildAt(0);
            int firstTop = firstChild.getTop();
            if (bottomOffset > 0) {
                if (this.mFirstPosition > 0 || firstTop < this.mListPadding.top) {
                    if (this.mFirstPosition == 0) {
                        bottomOffset = Math.min(bottomOffset, this.mListPadding.top - firstTop);
                    }
                    offsetChildrenTopAndBottom(bottomOffset);
                    if (this.mFirstPosition > 0) {
                        fillUp(this.mFirstPosition - 1, firstChild.getTop() - this.mDividerHeight);
                        adjustViewsUpOrDown();
                    }
                }
            }
        }
    }

    private void correctTooLow(int childCount) {
        if (this.mFirstPosition == 0 && childCount > 0) {
            View firstChild = getChildAt(0);
            int firstTop = firstChild.getTop();
            int start = this.mListPadding.top;
            int end = (this.mBottom - this.mTop) - this.mListPadding.bottom;
            int topOffset = firstTop - start;
            View lastChild = getChildAt(childCount - 1);
            int lastBottom = lastChild.getBottom();
            int lastPosition = (this.mFirstPosition + childCount) - 1;
            if (topOffset > 0) {
                if (lastPosition < this.mItemCount - 1 || lastBottom > end) {
                    if (lastPosition == this.mItemCount - 1) {
                        topOffset = Math.min(topOffset, lastBottom - end);
                    }
                    offsetChildrenTopAndBottom(-topOffset);
                    if (lastPosition < this.mItemCount - 1) {
                        fillDown(lastPosition + 1, lastChild.getBottom() + this.mDividerHeight);
                        adjustViewsUpOrDown();
                    }
                } else if (lastPosition == this.mItemCount - 1) {
                    adjustViewsUpOrDown();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.AbsListView
    public void layoutChildren() {
        int accessibilityFocusPosition;
        View sel;
        boolean blockLayoutRequests = this.mBlockLayoutRequests;
        if (blockLayoutRequests) {
            return;
        }
        this.mBlockLayoutRequests = true;
        try {
            super.layoutChildren();
            invalidate();
            if (this.mAdapter == null) {
                resetList();
                invokeOnItemScrollListener();
                if (blockLayoutRequests) {
                    return;
                }
                this.mBlockLayoutRequests = false;
                return;
            }
            int childrenTop = this.mListPadding.top;
            int childrenBottom = (this.mBottom - this.mTop) - this.mListPadding.bottom;
            int childCount = getChildCount();
            View oldSel = null;
            View oldFirst = null;
            View newSel = null;
            switch (this.mLayoutMode) {
                case 1:
                case 3:
                case 4:
                case 5:
                    break;
                case 2:
                    int index = this.mNextSelectedPosition - this.mFirstPosition;
                    if (index >= 0 && index < childCount) {
                        newSel = getChildAt(index);
                        break;
                    }
                    break;
                case 6:
                default:
                    int index2 = this.mSelectedPosition - this.mFirstPosition;
                    if (index2 >= 0 && index2 < childCount) {
                        oldSel = getChildAt(index2);
                    }
                    oldFirst = getChildAt(0);
                    delta = this.mNextSelectedPosition >= 0 ? this.mNextSelectedPosition - this.mSelectedPosition : 0;
                    newSel = getChildAt(index2 + delta);
                    break;
            }
            boolean dataChanged = this.mDataChanged;
            if (dataChanged) {
                handleDataChanged();
            }
            if (this.mItemCount == 0) {
                resetList();
                invokeOnItemScrollListener();
                if (blockLayoutRequests) {
                    return;
                }
                this.mBlockLayoutRequests = false;
            } else if (this.mItemCount != this.mAdapter.getCount()) {
                throw new IllegalStateException("The content of the adapter has changed but ListView did not receive a notification. Make sure the content of your adapter is not modified from a background thread, but only from the UI thread. Make sure your adapter calls notifyDataSetChanged() when its content changes. [in ListView(" + getId() + ", " + getClass() + ") with Adapter(" + this.mAdapter.getClass() + ")]");
            } else {
                setSelectedPositionInt(this.mNextSelectedPosition);
                View accessFocusedChild = getAccessibilityFocusedChild();
                if (accessFocusedChild != null) {
                    accessibilityFocusPosition = getPositionForView(accessFocusedChild);
                    accessFocusedChild.setHasTransientState(true);
                } else {
                    accessibilityFocusPosition = -1;
                }
                View focusedChild = getFocusedChild();
                if (focusedChild != null) {
                    focusedChild.setHasTransientState(true);
                }
                int firstPosition = this.mFirstPosition;
                AbsListView.RecycleBin recycleBin = this.mRecycler;
                if (dataChanged) {
                    for (int i = 0; i < childCount; i++) {
                        recycleBin.addScrapView(getChildAt(i), firstPosition + i);
                    }
                } else {
                    recycleBin.fillActiveViews(childCount, firstPosition);
                }
                detachAllViewsFromParent();
                recycleBin.removeSkippedScrap();
                switch (this.mLayoutMode) {
                    case 1:
                        this.mFirstPosition = 0;
                        sel = fillFromTop(childrenTop);
                        adjustViewsUpOrDown();
                        break;
                    case 2:
                        if (newSel != null) {
                            sel = fillFromSelection(newSel.getTop(), childrenTop, childrenBottom);
                            break;
                        } else {
                            sel = fillFromMiddle(childrenTop, childrenBottom);
                            break;
                        }
                    case 3:
                        sel = fillUp(this.mItemCount - 1, childrenBottom);
                        adjustViewsUpOrDown();
                        break;
                    case 4:
                        sel = fillSpecific(reconcileSelectedPosition(), this.mSpecificTop);
                        break;
                    case 5:
                        sel = fillSpecific(this.mSyncPosition, this.mSpecificTop);
                        break;
                    case 6:
                        sel = moveSelection(oldSel, newSel, delta, childrenTop, childrenBottom);
                        break;
                    default:
                        if (childCount == 0) {
                            if (this.mStackFromBottom) {
                                int position = lookForSelectablePosition(this.mItemCount - 1, false);
                                setSelectedPositionInt(position);
                                sel = fillUp(this.mItemCount - 1, childrenBottom);
                                break;
                            } else {
                                int position2 = lookForSelectablePosition(0, true);
                                setSelectedPositionInt(position2);
                                sel = fillFromTop(childrenTop);
                                break;
                            }
                        } else if (this.mSelectedPosition < 0 || this.mSelectedPosition >= this.mItemCount) {
                            if (this.mFirstPosition < this.mItemCount) {
                                sel = fillSpecific(this.mFirstPosition, oldFirst == null ? childrenTop : oldFirst.getTop());
                                break;
                            } else {
                                sel = fillSpecific(0, childrenTop);
                                break;
                            }
                        } else {
                            sel = fillSpecific(this.mSelectedPosition, oldSel == null ? childrenTop : oldSel.getTop());
                            break;
                        }
                        break;
                }
                recycleBin.scrapActiveViews();
                if (sel != null) {
                    boolean shouldPlaceFocus = this.mItemsCanFocus && hasFocus();
                    boolean maintainedFocus = focusedChild != null && focusedChild.hasFocus();
                    if (!shouldPlaceFocus || maintainedFocus || sel.hasFocus()) {
                        positionSelector(-1, sel);
                    } else if (sel.requestFocus()) {
                        sel.setSelected(false);
                        this.mSelectorRect.setEmpty();
                    } else {
                        View focused = getFocusedChild();
                        if (focused != null) {
                            focused.clearFocus();
                        }
                        positionSelector(-1, sel);
                    }
                    this.mSelectedTop = sel.getTop();
                } else if (this.mTouchMode == 1 || this.mTouchMode == 2) {
                    View child = getChildAt(this.mMotionPosition - this.mFirstPosition);
                    if (child != null) {
                        positionSelector(this.mMotionPosition, child);
                    }
                } else {
                    this.mSelectedTop = 0;
                    this.mSelectorRect.setEmpty();
                }
                if (accessFocusedChild != null) {
                    accessFocusedChild.setHasTransientState(false);
                    if (!accessFocusedChild.isAccessibilityFocused() && accessibilityFocusPosition != -1) {
                        int position3 = MathUtils.constrain(accessibilityFocusPosition - this.mFirstPosition, 0, getChildCount() - 1);
                        View restoreView = getChildAt(position3);
                        if (restoreView != null) {
                            restoreView.requestAccessibilityFocus();
                        }
                    }
                }
                if (focusedChild != null) {
                    focusedChild.setHasTransientState(false);
                }
                this.mLayoutMode = 0;
                this.mDataChanged = false;
                if (this.mPositionScrollAfterLayout != null) {
                    post(this.mPositionScrollAfterLayout);
                    this.mPositionScrollAfterLayout = null;
                }
                this.mNeedSync = false;
                setNextSelectedPositionInt(this.mSelectedPosition);
                updateScrollIndicators();
                if (this.mItemCount > 0) {
                    checkSelectionChanged();
                }
                invokeOnItemScrollListener();
                if (blockLayoutRequests) {
                    return;
                }
                this.mBlockLayoutRequests = false;
            }
        } catch (Throwable th) {
            if (!blockLayoutRequests) {
                this.mBlockLayoutRequests = false;
            }
            throw th;
        }
    }

    private View getAccessibilityFocusedChild() {
        ViewParent viewParent;
        ViewRootImpl viewRootImpl = getViewRootImpl();
        if (viewRootImpl == null) {
            return null;
        }
        View focusedView = viewRootImpl.getAccessibilityFocusedHost();
        if (focusedView == null) {
            return null;
        }
        ViewParent parent = focusedView.getParent();
        while (true) {
            viewParent = parent;
            if (!(viewParent instanceof View) || viewParent == this) {
                break;
            }
            focusedView = (View) viewParent;
            parent = viewParent.getParent();
        }
        if (!(viewParent instanceof View)) {
            return null;
        }
        return focusedView;
    }

    private View makeAndAddView(int position, int y, boolean flow, int childrenLeft, boolean selected) {
        View child;
        if (!this.mDataChanged && (child = this.mRecycler.getActiveView(position)) != null) {
            setupChild(child, position, y, flow, childrenLeft, selected, true);
            return child;
        }
        View child2 = obtainView(position, this.mIsScrap);
        setupChild(child2, position, y, flow, childrenLeft, selected, this.mIsScrap[0]);
        return child2;
    }

    private void setupChild(View child, int position, int y, boolean flowDown, int childrenLeft, boolean selected, boolean recycled) {
        int childHeightSpec;
        Trace.traceBegin(8L, "setupListItem");
        boolean isSelected = selected && shouldShowSelector();
        boolean updateChildSelected = isSelected != child.isSelected();
        int mode = this.mTouchMode;
        boolean isPressed = mode > 0 && mode < 3 && this.mMotionPosition == position;
        boolean updateChildPressed = isPressed != child.isPressed();
        boolean needToMeasure = !recycled || updateChildSelected || child.isLayoutRequested();
        AbsListView.LayoutParams p = (AbsListView.LayoutParams) child.getLayoutParams();
        if (p == null) {
            p = (AbsListView.LayoutParams) generateDefaultLayoutParams();
        }
        p.viewType = this.mAdapter.getItemViewType(position);
        if ((recycled && !p.forceAdd) || (p.recycledHeaderFooter && p.viewType == -2)) {
            attachViewToParent(child, flowDown ? -1 : 0, p);
        } else {
            p.forceAdd = false;
            if (p.viewType == -2) {
                p.recycledHeaderFooter = true;
            }
            addViewInLayout(child, flowDown ? -1 : 0, p, true);
        }
        if (updateChildSelected) {
            child.setSelected(isSelected);
        }
        if (updateChildPressed) {
            child.setPressed(isPressed);
        }
        if (this.mChoiceMode != 0 && this.mCheckStates != null) {
            if (child instanceof Checkable) {
                ((Checkable) child).setChecked(this.mCheckStates.get(position));
            } else if (getContext().getApplicationInfo().targetSdkVersion >= 11) {
                child.setActivated(this.mCheckStates.get(position));
            }
        }
        if (needToMeasure) {
            int childWidthSpec = ViewGroup.getChildMeasureSpec(this.mWidthMeasureSpec, this.mListPadding.left + this.mListPadding.right, p.width);
            int lpHeight = p.height;
            if (lpHeight > 0) {
                childHeightSpec = View.MeasureSpec.makeMeasureSpec(lpHeight, 1073741824);
            } else {
                childHeightSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
            }
            child.measure(childWidthSpec, childHeightSpec);
        } else {
            cleanupLayoutState(child);
        }
        int w = child.getMeasuredWidth();
        int h = child.getMeasuredHeight();
        int childTop = flowDown ? y : y - h;
        if (needToMeasure) {
            int childRight = childrenLeft + w;
            int childBottom = childTop + h;
            child.layout(childrenLeft, childTop, childRight, childBottom);
        } else {
            child.offsetLeftAndRight(childrenLeft - child.getLeft());
            child.offsetTopAndBottom(childTop - child.getTop());
        }
        if (this.mCachingStarted && !child.isDrawingCacheEnabled()) {
            child.setDrawingCacheEnabled(true);
        }
        if (recycled && ((AbsListView.LayoutParams) child.getLayoutParams()).scrappedFromPosition != position) {
            child.jumpDrawablesToCurrentState();
        }
        Trace.traceEnd(8L);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.AdapterView, android.view.ViewGroup
    public boolean canAnimate() {
        return super.canAnimate() && this.mItemCount > 0;
    }

    @Override // android.widget.AdapterView
    public void setSelection(int position) {
        setSelectionFromTop(position, 0);
    }

    public void setSelectionFromTop(int position, int y) {
        if (this.mAdapter == null) {
            return;
        }
        if (!isInTouchMode()) {
            position = lookForSelectablePosition(position, true);
            if (position >= 0) {
                setNextSelectedPositionInt(position);
            }
        } else {
            this.mResurrectToPosition = position;
        }
        if (position >= 0) {
            this.mLayoutMode = 4;
            this.mSpecificTop = this.mListPadding.top + y;
            if (this.mNeedSync) {
                this.mSyncPosition = position;
                this.mSyncRowId = this.mAdapter.getItemId(position);
            }
            if (this.mPositionScroller != null) {
                this.mPositionScroller.stop();
            }
            requestLayout();
        }
    }

    @Override // android.widget.AbsListView
    void setSelectionInt(int position) {
        setNextSelectedPositionInt(position);
        boolean awakeScrollbars = false;
        int selectedPosition = this.mSelectedPosition;
        if (selectedPosition >= 0) {
            if (position == selectedPosition - 1) {
                awakeScrollbars = true;
            } else if (position == selectedPosition + 1) {
                awakeScrollbars = true;
            }
        }
        if (this.mPositionScroller != null) {
            this.mPositionScroller.stop();
        }
        layoutChildren();
        if (awakeScrollbars) {
            awakenScrollBars();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.widget.AdapterView
    public int lookForSelectablePosition(int position, boolean lookDown) {
        ListAdapter adapter = this.mAdapter;
        if (adapter == null || isInTouchMode()) {
            return -1;
        }
        int count = adapter.getCount();
        if (!this.mAreAllItemsSelectable) {
            if (lookDown) {
                position = Math.max(0, position);
                while (position < count && !adapter.isEnabled(position)) {
                    position++;
                }
            } else {
                position = Math.min(position, count - 1);
                while (position >= 0 && !adapter.isEnabled(position)) {
                    position--;
                }
            }
        }
        if (position < 0 || position >= count) {
            return -1;
        }
        return position;
    }

    int lookForSelectablePositionAfter(int current, int position, boolean lookDown) {
        int position2;
        ListAdapter adapter = this.mAdapter;
        if (adapter == null || isInTouchMode()) {
            return -1;
        }
        int after = lookForSelectablePosition(position, lookDown);
        if (after != -1) {
            return after;
        }
        int count = adapter.getCount();
        int current2 = MathUtils.constrain(current, -1, count - 1);
        if (lookDown) {
            position2 = Math.min(position - 1, count - 1);
            while (position2 > current2 && !adapter.isEnabled(position2)) {
                position2--;
            }
            if (position2 <= current2) {
                return -1;
            }
        } else {
            position2 = Math.max(0, position + 1);
            while (position2 < current2 && !adapter.isEnabled(position2)) {
                position2++;
            }
            if (position2 >= current2) {
                return -1;
            }
        }
        return position2;
    }

    public void setSelectionAfterHeaderView() {
        int count = this.mHeaderViewInfos.size();
        if (count > 0) {
            this.mNextSelectedPosition = 0;
        } else if (this.mAdapter != null) {
            setSelection(count);
        } else {
            this.mNextSelectedPosition = count;
            this.mLayoutMode = 2;
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchKeyEvent(KeyEvent event) {
        boolean handled = super.dispatchKeyEvent(event);
        if (!handled) {
            View focused = getFocusedChild();
            if (focused != null && event.getAction() == 0) {
                handled = onKeyDown(event.getKeyCode(), event);
            }
        }
        return handled;
    }

    @Override // android.widget.AbsListView, android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return commonKey(keyCode, 1, event);
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        return commonKey(keyCode, repeatCount, event);
    }

    @Override // android.widget.AbsListView, android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return commonKey(keyCode, 1, event);
    }

    private boolean commonKey(int keyCode, int count, KeyEvent event) {
        if (this.mAdapter == null || !isAttachedToWindow()) {
            return false;
        }
        if (this.mDataChanged) {
            layoutChildren();
        }
        boolean handled = false;
        int action = event.getAction();
        if (action != 1) {
            switch (keyCode) {
                case 19:
                    if (event.hasNoModifiers()) {
                        handled = resurrectSelectionIfNeeded();
                        if (!handled) {
                            while (true) {
                                int i = count;
                                count--;
                                if (i > 0 && arrowScroll(33)) {
                                    handled = true;
                                }
                            }
                        }
                    } else if (event.hasModifiers(2)) {
                        handled = resurrectSelectionIfNeeded() || fullScroll(33);
                        break;
                    }
                    break;
                case 20:
                    if (event.hasNoModifiers()) {
                        handled = resurrectSelectionIfNeeded();
                        if (!handled) {
                            while (true) {
                                int i2 = count;
                                count--;
                                if (i2 > 0 && arrowScroll(130)) {
                                    handled = true;
                                }
                            }
                        }
                    } else if (event.hasModifiers(2)) {
                        handled = resurrectSelectionIfNeeded() || fullScroll(130);
                        break;
                    }
                    break;
                case 21:
                    if (event.hasNoModifiers()) {
                        handled = handleHorizontalFocusWithinListItem(17);
                        break;
                    }
                    break;
                case 22:
                    if (event.hasNoModifiers()) {
                        handled = handleHorizontalFocusWithinListItem(66);
                        break;
                    }
                    break;
                case 23:
                case 66:
                    if (event.hasNoModifiers()) {
                        handled = resurrectSelectionIfNeeded();
                        if (!handled && event.getRepeatCount() == 0 && getChildCount() > 0) {
                            keyPressed();
                            handled = true;
                            break;
                        }
                    }
                    break;
                case 62:
                    if (this.mPopup == null || !this.mPopup.isShowing()) {
                        if (event.hasNoModifiers()) {
                            boolean z = resurrectSelectionIfNeeded() || pageScroll(130);
                        } else if (event.hasModifiers(1)) {
                            boolean z2 = resurrectSelectionIfNeeded() || pageScroll(33);
                        }
                        handled = true;
                        break;
                    }
                    break;
                case 92:
                    if (event.hasNoModifiers()) {
                        handled = resurrectSelectionIfNeeded() || pageScroll(33);
                        break;
                    } else if (event.hasModifiers(2)) {
                        handled = resurrectSelectionIfNeeded() || fullScroll(33);
                        break;
                    }
                    break;
                case 93:
                    if (event.hasNoModifiers()) {
                        handled = resurrectSelectionIfNeeded() || pageScroll(130);
                        break;
                    } else if (event.hasModifiers(2)) {
                        handled = resurrectSelectionIfNeeded() || fullScroll(130);
                        break;
                    }
                    break;
                case 122:
                    if (event.hasNoModifiers()) {
                        handled = resurrectSelectionIfNeeded() || fullScroll(33);
                        break;
                    }
                    break;
                case 123:
                    if (event.hasNoModifiers()) {
                        handled = resurrectSelectionIfNeeded() || fullScroll(130);
                        break;
                    }
                    break;
            }
        }
        if (handled || sendToTextFilter(keyCode, count, event)) {
            return true;
        }
        switch (action) {
            case 0:
                return super.onKeyDown(keyCode, event);
            case 1:
                return super.onKeyUp(keyCode, event);
            case 2:
                return super.onKeyMultiple(keyCode, count, event);
            default:
                return false;
        }
    }

    boolean pageScroll(int direction) {
        int nextPage;
        boolean down;
        int position;
        if (direction == 33) {
            nextPage = Math.max(0, (this.mSelectedPosition - getChildCount()) - 1);
            down = false;
        } else if (direction == 130) {
            nextPage = Math.min(this.mItemCount - 1, (this.mSelectedPosition + getChildCount()) - 1);
            down = true;
        } else {
            return false;
        }
        if (nextPage >= 0 && (position = lookForSelectablePositionAfter(this.mSelectedPosition, nextPage, down)) >= 0) {
            this.mLayoutMode = 4;
            this.mSpecificTop = this.mPaddingTop + getVerticalFadingEdgeLength();
            if (down && position > this.mItemCount - getChildCount()) {
                this.mLayoutMode = 3;
            }
            if (!down && position < getChildCount()) {
                this.mLayoutMode = 1;
            }
            setSelectionInt(position);
            invokeOnItemScrollListener();
            if (!awakenScrollBars()) {
                invalidate();
                return true;
            }
            return true;
        }
        return false;
    }

    boolean fullScroll(int direction) {
        int lastItem;
        boolean moved = false;
        if (direction == 33) {
            if (this.mSelectedPosition != 0) {
                int position = lookForSelectablePositionAfter(this.mSelectedPosition, 0, true);
                if (position >= 0) {
                    this.mLayoutMode = 1;
                    setSelectionInt(position);
                    invokeOnItemScrollListener();
                }
                moved = true;
            }
        } else if (direction == 130 && this.mSelectedPosition < (lastItem = this.mItemCount - 1)) {
            int position2 = lookForSelectablePositionAfter(this.mSelectedPosition, lastItem, false);
            if (position2 >= 0) {
                this.mLayoutMode = 3;
                setSelectionInt(position2);
                invokeOnItemScrollListener();
            }
            moved = true;
        }
        if (moved && !awakenScrollBars()) {
            awakenScrollBars();
            invalidate();
        }
        return moved;
    }

    private boolean handleHorizontalFocusWithinListItem(int direction) {
        View selectedView;
        if (direction != 17 && direction != 66) {
            throw new IllegalArgumentException("direction must be one of {View.FOCUS_LEFT, View.FOCUS_RIGHT}");
        }
        int numChildren = getChildCount();
        if (this.mItemsCanFocus && numChildren > 0 && this.mSelectedPosition != -1 && (selectedView = getSelectedView()) != null && selectedView.hasFocus() && (selectedView instanceof ViewGroup)) {
            View currentFocus = selectedView.findFocus();
            View nextFocus = FocusFinder.getInstance().findNextFocus((ViewGroup) selectedView, currentFocus, direction);
            if (nextFocus != null) {
                currentFocus.getFocusedRect(this.mTempRect);
                offsetDescendantRectToMyCoords(currentFocus, this.mTempRect);
                offsetRectIntoDescendantCoords(nextFocus, this.mTempRect);
                if (nextFocus.requestFocus(direction, this.mTempRect)) {
                    return true;
                }
            }
            View globalNextFocus = FocusFinder.getInstance().findNextFocus((ViewGroup) getRootView(), currentFocus, direction);
            if (globalNextFocus != null) {
                return isViewAncestorOf(globalNextFocus, this);
            }
            return false;
        }
        return false;
    }

    boolean arrowScroll(int direction) {
        try {
            this.mInLayout = true;
            boolean handled = arrowScrollImpl(direction);
            if (handled) {
                playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction));
            }
            return handled;
        } finally {
            this.mInLayout = false;
        }
    }

    private final int nextSelectedPositionForDirection(View selectedView, int selectedPos, int direction) {
        int nextSelected;
        if (direction == 130) {
            int listBottom = getHeight() - this.mListPadding.bottom;
            if (selectedView != null && selectedView.getBottom() <= listBottom) {
                nextSelected = (selectedPos == -1 || selectedPos < this.mFirstPosition) ? this.mFirstPosition : selectedPos + 1;
            } else {
                return -1;
            }
        } else {
            int listTop = this.mListPadding.top;
            if (selectedView != null && selectedView.getTop() >= listTop) {
                int lastPos = (this.mFirstPosition + getChildCount()) - 1;
                nextSelected = (selectedPos == -1 || selectedPos > lastPos) ? lastPos : selectedPos - 1;
            } else {
                return -1;
            }
        }
        if (nextSelected < 0 || nextSelected >= this.mAdapter.getCount()) {
            return -1;
        }
        return lookForSelectablePosition(nextSelected, direction == 130);
    }

    private boolean arrowScrollImpl(int direction) {
        View focused;
        if (getChildCount() <= 0) {
            return false;
        }
        View selectedView = getSelectedView();
        int selectedPos = this.mSelectedPosition;
        int nextSelectedPosition = nextSelectedPositionForDirection(selectedView, selectedPos, direction);
        int amountToScroll = amountToScroll(direction, nextSelectedPosition);
        ArrowScrollFocusResult focusResult = this.mItemsCanFocus ? arrowScrollFocused(direction) : null;
        if (focusResult != null) {
            nextSelectedPosition = focusResult.getSelectedPosition();
            amountToScroll = focusResult.getAmountToScroll();
        }
        boolean needToRedraw = focusResult != null;
        if (nextSelectedPosition != -1) {
            handleNewSelectionChange(selectedView, direction, nextSelectedPosition, focusResult != null);
            setSelectedPositionInt(nextSelectedPosition);
            setNextSelectedPositionInt(nextSelectedPosition);
            selectedView = getSelectedView();
            selectedPos = nextSelectedPosition;
            if (this.mItemsCanFocus && focusResult == null && (focused = getFocusedChild()) != null) {
                focused.clearFocus();
            }
            needToRedraw = true;
            checkSelectionChanged();
        }
        if (amountToScroll > 0) {
            scrollListItemsBy(direction == 33 ? amountToScroll : -amountToScroll);
            needToRedraw = true;
        }
        if (this.mItemsCanFocus && focusResult == null && selectedView != null && selectedView.hasFocus()) {
            View focused2 = selectedView.findFocus();
            if (!isViewAncestorOf(focused2, this) || distanceToView(focused2) > 0) {
                focused2.clearFocus();
            }
        }
        if (nextSelectedPosition == -1 && selectedView != null && !isViewAncestorOf(selectedView, this)) {
            selectedView = null;
            hideSelector();
            this.mResurrectToPosition = -1;
        }
        if (needToRedraw) {
            if (selectedView != null) {
                positionSelector(selectedPos, selectedView);
                this.mSelectedTop = selectedView.getTop();
            }
            if (!awakenScrollBars()) {
                invalidate();
            }
            invokeOnItemScrollListener();
            return true;
        }
        return false;
    }

    private void handleNewSelectionChange(View selectedView, int direction, int newSelectedPosition, boolean newFocusAssigned) {
        int topViewIndex;
        int bottomViewIndex;
        View topView;
        View bottomView;
        if (newSelectedPosition == -1) {
            throw new IllegalArgumentException("newSelectedPosition needs to be valid");
        }
        boolean topSelected = false;
        int selectedIndex = this.mSelectedPosition - this.mFirstPosition;
        int nextSelectedIndex = newSelectedPosition - this.mFirstPosition;
        if (direction == 33) {
            topViewIndex = nextSelectedIndex;
            bottomViewIndex = selectedIndex;
            topView = getChildAt(topViewIndex);
            bottomView = selectedView;
            topSelected = true;
        } else {
            topViewIndex = selectedIndex;
            bottomViewIndex = nextSelectedIndex;
            topView = selectedView;
            bottomView = getChildAt(bottomViewIndex);
        }
        int numChildren = getChildCount();
        if (topView != null) {
            topView.setSelected(!newFocusAssigned && topSelected);
            measureAndAdjustDown(topView, topViewIndex, numChildren);
        }
        if (bottomView != null) {
            bottomView.setSelected((newFocusAssigned || topSelected) ? false : true);
            measureAndAdjustDown(bottomView, bottomViewIndex, numChildren);
        }
    }

    private void measureAndAdjustDown(View child, int childIndex, int numChildren) {
        int oldHeight = child.getHeight();
        measureItem(child);
        if (child.getMeasuredHeight() != oldHeight) {
            relayoutMeasuredItem(child);
            int heightDelta = child.getMeasuredHeight() - oldHeight;
            for (int i = childIndex + 1; i < numChildren; i++) {
                getChildAt(i).offsetTopAndBottom(heightDelta);
            }
        }
    }

    private void measureItem(View child) {
        int childHeightSpec;
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(-1, -2);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(this.mWidthMeasureSpec, this.mListPadding.left + this.mListPadding.right, p.width);
        int lpHeight = p.height;
        if (lpHeight > 0) {
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(lpHeight, 1073741824);
        } else {
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    private void relayoutMeasuredItem(View child) {
        int w = child.getMeasuredWidth();
        int h = child.getMeasuredHeight();
        int childLeft = this.mListPadding.left;
        int childRight = childLeft + w;
        int childTop = child.getTop();
        int childBottom = childTop + h;
        child.layout(childLeft, childTop, childRight, childBottom);
    }

    private int getArrowScrollPreviewLength() {
        return Math.max(2, getVerticalFadingEdgeLength());
    }

    private int amountToScroll(int direction, int nextSelectedPosition) {
        int listBottom = getHeight() - this.mListPadding.bottom;
        int listTop = this.mListPadding.top;
        int numChildren = getChildCount();
        if (direction == 130) {
            int indexToMakeVisible = numChildren - 1;
            if (nextSelectedPosition != -1) {
                indexToMakeVisible = nextSelectedPosition - this.mFirstPosition;
            }
            while (numChildren <= indexToMakeVisible) {
                addViewBelow(getChildAt(numChildren - 1), (this.mFirstPosition + numChildren) - 1);
                numChildren++;
            }
            int positionToMakeVisible = this.mFirstPosition + indexToMakeVisible;
            View viewToMakeVisible = getChildAt(indexToMakeVisible);
            int goalBottom = listBottom;
            if (positionToMakeVisible < this.mItemCount - 1) {
                goalBottom -= getArrowScrollPreviewLength();
            }
            if (viewToMakeVisible.getBottom() <= goalBottom) {
                return 0;
            }
            if (nextSelectedPosition != -1 && goalBottom - viewToMakeVisible.getTop() >= getMaxScrollAmount()) {
                return 0;
            }
            int amountToScroll = viewToMakeVisible.getBottom() - goalBottom;
            if (this.mFirstPosition + numChildren == this.mItemCount) {
                int max = getChildAt(numChildren - 1).getBottom() - listBottom;
                amountToScroll = Math.min(amountToScroll, max);
            }
            return Math.min(amountToScroll, getMaxScrollAmount());
        }
        int indexToMakeVisible2 = 0;
        if (nextSelectedPosition != -1) {
            indexToMakeVisible2 = nextSelectedPosition - this.mFirstPosition;
        }
        while (indexToMakeVisible2 < 0) {
            addViewAbove(getChildAt(0), this.mFirstPosition);
            this.mFirstPosition--;
            indexToMakeVisible2 = nextSelectedPosition - this.mFirstPosition;
        }
        int positionToMakeVisible2 = this.mFirstPosition + indexToMakeVisible2;
        View viewToMakeVisible2 = getChildAt(indexToMakeVisible2);
        int goalTop = listTop;
        if (positionToMakeVisible2 > 0) {
            goalTop += getArrowScrollPreviewLength();
        }
        if (viewToMakeVisible2.getTop() >= goalTop) {
            return 0;
        }
        if (nextSelectedPosition != -1 && viewToMakeVisible2.getBottom() - goalTop >= getMaxScrollAmount()) {
            return 0;
        }
        int amountToScroll2 = goalTop - viewToMakeVisible2.getTop();
        if (this.mFirstPosition == 0) {
            int max2 = listTop - getChildAt(0).getTop();
            amountToScroll2 = Math.min(amountToScroll2, max2);
        }
        return Math.min(amountToScroll2, getMaxScrollAmount());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ListView$ArrowScrollFocusResult.class */
    public static class ArrowScrollFocusResult {
        private int mSelectedPosition;
        private int mAmountToScroll;

        private ArrowScrollFocusResult() {
        }

        void populate(int selectedPosition, int amountToScroll) {
            this.mSelectedPosition = selectedPosition;
            this.mAmountToScroll = amountToScroll;
        }

        public int getSelectedPosition() {
            return this.mSelectedPosition;
        }

        public int getAmountToScroll() {
            return this.mAmountToScroll;
        }
    }

    private int lookForSelectablePositionOnScreen(int direction) {
        int firstPosition = this.mFirstPosition;
        if (direction == 130) {
            int startPos = this.mSelectedPosition != -1 ? this.mSelectedPosition + 1 : firstPosition;
            if (startPos >= this.mAdapter.getCount()) {
                return -1;
            }
            if (startPos < firstPosition) {
                startPos = firstPosition;
            }
            int lastVisiblePos = getLastVisiblePosition();
            ListAdapter adapter = getAdapter();
            for (int pos = startPos; pos <= lastVisiblePos; pos++) {
                if (adapter.isEnabled(pos) && getChildAt(pos - firstPosition).getVisibility() == 0) {
                    return pos;
                }
            }
            return -1;
        }
        int last = (firstPosition + getChildCount()) - 1;
        int startPos2 = this.mSelectedPosition != -1 ? this.mSelectedPosition - 1 : (firstPosition + getChildCount()) - 1;
        if (startPos2 < 0 || startPos2 >= this.mAdapter.getCount()) {
            return -1;
        }
        if (startPos2 > last) {
            startPos2 = last;
        }
        ListAdapter adapter2 = getAdapter();
        for (int pos2 = startPos2; pos2 >= firstPosition; pos2--) {
            if (adapter2.isEnabled(pos2) && getChildAt(pos2 - firstPosition).getVisibility() == 0) {
                return pos2;
            }
        }
        return -1;
    }

    private ArrowScrollFocusResult arrowScrollFocused(int direction) {
        View newFocus;
        int selectablePosition;
        View selectedView = getSelectedView();
        if (selectedView != null && selectedView.hasFocus()) {
            View oldFocus = selectedView.findFocus();
            newFocus = FocusFinder.getInstance().findNextFocus(this, oldFocus, direction);
        } else {
            if (direction == 130) {
                boolean topFadingEdgeShowing = this.mFirstPosition > 0;
                int listTop = this.mListPadding.top + (topFadingEdgeShowing ? getArrowScrollPreviewLength() : 0);
                int ySearchPoint = (selectedView == null || selectedView.getTop() <= listTop) ? listTop : selectedView.getTop();
                this.mTempRect.set(0, ySearchPoint, 0, ySearchPoint);
            } else {
                boolean bottomFadingEdgeShowing = (this.mFirstPosition + getChildCount()) - 1 < this.mItemCount;
                int listBottom = (getHeight() - this.mListPadding.bottom) - (bottomFadingEdgeShowing ? getArrowScrollPreviewLength() : 0);
                int ySearchPoint2 = (selectedView == null || selectedView.getBottom() >= listBottom) ? listBottom : selectedView.getBottom();
                this.mTempRect.set(0, ySearchPoint2, 0, ySearchPoint2);
            }
            newFocus = FocusFinder.getInstance().findNextFocusFromRect(this, this.mTempRect, direction);
        }
        if (newFocus != null) {
            int positionOfNewFocus = positionOfNewFocus(newFocus);
            if (this.mSelectedPosition != -1 && positionOfNewFocus != this.mSelectedPosition && (selectablePosition = lookForSelectablePositionOnScreen(direction)) != -1) {
                if (direction == 130 && selectablePosition < positionOfNewFocus) {
                    return null;
                }
                if (direction == 33 && selectablePosition > positionOfNewFocus) {
                    return null;
                }
            }
            int focusScroll = amountToScrollToNewFocus(direction, newFocus, positionOfNewFocus);
            int maxScrollAmount = getMaxScrollAmount();
            if (focusScroll < maxScrollAmount) {
                newFocus.requestFocus(direction);
                this.mArrowScrollFocusResult.populate(positionOfNewFocus, focusScroll);
                return this.mArrowScrollFocusResult;
            } else if (distanceToView(newFocus) < maxScrollAmount) {
                newFocus.requestFocus(direction);
                this.mArrowScrollFocusResult.populate(positionOfNewFocus, maxScrollAmount);
                return this.mArrowScrollFocusResult;
            } else {
                return null;
            }
        }
        return null;
    }

    private int positionOfNewFocus(View newFocus) {
        int numChildren = getChildCount();
        for (int i = 0; i < numChildren; i++) {
            View child = getChildAt(i);
            if (isViewAncestorOf(newFocus, child)) {
                return this.mFirstPosition + i;
            }
        }
        throw new IllegalArgumentException("newFocus is not a child of any of the children of the list!");
    }

    private boolean isViewAncestorOf(View child, View parent) {
        if (child == parent) {
            return true;
        }
        ViewParent theParent = child.getParent();
        return (theParent instanceof ViewGroup) && isViewAncestorOf((View) theParent, parent);
    }

    private int amountToScrollToNewFocus(int direction, View newFocus, int positionOfNewFocus) {
        int amountToScroll = 0;
        newFocus.getDrawingRect(this.mTempRect);
        offsetDescendantRectToMyCoords(newFocus, this.mTempRect);
        if (direction == 33) {
            if (this.mTempRect.top < this.mListPadding.top) {
                amountToScroll = this.mListPadding.top - this.mTempRect.top;
                if (positionOfNewFocus > 0) {
                    amountToScroll += getArrowScrollPreviewLength();
                }
            }
        } else {
            int listBottom = getHeight() - this.mListPadding.bottom;
            if (this.mTempRect.bottom > listBottom) {
                amountToScroll = this.mTempRect.bottom - listBottom;
                if (positionOfNewFocus < this.mItemCount - 1) {
                    amountToScroll += getArrowScrollPreviewLength();
                }
            }
        }
        return amountToScroll;
    }

    private int distanceToView(View descendant) {
        int distance = 0;
        descendant.getDrawingRect(this.mTempRect);
        offsetDescendantRectToMyCoords(descendant, this.mTempRect);
        int listBottom = (this.mBottom - this.mTop) - this.mListPadding.bottom;
        if (this.mTempRect.bottom < this.mListPadding.top) {
            distance = this.mListPadding.top - this.mTempRect.bottom;
        } else if (this.mTempRect.top > listBottom) {
            distance = this.mTempRect.top - listBottom;
        }
        return distance;
    }

    private void scrollListItemsBy(int amount) {
        int lastVisiblePosition;
        offsetChildrenTopAndBottom(amount);
        int listBottom = getHeight() - this.mListPadding.bottom;
        int listTop = this.mListPadding.top;
        AbsListView.RecycleBin recycleBin = this.mRecycler;
        if (amount < 0) {
            int numChildren = getChildCount();
            View last = getChildAt(numChildren - 1);
            while (last.getBottom() < listBottom && (lastVisiblePosition = (this.mFirstPosition + numChildren) - 1) < this.mItemCount - 1) {
                last = addViewBelow(last, lastVisiblePosition);
                numChildren++;
            }
            if (last.getBottom() < listBottom) {
                offsetChildrenTopAndBottom(listBottom - last.getBottom());
            }
            View first = getChildAt(0);
            while (first.getBottom() < listTop) {
                AbsListView.LayoutParams layoutParams = (AbsListView.LayoutParams) first.getLayoutParams();
                if (recycleBin.shouldRecycleViewType(layoutParams.viewType)) {
                    recycleBin.addScrapView(first, this.mFirstPosition);
                }
                detachViewFromParent(first);
                first = getChildAt(0);
                this.mFirstPosition++;
            }
            return;
        }
        View first2 = getChildAt(0);
        while (first2.getTop() > listTop && this.mFirstPosition > 0) {
            first2 = addViewAbove(first2, this.mFirstPosition);
            this.mFirstPosition--;
        }
        if (first2.getTop() > listTop) {
            offsetChildrenTopAndBottom(listTop - first2.getTop());
        }
        int lastIndex = getChildCount() - 1;
        View childAt = getChildAt(lastIndex);
        while (true) {
            View last2 = childAt;
            if (last2.getTop() > listBottom) {
                AbsListView.LayoutParams layoutParams2 = (AbsListView.LayoutParams) last2.getLayoutParams();
                if (recycleBin.shouldRecycleViewType(layoutParams2.viewType)) {
                    recycleBin.addScrapView(last2, this.mFirstPosition + lastIndex);
                }
                detachViewFromParent(last2);
                lastIndex--;
                childAt = getChildAt(lastIndex);
            } else {
                return;
            }
        }
    }

    private View addViewAbove(View theView, int position) {
        int abovePosition = position - 1;
        View view = obtainView(abovePosition, this.mIsScrap);
        int edgeOfNewChild = theView.getTop() - this.mDividerHeight;
        setupChild(view, abovePosition, edgeOfNewChild, false, this.mListPadding.left, false, this.mIsScrap[0]);
        return view;
    }

    private View addViewBelow(View theView, int position) {
        int belowPosition = position + 1;
        View view = obtainView(belowPosition, this.mIsScrap);
        int edgeOfNewChild = theView.getBottom() + this.mDividerHeight;
        setupChild(view, belowPosition, edgeOfNewChild, true, this.mListPadding.left, false, this.mIsScrap[0]);
        return view;
    }

    public void setItemsCanFocus(boolean itemsCanFocus) {
        this.mItemsCanFocus = itemsCanFocus;
        if (!itemsCanFocus) {
            setDescendantFocusability(393216);
        }
    }

    public boolean getItemsCanFocus() {
        return this.mItemsCanFocus;
    }

    @Override // android.view.View
    public boolean isOpaque() {
        boolean retValue = (this.mCachingActive && this.mIsCacheColorOpaque && this.mDividerIsOpaque && hasOpaqueScrollbars()) || super.isOpaque();
        if (retValue) {
            int listTop = this.mListPadding != null ? this.mListPadding.top : this.mPaddingTop;
            View first = getChildAt(0);
            if (first == null || first.getTop() > listTop) {
                return false;
            }
            int listBottom = getHeight() - (this.mListPadding != null ? this.mListPadding.bottom : this.mPaddingBottom);
            View last = getChildAt(getChildCount() - 1);
            if (last == null || last.getBottom() < listBottom) {
                return false;
            }
        }
        return retValue;
    }

    @Override // android.widget.AbsListView
    public void setCacheColorHint(int color) {
        boolean opaque = (color >>> 24) == 255;
        this.mIsCacheColorOpaque = opaque;
        if (opaque) {
            if (this.mDividerPaint == null) {
                this.mDividerPaint = new Paint();
            }
            this.mDividerPaint.setColor(color);
        }
        super.setCacheColorHint(color);
    }

    void drawOverscrollHeader(Canvas canvas, Drawable drawable, Rect bounds) {
        int height = drawable.getMinimumHeight();
        canvas.save();
        canvas.clipRect(bounds);
        int span = bounds.bottom - bounds.top;
        if (span < height) {
            bounds.top = bounds.bottom - height;
        }
        drawable.setBounds(bounds);
        drawable.draw(canvas);
        canvas.restore();
    }

    void drawOverscrollFooter(Canvas canvas, Drawable drawable, Rect bounds) {
        int height = drawable.getMinimumHeight();
        canvas.save();
        canvas.clipRect(bounds);
        int span = bounds.bottom - bounds.top;
        if (span < height) {
            bounds.bottom = bounds.top + height;
        }
        drawable.setBounds(bounds);
        drawable.draw(canvas);
        canvas.restore();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.AbsListView, android.view.ViewGroup, android.view.View
    public void dispatchDraw(Canvas canvas) {
        if (this.mCachingStarted) {
            this.mCachingActive = true;
        }
        int dividerHeight = this.mDividerHeight;
        Drawable overscrollHeader = this.mOverScrollHeader;
        Drawable overscrollFooter = this.mOverScrollFooter;
        boolean drawOverscrollHeader = overscrollHeader != null;
        boolean drawOverscrollFooter = overscrollFooter != null;
        boolean drawDividers = dividerHeight > 0 && this.mDivider != null;
        if (drawDividers || drawOverscrollHeader || drawOverscrollFooter) {
            Rect bounds = this.mTempRect;
            bounds.left = this.mPaddingLeft;
            bounds.right = (this.mRight - this.mLeft) - this.mPaddingRight;
            int count = getChildCount();
            int headerCount = this.mHeaderViewInfos.size();
            int itemCount = this.mItemCount;
            int footerLimit = itemCount - this.mFooterViewInfos.size();
            boolean headerDividers = this.mHeaderDividersEnabled;
            boolean footerDividers = this.mFooterDividersEnabled;
            int first = this.mFirstPosition;
            boolean areAllItemsSelectable = this.mAreAllItemsSelectable;
            ListAdapter adapter = this.mAdapter;
            boolean fillForMissingDividers = isOpaque() && !super.isOpaque();
            if (fillForMissingDividers && this.mDividerPaint == null && this.mIsCacheColorOpaque) {
                this.mDividerPaint = new Paint();
                this.mDividerPaint.setColor(getCacheColorHint());
            }
            Paint paint = this.mDividerPaint;
            int effectivePaddingTop = 0;
            int effectivePaddingBottom = 0;
            if ((this.mGroupFlags & 34) == 34) {
                effectivePaddingTop = this.mListPadding.top;
                effectivePaddingBottom = this.mListPadding.bottom;
            }
            int listBottom = ((this.mBottom - this.mTop) - effectivePaddingBottom) + this.mScrollY;
            if (!this.mStackFromBottom) {
                int bottom = 0;
                int scrollY = this.mScrollY;
                if (count > 0 && scrollY < 0) {
                    if (drawOverscrollHeader) {
                        bounds.bottom = 0;
                        bounds.top = scrollY;
                        drawOverscrollHeader(canvas, overscrollHeader, bounds);
                    } else if (drawDividers) {
                        bounds.bottom = 0;
                        bounds.top = -dividerHeight;
                        drawDivider(canvas, bounds, -1);
                    }
                }
                int i = 0;
                while (i < count) {
                    int itemIndex = first + i;
                    boolean isHeader = itemIndex < headerCount;
                    boolean isFooter = itemIndex >= footerLimit;
                    if ((headerDividers || !isHeader) && (footerDividers || !isFooter)) {
                        View child = getChildAt(i);
                        bottom = child.getBottom();
                        boolean isLastItem = i == count - 1;
                        if (drawDividers && bottom < listBottom && (!drawOverscrollFooter || !isLastItem)) {
                            int nextIndex = itemIndex + 1;
                            if (areAllItemsSelectable || ((adapter.isEnabled(itemIndex) || ((headerDividers && isHeader) || (footerDividers && isFooter))) && (isLastItem || adapter.isEnabled(nextIndex) || ((headerDividers && nextIndex < headerCount) || (footerDividers && nextIndex >= footerLimit))))) {
                                bounds.top = bottom;
                                bounds.bottom = bottom + dividerHeight;
                                drawDivider(canvas, bounds, i);
                            } else if (fillForMissingDividers) {
                                bounds.top = bottom;
                                bounds.bottom = bottom + dividerHeight;
                                canvas.drawRect(bounds, paint);
                            }
                        }
                    }
                    i++;
                }
                int overFooterBottom = this.mBottom + this.mScrollY;
                if (drawOverscrollFooter && first + count == itemCount && overFooterBottom > bottom) {
                    bounds.top = bottom;
                    bounds.bottom = overFooterBottom;
                    drawOverscrollFooter(canvas, overscrollFooter, bounds);
                }
            } else {
                int scrollY2 = this.mScrollY;
                if (count > 0 && drawOverscrollHeader) {
                    bounds.top = scrollY2;
                    bounds.bottom = getChildAt(0).getTop();
                    drawOverscrollHeader(canvas, overscrollHeader, bounds);
                }
                int start = drawOverscrollHeader ? 1 : 0;
                int i2 = start;
                while (i2 < count) {
                    int itemIndex2 = first + i2;
                    boolean isHeader2 = itemIndex2 < headerCount;
                    boolean isFooter2 = itemIndex2 >= footerLimit;
                    if ((headerDividers || !isHeader2) && (footerDividers || !isFooter2)) {
                        View child2 = getChildAt(i2);
                        int top = child2.getTop();
                        if (drawDividers && top > effectivePaddingTop) {
                            boolean isFirstItem = i2 == start;
                            int previousIndex = itemIndex2 - 1;
                            if (areAllItemsSelectable || ((adapter.isEnabled(itemIndex2) || ((headerDividers && isHeader2) || (footerDividers && isFooter2))) && (isFirstItem || adapter.isEnabled(previousIndex) || ((headerDividers && previousIndex < headerCount) || (footerDividers && previousIndex >= footerLimit))))) {
                                bounds.top = top - dividerHeight;
                                bounds.bottom = top;
                                drawDivider(canvas, bounds, i2 - 1);
                            } else if (fillForMissingDividers) {
                                bounds.top = top - dividerHeight;
                                bounds.bottom = top;
                                canvas.drawRect(bounds, paint);
                            }
                        }
                    }
                    i2++;
                }
                if (count > 0 && scrollY2 > 0) {
                    if (drawOverscrollFooter) {
                        int absListBottom = this.mBottom;
                        bounds.top = absListBottom;
                        bounds.bottom = absListBottom + scrollY2;
                        drawOverscrollFooter(canvas, overscrollFooter, bounds);
                    } else if (drawDividers) {
                        bounds.top = listBottom;
                        bounds.bottom = listBottom + dividerHeight;
                        drawDivider(canvas, bounds, -1);
                    }
                }
            }
        }
        super.dispatchDraw(canvas);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean more = super.drawChild(canvas, child, drawingTime);
        if (this.mCachingActive && child.mCachingFailed) {
            this.mCachingActive = false;
        }
        return more;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void drawDivider(Canvas canvas, Rect bounds, int childIndex) {
        Drawable divider = this.mDivider;
        divider.setBounds(bounds);
        divider.draw(canvas);
    }

    public Drawable getDivider() {
        return this.mDivider;
    }

    public void setDivider(Drawable divider) {
        if (divider != null) {
            this.mDividerHeight = divider.getIntrinsicHeight();
        } else {
            this.mDividerHeight = 0;
        }
        this.mDivider = divider;
        this.mDividerIsOpaque = divider == null || divider.getOpacity() == -1;
        requestLayout();
        invalidate();
    }

    public int getDividerHeight() {
        return this.mDividerHeight;
    }

    public void setDividerHeight(int height) {
        this.mDividerHeight = height;
        requestLayout();
        invalidate();
    }

    public void setHeaderDividersEnabled(boolean headerDividersEnabled) {
        this.mHeaderDividersEnabled = headerDividersEnabled;
        invalidate();
    }

    public boolean areHeaderDividersEnabled() {
        return this.mHeaderDividersEnabled;
    }

    public void setFooterDividersEnabled(boolean footerDividersEnabled) {
        this.mFooterDividersEnabled = footerDividersEnabled;
        invalidate();
    }

    public boolean areFooterDividersEnabled() {
        return this.mFooterDividersEnabled;
    }

    public void setOverscrollHeader(Drawable header) {
        this.mOverScrollHeader = header;
        if (this.mScrollY < 0) {
            invalidate();
        }
    }

    public Drawable getOverscrollHeader() {
        return this.mOverScrollHeader;
    }

    public void setOverscrollFooter(Drawable footer) {
        this.mOverScrollFooter = footer;
        invalidate();
    }

    public Drawable getOverscrollFooter() {
        return this.mOverScrollFooter;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.AbsListView, android.view.View
    public void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        ListAdapter adapter = this.mAdapter;
        int closetChildIndex = -1;
        int closestChildTop = 0;
        if (adapter != null && gainFocus && previouslyFocusedRect != null) {
            previouslyFocusedRect.offset(this.mScrollX, this.mScrollY);
            if (adapter.getCount() < getChildCount() + this.mFirstPosition) {
                this.mLayoutMode = 0;
                layoutChildren();
            }
            Rect otherRect = this.mTempRect;
            int minDistance = Integer.MAX_VALUE;
            int childCount = getChildCount();
            int firstPosition = this.mFirstPosition;
            for (int i = 0; i < childCount; i++) {
                if (adapter.isEnabled(firstPosition + i)) {
                    View other = getChildAt(i);
                    other.getDrawingRect(otherRect);
                    offsetDescendantRectToMyCoords(other, otherRect);
                    int distance = getDistance(previouslyFocusedRect, otherRect, direction);
                    if (distance < minDistance) {
                        minDistance = distance;
                        closetChildIndex = i;
                        closestChildTop = other.getTop();
                    }
                }
            }
        }
        if (closetChildIndex >= 0) {
            setSelectionFromTop(closetChildIndex + this.mFirstPosition, closestChildTop);
        } else {
            requestLayout();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        int count = getChildCount();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                addHeaderView(getChildAt(i));
            }
            removeAllViews();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public View findViewTraversal(int id) {
        View v = super.findViewTraversal(id);
        if (v == null) {
            View v2 = findViewInHeadersOrFooters(this.mHeaderViewInfos, id);
            if (v2 != null) {
                return v2;
            }
            v = findViewInHeadersOrFooters(this.mFooterViewInfos, id);
            if (v != null) {
                return v;
            }
        }
        return v;
    }

    View findViewInHeadersOrFooters(ArrayList<FixedViewInfo> where, int id) {
        View v;
        if (where != null) {
            int len = where.size();
            for (int i = 0; i < len; i++) {
                View v2 = where.get(i).view;
                if (!v2.isRootNamespace() && (v = v2.findViewById(id)) != null) {
                    return v;
                }
            }
            return null;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public View findViewWithTagTraversal(Object tag) {
        View v = super.findViewWithTagTraversal(tag);
        if (v == null) {
            View v2 = findViewWithTagInHeadersOrFooters(this.mHeaderViewInfos, tag);
            if (v2 != null) {
                return v2;
            }
            v = findViewWithTagInHeadersOrFooters(this.mFooterViewInfos, tag);
            if (v != null) {
                return v;
            }
        }
        return v;
    }

    View findViewWithTagInHeadersOrFooters(ArrayList<FixedViewInfo> where, Object tag) {
        View v;
        if (where != null) {
            int len = where.size();
            for (int i = 0; i < len; i++) {
                View v2 = where.get(i).view;
                if (!v2.isRootNamespace() && (v = v2.findViewWithTag(tag)) != null) {
                    return v;
                }
            }
            return null;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public View findViewByPredicateTraversal(Predicate<View> predicate, View childToSkip) {
        View v = super.findViewByPredicateTraversal(predicate, childToSkip);
        if (v == null) {
            View v2 = findViewByPredicateInHeadersOrFooters(this.mHeaderViewInfos, predicate, childToSkip);
            if (v2 != null) {
                return v2;
            }
            v = findViewByPredicateInHeadersOrFooters(this.mFooterViewInfos, predicate, childToSkip);
            if (v != null) {
                return v;
            }
        }
        return v;
    }

    View findViewByPredicateInHeadersOrFooters(ArrayList<FixedViewInfo> where, Predicate<View> predicate, View childToSkip) {
        View v;
        if (where != null) {
            int len = where.size();
            for (int i = 0; i < len; i++) {
                View v2 = where.get(i).view;
                if (v2 != childToSkip && !v2.isRootNamespace() && (v = v2.findViewByPredicate(predicate)) != null) {
                    return v;
                }
            }
            return null;
        }
        return null;
    }

    @Deprecated
    public long[] getCheckItemIds() {
        if (this.mAdapter != null && this.mAdapter.hasStableIds()) {
            return getCheckedItemIds();
        }
        if (this.mChoiceMode != 0 && this.mCheckStates != null && this.mAdapter != null) {
            SparseBooleanArray states = this.mCheckStates;
            int count = states.size();
            long[] ids = new long[count];
            ListAdapter adapter = this.mAdapter;
            int checkedCount = 0;
            for (int i = 0; i < count; i++) {
                if (states.valueAt(i)) {
                    int i2 = checkedCount;
                    checkedCount++;
                    ids[i2] = adapter.getItemId(states.keyAt(i));
                }
            }
            if (checkedCount == count) {
                return ids;
            }
            long[] result = new long[checkedCount];
            System.arraycopy(ids, 0, result, 0, checkedCount);
            return result;
        }
        return new long[0];
    }

    @Override // android.widget.AbsListView, android.widget.AdapterView, android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(ListView.class.getName());
    }

    @Override // android.widget.AbsListView, android.widget.AdapterView, android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(ListView.class.getName());
        int count = getCount();
        AccessibilityNodeInfo.CollectionInfo collectionInfo = AccessibilityNodeInfo.CollectionInfo.obtain(1, count, false);
        info.setCollectionInfo(collectionInfo);
    }

    @Override // android.widget.AbsListView
    public void onInitializeAccessibilityNodeInfoForItem(View view, int position, AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfoForItem(view, position, info);
        AbsListView.LayoutParams lp = (AbsListView.LayoutParams) view.getLayoutParams();
        boolean isHeading = (lp == null || lp.viewType == -2) ? false : true;
        AccessibilityNodeInfo.CollectionItemInfo itemInfo = AccessibilityNodeInfo.CollectionItemInfo.obtain(0, 1, position, 1, isHeading);
        info.setCollectionItemInfo(itemInfo);
    }
}