package gov.nist.javax.sip.parser;

import gov.nist.core.Host;
import gov.nist.core.HostNameParser;
import gov.nist.core.Separators;
import gov.nist.javax.sip.SIPConstants;
import gov.nist.javax.sip.address.AddressImpl;
import gov.nist.javax.sip.address.GenericURI;
import gov.nist.javax.sip.address.SipUri;
import gov.nist.javax.sip.address.TelephoneNumber;
import gov.nist.javax.sip.header.ExtensionHeaderImpl;
import gov.nist.javax.sip.header.NameMap;
import gov.nist.javax.sip.header.RequestLine;
import gov.nist.javax.sip.header.SIPHeader;
import gov.nist.javax.sip.header.StatusLine;
import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;

/* loaded from: StringMsgParser.class */
public class StringMsgParser {
    protected boolean readBody;
    private ParseExceptionListener parseExceptionListener;
    private String rawStringMessage;
    private boolean strict;
    private static boolean computeContentLengthFromMessage = false;

    public StringMsgParser() {
        this.readBody = true;
    }

    public StringMsgParser(ParseExceptionListener exhandler) {
        this();
        this.parseExceptionListener = exhandler;
    }

    public void setParseExceptionListener(ParseExceptionListener pexhandler) {
        this.parseExceptionListener = pexhandler;
    }

    public SIPMessage parseSIPMessage(byte[] msgBuffer) throws ParseException {
        String currentLine;
        if (msgBuffer == null || msgBuffer.length == 0) {
            return null;
        }
        int i = 0;
        while (msgBuffer[i] < 32) {
            try {
                i++;
            } catch (ArrayIndexOutOfBoundsException e) {
                return null;
            }
        }
        String currentHeader = null;
        boolean isFirstLine = true;
        SIPMessage message = null;
        do {
            int lineStart = i;
            while (msgBuffer[i] != 13 && msgBuffer[i] != 10) {
                try {
                    i++;
                } catch (ArrayIndexOutOfBoundsException e2) {
                }
            }
            int lineLength = i - lineStart;
            try {
                currentLine = trimEndOfLine(new String(msgBuffer, lineStart, lineLength, "UTF-8"));
                if (currentLine.length() == 0) {
                    if (currentHeader != null && message != null) {
                        processHeader(currentHeader, message);
                    }
                } else if (isFirstLine) {
                    message = processFirstLine(currentLine);
                } else {
                    char firstChar = currentLine.charAt(0);
                    if (firstChar == '\t' || firstChar == ' ') {
                        if (currentHeader == null) {
                            throw new ParseException("Bad header continuation.", 0);
                        }
                        currentHeader = currentHeader + currentLine.substring(1);
                    } else {
                        if (currentHeader != null && message != null) {
                            processHeader(currentHeader, message);
                        }
                        currentHeader = currentLine;
                    }
                }
                if (msgBuffer[i] == 13 && msgBuffer.length > i + 1 && msgBuffer[i + 1] == 10) {
                    i++;
                }
                i++;
                isFirstLine = false;
            } catch (UnsupportedEncodingException e3) {
                throw new ParseException("Bad message encoding!", 0);
            }
        } while (currentLine.length() > 0);
        if (message == null) {
            throw new ParseException("Bad message", 0);
        }
        message.setSize(i);
        if (this.readBody && message.getContentLength() != null && message.getContentLength().getContentLength() != 0) {
            int bodyLength = msgBuffer.length - i;
            byte[] body = new byte[bodyLength];
            System.arraycopy(msgBuffer, i, body, 0, bodyLength);
            message.setMessageContent(body, computeContentLengthFromMessage, message.getContentLength().getContentLength());
        }
        return message;
    }

    public SIPMessage parseSIPMessage(String msgString) throws ParseException {
        String currentLine;
        if (msgString == null || msgString.length() == 0) {
            return null;
        }
        this.rawStringMessage = msgString;
        int i = 0;
        while (msgString.charAt(i) < ' ') {
            try {
                i++;
            } catch (ArrayIndexOutOfBoundsException e) {
                return null;
            } catch (StringIndexOutOfBoundsException e2) {
                return null;
            }
        }
        String currentHeader = null;
        boolean isFirstLine = true;
        SIPMessage message = null;
        do {
            int lineStart = i;
            try {
                char c = msgString.charAt(i);
                while (c != '\r' && c != '\n') {
                    i++;
                    c = msgString.charAt(i);
                }
                currentLine = trimEndOfLine(msgString.substring(lineStart, i));
                if (currentLine.length() == 0) {
                    if (currentHeader != null) {
                        processHeader(currentHeader, message);
                    }
                } else if (isFirstLine) {
                    message = processFirstLine(currentLine);
                } else {
                    char firstChar = currentLine.charAt(0);
                    if (firstChar == '\t' || firstChar == ' ') {
                        if (currentHeader == null) {
                            throw new ParseException("Bad header continuation.", 0);
                        }
                        currentHeader = currentHeader + currentLine.substring(1);
                    } else {
                        if (currentHeader != null) {
                            processHeader(currentHeader, message);
                        }
                        currentHeader = currentLine;
                    }
                }
                if (msgString.charAt(i) == '\r' && msgString.length() > i + 1 && msgString.charAt(i + 1) == '\n') {
                    i++;
                }
                i++;
                isFirstLine = false;
            } catch (ArrayIndexOutOfBoundsException e3) {
            } catch (StringIndexOutOfBoundsException e4) {
            }
        } while (currentLine.length() > 0);
        message.setSize(i);
        if (this.readBody && message.getContentLength() != null) {
            if (message.getContentLength().getContentLength() != 0) {
                String body = msgString.substring(i);
                message.setMessageContent(body, this.strict, computeContentLengthFromMessage, message.getContentLength().getContentLength());
            } else if (!computeContentLengthFromMessage && message.getContentLength().getContentLength() == 0 && !msgString.endsWith("\r\n\r\n") && this.strict) {
                throw new ParseException("Extraneous characters at the end of the message ", i);
            }
        }
        return message;
    }

    private String trimEndOfLine(String line) {
        if (line == null) {
            return line;
        }
        int i = line.length() - 1;
        while (i >= 0 && line.charAt(i) <= ' ') {
            i--;
        }
        if (i == line.length() - 1) {
            return line;
        }
        if (i == -1) {
            return "";
        }
        return line.substring(0, i + 1);
    }

    private SIPMessage processFirstLine(String firstLine) throws ParseException {
        SIPMessage message;
        if (!firstLine.startsWith(SIPConstants.SIP_VERSION_STRING)) {
            message = new SIPRequest();
            try {
                RequestLine requestLine = new RequestLineParser(firstLine + Separators.RETURN).parse();
                ((SIPRequest) message).setRequestLine(requestLine);
            } catch (ParseException ex) {
                if (this.parseExceptionListener != null) {
                    this.parseExceptionListener.handleException(ex, message, RequestLine.class, firstLine, this.rawStringMessage);
                } else {
                    throw ex;
                }
            }
        } else {
            message = new SIPResponse();
            try {
                StatusLine sl = new StatusLineParser(firstLine + Separators.RETURN).parse();
                ((SIPResponse) message).setStatusLine(sl);
            } catch (ParseException ex2) {
                if (this.parseExceptionListener != null) {
                    this.parseExceptionListener.handleException(ex2, message, StatusLine.class, firstLine, this.rawStringMessage);
                } else {
                    throw ex2;
                }
            }
        }
        return message;
    }

    private void processHeader(String header, SIPMessage message) throws ParseException {
        if (header == null || header.length() == 0) {
            return;
        }
        try {
            HeaderParser headerParser = ParserFactory.createParser(header + Separators.RETURN);
            try {
                SIPHeader sipHeader = headerParser.parse();
                message.attachHeader(sipHeader, false);
            } catch (ParseException ex) {
                if (this.parseExceptionListener != null) {
                    String headerName = Lexer.getHeaderName(header);
                    Class headerClass = NameMap.getClassFromName(headerName);
                    if (headerClass == null) {
                        headerClass = ExtensionHeaderImpl.class;
                    }
                    this.parseExceptionListener.handleException(ex, message, headerClass, header, this.rawStringMessage);
                }
            }
        } catch (ParseException ex2) {
            this.parseExceptionListener.handleException(ex2, message, null, header, this.rawStringMessage);
        }
    }

    public AddressImpl parseAddress(String address) throws ParseException {
        AddressParser addressParser = new AddressParser(address);
        return addressParser.address(true);
    }

    public Host parseHost(String host) throws ParseException {
        Lexer lexer = new Lexer("charLexer", host);
        return new HostNameParser(lexer).host();
    }

    public TelephoneNumber parseTelephoneNumber(String telephone_number) throws ParseException {
        return new URLParser(telephone_number).parseTelephoneNumber(true);
    }

    public SipUri parseSIPUrl(String url) throws ParseException {
        try {
            return new URLParser(url).sipURL(true);
        } catch (ClassCastException e) {
            throw new ParseException(url + " Not a SIP URL ", 0);
        }
    }

    public GenericURI parseUrl(String url) throws ParseException {
        return new URLParser(url).parse();
    }

    public SIPHeader parseSIPHeader(String header) throws ParseException {
        int start = 0;
        int end = header.length() - 1;
        while (header.charAt(start) <= ' ') {
            try {
                start++;
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new ParseException("Empty header.", 0);
            }
        }
        while (header.charAt(end) <= ' ') {
            end--;
        }
        StringBuffer buffer = new StringBuffer(end + 1);
        int i = start;
        int lineStart = start;
        boolean endOfLine = false;
        while (i <= end) {
            char c = header.charAt(i);
            if (c == '\r' || c == '\n') {
                if (!endOfLine) {
                    buffer.append(header.substring(lineStart, i));
                    endOfLine = true;
                }
            } else if (endOfLine) {
                endOfLine = false;
                if (c == ' ' || c == '\t') {
                    buffer.append(' ');
                    lineStart = i + 1;
                } else {
                    lineStart = i;
                }
            }
            i++;
        }
        buffer.append(header.substring(lineStart, i));
        buffer.append('\n');
        HeaderParser hp = ParserFactory.createParser(buffer.toString());
        if (hp == null) {
            throw new ParseException("could not create parser", 0);
        }
        return hp.parse();
    }

    public RequestLine parseSIPRequestLine(String requestLine) throws ParseException {
        return new RequestLineParser(requestLine + Separators.RETURN).parse();
    }

    public StatusLine parseSIPStatusLine(String statusLine) throws ParseException {
        return new StatusLineParser(statusLine + Separators.RETURN).parse();
    }

    public static void setComputeContentLengthFromMessage(boolean computeContentLengthFromMessage2) {
        computeContentLengthFromMessage = computeContentLengthFromMessage2;
    }

    public static void main(String[] args) throws ParseException {
        String[] messages = {"SIP/2.0 200 OK\r\nTo: \"The Little Blister\" <sip:LittleGuy@there.com>;tag=469bc066\r\nFrom: \"The Master Blaster\" <sip:BigGuy@here.com>;tag=11\r\nVia: SIP/2.0/UDP 139.10.134.246:5060;branch=z9hG4bK8b0a86f6_1030c7d18e0_17;received=139.10.134.246\r\nCall-ID: 1030c7d18ae_a97b0b_b@8b0a86f6\r\nCSeq: 1 SUBSCRIBE\r\nContact: <sip:172.16.11.162:5070>\r\nContent-Length: 0\r\n\r\n", "SIP/2.0 180 Ringing\r\nVia: SIP/2.0/UDP 172.18.1.29:5060;branch=z9hG4bK43fc10fb4446d55fc5c8f969607991f4\r\nTo: \"0440\" <sip:0440@212.209.220.131>;tag=2600\r\nFrom: \"Andreas\" <sip:andreas@e-horizon.se>;tag=8524\r\nCall-ID: f51a1851c5f570606140f14c8eb64fd3@172.18.1.29\r\nCSeq: 1 INVITE\r\nMax-Forwards: 70\r\nRecord-Route: <sip:212.209.220.131:5060>\r\nContent-Length: 0\r\n\r\n", "REGISTER sip:nist.gov SIP/2.0\r\nVia: SIP/2.0/UDP 129.6.55.182:14826\r\nMax-Forwards: 70\r\nFrom: <sip:mranga@nist.gov>;tag=6fcd5c7ace8b4a45acf0f0cd539b168b;epid=0d4c418ddf\r\nTo: <sip:mranga@nist.gov>\r\nCall-ID: c5679907eb954a8da9f9dceb282d7230@129.6.55.182\r\nCSeq: 1 REGISTER\r\nContact: <sip:129.6.55.182:14826>;methods=\"INVITE, MESSAGE, INFO, SUBSCRIBE, OPTIONS, BYE, CANCEL, NOTIFY, ACK, REFER\"\r\nUser-Agent: RTC/(Microsoft RTC)\r\nEvent:  registration\r\nAllow-Events: presence\r\nContent-Length: 0\r\n\r\nINVITE sip:littleguy@there.com:5060 SIP/2.0\r\nVia: SIP/2.0/UDP 65.243.118.100:5050\r\nFrom: M. Ranganathan  <sip:M.Ranganathan@sipbakeoff.com>;tag=1234\r\nTo: \"littleguy@there.com\" <sip:littleguy@there.com:5060> \r\nCall-ID: Q2AboBsaGn9!?x6@sipbakeoff.com \r\nCSeq: 1 INVITE \r\nContent-Length: 247\r\n\r\nv=0\r\no=4855 13760799956958020 13760799956958020 IN IP4  129.6.55.78\r\ns=mysession session\r\np=+46 8 52018010\r\nc=IN IP4  129.6.55.78\r\nt=0 0\r\nm=audio 6022 RTP/AVP 0 4 18\r\na=rtpmap:0 PCMU/8000\r\na=rtpmap:4 G723/8000\r\na=rtpmap:18 G729A/8000\r\na=ptime:20\r\n"};
        for (int i = 0; i < 20; i++) {
            new Thread(new Runnable(messages) { // from class: gov.nist.javax.sip.parser.StringMsgParser.1ParserThread
                String[] messages;

                {
                    this.messages = messages;
                }

                @Override // java.lang.Runnable
                public void run() {
                    for (int i2 = 0; i2 < this.messages.length; i2++) {
                        StringMsgParser smp = new StringMsgParser();
                        try {
                            SIPMessage sipMessage = smp.parseSIPMessage(this.messages[i2]);
                            System.out.println(" i = " + i2 + " branchId = " + sipMessage.getTopmostVia().getBranch());
                        } catch (ParseException e) {
                        }
                    }
                }
            }).start();
        }
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }
}