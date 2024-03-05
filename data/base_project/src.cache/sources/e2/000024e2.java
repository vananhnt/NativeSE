package java.security.spec;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: PSSParameterSpec.class */
public class PSSParameterSpec implements AlgorithmParameterSpec {
    public static final PSSParameterSpec DEFAULT = null;

    public PSSParameterSpec(int saltLen) {
        throw new RuntimeException("Stub!");
    }

    public PSSParameterSpec(String mdName, String mgfName, AlgorithmParameterSpec mgfSpec, int saltLen, int trailerField) {
        throw new RuntimeException("Stub!");
    }

    public int getSaltLength() {
        throw new RuntimeException("Stub!");
    }

    public String getDigestAlgorithm() {
        throw new RuntimeException("Stub!");
    }

    public String getMGFAlgorithm() {
        throw new RuntimeException("Stub!");
    }

    public AlgorithmParameterSpec getMGFParameters() {
        throw new RuntimeException("Stub!");
    }

    public int getTrailerField() {
        throw new RuntimeException("Stub!");
    }
}