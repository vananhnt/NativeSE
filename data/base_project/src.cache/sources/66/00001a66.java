package com.android.internal.telephony;

import gov.nist.core.Separators;

/* loaded from: EncodeException.class */
public class EncodeException extends Exception {
    public EncodeException() {
    }

    public EncodeException(String s) {
        super(s);
    }

    public EncodeException(char c) {
        super("Unencodable char: '" + c + Separators.QUOTE);
    }
}