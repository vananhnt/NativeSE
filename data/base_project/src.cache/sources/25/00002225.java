package gov.nist.javax.sip.parser.ims;

import gov.nist.javax.sip.parser.HeaderParser;
import gov.nist.javax.sip.parser.Lexer;

/* loaded from: AddressHeaderParser.class */
abstract class AddressHeaderParser extends HeaderParser {
    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.parser.ims.AddressHeaderParser.parse(gov.nist.javax.sip.header.ims.AddressHeaderIms):void, file: AddressHeaderParser.class
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
    protected void parse(gov.nist.javax.sip.header.ims.AddressHeaderIms r1) throws java.text.ParseException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.parser.ims.AddressHeaderParser.parse(gov.nist.javax.sip.header.ims.AddressHeaderIms):void, file: AddressHeaderParser.class
        */
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.parser.ims.AddressHeaderParser.parse(gov.nist.javax.sip.header.ims.AddressHeaderIms):void");
    }

    protected AddressHeaderParser(Lexer lexer) {
        super(lexer);
    }

    protected AddressHeaderParser(String buffer) {
        super(buffer);
    }
}