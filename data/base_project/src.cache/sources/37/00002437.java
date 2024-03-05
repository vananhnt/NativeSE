package java.security;

import java.io.IOException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: AlgorithmParametersSpi.class */
public abstract class AlgorithmParametersSpi {
    protected abstract void engineInit(AlgorithmParameterSpec algorithmParameterSpec) throws InvalidParameterSpecException;

    protected abstract void engineInit(byte[] bArr) throws IOException;

    protected abstract void engineInit(byte[] bArr, String str) throws IOException;

    protected abstract <T extends AlgorithmParameterSpec> T engineGetParameterSpec(Class<T> cls) throws InvalidParameterSpecException;

    protected abstract byte[] engineGetEncoded() throws IOException;

    protected abstract byte[] engineGetEncoded(String str) throws IOException;

    protected abstract String engineToString();

    public AlgorithmParametersSpi() {
        throw new RuntimeException("Stub!");
    }
}