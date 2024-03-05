package org.apache.http.cookie;

import java.util.Date;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Cookie.class */
public interface Cookie {
    String getName();

    String getValue();

    String getComment();

    String getCommentURL();

    Date getExpiryDate();

    boolean isPersistent();

    String getDomain();

    String getPath();

    int[] getPorts();

    boolean isSecure();

    int getVersion();

    boolean isExpired(Date date);
}