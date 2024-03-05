package android.support.v4.app;

import android.graphics.Rect;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import java.util.ArrayList;
import java.util.Map;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: FragmentTransitionCompat21.class */
public class FragmentTransitionCompat21 {

    /* loaded from: FragmentTransitionCompat21$EpicenterView.class */
    public static class EpicenterView {
        public View epicenter;
    }

    /* loaded from: FragmentTransitionCompat21$ViewRetriever.class */
    public interface ViewRetriever {
        View getView();
    }

    FragmentTransitionCompat21() {
    }

    public static void addTargets(Object obj, ArrayList<View> arrayList) {
        Transition transition = (Transition) obj;
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            transition.addTarget(arrayList.get(i));
        }
    }

    public static void addTransitionTargets(Object obj, Object obj2, View view, ViewRetriever viewRetriever, View view2, EpicenterView epicenterView, Map<String, String> map, ArrayList<View> arrayList, Map<String, View> map2, ArrayList<View> arrayList2) {
        if (obj == null && obj2 == null) {
            return;
        }
        Transition transition = (Transition) obj;
        if (transition != null) {
            transition.addTarget(view2);
        }
        if (obj2 != null) {
            addTargets((Transition) obj2, arrayList2);
        }
        if (viewRetriever != null) {
            view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener(view, viewRetriever, map, map2, transition, arrayList) { // from class: android.support.v4.app.FragmentTransitionCompat21.2
                final View val$container;
                final Transition val$enterTransition;
                final ArrayList val$enteringViews;
                final ViewRetriever val$inFragment;
                final Map val$nameOverrides;
                final Map val$renamedViews;

                {
                    this.val$container = view;
                    this.val$inFragment = viewRetriever;
                    this.val$nameOverrides = map;
                    this.val$renamedViews = map2;
                    this.val$enterTransition = transition;
                    this.val$enteringViews = arrayList;
                }

                @Override // android.view.ViewTreeObserver.OnPreDrawListener
                public boolean onPreDraw() {
                    this.val$container.getViewTreeObserver().removeOnPreDrawListener(this);
                    View view3 = this.val$inFragment.getView();
                    if (view3 != null) {
                        if (!this.val$nameOverrides.isEmpty()) {
                            FragmentTransitionCompat21.findNamedViews(this.val$renamedViews, view3);
                            this.val$renamedViews.keySet().retainAll(this.val$nameOverrides.values());
                            for (Map.Entry entry : this.val$nameOverrides.entrySet()) {
                                View view4 = (View) this.val$renamedViews.get((String) entry.getValue());
                                if (view4 != null) {
                                    view4.setTransitionName((String) entry.getKey());
                                }
                            }
                        }
                        if (this.val$enterTransition != null) {
                            FragmentTransitionCompat21.captureTransitioningViews(this.val$enteringViews, view3);
                            this.val$enteringViews.removeAll(this.val$renamedViews.values());
                            FragmentTransitionCompat21.addTargets(this.val$enterTransition, this.val$enteringViews);
                            return true;
                        }
                        return true;
                    }
                    return true;
                }
            });
        }
        setSharedElementEpicenter(transition, epicenterView);
    }

    public static void beginDelayedTransition(ViewGroup viewGroup, Object obj) {
        TransitionManager.beginDelayedTransition(viewGroup, (Transition) obj);
    }

    public static Object captureExitingViews(Object obj, View view, ArrayList<View> arrayList, Map<String, View> map) {
        if (obj != null) {
            captureTransitioningViews(arrayList, view);
            if (map != null) {
                arrayList.removeAll(map.values());
            }
            if (arrayList.isEmpty()) {
                obj = null;
            } else {
                addTargets((Transition) obj, arrayList);
            }
        }
        return obj;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void captureTransitioningViews(ArrayList<View> arrayList, View view) {
        if (view.getVisibility() == 0) {
            if (!(view instanceof ViewGroup)) {
                arrayList.add(view);
                return;
            }
            ViewGroup viewGroup = (ViewGroup) view;
            if (viewGroup.isTransitionGroup()) {
                arrayList.add(viewGroup);
                return;
            }
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                captureTransitioningViews(arrayList, viewGroup.getChildAt(i));
            }
        }
    }

    public static void cleanupTransitions(View view, View view2, Object obj, ArrayList<View> arrayList, Object obj2, ArrayList<View> arrayList2, Object obj3, ArrayList<View> arrayList3, Object obj4, ArrayList<View> arrayList4, Map<String, View> map) {
        Transition transition = (Transition) obj;
        Transition transition2 = (Transition) obj2;
        Transition transition3 = (Transition) obj3;
        Transition transition4 = (Transition) obj4;
        if (transition4 != null) {
            view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener(view, transition, view2, arrayList, transition2, arrayList2, transition3, arrayList3, map, arrayList4, transition4) { // from class: android.support.v4.app.FragmentTransitionCompat21.4
                final Transition val$enterTransition;
                final ArrayList val$enteringViews;
                final Transition val$exitTransition;
                final ArrayList val$exitingViews;
                final ArrayList val$hiddenViews;
                final View val$nonExistentView;
                final Transition val$overallTransition;
                final Map val$renamedViews;
                final View val$sceneRoot;
                final ArrayList val$sharedElementTargets;
                final Transition val$sharedElementTransition;

                {
                    this.val$sceneRoot = view;
                    this.val$enterTransition = transition;
                    this.val$nonExistentView = view2;
                    this.val$enteringViews = arrayList;
                    this.val$exitTransition = transition2;
                    this.val$exitingViews = arrayList2;
                    this.val$sharedElementTransition = transition3;
                    this.val$sharedElementTargets = arrayList3;
                    this.val$renamedViews = map;
                    this.val$hiddenViews = arrayList4;
                    this.val$overallTransition = transition4;
                }

                @Override // android.view.ViewTreeObserver.OnPreDrawListener
                public boolean onPreDraw() {
                    this.val$sceneRoot.getViewTreeObserver().removeOnPreDrawListener(this);
                    Transition transition5 = this.val$enterTransition;
                    if (transition5 != null) {
                        transition5.removeTarget(this.val$nonExistentView);
                        FragmentTransitionCompat21.removeTargets(this.val$enterTransition, this.val$enteringViews);
                    }
                    Transition transition6 = this.val$exitTransition;
                    if (transition6 != null) {
                        FragmentTransitionCompat21.removeTargets(transition6, this.val$exitingViews);
                    }
                    Transition transition7 = this.val$sharedElementTransition;
                    if (transition7 != null) {
                        FragmentTransitionCompat21.removeTargets(transition7, this.val$sharedElementTargets);
                    }
                    for (Map.Entry entry : this.val$renamedViews.entrySet()) {
                        ((View) entry.getValue()).setTransitionName((String) entry.getKey());
                    }
                    int size = this.val$hiddenViews.size();
                    for (int i = 0; i < size; i++) {
                        this.val$overallTransition.excludeTarget((View) this.val$hiddenViews.get(i), false);
                    }
                    this.val$overallTransition.excludeTarget(this.val$nonExistentView, false);
                    return true;
                }
            });
        }
    }

    public static Object cloneTransition(Object obj) {
        if (obj != null) {
            obj = ((Transition) obj).mo879clone();
        }
        return obj;
    }

    public static void excludeTarget(Object obj, View view, boolean z) {
        ((Transition) obj).excludeTarget(view, z);
    }

    public static void findNamedViews(Map<String, View> map, View view) {
        if (view.getVisibility() == 0) {
            String transitionName = view.getTransitionName();
            if (transitionName != null) {
                map.put(transitionName, view);
            }
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                int childCount = viewGroup.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    findNamedViews(map, viewGroup.getChildAt(i));
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Rect getBoundsOnScreen(View view) {
        Rect rect = new Rect();
        int[] iArr = new int[2];
        view.getLocationOnScreen(iArr);
        rect.set(iArr[0], iArr[1], iArr[0] + view.getWidth(), iArr[1] + view.getHeight());
        return rect;
    }

    public static String getTransitionName(View view) {
        return view.getTransitionName();
    }

    public static Object mergeTransitions(Object obj, Object obj2, Object obj3, boolean z) {
        TransitionSet transitionSet = (Transition) obj;
        Transition transition = (Transition) obj2;
        Transition transition2 = (Transition) obj3;
        if (transitionSet == null || transition == null) {
            z = true;
        }
        if (z) {
            TransitionSet transitionSet2 = new TransitionSet();
            if (transitionSet != null) {
                transitionSet2.addTransition(transitionSet);
            }
            if (transition != null) {
                transitionSet2.addTransition(transition);
            }
            if (transition2 != null) {
                transitionSet2.addTransition(transition2);
            }
            transitionSet = transitionSet2;
        } else {
            if (transition != null && transitionSet != null) {
                transitionSet = new TransitionSet().addTransition(transition).addTransition(transitionSet).setOrdering(1);
            } else if (transition != null) {
                transitionSet = transition;
            } else if (transitionSet == null) {
                transitionSet = null;
            }
            if (transition2 != null) {
                TransitionSet transitionSet3 = new TransitionSet();
                if (transitionSet != null) {
                    transitionSet3.addTransition(transitionSet);
                }
                transitionSet3.addTransition(transition2);
                transitionSet = transitionSet3;
            }
        }
        return transitionSet;
    }

    public static void removeTargets(Object obj, ArrayList<View> arrayList) {
        Transition transition = (Transition) obj;
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            transition.removeTarget(arrayList.get(i));
        }
    }

    public static void setEpicenter(Object obj, View view) {
        ((Transition) obj).setEpicenterCallback(new Transition.EpicenterCallback(getBoundsOnScreen(view)) { // from class: android.support.v4.app.FragmentTransitionCompat21.1
            final Rect val$epicenter;

            {
                this.val$epicenter = r4;
            }

            @Override // android.transition.Transition.EpicenterCallback
            public Rect onGetEpicenter(Transition transition) {
                return this.val$epicenter;
            }
        });
    }

    private static void setSharedElementEpicenter(Transition transition, EpicenterView epicenterView) {
        if (transition != null) {
            transition.setEpicenterCallback(new Transition.EpicenterCallback(epicenterView) { // from class: android.support.v4.app.FragmentTransitionCompat21.3
                private Rect mEpicenter;
                final EpicenterView val$epicenterView;

                {
                    this.val$epicenterView = epicenterView;
                }

                @Override // android.transition.Transition.EpicenterCallback
                public Rect onGetEpicenter(Transition transition2) {
                    if (this.mEpicenter == null && this.val$epicenterView.epicenter != null) {
                        this.mEpicenter = FragmentTransitionCompat21.getBoundsOnScreen(this.val$epicenterView.epicenter);
                    }
                    return this.mEpicenter;
                }
            });
        }
    }
}