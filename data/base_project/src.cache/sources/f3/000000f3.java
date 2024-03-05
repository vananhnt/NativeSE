package android.app;

import android.content.ComponentCallbacks;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.os.Bundle;
import java.util.ArrayList;

/* loaded from: Application.class */
public class Application extends ContextWrapper implements ComponentCallbacks2 {
    private ArrayList<ComponentCallbacks> mComponentCallbacks;
    private ArrayList<ActivityLifecycleCallbacks> mActivityLifecycleCallbacks;
    private ArrayList<OnProvideAssistDataListener> mAssistCallbacks;
    public LoadedApk mLoadedApk;

    /* loaded from: Application$ActivityLifecycleCallbacks.class */
    public interface ActivityLifecycleCallbacks {
        void onActivityCreated(Activity activity, Bundle bundle);

        void onActivityStarted(Activity activity);

        void onActivityResumed(Activity activity);

        void onActivityPaused(Activity activity);

        void onActivityStopped(Activity activity);

        void onActivitySaveInstanceState(Activity activity, Bundle bundle);

        void onActivityDestroyed(Activity activity);
    }

    /* loaded from: Application$OnProvideAssistDataListener.class */
    public interface OnProvideAssistDataListener {
        void onProvideAssistData(Activity activity, Bundle bundle);
    }

    public Application() {
        super(null);
        this.mComponentCallbacks = new ArrayList<>();
        this.mActivityLifecycleCallbacks = new ArrayList<>();
        this.mAssistCallbacks = null;
    }

    public void onCreate() {
    }

    public void onTerminate() {
    }

    @Override // android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        Object[] callbacks = collectComponentCallbacks();
        if (callbacks != null) {
            for (Object obj : callbacks) {
                ((ComponentCallbacks) obj).onConfigurationChanged(newConfig);
            }
        }
    }

    @Override // android.content.ComponentCallbacks
    public void onLowMemory() {
        Object[] callbacks = collectComponentCallbacks();
        if (callbacks != null) {
            for (Object obj : callbacks) {
                ((ComponentCallbacks) obj).onLowMemory();
            }
        }
    }

    @Override // android.content.ComponentCallbacks2
    public void onTrimMemory(int level) {
        Object[] callbacks = collectComponentCallbacks();
        if (callbacks != null) {
            for (Object c : callbacks) {
                if (c instanceof ComponentCallbacks2) {
                    ((ComponentCallbacks2) c).onTrimMemory(level);
                }
            }
        }
    }

    @Override // android.content.Context
    public void registerComponentCallbacks(ComponentCallbacks callback) {
        synchronized (this.mComponentCallbacks) {
            this.mComponentCallbacks.add(callback);
        }
    }

    @Override // android.content.Context
    public void unregisterComponentCallbacks(ComponentCallbacks callback) {
        synchronized (this.mComponentCallbacks) {
            this.mComponentCallbacks.remove(callback);
        }
    }

    public void registerActivityLifecycleCallbacks(ActivityLifecycleCallbacks callback) {
        synchronized (this.mActivityLifecycleCallbacks) {
            this.mActivityLifecycleCallbacks.add(callback);
        }
    }

    public void unregisterActivityLifecycleCallbacks(ActivityLifecycleCallbacks callback) {
        synchronized (this.mActivityLifecycleCallbacks) {
            this.mActivityLifecycleCallbacks.remove(callback);
        }
    }

    public void registerOnProvideAssistDataListener(OnProvideAssistDataListener callback) {
        synchronized (this) {
            if (this.mAssistCallbacks == null) {
                this.mAssistCallbacks = new ArrayList<>();
            }
            this.mAssistCallbacks.add(callback);
        }
    }

    public void unregisterOnProvideAssistDataListener(OnProvideAssistDataListener callback) {
        synchronized (this) {
            if (this.mAssistCallbacks != null) {
                this.mAssistCallbacks.remove(callback);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void attach(Context context) {
        attachBaseContext(context);
        this.mLoadedApk = ContextImpl.getImpl(context).mPackageInfo;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dispatchActivityCreated(Activity activity, Bundle savedInstanceState) {
        Object[] callbacks = collectActivityLifecycleCallbacks();
        if (callbacks != null) {
            for (Object obj : callbacks) {
                ((ActivityLifecycleCallbacks) obj).onActivityCreated(activity, savedInstanceState);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dispatchActivityStarted(Activity activity) {
        Object[] callbacks = collectActivityLifecycleCallbacks();
        if (callbacks != null) {
            for (Object obj : callbacks) {
                ((ActivityLifecycleCallbacks) obj).onActivityStarted(activity);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dispatchActivityResumed(Activity activity) {
        Object[] callbacks = collectActivityLifecycleCallbacks();
        if (callbacks != null) {
            for (Object obj : callbacks) {
                ((ActivityLifecycleCallbacks) obj).onActivityResumed(activity);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dispatchActivityPaused(Activity activity) {
        Object[] callbacks = collectActivityLifecycleCallbacks();
        if (callbacks != null) {
            for (Object obj : callbacks) {
                ((ActivityLifecycleCallbacks) obj).onActivityPaused(activity);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dispatchActivityStopped(Activity activity) {
        Object[] callbacks = collectActivityLifecycleCallbacks();
        if (callbacks != null) {
            for (Object obj : callbacks) {
                ((ActivityLifecycleCallbacks) obj).onActivityStopped(activity);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dispatchActivitySaveInstanceState(Activity activity, Bundle outState) {
        Object[] callbacks = collectActivityLifecycleCallbacks();
        if (callbacks != null) {
            for (Object obj : callbacks) {
                ((ActivityLifecycleCallbacks) obj).onActivitySaveInstanceState(activity, outState);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dispatchActivityDestroyed(Activity activity) {
        Object[] callbacks = collectActivityLifecycleCallbacks();
        if (callbacks != null) {
            for (Object obj : callbacks) {
                ((ActivityLifecycleCallbacks) obj).onActivityDestroyed(activity);
            }
        }
    }

    private Object[] collectComponentCallbacks() {
        Object[] callbacks = null;
        synchronized (this.mComponentCallbacks) {
            if (this.mComponentCallbacks.size() > 0) {
                callbacks = this.mComponentCallbacks.toArray();
            }
        }
        return callbacks;
    }

    private Object[] collectActivityLifecycleCallbacks() {
        Object[] callbacks = null;
        synchronized (this.mActivityLifecycleCallbacks) {
            if (this.mActivityLifecycleCallbacks.size() > 0) {
                callbacks = this.mActivityLifecycleCallbacks.toArray();
            }
        }
        return callbacks;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dispatchOnProvideAssistData(Activity activity, Bundle data) {
        synchronized (this) {
            if (this.mAssistCallbacks == null) {
                return;
            }
            Object[] callbacks = this.mAssistCallbacks.toArray();
            if (callbacks != null) {
                for (Object obj : callbacks) {
                    ((OnProvideAssistDataListener) obj).onProvideAssistData(activity, data);
                }
            }
        }
    }
}