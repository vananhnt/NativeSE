package android.telephony;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.CountryDetector;
import android.net.Uri;
import android.os.SystemProperties;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.SparseIntArray;
import com.android.i18n.phonenumbers.NumberParseException;
import com.android.i18n.phonenumbers.PhoneNumberUtil;
import com.android.i18n.phonenumbers.Phonenumber;
import com.android.i18n.phonenumbers.ShortNumberUtil;
import com.android.internal.R;
import com.android.internal.telephony.CommandsInterface;
import com.android.internal.telephony.TelephonyProperties;
import gov.nist.core.Separators;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: PhoneNumberUtils.class */
public class PhoneNumberUtils {
    public static final char PAUSE = ',';
    public static final char WAIT = ';';
    public static final char WILD = 'N';
    private static final String CLIR_ON = "*31#";
    private static final String CLIR_OFF = "#31#";
    public static final int TOA_International = 145;
    public static final int TOA_Unknown = 129;
    static final String LOG_TAG = "PhoneNumberUtils";
    private static final boolean DBG = false;
    public static final int FORMAT_UNKNOWN = 0;
    public static final int FORMAT_NANP = 1;
    public static final int FORMAT_JAPAN = 2;
    private static final int NANP_STATE_DIGIT = 1;
    private static final int NANP_STATE_PLUS = 2;
    private static final int NANP_STATE_ONE = 3;
    private static final int NANP_STATE_DASH = 4;
    static final int MIN_MATCH = 7;
    private static final char PLUS_SIGN_CHAR = '+';
    private static final String PLUS_SIGN_STRING = "+";
    private static final String NANP_IDP_STRING = "011";
    private static final int NANP_LENGTH = 10;
    private static final boolean[] COUNTRY_CALLING_CALL;
    private static final int CCC_LENGTH;
    private static final Pattern GLOBAL_PHONE_NUMBER_PATTERN = Pattern.compile("[\\+]?[0-9.-]+");
    private static final String[] NANP_COUNTRIES = {"US", "CA", "AS", CommandsInterface.CB_FACILITY_BAIC, CommandsInterface.CB_FACILITY_BA_MO, "BS", "BB", "BM", "VG", "KY", "DM", "DO", "GD", "GU", "JM", "PR", "MS", "MP", "KN", "LC", "VC", "TT", "TC", "VI"};
    private static final SparseIntArray KEYPAD_MAP = new SparseIntArray();

    static {
        KEYPAD_MAP.put(97, 50);
        KEYPAD_MAP.put(98, 50);
        KEYPAD_MAP.put(99, 50);
        KEYPAD_MAP.put(65, 50);
        KEYPAD_MAP.put(66, 50);
        KEYPAD_MAP.put(67, 50);
        KEYPAD_MAP.put(100, 51);
        KEYPAD_MAP.put(101, 51);
        KEYPAD_MAP.put(102, 51);
        KEYPAD_MAP.put(68, 51);
        KEYPAD_MAP.put(69, 51);
        KEYPAD_MAP.put(70, 51);
        KEYPAD_MAP.put(103, 52);
        KEYPAD_MAP.put(104, 52);
        KEYPAD_MAP.put(105, 52);
        KEYPAD_MAP.put(71, 52);
        KEYPAD_MAP.put(72, 52);
        KEYPAD_MAP.put(73, 52);
        KEYPAD_MAP.put(106, 53);
        KEYPAD_MAP.put(107, 53);
        KEYPAD_MAP.put(108, 53);
        KEYPAD_MAP.put(74, 53);
        KEYPAD_MAP.put(75, 53);
        KEYPAD_MAP.put(76, 53);
        KEYPAD_MAP.put(109, 54);
        KEYPAD_MAP.put(110, 54);
        KEYPAD_MAP.put(111, 54);
        KEYPAD_MAP.put(77, 54);
        KEYPAD_MAP.put(78, 54);
        KEYPAD_MAP.put(79, 54);
        KEYPAD_MAP.put(112, 55);
        KEYPAD_MAP.put(113, 55);
        KEYPAD_MAP.put(114, 55);
        KEYPAD_MAP.put(115, 55);
        KEYPAD_MAP.put(80, 55);
        KEYPAD_MAP.put(81, 55);
        KEYPAD_MAP.put(82, 55);
        KEYPAD_MAP.put(83, 55);
        KEYPAD_MAP.put(116, 56);
        KEYPAD_MAP.put(117, 56);
        KEYPAD_MAP.put(118, 56);
        KEYPAD_MAP.put(84, 56);
        KEYPAD_MAP.put(85, 56);
        KEYPAD_MAP.put(86, 56);
        KEYPAD_MAP.put(119, 57);
        KEYPAD_MAP.put(120, 57);
        KEYPAD_MAP.put(121, 57);
        KEYPAD_MAP.put(122, 57);
        KEYPAD_MAP.put(87, 57);
        KEYPAD_MAP.put(88, 57);
        KEYPAD_MAP.put(89, 57);
        KEYPAD_MAP.put(90, 57);
        COUNTRY_CALLING_CALL = new boolean[]{true, true, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, true, true, false, true, true, true, true, true, false, true, false, false, true, true, false, false, true, true, true, true, true, true, true, false, true, true, true, true, true, true, true, true, false, true, true, true, true, true, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, true, true, false, true, false, false, true, true, true, true, true, true, true, false, false, true, false};
        CCC_LENGTH = COUNTRY_CALLING_CALL.length;
    }

    public static boolean isISODigit(char c) {
        return c >= '0' && c <= '9';
    }

    public static final boolean is12Key(char c) {
        return (c >= '0' && c <= '9') || c == '*' || c == '#';
    }

    public static final boolean isDialable(char c) {
        return (c >= '0' && c <= '9') || c == '*' || c == '#' || c == '+' || c == 'N';
    }

    public static final boolean isReallyDialable(char c) {
        return (c >= '0' && c <= '9') || c == '*' || c == '#' || c == '+';
    }

    public static final boolean isNonSeparator(char c) {
        return (c >= '0' && c <= '9') || c == '*' || c == '#' || c == '+' || c == 'N' || c == ';' || c == ',';
    }

    public static final boolean isStartsPostDial(char c) {
        return c == ',' || c == ';';
    }

    private static boolean isPause(char c) {
        return c == 'p' || c == 'P';
    }

    private static boolean isToneWait(char c) {
        return c == 'w' || c == 'W';
    }

    private static boolean isSeparator(char ch) {
        return !isDialable(ch) && ('a' > ch || ch > 'z') && ('A' > ch || ch > 'Z');
    }

    public static String getNumberFromIntent(Intent intent, Context context) {
        String number = null;
        Uri uri = intent.getData();
        if (uri == null) {
            return null;
        }
        String scheme = uri.getScheme();
        if (scheme.equals("tel") || scheme.equals("sip")) {
            return uri.getSchemeSpecificPart();
        }
        if (scheme.equals("voicemail")) {
            return TelephonyManager.getDefault().getCompleteVoiceMailNumber();
        }
        if (context == null) {
            return null;
        }
        intent.resolveType(context);
        String phoneColumn = null;
        String authority = uri.getAuthority();
        if (Contacts.AUTHORITY.equals(authority)) {
            phoneColumn = "number";
        } else if (ContactsContract.AUTHORITY.equals(authority)) {
            phoneColumn = "data1";
        }
        Cursor c = context.getContentResolver().query(uri, new String[]{phoneColumn}, null, null, null);
        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    number = c.getString(c.getColumnIndex(phoneColumn));
                }
            } finally {
                c.close();
            }
        }
        return number;
    }

    public static String extractNetworkPortion(String phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }
        int len = phoneNumber.length();
        StringBuilder ret = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = phoneNumber.charAt(i);
            int digit = Character.digit(c, 10);
            if (digit != -1) {
                ret.append(digit);
            } else if (c == '+') {
                String prefix = ret.toString();
                if (prefix.length() == 0 || prefix.equals(CLIR_ON) || prefix.equals(CLIR_OFF)) {
                    ret.append(c);
                }
            } else if (isDialable(c)) {
                ret.append(c);
            } else if (isStartsPostDial(c)) {
                break;
            }
        }
        return ret.toString();
    }

    public static String extractNetworkPortionAlt(String phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }
        int len = phoneNumber.length();
        StringBuilder ret = new StringBuilder(len);
        boolean haveSeenPlus = false;
        for (int i = 0; i < len; i++) {
            char c = phoneNumber.charAt(i);
            if (c == '+') {
                if (haveSeenPlus) {
                    continue;
                } else {
                    haveSeenPlus = true;
                }
            }
            if (isDialable(c)) {
                ret.append(c);
            } else if (isStartsPostDial(c)) {
                break;
            }
        }
        return ret.toString();
    }

    public static String stripSeparators(String phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }
        int len = phoneNumber.length();
        StringBuilder ret = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = phoneNumber.charAt(i);
            int digit = Character.digit(c, 10);
            if (digit != -1) {
                ret.append(digit);
            } else if (isNonSeparator(c)) {
                ret.append(c);
            }
        }
        return ret.toString();
    }

    public static String convertAndStrip(String phoneNumber) {
        return stripSeparators(convertKeypadLettersToDigits(phoneNumber));
    }

    public static String convertPreDial(String phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }
        int len = phoneNumber.length();
        StringBuilder ret = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = phoneNumber.charAt(i);
            if (isPause(c)) {
                c = ',';
            } else if (isToneWait(c)) {
                c = ';';
            }
            ret.append(c);
        }
        return ret.toString();
    }

    private static int minPositive(int a, int b) {
        if (a >= 0 && b >= 0) {
            return a < b ? a : b;
        } else if (a >= 0) {
            return a;
        } else {
            if (b >= 0) {
                return b;
            }
            return -1;
        }
    }

    private static void log(String msg) {
        Rlog.d(LOG_TAG, msg);
    }

    private static int indexOfLastNetworkChar(String a) {
        int origLength = a.length();
        int pIndex = a.indexOf(44);
        int wIndex = a.indexOf(59);
        int trimIndex = minPositive(pIndex, wIndex);
        if (trimIndex < 0) {
            return origLength - 1;
        }
        return trimIndex - 1;
    }

    public static String extractPostDialPortion(String phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }
        StringBuilder ret = new StringBuilder();
        int trimIndex = indexOfLastNetworkChar(phoneNumber);
        int s = phoneNumber.length();
        for (int i = trimIndex + 1; i < s; i++) {
            char c = phoneNumber.charAt(i);
            if (isNonSeparator(c)) {
                ret.append(c);
            }
        }
        return ret.toString();
    }

    public static boolean compare(String a, String b) {
        return compare(a, b, false);
    }

    public static boolean compare(Context context, String a, String b) {
        boolean useStrict = context.getResources().getBoolean(R.bool.config_use_strict_phone_number_comparation);
        return compare(a, b, useStrict);
    }

    public static boolean compare(String a, String b, boolean useStrictComparation) {
        return useStrictComparation ? compareStrictly(a, b) : compareLoosely(a, b);
    }

    public static boolean compareLoosely(String a, String b) {
        int numNonDialableCharsInA = 0;
        int numNonDialableCharsInB = 0;
        if (a == null || b == null) {
            return a == b;
        } else if (a.length() == 0 || b.length() == 0) {
            return false;
        } else {
            int ia = indexOfLastNetworkChar(a);
            int ib = indexOfLastNetworkChar(b);
            int matched = 0;
            while (ia >= 0 && ib >= 0) {
                boolean skipCmp = false;
                char ca = a.charAt(ia);
                if (!isDialable(ca)) {
                    ia--;
                    skipCmp = true;
                    numNonDialableCharsInA++;
                }
                char cb = b.charAt(ib);
                if (!isDialable(cb)) {
                    ib--;
                    skipCmp = true;
                    numNonDialableCharsInB++;
                }
                if (!skipCmp) {
                    if (cb != ca && ca != 'N' && cb != 'N') {
                        break;
                    }
                    ia--;
                    ib--;
                    matched++;
                }
            }
            if (matched < 7) {
                int effectiveALen = a.length() - numNonDialableCharsInA;
                int effectiveBLen = b.length() - numNonDialableCharsInB;
                if (effectiveALen == effectiveBLen && effectiveALen == matched) {
                    return true;
                }
                return false;
            } else if (matched >= 7 && (ia < 0 || ib < 0)) {
                return true;
            } else {
                if (matchIntlPrefix(a, ia + 1) && matchIntlPrefix(b, ib + 1)) {
                    return true;
                }
                if (matchTrunkPrefix(a, ia + 1) && matchIntlPrefixAndCC(b, ib + 1)) {
                    return true;
                }
                if (matchTrunkPrefix(b, ib + 1) && matchIntlPrefixAndCC(a, ia + 1)) {
                    return true;
                }
                return false;
            }
        }
    }

    public static boolean compareStrictly(String a, String b) {
        return compareStrictly(a, b, true);
    }

    public static boolean compareStrictly(String a, String b, boolean acceptInvalidCCCPrefix) {
        if (a == null || b == null) {
            return a == b;
        } else if (a.length() == 0 && b.length() == 0) {
            return false;
        } else {
            int forwardIndexA = 0;
            int forwardIndexB = 0;
            CountryCallingCodeAndNewIndex cccA = tryGetCountryCallingCodeAndNewIndex(a, acceptInvalidCCCPrefix);
            CountryCallingCodeAndNewIndex cccB = tryGetCountryCallingCodeAndNewIndex(b, acceptInvalidCCCPrefix);
            boolean bothHasCountryCallingCode = false;
            boolean okToIgnorePrefix = true;
            boolean trunkPrefixIsOmittedA = false;
            boolean trunkPrefixIsOmittedB = false;
            if (cccA != null && cccB != null) {
                if (cccA.countryCallingCode != cccB.countryCallingCode) {
                    return false;
                }
                okToIgnorePrefix = false;
                bothHasCountryCallingCode = true;
                forwardIndexA = cccA.newIndex;
                forwardIndexB = cccB.newIndex;
            } else if (cccA == null && cccB == null) {
                okToIgnorePrefix = false;
            } else {
                if (cccA != null) {
                    forwardIndexA = cccA.newIndex;
                } else {
                    int tmp = tryGetTrunkPrefixOmittedIndex(b, 0);
                    if (tmp >= 0) {
                        forwardIndexA = tmp;
                        trunkPrefixIsOmittedA = true;
                    }
                }
                if (cccB != null) {
                    forwardIndexB = cccB.newIndex;
                } else {
                    int tmp2 = tryGetTrunkPrefixOmittedIndex(b, 0);
                    if (tmp2 >= 0) {
                        forwardIndexB = tmp2;
                        trunkPrefixIsOmittedB = true;
                    }
                }
            }
            int backwardIndexA = a.length() - 1;
            int backwardIndexB = b.length() - 1;
            while (backwardIndexA >= forwardIndexA && backwardIndexB >= forwardIndexB) {
                boolean skip_compare = false;
                char chA = a.charAt(backwardIndexA);
                char chB = b.charAt(backwardIndexB);
                if (isSeparator(chA)) {
                    backwardIndexA--;
                    skip_compare = true;
                }
                if (isSeparator(chB)) {
                    backwardIndexB--;
                    skip_compare = true;
                }
                if (!skip_compare) {
                    if (chA != chB) {
                        return false;
                    }
                    backwardIndexA--;
                    backwardIndexB--;
                }
            }
            if (okToIgnorePrefix) {
                if ((trunkPrefixIsOmittedA && forwardIndexA <= backwardIndexA) || !checkPrefixIsIgnorable(a, forwardIndexA, backwardIndexA)) {
                    if (acceptInvalidCCCPrefix) {
                        return compare(a, b, false);
                    }
                    return false;
                } else if ((trunkPrefixIsOmittedB && forwardIndexB <= backwardIndexB) || !checkPrefixIsIgnorable(b, forwardIndexA, backwardIndexB)) {
                    if (acceptInvalidCCCPrefix) {
                        return compare(a, b, false);
                    }
                    return false;
                } else {
                    return true;
                }
            }
            boolean maybeNamp = !bothHasCountryCallingCode;
            while (backwardIndexA >= forwardIndexA) {
                char chA2 = a.charAt(backwardIndexA);
                if (isDialable(chA2)) {
                    if (maybeNamp && tryGetISODigit(chA2) == 1) {
                        maybeNamp = false;
                    } else {
                        return false;
                    }
                }
                backwardIndexA--;
            }
            while (backwardIndexB >= forwardIndexB) {
                char chB2 = b.charAt(backwardIndexB);
                if (isDialable(chB2)) {
                    if (maybeNamp && tryGetISODigit(chB2) == 1) {
                        maybeNamp = false;
                    } else {
                        return false;
                    }
                }
                backwardIndexB--;
            }
            return true;
        }
    }

    public static String toCallerIDMinMatch(String phoneNumber) {
        String np = extractNetworkPortionAlt(phoneNumber);
        return internalGetStrippedReversed(np, 7);
    }

    public static String getStrippedReversed(String phoneNumber) {
        String np = extractNetworkPortionAlt(phoneNumber);
        if (np == null) {
            return null;
        }
        return internalGetStrippedReversed(np, np.length());
    }

    private static String internalGetStrippedReversed(String np, int numDigits) {
        if (np == null) {
            return null;
        }
        StringBuilder ret = new StringBuilder(numDigits);
        int length = np.length();
        for (int i = length - 1; i >= 0 && length - i <= numDigits; i--) {
            char c = np.charAt(i);
            ret.append(c);
        }
        return ret.toString();
    }

    public static String stringFromStringAndTOA(String s, int TOA) {
        if (s == null) {
            return null;
        }
        if (TOA == 145 && s.length() > 0 && s.charAt(0) != '+') {
            return PLUS_SIGN_STRING + s;
        }
        return s;
    }

    public static int toaFromString(String s) {
        if (s != null && s.length() > 0 && s.charAt(0) == '+') {
            return 145;
        }
        return 129;
    }

    public static String calledPartyBCDToString(byte[] bytes, int offset, int length) {
        boolean prependPlus = false;
        StringBuilder ret = new StringBuilder(1 + (length * 2));
        if (length < 2) {
            return "";
        }
        if ((bytes[offset] & 240) == 144) {
            prependPlus = true;
        }
        internalCalledPartyBCDFragmentToString(ret, bytes, offset + 1, length - 1);
        if (prependPlus && ret.length() == 0) {
            return "";
        }
        if (prependPlus) {
            String retString = ret.toString();
            Pattern p = Pattern.compile("(^[#*])(.*)([#*])(.*)(#)$");
            Matcher m = p.matcher(retString);
            if (m.matches()) {
                if ("".equals(m.group(2))) {
                    ret = new StringBuilder();
                    ret.append(m.group(1));
                    ret.append(m.group(3));
                    ret.append(m.group(4));
                    ret.append(m.group(5));
                    ret.append(PLUS_SIGN_STRING);
                } else {
                    ret = new StringBuilder();
                    ret.append(m.group(1));
                    ret.append(m.group(2));
                    ret.append(m.group(3));
                    ret.append(PLUS_SIGN_STRING);
                    ret.append(m.group(4));
                    ret.append(m.group(5));
                }
            } else {
                Pattern p2 = Pattern.compile("(^[#*])(.*)([#*])(.*)");
                Matcher m2 = p2.matcher(retString);
                if (m2.matches()) {
                    ret = new StringBuilder();
                    ret.append(m2.group(1));
                    ret.append(m2.group(2));
                    ret.append(m2.group(3));
                    ret.append(PLUS_SIGN_STRING);
                    ret.append(m2.group(4));
                } else {
                    ret = new StringBuilder();
                    ret.append('+');
                    ret.append(retString);
                }
            }
        }
        return ret.toString();
    }

    private static void internalCalledPartyBCDFragmentToString(StringBuilder sb, byte[] bytes, int offset, int length) {
        char c;
        char c2;
        for (int i = offset; i < length + offset && (c = bcdToChar((byte) (bytes[i] & 15))) != 0; i++) {
            sb.append(c);
            byte b = (byte) ((bytes[i] >> 4) & 15);
            if ((b == 15 && i + 1 == length + offset) || (c2 = bcdToChar(b)) == 0) {
                return;
            }
            sb.append(c2);
        }
    }

    public static String calledPartyBCDFragmentToString(byte[] bytes, int offset, int length) {
        StringBuilder ret = new StringBuilder(length * 2);
        internalCalledPartyBCDFragmentToString(ret, bytes, offset, length);
        return ret.toString();
    }

    private static char bcdToChar(byte b) {
        if (b < 10) {
            return (char) (48 + b);
        }
        switch (b) {
            case 10:
                return '*';
            case 11:
                return '#';
            case 12:
                return ',';
            case 13:
                return 'N';
            default:
                return (char) 0;
        }
    }

    private static int charToBCD(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        if (c == '*') {
            return 10;
        }
        if (c == '#') {
            return 11;
        }
        if (c == ',') {
            return 12;
        }
        if (c == 'N') {
            return 13;
        }
        throw new RuntimeException("invalid char for BCD " + c);
    }

    public static boolean isWellFormedSmsAddress(String address) {
        String networkPortion = extractNetworkPortion(address);
        return (networkPortion.equals(PLUS_SIGN_STRING) || TextUtils.isEmpty(networkPortion) || !isDialable(networkPortion)) ? false : true;
    }

    public static boolean isGlobalPhoneNumber(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            return false;
        }
        Matcher match = GLOBAL_PHONE_NUMBER_PATTERN.matcher(phoneNumber);
        return match.matches();
    }

    private static boolean isDialable(String address) {
        int count = address.length();
        for (int i = 0; i < count; i++) {
            if (!isDialable(address.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static boolean isNonSeparator(String address) {
        int count = address.length();
        for (int i = 0; i < count; i++) {
            if (!isNonSeparator(address.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static byte[] networkPortionToCalledPartyBCD(String s) {
        String networkPortion = extractNetworkPortion(s);
        return numberToCalledPartyBCDHelper(networkPortion, false);
    }

    public static byte[] networkPortionToCalledPartyBCDWithLength(String s) {
        String networkPortion = extractNetworkPortion(s);
        return numberToCalledPartyBCDHelper(networkPortion, true);
    }

    public static byte[] numberToCalledPartyBCD(String number) {
        return numberToCalledPartyBCDHelper(number, false);
    }

    private static byte[] numberToCalledPartyBCDHelper(String number, boolean includeLength) {
        int numberLenReal = number.length();
        int numberLenEffective = numberLenReal;
        boolean hasPlus = number.indexOf(43) != -1;
        if (hasPlus) {
            numberLenEffective--;
        }
        if (numberLenEffective == 0) {
            return null;
        }
        int resultLen = (numberLenEffective + 1) / 2;
        int extraBytes = includeLength ? 1 + 1 : 1;
        int resultLen2 = resultLen + extraBytes;
        byte[] result = new byte[resultLen2];
        int digitCount = 0;
        for (int i = 0; i < numberLenReal; i++) {
            char c = number.charAt(i);
            if (c != '+') {
                int shift = (digitCount & 1) == 1 ? 4 : 0;
                int i2 = extraBytes + (digitCount >> 1);
                result[i2] = (byte) (result[i2] | ((byte) ((charToBCD(c) & 15) << shift)));
                digitCount++;
            }
        }
        if ((digitCount & 1) == 1) {
            int i3 = extraBytes + (digitCount >> 1);
            result[i3] = (byte) (result[i3] | 240);
        }
        int offset = 0;
        if (includeLength) {
            offset = 0 + 1;
            result[0] = (byte) (resultLen2 - 1);
        }
        result[offset] = (byte) (hasPlus ? 145 : 129);
        return result;
    }

    public static String formatNumber(String source) {
        SpannableStringBuilder text = new SpannableStringBuilder(source);
        formatNumber(text, getFormatTypeForLocale(Locale.getDefault()));
        return text.toString();
    }

    public static String formatNumber(String source, int defaultFormattingType) {
        SpannableStringBuilder text = new SpannableStringBuilder(source);
        formatNumber(text, defaultFormattingType);
        return text.toString();
    }

    public static int getFormatTypeForLocale(Locale locale) {
        String country = locale.getCountry();
        return getFormatTypeFromCountryCode(country);
    }

    public static void formatNumber(Editable text, int defaultFormattingType) {
        int formatType = defaultFormattingType;
        if (text.length() > 2 && text.charAt(0) == '+') {
            formatType = text.charAt(1) == '1' ? 1 : (text.length() >= 3 && text.charAt(1) == '8' && text.charAt(2) == '1') ? 2 : 0;
        }
        switch (formatType) {
            case 0:
                removeDashes(text);
                return;
            case 1:
                formatNanpNumber(text);
                return;
            case 2:
                formatJapaneseNumber(text);
                return;
            default:
                return;
        }
    }

    public static void formatNanpNumber(Editable text) {
        int length = text.length();
        if (length > "+1-nnn-nnn-nnnn".length() || length <= 5) {
            return;
        }
        CharSequence saved = text.subSequence(0, length);
        removeDashes(text);
        int length2 = text.length();
        int[] dashPositions = new int[3];
        int numDashes = 0;
        int state = 1;
        int numDigits = 0;
        for (int i = 0; i < length2; i++) {
            char c = text.charAt(i);
            switch (c) {
                case '+':
                    if (i == 0) {
                        state = 2;
                        continue;
                    } else {
                        text.replace(0, length2, saved);
                        return;
                    }
                case ',':
                case '.':
                case '/':
                default:
                    text.replace(0, length2, saved);
                    return;
                case '-':
                    state = 4;
                    continue;
                case '0':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    break;
                case '1':
                    if (numDigits == 0 || state == 2) {
                        state = 3;
                        continue;
                    }
                    break;
            }
            if (state == 2) {
                text.replace(0, length2, saved);
                return;
            }
            if (state == 3) {
                int i2 = numDashes;
                numDashes++;
                dashPositions[i2] = i;
            } else if (state != 4 && (numDigits == 3 || numDigits == 6)) {
                int i3 = numDashes;
                numDashes++;
                dashPositions[i3] = i;
            }
            state = 1;
            numDigits++;
        }
        if (numDigits == 7) {
            numDashes--;
        }
        for (int i4 = 0; i4 < numDashes; i4++) {
            int pos = dashPositions[i4];
            text.replace(pos + i4, pos + i4, "-");
        }
        for (int len = text.length(); len > 0 && text.charAt(len - 1) == '-'; len--) {
            text.delete(len - 1, len);
        }
    }

    public static void formatJapaneseNumber(Editable text) {
        JapanesePhoneNumberFormatter.format(text);
    }

    private static void removeDashes(Editable text) {
        int p = 0;
        while (p < text.length()) {
            if (text.charAt(p) == '-') {
                text.delete(p, p + 1);
            } else {
                p++;
            }
        }
    }

    public static String formatNumberToE164(String phoneNumber, String defaultCountryIso) {
        PhoneNumberUtil util = PhoneNumberUtil.getInstance();
        String result = null;
        try {
            Phonenumber.PhoneNumber pn = util.parse(phoneNumber, defaultCountryIso);
            if (util.isValidNumber(pn)) {
                result = util.format(pn, PhoneNumberUtil.PhoneNumberFormat.E164);
            }
        } catch (NumberParseException e) {
        }
        return result;
    }

    public static String formatNumber(String phoneNumber, String defaultCountryIso) {
        if (phoneNumber.startsWith(Separators.POUND) || phoneNumber.startsWith("*")) {
            return phoneNumber;
        }
        PhoneNumberUtil util = PhoneNumberUtil.getInstance();
        String result = null;
        try {
            Phonenumber.PhoneNumber pn = util.parseAndKeepRawInput(phoneNumber, defaultCountryIso);
            result = util.formatInOriginalFormat(pn, defaultCountryIso);
        } catch (NumberParseException e) {
        }
        return result;
    }

    public static String formatNumber(String phoneNumber, String phoneNumberE164, String defaultCountryIso) {
        int len = phoneNumber.length();
        for (int i = 0; i < len; i++) {
            if (!isDialable(phoneNumber.charAt(i))) {
                return phoneNumber;
            }
        }
        PhoneNumberUtil util = PhoneNumberUtil.getInstance();
        if (phoneNumberE164 != null && phoneNumberE164.length() >= 2 && phoneNumberE164.charAt(0) == '+') {
            try {
                Phonenumber.PhoneNumber pn = util.parse(phoneNumberE164, "ZZ");
                String regionCode = util.getRegionCodeForNumber(pn);
                if (!TextUtils.isEmpty(regionCode)) {
                    if (normalizeNumber(phoneNumber).indexOf(phoneNumberE164.substring(1)) <= 0) {
                        defaultCountryIso = regionCode;
                    }
                }
            } catch (NumberParseException e) {
            }
        }
        String result = formatNumber(phoneNumber, defaultCountryIso);
        return result != null ? result : phoneNumber;
    }

    public static String normalizeNumber(String phoneNumber) {
        StringBuilder sb = new StringBuilder();
        int len = phoneNumber.length();
        for (int i = 0; i < len; i++) {
            char c = phoneNumber.charAt(i);
            int digit = Character.digit(c, 10);
            if (digit != -1) {
                sb.append(digit);
            } else if (i == 0 && c == '+') {
                sb.append(c);
            } else if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                return normalizeNumber(convertKeypadLettersToDigits(phoneNumber));
            }
        }
        return sb.toString();
    }

    public static String replaceUnicodeDigits(String number) {
        StringBuilder normalizedDigits = new StringBuilder(number.length());
        char[] arr$ = number.toCharArray();
        for (char c : arr$) {
            int digit = Character.digit(c, 10);
            if (digit != -1) {
                normalizedDigits.append(digit);
            } else {
                normalizedDigits.append(c);
            }
        }
        return normalizedDigits.toString();
    }

    public static boolean isEmergencyNumber(String number) {
        return isEmergencyNumberInternal(number, true);
    }

    public static boolean isPotentialEmergencyNumber(String number) {
        return isEmergencyNumberInternal(number, false);
    }

    private static boolean isEmergencyNumberInternal(String number, boolean useExactMatch) {
        return isEmergencyNumberInternal(number, null, useExactMatch);
    }

    public static boolean isEmergencyNumber(String number, String defaultCountryIso) {
        return isEmergencyNumberInternal(number, defaultCountryIso, true);
    }

    public static boolean isPotentialEmergencyNumber(String number, String defaultCountryIso) {
        return isEmergencyNumberInternal(number, defaultCountryIso, false);
    }

    private static boolean isEmergencyNumberInternal(String number, String defaultCountryIso, boolean useExactMatch) {
        if (number == null || isUriNumber(number)) {
            return false;
        }
        String number2 = extractNetworkPortionAlt(number);
        String numbers = SystemProperties.get("ril.ecclist");
        if (TextUtils.isEmpty(numbers)) {
            numbers = SystemProperties.get("ro.ril.ecclist");
        }
        if (!TextUtils.isEmpty(numbers)) {
            String[] arr$ = numbers.split(Separators.COMMA);
            for (String emergencyNum : arr$) {
                if (useExactMatch || "BR".equalsIgnoreCase(defaultCountryIso)) {
                    if (number2.equals(emergencyNum)) {
                        return true;
                    }
                } else if (number2.startsWith(emergencyNum)) {
                    return true;
                }
            }
            return false;
        }
        Rlog.d(LOG_TAG, "System property doesn't provide any emergency numbers. Use embedded logic for determining ones.");
        if (defaultCountryIso == null) {
            return useExactMatch ? number2.equals("112") || number2.equals("911") : number2.startsWith("112") || number2.startsWith("911");
        }
        ShortNumberUtil util = new ShortNumberUtil();
        if (useExactMatch) {
            return util.isEmergencyNumber(number2, defaultCountryIso);
        }
        return util.connectsToEmergencyNumber(number2, defaultCountryIso);
    }

    public static boolean isLocalEmergencyNumber(String number, Context context) {
        return isLocalEmergencyNumberInternal(number, context, true);
    }

    public static boolean isPotentialLocalEmergencyNumber(String number, Context context) {
        return isLocalEmergencyNumberInternal(number, context, false);
    }

    private static boolean isLocalEmergencyNumberInternal(String number, Context context, boolean useExactMatch) {
        String countryIso;
        CountryDetector detector = (CountryDetector) context.getSystemService(Context.COUNTRY_DETECTOR);
        if (detector != null && detector.detectCountry() != null) {
            countryIso = detector.detectCountry().getCountryIso();
        } else {
            Locale locale = context.getResources().getConfiguration().locale;
            countryIso = locale.getCountry();
            Rlog.w(LOG_TAG, "No CountryDetector; falling back to countryIso based on locale: " + countryIso);
        }
        return isEmergencyNumberInternal(number, countryIso, useExactMatch);
    }

    public static boolean isVoiceMailNumber(String number) {
        try {
            String vmNumber = TelephonyManager.getDefault().getVoiceMailNumber();
            String number2 = extractNetworkPortionAlt(number);
            return !TextUtils.isEmpty(number2) && compare(number2, vmNumber);
        } catch (SecurityException e) {
            return false;
        }
    }

    public static String convertKeypadLettersToDigits(String input) {
        if (input == null) {
            return input;
        }
        int len = input.length();
        if (len == 0) {
            return input;
        }
        char[] out = input.toCharArray();
        for (int i = 0; i < len; i++) {
            char c = out[i];
            out[i] = (char) KEYPAD_MAP.get(c, c);
        }
        return new String(out);
    }

    public static String cdmaCheckAndProcessPlusCode(String dialStr) {
        if (!TextUtils.isEmpty(dialStr) && isReallyDialable(dialStr.charAt(0)) && isNonSeparator(dialStr)) {
            String currIso = SystemProperties.get(TelephonyProperties.PROPERTY_OPERATOR_ISO_COUNTRY, "");
            String defaultIso = SystemProperties.get(TelephonyProperties.PROPERTY_ICC_OPERATOR_ISO_COUNTRY, "");
            if (!TextUtils.isEmpty(currIso) && !TextUtils.isEmpty(defaultIso)) {
                return cdmaCheckAndProcessPlusCodeByNumberFormat(dialStr, getFormatTypeFromCountryCode(currIso), getFormatTypeFromCountryCode(defaultIso));
            }
        }
        return dialStr;
    }

    public static String cdmaCheckAndProcessPlusCodeByNumberFormat(String dialStr, int currFormat, int defaultFormat) {
        String retStr = dialStr;
        if (dialStr != null && dialStr.lastIndexOf(PLUS_SIGN_STRING) != -1) {
            if (currFormat == defaultFormat && currFormat == 1) {
                String tempDialStr = dialStr;
                retStr = null;
                do {
                    String networkDialStr = processPlusCodeWithinNanp(extractNetworkPortion(tempDialStr));
                    if (!TextUtils.isEmpty(networkDialStr)) {
                        if (retStr == null) {
                            retStr = networkDialStr;
                        } else {
                            retStr = retStr.concat(networkDialStr);
                        }
                        String postDialStr = extractPostDialPortion(tempDialStr);
                        if (!TextUtils.isEmpty(postDialStr)) {
                            int dialableIndex = findDialableIndexFromPostDialStr(postDialStr);
                            if (dialableIndex >= 1) {
                                retStr = appendPwCharBackToOrigDialStr(dialableIndex, retStr, postDialStr);
                                tempDialStr = postDialStr.substring(dialableIndex);
                            } else {
                                if (dialableIndex < 0) {
                                    postDialStr = "";
                                }
                                Rlog.e("wrong postDialStr=", postDialStr);
                            }
                        }
                        if (TextUtils.isEmpty(postDialStr)) {
                            break;
                        }
                    } else {
                        Rlog.e("checkAndProcessPlusCode: null newDialStr", networkDialStr);
                        return dialStr;
                    }
                } while (!TextUtils.isEmpty(tempDialStr));
            } else {
                Rlog.e("checkAndProcessPlusCode:non-NANP not supported", dialStr);
            }
        }
        return retStr;
    }

    private static String getDefaultIdp() {
        String ps = null;
        SystemProperties.get(TelephonyProperties.PROPERTY_IDP_STRING, null);
        if (TextUtils.isEmpty(null)) {
            ps = NANP_IDP_STRING;
        }
        return ps;
    }

    private static boolean isTwoToNine(char c) {
        if (c >= '2' && c <= '9') {
            return true;
        }
        return false;
    }

    private static int getFormatTypeFromCountryCode(String country) {
        int length = NANP_COUNTRIES.length;
        for (int i = 0; i < length; i++) {
            if (NANP_COUNTRIES[i].compareToIgnoreCase(country) == 0) {
                return 1;
            }
        }
        if ("jp".compareToIgnoreCase(country) == 0) {
            return 2;
        }
        return 0;
    }

    private static boolean isNanp(String dialStr) {
        boolean retVal = false;
        if (dialStr != null) {
            if (dialStr.length() == 10 && isTwoToNine(dialStr.charAt(0)) && isTwoToNine(dialStr.charAt(3))) {
                retVal = true;
                int i = 1;
                while (true) {
                    if (i >= 10) {
                        break;
                    }
                    char c = dialStr.charAt(i);
                    if (isISODigit(c)) {
                        i++;
                    } else {
                        retVal = false;
                        break;
                    }
                }
            }
        } else {
            Rlog.e("isNanp: null dialStr passed in", dialStr);
        }
        return retVal;
    }

    private static boolean isOneNanp(String dialStr) {
        boolean retVal = false;
        if (dialStr != null) {
            String newDialStr = dialStr.substring(1);
            if (dialStr.charAt(0) == '1' && isNanp(newDialStr)) {
                retVal = true;
            }
        } else {
            Rlog.e("isOneNanp: null dialStr passed in", dialStr);
        }
        return retVal;
    }

    public static boolean isUriNumber(String number) {
        return number != null && (number.contains(Separators.AT) || number.contains("%40"));
    }

    public static String getUsernameFromUriNumber(String number) {
        int delimiterIndex = number.indexOf(64);
        if (delimiterIndex < 0) {
            delimiterIndex = number.indexOf("%40");
        }
        if (delimiterIndex < 0) {
            Rlog.w(LOG_TAG, "getUsernameFromUriNumber: no delimiter found in SIP addr '" + number + Separators.QUOTE);
            delimiterIndex = number.length();
        }
        return number.substring(0, delimiterIndex);
    }

    private static String processPlusCodeWithinNanp(String networkDialStr) {
        String retStr = networkDialStr;
        if (networkDialStr != null && networkDialStr.charAt(0) == '+' && networkDialStr.length() > 1) {
            String newStr = networkDialStr.substring(1);
            if (isOneNanp(newStr)) {
                retStr = newStr;
            } else {
                String idpStr = getDefaultIdp();
                retStr = networkDialStr.replaceFirst("[+]", idpStr);
            }
        }
        return retStr;
    }

    private static int findDialableIndexFromPostDialStr(String postDialStr) {
        for (int index = 0; index < postDialStr.length(); index++) {
            char c = postDialStr.charAt(index);
            if (isReallyDialable(c)) {
                return index;
            }
        }
        return -1;
    }

    private static String appendPwCharBackToOrigDialStr(int dialableIndex, String origStr, String dialStr) {
        String retStr;
        if (dialableIndex == 1) {
            StringBuilder ret = new StringBuilder(origStr);
            retStr = ret.append(dialStr.charAt(0)).toString();
        } else {
            String nonDigitStr = dialStr.substring(0, dialableIndex);
            retStr = origStr.concat(nonDigitStr);
        }
        return retStr;
    }

    private static boolean matchIntlPrefix(String a, int len) {
        int state = 0;
        for (int i = 0; i < len; i++) {
            char c = a.charAt(i);
            switch (state) {
                case 0:
                    if (c == '+') {
                        state = 1;
                        break;
                    } else if (c == '0') {
                        state = 2;
                        break;
                    } else if (!isNonSeparator(c)) {
                        break;
                    } else {
                        return false;
                    }
                case 1:
                case 3:
                default:
                    if (!isNonSeparator(c)) {
                        break;
                    } else {
                        return false;
                    }
                case 2:
                    if (c == '0') {
                        state = 3;
                        break;
                    } else if (c == '1') {
                        state = 4;
                        break;
                    } else if (!isNonSeparator(c)) {
                        break;
                    } else {
                        return false;
                    }
                case 4:
                    if (c == '1') {
                        state = 5;
                        break;
                    } else if (!isNonSeparator(c)) {
                        break;
                    } else {
                        return false;
                    }
            }
        }
        return state == 1 || state == 3 || state == 5;
    }

    private static boolean matchIntlPrefixAndCC(String a, int len) {
        int state = 0;
        for (int i = 0; i < len; i++) {
            char c = a.charAt(i);
            switch (state) {
                case 0:
                    if (c == '+') {
                        state = 1;
                        break;
                    } else if (c == '0') {
                        state = 2;
                        break;
                    } else if (!isNonSeparator(c)) {
                        break;
                    } else {
                        return false;
                    }
                case 1:
                case 3:
                case 5:
                    if (isISODigit(c)) {
                        state = 6;
                        break;
                    } else if (!isNonSeparator(c)) {
                        break;
                    } else {
                        return false;
                    }
                case 2:
                    if (c == '0') {
                        state = 3;
                        break;
                    } else if (c == '1') {
                        state = 4;
                        break;
                    } else if (!isNonSeparator(c)) {
                        break;
                    } else {
                        return false;
                    }
                case 4:
                    if (c == '1') {
                        state = 5;
                        break;
                    } else if (!isNonSeparator(c)) {
                        break;
                    } else {
                        return false;
                    }
                case 6:
                case 7:
                    if (isISODigit(c)) {
                        state++;
                        break;
                    } else if (!isNonSeparator(c)) {
                        break;
                    } else {
                        return false;
                    }
                default:
                    if (!isNonSeparator(c)) {
                        break;
                    } else {
                        return false;
                    }
            }
        }
        return state == 6 || state == 7 || state == 8;
    }

    private static boolean matchTrunkPrefix(String a, int len) {
        boolean found = false;
        for (int i = 0; i < len; i++) {
            char c = a.charAt(i);
            if (c == '0' && !found) {
                found = true;
            } else if (isNonSeparator(c)) {
                return false;
            }
        }
        return found;
    }

    private static boolean isCountryCallingCode(int countryCallingCodeCandidate) {
        return countryCallingCodeCandidate > 0 && countryCallingCodeCandidate < CCC_LENGTH && COUNTRY_CALLING_CALL[countryCallingCodeCandidate];
    }

    private static int tryGetISODigit(char ch) {
        if ('0' <= ch && ch <= '9') {
            return ch - '0';
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: PhoneNumberUtils$CountryCallingCodeAndNewIndex.class */
    public static class CountryCallingCodeAndNewIndex {
        public final int countryCallingCode;
        public final int newIndex;

        public CountryCallingCodeAndNewIndex(int countryCode, int newIndex) {
            this.countryCallingCode = countryCode;
            this.newIndex = newIndex;
        }
    }

    private static CountryCallingCodeAndNewIndex tryGetCountryCallingCodeAndNewIndex(String str, boolean acceptThailandCase) {
        int state = 0;
        int ccc = 0;
        int length = str.length();
        for (int i = 0; i < length; i++) {
            char ch = str.charAt(i);
            switch (state) {
                case 0:
                    if (ch == '+') {
                        state = 1;
                        break;
                    } else if (ch == '0') {
                        state = 2;
                        break;
                    } else if (ch == '1') {
                        if (acceptThailandCase) {
                            state = 8;
                            break;
                        } else {
                            return null;
                        }
                    } else if (!isDialable(ch)) {
                        break;
                    } else {
                        return null;
                    }
                case 1:
                case 3:
                case 5:
                case 6:
                case 7:
                    int ret = tryGetISODigit(ch);
                    if (ret > 0) {
                        ccc = (ccc * 10) + ret;
                        if (ccc >= 100 || isCountryCallingCode(ccc)) {
                            return new CountryCallingCodeAndNewIndex(ccc, i + 1);
                        }
                        if (state == 1 || state == 3 || state == 5) {
                            state = 6;
                            break;
                        } else {
                            state++;
                            break;
                        }
                    } else if (!isDialable(ch)) {
                        break;
                    } else {
                        return null;
                    }
                case 2:
                    if (ch == '0') {
                        state = 3;
                        break;
                    } else if (ch == '1') {
                        state = 4;
                        break;
                    } else if (!isDialable(ch)) {
                        break;
                    } else {
                        return null;
                    }
                case 4:
                    if (ch == '1') {
                        state = 5;
                        break;
                    } else if (!isDialable(ch)) {
                        break;
                    } else {
                        return null;
                    }
                case 8:
                    if (ch == '6') {
                        state = 9;
                        break;
                    } else if (!isDialable(ch)) {
                        break;
                    } else {
                        return null;
                    }
                case 9:
                    if (ch == '6') {
                        return new CountryCallingCodeAndNewIndex(66, i + 1);
                    }
                    return null;
                default:
                    return null;
            }
        }
        return null;
    }

    private static int tryGetTrunkPrefixOmittedIndex(String str, int currentIndex) {
        int length = str.length();
        for (int i = currentIndex; i < length; i++) {
            char ch = str.charAt(i);
            if (tryGetISODigit(ch) >= 0) {
                return i + 1;
            }
            if (isDialable(ch)) {
                return -1;
            }
        }
        return -1;
    }

    private static boolean checkPrefixIsIgnorable(String str, int forwardIndex, int backwardIndex) {
        boolean trunk_prefix_was_read = false;
        while (backwardIndex >= forwardIndex) {
            if (tryGetISODigit(str.charAt(backwardIndex)) >= 0) {
                if (trunk_prefix_was_read) {
                    return false;
                }
                trunk_prefix_was_read = true;
            } else if (isDialable(str.charAt(backwardIndex))) {
                return false;
            }
            backwardIndex--;
        }
        return true;
    }
}