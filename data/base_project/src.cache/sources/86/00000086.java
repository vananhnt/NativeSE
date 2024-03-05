package android.animation;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/* loaded from: LayoutTransition.class */
public class LayoutTransition {
    public static final int CHANGE_APPEARING = 0;
    public static final int CHANGE_DISAPPEARING = 1;
    public static final int APPEARING = 2;
    public static final int DISAPPEARING = 3;
    public static final int CHANGING = 4;
    private static final int FLAG_APPEARING = 1;
    private static final int FLAG_DISAPPEARING = 2;
    private static final int FLAG_CHANGE_APPEARING = 4;
    private static final int FLAG_CHANGE_DISAPPEARING = 8;
    private static final int FLAG_CHANGING = 16;
    private Animator mDisappearingAnim;
    private Animator mAppearingAnim;
    private Animator mChangingAppearingAnim;
    private Animator mChangingDisappearingAnim;
    private Animator mChangingAnim;
    private static ObjectAnimator defaultChange;
    private static ObjectAnimator defaultChangeIn;
    private static ObjectAnimator defaultChangeOut;
    private static ObjectAnimator defaultFadeIn;
    private static ObjectAnimator defaultFadeOut;
    private long staggerDelay;
    private ArrayList<TransitionListener> mListeners;
    private static long DEFAULT_DURATION = 300;
    private static TimeInterpolator ACCEL_DECEL_INTERPOLATOR = new AccelerateDecelerateInterpolator();
    private static TimeInterpolator DECEL_INTERPOLATOR = new DecelerateInterpolator();
    private static TimeInterpolator sAppearingInterpolator = ACCEL_DECEL_INTERPOLATOR;
    private static TimeInterpolator sDisappearingInterpolator = ACCEL_DECEL_INTERPOLATOR;
    private static TimeInterpolator sChangingAppearingInterpolator = DECEL_INTERPOLATOR;
    private static TimeInterpolator sChangingDisappearingInterpolator = DECEL_INTERPOLATOR;
    private static TimeInterpolator sChangingInterpolator = DECEL_INTERPOLATOR;
    private long mChangingAppearingDuration = DEFAULT_DURATION;
    private long mChangingDisappearingDuration = DEFAULT_DURATION;
    private long mChangingDuration = DEFAULT_DURATION;
    private long mAppearingDuration = DEFAULT_DURATION;
    private long mDisappearingDuration = DEFAULT_DURATION;
    private long mAppearingDelay = DEFAULT_DURATION;
    private long mDisappearingDelay = 0;
    private long mChangingAppearingDelay = 0;
    private long mChangingDisappearingDelay = DEFAULT_DURATION;
    private long mChangingDelay = 0;
    private long mChangingAppearingStagger = 0;
    private long mChangingDisappearingStagger = 0;
    private long mChangingStagger = 0;
    private TimeInterpolator mAppearingInterpolator = sAppearingInterpolator;
    private TimeInterpolator mDisappearingInterpolator = sDisappearingInterpolator;
    private TimeInterpolator mChangingAppearingInterpolator = sChangingAppearingInterpolator;
    private TimeInterpolator mChangingDisappearingInterpolator = sChangingDisappearingInterpolator;
    private TimeInterpolator mChangingInterpolator = sChangingInterpolator;
    private final HashMap<View, Animator> pendingAnimations = new HashMap<>();
    private final LinkedHashMap<View, Animator> currentChangingAnimations = new LinkedHashMap<>();
    private final LinkedHashMap<View, Animator> currentAppearingAnimations = new LinkedHashMap<>();
    private final LinkedHashMap<View, Animator> currentDisappearingAnimations = new LinkedHashMap<>();
    private final HashMap<View, View.OnLayoutChangeListener> layoutChangeListenerMap = new HashMap<>();
    private int mTransitionTypes = 15;
    private boolean mAnimateParentHierarchy = true;

    /* loaded from: LayoutTransition$TransitionListener.class */
    public interface TransitionListener {
        void startTransition(LayoutTransition layoutTransition, ViewGroup viewGroup, View view, int i);

        void endTransition(LayoutTransition layoutTransition, ViewGroup viewGroup, View view, int i);
    }

    static /* synthetic */ long access$314(LayoutTransition x0, long x1) {
        long j = x0.staggerDelay + x1;
        x0.staggerDelay = j;
        return j;
    }

    public LayoutTransition() {
        this.mDisappearingAnim = null;
        this.mAppearingAnim = null;
        this.mChangingAppearingAnim = null;
        this.mChangingDisappearingAnim = null;
        this.mChangingAnim = null;
        if (defaultChangeIn == null) {
            PropertyValuesHolder pvhLeft = PropertyValuesHolder.ofInt("left", 0, 1);
            PropertyValuesHolder pvhTop = PropertyValuesHolder.ofInt("top", 0, 1);
            PropertyValuesHolder pvhRight = PropertyValuesHolder.ofInt("right", 0, 1);
            PropertyValuesHolder pvhBottom = PropertyValuesHolder.ofInt("bottom", 0, 1);
            PropertyValuesHolder pvhScrollX = PropertyValuesHolder.ofInt("scrollX", 0, 1);
            PropertyValuesHolder pvhScrollY = PropertyValuesHolder.ofInt("scrollY", 0, 1);
            defaultChangeIn = ObjectAnimator.ofPropertyValuesHolder(null, pvhLeft, pvhTop, pvhRight, pvhBottom, pvhScrollX, pvhScrollY);
            defaultChangeIn.setDuration(DEFAULT_DURATION);
            defaultChangeIn.setStartDelay(this.mChangingAppearingDelay);
            defaultChangeIn.setInterpolator(this.mChangingAppearingInterpolator);
            defaultChangeOut = defaultChangeIn.mo6clone();
            defaultChangeOut.setStartDelay(this.mChangingDisappearingDelay);
            defaultChangeOut.setInterpolator(this.mChangingDisappearingInterpolator);
            defaultChange = defaultChangeIn.mo6clone();
            defaultChange.setStartDelay(this.mChangingDelay);
            defaultChange.setInterpolator(this.mChangingInterpolator);
            defaultFadeIn = ObjectAnimator.ofFloat((Object) null, "alpha", 0.0f, 1.0f);
            defaultFadeIn.setDuration(DEFAULT_DURATION);
            defaultFadeIn.setStartDelay(this.mAppearingDelay);
            defaultFadeIn.setInterpolator(this.mAppearingInterpolator);
            defaultFadeOut = ObjectAnimator.ofFloat((Object) null, "alpha", 1.0f, 0.0f);
            defaultFadeOut.setDuration(DEFAULT_DURATION);
            defaultFadeOut.setStartDelay(this.mDisappearingDelay);
            defaultFadeOut.setInterpolator(this.mDisappearingInterpolator);
        }
        this.mChangingAppearingAnim = defaultChangeIn;
        this.mChangingDisappearingAnim = defaultChangeOut;
        this.mChangingAnim = defaultChange;
        this.mAppearingAnim = defaultFadeIn;
        this.mDisappearingAnim = defaultFadeOut;
    }

    public void setDuration(long duration) {
        this.mChangingAppearingDuration = duration;
        this.mChangingDisappearingDuration = duration;
        this.mChangingDuration = duration;
        this.mAppearingDuration = duration;
        this.mDisappearingDuration = duration;
    }

    public void enableTransitionType(int transitionType) {
        switch (transitionType) {
            case 0:
                this.mTransitionTypes |= 4;
                return;
            case 1:
                this.mTransitionTypes |= 8;
                return;
            case 2:
                this.mTransitionTypes |= 1;
                return;
            case 3:
                this.mTransitionTypes |= 2;
                return;
            case 4:
                this.mTransitionTypes |= 16;
                return;
            default:
                return;
        }
    }

    public void disableTransitionType(int transitionType) {
        switch (transitionType) {
            case 0:
                this.mTransitionTypes &= -5;
                return;
            case 1:
                this.mTransitionTypes &= -9;
                return;
            case 2:
                this.mTransitionTypes &= -2;
                return;
            case 3:
                this.mTransitionTypes &= -3;
                return;
            case 4:
                this.mTransitionTypes &= -17;
                return;
            default:
                return;
        }
    }

    public boolean isTransitionTypeEnabled(int transitionType) {
        switch (transitionType) {
            case 0:
                return (this.mTransitionTypes & 4) == 4;
            case 1:
                return (this.mTransitionTypes & 8) == 8;
            case 2:
                return (this.mTransitionTypes & 1) == 1;
            case 3:
                return (this.mTransitionTypes & 2) == 2;
            case 4:
                return (this.mTransitionTypes & 16) == 16;
            default:
                return false;
        }
    }

    public void setStartDelay(int transitionType, long delay) {
        switch (transitionType) {
            case 0:
                this.mChangingAppearingDelay = delay;
                return;
            case 1:
                this.mChangingDisappearingDelay = delay;
                return;
            case 2:
                this.mAppearingDelay = delay;
                return;
            case 3:
                this.mDisappearingDelay = delay;
                return;
            case 4:
                this.mChangingDelay = delay;
                return;
            default:
                return;
        }
    }

    public long getStartDelay(int transitionType) {
        switch (transitionType) {
            case 0:
                return this.mChangingAppearingDelay;
            case 1:
                return this.mChangingDisappearingDelay;
            case 2:
                return this.mAppearingDelay;
            case 3:
                return this.mDisappearingDelay;
            case 4:
                return this.mChangingDelay;
            default:
                return 0L;
        }
    }

    public void setDuration(int transitionType, long duration) {
        switch (transitionType) {
            case 0:
                this.mChangingAppearingDuration = duration;
                return;
            case 1:
                this.mChangingDisappearingDuration = duration;
                return;
            case 2:
                this.mAppearingDuration = duration;
                return;
            case 3:
                this.mDisappearingDuration = duration;
                return;
            case 4:
                this.mChangingDuration = duration;
                return;
            default:
                return;
        }
    }

    public long getDuration(int transitionType) {
        switch (transitionType) {
            case 0:
                return this.mChangingAppearingDuration;
            case 1:
                return this.mChangingDisappearingDuration;
            case 2:
                return this.mAppearingDuration;
            case 3:
                return this.mDisappearingDuration;
            case 4:
                return this.mChangingDuration;
            default:
                return 0L;
        }
    }

    public void setStagger(int transitionType, long duration) {
        switch (transitionType) {
            case 0:
                this.mChangingAppearingStagger = duration;
                return;
            case 1:
                this.mChangingDisappearingStagger = duration;
                return;
            case 2:
            case 3:
            default:
                return;
            case 4:
                this.mChangingStagger = duration;
                return;
        }
    }

    public long getStagger(int transitionType) {
        switch (transitionType) {
            case 0:
                return this.mChangingAppearingStagger;
            case 1:
                return this.mChangingDisappearingStagger;
            case 2:
            case 3:
            default:
                return 0L;
            case 4:
                return this.mChangingStagger;
        }
    }

    public void setInterpolator(int transitionType, TimeInterpolator interpolator) {
        switch (transitionType) {
            case 0:
                this.mChangingAppearingInterpolator = interpolator;
                return;
            case 1:
                this.mChangingDisappearingInterpolator = interpolator;
                return;
            case 2:
                this.mAppearingInterpolator = interpolator;
                return;
            case 3:
                this.mDisappearingInterpolator = interpolator;
                return;
            case 4:
                this.mChangingInterpolator = interpolator;
                return;
            default:
                return;
        }
    }

    public TimeInterpolator getInterpolator(int transitionType) {
        switch (transitionType) {
            case 0:
                return this.mChangingAppearingInterpolator;
            case 1:
                return this.mChangingDisappearingInterpolator;
            case 2:
                return this.mAppearingInterpolator;
            case 3:
                return this.mDisappearingInterpolator;
            case 4:
                return this.mChangingInterpolator;
            default:
                return null;
        }
    }

    public void setAnimator(int transitionType, Animator animator) {
        switch (transitionType) {
            case 0:
                this.mChangingAppearingAnim = animator;
                return;
            case 1:
                this.mChangingDisappearingAnim = animator;
                return;
            case 2:
                this.mAppearingAnim = animator;
                return;
            case 3:
                this.mDisappearingAnim = animator;
                return;
            case 4:
                this.mChangingAnim = animator;
                return;
            default:
                return;
        }
    }

    public Animator getAnimator(int transitionType) {
        switch (transitionType) {
            case 0:
                return this.mChangingAppearingAnim;
            case 1:
                return this.mChangingDisappearingAnim;
            case 2:
                return this.mAppearingAnim;
            case 3:
                return this.mDisappearingAnim;
            case 4:
                return this.mChangingAnim;
            default:
                return null;
        }
    }

    private void runChangeTransition(final ViewGroup parent, View newView, int changeReason) {
        long duration;
        Animator baseAnimator = null;
        Animator parentAnimator = null;
        switch (changeReason) {
            case 2:
                baseAnimator = this.mChangingAppearingAnim;
                duration = this.mChangingAppearingDuration;
                parentAnimator = defaultChangeIn;
                break;
            case 3:
                baseAnimator = this.mChangingDisappearingAnim;
                duration = this.mChangingDisappearingDuration;
                parentAnimator = defaultChangeOut;
                break;
            case 4:
                baseAnimator = this.mChangingAnim;
                duration = this.mChangingDuration;
                parentAnimator = defaultChange;
                break;
            default:
                duration = 0;
                break;
        }
        if (baseAnimator == null) {
            return;
        }
        this.staggerDelay = 0L;
        ViewTreeObserver observer = parent.getViewTreeObserver();
        if (!observer.isAlive()) {
            return;
        }
        int numChildren = parent.getChildCount();
        for (int i = 0; i < numChildren; i++) {
            View child = parent.getChildAt(i);
            if (child != newView) {
                setupChangeAnimation(parent, changeReason, baseAnimator, duration, child);
            }
        }
        if (this.mAnimateParentHierarchy) {
            ViewGroup viewGroup = parent;
            while (true) {
                ViewGroup tempParent = viewGroup;
                if (tempParent != null) {
                    ViewParent parentParent = tempParent.getParent();
                    if (parentParent instanceof ViewGroup) {
                        setupChangeAnimation((ViewGroup) parentParent, changeReason, parentAnimator, duration, tempParent);
                        viewGroup = (ViewGroup) parentParent;
                    } else {
                        viewGroup = null;
                    }
                }
            }
        }
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: android.animation.LayoutTransition.1
            @Override // android.view.ViewTreeObserver.OnPreDrawListener
            public boolean onPreDraw() {
                parent.getViewTreeObserver().removeOnPreDrawListener(this);
                int count = LayoutTransition.this.layoutChangeListenerMap.size();
                if (count > 0) {
                    Collection<View> views = LayoutTransition.this.layoutChangeListenerMap.keySet();
                    for (View view : views) {
                        View.OnLayoutChangeListener listener = (View.OnLayoutChangeListener) LayoutTransition.this.layoutChangeListenerMap.get(view);
                        view.removeOnLayoutChangeListener(listener);
                    }
                }
                LayoutTransition.this.layoutChangeListenerMap.clear();
                return true;
            }
        });
    }

    public void setAnimateParentHierarchy(boolean animateParentHierarchy) {
        this.mAnimateParentHierarchy = animateParentHierarchy;
    }

    private void setupChangeAnimation(final ViewGroup parent, final int changeReason, Animator baseAnimator, final long duration, final View child) {
        if (this.layoutChangeListenerMap.get(child) != null) {
            return;
        }
        if (child.getWidth() == 0 && child.getHeight() == 0) {
            return;
        }
        final Animator anim = baseAnimator.mo6clone();
        anim.setTarget(child);
        anim.setupStartValues();
        Animator currentAnimation = this.pendingAnimations.get(child);
        if (currentAnimation != null) {
            currentAnimation.cancel();
            this.pendingAnimations.remove(child);
        }
        this.pendingAnimations.put(child, anim);
        ValueAnimator pendingAnimRemover = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(duration + 100);
        pendingAnimRemover.addListener(new AnimatorListenerAdapter() { // from class: android.animation.LayoutTransition.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                LayoutTransition.this.pendingAnimations.remove(child);
            }
        });
        pendingAnimRemover.start();
        final View.OnLayoutChangeListener listener = new View.OnLayoutChangeListener() { // from class: android.animation.LayoutTransition.3
            @Override // android.view.View.OnLayoutChangeListener
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                anim.setupEndValues();
                if (anim instanceof ValueAnimator) {
                    boolean valuesDiffer = false;
                    ValueAnimator valueAnim = (ValueAnimator) anim;
                    PropertyValuesHolder[] oldValues = valueAnim.getValues();
                    for (PropertyValuesHolder pvh : oldValues) {
                        KeyframeSet keyframeSet = pvh.mKeyframeSet;
                        if (keyframeSet.mFirstKeyframe == null || keyframeSet.mLastKeyframe == null || !keyframeSet.mFirstKeyframe.getValue().equals(keyframeSet.mLastKeyframe.getValue())) {
                            valuesDiffer = true;
                        }
                    }
                    if (!valuesDiffer) {
                        return;
                    }
                }
                long startDelay = 0;
                switch (changeReason) {
                    case 2:
                        startDelay = LayoutTransition.this.mChangingAppearingDelay + LayoutTransition.this.staggerDelay;
                        LayoutTransition.access$314(LayoutTransition.this, LayoutTransition.this.mChangingAppearingStagger);
                        if (LayoutTransition.this.mChangingAppearingInterpolator != LayoutTransition.sChangingAppearingInterpolator) {
                            anim.setInterpolator(LayoutTransition.this.mChangingAppearingInterpolator);
                            break;
                        }
                        break;
                    case 3:
                        startDelay = LayoutTransition.this.mChangingDisappearingDelay + LayoutTransition.this.staggerDelay;
                        LayoutTransition.access$314(LayoutTransition.this, LayoutTransition.this.mChangingDisappearingStagger);
                        if (LayoutTransition.this.mChangingDisappearingInterpolator != LayoutTransition.sChangingDisappearingInterpolator) {
                            anim.setInterpolator(LayoutTransition.this.mChangingDisappearingInterpolator);
                            break;
                        }
                        break;
                    case 4:
                        startDelay = LayoutTransition.this.mChangingDelay + LayoutTransition.this.staggerDelay;
                        LayoutTransition.access$314(LayoutTransition.this, LayoutTransition.this.mChangingStagger);
                        if (LayoutTransition.this.mChangingInterpolator != LayoutTransition.sChangingInterpolator) {
                            anim.setInterpolator(LayoutTransition.this.mChangingInterpolator);
                            break;
                        }
                        break;
                }
                anim.setStartDelay(startDelay);
                anim.setDuration(duration);
                Animator prevAnimation = (Animator) LayoutTransition.this.currentChangingAnimations.get(child);
                if (prevAnimation != null) {
                    prevAnimation.cancel();
                }
                Animator pendingAnimation = (Animator) LayoutTransition.this.pendingAnimations.get(child);
                if (pendingAnimation != null) {
                    LayoutTransition.this.pendingAnimations.remove(child);
                }
                LayoutTransition.this.currentChangingAnimations.put(child, anim);
                parent.requestTransitionStart(LayoutTransition.this);
                child.removeOnLayoutChangeListener(this);
                LayoutTransition.this.layoutChangeListenerMap.remove(child);
            }
        };
        anim.addListener(new AnimatorListenerAdapter() { // from class: android.animation.LayoutTransition.4
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
                if (LayoutTransition.this.hasListeners()) {
                    ArrayList<TransitionListener> listeners = (ArrayList) LayoutTransition.this.mListeners.clone();
                    Iterator i$ = listeners.iterator();
                    while (i$.hasNext()) {
                        TransitionListener listener2 = i$.next();
                        listener2.startTransition(LayoutTransition.this, parent, child, changeReason == 2 ? 0 : changeReason == 3 ? 1 : 4);
                    }
                }
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
                child.removeOnLayoutChangeListener(listener);
                LayoutTransition.this.layoutChangeListenerMap.remove(child);
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                LayoutTransition.this.currentChangingAnimations.remove(child);
                if (LayoutTransition.this.hasListeners()) {
                    ArrayList<TransitionListener> listeners = (ArrayList) LayoutTransition.this.mListeners.clone();
                    Iterator i$ = listeners.iterator();
                    while (i$.hasNext()) {
                        TransitionListener listener2 = i$.next();
                        listener2.endTransition(LayoutTransition.this, parent, child, changeReason == 2 ? 0 : changeReason == 3 ? 1 : 4);
                    }
                }
            }
        });
        child.addOnLayoutChangeListener(listener);
        this.layoutChangeListenerMap.put(child, listener);
    }

    public void startChangingAnimations() {
        LinkedHashMap<View, Animator> currentAnimCopy = (LinkedHashMap) this.currentChangingAnimations.clone();
        for (Animator anim : currentAnimCopy.values()) {
            if (anim instanceof ObjectAnimator) {
                ((ObjectAnimator) anim).setCurrentPlayTime(0L);
            }
            anim.start();
        }
    }

    public void endChangingAnimations() {
        LinkedHashMap<View, Animator> currentAnimCopy = (LinkedHashMap) this.currentChangingAnimations.clone();
        for (Animator anim : currentAnimCopy.values()) {
            anim.start();
            anim.end();
        }
        this.currentChangingAnimations.clear();
    }

    public boolean isChangingLayout() {
        return this.currentChangingAnimations.size() > 0;
    }

    public boolean isRunning() {
        return this.currentChangingAnimations.size() > 0 || this.currentAppearingAnimations.size() > 0 || this.currentDisappearingAnimations.size() > 0;
    }

    public void cancel() {
        if (this.currentChangingAnimations.size() > 0) {
            LinkedHashMap<View, Animator> currentAnimCopy = (LinkedHashMap) this.currentChangingAnimations.clone();
            for (Animator anim : currentAnimCopy.values()) {
                anim.cancel();
            }
            this.currentChangingAnimations.clear();
        }
        if (this.currentAppearingAnimations.size() > 0) {
            LinkedHashMap<View, Animator> currentAnimCopy2 = (LinkedHashMap) this.currentAppearingAnimations.clone();
            for (Animator anim2 : currentAnimCopy2.values()) {
                anim2.end();
            }
            this.currentAppearingAnimations.clear();
        }
        if (this.currentDisappearingAnimations.size() > 0) {
            LinkedHashMap<View, Animator> currentAnimCopy3 = (LinkedHashMap) this.currentDisappearingAnimations.clone();
            for (Animator anim3 : currentAnimCopy3.values()) {
                anim3.end();
            }
            this.currentDisappearingAnimations.clear();
        }
    }

    public void cancel(int transitionType) {
        switch (transitionType) {
            case 0:
            case 1:
            case 4:
                if (this.currentChangingAnimations.size() > 0) {
                    LinkedHashMap<View, Animator> currentAnimCopy = (LinkedHashMap) this.currentChangingAnimations.clone();
                    for (Animator anim : currentAnimCopy.values()) {
                        anim.cancel();
                    }
                    this.currentChangingAnimations.clear();
                    return;
                }
                return;
            case 2:
                if (this.currentAppearingAnimations.size() > 0) {
                    LinkedHashMap<View, Animator> currentAnimCopy2 = (LinkedHashMap) this.currentAppearingAnimations.clone();
                    for (Animator anim2 : currentAnimCopy2.values()) {
                        anim2.end();
                    }
                    this.currentAppearingAnimations.clear();
                    return;
                }
                return;
            case 3:
                if (this.currentDisappearingAnimations.size() > 0) {
                    LinkedHashMap<View, Animator> currentAnimCopy3 = (LinkedHashMap) this.currentDisappearingAnimations.clone();
                    for (Animator anim3 : currentAnimCopy3.values()) {
                        anim3.end();
                    }
                    this.currentDisappearingAnimations.clear();
                    return;
                }
                return;
            default:
                return;
        }
    }

    private void runAppearingTransition(final ViewGroup parent, final View child) {
        Animator currentAnimation = this.currentDisappearingAnimations.get(child);
        if (currentAnimation != null) {
            currentAnimation.cancel();
        }
        if (this.mAppearingAnim == null) {
            if (hasListeners()) {
                ArrayList<TransitionListener> listeners = (ArrayList) this.mListeners.clone();
                Iterator i$ = listeners.iterator();
                while (i$.hasNext()) {
                    TransitionListener listener = i$.next();
                    listener.endTransition(this, parent, child, 2);
                }
                return;
            }
            return;
        }
        Animator anim = this.mAppearingAnim.mo6clone();
        anim.setTarget(child);
        anim.setStartDelay(this.mAppearingDelay);
        anim.setDuration(this.mAppearingDuration);
        if (this.mAppearingInterpolator != sAppearingInterpolator) {
            anim.setInterpolator(this.mAppearingInterpolator);
        }
        if (anim instanceof ObjectAnimator) {
            ((ObjectAnimator) anim).setCurrentPlayTime(0L);
        }
        anim.addListener(new AnimatorListenerAdapter() { // from class: android.animation.LayoutTransition.5
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator anim2) {
                LayoutTransition.this.currentAppearingAnimations.remove(child);
                if (LayoutTransition.this.hasListeners()) {
                    ArrayList<TransitionListener> listeners2 = (ArrayList) LayoutTransition.this.mListeners.clone();
                    Iterator i$2 = listeners2.iterator();
                    while (i$2.hasNext()) {
                        TransitionListener listener2 = i$2.next();
                        listener2.endTransition(LayoutTransition.this, parent, child, 2);
                    }
                }
            }
        });
        this.currentAppearingAnimations.put(child, anim);
        anim.start();
    }

    private void runDisappearingTransition(final ViewGroup parent, final View child) {
        Animator currentAnimation = this.currentAppearingAnimations.get(child);
        if (currentAnimation != null) {
            currentAnimation.cancel();
        }
        if (this.mDisappearingAnim == null) {
            if (hasListeners()) {
                ArrayList<TransitionListener> listeners = (ArrayList) this.mListeners.clone();
                Iterator i$ = listeners.iterator();
                while (i$.hasNext()) {
                    TransitionListener listener = i$.next();
                    listener.endTransition(this, parent, child, 3);
                }
                return;
            }
            return;
        }
        Animator anim = this.mDisappearingAnim.mo6clone();
        anim.setStartDelay(this.mDisappearingDelay);
        anim.setDuration(this.mDisappearingDuration);
        if (this.mDisappearingInterpolator != sDisappearingInterpolator) {
            anim.setInterpolator(this.mDisappearingInterpolator);
        }
        anim.setTarget(child);
        final float preAnimAlpha = child.getAlpha();
        anim.addListener(new AnimatorListenerAdapter() { // from class: android.animation.LayoutTransition.6
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator anim2) {
                LayoutTransition.this.currentDisappearingAnimations.remove(child);
                child.setAlpha(preAnimAlpha);
                if (LayoutTransition.this.hasListeners()) {
                    ArrayList<TransitionListener> listeners2 = (ArrayList) LayoutTransition.this.mListeners.clone();
                    Iterator i$2 = listeners2.iterator();
                    while (i$2.hasNext()) {
                        TransitionListener listener2 = i$2.next();
                        listener2.endTransition(LayoutTransition.this, parent, child, 3);
                    }
                }
            }
        });
        if (anim instanceof ObjectAnimator) {
            ((ObjectAnimator) anim).setCurrentPlayTime(0L);
        }
        this.currentDisappearingAnimations.put(child, anim);
        anim.start();
    }

    private void addChild(ViewGroup parent, View child, boolean changesLayout) {
        if (parent.getWindowVisibility() != 0) {
            return;
        }
        if ((this.mTransitionTypes & 1) == 1) {
            cancel(3);
        }
        if (changesLayout && (this.mTransitionTypes & 4) == 4) {
            cancel(0);
            cancel(4);
        }
        if (hasListeners() && (this.mTransitionTypes & 1) == 1) {
            ArrayList<TransitionListener> listeners = (ArrayList) this.mListeners.clone();
            Iterator i$ = listeners.iterator();
            while (i$.hasNext()) {
                TransitionListener listener = i$.next();
                listener.startTransition(this, parent, child, 2);
            }
        }
        if (changesLayout && (this.mTransitionTypes & 4) == 4) {
            runChangeTransition(parent, child, 2);
        }
        if ((this.mTransitionTypes & 1) == 1) {
            runAppearingTransition(parent, child);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean hasListeners() {
        return this.mListeners != null && this.mListeners.size() > 0;
    }

    public void layoutChange(ViewGroup parent) {
        if (parent.getWindowVisibility() == 0 && (this.mTransitionTypes & 16) == 16 && !isRunning()) {
            runChangeTransition(parent, null, 4);
        }
    }

    public void addChild(ViewGroup parent, View child) {
        addChild(parent, child, true);
    }

    @Deprecated
    public void showChild(ViewGroup parent, View child) {
        addChild(parent, child, true);
    }

    public void showChild(ViewGroup parent, View child, int oldVisibility) {
        addChild(parent, child, oldVisibility == 8);
    }

    private void removeChild(ViewGroup parent, View child, boolean changesLayout) {
        if (parent.getWindowVisibility() != 0) {
            return;
        }
        if ((this.mTransitionTypes & 2) == 2) {
            cancel(2);
        }
        if (changesLayout && (this.mTransitionTypes & 8) == 8) {
            cancel(1);
            cancel(4);
        }
        if (hasListeners() && (this.mTransitionTypes & 2) == 2) {
            ArrayList<TransitionListener> listeners = (ArrayList) this.mListeners.clone();
            Iterator i$ = listeners.iterator();
            while (i$.hasNext()) {
                TransitionListener listener = i$.next();
                listener.startTransition(this, parent, child, 3);
            }
        }
        if (changesLayout && (this.mTransitionTypes & 8) == 8) {
            runChangeTransition(parent, child, 3);
        }
        if ((this.mTransitionTypes & 2) == 2) {
            runDisappearingTransition(parent, child);
        }
    }

    public void removeChild(ViewGroup parent, View child) {
        removeChild(parent, child, true);
    }

    @Deprecated
    public void hideChild(ViewGroup parent, View child) {
        removeChild(parent, child, true);
    }

    public void hideChild(ViewGroup parent, View child, int newVisibility) {
        removeChild(parent, child, newVisibility == 8);
    }

    public void addTransitionListener(TransitionListener listener) {
        if (this.mListeners == null) {
            this.mListeners = new ArrayList<>();
        }
        this.mListeners.add(listener);
    }

    public void removeTransitionListener(TransitionListener listener) {
        if (this.mListeners == null) {
            return;
        }
        this.mListeners.remove(listener);
    }

    public List<TransitionListener> getTransitionListeners() {
        return this.mListeners;
    }
}