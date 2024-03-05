package android.transition;

import android.animation.TimeInterpolator;
import android.transition.Transition;
import android.util.AndroidRuntimeException;
import android.view.View;
import android.view.ViewGroup;
import gov.nist.core.Separators;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: TransitionSet.class */
public class TransitionSet extends Transition {
    int mCurrentListeners;
    public static final int ORDERING_TOGETHER = 0;
    public static final int ORDERING_SEQUENTIAL = 1;
    ArrayList<Transition> mTransitions = new ArrayList<>();
    private boolean mPlayTogether = true;
    boolean mStarted = false;

    public TransitionSet setOrdering(int ordering) {
        switch (ordering) {
            case 0:
                this.mPlayTogether = true;
                break;
            case 1:
                this.mPlayTogether = false;
                break;
            default:
                throw new AndroidRuntimeException("Invalid parameter for TransitionSet ordering: " + ordering);
        }
        return this;
    }

    public int getOrdering() {
        return this.mPlayTogether ? 0 : 1;
    }

    public TransitionSet addTransition(Transition transition) {
        if (transition != null) {
            this.mTransitions.add(transition);
            transition.mParent = this;
            if (this.mDuration >= 0) {
                transition.setDuration(this.mDuration);
            }
        }
        return this;
    }

    @Override // android.transition.Transition
    public TransitionSet setDuration(long duration) {
        super.setDuration(duration);
        if (this.mDuration >= 0) {
            int numTransitions = this.mTransitions.size();
            for (int i = 0; i < numTransitions; i++) {
                this.mTransitions.get(i).setDuration(duration);
            }
        }
        return this;
    }

    @Override // android.transition.Transition
    public TransitionSet setStartDelay(long startDelay) {
        return (TransitionSet) super.setStartDelay(startDelay);
    }

    @Override // android.transition.Transition
    public TransitionSet setInterpolator(TimeInterpolator interpolator) {
        return (TransitionSet) super.setInterpolator(interpolator);
    }

    @Override // android.transition.Transition
    public TransitionSet addTarget(View target) {
        return (TransitionSet) super.addTarget(target);
    }

    @Override // android.transition.Transition
    public TransitionSet addTarget(int targetId) {
        return (TransitionSet) super.addTarget(targetId);
    }

    @Override // android.transition.Transition
    public TransitionSet addListener(Transition.TransitionListener listener) {
        return (TransitionSet) super.addListener(listener);
    }

    @Override // android.transition.Transition
    public TransitionSet removeTarget(int targetId) {
        return (TransitionSet) super.removeTarget(targetId);
    }

    @Override // android.transition.Transition
    public TransitionSet removeTarget(View target) {
        return (TransitionSet) super.removeTarget(target);
    }

    @Override // android.transition.Transition
    public TransitionSet removeListener(Transition.TransitionListener listener) {
        return (TransitionSet) super.removeListener(listener);
    }

    public TransitionSet removeTransition(Transition transition) {
        this.mTransitions.remove(transition);
        transition.mParent = null;
        return this;
    }

    private void setupStartEndListeners() {
        TransitionSetListener listener = new TransitionSetListener(this);
        Iterator i$ = this.mTransitions.iterator();
        while (i$.hasNext()) {
            Transition childTransition = i$.next();
            childTransition.addListener(listener);
        }
        this.mCurrentListeners = this.mTransitions.size();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: TransitionSet$TransitionSetListener.class */
    public static class TransitionSetListener extends Transition.TransitionListenerAdapter {
        TransitionSet mTransitionSet;

        TransitionSetListener(TransitionSet transitionSet) {
            this.mTransitionSet = transitionSet;
        }

        @Override // android.transition.Transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
        public void onTransitionStart(Transition transition) {
            if (!this.mTransitionSet.mStarted) {
                this.mTransitionSet.start();
                this.mTransitionSet.mStarted = true;
            }
        }

        @Override // android.transition.Transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
        public void onTransitionEnd(Transition transition) {
            this.mTransitionSet.mCurrentListeners--;
            if (this.mTransitionSet.mCurrentListeners == 0) {
                this.mTransitionSet.mStarted = false;
                this.mTransitionSet.end();
            }
            transition.removeListener(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.transition.Transition
    public void createAnimators(ViewGroup sceneRoot, TransitionValuesMaps startValues, TransitionValuesMaps endValues) {
        Iterator i$ = this.mTransitions.iterator();
        while (i$.hasNext()) {
            Transition childTransition = i$.next();
            childTransition.createAnimators(sceneRoot, startValues, endValues);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.transition.Transition
    public void runAnimators() {
        setupStartEndListeners();
        if (!this.mPlayTogether) {
            for (int i = 1; i < this.mTransitions.size(); i++) {
                Transition previousTransition = this.mTransitions.get(i - 1);
                final Transition nextTransition = this.mTransitions.get(i);
                previousTransition.addListener(new Transition.TransitionListenerAdapter() { // from class: android.transition.TransitionSet.1
                    @Override // android.transition.Transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
                    public void onTransitionEnd(Transition transition) {
                        nextTransition.runAnimators();
                        transition.removeListener(this);
                    }
                });
            }
            Transition firstTransition = this.mTransitions.get(0);
            if (firstTransition != null) {
                firstTransition.runAnimators();
                return;
            }
            return;
        }
        Iterator i$ = this.mTransitions.iterator();
        while (i$.hasNext()) {
            Transition childTransition = i$.next();
            childTransition.runAnimators();
        }
    }

    @Override // android.transition.Transition
    public void captureStartValues(TransitionValues transitionValues) {
        int targetId = transitionValues.view.getId();
        if (isValidTarget(transitionValues.view, targetId)) {
            Iterator i$ = this.mTransitions.iterator();
            while (i$.hasNext()) {
                Transition childTransition = i$.next();
                if (childTransition.isValidTarget(transitionValues.view, targetId)) {
                    childTransition.captureStartValues(transitionValues);
                }
            }
        }
    }

    @Override // android.transition.Transition
    public void captureEndValues(TransitionValues transitionValues) {
        int targetId = transitionValues.view.getId();
        if (isValidTarget(transitionValues.view, targetId)) {
            Iterator i$ = this.mTransitions.iterator();
            while (i$.hasNext()) {
                Transition childTransition = i$.next();
                if (childTransition.isValidTarget(transitionValues.view, targetId)) {
                    childTransition.captureEndValues(transitionValues);
                }
            }
        }
    }

    @Override // android.transition.Transition
    public void pause() {
        super.pause();
        int numTransitions = this.mTransitions.size();
        for (int i = 0; i < numTransitions; i++) {
            this.mTransitions.get(i).pause();
        }
    }

    @Override // android.transition.Transition
    public void resume() {
        super.resume();
        int numTransitions = this.mTransitions.size();
        for (int i = 0; i < numTransitions; i++) {
            this.mTransitions.get(i).resume();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.transition.Transition
    public void cancel() {
        super.cancel();
        int numTransitions = this.mTransitions.size();
        for (int i = 0; i < numTransitions; i++) {
            this.mTransitions.get(i).cancel();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.transition.Transition
    public TransitionSet setSceneRoot(ViewGroup sceneRoot) {
        super.setSceneRoot(sceneRoot);
        int numTransitions = this.mTransitions.size();
        for (int i = 0; i < numTransitions; i++) {
            this.mTransitions.get(i).setSceneRoot(sceneRoot);
        }
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.transition.Transition
    public void setCanRemoveViews(boolean canRemoveViews) {
        super.setCanRemoveViews(canRemoveViews);
        int numTransitions = this.mTransitions.size();
        for (int i = 0; i < numTransitions; i++) {
            this.mTransitions.get(i).setCanRemoveViews(canRemoveViews);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.transition.Transition
    public String toString(String indent) {
        String result = super.toString(indent);
        for (int i = 0; i < this.mTransitions.size(); i++) {
            result = result + Separators.RETURN + this.mTransitions.get(i).toString(indent + "  ");
        }
        return result;
    }

    @Override // android.transition.Transition
    /* renamed from: clone */
    public TransitionSet mo879clone() {
        TransitionSet clone = (TransitionSet) super.mo879clone();
        clone.mTransitions = new ArrayList<>();
        int numTransitions = this.mTransitions.size();
        for (int i = 0; i < numTransitions; i++) {
            clone.addTransition(this.mTransitions.get(i).mo879clone());
        }
        return clone;
    }
}