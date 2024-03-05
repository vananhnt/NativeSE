package gov.nist.javax.sip.header;

import javax.sip.header.ViaHeader;

/* loaded from: ViaHeaderExt.class */
public interface ViaHeaderExt extends ViaHeader {
    @Override // 
    String getSentByField();

    @Override // 
    String getSentProtocolField();
}