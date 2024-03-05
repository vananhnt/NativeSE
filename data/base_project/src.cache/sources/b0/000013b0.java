package android.text;

import java.nio.CharBuffer;
import java.util.Locale;

/* loaded from: TextDirectionHeuristics.class */
public class TextDirectionHeuristics {
    public static final TextDirectionHeuristic LTR = new TextDirectionHeuristicInternal(null, false);
    public static final TextDirectionHeuristic RTL = new TextDirectionHeuristicInternal(null, true);
    public static final TextDirectionHeuristic FIRSTSTRONG_LTR = new TextDirectionHeuristicInternal(FirstStrong.INSTANCE, false);
    public static final TextDirectionHeuristic FIRSTSTRONG_RTL = new TextDirectionHeuristicInternal(FirstStrong.INSTANCE, true);
    public static final TextDirectionHeuristic ANYRTL_LTR = new TextDirectionHeuristicInternal(AnyStrong.INSTANCE_RTL, false);
    public static final TextDirectionHeuristic LOCALE = TextDirectionHeuristicLocale.INSTANCE;
    private static final int STATE_TRUE = 0;
    private static final int STATE_FALSE = 1;
    private static final int STATE_UNKNOWN = 2;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: TextDirectionHeuristics$TextDirectionAlgorithm.class */
    public interface TextDirectionAlgorithm {
        int checkRtl(CharSequence charSequence, int i, int i2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int isRtlText(int directionality) {
        switch (directionality) {
            case 0:
                return 1;
            case 1:
            case 2:
                return 0;
            default:
                return 2;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int isRtlTextOrFormat(int directionality) {
        switch (directionality) {
            case 0:
            case 14:
            case 15:
                return 1;
            case 1:
            case 2:
            case 16:
            case 17:
                return 0;
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            default:
                return 2;
        }
    }

    /* loaded from: TextDirectionHeuristics$TextDirectionHeuristicImpl.class */
    private static abstract class TextDirectionHeuristicImpl implements TextDirectionHeuristic {
        private final TextDirectionAlgorithm mAlgorithm;

        protected abstract boolean defaultIsRtl();

        public TextDirectionHeuristicImpl(TextDirectionAlgorithm algorithm) {
            this.mAlgorithm = algorithm;
        }

        @Override // android.text.TextDirectionHeuristic
        public boolean isRtl(char[] array, int start, int count) {
            return isRtl(CharBuffer.wrap(array), start, count);
        }

        @Override // android.text.TextDirectionHeuristic
        public boolean isRtl(CharSequence cs, int start, int count) {
            if (cs == null || start < 0 || count < 0 || cs.length() - count < start) {
                throw new IllegalArgumentException();
            }
            if (this.mAlgorithm == null) {
                return defaultIsRtl();
            }
            return doCheck(cs, start, count);
        }

        private boolean doCheck(CharSequence cs, int start, int count) {
            switch (this.mAlgorithm.checkRtl(cs, start, count)) {
                case 0:
                    return true;
                case 1:
                    return false;
                default:
                    return defaultIsRtl();
            }
        }
    }

    /* loaded from: TextDirectionHeuristics$TextDirectionHeuristicInternal.class */
    private static class TextDirectionHeuristicInternal extends TextDirectionHeuristicImpl {
        private final boolean mDefaultIsRtl;

        private TextDirectionHeuristicInternal(TextDirectionAlgorithm algorithm, boolean defaultIsRtl) {
            super(algorithm);
            this.mDefaultIsRtl = defaultIsRtl;
        }

        @Override // android.text.TextDirectionHeuristics.TextDirectionHeuristicImpl
        protected boolean defaultIsRtl() {
            return this.mDefaultIsRtl;
        }
    }

    /* loaded from: TextDirectionHeuristics$FirstStrong.class */
    private static class FirstStrong implements TextDirectionAlgorithm {
        public static final FirstStrong INSTANCE = new FirstStrong();

        @Override // android.text.TextDirectionHeuristics.TextDirectionAlgorithm
        public int checkRtl(CharSequence cs, int start, int count) {
            int result = 2;
            int e = start + count;
            for (int i = start; i < e && result == 2; i++) {
                result = TextDirectionHeuristics.isRtlTextOrFormat(Character.getDirectionality(cs.charAt(i)));
            }
            return result;
        }

        private FirstStrong() {
        }
    }

    /* loaded from: TextDirectionHeuristics$AnyStrong.class */
    private static class AnyStrong implements TextDirectionAlgorithm {
        private final boolean mLookForRtl;
        public static final AnyStrong INSTANCE_RTL = new AnyStrong(true);
        public static final AnyStrong INSTANCE_LTR = new AnyStrong(false);

        @Override // android.text.TextDirectionHeuristics.TextDirectionAlgorithm
        public int checkRtl(CharSequence cs, int start, int count) {
            boolean haveUnlookedFor = false;
            int e = start + count;
            for (int i = start; i < e; i++) {
                switch (TextDirectionHeuristics.isRtlText(Character.getDirectionality(cs.charAt(i)))) {
                    case 0:
                        if (this.mLookForRtl) {
                            return 0;
                        }
                        haveUnlookedFor = true;
                        break;
                    case 1:
                        if (!this.mLookForRtl) {
                            return 1;
                        }
                        haveUnlookedFor = true;
                        break;
                }
            }
            if (haveUnlookedFor) {
                return this.mLookForRtl ? 1 : 0;
            }
            return 2;
        }

        private AnyStrong(boolean lookForRtl) {
            this.mLookForRtl = lookForRtl;
        }
    }

    /* loaded from: TextDirectionHeuristics$TextDirectionHeuristicLocale.class */
    private static class TextDirectionHeuristicLocale extends TextDirectionHeuristicImpl {
        public static final TextDirectionHeuristicLocale INSTANCE = new TextDirectionHeuristicLocale();

        public TextDirectionHeuristicLocale() {
            super(null);
        }

        @Override // android.text.TextDirectionHeuristics.TextDirectionHeuristicImpl
        protected boolean defaultIsRtl() {
            int dir = TextUtils.getLayoutDirectionFromLocale(Locale.getDefault());
            return dir == 1;
        }
    }
}