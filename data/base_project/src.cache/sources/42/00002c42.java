package org.xml.sax.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: NamespaceSupport.class */
public class NamespaceSupport {
    public static final String XMLNS = "http://www.w3.org/XML/1998/namespace";
    public static final String NSDECL = "http://www.w3.org/xmlns/2000/";

    public NamespaceSupport() {
        throw new RuntimeException("Stub!");
    }

    public void reset() {
        throw new RuntimeException("Stub!");
    }

    public void pushContext() {
        throw new RuntimeException("Stub!");
    }

    public void popContext() {
        throw new RuntimeException("Stub!");
    }

    public boolean declarePrefix(String prefix, String uri) {
        throw new RuntimeException("Stub!");
    }

    public String[] processName(String qName, String[] parts, boolean isAttribute) {
        throw new RuntimeException("Stub!");
    }

    public String getURI(String prefix) {
        throw new RuntimeException("Stub!");
    }

    public Enumeration getPrefixes() {
        throw new RuntimeException("Stub!");
    }

    public String getPrefix(String uri) {
        throw new RuntimeException("Stub!");
    }

    public Enumeration getPrefixes(String uri) {
        throw new RuntimeException("Stub!");
    }

    public Enumeration getDeclaredPrefixes() {
        throw new RuntimeException("Stub!");
    }

    public void setNamespaceDeclUris(boolean value) {
        throw new RuntimeException("Stub!");
    }

    public boolean isNamespaceDeclUris() {
        throw new RuntimeException("Stub!");
    }

    /* loaded from: NamespaceSupport$Context.class */
    final class Context {
        Hashtable prefixTable;
        Hashtable uriTable;
        Hashtable elementNameTable;
        Hashtable attributeNameTable;
        String defaultNS = null;
        boolean declsOK = true;
        private ArrayList<String> declarations = null;
        private boolean declSeen = false;
        private Context parent = null;

        Context() {
            copyTables();
        }

        void setParent(Context parent) {
            this.parent = parent;
            this.declarations = null;
            this.prefixTable = parent.prefixTable;
            this.uriTable = parent.uriTable;
            this.elementNameTable = parent.elementNameTable;
            this.attributeNameTable = parent.attributeNameTable;
            this.defaultNS = parent.defaultNS;
            this.declSeen = false;
            this.declsOK = true;
        }

        void clear() {
            this.parent = null;
            this.prefixTable = null;
            this.uriTable = null;
            this.elementNameTable = null;
            this.attributeNameTable = null;
            this.defaultNS = null;
        }

        void declarePrefix(String prefix, String uri) {
            if (!this.declsOK) {
                throw new IllegalStateException("can't declare any more prefixes in this context");
            }
            if (!this.declSeen) {
                copyTables();
            }
            if (this.declarations == null) {
                this.declarations = new ArrayList<>();
            }
            String prefix2 = prefix.intern();
            String uri2 = uri.intern();
            if ("".equals(prefix2)) {
                if ("".equals(uri2)) {
                    this.defaultNS = null;
                } else {
                    this.defaultNS = uri2;
                }
            } else {
                this.prefixTable.put(prefix2, uri2);
                this.uriTable.put(uri2, prefix2);
            }
            this.declarations.add(prefix2);
        }

        String[] processName(String qName, boolean isAttribute) {
            Hashtable table;
            String uri;
            this.declsOK = false;
            if (isAttribute) {
                table = this.attributeNameTable;
            } else {
                table = this.elementNameTable;
            }
            String[] name = (String[]) table.get(qName);
            if (name != null) {
                return name;
            }
            String[] name2 = new String[3];
            name2[2] = qName.intern();
            int index = qName.indexOf(58);
            if (index == -1) {
                if (isAttribute) {
                    if (qName == "xmlns" && NamespaceSupport.access$000(NamespaceSupport.this)) {
                        name2[0] = "http://www.w3.org/xmlns/2000/";
                    } else {
                        name2[0] = "";
                    }
                } else if (this.defaultNS == null) {
                    name2[0] = "";
                } else {
                    name2[0] = this.defaultNS;
                }
                name2[1] = name2[2];
            } else {
                String prefix = qName.substring(0, index);
                String local = qName.substring(index + 1);
                if ("".equals(prefix)) {
                    uri = this.defaultNS;
                } else {
                    uri = (String) this.prefixTable.get(prefix);
                }
                if (uri == null) {
                    return null;
                }
                if (!isAttribute && "xmlns".equals(prefix)) {
                    return null;
                }
                name2[0] = uri;
                name2[1] = local.intern();
            }
            table.put(name2[2], name2);
            return name2;
        }

        String getURI(String prefix) {
            if ("".equals(prefix)) {
                return this.defaultNS;
            }
            if (this.prefixTable == null) {
                return null;
            }
            return (String) this.prefixTable.get(prefix);
        }

        String getPrefix(String uri) {
            if (this.uriTable == null) {
                return null;
            }
            return (String) this.uriTable.get(uri);
        }

        Enumeration getDeclaredPrefixes() {
            return this.declarations == null ? NamespaceSupport.access$100() : Collections.enumeration(this.declarations);
        }

        Enumeration getPrefixes() {
            if (this.prefixTable == null) {
                return NamespaceSupport.access$100();
            }
            return this.prefixTable.keys();
        }

        private void copyTables() {
            if (this.prefixTable != null) {
                this.prefixTable = (Hashtable) this.prefixTable.clone();
            } else {
                this.prefixTable = new Hashtable();
            }
            if (this.uriTable != null) {
                this.uriTable = (Hashtable) this.uriTable.clone();
            } else {
                this.uriTable = new Hashtable();
            }
            this.elementNameTable = new Hashtable();
            this.attributeNameTable = new Hashtable();
            this.declSeen = true;
        }
    }
}