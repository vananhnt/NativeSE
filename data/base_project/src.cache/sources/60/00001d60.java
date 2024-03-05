package com.android.server.accounts;

import android.accounts.AuthenticatorDescription;
import android.content.pm.RegisteredServicesCache;
import android.content.pm.RegisteredServicesCacheListener;
import android.os.Handler;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Collection;

/* loaded from: IAccountAuthenticatorCache.class */
public interface IAccountAuthenticatorCache {
    RegisteredServicesCache.ServiceInfo<AuthenticatorDescription> getServiceInfo(AuthenticatorDescription authenticatorDescription, int i);

    Collection<RegisteredServicesCache.ServiceInfo<AuthenticatorDescription>> getAllServices(int i);

    void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr, int i);

    void setListener(RegisteredServicesCacheListener<AuthenticatorDescription> registeredServicesCacheListener, Handler handler);

    void invalidateCache(int i);
}