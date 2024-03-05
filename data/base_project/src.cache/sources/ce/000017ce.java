package android.widget;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Trace;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.RemotableViewMethod;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.GridLayoutAnimationController;
import android.widget.AbsListView;
import android.widget.RemoteViews;
import com.android.internal.R;

@RemoteViews.RemoteView
/* loaded from: GridView.class */
public class GridView extends AbsListView {
    public static final int NO_STRETCH = 0;
    public static final int STRETCH_SPACING = 1;
    public static final int STRETCH_COLUMN_WIDTH = 2;
    public static final int STRETCH_SPACING_UNIFORM = 3;
    public static final int AUTO_FIT = -1;
    private int mNumColumns;
    private int mHorizontalSpacing;
    private int mRequestedHorizontalSpacing;
    private int mVerticalSpacing;
    private int mStretchMode;
    private int mColumnWidth;
    private int mRequestedColumnWidth;
    private int mRequestedNumColumns;
    private View mReferenceView;
    private View mReferenceViewInSelectedRow;
    private int mGravity;
    private final Rect mTempRect;

    public GridView(Context context) {
        this(context, null);
    }

    public GridView(Context context, AttributeSet attrs) {
        this(context, attrs, 16842865);
    }

    public GridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mNumColumns = -1;
        this.mHorizontalSpacing = 0;
        this.mVerticalSpacing = 0;
        this.mStretchMode = 2;
        this.mReferenceView = null;
        this.mReferenceViewInSelectedRow = null;
        this.mGravity = 8388611;
        this.mTempRect = new Rect();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GridView, defStyle, 0);
        int hSpacing = a.getDimensionPixelOffset(1, 0);
        setHorizontalSpacing(hSpacing);
        int vSpacing = a.getDimensionPixelOffset(2, 0);
        setVerticalSpacing(vSpacing);
        int index = a.getInt(3, 2);
        if (index >= 0) {
            setStretchMode(index);
        }
        int columnWidth = a.getDimensionPixelOffset(4, -1);
        if (columnWidth > 0) {
            setColumnWidth(columnWidth);
        }
        int numColumns = a.getInt(5, 1);
        setNumColumns(numColumns);
        int index2 = a.getInt(0, -1);
        if (index2 >= 0) {
            setGravity(index2);
        }
        a.recycle();
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
        this.mAdapter = adapter;
        this.mOldSelectedPosition = -1;
        this.mOldSelectedRowId = Long.MIN_VALUE;
        super.setAdapter(adapter);
        if (this.mAdapter != null) {
            this.mOldItemCount = this.mItemCount;
            this.mItemCount = this.mAdapter.getCount();
            this.mDataChanged = true;
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
            checkSelectionChanged();
        } else {
            checkFocus();
            checkSelectionChanged();
        }
        requestLayout();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.widget.AdapterView
    public int lookForSelectablePosition(int position, boolean lookDown) {
        ListAdapter adapter = this.mAdapter;
        if (adapter == null || isInTouchMode() || position < 0 || position >= this.mItemCount) {
            return -1;
        }
        return position;
    }

    @Override // android.widget.AbsListView
    void fillGap(boolean down) {
        int position;
        int numColumns = this.mNumColumns;
        int verticalSpacing = this.mVerticalSpacing;
        int count = getChildCount();
        if (down) {
            int paddingTop = 0;
            if ((this.mGroupFlags & 34) == 34) {
                paddingTop = getListPaddingTop();
            }
            int startOffset = count > 0 ? getChildAt(count - 1).getBottom() + verticalSpacing : paddingTop;
            int position2 = this.mFirstPosition + count;
            if (this.mStackFromBottom) {
                position2 += numColumns - 1;
            }
            fillDown(position2, startOffset);
            correctTooHigh(numColumns, verticalSpacing, getChildCount());
            return;
        }
        int paddingBottom = 0;
        if ((this.mGroupFlags & 34) == 34) {
            paddingBottom = getListPaddingBottom();
        }
        int startOffset2 = count > 0 ? getChildAt(0).getTop() - verticalSpacing : getHeight() - paddingBottom;
        int position3 = this.mFirstPosition;
        if (!this.mStackFromBottom) {
            position = position3 - numColumns;
        } else {
            position = position3 - 1;
        }
        fillUp(position, startOffset2);
        correctTooLow(numColumns, verticalSpacing, getChildCount());
    }

    private View fillDown(int pos, int nextTop) {
        View selectedView = null;
        int end = this.mBottom - this.mTop;
        if ((this.mGroupFlags & 34) == 34) {
            end -= this.mListPadding.bottom;
        }
        while (nextTop < end && pos < this.mItemCount) {
            View temp = makeRow(pos, nextTop, true);
            if (temp != null) {
                selectedView = temp;
            }
            nextTop = this.mReferenceView.getBottom() + this.mVerticalSpacing;
            pos += this.mNumColumns;
        }
        setVisibleRangeHint(this.mFirstPosition, (this.mFirstPosition + getChildCount()) - 1);
        return selectedView;
    }

    private View makeRow(int startPos, int y, boolean flow) {
        int nextLeft;
        int last;
        int columnWidth = this.mColumnWidth;
        int horizontalSpacing = this.mHorizontalSpacing;
        boolean isLayoutRtl = isLayoutRtl();
        if (isLayoutRtl) {
            nextLeft = ((getWidth() - this.mListPadding.right) - columnWidth) - (this.mStretchMode == 3 ? horizontalSpacing : 0);
        } else {
            nextLeft = this.mListPadding.left + (this.mStretchMode == 3 ? horizontalSpacing : 0);
        }
        if (!this.mStackFromBottom) {
            last = Math.min(startPos + this.mNumColumns, this.mItemCount);
        } else {
            last = startPos + 1;
            startPos = Math.max(0, (startPos - this.mNumColumns) + 1);
            if (last - startPos < this.mNumColumns) {
                int deltaLeft = (this.mNumColumns - (last - startPos)) * (columnWidth + horizontalSpacing);
                nextLeft += (isLayoutRtl ? -1 : 1) * deltaLeft;
            }
        }
        View selectedView = null;
        boolean hasFocus = shouldShowSelector();
        boolean inClick = touchModeDrawsInPressedState();
        int selectedPosition = this.mSelectedPosition;
        View child = null;
        int pos = startPos;
        while (pos < last) {
            boolean selected = pos == selectedPosition;
            int where = flow ? -1 : pos - startPos;
            child = makeAndAddView(pos, y, flow, nextLeft, selected, where);
            nextLeft += (isLayoutRtl ? -1 : 1) * columnWidth;
            if (pos < last - 1) {
                nextLeft += horizontalSpacing;
            }
            if (selected && (hasFocus || inClick)) {
                selectedView = child;
            }
            pos++;
        }
        this.mReferenceView = child;
        if (selectedView != null) {
            this.mReferenceViewInSelectedRow = this.mReferenceView;
        }
        return selectedView;
    }

    private View fillUp(int pos, int nextBottom) {
        View selectedView = null;
        int end = 0;
        if ((this.mGroupFlags & 34) == 34) {
            end = this.mListPadding.top;
        }
        while (nextBottom > end && pos >= 0) {
            View temp = makeRow(pos, nextBottom, false);
            if (temp != null) {
                selectedView = temp;
            }
            nextBottom = this.mReferenceView.getTop() - this.mVerticalSpacing;
            this.mFirstPosition = pos;
            pos -= this.mNumColumns;
        }
        if (this.mStackFromBottom) {
            this.mFirstPosition = Math.max(0, pos + 1);
        }
        setVisibleRangeHint(this.mFirstPosition, (this.mFirstPosition + getChildCount()) - 1);
        return selectedView;
    }

    private View fillFromTop(int nextTop) {
        this.mFirstPosition = Math.min(this.mFirstPosition, this.mSelectedPosition);
        this.mFirstPosition = Math.min(this.mFirstPosition, this.mItemCount - 1);
        if (this.mFirstPosition < 0) {
            this.mFirstPosition = 0;
        }
        this.mFirstPosition -= this.mFirstPosition % this.mNumColumns;
        return fillDown(this.mFirstPosition, nextTop);
    }

    private View fillFromBottom(int lastPosition, int nextBottom) {
        int invertedPosition = (this.mItemCount - 1) - Math.min(Math.max(lastPosition, this.mSelectedPosition), this.mItemCount - 1);
        int lastPosition2 = (this.mItemCount - 1) - (invertedPosition - (invertedPosition % this.mNumColumns));
        return fillUp(lastPosition2, nextBottom);
    }

    private View fillSelection(int childrenTop, int childrenBottom) {
        int rowStart;
        int selectedPosition = reconcileSelectedPosition();
        int numColumns = this.mNumColumns;
        int verticalSpacing = this.mVerticalSpacing;
        int rowEnd = -1;
        if (!this.mStackFromBottom) {
            rowStart = selectedPosition - (selectedPosition % numColumns);
        } else {
            int invertedSelection = (this.mItemCount - 1) - selectedPosition;
            rowEnd = (this.mItemCount - 1) - (invertedSelection - (invertedSelection % numColumns));
            rowStart = Math.max(0, (rowEnd - numColumns) + 1);
        }
        int fadingEdgeLength = getVerticalFadingEdgeLength();
        int topSelectionPixel = getTopSelectionPixel(childrenTop, fadingEdgeLength, rowStart);
        View sel = makeRow(this.mStackFromBottom ? rowEnd : rowStart, topSelectionPixel, true);
        this.mFirstPosition = rowStart;
        View referenceView = this.mReferenceView;
        if (!this.mStackFromBottom) {
            fillDown(rowStart + numColumns, referenceView.getBottom() + verticalSpacing);
            pinToBottom(childrenBottom);
            fillUp(rowStart - numColumns, referenceView.getTop() - verticalSpacing);
            adjustViewsUpOrDown();
        } else {
            int bottomSelectionPixel = getBottomSelectionPixel(childrenBottom, fadingEdgeLength, numColumns, rowStart);
            int offset = bottomSelectionPixel - referenceView.getBottom();
            offsetChildrenTopAndBottom(offset);
            fillUp(rowStart - 1, referenceView.getTop() - verticalSpacing);
            pinToTop(childrenTop);
            fillDown(rowEnd + numColumns, referenceView.getBottom() + verticalSpacing);
            adjustViewsUpOrDown();
        }
        return sel;
    }

    private void pinToTop(int childrenTop) {
        if (this.mFirstPosition == 0) {
            int top = getChildAt(0).getTop();
            int offset = childrenTop - top;
            if (offset < 0) {
                offsetChildrenTopAndBottom(offset);
            }
        }
    }

    private void pinToBottom(int childrenBottom) {
        int count = getChildCount();
        if (this.mFirstPosition + count == this.mItemCount) {
            int bottom = getChildAt(count - 1).getBottom();
            int offset = childrenBottom - bottom;
            if (offset > 0) {
                offsetChildrenTopAndBottom(offset);
            }
        }
    }

    @Override // android.widget.AbsListView
    int findMotionRow(int y) {
        int childCount = getChildCount();
        if (childCount > 0) {
            int numColumns = this.mNumColumns;
            if (!this.mStackFromBottom) {
                int i = 0;
                while (true) {
                    int i2 = i;
                    if (i2 < childCount) {
                        if (y > getChildAt(i2).getBottom()) {
                            i = i2 + numColumns;
                        } else {
                            return this.mFirstPosition + i2;
                        }
                    } else {
                        return -1;
                    }
                }
            } else {
                int i3 = childCount;
                int i4 = 1;
                while (true) {
                    int i5 = i3 - i4;
                    if (i5 >= 0) {
                        if (y < getChildAt(i5).getTop()) {
                            i3 = i5;
                            i4 = numColumns;
                        } else {
                            return this.mFirstPosition + i5;
                        }
                    } else {
                        return -1;
                    }
                }
            }
        } else {
            return -1;
        }
    }

    private View fillSpecific(int position, int top) {
        int motionRowStart;
        View below;
        View above;
        int numColumns = this.mNumColumns;
        int motionRowEnd = -1;
        if (!this.mStackFromBottom) {
            motionRowStart = position - (position % numColumns);
        } else {
            int invertedSelection = (this.mItemCount - 1) - position;
            motionRowEnd = (this.mItemCount - 1) - (invertedSelection - (invertedSelection % numColumns));
            motionRowStart = Math.max(0, (motionRowEnd - numColumns) + 1);
        }
        View temp = makeRow(this.mStackFromBottom ? motionRowEnd : motionRowStart, top, true);
        this.mFirstPosition = motionRowStart;
        View referenceView = this.mReferenceView;
        if (referenceView == null) {
            return null;
        }
        int verticalSpacing = this.mVerticalSpacing;
        if (!this.mStackFromBottom) {
            above = fillUp(motionRowStart - numColumns, referenceView.getTop() - verticalSpacing);
            adjustViewsUpOrDown();
            below = fillDown(motionRowStart + numColumns, referenceView.getBottom() + verticalSpacing);
            int childCount = getChildCount();
            if (childCount > 0) {
                correctTooHigh(numColumns, verticalSpacing, childCount);
            }
        } else {
            below = fillDown(motionRowEnd + numColumns, referenceView.getBottom() + verticalSpacing);
            adjustViewsUpOrDown();
            above = fillUp(motionRowStart - 1, referenceView.getTop() - verticalSpacing);
            int childCount2 = getChildCount();
            if (childCount2 > 0) {
                correctTooLow(numColumns, verticalSpacing, childCount2);
            }
        }
        if (temp != null) {
            return temp;
        }
        if (above != null) {
            return above;
        }
        return below;
    }

    private void correctTooHigh(int numColumns, int verticalSpacing, int childCount) {
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
                        fillUp(this.mFirstPosition - (this.mStackFromBottom ? 1 : numColumns), firstChild.getTop() - verticalSpacing);
                        adjustViewsUpOrDown();
                    }
                }
            }
        }
    }

    private void correctTooLow(int numColumns, int verticalSpacing, int childCount) {
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
                        fillDown(lastPosition + (!this.mStackFromBottom ? 1 : numColumns), lastChild.getBottom() + verticalSpacing);
                        adjustViewsUpOrDown();
                    }
                }
            }
        }
    }

    private View fillFromSelection(int selectedTop, int childrenTop, int childrenBottom) {
        int rowStart;
        int fadingEdgeLength = getVerticalFadingEdgeLength();
        int selectedPosition = this.mSelectedPosition;
        int numColumns = this.mNumColumns;
        int verticalSpacing = this.mVerticalSpacing;
        int rowEnd = -1;
        if (!this.mStackFromBottom) {
            rowStart = selectedPosition - (selectedPosition % numColumns);
        } else {
            int invertedSelection = (this.mItemCount - 1) - selectedPosition;
            rowEnd = (this.mItemCount - 1) - (invertedSelection - (invertedSelection % numColumns));
            rowStart = Math.max(0, (rowEnd - numColumns) + 1);
        }
        int topSelectionPixel = getTopSelectionPixel(childrenTop, fadingEdgeLength, rowStart);
        int bottomSelectionPixel = getBottomSelectionPixel(childrenBottom, fadingEdgeLength, numColumns, rowStart);
        View sel = makeRow(this.mStackFromBottom ? rowEnd : rowStart, selectedTop, true);
        this.mFirstPosition = rowStart;
        View referenceView = this.mReferenceView;
        adjustForTopFadingEdge(referenceView, topSelectionPixel, bottomSelectionPixel);
        adjustForBottomFadingEdge(referenceView, topSelectionPixel, bottomSelectionPixel);
        if (!this.mStackFromBottom) {
            fillUp(rowStart - numColumns, referenceView.getTop() - verticalSpacing);
            adjustViewsUpOrDown();
            fillDown(rowStart + numColumns, referenceView.getBottom() + verticalSpacing);
        } else {
            fillDown(rowEnd + numColumns, referenceView.getBottom() + verticalSpacing);
            adjustViewsUpOrDown();
            fillUp(rowStart - 1, referenceView.getTop() - verticalSpacing);
        }
        return sel;
    }

    private int getBottomSelectionPixel(int childrenBottom, int fadingEdgeLength, int numColumns, int rowStart) {
        int bottomSelectionPixel = childrenBottom;
        if ((rowStart + numColumns) - 1 < this.mItemCount - 1) {
            bottomSelectionPixel -= fadingEdgeLength;
        }
        return bottomSelectionPixel;
    }

    private int getTopSelectionPixel(int childrenTop, int fadingEdgeLength, int rowStart) {
        int topSelectionPixel = childrenTop;
        if (rowStart > 0) {
            topSelectionPixel += fadingEdgeLength;
        }
        return topSelectionPixel;
    }

    private void adjustForBottomFadingEdge(View childInSelectedRow, int topSelectionPixel, int bottomSelectionPixel) {
        if (childInSelectedRow.getBottom() > bottomSelectionPixel) {
            int spaceAbove = childInSelectedRow.getTop() - topSelectionPixel;
            int spaceBelow = childInSelectedRow.getBottom() - bottomSelectionPixel;
            int offset = Math.min(spaceAbove, spaceBelow);
            offsetChildrenTopAndBottom(-offset);
        }
    }

    private void adjustForTopFadingEdge(View childInSelectedRow, int topSelectionPixel, int bottomSelectionPixel) {
        if (childInSelectedRow.getTop() < topSelectionPixel) {
            int spaceAbove = topSelectionPixel - childInSelectedRow.getTop();
            int spaceBelow = bottomSelectionPixel - childInSelectedRow.getBottom();
            int offset = Math.min(spaceAbove, spaceBelow);
            offsetChildrenTopAndBottom(offset);
        }
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

    private View moveSelection(int delta, int childrenTop, int childrenBottom) {
        int rowStart;
        int oldRowStart;
        View sel;
        View referenceView;
        int fadingEdgeLength = getVerticalFadingEdgeLength();
        int selectedPosition = this.mSelectedPosition;
        int numColumns = this.mNumColumns;
        int verticalSpacing = this.mVerticalSpacing;
        int rowEnd = -1;
        if (!this.mStackFromBottom) {
            oldRowStart = (selectedPosition - delta) - ((selectedPosition - delta) % numColumns);
            rowStart = selectedPosition - (selectedPosition % numColumns);
        } else {
            int invertedSelection = (this.mItemCount - 1) - selectedPosition;
            rowEnd = (this.mItemCount - 1) - (invertedSelection - (invertedSelection % numColumns));
            rowStart = Math.max(0, (rowEnd - numColumns) + 1);
            int invertedSelection2 = (this.mItemCount - 1) - (selectedPosition - delta);
            int oldRowStart2 = (this.mItemCount - 1) - (invertedSelection2 - (invertedSelection2 % numColumns));
            oldRowStart = Math.max(0, (oldRowStart2 - numColumns) + 1);
        }
        int rowDelta = rowStart - oldRowStart;
        int topSelectionPixel = getTopSelectionPixel(childrenTop, fadingEdgeLength, rowStart);
        int bottomSelectionPixel = getBottomSelectionPixel(childrenBottom, fadingEdgeLength, numColumns, rowStart);
        this.mFirstPosition = rowStart;
        if (rowDelta > 0) {
            int oldBottom = this.mReferenceViewInSelectedRow == null ? 0 : this.mReferenceViewInSelectedRow.getBottom();
            sel = makeRow(this.mStackFromBottom ? rowEnd : rowStart, oldBottom + verticalSpacing, true);
            referenceView = this.mReferenceView;
            adjustForBottomFadingEdge(referenceView, topSelectionPixel, bottomSelectionPixel);
        } else if (rowDelta < 0) {
            int oldTop = this.mReferenceViewInSelectedRow == null ? 0 : this.mReferenceViewInSelectedRow.getTop();
            sel = makeRow(this.mStackFromBottom ? rowEnd : rowStart, oldTop - verticalSpacing, false);
            referenceView = this.mReferenceView;
            adjustForTopFadingEdge(referenceView, topSelectionPixel, bottomSelectionPixel);
        } else {
            int oldTop2 = this.mReferenceViewInSelectedRow == null ? 0 : this.mReferenceViewInSelectedRow.getTop();
            sel = makeRow(this.mStackFromBottom ? rowEnd : rowStart, oldTop2, true);
            referenceView = this.mReferenceView;
        }
        if (!this.mStackFromBottom) {
            fillUp(rowStart - numColumns, referenceView.getTop() - verticalSpacing);
            adjustViewsUpOrDown();
            fillDown(rowStart + numColumns, referenceView.getBottom() + verticalSpacing);
        } else {
            fillDown(rowEnd + numColumns, referenceView.getBottom() + verticalSpacing);
            adjustViewsUpOrDown();
            fillUp(rowStart - 1, referenceView.getTop() - verticalSpacing);
        }
        return sel;
    }

    private boolean determineColumns(int availableSpace) {
        int requestedHorizontalSpacing = this.mRequestedHorizontalSpacing;
        int stretchMode = this.mStretchMode;
        int requestedColumnWidth = this.mRequestedColumnWidth;
        boolean didNotInitiallyFit = false;
        if (this.mRequestedNumColumns == -1) {
            if (requestedColumnWidth > 0) {
                this.mNumColumns = (availableSpace + requestedHorizontalSpacing) / (requestedColumnWidth + requestedHorizontalSpacing);
            } else {
                this.mNumColumns = 2;
            }
        } else {
            this.mNumColumns = this.mRequestedNumColumns;
        }
        if (this.mNumColumns <= 0) {
            this.mNumColumns = 1;
        }
        switch (stretchMode) {
            case 0:
                this.mColumnWidth = requestedColumnWidth;
                this.mHorizontalSpacing = requestedHorizontalSpacing;
                break;
            default:
                int spaceLeftOver = (availableSpace - (this.mNumColumns * requestedColumnWidth)) - ((this.mNumColumns - 1) * requestedHorizontalSpacing);
                if (spaceLeftOver < 0) {
                    didNotInitiallyFit = true;
                }
                switch (stretchMode) {
                    case 1:
                        this.mColumnWidth = requestedColumnWidth;
                        if (this.mNumColumns > 1) {
                            this.mHorizontalSpacing = requestedHorizontalSpacing + (spaceLeftOver / (this.mNumColumns - 1));
                            break;
                        } else {
                            this.mHorizontalSpacing = requestedHorizontalSpacing + spaceLeftOver;
                            break;
                        }
                    case 2:
                        this.mColumnWidth = requestedColumnWidth + (spaceLeftOver / this.mNumColumns);
                        this.mHorizontalSpacing = requestedHorizontalSpacing;
                        break;
                    case 3:
                        this.mColumnWidth = requestedColumnWidth;
                        if (this.mNumColumns > 1) {
                            this.mHorizontalSpacing = requestedHorizontalSpacing + (spaceLeftOver / (this.mNumColumns + 1));
                            break;
                        } else {
                            this.mHorizontalSpacing = requestedHorizontalSpacing + spaceLeftOver;
                            break;
                        }
                }
        }
        return didNotInitiallyFit;
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
        if (widthMode == 0) {
            if (this.mColumnWidth > 0) {
                widthSize = this.mColumnWidth + this.mListPadding.left + this.mListPadding.right;
            } else {
                widthSize = this.mListPadding.left + this.mListPadding.right;
            }
            widthSize2 = widthSize + getVerticalScrollbarWidth();
        }
        int childWidth = (widthSize2 - this.mListPadding.left) - this.mListPadding.right;
        boolean didNotInitiallyFit = determineColumns(childWidth);
        int childHeight = 0;
        this.mItemCount = this.mAdapter == null ? 0 : this.mAdapter.getCount();
        int count = this.mItemCount;
        if (count > 0) {
            View child = obtainView(0, this.mIsScrap);
            AbsListView.LayoutParams p = (AbsListView.LayoutParams) child.getLayoutParams();
            if (p == null) {
                p = (AbsListView.LayoutParams) generateDefaultLayoutParams();
                child.setLayoutParams(p);
            }
            p.viewType = this.mAdapter.getItemViewType(0);
            p.forceAdd = true;
            int childHeightSpec = getChildMeasureSpec(View.MeasureSpec.makeMeasureSpec(0, 0), 0, p.height);
            int childWidthSpec = getChildMeasureSpec(View.MeasureSpec.makeMeasureSpec(this.mColumnWidth, 1073741824), 0, p.width);
            child.measure(childWidthSpec, childHeightSpec);
            childHeight = child.getMeasuredHeight();
            combineMeasuredStates(0, child.getMeasuredState());
            if (this.mRecycler.shouldRecycleViewType(p.viewType)) {
                this.mRecycler.addScrapView(child, -1);
            }
        }
        if (heightMode == 0) {
            heightSize = this.mListPadding.top + this.mListPadding.bottom + childHeight + (getVerticalFadingEdgeLength() * 2);
        }
        if (heightMode == Integer.MIN_VALUE) {
            int ourSize = this.mListPadding.top + this.mListPadding.bottom;
            int numColumns = this.mNumColumns;
            int i = 0;
            while (true) {
                int i2 = i;
                if (i2 >= count) {
                    break;
                }
                ourSize += childHeight;
                if (i2 + numColumns < count) {
                    ourSize += this.mVerticalSpacing;
                }
                if (ourSize < heightSize) {
                    i = i2 + numColumns;
                } else {
                    ourSize = heightSize;
                    break;
                }
            }
            heightSize = ourSize;
        }
        if (widthMode == Integer.MIN_VALUE && this.mRequestedNumColumns != -1) {
            int ourSize2 = (this.mRequestedNumColumns * this.mColumnWidth) + ((this.mRequestedNumColumns - 1) * this.mHorizontalSpacing) + this.mListPadding.left + this.mListPadding.right;
            if (ourSize2 > widthSize2 || didNotInitiallyFit) {
                widthSize2 |= 16777216;
            }
        }
        setMeasuredDimension(widthSize2, heightSize);
        this.mWidthMeasureSpec = widthMeasureSpec;
    }

    @Override // android.view.ViewGroup
    protected void attachLayoutAnimationParameters(View child, ViewGroup.LayoutParams params, int index, int count) {
        GridLayoutAnimationController.AnimationParameters animationParams = (GridLayoutAnimationController.AnimationParameters) params.layoutAnimationParameters;
        if (animationParams == null) {
            animationParams = new GridLayoutAnimationController.AnimationParameters();
            params.layoutAnimationParameters = animationParams;
        }
        animationParams.count = count;
        animationParams.index = index;
        animationParams.columnsCount = this.mNumColumns;
        animationParams.rowsCount = count / this.mNumColumns;
        if (!this.mStackFromBottom) {
            animationParams.column = index % this.mNumColumns;
            animationParams.row = index / this.mNumColumns;
            return;
        }
        int invertedIndex = (count - 1) - index;
        animationParams.column = (this.mNumColumns - 1) - (invertedIndex % this.mNumColumns);
        animationParams.row = (animationParams.rowsCount - 1) - (invertedIndex / this.mNumColumns);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.AbsListView
    public void layoutChildren() {
        View sel;
        boolean blockLayoutRequests = this.mBlockLayoutRequests;
        if (!blockLayoutRequests) {
            this.mBlockLayoutRequests = true;
        }
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
            int delta = 0;
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
                    if (this.mNextSelectedPosition >= 0) {
                        delta = this.mNextSelectedPosition - this.mSelectedPosition;
                        break;
                    }
                    break;
                default:
                    int index2 = this.mSelectedPosition - this.mFirstPosition;
                    if (index2 >= 0 && index2 < childCount) {
                        oldSel = getChildAt(index2);
                    }
                    oldFirst = getChildAt(0);
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
                return;
            }
            setSelectedPositionInt(this.mNextSelectedPosition);
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
                        sel = fillSelection(childrenTop, childrenBottom);
                        break;
                    }
                case 3:
                    sel = fillUp(this.mItemCount - 1, childrenBottom);
                    adjustViewsUpOrDown();
                    break;
                case 4:
                    sel = fillSpecific(this.mSelectedPosition, this.mSpecificTop);
                    break;
                case 5:
                    sel = fillSpecific(this.mSyncPosition, this.mSpecificTop);
                    break;
                case 6:
                    sel = moveSelection(delta, childrenTop, childrenBottom);
                    break;
                default:
                    if (childCount == 0) {
                        if (this.mStackFromBottom) {
                            int last = this.mItemCount - 1;
                            setSelectedPositionInt((this.mAdapter == null || isInTouchMode()) ? -1 : last);
                            sel = fillFromBottom(last, childrenBottom);
                            break;
                        } else {
                            setSelectedPositionInt((this.mAdapter == null || isInTouchMode()) ? -1 : 0);
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
                positionSelector(-1, sel);
                this.mSelectedTop = sel.getTop();
            } else if (this.mTouchMode <= 0 || this.mTouchMode >= 3) {
                this.mSelectedTop = 0;
                this.mSelectorRect.setEmpty();
            } else {
                View child = getChildAt(this.mMotionPosition - this.mFirstPosition);
                if (child != null) {
                    positionSelector(this.mMotionPosition, child);
                }
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
        } catch (Throwable th) {
            if (!blockLayoutRequests) {
                this.mBlockLayoutRequests = false;
            }
            throw th;
        }
    }

    private View makeAndAddView(int position, int y, boolean flow, int childrenLeft, boolean selected, int where) {
        View child;
        if (!this.mDataChanged && (child = this.mRecycler.getActiveView(position)) != null) {
            setupChild(child, position, y, flow, childrenLeft, selected, true, where);
            return child;
        }
        View child2 = obtainView(position, this.mIsScrap);
        setupChild(child2, position, y, flow, childrenLeft, selected, this.mIsScrap[0], where);
        return child2;
    }

    private void setupChild(View child, int position, int y, boolean flow, int childrenLeft, boolean selected, boolean recycled, int where) {
        int childLeft;
        Trace.traceBegin(8L, "setupGridItem");
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
        if (recycled && !p.forceAdd) {
            attachViewToParent(child, where, p);
        } else {
            p.forceAdd = false;
            addViewInLayout(child, where, p, true);
        }
        if (updateChildSelected) {
            child.setSelected(isSelected);
            if (isSelected) {
                requestFocus();
            }
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
            int childHeightSpec = ViewGroup.getChildMeasureSpec(View.MeasureSpec.makeMeasureSpec(0, 0), 0, p.height);
            int childWidthSpec = ViewGroup.getChildMeasureSpec(View.MeasureSpec.makeMeasureSpec(this.mColumnWidth, 1073741824), 0, p.width);
            child.measure(childWidthSpec, childHeightSpec);
        } else {
            cleanupLayoutState(child);
        }
        int w = child.getMeasuredWidth();
        int h = child.getMeasuredHeight();
        int childTop = flow ? y : y - h;
        int layoutDirection = getLayoutDirection();
        int absoluteGravity = Gravity.getAbsoluteGravity(this.mGravity, layoutDirection);
        switch (absoluteGravity & 7) {
            case 1:
                childLeft = childrenLeft + ((this.mColumnWidth - w) / 2);
                break;
            case 2:
            case 4:
            default:
                childLeft = childrenLeft;
                break;
            case 3:
                childLeft = childrenLeft;
                break;
            case 5:
                childLeft = (childrenLeft + this.mColumnWidth) - w;
                break;
        }
        if (needToMeasure) {
            int childRight = childLeft + w;
            int childBottom = childTop + h;
            child.layout(childLeft, childTop, childRight, childBottom);
        } else {
            child.offsetLeftAndRight(childLeft - child.getLeft());
            child.offsetTopAndBottom(childTop - child.getTop());
        }
        if (this.mCachingStarted) {
            child.setDrawingCacheEnabled(true);
        }
        if (recycled && ((AbsListView.LayoutParams) child.getLayoutParams()).scrappedFromPosition != position) {
            child.jumpDrawablesToCurrentState();
        }
        Trace.traceEnd(8L);
    }

    @Override // android.widget.AdapterView
    public void setSelection(int position) {
        if (!isInTouchMode()) {
            setNextSelectedPositionInt(position);
        } else {
            this.mResurrectToPosition = position;
        }
        this.mLayoutMode = 2;
        if (this.mPositionScroller != null) {
            this.mPositionScroller.stop();
        }
        requestLayout();
    }

    @Override // android.widget.AbsListView
    void setSelectionInt(int position) {
        int previousSelectedPosition = this.mNextSelectedPosition;
        if (this.mPositionScroller != null) {
            this.mPositionScroller.stop();
        }
        setNextSelectedPositionInt(position);
        layoutChildren();
        int next = this.mStackFromBottom ? (this.mItemCount - 1) - this.mNextSelectedPosition : this.mNextSelectedPosition;
        int previous = this.mStackFromBottom ? (this.mItemCount - 1) - previousSelectedPosition : previousSelectedPosition;
        int nextRow = next / this.mNumColumns;
        int previousRow = previous / this.mNumColumns;
        if (nextRow != previousRow) {
            awakenScrollBars();
        }
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
        if (this.mAdapter == null) {
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
                        handled = resurrectSelectionIfNeeded() || arrowScroll(33);
                        break;
                    } else if (event.hasModifiers(2)) {
                        handled = resurrectSelectionIfNeeded() || fullScroll(33);
                        break;
                    }
                    break;
                case 20:
                    if (event.hasNoModifiers()) {
                        handled = resurrectSelectionIfNeeded() || arrowScroll(130);
                        break;
                    } else if (event.hasModifiers(2)) {
                        handled = resurrectSelectionIfNeeded() || fullScroll(130);
                        break;
                    }
                    break;
                case 21:
                    if (event.hasNoModifiers()) {
                        handled = resurrectSelectionIfNeeded() || arrowScroll(17);
                        break;
                    }
                    break;
                case 22:
                    if (event.hasNoModifiers()) {
                        handled = resurrectSelectionIfNeeded() || arrowScroll(66);
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
                            handled = resurrectSelectionIfNeeded() || pageScroll(130);
                            break;
                        } else if (event.hasModifiers(1)) {
                            handled = resurrectSelectionIfNeeded() || pageScroll(33);
                            break;
                        }
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
        int nextPage = -1;
        if (direction == 33) {
            nextPage = Math.max(0, this.mSelectedPosition - getChildCount());
        } else if (direction == 130) {
            nextPage = Math.min(this.mItemCount - 1, this.mSelectedPosition + getChildCount());
        }
        if (nextPage >= 0) {
            setSelectionInt(nextPage);
            invokeOnItemScrollListener();
            awakenScrollBars();
            return true;
        }
        return false;
    }

    boolean fullScroll(int direction) {
        boolean moved = false;
        if (direction == 33) {
            this.mLayoutMode = 2;
            setSelectionInt(0);
            invokeOnItemScrollListener();
            moved = true;
        } else if (direction == 130) {
            this.mLayoutMode = 2;
            setSelectionInt(this.mItemCount - 1);
            invokeOnItemScrollListener();
            moved = true;
        }
        if (moved) {
            awakenScrollBars();
        }
        return moved;
    }

    boolean arrowScroll(int direction) {
        int endOfRowPos;
        int startOfRowPos;
        int selectedPosition = this.mSelectedPosition;
        int numColumns = this.mNumColumns;
        boolean moved = false;
        if (!this.mStackFromBottom) {
            startOfRowPos = (selectedPosition / numColumns) * numColumns;
            endOfRowPos = Math.min((startOfRowPos + numColumns) - 1, this.mItemCount - 1);
        } else {
            int invertedSelection = (this.mItemCount - 1) - selectedPosition;
            endOfRowPos = (this.mItemCount - 1) - ((invertedSelection / numColumns) * numColumns);
            startOfRowPos = Math.max(0, (endOfRowPos - numColumns) + 1);
        }
        switch (direction) {
            case 17:
                if (selectedPosition > startOfRowPos) {
                    this.mLayoutMode = 6;
                    setSelectionInt(Math.max(0, selectedPosition - 1));
                    moved = true;
                    break;
                }
                break;
            case 33:
                if (startOfRowPos > 0) {
                    this.mLayoutMode = 6;
                    setSelectionInt(Math.max(0, selectedPosition - numColumns));
                    moved = true;
                    break;
                }
                break;
            case 66:
                if (selectedPosition < endOfRowPos) {
                    this.mLayoutMode = 6;
                    setSelectionInt(Math.min(selectedPosition + 1, this.mItemCount - 1));
                    moved = true;
                    break;
                }
                break;
            case 130:
                if (endOfRowPos < this.mItemCount - 1) {
                    this.mLayoutMode = 6;
                    setSelectionInt(Math.min(selectedPosition + numColumns, this.mItemCount - 1));
                    moved = true;
                    break;
                }
                break;
        }
        if (moved) {
            playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction));
            invokeOnItemScrollListener();
        }
        if (moved) {
            awakenScrollBars();
        }
        return moved;
    }

    boolean sequenceScroll(int direction) {
        int endOfRow;
        int startOfRow;
        int selectedPosition = this.mSelectedPosition;
        int numColumns = this.mNumColumns;
        int count = this.mItemCount;
        if (!this.mStackFromBottom) {
            startOfRow = (selectedPosition / numColumns) * numColumns;
            endOfRow = Math.min((startOfRow + numColumns) - 1, count - 1);
        } else {
            int invertedSelection = (count - 1) - selectedPosition;
            endOfRow = (count - 1) - ((invertedSelection / numColumns) * numColumns);
            startOfRow = Math.max(0, (endOfRow - numColumns) + 1);
        }
        boolean moved = false;
        boolean showScroll = false;
        switch (direction) {
            case 1:
                if (selectedPosition > 0) {
                    this.mLayoutMode = 6;
                    setSelectionInt(selectedPosition - 1);
                    moved = true;
                    showScroll = selectedPosition == startOfRow;
                    break;
                }
                break;
            case 2:
                if (selectedPosition < count - 1) {
                    this.mLayoutMode = 6;
                    setSelectionInt(selectedPosition + 1);
                    moved = true;
                    showScroll = selectedPosition == endOfRow;
                    break;
                }
                break;
        }
        if (moved) {
            playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction));
            invokeOnItemScrollListener();
        }
        if (showScroll) {
            awakenScrollBars();
        }
        return moved;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.AbsListView, android.view.View
    public void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        int closestChildIndex = -1;
        if (gainFocus && previouslyFocusedRect != null) {
            previouslyFocusedRect.offset(this.mScrollX, this.mScrollY);
            Rect otherRect = this.mTempRect;
            int minDistance = Integer.MAX_VALUE;
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                if (isCandidateSelection(i, direction)) {
                    View other = getChildAt(i);
                    other.getDrawingRect(otherRect);
                    offsetDescendantRectToMyCoords(other, otherRect);
                    int distance = getDistance(previouslyFocusedRect, otherRect, direction);
                    if (distance < minDistance) {
                        minDistance = distance;
                        closestChildIndex = i;
                    }
                }
            }
        }
        if (closestChildIndex >= 0) {
            setSelection(closestChildIndex + this.mFirstPosition);
        } else {
            requestLayout();
        }
    }

    private boolean isCandidateSelection(int childIndex, int direction) {
        int rowEnd;
        int rowStart;
        int count = getChildCount();
        int invertedIndex = (count - 1) - childIndex;
        if (!this.mStackFromBottom) {
            rowStart = childIndex - (childIndex % this.mNumColumns);
            rowEnd = Math.max((rowStart + this.mNumColumns) - 1, count);
        } else {
            rowEnd = (count - 1) - (invertedIndex - (invertedIndex % this.mNumColumns));
            rowStart = Math.max(0, (rowEnd - this.mNumColumns) + 1);
        }
        switch (direction) {
            case 1:
                return childIndex == rowEnd && rowEnd == count - 1;
            case 2:
                return childIndex == rowStart && rowStart == 0;
            case 17:
                return childIndex == rowEnd;
            case 33:
                return rowEnd == count - 1;
            case 66:
                return childIndex == rowStart;
            case 130:
                return rowStart == 0;
            default:
                throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT, FOCUS_FORWARD, FOCUS_BACKWARD}.");
        }
    }

    public void setGravity(int gravity) {
        if (this.mGravity != gravity) {
            this.mGravity = gravity;
            requestLayoutIfNecessary();
        }
    }

    public int getGravity() {
        return this.mGravity;
    }

    public void setHorizontalSpacing(int horizontalSpacing) {
        if (horizontalSpacing != this.mRequestedHorizontalSpacing) {
            this.mRequestedHorizontalSpacing = horizontalSpacing;
            requestLayoutIfNecessary();
        }
    }

    public int getHorizontalSpacing() {
        return this.mHorizontalSpacing;
    }

    public int getRequestedHorizontalSpacing() {
        return this.mRequestedHorizontalSpacing;
    }

    public void setVerticalSpacing(int verticalSpacing) {
        if (verticalSpacing != this.mVerticalSpacing) {
            this.mVerticalSpacing = verticalSpacing;
            requestLayoutIfNecessary();
        }
    }

    public int getVerticalSpacing() {
        return this.mVerticalSpacing;
    }

    public void setStretchMode(int stretchMode) {
        if (stretchMode != this.mStretchMode) {
            this.mStretchMode = stretchMode;
            requestLayoutIfNecessary();
        }
    }

    public int getStretchMode() {
        return this.mStretchMode;
    }

    public void setColumnWidth(int columnWidth) {
        if (columnWidth != this.mRequestedColumnWidth) {
            this.mRequestedColumnWidth = columnWidth;
            requestLayoutIfNecessary();
        }
    }

    public int getColumnWidth() {
        return this.mColumnWidth;
    }

    public int getRequestedColumnWidth() {
        return this.mRequestedColumnWidth;
    }

    public void setNumColumns(int numColumns) {
        if (numColumns != this.mRequestedNumColumns) {
            this.mRequestedNumColumns = numColumns;
            requestLayoutIfNecessary();
        }
    }

    @ViewDebug.ExportedProperty
    public int getNumColumns() {
        return this.mNumColumns;
    }

    private void adjustViewsUpOrDown() {
        int delta;
        int childCount = getChildCount();
        if (childCount > 0) {
            if (!this.mStackFromBottom) {
                View child = getChildAt(0);
                delta = child.getTop() - this.mListPadding.top;
                if (this.mFirstPosition != 0) {
                    delta -= this.mVerticalSpacing;
                }
                if (delta < 0) {
                    delta = 0;
                }
            } else {
                View child2 = getChildAt(childCount - 1);
                delta = child2.getBottom() - (getHeight() - this.mListPadding.bottom);
                if (this.mFirstPosition + childCount < this.mItemCount) {
                    delta += this.mVerticalSpacing;
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

    @Override // android.widget.AbsListView, android.view.View
    protected int computeVerticalScrollExtent() {
        int count = getChildCount();
        if (count > 0) {
            int numColumns = this.mNumColumns;
            int rowCount = ((count + numColumns) - 1) / numColumns;
            int extent = rowCount * 100;
            View view = getChildAt(0);
            int top = view.getTop();
            int height = view.getHeight();
            if (height > 0) {
                extent += (top * 100) / height;
            }
            View view2 = getChildAt(count - 1);
            int bottom = view2.getBottom();
            int height2 = view2.getHeight();
            if (height2 > 0) {
                extent -= ((bottom - getHeight()) * 100) / height2;
            }
            return extent;
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.AbsListView, android.view.View
    public int computeVerticalScrollOffset() {
        if (this.mFirstPosition >= 0 && getChildCount() > 0) {
            View view = getChildAt(0);
            int top = view.getTop();
            int height = view.getHeight();
            if (height > 0) {
                int numColumns = this.mNumColumns;
                int rowCount = ((this.mItemCount + numColumns) - 1) / numColumns;
                int oddItemsOnFirstRow = isStackFromBottom() ? (rowCount * numColumns) - this.mItemCount : 0;
                int whichRow = (this.mFirstPosition + oddItemsOnFirstRow) / numColumns;
                return Math.max(((whichRow * 100) - ((top * 100) / height)) + ((int) ((this.mScrollY / getHeight()) * rowCount * 100.0f)), 0);
            }
            return 0;
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.AbsListView, android.view.View
    public int computeVerticalScrollRange() {
        int numColumns = this.mNumColumns;
        int rowCount = ((this.mItemCount + numColumns) - 1) / numColumns;
        int result = Math.max(rowCount * 100, 0);
        if (this.mScrollY != 0) {
            result += Math.abs((int) ((this.mScrollY / getHeight()) * rowCount * 100.0f));
        }
        return result;
    }

    @Override // android.widget.AbsListView, android.widget.AdapterView, android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(GridView.class.getName());
    }

    @Override // android.widget.AbsListView, android.widget.AdapterView, android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(GridView.class.getName());
        int columnsCount = getNumColumns();
        int rowsCount = getCount() / columnsCount;
        AccessibilityNodeInfo.CollectionInfo collectionInfo = AccessibilityNodeInfo.CollectionInfo.obtain(columnsCount, rowsCount, false);
        info.setCollectionInfo(collectionInfo);
    }

    @Override // android.widget.AbsListView
    public void onInitializeAccessibilityNodeInfoForItem(View view, int position, AccessibilityNodeInfo info) {
        int column;
        int row;
        super.onInitializeAccessibilityNodeInfoForItem(view, position, info);
        int count = getCount();
        int columnsCount = getNumColumns();
        int rowsCount = count / columnsCount;
        if (!this.mStackFromBottom) {
            column = position % columnsCount;
            row = position / columnsCount;
        } else {
            int invertedIndex = (count - 1) - position;
            column = (columnsCount - 1) - (invertedIndex % columnsCount);
            row = (rowsCount - 1) - (invertedIndex / columnsCount);
        }
        AbsListView.LayoutParams lp = (AbsListView.LayoutParams) view.getLayoutParams();
        boolean isHeading = (lp == null || lp.viewType == -2) ? false : true;
        AccessibilityNodeInfo.CollectionItemInfo itemInfo = AccessibilityNodeInfo.CollectionItemInfo.obtain(column, 1, row, 1, isHeading);
        info.setCollectionItemInfo(itemInfo);
    }
}