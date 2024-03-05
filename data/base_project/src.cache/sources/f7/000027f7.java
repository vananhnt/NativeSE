package javax.security.auth.callback;

import java.io.IOException;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: CallbackHandler.class */
public interface CallbackHandler {
    void handle(Callback[] callbackArr) throws IOException, UnsupportedCallbackException;
}