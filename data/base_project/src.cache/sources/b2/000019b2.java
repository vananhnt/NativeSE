package com.android.internal.app;

import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.app.AppGlobals;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.Settings;
import android.provider.Telephony;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.android.internal.R;
import com.android.internal.app.AlertController;
import com.android.internal.content.PackageMonitor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/* loaded from: ResolverActivity.class */
public class ResolverActivity extends AlertActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = "ResolverActivity";
    private static final boolean DEBUG = false;
    private int mLaunchedFromUid;
    private ResolveListAdapter mAdapter;
    private PackageManager mPm;
    private boolean mAlwaysUseOption;
    private boolean mShowExtended;
    private ListView mListView;
    private Button mAlwaysButton;
    private Button mOnceButton;
    private int mIconDpi;
    private int mIconSize;
    private int mMaxColumns;
    private boolean mRegistered;
    private int mLastSelected = -1;
    private final PackageMonitor mPackageMonitor = new PackageMonitor() { // from class: com.android.internal.app.ResolverActivity.1
        @Override // com.android.internal.content.PackageMonitor
        public void onSomePackagesChanged() {
            ResolverActivity.this.mAdapter.handlePackagesChanged();
        }
    };

    private Intent makeMyIntent() {
        Intent intent = new Intent(getIntent());
        intent.setComponent(null);
        intent.setFlags(intent.getFlags() & (-8388609));
        return intent;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.app.AlertActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        int titleResource;
        Intent intent = makeMyIntent();
        Set<String> categories = intent.getCategories();
        if (Intent.ACTION_MAIN.equals(intent.getAction()) && categories != null && categories.size() == 1 && categories.contains(Intent.CATEGORY_HOME)) {
            titleResource = 17040371;
        } else {
            titleResource = 17040370;
        }
        onCreate(savedInstanceState, intent, getResources().getText(titleResource), null, null, true);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState, Intent intent, CharSequence title, Intent[] initialIntents, List<ResolveInfo> rList, boolean alwaysUseOption) {
        setTheme(R.style.Theme_DeviceDefault_Light_Dialog_Alert);
        super.onCreate(savedInstanceState);
        try {
            this.mLaunchedFromUid = ActivityManagerNative.getDefault().getLaunchedFromUid(getActivityToken());
        } catch (RemoteException e) {
            this.mLaunchedFromUid = -1;
        }
        this.mPm = getPackageManager();
        this.mAlwaysUseOption = alwaysUseOption;
        this.mMaxColumns = getResources().getInteger(R.integer.config_maxResolverActivityColumns);
        AlertController.AlertParams ap = this.mAlertParams;
        ap.mTitle = title;
        this.mPackageMonitor.register(this, getMainLooper(), false);
        this.mRegistered = true;
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        this.mIconDpi = am.getLauncherLargeIconDensity();
        this.mIconSize = am.getLauncherLargeIconSize();
        this.mAdapter = new ResolveListAdapter(this, intent, initialIntents, rList, this.mLaunchedFromUid);
        int count = this.mAdapter.getCount();
        if (this.mLaunchedFromUid < 0 || UserHandle.isIsolated(this.mLaunchedFromUid)) {
            finish();
            return;
        }
        if (count > 1) {
            ap.mView = getLayoutInflater().inflate(R.layout.resolver_list, (ViewGroup) null);
            this.mListView = (ListView) ap.mView.findViewById(R.id.resolver_list);
            this.mListView.setAdapter((ListAdapter) this.mAdapter);
            this.mListView.setOnItemClickListener(this);
            this.mListView.setOnItemLongClickListener(new ItemLongClickListener());
            if (alwaysUseOption) {
                this.mListView.setChoiceMode(1);
            }
        } else if (count == 1) {
            startActivity(this.mAdapter.intentForPosition(0));
            this.mPackageMonitor.unregister();
            this.mRegistered = false;
            finish();
            return;
        } else {
            ap.mMessage = getResources().getText(R.string.noApplications);
        }
        setupAlert();
        if (alwaysUseOption) {
            ViewGroup buttonLayout = (ViewGroup) findViewById(R.id.button_bar);
            if (buttonLayout != null) {
                buttonLayout.setVisibility(0);
                this.mAlwaysButton = (Button) buttonLayout.findViewById(R.id.button_always);
                this.mOnceButton = (Button) buttonLayout.findViewById(R.id.button_once);
            } else {
                this.mAlwaysUseOption = false;
            }
        }
        int initialHighlight = this.mAdapter.getInitialHighlight();
        if (initialHighlight >= 0) {
            this.mListView.setItemChecked(initialHighlight, true);
            onItemClick(null, null, initialHighlight, 0L);
        }
    }

    Drawable getIcon(Resources res, int resId) {
        Drawable result;
        try {
            result = res.getDrawableForDensity(resId, this.mIconDpi);
        } catch (Resources.NotFoundException e) {
            result = null;
        }
        return result;
    }

    Drawable loadIconForResolveInfo(ResolveInfo ri) {
        Drawable dr;
        try {
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Couldn't find resources for package", e);
        }
        if (ri.resolvePackageName != null && ri.icon != 0 && (dr = getIcon(this.mPm.getResourcesForApplication(ri.resolvePackageName), ri.icon)) != null) {
            return dr;
        }
        int iconRes = ri.getIconResource();
        if (iconRes != 0) {
            Drawable dr2 = getIcon(this.mPm.getResourcesForApplication(ri.activityInfo.packageName), iconRes);
            if (dr2 != null) {
                return dr2;
            }
        }
        return ri.loadIcon(this.mPm);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onRestart() {
        super.onRestart();
        if (!this.mRegistered) {
            this.mPackageMonitor.register(this, getMainLooper(), false);
            this.mRegistered = true;
        }
        this.mAdapter.handlePackagesChanged();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onStop() {
        super.onStop();
        if (this.mRegistered) {
            this.mPackageMonitor.unregister();
            this.mRegistered = false;
        }
        if ((getIntent().getFlags() & 268435456) != 0 && !isChangingConfigurations()) {
            finish();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (this.mAlwaysUseOption) {
            int checkedPos = this.mListView.getCheckedItemPosition();
            boolean enabled = checkedPos != -1;
            this.mLastSelected = checkedPos;
            this.mAlwaysButton.setEnabled(enabled);
            this.mOnceButton.setEnabled(enabled);
            if (enabled) {
                this.mListView.setSelection(checkedPos);
            }
        }
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int checkedPos = this.mListView.getCheckedItemPosition();
        boolean hasValidSelection = checkedPos != -1;
        if (this.mAlwaysUseOption && (!hasValidSelection || this.mLastSelected != checkedPos)) {
            this.mAlwaysButton.setEnabled(hasValidSelection);
            this.mOnceButton.setEnabled(hasValidSelection);
            if (hasValidSelection) {
                this.mListView.smoothScrollToPosition(checkedPos);
            }
            this.mLastSelected = checkedPos;
            return;
        }
        startSelected(position, false);
    }

    public void onButtonClick(View v) {
        int id = v.getId();
        startSelected(this.mListView.getCheckedItemPosition(), id == 16909058);
        dismiss();
    }

    void startSelected(int which, boolean always) {
        if (isFinishing()) {
            return;
        }
        ResolveInfo ri = this.mAdapter.resolveInfoForPosition(which);
        Intent intent = this.mAdapter.intentForPosition(which);
        onIntentSelected(ri, intent, always);
        finish();
    }

    protected void onIntentSelected(ResolveInfo ri, Intent intent, boolean alwaysCheck) {
        String mimeType;
        if (this.mAlwaysUseOption && this.mAdapter.mOrigResolveList != null) {
            IntentFilter filter = new IntentFilter();
            if (intent.getAction() != null) {
                filter.addAction(intent.getAction());
            }
            Set<String> categories = intent.getCategories();
            if (categories != null) {
                for (String cat : categories) {
                    filter.addCategory(cat);
                }
            }
            filter.addCategory(Intent.CATEGORY_DEFAULT);
            int cat2 = ri.match & IntentFilter.MATCH_CATEGORY_MASK;
            Uri data = intent.getData();
            if (cat2 == 6291456 && (mimeType = intent.resolveType(this)) != null) {
                try {
                    filter.addDataType(mimeType);
                } catch (IntentFilter.MalformedMimeTypeException e) {
                    Log.w(TAG, e);
                    filter = null;
                }
            }
            if (data != null && data.getScheme() != null && (cat2 != 6291456 || (!ContentResolver.SCHEME_FILE.equals(data.getScheme()) && !"content".equals(data.getScheme())))) {
                filter.addDataScheme(data.getScheme());
                Iterator<PatternMatcher> pIt = ri.filter.schemeSpecificPartsIterator();
                if (pIt != null) {
                    String ssp = data.getSchemeSpecificPart();
                    while (true) {
                        if (ssp == null || !pIt.hasNext()) {
                            break;
                        }
                        PatternMatcher p = pIt.next();
                        if (p.match(ssp)) {
                            filter.addDataSchemeSpecificPart(p.getPath(), p.getType());
                            break;
                        }
                    }
                }
                Iterator<IntentFilter.AuthorityEntry> aIt = ri.filter.authoritiesIterator();
                if (aIt != null) {
                    while (true) {
                        if (!aIt.hasNext()) {
                            break;
                        }
                        IntentFilter.AuthorityEntry a = aIt.next();
                        if (a.match(data) >= 0) {
                            int port = a.getPort();
                            filter.addDataAuthority(a.getHost(), port >= 0 ? Integer.toString(port) : null);
                        }
                    }
                }
                Iterator<PatternMatcher> pIt2 = ri.filter.pathsIterator();
                if (pIt2 != null) {
                    String path = data.getPath();
                    while (true) {
                        if (path == null || !pIt2.hasNext()) {
                            break;
                        }
                        PatternMatcher p2 = pIt2.next();
                        if (p2.match(path)) {
                            filter.addDataPath(p2.getPath(), p2.getType());
                            break;
                        }
                    }
                }
            }
            if (filter != null) {
                int N = this.mAdapter.mOrigResolveList.size();
                ComponentName[] set = new ComponentName[N];
                int bestMatch = 0;
                for (int i = 0; i < N; i++) {
                    ResolveInfo r = this.mAdapter.mOrigResolveList.get(i);
                    set[i] = new ComponentName(r.activityInfo.packageName, r.activityInfo.name);
                    if (r.match > bestMatch) {
                        bestMatch = r.match;
                    }
                }
                if (alwaysCheck) {
                    getPackageManager().addPreferredActivity(filter, bestMatch, set, intent.getComponent());
                } else {
                    try {
                        AppGlobals.getPackageManager().setLastChosenActivity(intent, intent.resolveTypeIfNeeded(getContentResolver()), 65536, filter, bestMatch, intent.getComponent());
                    } catch (RemoteException re) {
                        Log.d(TAG, "Error calling setLastChosenActivity\n" + re);
                    }
                }
            }
        }
        if (intent != null) {
            startActivity(intent);
        }
    }

    void showAppDetails(ResolveInfo ri) {
        Intent in = new Intent().setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.fromParts(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, ri.activityInfo.packageName, null)).addFlags(524288);
        startActivity(in);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ResolverActivity$DisplayResolveInfo.class */
    public final class DisplayResolveInfo {
        ResolveInfo ri;
        CharSequence displayLabel;
        Drawable displayIcon;
        CharSequence extendedInfo;
        Intent origIntent;

        DisplayResolveInfo(ResolveInfo pri, CharSequence pLabel, CharSequence pInfo, Intent pOrigIntent) {
            this.ri = pri;
            this.displayLabel = pLabel;
            this.extendedInfo = pInfo;
            this.origIntent = pOrigIntent;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ResolverActivity$ResolveListAdapter.class */
    public final class ResolveListAdapter extends BaseAdapter {
        private final Intent[] mInitialIntents;
        private final List<ResolveInfo> mBaseResolveList;
        private ResolveInfo mLastChosen;
        private final Intent mIntent;
        private final int mLaunchedFromUid;
        private final LayoutInflater mInflater;
        List<ResolveInfo> mOrigResolveList;
        private int mInitialHighlight = -1;
        List<DisplayResolveInfo> mList = new ArrayList();

        public ResolveListAdapter(Context context, Intent intent, Intent[] initialIntents, List<ResolveInfo> rList, int launchedFromUid) {
            this.mIntent = new Intent(intent);
            this.mInitialIntents = initialIntents;
            this.mBaseResolveList = rList;
            this.mLaunchedFromUid = launchedFromUid;
            this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rebuildList();
        }

        public void handlePackagesChanged() {
            getCount();
            rebuildList();
            notifyDataSetChanged();
            int newItemCount = getCount();
            if (newItemCount == 0) {
                ResolverActivity.this.finish();
            }
        }

        public int getInitialHighlight() {
            return this.mInitialHighlight;
        }

        private void rebuildList() {
            List<ResolveInfo> currentResolveList;
            try {
                this.mLastChosen = AppGlobals.getPackageManager().getLastChosenActivity(this.mIntent, this.mIntent.resolveTypeIfNeeded(ResolverActivity.this.getContentResolver()), 65536);
            } catch (RemoteException re) {
                Log.d(ResolverActivity.TAG, "Error calling setLastChosenActivity\n" + re);
            }
            this.mList.clear();
            if (this.mBaseResolveList == null) {
                List<ResolveInfo> queryIntentActivities = ResolverActivity.this.mPm.queryIntentActivities(this.mIntent, 65536 | (ResolverActivity.this.mAlwaysUseOption ? 64 : 0));
                this.mOrigResolveList = queryIntentActivities;
                currentResolveList = queryIntentActivities;
                if (currentResolveList != null) {
                    for (int i = currentResolveList.size() - 1; i >= 0; i--) {
                        ActivityInfo ai = currentResolveList.get(i).activityInfo;
                        int granted = ActivityManager.checkComponentPermission(ai.permission, this.mLaunchedFromUid, ai.applicationInfo.uid, ai.exported);
                        if (granted != 0) {
                            if (this.mOrigResolveList == currentResolveList) {
                                this.mOrigResolveList = new ArrayList(this.mOrigResolveList);
                            }
                            currentResolveList.remove(i);
                        }
                    }
                }
            } else {
                currentResolveList = this.mBaseResolveList;
                this.mOrigResolveList = null;
            }
            if (currentResolveList != null) {
                int size = currentResolveList.size();
                int N = size;
                if (size > 0) {
                    ResolveInfo r0 = currentResolveList.get(0);
                    for (int i2 = 1; i2 < N; i2++) {
                        ResolveInfo ri = currentResolveList.get(i2);
                        if (r0.priority != ri.priority || r0.isDefault != ri.isDefault) {
                            while (i2 < N) {
                                if (this.mOrigResolveList == currentResolveList) {
                                    this.mOrigResolveList = new ArrayList(this.mOrigResolveList);
                                }
                                currentResolveList.remove(i2);
                                N--;
                            }
                        }
                    }
                    if (N > 1) {
                        ResolveInfo.DisplayNameComparator rComparator = new ResolveInfo.DisplayNameComparator(ResolverActivity.this.mPm);
                        Collections.sort(currentResolveList, rComparator);
                    }
                    if (this.mInitialIntents != null) {
                        for (int i3 = 0; i3 < this.mInitialIntents.length; i3++) {
                            Intent ii = this.mInitialIntents[i3];
                            if (ii != null) {
                                ActivityInfo ai2 = ii.resolveActivityInfo(ResolverActivity.this.getPackageManager(), 0);
                                if (ai2 == null) {
                                    Log.w(ResolverActivity.TAG, "No activity found for " + ii);
                                } else {
                                    ResolveInfo ri2 = new ResolveInfo();
                                    ri2.activityInfo = ai2;
                                    if (ii instanceof LabeledIntent) {
                                        LabeledIntent li = (LabeledIntent) ii;
                                        ri2.resolvePackageName = li.getSourcePackage();
                                        ri2.labelRes = li.getLabelResource();
                                        ri2.nonLocalizedLabel = li.getNonLocalizedLabel();
                                        ri2.icon = li.getIconResource();
                                    }
                                    this.mList.add(new DisplayResolveInfo(ri2, ri2.loadLabel(ResolverActivity.this.getPackageManager()), null, ii));
                                }
                            }
                        }
                    }
                    ResolveInfo r02 = currentResolveList.get(0);
                    int start = 0;
                    CharSequence r0Label = r02.loadLabel(ResolverActivity.this.mPm);
                    ResolverActivity.this.mShowExtended = false;
                    for (int i4 = 1; i4 < N; i4++) {
                        if (r0Label == null) {
                            r0Label = r02.activityInfo.packageName;
                        }
                        ResolveInfo ri3 = currentResolveList.get(i4);
                        CharSequence riLabel = ri3.loadLabel(ResolverActivity.this.mPm);
                        if (riLabel == null) {
                            riLabel = ri3.activityInfo.packageName;
                        }
                        if (!riLabel.equals(r0Label)) {
                            processGroup(currentResolveList, start, i4 - 1, r02, r0Label);
                            r02 = ri3;
                            r0Label = riLabel;
                            start = i4;
                        }
                    }
                    processGroup(currentResolveList, start, N - 1, r02, r0Label);
                }
            }
        }

        private void processGroup(List<ResolveInfo> rList, int start, int end, ResolveInfo ro, CharSequence roLabel) {
            int num = (end - start) + 1;
            if (num != 1) {
                ResolverActivity.this.mShowExtended = true;
                boolean usePkg = false;
                CharSequence startApp = ro.activityInfo.applicationInfo.loadLabel(ResolverActivity.this.mPm);
                if (startApp == null) {
                    usePkg = true;
                }
                if (!usePkg) {
                    HashSet<CharSequence> duplicates = new HashSet<>();
                    duplicates.add(startApp);
                    for (int j = start + 1; j <= end; j++) {
                        ResolveInfo jRi = rList.get(j);
                        CharSequence jApp = jRi.activityInfo.applicationInfo.loadLabel(ResolverActivity.this.mPm);
                        if (jApp == null || duplicates.contains(jApp)) {
                            usePkg = true;
                            break;
                        }
                        duplicates.add(jApp);
                    }
                    duplicates.clear();
                }
                for (int k = start; k <= end; k++) {
                    ResolveInfo add = rList.get(k);
                    if (this.mLastChosen != null && this.mLastChosen.activityInfo.packageName.equals(add.activityInfo.packageName) && this.mLastChosen.activityInfo.name.equals(add.activityInfo.name)) {
                        this.mInitialHighlight = this.mList.size();
                    }
                    if (!usePkg) {
                        this.mList.add(new DisplayResolveInfo(add, roLabel, add.activityInfo.applicationInfo.loadLabel(ResolverActivity.this.mPm), null));
                    } else {
                        this.mList.add(new DisplayResolveInfo(add, roLabel, add.activityInfo.packageName, null));
                    }
                }
                return;
            }
            if (this.mLastChosen != null && this.mLastChosen.activityInfo.packageName.equals(ro.activityInfo.packageName) && this.mLastChosen.activityInfo.name.equals(ro.activityInfo.name)) {
                this.mInitialHighlight = this.mList.size();
            }
            this.mList.add(new DisplayResolveInfo(ro, roLabel, null, null));
        }

        public ResolveInfo resolveInfoForPosition(int position) {
            return this.mList.get(position).ri;
        }

        public Intent intentForPosition(int position) {
            DisplayResolveInfo dri = this.mList.get(position);
            Intent intent = new Intent(dri.origIntent != null ? dri.origIntent : this.mIntent);
            intent.addFlags(View.SCROLLBARS_OUTSIDE_INSET);
            ActivityInfo ai = dri.ri.activityInfo;
            intent.setComponent(new ComponentName(ai.applicationInfo.packageName, ai.name));
            return intent;
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return this.mList.size();
        }

        @Override // android.widget.Adapter
        public Object getItem(int position) {
            return this.mList.get(position);
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }

        @Override // android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                view = this.mInflater.inflate(R.layout.resolve_list_item, parent, false);
                ViewHolder holder = new ViewHolder(view);
                view.setTag(holder);
                ViewGroup.LayoutParams lp = holder.icon.getLayoutParams();
                int i = ResolverActivity.this.mIconSize;
                lp.height = i;
                lp.width = i;
            } else {
                view = convertView;
            }
            bindView(view, this.mList.get(position));
            return view;
        }

        private final void bindView(View view, DisplayResolveInfo info) {
            ViewHolder holder = (ViewHolder) view.getTag();
            holder.text.setText(info.displayLabel);
            if (ResolverActivity.this.mShowExtended) {
                holder.text2.setVisibility(0);
                holder.text2.setText(info.extendedInfo);
            } else {
                holder.text2.setVisibility(8);
            }
            if (info.displayIcon == null) {
                new LoadIconTask().execute(info);
            }
            holder.icon.setImageDrawable(info.displayIcon);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ResolverActivity$ViewHolder.class */
    public static class ViewHolder {
        public TextView text;
        public TextView text2;
        public ImageView icon;

        public ViewHolder(View view) {
            this.text = (TextView) view.findViewById(16908308);
            this.text2 = (TextView) view.findViewById(16908309);
            this.icon = (ImageView) view.findViewById(16908294);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ResolverActivity$ItemLongClickListener.class */
    public class ItemLongClickListener implements AdapterView.OnItemLongClickListener {
        ItemLongClickListener() {
        }

        @Override // android.widget.AdapterView.OnItemLongClickListener
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            ResolveInfo ri = ResolverActivity.this.mAdapter.resolveInfoForPosition(position);
            ResolverActivity.this.showAppDetails(ri);
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ResolverActivity$LoadIconTask.class */
    public class LoadIconTask extends AsyncTask<DisplayResolveInfo, Void, DisplayResolveInfo> {
        LoadIconTask() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public DisplayResolveInfo doInBackground(DisplayResolveInfo... params) {
            DisplayResolveInfo info = params[0];
            if (info.displayIcon == null) {
                info.displayIcon = ResolverActivity.this.loadIconForResolveInfo(info.ri);
            }
            return info;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(DisplayResolveInfo info) {
            ResolverActivity.this.mAdapter.notifyDataSetChanged();
        }
    }
}