package android.accounts;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/* loaded from: AccountManagerFuture.class */
public interface AccountManagerFuture<V> {
    boolean cancel(boolean z);

    boolean isCancelled();

    boolean isDone();

    V getResult() throws OperationCanceledException, IOException, AuthenticatorException;

    V getResult(long j, TimeUnit timeUnit) throws OperationCanceledException, IOException, AuthenticatorException;
}