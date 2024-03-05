package android.media;

import android.media.SubtitleTrack;
import android.util.Log;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/* compiled from: WebVttRenderer.java */
/* loaded from: WebVttTrack.class */
class WebVttTrack extends SubtitleTrack implements WebVttCueListener {
    private static final String TAG = "WebVttTrack";
    private final WebVttParser mParser;
    private final UnstyledTextExtractor mExtractor;
    private final Tokenizer mTokenizer;
    private final Vector<Long> mTimestamps;
    private final WebVttRenderingWidget mRenderingWidget;
    private final Map<String, TextTrackRegion> mRegions;
    private Long mCurrentRunID;

    /* JADX INFO: Access modifiers changed from: package-private */
    public WebVttTrack(WebVttRenderingWidget renderingWidget, MediaFormat format) {
        super(format);
        this.mParser = new WebVttParser(this);
        this.mExtractor = new UnstyledTextExtractor();
        this.mTokenizer = new Tokenizer(this.mExtractor);
        this.mTimestamps = new Vector<>();
        this.mRegions = new HashMap();
        this.mRenderingWidget = renderingWidget;
    }

    @Override // android.media.SubtitleTrack
    public WebVttRenderingWidget getRenderingWidget() {
        return this.mRenderingWidget;
    }

    @Override // android.media.SubtitleTrack
    public void onData(String data, boolean eos, long runID) {
        synchronized (this.mParser) {
            if (this.mCurrentRunID != null && runID != this.mCurrentRunID.longValue()) {
                throw new IllegalStateException("Run #" + this.mCurrentRunID + " in progress.  Cannot process run #" + runID);
            }
            this.mCurrentRunID = Long.valueOf(runID);
            this.mParser.parse(data);
            if (eos) {
                finishedRun(runID);
                this.mParser.eos();
                this.mRegions.clear();
                this.mCurrentRunID = null;
            }
        }
    }

    @Override // android.media.WebVttCueListener
    public void onCueParsed(TextTrackCue cue) {
        synchronized (this.mParser) {
            if (cue.mRegionId.length() != 0) {
                cue.mRegion = this.mRegions.get(cue.mRegionId);
            }
            if (this.DEBUG) {
                Log.v(TAG, "adding cue " + cue);
            }
            this.mTokenizer.reset();
            String[] arr$ = cue.mStrings;
            for (String s : arr$) {
                this.mTokenizer.tokenize(s);
            }
            cue.mLines = this.mExtractor.getText();
            if (this.DEBUG) {
                Log.v(TAG, cue.appendLinesToBuilder(cue.appendStringsToBuilder(new StringBuilder()).append(" simplified to: ")).toString());
            }
            TextTrackCueSpan[][] arr$2 = cue.mLines;
            for (TextTrackCueSpan[] line : arr$2) {
                for (TextTrackCueSpan span : line) {
                    if (span.mTimestampMs > cue.mStartTimeMs && span.mTimestampMs < cue.mEndTimeMs && !this.mTimestamps.contains(Long.valueOf(span.mTimestampMs))) {
                        this.mTimestamps.add(Long.valueOf(span.mTimestampMs));
                    }
                }
            }
            if (this.mTimestamps.size() > 0) {
                cue.mInnerTimesMs = new long[this.mTimestamps.size()];
                for (int ix = 0; ix < this.mTimestamps.size(); ix++) {
                    cue.mInnerTimesMs[ix] = this.mTimestamps.get(ix).longValue();
                }
                this.mTimestamps.clear();
            } else {
                cue.mInnerTimesMs = null;
            }
            cue.mRunID = this.mCurrentRunID.longValue();
        }
        addCue(cue);
    }

    @Override // android.media.WebVttCueListener
    public void onRegionParsed(TextTrackRegion region) {
        synchronized (this.mParser) {
            this.mRegions.put(region.mId, region);
        }
    }

    @Override // android.media.SubtitleTrack
    public void updateView(Vector<SubtitleTrack.Cue> activeCues) {
        if (!this.mVisible) {
            return;
        }
        if (this.DEBUG && this.mTimeProvider != null) {
            try {
                Log.d(TAG, "at " + (this.mTimeProvider.getCurrentTimeUs(false, true) / 1000) + " ms the active cues are:");
            } catch (IllegalStateException e) {
                Log.d(TAG, "at (illegal state) the active cues are:");
            }
        }
        this.mRenderingWidget.setActiveCues(activeCues);
    }
}