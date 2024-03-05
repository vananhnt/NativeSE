package com.android.server.print;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Binder;
import android.os.UserHandle;
import android.print.IPrintManager;
import android.printservice.PrintService;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.SparseArray;
import com.android.internal.R;
import com.android.internal.content.PackageMonitor;
import com.android.internal.os.BackgroundThread;
import gov.nist.core.Separators;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/* loaded from: PrintManagerService.class */
public final class PrintManagerService extends IPrintManager.Stub {
    private static final char COMPONENT_NAME_SEPARATOR = ':';
    private static final String EXTRA_PRINT_SERVICE_COMPONENT_NAME = "EXTRA_PRINT_SERVICE_COMPONENT_NAME";
    private final Context mContext;
    private final Object mLock = new Object();
    private final SparseArray<UserState> mUserStates = new SparseArray<>();
    private int mCurrentUserId = 0;

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.PrintManagerService.print(java.lang.String, android.print.IPrintDocumentAdapter, android.print.PrintAttributes, java.lang.String, int, int):android.os.Bundle, file: PrintManagerService.class
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
    @Override // android.print.IPrintManager
    public android.os.Bundle print(java.lang.String r1, android.print.IPrintDocumentAdapter r2, android.print.PrintAttributes r3, java.lang.String r4, int r5, int r6) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.PrintManagerService.print(java.lang.String, android.print.IPrintDocumentAdapter, android.print.PrintAttributes, java.lang.String, int, int):android.os.Bundle, file: PrintManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.print.PrintManagerService.print(java.lang.String, android.print.IPrintDocumentAdapter, android.print.PrintAttributes, java.lang.String, int, int):android.os.Bundle");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.PrintManagerService.getPrintJobInfos(int, int):java.util.List<android.print.PrintJobInfo>, file: PrintManagerService.class
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
    @Override // android.print.IPrintManager
    public java.util.List<android.print.PrintJobInfo> getPrintJobInfos(int r1, int r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.PrintManagerService.getPrintJobInfos(int, int):java.util.List<android.print.PrintJobInfo>, file: PrintManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.print.PrintManagerService.getPrintJobInfos(int, int):java.util.List");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.PrintManagerService.getPrintJobInfo(android.print.PrintJobId, int, int):android.print.PrintJobInfo, file: PrintManagerService.class
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
    @Override // android.print.IPrintManager
    public android.print.PrintJobInfo getPrintJobInfo(android.print.PrintJobId r1, int r2, int r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.PrintManagerService.getPrintJobInfo(android.print.PrintJobId, int, int):android.print.PrintJobInfo, file: PrintManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.print.PrintManagerService.getPrintJobInfo(android.print.PrintJobId, int, int):android.print.PrintJobInfo");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.PrintManagerService.cancelPrintJob(android.print.PrintJobId, int, int):void, file: PrintManagerService.class
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
    @Override // android.print.IPrintManager
    public void cancelPrintJob(android.print.PrintJobId r1, int r2, int r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.PrintManagerService.cancelPrintJob(android.print.PrintJobId, int, int):void, file: PrintManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.print.PrintManagerService.cancelPrintJob(android.print.PrintJobId, int, int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.PrintManagerService.restartPrintJob(android.print.PrintJobId, int, int):void, file: PrintManagerService.class
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
    @Override // android.print.IPrintManager
    public void restartPrintJob(android.print.PrintJobId r1, int r2, int r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.PrintManagerService.restartPrintJob(android.print.PrintJobId, int, int):void, file: PrintManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.print.PrintManagerService.restartPrintJob(android.print.PrintJobId, int, int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.PrintManagerService.getEnabledPrintServices(int):java.util.List<android.printservice.PrintServiceInfo>, file: PrintManagerService.class
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
    @Override // android.print.IPrintManager
    public java.util.List<android.printservice.PrintServiceInfo> getEnabledPrintServices(int r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.PrintManagerService.getEnabledPrintServices(int):java.util.List<android.printservice.PrintServiceInfo>, file: PrintManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.print.PrintManagerService.getEnabledPrintServices(int):java.util.List");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.PrintManagerService.getInstalledPrintServices(int):java.util.List<android.printservice.PrintServiceInfo>, file: PrintManagerService.class
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
    @Override // android.print.IPrintManager
    public java.util.List<android.printservice.PrintServiceInfo> getInstalledPrintServices(int r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.PrintManagerService.getInstalledPrintServices(int):java.util.List<android.printservice.PrintServiceInfo>, file: PrintManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.print.PrintManagerService.getInstalledPrintServices(int):java.util.List");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.PrintManagerService.createPrinterDiscoverySession(android.print.IPrinterDiscoveryObserver, int):void, file: PrintManagerService.class
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
    @Override // android.print.IPrintManager
    public void createPrinterDiscoverySession(android.print.IPrinterDiscoveryObserver r1, int r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.PrintManagerService.createPrinterDiscoverySession(android.print.IPrinterDiscoveryObserver, int):void, file: PrintManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.print.PrintManagerService.createPrinterDiscoverySession(android.print.IPrinterDiscoveryObserver, int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.PrintManagerService.destroyPrinterDiscoverySession(android.print.IPrinterDiscoveryObserver, int):void, file: PrintManagerService.class
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
    @Override // android.print.IPrintManager
    public void destroyPrinterDiscoverySession(android.print.IPrinterDiscoveryObserver r1, int r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.PrintManagerService.destroyPrinterDiscoverySession(android.print.IPrinterDiscoveryObserver, int):void, file: PrintManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.print.PrintManagerService.destroyPrinterDiscoverySession(android.print.IPrinterDiscoveryObserver, int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.PrintManagerService.startPrinterDiscovery(android.print.IPrinterDiscoveryObserver, java.util.List<android.print.PrinterId>, int):void, file: PrintManagerService.class
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
    @Override // android.print.IPrintManager
    public void startPrinterDiscovery(android.print.IPrinterDiscoveryObserver r1, java.util.List<android.print.PrinterId> r2, int r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.PrintManagerService.startPrinterDiscovery(android.print.IPrinterDiscoveryObserver, java.util.List<android.print.PrinterId>, int):void, file: PrintManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.print.PrintManagerService.startPrinterDiscovery(android.print.IPrinterDiscoveryObserver, java.util.List, int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.PrintManagerService.stopPrinterDiscovery(android.print.IPrinterDiscoveryObserver, int):void, file: PrintManagerService.class
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
    @Override // android.print.IPrintManager
    public void stopPrinterDiscovery(android.print.IPrinterDiscoveryObserver r1, int r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.PrintManagerService.stopPrinterDiscovery(android.print.IPrinterDiscoveryObserver, int):void, file: PrintManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.print.PrintManagerService.stopPrinterDiscovery(android.print.IPrinterDiscoveryObserver, int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.PrintManagerService.validatePrinters(java.util.List<android.print.PrinterId>, int):void, file: PrintManagerService.class
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
    @Override // android.print.IPrintManager
    public void validatePrinters(java.util.List<android.print.PrinterId> r1, int r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.PrintManagerService.validatePrinters(java.util.List<android.print.PrinterId>, int):void, file: PrintManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.print.PrintManagerService.validatePrinters(java.util.List, int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.PrintManagerService.startPrinterStateTracking(android.print.PrinterId, int):void, file: PrintManagerService.class
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
    @Override // android.print.IPrintManager
    public void startPrinterStateTracking(android.print.PrinterId r1, int r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.PrintManagerService.startPrinterStateTracking(android.print.PrinterId, int):void, file: PrintManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.print.PrintManagerService.startPrinterStateTracking(android.print.PrinterId, int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.PrintManagerService.stopPrinterStateTracking(android.print.PrinterId, int):void, file: PrintManagerService.class
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
    @Override // android.print.IPrintManager
    public void stopPrinterStateTracking(android.print.PrinterId r1, int r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.PrintManagerService.stopPrinterStateTracking(android.print.PrinterId, int):void, file: PrintManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.print.PrintManagerService.stopPrinterStateTracking(android.print.PrinterId, int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.PrintManagerService.addPrintJobStateChangeListener(android.print.IPrintJobStateChangeListener, int, int):void, file: PrintManagerService.class
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
    @Override // android.print.IPrintManager
    public void addPrintJobStateChangeListener(android.print.IPrintJobStateChangeListener r1, int r2, int r3) throws android.os.RemoteException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.PrintManagerService.addPrintJobStateChangeListener(android.print.IPrintJobStateChangeListener, int, int):void, file: PrintManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.print.PrintManagerService.addPrintJobStateChangeListener(android.print.IPrintJobStateChangeListener, int, int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.PrintManagerService.removePrintJobStateChangeListener(android.print.IPrintJobStateChangeListener, int):void, file: PrintManagerService.class
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
    @Override // android.print.IPrintManager
    public void removePrintJobStateChangeListener(android.print.IPrintJobStateChangeListener r1, int r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.print.PrintManagerService.removePrintJobStateChangeListener(android.print.IPrintJobStateChangeListener, int):void, file: PrintManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.print.PrintManagerService.removePrintJobStateChangeListener(android.print.IPrintJobStateChangeListener, int):void");
    }

    public PrintManagerService(Context context) {
        this.mContext = context;
        registerContentObservers();
        registerBoradcastReceivers();
    }

    public void systemRuning() {
        BackgroundThread.getHandler().post(new Runnable() { // from class: com.android.server.print.PrintManagerService.1
            @Override // java.lang.Runnable
            public void run() {
                UserState userState;
                synchronized (PrintManagerService.this.mLock) {
                    userState = PrintManagerService.this.getCurrentUserStateLocked();
                    userState.updateIfNeededLocked();
                }
                userState.removeObsoletePrintJobs();
            }
        });
    }

    @Override // android.os.Binder
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.DUMP) != 0) {
            pw.println("Permission Denial: can't dump PrintManager from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
            return;
        }
        synchronized (this.mLock) {
            pw.println("PRINT MANAGER STATE (dumpsys print)");
            int userStateCount = this.mUserStates.size();
            for (int i = 0; i < userStateCount; i++) {
                UserState userState = this.mUserStates.get(i);
                userState.dump(fd, pw, "");
                pw.println();
            }
        }
    }

    private void registerContentObservers() {
        final Uri enabledPrintServicesUri = Settings.Secure.getUriFor(Settings.Secure.ENABLED_PRINT_SERVICES);
        ContentObserver observer = new ContentObserver(BackgroundThread.getHandler()) { // from class: com.android.server.print.PrintManagerService.2
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange, Uri uri) {
                if (enabledPrintServicesUri.equals(uri)) {
                    synchronized (PrintManagerService.this.mLock) {
                        UserState userState = PrintManagerService.this.getCurrentUserStateLocked();
                        userState.updateIfNeededLocked();
                    }
                }
            }
        };
        this.mContext.getContentResolver().registerContentObserver(enabledPrintServicesUri, false, observer, -1);
    }

    private void registerBoradcastReceivers() {
        PackageMonitor monitor = new PackageMonitor() { // from class: com.android.server.print.PrintManagerService.3
            @Override // com.android.internal.content.PackageMonitor
            public boolean onPackageChanged(String packageName, int uid, String[] components) {
                synchronized (PrintManagerService.this.mLock) {
                    UserState userState = PrintManagerService.this.getOrCreateUserStateLocked(getChangingUserId());
                    for (ComponentName componentName : userState.getEnabledServices()) {
                        if (packageName.equals(componentName.getPackageName())) {
                            userState.updateIfNeededLocked();
                            return true;
                        }
                    }
                    return false;
                }
            }

            @Override // com.android.internal.content.PackageMonitor
            public void onPackageRemoved(String packageName, int uid) {
                synchronized (PrintManagerService.this.mLock) {
                    UserState userState = PrintManagerService.this.getOrCreateUserStateLocked(getChangingUserId());
                    Iterator<ComponentName> iterator = userState.getEnabledServices().iterator();
                    while (iterator.hasNext()) {
                        ComponentName componentName = iterator.next();
                        if (packageName.equals(componentName.getPackageName())) {
                            iterator.remove();
                            persistComponentNamesToSettingLocked(Settings.Secure.ENABLED_PRINT_SERVICES, userState.getEnabledServices(), getChangingUserId());
                            userState.updateIfNeededLocked();
                            return;
                        }
                    }
                }
            }

            @Override // com.android.internal.content.PackageMonitor
            public boolean onHandleForceStop(Intent intent, String[] stoppedPackages, int uid, boolean doit) {
                synchronized (PrintManagerService.this.mLock) {
                    UserState userState = PrintManagerService.this.getOrCreateUserStateLocked(getChangingUserId());
                    boolean stoppedSomePackages = false;
                    for (ComponentName componentName : userState.getEnabledServices()) {
                        String componentPackage = componentName.getPackageName();
                        int len$ = stoppedPackages.length;
                        int i$ = 0;
                        while (true) {
                            if (i$ < len$) {
                                String stoppedPackage = stoppedPackages[i$];
                                if (!componentPackage.equals(stoppedPackage)) {
                                    i$++;
                                } else if (!doit) {
                                    return true;
                                } else {
                                    stoppedSomePackages = true;
                                }
                            }
                        }
                    }
                    if (stoppedSomePackages) {
                        userState.updateIfNeededLocked();
                    }
                    return false;
                }
            }

            @Override // com.android.internal.content.PackageMonitor
            public void onPackageAdded(String packageName, int uid) {
                Intent intent = new Intent(PrintService.SERVICE_INTERFACE);
                intent.setPackage(packageName);
                List<ResolveInfo> installedServices = PrintManagerService.this.mContext.getPackageManager().queryIntentServicesAsUser(intent, 4, getChangingUserId());
                if (installedServices == null) {
                    return;
                }
                int installedServiceCount = installedServices.size();
                for (int i = 0; i < installedServiceCount; i++) {
                    ServiceInfo serviceInfo = installedServices.get(i).serviceInfo;
                    ComponentName component = new ComponentName(serviceInfo.packageName, serviceInfo.name);
                    String label = serviceInfo.loadLabel(PrintManagerService.this.mContext.getPackageManager()).toString();
                    PrintManagerService.this.showEnableInstalledPrintServiceNotification(component, label, getChangingUserId());
                }
            }

            private void persistComponentNamesToSettingLocked(String settingName, Set<ComponentName> componentNames, int userId) {
                StringBuilder builder = new StringBuilder();
                for (ComponentName componentName : componentNames) {
                    if (builder.length() > 0) {
                        builder.append(':');
                    }
                    builder.append(componentName.flattenToShortString());
                }
                Settings.Secure.putStringForUser(PrintManagerService.this.mContext.getContentResolver(), settingName, builder.toString(), userId);
            }
        };
        monitor.register(this.mContext, BackgroundThread.getHandler().getLooper(), UserHandle.ALL, true);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_USER_SWITCHED);
        intentFilter.addAction(Intent.ACTION_USER_REMOVED);
        this.mContext.registerReceiverAsUser(new BroadcastReceiver() { // from class: com.android.server.print.PrintManagerService.4
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (Intent.ACTION_USER_SWITCHED.equals(action)) {
                    PrintManagerService.this.switchUser(intent.getIntExtra(Intent.EXTRA_USER_HANDLE, 0));
                } else if (Intent.ACTION_USER_REMOVED.equals(action)) {
                    PrintManagerService.this.removeUser(intent.getIntExtra(Intent.EXTRA_USER_HANDLE, 0));
                }
            }
        }, UserHandle.ALL, intentFilter, null, BackgroundThread.getHandler());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public UserState getCurrentUserStateLocked() {
        return getOrCreateUserStateLocked(this.mCurrentUserId);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public UserState getOrCreateUserStateLocked(int userId) {
        UserState userState = this.mUserStates.get(userId);
        if (userState == null) {
            userState = new UserState(this.mContext, userId, this.mLock);
            this.mUserStates.put(userId, userState);
        }
        return userState;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void switchUser(int newUserId) {
        synchronized (this.mLock) {
            if (newUserId == this.mCurrentUserId) {
                return;
            }
            this.mCurrentUserId = newUserId;
            UserState userState = this.mUserStates.get(this.mCurrentUserId);
            if (userState == null) {
                userState = getCurrentUserStateLocked();
                userState.updateIfNeededLocked();
            } else {
                userState.updateIfNeededLocked();
            }
            userState.removeObsoletePrintJobs();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeUser(int removedUserId) {
        synchronized (this.mLock) {
            UserState userState = this.mUserStates.get(removedUserId);
            if (userState != null) {
                userState.destroyLocked();
                this.mUserStates.remove(removedUserId);
            }
        }
    }

    private int resolveCallingAppEnforcingPermissions(int appId) {
        int callingUid = Binder.getCallingUid();
        if (callingUid == 0 || callingUid == 1000 || callingUid == 2000) {
            return appId;
        }
        int callingAppId = UserHandle.getAppId(callingUid);
        if (appId == callingAppId) {
            return appId;
        }
        if (this.mContext.checkCallingPermission("com.android.printspooler.permission.ACCESS_ALL_PRINT_JOBS") != 0) {
            throw new SecurityException("Call from app " + callingAppId + " as app " + appId + " without com.android.printspooler.permission.ACCESS_ALL_PRINT_JOBS");
        }
        return appId;
    }

    private int resolveCallingUserEnforcingPermissions(int userId) {
        int callingUid = Binder.getCallingUid();
        if (callingUid == 0 || callingUid == 1000 || callingUid == 2000) {
            return userId;
        }
        int callingUserId = UserHandle.getUserId(callingUid);
        if (callingUserId == userId) {
            return userId;
        }
        if (this.mContext.checkCallingPermission(Manifest.permission.INTERACT_ACROSS_USERS_FULL) != 0 || this.mContext.checkCallingPermission(Manifest.permission.INTERACT_ACROSS_USERS) != 0) {
            if (userId == -3) {
                return callingUserId;
            }
            throw new SecurityException("Call from user " + callingUserId + " as user " + userId + " without permission INTERACT_ACROSS_USERS or INTERACT_ACROSS_USERS_FULL not allowed.");
        } else if (userId == -2 || userId == -3) {
            return this.mCurrentUserId;
        } else {
            throw new IllegalArgumentException("Calling user can be changed to only UserHandle.USER_CURRENT or UserHandle.USER_CURRENT_OR_SELF.");
        }
    }

    private String resolveCallingPackageNameEnforcingSecurity(String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return null;
        }
        String[] packages = this.mContext.getPackageManager().getPackagesForUid(Binder.getCallingUid());
        for (String str : packages) {
            if (packageName.equals(str)) {
                return packageName;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showEnableInstalledPrintServiceNotification(ComponentName component, String label, int userId) {
        UserHandle userHandle = new UserHandle(userId);
        Intent intent = new Intent(Settings.ACTION_PRINT_SETTINGS);
        intent.putExtra(EXTRA_PRINT_SERVICE_COMPONENT_NAME, component.flattenToString());
        PendingIntent pendingIntent = PendingIntent.getActivityAsUser(this.mContext, 0, intent, 1342177280, null, userHandle);
        Notification.Builder builder = new Notification.Builder(this.mContext).setSmallIcon(R.drawable.ic_print).setContentTitle(this.mContext.getString(R.string.print_service_installed_title, label)).setContentText(this.mContext.getString(R.string.print_service_installed_message)).setContentIntent(pendingIntent).setWhen(System.currentTimeMillis()).setAutoCancel(true).setShowWhen(true);
        NotificationManager notificationManager = (NotificationManager) this.mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        String notificationTag = getClass().getName() + Separators.COLON + component.flattenToString();
        notificationManager.notifyAsUser(notificationTag, 0, builder.build(), userHandle);
    }
}