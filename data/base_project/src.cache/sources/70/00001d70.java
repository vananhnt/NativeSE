package com.android.server.am;

import android.app.ApplicationErrorReport;
import android.os.DropBoxManager;
import java.io.File;

/* loaded from: ActivityManagerService$17.class */
class ActivityManagerService$17 extends Thread {
    final /* synthetic */ String val$report;
    final /* synthetic */ StringBuilder val$sb;
    final /* synthetic */ File val$logFile;
    final /* synthetic */ ApplicationErrorReport.CrashInfo val$crashInfo;
    final /* synthetic */ String val$dropboxTag;
    final /* synthetic */ DropBoxManager val$dbox;
    final /* synthetic */ ActivityManagerService this$0;

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.ActivityManagerService$17.run():void, file: ActivityManagerService$17.class
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
    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.ActivityManagerService$17.run():void, file: ActivityManagerService$17.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ActivityManagerService$17.run():void");
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    ActivityManagerService$17(ActivityManagerService activityManagerService, String x0, String str, StringBuilder sb, File file, ApplicationErrorReport.CrashInfo crashInfo, String str2, DropBoxManager dropBoxManager) {
        super(x0);
        this.this$0 = activityManagerService;
        this.val$report = str;
        this.val$sb = sb;
        this.val$logFile = file;
        this.val$crashInfo = crashInfo;
        this.val$dropboxTag = str2;
        this.val$dbox = dropBoxManager;
    }
}