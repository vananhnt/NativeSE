package gov.nist.javax.sip.parser;

import gov.nist.javax.sip.header.UserAgent;
import java.text.ParseException;

/* loaded from: UserAgentParser.class */
public class UserAgentParser extends HeaderParser {
    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.parser.UserAgentParser.parse():gov.nist.javax.sip.header.SIPHeader, file: UserAgentParser.class
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
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.parser.UserAgentParser.parse():gov.nist.javax.sip.header.SIPHeader, file: UserAgentParser.class
        */
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.parser.UserAgentParser.parse():gov.nist.javax.sip.header.SIPHeader");
    }

    public UserAgentParser(String userAgent) {
        super(userAgent);
    }

    protected UserAgentParser(Lexer lexer) {
        super(lexer);
    }

    public static void main(String[] args) throws ParseException {
        String[] userAgent = {"User-Agent: Softphone/Beta1.5 \n", "User-Agent:Nist/Beta1 (beta version) \n", "User-Agent: Nist UA (beta version)\n", "User-Agent: Nist1.0/Beta2 Ubi/vers.1.0 (very cool) \n", "User-Agent: SJphone/1.60.299a/L (SJ Labs)\n", "User-Agent: sipXecs/3.5.11 sipXecs/sipxbridge (Linux)\n"};
        for (String str : userAgent) {
            UserAgentParser parser = new UserAgentParser(str);
            UserAgent ua = (UserAgent) parser.parse();
            System.out.println("encoded = " + ua.encode());
        }
    }
}