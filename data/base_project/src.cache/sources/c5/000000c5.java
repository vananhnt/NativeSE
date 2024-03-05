package android.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IRemoteCallback;
import android.os.RemoteException;
import android.view.View;

/* loaded from: ActivityOptions.class */
public class ActivityOptions {
    public static final String KEY_PACKAGE_NAME = "android:packageName";
    public static final String KEY_ANIM_TYPE = "android:animType";
    public static final String KEY_ANIM_ENTER_RES_ID = "android:animEnterRes";
    public static final String KEY_ANIM_EXIT_RES_ID = "android:animExitRes";
    public static final String KEY_ANIM_THUMBNAIL = "android:animThumbnail";
    public static final String KEY_ANIM_START_X = "android:animStartX";
    public static final String KEY_ANIM_START_Y = "android:animStartY";
    public static final String KEY_ANIM_START_WIDTH = "android:animStartWidth";
    public static final String KEY_ANIM_START_HEIGHT = "android:animStartHeight";
    public static final String KEY_ANIM_START_LISTENER = "android:animStartListener";
    public static final int ANIM_NONE = 0;
    public static final int ANIM_CUSTOM = 1;
    public static final int ANIM_SCALE_UP = 2;
    public static final int ANIM_THUMBNAIL_SCALE_UP = 3;
    public static final int ANIM_THUMBNAIL_SCALE_DOWN = 4;
    private String mPackageName;
    private int mAnimationType;
    private int mCustomEnterResId;
    private int mCustomExitResId;
    private Bitmap mThumbnail;
    private int mStartX;
    private int mStartY;
    private int mStartWidth;
    private int mStartHeight;
    private IRemoteCallback mAnimationStartedListener;

    /* loaded from: ActivityOptions$OnAnimationStartedListener.class */
    public interface OnAnimationStartedListener {
        void onAnimationStarted();
    }

    public static ActivityOptions makeCustomAnimation(Context context, int enterResId, int exitResId) {
        return makeCustomAnimation(context, enterResId, exitResId, null, null);
    }

    public static ActivityOptions makeCustomAnimation(Context context, int enterResId, int exitResId, Handler handler, OnAnimationStartedListener listener) {
        ActivityOptions opts = new ActivityOptions();
        opts.mPackageName = context.getPackageName();
        opts.mAnimationType = 1;
        opts.mCustomEnterResId = enterResId;
        opts.mCustomExitResId = exitResId;
        opts.setListener(handler, listener);
        return opts;
    }

    private void setListener(final Handler handler, final OnAnimationStartedListener listener) {
        if (listener != null) {
            this.mAnimationStartedListener = new IRemoteCallback.Stub() { // from class: android.app.ActivityOptions.1
                @Override // android.os.IRemoteCallback
                public void sendResult(Bundle data) throws RemoteException {
                    handler.post(new Runnable() { // from class: android.app.ActivityOptions.1.1
                        @Override // java.lang.Runnable
                        public void run() {
                            listener.onAnimationStarted();
                        }
                    });
                }
            };
        }
    }

    public static ActivityOptions makeScaleUpAnimation(View source, int startX, int startY, int startWidth, int startHeight) {
        ActivityOptions opts = new ActivityOptions();
        opts.mPackageName = source.getContext().getPackageName();
        opts.mAnimationType = 2;
        int[] pts = new int[2];
        source.getLocationOnScreen(pts);
        opts.mStartX = pts[0] + startX;
        opts.mStartY = pts[1] + startY;
        opts.mStartWidth = startWidth;
        opts.mStartHeight = startHeight;
        return opts;
    }

    public static ActivityOptions makeThumbnailScaleUpAnimation(View source, Bitmap thumbnail, int startX, int startY) {
        return makeThumbnailScaleUpAnimation(source, thumbnail, startX, startY, null);
    }

    public static ActivityOptions makeThumbnailScaleUpAnimation(View source, Bitmap thumbnail, int startX, int startY, OnAnimationStartedListener listener) {
        return makeThumbnailAnimation(source, thumbnail, startX, startY, listener, true);
    }

    public static ActivityOptions makeThumbnailScaleDownAnimation(View source, Bitmap thumbnail, int startX, int startY, OnAnimationStartedListener listener) {
        return makeThumbnailAnimation(source, thumbnail, startX, startY, listener, false);
    }

    private static ActivityOptions makeThumbnailAnimation(View source, Bitmap thumbnail, int startX, int startY, OnAnimationStartedListener listener, boolean scaleUp) {
        ActivityOptions opts = new ActivityOptions();
        opts.mPackageName = source.getContext().getPackageName();
        opts.mAnimationType = scaleUp ? 3 : 4;
        opts.mThumbnail = thumbnail;
        int[] pts = new int[2];
        source.getLocationOnScreen(pts);
        opts.mStartX = pts[0] + startX;
        opts.mStartY = pts[1] + startY;
        opts.setListener(source.getHandler(), listener);
        return opts;
    }

    private ActivityOptions() {
        this.mAnimationType = 0;
    }

    public ActivityOptions(Bundle opts) {
        this.mAnimationType = 0;
        this.mPackageName = opts.getString(KEY_PACKAGE_NAME);
        this.mAnimationType = opts.getInt(KEY_ANIM_TYPE);
        if (this.mAnimationType == 1) {
            this.mCustomEnterResId = opts.getInt(KEY_ANIM_ENTER_RES_ID, 0);
            this.mCustomExitResId = opts.getInt(KEY_ANIM_EXIT_RES_ID, 0);
            this.mAnimationStartedListener = IRemoteCallback.Stub.asInterface(opts.getIBinder(KEY_ANIM_START_LISTENER));
        } else if (this.mAnimationType == 2) {
            this.mStartX = opts.getInt(KEY_ANIM_START_X, 0);
            this.mStartY = opts.getInt(KEY_ANIM_START_Y, 0);
            this.mStartWidth = opts.getInt(KEY_ANIM_START_WIDTH, 0);
            this.mStartHeight = opts.getInt(KEY_ANIM_START_HEIGHT, 0);
        } else if (this.mAnimationType == 3 || this.mAnimationType == 4) {
            this.mThumbnail = (Bitmap) opts.getParcelable(KEY_ANIM_THUMBNAIL);
            this.mStartX = opts.getInt(KEY_ANIM_START_X, 0);
            this.mStartY = opts.getInt(KEY_ANIM_START_Y, 0);
            this.mAnimationStartedListener = IRemoteCallback.Stub.asInterface(opts.getIBinder(KEY_ANIM_START_LISTENER));
        }
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public int getAnimationType() {
        return this.mAnimationType;
    }

    public int getCustomEnterResId() {
        return this.mCustomEnterResId;
    }

    public int getCustomExitResId() {
        return this.mCustomExitResId;
    }

    public Bitmap getThumbnail() {
        return this.mThumbnail;
    }

    public int getStartX() {
        return this.mStartX;
    }

    public int getStartY() {
        return this.mStartY;
    }

    public int getStartWidth() {
        return this.mStartWidth;
    }

    public int getStartHeight() {
        return this.mStartHeight;
    }

    public IRemoteCallback getOnAnimationStartListener() {
        return this.mAnimationStartedListener;
    }

    public void abort() {
        if (this.mAnimationStartedListener != null) {
            try {
                this.mAnimationStartedListener.sendResult(null);
            } catch (RemoteException e) {
            }
        }
    }

    public static void abort(Bundle options) {
        if (options != null) {
            new ActivityOptions(options).abort();
        }
    }

    public void update(ActivityOptions otherOptions) {
        if (otherOptions.mPackageName != null) {
            this.mPackageName = otherOptions.mPackageName;
        }
        switch (otherOptions.mAnimationType) {
            case 1:
                this.mAnimationType = otherOptions.mAnimationType;
                this.mCustomEnterResId = otherOptions.mCustomEnterResId;
                this.mCustomExitResId = otherOptions.mCustomExitResId;
                this.mThumbnail = null;
                if (otherOptions.mAnimationStartedListener != null) {
                    try {
                        otherOptions.mAnimationStartedListener.sendResult(null);
                    } catch (RemoteException e) {
                    }
                }
                this.mAnimationStartedListener = otherOptions.mAnimationStartedListener;
                return;
            case 2:
                this.mAnimationType = otherOptions.mAnimationType;
                this.mStartX = otherOptions.mStartX;
                this.mStartY = otherOptions.mStartY;
                this.mStartWidth = otherOptions.mStartWidth;
                this.mStartHeight = otherOptions.mStartHeight;
                if (otherOptions.mAnimationStartedListener != null) {
                    try {
                        otherOptions.mAnimationStartedListener.sendResult(null);
                    } catch (RemoteException e2) {
                    }
                }
                this.mAnimationStartedListener = null;
                return;
            case 3:
            case 4:
                this.mAnimationType = otherOptions.mAnimationType;
                this.mThumbnail = otherOptions.mThumbnail;
                this.mStartX = otherOptions.mStartX;
                this.mStartY = otherOptions.mStartY;
                if (otherOptions.mAnimationStartedListener != null) {
                    try {
                        otherOptions.mAnimationStartedListener.sendResult(null);
                    } catch (RemoteException e3) {
                    }
                }
                this.mAnimationStartedListener = otherOptions.mAnimationStartedListener;
                return;
            default:
                return;
        }
    }

    public Bundle toBundle() {
        Bundle b = new Bundle();
        if (this.mPackageName != null) {
            b.putString(KEY_PACKAGE_NAME, this.mPackageName);
        }
        switch (this.mAnimationType) {
            case 1:
                b.putInt(KEY_ANIM_TYPE, this.mAnimationType);
                b.putInt(KEY_ANIM_ENTER_RES_ID, this.mCustomEnterResId);
                b.putInt(KEY_ANIM_EXIT_RES_ID, this.mCustomExitResId);
                b.putIBinder(KEY_ANIM_START_LISTENER, this.mAnimationStartedListener != null ? this.mAnimationStartedListener.asBinder() : null);
                break;
            case 2:
                b.putInt(KEY_ANIM_TYPE, this.mAnimationType);
                b.putInt(KEY_ANIM_START_X, this.mStartX);
                b.putInt(KEY_ANIM_START_Y, this.mStartY);
                b.putInt(KEY_ANIM_START_WIDTH, this.mStartWidth);
                b.putInt(KEY_ANIM_START_HEIGHT, this.mStartHeight);
                break;
            case 3:
            case 4:
                b.putInt(KEY_ANIM_TYPE, this.mAnimationType);
                b.putParcelable(KEY_ANIM_THUMBNAIL, this.mThumbnail);
                b.putInt(KEY_ANIM_START_X, this.mStartX);
                b.putInt(KEY_ANIM_START_Y, this.mStartY);
                b.putIBinder(KEY_ANIM_START_LISTENER, this.mAnimationStartedListener != null ? this.mAnimationStartedListener.asBinder() : null);
                break;
        }
        return b;
    }
}