package gov.nist.javax.sip;

import javax.sip.Dialog;
import javax.sip.SipProvider;

/* loaded from: DialogExt.class */
public interface DialogExt extends Dialog {
    @Override // javax.sip.Dialog
    SipProvider getSipProvider();

    @Override // javax.sip.Dialog
    void setBackToBackUserAgent();

    void disableSequenceNumberValidation();
}