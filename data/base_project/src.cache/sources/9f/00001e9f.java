package com.android.server.firewall;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.nfc.cardemulation.CardEmulation;
import android.os.PatternMatcher;
import android.security.KeyChain;
import java.io.IOException;
import java.util.regex.Pattern;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: StringFilter.class */
abstract class StringFilter implements Filter {
    private static final String ATTR_EQUALS = "equals";
    private static final String ATTR_STARTS_WITH = "startsWith";
    private static final String ATTR_CONTAINS = "contains";
    private static final String ATTR_PATTERN = "pattern";
    private static final String ATTR_REGEX = "regex";
    private static final String ATTR_IS_NULL = "isNull";
    private final ValueProvider mValueProvider;
    public static final ValueProvider COMPONENT = new ValueProvider(CardEmulation.EXTRA_SERVICE_COMPONENT) { // from class: com.android.server.firewall.StringFilter.1
        @Override // com.android.server.firewall.StringFilter.ValueProvider
        public String getValue(ComponentName resolvedComponent, Intent intent, String resolvedType) {
            if (resolvedComponent != null) {
                return resolvedComponent.flattenToString();
            }
            return null;
        }
    };
    public static final ValueProvider COMPONENT_NAME = new ValueProvider("component-name") { // from class: com.android.server.firewall.StringFilter.2
        @Override // com.android.server.firewall.StringFilter.ValueProvider
        public String getValue(ComponentName resolvedComponent, Intent intent, String resolvedType) {
            if (resolvedComponent != null) {
                return resolvedComponent.getClassName();
            }
            return null;
        }
    };
    public static final ValueProvider COMPONENT_PACKAGE = new ValueProvider("component-package") { // from class: com.android.server.firewall.StringFilter.3
        @Override // com.android.server.firewall.StringFilter.ValueProvider
        public String getValue(ComponentName resolvedComponent, Intent intent, String resolvedType) {
            if (resolvedComponent != null) {
                return resolvedComponent.getPackageName();
            }
            return null;
        }
    };
    public static final FilterFactory ACTION = new ValueProvider("action") { // from class: com.android.server.firewall.StringFilter.4
        @Override // com.android.server.firewall.StringFilter.ValueProvider
        public String getValue(ComponentName resolvedComponent, Intent intent, String resolvedType) {
            return intent.getAction();
        }
    };
    public static final ValueProvider DATA = new ValueProvider("data") { // from class: com.android.server.firewall.StringFilter.5
        @Override // com.android.server.firewall.StringFilter.ValueProvider
        public String getValue(ComponentName resolvedComponent, Intent intent, String resolvedType) {
            Uri data = intent.getData();
            if (data != null) {
                return data.toString();
            }
            return null;
        }
    };
    public static final ValueProvider MIME_TYPE = new ValueProvider("mime-type") { // from class: com.android.server.firewall.StringFilter.6
        @Override // com.android.server.firewall.StringFilter.ValueProvider
        public String getValue(ComponentName resolvedComponent, Intent intent, String resolvedType) {
            return resolvedType;
        }
    };
    public static final ValueProvider SCHEME = new ValueProvider("scheme") { // from class: com.android.server.firewall.StringFilter.7
        @Override // com.android.server.firewall.StringFilter.ValueProvider
        public String getValue(ComponentName resolvedComponent, Intent intent, String resolvedType) {
            Uri data = intent.getData();
            if (data != null) {
                return data.getScheme();
            }
            return null;
        }
    };
    public static final ValueProvider SSP = new ValueProvider("scheme-specific-part") { // from class: com.android.server.firewall.StringFilter.8
        @Override // com.android.server.firewall.StringFilter.ValueProvider
        public String getValue(ComponentName resolvedComponent, Intent intent, String resolvedType) {
            Uri data = intent.getData();
            if (data != null) {
                return data.getSchemeSpecificPart();
            }
            return null;
        }
    };
    public static final ValueProvider HOST = new ValueProvider(KeyChain.EXTRA_HOST) { // from class: com.android.server.firewall.StringFilter.9
        @Override // com.android.server.firewall.StringFilter.ValueProvider
        public String getValue(ComponentName resolvedComponent, Intent intent, String resolvedType) {
            Uri data = intent.getData();
            if (data != null) {
                return data.getHost();
            }
            return null;
        }
    };
    public static final ValueProvider PATH = new ValueProvider("path") { // from class: com.android.server.firewall.StringFilter.10
        @Override // com.android.server.firewall.StringFilter.ValueProvider
        public String getValue(ComponentName resolvedComponent, Intent intent, String resolvedType) {
            Uri data = intent.getData();
            if (data != null) {
                return data.getPath();
            }
            return null;
        }
    };

    protected abstract boolean matchesValue(String str);

    private StringFilter(ValueProvider valueProvider) {
        this.mValueProvider = valueProvider;
    }

    public static StringFilter readFromXml(ValueProvider valueProvider, XmlPullParser parser) throws IOException, XmlPullParserException {
        StringFilter filter = null;
        for (int i = 0; i < parser.getAttributeCount(); i++) {
            StringFilter newFilter = getFilter(valueProvider, parser, i);
            if (newFilter != null) {
                if (filter != null) {
                    throw new XmlPullParserException("Multiple string filter attributes found");
                }
                filter = newFilter;
            }
        }
        if (filter == null) {
            filter = new IsNullFilter(valueProvider, false);
        }
        return filter;
    }

    private static StringFilter getFilter(ValueProvider valueProvider, XmlPullParser parser, int attributeIndex) {
        String attributeName = parser.getAttributeName(attributeIndex);
        switch (attributeName.charAt(0)) {
            case 'c':
                if (!attributeName.equals(ATTR_CONTAINS)) {
                    return null;
                }
                return new ContainsFilter(valueProvider, parser.getAttributeValue(attributeIndex));
            case 'd':
            case 'f':
            case 'g':
            case 'h':
            case 'j':
            case 'k':
            case 'l':
            case 'm':
            case 'n':
            case 'o':
            case 'q':
            default:
                return null;
            case 'e':
                if (!attributeName.equals(ATTR_EQUALS)) {
                    return null;
                }
                return new EqualsFilter(valueProvider, parser.getAttributeValue(attributeIndex));
            case 'i':
                if (!attributeName.equals(ATTR_IS_NULL)) {
                    return null;
                }
                return new IsNullFilter(valueProvider, parser.getAttributeValue(attributeIndex));
            case 'p':
                if (!attributeName.equals(ATTR_PATTERN)) {
                    return null;
                }
                return new PatternStringFilter(valueProvider, parser.getAttributeValue(attributeIndex));
            case 'r':
                if (!attributeName.equals(ATTR_REGEX)) {
                    return null;
                }
                return new RegexFilter(valueProvider, parser.getAttributeValue(attributeIndex));
            case 's':
                if (!attributeName.equals(ATTR_STARTS_WITH)) {
                    return null;
                }
                return new StartsWithFilter(valueProvider, parser.getAttributeValue(attributeIndex));
        }
    }

    @Override // com.android.server.firewall.Filter
    public boolean matches(IntentFirewall ifw, ComponentName resolvedComponent, Intent intent, int callerUid, int callerPid, String resolvedType, int receivingUid) {
        String value = this.mValueProvider.getValue(resolvedComponent, intent, resolvedType);
        return matchesValue(value);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: StringFilter$ValueProvider.class */
    public static abstract class ValueProvider extends FilterFactory {
        public abstract String getValue(ComponentName componentName, Intent intent, String str);

        protected ValueProvider(String tag) {
            super(tag);
        }

        @Override // com.android.server.firewall.FilterFactory
        public Filter newFilter(XmlPullParser parser) throws IOException, XmlPullParserException {
            return StringFilter.readFromXml(this, parser);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: StringFilter$EqualsFilter.class */
    public static class EqualsFilter extends StringFilter {
        private final String mFilterValue;

        public EqualsFilter(ValueProvider valueProvider, String attrValue) {
            super(valueProvider);
            this.mFilterValue = attrValue;
        }

        @Override // com.android.server.firewall.StringFilter
        public boolean matchesValue(String value) {
            return value != null && value.equals(this.mFilterValue);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: StringFilter$ContainsFilter.class */
    public static class ContainsFilter extends StringFilter {
        private final String mFilterValue;

        public ContainsFilter(ValueProvider valueProvider, String attrValue) {
            super(valueProvider);
            this.mFilterValue = attrValue;
        }

        @Override // com.android.server.firewall.StringFilter
        public boolean matchesValue(String value) {
            return value != null && value.contains(this.mFilterValue);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: StringFilter$StartsWithFilter.class */
    public static class StartsWithFilter extends StringFilter {
        private final String mFilterValue;

        public StartsWithFilter(ValueProvider valueProvider, String attrValue) {
            super(valueProvider);
            this.mFilterValue = attrValue;
        }

        @Override // com.android.server.firewall.StringFilter
        public boolean matchesValue(String value) {
            return value != null && value.startsWith(this.mFilterValue);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: StringFilter$PatternStringFilter.class */
    public static class PatternStringFilter extends StringFilter {
        private final PatternMatcher mPattern;

        public PatternStringFilter(ValueProvider valueProvider, String attrValue) {
            super(valueProvider);
            this.mPattern = new PatternMatcher(attrValue, 2);
        }

        @Override // com.android.server.firewall.StringFilter
        public boolean matchesValue(String value) {
            return value != null && this.mPattern.match(value);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: StringFilter$RegexFilter.class */
    public static class RegexFilter extends StringFilter {
        private final Pattern mPattern;

        public RegexFilter(ValueProvider valueProvider, String attrValue) {
            super(valueProvider);
            this.mPattern = Pattern.compile(attrValue);
        }

        @Override // com.android.server.firewall.StringFilter
        public boolean matchesValue(String value) {
            return value != null && this.mPattern.matcher(value).matches();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: StringFilter$IsNullFilter.class */
    public static class IsNullFilter extends StringFilter {
        private final boolean mIsNull;

        public IsNullFilter(ValueProvider valueProvider, String attrValue) {
            super(valueProvider);
            this.mIsNull = Boolean.parseBoolean(attrValue);
        }

        public IsNullFilter(ValueProvider valueProvider, boolean isNull) {
            super(valueProvider);
            this.mIsNull = isNull;
        }

        @Override // com.android.server.firewall.StringFilter
        public boolean matchesValue(String value) {
            return (value == null) == this.mIsNull;
        }
    }
}