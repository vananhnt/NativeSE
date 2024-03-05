package com.android.server.am;

import android.content.Intent;
import android.os.IBinder;
import android.util.ArrayMap;
import java.io.PrintWriter;
import java.util.Iterator;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: IntentBindRecord.class */
public final class IntentBindRecord {
    final ServiceRecord service;
    final Intent.FilterComparison intent;
    final ArrayMap<ProcessRecord, AppBindRecord> apps = new ArrayMap<>();
    IBinder binder;
    boolean requested;
    boolean received;
    boolean hasBound;
    boolean doRebind;
    String stringName;

    void dump(PrintWriter pw, String prefix) {
        pw.print(prefix);
        pw.print("service=");
        pw.println(this.service);
        dumpInService(pw, prefix);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dumpInService(PrintWriter pw, String prefix) {
        pw.print(prefix);
        pw.print("intent={");
        pw.print(this.intent.getIntent().toShortString(false, true, false, false));
        pw.println('}');
        pw.print(prefix);
        pw.print("binder=");
        pw.println(this.binder);
        pw.print(prefix);
        pw.print("requested=");
        pw.print(this.requested);
        pw.print(" received=");
        pw.print(this.received);
        pw.print(" hasBound=");
        pw.print(this.hasBound);
        pw.print(" doRebind=");
        pw.println(this.doRebind);
        for (int i = 0; i < this.apps.size(); i++) {
            AppBindRecord a = this.apps.valueAt(i);
            pw.print(prefix);
            pw.print("* Client AppBindRecord{");
            pw.print(Integer.toHexString(System.identityHashCode(a)));
            pw.print(' ');
            pw.print(a.client);
            pw.println('}');
            a.dumpInIntentBind(pw, prefix + "  ");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public IntentBindRecord(ServiceRecord _service, Intent.FilterComparison _intent) {
        this.service = _service;
        this.intent = _intent;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int collectFlags() {
        int flags = 0;
        for (int i = this.apps.size() - 1; i >= 0; i--) {
            AppBindRecord app = this.apps.valueAt(i);
            if (app.connections.size() > 0) {
                Iterator i$ = app.connections.iterator();
                while (i$.hasNext()) {
                    ConnectionRecord conn = i$.next();
                    flags |= conn.flags;
                }
            }
        }
        return flags;
    }

    public String toString() {
        if (this.stringName != null) {
            return this.stringName;
        }
        StringBuilder sb = new StringBuilder(128);
        sb.append("IntentBindRecord{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(' ');
        if ((collectFlags() & 1) != 0) {
            sb.append("CR ");
        }
        sb.append(this.service.shortName);
        sb.append(':');
        if (this.intent != null) {
            this.intent.getIntent().toShortString(sb, false, false, false, false);
        }
        sb.append('}');
        String sb2 = sb.toString();
        this.stringName = sb2;
        return sb2;
    }
}