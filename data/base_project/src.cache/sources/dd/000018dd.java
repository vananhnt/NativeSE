package android.widget;

import android.app.INotificationManager;
import android.app.ITransientNotification;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Handler;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import com.android.internal.R;

/* loaded from: Toast.class */
public class Toast {
    static final String TAG = "Toast";
    static final boolean localLOGV = false;
    public static final int LENGTH_SHORT = 0;
    public static final int LENGTH_LONG = 1;
    final Context mContext;
    final TN mTN = new TN();
    int mDuration;
    View mNextView;
    private static INotificationManager sService;

    public Toast(Context context) {
        this.mContext = context;
        this.mTN.mY = context.getResources().getDimensionPixelSize(R.dimen.toast_y_offset);
        this.mTN.mGravity = context.getResources().getInteger(R.integer.config_toastDefaultGravity);
    }

    public void show() {
        if (this.mNextView == null) {
            throw new RuntimeException("setView must have been called");
        }
        INotificationManager service = getService();
        String pkg = this.mContext.getPackageName();
        TN tn = this.mTN;
        tn.mNextView = this.mNextView;
        try {
            service.enqueueToast(pkg, tn, this.mDuration);
        } catch (RemoteException e) {
        }
    }

    public void cancel() {
        this.mTN.hide();
        try {
            getService().cancelToast(this.mContext.getPackageName(), this.mTN);
        } catch (RemoteException e) {
        }
    }

    public void setView(View view) {
        this.mNextView = view;
    }

    public View getView() {
        return this.mNextView;
    }

    public void setDuration(int duration) {
        this.mDuration = duration;
    }

    public int getDuration() {
        return this.mDuration;
    }

    public void setMargin(float horizontalMargin, float verticalMargin) {
        this.mTN.mHorizontalMargin = horizontalMargin;
        this.mTN.mVerticalMargin = verticalMargin;
    }

    public float getHorizontalMargin() {
        return this.mTN.mHorizontalMargin;
    }

    public float getVerticalMargin() {
        return this.mTN.mVerticalMargin;
    }

    public void setGravity(int gravity, int xOffset, int yOffset) {
        this.mTN.mGravity = gravity;
        this.mTN.mX = xOffset;
        this.mTN.mY = yOffset;
    }

    public int getGravity() {
        return this.mTN.mGravity;
    }

    public int getXOffset() {
        return this.mTN.mX;
    }

    public int getYOffset() {
        return this.mTN.mY;
    }

    public static Toast makeText(Context context, CharSequence text, int duration) {
        Toast result = new Toast(context);
        LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflate.inflate(R.layout.transient_notification, (ViewGroup) null);
        TextView tv = (TextView) v.findViewById(16908299);
        tv.setText(text);
        result.mNextView = v;
        result.mDuration = duration;
        return result;
    }

    public static Toast makeText(Context context, int resId, int duration) throws Resources.NotFoundException {
        return makeText(context, context.getResources().getText(resId), duration);
    }

    public void setText(int resId) {
        setText(this.mContext.getText(resId));
    }

    public void setText(CharSequence s) {
        if (this.mNextView == null) {
            throw new RuntimeException("This Toast was not created with Toast.makeText()");
        }
        TextView tv = (TextView) this.mNextView.findViewById(16908299);
        if (tv == null) {
            throw new RuntimeException("This Toast was not created with Toast.makeText()");
        }
        tv.setText(s);
    }

    private static INotificationManager getService() {
        if (sService != null) {
            return sService;
        }
        sService = INotificationManager.Stub.asInterface(ServiceManager.getService(Context.NOTIFICATION_SERVICE));
        return sService;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: Toast$TN.class */
    public static class TN extends ITransientNotification.Stub {
        final Runnable mShow = new Runnable() { // from class: android.widget.Toast.TN.1
            @Override // java.lang.Runnable
            public void run() {
                TN.this.handleShow();
            }
        };
        final Runnable mHide = new Runnable() { // from class: android.widget.Toast.TN.2
            @Override // java.lang.Runnable
            public void run() {
                TN.this.handleHide();
                TN.this.mNextView = null;
            }
        };
        private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
        final Handler mHandler = new Handler();
        int mGravity;
        int mX;
        int mY;
        float mHorizontalMargin;
        float mVerticalMargin;
        View mView;
        View mNextView;
        WindowManager mWM;

        TN() {
            WindowManager.LayoutParams params = this.mParams;
            params.height = -2;
            params.width = -2;
            params.format = -3;
            params.windowAnimations = 16973828;
            params.type = 2005;
            params.setTitle(Toast.TAG);
            params.flags = 152;
        }

        @Override // android.app.ITransientNotification
        public void show() {
            this.mHandler.post(this.mShow);
        }

        @Override // android.app.ITransientNotification
        public void hide() {
            this.mHandler.post(this.mHide);
        }

        public void handleShow() {
            if (this.mView != this.mNextView) {
                handleHide();
                this.mView = this.mNextView;
                Context context = this.mView.getContext().getApplicationContext();
                if (context == null) {
                    context = this.mView.getContext();
                }
                this.mWM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                Configuration config = this.mView.getContext().getResources().getConfiguration();
                int gravity = Gravity.getAbsoluteGravity(this.mGravity, config.getLayoutDirection());
                this.mParams.gravity = gravity;
                if ((gravity & 7) == 7) {
                    this.mParams.horizontalWeight = 1.0f;
                }
                if ((gravity & 112) == 112) {
                    this.mParams.verticalWeight = 1.0f;
                }
                this.mParams.x = this.mX;
                this.mParams.y = this.mY;
                this.mParams.verticalMargin = this.mVerticalMargin;
                this.mParams.horizontalMargin = this.mHorizontalMargin;
                if (this.mView.getParent() != null) {
                    this.mWM.removeView(this.mView);
                }
                this.mWM.addView(this.mView, this.mParams);
                trySendAccessibilityEvent();
            }
        }

        private void trySendAccessibilityEvent() {
            AccessibilityManager accessibilityManager = AccessibilityManager.getInstance(this.mView.getContext());
            if (!accessibilityManager.isEnabled()) {
                return;
            }
            AccessibilityEvent event = AccessibilityEvent.obtain(64);
            event.setClassName(getClass().getName());
            event.setPackageName(this.mView.getContext().getPackageName());
            this.mView.dispatchPopulateAccessibilityEvent(event);
            accessibilityManager.sendAccessibilityEvent(event);
        }

        public void handleHide() {
            if (this.mView != null) {
                if (this.mView.getParent() != null) {
                    this.mWM.removeView(this.mView);
                }
                this.mView = null;
            }
        }
    }
}