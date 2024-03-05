package gov.nist.javax.sip.parser;

/* loaded from: AddressParametersParser.class */
public class AddressParametersParser extends ParametersParser {
    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.parser.AddressParametersParser.parse(gov.nist.javax.sip.header.AddressParametersHeader):void, file: AddressParametersParser.class
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
    protected void parse(gov.nist.javax.sip.header.AddressParametersHeader r1) throws java.text.ParseException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.parser.AddressParametersParser.parse(gov.nist.javax.sip.header.AddressParametersHeader):void, file: AddressParametersParser.class
        */
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.parser.AddressParametersParser.parse(gov.nist.javax.sip.header.AddressParametersHeader):void");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AddressParametersParser(Lexer lexer) {
        super(lexer);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AddressParametersParser(String buffer) {
        super(buffer);
    }
}