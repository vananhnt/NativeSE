package android.graphics;

import android.graphics.PorterDuff;

/* loaded from: ComposeShader.class */
public class ComposeShader extends Shader {
    private static final int TYPE_XFERMODE = 1;
    private static final int TYPE_PORTERDUFFMODE = 2;
    private int mType = 1;
    private Xfermode mXferMode;
    private PorterDuff.Mode mPorterDuffMode;
    private final Shader mShaderA;
    private final Shader mShaderB;

    private static native int nativeCreate1(int i, int i2, int i3);

    private static native int nativeCreate2(int i, int i2, int i3);

    private static native int nativePostCreate1(int i, int i2, int i3, int i4);

    private static native int nativePostCreate2(int i, int i2, int i3, int i4);

    public ComposeShader(Shader shaderA, Shader shaderB, Xfermode mode) {
        this.mShaderA = shaderA;
        this.mShaderB = shaderB;
        this.mXferMode = mode;
        this.native_instance = nativeCreate1(shaderA.native_instance, shaderB.native_instance, mode != null ? mode.native_instance : 0);
        if (mode instanceof PorterDuffXfermode) {
            PorterDuff.Mode pdMode = ((PorterDuffXfermode) mode).mode;
            this.native_shader = nativePostCreate2(this.native_instance, shaderA.native_shader, shaderB.native_shader, pdMode != null ? pdMode.nativeInt : 0);
            return;
        }
        this.native_shader = nativePostCreate1(this.native_instance, shaderA.native_shader, shaderB.native_shader, mode != null ? mode.native_instance : 0);
    }

    public ComposeShader(Shader shaderA, Shader shaderB, PorterDuff.Mode mode) {
        this.mShaderA = shaderA;
        this.mShaderB = shaderB;
        this.mPorterDuffMode = mode;
        this.native_instance = nativeCreate2(shaderA.native_instance, shaderB.native_instance, mode.nativeInt);
        this.native_shader = nativePostCreate2(this.native_instance, shaderA.native_shader, shaderB.native_shader, mode.nativeInt);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.graphics.Shader
    public Shader copy() {
        ComposeShader copy;
        switch (this.mType) {
            case 1:
                copy = new ComposeShader(this.mShaderA.copy(), this.mShaderB.copy(), this.mXferMode);
                break;
            case 2:
                copy = new ComposeShader(this.mShaderA.copy(), this.mShaderB.copy(), this.mPorterDuffMode);
                break;
            default:
                throw new IllegalArgumentException("ComposeShader should be created with either Xfermode or PorterDuffMode");
        }
        copyLocalMatrix(copy);
        return copy;
    }
}