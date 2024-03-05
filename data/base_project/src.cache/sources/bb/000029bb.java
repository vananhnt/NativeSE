package org.apache.harmony.security.provider.crypto;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.DSAKey;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.DSAPublicKey;

/* loaded from: SHA1withDSA_SignatureImpl.class */
public class SHA1withDSA_SignatureImpl extends Signature {
    private MessageDigest msgDigest;
    private DSAKey dsaKey;

    public SHA1withDSA_SignatureImpl() throws NoSuchAlgorithmException {
        super("SHA1withDSA");
        this.msgDigest = MessageDigest.getInstance("SHA1");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // java.security.SignatureSpi
    public Object engineGetParameter(String param) throws InvalidParameterException {
        if (param == null) {
            throw new NullPointerException("param == null");
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // java.security.SignatureSpi
    public void engineInitSign(PrivateKey privateKey) throws InvalidKeyException {
        if (privateKey == null || !(privateKey instanceof DSAPrivateKey)) {
            throw new InvalidKeyException();
        }
        DSAParams params = ((DSAPrivateKey) privateKey).getParams();
        BigInteger p = params.getP();
        BigInteger q = params.getQ();
        BigInteger x = ((DSAPrivateKey) privateKey).getX();
        int n = p.bitLength();
        if (p.compareTo(BigInteger.valueOf(1L)) != 1 || n < 512 || n > 1024 || (n & 63) != 0) {
            throw new InvalidKeyException("bad p");
        }
        if (q.signum() != 1 && q.bitLength() != 160) {
            throw new InvalidKeyException("bad q");
        }
        if (x.signum() != 1 || x.compareTo(q) != -1) {
            throw new InvalidKeyException("x <= 0 || x >= q");
        }
        this.dsaKey = (DSAKey) privateKey;
        this.msgDigest.reset();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // java.security.SignatureSpi
    public void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {
        if (publicKey == null || !(publicKey instanceof DSAPublicKey)) {
            throw new InvalidKeyException("publicKey is not an instance of DSAPublicKey");
        }
        DSAParams params = ((DSAPublicKey) publicKey).getParams();
        BigInteger p = params.getP();
        BigInteger q = params.getQ();
        BigInteger y = ((DSAPublicKey) publicKey).getY();
        int n1 = p.bitLength();
        if (p.compareTo(BigInteger.valueOf(1L)) != 1 || n1 < 512 || n1 > 1024 || (n1 & 63) != 0) {
            throw new InvalidKeyException("bad p");
        }
        if (q.signum() != 1 || q.bitLength() != 160) {
            throw new InvalidKeyException("bad q");
        }
        if (y.signum() != 1) {
            throw new InvalidKeyException("y <= 0");
        }
        this.dsaKey = (DSAKey) publicKey;
        this.msgDigest.reset();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // java.security.SignatureSpi
    public void engineSetParameter(String param, Object value) throws InvalidParameterException {
        if (param == null) {
            throw new NullPointerException("param == null");
        }
        throw new InvalidParameterException("invalid parameter for this engine");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // java.security.SignatureSpi
    public byte[] engineSign() throws SignatureException {
        BigInteger r;
        BigInteger s;
        int n;
        int n2;
        if (this.appRandom == null) {
            this.appRandom = new SecureRandom();
        }
        DSAParams params = this.dsaKey.getParams();
        BigInteger p = params.getP();
        BigInteger q = params.getQ();
        BigInteger g = params.getG();
        BigInteger x = ((DSAPrivateKey) this.dsaKey).getX();
        BigInteger digestBI = new BigInteger(1, this.msgDigest.digest());
        byte[] randomBytes = new byte[20];
        while (true) {
            this.appRandom.nextBytes(randomBytes);
            BigInteger k = new BigInteger(1, randomBytes);
            if (k.compareTo(q) == -1) {
                r = g.modPow(k, p).mod(q);
                if (r.signum() == 0) {
                    continue;
                } else {
                    s = k.modInverse(q).multiply(digestBI.add(x.multiply(r)).mod(q)).mod(q);
                    if (s.signum() != 0) {
                        break;
                    }
                }
            }
        }
        byte[] rBytes = r.toByteArray();
        int n1 = rBytes.length;
        if ((rBytes[0] & 128) != 0) {
            n1++;
        }
        byte[] sBytes = s.toByteArray();
        int n22 = sBytes.length;
        if ((sBytes[0] & 128) != 0) {
            n22++;
        }
        byte[] signature = new byte[6 + n1 + n22];
        signature[0] = 48;
        signature[1] = (byte) (4 + n1 + n22);
        signature[2] = 2;
        signature[3] = (byte) n1;
        signature[4 + n1] = 2;
        signature[5 + n1] = (byte) n22;
        if (n1 == rBytes.length) {
            n = 4;
        } else {
            n = 5;
        }
        System.arraycopy(rBytes, 0, signature, n, rBytes.length);
        if (n22 == sBytes.length) {
            n2 = 6 + n1;
        } else {
            n2 = 7 + n1;
        }
        System.arraycopy(sBytes, 0, signature, n2, sBytes.length);
        return signature;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // java.security.SignatureSpi
    public void engineUpdate(byte b) throws SignatureException {
        this.msgDigest.update(b);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // java.security.SignatureSpi
    public void engineUpdate(byte[] b, int off, int len) throws SignatureException {
        this.msgDigest.update(b, off, len);
    }

    private boolean checkSignature(byte[] sigBytes, int offset, int length) throws SignatureException {
        try {
            int n1 = sigBytes[offset + 3];
            int n2 = sigBytes[offset + n1 + 5];
            if (sigBytes[offset + 0] != 48 || sigBytes[offset + 2] != 2 || sigBytes[offset + n1 + 4] != 2 || sigBytes[offset + 1] != n1 + n2 + 4 || n1 > 21 || n2 > 21 || (length != 0 && (sigBytes[offset + 1] == 1 ? 1 : 0) + 2 > length)) {
                throw new SignatureException("signature bytes have invalid encoding");
            }
            byte b = sigBytes[5 + n1 + n2];
            byte[] digest = this.msgDigest.digest();
            byte[] bytes = new byte[n1];
            System.arraycopy(sigBytes, offset + 4, bytes, 0, n1);
            BigInteger r = new BigInteger(bytes);
            byte[] bytes2 = new byte[n2];
            System.arraycopy(sigBytes, offset + 6 + n1, bytes2, 0, n2);
            BigInteger s = new BigInteger(bytes2);
            DSAParams params = this.dsaKey.getParams();
            BigInteger p = params.getP();
            BigInteger q = params.getQ();
            BigInteger g = params.getG();
            BigInteger y = ((DSAPublicKey) this.dsaKey).getY();
            if (r.signum() != 1 || r.compareTo(q) != -1 || s.signum() != 1 || s.compareTo(q) != -1) {
                return false;
            }
            BigInteger w = s.modInverse(q);
            BigInteger u1 = new BigInteger(1, digest).multiply(w).mod(q);
            BigInteger u2 = r.multiply(w).mod(q);
            BigInteger v = g.modPow(u1, p).multiply(y.modPow(u2, p)).mod(p).mod(q);
            if (v.compareTo(r) != 0) {
                return false;
            }
            return true;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new SignatureException("bad argument: byte[] is too small");
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // java.security.SignatureSpi
    public boolean engineVerify(byte[] sigBytes) throws SignatureException {
        if (sigBytes == null) {
            throw new NullPointerException("sigBytes == null");
        }
        return checkSignature(sigBytes, 0, 0);
    }

    @Override // java.security.SignatureSpi
    protected boolean engineVerify(byte[] sigBytes, int offset, int length) throws SignatureException {
        return checkSignature(sigBytes, offset, length);
    }
}