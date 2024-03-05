package android.media.videoeditor;

import android.graphics.Rect;

/* loaded from: EffectKenBurns.class */
public class EffectKenBurns extends Effect {
    private Rect mStartRect;
    private Rect mEndRect;

    private EffectKenBurns() {
        this(null, null, null, null, 0L, 0L);
    }

    public EffectKenBurns(MediaItem mediaItem, String effectId, Rect startRect, Rect endRect, long startTimeMs, long durationMs) {
        super(mediaItem, effectId, startTimeMs, durationMs);
        if (startRect.width() <= 0 || startRect.height() <= 0) {
            throw new IllegalArgumentException("Invalid Start rectangle");
        }
        if (endRect.width() <= 0 || endRect.height() <= 0) {
            throw new IllegalArgumentException("Invalid End rectangle");
        }
        this.mStartRect = startRect;
        this.mEndRect = endRect;
    }

    public Rect getStartRect() {
        return this.mStartRect;
    }

    public Rect getEndRect() {
        return this.mEndRect;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void getKenBurnsSettings(Rect start, Rect end) {
        start.left = getStartRect().left;
        start.top = getStartRect().top;
        start.right = getStartRect().right;
        start.bottom = getStartRect().bottom;
        end.left = getEndRect().left;
        end.top = getEndRect().top;
        end.right = getEndRect().right;
        end.bottom = getEndRect().bottom;
    }
}