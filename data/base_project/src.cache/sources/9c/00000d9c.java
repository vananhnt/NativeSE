package android.renderscript;

import android.renderscript.Element;
import android.renderscript.Program;
import android.renderscript.Type;

/* loaded from: ProgramFragmentFixedFunction.class */
public class ProgramFragmentFixedFunction extends ProgramFragment {
    ProgramFragmentFixedFunction(int id, RenderScript rs) {
        super(id, rs);
    }

    /* loaded from: ProgramFragmentFixedFunction$InternalBuilder.class */
    static class InternalBuilder extends Program.BaseProgramBuilder {
        public InternalBuilder(RenderScript rs) {
            super(rs);
        }

        public ProgramFragmentFixedFunction create() {
            this.mRS.validate();
            int[] tmp = new int[(this.mInputCount + this.mOutputCount + this.mConstantCount + this.mTextureCount) * 2];
            String[] texNames = new String[this.mTextureCount];
            int idx = 0;
            for (int i = 0; i < this.mInputCount; i++) {
                int i2 = idx;
                int idx2 = idx + 1;
                tmp[i2] = Program.ProgramParam.INPUT.mID;
                idx = idx2 + 1;
                tmp[idx2] = this.mInputs[i].getID(this.mRS);
            }
            for (int i3 = 0; i3 < this.mOutputCount; i3++) {
                int i4 = idx;
                int idx3 = idx + 1;
                tmp[i4] = Program.ProgramParam.OUTPUT.mID;
                idx = idx3 + 1;
                tmp[idx3] = this.mOutputs[i3].getID(this.mRS);
            }
            for (int i5 = 0; i5 < this.mConstantCount; i5++) {
                int i6 = idx;
                int idx4 = idx + 1;
                tmp[i6] = Program.ProgramParam.CONSTANT.mID;
                idx = idx4 + 1;
                tmp[idx4] = this.mConstants[i5].getID(this.mRS);
            }
            for (int i7 = 0; i7 < this.mTextureCount; i7++) {
                int i8 = idx;
                int idx5 = idx + 1;
                tmp[i8] = Program.ProgramParam.TEXTURE_TYPE.mID;
                idx = idx5 + 1;
                tmp[idx5] = this.mTextureTypes[i7].mID;
                texNames[i7] = this.mTextureNames[i7];
            }
            int id = this.mRS.nProgramFragmentCreate(this.mShader, texNames, tmp);
            ProgramFragmentFixedFunction pf = new ProgramFragmentFixedFunction(id, this.mRS);
            initProgram(pf);
            return pf;
        }
    }

    /* loaded from: ProgramFragmentFixedFunction$Builder.class */
    public static class Builder {
        public static final int MAX_TEXTURE = 2;
        int mNumTextures;
        boolean mVaryingColorEnable;
        String mShader;
        RenderScript mRS;
        Slot[] mSlots = new Slot[2];
        boolean mPointSpriteEnable = false;

        /* loaded from: ProgramFragmentFixedFunction$Builder$EnvMode.class */
        public enum EnvMode {
            REPLACE(1),
            MODULATE(2),
            DECAL(3);
            
            int mID;

            EnvMode(int id) {
                this.mID = id;
            }
        }

        /* loaded from: ProgramFragmentFixedFunction$Builder$Format.class */
        public enum Format {
            ALPHA(1),
            LUMINANCE_ALPHA(2),
            RGB(3),
            RGBA(4);
            
            int mID;

            Format(int id) {
                this.mID = id;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: ProgramFragmentFixedFunction$Builder$Slot.class */
        public class Slot {
            EnvMode env;
            Format format;

            Slot(EnvMode _env, Format _fmt) {
                this.env = _env;
                this.format = _fmt;
            }
        }

        private void buildShaderString() {
            this.mShader = "//rs_shader_internal\n";
            this.mShader += "varying lowp vec4 varColor;\n";
            this.mShader += "varying vec2 varTex0;\n";
            this.mShader += "void main() {\n";
            if (this.mVaryingColorEnable) {
                this.mShader += "  lowp vec4 col = varColor;\n";
            } else {
                this.mShader += "  lowp vec4 col = UNI_Color;\n";
            }
            if (this.mNumTextures != 0) {
                if (this.mPointSpriteEnable) {
                    this.mShader += "  vec2 t0 = gl_PointCoord;\n";
                } else {
                    this.mShader += "  vec2 t0 = varTex0.xy;\n";
                }
            }
            for (int i = 0; i < this.mNumTextures; i++) {
                switch (this.mSlots[i].env) {
                    case REPLACE:
                        switch (this.mSlots[i].format) {
                            case ALPHA:
                                this.mShader += "  col.a = texture2D(UNI_Tex0, t0).a;\n";
                                continue;
                            case LUMINANCE_ALPHA:
                                this.mShader += "  col.rgba = texture2D(UNI_Tex0, t0).rgba;\n";
                                continue;
                            case RGB:
                                this.mShader += "  col.rgb = texture2D(UNI_Tex0, t0).rgb;\n";
                                continue;
                            case RGBA:
                                this.mShader += "  col.rgba = texture2D(UNI_Tex0, t0).rgba;\n";
                                continue;
                        }
                    case MODULATE:
                        switch (this.mSlots[i].format) {
                            case ALPHA:
                                this.mShader += "  col.a *= texture2D(UNI_Tex0, t0).a;\n";
                                continue;
                            case LUMINANCE_ALPHA:
                                this.mShader += "  col.rgba *= texture2D(UNI_Tex0, t0).rgba;\n";
                                continue;
                            case RGB:
                                this.mShader += "  col.rgb *= texture2D(UNI_Tex0, t0).rgb;\n";
                                continue;
                            case RGBA:
                                this.mShader += "  col.rgba *= texture2D(UNI_Tex0, t0).rgba;\n";
                                continue;
                        }
                    case DECAL:
                        this.mShader += "  col = texture2D(UNI_Tex0, t0);\n";
                        break;
                }
            }
            this.mShader += "  gl_FragColor = col;\n";
            this.mShader += "}\n";
        }

        public Builder(RenderScript rs) {
            this.mRS = rs;
        }

        public Builder setTexture(EnvMode env, Format fmt, int slot) throws IllegalArgumentException {
            if (slot < 0 || slot >= 2) {
                throw new IllegalArgumentException("MAX_TEXTURE exceeded.");
            }
            this.mSlots[slot] = new Slot(env, fmt);
            return this;
        }

        public Builder setPointSpriteTexCoordinateReplacement(boolean enable) {
            this.mPointSpriteEnable = enable;
            return this;
        }

        public Builder setVaryingColor(boolean enable) {
            this.mVaryingColorEnable = enable;
            return this;
        }

        public ProgramFragmentFixedFunction create() {
            InternalBuilder sb = new InternalBuilder(this.mRS);
            this.mNumTextures = 0;
            for (int i = 0; i < 2; i++) {
                if (this.mSlots[i] != null) {
                    this.mNumTextures++;
                }
            }
            buildShaderString();
            sb.setShader(this.mShader);
            Type constType = null;
            if (!this.mVaryingColorEnable) {
                Element.Builder b = new Element.Builder(this.mRS);
                b.add(Element.F32_4(this.mRS), "Color");
                Type.Builder typeBuilder = new Type.Builder(this.mRS, b.create());
                typeBuilder.setX(1);
                constType = typeBuilder.create();
                sb.addConstant(constType);
            }
            for (int i2 = 0; i2 < this.mNumTextures; i2++) {
                sb.addTexture(Program.TextureType.TEXTURE_2D);
            }
            ProgramFragmentFixedFunction pf = sb.create();
            pf.mTextureCount = 2;
            if (!this.mVaryingColorEnable) {
                Allocation constantData = Allocation.createTyped(this.mRS, constType);
                FieldPacker fp = new FieldPacker(16);
                Float4 f4 = new Float4(1.0f, 1.0f, 1.0f, 1.0f);
                fp.addF32(f4);
                constantData.setFromFieldPacker(0, fp);
                pf.bindConstants(constantData, 0);
            }
            return pf;
        }
    }
}