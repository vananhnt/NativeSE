package gov.nist.javax.sip.parser.ims;

import gov.nist.javax.sip.header.ims.PUserDatabase;
import gov.nist.javax.sip.parser.Lexer;
import gov.nist.javax.sip.parser.ParametersParser;
import gov.nist.javax.sip.parser.TokenTypes;
import java.text.ParseException;

/* loaded from: PUserDatabaseParser.class */
public class PUserDatabaseParser extends ParametersParser implements TokenTypes {
    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.parser.ims.PUserDatabaseParser.parse():gov.nist.javax.sip.header.SIPHeader, file: PUserDatabaseParser.class
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
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.parser.ims.PUserDatabaseParser.parse():gov.nist.javax.sip.header.SIPHeader, file: PUserDatabaseParser.class
        */
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.parser.ims.PUserDatabaseParser.parse():gov.nist.javax.sip.header.SIPHeader");
    }

    public PUserDatabaseParser(String databaseName) {
        super(databaseName);
    }

    public PUserDatabaseParser(Lexer lexer) {
        super(lexer);
    }

    private void parseheader(PUserDatabase userDatabase) throws ParseException {
        StringBuffer dbname = new StringBuffer();
        this.lexer.match(60);
        while (this.lexer.hasMoreChars()) {
            char next = this.lexer.getNextChar();
            if (next != '>' && next != '\n') {
                dbname.append(next);
            }
        }
        userDatabase.setDatabaseName(dbname.toString());
        super.parse(userDatabase);
    }
}