package com.android.server.wm;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.view.SurfaceControl;
import android.view.SurfaceSession;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import gov.nist.core.Separators;
import java.io.PrintWriter;

/*  JADX ERROR: NullPointerException in pass: ExtractFieldInit
    java.lang.NullPointerException
    	at jadx.core.utils.BlockUtils.isAllBlocksEmpty(BlockUtils.java:977)
    	at jadx.core.dex.visitors.ExtractFieldInit.getConstructorsList(ExtractFieldInit.java:379)
    	at jadx.core.dex.visitors.ExtractFieldInit.moveCommonFieldsInit(ExtractFieldInit.java:128)
    	at jadx.core.dex.visitors.ExtractFieldInit.visit(ExtractFieldInit.java:51)
    */
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: ScreenRotationAnimation.class */
public class ScreenRotationAnimation {
    static final String TAG = "ScreenRotationAnimation";
    static final boolean DEBUG_STATE = false;
    static final boolean DEBUG_TRANSFORMS = false;
    static final boolean TWO_PHASE_ANIMATION = false;
    static final boolean USE_CUSTOM_BLACK_FRAME = false;
    static final int FREEZE_LAYER = 2000000;
    final Context mContext;
    final DisplayContent mDisplayContent;
    SurfaceControl mSurfaceControl;
    BlackFrame mCustomBlackFrame;
    BlackFrame mExitingBlackFrame;
    BlackFrame mEnteringBlackFrame;
    int mWidth;
    int mHeight;
    int mOriginalRotation;
    int mOriginalWidth;
    int mOriginalHeight;
    int mCurRotation;
    Rect mOriginalDisplayRect;
    Rect mCurrentDisplayRect;
    Animation mStartExitAnimation;
    final Transformation mStartExitTransformation;
    Animation mStartEnterAnimation;
    final Transformation mStartEnterTransformation;
    Animation mStartFrameAnimation;
    final Transformation mStartFrameTransformation;
    Animation mFinishExitAnimation;
    final Transformation mFinishExitTransformation;
    Animation mFinishEnterAnimation;
    final Transformation mFinishEnterTransformation;
    Animation mFinishFrameAnimation;
    final Transformation mFinishFrameTransformation;
    Animation mRotateExitAnimation;
    final Transformation mRotateExitTransformation;
    Animation mRotateEnterAnimation;
    final Transformation mRotateEnterTransformation;
    Animation mRotateFrameAnimation;
    final Transformation mRotateFrameTransformation;
    Animation mLastRotateExitAnimation;
    final Transformation mLastRotateExitTransformation;
    Animation mLastRotateEnterAnimation;
    final Transformation mLastRotateEnterTransformation;
    Animation mLastRotateFrameAnimation;
    final Transformation mLastRotateFrameTransformation;
    final Transformation mExitTransformation;
    final Transformation mEnterTransformation;
    final Transformation mFrameTransformation;
    boolean mStarted;
    boolean mAnimRunning;
    boolean mFinishAnimReady;
    long mFinishAnimStartTime;
    boolean mForceDefaultOrientation;
    final Matrix mFrameInitialMatrix;
    final Matrix mSnapshotInitialMatrix;
    final Matrix mSnapshotFinalMatrix;
    final Matrix mExitFrameFinalMatrix;
    final Matrix mTmpMatrix;
    final float[] mTmpFloats;
    private boolean mMoreRotateEnter;
    private boolean mMoreRotateExit;
    private boolean mMoreRotateFrame;
    private boolean mMoreFinishEnter;
    private boolean mMoreFinishExit;
    private boolean mMoreFinishFrame;
    private boolean mMoreStartEnter;
    private boolean mMoreStartExit;
    private boolean mMoreStartFrame;
    long mHalfwayPoint;

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.ScreenRotationAnimation.<init>(android.content.Context, com.android.server.wm.DisplayContent, android.view.SurfaceSession, boolean, boolean):void, file: ScreenRotationAnimation.class
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
    public ScreenRotationAnimation(android.content.Context r1, com.android.server.wm.DisplayContent r2, android.view.SurfaceSession r3, boolean r4, boolean r5) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.ScreenRotationAnimation.<init>(android.content.Context, com.android.server.wm.DisplayContent, android.view.SurfaceSession, boolean, boolean):void, file: ScreenRotationAnimation.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ScreenRotationAnimation.<init>(android.content.Context, com.android.server.wm.DisplayContent, android.view.SurfaceSession, boolean, boolean):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.ScreenRotationAnimation.startAnimation(android.view.SurfaceSession, long, float, int, int, boolean, int, int):boolean, file: ScreenRotationAnimation.class
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
    private boolean startAnimation(android.view.SurfaceSession r1, long r2, float r4, int r5, int r6, boolean r7, int r8, int r9) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.ScreenRotationAnimation.startAnimation(android.view.SurfaceSession, long, float, int, int, boolean, int, int):boolean, file: ScreenRotationAnimation.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ScreenRotationAnimation.startAnimation(android.view.SurfaceSession, long, float, int, int, boolean, int, int):boolean");
    }

    public void printTo(String prefix, PrintWriter pw) {
        pw.print(prefix);
        pw.print("mSurface=");
        pw.print(this.mSurfaceControl);
        pw.print(" mWidth=");
        pw.print(this.mWidth);
        pw.print(" mHeight=");
        pw.println(this.mHeight);
        pw.print(prefix);
        pw.print("mExitingBlackFrame=");
        pw.println(this.mExitingBlackFrame);
        if (this.mExitingBlackFrame != null) {
            this.mExitingBlackFrame.printTo(prefix + "  ", pw);
        }
        pw.print(prefix);
        pw.print("mEnteringBlackFrame=");
        pw.println(this.mEnteringBlackFrame);
        if (this.mEnteringBlackFrame != null) {
            this.mEnteringBlackFrame.printTo(prefix + "  ", pw);
        }
        pw.print(prefix);
        pw.print("mCurRotation=");
        pw.print(this.mCurRotation);
        pw.print(" mOriginalRotation=");
        pw.println(this.mOriginalRotation);
        pw.print(prefix);
        pw.print("mOriginalWidth=");
        pw.print(this.mOriginalWidth);
        pw.print(" mOriginalHeight=");
        pw.println(this.mOriginalHeight);
        pw.print(prefix);
        pw.print("mStarted=");
        pw.print(this.mStarted);
        pw.print(" mAnimRunning=");
        pw.print(this.mAnimRunning);
        pw.print(" mFinishAnimReady=");
        pw.print(this.mFinishAnimReady);
        pw.print(" mFinishAnimStartTime=");
        pw.println(this.mFinishAnimStartTime);
        pw.print(prefix);
        pw.print("mStartExitAnimation=");
        pw.print(this.mStartExitAnimation);
        pw.print(Separators.SP);
        this.mStartExitTransformation.printShortString(pw);
        pw.println();
        pw.print(prefix);
        pw.print("mStartEnterAnimation=");
        pw.print(this.mStartEnterAnimation);
        pw.print(Separators.SP);
        this.mStartEnterTransformation.printShortString(pw);
        pw.println();
        pw.print(prefix);
        pw.print("mStartFrameAnimation=");
        pw.print(this.mStartFrameAnimation);
        pw.print(Separators.SP);
        this.mStartFrameTransformation.printShortString(pw);
        pw.println();
        pw.print(prefix);
        pw.print("mFinishExitAnimation=");
        pw.print(this.mFinishExitAnimation);
        pw.print(Separators.SP);
        this.mFinishExitTransformation.printShortString(pw);
        pw.println();
        pw.print(prefix);
        pw.print("mFinishEnterAnimation=");
        pw.print(this.mFinishEnterAnimation);
        pw.print(Separators.SP);
        this.mFinishEnterTransformation.printShortString(pw);
        pw.println();
        pw.print(prefix);
        pw.print("mFinishFrameAnimation=");
        pw.print(this.mFinishFrameAnimation);
        pw.print(Separators.SP);
        this.mFinishFrameTransformation.printShortString(pw);
        pw.println();
        pw.print(prefix);
        pw.print("mRotateExitAnimation=");
        pw.print(this.mRotateExitAnimation);
        pw.print(Separators.SP);
        this.mRotateExitTransformation.printShortString(pw);
        pw.println();
        pw.print(prefix);
        pw.print("mRotateEnterAnimation=");
        pw.print(this.mRotateEnterAnimation);
        pw.print(Separators.SP);
        this.mRotateEnterTransformation.printShortString(pw);
        pw.println();
        pw.print(prefix);
        pw.print("mRotateFrameAnimation=");
        pw.print(this.mRotateFrameAnimation);
        pw.print(Separators.SP);
        this.mRotateFrameTransformation.printShortString(pw);
        pw.println();
        pw.print(prefix);
        pw.print("mExitTransformation=");
        this.mExitTransformation.printShortString(pw);
        pw.println();
        pw.print(prefix);
        pw.print("mEnterTransformation=");
        this.mEnterTransformation.printShortString(pw);
        pw.println();
        pw.print(prefix);
        pw.print("mFrameTransformation=");
        this.mEnterTransformation.printShortString(pw);
        pw.println();
        pw.print(prefix);
        pw.print("mFrameInitialMatrix=");
        this.mFrameInitialMatrix.printShortString(pw);
        pw.println();
        pw.print(prefix);
        pw.print("mSnapshotInitialMatrix=");
        this.mSnapshotInitialMatrix.printShortString(pw);
        pw.print(" mSnapshotFinalMatrix=");
        this.mSnapshotFinalMatrix.printShortString(pw);
        pw.println();
        pw.print(prefix);
        pw.print("mExitFrameFinalMatrix=");
        this.mExitFrameFinalMatrix.printShortString(pw);
        pw.println();
        pw.print(prefix);
        pw.print("mForceDefaultOrientation=");
        pw.print(this.mForceDefaultOrientation);
        if (this.mForceDefaultOrientation) {
            pw.print(" mOriginalDisplayRect=");
            pw.print(this.mOriginalDisplayRect.toShortString());
            pw.print(" mCurrentDisplayRect=");
            pw.println(this.mCurrentDisplayRect.toShortString());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasScreenshot() {
        return this.mSurfaceControl != null;
    }

    static int deltaRotation(int oldRotation, int newRotation) {
        int delta = newRotation - oldRotation;
        if (delta < 0) {
            delta += 4;
        }
        return delta;
    }

    private void setSnapshotTransformInTransaction(Matrix matrix, float alpha) {
        if (this.mSurfaceControl != null) {
            matrix.getValues(this.mTmpFloats);
            float x = this.mTmpFloats[2];
            float y = this.mTmpFloats[5];
            if (this.mForceDefaultOrientation) {
                this.mDisplayContent.getLogicalDisplayRect(this.mCurrentDisplayRect);
                x -= this.mCurrentDisplayRect.left;
                y -= this.mCurrentDisplayRect.top;
            }
            this.mSurfaceControl.setPosition(x, y);
            this.mSurfaceControl.setMatrix(this.mTmpFloats[0], this.mTmpFloats[3], this.mTmpFloats[1], this.mTmpFloats[4]);
            this.mSurfaceControl.setAlpha(alpha);
        }
    }

    public static void createRotationMatrix(int rotation, int width, int height, Matrix outMatrix) {
        switch (rotation) {
            case 0:
                outMatrix.reset();
                return;
            case 1:
                outMatrix.setRotate(90.0f, 0.0f, 0.0f);
                outMatrix.postTranslate(height, 0.0f);
                return;
            case 2:
                outMatrix.setRotate(180.0f, 0.0f, 0.0f);
                outMatrix.postTranslate(width, height);
                return;
            case 3:
                outMatrix.setRotate(270.0f, 0.0f, 0.0f);
                outMatrix.postTranslate(0.0f, width);
                return;
            default:
                return;
        }
    }

    private void setRotationInTransaction(int rotation) {
        this.mCurRotation = rotation;
        int delta = deltaRotation(rotation, 0);
        createRotationMatrix(delta, this.mWidth, this.mHeight, this.mSnapshotInitialMatrix);
        setSnapshotTransformInTransaction(this.mSnapshotInitialMatrix, 1.0f);
    }

    public boolean setRotationInTransaction(int rotation, SurfaceSession session, long maxAnimationDuration, float animationScale, int finalWidth, int finalHeight) {
        setRotationInTransaction(rotation);
        return false;
    }

    public boolean dismiss(SurfaceSession session, long maxAnimationDuration, float animationScale, int finalWidth, int finalHeight, int exitAnim, int enterAnim) {
        if (this.mSurfaceControl == null) {
            return false;
        }
        if (!this.mStarted) {
            startAnimation(session, maxAnimationDuration, animationScale, finalWidth, finalHeight, true, exitAnim, enterAnim);
        }
        if (!this.mStarted) {
            return false;
        }
        this.mFinishAnimReady = true;
        return true;
    }

    public void kill() {
        if (this.mSurfaceControl != null) {
            this.mSurfaceControl.destroy();
            this.mSurfaceControl = null;
        }
        if (this.mCustomBlackFrame != null) {
            this.mCustomBlackFrame.kill();
            this.mCustomBlackFrame = null;
        }
        if (this.mExitingBlackFrame != null) {
            this.mExitingBlackFrame.kill();
            this.mExitingBlackFrame = null;
        }
        if (this.mEnteringBlackFrame != null) {
            this.mEnteringBlackFrame.kill();
            this.mEnteringBlackFrame = null;
        }
        if (this.mRotateExitAnimation != null) {
            this.mRotateExitAnimation.cancel();
            this.mRotateExitAnimation = null;
        }
        if (this.mRotateEnterAnimation != null) {
            this.mRotateEnterAnimation.cancel();
            this.mRotateEnterAnimation = null;
        }
    }

    public boolean isAnimating() {
        return hasAnimations();
    }

    public boolean isRotating() {
        return this.mCurRotation != this.mOriginalRotation;
    }

    private boolean hasAnimations() {
        return (this.mRotateEnterAnimation == null && this.mRotateExitAnimation == null) ? false : true;
    }

    private boolean stepAnimation(long now) {
        if (now > this.mHalfwayPoint) {
            this.mHalfwayPoint = Long.MAX_VALUE;
        }
        if (this.mFinishAnimReady && this.mFinishAnimStartTime < 0) {
            this.mFinishAnimStartTime = now;
        }
        long j = this.mFinishAnimReady ? now - this.mFinishAnimStartTime : 0L;
        this.mMoreRotateExit = false;
        if (this.mRotateExitAnimation != null) {
            this.mMoreRotateExit = this.mRotateExitAnimation.getTransformation(now, this.mRotateExitTransformation);
        }
        this.mMoreRotateEnter = false;
        if (this.mRotateEnterAnimation != null) {
            this.mMoreRotateEnter = this.mRotateEnterAnimation.getTransformation(now, this.mRotateEnterTransformation);
        }
        if (!this.mMoreRotateExit && this.mRotateExitAnimation != null) {
            this.mRotateExitAnimation.cancel();
            this.mRotateExitAnimation = null;
            this.mRotateExitTransformation.clear();
        }
        if (!this.mMoreRotateEnter && this.mRotateEnterAnimation != null) {
            this.mRotateEnterAnimation.cancel();
            this.mRotateEnterAnimation = null;
            this.mRotateEnterTransformation.clear();
        }
        this.mExitTransformation.set(this.mRotateExitTransformation);
        this.mEnterTransformation.set(this.mRotateEnterTransformation);
        boolean more = this.mMoreRotateEnter || this.mMoreRotateExit || !this.mFinishAnimReady;
        this.mSnapshotFinalMatrix.setConcat(this.mExitTransformation.getMatrix(), this.mSnapshotInitialMatrix);
        return more;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateSurfacesInTransaction() {
        if (!this.mStarted) {
            return;
        }
        if (this.mSurfaceControl != null && !this.mMoreStartExit && !this.mMoreFinishExit && !this.mMoreRotateExit) {
            this.mSurfaceControl.hide();
        }
        if (this.mCustomBlackFrame != null) {
            if (!this.mMoreStartFrame && !this.mMoreFinishFrame && !this.mMoreRotateFrame) {
                this.mCustomBlackFrame.hide();
            } else {
                this.mCustomBlackFrame.setMatrix(this.mFrameTransformation.getMatrix());
            }
        }
        if (this.mExitingBlackFrame != null) {
            if (!this.mMoreStartExit && !this.mMoreFinishExit && !this.mMoreRotateExit) {
                this.mExitingBlackFrame.hide();
            } else {
                this.mExitFrameFinalMatrix.setConcat(this.mExitTransformation.getMatrix(), this.mFrameInitialMatrix);
                this.mExitingBlackFrame.setMatrix(this.mExitFrameFinalMatrix);
                if (this.mForceDefaultOrientation) {
                    this.mExitingBlackFrame.setAlpha(this.mExitTransformation.getAlpha());
                }
            }
        }
        if (this.mEnteringBlackFrame != null) {
            if (!this.mMoreStartEnter && !this.mMoreFinishEnter && !this.mMoreRotateEnter) {
                this.mEnteringBlackFrame.hide();
            } else {
                this.mEnteringBlackFrame.setMatrix(this.mEnterTransformation.getMatrix());
            }
        }
        setSnapshotTransformInTransaction(this.mSnapshotFinalMatrix, this.mExitTransformation.getAlpha());
    }

    public boolean stepAnimationLocked(long now) {
        if (!hasAnimations()) {
            this.mFinishAnimReady = false;
            return false;
        }
        if (!this.mAnimRunning) {
            if (this.mRotateEnterAnimation != null) {
                this.mRotateEnterAnimation.setStartTime(now);
            }
            if (this.mRotateExitAnimation != null) {
                this.mRotateExitAnimation.setStartTime(now);
            }
            this.mAnimRunning = true;
            this.mHalfwayPoint = now + (this.mRotateEnterAnimation.getDuration() / 2);
        }
        return stepAnimation(now);
    }

    public Transformation getEnterTransformation() {
        return this.mEnterTransformation;
    }
}