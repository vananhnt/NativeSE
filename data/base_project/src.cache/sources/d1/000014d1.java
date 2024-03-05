package android.view;

import android.graphics.Matrix;

/* loaded from: DisplayList.class */
public abstract class DisplayList {
    private boolean mDirty;
    public static final int FLAG_CLIP_CHILDREN = 1;
    public static final int STATUS_DONE = 0;
    public static final int STATUS_DRAW = 1;
    public static final int STATUS_INVOKE = 2;
    public static final int STATUS_DREW = 4;

    public abstract HardwareCanvas start(int i, int i2);

    public abstract void end();

    public abstract void clear();

    public abstract void reset();

    public abstract boolean isValid();

    public abstract int getSize();

    public abstract void setCaching(boolean z);

    public abstract void setClipToBounds(boolean z);

    public abstract void setMatrix(Matrix matrix);

    public abstract Matrix getMatrix(Matrix matrix);

    public abstract void setAnimationMatrix(Matrix matrix);

    public abstract void setAlpha(float f);

    public abstract float getAlpha();

    public abstract void setHasOverlappingRendering(boolean z);

    public abstract boolean hasOverlappingRendering();

    public abstract void setTranslationX(float f);

    public abstract float getTranslationX();

    public abstract void setTranslationY(float f);

    public abstract float getTranslationY();

    public abstract void setRotation(float f);

    public abstract float getRotation();

    public abstract void setRotationX(float f);

    public abstract float getRotationX();

    public abstract void setRotationY(float f);

    public abstract float getRotationY();

    public abstract void setScaleX(float f);

    public abstract float getScaleX();

    public abstract void setScaleY(float f);

    public abstract float getScaleY();

    public abstract void setTransformationInfo(float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8);

    public abstract void setPivotX(float f);

    public abstract float getPivotX();

    public abstract void setPivotY(float f);

    public abstract float getPivotY();

    public abstract void setCameraDistance(float f);

    public abstract float getCameraDistance();

    public abstract void setLeft(int i);

    public abstract float getLeft();

    public abstract void setTop(int i);

    public abstract float getTop();

    public abstract void setRight(int i);

    public abstract float getRight();

    public abstract void setBottom(int i);

    public abstract float getBottom();

    public abstract void setLeftTopRightBottom(int i, int i2, int i3, int i4);

    public abstract void offsetLeftAndRight(float f);

    public abstract void offsetTopAndBottom(float f);

    public void markDirty() {
        this.mDirty = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void clearDirty() {
        this.mDirty = false;
    }

    public boolean isDirty() {
        return this.mDirty;
    }

    public Matrix getMatrix() {
        return getMatrix(new Matrix());
    }
}