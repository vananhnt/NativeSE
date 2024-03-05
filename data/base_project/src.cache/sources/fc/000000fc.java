package android.app;

import android.os.Trace;
import android.util.ArrayMap;
import dalvik.system.PathClassLoader;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: ApplicationLoaders.class */
public class ApplicationLoaders {
    private final ArrayMap<String, ClassLoader> mLoaders = new ArrayMap<>();
    private static final ApplicationLoaders gApplicationLoaders = new ApplicationLoaders();

    ApplicationLoaders() {
    }

    public static ApplicationLoaders getDefault() {
        return gApplicationLoaders;
    }

    public ClassLoader getClassLoader(String zip, String libPath, ClassLoader parent) {
        ClassLoader baseParent = ClassLoader.getSystemClassLoader().getParent();
        synchronized (this.mLoaders) {
            if (parent == null) {
                parent = baseParent;
            }
            if (parent == baseParent) {
                ClassLoader loader = this.mLoaders.get(zip);
                if (loader != null) {
                    return loader;
                }
                Trace.traceBegin(64L, zip);
                PathClassLoader pathClassloader = new PathClassLoader(zip, libPath, parent);
                Trace.traceEnd(64L);
                this.mLoaders.put(zip, pathClassloader);
                return pathClassloader;
            }
            Trace.traceBegin(64L, zip);
            PathClassLoader pathClassloader2 = new PathClassLoader(zip, parent);
            Trace.traceEnd(64L);
            return pathClassloader2;
        }
    }
}