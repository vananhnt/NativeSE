package java.security;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* JADX WARN: Classes with same name are omitted:
  
 */
@Deprecated
/* loaded from: Certificate.class */
public interface Certificate {
    void decode(InputStream inputStream) throws KeyException, IOException;

    void encode(OutputStream outputStream) throws KeyException, IOException;

    String getFormat();

    Principal getGuarantor();

    Principal getPrincipal();

    PublicKey getPublicKey();

    String toString(boolean z);
}