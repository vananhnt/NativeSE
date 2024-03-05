package com.android.server;

import android.content.Context;
import android.os.Binder;
import android.os.StatFs;
import java.io.File;
import java.io.PrintWriter;

/* loaded from: DiskStatsService.class */
public class DiskStatsService extends Binder {
    private static final String TAG = "DiskStatsService";
    private final Context mContext;

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.DiskStatsService.dump(java.io.FileDescriptor, java.io.PrintWriter, java.lang.String[]):void, file: DiskStatsService.class
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
    @Override // android.os.Binder
    protected void dump(java.io.FileDescriptor r1, java.io.PrintWriter r2, java.lang.String[] r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.DiskStatsService.dump(java.io.FileDescriptor, java.io.PrintWriter, java.lang.String[]):void, file: DiskStatsService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.DiskStatsService.dump(java.io.FileDescriptor, java.io.PrintWriter, java.lang.String[]):void");
    }

    public DiskStatsService(Context context) {
        this.mContext = context;
    }

    private void reportFreeSpace(File path, String name, PrintWriter pw) {
        try {
            StatFs statfs = new StatFs(path.getPath());
            long bsize = statfs.getBlockSize();
            long avail = statfs.getAvailableBlocks();
            long total = statfs.getBlockCount();
            if (bsize <= 0 || total <= 0) {
                throw new IllegalArgumentException("Invalid stat: bsize=" + bsize + " avail=" + avail + " total=" + total);
            }
            pw.print(name);
            pw.print("-Free: ");
            pw.print((avail * bsize) / 1024);
            pw.print("K / ");
            pw.print((total * bsize) / 1024);
            pw.print("K total = ");
            pw.print((avail * 100) / total);
            pw.println("% free");
        } catch (IllegalArgumentException e) {
            pw.print(name);
            pw.print("-Error: ");
            pw.println(e.toString());
        }
    }
}