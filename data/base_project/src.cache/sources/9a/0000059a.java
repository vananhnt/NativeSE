package android.graphics.drawable;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Insets;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.SparseArray;

/* loaded from: DrawableContainer.class */
public class DrawableContainer extends Drawable implements Drawable.Callback {
    private static final boolean DEBUG = false;
    private static final String TAG = "DrawableContainer";
    private static final boolean DEFAULT_DITHER = true;
    private DrawableContainerState mDrawableContainerState;
    private Drawable mCurrDrawable;
    private ColorFilter mColorFilter;
    private boolean mMutated;
    private Runnable mAnimationRunnable;
    private long mEnterAnimationEnd;
    private long mExitAnimationEnd;
    private Drawable mLastDrawable;
    private int mAlpha = 255;
    private int mCurIndex = -1;

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        if (this.mCurrDrawable != null) {
            this.mCurrDrawable.draw(canvas);
        }
        if (this.mLastDrawable != null) {
            this.mLastDrawable.draw(canvas);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public int getChangingConfigurations() {
        return super.getChangingConfigurations() | this.mDrawableContainerState.mChangingConfigurations | this.mDrawableContainerState.mChildrenChangingConfigurations;
    }

    @Override // android.graphics.drawable.Drawable
    public boolean getPadding(Rect padding) {
        Rect r = this.mDrawableContainerState.getConstantPadding();
        if (r != null) {
            padding.set(r);
            return true;
        } else if (this.mCurrDrawable != null) {
            return this.mCurrDrawable.getPadding(padding);
        } else {
            return super.getPadding(padding);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public Insets getOpticalInsets() {
        return this.mCurrDrawable == null ? Insets.NONE : this.mCurrDrawable.getOpticalInsets();
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        if (this.mAlpha != alpha) {
            this.mAlpha = alpha;
            if (this.mCurrDrawable != null) {
                if (this.mEnterAnimationEnd == 0) {
                    this.mCurrDrawable.mutate().setAlpha(alpha);
                } else {
                    animate(false);
                }
            }
        }
    }

    @Override // android.graphics.drawable.Drawable
    public int getAlpha() {
        return this.mAlpha;
    }

    @Override // android.graphics.drawable.Drawable
    public void setDither(boolean dither) {
        if (this.mDrawableContainerState.mDither != dither) {
            this.mDrawableContainerState.mDither = dither;
            if (this.mCurrDrawable != null) {
                this.mCurrDrawable.mutate().setDither(this.mDrawableContainerState.mDither);
            }
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter cf) {
        if (this.mColorFilter != cf) {
            this.mColorFilter = cf;
            if (this.mCurrDrawable != null) {
                this.mCurrDrawable.mutate().setColorFilter(cf);
            }
        }
    }

    public void setEnterFadeDuration(int ms) {
        this.mDrawableContainerState.mEnterFadeDuration = ms;
    }

    public void setExitFadeDuration(int ms) {
        this.mDrawableContainerState.mExitFadeDuration = ms;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.graphics.drawable.Drawable
    public void onBoundsChange(Rect bounds) {
        if (this.mLastDrawable != null) {
            this.mLastDrawable.setBounds(bounds);
        }
        if (this.mCurrDrawable != null) {
            this.mCurrDrawable.setBounds(bounds);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isStateful() {
        return this.mDrawableContainerState.isStateful();
    }

    @Override // android.graphics.drawable.Drawable
    public void setAutoMirrored(boolean mirrored) {
        this.mDrawableContainerState.mAutoMirrored = mirrored;
        if (this.mCurrDrawable != null) {
            this.mCurrDrawable.mutate().setAutoMirrored(this.mDrawableContainerState.mAutoMirrored);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isAutoMirrored() {
        return this.mDrawableContainerState.mAutoMirrored;
    }

    @Override // android.graphics.drawable.Drawable
    public void jumpToCurrentState() {
        boolean changed = false;
        if (this.mLastDrawable != null) {
            this.mLastDrawable.jumpToCurrentState();
            this.mLastDrawable = null;
            changed = true;
        }
        if (this.mCurrDrawable != null) {
            this.mCurrDrawable.jumpToCurrentState();
            this.mCurrDrawable.mutate().setAlpha(this.mAlpha);
        }
        if (this.mExitAnimationEnd != 0) {
            this.mExitAnimationEnd = 0L;
            changed = true;
        }
        if (this.mEnterAnimationEnd != 0) {
            this.mEnterAnimationEnd = 0L;
            changed = true;
        }
        if (changed) {
            invalidateSelf();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.graphics.drawable.Drawable
    public boolean onStateChange(int[] state) {
        if (this.mLastDrawable != null) {
            return this.mLastDrawable.setState(state);
        }
        if (this.mCurrDrawable != null) {
            return this.mCurrDrawable.setState(state);
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.graphics.drawable.Drawable
    public boolean onLevelChange(int level) {
        if (this.mLastDrawable != null) {
            return this.mLastDrawable.setLevel(level);
        }
        if (this.mCurrDrawable != null) {
            return this.mCurrDrawable.setLevel(level);
        }
        return false;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        if (this.mDrawableContainerState.isConstantSize()) {
            return this.mDrawableContainerState.getConstantWidth();
        }
        if (this.mCurrDrawable != null) {
            return this.mCurrDrawable.getIntrinsicWidth();
        }
        return -1;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        if (this.mDrawableContainerState.isConstantSize()) {
            return this.mDrawableContainerState.getConstantHeight();
        }
        if (this.mCurrDrawable != null) {
            return this.mCurrDrawable.getIntrinsicHeight();
        }
        return -1;
    }

    @Override // android.graphics.drawable.Drawable
    public int getMinimumWidth() {
        if (this.mDrawableContainerState.isConstantSize()) {
            return this.mDrawableContainerState.getConstantMinimumWidth();
        }
        if (this.mCurrDrawable != null) {
            return this.mCurrDrawable.getMinimumWidth();
        }
        return 0;
    }

    @Override // android.graphics.drawable.Drawable
    public int getMinimumHeight() {
        if (this.mDrawableContainerState.isConstantSize()) {
            return this.mDrawableContainerState.getConstantMinimumHeight();
        }
        if (this.mCurrDrawable != null) {
            return this.mCurrDrawable.getMinimumHeight();
        }
        return 0;
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void invalidateDrawable(Drawable who) {
        if (who == this.mCurrDrawable && getCallback() != null) {
            getCallback().invalidateDrawable(this);
        }
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        if (who == this.mCurrDrawable && getCallback() != null) {
            getCallback().scheduleDrawable(this, what, when);
        }
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void unscheduleDrawable(Drawable who, Runnable what) {
        if (who == this.mCurrDrawable && getCallback() != null) {
            getCallback().unscheduleDrawable(this, what);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public boolean setVisible(boolean visible, boolean restart) {
        boolean changed = super.setVisible(visible, restart);
        if (this.mLastDrawable != null) {
            this.mLastDrawable.setVisible(visible, restart);
        }
        if (this.mCurrDrawable != null) {
            this.mCurrDrawable.setVisible(visible, restart);
        }
        return changed;
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        if (this.mCurrDrawable == null || !this.mCurrDrawable.isVisible()) {
            return -2;
        }
        return this.mDrawableContainerState.getOpacity();
    }

    public boolean selectDrawable(int idx) {
        if (idx == this.mCurIndex) {
            return false;
        }
        long now = SystemClock.uptimeMillis();
        if (this.mDrawableContainerState.mExitFadeDuration > 0) {
            if (this.mLastDrawable != null) {
                this.mLastDrawable.setVisible(false, false);
            }
            if (this.mCurrDrawable != null) {
                this.mLastDrawable = this.mCurrDrawable;
                this.mExitAnimationEnd = now + this.mDrawableContainerState.mExitFadeDuration;
            } else {
                this.mLastDrawable = null;
                this.mExitAnimationEnd = 0L;
            }
        } else if (this.mCurrDrawable != null) {
            this.mCurrDrawable.setVisible(false, false);
        }
        if (idx >= 0 && idx < this.mDrawableContainerState.mNumChildren) {
            Drawable d = this.mDrawableContainerState.getChild(idx);
            this.mCurrDrawable = d;
            this.mCurIndex = idx;
            if (d != null) {
                d.mutate();
                if (this.mDrawableContainerState.mEnterFadeDuration > 0) {
                    this.mEnterAnimationEnd = now + this.mDrawableContainerState.mEnterFadeDuration;
                } else {
                    d.setAlpha(this.mAlpha);
                }
                d.setVisible(isVisible(), true);
                d.setDither(this.mDrawableContainerState.mDither);
                d.setColorFilter(this.mColorFilter);
                d.setState(getState());
                d.setLevel(getLevel());
                d.setBounds(getBounds());
                d.setLayoutDirection(getLayoutDirection());
                d.setAutoMirrored(this.mDrawableContainerState.mAutoMirrored);
            }
        } else {
            this.mCurrDrawable = null;
            this.mCurIndex = -1;
        }
        if (this.mEnterAnimationEnd != 0 || this.mExitAnimationEnd != 0) {
            if (this.mAnimationRunnable == null) {
                this.mAnimationRunnable = new Runnable() { // from class: android.graphics.drawable.DrawableContainer.1
                    @Override // java.lang.Runnable
                    public void run() {
                        DrawableContainer.this.animate(true);
                        DrawableContainer.this.invalidateSelf();
                    }
                };
            } else {
                unscheduleSelf(this.mAnimationRunnable);
            }
            animate(true);
        }
        invalidateSelf();
        return true;
    }

    void animate(boolean schedule) {
        long now = SystemClock.uptimeMillis();
        boolean animating = false;
        if (this.mCurrDrawable != null) {
            if (this.mEnterAnimationEnd != 0) {
                if (this.mEnterAnimationEnd <= now) {
                    this.mCurrDrawable.mutate().setAlpha(this.mAlpha);
                    this.mEnterAnimationEnd = 0L;
                } else {
                    int animAlpha = ((int) ((this.mEnterAnimationEnd - now) * 255)) / this.mDrawableContainerState.mEnterFadeDuration;
                    this.mCurrDrawable.mutate().setAlpha(((255 - animAlpha) * this.mAlpha) / 255);
                    animating = true;
                }
            }
        } else {
            this.mEnterAnimationEnd = 0L;
        }
        if (this.mLastDrawable != null) {
            if (this.mExitAnimationEnd != 0) {
                if (this.mExitAnimationEnd <= now) {
                    this.mLastDrawable.setVisible(false, false);
                    this.mLastDrawable = null;
                    this.mExitAnimationEnd = 0L;
                } else {
                    int animAlpha2 = ((int) ((this.mExitAnimationEnd - now) * 255)) / this.mDrawableContainerState.mExitFadeDuration;
                    this.mLastDrawable.mutate().setAlpha((animAlpha2 * this.mAlpha) / 255);
                    animating = true;
                }
            }
        } else {
            this.mExitAnimationEnd = 0L;
        }
        if (schedule && animating) {
            scheduleSelf(this.mAnimationRunnable, now + 16);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable getCurrent() {
        return this.mCurrDrawable;
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable.ConstantState getConstantState() {
        if (this.mDrawableContainerState.canConstantState()) {
            this.mDrawableContainerState.mChangingConfigurations = getChangingConfigurations();
            return this.mDrawableContainerState;
        }
        return null;
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable mutate() {
        if (!this.mMutated && super.mutate() == this) {
            this.mDrawableContainerState.mutate();
            this.mMutated = true;
        }
        return this;
    }

    /* loaded from: DrawableContainer$DrawableContainerState.class */
    public static abstract class DrawableContainerState extends Drawable.ConstantState {
        final DrawableContainer mOwner;
        final Resources mRes;
        SparseArray<ConstantStateFuture> mDrawableFutures;
        int mChangingConfigurations;
        int mChildrenChangingConfigurations;
        Drawable[] mDrawables;
        int mNumChildren;
        boolean mVariablePadding;
        boolean mPaddingChecked;
        Rect mConstantPadding;
        boolean mConstantSize;
        boolean mComputedConstantSize;
        int mConstantWidth;
        int mConstantHeight;
        int mConstantMinimumWidth;
        int mConstantMinimumHeight;
        boolean mCheckedOpacity;
        int mOpacity;
        boolean mCheckedStateful;
        boolean mStateful;
        boolean mCheckedConstantState;
        boolean mCanConstantState;
        boolean mDither;
        boolean mMutated;
        int mLayoutDirection;
        int mEnterFadeDuration;
        int mExitFadeDuration;
        boolean mAutoMirrored;

        /* JADX INFO: Access modifiers changed from: package-private */
        public DrawableContainerState(DrawableContainerState orig, DrawableContainer owner, Resources res) {
            this.mDither = true;
            this.mOwner = owner;
            this.mRes = res;
            if (orig != null) {
                this.mChangingConfigurations = orig.mChangingConfigurations;
                this.mChildrenChangingConfigurations = orig.mChildrenChangingConfigurations;
                this.mCheckedConstantState = true;
                this.mCanConstantState = true;
                this.mVariablePadding = orig.mVariablePadding;
                this.mConstantSize = orig.mConstantSize;
                this.mDither = orig.mDither;
                this.mMutated = orig.mMutated;
                this.mLayoutDirection = orig.mLayoutDirection;
                this.mEnterFadeDuration = orig.mEnterFadeDuration;
                this.mExitFadeDuration = orig.mExitFadeDuration;
                this.mAutoMirrored = orig.mAutoMirrored;
                this.mConstantPadding = orig.getConstantPadding();
                this.mPaddingChecked = true;
                this.mConstantWidth = orig.getConstantWidth();
                this.mConstantHeight = orig.getConstantHeight();
                this.mConstantMinimumWidth = orig.getConstantMinimumWidth();
                this.mConstantMinimumHeight = orig.getConstantMinimumHeight();
                this.mComputedConstantSize = true;
                this.mOpacity = orig.getOpacity();
                this.mCheckedOpacity = true;
                this.mStateful = orig.isStateful();
                this.mCheckedStateful = true;
                Drawable[] origDr = orig.mDrawables;
                this.mDrawables = new Drawable[origDr.length];
                this.mNumChildren = orig.mNumChildren;
                SparseArray<ConstantStateFuture> origDf = orig.mDrawableFutures;
                if (origDf != null) {
                    this.mDrawableFutures = origDf.m904clone();
                } else {
                    this.mDrawableFutures = new SparseArray<>(this.mNumChildren);
                }
                int N = this.mNumChildren;
                for (int i = 0; i < N; i++) {
                    if (origDr[i] != null) {
                        this.mDrawableFutures.put(i, new ConstantStateFuture(origDr[i]));
                    }
                }
                return;
            }
            this.mDrawables = new Drawable[10];
            this.mNumChildren = 0;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public int getChangingConfigurations() {
            return this.mChangingConfigurations | this.mChildrenChangingConfigurations;
        }

        public final int addChild(Drawable dr) {
            int pos = this.mNumChildren;
            if (pos >= this.mDrawables.length) {
                growArray(pos, pos + 10);
            }
            dr.setVisible(false, true);
            dr.setCallback(this.mOwner);
            this.mDrawables[pos] = dr;
            this.mNumChildren++;
            this.mChildrenChangingConfigurations |= dr.getChangingConfigurations();
            this.mCheckedStateful = false;
            this.mCheckedOpacity = false;
            this.mConstantPadding = null;
            this.mPaddingChecked = false;
            this.mComputedConstantSize = false;
            return pos;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public final int getCapacity() {
            return this.mDrawables.length;
        }

        private final void createAllFutures() {
            if (this.mDrawableFutures != null) {
                int futureCount = this.mDrawableFutures.size();
                for (int keyIndex = 0; keyIndex < futureCount; keyIndex++) {
                    int index = this.mDrawableFutures.keyAt(keyIndex);
                    this.mDrawables[index] = this.mDrawableFutures.valueAt(keyIndex).get(this);
                }
                this.mDrawableFutures = null;
            }
        }

        public final int getChildCount() {
            return this.mNumChildren;
        }

        public final Drawable[] getChildren() {
            createAllFutures();
            return this.mDrawables;
        }

        public final Drawable getChild(int index) {
            int keyIndex;
            Drawable result = this.mDrawables[index];
            if (result != null) {
                return result;
            }
            if (this.mDrawableFutures != null && (keyIndex = this.mDrawableFutures.indexOfKey(index)) >= 0) {
                Drawable prepared = this.mDrawableFutures.valueAt(keyIndex).get(this);
                this.mDrawables[index] = prepared;
                this.mDrawableFutures.removeAt(keyIndex);
                return prepared;
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public final void setLayoutDirection(int layoutDirection) {
            int N = this.mNumChildren;
            Drawable[] drawables = this.mDrawables;
            for (int i = 0; i < N; i++) {
                if (drawables[i] != null) {
                    drawables[i].setLayoutDirection(layoutDirection);
                }
            }
            this.mLayoutDirection = layoutDirection;
        }

        final void mutate() {
            int N = this.mNumChildren;
            Drawable[] drawables = this.mDrawables;
            for (int i = 0; i < N; i++) {
                if (drawables[i] != null) {
                    drawables[i].mutate();
                }
            }
            this.mMutated = true;
        }

        public final void setVariablePadding(boolean variable) {
            this.mVariablePadding = variable;
        }

        public final Rect getConstantPadding() {
            if (this.mVariablePadding) {
                return null;
            }
            if (this.mConstantPadding != null || this.mPaddingChecked) {
                return this.mConstantPadding;
            }
            createAllFutures();
            Rect r = null;
            Rect t = new Rect();
            int N = this.mNumChildren;
            Drawable[] drawables = this.mDrawables;
            for (int i = 0; i < N; i++) {
                if (drawables[i].getPadding(t)) {
                    if (r == null) {
                        r = new Rect(0, 0, 0, 0);
                    }
                    if (t.left > r.left) {
                        r.left = t.left;
                    }
                    if (t.top > r.top) {
                        r.top = t.top;
                    }
                    if (t.right > r.right) {
                        r.right = t.right;
                    }
                    if (t.bottom > r.bottom) {
                        r.bottom = t.bottom;
                    }
                }
            }
            this.mPaddingChecked = true;
            Rect rect = r;
            this.mConstantPadding = rect;
            return rect;
        }

        public final void setConstantSize(boolean constant) {
            this.mConstantSize = constant;
        }

        public final boolean isConstantSize() {
            return this.mConstantSize;
        }

        public final int getConstantWidth() {
            if (!this.mComputedConstantSize) {
                computeConstantSize();
            }
            return this.mConstantWidth;
        }

        public final int getConstantHeight() {
            if (!this.mComputedConstantSize) {
                computeConstantSize();
            }
            return this.mConstantHeight;
        }

        public final int getConstantMinimumWidth() {
            if (!this.mComputedConstantSize) {
                computeConstantSize();
            }
            return this.mConstantMinimumWidth;
        }

        public final int getConstantMinimumHeight() {
            if (!this.mComputedConstantSize) {
                computeConstantSize();
            }
            return this.mConstantMinimumHeight;
        }

        protected void computeConstantSize() {
            this.mComputedConstantSize = true;
            createAllFutures();
            int N = this.mNumChildren;
            Drawable[] drawables = this.mDrawables;
            this.mConstantHeight = -1;
            this.mConstantWidth = -1;
            this.mConstantMinimumHeight = 0;
            this.mConstantMinimumWidth = 0;
            for (int i = 0; i < N; i++) {
                Drawable dr = drawables[i];
                int s = dr.getIntrinsicWidth();
                if (s > this.mConstantWidth) {
                    this.mConstantWidth = s;
                }
                int s2 = dr.getIntrinsicHeight();
                if (s2 > this.mConstantHeight) {
                    this.mConstantHeight = s2;
                }
                int s3 = dr.getMinimumWidth();
                if (s3 > this.mConstantMinimumWidth) {
                    this.mConstantMinimumWidth = s3;
                }
                int s4 = dr.getMinimumHeight();
                if (s4 > this.mConstantMinimumHeight) {
                    this.mConstantMinimumHeight = s4;
                }
            }
        }

        public final void setEnterFadeDuration(int duration) {
            this.mEnterFadeDuration = duration;
        }

        public final int getEnterFadeDuration() {
            return this.mEnterFadeDuration;
        }

        public final void setExitFadeDuration(int duration) {
            this.mExitFadeDuration = duration;
        }

        public final int getExitFadeDuration() {
            return this.mExitFadeDuration;
        }

        public final int getOpacity() {
            if (this.mCheckedOpacity) {
                return this.mOpacity;
            }
            createAllFutures();
            this.mCheckedOpacity = true;
            int N = this.mNumChildren;
            Drawable[] drawables = this.mDrawables;
            int op = N > 0 ? drawables[0].getOpacity() : -2;
            for (int i = 1; i < N; i++) {
                op = Drawable.resolveOpacity(op, drawables[i].getOpacity());
            }
            this.mOpacity = op;
            return op;
        }

        public final boolean isStateful() {
            if (this.mCheckedStateful) {
                return this.mStateful;
            }
            createAllFutures();
            this.mCheckedStateful = true;
            int N = this.mNumChildren;
            Drawable[] drawables = this.mDrawables;
            for (int i = 0; i < N; i++) {
                if (drawables[i].isStateful()) {
                    this.mStateful = true;
                    return true;
                }
            }
            this.mStateful = false;
            return false;
        }

        public void growArray(int oldSize, int newSize) {
            Drawable[] newDrawables = new Drawable[newSize];
            System.arraycopy(this.mDrawables, 0, newDrawables, 0, oldSize);
            this.mDrawables = newDrawables;
        }

        public synchronized boolean canConstantState() {
            if (this.mCheckedConstantState) {
                return this.mCanConstantState;
            }
            createAllFutures();
            this.mCheckedConstantState = true;
            int N = this.mNumChildren;
            Drawable[] drawables = this.mDrawables;
            for (int i = 0; i < N; i++) {
                if (drawables[i].getConstantState() == null) {
                    this.mCanConstantState = false;
                    return false;
                }
            }
            this.mCanConstantState = true;
            return true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: DrawableContainer$DrawableContainerState$ConstantStateFuture.class */
        public static class ConstantStateFuture {
            private final Drawable.ConstantState mConstantState;

            private ConstantStateFuture(Drawable source) {
                this.mConstantState = source.getConstantState();
            }

            public Drawable get(DrawableContainerState state) {
                Drawable result = state.mRes == null ? this.mConstantState.newDrawable() : this.mConstantState.newDrawable(state.mRes);
                result.setLayoutDirection(state.mLayoutDirection);
                result.setCallback(state.mOwner);
                if (state.mMutated) {
                    result.mutate();
                }
                return result;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setConstantState(DrawableContainerState state) {
        this.mDrawableContainerState = state;
    }
}