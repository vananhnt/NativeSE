package com.android.server.updates;

import android.util.Base64;
import java.io.IOException;

/* loaded from: TZInfoInstallReceiver.class */
public class TZInfoInstallReceiver extends ConfigUpdateInstallReceiver {
    public TZInfoInstallReceiver() {
        super("/data/misc/zoneinfo/", "tzdata", "metadata/", "version");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.server.updates.ConfigUpdateInstallReceiver
    public void install(byte[] encodedContent, int version) throws IOException {
        super.install(Base64.decode(encodedContent, 0), version);
    }
}