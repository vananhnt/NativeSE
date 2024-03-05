package android.view;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.opengl.GLUtils;
import android.opengl.ManagedEGLContext;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.IAssetAtlas;
import android.view.Surface;
import android.view.View;
import com.android.server.AssetAtlasService;
import com.google.android.gles_jni.EGLImpl;
import gov.nist.core.Separators;
import java.io.File;
import java.io.PrintWriter;
import java.util.concurrent.locks.ReentrantLock;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL;

/* loaded from: HardwareRenderer.class */
public abstract class HardwareRenderer {
    static final String LOG_TAG = "HardwareRenderer";
    private static final String CACHE_PATH_SHADERS = "com.android.opengl.shaders_cache";
    static final boolean RENDER_DIRTY_REGIONS = true;
    static final String RENDER_DIRTY_REGIONS_PROPERTY = "debug.hwui.render_dirty_regions";
    public static final String PROFILE_PROPERTY = "debug.hwui.profile";
    public static final String PROFILE_PROPERTY_VISUALIZE_BARS = "visual_bars";
    public static final String PROFILE_PROPERTY_VISUALIZE_LINES = "visual_lines";
    static final String PROFILE_MAXFRAMES_PROPERTY = "debug.hwui.profile.maxframes";
    static final String PRINT_CONFIG_PROPERTY = "debug.hwui.print_config";
    public static final String DEBUG_DIRTY_REGIONS_PROPERTY = "debug.hwui.show_dirty_regions";
    public static final String DEBUG_SHOW_LAYERS_UPDATES_PROPERTY = "debug.hwui.show_layers_updates";
    public static final String DEBUG_OVERDRAW_PROPERTY = "debug.hwui.overdraw";
    public static final String OVERDRAW_PROPERTY_SHOW = "show";
    public static final String OVERDRAW_PROPERTY_COUNT = "count";
    public static final String DEBUG_SHOW_NON_RECTANGULAR_CLIP_PROPERTY = "debug.hwui.show_non_rect_clip";
    public static boolean sRendererDisabled = false;
    public static boolean sSystemRendererDisabled = false;
    private static final int PROFILE_MAX_FRAMES = 128;
    private static final int PROFILE_FRAME_DATA_COUNT = 3;
    private boolean mEnabled;
    private boolean mRequested = true;

    /* loaded from: HardwareRenderer$HardwareDrawCallbacks.class */
    interface HardwareDrawCallbacks {
        void onHardwarePreDraw(HardwareCanvas hardwareCanvas);

        void onHardwarePostDraw(HardwareCanvas hardwareCanvas);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void destroy(boolean z);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract boolean initialize(Surface surface) throws Surface.OutOfResourcesException;

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void updateSurface(Surface surface) throws Surface.OutOfResourcesException;

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void destroyLayers(View view);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void destroyHardwareResources(View view);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void invalidate(Surface surface);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract boolean validate();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract boolean safelyRun(Runnable runnable);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void setup(int i, int i2);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract int getWidth();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract int getHeight();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract HardwareCanvas getCanvas();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void dumpGfxInfo(PrintWriter printWriter);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract long getFrameCount();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract boolean loadSystemProperties(Surface surface);

    private static native boolean nLoadProperties();

    private static native void nSetupShadersDiskCache(String str);

    private static native void nBeginFrame(int[] iArr);

    private static native long nGetSystemTime();

    private static native boolean nPreserveBackBuffer();

    private static native boolean nIsBackBufferPreserved();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void pushLayerUpdate(HardwareLayer hardwareLayer);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void cancelLayerUpdate(HardwareLayer hardwareLayer);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void flushLayerUpdates();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void draw(View view, View.AttachInfo attachInfo, HardwareDrawCallbacks hardwareDrawCallbacks, Rect rect);

    public abstract DisplayList createDisplayList(String str);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract HardwareLayer createHardwareLayer(boolean z);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract HardwareLayer createHardwareLayer(int i, int i2, boolean z);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract SurfaceTexture createSurfaceTexture(HardwareLayer hardwareLayer);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void setSurfaceTexture(HardwareLayer hardwareLayer, SurfaceTexture surfaceTexture);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void detachFunctor(int i);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract boolean attachFunctor(View.AttachInfo attachInfo, int i);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void setName(String str);

    static /* synthetic */ boolean access$000() {
        return nLoadProperties();
    }

    public static void disable(boolean system) {
        sRendererDisabled = true;
        if (system) {
            sSystemRendererDisabled = true;
        }
    }

    public static boolean isAvailable() {
        return GLES20Canvas.isAvailable();
    }

    public static void setupDiskCache(File cacheDir) {
        nSetupShadersDiskCache(new File(cacheDir, CACHE_PATH_SHADERS).getAbsolutePath());
    }

    static void beginFrame(int[] size) {
        nBeginFrame(size);
    }

    static long getSystemTime() {
        return nGetSystemTime();
    }

    static boolean preserveBackBuffer() {
        return nPreserveBackBuffer();
    }

    static boolean isBackBufferPreserved() {
        return nIsBackBufferPreserved();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean initializeIfNeeded(int width, int height, Surface surface) throws Surface.OutOfResourcesException {
        if (isRequested() && !isEnabled() && initialize(surface)) {
            setup(width, height);
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static HardwareRenderer createGlRenderer(int glVersion, boolean translucent) {
        switch (glVersion) {
            case 2:
                return Gl20Renderer.create(translucent);
            default:
                throw new IllegalArgumentException("Unknown GL version: " + glVersion);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void trimMemory(int level) {
        startTrimMemory(level);
        endTrimMemory();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void startTrimMemory(int level) {
        Gl20Renderer.startTrimMemory(level);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void endTrimMemory() {
        Gl20Renderer.endTrimMemory();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isEnabled() {
        return this.mEnabled;
    }

    void setEnabled(boolean enabled) {
        this.mEnabled = enabled;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isRequested() {
        return this.mRequested;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setRequested(boolean requested) {
        this.mRequested = requested;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: HardwareRenderer$GraphDataProvider.class */
    public abstract class GraphDataProvider {
        public static final int GRAPH_TYPE_BARS = 0;
        public static final int GRAPH_TYPE_LINES = 1;

        abstract int getGraphType();

        abstract void prepare(DisplayMetrics displayMetrics);

        abstract int getVerticalUnitSize();

        abstract int getHorizontalUnitSize();

        abstract int getHorizontaUnitMargin();

        abstract float getThreshold();

        abstract float[] getData();

        abstract int getFrameCount();

        abstract int getElementCount();

        abstract int getCurrentFrame();

        abstract void setupGraphPaint(Paint paint, int i);

        abstract void setupThresholdPaint(Paint paint);

        abstract void setupCurrentFramePaint(Paint paint);

        GraphDataProvider() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: HardwareRenderer$GlRenderer.class */
    public static abstract class GlRenderer extends HardwareRenderer {
        static final int SURFACE_STATE_ERROR = 0;
        static final int SURFACE_STATE_SUCCESS = 1;
        static final int SURFACE_STATE_UPDATED = 2;
        static final int FUNCTOR_PROCESS_DELAY = 4;
        private static final int PROFILE_DRAW_MARGIN = 0;
        private static final int PROFILE_DRAW_WIDTH = 3;
        private static final int PROFILE_DRAW_CURRENT_FRAME_COLOR = -815814067;
        private static final int PROFILE_DRAW_THRESHOLD_COLOR = -10507699;
        private static final int PROFILE_DRAW_THRESHOLD_STROKE_WIDTH = 2;
        private static final int PROFILE_DRAW_DP_PER_MS = 7;
        private static final int OVERDRAW_TYPE_COUNT = 1;
        static EGL10 sEgl;
        static EGLDisplay sEglDisplay;
        static EGLConfig sEglConfig;
        EGLContext mEglContext;
        Thread mEglThread;
        EGLSurface mEglSurface;
        GL mGl;
        HardwareCanvas mCanvas;
        String mName;
        long mFrameCount;
        Paint mDebugPaint;
        static boolean sDirtyRegions;
        static final boolean sDirtyRegionsRequested;
        boolean mDirtyRegionsEnabled;
        boolean mUpdateDirtyRegions;
        boolean mProfileEnabled;
        float[] mProfileData;
        ReentrantLock mProfileLock;
        GraphDataProvider mDebugDataProvider;
        float[][] mProfileShapes;
        Paint mProfilePaint;
        boolean mDebugDirtyRegions;
        HardwareLayer mDebugOverdrawLayer;
        Paint mDebugOverdrawPaint;
        final int mGlVersion;
        final boolean mTranslucent;
        private boolean mDestroyed;
        private static final int[] PROFILE_DRAW_COLORS = {-817994036, -807651054, -806971392};
        private static final String[] VISUALIZERS = {HardwareRenderer.PROFILE_PROPERTY_VISUALIZE_BARS, HardwareRenderer.PROFILE_PROPERTY_VISUALIZE_LINES};
        private static final String[] OVERDRAW = {HardwareRenderer.OVERDRAW_PROPERTY_SHOW, HardwareRenderer.OVERDRAW_PROPERTY_COUNT};
        static final Object[] sEglLock = new Object[0];
        static final ThreadLocal<ManagedEGLContext> sEglContextStorage = new ThreadLocal<>();
        int mWidth = -1;
        int mHeight = -1;
        int mProfileVisualizerType = -1;
        int mProfileCurrentFrame = -3;
        int mDebugOverdraw = -1;
        private final Rect mRedrawClip = new Rect();
        private final int[] mSurfaceSize = new int[2];
        private final FunctorsRunnable mFunctorsRunnable = new FunctorsRunnable();
        private long mDrawDelta = Long.MAX_VALUE;

        abstract HardwareCanvas createCanvas();

        abstract int[] getConfig(boolean z);

        abstract ManagedEGLContext createManagedContext(EGLContext eGLContext);

        abstract void initCaches();

        abstract void initAtlas();

        abstract void countOverdraw(HardwareCanvas hardwareCanvas);

        abstract float getOverdraw(HardwareCanvas hardwareCanvas);

        abstract void drawProfileData(View.AttachInfo attachInfo);

        static {
            String dirtyProperty = SystemProperties.get(HardwareRenderer.RENDER_DIRTY_REGIONS_PROPERTY, "true");
            sDirtyRegions = "true".equalsIgnoreCase(dirtyProperty);
            sDirtyRegionsRequested = sDirtyRegions;
        }

        GlRenderer(int glVersion, boolean translucent) {
            this.mGlVersion = glVersion;
            this.mTranslucent = translucent;
            loadSystemProperties(null);
        }

        @Override // android.view.HardwareRenderer
        boolean loadSystemProperties(Surface surface) {
            boolean changed = false;
            String profiling = SystemProperties.get(HardwareRenderer.PROFILE_PROPERTY);
            int graphType = search(VISUALIZERS, profiling);
            boolean value = graphType >= 0;
            if (graphType != this.mProfileVisualizerType) {
                changed = true;
                this.mProfileVisualizerType = graphType;
                this.mProfileShapes = null;
                this.mProfilePaint = null;
                if (value) {
                    this.mDebugDataProvider = new DrawPerformanceDataProvider(graphType);
                } else {
                    this.mDebugDataProvider = null;
                }
            }
            if (!value) {
                value = Boolean.parseBoolean(profiling);
            }
            if (value != this.mProfileEnabled) {
                changed = true;
                this.mProfileEnabled = value;
                if (this.mProfileEnabled) {
                    Log.d(HardwareRenderer.LOG_TAG, "Profiling hardware renderer");
                    int maxProfileFrames = SystemProperties.getInt(HardwareRenderer.PROFILE_MAXFRAMES_PROPERTY, 128);
                    this.mProfileData = new float[maxProfileFrames * 3];
                    for (int i = 0; i < this.mProfileData.length; i += 3) {
                        this.mProfileData[i + 2] = -1.0f;
                        this.mProfileData[i + 1] = -1.0f;
                        this.mProfileData[i] = -1.0f;
                    }
                    this.mProfileLock = new ReentrantLock();
                } else {
                    this.mProfileData = null;
                    this.mProfileLock = null;
                    this.mProfileVisualizerType = -1;
                }
                this.mProfileCurrentFrame = -3;
            }
            boolean value2 = SystemProperties.getBoolean(HardwareRenderer.DEBUG_DIRTY_REGIONS_PROPERTY, false);
            if (value2 != this.mDebugDirtyRegions) {
                changed = true;
                this.mDebugDirtyRegions = value2;
                if (this.mDebugDirtyRegions) {
                    Log.d(HardwareRenderer.LOG_TAG, "Debugging dirty regions");
                }
            }
            String overdraw = SystemProperties.get(HardwareRenderer.DEBUG_OVERDRAW_PROPERTY);
            int debugOverdraw = search(OVERDRAW, overdraw);
            if (debugOverdraw != this.mDebugOverdraw) {
                changed = true;
                this.mDebugOverdraw = debugOverdraw;
                if (this.mDebugOverdraw != 1 && this.mDebugOverdrawLayer != null) {
                    this.mDebugOverdrawLayer.destroy();
                    this.mDebugOverdrawLayer = null;
                    this.mDebugOverdrawPaint = null;
                }
            }
            if (HardwareRenderer.access$000()) {
                changed = true;
            }
            return changed;
        }

        private static int search(String[] values, String value) {
            for (int i = 0; i < values.length; i++) {
                if (values[i].equals(value)) {
                    return i;
                }
            }
            return -1;
        }

        @Override // android.view.HardwareRenderer
        void dumpGfxInfo(PrintWriter pw) {
            if (this.mProfileEnabled) {
                pw.printf("\n\tDraw\tProcess\tExecute\n", new Object[0]);
                this.mProfileLock.lock();
                for (int i = 0; i < this.mProfileData.length && this.mProfileData[i] >= 0.0f; i += 3) {
                    try {
                        pw.printf("\t%3.2f\t%3.2f\t%3.2f\n", Float.valueOf(this.mProfileData[i]), Float.valueOf(this.mProfileData[i + 1]), Float.valueOf(this.mProfileData[i + 2]));
                        this.mProfileData[i + 2] = -1.0f;
                        this.mProfileData[i + 1] = -1.0f;
                        this.mProfileData[i] = -1.0f;
                    } catch (Throwable th) {
                        this.mProfileLock.unlock();
                        throw th;
                    }
                }
                this.mProfileCurrentFrame = this.mProfileData.length;
                this.mProfileLock.unlock();
            }
        }

        @Override // android.view.HardwareRenderer
        long getFrameCount() {
            return this.mFrameCount;
        }

        boolean hasDirtyRegions() {
            return this.mDirtyRegionsEnabled;
        }

        void checkEglErrors() {
            if (isEnabled()) {
                checkEglErrorsForced();
            }
        }

        private void checkEglErrorsForced() {
            int error = sEgl.eglGetError();
            if (error != 12288) {
                Log.w(HardwareRenderer.LOG_TAG, "EGL error: " + GLUtils.getEGLErrorString(error));
                fallback(error != 12302);
            }
        }

        private void fallback(boolean fallback) {
            destroy(true);
            if (fallback) {
                setRequested(false);
                Log.w(HardwareRenderer.LOG_TAG, "Mountain View, we've had a problem here. Switching back to software rendering.");
            }
        }

        @Override // android.view.HardwareRenderer
        boolean initialize(Surface surface) throws Surface.OutOfResourcesException {
            if (isRequested() && !isEnabled()) {
                boolean contextCreated = initializeEgl();
                this.mGl = createEglSurface(surface);
                this.mDestroyed = false;
                if (this.mGl != null) {
                    int err = sEgl.eglGetError();
                    if (err != 12288) {
                        destroy(true);
                        setRequested(false);
                    } else {
                        if (this.mCanvas == null) {
                            this.mCanvas = createCanvas();
                            this.mCanvas.setName(this.mName);
                        }
                        setEnabled(true);
                        if (contextCreated) {
                            initAtlas();
                        }
                    }
                    return this.mCanvas != null;
                }
                return false;
            }
            return false;
        }

        @Override // android.view.HardwareRenderer
        void updateSurface(Surface surface) throws Surface.OutOfResourcesException {
            if (isRequested() && isEnabled()) {
                createEglSurface(surface);
            }
        }

        boolean initializeEgl() {
            synchronized (sEglLock) {
                if (sEgl == null && sEglConfig == null) {
                    sEgl = (EGL10) EGLContext.getEGL();
                    sEglDisplay = sEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
                    if (sEglDisplay == EGL10.EGL_NO_DISPLAY) {
                        throw new RuntimeException("eglGetDisplay failed " + GLUtils.getEGLErrorString(sEgl.eglGetError()));
                    }
                    int[] version = new int[2];
                    if (!sEgl.eglInitialize(sEglDisplay, version)) {
                        throw new RuntimeException("eglInitialize failed " + GLUtils.getEGLErrorString(sEgl.eglGetError()));
                    }
                    checkEglErrorsForced();
                    sEglConfig = loadEglConfig();
                }
            }
            ManagedEGLContext managedContext = sEglContextStorage.get();
            this.mEglContext = managedContext != null ? managedContext.getContext() : null;
            this.mEglThread = Thread.currentThread();
            if (this.mEglContext == null) {
                this.mEglContext = createContext(sEgl, sEglDisplay, sEglConfig);
                sEglContextStorage.set(createManagedContext(this.mEglContext));
                return true;
            }
            return false;
        }

        private EGLConfig loadEglConfig() {
            EGLConfig eglConfig = chooseEglConfig();
            if (eglConfig == null) {
                if (sDirtyRegions) {
                    sDirtyRegions = false;
                    eglConfig = chooseEglConfig();
                    if (eglConfig == null) {
                        throw new RuntimeException("eglConfig not initialized");
                    }
                } else {
                    throw new RuntimeException("eglConfig not initialized");
                }
            }
            return eglConfig;
        }

        private EGLConfig chooseEglConfig() {
            EGLConfig[] configs = new EGLConfig[1];
            int[] configsCount = new int[1];
            int[] configSpec = getConfig(sDirtyRegions);
            String debug = SystemProperties.get(HardwareRenderer.PRINT_CONFIG_PROPERTY, "");
            if ("all".equalsIgnoreCase(debug)) {
                sEgl.eglChooseConfig(sEglDisplay, configSpec, null, 0, configsCount);
                EGLConfig[] debugConfigs = new EGLConfig[configsCount[0]];
                sEgl.eglChooseConfig(sEglDisplay, configSpec, debugConfigs, configsCount[0], configsCount);
                for (EGLConfig config : debugConfigs) {
                    printConfig(config);
                }
            }
            if (!sEgl.eglChooseConfig(sEglDisplay, configSpec, configs, 1, configsCount)) {
                throw new IllegalArgumentException("eglChooseConfig failed " + GLUtils.getEGLErrorString(sEgl.eglGetError()));
            }
            if (configsCount[0] > 0) {
                if ("choice".equalsIgnoreCase(debug)) {
                    printConfig(configs[0]);
                }
                return configs[0];
            }
            return null;
        }

        private static void printConfig(EGLConfig config) {
            int[] value = new int[1];
            Log.d(HardwareRenderer.LOG_TAG, "EGL configuration " + config + Separators.COLON);
            sEgl.eglGetConfigAttrib(sEglDisplay, config, 12324, value);
            Log.d(HardwareRenderer.LOG_TAG, "  RED_SIZE = " + value[0]);
            sEgl.eglGetConfigAttrib(sEglDisplay, config, 12323, value);
            Log.d(HardwareRenderer.LOG_TAG, "  GREEN_SIZE = " + value[0]);
            sEgl.eglGetConfigAttrib(sEglDisplay, config, 12322, value);
            Log.d(HardwareRenderer.LOG_TAG, "  BLUE_SIZE = " + value[0]);
            sEgl.eglGetConfigAttrib(sEglDisplay, config, 12321, value);
            Log.d(HardwareRenderer.LOG_TAG, "  ALPHA_SIZE = " + value[0]);
            sEgl.eglGetConfigAttrib(sEglDisplay, config, 12325, value);
            Log.d(HardwareRenderer.LOG_TAG, "  DEPTH_SIZE = " + value[0]);
            sEgl.eglGetConfigAttrib(sEglDisplay, config, 12326, value);
            Log.d(HardwareRenderer.LOG_TAG, "  STENCIL_SIZE = " + value[0]);
            sEgl.eglGetConfigAttrib(sEglDisplay, config, 12338, value);
            Log.d(HardwareRenderer.LOG_TAG, "  SAMPLE_BUFFERS = " + value[0]);
            sEgl.eglGetConfigAttrib(sEglDisplay, config, 12337, value);
            Log.d(HardwareRenderer.LOG_TAG, "  SAMPLES = " + value[0]);
            sEgl.eglGetConfigAttrib(sEglDisplay, config, 12339, value);
            Log.d(HardwareRenderer.LOG_TAG, "  SURFACE_TYPE = 0x" + Integer.toHexString(value[0]));
            sEgl.eglGetConfigAttrib(sEglDisplay, config, 12327, value);
            Log.d(HardwareRenderer.LOG_TAG, "  CONFIG_CAVEAT = 0x" + Integer.toHexString(value[0]));
        }

        GL createEglSurface(Surface surface) throws Surface.OutOfResourcesException {
            if (sEgl == null) {
                throw new RuntimeException("egl not initialized");
            }
            if (sEglDisplay == null) {
                throw new RuntimeException("eglDisplay not initialized");
            }
            if (sEglConfig == null) {
                throw new RuntimeException("eglConfig not initialized");
            }
            if (Thread.currentThread() != this.mEglThread) {
                throw new IllegalStateException("HardwareRenderer cannot be used from multiple threads");
            }
            destroySurface();
            if (!createSurface(surface)) {
                return null;
            }
            initCaches();
            return this.mEglContext.getGL();
        }

        private void enableDirtyRegions() {
            if (sDirtyRegions) {
                boolean preserveBackBuffer = preserveBackBuffer();
                this.mDirtyRegionsEnabled = preserveBackBuffer;
                if (!preserveBackBuffer) {
                    Log.w(HardwareRenderer.LOG_TAG, "Backbuffer cannot be preserved");
                }
            } else if (sDirtyRegionsRequested) {
                this.mDirtyRegionsEnabled = isBackBufferPreserved();
            }
        }

        EGLContext createContext(EGL10 egl, EGLDisplay eglDisplay, EGLConfig eglConfig) {
            int[] attribs = {12440, this.mGlVersion, 12344};
            EGLContext context = egl.eglCreateContext(eglDisplay, eglConfig, EGL10.EGL_NO_CONTEXT, this.mGlVersion != 0 ? attribs : null);
            if (context == null || context == EGL10.EGL_NO_CONTEXT) {
                throw new IllegalStateException("Could not create an EGL context. eglCreateContext failed with error: " + GLUtils.getEGLErrorString(sEgl.eglGetError()));
            }
            return context;
        }

        @Override // android.view.HardwareRenderer
        void destroy(boolean full) {
            if (full && this.mCanvas != null) {
                this.mCanvas = null;
            }
            if (!isEnabled() || this.mDestroyed) {
                setEnabled(false);
                return;
            }
            destroySurface();
            setEnabled(false);
            this.mDestroyed = true;
            this.mGl = null;
        }

        void destroySurface() {
            if (this.mEglSurface != null && this.mEglSurface != EGL10.EGL_NO_SURFACE) {
                if (this.mEglSurface.equals(sEgl.eglGetCurrentSurface(12377))) {
                    sEgl.eglMakeCurrent(sEglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
                }
                sEgl.eglDestroySurface(sEglDisplay, this.mEglSurface);
                this.mEglSurface = null;
            }
        }

        @Override // android.view.HardwareRenderer
        void invalidate(Surface surface) {
            sEgl.eglMakeCurrent(sEglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
            if (this.mEglSurface != null && this.mEglSurface != EGL10.EGL_NO_SURFACE) {
                sEgl.eglDestroySurface(sEglDisplay, this.mEglSurface);
                this.mEglSurface = null;
                setEnabled(false);
            }
            if (!surface.isValid() || !createSurface(surface)) {
                return;
            }
            this.mUpdateDirtyRegions = true;
            if (this.mCanvas != null) {
                setEnabled(true);
            }
        }

        private boolean createSurface(Surface surface) {
            this.mEglSurface = sEgl.eglCreateWindowSurface(sEglDisplay, sEglConfig, surface, null);
            if (this.mEglSurface == null || this.mEglSurface == EGL10.EGL_NO_SURFACE) {
                int error = sEgl.eglGetError();
                if (error == 12299) {
                    Log.e(HardwareRenderer.LOG_TAG, "createWindowSurface returned EGL_BAD_NATIVE_WINDOW.");
                    return false;
                }
                throw new RuntimeException("createWindowSurface failed " + GLUtils.getEGLErrorString(error));
            } else if (!sEgl.eglMakeCurrent(sEglDisplay, this.mEglSurface, this.mEglSurface, this.mEglContext)) {
                throw new IllegalStateException("eglMakeCurrent failed " + GLUtils.getEGLErrorString(sEgl.eglGetError()));
            } else {
                enableDirtyRegions();
                return true;
            }
        }

        @Override // android.view.HardwareRenderer
        boolean validate() {
            return checkRenderContext() != 0;
        }

        @Override // android.view.HardwareRenderer
        void setup(int width, int height) {
            if (validate()) {
                this.mCanvas.setViewport(width, height);
                this.mWidth = width;
                this.mHeight = height;
            }
        }

        @Override // android.view.HardwareRenderer
        int getWidth() {
            return this.mWidth;
        }

        @Override // android.view.HardwareRenderer
        int getHeight() {
            return this.mHeight;
        }

        @Override // android.view.HardwareRenderer
        HardwareCanvas getCanvas() {
            return this.mCanvas;
        }

        @Override // android.view.HardwareRenderer
        void setName(String name) {
            this.mName = name;
        }

        boolean canDraw() {
            return (this.mGl == null || this.mCanvas == null) ? false : true;
        }

        int onPreDraw(Rect dirty) {
            return 0;
        }

        void onPostDraw() {
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: HardwareRenderer$GlRenderer$FunctorsRunnable.class */
        public class FunctorsRunnable implements Runnable {
            View.AttachInfo attachInfo;

            FunctorsRunnable() {
            }

            @Override // java.lang.Runnable
            public void run() {
                HardwareRenderer renderer = this.attachInfo.mHardwareRenderer;
                if (renderer != null && renderer.isEnabled() && renderer == GlRenderer.this && GlRenderer.this.checkRenderContext() != 0) {
                    int status = GlRenderer.this.mCanvas.invokeFunctors(GlRenderer.this.mRedrawClip);
                    GlRenderer.this.handleFunctorStatus(this.attachInfo, status);
                }
            }
        }

        @Override // android.view.HardwareRenderer
        void draw(View view, View.AttachInfo attachInfo, HardwareDrawCallbacks callbacks, Rect dirty) {
            if (canDraw()) {
                if (!hasDirtyRegions()) {
                    dirty = null;
                }
                attachInfo.mIgnoreDirtyState = true;
                attachInfo.mDrawingTime = SystemClock.uptimeMillis();
                view.mPrivateFlags |= 32;
                int surfaceState = checkRenderContextUnsafe();
                if (surfaceState != 0) {
                    HardwareCanvas canvas = this.mCanvas;
                    attachInfo.mHardwareCanvas = canvas;
                    if (this.mProfileEnabled) {
                        this.mProfileLock.lock();
                    }
                    Rect dirty2 = beginFrame(canvas, dirty, surfaceState);
                    DisplayList displayList = buildDisplayList(view, canvas);
                    if (checkRenderContextUnsafe() == 0) {
                        return;
                    }
                    int status = 0;
                    long start = getSystemTime();
                    try {
                        try {
                            status = prepareFrame(dirty2);
                            int saveCount = canvas.save();
                            callbacks.onHardwarePreDraw(canvas);
                            if (displayList != null) {
                                status |= drawDisplayList(attachInfo, canvas, displayList, status);
                            } else {
                                view.draw(canvas);
                            }
                            callbacks.onHardwarePostDraw(canvas);
                            canvas.restoreToCount(saveCount);
                            view.mRecreateDisplayList = false;
                            this.mDrawDelta = getSystemTime() - start;
                            if (this.mDrawDelta > 0) {
                                this.mFrameCount++;
                                debugOverdraw(attachInfo, dirty2, canvas, displayList);
                                debugDirtyRegions(dirty2, canvas);
                                drawProfileData(attachInfo);
                            }
                        } catch (Exception e) {
                            Log.e(HardwareRenderer.LOG_TAG, "An error has occurred while drawing:", e);
                            callbacks.onHardwarePostDraw(canvas);
                            canvas.restoreToCount(0);
                            view.mRecreateDisplayList = false;
                            this.mDrawDelta = getSystemTime() - start;
                            if (this.mDrawDelta > 0) {
                                this.mFrameCount++;
                                debugOverdraw(attachInfo, dirty2, canvas, displayList);
                                debugDirtyRegions(dirty2, canvas);
                                drawProfileData(attachInfo);
                            }
                        }
                        onPostDraw();
                        swapBuffers(status);
                        if (this.mProfileEnabled) {
                            this.mProfileLock.unlock();
                        }
                        attachInfo.mIgnoreDirtyState = false;
                    } catch (Throwable th) {
                        callbacks.onHardwarePostDraw(canvas);
                        canvas.restoreToCount(0);
                        view.mRecreateDisplayList = false;
                        this.mDrawDelta = getSystemTime() - start;
                        if (this.mDrawDelta > 0) {
                            this.mFrameCount++;
                            debugOverdraw(attachInfo, dirty2, canvas, displayList);
                            debugDirtyRegions(dirty2, canvas);
                            drawProfileData(attachInfo);
                        }
                        throw th;
                    }
                }
            }
        }

        private void debugOverdraw(View.AttachInfo attachInfo, Rect dirty, HardwareCanvas canvas, DisplayList displayList) {
            if (this.mDebugOverdraw == 1) {
                if (this.mDebugOverdrawLayer == null) {
                    this.mDebugOverdrawLayer = createHardwareLayer(this.mWidth, this.mHeight, true);
                } else if (this.mDebugOverdrawLayer.getWidth() != this.mWidth || this.mDebugOverdrawLayer.getHeight() != this.mHeight) {
                    this.mDebugOverdrawLayer.resize(this.mWidth, this.mHeight);
                }
                if (!this.mDebugOverdrawLayer.isValid()) {
                    this.mDebugOverdraw = -1;
                    return;
                }
                HardwareCanvas layerCanvas = this.mDebugOverdrawLayer.start(canvas, dirty);
                countOverdraw(layerCanvas);
                int restoreCount = layerCanvas.save();
                layerCanvas.drawDisplayList(displayList, null, 1);
                layerCanvas.restoreToCount(restoreCount);
                this.mDebugOverdrawLayer.end(canvas);
                float overdraw = getOverdraw(layerCanvas);
                DisplayMetrics metrics = attachInfo.mRootView.getResources().getDisplayMetrics();
                drawOverdrawCounter(canvas, overdraw, metrics.density);
            }
        }

        private void drawOverdrawCounter(HardwareCanvas canvas, float overdraw, float density) {
            String text = String.format("%.2fx", Float.valueOf(overdraw));
            Paint paint = setupPaint(density);
            paint.setColor(Color.HSBtoColor(0.28f - ((0.28f * overdraw) / 3.5f), 0.8f, 1.0f));
            canvas.drawText(text, density * 4.0f, this.mHeight - paint.getFontMetrics().bottom, paint);
        }

        private Paint setupPaint(float density) {
            if (this.mDebugOverdrawPaint == null) {
                this.mDebugOverdrawPaint = new Paint();
                this.mDebugOverdrawPaint.setAntiAlias(true);
                this.mDebugOverdrawPaint.setShadowLayer(density * 3.0f, 0.0f, 0.0f, -16777216);
                this.mDebugOverdrawPaint.setTextSize(density * 20.0f);
            }
            return this.mDebugOverdrawPaint;
        }

        private DisplayList buildDisplayList(View view, HardwareCanvas canvas) {
            if (this.mDrawDelta <= 0) {
                return view.mDisplayList;
            }
            view.mRecreateDisplayList = (view.mPrivateFlags & Integer.MIN_VALUE) == Integer.MIN_VALUE;
            view.mPrivateFlags &= Integer.MAX_VALUE;
            long buildDisplayListStartTime = startBuildDisplayListProfiling();
            canvas.clearLayerUpdates();
            Trace.traceBegin(8L, "getDisplayList");
            DisplayList displayList = view.getDisplayList();
            Trace.traceEnd(8L);
            endBuildDisplayListProfiling(buildDisplayListStartTime);
            return displayList;
        }

        private Rect beginFrame(HardwareCanvas canvas, Rect dirty, int surfaceState) {
            if (surfaceState == 2) {
                dirty = null;
                beginFrame(null);
            } else {
                int[] size = this.mSurfaceSize;
                beginFrame(size);
                if (size[1] != this.mHeight || size[0] != this.mWidth) {
                    this.mWidth = size[0];
                    this.mHeight = size[1];
                    canvas.setViewport(this.mWidth, this.mHeight);
                    dirty = null;
                }
            }
            if (this.mDebugDataProvider != null) {
                dirty = null;
            }
            return dirty;
        }

        private long startBuildDisplayListProfiling() {
            if (this.mProfileEnabled) {
                this.mProfileCurrentFrame += 3;
                if (this.mProfileCurrentFrame >= this.mProfileData.length) {
                    this.mProfileCurrentFrame = 0;
                }
                return System.nanoTime();
            }
            return 0L;
        }

        private void endBuildDisplayListProfiling(long getDisplayListStartTime) {
            if (this.mProfileEnabled) {
                long now = System.nanoTime();
                float total = ((float) (now - getDisplayListStartTime)) * 1.0E-6f;
                this.mProfileData[this.mProfileCurrentFrame] = total;
            }
        }

        private int prepareFrame(Rect dirty) {
            Trace.traceBegin(8L, "prepareFrame");
            try {
                int status = onPreDraw(dirty);
                Trace.traceEnd(8L);
                return status;
            } catch (Throwable th) {
                Trace.traceEnd(8L);
                throw th;
            }
        }

        private int drawDisplayList(View.AttachInfo attachInfo, HardwareCanvas canvas, DisplayList displayList, int status) {
            long drawDisplayListStartTime = 0;
            if (this.mProfileEnabled) {
                drawDisplayListStartTime = System.nanoTime();
            }
            Trace.traceBegin(8L, "drawDisplayList");
            try {
                int status2 = status | canvas.drawDisplayList(displayList, this.mRedrawClip, 1);
                Trace.traceEnd(8L);
                if (this.mProfileEnabled) {
                    long now = System.nanoTime();
                    float total = ((float) (now - drawDisplayListStartTime)) * 1.0E-6f;
                    this.mProfileData[this.mProfileCurrentFrame + 1] = total;
                }
                handleFunctorStatus(attachInfo, status2);
                return status2;
            } catch (Throwable th) {
                Trace.traceEnd(8L);
                throw th;
            }
        }

        private void swapBuffers(int status) {
            if ((status & 4) == 4) {
                long eglSwapBuffersStartTime = 0;
                if (this.mProfileEnabled) {
                    eglSwapBuffersStartTime = System.nanoTime();
                }
                sEgl.eglSwapBuffers(sEglDisplay, this.mEglSurface);
                if (this.mProfileEnabled) {
                    long now = System.nanoTime();
                    float total = ((float) (now - eglSwapBuffersStartTime)) * 1.0E-6f;
                    this.mProfileData[this.mProfileCurrentFrame + 2] = total;
                }
                checkEglErrors();
            }
        }

        private void debugDirtyRegions(Rect dirty, HardwareCanvas canvas) {
            if (this.mDebugDirtyRegions) {
                if (this.mDebugPaint == null) {
                    this.mDebugPaint = new Paint();
                    this.mDebugPaint.setColor(2147418112);
                }
                if (dirty != null && (this.mFrameCount & 1) == 0) {
                    canvas.drawRect(dirty, this.mDebugPaint);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void handleFunctorStatus(View.AttachInfo attachInfo, int status) {
            if ((status & 1) != 0) {
                if (this.mRedrawClip.isEmpty()) {
                    attachInfo.mViewRootImpl.invalidate();
                } else {
                    attachInfo.mViewRootImpl.invalidateChildInParent(null, this.mRedrawClip);
                    this.mRedrawClip.setEmpty();
                }
            }
            if ((status & 2) != 0 || attachInfo.mHandler.hasCallbacks(this.mFunctorsRunnable)) {
                attachInfo.mHandler.removeCallbacks(this.mFunctorsRunnable);
                this.mFunctorsRunnable.attachInfo = attachInfo;
                attachInfo.mHandler.postDelayed(this.mFunctorsRunnable, 4L);
            }
        }

        @Override // android.view.HardwareRenderer
        void detachFunctor(int functor) {
            if (this.mCanvas != null) {
                this.mCanvas.detachFunctor(functor);
            }
        }

        @Override // android.view.HardwareRenderer
        boolean attachFunctor(View.AttachInfo attachInfo, int functor) {
            if (this.mCanvas != null) {
                this.mCanvas.attachFunctor(functor);
                this.mFunctorsRunnable.attachInfo = attachInfo;
                attachInfo.mHandler.removeCallbacks(this.mFunctorsRunnable);
                attachInfo.mHandler.postDelayed(this.mFunctorsRunnable, 0L);
                return true;
            }
            return false;
        }

        int checkRenderContext() {
            if (this.mEglThread != Thread.currentThread()) {
                throw new IllegalStateException("Hardware acceleration can only be used with a single UI thread.\nOriginal thread: " + this.mEglThread + Separators.RETURN + "Current thread: " + Thread.currentThread());
            }
            return checkRenderContextUnsafe();
        }

        private int checkRenderContextUnsafe() {
            if (!this.mEglSurface.equals(sEgl.eglGetCurrentSurface(12377)) || !this.mEglContext.equals(sEgl.eglGetCurrentContext())) {
                if (!sEgl.eglMakeCurrent(sEglDisplay, this.mEglSurface, this.mEglSurface, this.mEglContext)) {
                    Log.e(HardwareRenderer.LOG_TAG, "eglMakeCurrent failed " + GLUtils.getEGLErrorString(sEgl.eglGetError()));
                    fallback(true);
                    return 0;
                } else if (this.mUpdateDirtyRegions) {
                    enableDirtyRegions();
                    this.mUpdateDirtyRegions = false;
                    return 2;
                } else {
                    return 2;
                }
            }
            return 1;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static int dpToPx(int dp, float density) {
            return (int) ((dp * density) + 0.5f);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: HardwareRenderer$GlRenderer$DrawPerformanceDataProvider.class */
        public class DrawPerformanceDataProvider extends GraphDataProvider {
            private final int mGraphType;
            private int mVerticalUnit;
            private int mHorizontalUnit;
            private int mHorizontalMargin;
            private int mThresholdStroke;

            DrawPerformanceDataProvider(int graphType) {
                super();
                this.mGraphType = graphType;
            }

            @Override // android.view.HardwareRenderer.GraphDataProvider
            void prepare(DisplayMetrics metrics) {
                float density = metrics.density;
                this.mVerticalUnit = GlRenderer.dpToPx(7, density);
                this.mHorizontalUnit = GlRenderer.dpToPx(3, density);
                this.mHorizontalMargin = GlRenderer.dpToPx(0, density);
                this.mThresholdStroke = GlRenderer.dpToPx(2, density);
            }

            @Override // android.view.HardwareRenderer.GraphDataProvider
            int getGraphType() {
                return this.mGraphType;
            }

            @Override // android.view.HardwareRenderer.GraphDataProvider
            int getVerticalUnitSize() {
                return this.mVerticalUnit;
            }

            @Override // android.view.HardwareRenderer.GraphDataProvider
            int getHorizontalUnitSize() {
                return this.mHorizontalUnit;
            }

            @Override // android.view.HardwareRenderer.GraphDataProvider
            int getHorizontaUnitMargin() {
                return this.mHorizontalMargin;
            }

            @Override // android.view.HardwareRenderer.GraphDataProvider
            float[] getData() {
                return GlRenderer.this.mProfileData;
            }

            @Override // android.view.HardwareRenderer.GraphDataProvider
            float getThreshold() {
                return 16.0f;
            }

            @Override // android.view.HardwareRenderer.GraphDataProvider
            int getFrameCount() {
                return GlRenderer.this.mProfileData.length / 3;
            }

            @Override // android.view.HardwareRenderer.GraphDataProvider
            int getElementCount() {
                return 3;
            }

            @Override // android.view.HardwareRenderer.GraphDataProvider
            int getCurrentFrame() {
                return GlRenderer.this.mProfileCurrentFrame / 3;
            }

            @Override // android.view.HardwareRenderer.GraphDataProvider
            void setupGraphPaint(Paint paint, int elementIndex) {
                paint.setColor(GlRenderer.PROFILE_DRAW_COLORS[elementIndex]);
                if (this.mGraphType == 1) {
                    paint.setStrokeWidth(this.mThresholdStroke);
                }
            }

            @Override // android.view.HardwareRenderer.GraphDataProvider
            void setupThresholdPaint(Paint paint) {
                paint.setColor(GlRenderer.PROFILE_DRAW_THRESHOLD_COLOR);
                paint.setStrokeWidth(this.mThresholdStroke);
            }

            @Override // android.view.HardwareRenderer.GraphDataProvider
            void setupCurrentFramePaint(Paint paint) {
                paint.setColor(GlRenderer.PROFILE_DRAW_CURRENT_FRAME_COLOR);
                if (this.mGraphType == 1) {
                    paint.setStrokeWidth(this.mThresholdStroke);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: HardwareRenderer$Gl20Renderer.class */
    public static class Gl20Renderer extends GlRenderer {
        private GLES20Canvas mGlCanvas;
        private DisplayMetrics mDisplayMetrics;
        private static EGLSurface sPbuffer;
        private static final Object[] sPbufferLock = new Object[0];

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: HardwareRenderer$Gl20Renderer$Gl20RendererEglContext.class */
        public static class Gl20RendererEglContext extends ManagedEGLContext {
            final Handler mHandler;

            public Gl20RendererEglContext(EGLContext context) {
                super(context);
                this.mHandler = new Handler();
            }

            @Override // android.opengl.ManagedEGLContext
            public void onTerminate(final EGLContext eglContext) {
                if (this.mHandler.getLooper() != Looper.myLooper()) {
                    this.mHandler.post(new Runnable() { // from class: android.view.HardwareRenderer.Gl20Renderer.Gl20RendererEglContext.1
                        @Override // java.lang.Runnable
                        public void run() {
                            Gl20RendererEglContext.this.onTerminate(eglContext);
                        }
                    });
                    return;
                }
                synchronized (GlRenderer.sEglLock) {
                    if (GlRenderer.sEgl == null) {
                        return;
                    }
                    if (EGLImpl.getInitCount(GlRenderer.sEglDisplay) == 1) {
                        Gl20Renderer.usePbufferSurface(eglContext);
                        GLES20Canvas.terminateCaches();
                        GlRenderer.sEgl.eglDestroyContext(GlRenderer.sEglDisplay, eglContext);
                        GlRenderer.sEglContextStorage.set(null);
                        GlRenderer.sEglContextStorage.remove();
                        GlRenderer.sEgl.eglDestroySurface(GlRenderer.sEglDisplay, Gl20Renderer.sPbuffer);
                        GlRenderer.sEgl.eglMakeCurrent(GlRenderer.sEglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
                        GlRenderer.sEgl.eglReleaseThread();
                        GlRenderer.sEgl.eglTerminate(GlRenderer.sEglDisplay);
                        GlRenderer.sEgl = null;
                        GlRenderer.sEglDisplay = null;
                        GlRenderer.sEglConfig = null;
                        EGLSurface unused = Gl20Renderer.sPbuffer = null;
                    }
                }
            }
        }

        Gl20Renderer(boolean translucent) {
            super(2, translucent);
        }

        @Override // android.view.HardwareRenderer.GlRenderer
        HardwareCanvas createCanvas() {
            GLES20Canvas gLES20Canvas = new GLES20Canvas(this.mTranslucent);
            this.mGlCanvas = gLES20Canvas;
            return gLES20Canvas;
        }

        @Override // android.view.HardwareRenderer.GlRenderer
        ManagedEGLContext createManagedContext(EGLContext eglContext) {
            return new Gl20RendererEglContext(this.mEglContext);
        }

        @Override // android.view.HardwareRenderer.GlRenderer
        int[] getConfig(boolean dirtyRegions) {
            int stencilSize = GLES20Canvas.getStencilSize();
            int swapBehavior = dirtyRegions ? 1024 : 0;
            return new int[]{12352, 4, 12324, 8, 12323, 8, 12322, 8, 12321, 8, 12325, 0, 12327, 12344, 12326, stencilSize, 12339, 4 | swapBehavior, 12344};
        }

        @Override // android.view.HardwareRenderer.GlRenderer
        void initCaches() {
            if (GLES20Canvas.initCaches()) {
                initAtlas();
            }
        }

        @Override // android.view.HardwareRenderer.GlRenderer
        void initAtlas() {
            GraphicBuffer buffer;
            IBinder binder = ServiceManager.getService(AssetAtlasService.ASSET_ATLAS_SERVICE);
            if (binder == null) {
                return;
            }
            IAssetAtlas atlas = IAssetAtlas.Stub.asInterface(binder);
            try {
                if (atlas.isCompatible(Process.myPpid()) && (buffer = atlas.getBuffer()) != null) {
                    int[] map = atlas.getMap();
                    if (map != null) {
                        GLES20Canvas.initAtlas(buffer, map);
                    }
                    if (atlas.getClass() != binder.getClass()) {
                        buffer.destroy();
                    }
                }
            } catch (RemoteException e) {
                Log.w(HardwareRenderer.LOG_TAG, "Could not acquire atlas", e);
            }
        }

        @Override // android.view.HardwareRenderer.GlRenderer
        boolean canDraw() {
            return super.canDraw() && this.mGlCanvas != null;
        }

        @Override // android.view.HardwareRenderer.GlRenderer
        int onPreDraw(Rect dirty) {
            return this.mGlCanvas.onPreDraw(dirty);
        }

        @Override // android.view.HardwareRenderer.GlRenderer
        void onPostDraw() {
            this.mGlCanvas.onPostDraw();
        }

        @Override // android.view.HardwareRenderer.GlRenderer
        void drawProfileData(View.AttachInfo attachInfo) {
            if (this.mDebugDataProvider != null) {
                GraphDataProvider provider = this.mDebugDataProvider;
                initProfileDrawData(attachInfo, provider);
                int height = provider.getVerticalUnitSize();
                int margin = provider.getHorizontaUnitMargin();
                int width = provider.getHorizontalUnitSize();
                int x = 0;
                int count = 0;
                int current = 0;
                float[] data = provider.getData();
                int elementCount = provider.getElementCount();
                int graphType = provider.getGraphType();
                int totalCount = provider.getFrameCount() * elementCount;
                if (graphType == 1) {
                    totalCount -= elementCount;
                }
                int i = 0;
                while (true) {
                    int i2 = i;
                    if (i2 < totalCount && data[i2] >= 0.0f) {
                        int index = count * 4;
                        if (i2 == provider.getCurrentFrame() * elementCount) {
                            current = index;
                        }
                        int x2 = x + margin;
                        int x22 = x2 + width;
                        int y2 = this.mHeight;
                        int y1 = (int) (y2 - (data[i2] * height));
                        switch (graphType) {
                            case 0:
                                for (int j = 0; j < elementCount; j++) {
                                    float[] r = this.mProfileShapes[j];
                                    r[index] = x2;
                                    r[index + 1] = y1;
                                    r[index + 2] = x22;
                                    r[index + 3] = y2;
                                    y2 = y1;
                                    if (j < elementCount - 1) {
                                        y1 = (int) (y2 - (data[(i2 + j) + 1] * height));
                                    }
                                }
                                break;
                            case 1:
                                for (int j2 = 0; j2 < elementCount; j2++) {
                                    float[] r2 = this.mProfileShapes[j2];
                                    r2[index] = (x2 + x22) * 0.5f;
                                    r2[index + 1] = index == 0 ? y1 : r2[index - 1];
                                    r2[index + 2] = r2[index] + width;
                                    r2[index + 3] = y1;
                                    int y22 = y1;
                                    if (j2 < elementCount - 1) {
                                        y1 = (int) (y22 - (data[(i2 + j2) + 1] * height));
                                    }
                                }
                                break;
                        }
                        x = x2 + width;
                        count++;
                        i = i2 + elementCount;
                    }
                }
                drawGraph(graphType, count);
                drawCurrentFrame(graphType, current);
                drawThreshold(x + margin, height);
            }
        }

        private void drawGraph(int graphType, int count) {
            for (int i = 0; i < this.mProfileShapes.length; i++) {
                this.mDebugDataProvider.setupGraphPaint(this.mProfilePaint, i);
                switch (graphType) {
                    case 0:
                        this.mGlCanvas.drawRects(this.mProfileShapes[i], count * 4, this.mProfilePaint);
                        break;
                    case 1:
                        this.mGlCanvas.drawLines(this.mProfileShapes[i], 0, count * 4, this.mProfilePaint);
                        break;
                }
            }
        }

        private void drawCurrentFrame(int graphType, int index) {
            if (index >= 0) {
                this.mDebugDataProvider.setupCurrentFramePaint(this.mProfilePaint);
                switch (graphType) {
                    case 0:
                        this.mGlCanvas.drawRect(this.mProfileShapes[2][index], this.mProfileShapes[2][index + 1], this.mProfileShapes[2][index + 2], this.mProfileShapes[0][index + 3], this.mProfilePaint);
                        return;
                    case 1:
                        this.mGlCanvas.drawLine(this.mProfileShapes[2][index], this.mProfileShapes[2][index + 1], this.mProfileShapes[2][index], this.mHeight, this.mProfilePaint);
                        return;
                    default:
                        return;
                }
            }
        }

        private void drawThreshold(int x, int height) {
            float threshold = this.mDebugDataProvider.getThreshold();
            if (threshold > 0.0f) {
                this.mDebugDataProvider.setupThresholdPaint(this.mProfilePaint);
                int y = (int) (this.mHeight - (threshold * height));
                this.mGlCanvas.drawLine(0.0f, y, x, y, this.mProfilePaint);
            }
        }

        /* JADX WARN: Type inference failed for: r1v8, types: [float[], float[][]] */
        private void initProfileDrawData(View.AttachInfo attachInfo, GraphDataProvider provider) {
            if (this.mProfileShapes == null) {
                int elementCount = provider.getElementCount();
                int frameCount = provider.getFrameCount();
                this.mProfileShapes = new float[elementCount];
                for (int i = 0; i < elementCount; i++) {
                    this.mProfileShapes[i] = new float[frameCount * 4];
                }
                this.mProfilePaint = new Paint();
            }
            this.mProfilePaint.reset();
            if (provider.getGraphType() == 1) {
                this.mProfilePaint.setAntiAlias(true);
            }
            if (this.mDisplayMetrics == null) {
                this.mDisplayMetrics = new DisplayMetrics();
            }
            attachInfo.mDisplay.getMetrics(this.mDisplayMetrics);
            provider.prepare(this.mDisplayMetrics);
        }

        @Override // android.view.HardwareRenderer.GlRenderer, android.view.HardwareRenderer
        void destroy(boolean full) {
            try {
                super.destroy(full);
                if (full && this.mGlCanvas != null) {
                    this.mGlCanvas = null;
                }
            } catch (Throwable th) {
                if (full && this.mGlCanvas != null) {
                    this.mGlCanvas = null;
                }
                throw th;
            }
        }

        @Override // android.view.HardwareRenderer
        void pushLayerUpdate(HardwareLayer layer) {
            this.mGlCanvas.pushLayerUpdate(layer);
        }

        @Override // android.view.HardwareRenderer
        void cancelLayerUpdate(HardwareLayer layer) {
            this.mGlCanvas.cancelLayerUpdate(layer);
        }

        @Override // android.view.HardwareRenderer
        void flushLayerUpdates() {
            this.mGlCanvas.flushLayerUpdates();
        }

        @Override // android.view.HardwareRenderer
        public DisplayList createDisplayList(String name) {
            return new GLES20DisplayList(name);
        }

        @Override // android.view.HardwareRenderer
        HardwareLayer createHardwareLayer(boolean isOpaque) {
            return new GLES20TextureLayer(isOpaque);
        }

        @Override // android.view.HardwareRenderer
        public HardwareLayer createHardwareLayer(int width, int height, boolean isOpaque) {
            return new GLES20RenderLayer(width, height, isOpaque);
        }

        @Override // android.view.HardwareRenderer.GlRenderer
        void countOverdraw(HardwareCanvas canvas) {
            ((GLES20Canvas) canvas).setCountOverdrawEnabled(true);
        }

        @Override // android.view.HardwareRenderer.GlRenderer
        float getOverdraw(HardwareCanvas canvas) {
            return ((GLES20Canvas) canvas).getOverdraw();
        }

        @Override // android.view.HardwareRenderer
        public SurfaceTexture createSurfaceTexture(HardwareLayer layer) {
            return ((GLES20TextureLayer) layer).getSurfaceTexture();
        }

        @Override // android.view.HardwareRenderer
        void setSurfaceTexture(HardwareLayer layer, SurfaceTexture surfaceTexture) {
            ((GLES20TextureLayer) layer).setSurfaceTexture(surfaceTexture);
        }

        @Override // android.view.HardwareRenderer
        boolean safelyRun(Runnable action) {
            boolean needsContext = !isEnabled() || checkRenderContext() == 0;
            if (needsContext) {
                Gl20RendererEglContext managedContext = (Gl20RendererEglContext) sEglContextStorage.get();
                if (managedContext == null) {
                    return false;
                }
                usePbufferSurface(managedContext.getContext());
            }
            try {
                action.run();
                if (needsContext) {
                    sEgl.eglMakeCurrent(sEglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
                    return true;
                }
                return true;
            } catch (Throwable th) {
                if (needsContext) {
                    sEgl.eglMakeCurrent(sEglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
                }
                throw th;
            }
        }

        @Override // android.view.HardwareRenderer
        void destroyLayers(final View view) {
            if (view != null) {
                safelyRun(new Runnable() { // from class: android.view.HardwareRenderer.Gl20Renderer.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (Gl20Renderer.this.mCanvas != null) {
                            Gl20Renderer.this.mCanvas.clearLayerUpdates();
                        }
                        Gl20Renderer.destroyHardwareLayer(view);
                        GLES20Canvas.flushCaches(0);
                    }
                });
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static void destroyHardwareLayer(View view) {
            view.destroyLayer(true);
            if (view instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) view;
                int count = group.getChildCount();
                for (int i = 0; i < count; i++) {
                    destroyHardwareLayer(group.getChildAt(i));
                }
            }
        }

        @Override // android.view.HardwareRenderer
        void destroyHardwareResources(final View view) {
            if (view != null) {
                safelyRun(new Runnable() { // from class: android.view.HardwareRenderer.Gl20Renderer.2
                    @Override // java.lang.Runnable
                    public void run() {
                        if (Gl20Renderer.this.mCanvas != null) {
                            Gl20Renderer.this.mCanvas.clearLayerUpdates();
                        }
                        Gl20Renderer.destroyResources(view);
                        GLES20Canvas.flushCaches(0);
                    }
                });
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static void destroyResources(View view) {
            view.destroyHardwareResources();
            if (view instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) view;
                int count = group.getChildCount();
                for (int i = 0; i < count; i++) {
                    destroyResources(group.getChildAt(i));
                }
            }
        }

        static HardwareRenderer create(boolean translucent) {
            if (GLES20Canvas.isAvailable()) {
                return new Gl20Renderer(translucent);
            }
            return null;
        }

        static void startTrimMemory(int level) {
            Gl20RendererEglContext managedContext;
            if (sEgl == null || sEglConfig == null || (managedContext = (Gl20RendererEglContext) sEglContextStorage.get()) == null) {
                return;
            }
            usePbufferSurface(managedContext.getContext());
            if (level >= 80) {
                GLES20Canvas.flushCaches(2);
            } else if (level >= 20) {
                GLES20Canvas.flushCaches(1);
            }
        }

        static void endTrimMemory() {
            if (sEgl != null && sEglDisplay != null) {
                sEgl.eglMakeCurrent(sEglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static void usePbufferSurface(EGLContext eglContext) {
            synchronized (sPbufferLock) {
                if (sPbuffer == null) {
                    sPbuffer = sEgl.eglCreatePbufferSurface(sEglDisplay, sEglConfig, new int[]{12375, 1, 12374, 1, 12344});
                }
            }
            sEgl.eglMakeCurrent(sEglDisplay, sPbuffer, sPbuffer, eglContext);
        }
    }
}