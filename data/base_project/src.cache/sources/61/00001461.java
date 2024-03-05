package android.util;

import android.os.Build;
import android.text.TextUtils;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.HashMap;
import java.util.Map;

/* loaded from: CharsetUtils.class */
public final class CharsetUtils {
    private static final String VENDOR_DOCOMO = "docomo";
    private static final String VENDOR_KDDI = "kddi";
    private static final String VENDOR_SOFTBANK = "softbank";
    private static final Map<String, String> sVendorShiftJisMap = new HashMap();

    static {
        sVendorShiftJisMap.put(VENDOR_DOCOMO, "docomo-shift_jis-2007");
        sVendorShiftJisMap.put(VENDOR_KDDI, "kddi-shift_jis-2007");
        sVendorShiftJisMap.put(VENDOR_SOFTBANK, "softbank-shift_jis-2007");
    }

    private CharsetUtils() {
    }

    public static String nameForVendor(String charsetName, String vendor) {
        String vendorShiftJis;
        if (!TextUtils.isEmpty(charsetName) && !TextUtils.isEmpty(vendor) && isShiftJis(charsetName) && (vendorShiftJis = sVendorShiftJisMap.get(vendor)) != null) {
            return vendorShiftJis;
        }
        return charsetName;
    }

    public static String nameForDefaultVendor(String charsetName) {
        return nameForVendor(charsetName, getDefaultVendor());
    }

    public static Charset charsetForVendor(String charsetName, String vendor) throws UnsupportedCharsetException, IllegalCharsetNameException {
        return Charset.forName(nameForVendor(charsetName, vendor));
    }

    public static Charset charsetForVendor(String charsetName) throws UnsupportedCharsetException, IllegalCharsetNameException {
        return charsetForVendor(charsetName, getDefaultVendor());
    }

    private static boolean isShiftJis(String charsetName) {
        if (charsetName == null) {
            return false;
        }
        int length = charsetName.length();
        if (length == 4 || length == 9) {
            return charsetName.equalsIgnoreCase("shift_jis") || charsetName.equalsIgnoreCase("shift-jis") || charsetName.equalsIgnoreCase("sjis");
        }
        return false;
    }

    private static String getDefaultVendor() {
        return Build.BRAND;
    }
}