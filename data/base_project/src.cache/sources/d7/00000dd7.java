package android.renderscript;

import android.renderscript.Script;

/* loaded from: ScriptIntrinsicYuvToRGB.class */
public final class ScriptIntrinsicYuvToRGB extends ScriptIntrinsic {
    private Allocation mInput;

    ScriptIntrinsicYuvToRGB(int id, RenderScript rs) {
        super(id, rs);
    }

    public static ScriptIntrinsicYuvToRGB create(RenderScript rs, Element e) {
        int id = rs.nScriptIntrinsicCreate(6, e.getID(rs));
        ScriptIntrinsicYuvToRGB si = new ScriptIntrinsicYuvToRGB(id, rs);
        return si;
    }

    public void setInput(Allocation ain) {
        this.mInput = ain;
        setVar(0, ain);
    }

    public void forEach(Allocation aout) {
        forEach(0, null, aout, null);
    }

    public Script.KernelID getKernelID() {
        return createKernelID(0, 2, null, null);
    }

    public Script.FieldID getFieldID_Input() {
        return createFieldID(0, null);
    }
}