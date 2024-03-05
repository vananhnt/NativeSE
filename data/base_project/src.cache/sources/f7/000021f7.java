package gov.nist.javax.sip.parser;

import gov.nist.core.LexerCore;
import gov.nist.core.ParserCore;
import gov.nist.core.Separators;
import java.text.ParseException;

/* loaded from: Parser.class */
public abstract class Parser extends ParserCore implements TokenTypes {
    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.parser.Parser.sipVersion():java.lang.String, file: Parser.class
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
    protected java.lang.String sipVersion() throws java.text.ParseException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.parser.Parser.sipVersion():java.lang.String, file: Parser.class
        */
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.parser.Parser.sipVersion():java.lang.String");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.parser.Parser.method():java.lang.String, file: Parser.class
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
    protected java.lang.String method() throws java.text.ParseException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.parser.Parser.method():java.lang.String, file: Parser.class
        */
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.parser.Parser.method():java.lang.String");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ParseException createParseException(String exceptionString) {
        return new ParseException(this.lexer.getBuffer() + Separators.COLON + exceptionString, this.lexer.getPtr());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Lexer getLexer() {
        return (Lexer) this.lexer;
    }

    public static final void checkToken(String token) throws ParseException {
        if (token == null || token.length() == 0) {
            throw new ParseException("null or empty token", -1);
        }
        for (int i = 0; i < token.length(); i++) {
            if (!LexerCore.isTokenChar(token.charAt(i))) {
                throw new ParseException("Invalid character(s) in string (not allowed in 'token')", i);
            }
        }
    }
}