package android.widget;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;

/* loaded from: Adapter.class */
public interface Adapter {
    public static final int IGNORE_ITEM_VIEW_TYPE = -1;
    public static final int NO_SELECTION = Integer.MIN_VALUE;

    void registerDataSetObserver(DataSetObserver dataSetObserver);

    void unregisterDataSetObserver(DataSetObserver dataSetObserver);

    int getCount();

    Object getItem(int i);

    long getItemId(int i);

    boolean hasStableIds();

    View getView(int i, View view, ViewGroup viewGroup);

    int getItemViewType(int i);

    int getViewTypeCount();

    boolean isEmpty();
}