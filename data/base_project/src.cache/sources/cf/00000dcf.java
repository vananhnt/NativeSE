package android.renderscript;

import android.renderscript.Script;

/* loaded from: ScriptIntrinsic3DLUT.class */
public final class ScriptIntrinsic3DLUT extends ScriptIntrinsic {
    private Allocation mLUT;
    private Element mElement;

    private ScriptIntrinsic3DLUT(int id, RenderScript rs, Element e) {
        super(id, rs);
        this.mElement = e;
    }

    public static ScriptIntrinsic3DLUT create(RenderScript rs, Element e) {
        int id = rs.nScriptIntrinsicCreate(8, e.getID(rs));
        if (!e.isCompatible(Element.U8_4(rs))) {
            throw new RSIllegalArgumentException("Element must be compatible with uchar4.");
        }
        return new ScriptIntrinsic3DLUT(id, rs, e);
    }

    public void setLUT(Allocation lut) {
        Type t = lut.getType();
        if (t.getZ() == 0) {
            throw new RSIllegalArgumentException("LUT must be 3d.");
        }
        if (!t.getElement().isCompatible(this.mElement)) {
            throw new RSIllegalArgumentException("LUT element type must match.");
        }
        this.mLUT = lut;
        setVar(0, this.mLUT);
    }

    public void forEach(Allocation ain, Allocation aout) {
        forEach(0, ain, aout, null);
    }

    public Script.KernelID getKernelID() {
        return createKernelID(0, 3, null, null);
    }
}