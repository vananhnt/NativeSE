package org.apache.harmony.security.provider.cert;

import java.util.Arrays;

/* loaded from: Cache.class */
public class Cache {
    private static final long HASH_MASK = -65536;
    private static final long PREFIX_HASH_MASK = -4294967296L;
    private static final int INDEX_MASK = 65535;
    private final int cache_size;
    private final int prefix_size;
    private final long[] hashes;
    private final byte[][] encodings;
    private final Object[] cache;
    private final long[] hashes_idx;
    private int last_cached;
    private boolean cache_is_full;

    /* JADX WARN: Type inference failed for: r1v12, types: [byte[], byte[][]] */
    public Cache(int pref_size, int size) {
        this.last_cached = 0;
        this.cache_is_full = false;
        this.cache_size = size;
        this.prefix_size = pref_size;
        this.hashes = new long[this.cache_size];
        this.hashes_idx = new long[this.cache_size];
        this.encodings = new byte[this.cache_size];
        this.cache = new Object[this.cache_size];
    }

    public Cache(int pref_size) {
        this(pref_size, 9);
    }

    public Cache() {
        this(28, 9);
    }

    public long getHash(byte[] arr) {
        long hash = 0;
        for (int i = 1; i < this.prefix_size; i++) {
            hash += arr[i] & 255;
        }
        return hash << 32;
    }

    public boolean contains(long prefix_hash) {
        int idx;
        return (prefix_hash == 0 || (idx = ((-1) * Arrays.binarySearch(this.hashes_idx, prefix_hash)) - 1) == this.cache_size || (this.hashes_idx[idx] & PREFIX_HASH_MASK) != prefix_hash) ? false : true;
    }

    public Object get(long hash, byte[] encoding) {
        long hash2 = hash | getSuffHash(encoding);
        if (hash2 == 0) {
            return null;
        }
        int idx = ((-1) * Arrays.binarySearch(this.hashes_idx, hash2)) - 1;
        if (idx == this.cache_size) {
            return null;
        }
        while ((this.hashes_idx[idx] & HASH_MASK) == hash2) {
            int i = ((int) (this.hashes_idx[idx] & 65535)) - 1;
            if (Arrays.equals(encoding, this.encodings[i])) {
                return this.cache[i];
            }
            idx++;
            if (idx == this.cache_size) {
                return null;
            }
        }
        return null;
    }

    public void put(long hash, byte[] encoding, Object object) {
        if (this.last_cached == this.cache_size) {
            this.last_cached = 0;
            this.cache_is_full = true;
        }
        int index = this.last_cached;
        this.last_cached = index + 1;
        long hash2 = hash | getSuffHash(encoding);
        if (this.cache_is_full) {
            int idx = Arrays.binarySearch(this.hashes_idx, this.hashes[index] | (index + 1));
            if (idx < 0) {
                System.out.println("WARNING! " + idx);
                idx = -(idx + 1);
            }
            long new_hash_idx = hash2 | (index + 1);
            int new_idx = Arrays.binarySearch(this.hashes_idx, new_hash_idx);
            if (new_idx >= 0) {
                if (idx != new_idx) {
                    System.out.println("WARNING: ");
                    System.out.println(">> idx: " + idx + " new_idx: " + new_idx);
                }
            } else {
                int new_idx2 = -(new_idx + 1);
                if (new_idx2 > idx) {
                    System.arraycopy(this.hashes_idx, idx + 1, this.hashes_idx, idx, (new_idx2 - idx) - 1);
                    this.hashes_idx[new_idx2 - 1] = new_hash_idx;
                } else if (idx > new_idx2) {
                    System.arraycopy(this.hashes_idx, new_idx2, this.hashes_idx, new_idx2 + 1, idx - new_idx2);
                    this.hashes_idx[new_idx2] = new_hash_idx;
                } else {
                    this.hashes_idx[new_idx2] = new_hash_idx;
                }
            }
        } else {
            long idx_hash = hash2 | (index + 1);
            int idx2 = Arrays.binarySearch(this.hashes_idx, idx_hash);
            if (idx2 < 0) {
                idx2 = -(idx2 + 1);
            }
            int idx3 = idx2 - 1;
            if (idx3 != (this.cache_size - index) - 1) {
                System.arraycopy(this.hashes_idx, this.cache_size - index, this.hashes_idx, (this.cache_size - index) - 1, (idx3 - (this.cache_size - index)) + 1);
            }
            this.hashes_idx[idx3] = idx_hash;
        }
        this.hashes[index] = hash2;
        this.encodings[index] = encoding;
        this.cache[index] = object;
    }

    private long getSuffHash(byte[] arr) {
        long hash_addon = 0;
        for (int i = arr.length - 1; i > arr.length - this.prefix_size; i--) {
            hash_addon += arr[i] & 255;
        }
        return hash_addon << 16;
    }
}