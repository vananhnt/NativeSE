package android.widget;

import android.graphics.Rect;
import android.text.Layout;
import android.text.Spannable;
import android.view.AccessibilityIterators;

/* loaded from: AccessibilityIterators.class */
final class AccessibilityIterators {
    AccessibilityIterators() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: AccessibilityIterators$LineTextSegmentIterator.class */
    public static class LineTextSegmentIterator extends AccessibilityIterators.AbstractTextSegmentIterator {
        private static LineTextSegmentIterator sLineInstance;
        protected static final int DIRECTION_START = -1;
        protected static final int DIRECTION_END = 1;
        protected Layout mLayout;

        LineTextSegmentIterator() {
        }

        public static LineTextSegmentIterator getInstance() {
            if (sLineInstance == null) {
                sLineInstance = new LineTextSegmentIterator();
            }
            return sLineInstance;
        }

        public void initialize(Spannable text, Layout layout) {
            this.mText = text.toString();
            this.mLayout = layout;
        }

        @Override // android.view.AccessibilityIterators.TextSegmentIterator
        public int[] following(int offset) {
            int nextLine;
            int textLegth = this.mText.length();
            if (textLegth <= 0 || offset >= this.mText.length()) {
                return null;
            }
            if (offset < 0) {
                nextLine = this.mLayout.getLineForOffset(0);
            } else {
                int currentLine = this.mLayout.getLineForOffset(offset);
                if (getLineEdgeIndex(currentLine, -1) == offset) {
                    nextLine = currentLine;
                } else {
                    nextLine = currentLine + 1;
                }
            }
            if (nextLine >= this.mLayout.getLineCount()) {
                return null;
            }
            int start = getLineEdgeIndex(nextLine, -1);
            int end = getLineEdgeIndex(nextLine, 1) + 1;
            return getRange(start, end);
        }

        @Override // android.view.AccessibilityIterators.TextSegmentIterator
        public int[] preceding(int offset) {
            int previousLine;
            int textLegth = this.mText.length();
            if (textLegth <= 0 || offset <= 0) {
                return null;
            }
            if (offset > this.mText.length()) {
                previousLine = this.mLayout.getLineForOffset(this.mText.length());
            } else {
                int currentLine = this.mLayout.getLineForOffset(offset);
                if (getLineEdgeIndex(currentLine, 1) + 1 == offset) {
                    previousLine = currentLine;
                } else {
                    previousLine = currentLine - 1;
                }
            }
            if (previousLine < 0) {
                return null;
            }
            int start = getLineEdgeIndex(previousLine, -1);
            int end = getLineEdgeIndex(previousLine, 1) + 1;
            return getRange(start, end);
        }

        protected int getLineEdgeIndex(int lineNumber, int direction) {
            int paragraphDirection = this.mLayout.getParagraphDirection(lineNumber);
            if (direction * paragraphDirection < 0) {
                return this.mLayout.getLineStart(lineNumber);
            }
            return this.mLayout.getLineEnd(lineNumber) - 1;
        }
    }

    /* loaded from: AccessibilityIterators$PageTextSegmentIterator.class */
    static class PageTextSegmentIterator extends LineTextSegmentIterator {
        private static PageTextSegmentIterator sPageInstance;
        private TextView mView;
        private final Rect mTempRect = new Rect();

        PageTextSegmentIterator() {
        }

        public static PageTextSegmentIterator getInstance() {
            if (sPageInstance == null) {
                sPageInstance = new PageTextSegmentIterator();
            }
            return sPageInstance;
        }

        public void initialize(TextView view) {
            super.initialize((Spannable) view.getIterableTextForAccessibility(), view.getLayout());
            this.mView = view;
        }

        @Override // android.widget.AccessibilityIterators.LineTextSegmentIterator, android.view.AccessibilityIterators.TextSegmentIterator
        public int[] following(int offset) {
            int textLegth = this.mText.length();
            if (textLegth <= 0 || offset >= this.mText.length() || !this.mView.getGlobalVisibleRect(this.mTempRect)) {
                return null;
            }
            int start = Math.max(0, offset);
            int currentLine = this.mLayout.getLineForOffset(start);
            int currentLineTop = this.mLayout.getLineTop(currentLine);
            int pageHeight = (this.mTempRect.height() - this.mView.getTotalPaddingTop()) - this.mView.getTotalPaddingBottom();
            int nextPageStartY = currentLineTop + pageHeight;
            int lastLineTop = this.mLayout.getLineTop(this.mLayout.getLineCount() - 1);
            int currentPageEndLine = nextPageStartY < lastLineTop ? this.mLayout.getLineForVertical(nextPageStartY) - 1 : this.mLayout.getLineCount() - 1;
            int end = getLineEdgeIndex(currentPageEndLine, 1) + 1;
            return getRange(start, end);
        }

        @Override // android.widget.AccessibilityIterators.LineTextSegmentIterator, android.view.AccessibilityIterators.TextSegmentIterator
        public int[] preceding(int offset) {
            int textLegth = this.mText.length();
            if (textLegth <= 0 || offset <= 0 || !this.mView.getGlobalVisibleRect(this.mTempRect)) {
                return null;
            }
            int end = Math.min(this.mText.length(), offset);
            int currentLine = this.mLayout.getLineForOffset(end);
            int currentLineTop = this.mLayout.getLineTop(currentLine);
            int pageHeight = (this.mTempRect.height() - this.mView.getTotalPaddingTop()) - this.mView.getTotalPaddingBottom();
            int previousPageEndY = currentLineTop - pageHeight;
            int currentPageStartLine = previousPageEndY > 0 ? this.mLayout.getLineForVertical(previousPageEndY) + 1 : 0;
            int start = getLineEdgeIndex(currentPageStartLine, -1);
            return getRange(start, end);
        }
    }
}