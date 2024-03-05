package android.renderscript;

import android.renderscript.Program;

/* loaded from: ProgramFragment.class */
public class ProgramFragment extends Program {
    /* JADX INFO: Access modifiers changed from: package-private */
    public ProgramFragment(int id, RenderScript rs) {
        super(id, rs);
    }

    /* loaded from: ProgramFragment$Builder.class */
    public static class Builder extends Program.BaseProgramBuilder {
        public Builder(RenderScript rs) {
            super(rs);
        }

        public ProgramFragment create() {
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
            ProgramFragment pf = new ProgramFragment(id, this.mRS);
            initProgram(pf);
            return pf;
        }
    }
}