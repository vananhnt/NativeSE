package android.support.v4.app;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.util.DebugUtils;
import android.support.v4.util.SparseArrayCompat;
import android.util.Log;
import gov.nist.core.Separators;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.Modifier;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: LoaderManagerImpl.class */
public class LoaderManagerImpl extends LoaderManager {
    static boolean DEBUG = false;
    static final String TAG = "LoaderManager";
    FragmentActivity mActivity;
    boolean mCreatingLoader;
    boolean mRetaining;
    boolean mRetainingStarted;
    boolean mStarted;
    final String mWho;
    final SparseArrayCompat<LoaderInfo> mLoaders = new SparseArrayCompat<>();
    final SparseArrayCompat<LoaderInfo> mInactiveLoaders = new SparseArrayCompat<>();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: LoaderManagerImpl$LoaderInfo.class */
    public final class LoaderInfo implements Loader.OnLoadCompleteListener<Object> {
        final Bundle mArgs;
        LoaderManager.LoaderCallbacks<Object> mCallbacks;
        Object mData;
        boolean mDeliveredData;
        boolean mDestroyed;
        boolean mHaveData;
        final int mId;
        boolean mListenerRegistered;
        Loader<Object> mLoader;
        LoaderInfo mPendingLoader;
        boolean mReportNextStart;
        boolean mRetaining;
        boolean mRetainingStarted;
        boolean mStarted;
        final LoaderManagerImpl this$0;

        public LoaderInfo(LoaderManagerImpl loaderManagerImpl, int i, Bundle bundle, LoaderManager.LoaderCallbacks<Object> loaderCallbacks) {
            this.this$0 = loaderManagerImpl;
            this.mId = i;
            this.mArgs = bundle;
            this.mCallbacks = loaderCallbacks;
        }

        void callOnLoadFinished(Loader<Object> loader, Object obj) {
            if (this.mCallbacks != null) {
                String str = null;
                if (this.this$0.mActivity != null) {
                    str = this.this$0.mActivity.mFragments.mNoTransactionsBecause;
                    this.this$0.mActivity.mFragments.mNoTransactionsBecause = "onLoadFinished";
                }
                try {
                    if (LoaderManagerImpl.DEBUG) {
                        Log.v(LoaderManagerImpl.TAG, "  onLoadFinished in " + loader + ": " + loader.dataToString(obj));
                    }
                    this.mCallbacks.onLoadFinished(loader, obj);
                    this.mDeliveredData = true;
                } finally {
                    if (this.this$0.mActivity != null) {
                        this.this$0.mActivity.mFragments.mNoTransactionsBecause = str;
                    }
                }
            }
        }

        void destroy() {
            if (LoaderManagerImpl.DEBUG) {
                Log.v(LoaderManagerImpl.TAG, "  Destroying: " + this);
            }
            this.mDestroyed = true;
            boolean z = this.mDeliveredData;
            this.mDeliveredData = false;
            if (this.mCallbacks != null && this.mLoader != null && this.mHaveData && z) {
                if (LoaderManagerImpl.DEBUG) {
                    Log.v(LoaderManagerImpl.TAG, "  Reseting: " + this);
                }
                String str = null;
                if (this.this$0.mActivity != null) {
                    str = this.this$0.mActivity.mFragments.mNoTransactionsBecause;
                    this.this$0.mActivity.mFragments.mNoTransactionsBecause = "onLoaderReset";
                }
                try {
                    this.mCallbacks.onLoaderReset(this.mLoader);
                } finally {
                    if (this.this$0.mActivity != null) {
                        this.this$0.mActivity.mFragments.mNoTransactionsBecause = str;
                    }
                }
            }
            this.mCallbacks = null;
            this.mData = null;
            this.mHaveData = false;
            Loader<Object> loader = this.mLoader;
            if (loader != null) {
                if (this.mListenerRegistered) {
                    this.mListenerRegistered = false;
                    loader.unregisterListener(this);
                }
                this.mLoader.reset();
            }
            LoaderInfo loaderInfo = this.mPendingLoader;
            if (loaderInfo != null) {
                loaderInfo.destroy();
            }
        }

        public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            printWriter.print(str);
            printWriter.print("mId=");
            printWriter.print(this.mId);
            printWriter.print(" mArgs=");
            printWriter.println(this.mArgs);
            printWriter.print(str);
            printWriter.print("mCallbacks=");
            printWriter.println(this.mCallbacks);
            printWriter.print(str);
            printWriter.print("mLoader=");
            printWriter.println(this.mLoader);
            Loader<Object> loader = this.mLoader;
            if (loader != null) {
                loader.dump(str + "  ", fileDescriptor, printWriter, strArr);
            }
            if (this.mHaveData || this.mDeliveredData) {
                printWriter.print(str);
                printWriter.print("mHaveData=");
                printWriter.print(this.mHaveData);
                printWriter.print("  mDeliveredData=");
                printWriter.println(this.mDeliveredData);
                printWriter.print(str);
                printWriter.print("mData=");
                printWriter.println(this.mData);
            }
            printWriter.print(str);
            printWriter.print("mStarted=");
            printWriter.print(this.mStarted);
            printWriter.print(" mReportNextStart=");
            printWriter.print(this.mReportNextStart);
            printWriter.print(" mDestroyed=");
            printWriter.println(this.mDestroyed);
            printWriter.print(str);
            printWriter.print("mRetaining=");
            printWriter.print(this.mRetaining);
            printWriter.print(" mRetainingStarted=");
            printWriter.print(this.mRetainingStarted);
            printWriter.print(" mListenerRegistered=");
            printWriter.println(this.mListenerRegistered);
            if (this.mPendingLoader != null) {
                printWriter.print(str);
                printWriter.println("Pending Loader ");
                printWriter.print(this.mPendingLoader);
                printWriter.println(Separators.COLON);
                LoaderInfo loaderInfo = this.mPendingLoader;
                loaderInfo.dump(str + "  ", fileDescriptor, printWriter, strArr);
            }
        }

        void finishRetain() {
            if (this.mRetaining) {
                if (LoaderManagerImpl.DEBUG) {
                    Log.v(LoaderManagerImpl.TAG, "  Finished Retaining: " + this);
                }
                this.mRetaining = false;
                boolean z = this.mStarted;
                if (z != this.mRetainingStarted && !z) {
                    stop();
                }
            }
            if (this.mStarted && this.mHaveData && !this.mReportNextStart) {
                callOnLoadFinished(this.mLoader, this.mData);
            }
        }

        @Override // android.support.v4.content.Loader.OnLoadCompleteListener
        public void onLoadComplete(Loader<Object> loader, Object obj) {
            if (LoaderManagerImpl.DEBUG) {
                Log.v(LoaderManagerImpl.TAG, "onLoadComplete: " + this);
            }
            if (this.mDestroyed) {
                if (LoaderManagerImpl.DEBUG) {
                    Log.v(LoaderManagerImpl.TAG, "  Ignoring load complete -- destroyed");
                }
            } else if (this.this$0.mLoaders.get(this.mId) != this) {
                if (LoaderManagerImpl.DEBUG) {
                    Log.v(LoaderManagerImpl.TAG, "  Ignoring load complete -- not active");
                }
            } else {
                LoaderInfo loaderInfo = this.mPendingLoader;
                if (loaderInfo != null) {
                    if (LoaderManagerImpl.DEBUG) {
                        Log.v(LoaderManagerImpl.TAG, "  Switching to pending loader: " + loaderInfo);
                    }
                    this.mPendingLoader = null;
                    this.this$0.mLoaders.put(this.mId, null);
                    destroy();
                    this.this$0.installLoader(loaderInfo);
                    return;
                }
                if (this.mData != obj || !this.mHaveData) {
                    this.mData = obj;
                    this.mHaveData = true;
                    if (this.mStarted) {
                        callOnLoadFinished(loader, obj);
                    }
                }
                LoaderInfo loaderInfo2 = this.this$0.mInactiveLoaders.get(this.mId);
                if (loaderInfo2 != null && loaderInfo2 != this) {
                    loaderInfo2.mDeliveredData = false;
                    loaderInfo2.destroy();
                    this.this$0.mInactiveLoaders.remove(this.mId);
                }
                if (this.this$0.mActivity == null || this.this$0.hasRunningLoaders()) {
                    return;
                }
                this.this$0.mActivity.mFragments.startPendingDeferredFragments();
            }
        }

        void reportStart() {
            if (this.mStarted && this.mReportNextStart) {
                this.mReportNextStart = false;
                if (this.mHaveData) {
                    callOnLoadFinished(this.mLoader, this.mData);
                }
            }
        }

        void retain() {
            if (LoaderManagerImpl.DEBUG) {
                Log.v(LoaderManagerImpl.TAG, "  Retaining: " + this);
            }
            this.mRetaining = true;
            this.mRetainingStarted = this.mStarted;
            this.mStarted = false;
            this.mCallbacks = null;
        }

        void start() {
            LoaderManager.LoaderCallbacks<Object> loaderCallbacks;
            if (this.mRetaining && this.mRetainingStarted) {
                this.mStarted = true;
            } else if (this.mStarted) {
            } else {
                this.mStarted = true;
                if (LoaderManagerImpl.DEBUG) {
                    Log.v(LoaderManagerImpl.TAG, "  Starting: " + this);
                }
                if (this.mLoader == null && (loaderCallbacks = this.mCallbacks) != null) {
                    this.mLoader = loaderCallbacks.onCreateLoader(this.mId, this.mArgs);
                }
                Loader<Object> loader = this.mLoader;
                if (loader != null) {
                    if (loader.getClass().isMemberClass() && !Modifier.isStatic(this.mLoader.getClass().getModifiers())) {
                        throw new IllegalArgumentException("Object returned from onCreateLoader must not be a non-static inner member class: " + this.mLoader);
                    }
                    if (!this.mListenerRegistered) {
                        this.mLoader.registerListener(this.mId, this);
                        this.mListenerRegistered = true;
                    }
                    this.mLoader.startLoading();
                }
            }
        }

        void stop() {
            Loader<Object> loader;
            if (LoaderManagerImpl.DEBUG) {
                Log.v(LoaderManagerImpl.TAG, "  Stopping: " + this);
            }
            this.mStarted = false;
            if (this.mRetaining || (loader = this.mLoader) == null || !this.mListenerRegistered) {
                return;
            }
            this.mListenerRegistered = false;
            loader.unregisterListener(this);
            this.mLoader.stopLoading();
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(64);
            sb.append("LoaderInfo{");
            sb.append(Integer.toHexString(System.identityHashCode(this)));
            sb.append(" #");
            sb.append(this.mId);
            sb.append(" : ");
            DebugUtils.buildShortClassTag(this.mLoader, sb);
            sb.append("}}");
            return sb.toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public LoaderManagerImpl(String str, FragmentActivity fragmentActivity, boolean z) {
        this.mWho = str;
        this.mActivity = fragmentActivity;
        this.mStarted = z;
    }

    private LoaderInfo createAndInstallLoader(int i, Bundle bundle, LoaderManager.LoaderCallbacks<Object> loaderCallbacks) {
        try {
            this.mCreatingLoader = true;
            LoaderInfo createLoader = createLoader(i, bundle, loaderCallbacks);
            installLoader(createLoader);
            return createLoader;
        } finally {
            this.mCreatingLoader = false;
        }
    }

    private LoaderInfo createLoader(int i, Bundle bundle, LoaderManager.LoaderCallbacks<Object> loaderCallbacks) {
        LoaderInfo loaderInfo = new LoaderInfo(this, i, bundle, loaderCallbacks);
        loaderInfo.mLoader = loaderCallbacks.onCreateLoader(i, bundle);
        return loaderInfo;
    }

    @Override // android.support.v4.app.LoaderManager
    public void destroyLoader(int i) {
        if (this.mCreatingLoader) {
            throw new IllegalStateException("Called while creating a loader");
        }
        if (DEBUG) {
            Log.v(TAG, "destroyLoader in " + this + " of " + i);
        }
        int indexOfKey = this.mLoaders.indexOfKey(i);
        if (indexOfKey >= 0) {
            LoaderInfo valueAt = this.mLoaders.valueAt(indexOfKey);
            this.mLoaders.removeAt(indexOfKey);
            valueAt.destroy();
        }
        int indexOfKey2 = this.mInactiveLoaders.indexOfKey(i);
        if (indexOfKey2 >= 0) {
            LoaderInfo valueAt2 = this.mInactiveLoaders.valueAt(indexOfKey2);
            this.mInactiveLoaders.removeAt(indexOfKey2);
            valueAt2.destroy();
        }
        if (this.mActivity == null || hasRunningLoaders()) {
            return;
        }
        this.mActivity.mFragments.startPendingDeferredFragments();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void doDestroy() {
        if (!this.mRetaining) {
            if (DEBUG) {
                Log.v(TAG, "Destroying Active in " + this);
            }
            for (int size = this.mLoaders.size() - 1; size >= 0; size--) {
                this.mLoaders.valueAt(size).destroy();
            }
            this.mLoaders.clear();
        }
        if (DEBUG) {
            Log.v(TAG, "Destroying Inactive in " + this);
        }
        for (int size2 = this.mInactiveLoaders.size() - 1; size2 >= 0; size2--) {
            this.mInactiveLoaders.valueAt(size2).destroy();
        }
        this.mInactiveLoaders.clear();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void doReportNextStart() {
        for (int size = this.mLoaders.size() - 1; size >= 0; size--) {
            this.mLoaders.valueAt(size).mReportNextStart = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void doReportStart() {
        for (int size = this.mLoaders.size() - 1; size >= 0; size--) {
            this.mLoaders.valueAt(size).reportStart();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void doRetain() {
        if (DEBUG) {
            Log.v(TAG, "Retaining in " + this);
        }
        if (this.mStarted) {
            this.mRetaining = true;
            this.mStarted = false;
            for (int size = this.mLoaders.size() - 1; size >= 0; size--) {
                this.mLoaders.valueAt(size).retain();
            }
            return;
        }
        RuntimeException runtimeException = new RuntimeException("here");
        runtimeException.fillInStackTrace();
        Log.w(TAG, "Called doRetain when not started: " + this, runtimeException);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void doStart() {
        if (DEBUG) {
            Log.v(TAG, "Starting in " + this);
        }
        if (!this.mStarted) {
            this.mStarted = true;
            for (int size = this.mLoaders.size() - 1; size >= 0; size--) {
                this.mLoaders.valueAt(size).start();
            }
            return;
        }
        RuntimeException runtimeException = new RuntimeException("here");
        runtimeException.fillInStackTrace();
        Log.w(TAG, "Called doStart when already started: " + this, runtimeException);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void doStop() {
        if (DEBUG) {
            Log.v(TAG, "Stopping in " + this);
        }
        if (this.mStarted) {
            for (int size = this.mLoaders.size() - 1; size >= 0; size--) {
                this.mLoaders.valueAt(size).stop();
            }
            this.mStarted = false;
            return;
        }
        RuntimeException runtimeException = new RuntimeException("here");
        runtimeException.fillInStackTrace();
        Log.w(TAG, "Called doStop when not started: " + this, runtimeException);
    }

    @Override // android.support.v4.app.LoaderManager
    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        if (this.mLoaders.size() > 0) {
            printWriter.print(str);
            printWriter.println("Active Loaders:");
            String str2 = str + "    ";
            for (int i = 0; i < this.mLoaders.size(); i++) {
                LoaderInfo valueAt = this.mLoaders.valueAt(i);
                printWriter.print(str);
                printWriter.print("  #");
                printWriter.print(this.mLoaders.keyAt(i));
                printWriter.print(": ");
                printWriter.println(valueAt.toString());
                valueAt.dump(str2, fileDescriptor, printWriter, strArr);
            }
        }
        if (this.mInactiveLoaders.size() > 0) {
            printWriter.print(str);
            printWriter.println("Inactive Loaders:");
            String str3 = str + "    ";
            for (int i2 = 0; i2 < this.mInactiveLoaders.size(); i2++) {
                LoaderInfo valueAt2 = this.mInactiveLoaders.valueAt(i2);
                printWriter.print(str);
                printWriter.print("  #");
                printWriter.print(this.mInactiveLoaders.keyAt(i2));
                printWriter.print(": ");
                printWriter.println(valueAt2.toString());
                valueAt2.dump(str3, fileDescriptor, printWriter, strArr);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void finishRetain() {
        if (this.mRetaining) {
            if (DEBUG) {
                Log.v(TAG, "Finished Retaining in " + this);
            }
            this.mRetaining = false;
            for (int size = this.mLoaders.size() - 1; size >= 0; size--) {
                this.mLoaders.valueAt(size).finishRetain();
            }
        }
    }

    @Override // android.support.v4.app.LoaderManager
    public <D> Loader<D> getLoader(int i) {
        if (this.mCreatingLoader) {
            throw new IllegalStateException("Called while creating a loader");
        }
        LoaderInfo loaderInfo = this.mLoaders.get(i);
        if (loaderInfo != null) {
            return loaderInfo.mPendingLoader != null ? (Loader<D>) loaderInfo.mPendingLoader.mLoader : (Loader<D>) loaderInfo.mLoader;
        }
        return null;
    }

    @Override // android.support.v4.app.LoaderManager
    public boolean hasRunningLoaders() {
        boolean z = false;
        int size = this.mLoaders.size();
        for (int i = 0; i < size; i++) {
            LoaderInfo valueAt = this.mLoaders.valueAt(i);
            z |= valueAt.mStarted && !valueAt.mDeliveredData;
        }
        return z;
    }

    @Override // android.support.v4.app.LoaderManager
    public <D> Loader<D> initLoader(int i, Bundle bundle, LoaderManager.LoaderCallbacks<D> loaderCallbacks) {
        LoaderInfo loaderInfo;
        if (this.mCreatingLoader) {
            throw new IllegalStateException("Called while creating a loader");
        }
        LoaderInfo loaderInfo2 = this.mLoaders.get(i);
        if (DEBUG) {
            Log.v(TAG, "initLoader in " + this + ": args=" + bundle);
        }
        if (loaderInfo2 == null) {
            loaderInfo = createAndInstallLoader(i, bundle, loaderCallbacks);
            if (DEBUG) {
                Log.v(TAG, "  Created new loader " + loaderInfo);
            }
        } else {
            if (DEBUG) {
                Log.v(TAG, "  Re-using existing loader " + loaderInfo2);
            }
            loaderInfo2.mCallbacks = loaderCallbacks;
            loaderInfo = loaderInfo2;
        }
        if (loaderInfo.mHaveData && this.mStarted) {
            loaderInfo.callOnLoadFinished(loaderInfo.mLoader, loaderInfo.mData);
        }
        return (Loader<D>) loaderInfo.mLoader;
    }

    void installLoader(LoaderInfo loaderInfo) {
        this.mLoaders.put(loaderInfo.mId, loaderInfo);
        if (this.mStarted) {
            loaderInfo.start();
        }
    }

    @Override // android.support.v4.app.LoaderManager
    public <D> Loader<D> restartLoader(int i, Bundle bundle, LoaderManager.LoaderCallbacks<D> loaderCallbacks) {
        if (this.mCreatingLoader) {
            throw new IllegalStateException("Called while creating a loader");
        }
        LoaderInfo loaderInfo = this.mLoaders.get(i);
        if (DEBUG) {
            Log.v(TAG, "restartLoader in " + this + ": args=" + bundle);
        }
        if (loaderInfo != null) {
            LoaderInfo loaderInfo2 = this.mInactiveLoaders.get(i);
            if (loaderInfo2 == null) {
                if (DEBUG) {
                    Log.v(TAG, "  Making last loader inactive: " + loaderInfo);
                }
                loaderInfo.mLoader.abandon();
                this.mInactiveLoaders.put(i, loaderInfo);
            } else if (loaderInfo.mHaveData) {
                if (DEBUG) {
                    Log.v(TAG, "  Removing last inactive loader: " + loaderInfo);
                }
                loaderInfo2.mDeliveredData = false;
                loaderInfo2.destroy();
                loaderInfo.mLoader.abandon();
                this.mInactiveLoaders.put(i, loaderInfo);
            } else if (loaderInfo.mStarted) {
                if (loaderInfo.mPendingLoader != null) {
                    if (DEBUG) {
                        Log.v(TAG, "  Removing pending loader: " + loaderInfo.mPendingLoader);
                    }
                    loaderInfo.mPendingLoader.destroy();
                    loaderInfo.mPendingLoader = null;
                }
                if (DEBUG) {
                    Log.v(TAG, "  Enqueuing as new pending loader");
                }
                loaderInfo.mPendingLoader = createLoader(i, bundle, loaderCallbacks);
                return (Loader<D>) loaderInfo.mPendingLoader.mLoader;
            } else {
                if (DEBUG) {
                    Log.v(TAG, "  Current loader is stopped; replacing");
                }
                this.mLoaders.put(i, null);
                loaderInfo.destroy();
            }
        }
        return (Loader<D>) createAndInstallLoader(i, bundle, loaderCallbacks).mLoader;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("LoaderManager{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(" in ");
        DebugUtils.buildShortClassTag(this.mActivity, sb);
        sb.append("}}");
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateActivity(FragmentActivity fragmentActivity) {
        this.mActivity = fragmentActivity;
    }
}