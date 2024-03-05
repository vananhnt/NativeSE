package org.apache.http.client.methods;

import java.io.IOException;
import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.conn.ConnectionReleaseTrigger;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: AbortableHttpRequest.class */
public interface AbortableHttpRequest {
    void setConnectionRequest(ClientConnectionRequest clientConnectionRequest) throws IOException;

    void setReleaseTrigger(ConnectionReleaseTrigger connectionReleaseTrigger) throws IOException;

    void abort();
}