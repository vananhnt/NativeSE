package android.widget;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.ActionProvider;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ActivityChooserModel;
import com.android.internal.R;

/* loaded from: ShareActionProvider.class */
public class ShareActionProvider extends ActionProvider {
    private static final int DEFAULT_INITIAL_ACTIVITY_COUNT = 4;
    private int mMaxShownActivityCount;
    private final ShareMenuItemOnMenuItemClickListener mOnMenuItemClickListener;
    public static final String DEFAULT_SHARE_HISTORY_FILE_NAME = "share_history.xml";
    private final Context mContext;
    private String mShareHistoryFileName;
    private OnShareTargetSelectedListener mOnShareTargetSelectedListener;
    private ActivityChooserModel.OnChooseActivityListener mOnChooseActivityListener;

    /* loaded from: ShareActionProvider$OnShareTargetSelectedListener.class */
    public interface OnShareTargetSelectedListener {
        boolean onShareTargetSelected(ShareActionProvider shareActionProvider, Intent intent);
    }

    public ShareActionProvider(Context context) {
        super(context);
        this.mMaxShownActivityCount = 4;
        this.mOnMenuItemClickListener = new ShareMenuItemOnMenuItemClickListener();
        this.mShareHistoryFileName = "share_history.xml";
        this.mContext = context;
    }

    public void setOnShareTargetSelectedListener(OnShareTargetSelectedListener listener) {
        this.mOnShareTargetSelectedListener = listener;
        setActivityChooserPolicyIfNeeded();
    }

    @Override // android.view.ActionProvider
    public View onCreateActionView() {
        ActivityChooserModel dataModel = ActivityChooserModel.get(this.mContext, this.mShareHistoryFileName);
        ActivityChooserView activityChooserView = new ActivityChooserView(this.mContext);
        activityChooserView.setActivityChooserModel(dataModel);
        TypedValue outTypedValue = new TypedValue();
        this.mContext.getTheme().resolveAttribute(R.attr.actionModeShareDrawable, outTypedValue, true);
        Drawable drawable = this.mContext.getResources().getDrawable(outTypedValue.resourceId);
        activityChooserView.setExpandActivityOverflowButtonDrawable(drawable);
        activityChooserView.setProvider(this);
        activityChooserView.setDefaultActionButtonContentDescription(R.string.shareactionprovider_share_with_application);
        activityChooserView.setExpandActivityOverflowButtonContentDescription(R.string.shareactionprovider_share_with);
        return activityChooserView;
    }

    @Override // android.view.ActionProvider
    public boolean hasSubMenu() {
        return true;
    }

    @Override // android.view.ActionProvider
    public void onPrepareSubMenu(SubMenu subMenu) {
        subMenu.clear();
        ActivityChooserModel dataModel = ActivityChooserModel.get(this.mContext, this.mShareHistoryFileName);
        PackageManager packageManager = this.mContext.getPackageManager();
        int expandedActivityCount = dataModel.getActivityCount();
        int collapsedActivityCount = Math.min(expandedActivityCount, this.mMaxShownActivityCount);
        for (int i = 0; i < collapsedActivityCount; i++) {
            ResolveInfo activity = dataModel.getActivity(i);
            subMenu.add(0, i, i, activity.loadLabel(packageManager)).setIcon(activity.loadIcon(packageManager)).setOnMenuItemClickListener(this.mOnMenuItemClickListener);
        }
        if (collapsedActivityCount < expandedActivityCount) {
            SubMenu expandedSubMenu = subMenu.addSubMenu(0, collapsedActivityCount, collapsedActivityCount, this.mContext.getString(R.string.activity_chooser_view_see_all));
            for (int i2 = 0; i2 < expandedActivityCount; i2++) {
                ResolveInfo activity2 = dataModel.getActivity(i2);
                expandedSubMenu.add(0, i2, i2, activity2.loadLabel(packageManager)).setIcon(activity2.loadIcon(packageManager)).setOnMenuItemClickListener(this.mOnMenuItemClickListener);
            }
        }
    }

    public void setShareHistoryFileName(String shareHistoryFile) {
        this.mShareHistoryFileName = shareHistoryFile;
        setActivityChooserPolicyIfNeeded();
    }

    public void setShareIntent(Intent shareIntent) {
        ActivityChooserModel dataModel = ActivityChooserModel.get(this.mContext, this.mShareHistoryFileName);
        dataModel.setIntent(shareIntent);
    }

    /* loaded from: ShareActionProvider$ShareMenuItemOnMenuItemClickListener.class */
    private class ShareMenuItemOnMenuItemClickListener implements MenuItem.OnMenuItemClickListener {
        private ShareMenuItemOnMenuItemClickListener() {
        }

        @Override // android.view.MenuItem.OnMenuItemClickListener
        public boolean onMenuItemClick(MenuItem item) {
            ActivityChooserModel dataModel = ActivityChooserModel.get(ShareActionProvider.this.mContext, ShareActionProvider.this.mShareHistoryFileName);
            int itemId = item.getItemId();
            Intent launchIntent = dataModel.chooseActivity(itemId);
            if (launchIntent != null) {
                launchIntent.addFlags(524288);
                ShareActionProvider.this.mContext.startActivity(launchIntent);
                return true;
            }
            return true;
        }
    }

    private void setActivityChooserPolicyIfNeeded() {
        if (this.mOnShareTargetSelectedListener == null) {
            return;
        }
        if (this.mOnChooseActivityListener == null) {
            this.mOnChooseActivityListener = new ShareAcitivityChooserModelPolicy();
        }
        ActivityChooserModel dataModel = ActivityChooserModel.get(this.mContext, this.mShareHistoryFileName);
        dataModel.setOnChooseActivityListener(this.mOnChooseActivityListener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ShareActionProvider$ShareAcitivityChooserModelPolicy.class */
    public class ShareAcitivityChooserModelPolicy implements ActivityChooserModel.OnChooseActivityListener {
        private ShareAcitivityChooserModelPolicy() {
        }

        @Override // android.widget.ActivityChooserModel.OnChooseActivityListener
        public boolean onChooseActivity(ActivityChooserModel host, Intent intent) {
            if (ShareActionProvider.this.mOnShareTargetSelectedListener != null) {
                ShareActionProvider.this.mOnShareTargetSelectedListener.onShareTargetSelected(ShareActionProvider.this, intent);
                return false;
            }
            return false;
        }
    }
}