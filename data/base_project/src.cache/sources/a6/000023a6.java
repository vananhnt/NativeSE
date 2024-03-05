package java.net;

import android.content.ContentResolver;
import gov.nist.core.Separators;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.SecureClassLoader;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import libcore.io.Streams;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: URLClassLoader.class */
public class URLClassLoader extends SecureClassLoader {
    public URLClassLoader(URL[] urls) {
        throw new RuntimeException("Stub!");
    }

    public URLClassLoader(URL[] urls, ClassLoader parent) {
        throw new RuntimeException("Stub!");
    }

    public URLClassLoader(URL[] searchUrls, ClassLoader parent, URLStreamHandlerFactory factory) {
        throw new RuntimeException("Stub!");
    }

    protected void addURL(URL url) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.ClassLoader
    public Enumeration<URL> findResources(String name) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.security.SecureClassLoader
    protected PermissionCollection getPermissions(CodeSource codesource) {
        throw new RuntimeException("Stub!");
    }

    public URL[] getURLs() {
        throw new RuntimeException("Stub!");
    }

    public static URLClassLoader newInstance(URL[] urls) {
        throw new RuntimeException("Stub!");
    }

    public static URLClassLoader newInstance(URL[] urls, ClassLoader parentCl) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.ClassLoader
    protected Class<?> findClass(String className) throws ClassNotFoundException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.ClassLoader
    public URL findResource(String name) {
        throw new RuntimeException("Stub!");
    }

    protected Package definePackage(String packageName, Manifest manifest, URL url) throws IllegalArgumentException {
        throw new RuntimeException("Stub!");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: URLClassLoader$IndexFile.class */
    public static class IndexFile {
        private HashMap<String, ArrayList<URL>> map;

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.net.URLClassLoader.IndexFile.readIndexFile(java.util.jar.JarFile, java.util.jar.JarEntry, java.net.URL):java.net.URLClassLoader$IndexFile, file: URLClassLoader$IndexFile.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        static java.net.URLClassLoader.IndexFile readIndexFile(java.util.jar.JarFile r0, java.util.jar.JarEntry r1, java.net.URL r2) {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.net.URLClassLoader.IndexFile.readIndexFile(java.util.jar.JarFile, java.util.jar.JarEntry, java.net.URL):java.net.URLClassLoader$IndexFile, file: URLClassLoader$IndexFile.class
            */
            throw new UnsupportedOperationException("Method not decompiled: java.net.URLClassLoader.IndexFile.readIndexFile(java.util.jar.JarFile, java.util.jar.JarEntry, java.net.URL):java.net.URLClassLoader$IndexFile");
        }

        private static URL getParentURL(URL url) throws IOException {
            URL fileURL = ((JarURLConnection) url.openConnection()).getJarFileURL();
            String file = fileURL.getFile();
            String parentFile = new File(file).getParent().replace(File.separatorChar, '/');
            if (parentFile.charAt(0) != '/') {
                parentFile = Separators.SLASH + parentFile;
            }
            URL parentURL = new URL(fileURL.getProtocol(), fileURL.getHost(), fileURL.getPort(), parentFile);
            return parentURL;
        }

        public IndexFile(HashMap<String, ArrayList<URL>> map) {
            this.map = map;
        }

        ArrayList<URL> get(String name) {
            return this.map.get(name);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: URLClassLoader$URLHandler.class */
    public class URLHandler {
        URL url;
        URL codeSourceUrl;

        public URLHandler(URL url) {
            this.url = url;
            this.codeSourceUrl = url;
        }

        void findResources(String name, ArrayList<URL> resources) {
            URL res = findResource(name);
            if (res != null && !resources.contains(res)) {
                resources.add(res);
            }
        }

        Class<?> findClass(String packageName, String name, String origName) {
            URL resURL = targetURL(this.url, name);
            if (resURL != null) {
                try {
                    InputStream is = resURL.openStream();
                    return createClass(is, packageName, origName);
                } catch (IOException e) {
                    return null;
                }
            }
            return null;
        }

        Class<?> createClass(InputStream is, String packageName, String origName) {
            if (is == null) {
                return null;
            }
            try {
                byte[] clBuf = Streams.readFully(is);
                if (packageName != null) {
                    String packageDotName = packageName.replace('/', '.');
                    Package packageObj = URLClassLoader.access$000(URLClassLoader.this, packageDotName);
                    if (packageObj == null) {
                        URLClassLoader.access$100(URLClassLoader.this, packageDotName, null, null, null, null, null, null, null);
                    } else if (packageObj.isSealed()) {
                        throw new SecurityException("Package is sealed");
                    }
                }
                return URLClassLoader.access$200(URLClassLoader.this, origName, clBuf, 0, clBuf.length, new CodeSource(this.codeSourceUrl, (Certificate[]) null));
            } catch (IOException e) {
                return null;
            }
        }

        URL findResource(String name) {
            URL resURL = targetURL(this.url, name);
            if (resURL != null) {
                try {
                    URLConnection uc = resURL.openConnection();
                    uc.getInputStream().close();
                    if (!resURL.getProtocol().equals("http")) {
                        return resURL;
                    }
                    int code = ((HttpURLConnection) uc).getResponseCode();
                    if (code >= 200 && code < 300) {
                        return resURL;
                    }
                    return null;
                } catch (IOException e) {
                    return null;
                } catch (SecurityException e2) {
                    return null;
                }
            }
            return null;
        }

        URL targetURL(URL base, String name) {
            try {
                StringBuilder fileBuilder = new StringBuilder();
                fileBuilder.append(base.getFile());
                URI.PATH_ENCODER.appendEncoded(fileBuilder, name);
                String file = fileBuilder.toString();
                return new URL(base.getProtocol(), base.getHost(), base.getPort(), file, null);
            } catch (MalformedURLException e) {
                return null;
            }
        }
    }

    /* loaded from: URLClassLoader$URLJarHandler.class */
    class URLJarHandler extends URLHandler {
        final JarFile jf;
        final String prefixName;
        final IndexFile index;
        final Map<URL, URLHandler> subHandlers;

        public URLJarHandler(URL url, URL jarURL, JarFile jf, String prefixName) {
            super(url);
            this.subHandlers = new HashMap();
            this.jf = jf;
            this.prefixName = prefixName;
            this.codeSourceUrl = jarURL;
            JarEntry je = jf.getJarEntry("META-INF/INDEX.LIST");
            this.index = je == null ? null : IndexFile.readIndexFile(jf, je, url);
        }

        public URLJarHandler(URL url, URL jarURL, JarFile jf, String prefixName, IndexFile index) {
            super(url);
            this.subHandlers = new HashMap();
            this.jf = jf;
            this.prefixName = prefixName;
            this.index = index;
            this.codeSourceUrl = jarURL;
        }

        IndexFile getIndex() {
            return this.index;
        }

        @Override // java.net.URLClassLoader.URLHandler
        void findResources(String name, ArrayList<URL> resources) {
            URL res = findResourceInOwn(name);
            if (res != null && !resources.contains(res)) {
                resources.add(res);
            }
            if (this.index != null) {
                int pos = name.lastIndexOf(Separators.SLASH);
                String indexedName = pos > 0 ? name.substring(0, pos) : name;
                ArrayList<URL> urls = this.index.get(indexedName);
                if (urls != null) {
                    urls.remove(this.url);
                    Iterator i$ = urls.iterator();
                    while (i$.hasNext()) {
                        URL url = i$.next();
                        URLHandler h = getSubHandler(url);
                        if (h != null) {
                            h.findResources(name, resources);
                        }
                    }
                }
            }
        }

        @Override // java.net.URLClassLoader.URLHandler
        Class<?> findClass(String packageName, String name, String origName) {
            ArrayList<URL> urls;
            Class<?> res;
            String entryName = this.prefixName + name;
            JarEntry entry = this.jf.getJarEntry(entryName);
            if (entry != null) {
                try {
                    Manifest manifest = this.jf.getManifest();
                    return createClass(entry, manifest, packageName, origName);
                } catch (IOException e) {
                }
            }
            if (this.index != null) {
                if (packageName == null) {
                    urls = this.index.get(name);
                } else {
                    urls = this.index.get(packageName);
                }
                if (urls != null) {
                    urls.remove(this.url);
                    Iterator i$ = urls.iterator();
                    while (i$.hasNext()) {
                        URL url = i$.next();
                        URLHandler h = getSubHandler(url);
                        if (h != null && (res = h.findClass(packageName, name, origName)) != null) {
                            return res;
                        }
                    }
                    return null;
                }
                return null;
            }
            return null;
        }

        private Class<?> createClass(JarEntry entry, Manifest manifest, String packageName, String origName) {
            try {
                InputStream is = this.jf.getInputStream(entry);
                byte[] clBuf = Streams.readFully(is);
                if (packageName != null) {
                    String packageDotName = packageName.replace('/', '.');
                    Package packageObj = URLClassLoader.access$300(URLClassLoader.this, packageDotName);
                    if (packageObj == null) {
                        if (manifest != null) {
                            URLClassLoader.this.definePackage(packageDotName, manifest, this.codeSourceUrl);
                        } else {
                            URLClassLoader.access$400(URLClassLoader.this, packageDotName, null, null, null, null, null, null, null);
                        }
                    } else {
                        boolean exception = packageObj.isSealed();
                        if (manifest != null && URLClassLoader.access$500(URLClassLoader.this, manifest, packageName + Separators.SLASH)) {
                            exception = !packageObj.isSealed(this.codeSourceUrl);
                        }
                        if (exception) {
                            throw new SecurityException(String.format("Package %s is sealed", packageName));
                        }
                    }
                }
                CodeSource codeS = new CodeSource(this.codeSourceUrl, entry.getCertificates());
                return URLClassLoader.access$600(URLClassLoader.this, origName, clBuf, 0, clBuf.length, codeS);
            } catch (IOException e) {
                return null;
            }
        }

        URL findResourceInOwn(String name) {
            String entryName = this.prefixName + name;
            if (this.jf.getEntry(entryName) != null) {
                return targetURL(this.url, name);
            }
            return null;
        }

        @Override // java.net.URLClassLoader.URLHandler
        URL findResource(String name) {
            URL res;
            URL res2 = findResourceInOwn(name);
            if (res2 != null) {
                return res2;
            }
            if (this.index != null) {
                int pos = name.lastIndexOf(Separators.SLASH);
                String indexedName = pos > 0 ? name.substring(0, pos) : name;
                ArrayList<URL> urls = this.index.get(indexedName);
                if (urls != null) {
                    urls.remove(this.url);
                    Iterator i$ = urls.iterator();
                    while (i$.hasNext()) {
                        URL url = i$.next();
                        URLHandler h = getSubHandler(url);
                        if (h != null && (res = h.findResource(name)) != null) {
                            return res;
                        }
                    }
                    return null;
                }
                return null;
            }
            return null;
        }

        private synchronized URLHandler getSubHandler(URL url) {
            URLHandler sub;
            URLHandler sub2 = this.subHandlers.get(url);
            if (sub2 != null) {
                return sub2;
            }
            String protocol = url.getProtocol();
            if (protocol.equals("jar")) {
                sub = URLClassLoader.access$700(URLClassLoader.this, url);
            } else if (protocol.equals(ContentResolver.SCHEME_FILE)) {
                sub = createURLSubJarHandler(url);
            } else {
                sub = URLClassLoader.access$800(URLClassLoader.this, url);
            }
            if (sub != null) {
                this.subHandlers.put(url, sub);
            }
            return sub;
        }

        private URLHandler createURLSubJarHandler(URL url) {
            String prefixName;
            String file = url.getFile();
            if (url.getFile().endsWith("!/")) {
                prefixName = "";
            } else {
                int sepIdx = file.lastIndexOf("!/");
                if (sepIdx == -1) {
                    return null;
                }
                prefixName = file.substring(sepIdx + 2);
            }
            try {
                URL jarURL = ((JarURLConnection) url.openConnection()).getJarFileURL();
                JarURLConnection juc = (JarURLConnection) new URL("jar", "", jarURL.toExternalForm() + "!/").openConnection();
                JarFile jf = juc.getJarFile();
                URLJarHandler jarH = new URLJarHandler(url, jarURL, jf, prefixName, null);
                return jarH;
            } catch (IOException e) {
                return null;
            }
        }
    }

    /* loaded from: URLClassLoader$URLFileHandler.class */
    class URLFileHandler extends URLHandler {
        private String prefix;

        public URLFileHandler(URL url) {
            super(url);
            String baseFile = url.getFile();
            String host = url.getHost();
            int hostLength = 0;
            hostLength = host != null ? host.length() : hostLength;
            StringBuilder buf = new StringBuilder(2 + hostLength + baseFile.length());
            if (hostLength > 0) {
                buf.append("//").append(host);
            }
            buf.append(baseFile);
            this.prefix = buf.toString();
        }

        @Override // java.net.URLClassLoader.URLHandler
        Class<?> findClass(String packageName, String name, String origName) {
            String filename = this.prefix + name;
            try {
                File file = new File(URLDecoder.decode(filename, "UTF-8"));
                if (file.exists()) {
                    try {
                        InputStream is = new FileInputStream(file);
                        return createClass(is, packageName, origName);
                    } catch (FileNotFoundException e) {
                        return null;
                    }
                }
                return null;
            } catch (UnsupportedEncodingException e2) {
                return null;
            } catch (IllegalArgumentException e3) {
                return null;
            }
        }

        @Override // java.net.URLClassLoader.URLHandler
        URL findResource(String name) {
            int idx = 0;
            while (idx < name.length() && (name.charAt(idx) == '/' || name.charAt(idx) == '\\')) {
                idx++;
            }
            if (idx > 0) {
                name = name.substring(idx);
            }
            try {
                String filename = URLDecoder.decode(this.prefix, "UTF-8") + name;
                if (new File(filename).exists()) {
                    return targetURL(this.url, name);
                }
                return null;
            } catch (UnsupportedEncodingException e) {
                throw new AssertionError(e);
            } catch (IllegalArgumentException e2) {
                return null;
            }
        }
    }
}