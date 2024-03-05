package gov.nist.javax.sip.parser.ims;

import gov.nist.javax.sip.parser.AddressParametersParser;
import gov.nist.javax.sip.parser.Lexer;

/* loaded from: PAssociatedURIParser.class */
public class PAssociatedURIParser extends AddressParametersParser {
    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.parser.ims.PAssociatedURIParser.parse():gov.nist.javax.sip.header.SIPHeader, file: PAssociatedURIParser.class
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
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.parser.ims.PAssociatedURIParser.parse():gov.nist.javax.sip.header.SIPHeader, file: PAssociatedURIParser.class
        */
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.parser.ims.PAssociatedURIParser.parse():gov.nist.javax.sip.header.SIPHeader");
    }

    public PAssociatedURIParser(String associatedURI) {
        super(associatedURI);
    }

    protected PAssociatedURIParser(Lexer lexer) {
        super(lexer);
    }
}