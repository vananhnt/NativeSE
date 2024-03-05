package android.media;

/* loaded from: MediaSyncEvent.class */
public class MediaSyncEvent {
    public static final int SYNC_EVENT_NONE = 0;
    public static final int SYNC_EVENT_PRESENTATION_COMPLETE = 1;
    private final int mType;
    private int mAudioSession = 0;

    public static MediaSyncEvent createEvent(int eventType) throws IllegalArgumentException {
        if (!isValidType(eventType)) {
            throw new IllegalArgumentException(eventType + "is not a valid MediaSyncEvent type.");
        }
        return new MediaSyncEvent(eventType);
    }

    private MediaSyncEvent(int eventType) {
        this.mType = eventType;
    }

    public MediaSyncEvent setAudioSessionId(int audioSessionId) throws IllegalArgumentException {
        if (audioSessionId > 0) {
            this.mAudioSession = audioSessionId;
            return this;
        }
        throw new IllegalArgumentException(audioSessionId + " is not a valid session ID.");
    }

    public int getType() {
        return this.mType;
    }

    public int getAudioSessionId() {
        return this.mAudioSession;
    }

    private static boolean isValidType(int type) {
        switch (type) {
            case 0:
            case 1:
                return true;
            default:
                return false;
        }
    }
}