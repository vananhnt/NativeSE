package com.android.server.search;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AppGlobals;
import android.app.ISearchManager;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.IPackageManager;
import android.content.pm.ResolveInfo;
import android.database.ContentObserver;
import android.os.Binder;
import android.os.Process;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.content.PackageMonitor;
import com.android.internal.util.IndentingPrintWriter;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;

/* loaded from: SearchManagerService.class */
public class SearchManagerService extends ISearchManager.Stub {
    private static final String TAG = "SearchManagerService";
    private final Context mContext;
    private final SparseArray<Searchables> mSearchables = new SparseArray<>();

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.search.SearchManagerService.getSearchables(int):com.android.server.search.Searchables, file: SearchManagerService.class
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
    public com.android.server.search.Searchables getSearchables(int r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.search.SearchManagerService.getSearchables(int):com.android.server.search.Searchables, file: SearchManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.search.SearchManagerService.getSearchables(int):com.android.server.search.Searchables");
    }

    public SearchManagerService(Context context) {
        this.mContext = context;
        this.mContext.registerReceiver(new BootCompletedReceiver(), new IntentFilter(Intent.ACTION_BOOT_COMPLETED));
        this.mContext.registerReceiver(new UserReceiver(), new IntentFilter(Intent.ACTION_USER_REMOVED));
        new MyPackageMonitor().register(context, null, UserHandle.ALL, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onUserRemoved(int userId) {
        if (userId != 0) {
            synchronized (this.mSearchables) {
                this.mSearchables.remove(userId);
            }
        }
    }

    /* loaded from: SearchManagerService$BootCompletedReceiver.class */
    private final class BootCompletedReceiver extends BroadcastReceiver {
        private BootCompletedReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            new Thread() { // from class: com.android.server.search.SearchManagerService.BootCompletedReceiver.1
                @Override // java.lang.Thread, java.lang.Runnable
                public void run() {
                    Process.setThreadPriority(10);
                    SearchManagerService.this.mContext.unregisterReceiver(BootCompletedReceiver.this);
                    SearchManagerService.this.getSearchables(0);
                }
            }.start();
        }
    }

    /* loaded from: SearchManagerService$UserReceiver.class */
    private final class UserReceiver extends BroadcastReceiver {
        private UserReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            SearchManagerService.this.onUserRemoved(intent.getIntExtra(Intent.EXTRA_USER_HANDLE, 0));
        }
    }

    /* loaded from: SearchManagerService$MyPackageMonitor.class */
    class MyPackageMonitor extends PackageMonitor {
        MyPackageMonitor() {
        }

        @Override // com.android.internal.content.PackageMonitor
        public void onSomePackagesChanged() {
            updateSearchables();
        }

        @Override // com.android.internal.content.PackageMonitor
        public void onPackageModified(String pkg) {
            updateSearchables();
        }

        private void updateSearchables() {
            int changingUserId = getChangingUserId();
            synchronized (SearchManagerService.this.mSearchables) {
                int i = 0;
                while (true) {
                    if (i >= SearchManagerService.this.mSearchables.size()) {
                        break;
                    } else if (changingUserId == SearchManagerService.this.mSearchables.keyAt(i)) {
                        SearchManagerService.this.getSearchables(SearchManagerService.this.mSearchables.keyAt(i)).buildSearchableList();
                        break;
                    } else {
                        i++;
                    }
                }
            }
            Intent intent = new Intent(SearchManager.INTENT_ACTION_SEARCHABLES_CHANGED);
            intent.addFlags(603979776);
            SearchManagerService.this.mContext.sendBroadcastAsUser(intent, new UserHandle(changingUserId));
        }
    }

    /* loaded from: SearchManagerService$GlobalSearchProviderObserver.class */
    class GlobalSearchProviderObserver extends ContentObserver {
        private final ContentResolver mResolver;

        public GlobalSearchProviderObserver(ContentResolver resolver) {
            super(null);
            this.mResolver = resolver;
            this.mResolver.registerContentObserver(Settings.Secure.getUriFor(Settings.Secure.SEARCH_GLOBAL_SEARCH_ACTIVITY), false, this);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            synchronized (SearchManagerService.this.mSearchables) {
                for (int i = 0; i < SearchManagerService.this.mSearchables.size(); i++) {
                    SearchManagerService.this.getSearchables(SearchManagerService.this.mSearchables.keyAt(i)).buildSearchableList();
                }
            }
            Intent intent = new Intent(SearchManager.INTENT_GLOBAL_SEARCH_ACTIVITY_CHANGED);
            intent.addFlags(536870912);
            SearchManagerService.this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
        }
    }

    @Override // android.app.ISearchManager
    public SearchableInfo getSearchableInfo(ComponentName launchActivity) {
        if (launchActivity == null) {
            Log.e(TAG, "getSearchableInfo(), activity == null");
            return null;
        }
        return getSearchables(UserHandle.getCallingUserId()).getSearchableInfo(launchActivity);
    }

    @Override // android.app.ISearchManager
    public List<SearchableInfo> getSearchablesInGlobalSearch() {
        return getSearchables(UserHandle.getCallingUserId()).getSearchablesInGlobalSearchList();
    }

    @Override // android.app.ISearchManager
    public List<ResolveInfo> getGlobalSearchActivities() {
        return getSearchables(UserHandle.getCallingUserId()).getGlobalSearchActivities();
    }

    @Override // android.app.ISearchManager
    public ComponentName getGlobalSearchActivity() {
        return getSearchables(UserHandle.getCallingUserId()).getGlobalSearchActivity();
    }

    @Override // android.app.ISearchManager
    public ComponentName getWebSearchActivity() {
        return getSearchables(UserHandle.getCallingUserId()).getWebSearchActivity();
    }

    @Override // android.app.ISearchManager
    public ComponentName getAssistIntent(int userHandle) {
        try {
            int userHandle2 = ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), userHandle, true, false, "getAssistIntent", null);
            IPackageManager pm = AppGlobals.getPackageManager();
            Intent assistIntent = new Intent(Intent.ACTION_ASSIST);
            ResolveInfo info = pm.resolveIntent(assistIntent, assistIntent.resolveTypeIfNeeded(this.mContext.getContentResolver()), 65536, userHandle2);
            if (info != null) {
                return new ComponentName(info.activityInfo.applicationInfo.packageName, info.activityInfo.name);
            }
            return null;
        } catch (RemoteException re) {
            Log.e(TAG, "RemoteException in getAssistIntent: " + re);
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Exception in getAssistIntent: " + e);
            return null;
        }
    }

    @Override // android.os.Binder
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.DUMP, TAG);
        IndentingPrintWriter ipw = new IndentingPrintWriter(pw, "  ");
        synchronized (this.mSearchables) {
            for (int i = 0; i < this.mSearchables.size(); i++) {
                ipw.print("\nUser: ");
                ipw.println(this.mSearchables.keyAt(i));
                ipw.increaseIndent();
                this.mSearchables.valueAt(i).dump(fd, ipw, args);
                ipw.decreaseIndent();
            }
        }
    }
}