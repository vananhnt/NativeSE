package java.util.logging;

import java.io.Serializable;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Level.class */
public class Level implements Serializable {
    public static final Level OFF = null;
    public static final Level SEVERE = null;
    public static final Level WARNING = null;
    public static final Level INFO = null;
    public static final Level CONFIG = null;
    public static final Level FINE = null;
    public static final Level FINER = null;
    public static final Level FINEST = null;
    public static final Level ALL = null;

    protected Level(String name, int level) {
        throw new RuntimeException("Stub!");
    }

    protected Level(String name, int level, String resourceBundleName) {
        throw new RuntimeException("Stub!");
    }

    public static Level parse(String name) throws IllegalArgumentException {
        throw new RuntimeException("Stub!");
    }

    public String getName() {
        throw new RuntimeException("Stub!");
    }

    public String getResourceBundleName() {
        throw new RuntimeException("Stub!");
    }

    public final int intValue() {
        throw new RuntimeException("Stub!");
    }

    public String getLocalizedName() {
        throw new RuntimeException("Stub!");
    }

    public boolean equals(Object o) {
        throw new RuntimeException("Stub!");
    }

    public int hashCode() {
        throw new RuntimeException("Stub!");
    }

    public final String toString() {
        throw new RuntimeException("Stub!");
    }
}