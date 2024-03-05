package com.android.server.am;

import android.app.AppGlobals;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.res.CompatibilityInfo;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.AtomicFile;
import android.util.Slog;
import com.android.internal.util.FastXmlSerializer;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.xmlpull.v1.XmlSerializer;

/*  JADX ERROR: NullPointerException in pass: ExtractFieldInit
    java.lang.NullPointerException
    */
/* loaded from: CompatModePackages.class */
public final class CompatModePackages {
    private final String TAG = "ActivityManager";
    private final boolean DEBUG_CONFIGURATION = false;
    private final ActivityManagerService mService;
    private final AtomicFile mFile;
    public static final int COMPAT_FLAG_DONT_ASK = 1;
    public static final int COMPAT_FLAG_ENABLED = 2;
    private final HashMap<String, Integer> mPackages;
    private static final int MSG_WRITE = 300;
    private final Handler mHandler;

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.CompatModePackages.<init>(com.android.server.am.ActivityManagerService, java.io.File):void, file: CompatModePackages.class
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
    public CompatModePackages(com.android.server.am.ActivityManagerService r1, java.io.File r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.CompatModePackages.<init>(com.android.server.am.ActivityManagerService, java.io.File):void, file: CompatModePackages.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.CompatModePackages.<init>(com.android.server.am.ActivityManagerService, java.io.File):void");
    }

    public HashMap<String, Integer> getPackages() {
        return this.mPackages;
    }

    private int getPackageFlags(String packageName) {
        Integer flags = this.mPackages.get(packageName);
        if (flags != null) {
            return flags.intValue();
        }
        return 0;
    }

    public void handlePackageAddedLocked(String packageName, boolean updated) {
        ApplicationInfo ai = null;
        try {
            ai = AppGlobals.getPackageManager().getApplicationInfo(packageName, 0, 0);
        } catch (RemoteException e) {
        }
        if (ai == null) {
            return;
        }
        CompatibilityInfo ci = compatibilityInfoForPackageLocked(ai);
        boolean mayCompat = (ci.alwaysSupportsScreen() || ci.neverSupportsScreen()) ? false : true;
        if (updated && !mayCompat && this.mPackages.containsKey(packageName)) {
            this.mPackages.remove(packageName);
            this.mHandler.removeMessages(300);
            Message msg = this.mHandler.obtainMessage(300);
            this.mHandler.sendMessageDelayed(msg, 10000L);
        }
    }

    public CompatibilityInfo compatibilityInfoForPackageLocked(ApplicationInfo ai) {
        CompatibilityInfo ci = new CompatibilityInfo(ai, this.mService.mConfiguration.screenLayout, this.mService.mConfiguration.smallestScreenWidthDp, (getPackageFlags(ai.packageName) & 2) != 0);
        return ci;
    }

    public int computeCompatModeLocked(ApplicationInfo ai) {
        boolean enabled = (getPackageFlags(ai.packageName) & 2) != 0;
        CompatibilityInfo info = new CompatibilityInfo(ai, this.mService.mConfiguration.screenLayout, this.mService.mConfiguration.smallestScreenWidthDp, enabled);
        if (info.alwaysSupportsScreen()) {
            return -2;
        }
        if (info.neverSupportsScreen()) {
            return -1;
        }
        return enabled ? 1 : 0;
    }

    public boolean getFrontActivityAskCompatModeLocked() {
        ActivityRecord r = this.mService.getFocusedStack().topRunningActivityLocked(null);
        if (r == null) {
            return false;
        }
        return getPackageAskCompatModeLocked(r.packageName);
    }

    public boolean getPackageAskCompatModeLocked(String packageName) {
        return (getPackageFlags(packageName) & 1) == 0;
    }

    public void setFrontActivityAskCompatModeLocked(boolean ask) {
        ActivityRecord r = this.mService.getFocusedStack().topRunningActivityLocked(null);
        if (r != null) {
            setPackageAskCompatModeLocked(r.packageName, ask);
        }
    }

    public void setPackageAskCompatModeLocked(String packageName, boolean ask) {
        int curFlags = getPackageFlags(packageName);
        int newFlags = ask ? curFlags & (-2) : curFlags | 1;
        if (curFlags != newFlags) {
            if (newFlags != 0) {
                this.mPackages.put(packageName, Integer.valueOf(newFlags));
            } else {
                this.mPackages.remove(packageName);
            }
            this.mHandler.removeMessages(300);
            Message msg = this.mHandler.obtainMessage(300);
            this.mHandler.sendMessageDelayed(msg, 10000L);
        }
    }

    public int getFrontActivityScreenCompatModeLocked() {
        ActivityRecord r = this.mService.getFocusedStack().topRunningActivityLocked(null);
        if (r == null) {
            return -3;
        }
        return computeCompatModeLocked(r.info.applicationInfo);
    }

    public void setFrontActivityScreenCompatModeLocked(int mode) {
        ActivityRecord r = this.mService.getFocusedStack().topRunningActivityLocked(null);
        if (r == null) {
            Slog.w("ActivityManager", "setFrontActivityScreenCompatMode failed: no top activity");
        } else {
            setPackageScreenCompatModeLocked(r.info.applicationInfo, mode);
        }
    }

    public int getPackageScreenCompatModeLocked(String packageName) {
        ApplicationInfo ai = null;
        try {
            ai = AppGlobals.getPackageManager().getApplicationInfo(packageName, 0, 0);
        } catch (RemoteException e) {
        }
        if (ai == null) {
            return -3;
        }
        return computeCompatModeLocked(ai);
    }

    public void setPackageScreenCompatModeLocked(String packageName, int mode) {
        ApplicationInfo ai = null;
        try {
            ai = AppGlobals.getPackageManager().getApplicationInfo(packageName, 0, 0);
        } catch (RemoteException e) {
        }
        if (ai == null) {
            Slog.w("ActivityManager", "setPackageScreenCompatMode failed: unknown package " + packageName);
        } else {
            setPackageScreenCompatModeLocked(ai, mode);
        }
    }

    private void setPackageScreenCompatModeLocked(ApplicationInfo ai, int mode) {
        boolean enable;
        int newFlags;
        String packageName = ai.packageName;
        int curFlags = getPackageFlags(packageName);
        switch (mode) {
            case 0:
                enable = false;
                break;
            case 1:
                enable = true;
                break;
            case 2:
                enable = (curFlags & 2) == 0;
                break;
            default:
                Slog.w("ActivityManager", "Unknown screen compat mode req #" + mode + "; ignoring");
                return;
        }
        if (enable) {
            newFlags = curFlags | 2;
        } else {
            newFlags = curFlags & (-3);
        }
        CompatibilityInfo ci = compatibilityInfoForPackageLocked(ai);
        if (ci.alwaysSupportsScreen()) {
            Slog.w("ActivityManager", "Ignoring compat mode change of " + packageName + "; compatibility never needed");
            newFlags = 0;
        }
        if (ci.neverSupportsScreen()) {
            Slog.w("ActivityManager", "Ignoring compat mode change of " + packageName + "; compatibility always needed");
            newFlags = 0;
        }
        if (newFlags != curFlags) {
            if (newFlags != 0) {
                this.mPackages.put(packageName, Integer.valueOf(newFlags));
            } else {
                this.mPackages.remove(packageName);
            }
            CompatibilityInfo ci2 = compatibilityInfoForPackageLocked(ai);
            this.mHandler.removeMessages(300);
            Message msg = this.mHandler.obtainMessage(300);
            this.mHandler.sendMessageDelayed(msg, 10000L);
            ActivityStack stack = this.mService.getFocusedStack();
            ActivityRecord starting = stack.restartPackage(packageName);
            for (int i = this.mService.mLruProcesses.size() - 1; i >= 0; i--) {
                ProcessRecord app = (ProcessRecord) this.mService.mLruProcesses.get(i);
                if (app.pkgList.containsKey(packageName)) {
                    try {
                        if (app.thread != null) {
                            app.thread.updatePackageCompatibilityInfo(packageName, ci2);
                        }
                    } catch (Exception e) {
                    }
                }
            }
            if (starting != null) {
                stack.ensureActivityConfigurationLocked(starting, 0);
                stack.ensureActivitiesVisibleLocked(starting, 0);
            }
        }
    }

    void saveCompatModes() {
        HashMap<String, Integer> pkgs;
        synchronized (this.mService) {
            pkgs = new HashMap<>(this.mPackages);
        }
        FileOutputStream fos = null;
        try {
            fos = this.mFile.startWrite();
            XmlSerializer out = new FastXmlSerializer();
            out.setOutput(fos, "utf-8");
            out.startDocument(null, true);
            out.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            out.startTag(null, "compat-packages");
            IPackageManager pm = AppGlobals.getPackageManager();
            int screenLayout = this.mService.mConfiguration.screenLayout;
            int smallestScreenWidthDp = this.mService.mConfiguration.smallestScreenWidthDp;
            for (Map.Entry<String, Integer> entry : pkgs.entrySet()) {
                String pkg = entry.getKey();
                int mode = entry.getValue().intValue();
                if (mode != 0) {
                    ApplicationInfo ai = null;
                    try {
                        ai = pm.getApplicationInfo(pkg, 0, 0);
                    } catch (RemoteException e) {
                    }
                    if (ai != null) {
                        CompatibilityInfo info = new CompatibilityInfo(ai, screenLayout, smallestScreenWidthDp, false);
                        if (!info.alwaysSupportsScreen() && !info.neverSupportsScreen()) {
                            out.startTag(null, "pkg");
                            out.attribute(null, "name", pkg);
                            out.attribute(null, "mode", Integer.toString(mode));
                            out.endTag(null, "pkg");
                        }
                    }
                }
            }
            out.endTag(null, "compat-packages");
            out.endDocument();
            this.mFile.finishWrite(fos);
        } catch (IOException e1) {
            Slog.w("ActivityManager", "Error writing compat packages", e1);
            if (fos != null) {
                this.mFile.failWrite(fos);
            }
        }
    }
}