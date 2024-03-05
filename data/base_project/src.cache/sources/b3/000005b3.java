package android.graphics.drawable;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import com.android.internal.R;
import gov.nist.core.Separators;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: ScaleDrawable.class */
public class ScaleDrawable extends Drawable implements Drawable.Callback {
    private ScaleState mScaleState;
    private boolean mMutated;
    private final Rect mTmpRect;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ScaleDrawable() {
        this(null, null);
    }

    public ScaleDrawable(Drawable drawable, int gravity, float scaleWidth, float scaleHeight) {
        this(null, null);
        this.mScaleState.mDrawable = drawable;
        this.mScaleState.mGravity = gravity;
        this.mScaleState.mScaleWidth = scaleWidth;
        this.mScaleState.mScaleHeight = scaleHeight;
        if (drawable != null) {
            drawable.setCallback(this);
        }
    }

    public Drawable getDrawable() {
        return this.mScaleState.mDrawable;
    }

    private static float getPercent(TypedArray a, int name) {
        String s = a.getString(name);
        if (s != null && s.endsWith(Separators.PERCENT)) {
            String f = s.substring(0, s.length() - 1);
            return Float.parseFloat(f) / 100.0f;
        }
        return -1.0f;
    }

    @Override // android.graphics.drawable.Drawable
    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs) throws XmlPullParserException, IOException {
        super.inflate(r, parser, attrs);
        TypedArray a = r.obtainAttributes(attrs, R.styleable.ScaleDrawable);
        float sw = getPercent(a, 1);
        float sh = getPercent(a, 2);
        int g = a.getInt(3, 3);
        boolean min = a.getBoolean(4, false);
        Drawable dr = a.getDrawable(0);
        a.recycle();
        int outerDepth = parser.getDepth();
        while (true) {
            int type = parser.next();
            if (type == 1 || (type == 3 && parser.getDepth() <= outerDepth)) {
                break;
            } else if (type == 2) {
                dr = Drawable.createFromXmlInner(r, parser, attrs);
            }
        }
        if (dr == null) {
            throw new IllegalArgumentException("No drawable specified for <scale>");
        }
        this.mScaleState.mDrawable = dr;
        this.mScaleState.mScaleWidth = sw;
        this.mScaleState.mScaleHeight = sh;
        this.mScaleState.mGravity = g;
        this.mScaleState.mUseIntrinsicSizeAsMin = min;
        if (dr != null) {
            dr.setCallback(this);
        }
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void invalidateDrawable(Drawable who) {
        if (getCallback() != null) {
            getCallback().invalidateDrawable(this);
        }
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        if (getCallback() != null) {
            getCallback().scheduleDrawable(this, what, when);
        }
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void unscheduleDrawable(Drawable who, Runnable what) {
        if (getCallback() != null) {
            getCallback().unscheduleDrawable(this, what);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        if (this.mScaleState.mDrawable.getLevel() != 0) {
            this.mScaleState.mDrawable.draw(canvas);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public int getChangingConfigurations() {
        return super.getChangingConfigurations() | this.mScaleState.mChangingConfigurations | this.mScaleState.mDrawable.getChangingConfigurations();
    }

    @Override // android.graphics.drawable.Drawable
    public boolean getPadding(Rect padding) {
        return this.mScaleState.mDrawable.getPadding(padding);
    }

    @Override // android.graphics.drawable.Drawable
    public boolean setVisible(boolean visible, boolean restart) {
        this.mScaleState.mDrawable.setVisible(visible, restart);
        return super.setVisible(visible, restart);
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        this.mScaleState.mDrawable.setAlpha(alpha);
    }

    @Override // android.graphics.drawable.Drawable
    public int getAlpha() {
        return this.mScaleState.mDrawable.getAlpha();
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter cf) {
        this.mScaleState.mDrawable.setColorFilter(cf);
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return this.mScaleState.mDrawable.getOpacity();
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isStateful() {
        return this.mScaleState.mDrawable.isStateful();
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onStateChange(int[] state) {
        boolean changed = this.mScaleState.mDrawable.setState(state);
        onBoundsChange(getBounds());
        return changed;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.graphics.drawable.Drawable
    public boolean onLevelChange(int level) {
        this.mScaleState.mDrawable.setLevel(level);
        onBoundsChange(getBounds());
        invalidateSelf();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.graphics.drawable.Drawable
    public void onBoundsChange(Rect bounds) {
        Rect r = this.mTmpRect;
        boolean min = this.mScaleState.mUseIntrinsicSizeAsMin;
        int level = getLevel();
        int w = bounds.width();
        if (this.mScaleState.mScaleWidth > 0.0f) {
            int iw = min ? this.mScaleState.mDrawable.getIntrinsicWidth() : 0;
            w -= (int) ((((w - iw) * (10000 - level)) * this.mScaleState.mScaleWidth) / 10000.0f);
        }
        int h = bounds.height();
        if (this.mScaleState.mScaleHeight > 0.0f) {
            int ih = min ? this.mScaleState.mDrawable.getIntrinsicHeight() : 0;
            h -= (int) ((((h - ih) * (10000 - level)) * this.mScaleState.mScaleHeight) / 10000.0f);
        }
        int layoutDirection = getLayoutDirection();
        Gravity.apply(this.mScaleState.mGravity, w, h, bounds, r, layoutDirection);
        if (w > 0 && h > 0) {
            this.mScaleState.mDrawable.setBounds(r.left, r.top, r.right, r.bottom);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return this.mScaleState.mDrawable.getIntrinsicWidth();
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return this.mScaleState.mDrawable.getIntrinsicHeight();
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable.ConstantState getConstantState() {
        if (this.mScaleState.canConstantState()) {
            this.mScaleState.mChangingConfigurations = getChangingConfigurations();
            return this.mScaleState;
        }
        return null;
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable mutate() {
        if (!this.mMutated && super.mutate() == this) {
            this.mScaleState.mDrawable.mutate();
            this.mMutated = true;
        }
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ScaleDrawable$ScaleState.class */
    public static final class ScaleState extends Drawable.ConstantState {
        Drawable mDrawable;
        int mChangingConfigurations;
        float mScaleWidth;
        float mScaleHeight;
        int mGravity;
        boolean mUseIntrinsicSizeAsMin;
        private boolean mCheckedConstantState;
        private boolean mCanConstantState;

        ScaleState(ScaleState orig, ScaleDrawable owner, Resources res) {
            if (orig != null) {
                if (res != null) {
                    this.mDrawable = orig.mDrawable.getConstantState().newDrawable(res);
                } else {
                    this.mDrawable = orig.mDrawable.getConstantState().newDrawable();
                }
                this.mDrawable.setCallback(owner);
                this.mDrawable.setLayoutDirection(orig.mDrawable.getLayoutDirection());
                this.mScaleWidth = orig.mScaleWidth;
                this.mScaleHeight = orig.mScaleHeight;
                this.mGravity = orig.mGravity;
                this.mUseIntrinsicSizeAsMin = orig.mUseIntrinsicSizeAsMin;
                this.mCanConstantState = true;
                this.mCheckedConstantState = true;
            }
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            return new ScaleDrawable(this, null);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable(Resources res) {
            return new ScaleDrawable(this, res);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public int getChangingConfigurations() {
            return this.mChangingConfigurations;
        }

        boolean canConstantState() {
            if (!this.mCheckedConstantState) {
                this.mCanConstantState = this.mDrawable.getConstantState() != null;
                this.mCheckedConstantState = true;
            }
            return this.mCanConstantState;
        }
    }

    private ScaleDrawable(ScaleState state, Resources res) {
        this.mTmpRect = new Rect();
        this.mScaleState = new ScaleState(state, this, res);
    }
}