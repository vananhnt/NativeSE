package android.media;

import android.util.Log;
import gov.nist.core.Separators;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: WebVttRenderer.java */
/* loaded from: Tokenizer.class */
public class Tokenizer {
    private static final String TAG = "Tokenizer";
    private TokenizerPhase mPhase;
    private TokenizerPhase mDataTokenizer = new DataTokenizer();
    private TokenizerPhase mTagTokenizer = new TagTokenizer();
    private OnTokenListener mListener;
    private String mLine;
    private int mHandledLen;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* compiled from: WebVttRenderer.java */
    /* loaded from: Tokenizer$OnTokenListener.class */
    public interface OnTokenListener {
        void onData(String str);

        void onStart(String str, String[] strArr, String str2);

        void onEnd(String str);

        void onTimeStamp(long j);

        void onLineEnd();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* compiled from: WebVttRenderer.java */
    /* loaded from: Tokenizer$TokenizerPhase.class */
    public interface TokenizerPhase {
        TokenizerPhase start();

        void tokenize();
    }

    static /* synthetic */ int access$108(Tokenizer x0) {
        int i = x0.mHandledLen;
        x0.mHandledLen = i + 1;
        return i;
    }

    static /* synthetic */ int access$112(Tokenizer x0, int x1) {
        int i = x0.mHandledLen + x1;
        x0.mHandledLen = i;
        return i;
    }

    /* compiled from: WebVttRenderer.java */
    /* loaded from: Tokenizer$DataTokenizer.class */
    class DataTokenizer implements TokenizerPhase {
        private StringBuilder mData;

        DataTokenizer() {
        }

        @Override // android.media.Tokenizer.TokenizerPhase
        public TokenizerPhase start() {
            this.mData = new StringBuilder();
            return this;
        }

        private boolean replaceEscape(String escape, String replacement, int pos) {
            if (Tokenizer.this.mLine.startsWith(escape, pos)) {
                this.mData.append(Tokenizer.this.mLine.substring(Tokenizer.this.mHandledLen, pos));
                this.mData.append(replacement);
                Tokenizer.this.mHandledLen = pos + escape.length();
                int i = Tokenizer.this.mHandledLen - 1;
                return true;
            }
            return false;
        }

        @Override // android.media.Tokenizer.TokenizerPhase
        public void tokenize() {
            int end = Tokenizer.this.mLine.length();
            int pos = Tokenizer.this.mHandledLen;
            while (true) {
                if (pos >= Tokenizer.this.mLine.length()) {
                    break;
                }
                if (Tokenizer.this.mLine.charAt(pos) != '&') {
                    if (Tokenizer.this.mLine.charAt(pos) == '<') {
                        end = pos;
                        Tokenizer.this.mPhase = Tokenizer.this.mTagTokenizer.start();
                        break;
                    }
                } else if (!replaceEscape("&amp;", Separators.AND, pos) && !replaceEscape("&lt;", Separators.LESS_THAN, pos) && !replaceEscape("&gt;", Separators.GREATER_THAN, pos) && !replaceEscape("&lrm;", "\u200e", pos) && !replaceEscape("&rlm;", "\u200f", pos) && !replaceEscape("&nbsp;", " ", pos)) {
                }
                pos++;
            }
            this.mData.append(Tokenizer.this.mLine.substring(Tokenizer.this.mHandledLen, end));
            Tokenizer.this.mListener.onData(this.mData.toString());
            this.mData.delete(0, this.mData.length());
            Tokenizer.this.mHandledLen = end;
        }
    }

    /* compiled from: WebVttRenderer.java */
    /* loaded from: Tokenizer$TagTokenizer.class */
    class TagTokenizer implements TokenizerPhase {
        private boolean mAtAnnotation;
        private String mName;
        private String mAnnotation;

        TagTokenizer() {
        }

        @Override // android.media.Tokenizer.TokenizerPhase
        public TokenizerPhase start() {
            this.mAnnotation = "";
            this.mName = "";
            this.mAtAnnotation = false;
            return this;
        }

        @Override // android.media.Tokenizer.TokenizerPhase
        public void tokenize() {
            if (!this.mAtAnnotation) {
                Tokenizer.access$108(Tokenizer.this);
            }
            if (Tokenizer.this.mHandledLen < Tokenizer.this.mLine.length()) {
                String[] parts = (this.mAtAnnotation || Tokenizer.this.mLine.charAt(Tokenizer.this.mHandledLen) == '/') ? Tokenizer.this.mLine.substring(Tokenizer.this.mHandledLen).split(Separators.GREATER_THAN) : Tokenizer.this.mLine.substring(Tokenizer.this.mHandledLen).split("[\t\f >]");
                String part = Tokenizer.this.mLine.substring(Tokenizer.this.mHandledLen, Tokenizer.this.mHandledLen + parts[0].length());
                Tokenizer.access$112(Tokenizer.this, parts[0].length());
                if (this.mAtAnnotation) {
                    this.mAnnotation += Separators.SP + part;
                } else {
                    this.mName = part;
                }
            }
            this.mAtAnnotation = true;
            if (Tokenizer.this.mHandledLen < Tokenizer.this.mLine.length() && Tokenizer.this.mLine.charAt(Tokenizer.this.mHandledLen) == '>') {
                yield_tag();
                Tokenizer.this.mPhase = Tokenizer.this.mDataTokenizer.start();
                Tokenizer.access$108(Tokenizer.this);
            }
        }

        private void yield_tag() {
            if (this.mName.startsWith(Separators.SLASH)) {
                Tokenizer.this.mListener.onEnd(this.mName.substring(1));
            } else if (this.mName.length() > 0 && Character.isDigit(this.mName.charAt(0))) {
                try {
                    long timestampMs = WebVttParser.parseTimestampMs(this.mName);
                    Tokenizer.this.mListener.onTimeStamp(timestampMs);
                } catch (NumberFormatException e) {
                    Log.d(Tokenizer.TAG, "invalid timestamp tag: <" + this.mName + Separators.GREATER_THAN);
                }
            } else {
                this.mAnnotation = this.mAnnotation.replaceAll("\\s+", Separators.SP);
                if (this.mAnnotation.startsWith(Separators.SP)) {
                    this.mAnnotation = this.mAnnotation.substring(1);
                }
                if (this.mAnnotation.endsWith(Separators.SP)) {
                    this.mAnnotation = this.mAnnotation.substring(0, this.mAnnotation.length() - 1);
                }
                String[] classes = null;
                int dotAt = this.mName.indexOf(46);
                if (dotAt >= 0) {
                    classes = this.mName.substring(dotAt + 1).split("\\.");
                    this.mName = this.mName.substring(0, dotAt);
                }
                Tokenizer.this.mListener.onStart(this.mName, classes, this.mAnnotation);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Tokenizer(OnTokenListener listener) {
        reset();
        this.mListener = listener;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void reset() {
        this.mPhase = this.mDataTokenizer.start();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void tokenize(String s) {
        this.mHandledLen = 0;
        this.mLine = s;
        while (this.mHandledLen < this.mLine.length()) {
            this.mPhase.tokenize();
        }
        if (!(this.mPhase instanceof TagTokenizer)) {
            this.mListener.onLineEnd();
        }
    }
}