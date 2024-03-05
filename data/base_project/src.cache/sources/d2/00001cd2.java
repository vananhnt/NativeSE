package com.android.server;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Binder;
import android.os.DropBoxManager;
import android.os.FileObserver;
import android.os.SystemProperties;
import android.provider.Settings;
import java.io.File;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* loaded from: SamplingProfilerService.class */
public class SamplingProfilerService extends Binder {
    private static final String TAG = "SamplingProfilerService";
    private static final boolean LOCAL_LOGV = false;
    public static final String SNAPSHOT_DIR = "/data/snapshots";
    private final Context mContext;
    private FileObserver snapshotObserver;

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.SamplingProfilerService.handleSnapshotFile(java.io.File, android.os.DropBoxManager):void, file: SamplingProfilerService.class
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
    /* JADX INFO: Access modifiers changed from: private */
    public void handleSnapshotFile(java.io.File r1, android.os.DropBoxManager r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.SamplingProfilerService.handleSnapshotFile(java.io.File, android.os.DropBoxManager):void, file: SamplingProfilerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.SamplingProfilerService.handleSnapshotFile(java.io.File, android.os.DropBoxManager):void");
    }

    public SamplingProfilerService(Context context) {
        this.mContext = context;
        registerSettingObserver(context);
        startWorking(context);
    }

    private void startWorking(Context context) {
        final DropBoxManager dropbox = (DropBoxManager) context.getSystemService(Context.DROPBOX_SERVICE);
        File[] snapshotFiles = new File("/data/snapshots").listFiles();
        for (int i = 0; snapshotFiles != null && i < snapshotFiles.length; i++) {
            handleSnapshotFile(snapshotFiles[i], dropbox);
        }
        this.snapshotObserver = new FileObserver("/data/snapshots", 4) { // from class: com.android.server.SamplingProfilerService.1
            @Override // android.os.FileObserver
            public void onEvent(int event, String path) {
                SamplingProfilerService.this.handleSnapshotFile(new File("/data/snapshots", path), dropbox);
            }
        };
        this.snapshotObserver.startWatching();
    }

    private void registerSettingObserver(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        contentResolver.registerContentObserver(Settings.Global.getUriFor(Settings.Global.SAMPLING_PROFILER_MS), false, new SamplingProfilerSettingsObserver(contentResolver));
    }

    @Override // android.os.Binder
    protected void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.DUMP, TAG);
        pw.println("SamplingProfilerService:");
        pw.println("Watching directory: /data/snapshots");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: SamplingProfilerService$SamplingProfilerSettingsObserver.class */
    public class SamplingProfilerSettingsObserver extends ContentObserver {
        private ContentResolver mContentResolver;

        public SamplingProfilerSettingsObserver(ContentResolver contentResolver) {
            super(null);
            this.mContentResolver = contentResolver;
            onChange(false);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            Integer samplingProfilerMs = Integer.valueOf(Settings.Global.getInt(this.mContentResolver, Settings.Global.SAMPLING_PROFILER_MS, 0));
            SystemProperties.set("persist.sys.profiler_ms", samplingProfilerMs.toString());
        }
    }
}