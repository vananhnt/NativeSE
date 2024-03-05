package android.view;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.view.View;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/* loaded from: ViewPropertyAnimator.class */
public class ViewPropertyAnimator {
    private final View mView;
    private long mDuration;
    private TimeInterpolator mInterpolator;
    private ValueAnimator mTempValueAnimator;
    private Runnable mPendingSetupAction;
    private Runnable mPendingCleanupAction;
    private Runnable mPendingOnStartAction;
    private Runnable mPendingOnEndAction;
    private static final int NONE = 0;
    private static final int TRANSLATION_X = 1;
    private static final int TRANSLATION_Y = 2;
    private static final int SCALE_X = 4;
    private static final int SCALE_Y = 8;
    private static final int ROTATION = 16;
    private static final int ROTATION_X = 32;
    private static final int ROTATION_Y = 64;
    private static final int X = 128;
    private static final int Y = 256;
    private static final int ALPHA = 512;
    private static final int TRANSFORM_MASK = 511;
    private HashMap<Animator, Runnable> mAnimatorSetupMap;
    private HashMap<Animator, Runnable> mAnimatorCleanupMap;
    private HashMap<Animator, Runnable> mAnimatorOnStartMap;
    private HashMap<Animator, Runnable> mAnimatorOnEndMap;
    private boolean mDurationSet = false;
    private long mStartDelay = 0;
    private boolean mStartDelaySet = false;
    private boolean mInterpolatorSet = false;
    private Animator.AnimatorListener mListener = null;
    private ValueAnimator.AnimatorUpdateListener mUpdateListener = null;
    private AnimatorEventListener mAnimatorEventListener = new AnimatorEventListener();
    ArrayList<NameValuesHolder> mPendingAnimations = new ArrayList<>();
    private Runnable mAnimationStarter = new Runnable() { // from class: android.view.ViewPropertyAnimator.1
        @Override // java.lang.Runnable
        public void run() {
            ViewPropertyAnimator.this.startAnimation();
        }
    };
    private HashMap<Animator, PropertyBundle> mAnimatorMap = new HashMap<>();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ViewPropertyAnimator$PropertyBundle.class */
    public static class PropertyBundle {
        int mPropertyMask;
        ArrayList<NameValuesHolder> mNameValuesHolder;

        PropertyBundle(int propertyMask, ArrayList<NameValuesHolder> nameValuesHolder) {
            this.mPropertyMask = propertyMask;
            this.mNameValuesHolder = nameValuesHolder;
        }

        boolean cancel(int propertyConstant) {
            if ((this.mPropertyMask & propertyConstant) != 0 && this.mNameValuesHolder != null) {
                int count = this.mNameValuesHolder.size();
                for (int i = 0; i < count; i++) {
                    NameValuesHolder nameValuesHolder = this.mNameValuesHolder.get(i);
                    if (nameValuesHolder.mNameConstant == propertyConstant) {
                        this.mNameValuesHolder.remove(i);
                        this.mPropertyMask &= propertyConstant ^ (-1);
                        return true;
                    }
                }
                return false;
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ViewPropertyAnimator$NameValuesHolder.class */
    public static class NameValuesHolder {
        int mNameConstant;
        float mFromValue;
        float mDeltaValue;

        NameValuesHolder(int nameConstant, float fromValue, float deltaValue) {
            this.mNameConstant = nameConstant;
            this.mFromValue = fromValue;
            this.mDeltaValue = deltaValue;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ViewPropertyAnimator(View view) {
        this.mView = view;
        view.ensureTransformationInfo();
    }

    public ViewPropertyAnimator setDuration(long duration) {
        if (duration < 0) {
            throw new IllegalArgumentException("Animators cannot have negative duration: " + duration);
        }
        this.mDurationSet = true;
        this.mDuration = duration;
        return this;
    }

    public long getDuration() {
        if (this.mDurationSet) {
            return this.mDuration;
        }
        if (this.mTempValueAnimator == null) {
            this.mTempValueAnimator = new ValueAnimator();
        }
        return this.mTempValueAnimator.getDuration();
    }

    public long getStartDelay() {
        if (this.mStartDelaySet) {
            return this.mStartDelay;
        }
        return 0L;
    }

    public ViewPropertyAnimator setStartDelay(long startDelay) {
        if (startDelay < 0) {
            throw new IllegalArgumentException("Animators cannot have negative duration: " + startDelay);
        }
        this.mStartDelaySet = true;
        this.mStartDelay = startDelay;
        return this;
    }

    public ViewPropertyAnimator setInterpolator(TimeInterpolator interpolator) {
        this.mInterpolatorSet = true;
        this.mInterpolator = interpolator;
        return this;
    }

    public TimeInterpolator getInterpolator() {
        if (this.mInterpolatorSet) {
            return this.mInterpolator;
        }
        if (this.mTempValueAnimator == null) {
            this.mTempValueAnimator = new ValueAnimator();
        }
        return this.mTempValueAnimator.getInterpolator();
    }

    public ViewPropertyAnimator setListener(Animator.AnimatorListener listener) {
        this.mListener = listener;
        return this;
    }

    public ViewPropertyAnimator setUpdateListener(ValueAnimator.AnimatorUpdateListener listener) {
        this.mUpdateListener = listener;
        return this;
    }

    public void start() {
        this.mView.removeCallbacks(this.mAnimationStarter);
        startAnimation();
    }

    public void cancel() {
        if (this.mAnimatorMap.size() > 0) {
            HashMap<Animator, PropertyBundle> mAnimatorMapCopy = (HashMap) this.mAnimatorMap.clone();
            Set<Animator> animatorSet = mAnimatorMapCopy.keySet();
            for (Animator runningAnim : animatorSet) {
                runningAnim.cancel();
            }
        }
        this.mPendingAnimations.clear();
        this.mView.removeCallbacks(this.mAnimationStarter);
    }

    public ViewPropertyAnimator x(float value) {
        animateProperty(128, value);
        return this;
    }

    public ViewPropertyAnimator xBy(float value) {
        animatePropertyBy(128, value);
        return this;
    }

    public ViewPropertyAnimator y(float value) {
        animateProperty(256, value);
        return this;
    }

    public ViewPropertyAnimator yBy(float value) {
        animatePropertyBy(256, value);
        return this;
    }

    public ViewPropertyAnimator rotation(float value) {
        animateProperty(16, value);
        return this;
    }

    public ViewPropertyAnimator rotationBy(float value) {
        animatePropertyBy(16, value);
        return this;
    }

    public ViewPropertyAnimator rotationX(float value) {
        animateProperty(32, value);
        return this;
    }

    public ViewPropertyAnimator rotationXBy(float value) {
        animatePropertyBy(32, value);
        return this;
    }

    public ViewPropertyAnimator rotationY(float value) {
        animateProperty(64, value);
        return this;
    }

    public ViewPropertyAnimator rotationYBy(float value) {
        animatePropertyBy(64, value);
        return this;
    }

    public ViewPropertyAnimator translationX(float value) {
        animateProperty(1, value);
        return this;
    }

    public ViewPropertyAnimator translationXBy(float value) {
        animatePropertyBy(1, value);
        return this;
    }

    public ViewPropertyAnimator translationY(float value) {
        animateProperty(2, value);
        return this;
    }

    public ViewPropertyAnimator translationYBy(float value) {
        animatePropertyBy(2, value);
        return this;
    }

    public ViewPropertyAnimator scaleX(float value) {
        animateProperty(4, value);
        return this;
    }

    public ViewPropertyAnimator scaleXBy(float value) {
        animatePropertyBy(4, value);
        return this;
    }

    public ViewPropertyAnimator scaleY(float value) {
        animateProperty(8, value);
        return this;
    }

    public ViewPropertyAnimator scaleYBy(float value) {
        animatePropertyBy(8, value);
        return this;
    }

    public ViewPropertyAnimator alpha(float value) {
        animateProperty(512, value);
        return this;
    }

    public ViewPropertyAnimator alphaBy(float value) {
        animatePropertyBy(512, value);
        return this;
    }

    public ViewPropertyAnimator withLayer() {
        this.mPendingSetupAction = new Runnable() { // from class: android.view.ViewPropertyAnimator.2
            @Override // java.lang.Runnable
            public void run() {
                ViewPropertyAnimator.this.mView.setLayerType(2, null);
                if (ViewPropertyAnimator.this.mView.isAttachedToWindow()) {
                    ViewPropertyAnimator.this.mView.buildLayer();
                }
            }
        };
        final int currentLayerType = this.mView.getLayerType();
        this.mPendingCleanupAction = new Runnable() { // from class: android.view.ViewPropertyAnimator.3
            @Override // java.lang.Runnable
            public void run() {
                ViewPropertyAnimator.this.mView.setLayerType(currentLayerType, null);
            }
        };
        if (this.mAnimatorSetupMap == null) {
            this.mAnimatorSetupMap = new HashMap<>();
        }
        if (this.mAnimatorCleanupMap == null) {
            this.mAnimatorCleanupMap = new HashMap<>();
        }
        return this;
    }

    public ViewPropertyAnimator withStartAction(Runnable runnable) {
        this.mPendingOnStartAction = runnable;
        if (runnable != null && this.mAnimatorOnStartMap == null) {
            this.mAnimatorOnStartMap = new HashMap<>();
        }
        return this;
    }

    public ViewPropertyAnimator withEndAction(Runnable runnable) {
        this.mPendingOnEndAction = runnable;
        if (runnable != null && this.mAnimatorOnEndMap == null) {
            this.mAnimatorOnEndMap = new HashMap<>();
        }
        return this;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startAnimation() {
        this.mView.setHasTransientState(true);
        ValueAnimator animator = ValueAnimator.ofFloat(1.0f);
        ArrayList<NameValuesHolder> nameValueList = (ArrayList) this.mPendingAnimations.clone();
        this.mPendingAnimations.clear();
        int propertyMask = 0;
        int propertyCount = nameValueList.size();
        for (int i = 0; i < propertyCount; i++) {
            NameValuesHolder nameValuesHolder = nameValueList.get(i);
            propertyMask |= nameValuesHolder.mNameConstant;
        }
        this.mAnimatorMap.put(animator, new PropertyBundle(propertyMask, nameValueList));
        if (this.mPendingSetupAction != null) {
            this.mAnimatorSetupMap.put(animator, this.mPendingSetupAction);
            this.mPendingSetupAction = null;
        }
        if (this.mPendingCleanupAction != null) {
            this.mAnimatorCleanupMap.put(animator, this.mPendingCleanupAction);
            this.mPendingCleanupAction = null;
        }
        if (this.mPendingOnStartAction != null) {
            this.mAnimatorOnStartMap.put(animator, this.mPendingOnStartAction);
            this.mPendingOnStartAction = null;
        }
        if (this.mPendingOnEndAction != null) {
            this.mAnimatorOnEndMap.put(animator, this.mPendingOnEndAction);
            this.mPendingOnEndAction = null;
        }
        animator.addUpdateListener(this.mAnimatorEventListener);
        animator.addListener(this.mAnimatorEventListener);
        if (this.mStartDelaySet) {
            animator.setStartDelay(this.mStartDelay);
        }
        if (this.mDurationSet) {
            animator.setDuration(this.mDuration);
        }
        if (this.mInterpolatorSet) {
            animator.setInterpolator(this.mInterpolator);
        }
        animator.start();
    }

    private void animateProperty(int constantName, float toValue) {
        float fromValue = getValue(constantName);
        float deltaValue = toValue - fromValue;
        animatePropertyBy(constantName, fromValue, deltaValue);
    }

    private void animatePropertyBy(int constantName, float byValue) {
        float fromValue = getValue(constantName);
        animatePropertyBy(constantName, fromValue, byValue);
    }

    private void animatePropertyBy(int constantName, float startValue, float byValue) {
        if (this.mAnimatorMap.size() > 0) {
            Animator animatorToCancel = null;
            Set<Animator> animatorSet = this.mAnimatorMap.keySet();
            Iterator i$ = animatorSet.iterator();
            while (true) {
                if (!i$.hasNext()) {
                    break;
                }
                Animator runningAnim = i$.next();
                PropertyBundle bundle = this.mAnimatorMap.get(runningAnim);
                if (bundle.cancel(constantName) && bundle.mPropertyMask == 0) {
                    animatorToCancel = runningAnim;
                    break;
                }
            }
            if (animatorToCancel != null) {
                animatorToCancel.cancel();
            }
        }
        NameValuesHolder nameValuePair = new NameValuesHolder(constantName, startValue, byValue);
        this.mPendingAnimations.add(nameValuePair);
        this.mView.removeCallbacks(this.mAnimationStarter);
        this.mView.postOnAnimation(this.mAnimationStarter);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setValue(int propertyConstant, float value) {
        View.TransformationInfo info = this.mView.mTransformationInfo;
        DisplayList displayList = this.mView.mDisplayList;
        switch (propertyConstant) {
            case 1:
                info.mTranslationX = value;
                if (displayList != null) {
                    displayList.setTranslationX(value);
                    return;
                }
                return;
            case 2:
                info.mTranslationY = value;
                if (displayList != null) {
                    displayList.setTranslationY(value);
                    return;
                }
                return;
            case 4:
                info.mScaleX = value;
                if (displayList != null) {
                    displayList.setScaleX(value);
                    return;
                }
                return;
            case 8:
                info.mScaleY = value;
                if (displayList != null) {
                    displayList.setScaleY(value);
                    return;
                }
                return;
            case 16:
                info.mRotation = value;
                if (displayList != null) {
                    displayList.setRotation(value);
                    return;
                }
                return;
            case 32:
                info.mRotationX = value;
                if (displayList != null) {
                    displayList.setRotationX(value);
                    return;
                }
                return;
            case 64:
                info.mRotationY = value;
                if (displayList != null) {
                    displayList.setRotationY(value);
                    return;
                }
                return;
            case 128:
                info.mTranslationX = value - this.mView.mLeft;
                if (displayList != null) {
                    displayList.setTranslationX(value - this.mView.mLeft);
                    return;
                }
                return;
            case 256:
                info.mTranslationY = value - this.mView.mTop;
                if (displayList != null) {
                    displayList.setTranslationY(value - this.mView.mTop);
                    return;
                }
                return;
            case 512:
                info.mAlpha = value;
                if (displayList != null) {
                    displayList.setAlpha(value);
                    return;
                }
                return;
            default:
                return;
        }
    }

    private float getValue(int propertyConstant) {
        View.TransformationInfo info = this.mView.mTransformationInfo;
        switch (propertyConstant) {
            case 1:
                return info.mTranslationX;
            case 2:
                return info.mTranslationY;
            case 4:
                return info.mScaleX;
            case 8:
                return info.mScaleY;
            case 16:
                return info.mRotation;
            case 32:
                return info.mRotationX;
            case 64:
                return info.mRotationY;
            case 128:
                return this.mView.mLeft + info.mTranslationX;
            case 256:
                return this.mView.mTop + info.mTranslationY;
            case 512:
                return info.mAlpha;
            default:
                return 0.0f;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ViewPropertyAnimator$AnimatorEventListener.class */
    public class AnimatorEventListener implements Animator.AnimatorListener, ValueAnimator.AnimatorUpdateListener {
        private AnimatorEventListener() {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationStart(Animator animation) {
            if (ViewPropertyAnimator.this.mAnimatorSetupMap != null) {
                Runnable r = (Runnable) ViewPropertyAnimator.this.mAnimatorSetupMap.get(animation);
                if (r != null) {
                    r.run();
                }
                ViewPropertyAnimator.this.mAnimatorSetupMap.remove(animation);
            }
            if (ViewPropertyAnimator.this.mAnimatorOnStartMap != null) {
                Runnable r2 = (Runnable) ViewPropertyAnimator.this.mAnimatorOnStartMap.get(animation);
                if (r2 != null) {
                    r2.run();
                }
                ViewPropertyAnimator.this.mAnimatorOnStartMap.remove(animation);
            }
            if (ViewPropertyAnimator.this.mListener != null) {
                ViewPropertyAnimator.this.mListener.onAnimationStart(animation);
            }
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationCancel(Animator animation) {
            if (ViewPropertyAnimator.this.mListener != null) {
                ViewPropertyAnimator.this.mListener.onAnimationCancel(animation);
            }
            if (ViewPropertyAnimator.this.mAnimatorOnEndMap != null) {
                ViewPropertyAnimator.this.mAnimatorOnEndMap.remove(animation);
            }
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationRepeat(Animator animation) {
            if (ViewPropertyAnimator.this.mListener != null) {
                ViewPropertyAnimator.this.mListener.onAnimationRepeat(animation);
            }
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animation) {
            ViewPropertyAnimator.this.mView.setHasTransientState(false);
            if (ViewPropertyAnimator.this.mListener != null) {
                ViewPropertyAnimator.this.mListener.onAnimationEnd(animation);
            }
            if (ViewPropertyAnimator.this.mAnimatorOnEndMap != null) {
                Runnable r = (Runnable) ViewPropertyAnimator.this.mAnimatorOnEndMap.get(animation);
                if (r != null) {
                    r.run();
                }
                ViewPropertyAnimator.this.mAnimatorOnEndMap.remove(animation);
            }
            if (ViewPropertyAnimator.this.mAnimatorCleanupMap != null) {
                Runnable r2 = (Runnable) ViewPropertyAnimator.this.mAnimatorCleanupMap.get(animation);
                if (r2 != null) {
                    r2.run();
                }
                ViewPropertyAnimator.this.mAnimatorCleanupMap.remove(animation);
            }
            ViewPropertyAnimator.this.mAnimatorMap.remove(animation);
        }

        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
        public void onAnimationUpdate(ValueAnimator animation) {
            PropertyBundle propertyBundle = (PropertyBundle) ViewPropertyAnimator.this.mAnimatorMap.get(animation);
            if (propertyBundle != null) {
                boolean useDisplayListProperties = ViewPropertyAnimator.this.mView.mDisplayList != null;
                boolean alphaHandled = false;
                if (!useDisplayListProperties) {
                    ViewPropertyAnimator.this.mView.invalidateParentCaches();
                }
                float fraction = animation.getAnimatedFraction();
                int propertyMask = propertyBundle.mPropertyMask;
                if ((propertyMask & 511) != 0) {
                    ViewPropertyAnimator.this.mView.invalidateViewProperty(false, false);
                }
                ArrayList<NameValuesHolder> valueList = propertyBundle.mNameValuesHolder;
                if (valueList != null) {
                    int count = valueList.size();
                    for (int i = 0; i < count; i++) {
                        NameValuesHolder values = valueList.get(i);
                        float value = values.mFromValue + (fraction * values.mDeltaValue);
                        if (values.mNameConstant == 512) {
                            alphaHandled = ViewPropertyAnimator.this.mView.setAlphaNoInvalidation(value);
                        } else {
                            ViewPropertyAnimator.this.setValue(values.mNameConstant, value);
                        }
                    }
                }
                if ((propertyMask & 511) != 0) {
                    ViewPropertyAnimator.this.mView.mTransformationInfo.mMatrixDirty = true;
                    if (!useDisplayListProperties) {
                        ViewPropertyAnimator.this.mView.mPrivateFlags |= 32;
                    }
                }
                if (alphaHandled) {
                    ViewPropertyAnimator.this.mView.invalidate(true);
                } else {
                    ViewPropertyAnimator.this.mView.invalidateViewProperty(false, false);
                }
                if (ViewPropertyAnimator.this.mUpdateListener != null) {
                    ViewPropertyAnimator.this.mUpdateListener.onAnimationUpdate(animation);
                }
            }
        }
    }
}