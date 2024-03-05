package java.security;

import java.io.IOException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: AlgorithmParameters.class */
public class AlgorithmParameters {
    protected AlgorithmParameters(AlgorithmParametersSpi algPramSpi, Provider provider, String algorithm) {
        throw new RuntimeException("Stub!");
    }

    public static AlgorithmParameters getInstance(String algorithm) throws NoSuchAlgorithmException {
        throw new RuntimeException("Stub!");
    }

    public static AlgorithmParameters getInstance(String algorithm, String provider) throws NoSuchAlgorithmException, NoSuchProviderException {
        throw new RuntimeException("Stub!");
    }

    public static AlgorithmParameters getInstance(String algorithm, Provider provider) throws NoSuchAlgorithmException {
        throw new RuntimeException("Stub!");
    }

    public final Provider getProvider() {
        throw new RuntimeException("Stub!");
    }

    public final String getAlgorithm() {
        throw new RuntimeException("Stub!");
    }

    public final void init(AlgorithmParameterSpec paramSpec) throws InvalidParameterSpecException {
        throw new RuntimeException("Stub!");
    }

    public final void init(byte[] params) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public final void init(byte[] params, String format) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public final <T extends AlgorithmParameterSpec> T getParameterSpec(Class<T> paramSpec) throws InvalidParameterSpecException {
        throw new RuntimeException("Stub!");
    }

    public final byte[] getEncoded() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public final byte[] getEncoded(String format) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public final String toString() {
        throw new RuntimeException("Stub!");
    }
}