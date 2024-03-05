package android.support.v4.app;

import android.content.Context;
import android.os.Parcelable;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: NoSaveStateFrameLayout.class */
public class NoSaveStateFrameLayout extends FrameLayout {
    public NoSaveStateFrameLayout(Context context) {
        super(context);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ViewGroup wrap(View view) {
        NoSaveStateFrameLayout noSaveStateFrameLayout = new NoSaveStateFrameLayout(view.getContext());
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams != null) {
            noSaveStateFrameLayout.setLayoutParams(layoutParams);
        }
        view.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        noSaveStateFrameLayout.addView(view);
        return noSaveStateFrameLayout;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void dispatchRestoreInstanceState(SparseArray<Parcelable> sparseArray) {
        dispatchThawSelfOnly(sparseArray);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void dispatchSaveInstanceState(SparseArray<Parcelable> sparseArray) {
        dispatchFreezeSelfOnly(sparseArray);
    }
}