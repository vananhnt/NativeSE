package android.support.v4.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.view.accessibility.AccessibilityRecordCompat;
import android.support.v4.widget.EdgeEffectCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.Interpolator;
import android.widget.Scroller;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/* loaded from: ViewPager.class */
public class ViewPager extends ViewGroup {
    private static final int CLOSE_ENOUGH = 2;
    private static final boolean DEBUG = false;
    private static final int DEFAULT_GUTTER_SIZE = 16;
    private static final int DEFAULT_OFFSCREEN_PAGES = 1;
    private static final int DRAW_ORDER_DEFAULT = 0;
    private static final int DRAW_ORDER_FORWARD = 1;
    private static final int DRAW_ORDER_REVERSE = 2;
    private static final int INVALID_POINTER = -1;
    private static final int MAX_SETTLE_DURATION = 600;
    private static final int MIN_DISTANCE_FOR_FLING = 25;
    private static final int MIN_FLING_VELOCITY = 400;
    public static final int SCROLL_STATE_DRAGGING = 1;
    public static final int SCROLL_STATE_IDLE = 0;
    public static final int SCROLL_STATE_SETTLING = 2;
    private static final String TAG = "ViewPager";
    private static final boolean USE_CACHE = false;
    private int mActivePointerId;
    private PagerAdapter mAdapter;
    private OnAdapterChangeListener mAdapterChangeListener;
    private int mBottomPageBounds;
    private boolean mCalledSuper;
    private int mChildHeightMeasureSpec;
    private int mChildWidthMeasureSpec;
    private int mCloseEnough;
    private int mCurItem;
    private int mDecorChildCount;
    private int mDefaultGutterSize;
    private int mDrawingOrder;
    private ArrayList<View> mDrawingOrderedChildren;
    private final Runnable mEndScrollRunnable;
    private int mExpectedAdapterCount;
    private long mFakeDragBeginTime;
    private boolean mFakeDragging;
    private boolean mFirstLayout;
    private float mFirstOffset;
    private int mFlingDistance;
    private int mGutterSize;
    private boolean mIgnoreGutter;
    private boolean mInLayout;
    private float mInitialMotionX;
    private float mInitialMotionY;
    private OnPageChangeListener mInternalPageChangeListener;
    private boolean mIsBeingDragged;
    private boolean mIsUnableToDrag;
    private final ArrayList<ItemInfo> mItems;
    private float mLastMotionX;
    private float mLastMotionY;
    private float mLastOffset;
    private EdgeEffectCompat mLeftEdge;
    private Drawable mMarginDrawable;
    private int mMaximumVelocity;
    private int mMinimumVelocity;
    private boolean mNeedCalculatePageOffsets;
    private PagerObserver mObserver;
    private int mOffscreenPageLimit;
    private OnPageChangeListener mOnPageChangeListener;
    private int mPageMargin;
    private PageTransformer mPageTransformer;
    private boolean mPopulatePending;
    private Parcelable mRestoredAdapterState;
    private ClassLoader mRestoredClassLoader;
    private int mRestoredCurItem;
    private EdgeEffectCompat mRightEdge;
    private int mScrollState;
    private Scroller mScroller;
    private boolean mScrollingCacheEnabled;
    private Method mSetChildrenDrawingOrderEnabled;
    private final ItemInfo mTempItem;
    private final Rect mTempRect;
    private int mTopPageBounds;
    private int mTouchSlop;
    private VelocityTracker mVelocityTracker;
    private static final int[] LAYOUT_ATTRS = {16842931};
    private static final Comparator<ItemInfo> COMPARATOR = new Comparator<ItemInfo>() { // from class: android.support.v4.view.ViewPager.1
        @Override // java.util.Comparator
        public int compare(ItemInfo itemInfo, ItemInfo itemInfo2) {
            return itemInfo.position - itemInfo2.position;
        }
    };
    private static final Interpolator sInterpolator = new Interpolator() { // from class: android.support.v4.view.ViewPager.2
        @Override // android.animation.TimeInterpolator
        public float getInterpolation(float f) {
            float f2 = f - 1.0f;
            return (f2 * f2 * f2 * f2 * f2) + 1.0f;
        }
    };
    private static final ViewPositionComparator sPositionComparator = new ViewPositionComparator();

    /* loaded from: ViewPager$Decor.class */
    interface Decor {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ViewPager$ItemInfo.class */
    public static class ItemInfo {
        Object object;
        float offset;
        int position;
        boolean scrolling;
        float widthFactor;

        ItemInfo() {
        }
    }

    /* loaded from: ViewPager$LayoutParams.class */
    public static class LayoutParams extends ViewGroup.LayoutParams {
        int childIndex;
        public int gravity;
        public boolean isDecor;
        boolean needsMeasure;
        int position;
        float widthFactor;

        public LayoutParams() {
            super(-1, -1);
            this.widthFactor = 0.0f;
        }

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            this.widthFactor = 0.0f;
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, ViewPager.LAYOUT_ATTRS);
            this.gravity = obtainStyledAttributes.getInteger(0, 48);
            obtainStyledAttributes.recycle();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ViewPager$MyAccessibilityDelegate.class */
    public class MyAccessibilityDelegate extends AccessibilityDelegateCompat {
        final ViewPager this$0;

        MyAccessibilityDelegate(ViewPager viewPager) {
            this.this$0 = viewPager;
        }

        private boolean canScroll() {
            boolean z = true;
            if (this.this$0.mAdapter == null || this.this$0.mAdapter.getCount() <= 1) {
                z = false;
            }
            return z;
        }

        @Override // android.support.v4.view.AccessibilityDelegateCompat
        public void onInitializeAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
            super.onInitializeAccessibilityEvent(view, accessibilityEvent);
            accessibilityEvent.setClassName(ViewPager.class.getName());
            AccessibilityRecordCompat obtain = AccessibilityRecordCompat.obtain();
            obtain.setScrollable(canScroll());
            if (accessibilityEvent.getEventType() != 4096 || this.this$0.mAdapter == null) {
                return;
            }
            obtain.setItemCount(this.this$0.mAdapter.getCount());
            obtain.setFromIndex(this.this$0.mCurItem);
            obtain.setToIndex(this.this$0.mCurItem);
        }

        @Override // android.support.v4.view.AccessibilityDelegateCompat
        public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
            accessibilityNodeInfoCompat.setClassName(ViewPager.class.getName());
            accessibilityNodeInfoCompat.setScrollable(canScroll());
            if (this.this$0.canScrollHorizontally(1)) {
                accessibilityNodeInfoCompat.addAction(4096);
            }
            if (this.this$0.canScrollHorizontally(-1)) {
                accessibilityNodeInfoCompat.addAction(8192);
            }
        }

        @Override // android.support.v4.view.AccessibilityDelegateCompat
        public boolean performAccessibilityAction(View view, int i, Bundle bundle) {
            if (super.performAccessibilityAction(view, i, bundle)) {
                return true;
            }
            if (i == 4096) {
                if (this.this$0.canScrollHorizontally(1)) {
                    ViewPager viewPager = this.this$0;
                    viewPager.setCurrentItem(viewPager.mCurItem + 1);
                    return true;
                }
                return false;
            } else if (i == 8192 && this.this$0.canScrollHorizontally(-1)) {
                ViewPager viewPager2 = this.this$0;
                viewPager2.setCurrentItem(viewPager2.mCurItem - 1);
                return true;
            } else {
                return false;
            }
        }
    }

    /* loaded from: ViewPager$OnAdapterChangeListener.class */
    interface OnAdapterChangeListener {
        void onAdapterChanged(PagerAdapter pagerAdapter, PagerAdapter pagerAdapter2);
    }

    /* loaded from: ViewPager$OnPageChangeListener.class */
    public interface OnPageChangeListener {
        void onPageScrollStateChanged(int i);

        void onPageScrolled(int i, float f, int i2);

        void onPageSelected(int i);
    }

    /* loaded from: ViewPager$PageTransformer.class */
    public interface PageTransformer {
        void transformPage(View view, float f);
    }

    /* loaded from: ViewPager$PagerObserver.class */
    private class PagerObserver extends DataSetObserver {
        final ViewPager this$0;

        private PagerObserver(ViewPager viewPager) {
            this.this$0 = viewPager;
        }

        @Override // android.database.DataSetObserver
        public void onChanged() {
            this.this$0.dataSetChanged();
        }

        @Override // android.database.DataSetObserver
        public void onInvalidated() {
            this.this$0.dataSetChanged();
        }
    }

    /* loaded from: ViewPager$SavedState.class */
    public static class SavedState extends View.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<SavedState>() { // from class: android.support.v4.view.ViewPager.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.support.v4.os.ParcelableCompatCreatorCallbacks
            public SavedState createFromParcel(Parcel parcel, ClassLoader classLoader) {
                return new SavedState(parcel, classLoader);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.support.v4.os.ParcelableCompatCreatorCallbacks
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        });
        Parcelable adapterState;
        ClassLoader loader;
        int position;

        SavedState(Parcel parcel, ClassLoader classLoader) {
            super(parcel);
            classLoader = classLoader == null ? getClass().getClassLoader() : classLoader;
            this.position = parcel.readInt();
            this.adapterState = parcel.readParcelable(classLoader);
            this.loader = classLoader;
        }

        public SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        public String toString() {
            return "FragmentPager.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " position=" + this.position + "}";
        }

        @Override // android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.position);
            parcel.writeParcelable(this.adapterState, i);
        }
    }

    /* loaded from: ViewPager$SimpleOnPageChangeListener.class */
    public static class SimpleOnPageChangeListener implements OnPageChangeListener {
        @Override // android.support.v4.view.ViewPager.OnPageChangeListener
        public void onPageScrollStateChanged(int i) {
        }

        @Override // android.support.v4.view.ViewPager.OnPageChangeListener
        public void onPageScrolled(int i, float f, int i2) {
        }

        @Override // android.support.v4.view.ViewPager.OnPageChangeListener
        public void onPageSelected(int i) {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ViewPager$ViewPositionComparator.class */
    public static class ViewPositionComparator implements Comparator<View> {
        ViewPositionComparator() {
        }

        @Override // java.util.Comparator
        public int compare(View view, View view2) {
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            LayoutParams layoutParams2 = (LayoutParams) view2.getLayoutParams();
            if (layoutParams.isDecor != layoutParams2.isDecor) {
                return layoutParams.isDecor ? 1 : -1;
            }
            return layoutParams.position - layoutParams2.position;
        }
    }

    public ViewPager(Context context) {
        super(context);
        this.mItems = new ArrayList<>();
        this.mTempItem = new ItemInfo();
        this.mTempRect = new Rect();
        this.mRestoredCurItem = -1;
        this.mRestoredAdapterState = null;
        this.mRestoredClassLoader = null;
        this.mFirstOffset = -3.4028235E38f;
        this.mLastOffset = Float.MAX_VALUE;
        this.mOffscreenPageLimit = 1;
        this.mActivePointerId = -1;
        this.mFirstLayout = true;
        this.mNeedCalculatePageOffsets = false;
        this.mEndScrollRunnable = new Runnable(this) { // from class: android.support.v4.view.ViewPager.3
            final ViewPager this$0;

            {
                this.this$0 = this;
            }

            @Override // java.lang.Runnable
            public void run() {
                this.this$0.setScrollState(0);
                this.this$0.populate();
            }
        };
        this.mScrollState = 0;
        initViewPager();
    }

    public ViewPager(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mItems = new ArrayList<>();
        this.mTempItem = new ItemInfo();
        this.mTempRect = new Rect();
        this.mRestoredCurItem = -1;
        this.mRestoredAdapterState = null;
        this.mRestoredClassLoader = null;
        this.mFirstOffset = -3.4028235E38f;
        this.mLastOffset = Float.MAX_VALUE;
        this.mOffscreenPageLimit = 1;
        this.mActivePointerId = -1;
        this.mFirstLayout = true;
        this.mNeedCalculatePageOffsets = false;
        this.mEndScrollRunnable = new Runnable(this) { // from class: android.support.v4.view.ViewPager.3
            final ViewPager this$0;

            {
                this.this$0 = this;
            }

            @Override // java.lang.Runnable
            public void run() {
                this.this$0.setScrollState(0);
                this.this$0.populate();
            }
        };
        this.mScrollState = 0;
        initViewPager();
    }

    private void calculatePageOffsets(ItemInfo itemInfo, int i, ItemInfo itemInfo2) {
        ItemInfo itemInfo3;
        ItemInfo itemInfo4;
        int count = this.mAdapter.getCount();
        int clientWidth = getClientWidth();
        float f = clientWidth > 0 ? this.mPageMargin / clientWidth : 0.0f;
        if (itemInfo2 != null) {
            int i2 = itemInfo2.position;
            if (i2 < itemInfo.position) {
                int i3 = 0;
                float f2 = itemInfo2.offset + itemInfo2.widthFactor;
                float f3 = f;
                while (true) {
                    float f4 = f2 + f3;
                    i2++;
                    if (i2 > itemInfo.position || i3 >= this.mItems.size()) {
                        break;
                    }
                    ItemInfo itemInfo5 = this.mItems.get(i3);
                    while (true) {
                        itemInfo4 = itemInfo5;
                        if (i2 <= itemInfo4.position || i3 >= this.mItems.size() - 1) {
                            break;
                        }
                        i3++;
                        itemInfo5 = this.mItems.get(i3);
                    }
                    while (i2 < itemInfo4.position) {
                        f4 += this.mAdapter.getPageWidth(i2) + f;
                        i2++;
                    }
                    itemInfo4.offset = f4;
                    f2 = f4;
                    f3 = itemInfo4.widthFactor + f;
                }
            } else if (i2 > itemInfo.position) {
                int size = this.mItems.size() - 1;
                float f5 = itemInfo2.offset;
                while (true) {
                    i2--;
                    if (i2 < itemInfo.position || size < 0) {
                        break;
                    }
                    ItemInfo itemInfo6 = this.mItems.get(size);
                    while (true) {
                        itemInfo3 = itemInfo6;
                        if (i2 >= itemInfo3.position || size <= 0) {
                            break;
                        }
                        size--;
                        itemInfo6 = this.mItems.get(size);
                    }
                    while (i2 > itemInfo3.position) {
                        f5 -= this.mAdapter.getPageWidth(i2) + f;
                        i2--;
                    }
                    f5 -= itemInfo3.widthFactor + f;
                    itemInfo3.offset = f5;
                }
            }
        }
        int size2 = this.mItems.size();
        float f6 = itemInfo.offset;
        int i4 = itemInfo.position - 1;
        this.mFirstOffset = itemInfo.position == 0 ? itemInfo.offset : -3.4028235E38f;
        this.mLastOffset = itemInfo.position == count - 1 ? (itemInfo.offset + itemInfo.widthFactor) - 1.0f : Float.MAX_VALUE;
        int i5 = i - 1;
        float f7 = f6;
        while (i5 >= 0) {
            ItemInfo itemInfo7 = this.mItems.get(i5);
            while (i4 > itemInfo7.position) {
                f7 -= this.mAdapter.getPageWidth(i4) + f;
                i4--;
            }
            f7 -= itemInfo7.widthFactor + f;
            itemInfo7.offset = f7;
            if (itemInfo7.position == 0) {
                this.mFirstOffset = f7;
            }
            i5--;
            i4--;
        }
        float f8 = itemInfo.offset + itemInfo.widthFactor + f;
        int i6 = i + 1;
        int i7 = itemInfo.position + 1;
        while (i6 < size2) {
            ItemInfo itemInfo8 = this.mItems.get(i6);
            while (i7 < itemInfo8.position) {
                f8 += this.mAdapter.getPageWidth(i7) + f;
                i7++;
            }
            if (itemInfo8.position == count - 1) {
                this.mLastOffset = (itemInfo8.widthFactor + f8) - 1.0f;
            }
            itemInfo8.offset = f8;
            f8 += itemInfo8.widthFactor + f;
            i6++;
            i7++;
        }
        this.mNeedCalculatePageOffsets = false;
    }

    private void completeScroll(boolean z) {
        boolean z2 = this.mScrollState == 2;
        if (z2) {
            setScrollingCacheEnabled(false);
            this.mScroller.abortAnimation();
            int scrollX = getScrollX();
            int scrollY = getScrollY();
            int currX = this.mScroller.getCurrX();
            int currY = this.mScroller.getCurrY();
            if (scrollX != currX || scrollY != currY) {
                scrollTo(currX, currY);
            }
        }
        this.mPopulatePending = false;
        for (int i = 0; i < this.mItems.size(); i++) {
            ItemInfo itemInfo = this.mItems.get(i);
            if (itemInfo.scrolling) {
                z2 = true;
                itemInfo.scrolling = false;
            }
        }
        if (z2) {
            if (z) {
                ViewCompat.postOnAnimation(this, this.mEndScrollRunnable);
            } else {
                this.mEndScrollRunnable.run();
            }
        }
    }

    private int determineTargetPage(int i, float f, int i2, int i3) {
        if (Math.abs(i3) <= this.mFlingDistance || Math.abs(i2) <= this.mMinimumVelocity) {
            i = (int) (i + f + (i >= this.mCurItem ? 0.4f : 0.6f));
        } else if (i2 <= 0) {
            i++;
        }
        if (this.mItems.size() > 0) {
            ItemInfo itemInfo = this.mItems.get(0);
            ArrayList<ItemInfo> arrayList = this.mItems;
            i = Math.max(itemInfo.position, Math.min(i, arrayList.get(arrayList.size() - 1).position));
        }
        return i;
    }

    private void enableLayers(boolean z) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            ViewCompat.setLayerType(getChildAt(i), z ? 2 : 0, null);
        }
    }

    private void endDrag() {
        this.mIsBeingDragged = false;
        this.mIsUnableToDrag = false;
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    private Rect getChildRectInPagerCoordinates(Rect rect, View view) {
        if (rect == null) {
            rect = new Rect();
        }
        if (view == null) {
            rect.set(0, 0, 0, 0);
            return rect;
        }
        rect.left = view.getLeft();
        rect.right = view.getRight();
        rect.top = view.getTop();
        rect.bottom = view.getBottom();
        ViewParent parent = view.getParent();
        while (true) {
            ViewParent viewParent = parent;
            if (!(viewParent instanceof ViewGroup) || viewParent == this) {
                break;
            }
            ViewGroup viewGroup = (ViewGroup) viewParent;
            rect.left += viewGroup.getLeft();
            rect.right += viewGroup.getRight();
            rect.top += viewGroup.getTop();
            rect.bottom += viewGroup.getBottom();
            parent = viewGroup.getParent();
        }
        return rect;
    }

    private int getClientWidth() {
        return (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight();
    }

    private ItemInfo infoForCurrentScrollPosition() {
        int clientWidth = getClientWidth();
        float f = 0.0f;
        float scrollX = clientWidth > 0 ? getScrollX() / clientWidth : 0.0f;
        if (clientWidth > 0) {
            f = this.mPageMargin / clientWidth;
        }
        int i = -1;
        float f2 = 0.0f;
        float f3 = 0.0f;
        boolean z = true;
        ItemInfo itemInfo = null;
        int i2 = 0;
        while (i2 < this.mItems.size()) {
            ItemInfo itemInfo2 = this.mItems.get(i2);
            if (!z && itemInfo2.position != i + 1) {
                itemInfo2 = this.mTempItem;
                itemInfo2.offset = f2 + f3 + f;
                itemInfo2.position = i + 1;
                itemInfo2.widthFactor = this.mAdapter.getPageWidth(itemInfo2.position);
                i2--;
            }
            f2 = itemInfo2.offset;
            float f4 = itemInfo2.widthFactor;
            if (!z && scrollX < f2) {
                return itemInfo;
            }
            if (scrollX < f4 + f2 + f || i2 == this.mItems.size() - 1) {
                return itemInfo2;
            }
            z = false;
            i = itemInfo2.position;
            f3 = itemInfo2.widthFactor;
            i2++;
            itemInfo = itemInfo2;
        }
        return itemInfo;
    }

    private boolean isGutterDrag(float f, float f2) {
        return (f < ((float) this.mGutterSize) && f2 > 0.0f) || (f > ((float) (getWidth() - this.mGutterSize)) && f2 < 0.0f);
    }

    private void onSecondaryPointerUp(MotionEvent motionEvent) {
        int actionIndex = MotionEventCompat.getActionIndex(motionEvent);
        if (MotionEventCompat.getPointerId(motionEvent, actionIndex) == this.mActivePointerId) {
            int i = actionIndex == 0 ? 1 : 0;
            this.mLastMotionX = MotionEventCompat.getX(motionEvent, i);
            this.mActivePointerId = MotionEventCompat.getPointerId(motionEvent, i);
            VelocityTracker velocityTracker = this.mVelocityTracker;
            if (velocityTracker != null) {
                velocityTracker.clear();
            }
        }
    }

    private boolean pageScrolled(int i) {
        if (this.mItems.size() == 0) {
            this.mCalledSuper = false;
            onPageScrolled(0, 0.0f, 0);
            if (this.mCalledSuper) {
                return false;
            }
            throw new IllegalStateException("onPageScrolled did not call superclass implementation");
        }
        ItemInfo infoForCurrentScrollPosition = infoForCurrentScrollPosition();
        int clientWidth = getClientWidth();
        int i2 = this.mPageMargin;
        float f = i2 / clientWidth;
        int i3 = infoForCurrentScrollPosition.position;
        float f2 = ((i / clientWidth) - infoForCurrentScrollPosition.offset) / (infoForCurrentScrollPosition.widthFactor + f);
        this.mCalledSuper = false;
        onPageScrolled(i3, f2, (int) ((clientWidth + i2) * f2));
        if (this.mCalledSuper) {
            return true;
        }
        throw new IllegalStateException("onPageScrolled did not call superclass implementation");
    }

    private boolean performDrag(float f) {
        boolean z = false;
        float f2 = this.mLastMotionX;
        this.mLastMotionX = f;
        float scrollX = getScrollX() + (f2 - f);
        int clientWidth = getClientWidth();
        float f3 = clientWidth * this.mFirstOffset;
        float f4 = clientWidth * this.mLastOffset;
        boolean z2 = true;
        boolean z3 = true;
        ItemInfo itemInfo = this.mItems.get(0);
        ArrayList<ItemInfo> arrayList = this.mItems;
        ItemInfo itemInfo2 = arrayList.get(arrayList.size() - 1);
        if (itemInfo.position != 0) {
            z2 = false;
            f3 = itemInfo.offset * clientWidth;
        }
        if (itemInfo2.position != this.mAdapter.getCount() - 1) {
            z3 = false;
            f4 = itemInfo2.offset * clientWidth;
        }
        if (scrollX < f3) {
            z = z2 ? this.mLeftEdge.onPull(Math.abs(f3 - scrollX) / clientWidth) : false;
        } else if (scrollX > f4) {
            z = z3 ? this.mRightEdge.onPull(Math.abs(scrollX - f4) / clientWidth) : false;
            f3 = f4;
        } else {
            f3 = scrollX;
        }
        this.mLastMotionX += f3 - ((int) f3);
        scrollTo((int) f3, getScrollY());
        pageScrolled((int) f3);
        return z;
    }

    private void recomputeScrollPosition(int i, int i2, int i3, int i4) {
        if (i2 <= 0 || this.mItems.isEmpty()) {
            ItemInfo infoForPosition = infoForPosition(this.mCurItem);
            int paddingLeft = (int) (((i - getPaddingLeft()) - getPaddingRight()) * (infoForPosition != null ? Math.min(infoForPosition.offset, this.mLastOffset) : 0.0f));
            if (paddingLeft != getScrollX()) {
                completeScroll(false);
                scrollTo(paddingLeft, getScrollY());
                return;
            }
            return;
        }
        int paddingLeft2 = getPaddingLeft();
        int paddingRight = (int) ((((i - paddingLeft2) - getPaddingRight()) + i3) * (getScrollX() / (((i2 - getPaddingLeft()) - getPaddingRight()) + i4)));
        scrollTo(paddingRight, getScrollY());
        if (this.mScroller.isFinished()) {
            return;
        }
        this.mScroller.startScroll(paddingRight, 0, (int) (infoForPosition(this.mCurItem).offset * i), 0, this.mScroller.getDuration() - this.mScroller.timePassed());
    }

    private void removeNonDecorViews() {
        int i = 0;
        while (i < getChildCount()) {
            if (!((LayoutParams) getChildAt(i).getLayoutParams()).isDecor) {
                removeViewAt(i);
                i--;
            }
            i++;
        }
    }

    private void requestParentDisallowInterceptTouchEvent(boolean z) {
        ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(z);
        }
    }

    private void scrollToItem(int i, boolean z, int i2, boolean z2) {
        OnPageChangeListener onPageChangeListener;
        OnPageChangeListener onPageChangeListener2;
        OnPageChangeListener onPageChangeListener3;
        OnPageChangeListener onPageChangeListener4;
        ItemInfo infoForPosition = infoForPosition(i);
        int i3 = 0;
        if (infoForPosition != null) {
            i3 = (int) (getClientWidth() * Math.max(this.mFirstOffset, Math.min(infoForPosition.offset, this.mLastOffset)));
        }
        if (z) {
            smoothScrollTo(i3, 0, i2);
            if (z2 && (onPageChangeListener4 = this.mOnPageChangeListener) != null) {
                onPageChangeListener4.onPageSelected(i);
            }
            if (!z2 || (onPageChangeListener3 = this.mInternalPageChangeListener) == null) {
                return;
            }
            onPageChangeListener3.onPageSelected(i);
            return;
        }
        if (z2 && (onPageChangeListener2 = this.mOnPageChangeListener) != null) {
            onPageChangeListener2.onPageSelected(i);
        }
        if (z2 && (onPageChangeListener = this.mInternalPageChangeListener) != null) {
            onPageChangeListener.onPageSelected(i);
        }
        completeScroll(false);
        scrollTo(i3, 0);
        pageScrolled(i3);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setScrollState(int i) {
        if (this.mScrollState == i) {
            return;
        }
        this.mScrollState = i;
        if (this.mPageTransformer != null) {
            enableLayers(i != 0);
        }
        OnPageChangeListener onPageChangeListener = this.mOnPageChangeListener;
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageScrollStateChanged(i);
        }
    }

    private void setScrollingCacheEnabled(boolean z) {
        if (this.mScrollingCacheEnabled != z) {
            this.mScrollingCacheEnabled = z;
        }
    }

    private void sortChildDrawingOrder() {
        if (this.mDrawingOrder != 0) {
            ArrayList<View> arrayList = this.mDrawingOrderedChildren;
            if (arrayList == null) {
                this.mDrawingOrderedChildren = new ArrayList<>();
            } else {
                arrayList.clear();
            }
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                this.mDrawingOrderedChildren.add(getChildAt(i));
            }
            Collections.sort(this.mDrawingOrderedChildren, sPositionComparator);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public void addFocusables(ArrayList<View> arrayList, int i, int i2) {
        ItemInfo infoForChild;
        int size = arrayList.size();
        int descendantFocusability = getDescendantFocusability();
        if (descendantFocusability != 393216) {
            for (int i3 = 0; i3 < getChildCount(); i3++) {
                View childAt = getChildAt(i3);
                if (childAt.getVisibility() == 0 && (infoForChild = infoForChild(childAt)) != null && infoForChild.position == this.mCurItem) {
                    childAt.addFocusables(arrayList, i, i2);
                }
            }
        }
        if ((descendantFocusability != 262144 || size == arrayList.size()) && isFocusable()) {
            if (((i2 & 1) == 1 && isInTouchMode() && !isFocusableInTouchMode()) || arrayList == null) {
                return;
            }
            arrayList.add(this);
        }
    }

    ItemInfo addNewItem(int i, int i2) {
        ItemInfo itemInfo = new ItemInfo();
        itemInfo.position = i;
        itemInfo.object = this.mAdapter.instantiateItem((ViewGroup) this, i);
        itemInfo.widthFactor = this.mAdapter.getPageWidth(i);
        if (i2 < 0 || i2 >= this.mItems.size()) {
            this.mItems.add(itemInfo);
        } else {
            this.mItems.add(i2, itemInfo);
        }
        return itemInfo;
    }

    @Override // android.view.ViewGroup, android.view.View
    public void addTouchables(ArrayList<View> arrayList) {
        ItemInfo infoForChild;
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            if (childAt.getVisibility() == 0 && (infoForChild = infoForChild(childAt)) != null && infoForChild.position == this.mCurItem) {
                childAt.addTouchables(arrayList);
            }
        }
    }

    @Override // android.view.ViewGroup
    public void addView(View view, int i, ViewGroup.LayoutParams layoutParams) {
        if (!checkLayoutParams(layoutParams)) {
            layoutParams = generateLayoutParams(layoutParams);
        }
        LayoutParams layoutParams2 = (LayoutParams) layoutParams;
        layoutParams2.isDecor |= view instanceof Decor;
        if (!this.mInLayout) {
            super.addView(view, i, layoutParams);
        } else if (layoutParams2 != null && layoutParams2.isDecor) {
            throw new IllegalStateException("Cannot add pager decor view during layout");
        } else {
            layoutParams2.needsMeasure = true;
            addViewInLayout(view, i, layoutParams);
        }
    }

    public boolean arrowScroll(int i) {
        View view;
        View findFocus = findFocus();
        if (findFocus == this) {
            view = null;
        } else if (findFocus != null) {
            boolean z = false;
            ViewParent parent = findFocus.getParent();
            while (true) {
                ViewParent viewParent = parent;
                if (!(viewParent instanceof ViewGroup)) {
                    break;
                } else if (viewParent == this) {
                    z = true;
                    break;
                } else {
                    parent = viewParent.getParent();
                }
            }
            if (z) {
                view = findFocus;
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append(findFocus.getClass().getSimpleName());
                ViewParent parent2 = findFocus.getParent();
                while (true) {
                    ViewParent viewParent2 = parent2;
                    if (!(viewParent2 instanceof ViewGroup)) {
                        break;
                    }
                    sb.append(" => ");
                    sb.append(viewParent2.getClass().getSimpleName());
                    parent2 = viewParent2.getParent();
                }
                Log.e(TAG, "arrowScroll tried to find focus based on non-child current focused view " + sb.toString());
                view = null;
            }
        } else {
            view = findFocus;
        }
        boolean z2 = false;
        View findNextFocus = FocusFinder.getInstance().findNextFocus(this, view, i);
        if (findNextFocus == null || findNextFocus == view) {
            z2 = (i == 17 || i == 1) ? pageLeft() : (i == 66 || i == 2) ? pageRight() : false;
        } else if (i == 17) {
            z2 = (view == null || getChildRectInPagerCoordinates(this.mTempRect, findNextFocus).left < getChildRectInPagerCoordinates(this.mTempRect, view).left) ? findNextFocus.requestFocus() : pageLeft();
        } else if (i == 66) {
            z2 = (view == null || getChildRectInPagerCoordinates(this.mTempRect, findNextFocus).left > getChildRectInPagerCoordinates(this.mTempRect, view).left) ? findNextFocus.requestFocus() : pageRight();
        }
        if (z2) {
            playSoundEffect(SoundEffectConstants.getContantForFocusDirection(i));
        }
        return z2;
    }

    public boolean beginFakeDrag() {
        if (this.mIsBeingDragged) {
            return false;
        }
        this.mFakeDragging = true;
        setScrollState(1);
        this.mLastMotionX = 0.0f;
        this.mInitialMotionX = 0.0f;
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        } else {
            velocityTracker.clear();
        }
        long uptimeMillis = SystemClock.uptimeMillis();
        MotionEvent obtain = MotionEvent.obtain(uptimeMillis, uptimeMillis, 0, 0.0f, 0.0f, 0);
        this.mVelocityTracker.addMovement(obtain);
        obtain.recycle();
        this.mFakeDragBeginTime = uptimeMillis;
        return true;
    }

    protected boolean canScroll(View view, boolean z, int i, int i2, int i3) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int scrollX = view.getScrollX();
            int scrollY = view.getScrollY();
            for (int childCount = viewGroup.getChildCount() - 1; childCount >= 0; childCount--) {
                View childAt = viewGroup.getChildAt(childCount);
                if (i2 + scrollX >= childAt.getLeft() && i2 + scrollX < childAt.getRight() && i3 + scrollY >= childAt.getTop() && i3 + scrollY < childAt.getBottom() && canScroll(childAt, true, i, (i2 + scrollX) - childAt.getLeft(), (i3 + scrollY) - childAt.getTop())) {
                    return true;
                }
            }
        }
        return z && ViewCompat.canScrollHorizontally(view, -i);
    }

    @Override // android.view.View
    public boolean canScrollHorizontally(int i) {
        boolean z = false;
        if (this.mAdapter == null) {
            return false;
        }
        int clientWidth = getClientWidth();
        int scrollX = getScrollX();
        if (i < 0) {
            if (scrollX > ((int) (clientWidth * this.mFirstOffset))) {
                z = true;
            }
            return z;
        } else if (i > 0) {
            boolean z2 = false;
            if (scrollX < ((int) (clientWidth * this.mLastOffset))) {
                z2 = true;
            }
            return z2;
        } else {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public boolean checkLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return (layoutParams instanceof LayoutParams) && super.checkLayoutParams(layoutParams);
    }

    @Override // android.view.View
    public void computeScroll() {
        if (this.mScroller.isFinished() || !this.mScroller.computeScrollOffset()) {
            completeScroll(true);
            return;
        }
        int scrollX = getScrollX();
        int scrollY = getScrollY();
        int currX = this.mScroller.getCurrX();
        int currY = this.mScroller.getCurrY();
        if (scrollX != currX || scrollY != currY) {
            scrollTo(currX, currY);
            if (!pageScrolled(currX)) {
                this.mScroller.abortAnimation();
                scrollTo(0, currY);
            }
        }
        ViewCompat.postInvalidateOnAnimation(this);
    }

    void dataSetChanged() {
        int count = this.mAdapter.getCount();
        this.mExpectedAdapterCount = count;
        boolean z = this.mItems.size() < (this.mOffscreenPageLimit * 2) + 1 && this.mItems.size() < count;
        int i = this.mCurItem;
        boolean z2 = false;
        int i2 = 0;
        while (i2 < this.mItems.size()) {
            ItemInfo itemInfo = this.mItems.get(i2);
            int itemPosition = this.mAdapter.getItemPosition(itemInfo.object);
            if (itemPosition != -1) {
                if (itemPosition == -2) {
                    this.mItems.remove(i2);
                    i2--;
                    if (!z2) {
                        this.mAdapter.startUpdate((ViewGroup) this);
                        z2 = true;
                    }
                    this.mAdapter.destroyItem((ViewGroup) this, itemInfo.position, itemInfo.object);
                    z = true;
                    if (this.mCurItem == itemInfo.position) {
                        i = Math.max(0, Math.min(this.mCurItem, count - 1));
                        z = true;
                    }
                } else if (itemInfo.position != itemPosition) {
                    if (itemInfo.position == this.mCurItem) {
                        i = itemPosition;
                    }
                    itemInfo.position = itemPosition;
                    z = true;
                }
            }
            i2++;
        }
        if (z2) {
            this.mAdapter.finishUpdate((ViewGroup) this);
        }
        Collections.sort(this.mItems, COMPARATOR);
        if (z) {
            int childCount = getChildCount();
            for (int i3 = 0; i3 < childCount; i3++) {
                LayoutParams layoutParams = (LayoutParams) getChildAt(i3).getLayoutParams();
                if (!layoutParams.isDecor) {
                    layoutParams.widthFactor = 0.0f;
                }
            }
            setCurrentItemInternal(i, false, true);
            requestLayout();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        return super.dispatchKeyEvent(keyEvent) || executeKeyEvent(keyEvent);
    }

    @Override // android.view.View
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        ItemInfo infoForChild;
        if (accessibilityEvent.getEventType() == 4096) {
            return super.dispatchPopulateAccessibilityEvent(accessibilityEvent);
        }
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt.getVisibility() == 0 && (infoForChild = infoForChild(childAt)) != null && infoForChild.position == this.mCurItem && childAt.dispatchPopulateAccessibilityEvent(accessibilityEvent)) {
                return true;
            }
        }
        return false;
    }

    float distanceInfluenceForSnapDuration(float f) {
        return (float) Math.sin((float) ((f - 0.5f) * 0.4712389167638204d));
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        PagerAdapter pagerAdapter;
        super.draw(canvas);
        boolean z = false;
        int overScrollMode = ViewCompat.getOverScrollMode(this);
        if (overScrollMode == 0 || (overScrollMode == 1 && (pagerAdapter = this.mAdapter) != null && pagerAdapter.getCount() > 1)) {
            if (this.mLeftEdge.isFinished()) {
                z = false;
            } else {
                int save = canvas.save();
                int height = (getHeight() - getPaddingTop()) - getPaddingBottom();
                int width = getWidth();
                canvas.rotate(270.0f);
                canvas.translate((-height) + getPaddingTop(), this.mFirstOffset * width);
                this.mLeftEdge.setSize(height, width);
                z = false | this.mLeftEdge.draw(canvas);
                canvas.restoreToCount(save);
            }
            if (!this.mRightEdge.isFinished()) {
                int save2 = canvas.save();
                int width2 = getWidth();
                int height2 = getHeight();
                int paddingTop = getPaddingTop();
                int paddingBottom = getPaddingBottom();
                canvas.rotate(90.0f);
                canvas.translate(-getPaddingTop(), (-(this.mLastOffset + 1.0f)) * width2);
                this.mRightEdge.setSize((height2 - paddingTop) - paddingBottom, width2);
                z |= this.mRightEdge.draw(canvas);
                canvas.restoreToCount(save2);
            }
        } else {
            this.mLeftEdge.finish();
            this.mRightEdge.finish();
        }
        if (z) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void drawableStateChanged() {
        super.drawableStateChanged();
        Drawable drawable = this.mMarginDrawable;
        if (drawable == null || !drawable.isStateful()) {
            return;
        }
        drawable.setState(getDrawableState());
    }

    public void endFakeDrag() {
        if (!this.mFakeDragging) {
            throw new IllegalStateException("No fake drag in progress. Call beginFakeDrag first.");
        }
        VelocityTracker velocityTracker = this.mVelocityTracker;
        velocityTracker.computeCurrentVelocity(1000, this.mMaximumVelocity);
        int xVelocity = (int) VelocityTrackerCompat.getXVelocity(velocityTracker, this.mActivePointerId);
        this.mPopulatePending = true;
        int clientWidth = getClientWidth();
        int scrollX = getScrollX();
        ItemInfo infoForCurrentScrollPosition = infoForCurrentScrollPosition();
        setCurrentItemInternal(determineTargetPage(infoForCurrentScrollPosition.position, ((scrollX / clientWidth) - infoForCurrentScrollPosition.offset) / infoForCurrentScrollPosition.widthFactor, xVelocity, (int) (this.mLastMotionX - this.mInitialMotionX)), true, true, xVelocity);
        endDrag();
        this.mFakeDragging = false;
    }

    public boolean executeKeyEvent(KeyEvent keyEvent) {
        boolean z = false;
        if (keyEvent.getAction() == 0) {
            int keyCode = keyEvent.getKeyCode();
            if (keyCode != 61) {
                switch (keyCode) {
                    case 21:
                        z = arrowScroll(17);
                        break;
                    case 22:
                        z = arrowScroll(66);
                        break;
                }
            } else if (Build.VERSION.SDK_INT >= 11) {
                if (KeyEventCompat.hasNoModifiers(keyEvent)) {
                    z = arrowScroll(2);
                } else if (KeyEventCompat.hasModifiers(keyEvent, 1)) {
                    z = arrowScroll(1);
                }
            }
        }
        return z;
    }

    public void fakeDragBy(float f) {
        if (!this.mFakeDragging) {
            throw new IllegalStateException("No fake drag in progress. Call beginFakeDrag first.");
        }
        this.mLastMotionX += f;
        float scrollX = getScrollX() - f;
        int clientWidth = getClientWidth();
        float f2 = clientWidth * this.mFirstOffset;
        float f3 = clientWidth * this.mLastOffset;
        ItemInfo itemInfo = this.mItems.get(0);
        ArrayList<ItemInfo> arrayList = this.mItems;
        ItemInfo itemInfo2 = arrayList.get(arrayList.size() - 1);
        if (itemInfo.position != 0) {
            f2 = itemInfo.offset * clientWidth;
        }
        if (itemInfo2.position != this.mAdapter.getCount() - 1) {
            f3 = itemInfo2.offset * clientWidth;
        }
        if (scrollX >= f2) {
            f2 = scrollX > f3 ? f3 : scrollX;
        }
        this.mLastMotionX += f2 - ((int) f2);
        scrollTo((int) f2, getScrollY());
        pageScrolled((int) f2);
        MotionEvent obtain = MotionEvent.obtain(this.mFakeDragBeginTime, SystemClock.uptimeMillis(), 2, this.mLastMotionX, 0.0f, 0);
        this.mVelocityTracker.addMovement(obtain);
        obtain.recycle();
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams();
    }

    @Override // android.view.ViewGroup
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LayoutParams(getContext(), attributeSet);
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return generateDefaultLayoutParams();
    }

    public PagerAdapter getAdapter() {
        return this.mAdapter;
    }

    @Override // android.view.ViewGroup
    protected int getChildDrawingOrder(int i, int i2) {
        return ((LayoutParams) this.mDrawingOrderedChildren.get(this.mDrawingOrder == 2 ? (i - 1) - i2 : i2).getLayoutParams()).childIndex;
    }

    public int getCurrentItem() {
        return this.mCurItem;
    }

    public int getOffscreenPageLimit() {
        return this.mOffscreenPageLimit;
    }

    public int getPageMargin() {
        return this.mPageMargin;
    }

    ItemInfo infoForAnyChild(View view) {
        while (true) {
            ViewParent parent = view.getParent();
            if (parent == this) {
                return infoForChild(view);
            }
            if (parent == null || !(parent instanceof View)) {
                return null;
            }
            view = (View) parent;
        }
    }

    ItemInfo infoForChild(View view) {
        for (int i = 0; i < this.mItems.size(); i++) {
            ItemInfo itemInfo = this.mItems.get(i);
            if (this.mAdapter.isViewFromObject(view, itemInfo.object)) {
                return itemInfo;
            }
        }
        return null;
    }

    ItemInfo infoForPosition(int i) {
        for (int i2 = 0; i2 < this.mItems.size(); i2++) {
            ItemInfo itemInfo = this.mItems.get(i2);
            if (itemInfo.position == i) {
                return itemInfo;
            }
        }
        return null;
    }

    void initViewPager() {
        setWillNotDraw(false);
        setDescendantFocusability(262144);
        setFocusable(true);
        Context context = getContext();
        this.mScroller = new Scroller(context, sInterpolator);
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        float f = context.getResources().getDisplayMetrics().density;
        this.mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(viewConfiguration);
        this.mMinimumVelocity = (int) (400.0f * f);
        this.mMaximumVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
        this.mLeftEdge = new EdgeEffectCompat(context);
        this.mRightEdge = new EdgeEffectCompat(context);
        this.mFlingDistance = (int) (25.0f * f);
        this.mCloseEnough = (int) (2.0f * f);
        this.mDefaultGutterSize = (int) (16.0f * f);
        ViewCompat.setAccessibilityDelegate(this, new MyAccessibilityDelegate(this));
        if (ViewCompat.getImportantForAccessibility(this) == 0) {
            ViewCompat.setImportantForAccessibility(this, 1);
        }
    }

    public boolean isFakeDragging() {
        return this.mFakeDragging;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mFirstLayout = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        removeCallbacks(this.mEndScrollRunnable);
        super.onDetachedFromWindow();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        float f;
        super.onDraw(canvas);
        if (this.mPageMargin <= 0 || this.mMarginDrawable == null || this.mItems.size() <= 0 || this.mAdapter == null) {
            return;
        }
        int scrollX = getScrollX();
        int width = getWidth();
        float f2 = this.mPageMargin / width;
        int i = 0;
        ItemInfo itemInfo = this.mItems.get(0);
        float f3 = itemInfo.offset;
        int size = this.mItems.size();
        int i2 = this.mItems.get(size - 1).position;
        for (int i3 = itemInfo.position; i3 < i2; i3++) {
            while (i3 > itemInfo.position && i < size) {
                i++;
                itemInfo = this.mItems.get(i);
            }
            if (i3 == itemInfo.position) {
                f = (itemInfo.offset + itemInfo.widthFactor) * width;
                f3 = itemInfo.offset + itemInfo.widthFactor + f2;
            } else {
                float pageWidth = this.mAdapter.getPageWidth(i3);
                f = (f3 + pageWidth) * width;
                f3 += pageWidth + f2;
            }
            int i4 = this.mPageMargin;
            if (i4 + f > scrollX) {
                this.mMarginDrawable.setBounds((int) f, this.mTopPageBounds, (int) (i4 + f + 0.5f), this.mBottomPageBounds);
                this.mMarginDrawable.draw(canvas);
            }
            if (f > scrollX + width) {
                return;
            }
        }
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction() & 255;
        if (action == 3 || action == 1) {
            this.mIsBeingDragged = false;
            this.mIsUnableToDrag = false;
            this.mActivePointerId = -1;
            VelocityTracker velocityTracker = this.mVelocityTracker;
            if (velocityTracker != null) {
                velocityTracker.recycle();
                this.mVelocityTracker = null;
                return false;
            }
            return false;
        }
        if (action != 0) {
            if (this.mIsBeingDragged) {
                return true;
            }
            if (this.mIsUnableToDrag) {
                return false;
            }
        }
        if (action == 0) {
            float x = motionEvent.getX();
            this.mInitialMotionX = x;
            this.mLastMotionX = x;
            float y = motionEvent.getY();
            this.mInitialMotionY = y;
            this.mLastMotionY = y;
            this.mActivePointerId = MotionEventCompat.getPointerId(motionEvent, 0);
            this.mIsUnableToDrag = false;
            this.mScroller.computeScrollOffset();
            if (this.mScrollState != 2 || Math.abs(this.mScroller.getFinalX() - this.mScroller.getCurrX()) <= this.mCloseEnough) {
                completeScroll(false);
                this.mIsBeingDragged = false;
            } else {
                this.mScroller.abortAnimation();
                this.mPopulatePending = false;
                populate();
                this.mIsBeingDragged = true;
                requestParentDisallowInterceptTouchEvent(true);
                setScrollState(1);
            }
        } else if (action == 2) {
            int i = this.mActivePointerId;
            if (i != -1) {
                int findPointerIndex = MotionEventCompat.findPointerIndex(motionEvent, i);
                float x2 = MotionEventCompat.getX(motionEvent, findPointerIndex);
                float f = x2 - this.mLastMotionX;
                float abs = Math.abs(f);
                float y2 = MotionEventCompat.getY(motionEvent, findPointerIndex);
                float abs2 = Math.abs(y2 - this.mInitialMotionY);
                if (f != 0.0f && !isGutterDrag(this.mLastMotionX, f) && canScroll(this, false, (int) f, (int) x2, (int) y2)) {
                    this.mLastMotionX = x2;
                    this.mLastMotionY = y2;
                    this.mIsUnableToDrag = true;
                    return false;
                }
                if (abs > this.mTouchSlop && 0.5f * abs > abs2) {
                    this.mIsBeingDragged = true;
                    requestParentDisallowInterceptTouchEvent(true);
                    setScrollState(1);
                    this.mLastMotionX = f > 0.0f ? this.mInitialMotionX + this.mTouchSlop : this.mInitialMotionX - this.mTouchSlop;
                    this.mLastMotionY = y2;
                    setScrollingCacheEnabled(true);
                } else if (abs2 > this.mTouchSlop) {
                    this.mIsUnableToDrag = true;
                }
                if (this.mIsBeingDragged && performDrag(x2)) {
                    ViewCompat.postInvalidateOnAnimation(this);
                }
            }
        } else if (action == 6) {
            onSecondaryPointerUp(motionEvent);
        }
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(motionEvent);
        return this.mIsBeingDragged;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        ItemInfo infoForChild;
        int max;
        int i5;
        int max2;
        int childCount = getChildCount();
        int i6 = i3 - i;
        int i7 = i4 - i2;
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();
        int scrollX = getScrollX();
        int i8 = 0;
        for (int i9 = 0; i9 < childCount; i9++) {
            View childAt = getChildAt(i9);
            if (childAt.getVisibility() != 8) {
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                if (layoutParams.isDecor) {
                    int i10 = layoutParams.gravity & 7;
                    int i11 = layoutParams.gravity & 112;
                    if (i10 == 1) {
                        max = Math.max((i6 - childAt.getMeasuredWidth()) / 2, paddingLeft);
                        i5 = paddingLeft;
                    } else if (i10 == 3) {
                        max = paddingLeft;
                        i5 = paddingLeft + childAt.getMeasuredWidth();
                    } else if (i10 != 5) {
                        max = paddingLeft;
                        i5 = paddingLeft;
                    } else {
                        max = (i6 - paddingRight) - childAt.getMeasuredWidth();
                        paddingRight += childAt.getMeasuredWidth();
                        i5 = paddingLeft;
                    }
                    if (i11 == 16) {
                        max2 = Math.max((i7 - childAt.getMeasuredHeight()) / 2, paddingTop);
                    } else if (i11 == 48) {
                        max2 = paddingTop;
                        paddingTop += childAt.getMeasuredHeight();
                    } else if (i11 != 80) {
                        max2 = paddingTop;
                    } else {
                        max2 = (i7 - paddingBottom) - childAt.getMeasuredHeight();
                        paddingBottom += childAt.getMeasuredHeight();
                    }
                    int i12 = max + scrollX;
                    childAt.layout(i12, max2, childAt.getMeasuredWidth() + i12, max2 + childAt.getMeasuredHeight());
                    i8++;
                    paddingLeft = i5;
                }
            }
        }
        int i13 = (i6 - paddingLeft) - paddingRight;
        for (int i14 = 0; i14 < childCount; i14++) {
            View childAt2 = getChildAt(i14);
            if (childAt2.getVisibility() != 8) {
                LayoutParams layoutParams2 = (LayoutParams) childAt2.getLayoutParams();
                if (!layoutParams2.isDecor && (infoForChild = infoForChild(childAt2)) != null) {
                    int i15 = paddingLeft + ((int) (i13 * infoForChild.offset));
                    if (layoutParams2.needsMeasure) {
                        layoutParams2.needsMeasure = false;
                        childAt2.measure(View.MeasureSpec.makeMeasureSpec((int) (i13 * layoutParams2.widthFactor), 1073741824), View.MeasureSpec.makeMeasureSpec((i7 - paddingTop) - paddingBottom, 1073741824));
                    }
                    childAt2.layout(i15, paddingTop, childAt2.getMeasuredWidth() + i15, childAt2.getMeasuredHeight() + paddingTop);
                }
            }
        }
        this.mTopPageBounds = paddingTop;
        this.mBottomPageBounds = i7 - paddingBottom;
        this.mDecorChildCount = i8;
        if (this.mFirstLayout) {
            scrollToItem(this.mCurItem, false, 0, false);
        }
        this.mFirstLayout = false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int i, int i2) {
        LayoutParams layoutParams;
        LayoutParams layoutParams2;
        int i3;
        int i4;
        int i5;
        int i6;
        setMeasuredDimension(getDefaultSize(0, i), getDefaultSize(0, i2));
        int measuredWidth = getMeasuredWidth();
        this.mGutterSize = Math.min(measuredWidth / 10, this.mDefaultGutterSize);
        int paddingLeft = (measuredWidth - getPaddingLeft()) - getPaddingRight();
        int measuredHeight = (getMeasuredHeight() - getPaddingTop()) - getPaddingBottom();
        int childCount = getChildCount();
        for (int i7 = 0; i7 < childCount; i7++) {
            View childAt = getChildAt(i7);
            if (childAt.getVisibility() != 8 && (layoutParams2 = (LayoutParams) childAt.getLayoutParams()) != null && layoutParams2.isDecor) {
                int i8 = layoutParams2.gravity & 7;
                int i9 = layoutParams2.gravity & 112;
                int i10 = Integer.MIN_VALUE;
                int i11 = Integer.MIN_VALUE;
                boolean z = i9 == 48 || i9 == 80;
                boolean z2 = i8 == 3 || i8 == 5;
                if (z) {
                    i10 = 1073741824;
                } else if (z2) {
                    i11 = 1073741824;
                }
                if (layoutParams2.width != -2) {
                    i3 = 1073741824;
                    i4 = layoutParams2.width != -1 ? layoutParams2.width : paddingLeft;
                } else {
                    i3 = i10;
                    i4 = paddingLeft;
                }
                if (layoutParams2.height == -2) {
                    i5 = i11;
                    i6 = measuredHeight;
                } else if (layoutParams2.height != -1) {
                    i6 = layoutParams2.height;
                    i5 = 1073741824;
                } else {
                    i5 = 1073741824;
                    i6 = measuredHeight;
                }
                childAt.measure(View.MeasureSpec.makeMeasureSpec(i4, i3), View.MeasureSpec.makeMeasureSpec(i6, i5));
                if (z) {
                    measuredHeight -= childAt.getMeasuredHeight();
                } else if (z2) {
                    paddingLeft -= childAt.getMeasuredWidth();
                }
            }
        }
        this.mChildWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(paddingLeft, 1073741824);
        this.mChildHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredHeight, 1073741824);
        this.mInLayout = true;
        populate();
        this.mInLayout = false;
        int childCount2 = getChildCount();
        for (int i12 = 0; i12 < childCount2; i12++) {
            View childAt2 = getChildAt(i12);
            if (childAt2.getVisibility() != 8 && ((layoutParams = (LayoutParams) childAt2.getLayoutParams()) == null || !layoutParams.isDecor)) {
                childAt2.measure(View.MeasureSpec.makeMeasureSpec((int) (paddingLeft * layoutParams.widthFactor), 1073741824), this.mChildHeightMeasureSpec);
            }
        }
    }

    protected void onPageScrolled(int i, float f, int i2) {
        int max;
        if (this.mDecorChildCount > 0) {
            int scrollX = getScrollX();
            int paddingLeft = getPaddingLeft();
            int paddingRight = getPaddingRight();
            int width = getWidth();
            int childCount = getChildCount();
            for (int i3 = 0; i3 < childCount; i3++) {
                View childAt = getChildAt(i3);
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                if (layoutParams.isDecor) {
                    int i4 = layoutParams.gravity & 7;
                    if (i4 == 1) {
                        max = Math.max((width - childAt.getMeasuredWidth()) / 2, paddingLeft);
                    } else if (i4 == 3) {
                        max = paddingLeft;
                        paddingLeft += childAt.getWidth();
                    } else if (i4 != 5) {
                        max = paddingLeft;
                    } else {
                        max = (width - paddingRight) - childAt.getMeasuredWidth();
                        paddingRight += childAt.getMeasuredWidth();
                    }
                    int left = (max + scrollX) - childAt.getLeft();
                    if (left != 0) {
                        childAt.offsetLeftAndRight(left);
                    }
                }
            }
        }
        OnPageChangeListener onPageChangeListener = this.mOnPageChangeListener;
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageScrolled(i, f, i2);
        }
        OnPageChangeListener onPageChangeListener2 = this.mInternalPageChangeListener;
        if (onPageChangeListener2 != null) {
            onPageChangeListener2.onPageScrolled(i, f, i2);
        }
        if (this.mPageTransformer != null) {
            int scrollX2 = getScrollX();
            int childCount2 = getChildCount();
            for (int i5 = 0; i5 < childCount2; i5++) {
                View childAt2 = getChildAt(i5);
                if (!((LayoutParams) childAt2.getLayoutParams()).isDecor) {
                    this.mPageTransformer.transformPage(childAt2, (childAt2.getLeft() - scrollX2) / getClientWidth());
                }
            }
        }
        this.mCalledSuper = true;
    }

    @Override // android.view.ViewGroup
    protected boolean onRequestFocusInDescendants(int i, Rect rect) {
        int i2;
        int i3;
        ItemInfo infoForChild;
        int childCount = getChildCount();
        if ((i & 2) != 0) {
            i2 = 0;
            i3 = 1;
        } else {
            i2 = childCount - 1;
            i3 = -1;
            childCount = -1;
        }
        while (i2 != childCount) {
            View childAt = getChildAt(i2);
            if (childAt.getVisibility() == 0 && (infoForChild = infoForChild(childAt)) != null && infoForChild.position == this.mCurItem && childAt.requestFocus(i, rect)) {
                return true;
            }
            i2 += i3;
        }
        return false;
    }

    @Override // android.view.View
    public void onRestoreInstanceState(Parcelable parcelable) {
        if (!(parcelable instanceof SavedState)) {
            super.onRestoreInstanceState(parcelable);
            return;
        }
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        PagerAdapter pagerAdapter = this.mAdapter;
        if (pagerAdapter != null) {
            pagerAdapter.restoreState(savedState.adapterState, savedState.loader);
            setCurrentItemInternal(savedState.position, false, true);
            return;
        }
        this.mRestoredCurItem = savedState.position;
        this.mRestoredAdapterState = savedState.adapterState;
        this.mRestoredClassLoader = savedState.loader;
    }

    @Override // android.view.View
    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.position = this.mCurItem;
        PagerAdapter pagerAdapter = this.mAdapter;
        if (pagerAdapter != null) {
            savedState.adapterState = pagerAdapter.saveState();
        }
        return savedState;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        if (i != i3) {
            int i5 = this.mPageMargin;
            recomputeScrollPosition(i, i3, i5, i5);
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        PagerAdapter pagerAdapter;
        if (this.mFakeDragging) {
            return true;
        }
        if ((motionEvent.getAction() == 0 && motionEvent.getEdgeFlags() != 0) || (pagerAdapter = this.mAdapter) == null || pagerAdapter.getCount() == 0) {
            return false;
        }
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(motionEvent);
        boolean z = false;
        switch (motionEvent.getAction() & 255) {
            case 0:
                this.mScroller.abortAnimation();
                this.mPopulatePending = false;
                populate();
                float x = motionEvent.getX();
                this.mInitialMotionX = x;
                this.mLastMotionX = x;
                float y = motionEvent.getY();
                this.mInitialMotionY = y;
                this.mLastMotionY = y;
                this.mActivePointerId = MotionEventCompat.getPointerId(motionEvent, 0);
                break;
            case 1:
                if (this.mIsBeingDragged) {
                    VelocityTracker velocityTracker = this.mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(1000, this.mMaximumVelocity);
                    int xVelocity = (int) VelocityTrackerCompat.getXVelocity(velocityTracker, this.mActivePointerId);
                    this.mPopulatePending = true;
                    int clientWidth = getClientWidth();
                    int scrollX = getScrollX();
                    ItemInfo infoForCurrentScrollPosition = infoForCurrentScrollPosition();
                    setCurrentItemInternal(determineTargetPage(infoForCurrentScrollPosition.position, ((scrollX / clientWidth) - infoForCurrentScrollPosition.offset) / infoForCurrentScrollPosition.widthFactor, xVelocity, (int) (MotionEventCompat.getX(motionEvent, MotionEventCompat.findPointerIndex(motionEvent, this.mActivePointerId)) - this.mInitialMotionX)), true, true, xVelocity);
                    this.mActivePointerId = -1;
                    endDrag();
                    z = this.mLeftEdge.onRelease() | this.mRightEdge.onRelease();
                    break;
                }
                break;
            case 2:
                if (!this.mIsBeingDragged) {
                    int findPointerIndex = MotionEventCompat.findPointerIndex(motionEvent, this.mActivePointerId);
                    float x2 = MotionEventCompat.getX(motionEvent, findPointerIndex);
                    float abs = Math.abs(x2 - this.mLastMotionX);
                    float y2 = MotionEventCompat.getY(motionEvent, findPointerIndex);
                    float abs2 = Math.abs(y2 - this.mLastMotionY);
                    if (abs > this.mTouchSlop && abs > abs2) {
                        this.mIsBeingDragged = true;
                        requestParentDisallowInterceptTouchEvent(true);
                        float f = this.mInitialMotionX;
                        this.mLastMotionX = x2 - f > 0.0f ? f + this.mTouchSlop : f - this.mTouchSlop;
                        this.mLastMotionY = y2;
                        setScrollState(1);
                        setScrollingCacheEnabled(true);
                        ViewParent parent = getParent();
                        if (parent != null) {
                            parent.requestDisallowInterceptTouchEvent(true);
                        }
                    }
                }
                if (this.mIsBeingDragged) {
                    z = false | performDrag(MotionEventCompat.getX(motionEvent, MotionEventCompat.findPointerIndex(motionEvent, this.mActivePointerId)));
                    break;
                }
                break;
            case 3:
                if (this.mIsBeingDragged) {
                    scrollToItem(this.mCurItem, true, 0, false);
                    this.mActivePointerId = -1;
                    endDrag();
                    z = this.mLeftEdge.onRelease() | this.mRightEdge.onRelease();
                    break;
                }
                break;
            case 5:
                int actionIndex = MotionEventCompat.getActionIndex(motionEvent);
                this.mLastMotionX = MotionEventCompat.getX(motionEvent, actionIndex);
                this.mActivePointerId = MotionEventCompat.getPointerId(motionEvent, actionIndex);
                break;
            case 6:
                onSecondaryPointerUp(motionEvent);
                this.mLastMotionX = MotionEventCompat.getX(motionEvent, MotionEventCompat.findPointerIndex(motionEvent, this.mActivePointerId));
                break;
        }
        if (z) {
            ViewCompat.postInvalidateOnAnimation(this);
            return true;
        }
        return true;
    }

    boolean pageLeft() {
        int i = this.mCurItem;
        if (i > 0) {
            setCurrentItem(i - 1, true);
            return true;
        }
        return false;
    }

    boolean pageRight() {
        PagerAdapter pagerAdapter = this.mAdapter;
        if (pagerAdapter == null || this.mCurItem >= pagerAdapter.getCount() - 1) {
            return false;
        }
        setCurrentItem(this.mCurItem + 1, true);
        return true;
    }

    void populate() {
        populate(this.mCurItem);
    }

    void populate(int i) {
        int i2;
        ItemInfo itemInfo;
        String hexString;
        ItemInfo itemInfo2;
        ItemInfo infoForChild;
        float f;
        int i3;
        int i4 = this.mCurItem;
        if (i4 != i) {
            int i5 = i4 < i ? 66 : 17;
            itemInfo = infoForPosition(this.mCurItem);
            this.mCurItem = i;
            i2 = i5;
        } else {
            i2 = 2;
            itemInfo = null;
        }
        if (this.mAdapter == null) {
            sortChildDrawingOrder();
        } else if (this.mPopulatePending) {
            sortChildDrawingOrder();
        } else if (getWindowToken() != null) {
            this.mAdapter.startUpdate((ViewGroup) this);
            int i6 = this.mOffscreenPageLimit;
            int max = Math.max(0, this.mCurItem - i6);
            int count = this.mAdapter.getCount();
            int min = Math.min(count - 1, this.mCurItem + i6);
            if (count != this.mExpectedAdapterCount) {
                try {
                    hexString = getResources().getResourceName(getId());
                } catch (Resources.NotFoundException e) {
                    hexString = Integer.toHexString(getId());
                }
                throw new IllegalStateException("The application's PagerAdapter changed the adapter's contents without calling PagerAdapter#notifyDataSetChanged! Expected adapter item count: " + this.mExpectedAdapterCount + ", found: " + count + " Pager id: " + hexString + " Pager class: " + getClass() + " Problematic adapter: " + this.mAdapter.getClass());
            }
            int i7 = 0;
            while (true) {
                itemInfo2 = null;
                if (i7 >= this.mItems.size()) {
                    break;
                }
                itemInfo2 = this.mItems.get(i7);
                if (itemInfo2.position < this.mCurItem) {
                    i7++;
                } else if (itemInfo2.position != this.mCurItem) {
                    itemInfo2 = null;
                }
            }
            ItemInfo addNewItem = (itemInfo2 != null || count <= 0) ? itemInfo2 : addNewItem(this.mCurItem, i7);
            if (addNewItem != null) {
                float f2 = 0.0f;
                int i8 = i7 - 1;
                ItemInfo itemInfo3 = i8 >= 0 ? this.mItems.get(i8) : null;
                int clientWidth = getClientWidth();
                float paddingLeft = clientWidth <= 0 ? 0.0f : (getPaddingLeft() / clientWidth) + (2.0f - addNewItem.widthFactor);
                int i9 = this.mCurItem - 1;
                int i10 = i7;
                float f3 = paddingLeft;
                while (i9 >= 0) {
                    if (f2 < f3 || i9 >= max) {
                        if (itemInfo3 == null || i9 != itemInfo3.position) {
                            f = f2 + addNewItem(i9, i8 + 1).widthFactor;
                            i3 = i10 + 1;
                            itemInfo3 = i8 >= 0 ? this.mItems.get(i8) : null;
                        } else {
                            f = f2 + itemInfo3.widthFactor;
                            i8--;
                            itemInfo3 = i8 >= 0 ? this.mItems.get(i8) : null;
                            i3 = i10;
                        }
                    } else if (itemInfo3 == null) {
                        break;
                    } else if (i9 != itemInfo3.position || itemInfo3.scrolling) {
                        i3 = i10;
                        f = f2;
                    } else {
                        this.mItems.remove(i8);
                        this.mAdapter.destroyItem((ViewGroup) this, i9, itemInfo3.object);
                        i8--;
                        i3 = i10 - 1;
                        itemInfo3 = i8 >= 0 ? this.mItems.get(i8) : null;
                        f = f2;
                    }
                    i9--;
                    i10 = i3;
                    f2 = f;
                }
                float f4 = addNewItem.widthFactor;
                int i11 = i10 + 1;
                if (f4 < 2.0f) {
                    ItemInfo itemInfo4 = i11 < this.mItems.size() ? this.mItems.get(i11) : null;
                    float paddingRight = clientWidth <= 0 ? 0.0f : (getPaddingRight() / clientWidth) + 2.0f;
                    for (int i12 = this.mCurItem + 1; i12 < count; i12++) {
                        if (f4 < paddingRight || i12 <= min) {
                            if (itemInfo4 == null || i12 != itemInfo4.position) {
                                i11++;
                                f4 += addNewItem(i12, i11).widthFactor;
                                itemInfo4 = i11 < this.mItems.size() ? this.mItems.get(i11) : null;
                            } else {
                                f4 += itemInfo4.widthFactor;
                                i11++;
                                itemInfo4 = i11 < this.mItems.size() ? this.mItems.get(i11) : null;
                            }
                        } else if (itemInfo4 == null) {
                            break;
                        } else if (i12 == itemInfo4.position && !itemInfo4.scrolling) {
                            this.mItems.remove(i11);
                            this.mAdapter.destroyItem((ViewGroup) this, i12, itemInfo4.object);
                            itemInfo4 = i11 < this.mItems.size() ? this.mItems.get(i11) : null;
                        }
                    }
                }
                calculatePageOffsets(addNewItem, i10, itemInfo);
            }
            this.mAdapter.setPrimaryItem((ViewGroup) this, this.mCurItem, addNewItem != null ? addNewItem.object : null);
            this.mAdapter.finishUpdate((ViewGroup) this);
            int childCount = getChildCount();
            for (int i13 = 0; i13 < childCount; i13++) {
                View childAt = getChildAt(i13);
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                layoutParams.childIndex = i13;
                if (!layoutParams.isDecor && layoutParams.widthFactor == 0.0f && (infoForChild = infoForChild(childAt)) != null) {
                    layoutParams.widthFactor = infoForChild.widthFactor;
                    layoutParams.position = infoForChild.position;
                }
            }
            sortChildDrawingOrder();
            if (hasFocus()) {
                View findFocus = findFocus();
                ItemInfo infoForAnyChild = findFocus != null ? infoForAnyChild(findFocus) : null;
                if (infoForAnyChild == null || infoForAnyChild.position != this.mCurItem) {
                    for (int i14 = 0; i14 < getChildCount(); i14++) {
                        View childAt2 = getChildAt(i14);
                        ItemInfo infoForChild2 = infoForChild(childAt2);
                        if (infoForChild2 != null && infoForChild2.position == this.mCurItem && childAt2.requestFocus(i2)) {
                            return;
                        }
                    }
                }
            }
        }
    }

    @Override // android.view.ViewGroup, android.view.ViewManager
    public void removeView(View view) {
        if (this.mInLayout) {
            removeViewInLayout(view);
        } else {
            super.removeView(view);
        }
    }

    public void setAdapter(PagerAdapter pagerAdapter) {
        PagerAdapter pagerAdapter2 = this.mAdapter;
        if (pagerAdapter2 != null) {
            pagerAdapter2.unregisterDataSetObserver(this.mObserver);
            this.mAdapter.startUpdate((ViewGroup) this);
            for (int i = 0; i < this.mItems.size(); i++) {
                ItemInfo itemInfo = this.mItems.get(i);
                this.mAdapter.destroyItem((ViewGroup) this, itemInfo.position, itemInfo.object);
            }
            this.mAdapter.finishUpdate((ViewGroup) this);
            this.mItems.clear();
            removeNonDecorViews();
            this.mCurItem = 0;
            scrollTo(0, 0);
        }
        PagerAdapter pagerAdapter3 = this.mAdapter;
        this.mAdapter = pagerAdapter;
        this.mExpectedAdapterCount = 0;
        if (this.mAdapter != null) {
            if (this.mObserver == null) {
                this.mObserver = new PagerObserver();
            }
            this.mAdapter.registerDataSetObserver(this.mObserver);
            this.mPopulatePending = false;
            boolean z = this.mFirstLayout;
            this.mFirstLayout = true;
            this.mExpectedAdapterCount = this.mAdapter.getCount();
            if (this.mRestoredCurItem >= 0) {
                this.mAdapter.restoreState(this.mRestoredAdapterState, this.mRestoredClassLoader);
                setCurrentItemInternal(this.mRestoredCurItem, false, true);
                this.mRestoredCurItem = -1;
                this.mRestoredAdapterState = null;
                this.mRestoredClassLoader = null;
            } else if (z) {
                requestLayout();
            } else {
                populate();
            }
        }
        OnAdapterChangeListener onAdapterChangeListener = this.mAdapterChangeListener;
        if (onAdapterChangeListener == null || pagerAdapter3 == pagerAdapter) {
            return;
        }
        onAdapterChangeListener.onAdapterChanged(pagerAdapter3, pagerAdapter);
    }

    void setChildrenDrawingOrderEnabledCompat(boolean z) {
        if (Build.VERSION.SDK_INT >= 7) {
            if (this.mSetChildrenDrawingOrderEnabled == null) {
                try {
                    this.mSetChildrenDrawingOrderEnabled = ViewGroup.class.getDeclaredMethod("setChildrenDrawingOrderEnabled", Boolean.TYPE);
                } catch (NoSuchMethodException e) {
                    Log.e(TAG, "Can't find setChildrenDrawingOrderEnabled", e);
                }
            }
            try {
                this.mSetChildrenDrawingOrderEnabled.invoke(this, Boolean.valueOf(z));
            } catch (Exception e2) {
                Log.e(TAG, "Error changing children drawing order", e2);
            }
        }
    }

    public void setCurrentItem(int i) {
        this.mPopulatePending = false;
        setCurrentItemInternal(i, !this.mFirstLayout, false);
    }

    public void setCurrentItem(int i, boolean z) {
        this.mPopulatePending = false;
        setCurrentItemInternal(i, z, false);
    }

    void setCurrentItemInternal(int i, boolean z, boolean z2) {
        setCurrentItemInternal(i, z, z2, 0);
    }

    void setCurrentItemInternal(int i, boolean z, boolean z2, int i2) {
        OnPageChangeListener onPageChangeListener;
        OnPageChangeListener onPageChangeListener2;
        PagerAdapter pagerAdapter = this.mAdapter;
        if (pagerAdapter == null || pagerAdapter.getCount() <= 0) {
            setScrollingCacheEnabled(false);
        } else if (z2 || this.mCurItem != i || this.mItems.size() == 0) {
            if (i < 0) {
                i = 0;
            } else if (i >= this.mAdapter.getCount()) {
                i = this.mAdapter.getCount() - 1;
            }
            int i3 = this.mOffscreenPageLimit;
            int i4 = this.mCurItem;
            if (i > i4 + i3 || i < i4 - i3) {
                for (int i5 = 0; i5 < this.mItems.size(); i5++) {
                    this.mItems.get(i5).scrolling = true;
                }
            }
            boolean z3 = this.mCurItem != i;
            if (!this.mFirstLayout) {
                populate(i);
                scrollToItem(i, z, i2, z3);
                return;
            }
            this.mCurItem = i;
            if (z3 && (onPageChangeListener2 = this.mOnPageChangeListener) != null) {
                onPageChangeListener2.onPageSelected(i);
            }
            if (z3 && (onPageChangeListener = this.mInternalPageChangeListener) != null) {
                onPageChangeListener.onPageSelected(i);
            }
            requestLayout();
        } else {
            setScrollingCacheEnabled(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public OnPageChangeListener setInternalPageChangeListener(OnPageChangeListener onPageChangeListener) {
        OnPageChangeListener onPageChangeListener2 = this.mInternalPageChangeListener;
        this.mInternalPageChangeListener = onPageChangeListener;
        return onPageChangeListener2;
    }

    public void setOffscreenPageLimit(int i) {
        if (i < 1) {
            Log.w(TAG, "Requested offscreen page limit " + i + " too small; defaulting to 1");
            i = 1;
        }
        if (i != this.mOffscreenPageLimit) {
            this.mOffscreenPageLimit = i;
            populate();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setOnAdapterChangeListener(OnAdapterChangeListener onAdapterChangeListener) {
        this.mAdapterChangeListener = onAdapterChangeListener;
    }

    public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        this.mOnPageChangeListener = onPageChangeListener;
    }

    public void setPageMargin(int i) {
        int i2 = this.mPageMargin;
        this.mPageMargin = i;
        int width = getWidth();
        recomputeScrollPosition(width, width, i, i2);
        requestLayout();
    }

    public void setPageMarginDrawable(int i) {
        setPageMarginDrawable(getContext().getResources().getDrawable(i));
    }

    public void setPageMarginDrawable(Drawable drawable) {
        this.mMarginDrawable = drawable;
        if (drawable != null) {
            refreshDrawableState();
        }
        setWillNotDraw(drawable == null);
        invalidate();
    }

    public void setPageTransformer(boolean z, PageTransformer pageTransformer) {
        if (Build.VERSION.SDK_INT >= 11) {
            int i = 1;
            boolean z2 = pageTransformer != null;
            boolean z3 = z2 != (this.mPageTransformer != null);
            this.mPageTransformer = pageTransformer;
            setChildrenDrawingOrderEnabledCompat(z2);
            if (z2) {
                if (z) {
                    i = 2;
                }
                this.mDrawingOrder = i;
            } else {
                this.mDrawingOrder = 0;
            }
            if (z3) {
                populate();
            }
        }
    }

    void smoothScrollTo(int i, int i2) {
        smoothScrollTo(i, i2, 0);
    }

    void smoothScrollTo(int i, int i2, int i3) {
        int abs;
        if (getChildCount() == 0) {
            setScrollingCacheEnabled(false);
            return;
        }
        int scrollX = getScrollX();
        int scrollY = getScrollY();
        int i4 = i - scrollX;
        int i5 = i2 - scrollY;
        if (i4 == 0 && i5 == 0) {
            completeScroll(false);
            populate();
            setScrollState(0);
            return;
        }
        setScrollingCacheEnabled(true);
        setScrollState(2);
        int clientWidth = getClientWidth();
        int i6 = clientWidth / 2;
        float f = i6;
        float f2 = i6;
        float distanceInfluenceForSnapDuration = distanceInfluenceForSnapDuration(Math.min(1.0f, (Math.abs(i4) * 1.0f) / clientWidth));
        int abs2 = Math.abs(i3);
        if (abs2 > 0) {
            abs = Math.round(Math.abs((f + (f2 * distanceInfluenceForSnapDuration)) / abs2) * 1000.0f) * 4;
        } else {
            abs = (int) ((1.0f + (Math.abs(i4) / (this.mPageMargin + (clientWidth * this.mAdapter.getPageWidth(this.mCurItem))))) * 100.0f);
        }
        this.mScroller.startScroll(scrollX, scrollY, i4, i5, Math.min(abs, 600));
        ViewCompat.postInvalidateOnAnimation(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public boolean verifyDrawable(Drawable drawable) {
        return super.verifyDrawable(drawable) || drawable == this.mMarginDrawable;
    }
}