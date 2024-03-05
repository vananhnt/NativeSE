package android.util;

/* loaded from: TrustedTime.class */
public interface TrustedTime {
    boolean forceRefresh();

    boolean hasCache();

    long getCacheAge();

    long getCacheCertainty();

    long currentTimeMillis();
}