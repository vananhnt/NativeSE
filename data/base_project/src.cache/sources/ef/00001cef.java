package com.android.server;

import android.Manifest;
import android.app.ActivityManagerNative;
import android.app.IUserSwitchObserver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IRemoteCallback;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.Settings;
import android.service.textservice.SpellCheckerService;
import android.text.TextUtils;
import android.util.Slog;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.view.textservice.SpellCheckerInfo;
import android.view.textservice.SpellCheckerSubtype;
import com.android.internal.content.PackageMonitor;
import com.android.internal.textservice.ISpellCheckerService;
import com.android.internal.textservice.ISpellCheckerSession;
import com.android.internal.textservice.ISpellCheckerSessionListener;
import com.android.internal.textservice.ITextServicesManager;
import com.android.internal.textservice.ITextServicesSessionListener;
import gov.nist.core.Separators;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: TextServicesManagerService.class */
public class TextServicesManagerService extends ITextServicesManager.Stub {
    private static final String TAG = TextServicesManagerService.class.getSimpleName();
    private static final boolean DBG = false;
    private final Context mContext;
    private final TextServicesMonitor mMonitor;
    private final TextServicesSettings mSettings;
    private final HashMap<String, SpellCheckerInfo> mSpellCheckerMap = new HashMap<>();
    private final ArrayList<SpellCheckerInfo> mSpellCheckerList = new ArrayList<>();
    private final HashMap<String, SpellCheckerBindGroup> mSpellCheckerBindGroups = new HashMap<>();
    private boolean mSystemReady = false;

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.TextServicesManagerService.getSpellCheckerService(java.lang.String, java.lang.String, com.android.internal.textservice.ITextServicesSessionListener, com.android.internal.textservice.ISpellCheckerSessionListener, android.os.Bundle):void, file: TextServicesManagerService.class
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
    @Override // com.android.internal.textservice.ITextServicesManager
    public void getSpellCheckerService(java.lang.String r1, java.lang.String r2, com.android.internal.textservice.ITextServicesSessionListener r3, com.android.internal.textservice.ISpellCheckerSessionListener r4, android.os.Bundle r5) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.TextServicesManagerService.getSpellCheckerService(java.lang.String, java.lang.String, com.android.internal.textservice.ITextServicesSessionListener, com.android.internal.textservice.ISpellCheckerSessionListener, android.os.Bundle):void, file: TextServicesManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.TextServicesManagerService.getSpellCheckerService(java.lang.String, java.lang.String, com.android.internal.textservice.ITextServicesSessionListener, com.android.internal.textservice.ISpellCheckerSessionListener, android.os.Bundle):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.TextServicesManagerService.setCurrentSpellCheckerLocked(java.lang.String):void, file: TextServicesManagerService.class
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
    public void setCurrentSpellCheckerLocked(java.lang.String r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.TextServicesManagerService.setCurrentSpellCheckerLocked(java.lang.String):void, file: TextServicesManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.TextServicesManagerService.setCurrentSpellCheckerLocked(java.lang.String):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.TextServicesManagerService.setCurrentSpellCheckerSubtypeLocked(int):void, file: TextServicesManagerService.class
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
    private void setCurrentSpellCheckerSubtypeLocked(int r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.TextServicesManagerService.setCurrentSpellCheckerSubtypeLocked(int):void, file: TextServicesManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.TextServicesManagerService.setCurrentSpellCheckerSubtypeLocked(int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.TextServicesManagerService.setSpellCheckerEnabledLocked(boolean):void, file: TextServicesManagerService.class
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
    private void setSpellCheckerEnabledLocked(boolean r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.TextServicesManagerService.setSpellCheckerEnabledLocked(boolean):void, file: TextServicesManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.TextServicesManagerService.setSpellCheckerEnabledLocked(boolean):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.TextServicesManagerService.isSpellCheckerEnabledLocked():boolean, file: TextServicesManagerService.class
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
    private boolean isSpellCheckerEnabledLocked() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.TextServicesManagerService.isSpellCheckerEnabledLocked():boolean, file: TextServicesManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.TextServicesManagerService.isSpellCheckerEnabledLocked():boolean");
    }

    public void systemRunning() {
        if (!this.mSystemReady) {
            this.mSystemReady = true;
        }
    }

    public TextServicesManagerService(Context context) {
        this.mContext = context;
        int userId = 0;
        try {
            ActivityManagerNative.getDefault().registerUserSwitchObserver(new IUserSwitchObserver.Stub() { // from class: com.android.server.TextServicesManagerService.1
                @Override // android.app.IUserSwitchObserver
                public void onUserSwitching(int newUserId, IRemoteCallback reply) {
                    synchronized (TextServicesManagerService.this.mSpellCheckerMap) {
                        TextServicesManagerService.this.switchUserLocked(newUserId);
                    }
                    if (reply != null) {
                        try {
                            reply.sendResult(null);
                        } catch (RemoteException e) {
                        }
                    }
                }

                @Override // android.app.IUserSwitchObserver
                public void onUserSwitchComplete(int newUserId) throws RemoteException {
                }
            });
            userId = ActivityManagerNative.getDefault().getCurrentUser().id;
        } catch (RemoteException e) {
            Slog.w(TAG, "Couldn't get current user ID; guessing it's 0", e);
        }
        this.mMonitor = new TextServicesMonitor();
        this.mMonitor.register(context, null, true);
        this.mSettings = new TextServicesSettings(context.getContentResolver(), userId);
        switchUserLocked(userId);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void switchUserLocked(int userId) {
        SpellCheckerInfo sci;
        this.mSettings.setCurrentUserId(userId);
        unbindServiceLocked();
        buildSpellCheckerMapLocked(this.mContext, this.mSpellCheckerList, this.mSpellCheckerMap, this.mSettings);
        if (getCurrentSpellChecker(null) == null && (sci = findAvailSpellCheckerLocked(null, null)) != null) {
            setCurrentSpellCheckerLocked(sci.getId());
        }
    }

    /* loaded from: TextServicesManagerService$TextServicesMonitor.class */
    private class TextServicesMonitor extends PackageMonitor {
        private TextServicesMonitor() {
        }

        private boolean isChangingPackagesOfCurrentUser() {
            int userId = getChangingUserId();
            boolean retval = userId == TextServicesManagerService.this.mSettings.getCurrentUserId();
            return retval;
        }

        @Override // com.android.internal.content.PackageMonitor
        public void onSomePackagesChanged() {
            SpellCheckerInfo sci;
            if (isChangingPackagesOfCurrentUser()) {
                synchronized (TextServicesManagerService.this.mSpellCheckerMap) {
                    TextServicesManagerService.buildSpellCheckerMapLocked(TextServicesManagerService.this.mContext, TextServicesManagerService.this.mSpellCheckerList, TextServicesManagerService.this.mSpellCheckerMap, TextServicesManagerService.this.mSettings);
                    SpellCheckerInfo sci2 = TextServicesManagerService.this.getCurrentSpellChecker(null);
                    if (sci2 == null) {
                        return;
                    }
                    String packageName = sci2.getPackageName();
                    int change = isPackageDisappearing(packageName);
                    if ((change == 3 || change == 2 || isPackageModified(packageName)) && (sci = TextServicesManagerService.this.findAvailSpellCheckerLocked(null, packageName)) != null) {
                        TextServicesManagerService.this.setCurrentSpellCheckerLocked(sci.getId());
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void buildSpellCheckerMapLocked(Context context, ArrayList<SpellCheckerInfo> list, HashMap<String, SpellCheckerInfo> map, TextServicesSettings settings) {
        list.clear();
        map.clear();
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> services = pm.queryIntentServicesAsUser(new Intent(SpellCheckerService.SERVICE_INTERFACE), 128, settings.getCurrentUserId());
        int N = services.size();
        for (int i = 0; i < N; i++) {
            ResolveInfo ri = services.get(i);
            ServiceInfo si = ri.serviceInfo;
            ComponentName compName = new ComponentName(si.packageName, si.name);
            if (!Manifest.permission.BIND_TEXT_SERVICE.equals(si.permission)) {
                Slog.w(TAG, "Skipping text service " + compName + ": it does not require the permission " + Manifest.permission.BIND_TEXT_SERVICE);
            } else {
                try {
                    SpellCheckerInfo sci = new SpellCheckerInfo(context, ri);
                    if (sci.getSubtypeCount() <= 0) {
                        Slog.w(TAG, "Skipping text service " + compName + ": it does not contain subtypes.");
                    } else {
                        list.add(sci);
                        map.put(sci.getId(), sci);
                    }
                } catch (IOException e) {
                    Slog.w(TAG, "Unable to load the spell checker " + compName, e);
                } catch (XmlPullParserException e2) {
                    Slog.w(TAG, "Unable to load the spell checker " + compName, e2);
                }
            }
        }
    }

    private boolean calledFromValidUser() {
        int uid = Binder.getCallingUid();
        int userId = UserHandle.getUserId(uid);
        if (uid == 1000 || userId == this.mSettings.getCurrentUserId()) {
            return true;
        }
        Slog.w(TAG, "--- IPC called from background users. Ignore. \n" + getStackTrace());
        return false;
    }

    private boolean bindCurrentSpellCheckerService(Intent service, ServiceConnection conn, int flags) {
        if (service == null || conn == null) {
            Slog.e(TAG, "--- bind failed: service = " + service + ", conn = " + conn);
            return false;
        }
        return this.mContext.bindServiceAsUser(service, conn, flags, new UserHandle(this.mSettings.getCurrentUserId()));
    }

    private void unbindServiceLocked() {
        for (SpellCheckerBindGroup scbg : this.mSpellCheckerBindGroups.values()) {
            scbg.removeAll();
        }
        this.mSpellCheckerBindGroups.clear();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public SpellCheckerInfo findAvailSpellCheckerLocked(String locale, String prefPackage) {
        int spellCheckersCount = this.mSpellCheckerList.size();
        if (spellCheckersCount == 0) {
            Slog.w(TAG, "no available spell checker services found");
            return null;
        }
        if (prefPackage != null) {
            for (int i = 0; i < spellCheckersCount; i++) {
                SpellCheckerInfo sci = this.mSpellCheckerList.get(i);
                if (prefPackage.equals(sci.getPackageName())) {
                    return sci;
                }
            }
        }
        if (spellCheckersCount > 1) {
            Slog.w(TAG, "more than one spell checker service found, picking first");
        }
        return this.mSpellCheckerList.get(0);
    }

    @Override // com.android.internal.textservice.ITextServicesManager
    public SpellCheckerInfo getCurrentSpellChecker(String locale) {
        if (!calledFromValidUser()) {
            return null;
        }
        synchronized (this.mSpellCheckerMap) {
            String curSpellCheckerId = this.mSettings.getSelectedSpellChecker();
            if (TextUtils.isEmpty(curSpellCheckerId)) {
                return null;
            }
            return this.mSpellCheckerMap.get(curSpellCheckerId);
        }
    }

    @Override // com.android.internal.textservice.ITextServicesManager
    public SpellCheckerSubtype getCurrentSpellCheckerSubtype(String locale, boolean allowImplicitlySelectedSubtype) {
        int hashCode;
        InputMethodSubtype currentInputMethodSubtype;
        if (!calledFromValidUser()) {
            return null;
        }
        synchronized (this.mSpellCheckerMap) {
            String subtypeHashCodeStr = this.mSettings.getSelectedSpellCheckerSubtype();
            SpellCheckerInfo sci = getCurrentSpellChecker(null);
            if (sci == null || sci.getSubtypeCount() == 0) {
                return null;
            }
            if (!TextUtils.isEmpty(subtypeHashCodeStr)) {
                hashCode = Integer.valueOf(subtypeHashCodeStr).intValue();
            } else {
                hashCode = 0;
            }
            if (hashCode == 0 && !allowImplicitlySelectedSubtype) {
                return null;
            }
            String candidateLocale = null;
            if (hashCode == 0) {
                InputMethodManager imm = (InputMethodManager) this.mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null && (currentInputMethodSubtype = imm.getCurrentInputMethodSubtype()) != null) {
                    String localeString = currentInputMethodSubtype.getLocale();
                    if (!TextUtils.isEmpty(localeString)) {
                        candidateLocale = localeString;
                    }
                }
                if (candidateLocale == null) {
                    candidateLocale = this.mContext.getResources().getConfiguration().locale.toString();
                }
            }
            SpellCheckerSubtype candidate = null;
            for (int i = 0; i < sci.getSubtypeCount(); i++) {
                SpellCheckerSubtype scs = sci.getSubtypeAt(i);
                if (hashCode == 0) {
                    String scsLocale = scs.getLocale();
                    if (candidateLocale.equals(scsLocale)) {
                        return scs;
                    }
                    if (candidate == null && candidateLocale.length() >= 2 && scsLocale.length() >= 2 && candidateLocale.startsWith(scsLocale)) {
                        candidate = scs;
                    }
                } else if (scs.hashCode() == hashCode) {
                    return scs;
                }
            }
            return candidate;
        }
    }

    @Override // com.android.internal.textservice.ITextServicesManager
    public boolean isSpellCheckerEnabled() {
        boolean isSpellCheckerEnabledLocked;
        if (!calledFromValidUser()) {
            return false;
        }
        synchronized (this.mSpellCheckerMap) {
            isSpellCheckerEnabledLocked = isSpellCheckerEnabledLocked();
        }
        return isSpellCheckerEnabledLocked;
    }

    private void startSpellCheckerServiceInnerLocked(SpellCheckerInfo info, String locale, ITextServicesSessionListener tsListener, ISpellCheckerSessionListener scListener, int uid, Bundle bundle) {
        String sciId = info.getId();
        InternalServiceConnection connection = new InternalServiceConnection(sciId, locale, bundle);
        Intent serviceIntent = new Intent(SpellCheckerService.SERVICE_INTERFACE);
        serviceIntent.setComponent(info.getComponent());
        if (!bindCurrentSpellCheckerService(serviceIntent, connection, 1)) {
            Slog.e(TAG, "Failed to get a spell checker service.");
            return;
        }
        SpellCheckerBindGroup group = new SpellCheckerBindGroup(connection, tsListener, locale, scListener, uid, bundle);
        this.mSpellCheckerBindGroups.put(sciId, group);
    }

    @Override // com.android.internal.textservice.ITextServicesManager
    public SpellCheckerInfo[] getEnabledSpellCheckers() {
        if (!calledFromValidUser()) {
            return null;
        }
        return (SpellCheckerInfo[]) this.mSpellCheckerList.toArray(new SpellCheckerInfo[this.mSpellCheckerList.size()]);
    }

    @Override // com.android.internal.textservice.ITextServicesManager
    public void finishSpellCheckerService(ISpellCheckerSessionListener listener) {
        if (!calledFromValidUser()) {
            return;
        }
        synchronized (this.mSpellCheckerMap) {
            ArrayList<SpellCheckerBindGroup> removeList = new ArrayList<>();
            for (SpellCheckerBindGroup group : this.mSpellCheckerBindGroups.values()) {
                if (group != null) {
                    removeList.add(group);
                }
            }
            int removeSize = removeList.size();
            for (int i = 0; i < removeSize; i++) {
                removeList.get(i).removeListener(listener);
            }
        }
    }

    @Override // com.android.internal.textservice.ITextServicesManager
    public void setCurrentSpellChecker(String locale, String sciId) {
        if (!calledFromValidUser()) {
            return;
        }
        synchronized (this.mSpellCheckerMap) {
            if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) != 0) {
                throw new SecurityException("Requires permission android.permission.WRITE_SECURE_SETTINGS");
            }
            setCurrentSpellCheckerLocked(sciId);
        }
    }

    @Override // com.android.internal.textservice.ITextServicesManager
    public void setCurrentSpellCheckerSubtype(String locale, int hashCode) {
        if (!calledFromValidUser()) {
            return;
        }
        synchronized (this.mSpellCheckerMap) {
            if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) != 0) {
                throw new SecurityException("Requires permission android.permission.WRITE_SECURE_SETTINGS");
            }
            setCurrentSpellCheckerSubtypeLocked(hashCode);
        }
    }

    @Override // com.android.internal.textservice.ITextServicesManager
    public void setSpellCheckerEnabled(boolean enabled) {
        if (!calledFromValidUser()) {
            return;
        }
        synchronized (this.mSpellCheckerMap) {
            if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) != 0) {
                throw new SecurityException("Requires permission android.permission.WRITE_SECURE_SETTINGS");
            }
            setSpellCheckerEnabledLocked(enabled);
        }
    }

    @Override // android.os.Binder
    protected void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.DUMP) != 0) {
            pw.println("Permission Denial: can't dump TextServicesManagerService from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
            return;
        }
        synchronized (this.mSpellCheckerMap) {
            pw.println("Current Text Services Manager state:");
            pw.println("  Spell Checker Map:");
            for (Map.Entry<String, SpellCheckerInfo> ent : this.mSpellCheckerMap.entrySet()) {
                pw.print("    ");
                pw.print(ent.getKey());
                pw.println(Separators.COLON);
                SpellCheckerInfo info = ent.getValue();
                pw.print("      ");
                pw.print("id=");
                pw.println(info.getId());
                pw.print("      ");
                pw.print("comp=");
                pw.println(info.getComponent().toShortString());
                int NS = info.getSubtypeCount();
                for (int i = 0; i < NS; i++) {
                    SpellCheckerSubtype st = info.getSubtypeAt(i);
                    pw.print("      ");
                    pw.print("Subtype #");
                    pw.print(i);
                    pw.println(Separators.COLON);
                    pw.print("        ");
                    pw.print("locale=");
                    pw.println(st.getLocale());
                    pw.print("        ");
                    pw.print("extraValue=");
                    pw.println(st.getExtraValue());
                }
            }
            pw.println("");
            pw.println("  Spell Checker Bind Groups:");
            for (Map.Entry<String, SpellCheckerBindGroup> ent2 : this.mSpellCheckerBindGroups.entrySet()) {
                SpellCheckerBindGroup grp = ent2.getValue();
                pw.print("    ");
                pw.print(ent2.getKey());
                pw.print(Separators.SP);
                pw.print(grp);
                pw.println(Separators.COLON);
                pw.print("      ");
                pw.print("mInternalConnection=");
                pw.println(grp.mInternalConnection);
                pw.print("      ");
                pw.print("mSpellChecker=");
                pw.println(grp.mSpellChecker);
                pw.print("      ");
                pw.print("mBound=");
                pw.print(grp.mBound);
                pw.print(" mConnected=");
                pw.println(grp.mConnected);
                int NL = grp.mListeners.size();
                for (int i2 = 0; i2 < NL; i2++) {
                    InternalDeathRecipient listener = (InternalDeathRecipient) grp.mListeners.get(i2);
                    pw.print("      ");
                    pw.print("Listener #");
                    pw.print(i2);
                    pw.println(Separators.COLON);
                    pw.print("        ");
                    pw.print("mTsListener=");
                    pw.println(listener.mTsListener);
                    pw.print("        ");
                    pw.print("mScListener=");
                    pw.println(listener.mScListener);
                    pw.print("        ");
                    pw.print("mGroup=");
                    pw.println(listener.mGroup);
                    pw.print("        ");
                    pw.print("mScLocale=");
                    pw.print(listener.mScLocale);
                    pw.print(" mUid=");
                    pw.println(listener.mUid);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: TextServicesManagerService$SpellCheckerBindGroup.class */
    public class SpellCheckerBindGroup {
        private final InternalServiceConnection mInternalConnection;
        public ISpellCheckerService mSpellChecker;
        private final String TAG = SpellCheckerBindGroup.class.getSimpleName();
        private final CopyOnWriteArrayList<InternalDeathRecipient> mListeners = new CopyOnWriteArrayList<>();
        public boolean mBound = true;
        public boolean mConnected = false;

        public SpellCheckerBindGroup(InternalServiceConnection connection, ITextServicesSessionListener listener, String locale, ISpellCheckerSessionListener scListener, int uid, Bundle bundle) {
            this.mInternalConnection = connection;
            addListener(listener, locale, scListener, uid, bundle);
        }

        public void onServiceConnected(ISpellCheckerService spellChecker) {
            Iterator i$ = this.mListeners.iterator();
            while (i$.hasNext()) {
                InternalDeathRecipient listener = i$.next();
                try {
                    ISpellCheckerSession session = spellChecker.getISpellCheckerSession(listener.mScLocale, listener.mScListener, listener.mBundle);
                    synchronized (TextServicesManagerService.this.mSpellCheckerMap) {
                        if (this.mListeners.contains(listener)) {
                            listener.mTsListener.onServiceConnected(session);
                        }
                    }
                } catch (RemoteException e) {
                    Slog.e(this.TAG, "Exception in getting the spell checker session.Reconnect to the spellchecker. ", e);
                    removeAll();
                    return;
                }
            }
            synchronized (TextServicesManagerService.this.mSpellCheckerMap) {
                this.mSpellChecker = spellChecker;
                this.mConnected = true;
            }
        }

        public InternalDeathRecipient addListener(ITextServicesSessionListener tsListener, String locale, ISpellCheckerSessionListener scListener, int uid, Bundle bundle) {
            InternalDeathRecipient recipient = null;
            synchronized (TextServicesManagerService.this.mSpellCheckerMap) {
                try {
                    int size = this.mListeners.size();
                    for (int i = 0; i < size; i++) {
                        if (this.mListeners.get(i).hasSpellCheckerListener(scListener)) {
                            return null;
                        }
                    }
                    recipient = new InternalDeathRecipient(this, tsListener, locale, scListener, uid, bundle);
                    scListener.asBinder().linkToDeath(recipient, 0);
                    this.mListeners.add(recipient);
                } catch (RemoteException e) {
                }
                cleanLocked();
                return recipient;
            }
        }

        public void removeListener(ISpellCheckerSessionListener listener) {
            synchronized (TextServicesManagerService.this.mSpellCheckerMap) {
                int size = this.mListeners.size();
                ArrayList<InternalDeathRecipient> removeList = new ArrayList<>();
                for (int i = 0; i < size; i++) {
                    InternalDeathRecipient tempRecipient = this.mListeners.get(i);
                    if (tempRecipient.hasSpellCheckerListener(listener)) {
                        removeList.add(tempRecipient);
                    }
                }
                int removeSize = removeList.size();
                for (int i2 = 0; i2 < removeSize; i2++) {
                    InternalDeathRecipient idr = removeList.get(i2);
                    idr.mScListener.asBinder().unlinkToDeath(idr, 0);
                    this.mListeners.remove(idr);
                }
                cleanLocked();
            }
        }

        private void cleanLocked() {
            if (this.mBound && this.mListeners.isEmpty()) {
                this.mBound = false;
                String sciId = this.mInternalConnection.mSciId;
                SpellCheckerBindGroup cur = (SpellCheckerBindGroup) TextServicesManagerService.this.mSpellCheckerBindGroups.get(sciId);
                if (cur == this) {
                    TextServicesManagerService.this.mSpellCheckerBindGroups.remove(sciId);
                }
                TextServicesManagerService.this.mContext.unbindService(this.mInternalConnection);
            }
        }

        public void removeAll() {
            Slog.e(this.TAG, "Remove the spell checker bind unexpectedly.");
            synchronized (TextServicesManagerService.this.mSpellCheckerMap) {
                int size = this.mListeners.size();
                for (int i = 0; i < size; i++) {
                    InternalDeathRecipient idr = this.mListeners.get(i);
                    idr.mScListener.asBinder().unlinkToDeath(idr, 0);
                }
                this.mListeners.clear();
                cleanLocked();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: TextServicesManagerService$InternalServiceConnection.class */
    public class InternalServiceConnection implements ServiceConnection {
        private final String mSciId;
        private final String mLocale;
        private final Bundle mBundle;

        public InternalServiceConnection(String id, String locale, Bundle bundle) {
            this.mSciId = id;
            this.mLocale = locale;
            this.mBundle = bundle;
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName name, IBinder service) {
            synchronized (TextServicesManagerService.this.mSpellCheckerMap) {
                onServiceConnectedInnerLocked(name, service);
            }
        }

        private void onServiceConnectedInnerLocked(ComponentName name, IBinder service) {
            ISpellCheckerService spellChecker = ISpellCheckerService.Stub.asInterface(service);
            SpellCheckerBindGroup group = (SpellCheckerBindGroup) TextServicesManagerService.this.mSpellCheckerBindGroups.get(this.mSciId);
            if (group != null && this == group.mInternalConnection) {
                group.onServiceConnected(spellChecker);
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName name) {
            synchronized (TextServicesManagerService.this.mSpellCheckerMap) {
                SpellCheckerBindGroup group = (SpellCheckerBindGroup) TextServicesManagerService.this.mSpellCheckerBindGroups.get(this.mSciId);
                if (group != null && this == group.mInternalConnection) {
                    TextServicesManagerService.this.mSpellCheckerBindGroups.remove(this.mSciId);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: TextServicesManagerService$InternalDeathRecipient.class */
    public class InternalDeathRecipient implements IBinder.DeathRecipient {
        public final ITextServicesSessionListener mTsListener;
        public final ISpellCheckerSessionListener mScListener;
        public final String mScLocale;
        private final SpellCheckerBindGroup mGroup;
        public final int mUid;
        public final Bundle mBundle;

        public InternalDeathRecipient(SpellCheckerBindGroup group, ITextServicesSessionListener tsListener, String scLocale, ISpellCheckerSessionListener scListener, int uid, Bundle bundle) {
            this.mTsListener = tsListener;
            this.mScListener = scListener;
            this.mScLocale = scLocale;
            this.mGroup = group;
            this.mUid = uid;
            this.mBundle = bundle;
        }

        public boolean hasSpellCheckerListener(ISpellCheckerSessionListener listener) {
            return listener.asBinder().equals(this.mScListener.asBinder());
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            this.mGroup.removeListener(this.mScListener);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: TextServicesManagerService$TextServicesSettings.class */
    public static class TextServicesSettings {
        private final ContentResolver mResolver;
        private int mCurrentUserId;

        public TextServicesSettings(ContentResolver resolver, int userId) {
            this.mResolver = resolver;
            this.mCurrentUserId = userId;
        }

        public void setCurrentUserId(int userId) {
            this.mCurrentUserId = userId;
        }

        public int getCurrentUserId() {
            return this.mCurrentUserId;
        }

        public void putSelectedSpellChecker(String sciId) {
            Settings.Secure.putStringForUser(this.mResolver, Settings.Secure.SELECTED_SPELL_CHECKER, sciId, this.mCurrentUserId);
        }

        public void putSelectedSpellCheckerSubtype(int hashCode) {
            Settings.Secure.putStringForUser(this.mResolver, Settings.Secure.SELECTED_SPELL_CHECKER_SUBTYPE, String.valueOf(hashCode), this.mCurrentUserId);
        }

        public void setSpellCheckerEnabled(boolean enabled) {
            Settings.Secure.putIntForUser(this.mResolver, Settings.Secure.SPELL_CHECKER_ENABLED, enabled ? 1 : 0, this.mCurrentUserId);
        }

        public String getSelectedSpellChecker() {
            return Settings.Secure.getStringForUser(this.mResolver, Settings.Secure.SELECTED_SPELL_CHECKER, this.mCurrentUserId);
        }

        public String getSelectedSpellCheckerSubtype() {
            return Settings.Secure.getStringForUser(this.mResolver, Settings.Secure.SELECTED_SPELL_CHECKER_SUBTYPE, this.mCurrentUserId);
        }

        public boolean isSpellCheckerEnabled() {
            return Settings.Secure.getIntForUser(this.mResolver, Settings.Secure.SPELL_CHECKER_ENABLED, 1, this.mCurrentUserId) == 1;
        }
    }

    private static String getStackTrace() {
        StringBuilder sb = new StringBuilder();
        try {
            throw new RuntimeException();
        } catch (RuntimeException e) {
            StackTraceElement[] frames = e.getStackTrace();
            for (int j = 1; j < frames.length; j++) {
                sb.append(frames[j].toString() + Separators.RETURN);
            }
            return sb.toString();
        }
    }
}