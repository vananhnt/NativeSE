package android.widget;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ActionProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ActivityChooserModel;
import android.widget.AdapterView;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;
import com.android.internal.R;

/* loaded from: ActivityChooserView.class */
public class ActivityChooserView extends ViewGroup implements ActivityChooserModel.ActivityChooserModelClient {
    private final ActivityChooserViewAdapter mAdapter;
    private final Callbacks mCallbacks;
    private final LinearLayout mActivityChooserContent;
    private final Drawable mActivityChooserContentBackground;
    private final FrameLayout mExpandActivityOverflowButton;
    private final ImageView mExpandActivityOverflowButtonImage;
    private final FrameLayout mDefaultActivityButton;
    private final ImageView mDefaultActivityButtonImage;
    private final int mListPopupMaxWidth;
    ActionProvider mProvider;
    private final DataSetObserver mModelDataSetOberver;
    private final ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener;
    private ListPopupWindow mListPopupWindow;
    private PopupWindow.OnDismissListener mOnDismissListener;
    private boolean mIsSelectingDefaultActivity;
    private int mInitialActivityCount;
    private boolean mIsAttachedToWindow;
    private int mDefaultActionButtonContentDescription;

    public ActivityChooserView(Context context) {
        this(context, null);
    }

    public ActivityChooserView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ActivityChooserView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mModelDataSetOberver = new DataSetObserver() { // from class: android.widget.ActivityChooserView.1
            @Override // android.database.DataSetObserver
            public void onChanged() {
                super.onChanged();
                ActivityChooserView.this.mAdapter.notifyDataSetChanged();
            }

            @Override // android.database.DataSetObserver
            public void onInvalidated() {
                super.onInvalidated();
                ActivityChooserView.this.mAdapter.notifyDataSetInvalidated();
            }
        };
        this.mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() { // from class: android.widget.ActivityChooserView.2
            @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
            public void onGlobalLayout() {
                if (ActivityChooserView.this.isShowingPopup()) {
                    if (!ActivityChooserView.this.isShown()) {
                        ActivityChooserView.this.getListPopupWindow().dismiss();
                        return;
                    }
                    ActivityChooserView.this.getListPopupWindow().show();
                    if (ActivityChooserView.this.mProvider != null) {
                        ActivityChooserView.this.mProvider.subUiVisibilityChanged(true);
                    }
                }
            }
        };
        this.mInitialActivityCount = 4;
        TypedArray attributesArray = context.obtainStyledAttributes(attrs, R.styleable.ActivityChooserView, defStyle, 0);
        this.mInitialActivityCount = attributesArray.getInt(0, 4);
        Drawable expandActivityOverflowButtonDrawable = attributesArray.getDrawable(1);
        attributesArray.recycle();
        LayoutInflater inflater = LayoutInflater.from(this.mContext);
        inflater.inflate(R.layout.activity_chooser_view, (ViewGroup) this, true);
        this.mCallbacks = new Callbacks();
        this.mActivityChooserContent = (LinearLayout) findViewById(R.id.activity_chooser_view_content);
        this.mActivityChooserContentBackground = this.mActivityChooserContent.getBackground();
        this.mDefaultActivityButton = (FrameLayout) findViewById(R.id.default_activity_button);
        this.mDefaultActivityButton.setOnClickListener(this.mCallbacks);
        this.mDefaultActivityButton.setOnLongClickListener(this.mCallbacks);
        this.mDefaultActivityButtonImage = (ImageView) this.mDefaultActivityButton.findViewById(R.id.image);
        FrameLayout expandButton = (FrameLayout) findViewById(R.id.expand_activities_button);
        expandButton.setOnClickListener(this.mCallbacks);
        expandButton.setAccessibilityDelegate(new View.AccessibilityDelegate() { // from class: android.widget.ActivityChooserView.3
            @Override // android.view.View.AccessibilityDelegate
            public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
                super.onInitializeAccessibilityNodeInfo(host, info);
                info.setCanOpenPopup(true);
            }
        });
        expandButton.setOnTouchListener(new ListPopupWindow.ForwardingListener(expandButton) { // from class: android.widget.ActivityChooserView.4
            @Override // android.widget.ListPopupWindow.ForwardingListener
            public ListPopupWindow getPopup() {
                return ActivityChooserView.this.getListPopupWindow();
            }

            @Override // android.widget.ListPopupWindow.ForwardingListener
            protected boolean onForwardingStarted() {
                ActivityChooserView.this.showPopup();
                return true;
            }

            @Override // android.widget.ListPopupWindow.ForwardingListener
            protected boolean onForwardingStopped() {
                ActivityChooserView.this.dismissPopup();
                return true;
            }
        });
        this.mExpandActivityOverflowButton = expandButton;
        this.mExpandActivityOverflowButtonImage = (ImageView) expandButton.findViewById(R.id.image);
        this.mExpandActivityOverflowButtonImage.setImageDrawable(expandActivityOverflowButtonDrawable);
        this.mAdapter = new ActivityChooserViewAdapter();
        this.mAdapter.registerDataSetObserver(new DataSetObserver() { // from class: android.widget.ActivityChooserView.5
            @Override // android.database.DataSetObserver
            public void onChanged() {
                super.onChanged();
                ActivityChooserView.this.updateAppearance();
            }
        });
        Resources resources = context.getResources();
        this.mListPopupMaxWidth = Math.max(resources.getDisplayMetrics().widthPixels / 2, resources.getDimensionPixelSize(R.dimen.config_prefDialogWidth));
    }

    @Override // android.widget.ActivityChooserModel.ActivityChooserModelClient
    public void setActivityChooserModel(ActivityChooserModel dataModel) {
        this.mAdapter.setDataModel(dataModel);
        if (isShowingPopup()) {
            dismissPopup();
            showPopup();
        }
    }

    public void setExpandActivityOverflowButtonDrawable(Drawable drawable) {
        this.mExpandActivityOverflowButtonImage.setImageDrawable(drawable);
    }

    public void setExpandActivityOverflowButtonContentDescription(int resourceId) {
        CharSequence contentDescription = this.mContext.getString(resourceId);
        this.mExpandActivityOverflowButtonImage.setContentDescription(contentDescription);
    }

    public void setProvider(ActionProvider provider) {
        this.mProvider = provider;
    }

    public boolean showPopup() {
        if (isShowingPopup() || !this.mIsAttachedToWindow) {
            return false;
        }
        this.mIsSelectingDefaultActivity = false;
        showPopupUnchecked(this.mInitialActivityCount);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showPopupUnchecked(int maxActivityCount) {
        if (this.mAdapter.getDataModel() == null) {
            throw new IllegalStateException("No data model. Did you call #setDataModel?");
        }
        getViewTreeObserver().addOnGlobalLayoutListener(this.mOnGlobalLayoutListener);
        boolean defaultActivityButtonShown = this.mDefaultActivityButton.getVisibility() == 0;
        int activityCount = this.mAdapter.getActivityCount();
        int maxActivityCountOffset = defaultActivityButtonShown ? 1 : 0;
        if (maxActivityCount != Integer.MAX_VALUE && activityCount > maxActivityCount + maxActivityCountOffset) {
            this.mAdapter.setShowFooterView(true);
            this.mAdapter.setMaxActivityCount(maxActivityCount - 1);
        } else {
            this.mAdapter.setShowFooterView(false);
            this.mAdapter.setMaxActivityCount(maxActivityCount);
        }
        ListPopupWindow popupWindow = getListPopupWindow();
        if (!popupWindow.isShowing()) {
            if (this.mIsSelectingDefaultActivity || !defaultActivityButtonShown) {
                this.mAdapter.setShowDefaultActivity(true, defaultActivityButtonShown);
            } else {
                this.mAdapter.setShowDefaultActivity(false, false);
            }
            int contentWidth = Math.min(this.mAdapter.measureContentWidth(), this.mListPopupMaxWidth);
            popupWindow.setContentWidth(contentWidth);
            popupWindow.show();
            if (this.mProvider != null) {
                this.mProvider.subUiVisibilityChanged(true);
            }
            popupWindow.getListView().setContentDescription(this.mContext.getString(R.string.activitychooserview_choose_application));
        }
    }

    public boolean dismissPopup() {
        if (isShowingPopup()) {
            getListPopupWindow().dismiss();
            ViewTreeObserver viewTreeObserver = getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.removeOnGlobalLayoutListener(this.mOnGlobalLayoutListener);
                return true;
            }
            return true;
        }
        return true;
    }

    public boolean isShowingPopup() {
        return getListPopupWindow().isShowing();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        ActivityChooserModel dataModel = this.mAdapter.getDataModel();
        if (dataModel != null) {
            dataModel.registerObserver(this.mModelDataSetOberver);
        }
        this.mIsAttachedToWindow = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ActivityChooserModel dataModel = this.mAdapter.getDataModel();
        if (dataModel != null) {
            dataModel.unregisterObserver(this.mModelDataSetOberver);
        }
        ViewTreeObserver viewTreeObserver = getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.removeOnGlobalLayoutListener(this.mOnGlobalLayoutListener);
        }
        if (isShowingPopup()) {
            dismissPopup();
        }
        this.mIsAttachedToWindow = false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        View child = this.mActivityChooserContent;
        if (this.mDefaultActivityButton.getVisibility() != 0) {
            heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(heightMeasureSpec), 1073741824);
        }
        measureChild(child, widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(child.getMeasuredWidth(), child.getMeasuredHeight());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        this.mActivityChooserContent.layout(0, 0, right - left, bottom - top);
        if (!isShowingPopup()) {
            dismissPopup();
        }
    }

    public ActivityChooserModel getDataModel() {
        return this.mAdapter.getDataModel();
    }

    public void setOnDismissListener(PopupWindow.OnDismissListener listener) {
        this.mOnDismissListener = listener;
    }

    public void setInitialActivityCount(int itemCount) {
        this.mInitialActivityCount = itemCount;
    }

    public void setDefaultActionButtonContentDescription(int resourceId) {
        this.mDefaultActionButtonContentDescription = resourceId;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ListPopupWindow getListPopupWindow() {
        if (this.mListPopupWindow == null) {
            this.mListPopupWindow = new ListPopupWindow(getContext());
            this.mListPopupWindow.setAdapter(this.mAdapter);
            this.mListPopupWindow.setAnchorView(this);
            this.mListPopupWindow.setModal(true);
            this.mListPopupWindow.setOnItemClickListener(this.mCallbacks);
            this.mListPopupWindow.setOnDismissListener(this.mCallbacks);
        }
        return this.mListPopupWindow;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAppearance() {
        if (this.mAdapter.getCount() > 0) {
            this.mExpandActivityOverflowButton.setEnabled(true);
        } else {
            this.mExpandActivityOverflowButton.setEnabled(false);
        }
        int activityCount = this.mAdapter.getActivityCount();
        int historySize = this.mAdapter.getHistorySize();
        if (activityCount == 1 || (activityCount > 1 && historySize > 0)) {
            this.mDefaultActivityButton.setVisibility(0);
            ResolveInfo activity = this.mAdapter.getDefaultActivity();
            PackageManager packageManager = this.mContext.getPackageManager();
            this.mDefaultActivityButtonImage.setImageDrawable(activity.loadIcon(packageManager));
            if (this.mDefaultActionButtonContentDescription != 0) {
                CharSequence label = activity.loadLabel(packageManager);
                String contentDescription = this.mContext.getString(this.mDefaultActionButtonContentDescription, label);
                this.mDefaultActivityButton.setContentDescription(contentDescription);
            }
        } else {
            this.mDefaultActivityButton.setVisibility(8);
        }
        if (this.mDefaultActivityButton.getVisibility() == 0) {
            this.mActivityChooserContent.setBackgroundDrawable(this.mActivityChooserContentBackground);
        } else {
            this.mActivityChooserContent.setBackgroundDrawable(null);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ActivityChooserView$Callbacks.class */
    public class Callbacks implements AdapterView.OnItemClickListener, View.OnClickListener, View.OnLongClickListener, PopupWindow.OnDismissListener {
        private Callbacks() {
        }

        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ActivityChooserViewAdapter adapter = (ActivityChooserViewAdapter) parent.getAdapter();
            int itemViewType = adapter.getItemViewType(position);
            switch (itemViewType) {
                case 0:
                    ActivityChooserView.this.dismissPopup();
                    if (!ActivityChooserView.this.mIsSelectingDefaultActivity) {
                        Intent launchIntent = ActivityChooserView.this.mAdapter.getDataModel().chooseActivity(ActivityChooserView.this.mAdapter.getShowDefaultActivity() ? position : position + 1);
                        if (launchIntent != null) {
                            launchIntent.addFlags(524288);
                            ActivityChooserView.this.mContext.startActivity(launchIntent);
                            return;
                        }
                        return;
                    } else if (position > 0) {
                        ActivityChooserView.this.mAdapter.getDataModel().setDefaultActivity(position);
                        return;
                    } else {
                        return;
                    }
                case 1:
                    ActivityChooserView.this.showPopupUnchecked(Integer.MAX_VALUE);
                    return;
                default:
                    throw new IllegalArgumentException();
            }
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (view != ActivityChooserView.this.mDefaultActivityButton) {
                if (view == ActivityChooserView.this.mExpandActivityOverflowButton) {
                    ActivityChooserView.this.mIsSelectingDefaultActivity = false;
                    ActivityChooserView.this.showPopupUnchecked(ActivityChooserView.this.mInitialActivityCount);
                    return;
                }
                throw new IllegalArgumentException();
            }
            ActivityChooserView.this.dismissPopup();
            ResolveInfo defaultActivity = ActivityChooserView.this.mAdapter.getDefaultActivity();
            int index = ActivityChooserView.this.mAdapter.getDataModel().getActivityIndex(defaultActivity);
            Intent launchIntent = ActivityChooserView.this.mAdapter.getDataModel().chooseActivity(index);
            if (launchIntent != null) {
                launchIntent.addFlags(524288);
                ActivityChooserView.this.mContext.startActivity(launchIntent);
            }
        }

        @Override // android.view.View.OnLongClickListener
        public boolean onLongClick(View view) {
            if (view == ActivityChooserView.this.mDefaultActivityButton) {
                if (ActivityChooserView.this.mAdapter.getCount() > 0) {
                    ActivityChooserView.this.mIsSelectingDefaultActivity = true;
                    ActivityChooserView.this.showPopupUnchecked(ActivityChooserView.this.mInitialActivityCount);
                    return true;
                }
                return true;
            }
            throw new IllegalArgumentException();
        }

        @Override // android.widget.PopupWindow.OnDismissListener
        public void onDismiss() {
            notifyOnDismissListener();
            if (ActivityChooserView.this.mProvider != null) {
                ActivityChooserView.this.mProvider.subUiVisibilityChanged(false);
            }
        }

        private void notifyOnDismissListener() {
            if (ActivityChooserView.this.mOnDismissListener != null) {
                ActivityChooserView.this.mOnDismissListener.onDismiss();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ActivityChooserView$ActivityChooserViewAdapter.class */
    public class ActivityChooserViewAdapter extends BaseAdapter {
        public static final int MAX_ACTIVITY_COUNT_UNLIMITED = Integer.MAX_VALUE;
        public static final int MAX_ACTIVITY_COUNT_DEFAULT = 4;
        private static final int ITEM_VIEW_TYPE_ACTIVITY = 0;
        private static final int ITEM_VIEW_TYPE_FOOTER = 1;
        private static final int ITEM_VIEW_TYPE_COUNT = 3;
        private ActivityChooserModel mDataModel;
        private int mMaxActivityCount;
        private boolean mShowDefaultActivity;
        private boolean mHighlightDefaultActivity;
        private boolean mShowFooterView;

        private ActivityChooserViewAdapter() {
            this.mMaxActivityCount = 4;
        }

        public void setDataModel(ActivityChooserModel dataModel) {
            ActivityChooserModel oldDataModel = ActivityChooserView.this.mAdapter.getDataModel();
            if (oldDataModel != null && ActivityChooserView.this.isShown()) {
                oldDataModel.unregisterObserver(ActivityChooserView.this.mModelDataSetOberver);
            }
            this.mDataModel = dataModel;
            if (dataModel != null && ActivityChooserView.this.isShown()) {
                dataModel.registerObserver(ActivityChooserView.this.mModelDataSetOberver);
            }
            notifyDataSetChanged();
        }

        @Override // android.widget.BaseAdapter, android.widget.Adapter
        public int getItemViewType(int position) {
            if (this.mShowFooterView && position == getCount() - 1) {
                return 1;
            }
            return 0;
        }

        @Override // android.widget.BaseAdapter, android.widget.Adapter
        public int getViewTypeCount() {
            return 3;
        }

        @Override // android.widget.Adapter
        public int getCount() {
            int activityCount = this.mDataModel.getActivityCount();
            if (!this.mShowDefaultActivity && this.mDataModel.getDefaultActivity() != null) {
                activityCount--;
            }
            int count = Math.min(activityCount, this.mMaxActivityCount);
            if (this.mShowFooterView) {
                count++;
            }
            return count;
        }

        @Override // android.widget.Adapter
        public Object getItem(int position) {
            int itemViewType = getItemViewType(position);
            switch (itemViewType) {
                case 0:
                    if (!this.mShowDefaultActivity && this.mDataModel.getDefaultActivity() != null) {
                        position++;
                    }
                    return this.mDataModel.getActivity(position);
                case 1:
                    return null;
                default:
                    throw new IllegalArgumentException();
            }
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }

        @Override // android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            int itemViewType = getItemViewType(position);
            switch (itemViewType) {
                case 0:
                    if (convertView == null || convertView.getId() != 16908907) {
                        convertView = LayoutInflater.from(ActivityChooserView.this.getContext()).inflate(R.layout.activity_chooser_view_list_item, parent, false);
                    }
                    PackageManager packageManager = ActivityChooserView.this.mContext.getPackageManager();
                    ImageView iconView = (ImageView) convertView.findViewById(16908294);
                    ResolveInfo activity = (ResolveInfo) getItem(position);
                    iconView.setImageDrawable(activity.loadIcon(packageManager));
                    TextView titleView = (TextView) convertView.findViewById(16908310);
                    titleView.setText(activity.loadLabel(packageManager));
                    if (this.mShowDefaultActivity && position == 0 && this.mHighlightDefaultActivity) {
                        convertView.setActivated(true);
                    } else {
                        convertView.setActivated(false);
                    }
                    return convertView;
                case 1:
                    if (convertView == null || convertView.getId() != 1) {
                        convertView = LayoutInflater.from(ActivityChooserView.this.getContext()).inflate(R.layout.activity_chooser_view_list_item, parent, false);
                        convertView.setId(1);
                        TextView titleView2 = (TextView) convertView.findViewById(16908310);
                        titleView2.setText(ActivityChooserView.this.mContext.getString(R.string.activity_chooser_view_see_all));
                    }
                    return convertView;
                default:
                    throw new IllegalArgumentException();
            }
        }

        public int measureContentWidth() {
            int oldMaxActivityCount = this.mMaxActivityCount;
            this.mMaxActivityCount = Integer.MAX_VALUE;
            int contentWidth = 0;
            View itemView = null;
            int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
            int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
            int count = getCount();
            for (int i = 0; i < count; i++) {
                itemView = getView(i, itemView, null);
                itemView.measure(widthMeasureSpec, heightMeasureSpec);
                contentWidth = Math.max(contentWidth, itemView.getMeasuredWidth());
            }
            this.mMaxActivityCount = oldMaxActivityCount;
            return contentWidth;
        }

        public void setMaxActivityCount(int maxActivityCount) {
            if (this.mMaxActivityCount != maxActivityCount) {
                this.mMaxActivityCount = maxActivityCount;
                notifyDataSetChanged();
            }
        }

        public ResolveInfo getDefaultActivity() {
            return this.mDataModel.getDefaultActivity();
        }

        public void setShowFooterView(boolean showFooterView) {
            if (this.mShowFooterView != showFooterView) {
                this.mShowFooterView = showFooterView;
                notifyDataSetChanged();
            }
        }

        public int getActivityCount() {
            return this.mDataModel.getActivityCount();
        }

        public int getHistorySize() {
            return this.mDataModel.getHistorySize();
        }

        public int getMaxActivityCount() {
            return this.mMaxActivityCount;
        }

        public ActivityChooserModel getDataModel() {
            return this.mDataModel;
        }

        public void setShowDefaultActivity(boolean showDefaultActivity, boolean highlightDefaultActivity) {
            if (this.mShowDefaultActivity != showDefaultActivity || this.mHighlightDefaultActivity != highlightDefaultActivity) {
                this.mShowDefaultActivity = showDefaultActivity;
                this.mHighlightDefaultActivity = highlightDefaultActivity;
                notifyDataSetChanged();
            }
        }

        public boolean getShowDefaultActivity() {
            return this.mShowDefaultActivity;
        }
    }
}