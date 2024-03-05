package android.app;

import android.content.Context;
import android.content.ContextWrapper;
import android.media.MediaRouter;
import android.util.Log;
import android.view.ActionProvider;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.android.internal.app.MediaRouteChooserDialogFragment;
import java.lang.ref.WeakReference;

/* loaded from: MediaRouteActionProvider.class */
public class MediaRouteActionProvider extends ActionProvider {
    private static final String TAG = "MediaRouteActionProvider";
    private Context mContext;
    private MediaRouter mRouter;
    private MenuItem mMenuItem;
    private MediaRouteButton mView;
    private int mRouteTypes;
    private View.OnClickListener mExtendedSettingsListener;
    private RouterCallback mCallback;

    public MediaRouteActionProvider(Context context) {
        super(context);
        this.mContext = context;
        this.mRouter = (MediaRouter) context.getSystemService(Context.MEDIA_ROUTER_SERVICE);
        this.mCallback = new RouterCallback(this);
        setRouteTypes(1);
    }

    public void setRouteTypes(int types) {
        if (this.mRouteTypes == types) {
            return;
        }
        if (this.mRouteTypes != 0) {
            this.mRouter.removeCallback(this.mCallback);
        }
        this.mRouteTypes = types;
        if (types != 0) {
            this.mRouter.addCallback(types, this.mCallback);
        }
        if (this.mView != null) {
            this.mView.setRouteTypes(this.mRouteTypes);
        }
    }

    @Override // android.view.ActionProvider
    public View onCreateActionView() {
        throw new UnsupportedOperationException("Use onCreateActionView(MenuItem) instead.");
    }

    @Override // android.view.ActionProvider
    public View onCreateActionView(MenuItem item) {
        if (this.mMenuItem != null || this.mView != null) {
            Log.e(TAG, "onCreateActionView: this ActionProvider is already associated with a menu item. Don't reuse MediaRouteActionProvider instances! Abandoning the old one...");
        }
        this.mMenuItem = item;
        this.mView = new MediaRouteButton(this.mContext);
        this.mView.setCheatSheetEnabled(true);
        this.mView.setRouteTypes(this.mRouteTypes);
        this.mView.setExtendedSettingsClickListener(this.mExtendedSettingsListener);
        this.mView.setLayoutParams(new ViewGroup.LayoutParams(-2, -1));
        return this.mView;
    }

    @Override // android.view.ActionProvider
    public boolean onPerformDefaultAction() {
        FragmentManager fm = getActivity().getFragmentManager();
        if (((MediaRouteChooserDialogFragment) fm.findFragmentByTag(MediaRouteChooserDialogFragment.FRAGMENT_TAG)) != null) {
            Log.w(TAG, "onPerformDefaultAction(): Chooser dialog already showing!");
            return false;
        }
        MediaRouteChooserDialogFragment dialogFragment = new MediaRouteChooserDialogFragment();
        dialogFragment.setExtendedSettingsClickListener(this.mExtendedSettingsListener);
        dialogFragment.setRouteTypes(this.mRouteTypes);
        dialogFragment.show(fm, MediaRouteChooserDialogFragment.FRAGMENT_TAG);
        return true;
    }

    private Activity getActivity() {
        Context context;
        Context context2 = this.mContext;
        while (true) {
            context = context2;
            if (!(context instanceof ContextWrapper) || (context instanceof Activity)) {
                break;
            }
            context2 = ((ContextWrapper) context).getBaseContext();
        }
        if (!(context instanceof Activity)) {
            throw new IllegalStateException("The MediaRouteActionProvider's Context is not an Activity.");
        }
        return (Activity) context;
    }

    public void setExtendedSettingsClickListener(View.OnClickListener listener) {
        this.mExtendedSettingsListener = listener;
        if (this.mView != null) {
            this.mView.setExtendedSettingsClickListener(listener);
        }
    }

    @Override // android.view.ActionProvider
    public boolean overridesItemVisibility() {
        return true;
    }

    @Override // android.view.ActionProvider
    public boolean isVisible() {
        return this.mRouter.getRouteCount() > 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: MediaRouteActionProvider$RouterCallback.class */
    public static class RouterCallback extends MediaRouter.SimpleCallback {
        private WeakReference<MediaRouteActionProvider> mAp;

        RouterCallback(MediaRouteActionProvider ap) {
            this.mAp = new WeakReference<>(ap);
        }

        @Override // android.media.MediaRouter.SimpleCallback, android.media.MediaRouter.Callback
        public void onRouteAdded(MediaRouter router, MediaRouter.RouteInfo info) {
            MediaRouteActionProvider ap = this.mAp.get();
            if (ap == null) {
                router.removeCallback(this);
            } else {
                ap.refreshVisibility();
            }
        }

        @Override // android.media.MediaRouter.SimpleCallback, android.media.MediaRouter.Callback
        public void onRouteRemoved(MediaRouter router, MediaRouter.RouteInfo info) {
            MediaRouteActionProvider ap = this.mAp.get();
            if (ap == null) {
                router.removeCallback(this);
            } else {
                ap.refreshVisibility();
            }
        }
    }
}