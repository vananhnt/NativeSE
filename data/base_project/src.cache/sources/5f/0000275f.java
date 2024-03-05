package java.util.regex;

/* loaded from: MatchResultImpl.class */
class MatchResultImpl implements MatchResult {
    private String text;
    private int[] offsets;

    MatchResultImpl(String text, int[] offsets) {
        this.text = text;
        this.offsets = (int[]) offsets.clone();
    }

    @Override // java.util.regex.MatchResult
    public int end() {
        return end(0);
    }

    @Override // java.util.regex.MatchResult
    public int end(int group) {
        return this.offsets[(2 * group) + 1];
    }

    @Override // java.util.regex.MatchResult
    public String group() {
        return this.text.substring(start(), end());
    }

    @Override // java.util.regex.MatchResult
    public String group(int group) {
        int from = this.offsets[group * 2];
        int to = this.offsets[(group * 2) + 1];
        if (from == -1 || to == -1) {
            return null;
        }
        return this.text.substring(from, to);
    }

    @Override // java.util.regex.MatchResult
    public int groupCount() {
        return (this.offsets.length / 2) - 1;
    }

    @Override // java.util.regex.MatchResult
    public int start() {
        return start(0);
    }

    @Override // java.util.regex.MatchResult
    public int start(int group) {
        return this.offsets[2 * group];
    }
}