package com.android.server.updates;

/* loaded from: CertPinInstallReceiver.class */
public class CertPinInstallReceiver extends ConfigUpdateInstallReceiver {
    public CertPinInstallReceiver() {
        super("/data/misc/keychain/", "pins", "metadata/", "version");
    }
}