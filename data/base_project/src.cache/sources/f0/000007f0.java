package android.media;

/* compiled from: WebVttRenderer.java */
/* loaded from: TextTrackCueSpan.class */
class TextTrackCueSpan {
    long mTimestampMs;
    boolean mEnabled;
    String mText;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TextTrackCueSpan(String text, long timestamp) {
        this.mTimestampMs = timestamp;
        this.mText = text;
        this.mEnabled = this.mTimestampMs < 0;
    }

    public boolean equals(Object o) {
        if (!(o instanceof TextTrackCueSpan)) {
            return false;
        }
        TextTrackCueSpan span = (TextTrackCueSpan) o;
        return this.mTimestampMs == span.mTimestampMs && this.mText.equals(span.mText);
    }
}