package javax.sql;

import java.util.EventListener;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: StatementEventListener.class */
public interface StatementEventListener extends EventListener {
    void statementClosed(StatementEvent statementEvent);

    void statementErrorOccurred(StatementEvent statementEvent);
}