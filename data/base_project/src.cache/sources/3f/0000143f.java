package android.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.util.ArrayMap;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import gov.nist.core.Separators;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: Transition.class */
public abstract class Transition implements Cloneable {
    private static final String LOG_TAG = "Transition";
    static final boolean DBG = false;
    private static ThreadLocal<ArrayMap<Animator, AnimationInfo>> sRunningAnimators = new ThreadLocal<>();
    private String mName = getClass().getName();
    long mStartDelay = -1;
    long mDuration = -1;
    TimeInterpolator mInterpolator = null;
    ArrayList<Integer> mTargetIds = new ArrayList<>();
    ArrayList<View> mTargets = new ArrayList<>();
    ArrayList<Integer> mTargetIdExcludes = null;
    ArrayList<View> mTargetExcludes = null;
    ArrayList<Class> mTargetTypeExcludes = null;
    ArrayList<Integer> mTargetIdChildExcludes = null;
    ArrayList<View> mTargetChildExcludes = null;
    ArrayList<Class> mTargetTypeChildExcludes = null;
    private TransitionValuesMaps mStartValues = new TransitionValuesMaps();
    private TransitionValuesMaps mEndValues = new TransitionValuesMaps();
    TransitionSet mParent = null;
    ViewGroup mSceneRoot = null;
    boolean mCanRemoveViews = false;
    private ArrayList<Animator> mCurrentAnimators = new ArrayList<>();
    int mNumInstances = 0;
    boolean mPaused = false;
    private boolean mEnded = false;
    ArrayList<TransitionListener> mListeners = null;
    ArrayList<Animator> mAnimators = new ArrayList<>();

    /* loaded from: Transition$TransitionListener.class */
    public interface TransitionListener {
        void onTransitionStart(Transition transition);

        void onTransitionEnd(Transition transition);

        void onTransitionCancel(Transition transition);

        void onTransitionPause(Transition transition);

        void onTransitionResume(Transition transition);
    }

    public abstract void captureStartValues(TransitionValues transitionValues);

    public abstract void captureEndValues(TransitionValues transitionValues);

    public Transition setDuration(long duration) {
        this.mDuration = duration;
        return this;
    }

    public long getDuration() {
        return this.mDuration;
    }

    public Transition setStartDelay(long startDelay) {
        this.mStartDelay = startDelay;
        return this;
    }

    public long getStartDelay() {
        return this.mStartDelay;
    }

    public Transition setInterpolator(TimeInterpolator interpolator) {
        this.mInterpolator = interpolator;
        return this;
    }

    public TimeInterpolator getInterpolator() {
        return this.mInterpolator;
    }

    public String[] getTransitionProperties() {
        return null;
    }

    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void createAnimators(ViewGroup sceneRoot, TransitionValuesMaps startValues, TransitionValuesMaps endValues) {
        View view;
        ArrayMap<View, TransitionValues> endCopy = new ArrayMap<>(endValues.viewValues);
        SparseArray<TransitionValues> endIdCopy = new SparseArray<>(endValues.idValues.size());
        for (int i = 0; i < endValues.idValues.size(); i++) {
            endIdCopy.put(endValues.idValues.keyAt(i), endValues.idValues.valueAt(i));
        }
        LongSparseArray<TransitionValues> endItemIdCopy = new LongSparseArray<>(endValues.itemIdValues.size());
        for (int i2 = 0; i2 < endValues.itemIdValues.size(); i2++) {
            endItemIdCopy.put(endValues.itemIdValues.keyAt(i2), endValues.itemIdValues.valueAt(i2));
        }
        ArrayList<TransitionValues> startValuesList = new ArrayList<>();
        ArrayList<TransitionValues> endValuesList = new ArrayList<>();
        for (View view2 : startValues.viewValues.keySet()) {
            TransitionValues end = null;
            boolean isInListView = false;
            if (view2.getParent() instanceof ListView) {
                isInListView = true;
            }
            if (!isInListView) {
                int id = view2.getId();
                TransitionValues start = startValues.viewValues.get(view2) != null ? startValues.viewValues.get(view2) : startValues.idValues.get(id);
                if (endValues.viewValues.get(view2) != null) {
                    end = endValues.viewValues.get(view2);
                    endCopy.remove(view2);
                } else if (id != -1) {
                    end = endValues.idValues.get(id);
                    View removeView = null;
                    for (View viewToRemove : endCopy.keySet()) {
                        if (viewToRemove.getId() == id) {
                            removeView = viewToRemove;
                        }
                    }
                    if (removeView != null) {
                        endCopy.remove(removeView);
                    }
                }
                endIdCopy.remove(id);
                if (isValidTarget(view2, id)) {
                    startValuesList.add(start);
                    endValuesList.add(end);
                }
            } else {
                ListView parent = (ListView) view2.getParent();
                if (parent.getAdapter().hasStableIds()) {
                    int position = parent.getPositionForView(view2);
                    long itemId = parent.getItemIdAtPosition(position);
                    endItemIdCopy.remove(itemId);
                    startValuesList.add(startValues.itemIdValues.get(itemId));
                    endValuesList.add(null);
                }
            }
        }
        int startItemIdCopySize = startValues.itemIdValues.size();
        for (int i3 = 0; i3 < startItemIdCopySize; i3++) {
            long id2 = startValues.itemIdValues.keyAt(i3);
            if (isValidTarget(null, id2)) {
                TransitionValues start2 = startValues.itemIdValues.get(id2);
                endItemIdCopy.remove(id2);
                startValuesList.add(start2);
                endValuesList.add(endValues.itemIdValues.get(id2));
            }
        }
        for (View view3 : endCopy.keySet()) {
            int id3 = view3.getId();
            if (isValidTarget(view3, id3)) {
                TransitionValues start3 = startValues.viewValues.get(view3) != null ? startValues.viewValues.get(view3) : startValues.idValues.get(id3);
                endIdCopy.remove(id3);
                startValuesList.add(start3);
                endValuesList.add(endCopy.get(view3));
            }
        }
        int endIdCopySize = endIdCopy.size();
        for (int i4 = 0; i4 < endIdCopySize; i4++) {
            int id4 = endIdCopy.keyAt(i4);
            if (isValidTarget(null, id4)) {
                startValuesList.add(startValues.idValues.get(id4));
                endValuesList.add(endIdCopy.get(id4));
            }
        }
        int endItemIdCopySize = endItemIdCopy.size();
        for (int i5 = 0; i5 < endItemIdCopySize; i5++) {
            long id5 = endItemIdCopy.keyAt(i5);
            startValuesList.add(startValues.itemIdValues.get(id5));
            endValuesList.add(endItemIdCopy.get(id5));
        }
        ArrayMap<Animator, AnimationInfo> runningAnimators = getRunningAnimators();
        for (int i6 = 0; i6 < startValuesList.size(); i6++) {
            TransitionValues start4 = startValuesList.get(i6);
            TransitionValues end2 = endValuesList.get(i6);
            if ((start4 != null || end2 != null) && (start4 == null || !start4.equals(end2))) {
                Animator animator = createAnimator(sceneRoot, start4, end2);
                if (animator != null) {
                    TransitionValues infoValues = null;
                    if (end2 != null) {
                        view = end2.view;
                        String[] properties = getTransitionProperties();
                        if (view != null && properties != null && properties.length > 0) {
                            infoValues = new TransitionValues();
                            infoValues.view = view;
                            TransitionValues newValues = endValues.viewValues.get(view);
                            if (newValues != null) {
                                for (int j = 0; j < properties.length; j++) {
                                    infoValues.values.put(properties[j], newValues.values.get(properties[j]));
                                }
                            }
                            int numExistingAnims = runningAnimators.size();
                            int j2 = 0;
                            while (true) {
                                if (j2 >= numExistingAnims) {
                                    break;
                                }
                                Animator anim = runningAnimators.keyAt(j2);
                                AnimationInfo info = runningAnimators.get(anim);
                                if (info.values == null || info.view != view || (((info.name != null || getName() != null) && !info.name.equals(getName())) || !info.values.equals(infoValues))) {
                                    j2++;
                                } else {
                                    animator = null;
                                    break;
                                }
                            }
                        }
                    } else {
                        view = start4 != null ? start4.view : null;
                    }
                    if (animator != null) {
                        runningAnimators.put(animator, new AnimationInfo(view, getName(), infoValues));
                        this.mAnimators.add(animator);
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isValidTarget(View target, long targetId) {
        if (this.mTargetIdExcludes != null && this.mTargetIdExcludes.contains(Long.valueOf(targetId))) {
            return false;
        }
        if (this.mTargetExcludes != null && this.mTargetExcludes.contains(target)) {
            return false;
        }
        if (this.mTargetTypeExcludes != null && target != null) {
            int numTypes = this.mTargetTypeExcludes.size();
            for (int i = 0; i < numTypes; i++) {
                Class type = this.mTargetTypeExcludes.get(i);
                if (type.isInstance(target)) {
                    return false;
                }
            }
        }
        if (this.mTargetIds.size() == 0 && this.mTargets.size() == 0) {
            return true;
        }
        if (this.mTargetIds.size() > 0) {
            for (int i2 = 0; i2 < this.mTargetIds.size(); i2++) {
                if (this.mTargetIds.get(i2).intValue() == targetId) {
                    return true;
                }
            }
        }
        if (target != null && this.mTargets.size() > 0) {
            for (int i3 = 0; i3 < this.mTargets.size(); i3++) {
                if (this.mTargets.get(i3) == target) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private static ArrayMap<Animator, AnimationInfo> getRunningAnimators() {
        ArrayMap<Animator, AnimationInfo> runningAnimators = sRunningAnimators.get();
        if (runningAnimators == null) {
            runningAnimators = new ArrayMap<>();
            sRunningAnimators.set(runningAnimators);
        }
        return runningAnimators;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void runAnimators() {
        start();
        ArrayMap<Animator, AnimationInfo> runningAnimators = getRunningAnimators();
        Iterator i$ = this.mAnimators.iterator();
        while (i$.hasNext()) {
            Animator anim = i$.next();
            if (runningAnimators.containsKey(anim)) {
                start();
                runAnimator(anim, runningAnimators);
            }
        }
        this.mAnimators.clear();
        end();
    }

    private void runAnimator(Animator animator, final ArrayMap<Animator, AnimationInfo> runningAnimators) {
        if (animator != null) {
            animator.addListener(new AnimatorListenerAdapter() { // from class: android.transition.Transition.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animation) {
                    Transition.this.mCurrentAnimators.add(animation);
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    runningAnimators.remove(animation);
                    Transition.this.mCurrentAnimators.remove(animation);
                }
            });
            animate(animator);
        }
    }

    public Transition addTarget(int targetId) {
        if (targetId > 0) {
            this.mTargetIds.add(Integer.valueOf(targetId));
        }
        return this;
    }

    public Transition removeTarget(int targetId) {
        if (targetId > 0) {
            this.mTargetIds.remove(targetId);
        }
        return this;
    }

    public Transition excludeTarget(int targetId, boolean exclude) {
        this.mTargetIdExcludes = excludeId(this.mTargetIdExcludes, targetId, exclude);
        return this;
    }

    public Transition excludeChildren(int targetId, boolean exclude) {
        this.mTargetIdChildExcludes = excludeId(this.mTargetIdChildExcludes, targetId, exclude);
        return this;
    }

    private ArrayList<Integer> excludeId(ArrayList<Integer> list, int targetId, boolean exclude) {
        if (targetId > 0) {
            if (exclude) {
                list = ArrayListManager.add(list, Integer.valueOf(targetId));
            } else {
                list = ArrayListManager.remove(list, Integer.valueOf(targetId));
            }
        }
        return list;
    }

    public Transition excludeTarget(View target, boolean exclude) {
        this.mTargetExcludes = excludeView(this.mTargetExcludes, target, exclude);
        return this;
    }

    public Transition excludeChildren(View target, boolean exclude) {
        this.mTargetChildExcludes = excludeView(this.mTargetChildExcludes, target, exclude);
        return this;
    }

    private ArrayList<View> excludeView(ArrayList<View> list, View target, boolean exclude) {
        if (target != null) {
            if (exclude) {
                list = ArrayListManager.add(list, target);
            } else {
                list = ArrayListManager.remove(list, target);
            }
        }
        return list;
    }

    public Transition excludeTarget(Class type, boolean exclude) {
        this.mTargetTypeExcludes = excludeType(this.mTargetTypeExcludes, type, exclude);
        return this;
    }

    public Transition excludeChildren(Class type, boolean exclude) {
        this.mTargetTypeChildExcludes = excludeType(this.mTargetTypeChildExcludes, type, exclude);
        return this;
    }

    private ArrayList<Class> excludeType(ArrayList<Class> list, Class type, boolean exclude) {
        if (type != null) {
            if (exclude) {
                list = ArrayListManager.add(list, type);
            } else {
                list = ArrayListManager.remove(list, type);
            }
        }
        return list;
    }

    public Transition addTarget(View target) {
        this.mTargets.add(target);
        return this;
    }

    public Transition removeTarget(View target) {
        if (target != null) {
            this.mTargets.remove(target);
        }
        return this;
    }

    public List<Integer> getTargetIds() {
        return this.mTargetIds;
    }

    public List<View> getTargets() {
        return this.mTargets;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void captureValues(ViewGroup sceneRoot, boolean start) {
        if (start) {
            this.mStartValues.viewValues.clear();
            this.mStartValues.idValues.clear();
            this.mStartValues.itemIdValues.clear();
        } else {
            this.mEndValues.viewValues.clear();
            this.mEndValues.idValues.clear();
            this.mEndValues.itemIdValues.clear();
        }
        if (this.mTargetIds.size() > 0 || this.mTargets.size() > 0) {
            if (this.mTargetIds.size() > 0) {
                for (int i = 0; i < this.mTargetIds.size(); i++) {
                    int id = this.mTargetIds.get(i).intValue();
                    View view = sceneRoot.findViewById(id);
                    if (view != null) {
                        TransitionValues values = new TransitionValues();
                        values.view = view;
                        if (start) {
                            captureStartValues(values);
                        } else {
                            captureEndValues(values);
                        }
                        if (start) {
                            this.mStartValues.viewValues.put(view, values);
                            if (id >= 0) {
                                this.mStartValues.idValues.put(id, values);
                            }
                        } else {
                            this.mEndValues.viewValues.put(view, values);
                            if (id >= 0) {
                                this.mEndValues.idValues.put(id, values);
                            }
                        }
                    }
                }
            }
            if (this.mTargets.size() > 0) {
                for (int i2 = 0; i2 < this.mTargets.size(); i2++) {
                    View view2 = this.mTargets.get(i2);
                    if (view2 != null) {
                        TransitionValues values2 = new TransitionValues();
                        values2.view = view2;
                        if (start) {
                            captureStartValues(values2);
                        } else {
                            captureEndValues(values2);
                        }
                        if (start) {
                            this.mStartValues.viewValues.put(view2, values2);
                        } else {
                            this.mEndValues.viewValues.put(view2, values2);
                        }
                    }
                }
                return;
            }
            return;
        }
        captureHierarchy(sceneRoot, start);
    }

    private void captureHierarchy(View view, boolean start) {
        if (view == null) {
            return;
        }
        boolean isListViewItem = false;
        if (view.getParent() instanceof ListView) {
            isListViewItem = true;
        }
        if (isListViewItem && !((ListView) view.getParent()).getAdapter().hasStableIds()) {
            return;
        }
        int id = -1;
        long itemId = -1;
        if (!isListViewItem) {
            id = view.getId();
        } else {
            ListView listview = (ListView) view.getParent();
            int position = listview.getPositionForView(view);
            itemId = listview.getItemIdAtPosition(position);
            view.setHasTransientState(true);
        }
        if (this.mTargetIdExcludes != null && this.mTargetIdExcludes.contains(Integer.valueOf(id))) {
            return;
        }
        if (this.mTargetExcludes != null && this.mTargetExcludes.contains(view)) {
            return;
        }
        if (this.mTargetTypeExcludes != null && view != null) {
            int numTypes = this.mTargetTypeExcludes.size();
            for (int i = 0; i < numTypes; i++) {
                if (this.mTargetTypeExcludes.get(i).isInstance(view)) {
                    return;
                }
            }
        }
        TransitionValues values = new TransitionValues();
        values.view = view;
        if (start) {
            captureStartValues(values);
        } else {
            captureEndValues(values);
        }
        if (start) {
            if (!isListViewItem) {
                this.mStartValues.viewValues.put(view, values);
                if (id >= 0) {
                    this.mStartValues.idValues.put(id, values);
                }
            } else {
                this.mStartValues.itemIdValues.put(itemId, values);
            }
        } else if (!isListViewItem) {
            this.mEndValues.viewValues.put(view, values);
            if (id >= 0) {
                this.mEndValues.idValues.put(id, values);
            }
        } else {
            this.mEndValues.itemIdValues.put(itemId, values);
        }
        if (view instanceof ViewGroup) {
            if (this.mTargetIdChildExcludes != null && this.mTargetIdChildExcludes.contains(Integer.valueOf(id))) {
                return;
            }
            if (this.mTargetChildExcludes != null && this.mTargetChildExcludes.contains(view)) {
                return;
            }
            if (this.mTargetTypeChildExcludes != null && view != null) {
                int numTypes2 = this.mTargetTypeChildExcludes.size();
                for (int i2 = 0; i2 < numTypes2; i2++) {
                    if (this.mTargetTypeChildExcludes.get(i2).isInstance(view)) {
                        return;
                    }
                }
            }
            ViewGroup parent = (ViewGroup) view;
            for (int i3 = 0; i3 < parent.getChildCount(); i3++) {
                captureHierarchy(parent.getChildAt(i3), start);
            }
        }
    }

    public TransitionValues getTransitionValues(View view, boolean start) {
        if (this.mParent != null) {
            return this.mParent.getTransitionValues(view, start);
        }
        TransitionValuesMaps valuesMaps = start ? this.mStartValues : this.mEndValues;
        TransitionValues values = valuesMaps.viewValues.get(view);
        if (values == null) {
            int id = view.getId();
            if (id >= 0) {
                values = valuesMaps.idValues.get(id);
            }
            if (values == null && (view.getParent() instanceof ListView)) {
                ListView listview = (ListView) view.getParent();
                int position = listview.getPositionForView(view);
                long itemId = listview.getItemIdAtPosition(position);
                values = valuesMaps.itemIdValues.get(itemId);
            }
        }
        return values;
    }

    public void pause() {
        if (!this.mEnded) {
            ArrayMap<Animator, AnimationInfo> runningAnimators = getRunningAnimators();
            int numOldAnims = runningAnimators.size();
            for (int i = numOldAnims - 1; i >= 0; i--) {
                Animator anim = runningAnimators.keyAt(i);
                anim.pause();
            }
            if (this.mListeners != null && this.mListeners.size() > 0) {
                ArrayList<TransitionListener> tmpListeners = (ArrayList) this.mListeners.clone();
                int numListeners = tmpListeners.size();
                for (int i2 = 0; i2 < numListeners; i2++) {
                    tmpListeners.get(i2).onTransitionPause(this);
                }
            }
            this.mPaused = true;
        }
    }

    public void resume() {
        if (this.mPaused) {
            if (!this.mEnded) {
                ArrayMap<Animator, AnimationInfo> runningAnimators = getRunningAnimators();
                int numOldAnims = runningAnimators.size();
                for (int i = numOldAnims - 1; i >= 0; i--) {
                    Animator anim = runningAnimators.keyAt(i);
                    anim.resume();
                }
                if (this.mListeners != null && this.mListeners.size() > 0) {
                    ArrayList<TransitionListener> tmpListeners = (ArrayList) this.mListeners.clone();
                    int numListeners = tmpListeners.size();
                    for (int i2 = 0; i2 < numListeners; i2++) {
                        tmpListeners.get(i2).onTransitionResume(this);
                    }
                }
            }
            this.mPaused = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void playTransition(ViewGroup sceneRoot) {
        AnimationInfo oldInfo;
        ArrayMap<Animator, AnimationInfo> runningAnimators = getRunningAnimators();
        int numOldAnims = runningAnimators.size();
        for (int i = numOldAnims - 1; i >= 0; i--) {
            Animator anim = runningAnimators.keyAt(i);
            if (anim != null && (oldInfo = runningAnimators.get(anim)) != null) {
                boolean cancel = false;
                TransitionValues oldValues = oldInfo.values;
                View oldView = oldInfo.view;
                TransitionValues newValues = this.mEndValues.viewValues != null ? this.mEndValues.viewValues.get(oldView) : null;
                if (newValues == null) {
                    newValues = this.mEndValues.idValues.get(oldView.getId());
                }
                if (oldValues != null && newValues != null) {
                    Iterator i$ = oldValues.values.keySet().iterator();
                    while (true) {
                        if (!i$.hasNext()) {
                            break;
                        }
                        String key = i$.next();
                        Object oldValue = oldValues.values.get(key);
                        Object newValue = newValues.values.get(key);
                        if (oldValue != null && newValue != null && !oldValue.equals(newValue)) {
                            cancel = true;
                            break;
                        }
                    }
                }
                if (cancel) {
                    if (anim.isRunning() || anim.isStarted()) {
                        anim.cancel();
                    } else {
                        runningAnimators.remove(anim);
                    }
                }
            }
        }
        createAnimators(sceneRoot, this.mStartValues, this.mEndValues);
        runAnimators();
    }

    protected void animate(Animator animator) {
        if (animator == null) {
            end();
            return;
        }
        if (getDuration() >= 0) {
            animator.setDuration(getDuration());
        }
        if (getStartDelay() >= 0) {
            animator.setStartDelay(getStartDelay());
        }
        if (getInterpolator() != null) {
            animator.setInterpolator(getInterpolator());
        }
        animator.addListener(new AnimatorListenerAdapter() { // from class: android.transition.Transition.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                Transition.this.end();
                animation.removeListener(this);
            }
        });
        animator.start();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void start() {
        if (this.mNumInstances == 0) {
            if (this.mListeners != null && this.mListeners.size() > 0) {
                ArrayList<TransitionListener> tmpListeners = (ArrayList) this.mListeners.clone();
                int numListeners = tmpListeners.size();
                for (int i = 0; i < numListeners; i++) {
                    tmpListeners.get(i).onTransitionStart(this);
                }
            }
            this.mEnded = false;
        }
        this.mNumInstances++;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void end() {
        this.mNumInstances--;
        if (this.mNumInstances == 0) {
            if (this.mListeners != null && this.mListeners.size() > 0) {
                ArrayList<TransitionListener> tmpListeners = (ArrayList) this.mListeners.clone();
                int numListeners = tmpListeners.size();
                for (int i = 0; i < numListeners; i++) {
                    tmpListeners.get(i).onTransitionEnd(this);
                }
            }
            for (int i2 = 0; i2 < this.mStartValues.itemIdValues.size(); i2++) {
                TransitionValues tv = this.mStartValues.itemIdValues.valueAt(i2);
                View v = tv.view;
                if (v.hasTransientState()) {
                    v.setHasTransientState(false);
                }
            }
            for (int i3 = 0; i3 < this.mEndValues.itemIdValues.size(); i3++) {
                TransitionValues tv2 = this.mEndValues.itemIdValues.valueAt(i3);
                View v2 = tv2.view;
                if (v2.hasTransientState()) {
                    v2.setHasTransientState(false);
                }
            }
            this.mEnded = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void cancel() {
        int numAnimators = this.mCurrentAnimators.size();
        for (int i = numAnimators - 1; i >= 0; i--) {
            Animator animator = this.mCurrentAnimators.get(i);
            animator.cancel();
        }
        if (this.mListeners != null && this.mListeners.size() > 0) {
            ArrayList<TransitionListener> tmpListeners = (ArrayList) this.mListeners.clone();
            int numListeners = tmpListeners.size();
            for (int i2 = 0; i2 < numListeners; i2++) {
                tmpListeners.get(i2).onTransitionCancel(this);
            }
        }
    }

    public Transition addListener(TransitionListener listener) {
        if (this.mListeners == null) {
            this.mListeners = new ArrayList<>();
        }
        this.mListeners.add(listener);
        return this;
    }

    public Transition removeListener(TransitionListener listener) {
        if (this.mListeners == null) {
            return this;
        }
        this.mListeners.remove(listener);
        if (this.mListeners.size() == 0) {
            this.mListeners = null;
        }
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Transition setSceneRoot(ViewGroup sceneRoot) {
        this.mSceneRoot = sceneRoot;
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setCanRemoveViews(boolean canRemoveViews) {
        this.mCanRemoveViews = canRemoveViews;
    }

    public String toString() {
        return toString("");
    }

    @Override // 
    /* renamed from: clone */
    public Transition mo879clone() {
        Transition clone = null;
        try {
            clone = (Transition) super.clone();
            clone.mAnimators = new ArrayList<>();
            clone.mStartValues = new TransitionValuesMaps();
            clone.mEndValues = new TransitionValuesMaps();
        } catch (CloneNotSupportedException e) {
        }
        return clone;
    }

    public String getName() {
        return this.mName;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String toString(String indent) {
        String result = indent + getClass().getSimpleName() + Separators.AT + Integer.toHexString(hashCode()) + ": ";
        if (this.mDuration != -1) {
            result = result + "dur(" + this.mDuration + ") ";
        }
        if (this.mStartDelay != -1) {
            result = result + "dly(" + this.mStartDelay + ") ";
        }
        if (this.mInterpolator != null) {
            result = result + "interp(" + this.mInterpolator + ") ";
        }
        if (this.mTargetIds.size() > 0 || this.mTargets.size() > 0) {
            String result2 = result + "tgts(";
            if (this.mTargetIds.size() > 0) {
                for (int i = 0; i < this.mTargetIds.size(); i++) {
                    if (i > 0) {
                        result2 = result2 + ", ";
                    }
                    result2 = result2 + this.mTargetIds.get(i);
                }
            }
            if (this.mTargets.size() > 0) {
                for (int i2 = 0; i2 < this.mTargets.size(); i2++) {
                    if (i2 > 0) {
                        result2 = result2 + ", ";
                    }
                    result2 = result2 + this.mTargets.get(i2);
                }
            }
            result = result2 + Separators.RPAREN;
        }
        return result;
    }

    /* loaded from: Transition$TransitionListenerAdapter.class */
    public static class TransitionListenerAdapter implements TransitionListener {
        @Override // android.transition.Transition.TransitionListener
        public void onTransitionStart(Transition transition) {
        }

        @Override // android.transition.Transition.TransitionListener
        public void onTransitionEnd(Transition transition) {
        }

        @Override // android.transition.Transition.TransitionListener
        public void onTransitionCancel(Transition transition) {
        }

        @Override // android.transition.Transition.TransitionListener
        public void onTransitionPause(Transition transition) {
        }

        @Override // android.transition.Transition.TransitionListener
        public void onTransitionResume(Transition transition) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: Transition$AnimationInfo.class */
    public static class AnimationInfo {
        View view;
        String name;
        TransitionValues values;

        AnimationInfo(View view, String name, TransitionValues values) {
            this.view = view;
            this.name = name;
            this.values = values;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: Transition$ArrayListManager.class */
    public static class ArrayListManager {
        private ArrayListManager() {
        }

        static <T> ArrayList<T> add(ArrayList<T> list, T item) {
            if (list == null) {
                list = new ArrayList<>();
            }
            if (!list.contains(item)) {
                list.add(item);
            }
            return list;
        }

        static <T> ArrayList<T> remove(ArrayList<T> list, T item) {
            if (list != null) {
                list.remove(item);
                if (list.isEmpty()) {
                    list = null;
                }
            }
            return list;
        }
    }
}