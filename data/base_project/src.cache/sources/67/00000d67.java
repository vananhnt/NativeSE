package android.renderscript;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.os.Trace;
import android.renderscript.Element;
import android.renderscript.Type;
import android.util.Log;
import android.view.Surface;
import gov.nist.core.Separators;
import java.util.HashMap;

/* loaded from: Allocation.class */
public class Allocation extends BaseObj {
    Type mType;
    Bitmap mBitmap;
    int mUsage;
    Allocation mAdaptedAllocation;
    int mSize;
    boolean mConstrainedLOD;
    boolean mConstrainedFace;
    boolean mConstrainedY;
    boolean mConstrainedZ;
    boolean mReadAllowed;
    boolean mWriteAllowed;
    int mSelectedY;
    int mSelectedZ;
    int mSelectedLOD;
    Type.CubemapFace mSelectedFace;
    int mCurrentDimX;
    int mCurrentDimY;
    int mCurrentDimZ;
    int mCurrentCount;
    OnBufferAvailableListener mBufferNotifier;
    public static final int USAGE_SCRIPT = 1;
    public static final int USAGE_GRAPHICS_TEXTURE = 2;
    public static final int USAGE_GRAPHICS_VERTEX = 4;
    public static final int USAGE_GRAPHICS_CONSTANTS = 8;
    public static final int USAGE_GRAPHICS_RENDER_TARGET = 16;
    public static final int USAGE_IO_INPUT = 32;
    public static final int USAGE_IO_OUTPUT = 64;
    public static final int USAGE_SHARED = 128;
    static HashMap<Integer, Allocation> mAllocationMap = new HashMap<>();
    static BitmapFactory.Options mBitmapOptions = new BitmapFactory.Options();

    /* loaded from: Allocation$OnBufferAvailableListener.class */
    public interface OnBufferAvailableListener {
        void onBufferAvailable(Allocation allocation);
    }

    static {
        mBitmapOptions.inScaled = false;
    }

    /* loaded from: Allocation$MipmapControl.class */
    public enum MipmapControl {
        MIPMAP_NONE(0),
        MIPMAP_FULL(1),
        MIPMAP_ON_SYNC_TO_TEXTURE(2);
        
        int mID;

        MipmapControl(int id) {
            this.mID = id;
        }
    }

    private int getIDSafe() {
        if (this.mAdaptedAllocation != null) {
            return this.mAdaptedAllocation.getID(this.mRS);
        }
        return getID(this.mRS);
    }

    public Element getElement() {
        return this.mType.getElement();
    }

    public int getUsage() {
        return this.mUsage;
    }

    public int getBytesSize() {
        return this.mType.getCount() * this.mType.getElement().getBytesSize();
    }

    private void updateCacheInfo(Type t) {
        this.mCurrentDimX = t.getX();
        this.mCurrentDimY = t.getY();
        this.mCurrentDimZ = t.getZ();
        this.mCurrentCount = this.mCurrentDimX;
        if (this.mCurrentDimY > 1) {
            this.mCurrentCount *= this.mCurrentDimY;
        }
        if (this.mCurrentDimZ > 1) {
            this.mCurrentCount *= this.mCurrentDimZ;
        }
    }

    private void setBitmap(Bitmap b) {
        this.mBitmap = b;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Allocation(int id, RenderScript rs, Type t, int usage) {
        super(id, rs);
        this.mReadAllowed = true;
        this.mWriteAllowed = true;
        this.mSelectedFace = Type.CubemapFace.POSITIVE_X;
        if ((usage & (-256)) != 0) {
            throw new RSIllegalArgumentException("Unknown usage specified.");
        }
        if ((usage & 32) != 0) {
            this.mWriteAllowed = false;
            if ((usage & (-36)) != 0) {
                throw new RSIllegalArgumentException("Invalid usage combination.");
            }
        }
        this.mType = t;
        this.mUsage = usage;
        if (t != null) {
            this.mSize = this.mType.getCount() * this.mType.getElement().getBytesSize();
            updateCacheInfo(t);
        }
        try {
            RenderScript.registerNativeAllocation.invoke(RenderScript.sRuntime, Integer.valueOf(this.mSize));
        } catch (Exception e) {
            Log.e("RenderScript_jni", "Couldn't invoke registerNativeAllocation:" + e);
            throw new RSRuntimeException("Couldn't invoke registerNativeAllocation:" + e);
        }
    }

    @Override // android.renderscript.BaseObj
    protected void finalize() throws Throwable {
        RenderScript.registerNativeFree.invoke(RenderScript.sRuntime, Integer.valueOf(this.mSize));
        super.finalize();
    }

    private void validateIsInt32() {
        if (this.mType.mElement.mType == Element.DataType.SIGNED_32 || this.mType.mElement.mType == Element.DataType.UNSIGNED_32) {
            return;
        }
        throw new RSIllegalArgumentException("32 bit integer source does not match allocation type " + this.mType.mElement.mType);
    }

    private void validateIsInt16() {
        if (this.mType.mElement.mType == Element.DataType.SIGNED_16 || this.mType.mElement.mType == Element.DataType.UNSIGNED_16) {
            return;
        }
        throw new RSIllegalArgumentException("16 bit integer source does not match allocation type " + this.mType.mElement.mType);
    }

    private void validateIsInt8() {
        if (this.mType.mElement.mType == Element.DataType.SIGNED_8 || this.mType.mElement.mType == Element.DataType.UNSIGNED_8) {
            return;
        }
        throw new RSIllegalArgumentException("8 bit integer source does not match allocation type " + this.mType.mElement.mType);
    }

    private void validateIsFloat32() {
        if (this.mType.mElement.mType == Element.DataType.FLOAT_32) {
            return;
        }
        throw new RSIllegalArgumentException("32 bit float source does not match allocation type " + this.mType.mElement.mType);
    }

    private void validateIsObject() {
        if (this.mType.mElement.mType == Element.DataType.RS_ELEMENT || this.mType.mElement.mType == Element.DataType.RS_TYPE || this.mType.mElement.mType == Element.DataType.RS_ALLOCATION || this.mType.mElement.mType == Element.DataType.RS_SAMPLER || this.mType.mElement.mType == Element.DataType.RS_SCRIPT || this.mType.mElement.mType == Element.DataType.RS_MESH || this.mType.mElement.mType == Element.DataType.RS_PROGRAM_FRAGMENT || this.mType.mElement.mType == Element.DataType.RS_PROGRAM_VERTEX || this.mType.mElement.mType == Element.DataType.RS_PROGRAM_RASTER || this.mType.mElement.mType == Element.DataType.RS_PROGRAM_STORE) {
            return;
        }
        throw new RSIllegalArgumentException("Object source does not match allocation type " + this.mType.mElement.mType);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.renderscript.BaseObj
    public void updateFromNative() {
        super.updateFromNative();
        int typeID = this.mRS.nAllocationGetType(getID(this.mRS));
        if (typeID != 0) {
            this.mType = new Type(typeID, this.mRS);
            this.mType.updateFromNative();
            updateCacheInfo(this.mType);
        }
    }

    public Type getType() {
        return this.mType;
    }

    public void syncAll(int srcLocation) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "syncAll");
        switch (srcLocation) {
            case 1:
            case 2:
                if ((this.mUsage & 128) != 0) {
                    copyFrom(this.mBitmap);
                    break;
                }
                break;
            case 4:
            case 8:
                break;
            case 128:
                if ((this.mUsage & 128) != 0) {
                    copyTo(this.mBitmap);
                    break;
                }
                break;
            default:
                throw new RSIllegalArgumentException("Source must be exactly one usage type.");
        }
        this.mRS.validate();
        this.mRS.nAllocationSyncAll(getIDSafe(), srcLocation);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void ioSend() {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "ioSend");
        if ((this.mUsage & 64) == 0) {
            throw new RSIllegalArgumentException("Can only send buffer if IO_OUTPUT usage specified.");
        }
        this.mRS.validate();
        this.mRS.nAllocationIoSend(getID(this.mRS));
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void ioSendOutput() {
        ioSend();
    }

    public void ioReceive() {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "ioReceive");
        if ((this.mUsage & 32) == 0) {
            throw new RSIllegalArgumentException("Can only receive if IO_INPUT usage specified.");
        }
        this.mRS.validate();
        this.mRS.nAllocationIoReceive(getID(this.mRS));
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copyFrom(BaseObj[] d) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copyFrom");
        this.mRS.validate();
        validateIsObject();
        if (d.length != this.mCurrentCount) {
            throw new RSIllegalArgumentException("Array size mismatch, allocation sizeX = " + this.mCurrentCount + ", array length = " + d.length);
        }
        int[] i = new int[d.length];
        for (int ct = 0; ct < d.length; ct++) {
            i[ct] = d[ct].getID(this.mRS);
        }
        copy1DRangeFromUnchecked(0, this.mCurrentCount, i);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    private void validateBitmapFormat(Bitmap b) {
        Bitmap.Config bc = b.getConfig();
        if (bc == null) {
            throw new RSIllegalArgumentException("Bitmap has an unsupported format for this operation");
        }
        switch (bc) {
            case ALPHA_8:
                if (this.mType.getElement().mKind != Element.DataKind.PIXEL_A) {
                    throw new RSIllegalArgumentException("Allocation kind is " + this.mType.getElement().mKind + ", type " + this.mType.getElement().mType + " of " + this.mType.getElement().getBytesSize() + " bytes, passed bitmap was " + bc);
                }
                return;
            case ARGB_8888:
                if (this.mType.getElement().mKind != Element.DataKind.PIXEL_RGBA || this.mType.getElement().getBytesSize() != 4) {
                    throw new RSIllegalArgumentException("Allocation kind is " + this.mType.getElement().mKind + ", type " + this.mType.getElement().mType + " of " + this.mType.getElement().getBytesSize() + " bytes, passed bitmap was " + bc);
                }
                return;
            case RGB_565:
                if (this.mType.getElement().mKind != Element.DataKind.PIXEL_RGB || this.mType.getElement().getBytesSize() != 2) {
                    throw new RSIllegalArgumentException("Allocation kind is " + this.mType.getElement().mKind + ", type " + this.mType.getElement().mType + " of " + this.mType.getElement().getBytesSize() + " bytes, passed bitmap was " + bc);
                }
                return;
            case ARGB_4444:
                if (this.mType.getElement().mKind != Element.DataKind.PIXEL_RGBA || this.mType.getElement().getBytesSize() != 2) {
                    throw new RSIllegalArgumentException("Allocation kind is " + this.mType.getElement().mKind + ", type " + this.mType.getElement().mType + " of " + this.mType.getElement().getBytesSize() + " bytes, passed bitmap was " + bc);
                }
                return;
            default:
                return;
        }
    }

    private void validateBitmapSize(Bitmap b) {
        if (this.mCurrentDimX != b.getWidth() || this.mCurrentDimY != b.getHeight()) {
            throw new RSIllegalArgumentException("Cannot update allocation from bitmap, sizes mismatch");
        }
    }

    public void copyFromUnchecked(int[] d) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copyFromUnchecked");
        this.mRS.validate();
        if (this.mCurrentDimZ > 0) {
            copy3DRangeFromUnchecked(0, 0, 0, this.mCurrentDimX, this.mCurrentDimY, this.mCurrentDimZ, d);
        } else if (this.mCurrentDimY > 0) {
            copy2DRangeFromUnchecked(0, 0, this.mCurrentDimX, this.mCurrentDimY, d);
        } else {
            copy1DRangeFromUnchecked(0, this.mCurrentCount, d);
        }
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copyFromUnchecked(short[] d) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copyFromUnchecked");
        this.mRS.validate();
        if (this.mCurrentDimZ > 0) {
            copy3DRangeFromUnchecked(0, 0, 0, this.mCurrentDimX, this.mCurrentDimY, this.mCurrentDimZ, d);
        } else if (this.mCurrentDimY > 0) {
            copy2DRangeFromUnchecked(0, 0, this.mCurrentDimX, this.mCurrentDimY, d);
        } else {
            copy1DRangeFromUnchecked(0, this.mCurrentCount, d);
        }
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copyFromUnchecked(byte[] d) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copyFromUnchecked");
        this.mRS.validate();
        if (this.mCurrentDimZ > 0) {
            copy3DRangeFromUnchecked(0, 0, 0, this.mCurrentDimX, this.mCurrentDimY, this.mCurrentDimZ, d);
        } else if (this.mCurrentDimY > 0) {
            copy2DRangeFromUnchecked(0, 0, this.mCurrentDimX, this.mCurrentDimY, d);
        } else {
            copy1DRangeFromUnchecked(0, this.mCurrentCount, d);
        }
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copyFromUnchecked(float[] d) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copyFromUnchecked");
        this.mRS.validate();
        if (this.mCurrentDimZ > 0) {
            copy3DRangeFromUnchecked(0, 0, 0, this.mCurrentDimX, this.mCurrentDimY, this.mCurrentDimZ, d);
        } else if (this.mCurrentDimY > 0) {
            copy2DRangeFromUnchecked(0, 0, this.mCurrentDimX, this.mCurrentDimY, d);
        } else {
            copy1DRangeFromUnchecked(0, this.mCurrentCount, d);
        }
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copyFrom(int[] d) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copyFrom");
        this.mRS.validate();
        if (this.mCurrentDimZ > 0) {
            copy3DRangeFrom(0, 0, 0, this.mCurrentDimX, this.mCurrentDimY, this.mCurrentDimZ, d);
        } else if (this.mCurrentDimY > 0) {
            copy2DRangeFrom(0, 0, this.mCurrentDimX, this.mCurrentDimY, d);
        } else {
            copy1DRangeFrom(0, this.mCurrentCount, d);
        }
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copyFrom(short[] d) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copyFrom");
        this.mRS.validate();
        if (this.mCurrentDimZ > 0) {
            copy3DRangeFrom(0, 0, 0, this.mCurrentDimX, this.mCurrentDimY, this.mCurrentDimZ, d);
        } else if (this.mCurrentDimY > 0) {
            copy2DRangeFrom(0, 0, this.mCurrentDimX, this.mCurrentDimY, d);
        } else {
            copy1DRangeFrom(0, this.mCurrentCount, d);
        }
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copyFrom(byte[] d) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copyFrom");
        this.mRS.validate();
        if (this.mCurrentDimZ > 0) {
            copy3DRangeFrom(0, 0, 0, this.mCurrentDimX, this.mCurrentDimY, this.mCurrentDimZ, d);
        } else if (this.mCurrentDimY > 0) {
            copy2DRangeFrom(0, 0, this.mCurrentDimX, this.mCurrentDimY, d);
        } else {
            copy1DRangeFrom(0, this.mCurrentCount, d);
        }
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copyFrom(float[] d) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copyFrom");
        this.mRS.validate();
        if (this.mCurrentDimZ > 0) {
            copy3DRangeFrom(0, 0, 0, this.mCurrentDimX, this.mCurrentDimY, this.mCurrentDimZ, d);
        } else if (this.mCurrentDimY > 0) {
            copy2DRangeFrom(0, 0, this.mCurrentDimX, this.mCurrentDimY, d);
        } else {
            copy1DRangeFrom(0, this.mCurrentCount, d);
        }
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copyFrom(Bitmap b) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copyFrom");
        this.mRS.validate();
        if (b.getConfig() == null) {
            Bitmap newBitmap = Bitmap.createBitmap(b.getWidth(), b.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(newBitmap);
            c.drawBitmap(b, 0.0f, 0.0f, (Paint) null);
            copyFrom(newBitmap);
            return;
        }
        validateBitmapSize(b);
        validateBitmapFormat(b);
        this.mRS.nAllocationCopyFromBitmap(getID(this.mRS), b);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copyFrom(Allocation a) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copyFrom");
        this.mRS.validate();
        if (!this.mType.equals(a.getType())) {
            throw new RSIllegalArgumentException("Types of allocations must match.");
        }
        copy2DRangeFrom(0, 0, this.mCurrentDimX, this.mCurrentDimY, a, 0, 0);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void setFromFieldPacker(int xoff, FieldPacker fp) {
        this.mRS.validate();
        int eSize = this.mType.mElement.getBytesSize();
        byte[] data = fp.getData();
        int count = data.length / eSize;
        if (eSize * count != data.length) {
            throw new RSIllegalArgumentException("Field packer length " + data.length + " not divisible by element size " + eSize + Separators.DOT);
        }
        copy1DRangeFromUnchecked(xoff, count, data);
    }

    public void setFromFieldPacker(int xoff, int component_number, FieldPacker fp) {
        this.mRS.validate();
        if (component_number >= this.mType.mElement.mElements.length) {
            throw new RSIllegalArgumentException("Component_number " + component_number + " out of range.");
        }
        if (xoff < 0) {
            throw new RSIllegalArgumentException("Offset must be >= 0.");
        }
        byte[] data = fp.getData();
        int eSize = this.mType.mElement.mElements[component_number].getBytesSize() * this.mType.mElement.mArraySizes[component_number];
        if (data.length != eSize) {
            throw new RSIllegalArgumentException("Field packer sizelength " + data.length + " does not match component size " + eSize + Separators.DOT);
        }
        this.mRS.nAllocationElementData1D(getIDSafe(), xoff, this.mSelectedLOD, component_number, data, data.length);
    }

    private void data1DChecks(int off, int count, int len, int dataSize) {
        this.mRS.validate();
        if (off < 0) {
            throw new RSIllegalArgumentException("Offset must be >= 0.");
        }
        if (count < 1) {
            throw new RSIllegalArgumentException("Count must be >= 1.");
        }
        if (off + count > this.mCurrentCount) {
            throw new RSIllegalArgumentException("Overflow, Available count " + this.mCurrentCount + ", got " + count + " at offset " + off + Separators.DOT);
        }
        if (len < dataSize) {
            throw new RSIllegalArgumentException("Array too small for allocation type.");
        }
    }

    public void generateMipmaps() {
        this.mRS.nAllocationGenerateMipmaps(getID(this.mRS));
    }

    public void copy1DRangeFromUnchecked(int off, int count, int[] d) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copy1DRangeFromUnchecked");
        int dataSize = this.mType.mElement.getBytesSize() * count;
        data1DChecks(off, count, d.length * 4, dataSize);
        this.mRS.nAllocationData1D(getIDSafe(), off, this.mSelectedLOD, count, d, dataSize);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copy1DRangeFromUnchecked(int off, int count, short[] d) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copy1DRangeFromUnchecked");
        int dataSize = this.mType.mElement.getBytesSize() * count;
        data1DChecks(off, count, d.length * 2, dataSize);
        this.mRS.nAllocationData1D(getIDSafe(), off, this.mSelectedLOD, count, d, dataSize);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copy1DRangeFromUnchecked(int off, int count, byte[] d) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copy1DRangeFromUnchecked");
        int dataSize = this.mType.mElement.getBytesSize() * count;
        data1DChecks(off, count, d.length, dataSize);
        this.mRS.nAllocationData1D(getIDSafe(), off, this.mSelectedLOD, count, d, dataSize);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copy1DRangeFromUnchecked(int off, int count, float[] d) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copy1DRangeFromUnchecked");
        int dataSize = this.mType.mElement.getBytesSize() * count;
        data1DChecks(off, count, d.length * 4, dataSize);
        this.mRS.nAllocationData1D(getIDSafe(), off, this.mSelectedLOD, count, d, dataSize);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copy1DRangeFrom(int off, int count, int[] d) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copy1DRangeFrom");
        validateIsInt32();
        copy1DRangeFromUnchecked(off, count, d);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copy1DRangeFrom(int off, int count, short[] d) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copy1DRangeFrom");
        validateIsInt16();
        copy1DRangeFromUnchecked(off, count, d);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copy1DRangeFrom(int off, int count, byte[] d) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copy1DRangeFrom");
        validateIsInt8();
        copy1DRangeFromUnchecked(off, count, d);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copy1DRangeFrom(int off, int count, float[] d) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copy1DRangeFrom");
        validateIsFloat32();
        copy1DRangeFromUnchecked(off, count, d);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copy1DRangeFrom(int off, int count, Allocation data, int dataOff) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copy1DRangeFrom");
        this.mRS.nAllocationData2D(getIDSafe(), off, 0, this.mSelectedLOD, this.mSelectedFace.mID, count, 1, data.getID(this.mRS), dataOff, 0, data.mSelectedLOD, data.mSelectedFace.mID);
    }

    private void validate2DRange(int xoff, int yoff, int w, int h) {
        if (this.mAdaptedAllocation == null) {
            if (xoff < 0 || yoff < 0) {
                throw new RSIllegalArgumentException("Offset cannot be negative.");
            }
            if (h < 0 || w < 0) {
                throw new RSIllegalArgumentException("Height or width cannot be negative.");
            }
            if (xoff + w > this.mCurrentDimX || yoff + h > this.mCurrentDimY) {
                throw new RSIllegalArgumentException("Updated region larger than allocation.");
            }
        }
    }

    void copy2DRangeFromUnchecked(int xoff, int yoff, int w, int h, byte[] data) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copy2DRangeFromUnchecked");
        this.mRS.validate();
        validate2DRange(xoff, yoff, w, h);
        this.mRS.nAllocationData2D(getIDSafe(), xoff, yoff, this.mSelectedLOD, this.mSelectedFace.mID, w, h, data, data.length);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    void copy2DRangeFromUnchecked(int xoff, int yoff, int w, int h, short[] data) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copy2DRangeFromUnchecked");
        this.mRS.validate();
        validate2DRange(xoff, yoff, w, h);
        this.mRS.nAllocationData2D(getIDSafe(), xoff, yoff, this.mSelectedLOD, this.mSelectedFace.mID, w, h, data, data.length * 2);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    void copy2DRangeFromUnchecked(int xoff, int yoff, int w, int h, int[] data) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copy2DRangeFromUnchecked");
        this.mRS.validate();
        validate2DRange(xoff, yoff, w, h);
        this.mRS.nAllocationData2D(getIDSafe(), xoff, yoff, this.mSelectedLOD, this.mSelectedFace.mID, w, h, data, data.length * 4);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    void copy2DRangeFromUnchecked(int xoff, int yoff, int w, int h, float[] data) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copy2DRangeFromUnchecked");
        this.mRS.validate();
        validate2DRange(xoff, yoff, w, h);
        this.mRS.nAllocationData2D(getIDSafe(), xoff, yoff, this.mSelectedLOD, this.mSelectedFace.mID, w, h, data, data.length * 4);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copy2DRangeFrom(int xoff, int yoff, int w, int h, byte[] data) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copy2DRangeFrom");
        validateIsInt8();
        copy2DRangeFromUnchecked(xoff, yoff, w, h, data);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copy2DRangeFrom(int xoff, int yoff, int w, int h, short[] data) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copy2DRangeFrom");
        validateIsInt16();
        copy2DRangeFromUnchecked(xoff, yoff, w, h, data);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copy2DRangeFrom(int xoff, int yoff, int w, int h, int[] data) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copy2DRangeFrom");
        validateIsInt32();
        copy2DRangeFromUnchecked(xoff, yoff, w, h, data);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copy2DRangeFrom(int xoff, int yoff, int w, int h, float[] data) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copy2DRangeFrom");
        validateIsFloat32();
        copy2DRangeFromUnchecked(xoff, yoff, w, h, data);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copy2DRangeFrom(int xoff, int yoff, int w, int h, Allocation data, int dataXoff, int dataYoff) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copy2DRangeFrom");
        this.mRS.validate();
        validate2DRange(xoff, yoff, w, h);
        this.mRS.nAllocationData2D(getIDSafe(), xoff, yoff, this.mSelectedLOD, this.mSelectedFace.mID, w, h, data.getID(this.mRS), dataXoff, dataYoff, data.mSelectedLOD, data.mSelectedFace.mID);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copy2DRangeFrom(int xoff, int yoff, Bitmap data) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copy2DRangeFrom");
        this.mRS.validate();
        if (data.getConfig() == null) {
            Bitmap newBitmap = Bitmap.createBitmap(data.getWidth(), data.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(newBitmap);
            c.drawBitmap(data, 0.0f, 0.0f, (Paint) null);
            copy2DRangeFrom(xoff, yoff, newBitmap);
            return;
        }
        validateBitmapFormat(data);
        validate2DRange(xoff, yoff, data.getWidth(), data.getHeight());
        this.mRS.nAllocationData2D(getIDSafe(), xoff, yoff, this.mSelectedLOD, this.mSelectedFace.mID, data);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    private void validate3DRange(int xoff, int yoff, int zoff, int w, int h, int d) {
        if (this.mAdaptedAllocation == null) {
            if (xoff < 0 || yoff < 0 || zoff < 0) {
                throw new RSIllegalArgumentException("Offset cannot be negative.");
            }
            if (h < 0 || w < 0 || d < 0) {
                throw new RSIllegalArgumentException("Height or width cannot be negative.");
            }
            if (xoff + w > this.mCurrentDimX || yoff + h > this.mCurrentDimY || zoff + d > this.mCurrentDimZ) {
                throw new RSIllegalArgumentException("Updated region larger than allocation.");
            }
        }
    }

    void copy3DRangeFromUnchecked(int xoff, int yoff, int zoff, int w, int h, int d, byte[] data) {
        this.mRS.validate();
        validate3DRange(xoff, yoff, zoff, w, h, d);
        this.mRS.nAllocationData3D(getIDSafe(), xoff, yoff, zoff, this.mSelectedLOD, w, h, d, data, data.length);
    }

    void copy3DRangeFromUnchecked(int xoff, int yoff, int zoff, int w, int h, int d, short[] data) {
        this.mRS.validate();
        validate3DRange(xoff, yoff, zoff, w, h, d);
        this.mRS.nAllocationData3D(getIDSafe(), xoff, yoff, zoff, this.mSelectedLOD, w, h, d, data, data.length * 2);
    }

    void copy3DRangeFromUnchecked(int xoff, int yoff, int zoff, int w, int h, int d, int[] data) {
        this.mRS.validate();
        validate3DRange(xoff, yoff, zoff, w, h, d);
        this.mRS.nAllocationData3D(getIDSafe(), xoff, yoff, zoff, this.mSelectedLOD, w, h, d, data, data.length * 4);
    }

    void copy3DRangeFromUnchecked(int xoff, int yoff, int zoff, int w, int h, int d, float[] data) {
        this.mRS.validate();
        validate3DRange(xoff, yoff, zoff, w, h, d);
        this.mRS.nAllocationData3D(getIDSafe(), xoff, yoff, zoff, this.mSelectedLOD, w, h, d, data, data.length * 4);
    }

    public void copy3DRangeFrom(int xoff, int yoff, int zoff, int w, int h, int d, byte[] data) {
        validateIsInt8();
        copy3DRangeFromUnchecked(xoff, yoff, zoff, w, h, d, data);
    }

    public void copy3DRangeFrom(int xoff, int yoff, int zoff, int w, int h, int d, short[] data) {
        validateIsInt16();
        copy3DRangeFromUnchecked(xoff, yoff, zoff, w, h, d, data);
    }

    public void copy3DRangeFrom(int xoff, int yoff, int zoff, int w, int h, int d, int[] data) {
        validateIsInt32();
        copy3DRangeFromUnchecked(xoff, yoff, zoff, w, h, d, data);
    }

    public void copy3DRangeFrom(int xoff, int yoff, int zoff, int w, int h, int d, float[] data) {
        validateIsFloat32();
        copy3DRangeFromUnchecked(xoff, yoff, zoff, w, h, d, data);
    }

    public void copy3DRangeFrom(int xoff, int yoff, int zoff, int w, int h, int d, Allocation data, int dataXoff, int dataYoff, int dataZoff) {
        this.mRS.validate();
        validate3DRange(xoff, yoff, zoff, w, h, d);
        this.mRS.nAllocationData3D(getIDSafe(), xoff, yoff, zoff, this.mSelectedLOD, w, h, d, data.getID(this.mRS), dataXoff, dataYoff, dataZoff, data.mSelectedLOD);
    }

    public void copyTo(Bitmap b) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copyTo");
        this.mRS.validate();
        validateBitmapFormat(b);
        validateBitmapSize(b);
        this.mRS.nAllocationCopyToBitmap(getID(this.mRS), b);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copyTo(byte[] d) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copyTo");
        validateIsInt8();
        this.mRS.validate();
        this.mRS.nAllocationRead(getID(this.mRS), d);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copyTo(short[] d) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copyTo");
        validateIsInt16();
        this.mRS.validate();
        this.mRS.nAllocationRead(getID(this.mRS), d);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copyTo(int[] d) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copyTo");
        validateIsInt32();
        this.mRS.validate();
        this.mRS.nAllocationRead(getID(this.mRS), d);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copyTo(float[] d) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copyTo");
        validateIsFloat32();
        this.mRS.validate();
        this.mRS.nAllocationRead(getID(this.mRS), d);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public synchronized void resize(int dimX) {
        if (this.mType.getY() > 0 || this.mType.getZ() > 0 || this.mType.hasFaces() || this.mType.hasMipmaps()) {
            throw new RSInvalidStateException("Resize only support for 1D allocations at this time.");
        }
        this.mRS.nAllocationResize1D(getID(this.mRS), dimX);
        this.mRS.finish();
        int typeID = this.mRS.nAllocationGetType(getID(this.mRS));
        this.mType = new Type(typeID, this.mRS);
        this.mType.updateFromNative();
        updateCacheInfo(this.mType);
    }

    public static Allocation createTyped(RenderScript rs, Type type, MipmapControl mips, int usage) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "createTyped");
        rs.validate();
        if (type.getID(rs) == 0) {
            throw new RSInvalidStateException("Bad Type");
        }
        int id = rs.nAllocationCreateTyped(type.getID(rs), mips.mID, usage, 0);
        if (id == 0) {
            throw new RSRuntimeException("Allocation creation failed.");
        }
        Trace.traceEnd(Trace.TRACE_TAG_RS);
        return new Allocation(id, rs, type, usage);
    }

    public static Allocation createTyped(RenderScript rs, Type type, int usage) {
        return createTyped(rs, type, MipmapControl.MIPMAP_NONE, usage);
    }

    public static Allocation createTyped(RenderScript rs, Type type) {
        return createTyped(rs, type, MipmapControl.MIPMAP_NONE, 1);
    }

    public static Allocation createSized(RenderScript rs, Element e, int count, int usage) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "createSized");
        rs.validate();
        Type.Builder b = new Type.Builder(rs, e);
        b.setX(count);
        Type t = b.create();
        int id = rs.nAllocationCreateTyped(t.getID(rs), MipmapControl.MIPMAP_NONE.mID, usage, 0);
        if (id == 0) {
            throw new RSRuntimeException("Allocation creation failed.");
        }
        Trace.traceEnd(Trace.TRACE_TAG_RS);
        return new Allocation(id, rs, t, usage);
    }

    public static Allocation createSized(RenderScript rs, Element e, int count) {
        return createSized(rs, e, count, 1);
    }

    static Element elementFromBitmap(RenderScript rs, Bitmap b) {
        Bitmap.Config bc = b.getConfig();
        if (bc == Bitmap.Config.ALPHA_8) {
            return Element.A_8(rs);
        }
        if (bc == Bitmap.Config.ARGB_4444) {
            return Element.RGBA_4444(rs);
        }
        if (bc == Bitmap.Config.ARGB_8888) {
            return Element.RGBA_8888(rs);
        }
        if (bc == Bitmap.Config.RGB_565) {
            return Element.RGB_565(rs);
        }
        throw new RSInvalidStateException("Bad bitmap type: " + bc);
    }

    static Type typeFromBitmap(RenderScript rs, Bitmap b, MipmapControl mip) {
        Element e = elementFromBitmap(rs, b);
        Type.Builder tb = new Type.Builder(rs, e);
        tb.setX(b.getWidth());
        tb.setY(b.getHeight());
        tb.setMipmaps(mip == MipmapControl.MIPMAP_FULL);
        return tb.create();
    }

    public static Allocation createFromBitmap(RenderScript rs, Bitmap b, MipmapControl mips, int usage) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "createFromBitmap");
        rs.validate();
        if (b.getConfig() == null) {
            if ((usage & 128) != 0) {
                throw new RSIllegalArgumentException("USAGE_SHARED cannot be used with a Bitmap that has a null config.");
            }
            Bitmap newBitmap = Bitmap.createBitmap(b.getWidth(), b.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(newBitmap);
            c.drawBitmap(b, 0.0f, 0.0f, (Paint) null);
            return createFromBitmap(rs, newBitmap, mips, usage);
        }
        Type t = typeFromBitmap(rs, b, mips);
        if (mips == MipmapControl.MIPMAP_NONE && t.getElement().isCompatible(Element.RGBA_8888(rs)) && usage == 131) {
            int id = rs.nAllocationCreateBitmapBackedAllocation(t.getID(rs), mips.mID, b, usage);
            if (id == 0) {
                throw new RSRuntimeException("Load failed.");
            }
            Allocation alloc = new Allocation(id, rs, t, usage);
            alloc.setBitmap(b);
            return alloc;
        }
        int id2 = rs.nAllocationCreateFromBitmap(t.getID(rs), mips.mID, b, usage);
        if (id2 == 0) {
            throw new RSRuntimeException("Load failed.");
        }
        Trace.traceEnd(Trace.TRACE_TAG_RS);
        return new Allocation(id2, rs, t, usage);
    }

    public Surface getSurface() {
        if ((this.mUsage & 32) == 0) {
            throw new RSInvalidStateException("Allocation is not a surface texture.");
        }
        return this.mRS.nAllocationGetSurface(getID(this.mRS));
    }

    public void setSurfaceTexture(SurfaceTexture st) {
        setSurface(new Surface(st));
    }

    public void setSurface(Surface sur) {
        this.mRS.validate();
        if ((this.mUsage & 64) == 0) {
            throw new RSInvalidStateException("Allocation is not USAGE_IO_OUTPUT.");
        }
        this.mRS.nAllocationSetSurface(getID(this.mRS), sur);
    }

    public static Allocation createFromBitmap(RenderScript rs, Bitmap b) {
        if (rs.getApplicationContext().getApplicationInfo().targetSdkVersion >= 18) {
            return createFromBitmap(rs, b, MipmapControl.MIPMAP_NONE, 131);
        }
        return createFromBitmap(rs, b, MipmapControl.MIPMAP_NONE, 2);
    }

    public static Allocation createCubemapFromBitmap(RenderScript rs, Bitmap b, MipmapControl mips, int usage) {
        rs.validate();
        int height = b.getHeight();
        int width = b.getWidth();
        if (width % 6 != 0) {
            throw new RSIllegalArgumentException("Cubemap height must be multiple of 6");
        }
        if (width / 6 != height) {
            throw new RSIllegalArgumentException("Only square cube map faces supported");
        }
        boolean isPow2 = (height & (height - 1)) == 0;
        if (!isPow2) {
            throw new RSIllegalArgumentException("Only power of 2 cube faces supported");
        }
        Element e = elementFromBitmap(rs, b);
        Type.Builder tb = new Type.Builder(rs, e);
        tb.setX(height);
        tb.setY(height);
        tb.setFaces(true);
        tb.setMipmaps(mips == MipmapControl.MIPMAP_FULL);
        Type t = tb.create();
        int id = rs.nAllocationCubeCreateFromBitmap(t.getID(rs), mips.mID, b, usage);
        if (id == 0) {
            throw new RSRuntimeException("Load failed for bitmap " + b + " element " + e);
        }
        return new Allocation(id, rs, t, usage);
    }

    public static Allocation createCubemapFromBitmap(RenderScript rs, Bitmap b) {
        return createCubemapFromBitmap(rs, b, MipmapControl.MIPMAP_NONE, 2);
    }

    public static Allocation createCubemapFromCubeFaces(RenderScript rs, Bitmap xpos, Bitmap xneg, Bitmap ypos, Bitmap yneg, Bitmap zpos, Bitmap zneg, MipmapControl mips, int usage) {
        int height = xpos.getHeight();
        if (xpos.getWidth() != height || xneg.getWidth() != height || xneg.getHeight() != height || ypos.getWidth() != height || ypos.getHeight() != height || yneg.getWidth() != height || yneg.getHeight() != height || zpos.getWidth() != height || zpos.getHeight() != height || zneg.getWidth() != height || zneg.getHeight() != height) {
            throw new RSIllegalArgumentException("Only square cube map faces supported");
        }
        boolean isPow2 = (height & (height - 1)) == 0;
        if (!isPow2) {
            throw new RSIllegalArgumentException("Only power of 2 cube faces supported");
        }
        Element e = elementFromBitmap(rs, xpos);
        Type.Builder tb = new Type.Builder(rs, e);
        tb.setX(height);
        tb.setY(height);
        tb.setFaces(true);
        tb.setMipmaps(mips == MipmapControl.MIPMAP_FULL);
        Type t = tb.create();
        Allocation cubemap = createTyped(rs, t, mips, usage);
        AllocationAdapter adapter = AllocationAdapter.create2D(rs, cubemap);
        adapter.setFace(Type.CubemapFace.POSITIVE_X);
        adapter.copyFrom(xpos);
        adapter.setFace(Type.CubemapFace.NEGATIVE_X);
        adapter.copyFrom(xneg);
        adapter.setFace(Type.CubemapFace.POSITIVE_Y);
        adapter.copyFrom(ypos);
        adapter.setFace(Type.CubemapFace.NEGATIVE_Y);
        adapter.copyFrom(yneg);
        adapter.setFace(Type.CubemapFace.POSITIVE_Z);
        adapter.copyFrom(zpos);
        adapter.setFace(Type.CubemapFace.NEGATIVE_Z);
        adapter.copyFrom(zneg);
        return cubemap;
    }

    public static Allocation createCubemapFromCubeFaces(RenderScript rs, Bitmap xpos, Bitmap xneg, Bitmap ypos, Bitmap yneg, Bitmap zpos, Bitmap zneg) {
        return createCubemapFromCubeFaces(rs, xpos, xneg, ypos, yneg, zpos, zneg, MipmapControl.MIPMAP_NONE, 2);
    }

    public static Allocation createFromBitmapResource(RenderScript rs, Resources res, int id, MipmapControl mips, int usage) {
        rs.validate();
        if ((usage & 224) != 0) {
            throw new RSIllegalArgumentException("Unsupported usage specified.");
        }
        Bitmap b = BitmapFactory.decodeResource(res, id);
        Allocation alloc = createFromBitmap(rs, b, mips, usage);
        b.recycle();
        return alloc;
    }

    public static Allocation createFromBitmapResource(RenderScript rs, Resources res, int id) {
        if (rs.getApplicationContext().getApplicationInfo().targetSdkVersion >= 18) {
            return createFromBitmapResource(rs, res, id, MipmapControl.MIPMAP_NONE, 3);
        }
        return createFromBitmapResource(rs, res, id, MipmapControl.MIPMAP_NONE, 2);
    }

    public static Allocation createFromString(RenderScript rs, String str, int usage) {
        rs.validate();
        try {
            byte[] allocArray = str.getBytes("UTF-8");
            Allocation alloc = createSized(rs, Element.U8(rs), allocArray.length, usage);
            alloc.copyFrom(allocArray);
            return alloc;
        } catch (Exception e) {
            throw new RSRuntimeException("Could not convert string to utf-8.");
        }
    }

    public void setOnBufferAvailableListener(OnBufferAvailableListener callback) {
        synchronized (mAllocationMap) {
            mAllocationMap.put(new Integer(getID(this.mRS)), this);
            this.mBufferNotifier = callback;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void sendBufferNotification(int id) {
        synchronized (mAllocationMap) {
            Allocation a = mAllocationMap.get(new Integer(id));
            if (a != null && a.mBufferNotifier != null) {
                a.mBufferNotifier.onBufferAvailable(a);
            }
        }
    }
}