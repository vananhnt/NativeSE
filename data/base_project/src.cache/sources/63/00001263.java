package android.support.v7.internal.widget;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewCompat;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: AbsSpinnerCompat.class */
public abstract class AbsSpinnerCompat extends AdapterViewCompat<SpinnerAdapter> {
    SpinnerAdapter mAdapter;
    private DataSetObserver mDataSetObserver;
    int mHeightMeasureSpec;
    final RecycleBin mRecycler;
    int mSelectionBottomPadding;
    int mSelectionLeftPadding;
    int mSelectionRightPadding;
    int mSelectionTopPadding;
    final Rect mSpinnerPadding;
    private Rect mTouchFrame;
    int mWidthMeasureSpec;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: AbsSpinnerCompat$RecycleBin.class */
    public class RecycleBin {
        private final SparseArray<View> mScrapHeap = new SparseArray<>();
        final AbsSpinnerCompat this$0;

        RecycleBin(AbsSpinnerCompat absSpinnerCompat) {
            this.this$0 = absSpinnerCompat;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void clear() {
            SparseArray<View> sparseArray = this.mScrapHeap;
            int size = sparseArray.size();
            for (int i = 0; i < size; i++) {
                View valueAt = sparseArray.valueAt(i);
                if (valueAt != null) {
                    this.this$0.removeDetachedView(valueAt, true);
                }
            }
            sparseArray.clear();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public View get(int i) {
            View view = this.mScrapHeap.get(i);
            if (view != null) {
                this.mScrapHeap.delete(i);
            }
            return view;
        }

        public void put(int i, View view) {
            this.mScrapHeap.put(i, view);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: AbsSpinnerCompat$SavedState.class */
    public static class SavedState extends View.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: android.support.v7.internal.widget.AbsSpinnerCompat.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        int position;
        long selectedId;

        /* JADX INFO: Access modifiers changed from: package-private */
        public SavedState(Parcel parcel) {
            super(parcel);
            this.selectedId = parcel.readLong();
            this.position = parcel.readInt();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        public String toString() {
            return "AbsSpinner.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " selectedId=" + this.selectedId + " position=" + this.position + "}";
        }

        @Override // android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeLong(this.selectedId);
            parcel.writeInt(this.position);
        }
    }

    AbsSpinnerCompat(Context context) {
        super(context);
        this.mSelectionLeftPadding = 0;
        this.mSelectionTopPadding = 0;
        this.mSelectionRightPadding = 0;
        this.mSelectionBottomPadding = 0;
        this.mSpinnerPadding = new Rect();
        this.mRecycler = new RecycleBin(this);
        initAbsSpinner();
    }

    AbsSpinnerCompat(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbsSpinnerCompat(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mSelectionLeftPadding = 0;
        this.mSelectionTopPadding = 0;
        this.mSelectionRightPadding = 0;
        this.mSelectionBottomPadding = 0;
        this.mSpinnerPadding = new Rect();
        this.mRecycler = new RecycleBin(this);
        initAbsSpinner();
    }

    private void initAbsSpinner() {
        setFocusable(true);
        setWillNotDraw(false);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new ViewGroup.LayoutParams(-1, -2);
    }

    @Override // android.support.v7.internal.widget.AdapterViewCompat
    public SpinnerAdapter getAdapter() {
        return this.mAdapter;
    }

    int getChildHeight(View view) {
        return view.getMeasuredHeight();
    }

    int getChildWidth(View view) {
        return view.getMeasuredWidth();
    }

    @Override // android.support.v7.internal.widget.AdapterViewCompat
    public int getCount() {
        return this.mItemCount;
    }

    @Override // android.support.v7.internal.widget.AdapterViewCompat
    public View getSelectedView() {
        if (this.mItemCount <= 0 || this.mSelectedPosition < 0) {
            return null;
        }
        return getChildAt(this.mSelectedPosition - this.mFirstPosition);
    }

    abstract void layout(int i, boolean z);

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int i, int i2) {
        SpinnerAdapter spinnerAdapter;
        int mode = View.MeasureSpec.getMode(i);
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();
        Rect rect = this.mSpinnerPadding;
        int i3 = this.mSelectionLeftPadding;
        int i4 = i3;
        if (paddingLeft > i3) {
            i4 = paddingLeft;
        }
        rect.left = i4;
        Rect rect2 = this.mSpinnerPadding;
        int i5 = this.mSelectionTopPadding;
        int i6 = i5;
        if (paddingTop > i5) {
            i6 = paddingTop;
        }
        rect2.top = i6;
        Rect rect3 = this.mSpinnerPadding;
        int i7 = this.mSelectionRightPadding;
        int i8 = i7;
        if (paddingRight > i7) {
            i8 = paddingRight;
        }
        rect3.right = i8;
        Rect rect4 = this.mSpinnerPadding;
        int i9 = this.mSelectionBottomPadding;
        int i10 = i9;
        if (paddingBottom > i9) {
            i10 = paddingBottom;
        }
        rect4.bottom = i10;
        if (this.mDataChanged) {
            handleDataChanged();
        }
        int i11 = 0;
        int i12 = 0;
        boolean z = true;
        int selectedItemPosition = getSelectedItemPosition();
        if (selectedItemPosition >= 0 && (spinnerAdapter = this.mAdapter) != null && selectedItemPosition < spinnerAdapter.getCount()) {
            View view = this.mRecycler.get(selectedItemPosition);
            if (view == null) {
                view = this.mAdapter.getView(selectedItemPosition, null, this);
            }
            if (view != null) {
                this.mRecycler.put(selectedItemPosition, view);
                if (view.getLayoutParams() == null) {
                    this.mBlockLayoutRequests = true;
                    view.setLayoutParams(generateDefaultLayoutParams());
                    this.mBlockLayoutRequests = false;
                }
                measureChild(view, i, i2);
                i11 = getChildHeight(view) + this.mSpinnerPadding.top + this.mSpinnerPadding.bottom;
                i12 = getChildWidth(view) + this.mSpinnerPadding.left + this.mSpinnerPadding.right;
                z = false;
            }
        }
        if (z) {
            i11 = this.mSpinnerPadding.top + this.mSpinnerPadding.bottom;
            if (mode == 0) {
                i12 = this.mSpinnerPadding.left + this.mSpinnerPadding.right;
            }
        }
        setMeasuredDimension(ViewCompat.resolveSizeAndState(Math.max(i12, getSuggestedMinimumWidth()), i, 0), ViewCompat.resolveSizeAndState(Math.max(i11, getSuggestedMinimumHeight()), i2, 0));
        this.mHeightMeasureSpec = i2;
        this.mWidthMeasureSpec = i;
    }

    @Override // android.view.View
    public void onRestoreInstanceState(Parcelable parcelable) {
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        if (savedState.selectedId >= 0) {
            this.mDataChanged = true;
            this.mNeedSync = true;
            this.mSyncRowId = savedState.selectedId;
            this.mSyncPosition = savedState.position;
            this.mSyncMode = 0;
            requestLayout();
        }
    }

    @Override // android.view.View
    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.selectedId = getSelectedItemId();
        if (savedState.selectedId >= 0) {
            savedState.position = getSelectedItemPosition();
        } else {
            savedState.position = -1;
        }
        return savedState;
    }

    public int pointToPosition(int i, int i2) {
        Rect rect = this.mTouchFrame;
        if (rect == null) {
            this.mTouchFrame = new Rect();
            rect = this.mTouchFrame;
        }
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            View childAt = getChildAt(childCount);
            if (childAt.getVisibility() == 0) {
                childAt.getHitRect(rect);
                if (rect.contains(i, i2)) {
                    return this.mFirstPosition + childCount;
                }
            }
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void recycleAllViews() {
        int childCount = getChildCount();
        RecycleBin recycleBin = this.mRecycler;
        int i = this.mFirstPosition;
        for (int i2 = 0; i2 < childCount; i2++) {
            recycleBin.put(i + i2, getChildAt(i2));
        }
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestLayout() {
        if (this.mBlockLayoutRequests) {
            return;
        }
        super.requestLayout();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void resetList() {
        this.mDataChanged = false;
        this.mNeedSync = false;
        removeAllViewsInLayout();
        this.mOldSelectedPosition = -1;
        this.mOldSelectedRowId = Long.MIN_VALUE;
        setSelectedPositionInt(-1);
        setNextSelectedPositionInt(-1);
        invalidate();
    }

    @Override // android.support.v7.internal.widget.AdapterViewCompat
    public void setAdapter(SpinnerAdapter spinnerAdapter) {
        SpinnerAdapter spinnerAdapter2 = this.mAdapter;
        if (spinnerAdapter2 != null) {
            spinnerAdapter2.unregisterDataSetObserver(this.mDataSetObserver);
            resetList();
        }
        this.mAdapter = spinnerAdapter;
        int i = -1;
        this.mOldSelectedPosition = -1;
        this.mOldSelectedRowId = Long.MIN_VALUE;
        if (this.mAdapter != null) {
            this.mOldItemCount = this.mItemCount;
            this.mItemCount = this.mAdapter.getCount();
            checkFocus();
            this.mDataSetObserver = new AdapterViewCompat.AdapterDataSetObserver(this);
            this.mAdapter.registerDataSetObserver(this.mDataSetObserver);
            if (this.mItemCount > 0) {
                i = 0;
            }
            setSelectedPositionInt(i);
            setNextSelectedPositionInt(i);
            if (this.mItemCount == 0) {
                checkSelectionChanged();
            }
        } else {
            checkFocus();
            resetList();
            checkSelectionChanged();
        }
        requestLayout();
    }

    @Override // android.support.v7.internal.widget.AdapterViewCompat
    public void setSelection(int i) {
        setNextSelectedPositionInt(i);
        requestLayout();
        invalidate();
    }

    public void setSelection(int i, boolean z) {
        setSelectionInt(i, z && this.mFirstPosition <= i && i <= (this.mFirstPosition + getChildCount()) - 1);
    }

    void setSelectionInt(int i, boolean z) {
        if (i != this.mOldSelectedPosition) {
            this.mBlockLayoutRequests = true;
            int i2 = this.mSelectedPosition;
            setNextSelectedPositionInt(i);
            layout(i - i2, z);
            this.mBlockLayoutRequests = false;
        }
    }
}