package android.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import java.util.ArrayList;

/* loaded from: ViewOverlay.class */
public class ViewOverlay {
    OverlayViewGroup mOverlayViewGroup;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ViewOverlay(Context context, View hostView) {
        this.mOverlayViewGroup = new OverlayViewGroup(context, hostView);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ViewGroup getOverlayView() {
        return this.mOverlayViewGroup;
    }

    public void add(Drawable drawable) {
        this.mOverlayViewGroup.add(drawable);
    }

    public void remove(Drawable drawable) {
        this.mOverlayViewGroup.remove(drawable);
    }

    public void clear() {
        this.mOverlayViewGroup.clear();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isEmpty() {
        return this.mOverlayViewGroup.isEmpty();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ViewOverlay$OverlayViewGroup.class */
    public static class OverlayViewGroup extends ViewGroup {
        View mHostView;
        ArrayList<Drawable> mDrawables;

        OverlayViewGroup(Context context, View hostView) {
            super(context);
            this.mDrawables = null;
            this.mHostView = hostView;
            this.mAttachInfo = this.mHostView.mAttachInfo;
            this.mRight = hostView.getWidth();
            this.mBottom = hostView.getHeight();
        }

        public void add(Drawable drawable) {
            if (this.mDrawables == null) {
                this.mDrawables = new ArrayList<>();
            }
            if (!this.mDrawables.contains(drawable)) {
                this.mDrawables.add(drawable);
                invalidate(drawable.getBounds());
                drawable.setCallback(this);
            }
        }

        public void remove(Drawable drawable) {
            if (this.mDrawables != null) {
                this.mDrawables.remove(drawable);
                invalidate(drawable.getBounds());
                drawable.setCallback(null);
            }
        }

        public void add(View child) {
            if (child.getParent() instanceof ViewGroup) {
                ViewGroup parent = (ViewGroup) child.getParent();
                if (parent != this.mHostView && parent.getParent() != null && parent.mAttachInfo != null) {
                    int[] parentLocation = new int[2];
                    int[] hostViewLocation = new int[2];
                    parent.getLocationOnScreen(parentLocation);
                    this.mHostView.getLocationOnScreen(hostViewLocation);
                    child.offsetLeftAndRight(parentLocation[0] - hostViewLocation[0]);
                    child.offsetTopAndBottom(parentLocation[1] - hostViewLocation[1]);
                }
                parent.removeView(child);
                if (parent.getLayoutTransition() != null) {
                    parent.getLayoutTransition().cancel(3);
                }
                if (child.getParent() != null) {
                    child.mParent = null;
                }
            }
            super.addView(child);
        }

        public void remove(View view) {
            super.removeView(view);
        }

        public void clear() {
            removeAllViews();
            if (this.mDrawables != null) {
                this.mDrawables.clear();
            }
        }

        boolean isEmpty() {
            if (getChildCount() == 0) {
                if (this.mDrawables == null || this.mDrawables.size() == 0) {
                    return true;
                }
                return false;
            }
            return false;
        }

        @Override // android.view.View, android.graphics.drawable.Drawable.Callback
        public void invalidateDrawable(Drawable drawable) {
            invalidate(drawable.getBounds());
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.view.ViewGroup, android.view.View
        public void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            int numDrawables = this.mDrawables == null ? 0 : this.mDrawables.size();
            for (int i = 0; i < numDrawables; i++) {
                this.mDrawables.get(i).draw(canvas);
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.view.ViewGroup, android.view.View
        public void onLayout(boolean changed, int l, int t, int r, int b) {
        }

        @Override // android.view.View
        public void invalidate(Rect dirty) {
            super.invalidate(dirty);
            if (this.mHostView != null) {
                this.mHostView.invalidate(dirty);
            }
        }

        @Override // android.view.View
        public void invalidate(int l, int t, int r, int b) {
            super.invalidate(l, t, r, b);
            if (this.mHostView != null) {
                this.mHostView.invalidate(l, t, r, b);
            }
        }

        @Override // android.view.View
        public void invalidate() {
            super.invalidate();
            if (this.mHostView != null) {
                this.mHostView.invalidate();
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // android.view.View
        public void invalidate(boolean invalidateCache) {
            super.invalidate(invalidateCache);
            if (this.mHostView != null) {
                this.mHostView.invalidate(invalidateCache);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // android.view.View
        public void invalidateViewProperty(boolean invalidateParent, boolean forceRedraw) {
            super.invalidateViewProperty(invalidateParent, forceRedraw);
            if (this.mHostView != null) {
                this.mHostView.invalidateViewProperty(invalidateParent, forceRedraw);
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.view.View
        public void invalidateParentCaches() {
            super.invalidateParentCaches();
            if (this.mHostView != null) {
                this.mHostView.invalidateParentCaches();
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.view.View
        public void invalidateParentIfNeeded() {
            super.invalidateParentIfNeeded();
            if (this.mHostView != null) {
                this.mHostView.invalidateParentIfNeeded();
            }
        }

        @Override // android.view.ViewGroup
        public void invalidateChildFast(View child, Rect dirty) {
            if (this.mHostView != null) {
                int left = child.mLeft;
                int top = child.mTop;
                if (!child.getMatrix().isIdentity()) {
                    child.transformRect(dirty);
                }
                dirty.offset(left, top);
                this.mHostView.invalidate(dirty);
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.view.ViewGroup
        public ViewParent invalidateChildInParentFast(int left, int top, Rect dirty) {
            if (this.mHostView instanceof ViewGroup) {
                return ((ViewGroup) this.mHostView).invalidateChildInParentFast(left, top, dirty);
            }
            return null;
        }

        @Override // android.view.ViewGroup, android.view.ViewParent
        public ViewParent invalidateChildInParent(int[] location, Rect dirty) {
            if (this.mHostView != null) {
                dirty.offset(location[0], location[1]);
                if (this.mHostView instanceof ViewGroup) {
                    location[0] = 0;
                    location[1] = 0;
                    super.invalidateChildInParent(location, dirty);
                    return ((ViewGroup) this.mHostView).invalidateChildInParent(location, dirty);
                }
                invalidate(dirty);
                return null;
            }
            return null;
        }
    }
}