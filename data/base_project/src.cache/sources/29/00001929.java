package com.android.i18n.phonenumbers;

import com.android.i18n.phonenumbers.Phonemetadata;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: MetadataManager.class */
public class MetadataManager {
    private static final String ALTERNATE_FORMATS_FILE_PREFIX = "/com/android/i18n/phonenumbers/data/PhoneNumberAlternateFormatsProto";
    private static final Logger LOGGER = Logger.getLogger(MetadataManager.class.getName());
    private static final Map<Integer, Phonemetadata.PhoneMetadata> callingCodeToAlternateFormatsMap = Collections.synchronizedMap(new HashMap());
    private static final Set<Integer> countryCodeSet = AlternateFormatsCountryCodeSet.getCountryCodeSet();

    private MetadataManager() {
    }

    private static void close(InputStream in) {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, e.toString());
            }
        }
    }

    private static void loadMetadataFromFile(int countryCallingCode) {
        InputStream source = PhoneNumberMatcher.class.getResourceAsStream("/com/android/i18n/phonenumbers/data/PhoneNumberAlternateFormatsProto_" + countryCallingCode);
        ObjectInputStream in = null;
        try {
            try {
                in = new ObjectInputStream(source);
                Phonemetadata.PhoneMetadataCollection alternateFormats = new Phonemetadata.PhoneMetadataCollection();
                alternateFormats.readExternal(in);
                for (Phonemetadata.PhoneMetadata metadata : alternateFormats.getMetadataList()) {
                    callingCodeToAlternateFormatsMap.put(Integer.valueOf(metadata.getCountryCode()), metadata);
                }
                close(in);
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, e.toString());
                close(in);
            }
        } catch (Throwable th) {
            close(in);
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Phonemetadata.PhoneMetadata getAlternateFormatsForCountry(int countryCallingCode) {
        if (!countryCodeSet.contains(Integer.valueOf(countryCallingCode))) {
            return null;
        }
        synchronized (callingCodeToAlternateFormatsMap) {
            if (!callingCodeToAlternateFormatsMap.containsKey(Integer.valueOf(countryCallingCode))) {
                loadMetadataFromFile(countryCallingCode);
            }
        }
        return callingCodeToAlternateFormatsMap.get(Integer.valueOf(countryCallingCode));
    }
}