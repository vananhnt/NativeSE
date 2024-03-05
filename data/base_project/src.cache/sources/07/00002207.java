package gov.nist.javax.sip.parser;

import gov.nist.javax.sip.header.RequestLine;
import java.text.ParseException;

/* loaded from: RequestLineParser.class */
public class RequestLineParser extends Parser {
    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.parser.RequestLineParser.parse():gov.nist.javax.sip.header.RequestLine, file: RequestLineParser.class
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
    public gov.nist.javax.sip.header.RequestLine parse() throws java.text.ParseException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.parser.RequestLineParser.parse():gov.nist.javax.sip.header.RequestLine, file: RequestLineParser.class
        */
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.parser.RequestLineParser.parse():gov.nist.javax.sip.header.RequestLine");
    }

    public RequestLineParser(String requestLine) {
        this.lexer = new Lexer("method_keywordLexer", requestLine);
    }

    public RequestLineParser(Lexer lexer) {
        this.lexer = lexer;
        this.lexer.selectLexer("method_keywordLexer");
    }

    public static void main(String[] args) throws ParseException {
        String[] requestLines = {"REGISTER sip:192.168.0.68 SIP/2.0\n", "REGISTER sip:company.com SIP/2.0\n", "INVITE sip:3660@166.35.231.140 SIP/2.0\n", "INVITE sip:user@company.com SIP/2.0\n", "REGISTER sip:[2001::1]:5060;transport=tcp SIP/2.0\n", "REGISTER sip:[2002:800:700:600:30:4:6:1]:5060;transport=udp SIP/2.0\n", "REGISTER sip:[3ffe:800:700::30:4:6:1]:5060;transport=tls SIP/2.0\n", "REGISTER sip:[2001:720:1710:0:201:29ff:fe21:f403]:5060;transport=udp SIP/2.0\n", "OPTIONS sip:135.180.130.133 SIP/2.0\n"};
        for (String str : requestLines) {
            RequestLineParser rlp = new RequestLineParser(str);
            RequestLine rl = rlp.parse();
            System.out.println("encoded = " + rl.encode());
        }
    }
}