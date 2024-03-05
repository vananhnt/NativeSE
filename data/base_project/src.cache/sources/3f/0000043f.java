package android.database.sqlite;

/* loaded from: SQLiteTransactionListener.class */
public interface SQLiteTransactionListener {
    void onBegin();

    void onCommit();

    void onRollback();
}