package java.security.spec;

import java.math.BigInteger;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ECParameterSpec.class */
public class ECParameterSpec implements AlgorithmParameterSpec {
    public ECParameterSpec(EllipticCurve curve, ECPoint generator, BigInteger order, int cofactor) {
        throw new RuntimeException("Stub!");
    }

    public int getCofactor() {
        throw new RuntimeException("Stub!");
    }

    public EllipticCurve getCurve() {
        throw new RuntimeException("Stub!");
    }

    public ECPoint getGenerator() {
        throw new RuntimeException("Stub!");
    }

    public BigInteger getOrder() {
        throw new RuntimeException("Stub!");
    }
}