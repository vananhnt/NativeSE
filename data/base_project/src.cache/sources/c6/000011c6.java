package android.support.v4.widget;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.view.View;
import android.widget.SearchView;

/* loaded from: SearchViewCompatHoneycomb.class */
class SearchViewCompatHoneycomb {

    /* loaded from: SearchViewCompatHoneycomb$OnCloseListenerCompatBridge.class */
    interface OnCloseListenerCompatBridge {
        boolean onClose();
    }

    /* loaded from: SearchViewCompatHoneycomb$OnQueryTextListenerCompatBridge.class */
    interface OnQueryTextListenerCompatBridge {
        boolean onQueryTextChange(String str);

        boolean onQueryTextSubmit(String str);
    }

    SearchViewCompatHoneycomb() {
    }

    public static CharSequence getQuery(View view) {
        return ((SearchView) view).getQuery();
    }

    public static boolean isIconified(View view) {
        return ((SearchView) view).isIconified();
    }

    public static boolean isQueryRefinementEnabled(View view) {
        return ((SearchView) view).isQueryRefinementEnabled();
    }

    public static boolean isSubmitButtonEnabled(View view) {
        return ((SearchView) view).isSubmitButtonEnabled();
    }

    public static Object newOnCloseListener(OnCloseListenerCompatBridge onCloseListenerCompatBridge) {
        return new SearchView.OnCloseListener(onCloseListenerCompatBridge) { // from class: android.support.v4.widget.SearchViewCompatHoneycomb.2
            final OnCloseListenerCompatBridge val$listener;

            {
                this.val$listener = onCloseListenerCompatBridge;
            }

            @Override // android.widget.SearchView.OnCloseListener
            public boolean onClose() {
                return this.val$listener.onClose();
            }
        };
    }

    public static Object newOnQueryTextListener(OnQueryTextListenerCompatBridge onQueryTextListenerCompatBridge) {
        return new SearchView.OnQueryTextListener(onQueryTextListenerCompatBridge) { // from class: android.support.v4.widget.SearchViewCompatHoneycomb.1
            final OnQueryTextListenerCompatBridge val$listener;

            {
                this.val$listener = onQueryTextListenerCompatBridge;
            }

            @Override // android.widget.SearchView.OnQueryTextListener
            public boolean onQueryTextChange(String str) {
                return this.val$listener.onQueryTextChange(str);
            }

            @Override // android.widget.SearchView.OnQueryTextListener
            public boolean onQueryTextSubmit(String str) {
                return this.val$listener.onQueryTextSubmit(str);
            }
        };
    }

    public static View newSearchView(Context context) {
        return new SearchView(context);
    }

    public static void setIconified(View view, boolean z) {
        ((SearchView) view).setIconified(z);
    }

    public static void setMaxWidth(View view, int i) {
        ((SearchView) view).setMaxWidth(i);
    }

    public static void setOnCloseListener(Object obj, Object obj2) {
        ((SearchView) obj).setOnCloseListener((SearchView.OnCloseListener) obj2);
    }

    public static void setOnQueryTextListener(Object obj, Object obj2) {
        ((SearchView) obj).setOnQueryTextListener((SearchView.OnQueryTextListener) obj2);
    }

    public static void setQuery(View view, CharSequence charSequence, boolean z) {
        ((SearchView) view).setQuery(charSequence, z);
    }

    public static void setQueryHint(View view, CharSequence charSequence) {
        ((SearchView) view).setQueryHint(charSequence);
    }

    public static void setQueryRefinementEnabled(View view, boolean z) {
        ((SearchView) view).setQueryRefinementEnabled(z);
    }

    public static void setSearchableInfo(View view, ComponentName componentName) {
        SearchView searchView = (SearchView) view;
        searchView.setSearchableInfo(((SearchManager) searchView.getContext().getSystemService("search")).getSearchableInfo(componentName));
    }

    public static void setSubmitButtonEnabled(View view, boolean z) {
        ((SearchView) view).setSubmitButtonEnabled(z);
    }
}