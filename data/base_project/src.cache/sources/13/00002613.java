package java.util;

import java.io.IOException;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ResourceBundle.class */
public abstract class ResourceBundle {
    protected ResourceBundle parent;

    public abstract Enumeration<String> getKeys();

    protected abstract Object handleGetObject(String str);

    /* JADX WARN: Classes with same name are omitted:
      
     */
    /* loaded from: ResourceBundle$Control.class */
    public static class Control {
        public static final List<String> FORMAT_DEFAULT = null;
        public static final List<String> FORMAT_CLASS = null;
        public static final List<String> FORMAT_PROPERTIES = null;
        public static final long TTL_DONT_CACHE = -1;
        public static final long TTL_NO_EXPIRATION_CONTROL = -2;

        protected Control() {
            throw new RuntimeException("Stub!");
        }

        public static Control getControl(List<String> formats) {
            throw new RuntimeException("Stub!");
        }

        public static Control getNoFallbackControl(List<String> formats) {
            throw new RuntimeException("Stub!");
        }

        public List<Locale> getCandidateLocales(String baseName, Locale locale) {
            throw new RuntimeException("Stub!");
        }

        public List<String> getFormats(String baseName) {
            throw new RuntimeException("Stub!");
        }

        public Locale getFallbackLocale(String baseName, Locale locale) {
            throw new RuntimeException("Stub!");
        }

        public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException, IOException {
            throw new RuntimeException("Stub!");
        }

        public long getTimeToLive(String baseName, Locale locale) {
            throw new RuntimeException("Stub!");
        }

        public boolean needsReload(String baseName, Locale locale, String format, ClassLoader loader, ResourceBundle bundle, long loadTime) {
            throw new RuntimeException("Stub!");
        }

        public String toBundleName(String baseName, Locale locale) {
            throw new RuntimeException("Stub!");
        }

        public final String toResourceName(String bundleName, String suffix) {
            throw new RuntimeException("Stub!");
        }
    }

    public ResourceBundle() {
        throw new RuntimeException("Stub!");
    }

    public static ResourceBundle getBundle(String bundleName) throws MissingResourceException {
        throw new RuntimeException("Stub!");
    }

    public static ResourceBundle getBundle(String bundleName, Locale locale) {
        throw new RuntimeException("Stub!");
    }

    public static ResourceBundle getBundle(String bundleName, Locale locale, ClassLoader loader) throws MissingResourceException {
        throw new RuntimeException("Stub!");
    }

    public static ResourceBundle getBundle(String baseName, Control control) {
        throw new RuntimeException("Stub!");
    }

    public static ResourceBundle getBundle(String baseName, Locale targetLocale, Control control) {
        throw new RuntimeException("Stub!");
    }

    public static ResourceBundle getBundle(String baseName, Locale targetLocale, ClassLoader loader, Control control) {
        throw new RuntimeException("Stub!");
    }

    public Locale getLocale() {
        throw new RuntimeException("Stub!");
    }

    public final Object getObject(String key) {
        throw new RuntimeException("Stub!");
    }

    public final String getString(String key) {
        throw new RuntimeException("Stub!");
    }

    public final String[] getStringArray(String key) {
        throw new RuntimeException("Stub!");
    }

    protected void setParent(ResourceBundle bundle) {
        throw new RuntimeException("Stub!");
    }

    public static void clearCache() {
        throw new RuntimeException("Stub!");
    }

    public static void clearCache(ClassLoader loader) {
        throw new RuntimeException("Stub!");
    }

    public boolean containsKey(String key) {
        throw new RuntimeException("Stub!");
    }

    public Set<String> keySet() {
        throw new RuntimeException("Stub!");
    }

    protected Set<String> handleKeySet() {
        throw new RuntimeException("Stub!");
    }

    /* loaded from: ResourceBundle$MissingBundle.class */
    static class MissingBundle extends ResourceBundle {
        MissingBundle() {
        }

        @Override // java.util.ResourceBundle
        public Enumeration<String> getKeys() {
            return null;
        }

        @Override // java.util.ResourceBundle
        public Object handleGetObject(String name) {
            return null;
        }
    }

    /* loaded from: ResourceBundle$NoFallbackControl.class */
    private static class NoFallbackControl extends Control {
        static final Control NOFALLBACK_FORMAT_PROPERTIES_CONTROL = new NoFallbackControl(JAVAPROPERTIES);
        static final Control NOFALLBACK_FORMAT_CLASS_CONTROL = new NoFallbackControl(JAVACLASS);
        static final Control NOFALLBACK_FORMAT_DEFAULT_CONTROL = new NoFallbackControl(listDefault);

        public NoFallbackControl(String format) {
            listClass = new ArrayList();
            listClass.add(format);
            ((Control) this).format = Collections.unmodifiableList(listClass);
        }

        public NoFallbackControl(List<String> list) {
            ((Control) this).format = list;
        }

        @Override // java.util.ResourceBundle.Control
        public Locale getFallbackLocale(String baseName, Locale locale) {
            if (baseName == null) {
                throw new NullPointerException("baseName == null");
            }
            if (locale == null) {
                throw new NullPointerException("locale == null");
            }
            return null;
        }
    }

    /* loaded from: ResourceBundle$SimpleControl.class */
    private static class SimpleControl extends Control {
        public SimpleControl(String format) {
            listClass = new ArrayList();
            listClass.add(format);
            ((Control) this).format = Collections.unmodifiableList(listClass);
        }
    }
}