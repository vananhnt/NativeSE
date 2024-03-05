package gov.nist.javax.sip.header;

import gov.nist.core.NameValue;
import gov.nist.core.Separators;
import java.util.Locale;
import javax.sip.InvalidArgumentException;
import javax.sip.header.AcceptLanguageHeader;

/* loaded from: AcceptLanguage.class */
public final class AcceptLanguage extends ParametersHeader implements AcceptLanguageHeader {
    private static final long serialVersionUID = -4473982069737324919L;
    protected String languageRange;

    public AcceptLanguage() {
        super("Accept-Language");
    }

    @Override // gov.nist.javax.sip.header.ParametersHeader, gov.nist.javax.sip.header.SIPHeader
    protected String encodeBody() {
        StringBuffer encoding = new StringBuffer();
        if (this.languageRange != null) {
            encoding.append(this.languageRange);
        }
        if (!this.parameters.isEmpty()) {
            encoding.append(Separators.SEMICOLON).append(this.parameters.encode());
        }
        return encoding.toString();
    }

    public String getLanguageRange() {
        return this.languageRange;
    }

    @Override // javax.sip.header.AcceptLanguageHeader
    public float getQValue() {
        if (!hasParameter("q")) {
            return -1.0f;
        }
        return ((Float) this.parameters.getValue("q")).floatValue();
    }

    @Override // javax.sip.header.AcceptLanguageHeader
    public boolean hasQValue() {
        return hasParameter("q");
    }

    @Override // javax.sip.header.AcceptLanguageHeader
    public void removeQValue() {
        removeParameter("q");
    }

    @Override // javax.sip.header.AcceptLanguageHeader
    public void setLanguageRange(String languageRange) {
        this.languageRange = languageRange.trim();
    }

    @Override // javax.sip.header.AcceptLanguageHeader
    public void setQValue(float q) throws InvalidArgumentException {
        if (q < 0.0d || q > 1.0d) {
            throw new InvalidArgumentException("qvalue out of range!");
        }
        if (q == -1.0f) {
            removeParameter("q");
        } else {
            setParameter(new NameValue("q", Float.valueOf(q)));
        }
    }

    @Override // javax.sip.header.AcceptLanguageHeader
    public Locale getAcceptLanguage() {
        if (this.languageRange == null) {
            return null;
        }
        int dash = this.languageRange.indexOf(45);
        if (dash >= 0) {
            return new Locale(this.languageRange.substring(0, dash), this.languageRange.substring(dash + 1));
        }
        return new Locale(this.languageRange);
    }

    @Override // javax.sip.header.AcceptLanguageHeader
    public void setAcceptLanguage(Locale language) {
        if ("".equals(language.getCountry())) {
            this.languageRange = language.getLanguage();
        } else {
            this.languageRange = language.getLanguage() + '-' + language.getCountry();
        }
    }
}