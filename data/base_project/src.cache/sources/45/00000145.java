package android.app;

import android.animation.LayoutTransition;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.internal.R;

/* loaded from: FragmentBreadCrumbs.class */
public class FragmentBreadCrumbs extends ViewGroup implements FragmentManager.OnBackStackChangedListener {
    Activity mActivity;
    LayoutInflater mInflater;
    LinearLayout mContainer;
    int mMaxVisible;
    BackStackRecord mTopEntry;
    BackStackRecord mParentEntry;
    private View.OnClickListener mParentClickListener;
    private OnBreadCrumbClickListener mOnBreadCrumbClickListener;
    private int mGravity;
    private static final int DEFAULT_GRAVITY = 8388627;
    private View.OnClickListener mOnClickListener;

    /* loaded from: FragmentBreadCrumbs$OnBreadCrumbClickListener.class */
    public interface OnBreadCrumbClickListener {
        boolean onBreadCrumbClick(FragmentManager.BackStackEntry backStackEntry, int i);
    }

    public FragmentBreadCrumbs(Context context) {
        this(context, null);
    }

    public FragmentBreadCrumbs(Context context, AttributeSet attrs) {
        this(context, attrs, 16973961);
    }

    public FragmentBreadCrumbs(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mMaxVisible = -1;
        this.mOnClickListener = new View.OnClickListener() { // from class: android.app.FragmentBreadCrumbs.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                if (v.getTag() instanceof FragmentManager.BackStackEntry) {
                    FragmentManager.BackStackEntry bse = (FragmentManager.BackStackEntry) v.getTag();
                    if (bse == FragmentBreadCrumbs.this.mParentEntry) {
                        if (FragmentBreadCrumbs.this.mParentClickListener != null) {
                            FragmentBreadCrumbs.this.mParentClickListener.onClick(v);
                            return;
                        }
                        return;
                    }
                    if (FragmentBreadCrumbs.this.mOnBreadCrumbClickListener != null) {
                        if (FragmentBreadCrumbs.this.mOnBreadCrumbClickListener.onBreadCrumbClick(bse == FragmentBreadCrumbs.this.mTopEntry ? null : bse, 0)) {
                            return;
                        }
                    }
                    if (bse == FragmentBreadCrumbs.this.mTopEntry) {
                        FragmentBreadCrumbs.this.mActivity.getFragmentManager().popBackStack();
                    } else {
                        FragmentBreadCrumbs.this.mActivity.getFragmentManager().popBackStack(bse.getId(), 0);
                    }
                }
            }
        };
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FragmentBreadCrumbs, defStyle, 0);
        this.mGravity = a.getInt(0, DEFAULT_GRAVITY);
        a.recycle();
    }

    public void setActivity(Activity a) {
        this.mActivity = a;
        this.mInflater = (LayoutInflater) a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mContainer = (LinearLayout) this.mInflater.inflate(R.layout.fragment_bread_crumbs, (ViewGroup) this, false);
        addView(this.mContainer);
        a.getFragmentManager().addOnBackStackChangedListener(this);
        updateCrumbs();
        setLayoutTransition(new LayoutTransition());
    }

    public void setMaxVisible(int visibleCrumbs) {
        if (visibleCrumbs < 1) {
            throw new IllegalArgumentException("visibleCrumbs must be greater than zero");
        }
        this.mMaxVisible = visibleCrumbs;
    }

    public void setParentTitle(CharSequence title, CharSequence shortTitle, View.OnClickListener listener) {
        this.mParentEntry = createBackStackEntry(title, shortTitle);
        this.mParentClickListener = listener;
        updateCrumbs();
    }

    public void setOnBreadCrumbClickListener(OnBreadCrumbClickListener listener) {
        this.mOnBreadCrumbClickListener = listener;
    }

    private BackStackRecord createBackStackEntry(CharSequence title, CharSequence shortTitle) {
        if (title == null) {
            return null;
        }
        BackStackRecord entry = new BackStackRecord((FragmentManagerImpl) this.mActivity.getFragmentManager());
        entry.setBreadCrumbTitle(title);
        entry.setBreadCrumbShortTitle(shortTitle);
        return entry;
    }

    public void setTitle(CharSequence title, CharSequence shortTitle) {
        this.mTopEntry = createBackStackEntry(title, shortTitle);
        updateCrumbs();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        int childLeft;
        int childRight;
        int childCount = getChildCount();
        if (childCount == 0) {
            return;
        }
        View child = getChildAt(0);
        int childTop = this.mPaddingTop;
        int childBottom = (this.mPaddingTop + child.getMeasuredHeight()) - this.mPaddingBottom;
        int layoutDirection = getLayoutDirection();
        int horizontalGravity = this.mGravity & 8388615;
        switch (Gravity.getAbsoluteGravity(horizontalGravity, layoutDirection)) {
            case 1:
                childLeft = this.mPaddingLeft + (((this.mRight - this.mLeft) - child.getMeasuredWidth()) / 2);
                childRight = childLeft + child.getMeasuredWidth();
                break;
            case 2:
            case 3:
            case 4:
            default:
                childLeft = this.mPaddingLeft;
                childRight = childLeft + child.getMeasuredWidth();
                break;
            case 5:
                childRight = (this.mRight - this.mLeft) - this.mPaddingRight;
                childLeft = childRight - child.getMeasuredWidth();
                break;
        }
        if (childLeft < this.mPaddingLeft) {
            childLeft = this.mPaddingLeft;
        }
        if (childRight > (this.mRight - this.mLeft) - this.mPaddingRight) {
            childRight = (this.mRight - this.mLeft) - this.mPaddingRight;
        }
        child.layout(childLeft, childTop, childRight, childBottom);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        int maxHeight = 0;
        int maxWidth = 0;
        int measuredChildState = 0;
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8) {
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
                maxWidth = Math.max(maxWidth, child.getMeasuredWidth());
                maxHeight = Math.max(maxHeight, child.getMeasuredHeight());
                measuredChildState = combineMeasuredStates(measuredChildState, child.getMeasuredState());
            }
        }
        setMeasuredDimension(resolveSizeAndState(Math.max(maxWidth + this.mPaddingLeft + this.mPaddingRight, getSuggestedMinimumWidth()), widthMeasureSpec, measuredChildState), resolveSizeAndState(Math.max(maxHeight + this.mPaddingTop + this.mPaddingBottom, getSuggestedMinimumHeight()), heightMeasureSpec, measuredChildState << 16));
    }

    @Override // android.app.FragmentManager.OnBackStackChangedListener
    public void onBackStackChanged() {
        updateCrumbs();
    }

    private int getPreEntryCount() {
        return (this.mTopEntry != null ? 1 : 0) + (this.mParentEntry != null ? 1 : 0);
    }

    private FragmentManager.BackStackEntry getPreEntry(int index) {
        if (this.mParentEntry != null) {
            return index == 0 ? this.mParentEntry : this.mTopEntry;
        }
        return this.mTopEntry;
    }

    void updateCrumbs() {
        FragmentManager fm = this.mActivity.getFragmentManager();
        int numEntries = fm.getBackStackEntryCount();
        int numPreEntries = getPreEntryCount();
        int numViews = this.mContainer.getChildCount();
        int i = 0;
        while (i < numEntries + numPreEntries) {
            FragmentManager.BackStackEntry bse = i < numPreEntries ? getPreEntry(i) : fm.getBackStackEntryAt(i - numPreEntries);
            if (i < numViews) {
                View v = this.mContainer.getChildAt(i);
                Object tag = v.getTag();
                if (tag != bse) {
                    for (int j = i; j < numViews; j++) {
                        this.mContainer.removeViewAt(i);
                    }
                    numViews = i;
                }
            }
            if (i >= numViews) {
                View item = this.mInflater.inflate(R.layout.fragment_bread_crumb_item, (ViewGroup) this, false);
                TextView text = (TextView) item.findViewById(16908310);
                text.setText(bse.getBreadCrumbTitle());
                text.setTag(bse);
                if (i == 0) {
                    item.findViewById(R.id.left_icon).setVisibility(8);
                }
                this.mContainer.addView(item);
                text.setOnClickListener(this.mOnClickListener);
            }
            i++;
        }
        int viewI = numEntries + numPreEntries;
        int numViews2 = this.mContainer.getChildCount();
        while (numViews2 > viewI) {
            this.mContainer.removeViewAt(numViews2 - 1);
            numViews2--;
        }
        int i2 = 0;
        while (i2 < numViews2) {
            View child = this.mContainer.getChildAt(i2);
            child.findViewById(16908310).setEnabled(i2 < numViews2 - 1);
            if (this.mMaxVisible > 0) {
                child.setVisibility(i2 < numViews2 - this.mMaxVisible ? 8 : 0);
                View leftIcon = child.findViewById(R.id.left_icon);
                leftIcon.setVisibility((i2 <= numViews2 - this.mMaxVisible || i2 == 0) ? 8 : 0);
            }
            i2++;
        }
    }
}