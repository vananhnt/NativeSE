package com.android.i18n.phonenumbers;

import com.android.i18n.phonenumbers.Phonemetadata;
import java.util.regex.Pattern;

/* loaded from: ShortNumberUtil.class */
public class ShortNumberUtil {
    private final PhoneNumberUtil phoneUtil;

    public ShortNumberUtil() {
        this.phoneUtil = PhoneNumberUtil.getInstance();
    }

    ShortNumberUtil(PhoneNumberUtil util) {
        this.phoneUtil = util;
    }

    public boolean connectsToEmergencyNumber(String number, String regionCode) {
        return matchesEmergencyNumberHelper(number, regionCode, true);
    }

    public boolean isEmergencyNumber(String number, String regionCode) {
        return matchesEmergencyNumberHelper(number, regionCode, false);
    }

    private boolean matchesEmergencyNumberHelper(String number, String regionCode, boolean allowPrefixMatch) {
        Phonemetadata.PhoneMetadata metadata;
        String number2 = PhoneNumberUtil.extractPossibleNumber(number);
        if (PhoneNumberUtil.PLUS_CHARS_PATTERN.matcher(number2).lookingAt() || (metadata = this.phoneUtil.getMetadataForRegion(regionCode)) == null || !metadata.hasEmergency()) {
            return false;
        }
        Pattern emergencyNumberPattern = Pattern.compile(metadata.getEmergency().getNationalNumberPattern());
        String normalizedNumber = PhoneNumberUtil.normalizeDigitsOnly(number2);
        return (!allowPrefixMatch || regionCode.equals("BR")) ? emergencyNumberPattern.matcher(normalizedNumber).matches() : emergencyNumberPattern.matcher(normalizedNumber).lookingAt();
    }
}