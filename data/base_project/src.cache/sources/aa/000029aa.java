package org.apache.harmony.security.provider.cert;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactorySpi;
import java.security.cert.X509CRL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import libcore.io.Base64;
import libcore.io.Streams;
import org.apache.harmony.security.asn1.BerInputStream;
import org.apache.harmony.security.pkcs7.ContentInfo;
import org.apache.harmony.security.pkcs7.SignedData;
import org.apache.harmony.security.x509.CertificateList;

/* loaded from: X509CertFactoryImpl.class */
public class X509CertFactoryImpl extends CertificateFactorySpi {
    private static final int CERT_CACHE_SEED_LENGTH = 28;
    private static final int CRL_CACHE_SEED_LENGTH = 24;
    private static final Cache CERT_CACHE = new Cache(28);
    private static final Cache CRL_CACHE = new Cache(24);
    private static final byte[] PEM_BEGIN = "-----BEGIN".getBytes(StandardCharsets.UTF_8);
    private static final byte[] PEM_END = "-----END".getBytes(StandardCharsets.UTF_8);
    private static final byte[] FREE_BOUND_SUFFIX = null;
    private static final byte[] CERT_BOUND_SUFFIX = " CERTIFICATE-----".getBytes(StandardCharsets.UTF_8);

    @Override // java.security.cert.CertificateFactorySpi
    public Certificate engineGenerateCertificate(InputStream inStream) throws CertificateException {
        if (inStream == null) {
            throw new CertificateException("inStream == null");
        }
        try {
            if (!inStream.markSupported()) {
                inStream = new RestoringInputStream(inStream);
            }
            inStream.mark(1);
            if (inStream.read() == 45) {
                return getCertificate(decodePEM(inStream, CERT_BOUND_SUFFIX));
            }
            inStream.reset();
            return getCertificate(inStream);
        } catch (IOException e) {
            throw new CertificateException(e);
        }
    }

    @Override // java.security.cert.CertificateFactorySpi
    public Collection<? extends Certificate> engineGenerateCertificates(InputStream inStream) throws CertificateException {
        int ch;
        if (inStream == null) {
            throw new CertificateException("inStream == null");
        }
        ArrayList<Certificate> result = new ArrayList<>();
        try {
            if (!inStream.markSupported()) {
                inStream = new RestoringInputStream(inStream);
            }
            byte[] encoding = null;
            int second_asn1_tag = -1;
            inStream.mark(1);
            while (true) {
                ch = inStream.read();
                if (ch == -1) {
                    break;
                }
                if (ch == 45) {
                    encoding = decodePEM(inStream, FREE_BOUND_SUFFIX);
                } else if (ch == 48) {
                    encoding = null;
                    inStream.reset();
                    inStream.mark(28);
                } else if (result.size() == 0) {
                    throw new CertificateException("Unsupported encoding");
                } else {
                    inStream.reset();
                    return result;
                }
                BerInputStream in = encoding == null ? new BerInputStream(inStream) : new BerInputStream(encoding);
                second_asn1_tag = in.next();
                if (encoding == null) {
                    inStream.reset();
                }
                if (second_asn1_tag != 48) {
                    if (result.size() != 0) {
                        return result;
                    }
                } else {
                    if (encoding == null) {
                        result.add(getCertificate(inStream));
                    } else {
                        result.add(getCertificate(encoding));
                    }
                    inStream.mark(1);
                }
            }
            if (result.size() != 0) {
                return result;
            }
            if (ch == -1) {
                return result;
            }
            if (second_asn1_tag == 6) {
                ContentInfo info = (ContentInfo) (encoding != null ? ContentInfo.ASN1.decode(encoding) : ContentInfo.ASN1.decode(inStream));
                SignedData data = info.getSignedData();
                if (data == null) {
                    throw new CertificateException("Invalid PKCS7 data provided");
                }
                List<org.apache.harmony.security.x509.Certificate> certs = data.getCertificates();
                if (certs != null) {
                    for (org.apache.harmony.security.x509.Certificate cert : certs) {
                        result.add(new X509CertImpl(cert));
                    }
                }
                return result;
            }
            throw new CertificateException("Unsupported encoding");
        } catch (IOException e) {
            throw new CertificateException(e);
        }
    }

    @Override // java.security.cert.CertificateFactorySpi
    public CRL engineGenerateCRL(InputStream inStream) throws CRLException {
        if (inStream == null) {
            throw new CRLException("inStream == null");
        }
        try {
            if (!inStream.markSupported()) {
                inStream = new RestoringInputStream(inStream);
            }
            inStream.mark(1);
            if (inStream.read() == 45) {
                return getCRL(decodePEM(inStream, FREE_BOUND_SUFFIX));
            }
            inStream.reset();
            return getCRL(inStream);
        } catch (IOException e) {
            throw new CRLException(e);
        }
    }

    @Override // java.security.cert.CertificateFactorySpi
    public Collection<? extends CRL> engineGenerateCRLs(InputStream inStream) throws CRLException {
        int ch;
        if (inStream == null) {
            throw new CRLException("inStream == null");
        }
        ArrayList<CRL> result = new ArrayList<>();
        try {
            if (!inStream.markSupported()) {
                inStream = new RestoringInputStream(inStream);
            }
            byte[] encoding = null;
            int second_asn1_tag = -1;
            inStream.mark(1);
            while (true) {
                ch = inStream.read();
                if (ch == -1) {
                    break;
                }
                if (ch == 45) {
                    encoding = decodePEM(inStream, FREE_BOUND_SUFFIX);
                } else if (ch == 48) {
                    encoding = null;
                    inStream.reset();
                    inStream.mark(24);
                } else if (result.size() == 0) {
                    throw new CRLException("Unsupported encoding");
                } else {
                    inStream.reset();
                    return result;
                }
                BerInputStream in = encoding == null ? new BerInputStream(inStream) : new BerInputStream(encoding);
                second_asn1_tag = in.next();
                if (encoding == null) {
                    inStream.reset();
                }
                if (second_asn1_tag != 48) {
                    if (result.size() != 0) {
                        return result;
                    }
                } else {
                    if (encoding == null) {
                        result.add(getCRL(inStream));
                    } else {
                        result.add(getCRL(encoding));
                    }
                    inStream.mark(1);
                }
            }
            if (result.size() != 0) {
                return result;
            }
            if (ch == -1) {
                throw new CRLException("There is no data in the stream");
            }
            if (second_asn1_tag == 6) {
                ContentInfo info = (ContentInfo) (encoding != null ? ContentInfo.ASN1.decode(encoding) : ContentInfo.ASN1.decode(inStream));
                SignedData data = info.getSignedData();
                if (data == null) {
                    throw new CRLException("Invalid PKCS7 data provided");
                }
                List<CertificateList> crls = data.getCRLs();
                if (crls != null) {
                    for (CertificateList crl : crls) {
                        result.add(new X509CRLImpl(crl));
                    }
                }
                return result;
            }
            throw new CRLException("Unsupported encoding");
        } catch (IOException e) {
            throw new CRLException(e);
        }
    }

    @Override // java.security.cert.CertificateFactorySpi
    public CertPath engineGenerateCertPath(InputStream inStream) throws CertificateException {
        if (inStream == null) {
            throw new CertificateException("inStream == null");
        }
        return engineGenerateCertPath(inStream, "PkiPath");
    }

    @Override // java.security.cert.CertificateFactorySpi
    public CertPath engineGenerateCertPath(InputStream inStream, String encoding) throws CertificateException {
        if (inStream == null) {
            throw new CertificateException("inStream == null");
        }
        if (!inStream.markSupported()) {
            inStream = new RestoringInputStream(inStream);
        }
        try {
            inStream.mark(1);
            int ch = inStream.read();
            if (ch == 45) {
                return X509CertPathImpl.getInstance(decodePEM(inStream, FREE_BOUND_SUFFIX), encoding);
            }
            if (ch == 48) {
                inStream.reset();
                return X509CertPathImpl.getInstance(inStream, encoding);
            }
            throw new CertificateException("Unsupported encoding");
        } catch (IOException e) {
            throw new CertificateException(e);
        }
    }

    @Override // java.security.cert.CertificateFactorySpi
    public CertPath engineGenerateCertPath(List<? extends Certificate> certificates) throws CertificateException {
        return new X509CertPathImpl(certificates);
    }

    @Override // java.security.cert.CertificateFactorySpi
    public Iterator<String> engineGetCertPathEncodings() {
        return X509CertPathImpl.encodings.iterator();
    }

    private byte[] decodePEM(InputStream inStream, byte[] boundary_suffix) throws IOException {
        int ch;
        int ch2;
        for (int i = 1; i < PEM_BEGIN.length; i++) {
            if (PEM_BEGIN[i] != inStream.read()) {
                throw new IOException("Incorrect PEM encoding: '-----BEGIN" + (boundary_suffix == null ? "" : new String(boundary_suffix)) + "' is expected as opening delimiter boundary.");
            }
        }
        if (boundary_suffix == null) {
            do {
                ch2 = inStream.read();
                if (ch2 != 10) {
                }
            } while (ch2 != -1);
            throw new IOException("Incorrect PEM encoding: EOF before content");
        }
        for (byte b : boundary_suffix) {
            if (b != inStream.read()) {
                throw new IOException("Incorrect PEM encoding: '-----BEGIN" + new String(boundary_suffix) + "' is expected as opening delimiter boundary.");
            }
        }
        int read = inStream.read();
        int ch3 = read;
        if (read == 13) {
            ch3 = inStream.read();
        }
        if (ch3 != 10) {
            throw new IOException("Incorrect PEM encoding: newline expected after opening delimiter boundary");
        }
        int size = 1024;
        byte[] buff = new byte[1024];
        int index = 0;
        while (true) {
            int ch4 = inStream.read();
            if (ch4 != 45) {
                if (ch4 == -1) {
                    throw new IOException("Incorrect Base64 encoding: EOF without closing delimiter");
                }
                int i2 = index;
                index++;
                buff[i2] = (byte) ch4;
                if (index == size) {
                    byte[] newbuff = new byte[size + 1024];
                    System.arraycopy(buff, 0, newbuff, 0, size);
                    buff = newbuff;
                    size += 1024;
                }
            } else if (buff[index - 1] != 10) {
                throw new IOException("Incorrect Base64 encoding: newline expected before closing boundary delimiter");
            } else {
                for (int i3 = 1; i3 < PEM_END.length; i3++) {
                    if (PEM_END[i3] != inStream.read()) {
                        throw badEnd(boundary_suffix);
                    }
                }
                if (boundary_suffix == null) {
                    do {
                        ch = inStream.read();
                        if (ch == -1 || ch == 10) {
                            break;
                        }
                    } while (ch != 13);
                } else {
                    for (byte b2 : boundary_suffix) {
                        if (b2 != inStream.read()) {
                            throw badEnd(boundary_suffix);
                        }
                    }
                }
                inStream.mark(1);
                while (true) {
                    int ch5 = inStream.read();
                    if (ch5 == -1 || !(ch5 == 10 || ch5 == 13)) {
                        break;
                    }
                    inStream.mark(1);
                }
                inStream.reset();
                byte[] buff2 = Base64.decode(buff, index);
                if (buff2 == null) {
                    throw new IOException("Incorrect Base64 encoding");
                }
                return buff2;
            }
        }
    }

    private IOException badEnd(byte[] boundary_suffix) throws IOException {
        String s = boundary_suffix == null ? "" : new String(boundary_suffix);
        throw new IOException("Incorrect PEM encoding: '-----END" + s + "' is expected as closing delimiter boundary.");
    }

    private static byte[] readBytes(InputStream source, int length) throws IOException {
        byte[] result = new byte[length];
        for (int i = 0; i < length; i++) {
            int bytik = source.read();
            if (bytik == -1) {
                return null;
            }
            result[i] = (byte) bytik;
        }
        return result;
    }

    private static Certificate getCertificate(byte[] encoding) throws CertificateException, IOException {
        Certificate res;
        if (encoding.length < 28) {
            throw new CertificateException("encoding.length < CERT_CACHE_SEED_LENGTH");
        }
        synchronized (CERT_CACHE) {
            long hash = CERT_CACHE.getHash(encoding);
            if (CERT_CACHE.contains(hash) && (res = (Certificate) CERT_CACHE.get(hash, encoding)) != null) {
                return res;
            }
            Certificate res2 = new X509CertImpl(encoding);
            CERT_CACHE.put(hash, encoding, res2);
            return res2;
        }
    }

    private static Certificate getCertificate(InputStream inStream) throws CertificateException, IOException {
        synchronized (CERT_CACHE) {
            inStream.mark(28);
            byte[] buff = readBytes(inStream, 28);
            inStream.reset();
            if (buff == null) {
                throw new CertificateException("InputStream doesn't contain enough data");
            }
            long hash = CERT_CACHE.getHash(buff);
            if (CERT_CACHE.contains(hash)) {
                byte[] encoding = new byte[BerInputStream.getLength(buff)];
                if (encoding.length < 28) {
                    throw new CertificateException("Bad Certificate encoding");
                }
                Streams.readFully(inStream, encoding);
                Certificate res = (Certificate) CERT_CACHE.get(hash, encoding);
                if (res != null) {
                    return res;
                }
                Certificate res2 = new X509CertImpl(encoding);
                CERT_CACHE.put(hash, encoding, res2);
                return res2;
            }
            inStream.reset();
            Certificate res3 = new X509CertImpl(inStream);
            CERT_CACHE.put(hash, res3.getEncoded(), res3);
            return res3;
        }
    }

    private static CRL getCRL(byte[] encoding) throws CRLException, IOException {
        X509CRL res;
        if (encoding.length < 24) {
            throw new CRLException("encoding.length < CRL_CACHE_SEED_LENGTH");
        }
        synchronized (CRL_CACHE) {
            long hash = CRL_CACHE.getHash(encoding);
            if (CRL_CACHE.contains(hash) && (res = (X509CRL) CRL_CACHE.get(hash, encoding)) != null) {
                return res;
            }
            X509CRL res2 = new X509CRLImpl(encoding);
            CRL_CACHE.put(hash, encoding, res2);
            return res2;
        }
    }

    private static CRL getCRL(InputStream inStream) throws CRLException, IOException {
        synchronized (CRL_CACHE) {
            inStream.mark(24);
            byte[] buff = readBytes(inStream, 24);
            inStream.reset();
            if (buff == null) {
                throw new CRLException("InputStream doesn't contain enough data");
            }
            long hash = CRL_CACHE.getHash(buff);
            if (CRL_CACHE.contains(hash)) {
                byte[] encoding = new byte[BerInputStream.getLength(buff)];
                if (encoding.length < 24) {
                    throw new CRLException("Bad CRL encoding");
                }
                Streams.readFully(inStream, encoding);
                CRL res = (CRL) CRL_CACHE.get(hash, encoding);
                if (res != null) {
                    return res;
                }
                CRL res2 = new X509CRLImpl(encoding);
                CRL_CACHE.put(hash, encoding, res2);
                return res2;
            }
            X509CRL res3 = new X509CRLImpl(inStream);
            CRL_CACHE.put(hash, res3.getEncoded(), res3);
            return res3;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: X509CertFactoryImpl$RestoringInputStream.class */
    public static class RestoringInputStream extends InputStream {
        private final InputStream inStream;
        private static final int BUFF_SIZE = 32;
        private final int[] buff = new int[64];
        private int pos = -1;
        private int bar = 0;
        private int end = 0;

        public RestoringInputStream(InputStream inStream) {
            this.inStream = inStream;
        }

        @Override // java.io.InputStream
        public int available() throws IOException {
            return (this.bar - this.pos) + this.inStream.available();
        }

        @Override // java.io.InputStream, java.io.Closeable
        public void close() throws IOException {
            this.inStream.close();
        }

        @Override // java.io.InputStream
        public void mark(int readlimit) {
            if (this.pos < 0) {
                this.pos = 0;
                this.bar = 0;
                this.end = 31;
                return;
            }
            this.end = ((this.pos + 32) - 1) % 32;
        }

        @Override // java.io.InputStream
        public boolean markSupported() {
            return true;
        }

        @Override // java.io.InputStream
        public int read() throws IOException {
            if (this.pos >= 0) {
                int cur = this.pos % 32;
                if (cur < this.bar) {
                    this.pos++;
                    return this.buff[cur];
                } else if (cur != this.end) {
                    this.buff[cur] = this.inStream.read();
                    this.bar = cur + 1;
                    this.pos++;
                    return this.buff[cur];
                } else {
                    this.pos = -1;
                }
            }
            return this.inStream.read();
        }

        @Override // java.io.InputStream
        public int read(byte[] b, int off, int len) throws IOException {
            int i = 0;
            while (i < len) {
                int read_b = read();
                if (read_b == -1) {
                    if (i == 0) {
                        return -1;
                    }
                    return i;
                }
                b[off + i] = (byte) read_b;
                i++;
            }
            return i;
        }

        @Override // java.io.InputStream
        public void reset() throws IOException {
            if (this.pos >= 0) {
                this.pos = (this.end + 1) % 32;
                return;
            }
            throw new IOException("Could not reset the stream: position became invalid or stream has not been marked");
        }
    }
}