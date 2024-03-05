package android.appwidget;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Process;
import android.os.UserHandle;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.RemoteViews;
import android.widget.RemoteViewsAdapter;
import android.widget.TextView;
import com.android.internal.R;

/* loaded from: AppWidgetHostView.class */
public class AppWidgetHostView extends FrameLayout {
    static final String TAG = "AppWidgetHostView";
    static final boolean LOGD = false;
    static final boolean CROSSFADE = false;
    static final int VIEW_MODE_NOINIT = 0;
    static final int VIEW_MODE_CONTENT = 1;
    static final int VIEW_MODE_ERROR = 2;
    static final int VIEW_MODE_DEFAULT = 3;
    static final int FADE_DURATION = 1000;
    static final LayoutInflater.Filter sInflaterFilter = new LayoutInflater.Filter() { // from class: android.appwidget.AppWidgetHostView.1
        @Override // android.view.LayoutInflater.Filter
        public boolean onLoadClass(Class clazz) {
            return clazz.isAnnotationPresent(RemoteViews.RemoteView.class);
        }
    };
    Context mContext;
    Context mRemoteContext;
    int mAppWidgetId;
    AppWidgetProviderInfo mInfo;
    View mView;
    int mViewMode;
    int mLayoutId;
    long mFadeStartTime;
    Bitmap mOld;
    Paint mOldPaint;
    private RemoteViews.OnClickHandler mOnClickHandler;
    private UserHandle mUser;

    public AppWidgetHostView(Context context) {
        this(context, 17432576, 17432577);
    }

    public AppWidgetHostView(Context context, RemoteViews.OnClickHandler handler) {
        this(context, 17432576, 17432577);
        this.mOnClickHandler = handler;
    }

    public AppWidgetHostView(Context context, int animationIn, int animationOut) {
        super(context);
        this.mViewMode = 0;
        this.mLayoutId = -1;
        this.mFadeStartTime = -1L;
        this.mOldPaint = new Paint();
        this.mContext = context;
        this.mUser = Process.myUserHandle();
        setIsRootNamespace(true);
    }

    public void setUserId(int userId) {
        this.mUser = new UserHandle(userId);
    }

    public void setOnClickHandler(RemoteViews.OnClickHandler handler) {
        this.mOnClickHandler = handler;
    }

    public void setAppWidget(int appWidgetId, AppWidgetProviderInfo info) {
        this.mAppWidgetId = appWidgetId;
        this.mInfo = info;
        if (info != null) {
            Rect padding = getDefaultPaddingForWidget(this.mContext, info.provider, null);
            setPadding(padding.left, padding.top, padding.right, padding.bottom);
            setContentDescription(info.label);
        }
    }

    public static Rect getDefaultPaddingForWidget(Context context, ComponentName component, Rect padding) {
        PackageManager packageManager = context.getPackageManager();
        if (padding == null) {
            padding = new Rect(0, 0, 0, 0);
        } else {
            padding.set(0, 0, 0, 0);
        }
        try {
            ApplicationInfo appInfo = packageManager.getApplicationInfo(component.getPackageName(), 0);
            if (appInfo.targetSdkVersion >= 14) {
                Resources r = context.getResources();
                padding.left = r.getDimensionPixelSize(R.dimen.default_app_widget_padding_left);
                padding.right = r.getDimensionPixelSize(R.dimen.default_app_widget_padding_right);
                padding.top = r.getDimensionPixelSize(R.dimen.default_app_widget_padding_top);
                padding.bottom = r.getDimensionPixelSize(R.dimen.default_app_widget_padding_bottom);
            }
            return padding;
        } catch (PackageManager.NameNotFoundException e) {
            return padding;
        }
    }

    public int getAppWidgetId() {
        return this.mAppWidgetId;
    }

    public AppWidgetProviderInfo getAppWidgetInfo() {
        return this.mInfo;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
        ParcelableSparseArray jail = new ParcelableSparseArray();
        super.dispatchSaveInstanceState(jail);
        container.put(generateId(), jail);
    }

    private int generateId() {
        int id = getId();
        return id == -1 ? this.mAppWidgetId : id;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        Parcelable parcelable = container.get(generateId());
        ParcelableSparseArray jail = null;
        if (parcelable != null && (parcelable instanceof ParcelableSparseArray)) {
            jail = (ParcelableSparseArray) parcelable;
        }
        if (jail == null) {
            jail = new ParcelableSparseArray();
        }
        try {
            super.dispatchRestoreInstanceState(jail);
        } catch (Exception e) {
            Log.e(TAG, "failed to restoreInstanceState for widget id: " + this.mAppWidgetId + ", " + (this.mInfo == null ? "null" : this.mInfo.provider), e);
        }
    }

    public void updateAppWidgetSize(Bundle newOptions, int minWidth, int minHeight, int maxWidth, int maxHeight) {
        updateAppWidgetSize(newOptions, minWidth, minHeight, maxWidth, maxHeight, false);
    }

    public void updateAppWidgetSize(Bundle newOptions, int minWidth, int minHeight, int maxWidth, int maxHeight, boolean ignorePadding) {
        if (newOptions == null) {
            newOptions = new Bundle();
        }
        Rect padding = new Rect();
        if (this.mInfo != null) {
            padding = getDefaultPaddingForWidget(this.mContext, this.mInfo.provider, padding);
        }
        float density = getResources().getDisplayMetrics().density;
        int xPaddingDips = (int) ((padding.left + padding.right) / density);
        int yPaddingDips = (int) ((padding.top + padding.bottom) / density);
        int newMinWidth = minWidth - (ignorePadding ? 0 : xPaddingDips);
        int newMinHeight = minHeight - (ignorePadding ? 0 : yPaddingDips);
        int newMaxWidth = maxWidth - (ignorePadding ? 0 : xPaddingDips);
        int newMaxHeight = maxHeight - (ignorePadding ? 0 : yPaddingDips);
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(this.mContext);
        Bundle oldOptions = widgetManager.getAppWidgetOptions(this.mAppWidgetId);
        boolean needsUpdate = false;
        if (newMinWidth != oldOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH) || newMinHeight != oldOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT) || newMaxWidth != oldOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH) || newMaxHeight != oldOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT)) {
            needsUpdate = true;
        }
        if (needsUpdate) {
            newOptions.putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, newMinWidth);
            newOptions.putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, newMinHeight);
            newOptions.putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, newMaxWidth);
            newOptions.putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, newMaxHeight);
            updateAppWidgetOptions(newOptions);
        }
    }

    public void updateAppWidgetOptions(Bundle options) {
        AppWidgetManager.getInstance(this.mContext).updateAppWidgetOptions(this.mAppWidgetId, options);
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup
    public FrameLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        Context context = this.mRemoteContext != null ? this.mRemoteContext : this.mContext;
        return new FrameLayout.LayoutParams(context, attrs);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void resetAppWidget(AppWidgetProviderInfo info) {
        this.mInfo = info;
        this.mViewMode = 0;
        updateAppWidget(null);
    }

    public void updateAppWidget(RemoteViews remoteViews) {
        boolean recycled = false;
        View content = null;
        Exception exception = null;
        if (remoteViews == null) {
            if (this.mViewMode == 3) {
                return;
            }
            content = getDefaultView();
            this.mLayoutId = -1;
            this.mViewMode = 3;
        } else {
            this.mRemoteContext = getRemoteContext(remoteViews);
            int layoutId = remoteViews.getLayoutId();
            if (0 == 0 && layoutId == this.mLayoutId) {
                try {
                    remoteViews.reapply(this.mContext, this.mView, this.mOnClickHandler);
                    content = this.mView;
                    recycled = true;
                } catch (RuntimeException e) {
                    exception = e;
                }
            }
            if (content == null) {
                try {
                    content = remoteViews.apply(this.mContext, this, this.mOnClickHandler);
                } catch (RuntimeException e2) {
                    exception = e2;
                }
            }
            this.mLayoutId = layoutId;
            this.mViewMode = 1;
        }
        if (content == null) {
            if (this.mViewMode == 2) {
                return;
            }
            Log.w(TAG, "updateAppWidget couldn't find any view, using error view", exception);
            content = getErrorView();
            this.mViewMode = 2;
        }
        if (!recycled) {
            prepareView(content);
            addView(content);
        }
        if (this.mView != content) {
            removeView(this.mView);
            this.mView = content;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void viewDataChanged(int viewId) {
        View v = findViewById(viewId);
        if (v != null && (v instanceof AdapterView)) {
            AdapterView<?> adapterView = (AdapterView) v;
            Adapter adapter = adapterView.getAdapter();
            if (adapter instanceof BaseAdapter) {
                BaseAdapter baseAdapter = (BaseAdapter) adapter;
                baseAdapter.notifyDataSetChanged();
            } else if (adapter == null && (adapterView instanceof RemoteViewsAdapter.RemoteAdapterConnectionCallback)) {
                ((RemoteViewsAdapter.RemoteAdapterConnectionCallback) adapterView).deferNotifyDataSetChanged();
            }
        }
    }

    private Context getRemoteContext(RemoteViews views) {
        String packageName = views.getPackage();
        if (packageName == null) {
            return this.mContext;
        }
        try {
            return this.mContext.createPackageContextAsUser(packageName, 4, this.mUser);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Package name " + packageName + " not found");
            return this.mContext;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        return super.drawChild(canvas, child, drawingTime);
    }

    protected void prepareView(View view) {
        FrameLayout.LayoutParams requested = (FrameLayout.LayoutParams) view.getLayoutParams();
        if (requested == null) {
            requested = new FrameLayout.LayoutParams(-1, -1);
        }
        requested.gravity = 17;
        view.setLayoutParams(requested);
    }

    protected View getDefaultView() {
        View defaultView = null;
        Exception exception = null;
        try {
            if (this.mInfo != null) {
                Context theirContext = this.mContext.createPackageContextAsUser(this.mInfo.provider.getPackageName(), 4, this.mUser);
                this.mRemoteContext = theirContext;
                LayoutInflater inflater = ((LayoutInflater) theirContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).cloneInContext(theirContext);
                inflater.setFilter(sInflaterFilter);
                AppWidgetManager manager = AppWidgetManager.getInstance(this.mContext);
                Bundle options = manager.getAppWidgetOptions(this.mAppWidgetId);
                int layoutId = this.mInfo.initialLayout;
                if (options.containsKey(AppWidgetManager.OPTION_APPWIDGET_HOST_CATEGORY)) {
                    int category = options.getInt(AppWidgetManager.OPTION_APPWIDGET_HOST_CATEGORY);
                    if (category == 2) {
                        int kgLayoutId = this.mInfo.initialKeyguardLayout;
                        layoutId = kgLayoutId == 0 ? layoutId : kgLayoutId;
                    }
                }
                defaultView = inflater.inflate(layoutId, (ViewGroup) this, false);
            } else {
                Log.w(TAG, "can't inflate defaultView because mInfo is missing");
            }
        } catch (PackageManager.NameNotFoundException e) {
            exception = e;
        } catch (RuntimeException e2) {
            exception = e2;
        }
        if (exception != null) {
            Log.w(TAG, "Error inflating AppWidget " + this.mInfo + ": " + exception.toString());
        }
        if (defaultView == null) {
            defaultView = getErrorView();
        }
        return defaultView;
    }

    protected View getErrorView() {
        TextView tv = new TextView(this.mContext);
        tv.setText(R.string.gadget_host_error_inflating);
        tv.setBackgroundColor(Color.argb(127, 0, 0, 0));
        return tv;
    }

    @Override // android.widget.FrameLayout, android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(AppWidgetHostView.class.getName());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: AppWidgetHostView$ParcelableSparseArray.class */
    public static class ParcelableSparseArray extends SparseArray<Parcelable> implements Parcelable {
        public static final Parcelable.Creator<ParcelableSparseArray> CREATOR = new Parcelable.Creator<ParcelableSparseArray>() { // from class: android.appwidget.AppWidgetHostView.ParcelableSparseArray.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public ParcelableSparseArray createFromParcel(Parcel source) {
                ParcelableSparseArray array = new ParcelableSparseArray();
                ClassLoader loader = array.getClass().getClassLoader();
                int count = source.readInt();
                for (int i = 0; i < count; i++) {
                    array.put(source.readInt(), source.readParcelable(loader));
                }
                return array;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public ParcelableSparseArray[] newArray(int size) {
                return new ParcelableSparseArray[size];
            }
        };

        private ParcelableSparseArray() {
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            int count = size();
            dest.writeInt(count);
            for (int i = 0; i < count; i++) {
                dest.writeInt(keyAt(i));
                dest.writeParcelable(valueAt(i), 0);
            }
        }
    }
}