package gov.nist.javax.sip.header;

import javax.sip.InvalidArgumentException;
import javax.sip.header.AcceptHeader;

/* loaded from: Accept.class */
public final class Accept extends ParametersHeader implements AcceptHeader {
    private static final long serialVersionUID = -7866187924308658151L;
    protected MediaRange mediaRange;

    public Accept() {
        super("Accept");
    }

    @Override // javax.sip.header.AcceptHeader
    public boolean allowsAllContentTypes() {
        return this.mediaRange != null && this.mediaRange.type.compareTo("*") == 0;
    }

    @Override // javax.sip.header.AcceptHeader
    public boolean allowsAllContentSubTypes() {
        return this.mediaRange != null && this.mediaRange.getSubtype().compareTo("*") == 0;
    }

    @Override // gov.nist.javax.sip.header.ParametersHeader, gov.nist.javax.sip.header.SIPHeader
    protected String encodeBody() {
        return encodeBody(new StringBuffer()).toString();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // gov.nist.javax.sip.header.SIPHeader
    public StringBuffer encodeBody(StringBuffer buffer) {
        if (this.mediaRange != null) {
            this.mediaRange.encode(buffer);
        }
        if (this.parameters != null && !this.parameters.isEmpty()) {
            buffer.append(';');
            this.parameters.encode(buffer);
        }
        return buffer;
    }

    public MediaRange getMediaRange() {
        return this.mediaRange;
    }

    @Override // javax.sip.header.MediaType
    public String getContentType() {
        if (this.mediaRange == null) {
            return null;
        }
        return this.mediaRange.getType();
    }

    @Override // javax.sip.header.MediaType
    public String getContentSubType() {
        if (this.mediaRange == null) {
            return null;
        }
        return this.mediaRange.getSubtype();
    }

    @Override // javax.sip.header.AcceptHeader
    public float getQValue() {
        return getParameterAsFloat("q");
    }

    @Override // javax.sip.header.AcceptHeader
    public boolean hasQValue() {
        return super.hasParameter("q");
    }

    @Override // javax.sip.header.AcceptHeader
    public void removeQValue() {
        super.removeParameter("q");
    }

    @Override // javax.sip.header.MediaType
    public void setContentSubType(String subtype) {
        if (this.mediaRange == null) {
            this.mediaRange = new MediaRange();
        }
        this.mediaRange.setSubtype(subtype);
    }

    @Override // javax.sip.header.MediaType
    public void setContentType(String type) {
        if (this.mediaRange == null) {
            this.mediaRange = new MediaRange();
        }
        this.mediaRange.setType(type);
    }

    @Override // javax.sip.header.AcceptHeader
    public void setQValue(float qValue) throws InvalidArgumentException {
        if (qValue == -1.0f) {
            super.removeParameter("q");
        }
        super.setParameter("q", qValue);
    }

    public void setMediaRange(MediaRange m) {
        this.mediaRange = m;
    }

    @Override // gov.nist.javax.sip.header.ParametersHeader, gov.nist.core.GenericObject
    public Object clone() {
        Accept retval = (Accept) super.clone();
        if (this.mediaRange != null) {
            retval.mediaRange = (MediaRange) this.mediaRange.clone();
        }
        return retval;
    }
}