package android.media.videoeditor;

import android.media.videoeditor.MediaArtistNativeHelper;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/* loaded from: Transition.class */
public abstract class Transition {
    private static final int BEHAVIOR_MIN_VALUE = 0;
    public static final int BEHAVIOR_SPEED_UP = 0;
    public static final int BEHAVIOR_SPEED_DOWN = 1;
    public static final int BEHAVIOR_LINEAR = 2;
    public static final int BEHAVIOR_MIDDLE_SLOW = 3;
    public static final int BEHAVIOR_MIDDLE_FAST = 4;
    private static final int BEHAVIOR_MAX_VALUE = 4;
    private final String mUniqueId;
    private final MediaItem mAfterMediaItem;
    private final MediaItem mBeforeMediaItem;
    protected final int mBehavior;
    protected long mDurationMs;
    protected String mFilename;
    protected MediaArtistNativeHelper mNativeHelper;

    private Transition() {
        this(null, null, null, 0L, 0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Transition(String transitionId, MediaItem afterMediaItem, MediaItem beforeMediaItem, long durationMs, int behavior) {
        if (behavior < 0 || behavior > 4) {
            throw new IllegalArgumentException("Invalid behavior: " + behavior);
        }
        if (afterMediaItem == null && beforeMediaItem == null) {
            throw new IllegalArgumentException("Null media items");
        }
        this.mUniqueId = transitionId;
        this.mAfterMediaItem = afterMediaItem;
        this.mBeforeMediaItem = beforeMediaItem;
        this.mDurationMs = durationMs;
        this.mBehavior = behavior;
        this.mNativeHelper = null;
        if (durationMs > getMaximumDuration()) {
            throw new IllegalArgumentException("The duration is too large");
        }
        if (afterMediaItem != null) {
            this.mNativeHelper = afterMediaItem.getNativeContext();
        } else {
            this.mNativeHelper = beforeMediaItem.getNativeContext();
        }
    }

    public String getId() {
        return this.mUniqueId;
    }

    public MediaItem getAfterMediaItem() {
        return this.mAfterMediaItem;
    }

    public MediaItem getBeforeMediaItem() {
        return this.mBeforeMediaItem;
    }

    public void setDuration(long durationMs) {
        if (durationMs > getMaximumDuration()) {
            throw new IllegalArgumentException("The duration is too large");
        }
        this.mDurationMs = durationMs;
        invalidate();
        this.mNativeHelper.setGeneratePreview(true);
    }

    public long getDuration() {
        return this.mDurationMs;
    }

    public long getMaximumDuration() {
        if (this.mAfterMediaItem == null) {
            return this.mBeforeMediaItem.getTimelineDuration() / 2;
        }
        if (this.mBeforeMediaItem == null) {
            return this.mAfterMediaItem.getTimelineDuration() / 2;
        }
        return Math.min(this.mAfterMediaItem.getTimelineDuration(), this.mBeforeMediaItem.getTimelineDuration()) / 2;
    }

    public int getBehavior() {
        return this.mBehavior;
    }

    MediaArtistNativeHelper.TransitionSettings getTransitionSettings() {
        MediaArtistNativeHelper.TransitionSettings transitionSetting = new MediaArtistNativeHelper.TransitionSettings();
        transitionSetting.duration = (int) getDuration();
        if (this instanceof TransitionAlpha) {
            TransitionAlpha transitionAlpha = (TransitionAlpha) this;
            transitionSetting.videoTransitionType = 257;
            transitionSetting.audioTransitionType = 1;
            transitionSetting.transitionBehaviour = this.mNativeHelper.getVideoTransitionBehaviour(transitionAlpha.getBehavior());
            transitionSetting.alphaSettings = new MediaArtistNativeHelper.AlphaMagicSettings();
            transitionSetting.slideSettings = null;
            transitionSetting.alphaSettings.file = transitionAlpha.getPNGMaskFilename();
            transitionSetting.alphaSettings.blendingPercent = transitionAlpha.getBlendingPercent();
            transitionSetting.alphaSettings.invertRotation = transitionAlpha.isInvert();
            transitionSetting.alphaSettings.rgbWidth = transitionAlpha.getRGBFileWidth();
            transitionSetting.alphaSettings.rgbHeight = transitionAlpha.getRGBFileHeight();
        } else if (this instanceof TransitionSliding) {
            TransitionSliding transitionSliding = (TransitionSliding) this;
            transitionSetting.videoTransitionType = 258;
            transitionSetting.audioTransitionType = 1;
            transitionSetting.transitionBehaviour = this.mNativeHelper.getVideoTransitionBehaviour(transitionSliding.getBehavior());
            transitionSetting.alphaSettings = null;
            transitionSetting.slideSettings = new MediaArtistNativeHelper.SlideTransitionSettings();
            transitionSetting.slideSettings.direction = this.mNativeHelper.getSlideSettingsDirection(transitionSliding.getDirection());
        } else if (this instanceof TransitionCrossfade) {
            TransitionCrossfade transitionCrossfade = (TransitionCrossfade) this;
            transitionSetting.videoTransitionType = 1;
            transitionSetting.audioTransitionType = 1;
            transitionSetting.transitionBehaviour = this.mNativeHelper.getVideoTransitionBehaviour(transitionCrossfade.getBehavior());
            transitionSetting.alphaSettings = null;
            transitionSetting.slideSettings = null;
        } else if (this instanceof TransitionFadeBlack) {
            TransitionFadeBlack transitionFadeBlack = (TransitionFadeBlack) this;
            transitionSetting.videoTransitionType = 259;
            transitionSetting.audioTransitionType = 1;
            transitionSetting.transitionBehaviour = this.mNativeHelper.getVideoTransitionBehaviour(transitionFadeBlack.getBehavior());
            transitionSetting.alphaSettings = null;
            transitionSetting.slideSettings = null;
        }
        return transitionSetting;
    }

    List<MediaArtistNativeHelper.EffectSettings> isEffectandOverlayOverlapping(MediaItem m, MediaArtistNativeHelper.ClipSettings clipSettings, int clipNo) {
        List<MediaArtistNativeHelper.EffectSettings> effectSettings = new ArrayList<>();
        List<Overlay> overlays = m.getAllOverlays();
        for (Overlay overlay : overlays) {
            MediaArtistNativeHelper.EffectSettings tmpEffectSettings = this.mNativeHelper.getOverlaySettings((OverlayFrame) overlay);
            this.mNativeHelper.adjustEffectsStartTimeAndDuration(tmpEffectSettings, clipSettings.beginCutTime, clipSettings.endCutTime);
            if (tmpEffectSettings.duration != 0) {
                effectSettings.add(tmpEffectSettings);
            }
        }
        List<Effect> effects = m.getAllEffects();
        for (Effect effect : effects) {
            if (effect instanceof EffectColor) {
                MediaArtistNativeHelper.EffectSettings tmpEffectSettings2 = this.mNativeHelper.getEffectSettings((EffectColor) effect);
                this.mNativeHelper.adjustEffectsStartTimeAndDuration(tmpEffectSettings2, clipSettings.beginCutTime, clipSettings.endCutTime);
                if (tmpEffectSettings2.duration != 0) {
                    if (m instanceof MediaVideoItem) {
                        tmpEffectSettings2.fiftiesFrameRate = this.mNativeHelper.GetClosestVideoFrameRate(((MediaVideoItem) m).getFps());
                    }
                    effectSettings.add(tmpEffectSettings2);
                }
            }
        }
        return effectSettings;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void generate() {
        MediaArtistNativeHelper.EffectSettings effectSettings;
        MediaArtistNativeHelper.EffectSettings effectSettings2;
        MediaItem m1 = getAfterMediaItem();
        MediaItem m2 = getBeforeMediaItem();
        MediaArtistNativeHelper.ClipSettings clipSettings1 = new MediaArtistNativeHelper.ClipSettings();
        MediaArtistNativeHelper.ClipSettings clipSettings2 = new MediaArtistNativeHelper.ClipSettings();
        MediaArtistNativeHelper.EditSettings editSettings = new MediaArtistNativeHelper.EditSettings();
        if (this.mNativeHelper == null) {
            if (m1 != null) {
                this.mNativeHelper = m1.getNativeContext();
            } else if (m2 != null) {
                this.mNativeHelper = m2.getNativeContext();
            }
        }
        MediaArtistNativeHelper.TransitionSettings transitionSetting = getTransitionSettings();
        if (m1 != null && m2 != null) {
            clipSettings1 = m1.getClipSettings();
            clipSettings2 = m2.getClipSettings();
            clipSettings1.beginCutTime = (int) (clipSettings1.endCutTime - this.mDurationMs);
            clipSettings2.endCutTime = (int) (clipSettings2.beginCutTime + this.mDurationMs);
            List<MediaArtistNativeHelper.EffectSettings> effectSettings_clip1 = isEffectandOverlayOverlapping(m1, clipSettings1, 1);
            List<MediaArtistNativeHelper.EffectSettings> effectSettings_clip2 = isEffectandOverlayOverlapping(m2, clipSettings2, 2);
            for (int index = 0; index < effectSettings_clip2.size(); index++) {
                effectSettings_clip2.get(index).startTime = (int) (effectSettings2.startTime + this.mDurationMs);
            }
            editSettings.effectSettingsArray = new MediaArtistNativeHelper.EffectSettings[effectSettings_clip1.size() + effectSettings_clip2.size()];
            int i = 0;
            int j = 0;
            while (i < effectSettings_clip1.size()) {
                editSettings.effectSettingsArray[j] = effectSettings_clip1.get(i);
                i++;
                j++;
            }
            int i2 = 0;
            while (i2 < effectSettings_clip2.size()) {
                editSettings.effectSettingsArray[j] = effectSettings_clip2.get(i2);
                i2++;
                j++;
            }
        } else if (m1 == null && m2 != null) {
            m2.generateBlankFrame(clipSettings1);
            clipSettings2 = m2.getClipSettings();
            clipSettings1.endCutTime = (int) (this.mDurationMs + 50);
            clipSettings2.endCutTime = (int) (clipSettings2.beginCutTime + this.mDurationMs);
            List<MediaArtistNativeHelper.EffectSettings> effectSettings_clip22 = isEffectandOverlayOverlapping(m2, clipSettings2, 2);
            for (int index2 = 0; index2 < effectSettings_clip22.size(); index2++) {
                effectSettings_clip22.get(index2).startTime = (int) (effectSettings.startTime + this.mDurationMs);
            }
            editSettings.effectSettingsArray = new MediaArtistNativeHelper.EffectSettings[effectSettings_clip22.size()];
            int i3 = 0;
            int j2 = 0;
            while (i3 < effectSettings_clip22.size()) {
                editSettings.effectSettingsArray[j2] = effectSettings_clip22.get(i3);
                i3++;
                j2++;
            }
        } else if (m1 != null && m2 == null) {
            clipSettings1 = m1.getClipSettings();
            m1.generateBlankFrame(clipSettings2);
            clipSettings1.beginCutTime = (int) (clipSettings1.endCutTime - this.mDurationMs);
            clipSettings2.endCutTime = (int) (this.mDurationMs + 50);
            List<MediaArtistNativeHelper.EffectSettings> effectSettings_clip12 = isEffectandOverlayOverlapping(m1, clipSettings1, 1);
            editSettings.effectSettingsArray = new MediaArtistNativeHelper.EffectSettings[effectSettings_clip12.size()];
            int i4 = 0;
            int j3 = 0;
            while (i4 < effectSettings_clip12.size()) {
                editSettings.effectSettingsArray[j3] = effectSettings_clip12.get(i4);
                i4++;
                j3++;
            }
        }
        editSettings.clipSettingsArray = new MediaArtistNativeHelper.ClipSettings[2];
        editSettings.clipSettingsArray[0] = clipSettings1;
        editSettings.clipSettingsArray[1] = clipSettings2;
        editSettings.backgroundMusicSettings = null;
        editSettings.transitionSettingsArray = new MediaArtistNativeHelper.TransitionSettings[1];
        editSettings.transitionSettingsArray[0] = transitionSetting;
        String output = this.mNativeHelper.generateTransitionClip(editSettings, this.mUniqueId, m1, m2, this);
        setFilename(output);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setFilename(String filename) {
        this.mFilename = filename;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getFilename() {
        return this.mFilename;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void invalidate() {
        if (this.mFilename != null) {
            new File(this.mFilename).delete();
            this.mFilename = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isGenerated() {
        return this.mFilename != null;
    }

    public boolean equals(Object object) {
        if (!(object instanceof Transition)) {
            return false;
        }
        return this.mUniqueId.equals(((Transition) object).mUniqueId);
    }

    public int hashCode() {
        return this.mUniqueId.hashCode();
    }
}