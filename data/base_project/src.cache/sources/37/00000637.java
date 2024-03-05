package android.hardware.camera2.impl;

import android.graphics.Rect;
import java.nio.ByteBuffer;

/* loaded from: MetadataMarshalRect.class */
public class MetadataMarshalRect implements MetadataMarshalClass<Rect> {
    private static final int SIZE = 16;

    @Override // android.hardware.camera2.impl.MetadataMarshalClass
    public int marshal(Rect value, ByteBuffer buffer, int nativeType, boolean sizeOnly) {
        if (sizeOnly) {
            return 16;
        }
        buffer.putInt(value.left);
        buffer.putInt(value.top);
        buffer.putInt(value.width());
        buffer.putInt(value.height());
        return 16;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // android.hardware.camera2.impl.MetadataMarshalClass
    public Rect unmarshal(ByteBuffer buffer, int nativeType) {
        int left = buffer.getInt();
        int top = buffer.getInt();
        int width = buffer.getInt();
        int height = buffer.getInt();
        int right = left + width;
        int bottom = top + height;
        return new Rect(left, top, right, bottom);
    }

    @Override // android.hardware.camera2.impl.MetadataMarshalClass
    public Class<Rect> getMarshalingClass() {
        return Rect.class;
    }

    @Override // android.hardware.camera2.impl.MetadataMarshalClass
    public boolean isNativeTypeSupported(int nativeType) {
        return nativeType == 1;
    }

    @Override // android.hardware.camera2.impl.MetadataMarshalClass
    public int getNativeSize(int nativeType) {
        return 16;
    }
}