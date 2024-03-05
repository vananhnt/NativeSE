package com.android.internal.net;

import android.net.NetworkStats;
import android.os.StrictMode;
import android.os.SystemClock;
import com.android.internal.util.ProcFileReader;
import com.android.server.NetworkManagementSocketTagger;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ProtocolException;
import libcore.io.IoUtils;

/* loaded from: NetworkStatsFactory.class */
public class NetworkStatsFactory {
    private static final String TAG = "NetworkStatsFactory";
    private static final boolean USE_NATIVE_PARSING = true;
    private static final boolean SANITY_CHECK_NATIVE = false;
    private final File mStatsXtIfaceAll;
    private final File mStatsXtIfaceFmt;
    private final File mStatsXtUid;

    public static native int nativeReadNetworkStatsDetail(NetworkStats networkStats, String str, int i);

    public NetworkStatsFactory() {
        this(new File("/proc/"));
    }

    public NetworkStatsFactory(File procRoot) {
        this.mStatsXtIfaceAll = new File(procRoot, "net/xt_qtaguid/iface_stat_all");
        this.mStatsXtIfaceFmt = new File(procRoot, "net/xt_qtaguid/iface_stat_fmt");
        this.mStatsXtUid = new File(procRoot, "net/xt_qtaguid/stats");
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r10v0 */
    /* JADX WARN: Type inference failed for: r10v1 */
    /* JADX WARN: Type inference failed for: r10v2, types: [com.android.internal.util.ProcFileReader, java.lang.AutoCloseable] */
    public NetworkStats readNetworkStatsSummaryDev() throws IOException {
        StrictMode.ThreadPolicy savedPolicy = StrictMode.allowThreadDiskReads();
        NetworkStats stats = new NetworkStats(SystemClock.elapsedRealtime(), 6);
        NetworkStats.Entry entry = new NetworkStats.Entry();
        ?? r10 = 0;
        try {
            try {
                r10 = new ProcFileReader(new FileInputStream(this.mStatsXtIfaceAll));
                while (r10.hasMoreData()) {
                    entry.iface = r10.nextString();
                    entry.uid = -1;
                    entry.set = -1;
                    entry.tag = 0;
                    boolean active = r10.nextInt() != 0;
                    entry.rxBytes = r10.nextLong();
                    entry.rxPackets = r10.nextLong();
                    entry.txBytes = r10.nextLong();
                    entry.txPackets = r10.nextLong();
                    if (active) {
                        entry.rxBytes += r10.nextLong();
                        entry.rxPackets += r10.nextLong();
                        entry.txBytes += r10.nextLong();
                        entry.txPackets += r10.nextLong();
                    }
                    stats.addValues(entry);
                    r10.finishLine();
                }
                IoUtils.closeQuietly((AutoCloseable) r10);
                StrictMode.setThreadPolicy(savedPolicy);
                return stats;
            } catch (NullPointerException e) {
                throw new ProtocolException("problem parsing stats", e);
            } catch (NumberFormatException e2) {
                throw new ProtocolException("problem parsing stats", e2);
            }
        } catch (Throwable th) {
            IoUtils.closeQuietly((AutoCloseable) r10);
            StrictMode.setThreadPolicy(savedPolicy);
            throw th;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r10v0 */
    /* JADX WARN: Type inference failed for: r10v1 */
    /* JADX WARN: Type inference failed for: r10v2, types: [com.android.internal.util.ProcFileReader, java.lang.AutoCloseable] */
    public NetworkStats readNetworkStatsSummaryXt() throws IOException {
        StrictMode.ThreadPolicy savedPolicy = StrictMode.allowThreadDiskReads();
        if (this.mStatsXtIfaceFmt.exists()) {
            NetworkStats stats = new NetworkStats(SystemClock.elapsedRealtime(), 6);
            NetworkStats.Entry entry = new NetworkStats.Entry();
            ?? r10 = 0;
            try {
                try {
                    r10 = new ProcFileReader(new FileInputStream(this.mStatsXtIfaceFmt));
                    r10.finishLine();
                    while (r10.hasMoreData()) {
                        entry.iface = r10.nextString();
                        entry.uid = -1;
                        entry.set = -1;
                        entry.tag = 0;
                        entry.rxBytes = r10.nextLong();
                        entry.rxPackets = r10.nextLong();
                        entry.txBytes = r10.nextLong();
                        entry.txPackets = r10.nextLong();
                        stats.addValues(entry);
                        r10.finishLine();
                    }
                    IoUtils.closeQuietly((AutoCloseable) r10);
                    StrictMode.setThreadPolicy(savedPolicy);
                    return stats;
                } catch (NullPointerException e) {
                    throw new ProtocolException("problem parsing stats", e);
                } catch (NumberFormatException e2) {
                    throw new ProtocolException("problem parsing stats", e2);
                }
            } catch (Throwable th) {
                IoUtils.closeQuietly((AutoCloseable) r10);
                StrictMode.setThreadPolicy(savedPolicy);
                throw th;
            }
        }
        return null;
    }

    public NetworkStats readNetworkStatsDetail() throws IOException {
        return readNetworkStatsDetail(-1);
    }

    public NetworkStats readNetworkStatsDetail(int limitUid) throws IOException {
        NetworkStats stats = new NetworkStats(SystemClock.elapsedRealtime(), 0);
        if (nativeReadNetworkStatsDetail(stats, this.mStatsXtUid.getAbsolutePath(), limitUid) != 0) {
            throw new IOException("Failed to parse network stats");
        }
        return stats;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r13v0 */
    /* JADX WARN: Type inference failed for: r13v1 */
    /* JADX WARN: Type inference failed for: r13v2, types: [com.android.internal.util.ProcFileReader, java.lang.AutoCloseable] */
    public static NetworkStats javaReadNetworkStatsDetail(File detailPath, int limitUid) throws IOException {
        StrictMode.ThreadPolicy savedPolicy = StrictMode.allowThreadDiskReads();
        NetworkStats stats = new NetworkStats(SystemClock.elapsedRealtime(), 24);
        NetworkStats.Entry entry = new NetworkStats.Entry();
        int idx = 1;
        int lastIdx = 1;
        ?? r13 = 0;
        try {
            try {
                r13 = new ProcFileReader(new FileInputStream(detailPath));
                r13.finishLine();
                while (r13.hasMoreData()) {
                    idx = r13.nextInt();
                    if (idx != lastIdx + 1) {
                        throw new ProtocolException("inconsistent idx=" + idx + " after lastIdx=" + lastIdx);
                    }
                    lastIdx = idx;
                    entry.iface = r13.nextString();
                    entry.tag = NetworkManagementSocketTagger.kernelToTag(r13.nextString());
                    entry.uid = r13.nextInt();
                    entry.set = r13.nextInt();
                    entry.rxBytes = r13.nextLong();
                    entry.rxPackets = r13.nextLong();
                    entry.txBytes = r13.nextLong();
                    entry.txPackets = r13.nextLong();
                    if (limitUid == -1 || limitUid == entry.uid) {
                        stats.addValues(entry);
                    }
                    r13.finishLine();
                }
                IoUtils.closeQuietly((AutoCloseable) r13);
                StrictMode.setThreadPolicy(savedPolicy);
                return stats;
            } catch (NullPointerException e) {
                throw new ProtocolException("problem parsing idx " + idx, e);
            } catch (NumberFormatException e2) {
                throw new ProtocolException("problem parsing idx " + idx, e2);
            }
        } catch (Throwable th) {
            IoUtils.closeQuietly((AutoCloseable) r13);
            StrictMode.setThreadPolicy(savedPolicy);
            throw th;
        }
    }

    public void assertEquals(NetworkStats expected, NetworkStats actual) {
        if (expected.size() != actual.size()) {
            throw new AssertionError("Expected size " + expected.size() + ", actual size " + actual.size());
        }
        NetworkStats.Entry expectedRow = null;
        NetworkStats.Entry actualRow = null;
        for (int i = 0; i < expected.size(); i++) {
            expectedRow = expected.getValues(i, expectedRow);
            actualRow = actual.getValues(i, actualRow);
            if (!expectedRow.equals(actualRow)) {
                throw new AssertionError("Expected row " + i + ": " + expectedRow + ", actual row " + actualRow);
            }
        }
    }
}