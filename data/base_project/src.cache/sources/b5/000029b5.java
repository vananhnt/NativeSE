package org.apache.harmony.security.provider.crypto;

import java.io.IOException;
import java.io.NotActiveException;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPrivateKey;
import java.security.spec.DSAParameterSpec;
import java.security.spec.DSAPrivateKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import org.apache.harmony.security.PrivateKeyImpl;
import org.apache.harmony.security.asn1.ASN1Integer;
import org.apache.harmony.security.pkcs8.PrivateKeyInfo;
import org.apache.harmony.security.utils.AlgNameMapper;
import org.apache.harmony.security.x509.AlgorithmIdentifier;

/* loaded from: DSAPrivateKeyImpl.class */
public class DSAPrivateKeyImpl extends PrivateKeyImpl implements DSAPrivateKey {
    private static final long serialVersionUID = -4716227614104950081L;
    private BigInteger x;
    private BigInteger g;
    private BigInteger p;
    private BigInteger q;
    private transient DSAParams params;

    public DSAPrivateKeyImpl(DSAPrivateKeySpec keySpec) {
        super("DSA");
        this.g = keySpec.getG();
        this.p = keySpec.getP();
        this.q = keySpec.getQ();
        ThreeIntegerSequence threeInts = new ThreeIntegerSequence(this.p.toByteArray(), this.q.toByteArray(), this.g.toByteArray());
        AlgorithmIdentifier ai = new AlgorithmIdentifier(AlgNameMapper.map2OID("DSA"), threeInts.getEncoded());
        this.x = keySpec.getX();
        PrivateKeyInfo pki = new PrivateKeyInfo(0, ai, ASN1Integer.getInstance().encode(this.x.toByteArray()), null);
        setEncoding(pki.getEncoded());
        this.params = new DSAParameterSpec(this.p, this.q, this.g);
    }

    public DSAPrivateKeyImpl(PKCS8EncodedKeySpec keySpec) throws InvalidKeySpecException {
        super("DSA");
        byte[] encoding = keySpec.getEncoded();
        try {
            PrivateKeyInfo privateKeyInfo = (PrivateKeyInfo) PrivateKeyInfo.ASN1.decode(encoding);
            try {
                this.x = new BigInteger((byte[]) ASN1Integer.getInstance().decode(privateKeyInfo.getPrivateKey()));
                AlgorithmIdentifier ai = privateKeyInfo.getAlgorithmIdentifier();
                try {
                    ThreeIntegerSequence threeInts = (ThreeIntegerSequence) ThreeIntegerSequence.ASN1.decode(ai.getParameters());
                    this.p = new BigInteger(threeInts.p);
                    this.q = new BigInteger(threeInts.q);
                    this.g = new BigInteger(threeInts.g);
                    this.params = new DSAParameterSpec(this.p, this.q, this.g);
                    setEncoding(encoding);
                    String alg = ai.getAlgorithm();
                    String algName = AlgNameMapper.map2AlgName(alg);
                    setAlgorithm(algName == null ? alg : algName);
                } catch (IOException e) {
                    throw new InvalidKeySpecException("Failed to decode parameters: " + e);
                }
            } catch (IOException e2) {
                throw new InvalidKeySpecException("Failed to decode parameters: " + e2);
            }
        } catch (IOException e3) {
            throw new InvalidKeySpecException("Failed to decode keySpec encoding: " + e3);
        }
    }

    @Override // java.security.interfaces.DSAPrivateKey
    public BigInteger getX() {
        return this.x;
    }

    @Override // java.security.interfaces.DSAKey
    public DSAParams getParams() {
        return this.params;
    }

    private void readObject(ObjectInputStream in) throws NotActiveException, IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.params = new DSAParameterSpec(this.p, this.q, this.g);
    }
}