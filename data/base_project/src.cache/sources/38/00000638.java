package android.hardware.camera2.impl;

import android.hardware.camera2.Size;
import java.nio.ByteBuffer;

/* loaded from: MetadataMarshalSize.class */
public class MetadataMarshalSize implements MetadataMarshalClass<Size> {
    private static final int SIZE = 8;

    @Override // android.hardware.camera2.impl.MetadataMarshalClass
    public int marshal(Size value, ByteBuffer buffer, int nativeType, boolean sizeOnly) {
        if (sizeOnly) {
            return 8;
        }
        buffer.putInt(value.getWidth());
        buffer.putInt(value.getHeight());
        return 8;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // android.hardware.camera2.impl.MetadataMarshalClass
    public Size unmarshal(ByteBuffer buffer, int nativeType) {
        int width = buffer.getInt();
        int height = buffer.getInt();
        return new Size(width, height);
    }

    @Override // android.hardware.camera2.impl.MetadataMarshalClass
    public Class<Size> getMarshalingClass() {
        return Size.class;
    }

    @Override // android.hardware.camera2.impl.MetadataMarshalClass
    public boolean isNativeTypeSupported(int nativeType) {
        return nativeType == 1;
    }

    @Override // android.hardware.camera2.impl.MetadataMarshalClass
    public int getNativeSize(int nativeType) {
        return 8;
    }
}