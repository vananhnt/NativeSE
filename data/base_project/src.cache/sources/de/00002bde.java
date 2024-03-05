package org.ccil.cowan.tagsoup;

import gov.nist.core.Separators;
import gov.nist.javax.sip.header.ims.AuthorizationHeaderIms;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Hashtable;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;

/* loaded from: CommandLine.class */
public class CommandLine {
    static Hashtable options = new Hashtable();
    private static Parser theParser;
    private static HTMLSchema theSchema;
    private static String theOutputEncoding;

    static {
        options.put("--nocdata", Boolean.FALSE);
        options.put("--files", Boolean.FALSE);
        options.put("--reuse", Boolean.FALSE);
        options.put("--nons", Boolean.FALSE);
        options.put("--nobogons", Boolean.FALSE);
        options.put("--any", Boolean.FALSE);
        options.put("--emptybogons", Boolean.FALSE);
        options.put("--norootbogons", Boolean.FALSE);
        options.put("--pyxin", Boolean.FALSE);
        options.put("--lexical", Boolean.FALSE);
        options.put("--pyx", Boolean.FALSE);
        options.put("--html", Boolean.FALSE);
        options.put("--method=", Boolean.FALSE);
        options.put("--doctype-public=", Boolean.FALSE);
        options.put("--doctype-system=", Boolean.FALSE);
        options.put("--output-encoding=", Boolean.FALSE);
        options.put("--omit-xml-declaration", Boolean.FALSE);
        options.put("--encoding=", Boolean.FALSE);
        options.put("--help", Boolean.FALSE);
        options.put("--version", Boolean.FALSE);
        options.put("--nodefaults", Boolean.FALSE);
        options.put("--nocolons", Boolean.FALSE);
        options.put("--norestart", Boolean.FALSE);
        options.put("--ignorable", Boolean.FALSE);
        theParser = null;
        theSchema = null;
        theOutputEncoding = null;
    }

    public static void main(String[] argv) throws IOException, SAXException {
        String str;
        int optind = getopts(options, argv);
        if (hasOption(options, "--help")) {
            doHelp();
        } else if (hasOption(options, "--version")) {
            System.err.println("TagSoup version 1.2");
        } else if (argv.length == optind) {
            process("", System.out);
        } else if (hasOption(options, "--files")) {
            for (int i = optind; i < argv.length; i++) {
                String src = argv[i];
                int j = src.lastIndexOf(46);
                if (j == -1) {
                    str = src + ".xhtml";
                } else if (src.endsWith(".xhtml")) {
                    str = src + "_";
                } else {
                    str = src.substring(0, j) + ".xhtml";
                }
                String dst = str;
                System.err.println("src: " + src + " dst: " + dst);
                OutputStream os = new FileOutputStream(dst);
                process(src, os);
            }
        } else {
            for (int i2 = optind; i2 < argv.length; i2++) {
                System.err.println("src: " + argv[i2]);
                process(argv[i2], System.out);
            }
        }
    }

    private static void doHelp() {
        System.err.print("usage: java -jar tagsoup-*.jar ");
        System.err.print(" [ ");
        boolean first = true;
        Enumeration e = options.keys();
        while (e.hasMoreElements()) {
            if (!first) {
                System.err.print("| ");
            }
            first = false;
            String key = (String) e.nextElement();
            System.err.print(key);
            if (key.endsWith(Separators.EQUALS)) {
                System.err.print(Separators.QUESTION);
            }
            System.err.print(Separators.SP);
        }
        System.err.println("]*");
    }

    private static void process(String src, OutputStream os) throws IOException, SAXException {
        XMLReader r;
        Writer w;
        String encoding;
        if (hasOption(options, "--reuse")) {
            if (theParser == null) {
                theParser = new Parser();
            }
            r = theParser;
        } else {
            r = new Parser();
        }
        theSchema = new HTMLSchema();
        r.setProperty(Parser.schemaProperty, theSchema);
        if (hasOption(options, "--nocdata")) {
            r.setFeature(Parser.CDATAElementsFeature, false);
        }
        if (hasOption(options, "--nons") || hasOption(options, "--html")) {
            r.setFeature(Parser.namespacesFeature, false);
        }
        if (hasOption(options, "--nobogons")) {
            r.setFeature(Parser.ignoreBogonsFeature, true);
        }
        if (hasOption(options, "--any")) {
            r.setFeature(Parser.bogonsEmptyFeature, false);
        } else if (hasOption(options, "--emptybogons")) {
            r.setFeature(Parser.bogonsEmptyFeature, true);
        }
        if (hasOption(options, "--norootbogons")) {
            r.setFeature(Parser.rootBogonsFeature, false);
        }
        if (hasOption(options, "--nodefaults")) {
            r.setFeature(Parser.defaultAttributesFeature, false);
        }
        if (hasOption(options, "--nocolons")) {
            r.setFeature(Parser.translateColonsFeature, true);
        }
        if (hasOption(options, "--norestart")) {
            r.setFeature(Parser.restartElementsFeature, false);
        }
        if (hasOption(options, "--ignorable")) {
            r.setFeature(Parser.ignorableWhitespaceFeature, true);
        }
        if (hasOption(options, "--pyxin")) {
            r.setProperty(Parser.scannerProperty, new PYXScanner());
        }
        if (theOutputEncoding == null) {
            w = new OutputStreamWriter(os);
        } else {
            w = new OutputStreamWriter(os, theOutputEncoding);
        }
        ContentHandler h = chooseContentHandler(w);
        r.setContentHandler(h);
        if (hasOption(options, "--lexical") && (h instanceof LexicalHandler)) {
            r.setProperty(Parser.lexicalHandlerProperty, h);
        }
        InputSource s = new InputSource();
        if (src != "") {
            s.setSystemId(src);
        } else {
            s.setByteStream(System.in);
        }
        if (hasOption(options, "--encoding=") && (encoding = (String) options.get("--encoding=")) != null) {
            s.setEncoding(encoding);
        }
        r.parse(s);
    }

    private static ContentHandler chooseContentHandler(Writer w) {
        String doctype_system;
        String doctype_public;
        String method;
        if (hasOption(options, "--pyx")) {
            return new PYXWriter(w);
        }
        XMLWriter x = new XMLWriter(w);
        if (hasOption(options, "--html")) {
            x.setOutputProperty("method", "html");
            x.setOutputProperty("omit-xml-declaration", AuthorizationHeaderIms.YES);
        }
        if (hasOption(options, "--method=") && (method = (String) options.get("--method=")) != null) {
            x.setOutputProperty("method", method);
        }
        if (hasOption(options, "--doctype-public=") && (doctype_public = (String) options.get("--doctype-public=")) != null) {
            x.setOutputProperty("doctype-public", doctype_public);
        }
        if (hasOption(options, "--doctype-system=") && (doctype_system = (String) options.get("--doctype-system=")) != null) {
            x.setOutputProperty("doctype-system", doctype_system);
        }
        if (hasOption(options, "--output-encoding=")) {
            theOutputEncoding = (String) options.get("--output-encoding=");
            if (theOutputEncoding != null) {
                x.setOutputProperty("encoding", theOutputEncoding);
            }
        }
        if (hasOption(options, "--omit-xml-declaration")) {
            x.setOutputProperty("omit-xml-declaration", AuthorizationHeaderIms.YES);
        }
        x.setPrefix(theSchema.getURI(), "");
        return x;
    }

    private static int getopts(Hashtable options2, String[] argv) {
        int optind = 0;
        while (optind < argv.length) {
            String arg = argv[optind];
            String value = null;
            if (arg.charAt(0) != '-') {
                break;
            }
            int eqsign = arg.indexOf(61);
            if (eqsign != -1) {
                value = arg.substring(eqsign + 1, arg.length());
                arg = arg.substring(0, eqsign + 1);
            }
            if (options2.containsKey(arg)) {
                if (value == null) {
                    options2.put(arg, Boolean.TRUE);
                } else {
                    options2.put(arg, value);
                }
            } else {
                System.err.print("Unknown option ");
                System.err.println(arg);
                System.exit(1);
            }
            optind++;
        }
        return optind;
    }

    private static boolean hasOption(Hashtable options2, String option) {
        return Boolean.getBoolean(option) || options2.get(option) != Boolean.FALSE;
    }
}