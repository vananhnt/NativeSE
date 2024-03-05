package android.transition;

import android.transition.Transition;
import android.util.ArrayMap;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: TransitionManager.class */
public class TransitionManager {
    ArrayMap<Scene, Transition> mSceneTransitions = new ArrayMap<>();
    ArrayMap<Scene, ArrayMap<Scene, Transition>> mScenePairTransitions = new ArrayMap<>();
    private static String LOG_TAG = "TransitionManager";
    private static Transition sDefaultTransition = new AutoTransition();
    private static ThreadLocal<WeakReference<ArrayMap<ViewGroup, ArrayList<Transition>>>> sRunningTransitions = new ThreadLocal<>();
    private static ArrayList<ViewGroup> sPendingTransitions = new ArrayList<>();

    static /* synthetic */ ArrayMap access$100() {
        return getRunningTransitions();
    }

    public void setDefaultTransition(Transition transition) {
        sDefaultTransition = transition;
    }

    public static Transition getDefaultTransition() {
        return sDefaultTransition;
    }

    public void setTransition(Scene scene, Transition transition) {
        this.mSceneTransitions.put(scene, transition);
    }

    public void setTransition(Scene fromScene, Scene toScene, Transition transition) {
        ArrayMap<Scene, Transition> sceneTransitionMap = this.mScenePairTransitions.get(toScene);
        if (sceneTransitionMap == null) {
            sceneTransitionMap = new ArrayMap<>();
            this.mScenePairTransitions.put(toScene, sceneTransitionMap);
        }
        sceneTransitionMap.put(fromScene, transition);
    }

    private Transition getTransition(Scene scene) {
        Scene currScene;
        ArrayMap<Scene, Transition> sceneTransitionMap;
        Transition transition;
        ViewGroup sceneRoot = scene.getSceneRoot();
        if (sceneRoot != null && (currScene = Scene.getCurrentScene(sceneRoot)) != null && (sceneTransitionMap = this.mScenePairTransitions.get(scene)) != null && (transition = sceneTransitionMap.get(currScene)) != null) {
            return transition;
        }
        Transition transition2 = this.mSceneTransitions.get(scene);
        return transition2 != null ? transition2 : sDefaultTransition;
    }

    private static void changeScene(Scene scene, Transition transition) {
        ViewGroup sceneRoot = scene.getSceneRoot();
        Transition transitionClone = transition.mo879clone();
        transitionClone.setSceneRoot(sceneRoot);
        Scene oldScene = Scene.getCurrentScene(sceneRoot);
        if (oldScene != null && oldScene.isCreatedFromLayoutResource()) {
            transitionClone.setCanRemoveViews(true);
        }
        sceneChangeSetup(sceneRoot, transitionClone);
        scene.enter();
        sceneChangeRunTransition(sceneRoot, transitionClone);
    }

    private static ArrayMap<ViewGroup, ArrayList<Transition>> getRunningTransitions() {
        WeakReference<ArrayMap<ViewGroup, ArrayList<Transition>>> runningTransitions = sRunningTransitions.get();
        if (runningTransitions == null || runningTransitions.get() == null) {
            ArrayMap<ViewGroup, ArrayList<Transition>> transitions = new ArrayMap<>();
            runningTransitions = new WeakReference<>(transitions);
            sRunningTransitions.set(runningTransitions);
        }
        return runningTransitions.get();
    }

    private static void sceneChangeRunTransition(final ViewGroup sceneRoot, final Transition transition) {
        if (transition != null) {
            ViewTreeObserver observer = sceneRoot.getViewTreeObserver();
            ViewTreeObserver.OnPreDrawListener listener = new ViewTreeObserver.OnPreDrawListener() { // from class: android.transition.TransitionManager.1
                @Override // android.view.ViewTreeObserver.OnPreDrawListener
                public boolean onPreDraw() {
                    ViewGroup.this.getViewTreeObserver().removeOnPreDrawListener(this);
                    TransitionManager.sPendingTransitions.remove(ViewGroup.this);
                    final ArrayMap<ViewGroup, ArrayList<Transition>> runningTransitions = TransitionManager.access$100();
                    ArrayList<Transition> currentTransitions = runningTransitions.get(ViewGroup.this);
                    ArrayList<Transition> previousRunningTransitions = null;
                    if (currentTransitions == null) {
                        currentTransitions = new ArrayList<>();
                        runningTransitions.put(ViewGroup.this, currentTransitions);
                    } else if (currentTransitions.size() > 0) {
                        previousRunningTransitions = new ArrayList<>(currentTransitions);
                    }
                    currentTransitions.add(transition);
                    transition.addListener(new Transition.TransitionListenerAdapter() { // from class: android.transition.TransitionManager.1.1
                        @Override // android.transition.Transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
                        public void onTransitionEnd(Transition transition2) {
                            ArrayList<Transition> currentTransitions2 = (ArrayList) runningTransitions.get(ViewGroup.this);
                            currentTransitions2.remove(transition2);
                        }
                    });
                    transition.captureValues(ViewGroup.this, false);
                    if (previousRunningTransitions != null) {
                        Iterator i$ = previousRunningTransitions.iterator();
                        while (i$.hasNext()) {
                            Transition runningTransition = i$.next();
                            runningTransition.resume();
                        }
                    }
                    transition.playTransition(ViewGroup.this);
                    return true;
                }
            };
            observer.addOnPreDrawListener(listener);
        }
    }

    private static void sceneChangeSetup(ViewGroup sceneRoot, Transition transition) {
        ArrayList<Transition> runningTransitions = getRunningTransitions().get(sceneRoot);
        if (runningTransitions != null && runningTransitions.size() > 0) {
            Iterator i$ = runningTransitions.iterator();
            while (i$.hasNext()) {
                Transition runningTransition = i$.next();
                runningTransition.pause();
            }
        }
        if (transition != null) {
            transition.captureValues(sceneRoot, true);
        }
        Scene previousScene = Scene.getCurrentScene(sceneRoot);
        if (previousScene != null) {
            previousScene.exit();
        }
    }

    public void transitionTo(Scene scene) {
        changeScene(scene, getTransition(scene));
    }

    public static void go(Scene scene) {
        changeScene(scene, sDefaultTransition);
    }

    public static void go(Scene scene, Transition transition) {
        changeScene(scene, transition);
    }

    public static void beginDelayedTransition(ViewGroup sceneRoot) {
        beginDelayedTransition(sceneRoot, null);
    }

    public static void beginDelayedTransition(ViewGroup sceneRoot, Transition transition) {
        if (!sPendingTransitions.contains(sceneRoot) && sceneRoot.isLaidOut()) {
            sPendingTransitions.add(sceneRoot);
            if (transition == null) {
                transition = sDefaultTransition;
            }
            Transition transitionClone = transition.mo879clone();
            sceneChangeSetup(sceneRoot, transitionClone);
            Scene.setCurrentScene(sceneRoot, null);
            sceneChangeRunTransition(sceneRoot, transitionClone);
        }
    }
}