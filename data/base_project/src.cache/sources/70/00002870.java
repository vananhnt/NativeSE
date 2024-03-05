package javax.sql;

import java.io.Serializable;
import java.util.EventObject;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: RowSetEvent.class */
public class RowSetEvent extends EventObject implements Serializable {
    private static final long serialVersionUID = -1875450876546332005L;

    public RowSetEvent(RowSet theSource) {
        super(theSource);
    }
}