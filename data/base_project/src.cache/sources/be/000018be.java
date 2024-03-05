package android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.LinearLayout;
import com.android.internal.R;

/* loaded from: TableRow.class */
public class TableRow extends LinearLayout {
    private int mNumColumns;
    private int[] mColumnWidths;
    private int[] mConstrainedColumnWidths;
    private SparseIntArray mColumnToChildIndex;
    private ChildrenTracker mChildrenTracker;

    public TableRow(Context context) {
        super(context);
        this.mNumColumns = 0;
        initTableRow();
    }

    public TableRow(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mNumColumns = 0;
        initTableRow();
    }

    private void initTableRow() {
        ViewGroup.OnHierarchyChangeListener oldListener = this.mOnHierarchyChangeListener;
        this.mChildrenTracker = new ChildrenTracker();
        if (oldListener != null) {
            this.mChildrenTracker.setOnHierarchyChangeListener(oldListener);
        }
        super.setOnHierarchyChangeListener(this.mChildrenTracker);
    }

    @Override // android.view.ViewGroup
    public void setOnHierarchyChangeListener(ViewGroup.OnHierarchyChangeListener listener) {
        this.mChildrenTracker.setOnHierarchyChangeListener(listener);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setColumnCollapsed(int columnIndex, boolean collapsed) {
        View child = getVirtualChildAt(columnIndex);
        if (child != null) {
            child.setVisibility(collapsed ? 8 : 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.LinearLayout, android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureHorizontal(widthMeasureSpec, heightMeasureSpec);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        layoutHorizontal(l, t, r, b);
    }

    @Override // android.widget.LinearLayout
    public View getVirtualChildAt(int i) {
        if (this.mColumnToChildIndex == null) {
            mapIndexAndColumns();
        }
        int deflectedIndex = this.mColumnToChildIndex.get(i, -1);
        if (deflectedIndex != -1) {
            return getChildAt(deflectedIndex);
        }
        return null;
    }

    @Override // android.widget.LinearLayout
    public int getVirtualChildCount() {
        if (this.mColumnToChildIndex == null) {
            mapIndexAndColumns();
        }
        return this.mNumColumns;
    }

    private void mapIndexAndColumns() {
        if (this.mColumnToChildIndex == null) {
            int virtualCount = 0;
            int count = getChildCount();
            this.mColumnToChildIndex = new SparseIntArray();
            SparseIntArray columnToChild = this.mColumnToChildIndex;
            for (int i = 0; i < count; i++) {
                View child = getChildAt(i);
                LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
                if (layoutParams.column >= virtualCount) {
                    virtualCount = layoutParams.column;
                }
                for (int j = 0; j < layoutParams.span; j++) {
                    int i2 = virtualCount;
                    virtualCount++;
                    columnToChild.put(i2, i);
                }
            }
            this.mNumColumns = virtualCount;
        }
    }

    @Override // android.widget.LinearLayout
    int measureNullChild(int childIndex) {
        return this.mConstrainedColumnWidths[childIndex];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.widget.LinearLayout
    public void measureChildBeforeLayout(View child, int childIndex, int widthMeasureSpec, int totalWidth, int heightMeasureSpec, int totalHeight) {
        if (this.mConstrainedColumnWidths != null) {
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            int measureMode = 1073741824;
            int columnWidth = 0;
            int span = lp.span;
            int[] constrainedColumnWidths = this.mConstrainedColumnWidths;
            for (int i = 0; i < span; i++) {
                columnWidth += constrainedColumnWidths[childIndex + i];
            }
            int gravity = lp.gravity;
            boolean isHorizontalGravity = Gravity.isHorizontal(gravity);
            if (isHorizontalGravity) {
                measureMode = Integer.MIN_VALUE;
            }
            int childWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(Math.max(0, (columnWidth - lp.leftMargin) - lp.rightMargin), measureMode);
            int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, this.mPaddingTop + this.mPaddingBottom + lp.topMargin + lp.bottomMargin + totalHeight, lp.height);
            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            if (isHorizontalGravity) {
                int childWidth = child.getMeasuredWidth();
                lp.mOffset[1] = columnWidth - childWidth;
                int layoutDirection = getLayoutDirection();
                int absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection);
                switch (absoluteGravity & 7) {
                    case 1:
                        lp.mOffset[0] = lp.mOffset[1] / 2;
                        return;
                    case 2:
                    case 3:
                    case 4:
                    default:
                        return;
                    case 5:
                        lp.mOffset[0] = lp.mOffset[1];
                        return;
                }
            }
            int[] iArr = lp.mOffset;
            lp.mOffset[1] = 0;
            iArr[0] = 0;
            return;
        }
        super.measureChildBeforeLayout(child, childIndex, widthMeasureSpec, totalWidth, heightMeasureSpec, totalHeight);
    }

    @Override // android.widget.LinearLayout
    int getChildrenSkipCount(View child, int index) {
        LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
        return layoutParams.span - 1;
    }

    @Override // android.widget.LinearLayout
    int getLocationOffset(View child) {
        return ((LayoutParams) child.getLayoutParams()).mOffset[0];
    }

    @Override // android.widget.LinearLayout
    int getNextLocationOffset(View child) {
        return ((LayoutParams) child.getLayoutParams()).mOffset[1];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int[] getColumnsWidths(int widthMeasureSpec) {
        int spec;
        int numColumns = getVirtualChildCount();
        if (this.mColumnWidths == null || numColumns != this.mColumnWidths.length) {
            this.mColumnWidths = new int[numColumns];
        }
        int[] columnWidths = this.mColumnWidths;
        for (int i = 0; i < numColumns; i++) {
            View child = getVirtualChildAt(i);
            if (child != null && child.getVisibility() != 8) {
                LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
                if (layoutParams.span == 1) {
                    switch (layoutParams.width) {
                        case -2:
                            spec = getChildMeasureSpec(widthMeasureSpec, 0, -2);
                            break;
                        case -1:
                            spec = View.MeasureSpec.makeMeasureSpec(0, 0);
                            break;
                        default:
                            spec = View.MeasureSpec.makeMeasureSpec(layoutParams.width, 1073741824);
                            break;
                    }
                    child.measure(spec, spec);
                    int width = child.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin;
                    columnWidths[i] = width;
                } else {
                    columnWidths[i] = 0;
                }
            } else {
                columnWidths[i] = 0;
            }
        }
        return columnWidths;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setColumnsWidthConstraints(int[] columnWidths) {
        if (columnWidths == null || columnWidths.length < getVirtualChildCount()) {
            throw new IllegalArgumentException("columnWidths should be >= getVirtualChildCount()");
        }
        this.mConstrainedColumnWidths = columnWidths;
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.LinearLayout, android.view.ViewGroup
    public LinearLayout.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.LinearLayout, android.view.ViewGroup
    public boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.LinearLayout, android.view.ViewGroup
    public LinearLayout.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override // android.widget.LinearLayout, android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(TableRow.class.getName());
    }

    @Override // android.widget.LinearLayout, android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(TableRow.class.getName());
    }

    /* loaded from: TableRow$LayoutParams.class */
    public static class LayoutParams extends LinearLayout.LayoutParams {
        @ViewDebug.ExportedProperty(category = "layout")
        public int column;
        @ViewDebug.ExportedProperty(category = "layout")
        public int span;
        private static final int LOCATION = 0;
        private static final int LOCATION_NEXT = 1;
        private int[] mOffset;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            this.mOffset = new int[2];
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.TableRow_Cell);
            this.column = a.getInt(0, -1);
            this.span = a.getInt(1, 1);
            if (this.span <= 1) {
                this.span = 1;
            }
            a.recycle();
        }

        public LayoutParams(int w, int h) {
            super(w, h);
            this.mOffset = new int[2];
            this.column = -1;
            this.span = 1;
        }

        public LayoutParams(int w, int h, float initWeight) {
            super(w, h, initWeight);
            this.mOffset = new int[2];
            this.column = -1;
            this.span = 1;
        }

        public LayoutParams() {
            super(-1, -2);
            this.mOffset = new int[2];
            this.column = -1;
            this.span = 1;
        }

        public LayoutParams(int column) {
            this();
            this.column = column;
        }

        public LayoutParams(ViewGroup.LayoutParams p) {
            super(p);
            this.mOffset = new int[2];
        }

        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
            this.mOffset = new int[2];
        }

        @Override // android.view.ViewGroup.LayoutParams
        protected void setBaseAttributes(TypedArray a, int widthAttr, int heightAttr) {
            if (a.hasValue(widthAttr)) {
                this.width = a.getLayoutDimension(widthAttr, "layout_width");
            } else {
                this.width = -1;
            }
            if (a.hasValue(heightAttr)) {
                this.height = a.getLayoutDimension(heightAttr, "layout_height");
            } else {
                this.height = -2;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: TableRow$ChildrenTracker.class */
    public class ChildrenTracker implements ViewGroup.OnHierarchyChangeListener {
        private ViewGroup.OnHierarchyChangeListener listener;

        private ChildrenTracker() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setOnHierarchyChangeListener(ViewGroup.OnHierarchyChangeListener listener) {
            this.listener = listener;
        }

        @Override // android.view.ViewGroup.OnHierarchyChangeListener
        public void onChildViewAdded(View parent, View child) {
            TableRow.this.mColumnToChildIndex = null;
            if (this.listener != null) {
                this.listener.onChildViewAdded(parent, child);
            }
        }

        @Override // android.view.ViewGroup.OnHierarchyChangeListener
        public void onChildViewRemoved(View parent, View child) {
            TableRow.this.mColumnToChildIndex = null;
            if (this.listener != null) {
                this.listener.onChildViewRemoved(parent, child);
            }
        }
    }
}