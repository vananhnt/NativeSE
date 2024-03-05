package android.renderscript;

import android.renderscript.Script;

/* loaded from: ScriptIntrinsicBlur.class */
public final class ScriptIntrinsicBlur extends ScriptIntrinsic {
    private final float[] mValues;
    private Allocation mInput;

    private ScriptIntrinsicBlur(int id, RenderScript rs) {
        super(id, rs);
        this.mValues = new float[9];
    }

    public static ScriptIntrinsicBlur create(RenderScript rs, Element e) {
        if (!e.isCompatible(Element.U8_4(rs)) && !e.isCompatible(Element.U8(rs))) {
            throw new RSIllegalArgumentException("Unsuported element type.");
        }
        int id = rs.nScriptIntrinsicCreate(5, e.getID(rs));
        ScriptIntrinsicBlur sib = new ScriptIntrinsicBlur(id, rs);
        sib.setRadius(5.0f);
        return sib;
    }

    public void setInput(Allocation ain) {
        this.mInput = ain;
        setVar(1, ain);
    }

    public void setRadius(float radius) {
        if (radius <= 0.0f || radius > 25.0f) {
            throw new RSIllegalArgumentException("Radius out of range (0 < r <= 25).");
        }
        setVar(0, radius);
    }

    public void forEach(Allocation aout) {
        forEach(0, null, aout, null);
    }

    public Script.KernelID getKernelID() {
        return createKernelID(0, 2, null, null);
    }

    public Script.FieldID getFieldID_Input() {
        return createFieldID(1, null);
    }
}