package android.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.IBinder;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import com.android.internal.R;
import java.lang.ref.WeakReference;

/* loaded from: PopupWindow.class */
public class PopupWindow {
    public static final int INPUT_METHOD_FROM_FOCUSABLE = 0;
    public static final int INPUT_METHOD_NEEDED = 1;
    public static final int INPUT_METHOD_NOT_NEEDED = 2;
    private static final int DEFAULT_ANCHORED_GRAVITY = 8388659;
    private Context mContext;
    private WindowManager mWindowManager;
    private boolean mIsShowing;
    private boolean mIsDropdown;
    private View mContentView;
    private View mPopupView;
    private boolean mFocusable;
    private int mInputMethodMode;
    private int mSoftInputMode;
    private boolean mTouchable;
    private boolean mOutsideTouchable;
    private boolean mClippingEnabled;
    private int mSplitTouchEnabled;
    private boolean mLayoutInScreen;
    private boolean mClipToScreen;
    private boolean mAllowScrollingAnchorParent;
    private boolean mLayoutInsetDecor;
    private boolean mNotTouchModal;
    private View.OnTouchListener mTouchInterceptor;
    private int mWidthMode;
    private int mWidth;
    private int mLastWidth;
    private int mHeightMode;
    private int mHeight;
    private int mLastHeight;
    private int mPopupWidth;
    private int mPopupHeight;
    private int[] mDrawingLocation;
    private int[] mScreenLocation;
    private Rect mTempRect;
    private Drawable mBackground;
    private Drawable mAboveAnchorBackgroundDrawable;
    private Drawable mBelowAnchorBackgroundDrawable;
    private boolean mAboveAnchor;
    private int mWindowLayoutType;
    private OnDismissListener mOnDismissListener;
    private boolean mIgnoreCheekPress;
    private int mAnimationStyle;
    private static final int[] ABOVE_ANCHOR_STATE_SET = {16842922};
    private WeakReference<View> mAnchor;
    private ViewTreeObserver.OnScrollChangedListener mOnScrollChangedListener;
    private int mAnchorXoff;
    private int mAnchorYoff;
    private int mAnchoredGravity;
    private boolean mPopupViewInitialLayoutDirectionInherited;

    /* loaded from: PopupWindow$OnDismissListener.class */
    public interface OnDismissListener {
        void onDismiss();
    }

    public PopupWindow(Context context) {
        this(context, (AttributeSet) null);
    }

    public PopupWindow(Context context, AttributeSet attrs) {
        this(context, attrs, 16842870);
    }

    public PopupWindow(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, defStyle, 0);
    }

    public PopupWindow(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this.mInputMethodMode = 0;
        this.mSoftInputMode = 1;
        this.mTouchable = true;
        this.mOutsideTouchable = false;
        this.mClippingEnabled = true;
        this.mSplitTouchEnabled = -1;
        this.mAllowScrollingAnchorParent = true;
        this.mLayoutInsetDecor = false;
        this.mDrawingLocation = new int[2];
        this.mScreenLocation = new int[2];
        this.mTempRect = new Rect();
        this.mWindowLayoutType = 1000;
        this.mIgnoreCheekPress = false;
        this.mAnimationStyle = -1;
        this.mOnScrollChangedListener = new ViewTreeObserver.OnScrollChangedListener() { // from class: android.widget.PopupWindow.1
            @Override // android.view.ViewTreeObserver.OnScrollChangedListener
            public void onScrollChanged() {
                View anchor = PopupWindow.this.mAnchor != null ? (View) PopupWindow.this.mAnchor.get() : null;
                if (anchor != null && PopupWindow.this.mPopupView != null) {
                    WindowManager.LayoutParams p = (WindowManager.LayoutParams) PopupWindow.this.mPopupView.getLayoutParams();
                    PopupWindow.this.updateAboveAnchor(PopupWindow.this.findDropDownPosition(anchor, p, PopupWindow.this.mAnchorXoff, PopupWindow.this.mAnchorYoff, PopupWindow.this.mAnchoredGravity));
                    PopupWindow.this.update(p.x, p.y, -1, -1, true);
                }
            }
        };
        this.mContext = context;
        this.mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PopupWindow, defStyleAttr, defStyleRes);
        this.mBackground = a.getDrawable(0);
        int animStyle = a.getResourceId(1, -1);
        this.mAnimationStyle = animStyle == 16974326 ? -1 : animStyle;
        if (this.mBackground instanceof StateListDrawable) {
            StateListDrawable background = (StateListDrawable) this.mBackground;
            int aboveAnchorStateIndex = background.getStateDrawableIndex(ABOVE_ANCHOR_STATE_SET);
            int count = background.getStateCount();
            int belowAnchorStateIndex = -1;
            int i = 0;
            while (true) {
                if (i >= count) {
                    break;
                } else if (i == aboveAnchorStateIndex) {
                    i++;
                } else {
                    belowAnchorStateIndex = i;
                    break;
                }
            }
            if (aboveAnchorStateIndex != -1 && belowAnchorStateIndex != -1) {
                this.mAboveAnchorBackgroundDrawable = background.getStateDrawable(aboveAnchorStateIndex);
                this.mBelowAnchorBackgroundDrawable = background.getStateDrawable(belowAnchorStateIndex);
            } else {
                this.mBelowAnchorBackgroundDrawable = null;
                this.mAboveAnchorBackgroundDrawable = null;
            }
        }
        a.recycle();
    }

    public PopupWindow() {
        this((View) null, 0, 0);
    }

    public PopupWindow(View contentView) {
        this(contentView, 0, 0);
    }

    public PopupWindow(int width, int height) {
        this((View) null, width, height);
    }

    public PopupWindow(View contentView, int width, int height) {
        this(contentView, width, height, false);
    }

    public PopupWindow(View contentView, int width, int height, boolean focusable) {
        this.mInputMethodMode = 0;
        this.mSoftInputMode = 1;
        this.mTouchable = true;
        this.mOutsideTouchable = false;
        this.mClippingEnabled = true;
        this.mSplitTouchEnabled = -1;
        this.mAllowScrollingAnchorParent = true;
        this.mLayoutInsetDecor = false;
        this.mDrawingLocation = new int[2];
        this.mScreenLocation = new int[2];
        this.mTempRect = new Rect();
        this.mWindowLayoutType = 1000;
        this.mIgnoreCheekPress = false;
        this.mAnimationStyle = -1;
        this.mOnScrollChangedListener = new ViewTreeObserver.OnScrollChangedListener() { // from class: android.widget.PopupWindow.1
            @Override // android.view.ViewTreeObserver.OnScrollChangedListener
            public void onScrollChanged() {
                View anchor = PopupWindow.this.mAnchor != null ? (View) PopupWindow.this.mAnchor.get() : null;
                if (anchor != null && PopupWindow.this.mPopupView != null) {
                    WindowManager.LayoutParams p = (WindowManager.LayoutParams) PopupWindow.this.mPopupView.getLayoutParams();
                    PopupWindow.this.updateAboveAnchor(PopupWindow.this.findDropDownPosition(anchor, p, PopupWindow.this.mAnchorXoff, PopupWindow.this.mAnchorYoff, PopupWindow.this.mAnchoredGravity));
                    PopupWindow.this.update(p.x, p.y, -1, -1, true);
                }
            }
        };
        if (contentView != null) {
            this.mContext = contentView.getContext();
            this.mWindowManager = (WindowManager) this.mContext.getSystemService(Context.WINDOW_SERVICE);
        }
        setContentView(contentView);
        setWidth(width);
        setHeight(height);
        setFocusable(focusable);
    }

    public Drawable getBackground() {
        return this.mBackground;
    }

    public void setBackgroundDrawable(Drawable background) {
        this.mBackground = background;
    }

    public int getAnimationStyle() {
        return this.mAnimationStyle;
    }

    public void setIgnoreCheekPress() {
        this.mIgnoreCheekPress = true;
    }

    public void setAnimationStyle(int animationStyle) {
        this.mAnimationStyle = animationStyle;
    }

    public View getContentView() {
        return this.mContentView;
    }

    public void setContentView(View contentView) {
        if (isShowing()) {
            return;
        }
        this.mContentView = contentView;
        if (this.mContext == null && this.mContentView != null) {
            this.mContext = this.mContentView.getContext();
        }
        if (this.mWindowManager == null && this.mContentView != null) {
            this.mWindowManager = (WindowManager) this.mContext.getSystemService(Context.WINDOW_SERVICE);
        }
    }

    public void setTouchInterceptor(View.OnTouchListener l) {
        this.mTouchInterceptor = l;
    }

    public boolean isFocusable() {
        return this.mFocusable;
    }

    public void setFocusable(boolean focusable) {
        this.mFocusable = focusable;
    }

    public int getInputMethodMode() {
        return this.mInputMethodMode;
    }

    public void setInputMethodMode(int mode) {
        this.mInputMethodMode = mode;
    }

    public void setSoftInputMode(int mode) {
        this.mSoftInputMode = mode;
    }

    public int getSoftInputMode() {
        return this.mSoftInputMode;
    }

    public boolean isTouchable() {
        return this.mTouchable;
    }

    public void setTouchable(boolean touchable) {
        this.mTouchable = touchable;
    }

    public boolean isOutsideTouchable() {
        return this.mOutsideTouchable;
    }

    public void setOutsideTouchable(boolean touchable) {
        this.mOutsideTouchable = touchable;
    }

    public boolean isClippingEnabled() {
        return this.mClippingEnabled;
    }

    public void setClippingEnabled(boolean enabled) {
        this.mClippingEnabled = enabled;
    }

    public void setClipToScreenEnabled(boolean enabled) {
        this.mClipToScreen = enabled;
        setClippingEnabled(!enabled);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setAllowScrollingAnchorParent(boolean enabled) {
        this.mAllowScrollingAnchorParent = enabled;
    }

    public boolean isSplitTouchEnabled() {
        return (this.mSplitTouchEnabled >= 0 || this.mContext == null) ? this.mSplitTouchEnabled == 1 : this.mContext.getApplicationInfo().targetSdkVersion >= 11;
    }

    public void setSplitTouchEnabled(boolean enabled) {
        this.mSplitTouchEnabled = enabled ? 1 : 0;
    }

    public boolean isLayoutInScreenEnabled() {
        return this.mLayoutInScreen;
    }

    public void setLayoutInScreenEnabled(boolean enabled) {
        this.mLayoutInScreen = enabled;
    }

    public void setLayoutInsetDecor(boolean enabled) {
        this.mLayoutInsetDecor = enabled;
    }

    public void setWindowLayoutType(int layoutType) {
        this.mWindowLayoutType = layoutType;
    }

    public int getWindowLayoutType() {
        return this.mWindowLayoutType;
    }

    public void setTouchModal(boolean touchModal) {
        this.mNotTouchModal = !touchModal;
    }

    public void setWindowLayoutMode(int widthSpec, int heightSpec) {
        this.mWidthMode = widthSpec;
        this.mHeightMode = heightSpec;
    }

    public int getHeight() {
        return this.mHeight;
    }

    public void setHeight(int height) {
        this.mHeight = height;
    }

    public int getWidth() {
        return this.mWidth;
    }

    public void setWidth(int width) {
        this.mWidth = width;
    }

    public boolean isShowing() {
        return this.mIsShowing;
    }

    public void showAtLocation(View parent, int gravity, int x, int y) {
        showAtLocation(parent.getWindowToken(), gravity, x, y);
    }

    public void showAtLocation(IBinder token, int gravity, int x, int y) {
        if (isShowing() || this.mContentView == null) {
            return;
        }
        unregisterForScrollChanged();
        this.mIsShowing = true;
        this.mIsDropdown = false;
        WindowManager.LayoutParams p = createPopupLayout(token);
        p.windowAnimations = computeAnimationResource();
        preparePopup(p);
        if (gravity == 0) {
            gravity = DEFAULT_ANCHORED_GRAVITY;
        }
        p.gravity = gravity;
        p.x = x;
        p.y = y;
        if (this.mHeightMode < 0) {
            int i = this.mHeightMode;
            this.mLastHeight = i;
            p.height = i;
        }
        if (this.mWidthMode < 0) {
            int i2 = this.mWidthMode;
            this.mLastWidth = i2;
            p.width = i2;
        }
        invokePopup(p);
    }

    public void showAsDropDown(View anchor) {
        showAsDropDown(anchor, 0, 0);
    }

    public void showAsDropDown(View anchor, int xoff, int yoff) {
        showAsDropDown(anchor, xoff, yoff, DEFAULT_ANCHORED_GRAVITY);
    }

    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        if (isShowing() || this.mContentView == null) {
            return;
        }
        registerForScrollChanged(anchor, xoff, yoff, gravity);
        this.mIsShowing = true;
        this.mIsDropdown = true;
        WindowManager.LayoutParams p = createPopupLayout(anchor.getWindowToken());
        preparePopup(p);
        updateAboveAnchor(findDropDownPosition(anchor, p, xoff, yoff, gravity));
        if (this.mHeightMode < 0) {
            int i = this.mHeightMode;
            this.mLastHeight = i;
            p.height = i;
        }
        if (this.mWidthMode < 0) {
            int i2 = this.mWidthMode;
            this.mLastWidth = i2;
            p.width = i2;
        }
        p.windowAnimations = computeAnimationResource();
        invokePopup(p);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAboveAnchor(boolean aboveAnchor) {
        if (aboveAnchor != this.mAboveAnchor) {
            this.mAboveAnchor = aboveAnchor;
            if (this.mBackground != null) {
                if (this.mAboveAnchorBackgroundDrawable != null) {
                    if (this.mAboveAnchor) {
                        this.mPopupView.setBackgroundDrawable(this.mAboveAnchorBackgroundDrawable);
                        return;
                    } else {
                        this.mPopupView.setBackgroundDrawable(this.mBelowAnchorBackgroundDrawable);
                        return;
                    }
                }
                this.mPopupView.refreshDrawableState();
            }
        }
    }

    public boolean isAboveAnchor() {
        return this.mAboveAnchor;
    }

    private void preparePopup(WindowManager.LayoutParams p) {
        if (this.mContentView == null || this.mContext == null || this.mWindowManager == null) {
            throw new IllegalStateException("You must specify a valid content view by calling setContentView() before attempting to show the popup.");
        }
        if (this.mBackground != null) {
            ViewGroup.LayoutParams layoutParams = this.mContentView.getLayoutParams();
            int height = -1;
            if (layoutParams != null && layoutParams.height == -2) {
                height = -2;
            }
            PopupViewContainer popupViewContainer = new PopupViewContainer(this.mContext);
            FrameLayout.LayoutParams listParams = new FrameLayout.LayoutParams(-1, height);
            popupViewContainer.setBackgroundDrawable(this.mBackground);
            popupViewContainer.addView(this.mContentView, listParams);
            this.mPopupView = popupViewContainer;
        } else {
            this.mPopupView = this.mContentView;
        }
        this.mPopupViewInitialLayoutDirectionInherited = this.mPopupView.getRawLayoutDirection() == 2;
        this.mPopupWidth = p.width;
        this.mPopupHeight = p.height;
    }

    private void invokePopup(WindowManager.LayoutParams p) {
        if (this.mContext != null) {
            p.packageName = this.mContext.getPackageName();
        }
        this.mPopupView.setFitsSystemWindows(this.mLayoutInsetDecor);
        setLayoutDirectionFromAnchor();
        this.mWindowManager.addView(this.mPopupView, p);
    }

    private void setLayoutDirectionFromAnchor() {
        View anchor;
        if (this.mAnchor != null && (anchor = this.mAnchor.get()) != null && this.mPopupViewInitialLayoutDirectionInherited) {
            this.mPopupView.setLayoutDirection(anchor.getLayoutDirection());
        }
    }

    private WindowManager.LayoutParams createPopupLayout(IBinder token) {
        WindowManager.LayoutParams p = new WindowManager.LayoutParams();
        p.gravity = DEFAULT_ANCHORED_GRAVITY;
        int i = this.mWidth;
        this.mLastWidth = i;
        p.width = i;
        int i2 = this.mHeight;
        this.mLastHeight = i2;
        p.height = i2;
        if (this.mBackground != null) {
            p.format = this.mBackground.getOpacity();
        } else {
            p.format = -3;
        }
        p.flags = computeFlags(p.flags);
        p.type = this.mWindowLayoutType;
        p.token = token;
        p.softInputMode = this.mSoftInputMode;
        p.setTitle("PopupWindow:" + Integer.toHexString(hashCode()));
        return p;
    }

    private int computeFlags(int curFlags) {
        int curFlags2 = curFlags & (-8815129);
        if (this.mIgnoreCheekPress) {
            curFlags2 |= 32768;
        }
        if (!this.mFocusable) {
            curFlags2 |= 8;
            if (this.mInputMethodMode == 1) {
                curFlags2 |= 131072;
            }
        } else if (this.mInputMethodMode == 2) {
            curFlags2 |= 131072;
        }
        if (!this.mTouchable) {
            curFlags2 |= 16;
        }
        if (this.mOutsideTouchable) {
            curFlags2 |= 262144;
        }
        if (!this.mClippingEnabled) {
            curFlags2 |= 512;
        }
        if (isSplitTouchEnabled()) {
            curFlags2 |= 8388608;
        }
        if (this.mLayoutInScreen) {
            curFlags2 |= 256;
        }
        if (this.mLayoutInsetDecor) {
            curFlags2 |= 65536;
        }
        if (this.mNotTouchModal) {
            curFlags2 |= 32;
        }
        return curFlags2;
    }

    private int computeAnimationResource() {
        if (this.mAnimationStyle == -1) {
            if (this.mIsDropdown) {
                return this.mAboveAnchor ? R.style.Animation_DropDownUp : R.style.Animation_DropDownDown;
            }
            return 0;
        }
        return this.mAnimationStyle;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean findDropDownPosition(View anchor, WindowManager.LayoutParams p, int xoff, int yoff, int gravity) {
        int anchorHeight = anchor.getHeight();
        anchor.getLocationInWindow(this.mDrawingLocation);
        p.x = this.mDrawingLocation[0] + xoff;
        p.y = this.mDrawingLocation[1] + anchorHeight + yoff;
        int hgrav = Gravity.getAbsoluteGravity(gravity, anchor.getLayoutDirection()) & 7;
        if (hgrav == 5) {
            p.x -= this.mPopupWidth - anchor.getWidth();
        }
        boolean onTop = false;
        p.gravity = 51;
        anchor.getLocationOnScreen(this.mScreenLocation);
        Rect displayFrame = new Rect();
        anchor.getWindowVisibleDisplayFrame(displayFrame);
        int screenY = this.mScreenLocation[1] + anchorHeight + yoff;
        View root = anchor.getRootView();
        if (screenY + this.mPopupHeight > displayFrame.bottom || (p.x + this.mPopupWidth) - root.getWidth() > 0) {
            if (this.mAllowScrollingAnchorParent) {
                int scrollX = anchor.getScrollX();
                int scrollY = anchor.getScrollY();
                Rect r = new Rect(scrollX, scrollY, scrollX + this.mPopupWidth + xoff, scrollY + this.mPopupHeight + anchor.getHeight() + yoff);
                anchor.requestRectangleOnScreen(r, true);
            }
            anchor.getLocationInWindow(this.mDrawingLocation);
            p.x = this.mDrawingLocation[0] + xoff;
            p.y = this.mDrawingLocation[1] + anchor.getHeight() + yoff;
            if (hgrav == 5) {
                p.x -= this.mPopupWidth - anchor.getWidth();
            }
            anchor.getLocationOnScreen(this.mScreenLocation);
            onTop = ((displayFrame.bottom - this.mScreenLocation[1]) - anchor.getHeight()) - yoff < (this.mScreenLocation[1] - yoff) - displayFrame.top;
            if (onTop) {
                p.gravity = 83;
                p.y = (root.getHeight() - this.mDrawingLocation[1]) + yoff;
            } else {
                p.y = this.mDrawingLocation[1] + anchor.getHeight() + yoff;
            }
        }
        if (this.mClipToScreen) {
            int displayFrameWidth = displayFrame.right - displayFrame.left;
            int right = p.x + p.width;
            if (right > displayFrameWidth) {
                p.x -= right - displayFrameWidth;
            }
            if (p.x < displayFrame.left) {
                p.x = displayFrame.left;
                p.width = Math.min(p.width, displayFrameWidth);
            }
            if (onTop) {
                int popupTop = (this.mScreenLocation[1] + yoff) - this.mPopupHeight;
                if (popupTop < 0) {
                    p.y += popupTop;
                }
            } else {
                p.y = Math.max(p.y, displayFrame.top);
            }
        }
        p.gravity |= 268435456;
        return onTop;
    }

    public int getMaxAvailableHeight(View anchor) {
        return getMaxAvailableHeight(anchor, 0);
    }

    public int getMaxAvailableHeight(View anchor, int yOffset) {
        return getMaxAvailableHeight(anchor, yOffset, false);
    }

    public int getMaxAvailableHeight(View anchor, int yOffset, boolean ignoreBottomDecorations) {
        Rect displayFrame = new Rect();
        anchor.getWindowVisibleDisplayFrame(displayFrame);
        int[] anchorPos = this.mDrawingLocation;
        anchor.getLocationOnScreen(anchorPos);
        int bottomEdge = displayFrame.bottom;
        if (ignoreBottomDecorations) {
            Resources res = anchor.getContext().getResources();
            bottomEdge = res.getDisplayMetrics().heightPixels;
        }
        int distanceToBottom = (bottomEdge - (anchorPos[1] + anchor.getHeight())) - yOffset;
        int distanceToTop = (anchorPos[1] - displayFrame.top) + yOffset;
        int returnedHeight = Math.max(distanceToBottom, distanceToTop);
        if (this.mBackground != null) {
            this.mBackground.getPadding(this.mTempRect);
            returnedHeight -= this.mTempRect.top + this.mTempRect.bottom;
        }
        return returnedHeight;
    }

    public void dismiss() {
        if (isShowing() && this.mPopupView != null) {
            this.mIsShowing = false;
            unregisterForScrollChanged();
            try {
                this.mWindowManager.removeViewImmediate(this.mPopupView);
                if (this.mPopupView != this.mContentView && (this.mPopupView instanceof ViewGroup)) {
                    ((ViewGroup) this.mPopupView).removeView(this.mContentView);
                }
                this.mPopupView = null;
                if (this.mOnDismissListener != null) {
                    this.mOnDismissListener.onDismiss();
                }
            } catch (Throwable th) {
                if (this.mPopupView != this.mContentView && (this.mPopupView instanceof ViewGroup)) {
                    ((ViewGroup) this.mPopupView).removeView(this.mContentView);
                }
                this.mPopupView = null;
                if (this.mOnDismissListener != null) {
                    this.mOnDismissListener.onDismiss();
                }
                throw th;
            }
        }
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.mOnDismissListener = onDismissListener;
    }

    public void update() {
        if (!isShowing() || this.mContentView == null) {
            return;
        }
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) this.mPopupView.getLayoutParams();
        boolean update = false;
        int newAnim = computeAnimationResource();
        if (newAnim != p.windowAnimations) {
            p.windowAnimations = newAnim;
            update = true;
        }
        int newFlags = computeFlags(p.flags);
        if (newFlags != p.flags) {
            p.flags = newFlags;
            update = true;
        }
        if (update) {
            setLayoutDirectionFromAnchor();
            this.mWindowManager.updateViewLayout(this.mPopupView, p);
        }
    }

    public void update(int width, int height) {
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) this.mPopupView.getLayoutParams();
        update(p.x, p.y, width, height, false);
    }

    public void update(int x, int y, int width, int height) {
        update(x, y, width, height, false);
    }

    public void update(int x, int y, int width, int height, boolean force) {
        if (width != -1) {
            this.mLastWidth = width;
            setWidth(width);
        }
        if (height != -1) {
            this.mLastHeight = height;
            setHeight(height);
        }
        if (!isShowing() || this.mContentView == null) {
            return;
        }
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) this.mPopupView.getLayoutParams();
        boolean update = force;
        int finalWidth = this.mWidthMode < 0 ? this.mWidthMode : this.mLastWidth;
        if (width != -1 && p.width != finalWidth) {
            this.mLastWidth = finalWidth;
            p.width = finalWidth;
            update = true;
        }
        int finalHeight = this.mHeightMode < 0 ? this.mHeightMode : this.mLastHeight;
        if (height != -1 && p.height != finalHeight) {
            this.mLastHeight = finalHeight;
            p.height = finalHeight;
            update = true;
        }
        if (p.x != x) {
            p.x = x;
            update = true;
        }
        if (p.y != y) {
            p.y = y;
            update = true;
        }
        int newAnim = computeAnimationResource();
        if (newAnim != p.windowAnimations) {
            p.windowAnimations = newAnim;
            update = true;
        }
        int newFlags = computeFlags(p.flags);
        if (newFlags != p.flags) {
            p.flags = newFlags;
            update = true;
        }
        if (update) {
            setLayoutDirectionFromAnchor();
            this.mWindowManager.updateViewLayout(this.mPopupView, p);
        }
    }

    public void update(View anchor, int width, int height) {
        update(anchor, false, 0, 0, true, width, height, this.mAnchoredGravity);
    }

    public void update(View anchor, int xoff, int yoff, int width, int height) {
        update(anchor, true, xoff, yoff, true, width, height, this.mAnchoredGravity);
    }

    private void update(View anchor, boolean updateLocation, int xoff, int yoff, boolean updateDimension, int width, int height, int gravity) {
        if (!isShowing() || this.mContentView == null) {
            return;
        }
        WeakReference<View> oldAnchor = this.mAnchor;
        boolean needsUpdate = updateLocation && !(this.mAnchorXoff == xoff && this.mAnchorYoff == yoff);
        if (oldAnchor == null || oldAnchor.get() != anchor || (needsUpdate && !this.mIsDropdown)) {
            registerForScrollChanged(anchor, xoff, yoff, gravity);
        } else if (needsUpdate) {
            this.mAnchorXoff = xoff;
            this.mAnchorYoff = yoff;
            this.mAnchoredGravity = gravity;
        }
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) this.mPopupView.getLayoutParams();
        if (updateDimension) {
            if (width == -1) {
                width = this.mPopupWidth;
            } else {
                this.mPopupWidth = width;
            }
            if (height == -1) {
                height = this.mPopupHeight;
            } else {
                this.mPopupHeight = height;
            }
        }
        int x = p.x;
        int y = p.y;
        if (updateLocation) {
            updateAboveAnchor(findDropDownPosition(anchor, p, xoff, yoff, gravity));
        } else {
            updateAboveAnchor(findDropDownPosition(anchor, p, this.mAnchorXoff, this.mAnchorYoff, this.mAnchoredGravity));
        }
        update(p.x, p.y, width, height, (x == p.x && y == p.y) ? false : true);
    }

    private void unregisterForScrollChanged() {
        WeakReference<View> anchorRef = this.mAnchor;
        View anchor = null;
        if (anchorRef != null) {
            anchor = anchorRef.get();
        }
        if (anchor != null) {
            ViewTreeObserver vto = anchor.getViewTreeObserver();
            vto.removeOnScrollChangedListener(this.mOnScrollChangedListener);
        }
        this.mAnchor = null;
    }

    private void registerForScrollChanged(View anchor, int xoff, int yoff, int gravity) {
        unregisterForScrollChanged();
        this.mAnchor = new WeakReference<>(anchor);
        ViewTreeObserver vto = anchor.getViewTreeObserver();
        if (vto != null) {
            vto.addOnScrollChangedListener(this.mOnScrollChangedListener);
        }
        this.mAnchorXoff = xoff;
        this.mAnchorYoff = yoff;
        this.mAnchoredGravity = gravity;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: PopupWindow$PopupViewContainer.class */
    public class PopupViewContainer extends FrameLayout {
        private static final String TAG = "PopupWindow.PopupViewContainer";

        public PopupViewContainer(Context context) {
            super(context);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.view.ViewGroup, android.view.View
        public int[] onCreateDrawableState(int extraSpace) {
            if (PopupWindow.this.mAboveAnchor) {
                int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
                View.mergeDrawableStates(drawableState, PopupWindow.ABOVE_ANCHOR_STATE_SET);
                return drawableState;
            }
            return super.onCreateDrawableState(extraSpace);
        }

        @Override // android.view.ViewGroup, android.view.View
        public boolean dispatchKeyEvent(KeyEvent event) {
            KeyEvent.DispatcherState state;
            if (event.getKeyCode() == 4) {
                if (getKeyDispatcherState() == null) {
                    return super.dispatchKeyEvent(event);
                }
                if (event.getAction() == 0 && event.getRepeatCount() == 0) {
                    KeyEvent.DispatcherState state2 = getKeyDispatcherState();
                    if (state2 != null) {
                        state2.startTracking(event, this);
                        return true;
                    }
                    return true;
                } else if (event.getAction() == 1 && (state = getKeyDispatcherState()) != null && state.isTracking(event) && !event.isCanceled()) {
                    PopupWindow.this.dismiss();
                    return true;
                } else {
                    return super.dispatchKeyEvent(event);
                }
            }
            return super.dispatchKeyEvent(event);
        }

        @Override // android.view.ViewGroup, android.view.View
        public boolean dispatchTouchEvent(MotionEvent ev) {
            if (PopupWindow.this.mTouchInterceptor != null && PopupWindow.this.mTouchInterceptor.onTouch(this, ev)) {
                return true;
            }
            return super.dispatchTouchEvent(ev);
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            if (event.getAction() == 0 && (x < 0 || x >= getWidth() || y < 0 || y >= getHeight())) {
                PopupWindow.this.dismiss();
                return true;
            } else if (event.getAction() == 4) {
                PopupWindow.this.dismiss();
                return true;
            } else {
                return super.onTouchEvent(event);
            }
        }

        @Override // android.view.View, android.view.accessibility.AccessibilityEventSource
        public void sendAccessibilityEvent(int eventType) {
            if (PopupWindow.this.mContentView != null) {
                PopupWindow.this.mContentView.sendAccessibilityEvent(eventType);
            } else {
                super.sendAccessibilityEvent(eventType);
            }
        }
    }
}