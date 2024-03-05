package android.app;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.MediaRouter;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.android.internal.R;
import com.android.internal.app.MediaRouteChooserDialogFragment;

/* loaded from: MediaRouteButton.class */
public class MediaRouteButton extends View {
    private static final String TAG = "MediaRouteButton";
    private MediaRouter mRouter;
    private final MediaRouteCallback mRouterCallback;
    private int mRouteTypes;
    private boolean mAttachedToWindow;
    private Drawable mRemoteIndicator;
    private boolean mRemoteActive;
    private boolean mToggleMode;
    private boolean mCheatSheetEnabled;
    private boolean mIsConnecting;
    private int mMinWidth;
    private int mMinHeight;
    private View.OnClickListener mExtendedSettingsClickListener;
    private MediaRouteChooserDialogFragment mDialogFragment;
    private static final int[] CHECKED_STATE_SET = {16842912};
    private static final int[] ACTIVATED_STATE_SET = {16843518};

    public MediaRouteButton(Context context) {
        this(context, null);
    }

    public MediaRouteButton(Context context, AttributeSet attrs) {
        this(context, attrs, 16843693);
    }

    public MediaRouteButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mRouterCallback = new MediaRouteCallback();
        this.mRouter = (MediaRouter) context.getSystemService(Context.MEDIA_ROUTER_SERVICE);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MediaRouteButton, defStyleAttr, 0);
        setRemoteIndicatorDrawable(a.getDrawable(3));
        this.mMinWidth = a.getDimensionPixelSize(0, 0);
        this.mMinHeight = a.getDimensionPixelSize(1, 0);
        int routeTypes = a.getInteger(2, 1);
        a.recycle();
        setClickable(true);
        setLongClickable(true);
        setRouteTypes(routeTypes);
    }

    private void setRemoteIndicatorDrawable(Drawable d) {
        if (this.mRemoteIndicator != null) {
            this.mRemoteIndicator.setCallback(null);
            unscheduleDrawable(this.mRemoteIndicator);
        }
        this.mRemoteIndicator = d;
        if (d != null) {
            d.setCallback(this);
            d.setState(getDrawableState());
            d.setVisible(getVisibility() == 0, false);
        }
        refreshDrawableState();
    }

    @Override // android.view.View
    public boolean performClick() {
        boolean handled = super.performClick();
        if (!handled) {
            playSoundEffect(0);
        }
        if (this.mToggleMode) {
            if (this.mRemoteActive) {
                this.mRouter.selectRouteInt(this.mRouteTypes, this.mRouter.getDefaultRoute());
            } else {
                int N = this.mRouter.getRouteCount();
                for (int i = 0; i < N; i++) {
                    MediaRouter.RouteInfo route = this.mRouter.getRouteAt(i);
                    if ((route.getSupportedTypes() & this.mRouteTypes) != 0 && route != this.mRouter.getDefaultRoute()) {
                        this.mRouter.selectRouteInt(this.mRouteTypes, route);
                    }
                }
            }
        } else {
            showDialog();
        }
        return handled;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setCheatSheetEnabled(boolean enable) {
        this.mCheatSheetEnabled = enable;
    }

    @Override // android.view.View
    public boolean performLongClick() {
        if (super.performLongClick()) {
            return true;
        }
        if (!this.mCheatSheetEnabled) {
            return false;
        }
        CharSequence contentDesc = getContentDescription();
        if (TextUtils.isEmpty(contentDesc)) {
            return false;
        }
        int[] screenPos = new int[2];
        Rect displayFrame = new Rect();
        getLocationOnScreen(screenPos);
        getWindowVisibleDisplayFrame(displayFrame);
        Context context = getContext();
        int width = getWidth();
        int height = getHeight();
        int midy = screenPos[1] + (height / 2);
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        Toast cheatSheet = Toast.makeText(context, contentDesc, 0);
        if (midy < displayFrame.height()) {
            cheatSheet.setGravity(8388661, (screenWidth - screenPos[0]) - (width / 2), height);
        } else {
            cheatSheet.setGravity(81, 0, height);
        }
        cheatSheet.show();
        performHapticFeedback(0);
        return true;
    }

    public void setRouteTypes(int types) {
        if (types == this.mRouteTypes) {
            return;
        }
        if (this.mAttachedToWindow && this.mRouteTypes != 0) {
            this.mRouter.removeCallback(this.mRouterCallback);
        }
        this.mRouteTypes = types;
        if (this.mAttachedToWindow) {
            updateRouteInfo();
            this.mRouter.addCallback(types, this.mRouterCallback);
        }
    }

    private void updateRouteInfo() {
        updateRemoteIndicator();
        updateRouteCount();
    }

    public int getRouteTypes() {
        return this.mRouteTypes;
    }

    void updateRemoteIndicator() {
        MediaRouter.RouteInfo selected = this.mRouter.getSelectedRoute(this.mRouteTypes);
        boolean isRemote = selected != this.mRouter.getDefaultRoute();
        boolean isConnecting = selected != null && selected.getStatusCode() == 2;
        boolean needsRefresh = false;
        if (this.mRemoteActive != isRemote) {
            this.mRemoteActive = isRemote;
            needsRefresh = true;
        }
        if (this.mIsConnecting != isConnecting) {
            this.mIsConnecting = isConnecting;
            needsRefresh = true;
        }
        if (needsRefresh) {
            refreshDrawableState();
        }
    }

    void updateRouteCount() {
        int N = this.mRouter.getRouteCount();
        int count = 0;
        boolean hasVideoRoutes = false;
        for (int i = 0; i < N; i++) {
            MediaRouter.RouteInfo route = this.mRouter.getRouteAt(i);
            int routeTypes = route.getSupportedTypes();
            if ((routeTypes & this.mRouteTypes) != 0) {
                if (route instanceof MediaRouter.RouteGroup) {
                    count += ((MediaRouter.RouteGroup) route).getRouteCount();
                } else {
                    count++;
                }
                if ((routeTypes & 2) != 0) {
                    hasVideoRoutes = true;
                }
            }
        }
        setEnabled(count != 0);
        this.mToggleMode = (count != 2 || (this.mRouteTypes & 1) == 0 || hasVideoRoutes) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public int[] onCreateDrawableState(int extraSpace) {
        int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (this.mIsConnecting) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        } else if (this.mRemoteActive) {
            mergeDrawableStates(drawableState, ACTIVATED_STATE_SET);
        }
        return drawableState;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void drawableStateChanged() {
        super.drawableStateChanged();
        if (this.mRemoteIndicator != null) {
            int[] myDrawableState = getDrawableState();
            this.mRemoteIndicator.setState(myDrawableState);
            invalidate();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || who == this.mRemoteIndicator;
    }

    @Override // android.view.View
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        if (this.mRemoteIndicator != null) {
            this.mRemoteIndicator.jumpToCurrentState();
        }
    }

    @Override // android.view.View
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (this.mRemoteIndicator != null) {
            this.mRemoteIndicator.setVisible(getVisibility() == 0, false);
        }
    }

    @Override // android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mAttachedToWindow = true;
        if (this.mRouteTypes != 0) {
            this.mRouter.addCallback(this.mRouteTypes, this.mRouterCallback);
            updateRouteInfo();
        }
    }

    @Override // android.view.View
    public void onDetachedFromWindow() {
        if (this.mRouteTypes != 0) {
            this.mRouter.removeCallback(this.mRouterCallback);
        }
        this.mAttachedToWindow = false;
        super.onDetachedFromWindow();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        int height;
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int minWidth = Math.max(this.mMinWidth, this.mRemoteIndicator != null ? this.mRemoteIndicator.getIntrinsicWidth() : 0);
        int minHeight = Math.max(this.mMinHeight, this.mRemoteIndicator != null ? this.mRemoteIndicator.getIntrinsicHeight() : 0);
        switch (widthMode) {
            case Integer.MIN_VALUE:
                width = Math.min(widthSize, minWidth + getPaddingLeft() + getPaddingRight());
                break;
            case 0:
            default:
                width = minWidth + getPaddingLeft() + getPaddingRight();
                break;
            case 1073741824:
                width = widthSize;
                break;
        }
        switch (heightMode) {
            case Integer.MIN_VALUE:
                height = Math.min(heightSize, minHeight + getPaddingTop() + getPaddingBottom());
                break;
            case 0:
            default:
                height = minHeight + getPaddingTop() + getPaddingBottom();
                break;
            case 1073741824:
                height = heightSize;
                break;
        }
        setMeasuredDimension(width, height);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mRemoteIndicator == null) {
            return;
        }
        int left = getPaddingLeft();
        int right = getWidth() - getPaddingRight();
        int top = getPaddingTop();
        int bottom = getHeight() - getPaddingBottom();
        int drawWidth = this.mRemoteIndicator.getIntrinsicWidth();
        int drawHeight = this.mRemoteIndicator.getIntrinsicHeight();
        int drawLeft = left + (((right - left) - drawWidth) / 2);
        int drawTop = top + (((bottom - top) - drawHeight) / 2);
        this.mRemoteIndicator.setBounds(drawLeft, drawTop, drawLeft + drawWidth, drawTop + drawHeight);
        this.mRemoteIndicator.draw(canvas);
    }

    public void setExtendedSettingsClickListener(View.OnClickListener listener) {
        this.mExtendedSettingsClickListener = listener;
        if (this.mDialogFragment != null) {
            this.mDialogFragment.setExtendedSettingsClickListener(listener);
        }
    }

    public void showDialog() {
        FragmentManager fm = getActivity().getFragmentManager();
        if (this.mDialogFragment == null) {
            this.mDialogFragment = (MediaRouteChooserDialogFragment) fm.findFragmentByTag(MediaRouteChooserDialogFragment.FRAGMENT_TAG);
        }
        if (this.mDialogFragment != null) {
            Log.w(TAG, "showDialog(): Already showing!");
            return;
        }
        this.mDialogFragment = new MediaRouteChooserDialogFragment();
        this.mDialogFragment.setExtendedSettingsClickListener(this.mExtendedSettingsClickListener);
        this.mDialogFragment.setLauncherListener(new MediaRouteChooserDialogFragment.LauncherListener() { // from class: android.app.MediaRouteButton.1
            @Override // com.android.internal.app.MediaRouteChooserDialogFragment.LauncherListener
            public void onDetached(MediaRouteChooserDialogFragment detachedFragment) {
                MediaRouteButton.this.mDialogFragment = null;
            }
        });
        this.mDialogFragment.setRouteTypes(this.mRouteTypes);
        this.mDialogFragment.show(fm, MediaRouteChooserDialogFragment.FRAGMENT_TAG);
    }

    private Activity getActivity() {
        Context context;
        Context context2 = getContext();
        while (true) {
            context = context2;
            if (!(context instanceof ContextWrapper) || (context instanceof Activity)) {
                break;
            }
            context2 = ((ContextWrapper) context).getBaseContext();
        }
        if (!(context instanceof Activity)) {
            throw new IllegalStateException("The MediaRouteButton's Context is not an Activity.");
        }
        return (Activity) context;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: MediaRouteButton$MediaRouteCallback.class */
    public class MediaRouteCallback extends MediaRouter.SimpleCallback {
        private MediaRouteCallback() {
        }

        @Override // android.media.MediaRouter.SimpleCallback, android.media.MediaRouter.Callback
        public void onRouteSelected(MediaRouter router, int type, MediaRouter.RouteInfo info) {
            MediaRouteButton.this.updateRemoteIndicator();
        }

        @Override // android.media.MediaRouter.SimpleCallback, android.media.MediaRouter.Callback
        public void onRouteUnselected(MediaRouter router, int type, MediaRouter.RouteInfo info) {
            MediaRouteButton.this.updateRemoteIndicator();
        }

        @Override // android.media.MediaRouter.SimpleCallback, android.media.MediaRouter.Callback
        public void onRouteChanged(MediaRouter router, MediaRouter.RouteInfo info) {
            MediaRouteButton.this.updateRemoteIndicator();
        }

        @Override // android.media.MediaRouter.SimpleCallback, android.media.MediaRouter.Callback
        public void onRouteAdded(MediaRouter router, MediaRouter.RouteInfo info) {
            MediaRouteButton.this.updateRouteCount();
        }

        @Override // android.media.MediaRouter.SimpleCallback, android.media.MediaRouter.Callback
        public void onRouteRemoved(MediaRouter router, MediaRouter.RouteInfo info) {
            MediaRouteButton.this.updateRouteCount();
        }

        @Override // android.media.MediaRouter.SimpleCallback, android.media.MediaRouter.Callback
        public void onRouteGrouped(MediaRouter router, MediaRouter.RouteInfo info, MediaRouter.RouteGroup group, int index) {
            MediaRouteButton.this.updateRouteCount();
        }

        @Override // android.media.MediaRouter.SimpleCallback, android.media.MediaRouter.Callback
        public void onRouteUngrouped(MediaRouter router, MediaRouter.RouteInfo info, MediaRouter.RouteGroup group) {
            MediaRouteButton.this.updateRouteCount();
        }
    }
}