package javax.crypto.spec;

import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import javax.crypto.spec.PSource;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: OAEPParameterSpec.class */
public class OAEPParameterSpec implements AlgorithmParameterSpec {
    private final String mdName;
    private final String mgfName;
    private final AlgorithmParameterSpec mgfSpec;
    private final PSource pSrc;
    public static final OAEPParameterSpec DEFAULT = new OAEPParameterSpec();

    private OAEPParameterSpec() {
        this.mdName = "SHA-1";
        this.mgfName = "MGF1";
        this.mgfSpec = MGF1ParameterSpec.SHA1;
        this.pSrc = PSource.PSpecified.DEFAULT;
    }

    public OAEPParameterSpec(String mdName, String mgfName, AlgorithmParameterSpec mgfSpec, PSource pSrc) {
        if (mdName == null) {
            throw new NullPointerException("mdName == null");
        }
        if (mgfName == null) {
            throw new NullPointerException("mgfName == null");
        }
        if (pSrc == null) {
            throw new NullPointerException("pSrc == null");
        }
        this.mdName = mdName;
        this.mgfName = mgfName;
        this.mgfSpec = mgfSpec;
        this.pSrc = pSrc;
    }

    public String getDigestAlgorithm() {
        return this.mdName;
    }

    public String getMGFAlgorithm() {
        return this.mgfName;
    }

    public AlgorithmParameterSpec getMGFParameters() {
        return this.mgfSpec;
    }

    public PSource getPSource() {
        return this.pSrc;
    }
}