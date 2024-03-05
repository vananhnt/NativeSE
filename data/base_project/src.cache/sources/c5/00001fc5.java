package com.android.server.search;

import android.app.AppGlobals;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.IPackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/* loaded from: Searchables.class */
public class Searchables {
    private static final String LOG_TAG = "Searchables";
    private static final String MD_LABEL_DEFAULT_SEARCHABLE = "android.app.default_searchable";
    private static final String MD_SEARCHABLE_SYSTEM_SEARCH = "*";
    private Context mContext;
    private List<ResolveInfo> mGlobalSearchActivities;
    private int mUserId;
    public static String GOOGLE_SEARCH_COMPONENT_NAME = "com.android.googlesearch/.GoogleSearch";
    public static String ENHANCED_GOOGLE_SEARCH_COMPONENT_NAME = "com.google.android.providers.enhancedgooglesearch/.Launcher";
    private static final Comparator<ResolveInfo> GLOBAL_SEARCH_RANKER = new Comparator<ResolveInfo>() { // from class: com.android.server.search.Searchables.1
        @Override // java.util.Comparator
        public int compare(ResolveInfo lhs, ResolveInfo rhs) {
            if (lhs != rhs) {
                boolean lhsSystem = Searchables.isSystemApp(lhs);
                boolean rhsSystem = Searchables.isSystemApp(rhs);
                if (lhsSystem && !rhsSystem) {
                    return -1;
                }
                if (rhsSystem && !lhsSystem) {
                    return 1;
                }
                return rhs.priority - lhs.priority;
            }
            return 0;
        }
    };
    private HashMap<ComponentName, SearchableInfo> mSearchablesMap = null;
    private ArrayList<SearchableInfo> mSearchablesList = null;
    private ArrayList<SearchableInfo> mSearchablesInGlobalSearchList = null;
    private ComponentName mCurrentGlobalSearchActivity = null;
    private ComponentName mWebSearchActivity = null;
    private final IPackageManager mPm = AppGlobals.getPackageManager();

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.search.Searchables.buildSearchableList():void, file: Searchables.class
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
    public void buildSearchableList() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.search.Searchables.buildSearchableList():void, file: Searchables.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.search.Searchables.buildSearchableList():void");
    }

    public Searchables(Context context, int userId) {
        this.mContext = context;
        this.mUserId = userId;
    }

    public SearchableInfo getSearchableInfo(ComponentName activity) {
        ComponentName referredActivity;
        Bundle md;
        synchronized (this) {
            SearchableInfo result = this.mSearchablesMap.get(activity);
            if (result != null) {
                return result;
            }
            try {
                ActivityInfo ai = this.mPm.getActivityInfo(activity, 128, this.mUserId);
                String refActivityName = null;
                Bundle md2 = ai.metaData;
                if (md2 != null) {
                    refActivityName = md2.getString(MD_LABEL_DEFAULT_SEARCHABLE);
                }
                if (refActivityName == null && (md = ai.applicationInfo.metaData) != null) {
                    refActivityName = md.getString(MD_LABEL_DEFAULT_SEARCHABLE);
                }
                if (refActivityName == null || refActivityName.equals("*")) {
                    return null;
                }
                String pkg = activity.getPackageName();
                if (refActivityName.charAt(0) == '.') {
                    referredActivity = new ComponentName(pkg, pkg + refActivityName);
                } else {
                    referredActivity = new ComponentName(pkg, refActivityName);
                }
                synchronized (this) {
                    SearchableInfo result2 = this.mSearchablesMap.get(referredActivity);
                    if (result2 != null) {
                        this.mSearchablesMap.put(activity, result2);
                        return result2;
                    }
                    return null;
                }
            } catch (RemoteException re) {
                Log.e(LOG_TAG, "Error getting activity info " + re);
                return null;
            }
        }
    }

    private List<ResolveInfo> findGlobalSearchActivities() {
        Intent intent = new Intent(SearchManager.INTENT_ACTION_GLOBAL_SEARCH);
        List<ResolveInfo> activities = queryIntentActivities(intent, 65536);
        if (activities != null && !activities.isEmpty()) {
            Collections.sort(activities, GLOBAL_SEARCH_RANKER);
        }
        return activities;
    }

    private ComponentName findGlobalSearchActivity(List<ResolveInfo> installed) {
        ComponentName globalSearchComponent;
        String searchProviderSetting = getGlobalSearchProviderSetting();
        if (!TextUtils.isEmpty(searchProviderSetting) && (globalSearchComponent = ComponentName.unflattenFromString(searchProviderSetting)) != null && isInstalled(globalSearchComponent)) {
            return globalSearchComponent;
        }
        return getDefaultGlobalSearchProvider(installed);
    }

    private boolean isInstalled(ComponentName globalSearch) {
        Intent intent = new Intent(SearchManager.INTENT_ACTION_GLOBAL_SEARCH);
        intent.setComponent(globalSearch);
        List<ResolveInfo> activities = queryIntentActivities(intent, 65536);
        if (activities != null && !activities.isEmpty()) {
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final boolean isSystemApp(ResolveInfo res) {
        return (res.activityInfo.applicationInfo.flags & 1) != 0;
    }

    private ComponentName getDefaultGlobalSearchProvider(List<ResolveInfo> providerList) {
        if (providerList != null && !providerList.isEmpty()) {
            ActivityInfo ai = providerList.get(0).activityInfo;
            return new ComponentName(ai.packageName, ai.name);
        }
        Log.w(LOG_TAG, "No global search activity found");
        return null;
    }

    private String getGlobalSearchProviderSetting() {
        return Settings.Secure.getString(this.mContext.getContentResolver(), Settings.Secure.SEARCH_GLOBAL_SEARCH_ACTIVITY);
    }

    private ComponentName findWebSearchActivity(ComponentName globalSearchActivity) {
        if (globalSearchActivity == null) {
            return null;
        }
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.setPackage(globalSearchActivity.getPackageName());
        List<ResolveInfo> activities = queryIntentActivities(intent, 65536);
        if (activities != null && !activities.isEmpty()) {
            ActivityInfo ai = activities.get(0).activityInfo;
            return new ComponentName(ai.packageName, ai.name);
        }
        Log.w(LOG_TAG, "No web search activity found");
        return null;
    }

    private List<ResolveInfo> queryIntentActivities(Intent intent, int flags) {
        List<ResolveInfo> activities = null;
        try {
            activities = this.mPm.queryIntentActivities(intent, intent.resolveTypeIfNeeded(this.mContext.getContentResolver()), flags, this.mUserId);
        } catch (RemoteException e) {
        }
        return activities;
    }

    public synchronized ArrayList<SearchableInfo> getSearchablesList() {
        ArrayList<SearchableInfo> result = new ArrayList<>(this.mSearchablesList);
        return result;
    }

    public synchronized ArrayList<SearchableInfo> getSearchablesInGlobalSearchList() {
        return new ArrayList<>(this.mSearchablesInGlobalSearchList);
    }

    public synchronized ArrayList<ResolveInfo> getGlobalSearchActivities() {
        return new ArrayList<>(this.mGlobalSearchActivities);
    }

    public synchronized ComponentName getGlobalSearchActivity() {
        return this.mCurrentGlobalSearchActivity;
    }

    public synchronized ComponentName getWebSearchActivity() {
        return this.mWebSearchActivity;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("Searchable authorities:");
        synchronized (this) {
            if (this.mSearchablesList != null) {
                Iterator i$ = this.mSearchablesList.iterator();
                while (i$.hasNext()) {
                    SearchableInfo info = i$.next();
                    pw.print("  ");
                    pw.println(info.getSuggestAuthority());
                }
            }
        }
    }
}