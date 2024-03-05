package javax.security.auth.callback;

import java.io.Serializable;
import java.util.Arrays;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: PasswordCallback.class */
public class PasswordCallback implements Callback, Serializable {
    private static final long serialVersionUID = 2267422647454909926L;
    private String prompt;
    boolean echoOn;
    private char[] inputPassword;

    private void setPrompt(String prompt) throws IllegalArgumentException {
        if (prompt == null || prompt.length() == 0) {
            throw new IllegalArgumentException("Invalid prompt");
        }
        this.prompt = prompt;
    }

    public PasswordCallback(String prompt, boolean echoOn) {
        setPrompt(prompt);
        this.echoOn = echoOn;
    }

    public String getPrompt() {
        return this.prompt;
    }

    public boolean isEchoOn() {
        return this.echoOn;
    }

    public void setPassword(char[] password) {
        if (password == null) {
            this.inputPassword = password;
            return;
        }
        this.inputPassword = new char[password.length];
        System.arraycopy(password, 0, this.inputPassword, 0, this.inputPassword.length);
    }

    public char[] getPassword() {
        if (this.inputPassword != null) {
            char[] tmp = new char[this.inputPassword.length];
            System.arraycopy(this.inputPassword, 0, tmp, 0, tmp.length);
            return tmp;
        }
        return null;
    }

    public void clearPassword() {
        if (this.inputPassword != null) {
            Arrays.fill(this.inputPassword, (char) 0);
        }
    }
}