package gov.nist.javax.sip.header;

import java.util.Arrays;

/* loaded from: Indentation.class */
class Indentation {
    private int indentation;

    protected Indentation() {
        this.indentation = 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Indentation(int initval) {
        this.indentation = initval;
    }

    protected void setIndentation(int initval) {
        this.indentation = initval;
    }

    protected int getCount() {
        return this.indentation;
    }

    protected void increment() {
        this.indentation++;
    }

    protected void decrement() {
        this.indentation--;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getIndentation() {
        char[] chars = new char[this.indentation];
        Arrays.fill(chars, ' ');
        return new String(chars);
    }
}