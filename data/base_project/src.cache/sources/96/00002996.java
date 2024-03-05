package org.apache.harmony.security.fortress;

import gov.nist.core.Separators;
import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/* loaded from: Services.class */
public class Services {
    private static Provider.Service cachedSecureRandomService;
    private static boolean needRefresh;
    private static final Map<String, Provider.Service> services = new HashMap(600);
    private static int cacheVersion = 1;
    private static final List<Provider> providers = new ArrayList(20);
    private static final Map<String, Provider> providersNames = new HashMap(20);

    static {
        int i = 1;
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        while (true) {
            int i2 = i;
            i++;
            String providerClassName = Security.getProperty("security.provider." + i2);
            if (providerClassName == null) {
                Engine.door.renumProviders();
                return;
            }
            try {
                Class providerClass = Class.forName(providerClassName.trim(), true, cl);
                Provider p = (Provider) providerClass.newInstance();
                providers.add(p);
                providersNames.put(p.getName(), p);
                initServiceInfo(p);
            } catch (ClassNotFoundException e) {
            } catch (IllegalAccessException e2) {
            } catch (InstantiationException e3) {
            }
        }
    }

    public static synchronized Provider[] getProviders() {
        return (Provider[]) providers.toArray(new Provider[providers.size()]);
    }

    public static synchronized List<Provider> getProvidersList() {
        return new ArrayList(providers);
    }

    public static synchronized Provider getProvider(String name) {
        if (name == null) {
            return null;
        }
        return providersNames.get(name);
    }

    public static synchronized int insertProviderAt(Provider provider, int position) {
        int size = providers.size();
        if (position < 1 || position > size) {
            position = size + 1;
        }
        providers.add(position - 1, provider);
        providersNames.put(provider.getName(), provider);
        setNeedRefresh();
        return position;
    }

    public static synchronized void removeProvider(int providerNumber) {
        Provider p = providers.remove(providerNumber - 1);
        providersNames.remove(p.getName());
        setNeedRefresh();
    }

    public static synchronized void initServiceInfo(Provider p) {
        for (Provider.Service service : p.getServices()) {
            String type = service.getType();
            if (cachedSecureRandomService == null && type.equals("SecureRandom")) {
                cachedSecureRandomService = service;
            }
            String key = type + Separators.DOT + service.getAlgorithm().toUpperCase(Locale.US);
            if (!services.containsKey(key)) {
                services.put(key, service);
            }
            for (String alias : Engine.door.getAliases(service)) {
                String key2 = type + Separators.DOT + alias.toUpperCase(Locale.US);
                if (!services.containsKey(key2)) {
                    services.put(key2, service);
                }
            }
        }
    }

    public static synchronized boolean isEmpty() {
        return services.isEmpty();
    }

    public static synchronized Provider.Service getService(String key) {
        return services.get(key);
    }

    public static synchronized Provider.Service getSecureRandomService() {
        getCacheVersion();
        return cachedSecureRandomService;
    }

    public static synchronized void setNeedRefresh() {
        needRefresh = true;
    }

    public static synchronized int getCacheVersion() {
        if (needRefresh) {
            cacheVersion++;
            synchronized (services) {
                services.clear();
            }
            cachedSecureRandomService = null;
            for (Provider p : providers) {
                initServiceInfo(p);
            }
            needRefresh = false;
        }
        return cacheVersion;
    }
}