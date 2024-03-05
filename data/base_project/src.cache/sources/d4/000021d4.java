package gov.nist.javax.sip.message;

import gov.nist.core.InternalErrorHandler;
import gov.nist.core.Separators;
import gov.nist.javax.sip.Utils;
import gov.nist.javax.sip.address.SipUri;
import gov.nist.javax.sip.header.CSeq;
import gov.nist.javax.sip.header.CallID;
import gov.nist.javax.sip.header.ContactList;
import gov.nist.javax.sip.header.ContentLength;
import gov.nist.javax.sip.header.ContentType;
import gov.nist.javax.sip.header.From;
import gov.nist.javax.sip.header.MaxForwards;
import gov.nist.javax.sip.header.ReasonList;
import gov.nist.javax.sip.header.RecordRouteList;
import gov.nist.javax.sip.header.RequireList;
import gov.nist.javax.sip.header.SIPHeader;
import gov.nist.javax.sip.header.StatusLine;
import gov.nist.javax.sip.header.To;
import gov.nist.javax.sip.header.Via;
import gov.nist.javax.sip.header.ViaList;
import gov.nist.javax.sip.header.extensions.SessionExpires;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.LinkedList;
import javax.sip.header.ReasonHeader;
import javax.sip.header.ServerHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;

/* loaded from: SIPResponse.class */
public final class SIPResponse extends SIPMessage implements Response, ResponseExt {
    protected StatusLine statusLine;

    public static String getReasonPhrase(int rc) {
        String retval;
        switch (rc) {
            case 100:
                retval = "Trying";
                break;
            case 180:
                retval = "Ringing";
                break;
            case 181:
                retval = "Call is being forwarded";
                break;
            case 182:
                retval = "Queued";
                break;
            case 183:
                retval = "Session progress";
                break;
            case 200:
                retval = "OK";
                break;
            case 202:
                retval = "Accepted";
                break;
            case 300:
                retval = "Multiple choices";
                break;
            case 301:
                retval = "Moved permanently";
                break;
            case 302:
                retval = "Moved Temporarily";
                break;
            case 305:
                retval = "Use proxy";
                break;
            case Response.ALTERNATIVE_SERVICE /* 380 */:
                retval = "Alternative service";
                break;
            case 400:
                retval = "Bad request";
                break;
            case 401:
                retval = "Unauthorized";
                break;
            case 402:
                retval = "Payment required";
                break;
            case 403:
                retval = "Forbidden";
                break;
            case 404:
                retval = "Not found";
                break;
            case 405:
                retval = "Method not allowed";
                break;
            case 406:
                retval = "Not acceptable";
                break;
            case 407:
                retval = "Proxy Authentication required";
                break;
            case 408:
                retval = "Request timeout";
                break;
            case 410:
                retval = "Gone";
                break;
            case 412:
                retval = "Conditional request failed";
                break;
            case 413:
                retval = "Request entity too large";
                break;
            case 414:
                retval = "Request-URI too large";
                break;
            case 415:
                retval = "Unsupported media type";
                break;
            case 416:
                retval = "Unsupported URI Scheme";
                break;
            case 420:
                retval = "Bad extension";
                break;
            case Response.EXTENSION_REQUIRED /* 421 */:
                retval = "Etension Required";
                break;
            case 423:
                retval = "Interval too brief";
                break;
            case 480:
                retval = "Temporarily Unavailable";
                break;
            case Response.CALL_OR_TRANSACTION_DOES_NOT_EXIST /* 481 */:
                retval = "Call leg/Transaction does not exist";
                break;
            case Response.LOOP_DETECTED /* 482 */:
                retval = "Loop detected";
                break;
            case Response.TOO_MANY_HOPS /* 483 */:
                retval = "Too many hops";
                break;
            case Response.ADDRESS_INCOMPLETE /* 484 */:
                retval = "Address incomplete";
                break;
            case Response.AMBIGUOUS /* 485 */:
                retval = "Ambiguous";
                break;
            case Response.BUSY_HERE /* 486 */:
                retval = "Busy here";
                break;
            case Response.REQUEST_TERMINATED /* 487 */:
                retval = "Request Terminated";
                break;
            case 488:
                retval = "Not Acceptable here";
                break;
            case 489:
                retval = "Bad Event";
                break;
            case 491:
                retval = "Request Pending";
                break;
            case 493:
                retval = "Undecipherable";
                break;
            case 500:
                retval = "Server Internal Error";
                break;
            case 501:
                retval = "Not implemented";
                break;
            case 502:
                retval = "Bad gateway";
                break;
            case 503:
                retval = "Service unavailable";
                break;
            case 504:
                retval = "Gateway timeout";
                break;
            case 505:
                retval = "SIP version not supported";
                break;
            case 513:
                retval = "Message Too Large";
                break;
            case 600:
                retval = "Busy everywhere";
                break;
            case 603:
                retval = "Decline";
                break;
            case 604:
                retval = "Does not exist anywhere";
                break;
            case 606:
                retval = "Session Not acceptable";
                break;
            default:
                retval = "Unknown Status";
                break;
        }
        return retval;
    }

    @Override // javax.sip.message.Response
    public void setStatusCode(int statusCode) throws ParseException {
        if (statusCode < 100 || statusCode > 699) {
            throw new ParseException("bad status code", 0);
        }
        if (this.statusLine == null) {
            this.statusLine = new StatusLine();
        }
        this.statusLine.setStatusCode(statusCode);
    }

    public StatusLine getStatusLine() {
        return this.statusLine;
    }

    @Override // javax.sip.message.Response
    public int getStatusCode() {
        return this.statusLine.getStatusCode();
    }

    @Override // javax.sip.message.Response
    public void setReasonPhrase(String reasonPhrase) {
        if (reasonPhrase == null) {
            throw new IllegalArgumentException("Bad reason phrase");
        }
        if (this.statusLine == null) {
            this.statusLine = new StatusLine();
        }
        this.statusLine.setReasonPhrase(reasonPhrase);
    }

    @Override // javax.sip.message.Response
    public String getReasonPhrase() {
        if (this.statusLine == null || this.statusLine.getReasonPhrase() == null) {
            return "";
        }
        return this.statusLine.getReasonPhrase();
    }

    public static boolean isFinalResponse(int rc) {
        return rc >= 200 && rc < 700;
    }

    public boolean isFinalResponse() {
        return isFinalResponse(this.statusLine.getStatusCode());
    }

    public void setStatusLine(StatusLine sl) {
        this.statusLine = sl;
    }

    @Override // gov.nist.javax.sip.message.SIPMessage, gov.nist.javax.sip.message.MessageObject, gov.nist.core.GenericObject
    public String debugDump() {
        String superstring = super.debugDump();
        this.stringRepresentation = "";
        sprint(SIPResponse.class.getCanonicalName());
        sprint("{");
        if (this.statusLine != null) {
            sprint(this.statusLine.debugDump());
        }
        sprint(superstring);
        sprint("}");
        return this.stringRepresentation;
    }

    public void checkHeaders() throws ParseException {
        if (getCSeq() == null) {
            throw new ParseException("CSeq Is missing ", 0);
        }
        if (getTo() == null) {
            throw new ParseException("To Is missing ", 0);
        }
        if (getFrom() == null) {
            throw new ParseException("From Is missing ", 0);
        }
        if (getViaHeaders() == null) {
            throw new ParseException("Via Is missing ", 0);
        }
        if (getCallId() == null) {
            throw new ParseException("Call-ID Is missing ", 0);
        }
        if (getStatusCode() > 699) {
            throw new ParseException("Unknown error code!" + getStatusCode(), 0);
        }
    }

    @Override // gov.nist.javax.sip.message.SIPMessage, gov.nist.javax.sip.message.MessageObject, gov.nist.core.GenericObject
    public String encode() {
        String retval;
        if (this.statusLine != null) {
            retval = this.statusLine.encode() + super.encode();
        } else {
            retval = super.encode();
        }
        return retval;
    }

    @Override // gov.nist.javax.sip.message.SIPMessage
    public String encodeMessage() {
        String retval;
        if (this.statusLine != null) {
            retval = this.statusLine.encode() + super.encodeSIPHeaders();
        } else {
            retval = super.encodeSIPHeaders();
        }
        return retval;
    }

    @Override // gov.nist.javax.sip.message.SIPMessage
    public LinkedList getMessageAsEncodedStrings() {
        LinkedList retval = super.getMessageAsEncodedStrings();
        if (this.statusLine != null) {
            retval.addFirst(this.statusLine.encode());
        }
        return retval;
    }

    @Override // gov.nist.javax.sip.message.SIPMessage, gov.nist.core.GenericObject
    public Object clone() {
        SIPResponse retval = (SIPResponse) super.clone();
        if (this.statusLine != null) {
            retval.statusLine = (StatusLine) this.statusLine.clone();
        }
        return retval;
    }

    @Override // gov.nist.javax.sip.message.SIPMessage, gov.nist.core.GenericObject
    public boolean equals(Object other) {
        if (!getClass().equals(other.getClass())) {
            return false;
        }
        SIPResponse that = (SIPResponse) other;
        return this.statusLine.equals(that.statusLine) && super.equals(other);
    }

    @Override // gov.nist.javax.sip.message.SIPMessage, gov.nist.core.GenericObject
    public boolean match(Object matchObj) {
        if (matchObj == null) {
            return true;
        }
        if (!matchObj.getClass().equals(getClass())) {
            return false;
        }
        if (matchObj == this) {
            return true;
        }
        SIPResponse that = (SIPResponse) matchObj;
        StatusLine rline = that.statusLine;
        if (this.statusLine == null && rline != null) {
            return false;
        }
        if (this.statusLine == rline) {
            return super.match(matchObj);
        }
        return this.statusLine.match(that.statusLine) && super.match(matchObj);
    }

    @Override // gov.nist.javax.sip.message.SIPMessage
    public byte[] encodeAsBytes(String transport) {
        byte[] slbytes = null;
        if (this.statusLine != null) {
            try {
                slbytes = this.statusLine.encode().getBytes("UTF-8");
            } catch (UnsupportedEncodingException ex) {
                InternalErrorHandler.handleException(ex);
            }
        }
        byte[] superbytes = super.encodeAsBytes(transport);
        byte[] retval = new byte[slbytes.length + superbytes.length];
        System.arraycopy(slbytes, 0, retval, 0, slbytes.length);
        System.arraycopy(superbytes, 0, retval, slbytes.length, superbytes.length);
        return retval;
    }

    @Override // gov.nist.javax.sip.message.SIPMessage
    public String getDialogId(boolean isServer) {
        CallID cid = (CallID) getCallId();
        From from = (From) getFrom();
        To to = (To) getTo();
        StringBuffer retval = new StringBuffer(cid.getCallId());
        if (!isServer) {
            if (from.getTag() != null) {
                retval.append(Separators.COLON);
                retval.append(from.getTag());
            }
            if (to.getTag() != null) {
                retval.append(Separators.COLON);
                retval.append(to.getTag());
            }
        } else {
            if (to.getTag() != null) {
                retval.append(Separators.COLON);
                retval.append(to.getTag());
            }
            if (from.getTag() != null) {
                retval.append(Separators.COLON);
                retval.append(from.getTag());
            }
        }
        return retval.toString().toLowerCase();
    }

    public String getDialogId(boolean isServer, String toTag) {
        CallID cid = (CallID) getCallId();
        From from = (From) getFrom();
        StringBuffer retval = new StringBuffer(cid.getCallId());
        if (!isServer) {
            if (from.getTag() != null) {
                retval.append(Separators.COLON);
                retval.append(from.getTag());
            }
            if (toTag != null) {
                retval.append(Separators.COLON);
                retval.append(toTag);
            }
        } else {
            if (toTag != null) {
                retval.append(Separators.COLON);
                retval.append(toTag);
            }
            if (from.getTag() != null) {
                retval.append(Separators.COLON);
                retval.append(from.getTag());
            }
        }
        return retval.toString().toLowerCase();
    }

    private final void setBranch(Via via, String method) {
        String branch;
        if (method.equals("ACK")) {
            if (this.statusLine.getStatusCode() >= 300) {
                branch = getTopmostVia().getBranch();
            } else {
                branch = Utils.getInstance().generateBranchId();
            }
        } else if (method.equals(Request.CANCEL)) {
            branch = getTopmostVia().getBranch();
        } else {
            return;
        }
        try {
            via.setBranch(branch);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override // gov.nist.javax.sip.message.SIPMessage, gov.nist.javax.sip.message.MessageExt
    public String getFirstLine() {
        if (this.statusLine == null) {
            return null;
        }
        return this.statusLine.encode();
    }

    @Override // gov.nist.javax.sip.message.SIPMessage, javax.sip.message.Message
    public void setSIPVersion(String sipVersion) {
        this.statusLine.setSipVersion(sipVersion);
    }

    @Override // gov.nist.javax.sip.message.SIPMessage, javax.sip.message.Message
    public String getSIPVersion() {
        return this.statusLine.getSipVersion();
    }

    @Override // gov.nist.javax.sip.message.SIPMessage, javax.sip.message.Message
    public String toString() {
        return this.statusLine == null ? "" : this.statusLine.encode() + super.encode();
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r10v0, types: [gov.nist.javax.sip.header.To] */
    public SIPRequest createRequest(SipUri requestURI, Via via, CSeq cseq, From from, To to) {
        SIPRequest newRequest = new SIPRequest();
        String method = cseq.getMethod();
        newRequest.setMethod(method);
        newRequest.setRequestURI(requestURI);
        setBranch(via, method);
        newRequest.setHeader(via);
        newRequest.setHeader(cseq);
        Iterator headerIterator = getHeaders();
        while (headerIterator.hasNext()) {
            SIPHeader nextHeader = headerIterator.next();
            if (!SIPMessage.isResponseHeader(nextHeader) && !(nextHeader instanceof ViaList) && !(nextHeader instanceof CSeq) && !(nextHeader instanceof ContentType) && !(nextHeader instanceof ContentLength) && !(nextHeader instanceof RecordRouteList) && !(nextHeader instanceof RequireList) && !(nextHeader instanceof ContactList) && !(nextHeader instanceof ContentLength) && !(nextHeader instanceof ServerHeader) && !(nextHeader instanceof ReasonHeader) && !(nextHeader instanceof SessionExpires) && !(nextHeader instanceof ReasonList)) {
                if (nextHeader instanceof To) {
                    nextHeader = to;
                } else if (nextHeader instanceof From) {
                    nextHeader = from;
                }
                try {
                    newRequest.attachHeader(nextHeader, false);
                } catch (SIPDuplicateHeaderException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            newRequest.attachHeader(new MaxForwards(70), false);
        } catch (Exception e2) {
        }
        if (MessageFactoryImpl.getDefaultUserAgentHeader() != null) {
            newRequest.setHeader(MessageFactoryImpl.getDefaultUserAgentHeader());
        }
        return newRequest;
    }
}