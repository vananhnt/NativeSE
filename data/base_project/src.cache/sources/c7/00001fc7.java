package com.android.server.updates;

/* loaded from: CarrierProvisioningUrlsInstallReceiver.class */
public class CarrierProvisioningUrlsInstallReceiver extends ConfigUpdateInstallReceiver {
    public CarrierProvisioningUrlsInstallReceiver() {
        super("/data/misc/radio/", "provisioning_urls.xml", "metadata/", "version");
    }
}