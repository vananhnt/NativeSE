package org.apache.http.cookie;

import java.util.Date;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: SetCookie.class */
public interface SetCookie extends Cookie {
    void setValue(String str);

    void setComment(String str);

    void setExpiryDate(Date date);

    void setDomain(String str);

    void setPath(String str);

    void setSecure(boolean z);

    void setVersion(int i);
}