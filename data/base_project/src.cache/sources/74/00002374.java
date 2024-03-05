package java.net;

import gov.nist.core.Separators;
import java.util.Locale;
import libcore.net.MimeUtils;

/* loaded from: DefaultFileNameMap.class */
class DefaultFileNameMap implements FileNameMap {
    DefaultFileNameMap() {
    }

    @Override // java.net.FileNameMap
    public String getContentTypeFor(String filename) {
        if (filename.endsWith(Separators.SLASH)) {
            return MimeUtils.guessMimeTypeFromExtension("html");
        }
        int lastCharInExtension = filename.lastIndexOf(35);
        if (lastCharInExtension < 0) {
            lastCharInExtension = filename.length();
        }
        int firstCharInExtension = filename.lastIndexOf(46) + 1;
        String ext = "";
        if (firstCharInExtension > filename.lastIndexOf(47)) {
            ext = filename.substring(firstCharInExtension, lastCharInExtension);
        }
        return MimeUtils.guessMimeTypeFromExtension(ext.toLowerCase(Locale.US));
    }
}