package android.renderscript;

import android.provider.BrowserContract;
import android.provider.CalendarContract;
import android.renderscript.Element;
import android.renderscript.Program;
import android.renderscript.Type;

/* loaded from: ProgramVertexFixedFunction.class */
public class ProgramVertexFixedFunction extends ProgramVertex {
    ProgramVertexFixedFunction(int id, RenderScript rs) {
        super(id, rs);
    }

    public void bindConstants(Constants va) {
        this.mRS.validate();
        bindConstants(va.getAllocation(), 0);
    }

    /* loaded from: ProgramVertexFixedFunction$InternalBuilder.class */
    static class InternalBuilder extends Program.BaseProgramBuilder {
        public InternalBuilder(RenderScript rs) {
            super(rs);
        }

        public InternalBuilder addInput(Element e) throws IllegalStateException {
            if (this.mInputCount >= 8) {
                throw new RSIllegalArgumentException("Max input count exceeded.");
            }
            if (e.isComplex()) {
                throw new RSIllegalArgumentException("Complex elements not allowed.");
            }
            Element[] elementArr = this.mInputs;
            int i = this.mInputCount;
            this.mInputCount = i + 1;
            elementArr[i] = e;
            return this;
        }

        public ProgramVertexFixedFunction create() {
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
            int id = this.mRS.nProgramVertexCreate(this.mShader, texNames, tmp);
            ProgramVertexFixedFunction pv = new ProgramVertexFixedFunction(id, this.mRS);
            initProgram(pv);
            return pv;
        }
    }

    /* loaded from: ProgramVertexFixedFunction$Builder.class */
    public static class Builder {
        boolean mTextureMatrixEnable;
        String mShader;
        RenderScript mRS;

        public Builder(RenderScript rs) {
            this.mRS = rs;
        }

        public Builder setTextureMatrixEnable(boolean enable) {
            this.mTextureMatrixEnable = enable;
            return this;
        }

        static Type getConstantInputType(RenderScript rs) {
            Element.Builder b = new Element.Builder(rs);
            b.add(Element.MATRIX4X4(rs), "MV");
            b.add(Element.MATRIX4X4(rs), "P");
            b.add(Element.MATRIX4X4(rs), "TexMatrix");
            b.add(Element.MATRIX4X4(rs), "MVP");
            Type.Builder typeBuilder = new Type.Builder(rs, b.create());
            typeBuilder.setX(1);
            return typeBuilder.create();
        }

        private void buildShaderString() {
            this.mShader = "//rs_shader_internal\n";
            this.mShader += "varying vec4 varColor;\n";
            this.mShader += "varying vec2 varTex0;\n";
            this.mShader += "void main() {\n";
            this.mShader += "  gl_Position = UNI_MVP * ATTRIB_position;\n";
            this.mShader += "  gl_PointSize = 1.0;\n";
            this.mShader += "  varColor = ATTRIB_color;\n";
            if (this.mTextureMatrixEnable) {
                this.mShader += "  varTex0 = (UNI_TexMatrix * vec4(ATTRIB_texture0, 0.0, 1.0)).xy;\n";
            } else {
                this.mShader += "  varTex0 = ATTRIB_texture0;\n";
            }
            this.mShader += "}\n";
        }

        public ProgramVertexFixedFunction create() {
            buildShaderString();
            InternalBuilder sb = new InternalBuilder(this.mRS);
            sb.setShader(this.mShader);
            sb.addConstant(getConstantInputType(this.mRS));
            Element.Builder b = new Element.Builder(this.mRS);
            b.add(Element.F32_4(this.mRS), BrowserContract.Bookmarks.POSITION);
            b.add(Element.F32_4(this.mRS), CalendarContract.ColorsColumns.COLOR);
            b.add(Element.F32_3(this.mRS), "normal");
            b.add(Element.F32_2(this.mRS), "texture0");
            sb.addInput(b.create());
            return sb.create();
        }
    }

    /* loaded from: ProgramVertexFixedFunction$Constants.class */
    public static class Constants {
        static final int MODELVIEW_OFFSET = 0;
        static final int PROJECTION_OFFSET = 16;
        static final int TEXTURE_OFFSET = 32;
        Matrix4f mModel;
        Matrix4f mProjection;
        Matrix4f mTexture;
        Allocation mAlloc;
        private FieldPacker mIOBuffer;

        Allocation getAllocation() {
            return this.mAlloc;
        }

        public Constants(RenderScript rs) {
            Type constInputType = Builder.getConstantInputType(rs);
            this.mAlloc = Allocation.createTyped(rs, constInputType);
            int bufferSize = constInputType.getElement().getBytesSize() * constInputType.getCount();
            this.mIOBuffer = new FieldPacker(bufferSize);
            this.mModel = new Matrix4f();
            this.mProjection = new Matrix4f();
            this.mTexture = new Matrix4f();
            setModelview(new Matrix4f());
            setProjection(new Matrix4f());
            setTexture(new Matrix4f());
        }

        public void destroy() {
            this.mAlloc.destroy();
            this.mAlloc = null;
        }

        private void addToBuffer(int offset, Matrix4f m) {
            this.mIOBuffer.reset(offset);
            for (int i = 0; i < 16; i++) {
                this.mIOBuffer.addF32(m.mMat[i]);
            }
            this.mAlloc.setFromFieldPacker(0, this.mIOBuffer);
        }

        public void setModelview(Matrix4f m) {
            this.mModel.load(m);
            addToBuffer(0, m);
        }

        public void setProjection(Matrix4f m) {
            this.mProjection.load(m);
            addToBuffer(64, m);
        }

        public void setTexture(Matrix4f m) {
            this.mTexture.load(m);
            addToBuffer(128, m);
        }
    }
}