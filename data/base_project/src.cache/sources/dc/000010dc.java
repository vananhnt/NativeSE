package android.support.v4.view;

import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.view.accessibility.AccessibilityNodeProviderCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.WeakHashMap;

/* loaded from: ViewCompat.class */
public class ViewCompat {
    public static final int ACCESSIBILITY_LIVE_REGION_ASSERTIVE = 2;
    public static final int ACCESSIBILITY_LIVE_REGION_NONE = 0;
    public static final int ACCESSIBILITY_LIVE_REGION_POLITE = 1;
    private static final long FAKE_FRAME_TIME = 10;
    static final ViewCompatImpl IMPL;
    public static final int IMPORTANT_FOR_ACCESSIBILITY_AUTO = 0;
    public static final int IMPORTANT_FOR_ACCESSIBILITY_NO = 2;
    public static final int IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS = 4;
    public static final int IMPORTANT_FOR_ACCESSIBILITY_YES = 1;
    public static final int LAYER_TYPE_HARDWARE = 2;
    public static final int LAYER_TYPE_NONE = 0;
    public static final int LAYER_TYPE_SOFTWARE = 1;
    public static final int LAYOUT_DIRECTION_INHERIT = 2;
    public static final int LAYOUT_DIRECTION_LOCALE = 3;
    public static final int LAYOUT_DIRECTION_LTR = 0;
    public static final int LAYOUT_DIRECTION_RTL = 1;
    public static final int MEASURED_HEIGHT_STATE_SHIFT = 16;
    public static final int MEASURED_SIZE_MASK = 16777215;
    public static final int MEASURED_STATE_MASK = -16777216;
    public static final int MEASURED_STATE_TOO_SMALL = 16777216;
    public static final int OVER_SCROLL_ALWAYS = 0;
    public static final int OVER_SCROLL_IF_CONTENT_SCROLLS = 1;
    public static final int OVER_SCROLL_NEVER = 2;
    private static final String TAG = "ViewCompat";

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({0, 1, 2})
    /* loaded from: ViewCompat$AccessibilityLiveRegion.class */
    private @interface AccessibilityLiveRegion {
    }

    /* loaded from: ViewCompat$Api21ViewCompatImpl.class */
    static class Api21ViewCompatImpl extends KitKatViewCompatImpl {
        Api21ViewCompatImpl() {
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public float getElevation(View view) {
            return ViewCompatApi21.getElevation(view);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public String getTransitionName(View view) {
            return ViewCompatApi21.getTransitionName(view);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public float getTranslationZ(View view) {
            return ViewCompatApi21.getTranslationZ(view);
        }

        @Override // android.support.v4.view.ViewCompat.JBViewCompatImpl, android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public void requestApplyInsets(View view) {
            ViewCompatApi21.requestApplyInsets(view);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setElevation(View view, float f) {
            ViewCompatApi21.setElevation(view, f);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setOnApplyWindowInsetsListener(View view, OnApplyWindowInsetsListener onApplyWindowInsetsListener) {
            ViewCompatApi21.setOnApplyWindowInsetsListener(view, onApplyWindowInsetsListener);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setTransitionName(View view, String str) {
            ViewCompatApi21.setTransitionName(view, str);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setTranslationZ(View view, float f) {
            ViewCompatApi21.setTranslationZ(view, f);
        }
    }

    /* loaded from: ViewCompat$BaseViewCompatImpl.class */
    static class BaseViewCompatImpl implements ViewCompatImpl {
        private Method mDispatchFinishTemporaryDetach;
        private Method mDispatchStartTemporaryDetach;
        private boolean mTempDetachBound;
        WeakHashMap<View, ViewPropertyAnimatorCompat> mViewPropertyAnimatorCompatMap = null;

        BaseViewCompatImpl() {
        }

        private void bindTempDetach() {
            try {
                this.mDispatchStartTemporaryDetach = View.class.getDeclaredMethod("dispatchStartTemporaryDetach", new Class[0]);
                this.mDispatchFinishTemporaryDetach = View.class.getDeclaredMethod("dispatchFinishTemporaryDetach", new Class[0]);
            } catch (NoSuchMethodException e) {
                Log.e("ViewCompat", "Couldn't find method", e);
            }
            this.mTempDetachBound = true;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public ViewPropertyAnimatorCompat animate(View view) {
            return new ViewPropertyAnimatorCompat(view);
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public boolean canScrollHorizontally(View view, int i) {
            return false;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public boolean canScrollVertically(View view, int i) {
            return false;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void dispatchFinishTemporaryDetach(View view) {
            if (!this.mTempDetachBound) {
                bindTempDetach();
            }
            Method method = this.mDispatchFinishTemporaryDetach;
            if (method == null) {
                view.onFinishTemporaryDetach();
                return;
            }
            try {
                method.invoke(view, new Object[0]);
            } catch (Exception e) {
                Log.d("ViewCompat", "Error calling dispatchFinishTemporaryDetach", e);
            }
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void dispatchStartTemporaryDetach(View view) {
            if (!this.mTempDetachBound) {
                bindTempDetach();
            }
            Method method = this.mDispatchStartTemporaryDetach;
            if (method == null) {
                view.onStartTemporaryDetach();
                return;
            }
            try {
                method.invoke(view, new Object[0]);
            } catch (Exception e) {
                Log.d("ViewCompat", "Error calling dispatchStartTemporaryDetach", e);
            }
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public int getAccessibilityLiveRegion(View view) {
            return 0;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public AccessibilityNodeProviderCompat getAccessibilityNodeProvider(View view) {
            return null;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public float getAlpha(View view) {
            return 1.0f;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public float getElevation(View view) {
            return 0.0f;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public boolean getFitsSystemWindows(View view) {
            return false;
        }

        long getFrameTime() {
            return ViewCompat.FAKE_FRAME_TIME;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public int getImportantForAccessibility(View view) {
            return 0;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public int getLabelFor(View view) {
            return 0;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public int getLayerType(View view) {
            return 0;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public int getLayoutDirection(View view) {
            return 0;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public int getMeasuredHeightAndState(View view) {
            return view.getMeasuredHeight();
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public int getMeasuredState(View view) {
            return 0;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public int getMeasuredWidthAndState(View view) {
            return view.getMeasuredWidth();
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public int getMinimumHeight(View view) {
            return 0;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public int getMinimumWidth(View view) {
            return 0;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public int getOverScrollMode(View view) {
            return 2;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public int getPaddingEnd(View view) {
            return view.getPaddingRight();
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public int getPaddingStart(View view) {
            return view.getPaddingLeft();
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public ViewParent getParentForAccessibility(View view) {
            return view.getParent();
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public float getPivotX(View view) {
            return 0.0f;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public float getPivotY(View view) {
            return 0.0f;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public float getRotation(View view) {
            return 0.0f;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public float getRotationX(View view) {
            return 0.0f;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public float getRotationY(View view) {
            return 0.0f;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public float getScaleX(View view) {
            return 0.0f;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public float getScaleY(View view) {
            return 0.0f;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public String getTransitionName(View view) {
            return null;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public float getTranslationX(View view) {
            return 0.0f;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public float getTranslationY(View view) {
            return 0.0f;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public float getTranslationZ(View view) {
            return 0.0f;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public int getWindowSystemUiVisibility(View view) {
            return 0;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public float getX(View view) {
            return 0.0f;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public float getY(View view) {
            return 0.0f;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public boolean hasAccessibilityDelegate(View view) {
            return false;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public boolean hasTransientState(View view) {
            return false;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public boolean isOpaque(View view) {
            Drawable background = view.getBackground();
            boolean z = false;
            if (background != null) {
                if (background.getOpacity() == -1) {
                    z = true;
                }
                return z;
            }
            return false;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void jumpDrawablesToCurrentState(View view) {
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void onInitializeAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void onPopulateAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public boolean performAccessibilityAction(View view, int i, Bundle bundle) {
            return false;
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void postInvalidateOnAnimation(View view) {
            view.invalidate();
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void postInvalidateOnAnimation(View view, int i, int i2, int i3, int i4) {
            view.invalidate(i, i2, i3, i4);
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void postOnAnimation(View view, Runnable runnable) {
            view.postDelayed(runnable, getFrameTime());
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void postOnAnimationDelayed(View view, Runnable runnable, long j) {
            view.postDelayed(runnable, getFrameTime() + j);
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void requestApplyInsets(View view) {
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public int resolveSizeAndState(int i, int i2, int i3) {
            return View.resolveSize(i, i2);
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setAccessibilityDelegate(View view, AccessibilityDelegateCompat accessibilityDelegateCompat) {
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setAccessibilityLiveRegion(View view, int i) {
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setAlpha(View view, float f) {
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setChildrenDrawingOrderEnabled(ViewGroup viewGroup, boolean z) {
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setElevation(View view, float f) {
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setHasTransientState(View view, boolean z) {
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setImportantForAccessibility(View view, int i) {
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setLabelFor(View view, int i) {
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setLayerPaint(View view, Paint paint) {
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setLayerType(View view, int i, Paint paint) {
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setLayoutDirection(View view, int i) {
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setOnApplyWindowInsetsListener(View view, OnApplyWindowInsetsListener onApplyWindowInsetsListener) {
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setOverScrollMode(View view, int i) {
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setPaddingRelative(View view, int i, int i2, int i3, int i4) {
            view.setPadding(i, i2, i3, i4);
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setPivotX(View view, float f) {
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setPivotY(View view, float f) {
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setRotation(View view, float f) {
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setRotationX(View view, float f) {
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setRotationY(View view, float f) {
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setScaleX(View view, float f) {
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setScaleY(View view, float f) {
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setTransitionName(View view, String str) {
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setTranslationX(View view, float f) {
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setTranslationY(View view, float f) {
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setTranslationZ(View view, float f) {
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setX(View view, float f) {
        }

        @Override // android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setY(View view, float f) {
        }
    }

    /* loaded from: ViewCompat$EclairMr1ViewCompatImpl.class */
    static class EclairMr1ViewCompatImpl extends BaseViewCompatImpl {
        EclairMr1ViewCompatImpl() {
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public boolean isOpaque(View view) {
            return ViewCompatEclairMr1.isOpaque(view);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setChildrenDrawingOrderEnabled(ViewGroup viewGroup, boolean z) {
            ViewCompatEclairMr1.setChildrenDrawingOrderEnabled(viewGroup, z);
        }
    }

    /* loaded from: ViewCompat$GBViewCompatImpl.class */
    static class GBViewCompatImpl extends EclairMr1ViewCompatImpl {
        GBViewCompatImpl() {
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public int getOverScrollMode(View view) {
            return ViewCompatGingerbread.getOverScrollMode(view);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setOverScrollMode(View view, int i) {
            ViewCompatGingerbread.setOverScrollMode(view, i);
        }
    }

    /* loaded from: ViewCompat$HCViewCompatImpl.class */
    static class HCViewCompatImpl extends GBViewCompatImpl {
        HCViewCompatImpl() {
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public float getAlpha(View view) {
            return ViewCompatHC.getAlpha(view);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl
        long getFrameTime() {
            return ViewCompatHC.getFrameTime();
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public int getLayerType(View view) {
            return ViewCompatHC.getLayerType(view);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public int getMeasuredHeightAndState(View view) {
            return ViewCompatHC.getMeasuredHeightAndState(view);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public int getMeasuredState(View view) {
            return ViewCompatHC.getMeasuredState(view);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public int getMeasuredWidthAndState(View view) {
            return ViewCompatHC.getMeasuredWidthAndState(view);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public float getPivotX(View view) {
            return ViewCompatHC.getPivotX(view);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public float getPivotY(View view) {
            return ViewCompatHC.getPivotY(view);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public float getRotation(View view) {
            return ViewCompatHC.getRotation(view);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public float getRotationX(View view) {
            return ViewCompatHC.getRotationX(view);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public float getRotationY(View view) {
            return ViewCompatHC.getRotationY(view);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public float getScaleX(View view) {
            return ViewCompatHC.getScaleX(view);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public float getScaleY(View view) {
            return ViewCompatHC.getScaleY(view);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public float getTranslationX(View view) {
            return ViewCompatHC.getTranslationX(view);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public float getTranslationY(View view) {
            return ViewCompatHC.getTranslationY(view);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public float getX(View view) {
            return ViewCompatHC.getX(view);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public float getY(View view) {
            return ViewCompatHC.getY(view);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public void jumpDrawablesToCurrentState(View view) {
            ViewCompatHC.jumpDrawablesToCurrentState(view);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public int resolveSizeAndState(int i, int i2, int i3) {
            return ViewCompatHC.resolveSizeAndState(i, i2, i3);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setAlpha(View view, float f) {
            ViewCompatHC.setAlpha(view, f);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setLayerPaint(View view, Paint paint) {
            setLayerType(view, getLayerType(view), paint);
            view.invalidate();
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setLayerType(View view, int i, Paint paint) {
            ViewCompatHC.setLayerType(view, i, paint);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setPivotX(View view, float f) {
            ViewCompatHC.setPivotX(view, f);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setPivotY(View view, float f) {
            ViewCompatHC.setPivotY(view, f);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setRotation(View view, float f) {
            ViewCompatHC.setRotation(view, f);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setRotationX(View view, float f) {
            ViewCompatHC.setRotationX(view, f);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setRotationY(View view, float f) {
            ViewCompatHC.setRotationY(view, f);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setScaleX(View view, float f) {
            ViewCompatHC.setScaleX(view, f);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setScaleY(View view, float f) {
            ViewCompatHC.setScaleY(view, f);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setTranslationX(View view, float f) {
            ViewCompatHC.setTranslationX(view, f);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setTranslationY(View view, float f) {
            ViewCompatHC.setTranslationY(view, f);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setX(View view, float f) {
            ViewCompatHC.setX(view, f);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setY(View view, float f) {
            ViewCompatHC.setY(view, f);
        }
    }

    /* loaded from: ViewCompat$ICSViewCompatImpl.class */
    static class ICSViewCompatImpl extends HCViewCompatImpl {
        static boolean accessibilityDelegateCheckFailed = false;
        static Field mAccessibilityDelegateField;

        ICSViewCompatImpl() {
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public ViewPropertyAnimatorCompat animate(View view) {
            ViewPropertyAnimatorCompat viewPropertyAnimatorCompat;
            if (this.mViewPropertyAnimatorCompatMap == null) {
                this.mViewPropertyAnimatorCompatMap = new WeakHashMap<>();
            }
            ViewPropertyAnimatorCompat viewPropertyAnimatorCompat2 = this.mViewPropertyAnimatorCompatMap.get(view);
            if (viewPropertyAnimatorCompat2 == null) {
                ViewPropertyAnimatorCompat viewPropertyAnimatorCompat3 = new ViewPropertyAnimatorCompat(view);
                this.mViewPropertyAnimatorCompatMap.put(view, viewPropertyAnimatorCompat3);
                viewPropertyAnimatorCompat = viewPropertyAnimatorCompat3;
            } else {
                viewPropertyAnimatorCompat = viewPropertyAnimatorCompat2;
            }
            return viewPropertyAnimatorCompat;
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public boolean canScrollHorizontally(View view, int i) {
            return ViewCompatICS.canScrollHorizontally(view, i);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public boolean canScrollVertically(View view, int i) {
            return ViewCompatICS.canScrollVertically(view, i);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public boolean hasAccessibilityDelegate(View view) {
            boolean z = false;
            if (accessibilityDelegateCheckFailed) {
                return false;
            }
            if (mAccessibilityDelegateField == null) {
                try {
                    mAccessibilityDelegateField = View.class.getDeclaredField("mAccessibilityDelegate");
                    mAccessibilityDelegateField.setAccessible(true);
                } catch (Throwable th) {
                    accessibilityDelegateCheckFailed = true;
                    return false;
                }
            }
            try {
                if (mAccessibilityDelegateField.get(view) != null) {
                    z = true;
                }
                return z;
            } catch (Throwable th2) {
                accessibilityDelegateCheckFailed = true;
                return false;
            }
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public void onInitializeAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
            ViewCompatICS.onInitializeAccessibilityEvent(view, accessibilityEvent);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            ViewCompatICS.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat.getInfo());
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public void onPopulateAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
            ViewCompatICS.onPopulateAccessibilityEvent(view, accessibilityEvent);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setAccessibilityDelegate(View view, AccessibilityDelegateCompat accessibilityDelegateCompat) {
            ViewCompatICS.setAccessibilityDelegate(view, accessibilityDelegateCompat.getBridge());
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({0, 1, 2, 4})
    /* loaded from: ViewCompat$ImportantForAccessibility.class */
    private @interface ImportantForAccessibility {
    }

    /* loaded from: ViewCompat$JBViewCompatImpl.class */
    static class JBViewCompatImpl extends ICSViewCompatImpl {
        JBViewCompatImpl() {
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public AccessibilityNodeProviderCompat getAccessibilityNodeProvider(View view) {
            Object accessibilityNodeProvider = ViewCompatJB.getAccessibilityNodeProvider(view);
            if (accessibilityNodeProvider != null) {
                return new AccessibilityNodeProviderCompat(accessibilityNodeProvider);
            }
            return null;
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public boolean getFitsSystemWindows(View view) {
            return ViewCompatJB.getFitsSystemWindows(view);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public int getImportantForAccessibility(View view) {
            return ViewCompatJB.getImportantForAccessibility(view);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public int getMinimumHeight(View view) {
            return ViewCompatJB.getMinimumHeight(view);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public int getMinimumWidth(View view) {
            return ViewCompatJB.getMinimumWidth(view);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public ViewParent getParentForAccessibility(View view) {
            return ViewCompatJB.getParentForAccessibility(view);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public boolean hasTransientState(View view) {
            return ViewCompatJB.hasTransientState(view);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public boolean performAccessibilityAction(View view, int i, Bundle bundle) {
            return ViewCompatJB.performAccessibilityAction(view, i, bundle);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public void postInvalidateOnAnimation(View view) {
            ViewCompatJB.postInvalidateOnAnimation(view);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public void postInvalidateOnAnimation(View view, int i, int i2, int i3, int i4) {
            ViewCompatJB.postInvalidateOnAnimation(view, i, i2, i3, i4);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public void postOnAnimation(View view, Runnable runnable) {
            ViewCompatJB.postOnAnimation(view, runnable);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public void postOnAnimationDelayed(View view, Runnable runnable, long j) {
            ViewCompatJB.postOnAnimationDelayed(view, runnable, j);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public void requestApplyInsets(View view) {
            ViewCompatJB.requestApplyInsets(view);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setHasTransientState(View view, boolean z) {
            ViewCompatJB.setHasTransientState(view, z);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setImportantForAccessibility(View view, int i) {
            if (i == 4) {
                i = 2;
            }
            ViewCompatJB.setImportantForAccessibility(view, i);
        }
    }

    /* loaded from: ViewCompat$JbMr1ViewCompatImpl.class */
    static class JbMr1ViewCompatImpl extends JBViewCompatImpl {
        JbMr1ViewCompatImpl() {
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public int getLabelFor(View view) {
            return ViewCompatJellybeanMr1.getLabelFor(view);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public int getLayoutDirection(View view) {
            return ViewCompatJellybeanMr1.getLayoutDirection(view);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public int getPaddingEnd(View view) {
            return ViewCompatJellybeanMr1.getPaddingEnd(view);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public int getPaddingStart(View view) {
            return ViewCompatJellybeanMr1.getPaddingStart(view);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public int getWindowSystemUiVisibility(View view) {
            return ViewCompatJellybeanMr1.getWindowSystemUiVisibility(view);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setLabelFor(View view, int i) {
            ViewCompatJellybeanMr1.setLabelFor(view, i);
        }

        @Override // android.support.v4.view.ViewCompat.HCViewCompatImpl, android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setLayerPaint(View view, Paint paint) {
            ViewCompatJellybeanMr1.setLayerPaint(view, paint);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setLayoutDirection(View view, int i) {
            ViewCompatJellybeanMr1.setLayoutDirection(view, i);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setPaddingRelative(View view, int i, int i2, int i3, int i4) {
            ViewCompatJellybeanMr1.setPaddingRelative(view, i, i2, i3, i4);
        }
    }

    /* loaded from: ViewCompat$KitKatViewCompatImpl.class */
    static class KitKatViewCompatImpl extends JbMr1ViewCompatImpl {
        KitKatViewCompatImpl() {
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public int getAccessibilityLiveRegion(View view) {
            return ViewCompatKitKat.getAccessibilityLiveRegion(view);
        }

        @Override // android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setAccessibilityLiveRegion(View view, int i) {
            ViewCompatKitKat.setAccessibilityLiveRegion(view, i);
        }

        @Override // android.support.v4.view.ViewCompat.JBViewCompatImpl, android.support.v4.view.ViewCompat.BaseViewCompatImpl, android.support.v4.view.ViewCompat.ViewCompatImpl
        public void setImportantForAccessibility(View view, int i) {
            ViewCompatJB.setImportantForAccessibility(view, i);
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({0, 1, 2})
    /* loaded from: ViewCompat$LayerType.class */
    private @interface LayerType {
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({0, 1, 2, 3})
    /* loaded from: ViewCompat$LayoutDirectionMode.class */
    private @interface LayoutDirectionMode {
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({0, 1, 1})
    /* loaded from: ViewCompat$OverScroll.class */
    private @interface OverScroll {
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({0, 1})
    /* loaded from: ViewCompat$ResolvedLayoutDirectionMode.class */
    private @interface ResolvedLayoutDirectionMode {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ViewCompat$ViewCompatImpl.class */
    public interface ViewCompatImpl {
        ViewPropertyAnimatorCompat animate(View view);

        boolean canScrollHorizontally(View view, int i);

        boolean canScrollVertically(View view, int i);

        void dispatchFinishTemporaryDetach(View view);

        void dispatchStartTemporaryDetach(View view);

        int getAccessibilityLiveRegion(View view);

        AccessibilityNodeProviderCompat getAccessibilityNodeProvider(View view);

        float getAlpha(View view);

        float getElevation(View view);

        boolean getFitsSystemWindows(View view);

        int getImportantForAccessibility(View view);

        int getLabelFor(View view);

        int getLayerType(View view);

        int getLayoutDirection(View view);

        int getMeasuredHeightAndState(View view);

        int getMeasuredState(View view);

        int getMeasuredWidthAndState(View view);

        int getMinimumHeight(View view);

        int getMinimumWidth(View view);

        int getOverScrollMode(View view);

        int getPaddingEnd(View view);

        int getPaddingStart(View view);

        ViewParent getParentForAccessibility(View view);

        float getPivotX(View view);

        float getPivotY(View view);

        float getRotation(View view);

        float getRotationX(View view);

        float getRotationY(View view);

        float getScaleX(View view);

        float getScaleY(View view);

        String getTransitionName(View view);

        float getTranslationX(View view);

        float getTranslationY(View view);

        float getTranslationZ(View view);

        int getWindowSystemUiVisibility(View view);

        float getX(View view);

        float getY(View view);

        boolean hasAccessibilityDelegate(View view);

        boolean hasTransientState(View view);

        boolean isOpaque(View view);

        void jumpDrawablesToCurrentState(View view);

        void onInitializeAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent);

        void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat);

        void onPopulateAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent);

        boolean performAccessibilityAction(View view, int i, Bundle bundle);

        void postInvalidateOnAnimation(View view);

        void postInvalidateOnAnimation(View view, int i, int i2, int i3, int i4);

        void postOnAnimation(View view, Runnable runnable);

        void postOnAnimationDelayed(View view, Runnable runnable, long j);

        void requestApplyInsets(View view);

        int resolveSizeAndState(int i, int i2, int i3);

        void setAccessibilityDelegate(View view, AccessibilityDelegateCompat accessibilityDelegateCompat);

        void setAccessibilityLiveRegion(View view, int i);

        void setAlpha(View view, float f);

        void setChildrenDrawingOrderEnabled(ViewGroup viewGroup, boolean z);

        void setElevation(View view, float f);

        void setHasTransientState(View view, boolean z);

        void setImportantForAccessibility(View view, int i);

        void setLabelFor(View view, int i);

        void setLayerPaint(View view, Paint paint);

        void setLayerType(View view, int i, Paint paint);

        void setLayoutDirection(View view, int i);

        void setOnApplyWindowInsetsListener(View view, OnApplyWindowInsetsListener onApplyWindowInsetsListener);

        void setOverScrollMode(View view, int i);

        void setPaddingRelative(View view, int i, int i2, int i3, int i4);

        void setPivotX(View view, float f);

        void setPivotY(View view, float f);

        void setRotation(View view, float f);

        void setRotationX(View view, float f);

        void setRotationY(View view, float f);

        void setScaleX(View view, float f);

        void setScaleY(View view, float f);

        void setTransitionName(View view, String str);

        void setTranslationX(View view, float f);

        void setTranslationY(View view, float f);

        void setTranslationZ(View view, float f);

        void setX(View view, float f);

        void setY(View view, float f);
    }

    static {
        int i = Build.VERSION.SDK_INT;
        if (i >= 21) {
            IMPL = new Api21ViewCompatImpl();
        } else if (i >= 19) {
            IMPL = new KitKatViewCompatImpl();
        } else if (i >= 17) {
            IMPL = new JbMr1ViewCompatImpl();
        } else if (i >= 16) {
            IMPL = new JBViewCompatImpl();
        } else if (i >= 14) {
            IMPL = new ICSViewCompatImpl();
        } else if (i >= 11) {
            IMPL = new HCViewCompatImpl();
        } else if (i >= 9) {
            IMPL = new GBViewCompatImpl();
        } else if (i >= 7) {
            IMPL = new EclairMr1ViewCompatImpl();
        } else {
            IMPL = new BaseViewCompatImpl();
        }
    }

    public static ViewPropertyAnimatorCompat animate(View view) {
        return IMPL.animate(view);
    }

    public static boolean canScrollHorizontally(View view, int i) {
        return IMPL.canScrollHorizontally(view, i);
    }

    public static boolean canScrollVertically(View view, int i) {
        return IMPL.canScrollVertically(view, i);
    }

    public static void dispatchFinishTemporaryDetach(View view) {
        IMPL.dispatchFinishTemporaryDetach(view);
    }

    public static void dispatchStartTemporaryDetach(View view) {
        IMPL.dispatchStartTemporaryDetach(view);
    }

    public static int getAccessibilityLiveRegion(View view) {
        return IMPL.getAccessibilityLiveRegion(view);
    }

    public static AccessibilityNodeProviderCompat getAccessibilityNodeProvider(View view) {
        return IMPL.getAccessibilityNodeProvider(view);
    }

    public static float getAlpha(View view) {
        return IMPL.getAlpha(view);
    }

    public static float getElevation(View view) {
        return IMPL.getElevation(view);
    }

    public static boolean getFitsSystemWindows(View view) {
        return IMPL.getFitsSystemWindows(view);
    }

    public static int getImportantForAccessibility(View view) {
        return IMPL.getImportantForAccessibility(view);
    }

    public static int getLabelFor(View view) {
        return IMPL.getLabelFor(view);
    }

    public static int getLayerType(View view) {
        return IMPL.getLayerType(view);
    }

    public static int getLayoutDirection(View view) {
        return IMPL.getLayoutDirection(view);
    }

    public static int getMeasuredHeightAndState(View view) {
        return IMPL.getMeasuredHeightAndState(view);
    }

    public static int getMeasuredState(View view) {
        return IMPL.getMeasuredState(view);
    }

    public static int getMeasuredWidthAndState(View view) {
        return IMPL.getMeasuredWidthAndState(view);
    }

    public static int getMinimumHeight(View view) {
        return IMPL.getMinimumHeight(view);
    }

    public static int getMinimumWidth(View view) {
        return IMPL.getMinimumWidth(view);
    }

    public static int getOverScrollMode(View view) {
        return IMPL.getOverScrollMode(view);
    }

    public static int getPaddingEnd(View view) {
        return IMPL.getPaddingEnd(view);
    }

    public static int getPaddingStart(View view) {
        return IMPL.getPaddingStart(view);
    }

    public static ViewParent getParentForAccessibility(View view) {
        return IMPL.getParentForAccessibility(view);
    }

    public static float getPivotX(View view) {
        return IMPL.getPivotX(view);
    }

    public static float getPivotY(View view) {
        return IMPL.getPivotY(view);
    }

    public static float getRotation(View view) {
        return IMPL.getRotation(view);
    }

    public static float getRotationX(View view) {
        return IMPL.getRotationX(view);
    }

    public static float getRotationY(View view) {
        return IMPL.getRotationY(view);
    }

    public static float getScaleX(View view) {
        return IMPL.getScaleX(view);
    }

    public static float getScaleY(View view) {
        return IMPL.getScaleY(view);
    }

    public static String getTransitionName(View view) {
        return IMPL.getTransitionName(view);
    }

    public static float getTranslationX(View view) {
        return IMPL.getTranslationX(view);
    }

    public static float getTranslationY(View view) {
        return IMPL.getTranslationY(view);
    }

    public static float getTranslationZ(View view) {
        return IMPL.getTranslationZ(view);
    }

    public static int getWindowSystemUiVisibility(View view) {
        return IMPL.getWindowSystemUiVisibility(view);
    }

    public static float getX(View view) {
        return IMPL.getX(view);
    }

    public static float getY(View view) {
        return IMPL.getY(view);
    }

    public static boolean hasAccessibilityDelegate(View view) {
        return IMPL.hasAccessibilityDelegate(view);
    }

    public static boolean hasTransientState(View view) {
        return IMPL.hasTransientState(view);
    }

    public static boolean isOpaque(View view) {
        return IMPL.isOpaque(view);
    }

    public static void jumpDrawablesToCurrentState(View view) {
        IMPL.jumpDrawablesToCurrentState(view);
    }

    public static void onInitializeAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
        IMPL.onInitializeAccessibilityEvent(view, accessibilityEvent);
    }

    public static void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
        IMPL.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
    }

    public static void onPopulateAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
        IMPL.onPopulateAccessibilityEvent(view, accessibilityEvent);
    }

    public static boolean performAccessibilityAction(View view, int i, Bundle bundle) {
        return IMPL.performAccessibilityAction(view, i, bundle);
    }

    public static void postInvalidateOnAnimation(View view) {
        IMPL.postInvalidateOnAnimation(view);
    }

    public static void postInvalidateOnAnimation(View view, int i, int i2, int i3, int i4) {
        IMPL.postInvalidateOnAnimation(view, i, i2, i3, i4);
    }

    public static void postOnAnimation(View view, Runnable runnable) {
        IMPL.postOnAnimation(view, runnable);
    }

    public static void postOnAnimationDelayed(View view, Runnable runnable, long j) {
        IMPL.postOnAnimationDelayed(view, runnable, j);
    }

    public static void requestApplyInsets(View view) {
        IMPL.requestApplyInsets(view);
    }

    public static int resolveSizeAndState(int i, int i2, int i3) {
        return IMPL.resolveSizeAndState(i, i2, i3);
    }

    public static void setAccessibilityDelegate(View view, AccessibilityDelegateCompat accessibilityDelegateCompat) {
        IMPL.setAccessibilityDelegate(view, accessibilityDelegateCompat);
    }

    public static void setAccessibilityLiveRegion(View view, int i) {
        IMPL.setAccessibilityLiveRegion(view, i);
    }

    public static void setAlpha(View view, float f) {
        IMPL.setAlpha(view, f);
    }

    public static void setChildrenDrawingOrderEnabled(ViewGroup viewGroup, boolean z) {
        IMPL.setChildrenDrawingOrderEnabled(viewGroup, z);
    }

    public static void setElevation(View view, float f) {
        IMPL.setElevation(view, f);
    }

    public static void setHasTransientState(View view, boolean z) {
        IMPL.setHasTransientState(view, z);
    }

    public static void setImportantForAccessibility(View view, int i) {
        IMPL.setImportantForAccessibility(view, i);
    }

    public static void setLabelFor(View view, int i) {
        IMPL.setLabelFor(view, i);
    }

    public static void setLayerPaint(View view, Paint paint) {
        IMPL.setLayerPaint(view, paint);
    }

    public static void setLayerType(View view, int i, Paint paint) {
        IMPL.setLayerType(view, i, paint);
    }

    public static void setLayoutDirection(View view, int i) {
        IMPL.setLayoutDirection(view, i);
    }

    public static void setOnApplyWindowInsetsListener(View view, OnApplyWindowInsetsListener onApplyWindowInsetsListener) {
        IMPL.setOnApplyWindowInsetsListener(view, onApplyWindowInsetsListener);
    }

    public static void setOverScrollMode(View view, int i) {
        IMPL.setOverScrollMode(view, i);
    }

    public static void setPaddingRelative(View view, int i, int i2, int i3, int i4) {
        IMPL.setPaddingRelative(view, i, i2, i3, i4);
    }

    public static void setPivotX(View view, float f) {
        IMPL.setPivotX(view, f);
    }

    public static void setPivotY(View view, float f) {
        IMPL.setPivotX(view, f);
    }

    public static void setRotation(View view, float f) {
        IMPL.setRotation(view, f);
    }

    public static void setRotationX(View view, float f) {
        IMPL.setRotationX(view, f);
    }

    public static void setRotationY(View view, float f) {
        IMPL.setRotationY(view, f);
    }

    public static void setScaleX(View view, float f) {
        IMPL.setScaleX(view, f);
    }

    public static void setScaleY(View view, float f) {
        IMPL.setScaleY(view, f);
    }

    public static void setTransitionName(View view, String str) {
        IMPL.setTransitionName(view, str);
    }

    public static void setTranslationX(View view, float f) {
        IMPL.setTranslationX(view, f);
    }

    public static void setTranslationY(View view, float f) {
        IMPL.setTranslationY(view, f);
    }

    public static void setTranslationZ(View view, float f) {
        IMPL.setTranslationZ(view, f);
    }

    public static void setX(View view, float f) {
        IMPL.setX(view, f);
    }

    public static void setY(View view, float f) {
        IMPL.setY(view, f);
    }
}