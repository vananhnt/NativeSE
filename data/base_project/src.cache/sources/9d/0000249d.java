package java.security.cert;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: CertPathBuilderResult.class */
public interface CertPathBuilderResult extends Cloneable {
    Object clone();

    CertPath getCertPath();
}