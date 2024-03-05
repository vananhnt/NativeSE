package com.android.server.am;

import android.app.IThumbnailReceiver;
import java.util.HashSet;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: PendingThumbnailsRecord.class */
public final class PendingThumbnailsRecord {
    final IThumbnailReceiver receiver;
    final HashSet<ActivityRecord> pendingRecords = new HashSet<>();
    boolean finished = false;

    PendingThumbnailsRecord(IThumbnailReceiver _receiver) {
        this.receiver = _receiver;
    }
}