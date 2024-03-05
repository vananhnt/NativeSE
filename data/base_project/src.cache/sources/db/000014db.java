package android.view;

import android.graphics.Matrix;
import java.util.ArrayList;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: GLES20DisplayList.class */
public class GLES20DisplayList extends DisplayList {
    private ArrayList<DisplayList> mChildDisplayLists;
    private GLES20RecordingCanvas mCanvas;
    private boolean mValid;
    private final String mName;
    private DisplayListFinalizer mFinalizer;

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nDestroyDisplayList(int i);

    private static native int nGetDisplayListSize(int i);

    private static native void nSetDisplayListName(int i, String str);

    private static native void nReset(int i);

    private static native void nOffsetTopAndBottom(int i, float f);

    private static native void nOffsetLeftAndRight(int i, float f);

    private static native void nSetLeftTopRightBottom(int i, int i2, int i3, int i4, int i5);

    private static native void nSetBottom(int i, int i2);

    private static native void nSetRight(int i, int i2);

    private static native void nSetTop(int i, int i2);

    private static native void nSetLeft(int i, int i2);

    private static native void nSetCameraDistance(int i, float f);

    private static native void nSetPivotY(int i, float f);

    private static native void nSetPivotX(int i, float f);

    private static native void nSetCaching(int i, boolean z);

    private static native void nSetClipToBounds(int i, boolean z);

    private static native void nSetAlpha(int i, float f);

    private static native void nSetHasOverlappingRendering(int i, boolean z);

    private static native void nSetTranslationX(int i, float f);

    private static native void nSetTranslationY(int i, float f);

    private static native void nSetRotation(int i, float f);

    private static native void nSetRotationX(int i, float f);

    private static native void nSetRotationY(int i, float f);

    private static native void nSetScaleX(int i, float f);

    private static native void nSetScaleY(int i, float f);

    private static native void nSetTransformationInfo(int i, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8);

    private static native void nSetStaticMatrix(int i, int i2);

    private static native void nSetAnimationMatrix(int i, int i2);

    private static native boolean nHasOverlappingRendering(int i);

    private static native void nGetMatrix(int i, int i2);

    private static native float nGetAlpha(int i);

    private static native float nGetLeft(int i);

    private static native float nGetTop(int i);

    private static native float nGetRight(int i);

    private static native float nGetBottom(int i);

    private static native float nGetCameraDistance(int i);

    private static native float nGetScaleX(int i);

    private static native float nGetScaleY(int i);

    private static native float nGetTranslationX(int i);

    private static native float nGetTranslationY(int i);

    private static native float nGetRotation(int i);

    private static native float nGetRotationX(int i);

    private static native float nGetRotationY(int i);

    private static native float nGetPivotX(int i);

    private static native float nGetPivotY(int i);

    /* JADX INFO: Access modifiers changed from: package-private */
    public GLES20DisplayList(String name) {
        this.mName = name;
    }

    boolean hasNativeDisplayList() {
        return this.mValid && this.mFinalizer != null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getNativeDisplayList() {
        if (!this.mValid || this.mFinalizer == null) {
            throw new IllegalStateException("The display list is not valid.");
        }
        return this.mFinalizer.mNativeDisplayList;
    }

    @Override // android.view.DisplayList
    public HardwareCanvas start(int width, int height) {
        if (this.mCanvas != null) {
            throw new IllegalStateException("Recording has already started");
        }
        this.mValid = false;
        this.mCanvas = GLES20RecordingCanvas.obtain(this);
        this.mCanvas.start();
        this.mCanvas.setViewport(width, height);
        this.mCanvas.onPreDraw(null);
        return this.mCanvas;
    }

    @Override // android.view.DisplayList
    public void clear() {
        clearDirty();
        if (this.mCanvas != null) {
            this.mCanvas.recycle();
            this.mCanvas = null;
        }
        this.mValid = false;
        clearReferences();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void clearReferences() {
        if (this.mChildDisplayLists != null) {
            this.mChildDisplayLists.clear();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ArrayList<DisplayList> getChildDisplayLists() {
        if (this.mChildDisplayLists == null) {
            this.mChildDisplayLists = new ArrayList<>();
        }
        return this.mChildDisplayLists;
    }

    @Override // android.view.DisplayList
    public void reset() {
        if (hasNativeDisplayList()) {
            nReset(this.mFinalizer.mNativeDisplayList);
        }
        clear();
    }

    @Override // android.view.DisplayList
    public boolean isValid() {
        return this.mValid;
    }

    @Override // android.view.DisplayList
    public void end() {
        if (this.mCanvas != null) {
            this.mCanvas.onPostDraw();
            if (this.mFinalizer != null) {
                this.mCanvas.end(this.mFinalizer.mNativeDisplayList);
            } else {
                this.mFinalizer = new DisplayListFinalizer(this.mCanvas.end(0));
                nSetDisplayListName(this.mFinalizer.mNativeDisplayList, this.mName);
            }
            this.mCanvas.recycle();
            this.mCanvas = null;
            this.mValid = true;
        }
    }

    @Override // android.view.DisplayList
    public int getSize() {
        if (this.mFinalizer == null) {
            return 0;
        }
        return nGetDisplayListSize(this.mFinalizer.mNativeDisplayList);
    }

    @Override // android.view.DisplayList
    public void setCaching(boolean caching) {
        if (hasNativeDisplayList()) {
            nSetCaching(this.mFinalizer.mNativeDisplayList, caching);
        }
    }

    @Override // android.view.DisplayList
    public void setClipToBounds(boolean clipToBounds) {
        if (hasNativeDisplayList()) {
            nSetClipToBounds(this.mFinalizer.mNativeDisplayList, clipToBounds);
        }
    }

    @Override // android.view.DisplayList
    public void setMatrix(Matrix matrix) {
        if (hasNativeDisplayList()) {
            nSetStaticMatrix(this.mFinalizer.mNativeDisplayList, matrix.native_instance);
        }
    }

    @Override // android.view.DisplayList
    public Matrix getMatrix(Matrix matrix) {
        if (hasNativeDisplayList()) {
            nGetMatrix(this.mFinalizer.mNativeDisplayList, matrix.native_instance);
        }
        return matrix;
    }

    @Override // android.view.DisplayList
    public void setAnimationMatrix(Matrix matrix) {
        if (hasNativeDisplayList()) {
            nSetAnimationMatrix(this.mFinalizer.mNativeDisplayList, matrix != null ? matrix.native_instance : 0);
        }
    }

    @Override // android.view.DisplayList
    public void setAlpha(float alpha) {
        if (hasNativeDisplayList()) {
            nSetAlpha(this.mFinalizer.mNativeDisplayList, alpha);
        }
    }

    @Override // android.view.DisplayList
    public float getAlpha() {
        if (hasNativeDisplayList()) {
            return nGetAlpha(this.mFinalizer.mNativeDisplayList);
        }
        return 1.0f;
    }

    @Override // android.view.DisplayList
    public void setHasOverlappingRendering(boolean hasOverlappingRendering) {
        if (hasNativeDisplayList()) {
            nSetHasOverlappingRendering(this.mFinalizer.mNativeDisplayList, hasOverlappingRendering);
        }
    }

    @Override // android.view.DisplayList
    public boolean hasOverlappingRendering() {
        if (hasNativeDisplayList()) {
            return nHasOverlappingRendering(this.mFinalizer.mNativeDisplayList);
        }
        return true;
    }

    @Override // android.view.DisplayList
    public void setTranslationX(float translationX) {
        if (hasNativeDisplayList()) {
            nSetTranslationX(this.mFinalizer.mNativeDisplayList, translationX);
        }
    }

    @Override // android.view.DisplayList
    public float getTranslationX() {
        if (hasNativeDisplayList()) {
            return nGetTranslationX(this.mFinalizer.mNativeDisplayList);
        }
        return 0.0f;
    }

    @Override // android.view.DisplayList
    public void setTranslationY(float translationY) {
        if (hasNativeDisplayList()) {
            nSetTranslationY(this.mFinalizer.mNativeDisplayList, translationY);
        }
    }

    @Override // android.view.DisplayList
    public float getTranslationY() {
        if (hasNativeDisplayList()) {
            return nGetTranslationY(this.mFinalizer.mNativeDisplayList);
        }
        return 0.0f;
    }

    @Override // android.view.DisplayList
    public void setRotation(float rotation) {
        if (hasNativeDisplayList()) {
            nSetRotation(this.mFinalizer.mNativeDisplayList, rotation);
        }
    }

    @Override // android.view.DisplayList
    public float getRotation() {
        if (hasNativeDisplayList()) {
            return nGetRotation(this.mFinalizer.mNativeDisplayList);
        }
        return 0.0f;
    }

    @Override // android.view.DisplayList
    public void setRotationX(float rotationX) {
        if (hasNativeDisplayList()) {
            nSetRotationX(this.mFinalizer.mNativeDisplayList, rotationX);
        }
    }

    @Override // android.view.DisplayList
    public float getRotationX() {
        if (hasNativeDisplayList()) {
            return nGetRotationX(this.mFinalizer.mNativeDisplayList);
        }
        return 0.0f;
    }

    @Override // android.view.DisplayList
    public void setRotationY(float rotationY) {
        if (hasNativeDisplayList()) {
            nSetRotationY(this.mFinalizer.mNativeDisplayList, rotationY);
        }
    }

    @Override // android.view.DisplayList
    public float getRotationY() {
        if (hasNativeDisplayList()) {
            return nGetRotationY(this.mFinalizer.mNativeDisplayList);
        }
        return 0.0f;
    }

    @Override // android.view.DisplayList
    public void setScaleX(float scaleX) {
        if (hasNativeDisplayList()) {
            nSetScaleX(this.mFinalizer.mNativeDisplayList, scaleX);
        }
    }

    @Override // android.view.DisplayList
    public float getScaleX() {
        if (hasNativeDisplayList()) {
            return nGetScaleX(this.mFinalizer.mNativeDisplayList);
        }
        return 1.0f;
    }

    @Override // android.view.DisplayList
    public void setScaleY(float scaleY) {
        if (hasNativeDisplayList()) {
            nSetScaleY(this.mFinalizer.mNativeDisplayList, scaleY);
        }
    }

    @Override // android.view.DisplayList
    public float getScaleY() {
        if (hasNativeDisplayList()) {
            return nGetScaleY(this.mFinalizer.mNativeDisplayList);
        }
        return 1.0f;
    }

    @Override // android.view.DisplayList
    public void setTransformationInfo(float alpha, float translationX, float translationY, float rotation, float rotationX, float rotationY, float scaleX, float scaleY) {
        if (hasNativeDisplayList()) {
            nSetTransformationInfo(this.mFinalizer.mNativeDisplayList, alpha, translationX, translationY, rotation, rotationX, rotationY, scaleX, scaleY);
        }
    }

    @Override // android.view.DisplayList
    public void setPivotX(float pivotX) {
        if (hasNativeDisplayList()) {
            nSetPivotX(this.mFinalizer.mNativeDisplayList, pivotX);
        }
    }

    @Override // android.view.DisplayList
    public float getPivotX() {
        if (hasNativeDisplayList()) {
            return nGetPivotX(this.mFinalizer.mNativeDisplayList);
        }
        return 0.0f;
    }

    @Override // android.view.DisplayList
    public void setPivotY(float pivotY) {
        if (hasNativeDisplayList()) {
            nSetPivotY(this.mFinalizer.mNativeDisplayList, pivotY);
        }
    }

    @Override // android.view.DisplayList
    public float getPivotY() {
        if (hasNativeDisplayList()) {
            return nGetPivotY(this.mFinalizer.mNativeDisplayList);
        }
        return 0.0f;
    }

    @Override // android.view.DisplayList
    public void setCameraDistance(float distance) {
        if (hasNativeDisplayList()) {
            nSetCameraDistance(this.mFinalizer.mNativeDisplayList, distance);
        }
    }

    @Override // android.view.DisplayList
    public float getCameraDistance() {
        if (hasNativeDisplayList()) {
            return nGetCameraDistance(this.mFinalizer.mNativeDisplayList);
        }
        return 0.0f;
    }

    @Override // android.view.DisplayList
    public void setLeft(int left) {
        if (hasNativeDisplayList()) {
            nSetLeft(this.mFinalizer.mNativeDisplayList, left);
        }
    }

    @Override // android.view.DisplayList
    public float getLeft() {
        if (hasNativeDisplayList()) {
            return nGetLeft(this.mFinalizer.mNativeDisplayList);
        }
        return 0.0f;
    }

    @Override // android.view.DisplayList
    public void setTop(int top) {
        if (hasNativeDisplayList()) {
            nSetTop(this.mFinalizer.mNativeDisplayList, top);
        }
    }

    @Override // android.view.DisplayList
    public float getTop() {
        if (hasNativeDisplayList()) {
            return nGetTop(this.mFinalizer.mNativeDisplayList);
        }
        return 0.0f;
    }

    @Override // android.view.DisplayList
    public void setRight(int right) {
        if (hasNativeDisplayList()) {
            nSetRight(this.mFinalizer.mNativeDisplayList, right);
        }
    }

    @Override // android.view.DisplayList
    public float getRight() {
        if (hasNativeDisplayList()) {
            return nGetRight(this.mFinalizer.mNativeDisplayList);
        }
        return 0.0f;
    }

    @Override // android.view.DisplayList
    public void setBottom(int bottom) {
        if (hasNativeDisplayList()) {
            nSetBottom(this.mFinalizer.mNativeDisplayList, bottom);
        }
    }

    @Override // android.view.DisplayList
    public float getBottom() {
        if (hasNativeDisplayList()) {
            return nGetBottom(this.mFinalizer.mNativeDisplayList);
        }
        return 0.0f;
    }

    @Override // android.view.DisplayList
    public void setLeftTopRightBottom(int left, int top, int right, int bottom) {
        if (hasNativeDisplayList()) {
            nSetLeftTopRightBottom(this.mFinalizer.mNativeDisplayList, left, top, right, bottom);
        }
    }

    @Override // android.view.DisplayList
    public void offsetLeftAndRight(float offset) {
        if (hasNativeDisplayList()) {
            nOffsetLeftAndRight(this.mFinalizer.mNativeDisplayList, offset);
        }
    }

    @Override // android.view.DisplayList
    public void offsetTopAndBottom(float offset) {
        if (hasNativeDisplayList()) {
            nOffsetTopAndBottom(this.mFinalizer.mNativeDisplayList, offset);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: GLES20DisplayList$DisplayListFinalizer.class */
    public static class DisplayListFinalizer {
        final int mNativeDisplayList;

        public DisplayListFinalizer(int nativeDisplayList) {
            this.mNativeDisplayList = nativeDisplayList;
        }

        protected void finalize() throws Throwable {
            try {
                GLES20DisplayList.nDestroyDisplayList(this.mNativeDisplayList);
                super.finalize();
            } catch (Throwable th) {
                super.finalize();
                throw th;
            }
        }
    }
}