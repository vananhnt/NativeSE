package android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import com.android.internal.R;
import java.lang.ref.WeakReference;

@RemoteViews.RemoteView
/* loaded from: ViewStub.class */
public final class ViewStub extends View {
    private int mLayoutResource;
    private int mInflatedId;
    private WeakReference<View> mInflatedViewRef;
    private LayoutInflater mInflater;
    private OnInflateListener mInflateListener;

    /* loaded from: ViewStub$OnInflateListener.class */
    public interface OnInflateListener {
        void onInflate(ViewStub viewStub, View view);
    }

    public ViewStub(Context context) {
        this.mLayoutResource = 0;
        initialize(context);
    }

    public ViewStub(Context context, int layoutResource) {
        this.mLayoutResource = 0;
        this.mLayoutResource = layoutResource;
        initialize(context);
    }

    public ViewStub(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewStub(Context context, AttributeSet attrs, int defStyle) {
        this.mLayoutResource = 0;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ViewStub, defStyle, 0);
        this.mInflatedId = a.getResourceId(1, -1);
        this.mLayoutResource = a.getResourceId(0, 0);
        a.recycle();
        TypedArray a2 = context.obtainStyledAttributes(attrs, R.styleable.View, defStyle, 0);
        this.mID = a2.getResourceId(8, -1);
        a2.recycle();
        initialize(context);
    }

    private void initialize(Context context) {
        this.mContext = context;
        setVisibility(8);
        setWillNotDraw(true);
    }

    public int getInflatedId() {
        return this.mInflatedId;
    }

    @RemotableViewMethod
    public void setInflatedId(int inflatedId) {
        this.mInflatedId = inflatedId;
    }

    public int getLayoutResource() {
        return this.mLayoutResource;
    }

    @RemotableViewMethod
    public void setLayoutResource(int layoutResource) {
        this.mLayoutResource = layoutResource;
    }

    public void setLayoutInflater(LayoutInflater inflater) {
        this.mInflater = inflater;
    }

    public LayoutInflater getLayoutInflater() {
        return this.mInflater;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(0, 0);
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void dispatchDraw(Canvas canvas) {
    }

    @Override // android.view.View
    @RemotableViewMethod
    public void setVisibility(int visibility) {
        if (this.mInflatedViewRef != null) {
            View view = this.mInflatedViewRef.get();
            if (view != null) {
                view.setVisibility(visibility);
                return;
            }
            throw new IllegalStateException("setVisibility called on un-referenced view");
        }
        super.setVisibility(visibility);
        if (visibility == 0 || visibility == 4) {
            inflate();
        }
    }

    public View inflate() {
        LayoutInflater factory;
        ViewParent viewParent = getParent();
        if (viewParent != null && (viewParent instanceof ViewGroup)) {
            if (this.mLayoutResource != 0) {
                ViewGroup parent = (ViewGroup) viewParent;
                if (this.mInflater != null) {
                    factory = this.mInflater;
                } else {
                    factory = LayoutInflater.from(this.mContext);
                }
                View view = factory.inflate(this.mLayoutResource, parent, false);
                if (this.mInflatedId != -1) {
                    view.setId(this.mInflatedId);
                }
                int index = parent.indexOfChild(this);
                parent.removeViewInLayout(this);
                ViewGroup.LayoutParams layoutParams = getLayoutParams();
                if (layoutParams != null) {
                    parent.addView(view, index, layoutParams);
                } else {
                    parent.addView(view, index);
                }
                this.mInflatedViewRef = new WeakReference<>(view);
                if (this.mInflateListener != null) {
                    this.mInflateListener.onInflate(this, view);
                }
                return view;
            }
            throw new IllegalArgumentException("ViewStub must have a valid layoutResource");
        }
        throw new IllegalStateException("ViewStub must have a non-null ViewGroup viewParent");
    }

    public void setOnInflateListener(OnInflateListener inflateListener) {
        this.mInflateListener = inflateListener;
    }
}