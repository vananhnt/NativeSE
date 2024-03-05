package gov.nist.javax.sip.parser.ims;

import gov.nist.core.Token;
import gov.nist.javax.sip.header.ims.PVisitedNetworkID;
import gov.nist.javax.sip.parser.Lexer;
import gov.nist.javax.sip.parser.ParametersParser;
import gov.nist.javax.sip.parser.TokenTypes;
import java.text.ParseException;

/* loaded from: PVisitedNetworkIDParser.class */
public class PVisitedNetworkIDParser extends ParametersParser implements TokenTypes {
    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.parser.ims.PVisitedNetworkIDParser.parse():gov.nist.javax.sip.header.SIPHeader, file: PVisitedNetworkIDParser.class
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
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.parser.ims.PVisitedNetworkIDParser.parse():gov.nist.javax.sip.header.SIPHeader, file: PVisitedNetworkIDParser.class
        */
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.parser.ims.PVisitedNetworkIDParser.parse():gov.nist.javax.sip.header.SIPHeader");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.parser.ims.PVisitedNetworkIDParser.parseQuotedString(gov.nist.javax.sip.header.ims.PVisitedNetworkID):void, file: PVisitedNetworkIDParser.class
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
    protected void parseQuotedString(gov.nist.javax.sip.header.ims.PVisitedNetworkID r1) throws java.text.ParseException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.parser.ims.PVisitedNetworkIDParser.parseQuotedString(gov.nist.javax.sip.header.ims.PVisitedNetworkID):void, file: PVisitedNetworkIDParser.class
        */
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.parser.ims.PVisitedNetworkIDParser.parseQuotedString(gov.nist.javax.sip.header.ims.PVisitedNetworkID):void");
    }

    public PVisitedNetworkIDParser(String networkID) {
        super(networkID);
    }

    protected PVisitedNetworkIDParser(Lexer lexer) {
        super(lexer);
    }

    protected void parseToken(PVisitedNetworkID visitedNetworkID) throws ParseException {
        this.lexer.match(4095);
        Token token = this.lexer.getNextToken();
        visitedNetworkID.setVisitedNetworkID(token);
        super.parse(visitedNetworkID);
    }
}