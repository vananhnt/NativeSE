package org.apache.harmony.security.fortress;

import java.security.Provider;
import java.util.List;

/* loaded from: SecurityAccess.class */
public interface SecurityAccess {
    void renumProviders();

    List<String> getAliases(Provider.Service service);

    Provider.Service getService(Provider provider, String str);
}