package android.renderscript;

import android.graphics.ImageFormat;

/* loaded from: Type.class */
public class Type extends BaseObj {
    int mDimX;
    int mDimY;
    int mDimZ;
    boolean mDimMipmaps;
    boolean mDimFaces;
    int mDimYuv;
    int mElementCount;
    Element mElement;

    /* loaded from: Type$CubemapFace.class */
    public enum CubemapFace {
        POSITIVE_X(0),
        NEGATIVE_X(1),
        POSITIVE_Y(2),
        NEGATIVE_Y(3),
        POSITIVE_Z(4),
        NEGATIVE_Z(5),
        POSITVE_X(0),
        POSITVE_Y(2),
        POSITVE_Z(4);
        
        int mID;

        CubemapFace(int id) {
            this.mID = id;
        }
    }

    public Element getElement() {
        return this.mElement;
    }

    public int getX() {
        return this.mDimX;
    }

    public int getY() {
        return this.mDimY;
    }

    public int getZ() {
        return this.mDimZ;
    }

    public int getYuv() {
        return this.mDimYuv;
    }

    public boolean hasMipmaps() {
        return this.mDimMipmaps;
    }

    public boolean hasFaces() {
        return this.mDimFaces;
    }

    public int getCount() {
        return this.mElementCount;
    }

    void calcElementCount() {
        int count;
        boolean hasLod = hasMipmaps();
        int x = getX();
        int y = getY();
        int z = getZ();
        int faces = 1;
        if (hasFaces()) {
            faces = 6;
        }
        if (x == 0) {
            x = 1;
        }
        if (y == 0) {
            y = 1;
        }
        if (z == 0) {
            z = 1;
        }
        int i = x * y * z * faces;
        while (true) {
            count = i;
            if (!hasLod || (x <= 1 && y <= 1 && z <= 1)) {
                break;
            }
            if (x > 1) {
                x >>= 1;
            }
            if (y > 1) {
                y >>= 1;
            }
            if (z > 1) {
                z >>= 1;
            }
            i = count + (x * y * z * faces);
        }
        this.mElementCount = count;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Type(int id, RenderScript rs) {
        super(id, rs);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.renderscript.BaseObj
    public void updateFromNative() {
        int[] dataBuffer = new int[6];
        this.mRS.nTypeGetNativeData(getID(this.mRS), dataBuffer);
        this.mDimX = dataBuffer[0];
        this.mDimY = dataBuffer[1];
        this.mDimZ = dataBuffer[2];
        this.mDimMipmaps = dataBuffer[3] == 1;
        this.mDimFaces = dataBuffer[4] == 1;
        int elementID = dataBuffer[5];
        if (elementID != 0) {
            this.mElement = new Element(elementID, this.mRS);
            this.mElement.updateFromNative();
        }
        calcElementCount();
    }

    /* loaded from: Type$Builder.class */
    public static class Builder {
        RenderScript mRS;
        int mDimX = 1;
        int mDimY;
        int mDimZ;
        boolean mDimMipmaps;
        boolean mDimFaces;
        int mYuv;
        Element mElement;

        public Builder(RenderScript rs, Element e) {
            e.checkValid();
            this.mRS = rs;
            this.mElement = e;
        }

        public Builder setX(int value) {
            if (value < 1) {
                throw new RSIllegalArgumentException("Values of less than 1 for Dimension X are not valid.");
            }
            this.mDimX = value;
            return this;
        }

        public Builder setY(int value) {
            if (value < 1) {
                throw new RSIllegalArgumentException("Values of less than 1 for Dimension Y are not valid.");
            }
            this.mDimY = value;
            return this;
        }

        public Builder setZ(int value) {
            if (value < 1) {
                throw new RSIllegalArgumentException("Values of less than 1 for Dimension Z are not valid.");
            }
            this.mDimZ = value;
            return this;
        }

        public Builder setMipmaps(boolean value) {
            this.mDimMipmaps = value;
            return this;
        }

        public Builder setFaces(boolean value) {
            this.mDimFaces = value;
            return this;
        }

        public Builder setYuvFormat(int yuvFormat) {
            switch (yuvFormat) {
                case 17:
                case 35:
                case ImageFormat.YV12 /* 842094169 */:
                    this.mYuv = yuvFormat;
                    return this;
                default:
                    throw new RSIllegalArgumentException("Only ImageFormat.NV21, .YV12, and .YUV_420_888 are supported..");
            }
        }

        public Type create() {
            if (this.mDimZ > 0) {
                if (this.mDimX < 1 || this.mDimY < 1) {
                    throw new RSInvalidStateException("Both X and Y dimension required when Z is present.");
                }
                if (this.mDimFaces) {
                    throw new RSInvalidStateException("Cube maps not supported with 3D types.");
                }
            }
            if (this.mDimY > 0 && this.mDimX < 1) {
                throw new RSInvalidStateException("X dimension required when Y is present.");
            }
            if (this.mDimFaces && this.mDimY < 1) {
                throw new RSInvalidStateException("Cube maps require 2D Types.");
            }
            if (this.mYuv != 0 && (this.mDimZ != 0 || this.mDimFaces || this.mDimMipmaps)) {
                throw new RSInvalidStateException("YUV only supports basic 2D.");
            }
            int id = this.mRS.nTypeCreate(this.mElement.getID(this.mRS), this.mDimX, this.mDimY, this.mDimZ, this.mDimMipmaps, this.mDimFaces, this.mYuv);
            Type t = new Type(id, this.mRS);
            t.mElement = this.mElement;
            t.mDimX = this.mDimX;
            t.mDimY = this.mDimY;
            t.mDimZ = this.mDimZ;
            t.mDimMipmaps = this.mDimMipmaps;
            t.mDimFaces = this.mDimFaces;
            t.mDimYuv = this.mYuv;
            t.calcElementCount();
            return t;
        }
    }
}