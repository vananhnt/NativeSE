package java.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import libcore.io.IoUtils;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ServiceLoader.class */
public final class ServiceLoader<S> implements Iterable<S> {
    ServiceLoader() {
        throw new RuntimeException("Stub!");
    }

    public void reload() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Iterable
    public Iterator<S> iterator() {
        throw new RuntimeException("Stub!");
    }

    public static <S> ServiceLoader<S> load(Class<S> service, ClassLoader classLoader) {
        throw new RuntimeException("Stub!");
    }

    public static <S> ServiceLoader<S> load(Class<S> service) {
        throw new RuntimeException("Stub!");
    }

    public static <S> ServiceLoader<S> loadInstalled(Class<S> service) {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }

    /* loaded from: ServiceLoader$ServiceIterator.class */
    private class ServiceIterator implements Iterator<S> {
        private final ClassLoader classLoader;
        private final Class<S> service;
        private final Set<URL> services;
        private boolean isRead = false;
        private LinkedList<String> queue = new LinkedList<>();

        public ServiceIterator(ServiceLoader<S> sl) {
            this.classLoader = ServiceLoader.access$000(sl);
            this.service = ServiceLoader.access$100(sl);
            this.services = ServiceLoader.access$200(sl);
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            if (!this.isRead) {
                readClass();
            }
            return (this.queue == null || this.queue.isEmpty()) ? false : true;
        }

        @Override // java.util.Iterator
        public S next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            String className = this.queue.remove();
            try {
                return this.service.cast(this.classLoader.loadClass(className).newInstance());
            } catch (Exception e) {
                throw new ServiceConfigurationError("Couldn't instantiate class " + className, e);
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r10v0 */
        /* JADX WARN: Type inference failed for: r10v1 */
        /* JADX WARN: Type inference failed for: r10v2 */
        /* JADX WARN: Type inference failed for: r10v3, types: [java.io.BufferedReader, java.lang.AutoCloseable] */
        private void readClass() {
            for (URL url : this.services) {
                ?? r10 = 0;
                try {
                    try {
                        r10 = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
                        while (true) {
                            String readLine = r10.readLine();
                            String line = readLine;
                            if (readLine == null) {
                                break;
                            }
                            int commentStart = line.indexOf(35);
                            if (commentStart != -1) {
                                line = line.substring(0, commentStart);
                            }
                            String line2 = line.trim();
                            if (!line2.isEmpty()) {
                                checkValidJavaClassName(line2);
                                if (!this.queue.contains(line2)) {
                                    this.queue.add(line2);
                                }
                            }
                        }
                        this.isRead = true;
                        IoUtils.closeQuietly((AutoCloseable) r10);
                    } catch (Exception e) {
                        throw new ServiceConfigurationError("Couldn't read " + url, e);
                    }
                } catch (Throwable th) {
                    IoUtils.closeQuietly((AutoCloseable) r10);
                    throw th;
                }
            }
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException();
        }

        private void checkValidJavaClassName(String className) {
            for (int i = 0; i < className.length(); i++) {
                char ch = className.charAt(i);
                if (!Character.isJavaIdentifierPart(ch) && ch != '.') {
                    throw new ServiceConfigurationError("Bad character '" + ch + "' in class name");
                }
            }
        }
    }
}