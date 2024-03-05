package gov.nist.javax.sip.parser.extensions;

import gov.nist.javax.sip.header.extensions.SessionExpires;
import gov.nist.javax.sip.parser.Lexer;
import gov.nist.javax.sip.parser.ParametersParser;
import java.text.ParseException;

/* loaded from: SessionExpiresParser.class */
public class SessionExpiresParser extends ParametersParser {
    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.parser.extensions.SessionExpiresParser.parse():gov.nist.javax.sip.header.SIPHeader, file: SessionExpiresParser.class
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
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.parser.extensions.SessionExpiresParser.parse():gov.nist.javax.sip.header.SIPHeader, file: SessionExpiresParser.class
        */
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.parser.extensions.SessionExpiresParser.parse():gov.nist.javax.sip.header.SIPHeader");
    }

    public SessionExpiresParser(String text) {
        super(text);
    }

    protected SessionExpiresParser(Lexer lexer) {
        super(lexer);
    }

    public static void main(String[] args) throws ParseException {
        String[] to = {"Session-Expires: 30\n", "Session-Expires: 45;refresher=uac\n"};
        for (String str : to) {
            SessionExpiresParser tp = new SessionExpiresParser(str);
            SessionExpires t = (SessionExpires) tp.parse();
            System.out.println("encoded = " + t.encode());
            System.out.println("\ntime=" + t.getExpires());
            if (t.getParameter(SessionExpires.REFRESHER) != null) {
                System.out.println("refresher=" + t.getParameter(SessionExpires.REFRESHER));
            }
        }
    }
}