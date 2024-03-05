package gov.nist.javax.sip.address;

import java.io.UnsupportedEncodingException;

/* loaded from: RFC2396UrlDecoder.class */
public class RFC2396UrlDecoder {
    public static String decode(String uri) {
        StringBuffer translatedUri = new StringBuffer(uri.length());
        byte[] encodedchars = new byte[uri.length() / 3];
        int i = 0;
        int length = uri.length();
        int encodedcharsLength = 0;
        while (i < length) {
            if (uri.charAt(i) == '%') {
                while (i < length && uri.charAt(i) == '%') {
                    if (i + 2 < length) {
                        try {
                            byte x = (byte) Integer.parseInt(uri.substring(i + 1, i + 3), 16);
                            encodedchars[encodedcharsLength] = x;
                            encodedcharsLength++;
                            i += 3;
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("Illegal hex characters in pattern %" + uri.substring(i + 1, i + 3));
                        }
                    } else {
                        throw new IllegalArgumentException("% character should be followed by 2 hexadecimal characters.");
                    }
                }
                try {
                    String translatedPart = new String(encodedchars, 0, encodedcharsLength, "UTF-8");
                    translatedUri.append(translatedPart);
                    encodedcharsLength = 0;
                } catch (UnsupportedEncodingException e2) {
                    throw new RuntimeException("Problem in decodePath: UTF-8 encoding not supported.");
                }
            } else {
                translatedUri.append(uri.charAt(i));
                i++;
            }
        }
        return translatedUri.toString();
    }
}