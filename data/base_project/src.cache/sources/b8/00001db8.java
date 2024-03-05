package com.android.server.am;

import android.os.Binder;
import android.os.SystemClock;
import android.util.TimeUtils;
import gov.nist.core.Separators;

/* loaded from: ContentProviderConnection.class */
public final class ContentProviderConnection extends Binder {
    public final ContentProviderRecord provider;
    public final ProcessRecord client;
    public final long createTime = SystemClock.elapsedRealtime();
    public int stableCount;
    public int unstableCount;
    public boolean waiting;
    public boolean dead;
    public int numStableIncs;
    public int numUnstableIncs;

    public ContentProviderConnection(ContentProviderRecord _provider, ProcessRecord _client) {
        this.provider = _provider;
        this.client = _client;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("ContentProviderConnection{");
        toShortString(sb);
        sb.append('}');
        return sb.toString();
    }

    public String toShortString() {
        StringBuilder sb = new StringBuilder(128);
        toShortString(sb);
        return sb.toString();
    }

    public String toClientString() {
        StringBuilder sb = new StringBuilder(128);
        toClientString(sb);
        return sb.toString();
    }

    public void toShortString(StringBuilder sb) {
        sb.append(this.provider.toShortString());
        sb.append("->");
        toClientString(sb);
    }

    public void toClientString(StringBuilder sb) {
        sb.append(this.client.toShortString());
        sb.append(" s");
        sb.append(this.stableCount);
        sb.append(Separators.SLASH);
        sb.append(this.numStableIncs);
        sb.append(" u");
        sb.append(this.unstableCount);
        sb.append(Separators.SLASH);
        sb.append(this.numUnstableIncs);
        if (this.waiting) {
            sb.append(" WAITING");
        }
        if (this.dead) {
            sb.append(" DEAD");
        }
        long nowReal = SystemClock.elapsedRealtime();
        sb.append(Separators.SP);
        TimeUtils.formatDuration(nowReal - this.createTime, sb);
    }
}