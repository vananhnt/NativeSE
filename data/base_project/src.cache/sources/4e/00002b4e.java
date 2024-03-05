package org.apache.http.impl.conn.tsccm;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import org.apache.http.conn.routing.HttpRoute;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: BasicPoolEntryRef.class */
public class BasicPoolEntryRef extends WeakReference<BasicPoolEntry> {
    public BasicPoolEntryRef(BasicPoolEntry entry, ReferenceQueue<Object> queue) {
        super(null, null);
        throw new RuntimeException("Stub!");
    }

    public final HttpRoute getRoute() {
        throw new RuntimeException("Stub!");
    }
}