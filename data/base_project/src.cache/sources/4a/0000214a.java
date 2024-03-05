package gov.nist.javax.sip.header;

import gov.nist.core.DuplicateNameValueList;
import gov.nist.core.NameValue;
import gov.nist.core.NameValueList;
import gov.nist.javax.sip.address.GenericURI;
import java.io.Serializable;
import java.text.ParseException;
import java.util.Iterator;
import javax.sip.header.Parameters;

/* loaded from: ParametersHeader.class */
public abstract class ParametersHeader extends SIPHeader implements Parameters, Serializable {
    protected NameValueList parameters;
    protected DuplicateNameValueList duplicates;

    @Override // gov.nist.javax.sip.header.SIPHeader
    protected abstract String encodeBody();

    /* JADX INFO: Access modifiers changed from: protected */
    public ParametersHeader() {
        this.parameters = new NameValueList();
        this.duplicates = new DuplicateNameValueList();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ParametersHeader(String hdrName) {
        super(hdrName);
        this.parameters = new NameValueList();
        this.duplicates = new DuplicateNameValueList();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ParametersHeader(String hdrName, boolean sync) {
        super(hdrName);
        this.parameters = new NameValueList(sync);
        this.duplicates = new DuplicateNameValueList();
    }

    @Override // javax.sip.header.Parameters
    public String getParameter(String name) {
        return this.parameters.getParameter(name);
    }

    public Object getParameterValue(String name) {
        return this.parameters.getValue(name);
    }

    @Override // javax.sip.header.Parameters
    public Iterator<String> getParameterNames() {
        return this.parameters.getNames();
    }

    public boolean hasParameters() {
        return (this.parameters == null || this.parameters.isEmpty()) ? false : true;
    }

    @Override // javax.sip.header.Parameters
    public void removeParameter(String name) {
        this.parameters.delete(name);
    }

    public void setParameter(String name, String value) throws ParseException {
        NameValue nv = this.parameters.getNameValue(name);
        if (nv != null) {
            nv.setValueAsObject(value);
            return;
        }
        this.parameters.set(new NameValue(name, value));
    }

    public void setQuotedParameter(String name, String value) throws ParseException {
        NameValue nv = this.parameters.getNameValue(name);
        if (nv != null) {
            nv.setValueAsObject(value);
            nv.setQuotedValue();
            return;
        }
        NameValue nv2 = new NameValue(name, value);
        nv2.setQuotedValue();
        this.parameters.set(nv2);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setParameter(String name, int value) {
        Integer val = Integer.valueOf(value);
        this.parameters.set(name, val);
    }

    protected void setParameter(String name, boolean value) {
        Boolean val = Boolean.valueOf(value);
        this.parameters.set(name, val);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setParameter(String name, float value) {
        Float val = Float.valueOf(value);
        NameValue nv = this.parameters.getNameValue(name);
        if (nv != null) {
            nv.setValueAsObject(val);
            return;
        }
        this.parameters.set(new NameValue(name, val));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setParameter(String name, Object value) {
        this.parameters.set(name, value);
    }

    public boolean hasParameter(String parameterName) {
        return this.parameters.hasNameValue(parameterName);
    }

    public void removeParameters() {
        this.parameters = new NameValueList();
    }

    public NameValueList getParameters() {
        return this.parameters;
    }

    public void setParameter(NameValue nameValue) {
        this.parameters.set(nameValue);
    }

    public void setParameters(NameValueList parameters) {
        this.parameters = parameters;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getParameterAsInt(String parameterName) {
        if (getParameterValue(parameterName) != null) {
            try {
                if (getParameterValue(parameterName) instanceof String) {
                    return Integer.parseInt(getParameter(parameterName));
                }
                return ((Integer) getParameterValue(parameterName)).intValue();
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getParameterAsHexInt(String parameterName) {
        if (getParameterValue(parameterName) != null) {
            try {
                if (getParameterValue(parameterName) instanceof String) {
                    return Integer.parseInt(getParameter(parameterName), 16);
                }
                return ((Integer) getParameterValue(parameterName)).intValue();
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public float getParameterAsFloat(String parameterName) {
        if (getParameterValue(parameterName) != null) {
            try {
                if (getParameterValue(parameterName) instanceof String) {
                    return Float.parseFloat(getParameter(parameterName));
                }
                return ((Float) getParameterValue(parameterName)).floatValue();
            } catch (NumberFormatException e) {
                return -1.0f;
            }
        }
        return -1.0f;
    }

    protected long getParameterAsLong(String parameterName) {
        if (getParameterValue(parameterName) != null) {
            try {
                if (getParameterValue(parameterName) instanceof String) {
                    return Long.parseLong(getParameter(parameterName));
                }
                return ((Long) getParameterValue(parameterName)).longValue();
            } catch (NumberFormatException e) {
                return -1L;
            }
        }
        return -1L;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public GenericURI getParameterAsURI(String parameterName) {
        Object val = getParameterValue(parameterName);
        if (val instanceof GenericURI) {
            return (GenericURI) val;
        }
        try {
            return new GenericURI((String) val);
        } catch (ParseException e) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean getParameterAsBoolean(String parameterName) {
        Object val = getParameterValue(parameterName);
        if (val == null) {
            return false;
        }
        if (val instanceof Boolean) {
            return ((Boolean) val).booleanValue();
        }
        if (val instanceof String) {
            return Boolean.valueOf((String) val).booleanValue();
        }
        return false;
    }

    public NameValue getNameValue(String parameterName) {
        return this.parameters.getNameValue(parameterName);
    }

    @Override // gov.nist.core.GenericObject
    public Object clone() {
        ParametersHeader retval = (ParametersHeader) super.clone();
        if (this.parameters != null) {
            retval.parameters = (NameValueList) this.parameters.clone();
        }
        return retval;
    }

    public void setMultiParameter(String name, String value) {
        NameValue nv = new NameValue();
        nv.setName(name);
        nv.setValue(value);
        this.duplicates.set(nv);
    }

    public void setMultiParameter(NameValue nameValue) {
        this.duplicates.set(nameValue);
    }

    public String getMultiParameter(String name) {
        return this.duplicates.getParameter(name);
    }

    public DuplicateNameValueList getMultiParameters() {
        return this.duplicates;
    }

    public Object getMultiParameterValue(String name) {
        return this.duplicates.getValue(name);
    }

    public Iterator<String> getMultiParameterNames() {
        return this.duplicates.getNames();
    }

    public boolean hasMultiParameters() {
        return (this.duplicates == null || this.duplicates.isEmpty()) ? false : true;
    }

    public void removeMultiParameter(String name) {
        this.duplicates.delete(name);
    }

    public boolean hasMultiParameter(String parameterName) {
        return this.duplicates.hasNameValue(parameterName);
    }

    public void removeMultiParameters() {
        this.duplicates = new DuplicateNameValueList();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final boolean equalParameters(Parameters other) {
        if (this == other) {
            return true;
        }
        Iterator i = getParameterNames();
        while (i.hasNext()) {
            String pname = i.next();
            String p1 = getParameter(pname);
            String p2 = other.getParameter(pname);
            if ((p1 == null) ^ (p2 == null)) {
                return false;
            }
            if (p1 != null && !p1.equalsIgnoreCase(p2)) {
                return false;
            }
        }
        Iterator i2 = other.getParameterNames();
        while (i2.hasNext()) {
            String pname2 = (String) i2.next();
            String p12 = other.getParameter(pname2);
            String p22 = getParameter(pname2);
            if ((p12 == null) ^ (p22 == null)) {
                return false;
            }
            if (p12 != null && !p12.equalsIgnoreCase(p22)) {
                return false;
            }
        }
        return true;
    }
}