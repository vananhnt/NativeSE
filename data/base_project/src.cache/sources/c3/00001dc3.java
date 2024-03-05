package com.android.server.am;

import android.content.IIntentReceiver;
import android.content.IIntentSender;
import android.content.Intent;
import android.os.Bundle;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: PendingIntentRecord.class */
public final class PendingIntentRecord extends IIntentSender.Stub {
    final ActivityManagerService owner;
    final Key key;
    final int uid;
    String stringName;
    boolean sent = false;
    boolean canceled = false;
    final WeakReference<PendingIntentRecord> ref = new WeakReference<>(this);

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.PendingIntentRecord.finalize():void, file: PendingIntentRecord.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    @Override // android.os.Binder
    protected void finalize() throws java.lang.Throwable {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.PendingIntentRecord.finalize():void, file: PendingIntentRecord.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.PendingIntentRecord.finalize():void");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: PendingIntentRecord$Key.class */
    public static final class Key {
        final int type;
        final String packageName;
        final ActivityRecord activity;
        final String who;
        final int requestCode;
        final Intent requestIntent;
        final String requestResolvedType;
        final Bundle options;
        Intent[] allIntents;
        String[] allResolvedTypes;
        final int flags;
        final int hashCode;
        final int userId;
        private static final int ODD_PRIME_NUMBER = 37;

        Key(int _t, String _p, ActivityRecord _a, String _w, int _r, Intent[] _i, String[] _it, int _f, Bundle _o, int _userId) {
            this.type = _t;
            this.packageName = _p;
            this.activity = _a;
            this.who = _w;
            this.requestCode = _r;
            this.requestIntent = _i != null ? _i[_i.length - 1] : null;
            this.requestResolvedType = _it != null ? _it[_it.length - 1] : null;
            this.allIntents = _i;
            this.allResolvedTypes = _it;
            this.flags = _f;
            this.options = _o;
            this.userId = _userId;
            int hash = (37 * ((37 * ((37 * 23) + _f)) + _r)) + _userId;
            hash = _w != null ? (37 * hash) + _w.hashCode() : hash;
            hash = _a != null ? (37 * hash) + _a.hashCode() : hash;
            hash = this.requestIntent != null ? (37 * hash) + this.requestIntent.filterHashCode() : hash;
            this.hashCode = (37 * ((37 * (this.requestResolvedType != null ? (37 * hash) + this.requestResolvedType.hashCode() : hash)) + _p.hashCode())) + _t;
        }

        public boolean equals(Object otherObj) {
            if (otherObj == null) {
                return false;
            }
            try {
                Key other = (Key) otherObj;
                if (this.type != other.type || this.userId != other.userId || !this.packageName.equals(other.packageName) || this.activity != other.activity) {
                    return false;
                }
                if (this.who != other.who) {
                    if (this.who != null) {
                        if (!this.who.equals(other.who)) {
                            return false;
                        }
                    } else if (other.who != null) {
                        return false;
                    }
                }
                if (this.requestCode != other.requestCode) {
                    return false;
                }
                if (this.requestIntent != other.requestIntent) {
                    if (this.requestIntent != null) {
                        if (!this.requestIntent.filterEquals(other.requestIntent)) {
                            return false;
                        }
                    } else if (other.requestIntent != null) {
                        return false;
                    }
                }
                if (this.requestResolvedType != other.requestResolvedType) {
                    if (this.requestResolvedType != null) {
                        if (!this.requestResolvedType.equals(other.requestResolvedType)) {
                            return false;
                        }
                    } else if (other.requestResolvedType != null) {
                        return false;
                    }
                }
                if (this.flags != other.flags) {
                    return false;
                }
                return true;
            } catch (ClassCastException e) {
                return false;
            }
        }

        public int hashCode() {
            return this.hashCode;
        }

        public String toString() {
            return "Key{" + typeName() + " pkg=" + this.packageName + " intent=" + (this.requestIntent != null ? this.requestIntent.toShortString(false, true, false, false) : "<null>") + " flags=0x" + Integer.toHexString(this.flags) + " u=" + this.userId + "}";
        }

        String typeName() {
            switch (this.type) {
                case 1:
                    return "broadcastIntent";
                case 2:
                    return "startActivity";
                case 3:
                    return "activityResult";
                case 4:
                    return "startService";
                default:
                    return Integer.toString(this.type);
            }
        }
    }

    PendingIntentRecord(ActivityManagerService _owner, Key _k, int _u) {
        this.owner = _owner;
        this.key = _k;
        this.uid = _u;
    }

    @Override // android.content.IIntentSender
    public int send(int code, Intent intent, String resolvedType, IIntentReceiver finishedReceiver, String requiredPermission) {
        return sendInner(code, intent, resolvedType, finishedReceiver, requiredPermission, null, null, 0, 0, 0, null);
    }

    /* JADX WARN: Removed duplicated region for block: B:75:0x0282 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    int sendInner(int r15, android.content.Intent r16, java.lang.String r17, android.content.IIntentReceiver r18, java.lang.String r19, android.os.IBinder r20, java.lang.String r21, int r22, int r23, int r24, android.os.Bundle r25) {
        /*
            Method dump skipped, instructions count: 702
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.PendingIntentRecord.sendInner(int, android.content.Intent, java.lang.String, android.content.IIntentReceiver, java.lang.String, android.os.IBinder, java.lang.String, int, int, int, android.os.Bundle):int");
    }

    public void completeFinalize() {
        synchronized (this.owner) {
            WeakReference<PendingIntentRecord> current = (WeakReference) this.owner.mIntentSenderRecords.get(this.key);
            if (current == this.ref) {
                this.owner.mIntentSenderRecords.remove(this.key);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dump(PrintWriter pw, String prefix) {
        pw.print(prefix);
        pw.print("uid=");
        pw.print(this.uid);
        pw.print(" packageName=");
        pw.print(this.key.packageName);
        pw.print(" type=");
        pw.print(this.key.typeName());
        pw.print(" flags=0x");
        pw.println(Integer.toHexString(this.key.flags));
        if (this.key.activity != null || this.key.who != null) {
            pw.print(prefix);
            pw.print("activity=");
            pw.print(this.key.activity);
            pw.print(" who=");
            pw.println(this.key.who);
        }
        if (this.key.requestCode != 0 || this.key.requestResolvedType != null) {
            pw.print(prefix);
            pw.print("requestCode=");
            pw.print(this.key.requestCode);
            pw.print(" requestResolvedType=");
            pw.println(this.key.requestResolvedType);
        }
        if (this.key.requestIntent != null) {
            pw.print(prefix);
            pw.print("requestIntent=");
            pw.println(this.key.requestIntent.toShortString(false, true, true, true));
        }
        if (this.sent || this.canceled) {
            pw.print(prefix);
            pw.print("sent=");
            pw.print(this.sent);
            pw.print(" canceled=");
            pw.println(this.canceled);
        }
    }

    public String toString() {
        if (this.stringName != null) {
            return this.stringName;
        }
        StringBuilder sb = new StringBuilder(128);
        sb.append("PendingIntentRecord{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(' ');
        sb.append(this.key.packageName);
        sb.append(' ');
        sb.append(this.key.typeName());
        sb.append('}');
        String sb2 = sb.toString();
        this.stringName = sb2;
        return sb2;
    }
}