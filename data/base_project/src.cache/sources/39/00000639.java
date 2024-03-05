package android.hardware.camera2.impl;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/* loaded from: MetadataMarshalString.class */
public class MetadataMarshalString implements MetadataMarshalClass<String> {
    private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

    @Override // android.hardware.camera2.impl.MetadataMarshalClass
    public int marshal(String value, ByteBuffer buffer, int nativeType, boolean sizeOnly) {
        byte[] arr = value.getBytes(UTF8_CHARSET);
        if (!sizeOnly) {
            buffer.put(arr);
            buffer.put((byte) 0);
        }
        return arr.length + 1;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // android.hardware.camera2.impl.MetadataMarshalClass
    public String unmarshal(ByteBuffer buffer, int nativeType) {
        buffer.mark();
        boolean foundNull = false;
        int stringLength = 0;
        while (true) {
            if (!buffer.hasRemaining()) {
                break;
            } else if (buffer.get() == 0) {
                foundNull = true;
                break;
            } else {
                stringLength++;
            }
        }
        if (!foundNull) {
            throw new IllegalArgumentException("Strings must be null-terminated");
        }
        buffer.reset();
        byte[] strBytes = new byte[stringLength + 1];
        buffer.get(strBytes, 0, stringLength + 1);
        return new String(strBytes, 0, stringLength, UTF8_CHARSET);
    }

    @Override // android.hardware.camera2.impl.MetadataMarshalClass
    public Class<String> getMarshalingClass() {
        return String.class;
    }

    @Override // android.hardware.camera2.impl.MetadataMarshalClass
    public boolean isNativeTypeSupported(int nativeType) {
        return nativeType == 0;
    }

    @Override // android.hardware.camera2.impl.MetadataMarshalClass
    public int getNativeSize(int nativeType) {
        return -1;
    }
}