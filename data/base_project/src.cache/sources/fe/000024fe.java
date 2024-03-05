package java.sql;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: RowIdLifetime.class */
public enum RowIdLifetime {
    ROWID_UNSUPPORTED,
    ROWID_VALID_FOREVER,
    ROWID_VALID_OTHER,
    ROWID_VALID_SESSION,
    ROWID_VALID_TRANSACTION
}