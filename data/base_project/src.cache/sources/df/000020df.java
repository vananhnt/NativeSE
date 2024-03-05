package gov.nist.core;

/* loaded from: Token.class */
public class Token {
    protected String tokenValue;
    protected int tokenType;

    public String getTokenValue() {
        return this.tokenValue;
    }

    public int getTokenType() {
        return this.tokenType;
    }

    public String toString() {
        return "tokenValue = " + this.tokenValue + "/tokenType = " + this.tokenType;
    }
}