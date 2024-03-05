package gov.nist.javax.sip.parser;

import gov.nist.core.HostNameParser;
import gov.nist.core.HostPort;
import gov.nist.core.NameValue;
import gov.nist.core.Token;
import gov.nist.javax.sip.header.Protocol;
import gov.nist.javax.sip.header.Via;
import java.text.ParseException;

/* loaded from: ViaParser.class */
public class ViaParser extends HeaderParser {
    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.parser.ViaParser.nameValue():gov.nist.core.NameValue, file: ViaParser.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    @Override // gov.nist.core.ParserCore
    protected gov.nist.core.NameValue nameValue() throws java.text.ParseException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.parser.ViaParser.nameValue():gov.nist.core.NameValue, file: ViaParser.class
        */
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.parser.ViaParser.nameValue():gov.nist.core.NameValue");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.parser.ViaParser.parse():gov.nist.javax.sip.header.SIPHeader, file: ViaParser.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    @Override // gov.nist.javax.sip.parser.HeaderParser
    public gov.nist.javax.sip.header.SIPHeader parse() throws java.text.ParseException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.parser.ViaParser.parse():gov.nist.javax.sip.header.SIPHeader, file: ViaParser.class
        */
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.parser.ViaParser.parse():gov.nist.javax.sip.header.SIPHeader");
    }

    public ViaParser(String via) {
        super(via);
    }

    public ViaParser(Lexer lexer) {
        super(lexer);
    }

    private void parseVia(Via v) throws ParseException {
        this.lexer.match(4095);
        Token protocolName = this.lexer.getNextToken();
        this.lexer.SPorHT();
        this.lexer.match(47);
        this.lexer.SPorHT();
        this.lexer.match(4095);
        this.lexer.SPorHT();
        Token protocolVersion = this.lexer.getNextToken();
        this.lexer.SPorHT();
        this.lexer.match(47);
        this.lexer.SPorHT();
        this.lexer.match(4095);
        this.lexer.SPorHT();
        Token transport = this.lexer.getNextToken();
        this.lexer.SPorHT();
        Protocol protocol = new Protocol();
        protocol.setProtocolName(protocolName.getTokenValue());
        protocol.setProtocolVersion(protocolVersion.getTokenValue());
        protocol.setTransport(transport.getTokenValue());
        v.setSentProtocol(protocol);
        HostNameParser hnp = new HostNameParser(getLexer());
        HostPort hostPort = hnp.hostPort(true);
        v.setSentBy(hostPort);
        this.lexer.SPorHT();
        while (this.lexer.lookAhead(0) == ';') {
            this.lexer.consume(1);
            this.lexer.SPorHT();
            NameValue nameValue = nameValue();
            String name = nameValue.getName();
            if (name.equals("branch")) {
                String branchId = (String) nameValue.getValueAsObject();
                if (branchId == null) {
                    throw new ParseException("null branch Id", this.lexer.getPtr());
                }
            }
            v.setParameter(nameValue);
            this.lexer.SPorHT();
        }
        if (this.lexer.lookAhead(0) == '(') {
            this.lexer.selectLexer("charLexer");
            this.lexer.consume(1);
            StringBuffer comment = new StringBuffer();
            while (true) {
                char ch = this.lexer.lookAhead(0);
                if (ch == ')') {
                    this.lexer.consume(1);
                    break;
                } else if (ch == '\\') {
                    Token tok = this.lexer.getNextToken();
                    comment.append(tok.getTokenValue());
                    this.lexer.consume(1);
                    Token tok2 = this.lexer.getNextToken();
                    comment.append(tok2.getTokenValue());
                    this.lexer.consume(1);
                } else if (ch == '\n') {
                    break;
                } else {
                    comment.append(ch);
                    this.lexer.consume(1);
                }
            }
            v.setComment(comment.toString());
        }
    }
}