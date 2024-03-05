package com.android.server.pm;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/* loaded from: PackageKeySetData.class */
public class PackageKeySetData {
    private long[] mSigningKeySets;
    private long[] mDefinedKeySets;
    private final Map<String, Long> mKeySetAliases;

    /* JADX INFO: Access modifiers changed from: package-private */
    public PackageKeySetData() {
        this.mSigningKeySets = new long[0];
        this.mDefinedKeySets = new long[0];
        this.mKeySetAliases = new HashMap();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PackageKeySetData(PackageKeySetData original) {
        this.mSigningKeySets = (long[]) original.getSigningKeySets().clone();
        this.mDefinedKeySets = (long[]) original.getDefinedKeySets().clone();
        this.mKeySetAliases = new HashMap();
        this.mKeySetAliases.putAll(original.getAliases());
    }

    public void addSigningKeySet(long ks) {
        long[] arr$ = this.mSigningKeySets;
        for (long knownKeySet : arr$) {
            if (ks == knownKeySet) {
                return;
            }
        }
        int end = this.mSigningKeySets.length;
        this.mSigningKeySets = Arrays.copyOf(this.mSigningKeySets, end + 1);
        this.mSigningKeySets[end] = ks;
    }

    public void removeSigningKeySet(long ks) {
        if (packageIsSignedBy(ks)) {
            long[] keysets = new long[this.mSigningKeySets.length - 1];
            int index = 0;
            long[] arr$ = this.mSigningKeySets;
            for (long signingKeySet : arr$) {
                if (signingKeySet != ks) {
                    keysets[index] = signingKeySet;
                    index++;
                }
            }
            this.mSigningKeySets = keysets;
        }
    }

    public void addDefinedKeySet(long ks, String alias) {
        long[] arr$ = this.mDefinedKeySets;
        for (long knownKeySet : arr$) {
            if (ks == knownKeySet) {
                return;
            }
        }
        int end = this.mDefinedKeySets.length;
        this.mDefinedKeySets = Arrays.copyOf(this.mDefinedKeySets, end + 1);
        this.mDefinedKeySets[end] = ks;
        this.mKeySetAliases.put(alias, Long.valueOf(ks));
    }

    public void removeDefinedKeySet(long ks) {
        if (this.mKeySetAliases.containsValue(Long.valueOf(ks))) {
            long[] keysets = new long[this.mDefinedKeySets.length - 1];
            int index = 0;
            long[] arr$ = this.mDefinedKeySets;
            for (long definedKeySet : arr$) {
                if (definedKeySet != ks) {
                    keysets[index] = definedKeySet;
                    index++;
                }
            }
            this.mDefinedKeySets = keysets;
            for (String alias : this.mKeySetAliases.keySet()) {
                if (this.mKeySetAliases.get(alias).longValue() == ks) {
                    this.mKeySetAliases.remove(alias);
                    return;
                }
            }
        }
    }

    public boolean packageIsSignedBy(long ks) {
        long[] arr$ = this.mSigningKeySets;
        for (long signingKeySet : arr$) {
            if (ks == signingKeySet) {
                return true;
            }
        }
        return false;
    }

    public long[] getSigningKeySets() {
        return this.mSigningKeySets;
    }

    public long[] getDefinedKeySets() {
        return this.mDefinedKeySets;
    }

    public Map<String, Long> getAliases() {
        return this.mKeySetAliases;
    }
}