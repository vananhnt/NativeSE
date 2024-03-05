package android.support.v4.widget;

import android.view.View;
import android.widget.ListView;

/* loaded from: ListViewAutoScrollHelper.class */
public class ListViewAutoScrollHelper extends AutoScrollHelper {
    private final ListView mTarget;

    public ListViewAutoScrollHelper(ListView listView) {
        super(listView);
        this.mTarget = listView;
    }

    @Override // android.support.v4.widget.AutoScrollHelper
    public boolean canTargetScrollHorizontally(int i) {
        return false;
    }

    @Override // android.support.v4.widget.AutoScrollHelper
    public boolean canTargetScrollVertically(int i) {
        ListView listView = this.mTarget;
        int count = listView.getCount();
        if (count == 0) {
            return false;
        }
        int childCount = listView.getChildCount();
        int firstVisiblePosition = listView.getFirstVisiblePosition();
        if (i > 0) {
            return firstVisiblePosition + childCount < count || listView.getChildAt(childCount - 1).getBottom() > listView.getHeight();
        } else if (i < 0) {
            return firstVisiblePosition > 0 || listView.getChildAt(0).getTop() < 0;
        } else {
            return false;
        }
    }

    @Override // android.support.v4.widget.AutoScrollHelper
    public void scrollTargetBy(int i, int i2) {
        View childAt;
        ListView listView = this.mTarget;
        int firstVisiblePosition = listView.getFirstVisiblePosition();
        if (firstVisiblePosition == -1 || (childAt = listView.getChildAt(0)) == null) {
            return;
        }
        listView.setSelectionFromTop(firstVisiblePosition, childAt.getTop() - i2);
    }
}