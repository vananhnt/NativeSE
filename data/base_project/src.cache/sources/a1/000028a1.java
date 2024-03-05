package javax.xml.transform.stream;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import javax.xml.transform.Source;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: StreamSource.class */
public class StreamSource implements Source {
    public static final String FEATURE = "http://javax.xml.transform.stream.StreamSource/feature";
    private String publicId;
    private String systemId;
    private InputStream inputStream;
    private Reader reader;

    public StreamSource() {
    }

    public StreamSource(InputStream inputStream) {
        setInputStream(inputStream);
    }

    public StreamSource(InputStream inputStream, String systemId) {
        setInputStream(inputStream);
        setSystemId(systemId);
    }

    public StreamSource(Reader reader) {
        setReader(reader);
    }

    public StreamSource(Reader reader, String systemId) {
        setReader(reader);
        setSystemId(systemId);
    }

    public StreamSource(String systemId) {
        this.systemId = systemId;
    }

    public StreamSource(File f) {
        setSystemId(f);
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public InputStream getInputStream() {
        return this.inputStream;
    }

    public void setReader(Reader reader) {
        this.reader = reader;
    }

    public Reader getReader() {
        return this.reader;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public String getPublicId() {
        return this.publicId;
    }

    @Override // javax.xml.transform.Source
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    @Override // javax.xml.transform.Source
    public String getSystemId() {
        return this.systemId;
    }

    public void setSystemId(File f) {
        this.systemId = FilePathToURI.filepath2URI(f.getAbsolutePath());
    }
}