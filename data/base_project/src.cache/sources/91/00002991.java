package org.apache.harmony.security.fortress;

import gov.nist.core.Separators;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.util.Locale;

/* loaded from: Engine.class */
public class Engine {
    public static SecurityAccess door;
    private final String serviceName;
    private volatile ServiceCacheEntry serviceCache;

    /* loaded from: Engine$ServiceCacheEntry.class */
    private static final class ServiceCacheEntry {
        private final String algorithm;
        private final int cacheVersion;
        private final Provider.Service service;

        private ServiceCacheEntry(String algorithm, int cacheVersion, Provider.Service service) {
            this.algorithm = algorithm;
            this.cacheVersion = cacheVersion;
            this.service = service;
        }
    }

    /* loaded from: Engine$SpiAndProvider.class */
    public static final class SpiAndProvider {
        public final Object spi;
        public final Provider provider;

        private SpiAndProvider(Object spi, Provider provider) {
            this.spi = spi;
            this.provider = provider;
        }
    }

    public Engine(String service) {
        this.serviceName = service;
    }

    public SpiAndProvider getInstance(String algorithm, Object param) throws NoSuchAlgorithmException {
        Provider.Service service;
        if (algorithm == null) {
            throw new NoSuchAlgorithmException("Null algorithm name");
        }
        int newCacheVersion = Services.getCacheVersion();
        ServiceCacheEntry cacheEntry = this.serviceCache;
        if (cacheEntry != null && cacheEntry.algorithm.equalsIgnoreCase(algorithm) && newCacheVersion == cacheEntry.cacheVersion) {
            service = cacheEntry.service;
        } else if (Services.isEmpty()) {
            throw notFound(this.serviceName, algorithm);
        } else {
            String name = this.serviceName + Separators.DOT + algorithm.toUpperCase(Locale.US);
            service = Services.getService(name);
            if (service == null) {
                throw notFound(this.serviceName, algorithm);
            }
            this.serviceCache = new ServiceCacheEntry(algorithm, newCacheVersion, service);
        }
        return new SpiAndProvider(service.newInstance(param), service.getProvider());
    }

    public Object getInstance(String algorithm, Provider provider, Object param) throws NoSuchAlgorithmException {
        if (algorithm == null) {
            throw new NoSuchAlgorithmException("algorithm == null");
        }
        Provider.Service service = provider.getService(this.serviceName, algorithm);
        if (service == null) {
            throw notFound(this.serviceName, algorithm);
        }
        return service.newInstance(param);
    }

    private NoSuchAlgorithmException notFound(String serviceName, String algorithm) throws NoSuchAlgorithmException {
        throw new NoSuchAlgorithmException(serviceName + Separators.SP + algorithm + " implementation not found");
    }
}