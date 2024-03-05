package gov.nist.javax.sip.message;

import java.text.ParseException;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContentLengthHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.Message;

/* loaded from: MessageExt.class */
public interface MessageExt extends Message {
    @Override // javax.sip.message.Message
    void setApplicationData(Object obj);

    @Override // javax.sip.message.Message
    Object getApplicationData();

    MultipartMimeContent getMultipartMimeContent() throws ParseException;

    ViaHeader getTopmostViaHeader();

    FromHeader getFromHeader();

    ToHeader getToHeader();

    CallIdHeader getCallIdHeader();

    CSeqHeader getCSeqHeader();

    ContentTypeHeader getContentTypeHeader();

    ContentLengthHeader getContentLengthHeader();

    String getFirstLine();
}