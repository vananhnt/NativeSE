package android.renderscript;

import android.util.Log;
import android.widget.ExpandableListView;
import java.util.BitSet;

/* loaded from: FieldPacker.class */
public class FieldPacker {
    private final byte[] mData;
    private int mLen;
    private int mPos = 0;
    private BitSet mAlignment = new BitSet();

    public FieldPacker(int len) {
        this.mLen = len;
        this.mData = new byte[len];
    }

    public FieldPacker(byte[] data) {
        this.mLen = data.length;
        this.mData = data;
    }

    public void align(int v) {
        if (v <= 0 || (v & (v - 1)) != 0) {
            throw new RSIllegalArgumentException("argument must be a non-negative non-zero power of 2: " + v);
        }
        while ((this.mPos & (v - 1)) != 0) {
            this.mAlignment.flip(this.mPos);
            byte[] bArr = this.mData;
            int i = this.mPos;
            this.mPos = i + 1;
            bArr[i] = 0;
        }
    }

    public void subalign(int v) {
        if ((v & (v - 1)) != 0) {
            throw new RSIllegalArgumentException("argument must be a non-negative non-zero power of 2: " + v);
        }
        while ((this.mPos & (v - 1)) != 0) {
            this.mPos--;
        }
        if (this.mPos > 0) {
            while (this.mAlignment.get(this.mPos - 1)) {
                this.mPos--;
                this.mAlignment.flip(this.mPos);
            }
        }
    }

    public void reset() {
        this.mPos = 0;
    }

    public void reset(int i) {
        if (i < 0 || i >= this.mLen) {
            throw new RSIllegalArgumentException("out of range argument: " + i);
        }
        this.mPos = i;
    }

    public void skip(int i) {
        int res = this.mPos + i;
        if (res < 0 || res > this.mLen) {
            throw new RSIllegalArgumentException("out of range argument: " + i);
        }
        this.mPos = res;
    }

    public void addI8(byte v) {
        byte[] bArr = this.mData;
        int i = this.mPos;
        this.mPos = i + 1;
        bArr[i] = v;
    }

    public byte subI8() {
        subalign(1);
        byte[] bArr = this.mData;
        int i = this.mPos - 1;
        this.mPos = i;
        return bArr[i];
    }

    public void addI16(short v) {
        align(2);
        byte[] bArr = this.mData;
        int i = this.mPos;
        this.mPos = i + 1;
        bArr[i] = (byte) (v & 255);
        byte[] bArr2 = this.mData;
        int i2 = this.mPos;
        this.mPos = i2 + 1;
        bArr2[i2] = (byte) (v >> 8);
    }

    public short subI16() {
        subalign(2);
        byte[] bArr = this.mData;
        int i = this.mPos - 1;
        this.mPos = i;
        short v = (short) ((bArr[i] & 255) << 8);
        byte[] bArr2 = this.mData;
        int i2 = this.mPos - 1;
        this.mPos = i2;
        return (short) (v | ((short) (bArr2[i2] & 255)));
    }

    public void addI32(int v) {
        align(4);
        byte[] bArr = this.mData;
        int i = this.mPos;
        this.mPos = i + 1;
        bArr[i] = (byte) (v & 255);
        byte[] bArr2 = this.mData;
        int i2 = this.mPos;
        this.mPos = i2 + 1;
        bArr2[i2] = (byte) ((v >> 8) & 255);
        byte[] bArr3 = this.mData;
        int i3 = this.mPos;
        this.mPos = i3 + 1;
        bArr3[i3] = (byte) ((v >> 16) & 255);
        byte[] bArr4 = this.mData;
        int i4 = this.mPos;
        this.mPos = i4 + 1;
        bArr4[i4] = (byte) ((v >> 24) & 255);
    }

    public int subI32() {
        subalign(4);
        byte[] bArr = this.mData;
        int i = this.mPos - 1;
        this.mPos = i;
        int v = (bArr[i] & 255) << 24;
        byte[] bArr2 = this.mData;
        int i2 = this.mPos - 1;
        this.mPos = i2;
        int v2 = v | ((bArr2[i2] & 255) << 16);
        byte[] bArr3 = this.mData;
        int i3 = this.mPos - 1;
        this.mPos = i3;
        int v3 = v2 | ((bArr3[i3] & 255) << 8);
        byte[] bArr4 = this.mData;
        int i4 = this.mPos - 1;
        this.mPos = i4;
        return v3 | (bArr4[i4] & 255);
    }

    public void addI64(long v) {
        align(8);
        byte[] bArr = this.mData;
        int i = this.mPos;
        this.mPos = i + 1;
        bArr[i] = (byte) (v & 255);
        byte[] bArr2 = this.mData;
        int i2 = this.mPos;
        this.mPos = i2 + 1;
        bArr2[i2] = (byte) ((v >> 8) & 255);
        byte[] bArr3 = this.mData;
        int i3 = this.mPos;
        this.mPos = i3 + 1;
        bArr3[i3] = (byte) ((v >> 16) & 255);
        byte[] bArr4 = this.mData;
        int i4 = this.mPos;
        this.mPos = i4 + 1;
        bArr4[i4] = (byte) ((v >> 24) & 255);
        byte[] bArr5 = this.mData;
        int i5 = this.mPos;
        this.mPos = i5 + 1;
        bArr5[i5] = (byte) ((v >> 32) & 255);
        byte[] bArr6 = this.mData;
        int i6 = this.mPos;
        this.mPos = i6 + 1;
        bArr6[i6] = (byte) ((v >> 40) & 255);
        byte[] bArr7 = this.mData;
        int i7 = this.mPos;
        this.mPos = i7 + 1;
        bArr7[i7] = (byte) ((v >> 48) & 255);
        byte[] bArr8 = this.mData;
        int i8 = this.mPos;
        this.mPos = i8 + 1;
        bArr8[i8] = (byte) ((v >> 56) & 255);
    }

    public long subI64() {
        subalign(8);
        byte[] bArr = this.mData;
        int i = this.mPos - 1;
        this.mPos = i;
        byte x = bArr[i];
        long v = 0 | ((x & 255) << 56);
        byte[] bArr2 = this.mData;
        int i2 = this.mPos - 1;
        this.mPos = i2;
        byte x2 = bArr2[i2];
        long v2 = v | ((x2 & 255) << 48);
        byte[] bArr3 = this.mData;
        int i3 = this.mPos - 1;
        this.mPos = i3;
        byte x3 = bArr3[i3];
        long v3 = v2 | ((x3 & 255) << 40);
        byte[] bArr4 = this.mData;
        int i4 = this.mPos - 1;
        this.mPos = i4;
        byte x4 = bArr4[i4];
        long v4 = v3 | ((x4 & 255) << 32);
        byte[] bArr5 = this.mData;
        int i5 = this.mPos - 1;
        this.mPos = i5;
        byte x5 = bArr5[i5];
        long v5 = v4 | ((x5 & 255) << 24);
        byte[] bArr6 = this.mData;
        int i6 = this.mPos - 1;
        this.mPos = i6;
        byte x6 = bArr6[i6];
        long v6 = v5 | ((x6 & 255) << 16);
        byte[] bArr7 = this.mData;
        int i7 = this.mPos - 1;
        this.mPos = i7;
        byte x7 = bArr7[i7];
        long v7 = v6 | ((x7 & 255) << 8);
        byte[] bArr8 = this.mData;
        int i8 = this.mPos - 1;
        this.mPos = i8;
        byte x8 = bArr8[i8];
        return v7 | (x8 & 255);
    }

    public void addU8(short v) {
        if (v < 0 || v > 255) {
            Log.e("rs", "FieldPacker.addU8( " + ((int) v) + " )");
            throw new IllegalArgumentException("Saving value out of range for type");
        }
        byte[] bArr = this.mData;
        int i = this.mPos;
        this.mPos = i + 1;
        bArr[i] = (byte) v;
    }

    public void addU16(int v) {
        if (v < 0 || v > 65535) {
            Log.e("rs", "FieldPacker.addU16( " + v + " )");
            throw new IllegalArgumentException("Saving value out of range for type");
        }
        align(2);
        byte[] bArr = this.mData;
        int i = this.mPos;
        this.mPos = i + 1;
        bArr[i] = (byte) (v & 255);
        byte[] bArr2 = this.mData;
        int i2 = this.mPos;
        this.mPos = i2 + 1;
        bArr2[i2] = (byte) (v >> 8);
    }

    public void addU32(long v) {
        if (v < 0 || v > ExpandableListView.PACKED_POSITION_VALUE_NULL) {
            Log.e("rs", "FieldPacker.addU32( " + v + " )");
            throw new IllegalArgumentException("Saving value out of range for type");
        }
        align(4);
        byte[] bArr = this.mData;
        int i = this.mPos;
        this.mPos = i + 1;
        bArr[i] = (byte) (v & 255);
        byte[] bArr2 = this.mData;
        int i2 = this.mPos;
        this.mPos = i2 + 1;
        bArr2[i2] = (byte) ((v >> 8) & 255);
        byte[] bArr3 = this.mData;
        int i3 = this.mPos;
        this.mPos = i3 + 1;
        bArr3[i3] = (byte) ((v >> 16) & 255);
        byte[] bArr4 = this.mData;
        int i4 = this.mPos;
        this.mPos = i4 + 1;
        bArr4[i4] = (byte) ((v >> 24) & 255);
    }

    public void addU64(long v) {
        if (v < 0) {
            Log.e("rs", "FieldPacker.addU64( " + v + " )");
            throw new IllegalArgumentException("Saving value out of range for type");
        }
        align(8);
        byte[] bArr = this.mData;
        int i = this.mPos;
        this.mPos = i + 1;
        bArr[i] = (byte) (v & 255);
        byte[] bArr2 = this.mData;
        int i2 = this.mPos;
        this.mPos = i2 + 1;
        bArr2[i2] = (byte) ((v >> 8) & 255);
        byte[] bArr3 = this.mData;
        int i3 = this.mPos;
        this.mPos = i3 + 1;
        bArr3[i3] = (byte) ((v >> 16) & 255);
        byte[] bArr4 = this.mData;
        int i4 = this.mPos;
        this.mPos = i4 + 1;
        bArr4[i4] = (byte) ((v >> 24) & 255);
        byte[] bArr5 = this.mData;
        int i5 = this.mPos;
        this.mPos = i5 + 1;
        bArr5[i5] = (byte) ((v >> 32) & 255);
        byte[] bArr6 = this.mData;
        int i6 = this.mPos;
        this.mPos = i6 + 1;
        bArr6[i6] = (byte) ((v >> 40) & 255);
        byte[] bArr7 = this.mData;
        int i7 = this.mPos;
        this.mPos = i7 + 1;
        bArr7[i7] = (byte) ((v >> 48) & 255);
        byte[] bArr8 = this.mData;
        int i8 = this.mPos;
        this.mPos = i8 + 1;
        bArr8[i8] = (byte) ((v >> 56) & 255);
    }

    public void addF32(float v) {
        addI32(Float.floatToRawIntBits(v));
    }

    public float subF32() {
        return Float.intBitsToFloat(subI32());
    }

    public void addF64(double v) {
        addI64(Double.doubleToRawLongBits(v));
    }

    public double subF64() {
        return Double.longBitsToDouble(subI64());
    }

    public void addObj(BaseObj obj) {
        if (obj != null) {
            addI32(obj.getID(null));
        } else {
            addI32(0);
        }
    }

    public void addF32(Float2 v) {
        addF32(v.x);
        addF32(v.y);
    }

    public void addF32(Float3 v) {
        addF32(v.x);
        addF32(v.y);
        addF32(v.z);
    }

    public void addF32(Float4 v) {
        addF32(v.x);
        addF32(v.y);
        addF32(v.z);
        addF32(v.w);
    }

    public void addF64(Double2 v) {
        addF64(v.x);
        addF64(v.y);
    }

    public void addF64(Double3 v) {
        addF64(v.x);
        addF64(v.y);
        addF64(v.z);
    }

    public void addF64(Double4 v) {
        addF64(v.x);
        addF64(v.y);
        addF64(v.z);
        addF64(v.w);
    }

    public void addI8(Byte2 v) {
        addI8(v.x);
        addI8(v.y);
    }

    public void addI8(Byte3 v) {
        addI8(v.x);
        addI8(v.y);
        addI8(v.z);
    }

    public void addI8(Byte4 v) {
        addI8(v.x);
        addI8(v.y);
        addI8(v.z);
        addI8(v.w);
    }

    public void addU8(Short2 v) {
        addU8(v.x);
        addU8(v.y);
    }

    public void addU8(Short3 v) {
        addU8(v.x);
        addU8(v.y);
        addU8(v.z);
    }

    public void addU8(Short4 v) {
        addU8(v.x);
        addU8(v.y);
        addU8(v.z);
        addU8(v.w);
    }

    public void addI16(Short2 v) {
        addI16(v.x);
        addI16(v.y);
    }

    public void addI16(Short3 v) {
        addI16(v.x);
        addI16(v.y);
        addI16(v.z);
    }

    public void addI16(Short4 v) {
        addI16(v.x);
        addI16(v.y);
        addI16(v.z);
        addI16(v.w);
    }

    public void addU16(Int2 v) {
        addU16(v.x);
        addU16(v.y);
    }

    public void addU16(Int3 v) {
        addU16(v.x);
        addU16(v.y);
        addU16(v.z);
    }

    public void addU16(Int4 v) {
        addU16(v.x);
        addU16(v.y);
        addU16(v.z);
        addU16(v.w);
    }

    public void addI32(Int2 v) {
        addI32(v.x);
        addI32(v.y);
    }

    public void addI32(Int3 v) {
        addI32(v.x);
        addI32(v.y);
        addI32(v.z);
    }

    public void addI32(Int4 v) {
        addI32(v.x);
        addI32(v.y);
        addI32(v.z);
        addI32(v.w);
    }

    public void addU32(Long2 v) {
        addU32(v.x);
        addU32(v.y);
    }

    public void addU32(Long3 v) {
        addU32(v.x);
        addU32(v.y);
        addU32(v.z);
    }

    public void addU32(Long4 v) {
        addU32(v.x);
        addU32(v.y);
        addU32(v.z);
        addU32(v.w);
    }

    public void addI64(Long2 v) {
        addI64(v.x);
        addI64(v.y);
    }

    public void addI64(Long3 v) {
        addI64(v.x);
        addI64(v.y);
        addI64(v.z);
    }

    public void addI64(Long4 v) {
        addI64(v.x);
        addI64(v.y);
        addI64(v.z);
        addI64(v.w);
    }

    public void addU64(Long2 v) {
        addU64(v.x);
        addU64(v.y);
    }

    public void addU64(Long3 v) {
        addU64(v.x);
        addU64(v.y);
        addU64(v.z);
    }

    public void addU64(Long4 v) {
        addU64(v.x);
        addU64(v.y);
        addU64(v.z);
        addU64(v.w);
    }

    public Float2 subFloat2() {
        Float2 v = new Float2();
        v.y = subF32();
        v.x = subF32();
        return v;
    }

    public Float3 subFloat3() {
        Float3 v = new Float3();
        v.z = subF32();
        v.y = subF32();
        v.x = subF32();
        return v;
    }

    public Float4 subFloat4() {
        Float4 v = new Float4();
        v.w = subF32();
        v.z = subF32();
        v.y = subF32();
        v.x = subF32();
        return v;
    }

    public Double2 subDouble2() {
        Double2 v = new Double2();
        v.y = subF64();
        v.x = subF64();
        return v;
    }

    public Double3 subDouble3() {
        Double3 v = new Double3();
        v.z = subF64();
        v.y = subF64();
        v.x = subF64();
        return v;
    }

    public Double4 subDouble4() {
        Double4 v = new Double4();
        v.w = subF64();
        v.z = subF64();
        v.y = subF64();
        v.x = subF64();
        return v;
    }

    public Byte2 subByte2() {
        Byte2 v = new Byte2();
        v.y = subI8();
        v.x = subI8();
        return v;
    }

    public Byte3 subByte3() {
        Byte3 v = new Byte3();
        v.z = subI8();
        v.y = subI8();
        v.x = subI8();
        return v;
    }

    public Byte4 subByte4() {
        Byte4 v = new Byte4();
        v.w = subI8();
        v.z = subI8();
        v.y = subI8();
        v.x = subI8();
        return v;
    }

    public Short2 subShort2() {
        Short2 v = new Short2();
        v.y = subI16();
        v.x = subI16();
        return v;
    }

    public Short3 subShort3() {
        Short3 v = new Short3();
        v.z = subI16();
        v.y = subI16();
        v.x = subI16();
        return v;
    }

    public Short4 subShort4() {
        Short4 v = new Short4();
        v.w = subI16();
        v.z = subI16();
        v.y = subI16();
        v.x = subI16();
        return v;
    }

    public Int2 subInt2() {
        Int2 v = new Int2();
        v.y = subI32();
        v.x = subI32();
        return v;
    }

    public Int3 subInt3() {
        Int3 v = new Int3();
        v.z = subI32();
        v.y = subI32();
        v.x = subI32();
        return v;
    }

    public Int4 subInt4() {
        Int4 v = new Int4();
        v.w = subI32();
        v.z = subI32();
        v.y = subI32();
        v.x = subI32();
        return v;
    }

    public Long2 subLong2() {
        Long2 v = new Long2();
        v.y = subI64();
        v.x = subI64();
        return v;
    }

    public Long3 subLong3() {
        Long3 v = new Long3();
        v.z = subI64();
        v.y = subI64();
        v.x = subI64();
        return v;
    }

    public Long4 subLong4() {
        Long4 v = new Long4();
        v.w = subI64();
        v.z = subI64();
        v.y = subI64();
        v.x = subI64();
        return v;
    }

    public void addMatrix(Matrix4f v) {
        for (int i = 0; i < v.mMat.length; i++) {
            addF32(v.mMat[i]);
        }
    }

    public Matrix4f subMatrix4f() {
        Matrix4f v = new Matrix4f();
        for (int i = v.mMat.length - 1; i >= 0; i--) {
            v.mMat[i] = subF32();
        }
        return v;
    }

    public void addMatrix(Matrix3f v) {
        for (int i = 0; i < v.mMat.length; i++) {
            addF32(v.mMat[i]);
        }
    }

    public Matrix3f subMatrix3f() {
        Matrix3f v = new Matrix3f();
        for (int i = v.mMat.length - 1; i >= 0; i--) {
            v.mMat[i] = subF32();
        }
        return v;
    }

    public void addMatrix(Matrix2f v) {
        for (int i = 0; i < v.mMat.length; i++) {
            addF32(v.mMat[i]);
        }
    }

    public Matrix2f subMatrix2f() {
        Matrix2f v = new Matrix2f();
        for (int i = v.mMat.length - 1; i >= 0; i--) {
            v.mMat[i] = subF32();
        }
        return v;
    }

    public void addBoolean(boolean v) {
        addI8((byte) (v ? 1 : 0));
    }

    public boolean subBoolean() {
        byte v = subI8();
        if (v == 1) {
            return true;
        }
        return false;
    }

    public final byte[] getData() {
        return this.mData;
    }
}