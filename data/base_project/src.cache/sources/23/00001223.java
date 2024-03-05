package android.support.v7.internal.app;

import android.support.v7.app.ActionBar;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.view.View;

/* loaded from: NavItemSelectedListener.class */
class NavItemSelectedListener implements AdapterViewCompat.OnItemSelectedListener {
    private final ActionBar.OnNavigationListener mListener;

    public NavItemSelectedListener(ActionBar.OnNavigationListener onNavigationListener) {
        this.mListener = onNavigationListener;
    }

    @Override // android.support.v7.internal.widget.AdapterViewCompat.OnItemSelectedListener
    public void onItemSelected(AdapterViewCompat<?> adapterViewCompat, View view, int i, long j) {
        ActionBar.OnNavigationListener onNavigationListener = this.mListener;
        if (onNavigationListener != null) {
            onNavigationListener.onNavigationItemSelected(i, j);
        }
    }

    @Override // android.support.v7.internal.widget.AdapterViewCompat.OnItemSelectedListener
    public void onNothingSelected(AdapterViewCompat<?> adapterViewCompat) {
    }
}