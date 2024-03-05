package java.text;

import gov.nist.core.Separators;
import libcore.icu.NativeBreakIterator;

/* loaded from: RuleBasedBreakIterator.class */
class RuleBasedBreakIterator extends BreakIterator {
    RuleBasedBreakIterator(NativeBreakIterator iterator) {
        super(iterator);
    }

    @Override // java.text.BreakIterator
    public int current() {
        return this.wrapped.current();
    }

    @Override // java.text.BreakIterator
    public int first() {
        return this.wrapped.first();
    }

    @Override // java.text.BreakIterator
    public int following(int offset) {
        checkOffset(offset);
        return this.wrapped.following(offset);
    }

    private void checkOffset(int offset) {
        if (!this.wrapped.hasText()) {
            throw new IllegalArgumentException("BreakIterator has no text");
        }
        CharacterIterator it = this.wrapped.getText();
        if (offset < it.getBeginIndex() || offset > it.getEndIndex()) {
            String message = "Valid range is [" + it.getBeginIndex() + Separators.SP + it.getEndIndex() + "]";
            throw new IllegalArgumentException(message);
        }
    }

    @Override // java.text.BreakIterator
    public CharacterIterator getText() {
        return this.wrapped.getText();
    }

    @Override // java.text.BreakIterator
    public int last() {
        return this.wrapped.last();
    }

    @Override // java.text.BreakIterator
    public int next() {
        return this.wrapped.next();
    }

    @Override // java.text.BreakIterator
    public int next(int n) {
        return this.wrapped.next(n);
    }

    @Override // java.text.BreakIterator
    public int previous() {
        return this.wrapped.previous();
    }

    @Override // java.text.BreakIterator
    public void setText(CharacterIterator newText) {
        if (newText == null) {
            throw new NullPointerException("newText == null");
        }
        newText.current();
        this.wrapped.setText(newText);
    }

    @Override // java.text.BreakIterator
    public boolean isBoundary(int offset) {
        checkOffset(offset);
        return this.wrapped.isBoundary(offset);
    }

    @Override // java.text.BreakIterator
    public int preceding(int offset) {
        checkOffset(offset);
        return this.wrapped.preceding(offset);
    }

    public boolean equals(Object o) {
        if (!(o instanceof RuleBasedBreakIterator)) {
            return false;
        }
        return this.wrapped.equals(((RuleBasedBreakIterator) o).wrapped);
    }

    public String toString() {
        return this.wrapped.toString();
    }

    public int hashCode() {
        return this.wrapped.hashCode();
    }

    @Override // java.text.BreakIterator
    public Object clone() {
        RuleBasedBreakIterator cloned = (RuleBasedBreakIterator) super.clone();
        cloned.wrapped = (NativeBreakIterator) this.wrapped.clone();
        return cloned;
    }
}