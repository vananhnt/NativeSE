package android.preference;

import android.os.Handler;
import android.preference.Preference;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: PreferenceGroupAdapter.class */
public class PreferenceGroupAdapter extends BaseAdapter implements Preference.OnPreferenceChangeInternalListener {
    private static final String TAG = "PreferenceGroupAdapter";
    private PreferenceGroup mPreferenceGroup;
    private List<Preference> mPreferenceList;
    private ArrayList<PreferenceLayout> mPreferenceLayouts;
    private PreferenceLayout mTempPreferenceLayout = new PreferenceLayout();
    private boolean mHasReturnedViewTypeCount = false;
    private volatile boolean mIsSyncing = false;
    private Handler mHandler = new Handler();
    private Runnable mSyncRunnable = new Runnable() { // from class: android.preference.PreferenceGroupAdapter.1
        @Override // java.lang.Runnable
        public void run() {
            PreferenceGroupAdapter.this.syncMyPreferences();
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: PreferenceGroupAdapter$PreferenceLayout.class */
    public static class PreferenceLayout implements Comparable<PreferenceLayout> {
        private int resId;
        private int widgetResId;
        private String name;

        private PreferenceLayout() {
        }

        @Override // java.lang.Comparable
        public int compareTo(PreferenceLayout other) {
            int compareNames = this.name.compareTo(other.name);
            if (compareNames == 0) {
                if (this.resId == other.resId) {
                    if (this.widgetResId == other.widgetResId) {
                        return 0;
                    }
                    return this.widgetResId - other.widgetResId;
                }
                return this.resId - other.resId;
            }
            return compareNames;
        }
    }

    public PreferenceGroupAdapter(PreferenceGroup preferenceGroup) {
        this.mPreferenceGroup = preferenceGroup;
        this.mPreferenceGroup.setOnPreferenceChangeInternalListener(this);
        this.mPreferenceList = new ArrayList();
        this.mPreferenceLayouts = new ArrayList<>();
        syncMyPreferences();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void syncMyPreferences() {
        synchronized (this) {
            if (this.mIsSyncing) {
                return;
            }
            this.mIsSyncing = true;
            List<Preference> newPreferenceList = new ArrayList<>(this.mPreferenceList.size());
            flattenPreferenceGroup(newPreferenceList, this.mPreferenceGroup);
            this.mPreferenceList = newPreferenceList;
            notifyDataSetChanged();
            synchronized (this) {
                this.mIsSyncing = false;
                notifyAll();
            }
        }
    }

    private void flattenPreferenceGroup(List<Preference> preferences, PreferenceGroup group) {
        group.sortPreferences();
        int groupSize = group.getPreferenceCount();
        for (int i = 0; i < groupSize; i++) {
            Preference preference = group.getPreference(i);
            preferences.add(preference);
            if (!this.mHasReturnedViewTypeCount && preference.canRecycleLayout()) {
                addPreferenceClassName(preference);
            }
            if (preference instanceof PreferenceGroup) {
                PreferenceGroup preferenceAsGroup = (PreferenceGroup) preference;
                if (preferenceAsGroup.isOnSameScreenAsChildren()) {
                    flattenPreferenceGroup(preferences, preferenceAsGroup);
                }
            }
            preference.setOnPreferenceChangeInternalListener(this);
        }
    }

    private PreferenceLayout createPreferenceLayout(Preference preference, PreferenceLayout in) {
        PreferenceLayout pl = in != null ? in : new PreferenceLayout();
        pl.name = preference.getClass().getName();
        pl.resId = preference.getLayoutResource();
        pl.widgetResId = preference.getWidgetLayoutResource();
        return pl;
    }

    private void addPreferenceClassName(Preference preference) {
        PreferenceLayout pl = createPreferenceLayout(preference, null);
        int insertPos = Collections.binarySearch(this.mPreferenceLayouts, pl);
        if (insertPos < 0) {
            this.mPreferenceLayouts.add((insertPos * (-1)) - 1, pl);
        }
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.mPreferenceList.size();
    }

    @Override // android.widget.Adapter
    public Preference getItem(int position) {
        if (position < 0 || position >= getCount()) {
            return null;
        }
        return this.mPreferenceList.get(position);
    }

    @Override // android.widget.Adapter
    public long getItemId(int position) {
        if (position < 0 || position >= getCount()) {
            return Long.MIN_VALUE;
        }
        return getItem(position).getId();
    }

    @Override // android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        Preference preference = getItem(position);
        this.mTempPreferenceLayout = createPreferenceLayout(preference, this.mTempPreferenceLayout);
        if (Collections.binarySearch(this.mPreferenceLayouts, this.mTempPreferenceLayout) < 0) {
            convertView = null;
        }
        return preference.getView(convertView, parent);
    }

    @Override // android.widget.BaseAdapter, android.widget.ListAdapter
    public boolean isEnabled(int position) {
        if (position < 0 || position >= getCount()) {
            return true;
        }
        return getItem(position).isSelectable();
    }

    @Override // android.widget.BaseAdapter, android.widget.ListAdapter
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override // android.preference.Preference.OnPreferenceChangeInternalListener
    public void onPreferenceChange(Preference preference) {
        notifyDataSetChanged();
    }

    @Override // android.preference.Preference.OnPreferenceChangeInternalListener
    public void onPreferenceHierarchyChange(Preference preference) {
        this.mHandler.removeCallbacks(this.mSyncRunnable);
        this.mHandler.post(this.mSyncRunnable);
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public boolean hasStableIds() {
        return true;
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public int getItemViewType(int position) {
        if (!this.mHasReturnedViewTypeCount) {
            this.mHasReturnedViewTypeCount = true;
        }
        Preference preference = getItem(position);
        if (!preference.canRecycleLayout()) {
            return -1;
        }
        this.mTempPreferenceLayout = createPreferenceLayout(preference, this.mTempPreferenceLayout);
        int viewType = Collections.binarySearch(this.mPreferenceLayouts, this.mTempPreferenceLayout);
        if (viewType < 0) {
            return -1;
        }
        return viewType;
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public int getViewTypeCount() {
        if (!this.mHasReturnedViewTypeCount) {
            this.mHasReturnedViewTypeCount = true;
        }
        return Math.max(1, this.mPreferenceLayouts.size());
    }
}