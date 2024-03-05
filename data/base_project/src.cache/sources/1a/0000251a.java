package java.sql;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Wrapper.class */
public interface Wrapper {
    <T> T unwrap(Class<T> cls) throws SQLException;

    boolean isWrapperFor(Class<?> cls) throws SQLException;
}