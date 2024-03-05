package java.security.cert;

import java.util.Iterator;
import java.util.Set;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: PolicyNode.class */
public interface PolicyNode {
    Iterator<? extends PolicyNode> getChildren();

    int getDepth();

    Set<String> getExpectedPolicies();

    PolicyNode getParent();

    Set<? extends PolicyQualifierInfo> getPolicyQualifiers();

    String getValidPolicy();

    boolean isCritical();
}