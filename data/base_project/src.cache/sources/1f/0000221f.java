package gov.nist.javax.sip.parser.extensions;

import gov.nist.javax.sip.header.extensions.Join;
import gov.nist.javax.sip.parser.Lexer;
import gov.nist.javax.sip.parser.ParametersParser;
import java.text.ParseException;

/* loaded from: JoinParser.class */
public class JoinParser extends ParametersParser {
    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.parser.extensions.JoinParser.parse():gov.nist.javax.sip.header.SIPHeader, file: JoinParser.class
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
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.parser.extensions.JoinParser.parse():gov.nist.javax.sip.header.SIPHeader, file: JoinParser.class
        */
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.parser.extensions.JoinParser.parse():gov.nist.javax.sip.header.SIPHeader");
    }

    public JoinParser(String callID) {
        super(callID);
    }

    protected JoinParser(Lexer lexer) {
        super(lexer);
    }

    public static void main(String[] args) throws ParseException {
        String[] to = {"Join: 12345th5z8z\n", "Join: 12345th5z8z;to-tag=tozght6-45;from-tag=fromzght789-337-2\n"};
        for (int i = 0; i < to.length; i++) {
            JoinParser tp = new JoinParser(to[i]);
            Join t = (Join) tp.parse();
            System.out.println("Parsing => " + to[i]);
            System.out.print("encoded = " + t.encode() + "==> ");
            System.out.println("callId " + t.getCallId() + " from-tag=" + t.getFromTag() + " to-tag=" + t.getToTag());
        }
    }
}