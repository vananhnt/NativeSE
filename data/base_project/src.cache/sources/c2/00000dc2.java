package android.renderscript;

import android.util.SparseArray;
import java.io.UnsupportedEncodingException;

/* loaded from: Script.class */
public class Script extends BaseObj {
    private final SparseArray<KernelID> mKIDs;
    private final SparseArray<FieldID> mFIDs;

    /* loaded from: Script$KernelID.class */
    public static final class KernelID extends BaseObj {
        Script mScript;
        int mSlot;
        int mSig;

        KernelID(int id, RenderScript rs, Script s, int slot, int sig) {
            super(id, rs);
            this.mScript = s;
            this.mSlot = slot;
            this.mSig = sig;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public KernelID createKernelID(int slot, int sig, Element ein, Element eout) {
        KernelID k = this.mKIDs.get(slot);
        if (k != null) {
            return k;
        }
        int id = this.mRS.nScriptKernelIDCreate(getID(this.mRS), slot, sig);
        if (id == 0) {
            throw new RSDriverException("Failed to create KernelID");
        }
        KernelID k2 = new KernelID(id, this.mRS, this, slot, sig);
        this.mKIDs.put(slot, k2);
        return k2;
    }

    /* loaded from: Script$FieldID.class */
    public static final class FieldID extends BaseObj {
        Script mScript;
        int mSlot;

        FieldID(int id, RenderScript rs, Script s, int slot) {
            super(id, rs);
            this.mScript = s;
            this.mSlot = slot;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public FieldID createFieldID(int slot, Element e) {
        FieldID f = this.mFIDs.get(slot);
        if (f != null) {
            return f;
        }
        int id = this.mRS.nScriptFieldIDCreate(getID(this.mRS), slot);
        if (id == 0) {
            throw new RSDriverException("Failed to create FieldID");
        }
        FieldID f2 = new FieldID(id, this.mRS, this, slot);
        this.mFIDs.put(slot, f2);
        return f2;
    }

    protected void invoke(int slot) {
        this.mRS.nScriptInvoke(getID(this.mRS), slot);
    }

    protected void invoke(int slot, FieldPacker v) {
        if (v != null) {
            this.mRS.nScriptInvokeV(getID(this.mRS), slot, v.getData());
        } else {
            this.mRS.nScriptInvoke(getID(this.mRS), slot);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void forEach(int slot, Allocation ain, Allocation aout, FieldPacker v) {
        if (ain == null && aout == null) {
            throw new RSIllegalArgumentException("At least one of ain or aout is required to be non-null.");
        }
        int in_id = 0;
        if (ain != null) {
            in_id = ain.getID(this.mRS);
        }
        int out_id = 0;
        if (aout != null) {
            out_id = aout.getID(this.mRS);
        }
        byte[] params = null;
        if (v != null) {
            params = v.getData();
        }
        this.mRS.nScriptForEach(getID(this.mRS), slot, in_id, out_id, params);
    }

    protected void forEach(int slot, Allocation ain, Allocation aout, FieldPacker v, LaunchOptions sc) {
        if (ain == null && aout == null) {
            throw new RSIllegalArgumentException("At least one of ain or aout is required to be non-null.");
        }
        if (sc == null) {
            forEach(slot, ain, aout, v);
            return;
        }
        int in_id = 0;
        if (ain != null) {
            in_id = ain.getID(this.mRS);
        }
        int out_id = 0;
        if (aout != null) {
            out_id = aout.getID(this.mRS);
        }
        byte[] params = null;
        if (v != null) {
            params = v.getData();
        }
        this.mRS.nScriptForEachClipped(getID(this.mRS), slot, in_id, out_id, params, sc.xstart, sc.xend, sc.ystart, sc.yend, sc.zstart, sc.zend);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Script(int id, RenderScript rs) {
        super(id, rs);
        this.mKIDs = new SparseArray<>();
        this.mFIDs = new SparseArray<>();
    }

    public void bindAllocation(Allocation va, int slot) {
        this.mRS.validate();
        if (va != null) {
            this.mRS.nScriptBindAllocation(getID(this.mRS), va.getID(this.mRS), slot);
        } else {
            this.mRS.nScriptBindAllocation(getID(this.mRS), 0, slot);
        }
    }

    public void setVar(int index, float v) {
        this.mRS.nScriptSetVarF(getID(this.mRS), index, v);
    }

    public float getVarF(int index) {
        return this.mRS.nScriptGetVarF(getID(this.mRS), index);
    }

    public void setVar(int index, double v) {
        this.mRS.nScriptSetVarD(getID(this.mRS), index, v);
    }

    public double getVarD(int index) {
        return this.mRS.nScriptGetVarD(getID(this.mRS), index);
    }

    public void setVar(int index, int v) {
        this.mRS.nScriptSetVarI(getID(this.mRS), index, v);
    }

    public int getVarI(int index) {
        return this.mRS.nScriptGetVarI(getID(this.mRS), index);
    }

    public void setVar(int index, long v) {
        this.mRS.nScriptSetVarJ(getID(this.mRS), index, v);
    }

    public long getVarJ(int index) {
        return this.mRS.nScriptGetVarJ(getID(this.mRS), index);
    }

    public void setVar(int index, boolean v) {
        this.mRS.nScriptSetVarI(getID(this.mRS), index, v ? 1 : 0);
    }

    public boolean getVarB(int index) {
        return this.mRS.nScriptGetVarI(getID(this.mRS), index) > 0;
    }

    public void setVar(int index, BaseObj o) {
        this.mRS.nScriptSetVarObj(getID(this.mRS), index, o == null ? 0 : o.getID(this.mRS));
    }

    public void setVar(int index, FieldPacker v) {
        this.mRS.nScriptSetVarV(getID(this.mRS), index, v.getData());
    }

    public void setVar(int index, FieldPacker v, Element e, int[] dims) {
        this.mRS.nScriptSetVarVE(getID(this.mRS), index, v.getData(), e.getID(this.mRS), dims);
    }

    public void getVarV(int index, FieldPacker v) {
        this.mRS.nScriptGetVarV(getID(this.mRS), index, v.getData());
    }

    public void setTimeZone(String timeZone) {
        this.mRS.validate();
        try {
            this.mRS.nScriptSetTimeZone(getID(this.mRS), timeZone.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /* loaded from: Script$Builder.class */
    public static class Builder {
        RenderScript mRS;

        Builder(RenderScript rs) {
            this.mRS = rs;
        }
    }

    /* loaded from: Script$FieldBase.class */
    public static class FieldBase {
        protected Element mElement;
        protected Allocation mAllocation;

        protected void init(RenderScript rs, int dimx) {
            this.mAllocation = Allocation.createSized(rs, this.mElement, dimx, 1);
        }

        protected void init(RenderScript rs, int dimx, int usages) {
            this.mAllocation = Allocation.createSized(rs, this.mElement, dimx, 1 | usages);
        }

        protected FieldBase() {
        }

        public Element getElement() {
            return this.mElement;
        }

        public Type getType() {
            return this.mAllocation.getType();
        }

        public Allocation getAllocation() {
            return this.mAllocation;
        }

        public void updateAllocation() {
        }
    }

    /* loaded from: Script$LaunchOptions.class */
    public static final class LaunchOptions {
        private int xstart = 0;
        private int ystart = 0;
        private int xend = 0;
        private int yend = 0;
        private int zstart = 0;
        private int zend = 0;
        private int strategy;

        public LaunchOptions setX(int xstartArg, int xendArg) {
            if (xstartArg < 0 || xendArg <= xstartArg) {
                throw new RSIllegalArgumentException("Invalid dimensions");
            }
            this.xstart = xstartArg;
            this.xend = xendArg;
            return this;
        }

        public LaunchOptions setY(int ystartArg, int yendArg) {
            if (ystartArg < 0 || yendArg <= ystartArg) {
                throw new RSIllegalArgumentException("Invalid dimensions");
            }
            this.ystart = ystartArg;
            this.yend = yendArg;
            return this;
        }

        public LaunchOptions setZ(int zstartArg, int zendArg) {
            if (zstartArg < 0 || zendArg <= zstartArg) {
                throw new RSIllegalArgumentException("Invalid dimensions");
            }
            this.zstart = zstartArg;
            this.zend = zendArg;
            return this;
        }

        public int getXStart() {
            return this.xstart;
        }

        public int getXEnd() {
            return this.xend;
        }

        public int getYStart() {
            return this.ystart;
        }

        public int getYEnd() {
            return this.yend;
        }

        public int getZStart() {
            return this.zstart;
        }

        public int getZEnd() {
            return this.zend;
        }
    }
}