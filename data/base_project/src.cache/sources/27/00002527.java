package java.text;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: CollationKey.class */
public abstract class CollationKey implements Comparable<CollationKey> {
    @Override // java.lang.Comparable
    public abstract int compareTo(CollationKey collationKey);

    public abstract byte[] toByteArray();

    /* JADX INFO: Access modifiers changed from: protected */
    public CollationKey(String source) {
        throw new RuntimeException("Stub!");
    }

    public String getSourceString() {
        throw new RuntimeException("Stub!");
    }
}