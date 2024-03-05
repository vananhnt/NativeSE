package android.support.v4.util;

import gov.nist.core.Separators;
import java.util.LinkedHashMap;
import java.util.Map;

/* loaded from: LruCache.class */
public class LruCache<K, V> {
    private int createCount;
    private int evictionCount;
    private int hitCount;
    private final LinkedHashMap<K, V> map;
    private int maxSize;
    private int missCount;
    private int putCount;
    private int size;

    public LruCache(int i) {
        if (i <= 0) {
            throw new IllegalArgumentException("maxSize <= 0");
        }
        this.maxSize = i;
        this.map = new LinkedHashMap<>(0, 0.75f, true);
    }

    private int safeSizeOf(K k, V v) {
        int sizeOf = sizeOf(k, v);
        if (sizeOf >= 0) {
            return sizeOf;
        }
        throw new IllegalStateException("Negative size: " + k + Separators.EQUALS + v);
    }

    protected V create(K k) {
        return null;
    }

    public final int createCount() {
        int i;
        synchronized (this) {
            i = this.createCount;
        }
        return i;
    }

    protected void entryRemoved(boolean z, K k, V v, V v2) {
    }

    public final void evictAll() {
        trimToSize(-1);
    }

    public final int evictionCount() {
        int i;
        synchronized (this) {
            i = this.evictionCount;
        }
        return i;
    }

    public final V get(K k) {
        V put;
        if (k != null) {
            synchronized (this) {
                try {
                    try {
                        V v = this.map.get(k);
                        if (v != null) {
                            this.hitCount++;
                            return v;
                        }
                        this.missCount++;
                        V create = create(k);
                        if (create == null) {
                            return null;
                        }
                        synchronized (this) {
                            this.createCount++;
                            put = this.map.put(k, create);
                            if (put != null) {
                                this.map.put(k, put);
                            } else {
                                this.size += safeSizeOf(k, create);
                            }
                        }
                        if (put != null) {
                            entryRemoved(false, k, create, put);
                            return put;
                        }
                        trimToSize(this.maxSize);
                        return create;
                    } catch (Throwable th) {
                        th = th;
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                    throw th;
                }
            }
        }
        throw new NullPointerException("key == null");
    }

    public final int hitCount() {
        int i;
        synchronized (this) {
            i = this.hitCount;
        }
        return i;
    }

    public final int maxSize() {
        int i;
        synchronized (this) {
            i = this.maxSize;
        }
        return i;
    }

    public final int missCount() {
        int i;
        synchronized (this) {
            i = this.missCount;
        }
        return i;
    }

    public final V put(K k, V v) {
        V put;
        if (k == null || v == null) {
            throw new NullPointerException("key == null || value == null");
        }
        synchronized (this) {
            this.putCount++;
            this.size += safeSizeOf(k, v);
            put = this.map.put(k, v);
            if (put != null) {
                this.size -= safeSizeOf(k, put);
            }
        }
        if (put != null) {
            entryRemoved(false, k, put, v);
        }
        trimToSize(this.maxSize);
        return put;
    }

    public final int putCount() {
        int i;
        synchronized (this) {
            i = this.putCount;
        }
        return i;
    }

    public final V remove(K k) {
        if (k != null) {
            synchronized (this) {
                try {
                } catch (Throwable th) {
                    th = th;
                }
                try {
                    V remove = this.map.remove(k);
                    if (remove != null) {
                        this.size -= safeSizeOf(k, remove);
                    }
                    if (remove != null) {
                        entryRemoved(false, k, remove, null);
                    }
                    return remove;
                } catch (Throwable th2) {
                    th = th2;
                    throw th;
                }
            }
        }
        throw new NullPointerException("key == null");
    }

    public void resize(int i) {
        if (i <= 0) {
            throw new IllegalArgumentException("maxSize <= 0");
        }
        synchronized (this) {
            this.maxSize = i;
        }
        trimToSize(i);
    }

    public final int size() {
        int i;
        synchronized (this) {
            i = this.size;
        }
        return i;
    }

    protected int sizeOf(K k, V v) {
        return 1;
    }

    public final Map<K, V> snapshot() {
        LinkedHashMap linkedHashMap;
        synchronized (this) {
            linkedHashMap = new LinkedHashMap(this.map);
        }
        return linkedHashMap;
    }

    public final String toString() {
        String format;
        synchronized (this) {
            int i = this.hitCount + this.missCount;
            format = String.format("LruCache[maxSize=%d,hits=%d,misses=%d,hitRate=%d%%]", Integer.valueOf(this.maxSize), Integer.valueOf(this.hitCount), Integer.valueOf(this.missCount), Integer.valueOf(i != 0 ? (this.hitCount * 100) / i : 0));
        }
        return format;
    }

    /* JADX WARN: Code restructure failed: missing block: B:26:0x00ac, code lost:
        throw new java.lang.IllegalStateException(getClass().getName() + ".sizeOf() is reporting inconsistent results!");
     */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:35:? -> B:32:0x00b2). Please submit an issue!!! */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void trimToSize(int r7) {
        /*
            r6 = this;
        L0:
            r0 = r6
            monitor-enter(r0)
            r0 = r6
            int r0 = r0.size     // Catch: java.lang.Throwable -> Lad
            if (r0 < 0) goto L84
            r0 = r6
            java.util.LinkedHashMap<K, V> r0 = r0.map     // Catch: java.lang.Throwable -> Lad
            boolean r0 = r0.isEmpty()     // Catch: java.lang.Throwable -> Lad
            if (r0 == 0) goto L1a
            r0 = r6
            int r0 = r0.size     // Catch: java.lang.Throwable -> Lad
            if (r0 != 0) goto L84
        L1a:
            r0 = r6
            int r0 = r0.size     // Catch: java.lang.Throwable -> Lad
            r1 = r7
            if (r0 <= r1) goto L81
            r0 = r6
            java.util.LinkedHashMap<K, V> r0 = r0.map     // Catch: java.lang.Throwable -> Lad
            boolean r0 = r0.isEmpty()     // Catch: java.lang.Throwable -> Lad
            if (r0 == 0) goto L2f
            goto L81
        L2f:
            r0 = r6
            java.util.LinkedHashMap<K, V> r0 = r0.map     // Catch: java.lang.Throwable -> Lad
            java.util.Set r0 = r0.entrySet()     // Catch: java.lang.Throwable -> Lad
            java.util.Iterator r0 = r0.iterator()     // Catch: java.lang.Throwable -> Lad
            java.lang.Object r0 = r0.next()     // Catch: java.lang.Throwable -> Lad
            java.util.Map$Entry r0 = (java.util.Map.Entry) r0     // Catch: java.lang.Throwable -> Lad
            r8 = r0
            r0 = r8
            java.lang.Object r0 = r0.getKey()     // Catch: java.lang.Throwable -> Lad
            r9 = r0
            r0 = r8
            java.lang.Object r0 = r0.getValue()     // Catch: java.lang.Throwable -> Lb2
            r8 = r0
            r0 = r6
            java.util.LinkedHashMap<K, V> r0 = r0.map     // Catch: java.lang.Throwable -> Lb2
            r1 = r9
            java.lang.Object r0 = r0.remove(r1)     // Catch: java.lang.Throwable -> Lb2
            r0 = r6
            r1 = r6
            int r1 = r1.size     // Catch: java.lang.Throwable -> Lb2
            r2 = r6
            r3 = r9
            r4 = r8
            int r2 = r2.safeSizeOf(r3, r4)     // Catch: java.lang.Throwable -> Lb2
            int r1 = r1 - r2
            r0.size = r1     // Catch: java.lang.Throwable -> Lb2
            r0 = r6
            r1 = r6
            int r1 = r1.evictionCount     // Catch: java.lang.Throwable -> Lb2
            r2 = 1
            int r1 = r1 + r2
            r0.evictionCount = r1     // Catch: java.lang.Throwable -> Lb2
            r0 = r6
            monitor-exit(r0)     // Catch: java.lang.Throwable -> Lb2
            r0 = r6
            r1 = 1
            r2 = r9
            r3 = r8
            r4 = 0
            r0.entryRemoved(r1, r2, r3, r4)
            goto L0
        L81:
            r0 = r6
            monitor-exit(r0)     // Catch: java.lang.Throwable -> Lad
            return
        L84:
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException     // Catch: java.lang.Throwable -> Lad
            r8 = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> Lad
            r9 = r0
            r0 = r9
            r0.<init>()     // Catch: java.lang.Throwable -> Lad
            r0 = r9
            r1 = r6
            java.lang.Class r1 = r1.getClass()     // Catch: java.lang.Throwable -> Lad
            java.lang.String r1 = r1.getName()     // Catch: java.lang.Throwable -> Lad
            java.lang.StringBuilder r0 = r0.append(r1)     // Catch: java.lang.Throwable -> Lad
            r0 = r9
            java.lang.String r1 = ".sizeOf() is reporting inconsistent results!"
            java.lang.StringBuilder r0 = r0.append(r1)     // Catch: java.lang.Throwable -> Lad
            r0 = r8
            r1 = r9
            java.lang.String r1 = r1.toString()     // Catch: java.lang.Throwable -> Lad
            r0.<init>(r1)     // Catch: java.lang.Throwable -> Lad
            r0 = r8
            throw r0     // Catch: java.lang.Throwable -> Lad
        Lad:
            r9 = move-exception
        Lae:
            r0 = r6
            monitor-exit(r0)     // Catch: java.lang.Throwable -> Lb2
            r0 = r9
            throw r0
        Lb2:
            r9 = move-exception
            goto Lae
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.util.LruCache.trimToSize(int):void");
    }
}