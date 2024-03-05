package android.widget;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.ContextMenu;
import android.view.RemotableViewMethod;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Adapter;

/* loaded from: AdapterView.class */
public abstract class AdapterView<T extends Adapter> extends ViewGroup {
    public static final int ITEM_VIEW_TYPE_IGNORE = -1;
    public static final int ITEM_VIEW_TYPE_HEADER_OR_FOOTER = -2;
    @ViewDebug.ExportedProperty(category = "scrolling")
    int mFirstPosition;
    int mSpecificTop;
    int mSyncPosition;
    long mSyncRowId;
    long mSyncHeight;
    boolean mNeedSync;
    int mSyncMode;
    private int mLayoutHeight;
    static final int SYNC_SELECTED_POSITION = 0;
    static final int SYNC_FIRST_POSITION = 1;
    static final int SYNC_MAX_DURATION_MILLIS = 100;
    boolean mInLayout;
    OnItemSelectedListener mOnItemSelectedListener;
    OnItemClickListener mOnItemClickListener;
    OnItemLongClickListener mOnItemLongClickListener;
    boolean mDataChanged;
    @ViewDebug.ExportedProperty(category = "list")
    int mNextSelectedPosition;
    long mNextSelectedRowId;
    @ViewDebug.ExportedProperty(category = "list")
    int mSelectedPosition;
    long mSelectedRowId;
    private View mEmptyView;
    @ViewDebug.ExportedProperty(category = "list")
    int mItemCount;
    int mOldItemCount;
    public static final int INVALID_POSITION = -1;
    public static final long INVALID_ROW_ID = Long.MIN_VALUE;
    int mOldSelectedPosition;
    long mOldSelectedRowId;
    private boolean mDesiredFocusableState;
    private boolean mDesiredFocusableInTouchModeState;
    private AdapterView<T>.SelectionNotifier mSelectionNotifier;
    boolean mBlockLayoutRequests;

    /* loaded from: AdapterView$OnItemClickListener.class */
    public interface OnItemClickListener {
        void onItemClick(AdapterView<?> adapterView, View view, int i, long j);
    }

    /* loaded from: AdapterView$OnItemLongClickListener.class */
    public interface OnItemLongClickListener {
        boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j);
    }

    /* loaded from: AdapterView$OnItemSelectedListener.class */
    public interface OnItemSelectedListener {
        void onItemSelected(AdapterView<?> adapterView, View view, int i, long j);

        void onNothingSelected(AdapterView<?> adapterView);
    }

    public abstract T getAdapter();

    public abstract void setAdapter(T t);

    public abstract View getSelectedView();

    public abstract void setSelection(int i);

    public AdapterView(Context context) {
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

    public AdapterView(Context context, AttributeSet attrs) {
        super(context, attrs);
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

    public AdapterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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
        if (getImportantForAccessibility() == 0) {
            setImportantForAccessibility(1);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public final OnItemClickListener getOnItemClickListener() {
        return this.mOnItemClickListener;
    }

    public boolean performItemClick(View view, int position, long id) {
        if (this.mOnItemClickListener != null) {
            playSoundEffect(0);
            if (view != null) {
                view.sendAccessibilityEvent(1);
            }
            this.mOnItemClickListener.onItemClick(this, view, position, id);
            return true;
        }
        return false;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        if (!isLongClickable()) {
            setLongClickable(true);
        }
        this.mOnItemLongClickListener = listener;
    }

    public final OnItemLongClickListener getOnItemLongClickListener() {
        return this.mOnItemLongClickListener;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.mOnItemSelectedListener = listener;
    }

    public final OnItemSelectedListener getOnItemSelectedListener() {
        return this.mOnItemSelectedListener;
    }

    /* loaded from: AdapterView$AdapterContextMenuInfo.class */
    public static class AdapterContextMenuInfo implements ContextMenu.ContextMenuInfo {
        public View targetView;
        public int position;
        public long id;

        public AdapterContextMenuInfo(View targetView, int position, long id) {
            this.targetView = targetView;
            this.position = position;
            this.id = id;
        }
    }

    @Override // android.view.ViewGroup
    public void addView(View child) {
        throw new UnsupportedOperationException("addView(View) is not supported in AdapterView");
    }

    @Override // android.view.ViewGroup
    public void addView(View child, int index) {
        throw new UnsupportedOperationException("addView(View, int) is not supported in AdapterView");
    }

    @Override // android.view.ViewGroup, android.view.ViewManager
    public void addView(View child, ViewGroup.LayoutParams params) {
        throw new UnsupportedOperationException("addView(View, LayoutParams) is not supported in AdapterView");
    }

    @Override // android.view.ViewGroup
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        throw new UnsupportedOperationException("addView(View, int, LayoutParams) is not supported in AdapterView");
    }

    @Override // android.view.ViewGroup, android.view.ViewManager
    public void removeView(View child) {
        throw new UnsupportedOperationException("removeView(View) is not supported in AdapterView");
    }

    @Override // android.view.ViewGroup
    public void removeViewAt(int index) {
        throw new UnsupportedOperationException("removeViewAt(int) is not supported in AdapterView");
    }

    @Override // android.view.ViewGroup
    public void removeAllViews() {
        throw new UnsupportedOperationException("removeAllViews() is not supported in AdapterView");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        this.mLayoutHeight = getHeight();
    }

    @ViewDebug.CapturedViewProperty
    public int getSelectedItemPosition() {
        return this.mNextSelectedPosition;
    }

    @ViewDebug.CapturedViewProperty
    public long getSelectedItemId() {
        return this.mNextSelectedRowId;
    }

    public Object getSelectedItem() {
        T adapter = getAdapter();
        int selection = getSelectedItemPosition();
        if (adapter != null && adapter.getCount() > 0 && selection >= 0) {
            return adapter.getItem(selection);
        }
        return null;
    }

    @ViewDebug.CapturedViewProperty
    public int getCount() {
        return this.mItemCount;
    }

    public int getPositionForView(View view) {
        View listItem = view;
        while (true) {
            try {
                View v = (View) listItem.getParent();
                if (v.equals(this)) {
                    break;
                }
                listItem = v;
            } catch (ClassCastException e) {
                return -1;
            }
        }
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (getChildAt(i).equals(listItem)) {
                return this.mFirstPosition + i;
            }
        }
        return -1;
    }

    public int getFirstVisiblePosition() {
        return this.mFirstPosition;
    }

    public int getLastVisiblePosition() {
        return (this.mFirstPosition + getChildCount()) - 1;
    }

    @RemotableViewMethod
    public void setEmptyView(View emptyView) {
        this.mEmptyView = emptyView;
        if (emptyView != null && emptyView.getImportantForAccessibility() == 0) {
            emptyView.setImportantForAccessibility(1);
        }
        T adapter = getAdapter();
        boolean empty = adapter == null || adapter.isEmpty();
        updateEmptyStatus(empty);
    }

    public View getEmptyView() {
        return this.mEmptyView;
    }

    boolean isInFilterMode() {
        return false;
    }

    @Override // android.view.View
    public void setFocusable(boolean focusable) {
        T adapter = getAdapter();
        boolean empty = adapter == null || adapter.getCount() == 0;
        this.mDesiredFocusableState = focusable;
        if (!focusable) {
            this.mDesiredFocusableInTouchModeState = false;
        }
        super.setFocusable(focusable && (!empty || isInFilterMode()));
    }

    @Override // android.view.View
    public void setFocusableInTouchMode(boolean focusable) {
        T adapter = getAdapter();
        boolean empty = adapter == null || adapter.getCount() == 0;
        this.mDesiredFocusableInTouchModeState = focusable;
        if (focusable) {
            this.mDesiredFocusableState = true;
        }
        super.setFocusableInTouchMode(focusable && (!empty || isInFilterMode()));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void checkFocus() {
        T adapter = getAdapter();
        boolean empty = adapter == null || adapter.getCount() == 0;
        boolean focusable = !empty || isInFilterMode();
        super.setFocusableInTouchMode(focusable && this.mDesiredFocusableInTouchModeState);
        super.setFocusable(focusable && this.mDesiredFocusableState);
        if (this.mEmptyView != null) {
            updateEmptyStatus(adapter == null || adapter.isEmpty());
        }
    }

    private void updateEmptyStatus(boolean empty) {
        if (isInFilterMode()) {
            empty = false;
        }
        if (empty) {
            if (this.mEmptyView != null) {
                this.mEmptyView.setVisibility(0);
                setVisibility(8);
            } else {
                setVisibility(0);
            }
            if (this.mDataChanged) {
                onLayout(false, this.mLeft, this.mTop, this.mRight, this.mBottom);
                return;
            }
            return;
        }
        if (this.mEmptyView != null) {
            this.mEmptyView.setVisibility(8);
        }
        setVisibility(0);
    }

    public Object getItemAtPosition(int position) {
        T adapter = getAdapter();
        if (adapter == null || position < 0) {
            return null;
        }
        return adapter.getItem(position);
    }

    public long getItemIdAtPosition(int position) {
        T adapter = getAdapter();
        if (adapter == null || position < 0) {
            return Long.MIN_VALUE;
        }
        return adapter.getItemId(position);
    }

    @Override // android.view.View
    public void setOnClickListener(View.OnClickListener l) {
        throw new RuntimeException("Don't call setOnClickListener for an AdapterView. You probably want setOnItemClickListener instead");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
        dispatchFreezeSelfOnly(container);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        dispatchThawSelfOnly(container);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: AdapterView$AdapterDataSetObserver.class */
    public class AdapterDataSetObserver extends DataSetObserver {
        private Parcelable mInstanceState = null;

        /* JADX INFO: Access modifiers changed from: package-private */
        public AdapterDataSetObserver() {
        }

        @Override // android.database.DataSetObserver
        public void onChanged() {
            AdapterView.this.mDataChanged = true;
            AdapterView.this.mOldItemCount = AdapterView.this.mItemCount;
            AdapterView.this.mItemCount = AdapterView.this.getAdapter().getCount();
            if (AdapterView.this.getAdapter().hasStableIds() && this.mInstanceState != null && AdapterView.this.mOldItemCount == 0 && AdapterView.this.mItemCount > 0) {
                AdapterView.this.onRestoreInstanceState(this.mInstanceState);
                this.mInstanceState = null;
            } else {
                AdapterView.this.rememberSyncState();
            }
            AdapterView.this.checkFocus();
            AdapterView.this.requestLayout();
        }

        @Override // android.database.DataSetObserver
        public void onInvalidated() {
            AdapterView.this.mDataChanged = true;
            if (AdapterView.this.getAdapter().hasStableIds()) {
                this.mInstanceState = AdapterView.this.onSaveInstanceState();
            }
            AdapterView.this.mOldItemCount = AdapterView.this.mItemCount;
            AdapterView.this.mItemCount = 0;
            AdapterView.this.mSelectedPosition = -1;
            AdapterView.this.mSelectedRowId = Long.MIN_VALUE;
            AdapterView.this.mNextSelectedPosition = -1;
            AdapterView.this.mNextSelectedRowId = Long.MIN_VALUE;
            AdapterView.this.mNeedSync = false;
            AdapterView.this.checkFocus();
            AdapterView.this.requestLayout();
        }

        public void clearSavedState() {
            this.mInstanceState = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(this.mSelectionNotifier);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: AdapterView$SelectionNotifier.class */
    public class SelectionNotifier implements Runnable {
        private SelectionNotifier() {
        }

        @Override // java.lang.Runnable
        public void run() {
            if (!AdapterView.this.mDataChanged) {
                AdapterView.this.fireOnSelected();
                AdapterView.this.performAccessibilityActionsOnSelected();
            } else if (AdapterView.this.getAdapter() != null) {
                AdapterView.this.post(this);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void selectionChanged() {
        if (this.mOnItemSelectedListener != null || AccessibilityManager.getInstance(this.mContext).isEnabled()) {
            if (this.mInLayout || this.mBlockLayoutRequests) {
                if (this.mSelectionNotifier == null) {
                    this.mSelectionNotifier = new SelectionNotifier();
                }
                post(this.mSelectionNotifier);
                return;
            }
            fireOnSelected();
            performAccessibilityActionsOnSelected();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void fireOnSelected() {
        if (this.mOnItemSelectedListener == null) {
            return;
        }
        int selection = getSelectedItemPosition();
        if (selection >= 0) {
            View v = getSelectedView();
            this.mOnItemSelectedListener.onItemSelected(this, v, selection, getAdapter().getItemId(selection));
            return;
        }
        this.mOnItemSelectedListener.onNothingSelected(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void performAccessibilityActionsOnSelected() {
        if (!AccessibilityManager.getInstance(this.mContext).isEnabled()) {
            return;
        }
        int position = getSelectedItemPosition();
        if (position >= 0) {
            sendAccessibilityEvent(4);
        }
    }

    @Override // android.view.View
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        View selectedView = getSelectedView();
        if (selectedView != null && selectedView.getVisibility() == 0 && selectedView.dispatchPopulateAccessibilityEvent(event)) {
            return true;
        }
        return false;
    }

    @Override // android.view.ViewGroup
    public boolean onRequestSendAccessibilityEvent(View child, AccessibilityEvent event) {
        if (super.onRequestSendAccessibilityEvent(child, event)) {
            AccessibilityEvent record = AccessibilityEvent.obtain();
            onInitializeAccessibilityEvent(record);
            child.dispatchPopulateAccessibilityEvent(record);
            event.appendRecord(record);
            return true;
        }
        return false;
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(AdapterView.class.getName());
        info.setScrollable(isScrollableForAccessibility());
        View selectedView = getSelectedView();
        if (selectedView != null) {
            info.setEnabled(selectedView.isEnabled());
        }
    }

    @Override // android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(AdapterView.class.getName());
        event.setScrollable(isScrollableForAccessibility());
        View selectedView = getSelectedView();
        if (selectedView != null) {
            event.setEnabled(selectedView.isEnabled());
        }
        event.setCurrentItemIndex(getSelectedItemPosition());
        event.setFromIndex(getFirstVisiblePosition());
        event.setToIndex(getLastVisiblePosition());
        event.setItemCount(getCount());
    }

    private boolean isScrollableForAccessibility() {
        int itemCount;
        T adapter = getAdapter();
        return adapter != null && (itemCount = adapter.getCount()) > 0 && (getFirstVisiblePosition() > 0 || getLastVisiblePosition() < itemCount - 1);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public boolean canAnimate() {
        return super.canAnimate() && this.mItemCount > 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void handleDataChanged() {
        int count = this.mItemCount;
        boolean found = false;
        if (count > 0) {
            if (this.mNeedSync) {
                this.mNeedSync = false;
                int newPos = findSyncPosition();
                if (newPos >= 0 && lookForSelectablePosition(newPos, true) == newPos) {
                    setNextSelectedPositionInt(newPos);
                    found = true;
                }
            }
            if (!found) {
                int newPos2 = getSelectedItemPosition();
                if (newPos2 >= count) {
                    newPos2 = count - 1;
                }
                if (newPos2 < 0) {
                    newPos2 = 0;
                }
                int selectablePos = lookForSelectablePosition(newPos2, true);
                if (selectablePos < 0) {
                    selectablePos = lookForSelectablePosition(newPos2, false);
                }
                if (selectablePos >= 0) {
                    setNextSelectedPositionInt(selectablePos);
                    checkSelectionChanged();
                    found = true;
                }
            }
        }
        if (!found) {
            this.mSelectedPosition = -1;
            this.mSelectedRowId = Long.MIN_VALUE;
            this.mNextSelectedPosition = -1;
            this.mNextSelectedRowId = Long.MIN_VALUE;
            this.mNeedSync = false;
            checkSelectionChanged();
        }
        notifySubtreeAccessibilityStateChangedIfNeeded();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void checkSelectionChanged() {
        if (this.mSelectedPosition != this.mOldSelectedPosition || this.mSelectedRowId != this.mOldSelectedRowId) {
            selectionChanged();
            this.mOldSelectedPosition = this.mSelectedPosition;
            this.mOldSelectedRowId = this.mSelectedRowId;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int findSyncPosition() {
        int count = this.mItemCount;
        if (count == 0) {
            return -1;
        }
        long idToMatch = this.mSyncRowId;
        int seed = this.mSyncPosition;
        if (idToMatch == Long.MIN_VALUE) {
            return -1;
        }
        int seed2 = Math.min(count - 1, Math.max(0, seed));
        long endTime = SystemClock.uptimeMillis() + 100;
        int first = seed2;
        int last = seed2;
        boolean next = false;
        T adapter = getAdapter();
        if (adapter == null) {
            return -1;
        }
        while (SystemClock.uptimeMillis() <= endTime) {
            long rowId = adapter.getItemId(seed2);
            if (rowId == idToMatch) {
                return seed2;
            }
            boolean hitLast = last == count - 1;
            boolean hitFirst = first == 0;
            if (!hitLast || !hitFirst) {
                if (hitFirst || (next && !hitLast)) {
                    last++;
                    seed2 = last;
                    next = false;
                } else if (hitLast || (!next && !hitFirst)) {
                    first--;
                    seed2 = first;
                    next = true;
                }
            } else {
                return -1;
            }
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int lookForSelectablePosition(int position, boolean lookDown) {
        return position;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setSelectedPositionInt(int position) {
        this.mSelectedPosition = position;
        this.mSelectedRowId = getItemIdAtPosition(position);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setNextSelectedPositionInt(int position) {
        this.mNextSelectedPosition = position;
        this.mNextSelectedRowId = getItemIdAtPosition(position);
        if (this.mNeedSync && this.mSyncMode == 0 && position >= 0) {
            this.mSyncPosition = position;
            this.mSyncRowId = this.mNextSelectedRowId;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void rememberSyncState() {
        if (getChildCount() > 0) {
            this.mNeedSync = true;
            this.mSyncHeight = this.mLayoutHeight;
            if (this.mSelectedPosition >= 0) {
                View v = getChildAt(this.mSelectedPosition - this.mFirstPosition);
                this.mSyncRowId = this.mNextSelectedRowId;
                this.mSyncPosition = this.mNextSelectedPosition;
                if (v != null) {
                    this.mSpecificTop = v.getTop();
                }
                this.mSyncMode = 0;
                return;
            }
            View v2 = getChildAt(0);
            T adapter = getAdapter();
            if (this.mFirstPosition >= 0 && this.mFirstPosition < adapter.getCount()) {
                this.mSyncRowId = adapter.getItemId(this.mFirstPosition);
            } else {
                this.mSyncRowId = -1L;
            }
            this.mSyncPosition = this.mFirstPosition;
            if (v2 != null) {
                this.mSpecificTop = v2.getTop();
            }
            this.mSyncMode = 1;
        }
    }
}