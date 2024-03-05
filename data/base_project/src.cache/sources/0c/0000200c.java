package com.android.server.wm;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.view.Surface;
import android.view.SurfaceControl;
import android.view.SurfaceSession;
import gov.nist.core.Separators;
import java.io.PrintWriter;

/*  JADX ERROR: NullPointerException in pass: ExtractFieldInit
    java.lang.NullPointerException
    	at jadx.core.utils.BlockUtils.isAllBlocksEmpty(BlockUtils.java:977)
    	at jadx.core.dex.visitors.ExtractFieldInit.getConstructorsList(ExtractFieldInit.java:379)
    	at jadx.core.dex.visitors.ExtractFieldInit.moveCommonFieldsInit(ExtractFieldInit.java:128)
    	at jadx.core.dex.visitors.ExtractFieldInit.visit(ExtractFieldInit.java:51)
    */
/* loaded from: BlackFrame.class */
public class BlackFrame {
    final Rect mOuterRect;
    final Rect mInnerRect;
    final Matrix mTmpMatrix;
    final float[] mTmpFloats;
    final BlackSurface[] mBlackSurfaces;
    final boolean mForceDefaultOrientation;

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.BlackFrame.<init>(android.view.SurfaceSession, android.graphics.Rect, android.graphics.Rect, int, int, boolean):void, file: BlackFrame.class
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
    public BlackFrame(android.view.SurfaceSession r1, android.graphics.Rect r2, android.graphics.Rect r3, int r4, int r5, boolean r6) throws android.view.Surface.OutOfResourcesException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.BlackFrame.<init>(android.view.SurfaceSession, android.graphics.Rect, android.graphics.Rect, int, int, boolean):void, file: BlackFrame.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.BlackFrame.<init>(android.view.SurfaceSession, android.graphics.Rect, android.graphics.Rect, int, int, boolean):void");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: BlackFrame$BlackSurface.class */
    public class BlackSurface {
        final int left;
        final int top;
        final int layer;
        final SurfaceControl surface;

        BlackSurface(SurfaceSession session, int layer, int l, int t, int r, int b, int layerStack) throws Surface.OutOfResourcesException {
            this.left = l;
            this.top = t;
            this.layer = layer;
            int w = r - l;
            int h = b - t;
            this.surface = new SurfaceControl(session, "BlackSurface", w, h, -1, 131076);
            this.surface.setAlpha(1.0f);
            this.surface.setLayerStack(layerStack);
            this.surface.setLayer(layer);
            this.surface.show();
        }

        void setAlpha(float alpha) {
            this.surface.setAlpha(alpha);
        }

        void setMatrix(Matrix matrix) {
            BlackFrame.this.mTmpMatrix.setTranslate(this.left, this.top);
            BlackFrame.this.mTmpMatrix.postConcat(matrix);
            BlackFrame.this.mTmpMatrix.getValues(BlackFrame.this.mTmpFloats);
            this.surface.setPosition(BlackFrame.this.mTmpFloats[2], BlackFrame.this.mTmpFloats[5]);
            this.surface.setMatrix(BlackFrame.this.mTmpFloats[0], BlackFrame.this.mTmpFloats[3], BlackFrame.this.mTmpFloats[1], BlackFrame.this.mTmpFloats[4]);
        }

        void clearMatrix() {
            this.surface.setMatrix(1.0f, 0.0f, 0.0f, 1.0f);
        }
    }

    public void printTo(String prefix, PrintWriter pw) {
        pw.print(prefix);
        pw.print("Outer: ");
        this.mOuterRect.printShortString(pw);
        pw.print(" / Inner: ");
        this.mInnerRect.printShortString(pw);
        pw.println();
        for (int i = 0; i < this.mBlackSurfaces.length; i++) {
            BlackSurface bs = this.mBlackSurfaces[i];
            pw.print(prefix);
            pw.print(Separators.POUND);
            pw.print(i);
            pw.print(": ");
            pw.print(bs.surface);
            pw.print(" left=");
            pw.print(bs.left);
            pw.print(" top=");
            pw.println(bs.top);
        }
    }

    public void kill() {
        if (this.mBlackSurfaces != null) {
            for (int i = 0; i < this.mBlackSurfaces.length; i++) {
                if (this.mBlackSurfaces[i] != null) {
                    this.mBlackSurfaces[i].surface.destroy();
                    this.mBlackSurfaces[i] = null;
                }
            }
        }
    }

    public void hide() {
        if (this.mBlackSurfaces != null) {
            for (int i = 0; i < this.mBlackSurfaces.length; i++) {
                if (this.mBlackSurfaces[i] != null) {
                    this.mBlackSurfaces[i].surface.hide();
                }
            }
        }
    }

    public void setAlpha(float alpha) {
        for (int i = 0; i < this.mBlackSurfaces.length; i++) {
            if (this.mBlackSurfaces[i] != null) {
                this.mBlackSurfaces[i].setAlpha(alpha);
            }
        }
    }

    public void setMatrix(Matrix matrix) {
        for (int i = 0; i < this.mBlackSurfaces.length; i++) {
            if (this.mBlackSurfaces[i] != null) {
                this.mBlackSurfaces[i].setMatrix(matrix);
            }
        }
    }

    public void clearMatrix() {
        for (int i = 0; i < this.mBlackSurfaces.length; i++) {
            if (this.mBlackSurfaces[i] != null) {
                this.mBlackSurfaces[i].clearMatrix();
            }
        }
    }
}