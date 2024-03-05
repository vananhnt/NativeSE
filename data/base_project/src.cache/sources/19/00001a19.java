package com.android.internal.os;

import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.SystemProperties;
import android.util.Log;
import dalvik.system.profiler.BinaryHprofWriter;
import dalvik.system.profiler.SamplingProfiler;
import gov.nist.core.Separators;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import libcore.io.IoUtils;

/* loaded from: SamplingProfilerIntegration.class */
public class SamplingProfilerIntegration {
    private static final String TAG = "SamplingProfilerIntegration";
    public static final String SNAPSHOT_DIR = "/data/snapshots";
    private static final boolean enabled;
    private static final Executor snapshotWriter;
    private static SamplingProfiler samplingProfiler;
    private static long startMillis;
    private static final AtomicBoolean pending = new AtomicBoolean(false);
    private static final int samplingProfilerMilliseconds = SystemProperties.getInt("persist.sys.profiler_ms", 0);
    private static final int samplingProfilerDepth = SystemProperties.getInt("persist.sys.profiler_depth", 4);

    static {
        if (samplingProfilerMilliseconds > 0) {
            File dir = new File("/data/snapshots");
            dir.mkdirs();
            dir.setWritable(true, false);
            dir.setExecutable(true, false);
            if (dir.isDirectory()) {
                snapshotWriter = Executors.newSingleThreadExecutor(new ThreadFactory() { // from class: com.android.internal.os.SamplingProfilerIntegration.1
                    @Override // java.util.concurrent.ThreadFactory
                    public Thread newThread(Runnable r) {
                        return new Thread(r, SamplingProfilerIntegration.TAG);
                    }
                });
                enabled = true;
                Log.i(TAG, "Profiling enabled. Sampling interval ms: " + samplingProfilerMilliseconds);
                return;
            }
            snapshotWriter = null;
            enabled = true;
            Log.w(TAG, "Profiling setup failed. Could not create /data/snapshots");
            return;
        }
        snapshotWriter = null;
        enabled = false;
        Log.i(TAG, "Profiling disabled.");
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void start() {
        if (!enabled) {
            return;
        }
        if (samplingProfiler != null) {
            Log.e(TAG, "SamplingProfilerIntegration already started at " + new Date(startMillis));
            return;
        }
        ThreadGroup group = Thread.currentThread().getThreadGroup();
        SamplingProfiler.ThreadSet threadSet = SamplingProfiler.newThreadGroupThreadSet(group);
        samplingProfiler = new SamplingProfiler(samplingProfilerDepth, threadSet);
        samplingProfiler.start(samplingProfilerMilliseconds);
        startMillis = System.currentTimeMillis();
    }

    public static void writeSnapshot(final String processName, final PackageInfo packageInfo) {
        if (!enabled) {
            return;
        }
        if (samplingProfiler == null) {
            Log.e(TAG, "SamplingProfilerIntegration is not started");
        } else if (pending.compareAndSet(false, true)) {
            snapshotWriter.execute(new Runnable() { // from class: com.android.internal.os.SamplingProfilerIntegration.2
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        SamplingProfilerIntegration.writeSnapshotFile(String.this, packageInfo);
                        SamplingProfilerIntegration.pending.set(false);
                    } catch (Throwable th) {
                        SamplingProfilerIntegration.pending.set(false);
                        throw th;
                    }
                }
            });
        }
    }

    public static void writeZygoteSnapshot() {
        if (!enabled) {
            return;
        }
        writeSnapshotFile("zygote", null);
        samplingProfiler.shutdown();
        samplingProfiler = null;
        startMillis = 0L;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Type inference failed for: r0v18, types: [java.io.OutputStream, java.lang.AutoCloseable, java.io.BufferedOutputStream] */
    public static void writeSnapshotFile(String processName, PackageInfo packageInfo) {
        if (!enabled) {
            return;
        }
        samplingProfiler.stop();
        String name = processName.replaceAll(Separators.COLON, Separators.DOT);
        String path = "/data/snapshots/" + name + "-" + startMillis + ".snapshot";
        long start = System.currentTimeMillis();
        try {
            try {
                ?? bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(path));
                PrintStream out = new PrintStream((OutputStream) bufferedOutputStream);
                generateSnapshotHeader(name, packageInfo, out);
                if (out.checkError()) {
                    throw new IOException();
                }
                BinaryHprofWriter.write(samplingProfiler.getHprofData(), (OutputStream) bufferedOutputStream);
                IoUtils.closeQuietly((AutoCloseable) bufferedOutputStream);
                new File(path).setReadable(true, false);
                long elapsed = System.currentTimeMillis() - start;
                Log.i(TAG, "Wrote snapshot " + path + " in " + elapsed + "ms.");
                samplingProfiler.start(samplingProfilerMilliseconds);
            } catch (IOException e) {
                Log.e(TAG, "Error writing snapshot to " + path, e);
                IoUtils.closeQuietly((AutoCloseable) null);
            }
        } catch (Throwable th) {
            IoUtils.closeQuietly((AutoCloseable) null);
            throw th;
        }
    }

    private static void generateSnapshotHeader(String processName, PackageInfo packageInfo, PrintStream out) {
        out.println("Version: 3");
        out.println("Process: " + processName);
        if (packageInfo != null) {
            out.println("Package: " + packageInfo.packageName);
            out.println("Package-Version: " + packageInfo.versionCode);
        }
        out.println("Build: " + Build.FINGERPRINT);
        out.println();
    }
}