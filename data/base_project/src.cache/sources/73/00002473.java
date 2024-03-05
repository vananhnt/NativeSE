package java.security;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Principal.class */
public interface Principal {
    boolean equals(Object obj);

    String getName();

    int hashCode();

    String toString();
}