package android.graphics.drawable;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/* loaded from: LayerDrawable.class */
public class LayerDrawable extends Drawable implements Drawable.Callback {
    LayerState mLayerState;
    private int mOpacityOverride;
    private int[] mPaddingL;
    private int[] mPaddingT;
    private int[] mPaddingR;
    private int[] mPaddingB;
    private final Rect mTmpRect;
    private boolean mMutated;

    public LayerDrawable(Drawable[] layers) {
        this(layers, (LayerState) null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public LayerDrawable(Drawable[] layers, LayerState state) {
        this(state, (Resources) null);
        int length = layers.length;
        ChildDrawable[] r = new ChildDrawable[length];
        for (int i = 0; i < length; i++) {
            r[i] = new ChildDrawable();
            r[i].mDrawable = layers[i];
            layers[i].setCallback(this);
            this.mLayerState.mChildrenChangingConfigurations |= layers[i].getChangingConfigurations();
        }
        this.mLayerState.mNum = length;
        this.mLayerState.mChildren = r;
        ensurePadding();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public LayerDrawable() {
        this((LayerState) null, (Resources) null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public LayerDrawable(LayerState state, Resources res) {
        this.mOpacityOverride = 0;
        this.mTmpRect = new Rect();
        LayerState as = createConstantState(state, res);
        this.mLayerState = as;
        if (as.mNum > 0) {
            ensurePadding();
        }
    }

    LayerState createConstantState(LayerState state, Resources res) {
        return new LayerState(state, this, res);
    }

    /* JADX WARN: Code restructure failed: missing block: B:29:0x0123, code lost:
        ensurePadding();
        onStateChange(getState());
     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x0130, code lost:
        return;
     */
    @Override // android.graphics.drawable.Drawable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void inflate(android.content.res.Resources r9, org.xmlpull.v1.XmlPullParser r10, android.util.AttributeSet r11) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            Method dump skipped, instructions count: 305
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: android.graphics.drawable.LayerDrawable.inflate(android.content.res.Resources, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet):void");
    }

    private void addLayer(Drawable layer, int id, int left, int top, int right, int bottom) {
        LayerState st = this.mLayerState;
        int N = st.mChildren != null ? st.mChildren.length : 0;
        int i = st.mNum;
        if (i >= N) {
            ChildDrawable[] nu = new ChildDrawable[N + 10];
            if (i > 0) {
                System.arraycopy(st.mChildren, 0, nu, 0, i);
            }
            st.mChildren = nu;
        }
        this.mLayerState.mChildrenChangingConfigurations |= layer.getChangingConfigurations();
        ChildDrawable childDrawable = new ChildDrawable();
        st.mChildren[i] = childDrawable;
        childDrawable.mId = id;
        childDrawable.mDrawable = layer;
        childDrawable.mDrawable.setAutoMirrored(isAutoMirrored());
        childDrawable.mInsetL = left;
        childDrawable.mInsetT = top;
        childDrawable.mInsetR = right;
        childDrawable.mInsetB = bottom;
        st.mNum++;
        layer.setCallback(this);
    }

    public Drawable findDrawableByLayerId(int id) {
        ChildDrawable[] layers = this.mLayerState.mChildren;
        for (int i = this.mLayerState.mNum - 1; i >= 0; i--) {
            if (layers[i].mId == id) {
                return layers[i].mDrawable;
            }
        }
        return null;
    }

    public void setId(int index, int id) {
        this.mLayerState.mChildren[index].mId = id;
    }

    public int getNumberOfLayers() {
        return this.mLayerState.mNum;
    }

    public Drawable getDrawable(int index) {
        return this.mLayerState.mChildren[index].mDrawable;
    }

    public int getId(int index) {
        return this.mLayerState.mChildren[index].mId;
    }

    public boolean setDrawableByLayerId(int id, Drawable drawable) {
        ChildDrawable[] layers = this.mLayerState.mChildren;
        for (int i = this.mLayerState.mNum - 1; i >= 0; i--) {
            if (layers[i].mId == id) {
                if (layers[i].mDrawable != null) {
                    if (drawable != null) {
                        Rect bounds = layers[i].mDrawable.getBounds();
                        drawable.setBounds(bounds);
                    }
                    layers[i].mDrawable.setCallback(null);
                }
                if (drawable != null) {
                    drawable.setCallback(this);
                }
                layers[i].mDrawable = drawable;
                return true;
            }
        }
        return false;
    }

    public void setLayerInset(int index, int l, int t, int r, int b) {
        ChildDrawable childDrawable = this.mLayerState.mChildren[index];
        childDrawable.mInsetL = l;
        childDrawable.mInsetT = t;
        childDrawable.mInsetR = r;
        childDrawable.mInsetB = b;
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
        ChildDrawable[] array = this.mLayerState.mChildren;
        int N = this.mLayerState.mNum;
        for (int i = 0; i < N; i++) {
            array[i].mDrawable.draw(canvas);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public int getChangingConfigurations() {
        return super.getChangingConfigurations() | this.mLayerState.mChangingConfigurations | this.mLayerState.mChildrenChangingConfigurations;
    }

    @Override // android.graphics.drawable.Drawable
    public boolean getPadding(Rect padding) {
        padding.left = 0;
        padding.top = 0;
        padding.right = 0;
        padding.bottom = 0;
        ChildDrawable[] array = this.mLayerState.mChildren;
        int N = this.mLayerState.mNum;
        for (int i = 0; i < N; i++) {
            reapplyPadding(i, array[i]);
            padding.left += this.mPaddingL[i];
            padding.top += this.mPaddingT[i];
            padding.right += this.mPaddingR[i];
            padding.bottom += this.mPaddingB[i];
        }
        return true;
    }

    @Override // android.graphics.drawable.Drawable
    public boolean setVisible(boolean visible, boolean restart) {
        boolean changed = super.setVisible(visible, restart);
        ChildDrawable[] array = this.mLayerState.mChildren;
        int N = this.mLayerState.mNum;
        for (int i = 0; i < N; i++) {
            array[i].mDrawable.setVisible(visible, restart);
        }
        return changed;
    }

    @Override // android.graphics.drawable.Drawable
    public void setDither(boolean dither) {
        ChildDrawable[] array = this.mLayerState.mChildren;
        int N = this.mLayerState.mNum;
        for (int i = 0; i < N; i++) {
            array[i].mDrawable.setDither(dither);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        ChildDrawable[] array = this.mLayerState.mChildren;
        int N = this.mLayerState.mNum;
        for (int i = 0; i < N; i++) {
            array[i].mDrawable.setAlpha(alpha);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public int getAlpha() {
        ChildDrawable[] array = this.mLayerState.mChildren;
        if (this.mLayerState.mNum > 0) {
            return array[0].mDrawable.getAlpha();
        }
        return super.getAlpha();
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter cf) {
        ChildDrawable[] array = this.mLayerState.mChildren;
        int N = this.mLayerState.mNum;
        for (int i = 0; i < N; i++) {
            array[i].mDrawable.setColorFilter(cf);
        }
    }

    public void setOpacity(int opacity) {
        this.mOpacityOverride = opacity;
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        if (this.mOpacityOverride != 0) {
            return this.mOpacityOverride;
        }
        return this.mLayerState.getOpacity();
    }

    @Override // android.graphics.drawable.Drawable
    public void setAutoMirrored(boolean mirrored) {
        this.mLayerState.mAutoMirrored = mirrored;
        ChildDrawable[] array = this.mLayerState.mChildren;
        int N = this.mLayerState.mNum;
        for (int i = 0; i < N; i++) {
            array[i].mDrawable.setAutoMirrored(mirrored);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isAutoMirrored() {
        return this.mLayerState.mAutoMirrored;
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isStateful() {
        return this.mLayerState.isStateful();
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onStateChange(int[] state) {
        ChildDrawable[] array = this.mLayerState.mChildren;
        int N = this.mLayerState.mNum;
        boolean paddingChanged = false;
        boolean changed = false;
        for (int i = 0; i < N; i++) {
            ChildDrawable r = array[i];
            if (r.mDrawable.setState(state)) {
                changed = true;
            }
            if (reapplyPadding(i, r)) {
                paddingChanged = true;
            }
        }
        if (paddingChanged) {
            onBoundsChange(getBounds());
        }
        return changed;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.graphics.drawable.Drawable
    public boolean onLevelChange(int level) {
        ChildDrawable[] array = this.mLayerState.mChildren;
        int N = this.mLayerState.mNum;
        boolean paddingChanged = false;
        boolean changed = false;
        for (int i = 0; i < N; i++) {
            ChildDrawable r = array[i];
            if (r.mDrawable.setLevel(level)) {
                changed = true;
            }
            if (reapplyPadding(i, r)) {
                paddingChanged = true;
            }
        }
        if (paddingChanged) {
            onBoundsChange(getBounds());
        }
        return changed;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.graphics.drawable.Drawable
    public void onBoundsChange(Rect bounds) {
        ChildDrawable[] array = this.mLayerState.mChildren;
        int N = this.mLayerState.mNum;
        int padL = 0;
        int padT = 0;
        int padR = 0;
        int padB = 0;
        for (int i = 0; i < N; i++) {
            ChildDrawable r = array[i];
            r.mDrawable.setBounds(bounds.left + r.mInsetL + padL, bounds.top + r.mInsetT + padT, (bounds.right - r.mInsetR) - padR, (bounds.bottom - r.mInsetB) - padB);
            padL += this.mPaddingL[i];
            padR += this.mPaddingR[i];
            padT += this.mPaddingT[i];
            padB += this.mPaddingB[i];
        }
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        int width = -1;
        ChildDrawable[] array = this.mLayerState.mChildren;
        int N = this.mLayerState.mNum;
        int padL = 0;
        int padR = 0;
        for (int i = 0; i < N; i++) {
            ChildDrawable r = array[i];
            int w = r.mDrawable.getIntrinsicWidth() + r.mInsetL + r.mInsetR + padL + padR;
            if (w > width) {
                width = w;
            }
            padL += this.mPaddingL[i];
            padR += this.mPaddingR[i];
        }
        return width;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        int height = -1;
        ChildDrawable[] array = this.mLayerState.mChildren;
        int N = this.mLayerState.mNum;
        int padT = 0;
        int padB = 0;
        for (int i = 0; i < N; i++) {
            ChildDrawable r = array[i];
            int h = r.mDrawable.getIntrinsicHeight() + r.mInsetT + r.mInsetB + padT + padB;
            if (h > height) {
                height = h;
            }
            padT += this.mPaddingT[i];
            padB += this.mPaddingB[i];
        }
        return height;
    }

    private boolean reapplyPadding(int i, ChildDrawable r) {
        Rect rect = this.mTmpRect;
        r.mDrawable.getPadding(rect);
        if (rect.left != this.mPaddingL[i] || rect.top != this.mPaddingT[i] || rect.right != this.mPaddingR[i] || rect.bottom != this.mPaddingB[i]) {
            this.mPaddingL[i] = rect.left;
            this.mPaddingT[i] = rect.top;
            this.mPaddingR[i] = rect.right;
            this.mPaddingB[i] = rect.bottom;
            return true;
        }
        return false;
    }

    private void ensurePadding() {
        int N = this.mLayerState.mNum;
        if (this.mPaddingL != null && this.mPaddingL.length >= N) {
            return;
        }
        this.mPaddingL = new int[N];
        this.mPaddingT = new int[N];
        this.mPaddingR = new int[N];
        this.mPaddingB = new int[N];
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable.ConstantState getConstantState() {
        if (this.mLayerState.canConstantState()) {
            this.mLayerState.mChangingConfigurations = getChangingConfigurations();
            return this.mLayerState;
        }
        return null;
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable mutate() {
        if (!this.mMutated && super.mutate() == this) {
            this.mLayerState = createConstantState(this.mLayerState, null);
            ChildDrawable[] array = this.mLayerState.mChildren;
            int N = this.mLayerState.mNum;
            for (int i = 0; i < N; i++) {
                array[i].mDrawable.mutate();
            }
            this.mMutated = true;
        }
        return this;
    }

    @Override // android.graphics.drawable.Drawable
    public void setLayoutDirection(int layoutDirection) {
        ChildDrawable[] array = this.mLayerState.mChildren;
        int N = this.mLayerState.mNum;
        for (int i = 0; i < N; i++) {
            array[i].mDrawable.setLayoutDirection(layoutDirection);
        }
        super.setLayoutDirection(layoutDirection);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: LayerDrawable$ChildDrawable.class */
    public static class ChildDrawable {
        public Drawable mDrawable;
        public int mInsetL;
        public int mInsetT;
        public int mInsetR;
        public int mInsetB;
        public int mId;

        ChildDrawable() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: LayerDrawable$LayerState.class */
    public static class LayerState extends Drawable.ConstantState {
        int mNum;
        ChildDrawable[] mChildren;
        int mChangingConfigurations;
        int mChildrenChangingConfigurations;
        private boolean mHaveOpacity;
        private int mOpacity;
        private boolean mHaveStateful;
        private boolean mStateful;
        private boolean mCheckedConstantState;
        private boolean mCanConstantState;
        private boolean mAutoMirrored;

        /* JADX INFO: Access modifiers changed from: package-private */
        public LayerState(LayerState orig, LayerDrawable owner, Resources res) {
            this.mHaveOpacity = false;
            this.mHaveStateful = false;
            if (orig != null) {
                ChildDrawable[] origChildDrawable = orig.mChildren;
                int N = orig.mNum;
                this.mNum = N;
                this.mChildren = new ChildDrawable[N];
                this.mChangingConfigurations = orig.mChangingConfigurations;
                this.mChildrenChangingConfigurations = orig.mChildrenChangingConfigurations;
                for (int i = 0; i < N; i++) {
                    ChildDrawable r = new ChildDrawable();
                    this.mChildren[i] = r;
                    ChildDrawable or = origChildDrawable[i];
                    if (res != null) {
                        r.mDrawable = or.mDrawable.getConstantState().newDrawable(res);
                    } else {
                        r.mDrawable = or.mDrawable.getConstantState().newDrawable();
                    }
                    r.mDrawable.setCallback(owner);
                    r.mDrawable.setLayoutDirection(or.mDrawable.getLayoutDirection());
                    r.mInsetL = or.mInsetL;
                    r.mInsetT = or.mInsetT;
                    r.mInsetR = or.mInsetR;
                    r.mInsetB = or.mInsetB;
                    r.mId = or.mId;
                }
                this.mHaveOpacity = orig.mHaveOpacity;
                this.mOpacity = orig.mOpacity;
                this.mHaveStateful = orig.mHaveStateful;
                this.mStateful = orig.mStateful;
                this.mCanConstantState = true;
                this.mCheckedConstantState = true;
                this.mAutoMirrored = orig.mAutoMirrored;
                return;
            }
            this.mNum = 0;
            this.mChildren = null;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            return new LayerDrawable(this, (Resources) null);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable(Resources res) {
            return new LayerDrawable(this, res);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public int getChangingConfigurations() {
            return this.mChangingConfigurations;
        }

        public final int getOpacity() {
            if (this.mHaveOpacity) {
                return this.mOpacity;
            }
            int N = this.mNum;
            int op = N > 0 ? this.mChildren[0].mDrawable.getOpacity() : -2;
            for (int i = 1; i < N; i++) {
                op = Drawable.resolveOpacity(op, this.mChildren[i].mDrawable.getOpacity());
            }
            this.mOpacity = op;
            this.mHaveOpacity = true;
            return op;
        }

        public final boolean isStateful() {
            if (this.mHaveStateful) {
                return this.mStateful;
            }
            boolean stateful = false;
            int N = this.mNum;
            int i = 0;
            while (true) {
                if (i >= N) {
                    break;
                } else if (!this.mChildren[i].mDrawable.isStateful()) {
                    i++;
                } else {
                    stateful = true;
                    break;
                }
            }
            this.mStateful = stateful;
            this.mHaveStateful = true;
            return stateful;
        }

        public boolean canConstantState() {
            if (!this.mCheckedConstantState && this.mChildren != null) {
                this.mCanConstantState = true;
                int N = this.mNum;
                int i = 0;
                while (true) {
                    if (i >= N) {
                        break;
                    } else if (this.mChildren[i].mDrawable.getConstantState() != null) {
                        i++;
                    } else {
                        this.mCanConstantState = false;
                        break;
                    }
                }
                this.mCheckedConstantState = true;
            }
            return this.mCanConstantState;
        }
    }
}