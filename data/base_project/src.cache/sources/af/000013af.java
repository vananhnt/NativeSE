package android.text;

/* loaded from: TextDirectionHeuristic.class */
public interface TextDirectionHeuristic {
    boolean isRtl(char[] cArr, int i, int i2);

    boolean isRtl(CharSequence charSequence, int i, int i2);
}