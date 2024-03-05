package com.android.server.net;

import android.net.NetworkIdentity;
import android.net.NetworkStats;
import android.net.NetworkStatsHistory;
import android.net.NetworkTemplate;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.FileRotator;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.Objects;
import com.google.android.collect.Lists;
import com.google.android.collect.Maps;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/* loaded from: NetworkStatsCollection.class */
public class NetworkStatsCollection implements FileRotator.Reader {
    private static final int FILE_MAGIC = 1095648596;
    private static final int VERSION_NETWORK_INIT = 1;
    private static final int VERSION_UID_INIT = 1;
    private static final int VERSION_UID_WITH_IDENT = 2;
    private static final int VERSION_UID_WITH_TAG = 3;
    private static final int VERSION_UID_WITH_SET = 4;
    private static final int VERSION_UNIFIED_INIT = 16;
    private HashMap<Key, NetworkStatsHistory> mStats = Maps.newHashMap();
    private final long mBucketDuration;
    private long mStartMillis;
    private long mEndMillis;
    private long mTotalBytes;
    private boolean mDirty;

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.net.NetworkStatsCollection.readLegacyNetwork(java.io.File):void, file: NetworkStatsCollection.class
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
    @java.lang.Deprecated
    public void readLegacyNetwork(java.io.File r1) throws java.io.IOException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.net.NetworkStatsCollection.readLegacyNetwork(java.io.File):void, file: NetworkStatsCollection.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.net.NetworkStatsCollection.readLegacyNetwork(java.io.File):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.net.NetworkStatsCollection.readLegacyUid(java.io.File, boolean):void, file: NetworkStatsCollection.class
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
    @java.lang.Deprecated
    public void readLegacyUid(java.io.File r1, boolean r2) throws java.io.IOException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.net.NetworkStatsCollection.readLegacyUid(java.io.File, boolean):void, file: NetworkStatsCollection.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.net.NetworkStatsCollection.readLegacyUid(java.io.File, boolean):void");
    }

    public NetworkStatsCollection(long bucketDuration) {
        this.mBucketDuration = bucketDuration;
        reset();
    }

    public void reset() {
        this.mStats.clear();
        this.mStartMillis = Long.MAX_VALUE;
        this.mEndMillis = Long.MIN_VALUE;
        this.mTotalBytes = 0L;
        this.mDirty = false;
    }

    public long getStartMillis() {
        return this.mStartMillis;
    }

    public long getFirstAtomicBucketMillis() {
        if (this.mStartMillis == Long.MAX_VALUE) {
            return Long.MAX_VALUE;
        }
        return this.mStartMillis + this.mBucketDuration;
    }

    public long getEndMillis() {
        return this.mEndMillis;
    }

    public long getTotalBytes() {
        return this.mTotalBytes;
    }

    public boolean isDirty() {
        return this.mDirty;
    }

    public void clearDirty() {
        this.mDirty = false;
    }

    public boolean isEmpty() {
        return this.mStartMillis == Long.MAX_VALUE && this.mEndMillis == Long.MIN_VALUE;
    }

    public NetworkStatsHistory getHistory(NetworkTemplate template, int uid, int set, int tag, int fields) {
        return getHistory(template, uid, set, tag, fields, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    public NetworkStatsHistory getHistory(NetworkTemplate template, int uid, int set, int tag, int fields, long start, long end) {
        NetworkStatsHistory combined = new NetworkStatsHistory(this.mBucketDuration, estimateBuckets(), fields);
        for (Map.Entry<Key, NetworkStatsHistory> entry : this.mStats.entrySet()) {
            Key key = entry.getKey();
            boolean setMatches = set == -1 || key.set == set;
            if (key.uid == uid && setMatches && key.tag == tag && templateMatches(template, key.ident)) {
                combined.recordHistory(entry.getValue(), start, end);
            }
        }
        return combined;
    }

    public NetworkStats getSummary(NetworkTemplate template, long start, long end) {
        long now = System.currentTimeMillis();
        NetworkStats stats = new NetworkStats(end - start, 24);
        NetworkStats.Entry entry = new NetworkStats.Entry();
        NetworkStatsHistory.Entry historyEntry = null;
        if (start == end) {
            return stats;
        }
        for (Map.Entry<Key, NetworkStatsHistory> mapEntry : this.mStats.entrySet()) {
            Key key = mapEntry.getKey();
            if (templateMatches(template, key.ident)) {
                NetworkStatsHistory history = mapEntry.getValue();
                historyEntry = history.getValues(start, end, now, historyEntry);
                entry.iface = NetworkStats.IFACE_ALL;
                entry.uid = key.uid;
                entry.set = key.set;
                entry.tag = key.tag;
                entry.rxBytes = historyEntry.rxBytes;
                entry.rxPackets = historyEntry.rxPackets;
                entry.txBytes = historyEntry.txBytes;
                entry.txPackets = historyEntry.txPackets;
                entry.operations = historyEntry.operations;
                if (!entry.isEmpty()) {
                    stats.combineValues(entry);
                }
            }
        }
        return stats;
    }

    public void recordData(NetworkIdentitySet ident, int uid, int set, int tag, long start, long end, NetworkStats.Entry entry) {
        NetworkStatsHistory history = findOrCreateHistory(ident, uid, set, tag);
        history.recordData(start, end, entry);
        noteRecordedHistory(history.getStart(), history.getEnd(), entry.rxBytes + entry.txBytes);
    }

    private void recordHistory(Key key, NetworkStatsHistory history) {
        if (history.size() == 0) {
            return;
        }
        noteRecordedHistory(history.getStart(), history.getEnd(), history.getTotalBytes());
        NetworkStatsHistory target = this.mStats.get(key);
        if (target == null) {
            target = new NetworkStatsHistory(history.getBucketDuration());
            this.mStats.put(key, target);
        }
        target.recordEntireHistory(history);
    }

    public void recordCollection(NetworkStatsCollection another) {
        for (Map.Entry<Key, NetworkStatsHistory> entry : another.mStats.entrySet()) {
            recordHistory(entry.getKey(), entry.getValue());
        }
    }

    private NetworkStatsHistory findOrCreateHistory(NetworkIdentitySet ident, int uid, int set, int tag) {
        Key key = new Key(ident, uid, set, tag);
        NetworkStatsHistory existing = this.mStats.get(key);
        NetworkStatsHistory updated = null;
        if (existing == null) {
            updated = new NetworkStatsHistory(this.mBucketDuration, 10);
        } else if (existing.getBucketDuration() != this.mBucketDuration) {
            updated = new NetworkStatsHistory(existing, this.mBucketDuration);
        }
        if (updated != null) {
            this.mStats.put(key, updated);
            return updated;
        }
        return existing;
    }

    @Override // com.android.internal.util.FileRotator.Reader
    public void read(InputStream in) throws IOException {
        read(new DataInputStream(in));
    }

    public void read(DataInputStream in) throws IOException {
        int magic = in.readInt();
        if (magic != FILE_MAGIC) {
            throw new ProtocolException("unexpected magic: " + magic);
        }
        int version = in.readInt();
        switch (version) {
            case 16:
                int identSize = in.readInt();
                for (int i = 0; i < identSize; i++) {
                    NetworkIdentitySet ident = new NetworkIdentitySet(in);
                    int size = in.readInt();
                    for (int j = 0; j < size; j++) {
                        int uid = in.readInt();
                        int set = in.readInt();
                        int tag = in.readInt();
                        Key key = new Key(ident, uid, set, tag);
                        NetworkStatsHistory history = new NetworkStatsHistory(in);
                        recordHistory(key, history);
                    }
                }
                return;
            default:
                throw new ProtocolException("unexpected version: " + version);
        }
    }

    public void write(DataOutputStream out) throws IOException {
        HashMap<NetworkIdentitySet, ArrayList<Key>> keysByIdent = Maps.newHashMap();
        for (Key key : this.mStats.keySet()) {
            ArrayList<Key> keys = keysByIdent.get(key.ident);
            if (keys == null) {
                keys = Lists.newArrayList();
                keysByIdent.put(key.ident, keys);
            }
            keys.add(key);
        }
        out.writeInt(FILE_MAGIC);
        out.writeInt(16);
        out.writeInt(keysByIdent.size());
        for (NetworkIdentitySet ident : keysByIdent.keySet()) {
            ArrayList<Key> keys2 = keysByIdent.get(ident);
            ident.writeToStream(out);
            out.writeInt(keys2.size());
            Iterator i$ = keys2.iterator();
            while (i$.hasNext()) {
                Key key2 = i$.next();
                NetworkStatsHistory history = this.mStats.get(key2);
                out.writeInt(key2.uid);
                out.writeInt(key2.set);
                out.writeInt(key2.tag);
                history.writeToStream(out);
            }
        }
        out.flush();
    }

    public void removeUids(int[] uids) {
        ArrayList<Key> knownKeys = Lists.newArrayList();
        knownKeys.addAll(this.mStats.keySet());
        Iterator i$ = knownKeys.iterator();
        while (i$.hasNext()) {
            Key key = i$.next();
            if (ArrayUtils.contains(uids, key.uid)) {
                if (key.tag == 0) {
                    NetworkStatsHistory uidHistory = this.mStats.get(key);
                    NetworkStatsHistory removedHistory = findOrCreateHistory(key.ident, -4, 0, 0);
                    removedHistory.recordEntireHistory(uidHistory);
                }
                this.mStats.remove(key);
                this.mDirty = true;
            }
        }
    }

    private void noteRecordedHistory(long startMillis, long endMillis, long totalBytes) {
        if (startMillis < this.mStartMillis) {
            this.mStartMillis = startMillis;
        }
        if (endMillis > this.mEndMillis) {
            this.mEndMillis = endMillis;
        }
        this.mTotalBytes += totalBytes;
        this.mDirty = true;
    }

    private int estimateBuckets() {
        return (int) (Math.min(this.mEndMillis - this.mStartMillis, 3024000000L) / this.mBucketDuration);
    }

    public void dump(IndentingPrintWriter pw) {
        ArrayList<Key> keys = Lists.newArrayList();
        keys.addAll(this.mStats.keySet());
        Collections.sort(keys);
        Iterator i$ = keys.iterator();
        while (i$.hasNext()) {
            Key key = i$.next();
            pw.print("ident=");
            pw.print(key.ident.toString());
            pw.print(" uid=");
            pw.print(key.uid);
            pw.print(" set=");
            pw.print(NetworkStats.setToString(key.set));
            pw.print(" tag=");
            pw.println(NetworkStats.tagToString(key.tag));
            NetworkStatsHistory history = this.mStats.get(key);
            pw.increaseIndent();
            history.dump(pw, true);
            pw.decreaseIndent();
        }
    }

    private static boolean templateMatches(NetworkTemplate template, NetworkIdentitySet identSet) {
        Iterator i$ = identSet.iterator();
        while (i$.hasNext()) {
            NetworkIdentity ident = i$.next();
            if (template.matches(ident)) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: NetworkStatsCollection$Key.class */
    public static class Key implements Comparable<Key> {
        public final NetworkIdentitySet ident;
        public final int uid;
        public final int set;
        public final int tag;
        private final int hashCode;

        public Key(NetworkIdentitySet ident, int uid, int set, int tag) {
            this.ident = ident;
            this.uid = uid;
            this.set = set;
            this.tag = tag;
            this.hashCode = Objects.hashCode(ident, Integer.valueOf(uid), Integer.valueOf(set), Integer.valueOf(tag));
        }

        public int hashCode() {
            return this.hashCode;
        }

        public boolean equals(Object obj) {
            if (obj instanceof Key) {
                Key key = (Key) obj;
                return this.uid == key.uid && this.set == key.set && this.tag == key.tag && Objects.equal(this.ident, key.ident);
            }
            return false;
        }

        @Override // java.lang.Comparable
        public int compareTo(Key another) {
            return Integer.compare(this.uid, another.uid);
        }
    }
}