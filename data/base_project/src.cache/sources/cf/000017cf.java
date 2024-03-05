package android.widget;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: HeaderViewListAdapter.class */
public class HeaderViewListAdapter implements WrapperListAdapter, Filterable {
    private final ListAdapter mAdapter;
    ArrayList<ListView.FixedViewInfo> mHeaderViewInfos;
    ArrayList<ListView.FixedViewInfo> mFooterViewInfos;
    static final ArrayList<ListView.FixedViewInfo> EMPTY_INFO_LIST = new ArrayList<>();
    boolean mAreAllFixedViewsSelectable;
    private final boolean mIsFilterable;

    public HeaderViewListAdapter(ArrayList<ListView.FixedViewInfo> headerViewInfos, ArrayList<ListView.FixedViewInfo> footerViewInfos, ListAdapter adapter) {
        this.mAdapter = adapter;
        this.mIsFilterable = adapter instanceof Filterable;
        if (headerViewInfos == null) {
            this.mHeaderViewInfos = EMPTY_INFO_LIST;
        } else {
            this.mHeaderViewInfos = headerViewInfos;
        }
        if (footerViewInfos == null) {
            this.mFooterViewInfos = EMPTY_INFO_LIST;
        } else {
            this.mFooterViewInfos = footerViewInfos;
        }
        this.mAreAllFixedViewsSelectable = areAllListInfosSelectable(this.mHeaderViewInfos) && areAllListInfosSelectable(this.mFooterViewInfos);
    }

    public int getHeadersCount() {
        return this.mHeaderViewInfos.size();
    }

    public int getFootersCount() {
        return this.mFooterViewInfos.size();
    }

    @Override // android.widget.Adapter
    public boolean isEmpty() {
        return this.mAdapter == null || this.mAdapter.isEmpty();
    }

    private boolean areAllListInfosSelectable(ArrayList<ListView.FixedViewInfo> infos) {
        if (infos != null) {
            Iterator i$ = infos.iterator();
            while (i$.hasNext()) {
                ListView.FixedViewInfo info = i$.next();
                if (!info.isSelectable) {
                    return false;
                }
            }
            return true;
        }
        return true;
    }

    public boolean removeHeader(View v) {
        for (int i = 0; i < this.mHeaderViewInfos.size(); i++) {
            ListView.FixedViewInfo info = this.mHeaderViewInfos.get(i);
            if (info.view == v) {
                this.mHeaderViewInfos.remove(i);
                this.mAreAllFixedViewsSelectable = areAllListInfosSelectable(this.mHeaderViewInfos) && areAllListInfosSelectable(this.mFooterViewInfos);
                return true;
            }
        }
        return false;
    }

    public boolean removeFooter(View v) {
        for (int i = 0; i < this.mFooterViewInfos.size(); i++) {
            ListView.FixedViewInfo info = this.mFooterViewInfos.get(i);
            if (info.view == v) {
                this.mFooterViewInfos.remove(i);
                this.mAreAllFixedViewsSelectable = areAllListInfosSelectable(this.mHeaderViewInfos) && areAllListInfosSelectable(this.mFooterViewInfos);
                return true;
            }
        }
        return false;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        if (this.mAdapter != null) {
            return getFootersCount() + getHeadersCount() + this.mAdapter.getCount();
        }
        return getFootersCount() + getHeadersCount();
    }

    @Override // android.widget.ListAdapter
    public boolean areAllItemsEnabled() {
        if (this.mAdapter != null) {
            return this.mAreAllFixedViewsSelectable && this.mAdapter.areAllItemsEnabled();
        }
        return true;
    }

    @Override // android.widget.ListAdapter
    public boolean isEnabled(int position) {
        int numHeaders = getHeadersCount();
        if (position < numHeaders) {
            return this.mHeaderViewInfos.get(position).isSelectable;
        }
        int adjPosition = position - numHeaders;
        int adapterCount = 0;
        if (this.mAdapter != null) {
            adapterCount = this.mAdapter.getCount();
            if (adjPosition < adapterCount) {
                return this.mAdapter.isEnabled(adjPosition);
            }
        }
        return this.mFooterViewInfos.get(adjPosition - adapterCount).isSelectable;
    }

    @Override // android.widget.Adapter
    public Object getItem(int position) {
        int numHeaders = getHeadersCount();
        if (position < numHeaders) {
            return this.mHeaderViewInfos.get(position).data;
        }
        int adjPosition = position - numHeaders;
        int adapterCount = 0;
        if (this.mAdapter != null) {
            adapterCount = this.mAdapter.getCount();
            if (adjPosition < adapterCount) {
                return this.mAdapter.getItem(adjPosition);
            }
        }
        return this.mFooterViewInfos.get(adjPosition - adapterCount).data;
    }

    @Override // android.widget.Adapter
    public long getItemId(int position) {
        int numHeaders = getHeadersCount();
        if (this.mAdapter != null && position >= numHeaders) {
            int adjPosition = position - numHeaders;
            int adapterCount = this.mAdapter.getCount();
            if (adjPosition < adapterCount) {
                return this.mAdapter.getItemId(adjPosition);
            }
            return -1L;
        }
        return -1L;
    }

    @Override // android.widget.Adapter
    public boolean hasStableIds() {
        if (this.mAdapter != null) {
            return this.mAdapter.hasStableIds();
        }
        return false;
    }

    @Override // android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        int numHeaders = getHeadersCount();
        if (position < numHeaders) {
            return this.mHeaderViewInfos.get(position).view;
        }
        int adjPosition = position - numHeaders;
        int adapterCount = 0;
        if (this.mAdapter != null) {
            adapterCount = this.mAdapter.getCount();
            if (adjPosition < adapterCount) {
                return this.mAdapter.getView(adjPosition, convertView, parent);
            }
        }
        return this.mFooterViewInfos.get(adjPosition - adapterCount).view;
    }

    @Override // android.widget.Adapter
    public int getItemViewType(int position) {
        int numHeaders = getHeadersCount();
        if (this.mAdapter != null && position >= numHeaders) {
            int adjPosition = position - numHeaders;
            int adapterCount = this.mAdapter.getCount();
            if (adjPosition < adapterCount) {
                return this.mAdapter.getItemViewType(adjPosition);
            }
            return -2;
        }
        return -2;
    }

    @Override // android.widget.Adapter
    public int getViewTypeCount() {
        if (this.mAdapter != null) {
            return this.mAdapter.getViewTypeCount();
        }
        return 1;
    }

    @Override // android.widget.Adapter
    public void registerDataSetObserver(DataSetObserver observer) {
        if (this.mAdapter != null) {
            this.mAdapter.registerDataSetObserver(observer);
        }
    }

    @Override // android.widget.Adapter
    public void unregisterDataSetObserver(DataSetObserver observer) {
        if (this.mAdapter != null) {
            this.mAdapter.unregisterDataSetObserver(observer);
        }
    }

    @Override // android.widget.Filterable
    public Filter getFilter() {
        if (this.mIsFilterable) {
            return ((Filterable) this.mAdapter).getFilter();
        }
        return null;
    }

    @Override // android.widget.WrapperListAdapter
    public ListAdapter getWrappedAdapter() {
        return this.mAdapter;
    }
}