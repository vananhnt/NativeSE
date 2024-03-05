package gov.nist.javax.sip.header;

import gov.nist.core.Separators;

/* loaded from: MediaRange.class */
public class MediaRange extends SIPObject {
    private static final long serialVersionUID = -6297125815438079210L;
    protected String type;
    protected String subtype;

    public String getType() {
        return this.type;
    }

    public String getSubtype() {
        return this.subtype;
    }

    public void setType(String t) {
        this.type = t;
    }

    public void setSubtype(String s) {
        this.subtype = s;
    }

    @Override // gov.nist.javax.sip.header.SIPObject, gov.nist.core.GenericObject
    public String encode() {
        return encode(new StringBuffer()).toString();
    }

    @Override // gov.nist.javax.sip.header.SIPObject, gov.nist.core.GenericObject
    public StringBuffer encode(StringBuffer buffer) {
        return buffer.append(this.type).append(Separators.SLASH).append(this.subtype);
    }
}