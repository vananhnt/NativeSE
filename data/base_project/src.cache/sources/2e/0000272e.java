package java.util.jar;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import libcore.io.Base64;
import org.apache.harmony.security.utils.JarUtils;

/* loaded from: JarVerifier.class */
class JarVerifier {
    private static final String[] DIGEST_ALGORITHMS = {"SHA-512", "SHA-384", "SHA-256", "SHA1"};
    private final String jarName;
    private Manifest man;
    private HashMap<String, byte[]> metaEntries = new HashMap<>(5);
    private final Hashtable<String, HashMap<String, Attributes>> signatures = new Hashtable<>(5);
    private final Hashtable<String, Certificate[]> certificates = new Hashtable<>(5);
    private final Hashtable<String, Certificate[]> verifiedEntries = new Hashtable<>();
    int mainAttributesEnd;

    /* loaded from: JarVerifier$VerifierEntry.class */
    class VerifierEntry extends OutputStream {
        private String name;
        private MessageDigest digest;
        private byte[] hash;
        private Certificate[] certificates;

        VerifierEntry(String name, MessageDigest digest, byte[] hash, Certificate[] certificates) {
            this.name = name;
            this.digest = digest;
            this.hash = hash;
            this.certificates = certificates;
        }

        @Override // java.io.OutputStream
        public void write(int value) {
            this.digest.update((byte) value);
        }

        @Override // java.io.OutputStream
        public void write(byte[] buf, int off, int nbytes) {
            this.digest.update(buf, off, nbytes);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void verify() {
            byte[] d = this.digest.digest();
            if (!MessageDigest.isEqual(d, Base64.decode(this.hash))) {
                throw JarVerifier.this.invalidDigest("META-INF/MANIFEST.MF", this.name, JarVerifier.this.jarName);
            }
            JarVerifier.this.verifiedEntries.put(this.name, this.certificates);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public SecurityException invalidDigest(String signatureFile, String name, String jarName) {
        throw new SecurityException(signatureFile + " has invalid digest for " + name + " in " + jarName);
    }

    private SecurityException failedVerification(String jarName, String signatureFile) {
        throw new SecurityException(jarName + " failed verification of " + signatureFile);
    }

    JarVerifier(String name) {
        this.jarName = name;
    }

    VerifierEntry initEntry(String name) {
        Attributes attributes;
        if (this.man == null || this.signatures.size() == 0 || (attributes = this.man.getAttributes(name)) == null) {
            return null;
        }
        ArrayList<Certificate> certs = new ArrayList<>();
        for (Map.Entry<String, HashMap<String, Attributes>> entry : this.signatures.entrySet()) {
            HashMap<String, Attributes> hm = entry.getValue();
            if (hm.get(name) != null) {
                String signatureFile = entry.getKey();
                certs.addAll(getSignerCertificates(signatureFile, this.certificates));
            }
        }
        if (certs.isEmpty()) {
            return null;
        }
        Certificate[] certificatesArray = (Certificate[]) certs.toArray(new Certificate[certs.size()]);
        for (int i = 0; i < DIGEST_ALGORITHMS.length; i++) {
            String algorithm = DIGEST_ALGORITHMS[i];
            String hash = attributes.getValue(algorithm + "-Digest");
            if (hash != null) {
                byte[] hashBytes = hash.getBytes(StandardCharsets.ISO_8859_1);
                try {
                    return new VerifierEntry(name, MessageDigest.getInstance(algorithm), hashBytes, certificatesArray);
                } catch (NoSuchAlgorithmException e) {
                }
            }
        }
        return null;
    }

    void addMetaEntry(String name, byte[] buf) {
        this.metaEntries.put(name.toUpperCase(Locale.US), buf);
    }

    synchronized boolean readCertificates() {
        if (this.metaEntries == null) {
            return false;
        }
        Iterator<String> it = this.metaEntries.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            if (key.endsWith(".DSA") || key.endsWith(".RSA") || key.endsWith(".EC")) {
                verifyCertificate(key);
                if (this.metaEntries == null) {
                    return false;
                }
                it.remove();
            }
        }
        return true;
    }

    private void verifyCertificate(String certFile) {
        byte[] manifest;
        String signatureFile = certFile.substring(0, certFile.lastIndexOf(46)) + ".SF";
        byte[] sfBytes = this.metaEntries.get(signatureFile);
        if (sfBytes == null || (manifest = this.metaEntries.get("META-INF/MANIFEST.MF")) == null) {
            return;
        }
        byte[] sBlockBytes = this.metaEntries.get(certFile);
        try {
            Certificate[] signerCertChain = JarUtils.verifySignature(new ByteArrayInputStream(sfBytes), new ByteArrayInputStream(sBlockBytes));
            if (this.metaEntries == null) {
                return;
            }
            if (signerCertChain != null) {
                this.certificates.put(signatureFile, signerCertChain);
            }
            Attributes attributes = new Attributes();
            HashMap<String, Attributes> entries = new HashMap<>();
            try {
                ManifestReader im = new ManifestReader(sfBytes, attributes);
                im.readEntries(entries, null);
                if (attributes.get(Attributes.Name.SIGNATURE_VERSION) == null) {
                    return;
                }
                boolean createdBySigntool = false;
                String createdBy = attributes.getValue("Created-By");
                if (createdBy != null) {
                    createdBySigntool = createdBy.indexOf("signtool") != -1;
                }
                if (this.mainAttributesEnd > 0 && !createdBySigntool && !verify(attributes, "-Digest-Manifest-Main-Attributes", manifest, 0, this.mainAttributesEnd, false, true)) {
                    throw failedVerification(this.jarName, signatureFile);
                }
                String digestAttribute = createdBySigntool ? "-Digest" : "-Digest-Manifest";
                if (!verify(attributes, digestAttribute, manifest, 0, manifest.length, false, false)) {
                    for (Map.Entry<String, Attributes> entry : entries.entrySet()) {
                        Manifest.Chunk chunk = this.man.getChunk(entry.getKey());
                        if (chunk == null) {
                            return;
                        }
                        if (!verify(entry.getValue(), "-Digest", manifest, chunk.start, chunk.end, createdBySigntool, false)) {
                            throw invalidDigest(signatureFile, entry.getKey(), this.jarName);
                        }
                    }
                }
                this.metaEntries.put(signatureFile, null);
                this.signatures.put(signatureFile, entries);
            } catch (IOException e) {
            }
        } catch (IOException e2) {
        } catch (GeneralSecurityException e3) {
            throw failedVerification(this.jarName, signatureFile);
        }
    }

    void setManifest(Manifest mf) {
        this.man = mf;
    }

    boolean isSignedJar() {
        return this.certificates.size() > 0;
    }

    private boolean verify(Attributes attributes, String entry, byte[] data, int start, int end, boolean ignoreSecondEndline, boolean ignorable) {
        for (int i = 0; i < DIGEST_ALGORITHMS.length; i++) {
            String algorithm = DIGEST_ALGORITHMS[i];
            String hash = attributes.getValue(algorithm + entry);
            if (hash != null) {
                try {
                    MessageDigest md = MessageDigest.getInstance(algorithm);
                    if (ignoreSecondEndline && data[end - 1] == 10 && data[end - 2] == 10) {
                        md.update(data, start, (end - 1) - start);
                    } else {
                        md.update(data, start, end - start);
                    }
                    byte[] b = md.digest();
                    byte[] hashBytes = hash.getBytes(StandardCharsets.ISO_8859_1);
                    return MessageDigest.isEqual(b, Base64.decode(hashBytes));
                } catch (NoSuchAlgorithmException e) {
                }
            }
        }
        return ignorable;
    }

    Certificate[] getCertificates(String name) {
        Certificate[] verifiedCerts = this.verifiedEntries.get(name);
        if (verifiedCerts == null) {
            return null;
        }
        return (Certificate[]) verifiedCerts.clone();
    }

    void removeMetaEntries() {
        this.metaEntries = null;
    }

    public static Vector<Certificate> getSignerCertificates(String signatureFileName, Map<String, Certificate[]> certificates) {
        Vector<Certificate> result = new Vector<>();
        Certificate[] certChain = certificates.get(signatureFileName);
        if (certChain != null) {
            for (Certificate element : certChain) {
                result.add(element);
            }
        }
        return result;
    }
}