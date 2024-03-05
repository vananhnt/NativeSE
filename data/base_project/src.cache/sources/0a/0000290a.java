package libcore.net.url;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.Permission;
import libcore.net.UriCodec;

/* loaded from: FileURLConnection.class */
public class FileURLConnection extends URLConnection {
    private String filename;
    private InputStream is;
    private int length;
    private boolean isDir;
    private FilePermission permission;

    public FileURLConnection(URL url) {
        super(url);
        this.length = -1;
        this.filename = url.getFile();
        if (this.filename == null) {
            this.filename = "";
        }
        this.filename = UriCodec.decode(this.filename);
    }

    @Override // java.net.URLConnection
    public void connect() throws IOException {
        File f = new File(this.filename);
        if (f.isDirectory()) {
            this.isDir = true;
            this.is = getDirectoryListing(f);
        } else {
            this.is = new BufferedInputStream(new FileInputStream(f));
            long lengthAsLong = f.length();
            this.length = lengthAsLong <= 2147483647L ? (int) lengthAsLong : Integer.MAX_VALUE;
        }
        this.connected = true;
    }

    @Override // java.net.URLConnection
    public int getContentLength() {
        try {
            if (!this.connected) {
                connect();
            }
        } catch (IOException e) {
        }
        return this.length;
    }

    @Override // java.net.URLConnection
    public String getContentType() {
        try {
            if (!this.connected) {
                connect();
            }
            if (this.isDir) {
                return "text/plain";
            }
            String result = guessContentTypeFromName(this.url.getFile());
            if (result != null) {
                return result;
            }
            try {
                result = guessContentTypeFromStream(this.is);
            } catch (IOException e) {
            }
            if (result != null) {
                return result;
            }
            return "content/unknown";
        } catch (IOException e2) {
            return "content/unknown";
        }
    }

    private InputStream getDirectoryListing(File f) {
        String[] fileList = f.list();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(bytes);
        out.print("<title>Directory Listing</title>\n");
        out.print("<base href=\"file:");
        out.print(f.getPath().replace('\\', '/') + "/\"><h1>" + f.getPath() + "</h1>\n<hr>\n");
        for (int i = 0; i < fileList.length; i++) {
            out.print(fileList[i] + "<br>\n");
        }
        out.close();
        return new ByteArrayInputStream(bytes.toByteArray());
    }

    @Override // java.net.URLConnection
    public InputStream getInputStream() throws IOException {
        if (!this.connected) {
            connect();
        }
        return this.is;
    }

    @Override // java.net.URLConnection
    public Permission getPermission() throws IOException {
        if (this.permission == null) {
            String path = this.filename;
            if (File.separatorChar != '/') {
                path = path.replace('/', File.separatorChar);
            }
            this.permission = new FilePermission(path, "read");
        }
        return this.permission;
    }
}