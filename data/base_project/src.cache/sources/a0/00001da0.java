package com.android.server.am;

import gov.nist.core.Separators;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: AppBindRecord.class */
public final class AppBindRecord {
    final ServiceRecord service;
    final IntentBindRecord intent;
    final ProcessRecord client;
    final HashSet<ConnectionRecord> connections = new HashSet<>();

    void dump(PrintWriter pw, String prefix) {
        pw.println(prefix + "service=" + this.service);
        pw.println(prefix + "client=" + this.client);
        dumpInIntentBind(pw, prefix);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dumpInIntentBind(PrintWriter pw, String prefix) {
        if (this.connections.size() > 0) {
            pw.println(prefix + "Per-process Connections:");
            Iterator<ConnectionRecord> it = this.connections.iterator();
            while (it.hasNext()) {
                ConnectionRecord c = it.next();
                pw.println(prefix + "  " + c);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AppBindRecord(ServiceRecord _service, IntentBindRecord _intent, ProcessRecord _client) {
        this.service = _service;
        this.intent = _intent;
        this.client = _client;
    }

    public String toString() {
        return "AppBindRecord{" + Integer.toHexString(System.identityHashCode(this)) + Separators.SP + this.service.shortName + Separators.COLON + this.client.processName + "}";
    }
}