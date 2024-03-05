package java.net;

import libcore.util.BasicLruCache;

/* loaded from: AddressCache.class */
class AddressCache {
    private static final int MAX_ENTRIES = 16;
    private static final long TTL_NANOS = 2000000000;
    private final BasicLruCache<String, AddressCacheEntry> cache = new BasicLruCache<>(16);

    AddressCache() {
    }

    /* loaded from: AddressCache$AddressCacheEntry.class */
    static class AddressCacheEntry {
        final Object value;
        final long expiryNanos = System.nanoTime() + AddressCache.TTL_NANOS;

        AddressCacheEntry(Object value) {
            this.value = value;
        }
    }

    public void clear() {
        this.cache.evictAll();
    }

    public Object get(String hostname) {
        AddressCacheEntry entry = this.cache.get(hostname);
        if (entry != null && entry.expiryNanos >= System.nanoTime()) {
            return entry.value;
        }
        return null;
    }

    public void put(String hostname, InetAddress[] addresses) {
        this.cache.put(hostname, new AddressCacheEntry(addresses));
    }

    public void putUnknownHost(String hostname, String detailMessage) {
        this.cache.put(hostname, new AddressCacheEntry(detailMessage));
    }
}