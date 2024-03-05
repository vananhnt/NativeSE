package org.apache.harmony.security.provider.crypto;

import java.io.IOException;
import java.io.NotActiveException;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.DSAParameterSpec;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import org.apache.harmony.security.PublicKeyImpl;
import org.apache.harmony.security.asn1.ASN1Integer;
import org.apache.harmony.security.utils.AlgNameMapper;
import org.apache.harmony.security.x509.AlgorithmIdentifier;
import org.apache.harmony.security.x509.SubjectPublicKeyInfo;

/* loaded from: DSAPublicKeyImpl.class */
public class DSAPublicKeyImpl extends PublicKeyImpl implements DSAPublicKey {
    private static final long serialVersionUID = -2279672131310978336L;
    private BigInteger y;
    private BigInteger g;
    private BigInteger p;
    private BigInteger q;
    private transient DSAParams params;

    public DSAPublicKeyImpl(DSAPublicKeySpec keySpec) {
        super("DSA");
        this.p = keySpec.getP();
        this.q = keySpec.getQ();
        this.g = keySpec.getG();
        ThreeIntegerSequence threeInts = new ThreeIntegerSequence(this.p.toByteArray(), this.q.toByteArray(), this.g.toByteArray());
        AlgorithmIdentifier ai = new AlgorithmIdentifier(AlgNameMapper.map2OID("DSA"), threeInts.getEncoded());
        this.y = keySpec.getY();
        SubjectPublicKeyInfo spki = new SubjectPublicKeyInfo(ai, ASN1Integer.getInstance().encode(this.y.toByteArray()));
        setEncoding(spki.getEncoded());
        this.params = new DSAParameterSpec(this.p, this.q, this.g);
    }

    public DSAPublicKeyImpl(X509EncodedKeySpec keySpec) throws InvalidKeySpecException {
        super("DSA");
        byte[] encoding = keySpec.getEncoded();
        try {
            SubjectPublicKeyInfo subjectPublicKeyInfo = (SubjectPublicKeyInfo) SubjectPublicKeyInfo.ASN1.decode(encoding);
            try {
                this.y = new BigInteger((byte[]) ASN1Integer.getInstance().decode(subjectPublicKeyInfo.getSubjectPublicKey()));
                AlgorithmIdentifier ai = subjectPublicKeyInfo.getAlgorithmIdentifier();
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

    @Override // java.security.interfaces.DSAPublicKey
    public BigInteger getY() {
        return this.y;
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