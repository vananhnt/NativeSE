package android.graphics.drawable;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import com.android.internal.R;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: AnimatedRotateDrawable.class */
public class AnimatedRotateDrawable extends Drawable implements Drawable.Callback, Runnable, Animatable {
    private AnimatedRotateState mState;
    private boolean mMutated;
    private float mCurrentDegrees;
    private float mIncrement;
    private boolean mRunning;

    public AnimatedRotateDrawable() {
        this(null, null);
    }

    private AnimatedRotateDrawable(AnimatedRotateState rotateState, Resources res) {
        this.mState = new AnimatedRotateState(rotateState, this, res);
        init();
    }

    private void init() {
        AnimatedRotateState state = this.mState;
        this.mIncrement = 360.0f / state.mFramesCount;
        Drawable drawable = state.mDrawable;
        if (drawable != null) {
            drawable.setFilterBitmap(true);
            if (drawable instanceof BitmapDrawable) {
                ((BitmapDrawable) drawable).setAntiAlias(true);
            }
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        int saveCount = canvas.save();
        AnimatedRotateState st = this.mState;
        Drawable drawable = st.mDrawable;
        Rect bounds = drawable.getBounds();
        int w = bounds.right - bounds.left;
        int h = bounds.bottom - bounds.top;
        float px = st.mPivotXRel ? w * st.mPivotX : st.mPivotX;
        float py = st.mPivotYRel ? h * st.mPivotY : st.mPivotY;
        canvas.rotate(this.mCurrentDegrees, px + bounds.left, py + bounds.top);
        drawable.draw(canvas);
        canvas.restoreToCount(saveCount);
    }

    @Override // android.graphics.drawable.Animatable
    public void start() {
        if (!this.mRunning) {
            this.mRunning = true;
            nextFrame();
        }
    }

    @Override // android.graphics.drawable.Animatable
    public void stop() {
        this.mRunning = false;
        unscheduleSelf(this);
    }

    @Override // android.graphics.drawable.Animatable
    public boolean isRunning() {
        return this.mRunning;
    }

    private void nextFrame() {
        unscheduleSelf(this);
        scheduleSelf(this, SystemClock.uptimeMillis() + this.mState.mFrameDuration);
    }

    @Override // java.lang.Runnable
    public void run() {
        this.mCurrentDegrees += this.mIncrement;
        if (this.mCurrentDegrees > 360.0f - this.mIncrement) {
            this.mCurrentDegrees = 0.0f;
        }
        invalidateSelf();
        nextFrame();
    }

    @Override // android.graphics.drawable.Drawable
    public boolean setVisible(boolean visible, boolean restart) {
        this.mState.mDrawable.setVisible(visible, restart);
        boolean changed = super.setVisible(visible, restart);
        if (visible) {
            if (changed || restart) {
                this.mCurrentDegrees = 0.0f;
                nextFrame();
            }
        } else {
            unscheduleSelf(this);
        }
        return changed;
    }

    public Drawable getDrawable() {
        return this.mState.mDrawable;
    }

    @Override // android.graphics.drawable.Drawable
    public int getChangingConfigurations() {
        return super.getChangingConfigurations() | this.mState.mChangingConfigurations | this.mState.mDrawable.getChangingConfigurations();
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        this.mState.mDrawable.setAlpha(alpha);
    }

    @Override // android.graphics.drawable.Drawable
    public int getAlpha() {
        return this.mState.mDrawable.getAlpha();
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter cf) {
        this.mState.mDrawable.setColorFilter(cf);
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return this.mState.mDrawable.getOpacity();
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void invalidateDrawable(Drawable who) {
        Drawable.Callback callback = getCallback();
        if (callback != null) {
            callback.invalidateDrawable(this);
        }
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        Drawable.Callback callback = getCallback();
        if (callback != null) {
            callback.scheduleDrawable(this, what, when);
        }
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void unscheduleDrawable(Drawable who, Runnable what) {
        Drawable.Callback callback = getCallback();
        if (callback != null) {
            callback.unscheduleDrawable(this, what);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public boolean getPadding(Rect padding) {
        return this.mState.mDrawable.getPadding(padding);
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isStateful() {
        return this.mState.mDrawable.isStateful();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.graphics.drawable.Drawable
    public void onBoundsChange(Rect bounds) {
        this.mState.mDrawable.setBounds(bounds.left, bounds.top, bounds.right, bounds.bottom);
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return this.mState.mDrawable.getIntrinsicWidth();
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return this.mState.mDrawable.getIntrinsicHeight();
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable.ConstantState getConstantState() {
        if (this.mState.canConstantState()) {
            this.mState.mChangingConfigurations = getChangingConfigurations();
            return this.mState;
        }
        return null;
    }

    @Override // android.graphics.drawable.Drawable
    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs) throws XmlPullParserException, IOException {
        TypedArray a = r.obtainAttributes(attrs, R.styleable.AnimatedRotateDrawable);
        super.inflateWithAttributes(r, parser, a, 0);
        TypedValue tv = a.peekValue(2);
        boolean pivotXRel = tv.type == 6;
        float pivotX = pivotXRel ? tv.getFraction(1.0f, 1.0f) : tv.getFloat();
        TypedValue tv2 = a.peekValue(3);
        boolean pivotYRel = tv2.type == 6;
        float pivotY = pivotYRel ? tv2.getFraction(1.0f, 1.0f) : tv2.getFloat();
        setFramesCount(a.getInt(5, 12));
        setFramesDuration(a.getInt(4, 150));
        int res = a.getResourceId(1, 0);
        Drawable drawable = null;
        if (res > 0) {
            drawable = r.getDrawable(res);
        }
        a.recycle();
        int outerDepth = parser.getDepth();
        while (true) {
            int type = parser.next();
            if (type == 1 || (type == 3 && parser.getDepth() <= outerDepth)) {
                break;
            } else if (type == 2) {
                Drawable createFromXmlInner = Drawable.createFromXmlInner(r, parser, attrs);
                drawable = createFromXmlInner;
                if (createFromXmlInner == null) {
                    Log.w("drawable", "Bad element under <animated-rotate>: " + parser.getName());
                }
            }
        }
        if (drawable == null) {
            Log.w("drawable", "No drawable specified for <animated-rotate>");
        }
        AnimatedRotateState rotateState = this.mState;
        rotateState.mDrawable = drawable;
        rotateState.mPivotXRel = pivotXRel;
        rotateState.mPivotX = pivotX;
        rotateState.mPivotYRel = pivotYRel;
        rotateState.mPivotY = pivotY;
        init();
        if (drawable != null) {
            drawable.setCallback(this);
        }
    }

    public void setFramesCount(int framesCount) {
        this.mState.mFramesCount = framesCount;
        this.mIncrement = 360.0f / this.mState.mFramesCount;
    }

    public void setFramesDuration(int framesDuration) {
        this.mState.mFrameDuration = framesDuration;
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable mutate() {
        if (!this.mMutated && super.mutate() == this) {
            this.mState.mDrawable.mutate();
            this.mMutated = true;
        }
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: AnimatedRotateDrawable$AnimatedRotateState.class */
    public static final class AnimatedRotateState extends Drawable.ConstantState {
        Drawable mDrawable;
        int mChangingConfigurations;
        boolean mPivotXRel;
        float mPivotX;
        boolean mPivotYRel;
        float mPivotY;
        int mFrameDuration;
        int mFramesCount;
        private boolean mCanConstantState;
        private boolean mCheckedConstantState;

        public AnimatedRotateState(AnimatedRotateState source, AnimatedRotateDrawable owner, Resources res) {
            if (source != null) {
                if (res != null) {
                    this.mDrawable = source.mDrawable.getConstantState().newDrawable(res);
                } else {
                    this.mDrawable = source.mDrawable.getConstantState().newDrawable();
                }
                this.mDrawable.setCallback(owner);
                this.mDrawable.setLayoutDirection(source.mDrawable.getLayoutDirection());
                this.mPivotXRel = source.mPivotXRel;
                this.mPivotX = source.mPivotX;
                this.mPivotYRel = source.mPivotYRel;
                this.mPivotY = source.mPivotY;
                this.mFramesCount = source.mFramesCount;
                this.mFrameDuration = source.mFrameDuration;
                this.mCheckedConstantState = true;
                this.mCanConstantState = true;
            }
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            return new AnimatedRotateDrawable(this, null);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable(Resources res) {
            return new AnimatedRotateDrawable(this, res);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public int getChangingConfigurations() {
            return this.mChangingConfigurations;
        }

        public boolean canConstantState() {
            if (!this.mCheckedConstantState) {
                this.mCanConstantState = this.mDrawable.getConstantState() != null;
                this.mCheckedConstantState = true;
            }
            return this.mCanConstantState;
        }
    }
}