package gov.nist.javax.sip.parser;

import gov.nist.core.NameValue;
import gov.nist.javax.sip.header.ParametersHeader;
import java.text.ParseException;

/* loaded from: ParametersParser.class */
public abstract class ParametersParser extends HeaderParser {
    /* JADX INFO: Access modifiers changed from: protected */
    public ParametersParser(Lexer lexer) {
        super(lexer);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ParametersParser(String buffer) {
        super(buffer);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void parse(ParametersHeader parametersHeader) throws ParseException {
        this.lexer.SPorHT();
        while (this.lexer.lookAhead(0) == ';') {
            this.lexer.consume(1);
            this.lexer.SPorHT();
            NameValue nv = nameValue();
            parametersHeader.setParameter(nv);
            this.lexer.SPorHT();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void parseNameValueList(ParametersHeader parametersHeader) throws ParseException {
        parametersHeader.removeParameters();
        while (true) {
            this.lexer.SPorHT();
            NameValue nv = nameValue();
            parametersHeader.setParameter(nv.getName(), (String) nv.getValueAsObject());
            this.lexer.SPorHT();
            if (this.lexer.lookAhead(0) == ';') {
                this.lexer.consume(1);
            } else {
                return;
            }
        }
    }
}