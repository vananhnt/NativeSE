package android.graphics.drawable;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import com.android.internal.R;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: RotateDrawable.class */
public class RotateDrawable extends Drawable implements Drawable.Callback {
    private static final float MAX_LEVEL = 10000.0f;
    private RotateState mState;
    private boolean mMutated;

    public RotateDrawable() {
        this(null, null);
    }

    private RotateDrawable(RotateState rotateState, Resources res) {
        this.mState = new RotateState(rotateState, this, res);
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        int saveCount = canvas.save();
        Rect bounds = this.mState.mDrawable.getBounds();
        int w = bounds.right - bounds.left;
        int h = bounds.bottom - bounds.top;
        RotateState st = this.mState;
        float px = st.mPivotXRel ? w * st.mPivotX : st.mPivotX;
        float py = st.mPivotYRel ? h * st.mPivotY : st.mPivotY;
        canvas.rotate(st.mCurrentDegrees, px + bounds.left, py + bounds.top);
        st.mDrawable.draw(canvas);
        canvas.restoreToCount(saveCount);
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
    public boolean setVisible(boolean visible, boolean restart) {
        this.mState.mDrawable.setVisible(visible, restart);
        return super.setVisible(visible, restart);
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isStateful() {
        return this.mState.mDrawable.isStateful();
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onStateChange(int[] state) {
        boolean changed = this.mState.mDrawable.setState(state);
        onBoundsChange(getBounds());
        return changed;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.graphics.drawable.Drawable
    public boolean onLevelChange(int level) {
        this.mState.mDrawable.setLevel(level);
        onBoundsChange(getBounds());
        this.mState.mCurrentDegrees = this.mState.mFromDegrees + ((this.mState.mToDegrees - this.mState.mFromDegrees) * (level / 10000.0f));
        invalidateSelf();
        return true;
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
        boolean pivotXRel;
        float pivotX;
        boolean pivotYRel;
        float pivotY;
        TypedArray a = r.obtainAttributes(attrs, R.styleable.RotateDrawable);
        super.inflateWithAttributes(r, parser, a, 0);
        TypedValue tv = a.peekValue(4);
        if (tv == null) {
            pivotXRel = true;
            pivotX = 0.5f;
        } else {
            pivotXRel = tv.type == 6;
            pivotX = pivotXRel ? tv.getFraction(1.0f, 1.0f) : tv.getFloat();
        }
        TypedValue tv2 = a.peekValue(5);
        if (tv2 == null) {
            pivotYRel = true;
            pivotY = 0.5f;
        } else {
            pivotYRel = tv2.type == 6;
            pivotY = pivotYRel ? tv2.getFraction(1.0f, 1.0f) : tv2.getFloat();
        }
        float fromDegrees = a.getFloat(2, 0.0f);
        float toDegrees = a.getFloat(3, 360.0f);
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
                    Log.w("drawable", "Bad element under <rotate>: " + parser.getName());
                }
            }
        }
        if (drawable == null) {
            Log.w("drawable", "No drawable specified for <rotate>");
        }
        this.mState.mDrawable = drawable;
        this.mState.mPivotXRel = pivotXRel;
        this.mState.mPivotX = pivotX;
        this.mState.mPivotYRel = pivotYRel;
        this.mState.mPivotY = pivotY;
        RotateState rotateState = this.mState;
        this.mState.mCurrentDegrees = fromDegrees;
        rotateState.mFromDegrees = fromDegrees;
        this.mState.mToDegrees = toDegrees;
        if (drawable != null) {
            drawable.setCallback(this);
        }
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
    /* loaded from: RotateDrawable$RotateState.class */
    public static final class RotateState extends Drawable.ConstantState {
        Drawable mDrawable;
        int mChangingConfigurations;
        boolean mPivotXRel;
        float mPivotX;
        boolean mPivotYRel;
        float mPivotY;
        float mFromDegrees;
        float mToDegrees;
        float mCurrentDegrees;
        private boolean mCanConstantState;
        private boolean mCheckedConstantState;

        public RotateState(RotateState source, RotateDrawable owner, Resources res) {
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
                float f = source.mFromDegrees;
                this.mCurrentDegrees = f;
                this.mFromDegrees = f;
                this.mToDegrees = source.mToDegrees;
                this.mCheckedConstantState = true;
                this.mCanConstantState = true;
            }
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            return new RotateDrawable(this, null);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable(Resources res) {
            return new RotateDrawable(this, res);
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