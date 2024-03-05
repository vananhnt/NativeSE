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
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: ClipDrawable.class */
public class ClipDrawable extends Drawable implements Drawable.Callback {
    private ClipState mClipState;
    private final Rect mTmpRect;
    public static final int HORIZONTAL = 1;
    public static final int VERTICAL = 2;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ClipDrawable() {
        this(null, null);
    }

    public ClipDrawable(Drawable drawable, int gravity, int orientation) {
        this(null, null);
        this.mClipState.mDrawable = drawable;
        this.mClipState.mGravity = gravity;
        this.mClipState.mOrientation = orientation;
        if (drawable != null) {
            drawable.setCallback(this);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs) throws XmlPullParserException, IOException {
        super.inflate(r, parser, attrs);
        TypedArray a = r.obtainAttributes(attrs, R.styleable.ClipDrawable);
        int orientation = a.getInt(2, 1);
        int g = a.getInt(0, 3);
        Drawable dr = a.getDrawable(1);
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
            throw new IllegalArgumentException("No drawable specified for <clip>");
        }
        this.mClipState.mDrawable = dr;
        this.mClipState.mOrientation = orientation;
        this.mClipState.mGravity = g;
        dr.setCallback(this);
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
    public int getChangingConfigurations() {
        return super.getChangingConfigurations() | this.mClipState.mChangingConfigurations | this.mClipState.mDrawable.getChangingConfigurations();
    }

    @Override // android.graphics.drawable.Drawable
    public boolean getPadding(Rect padding) {
        return this.mClipState.mDrawable.getPadding(padding);
    }

    @Override // android.graphics.drawable.Drawable
    public boolean setVisible(boolean visible, boolean restart) {
        this.mClipState.mDrawable.setVisible(visible, restart);
        return super.setVisible(visible, restart);
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        this.mClipState.mDrawable.setAlpha(alpha);
    }

    @Override // android.graphics.drawable.Drawable
    public int getAlpha() {
        return this.mClipState.mDrawable.getAlpha();
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter cf) {
        this.mClipState.mDrawable.setColorFilter(cf);
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return this.mClipState.mDrawable.getOpacity();
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isStateful() {
        return this.mClipState.mDrawable.isStateful();
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onStateChange(int[] state) {
        return this.mClipState.mDrawable.setState(state);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.graphics.drawable.Drawable
    public boolean onLevelChange(int level) {
        this.mClipState.mDrawable.setLevel(level);
        invalidateSelf();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.graphics.drawable.Drawable
    public void onBoundsChange(Rect bounds) {
        this.mClipState.mDrawable.setBounds(bounds);
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        if (this.mClipState.mDrawable.getLevel() == 0) {
            return;
        }
        Rect r = this.mTmpRect;
        Rect bounds = getBounds();
        int level = getLevel();
        int w = bounds.width();
        if ((this.mClipState.mOrientation & 1) != 0) {
            w -= ((w - 0) * (10000 - level)) / 10000;
        }
        int h = bounds.height();
        if ((this.mClipState.mOrientation & 2) != 0) {
            h -= ((h - 0) * (10000 - level)) / 10000;
        }
        int layoutDirection = getLayoutDirection();
        Gravity.apply(this.mClipState.mGravity, w, h, bounds, r, layoutDirection);
        if (w > 0 && h > 0) {
            canvas.save();
            canvas.clipRect(r);
            this.mClipState.mDrawable.draw(canvas);
            canvas.restore();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return this.mClipState.mDrawable.getIntrinsicWidth();
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return this.mClipState.mDrawable.getIntrinsicHeight();
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable.ConstantState getConstantState() {
        if (this.mClipState.canConstantState()) {
            this.mClipState.mChangingConfigurations = getChangingConfigurations();
            return this.mClipState;
        }
        return null;
    }

    @Override // android.graphics.drawable.Drawable
    public void setLayoutDirection(int layoutDirection) {
        this.mClipState.mDrawable.setLayoutDirection(layoutDirection);
        super.setLayoutDirection(layoutDirection);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ClipDrawable$ClipState.class */
    public static final class ClipState extends Drawable.ConstantState {
        Drawable mDrawable;
        int mChangingConfigurations;
        int mOrientation;
        int mGravity;
        private boolean mCheckedConstantState;
        private boolean mCanConstantState;

        ClipState(ClipState orig, ClipDrawable owner, Resources res) {
            if (orig != null) {
                if (res != null) {
                    this.mDrawable = orig.mDrawable.getConstantState().newDrawable(res);
                } else {
                    this.mDrawable = orig.mDrawable.getConstantState().newDrawable();
                }
                this.mDrawable.setCallback(owner);
                this.mDrawable.setLayoutDirection(orig.mDrawable.getLayoutDirection());
                this.mOrientation = orig.mOrientation;
                this.mGravity = orig.mGravity;
                this.mCanConstantState = true;
                this.mCheckedConstantState = true;
            }
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            return new ClipDrawable(this, (Resources) null);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable(Resources res) {
            return new ClipDrawable(this, res);
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

    private ClipDrawable(ClipState state, Resources res) {
        this.mTmpRect = new Rect();
        this.mClipState = new ClipState(state, this, res);
    }
}