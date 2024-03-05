package android.widget;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;

/* loaded from: ExpandableListAdapter.class */
public interface ExpandableListAdapter {
    void registerDataSetObserver(DataSetObserver dataSetObserver);

    void unregisterDataSetObserver(DataSetObserver dataSetObserver);

    int getGroupCount();

    int getChildrenCount(int i);

    Object getGroup(int i);

    Object getChild(int i, int i2);

    long getGroupId(int i);

    long getChildId(int i, int i2);

    boolean hasStableIds();

    View getGroupView(int i, boolean z, View view, ViewGroup viewGroup);

    View getChildView(int i, int i2, boolean z, View view, ViewGroup viewGroup);

    boolean isChildSelectable(int i, int i2);

    boolean areAllItemsEnabled();

    boolean isEmpty();

    void onGroupExpanded(int i);

    void onGroupCollapsed(int i);

    long getCombinedChildId(long j, long j2);

    long getCombinedGroupId(long j);
}