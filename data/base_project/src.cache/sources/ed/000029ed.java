package org.apache.harmony.security.x509;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import org.apache.harmony.security.asn1.ASN1SequenceOf;
import org.apache.harmony.security.asn1.ASN1Type;
import org.apache.harmony.security.asn1.BerInputStream;

/* loaded from: Extensions.class */
public final class Extensions {
    private final List<Extension> extensions;
    private volatile Set<String> critical;
    private volatile Set<String> noncritical;
    private volatile Boolean hasUnsupported;
    private volatile HashMap<String, Extension> oidMap;
    private byte[] encoding;
    private static List SUPPORTED_CRITICAL = Arrays.asList("2.5.29.15", "2.5.29.19", "2.5.29.32", "2.5.29.17", "2.5.29.30", "2.5.29.36", "2.5.29.37", "2.5.29.54");
    public static final ASN1Type ASN1 = new ASN1SequenceOf(Extension.ASN1) { // from class: org.apache.harmony.security.x509.Extensions.1
        @Override // org.apache.harmony.security.asn1.ASN1Type
        public Object getDecodedObject(BerInputStream in) {
            return new Extensions((List) in.content);
        }

        @Override // org.apache.harmony.security.asn1.ASN1ValueCollection
        public Collection getValues(Object object) {
            Extensions extensions = (Extensions) object;
            return extensions.extensions == null ? new ArrayList() : extensions.extensions;
        }
    };

    public Extensions() {
        this.extensions = null;
    }

    public Extensions(List<Extension> extensions) {
        this.extensions = extensions;
    }

    public int size() {
        if (this.extensions == null) {
            return 0;
        }
        return this.extensions.size();
    }

    public Set<String> getCriticalExtensions() {
        Set<String> resultCritical = this.critical;
        if (resultCritical == null) {
            makeOidsLists();
            resultCritical = this.critical;
        }
        return resultCritical;
    }

    public Set<String> getNonCriticalExtensions() {
        Set<String> resultNoncritical = this.noncritical;
        if (resultNoncritical == null) {
            makeOidsLists();
            resultNoncritical = this.noncritical;
        }
        return resultNoncritical;
    }

    public boolean hasUnsupportedCritical() {
        Boolean resultHasUnsupported = this.hasUnsupported;
        if (resultHasUnsupported == null) {
            makeOidsLists();
            resultHasUnsupported = this.hasUnsupported;
        }
        return resultHasUnsupported.booleanValue();
    }

    private void makeOidsLists() {
        if (this.extensions == null) {
            return;
        }
        int size = this.extensions.size();
        Set<String> localCritical = new HashSet<>(size);
        Set<String> localNoncritical = new HashSet<>(size);
        Boolean localHasUnsupported = Boolean.FALSE;
        for (Extension extension : this.extensions) {
            String oid = extension.getExtnID();
            if (extension.getCritical()) {
                if (!SUPPORTED_CRITICAL.contains(oid)) {
                    localHasUnsupported = Boolean.TRUE;
                }
                localCritical.add(oid);
            } else {
                localNoncritical.add(oid);
            }
        }
        this.critical = localCritical;
        this.noncritical = localNoncritical;
        this.hasUnsupported = localHasUnsupported;
    }

    public Extension getExtensionByOID(String oid) {
        if (this.extensions == null) {
            return null;
        }
        HashMap<String, Extension> localOidMap = this.oidMap;
        if (localOidMap == null) {
            localOidMap = new HashMap<>();
            for (Extension extension : this.extensions) {
                localOidMap.put(extension.getExtnID(), extension);
            }
            this.oidMap = localOidMap;
        }
        return localOidMap.get(oid);
    }

    public boolean[] valueOfKeyUsage() {
        KeyUsage kUsage;
        Extension extension = getExtensionByOID("2.5.29.15");
        if (extension == null || (kUsage = extension.getKeyUsageValue()) == null) {
            return null;
        }
        return kUsage.getKeyUsage();
    }

    public List<String> valueOfExtendedKeyUsage() throws IOException {
        Extension extension = getExtensionByOID("2.5.29.37");
        if (extension == null) {
            return null;
        }
        return ((ExtendedKeyUsage) extension.getDecodedExtensionValue()).getExtendedKeyUsage();
    }

    public int valueOfBasicConstraints() {
        BasicConstraints bc;
        Extension extension = getExtensionByOID("2.5.29.19");
        if (extension == null || (bc = extension.getBasicConstraintsValue()) == null || !bc.getCa()) {
            return -1;
        }
        return bc.getPathLenConstraint();
    }

    public Collection<List<?>> valueOfSubjectAlternativeName() throws IOException {
        return decodeGeneralNames(getExtensionByOID("2.5.29.17"));
    }

    public Collection<List<?>> valueOfIssuerAlternativeName() throws IOException {
        return decodeGeneralNames(getExtensionByOID("2.5.29.18"));
    }

    private static Collection<List<?>> decodeGeneralNames(Extension extension) throws IOException {
        if (extension == null) {
            return null;
        }
        Collection<List<?>> collection = ((GeneralNames) GeneralNames.ASN1.decode(extension.getExtnValue())).getPairsList();
        if (collection.size() == 0) {
            return null;
        }
        return Collections.unmodifiableCollection(collection);
    }

    public X500Principal valueOfCertificateIssuerExtension() throws IOException {
        Extension extension = getExtensionByOID("2.5.29.29");
        if (extension == null) {
            return null;
        }
        return ((CertificateIssuer) extension.getDecodedExtensionValue()).getIssuer();
    }

    public byte[] getEncoded() {
        if (this.encoding == null) {
            this.encoding = ASN1.encode(this);
        }
        return this.encoding;
    }

    public boolean equals(Object other) {
        if (!(other instanceof Extensions)) {
            return false;
        }
        Extensions that = (Extensions) other;
        return (this.extensions == null || this.extensions.isEmpty()) ? that.extensions == null || that.extensions.isEmpty() : this.extensions.equals(that.extensions);
    }

    public int hashCode() {
        int hashCode = 0;
        if (this.extensions != null) {
            hashCode = this.extensions.hashCode();
        }
        return hashCode;
    }

    public void dumpValue(StringBuilder sb, String prefix) {
        if (this.extensions == null) {
            return;
        }
        int num = 1;
        for (Extension extension : this.extensions) {
            int i = num;
            num++;
            sb.append('\n').append(prefix).append('[').append(i).append("]: ");
            extension.dumpValue(sb, prefix);
        }
    }
}