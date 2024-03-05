package android.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: RemoteViewsListAdapter.class */
public class RemoteViewsListAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<RemoteViews> mRemoteViewsList;
    private ArrayList<Integer> mViewTypes = new ArrayList<>();
    private int mViewTypeCount;

    public RemoteViewsListAdapter(Context context, ArrayList<RemoteViews> remoteViews, int viewTypeCount) {
        this.mContext = context;
        this.mRemoteViewsList = remoteViews;
        this.mViewTypeCount = viewTypeCount;
        init();
    }

    public void setViewsList(ArrayList<RemoteViews> remoteViews) {
        this.mRemoteViewsList = remoteViews;
        init();
        notifyDataSetChanged();
    }

    private void init() {
        if (this.mRemoteViewsList == null) {
            return;
        }
        this.mViewTypes.clear();
        Iterator i$ = this.mRemoteViewsList.iterator();
        while (i$.hasNext()) {
            RemoteViews rv = i$.next();
            if (!this.mViewTypes.contains(Integer.valueOf(rv.getLayoutId()))) {
                this.mViewTypes.add(Integer.valueOf(rv.getLayoutId()));
            }
        }
        if (this.mViewTypes.size() > this.mViewTypeCount || this.mViewTypeCount < 1) {
            throw new RuntimeException("Invalid view type count -- view type count must be >= 1and must be as large as the total number of distinct view types");
        }
    }

    @Override // android.widget.Adapter
    public int getCount() {
        if (this.mRemoteViewsList != null) {
            return this.mRemoteViewsList.size();
        }
        return 0;
    }

    @Override // android.widget.Adapter
    public Object getItem(int position) {
        return null;
    }

    @Override // android.widget.Adapter
    public long getItemId(int position) {
        return position;
    }

    @Override // android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;
        if (position < getCount()) {
            RemoteViews rv = this.mRemoteViewsList.get(position);
            rv.setIsWidgetCollectionChild(true);
            if (convertView != null && rv != null && convertView.getId() == rv.getLayoutId()) {
                v = convertView;
                rv.reapply(this.mContext, v);
            } else {
                v = rv.apply(this.mContext, parent);
            }
            return v;
        }
        return null;
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public int getItemViewType(int position) {
        if (position < getCount()) {
            int layoutId = this.mRemoteViewsList.get(position).getLayoutId();
            return this.mViewTypes.indexOf(Integer.valueOf(layoutId));
        }
        return 0;
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public int getViewTypeCount() {
        return this.mViewTypeCount;
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public boolean hasStableIds() {
        return false;
    }
}