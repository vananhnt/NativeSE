package android.media;

import android.media.SubtitleTrack;
import android.provider.Telephony;
import gov.nist.core.Separators;
import java.util.Arrays;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: WebVttRenderer.java */
/* loaded from: TextTrackCue.class */
public class TextTrackCue extends SubtitleTrack.Cue {
    static final int WRITING_DIRECTION_HORIZONTAL = 100;
    static final int WRITING_DIRECTION_VERTICAL_RL = 101;
    static final int WRITING_DIRECTION_VERTICAL_LR = 102;
    static final int ALIGNMENT_MIDDLE = 200;
    static final int ALIGNMENT_START = 201;
    static final int ALIGNMENT_END = 202;
    static final int ALIGNMENT_LEFT = 203;
    static final int ALIGNMENT_RIGHT = 204;
    private static final String TAG = "TTCue";
    boolean mAutoLinePosition;
    String[] mStrings;
    String mId = "";
    boolean mPauseOnExit = false;
    int mWritingDirection = 100;
    String mRegionId = "";
    boolean mSnapToLines = true;
    Integer mLinePosition = null;
    int mTextPosition = 50;
    int mSize = 100;
    int mAlignment = 200;
    TextTrackCueSpan[][] mLines = null;
    TextTrackRegion mRegion = null;

    public boolean equals(Object o) {
        if (!(o instanceof TextTrackCue)) {
            return false;
        }
        if (this == o) {
            return true;
        }
        try {
            TextTrackCue cue = (TextTrackCue) o;
            boolean res = this.mId.equals(cue.mId) && this.mPauseOnExit == cue.mPauseOnExit && this.mWritingDirection == cue.mWritingDirection && this.mRegionId.equals(cue.mRegionId) && this.mSnapToLines == cue.mSnapToLines && this.mAutoLinePosition == cue.mAutoLinePosition && (this.mAutoLinePosition || this.mLinePosition == cue.mLinePosition) && this.mTextPosition == cue.mTextPosition && this.mSize == cue.mSize && this.mAlignment == cue.mAlignment && this.mLines.length == cue.mLines.length;
            if (res) {
                for (int line = 0; line < this.mLines.length; line++) {
                    if (!Arrays.equals(this.mLines[line], cue.mLines[line])) {
                        return false;
                    }
                }
            }
            return res;
        } catch (IncompatibleClassChangeError e) {
            return false;
        }
    }

    public StringBuilder appendStringsToBuilder(StringBuilder builder) {
        if (this.mStrings == null) {
            builder.append("null");
        } else {
            builder.append("[");
            boolean first = true;
            String[] arr$ = this.mStrings;
            for (String s : arr$) {
                if (!first) {
                    builder.append(", ");
                }
                if (s == null) {
                    builder.append("null");
                } else {
                    builder.append(Separators.DOUBLE_QUOTE);
                    builder.append(s);
                    builder.append(Separators.DOUBLE_QUOTE);
                }
                first = false;
            }
            builder.append("]");
        }
        return builder;
    }

    public StringBuilder appendLinesToBuilder(StringBuilder builder) {
        if (this.mLines == null) {
            builder.append("null");
        } else {
            builder.append("[");
            boolean first = true;
            TextTrackCueSpan[][] arr$ = this.mLines;
            for (TextTrackCueSpan[] spans : arr$) {
                if (!first) {
                    builder.append(", ");
                }
                if (spans == null) {
                    builder.append("null");
                } else {
                    builder.append(Separators.DOUBLE_QUOTE);
                    boolean innerFirst = true;
                    long lastTimestamp = -1;
                    for (TextTrackCueSpan span : spans) {
                        if (!innerFirst) {
                            builder.append(Separators.SP);
                        }
                        if (span.mTimestampMs != lastTimestamp) {
                            builder.append(Separators.LESS_THAN).append(WebVttParser.timeToString(span.mTimestampMs)).append(Separators.GREATER_THAN);
                            lastTimestamp = span.mTimestampMs;
                        }
                        builder.append(span.mText);
                        innerFirst = false;
                    }
                    builder.append(Separators.DOUBLE_QUOTE);
                }
                first = false;
            }
            builder.append("]");
        }
        return builder;
    }

    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append(WebVttParser.timeToString(this.mStartTimeMs)).append(" --> ").append(WebVttParser.timeToString(this.mEndTimeMs)).append(" {id:\"").append(this.mId).append("\", pauseOnExit:").append(this.mPauseOnExit).append(", direction:").append(this.mWritingDirection == 100 ? "horizontal" : this.mWritingDirection == 102 ? "vertical_lr" : this.mWritingDirection == 101 ? "vertical_rl" : "INVALID").append(", regionId:\"").append(this.mRegionId).append("\", snapToLines:").append(this.mSnapToLines).append(", linePosition:").append(this.mAutoLinePosition ? "auto" : this.mLinePosition).append(", textPosition:").append(this.mTextPosition).append(", size:").append(this.mSize).append(", alignment:").append(this.mAlignment == 202 ? "end" : this.mAlignment == 203 ? "left" : this.mAlignment == 200 ? "middle" : this.mAlignment == 204 ? "right" : this.mAlignment == 201 ? Telephony.BaseMmsColumns.START : "INVALID").append(", text:");
        appendStringsToBuilder(res).append("}");
        return res.toString();
    }

    public int hashCode() {
        return toString().hashCode();
    }

    @Override // android.media.SubtitleTrack.Cue
    public void onTime(long timeMs) {
        TextTrackCueSpan[][] arr$ = this.mLines;
        for (TextTrackCueSpan[] line : arr$) {
            for (TextTrackCueSpan span : line) {
                span.mEnabled = timeMs >= span.mTimestampMs;
            }
        }
    }
}