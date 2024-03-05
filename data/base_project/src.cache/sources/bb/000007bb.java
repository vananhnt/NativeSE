package android.media;

/* loaded from: MediaTimeProvider.class */
public interface MediaTimeProvider {
    public static final long NO_TIME = -1;

    /* loaded from: MediaTimeProvider$OnMediaTimeListener.class */
    public interface OnMediaTimeListener {
        void onTimedEvent(long j);

        void onSeek(long j);

        void onStop();
    }

    void notifyAt(long j, OnMediaTimeListener onMediaTimeListener);

    void scheduleUpdate(OnMediaTimeListener onMediaTimeListener);

    void cancelNotifications(OnMediaTimeListener onMediaTimeListener);

    long getCurrentTimeUs(boolean z, boolean z2) throws IllegalStateException;
}