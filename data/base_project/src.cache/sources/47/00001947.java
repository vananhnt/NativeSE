package com.android.i18n.phonenumbers;

import java.io.Serializable;

/* loaded from: Phonenumber.class */
public final class Phonenumber {
    private Phonenumber() {
    }

    /* loaded from: Phonenumber$PhoneNumber.class */
    public static class PhoneNumber implements Serializable {
        private static final long serialVersionUID = 1;
        private boolean hasCountryCode;
        private boolean hasNationalNumber;
        private boolean hasExtension;
        private boolean hasItalianLeadingZero;
        private boolean hasRawInput;
        private boolean hasCountryCodeSource;
        private boolean hasPreferredDomesticCarrierCode;
        private int countryCode_ = 0;
        private long nationalNumber_ = 0;
        private String extension_ = "";
        private boolean italianLeadingZero_ = false;
        private String rawInput_ = "";
        private String preferredDomesticCarrierCode_ = "";
        private CountryCodeSource countryCodeSource_ = CountryCodeSource.FROM_NUMBER_WITH_PLUS_SIGN;

        /* loaded from: Phonenumber$PhoneNumber$CountryCodeSource.class */
        public enum CountryCodeSource {
            FROM_NUMBER_WITH_PLUS_SIGN,
            FROM_NUMBER_WITH_IDD,
            FROM_NUMBER_WITHOUT_PLUS_SIGN,
            FROM_DEFAULT_COUNTRY
        }

        public boolean hasCountryCode() {
            return this.hasCountryCode;
        }

        public int getCountryCode() {
            return this.countryCode_;
        }

        public PhoneNumber setCountryCode(int value) {
            this.hasCountryCode = true;
            this.countryCode_ = value;
            return this;
        }

        public PhoneNumber clearCountryCode() {
            this.hasCountryCode = false;
            this.countryCode_ = 0;
            return this;
        }

        public boolean hasNationalNumber() {
            return this.hasNationalNumber;
        }

        public long getNationalNumber() {
            return this.nationalNumber_;
        }

        public PhoneNumber setNationalNumber(long value) {
            this.hasNationalNumber = true;
            this.nationalNumber_ = value;
            return this;
        }

        public PhoneNumber clearNationalNumber() {
            this.hasNationalNumber = false;
            this.nationalNumber_ = 0L;
            return this;
        }

        public boolean hasExtension() {
            return this.hasExtension;
        }

        public String getExtension() {
            return this.extension_;
        }

        public PhoneNumber setExtension(String value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.hasExtension = true;
            this.extension_ = value;
            return this;
        }

        public PhoneNumber clearExtension() {
            this.hasExtension = false;
            this.extension_ = "";
            return this;
        }

        public boolean hasItalianLeadingZero() {
            return this.hasItalianLeadingZero;
        }

        public boolean isItalianLeadingZero() {
            return this.italianLeadingZero_;
        }

        public PhoneNumber setItalianLeadingZero(boolean value) {
            this.hasItalianLeadingZero = true;
            this.italianLeadingZero_ = value;
            return this;
        }

        public PhoneNumber clearItalianLeadingZero() {
            this.hasItalianLeadingZero = false;
            this.italianLeadingZero_ = false;
            return this;
        }

        public boolean hasRawInput() {
            return this.hasRawInput;
        }

        public String getRawInput() {
            return this.rawInput_;
        }

        public PhoneNumber setRawInput(String value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.hasRawInput = true;
            this.rawInput_ = value;
            return this;
        }

        public PhoneNumber clearRawInput() {
            this.hasRawInput = false;
            this.rawInput_ = "";
            return this;
        }

        public boolean hasCountryCodeSource() {
            return this.hasCountryCodeSource;
        }

        public CountryCodeSource getCountryCodeSource() {
            return this.countryCodeSource_;
        }

        public PhoneNumber setCountryCodeSource(CountryCodeSource value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.hasCountryCodeSource = true;
            this.countryCodeSource_ = value;
            return this;
        }

        public PhoneNumber clearCountryCodeSource() {
            this.hasCountryCodeSource = false;
            this.countryCodeSource_ = CountryCodeSource.FROM_NUMBER_WITH_PLUS_SIGN;
            return this;
        }

        public boolean hasPreferredDomesticCarrierCode() {
            return this.hasPreferredDomesticCarrierCode;
        }

        public String getPreferredDomesticCarrierCode() {
            return this.preferredDomesticCarrierCode_;
        }

        public PhoneNumber setPreferredDomesticCarrierCode(String value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.hasPreferredDomesticCarrierCode = true;
            this.preferredDomesticCarrierCode_ = value;
            return this;
        }

        public PhoneNumber clearPreferredDomesticCarrierCode() {
            this.hasPreferredDomesticCarrierCode = false;
            this.preferredDomesticCarrierCode_ = "";
            return this;
        }

        public final PhoneNumber clear() {
            clearCountryCode();
            clearNationalNumber();
            clearExtension();
            clearItalianLeadingZero();
            clearRawInput();
            clearCountryCodeSource();
            clearPreferredDomesticCarrierCode();
            return this;
        }

        public PhoneNumber mergeFrom(PhoneNumber other) {
            if (other.hasCountryCode()) {
                setCountryCode(other.getCountryCode());
            }
            if (other.hasNationalNumber()) {
                setNationalNumber(other.getNationalNumber());
            }
            if (other.hasExtension()) {
                setExtension(other.getExtension());
            }
            if (other.hasItalianLeadingZero()) {
                setItalianLeadingZero(other.isItalianLeadingZero());
            }
            if (other.hasRawInput()) {
                setRawInput(other.getRawInput());
            }
            if (other.hasCountryCodeSource()) {
                setCountryCodeSource(other.getCountryCodeSource());
            }
            if (other.hasPreferredDomesticCarrierCode()) {
                setPreferredDomesticCarrierCode(other.getPreferredDomesticCarrierCode());
            }
            return this;
        }

        public boolean exactlySameAs(PhoneNumber other) {
            if (other == null) {
                return false;
            }
            if (this == other) {
                return true;
            }
            return this.countryCode_ == other.countryCode_ && this.nationalNumber_ == other.nationalNumber_ && this.extension_.equals(other.extension_) && this.italianLeadingZero_ == other.italianLeadingZero_ && this.rawInput_.equals(other.rawInput_) && this.countryCodeSource_ == other.countryCodeSource_ && this.preferredDomesticCarrierCode_.equals(other.preferredDomesticCarrierCode_) && hasPreferredDomesticCarrierCode() == other.hasPreferredDomesticCarrierCode();
        }

        public boolean equals(Object that) {
            return (that instanceof PhoneNumber) && exactlySameAs((PhoneNumber) that);
        }

        public int hashCode() {
            int hash = (53 * 41) + getCountryCode();
            return (53 * ((53 * ((53 * ((53 * ((53 * ((53 * ((53 * hash) + Long.valueOf(getNationalNumber()).hashCode())) + getExtension().hashCode())) + (isItalianLeadingZero() ? 1231 : 1237))) + getRawInput().hashCode())) + getCountryCodeSource().hashCode())) + getPreferredDomesticCarrierCode().hashCode())) + (hasPreferredDomesticCarrierCode() ? 1231 : 1237);
        }

        public String toString() {
            StringBuilder outputString = new StringBuilder();
            outputString.append("Country Code: ").append(this.countryCode_);
            outputString.append(" National Number: ").append(this.nationalNumber_);
            if (hasItalianLeadingZero() && isItalianLeadingZero()) {
                outputString.append(" Leading Zero: true");
            }
            if (hasExtension()) {
                outputString.append(" Extension: ").append(this.extension_);
            }
            if (hasCountryCodeSource()) {
                outputString.append(" Country Code Source: ").append(this.countryCodeSource_);
            }
            if (hasPreferredDomesticCarrierCode()) {
                outputString.append(" Preferred Domestic Carrier Code: ").append(this.preferredDomesticCarrierCode_);
            }
            return outputString.toString();
        }
    }
}