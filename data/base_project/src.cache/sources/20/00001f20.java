package com.android.server.pm;

import android.content.pm.KeySet;
import android.content.pm.PackageParser;
import android.os.Binder;
import android.util.Base64;
import android.util.LongSparseArray;
import com.android.server.pm.PackageManagerService;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.PublicKey;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

/* loaded from: KeySetManager.class */
public class KeySetManager {
    static final String TAG = "KeySetManager";
    public static final long KEYSET_NOT_FOUND = -1;
    private static final long PUBLIC_KEY_NOT_FOUND = -1;
    private final Object mLockObject = new Object();
    private final LongSparseArray<KeySet> mKeySets = new LongSparseArray<>();
    private final LongSparseArray<PublicKey> mPublicKeys = new LongSparseArray<>();
    private final LongSparseArray<Set<Long>> mKeySetMapping = new LongSparseArray<>();
    private final Map<String, PackageSetting> mPackages;
    private static long lastIssuedKeySetId = 0;
    private static long lastIssuedKeyId = 0;

    public KeySetManager(Map<String, PackageSetting> packages) {
        this.mPackages = packages;
    }

    public boolean packageIsSignedBy(String packageName, KeySet ks) {
        boolean packageIsSignedBy;
        synchronized (this.mLockObject) {
            PackageSetting pkg = this.mPackages.get(packageName);
            if (pkg == null) {
                throw new NullPointerException("Invalid package name");
            }
            if (pkg.keySetData == null) {
                throw new NullPointerException("Package has no KeySet data");
            }
            long id = getIdByKeySetLocked(ks);
            packageIsSignedBy = pkg.keySetData.packageIsSignedBy(id);
        }
        return packageIsSignedBy;
    }

    public void addDefinedKeySetToPackage(String packageName, Set<PublicKey> keys, String alias) {
        if (packageName == null || keys == null || alias == null) {
            return;
        }
        synchronized (this.mLockObject) {
            KeySet ks = addKeySetLocked(keys);
            PackageSetting pkg = this.mPackages.get(packageName);
            if (pkg == null) {
                throw new NullPointerException("Unknown package");
            }
            long id = getIdByKeySetLocked(ks);
            pkg.keySetData.addDefinedKeySet(id, alias);
        }
    }

    public void addSigningKeySetToPackage(String packageName, Set<PublicKey> signingKeys) {
        if (packageName == null || signingKeys == null) {
            return;
        }
        synchronized (this.mLockObject) {
            KeySet ks = addKeySetLocked(signingKeys);
            long id = getIdByKeySetLocked(ks);
            Set<Long> publicKeyIds = this.mKeySetMapping.get(id);
            if (publicKeyIds == null) {
                throw new NullPointerException("Got invalid KeySet id");
            }
            PackageSetting pkg = this.mPackages.get(packageName);
            if (pkg == null) {
                throw new NullPointerException("No such package!");
            }
            pkg.keySetData.addSigningKeySet(id);
            long[] arr$ = pkg.keySetData.getDefinedKeySets();
            for (long j : arr$) {
                Long keySetID = Long.valueOf(j);
                Set<Long> definedKeys = this.mKeySetMapping.get(keySetID.longValue());
                if (publicKeyIds.contains(definedKeys)) {
                    pkg.keySetData.addSigningKeySet(keySetID.longValue());
                }
            }
        }
    }

    public long getIdByKeySet(KeySet ks) {
        long idByKeySetLocked;
        synchronized (this.mLockObject) {
            idByKeySetLocked = getIdByKeySetLocked(ks);
        }
        return idByKeySetLocked;
    }

    private long getIdByKeySetLocked(KeySet ks) {
        for (int keySetIndex = 0; keySetIndex < this.mKeySets.size(); keySetIndex++) {
            KeySet value = this.mKeySets.valueAt(keySetIndex);
            if (ks.equals(value)) {
                return this.mKeySets.keyAt(keySetIndex);
            }
        }
        return -1L;
    }

    public KeySet getKeySetById(long id) {
        KeySet keySet;
        synchronized (this.mLockObject) {
            keySet = this.mKeySets.get(id);
        }
        return keySet;
    }

    public KeySet getKeySetByAliasAndPackageName(String packageName, String alias) {
        KeySet keySet;
        synchronized (this.mLockObject) {
            PackageSetting p = this.mPackages.get(packageName);
            if (p == null) {
                throw new NullPointerException("Unknown package");
            }
            if (p.keySetData == null) {
                throw new IllegalArgumentException("Package has no keySet data");
            }
            long keySetId = p.keySetData.getAliases().get(alias).longValue();
            keySet = this.mKeySets.get(keySetId);
        }
        return keySet;
    }

    public Set<KeySet> getSigningKeySetsByPackageName(String packageName) {
        Set<KeySet> signingKeySets;
        synchronized (this.mLockObject) {
            signingKeySets = new HashSet<>();
            PackageSetting p = this.mPackages.get(packageName);
            if (p == null) {
                throw new NullPointerException("Unknown package");
            }
            if (p.keySetData == null) {
                throw new IllegalArgumentException("Package has no keySet data");
            }
            long[] arr$ = p.keySetData.getSigningKeySets();
            for (long l : arr$) {
                signingKeySets.add(this.mKeySets.get(l));
            }
        }
        return signingKeySets;
    }

    private KeySet addKeySetLocked(Set<PublicKey> keys) {
        if (keys == null) {
            throw new NullPointerException("Provided keys cannot be null");
        }
        Set<Long> addedKeyIds = new HashSet<>(keys.size());
        for (PublicKey k : keys) {
            addedKeyIds.add(Long.valueOf(addPublicKeyLocked(k)));
        }
        long existingKeySetId = getIdFromKeyIdsLocked(addedKeyIds);
        if (existingKeySetId != -1) {
            return this.mKeySets.get(existingKeySetId);
        }
        KeySet ks = new KeySet(new Binder());
        long id = getFreeKeySetIDLocked();
        this.mKeySets.put(id, ks);
        this.mKeySetMapping.put(id, addedKeyIds);
        return ks;
    }

    private long addPublicKeyLocked(PublicKey key) {
        long existingKeyId = getIdForPublicKeyLocked(key);
        if (existingKeyId != -1) {
            return existingKeyId;
        }
        long id = getFreePublicKeyIdLocked();
        this.mPublicKeys.put(id, key);
        return id;
    }

    private long getIdFromKeyIdsLocked(Set<Long> publicKeyIds) {
        for (int keyMapIndex = 0; keyMapIndex < this.mKeySetMapping.size(); keyMapIndex++) {
            Set<Long> value = this.mKeySetMapping.valueAt(keyMapIndex);
            if (value.equals(publicKeyIds)) {
                return this.mKeySetMapping.keyAt(keyMapIndex);
            }
        }
        return -1L;
    }

    private long getIdForPublicKeyLocked(PublicKey k) {
        String encodedPublicKey = new String(k.getEncoded());
        for (int publicKeyIndex = 0; publicKeyIndex < this.mPublicKeys.size(); publicKeyIndex++) {
            PublicKey value = this.mPublicKeys.valueAt(publicKeyIndex);
            String encodedExistingKey = new String(value.getEncoded());
            if (encodedPublicKey.equals(encodedExistingKey)) {
                return this.mPublicKeys.keyAt(publicKeyIndex);
            }
        }
        return -1L;
    }

    private long getFreeKeySetIDLocked() {
        lastIssuedKeySetId++;
        return lastIssuedKeySetId;
    }

    private long getFreePublicKeyIdLocked() {
        lastIssuedKeyId++;
        return lastIssuedKeyId;
    }

    public void removeAppKeySetData(String packageName) {
        synchronized (this.mLockObject) {
            Set<Long> deletableKeySets = getKnownKeySetsByPackageNameLocked(packageName);
            Set<Long> deletableKeys = new HashSet<>();
            for (Long ks : deletableKeySets) {
                Set<Long> knownKeys = this.mKeySetMapping.get(ks.longValue());
                if (knownKeys != null) {
                    deletableKeys.addAll(knownKeys);
                }
            }
            for (String pkgName : this.mPackages.keySet()) {
                if (!pkgName.equals(packageName)) {
                    Set<Long> knownKeySets = getKnownKeySetsByPackageNameLocked(pkgName);
                    deletableKeySets.removeAll(knownKeySets);
                    new HashSet();
                    for (Long ks2 : knownKeySets) {
                        Set<Long> knownKeys2 = this.mKeySetMapping.get(ks2.longValue());
                        if (knownKeys2 != null) {
                            deletableKeys.removeAll(knownKeys2);
                        }
                    }
                }
            }
            for (Long ks3 : deletableKeySets) {
                this.mKeySets.delete(ks3.longValue());
                this.mKeySetMapping.delete(ks3.longValue());
            }
            for (Long keyId : deletableKeys) {
                this.mPublicKeys.delete(keyId.longValue());
            }
            for (String pkgName2 : this.mPackages.keySet()) {
                PackageSetting p = this.mPackages.get(pkgName2);
                for (Long ks4 : deletableKeySets) {
                    p.keySetData.removeSigningKeySet(ks4.longValue());
                    p.keySetData.removeDefinedKeySet(ks4.longValue());
                }
            }
        }
    }

    private Set<Long> getKnownKeySetsByPackageNameLocked(String packageName) {
        PackageSetting p = this.mPackages.get(packageName);
        if (p == null) {
            throw new NullPointerException("Unknown package");
        }
        if (p.keySetData == null) {
            throw new IllegalArgumentException("Package has no keySet data");
        }
        Set<Long> knownKeySets = new HashSet<>();
        long[] arr$ = p.keySetData.getSigningKeySets();
        for (long ks : arr$) {
            knownKeySets.add(Long.valueOf(ks));
        }
        long[] arr$2 = p.keySetData.getDefinedKeySets();
        for (long ks2 : arr$2) {
            knownKeySets.add(Long.valueOf(ks2));
        }
        return knownKeySets;
    }

    public String encodePublicKey(PublicKey k) throws IOException {
        return new String(Base64.encode(k.getEncoded(), 0));
    }

    public void dump(PrintWriter pw, String packageName, PackageManagerService.DumpState dumpState) {
        synchronized (this.mLockObject) {
            boolean printedHeader = false;
            for (Map.Entry<String, PackageSetting> e : this.mPackages.entrySet()) {
                String keySetPackage = e.getKey();
                if (packageName == null || packageName.equals(keySetPackage)) {
                    if (!printedHeader) {
                        if (dumpState.onTitlePrinted()) {
                            pw.println();
                        }
                        pw.println("Key Set Manager:");
                        printedHeader = true;
                    }
                    PackageSetting pkg = e.getValue();
                    pw.print("  [");
                    pw.print(keySetPackage);
                    pw.println("]");
                    if (pkg.keySetData != null) {
                        boolean printedLabel = false;
                        for (Map.Entry<String, Long> entry : pkg.keySetData.getAliases().entrySet()) {
                            if (!printedLabel) {
                                pw.print("      KeySets Aliases: ");
                                printedLabel = true;
                            } else {
                                pw.print(", ");
                            }
                            pw.print(entry.getKey());
                            pw.print('=');
                            pw.print(Long.toString(entry.getValue().longValue()));
                        }
                        if (printedLabel) {
                            pw.println("");
                        }
                        boolean printedLabel2 = false;
                        long[] arr$ = pkg.keySetData.getDefinedKeySets();
                        for (long keySetId : arr$) {
                            if (!printedLabel2) {
                                pw.print("      Defined KeySets: ");
                                printedLabel2 = true;
                            } else {
                                pw.print(", ");
                            }
                            pw.print(Long.toString(keySetId));
                        }
                        if (printedLabel2) {
                            pw.println("");
                        }
                        boolean printedLabel3 = false;
                        long[] arr$2 = pkg.keySetData.getSigningKeySets();
                        for (long keySetId2 : arr$2) {
                            if (!printedLabel3) {
                                pw.print("      Signing KeySets: ");
                                printedLabel3 = true;
                            } else {
                                pw.print(", ");
                            }
                            pw.print(Long.toString(keySetId2));
                        }
                        if (printedLabel3) {
                            pw.println("");
                        }
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void writeKeySetManagerLPr(XmlSerializer serializer) throws IOException {
        serializer.startTag(null, "keyset-settings");
        writePublicKeysLPr(serializer);
        writeKeySetsLPr(serializer);
        serializer.startTag(null, "lastIssuedKeyId");
        serializer.attribute(null, "value", Long.toString(lastIssuedKeyId));
        serializer.endTag(null, "lastIssuedKeyId");
        serializer.startTag(null, "lastIssuedKeySetId");
        serializer.attribute(null, "value", Long.toString(lastIssuedKeySetId));
        serializer.endTag(null, "lastIssuedKeySetId");
        serializer.endTag(null, "keyset-settings");
    }

    void writePublicKeysLPr(XmlSerializer serializer) throws IOException {
        serializer.startTag(null, "keys");
        for (int pKeyIndex = 0; pKeyIndex < this.mPublicKeys.size(); pKeyIndex++) {
            long id = this.mPublicKeys.keyAt(pKeyIndex);
            PublicKey key = this.mPublicKeys.valueAt(pKeyIndex);
            String encodedKey = encodePublicKey(key);
            serializer.startTag(null, "public-key");
            serializer.attribute(null, "identifier", Long.toString(id));
            serializer.attribute(null, "value", encodedKey);
            serializer.endTag(null, "public-key");
        }
        serializer.endTag(null, "keys");
    }

    void writeKeySetsLPr(XmlSerializer serializer) throws IOException {
        serializer.startTag(null, "keysets");
        for (int keySetIndex = 0; keySetIndex < this.mKeySetMapping.size(); keySetIndex++) {
            long id = this.mKeySetMapping.keyAt(keySetIndex);
            Set<Long> keys = this.mKeySetMapping.valueAt(keySetIndex);
            serializer.startTag(null, "keyset");
            serializer.attribute(null, "identifier", Long.toString(id));
            for (Long l : keys) {
                long keyId = l.longValue();
                serializer.startTag(null, "key-id");
                serializer.attribute(null, "identifier", Long.toString(keyId));
                serializer.endTag(null, "key-id");
            }
            serializer.endTag(null, "keyset");
        }
        serializer.endTag(null, "keysets");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void readKeySetsLPw(XmlPullParser parser) throws XmlPullParserException, IOException {
        while (true) {
            int type = parser.next();
            if (type != 1) {
                if (type != 3 && type != 4) {
                    String tagName = parser.getName();
                    if (tagName.equals("keys")) {
                        readKeysLPw(parser);
                    } else if (tagName.equals("keysets")) {
                        readKeySetListLPw(parser);
                    }
                }
            } else {
                return;
            }
        }
    }

    void readKeysLPw(XmlPullParser parser) throws XmlPullParserException, IOException {
        int outerDepth = parser.getDepth();
        while (true) {
            int type = parser.next();
            if (type == 1) {
                return;
            }
            if (type != 3 || parser.getDepth() > outerDepth) {
                if (type != 3 && type != 4) {
                    String tagName = parser.getName();
                    if (tagName.equals("public-key")) {
                        readPublicKeyLPw(parser);
                    } else if (tagName.equals("lastIssuedKeyId")) {
                        lastIssuedKeyId = Long.parseLong(parser.getAttributeValue(null, "value"));
                    } else if (tagName.equals("lastIssuedKeySetId")) {
                        lastIssuedKeySetId = Long.parseLong(parser.getAttributeValue(null, "value"));
                    }
                }
            } else {
                return;
            }
        }
    }

    void readKeySetListLPw(XmlPullParser parser) throws XmlPullParserException, IOException {
        int outerDepth = parser.getDepth();
        long currentKeySetId = 0;
        while (true) {
            int type = parser.next();
            if (type == 1) {
                return;
            }
            if (type != 3 || parser.getDepth() > outerDepth) {
                if (type != 3 && type != 4) {
                    String tagName = parser.getName();
                    if (tagName.equals("keyset")) {
                        currentKeySetId = readIdentifierLPw(parser);
                        this.mKeySets.put(currentKeySetId, new KeySet(new Binder()));
                        this.mKeySetMapping.put(currentKeySetId, new HashSet());
                    } else if (tagName.equals("key-id")) {
                        long id = readIdentifierLPw(parser);
                        this.mKeySetMapping.get(currentKeySetId).add(Long.valueOf(id));
                    }
                }
            } else {
                return;
            }
        }
    }

    long readIdentifierLPw(XmlPullParser parser) throws XmlPullParserException {
        return Long.parseLong(parser.getAttributeValue(null, "identifier"));
    }

    void readPublicKeyLPw(XmlPullParser parser) throws XmlPullParserException {
        String encodedID = parser.getAttributeValue(null, "identifier");
        long identifier = Long.parseLong(encodedID);
        String encodedPublicKey = parser.getAttributeValue(null, "value");
        PublicKey pub = PackageParser.parsePublicKey(encodedPublicKey);
        if (pub != null) {
            this.mPublicKeys.put(identifier, pub);
        }
    }
}