package gov.nist.core;

import java.text.ParseException;
import java.util.Hashtable;

/* loaded from: LexerCore.class */
public class LexerCore extends StringTokenizer {
    public static final int START = 2048;
    public static final int END = 4096;
    public static final int ID = 4095;
    public static final int SAFE = 4094;
    public static final int WHITESPACE = 4097;
    public static final int DIGIT = 4098;
    public static final int ALPHA = 4099;
    public static final int BACKSLASH = 92;
    public static final int QUOTE = 39;
    public static final int AT = 64;
    public static final int SP = 32;
    public static final int HT = 9;
    public static final int COLON = 58;
    public static final int STAR = 42;
    public static final int DOLLAR = 36;
    public static final int PLUS = 43;
    public static final int POUND = 35;
    public static final int MINUS = 45;
    public static final int DOUBLEQUOTE = 34;
    public static final int TILDE = 126;
    public static final int BACK_QUOTE = 96;
    public static final int NULL = 0;
    public static final int EQUALS = 61;
    public static final int SEMICOLON = 59;
    public static final int SLASH = 47;
    public static final int L_SQUARE_BRACKET = 91;
    public static final int R_SQUARE_BRACKET = 93;
    public static final int R_CURLY = 125;
    public static final int L_CURLY = 123;
    public static final int HAT = 94;
    public static final int BAR = 124;
    public static final int DOT = 46;
    public static final int EXCLAMATION = 33;
    public static final int LPAREN = 40;
    public static final int RPAREN = 41;
    public static final int GREATER_THAN = 62;
    public static final int LESS_THAN = 60;
    public static final int PERCENT = 37;
    public static final int QUESTION = 63;
    public static final int AND = 38;
    public static final int UNDERSCORE = 95;
    protected static final Hashtable globalSymbolTable = new Hashtable();
    protected static final Hashtable lexerTables = new Hashtable();
    protected Hashtable currentLexer;
    protected String currentLexerName;
    protected Token currentMatch;
    static final char ALPHA_VALID_CHARS = 65535;
    static final char DIGIT_VALID_CHARS = 65534;
    static final char ALPHADIGIT_VALID_CHARS = 65533;

    /* JADX INFO: Access modifiers changed from: protected */
    public void addKeyword(String name, int value) {
        Integer val = Integer.valueOf(value);
        this.currentLexer.put(name, val);
        if (!globalSymbolTable.containsKey(val)) {
            globalSymbolTable.put(val, name);
        }
    }

    public String lookupToken(int value) {
        if (value > 2048) {
            return (String) globalSymbolTable.get(Integer.valueOf(value));
        }
        Character ch = Character.valueOf((char) value);
        return ch.toString();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Hashtable addLexer(String lexerName) {
        this.currentLexer = (Hashtable) lexerTables.get(lexerName);
        if (this.currentLexer == null) {
            this.currentLexer = new Hashtable();
            lexerTables.put(lexerName, this.currentLexer);
        }
        return this.currentLexer;
    }

    public void selectLexer(String lexerName) {
        this.currentLexerName = lexerName;
    }

    protected LexerCore() {
        this.currentLexer = new Hashtable();
        this.currentLexerName = "charLexer";
    }

    public LexerCore(String lexerName, String buffer) {
        super(buffer);
        this.currentLexerName = lexerName;
    }

    public String peekNextId() {
        int oldPtr = this.ptr;
        String retval = ttoken();
        this.savedPtr = this.ptr;
        this.ptr = oldPtr;
        return retval;
    }

    public String getNextId() {
        return ttoken();
    }

    public Token getNextToken() {
        return this.currentMatch;
    }

    public Token peekNextToken() throws ParseException {
        return peekNextToken(1)[0];
    }

    public Token[] peekNextToken(int ntokens) throws ParseException {
        int old = this.ptr;
        Token[] retval = new Token[ntokens];
        for (int i = 0; i < ntokens; i++) {
            Token tok = new Token();
            if (startsId()) {
                String id = ttoken();
                tok.tokenValue = id;
                String idUppercase = id.toUpperCase();
                if (this.currentLexer.containsKey(idUppercase)) {
                    Integer type = (Integer) this.currentLexer.get(idUppercase);
                    tok.tokenType = type.intValue();
                } else {
                    tok.tokenType = 4095;
                }
            } else {
                char nextChar = getNextChar();
                tok.tokenValue = String.valueOf(nextChar);
                if (isAlpha(nextChar)) {
                    tok.tokenType = 4099;
                } else if (isDigit(nextChar)) {
                    tok.tokenType = 4098;
                } else {
                    tok.tokenType = nextChar;
                }
            }
            retval[i] = tok;
        }
        this.savedPtr = this.ptr;
        this.ptr = old;
        return retval;
    }

    public Token match(int tok) throws ParseException {
        if (Debug.parserDebug) {
            Debug.println("match " + tok);
        }
        if (tok > 2048 && tok < 4096) {
            if (tok == 4095) {
                if (!startsId()) {
                    throw new ParseException(this.buffer + "\nID expected", this.ptr);
                }
                String id = getNextId();
                this.currentMatch = new Token();
                this.currentMatch.tokenValue = id;
                this.currentMatch.tokenType = 4095;
            } else if (tok == 4094) {
                if (!startsSafeToken()) {
                    throw new ParseException(this.buffer + "\nID expected", this.ptr);
                }
                String id2 = ttokenSafe();
                this.currentMatch = new Token();
                this.currentMatch.tokenValue = id2;
                this.currentMatch.tokenType = SAFE;
            } else {
                String nexttok = getNextId();
                Integer cur = (Integer) this.currentLexer.get(nexttok.toUpperCase());
                if (cur == null || cur.intValue() != tok) {
                    throw new ParseException(this.buffer + "\nUnexpected Token : " + nexttok, this.ptr);
                }
                this.currentMatch = new Token();
                this.currentMatch.tokenValue = nexttok;
                this.currentMatch.tokenType = tok;
            }
        } else if (tok > 4096) {
            char next = lookAhead(0);
            if (tok == 4098) {
                if (!isDigit(next)) {
                    throw new ParseException(this.buffer + "\nExpecting DIGIT", this.ptr);
                }
                this.currentMatch = new Token();
                this.currentMatch.tokenValue = String.valueOf(next);
                this.currentMatch.tokenType = tok;
                consume(1);
            } else if (tok == 4099) {
                if (!isAlpha(next)) {
                    throw new ParseException(this.buffer + "\nExpecting ALPHA", this.ptr);
                }
                this.currentMatch = new Token();
                this.currentMatch.tokenValue = String.valueOf(next);
                this.currentMatch.tokenType = tok;
                consume(1);
            }
        } else {
            char ch = (char) tok;
            char next2 = lookAhead(0);
            if (next2 == ch) {
                consume(1);
            } else {
                throw new ParseException(this.buffer + "\nExpecting  >>>" + ch + "<<< got >>>" + next2 + "<<<", this.ptr);
            }
        }
        return this.currentMatch;
    }

    public void SPorHT() {
        try {
            char c = lookAhead(0);
            while (true) {
                if (c == ' ' || c == '\t') {
                    consume(1);
                    c = lookAhead(0);
                } else {
                    return;
                }
            }
        } catch (ParseException e) {
        }
    }

    public static final boolean isTokenChar(char c) {
        if (isAlphaDigit(c)) {
            return true;
        }
        switch (c) {
            case '!':
            case '%':
            case '\'':
            case '*':
            case '+':
            case '-':
            case '.':
            case '_':
            case '`':
            case '~':
                return true;
            default:
                return false;
        }
    }

    public boolean startsId() {
        try {
            char nextChar = lookAhead(0);
            return isTokenChar(nextChar);
        } catch (ParseException e) {
            return false;
        }
    }

    public boolean startsSafeToken() {
        try {
            char nextChar = lookAhead(0);
            if (isAlphaDigit(nextChar)) {
                return true;
            }
            switch (nextChar) {
                case '!':
                case '\"':
                case '#':
                case '$':
                case '%':
                case '\'':
                case '*':
                case '+':
                case '-':
                case '.':
                case '/':
                case ':':
                case ';':
                case '=':
                case '?':
                case '@':
                case '[':
                case ']':
                case '^':
                case '_':
                case '`':
                case '{':
                case '|':
                case '}':
                case '~':
                    return true;
                case '&':
                case '(':
                case ')':
                case ',':
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case '<':
                case '>':
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                case 'G':
                case 'H':
                case 'I':
                case 'J':
                case 'K':
                case 'L':
                case 'M':
                case 'N':
                case 'O':
                case 'P':
                case 'Q':
                case 'R':
                case 'S':
                case 'T':
                case 'U':
                case 'V':
                case 'W':
                case 'X':
                case 'Y':
                case 'Z':
                case '\\':
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'g':
                case 'h':
                case 'i':
                case 'j':
                case 'k':
                case 'l':
                case 'm':
                case 'n':
                case 'o':
                case 'p':
                case 'q':
                case 'r':
                case 's':
                case 't':
                case 'u':
                case 'v':
                case 'w':
                case 'x':
                case 'y':
                case 'z':
                default:
                    return false;
            }
        } catch (ParseException e) {
            return false;
        }
    }

    public String ttoken() {
        int startIdx = this.ptr;
        while (hasMoreChars()) {
            try {
                char nextChar = lookAhead(0);
                if (!isTokenChar(nextChar)) {
                    break;
                }
                consume(1);
            } catch (ParseException e) {
                return null;
            }
        }
        return this.buffer.substring(startIdx, this.ptr);
    }

    public String ttokenSafe() {
        int startIdx = this.ptr;
        while (hasMoreChars()) {
            try {
                char nextChar = lookAhead(0);
                if (isAlphaDigit(nextChar)) {
                    consume(1);
                } else {
                    boolean isValidChar = false;
                    switch (nextChar) {
                        case '!':
                        case '\"':
                        case '#':
                        case '$':
                        case '%':
                        case '\'':
                        case '*':
                        case '+':
                        case '-':
                        case '.':
                        case '/':
                        case ':':
                        case ';':
                        case '?':
                        case '@':
                        case '[':
                        case ']':
                        case '^':
                        case '_':
                        case '`':
                        case '{':
                        case '|':
                        case '}':
                        case '~':
                            isValidChar = true;
                            break;
                    }
                    if (isValidChar) {
                        consume(1);
                    } else {
                        return this.buffer.substring(startIdx, this.ptr);
                    }
                }
            } catch (ParseException e) {
                return null;
            }
        }
        return this.buffer.substring(startIdx, this.ptr);
    }

    /* JADX WARN: Removed duplicated region for block: B:24:0x007b A[Catch: ParseException -> 0x0086, LOOP:0: B:28:0x0003->B:24:0x007b, LOOP_END, TryCatch #0 {ParseException -> 0x0086, blocks: (B:3:0x0003, B:5:0x000a, B:8:0x001c, B:9:0x0024, B:10:0x0040, B:21:0x0070, B:11:0x0049, B:12:0x0052, B:24:0x007b), top: B:28:0x0003 }] */
    /* JADX WARN: Removed duplicated region for block: B:31:0x0083 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void consumeValidChars(char[] r4) {
        /*
            r3 = this;
            r0 = r4
            int r0 = r0.length
            r5 = r0
        L3:
            r0 = r3
            boolean r0 = r0.hasMoreChars()     // Catch: java.text.ParseException -> L86
            if (r0 == 0) goto L83
            r0 = r3
            r1 = 0
            char r0 = r0.lookAhead(r1)     // Catch: java.text.ParseException -> L86
            r6 = r0
            r0 = 0
            r7 = r0
            r0 = 0
            r8 = r0
        L16:
            r0 = r8
            r1 = r5
            if (r0 >= r1) goto L76
            r0 = r4
            r1 = r8
            char r0 = r0[r1]     // Catch: java.text.ParseException -> L86
            r9 = r0
            r0 = r9
            switch(r0) {
                case 65533: goto L52;
                case 65534: goto L49;
                case 65535: goto L40;
                default: goto L5b;
            }     // Catch: java.text.ParseException -> L86
        L40:
            r0 = r6
            boolean r0 = isAlpha(r0)     // Catch: java.text.ParseException -> L86
            r7 = r0
            goto L68
        L49:
            r0 = r6
            boolean r0 = isDigit(r0)     // Catch: java.text.ParseException -> L86
            r7 = r0
            goto L68
        L52:
            r0 = r6
            boolean r0 = isAlphaDigit(r0)     // Catch: java.text.ParseException -> L86
            r7 = r0
            goto L68
        L5b:
            r0 = r6
            r1 = r9
            if (r0 != r1) goto L65
            r0 = 1
            goto L66
        L65:
            r0 = 0
        L66:
            r7 = r0
        L68:
            r0 = r7
            if (r0 == 0) goto L70
            goto L76
        L70:
            int r8 = r8 + 1
            goto L16
        L76:
            r0 = r7
            if (r0 == 0) goto L83
            r0 = r3
            r1 = 1
            r0.consume(r1)     // Catch: java.text.ParseException -> L86
            goto L3
        L83:
            goto L87
        L86:
            r6 = move-exception
        L87:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.core.LexerCore.consumeValidChars(char[]):void");
    }

    public String quotedString() throws ParseException {
        int startIdx = this.ptr + 1;
        if (lookAhead(0) != '\"') {
            return null;
        }
        consume(1);
        while (true) {
            char next = getNextChar();
            if (next != '\"') {
                if (next == 0) {
                    throw new ParseException(this.buffer + " :unexpected EOL", this.ptr);
                }
                if (next == '\\') {
                    consume(1);
                }
            } else {
                return this.buffer.substring(startIdx, this.ptr - 1);
            }
        }
    }

    public String comment() throws ParseException {
        StringBuffer retval = new StringBuffer();
        if (lookAhead(0) != '(') {
            return null;
        }
        consume(1);
        while (true) {
            char next = getNextChar();
            if (next != ')') {
                if (next == 0) {
                    throw new ParseException(this.buffer + " :unexpected EOL", this.ptr);
                }
                if (next == '\\') {
                    retval.append(next);
                    char next2 = getNextChar();
                    if (next2 == 0) {
                        throw new ParseException(this.buffer + " : unexpected EOL", this.ptr);
                    }
                    retval.append(next2);
                } else {
                    retval.append(next);
                }
            } else {
                return retval.toString();
            }
        }
    }

    public String byteStringNoSemicolon() {
        StringBuffer retval = new StringBuffer();
        while (true) {
            try {
                char next = lookAhead(0);
                if (next == 0 || next == '\n' || next == ';' || next == ',') {
                    break;
                }
                consume(1);
                retval.append(next);
            } catch (ParseException e) {
                return retval.toString();
            }
        }
        return retval.toString();
    }

    public String byteStringNoSlash() {
        StringBuffer retval = new StringBuffer();
        while (true) {
            try {
                char next = lookAhead(0);
                if (next == 0 || next == '\n' || next == '/') {
                    break;
                }
                consume(1);
                retval.append(next);
            } catch (ParseException e) {
                return retval.toString();
            }
        }
        return retval.toString();
    }

    public String byteStringNoComma() {
        StringBuffer retval = new StringBuffer();
        while (true) {
            try {
                char next = lookAhead(0);
                if (next == '\n' || next == ',') {
                    break;
                }
                consume(1);
                retval.append(next);
            } catch (ParseException e) {
            }
        }
        return retval.toString();
    }

    public static String charAsString(char ch) {
        return String.valueOf(ch);
    }

    public String charAsString(int nchars) {
        return this.buffer.substring(this.ptr, this.ptr + nchars);
    }

    /* JADX WARN: Incorrect condition in loop: B:9:0x0049 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public java.lang.String number() throws java.text.ParseException {
        /*
            r6 = this;
            r0 = r6
            int r0 = r0.ptr
            r7 = r0
            r0 = r6
            r1 = 0
            char r0 = r0.lookAhead(r1)     // Catch: java.text.ParseException -> L61
            boolean r0 = isDigit(r0)     // Catch: java.text.ParseException -> L61
            if (r0 != 0) goto L3a
            java.text.ParseException r0 = new java.text.ParseException     // Catch: java.text.ParseException -> L61
            r1 = r0
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch: java.text.ParseException -> L61
            r3 = r2
            r3.<init>()     // Catch: java.text.ParseException -> L61
            r3 = r6
            java.lang.String r3 = r3.buffer     // Catch: java.text.ParseException -> L61
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch: java.text.ParseException -> L61
            java.lang.String r3 = ": Unexpected token at "
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch: java.text.ParseException -> L61
            r3 = r6
            r4 = 0
            char r3 = r3.lookAhead(r4)     // Catch: java.text.ParseException -> L61
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch: java.text.ParseException -> L61
            java.lang.String r2 = r2.toString()     // Catch: java.text.ParseException -> L61
            r3 = r6
            int r3 = r3.ptr     // Catch: java.text.ParseException -> L61
            r1.<init>(r2, r3)     // Catch: java.text.ParseException -> L61
            throw r0     // Catch: java.text.ParseException -> L61
        L3a:
            r0 = r6
            r1 = 1
            r0.consume(r1)     // Catch: java.text.ParseException -> L61
        L3f:
            r0 = r6
            r1 = 0
            char r0 = r0.lookAhead(r1)     // Catch: java.text.ParseException -> L61
            r8 = r0
            r0 = r8
            boolean r0 = isDigit(r0)     // Catch: java.text.ParseException -> L61
            if (r0 == 0) goto L54
            r0 = r6
            r1 = 1
            r0.consume(r1)     // Catch: java.text.ParseException -> L61
            goto L3f
        L54:
            r0 = r6
            java.lang.String r0 = r0.buffer     // Catch: java.text.ParseException -> L61
            r1 = r7
            r2 = r6
            int r2 = r2.ptr     // Catch: java.text.ParseException -> L61
            java.lang.String r0 = r0.substring(r1, r2)     // Catch: java.text.ParseException -> L61
            return r0
        L61:
            r8 = move-exception
            r0 = r6
            java.lang.String r0 = r0.buffer
            r1 = r7
            r2 = r6
            int r2 = r2.ptr
            java.lang.String r0 = r0.substring(r1, r2)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.core.LexerCore.number():java.lang.String");
    }

    public int markInputPosition() {
        return this.ptr;
    }

    public void rewindInputPosition(int position) {
        this.ptr = position;
    }

    public String getRest() {
        if (this.ptr >= this.buffer.length()) {
            return null;
        }
        return this.buffer.substring(this.ptr);
    }

    public String getString(char c) throws ParseException {
        StringBuffer retval = new StringBuffer();
        while (true) {
            char next = lookAhead(0);
            if (next == 0) {
                throw new ParseException(this.buffer + "unexpected EOL", this.ptr);
            }
            if (next == c) {
                consume(1);
                return retval.toString();
            } else if (next == '\\') {
                consume(1);
                char nextchar = lookAhead(0);
                if (nextchar == 0) {
                    throw new ParseException(this.buffer + "unexpected EOL", this.ptr);
                }
                consume(1);
                retval.append(nextchar);
            } else {
                consume(1);
                retval.append(next);
            }
        }
    }

    public int getPtr() {
        return this.ptr;
    }

    public String getBuffer() {
        return this.buffer;
    }

    public ParseException createParseException() {
        return new ParseException(this.buffer, this.ptr);
    }
}