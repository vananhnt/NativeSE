package android.accounts;

/* loaded from: AccountManagerCallback.class */
public interface AccountManagerCallback<V> {
    void run(AccountManagerFuture<V> accountManagerFuture);
}