package com.android.internal.app;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaRouter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.android.internal.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/* loaded from: MediaRouteChooserDialogFragment.class */
public class MediaRouteChooserDialogFragment extends DialogFragment {
    private static final String TAG = "MediaRouteChooserDialogFragment";
    public static final String FRAGMENT_TAG = "android:MediaRouteChooserDialogFragment";
    private static final int[] ITEM_LAYOUTS = {R.layout.media_route_list_item_top_header, R.layout.media_route_list_item_section_header, R.layout.media_route_list_item, R.layout.media_route_list_item_checkable, R.layout.media_route_list_item_collapse_group};
    MediaRouter mRouter;
    private int mRouteTypes;
    private LayoutInflater mInflater;
    private LauncherListener mLauncherListener;
    private View.OnClickListener mExtendedSettingsListener;
    private RouteAdapter mAdapter;
    private ListView mListView;
    private SeekBar mVolumeSlider;
    private ImageView mVolumeIcon;
    final RouteComparator mComparator = new RouteComparator();
    final MediaRouterCallback mCallback = new MediaRouterCallback();
    private boolean mIgnoreSliderVolumeChanges;
    private boolean mIgnoreCallbackVolumeChanges;

    /* loaded from: MediaRouteChooserDialogFragment$LauncherListener.class */
    public interface LauncherListener {
        void onDetached(MediaRouteChooserDialogFragment mediaRouteChooserDialogFragment);
    }

    public MediaRouteChooserDialogFragment() {
        setStyle(1, 16974126);
    }

    public void setLauncherListener(LauncherListener listener) {
        this.mLauncherListener = listener;
    }

    @Override // android.app.DialogFragment, android.app.Fragment
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mRouter = (MediaRouter) activity.getSystemService(Context.MEDIA_ROUTER_SERVICE);
        this.mRouter.addCallback(this.mRouteTypes, this.mCallback, 1);
    }

    @Override // android.app.DialogFragment, android.app.Fragment
    public void onDetach() {
        super.onDetach();
        if (this.mLauncherListener != null) {
            this.mLauncherListener.onDetached(this);
        }
        if (this.mAdapter != null) {
            this.mAdapter = null;
        }
        this.mInflater = null;
        this.mRouter.removeCallback(this.mCallback);
        this.mRouter = null;
    }

    public void setExtendedSettingsClickListener(View.OnClickListener listener) {
        this.mExtendedSettingsListener = listener;
    }

    public void setRouteTypes(int types) {
        this.mRouteTypes = types;
    }

    void updateVolume() {
        if (this.mRouter == null) {
            return;
        }
        MediaRouter.RouteInfo selectedRoute = this.mRouter.getSelectedRoute(this.mRouteTypes);
        this.mVolumeIcon.setImageResource((selectedRoute == null || selectedRoute.getPlaybackType() == 0) ? R.drawable.ic_audio_vol : R.drawable.ic_media_route_on_holo_dark);
        this.mIgnoreSliderVolumeChanges = true;
        if (selectedRoute == null || selectedRoute.getVolumeHandling() == 0) {
            this.mVolumeSlider.setMax(1);
            this.mVolumeSlider.setProgress(1);
            this.mVolumeSlider.setEnabled(false);
        } else {
            this.mVolumeSlider.setEnabled(true);
            this.mVolumeSlider.setMax(selectedRoute.getVolumeMax());
            this.mVolumeSlider.setProgress(selectedRoute.getVolume());
        }
        this.mIgnoreSliderVolumeChanges = false;
    }

    void changeVolume(int newValue) {
        MediaRouter.RouteInfo selectedRoute;
        if (!this.mIgnoreSliderVolumeChanges && (selectedRoute = this.mRouter.getSelectedRoute(this.mRouteTypes)) != null && selectedRoute.getVolumeHandling() == 1) {
            int maxVolume = selectedRoute.getVolumeMax();
            selectedRoute.requestSetVolume(Math.max(0, Math.min(newValue, maxVolume)));
        }
    }

    @Override // android.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mInflater = inflater;
        View layout = inflater.inflate(R.layout.media_route_chooser_layout, container, false);
        this.mVolumeIcon = (ImageView) layout.findViewById(R.id.volume_icon);
        this.mVolumeSlider = (SeekBar) layout.findViewById(R.id.volume_slider);
        updateVolume();
        this.mVolumeSlider.setOnSeekBarChangeListener(new VolumeSliderChangeListener());
        if (this.mExtendedSettingsListener != null) {
            View extendedSettingsButton = layout.findViewById(R.id.extended_settings);
            extendedSettingsButton.setVisibility(0);
            extendedSettingsButton.setOnClickListener(this.mExtendedSettingsListener);
        }
        ListView list = (ListView) layout.findViewById(16908298);
        list.setItemsCanFocus(true);
        RouteAdapter routeAdapter = new RouteAdapter();
        this.mAdapter = routeAdapter;
        list.setAdapter((ListAdapter) routeAdapter);
        list.setOnItemClickListener(this.mAdapter);
        this.mListView = list;
        this.mAdapter.scrollToSelectedItem();
        return layout;
    }

    @Override // android.app.DialogFragment
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new RouteChooserDialog(getActivity(), getTheme());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: MediaRouteChooserDialogFragment$ViewHolder.class */
    public static class ViewHolder {
        public TextView text1;
        public TextView text2;
        public ImageView icon;
        public ImageButton expandGroupButton;
        public RouteAdapter.ExpandGroupListener expandGroupListener;
        public int position;
        public CheckBox check;

        private ViewHolder() {
        }
    }

    /* loaded from: MediaRouteChooserDialogFragment$RouteAdapter.class */
    private class RouteAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {
        private static final int VIEW_TOP_HEADER = 0;
        private static final int VIEW_SECTION_HEADER = 1;
        private static final int VIEW_ROUTE = 2;
        private static final int VIEW_GROUPING_ROUTE = 3;
        private static final int VIEW_GROUPING_DONE = 4;
        private MediaRouter.RouteCategory mCategoryEditingGroups;
        private MediaRouter.RouteGroup mEditingGroup;
        private boolean mIgnoreUpdates;
        private int mSelectedItemPosition = -1;
        private final ArrayList<Object> mItems = new ArrayList<>();
        private final ArrayList<MediaRouter.RouteInfo> mCatRouteList = new ArrayList<>();
        private final ArrayList<MediaRouter.RouteInfo> mSortRouteList = new ArrayList<>();

        RouteAdapter() {
            update();
        }

        void update() {
            if (this.mIgnoreUpdates) {
                return;
            }
            this.mItems.clear();
            MediaRouter.RouteInfo selectedRoute = MediaRouteChooserDialogFragment.this.mRouter.getSelectedRoute(MediaRouteChooserDialogFragment.this.mRouteTypes);
            this.mSelectedItemPosition = -1;
            int catCount = MediaRouteChooserDialogFragment.this.mRouter.getCategoryCount();
            for (int i = 0; i < catCount; i++) {
                MediaRouter.RouteCategory cat = MediaRouteChooserDialogFragment.this.mRouter.getCategoryAt(i);
                List<MediaRouter.RouteInfo> routes = cat.getRoutes(this.mCatRouteList);
                if (!cat.isSystem()) {
                    this.mItems.add(cat);
                }
                if (cat == this.mCategoryEditingGroups) {
                    addGroupEditingCategoryRoutes(routes);
                } else {
                    addSelectableRoutes(selectedRoute, routes);
                }
                routes.clear();
            }
            notifyDataSetChanged();
            if (MediaRouteChooserDialogFragment.this.mListView != null && this.mSelectedItemPosition >= 0) {
                MediaRouteChooserDialogFragment.this.mListView.setItemChecked(this.mSelectedItemPosition, true);
            }
        }

        void scrollToEditingGroup() {
            if (this.mCategoryEditingGroups == null || MediaRouteChooserDialogFragment.this.mListView == null) {
                return;
            }
            int pos = 0;
            int bound = 0;
            int itemCount = this.mItems.size();
            int i = 0;
            while (true) {
                if (i >= itemCount) {
                    break;
                }
                Object item = this.mItems.get(i);
                if (item != null && item == this.mCategoryEditingGroups) {
                    bound = i;
                }
                if (item != null) {
                    i++;
                } else {
                    pos = i;
                    break;
                }
            }
            MediaRouteChooserDialogFragment.this.mListView.smoothScrollToPosition(pos, bound);
        }

        void scrollToSelectedItem() {
            if (MediaRouteChooserDialogFragment.this.mListView == null || this.mSelectedItemPosition < 0) {
                return;
            }
            MediaRouteChooserDialogFragment.this.mListView.smoothScrollToPosition(this.mSelectedItemPosition);
        }

        void addSelectableRoutes(MediaRouter.RouteInfo selectedRoute, List<MediaRouter.RouteInfo> from) {
            int routeCount = from.size();
            for (int j = 0; j < routeCount; j++) {
                MediaRouter.RouteInfo info = from.get(j);
                if (info == selectedRoute) {
                    this.mSelectedItemPosition = this.mItems.size();
                }
                this.mItems.add(info);
            }
        }

        void addGroupEditingCategoryRoutes(List<MediaRouter.RouteInfo> from) {
            int topCount = from.size();
            for (int i = 0; i < topCount; i++) {
                MediaRouter.RouteInfo route = from.get(i);
                MediaRouter.RouteGroup group = route.getGroup();
                if (group == route) {
                    int groupCount = group.getRouteCount();
                    for (int j = 0; j < groupCount; j++) {
                        MediaRouter.RouteInfo innerRoute = group.getRouteAt(j);
                        this.mSortRouteList.add(innerRoute);
                    }
                } else {
                    this.mSortRouteList.add(route);
                }
            }
            Collections.sort(this.mSortRouteList, MediaRouteChooserDialogFragment.this.mComparator);
            this.mItems.addAll(this.mSortRouteList);
            this.mSortRouteList.clear();
            this.mItems.add(null);
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return this.mItems.size();
        }

        @Override // android.widget.BaseAdapter, android.widget.Adapter
        public int getViewTypeCount() {
            return 5;
        }

        @Override // android.widget.BaseAdapter, android.widget.Adapter
        public int getItemViewType(int position) {
            Object item = getItem(position);
            if (item instanceof MediaRouter.RouteCategory) {
                return position == 0 ? 0 : 1;
            } else if (item == null) {
                return 4;
            } else {
                MediaRouter.RouteInfo info = (MediaRouter.RouteInfo) item;
                if (info.getCategory() == this.mCategoryEditingGroups) {
                    return 3;
                }
                return 2;
            }
        }

        @Override // android.widget.BaseAdapter, android.widget.ListAdapter
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override // android.widget.BaseAdapter, android.widget.ListAdapter
        public boolean isEnabled(int position) {
            switch (getItemViewType(position)) {
                case 2:
                    return ((MediaRouter.RouteInfo) this.mItems.get(position)).isEnabled();
                case 3:
                case 4:
                    return true;
                default:
                    return false;
            }
        }

        @Override // android.widget.Adapter
        public Object getItem(int position) {
            return this.mItems.get(position);
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }

        @Override // android.widget.Adapter
        public View getView(int position, final View convertView, ViewGroup parent) {
            final ViewHolder holder;
            int viewType = getItemViewType(position);
            if (convertView == null) {
                convertView = MediaRouteChooserDialogFragment.this.mInflater.inflate(MediaRouteChooserDialogFragment.ITEM_LAYOUTS[viewType], parent, false);
                holder = new ViewHolder();
                holder.position = position;
                holder.text1 = (TextView) convertView.findViewById(16908308);
                holder.text2 = (TextView) convertView.findViewById(16908309);
                holder.icon = (ImageView) convertView.findViewById(16908294);
                holder.check = (CheckBox) convertView.findViewById(R.id.check);
                holder.expandGroupButton = (ImageButton) convertView.findViewById(R.id.expand_button);
                if (holder.expandGroupButton != null) {
                    holder.expandGroupListener = new ExpandGroupListener();
                    holder.expandGroupButton.setOnClickListener(holder.expandGroupListener);
                }
                final ListView list = (ListView) parent;
                convertView.setOnClickListener(new View.OnClickListener() { // from class: com.android.internal.app.MediaRouteChooserDialogFragment.RouteAdapter.1
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v) {
                        list.performItemClick(convertView, holder.position, 0L);
                    }
                });
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
                holder.position = position;
            }
            switch (viewType) {
                case 0:
                case 1:
                    bindHeaderView(position, holder);
                    break;
                case 2:
                case 3:
                    bindItemView(position, holder);
                    break;
            }
            convertView.setActivated(position == this.mSelectedItemPosition);
            convertView.setEnabled(isEnabled(position));
            return convertView;
        }

        void bindItemView(int position, ViewHolder holder) {
            MediaRouter.RouteInfo info = (MediaRouter.RouteInfo) this.mItems.get(position);
            holder.text1.setText(info.getName(MediaRouteChooserDialogFragment.this.getActivity()));
            CharSequence status = info.getStatus();
            if (TextUtils.isEmpty(status)) {
                holder.text2.setVisibility(8);
            } else {
                holder.text2.setVisibility(0);
                holder.text2.setText(status);
            }
            Drawable icon = info.getIconDrawable();
            if (icon != null) {
                icon = icon.getConstantState().newDrawable(MediaRouteChooserDialogFragment.this.getResources());
            }
            holder.icon.setImageDrawable(icon);
            holder.icon.setVisibility(icon != null ? 0 : 8);
            MediaRouter.RouteCategory cat = info.getCategory();
            boolean canGroup = false;
            if (cat == this.mCategoryEditingGroups) {
                MediaRouter.RouteGroup group = info.getGroup();
                holder.check.setEnabled(group.getRouteCount() > 1);
                holder.check.setChecked(group == this.mEditingGroup);
            } else if (cat.isGroupable()) {
                canGroup = ((MediaRouter.RouteGroup) info).getRouteCount() > 1 || getItemViewType(position - 1) == 2 || (position < getCount() - 1 && getItemViewType(position + 1) == 2);
            }
            if (holder.expandGroupButton != null) {
                holder.expandGroupButton.setVisibility(canGroup ? 0 : 8);
                holder.expandGroupListener.position = position;
            }
        }

        void bindHeaderView(int position, ViewHolder holder) {
            MediaRouter.RouteCategory cat = (MediaRouter.RouteCategory) this.mItems.get(position);
            holder.text1.setText(cat.getName(MediaRouteChooserDialogFragment.this.getActivity()));
        }

        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            int type = getItemViewType(position);
            if (type == 1 || type == 0) {
                return;
            }
            if (type == 4) {
                finishGrouping();
                return;
            }
            Object item = getItem(position);
            if (!(item instanceof MediaRouter.RouteInfo)) {
                return;
            }
            MediaRouter.RouteInfo route = (MediaRouter.RouteInfo) item;
            if (type == 2) {
                MediaRouteChooserDialogFragment.this.mRouter.selectRouteInt(MediaRouteChooserDialogFragment.this.mRouteTypes, route);
                MediaRouteChooserDialogFragment.this.dismiss();
            } else if (type == 3) {
                Checkable c = (Checkable) view;
                boolean wasChecked = c.isChecked();
                this.mIgnoreUpdates = true;
                MediaRouter.RouteGroup oldGroup = route.getGroup();
                if (!wasChecked && oldGroup != this.mEditingGroup) {
                    if (MediaRouteChooserDialogFragment.this.mRouter.getSelectedRoute(MediaRouteChooserDialogFragment.this.mRouteTypes) == oldGroup) {
                        MediaRouteChooserDialogFragment.this.mRouter.selectRouteInt(MediaRouteChooserDialogFragment.this.mRouteTypes, this.mEditingGroup);
                    }
                    oldGroup.removeRoute(route);
                    this.mEditingGroup.addRoute(route);
                    c.setChecked(true);
                } else if (wasChecked && this.mEditingGroup.getRouteCount() > 1) {
                    this.mEditingGroup.removeRoute(route);
                    MediaRouteChooserDialogFragment.this.mRouter.addRouteInt(route);
                }
                this.mIgnoreUpdates = false;
                update();
            }
        }

        boolean isGrouping() {
            return this.mCategoryEditingGroups != null;
        }

        void finishGrouping() {
            this.mCategoryEditingGroups = null;
            this.mEditingGroup = null;
            MediaRouteChooserDialogFragment.this.getDialog().setCanceledOnTouchOutside(true);
            update();
            scrollToSelectedItem();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: MediaRouteChooserDialogFragment$RouteAdapter$ExpandGroupListener.class */
        public class ExpandGroupListener implements View.OnClickListener {
            int position;

            ExpandGroupListener() {
            }

            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                MediaRouter.RouteGroup group = (MediaRouter.RouteGroup) RouteAdapter.this.getItem(this.position);
                RouteAdapter.this.mEditingGroup = group;
                RouteAdapter.this.mCategoryEditingGroups = group.getCategory();
                MediaRouteChooserDialogFragment.this.getDialog().setCanceledOnTouchOutside(false);
                MediaRouteChooserDialogFragment.this.mRouter.selectRouteInt(MediaRouteChooserDialogFragment.this.mRouteTypes, RouteAdapter.this.mEditingGroup);
                RouteAdapter.this.update();
                RouteAdapter.this.scrollToEditingGroup();
            }
        }
    }

    /* loaded from: MediaRouteChooserDialogFragment$MediaRouterCallback.class */
    class MediaRouterCallback extends MediaRouter.Callback {
        MediaRouterCallback() {
        }

        @Override // android.media.MediaRouter.Callback
        public void onRouteSelected(MediaRouter router, int type, MediaRouter.RouteInfo info) {
            MediaRouteChooserDialogFragment.this.mAdapter.update();
            MediaRouteChooserDialogFragment.this.updateVolume();
        }

        @Override // android.media.MediaRouter.Callback
        public void onRouteUnselected(MediaRouter router, int type, MediaRouter.RouteInfo info) {
            MediaRouteChooserDialogFragment.this.mAdapter.update();
        }

        @Override // android.media.MediaRouter.Callback
        public void onRouteAdded(MediaRouter router, MediaRouter.RouteInfo info) {
            MediaRouteChooserDialogFragment.this.mAdapter.update();
        }

        @Override // android.media.MediaRouter.Callback
        public void onRouteRemoved(MediaRouter router, MediaRouter.RouteInfo info) {
            if (info == MediaRouteChooserDialogFragment.this.mAdapter.mEditingGroup) {
                MediaRouteChooserDialogFragment.this.mAdapter.finishGrouping();
            }
            MediaRouteChooserDialogFragment.this.mAdapter.update();
        }

        @Override // android.media.MediaRouter.Callback
        public void onRouteChanged(MediaRouter router, MediaRouter.RouteInfo info) {
            MediaRouteChooserDialogFragment.this.mAdapter.notifyDataSetChanged();
        }

        @Override // android.media.MediaRouter.Callback
        public void onRouteGrouped(MediaRouter router, MediaRouter.RouteInfo info, MediaRouter.RouteGroup group, int index) {
            MediaRouteChooserDialogFragment.this.mAdapter.update();
        }

        @Override // android.media.MediaRouter.Callback
        public void onRouteUngrouped(MediaRouter router, MediaRouter.RouteInfo info, MediaRouter.RouteGroup group) {
            MediaRouteChooserDialogFragment.this.mAdapter.update();
        }

        @Override // android.media.MediaRouter.Callback
        public void onRouteVolumeChanged(MediaRouter router, MediaRouter.RouteInfo info) {
            if (!MediaRouteChooserDialogFragment.this.mIgnoreCallbackVolumeChanges) {
                MediaRouteChooserDialogFragment.this.updateVolume();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: MediaRouteChooserDialogFragment$RouteComparator.class */
    public class RouteComparator implements Comparator<MediaRouter.RouteInfo> {
        RouteComparator() {
        }

        @Override // java.util.Comparator
        public int compare(MediaRouter.RouteInfo lhs, MediaRouter.RouteInfo rhs) {
            return lhs.getName(MediaRouteChooserDialogFragment.this.getActivity()).toString().compareTo(rhs.getName(MediaRouteChooserDialogFragment.this.getActivity()).toString());
        }
    }

    /* loaded from: MediaRouteChooserDialogFragment$RouteChooserDialog.class */
    class RouteChooserDialog extends Dialog {
        public RouteChooserDialog(Context context, int theme) {
            super(context, theme);
        }

        @Override // android.app.Dialog
        public void onBackPressed() {
            if (MediaRouteChooserDialogFragment.this.mAdapter != null && MediaRouteChooserDialogFragment.this.mAdapter.isGrouping()) {
                MediaRouteChooserDialogFragment.this.mAdapter.finishGrouping();
            } else {
                super.onBackPressed();
            }
        }

        @Override // android.app.Dialog, android.view.KeyEvent.Callback
        public boolean onKeyDown(int keyCode, KeyEvent event) {
            if (keyCode != 25 || !MediaRouteChooserDialogFragment.this.mVolumeSlider.isEnabled()) {
                if (keyCode == 24 && MediaRouteChooserDialogFragment.this.mVolumeSlider.isEnabled() && MediaRouteChooserDialogFragment.this.mRouter.getSelectedRoute(MediaRouteChooserDialogFragment.this.mRouteTypes) != null) {
                    MediaRouteChooserDialogFragment.this.mRouter.getSelectedRoute(MediaRouteChooserDialogFragment.this.mRouteTypes).requestUpdateVolume(1);
                    return true;
                }
            } else {
                MediaRouter.RouteInfo selectedRoute = MediaRouteChooserDialogFragment.this.mRouter.getSelectedRoute(MediaRouteChooserDialogFragment.this.mRouteTypes);
                if (selectedRoute != null) {
                    selectedRoute.requestUpdateVolume(-1);
                    return true;
                }
            }
            return super.onKeyDown(keyCode, event);
        }

        @Override // android.app.Dialog, android.view.KeyEvent.Callback
        public boolean onKeyUp(int keyCode, KeyEvent event) {
            if (keyCode == 25 && MediaRouteChooserDialogFragment.this.mVolumeSlider.isEnabled()) {
                return true;
            }
            if (keyCode == 24 && MediaRouteChooserDialogFragment.this.mVolumeSlider.isEnabled()) {
                return true;
            }
            return super.onKeyUp(keyCode, event);
        }
    }

    /* loaded from: MediaRouteChooserDialogFragment$VolumeSliderChangeListener.class */
    class VolumeSliderChangeListener implements SeekBar.OnSeekBarChangeListener {
        VolumeSliderChangeListener() {
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            MediaRouteChooserDialogFragment.this.changeVolume(progress);
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onStartTrackingTouch(SeekBar seekBar) {
            MediaRouteChooserDialogFragment.this.mIgnoreCallbackVolumeChanges = true;
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onStopTrackingTouch(SeekBar seekBar) {
            MediaRouteChooserDialogFragment.this.mIgnoreCallbackVolumeChanges = false;
            MediaRouteChooserDialogFragment.this.updateVolume();
        }
    }
}