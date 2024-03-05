package java.util;

import java.io.Serializable;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: UUID.class */
public final class UUID implements Serializable, Comparable<UUID> {
    public UUID(long mostSigBits, long leastSigBits) {
        throw new RuntimeException("Stub!");
    }

    public static UUID randomUUID() {
        throw new RuntimeException("Stub!");
    }

    public static UUID nameUUIDFromBytes(byte[] name) {
        throw new RuntimeException("Stub!");
    }

    public static UUID fromString(String uuid) {
        throw new RuntimeException("Stub!");
    }

    public long getLeastSignificantBits() {
        throw new RuntimeException("Stub!");
    }

    public long getMostSignificantBits() {
        throw new RuntimeException("Stub!");
    }

    public int version() {
        throw new RuntimeException("Stub!");
    }

    public int variant() {
        throw new RuntimeException("Stub!");
    }

    public long timestamp() {
        throw new RuntimeException("Stub!");
    }

    public int clockSequence() {
        throw new RuntimeException("Stub!");
    }

    public long node() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Comparable
    public int compareTo(UUID uuid) {
        throw new RuntimeException("Stub!");
    }

    public boolean equals(Object object) {
        throw new RuntimeException("Stub!");
    }

    public int hashCode() {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }
}