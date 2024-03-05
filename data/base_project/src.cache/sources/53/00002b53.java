package org.apache.http.impl.conn.tsccm;

import java.lang.ref.Reference;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: RefQueueHandler.class */
public interface RefQueueHandler {
    void handleReference(Reference<?> reference);
}