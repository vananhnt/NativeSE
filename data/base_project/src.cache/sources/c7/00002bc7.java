package org.apache.http.protocol;

import java.util.List;
import org.apache.http.HttpResponseInterceptor;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: HttpResponseInterceptorList.class */
public interface HttpResponseInterceptorList {
    void addResponseInterceptor(HttpResponseInterceptor httpResponseInterceptor);

    void addResponseInterceptor(HttpResponseInterceptor httpResponseInterceptor, int i);

    int getResponseInterceptorCount();

    HttpResponseInterceptor getResponseInterceptor(int i);

    void clearResponseInterceptors();

    void removeResponseInterceptorByClass(Class cls);

    void setInterceptors(List list);
}