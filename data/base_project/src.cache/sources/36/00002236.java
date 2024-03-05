package gov.nist.javax.sip.parser.ims;

import gov.nist.core.Token;
import gov.nist.javax.sip.header.SIPHeaderList;
import gov.nist.javax.sip.header.ims.SecurityAgree;
import gov.nist.javax.sip.header.ims.SecurityClient;
import gov.nist.javax.sip.header.ims.SecurityClientList;
import gov.nist.javax.sip.header.ims.SecurityServer;
import gov.nist.javax.sip.header.ims.SecurityServerList;
import gov.nist.javax.sip.header.ims.SecurityVerify;
import gov.nist.javax.sip.header.ims.SecurityVerifyList;
import gov.nist.javax.sip.parser.HeaderParser;
import gov.nist.javax.sip.parser.Lexer;
import java.text.ParseException;

/* loaded from: SecurityAgreeParser.class */
public class SecurityAgreeParser extends HeaderParser {
    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.parser.ims.SecurityAgreeParser.parseParameter(gov.nist.javax.sip.header.ims.SecurityAgree):void, file: SecurityAgreeParser.class
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
    protected void parseParameter(gov.nist.javax.sip.header.ims.SecurityAgree r1) throws java.text.ParseException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.parser.ims.SecurityAgreeParser.parseParameter(gov.nist.javax.sip.header.ims.SecurityAgree):void, file: SecurityAgreeParser.class
        */
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.parser.ims.SecurityAgreeParser.parseParameter(gov.nist.javax.sip.header.ims.SecurityAgree):void");
    }

    public SecurityAgreeParser(String security) {
        super(security);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public SecurityAgreeParser(Lexer lexer) {
        super(lexer);
    }

    public SIPHeaderList parse(SecurityAgree header) throws ParseException {
        SIPHeaderList list;
        if (header.getClass().isInstance(new SecurityClient())) {
            list = new SecurityClientList();
        } else if (header.getClass().isInstance(new SecurityServer())) {
            list = new SecurityServerList();
        } else if (header.getClass().isInstance(new SecurityVerify())) {
            list = new SecurityVerifyList();
        } else {
            return null;
        }
        this.lexer.SPorHT();
        this.lexer.match(4095);
        Token type = this.lexer.getNextToken();
        header.setSecurityMechanism(type.getTokenValue());
        this.lexer.SPorHT();
        char la = this.lexer.lookAhead(0);
        if (la == '\n') {
            list.add((SIPHeaderList) header);
            return list;
        }
        if (la == ';') {
            this.lexer.match(59);
        }
        this.lexer.SPorHT();
        while (this.lexer.lookAhead(0) != '\n') {
            try {
                parseParameter(header);
                this.lexer.SPorHT();
                char laInLoop = this.lexer.lookAhead(0);
                if (laInLoop == '\n' || laInLoop == 0) {
                    break;
                }
                if (laInLoop == ',') {
                    list.add((SIPHeaderList) header);
                    if (header.getClass().isInstance(new SecurityClient())) {
                        header = new SecurityClient();
                    } else if (header.getClass().isInstance(new SecurityServer())) {
                        header = new SecurityServer();
                    } else if (header.getClass().isInstance(new SecurityVerify())) {
                        header = new SecurityVerify();
                    }
                    this.lexer.match(44);
                    this.lexer.SPorHT();
                    this.lexer.match(4095);
                    Token type2 = this.lexer.getNextToken();
                    header.setSecurityMechanism(type2.getTokenValue());
                }
                this.lexer.SPorHT();
                if (this.lexer.lookAhead(0) == ';') {
                    this.lexer.match(59);
                }
                this.lexer.SPorHT();
            } catch (ParseException ex) {
                throw ex;
            }
        }
        list.add((SIPHeaderList) header);
        return list;
    }
}