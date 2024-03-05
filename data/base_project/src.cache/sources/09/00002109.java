package gov.nist.javax.sip.address;

import gov.nist.core.Separators;

/* loaded from: UserInfo.class */
public final class UserInfo extends NetObject {
    private static final long serialVersionUID = 7268593273924256144L;
    protected String user;
    protected String password;
    protected int userType;
    public static final int TELEPHONE_SUBSCRIBER = 1;
    public static final int USER = 2;

    @Override // gov.nist.javax.sip.address.NetObject, gov.nist.core.GenericObject
    public boolean equals(Object obj) {
        if (getClass() != obj.getClass()) {
            return false;
        }
        UserInfo other = (UserInfo) obj;
        if (this.userType != other.userType || !this.user.equalsIgnoreCase(other.user)) {
            return false;
        }
        if (this.password != null && other.password == null) {
            return false;
        }
        if (other.password != null && this.password == null) {
            return false;
        }
        if (this.password == other.password) {
            return true;
        }
        return this.password.equals(other.password);
    }

    @Override // gov.nist.core.GenericObject
    public String encode() {
        return encode(new StringBuffer()).toString();
    }

    @Override // gov.nist.core.GenericObject
    public StringBuffer encode(StringBuffer buffer) {
        if (this.password != null) {
            buffer.append(this.user).append(Separators.COLON).append(this.password);
        } else {
            buffer.append(this.user);
        }
        return buffer;
    }

    public void clearPassword() {
        this.password = null;
    }

    public int getUserType() {
        return this.userType;
    }

    public String getUser() {
        return this.user;
    }

    public String getPassword() {
        return this.password;
    }

    public void setUser(String user) {
        this.user = user;
        if (user != null && (user.indexOf(Separators.POUND) >= 0 || user.indexOf(Separators.SEMICOLON) >= 0)) {
            setUserType(1);
        } else {
            setUserType(2);
        }
    }

    public void setPassword(String p) {
        this.password = p;
    }

    public void setUserType(int type) throws IllegalArgumentException {
        if (type != 1 && type != 2) {
            throw new IllegalArgumentException("Parameter not in range");
        }
        this.userType = type;
    }
}