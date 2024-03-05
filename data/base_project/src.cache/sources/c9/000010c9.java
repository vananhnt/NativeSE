package android.support.v4.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;
import java.lang.ref.WeakReference;

/* loaded from: PagerTitleStrip.class */
public class PagerTitleStrip extends ViewGroup implements ViewPager.Decor {
    private static final PagerTitleStripImpl IMPL;
    private static final float SIDE_ALPHA = 0.6f;
    private static final String TAG = "PagerTitleStrip";
    private static final int TEXT_SPACING = 16;
    TextView mCurrText;
    private int mGravity;
    private int mLastKnownCurrentPage;
    private float mLastKnownPositionOffset;
    TextView mNextText;
    private int mNonPrimaryAlpha;
    private final PageListener mPageListener;
    ViewPager mPager;
    TextView mPrevText;
    private int mScaledTextSpacing;
    int mTextColor;
    private boolean mUpdatingPositions;
    private boolean mUpdatingText;
    private WeakReference<PagerAdapter> mWatchingAdapter;
    private static final int[] ATTRS = {16842804, 16842901, 16842904, 16842927};
    private static final int[] TEXT_ATTRS = {16843660};

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: PagerTitleStrip$PageListener.class */
    public class PageListener extends DataSetObserver implements ViewPager.OnPageChangeListener, ViewPager.OnAdapterChangeListener {
        private int mScrollState;
        final PagerTitleStrip this$0;

        private PageListener(PagerTitleStrip pagerTitleStrip) {
            this.this$0 = pagerTitleStrip;
        }

        @Override // android.support.v4.view.ViewPager.OnAdapterChangeListener
        public void onAdapterChanged(PagerAdapter pagerAdapter, PagerAdapter pagerAdapter2) {
            this.this$0.updateAdapter(pagerAdapter, pagerAdapter2);
        }

        @Override // android.database.DataSetObserver
        public void onChanged() {
            PagerTitleStrip pagerTitleStrip = this.this$0;
            pagerTitleStrip.updateText(pagerTitleStrip.mPager.getCurrentItem(), this.this$0.mPager.getAdapter());
            float f = 0.0f;
            if (this.this$0.mLastKnownPositionOffset >= 0.0f) {
                f = this.this$0.mLastKnownPositionOffset;
            }
            PagerTitleStrip pagerTitleStrip2 = this.this$0;
            pagerTitleStrip2.updateTextPositions(pagerTitleStrip2.mPager.getCurrentItem(), f, true);
        }

        @Override // android.support.v4.view.ViewPager.OnPageChangeListener
        public void onPageScrollStateChanged(int i) {
            this.mScrollState = i;
        }

        @Override // android.support.v4.view.ViewPager.OnPageChangeListener
        public void onPageScrolled(int i, float f, int i2) {
            if (f > 0.5f) {
                i++;
            }
            this.this$0.updateTextPositions(i, f, false);
        }

        @Override // android.support.v4.view.ViewPager.OnPageChangeListener
        public void onPageSelected(int i) {
            if (this.mScrollState == 0) {
                PagerTitleStrip pagerTitleStrip = this.this$0;
                pagerTitleStrip.updateText(pagerTitleStrip.mPager.getCurrentItem(), this.this$0.mPager.getAdapter());
                float f = 0.0f;
                if (this.this$0.mLastKnownPositionOffset >= 0.0f) {
                    f = this.this$0.mLastKnownPositionOffset;
                }
                PagerTitleStrip pagerTitleStrip2 = this.this$0;
                pagerTitleStrip2.updateTextPositions(pagerTitleStrip2.mPager.getCurrentItem(), f, true);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: PagerTitleStrip$PagerTitleStripImpl.class */
    public interface PagerTitleStripImpl {
        void setSingleLineAllCaps(TextView textView);
    }

    /* loaded from: PagerTitleStrip$PagerTitleStripImplBase.class */
    static class PagerTitleStripImplBase implements PagerTitleStripImpl {
        PagerTitleStripImplBase() {
        }

        @Override // android.support.v4.view.PagerTitleStrip.PagerTitleStripImpl
        public void setSingleLineAllCaps(TextView textView) {
            textView.setSingleLine();
        }
    }

    /* loaded from: PagerTitleStrip$PagerTitleStripImplIcs.class */
    static class PagerTitleStripImplIcs implements PagerTitleStripImpl {
        PagerTitleStripImplIcs() {
        }

        @Override // android.support.v4.view.PagerTitleStrip.PagerTitleStripImpl
        public void setSingleLineAllCaps(TextView textView) {
            PagerTitleStripIcs.setSingleLineAllCaps(textView);
        }
    }

    static {
        if (Build.VERSION.SDK_INT >= 14) {
            IMPL = new PagerTitleStripImplIcs();
        } else {
            IMPL = new PagerTitleStripImplBase();
        }
    }

    public PagerTitleStrip(Context context) {
        this(context, null);
    }

    public PagerTitleStrip(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mLastKnownCurrentPage = -1;
        this.mLastKnownPositionOffset = -1.0f;
        this.mPageListener = new PageListener();
        TextView textView = new TextView(context);
        this.mPrevText = textView;
        addView(textView);
        TextView textView2 = new TextView(context);
        this.mCurrText = textView2;
        addView(textView2);
        TextView textView3 = new TextView(context);
        this.mNextText = textView3;
        addView(textView3);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, ATTRS);
        int resourceId = obtainStyledAttributes.getResourceId(0, 0);
        if (resourceId != 0) {
            this.mPrevText.setTextAppearance(context, resourceId);
            this.mCurrText.setTextAppearance(context, resourceId);
            this.mNextText.setTextAppearance(context, resourceId);
        }
        int dimensionPixelSize = obtainStyledAttributes.getDimensionPixelSize(1, 0);
        if (dimensionPixelSize != 0) {
            setTextSize(0, dimensionPixelSize);
        }
        if (obtainStyledAttributes.hasValue(2)) {
            int color = obtainStyledAttributes.getColor(2, 0);
            this.mPrevText.setTextColor(color);
            this.mCurrText.setTextColor(color);
            this.mNextText.setTextColor(color);
        }
        this.mGravity = obtainStyledAttributes.getInteger(3, 80);
        obtainStyledAttributes.recycle();
        this.mTextColor = this.mCurrText.getTextColors().getDefaultColor();
        setNonPrimaryAlpha(0.6f);
        this.mPrevText.setEllipsize(TextUtils.TruncateAt.END);
        this.mCurrText.setEllipsize(TextUtils.TruncateAt.END);
        this.mNextText.setEllipsize(TextUtils.TruncateAt.END);
        boolean z = false;
        if (resourceId != 0) {
            TypedArray obtainStyledAttributes2 = context.obtainStyledAttributes(resourceId, TEXT_ATTRS);
            z = obtainStyledAttributes2.getBoolean(0, false);
            obtainStyledAttributes2.recycle();
        }
        if (z) {
            setSingleLineAllCaps(this.mPrevText);
            setSingleLineAllCaps(this.mCurrText);
            setSingleLineAllCaps(this.mNextText);
        } else {
            this.mPrevText.setSingleLine();
            this.mCurrText.setSingleLine();
            this.mNextText.setSingleLine();
        }
        this.mScaledTextSpacing = (int) (16.0f * context.getResources().getDisplayMetrics().density);
    }

    private static void setSingleLineAllCaps(TextView textView) {
        IMPL.setSingleLineAllCaps(textView);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getMinHeight() {
        int i = 0;
        Drawable background = getBackground();
        if (background != null) {
            i = background.getIntrinsicHeight();
        }
        return i;
    }

    public int getTextSpacing() {
        return this.mScaledTextSpacing;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        ViewParent parent = getParent();
        if (!(parent instanceof ViewPager)) {
            throw new IllegalStateException("PagerTitleStrip must be a direct child of a ViewPager.");
        }
        ViewPager viewPager = (ViewPager) parent;
        PagerAdapter adapter = viewPager.getAdapter();
        viewPager.setInternalPageChangeListener(this.mPageListener);
        viewPager.setOnAdapterChangeListener(this.mPageListener);
        this.mPager = viewPager;
        WeakReference<PagerAdapter> weakReference = this.mWatchingAdapter;
        updateAdapter(weakReference != null ? weakReference.get() : null, adapter);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ViewPager viewPager = this.mPager;
        if (viewPager != null) {
            updateAdapter(viewPager.getAdapter(), null);
            this.mPager.setInternalPageChangeListener(null);
            this.mPager.setOnAdapterChangeListener(null);
            this.mPager = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (this.mPager != null) {
            float f = this.mLastKnownPositionOffset;
            if (f < 0.0f) {
                f = 0.0f;
            }
            updateTextPositions(this.mLastKnownCurrentPage, f, true);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int i, int i2) {
        int mode = View.MeasureSpec.getMode(i);
        int mode2 = View.MeasureSpec.getMode(i2);
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        if (mode != 1073741824) {
            throw new IllegalStateException("Must measure with an exact width");
        }
        int minHeight = getMinHeight();
        int paddingTop = getPaddingTop() + getPaddingBottom();
        int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec((int) (size * 0.8f), Integer.MIN_VALUE);
        int makeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(size2 - paddingTop, Integer.MIN_VALUE);
        this.mPrevText.measure(makeMeasureSpec, makeMeasureSpec2);
        this.mCurrText.measure(makeMeasureSpec, makeMeasureSpec2);
        this.mNextText.measure(makeMeasureSpec, makeMeasureSpec2);
        if (mode2 == 1073741824) {
            setMeasuredDimension(size, size2);
        } else {
            setMeasuredDimension(size, Math.max(minHeight, this.mCurrText.getMeasuredHeight() + paddingTop));
        }
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestLayout() {
        if (this.mUpdatingText) {
            return;
        }
        super.requestLayout();
    }

    public void setGravity(int i) {
        this.mGravity = i;
        requestLayout();
    }

    public void setNonPrimaryAlpha(float f) {
        this.mNonPrimaryAlpha = ((int) (255.0f * f)) & 255;
        int i = (this.mNonPrimaryAlpha << 24) | (this.mTextColor & 16777215);
        this.mPrevText.setTextColor(i);
        this.mNextText.setTextColor(i);
    }

    public void setTextColor(int i) {
        this.mTextColor = i;
        this.mCurrText.setTextColor(i);
        int i2 = (this.mNonPrimaryAlpha << 24) | (this.mTextColor & 16777215);
        this.mPrevText.setTextColor(i2);
        this.mNextText.setTextColor(i2);
    }

    public void setTextSize(int i, float f) {
        this.mPrevText.setTextSize(i, f);
        this.mCurrText.setTextSize(i, f);
        this.mNextText.setTextSize(i, f);
    }

    public void setTextSpacing(int i) {
        this.mScaledTextSpacing = i;
        requestLayout();
    }

    void updateAdapter(PagerAdapter pagerAdapter, PagerAdapter pagerAdapter2) {
        if (pagerAdapter != null) {
            pagerAdapter.unregisterDataSetObserver(this.mPageListener);
            this.mWatchingAdapter = null;
        }
        if (pagerAdapter2 != null) {
            pagerAdapter2.registerDataSetObserver(this.mPageListener);
            this.mWatchingAdapter = new WeakReference<>(pagerAdapter2);
        }
        ViewPager viewPager = this.mPager;
        if (viewPager != null) {
            this.mLastKnownCurrentPage = -1;
            this.mLastKnownPositionOffset = -1.0f;
            updateText(viewPager.getCurrentItem(), pagerAdapter2);
            requestLayout();
        }
    }

    void updateText(int i, PagerAdapter pagerAdapter) {
        int count = pagerAdapter != null ? pagerAdapter.getCount() : 0;
        this.mUpdatingText = true;
        CharSequence charSequence = null;
        if (i >= 1 && pagerAdapter != null) {
            charSequence = pagerAdapter.getPageTitle(i - 1);
        }
        this.mPrevText.setText(charSequence);
        this.mCurrText.setText((pagerAdapter == null || i >= count) ? null : pagerAdapter.getPageTitle(i));
        this.mNextText.setText((i + 1 >= count || pagerAdapter == null) ? null : pagerAdapter.getPageTitle(i + 1));
        int width = getWidth();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int height = getHeight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec((int) (((width - paddingLeft) - paddingRight) * 0.8f), Integer.MIN_VALUE);
        int makeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec((height - paddingTop) - paddingBottom, Integer.MIN_VALUE);
        this.mPrevText.measure(makeMeasureSpec, makeMeasureSpec2);
        this.mCurrText.measure(makeMeasureSpec, makeMeasureSpec2);
        this.mNextText.measure(makeMeasureSpec, makeMeasureSpec2);
        this.mLastKnownCurrentPage = i;
        if (!this.mUpdatingPositions) {
            updateTextPositions(i, this.mLastKnownPositionOffset, false);
        }
        this.mUpdatingText = false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateTextPositions(int i, float f, boolean z) {
        int i2;
        int i3;
        int i4;
        if (i != this.mLastKnownCurrentPage) {
            updateText(i, this.mPager.getAdapter());
        } else if (!z && f == this.mLastKnownPositionOffset) {
            return;
        }
        this.mUpdatingPositions = true;
        int measuredWidth = this.mPrevText.getMeasuredWidth();
        int measuredWidth2 = this.mCurrText.getMeasuredWidth();
        int measuredWidth3 = this.mNextText.getMeasuredWidth();
        int i5 = measuredWidth2 / 2;
        int width = getWidth();
        int height = getHeight();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int i6 = paddingRight + i5;
        float f2 = f + 0.5f;
        if (f2 > 1.0f) {
            f2 -= 1.0f;
        }
        int i7 = ((width - i6) - ((int) (((width - (paddingLeft + i5)) - i6) * f2))) - (measuredWidth2 / 2);
        int i8 = i7 + measuredWidth2;
        int baseline = this.mPrevText.getBaseline();
        int baseline2 = this.mCurrText.getBaseline();
        int baseline3 = this.mNextText.getBaseline();
        int max = Math.max(Math.max(baseline, baseline2), baseline3);
        int i9 = max - baseline;
        int i10 = max - baseline2;
        int i11 = max - baseline3;
        int max2 = Math.max(Math.max(i9 + this.mPrevText.getMeasuredHeight(), i10 + this.mCurrText.getMeasuredHeight()), i11 + this.mNextText.getMeasuredHeight());
        int i12 = this.mGravity & 112;
        if (i12 == 16) {
            int i13 = (((height - paddingTop) - paddingBottom) - max2) / 2;
            i2 = i13 + i9;
            i3 = i13 + i10;
            i4 = i13 + i11;
        } else if (i12 != 80) {
            i2 = paddingTop + i9;
            i3 = paddingTop + i10;
            i4 = paddingTop + i11;
        } else {
            int i14 = (height - paddingBottom) - max2;
            i2 = i14 + i9;
            i3 = i14 + i10;
            i4 = i14 + i11;
        }
        TextView textView = this.mCurrText;
        textView.layout(i7, i3, i8, i3 + textView.getMeasuredHeight());
        int min = Math.min(paddingLeft, (i7 - this.mScaledTextSpacing) - measuredWidth);
        TextView textView2 = this.mPrevText;
        textView2.layout(min, i2, min + measuredWidth, i2 + textView2.getMeasuredHeight());
        int max3 = Math.max((width - paddingRight) - measuredWidth3, this.mScaledTextSpacing + i8);
        TextView textView3 = this.mNextText;
        textView3.layout(max3, i4, max3 + measuredWidth3, i4 + textView3.getMeasuredHeight());
        this.mLastKnownPositionOffset = f;
        this.mUpdatingPositions = false;
    }
}