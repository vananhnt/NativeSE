package com.android.server.am;

import android.content.ComponentName;
import android.os.UserHandle;
import android.util.SparseArray;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/* loaded from: ProviderMap.class */
public final class ProviderMap {
    private static final String TAG = "ProviderMap";
    private static final boolean DBG = false;
    private final ActivityManagerService mAm;
    private final HashMap<String, ContentProviderRecord> mSingletonByName = new HashMap<>();
    private final HashMap<ComponentName, ContentProviderRecord> mSingletonByClass = new HashMap<>();
    private final SparseArray<HashMap<String, ContentProviderRecord>> mProvidersByNamePerUser = new SparseArray<>();
    private final SparseArray<HashMap<ComponentName, ContentProviderRecord>> mProvidersByClassPerUser = new SparseArray<>();

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.ProviderMap.dumpProvider(java.lang.String, java.io.FileDescriptor, java.io.PrintWriter, com.android.server.am.ContentProviderRecord, java.lang.String[], boolean):void, file: ProviderMap.class
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
    private void dumpProvider(java.lang.String r1, java.io.FileDescriptor r2, java.io.PrintWriter r3, com.android.server.am.ContentProviderRecord r4, java.lang.String[] r5, boolean r6) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.ProviderMap.dumpProvider(java.lang.String, java.io.FileDescriptor, java.io.PrintWriter, com.android.server.am.ContentProviderRecord, java.lang.String[], boolean):void, file: ProviderMap.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ProviderMap.dumpProvider(java.lang.String, java.io.FileDescriptor, java.io.PrintWriter, com.android.server.am.ContentProviderRecord, java.lang.String[], boolean):void");
    }

    ProviderMap(ActivityManagerService am) {
        this.mAm = am;
    }

    ContentProviderRecord getProviderByName(String name) {
        return getProviderByName(name, -1);
    }

    ContentProviderRecord getProviderByName(String name, int userId) {
        ContentProviderRecord record = this.mSingletonByName.get(name);
        if (record != null) {
            return record;
        }
        return getProvidersByName(userId).get(name);
    }

    ContentProviderRecord getProviderByClass(ComponentName name) {
        return getProviderByClass(name, -1);
    }

    ContentProviderRecord getProviderByClass(ComponentName name, int userId) {
        ContentProviderRecord record = this.mSingletonByClass.get(name);
        if (record != null) {
            return record;
        }
        return getProvidersByClass(userId).get(name);
    }

    void putProviderByName(String name, ContentProviderRecord record) {
        if (record.singleton) {
            this.mSingletonByName.put(name, record);
            return;
        }
        int userId = UserHandle.getUserId(record.appInfo.uid);
        getProvidersByName(userId).put(name, record);
    }

    void putProviderByClass(ComponentName name, ContentProviderRecord record) {
        if (record.singleton) {
            this.mSingletonByClass.put(name, record);
            return;
        }
        int userId = UserHandle.getUserId(record.appInfo.uid);
        getProvidersByClass(userId).put(name, record);
    }

    void removeProviderByName(String name, int userId) {
        if (this.mSingletonByName.containsKey(name)) {
            this.mSingletonByName.remove(name);
        } else if (userId < 0) {
            throw new IllegalArgumentException("Bad user " + userId);
        } else {
            HashMap<String, ContentProviderRecord> map = getProvidersByName(userId);
            map.remove(name);
            if (map.size() == 0) {
                this.mProvidersByNamePerUser.remove(userId);
            }
        }
    }

    void removeProviderByClass(ComponentName name, int userId) {
        if (this.mSingletonByClass.containsKey(name)) {
            this.mSingletonByClass.remove(name);
        } else if (userId < 0) {
            throw new IllegalArgumentException("Bad user " + userId);
        } else {
            HashMap<ComponentName, ContentProviderRecord> map = getProvidersByClass(userId);
            map.remove(name);
            if (map.size() == 0) {
                this.mProvidersByClassPerUser.remove(userId);
            }
        }
    }

    private HashMap<String, ContentProviderRecord> getProvidersByName(int userId) {
        if (userId < 0) {
            throw new IllegalArgumentException("Bad user " + userId);
        }
        HashMap<String, ContentProviderRecord> map = this.mProvidersByNamePerUser.get(userId);
        if (map == null) {
            HashMap<String, ContentProviderRecord> newMap = new HashMap<>();
            this.mProvidersByNamePerUser.put(userId, newMap);
            return newMap;
        }
        return map;
    }

    HashMap<ComponentName, ContentProviderRecord> getProvidersByClass(int userId) {
        if (userId < 0) {
            throw new IllegalArgumentException("Bad user " + userId);
        }
        HashMap<ComponentName, ContentProviderRecord> map = this.mProvidersByClassPerUser.get(userId);
        if (map == null) {
            HashMap<ComponentName, ContentProviderRecord> newMap = new HashMap<>();
            this.mProvidersByClassPerUser.put(userId, newMap);
            return newMap;
        }
        return map;
    }

    private boolean collectForceStopProvidersLocked(String name, int appId, boolean doit, boolean evenPersistent, int userId, HashMap<ComponentName, ContentProviderRecord> providers, ArrayList<ContentProviderRecord> result) {
        boolean didSomething = false;
        for (ContentProviderRecord provider : providers.values()) {
            if (name == null || provider.info.packageName.equals(name)) {
                if (provider.proc == null || evenPersistent || !provider.proc.persistent) {
                    if (!doit) {
                        return true;
                    }
                    didSomething = true;
                    result.add(provider);
                }
            }
        }
        return didSomething;
    }

    boolean collectForceStopProviders(String name, int appId, boolean doit, boolean evenPersistent, int userId, ArrayList<ContentProviderRecord> result) {
        boolean didSomething = collectForceStopProvidersLocked(name, appId, doit, evenPersistent, userId, this.mSingletonByClass, result);
        if (!doit && didSomething) {
            return true;
        }
        if (userId == -1) {
            for (int i = 0; i < this.mProvidersByClassPerUser.size(); i++) {
                if (collectForceStopProvidersLocked(name, appId, doit, evenPersistent, userId, this.mProvidersByClassPerUser.valueAt(i), result)) {
                    if (!doit) {
                        return true;
                    }
                    didSomething = true;
                }
            }
        } else {
            HashMap<ComponentName, ContentProviderRecord> items = getProvidersByClass(userId);
            if (items != null) {
                didSomething |= collectForceStopProvidersLocked(name, appId, doit, evenPersistent, userId, items, result);
            }
        }
        return didSomething;
    }

    private boolean dumpProvidersByClassLocked(PrintWriter pw, boolean dumpAll, String dumpPackage, String header, boolean needSep, HashMap<ComponentName, ContentProviderRecord> map) {
        boolean written = false;
        for (Map.Entry<ComponentName, ContentProviderRecord> e : map.entrySet()) {
            ContentProviderRecord r = e.getValue();
            if (dumpPackage == null || dumpPackage.equals(r.appInfo.packageName)) {
                if (needSep) {
                    pw.println("");
                    needSep = false;
                }
                if (header != null) {
                    pw.println(header);
                    header = null;
                }
                written = true;
                pw.print("  * ");
                pw.println(r);
                r.dump(pw, "    ", dumpAll);
            }
        }
        return written;
    }

    private boolean dumpProvidersByNameLocked(PrintWriter pw, String dumpPackage, String header, boolean needSep, HashMap<String, ContentProviderRecord> map) {
        boolean written = false;
        for (Map.Entry<String, ContentProviderRecord> e : map.entrySet()) {
            ContentProviderRecord r = e.getValue();
            if (dumpPackage == null || dumpPackage.equals(r.appInfo.packageName)) {
                if (needSep) {
                    pw.println("");
                    needSep = false;
                }
                if (header != null) {
                    pw.println(header);
                    header = null;
                }
                written = true;
                pw.print("  ");
                pw.print(e.getKey());
                pw.print(": ");
                pw.println(r.toShortString());
            }
        }
        return written;
    }

    boolean dumpProvidersLocked(PrintWriter pw, boolean dumpAll, String dumpPackage) {
        boolean needSep = false;
        if (this.mSingletonByClass.size() > 0) {
            needSep = false | dumpProvidersByClassLocked(pw, dumpAll, dumpPackage, "  Published single-user content providers (by class):", false, this.mSingletonByClass);
        }
        for (int i = 0; i < this.mProvidersByClassPerUser.size(); i++) {
            HashMap<ComponentName, ContentProviderRecord> map = this.mProvidersByClassPerUser.valueAt(i);
            needSep |= dumpProvidersByClassLocked(pw, dumpAll, dumpPackage, "  Published user " + this.mProvidersByClassPerUser.keyAt(i) + " content providers (by class):", needSep, map);
        }
        if (dumpAll) {
            needSep |= dumpProvidersByNameLocked(pw, dumpPackage, "  Single-user authority to provider mappings:", needSep, this.mSingletonByName);
            for (int i2 = 0; i2 < this.mProvidersByNamePerUser.size(); i2++) {
                needSep |= dumpProvidersByNameLocked(pw, dumpPackage, "  User " + this.mProvidersByNamePerUser.keyAt(i2) + " authority to provider mappings:", needSep, this.mProvidersByNamePerUser.valueAt(i2));
            }
        }
        return needSep;
    }

    protected boolean dumpProvider(FileDescriptor fd, PrintWriter pw, String name, String[] args, int opti, boolean dumpAll) {
        ArrayList<ContentProviderRecord> allProviders = new ArrayList<>();
        ArrayList<ContentProviderRecord> providers = new ArrayList<>();
        synchronized (this.mAm) {
            allProviders.addAll(this.mSingletonByClass.values());
            for (int i = 0; i < this.mProvidersByClassPerUser.size(); i++) {
                allProviders.addAll(this.mProvidersByClassPerUser.valueAt(i).values());
            }
            if ("all".equals(name)) {
                providers.addAll(allProviders);
            } else {
                ComponentName componentName = name != null ? ComponentName.unflattenFromString(name) : null;
                int objectId = 0;
                if (componentName == null) {
                    try {
                        objectId = Integer.parseInt(name, 16);
                        name = null;
                        componentName = null;
                    } catch (RuntimeException e) {
                    }
                }
                for (int i2 = 0; i2 < allProviders.size(); i2++) {
                    ContentProviderRecord r1 = allProviders.get(i2);
                    if (componentName != null) {
                        if (r1.name.equals(componentName)) {
                            providers.add(r1);
                        }
                    } else if (name != null) {
                        if (r1.name.flattenToString().contains(name)) {
                            providers.add(r1);
                        }
                    } else if (System.identityHashCode(r1) == objectId) {
                        providers.add(r1);
                    }
                }
            }
        }
        if (providers.size() <= 0) {
            return false;
        }
        boolean needSep = false;
        for (int i3 = 0; i3 < providers.size(); i3++) {
            if (needSep) {
                pw.println();
            }
            needSep = true;
            dumpProvider("", fd, pw, providers.get(i3), args, dumpAll);
        }
        return true;
    }
}