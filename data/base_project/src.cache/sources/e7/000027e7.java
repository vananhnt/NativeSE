package javax.net.ssl;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: X509KeyManager.class */
public interface X509KeyManager extends KeyManager {
    String chooseClientAlias(String[] strArr, Principal[] principalArr, Socket socket);

    String chooseServerAlias(String str, Principal[] principalArr, Socket socket);

    X509Certificate[] getCertificateChain(String str);

    String[] getClientAliases(String str, Principal[] principalArr);

    String[] getServerAliases(String str, Principal[] principalArr);

    PrivateKey getPrivateKey(String str);
}