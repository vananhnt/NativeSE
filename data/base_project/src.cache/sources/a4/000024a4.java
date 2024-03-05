package java.security.cert;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: CertSelector.class */
public interface CertSelector extends Cloneable {
    Object clone();

    boolean match(Certificate certificate);
}