package java.security.spec;

import java.math.BigInteger;
import java.security.interfaces.DSAParams;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: DSAParameterSpec.class */
public class DSAParameterSpec implements AlgorithmParameterSpec, DSAParams {
    public DSAParameterSpec(BigInteger p, BigInteger q, BigInteger g) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.security.interfaces.DSAParams
    public BigInteger getG() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.security.interfaces.DSAParams
    public BigInteger getP() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.security.interfaces.DSAParams
    public BigInteger getQ() {
        throw new RuntimeException("Stub!");
    }
}