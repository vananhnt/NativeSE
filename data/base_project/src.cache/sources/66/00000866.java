package android.media.videoeditor;

import android.media.videoeditor.MediaArtistNativeHelper;
import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;

/* loaded from: AudioTrack.class */
public class AudioTrack {
    private final MediaArtistNativeHelper mMANativeHelper;
    private final String mUniqueId;
    private final String mFilename;
    private long mStartTimeMs;
    private long mTimelineDurationMs;
    private int mVolumePercent;
    private long mBeginBoundaryTimeMs;
    private long mEndBoundaryTimeMs;
    private boolean mLoop;
    private boolean mMuted;
    private final long mDurationMs;
    private final int mAudioChannels;
    private final int mAudioType;
    private final int mAudioBitrate;
    private final int mAudioSamplingFrequency;
    private int mDuckingThreshold;
    private int mDuckedTrackVolume;
    private boolean mIsDuckingEnabled;
    private String mAudioWaveformFilename;
    private SoftReference<WaveformData> mWaveformData;

    private AudioTrack() throws IOException {
        this(null, null, null);
    }

    public AudioTrack(VideoEditor editor, String audioTrackId, String filename) throws IOException {
        this(editor, audioTrackId, filename, 0L, 0L, -1L, false, 100, false, false, 0, 0, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AudioTrack(VideoEditor editor, String audioTrackId, String filename, long startTimeMs, long beginMs, long endMs, boolean loop, int volume, boolean muted, boolean duckingEnabled, int duckThreshold, int duckedTrackVolume, String audioWaveformFilename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            throw new IOException(filename + " not found ! ");
        }
        if (VideoEditor.MAX_SUPPORTED_FILE_SIZE <= file.length()) {
            throw new IllegalArgumentException("File size is more than 2GB");
        }
        if (editor instanceof VideoEditorImpl) {
            this.mMANativeHelper = ((VideoEditorImpl) editor).getNativeContext();
            try {
                MediaArtistNativeHelper.Properties properties = this.mMANativeHelper.getMediaProperties(filename);
                int fileType = this.mMANativeHelper.getFileType(properties.fileType);
                switch (fileType) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                        switch (this.mMANativeHelper.getAudioCodecType(properties.audioFormat)) {
                            case 1:
                            case 2:
                            case 5:
                            case 8:
                                endMs = endMs == -1 ? properties.audioDuration : endMs;
                                this.mUniqueId = audioTrackId;
                                this.mFilename = filename;
                                this.mStartTimeMs = startTimeMs;
                                this.mDurationMs = properties.audioDuration;
                                this.mAudioChannels = properties.audioChannels;
                                this.mAudioBitrate = properties.audioBitrate;
                                this.mAudioSamplingFrequency = properties.audioSamplingFrequency;
                                this.mAudioType = properties.audioFormat;
                                this.mTimelineDurationMs = endMs - beginMs;
                                this.mVolumePercent = volume;
                                this.mBeginBoundaryTimeMs = beginMs;
                                this.mEndBoundaryTimeMs = endMs;
                                this.mLoop = loop;
                                this.mMuted = muted;
                                this.mIsDuckingEnabled = duckingEnabled;
                                this.mDuckingThreshold = duckThreshold;
                                this.mDuckedTrackVolume = duckedTrackVolume;
                                this.mAudioWaveformFilename = audioWaveformFilename;
                                if (audioWaveformFilename != null) {
                                    this.mWaveformData = new SoftReference<>(new WaveformData(audioWaveformFilename));
                                    return;
                                } else {
                                    this.mWaveformData = null;
                                    return;
                                }
                            case 3:
                            case 4:
                            case 6:
                            case 7:
                            default:
                                throw new IllegalArgumentException("Unsupported Audio Codec Format in Input File");
                        }
                    default:
                        throw new IllegalArgumentException("Unsupported input file type: " + fileType);
                }
            } catch (Exception e) {
                throw new IllegalArgumentException(e.getMessage() + " : " + filename);
            }
        }
        throw new IllegalArgumentException("editor is not of type VideoEditorImpl");
    }

    public String getId() {
        return this.mUniqueId;
    }

    public String getFilename() {
        return this.mFilename;
    }

    public int getAudioChannels() {
        return this.mAudioChannels;
    }

    public int getAudioType() {
        return this.mAudioType;
    }

    public int getAudioSamplingFrequency() {
        return this.mAudioSamplingFrequency;
    }

    public int getAudioBitrate() {
        return this.mAudioBitrate;
    }

    public void setVolume(int volumePercent) {
        if (volumePercent > 100) {
            throw new IllegalArgumentException("Volume set exceeds maximum allowed value");
        }
        if (volumePercent < 0) {
            throw new IllegalArgumentException("Invalid Volume ");
        }
        this.mMANativeHelper.setGeneratePreview(true);
        this.mVolumePercent = volumePercent;
    }

    public int getVolume() {
        return this.mVolumePercent;
    }

    public void setMute(boolean muted) {
        this.mMANativeHelper.setGeneratePreview(true);
        this.mMuted = muted;
    }

    public boolean isMuted() {
        return this.mMuted;
    }

    public long getStartTime() {
        return this.mStartTimeMs;
    }

    public long getDuration() {
        return this.mDurationMs;
    }

    public long getTimelineDuration() {
        return this.mTimelineDurationMs;
    }

    public void setExtractBoundaries(long beginMs, long endMs) {
        if (beginMs > this.mDurationMs) {
            throw new IllegalArgumentException("Invalid start time");
        }
        if (endMs > this.mDurationMs) {
            throw new IllegalArgumentException("Invalid end time");
        }
        if (beginMs < 0) {
            throw new IllegalArgumentException("Invalid start time; is < 0");
        }
        if (endMs < 0) {
            throw new IllegalArgumentException("Invalid end time; is < 0");
        }
        this.mMANativeHelper.setGeneratePreview(true);
        this.mBeginBoundaryTimeMs = beginMs;
        this.mEndBoundaryTimeMs = endMs;
        this.mTimelineDurationMs = this.mEndBoundaryTimeMs - this.mBeginBoundaryTimeMs;
    }

    public long getBoundaryBeginTime() {
        return this.mBeginBoundaryTimeMs;
    }

    public long getBoundaryEndTime() {
        return this.mEndBoundaryTimeMs;
    }

    public void enableLoop() {
        if (!this.mLoop) {
            this.mMANativeHelper.setGeneratePreview(true);
            this.mLoop = true;
        }
    }

    public void disableLoop() {
        if (this.mLoop) {
            this.mMANativeHelper.setGeneratePreview(true);
            this.mLoop = false;
        }
    }

    public boolean isLooping() {
        return this.mLoop;
    }

    public void disableDucking() {
        if (this.mIsDuckingEnabled) {
            this.mMANativeHelper.setGeneratePreview(true);
            this.mIsDuckingEnabled = false;
        }
    }

    public void enableDucking(int threshold, int duckedTrackVolume) {
        if (threshold < 0 || threshold > 90) {
            throw new IllegalArgumentException("Invalid threshold value: " + threshold);
        }
        if (duckedTrackVolume < 0 || duckedTrackVolume > 100) {
            throw new IllegalArgumentException("Invalid duckedTrackVolume value: " + duckedTrackVolume);
        }
        this.mMANativeHelper.setGeneratePreview(true);
        this.mDuckingThreshold = threshold;
        this.mDuckedTrackVolume = duckedTrackVolume;
        this.mIsDuckingEnabled = true;
    }

    public boolean isDuckingEnabled() {
        return this.mIsDuckingEnabled;
    }

    public int getDuckingThreshhold() {
        return this.mDuckingThreshold;
    }

    public int getDuckedTrackVolume() {
        return this.mDuckedTrackVolume;
    }

    public void extractAudioWaveform(ExtractAudioWaveformProgressListener listener) throws IOException {
        int frameDuration;
        int sampleCount;
        if (this.mAudioWaveformFilename == null) {
            String projectPath = this.mMANativeHelper.getProjectPath();
            String audioWaveFilename = String.format(projectPath + "/audioWaveformFile-" + getId() + ".dat", new Object[0]);
            int codecType = this.mMANativeHelper.getAudioCodecType(this.mAudioType);
            switch (codecType) {
                case 1:
                    frameDuration = 5;
                    sampleCount = 160;
                    break;
                case 2:
                    frameDuration = 32;
                    sampleCount = 1024;
                    break;
                case 3:
                case 4:
                case 6:
                case 7:
                default:
                    throw new IllegalStateException("Unsupported codec type: " + codecType);
                case 5:
                    frameDuration = 36;
                    sampleCount = 1152;
                    break;
                case 8:
                    frameDuration = 10;
                    sampleCount = 320;
                    break;
            }
            this.mMANativeHelper.generateAudioGraph(this.mUniqueId, this.mFilename, audioWaveFilename, frameDuration, 2, sampleCount, listener, false);
            this.mAudioWaveformFilename = audioWaveFilename;
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
            this.mWaveformData = null;
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

    public boolean equals(Object object) {
        if (!(object instanceof AudioTrack)) {
            return false;
        }
        return this.mUniqueId.equals(((AudioTrack) object).mUniqueId);
    }

    public int hashCode() {
        return this.mUniqueId.hashCode();
    }
}