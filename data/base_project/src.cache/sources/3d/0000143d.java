package android.transition;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.internal.R;

/* loaded from: Scene.class */
public final class Scene {
    private Context mContext;
    private int mLayoutId;
    private ViewGroup mSceneRoot;
    private ViewGroup mLayout;
    Runnable mEnterAction;
    Runnable mExitAction;
    private static ThreadLocal<SparseArray<Scene>> sScenes = new ThreadLocal<>();

    public static Scene getSceneForLayout(ViewGroup sceneRoot, int layoutId, Context context) {
        SparseArray<Scene> scenes = sScenes.get();
        if (scenes == null) {
            scenes = new SparseArray<>();
            sScenes.set(scenes);
        }
        Scene scene = scenes.get(layoutId);
        if (scene != null) {
            return scene;
        }
        Scene scene2 = new Scene(sceneRoot, layoutId, context);
        scenes.put(layoutId, scene2);
        return scene2;
    }

    public Scene(ViewGroup sceneRoot) {
        this.mLayoutId = -1;
        this.mSceneRoot = sceneRoot;
    }

    private Scene(ViewGroup sceneRoot, int layoutId, Context context) {
        this.mLayoutId = -1;
        this.mContext = context;
        this.mSceneRoot = sceneRoot;
        this.mLayoutId = layoutId;
    }

    public Scene(ViewGroup sceneRoot, ViewGroup layout) {
        this.mLayoutId = -1;
        this.mSceneRoot = sceneRoot;
        this.mLayout = layout;
    }

    public ViewGroup getSceneRoot() {
        return this.mSceneRoot;
    }

    public void exit() {
        if (getCurrentScene(this.mSceneRoot) == this && this.mExitAction != null) {
            this.mExitAction.run();
        }
    }

    public void enter() {
        if (this.mLayoutId > 0 || this.mLayout != null) {
            getSceneRoot().removeAllViews();
            if (this.mLayoutId > 0) {
                LayoutInflater.from(this.mContext).inflate(this.mLayoutId, this.mSceneRoot);
            } else {
                this.mSceneRoot.addView(this.mLayout);
            }
        }
        if (this.mEnterAction != null) {
            this.mEnterAction.run();
        }
        setCurrentScene(this.mSceneRoot, this);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void setCurrentScene(View view, Scene scene) {
        view.setTagInternal(R.id.current_scene, scene);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Scene getCurrentScene(View view) {
        return (Scene) view.getTag(R.id.current_scene);
    }

    public void setEnterAction(Runnable action) {
        this.mEnterAction = action;
    }

    public void setExitAction(Runnable action) {
        this.mExitAction = action;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isCreatedFromLayoutResource() {
        return this.mLayoutId > 0;
    }
}