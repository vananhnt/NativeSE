package android.support.v4.media;

import android.view.KeyEvent;

/* loaded from: TransportMediatorCallback.class */
interface TransportMediatorCallback {
    long getPlaybackPosition();

    void handleAudioFocusChange(int i);

    void handleKey(KeyEvent keyEvent);

    void playbackPositionUpdate(long j);
}