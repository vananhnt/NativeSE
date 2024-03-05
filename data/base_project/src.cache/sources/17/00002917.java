package libcore.reflect;

import gov.nist.core.Separators;
import java.lang.reflect.Constructor;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.GenericSignatureFormatError;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import libcore.util.EmptyArray;

/* loaded from: GenericSignatureParser.class */
public final class GenericSignatureParser {
    public ListOfTypes exceptionTypes;
    public ListOfTypes parameterTypes;
    public TypeVariable[] formalTypeParameters;
    public Type returnType;
    public Type fieldType;
    public ListOfTypes interfaceTypes;
    public Type superclassType;
    public ClassLoader loader;
    GenericDeclaration genericDecl;
    char symbol;
    String identifier;
    private boolean eof;
    char[] buffer;
    int pos;

    public GenericSignatureParser(ClassLoader loader) {
        this.loader = loader;
    }

    void setInput(GenericDeclaration genericDecl, String input) {
        if (input != null) {
            this.genericDecl = genericDecl;
            this.buffer = input.toCharArray();
            this.eof = false;
            scanSymbol();
            return;
        }
        this.eof = true;
    }

    public void parseForClass(GenericDeclaration genericDecl, String signature) {
        setInput(genericDecl, signature);
        if (!this.eof) {
            parseClassSignature();
        } else if (genericDecl instanceof Class) {
            Class c = (Class) genericDecl;
            this.formalTypeParameters = EmptyArray.TYPE_VARIABLE;
            this.superclassType = c.getSuperclass();
            Class<?>[] interfaces = c.getInterfaces();
            if (interfaces.length == 0) {
                this.interfaceTypes = ListOfTypes.EMPTY;
            } else {
                this.interfaceTypes = new ListOfTypes(interfaces);
            }
        } else {
            this.formalTypeParameters = EmptyArray.TYPE_VARIABLE;
            this.superclassType = Object.class;
            this.interfaceTypes = ListOfTypes.EMPTY;
        }
    }

    public void parseForMethod(GenericDeclaration genericDecl, String signature, Class<?>[] rawExceptionTypes) {
        setInput(genericDecl, signature);
        if (!this.eof) {
            parseMethodTypeSignature(rawExceptionTypes);
            return;
        }
        Method m = (Method) genericDecl;
        this.formalTypeParameters = EmptyArray.TYPE_VARIABLE;
        Class<?>[] parameterTypes = m.getParameterTypes();
        if (parameterTypes.length == 0) {
            this.parameterTypes = ListOfTypes.EMPTY;
        } else {
            this.parameterTypes = new ListOfTypes(parameterTypes);
        }
        Class<?>[] exceptionTypes = m.getExceptionTypes();
        if (exceptionTypes.length == 0) {
            this.exceptionTypes = ListOfTypes.EMPTY;
        } else {
            this.exceptionTypes = new ListOfTypes(exceptionTypes);
        }
        this.returnType = m.getReturnType();
    }

    public void parseForConstructor(GenericDeclaration genericDecl, String signature, Class<?>[] rawExceptionTypes) {
        setInput(genericDecl, signature);
        if (!this.eof) {
            parseMethodTypeSignature(rawExceptionTypes);
            return;
        }
        Constructor c = (Constructor) genericDecl;
        this.formalTypeParameters = EmptyArray.TYPE_VARIABLE;
        Class<?>[] parameterTypes = c.getParameterTypes();
        if (parameterTypes.length == 0) {
            this.parameterTypes = ListOfTypes.EMPTY;
        } else {
            this.parameterTypes = new ListOfTypes(parameterTypes);
        }
        Class<?>[] exceptionTypes = c.getExceptionTypes();
        if (exceptionTypes.length == 0) {
            this.exceptionTypes = ListOfTypes.EMPTY;
        } else {
            this.exceptionTypes = new ListOfTypes(exceptionTypes);
        }
    }

    public void parseForField(GenericDeclaration genericDecl, String signature) {
        setInput(genericDecl, signature);
        if (!this.eof) {
            this.fieldType = parseFieldTypeSignature();
        }
    }

    void parseClassSignature() {
        parseOptFormalTypeParameters();
        this.superclassType = parseClassTypeSignature();
        this.interfaceTypes = new ListOfTypes(16);
        while (this.symbol > 0) {
            this.interfaceTypes.add(parseClassTypeSignature());
        }
    }

    void parseOptFormalTypeParameters() {
        ListOfVariables typeParams = new ListOfVariables();
        if (this.symbol == '<') {
            scanSymbol();
            typeParams.add(parseFormalTypeParameter());
            while (this.symbol != '>' && this.symbol > 0) {
                typeParams.add(parseFormalTypeParameter());
            }
            expect('>');
        }
        this.formalTypeParameters = typeParams.getArray();
    }

    TypeVariableImpl<GenericDeclaration> parseFormalTypeParameter() {
        scanIdentifier();
        String name = this.identifier.intern();
        ListOfTypes bounds = new ListOfTypes(8);
        expect(':');
        if (this.symbol == 'L' || this.symbol == '[' || this.symbol == 'T') {
            bounds.add(parseFieldTypeSignature());
        }
        while (this.symbol == ':') {
            scanSymbol();
            bounds.add(parseFieldTypeSignature());
        }
        return new TypeVariableImpl<>(this.genericDecl, name, bounds);
    }

    Type parseFieldTypeSignature() {
        switch (this.symbol) {
            case 'L':
                return parseClassTypeSignature();
            case 'T':
                return parseTypeVariableSignature();
            case '[':
                scanSymbol();
                return new GenericArrayTypeImpl(parseTypeSignature());
            default:
                throw new GenericSignatureFormatError();
        }
    }

    Type parseClassTypeSignature() {
        expect('L');
        StringBuilder qualIdent = new StringBuilder();
        scanIdentifier();
        while (this.symbol == '/') {
            scanSymbol();
            qualIdent.append(this.identifier).append(Separators.DOT);
            scanIdentifier();
        }
        qualIdent.append(this.identifier);
        ListOfTypes typeArgs = parseOptTypeArguments();
        ParameterizedTypeImpl parentType = new ParameterizedTypeImpl(null, qualIdent.toString(), typeArgs, this.loader);
        ParameterizedTypeImpl parameterizedTypeImpl = parentType;
        while (true) {
            ParameterizedTypeImpl type = parameterizedTypeImpl;
            if (this.symbol == '.') {
                scanSymbol();
                scanIdentifier();
                qualIdent.append("$").append(this.identifier);
                ListOfTypes typeArgs2 = parseOptTypeArguments();
                parameterizedTypeImpl = new ParameterizedTypeImpl(parentType, qualIdent.toString(), typeArgs2, this.loader);
            } else {
                expect(';');
                return type;
            }
        }
    }

    ListOfTypes parseOptTypeArguments() {
        ListOfTypes typeArgs = new ListOfTypes(8);
        if (this.symbol == '<') {
            scanSymbol();
            typeArgs.add(parseTypeArgument());
            while (this.symbol != '>' && this.symbol > 0) {
                typeArgs.add(parseTypeArgument());
            }
            expect('>');
        }
        return typeArgs;
    }

    Type parseTypeArgument() {
        ListOfTypes extendsBound = new ListOfTypes(1);
        ListOfTypes superBound = new ListOfTypes(1);
        if (this.symbol == '*') {
            scanSymbol();
            extendsBound.add(Object.class);
            return new WildcardTypeImpl(extendsBound, superBound);
        } else if (this.symbol == '+') {
            scanSymbol();
            extendsBound.add(parseFieldTypeSignature());
            return new WildcardTypeImpl(extendsBound, superBound);
        } else if (this.symbol == '-') {
            scanSymbol();
            superBound.add(parseFieldTypeSignature());
            extendsBound.add(Object.class);
            return new WildcardTypeImpl(extendsBound, superBound);
        } else {
            return parseFieldTypeSignature();
        }
    }

    TypeVariableImpl<GenericDeclaration> parseTypeVariableSignature() {
        expect('T');
        scanIdentifier();
        expect(';');
        return new TypeVariableImpl<>(this.genericDecl, this.identifier);
    }

    Type parseTypeSignature() {
        switch (this.symbol) {
            case 'B':
                scanSymbol();
                return Byte.TYPE;
            case 'C':
                scanSymbol();
                return Character.TYPE;
            case 'D':
                scanSymbol();
                return Double.TYPE;
            case 'E':
            case 'G':
            case 'H':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            default:
                return parseFieldTypeSignature();
            case 'F':
                scanSymbol();
                return Float.TYPE;
            case 'I':
                scanSymbol();
                return Integer.TYPE;
            case 'J':
                scanSymbol();
                return Long.TYPE;
            case 'S':
                scanSymbol();
                return Short.TYPE;
            case 'Z':
                scanSymbol();
                return Boolean.TYPE;
        }
    }

    void parseMethodTypeSignature(Class<?>[] rawExceptionTypes) {
        parseOptFormalTypeParameters();
        this.parameterTypes = new ListOfTypes(16);
        expect('(');
        while (this.symbol != ')' && this.symbol > 0) {
            this.parameterTypes.add(parseTypeSignature());
        }
        expect(')');
        this.returnType = parseReturnType();
        if (this.symbol == '^') {
            this.exceptionTypes = new ListOfTypes(8);
            do {
                scanSymbol();
                if (this.symbol == 'T') {
                    this.exceptionTypes.add(parseTypeVariableSignature());
                } else {
                    this.exceptionTypes.add(parseClassTypeSignature());
                }
            } while (this.symbol == '^');
        } else if (rawExceptionTypes != null) {
            this.exceptionTypes = new ListOfTypes(rawExceptionTypes);
        } else {
            this.exceptionTypes = new ListOfTypes(0);
        }
    }

    Type parseReturnType() {
        if (this.symbol != 'V') {
            return parseTypeSignature();
        }
        scanSymbol();
        return Void.TYPE;
    }

    void scanSymbol() {
        if (!this.eof) {
            if (this.pos < this.buffer.length) {
                this.symbol = this.buffer[this.pos];
                this.pos++;
                return;
            }
            this.symbol = (char) 0;
            this.eof = true;
            return;
        }
        throw new GenericSignatureFormatError();
    }

    void expect(char c) {
        if (this.symbol == c) {
            scanSymbol();
            return;
        }
        throw new GenericSignatureFormatError();
    }

    static boolean isStopSymbol(char ch) {
        switch (ch) {
            case '.':
            case '/':
            case ':':
            case ';':
            case '<':
                return true;
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
            default:
                return false;
        }
    }

    void scanIdentifier() {
        if (!this.eof) {
            StringBuilder identBuf = new StringBuilder(32);
            if (!isStopSymbol(this.symbol)) {
                identBuf.append(this.symbol);
                do {
                    char ch = this.buffer[this.pos];
                    if ((ch >= 'a' && ch <= 'z') || ((ch >= 'A' && ch <= 'Z') || !isStopSymbol(ch))) {
                        identBuf.append(ch);
                        this.pos++;
                    } else {
                        this.identifier = identBuf.toString();
                        scanSymbol();
                        return;
                    }
                } while (this.pos != this.buffer.length);
                this.identifier = identBuf.toString();
                this.symbol = (char) 0;
                this.eof = true;
                return;
            }
            this.symbol = (char) 0;
            this.eof = true;
            throw new GenericSignatureFormatError();
        }
        throw new GenericSignatureFormatError();
    }
}