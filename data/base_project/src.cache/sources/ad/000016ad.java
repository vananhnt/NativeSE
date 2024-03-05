package android.webkit;

import android.content.ClipDescription;
import android.net.ParseException;
import android.net.Uri;
import android.net.WebAddress;
import gov.nist.core.Separators;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: URLUtil.class */
public final class URLUtil {
    private static final String LOGTAG = "webkit";
    static final String ASSET_BASE = "file:///android_asset/";
    static final String RESOURCE_BASE = "file:///android_res/";
    static final String FILE_BASE = "file://";
    static final String PROXY_BASE = "file:///cookieless_proxy/";
    static final String CONTENT_BASE = "content:";
    private static final Pattern CONTENT_DISPOSITION_PATTERN = Pattern.compile("attachment;\\s*filename\\s*=\\s*(\"?)([^\"]*)\\1\\s*$", 2);

    public static String guessUrl(String inUrl) {
        if (inUrl.length() != 0 && !inUrl.startsWith("about:") && !inUrl.startsWith("data:") && !inUrl.startsWith("file:") && !inUrl.startsWith("javascript:")) {
            if (inUrl.endsWith(Separators.DOT)) {
                inUrl = inUrl.substring(0, inUrl.length() - 1);
            }
            try {
                WebAddress webAddress = new WebAddress(inUrl);
                if (webAddress.getHost().indexOf(46) == -1) {
                    webAddress.setHost("www." + webAddress.getHost() + ".com");
                }
                return webAddress.toString();
            } catch (ParseException e) {
                return inUrl;
            }
        }
        return inUrl;
    }

    public static String composeSearchUrl(String inQuery, String template, String queryPlaceHolder) {
        int placeHolderIndex = template.indexOf(queryPlaceHolder);
        if (placeHolderIndex < 0) {
            return null;
        }
        StringBuilder buffer = new StringBuilder();
        buffer.append(template.substring(0, placeHolderIndex));
        try {
            String query = URLEncoder.encode(inQuery, "utf-8");
            buffer.append(query);
            buffer.append(template.substring(placeHolderIndex + queryPlaceHolder.length()));
            return buffer.toString();
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static byte[] decode(byte[] url) throws IllegalArgumentException {
        if (url.length == 0) {
            return new byte[0];
        }
        byte[] tempData = new byte[url.length];
        int tempCount = 0;
        int i = 0;
        while (i < url.length) {
            byte b = url[i];
            if (b == 37) {
                if (url.length - i > 2) {
                    b = (byte) ((parseHex(url[i + 1]) * 16) + parseHex(url[i + 2]));
                    i += 2;
                } else {
                    throw new IllegalArgumentException("Invalid format");
                }
            }
            int i2 = tempCount;
            tempCount++;
            tempData[i2] = b;
            i++;
        }
        byte[] retData = new byte[tempCount];
        System.arraycopy(tempData, 0, retData, 0, tempCount);
        return retData;
    }

    static boolean verifyURLEncoding(String url) {
        int count = url.length();
        if (count == 0) {
            return false;
        }
        int indexOf = url.indexOf(37);
        while (true) {
            int index = indexOf;
            if (index >= 0 && index < count) {
                if (index < count - 2) {
                    try {
                        int index2 = index + 1;
                        parseHex((byte) url.charAt(index2));
                        int index3 = index2 + 1;
                        parseHex((byte) url.charAt(index3));
                        indexOf = url.indexOf(37, index3 + 1);
                    } catch (IllegalArgumentException e) {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return true;
            }
        }
    }

    private static int parseHex(byte b) {
        if (b < 48 || b > 57) {
            if (b < 65 || b > 70) {
                if (b < 97 || b > 102) {
                    throw new IllegalArgumentException("Invalid hex char '" + ((int) b) + Separators.QUOTE);
                }
                return (b - 97) + 10;
            }
            return (b - 65) + 10;
        }
        return b - 48;
    }

    public static boolean isAssetUrl(String url) {
        return null != url && url.startsWith(ASSET_BASE);
    }

    public static boolean isResourceUrl(String url) {
        return null != url && url.startsWith(RESOURCE_BASE);
    }

    @Deprecated
    public static boolean isCookielessProxyUrl(String url) {
        return null != url && url.startsWith(PROXY_BASE);
    }

    public static boolean isFileUrl(String url) {
        return (null == url || !url.startsWith(FILE_BASE) || url.startsWith(ASSET_BASE) || url.startsWith(PROXY_BASE)) ? false : true;
    }

    public static boolean isAboutUrl(String url) {
        return null != url && url.startsWith("about:");
    }

    public static boolean isDataUrl(String url) {
        return null != url && url.startsWith("data:");
    }

    public static boolean isJavaScriptUrl(String url) {
        return null != url && url.startsWith("javascript:");
    }

    public static boolean isHttpUrl(String url) {
        return null != url && url.length() > 6 && url.substring(0, 7).equalsIgnoreCase("http://");
    }

    public static boolean isHttpsUrl(String url) {
        return null != url && url.length() > 7 && url.substring(0, 8).equalsIgnoreCase("https://");
    }

    public static boolean isNetworkUrl(String url) {
        if (url == null || url.length() == 0) {
            return false;
        }
        return isHttpUrl(url) || isHttpsUrl(url);
    }

    public static boolean isContentUrl(String url) {
        return null != url && url.startsWith(CONTENT_BASE);
    }

    public static boolean isValidUrl(String url) {
        if (url == null || url.length() == 0) {
            return false;
        }
        return isAssetUrl(url) || isResourceUrl(url) || isFileUrl(url) || isAboutUrl(url) || isHttpUrl(url) || isHttpsUrl(url) || isJavaScriptUrl(url) || isContentUrl(url);
    }

    public static String stripAnchor(String url) {
        int anchorIndex = url.indexOf(35);
        if (anchorIndex != -1) {
            return url.substring(0, anchorIndex);
        }
        return url;
    }

    public static final String guessFileName(String url, String contentDisposition, String mimeType) {
        int index;
        int index2;
        String filename = null;
        String extension = null;
        if (0 == 0 && contentDisposition != null) {
            filename = parseContentDisposition(contentDisposition);
            if (filename != null && (index2 = filename.lastIndexOf(47) + 1) > 0) {
                filename = filename.substring(index2);
            }
        }
        if (filename == null) {
            String decodedUrl = Uri.decode(url);
            if (decodedUrl != null) {
                int queryIndex = decodedUrl.indexOf(63);
                if (queryIndex > 0) {
                    decodedUrl = decodedUrl.substring(0, queryIndex);
                }
                if (!decodedUrl.endsWith(Separators.SLASH) && (index = decodedUrl.lastIndexOf(47) + 1) > 0) {
                    filename = decodedUrl.substring(index);
                }
            }
        }
        if (filename == null) {
            filename = "downloadfile";
        }
        int dotIndex = filename.indexOf(46);
        if (dotIndex < 0) {
            if (mimeType != null) {
                extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
                if (extension != null) {
                    extension = Separators.DOT + extension;
                }
            }
            if (extension == null) {
                if (mimeType != null && mimeType.toLowerCase(Locale.ROOT).startsWith("text/")) {
                    if (mimeType.equalsIgnoreCase(ClipDescription.MIMETYPE_TEXT_HTML)) {
                        extension = ".html";
                    } else {
                        extension = ".txt";
                    }
                } else {
                    extension = ".bin";
                }
            }
        } else {
            if (mimeType != null) {
                int lastDotIndex = filename.lastIndexOf(46);
                String typeFromExt = MimeTypeMap.getSingleton().getMimeTypeFromExtension(filename.substring(lastDotIndex + 1));
                if (typeFromExt != null && !typeFromExt.equalsIgnoreCase(mimeType)) {
                    extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
                    if (extension != null) {
                        extension = Separators.DOT + extension;
                    }
                }
            }
            if (extension == null) {
                extension = filename.substring(dotIndex);
            }
            filename = filename.substring(0, dotIndex);
        }
        return filename + extension;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String parseContentDisposition(String contentDisposition) {
        try {
            Matcher m = CONTENT_DISPOSITION_PATTERN.matcher(contentDisposition);
            if (m.find()) {
                return m.group(2);
            }
            return null;
        } catch (IllegalStateException e) {
            return null;
        }
    }
}