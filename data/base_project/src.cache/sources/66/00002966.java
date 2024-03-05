package org.apache.harmony.security;

import java.security.PublicKey;

/* loaded from: PublicKeyImpl.class */
public class PublicKeyImpl implements PublicKey {
    private static final long serialVersionUID = 7179022516819534075L;
    private byte[] encoding;
    private String algorithm;

    public PublicKeyImpl(String algorithm) {
        this.algorithm = algorithm;
    }

    @Override // java.security.Key
    public String getAlgorithm() {
        return this.algorithm;
    }

    @Override // java.security.Key
    public String getFormat() {
        return "X.509";
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        byte[] result = new byte[this.encoding.length];
        System.arraycopy(this.encoding, 0, result, 0, this.encoding.length);
        return result;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public void setEncoding(byte[] encoding) {
        this.encoding = new byte[encoding.length];
        System.arraycopy(encoding, 0, this.encoding, 0, encoding.length);
    }
}