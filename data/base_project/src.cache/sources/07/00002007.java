package com.android.server.wm;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Handler;
import android.os.IRemoteCallback;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import com.android.internal.R;
import com.android.internal.util.DumpUtils;
import com.android.server.AttributeCache;
import gov.nist.core.Separators;
import java.io.PrintWriter;

/* loaded from: AppTransition.class */
public class AppTransition implements DumpUtils.Dump {
    private static final String TAG = "AppTransition";
    private static final boolean DEBUG_APP_TRANSITIONS = false;
    private static final boolean DEBUG_ANIM = false;
    public static final int TRANSIT_ENTER_MASK = 4096;
    public static final int TRANSIT_EXIT_MASK = 8192;
    public static final int TRANSIT_UNSET = -1;
    public static final int TRANSIT_NONE = 0;
    public static final int TRANSIT_ACTIVITY_OPEN = 4102;
    public static final int TRANSIT_ACTIVITY_CLOSE = 8199;
    public static final int TRANSIT_TASK_OPEN = 4104;
    public static final int TRANSIT_TASK_CLOSE = 8201;
    public static final int TRANSIT_TASK_TO_FRONT = 4106;
    public static final int TRANSIT_TASK_TO_BACK = 8203;
    public static final int TRANSIT_WALLPAPER_CLOSE = 8204;
    public static final int TRANSIT_WALLPAPER_OPEN = 4109;
    public static final int TRANSIT_WALLPAPER_INTRA_OPEN = 4110;
    public static final int TRANSIT_WALLPAPER_INTRA_CLOSE = 8207;
    private static final float RECENTS_THUMBNAIL_FADEOUT_FRACTION = 0.25f;
    private static final long DEFAULT_APP_TRANSITION_DURATION = 250;
    private final Context mContext;
    private final Handler mH;
    private static final int NEXT_TRANSIT_TYPE_NONE = 0;
    private static final int NEXT_TRANSIT_TYPE_CUSTOM = 1;
    private static final int NEXT_TRANSIT_TYPE_SCALE_UP = 2;
    private static final int NEXT_TRANSIT_TYPE_THUMBNAIL_SCALE_UP = 3;
    private static final int NEXT_TRANSIT_TYPE_THUMBNAIL_SCALE_DOWN = 4;
    private String mNextAppTransitionPackage;
    private Bitmap mNextAppTransitionThumbnail;
    private boolean mNextAppTransitionScaleUp;
    private IRemoteCallback mNextAppTransitionCallback;
    private int mNextAppTransitionEnter;
    private int mNextAppTransitionExit;
    private int mNextAppTransitionStartX;
    private int mNextAppTransitionStartY;
    private int mNextAppTransitionStartWidth;
    private int mNextAppTransitionStartHeight;
    private static final int APP_STATE_IDLE = 0;
    private static final int APP_STATE_READY = 1;
    private static final int APP_STATE_RUNNING = 2;
    private static final int APP_STATE_TIMEOUT = 3;
    private final int mConfigShortAnimTime;
    private final Interpolator mDecelerateInterpolator;
    private int mNextAppTransition = -1;
    private int mNextAppTransitionType = 0;
    private int mAppTransitionState = 0;
    private int mCurrentUserId = 0;
    private final Interpolator mThumbnailFadeoutInterpolator = new Interpolator() { // from class: com.android.server.wm.AppTransition.1
        @Override // android.animation.TimeInterpolator
        public float getInterpolation(float input) {
            if (input < 0.25f) {
                return input / 0.25f;
            }
            return 1.0f;
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    public AppTransition(Context context, Handler h) {
        this.mContext = context;
        this.mH = h;
        this.mConfigShortAnimTime = context.getResources().getInteger(17694720);
        this.mDecelerateInterpolator = AnimationUtils.loadInterpolator(context, 17563651);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isTransitionSet() {
        return this.mNextAppTransition != -1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isTransitionNone() {
        return this.mNextAppTransition == 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isTransitionEqual(int transit) {
        return this.mNextAppTransition == transit;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getAppTransition() {
        return this.mNextAppTransition;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setAppTransition(int transit) {
        this.mNextAppTransition = transit;
    }

    boolean isReady() {
        return this.mAppTransitionState == 1 || this.mAppTransitionState == 3;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setReady() {
        this.mAppTransitionState = 1;
    }

    boolean isRunning() {
        return this.mAppTransitionState == 2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setIdle() {
        this.mAppTransitionState = 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isTimeout() {
        return this.mAppTransitionState == 3;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setTimeout() {
        this.mAppTransitionState = 3;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Bitmap getNextAppTransitionThumbnail() {
        return this.mNextAppTransitionThumbnail;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void getStartingPoint(Point outPoint) {
        outPoint.x = this.mNextAppTransitionStartX;
        outPoint.y = this.mNextAppTransitionStartY;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void prepare() {
        if (!isRunning()) {
            this.mAppTransitionState = 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void goodToGo() {
        this.mNextAppTransition = -1;
        this.mAppTransitionState = 2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void clear() {
        this.mNextAppTransitionType = 0;
        this.mNextAppTransitionPackage = null;
        this.mNextAppTransitionThumbnail = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void freeze() {
        setAppTransition(-1);
        clear();
        setReady();
    }

    private AttributeCache.Entry getCachedAnimations(WindowManager.LayoutParams lp) {
        if (lp != null && lp.windowAnimations != 0) {
            String packageName = lp.packageName != null ? lp.packageName : "android";
            int resId = lp.windowAnimations;
            if ((resId & (-16777216)) == 16777216) {
                packageName = "android";
            }
            return AttributeCache.instance().get(packageName, resId, R.styleable.WindowAnimation, this.mCurrentUserId);
        }
        return null;
    }

    private AttributeCache.Entry getCachedAnimations(String packageName, int resId) {
        if (packageName != null) {
            if ((resId & (-16777216)) == 16777216) {
                packageName = "android";
            }
            return AttributeCache.instance().get(packageName, resId, R.styleable.WindowAnimation, this.mCurrentUserId);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Animation loadAnimation(WindowManager.LayoutParams lp, int animAttr) {
        AttributeCache.Entry ent;
        int anim = 0;
        Context context = this.mContext;
        if (animAttr >= 0 && (ent = getCachedAnimations(lp)) != null) {
            context = ent.context;
            anim = ent.array.getResourceId(animAttr, 0);
        }
        if (anim != 0) {
            return AnimationUtils.loadAnimation(context, anim);
        }
        return null;
    }

    private Animation loadAnimation(String packageName, int resId) {
        AttributeCache.Entry ent;
        int anim = 0;
        Context context = this.mContext;
        if (resId >= 0 && (ent = getCachedAnimations(packageName, resId)) != null) {
            context = ent.context;
            anim = resId;
        }
        if (anim != 0) {
            return AnimationUtils.loadAnimation(context, anim);
        }
        return null;
    }

    private static float computePivot(int startPos, float finalScale) {
        float denom = finalScale - 1.0f;
        if (Math.abs(denom) < 1.0E-4f) {
            return startPos;
        }
        return (-startPos) / denom;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private Animation createScaleUpAnimationLocked(int transit, boolean enter, int appWidth, int appHeight) {
        Animation a;
        long duration;
        if (enter) {
            float scaleW = this.mNextAppTransitionStartWidth / appWidth;
            float scaleH = this.mNextAppTransitionStartHeight / appHeight;
            Animation scale = new ScaleAnimation(scaleW, 1.0f, scaleH, 1.0f, computePivot(this.mNextAppTransitionStartX, scaleW), computePivot(this.mNextAppTransitionStartY, scaleH));
            scale.setInterpolator(this.mDecelerateInterpolator);
            Animation alpha = new AlphaAnimation(0.0f, 1.0f);
            alpha.setInterpolator(this.mThumbnailFadeoutInterpolator);
            AnimationSet set = new AnimationSet(false);
            set.addAnimation(scale);
            set.addAnimation(alpha);
            set.setDetachWallpaper(true);
            a = set;
        } else if (transit == 4110 || transit == 8207) {
            a = new AlphaAnimation(1.0f, 0.0f);
            a.setDetachWallpaper(true);
        } else {
            a = new AlphaAnimation(1.0f, 1.0f);
        }
        switch (transit) {
            case 4102:
            case 8199:
                duration = this.mConfigShortAnimTime;
                break;
            default:
                duration = 250;
                break;
        }
        a.setDuration(duration);
        a.setFillAfter(true);
        a.setInterpolator(this.mDecelerateInterpolator);
        a.initialize(appWidth, appHeight, appWidth, appHeight);
        return a;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    public Animation createThumbnailAnimationLocked(int transit, boolean enter, boolean thumb, int appWidth, int appHeight) {
        Animation a;
        long duration;
        int thumbWidthI = this.mNextAppTransitionThumbnail.getWidth();
        float thumbWidth = thumbWidthI > 0 ? thumbWidthI : 1.0f;
        int thumbHeightI = this.mNextAppTransitionThumbnail.getHeight();
        float thumbHeight = thumbHeightI > 0 ? thumbHeightI : 1.0f;
        if (thumb) {
            if (this.mNextAppTransitionScaleUp) {
                float scaleW = appWidth / thumbWidth;
                float scaleH = appHeight / thumbHeight;
                Animation scale = new ScaleAnimation(1.0f, scaleW, 1.0f, scaleH, computePivot(this.mNextAppTransitionStartX, 1.0f / scaleW), computePivot(this.mNextAppTransitionStartY, 1.0f / scaleH));
                scale.setInterpolator(this.mDecelerateInterpolator);
                Animation alpha = new AlphaAnimation(1.0f, 0.0f);
                alpha.setInterpolator(this.mThumbnailFadeoutInterpolator);
                AnimationSet set = new AnimationSet(false);
                set.addAnimation(scale);
                set.addAnimation(alpha);
                a = set;
            } else {
                float scaleW2 = appWidth / thumbWidth;
                float scaleH2 = appHeight / thumbHeight;
                a = new ScaleAnimation(scaleW2, 1.0f, scaleH2, 1.0f, computePivot(this.mNextAppTransitionStartX, 1.0f / scaleW2), computePivot(this.mNextAppTransitionStartY, 1.0f / scaleH2));
            }
        } else if (enter) {
            if (this.mNextAppTransitionScaleUp) {
                float scaleW3 = thumbWidth / appWidth;
                float scaleH3 = thumbHeight / appHeight;
                a = new ScaleAnimation(scaleW3, 1.0f, scaleH3, 1.0f, computePivot(this.mNextAppTransitionStartX, scaleW3), computePivot(this.mNextAppTransitionStartY, scaleH3));
            } else {
                a = new AlphaAnimation(1.0f, 1.0f);
            }
        } else if (this.mNextAppTransitionScaleUp) {
            if (transit == 4110) {
                a = new AlphaAnimation(1.0f, 0.0f);
            } else {
                a = new AlphaAnimation(1.0f, 1.0f);
            }
        } else {
            float scaleW4 = thumbWidth / appWidth;
            float scaleH4 = thumbHeight / appHeight;
            Animation scale2 = new ScaleAnimation(1.0f, scaleW4, 1.0f, scaleH4, computePivot(this.mNextAppTransitionStartX, scaleW4), computePivot(this.mNextAppTransitionStartY, scaleH4));
            Animation alpha2 = new AlphaAnimation(1.0f, 0.0f);
            AnimationSet set2 = new AnimationSet(true);
            set2.addAnimation(scale2);
            set2.addAnimation(alpha2);
            set2.setZAdjustment(1);
            a = set2;
        }
        switch (transit) {
            case 4102:
            case 8199:
                duration = this.mConfigShortAnimTime;
                break;
            default:
                duration = 250;
                break;
        }
        a.setDuration(duration);
        a.setFillAfter(true);
        a.setInterpolator(this.mDecelerateInterpolator);
        a.initialize(appWidth, appHeight, appWidth, appHeight);
        return a;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Animation loadAnimation(WindowManager.LayoutParams lp, int transit, boolean enter, int appWidth, int appHeight) {
        Animation a;
        if (this.mNextAppTransitionType == 1) {
            a = loadAnimation(this.mNextAppTransitionPackage, enter ? this.mNextAppTransitionEnter : this.mNextAppTransitionExit);
        } else if (this.mNextAppTransitionType == 2) {
            a = createScaleUpAnimationLocked(transit, enter, appWidth, appHeight);
        } else if (this.mNextAppTransitionType == 3 || this.mNextAppTransitionType == 4) {
            this.mNextAppTransitionScaleUp = this.mNextAppTransitionType == 3;
            a = createThumbnailAnimationLocked(transit, enter, false, appWidth, appHeight);
        } else {
            int animAttr = 0;
            switch (transit) {
                case 4102:
                    animAttr = enter ? 4 : 5;
                    break;
                case TRANSIT_TASK_OPEN /* 4104 */:
                    animAttr = enter ? 8 : 9;
                    break;
                case TRANSIT_TASK_TO_FRONT /* 4106 */:
                    animAttr = enter ? 12 : 13;
                    break;
                case TRANSIT_WALLPAPER_OPEN /* 4109 */:
                    animAttr = enter ? 16 : 17;
                    break;
                case TRANSIT_WALLPAPER_INTRA_OPEN /* 4110 */:
                    animAttr = enter ? 20 : 21;
                    break;
                case 8199:
                    animAttr = enter ? 6 : 7;
                    break;
                case 8201:
                    animAttr = enter ? 10 : 11;
                    break;
                case 8203:
                    animAttr = enter ? 14 : 15;
                    break;
                case 8204:
                    animAttr = enter ? 18 : 19;
                    break;
                case 8207:
                    animAttr = enter ? 22 : 23;
                    break;
            }
            a = animAttr != 0 ? loadAnimation(lp, animAttr) : null;
        }
        return a;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void postAnimationCallback() {
        if (this.mNextAppTransitionCallback != null) {
            this.mH.sendMessage(this.mH.obtainMessage(26, this.mNextAppTransitionCallback));
            this.mNextAppTransitionCallback = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void overridePendingAppTransition(String packageName, int enterAnim, int exitAnim, IRemoteCallback startedCallback) {
        if (isTransitionSet()) {
            this.mNextAppTransitionType = 1;
            this.mNextAppTransitionPackage = packageName;
            this.mNextAppTransitionThumbnail = null;
            this.mNextAppTransitionEnter = enterAnim;
            this.mNextAppTransitionExit = exitAnim;
            postAnimationCallback();
            this.mNextAppTransitionCallback = startedCallback;
            return;
        }
        postAnimationCallback();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void overridePendingAppTransitionScaleUp(int startX, int startY, int startWidth, int startHeight) {
        if (isTransitionSet()) {
            this.mNextAppTransitionType = 2;
            this.mNextAppTransitionPackage = null;
            this.mNextAppTransitionThumbnail = null;
            this.mNextAppTransitionStartX = startX;
            this.mNextAppTransitionStartY = startY;
            this.mNextAppTransitionStartWidth = startWidth;
            this.mNextAppTransitionStartHeight = startHeight;
            postAnimationCallback();
            this.mNextAppTransitionCallback = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void overridePendingAppTransitionThumb(Bitmap srcThumb, int startX, int startY, IRemoteCallback startedCallback, boolean scaleUp) {
        if (isTransitionSet()) {
            this.mNextAppTransitionType = scaleUp ? 3 : 4;
            this.mNextAppTransitionPackage = null;
            this.mNextAppTransitionThumbnail = srcThumb;
            this.mNextAppTransitionScaleUp = scaleUp;
            this.mNextAppTransitionStartX = startX;
            this.mNextAppTransitionStartY = startY;
            postAnimationCallback();
            this.mNextAppTransitionCallback = startedCallback;
            return;
        }
        postAnimationCallback();
    }

    public String toString() {
        return "mNextAppTransition=0x" + Integer.toHexString(this.mNextAppTransition);
    }

    public static String appTransitionToString(int transition) {
        switch (transition) {
            case -1:
                return "TRANSIT_UNSET";
            case 0:
                return "TRANSIT_NONE";
            case 4102:
                return "TRANSIT_ACTIVITY_OPEN";
            case TRANSIT_TASK_OPEN /* 4104 */:
                return "TRANSIT_TASK_OPEN";
            case TRANSIT_TASK_TO_FRONT /* 4106 */:
                return "TRANSIT_TASK_TO_FRONT";
            case TRANSIT_WALLPAPER_OPEN /* 4109 */:
                return "TRANSIT_WALLPAPER_OPEN";
            case TRANSIT_WALLPAPER_INTRA_OPEN /* 4110 */:
                return "TRANSIT_WALLPAPER_INTRA_OPEN";
            case 8192:
                return "TRANSIT_EXIT_MASK";
            case 8199:
                return "TRANSIT_ACTIVITY_CLOSE";
            case 8201:
                return "TRANSIT_TASK_CLOSE";
            case 8203:
                return "TRANSIT_TASK_TO_BACK";
            case 8204:
                return "TRANSIT_WALLPAPER_CLOSE";
            case 8207:
                return "TRANSIT_WALLPAPER_INTRA_CLOSE";
            default:
                return "<UNKNOWN>";
        }
    }

    private String appStateToString() {
        switch (this.mAppTransitionState) {
            case 0:
                return "APP_STATE_IDLE";
            case 1:
                return "APP_STATE_READY";
            case 2:
                return "APP_STATE_RUNNING";
            case 3:
                return "APP_STATE_TIMEOUT";
            default:
                return "unknown state=" + this.mAppTransitionState;
        }
    }

    private String transitTypeToString() {
        switch (this.mNextAppTransitionType) {
            case 0:
                return "NEXT_TRANSIT_TYPE_NONE";
            case 1:
                return "NEXT_TRANSIT_TYPE_CUSTOM";
            case 2:
                return "NEXT_TRANSIT_TYPE_SCALE_UP";
            case 3:
                return "NEXT_TRANSIT_TYPE_THUMBNAIL_SCALE_UP";
            case 4:
                return "NEXT_TRANSIT_TYPE_THUMBNAIL_SCALE_DOWN";
            default:
                return "unknown type=" + this.mNextAppTransitionType;
        }
    }

    @Override // com.android.internal.util.DumpUtils.Dump
    public void dump(PrintWriter pw) {
        pw.print(Separators.SP + this);
        pw.print("  mAppTransitionState=");
        pw.println(appStateToString());
        if (this.mNextAppTransitionType != 0) {
            pw.print("  mNextAppTransitionType=");
            pw.println(transitTypeToString());
        }
        switch (this.mNextAppTransitionType) {
            case 1:
                pw.print("  mNextAppTransitionPackage=");
                pw.println(this.mNextAppTransitionPackage);
                pw.print("  mNextAppTransitionEnter=0x");
                pw.print(Integer.toHexString(this.mNextAppTransitionEnter));
                pw.print(" mNextAppTransitionExit=0x");
                pw.println(Integer.toHexString(this.mNextAppTransitionExit));
                break;
            case 2:
                pw.print("  mNextAppTransitionStartX=");
                pw.print(this.mNextAppTransitionStartX);
                pw.print(" mNextAppTransitionStartY=");
                pw.println(this.mNextAppTransitionStartY);
                pw.print("  mNextAppTransitionStartWidth=");
                pw.print(this.mNextAppTransitionStartWidth);
                pw.print(" mNextAppTransitionStartHeight=");
                pw.println(this.mNextAppTransitionStartHeight);
                break;
            case 3:
            case 4:
                pw.print("  mNextAppTransitionThumbnail=");
                pw.print(this.mNextAppTransitionThumbnail);
                pw.print(" mNextAppTransitionStartX=");
                pw.print(this.mNextAppTransitionStartX);
                pw.print(" mNextAppTransitionStartY=");
                pw.println(this.mNextAppTransitionStartY);
                pw.print("  mNextAppTransitionScaleUp=");
                pw.println(this.mNextAppTransitionScaleUp);
                break;
        }
        if (this.mNextAppTransitionCallback != null) {
            pw.print("  mNextAppTransitionCallback=");
            pw.println(this.mNextAppTransitionCallback);
        }
    }

    public void setCurrentUser(int newUserId) {
        this.mCurrentUserId = newUserId;
    }
}