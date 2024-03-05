package com.android.server.content;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.IContentService;
import android.content.SyncRequest;
import android.database.IContentObserver;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.util.Log;
import android.util.Slog;
import android.util.SparseIntArray;
import gov.nist.core.Separators;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.security.InvalidParameterException;
import java.util.ArrayList;

/* loaded from: ContentService.class */
public final class ContentService extends IContentService.Stub {
    private static final String TAG = "ContentService";
    private Context mContext;
    private boolean mFactoryTest;
    private final ObserverNode mRootNode = new ObserverNode("");
    private SyncManager mSyncManager = null;
    private final Object mSyncManagerLock = new Object();

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.ContentService.dump(java.io.FileDescriptor, java.io.PrintWriter, java.lang.String[]):void, file: ContentService.class
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
    @Override // android.os.Binder
    protected synchronized void dump(java.io.FileDescriptor r1, java.io.PrintWriter r2, java.lang.String[] r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.ContentService.dump(java.io.FileDescriptor, java.io.PrintWriter, java.lang.String[]):void, file: ContentService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.ContentService.dump(java.io.FileDescriptor, java.io.PrintWriter, java.lang.String[]):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.ContentService.notifyChange(android.net.Uri, android.database.IContentObserver, boolean, boolean, int):void, file: ContentService.class
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
    @Override // android.content.IContentService
    public void notifyChange(android.net.Uri r1, android.database.IContentObserver r2, boolean r3, boolean r4, int r5) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.ContentService.notifyChange(android.net.Uri, android.database.IContentObserver, boolean, boolean, int):void, file: ContentService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.ContentService.notifyChange(android.net.Uri, android.database.IContentObserver, boolean, boolean, int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.ContentService.requestSync(android.accounts.Account, java.lang.String, android.os.Bundle):void, file: ContentService.class
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
    @Override // android.content.IContentService
    public void requestSync(android.accounts.Account r1, java.lang.String r2, android.os.Bundle r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.ContentService.requestSync(android.accounts.Account, java.lang.String, android.os.Bundle):void, file: ContentService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.ContentService.requestSync(android.accounts.Account, java.lang.String, android.os.Bundle):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.ContentService.sync(android.content.SyncRequest):void, file: ContentService.class
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
    @Override // android.content.IContentService
    public void sync(android.content.SyncRequest r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.ContentService.sync(android.content.SyncRequest):void, file: ContentService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.ContentService.sync(android.content.SyncRequest):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.ContentService.cancelSync(android.accounts.Account, java.lang.String):void, file: ContentService.class
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
    @Override // android.content.IContentService
    public void cancelSync(android.accounts.Account r1, java.lang.String r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.ContentService.cancelSync(android.accounts.Account, java.lang.String):void, file: ContentService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.ContentService.cancelSync(android.accounts.Account, java.lang.String):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.ContentService.getSyncAdapterTypes():android.content.SyncAdapterType[], file: ContentService.class
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
    @Override // android.content.IContentService
    public android.content.SyncAdapterType[] getSyncAdapterTypes() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.ContentService.getSyncAdapterTypes():android.content.SyncAdapterType[], file: ContentService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.ContentService.getSyncAdapterTypes():android.content.SyncAdapterType[]");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.ContentService.getSyncAutomatically(android.accounts.Account, java.lang.String):boolean, file: ContentService.class
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
    @Override // android.content.IContentService
    public boolean getSyncAutomatically(android.accounts.Account r1, java.lang.String r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.ContentService.getSyncAutomatically(android.accounts.Account, java.lang.String):boolean, file: ContentService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.ContentService.getSyncAutomatically(android.accounts.Account, java.lang.String):boolean");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.ContentService.setSyncAutomatically(android.accounts.Account, java.lang.String, boolean):void, file: ContentService.class
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
    @Override // android.content.IContentService
    public void setSyncAutomatically(android.accounts.Account r1, java.lang.String r2, boolean r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.ContentService.setSyncAutomatically(android.accounts.Account, java.lang.String, boolean):void, file: ContentService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.ContentService.setSyncAutomatically(android.accounts.Account, java.lang.String, boolean):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.ContentService.addPeriodicSync(android.accounts.Account, java.lang.String, android.os.Bundle, long):void, file: ContentService.class
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
    @Override // android.content.IContentService
    public void addPeriodicSync(android.accounts.Account r1, java.lang.String r2, android.os.Bundle r3, long r4) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.ContentService.addPeriodicSync(android.accounts.Account, java.lang.String, android.os.Bundle, long):void, file: ContentService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.ContentService.addPeriodicSync(android.accounts.Account, java.lang.String, android.os.Bundle, long):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.ContentService.removePeriodicSync(android.accounts.Account, java.lang.String, android.os.Bundle):void, file: ContentService.class
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
    @Override // android.content.IContentService
    public void removePeriodicSync(android.accounts.Account r1, java.lang.String r2, android.os.Bundle r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.ContentService.removePeriodicSync(android.accounts.Account, java.lang.String, android.os.Bundle):void, file: ContentService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.ContentService.removePeriodicSync(android.accounts.Account, java.lang.String, android.os.Bundle):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.ContentService.getPeriodicSyncs(android.accounts.Account, java.lang.String):java.util.List<android.content.PeriodicSync>, file: ContentService.class
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
    @Override // android.content.IContentService
    public java.util.List<android.content.PeriodicSync> getPeriodicSyncs(android.accounts.Account r1, java.lang.String r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.ContentService.getPeriodicSyncs(android.accounts.Account, java.lang.String):java.util.List<android.content.PeriodicSync>, file: ContentService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.ContentService.getPeriodicSyncs(android.accounts.Account, java.lang.String):java.util.List");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.ContentService.getIsSyncable(android.accounts.Account, java.lang.String):int, file: ContentService.class
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
    @Override // android.content.IContentService
    public int getIsSyncable(android.accounts.Account r1, java.lang.String r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.ContentService.getIsSyncable(android.accounts.Account, java.lang.String):int, file: ContentService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.ContentService.getIsSyncable(android.accounts.Account, java.lang.String):int");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.ContentService.setIsSyncable(android.accounts.Account, java.lang.String, int):void, file: ContentService.class
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
    @Override // android.content.IContentService
    public void setIsSyncable(android.accounts.Account r1, java.lang.String r2, int r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.ContentService.setIsSyncable(android.accounts.Account, java.lang.String, int):void, file: ContentService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.ContentService.setIsSyncable(android.accounts.Account, java.lang.String, int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.ContentService.getMasterSyncAutomatically():boolean, file: ContentService.class
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
    @Override // android.content.IContentService
    public boolean getMasterSyncAutomatically() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.ContentService.getMasterSyncAutomatically():boolean, file: ContentService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.ContentService.getMasterSyncAutomatically():boolean");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.ContentService.setMasterSyncAutomatically(boolean):void, file: ContentService.class
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
    @Override // android.content.IContentService
    public void setMasterSyncAutomatically(boolean r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.ContentService.setMasterSyncAutomatically(boolean):void, file: ContentService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.ContentService.setMasterSyncAutomatically(boolean):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.ContentService.isSyncActive(android.accounts.Account, java.lang.String):boolean, file: ContentService.class
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
    @Override // android.content.IContentService
    public boolean isSyncActive(android.accounts.Account r1, java.lang.String r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.ContentService.isSyncActive(android.accounts.Account, java.lang.String):boolean, file: ContentService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.ContentService.isSyncActive(android.accounts.Account, java.lang.String):boolean");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.ContentService.getCurrentSyncs():java.util.List<android.content.SyncInfo>, file: ContentService.class
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
    @Override // android.content.IContentService
    public java.util.List<android.content.SyncInfo> getCurrentSyncs() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.ContentService.getCurrentSyncs():java.util.List<android.content.SyncInfo>, file: ContentService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.ContentService.getCurrentSyncs():java.util.List");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.ContentService.getSyncStatus(android.accounts.Account, java.lang.String):android.content.SyncStatusInfo, file: ContentService.class
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
    @Override // android.content.IContentService
    public android.content.SyncStatusInfo getSyncStatus(android.accounts.Account r1, java.lang.String r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.ContentService.getSyncStatus(android.accounts.Account, java.lang.String):android.content.SyncStatusInfo, file: ContentService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.ContentService.getSyncStatus(android.accounts.Account, java.lang.String):android.content.SyncStatusInfo");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.ContentService.isSyncPending(android.accounts.Account, java.lang.String):boolean, file: ContentService.class
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
    @Override // android.content.IContentService
    public boolean isSyncPending(android.accounts.Account r1, java.lang.String r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.ContentService.isSyncPending(android.accounts.Account, java.lang.String):boolean, file: ContentService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.ContentService.isSyncPending(android.accounts.Account, java.lang.String):boolean");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.ContentService.addStatusChangeListener(int, android.content.ISyncStatusObserver):void, file: ContentService.class
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
    @Override // android.content.IContentService
    public void addStatusChangeListener(int r1, android.content.ISyncStatusObserver r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.ContentService.addStatusChangeListener(int, android.content.ISyncStatusObserver):void, file: ContentService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.ContentService.addStatusChangeListener(int, android.content.ISyncStatusObserver):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.ContentService.removeStatusChangeListener(android.content.ISyncStatusObserver):void, file: ContentService.class
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
    @Override // android.content.IContentService
    public void removeStatusChangeListener(android.content.ISyncStatusObserver r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.content.ContentService.removeStatusChangeListener(android.content.ISyncStatusObserver):void, file: ContentService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.ContentService.removeStatusChangeListener(android.content.ISyncStatusObserver):void");
    }

    private SyncManager getSyncManager() {
        SyncManager syncManager;
        if (SystemProperties.getBoolean("config.disable_network", false)) {
            return null;
        }
        synchronized (this.mSyncManagerLock) {
            try {
                if (this.mSyncManager == null) {
                    this.mSyncManager = new SyncManager(this.mContext, this.mFactoryTest);
                }
            } catch (SQLiteException e) {
                Log.e(TAG, "Can't create SyncManager", e);
            }
            syncManager = this.mSyncManager;
        }
        return syncManager;
    }

    @Override // android.content.IContentService.Stub, android.os.Binder
    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        try {
            return super.onTransact(code, data, reply, flags);
        } catch (RuntimeException e) {
            if (!(e instanceof SecurityException)) {
                Slog.wtf(TAG, "Content Service Crash", e);
            }
            throw e;
        }
    }

    ContentService(Context context, boolean factoryTest) {
        this.mContext = context;
        this.mFactoryTest = factoryTest;
    }

    public void systemReady() {
        getSyncManager();
    }

    @Override // android.content.IContentService
    public void registerContentObserver(Uri uri, boolean notifyForDescendants, IContentObserver observer, int userHandle) {
        if (observer == null || uri == null) {
            throw new IllegalArgumentException("You must pass a valid uri and observer");
        }
        int callingUser = UserHandle.getCallingUserId();
        if (callingUser != userHandle) {
            this.mContext.enforceCallingOrSelfPermission(Manifest.permission.INTERACT_ACROSS_USERS_FULL, "no permission to observe other users' provider view");
        }
        if (userHandle < 0) {
            if (userHandle == -2) {
                userHandle = ActivityManager.getCurrentUser();
            } else if (userHandle != -1) {
                throw new InvalidParameterException("Bad user handle for registerContentObserver: " + userHandle);
            }
        }
        synchronized (this.mRootNode) {
            this.mRootNode.addObserverLocked(uri, observer, notifyForDescendants, this.mRootNode, Binder.getCallingUid(), Binder.getCallingPid(), userHandle);
        }
    }

    public void registerContentObserver(Uri uri, boolean notifyForDescendants, IContentObserver observer) {
        registerContentObserver(uri, notifyForDescendants, observer, UserHandle.getCallingUserId());
    }

    @Override // android.content.IContentService
    public void unregisterContentObserver(IContentObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException("You must pass a valid observer");
        }
        synchronized (this.mRootNode) {
            this.mRootNode.removeObserverLocked(observer);
        }
    }

    public void notifyChange(Uri uri, IContentObserver observer, boolean observerWantsSelfNotifications, boolean syncToNetwork) {
        notifyChange(uri, observer, observerWantsSelfNotifications, syncToNetwork, UserHandle.getCallingUserId());
    }

    /* loaded from: ContentService$ObserverCall.class */
    public static final class ObserverCall {
        final ObserverNode mNode;
        final IContentObserver mObserver;
        final boolean mSelfChange;

        ObserverCall(ObserverNode node, IContentObserver observer, boolean selfChange) {
            this.mNode = node;
            this.mObserver = observer;
            this.mSelfChange = selfChange;
        }
    }

    public void removeSync(SyncRequest request) {
    }

    public static ContentService main(Context context, boolean factoryTest) {
        ContentService service = new ContentService(context, factoryTest);
        ServiceManager.addService("content", service);
        return service;
    }

    /* loaded from: ContentService$ObserverNode.class */
    public static final class ObserverNode {
        public static final int INSERT_TYPE = 0;
        public static final int UPDATE_TYPE = 1;
        public static final int DELETE_TYPE = 2;
        private String mName;
        private ArrayList<ObserverNode> mChildren = new ArrayList<>();
        private ArrayList<ObserverEntry> mObservers = new ArrayList<>();

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: ContentService$ObserverNode$ObserverEntry.class */
        public class ObserverEntry implements IBinder.DeathRecipient {
            public final IContentObserver observer;
            public final int uid;
            public final int pid;
            public final boolean notifyForDescendants;
            private final int userHandle;
            private final Object observersLock;

            public ObserverEntry(IContentObserver o, boolean n, Object observersLock, int _uid, int _pid, int _userHandle) {
                this.observersLock = observersLock;
                this.observer = o;
                this.uid = _uid;
                this.pid = _pid;
                this.userHandle = _userHandle;
                this.notifyForDescendants = n;
                try {
                    this.observer.asBinder().linkToDeath(this, 0);
                } catch (RemoteException e) {
                    binderDied();
                }
            }

            @Override // android.os.IBinder.DeathRecipient
            public void binderDied() {
                synchronized (this.observersLock) {
                    ObserverNode.this.removeObserverLocked(this.observer);
                }
            }

            public void dumpLocked(FileDescriptor fd, PrintWriter pw, String[] args, String name, String prefix, SparseIntArray pidCounts) {
                pidCounts.put(this.pid, pidCounts.get(this.pid) + 1);
                pw.print(prefix);
                pw.print(name);
                pw.print(": pid=");
                pw.print(this.pid);
                pw.print(" uid=");
                pw.print(this.uid);
                pw.print(" user=");
                pw.print(this.userHandle);
                pw.print(" target=");
                pw.println(Integer.toHexString(System.identityHashCode(this.observer != null ? this.observer.asBinder() : null)));
            }
        }

        static /* synthetic */ ArrayList access$000(ObserverNode x0) {
            return x0.mObservers;
        }

        public ObserverNode(String name) {
            this.mName = name;
        }

        public void dumpLocked(FileDescriptor fd, PrintWriter pw, String[] args, String name, String prefix, int[] counts, SparseIntArray pidCounts) {
            String innerName = null;
            if (this.mObservers.size() > 0) {
                if ("".equals(name)) {
                    innerName = this.mName;
                } else {
                    innerName = name + Separators.SLASH + this.mName;
                }
                for (int i = 0; i < this.mObservers.size(); i++) {
                    counts[1] = counts[1] + 1;
                    this.mObservers.get(i).dumpLocked(fd, pw, args, innerName, prefix, pidCounts);
                }
            }
            if (this.mChildren.size() > 0) {
                if (innerName == null) {
                    if ("".equals(name)) {
                        innerName = this.mName;
                    } else {
                        innerName = name + Separators.SLASH + this.mName;
                    }
                }
                for (int i2 = 0; i2 < this.mChildren.size(); i2++) {
                    counts[0] = counts[0] + 1;
                    this.mChildren.get(i2).dumpLocked(fd, pw, args, innerName, prefix, counts, pidCounts);
                }
            }
        }

        private String getUriSegment(Uri uri, int index) {
            if (uri != null) {
                if (index == 0) {
                    return uri.getAuthority();
                }
                return uri.getPathSegments().get(index - 1);
            }
            return null;
        }

        private int countUriSegments(Uri uri) {
            if (uri == null) {
                return 0;
            }
            return uri.getPathSegments().size() + 1;
        }

        public void addObserverLocked(Uri uri, IContentObserver observer, boolean notifyForDescendants, Object observersLock, int uid, int pid, int userHandle) {
            addObserverLocked(uri, 0, observer, notifyForDescendants, observersLock, uid, pid, userHandle);
        }

        private void addObserverLocked(Uri uri, int index, IContentObserver observer, boolean notifyForDescendants, Object observersLock, int uid, int pid, int userHandle) {
            if (index == countUriSegments(uri)) {
                this.mObservers.add(new ObserverEntry(observer, notifyForDescendants, observersLock, uid, pid, userHandle));
                return;
            }
            String segment = getUriSegment(uri, index);
            if (segment == null) {
                throw new IllegalArgumentException("Invalid Uri (" + uri + ") used for observer");
            }
            int N = this.mChildren.size();
            for (int i = 0; i < N; i++) {
                ObserverNode node = this.mChildren.get(i);
                if (node.mName.equals(segment)) {
                    node.addObserverLocked(uri, index + 1, observer, notifyForDescendants, observersLock, uid, pid, userHandle);
                    return;
                }
            }
            ObserverNode node2 = new ObserverNode(segment);
            this.mChildren.add(node2);
            node2.addObserverLocked(uri, index + 1, observer, notifyForDescendants, observersLock, uid, pid, userHandle);
        }

        public boolean removeObserverLocked(IContentObserver observer) {
            int size = this.mChildren.size();
            int i = 0;
            while (i < size) {
                boolean empty = this.mChildren.get(i).removeObserverLocked(observer);
                if (empty) {
                    this.mChildren.remove(i);
                    i--;
                    size--;
                }
                i++;
            }
            IBinder observerBinder = observer.asBinder();
            int size2 = this.mObservers.size();
            int i2 = 0;
            while (true) {
                if (i2 >= size2) {
                    break;
                }
                ObserverEntry entry = this.mObservers.get(i2);
                if (entry.observer.asBinder() != observerBinder) {
                    i2++;
                } else {
                    this.mObservers.remove(i2);
                    observerBinder.unlinkToDeath(entry, 0);
                    break;
                }
            }
            if (this.mChildren.size() == 0 && this.mObservers.size() == 0) {
                return true;
            }
            return false;
        }

        private void collectMyObserversLocked(boolean leaf, IContentObserver observer, boolean observerWantsSelfNotifications, int targetUserHandle, ArrayList<ObserverCall> calls) {
            int N = this.mObservers.size();
            IBinder observerBinder = observer == null ? null : observer.asBinder();
            for (int i = 0; i < N; i++) {
                ObserverEntry entry = this.mObservers.get(i);
                boolean selfChange = entry.observer.asBinder() == observerBinder;
                if ((!selfChange || observerWantsSelfNotifications) && ((targetUserHandle == -1 || entry.userHandle == -1 || targetUserHandle == entry.userHandle) && (leaf || (!leaf && entry.notifyForDescendants)))) {
                    calls.add(new ObserverCall(this, entry.observer, selfChange));
                }
            }
        }

        public void collectObserversLocked(Uri uri, int index, IContentObserver observer, boolean observerWantsSelfNotifications, int targetUserHandle, ArrayList<ObserverCall> calls) {
            String segment = null;
            int segmentCount = countUriSegments(uri);
            if (index >= segmentCount) {
                collectMyObserversLocked(true, observer, observerWantsSelfNotifications, targetUserHandle, calls);
            } else if (index < segmentCount) {
                segment = getUriSegment(uri, index);
                collectMyObserversLocked(false, observer, observerWantsSelfNotifications, targetUserHandle, calls);
            }
            int N = this.mChildren.size();
            for (int i = 0; i < N; i++) {
                ObserverNode node = this.mChildren.get(i);
                if (segment == null || node.mName.equals(segment)) {
                    node.collectObserversLocked(uri, index + 1, observer, observerWantsSelfNotifications, targetUserHandle, calls);
                    if (segment != null) {
                        return;
                    }
                }
            }
        }
    }
}