package android.hardware.camera2.impl;

import java.nio.ByteBuffer;

/* loaded from: MetadataMarshalClass.class */
public interface MetadataMarshalClass<T> {
    public static final int NATIVE_SIZE_DYNAMIC = -1;

    int marshal(T t, ByteBuffer byteBuffer, int i, boolean z);

    T unmarshal(ByteBuffer byteBuffer, int i);

    Class<T> getMarshalingClass();

    boolean isNativeTypeSupported(int i);

    int getNativeSize(int i);
}