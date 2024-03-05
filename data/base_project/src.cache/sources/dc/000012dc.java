package android.support.v7.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.text.TextUtilsCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.widget.ListViewAutoScrollHelper;
import android.support.v4.widget.PopupWindowCompat;
import android.support.v7.appcompat.R;
import android.support.v7.internal.widget.AppCompatPopupWindow;
import android.support.v7.internal.widget.ListViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import java.lang.reflect.Method;

/* loaded from: ListPopupWindow.class */
public class ListPopupWindow {
    private static final boolean DEBUG = false;
    private static final int EXPAND_LIST_TIMEOUT = 250;
    public static final int INPUT_METHOD_FROM_FOCUSABLE = 0;
    public static final int INPUT_METHOD_NEEDED = 1;
    public static final int INPUT_METHOD_NOT_NEEDED = 2;
    public static final int MATCH_PARENT = -1;
    public static final int POSITION_PROMPT_ABOVE = 0;
    public static final int POSITION_PROMPT_BELOW = 1;
    private static final String TAG = "ListPopupWindow";
    public static final int WRAP_CONTENT = -2;
    private static Method sClipToWindowEnabledMethod;
    private ListAdapter mAdapter;
    private Context mContext;
    private boolean mDropDownAlwaysVisible;
    private View mDropDownAnchorView;
    private int mDropDownGravity;
    private int mDropDownHeight;
    private int mDropDownHorizontalOffset;
    private DropDownListView mDropDownList;
    private Drawable mDropDownListHighlight;
    private int mDropDownVerticalOffset;
    private boolean mDropDownVerticalOffsetSet;
    private int mDropDownWidth;
    private boolean mForceIgnoreOutsideTouch;
    private Handler mHandler;
    private final ListSelectorHider mHideSelector;
    private AdapterView.OnItemClickListener mItemClickListener;
    private AdapterView.OnItemSelectedListener mItemSelectedListener;
    private int mLayoutDirection;
    int mListItemExpandMaximum;
    private boolean mModal;
    private DataSetObserver mObserver;
    private PopupWindow mPopup;
    private int mPromptPosition;
    private View mPromptView;
    private final ResizePopupRunnable mResizePopupRunnable;
    private final PopupScrollListener mScrollListener;
    private Runnable mShowDropDownRunnable;
    private Rect mTempRect;
    private final PopupTouchInterceptor mTouchInterceptor;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ListPopupWindow$DropDownListView.class */
    public static class DropDownListView extends ListViewCompat {
        private ViewPropertyAnimatorCompat mClickAnimation;
        private boolean mDrawsInPressedState;
        private boolean mHijackFocus;
        private boolean mListSelectionHidden;
        private ListViewAutoScrollHelper mScrollHelper;

        public DropDownListView(Context context, boolean z) {
            super(context, null, R.attr.dropDownListViewStyle);
            this.mHijackFocus = z;
            setCacheColorHint(0);
        }

        private void clearPressedItem() {
            this.mDrawsInPressedState = false;
            setPressed(false);
            drawableStateChanged();
            ViewPropertyAnimatorCompat viewPropertyAnimatorCompat = this.mClickAnimation;
            if (viewPropertyAnimatorCompat != null) {
                viewPropertyAnimatorCompat.cancel();
                this.mClickAnimation = null;
            }
        }

        private void clickPressedItem(View view, int i) {
            performItemClick(view, i, getItemIdAtPosition(i));
        }

        private void setPressedItem(View view, int i, float f, float f2) {
            this.mDrawsInPressedState = true;
            setPressed(true);
            layoutChildren();
            setSelection(i);
            positionSelectorLikeTouchCompat(i, view, f, f2);
            setSelectorEnabled(false);
            refreshDrawableState();
        }

        @Override // android.view.ViewGroup, android.view.View
        public boolean hasFocus() {
            return this.mHijackFocus || super.hasFocus();
        }

        @Override // android.view.View
        public boolean hasWindowFocus() {
            return this.mHijackFocus || super.hasWindowFocus();
        }

        @Override // android.view.View
        public boolean isFocused() {
            return this.mHijackFocus || super.isFocused();
        }

        @Override // android.view.View
        public boolean isInTouchMode() {
            return (this.mHijackFocus && this.mListSelectionHidden) || super.isInTouchMode();
        }

        public boolean onForwardedEvent(MotionEvent motionEvent, int i) {
            boolean z;
            boolean z2 = true;
            int actionMasked = MotionEventCompat.getActionMasked(motionEvent);
            switch (actionMasked) {
                case 1:
                    z2 = false;
                case 2:
                    int findPointerIndex = motionEvent.findPointerIndex(i);
                    if (findPointerIndex >= 0) {
                        int x = (int) motionEvent.getX(findPointerIndex);
                        int y = (int) motionEvent.getY(findPointerIndex);
                        int pointToPosition = pointToPosition(x, y);
                        if (pointToPosition != -1) {
                            View childAt = getChildAt(pointToPosition - getFirstVisiblePosition());
                            setPressedItem(childAt, pointToPosition, x, y);
                            z2 = true;
                            if (actionMasked != 1) {
                                z = false;
                                break;
                            } else {
                                clickPressedItem(childAt, pointToPosition);
                                z = false;
                                break;
                            }
                        } else {
                            z = true;
                            break;
                        }
                    } else {
                        z2 = false;
                        z = false;
                        break;
                    }
                case 3:
                    z2 = false;
                    z = false;
                    break;
                default:
                    z2 = true;
                    z = false;
                    break;
            }
            if (!z2 || z) {
                clearPressedItem();
            }
            if (z2) {
                if (this.mScrollHelper == null) {
                    this.mScrollHelper = new ListViewAutoScrollHelper(this);
                }
                this.mScrollHelper.setEnabled(true);
                this.mScrollHelper.onTouch(this, motionEvent);
            } else {
                ListViewAutoScrollHelper listViewAutoScrollHelper = this.mScrollHelper;
                if (listViewAutoScrollHelper != null) {
                    listViewAutoScrollHelper.setEnabled(false);
                }
            }
            return z2;
        }

        @Override // android.support.v7.internal.widget.ListViewCompat
        protected boolean touchModeDrawsInPressedStateCompat() {
            return this.mDrawsInPressedState || super.touchModeDrawsInPressedStateCompat();
        }
    }

    /* loaded from: ListPopupWindow$ForwardingListener.class */
    public static abstract class ForwardingListener implements View.OnTouchListener {
        private int mActivePointerId;
        private Runnable mDisallowIntercept;
        private boolean mForwarding;
        private final float mScaledTouchSlop;
        private final View mSrc;
        private Runnable mTriggerLongPress;
        private boolean mWasLongPress;
        private final int[] mTmpLocation = new int[2];
        private final int mTapTimeout = ViewConfiguration.getTapTimeout();
        private final int mLongPressTimeout = (this.mTapTimeout + ViewConfiguration.getLongPressTimeout()) / 2;

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: ListPopupWindow$ForwardingListener$DisallowIntercept.class */
        public class DisallowIntercept implements Runnable {
            final ForwardingListener this$0;

            private DisallowIntercept(ForwardingListener forwardingListener) {
                this.this$0 = forwardingListener;
            }

            @Override // java.lang.Runnable
            public void run() {
                this.this$0.mSrc.getParent().requestDisallowInterceptTouchEvent(true);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: ListPopupWindow$ForwardingListener$TriggerLongPress.class */
        public class TriggerLongPress implements Runnable {
            final ForwardingListener this$0;

            private TriggerLongPress(ForwardingListener forwardingListener) {
                this.this$0 = forwardingListener;
            }

            @Override // java.lang.Runnable
            public void run() {
                this.this$0.onLongPress();
            }
        }

        public ForwardingListener(View view) {
            this.mSrc = view;
            this.mScaledTouchSlop = ViewConfiguration.get(view.getContext()).getScaledTouchSlop();
        }

        private void clearCallbacks() {
            Runnable runnable = this.mTriggerLongPress;
            if (runnable != null) {
                this.mSrc.removeCallbacks(runnable);
            }
            Runnable runnable2 = this.mDisallowIntercept;
            if (runnable2 != null) {
                this.mSrc.removeCallbacks(runnable2);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void onLongPress() {
            clearCallbacks();
            if (this.mSrc.isEnabled() && onForwardingStarted()) {
                this.mSrc.getParent().requestDisallowInterceptTouchEvent(true);
                long uptimeMillis = SystemClock.uptimeMillis();
                MotionEvent obtain = MotionEvent.obtain(uptimeMillis, uptimeMillis, 3, 0.0f, 0.0f, 0);
                this.mSrc.onTouchEvent(obtain);
                obtain.recycle();
                this.mForwarding = true;
                this.mWasLongPress = true;
            }
        }

        private boolean onTouchForwarded(MotionEvent motionEvent) {
            DropDownListView dropDownListView;
            View view = this.mSrc;
            ListPopupWindow popup = getPopup();
            if (popup == null || !popup.isShowing() || (dropDownListView = popup.mDropDownList) == null || !dropDownListView.isShown()) {
                return false;
            }
            MotionEvent obtainNoHistory = MotionEvent.obtainNoHistory(motionEvent);
            toGlobalMotionEvent(view, obtainNoHistory);
            toLocalMotionEvent(dropDownListView, obtainNoHistory);
            boolean onForwardedEvent = dropDownListView.onForwardedEvent(obtainNoHistory, this.mActivePointerId);
            obtainNoHistory.recycle();
            int actionMasked = MotionEventCompat.getActionMasked(motionEvent);
            boolean z = (actionMasked == 1 || actionMasked == 3) ? false : true;
            boolean z2 = false;
            if (onForwardedEvent) {
                z2 = false;
                if (z) {
                    z2 = true;
                }
            }
            return z2;
        }

        private boolean onTouchObserved(MotionEvent motionEvent) {
            View view = this.mSrc;
            if (view.isEnabled()) {
                switch (MotionEventCompat.getActionMasked(motionEvent)) {
                    case 0:
                        this.mActivePointerId = motionEvent.getPointerId(0);
                        this.mWasLongPress = false;
                        if (this.mDisallowIntercept == null) {
                            this.mDisallowIntercept = new DisallowIntercept();
                        }
                        view.postDelayed(this.mDisallowIntercept, this.mTapTimeout);
                        if (this.mTriggerLongPress == null) {
                            this.mTriggerLongPress = new TriggerLongPress();
                        }
                        view.postDelayed(this.mTriggerLongPress, this.mLongPressTimeout);
                        return false;
                    case 1:
                    case 3:
                        clearCallbacks();
                        return false;
                    case 2:
                        int findPointerIndex = motionEvent.findPointerIndex(this.mActivePointerId);
                        if (findPointerIndex < 0 || pointInView(view, motionEvent.getX(findPointerIndex), motionEvent.getY(findPointerIndex), this.mScaledTouchSlop)) {
                            return false;
                        }
                        clearCallbacks();
                        view.getParent().requestDisallowInterceptTouchEvent(true);
                        return true;
                    default:
                        return false;
                }
            }
            return false;
        }

        private static boolean pointInView(View view, float f, float f2, float f3) {
            return f >= (-f3) && f2 >= (-f3) && f < ((float) (view.getRight() - view.getLeft())) + f3 && f2 < ((float) (view.getBottom() - view.getTop())) + f3;
        }

        private boolean toGlobalMotionEvent(View view, MotionEvent motionEvent) {
            int[] iArr = this.mTmpLocation;
            view.getLocationOnScreen(iArr);
            motionEvent.offsetLocation(iArr[0], iArr[1]);
            return true;
        }

        private boolean toLocalMotionEvent(View view, MotionEvent motionEvent) {
            int[] iArr = this.mTmpLocation;
            view.getLocationOnScreen(iArr);
            motionEvent.offsetLocation(-iArr[0], -iArr[1]);
            return true;
        }

        public abstract ListPopupWindow getPopup();

        protected boolean onForwardingStarted() {
            ListPopupWindow popup = getPopup();
            if (popup == null || popup.isShowing()) {
                return true;
            }
            popup.show();
            return true;
        }

        protected boolean onForwardingStopped() {
            ListPopupWindow popup = getPopup();
            if (popup == null || !popup.isShowing()) {
                return true;
            }
            popup.dismiss();
            return true;
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View view, MotionEvent motionEvent) {
            boolean z;
            boolean z2 = this.mForwarding;
            if (z2) {
                z = this.mWasLongPress ? onTouchForwarded(motionEvent) : onTouchForwarded(motionEvent) || !onForwardingStopped();
            } else {
                z = onTouchObserved(motionEvent) && onForwardingStarted();
                if (z) {
                    long uptimeMillis = SystemClock.uptimeMillis();
                    MotionEvent obtain = MotionEvent.obtain(uptimeMillis, uptimeMillis, 3, 0.0f, 0.0f, 0);
                    this.mSrc.onTouchEvent(obtain);
                    obtain.recycle();
                }
            }
            this.mForwarding = z;
            boolean z3 = true;
            if (!z) {
                z3 = z2;
            }
            return z3;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ListPopupWindow$ListSelectorHider.class */
    public class ListSelectorHider implements Runnable {
        final ListPopupWindow this$0;

        private ListSelectorHider(ListPopupWindow listPopupWindow) {
            this.this$0 = listPopupWindow;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.this$0.clearListSelection();
        }
    }

    /* loaded from: ListPopupWindow$PopupDataSetObserver.class */
    private class PopupDataSetObserver extends DataSetObserver {
        final ListPopupWindow this$0;

        private PopupDataSetObserver(ListPopupWindow listPopupWindow) {
            this.this$0 = listPopupWindow;
        }

        @Override // android.database.DataSetObserver
        public void onChanged() {
            if (this.this$0.isShowing()) {
                this.this$0.show();
            }
        }

        @Override // android.database.DataSetObserver
        public void onInvalidated() {
            this.this$0.dismiss();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ListPopupWindow$PopupScrollListener.class */
    public class PopupScrollListener implements AbsListView.OnScrollListener {
        final ListPopupWindow this$0;

        private PopupScrollListener(ListPopupWindow listPopupWindow) {
            this.this$0 = listPopupWindow;
        }

        @Override // android.widget.AbsListView.OnScrollListener
        public void onScroll(AbsListView absListView, int i, int i2, int i3) {
        }

        @Override // android.widget.AbsListView.OnScrollListener
        public void onScrollStateChanged(AbsListView absListView, int i) {
            if (i != 1 || this.this$0.isInputMethodNotNeeded() || this.this$0.mPopup.getContentView() == null) {
                return;
            }
            this.this$0.mHandler.removeCallbacks(this.this$0.mResizePopupRunnable);
            this.this$0.mResizePopupRunnable.run();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ListPopupWindow$PopupTouchInterceptor.class */
    public class PopupTouchInterceptor implements View.OnTouchListener {
        final ListPopupWindow this$0;

        private PopupTouchInterceptor(ListPopupWindow listPopupWindow) {
            this.this$0 = listPopupWindow;
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View view, MotionEvent motionEvent) {
            int action = motionEvent.getAction();
            int x = (int) motionEvent.getX();
            int y = (int) motionEvent.getY();
            if (action == 0 && this.this$0.mPopup != null && this.this$0.mPopup.isShowing() && x >= 0 && x < this.this$0.mPopup.getWidth() && y >= 0 && y < this.this$0.mPopup.getHeight()) {
                this.this$0.mHandler.postDelayed(this.this$0.mResizePopupRunnable, 250L);
                return false;
            } else if (action == 1) {
                this.this$0.mHandler.removeCallbacks(this.this$0.mResizePopupRunnable);
                return false;
            } else {
                return false;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ListPopupWindow$ResizePopupRunnable.class */
    public class ResizePopupRunnable implements Runnable {
        final ListPopupWindow this$0;

        private ResizePopupRunnable(ListPopupWindow listPopupWindow) {
            this.this$0 = listPopupWindow;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (this.this$0.mDropDownList == null || this.this$0.mDropDownList.getCount() <= this.this$0.mDropDownList.getChildCount() || this.this$0.mDropDownList.getChildCount() > this.this$0.mListItemExpandMaximum) {
                return;
            }
            this.this$0.mPopup.setInputMethodMode(2);
            this.this$0.show();
        }
    }

    static {
        try {
            sClipToWindowEnabledMethod = PopupWindow.class.getDeclaredMethod("setClipToScreenEnabled", Boolean.TYPE);
        } catch (NoSuchMethodException e) {
            Log.i(TAG, "Could not find method setClipToScreenEnabled() on PopupWindow. Oh well.");
        }
    }

    public ListPopupWindow(Context context) {
        this(context, null, R.attr.listPopupWindowStyle);
    }

    public ListPopupWindow(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.listPopupWindowStyle);
    }

    public ListPopupWindow(Context context, AttributeSet attributeSet, int i) {
        this.mDropDownHeight = -2;
        this.mDropDownWidth = -2;
        this.mDropDownGravity = 0;
        this.mDropDownAlwaysVisible = false;
        this.mForceIgnoreOutsideTouch = false;
        this.mListItemExpandMaximum = Integer.MAX_VALUE;
        this.mPromptPosition = 0;
        this.mResizePopupRunnable = new ResizePopupRunnable();
        this.mTouchInterceptor = new PopupTouchInterceptor();
        this.mScrollListener = new PopupScrollListener();
        this.mHideSelector = new ListSelectorHider();
        this.mHandler = new Handler();
        this.mTempRect = new Rect();
        this.mContext = context;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.ListPopupWindow, i, 0);
        this.mDropDownHorizontalOffset = obtainStyledAttributes.getDimensionPixelOffset(R.styleable.ListPopupWindow_android_dropDownHorizontalOffset, 0);
        this.mDropDownVerticalOffset = obtainStyledAttributes.getDimensionPixelOffset(R.styleable.ListPopupWindow_android_dropDownVerticalOffset, 0);
        if (this.mDropDownVerticalOffset != 0) {
            this.mDropDownVerticalOffsetSet = true;
        }
        obtainStyledAttributes.recycle();
        this.mPopup = new AppCompatPopupWindow(context, attributeSet, i);
        this.mPopup.setInputMethodMode(1);
        this.mLayoutDirection = TextUtilsCompat.getLayoutDirectionFromLocale(this.mContext.getResources().getConfiguration().locale);
    }

    private int buildDropDown() {
        int makeMeasureSpec;
        LinearLayout linearLayout;
        int i = 0;
        if (this.mDropDownList == null) {
            Context context = this.mContext;
            this.mShowDropDownRunnable = new Runnable(this) { // from class: android.support.v7.widget.ListPopupWindow.2
                final ListPopupWindow this$0;

                {
                    this.this$0 = this;
                }

                @Override // java.lang.Runnable
                public void run() {
                    View anchorView = this.this$0.getAnchorView();
                    if (anchorView == null || anchorView.getWindowToken() == null) {
                        return;
                    }
                    this.this$0.show();
                }
            };
            this.mDropDownList = new DropDownListView(context, !this.mModal);
            Drawable drawable = this.mDropDownListHighlight;
            if (drawable != null) {
                this.mDropDownList.setSelector(drawable);
            }
            this.mDropDownList.setAdapter(this.mAdapter);
            this.mDropDownList.setOnItemClickListener(this.mItemClickListener);
            this.mDropDownList.setFocusable(true);
            this.mDropDownList.setFocusableInTouchMode(true);
            this.mDropDownList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(this) { // from class: android.support.v7.widget.ListPopupWindow.3
                final ListPopupWindow this$0;

                {
                    this.this$0 = this;
                }

                @Override // android.widget.AdapterView.OnItemSelectedListener
                public void onItemSelected(AdapterView<?> adapterView, View view, int i2, long j) {
                    DropDownListView dropDownListView;
                    if (i2 == -1 || (dropDownListView = this.this$0.mDropDownList) == null) {
                        return;
                    }
                    dropDownListView.mListSelectionHidden = false;
                }

                @Override // android.widget.AdapterView.OnItemSelectedListener
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
            this.mDropDownList.setOnScrollListener(this.mScrollListener);
            AdapterView.OnItemSelectedListener onItemSelectedListener = this.mItemSelectedListener;
            if (onItemSelectedListener != null) {
                this.mDropDownList.setOnItemSelectedListener(onItemSelectedListener);
            }
            DropDownListView dropDownListView = this.mDropDownList;
            View view = this.mPromptView;
            if (view != null) {
                LinearLayout linearLayout2 = new LinearLayout(context);
                linearLayout2.setOrientation(1);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, 0, 1.0f);
                switch (this.mPromptPosition) {
                    case 0:
                        linearLayout2.addView(view);
                        linearLayout2.addView(dropDownListView, layoutParams);
                        break;
                    case 1:
                        linearLayout2.addView(dropDownListView, layoutParams);
                        linearLayout2.addView(view);
                        break;
                    default:
                        Log.e(TAG, "Invalid hint position " + this.mPromptPosition);
                        break;
                }
                view.measure(View.MeasureSpec.makeMeasureSpec(this.mDropDownWidth, Integer.MIN_VALUE), 0);
                LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) view.getLayoutParams();
                i = view.getMeasuredHeight() + layoutParams2.topMargin + layoutParams2.bottomMargin;
                linearLayout = linearLayout2;
            } else {
                linearLayout = dropDownListView;
            }
            this.mPopup.setContentView(linearLayout);
        } else {
            ViewGroup viewGroup = (ViewGroup) this.mPopup.getContentView();
            View view2 = this.mPromptView;
            if (view2 != null) {
                LinearLayout.LayoutParams layoutParams3 = (LinearLayout.LayoutParams) view2.getLayoutParams();
                i = view2.getMeasuredHeight() + layoutParams3.topMargin + layoutParams3.bottomMargin;
            } else {
                i = 0;
            }
        }
        int i2 = 0;
        Drawable background = this.mPopup.getBackground();
        if (background != null) {
            background.getPadding(this.mTempRect);
            i2 = this.mTempRect.top + this.mTempRect.bottom;
            if (!this.mDropDownVerticalOffsetSet) {
                this.mDropDownVerticalOffset = -this.mTempRect.top;
            }
        } else {
            this.mTempRect.setEmpty();
        }
        if (this.mPopup.getInputMethodMode() == 2) {
        }
        int maxAvailableHeight = this.mPopup.getMaxAvailableHeight(getAnchorView(), this.mDropDownVerticalOffset);
        if (this.mDropDownAlwaysVisible || this.mDropDownHeight == -1) {
            return maxAvailableHeight + i2;
        }
        int i3 = this.mDropDownWidth;
        switch (i3) {
            case -2:
                makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(this.mContext.getResources().getDisplayMetrics().widthPixels - (this.mTempRect.left + this.mTempRect.right), Integer.MIN_VALUE);
                break;
            case -1:
                makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(this.mContext.getResources().getDisplayMetrics().widthPixels - (this.mTempRect.left + this.mTempRect.right), 1073741824);
                break;
            default:
                makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(i3, 1073741824);
                break;
        }
        int measureHeightOfChildrenCompat = this.mDropDownList.measureHeightOfChildrenCompat(makeMeasureSpec, 0, -1, maxAvailableHeight - i, -1);
        int i4 = i;
        if (measureHeightOfChildrenCompat > 0) {
            i4 = i + i2;
        }
        return measureHeightOfChildrenCompat + i4;
    }

    private static boolean isConfirmKey(int i) {
        return i == 66 || i == 23;
    }

    private void removePromptView() {
        View view = this.mPromptView;
        if (view != null) {
            ViewParent parent = view.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(this.mPromptView);
            }
        }
    }

    private void setPopupClipToScreenEnabled(boolean z) {
        Method method = sClipToWindowEnabledMethod;
        if (method != null) {
            try {
                method.invoke(this.mPopup, Boolean.valueOf(z));
            } catch (Exception e) {
                Log.i(TAG, "Could not call setClipToScreenEnabled() on PopupWindow. Oh well.");
            }
        }
    }

    public void clearListSelection() {
        DropDownListView dropDownListView = this.mDropDownList;
        if (dropDownListView != null) {
            dropDownListView.mListSelectionHidden = true;
            dropDownListView.requestLayout();
        }
    }

    public View.OnTouchListener createDragToOpenListener(View view) {
        return new ForwardingListener(this, view) { // from class: android.support.v7.widget.ListPopupWindow.1
            final ListPopupWindow this$0;

            {
                this.this$0 = this;
            }

            @Override // android.support.v7.widget.ListPopupWindow.ForwardingListener
            public ListPopupWindow getPopup() {
                return this.this$0;
            }
        };
    }

    public void dismiss() {
        this.mPopup.dismiss();
        removePromptView();
        this.mPopup.setContentView(null);
        this.mDropDownList = null;
        this.mHandler.removeCallbacks(this.mResizePopupRunnable);
    }

    public View getAnchorView() {
        return this.mDropDownAnchorView;
    }

    public int getAnimationStyle() {
        return this.mPopup.getAnimationStyle();
    }

    public Drawable getBackground() {
        return this.mPopup.getBackground();
    }

    public int getHeight() {
        return this.mDropDownHeight;
    }

    public int getHorizontalOffset() {
        return this.mDropDownHorizontalOffset;
    }

    public int getInputMethodMode() {
        return this.mPopup.getInputMethodMode();
    }

    public ListView getListView() {
        return this.mDropDownList;
    }

    public int getPromptPosition() {
        return this.mPromptPosition;
    }

    public Object getSelectedItem() {
        if (isShowing()) {
            return this.mDropDownList.getSelectedItem();
        }
        return null;
    }

    public long getSelectedItemId() {
        if (isShowing()) {
            return this.mDropDownList.getSelectedItemId();
        }
        return Long.MIN_VALUE;
    }

    public int getSelectedItemPosition() {
        if (isShowing()) {
            return this.mDropDownList.getSelectedItemPosition();
        }
        return -1;
    }

    public View getSelectedView() {
        if (isShowing()) {
            return this.mDropDownList.getSelectedView();
        }
        return null;
    }

    public int getSoftInputMode() {
        return this.mPopup.getSoftInputMode();
    }

    public int getVerticalOffset() {
        if (this.mDropDownVerticalOffsetSet) {
            return this.mDropDownVerticalOffset;
        }
        return 0;
    }

    public int getWidth() {
        return this.mDropDownWidth;
    }

    public boolean isDropDownAlwaysVisible() {
        return this.mDropDownAlwaysVisible;
    }

    public boolean isInputMethodNotNeeded() {
        return this.mPopup.getInputMethodMode() == 2;
    }

    public boolean isModal() {
        return this.mModal;
    }

    public boolean isShowing() {
        return this.mPopup.isShowing();
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (!isShowing() || i == 62) {
            return false;
        }
        if (this.mDropDownList.getSelectedItemPosition() >= 0 || !isConfirmKey(i)) {
            int selectedItemPosition = this.mDropDownList.getSelectedItemPosition();
            boolean z = !this.mPopup.isAboveAnchor();
            ListAdapter listAdapter = this.mAdapter;
            int i2 = Integer.MAX_VALUE;
            int i3 = Integer.MIN_VALUE;
            if (listAdapter != null) {
                boolean areAllItemsEnabled = listAdapter.areAllItemsEnabled();
                i2 = areAllItemsEnabled ? 0 : this.mDropDownList.lookForSelectablePosition(0, true);
                i3 = areAllItemsEnabled ? listAdapter.getCount() - 1 : this.mDropDownList.lookForSelectablePosition(listAdapter.getCount() - 1, false);
            }
            if ((z && i == 19 && selectedItemPosition <= i2) || (!z && i == 20 && selectedItemPosition >= i3)) {
                clearListSelection();
                this.mPopup.setInputMethodMode(1);
                show();
                return true;
            }
            this.mDropDownList.mListSelectionHidden = false;
            if (!this.mDropDownList.onKeyDown(i, keyEvent)) {
                return (z && i == 20) ? selectedItemPosition == i3 : !z && i == 19 && selectedItemPosition == i2;
            }
            this.mPopup.setInputMethodMode(2);
            this.mDropDownList.requestFocusFromTouch();
            show();
            if (i == 23 || i == 66) {
                return true;
            }
            switch (i) {
                case 19:
                case 20:
                    return true;
                default:
                    return false;
            }
        }
        return false;
    }

    public boolean onKeyPreIme(int i, KeyEvent keyEvent) {
        if (i == 4 && isShowing()) {
            View view = this.mDropDownAnchorView;
            if (keyEvent.getAction() == 0 && keyEvent.getRepeatCount() == 0) {
                KeyEvent.DispatcherState keyDispatcherState = view.getKeyDispatcherState();
                if (keyDispatcherState != null) {
                    keyDispatcherState.startTracking(keyEvent, this);
                    return true;
                }
                return true;
            } else if (keyEvent.getAction() == 1) {
                KeyEvent.DispatcherState keyDispatcherState2 = view.getKeyDispatcherState();
                if (keyDispatcherState2 != null) {
                    keyDispatcherState2.handleUpEvent(keyEvent);
                }
                if (!keyEvent.isTracking() || keyEvent.isCanceled()) {
                    return false;
                }
                dismiss();
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        if (!isShowing() || this.mDropDownList.getSelectedItemPosition() < 0) {
            return false;
        }
        boolean onKeyUp = this.mDropDownList.onKeyUp(i, keyEvent);
        if (onKeyUp && isConfirmKey(i)) {
            dismiss();
        }
        return onKeyUp;
    }

    public boolean performItemClick(int i) {
        if (isShowing()) {
            if (this.mItemClickListener != null) {
                DropDownListView dropDownListView = this.mDropDownList;
                this.mItemClickListener.onItemClick(dropDownListView, dropDownListView.getChildAt(i - dropDownListView.getFirstVisiblePosition()), i, dropDownListView.getAdapter().getItemId(i));
                return true;
            }
            return true;
        }
        return false;
    }

    public void postShow() {
        this.mHandler.post(this.mShowDropDownRunnable);
    }

    public void setAdapter(ListAdapter listAdapter) {
        DataSetObserver dataSetObserver = this.mObserver;
        if (dataSetObserver == null) {
            this.mObserver = new PopupDataSetObserver();
        } else {
            ListAdapter listAdapter2 = this.mAdapter;
            if (listAdapter2 != null) {
                listAdapter2.unregisterDataSetObserver(dataSetObserver);
            }
        }
        this.mAdapter = listAdapter;
        if (this.mAdapter != null) {
            listAdapter.registerDataSetObserver(this.mObserver);
        }
        DropDownListView dropDownListView = this.mDropDownList;
        if (dropDownListView != null) {
            dropDownListView.setAdapter(this.mAdapter);
        }
    }

    public void setAnchorView(View view) {
        this.mDropDownAnchorView = view;
    }

    public void setAnimationStyle(int i) {
        this.mPopup.setAnimationStyle(i);
    }

    public void setBackgroundDrawable(Drawable drawable) {
        this.mPopup.setBackgroundDrawable(drawable);
    }

    public void setContentWidth(int i) {
        Drawable background = this.mPopup.getBackground();
        if (background == null) {
            setWidth(i);
            return;
        }
        background.getPadding(this.mTempRect);
        this.mDropDownWidth = this.mTempRect.left + this.mTempRect.right + i;
    }

    public void setDropDownAlwaysVisible(boolean z) {
        this.mDropDownAlwaysVisible = z;
    }

    public void setDropDownGravity(int i) {
        this.mDropDownGravity = i;
    }

    public void setForceIgnoreOutsideTouch(boolean z) {
        this.mForceIgnoreOutsideTouch = z;
    }

    public void setHeight(int i) {
        this.mDropDownHeight = i;
    }

    public void setHorizontalOffset(int i) {
        this.mDropDownHorizontalOffset = i;
    }

    public void setInputMethodMode(int i) {
        this.mPopup.setInputMethodMode(i);
    }

    void setListItemExpandMax(int i) {
        this.mListItemExpandMaximum = i;
    }

    public void setListSelector(Drawable drawable) {
        this.mDropDownListHighlight = drawable;
    }

    public void setModal(boolean z) {
        this.mModal = z;
        this.mPopup.setFocusable(z);
    }

    public void setOnDismissListener(PopupWindow.OnDismissListener onDismissListener) {
        this.mPopup.setOnDismissListener(onDismissListener);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.mItemClickListener = onItemClickListener;
    }

    public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener onItemSelectedListener) {
        this.mItemSelectedListener = onItemSelectedListener;
    }

    public void setPromptPosition(int i) {
        this.mPromptPosition = i;
    }

    public void setPromptView(View view) {
        boolean isShowing = isShowing();
        if (isShowing) {
            removePromptView();
        }
        this.mPromptView = view;
        if (isShowing) {
            show();
        }
    }

    public void setSelection(int i) {
        DropDownListView dropDownListView = this.mDropDownList;
        if (!isShowing() || dropDownListView == null) {
            return;
        }
        dropDownListView.mListSelectionHidden = false;
        dropDownListView.setSelection(i);
        if (Build.VERSION.SDK_INT < 11 || dropDownListView.getChoiceMode() == 0) {
            return;
        }
        dropDownListView.setItemChecked(i, true);
    }

    public void setSoftInputMode(int i) {
        this.mPopup.setSoftInputMode(i);
    }

    public void setVerticalOffset(int i) {
        this.mDropDownVerticalOffset = i;
        this.mDropDownVerticalOffsetSet = true;
    }

    public void setWidth(int i) {
        this.mDropDownWidth = i;
    }

    public void show() {
        int i;
        int buildDropDown = buildDropDown();
        int i2 = 0;
        boolean isInputMethodNotNeeded = isInputMethodNotNeeded();
        boolean z = true;
        int i3 = -1;
        if (this.mPopup.isShowing()) {
            int i4 = this.mDropDownWidth;
            int width = i4 == -1 ? -1 : i4 == -2 ? getAnchorView().getWidth() : this.mDropDownWidth;
            int i5 = this.mDropDownHeight;
            if (i5 == -1) {
                if (!isInputMethodNotNeeded) {
                    buildDropDown = -1;
                }
                if (isInputMethodNotNeeded) {
                    PopupWindow popupWindow = this.mPopup;
                    if (this.mDropDownWidth != -1) {
                        i3 = 0;
                    }
                    popupWindow.setWindowLayoutMode(i3, 0);
                } else {
                    this.mPopup.setWindowLayoutMode(this.mDropDownWidth == -1 ? -1 : 0, -1);
                }
            } else if (i5 != -2) {
                buildDropDown = this.mDropDownHeight;
            }
            PopupWindow popupWindow2 = this.mPopup;
            if (this.mForceIgnoreOutsideTouch || this.mDropDownAlwaysVisible) {
                z = false;
            }
            popupWindow2.setOutsideTouchable(z);
            this.mPopup.update(getAnchorView(), this.mDropDownHorizontalOffset, this.mDropDownVerticalOffset, width, buildDropDown);
            return;
        }
        int i6 = this.mDropDownWidth;
        if (i6 == -1) {
            i2 = -1;
        } else if (i6 == -2) {
            this.mPopup.setWidth(getAnchorView().getWidth());
        } else {
            this.mPopup.setWidth(i6);
        }
        int i7 = this.mDropDownHeight;
        if (i7 == -1) {
            i = -1;
        } else if (i7 == -2) {
            this.mPopup.setHeight(buildDropDown);
            i = 0;
        } else {
            this.mPopup.setHeight(i7);
            i = 0;
        }
        this.mPopup.setWindowLayoutMode(i2, i);
        setPopupClipToScreenEnabled(true);
        this.mPopup.setOutsideTouchable((this.mForceIgnoreOutsideTouch || this.mDropDownAlwaysVisible) ? false : true);
        this.mPopup.setTouchInterceptor(this.mTouchInterceptor);
        PopupWindowCompat.showAsDropDown(this.mPopup, getAnchorView(), this.mDropDownHorizontalOffset, this.mDropDownVerticalOffset, this.mDropDownGravity);
        this.mDropDownList.setSelection(-1);
        if (!this.mModal || this.mDropDownList.isInTouchMode()) {
            clearListSelection();
        }
        if (this.mModal) {
            return;
        }
        this.mHandler.post(this.mHideSelector);
    }
}