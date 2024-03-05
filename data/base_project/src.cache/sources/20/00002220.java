package gov.nist.javax.sip.parser.extensions;

import gov.nist.javax.sip.header.extensions.MinSE;
import gov.nist.javax.sip.parser.Lexer;
import gov.nist.javax.sip.parser.ParametersParser;
import java.text.ParseException;

/* loaded from: MinSEParser.class */
public class MinSEParser extends ParametersParser {
    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.parser.extensions.MinSEParser.parse():gov.nist.javax.sip.header.SIPHeader, file: MinSEParser.class
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
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.parser.extensions.MinSEParser.parse():gov.nist.javax.sip.header.SIPHeader, file: MinSEParser.class
        */
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.parser.extensions.MinSEParser.parse():gov.nist.javax.sip.header.SIPHeader");
    }

    public MinSEParser(String text) {
        super(text);
    }

    protected MinSEParser(Lexer lexer) {
        super(lexer);
    }

    public static void main(String[] args) throws ParseException {
        String[] to = {"Min-SE: 30\n", "Min-SE: 45;some-param=somevalue\n"};
        for (String str : to) {
            MinSEParser tp = new MinSEParser(str);
            MinSE t = (MinSE) tp.parse();
            System.out.println("encoded = " + t.encode());
            System.out.println("\ntime=" + t.getExpires());
            if (t.getParameter("some-param") != null) {
                System.out.println("some-param=" + t.getParameter("some-param"));
            }
        }
    }
}