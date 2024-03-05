package com.android.server.location;

import android.os.Bundle;
import android.os.WorkSource;
import com.android.internal.location.ProviderProperties;
import com.android.internal.location.ProviderRequest;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* loaded from: LocationProviderInterface.class */
public interface LocationProviderInterface {
    String getName();

    void enable();

    void disable();

    boolean isEnabled();

    void setRequest(ProviderRequest providerRequest, WorkSource workSource);

    void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr);

    ProviderProperties getProperties();

    int getStatus(Bundle bundle);

    long getStatusUpdateTime();

    boolean sendExtraCommand(String str, Bundle bundle);
}