package javax.crypto.spec;

import java.security.spec.AlgorithmParameterSpec;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: DHGenParameterSpec.class */
public class DHGenParameterSpec implements AlgorithmParameterSpec {
    private final int primeSize;
    private final int exponentSize;

    public DHGenParameterSpec(int primeSize, int exponentSize) {
        this.primeSize = primeSize;
        this.exponentSize = exponentSize;
    }

    public int getPrimeSize() {
        return this.primeSize;
    }

    public int getExponentSize() {
        return this.exponentSize;
    }
}