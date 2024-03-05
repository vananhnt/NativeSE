package android.media.videoeditor;

import android.graphics.Bitmap;
import android.media.videoeditor.MediaArtistNativeHelper;
import android.media.videoeditor.MediaItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import gov.nist.core.Separators;
import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;

/* loaded from: MediaVideoItem.class */
public class MediaVideoItem extends MediaItem {
    private final int mWidth;
    private final int mHeight;
    private final int mAspectRatio;
    private final int mFileType;
    private final int mVideoType;
    private final int mVideoProfile;
    private final int mVideoLevel;
    private final int mVideoBitrate;
    private final long mDurationMs;
    private final int mAudioBitrate;
    private final int mFps;
    private final int mAudioType;
    private final int mAudioChannels;
    private final int mAudioSamplingFrequency;
    private long mBeginBoundaryTimeMs;
    private long mEndBoundaryTimeMs;
    private int mVolumePercentage;
    private boolean mMuted;
    private String mAudioWaveformFilename;
    private MediaArtistNativeHelper mMANativeHelper;
    private VideoEditorImpl mVideoEditor;
    private final int mVideoRotationDegree;
    private SoftReference<WaveformData> mWaveformData;

    private MediaVideoItem() throws IOException {
        this(null, null, null, 0);
    }

    public MediaVideoItem(VideoEditor editor, String mediaItemId, String filename, int renderingMode) throws IOException {
        this(editor, mediaItemId, filename, renderingMode, 0L, -1L, 100, false, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MediaVideoItem(VideoEditor editor, String mediaItemId, String filename, int renderingMode, long beginMs, long endMs, int volumePercent, boolean muted, String audioWaveformFilename) throws IOException {
        super(editor, mediaItemId, filename, renderingMode);
        if (editor instanceof VideoEditorImpl) {
            this.mMANativeHelper = ((VideoEditorImpl) editor).getNativeContext();
            this.mVideoEditor = (VideoEditorImpl) editor;
        }
        try {
            MediaArtistNativeHelper.Properties properties = this.mMANativeHelper.getMediaProperties(filename);
            VideoEditorProfile veProfile = VideoEditorProfile.get();
            if (veProfile == null) {
                throw new RuntimeException("Can't get the video editor profile");
            }
            int maxInputWidth = veProfile.maxInputVideoFrameWidth;
            int maxInputHeight = veProfile.maxInputVideoFrameHeight;
            if (properties.width > maxInputWidth || properties.height > maxInputHeight) {
                throw new IllegalArgumentException("Unsupported import resolution. Supported maximum width:" + maxInputWidth + " height:" + maxInputHeight + ", current width:" + properties.width + " height:" + properties.height);
            }
            if (!properties.profileSupported) {
                throw new IllegalArgumentException("Unsupported video profile " + properties.profile);
            }
            if (!properties.levelSupported) {
                throw new IllegalArgumentException("Unsupported video level " + properties.level);
            }
            switch (this.mMANativeHelper.getFileType(properties.fileType)) {
                case 0:
                case 1:
                case 10:
                    switch (this.mMANativeHelper.getVideoCodecType(properties.videoFormat)) {
                        case 1:
                        case 2:
                        case 3:
                            this.mWidth = properties.width;
                            this.mHeight = properties.height;
                            this.mAspectRatio = this.mMANativeHelper.getAspectRatio(properties.width, properties.height);
                            this.mFileType = this.mMANativeHelper.getFileType(properties.fileType);
                            this.mVideoType = this.mMANativeHelper.getVideoCodecType(properties.videoFormat);
                            this.mVideoProfile = properties.profile;
                            this.mVideoLevel = properties.level;
                            this.mDurationMs = properties.videoDuration;
                            this.mVideoBitrate = properties.videoBitrate;
                            this.mAudioBitrate = properties.audioBitrate;
                            this.mFps = (int) properties.averageFrameRate;
                            this.mAudioType = this.mMANativeHelper.getAudioCodecType(properties.audioFormat);
                            this.mAudioChannels = properties.audioChannels;
                            this.mAudioSamplingFrequency = properties.audioSamplingFrequency;
                            this.mBeginBoundaryTimeMs = beginMs;
                            this.mEndBoundaryTimeMs = endMs == -1 ? this.mDurationMs : endMs;
                            this.mVolumePercentage = volumePercent;
                            this.mMuted = muted;
                            this.mAudioWaveformFilename = audioWaveformFilename;
                            if (audioWaveformFilename != null) {
                                this.mWaveformData = new SoftReference<>(new WaveformData(audioWaveformFilename));
                            } else {
                                this.mWaveformData = null;
                            }
                            this.mVideoRotationDegree = properties.videoRotation;
                            return;
                        default:
                            throw new IllegalArgumentException("Unsupported Video Codec Format in Input File");
                    }
                default:
                    throw new IllegalArgumentException("Unsupported Input File Type");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage() + " : " + filename);
        }
    }

    public void setExtractBoundaries(long beginMs, long endMs) {
        if (beginMs > this.mDurationMs) {
            throw new IllegalArgumentException("setExtractBoundaries: Invalid start time");
        }
        if (endMs > this.mDurationMs) {
            throw new IllegalArgumentException("setExtractBoundaries: Invalid end time");
        }
        if (endMs != -1 && beginMs >= endMs) {
            throw new IllegalArgumentException("setExtractBoundaries: Start time is greater than end time");
        }
        if (beginMs < 0 || (endMs != -1 && endMs < 0)) {
            throw new IllegalArgumentException("setExtractBoundaries: Start time or end time is negative");
        }
        this.mMANativeHelper.setGeneratePreview(true);
        if (beginMs != this.mBeginBoundaryTimeMs && this.mBeginTransition != null) {
            this.mBeginTransition.invalidate();
        }
        if (endMs != this.mEndBoundaryTimeMs && this.mEndTransition != null) {
            this.mEndTransition.invalidate();
        }
        this.mBeginBoundaryTimeMs = beginMs;
        this.mEndBoundaryTimeMs = endMs;
        adjustTransitions();
        this.mVideoEditor.updateTimelineDuration();
    }

    public long getBoundaryBeginTime() {
        return this.mBeginBoundaryTimeMs;
    }

    public long getBoundaryEndTime() {
        return this.mEndBoundaryTimeMs;
    }

    @Override // android.media.videoeditor.MediaItem
    public void addEffect(Effect effect) {
        if (effect instanceof EffectKenBurns) {
            throw new IllegalArgumentException("Ken Burns effects cannot be applied to MediaVideoItem");
        }
        super.addEffect(effect);
    }

    @Override // android.media.videoeditor.MediaItem
    public Bitmap getThumbnail(int width, int height, long timeMs) {
        if (timeMs > this.mDurationMs) {
            throw new IllegalArgumentException("Time Exceeds duration");
        }
        if (timeMs < 0) {
            throw new IllegalArgumentException("Invalid Time duration");
        }
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Invalid Dimensions");
        }
        if (this.mVideoRotationDegree == 90 || this.mVideoRotationDegree == 270) {
            width = height;
            height = width;
        }
        return this.mMANativeHelper.getPixels(getFilename(), width, height, timeMs, this.mVideoRotationDegree);
    }

    @Override // android.media.videoeditor.MediaItem
    public void getThumbnailList(int width, int height, long startMs, long endMs, int thumbnailCount, int[] indices, MediaItem.GetThumbnailListCallback callback) throws IOException {
        if (startMs > endMs) {
            throw new IllegalArgumentException("Start time is greater than end time");
        }
        if (endMs > this.mDurationMs) {
            throw new IllegalArgumentException("End time is greater than file duration");
        }
        if (height <= 0 || width <= 0) {
            throw new IllegalArgumentException("Invalid dimension");
        }
        if (this.mVideoRotationDegree == 90 || this.mVideoRotationDegree == 270) {
            width = height;
            height = width;
        }
        this.mMANativeHelper.getPixelsList(getFilename(), width, height, startMs, endMs, thumbnailCount, indices, callback, this.mVideoRotationDegree);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.media.videoeditor.MediaItem
    public void invalidateTransitions(long startTimeMs, long durationMs) {
        if (this.mBeginTransition != null && isOverlapping(startTimeMs, durationMs, this.mBeginBoundaryTimeMs, this.mBeginTransition.getDuration())) {
            this.mBeginTransition.invalidate();
        }
        if (this.mEndTransition != null) {
            long transitionDurationMs = this.mEndTransition.getDuration();
            if (isOverlapping(startTimeMs, durationMs, this.mEndBoundaryTimeMs - transitionDurationMs, transitionDurationMs)) {
                this.mEndTransition.invalidate();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.media.videoeditor.MediaItem
    public void invalidateTransitions(long oldStartTimeMs, long oldDurationMs, long newStartTimeMs, long newDurationMs) {
        if (this.mBeginTransition != null) {
            long transitionDurationMs = this.mBeginTransition.getDuration();
            boolean oldOverlap = isOverlapping(oldStartTimeMs, oldDurationMs, this.mBeginBoundaryTimeMs, transitionDurationMs);
            boolean newOverlap = isOverlapping(newStartTimeMs, newDurationMs, this.mBeginBoundaryTimeMs, transitionDurationMs);
            if (newOverlap != oldOverlap) {
                this.mBeginTransition.invalidate();
            } else if (newOverlap && (oldStartTimeMs != newStartTimeMs || oldStartTimeMs + oldDurationMs <= transitionDurationMs || newStartTimeMs + newDurationMs <= transitionDurationMs)) {
                this.mBeginTransition.invalidate();
            }
        }
        if (this.mEndTransition != null) {
            long transitionDurationMs2 = this.mEndTransition.getDuration();
            boolean oldOverlap2 = isOverlapping(oldStartTimeMs, oldDurationMs, this.mEndBoundaryTimeMs - transitionDurationMs2, transitionDurationMs2);
            boolean newOverlap2 = isOverlapping(newStartTimeMs, newDurationMs, this.mEndBoundaryTimeMs - transitionDurationMs2, transitionDurationMs2);
            if (newOverlap2 != oldOverlap2) {
                this.mEndTransition.invalidate();
            } else if (newOverlap2) {
                if (oldStartTimeMs + oldDurationMs != newStartTimeMs + newDurationMs || oldStartTimeMs > this.mEndBoundaryTimeMs - transitionDurationMs2 || newStartTimeMs > this.mEndBoundaryTimeMs - transitionDurationMs2) {
                    this.mEndTransition.invalidate();
                }
            }
        }
    }

    @Override // android.media.videoeditor.MediaItem
    public int getAspectRatio() {
        return this.mAspectRatio;
    }

    @Override // android.media.videoeditor.MediaItem
    public int getFileType() {
        return this.mFileType;
    }

    @Override // android.media.videoeditor.MediaItem
    public int getWidth() {
        if (this.mVideoRotationDegree == 90 || this.mVideoRotationDegree == 270) {
            return this.mHeight;
        }
        return this.mWidth;
    }

    @Override // android.media.videoeditor.MediaItem
    public int getHeight() {
        if (this.mVideoRotationDegree == 90 || this.mVideoRotationDegree == 270) {
            return this.mWidth;
        }
        return this.mHeight;
    }

    @Override // android.media.videoeditor.MediaItem
    public long getDuration() {
        return this.mDurationMs;
    }

    @Override // android.media.videoeditor.MediaItem
    public long getTimelineDuration() {
        return this.mEndBoundaryTimeMs - this.mBeginBoundaryTimeMs;
    }

    public long renderFrame(SurfaceHolder surfaceHolder, long timeMs) {
        if (surfaceHolder == null) {
            throw new IllegalArgumentException("Surface Holder is null");
        }
        if (timeMs > this.mDurationMs || timeMs < 0) {
            throw new IllegalArgumentException("requested time not correct");
        }
        Surface surface = surfaceHolder.getSurface();
        if (surface == null) {
            throw new RuntimeException("Surface could not be retrieved from Surface holder");
        }
        if (this.mFilename != null) {
            return this.mMANativeHelper.renderMediaItemPreviewFrame(surface, this.mFilename, timeMs, this.mWidth, this.mHeight);
        }
        return 0L;
    }

    public void extractAudioWaveform(ExtractAudioWaveformProgressListener listener) throws IOException {
        int frameDuration = 0;
        int sampleCount = 0;
        String projectPath = this.mMANativeHelper.getProjectPath();
        if (this.mAudioWaveformFilename == null) {
            String mAudioWaveFileName = String.format(projectPath + Separators.SLASH + "audioWaveformFile-" + getId() + ".dat", new Object[0]);
            if (this.mMANativeHelper.getAudioCodecType(this.mAudioType) == 1) {
                frameDuration = 5;
                sampleCount = 160;
            } else if (this.mMANativeHelper.getAudioCodecType(this.mAudioType) == 8) {
                frameDuration = 10;
                sampleCount = 320;
            } else if (this.mMANativeHelper.getAudioCodecType(this.mAudioType) == 2) {
                frameDuration = 32;
                sampleCount = 1024;
            }
            this.mMANativeHelper.generateAudioGraph(getId(), this.mFilename, mAudioWaveFileName, frameDuration, 2, sampleCount, listener, true);
            this.mAudioWaveformFilename = mAudioWaveFileName;
        }
        this.mWaveformData = new SoftReference<>(new WaveformData(this.mAudioWaveformFilename));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getAudioWaveformFilename() {
        return this.mAudioWaveformFilename;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void invalidate() {
        if (this.mAudioWaveformFilename != null) {
            new File(this.mAudioWaveformFilename).delete();
            this.mAudioWaveformFilename = null;
        }
    }

    public WaveformData getWaveformData() throws IOException {
        if (this.mWaveformData == null) {
            return null;
        }
        WaveformData waveformData = this.mWaveformData.get();
        if (waveformData != null) {
            return waveformData;
        }
        if (this.mAudioWaveformFilename != null) {
            try {
                WaveformData waveformData2 = new WaveformData(this.mAudioWaveformFilename);
                this.mWaveformData = new SoftReference<>(waveformData2);
                return waveformData2;
            } catch (IOException e) {
                throw e;
            }
        }
        return null;
    }

    public void setVolume(int volumePercent) {
        if (volumePercent < 0 || volumePercent > 100) {
            throw new IllegalArgumentException("Invalid volume");
        }
        this.mVolumePercentage = volumePercent;
    }

    public int getVolume() {
        return this.mVolumePercentage;
    }

    public void setMute(boolean muted) {
        this.mMANativeHelper.setGeneratePreview(true);
        this.mMuted = muted;
        if (this.mBeginTransition != null) {
            this.mBeginTransition.invalidate();
        }
        if (this.mEndTransition != null) {
            this.mEndTransition.invalidate();
        }
    }

    public boolean isMuted() {
        return this.mMuted;
    }

    public int getVideoType() {
        return this.mVideoType;
    }

    public int getVideoProfile() {
        return this.mVideoProfile;
    }

    public int getVideoLevel() {
        return this.mVideoLevel;
    }

    public int getVideoBitrate() {
        return this.mVideoBitrate;
    }

    public int getAudioBitrate() {
        return this.mAudioBitrate;
    }

    public int getFps() {
        return this.mFps;
    }

    public int getAudioType() {
        return this.mAudioType;
    }

    public int getAudioChannels() {
        return this.mAudioChannels;
    }

    public int getAudioSamplingFrequency() {
        return this.mAudioSamplingFrequency;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MediaArtistNativeHelper.ClipSettings getVideoClipProperties() {
        MediaArtistNativeHelper.ClipSettings clipSettings = new MediaArtistNativeHelper.ClipSettings();
        clipSettings.clipPath = getFilename();
        clipSettings.fileType = this.mMANativeHelper.getMediaItemFileType(getFileType());
        clipSettings.beginCutTime = (int) getBoundaryBeginTime();
        clipSettings.endCutTime = (int) getBoundaryEndTime();
        clipSettings.mediaRendering = this.mMANativeHelper.getMediaItemRenderingMode(getRenderingMode());
        clipSettings.rotationDegree = this.mVideoRotationDegree;
        return clipSettings;
    }
}