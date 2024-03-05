package org.w3c.dom.ls;

import java.io.InputStream;
import java.io.Reader;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: LSInput.class */
public interface LSInput {
    Reader getCharacterStream();

    void setCharacterStream(Reader reader);

    InputStream getByteStream();

    void setByteStream(InputStream inputStream);

    String getStringData();

    void setStringData(String str);

    String getSystemId();

    void setSystemId(String str);

    String getPublicId();

    void setPublicId(String str);

    String getBaseURI();

    void setBaseURI(String str);

    String getEncoding();

    void setEncoding(String str);

    boolean getCertifiedText();

    void setCertifiedText(boolean z);
}