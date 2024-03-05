package android.widget;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.RemotableViewMethod;
import android.view.View;
import android.view.ViewDebug;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.RemoteViews;
import com.android.internal.R;
import java.io.IOException;
import java.io.InputStream;

@RemoteViews.RemoteView
/* loaded from: ImageView.class */
public class ImageView extends View {
    private Uri mUri;
    private int mResource;
    private Matrix mMatrix;
    private ScaleType mScaleType;
    private boolean mHaveFrame;
    private boolean mAdjustViewBounds;
    private int mMaxWidth;
    private int mMaxHeight;
    private ColorFilter mColorFilter;
    private Xfermode mXfermode;
    private int mAlpha;
    private int mViewAlphaScale;
    private boolean mColorMod;
    private Drawable mDrawable;
    private int[] mState;
    private boolean mMergeState;
    private int mLevel;
    private int mDrawableWidth;
    private int mDrawableHeight;
    private Matrix mDrawMatrix;
    private RectF mTempSrc;
    private RectF mTempDst;
    private boolean mCropToPadding;
    private int mBaseline;
    private boolean mBaselineAlignBottom;
    private boolean mAdjustViewBoundsCompat;
    private static final ScaleType[] sScaleTypeArray = {ScaleType.MATRIX, ScaleType.FIT_XY, ScaleType.FIT_START, ScaleType.FIT_CENTER, ScaleType.FIT_END, ScaleType.CENTER, ScaleType.CENTER_CROP, ScaleType.CENTER_INSIDE};
    private static final Matrix.ScaleToFit[] sS2FArray = {Matrix.ScaleToFit.FILL, Matrix.ScaleToFit.START, Matrix.ScaleToFit.CENTER, Matrix.ScaleToFit.END};

    public ImageView(Context context) {
        super(context);
        this.mResource = 0;
        this.mHaveFrame = false;
        this.mAdjustViewBounds = false;
        this.mMaxWidth = Integer.MAX_VALUE;
        this.mMaxHeight = Integer.MAX_VALUE;
        this.mAlpha = 255;
        this.mViewAlphaScale = 256;
        this.mColorMod = false;
        this.mDrawable = null;
        this.mState = null;
        this.mMergeState = false;
        this.mLevel = 0;
        this.mDrawMatrix = null;
        this.mTempSrc = new RectF();
        this.mTempDst = new RectF();
        this.mBaseline = -1;
        this.mBaselineAlignBottom = false;
        this.mAdjustViewBoundsCompat = false;
        initImageView();
    }

    public ImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mResource = 0;
        this.mHaveFrame = false;
        this.mAdjustViewBounds = false;
        this.mMaxWidth = Integer.MAX_VALUE;
        this.mMaxHeight = Integer.MAX_VALUE;
        this.mAlpha = 255;
        this.mViewAlphaScale = 256;
        this.mColorMod = false;
        this.mDrawable = null;
        this.mState = null;
        this.mMergeState = false;
        this.mLevel = 0;
        this.mDrawMatrix = null;
        this.mTempSrc = new RectF();
        this.mTempDst = new RectF();
        this.mBaseline = -1;
        this.mBaselineAlignBottom = false;
        this.mAdjustViewBoundsCompat = false;
        initImageView();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImageView, defStyle, 0);
        Drawable d = a.getDrawable(0);
        if (d != null) {
            setImageDrawable(d);
        }
        this.mBaselineAlignBottom = a.getBoolean(6, false);
        this.mBaseline = a.getDimensionPixelSize(8, -1);
        setAdjustViewBounds(a.getBoolean(2, false));
        setMaxWidth(a.getDimensionPixelSize(3, Integer.MAX_VALUE));
        setMaxHeight(a.getDimensionPixelSize(4, Integer.MAX_VALUE));
        int index = a.getInt(1, -1);
        if (index >= 0) {
            setScaleType(sScaleTypeArray[index]);
        }
        int tint = a.getInt(5, 0);
        if (tint != 0) {
            setColorFilter(tint);
        }
        int alpha = a.getInt(9, 255);
        if (alpha != 255) {
            setAlpha(alpha);
        }
        this.mCropToPadding = a.getBoolean(7, false);
        a.recycle();
    }

    private void initImageView() {
        this.mMatrix = new Matrix();
        this.mScaleType = ScaleType.FIT_CENTER;
        this.mAdjustViewBoundsCompat = this.mContext.getApplicationInfo().targetSdkVersion <= 17;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public boolean verifyDrawable(Drawable dr) {
        return this.mDrawable == dr || super.verifyDrawable(dr);
    }

    @Override // android.view.View
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        if (this.mDrawable != null) {
            this.mDrawable.jumpToCurrentState();
        }
    }

    @Override // android.view.View, android.graphics.drawable.Drawable.Callback
    public void invalidateDrawable(Drawable dr) {
        if (dr == this.mDrawable) {
            invalidate();
        } else {
            super.invalidateDrawable(dr);
        }
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return (getBackground() == null || getBackground().getCurrent() == null) ? false : true;
    }

    @Override // android.view.View
    public void onPopulateAccessibilityEvent(AccessibilityEvent event) {
        super.onPopulateAccessibilityEvent(event);
        CharSequence contentDescription = getContentDescription();
        if (!TextUtils.isEmpty(contentDescription)) {
            event.getText().add(contentDescription);
        }
    }

    public boolean getAdjustViewBounds() {
        return this.mAdjustViewBounds;
    }

    @RemotableViewMethod
    public void setAdjustViewBounds(boolean adjustViewBounds) {
        this.mAdjustViewBounds = adjustViewBounds;
        if (adjustViewBounds) {
            setScaleType(ScaleType.FIT_CENTER);
        }
    }

    public int getMaxWidth() {
        return this.mMaxWidth;
    }

    @RemotableViewMethod
    public void setMaxWidth(int maxWidth) {
        this.mMaxWidth = maxWidth;
    }

    public int getMaxHeight() {
        return this.mMaxHeight;
    }

    @RemotableViewMethod
    public void setMaxHeight(int maxHeight) {
        this.mMaxHeight = maxHeight;
    }

    public Drawable getDrawable() {
        return this.mDrawable;
    }

    @RemotableViewMethod
    public void setImageResource(int resId) {
        if (this.mUri != null || this.mResource != resId) {
            updateDrawable(null);
            this.mResource = resId;
            this.mUri = null;
            int oldWidth = this.mDrawableWidth;
            int oldHeight = this.mDrawableHeight;
            resolveUri();
            if (oldWidth != this.mDrawableWidth || oldHeight != this.mDrawableHeight) {
                requestLayout();
            }
            invalidate();
        }
    }

    @RemotableViewMethod
    public void setImageURI(Uri uri) {
        if (this.mResource == 0) {
            if (this.mUri == uri) {
                return;
            }
            if (uri != null && this.mUri != null && uri.equals(this.mUri)) {
                return;
            }
        }
        updateDrawable(null);
        this.mResource = 0;
        this.mUri = uri;
        int oldWidth = this.mDrawableWidth;
        int oldHeight = this.mDrawableHeight;
        resolveUri();
        if (oldWidth != this.mDrawableWidth || oldHeight != this.mDrawableHeight) {
            requestLayout();
        }
        invalidate();
    }

    public void setImageDrawable(Drawable drawable) {
        if (this.mDrawable != drawable) {
            this.mResource = 0;
            this.mUri = null;
            int oldWidth = this.mDrawableWidth;
            int oldHeight = this.mDrawableHeight;
            updateDrawable(drawable);
            if (oldWidth != this.mDrawableWidth || oldHeight != this.mDrawableHeight) {
                requestLayout();
            }
            invalidate();
        }
    }

    @RemotableViewMethod
    public void setImageBitmap(Bitmap bm) {
        setImageDrawable(new BitmapDrawable(this.mContext.getResources(), bm));
    }

    public void setImageState(int[] state, boolean merge) {
        this.mState = state;
        this.mMergeState = merge;
        if (this.mDrawable != null) {
            refreshDrawableState();
            resizeFromDrawable();
        }
    }

    @Override // android.view.View
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        resizeFromDrawable();
    }

    @RemotableViewMethod
    public void setImageLevel(int level) {
        this.mLevel = level;
        if (this.mDrawable != null) {
            this.mDrawable.setLevel(level);
            resizeFromDrawable();
        }
    }

    /* loaded from: ImageView$ScaleType.class */
    public enum ScaleType {
        MATRIX(0),
        FIT_XY(1),
        FIT_START(2),
        FIT_CENTER(3),
        FIT_END(4),
        CENTER(5),
        CENTER_CROP(6),
        CENTER_INSIDE(7);
        
        final int nativeInt;

        ScaleType(int ni) {
            this.nativeInt = ni;
        }
    }

    public void setScaleType(ScaleType scaleType) {
        if (scaleType == null) {
            throw new NullPointerException();
        }
        if (this.mScaleType != scaleType) {
            this.mScaleType = scaleType;
            setWillNotCacheDrawing(this.mScaleType == ScaleType.CENTER);
            requestLayout();
            invalidate();
        }
    }

    public ScaleType getScaleType() {
        return this.mScaleType;
    }

    public Matrix getImageMatrix() {
        if (this.mDrawMatrix == null) {
            return new Matrix(Matrix.IDENTITY_MATRIX);
        }
        return this.mDrawMatrix;
    }

    public void setImageMatrix(Matrix matrix) {
        if (matrix != null && matrix.isIdentity()) {
            matrix = null;
        }
        if ((matrix == null && !this.mMatrix.isIdentity()) || (matrix != null && !this.mMatrix.equals(matrix))) {
            this.mMatrix.set(matrix);
            configureBounds();
            invalidate();
        }
    }

    public boolean getCropToPadding() {
        return this.mCropToPadding;
    }

    public void setCropToPadding(boolean cropToPadding) {
        if (this.mCropToPadding != cropToPadding) {
            this.mCropToPadding = cropToPadding;
            requestLayout();
            invalidate();
        }
    }

    private void resolveUri() {
        Resources rsrc;
        if (this.mDrawable == null && (rsrc = getResources()) != null) {
            Drawable d = null;
            if (this.mResource != 0) {
                try {
                    d = rsrc.getDrawable(this.mResource);
                } catch (Exception e) {
                    Log.w("ImageView", "Unable to find resource: " + this.mResource, e);
                    this.mUri = null;
                }
            } else if (this.mUri == null) {
                return;
            } else {
                String scheme = this.mUri.getScheme();
                if (ContentResolver.SCHEME_ANDROID_RESOURCE.equals(scheme)) {
                    try {
                        ContentResolver.OpenResourceIdResult r = this.mContext.getContentResolver().getResourceId(this.mUri);
                        d = r.r.getDrawable(r.id);
                    } catch (Exception e2) {
                        Log.w("ImageView", "Unable to open content: " + this.mUri, e2);
                    }
                } else if ("content".equals(scheme) || ContentResolver.SCHEME_FILE.equals(scheme)) {
                    InputStream stream = null;
                    try {
                        try {
                            stream = this.mContext.getContentResolver().openInputStream(this.mUri);
                            d = Drawable.createFromStream(stream, null);
                            if (stream != null) {
                                try {
                                    stream.close();
                                } catch (IOException e3) {
                                    Log.w("ImageView", "Unable to close content: " + this.mUri, e3);
                                }
                            }
                        } catch (Exception e4) {
                            Log.w("ImageView", "Unable to open content: " + this.mUri, e4);
                            if (stream != null) {
                                try {
                                    stream.close();
                                } catch (IOException e5) {
                                    Log.w("ImageView", "Unable to close content: " + this.mUri, e5);
                                }
                            }
                        }
                    } catch (Throwable th) {
                        if (stream != null) {
                            try {
                                stream.close();
                            } catch (IOException e6) {
                                Log.w("ImageView", "Unable to close content: " + this.mUri, e6);
                            }
                        }
                        throw th;
                    }
                } else {
                    d = Drawable.createFromPath(this.mUri.toString());
                }
                if (d == null) {
                    System.out.println("resolveUri failed on bad bitmap uri: " + this.mUri);
                    this.mUri = null;
                }
            }
            updateDrawable(d);
        }
    }

    @Override // android.view.View
    public int[] onCreateDrawableState(int extraSpace) {
        if (this.mState == null) {
            return super.onCreateDrawableState(extraSpace);
        }
        if (!this.mMergeState) {
            return this.mState;
        }
        return mergeDrawableStates(super.onCreateDrawableState(extraSpace + this.mState.length), this.mState);
    }

    private void updateDrawable(Drawable d) {
        if (this.mDrawable != null) {
            this.mDrawable.setCallback(null);
            unscheduleDrawable(this.mDrawable);
        }
        this.mDrawable = d;
        if (d != null) {
            d.setCallback(this);
            if (d.isStateful()) {
                d.setState(getDrawableState());
            }
            d.setLevel(this.mLevel);
            d.setLayoutDirection(getLayoutDirection());
            this.mDrawableWidth = d.getIntrinsicWidth();
            this.mDrawableHeight = d.getIntrinsicHeight();
            applyColorMod();
            configureBounds();
            return;
        }
        this.mDrawableHeight = -1;
        this.mDrawableWidth = -1;
    }

    private void resizeFromDrawable() {
        Drawable d = this.mDrawable;
        if (d != null) {
            int w = d.getIntrinsicWidth();
            if (w < 0) {
                w = this.mDrawableWidth;
            }
            int h = d.getIntrinsicHeight();
            if (h < 0) {
                h = this.mDrawableHeight;
            }
            if (w != this.mDrawableWidth || h != this.mDrawableHeight) {
                this.mDrawableWidth = w;
                this.mDrawableHeight = h;
                requestLayout();
            }
        }
    }

    @Override // android.view.View
    public void onRtlPropertiesChanged(int layoutDirection) {
        super.onRtlPropertiesChanged(layoutDirection);
        if (this.mDrawable != null) {
            this.mDrawable.setLayoutDirection(layoutDirection);
        }
    }

    private static Matrix.ScaleToFit scaleTypeToScaleToFit(ScaleType st) {
        return sS2FArray[st.nativeInt - 1];
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w;
        int h;
        int widthSize;
        int heightSize;
        resolveUri();
        float desiredAspect = 0.0f;
        boolean resizeWidth = false;
        boolean resizeHeight = false;
        int widthSpecMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecMode = View.MeasureSpec.getMode(heightMeasureSpec);
        if (this.mDrawable == null) {
            this.mDrawableWidth = -1;
            this.mDrawableHeight = -1;
            h = 0;
            w = 0;
        } else {
            w = this.mDrawableWidth;
            h = this.mDrawableHeight;
            if (w <= 0) {
                w = 1;
            }
            if (h <= 0) {
                h = 1;
            }
            if (this.mAdjustViewBounds) {
                resizeWidth = widthSpecMode != 1073741824;
                resizeHeight = heightSpecMode != 1073741824;
                desiredAspect = w / h;
            }
        }
        int pleft = this.mPaddingLeft;
        int pright = this.mPaddingRight;
        int ptop = this.mPaddingTop;
        int pbottom = this.mPaddingBottom;
        if (resizeWidth || resizeHeight) {
            widthSize = resolveAdjustedSize(w + pleft + pright, this.mMaxWidth, widthMeasureSpec);
            heightSize = resolveAdjustedSize(h + ptop + pbottom, this.mMaxHeight, heightMeasureSpec);
            if (desiredAspect != 0.0f) {
                float actualAspect = ((widthSize - pleft) - pright) / ((heightSize - ptop) - pbottom);
                if (Math.abs(actualAspect - desiredAspect) > 1.0E-7d) {
                    boolean done = false;
                    if (resizeWidth) {
                        int newWidth = ((int) (desiredAspect * ((heightSize - ptop) - pbottom))) + pleft + pright;
                        if (!resizeHeight && !this.mAdjustViewBoundsCompat) {
                            widthSize = resolveAdjustedSize(newWidth, this.mMaxWidth, widthMeasureSpec);
                        }
                        if (newWidth <= widthSize) {
                            widthSize = newWidth;
                            done = true;
                        }
                    }
                    if (!done && resizeHeight) {
                        int newHeight = ((int) (((widthSize - pleft) - pright) / desiredAspect)) + ptop + pbottom;
                        if (!resizeWidth && !this.mAdjustViewBoundsCompat) {
                            heightSize = resolveAdjustedSize(newHeight, this.mMaxHeight, heightMeasureSpec);
                        }
                        if (newHeight <= heightSize) {
                            heightSize = newHeight;
                        }
                    }
                }
            }
        } else {
            int w2 = w + pleft + pright;
            int h2 = h + ptop + pbottom;
            int w3 = Math.max(w2, getSuggestedMinimumWidth());
            int h3 = Math.max(h2, getSuggestedMinimumHeight());
            widthSize = resolveSizeAndState(w3, widthMeasureSpec, 0);
            heightSize = resolveSizeAndState(h3, heightMeasureSpec, 0);
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    private int resolveAdjustedSize(int desiredSize, int maxSize, int measureSpec) {
        int result = desiredSize;
        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize = View.MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case Integer.MIN_VALUE:
                result = Math.min(Math.min(desiredSize, specSize), maxSize);
                break;
            case 0:
                result = Math.min(desiredSize, maxSize);
                break;
            case 1073741824:
                result = specSize;
                break;
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public boolean setFrame(int l, int t, int r, int b) {
        boolean changed = super.setFrame(l, t, r, b);
        this.mHaveFrame = true;
        configureBounds();
        return changed;
    }

    private void configureBounds() {
        float scale;
        float scale2;
        if (this.mDrawable == null || !this.mHaveFrame) {
            return;
        }
        int dwidth = this.mDrawableWidth;
        int dheight = this.mDrawableHeight;
        int vwidth = (getWidth() - this.mPaddingLeft) - this.mPaddingRight;
        int vheight = (getHeight() - this.mPaddingTop) - this.mPaddingBottom;
        boolean fits = (dwidth < 0 || vwidth == dwidth) && (dheight < 0 || vheight == dheight);
        if (dwidth <= 0 || dheight <= 0 || ScaleType.FIT_XY == this.mScaleType) {
            this.mDrawable.setBounds(0, 0, vwidth, vheight);
            this.mDrawMatrix = null;
            return;
        }
        this.mDrawable.setBounds(0, 0, dwidth, dheight);
        if (ScaleType.MATRIX == this.mScaleType) {
            if (this.mMatrix.isIdentity()) {
                this.mDrawMatrix = null;
            } else {
                this.mDrawMatrix = this.mMatrix;
            }
        } else if (fits) {
            this.mDrawMatrix = null;
        } else if (ScaleType.CENTER == this.mScaleType) {
            this.mDrawMatrix = this.mMatrix;
            this.mDrawMatrix.setTranslate((int) (((vwidth - dwidth) * 0.5f) + 0.5f), (int) (((vheight - dheight) * 0.5f) + 0.5f));
        } else if (ScaleType.CENTER_CROP == this.mScaleType) {
            this.mDrawMatrix = this.mMatrix;
            float dx = 0.0f;
            float dy = 0.0f;
            if (dwidth * vheight > vwidth * dheight) {
                scale2 = vheight / dheight;
                dx = (vwidth - (dwidth * scale2)) * 0.5f;
            } else {
                scale2 = vwidth / dwidth;
                dy = (vheight - (dheight * scale2)) * 0.5f;
            }
            this.mDrawMatrix.setScale(scale2, scale2);
            this.mDrawMatrix.postTranslate((int) (dx + 0.5f), (int) (dy + 0.5f));
        } else if (ScaleType.CENTER_INSIDE == this.mScaleType) {
            this.mDrawMatrix = this.mMatrix;
            if (dwidth <= vwidth && dheight <= vheight) {
                scale = 1.0f;
            } else {
                scale = Math.min(vwidth / dwidth, vheight / dheight);
            }
            float dx2 = (int) (((vwidth - (dwidth * scale)) * 0.5f) + 0.5f);
            float dy2 = (int) (((vheight - (dheight * scale)) * 0.5f) + 0.5f);
            this.mDrawMatrix.setScale(scale, scale);
            this.mDrawMatrix.postTranslate(dx2, dy2);
        } else {
            this.mTempSrc.set(0.0f, 0.0f, dwidth, dheight);
            this.mTempDst.set(0.0f, 0.0f, vwidth, vheight);
            this.mDrawMatrix = this.mMatrix;
            this.mDrawMatrix.setRectToRect(this.mTempSrc, this.mTempDst, scaleTypeToScaleToFit(this.mScaleType));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void drawableStateChanged() {
        super.drawableStateChanged();
        Drawable d = this.mDrawable;
        if (d != null && d.isStateful()) {
            d.setState(getDrawableState());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mDrawable == null || this.mDrawableWidth == 0 || this.mDrawableHeight == 0) {
            return;
        }
        if (this.mDrawMatrix == null && this.mPaddingTop == 0 && this.mPaddingLeft == 0) {
            this.mDrawable.draw(canvas);
            return;
        }
        int saveCount = canvas.getSaveCount();
        canvas.save();
        if (this.mCropToPadding) {
            int scrollX = this.mScrollX;
            int scrollY = this.mScrollY;
            canvas.clipRect(scrollX + this.mPaddingLeft, scrollY + this.mPaddingTop, ((scrollX + this.mRight) - this.mLeft) - this.mPaddingRight, ((scrollY + this.mBottom) - this.mTop) - this.mPaddingBottom);
        }
        canvas.translate(this.mPaddingLeft, this.mPaddingTop);
        if (this.mDrawMatrix != null) {
            canvas.concat(this.mDrawMatrix);
        }
        this.mDrawable.draw(canvas);
        canvas.restoreToCount(saveCount);
    }

    @Override // android.view.View
    @ViewDebug.ExportedProperty(category = "layout")
    public int getBaseline() {
        if (this.mBaselineAlignBottom) {
            return getMeasuredHeight();
        }
        return this.mBaseline;
    }

    public void setBaseline(int baseline) {
        if (this.mBaseline != baseline) {
            this.mBaseline = baseline;
            requestLayout();
        }
    }

    public void setBaselineAlignBottom(boolean aligned) {
        if (this.mBaselineAlignBottom != aligned) {
            this.mBaselineAlignBottom = aligned;
            requestLayout();
        }
    }

    public boolean getBaselineAlignBottom() {
        return this.mBaselineAlignBottom;
    }

    public final void setColorFilter(int color, PorterDuff.Mode mode) {
        setColorFilter(new PorterDuffColorFilter(color, mode));
    }

    @RemotableViewMethod
    public final void setColorFilter(int color) {
        setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    public final void clearColorFilter() {
        setColorFilter((ColorFilter) null);
    }

    public final void setXfermode(Xfermode mode) {
        if (this.mXfermode != mode) {
            this.mXfermode = mode;
            this.mColorMod = true;
            applyColorMod();
            invalidate();
        }
    }

    public ColorFilter getColorFilter() {
        return this.mColorFilter;
    }

    public void setColorFilter(ColorFilter cf) {
        if (this.mColorFilter != cf) {
            this.mColorFilter = cf;
            this.mColorMod = true;
            applyColorMod();
            invalidate();
        }
    }

    public int getImageAlpha() {
        return this.mAlpha;
    }

    @RemotableViewMethod
    public void setImageAlpha(int alpha) {
        setAlpha(alpha);
    }

    @RemotableViewMethod
    @Deprecated
    public void setAlpha(int alpha) {
        int alpha2 = alpha & 255;
        if (this.mAlpha != alpha2) {
            this.mAlpha = alpha2;
            this.mColorMod = true;
            applyColorMod();
            invalidate();
        }
    }

    private void applyColorMod() {
        if (this.mDrawable != null && this.mColorMod) {
            this.mDrawable = this.mDrawable.mutate();
            this.mDrawable.setColorFilter(this.mColorFilter);
            this.mDrawable.setXfermode(this.mXfermode);
            this.mDrawable.setAlpha((this.mAlpha * this.mViewAlphaScale) >> 8);
        }
    }

    @Override // android.view.View
    @RemotableViewMethod
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (this.mDrawable != null) {
            this.mDrawable.setVisible(visibility == 0, false);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mDrawable != null) {
            this.mDrawable.setVisible(getVisibility() == 0, false);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mDrawable != null) {
            this.mDrawable.setVisible(false, false);
        }
    }

    @Override // android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(ImageView.class.getName());
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(ImageView.class.getName());
    }
}