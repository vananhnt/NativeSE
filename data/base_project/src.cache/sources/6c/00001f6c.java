package com.android.server.power;

import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES10;
import android.opengl.GLES11Ext;
import android.util.FloatMath;
import android.util.Slog;
import android.view.DisplayInfo;
import android.view.Surface;
import android.view.SurfaceControl;
import android.view.SurfaceSession;
import com.android.server.display.DisplayManagerService;
import com.android.server.display.DisplayTransactionListener;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: ElectronBeam.class */
public final class ElectronBeam {
    private static final String TAG = "ElectronBeam";
    private static final boolean DEBUG = false;
    private static final int ELECTRON_BEAM_LAYER = 1073741825;
    private static final float HSTRETCH_DURATION = 0.5f;
    private static final float VSTRETCH_DURATION = 0.5f;
    private static final int DEJANK_FRAMES = 3;
    private boolean mPrepared;
    private int mMode;
    private final DisplayManagerService mDisplayManager;
    private int mDisplayLayerStack;
    private int mDisplayWidth;
    private int mDisplayHeight;
    private SurfaceSession mSurfaceSession;
    private SurfaceControl mSurfaceControl;
    private Surface mSurface;
    private NaturalSurfaceLayout mSurfaceLayout;
    private EGLDisplay mEglDisplay;
    private EGLConfig mEglConfig;
    private EGLContext mEglContext;
    private EGLSurface mEglSurface;
    private boolean mSurfaceVisible;
    private float mSurfaceAlpha;
    private boolean mTexNamesGenerated;
    public static final int MODE_WARM_UP = 0;
    public static final int MODE_COOL_DOWN = 1;
    public static final int MODE_FADE = 2;
    private final int[] mTexNames = new int[1];
    private final float[] mTexMatrix = new float[16];
    private final FloatBuffer mVertexBuffer = createNativeFloatBuffer(8);
    private final FloatBuffer mTexCoordBuffer = createNativeFloatBuffer(8);

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.ElectronBeam.draw(float):boolean, file: ElectronBeam.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    public boolean draw(float r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.ElectronBeam.draw(float):boolean, file: ElectronBeam.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.ElectronBeam.draw(float):boolean");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.ElectronBeam.captureScreenshotTextureAndSetViewport():boolean, file: ElectronBeam.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private boolean captureScreenshotTextureAndSetViewport() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.ElectronBeam.captureScreenshotTextureAndSetViewport():boolean, file: ElectronBeam.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.ElectronBeam.captureScreenshotTextureAndSetViewport():boolean");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.ElectronBeam.destroyScreenshotTexture():void, file: ElectronBeam.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private void destroyScreenshotTexture() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.ElectronBeam.destroyScreenshotTexture():void, file: ElectronBeam.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.ElectronBeam.destroyScreenshotTexture():void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.ElectronBeam.createSurface():boolean, file: ElectronBeam.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private boolean createSurface() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.ElectronBeam.createSurface():boolean, file: ElectronBeam.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.ElectronBeam.createSurface():boolean");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.ElectronBeam.destroySurface():void, file: ElectronBeam.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private void destroySurface() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.ElectronBeam.destroySurface():void, file: ElectronBeam.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.ElectronBeam.destroySurface():void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.ElectronBeam.showSurface(float):boolean, file: ElectronBeam.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private boolean showSurface(float r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.power.ElectronBeam.showSurface(float):boolean, file: ElectronBeam.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.ElectronBeam.showSurface(float):boolean");
    }

    public ElectronBeam(DisplayManagerService displayManager) {
        this.mDisplayManager = displayManager;
    }

    public boolean prepare(int mode) {
        this.mMode = mode;
        DisplayInfo displayInfo = this.mDisplayManager.getDisplayInfo(0);
        this.mDisplayLayerStack = displayInfo.layerStack;
        this.mDisplayWidth = displayInfo.getNaturalWidth();
        this.mDisplayHeight = displayInfo.getNaturalHeight();
        if (!tryPrepare()) {
            dismiss();
            return false;
        }
        this.mPrepared = true;
        if (mode == 1) {
            for (int i = 0; i < 3; i++) {
                draw(1.0f);
            }
            return true;
        }
        return true;
    }

    private boolean tryPrepare() {
        if (createSurface()) {
            if (this.mMode == 2) {
                return true;
            }
            return createEglContext() && createEglSurface() && captureScreenshotTextureAndSetViewport();
        }
        return false;
    }

    public void dismiss() {
        destroyScreenshotTexture();
        destroyEglSurface();
        destroySurface();
        this.mPrepared = false;
    }

    private void drawVStretch(float stretch) {
        float ar = scurve(stretch, 7.5f);
        float ag = scurve(stretch, 8.0f);
        float ab = scurve(stretch, 8.5f);
        GLES10.glBlendFunc(1, 1);
        GLES10.glEnable(3042);
        GLES10.glVertexPointer(2, 5126, 0, this.mVertexBuffer);
        GLES10.glEnableClientState(32884);
        GLES10.glDisable(3553);
        GLES10.glEnable(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
        GLES10.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, this.mTexNames[0]);
        GLES10.glTexEnvx(8960, 8704, this.mMode == 0 ? 8448 : 7681);
        GLES10.glTexParameterx(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 10240, 9729);
        GLES10.glTexParameterx(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 10241, 9729);
        GLES10.glTexParameterx(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 10242, 33071);
        GLES10.glTexParameterx(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 10243, 33071);
        GLES10.glEnable(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
        GLES10.glTexCoordPointer(2, 5126, 0, this.mTexCoordBuffer);
        GLES10.glEnableClientState(32888);
        setVStretchQuad(this.mVertexBuffer, this.mDisplayWidth, this.mDisplayHeight, ar);
        GLES10.glColorMask(true, false, false, true);
        GLES10.glDrawArrays(6, 0, 4);
        setVStretchQuad(this.mVertexBuffer, this.mDisplayWidth, this.mDisplayHeight, ag);
        GLES10.glColorMask(false, true, false, true);
        GLES10.glDrawArrays(6, 0, 4);
        setVStretchQuad(this.mVertexBuffer, this.mDisplayWidth, this.mDisplayHeight, ab);
        GLES10.glColorMask(false, false, true, true);
        GLES10.glDrawArrays(6, 0, 4);
        GLES10.glDisable(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
        GLES10.glDisableClientState(32888);
        GLES10.glColorMask(true, true, true, true);
        if (this.mMode == 1) {
            GLES10.glColor4f(ag, ag, ag, 1.0f);
            GLES10.glDrawArrays(6, 0, 4);
        }
        GLES10.glDisableClientState(32884);
        GLES10.glDisable(3042);
    }

    private void drawHStretch(float stretch) {
        float ag = scurve(stretch, 8.0f);
        if (stretch < 1.0f) {
            GLES10.glVertexPointer(2, 5126, 0, this.mVertexBuffer);
            GLES10.glEnableClientState(32884);
            setHStretchQuad(this.mVertexBuffer, this.mDisplayWidth, this.mDisplayHeight, ag);
            GLES10.glColor4f(1.0f - (ag * 0.75f), 1.0f - (ag * 0.75f), 1.0f - (ag * 0.75f), 1.0f);
            GLES10.glDrawArrays(6, 0, 4);
            GLES10.glDisableClientState(32884);
        }
    }

    private static void setVStretchQuad(FloatBuffer vtx, float dw, float dh, float a) {
        float w = dw + (dw * a);
        float h = dh - (dh * a);
        float x = (dw - w) * 0.5f;
        float y = (dh - h) * 0.5f;
        setQuad(vtx, x, y, w, h);
    }

    private static void setHStretchQuad(FloatBuffer vtx, float dw, float dh, float a) {
        float w = 2.0f * dw * (1.0f - a);
        float x = (dw - w) * 0.5f;
        float y = (dh - 1.0f) * 0.5f;
        setQuad(vtx, x, y, w, 1.0f);
    }

    private static void setQuad(FloatBuffer vtx, float x, float y, float w, float h) {
        vtx.put(0, x);
        vtx.put(1, y);
        vtx.put(2, x);
        vtx.put(3, y + h);
        vtx.put(4, x + w);
        vtx.put(5, y + h);
        vtx.put(6, x + w);
        vtx.put(7, y);
    }

    private boolean createEglContext() {
        if (this.mEglDisplay == null) {
            this.mEglDisplay = EGL14.eglGetDisplay(0);
            if (this.mEglDisplay == EGL14.EGL_NO_DISPLAY) {
                logEglError("eglGetDisplay");
                return false;
            }
            int[] version = new int[2];
            if (!EGL14.eglInitialize(this.mEglDisplay, version, 0, version, 1)) {
                this.mEglDisplay = null;
                logEglError("eglInitialize");
                return false;
            }
        }
        if (this.mEglConfig == null) {
            int[] eglConfigAttribList = {12324, 8, 12323, 8, 12322, 8, 12321, 8, 12344};
            int[] numEglConfigs = new int[1];
            EGLConfig[] eglConfigs = new EGLConfig[1];
            if (!EGL14.eglChooseConfig(this.mEglDisplay, eglConfigAttribList, 0, eglConfigs, 0, eglConfigs.length, numEglConfigs, 0)) {
                logEglError("eglChooseConfig");
                return false;
            }
            this.mEglConfig = eglConfigs[0];
        }
        if (this.mEglContext == null) {
            int[] eglContextAttribList = {12344};
            this.mEglContext = EGL14.eglCreateContext(this.mEglDisplay, this.mEglConfig, EGL14.EGL_NO_CONTEXT, eglContextAttribList, 0);
            if (this.mEglContext == null) {
                logEglError("eglCreateContext");
                return false;
            }
            return true;
        }
        return true;
    }

    private boolean createEglSurface() {
        if (this.mEglSurface == null) {
            int[] eglSurfaceAttribList = {12344};
            this.mEglSurface = EGL14.eglCreateWindowSurface(this.mEglDisplay, this.mEglConfig, this.mSurface, eglSurfaceAttribList, 0);
            if (this.mEglSurface == null) {
                logEglError("eglCreateWindowSurface");
                return false;
            }
            return true;
        }
        return true;
    }

    private void destroyEglSurface() {
        if (this.mEglSurface != null) {
            if (!EGL14.eglDestroySurface(this.mEglDisplay, this.mEglSurface)) {
                logEglError("eglDestroySurface");
            }
            this.mEglSurface = null;
        }
    }

    private boolean attachEglContext() {
        if (this.mEglSurface == null) {
            return false;
        }
        if (!EGL14.eglMakeCurrent(this.mEglDisplay, this.mEglSurface, this.mEglSurface, this.mEglContext)) {
            logEglError("eglMakeCurrent");
            return false;
        }
        return true;
    }

    private void detachEglContext() {
        if (this.mEglDisplay != null) {
            EGL14.eglMakeCurrent(this.mEglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
        }
    }

    private static float scurve(float value, float s) {
        float x = value - 0.5f;
        float y = sigmoid(x, s) - 0.5f;
        float v = sigmoid(0.5f, s) - 0.5f;
        return ((y / v) * 0.5f) + 0.5f;
    }

    private static float sigmoid(float x, float s) {
        return 1.0f / (1.0f + FloatMath.exp((-x) * s));
    }

    private static FloatBuffer createNativeFloatBuffer(int size) {
        ByteBuffer bb = ByteBuffer.allocateDirect(size * 4);
        bb.order(ByteOrder.nativeOrder());
        return bb.asFloatBuffer();
    }

    private static void logEglError(String func) {
        Slog.e(TAG, func + " failed: error " + EGL14.eglGetError(), new Throwable());
    }

    private static boolean checkGlErrors(String func) {
        return checkGlErrors(func, true);
    }

    private static boolean checkGlErrors(String func, boolean log) {
        boolean z = false;
        while (true) {
            boolean hadError = z;
            int error = GLES10.glGetError();
            if (error != 0) {
                if (log) {
                    Slog.e(TAG, func + " failed: error " + error, new Throwable());
                }
                z = true;
            } else {
                return hadError;
            }
        }
    }

    public void dump(PrintWriter pw) {
        pw.println();
        pw.println("Electron Beam State:");
        pw.println("  mPrepared=" + this.mPrepared);
        pw.println("  mMode=" + this.mMode);
        pw.println("  mDisplayLayerStack=" + this.mDisplayLayerStack);
        pw.println("  mDisplayWidth=" + this.mDisplayWidth);
        pw.println("  mDisplayHeight=" + this.mDisplayHeight);
        pw.println("  mSurfaceVisible=" + this.mSurfaceVisible);
        pw.println("  mSurfaceAlpha=" + this.mSurfaceAlpha);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ElectronBeam$NaturalSurfaceLayout.class */
    public static final class NaturalSurfaceLayout implements DisplayTransactionListener {
        private final DisplayManagerService mDisplayManager;
        private SurfaceControl mSurfaceControl;

        public NaturalSurfaceLayout(DisplayManagerService displayManager, SurfaceControl surfaceControl) {
            this.mDisplayManager = displayManager;
            this.mSurfaceControl = surfaceControl;
            this.mDisplayManager.registerDisplayTransactionListener(this);
        }

        public void dispose() {
            synchronized (this) {
                this.mSurfaceControl = null;
            }
            this.mDisplayManager.unregisterDisplayTransactionListener(this);
        }

        @Override // com.android.server.display.DisplayTransactionListener
        public void onDisplayTransaction() {
            synchronized (this) {
                if (this.mSurfaceControl == null) {
                    return;
                }
                DisplayInfo displayInfo = this.mDisplayManager.getDisplayInfo(0);
                switch (displayInfo.rotation) {
                    case 0:
                        this.mSurfaceControl.setPosition(0.0f, 0.0f);
                        this.mSurfaceControl.setMatrix(1.0f, 0.0f, 0.0f, 1.0f);
                        break;
                    case 1:
                        this.mSurfaceControl.setPosition(0.0f, displayInfo.logicalHeight);
                        this.mSurfaceControl.setMatrix(0.0f, -1.0f, 1.0f, 0.0f);
                        break;
                    case 2:
                        this.mSurfaceControl.setPosition(displayInfo.logicalWidth, displayInfo.logicalHeight);
                        this.mSurfaceControl.setMatrix(-1.0f, 0.0f, 0.0f, -1.0f);
                        break;
                    case 3:
                        this.mSurfaceControl.setPosition(displayInfo.logicalWidth, 0.0f);
                        this.mSurfaceControl.setMatrix(0.0f, 1.0f, -1.0f, 0.0f);
                        break;
                }
            }
        }
    }
}