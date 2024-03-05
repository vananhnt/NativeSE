package android.media;

import android.graphics.Canvas;
import android.media.MediaTimeProvider;
import android.os.Handler;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.Pair;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

/* loaded from: SubtitleTrack.class */
public abstract class SubtitleTrack implements MediaTimeProvider.OnMediaTimeListener {
    private static final String TAG = "SubtitleTrack";
    private long mLastUpdateTimeMs;
    private long mLastTimeMs;
    private Runnable mRunnable;
    protected boolean mVisible;
    private MediaFormat mFormat;
    protected MediaTimeProvider mTimeProvider;
    protected final LongSparseArray<Run> mRunsByEndTime = new LongSparseArray<>();
    protected final LongSparseArray<Run> mRunsByID = new LongSparseArray<>();
    protected final Vector<Cue> mActiveCues = new Vector<>();
    public boolean DEBUG = false;
    protected Handler mHandler = new Handler();
    private long mNextScheduledTimeMs = -1;
    protected CueList mCues = new CueList();

    /* loaded from: SubtitleTrack$RenderingWidget.class */
    public interface RenderingWidget {

        /* loaded from: SubtitleTrack$RenderingWidget$OnChangedListener.class */
        public interface OnChangedListener {
            void onChanged(RenderingWidget renderingWidget);
        }

        void setOnChangedListener(OnChangedListener onChangedListener);

        void setSize(int i, int i2);

        void setVisible(boolean z);

        void draw(Canvas canvas);

        void onAttachedToWindow();

        void onDetachedFromWindow();
    }

    public abstract void onData(String str, boolean z, long j);

    public abstract RenderingWidget getRenderingWidget();

    public abstract void updateView(Vector<Cue> vector);

    public SubtitleTrack(MediaFormat format) {
        this.mFormat = format;
        clearActiveCues();
        this.mLastTimeMs = -1L;
    }

    public final MediaFormat getFormat() {
        return this.mFormat;
    }

    protected synchronized void updateActiveCues(boolean rebuild, long timeMs) {
        if (rebuild || this.mLastUpdateTimeMs > timeMs) {
            clearActiveCues();
        }
        Iterator<Pair<Long, Cue>> it = this.mCues.entriesBetween(this.mLastUpdateTimeMs, timeMs).iterator();
        while (it.hasNext()) {
            Pair<Long, Cue> event = it.next();
            Cue cue = event.second;
            if (cue.mEndTimeMs == event.first.longValue()) {
                if (this.DEBUG) {
                    Log.v(TAG, "Removing " + cue);
                }
                this.mActiveCues.remove(cue);
                if (cue.mRunID == 0) {
                    it.remove();
                }
            } else if (cue.mStartTimeMs == event.first.longValue()) {
                if (this.DEBUG) {
                    Log.v(TAG, "Adding " + cue);
                }
                if (cue.mInnerTimesMs != null) {
                    cue.onTime(timeMs);
                }
                this.mActiveCues.add(cue);
            } else if (cue.mInnerTimesMs != null) {
                cue.onTime(timeMs);
            }
        }
        while (this.mRunsByEndTime.size() > 0 && this.mRunsByEndTime.keyAt(0) <= timeMs) {
            removeRunsByEndTimeIndex(0);
        }
        this.mLastUpdateTimeMs = timeMs;
    }

    private void removeRunsByEndTimeIndex(int ix) {
        Run valueAt = this.mRunsByEndTime.valueAt(ix);
        while (true) {
            Run run = valueAt;
            if (run != null) {
                Cue cue = run.mFirstCue;
                while (true) {
                    Cue cue2 = cue;
                    if (cue2 != null) {
                        this.mCues.remove(cue2);
                        Cue nextCue = cue2.mNextInRun;
                        cue2.mNextInRun = null;
                        cue = nextCue;
                    }
                }
                this.mRunsByID.remove(run.mRunID);
                Run nextRun = run.mNextRunAtEndTimeMs;
                run.mPrevRunAtEndTimeMs = null;
                run.mNextRunAtEndTimeMs = null;
                valueAt = nextRun;
            } else {
                this.mRunsByEndTime.removeAt(ix);
                return;
            }
        }
    }

    protected void finalize() throws Throwable {
        int size = this.mRunsByEndTime.size();
        for (int ix = size - 1; ix >= 0; ix--) {
            removeRunsByEndTimeIndex(ix);
        }
        super.finalize();
    }

    private synchronized void takeTime(long timeMs) {
        this.mLastTimeMs = timeMs;
    }

    protected synchronized void clearActiveCues() {
        if (this.DEBUG) {
            Log.v(TAG, "Clearing " + this.mActiveCues.size() + " active cues");
        }
        this.mActiveCues.clear();
        this.mLastUpdateTimeMs = -1L;
    }

    protected void scheduleTimedEvents() {
        if (this.mTimeProvider != null) {
            this.mNextScheduledTimeMs = this.mCues.nextTimeAfter(this.mLastTimeMs);
            if (this.DEBUG) {
                Log.d(TAG, "sched @" + this.mNextScheduledTimeMs + " after " + this.mLastTimeMs);
            }
            this.mTimeProvider.notifyAt(this.mNextScheduledTimeMs >= 0 ? this.mNextScheduledTimeMs * 1000 : -1L, this);
        }
    }

    @Override // android.media.MediaTimeProvider.OnMediaTimeListener
    public void onTimedEvent(long timeUs) {
        if (this.DEBUG) {
            Log.d(TAG, "onTimedEvent " + timeUs);
        }
        synchronized (this) {
            long timeMs = timeUs / 1000;
            updateActiveCues(false, timeMs);
            takeTime(timeMs);
        }
        updateView(this.mActiveCues);
        scheduleTimedEvents();
    }

    @Override // android.media.MediaTimeProvider.OnMediaTimeListener
    public void onSeek(long timeUs) {
        if (this.DEBUG) {
            Log.d(TAG, "onSeek " + timeUs);
        }
        synchronized (this) {
            long timeMs = timeUs / 1000;
            updateActiveCues(true, timeMs);
            takeTime(timeMs);
        }
        updateView(this.mActiveCues);
        scheduleTimedEvents();
    }

    @Override // android.media.MediaTimeProvider.OnMediaTimeListener
    public void onStop() {
        synchronized (this) {
            if (this.DEBUG) {
                Log.d(TAG, "onStop");
            }
            clearActiveCues();
            this.mLastTimeMs = -1L;
        }
        updateView(this.mActiveCues);
        this.mNextScheduledTimeMs = -1L;
        this.mTimeProvider.notifyAt(-1L, this);
    }

    public void show() {
        if (this.mVisible) {
            return;
        }
        this.mVisible = true;
        getRenderingWidget().setVisible(true);
        if (this.mTimeProvider != null) {
            this.mTimeProvider.scheduleUpdate(this);
        }
    }

    public void hide() {
        if (!this.mVisible) {
            return;
        }
        if (this.mTimeProvider != null) {
            this.mTimeProvider.cancelNotifications(this);
        }
        getRenderingWidget().setVisible(false);
        this.mVisible = false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public synchronized boolean addCue(Cue cue) {
        this.mCues.add(cue);
        if (cue.mRunID != 0) {
            Run run = this.mRunsByID.get(cue.mRunID);
            if (run == null) {
                run = new Run();
                this.mRunsByID.put(cue.mRunID, run);
                run.mEndTimeMs = cue.mEndTimeMs;
            } else if (run.mEndTimeMs < cue.mEndTimeMs) {
                run.mEndTimeMs = cue.mEndTimeMs;
            }
            cue.mNextInRun = run.mFirstCue;
            run.mFirstCue = cue;
        }
        long nowMs = -1;
        if (this.mTimeProvider != null) {
            try {
                nowMs = this.mTimeProvider.getCurrentTimeUs(false, true) / 1000;
            } catch (IllegalStateException e) {
            }
        }
        if (this.DEBUG) {
            Log.v(TAG, "mVisible=" + this.mVisible + ", " + cue.mStartTimeMs + " <= " + nowMs + ", " + cue.mEndTimeMs + " >= " + this.mLastTimeMs);
        }
        if (this.mVisible && cue.mStartTimeMs <= nowMs && cue.mEndTimeMs >= this.mLastTimeMs) {
            if (this.mRunnable != null) {
                this.mHandler.removeCallbacks(this.mRunnable);
            }
            final long thenMs = nowMs;
            this.mRunnable = new Runnable() { // from class: android.media.SubtitleTrack.1
                @Override // java.lang.Runnable
                public void run() {
                    synchronized (this) {
                        SubtitleTrack.this.mRunnable = null;
                        SubtitleTrack.this.updateActiveCues(true, thenMs);
                        SubtitleTrack.this.updateView(SubtitleTrack.this.mActiveCues);
                    }
                }
            };
            if (this.mHandler.postDelayed(this.mRunnable, 10L)) {
                if (this.DEBUG) {
                    Log.v(TAG, "scheduling update");
                    return true;
                }
                return true;
            } else if (this.DEBUG) {
                Log.w(TAG, "failed to schedule subtitle view update");
                return true;
            } else {
                return true;
            }
        } else if (!this.mVisible || cue.mEndTimeMs < this.mLastTimeMs) {
            return false;
        } else {
            if (cue.mStartTimeMs < this.mNextScheduledTimeMs || this.mNextScheduledTimeMs < 0) {
                scheduleTimedEvents();
                return false;
            }
            return false;
        }
    }

    public synchronized void setTimeProvider(MediaTimeProvider timeProvider) {
        if (this.mTimeProvider == timeProvider) {
            return;
        }
        if (this.mTimeProvider != null) {
            this.mTimeProvider.cancelNotifications(this);
        }
        this.mTimeProvider = timeProvider;
        if (this.mTimeProvider != null) {
            this.mTimeProvider.scheduleUpdate(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: SubtitleTrack$CueList.class */
    public static class CueList {
        private static final String TAG = "CueList";
        public boolean DEBUG = false;
        private SortedMap<Long, Vector<Cue>> mCues = new TreeMap();

        private boolean addEvent(Cue cue, long timeMs) {
            Vector<Cue> cues = this.mCues.get(Long.valueOf(timeMs));
            if (cues == null) {
                cues = new Vector<>(2);
                this.mCues.put(Long.valueOf(timeMs), cues);
            } else if (cues.contains(cue)) {
                return false;
            }
            cues.add(cue);
            return true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void removeEvent(Cue cue, long timeMs) {
            Vector<Cue> cues = this.mCues.get(Long.valueOf(timeMs));
            if (cues != null) {
                cues.remove(cue);
                if (cues.size() == 0) {
                    this.mCues.remove(Long.valueOf(timeMs));
                }
            }
        }

        public void add(Cue cue) {
            if (cue.mStartTimeMs >= cue.mEndTimeMs || !addEvent(cue, cue.mStartTimeMs)) {
                return;
            }
            long lastTimeMs = cue.mStartTimeMs;
            if (cue.mInnerTimesMs != null) {
                long[] arr$ = cue.mInnerTimesMs;
                for (long timeMs : arr$) {
                    if (timeMs > lastTimeMs && timeMs < cue.mEndTimeMs) {
                        addEvent(cue, timeMs);
                        lastTimeMs = timeMs;
                    }
                }
            }
            addEvent(cue, cue.mEndTimeMs);
        }

        public void remove(Cue cue) {
            removeEvent(cue, cue.mStartTimeMs);
            if (cue.mInnerTimesMs != null) {
                long[] arr$ = cue.mInnerTimesMs;
                for (long timeMs : arr$) {
                    removeEvent(cue, timeMs);
                }
            }
            removeEvent(cue, cue.mEndTimeMs);
        }

        public Iterable<Pair<Long, Cue>> entriesBetween(final long lastTimeMs, final long timeMs) {
            return new Iterable<Pair<Long, Cue>>() { // from class: android.media.SubtitleTrack.CueList.1
                @Override // java.lang.Iterable
                public Iterator<Pair<Long, Cue>> iterator() {
                    if (CueList.this.DEBUG) {
                        Log.d(CueList.TAG, "slice (" + lastTimeMs + ", " + timeMs + "]=");
                    }
                    try {
                        return new EntryIterator(CueList.this.mCues.subMap(Long.valueOf(lastTimeMs + 1), Long.valueOf(timeMs + 1)));
                    } catch (IllegalArgumentException e) {
                        return new EntryIterator(null);
                    }
                }
            };
        }

        public long nextTimeAfter(long timeMs) {
            try {
                SortedMap<Long, Vector<Cue>> tail = this.mCues.tailMap(Long.valueOf(timeMs + 1));
                if (tail != null) {
                    return tail.firstKey().longValue();
                }
                return -1L;
            } catch (IllegalArgumentException e) {
                return -1L;
            } catch (NoSuchElementException e2) {
                return -1L;
            }
        }

        /* loaded from: SubtitleTrack$CueList$EntryIterator.class */
        class EntryIterator implements Iterator<Pair<Long, Cue>> {
            private long mCurrentTimeMs;
            private Iterator<Cue> mListIterator;
            private boolean mDone;
            private SortedMap<Long, Vector<Cue>> mRemainingCues;
            private Iterator<Cue> mLastListIterator;
            private Pair<Long, Cue> mLastEntry;

            @Override // java.util.Iterator
            public boolean hasNext() {
                return !this.mDone;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.util.Iterator
            public Pair<Long, Cue> next() {
                if (this.mDone) {
                    throw new NoSuchElementException("");
                }
                this.mLastEntry = new Pair<>(Long.valueOf(this.mCurrentTimeMs), this.mListIterator.next());
                this.mLastListIterator = this.mListIterator;
                if (!this.mListIterator.hasNext()) {
                    nextKey();
                }
                return this.mLastEntry;
            }

            @Override // java.util.Iterator
            public void remove() {
                if (this.mLastListIterator == null || this.mLastEntry.second.mEndTimeMs != this.mLastEntry.first.longValue()) {
                    throw new IllegalStateException("");
                }
                this.mLastListIterator.remove();
                this.mLastListIterator = null;
                if (((Vector) CueList.this.mCues.get(this.mLastEntry.first)).size() == 0) {
                    CueList.this.mCues.remove(this.mLastEntry.first);
                }
                Cue cue = this.mLastEntry.second;
                CueList.this.removeEvent(cue, cue.mStartTimeMs);
                if (cue.mInnerTimesMs != null) {
                    long[] arr$ = cue.mInnerTimesMs;
                    for (long timeMs : arr$) {
                        CueList.this.removeEvent(cue, timeMs);
                    }
                }
            }

            public EntryIterator(SortedMap<Long, Vector<Cue>> cues) {
                if (CueList.this.DEBUG) {
                    Log.v(CueList.TAG, cues + "");
                }
                this.mRemainingCues = cues;
                this.mLastListIterator = null;
                nextKey();
            }

            private void nextKey() {
                do {
                    try {
                        if (this.mRemainingCues == null) {
                            throw new NoSuchElementException("");
                        }
                        this.mCurrentTimeMs = this.mRemainingCues.firstKey().longValue();
                        this.mListIterator = this.mRemainingCues.get(Long.valueOf(this.mCurrentTimeMs)).iterator();
                        try {
                            this.mRemainingCues = this.mRemainingCues.tailMap(Long.valueOf(this.mCurrentTimeMs + 1));
                        } catch (IllegalArgumentException e) {
                            this.mRemainingCues = null;
                        }
                        this.mDone = false;
                    } catch (NoSuchElementException e2) {
                        this.mDone = true;
                        this.mRemainingCues = null;
                        this.mListIterator = null;
                        return;
                    }
                    this.mDone = true;
                    this.mRemainingCues = null;
                    this.mListIterator = null;
                    return;
                } while (!this.mListIterator.hasNext());
            }
        }

        CueList() {
        }
    }

    /* loaded from: SubtitleTrack$Cue.class */
    public static class Cue {
        public long mStartTimeMs;
        public long mEndTimeMs;
        public long[] mInnerTimesMs;
        public long mRunID;
        public Cue mNextInRun;

        public void onTime(long timeMs) {
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void finishedRun(long runID) {
        Run run;
        if (runID != 0 && runID != -1 && (run = this.mRunsByID.get(runID)) != null) {
            run.storeByEndTimeMs(this.mRunsByEndTime);
        }
    }

    public void setRunDiscardTimeMs(long runID, long timeMs) {
        Run run;
        if (runID != 0 && runID != -1 && (run = this.mRunsByID.get(runID)) != null) {
            run.mEndTimeMs = timeMs;
            run.storeByEndTimeMs(this.mRunsByEndTime);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: SubtitleTrack$Run.class */
    public static class Run {
        public Cue mFirstCue;
        public Run mNextRunAtEndTimeMs;
        public Run mPrevRunAtEndTimeMs;
        public long mEndTimeMs;
        public long mRunID;
        private long mStoredEndTimeMs;
        static final /* synthetic */ boolean $assertionsDisabled;

        private Run() {
            this.mEndTimeMs = -1L;
            this.mRunID = 0L;
            this.mStoredEndTimeMs = -1L;
        }

        static {
            $assertionsDisabled = !SubtitleTrack.class.desiredAssertionStatus();
        }

        public void storeByEndTimeMs(LongSparseArray<Run> runsByEndTime) {
            int ix = runsByEndTime.indexOfKey(this.mStoredEndTimeMs);
            if (ix >= 0) {
                if (this.mPrevRunAtEndTimeMs == null) {
                    if (!$assertionsDisabled && this != runsByEndTime.valueAt(ix)) {
                        throw new AssertionError();
                    }
                    if (this.mNextRunAtEndTimeMs == null) {
                        runsByEndTime.removeAt(ix);
                    } else {
                        runsByEndTime.setValueAt(ix, this.mNextRunAtEndTimeMs);
                    }
                }
                removeAtEndTimeMs();
            }
            if (this.mEndTimeMs >= 0) {
                this.mPrevRunAtEndTimeMs = null;
                this.mNextRunAtEndTimeMs = runsByEndTime.get(this.mEndTimeMs);
                if (this.mNextRunAtEndTimeMs != null) {
                    this.mNextRunAtEndTimeMs.mPrevRunAtEndTimeMs = this;
                }
                runsByEndTime.put(this.mEndTimeMs, this);
                this.mStoredEndTimeMs = this.mEndTimeMs;
            }
        }

        public void removeAtEndTimeMs() {
            Run prev = this.mPrevRunAtEndTimeMs;
            if (this.mPrevRunAtEndTimeMs != null) {
                this.mPrevRunAtEndTimeMs.mNextRunAtEndTimeMs = this.mNextRunAtEndTimeMs;
                this.mPrevRunAtEndTimeMs = null;
            }
            if (this.mNextRunAtEndTimeMs != null) {
                this.mNextRunAtEndTimeMs.mPrevRunAtEndTimeMs = prev;
                this.mNextRunAtEndTimeMs = null;
            }
        }
    }
}