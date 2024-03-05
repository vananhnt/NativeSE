package android.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.IntProperty;
import android.util.MathUtils;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import com.android.internal.R;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: FastScroller.class */
public class FastScroller {
    private static final int DURATION_FADE_OUT = 300;
    private static final int DURATION_FADE_IN = 150;
    private static final int DURATION_CROSS_FADE = 50;
    private static final int DURATION_RESIZE = 100;
    private static final long FADE_TIMEOUT = 1500;
    private static final int MIN_PAGES = 4;
    private static final int STATE_NONE = 0;
    private static final int STATE_VISIBLE = 1;
    private static final int STATE_DRAGGING = 2;
    private static final int TEXT_COLOR = 0;
    private static final int THUMB_DRAWABLE = 1;
    private static final int TRACK_DRAWABLE = 2;
    private static final int PREVIEW_BACKGROUND_LEFT = 3;
    private static final int PREVIEW_BACKGROUND_RIGHT = 4;
    private static final int OVERLAY_POSITION = 5;
    private static final int OVERLAY_FLOATING = 0;
    private static final int OVERLAY_AT_THUMB = 1;
    private static final int PREVIEW_LEFT = 0;
    private static final int PREVIEW_RIGHT = 1;
    private final AbsListView mList;
    private final ViewGroupOverlay mOverlay;
    private final TextView mPrimaryText;
    private final TextView mSecondaryText;
    private final ImageView mThumbImage;
    private final ImageView mTrackImage;
    private final ImageView mPreviewImage;
    private final int mPreviewPadding;
    private final boolean mHasTrackImage;
    private final int mWidth;
    private AnimatorSet mDecorAnimation;
    private AnimatorSet mPreviewAnimation;
    private boolean mShowingPrimary;
    private boolean mScrollCompleted;
    private int mFirstVisibleItem;
    private int mHeaderCount;
    private boolean mLongList;
    private Object[] mSections;
    private boolean mUpdatingLayout;
    private int mState;
    private BaseAdapter mListAdapter;
    private SectionIndexer mSectionIndexer;
    private boolean mLayoutFromRight;
    private boolean mEnabled;
    private boolean mAlwaysShow;
    private int mOverlayPosition;
    private int mScrollBarStyle;
    private boolean mMatchDragPosition;
    private float mInitialTouchY;
    private boolean mHasPendingDrag;
    private int mScaledTouchSlop;
    private static final int[] ATTRS = {16843609, 16843574, 16843577, 16843575, 16843576, 16843578};
    private static final long TAP_TIMEOUT = ViewConfiguration.getTapTimeout();
    private static Property<View, Integer> LEFT = new IntProperty<View>("left") { // from class: android.widget.FastScroller.4
        @Override // android.util.IntProperty
        public void setValue(View object, int value) {
            object.setLeft(value);
        }

        @Override // android.util.Property
        public Integer get(View object) {
            return Integer.valueOf(object.getLeft());
        }
    };
    private static Property<View, Integer> TOP = new IntProperty<View>("top") { // from class: android.widget.FastScroller.5
        @Override // android.util.IntProperty
        public void setValue(View object, int value) {
            object.setTop(value);
        }

        @Override // android.util.Property
        public Integer get(View object) {
            return Integer.valueOf(object.getTop());
        }
    };
    private static Property<View, Integer> RIGHT = new IntProperty<View>("right") { // from class: android.widget.FastScroller.6
        @Override // android.util.IntProperty
        public void setValue(View object, int value) {
            object.setRight(value);
        }

        @Override // android.util.Property
        public Integer get(View object) {
            return Integer.valueOf(object.getRight());
        }
    };
    private static Property<View, Integer> BOTTOM = new IntProperty<View>("bottom") { // from class: android.widget.FastScroller.7
        @Override // android.util.IntProperty
        public void setValue(View object, int value) {
            object.setBottom(value);
        }

        @Override // android.util.Property
        public Integer get(View object) {
            return Integer.valueOf(object.getBottom());
        }
    };
    private final Rect mTempBounds = new Rect();
    private final Rect mTempMargins = new Rect();
    private final Rect mContainerRect = new Rect();
    private final int[] mPreviewResId = new int[2];
    private int mCurrentSection = -1;
    private int mScrollbarPosition = -1;
    private final Runnable mDeferStartDrag = new Runnable() { // from class: android.widget.FastScroller.1
        @Override // java.lang.Runnable
        public void run() {
            if (FastScroller.this.mList.isAttachedToWindow()) {
                FastScroller.this.beginDrag();
                float pos = FastScroller.this.getPosFromMotionEvent(FastScroller.this.mInitialTouchY);
                FastScroller.this.scrollTo(pos);
            }
            FastScroller.this.mHasPendingDrag = false;
        }
    };
    private final Runnable mDeferHide = new Runnable() { // from class: android.widget.FastScroller.2
        @Override // java.lang.Runnable
        public void run() {
            FastScroller.this.setState(0);
        }
    };
    private final Animator.AnimatorListener mSwitchPrimaryListener = new AnimatorListenerAdapter() { // from class: android.widget.FastScroller.3
        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animation) {
            FastScroller.this.mShowingPrimary = !FastScroller.this.mShowingPrimary;
        }
    };

    public FastScroller(AbsListView listView) {
        this.mList = listView;
        this.mOverlay = listView.getOverlay();
        Context context = listView.getContext();
        this.mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        Resources res = context.getResources();
        TypedArray ta = context.getTheme().obtainStyledAttributes(ATTRS);
        ImageView trackImage = new ImageView(context);
        this.mTrackImage = trackImage;
        int width = 0;
        Drawable trackDrawable = ta.getDrawable(2);
        if (trackDrawable != null) {
            this.mHasTrackImage = true;
            trackImage.setBackground(trackDrawable);
            this.mOverlay.add(trackImage);
            width = Math.max(0, trackDrawable.getIntrinsicWidth());
        } else {
            this.mHasTrackImage = false;
        }
        ImageView thumbImage = new ImageView(context);
        this.mThumbImage = thumbImage;
        Drawable thumbDrawable = ta.getDrawable(1);
        if (thumbDrawable != null) {
            thumbImage.setImageDrawable(thumbDrawable);
            this.mOverlay.add(thumbImage);
            width = Math.max(width, thumbDrawable.getIntrinsicWidth());
        }
        if (thumbDrawable.getIntrinsicWidth() <= 0 || thumbDrawable.getIntrinsicHeight() <= 0) {
            int minWidth = res.getDimensionPixelSize(R.dimen.fastscroll_thumb_width);
            thumbImage.setMinimumWidth(minWidth);
            thumbImage.setMinimumHeight(res.getDimensionPixelSize(R.dimen.fastscroll_thumb_height));
            width = Math.max(width, minWidth);
        }
        this.mWidth = width;
        int previewSize = res.getDimensionPixelSize(R.dimen.fastscroll_overlay_size);
        this.mPreviewImage = new ImageView(context);
        this.mPreviewImage.setMinimumWidth(previewSize);
        this.mPreviewImage.setMinimumHeight(previewSize);
        this.mPreviewImage.setAlpha(0.0f);
        this.mOverlay.add(this.mPreviewImage);
        this.mPreviewPadding = res.getDimensionPixelSize(R.dimen.fastscroll_overlay_padding);
        int textMinSize = Math.max(0, previewSize - this.mPreviewPadding);
        this.mPrimaryText = createPreviewTextView(context, ta);
        this.mPrimaryText.setMinimumWidth(textMinSize);
        this.mPrimaryText.setMinimumHeight(textMinSize);
        this.mOverlay.add(this.mPrimaryText);
        this.mSecondaryText = createPreviewTextView(context, ta);
        this.mSecondaryText.setMinimumWidth(textMinSize);
        this.mSecondaryText.setMinimumHeight(textMinSize);
        this.mOverlay.add(this.mSecondaryText);
        this.mPreviewResId[0] = ta.getResourceId(3, 0);
        this.mPreviewResId[1] = ta.getResourceId(4, 0);
        this.mOverlayPosition = ta.getInt(5, 0);
        ta.recycle();
        this.mScrollBarStyle = listView.getScrollBarStyle();
        this.mScrollCompleted = true;
        this.mState = 1;
        this.mMatchDragPosition = context.getApplicationInfo().targetSdkVersion >= 11;
        getSectionsFromIndexer();
        refreshDrawablePressedState();
        updateLongList(listView.getChildCount(), listView.getCount());
        setScrollbarPosition(this.mList.getVerticalScrollbarPosition());
        postAutoHide();
    }

    public void remove() {
        this.mOverlay.remove(this.mTrackImage);
        this.mOverlay.remove(this.mThumbImage);
        this.mOverlay.remove(this.mPreviewImage);
        this.mOverlay.remove(this.mPrimaryText);
        this.mOverlay.remove(this.mSecondaryText);
    }

    public void setEnabled(boolean enabled) {
        if (this.mEnabled != enabled) {
            this.mEnabled = enabled;
            onStateDependencyChanged();
        }
    }

    public boolean isEnabled() {
        return this.mEnabled && (this.mLongList || this.mAlwaysShow);
    }

    public void setAlwaysShow(boolean alwaysShow) {
        if (this.mAlwaysShow != alwaysShow) {
            this.mAlwaysShow = alwaysShow;
            onStateDependencyChanged();
        }
    }

    public boolean isAlwaysShowEnabled() {
        return this.mAlwaysShow;
    }

    private void onStateDependencyChanged() {
        if (isEnabled()) {
            if (isAlwaysShowEnabled()) {
                setState(1);
            } else if (this.mState == 1) {
                postAutoHide();
            }
        } else {
            stop();
        }
        this.mList.resolvePadding();
    }

    public void setScrollBarStyle(int style) {
        if (this.mScrollBarStyle != style) {
            this.mScrollBarStyle = style;
            updateLayout();
        }
    }

    public void stop() {
        setState(0);
    }

    public void setScrollbarPosition(int position) {
        if (position == 0) {
            position = this.mList.isLayoutRtl() ? 1 : 2;
        }
        if (this.mScrollbarPosition != position) {
            this.mScrollbarPosition = position;
            this.mLayoutFromRight = position != 1;
            int previewResId = this.mPreviewResId[this.mLayoutFromRight ? (char) 1 : (char) 0];
            this.mPreviewImage.setBackgroundResource(previewResId);
            Drawable background = this.mPreviewImage.getBackground();
            if (background != null) {
                Rect padding = this.mTempBounds;
                background.getPadding(padding);
                padding.offset(this.mPreviewPadding, this.mPreviewPadding);
                this.mPreviewImage.setPadding(padding.left, padding.top, padding.right, padding.bottom);
            }
            updateLayout();
        }
    }

    public int getWidth() {
        return this.mWidth;
    }

    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        updateLayout();
    }

    public void onItemCountChanged(int totalItemCount) {
        int visibleItemCount = this.mList.getChildCount();
        boolean hasMoreItems = totalItemCount - visibleItemCount > 0;
        if (hasMoreItems && this.mState != 2) {
            int firstVisibleItem = this.mList.getFirstVisiblePosition();
            setThumbPos(getPosFromItemCount(firstVisibleItem, visibleItemCount, totalItemCount));
        }
        updateLongList(visibleItemCount, totalItemCount);
    }

    private void updateLongList(int visibleItemCount, int totalItemCount) {
        boolean longList = visibleItemCount > 0 && totalItemCount / visibleItemCount >= 4;
        if (this.mLongList != longList) {
            this.mLongList = longList;
            onStateDependencyChanged();
        }
    }

    private TextView createPreviewTextView(Context context, TypedArray ta) {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(-2, -2);
        Resources res = context.getResources();
        res.getDimensionPixelSize(R.dimen.fastscroll_overlay_size);
        ColorStateList textColor = ta.getColorStateList(0);
        float textSize = res.getDimensionPixelSize(R.dimen.fastscroll_overlay_text_size);
        TextView textView = new TextView(context);
        textView.setLayoutParams(params);
        textView.setTextColor(textColor);
        textView.setTextSize(0, textSize);
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        textView.setGravity(17);
        textView.setAlpha(0.0f);
        textView.setLayoutDirection(this.mList.getLayoutDirection());
        return textView;
    }

    public void updateLayout() {
        if (this.mUpdatingLayout) {
            return;
        }
        this.mUpdatingLayout = true;
        updateContainerRect();
        layoutThumb();
        layoutTrack();
        Rect bounds = this.mTempBounds;
        measurePreview(this.mPrimaryText, bounds);
        applyLayout(this.mPrimaryText, bounds);
        measurePreview(this.mSecondaryText, bounds);
        applyLayout(this.mSecondaryText, bounds);
        if (this.mPreviewImage != null) {
            bounds.left -= this.mPreviewImage.getPaddingLeft();
            bounds.top -= this.mPreviewImage.getPaddingTop();
            bounds.right += this.mPreviewImage.getPaddingRight();
            bounds.bottom += this.mPreviewImage.getPaddingBottom();
            applyLayout(this.mPreviewImage, bounds);
        }
        this.mUpdatingLayout = false;
    }

    private void applyLayout(View view, Rect bounds) {
        view.layout(bounds.left, bounds.top, bounds.right, bounds.bottom);
        view.setPivotX(this.mLayoutFromRight ? bounds.right - bounds.left : 0.0f);
    }

    private void measurePreview(View v, Rect out) {
        Rect margins = this.mTempMargins;
        margins.left = this.mPreviewImage.getPaddingLeft();
        margins.top = this.mPreviewImage.getPaddingTop();
        margins.right = this.mPreviewImage.getPaddingRight();
        margins.bottom = this.mPreviewImage.getPaddingBottom();
        if (this.mOverlayPosition == 1) {
            measureViewToSide(v, this.mThumbImage, margins, out);
        } else {
            measureFloating(v, margins, out);
        }
    }

    private void measureViewToSide(View view, View adjacent, Rect margins, Rect out) {
        int marginLeft;
        int marginTop;
        int marginRight;
        int maxWidth;
        int left;
        int right;
        if (margins == null) {
            marginLeft = 0;
            marginTop = 0;
            marginRight = 0;
        } else {
            marginLeft = margins.left;
            marginTop = margins.top;
            marginRight = margins.right;
        }
        Rect container = this.mContainerRect;
        int containerWidth = container.width();
        if (adjacent == null) {
            maxWidth = containerWidth;
        } else if (this.mLayoutFromRight) {
            maxWidth = adjacent.getLeft();
        } else {
            maxWidth = containerWidth - adjacent.getRight();
        }
        int adjMaxWidth = (maxWidth - marginLeft) - marginRight;
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(adjMaxWidth, Integer.MIN_VALUE);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
        view.measure(widthMeasureSpec, heightMeasureSpec);
        int width = view.getMeasuredWidth();
        if (this.mLayoutFromRight) {
            right = (adjacent == null ? container.right : adjacent.getLeft()) - marginRight;
            left = right - width;
        } else {
            left = (adjacent == null ? container.left : adjacent.getRight()) + marginLeft;
            right = left + width;
        }
        int top = marginTop;
        int bottom = top + view.getMeasuredHeight();
        out.set(left, top, right, bottom);
    }

    private void measureFloating(View preview, Rect margins, Rect out) {
        int marginLeft;
        int marginTop;
        int marginRight;
        if (margins == null) {
            marginLeft = 0;
            marginTop = 0;
            marginRight = 0;
        } else {
            marginLeft = margins.left;
            marginTop = margins.top;
            marginRight = margins.right;
        }
        Rect container = this.mContainerRect;
        int containerWidth = container.width();
        int adjMaxWidth = (containerWidth - marginLeft) - marginRight;
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(adjMaxWidth, Integer.MIN_VALUE);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
        preview.measure(widthMeasureSpec, heightMeasureSpec);
        int containerHeight = container.height();
        int width = preview.getMeasuredWidth();
        int top = (containerHeight / 10) + marginTop + container.top;
        int bottom = top + preview.getMeasuredHeight();
        int left = ((containerWidth - width) / 2) + container.left;
        int right = left + width;
        out.set(left, top, right, bottom);
    }

    private void updateContainerRect() {
        AbsListView list = this.mList;
        list.resolvePadding();
        Rect container = this.mContainerRect;
        container.left = 0;
        container.top = 0;
        container.right = list.getWidth();
        container.bottom = list.getHeight();
        int scrollbarStyle = this.mScrollBarStyle;
        if (scrollbarStyle == 16777216 || scrollbarStyle == 0) {
            container.left += list.getPaddingLeft();
            container.top += list.getPaddingTop();
            container.right -= list.getPaddingRight();
            container.bottom -= list.getPaddingBottom();
            if (scrollbarStyle == 16777216) {
                int width = getWidth();
                if (this.mScrollbarPosition == 2) {
                    container.right += width;
                } else {
                    container.left -= width;
                }
            }
        }
    }

    private void layoutThumb() {
        Rect bounds = this.mTempBounds;
        measureViewToSide(this.mThumbImage, null, null, bounds);
        applyLayout(this.mThumbImage, bounds);
    }

    private void layoutTrack() {
        View track = this.mTrackImage;
        View thumb = this.mThumbImage;
        Rect container = this.mContainerRect;
        int containerWidth = container.width();
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(containerWidth, Integer.MIN_VALUE);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
        track.measure(widthMeasureSpec, heightMeasureSpec);
        int trackWidth = track.getMeasuredWidth();
        int thumbHalfHeight = thumb == null ? 0 : thumb.getHeight() / 2;
        int left = thumb.getLeft() + ((thumb.getWidth() - trackWidth) / 2);
        int right = left + trackWidth;
        int top = container.top + thumbHalfHeight;
        int bottom = container.bottom - thumbHalfHeight;
        track.layout(left, top, right, bottom);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setState(int state) {
        this.mList.removeCallbacks(this.mDeferHide);
        if (this.mAlwaysShow && state == 0) {
            state = 1;
        }
        if (state == this.mState) {
            return;
        }
        switch (state) {
            case 0:
                transitionToHidden();
                break;
            case 1:
                transitionToVisible();
                break;
            case 2:
                if (transitionPreviewLayout(this.mCurrentSection)) {
                    transitionToDragging();
                    break;
                } else {
                    transitionToVisible();
                    break;
                }
        }
        this.mState = state;
        refreshDrawablePressedState();
    }

    private void refreshDrawablePressedState() {
        boolean isPressed = this.mState == 2;
        this.mThumbImage.setPressed(isPressed);
        this.mTrackImage.setPressed(isPressed);
    }

    private void transitionToHidden() {
        if (this.mDecorAnimation != null) {
            this.mDecorAnimation.cancel();
        }
        Animator fadeOut = groupAnimatorOfFloat(View.ALPHA, 0.0f, this.mThumbImage, this.mTrackImage, this.mPreviewImage, this.mPrimaryText, this.mSecondaryText).setDuration(300L);
        float offset = this.mLayoutFromRight ? this.mThumbImage.getWidth() : -this.mThumbImage.getWidth();
        Animator slideOut = groupAnimatorOfFloat(View.TRANSLATION_X, offset, this.mThumbImage, this.mTrackImage).setDuration(300L);
        this.mDecorAnimation = new AnimatorSet();
        this.mDecorAnimation.playTogether(fadeOut, slideOut);
        this.mDecorAnimation.start();
    }

    private void transitionToVisible() {
        if (this.mDecorAnimation != null) {
            this.mDecorAnimation.cancel();
        }
        Animator fadeIn = groupAnimatorOfFloat(View.ALPHA, 1.0f, this.mThumbImage, this.mTrackImage).setDuration(150L);
        Animator fadeOut = groupAnimatorOfFloat(View.ALPHA, 0.0f, this.mPreviewImage, this.mPrimaryText, this.mSecondaryText).setDuration(300L);
        Animator slideIn = groupAnimatorOfFloat(View.TRANSLATION_X, 0.0f, this.mThumbImage, this.mTrackImage).setDuration(150L);
        this.mDecorAnimation = new AnimatorSet();
        this.mDecorAnimation.playTogether(fadeIn, fadeOut, slideIn);
        this.mDecorAnimation.start();
    }

    private void transitionToDragging() {
        if (this.mDecorAnimation != null) {
            this.mDecorAnimation.cancel();
        }
        Animator fadeIn = groupAnimatorOfFloat(View.ALPHA, 1.0f, this.mThumbImage, this.mTrackImage, this.mPreviewImage).setDuration(150L);
        Animator slideIn = groupAnimatorOfFloat(View.TRANSLATION_X, 0.0f, this.mThumbImage, this.mTrackImage).setDuration(150L);
        this.mDecorAnimation = new AnimatorSet();
        this.mDecorAnimation.playTogether(fadeIn, slideIn);
        this.mDecorAnimation.start();
    }

    private void postAutoHide() {
        this.mList.removeCallbacks(this.mDeferHide);
        this.mList.postDelayed(this.mDeferHide, FADE_TIMEOUT);
    }

    public void onScroll(int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (!isEnabled()) {
            setState(0);
            return;
        }
        boolean hasMoreItems = totalItemCount - visibleItemCount > 0;
        if (hasMoreItems && this.mState != 2) {
            setThumbPos(getPosFromItemCount(firstVisibleItem, visibleItemCount, totalItemCount));
        }
        this.mScrollCompleted = true;
        if (this.mFirstVisibleItem != firstVisibleItem) {
            this.mFirstVisibleItem = firstVisibleItem;
            if (this.mState != 2) {
                setState(1);
                postAutoHide();
            }
        }
    }

    private void getSectionsFromIndexer() {
        this.mSectionIndexer = null;
        Adapter adapter = this.mList.getAdapter();
        if (adapter instanceof HeaderViewListAdapter) {
            this.mHeaderCount = ((HeaderViewListAdapter) adapter).getHeadersCount();
            adapter = ((HeaderViewListAdapter) adapter).getWrappedAdapter();
        }
        if (adapter instanceof ExpandableListConnector) {
            ExpandableListAdapter expAdapter = ((ExpandableListConnector) adapter).getAdapter();
            if (expAdapter instanceof SectionIndexer) {
                this.mSectionIndexer = (SectionIndexer) expAdapter;
                this.mListAdapter = (BaseAdapter) adapter;
                this.mSections = this.mSectionIndexer.getSections();
            }
        } else if (adapter instanceof SectionIndexer) {
            this.mListAdapter = (BaseAdapter) adapter;
            this.mSectionIndexer = (SectionIndexer) adapter;
            this.mSections = this.mSectionIndexer.getSections();
        } else {
            this.mListAdapter = (BaseAdapter) adapter;
            this.mSections = null;
        }
    }

    public void onSectionsChanged() {
        this.mListAdapter = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void scrollTo(float position) {
        int sectionIndex;
        int targetIndex;
        this.mScrollCompleted = false;
        int count = this.mList.getCount();
        Object[] sections = this.mSections;
        int sectionCount = sections == null ? 0 : sections.length;
        if (sections != null && sectionCount > 1) {
            int exactSection = MathUtils.constrain((int) (position * sectionCount), 0, sectionCount - 1);
            int targetSection = exactSection;
            int targetIndex2 = this.mSectionIndexer.getPositionForSection(targetSection);
            sectionIndex = targetSection;
            int nextIndex = count;
            int prevIndex = targetIndex2;
            int prevSection = targetSection;
            int nextSection = targetSection + 1;
            if (targetSection < sectionCount - 1) {
                nextIndex = this.mSectionIndexer.getPositionForSection(targetSection + 1);
            }
            if (nextIndex == targetIndex2) {
                while (true) {
                    if (targetSection <= 0) {
                        break;
                    }
                    targetSection--;
                    prevIndex = this.mSectionIndexer.getPositionForSection(targetSection);
                    if (prevIndex != targetIndex2) {
                        prevSection = targetSection;
                        sectionIndex = targetSection;
                        break;
                    } else if (targetSection == 0) {
                        sectionIndex = 0;
                        break;
                    }
                }
            }
            int nextNextSection = nextSection + 1;
            while (nextNextSection < sectionCount && this.mSectionIndexer.getPositionForSection(nextNextSection) == nextIndex) {
                nextNextSection++;
                nextSection++;
            }
            float prevPosition = prevSection / sectionCount;
            float nextPosition = nextSection / sectionCount;
            float snapThreshold = count == 0 ? Float.MAX_VALUE : 0.125f / count;
            if (prevSection == exactSection && position - prevPosition < snapThreshold) {
                targetIndex = prevIndex;
            } else {
                targetIndex = prevIndex + ((int) (((nextIndex - prevIndex) * (position - prevPosition)) / (nextPosition - prevPosition)));
            }
            int targetIndex3 = MathUtils.constrain(targetIndex, 0, count - 1);
            if (this.mList instanceof ExpandableListView) {
                ExpandableListView expList = (ExpandableListView) this.mList;
                expList.setSelectionFromTop(expList.getFlatListPosition(ExpandableListView.getPackedPositionForGroup(targetIndex3 + this.mHeaderCount)), 0);
            } else if (this.mList instanceof ListView) {
                ((ListView) this.mList).setSelectionFromTop(targetIndex3 + this.mHeaderCount, 0);
            } else {
                this.mList.setSelection(targetIndex3 + this.mHeaderCount);
            }
        } else {
            int index = MathUtils.constrain((int) (position * count), 0, count - 1);
            if (this.mList instanceof ExpandableListView) {
                ExpandableListView expList2 = (ExpandableListView) this.mList;
                expList2.setSelectionFromTop(expList2.getFlatListPosition(ExpandableListView.getPackedPositionForGroup(index + this.mHeaderCount)), 0);
            } else if (this.mList instanceof ListView) {
                ((ListView) this.mList).setSelectionFromTop(index + this.mHeaderCount, 0);
            } else {
                this.mList.setSelection(index + this.mHeaderCount);
            }
            sectionIndex = -1;
        }
        if (this.mCurrentSection != sectionIndex) {
            this.mCurrentSection = sectionIndex;
            if (transitionPreviewLayout(sectionIndex)) {
                transitionToDragging();
            } else {
                transitionToVisible();
            }
        }
    }

    private boolean transitionPreviewLayout(int sectionIndex) {
        TextView showing;
        TextView target;
        Object section;
        Object[] sections = this.mSections;
        String text = null;
        if (sections != null && sectionIndex >= 0 && sectionIndex < sections.length && (section = sections[sectionIndex]) != null) {
            text = section.toString();
        }
        Rect bounds = this.mTempBounds;
        ImageView preview = this.mPreviewImage;
        if (this.mShowingPrimary) {
            showing = this.mPrimaryText;
            target = this.mSecondaryText;
        } else {
            showing = this.mSecondaryText;
            target = this.mPrimaryText;
        }
        target.setText(text);
        measurePreview(target, bounds);
        applyLayout(target, bounds);
        if (this.mPreviewAnimation != null) {
            this.mPreviewAnimation.cancel();
        }
        Animator showTarget = animateAlpha(target, 1.0f).setDuration(50L);
        Animator hideShowing = animateAlpha(showing, 0.0f).setDuration(50L);
        hideShowing.addListener(this.mSwitchPrimaryListener);
        bounds.left -= this.mPreviewImage.getPaddingLeft();
        bounds.top -= this.mPreviewImage.getPaddingTop();
        bounds.right += this.mPreviewImage.getPaddingRight();
        bounds.bottom += this.mPreviewImage.getPaddingBottom();
        Animator resizePreview = animateBounds(preview, bounds);
        resizePreview.setDuration(100L);
        this.mPreviewAnimation = new AnimatorSet();
        AnimatorSet.Builder builder = this.mPreviewAnimation.play(hideShowing).with(showTarget);
        builder.with(resizePreview);
        int previewWidth = (preview.getWidth() - preview.getPaddingLeft()) - preview.getPaddingRight();
        int targetWidth = target.getWidth();
        if (targetWidth > previewWidth) {
            target.setScaleX(previewWidth / targetWidth);
            Animator scaleAnim = animateScaleX(target, 1.0f).setDuration(100L);
            builder.with(scaleAnim);
        } else {
            target.setScaleX(1.0f);
        }
        int showingWidth = showing.getWidth();
        if (showingWidth > targetWidth) {
            float scale = targetWidth / showingWidth;
            Animator scaleAnim2 = animateScaleX(showing, scale).setDuration(100L);
            builder.with(scaleAnim2);
        }
        this.mPreviewAnimation.start();
        return text != null && text.length() > 0;
    }

    private void setThumbPos(float position) {
        Rect container = this.mContainerRect;
        int top = container.top;
        int bottom = container.bottom;
        ImageView trackImage = this.mTrackImage;
        ImageView thumbImage = this.mThumbImage;
        float min = trackImage.getTop();
        float max = trackImage.getBottom();
        float range = max - min;
        float thumbMiddle = (position * range) + min;
        thumbImage.setTranslationY(thumbMiddle - (thumbImage.getHeight() / 2));
        float previewPos = this.mOverlayPosition == 1 ? thumbMiddle : 0.0f;
        ImageView previewImage = this.mPreviewImage;
        float previewHalfHeight = previewImage.getHeight() / 2.0f;
        float minP = top + previewHalfHeight;
        float maxP = bottom - previewHalfHeight;
        float previewMiddle = MathUtils.constrain(previewPos, minP, maxP);
        float previewTop = previewMiddle - previewHalfHeight;
        previewImage.setTranslationY(previewTop);
        this.mPrimaryText.setTranslationY(previewTop);
        this.mSecondaryText.setTranslationY(previewTop);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public float getPosFromMotionEvent(float y) {
        Rect container = this.mContainerRect;
        int i = container.top;
        int i2 = container.bottom;
        ImageView trackImage = this.mTrackImage;
        float min = trackImage.getTop();
        float max = trackImage.getBottom();
        float range = max - min;
        if (range <= 0.0f) {
            return 0.0f;
        }
        return MathUtils.constrain((y - min) / range, 0.0f, 1.0f);
    }

    private float getPosFromItemCount(int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        float incrementalPos;
        int positionsInSection;
        float posWithinSection;
        int nextSectionPos;
        if (this.mSectionIndexer == null || this.mListAdapter == null) {
            getSectionsFromIndexer();
        }
        boolean hasSections = (this.mSectionIndexer == null || this.mSections == null || this.mSections.length <= 0) ? false : true;
        if (!hasSections || !this.mMatchDragPosition) {
            return firstVisibleItem / (totalItemCount - visibleItemCount);
        }
        int firstVisibleItem2 = firstVisibleItem - this.mHeaderCount;
        if (firstVisibleItem2 < 0) {
            return 0.0f;
        }
        int totalItemCount2 = totalItemCount - this.mHeaderCount;
        View child = this.mList.getChildAt(0);
        if (child == null || child.getHeight() == 0) {
            incrementalPos = 0.0f;
        } else {
            incrementalPos = (this.mList.getPaddingTop() - child.getTop()) / child.getHeight();
        }
        int section = this.mSectionIndexer.getSectionForPosition(firstVisibleItem2);
        int sectionPos = this.mSectionIndexer.getPositionForSection(section);
        int sectionCount = this.mSections.length;
        if (section < sectionCount - 1) {
            if (section + 1 < sectionCount) {
                nextSectionPos = this.mSectionIndexer.getPositionForSection(section + 1);
            } else {
                nextSectionPos = totalItemCount2 - 1;
            }
            positionsInSection = nextSectionPos - sectionPos;
        } else {
            positionsInSection = totalItemCount2 - sectionPos;
        }
        if (positionsInSection == 0) {
            posWithinSection = 0.0f;
        } else {
            posWithinSection = ((firstVisibleItem2 + incrementalPos) - sectionPos) / positionsInSection;
        }
        return (section + posWithinSection) / sectionCount;
    }

    private void cancelFling() {
        MotionEvent cancelFling = MotionEvent.obtain(0L, 0L, 3, 0.0f, 0.0f, 0);
        this.mList.onTouchEvent(cancelFling);
        cancelFling.recycle();
    }

    private void cancelPendingDrag() {
        this.mList.removeCallbacks(this.mDeferStartDrag);
        this.mHasPendingDrag = false;
    }

    private void startPendingDrag() {
        this.mHasPendingDrag = true;
        this.mList.postDelayed(this.mDeferStartDrag, TAP_TIMEOUT);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void beginDrag() {
        setState(2);
        if (this.mListAdapter == null && this.mList != null) {
            getSectionsFromIndexer();
        }
        if (this.mList != null) {
            this.mList.requestDisallowInterceptTouchEvent(true);
            this.mList.reportScrollStateChange(1);
        }
        cancelFling();
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isEnabled()) {
            return false;
        }
        switch (ev.getActionMasked()) {
            case 0:
                if (isPointInside(ev.getX(), ev.getY())) {
                    if (!this.mList.isInScrollingContainer()) {
                        beginDrag();
                        return true;
                    }
                    this.mInitialTouchY = ev.getY();
                    startPendingDrag();
                    return false;
                }
                return false;
            case 1:
            case 3:
                cancelPendingDrag();
                return false;
            case 2:
                if (!isPointInside(ev.getX(), ev.getY())) {
                    cancelPendingDrag();
                    return false;
                }
                return false;
            default:
                return false;
        }
    }

    public boolean onInterceptHoverEvent(MotionEvent ev) {
        if (!isEnabled()) {
            return false;
        }
        int actionMasked = ev.getActionMasked();
        if ((actionMasked == 9 || actionMasked == 7) && this.mState == 0 && isPointInside(ev.getX(), ev.getY())) {
            setState(1);
            postAutoHide();
            return false;
        }
        return false;
    }

    public boolean onTouchEvent(MotionEvent me) {
        if (!isEnabled()) {
            return false;
        }
        switch (me.getActionMasked()) {
            case 1:
                if (this.mHasPendingDrag) {
                    beginDrag();
                    float pos = getPosFromMotionEvent(me.getY());
                    setThumbPos(pos);
                    scrollTo(pos);
                    cancelPendingDrag();
                }
                if (this.mState == 2) {
                    if (this.mList != null) {
                        this.mList.requestDisallowInterceptTouchEvent(false);
                        this.mList.reportScrollStateChange(0);
                    }
                    setState(1);
                    postAutoHide();
                    return true;
                }
                return false;
            case 2:
                if (this.mHasPendingDrag && Math.abs(me.getY() - this.mInitialTouchY) > this.mScaledTouchSlop) {
                    setState(2);
                    if (this.mListAdapter == null && this.mList != null) {
                        getSectionsFromIndexer();
                    }
                    if (this.mList != null) {
                        this.mList.requestDisallowInterceptTouchEvent(true);
                        this.mList.reportScrollStateChange(1);
                    }
                    cancelFling();
                    cancelPendingDrag();
                }
                if (this.mState == 2) {
                    float pos2 = getPosFromMotionEvent(me.getY());
                    setThumbPos(pos2);
                    if (this.mScrollCompleted) {
                        scrollTo(pos2);
                        return true;
                    }
                    return true;
                }
                return false;
            case 3:
                cancelPendingDrag();
                return false;
            default:
                return false;
        }
    }

    private boolean isPointInside(float x, float y) {
        return isPointInsideX(x) && (this.mHasTrackImage || isPointInsideY(y));
    }

    private boolean isPointInsideX(float x) {
        return this.mLayoutFromRight ? x >= ((float) this.mThumbImage.getLeft()) : x <= ((float) this.mThumbImage.getRight());
    }

    private boolean isPointInsideY(float y) {
        float offset = this.mThumbImage.getTranslationY();
        float top = this.mThumbImage.getTop() + offset;
        float bottom = this.mThumbImage.getBottom() + offset;
        return y >= top && y <= bottom;
    }

    private static Animator groupAnimatorOfFloat(Property<View, Float> property, float value, View... views) {
        AnimatorSet animSet = new AnimatorSet();
        AnimatorSet.Builder builder = null;
        for (int i = views.length - 1; i >= 0; i--) {
            Animator anim = ObjectAnimator.ofFloat(views[i], property, value);
            if (builder == null) {
                builder = animSet.play(anim);
            } else {
                builder.with(anim);
            }
        }
        return animSet;
    }

    private static Animator animateScaleX(View v, float target) {
        return ObjectAnimator.ofFloat(v, View.SCALE_X, target);
    }

    private static Animator animateAlpha(View v, float alpha) {
        return ObjectAnimator.ofFloat(v, View.ALPHA, alpha);
    }

    private static Animator animateBounds(View v, Rect bounds) {
        PropertyValuesHolder left = PropertyValuesHolder.ofInt(LEFT, bounds.left);
        PropertyValuesHolder top = PropertyValuesHolder.ofInt(TOP, bounds.top);
        PropertyValuesHolder right = PropertyValuesHolder.ofInt(RIGHT, bounds.right);
        PropertyValuesHolder bottom = PropertyValuesHolder.ofInt(BOTTOM, bounds.bottom);
        return ObjectAnimator.ofPropertyValuesHolder(v, left, top, right, bottom);
    }
}