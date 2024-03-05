package com.android.server.am;

import android.content.IIntentReceiver;
import android.os.Binder;
import android.os.IBinder;
import android.util.PrintWriterPrinter;
import android.util.Printer;
import java.io.PrintWriter;
import java.util.ArrayList;

/* loaded from: ReceiverList.class */
final class ReceiverList extends ArrayList<BroadcastFilter> implements IBinder.DeathRecipient {
    final ActivityManagerService owner;
    public final IIntentReceiver receiver;
    public final ProcessRecord app;
    public final int pid;
    public final int uid;
    public final int userId;
    BroadcastRecord curBroadcast = null;
    boolean linkedToDeath = false;
    String stringName;

    ReceiverList(ActivityManagerService _owner, ProcessRecord _app, int _pid, int _uid, int _userId, IIntentReceiver _receiver) {
        this.owner = _owner;
        this.receiver = _receiver;
        this.app = _app;
        this.pid = _pid;
        this.uid = _uid;
        this.userId = _userId;
    }

    @Override // java.util.ArrayList, java.util.AbstractList, java.util.Collection
    public boolean equals(Object o) {
        return this == o;
    }

    @Override // java.util.ArrayList, java.util.AbstractList, java.util.Collection
    public int hashCode() {
        return System.identityHashCode(this);
    }

    @Override // android.os.IBinder.DeathRecipient
    public void binderDied() {
        this.linkedToDeath = false;
        this.owner.unregisterReceiver(this.receiver);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dumpLocal(PrintWriter pw, String prefix) {
        pw.print(prefix);
        pw.print("app=");
        pw.print(this.app != null ? this.app.toShortString() : null);
        pw.print(" pid=");
        pw.print(this.pid);
        pw.print(" uid=");
        pw.print(this.uid);
        pw.print(" user=");
        pw.println(this.userId);
        if (this.curBroadcast != null || this.linkedToDeath) {
            pw.print(prefix);
            pw.print("curBroadcast=");
            pw.print(this.curBroadcast);
            pw.print(" linkedToDeath=");
            pw.println(this.linkedToDeath);
        }
    }

    void dump(PrintWriter pw, String prefix) {
        Printer pr = new PrintWriterPrinter(pw);
        dumpLocal(pw, prefix);
        String p2 = prefix + "  ";
        int N = size();
        for (int i = 0; i < N; i++) {
            BroadcastFilter bf = get(i);
            pw.print(prefix);
            pw.print("Filter #");
            pw.print(i);
            pw.print(": BroadcastFilter{");
            pw.print(Integer.toHexString(System.identityHashCode(bf)));
            pw.println('}');
            bf.dumpInReceiverList(pw, pr, p2);
        }
    }

    @Override // java.util.AbstractCollection
    public String toString() {
        if (this.stringName != null) {
            return this.stringName;
        }
        StringBuilder sb = new StringBuilder(128);
        sb.append("ReceiverList{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(' ');
        sb.append(this.pid);
        sb.append(' ');
        sb.append(this.app != null ? this.app.processName : "(unknown name)");
        sb.append('/');
        sb.append(this.uid);
        sb.append("/u");
        sb.append(this.userId);
        sb.append(this.receiver.asBinder() instanceof Binder ? " local:" : " remote:");
        sb.append(Integer.toHexString(System.identityHashCode(this.receiver.asBinder())));
        sb.append('}');
        String sb2 = sb.toString();
        this.stringName = sb2;
        return sb2;
    }
}