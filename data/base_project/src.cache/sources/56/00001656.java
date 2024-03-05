package android.view.animation;

import android.graphics.Matrix;
import java.io.PrintWriter;

/* loaded from: Transformation.class */
public class Transformation {
    public static final int TYPE_IDENTITY = 0;
    public static final int TYPE_ALPHA = 1;
    public static final int TYPE_MATRIX = 2;
    public static final int TYPE_BOTH = 3;
    protected Matrix mMatrix;
    protected float mAlpha;
    protected int mTransformationType;

    public Transformation() {
        clear();
    }

    public void clear() {
        if (this.mMatrix == null) {
            this.mMatrix = new Matrix();
        } else {
            this.mMatrix.reset();
        }
        this.mAlpha = 1.0f;
        this.mTransformationType = 3;
    }

    public int getTransformationType() {
        return this.mTransformationType;
    }

    public void setTransformationType(int transformationType) {
        this.mTransformationType = transformationType;
    }

    public void set(Transformation t) {
        this.mAlpha = t.getAlpha();
        this.mMatrix.set(t.getMatrix());
        this.mTransformationType = t.getTransformationType();
    }

    public void compose(Transformation t) {
        this.mAlpha *= t.getAlpha();
        this.mMatrix.preConcat(t.getMatrix());
    }

    public void postCompose(Transformation t) {
        this.mAlpha *= t.getAlpha();
        this.mMatrix.postConcat(t.getMatrix());
    }

    public Matrix getMatrix() {
        return this.mMatrix;
    }

    public void setAlpha(float alpha) {
        this.mAlpha = alpha;
    }

    public float getAlpha() {
        return this.mAlpha;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(64);
        sb.append("Transformation");
        toShortString(sb);
        return sb.toString();
    }

    public String toShortString() {
        StringBuilder sb = new StringBuilder(64);
        toShortString(sb);
        return sb.toString();
    }

    public void toShortString(StringBuilder sb) {
        sb.append("{alpha=");
        sb.append(this.mAlpha);
        sb.append(" matrix=");
        this.mMatrix.toShortString(sb);
        sb.append('}');
    }

    public void printShortString(PrintWriter pw) {
        pw.print("{alpha=");
        pw.print(this.mAlpha);
        pw.print(" matrix=");
        this.mMatrix.printShortString(pw);
        pw.print('}');
    }
}