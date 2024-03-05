package android.support.v7.internal.widget;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.ContextMenu;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Adapter;
import android.widget.AdapterView;

/* loaded from: AdapterViewCompat.class */
public abstract class AdapterViewCompat<T extends Adapter> extends ViewGroup {
    public static final int INVALID_POSITION = -1;
    public static final long INVALID_ROW_ID = Long.MIN_VALUE;
    static final int ITEM_VIEW_TYPE_HEADER_OR_FOOTER = -2;
    static final int ITEM_VIEW_TYPE_IGNORE = -1;
    static final int SYNC_FIRST_POSITION = 1;
    static final int SYNC_MAX_DURATION_MILLIS = 100;
    static final int SYNC_SELECTED_POSITION = 0;
    boolean mBlockLayoutRequests;
    boolean mDataChanged;
    private boolean mDesiredFocusableInTouchModeState;
    private boolean mDesiredFocusableState;
    private View mEmptyView;
    @ViewDebug.ExportedProperty(category = "scrolling")
    int mFirstPosition;
    boolean mInLayout;
    @ViewDebug.ExportedProperty(category = "list")
    int mItemCount;
    private int mLayoutHeight;
    boolean mNeedSync;
    @ViewDebug.ExportedProperty(category = "list")
    int mNextSelectedPosition;
    long mNextSelectedRowId;
    int mOldItemCount;
    int mOldSelectedPosition;
    long mOldSelectedRowId;
    OnItemClickListener mOnItemClickListener;
    OnItemLongClickListener mOnItemLongClickListener;
    OnItemSelectedListener mOnItemSelectedListener;
    @ViewDebug.ExportedProperty(category = "list")
    int mSelectedPosition;
    long mSelectedRowId;
    private AdapterViewCompat<T>.SelectionNotifier mSelectionNotifier;
    int mSpecificTop;
    long mSyncHeight;
    int mSyncMode;
    int mSyncPosition;
    long mSyncRowId;

    /* loaded from: AdapterViewCompat$AdapterContextMenuInfo.class */
    public static class AdapterContextMenuInfo implements ContextMenu.ContextMenuInfo {
        public long id;
        public int position;
        public View targetView;

        public AdapterContextMenuInfo(View view, int i, long j) {
            this.targetView = view;
            this.position = i;
            this.id = j;
        }
    }

    /* loaded from: AdapterViewCompat$AdapterDataSetObserver.class */
    class AdapterDataSetObserver extends DataSetObserver {
        private Parcelable mInstanceState = null;
        final AdapterViewCompat this$0;

        /* JADX INFO: Access modifiers changed from: package-private */
        public AdapterDataSetObserver(AdapterViewCompat adapterViewCompat) {
            this.this$0 = adapterViewCompat;
        }

        public void clearSavedState() {
            this.mInstanceState = null;
        }

        @Override // android.database.DataSetObserver
        public void onChanged() {
            AdapterViewCompat adapterViewCompat = this.this$0;
            adapterViewCompat.mDataChanged = true;
            adapterViewCompat.mOldItemCount = adapterViewCompat.mItemCount;
            AdapterViewCompat adapterViewCompat2 = this.this$0;
            adapterViewCompat2.mItemCount = adapterViewCompat2.getAdapter().getCount();
            if (!this.this$0.getAdapter().hasStableIds() || this.mInstanceState == null || this.this$0.mOldItemCount != 0 || this.this$0.mItemCount <= 0) {
                this.this$0.rememberSyncState();
            } else {
                this.this$0.onRestoreInstanceState(this.mInstanceState);
                this.mInstanceState = null;
            }
            this.this$0.checkFocus();
            this.this$0.requestLayout();
        }

        @Override // android.database.DataSetObserver
        public void onInvalidated() {
            AdapterViewCompat adapterViewCompat = this.this$0;
            adapterViewCompat.mDataChanged = true;
            if (adapterViewCompat.getAdapter().hasStableIds()) {
                this.mInstanceState = this.this$0.onSaveInstanceState();
            }
            AdapterViewCompat adapterViewCompat2 = this.this$0;
            adapterViewCompat2.mOldItemCount = adapterViewCompat2.mItemCount;
            AdapterViewCompat adapterViewCompat3 = this.this$0;
            adapterViewCompat3.mItemCount = 0;
            adapterViewCompat3.mSelectedPosition = -1;
            adapterViewCompat3.mSelectedRowId = Long.MIN_VALUE;
            adapterViewCompat3.mNextSelectedPosition = -1;
            adapterViewCompat3.mNextSelectedRowId = Long.MIN_VALUE;
            adapterViewCompat3.mNeedSync = false;
            adapterViewCompat3.checkFocus();
            this.this$0.requestLayout();
        }
    }

    /* loaded from: AdapterViewCompat$OnItemClickListener.class */
    public interface OnItemClickListener {
        void onItemClick(AdapterViewCompat<?> adapterViewCompat, View view, int i, long j);
    }

    /* loaded from: AdapterViewCompat$OnItemClickListenerWrapper.class */
    class OnItemClickListenerWrapper implements AdapterView.OnItemClickListener {
        private final OnItemClickListener mWrappedListener;
        final AdapterViewCompat this$0;

        public OnItemClickListenerWrapper(AdapterViewCompat adapterViewCompat, OnItemClickListener onItemClickListener) {
            this.this$0 = adapterViewCompat;
            this.mWrappedListener = onItemClickListener;
        }

        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            this.mWrappedListener.onItemClick(this.this$0, view, i, j);
        }
    }

    /* loaded from: AdapterViewCompat$OnItemLongClickListener.class */
    public interface OnItemLongClickListener {
        boolean onItemLongClick(AdapterViewCompat<?> adapterViewCompat, View view, int i, long j);
    }

    /* loaded from: AdapterViewCompat$OnItemSelectedListener.class */
    public interface OnItemSelectedListener {
        void onItemSelected(AdapterViewCompat<?> adapterViewCompat, View view, int i, long j);

        void onNothingSelected(AdapterViewCompat<?> adapterViewCompat);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: AdapterViewCompat$SelectionNotifier.class */
    public class SelectionNotifier implements Runnable {
        final AdapterViewCompat this$0;

        private SelectionNotifier(AdapterViewCompat adapterViewCompat) {
            this.this$0 = adapterViewCompat;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (!this.this$0.mDataChanged) {
                this.this$0.fireOnSelected();
            } else if (this.this$0.getAdapter() != null) {
                this.this$0.post(this);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AdapterViewCompat(Context context) {
        super(context);
        this.mFirstPosition = 0;
        this.mSyncRowId = Long.MIN_VALUE;
        this.mNeedSync = false;
        this.mInLayout = false;
        this.mNextSelectedPosition = -1;
        this.mNextSelectedRowId = Long.MIN_VALUE;
        this.mSelectedPosition = -1;
        this.mSelectedRowId = Long.MIN_VALUE;
        this.mOldSelectedPosition = -1;
        this.mOldSelectedRowId = Long.MIN_VALUE;
        this.mBlockLayoutRequests = false;
    }

    AdapterViewCompat(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mFirstPosition = 0;
        this.mSyncRowId = Long.MIN_VALUE;
        this.mNeedSync = false;
        this.mInLayout = false;
        this.mNextSelectedPosition = -1;
        this.mNextSelectedRowId = Long.MIN_VALUE;
        this.mSelectedPosition = -1;
        this.mSelectedRowId = Long.MIN_VALUE;
        this.mOldSelectedPosition = -1;
        this.mOldSelectedRowId = Long.MIN_VALUE;
        this.mBlockLayoutRequests = false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AdapterViewCompat(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mFirstPosition = 0;
        this.mSyncRowId = Long.MIN_VALUE;
        this.mNeedSync = false;
        this.mInLayout = false;
        this.mNextSelectedPosition = -1;
        this.mNextSelectedRowId = Long.MIN_VALUE;
        this.mSelectedPosition = -1;
        this.mSelectedRowId = Long.MIN_VALUE;
        this.mOldSelectedPosition = -1;
        this.mOldSelectedRowId = Long.MIN_VALUE;
        this.mBlockLayoutRequests = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void fireOnSelected() {
        if (this.mOnItemSelectedListener == null) {
            return;
        }
        int selectedItemPosition = getSelectedItemPosition();
        if (selectedItemPosition < 0) {
            this.mOnItemSelectedListener.onNothingSelected(this);
            return;
        }
        this.mOnItemSelectedListener.onItemSelected(this, getSelectedView(), selectedItemPosition, getAdapter().getItemId(selectedItemPosition));
    }

    private void updateEmptyStatus(boolean z) {
        if (isInFilterMode()) {
            z = false;
        }
        if (!z) {
            View view = this.mEmptyView;
            if (view != null) {
                view.setVisibility(8);
            }
            setVisibility(0);
            return;
        }
        View view2 = this.mEmptyView;
        if (view2 != null) {
            view2.setVisibility(0);
            setVisibility(8);
        } else {
            setVisibility(0);
        }
        if (this.mDataChanged) {
            onLayout(false, getLeft(), getTop(), getRight(), getBottom());
        }
    }

    @Override // android.view.ViewGroup
    public void addView(View view) {
        throw new UnsupportedOperationException("addView(View) is not supported in AdapterView");
    }

    @Override // android.view.ViewGroup
    public void addView(View view, int i) {
        throw new UnsupportedOperationException("addView(View, int) is not supported in AdapterView");
    }

    @Override // android.view.ViewGroup
    public void addView(View view, int i, ViewGroup.LayoutParams layoutParams) {
        throw new UnsupportedOperationException("addView(View, int, LayoutParams) is not supported in AdapterView");
    }

    @Override // android.view.ViewGroup, android.view.ViewManager
    public void addView(View view, ViewGroup.LayoutParams layoutParams) {
        throw new UnsupportedOperationException("addView(View, LayoutParams) is not supported in AdapterView");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public boolean canAnimate() {
        return super.canAnimate() && this.mItemCount > 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void checkFocus() {
        T adapter = getAdapter();
        boolean z = !(adapter == null || adapter.getCount() == 0) || isInFilterMode();
        super.setFocusableInTouchMode(z && this.mDesiredFocusableInTouchModeState);
        super.setFocusable(z && this.mDesiredFocusableState);
        if (this.mEmptyView != null) {
            updateEmptyStatus(adapter == null || adapter.isEmpty());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void checkSelectionChanged() {
        if (this.mSelectedPosition == this.mOldSelectedPosition && this.mSelectedRowId == this.mOldSelectedRowId) {
            return;
        }
        selectionChanged();
        this.mOldSelectedPosition = this.mSelectedPosition;
        this.mOldSelectedRowId = this.mSelectedRowId;
    }

    @Override // android.view.View
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        View selectedView = getSelectedView();
        return selectedView != null && selectedView.getVisibility() == 0 && selectedView.dispatchPopulateAccessibilityEvent(accessibilityEvent);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void dispatchRestoreInstanceState(SparseArray<Parcelable> sparseArray) {
        dispatchThawSelfOnly(sparseArray);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void dispatchSaveInstanceState(SparseArray<Parcelable> sparseArray) {
        dispatchFreezeSelfOnly(sparseArray);
    }

    int findSyncPosition() {
        int i = this.mItemCount;
        if (i == 0) {
            return -1;
        }
        long j = this.mSyncRowId;
        int i2 = this.mSyncPosition;
        if (j == Long.MIN_VALUE) {
            return -1;
        }
        int min = Math.min(i - 1, Math.max(0, i2));
        long uptimeMillis = SystemClock.uptimeMillis();
        int i3 = min;
        int i4 = min;
        boolean z = false;
        T adapter = getAdapter();
        if (adapter == null) {
            return -1;
        }
        while (SystemClock.uptimeMillis() <= uptimeMillis + 100) {
            if (adapter.getItemId(min) == j) {
                return min;
            }
            boolean z2 = true;
            boolean z3 = i4 == i - 1;
            if (i3 != 0) {
                z2 = false;
            }
            if (z3 && z2) {
                return -1;
            }
            if (z2 || (z && !z3)) {
                i4++;
                min = i4;
                z = false;
            } else if (z3 || (!z && !z2)) {
                i3--;
                min = i3;
                z = true;
            }
        }
        return -1;
    }

    public abstract T getAdapter();

    @ViewDebug.CapturedViewProperty
    public int getCount() {
        return this.mItemCount;
    }

    public View getEmptyView() {
        return this.mEmptyView;
    }

    public int getFirstVisiblePosition() {
        return this.mFirstPosition;
    }

    public Object getItemAtPosition(int i) {
        T adapter = getAdapter();
        return (adapter == null || i < 0) ? null : adapter.getItem(i);
    }

    public long getItemIdAtPosition(int i) {
        T adapter = getAdapter();
        return (adapter == null || i < 0) ? Long.MIN_VALUE : adapter.getItemId(i);
    }

    public int getLastVisiblePosition() {
        return (this.mFirstPosition + getChildCount()) - 1;
    }

    public final OnItemClickListener getOnItemClickListener() {
        return this.mOnItemClickListener;
    }

    public final OnItemLongClickListener getOnItemLongClickListener() {
        return this.mOnItemLongClickListener;
    }

    public final OnItemSelectedListener getOnItemSelectedListener() {
        return this.mOnItemSelectedListener;
    }

    public int getPositionForView(View view) {
        while (true) {
            try {
                View view2 = (View) view.getParent();
                if (view2.equals(this)) {
                    break;
                }
                view = view2;
            } catch (ClassCastException e) {
                return -1;
            }
        }
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (getChildAt(i).equals(view)) {
                return this.mFirstPosition + i;
            }
        }
        return -1;
    }

    public Object getSelectedItem() {
        T adapter = getAdapter();
        int selectedItemPosition = getSelectedItemPosition();
        if (adapter == null || adapter.getCount() <= 0 || selectedItemPosition < 0) {
            return null;
        }
        return adapter.getItem(selectedItemPosition);
    }

    @ViewDebug.CapturedViewProperty
    public long getSelectedItemId() {
        return this.mNextSelectedRowId;
    }

    @ViewDebug.CapturedViewProperty
    public int getSelectedItemPosition() {
        return this.mNextSelectedPosition;
    }

    public abstract View getSelectedView();

    /* JADX INFO: Access modifiers changed from: package-private */
    public void handleDataChanged() {
        int i = this.mItemCount;
        boolean z = false;
        if (i > 0) {
            if (this.mNeedSync) {
                this.mNeedSync = false;
                int findSyncPosition = findSyncPosition();
                if (findSyncPosition >= 0 && lookForSelectablePosition(findSyncPosition, true) == findSyncPosition) {
                    setNextSelectedPositionInt(findSyncPosition);
                    z = true;
                }
            }
            if (!z) {
                int selectedItemPosition = getSelectedItemPosition();
                if (selectedItemPosition >= i) {
                    selectedItemPosition = i - 1;
                }
                if (selectedItemPosition < 0) {
                    selectedItemPosition = 0;
                }
                int lookForSelectablePosition = lookForSelectablePosition(selectedItemPosition, true);
                int lookForSelectablePosition2 = lookForSelectablePosition < 0 ? lookForSelectablePosition(selectedItemPosition, false) : lookForSelectablePosition;
                if (lookForSelectablePosition2 >= 0) {
                    setNextSelectedPositionInt(lookForSelectablePosition2);
                    checkSelectionChanged();
                    z = true;
                }
            }
        } else {
            z = false;
        }
        if (z) {
            return;
        }
        this.mSelectedPosition = -1;
        this.mSelectedRowId = Long.MIN_VALUE;
        this.mNextSelectedPosition = -1;
        this.mNextSelectedRowId = Long.MIN_VALUE;
        this.mNeedSync = false;
        checkSelectionChanged();
    }

    boolean isInFilterMode() {
        return false;
    }

    int lookForSelectablePosition(int i, boolean z) {
        return i;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(this.mSelectionNotifier);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        this.mLayoutHeight = getHeight();
    }

    public boolean performItemClick(View view, int i, long j) {
        if (this.mOnItemClickListener != null) {
            playSoundEffect(0);
            if (view != null) {
                view.sendAccessibilityEvent(1);
            }
            this.mOnItemClickListener.onItemClick(this, view, i, j);
            return true;
        }
        return false;
    }

    void rememberSyncState() {
        if (getChildCount() > 0) {
            this.mNeedSync = true;
            this.mSyncHeight = this.mLayoutHeight;
            int i = this.mSelectedPosition;
            if (i >= 0) {
                View childAt = getChildAt(i - this.mFirstPosition);
                this.mSyncRowId = this.mNextSelectedRowId;
                this.mSyncPosition = this.mNextSelectedPosition;
                if (childAt != null) {
                    this.mSpecificTop = childAt.getTop();
                }
                this.mSyncMode = 0;
                return;
            }
            View childAt2 = getChildAt(0);
            T adapter = getAdapter();
            int i2 = this.mFirstPosition;
            if (i2 < 0 || i2 >= adapter.getCount()) {
                this.mSyncRowId = -1L;
            } else {
                this.mSyncRowId = adapter.getItemId(this.mFirstPosition);
            }
            this.mSyncPosition = this.mFirstPosition;
            if (childAt2 != null) {
                this.mSpecificTop = childAt2.getTop();
            }
            this.mSyncMode = 1;
        }
    }

    @Override // android.view.ViewGroup
    public void removeAllViews() {
        throw new UnsupportedOperationException("removeAllViews() is not supported in AdapterView");
    }

    @Override // android.view.ViewGroup, android.view.ViewManager
    public void removeView(View view) {
        throw new UnsupportedOperationException("removeView(View) is not supported in AdapterView");
    }

    @Override // android.view.ViewGroup
    public void removeViewAt(int i) {
        throw new UnsupportedOperationException("removeViewAt(int) is not supported in AdapterView");
    }

    void selectionChanged() {
        if (this.mOnItemSelectedListener != null) {
            if (this.mInLayout || this.mBlockLayoutRequests) {
                if (this.mSelectionNotifier == null) {
                    this.mSelectionNotifier = new SelectionNotifier();
                }
                post(this.mSelectionNotifier);
            } else {
                fireOnSelected();
            }
        }
        if (this.mSelectedPosition == -1 || !isShown() || isInTouchMode()) {
            return;
        }
        sendAccessibilityEvent(4);
    }

    public abstract void setAdapter(T t);

    public void setEmptyView(View view) {
        this.mEmptyView = view;
        T adapter = getAdapter();
        updateEmptyStatus(adapter == null || adapter.isEmpty());
    }

    @Override // android.view.View
    public void setFocusable(boolean z) {
        T adapter = getAdapter();
        boolean z2 = adapter == null || adapter.getCount() == 0;
        this.mDesiredFocusableState = z;
        if (!z) {
            this.mDesiredFocusableInTouchModeState = false;
        }
        super.setFocusable(z && (!z2 || isInFilterMode()));
    }

    /* JADX WARN: Code restructure failed: missing block: B:17:0x0044, code lost:
        if (isInFilterMode() != false) goto L15;
     */
    @Override // android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void setFocusableInTouchMode(boolean r4) {
        /*
            r3 = this;
            r0 = r3
            android.widget.Adapter r0 = r0.getAdapter()
            r5 = r0
            r0 = 0
            r6 = r0
            r0 = r5
            if (r0 == 0) goto L1d
            r0 = r5
            int r0 = r0.getCount()
            if (r0 != 0) goto L17
            goto L1d
        L17:
            r0 = 0
            r7 = r0
            goto L20
        L1d:
            r0 = 1
            r7 = r0
        L20:
            r0 = r3
            r1 = r4
            r0.mDesiredFocusableInTouchModeState = r1
            r0 = r4
            if (r0 == 0) goto L31
            r0 = r3
            r1 = 1
            r0.mDesiredFocusableState = r1
            goto L31
        L31:
            r0 = r6
            r8 = r0
            r0 = r4
            if (r0 == 0) goto L4a
            r0 = r7
            if (r0 == 0) goto L47
            r0 = r6
            r8 = r0
            r0 = r3
            boolean r0 = r0.isInFilterMode()
            if (r0 == 0) goto L4a
        L47:
            r0 = 1
            r8 = r0
        L4a:
            r0 = r3
            r1 = r8
            super.setFocusableInTouchMode(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.internal.widget.AdapterViewCompat.setFocusableInTouchMode(boolean):void");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setNextSelectedPositionInt(int i) {
        this.mNextSelectedPosition = i;
        this.mNextSelectedRowId = getItemIdAtPosition(i);
        if (this.mNeedSync && this.mSyncMode == 0 && i >= 0) {
            this.mSyncPosition = i;
            this.mSyncRowId = this.mNextSelectedRowId;
        }
    }

    @Override // android.view.View
    public void setOnClickListener(View.OnClickListener onClickListener) {
        throw new RuntimeException("Don't call setOnClickListener for an AdapterView. You probably want setOnItemClickListener instead");
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        if (!isLongClickable()) {
            setLongClickable(true);
        }
        this.mOnItemLongClickListener = onItemLongClickListener;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.mOnItemSelectedListener = onItemSelectedListener;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setSelectedPositionInt(int i) {
        this.mSelectedPosition = i;
        this.mSelectedRowId = getItemIdAtPosition(i);
    }

    public abstract void setSelection(int i);
}