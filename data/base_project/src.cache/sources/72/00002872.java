package javax.sql;

import java.util.EventListener;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: RowSetListener.class */
public interface RowSetListener extends EventListener {
    void cursorMoved(RowSetEvent rowSetEvent);

    void rowChanged(RowSetEvent rowSetEvent);

    void rowSetChanged(RowSetEvent rowSetEvent);
}