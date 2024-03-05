package android.app;

import android.content.Loader;
import android.os.Bundle;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* loaded from: LoaderManager.class */
public abstract class LoaderManager {

    /* loaded from: LoaderManager$LoaderCallbacks.class */
    public interface LoaderCallbacks<D> {
        Loader<D> onCreateLoader(int i, Bundle bundle);

        void onLoadFinished(Loader<D> loader, D d);

        void onLoaderReset(Loader<D> loader);
    }

    public abstract <D> Loader<D> initLoader(int i, Bundle bundle, LoaderCallbacks<D> loaderCallbacks);

    public abstract <D> Loader<D> restartLoader(int i, Bundle bundle, LoaderCallbacks<D> loaderCallbacks);

    public abstract void destroyLoader(int i);

    public abstract <D> Loader<D> getLoader(int i);

    public abstract void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr);

    public static void enableDebugLogging(boolean enabled) {
        LoaderManagerImpl.DEBUG = enabled;
    }
}