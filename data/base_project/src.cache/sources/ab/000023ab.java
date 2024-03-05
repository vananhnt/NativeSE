package java.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Permission;
import java.util.List;
import java.util.Map;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: URLConnection.class */
public abstract class URLConnection {
    protected URL url;
    protected long ifModifiedSince;
    protected boolean useCaches;
    protected boolean connected;
    protected boolean doOutput;
    protected boolean doInput;
    protected boolean allowUserInteraction;

    public abstract void connect() throws IOException;

    /* JADX INFO: Access modifiers changed from: protected */
    public URLConnection(URL url) {
        throw new RuntimeException("Stub!");
    }

    public boolean getAllowUserInteraction() {
        throw new RuntimeException("Stub!");
    }

    public Object getContent() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public Object getContent(Class[] types) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public String getContentEncoding() {
        throw new RuntimeException("Stub!");
    }

    public int getContentLength() {
        throw new RuntimeException("Stub!");
    }

    public String getContentType() {
        throw new RuntimeException("Stub!");
    }

    public long getDate() {
        throw new RuntimeException("Stub!");
    }

    public static boolean getDefaultAllowUserInteraction() {
        throw new RuntimeException("Stub!");
    }

    @Deprecated
    public static String getDefaultRequestProperty(String field) {
        throw new RuntimeException("Stub!");
    }

    public boolean getDefaultUseCaches() {
        throw new RuntimeException("Stub!");
    }

    public boolean getDoInput() {
        throw new RuntimeException("Stub!");
    }

    public boolean getDoOutput() {
        throw new RuntimeException("Stub!");
    }

    public long getExpiration() {
        throw new RuntimeException("Stub!");
    }

    public static FileNameMap getFileNameMap() {
        throw new RuntimeException("Stub!");
    }

    public String getHeaderField(int pos) {
        throw new RuntimeException("Stub!");
    }

    public Map<String, List<String>> getHeaderFields() {
        throw new RuntimeException("Stub!");
    }

    public Map<String, List<String>> getRequestProperties() {
        throw new RuntimeException("Stub!");
    }

    public void addRequestProperty(String field, String newValue) {
        throw new RuntimeException("Stub!");
    }

    public String getHeaderField(String key) {
        throw new RuntimeException("Stub!");
    }

    public long getHeaderFieldDate(String field, long defaultValue) {
        throw new RuntimeException("Stub!");
    }

    public int getHeaderFieldInt(String field, int defaultValue) {
        throw new RuntimeException("Stub!");
    }

    public String getHeaderFieldKey(int posn) {
        throw new RuntimeException("Stub!");
    }

    public long getIfModifiedSince() {
        throw new RuntimeException("Stub!");
    }

    public InputStream getInputStream() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public long getLastModified() {
        throw new RuntimeException("Stub!");
    }

    public OutputStream getOutputStream() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public Permission getPermission() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public String getRequestProperty(String field) {
        throw new RuntimeException("Stub!");
    }

    public URL getURL() {
        throw new RuntimeException("Stub!");
    }

    public boolean getUseCaches() {
        throw new RuntimeException("Stub!");
    }

    public static String guessContentTypeFromName(String url) {
        throw new RuntimeException("Stub!");
    }

    public static String guessContentTypeFromStream(InputStream is) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void setAllowUserInteraction(boolean newValue) {
        throw new RuntimeException("Stub!");
    }

    public static synchronized void setContentHandlerFactory(ContentHandlerFactory contentFactory) {
        throw new RuntimeException("Stub!");
    }

    public static void setDefaultAllowUserInteraction(boolean allows) {
        throw new RuntimeException("Stub!");
    }

    @Deprecated
    public static void setDefaultRequestProperty(String field, String value) {
        throw new RuntimeException("Stub!");
    }

    public void setDefaultUseCaches(boolean newValue) {
        throw new RuntimeException("Stub!");
    }

    public void setDoInput(boolean newValue) {
        throw new RuntimeException("Stub!");
    }

    public void setDoOutput(boolean newValue) {
        throw new RuntimeException("Stub!");
    }

    public static void setFileNameMap(FileNameMap map) {
        throw new RuntimeException("Stub!");
    }

    public void setIfModifiedSince(long newValue) {
        throw new RuntimeException("Stub!");
    }

    public void setRequestProperty(String field, String newValue) {
        throw new RuntimeException("Stub!");
    }

    public void setUseCaches(boolean newValue) {
        throw new RuntimeException("Stub!");
    }

    public void setConnectTimeout(int timeoutMillis) {
        throw new RuntimeException("Stub!");
    }

    public int getConnectTimeout() {
        throw new RuntimeException("Stub!");
    }

    public void setReadTimeout(int timeoutMillis) {
        throw new RuntimeException("Stub!");
    }

    public int getReadTimeout() {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }

    /* loaded from: URLConnection$DefaultContentHandler.class */
    static class DefaultContentHandler extends ContentHandler {
        DefaultContentHandler() {
        }

        @Override // java.net.ContentHandler
        public Object getContent(URLConnection u) throws IOException {
            return u.getInputStream();
        }
    }
}