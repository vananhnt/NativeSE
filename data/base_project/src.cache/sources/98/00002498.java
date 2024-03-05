package java.security.cert;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: CRLSelector.class */
public interface CRLSelector extends Cloneable {
    Object clone();

    boolean match(CRL crl);
}