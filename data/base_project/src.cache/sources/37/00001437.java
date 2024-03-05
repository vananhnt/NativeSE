package android.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/* loaded from: Fade.class */
public class Fade extends Visibility {
    private static boolean DBG = false;
    private static final String LOG_TAG = "Fade";
    private static final String PROPNAME_SCREEN_X = "android:fade:screenX";
    private static final String PROPNAME_SCREEN_Y = "android:fade:screenY";
    public static final int IN = 1;
    public static final int OUT = 2;
    private int mFadingMode;

    public Fade() {
        this(3);
    }

    public Fade(int fadingMode) {
        this.mFadingMode = fadingMode;
    }

    private Animator createAnimation(View view, float startAlpha, float endAlpha, AnimatorListenerAdapter listener) {
        if (startAlpha == endAlpha) {
            if (listener != null) {
                listener.onAnimationEnd(null);
                return null;
            }
            return null;
        }
        ObjectAnimator anim = ObjectAnimator.ofFloat(view, "transitionAlpha", startAlpha, endAlpha);
        if (DBG) {
            Log.d(LOG_TAG, "Created animator " + anim);
        }
        if (listener != null) {
            anim.addListener(listener);
            anim.addPauseListener(listener);
        }
        return anim;
    }

    private void captureValues(TransitionValues transitionValues) {
        int[] loc = new int[2];
        transitionValues.view.getLocationOnScreen(loc);
        transitionValues.values.put(PROPNAME_SCREEN_X, Integer.valueOf(loc[0]));
        transitionValues.values.put(PROPNAME_SCREEN_Y, Integer.valueOf(loc[1]));
    }

    @Override // android.transition.Visibility, android.transition.Transition
    public void captureStartValues(TransitionValues transitionValues) {
        super.captureStartValues(transitionValues);
        captureValues(transitionValues);
    }

    @Override // android.transition.Visibility
    public Animator onAppear(ViewGroup sceneRoot, TransitionValues startValues, int startVisibility, TransitionValues endValues, int endVisibility) {
        if ((this.mFadingMode & 1) != 1 || endValues == null) {
            return null;
        }
        final View endView = endValues.view;
        if (DBG) {
            View startView = startValues != null ? startValues.view : null;
            Log.d(LOG_TAG, "Fade.onAppear: startView, startVis, endView, endVis = " + startView + ", " + startVisibility + ", " + endView + ", " + endVisibility);
        }
        endView.setTransitionAlpha(0.0f);
        Transition.TransitionListener transitionListener = new Transition.TransitionListenerAdapter() { // from class: android.transition.Fade.1
            boolean mCanceled = false;
            float mPausedAlpha;

            @Override // android.transition.Transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
            public void onTransitionCancel(Transition transition) {
                endView.setTransitionAlpha(1.0f);
                this.mCanceled = true;
            }

            @Override // android.transition.Transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
            public void onTransitionEnd(Transition transition) {
                if (!this.mCanceled) {
                    endView.setTransitionAlpha(1.0f);
                }
            }

            @Override // android.transition.Transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
            public void onTransitionPause(Transition transition) {
                this.mPausedAlpha = endView.getTransitionAlpha();
                endView.setTransitionAlpha(1.0f);
            }

            @Override // android.transition.Transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
            public void onTransitionResume(Transition transition) {
                endView.setTransitionAlpha(this.mPausedAlpha);
            }
        };
        addListener(transitionListener);
        return createAnimation(endView, 0.0f, 1.0f, null);
    }

    @Override // android.transition.Visibility
    public Animator onDisappear(final ViewGroup sceneRoot, TransitionValues startValues, int startVisibility, TransitionValues endValues, final int endVisibility) {
        if ((this.mFadingMode & 2) != 2) {
            return null;
        }
        View view = null;
        View startView = startValues != null ? startValues.view : null;
        View endView = endValues != null ? endValues.view : null;
        if (DBG) {
            Log.d(LOG_TAG, "Fade.onDisappear: startView, startVis, endView, endVis = " + startView + ", " + startVisibility + ", " + endView + ", " + endVisibility);
        }
        View overlayView = null;
        View viewToKeep = null;
        if (endView == null || endView.getParent() == null) {
            if (endView != null) {
                overlayView = endView;
                view = endView;
            } else if (startView != null) {
                if (startView.getParent() == null) {
                    overlayView = startView;
                    view = startView;
                } else if ((startView.getParent() instanceof View) && startView.getParent().getParent() == null) {
                    View startParent = (View) startView.getParent();
                    int id = startParent.getId();
                    if (id != -1 && sceneRoot.findViewById(id) != null && this.mCanRemoveViews) {
                        overlayView = startView;
                        view = startView;
                    }
                }
            }
        } else if (endVisibility == 4) {
            view = endView;
            viewToKeep = view;
        } else if (startView == endView) {
            view = endView;
            viewToKeep = view;
        } else {
            view = startView;
            overlayView = view;
        }
        if (overlayView != null) {
            int screenX = ((Integer) startValues.values.get(PROPNAME_SCREEN_X)).intValue();
            int screenY = ((Integer) startValues.values.get(PROPNAME_SCREEN_Y)).intValue();
            int[] loc = new int[2];
            sceneRoot.getLocationOnScreen(loc);
            overlayView.offsetLeftAndRight((screenX - loc[0]) - overlayView.getLeft());
            overlayView.offsetTopAndBottom((screenY - loc[1]) - overlayView.getTop());
            sceneRoot.getOverlay().add(overlayView);
            final View finalView = view;
            final View finalOverlayView = overlayView;
            final View finalViewToKeep = viewToKeep;
            AnimatorListenerAdapter endListener = new AnimatorListenerAdapter() { // from class: android.transition.Fade.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    finalView.setTransitionAlpha(1.0f);
                    if (finalViewToKeep != null) {
                        finalViewToKeep.setVisibility(endVisibility);
                    }
                    if (finalOverlayView != null) {
                        sceneRoot.getOverlay().remove(finalOverlayView);
                    }
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorPauseListener
                public void onAnimationPause(Animator animation) {
                    if (finalOverlayView != null) {
                        sceneRoot.getOverlay().remove(finalOverlayView);
                    }
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorPauseListener
                public void onAnimationResume(Animator animation) {
                    if (finalOverlayView != null) {
                        sceneRoot.getOverlay().add(finalOverlayView);
                    }
                }
            };
            return createAnimation(view, 1.0f, 0.0f, endListener);
        } else if (viewToKeep != null) {
            viewToKeep.setVisibility(0);
            final View finalView2 = view;
            final View finalOverlayView2 = overlayView;
            final View finalViewToKeep2 = viewToKeep;
            AnimatorListenerAdapter endListener2 = new AnimatorListenerAdapter() { // from class: android.transition.Fade.3
                boolean mCanceled = false;
                float mPausedAlpha = -1.0f;

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorPauseListener
                public void onAnimationPause(Animator animation) {
                    if (finalViewToKeep2 != null && !this.mCanceled) {
                        finalViewToKeep2.setVisibility(endVisibility);
                    }
                    this.mPausedAlpha = finalView2.getTransitionAlpha();
                    finalView2.setTransitionAlpha(1.0f);
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorPauseListener
                public void onAnimationResume(Animator animation) {
                    if (finalViewToKeep2 != null && !this.mCanceled) {
                        finalViewToKeep2.setVisibility(0);
                    }
                    finalView2.setTransitionAlpha(this.mPausedAlpha);
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animation) {
                    this.mCanceled = true;
                    if (this.mPausedAlpha >= 0.0f) {
                        finalView2.setTransitionAlpha(this.mPausedAlpha);
                    }
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (!this.mCanceled) {
                        finalView2.setTransitionAlpha(1.0f);
                    }
                    if (finalViewToKeep2 != null && !this.mCanceled) {
                        finalViewToKeep2.setVisibility(endVisibility);
                    }
                    if (finalOverlayView2 != null) {
                        sceneRoot.getOverlay().remove(finalOverlayView2);
                    }
                }
            };
            return createAnimation(view, 1.0f, 0.0f, endListener2);
        } else {
            return null;
        }
    }
}