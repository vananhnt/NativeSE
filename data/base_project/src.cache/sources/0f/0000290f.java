package libcore.net.url;

import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Permission;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/* loaded from: JarURLConnectionImpl.class */
public class JarURLConnectionImpl extends JarURLConnection {
    private static final HashMap<URL, JarFile> jarCache = new HashMap<>();
    private URL jarFileURL;
    private InputStream jarInput;
    private JarFile jarFile;
    private JarEntry jarEntry;
    private boolean closed;

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: libcore.net.url.JarURLConnectionImpl.openJarFile():java.util.jar.JarFile, file: JarURLConnectionImpl.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private java.util.jar.JarFile openJarFile() throws java.io.IOException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: libcore.net.url.JarURLConnectionImpl.openJarFile():java.util.jar.JarFile, file: JarURLConnectionImpl.class
        */
        throw new UnsupportedOperationException("Method not decompiled: libcore.net.url.JarURLConnectionImpl.openJarFile():java.util.jar.JarFile");
    }

    public JarURLConnectionImpl(URL url) throws MalformedURLException, IOException {
        super(url);
        this.jarFileURL = getJarFileURL();
        this.jarFileURLConnection = this.jarFileURL.openConnection();
    }

    @Override // java.net.URLConnection
    public void connect() throws IOException {
        if (!this.connected) {
            findJarFile();
            findJarEntry();
            this.connected = true;
        }
    }

    @Override // java.net.JarURLConnection
    public JarFile getJarFile() throws IOException {
        connect();
        return this.jarFile;
    }

    private void findJarFile() throws IOException {
        if (getUseCaches()) {
            synchronized (jarCache) {
                this.jarFile = jarCache.get(this.jarFileURL);
            }
            if (this.jarFile == null) {
                JarFile jar = openJarFile();
                synchronized (jarCache) {
                    this.jarFile = jarCache.get(this.jarFileURL);
                    if (this.jarFile == null) {
                        jarCache.put(this.jarFileURL, jar);
                        this.jarFile = jar;
                    } else {
                        jar.close();
                    }
                }
            }
        } else {
            this.jarFile = openJarFile();
        }
        if (this.jarFile == null) {
            throw new IOException();
        }
    }

    @Override // java.net.JarURLConnection
    public JarEntry getJarEntry() throws IOException {
        connect();
        return this.jarEntry;
    }

    private void findJarEntry() throws IOException {
        if (getEntryName() == null) {
            return;
        }
        this.jarEntry = this.jarFile.getJarEntry(getEntryName());
        if (this.jarEntry == null) {
            throw new FileNotFoundException(getEntryName());
        }
    }

    @Override // java.net.URLConnection
    public InputStream getInputStream() throws IOException {
        if (this.closed) {
            throw new IllegalStateException("JarURLConnection InputStream has been closed");
        }
        connect();
        if (this.jarInput != null) {
            return this.jarInput;
        }
        if (this.jarEntry == null) {
            throw new IOException("Jar entry not specified");
        }
        JarURLConnectionInputStream jarURLConnectionInputStream = new JarURLConnectionInputStream(this.jarFile.getInputStream(this.jarEntry), this.jarFile);
        this.jarInput = jarURLConnectionInputStream;
        return jarURLConnectionInputStream;
    }

    @Override // java.net.URLConnection
    public String getContentType() {
        if (this.url.getFile().endsWith("!/")) {
            return "x-java/jar";
        }
        String cType = null;
        String entryName = getEntryName();
        if (entryName != null) {
            cType = guessContentTypeFromName(entryName);
        } else {
            try {
                connect();
                cType = this.jarFileURLConnection.getContentType();
            } catch (IOException e) {
            }
        }
        if (cType == null) {
            cType = "content/unknown";
        }
        return cType;
    }

    @Override // java.net.URLConnection
    public int getContentLength() {
        try {
            connect();
            if (this.jarEntry == null) {
                return this.jarFileURLConnection.getContentLength();
            }
            return (int) getJarEntry().getSize();
        } catch (IOException e) {
            return -1;
        }
    }

    @Override // java.net.URLConnection
    public Object getContent() throws IOException {
        connect();
        if (this.jarEntry == null) {
            return this.jarFile;
        }
        return super.getContent();
    }

    @Override // java.net.URLConnection
    public Permission getPermission() throws IOException {
        return this.jarFileURLConnection.getPermission();
    }

    @Override // java.net.URLConnection
    public boolean getUseCaches() {
        return this.jarFileURLConnection.getUseCaches();
    }

    @Override // java.net.URLConnection
    public void setUseCaches(boolean usecaches) {
        this.jarFileURLConnection.setUseCaches(usecaches);
    }

    @Override // java.net.URLConnection
    public boolean getDefaultUseCaches() {
        return this.jarFileURLConnection.getDefaultUseCaches();
    }

    @Override // java.net.URLConnection
    public void setDefaultUseCaches(boolean defaultusecaches) {
        this.jarFileURLConnection.setDefaultUseCaches(defaultusecaches);
    }

    /* loaded from: JarURLConnectionImpl$JarURLConnectionInputStream.class */
    private class JarURLConnectionInputStream extends FilterInputStream {
        final JarFile jarFile;

        protected JarURLConnectionInputStream(InputStream in, JarFile file) {
            super(in);
            this.jarFile = file;
        }

        @Override // java.io.FilterInputStream, java.io.InputStream, java.io.Closeable
        public void close() throws IOException {
            super.close();
            if (!JarURLConnectionImpl.this.getUseCaches()) {
                JarURLConnectionImpl.this.closed = true;
                this.jarFile.close();
            }
        }
    }
}