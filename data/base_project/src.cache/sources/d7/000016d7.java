package android.widget;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.StrictMode;
import android.os.Trace;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.StateSet;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.CorrectionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.RemoteViews;
import android.widget.RemoteViewsAdapter;
import com.android.internal.R;
import java.util.ArrayList;
import java.util.List;

/* loaded from: AbsListView.class */
public abstract class AbsListView extends AdapterView<ListAdapter> implements TextWatcher, ViewTreeObserver.OnGlobalLayoutListener, Filter.FilterListener, ViewTreeObserver.OnTouchModeChangeListener, RemoteViewsAdapter.RemoteAdapterConnectionCallback {
    private static final String TAG = "AbsListView";
    public static final int TRANSCRIPT_MODE_DISABLED = 0;
    public static final int TRANSCRIPT_MODE_NORMAL = 1;
    public static final int TRANSCRIPT_MODE_ALWAYS_SCROLL = 2;
    static final int TOUCH_MODE_REST = -1;
    static final int TOUCH_MODE_DOWN = 0;
    static final int TOUCH_MODE_TAP = 1;
    static final int TOUCH_MODE_DONE_WAITING = 2;
    static final int TOUCH_MODE_SCROLL = 3;
    static final int TOUCH_MODE_FLING = 4;
    static final int TOUCH_MODE_OVERSCROLL = 5;
    static final int TOUCH_MODE_OVERFLING = 6;
    static final int LAYOUT_NORMAL = 0;
    static final int LAYOUT_FORCE_TOP = 1;
    static final int LAYOUT_SET_SELECTION = 2;
    static final int LAYOUT_FORCE_BOTTOM = 3;
    static final int LAYOUT_SPECIFIC = 4;
    static final int LAYOUT_SYNC = 5;
    static final int LAYOUT_MOVE_SELECTION = 6;
    public static final int CHOICE_MODE_NONE = 0;
    public static final int CHOICE_MODE_SINGLE = 1;
    public static final int CHOICE_MODE_MULTIPLE = 2;
    public static final int CHOICE_MODE_MULTIPLE_MODAL = 3;
    private final Thread mOwnerThread;
    int mChoiceMode;
    ActionMode mChoiceActionMode;
    MultiChoiceModeWrapper mMultiChoiceModeCallback;
    int mCheckedItemCount;
    SparseBooleanArray mCheckStates;
    LongSparseArray<Integer> mCheckedIdStates;
    int mLayoutMode;
    AdapterDataSetObserver mDataSetObserver;
    ListAdapter mAdapter;
    private RemoteViewsAdapter mRemoteAdapter;
    boolean mAdapterHasStableIds;
    private boolean mDeferNotifyDataSetChanged;
    boolean mDrawSelectorOnTop;
    Drawable mSelector;
    int mSelectorPosition;
    Rect mSelectorRect;
    final RecycleBin mRecycler;
    int mSelectionLeftPadding;
    int mSelectionTopPadding;
    int mSelectionRightPadding;
    int mSelectionBottomPadding;
    Rect mListPadding;
    int mWidthMeasureSpec;
    View mScrollUp;
    View mScrollDown;
    boolean mCachingStarted;
    boolean mCachingActive;
    int mMotionPosition;
    int mMotionViewOriginalTop;
    int mMotionViewNewTop;
    int mMotionX;
    int mMotionY;
    int mTouchMode;
    int mLastY;
    int mMotionCorrection;
    private VelocityTracker mVelocityTracker;
    private FlingRunnable mFlingRunnable;
    PositionScroller mPositionScroller;
    int mSelectedTop;
    boolean mStackFromBottom;
    boolean mScrollingCacheEnabled;
    boolean mFastScrollEnabled;
    boolean mFastScrollAlwaysVisible;
    private OnScrollListener mOnScrollListener;
    PopupWindow mPopup;
    EditText mTextFilter;
    private boolean mSmoothScrollbarEnabled;
    private boolean mTextFilterEnabled;
    private boolean mFiltered;
    private Rect mTouchFrame;
    int mResurrectToPosition;
    private ContextMenu.ContextMenuInfo mContextMenuInfo;
    int mOverscrollMax;
    static final int OVERSCROLL_LIMIT_DIVISOR = 3;
    private static final int CHECK_POSITION_SEARCH_DISTANCE = 20;
    private static final int TOUCH_MODE_UNKNOWN = -1;
    private static final int TOUCH_MODE_ON = 0;
    private static final int TOUCH_MODE_OFF = 1;
    private int mLastTouchMode;
    private static final boolean PROFILE_SCROLLING = false;
    private boolean mScrollProfilingStarted;
    private static final boolean PROFILE_FLINGING = false;
    private boolean mFlingProfilingStarted;
    private StrictMode.Span mScrollStrictSpan;
    private StrictMode.Span mFlingStrictSpan;
    private CheckForLongPress mPendingCheckForLongPress;
    private Runnable mPendingCheckForTap;
    private CheckForKeyLongPress mPendingCheckForKeyLongPress;
    private PerformClick mPerformClick;
    private Runnable mTouchModeReset;
    private int mTranscriptMode;
    private int mCacheColorHint;
    private boolean mIsChildViewEnabled;
    private int mLastScrollState;
    private FastScroller mFastScroller;
    private boolean mGlobalLayoutListenerAddedFilter;
    private int mTouchSlop;
    private float mDensityScale;
    private InputConnection mDefInputConnection;
    private InputConnectionWrapper mPublicInputConnection;
    private Runnable mClearScrollingCache;
    Runnable mPositionScrollAfterLayout;
    private int mMinimumVelocity;
    private int mMaximumVelocity;
    private float mVelocityScale;
    final boolean[] mIsScrap;
    private boolean mPopupHidden;
    private int mActivePointerId;
    private static final int INVALID_POINTER = -1;
    int mOverscrollDistance;
    int mOverflingDistance;
    private EdgeEffect mEdgeGlowTop;
    private EdgeEffect mEdgeGlowBottom;
    private int mFirstPositionDistanceGuess;
    private int mLastPositionDistanceGuess;
    private int mDirection;
    private boolean mForceTranscriptScroll;
    private int mGlowPaddingLeft;
    private int mGlowPaddingRight;
    private ListItemAccessibilityDelegate mAccessibilityDelegate;
    private int mLastAccessibilityScrollEventFromIndex;
    private int mLastAccessibilityScrollEventToIndex;
    private int mLastHandledItemCount;
    static final Interpolator sLinearInterpolator = new LinearInterpolator();
    private SavedState mPendingSync;

    /* loaded from: AbsListView$MultiChoiceModeListener.class */
    public interface MultiChoiceModeListener extends ActionMode.Callback {
        void onItemCheckedStateChanged(ActionMode actionMode, int i, long j, boolean z);
    }

    /* loaded from: AbsListView$OnScrollListener.class */
    public interface OnScrollListener {
        public static final int SCROLL_STATE_IDLE = 0;
        public static final int SCROLL_STATE_TOUCH_SCROLL = 1;
        public static final int SCROLL_STATE_FLING = 2;

        void onScrollStateChanged(AbsListView absListView, int i);

        void onScroll(AbsListView absListView, int i, int i2, int i3);
    }

    /* loaded from: AbsListView$RecyclerListener.class */
    public interface RecyclerListener {
        void onMovedToScrapHeap(View view);
    }

    /* loaded from: AbsListView$SelectionBoundsAdjuster.class */
    public interface SelectionBoundsAdjuster {
        void adjustListItemSelectionBounds(Rect rect);
    }

    abstract void fillGap(boolean z);

    abstract int findMotionRow(int i);

    abstract void setSelectionInt(int i);

    public AbsListView(Context context) {
        super(context);
        this.mChoiceMode = 0;
        this.mLayoutMode = 0;
        this.mDeferNotifyDataSetChanged = false;
        this.mDrawSelectorOnTop = false;
        this.mSelectorPosition = -1;
        this.mSelectorRect = new Rect();
        this.mRecycler = new RecycleBin();
        this.mSelectionLeftPadding = 0;
        this.mSelectionTopPadding = 0;
        this.mSelectionRightPadding = 0;
        this.mSelectionBottomPadding = 0;
        this.mListPadding = new Rect();
        this.mWidthMeasureSpec = 0;
        this.mTouchMode = -1;
        this.mSelectedTop = 0;
        this.mSmoothScrollbarEnabled = true;
        this.mResurrectToPosition = -1;
        this.mContextMenuInfo = null;
        this.mLastTouchMode = -1;
        this.mScrollProfilingStarted = false;
        this.mFlingProfilingStarted = false;
        this.mScrollStrictSpan = null;
        this.mFlingStrictSpan = null;
        this.mLastScrollState = 0;
        this.mVelocityScale = 1.0f;
        this.mIsScrap = new boolean[1];
        this.mActivePointerId = -1;
        this.mDirection = 0;
        initAbsListView();
        this.mOwnerThread = Thread.currentThread();
        setVerticalScrollBarEnabled(true);
        TypedArray a = context.obtainStyledAttributes(R.styleable.View);
        initializeScrollbars(a);
        a.recycle();
    }

    public AbsListView(Context context, AttributeSet attrs) {
        this(context, attrs, 16842858);
    }

    public AbsListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mChoiceMode = 0;
        this.mLayoutMode = 0;
        this.mDeferNotifyDataSetChanged = false;
        this.mDrawSelectorOnTop = false;
        this.mSelectorPosition = -1;
        this.mSelectorRect = new Rect();
        this.mRecycler = new RecycleBin();
        this.mSelectionLeftPadding = 0;
        this.mSelectionTopPadding = 0;
        this.mSelectionRightPadding = 0;
        this.mSelectionBottomPadding = 0;
        this.mListPadding = new Rect();
        this.mWidthMeasureSpec = 0;
        this.mTouchMode = -1;
        this.mSelectedTop = 0;
        this.mSmoothScrollbarEnabled = true;
        this.mResurrectToPosition = -1;
        this.mContextMenuInfo = null;
        this.mLastTouchMode = -1;
        this.mScrollProfilingStarted = false;
        this.mFlingProfilingStarted = false;
        this.mScrollStrictSpan = null;
        this.mFlingStrictSpan = null;
        this.mLastScrollState = 0;
        this.mVelocityScale = 1.0f;
        this.mIsScrap = new boolean[1];
        this.mActivePointerId = -1;
        this.mDirection = 0;
        initAbsListView();
        this.mOwnerThread = Thread.currentThread();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AbsListView, defStyle, 0);
        Drawable d = a.getDrawable(0);
        if (d != null) {
            setSelector(d);
        }
        this.mDrawSelectorOnTop = a.getBoolean(1, false);
        boolean stackFromBottom = a.getBoolean(2, false);
        setStackFromBottom(stackFromBottom);
        boolean scrollingCacheEnabled = a.getBoolean(3, true);
        setScrollingCacheEnabled(scrollingCacheEnabled);
        boolean useTextFilter = a.getBoolean(4, false);
        setTextFilterEnabled(useTextFilter);
        int transcriptMode = a.getInt(5, 0);
        setTranscriptMode(transcriptMode);
        int color = a.getColor(6, 0);
        setCacheColorHint(color);
        boolean enableFastScroll = a.getBoolean(8, false);
        setFastScrollEnabled(enableFastScroll);
        boolean smoothScrollbar = a.getBoolean(9, true);
        setSmoothScrollbarEnabled(smoothScrollbar);
        setChoiceMode(a.getInt(7, 0));
        setFastScrollAlwaysVisible(a.getBoolean(10, false));
        a.recycle();
    }

    private void initAbsListView() {
        setClickable(true);
        setFocusableInTouchMode(true);
        setWillNotDraw(false);
        setAlwaysDrawnWithCacheEnabled(false);
        setScrollingCacheEnabled(true);
        ViewConfiguration configuration = ViewConfiguration.get(this.mContext);
        this.mTouchSlop = configuration.getScaledTouchSlop();
        this.mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        this.mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        this.mOverscrollDistance = configuration.getScaledOverscrollDistance();
        this.mOverflingDistance = configuration.getScaledOverflingDistance();
        this.mDensityScale = getContext().getResources().getDisplayMetrics().density;
    }

    @Override // android.view.View
    public void setOverScrollMode(int mode) {
        if (mode != 2) {
            if (this.mEdgeGlowTop == null) {
                Context context = getContext();
                this.mEdgeGlowTop = new EdgeEffect(context);
                this.mEdgeGlowBottom = new EdgeEffect(context);
            }
        } else {
            this.mEdgeGlowTop = null;
            this.mEdgeGlowBottom = null;
        }
        super.setOverScrollMode(mode);
    }

    @Override // android.widget.AdapterView
    public void setAdapter(ListAdapter adapter) {
        if (adapter != null) {
            this.mAdapterHasStableIds = this.mAdapter.hasStableIds();
            if (this.mChoiceMode != 0 && this.mAdapterHasStableIds && this.mCheckedIdStates == null) {
                this.mCheckedIdStates = new LongSparseArray<>();
            }
        }
        if (this.mCheckStates != null) {
            this.mCheckStates.clear();
        }
        if (this.mCheckedIdStates != null) {
            this.mCheckedIdStates.clear();
        }
    }

    public int getCheckedItemCount() {
        return this.mCheckedItemCount;
    }

    public boolean isItemChecked(int position) {
        if (this.mChoiceMode != 0 && this.mCheckStates != null) {
            return this.mCheckStates.get(position);
        }
        return false;
    }

    public int getCheckedItemPosition() {
        if (this.mChoiceMode == 1 && this.mCheckStates != null && this.mCheckStates.size() == 1) {
            return this.mCheckStates.keyAt(0);
        }
        return -1;
    }

    public SparseBooleanArray getCheckedItemPositions() {
        if (this.mChoiceMode != 0) {
            return this.mCheckStates;
        }
        return null;
    }

    public long[] getCheckedItemIds() {
        if (this.mChoiceMode == 0 || this.mCheckedIdStates == null || this.mAdapter == null) {
            return new long[0];
        }
        LongSparseArray<Integer> idStates = this.mCheckedIdStates;
        int count = idStates.size();
        long[] ids = new long[count];
        for (int i = 0; i < count; i++) {
            ids[i] = idStates.keyAt(i);
        }
        return ids;
    }

    public void clearChoices() {
        if (this.mCheckStates != null) {
            this.mCheckStates.clear();
        }
        if (this.mCheckedIdStates != null) {
            this.mCheckedIdStates.clear();
        }
        this.mCheckedItemCount = 0;
    }

    public void setItemChecked(int position, boolean value) {
        if (this.mChoiceMode == 0) {
            return;
        }
        if (value && this.mChoiceMode == 3 && this.mChoiceActionMode == null) {
            if (this.mMultiChoiceModeCallback == null || !this.mMultiChoiceModeCallback.hasWrappedCallback()) {
                throw new IllegalStateException("AbsListView: attempted to start selection mode for CHOICE_MODE_MULTIPLE_MODAL but no choice mode callback was supplied. Call setMultiChoiceModeListener to set a callback.");
            }
            this.mChoiceActionMode = startActionMode(this.mMultiChoiceModeCallback);
        }
        if (this.mChoiceMode == 2 || this.mChoiceMode == 3) {
            boolean oldValue = this.mCheckStates.get(position);
            this.mCheckStates.put(position, value);
            if (this.mCheckedIdStates != null && this.mAdapter.hasStableIds()) {
                if (value) {
                    this.mCheckedIdStates.put(this.mAdapter.getItemId(position), Integer.valueOf(position));
                } else {
                    this.mCheckedIdStates.delete(this.mAdapter.getItemId(position));
                }
            }
            if (oldValue != value) {
                if (value) {
                    this.mCheckedItemCount++;
                } else {
                    this.mCheckedItemCount--;
                }
            }
            if (this.mChoiceActionMode != null) {
                long id = this.mAdapter.getItemId(position);
                this.mMultiChoiceModeCallback.onItemCheckedStateChanged(this.mChoiceActionMode, position, id, value);
            }
        } else {
            boolean updateIds = this.mCheckedIdStates != null && this.mAdapter.hasStableIds();
            if (value || isItemChecked(position)) {
                this.mCheckStates.clear();
                if (updateIds) {
                    this.mCheckedIdStates.clear();
                }
            }
            if (value) {
                this.mCheckStates.put(position, true);
                if (updateIds) {
                    this.mCheckedIdStates.put(this.mAdapter.getItemId(position), Integer.valueOf(position));
                }
                this.mCheckedItemCount = 1;
            } else if (this.mCheckStates.size() == 0 || !this.mCheckStates.valueAt(0)) {
                this.mCheckedItemCount = 0;
            }
        }
        if (!this.mInLayout && !this.mBlockLayoutRequests) {
            this.mDataChanged = true;
            rememberSyncState();
            requestLayout();
        }
    }

    @Override // android.widget.AdapterView
    public boolean performItemClick(View view, int position, long id) {
        boolean handled = false;
        boolean dispatchItemClick = true;
        if (this.mChoiceMode != 0) {
            handled = true;
            boolean checkedStateChanged = false;
            if (this.mChoiceMode == 2 || (this.mChoiceMode == 3 && this.mChoiceActionMode != null)) {
                boolean checked = !this.mCheckStates.get(position, false);
                this.mCheckStates.put(position, checked);
                if (this.mCheckedIdStates != null && this.mAdapter.hasStableIds()) {
                    if (checked) {
                        this.mCheckedIdStates.put(this.mAdapter.getItemId(position), Integer.valueOf(position));
                    } else {
                        this.mCheckedIdStates.delete(this.mAdapter.getItemId(position));
                    }
                }
                if (checked) {
                    this.mCheckedItemCount++;
                } else {
                    this.mCheckedItemCount--;
                }
                if (this.mChoiceActionMode != null) {
                    this.mMultiChoiceModeCallback.onItemCheckedStateChanged(this.mChoiceActionMode, position, id, checked);
                    dispatchItemClick = false;
                }
                checkedStateChanged = true;
            } else if (this.mChoiceMode == 1) {
                if (!this.mCheckStates.get(position, false)) {
                    this.mCheckStates.clear();
                    this.mCheckStates.put(position, true);
                    if (this.mCheckedIdStates != null && this.mAdapter.hasStableIds()) {
                        this.mCheckedIdStates.clear();
                        this.mCheckedIdStates.put(this.mAdapter.getItemId(position), Integer.valueOf(position));
                    }
                    this.mCheckedItemCount = 1;
                } else if (this.mCheckStates.size() == 0 || !this.mCheckStates.valueAt(0)) {
                    this.mCheckedItemCount = 0;
                }
                checkedStateChanged = true;
            }
            if (checkedStateChanged) {
                updateOnScreenCheckedViews();
            }
        }
        if (dispatchItemClick) {
            handled |= super.performItemClick(view, position, id);
        }
        return handled;
    }

    private void updateOnScreenCheckedViews() {
        int firstPos = this.mFirstPosition;
        int count = getChildCount();
        boolean useActivated = getContext().getApplicationInfo().targetSdkVersion >= 11;
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            int position = firstPos + i;
            if (child instanceof Checkable) {
                ((Checkable) child).setChecked(this.mCheckStates.get(position));
            } else if (useActivated) {
                child.setActivated(this.mCheckStates.get(position));
            }
        }
    }

    public int getChoiceMode() {
        return this.mChoiceMode;
    }

    public void setChoiceMode(int choiceMode) {
        this.mChoiceMode = choiceMode;
        if (this.mChoiceActionMode != null) {
            this.mChoiceActionMode.finish();
            this.mChoiceActionMode = null;
        }
        if (this.mChoiceMode != 0) {
            if (this.mCheckStates == null) {
                this.mCheckStates = new SparseBooleanArray(0);
            }
            if (this.mCheckedIdStates == null && this.mAdapter != null && this.mAdapter.hasStableIds()) {
                this.mCheckedIdStates = new LongSparseArray<>(0);
            }
            if (this.mChoiceMode == 3) {
                clearChoices();
                setLongClickable(true);
            }
        }
    }

    public void setMultiChoiceModeListener(MultiChoiceModeListener listener) {
        if (this.mMultiChoiceModeCallback == null) {
            this.mMultiChoiceModeCallback = new MultiChoiceModeWrapper();
        }
        this.mMultiChoiceModeCallback.setWrapped(listener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean contentFits() {
        int childCount = getChildCount();
        if (childCount == 0) {
            return true;
        }
        return childCount == this.mItemCount && getChildAt(0).getTop() >= this.mListPadding.top && getChildAt(childCount - 1).getBottom() <= getHeight() - this.mListPadding.bottom;
    }

    public void setFastScrollEnabled(final boolean enabled) {
        if (this.mFastScrollEnabled != enabled) {
            this.mFastScrollEnabled = enabled;
            if (isOwnerThread()) {
                setFastScrollerEnabledUiThread(enabled);
            } else {
                post(new Runnable() { // from class: android.widget.AbsListView.1
                    @Override // java.lang.Runnable
                    public void run() {
                        AbsListView.this.setFastScrollerEnabledUiThread(enabled);
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setFastScrollerEnabledUiThread(boolean enabled) {
        if (this.mFastScroller != null) {
            this.mFastScroller.setEnabled(enabled);
        } else if (enabled) {
            this.mFastScroller = new FastScroller(this);
            this.mFastScroller.setEnabled(true);
        }
        resolvePadding();
        if (this.mFastScroller != null) {
            this.mFastScroller.updateLayout();
        }
    }

    public void setFastScrollAlwaysVisible(final boolean alwaysShow) {
        if (this.mFastScrollAlwaysVisible != alwaysShow) {
            if (alwaysShow && !this.mFastScrollEnabled) {
                setFastScrollEnabled(true);
            }
            this.mFastScrollAlwaysVisible = alwaysShow;
            if (isOwnerThread()) {
                setFastScrollerAlwaysVisibleUiThread(alwaysShow);
            } else {
                post(new Runnable() { // from class: android.widget.AbsListView.2
                    @Override // java.lang.Runnable
                    public void run() {
                        AbsListView.this.setFastScrollerAlwaysVisibleUiThread(alwaysShow);
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setFastScrollerAlwaysVisibleUiThread(boolean alwaysShow) {
        if (this.mFastScroller != null) {
            this.mFastScroller.setAlwaysShow(alwaysShow);
        }
    }

    private boolean isOwnerThread() {
        return this.mOwnerThread == Thread.currentThread();
    }

    public boolean isFastScrollAlwaysVisible() {
        return this.mFastScroller == null ? this.mFastScrollEnabled && this.mFastScrollAlwaysVisible : this.mFastScroller.isEnabled() && this.mFastScroller.isAlwaysShowEnabled();
    }

    @Override // android.view.View
    public int getVerticalScrollbarWidth() {
        if (this.mFastScroller != null && this.mFastScroller.isEnabled()) {
            return Math.max(super.getVerticalScrollbarWidth(), this.mFastScroller.getWidth());
        }
        return super.getVerticalScrollbarWidth();
    }

    @ViewDebug.ExportedProperty
    public boolean isFastScrollEnabled() {
        if (this.mFastScroller == null) {
            return this.mFastScrollEnabled;
        }
        return this.mFastScroller.isEnabled();
    }

    @Override // android.view.View
    public void setVerticalScrollbarPosition(int position) {
        super.setVerticalScrollbarPosition(position);
        if (this.mFastScroller != null) {
            this.mFastScroller.setScrollbarPosition(position);
        }
    }

    @Override // android.view.View
    public void setScrollBarStyle(int style) {
        super.setScrollBarStyle(style);
        if (this.mFastScroller != null) {
            this.mFastScroller.setScrollBarStyle(style);
        }
    }

    @Override // android.view.View
    protected boolean isVerticalScrollBarHidden() {
        return isFastScrollEnabled();
    }

    public void setSmoothScrollbarEnabled(boolean enabled) {
        this.mSmoothScrollbarEnabled = enabled;
    }

    @ViewDebug.ExportedProperty
    public boolean isSmoothScrollbarEnabled() {
        return this.mSmoothScrollbarEnabled;
    }

    public void setOnScrollListener(OnScrollListener l) {
        this.mOnScrollListener = l;
        invokeOnItemScrollListener();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void invokeOnItemScrollListener() {
        if (this.mFastScroller != null) {
            this.mFastScroller.onScroll(this.mFirstPosition, getChildCount(), this.mItemCount);
        }
        if (this.mOnScrollListener != null) {
            this.mOnScrollListener.onScroll(this, this.mFirstPosition, getChildCount(), this.mItemCount);
        }
        onScrollChanged(0, 0, 0, 0);
    }

    @Override // android.view.View, android.view.accessibility.AccessibilityEventSource
    public void sendAccessibilityEvent(int eventType) {
        if (eventType == 4096) {
            int firstVisiblePosition = getFirstVisiblePosition();
            int lastVisiblePosition = getLastVisiblePosition();
            if (this.mLastAccessibilityScrollEventFromIndex == firstVisiblePosition && this.mLastAccessibilityScrollEventToIndex == lastVisiblePosition) {
                return;
            }
            this.mLastAccessibilityScrollEventFromIndex = firstVisiblePosition;
            this.mLastAccessibilityScrollEventToIndex = lastVisiblePosition;
        }
        super.sendAccessibilityEvent(eventType);
    }

    @Override // android.widget.AdapterView, android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(AbsListView.class.getName());
    }

    @Override // android.widget.AdapterView, android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(AbsListView.class.getName());
        if (isEnabled()) {
            if (getFirstVisiblePosition() > 0) {
                info.addAction(8192);
                info.setScrollable(true);
            }
            if (getLastVisiblePosition() < getCount() - 1) {
                info.addAction(4096);
                info.setScrollable(true);
            }
        }
    }

    @Override // android.view.View
    public boolean performAccessibilityAction(int action, Bundle arguments) {
        if (super.performAccessibilityAction(action, arguments)) {
            return true;
        }
        switch (action) {
            case 4096:
                if (isEnabled() && getLastVisiblePosition() < getCount() - 1) {
                    int viewportHeight = (getHeight() - this.mListPadding.top) - this.mListPadding.bottom;
                    smoothScrollBy(viewportHeight, 200);
                    return true;
                }
                return false;
            case 8192:
                if (isEnabled() && this.mFirstPosition > 0) {
                    int viewportHeight2 = (getHeight() - this.mListPadding.top) - this.mListPadding.bottom;
                    smoothScrollBy(-viewportHeight2, 200);
                    return true;
                }
                return false;
            default:
                return false;
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public View findViewByAccessibilityIdTraversal(int accessibilityId) {
        if (accessibilityId == getAccessibilityViewId()) {
            return this;
        }
        if (this.mDataChanged) {
            return null;
        }
        return super.findViewByAccessibilityIdTraversal(accessibilityId);
    }

    @ViewDebug.ExportedProperty
    public boolean isScrollingCacheEnabled() {
        return this.mScrollingCacheEnabled;
    }

    public void setScrollingCacheEnabled(boolean enabled) {
        if (this.mScrollingCacheEnabled && !enabled) {
            clearScrollingCache();
        }
        this.mScrollingCacheEnabled = enabled;
    }

    public void setTextFilterEnabled(boolean textFilterEnabled) {
        this.mTextFilterEnabled = textFilterEnabled;
    }

    @ViewDebug.ExportedProperty
    public boolean isTextFilterEnabled() {
        return this.mTextFilterEnabled;
    }

    @Override // android.view.View
    public void getFocusedRect(Rect r) {
        View view = getSelectedView();
        if (view != null && view.getParent() == this) {
            view.getFocusedRect(r);
            offsetDescendantRectToMyCoords(view, r);
            return;
        }
        super.getFocusedRect(r);
    }

    private void useDefaultSelector() {
        setSelector(getResources().getDrawable(17301602));
    }

    @ViewDebug.ExportedProperty
    public boolean isStackFromBottom() {
        return this.mStackFromBottom;
    }

    public void setStackFromBottom(boolean stackFromBottom) {
        if (this.mStackFromBottom != stackFromBottom) {
            this.mStackFromBottom = stackFromBottom;
            requestLayoutIfNecessary();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void requestLayoutIfNecessary() {
        if (getChildCount() > 0) {
            resetList();
            requestLayout();
            invalidate();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: AbsListView$SavedState.class */
    public static class SavedState extends View.BaseSavedState {
        long selectedId;
        long firstId;
        int viewTop;
        int position;
        int height;
        String filter;
        boolean inActionMode;
        int checkedItemCount;
        SparseBooleanArray checkState;
        LongSparseArray<Integer> checkIdState;
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: android.widget.AbsListView.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.selectedId = in.readLong();
            this.firstId = in.readLong();
            this.viewTop = in.readInt();
            this.position = in.readInt();
            this.height = in.readInt();
            this.filter = in.readString();
            this.inActionMode = in.readByte() != 0;
            this.checkedItemCount = in.readInt();
            this.checkState = in.readSparseBooleanArray();
            int N = in.readInt();
            if (N > 0) {
                this.checkIdState = new LongSparseArray<>();
                for (int i = 0; i < N; i++) {
                    long key = in.readLong();
                    int value = in.readInt();
                    this.checkIdState.put(key, Integer.valueOf(value));
                }
            }
        }

        @Override // android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeLong(this.selectedId);
            out.writeLong(this.firstId);
            out.writeInt(this.viewTop);
            out.writeInt(this.position);
            out.writeInt(this.height);
            out.writeString(this.filter);
            out.writeByte((byte) (this.inActionMode ? 1 : 0));
            out.writeInt(this.checkedItemCount);
            out.writeSparseBooleanArray(this.checkState);
            int N = this.checkIdState != null ? this.checkIdState.size() : 0;
            out.writeInt(N);
            for (int i = 0; i < N; i++) {
                out.writeLong(this.checkIdState.keyAt(i));
                out.writeInt(this.checkIdState.valueAt(i).intValue());
            }
        }

        public String toString() {
            return "AbsListView.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " selectedId=" + this.selectedId + " firstId=" + this.firstId + " viewTop=" + this.viewTop + " position=" + this.position + " height=" + this.height + " filter=" + this.filter + " checkState=" + this.checkState + "}";
        }
    }

    @Override // android.view.View
    public Parcelable onSaveInstanceState() {
        EditText textFilter;
        Editable filterText;
        dismissPopup();
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        if (this.mPendingSync != null) {
            ss.selectedId = this.mPendingSync.selectedId;
            ss.firstId = this.mPendingSync.firstId;
            ss.viewTop = this.mPendingSync.viewTop;
            ss.position = this.mPendingSync.position;
            ss.height = this.mPendingSync.height;
            ss.filter = this.mPendingSync.filter;
            ss.inActionMode = this.mPendingSync.inActionMode;
            ss.checkedItemCount = this.mPendingSync.checkedItemCount;
            ss.checkState = this.mPendingSync.checkState;
            ss.checkIdState = this.mPendingSync.checkIdState;
            return ss;
        }
        boolean haveChildren = getChildCount() > 0 && this.mItemCount > 0;
        long selectedId = getSelectedItemId();
        ss.selectedId = selectedId;
        ss.height = getHeight();
        if (selectedId >= 0) {
            ss.viewTop = this.mSelectedTop;
            ss.position = getSelectedItemPosition();
            ss.firstId = -1L;
        } else if (haveChildren && this.mFirstPosition > 0) {
            View v = getChildAt(0);
            ss.viewTop = v.getTop();
            int firstPos = this.mFirstPosition;
            if (firstPos >= this.mItemCount) {
                firstPos = this.mItemCount - 1;
            }
            ss.position = firstPos;
            ss.firstId = this.mAdapter.getItemId(firstPos);
        } else {
            ss.viewTop = 0;
            ss.firstId = -1L;
            ss.position = 0;
        }
        ss.filter = null;
        if (this.mFiltered && (textFilter = this.mTextFilter) != null && (filterText = textFilter.getText()) != null) {
            ss.filter = filterText.toString();
        }
        ss.inActionMode = this.mChoiceMode == 3 && this.mChoiceActionMode != null;
        if (this.mCheckStates != null) {
            ss.checkState = this.mCheckStates.m905clone();
        }
        if (this.mCheckedIdStates != null) {
            LongSparseArray<Integer> idState = new LongSparseArray<>();
            int count = this.mCheckedIdStates.size();
            for (int i = 0; i < count; i++) {
                idState.put(this.mCheckedIdStates.keyAt(i), this.mCheckedIdStates.valueAt(i));
            }
            ss.checkIdState = idState;
        }
        ss.checkedItemCount = this.mCheckedItemCount;
        if (this.mRemoteAdapter != null) {
            this.mRemoteAdapter.saveRemoteViewsCache();
        }
        return ss;
    }

    @Override // android.view.View
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        this.mDataChanged = true;
        this.mSyncHeight = ss.height;
        if (ss.selectedId >= 0) {
            this.mNeedSync = true;
            this.mPendingSync = ss;
            this.mSyncRowId = ss.selectedId;
            this.mSyncPosition = ss.position;
            this.mSpecificTop = ss.viewTop;
            this.mSyncMode = 0;
        } else if (ss.firstId >= 0) {
            setSelectedPositionInt(-1);
            setNextSelectedPositionInt(-1);
            this.mSelectorPosition = -1;
            this.mNeedSync = true;
            this.mPendingSync = ss;
            this.mSyncRowId = ss.firstId;
            this.mSyncPosition = ss.position;
            this.mSpecificTop = ss.viewTop;
            this.mSyncMode = 1;
        }
        setFilterText(ss.filter);
        if (ss.checkState != null) {
            this.mCheckStates = ss.checkState;
        }
        if (ss.checkIdState != null) {
            this.mCheckedIdStates = ss.checkIdState;
        }
        this.mCheckedItemCount = ss.checkedItemCount;
        if (ss.inActionMode && this.mChoiceMode == 3 && this.mMultiChoiceModeCallback != null) {
            this.mChoiceActionMode = startActionMode(this.mMultiChoiceModeCallback);
        }
        requestLayout();
    }

    private boolean acceptFilter() {
        return this.mTextFilterEnabled && (getAdapter() instanceof Filterable) && ((Filterable) getAdapter()).getFilter() != null;
    }

    public void setFilterText(String filterText) {
        if (this.mTextFilterEnabled && !TextUtils.isEmpty(filterText)) {
            createTextFilter(false);
            this.mTextFilter.setText(filterText);
            this.mTextFilter.setSelection(filterText.length());
            if (this.mAdapter instanceof Filterable) {
                if (this.mPopup == null) {
                    Filter f = ((Filterable) this.mAdapter).getFilter();
                    f.filter(filterText);
                }
                this.mFiltered = true;
                this.mDataSetObserver.clearSavedState();
            }
        }
    }

    public CharSequence getTextFilter() {
        if (this.mTextFilterEnabled && this.mTextFilter != null) {
            return this.mTextFilter.getText();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (gainFocus && this.mSelectedPosition < 0 && !isInTouchMode()) {
            if (!isAttachedToWindow() && this.mAdapter != null) {
                this.mDataChanged = true;
                this.mOldItemCount = this.mItemCount;
                this.mItemCount = this.mAdapter.getCount();
            }
            resurrectSelection();
        }
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestLayout() {
        if (!this.mBlockLayoutRequests && !this.mInLayout) {
            super.requestLayout();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void resetList() {
        removeAllViewsInLayout();
        this.mFirstPosition = 0;
        this.mDataChanged = false;
        this.mPositionScrollAfterLayout = null;
        this.mNeedSync = false;
        this.mPendingSync = null;
        this.mOldSelectedPosition = -1;
        this.mOldSelectedRowId = Long.MIN_VALUE;
        setSelectedPositionInt(-1);
        setNextSelectedPositionInt(-1);
        this.mSelectedTop = 0;
        this.mSelectorPosition = -1;
        this.mSelectorRect.setEmpty();
        invalidate();
    }

    @Override // android.view.View
    protected int computeVerticalScrollExtent() {
        int count = getChildCount();
        if (count > 0) {
            if (this.mSmoothScrollbarEnabled) {
                int extent = count * 100;
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
            return 1;
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public int computeVerticalScrollOffset() {
        int index;
        int firstPosition = this.mFirstPosition;
        int childCount = getChildCount();
        if (firstPosition >= 0 && childCount > 0) {
            if (this.mSmoothScrollbarEnabled) {
                View view = getChildAt(0);
                int top = view.getTop();
                int height = view.getHeight();
                if (height > 0) {
                    return Math.max(((firstPosition * 100) - ((top * 100) / height)) + ((int) ((this.mScrollY / getHeight()) * this.mItemCount * 100.0f)), 0);
                }
                return 0;
            }
            int count = this.mItemCount;
            if (firstPosition == 0) {
                index = 0;
            } else if (firstPosition + childCount == count) {
                index = count;
            } else {
                index = firstPosition + (childCount / 2);
            }
            return (int) (firstPosition + (childCount * (index / count)));
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public int computeVerticalScrollRange() {
        int result;
        if (this.mSmoothScrollbarEnabled) {
            result = Math.max(this.mItemCount * 100, 0);
            if (this.mScrollY != 0) {
                result += Math.abs((int) ((this.mScrollY / getHeight()) * this.mItemCount * 100.0f));
            }
        } else {
            result = this.mItemCount;
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public float getTopFadingEdgeStrength() {
        int count = getChildCount();
        float fadeEdge = super.getTopFadingEdgeStrength();
        if (count == 0) {
            return fadeEdge;
        }
        if (this.mFirstPosition > 0) {
            return 1.0f;
        }
        int top = getChildAt(0).getTop();
        float fadeLength = getVerticalFadingEdgeLength();
        return top < this.mPaddingTop ? (-(top - this.mPaddingTop)) / fadeLength : fadeEdge;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public float getBottomFadingEdgeStrength() {
        int count = getChildCount();
        float fadeEdge = super.getBottomFadingEdgeStrength();
        if (count == 0) {
            return fadeEdge;
        }
        if ((this.mFirstPosition + count) - 1 < this.mItemCount - 1) {
            return 1.0f;
        }
        int bottom = getChildAt(count - 1).getBottom();
        int height = getHeight();
        float fadeLength = getVerticalFadingEdgeLength();
        return bottom > height - this.mPaddingBottom ? ((bottom - height) + this.mPaddingBottom) / fadeLength : fadeEdge;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.mSelector == null) {
            useDefaultSelector();
        }
        Rect listPadding = this.mListPadding;
        listPadding.left = this.mSelectionLeftPadding + this.mPaddingLeft;
        listPadding.top = this.mSelectionTopPadding + this.mPaddingTop;
        listPadding.right = this.mSelectionRightPadding + this.mPaddingRight;
        listPadding.bottom = this.mSelectionBottomPadding + this.mPaddingBottom;
        if (this.mTranscriptMode == 1) {
            int childCount = getChildCount();
            int listBottom = getHeight() - getPaddingBottom();
            View lastChild = getChildAt(childCount - 1);
            int lastBottom = lastChild != null ? lastChild.getBottom() : listBottom;
            this.mForceTranscriptScroll = this.mFirstPosition + childCount >= this.mLastHandledItemCount && lastBottom <= listBottom;
        }
    }

    @Override // android.widget.AdapterView, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        this.mInLayout = true;
        if (changed) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                getChildAt(i).forceLayout();
            }
            this.mRecycler.markChildrenDirty();
        }
        if (this.mFastScroller != null && (this.mItemCount != this.mOldItemCount || this.mDataChanged)) {
            this.mFastScroller.onItemCountChanged(this.mItemCount);
        }
        layoutChildren();
        this.mInLayout = false;
        this.mOverscrollMax = (b - t) / 3;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public boolean setFrame(int left, int top, int right, int bottom) {
        boolean changed = super.setFrame(left, top, right, bottom);
        if (changed) {
            boolean visible = getWindowVisibility() == 0;
            if (this.mFiltered && visible && this.mPopup != null && this.mPopup.isShowing()) {
                positionPopup();
            }
        }
        return changed;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void layoutChildren() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateScrollIndicators() {
        if (this.mScrollUp != null) {
            boolean canScrollUp = this.mFirstPosition > 0;
            if (!canScrollUp && getChildCount() > 0) {
                View child = getChildAt(0);
                canScrollUp = child.getTop() < this.mListPadding.top;
            }
            this.mScrollUp.setVisibility(canScrollUp ? 0 : 4);
        }
        if (this.mScrollDown != null) {
            int count = getChildCount();
            boolean canScrollDown = this.mFirstPosition + count < this.mItemCount;
            if (!canScrollDown && count > 0) {
                View child2 = getChildAt(count - 1);
                canScrollDown = child2.getBottom() > this.mBottom - this.mListPadding.bottom;
            }
            this.mScrollDown.setVisibility(canScrollDown ? 0 : 4);
        }
    }

    @Override // android.widget.AdapterView
    @ViewDebug.ExportedProperty
    public View getSelectedView() {
        if (this.mItemCount > 0 && this.mSelectedPosition >= 0) {
            return getChildAt(this.mSelectedPosition - this.mFirstPosition);
        }
        return null;
    }

    public int getListPaddingTop() {
        return this.mListPadding.top;
    }

    public int getListPaddingBottom() {
        return this.mListPadding.bottom;
    }

    public int getListPaddingLeft() {
        return this.mListPadding.left;
    }

    public int getListPaddingRight() {
        return this.mListPadding.right;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public View obtainView(int position, boolean[] isScrap) {
        View child;
        LayoutParams lp;
        Trace.traceBegin(8L, "obtainView");
        isScrap[0] = false;
        View scrapView = this.mRecycler.getTransientStateView(position);
        if (scrapView == null) {
            scrapView = this.mRecycler.getScrapView(position);
        }
        if (scrapView != null) {
            child = this.mAdapter.getView(position, scrapView, this);
            if (child.getImportantForAccessibility() == 0) {
                child.setImportantForAccessibility(1);
            }
            if (child != scrapView) {
                this.mRecycler.addScrapView(scrapView, position);
                if (this.mCacheColorHint != 0) {
                    child.setDrawingCacheBackgroundColor(this.mCacheColorHint);
                }
            } else {
                isScrap[0] = true;
                if (child.isAccessibilityFocused()) {
                    child.clearAccessibilityFocus();
                }
                child.dispatchFinishTemporaryDetach();
            }
        } else {
            child = this.mAdapter.getView(position, null, this);
            if (child.getImportantForAccessibility() == 0) {
                child.setImportantForAccessibility(1);
            }
            if (this.mCacheColorHint != 0) {
                child.setDrawingCacheBackgroundColor(this.mCacheColorHint);
            }
        }
        if (this.mAdapterHasStableIds) {
            ViewGroup.LayoutParams vlp = child.getLayoutParams();
            if (vlp == null) {
                lp = (LayoutParams) generateDefaultLayoutParams();
            } else if (!checkLayoutParams(vlp)) {
                lp = (LayoutParams) generateLayoutParams(vlp);
            } else {
                lp = (LayoutParams) vlp;
            }
            lp.itemId = this.mAdapter.getItemId(position);
            child.setLayoutParams(lp);
        }
        if (AccessibilityManager.getInstance(this.mContext).isEnabled()) {
            if (this.mAccessibilityDelegate == null) {
                this.mAccessibilityDelegate = new ListItemAccessibilityDelegate();
            }
            if (child.getAccessibilityDelegate() == null) {
                child.setAccessibilityDelegate(this.mAccessibilityDelegate);
            }
        }
        Trace.traceEnd(8L);
        return child;
    }

    /* loaded from: AbsListView$ListItemAccessibilityDelegate.class */
    class ListItemAccessibilityDelegate extends View.AccessibilityDelegate {
        ListItemAccessibilityDelegate() {
        }

        @Override // android.view.View.AccessibilityDelegate
        public AccessibilityNodeInfo createAccessibilityNodeInfo(View host) {
            if (AbsListView.this.mDataChanged) {
                return null;
            }
            return super.createAccessibilityNodeInfo(host);
        }

        @Override // android.view.View.AccessibilityDelegate
        public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(host, info);
            int position = AbsListView.this.getPositionForView(host);
            AbsListView.this.onInitializeAccessibilityNodeInfoForItem(host, position, info);
        }

        @Override // android.view.View.AccessibilityDelegate
        public boolean performAccessibilityAction(View host, int action, Bundle arguments) {
            if (super.performAccessibilityAction(host, action, arguments)) {
                return true;
            }
            int position = AbsListView.this.getPositionForView(host);
            ListAdapter adapter = AbsListView.this.getAdapter();
            if (position == -1 || adapter == null || !AbsListView.this.isEnabled() || !adapter.isEnabled(position)) {
                return false;
            }
            long id = AbsListView.this.getItemIdAtPosition(position);
            switch (action) {
                case 4:
                    if (AbsListView.this.getSelectedItemPosition() != position) {
                        AbsListView.this.setSelection(position);
                        return true;
                    }
                    return false;
                case 8:
                    if (AbsListView.this.getSelectedItemPosition() == position) {
                        AbsListView.this.setSelection(-1);
                        return true;
                    }
                    return false;
                case 16:
                    if (AbsListView.this.isClickable()) {
                        return AbsListView.this.performItemClick(host, position, id);
                    }
                    return false;
                case 32:
                    if (AbsListView.this.isLongClickable()) {
                        return AbsListView.this.performLongPress(host, position, id);
                    }
                    return false;
                default:
                    return false;
            }
        }
    }

    public void onInitializeAccessibilityNodeInfoForItem(View view, int position, AccessibilityNodeInfo info) {
        ListAdapter adapter = getAdapter();
        if (position == -1 || adapter == null) {
            return;
        }
        if (!isEnabled() || !adapter.isEnabled(position)) {
            info.setEnabled(false);
            return;
        }
        if (position == getSelectedItemPosition()) {
            info.setSelected(true);
            info.addAction(8);
        } else {
            info.addAction(4);
        }
        if (isClickable()) {
            info.addAction(16);
            info.setClickable(true);
        }
        if (isLongClickable()) {
            info.addAction(32);
            info.setLongClickable(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void positionSelector(int position, View sel) {
        if (position != -1) {
            this.mSelectorPosition = position;
        }
        Rect selectorRect = this.mSelectorRect;
        selectorRect.set(sel.getLeft(), sel.getTop(), sel.getRight(), sel.getBottom());
        if (sel instanceof SelectionBoundsAdjuster) {
            ((SelectionBoundsAdjuster) sel).adjustListItemSelectionBounds(selectorRect);
        }
        positionSelector(selectorRect.left, selectorRect.top, selectorRect.right, selectorRect.bottom);
        boolean isChildViewEnabled = this.mIsChildViewEnabled;
        if (sel.isEnabled() != isChildViewEnabled) {
            this.mIsChildViewEnabled = !isChildViewEnabled;
            if (getSelectedItemPosition() != -1) {
                refreshDrawableState();
            }
        }
    }

    private void positionSelector(int l, int t, int r, int b) {
        this.mSelectorRect.set(l - this.mSelectionLeftPadding, t - this.mSelectionTopPadding, r + this.mSelectionRightPadding, b + this.mSelectionBottomPadding);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void dispatchDraw(Canvas canvas) {
        int saveCount = 0;
        boolean clipToPadding = (this.mGroupFlags & 34) == 34;
        if (clipToPadding) {
            saveCount = canvas.save();
            int scrollX = this.mScrollX;
            int scrollY = this.mScrollY;
            canvas.clipRect(scrollX + this.mPaddingLeft, scrollY + this.mPaddingTop, ((scrollX + this.mRight) - this.mLeft) - this.mPaddingRight, ((scrollY + this.mBottom) - this.mTop) - this.mPaddingBottom);
            this.mGroupFlags &= -35;
        }
        boolean drawSelectorOnTop = this.mDrawSelectorOnTop;
        if (!drawSelectorOnTop) {
            drawSelector(canvas);
        }
        super.dispatchDraw(canvas);
        if (drawSelectorOnTop) {
            drawSelector(canvas);
        }
        if (clipToPadding) {
            canvas.restoreToCount(saveCount);
            this.mGroupFlags |= 34;
        }
    }

    @Override // android.view.View
    protected boolean isPaddingOffsetRequired() {
        return (this.mGroupFlags & 34) != 34;
    }

    @Override // android.view.View
    protected int getLeftPaddingOffset() {
        if ((this.mGroupFlags & 34) == 34) {
            return 0;
        }
        return -this.mPaddingLeft;
    }

    @Override // android.view.View
    protected int getTopPaddingOffset() {
        if ((this.mGroupFlags & 34) == 34) {
            return 0;
        }
        return -this.mPaddingTop;
    }

    @Override // android.view.View
    protected int getRightPaddingOffset() {
        if ((this.mGroupFlags & 34) == 34) {
            return 0;
        }
        return this.mPaddingRight;
    }

    @Override // android.view.View
    protected int getBottomPaddingOffset() {
        if ((this.mGroupFlags & 34) == 34) {
            return 0;
        }
        return this.mPaddingBottom;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (getChildCount() > 0) {
            this.mDataChanged = true;
            rememberSyncState();
        }
        if (this.mFastScroller != null) {
            this.mFastScroller.onSizeChanged(w, h, oldw, oldh);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean touchModeDrawsInPressedState() {
        switch (this.mTouchMode) {
            case 1:
            case 2:
                return true;
            default:
                return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean shouldShowSelector() {
        return !isInTouchMode() || (touchModeDrawsInPressedState() && isPressed());
    }

    private void drawSelector(Canvas canvas) {
        if (!this.mSelectorRect.isEmpty()) {
            Drawable selector = this.mSelector;
            selector.setBounds(this.mSelectorRect);
            selector.draw(canvas);
        }
    }

    public void setDrawSelectorOnTop(boolean onTop) {
        this.mDrawSelectorOnTop = onTop;
    }

    public void setSelector(int resID) {
        setSelector(getResources().getDrawable(resID));
    }

    public void setSelector(Drawable sel) {
        if (this.mSelector != null) {
            this.mSelector.setCallback(null);
            unscheduleDrawable(this.mSelector);
        }
        this.mSelector = sel;
        Rect padding = new Rect();
        sel.getPadding(padding);
        this.mSelectionLeftPadding = padding.left;
        this.mSelectionTopPadding = padding.top;
        this.mSelectionRightPadding = padding.right;
        this.mSelectionBottomPadding = padding.bottom;
        sel.setCallback(this);
        updateSelectorState();
    }

    public Drawable getSelector() {
        return this.mSelector;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void keyPressed() {
        if (!isEnabled() || !isClickable()) {
            return;
        }
        Drawable selector = this.mSelector;
        Rect selectorRect = this.mSelectorRect;
        if (selector != null) {
            if ((isFocused() || touchModeDrawsInPressedState()) && !selectorRect.isEmpty()) {
                View v = getChildAt(this.mSelectedPosition - this.mFirstPosition);
                if (v != null) {
                    if (v.hasFocusable()) {
                        return;
                    }
                    v.setPressed(true);
                }
                setPressed(true);
                boolean longClickable = isLongClickable();
                Drawable d = selector.getCurrent();
                if (d != null && (d instanceof TransitionDrawable)) {
                    if (longClickable) {
                        ((TransitionDrawable) d).startTransition(ViewConfiguration.getLongPressTimeout());
                    } else {
                        ((TransitionDrawable) d).resetTransition();
                    }
                }
                if (longClickable && !this.mDataChanged) {
                    if (this.mPendingCheckForKeyLongPress == null) {
                        this.mPendingCheckForKeyLongPress = new CheckForKeyLongPress();
                    }
                    this.mPendingCheckForKeyLongPress.rememberWindowAttachCount();
                    postDelayed(this.mPendingCheckForKeyLongPress, ViewConfiguration.getLongPressTimeout());
                }
            }
        }
    }

    public void setScrollIndicators(View up, View down) {
        this.mScrollUp = up;
        this.mScrollDown = down;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateSelectorState() {
        if (this.mSelector != null) {
            if (shouldShowSelector()) {
                this.mSelector.setState(getDrawableState());
            } else {
                this.mSelector.setState(StateSet.NOTHING);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void drawableStateChanged() {
        super.drawableStateChanged();
        updateSelectorState();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public int[] onCreateDrawableState(int extraSpace) {
        if (this.mIsChildViewEnabled) {
            return super.onCreateDrawableState(extraSpace);
        }
        int enabledState = ENABLED_STATE_SET[0];
        int[] state = super.onCreateDrawableState(extraSpace + 1);
        int enabledPos = -1;
        int i = state.length - 1;
        while (true) {
            if (i < 0) {
                break;
            } else if (state[i] != enabledState) {
                i--;
            } else {
                enabledPos = i;
                break;
            }
        }
        if (enabledPos >= 0) {
            System.arraycopy(state, enabledPos + 1, state, enabledPos, (state.length - enabledPos) - 1);
        }
        return state;
    }

    @Override // android.view.View
    public boolean verifyDrawable(Drawable dr) {
        return this.mSelector == dr || super.verifyDrawable(dr);
    }

    @Override // android.view.ViewGroup, android.view.View
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        if (this.mSelector != null) {
            this.mSelector.jumpToCurrentState();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        ViewTreeObserver treeObserver = getViewTreeObserver();
        treeObserver.addOnTouchModeChangeListener(this);
        if (this.mTextFilterEnabled && this.mPopup != null && !this.mGlobalLayoutListenerAddedFilter) {
            treeObserver.addOnGlobalLayoutListener(this);
        }
        if (this.mAdapter != null && this.mDataSetObserver == null) {
            this.mDataSetObserver = new AdapterDataSetObserver();
            this.mAdapter.registerDataSetObserver(this.mDataSetObserver);
            this.mDataChanged = true;
            this.mOldItemCount = this.mItemCount;
            this.mItemCount = this.mAdapter.getCount();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.AdapterView, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        dismissPopup();
        this.mRecycler.clear();
        ViewTreeObserver treeObserver = getViewTreeObserver();
        treeObserver.removeOnTouchModeChangeListener(this);
        if (this.mTextFilterEnabled && this.mPopup != null) {
            treeObserver.removeOnGlobalLayoutListener(this);
            this.mGlobalLayoutListenerAddedFilter = false;
        }
        if (this.mAdapter != null && this.mDataSetObserver != null) {
            this.mAdapter.unregisterDataSetObserver(this.mDataSetObserver);
            this.mDataSetObserver = null;
        }
        if (this.mScrollStrictSpan != null) {
            this.mScrollStrictSpan.finish();
            this.mScrollStrictSpan = null;
        }
        if (this.mFlingStrictSpan != null) {
            this.mFlingStrictSpan.finish();
            this.mFlingStrictSpan = null;
        }
        if (this.mFlingRunnable != null) {
            removeCallbacks(this.mFlingRunnable);
        }
        if (this.mPositionScroller != null) {
            this.mPositionScroller.stop();
        }
        if (this.mClearScrollingCache != null) {
            removeCallbacks(this.mClearScrollingCache);
        }
        if (this.mPerformClick != null) {
            removeCallbacks(this.mPerformClick);
        }
        if (this.mTouchModeReset != null) {
            removeCallbacks(this.mTouchModeReset);
            this.mTouchModeReset.run();
        }
    }

    @Override // android.view.View
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        int touchMode = isInTouchMode() ? 0 : 1;
        if (!hasWindowFocus) {
            setChildrenDrawingCacheEnabled(false);
            if (this.mFlingRunnable != null) {
                removeCallbacks(this.mFlingRunnable);
                this.mFlingRunnable.endFling();
                if (this.mPositionScroller != null) {
                    this.mPositionScroller.stop();
                }
                if (this.mScrollY != 0) {
                    this.mScrollY = 0;
                    invalidateParentCaches();
                    finishGlows();
                    invalidate();
                }
            }
            dismissPopup();
            if (touchMode == 1) {
                this.mResurrectToPosition = this.mSelectedPosition;
            }
        } else {
            if (this.mFiltered && !this.mPopupHidden) {
                showPopup();
            }
            if (touchMode != this.mLastTouchMode && this.mLastTouchMode != -1) {
                if (touchMode == 1) {
                    resurrectSelection();
                } else {
                    hideSelector();
                    this.mLayoutMode = 0;
                    layoutChildren();
                }
            }
        }
        this.mLastTouchMode = touchMode;
    }

    @Override // android.view.View
    public void onRtlPropertiesChanged(int layoutDirection) {
        super.onRtlPropertiesChanged(layoutDirection);
        if (this.mFastScroller != null) {
            this.mFastScroller.setScrollbarPosition(getVerticalScrollbarPosition());
        }
    }

    ContextMenu.ContextMenuInfo createContextMenuInfo(View view, int position, long id) {
        return new AdapterView.AdapterContextMenuInfo(view, position, id);
    }

    @Override // android.view.View
    public void onCancelPendingInputEvents() {
        super.onCancelPendingInputEvents();
        if (this.mPerformClick != null) {
            removeCallbacks(this.mPerformClick);
        }
        if (this.mPendingCheckForTap != null) {
            removeCallbacks(this.mPendingCheckForTap);
        }
        if (this.mPendingCheckForLongPress != null) {
            removeCallbacks(this.mPendingCheckForLongPress);
        }
        if (this.mPendingCheckForKeyLongPress != null) {
            removeCallbacks(this.mPendingCheckForKeyLongPress);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: AbsListView$WindowRunnnable.class */
    public class WindowRunnnable {
        private int mOriginalAttachCount;

        private WindowRunnnable() {
        }

        public void rememberWindowAttachCount() {
            this.mOriginalAttachCount = AbsListView.this.getWindowAttachCount();
        }

        public boolean sameWindow() {
            return AbsListView.this.getWindowAttachCount() == this.mOriginalAttachCount;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: AbsListView$PerformClick.class */
    public class PerformClick extends WindowRunnnable implements Runnable {
        int mClickMotionPosition;

        private PerformClick() {
            super();
        }

        @Override // java.lang.Runnable
        public void run() {
            View view;
            if (AbsListView.this.mDataChanged) {
                return;
            }
            ListAdapter adapter = AbsListView.this.mAdapter;
            int motionPosition = this.mClickMotionPosition;
            if (adapter != null && AbsListView.this.mItemCount > 0 && motionPosition != -1 && motionPosition < adapter.getCount() && sameWindow() && (view = AbsListView.this.getChildAt(motionPosition - AbsListView.this.mFirstPosition)) != null) {
                AbsListView.this.performItemClick(view, motionPosition, adapter.getItemId(motionPosition));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: AbsListView$CheckForLongPress.class */
    public class CheckForLongPress extends WindowRunnnable implements Runnable {
        private CheckForLongPress() {
            super();
        }

        @Override // java.lang.Runnable
        public void run() {
            int motionPosition = AbsListView.this.mMotionPosition;
            View child = AbsListView.this.getChildAt(motionPosition - AbsListView.this.mFirstPosition);
            if (child != null) {
                int longPressPosition = AbsListView.this.mMotionPosition;
                long longPressId = AbsListView.this.mAdapter.getItemId(AbsListView.this.mMotionPosition);
                boolean handled = false;
                if (sameWindow() && !AbsListView.this.mDataChanged) {
                    handled = AbsListView.this.performLongPress(child, longPressPosition, longPressId);
                }
                if (handled) {
                    AbsListView.this.mTouchMode = -1;
                    AbsListView.this.setPressed(false);
                    child.setPressed(false);
                    return;
                }
                AbsListView.this.mTouchMode = 2;
            }
        }
    }

    /* loaded from: AbsListView$CheckForKeyLongPress.class */
    private class CheckForKeyLongPress extends WindowRunnnable implements Runnable {
        private CheckForKeyLongPress() {
            super();
        }

        @Override // java.lang.Runnable
        public void run() {
            if (AbsListView.this.isPressed() && AbsListView.this.mSelectedPosition >= 0) {
                int index = AbsListView.this.mSelectedPosition - AbsListView.this.mFirstPosition;
                View v = AbsListView.this.getChildAt(index);
                if (!AbsListView.this.mDataChanged) {
                    boolean handled = false;
                    if (sameWindow()) {
                        handled = AbsListView.this.performLongPress(v, AbsListView.this.mSelectedPosition, AbsListView.this.mSelectedRowId);
                    }
                    if (handled) {
                        AbsListView.this.setPressed(false);
                        v.setPressed(false);
                        return;
                    }
                    return;
                }
                AbsListView.this.setPressed(false);
                if (v != null) {
                    v.setPressed(false);
                }
            }
        }
    }

    boolean performLongPress(View child, int longPressPosition, long longPressId) {
        if (this.mChoiceMode == 3) {
            if (this.mChoiceActionMode == null) {
                ActionMode startActionMode = startActionMode(this.mMultiChoiceModeCallback);
                this.mChoiceActionMode = startActionMode;
                if (startActionMode != null) {
                    setItemChecked(longPressPosition, true);
                    performHapticFeedback(0);
                    return true;
                }
                return true;
            }
            return true;
        }
        boolean handled = false;
        if (this.mOnItemLongClickListener != null) {
            handled = this.mOnItemLongClickListener.onItemLongClick(this, child, longPressPosition, longPressId);
        }
        if (!handled) {
            this.mContextMenuInfo = createContextMenuInfo(child, longPressPosition, longPressId);
            handled = super.showContextMenuForChild(this);
        }
        if (handled) {
            performHapticFeedback(0);
        }
        return handled;
    }

    @Override // android.view.View
    protected ContextMenu.ContextMenuInfo getContextMenuInfo() {
        return this.mContextMenuInfo;
    }

    @Override // android.view.View
    public boolean showContextMenu(float x, float y, int metaState) {
        int position = pointToPosition((int) x, (int) y);
        if (position != -1) {
            long id = this.mAdapter.getItemId(position);
            View child = getChildAt(position - this.mFirstPosition);
            if (child != null) {
                this.mContextMenuInfo = createContextMenuInfo(child, position, id);
                return super.showContextMenuForChild(this);
            }
        }
        return super.showContextMenu(x, y, metaState);
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public boolean showContextMenuForChild(View originalView) {
        int longPressPosition = getPositionForView(originalView);
        if (longPressPosition >= 0) {
            long longPressId = this.mAdapter.getItemId(longPressPosition);
            boolean handled = false;
            if (this.mOnItemLongClickListener != null) {
                handled = this.mOnItemLongClickListener.onItemLongClick(this, originalView, longPressPosition, longPressId);
            }
            if (!handled) {
                this.mContextMenuInfo = createContextMenuInfo(getChildAt(longPressPosition - this.mFirstPosition), longPressPosition, longPressId);
                handled = super.showContextMenuForChild(originalView);
            }
            return handled;
        }
        return false;
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (KeyEvent.isConfirmKey(keyCode)) {
            if (!isEnabled()) {
                return true;
            }
            if (isClickable() && isPressed() && this.mSelectedPosition >= 0 && this.mAdapter != null && this.mSelectedPosition < this.mAdapter.getCount()) {
                View view = getChildAt(this.mSelectedPosition - this.mFirstPosition);
                if (view != null) {
                    performItemClick(view, this.mSelectedPosition, this.mSelectedRowId);
                    view.setPressed(false);
                }
                setPressed(false);
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchSetPressed(boolean pressed) {
    }

    public int pointToPosition(int x, int y) {
        Rect frame = this.mTouchFrame;
        if (frame == null) {
            this.mTouchFrame = new Rect();
            frame = this.mTouchFrame;
        }
        int count = getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            View child = getChildAt(i);
            if (child.getVisibility() == 0) {
                child.getHitRect(frame);
                if (frame.contains(x, y)) {
                    return this.mFirstPosition + i;
                }
            }
        }
        return -1;
    }

    public long pointToRowId(int x, int y) {
        int position = pointToPosition(x, y);
        if (position >= 0) {
            return this.mAdapter.getItemId(position);
        }
        return Long.MIN_VALUE;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: AbsListView$CheckForTap.class */
    public final class CheckForTap implements Runnable {
        CheckForTap() {
        }

        @Override // java.lang.Runnable
        public void run() {
            Drawable d;
            if (AbsListView.this.mTouchMode == 0) {
                AbsListView.this.mTouchMode = 1;
                View child = AbsListView.this.getChildAt(AbsListView.this.mMotionPosition - AbsListView.this.mFirstPosition);
                if (child != null && !child.hasFocusable()) {
                    AbsListView.this.mLayoutMode = 0;
                    if (!AbsListView.this.mDataChanged) {
                        child.setPressed(true);
                        AbsListView.this.setPressed(true);
                        AbsListView.this.layoutChildren();
                        AbsListView.this.positionSelector(AbsListView.this.mMotionPosition, child);
                        AbsListView.this.refreshDrawableState();
                        int longPressTimeout = ViewConfiguration.getLongPressTimeout();
                        boolean longClickable = AbsListView.this.isLongClickable();
                        if (AbsListView.this.mSelector != null && (d = AbsListView.this.mSelector.getCurrent()) != null && (d instanceof TransitionDrawable)) {
                            if (longClickable) {
                                ((TransitionDrawable) d).startTransition(longPressTimeout);
                            } else {
                                ((TransitionDrawable) d).resetTransition();
                            }
                        }
                        if (longClickable) {
                            if (AbsListView.this.mPendingCheckForLongPress == null) {
                                AbsListView.this.mPendingCheckForLongPress = new CheckForLongPress();
                            }
                            AbsListView.this.mPendingCheckForLongPress.rememberWindowAttachCount();
                            AbsListView.this.postDelayed(AbsListView.this.mPendingCheckForLongPress, longPressTimeout);
                            return;
                        }
                        AbsListView.this.mTouchMode = 2;
                        return;
                    }
                    AbsListView.this.mTouchMode = 2;
                }
            }
        }
    }

    private boolean startScrollIfNeeded(int y) {
        int deltaY = y - this.mMotionY;
        int distance = Math.abs(deltaY);
        boolean overscroll = this.mScrollY != 0;
        if (overscroll || distance > this.mTouchSlop) {
            createScrollingCache();
            if (overscroll) {
                this.mTouchMode = 5;
                this.mMotionCorrection = 0;
            } else {
                this.mTouchMode = 3;
                this.mMotionCorrection = deltaY > 0 ? this.mTouchSlop : -this.mTouchSlop;
            }
            removeCallbacks(this.mPendingCheckForLongPress);
            setPressed(false);
            View motionView = getChildAt(this.mMotionPosition - this.mFirstPosition);
            if (motionView != null) {
                motionView.setPressed(false);
            }
            reportScrollStateChange(1);
            ViewParent parent = getParent();
            if (parent != null) {
                parent.requestDisallowInterceptTouchEvent(true);
            }
            scrollIfNeeded(y);
            return true;
        }
        return false;
    }

    private void scrollIfNeeded(int y) {
        int incrementalDeltaY;
        int motionIndex;
        ViewParent parent;
        int rawDeltaY = y - this.mMotionY;
        int deltaY = rawDeltaY - this.mMotionCorrection;
        int incrementalDeltaY2 = this.mLastY != Integer.MIN_VALUE ? y - this.mLastY : deltaY;
        if (this.mTouchMode == 3) {
            if (this.mScrollStrictSpan == null) {
                this.mScrollStrictSpan = StrictMode.enterCriticalSpan("AbsListView-scroll");
            }
            if (y != this.mLastY) {
                if ((this.mGroupFlags & 524288) == 0 && Math.abs(rawDeltaY) > this.mTouchSlop && (parent = getParent()) != null) {
                    parent.requestDisallowInterceptTouchEvent(true);
                }
                if (this.mMotionPosition >= 0) {
                    motionIndex = this.mMotionPosition - this.mFirstPosition;
                } else {
                    motionIndex = getChildCount() / 2;
                }
                int motionViewPrevTop = 0;
                View motionView = getChildAt(motionIndex);
                if (motionView != null) {
                    motionViewPrevTop = motionView.getTop();
                }
                boolean atEdge = false;
                if (incrementalDeltaY2 != 0) {
                    atEdge = trackMotionScroll(deltaY, incrementalDeltaY2);
                }
                View motionView2 = getChildAt(motionIndex);
                if (motionView2 != null) {
                    int motionViewRealTop = motionView2.getTop();
                    if (atEdge) {
                        int overscroll = (-incrementalDeltaY2) - (motionViewRealTop - motionViewPrevTop);
                        overScrollBy(0, overscroll, 0, this.mScrollY, 0, 0, 0, this.mOverscrollDistance, true);
                        if (Math.abs(this.mOverscrollDistance) == Math.abs(this.mScrollY) && this.mVelocityTracker != null) {
                            this.mVelocityTracker.clear();
                        }
                        int overscrollMode = getOverScrollMode();
                        if (overscrollMode == 0 || (overscrollMode == 1 && !contentFits())) {
                            this.mDirection = 0;
                            this.mTouchMode = 5;
                            if (rawDeltaY > 0) {
                                this.mEdgeGlowTop.onPull(overscroll / getHeight());
                                if (!this.mEdgeGlowBottom.isFinished()) {
                                    this.mEdgeGlowBottom.onRelease();
                                }
                                invalidate(this.mEdgeGlowTop.getBounds(false));
                            } else if (rawDeltaY < 0) {
                                this.mEdgeGlowBottom.onPull(overscroll / getHeight());
                                if (!this.mEdgeGlowTop.isFinished()) {
                                    this.mEdgeGlowTop.onRelease();
                                }
                                invalidate(this.mEdgeGlowBottom.getBounds(true));
                            }
                        }
                    }
                    this.mMotionY = y;
                }
                this.mLastY = y;
            }
        } else if (this.mTouchMode == 5 && y != this.mLastY) {
            int oldScroll = this.mScrollY;
            int newScroll = oldScroll - incrementalDeltaY2;
            int newDirection = y > this.mLastY ? 1 : -1;
            if (this.mDirection == 0) {
                this.mDirection = newDirection;
            }
            int overScrollDistance = -incrementalDeltaY2;
            if ((newScroll < 0 && oldScroll >= 0) || (newScroll > 0 && oldScroll <= 0)) {
                overScrollDistance = -oldScroll;
                incrementalDeltaY = incrementalDeltaY2 + overScrollDistance;
            } else {
                incrementalDeltaY = 0;
            }
            if (overScrollDistance != 0) {
                overScrollBy(0, overScrollDistance, 0, this.mScrollY, 0, 0, 0, this.mOverscrollDistance, true);
                int overscrollMode2 = getOverScrollMode();
                if (overscrollMode2 == 0 || (overscrollMode2 == 1 && !contentFits())) {
                    if (rawDeltaY > 0) {
                        this.mEdgeGlowTop.onPull(overScrollDistance / getHeight());
                        if (!this.mEdgeGlowBottom.isFinished()) {
                            this.mEdgeGlowBottom.onRelease();
                        }
                        invalidate(this.mEdgeGlowTop.getBounds(false));
                    } else if (rawDeltaY < 0) {
                        this.mEdgeGlowBottom.onPull(overScrollDistance / getHeight());
                        if (!this.mEdgeGlowTop.isFinished()) {
                            this.mEdgeGlowTop.onRelease();
                        }
                        invalidate(this.mEdgeGlowBottom.getBounds(true));
                    }
                }
            }
            if (incrementalDeltaY != 0) {
                if (this.mScrollY != 0) {
                    this.mScrollY = 0;
                    invalidateParentIfNeeded();
                }
                trackMotionScroll(incrementalDeltaY, incrementalDeltaY);
                this.mTouchMode = 3;
                int motionPosition = findClosestMotionRow(y);
                this.mMotionCorrection = 0;
                View motionView3 = getChildAt(motionPosition - this.mFirstPosition);
                this.mMotionViewOriginalTop = motionView3 != null ? motionView3.getTop() : 0;
                this.mMotionY = y;
                this.mMotionPosition = motionPosition;
            }
            this.mLastY = y;
            this.mDirection = newDirection;
        }
    }

    @Override // android.view.ViewTreeObserver.OnTouchModeChangeListener
    public void onTouchModeChanged(boolean isInTouchMode) {
        if (isInTouchMode) {
            hideSelector();
            if (getHeight() > 0 && getChildCount() > 0) {
                layoutChildren();
            }
            updateSelectorState();
            return;
        }
        int touchMode = this.mTouchMode;
        if (touchMode == 5 || touchMode == 6) {
            if (this.mFlingRunnable != null) {
                this.mFlingRunnable.endFling();
            }
            if (this.mPositionScroller != null) {
                this.mPositionScroller.stop();
            }
            if (this.mScrollY != 0) {
                this.mScrollY = 0;
                invalidateParentCaches();
                finishGlows();
                invalidate();
            }
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isEnabled()) {
            return isClickable() || isLongClickable();
        }
        if (this.mPositionScroller != null) {
            this.mPositionScroller.stop();
        }
        if (!isAttachedToWindow()) {
            return false;
        }
        if (this.mFastScroller != null) {
            boolean intercepted = this.mFastScroller.onTouchEvent(ev);
            if (intercepted) {
                return true;
            }
        }
        initVelocityTrackerIfNotExists();
        this.mVelocityTracker.addMovement(ev);
        int actionMasked = ev.getActionMasked();
        switch (actionMasked) {
            case 0:
                onTouchDown(ev);
                return true;
            case 1:
                onTouchUp(ev);
                return true;
            case 2:
                onTouchMove(ev);
                return true;
            case 3:
                onTouchCancel();
                return true;
            case 4:
            default:
                return true;
            case 5:
                int index = ev.getActionIndex();
                int id = ev.getPointerId(index);
                int x = (int) ev.getX(index);
                int y = (int) ev.getY(index);
                this.mMotionCorrection = 0;
                this.mActivePointerId = id;
                this.mMotionX = x;
                this.mMotionY = y;
                int motionPosition = pointToPosition(x, y);
                if (motionPosition >= 0) {
                    View child = getChildAt(motionPosition - this.mFirstPosition);
                    this.mMotionViewOriginalTop = child.getTop();
                    this.mMotionPosition = motionPosition;
                }
                this.mLastY = y;
                return true;
            case 6:
                onSecondaryPointerUp(ev);
                int x2 = this.mMotionX;
                int y2 = this.mMotionY;
                int motionPosition2 = pointToPosition(x2, y2);
                if (motionPosition2 >= 0) {
                    View child2 = getChildAt(motionPosition2 - this.mFirstPosition);
                    this.mMotionViewOriginalTop = child2.getTop();
                    this.mMotionPosition = motionPosition2;
                }
                this.mLastY = y2;
                return true;
        }
    }

    private void onTouchDown(MotionEvent ev) {
        this.mActivePointerId = ev.getPointerId(0);
        if (this.mTouchMode == 6) {
            this.mFlingRunnable.endFling();
            if (this.mPositionScroller != null) {
                this.mPositionScroller.stop();
            }
            this.mTouchMode = 5;
            this.mMotionX = (int) ev.getX();
            this.mMotionY = (int) ev.getY();
            this.mLastY = this.mMotionY;
            this.mMotionCorrection = 0;
            this.mDirection = 0;
        } else {
            int x = (int) ev.getX();
            int y = (int) ev.getY();
            int motionPosition = pointToPosition(x, y);
            if (!this.mDataChanged) {
                if (this.mTouchMode == 4) {
                    createScrollingCache();
                    this.mTouchMode = 3;
                    this.mMotionCorrection = 0;
                    motionPosition = findMotionRow(y);
                    this.mFlingRunnable.flywheelTouch();
                } else if (motionPosition >= 0 && getAdapter().isEnabled(motionPosition)) {
                    this.mTouchMode = 0;
                    if (this.mPendingCheckForTap == null) {
                        this.mPendingCheckForTap = new CheckForTap();
                    }
                    postDelayed(this.mPendingCheckForTap, ViewConfiguration.getTapTimeout());
                }
            }
            if (motionPosition >= 0) {
                View v = getChildAt(motionPosition - this.mFirstPosition);
                this.mMotionViewOriginalTop = v.getTop();
            }
            this.mMotionX = x;
            this.mMotionY = y;
            this.mMotionPosition = motionPosition;
            this.mLastY = Integer.MIN_VALUE;
        }
        if (this.mTouchMode == 0 && this.mMotionPosition != -1 && performButtonActionOnTouchDown(ev)) {
            removeCallbacks(this.mPendingCheckForTap);
        }
    }

    private void onTouchMove(MotionEvent ev) {
        int pointerIndex = ev.findPointerIndex(this.mActivePointerId);
        if (pointerIndex == -1) {
            pointerIndex = 0;
            this.mActivePointerId = ev.getPointerId(0);
        }
        if (this.mDataChanged) {
            layoutChildren();
        }
        int y = (int) ev.getY(pointerIndex);
        switch (this.mTouchMode) {
            case 0:
            case 1:
            case 2:
                if (!startScrollIfNeeded(y)) {
                    float x = ev.getX(pointerIndex);
                    if (!pointInView(x, y, this.mTouchSlop)) {
                        setPressed(false);
                        View motionView = getChildAt(this.mMotionPosition - this.mFirstPosition);
                        if (motionView != null) {
                            motionView.setPressed(false);
                        }
                        removeCallbacks(this.mTouchMode == 0 ? this.mPendingCheckForTap : this.mPendingCheckForLongPress);
                        this.mTouchMode = 2;
                        updateSelectorState();
                        return;
                    }
                    return;
                }
                return;
            case 3:
            case 5:
                scrollIfNeeded(y);
                return;
            case 4:
            default:
                return;
        }
    }

    private void onTouchUp(MotionEvent ev) {
        Drawable d;
        switch (this.mTouchMode) {
            case 0:
            case 1:
            case 2:
                int motionPosition = this.mMotionPosition;
                final View child = getChildAt(motionPosition - this.mFirstPosition);
                if (child != null) {
                    if (this.mTouchMode != 0) {
                        child.setPressed(false);
                    }
                    float x = ev.getX();
                    boolean inList = x > ((float) this.mListPadding.left) && x < ((float) (getWidth() - this.mListPadding.right));
                    if (inList && !child.hasFocusable()) {
                        if (this.mPerformClick == null) {
                            this.mPerformClick = new PerformClick();
                        }
                        final PerformClick performClick = this.mPerformClick;
                        performClick.mClickMotionPosition = motionPosition;
                        performClick.rememberWindowAttachCount();
                        this.mResurrectToPosition = motionPosition;
                        if (this.mTouchMode == 0 || this.mTouchMode == 1) {
                            removeCallbacks(this.mTouchMode == 0 ? this.mPendingCheckForTap : this.mPendingCheckForLongPress);
                            this.mLayoutMode = 0;
                            if (!this.mDataChanged && this.mAdapter.isEnabled(motionPosition)) {
                                this.mTouchMode = 1;
                                setSelectedPositionInt(this.mMotionPosition);
                                layoutChildren();
                                child.setPressed(true);
                                positionSelector(this.mMotionPosition, child);
                                setPressed(true);
                                if (this.mSelector != null && (d = this.mSelector.getCurrent()) != null && (d instanceof TransitionDrawable)) {
                                    ((TransitionDrawable) d).resetTransition();
                                }
                                if (this.mTouchModeReset != null) {
                                    removeCallbacks(this.mTouchModeReset);
                                }
                                this.mTouchModeReset = new Runnable() { // from class: android.widget.AbsListView.3
                                    @Override // java.lang.Runnable
                                    public void run() {
                                        AbsListView.this.mTouchModeReset = null;
                                        AbsListView.this.mTouchMode = -1;
                                        child.setPressed(false);
                                        AbsListView.this.setPressed(false);
                                        if (!AbsListView.this.mDataChanged && AbsListView.this.isAttachedToWindow()) {
                                            performClick.run();
                                        }
                                    }
                                };
                                postDelayed(this.mTouchModeReset, ViewConfiguration.getPressedStateDuration());
                                return;
                            }
                            this.mTouchMode = -1;
                            updateSelectorState();
                            return;
                        } else if (!this.mDataChanged && this.mAdapter.isEnabled(motionPosition)) {
                            performClick.run();
                        }
                    }
                }
                this.mTouchMode = -1;
                updateSelectorState();
                break;
            case 3:
                int childCount = getChildCount();
                if (childCount > 0) {
                    int firstChildTop = getChildAt(0).getTop();
                    int lastChildBottom = getChildAt(childCount - 1).getBottom();
                    int contentTop = this.mListPadding.top;
                    int contentBottom = getHeight() - this.mListPadding.bottom;
                    if (this.mFirstPosition == 0 && firstChildTop >= contentTop && this.mFirstPosition + childCount < this.mItemCount && lastChildBottom <= getHeight() - contentBottom) {
                        this.mTouchMode = -1;
                        reportScrollStateChange(0);
                        break;
                    } else {
                        VelocityTracker velocityTracker = this.mVelocityTracker;
                        velocityTracker.computeCurrentVelocity(1000, this.mMaximumVelocity);
                        int initialVelocity = (int) (velocityTracker.getYVelocity(this.mActivePointerId) * this.mVelocityScale);
                        if (Math.abs(initialVelocity) > this.mMinimumVelocity && ((this.mFirstPosition != 0 || firstChildTop != contentTop - this.mOverscrollDistance) && (this.mFirstPosition + childCount != this.mItemCount || lastChildBottom != contentBottom + this.mOverscrollDistance))) {
                            if (this.mFlingRunnable == null) {
                                this.mFlingRunnable = new FlingRunnable();
                            }
                            reportScrollStateChange(2);
                            this.mFlingRunnable.start(-initialVelocity);
                            break;
                        } else {
                            this.mTouchMode = -1;
                            reportScrollStateChange(0);
                            if (this.mFlingRunnable != null) {
                                this.mFlingRunnable.endFling();
                            }
                            if (this.mPositionScroller != null) {
                                this.mPositionScroller.stop();
                                break;
                            }
                        }
                    }
                } else {
                    this.mTouchMode = -1;
                    reportScrollStateChange(0);
                    break;
                }
                break;
            case 5:
                if (this.mFlingRunnable == null) {
                    this.mFlingRunnable = new FlingRunnable();
                }
                VelocityTracker velocityTracker2 = this.mVelocityTracker;
                velocityTracker2.computeCurrentVelocity(1000, this.mMaximumVelocity);
                int initialVelocity2 = (int) velocityTracker2.getYVelocity(this.mActivePointerId);
                reportScrollStateChange(2);
                if (Math.abs(initialVelocity2) > this.mMinimumVelocity) {
                    this.mFlingRunnable.startOverfling(-initialVelocity2);
                    break;
                } else {
                    this.mFlingRunnable.startSpringback();
                    break;
                }
        }
        setPressed(false);
        if (this.mEdgeGlowTop != null) {
            this.mEdgeGlowTop.onRelease();
            this.mEdgeGlowBottom.onRelease();
        }
        invalidate();
        removeCallbacks(this.mPendingCheckForLongPress);
        recycleVelocityTracker();
        this.mActivePointerId = -1;
        if (this.mScrollStrictSpan != null) {
            this.mScrollStrictSpan.finish();
            this.mScrollStrictSpan = null;
        }
    }

    private void onTouchCancel() {
        switch (this.mTouchMode) {
            case 5:
                if (this.mFlingRunnable == null) {
                    this.mFlingRunnable = new FlingRunnable();
                }
                this.mFlingRunnable.startSpringback();
                break;
            case 6:
                break;
            default:
                this.mTouchMode = -1;
                setPressed(false);
                View motionView = getChildAt(this.mMotionPosition - this.mFirstPosition);
                if (motionView != null) {
                    motionView.setPressed(false);
                }
                clearScrollingCache();
                removeCallbacks(this.mPendingCheckForLongPress);
                recycleVelocityTracker();
                break;
        }
        if (this.mEdgeGlowTop != null) {
            this.mEdgeGlowTop.onRelease();
            this.mEdgeGlowBottom.onRelease();
        }
        this.mActivePointerId = -1;
    }

    @Override // android.view.View
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        if (this.mScrollY != scrollY) {
            onScrollChanged(this.mScrollX, scrollY, this.mScrollX, this.mScrollY);
            this.mScrollY = scrollY;
            invalidateParentIfNeeded();
            awakenScrollBars();
        }
    }

    @Override // android.view.View
    public boolean onGenericMotionEvent(MotionEvent event) {
        if ((event.getSource() & 2) != 0) {
            switch (event.getAction()) {
                case 8:
                    if (this.mTouchMode == -1) {
                        float vscroll = event.getAxisValue(9);
                        if (vscroll != 0.0f) {
                            int delta = (int) (vscroll * getVerticalScrollFactor());
                            if (!trackMotionScroll(delta, delta)) {
                                return true;
                            }
                        }
                    }
                    break;
            }
        }
        return super.onGenericMotionEvent(event);
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (this.mEdgeGlowTop != null) {
            int scrollY = this.mScrollY;
            if (!this.mEdgeGlowTop.isFinished()) {
                int restoreCount = canvas.save();
                int leftPadding = this.mListPadding.left + this.mGlowPaddingLeft;
                int rightPadding = this.mListPadding.right + this.mGlowPaddingRight;
                int width = (getWidth() - leftPadding) - rightPadding;
                int edgeY = Math.min(0, scrollY + this.mFirstPositionDistanceGuess);
                canvas.translate(leftPadding, edgeY);
                this.mEdgeGlowTop.setSize(width, getHeight());
                if (this.mEdgeGlowTop.draw(canvas)) {
                    this.mEdgeGlowTop.setPosition(leftPadding, edgeY);
                    invalidate(this.mEdgeGlowTop.getBounds(false));
                }
                canvas.restoreToCount(restoreCount);
            }
            if (!this.mEdgeGlowBottom.isFinished()) {
                int restoreCount2 = canvas.save();
                int leftPadding2 = this.mListPadding.left + this.mGlowPaddingLeft;
                int rightPadding2 = this.mListPadding.right + this.mGlowPaddingRight;
                int width2 = (getWidth() - leftPadding2) - rightPadding2;
                int height = getHeight();
                int edgeX = (-width2) + leftPadding2;
                int edgeY2 = Math.max(height, scrollY + this.mLastPositionDistanceGuess);
                canvas.translate(edgeX, edgeY2);
                canvas.rotate(180.0f, width2, 0.0f);
                this.mEdgeGlowBottom.setSize(width2, height);
                if (this.mEdgeGlowBottom.draw(canvas)) {
                    this.mEdgeGlowBottom.setPosition(edgeX + width2, edgeY2);
                    invalidate(this.mEdgeGlowBottom.getBounds(true));
                }
                canvas.restoreToCount(restoreCount2);
            }
        }
    }

    public void setOverScrollEffectPadding(int leftPadding, int rightPadding) {
        this.mGlowPaddingLeft = leftPadding;
        this.mGlowPaddingRight = rightPadding;
    }

    private void initOrResetVelocityTracker() {
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        } else {
            this.mVelocityTracker.clear();
        }
    }

    private void initVelocityTrackerIfNotExists() {
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
    }

    private void recycleVelocityTracker() {
        if (this.mVelocityTracker != null) {
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        if (disallowIntercept) {
            recycleVelocityTracker();
        }
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptHoverEvent(MotionEvent event) {
        if (this.mFastScroller != null && this.mFastScroller.onInterceptHoverEvent(event)) {
            return true;
        }
        return super.onInterceptHoverEvent(event);
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (this.mPositionScroller != null) {
            this.mPositionScroller.stop();
        }
        if (!isAttachedToWindow()) {
            return false;
        }
        if (this.mFastScroller != null && this.mFastScroller.onInterceptTouchEvent(ev)) {
            return true;
        }
        switch (action & 255) {
            case 0:
                int touchMode = this.mTouchMode;
                if (touchMode == 6 || touchMode == 5) {
                    this.mMotionCorrection = 0;
                    return true;
                }
                int x = (int) ev.getX();
                int y = (int) ev.getY();
                this.mActivePointerId = ev.getPointerId(0);
                int motionPosition = findMotionRow(y);
                if (touchMode != 4 && motionPosition >= 0) {
                    View v = getChildAt(motionPosition - this.mFirstPosition);
                    this.mMotionViewOriginalTop = v.getTop();
                    this.mMotionX = x;
                    this.mMotionY = y;
                    this.mMotionPosition = motionPosition;
                    this.mTouchMode = 0;
                    clearScrollingCache();
                }
                this.mLastY = Integer.MIN_VALUE;
                initOrResetVelocityTracker();
                this.mVelocityTracker.addMovement(ev);
                if (touchMode == 4) {
                    return true;
                }
                return false;
            case 1:
            case 3:
                this.mTouchMode = -1;
                this.mActivePointerId = -1;
                recycleVelocityTracker();
                reportScrollStateChange(0);
                return false;
            case 2:
                switch (this.mTouchMode) {
                    case 0:
                        int pointerIndex = ev.findPointerIndex(this.mActivePointerId);
                        if (pointerIndex == -1) {
                            pointerIndex = 0;
                            this.mActivePointerId = ev.getPointerId(0);
                        }
                        int y2 = (int) ev.getY(pointerIndex);
                        initVelocityTrackerIfNotExists();
                        this.mVelocityTracker.addMovement(ev);
                        if (startScrollIfNeeded(y2)) {
                            return true;
                        }
                        return false;
                    default:
                        return false;
                }
            case 4:
            case 5:
            default:
                return false;
            case 6:
                onSecondaryPointerUp(ev);
                return false;
        }
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        int pointerIndex = (ev.getAction() & 65280) >> 8;
        int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == this.mActivePointerId) {
            int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            this.mMotionX = (int) ev.getX(newPointerIndex);
            this.mMotionY = (int) ev.getY(newPointerIndex);
            this.mMotionCorrection = 0;
            this.mActivePointerId = ev.getPointerId(newPointerIndex);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public void addTouchables(ArrayList<View> views) {
        int count = getChildCount();
        int firstPosition = this.mFirstPosition;
        ListAdapter adapter = this.mAdapter;
        if (adapter == null) {
            return;
        }
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (adapter.isEnabled(firstPosition + i)) {
                views.add(child);
            }
            child.addTouchables(views);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void reportScrollStateChange(int newState) {
        if (newState != this.mLastScrollState && this.mOnScrollListener != null) {
            this.mLastScrollState = newState;
            this.mOnScrollListener.onScrollStateChanged(this, newState);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: AbsListView$FlingRunnable.class */
    public class FlingRunnable implements Runnable {
        private final OverScroller mScroller;
        private int mLastFlingY;
        private final Runnable mCheckFlywheel = new Runnable() { // from class: android.widget.AbsListView.FlingRunnable.1
            @Override // java.lang.Runnable
            public void run() {
                int activeId = AbsListView.this.mActivePointerId;
                VelocityTracker vt = AbsListView.this.mVelocityTracker;
                OverScroller scroller = FlingRunnable.this.mScroller;
                if (vt == null || activeId == -1) {
                    return;
                }
                vt.computeCurrentVelocity(1000, AbsListView.this.mMaximumVelocity);
                float yvel = -vt.getYVelocity(activeId);
                if (Math.abs(yvel) >= AbsListView.this.mMinimumVelocity && scroller.isScrollingInDirection(0.0f, yvel)) {
                    AbsListView.this.postDelayed(this, 40L);
                    return;
                }
                FlingRunnable.this.endFling();
                AbsListView.this.mTouchMode = 3;
                AbsListView.this.reportScrollStateChange(1);
            }
        };
        private static final int FLYWHEEL_TIMEOUT = 40;

        FlingRunnable() {
            this.mScroller = new OverScroller(AbsListView.this.getContext());
        }

        void start(int initialVelocity) {
            int initialY = initialVelocity < 0 ? Integer.MAX_VALUE : 0;
            this.mLastFlingY = initialY;
            this.mScroller.setInterpolator(null);
            this.mScroller.fling(0, initialY, 0, initialVelocity, 0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
            AbsListView.this.mTouchMode = 4;
            AbsListView.this.postOnAnimation(this);
            if (AbsListView.this.mFlingStrictSpan == null) {
                AbsListView.this.mFlingStrictSpan = StrictMode.enterCriticalSpan("AbsListView-fling");
            }
        }

        void startSpringback() {
            if (this.mScroller.springBack(0, AbsListView.this.mScrollY, 0, 0, 0, 0)) {
                AbsListView.this.mTouchMode = 6;
                AbsListView.this.invalidate();
                AbsListView.this.postOnAnimation(this);
                return;
            }
            AbsListView.this.mTouchMode = -1;
            AbsListView.this.reportScrollStateChange(0);
        }

        void startOverfling(int initialVelocity) {
            this.mScroller.setInterpolator(null);
            this.mScroller.fling(0, AbsListView.this.mScrollY, 0, initialVelocity, 0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, AbsListView.this.getHeight());
            AbsListView.this.mTouchMode = 6;
            AbsListView.this.invalidate();
            AbsListView.this.postOnAnimation(this);
        }

        void edgeReached(int delta) {
            this.mScroller.notifyVerticalEdgeReached(AbsListView.this.mScrollY, 0, AbsListView.this.mOverflingDistance);
            int overscrollMode = AbsListView.this.getOverScrollMode();
            if (overscrollMode == 0 || (overscrollMode == 1 && !AbsListView.this.contentFits())) {
                AbsListView.this.mTouchMode = 6;
                int vel = (int) this.mScroller.getCurrVelocity();
                if (delta > 0) {
                    AbsListView.this.mEdgeGlowTop.onAbsorb(vel);
                } else {
                    AbsListView.this.mEdgeGlowBottom.onAbsorb(vel);
                }
            } else {
                AbsListView.this.mTouchMode = -1;
                if (AbsListView.this.mPositionScroller != null) {
                    AbsListView.this.mPositionScroller.stop();
                }
            }
            AbsListView.this.invalidate();
            AbsListView.this.postOnAnimation(this);
        }

        void startScroll(int distance, int duration, boolean linear) {
            int initialY = distance < 0 ? Integer.MAX_VALUE : 0;
            this.mLastFlingY = initialY;
            this.mScroller.setInterpolator(linear ? AbsListView.sLinearInterpolator : null);
            this.mScroller.startScroll(0, initialY, 0, distance, duration);
            AbsListView.this.mTouchMode = 4;
            AbsListView.this.postOnAnimation(this);
        }

        void endFling() {
            AbsListView.this.mTouchMode = -1;
            AbsListView.this.removeCallbacks(this);
            AbsListView.this.removeCallbacks(this.mCheckFlywheel);
            AbsListView.this.reportScrollStateChange(0);
            AbsListView.this.clearScrollingCache();
            this.mScroller.abortAnimation();
            if (AbsListView.this.mFlingStrictSpan != null) {
                AbsListView.this.mFlingStrictSpan.finish();
                AbsListView.this.mFlingStrictSpan = null;
            }
        }

        void flywheelTouch() {
            AbsListView.this.postDelayed(this.mCheckFlywheel, 40L);
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        @Override // java.lang.Runnable
        public void run() {
            int delta;
            switch (AbsListView.this.mTouchMode) {
                case 3:
                    if (this.mScroller.isFinished()) {
                        return;
                    }
                    break;
                case 4:
                    break;
                case 5:
                default:
                    endFling();
                    return;
                case 6:
                    OverScroller scroller = this.mScroller;
                    if (scroller.computeScrollOffset()) {
                        int scrollY = AbsListView.this.mScrollY;
                        int currY = scroller.getCurrY();
                        int deltaY = currY - scrollY;
                        if (AbsListView.this.overScrollBy(0, deltaY, 0, scrollY, 0, 0, 0, AbsListView.this.mOverflingDistance, false)) {
                            boolean crossDown = scrollY <= 0 && currY > 0;
                            boolean crossUp = scrollY >= 0 && currY < 0;
                            if (crossDown || crossUp) {
                                int velocity = (int) scroller.getCurrVelocity();
                                if (crossUp) {
                                    velocity = -velocity;
                                }
                                scroller.abortAnimation();
                                start(velocity);
                                return;
                            }
                            startSpringback();
                            return;
                        }
                        AbsListView.this.invalidate();
                        AbsListView.this.postOnAnimation(this);
                        return;
                    }
                    endFling();
                    return;
            }
            if (AbsListView.this.mDataChanged) {
                AbsListView.this.layoutChildren();
            }
            if (AbsListView.this.mItemCount == 0 || AbsListView.this.getChildCount() == 0) {
                endFling();
                return;
            }
            OverScroller scroller2 = this.mScroller;
            boolean more = scroller2.computeScrollOffset();
            int y = scroller2.getCurrY();
            int delta2 = this.mLastFlingY - y;
            if (delta2 > 0) {
                AbsListView.this.mMotionPosition = AbsListView.this.mFirstPosition;
                View firstView = AbsListView.this.getChildAt(0);
                AbsListView.this.mMotionViewOriginalTop = firstView.getTop();
                delta = Math.min(((AbsListView.this.getHeight() - AbsListView.this.mPaddingBottom) - AbsListView.this.mPaddingTop) - 1, delta2);
            } else {
                int offsetToLast = AbsListView.this.getChildCount() - 1;
                AbsListView.this.mMotionPosition = AbsListView.this.mFirstPosition + offsetToLast;
                View lastView = AbsListView.this.getChildAt(offsetToLast);
                AbsListView.this.mMotionViewOriginalTop = lastView.getTop();
                delta = Math.max(-(((AbsListView.this.getHeight() - AbsListView.this.mPaddingBottom) - AbsListView.this.mPaddingTop) - 1), delta2);
            }
            View motionView = AbsListView.this.getChildAt(AbsListView.this.mMotionPosition - AbsListView.this.mFirstPosition);
            int oldTop = 0;
            if (motionView != null) {
                oldTop = motionView.getTop();
            }
            boolean atEdge = AbsListView.this.trackMotionScroll(delta, delta);
            boolean atEnd = atEdge && delta != 0;
            if (atEnd) {
                if (motionView != null) {
                    int overshoot = -(delta - (motionView.getTop() - oldTop));
                    AbsListView.this.overScrollBy(0, overshoot, 0, AbsListView.this.mScrollY, 0, 0, 0, AbsListView.this.mOverflingDistance, false);
                }
                if (more) {
                    edgeReached(delta);
                }
            } else if (more && !atEnd) {
                if (atEdge) {
                    AbsListView.this.invalidate();
                }
                this.mLastFlingY = y;
                AbsListView.this.postOnAnimation(this);
            } else {
                endFling();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: AbsListView$PositionScroller.class */
    public class PositionScroller implements Runnable {
        private static final int SCROLL_DURATION = 200;
        private static final int MOVE_DOWN_POS = 1;
        private static final int MOVE_UP_POS = 2;
        private static final int MOVE_DOWN_BOUND = 3;
        private static final int MOVE_UP_BOUND = 4;
        private static final int MOVE_OFFSET = 5;
        private int mMode;
        private int mTargetPos;
        private int mBoundPos;
        private int mLastSeenPos;
        private int mScrollDuration;
        private final int mExtraScroll;
        private int mOffsetFromTop;

        PositionScroller() {
            this.mExtraScroll = ViewConfiguration.get(AbsListView.this.mContext).getScaledFadingEdgeLength();
        }

        void start(final int position) {
            int viewTravelCount;
            stop();
            if (AbsListView.this.mDataChanged) {
                AbsListView.this.mPositionScrollAfterLayout = new Runnable() { // from class: android.widget.AbsListView.PositionScroller.1
                    @Override // java.lang.Runnable
                    public void run() {
                        PositionScroller.this.start(position);
                    }
                };
                return;
            }
            int childCount = AbsListView.this.getChildCount();
            if (childCount == 0) {
                return;
            }
            int firstPos = AbsListView.this.mFirstPosition;
            int lastPos = (firstPos + childCount) - 1;
            int clampedPosition = Math.max(0, Math.min(AbsListView.this.getCount() - 1, position));
            if (clampedPosition < firstPos) {
                viewTravelCount = (firstPos - clampedPosition) + 1;
                this.mMode = 2;
            } else if (clampedPosition > lastPos) {
                viewTravelCount = (clampedPosition - lastPos) + 1;
                this.mMode = 1;
            } else {
                scrollToVisible(clampedPosition, -1, 200);
                return;
            }
            if (viewTravelCount > 0) {
                this.mScrollDuration = 200 / viewTravelCount;
            } else {
                this.mScrollDuration = 200;
            }
            this.mTargetPos = clampedPosition;
            this.mBoundPos = -1;
            this.mLastSeenPos = -1;
            AbsListView.this.postOnAnimation(this);
        }

        void start(final int position, final int boundPosition) {
            int viewTravelCount;
            stop();
            if (boundPosition == -1) {
                start(position);
            } else if (AbsListView.this.mDataChanged) {
                AbsListView.this.mPositionScrollAfterLayout = new Runnable() { // from class: android.widget.AbsListView.PositionScroller.2
                    @Override // java.lang.Runnable
                    public void run() {
                        PositionScroller.this.start(position, boundPosition);
                    }
                };
            } else {
                int childCount = AbsListView.this.getChildCount();
                if (childCount == 0) {
                    return;
                }
                int firstPos = AbsListView.this.mFirstPosition;
                int lastPos = (firstPos + childCount) - 1;
                int clampedPosition = Math.max(0, Math.min(AbsListView.this.getCount() - 1, position));
                if (clampedPosition < firstPos) {
                    int boundPosFromLast = lastPos - boundPosition;
                    if (boundPosFromLast < 1) {
                        return;
                    }
                    int posTravel = (firstPos - clampedPosition) + 1;
                    int boundTravel = boundPosFromLast - 1;
                    if (boundTravel < posTravel) {
                        viewTravelCount = boundTravel;
                        this.mMode = 4;
                    } else {
                        viewTravelCount = posTravel;
                        this.mMode = 2;
                    }
                } else if (clampedPosition > lastPos) {
                    int boundPosFromFirst = boundPosition - firstPos;
                    if (boundPosFromFirst < 1) {
                        return;
                    }
                    int posTravel2 = (clampedPosition - lastPos) + 1;
                    int boundTravel2 = boundPosFromFirst - 1;
                    if (boundTravel2 < posTravel2) {
                        viewTravelCount = boundTravel2;
                        this.mMode = 3;
                    } else {
                        viewTravelCount = posTravel2;
                        this.mMode = 1;
                    }
                } else {
                    scrollToVisible(clampedPosition, boundPosition, 200);
                    return;
                }
                if (viewTravelCount > 0) {
                    this.mScrollDuration = 200 / viewTravelCount;
                } else {
                    this.mScrollDuration = 200;
                }
                this.mTargetPos = clampedPosition;
                this.mBoundPos = boundPosition;
                this.mLastSeenPos = -1;
                AbsListView.this.postOnAnimation(this);
            }
        }

        void startWithOffset(int position, int offset) {
            startWithOffset(position, offset, 200);
        }

        void startWithOffset(final int position, final int offset, final int duration) {
            int viewTravelCount;
            stop();
            if (AbsListView.this.mDataChanged) {
                AbsListView.this.mPositionScrollAfterLayout = new Runnable() { // from class: android.widget.AbsListView.PositionScroller.3
                    @Override // java.lang.Runnable
                    public void run() {
                        PositionScroller.this.startWithOffset(position, offset, duration);
                    }
                };
                return;
            }
            int childCount = AbsListView.this.getChildCount();
            if (childCount == 0) {
                return;
            }
            int offset2 = offset + AbsListView.this.getPaddingTop();
            this.mTargetPos = Math.max(0, Math.min(AbsListView.this.getCount() - 1, position));
            this.mOffsetFromTop = offset2;
            this.mBoundPos = -1;
            this.mLastSeenPos = -1;
            this.mMode = 5;
            int firstPos = AbsListView.this.mFirstPosition;
            int lastPos = (firstPos + childCount) - 1;
            if (this.mTargetPos < firstPos) {
                viewTravelCount = firstPos - this.mTargetPos;
            } else if (this.mTargetPos > lastPos) {
                viewTravelCount = this.mTargetPos - lastPos;
            } else {
                int targetTop = AbsListView.this.getChildAt(this.mTargetPos - firstPos).getTop();
                AbsListView.this.smoothScrollBy(targetTop - offset2, duration, true);
                return;
            }
            float screenTravelCount = viewTravelCount / childCount;
            this.mScrollDuration = screenTravelCount < 1.0f ? duration : (int) (duration / screenTravelCount);
            this.mLastSeenPos = -1;
            AbsListView.this.postOnAnimation(this);
        }

        void scrollToVisible(int targetPos, int boundPos, int duration) {
            int firstPos = AbsListView.this.mFirstPosition;
            int childCount = AbsListView.this.getChildCount();
            int lastPos = (firstPos + childCount) - 1;
            int paddedTop = AbsListView.this.mListPadding.top;
            int paddedBottom = AbsListView.this.getHeight() - AbsListView.this.mListPadding.bottom;
            if (targetPos < firstPos || targetPos > lastPos) {
                Log.w(AbsListView.TAG, "scrollToVisible called with targetPos " + targetPos + " not visible [" + firstPos + ", " + lastPos + "]");
            }
            if (boundPos < firstPos || boundPos > lastPos) {
                boundPos = -1;
            }
            View targetChild = AbsListView.this.getChildAt(targetPos - firstPos);
            int targetTop = targetChild.getTop();
            int targetBottom = targetChild.getBottom();
            int scrollBy = 0;
            if (targetBottom > paddedBottom) {
                scrollBy = targetBottom - paddedBottom;
            }
            if (targetTop < paddedTop) {
                scrollBy = targetTop - paddedTop;
            }
            if (scrollBy == 0) {
                return;
            }
            if (boundPos >= 0) {
                View boundChild = AbsListView.this.getChildAt(boundPos - firstPos);
                int boundTop = boundChild.getTop();
                int boundBottom = boundChild.getBottom();
                int absScroll = Math.abs(scrollBy);
                if (scrollBy < 0 && boundBottom + absScroll > paddedBottom) {
                    scrollBy = Math.max(0, boundBottom - paddedBottom);
                } else if (scrollBy > 0 && boundTop - absScroll < paddedTop) {
                    scrollBy = Math.min(0, boundTop - paddedTop);
                }
            }
            AbsListView.this.smoothScrollBy(scrollBy, duration);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void stop() {
            AbsListView.this.removeCallbacks(this);
        }

        @Override // java.lang.Runnable
        public void run() {
            int listHeight = AbsListView.this.getHeight();
            int firstPos = AbsListView.this.mFirstPosition;
            switch (this.mMode) {
                case 1:
                    int lastViewIndex = AbsListView.this.getChildCount() - 1;
                    int lastPos = firstPos + lastViewIndex;
                    if (lastViewIndex < 0) {
                        return;
                    }
                    if (lastPos == this.mLastSeenPos) {
                        AbsListView.this.postOnAnimation(this);
                        return;
                    }
                    View lastView = AbsListView.this.getChildAt(lastViewIndex);
                    int lastViewHeight = lastView.getHeight();
                    int lastViewPixelsShowing = listHeight - lastView.getTop();
                    int scrollBy = (lastViewHeight - lastViewPixelsShowing) + (lastPos < AbsListView.this.mItemCount - 1 ? Math.max(AbsListView.this.mListPadding.bottom, this.mExtraScroll) : AbsListView.this.mListPadding.bottom);
                    AbsListView.this.smoothScrollBy(scrollBy, this.mScrollDuration, true);
                    this.mLastSeenPos = lastPos;
                    if (lastPos < this.mTargetPos) {
                        AbsListView.this.postOnAnimation(this);
                        return;
                    }
                    return;
                case 2:
                    if (firstPos == this.mLastSeenPos) {
                        AbsListView.this.postOnAnimation(this);
                        return;
                    }
                    View firstView = AbsListView.this.getChildAt(0);
                    if (firstView == null) {
                        return;
                    }
                    int firstViewTop = firstView.getTop();
                    AbsListView.this.smoothScrollBy(firstViewTop - (firstPos > 0 ? Math.max(this.mExtraScroll, AbsListView.this.mListPadding.top) : AbsListView.this.mListPadding.top), this.mScrollDuration, true);
                    this.mLastSeenPos = firstPos;
                    if (firstPos > this.mTargetPos) {
                        AbsListView.this.postOnAnimation(this);
                        return;
                    }
                    return;
                case 3:
                    int childCount = AbsListView.this.getChildCount();
                    if (firstPos == this.mBoundPos || childCount <= 1 || firstPos + childCount >= AbsListView.this.mItemCount) {
                        return;
                    }
                    int nextPos = firstPos + 1;
                    if (nextPos == this.mLastSeenPos) {
                        AbsListView.this.postOnAnimation(this);
                        return;
                    }
                    View nextView = AbsListView.this.getChildAt(1);
                    int nextViewHeight = nextView.getHeight();
                    int nextViewTop = nextView.getTop();
                    int extraScroll = Math.max(AbsListView.this.mListPadding.bottom, this.mExtraScroll);
                    if (nextPos < this.mBoundPos) {
                        AbsListView.this.smoothScrollBy(Math.max(0, (nextViewHeight + nextViewTop) - extraScroll), this.mScrollDuration, true);
                        this.mLastSeenPos = nextPos;
                        AbsListView.this.postOnAnimation(this);
                        return;
                    } else if (nextViewTop > extraScroll) {
                        AbsListView.this.smoothScrollBy(nextViewTop - extraScroll, this.mScrollDuration, true);
                        return;
                    } else {
                        return;
                    }
                case 4:
                    int lastViewIndex2 = AbsListView.this.getChildCount() - 2;
                    if (lastViewIndex2 < 0) {
                        return;
                    }
                    int lastPos2 = firstPos + lastViewIndex2;
                    if (lastPos2 == this.mLastSeenPos) {
                        AbsListView.this.postOnAnimation(this);
                        return;
                    }
                    View lastView2 = AbsListView.this.getChildAt(lastViewIndex2);
                    int lastViewHeight2 = lastView2.getHeight();
                    int lastViewTop = lastView2.getTop();
                    int lastViewPixelsShowing2 = listHeight - lastViewTop;
                    int extraScroll2 = Math.max(AbsListView.this.mListPadding.top, this.mExtraScroll);
                    this.mLastSeenPos = lastPos2;
                    if (lastPos2 > this.mBoundPos) {
                        AbsListView.this.smoothScrollBy(-(lastViewPixelsShowing2 - extraScroll2), this.mScrollDuration, true);
                        AbsListView.this.postOnAnimation(this);
                        return;
                    }
                    int bottom = listHeight - extraScroll2;
                    int lastViewBottom = lastViewTop + lastViewHeight2;
                    if (bottom > lastViewBottom) {
                        AbsListView.this.smoothScrollBy(-(bottom - lastViewBottom), this.mScrollDuration, true);
                        return;
                    }
                    return;
                case 5:
                    if (this.mLastSeenPos == firstPos) {
                        AbsListView.this.postOnAnimation(this);
                        return;
                    }
                    this.mLastSeenPos = firstPos;
                    int childCount2 = AbsListView.this.getChildCount();
                    int position = this.mTargetPos;
                    int lastPos3 = (firstPos + childCount2) - 1;
                    int viewTravelCount = 0;
                    if (position < firstPos) {
                        viewTravelCount = (firstPos - position) + 1;
                    } else if (position > lastPos3) {
                        viewTravelCount = position - lastPos3;
                    }
                    float screenTravelCount = viewTravelCount / childCount2;
                    float modifier = Math.min(Math.abs(screenTravelCount), 1.0f);
                    if (position < firstPos) {
                        int distance = (int) ((-AbsListView.this.getHeight()) * modifier);
                        int duration = (int) (this.mScrollDuration * modifier);
                        AbsListView.this.smoothScrollBy(distance, duration, true);
                        AbsListView.this.postOnAnimation(this);
                        return;
                    } else if (position > lastPos3) {
                        int distance2 = (int) (AbsListView.this.getHeight() * modifier);
                        int duration2 = (int) (this.mScrollDuration * modifier);
                        AbsListView.this.smoothScrollBy(distance2, duration2, true);
                        AbsListView.this.postOnAnimation(this);
                        return;
                    } else {
                        int targetTop = AbsListView.this.getChildAt(position - firstPos).getTop();
                        int distance3 = targetTop - this.mOffsetFromTop;
                        int duration3 = (int) (this.mScrollDuration * (Math.abs(distance3) / AbsListView.this.getHeight()));
                        AbsListView.this.smoothScrollBy(distance3, duration3, true);
                        return;
                    }
                default:
                    return;
            }
        }
    }

    public void setFriction(float friction) {
        if (this.mFlingRunnable == null) {
            this.mFlingRunnable = new FlingRunnable();
        }
        this.mFlingRunnable.mScroller.setFriction(friction);
    }

    public void setVelocityScale(float scale) {
        this.mVelocityScale = scale;
    }

    public void smoothScrollToPosition(int position) {
        if (this.mPositionScroller == null) {
            this.mPositionScroller = new PositionScroller();
        }
        this.mPositionScroller.start(position);
    }

    public void smoothScrollToPositionFromTop(int position, int offset, int duration) {
        if (this.mPositionScroller == null) {
            this.mPositionScroller = new PositionScroller();
        }
        this.mPositionScroller.startWithOffset(position, offset, duration);
    }

    public void smoothScrollToPositionFromTop(int position, int offset) {
        if (this.mPositionScroller == null) {
            this.mPositionScroller = new PositionScroller();
        }
        this.mPositionScroller.startWithOffset(position, offset);
    }

    public void smoothScrollToPosition(int position, int boundPosition) {
        if (this.mPositionScroller == null) {
            this.mPositionScroller = new PositionScroller();
        }
        this.mPositionScroller.start(position, boundPosition);
    }

    public void smoothScrollBy(int distance, int duration) {
        smoothScrollBy(distance, duration, false);
    }

    void smoothScrollBy(int distance, int duration, boolean linear) {
        if (this.mFlingRunnable == null) {
            this.mFlingRunnable = new FlingRunnable();
        }
        int firstPos = this.mFirstPosition;
        int childCount = getChildCount();
        int lastPos = firstPos + childCount;
        int topLimit = getPaddingTop();
        int bottomLimit = getHeight() - getPaddingBottom();
        if (distance == 0 || this.mItemCount == 0 || childCount == 0 || ((firstPos == 0 && getChildAt(0).getTop() == topLimit && distance < 0) || (lastPos == this.mItemCount && getChildAt(childCount - 1).getBottom() == bottomLimit && distance > 0))) {
            this.mFlingRunnable.endFling();
            if (this.mPositionScroller != null) {
                this.mPositionScroller.stop();
                return;
            }
            return;
        }
        reportScrollStateChange(2);
        this.mFlingRunnable.startScroll(distance, duration, linear);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void smoothScrollByOffset(int position) {
        View child;
        int index = -1;
        if (position < 0) {
            index = getFirstVisiblePosition();
        } else if (position > 0) {
            index = getLastVisiblePosition();
        }
        if (index > -1 && (child = getChildAt(index - getFirstVisiblePosition())) != null) {
            Rect visibleRect = new Rect();
            if (child.getGlobalVisibleRect(visibleRect)) {
                int childRectArea = child.getWidth() * child.getHeight();
                int visibleRectArea = visibleRect.width() * visibleRect.height();
                float visibleArea = visibleRectArea / childRectArea;
                if (position < 0 && visibleArea < 0.75f) {
                    index++;
                } else if (position > 0 && visibleArea < 0.75f) {
                    index--;
                }
            }
            smoothScrollToPosition(Math.max(0, Math.min(getCount(), index + position)));
        }
    }

    private void createScrollingCache() {
        if (this.mScrollingCacheEnabled && !this.mCachingStarted && !isHardwareAccelerated()) {
            setChildrenDrawnWithCacheEnabled(true);
            setChildrenDrawingCacheEnabled(true);
            this.mCachingActive = true;
            this.mCachingStarted = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearScrollingCache() {
        if (!isHardwareAccelerated()) {
            if (this.mClearScrollingCache == null) {
                this.mClearScrollingCache = new Runnable() { // from class: android.widget.AbsListView.4
                    @Override // java.lang.Runnable
                    public void run() {
                        if (AbsListView.this.mCachingStarted) {
                            AbsListView absListView = AbsListView.this;
                            AbsListView.this.mCachingActive = false;
                            absListView.mCachingStarted = false;
                            AbsListView.this.setChildrenDrawnWithCacheEnabled(false);
                            if ((AbsListView.this.mPersistentDrawingCache & 2) == 0) {
                                AbsListView.this.setChildrenDrawingCacheEnabled(false);
                            }
                            if (!AbsListView.this.isAlwaysDrawnWithCacheEnabled()) {
                                AbsListView.this.invalidate();
                            }
                        }
                    }
                };
            }
            post(this.mClearScrollingCache);
        }
    }

    public void scrollListBy(int y) {
        trackMotionScroll(-y, -y);
    }

    public boolean canScrollList(int direction) {
        int childCount = getChildCount();
        if (childCount == 0) {
            return false;
        }
        int firstPosition = this.mFirstPosition;
        Rect listPadding = this.mListPadding;
        if (direction > 0) {
            int lastBottom = getChildAt(childCount - 1).getBottom();
            int lastPosition = firstPosition + childCount;
            return lastPosition < this.mItemCount || lastBottom > getHeight() - listPadding.bottom;
        }
        int firstTop = getChildAt(0).getTop();
        return firstPosition > 0 || firstTop < listPadding.top;
    }

    boolean trackMotionScroll(int deltaY, int incrementalDeltaY) {
        int deltaY2;
        int incrementalDeltaY2;
        int childCount = getChildCount();
        if (childCount == 0) {
            return true;
        }
        int firstTop = getChildAt(0).getTop();
        int lastBottom = getChildAt(childCount - 1).getBottom();
        Rect listPadding = this.mListPadding;
        int effectivePaddingTop = 0;
        int effectivePaddingBottom = 0;
        if ((this.mGroupFlags & 34) == 34) {
            effectivePaddingTop = listPadding.top;
            effectivePaddingBottom = listPadding.bottom;
        }
        int spaceAbove = effectivePaddingTop - firstTop;
        int end = getHeight() - effectivePaddingBottom;
        int spaceBelow = lastBottom - end;
        int height = (getHeight() - this.mPaddingBottom) - this.mPaddingTop;
        if (deltaY < 0) {
            deltaY2 = Math.max(-(height - 1), deltaY);
        } else {
            deltaY2 = Math.min(height - 1, deltaY);
        }
        if (incrementalDeltaY < 0) {
            incrementalDeltaY2 = Math.max(-(height - 1), incrementalDeltaY);
        } else {
            incrementalDeltaY2 = Math.min(height - 1, incrementalDeltaY);
        }
        int firstPosition = this.mFirstPosition;
        if (firstPosition == 0) {
            this.mFirstPositionDistanceGuess = firstTop - listPadding.top;
        } else {
            this.mFirstPositionDistanceGuess += incrementalDeltaY2;
        }
        if (firstPosition + childCount == this.mItemCount) {
            this.mLastPositionDistanceGuess = lastBottom + listPadding.bottom;
        } else {
            this.mLastPositionDistanceGuess += incrementalDeltaY2;
        }
        boolean cannotScrollDown = firstPosition == 0 && firstTop >= listPadding.top && incrementalDeltaY2 >= 0;
        boolean cannotScrollUp = firstPosition + childCount == this.mItemCount && lastBottom <= getHeight() - listPadding.bottom && incrementalDeltaY2 <= 0;
        if (cannotScrollDown || cannotScrollUp) {
            return incrementalDeltaY2 != 0;
        }
        boolean down = incrementalDeltaY2 < 0;
        boolean inTouchMode = isInTouchMode();
        if (inTouchMode) {
            hideSelector();
        }
        int headerViewsCount = getHeaderViewsCount();
        int footerViewsStart = this.mItemCount - getFooterViewsCount();
        int start = 0;
        int count = 0;
        if (down) {
            int top = -incrementalDeltaY2;
            if ((this.mGroupFlags & 34) == 34) {
                top += listPadding.top;
            }
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                if (child.getBottom() >= top) {
                    break;
                }
                count++;
                int position = firstPosition + i;
                if (position >= headerViewsCount && position < footerViewsStart) {
                    if (child.isAccessibilityFocused()) {
                        child.clearAccessibilityFocus();
                    }
                    this.mRecycler.addScrapView(child, position);
                }
            }
        } else {
            int bottom = getHeight() - incrementalDeltaY2;
            if ((this.mGroupFlags & 34) == 34) {
                bottom -= listPadding.bottom;
            }
            for (int i2 = childCount - 1; i2 >= 0; i2--) {
                View child2 = getChildAt(i2);
                if (child2.getTop() <= bottom) {
                    break;
                }
                start = i2;
                count++;
                int position2 = firstPosition + i2;
                if (position2 >= headerViewsCount && position2 < footerViewsStart) {
                    if (child2.isAccessibilityFocused()) {
                        child2.clearAccessibilityFocus();
                    }
                    this.mRecycler.addScrapView(child2, position2);
                }
            }
        }
        this.mMotionViewNewTop = this.mMotionViewOriginalTop + deltaY2;
        this.mBlockLayoutRequests = true;
        if (count > 0) {
            detachViewsFromParent(start, count);
            this.mRecycler.removeSkippedScrap();
        }
        if (!awakenScrollBars()) {
            invalidate();
        }
        offsetChildrenTopAndBottom(incrementalDeltaY2);
        if (down) {
            this.mFirstPosition += count;
        }
        int absIncrementalDeltaY = Math.abs(incrementalDeltaY2);
        if (spaceAbove < absIncrementalDeltaY || spaceBelow < absIncrementalDeltaY) {
            fillGap(down);
        }
        if (!inTouchMode && this.mSelectedPosition != -1) {
            int childIndex = this.mSelectedPosition - this.mFirstPosition;
            if (childIndex >= 0 && childIndex < getChildCount()) {
                positionSelector(this.mSelectedPosition, getChildAt(childIndex));
            }
        } else if (this.mSelectorPosition != -1) {
            int childIndex2 = this.mSelectorPosition - this.mFirstPosition;
            if (childIndex2 >= 0 && childIndex2 < getChildCount()) {
                positionSelector(-1, getChildAt(childIndex2));
            }
        } else {
            this.mSelectorRect.setEmpty();
        }
        this.mBlockLayoutRequests = false;
        invokeOnItemScrollListener();
        return false;
    }

    int getHeaderViewsCount() {
        return 0;
    }

    int getFooterViewsCount() {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void hideSelector() {
        if (this.mSelectedPosition != -1) {
            if (this.mLayoutMode != 4) {
                this.mResurrectToPosition = this.mSelectedPosition;
            }
            if (this.mNextSelectedPosition >= 0 && this.mNextSelectedPosition != this.mSelectedPosition) {
                this.mResurrectToPosition = this.mNextSelectedPosition;
            }
            setSelectedPositionInt(-1);
            setNextSelectedPositionInt(-1);
            this.mSelectedTop = 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int reconcileSelectedPosition() {
        int position = this.mSelectedPosition;
        if (position < 0) {
            position = this.mResurrectToPosition;
        }
        return Math.min(Math.max(0, position), this.mItemCount - 1);
    }

    int findClosestMotionRow(int y) {
        int childCount = getChildCount();
        if (childCount == 0) {
            return -1;
        }
        int motionRow = findMotionRow(y);
        return motionRow != -1 ? motionRow : (this.mFirstPosition + childCount) - 1;
    }

    public void invalidateViews() {
        this.mDataChanged = true;
        rememberSyncState();
        requestLayout();
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean resurrectSelectionIfNeeded() {
        if (this.mSelectedPosition < 0 && resurrectSelection()) {
            updateSelectorState();
            return true;
        }
        return false;
    }

    boolean resurrectSelection() {
        int selectedPos;
        int childCount = getChildCount();
        if (childCount <= 0) {
            return false;
        }
        int selectedTop = 0;
        int childrenTop = this.mListPadding.top;
        int childrenBottom = (this.mBottom - this.mTop) - this.mListPadding.bottom;
        int firstPosition = this.mFirstPosition;
        int toPosition = this.mResurrectToPosition;
        boolean down = true;
        if (toPosition >= firstPosition && toPosition < firstPosition + childCount) {
            selectedPos = toPosition;
            View selected = getChildAt(selectedPos - this.mFirstPosition);
            selectedTop = selected.getTop();
            int selectedBottom = selected.getBottom();
            if (selectedTop < childrenTop) {
                selectedTop = childrenTop + getVerticalFadingEdgeLength();
            } else if (selectedBottom > childrenBottom) {
                selectedTop = (childrenBottom - selected.getMeasuredHeight()) - getVerticalFadingEdgeLength();
            }
        } else if (toPosition < firstPosition) {
            selectedPos = firstPosition;
            int i = 0;
            while (true) {
                if (i >= childCount) {
                    break;
                }
                int top = getChildAt(i).getTop();
                if (i == 0) {
                    selectedTop = top;
                    if (firstPosition > 0 || top < childrenTop) {
                        childrenTop += getVerticalFadingEdgeLength();
                    }
                }
                if (top < childrenTop) {
                    i++;
                } else {
                    selectedPos = firstPosition + i;
                    selectedTop = top;
                    break;
                }
            }
        } else {
            int itemCount = this.mItemCount;
            down = false;
            selectedPos = (firstPosition + childCount) - 1;
            int i2 = childCount - 1;
            while (true) {
                if (i2 < 0) {
                    break;
                }
                View v = getChildAt(i2);
                int top2 = v.getTop();
                int bottom = v.getBottom();
                if (i2 == childCount - 1) {
                    selectedTop = top2;
                    if (firstPosition + childCount < itemCount || bottom > childrenBottom) {
                        childrenBottom -= getVerticalFadingEdgeLength();
                    }
                }
                if (bottom > childrenBottom) {
                    i2--;
                } else {
                    selectedPos = firstPosition + i2;
                    selectedTop = top2;
                    break;
                }
            }
        }
        this.mResurrectToPosition = -1;
        removeCallbacks(this.mFlingRunnable);
        if (this.mPositionScroller != null) {
            this.mPositionScroller.stop();
        }
        this.mTouchMode = -1;
        clearScrollingCache();
        this.mSpecificTop = selectedTop;
        int selectedPos2 = lookForSelectablePosition(selectedPos, down);
        if (selectedPos2 >= firstPosition && selectedPos2 <= getLastVisiblePosition()) {
            this.mLayoutMode = 4;
            updateSelectorState();
            setSelectionInt(selectedPos2);
            invokeOnItemScrollListener();
        } else {
            selectedPos2 = -1;
        }
        reportScrollStateChange(0);
        return selectedPos2 >= 0;
    }

    void confirmCheckedPositionsById() {
        this.mCheckStates.clear();
        boolean checkedCountChanged = false;
        int checkedIndex = 0;
        while (checkedIndex < this.mCheckedIdStates.size()) {
            long id = this.mCheckedIdStates.keyAt(checkedIndex);
            int lastPos = this.mCheckedIdStates.valueAt(checkedIndex).intValue();
            long lastPosId = this.mAdapter.getItemId(lastPos);
            if (id != lastPosId) {
                int start = Math.max(0, lastPos - 20);
                int end = Math.min(lastPos + 20, this.mItemCount);
                boolean found = false;
                int searchPos = start;
                while (true) {
                    if (searchPos >= end) {
                        break;
                    }
                    long searchId = this.mAdapter.getItemId(searchPos);
                    if (id != searchId) {
                        searchPos++;
                    } else {
                        found = true;
                        this.mCheckStates.put(searchPos, true);
                        this.mCheckedIdStates.setValueAt(checkedIndex, Integer.valueOf(searchPos));
                        break;
                    }
                }
                if (!found) {
                    this.mCheckedIdStates.delete(id);
                    checkedIndex--;
                    this.mCheckedItemCount--;
                    checkedCountChanged = true;
                    if (this.mChoiceActionMode != null && this.mMultiChoiceModeCallback != null) {
                        this.mMultiChoiceModeCallback.onItemCheckedStateChanged(this.mChoiceActionMode, lastPos, id, false);
                    }
                }
            } else {
                this.mCheckStates.put(lastPos, true);
            }
            checkedIndex++;
        }
        if (checkedCountChanged && this.mChoiceActionMode != null) {
            this.mChoiceActionMode.invalidate();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.AdapterView
    public void handleDataChanged() {
        int count = this.mItemCount;
        int lastHandledItemCount = this.mLastHandledItemCount;
        this.mLastHandledItemCount = this.mItemCount;
        if (this.mChoiceMode != 0 && this.mAdapter != null && this.mAdapter.hasStableIds()) {
            confirmCheckedPositionsById();
        }
        this.mRecycler.clearTransientStateViews();
        if (count > 0) {
            if (this.mNeedSync) {
                this.mNeedSync = false;
                this.mPendingSync = null;
                if (this.mTranscriptMode == 2) {
                    this.mLayoutMode = 3;
                    return;
                }
                if (this.mTranscriptMode == 1) {
                    if (this.mForceTranscriptScroll) {
                        this.mForceTranscriptScroll = false;
                        this.mLayoutMode = 3;
                        return;
                    }
                    int childCount = getChildCount();
                    int listBottom = getHeight() - getPaddingBottom();
                    View lastChild = getChildAt(childCount - 1);
                    int lastBottom = lastChild != null ? lastChild.getBottom() : listBottom;
                    if (this.mFirstPosition + childCount >= lastHandledItemCount && lastBottom <= listBottom) {
                        this.mLayoutMode = 3;
                        return;
                    }
                    awakenScrollBars();
                }
                switch (this.mSyncMode) {
                    case 1:
                        this.mLayoutMode = 5;
                        this.mSyncPosition = Math.min(Math.max(0, this.mSyncPosition), count - 1);
                        return;
                    case 0:
                        if (isInTouchMode()) {
                            this.mLayoutMode = 5;
                            this.mSyncPosition = Math.min(Math.max(0, this.mSyncPosition), count - 1);
                            return;
                        }
                        int newPos = findSyncPosition();
                        if (newPos >= 0 && lookForSelectablePosition(newPos, true) == newPos) {
                            this.mSyncPosition = newPos;
                            if (this.mSyncHeight == getHeight()) {
                                this.mLayoutMode = 5;
                            } else {
                                this.mLayoutMode = 2;
                            }
                            setNextSelectedPositionInt(newPos);
                            return;
                        }
                        break;
                }
            }
            if (!isInTouchMode()) {
                int newPos2 = getSelectedItemPosition();
                if (newPos2 >= count) {
                    newPos2 = count - 1;
                }
                if (newPos2 < 0) {
                    newPos2 = 0;
                }
                int selectablePos = lookForSelectablePosition(newPos2, true);
                if (selectablePos >= 0) {
                    setNextSelectedPositionInt(selectablePos);
                    return;
                }
                int selectablePos2 = lookForSelectablePosition(newPos2, false);
                if (selectablePos2 >= 0) {
                    setNextSelectedPositionInt(selectablePos2);
                    return;
                }
            } else if (this.mResurrectToPosition >= 0) {
                return;
            }
        }
        this.mLayoutMode = this.mStackFromBottom ? 3 : 1;
        this.mSelectedPosition = -1;
        this.mSelectedRowId = Long.MIN_VALUE;
        this.mNextSelectedPosition = -1;
        this.mNextSelectedRowId = Long.MIN_VALUE;
        this.mNeedSync = false;
        this.mPendingSync = null;
        this.mSelectorPosition = -1;
        checkSelectionChanged();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onDisplayHint(int hint) {
        super.onDisplayHint(hint);
        switch (hint) {
            case 0:
                if (this.mFiltered && this.mPopup != null && !this.mPopup.isShowing()) {
                    showPopup();
                    break;
                }
                break;
            case 4:
                if (this.mPopup != null && this.mPopup.isShowing()) {
                    dismissPopup();
                    break;
                }
                break;
        }
        this.mPopupHidden = hint == 4;
    }

    private void dismissPopup() {
        if (this.mPopup != null) {
            this.mPopup.dismiss();
        }
    }

    private void showPopup() {
        if (getWindowVisibility() == 0) {
            createTextFilter(true);
            positionPopup();
            checkFocus();
        }
    }

    private void positionPopup() {
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        int[] xy = new int[2];
        getLocationOnScreen(xy);
        int bottomGap = ((screenHeight - xy[1]) - getHeight()) + ((int) (this.mDensityScale * 20.0f));
        if (!this.mPopup.isShowing()) {
            this.mPopup.showAtLocation(this, 81, xy[0], bottomGap);
        } else {
            this.mPopup.update(xy[0], bottomGap, -1, -1);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getDistance(Rect source, Rect dest, int direction) {
        int sX;
        int sY;
        int dX;
        int dY;
        switch (direction) {
            case 1:
            case 2:
                sX = source.right + (source.width() / 2);
                sY = source.top + (source.height() / 2);
                dX = dest.left + (dest.width() / 2);
                dY = dest.top + (dest.height() / 2);
                break;
            case 17:
                sX = source.left;
                sY = source.top + (source.height() / 2);
                dX = dest.right;
                dY = dest.top + (dest.height() / 2);
                break;
            case 33:
                sX = source.left + (source.width() / 2);
                sY = source.top;
                dX = dest.left + (dest.width() / 2);
                dY = dest.bottom;
                break;
            case 66:
                sX = source.right;
                sY = source.top + (source.height() / 2);
                dX = dest.left;
                dY = dest.top + (dest.height() / 2);
                break;
            case 130:
                sX = source.left + (source.width() / 2);
                sY = source.bottom;
                dX = dest.left + (dest.width() / 2);
                dY = dest.top;
                break;
            default:
                throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT, FOCUS_FORWARD, FOCUS_BACKWARD}.");
        }
        int deltaX = dX - sX;
        int deltaY = dY - sY;
        return (deltaY * deltaY) + (deltaX * deltaX);
    }

    @Override // android.widget.AdapterView
    protected boolean isInFilterMode() {
        return this.mFiltered;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean sendToTextFilter(int keyCode, int count, KeyEvent event) {
        if (!acceptFilter()) {
            return false;
        }
        boolean handled = false;
        boolean okToSend = true;
        switch (keyCode) {
            case 4:
                if (this.mFiltered && this.mPopup != null && this.mPopup.isShowing()) {
                    if (event.getAction() == 0 && event.getRepeatCount() == 0) {
                        KeyEvent.DispatcherState state = getKeyDispatcherState();
                        if (state != null) {
                            state.startTracking(event, this);
                        }
                        handled = true;
                    } else if (event.getAction() == 1 && event.isTracking() && !event.isCanceled()) {
                        handled = true;
                        this.mTextFilter.setText("");
                    }
                }
                okToSend = false;
                break;
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 66:
                okToSend = false;
                break;
            case 62:
                okToSend = this.mFiltered;
                break;
        }
        if (okToSend) {
            createTextFilter(true);
            KeyEvent forwardEvent = event;
            if (forwardEvent.getRepeatCount() > 0) {
                forwardEvent = KeyEvent.changeTimeRepeat(event, event.getEventTime(), 0);
            }
            int action = event.getAction();
            switch (action) {
                case 0:
                    handled = this.mTextFilter.onKeyDown(keyCode, forwardEvent);
                    break;
                case 1:
                    handled = this.mTextFilter.onKeyUp(keyCode, forwardEvent);
                    break;
                case 2:
                    handled = this.mTextFilter.onKeyMultiple(keyCode, count, event);
                    break;
            }
        }
        return handled;
    }

    @Override // android.view.View
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        if (isTextFilterEnabled()) {
            if (this.mPublicInputConnection == null) {
                this.mDefInputConnection = new BaseInputConnection((View) this, false);
                this.mPublicInputConnection = new InputConnectionWrapper(outAttrs);
            }
            outAttrs.inputType = 177;
            outAttrs.imeOptions = 6;
            return this.mPublicInputConnection;
        }
        return null;
    }

    /* loaded from: AbsListView$InputConnectionWrapper.class */
    private class InputConnectionWrapper implements InputConnection {
        private final EditorInfo mOutAttrs;
        private InputConnection mTarget;

        public InputConnectionWrapper(EditorInfo outAttrs) {
            this.mOutAttrs = outAttrs;
        }

        private InputConnection getTarget() {
            if (this.mTarget == null) {
                this.mTarget = AbsListView.this.getTextFilterInput().onCreateInputConnection(this.mOutAttrs);
            }
            return this.mTarget;
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean reportFullscreenMode(boolean enabled) {
            return AbsListView.this.mDefInputConnection.reportFullscreenMode(enabled);
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean performEditorAction(int editorAction) {
            if (editorAction == 6) {
                InputMethodManager imm = (InputMethodManager) AbsListView.this.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(AbsListView.this.getWindowToken(), 0);
                    return true;
                }
                return true;
            }
            return false;
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean sendKeyEvent(KeyEvent event) {
            return AbsListView.this.mDefInputConnection.sendKeyEvent(event);
        }

        @Override // android.view.inputmethod.InputConnection
        public CharSequence getTextBeforeCursor(int n, int flags) {
            return this.mTarget == null ? "" : this.mTarget.getTextBeforeCursor(n, flags);
        }

        @Override // android.view.inputmethod.InputConnection
        public CharSequence getTextAfterCursor(int n, int flags) {
            return this.mTarget == null ? "" : this.mTarget.getTextAfterCursor(n, flags);
        }

        @Override // android.view.inputmethod.InputConnection
        public CharSequence getSelectedText(int flags) {
            return this.mTarget == null ? "" : this.mTarget.getSelectedText(flags);
        }

        @Override // android.view.inputmethod.InputConnection
        public int getCursorCapsMode(int reqModes) {
            if (this.mTarget == null) {
                return 16384;
            }
            return this.mTarget.getCursorCapsMode(reqModes);
        }

        @Override // android.view.inputmethod.InputConnection
        public ExtractedText getExtractedText(ExtractedTextRequest request, int flags) {
            return getTarget().getExtractedText(request, flags);
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean deleteSurroundingText(int beforeLength, int afterLength) {
            return getTarget().deleteSurroundingText(beforeLength, afterLength);
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean setComposingText(CharSequence text, int newCursorPosition) {
            return getTarget().setComposingText(text, newCursorPosition);
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean setComposingRegion(int start, int end) {
            return getTarget().setComposingRegion(start, end);
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean finishComposingText() {
            return this.mTarget == null || this.mTarget.finishComposingText();
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean commitText(CharSequence text, int newCursorPosition) {
            return getTarget().commitText(text, newCursorPosition);
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean commitCompletion(CompletionInfo text) {
            return getTarget().commitCompletion(text);
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean commitCorrection(CorrectionInfo correctionInfo) {
            return getTarget().commitCorrection(correctionInfo);
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean setSelection(int start, int end) {
            return getTarget().setSelection(start, end);
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean performContextMenuAction(int id) {
            return getTarget().performContextMenuAction(id);
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean beginBatchEdit() {
            return getTarget().beginBatchEdit();
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean endBatchEdit() {
            return getTarget().endBatchEdit();
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean clearMetaKeyStates(int states) {
            return getTarget().clearMetaKeyStates(states);
        }

        @Override // android.view.inputmethod.InputConnection
        public boolean performPrivateCommand(String action, Bundle data) {
            return getTarget().performPrivateCommand(action, data);
        }
    }

    @Override // android.view.View
    public boolean checkInputConnectionProxy(View view) {
        return view == this.mTextFilter;
    }

    private void createTextFilter(boolean animateEntrance) {
        if (this.mPopup == null) {
            PopupWindow p = new PopupWindow(getContext());
            p.setFocusable(false);
            p.setTouchable(false);
            p.setInputMethodMode(2);
            p.setContentView(getTextFilterInput());
            p.setWidth(-2);
            p.setHeight(-2);
            p.setBackgroundDrawable(null);
            this.mPopup = p;
            getViewTreeObserver().addOnGlobalLayoutListener(this);
            this.mGlobalLayoutListenerAddedFilter = true;
        }
        if (animateEntrance) {
            this.mPopup.setAnimationStyle(R.style.Animation_TypingFilter);
        } else {
            this.mPopup.setAnimationStyle(R.style.Animation_TypingFilterRestore);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public EditText getTextFilterInput() {
        if (this.mTextFilter == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            this.mTextFilter = (EditText) layoutInflater.inflate(R.layout.typing_filter, (ViewGroup) null);
            this.mTextFilter.setRawInputType(177);
            this.mTextFilter.setImeOptions(268435456);
            this.mTextFilter.addTextChangedListener(this);
        }
        return this.mTextFilter;
    }

    public void clearTextFilter() {
        if (this.mFiltered) {
            getTextFilterInput().setText("");
            this.mFiltered = false;
            if (this.mPopup != null && this.mPopup.isShowing()) {
                dismissPopup();
            }
        }
    }

    public boolean hasTextFilter() {
        return this.mFiltered;
    }

    @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
    public void onGlobalLayout() {
        if (isShown()) {
            if (this.mFiltered && this.mPopup != null && !this.mPopup.isShowing() && !this.mPopupHidden) {
                showPopup();
            }
        } else if (this.mPopup != null && this.mPopup.isShowing()) {
            dismissPopup();
        }
    }

    @Override // android.text.TextWatcher
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override // android.text.TextWatcher
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (isTextFilterEnabled()) {
            createTextFilter(true);
            int length = s.length();
            boolean showing = this.mPopup.isShowing();
            if (!showing && length > 0) {
                showPopup();
                this.mFiltered = true;
            } else if (showing && length == 0) {
                dismissPopup();
                this.mFiltered = false;
            }
            if (this.mAdapter instanceof Filterable) {
                Filter f = ((Filterable) this.mAdapter).getFilter();
                if (f != null) {
                    f.filter(s, this);
                    return;
                }
                throw new IllegalStateException("You cannot call onTextChanged with a non filterable adapter");
            }
        }
    }

    @Override // android.text.TextWatcher
    public void afterTextChanged(Editable s) {
    }

    @Override // android.widget.Filter.FilterListener
    public void onFilterComplete(int count) {
        if (this.mSelectedPosition < 0 && count > 0) {
            this.mResurrectToPosition = -1;
            resurrectSelection();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-1, -2, 0);
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override // android.view.ViewGroup
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    public void setTranscriptMode(int mode) {
        this.mTranscriptMode = mode;
    }

    public int getTranscriptMode() {
        return this.mTranscriptMode;
    }

    @Override // android.view.View
    public int getSolidColor() {
        return this.mCacheColorHint;
    }

    public void setCacheColorHint(int color) {
        if (color != this.mCacheColorHint) {
            this.mCacheColorHint = color;
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                getChildAt(i).setDrawingCacheBackgroundColor(color);
            }
            this.mRecycler.setCacheColorHint(color);
        }
    }

    @ViewDebug.ExportedProperty(category = "drawing")
    public int getCacheColorHint() {
        return this.mCacheColorHint;
    }

    public void reclaimViews(List<View> views) {
        int childCount = getChildCount();
        RecyclerListener listener = this.mRecycler.mRecyclerListener;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (lp != null && this.mRecycler.shouldRecycleViewType(lp.viewType)) {
                views.add(child);
                child.setAccessibilityDelegate(null);
                if (listener != null) {
                    listener.onMovedToScrapHeap(child);
                }
            }
        }
        this.mRecycler.reclaimScrapViews(views);
        removeAllViewsInLayout();
    }

    private void finishGlows() {
        if (this.mEdgeGlowTop != null) {
            this.mEdgeGlowTop.finish();
            this.mEdgeGlowBottom.finish();
        }
    }

    public void setRemoteViewsAdapter(Intent intent) {
        if (this.mRemoteAdapter != null) {
            Intent.FilterComparison fcNew = new Intent.FilterComparison(intent);
            Intent.FilterComparison fcOld = new Intent.FilterComparison(this.mRemoteAdapter.getRemoteViewsServiceIntent());
            if (fcNew.equals(fcOld)) {
                return;
            }
        }
        this.mDeferNotifyDataSetChanged = false;
        this.mRemoteAdapter = new RemoteViewsAdapter(getContext(), intent, this);
        if (this.mRemoteAdapter.isDataReady()) {
            setAdapter((ListAdapter) this.mRemoteAdapter);
        }
    }

    public void setRemoteViewsOnClickHandler(RemoteViews.OnClickHandler handler) {
        if (this.mRemoteAdapter != null) {
            this.mRemoteAdapter.setRemoteViewsOnClickHandler(handler);
        }
    }

    @Override // android.widget.RemoteViewsAdapter.RemoteAdapterConnectionCallback
    public void deferNotifyDataSetChanged() {
        this.mDeferNotifyDataSetChanged = true;
    }

    @Override // android.widget.RemoteViewsAdapter.RemoteAdapterConnectionCallback
    public boolean onRemoteAdapterConnected() {
        if (this.mRemoteAdapter != this.mAdapter) {
            setAdapter((ListAdapter) this.mRemoteAdapter);
            if (this.mDeferNotifyDataSetChanged) {
                this.mRemoteAdapter.notifyDataSetChanged();
                this.mDeferNotifyDataSetChanged = false;
                return false;
            }
            return false;
        } else if (this.mRemoteAdapter != null) {
            this.mRemoteAdapter.superNotifyDataSetChanged();
            return true;
        } else {
            return false;
        }
    }

    @Override // android.widget.RemoteViewsAdapter.RemoteAdapterConnectionCallback
    public void onRemoteAdapterDisconnected() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setVisibleRangeHint(int start, int end) {
        if (this.mRemoteAdapter != null) {
            this.mRemoteAdapter.setVisibleRangeHint(start, end);
        }
    }

    public void setRecyclerListener(RecyclerListener listener) {
        this.mRecycler.mRecyclerListener = listener;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: AbsListView$AdapterDataSetObserver.class */
    public class AdapterDataSetObserver extends AdapterView.AdapterDataSetObserver {
        /* JADX INFO: Access modifiers changed from: package-private */
        public AdapterDataSetObserver() {
            super();
        }

        @Override // android.widget.AdapterView.AdapterDataSetObserver, android.database.DataSetObserver
        public void onChanged() {
            super.onChanged();
            if (AbsListView.this.mFastScroller != null) {
                AbsListView.this.mFastScroller.onSectionsChanged();
            }
        }

        @Override // android.widget.AdapterView.AdapterDataSetObserver, android.database.DataSetObserver
        public void onInvalidated() {
            super.onInvalidated();
            if (AbsListView.this.mFastScroller != null) {
                AbsListView.this.mFastScroller.onSectionsChanged();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: AbsListView$MultiChoiceModeWrapper.class */
    public class MultiChoiceModeWrapper implements MultiChoiceModeListener {
        private MultiChoiceModeListener mWrapped;

        MultiChoiceModeWrapper() {
        }

        public void setWrapped(MultiChoiceModeListener wrapped) {
            this.mWrapped = wrapped;
        }

        public boolean hasWrappedCallback() {
            return this.mWrapped != null;
        }

        @Override // android.view.ActionMode.Callback
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            if (this.mWrapped.onCreateActionMode(mode, menu)) {
                AbsListView.this.setLongClickable(false);
                return true;
            }
            return false;
        }

        @Override // android.view.ActionMode.Callback
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return this.mWrapped.onPrepareActionMode(mode, menu);
        }

        @Override // android.view.ActionMode.Callback
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return this.mWrapped.onActionItemClicked(mode, item);
        }

        @Override // android.view.ActionMode.Callback
        public void onDestroyActionMode(ActionMode mode) {
            this.mWrapped.onDestroyActionMode(mode);
            AbsListView.this.mChoiceActionMode = null;
            AbsListView.this.clearChoices();
            AbsListView.this.mDataChanged = true;
            AbsListView.this.rememberSyncState();
            AbsListView.this.requestLayout();
            AbsListView.this.setLongClickable(true);
        }

        @Override // android.widget.AbsListView.MultiChoiceModeListener
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            this.mWrapped.onItemCheckedStateChanged(mode, position, id, checked);
            if (AbsListView.this.getCheckedItemCount() == 0) {
                mode.finish();
            }
        }
    }

    /* loaded from: AbsListView$LayoutParams.class */
    public static class LayoutParams extends ViewGroup.LayoutParams {
        @ViewDebug.ExportedProperty(category = "list", mapping = {@ViewDebug.IntToString(from = -1, to = "ITEM_VIEW_TYPE_IGNORE"), @ViewDebug.IntToString(from = -2, to = "ITEM_VIEW_TYPE_HEADER_OR_FOOTER")})
        int viewType;
        @ViewDebug.ExportedProperty(category = "list")
        boolean recycledHeaderFooter;
        @ViewDebug.ExportedProperty(category = "list")
        boolean forceAdd;
        int scrappedFromPosition;
        long itemId;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            this.itemId = -1L;
        }

        public LayoutParams(int w, int h) {
            super(w, h);
            this.itemId = -1L;
        }

        public LayoutParams(int w, int h, int viewType) {
            super(w, h);
            this.itemId = -1L;
            this.viewType = viewType;
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
            this.itemId = -1L;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: AbsListView$RecycleBin.class */
    public class RecycleBin {
        private RecyclerListener mRecyclerListener;
        private int mFirstActivePosition;
        private View[] mActiveViews = new View[0];
        private ArrayList<View>[] mScrapViews;
        private int mViewTypeCount;
        private ArrayList<View> mCurrentScrap;
        private ArrayList<View> mSkippedScrap;
        private SparseArray<View> mTransientStateViews;
        private LongSparseArray<View> mTransientStateViewsById;

        RecycleBin() {
        }

        public void setViewTypeCount(int viewTypeCount) {
            if (viewTypeCount < 1) {
                throw new IllegalArgumentException("Can't have a viewTypeCount < 1");
            }
            ArrayList<View>[] scrapViews = new ArrayList[viewTypeCount];
            for (int i = 0; i < viewTypeCount; i++) {
                scrapViews[i] = new ArrayList<>();
            }
            this.mViewTypeCount = viewTypeCount;
            this.mCurrentScrap = scrapViews[0];
            this.mScrapViews = scrapViews;
        }

        public void markChildrenDirty() {
            if (this.mViewTypeCount == 1) {
                ArrayList<View> scrap = this.mCurrentScrap;
                int scrapCount = scrap.size();
                for (int i = 0; i < scrapCount; i++) {
                    scrap.get(i).forceLayout();
                }
            } else {
                int typeCount = this.mViewTypeCount;
                for (int i2 = 0; i2 < typeCount; i2++) {
                    ArrayList<View> scrap2 = this.mScrapViews[i2];
                    int scrapCount2 = scrap2.size();
                    for (int j = 0; j < scrapCount2; j++) {
                        scrap2.get(j).forceLayout();
                    }
                }
            }
            if (this.mTransientStateViews != null) {
                int count = this.mTransientStateViews.size();
                for (int i3 = 0; i3 < count; i3++) {
                    this.mTransientStateViews.valueAt(i3).forceLayout();
                }
            }
            if (this.mTransientStateViewsById != null) {
                int count2 = this.mTransientStateViewsById.size();
                for (int i4 = 0; i4 < count2; i4++) {
                    this.mTransientStateViewsById.valueAt(i4).forceLayout();
                }
            }
        }

        public boolean shouldRecycleViewType(int viewType) {
            return viewType >= 0;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void clear() {
            if (this.mViewTypeCount == 1) {
                ArrayList<View> scrap = this.mCurrentScrap;
                int scrapCount = scrap.size();
                for (int i = 0; i < scrapCount; i++) {
                    AbsListView.this.removeDetachedView(scrap.remove((scrapCount - 1) - i), false);
                }
            } else {
                int typeCount = this.mViewTypeCount;
                for (int i2 = 0; i2 < typeCount; i2++) {
                    ArrayList<View> scrap2 = this.mScrapViews[i2];
                    int scrapCount2 = scrap2.size();
                    for (int j = 0; j < scrapCount2; j++) {
                        AbsListView.this.removeDetachedView(scrap2.remove((scrapCount2 - 1) - j), false);
                    }
                }
            }
            if (this.mTransientStateViews != null) {
                this.mTransientStateViews.clear();
            }
            if (this.mTransientStateViewsById != null) {
                this.mTransientStateViewsById.clear();
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void fillActiveViews(int childCount, int firstActivePosition) {
            if (this.mActiveViews.length < childCount) {
                this.mActiveViews = new View[childCount];
            }
            this.mFirstActivePosition = firstActivePosition;
            View[] activeViews = this.mActiveViews;
            for (int i = 0; i < childCount; i++) {
                View child = AbsListView.this.getChildAt(i);
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (lp != null && lp.viewType != -2) {
                    activeViews[i] = child;
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public View getActiveView(int position) {
            int index = position - this.mFirstActivePosition;
            View[] activeViews = this.mActiveViews;
            if (index >= 0 && index < activeViews.length) {
                View match = activeViews[index];
                activeViews[index] = null;
                return match;
            }
            return null;
        }

        View getTransientStateView(int position) {
            int index;
            if (AbsListView.this.mAdapter != null && AbsListView.this.mAdapterHasStableIds && this.mTransientStateViewsById != null) {
                long id = AbsListView.this.mAdapter.getItemId(position);
                View result = this.mTransientStateViewsById.get(id);
                this.mTransientStateViewsById.remove(id);
                return result;
            } else if (this.mTransientStateViews != null && (index = this.mTransientStateViews.indexOfKey(position)) >= 0) {
                View result2 = this.mTransientStateViews.valueAt(index);
                this.mTransientStateViews.removeAt(index);
                return result2;
            } else {
                return null;
            }
        }

        void clearTransientStateViews() {
            if (this.mTransientStateViews != null) {
                this.mTransientStateViews.clear();
            }
            if (this.mTransientStateViewsById != null) {
                this.mTransientStateViewsById.clear();
            }
        }

        View getScrapView(int position) {
            if (this.mViewTypeCount == 1) {
                return AbsListView.retrieveFromScrap(this.mCurrentScrap, position);
            }
            int whichScrap = AbsListView.this.mAdapter.getItemViewType(position);
            if (whichScrap >= 0 && whichScrap < this.mScrapViews.length) {
                return AbsListView.retrieveFromScrap(this.mScrapViews[whichScrap], position);
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void addScrapView(View scrap, int position) {
            LayoutParams lp = (LayoutParams) scrap.getLayoutParams();
            if (lp == null) {
                return;
            }
            lp.scrappedFromPosition = position;
            int viewType = lp.viewType;
            if (!shouldRecycleViewType(viewType)) {
                return;
            }
            scrap.dispatchStartTemporaryDetach();
            boolean scrapHasTransientState = scrap.hasTransientState();
            if (scrapHasTransientState) {
                if (AbsListView.this.mAdapter != null && AbsListView.this.mAdapterHasStableIds) {
                    if (this.mTransientStateViewsById == null) {
                        this.mTransientStateViewsById = new LongSparseArray<>();
                    }
                    this.mTransientStateViewsById.put(lp.itemId, scrap);
                    return;
                } else if (!AbsListView.this.mDataChanged) {
                    if (this.mTransientStateViews == null) {
                        this.mTransientStateViews = new SparseArray<>();
                    }
                    this.mTransientStateViews.put(position, scrap);
                    return;
                } else {
                    if (this.mSkippedScrap == null) {
                        this.mSkippedScrap = new ArrayList<>();
                    }
                    this.mSkippedScrap.add(scrap);
                    return;
                }
            }
            if (this.mViewTypeCount == 1) {
                this.mCurrentScrap.add(scrap);
            } else {
                this.mScrapViews[viewType].add(scrap);
            }
            if (scrap.isAccessibilityFocused()) {
                scrap.clearAccessibilityFocus();
            }
            scrap.setAccessibilityDelegate(null);
            if (this.mRecyclerListener != null) {
                this.mRecyclerListener.onMovedToScrapHeap(scrap);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void removeSkippedScrap() {
            if (this.mSkippedScrap == null) {
                return;
            }
            int count = this.mSkippedScrap.size();
            for (int i = 0; i < count; i++) {
                AbsListView.this.removeDetachedView(this.mSkippedScrap.get(i), false);
            }
            this.mSkippedScrap.clear();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void scrapActiveViews() {
            View[] activeViews = this.mActiveViews;
            boolean hasListener = this.mRecyclerListener != null;
            boolean multipleScraps = this.mViewTypeCount > 1;
            ArrayList<View> scrapViews = this.mCurrentScrap;
            int count = activeViews.length;
            for (int i = count - 1; i >= 0; i--) {
                View victim = activeViews[i];
                if (victim != null) {
                    LayoutParams lp = (LayoutParams) victim.getLayoutParams();
                    int whichScrap = lp.viewType;
                    activeViews[i] = null;
                    boolean scrapHasTransientState = victim.hasTransientState();
                    if (!shouldRecycleViewType(whichScrap) || scrapHasTransientState) {
                        if (whichScrap != -2 && scrapHasTransientState) {
                            AbsListView.this.removeDetachedView(victim, false);
                        }
                        if (scrapHasTransientState) {
                            if (AbsListView.this.mAdapter != null && AbsListView.this.mAdapterHasStableIds) {
                                if (this.mTransientStateViewsById == null) {
                                    this.mTransientStateViewsById = new LongSparseArray<>();
                                }
                                long id = AbsListView.this.mAdapter.getItemId(this.mFirstActivePosition + i);
                                this.mTransientStateViewsById.put(id, victim);
                            } else {
                                if (this.mTransientStateViews == null) {
                                    this.mTransientStateViews = new SparseArray<>();
                                }
                                this.mTransientStateViews.put(this.mFirstActivePosition + i, victim);
                            }
                        }
                    } else {
                        if (multipleScraps) {
                            scrapViews = this.mScrapViews[whichScrap];
                        }
                        victim.dispatchStartTemporaryDetach();
                        lp.scrappedFromPosition = this.mFirstActivePosition + i;
                        scrapViews.add(victim);
                        victim.setAccessibilityDelegate(null);
                        if (hasListener) {
                            this.mRecyclerListener.onMovedToScrapHeap(victim);
                        }
                    }
                }
            }
            pruneScrapViews();
        }

        private void pruneScrapViews() {
            int maxViews = this.mActiveViews.length;
            int viewTypeCount = this.mViewTypeCount;
            ArrayList<View>[] scrapViews = this.mScrapViews;
            for (int i = 0; i < viewTypeCount; i++) {
                ArrayList<View> scrapPile = scrapViews[i];
                int size = scrapPile.size();
                int extras = size - maxViews;
                int size2 = size - 1;
                for (int j = 0; j < extras; j++) {
                    int i2 = size2;
                    size2--;
                    AbsListView.this.removeDetachedView(scrapPile.remove(i2), false);
                }
            }
            if (this.mTransientStateViews != null) {
                int i3 = 0;
                while (i3 < this.mTransientStateViews.size()) {
                    View v = this.mTransientStateViews.valueAt(i3);
                    if (!v.hasTransientState()) {
                        this.mTransientStateViews.removeAt(i3);
                        i3--;
                    }
                    i3++;
                }
            }
            if (this.mTransientStateViewsById != null) {
                int i4 = 0;
                while (i4 < this.mTransientStateViewsById.size()) {
                    View v2 = this.mTransientStateViewsById.valueAt(i4);
                    if (!v2.hasTransientState()) {
                        this.mTransientStateViewsById.removeAt(i4);
                        i4--;
                    }
                    i4++;
                }
            }
        }

        void reclaimScrapViews(List<View> views) {
            if (this.mViewTypeCount == 1) {
                views.addAll(this.mCurrentScrap);
                return;
            }
            int viewTypeCount = this.mViewTypeCount;
            ArrayList<View>[] scrapViews = this.mScrapViews;
            for (int i = 0; i < viewTypeCount; i++) {
                ArrayList<View> scrapPile = scrapViews[i];
                views.addAll(scrapPile);
            }
        }

        void setCacheColorHint(int color) {
            if (this.mViewTypeCount == 1) {
                ArrayList<View> scrap = this.mCurrentScrap;
                int scrapCount = scrap.size();
                for (int i = 0; i < scrapCount; i++) {
                    scrap.get(i).setDrawingCacheBackgroundColor(color);
                }
            } else {
                int typeCount = this.mViewTypeCount;
                for (int i2 = 0; i2 < typeCount; i2++) {
                    ArrayList<View> scrap2 = this.mScrapViews[i2];
                    int scrapCount2 = scrap2.size();
                    for (int j = 0; j < scrapCount2; j++) {
                        scrap2.get(j).setDrawingCacheBackgroundColor(color);
                    }
                }
            }
            View[] activeViews = this.mActiveViews;
            for (View victim : activeViews) {
                if (victim != null) {
                    victim.setDrawingCacheBackgroundColor(color);
                }
            }
        }
    }

    static View retrieveFromScrap(ArrayList<View> scrapViews, int position) {
        int size = scrapViews.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                View view = scrapViews.get(i);
                if (((LayoutParams) view.getLayoutParams()).scrappedFromPosition == position) {
                    scrapViews.remove(i);
                    return view;
                }
            }
            return scrapViews.remove(size - 1);
        }
        return null;
    }
}