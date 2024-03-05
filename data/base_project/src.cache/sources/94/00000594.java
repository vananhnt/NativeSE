package android.graphics.drawable;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewDebug;
import com.android.internal.R;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: ColorDrawable.class */
public class ColorDrawable extends Drawable {
    @ViewDebug.ExportedProperty(deepExport = true, prefix = "state_")
    private ColorState mState;
    private final Paint mPaint;
    private boolean mMutated;

    public ColorDrawable() {
        this((ColorState) null);
    }

    public ColorDrawable(int color) {
        this((ColorState) null);
        setColor(color);
    }

    private ColorDrawable(ColorState state) {
        this.mPaint = new Paint();
        this.mState = new ColorState(state);
    }

    @Override // android.graphics.drawable.Drawable
    public int getChangingConfigurations() {
        return super.getChangingConfigurations() | this.mState.mChangingConfigurations;
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable mutate() {
        if (!this.mMutated && super.mutate() == this) {
            this.mState = new ColorState(this.mState);
            this.mMutated = true;
        }
        return this;
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        if ((this.mState.mUseColor >>> 24) != 0) {
            this.mPaint.setColor(this.mState.mUseColor);
            canvas.drawRect(getBounds(), this.mPaint);
        }
    }

    public int getColor() {
        return this.mState.mUseColor;
    }

    public void setColor(int color) {
        if (this.mState.mBaseColor != color || this.mState.mUseColor != color) {
            invalidateSelf();
            ColorState colorState = this.mState;
            this.mState.mUseColor = color;
            colorState.mBaseColor = color;
        }
    }

    @Override // android.graphics.drawable.Drawable
    public int getAlpha() {
        return this.mState.mUseColor >>> 24;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        int baseAlpha = this.mState.mBaseColor >>> 24;
        int useAlpha = (baseAlpha * (alpha + (alpha >> 7))) >> 8;
        int oldUseColor = this.mState.mUseColor;
        this.mState.mUseColor = ((this.mState.mBaseColor << 8) >>> 8) | (useAlpha << 24);
        if (oldUseColor != this.mState.mUseColor) {
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        switch (this.mState.mUseColor >>> 24) {
            case 0:
                return -2;
            case 255:
                return -1;
            default:
                return -3;
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs) throws XmlPullParserException, IOException {
        super.inflate(r, parser, attrs);
        TypedArray a = r.obtainAttributes(attrs, R.styleable.ColorDrawable);
        int color = a.getColor(0, this.mState.mBaseColor);
        ColorState colorState = this.mState;
        this.mState.mUseColor = color;
        colorState.mBaseColor = color;
        a.recycle();
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable.ConstantState getConstantState() {
        this.mState.mChangingConfigurations = getChangingConfigurations();
        return this.mState;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ColorDrawable$ColorState.class */
    public static final class ColorState extends Drawable.ConstantState {
        int mBaseColor;
        @ViewDebug.ExportedProperty
        int mUseColor;
        int mChangingConfigurations;

        ColorState(ColorState state) {
            if (state != null) {
                this.mBaseColor = state.mBaseColor;
                this.mUseColor = state.mUseColor;
                this.mChangingConfigurations = state.mChangingConfigurations;
            }
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            return new ColorDrawable(this);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable(Resources res) {
            return new ColorDrawable(this);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public int getChangingConfigurations() {
            return this.mChangingConfigurations;
        }
    }
}