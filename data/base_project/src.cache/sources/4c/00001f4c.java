package com.android.server.pm;

import android.content.pm.PackageParser;
import android.content.pm.Signature;
import android.os.Environment;
import android.provider.Telephony;
import android.util.Slog;
import android.util.Xml;
import com.android.internal.util.XmlUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: SELinuxMMAC.class */
public final class SELinuxMMAC {
    private static final String TAG = "SELinuxMMAC";
    private static final boolean DEBUG_POLICY = false;
    private static final boolean DEBUG_POLICY_INSTALL = false;
    private static final HashMap<Signature, String> sSigSeinfo = new HashMap<>();
    private static final HashMap<String, String> sPackageSeinfo = new HashMap<>();
    private static final File[] INSTALL_POLICY_FILE = {new File(Environment.getDataDirectory(), "security/mac_permissions.xml"), new File(Environment.getRootDirectory(), "etc/security/mac_permissions.xml"), null};

    private static void flushInstallPolicy() {
        sSigSeinfo.clear();
        sPackageSeinfo.clear();
    }

    public static boolean readInstallPolicy() {
        return readInstallPolicy(INSTALL_POLICY_FILE);
    }

    public static boolean readInstallPolicy(File policyFile) {
        return readInstallPolicy(new File[]{policyFile, null});
    }

    private static boolean readInstallPolicy(File[] policyFiles) {
        FileReader policyFile = null;
        int i = 0;
        while (policyFile == null && policyFiles != null && policyFiles[i] != null) {
            try {
                policyFile = new FileReader(policyFiles[i]);
                break;
            } catch (FileNotFoundException e) {
                Slog.d(TAG, "Couldn't find install policy " + policyFiles[i].getPath());
                i++;
            }
        }
        if (policyFile == null) {
            Slog.d(TAG, "No policy file found. All seinfo values will be null.");
            return false;
        }
        Slog.d(TAG, "Using install policy file " + policyFiles[i].getPath());
        flushInstallPolicy();
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(policyFile);
            XmlUtils.beginDocument(parser, "policy");
            while (true) {
                XmlUtils.nextElement(parser);
                if (parser.getEventType() == 1) {
                    break;
                }
                String tagName = parser.getName();
                if ("signer".equals(tagName)) {
                    String cert = parser.getAttributeValue(null, "signature");
                    if (cert == null) {
                        Slog.w(TAG, "<signer> without signature at " + parser.getPositionDescription());
                        XmlUtils.skipCurrentTag(parser);
                    } else {
                        try {
                            Signature signature = new Signature(cert);
                            String seinfo = readSeinfoTag(parser);
                            if (seinfo != null) {
                                sSigSeinfo.put(signature, seinfo);
                            }
                        } catch (IllegalArgumentException e2) {
                            Slog.w(TAG, "<signer> with bad signature at " + parser.getPositionDescription(), e2);
                            XmlUtils.skipCurrentTag(parser);
                        }
                    }
                } else if ("default".equals(tagName)) {
                    String seinfo2 = readSeinfoTag(parser);
                    if (seinfo2 != null) {
                        sSigSeinfo.put(null, seinfo2);
                    }
                } else if (Telephony.Sms.Intents.EXTRA_PACKAGE_NAME.equals(tagName)) {
                    String pkgName = parser.getAttributeValue(null, "name");
                    if (pkgName == null) {
                        Slog.w(TAG, "<package> without name at " + parser.getPositionDescription());
                        XmlUtils.skipCurrentTag(parser);
                    } else {
                        String seinfo3 = readSeinfoTag(parser);
                        if (seinfo3 != null) {
                            sPackageSeinfo.put(pkgName, seinfo3);
                        }
                    }
                } else {
                    XmlUtils.skipCurrentTag(parser);
                }
            }
        } catch (IOException e3) {
            Slog.w(TAG, "Got execption parsing ", e3);
        } catch (XmlPullParserException e4) {
            Slog.w(TAG, "Got execption parsing ", e4);
        }
        try {
            policyFile.close();
            return true;
        } catch (IOException e5) {
            return true;
        }
    }

    private static String readSeinfoTag(XmlPullParser parser) throws IOException, XmlPullParserException {
        int outerDepth = parser.getDepth();
        String seinfo = null;
        while (true) {
            int type = parser.next();
            if (type == 1 || (type == 3 && parser.getDepth() <= outerDepth)) {
                break;
            } else if (type != 3 && type != 4) {
                String tagName = parser.getName();
                if ("seinfo".equals(tagName)) {
                    String seinfoValue = parser.getAttributeValue(null, "value");
                    if (validateValue(seinfoValue)) {
                        seinfo = seinfoValue;
                    } else {
                        Slog.w(TAG, "<seinfo> without valid value at " + parser.getPositionDescription());
                    }
                }
                XmlUtils.skipCurrentTag(parser);
            }
        }
        return seinfo;
    }

    private static boolean validateValue(String name) {
        int N;
        if (name == null || (N = name.length()) == 0) {
            return false;
        }
        for (int i = 0; i < N; i++) {
            char c = name.charAt(i);
            if ((c < 'a' || c > 'z') && ((c < 'A' || c > 'Z') && c != '_')) {
                return false;
            }
        }
        return true;
    }

    public static void assignSeinfoValue(PackageParser.Package pkg) {
        if ((pkg.applicationInfo.flags & 1) != 0 || (pkg.applicationInfo.flags & 128) != 0) {
            Signature[] arr$ = pkg.mSignatures;
            for (Signature s : arr$) {
                if (s != null && sSigSeinfo.containsKey(s)) {
                    pkg.applicationInfo.seinfo = sSigSeinfo.get(s);
                    return;
                }
            }
            if (sPackageSeinfo.containsKey(pkg.packageName)) {
                pkg.applicationInfo.seinfo = sPackageSeinfo.get(pkg.packageName);
                return;
            }
        }
        pkg.applicationInfo.seinfo = sSigSeinfo.get(null);
    }
}