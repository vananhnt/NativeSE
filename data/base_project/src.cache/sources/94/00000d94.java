package android.renderscript;

/* loaded from: Path.class */
public class Path extends BaseObj {
    Allocation mVertexBuffer;
    Allocation mLoopBuffer;
    Primitive mPrimitive;
    float mQuality;
    boolean mCoverageToAlpha;

    /* loaded from: Path$Primitive.class */
    public enum Primitive {
        QUADRATIC_BEZIER(0),
        CUBIC_BEZIER(1);
        
        int mID;

        Primitive(int id) {
            this.mID = id;
        }
    }

    Path(int id, RenderScript rs, Primitive p, Allocation vtx, Allocation loop, float q) {
        super(id, rs);
        this.mVertexBuffer = vtx;
        this.mLoopBuffer = loop;
        this.mPrimitive = p;
        this.mQuality = q;
    }

    public Allocation getVertexAllocation() {
        return this.mVertexBuffer;
    }

    public Allocation getLoopAllocation() {
        return this.mLoopBuffer;
    }

    public Primitive getPrimitive() {
        return this.mPrimitive;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.renderscript.BaseObj
    public void updateFromNative() {
    }

    public static Path createStaticPath(RenderScript rs, Primitive p, float quality, Allocation vtx) {
        int id = rs.nPathCreate(p.mID, false, vtx.getID(rs), 0, quality);
        Path newPath = new Path(id, rs, p, null, null, quality);
        return newPath;
    }

    public static Path createStaticPath(RenderScript rs, Primitive p, float quality, Allocation vtx, Allocation loops) {
        return null;
    }

    public static Path createDynamicPath(RenderScript rs, Primitive p, float quality, Allocation vtx) {
        return null;
    }

    public static Path createDynamicPath(RenderScript rs, Primitive p, float quality, Allocation vtx, Allocation loops) {
        return null;
    }
}