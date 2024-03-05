package javax.crypto.spec;

import java.math.BigInteger;
import java.security.spec.KeySpec;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: DHPrivateKeySpec.class */
public class DHPrivateKeySpec implements KeySpec {
    private final BigInteger x;
    private final BigInteger p;
    private final BigInteger g;

    public DHPrivateKeySpec(BigInteger x, BigInteger p, BigInteger g) {
        this.x = x;
        this.p = p;
        this.g = g;
    }

    public BigInteger getX() {
        return this.x;
    }

    public BigInteger getP() {
        return this.p;
    }

    public BigInteger getG() {
        return this.g;
    }
}