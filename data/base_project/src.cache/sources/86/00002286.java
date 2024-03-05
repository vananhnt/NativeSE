package java.io;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Externalizable.class */
public interface Externalizable extends Serializable {
    void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException;

    void writeExternal(ObjectOutput objectOutput) throws IOException;
}