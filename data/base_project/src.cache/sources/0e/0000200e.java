package com.android.server.wm;

import android.graphics.Rect;
import android.os.SystemClock;
import android.util.Slog;
import android.view.DisplayInfo;
import android.view.SurfaceControl;
import java.io.PrintWriter;

/*  JADX ERROR: NullPointerException in pass: ExtractFieldInit
    java.lang.NullPointerException
    	at jadx.core.utils.BlockUtils.isAllBlocksEmpty(BlockUtils.java:977)
    	at jadx.core.dex.visitors.ExtractFieldInit.getConstructorsList(ExtractFieldInit.java:379)
    	at jadx.core.dex.visitors.ExtractFieldInit.moveCommonFieldsInit(ExtractFieldInit.java:128)
    	at jadx.core.dex.visitors.ExtractFieldInit.visit(ExtractFieldInit.java:51)
    */
/* loaded from: DimLayer.class */
public class DimLayer {
    private static final String TAG = "DimLayer";
    private static final boolean DEBUG = false;
    final DisplayContent mDisplayContent;
    SurfaceControl mDimSurface;
    float mAlpha;
    int mLayer;
    Rect mBounds;
    Rect mLastBounds;
    private boolean mShowing;
    float mStartAlpha;
    float mTargetAlpha;
    long mStartTime;
    long mDuration;
    final TaskStack mStack;

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.DimLayer.<init>(com.android.server.wm.WindowManagerService, com.android.server.wm.TaskStack):void, file: DimLayer.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    DimLayer(com.android.server.wm.WindowManagerService r1, com.android.server.wm.TaskStack r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.DimLayer.<init>(com.android.server.wm.WindowManagerService, com.android.server.wm.TaskStack):void, file: DimLayer.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.DimLayer.<init>(com.android.server.wm.WindowManagerService, com.android.server.wm.TaskStack):void");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isDimming() {
        return this.mTargetAlpha != 0.0f;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isAnimating() {
        return this.mTargetAlpha != this.mAlpha;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public float getTargetAlpha() {
        return this.mTargetAlpha;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setLayer(int layer) {
        if (this.mLayer != layer) {
            this.mLayer = layer;
            this.mDimSurface.setLayer(layer);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getLayer() {
        return this.mLayer;
    }

    private void setAlpha(float alpha) {
        if (this.mAlpha != alpha) {
            try {
                this.mDimSurface.setAlpha(alpha);
                if (alpha == 0.0f && this.mShowing) {
                    this.mDimSurface.hide();
                    this.mShowing = false;
                } else if (alpha > 0.0f && !this.mShowing) {
                    this.mDimSurface.show();
                    this.mShowing = true;
                }
            } catch (RuntimeException e) {
                Slog.w(TAG, "Failure setting alpha immediately", e);
            }
            this.mAlpha = alpha;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setBounds(Rect bounds) {
        this.mBounds.set(bounds);
    }

    private boolean durationEndsEarlier(long duration) {
        return SystemClock.uptimeMillis() + duration < this.mStartTime + this.mDuration;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void show() {
        if (isAnimating()) {
            show(this.mLayer, this.mTargetAlpha, 0L);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void show(int layer, float alpha, long duration) {
        int dw;
        int dh;
        float xPos;
        float yPos;
        if (this.mDimSurface == null) {
            Slog.e(TAG, "show: no Surface");
            this.mAlpha = 0.0f;
            this.mTargetAlpha = 0.0f;
            return;
        }
        if (this.mStack.hasSibling()) {
            dw = this.mBounds.width();
            dh = this.mBounds.height();
            xPos = this.mBounds.left;
            yPos = this.mBounds.right;
        } else {
            DisplayInfo info = this.mDisplayContent.getDisplayInfo();
            dw = (int) (info.logicalWidth * 1.5d);
            dh = (int) (info.logicalHeight * 1.5d);
            xPos = ((-1) * dw) / 6;
            yPos = ((-1) * dh) / 6;
        }
        if (!this.mLastBounds.equals(this.mBounds) || this.mLayer != layer) {
            try {
                this.mDimSurface.setPosition(xPos, yPos);
                this.mDimSurface.setSize(dw, dh);
                this.mDimSurface.setLayer(layer);
            } catch (RuntimeException e) {
                Slog.w(TAG, "Failure setting size or layer", e);
            }
            this.mLastBounds.set(this.mBounds);
            this.mLayer = layer;
        }
        long curTime = SystemClock.uptimeMillis();
        boolean animating = isAnimating();
        if ((animating && (this.mTargetAlpha != alpha || durationEndsEarlier(duration))) || (!animating && this.mAlpha != alpha)) {
            if (duration <= 0) {
                setAlpha(alpha);
            } else {
                this.mStartAlpha = this.mAlpha;
                this.mStartTime = curTime;
                this.mDuration = duration;
            }
        }
        this.mTargetAlpha = alpha;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void hide() {
        if (this.mShowing) {
            hide(0L);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void hide(long duration) {
        if (this.mShowing) {
            if (this.mTargetAlpha != 0.0f || durationEndsEarlier(duration)) {
                show(this.mLayer, 0.0f, duration);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean stepAnimation() {
        if (this.mDimSurface == null) {
            Slog.e(TAG, "stepAnimation: null Surface");
            this.mAlpha = 0.0f;
            this.mTargetAlpha = 0.0f;
            return false;
        }
        if (isAnimating()) {
            long curTime = SystemClock.uptimeMillis();
            float alphaDelta = this.mTargetAlpha - this.mStartAlpha;
            float alpha = this.mStartAlpha + ((alphaDelta * ((float) (curTime - this.mStartTime))) / ((float) this.mDuration));
            if ((alphaDelta > 0.0f && alpha > this.mTargetAlpha) || (alphaDelta < 0.0f && alpha < this.mTargetAlpha)) {
                alpha = this.mTargetAlpha;
            }
            setAlpha(alpha);
        }
        return isAnimating();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void destroySurface() {
        if (this.mDimSurface != null) {
            this.mDimSurface.destroy();
            this.mDimSurface = null;
        }
    }

    public void printTo(String prefix, PrintWriter pw) {
        pw.print(prefix);
        pw.print("mDimSurface=");
        pw.print(this.mDimSurface);
        pw.print(" mLayer=");
        pw.print(this.mLayer);
        pw.print(" mAlpha=");
        pw.println(this.mAlpha);
        pw.print(prefix);
        pw.print("mLastBounds=");
        pw.print(this.mLastBounds.toShortString());
        pw.print(" mBounds=");
        pw.println(this.mBounds.toShortString());
        pw.print(prefix);
        pw.print("Last animation: ");
        pw.print(" mDuration=");
        pw.print(this.mDuration);
        pw.print(" mStartTime=");
        pw.print(this.mStartTime);
        pw.print(" curTime=");
        pw.println(SystemClock.uptimeMillis());
        pw.print(prefix);
        pw.print(" mStartAlpha=");
        pw.print(this.mStartAlpha);
        pw.print(" mTargetAlpha=");
        pw.println(this.mTargetAlpha);
    }
}