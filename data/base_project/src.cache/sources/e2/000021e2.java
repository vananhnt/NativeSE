package gov.nist.javax.sip.parser;

import gov.nist.core.Token;
import gov.nist.javax.sip.header.AuthenticationHeader;
import java.text.ParseException;

/* loaded from: ChallengeParser.class */
public abstract class ChallengeParser extends HeaderParser {
    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.parser.ChallengeParser.parseParameter(gov.nist.javax.sip.header.AuthenticationHeader):void, file: ChallengeParser.class
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
    protected void parseParameter(gov.nist.javax.sip.header.AuthenticationHeader r1) throws java.text.ParseException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.parser.ChallengeParser.parseParameter(gov.nist.javax.sip.header.AuthenticationHeader):void, file: ChallengeParser.class
        */
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.parser.ChallengeParser.parseParameter(gov.nist.javax.sip.header.AuthenticationHeader):void");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ChallengeParser(String challenge) {
        super(challenge);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ChallengeParser(Lexer lexer) {
        super(lexer);
    }

    public void parse(AuthenticationHeader header) throws ParseException {
        this.lexer.SPorHT();
        this.lexer.match(4095);
        Token type = this.lexer.getNextToken();
        this.lexer.SPorHT();
        header.setScheme(type.getTokenValue());
        while (this.lexer.lookAhead(0) != '\n') {
            try {
                parseParameter(header);
                this.lexer.SPorHT();
                char la = this.lexer.lookAhead(0);
                if (la == '\n' || la == 0) {
                    break;
                }
                this.lexer.match(44);
                this.lexer.SPorHT();
            } catch (ParseException ex) {
                throw ex;
            }
        }
    }
}