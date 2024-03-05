package android.text.style;

/* loaded from: TabStopSpan.class */
public interface TabStopSpan extends ParagraphStyle {
    int getTabStop();

    /* loaded from: TabStopSpan$Standard.class */
    public static class Standard implements TabStopSpan {
        private int mTab;

        public Standard(int where) {
            this.mTab = where;
        }

        @Override // android.text.style.TabStopSpan
        public int getTabStop() {
            return this.mTab;
        }
    }
}