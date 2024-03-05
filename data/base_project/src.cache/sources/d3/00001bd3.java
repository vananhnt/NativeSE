package com.android.server;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.UserHandle;
import android.util.SparseArray;
import java.util.HashMap;
import java.util.WeakHashMap;

/* loaded from: AttributeCache.class */
public final class AttributeCache {
    private static AttributeCache sInstance = null;
    private final Context mContext;
    private final WeakHashMap<String, Package> mPackages = new WeakHashMap<>();
    private final Configuration mConfiguration = new Configuration();

    /* loaded from: AttributeCache$Package.class */
    public static final class Package {
        public final Context context;
        private final SparseArray<HashMap<int[], Entry>> mMap = new SparseArray<>();

        public Package(Context c) {
            this.context = c;
        }
    }

    /* loaded from: AttributeCache$Entry.class */
    public static final class Entry {
        public final Context context;
        public final TypedArray array;

        public Entry(Context c, TypedArray ta) {
            this.context = c;
            this.array = ta;
        }
    }

    public static void init(Context context) {
        if (sInstance == null) {
            sInstance = new AttributeCache(context);
        }
    }

    public static AttributeCache instance() {
        return sInstance;
    }

    public AttributeCache(Context context) {
        this.mContext = context;
    }

    public void removePackage(String packageName) {
        synchronized (this) {
            this.mPackages.remove(packageName);
        }
    }

    public void updateConfiguration(Configuration config) {
        synchronized (this) {
            int changes = this.mConfiguration.updateFrom(config);
            if ((changes & (-1073741985)) != 0) {
                this.mPackages.clear();
            }
        }
    }

    public Entry get(String packageName, int resId, int[] styleable, int userId) {
        Entry ent;
        synchronized (this) {
            Package pkg = this.mPackages.get(packageName);
            HashMap<int[], Entry> map = null;
            if (pkg != null) {
                map = (HashMap) pkg.mMap.get(resId);
                if (map != null && (ent = map.get(styleable)) != null) {
                    return ent;
                }
            } else {
                try {
                    Context context = this.mContext.createPackageContextAsUser(packageName, 0, new UserHandle(userId));
                    if (context == null) {
                        return null;
                    }
                    pkg = new Package(context);
                    this.mPackages.put(packageName, pkg);
                } catch (PackageManager.NameNotFoundException e) {
                    return null;
                }
            }
            if (map == null) {
                map = new HashMap<>();
                pkg.mMap.put(resId, map);
            }
            try {
                Entry ent2 = new Entry(pkg.context, pkg.context.obtainStyledAttributes(resId, styleable));
                map.put(styleable, ent2);
                return ent2;
            } catch (Resources.NotFoundException e2) {
                return null;
            }
        }
    }
}