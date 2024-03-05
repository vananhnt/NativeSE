package javax.xml.namespace;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: QName.class */
public class QName implements Serializable {
    private static final long serialVersionUID;
    private static final long defaultSerialVersionUID = -9120448754896609940L;
    private static final long compatibilitySerialVersionUID = 4418622981026545151L;
    private final String namespaceURI;
    private final String localPart;
    private String prefix;
    private transient String qNameAsString;

    static {
        String compatPropValue = System.getProperty("org.apache.xml.namespace.QName.useCompatibleSerialVersionUID");
        serialVersionUID = !"1.0".equals(compatPropValue) ? defaultSerialVersionUID : compatibilitySerialVersionUID;
    }

    public QName(String namespaceURI, String localPart) {
        this(namespaceURI, localPart, "");
    }

    public QName(String namespaceURI, String localPart, String prefix) {
        if (namespaceURI == null) {
            this.namespaceURI = "";
        } else {
            this.namespaceURI = namespaceURI;
        }
        if (localPart == null) {
            throw new IllegalArgumentException("local part cannot be \"null\" when creating a QName");
        }
        this.localPart = localPart;
        if (prefix == null) {
            throw new IllegalArgumentException("prefix cannot be \"null\" when creating a QName");
        }
        this.prefix = prefix;
    }

    public QName(String localPart) {
        this("", localPart, "");
    }

    public String getNamespaceURI() {
        return this.namespaceURI;
    }

    public String getLocalPart() {
        return this.localPart;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public final boolean equals(Object objectToTest) {
        if (objectToTest == this) {
            return true;
        }
        if (objectToTest instanceof QName) {
            QName qName = (QName) objectToTest;
            return this.localPart.equals(qName.localPart) && this.namespaceURI.equals(qName.namespaceURI);
        }
        return false;
    }

    public final int hashCode() {
        return this.namespaceURI.hashCode() ^ this.localPart.hashCode();
    }

    public String toString() {
        String _qNameAsString = this.qNameAsString;
        if (_qNameAsString == null) {
            int nsLength = this.namespaceURI.length();
            if (nsLength == 0) {
                _qNameAsString = this.localPart;
            } else {
                StringBuilder buffer = new StringBuilder(nsLength + this.localPart.length() + 2);
                buffer.append('{');
                buffer.append(this.namespaceURI);
                buffer.append('}');
                buffer.append(this.localPart);
                _qNameAsString = buffer.toString();
            }
            this.qNameAsString = _qNameAsString;
        }
        return _qNameAsString;
    }

    public static QName valueOf(String qNameAsString) {
        if (qNameAsString == null) {
            throw new IllegalArgumentException("cannot create QName from \"null\" or \"\" String");
        }
        if (qNameAsString.length() == 0) {
            return new QName("", qNameAsString, "");
        }
        if (qNameAsString.charAt(0) != '{') {
            return new QName("", qNameAsString, "");
        }
        if (qNameAsString.startsWith("{}")) {
            throw new IllegalArgumentException("Namespace URI .equals(XMLConstants.NULL_NS_URI), .equals(\"\"), only the local part, \"" + qNameAsString.substring(2 + "".length()) + "\", should be provided.");
        }
        int endOfNamespaceURI = qNameAsString.indexOf(125);
        if (endOfNamespaceURI == -1) {
            throw new IllegalArgumentException("cannot create QName from \"" + qNameAsString + "\", missing closing \"}\"");
        }
        return new QName(qNameAsString.substring(1, endOfNamespaceURI), qNameAsString.substring(endOfNamespaceURI + 1), "");
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (this.prefix == null) {
            this.prefix = "";
        }
    }
}