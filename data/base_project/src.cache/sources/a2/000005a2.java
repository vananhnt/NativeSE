package android.graphics.drawable;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import com.android.internal.R;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: InsetDrawable.class */
public class InsetDrawable extends Drawable implements Drawable.Callback {
    private InsetState mInsetState;
    private final Rect mTmpRect;
    private boolean mMutated;

    /* JADX INFO: Access modifiers changed from: package-private */
    public InsetDrawable() {
        this((InsetState) null, (Resources) null);
    }

    public InsetDrawable(Drawable drawable, int inset) {
        this(drawable, inset, inset, inset, inset);
    }

    public InsetDrawable(Drawable drawable, int insetLeft, int insetTop, int insetRight, int insetBottom) {
        this((InsetState) null, (Resources) null);
        this.mInsetState.mDrawable = drawable;
        this.mInsetState.mInsetLeft = insetLeft;
        this.mInsetState.mInsetTop = insetTop;
        this.mInsetState.mInsetRight = insetRight;
        this.mInsetState.mInsetBottom = insetBottom;
        if (drawable != null) {
            drawable.setCallback(this);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs) throws XmlPullParserException, IOException {
        int type;
        Drawable dr;
        TypedArray a = r.obtainAttributes(attrs, R.styleable.InsetDrawable);
        super.inflateWithAttributes(r, parser, a, 0);
        int drawableRes = a.getResourceId(1, 0);
        int inLeft = a.getDimensionPixelOffset(2, 0);
        int inTop = a.getDimensionPixelOffset(4, 0);
        int inRight = a.getDimensionPixelOffset(3, 0);
        int inBottom = a.getDimensionPixelOffset(5, 0);
        a.recycle();
        if (drawableRes != 0) {
            dr = r.getDrawable(drawableRes);
        } else {
            do {
                type = parser.next();
            } while (type == 4);
            if (type != 2) {
                throw new XmlPullParserException(parser.getPositionDescription() + ": <inset> tag requires a 'drawable' attribute or child tag defining a drawable");
            }
            dr = Drawable.createFromXmlInner(r, parser, attrs);
        }
        if (dr == null) {
            Log.w("drawable", "No drawable specified for <inset>");
        }
        this.mInsetState.mDrawable = dr;
        this.mInsetState.mInsetLeft = inLeft;
        this.mInsetState.mInsetRight = inRight;
        this.mInsetState.mInsetTop = inTop;
        this.mInsetState.mInsetBottom = inBottom;
        if (dr != null) {
            dr.setCallback(this);
        }
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
    public void draw(Canvas canvas) {
        this.mInsetState.mDrawable.draw(canvas);
    }

    @Override // android.graphics.drawable.Drawable
    public int getChangingConfigurations() {
        return super.getChangingConfigurations() | this.mInsetState.mChangingConfigurations | this.mInsetState.mDrawable.getChangingConfigurations();
    }

    @Override // android.graphics.drawable.Drawable
    public boolean getPadding(Rect padding) {
        boolean pad = this.mInsetState.mDrawable.getPadding(padding);
        padding.left += this.mInsetState.mInsetLeft;
        padding.right += this.mInsetState.mInsetRight;
        padding.top += this.mInsetState.mInsetTop;
        padding.bottom += this.mInsetState.mInsetBottom;
        if (pad || (this.mInsetState.mInsetLeft | this.mInsetState.mInsetRight | this.mInsetState.mInsetTop | this.mInsetState.mInsetBottom) != 0) {
            return true;
        }
        return false;
    }

    @Override // android.graphics.drawable.Drawable
    public boolean setVisible(boolean visible, boolean restart) {
        this.mInsetState.mDrawable.setVisible(visible, restart);
        return super.setVisible(visible, restart);
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        this.mInsetState.mDrawable.setAlpha(alpha);
    }

    @Override // android.graphics.drawable.Drawable
    public int getAlpha() {
        return this.mInsetState.mDrawable.getAlpha();
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter cf) {
        this.mInsetState.mDrawable.setColorFilter(cf);
    }

    @Override // android.graphics.drawable.Drawable
    public void setLayoutDirection(int layoutDirection) {
        this.mInsetState.mDrawable.setLayoutDirection(layoutDirection);
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return this.mInsetState.mDrawable.getOpacity();
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isStateful() {
        return this.mInsetState.mDrawable.isStateful();
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onStateChange(int[] state) {
        boolean changed = this.mInsetState.mDrawable.setState(state);
        onBoundsChange(getBounds());
        return changed;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.graphics.drawable.Drawable
    public void onBoundsChange(Rect bounds) {
        Rect r = this.mTmpRect;
        r.set(bounds);
        r.left += this.mInsetState.mInsetLeft;
        r.top += this.mInsetState.mInsetTop;
        r.right -= this.mInsetState.mInsetRight;
        r.bottom -= this.mInsetState.mInsetBottom;
        this.mInsetState.mDrawable.setBounds(r.left, r.top, r.right, r.bottom);
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return this.mInsetState.mDrawable.getIntrinsicWidth();
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return this.mInsetState.mDrawable.getIntrinsicHeight();
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable.ConstantState getConstantState() {
        if (this.mInsetState.canConstantState()) {
            this.mInsetState.mChangingConfigurations = getChangingConfigurations();
            return this.mInsetState;
        }
        return null;
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable mutate() {
        if (!this.mMutated && super.mutate() == this) {
            this.mInsetState.mDrawable.mutate();
            this.mMutated = true;
        }
        return this;
    }

    public Drawable getDrawable() {
        return this.mInsetState.mDrawable;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: InsetDrawable$InsetState.class */
    public static final class InsetState extends Drawable.ConstantState {
        Drawable mDrawable;
        int mChangingConfigurations;
        int mInsetLeft;
        int mInsetTop;
        int mInsetRight;
        int mInsetBottom;
        boolean mCheckedConstantState;
        boolean mCanConstantState;

        InsetState(InsetState orig, InsetDrawable owner, Resources res) {
            if (orig != null) {
                if (res != null) {
                    this.mDrawable = orig.mDrawable.getConstantState().newDrawable(res);
                } else {
                    this.mDrawable = orig.mDrawable.getConstantState().newDrawable();
                }
                this.mDrawable.setCallback(owner);
                this.mDrawable.setLayoutDirection(orig.mDrawable.getLayoutDirection());
                this.mInsetLeft = orig.mInsetLeft;
                this.mInsetTop = orig.mInsetTop;
                this.mInsetRight = orig.mInsetRight;
                this.mInsetBottom = orig.mInsetBottom;
                this.mCanConstantState = true;
                this.mCheckedConstantState = true;
            }
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            return new InsetDrawable(this, null);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable(Resources res) {
            return new InsetDrawable(this, res);
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

    private InsetDrawable(InsetState state, Resources res) {
        this.mTmpRect = new Rect();
        this.mInsetState = new InsetState(state, this, res);
    }
}