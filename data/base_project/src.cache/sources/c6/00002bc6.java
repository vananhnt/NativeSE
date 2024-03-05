package org.apache.http.protocol;

import java.util.List;
import org.apache.http.HttpRequestInterceptor;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: HttpRequestInterceptorList.class */
public interface HttpRequestInterceptorList {
    void addRequestInterceptor(HttpRequestInterceptor httpRequestInterceptor);

    void addRequestInterceptor(HttpRequestInterceptor httpRequestInterceptor, int i);

    int getRequestInterceptorCount();

    HttpRequestInterceptor getRequestInterceptor(int i);

    void clearRequestInterceptors();

    void removeRequestInterceptorByClass(Class cls);

    void setInterceptors(List list);
}