package org.apache.harmony.xml.dom;

import android.provider.ContactsContract;
import java.util.Map;
import java.util.TreeMap;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMStringList;

/* loaded from: DOMConfigurationImpl.class */
public final class DOMConfigurationImpl implements DOMConfiguration {
    private static final Map<String, Parameter> PARAMETERS = new TreeMap(String.CASE_INSENSITIVE_ORDER);
    private DOMErrorHandler errorHandler;
    private String schemaLocation;
    private String schemaType;
    private boolean cdataSections = true;
    private boolean comments = true;
    private boolean datatypeNormalization = false;
    private boolean entities = true;
    private boolean namespaces = true;
    private boolean splitCdataSections = true;
    private boolean validate = false;
    private boolean wellFormed = true;

    /* loaded from: DOMConfigurationImpl$Parameter.class */
    interface Parameter {
        Object get(DOMConfigurationImpl dOMConfigurationImpl);

        void set(DOMConfigurationImpl dOMConfigurationImpl, Object obj);

        boolean canSet(DOMConfigurationImpl dOMConfigurationImpl, Object obj);
    }

    static {
        PARAMETERS.put("canonical-form", new FixedParameter(false));
        PARAMETERS.put("cdata-sections", new BooleanParameter() { // from class: org.apache.harmony.xml.dom.DOMConfigurationImpl.1
            @Override // org.apache.harmony.xml.dom.DOMConfigurationImpl.Parameter
            public Object get(DOMConfigurationImpl config) {
                return Boolean.valueOf(config.cdataSections);
            }

            @Override // org.apache.harmony.xml.dom.DOMConfigurationImpl.Parameter
            public void set(DOMConfigurationImpl config, Object value) {
                config.cdataSections = ((Boolean) value).booleanValue();
            }
        });
        PARAMETERS.put("check-character-normalization", new FixedParameter(false));
        PARAMETERS.put(ContactsContract.StreamItemsColumns.COMMENTS, new BooleanParameter() { // from class: org.apache.harmony.xml.dom.DOMConfigurationImpl.2
            @Override // org.apache.harmony.xml.dom.DOMConfigurationImpl.Parameter
            public Object get(DOMConfigurationImpl config) {
                return Boolean.valueOf(config.comments);
            }

            @Override // org.apache.harmony.xml.dom.DOMConfigurationImpl.Parameter
            public void set(DOMConfigurationImpl config, Object value) {
                config.comments = ((Boolean) value).booleanValue();
            }
        });
        PARAMETERS.put("datatype-normalization", new BooleanParameter() { // from class: org.apache.harmony.xml.dom.DOMConfigurationImpl.3
            @Override // org.apache.harmony.xml.dom.DOMConfigurationImpl.Parameter
            public Object get(DOMConfigurationImpl config) {
                return Boolean.valueOf(config.datatypeNormalization);
            }

            @Override // org.apache.harmony.xml.dom.DOMConfigurationImpl.Parameter
            public void set(DOMConfigurationImpl config, Object value) {
                if (((Boolean) value).booleanValue()) {
                    config.datatypeNormalization = true;
                    config.validate = true;
                    return;
                }
                config.datatypeNormalization = false;
            }
        });
        PARAMETERS.put("element-content-whitespace", new FixedParameter(true));
        PARAMETERS.put(ContactsContract.Contacts.Entity.CONTENT_DIRECTORY, new BooleanParameter() { // from class: org.apache.harmony.xml.dom.DOMConfigurationImpl.4
            @Override // org.apache.harmony.xml.dom.DOMConfigurationImpl.Parameter
            public Object get(DOMConfigurationImpl config) {
                return Boolean.valueOf(config.entities);
            }

            @Override // org.apache.harmony.xml.dom.DOMConfigurationImpl.Parameter
            public void set(DOMConfigurationImpl config, Object value) {
                config.entities = ((Boolean) value).booleanValue();
            }
        });
        PARAMETERS.put("error-handler", new Parameter() { // from class: org.apache.harmony.xml.dom.DOMConfigurationImpl.5
            @Override // org.apache.harmony.xml.dom.DOMConfigurationImpl.Parameter
            public Object get(DOMConfigurationImpl config) {
                return config.errorHandler;
            }

            @Override // org.apache.harmony.xml.dom.DOMConfigurationImpl.Parameter
            public void set(DOMConfigurationImpl config, Object value) {
                config.errorHandler = (DOMErrorHandler) value;
            }

            @Override // org.apache.harmony.xml.dom.DOMConfigurationImpl.Parameter
            public boolean canSet(DOMConfigurationImpl config, Object value) {
                return value == null || (value instanceof DOMErrorHandler);
            }
        });
        PARAMETERS.put("infoset", new BooleanParameter() { // from class: org.apache.harmony.xml.dom.DOMConfigurationImpl.6
            @Override // org.apache.harmony.xml.dom.DOMConfigurationImpl.Parameter
            public Object get(DOMConfigurationImpl config) {
                return Boolean.valueOf(!config.entities && !config.datatypeNormalization && !config.cdataSections && config.wellFormed && config.comments && config.namespaces);
            }

            @Override // org.apache.harmony.xml.dom.DOMConfigurationImpl.Parameter
            public void set(DOMConfigurationImpl config, Object value) {
                if (((Boolean) value).booleanValue()) {
                    config.entities = false;
                    config.datatypeNormalization = false;
                    config.cdataSections = false;
                    config.wellFormed = true;
                    config.comments = true;
                    config.namespaces = true;
                }
            }
        });
        PARAMETERS.put("namespaces", new BooleanParameter() { // from class: org.apache.harmony.xml.dom.DOMConfigurationImpl.7
            @Override // org.apache.harmony.xml.dom.DOMConfigurationImpl.Parameter
            public Object get(DOMConfigurationImpl config) {
                return Boolean.valueOf(config.namespaces);
            }

            @Override // org.apache.harmony.xml.dom.DOMConfigurationImpl.Parameter
            public void set(DOMConfigurationImpl config, Object value) {
                config.namespaces = ((Boolean) value).booleanValue();
            }
        });
        PARAMETERS.put("namespace-declarations", new FixedParameter(true));
        PARAMETERS.put("normalize-characters", new FixedParameter(false));
        PARAMETERS.put("schema-location", new Parameter() { // from class: org.apache.harmony.xml.dom.DOMConfigurationImpl.8
            @Override // org.apache.harmony.xml.dom.DOMConfigurationImpl.Parameter
            public Object get(DOMConfigurationImpl config) {
                return config.schemaLocation;
            }

            @Override // org.apache.harmony.xml.dom.DOMConfigurationImpl.Parameter
            public void set(DOMConfigurationImpl config, Object value) {
                config.schemaLocation = (String) value;
            }

            @Override // org.apache.harmony.xml.dom.DOMConfigurationImpl.Parameter
            public boolean canSet(DOMConfigurationImpl config, Object value) {
                return value == null || (value instanceof String);
            }
        });
        PARAMETERS.put("schema-type", new Parameter() { // from class: org.apache.harmony.xml.dom.DOMConfigurationImpl.9
            @Override // org.apache.harmony.xml.dom.DOMConfigurationImpl.Parameter
            public Object get(DOMConfigurationImpl config) {
                return config.schemaType;
            }

            @Override // org.apache.harmony.xml.dom.DOMConfigurationImpl.Parameter
            public void set(DOMConfigurationImpl config, Object value) {
                config.schemaType = (String) value;
            }

            @Override // org.apache.harmony.xml.dom.DOMConfigurationImpl.Parameter
            public boolean canSet(DOMConfigurationImpl config, Object value) {
                return value == null || (value instanceof String);
            }
        });
        PARAMETERS.put("split-cdata-sections", new BooleanParameter() { // from class: org.apache.harmony.xml.dom.DOMConfigurationImpl.10
            @Override // org.apache.harmony.xml.dom.DOMConfigurationImpl.Parameter
            public Object get(DOMConfigurationImpl config) {
                return Boolean.valueOf(config.splitCdataSections);
            }

            @Override // org.apache.harmony.xml.dom.DOMConfigurationImpl.Parameter
            public void set(DOMConfigurationImpl config, Object value) {
                config.splitCdataSections = ((Boolean) value).booleanValue();
            }
        });
        PARAMETERS.put("validate", new BooleanParameter() { // from class: org.apache.harmony.xml.dom.DOMConfigurationImpl.11
            @Override // org.apache.harmony.xml.dom.DOMConfigurationImpl.Parameter
            public Object get(DOMConfigurationImpl config) {
                return Boolean.valueOf(config.validate);
            }

            @Override // org.apache.harmony.xml.dom.DOMConfigurationImpl.Parameter
            public void set(DOMConfigurationImpl config, Object value) {
                config.validate = ((Boolean) value).booleanValue();
            }
        });
        PARAMETERS.put("validate-if-schema", new FixedParameter(false));
        PARAMETERS.put("well-formed", new BooleanParameter() { // from class: org.apache.harmony.xml.dom.DOMConfigurationImpl.12
            @Override // org.apache.harmony.xml.dom.DOMConfigurationImpl.Parameter
            public Object get(DOMConfigurationImpl config) {
                return Boolean.valueOf(config.wellFormed);
            }

            @Override // org.apache.harmony.xml.dom.DOMConfigurationImpl.Parameter
            public void set(DOMConfigurationImpl config, Object value) {
                config.wellFormed = ((Boolean) value).booleanValue();
            }
        });
    }

    /* loaded from: DOMConfigurationImpl$FixedParameter.class */
    static class FixedParameter implements Parameter {
        final Object onlyValue;

        FixedParameter(Object onlyValue) {
            this.onlyValue = onlyValue;
        }

        @Override // org.apache.harmony.xml.dom.DOMConfigurationImpl.Parameter
        public Object get(DOMConfigurationImpl config) {
            return this.onlyValue;
        }

        @Override // org.apache.harmony.xml.dom.DOMConfigurationImpl.Parameter
        public void set(DOMConfigurationImpl config, Object value) {
            if (!this.onlyValue.equals(value)) {
                throw new DOMException((short) 9, "Unsupported value: " + value);
            }
        }

        @Override // org.apache.harmony.xml.dom.DOMConfigurationImpl.Parameter
        public boolean canSet(DOMConfigurationImpl config, Object value) {
            return this.onlyValue.equals(value);
        }
    }

    /* loaded from: DOMConfigurationImpl$BooleanParameter.class */
    static abstract class BooleanParameter implements Parameter {
        BooleanParameter() {
        }

        @Override // org.apache.harmony.xml.dom.DOMConfigurationImpl.Parameter
        public boolean canSet(DOMConfigurationImpl config, Object value) {
            return value instanceof Boolean;
        }
    }

    @Override // org.w3c.dom.DOMConfiguration
    public boolean canSetParameter(String name, Object value) {
        Parameter parameter = PARAMETERS.get(name);
        return parameter != null && parameter.canSet(this, value);
    }

    @Override // org.w3c.dom.DOMConfiguration
    public void setParameter(String name, Object value) throws DOMException {
        Parameter parameter = PARAMETERS.get(name);
        if (parameter == null) {
            throw new DOMException((short) 8, "No such parameter: " + name);
        }
        try {
            parameter.set(this, value);
        } catch (ClassCastException e) {
            throw new DOMException((short) 17, "Invalid type for " + name + ": " + value.getClass());
        } catch (NullPointerException e2) {
            throw new DOMException((short) 17, "Null not allowed for " + name);
        }
    }

    @Override // org.w3c.dom.DOMConfiguration
    public Object getParameter(String name) throws DOMException {
        Parameter parameter = PARAMETERS.get(name);
        if (parameter == null) {
            throw new DOMException((short) 8, "No such parameter: " + name);
        }
        return parameter.get(this);
    }

    @Override // org.w3c.dom.DOMConfiguration
    public DOMStringList getParameterNames() {
        final String[] result = (String[]) PARAMETERS.keySet().toArray(new String[PARAMETERS.size()]);
        return new DOMStringList() { // from class: org.apache.harmony.xml.dom.DOMConfigurationImpl.13
            @Override // org.w3c.dom.DOMStringList
            public String item(int index) {
                if (index < result.length) {
                    return result[index];
                }
                return null;
            }

            @Override // org.w3c.dom.DOMStringList
            public int getLength() {
                return result.length;
            }

            @Override // org.w3c.dom.DOMStringList
            public boolean contains(String str) {
                return DOMConfigurationImpl.PARAMETERS.containsKey(str);
            }
        };
    }

    /* JADX WARN: Removed duplicated region for block: B:16:0x0091  */
    /* JADX WARN: Removed duplicated region for block: B:33:0x012c A[LOOP:1: B:31:0x0127->B:33:0x012c, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:40:0x016a A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:41:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void normalize(org.w3c.dom.Node r7) {
        /*
            Method dump skipped, instructions count: 363
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.harmony.xml.dom.DOMConfigurationImpl.normalize(org.w3c.dom.Node):void");
    }

    private void checkTextValidity(CharSequence s) {
        if (this.wellFormed && !isValid(s)) {
            report((short) 2, "wf-invalid-character");
        }
    }

    private boolean isValid(CharSequence text) {
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            boolean valid = c == '\t' || c == '\n' || c == '\r' || (c >= ' ' && c <= 55295) || (c >= 57344 && c <= 65533);
            if (!valid) {
                return false;
            }
        }
        return true;
    }

    private void report(short severity, String type) {
        if (this.errorHandler != null) {
            this.errorHandler.handleError(new DOMErrorImpl(severity, type));
        }
    }
}