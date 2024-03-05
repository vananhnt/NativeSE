package com.android.server.net;

import android.net.NetworkStats;
import android.net.NetworkTemplate;
import android.os.DropBoxManager;
import android.util.Log;
import android.util.MathUtils;
import com.android.internal.util.FileRotator;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.Preconditions;
import com.google.android.collect.Sets;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

/* loaded from: NetworkStatsRecorder.class */
public class NetworkStatsRecorder {
    private static final String TAG = "NetworkStatsRecorder";
    private static final boolean LOGD = false;
    private static final boolean LOGV = false;
    private static final String TAG_NETSTATS_DUMP = "netstats_dump";
    private static final boolean DUMP_BEFORE_DELETE = true;
    private final FileRotator mRotator;
    private final NetworkStats.NonMonotonicObserver<String> mObserver;
    private final DropBoxManager mDropBox;
    private final String mCookie;
    private final long mBucketDuration;
    private final boolean mOnlyTags;
    private long mPersistThresholdBytes = 2097152;
    private NetworkStats mLastSnapshot;
    private final NetworkStatsCollection mPending;
    private final NetworkStatsCollection mSinceBoot;
    private final CombiningRewriter mPendingRewriter;
    private WeakReference<NetworkStatsCollection> mComplete;

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.net.NetworkStatsRecorder.recoverFromWtf():void, file: NetworkStatsRecorder.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private void recoverFromWtf() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.net.NetworkStatsRecorder.recoverFromWtf():void, file: NetworkStatsRecorder.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.net.NetworkStatsRecorder.recoverFromWtf():void");
    }

    public NetworkStatsRecorder(FileRotator rotator, NetworkStats.NonMonotonicObserver<String> observer, DropBoxManager dropBox, String cookie, long bucketDuration, boolean onlyTags) {
        this.mRotator = (FileRotator) Preconditions.checkNotNull(rotator, "missing FileRotator");
        this.mObserver = (NetworkStats.NonMonotonicObserver) Preconditions.checkNotNull(observer, "missing NonMonotonicObserver");
        this.mDropBox = (DropBoxManager) Preconditions.checkNotNull(dropBox, "missing DropBoxManager");
        this.mCookie = cookie;
        this.mBucketDuration = bucketDuration;
        this.mOnlyTags = onlyTags;
        this.mPending = new NetworkStatsCollection(bucketDuration);
        this.mSinceBoot = new NetworkStatsCollection(bucketDuration);
        this.mPendingRewriter = new CombiningRewriter(this.mPending);
    }

    public void setPersistThreshold(long thresholdBytes) {
        this.mPersistThresholdBytes = MathUtils.constrain(thresholdBytes, 1024L, 104857600L);
    }

    public void resetLocked() {
        this.mLastSnapshot = null;
        this.mPending.reset();
        this.mSinceBoot.reset();
        this.mComplete.clear();
    }

    public NetworkStats.Entry getTotalSinceBootLocked(NetworkTemplate template) {
        return this.mSinceBoot.getSummary(template, Long.MIN_VALUE, Long.MAX_VALUE).getTotal(null);
    }

    public NetworkStatsCollection getOrLoadCompleteLocked() {
        NetworkStatsCollection complete = this.mComplete != null ? this.mComplete.get() : null;
        if (complete == null) {
            try {
                complete = new NetworkStatsCollection(this.mBucketDuration);
                this.mRotator.readMatching(complete, Long.MIN_VALUE, Long.MAX_VALUE);
                complete.recordCollection(this.mPending);
                this.mComplete = new WeakReference<>(complete);
            } catch (IOException e) {
                Log.wtf(TAG, "problem completely reading network stats", e);
                recoverFromWtf();
            } catch (OutOfMemoryError e2) {
                Log.wtf(TAG, "problem completely reading network stats", e2);
                recoverFromWtf();
            }
        }
        return complete;
    }

    public void recordSnapshotLocked(NetworkStats snapshot, Map<String, NetworkIdentitySet> ifaceIdent, long currentTimeMillis) {
        HashSet<String> unknownIfaces = Sets.newHashSet();
        if (snapshot == null) {
            return;
        }
        if (this.mLastSnapshot == null) {
            this.mLastSnapshot = snapshot;
            return;
        }
        NetworkStatsCollection complete = this.mComplete != null ? this.mComplete.get() : null;
        NetworkStats delta = NetworkStats.subtract(snapshot, this.mLastSnapshot, this.mObserver, this.mCookie);
        long start = currentTimeMillis - delta.getElapsedRealtime();
        NetworkStats.Entry entry = null;
        for (int i = 0; i < delta.size(); i++) {
            entry = delta.getValues(i, entry);
            NetworkIdentitySet ident = ifaceIdent.get(entry.iface);
            if (ident == null) {
                unknownIfaces.add(entry.iface);
            } else if (!entry.isEmpty()) {
                if ((entry.tag == 0) != this.mOnlyTags) {
                    this.mPending.recordData(ident, entry.uid, entry.set, entry.tag, start, currentTimeMillis, entry);
                    if (this.mSinceBoot != null) {
                        this.mSinceBoot.recordData(ident, entry.uid, entry.set, entry.tag, start, currentTimeMillis, entry);
                    }
                    if (complete != null) {
                        complete.recordData(ident, entry.uid, entry.set, entry.tag, start, currentTimeMillis, entry);
                    }
                }
            }
        }
        this.mLastSnapshot = snapshot;
    }

    public void maybePersistLocked(long currentTimeMillis) {
        long pendingBytes = this.mPending.getTotalBytes();
        if (pendingBytes >= this.mPersistThresholdBytes) {
            forcePersistLocked(currentTimeMillis);
        } else {
            this.mRotator.maybeRotate(currentTimeMillis);
        }
    }

    public void forcePersistLocked(long currentTimeMillis) {
        if (this.mPending.isDirty()) {
            try {
                this.mRotator.rewriteActive(this.mPendingRewriter, currentTimeMillis);
                this.mRotator.maybeRotate(currentTimeMillis);
                this.mPending.reset();
            } catch (IOException e) {
                Log.wtf(TAG, "problem persisting pending stats", e);
                recoverFromWtf();
            } catch (OutOfMemoryError e2) {
                Log.wtf(TAG, "problem persisting pending stats", e2);
                recoverFromWtf();
            }
        }
    }

    public void removeUidsLocked(int[] uids) {
        try {
            this.mRotator.rewriteAll(new RemoveUidRewriter(this.mBucketDuration, uids));
        } catch (IOException e) {
            Log.wtf(TAG, "problem removing UIDs " + Arrays.toString(uids), e);
            recoverFromWtf();
        } catch (OutOfMemoryError e2) {
            Log.wtf(TAG, "problem removing UIDs " + Arrays.toString(uids), e2);
            recoverFromWtf();
        }
        this.mPending.removeUids(uids);
        this.mSinceBoot.removeUids(uids);
        if (this.mLastSnapshot != null) {
            this.mLastSnapshot = this.mLastSnapshot.withoutUids(uids);
        }
        NetworkStatsCollection complete = this.mComplete != null ? this.mComplete.get() : null;
        if (complete != null) {
            complete.removeUids(uids);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: NetworkStatsRecorder$CombiningRewriter.class */
    public static class CombiningRewriter implements FileRotator.Rewriter {
        private final NetworkStatsCollection mCollection;

        public CombiningRewriter(NetworkStatsCollection collection) {
            this.mCollection = (NetworkStatsCollection) Preconditions.checkNotNull(collection, "missing NetworkStatsCollection");
        }

        @Override // com.android.internal.util.FileRotator.Rewriter
        public void reset() {
        }

        @Override // com.android.internal.util.FileRotator.Reader
        public void read(InputStream in) throws IOException {
            this.mCollection.read(in);
        }

        @Override // com.android.internal.util.FileRotator.Rewriter
        public boolean shouldWrite() {
            return true;
        }

        @Override // com.android.internal.util.FileRotator.Writer
        public void write(OutputStream out) throws IOException {
            this.mCollection.write(new DataOutputStream(out));
            this.mCollection.reset();
        }
    }

    /* loaded from: NetworkStatsRecorder$RemoveUidRewriter.class */
    public static class RemoveUidRewriter implements FileRotator.Rewriter {
        private final NetworkStatsCollection mTemp;
        private final int[] mUids;

        public RemoveUidRewriter(long bucketDuration, int[] uids) {
            this.mTemp = new NetworkStatsCollection(bucketDuration);
            this.mUids = uids;
        }

        @Override // com.android.internal.util.FileRotator.Rewriter
        public void reset() {
            this.mTemp.reset();
        }

        @Override // com.android.internal.util.FileRotator.Reader
        public void read(InputStream in) throws IOException {
            this.mTemp.read(in);
            this.mTemp.clearDirty();
            this.mTemp.removeUids(this.mUids);
        }

        @Override // com.android.internal.util.FileRotator.Rewriter
        public boolean shouldWrite() {
            return this.mTemp.isDirty();
        }

        @Override // com.android.internal.util.FileRotator.Writer
        public void write(OutputStream out) throws IOException {
            this.mTemp.write(new DataOutputStream(out));
        }
    }

    public void importLegacyNetworkLocked(File file) throws IOException {
        this.mRotator.deleteAll();
        NetworkStatsCollection collection = new NetworkStatsCollection(this.mBucketDuration);
        collection.readLegacyNetwork(file);
        long startMillis = collection.getStartMillis();
        long endMillis = collection.getEndMillis();
        if (!collection.isEmpty()) {
            this.mRotator.rewriteActive(new CombiningRewriter(collection), startMillis);
            this.mRotator.maybeRotate(endMillis);
        }
    }

    public void importLegacyUidLocked(File file) throws IOException {
        this.mRotator.deleteAll();
        NetworkStatsCollection collection = new NetworkStatsCollection(this.mBucketDuration);
        collection.readLegacyUid(file, this.mOnlyTags);
        long startMillis = collection.getStartMillis();
        long endMillis = collection.getEndMillis();
        if (!collection.isEmpty()) {
            this.mRotator.rewriteActive(new CombiningRewriter(collection), startMillis);
            this.mRotator.maybeRotate(endMillis);
        }
    }

    public void dumpLocked(IndentingPrintWriter pw, boolean fullHistory) {
        pw.print("Pending bytes: ");
        pw.println(this.mPending.getTotalBytes());
        if (fullHistory) {
            pw.println("Complete history:");
            getOrLoadCompleteLocked().dump(pw);
            return;
        }
        pw.println("History since boot:");
        this.mSinceBoot.dump(pw);
    }
}