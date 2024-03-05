package android.support.v7.internal.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.appcompat.R;
import android.support.v7.internal.widget.AbsSpinnerCompat;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.support.v7.widget.ListPopupWindow;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.SpinnerAdapter;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: SpinnerCompat.class */
public class SpinnerCompat extends AbsSpinnerCompat implements DialogInterface.OnClickListener {
    private static final int MAX_ITEMS_MEASURED = 15;
    public static final int MODE_DIALOG = 0;
    public static final int MODE_DROPDOWN = 1;
    private static final int MODE_THEME = -1;
    private static final String TAG = "Spinner";
    private boolean mDisableChildrenWhenDisabled;
    int mDropDownWidth;
    private ListPopupWindow.ForwardingListener mForwardingListener;
    private int mGravity;
    private SpinnerPopup mPopup;
    private DropDownAdapter mTempAdapter;
    private Rect mTempRect;
    private final TintManager mTintManager;

    /* loaded from: SpinnerCompat$DialogPopup.class */
    private class DialogPopup implements SpinnerPopup, DialogInterface.OnClickListener {
        private ListAdapter mListAdapter;
        private AlertDialog mPopup;
        private CharSequence mPrompt;
        final SpinnerCompat this$0;

        private DialogPopup(SpinnerCompat spinnerCompat) {
            this.this$0 = spinnerCompat;
        }

        @Override // android.support.v7.internal.widget.SpinnerCompat.SpinnerPopup
        public void dismiss() {
            AlertDialog alertDialog = this.mPopup;
            if (alertDialog != null) {
                alertDialog.dismiss();
                this.mPopup = null;
            }
        }

        @Override // android.support.v7.internal.widget.SpinnerCompat.SpinnerPopup
        public Drawable getBackground() {
            return null;
        }

        @Override // android.support.v7.internal.widget.SpinnerCompat.SpinnerPopup
        public CharSequence getHintText() {
            return this.mPrompt;
        }

        @Override // android.support.v7.internal.widget.SpinnerCompat.SpinnerPopup
        public int getHorizontalOffset() {
            return 0;
        }

        @Override // android.support.v7.internal.widget.SpinnerCompat.SpinnerPopup
        public int getVerticalOffset() {
            return 0;
        }

        @Override // android.support.v7.internal.widget.SpinnerCompat.SpinnerPopup
        public boolean isShowing() {
            AlertDialog alertDialog = this.mPopup;
            return alertDialog != null ? alertDialog.isShowing() : false;
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            this.this$0.setSelection(i);
            if (this.this$0.mOnItemClickListener != null) {
                this.this$0.performItemClick(null, i, this.mListAdapter.getItemId(i));
            }
            dismiss();
        }

        @Override // android.support.v7.internal.widget.SpinnerCompat.SpinnerPopup
        public void setAdapter(ListAdapter listAdapter) {
            this.mListAdapter = listAdapter;
        }

        @Override // android.support.v7.internal.widget.SpinnerCompat.SpinnerPopup
        public void setBackgroundDrawable(Drawable drawable) {
            Log.e(SpinnerCompat.TAG, "Cannot set popup background for MODE_DIALOG, ignoring");
        }

        @Override // android.support.v7.internal.widget.SpinnerCompat.SpinnerPopup
        public void setHorizontalOffset(int i) {
            Log.e(SpinnerCompat.TAG, "Cannot set horizontal offset for MODE_DIALOG, ignoring");
        }

        @Override // android.support.v7.internal.widget.SpinnerCompat.SpinnerPopup
        public void setPromptText(CharSequence charSequence) {
            this.mPrompt = charSequence;
        }

        @Override // android.support.v7.internal.widget.SpinnerCompat.SpinnerPopup
        public void setVerticalOffset(int i) {
            Log.e(SpinnerCompat.TAG, "Cannot set vertical offset for MODE_DIALOG, ignoring");
        }

        @Override // android.support.v7.internal.widget.SpinnerCompat.SpinnerPopup
        public void show() {
            if (this.mListAdapter == null) {
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this.this$0.getContext());
            CharSequence charSequence = this.mPrompt;
            if (charSequence != null) {
                builder.setTitle(charSequence);
            }
            this.mPopup = builder.setSingleChoiceItems(this.mListAdapter, this.this$0.getSelectedItemPosition(), this).create();
            this.mPopup.show();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: SpinnerCompat$DropDownAdapter.class */
    public static class DropDownAdapter implements ListAdapter, SpinnerAdapter {
        private SpinnerAdapter mAdapter;
        private ListAdapter mListAdapter;

        public DropDownAdapter(SpinnerAdapter spinnerAdapter) {
            this.mAdapter = spinnerAdapter;
            if (spinnerAdapter instanceof ListAdapter) {
                this.mListAdapter = (ListAdapter) spinnerAdapter;
            }
        }

        @Override // android.widget.ListAdapter
        public boolean areAllItemsEnabled() {
            ListAdapter listAdapter = this.mListAdapter;
            if (listAdapter != null) {
                return listAdapter.areAllItemsEnabled();
            }
            return true;
        }

        @Override // android.widget.Adapter
        public int getCount() {
            SpinnerAdapter spinnerAdapter = this.mAdapter;
            return spinnerAdapter == null ? 0 : spinnerAdapter.getCount();
        }

        @Override // android.widget.SpinnerAdapter
        public View getDropDownView(int i, View view, ViewGroup viewGroup) {
            SpinnerAdapter spinnerAdapter = this.mAdapter;
            return spinnerAdapter == null ? null : spinnerAdapter.getDropDownView(i, view, viewGroup);
        }

        @Override // android.widget.Adapter
        public Object getItem(int i) {
            SpinnerAdapter spinnerAdapter = this.mAdapter;
            return spinnerAdapter == null ? null : spinnerAdapter.getItem(i);
        }

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            SpinnerAdapter spinnerAdapter = this.mAdapter;
            return spinnerAdapter == null ? -1L : spinnerAdapter.getItemId(i);
        }

        @Override // android.widget.Adapter
        public int getItemViewType(int i) {
            return 0;
        }

        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            return getDropDownView(i, view, viewGroup);
        }

        @Override // android.widget.Adapter
        public int getViewTypeCount() {
            return 1;
        }

        @Override // android.widget.Adapter
        public boolean hasStableIds() {
            SpinnerAdapter spinnerAdapter = this.mAdapter;
            return spinnerAdapter != null && spinnerAdapter.hasStableIds();
        }

        @Override // android.widget.Adapter
        public boolean isEmpty() {
            return getCount() == 0;
        }

        @Override // android.widget.ListAdapter
        public boolean isEnabled(int i) {
            ListAdapter listAdapter = this.mListAdapter;
            if (listAdapter != null) {
                return listAdapter.isEnabled(i);
            }
            return true;
        }

        @Override // android.widget.Adapter
        public void registerDataSetObserver(DataSetObserver dataSetObserver) {
            SpinnerAdapter spinnerAdapter = this.mAdapter;
            if (spinnerAdapter != null) {
                spinnerAdapter.registerDataSetObserver(dataSetObserver);
            }
        }

        @Override // android.widget.Adapter
        public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
            SpinnerAdapter spinnerAdapter = this.mAdapter;
            if (spinnerAdapter != null) {
                spinnerAdapter.unregisterDataSetObserver(dataSetObserver);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: SpinnerCompat$DropdownPopup.class */
    public class DropdownPopup extends ListPopupWindow implements SpinnerPopup {
        private ListAdapter mAdapter;
        private CharSequence mHintText;
        final SpinnerCompat this$0;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public DropdownPopup(SpinnerCompat spinnerCompat, Context context, AttributeSet attributeSet, int i) {
            super(context, attributeSet, i);
            this.this$0 = spinnerCompat;
            setAnchorView(spinnerCompat);
            setModal(true);
            setPromptPosition(0);
            setOnItemClickListener(new AdapterView.OnItemClickListener(this, spinnerCompat) { // from class: android.support.v7.internal.widget.SpinnerCompat.DropdownPopup.1
                final DropdownPopup this$1;
                final SpinnerCompat val$this$0;

                {
                    this.this$1 = this;
                    this.val$this$0 = spinnerCompat;
                }

                @Override // android.widget.AdapterView.OnItemClickListener
                public void onItemClick(AdapterView<?> adapterView, View view, int i2, long j) {
                    this.this$1.this$0.setSelection(i2);
                    if (this.this$1.this$0.mOnItemClickListener != null) {
                        this.this$1.this$0.performItemClick(view, i2, this.this$1.mAdapter.getItemId(i2));
                    }
                    this.this$1.dismiss();
                }
            });
        }

        void computeContentWidth() {
            Drawable background = getBackground();
            int i = 0;
            if (background != null) {
                background.getPadding(this.this$0.mTempRect);
                i = ViewUtils.isLayoutRtl(this.this$0) ? this.this$0.mTempRect.right : -this.this$0.mTempRect.left;
            } else {
                Rect rect = this.this$0.mTempRect;
                this.this$0.mTempRect.right = 0;
                rect.left = 0;
            }
            int paddingLeft = this.this$0.getPaddingLeft();
            int paddingRight = this.this$0.getPaddingRight();
            int width = this.this$0.getWidth();
            if (this.this$0.mDropDownWidth == -2) {
                int measureContentWidth = this.this$0.measureContentWidth((SpinnerAdapter) this.mAdapter, getBackground());
                int i2 = (this.this$0.getContext().getResources().getDisplayMetrics().widthPixels - this.this$0.mTempRect.left) - this.this$0.mTempRect.right;
                if (measureContentWidth > i2) {
                    measureContentWidth = i2;
                }
                setContentWidth(Math.max(measureContentWidth, (width - paddingLeft) - paddingRight));
            } else if (this.this$0.mDropDownWidth == -1) {
                setContentWidth((width - paddingLeft) - paddingRight);
            } else {
                setContentWidth(this.this$0.mDropDownWidth);
            }
            setHorizontalOffset(ViewUtils.isLayoutRtl(this.this$0) ? i + ((width - paddingRight) - getWidth()) : i + paddingLeft);
        }

        @Override // android.support.v7.internal.widget.SpinnerCompat.SpinnerPopup
        public CharSequence getHintText() {
            return this.mHintText;
        }

        @Override // android.support.v7.widget.ListPopupWindow, android.support.v7.internal.widget.SpinnerCompat.SpinnerPopup
        public void setAdapter(ListAdapter listAdapter) {
            super.setAdapter(listAdapter);
            this.mAdapter = listAdapter;
        }

        @Override // android.support.v7.internal.widget.SpinnerCompat.SpinnerPopup
        public void setPromptText(CharSequence charSequence) {
            this.mHintText = charSequence;
        }

        public void show(int i, int i2) {
            ViewTreeObserver viewTreeObserver;
            boolean isShowing = isShowing();
            computeContentWidth();
            setInputMethodMode(2);
            super.show();
            getListView().setChoiceMode(1);
            setSelection(this.this$0.getSelectedItemPosition());
            if (isShowing || (viewTreeObserver = this.this$0.getViewTreeObserver()) == null) {
                return;
            }
            ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener(this) { // from class: android.support.v7.internal.widget.SpinnerCompat.DropdownPopup.2
                final DropdownPopup this$1;

                {
                    this.this$1 = this;
                }

                @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
                public void onGlobalLayout() {
                    this.this$1.computeContentWidth();
                    DropdownPopup.super.show();
                }
            };
            viewTreeObserver.addOnGlobalLayoutListener(onGlobalLayoutListener);
            setOnDismissListener(new PopupWindow.OnDismissListener(this, onGlobalLayoutListener) { // from class: android.support.v7.internal.widget.SpinnerCompat.DropdownPopup.3
                final DropdownPopup this$1;
                final ViewTreeObserver.OnGlobalLayoutListener val$layoutListener;

                {
                    this.this$1 = this;
                    this.val$layoutListener = onGlobalLayoutListener;
                }

                @Override // android.widget.PopupWindow.OnDismissListener
                public void onDismiss() {
                    ViewTreeObserver viewTreeObserver2 = this.this$1.this$0.getViewTreeObserver();
                    if (viewTreeObserver2 != null) {
                        viewTreeObserver2.removeGlobalOnLayoutListener(this.val$layoutListener);
                    }
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: SpinnerCompat$SavedState.class */
    public static class SavedState extends AbsSpinnerCompat.SavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: android.support.v7.internal.widget.SpinnerCompat.SavedState.1
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
        boolean showDropdown;

        private SavedState(Parcel parcel) {
            super(parcel);
            this.showDropdown = parcel.readByte() != 0;
        }

        SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        @Override // android.support.v7.internal.widget.AbsSpinnerCompat.SavedState, android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeByte(this.showDropdown ? (byte) 1 : (byte) 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: SpinnerCompat$SpinnerPopup.class */
    public interface SpinnerPopup {
        void dismiss();

        Drawable getBackground();

        CharSequence getHintText();

        int getHorizontalOffset();

        int getVerticalOffset();

        boolean isShowing();

        void setAdapter(ListAdapter listAdapter);

        void setBackgroundDrawable(Drawable drawable);

        void setHorizontalOffset(int i);

        void setPromptText(CharSequence charSequence);

        void setVerticalOffset(int i);

        void show();
    }

    SpinnerCompat(Context context) {
        this(context, (AttributeSet) null);
    }

    SpinnerCompat(Context context, int i) {
        this(context, null, R.attr.spinnerStyle, i);
    }

    SpinnerCompat(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.spinnerStyle);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SpinnerCompat(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, -1);
    }

    SpinnerCompat(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i);
        this.mTempRect = new Rect();
        TintTypedArray obtainStyledAttributes = TintTypedArray.obtainStyledAttributes(context, attributeSet, R.styleable.Spinner, i, 0);
        setBackgroundDrawable(obtainStyledAttributes.getDrawable(R.styleable.Spinner_android_background));
        switch (i2 == -1 ? obtainStyledAttributes.getInt(R.styleable.Spinner_spinnerMode, 0) : i2) {
            case 0:
                this.mPopup = new DialogPopup();
                break;
            case 1:
                DropdownPopup dropdownPopup = new DropdownPopup(this, context, attributeSet, i);
                this.mDropDownWidth = obtainStyledAttributes.getLayoutDimension(R.styleable.Spinner_android_dropDownWidth, -2);
                dropdownPopup.setBackgroundDrawable(obtainStyledAttributes.getDrawable(R.styleable.Spinner_android_popupBackground));
                this.mPopup = dropdownPopup;
                this.mForwardingListener = new ListPopupWindow.ForwardingListener(this, this, dropdownPopup) { // from class: android.support.v7.internal.widget.SpinnerCompat.1
                    final SpinnerCompat this$0;
                    final DropdownPopup val$popup;

                    {
                        this.this$0 = this;
                        this.val$popup = dropdownPopup;
                    }

                    @Override // android.support.v7.widget.ListPopupWindow.ForwardingListener
                    public ListPopupWindow getPopup() {
                        return this.val$popup;
                    }

                    @Override // android.support.v7.widget.ListPopupWindow.ForwardingListener
                    public boolean onForwardingStarted() {
                        if (this.this$0.mPopup.isShowing()) {
                            return true;
                        }
                        this.this$0.mPopup.show();
                        return true;
                    }
                };
                break;
        }
        this.mGravity = obtainStyledAttributes.getInt(R.styleable.Spinner_android_gravity, 17);
        this.mPopup.setPromptText(obtainStyledAttributes.getString(R.styleable.Spinner_prompt));
        this.mDisableChildrenWhenDisabled = obtainStyledAttributes.getBoolean(R.styleable.Spinner_disableChildrenWhenDisabled, false);
        obtainStyledAttributes.recycle();
        DropDownAdapter dropDownAdapter = this.mTempAdapter;
        if (dropDownAdapter != null) {
            this.mPopup.setAdapter(dropDownAdapter);
            this.mTempAdapter = null;
        }
        this.mTintManager = obtainStyledAttributes.getTintManager();
    }

    private View makeView(int i, boolean z) {
        View view;
        if (!this.mDataChanged && (view = this.mRecycler.get(i)) != null) {
            setUpChild(view, z);
            return view;
        }
        View view2 = this.mAdapter.getView(i, null, this);
        setUpChild(view2, z);
        return view2;
    }

    private void setUpChild(View view, boolean z) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = generateDefaultLayoutParams();
        }
        if (z) {
            addViewInLayout(view, 0, layoutParams);
        }
        view.setSelected(hasFocus());
        if (this.mDisableChildrenWhenDisabled) {
            view.setEnabled(isEnabled());
        }
        view.measure(ViewGroup.getChildMeasureSpec(this.mWidthMeasureSpec, this.mSpinnerPadding.left + this.mSpinnerPadding.right, layoutParams.width), ViewGroup.getChildMeasureSpec(this.mHeightMeasureSpec, this.mSpinnerPadding.top + this.mSpinnerPadding.bottom, layoutParams.height));
        int measuredHeight = this.mSpinnerPadding.top + ((((getMeasuredHeight() - this.mSpinnerPadding.bottom) - this.mSpinnerPadding.top) - view.getMeasuredHeight()) / 2);
        view.layout(0, measuredHeight, 0 + view.getMeasuredWidth(), view.getMeasuredHeight() + measuredHeight);
    }

    @Override // android.view.View
    public int getBaseline() {
        View view = null;
        if (getChildCount() > 0) {
            view = getChildAt(0);
        } else if (this.mAdapter != null && this.mAdapter.getCount() > 0) {
            view = makeView(0, false);
            this.mRecycler.put(0, view);
        }
        int i = -1;
        if (view != null) {
            int baseline = view.getBaseline();
            if (baseline >= 0) {
                i = view.getTop() + baseline;
            }
            return i;
        }
        return -1;
    }

    public int getDropDownHorizontalOffset() {
        return this.mPopup.getHorizontalOffset();
    }

    public int getDropDownVerticalOffset() {
        return this.mPopup.getVerticalOffset();
    }

    public int getDropDownWidth() {
        return this.mDropDownWidth;
    }

    public Drawable getPopupBackground() {
        return this.mPopup.getBackground();
    }

    public CharSequence getPrompt() {
        return this.mPopup.getHintText();
    }

    @Override // android.support.v7.internal.widget.AbsSpinnerCompat
    void layout(int i, boolean z) {
        int i2 = this.mSpinnerPadding.left;
        int right = ((getRight() - getLeft()) - this.mSpinnerPadding.left) - this.mSpinnerPadding.right;
        if (this.mDataChanged) {
            handleDataChanged();
        }
        if (this.mItemCount == 0) {
            resetList();
            return;
        }
        if (this.mNextSelectedPosition >= 0) {
            setSelectedPositionInt(this.mNextSelectedPosition);
        }
        recycleAllViews();
        removeAllViewsInLayout();
        this.mFirstPosition = this.mSelectedPosition;
        if (this.mAdapter != null) {
            View makeView = makeView(this.mSelectedPosition, true);
            int measuredWidth = makeView.getMeasuredWidth();
            int i3 = i2;
            int absoluteGravity = GravityCompat.getAbsoluteGravity(this.mGravity, ViewCompat.getLayoutDirection(this)) & 7;
            if (absoluteGravity == 1) {
                i3 = ((right / 2) + i2) - (measuredWidth / 2);
            } else if (absoluteGravity == 5) {
                i3 = (i2 + right) - measuredWidth;
            }
            makeView.offsetLeftAndRight(i3);
        }
        this.mRecycler.clear();
        invalidate();
        checkSelectionChanged();
        this.mDataChanged = false;
        this.mNeedSync = false;
        setNextSelectedPositionInt(this.mSelectedPosition);
    }

    int measureContentWidth(SpinnerAdapter spinnerAdapter, Drawable drawable) {
        if (spinnerAdapter == null) {
            return 0;
        }
        int i = 0;
        View view = null;
        int i2 = 0;
        int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
        int makeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(0, 0);
        int max = Math.max(0, getSelectedItemPosition());
        int min = Math.min(spinnerAdapter.getCount(), max + 15);
        for (int max2 = Math.max(0, max - (15 - (min - max))); max2 < min; max2++) {
            int itemViewType = spinnerAdapter.getItemViewType(max2);
            if (itemViewType != i2) {
                i2 = itemViewType;
                view = null;
            }
            view = spinnerAdapter.getView(max2, view, this);
            if (view.getLayoutParams() == null) {
                view.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
            }
            view.measure(makeMeasureSpec, makeMeasureSpec2);
            i = Math.max(i, view.getMeasuredWidth());
        }
        if (drawable != null) {
            drawable.getPadding(this.mTempRect);
            i += this.mTempRect.left + this.mTempRect.right;
        }
        return i;
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        setSelection(i);
        dialogInterface.dismiss();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.internal.widget.AdapterViewCompat, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        SpinnerPopup spinnerPopup = this.mPopup;
        if (spinnerPopup == null || !spinnerPopup.isShowing()) {
            return;
        }
        this.mPopup.dismiss();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.internal.widget.AdapterViewCompat, android.view.ViewGroup, android.view.View
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        this.mInLayout = true;
        layout(0, false);
        this.mInLayout = false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.internal.widget.AbsSpinnerCompat, android.view.View
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (this.mPopup == null || View.MeasureSpec.getMode(i) != Integer.MIN_VALUE) {
            return;
        }
        setMeasuredDimension(Math.min(Math.max(getMeasuredWidth(), measureContentWidth(getAdapter(), getBackground())), View.MeasureSpec.getSize(i)), getMeasuredHeight());
    }

    @Override // android.support.v7.internal.widget.AbsSpinnerCompat, android.view.View
    public void onRestoreInstanceState(Parcelable parcelable) {
        ViewTreeObserver viewTreeObserver;
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        if (!savedState.showDropdown || (viewTreeObserver = getViewTreeObserver()) == null) {
            return;
        }
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener(this) { // from class: android.support.v7.internal.widget.SpinnerCompat.2
            final SpinnerCompat this$0;

            {
                this.this$0 = this;
            }

            @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
            public void onGlobalLayout() {
                if (!this.this$0.mPopup.isShowing()) {
                    this.this$0.mPopup.show();
                }
                ViewTreeObserver viewTreeObserver2 = this.this$0.getViewTreeObserver();
                if (viewTreeObserver2 != null) {
                    viewTreeObserver2.removeGlobalOnLayoutListener(this);
                }
            }
        });
    }

    @Override // android.support.v7.internal.widget.AbsSpinnerCompat, android.view.View
    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        SpinnerPopup spinnerPopup = this.mPopup;
        savedState.showDropdown = spinnerPopup != null && spinnerPopup.isShowing();
        return savedState;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        ListPopupWindow.ForwardingListener forwardingListener = this.mForwardingListener;
        if (forwardingListener == null || !forwardingListener.onTouch(this, motionEvent)) {
            return super.onTouchEvent(motionEvent);
        }
        return true;
    }

    @Override // android.view.View
    public boolean performClick() {
        boolean performClick = super.performClick();
        if (!performClick) {
            performClick = true;
            if (!this.mPopup.isShowing()) {
                this.mPopup.show();
            }
        }
        return performClick;
    }

    @Override // android.support.v7.internal.widget.AbsSpinnerCompat, android.support.v7.internal.widget.AdapterViewCompat
    public void setAdapter(SpinnerAdapter spinnerAdapter) {
        super.setAdapter(spinnerAdapter);
        this.mRecycler.clear();
        if (getContext().getApplicationInfo().targetSdkVersion >= 21 && spinnerAdapter != null && spinnerAdapter.getViewTypeCount() != 1) {
            throw new IllegalArgumentException("Spinner adapter view type count must be 1");
        }
        SpinnerPopup spinnerPopup = this.mPopup;
        if (spinnerPopup != null) {
            spinnerPopup.setAdapter(new DropDownAdapter(spinnerAdapter));
        } else {
            this.mTempAdapter = new DropDownAdapter(spinnerAdapter);
        }
    }

    public void setDropDownHorizontalOffset(int i) {
        this.mPopup.setHorizontalOffset(i);
    }

    public void setDropDownVerticalOffset(int i) {
        this.mPopup.setVerticalOffset(i);
    }

    public void setDropDownWidth(int i) {
        if (this.mPopup instanceof DropdownPopup) {
            this.mDropDownWidth = i;
        } else {
            Log.e(TAG, "Cannot set dropdown width for MODE_DIALOG, ignoring");
        }
    }

    @Override // android.view.View
    public void setEnabled(boolean z) {
        super.setEnabled(z);
        if (this.mDisableChildrenWhenDisabled) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                getChildAt(i).setEnabled(z);
            }
        }
    }

    public void setGravity(int i) {
        if (this.mGravity != i) {
            if ((i & 7) == 0) {
                i |= 8388611;
            }
            this.mGravity = i;
            requestLayout();
        }
    }

    @Override // android.support.v7.internal.widget.AdapterViewCompat
    public void setOnItemClickListener(AdapterViewCompat.OnItemClickListener onItemClickListener) {
        throw new RuntimeException("setOnItemClickListener cannot be used with a spinner.");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setOnItemClickListenerInt(AdapterViewCompat.OnItemClickListener onItemClickListener) {
        super.setOnItemClickListener(onItemClickListener);
    }

    public void setPopupBackgroundDrawable(Drawable drawable) {
        SpinnerPopup spinnerPopup = this.mPopup;
        if (spinnerPopup instanceof DropdownPopup) {
            ((DropdownPopup) spinnerPopup).setBackgroundDrawable(drawable);
        } else {
            Log.e(TAG, "setPopupBackgroundDrawable: incompatible spinner mode; ignoring...");
        }
    }

    public void setPopupBackgroundResource(int i) {
        setPopupBackgroundDrawable(this.mTintManager.getDrawable(i));
    }

    public void setPrompt(CharSequence charSequence) {
        this.mPopup.setPromptText(charSequence);
    }

    public void setPromptId(int i) {
        setPrompt(getContext().getText(i));
    }
}