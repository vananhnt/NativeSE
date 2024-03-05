package android.media.videoeditor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.videoeditor.MediaItem;
import android.media.videoeditor.VideoEditor;
import android.util.Log;
import android.util.Pair;
import android.view.Surface;
import gov.nist.core.Separators;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.IntBuffer;
import java.util.List;
import java.util.concurrent.Semaphore;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: MediaArtistNativeHelper.class */
public class MediaArtistNativeHelper {
    private static final String TAG = "MediaArtistNativeHelper";
    private static final int MAX_THUMBNAIL_PERMITTED = 8;
    public static final int TASK_LOADING_SETTINGS = 1;
    public static final int TASK_ENCODING = 2;
    private static final Paint sResizePaint;
    private final VideoEditor mVideoEditor;
    private final Semaphore mLock;
    private EditSettings mStoryBoardSettings;
    private String mOutputFilename;
    private EditSettings mPreviewEditSettings;
    private int mProgressToApp;
    private String mRenderPreviewOverlayFile;
    private int mRenderPreviewRenderingMode;
    private boolean mIsFirstProgress;
    private static final String AUDIO_TRACK_PCM_FILE = "AudioPcm.pcm";
    public static final int PROCESSING_NONE = 0;
    public static final int PROCESSING_AUDIO_PCM = 1;
    public static final int PROCESSING_TRANSITION = 2;
    public static final int PROCESSING_KENBURNS = 3;
    public static final int PROCESSING_INTERMEDIATE1 = 11;
    public static final int PROCESSING_INTERMEDIATE2 = 12;
    public static final int PROCESSING_INTERMEDIATE3 = 13;
    public static final int PROCESSING_EXPORT = 20;
    private int mProcessingState;
    private Object mProcessingObject;
    private VideoEditor.PreviewProgressListener mPreviewProgressListener;
    private VideoEditor.ExportProgressListener mExportProgressListener;
    private ExtractAudioWaveformProgressListener mExtractAudioWaveformProgressListener;
    private VideoEditor.MediaProcessingProgressListener mMediaProcessingProgressListener;
    private final String mProjectPath;
    private long mPreviewProgress;
    private String mAudioTrackPCMFilePath;
    private int mManualEditContext;
    private PreviewClipProperties mClipProperties = null;
    private AudioSettings mAudioSettings = null;
    private AudioTrack mAudioTrack = null;
    private boolean mInvalidatePreviewArray = true;
    private boolean mRegenerateAudio = true;
    private String mExportFilename = null;
    private int mExportVideoCodec = 0;
    private int mExportAudioCodec = 0;
    private int mTotalClips = 0;
    private boolean mErrorFlagSet = false;

    /* loaded from: MediaArtistNativeHelper$AlphaMagicSettings.class */
    public static class AlphaMagicSettings {
        public String file;
        public int blendingPercent;
        public boolean invertRotation;
        public int rgbWidth;
        public int rgbHeight;
    }

    /* loaded from: MediaArtistNativeHelper$AudioEffect.class */
    public static class AudioEffect {
        public static final int NONE = 0;
        public static final int FADE_IN = 8;
        public static final int FADE_OUT = 16;
    }

    /* loaded from: MediaArtistNativeHelper$AudioSettings.class */
    public static class AudioSettings {
        String pFile;
        String Id;
        boolean bRemoveOriginal;
        int channels;
        int Fs;
        int ExtendedFs;
        long startMs;
        long beginCutTime;
        long endCutTime;
        int fileType;
        int volume;
        boolean loop;
        int ducking_threshold;
        int ducking_lowVolume;
        boolean bInDucking_enable;
        String pcmFilePath;
    }

    /* loaded from: MediaArtistNativeHelper$AudioTransition.class */
    public static final class AudioTransition {
        public static final int NONE = 0;
        public static final int CROSS_FADE = 1;
    }

    /* loaded from: MediaArtistNativeHelper$BackgroundMusicSettings.class */
    public static class BackgroundMusicSettings {
        public String file;
        public int fileType;
        public long insertionTime;
        public int volumePercent;
        public long beginLoop;
        public long endLoop;
        public boolean enableDucking;
        public int duckingThreshold;
        public int lowVolume;
        public boolean isLooping;
    }

    /* loaded from: MediaArtistNativeHelper$ClipSettings.class */
    public static class ClipSettings {
        public String clipPath;
        public String clipDecodedPath;
        public String clipOriginalPath;
        public int fileType;
        public int beginCutTime;
        public int endCutTime;
        public int beginCutPercent;
        public int endCutPercent;
        public boolean panZoomEnabled;
        public int panZoomPercentStart;
        public int panZoomTopLeftXStart;
        public int panZoomTopLeftYStart;
        public int panZoomPercentEnd;
        public int panZoomTopLeftXEnd;
        public int panZoomTopLeftYEnd;
        public int mediaRendering;
        public int rgbWidth;
        public int rgbHeight;
        public int rotationDegree;
    }

    /* loaded from: MediaArtistNativeHelper$EditSettings.class */
    public static class EditSettings {
        public ClipSettings[] clipSettingsArray;
        public TransitionSettings[] transitionSettingsArray;
        public EffectSettings[] effectSettingsArray;
        public int videoFrameRate;
        public String outputFile;
        public int videoFrameSize;
        public int videoFormat;
        public int videoProfile;
        public int videoLevel;
        public int audioFormat;
        public int audioSamplingFreq;
        public int maxFileSize;
        public int audioChannels;
        public int videoBitrate;
        public int audioBitrate;
        public BackgroundMusicSettings backgroundMusicSettings;
        public int primaryTrackVolume;
    }

    /* loaded from: MediaArtistNativeHelper$EffectSettings.class */
    public static class EffectSettings {
        public int startTime;
        public int duration;
        public int videoEffectType;
        public int audioEffectType;
        public int startPercent;
        public int durationPercent;
        public String framingFile;
        public int[] framingBuffer;
        public int bitmapType;
        public int width;
        public int height;
        public int topLeftX;
        public int topLeftY;
        public boolean framingResize;
        public int framingScaledSize;
        public String text;
        public String textRenderingData;
        public int textBufferWidth;
        public int textBufferHeight;
        public int fiftiesFrameRate;
        public int rgb16InputColor;
        public int alphaBlendingStartPercent;
        public int alphaBlendingMiddlePercent;
        public int alphaBlendingEndPercent;
        public int alphaBlendingFadeInTimePercent;
        public int alphaBlendingFadeOutTimePercent;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: MediaArtistNativeHelper$NativeGetPixelsListCallback.class */
    public interface NativeGetPixelsListCallback {
        void onThumbnail(int i);
    }

    /* loaded from: MediaArtistNativeHelper$OnProgressUpdateListener.class */
    public interface OnProgressUpdateListener {
        void OnProgressUpdate(int i, int i2);
    }

    /* loaded from: MediaArtistNativeHelper$PreviewClipProperties.class */
    public static class PreviewClipProperties {
        public Properties[] clipProperties;
    }

    /* loaded from: MediaArtistNativeHelper$PreviewClips.class */
    public static class PreviewClips {
        public String clipPath;
        public int fileType;
        public long beginPlayTime;
        public long endPlayTime;
        public int mediaRendering;
    }

    /* loaded from: MediaArtistNativeHelper$PreviewSettings.class */
    public static class PreviewSettings {
        public PreviewClips[] previewClipsArray;
        public EffectSettings[] effectSettingsArray;
    }

    /* loaded from: MediaArtistNativeHelper$Properties.class */
    public static class Properties {
        public int duration;
        public int fileType;
        public int videoFormat;
        public int videoDuration;
        public int videoBitrate;
        public int width;
        public int height;
        public float averageFrameRate;
        public int profile;
        public int level;
        public boolean profileSupported;
        public boolean levelSupported;
        public int audioFormat;
        public int audioDuration;
        public int audioBitrate;
        public int audioChannels;
        public int audioSamplingFrequency;
        public int audioVolumeValue;
        public int videoRotation;
        public String Id;
    }

    /* loaded from: MediaArtistNativeHelper$SlideDirection.class */
    public static final class SlideDirection {
        public static final int RIGHT_OUT_LEFT_IN = 0;
        public static final int LEFT_OUT_RIGTH_IN = 1;
        public static final int TOP_OUT_BOTTOM_IN = 2;
        public static final int BOTTOM_OUT_TOP_IN = 3;
    }

    /* loaded from: MediaArtistNativeHelper$SlideTransitionSettings.class */
    public static class SlideTransitionSettings {
        public int direction;
    }

    /* loaded from: MediaArtistNativeHelper$TransitionBehaviour.class */
    public static final class TransitionBehaviour {
        public static final int SPEED_UP = 0;
        public static final int LINEAR = 1;
        public static final int SPEED_DOWN = 2;
        public static final int SLOW_MIDDLE = 3;
        public static final int FAST_MIDDLE = 4;
    }

    /* loaded from: MediaArtistNativeHelper$TransitionSettings.class */
    public static class TransitionSettings {
        public int duration;
        public int videoTransitionType;
        public int audioTransitionType;
        public int transitionBehaviour;
        public AlphaMagicSettings alphaSettings;
        public SlideTransitionSettings slideSettings;
    }

    /* loaded from: MediaArtistNativeHelper$VideoEffect.class */
    public static class VideoEffect {
        public static final int NONE = 0;
        public static final int FADE_FROM_BLACK = 8;
        public static final int FADE_TO_BLACK = 16;
        public static final int EXTERNAL = 256;
        public static final int BLACK_AND_WHITE = 257;
        public static final int PINK = 258;
        public static final int GREEN = 259;
        public static final int SEPIA = 260;
        public static final int NEGATIVE = 261;
        public static final int FRAMING = 262;
        public static final int TEXT = 263;
        public static final int ZOOM_IN = 264;
        public static final int ZOOM_OUT = 265;
        public static final int FIFTIES = 266;
        public static final int COLORRGB16 = 267;
        public static final int GRADIENT = 268;
    }

    /* loaded from: MediaArtistNativeHelper$VideoTransition.class */
    public static class VideoTransition {
        public static final int NONE = 0;
        public static final int CROSS_FADE = 1;
        public static final int EXTERNAL = 256;
        public static final int ALPHA_MAGIC = 257;
        public static final int SLIDE_TRANSITION = 258;
        public static final int FADE_BLACK = 259;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public native Properties getMediaProperties(String str) throws IllegalArgumentException, IllegalStateException, RuntimeException, Exception;

    private static native Version getVersion() throws RuntimeException;

    private native int nativeGetPixels(String str, int[] iArr, int i, int i2, long j);

    private native int nativeGetPixelsList(String str, int[] iArr, int i, int i2, int i3, long j, long j2, int[] iArr2, NativeGetPixelsListCallback nativeGetPixelsListCallback);

    private native void release() throws IllegalStateException, RuntimeException;

    private native void nativeClearSurface(Surface surface);

    private native void stopEncoding() throws IllegalStateException, RuntimeException;

    private native void _init(String str, String str2) throws IllegalArgumentException, IllegalStateException, RuntimeException;

    private native void nativeStartPreview(Surface surface, long j, long j2, int i, boolean z) throws IllegalArgumentException, IllegalStateException, RuntimeException;

    private native void nativePopulateSettings(EditSettings editSettings, PreviewClipProperties previewClipProperties, AudioSettings audioSettings) throws IllegalArgumentException, IllegalStateException, RuntimeException;

    private native int nativeRenderPreviewFrame(Surface surface, long j, int i, int i2) throws IllegalArgumentException, IllegalStateException, RuntimeException;

    private native int nativeRenderMediaItemPreviewFrame(Surface surface, String str, int i, int i2, int i3, int i4, long j) throws IllegalArgumentException, IllegalStateException, RuntimeException;

    private native int nativeStopPreview();

    private native int nativeGenerateAudioGraph(String str, String str2, int i, int i2, int i3);

    private native int nativeGenerateRawAudio(String str, String str2);

    private native int nativeGenerateClip(EditSettings editSettings) throws IllegalArgumentException, IllegalStateException, RuntimeException;

    static {
        System.loadLibrary("videoeditor_jni");
        sResizePaint = new Paint(2);
    }

    /* loaded from: MediaArtistNativeHelper$Version.class */
    public final class Version {
        public int major;
        public int minor;
        public int revision;
        private static final int VIDEOEDITOR_MAJOR_VERSION = 0;
        private static final int VIDEOEDITOR_MINOR_VERSION = 0;
        private static final int VIDEOEDITOR_REVISION_VERSION = 1;

        public Version() {
        }

        public Version getVersion() {
            Version version = new Version();
            version.major = 0;
            version.minor = 0;
            version.revision = 1;
            return version;
        }
    }

    /* loaded from: MediaArtistNativeHelper$AudioFormat.class */
    public final class AudioFormat {
        public static final int NO_AUDIO = 0;
        public static final int AMR_NB = 1;
        public static final int AAC = 2;
        public static final int AAC_PLUS = 3;
        public static final int ENHANCED_AAC_PLUS = 4;
        public static final int MP3 = 5;
        public static final int EVRC = 6;
        public static final int PCM = 7;
        public static final int NULL_AUDIO = 254;
        public static final int UNSUPPORTED_AUDIO = 255;

        public AudioFormat() {
        }
    }

    /* loaded from: MediaArtistNativeHelper$AudioSamplingFrequency.class */
    public final class AudioSamplingFrequency {
        public static final int FREQ_DEFAULT = 0;
        public static final int FREQ_8000 = 8000;
        public static final int FREQ_11025 = 11025;
        public static final int FREQ_12000 = 12000;
        public static final int FREQ_16000 = 16000;
        public static final int FREQ_22050 = 22050;
        public static final int FREQ_24000 = 24000;
        public static final int FREQ_32000 = 32000;
        public static final int FREQ_44100 = 44100;
        public static final int FREQ_48000 = 48000;

        public AudioSamplingFrequency() {
        }
    }

    /* loaded from: MediaArtistNativeHelper$Bitrate.class */
    public final class Bitrate {
        public static final int VARIABLE = -1;
        public static final int UNDEFINED = 0;
        public static final int BR_9_2_KBPS = 9200;
        public static final int BR_12_2_KBPS = 12200;
        public static final int BR_16_KBPS = 16000;
        public static final int BR_24_KBPS = 24000;
        public static final int BR_32_KBPS = 32000;
        public static final int BR_48_KBPS = 48000;
        public static final int BR_64_KBPS = 64000;
        public static final int BR_96_KBPS = 96000;
        public static final int BR_128_KBPS = 128000;
        public static final int BR_192_KBPS = 192000;
        public static final int BR_256_KBPS = 256000;
        public static final int BR_288_KBPS = 288000;
        public static final int BR_384_KBPS = 384000;
        public static final int BR_512_KBPS = 512000;
        public static final int BR_800_KBPS = 800000;
        public static final int BR_2_MBPS = 2000000;
        public static final int BR_5_MBPS = 5000000;
        public static final int BR_8_MBPS = 8000000;

        public Bitrate() {
        }
    }

    /* loaded from: MediaArtistNativeHelper$FileType.class */
    public final class FileType {
        public static final int THREE_GPP = 0;
        public static final int MP4 = 1;
        public static final int AMR = 2;
        public static final int MP3 = 3;
        public static final int PCM = 4;
        public static final int JPG = 5;
        public static final int GIF = 7;
        public static final int PNG = 8;
        public static final int M4V = 10;
        public static final int UNSUPPORTED = 255;

        public FileType() {
        }
    }

    /* loaded from: MediaArtistNativeHelper$MediaRendering.class */
    public final class MediaRendering {
        public static final int RESIZING = 0;
        public static final int CROPPING = 1;
        public static final int BLACK_BORDERS = 2;

        public MediaRendering() {
        }
    }

    /* loaded from: MediaArtistNativeHelper$Result.class */
    public final class Result {
        public static final int NO_ERROR = 0;
        public static final int ERR_FILE_NOT_FOUND = 1;
        public static final int ERR_BUFFER_OUT_TOO_SMALL = 2;
        public static final int ERR_INVALID_FILE_TYPE = 3;
        public static final int ERR_INVALID_EFFECT_KIND = 4;
        public static final int ERR_INVALID_VIDEO_EFFECT_TYPE = 5;
        public static final int ERR_INVALID_AUDIO_EFFECT_TYPE = 6;
        public static final int ERR_INVALID_VIDEO_TRANSITION_TYPE = 7;
        public static final int ERR_INVALID_AUDIO_TRANSITION_TYPE = 8;
        public static final int ERR_INVALID_VIDEO_ENCODING_FRAME_RATE = 9;
        public static final int ERR_EXTERNAL_EFFECT_NULL = 10;
        public static final int ERR_EXTERNAL_TRANSITION_NULL = 11;
        public static final int ERR_BEGIN_CUT_LARGER_THAN_DURATION = 12;
        public static final int ERR_BEGIN_CUT_LARGER_THAN_END_CUT = 13;
        public static final int ERR_OVERLAPPING_TRANSITIONS = 14;
        public static final int ERR_ANALYSIS_DATA_SIZE_TOO_SMALL = 15;
        public static final int ERR_INVALID_3GPP_FILE = 16;
        public static final int ERR_UNSUPPORTED_INPUT_VIDEO_FORMAT = 17;
        public static final int ERR_UNSUPPORTED_INPUT_AUDIO_FORMAT = 18;
        public static final int ERR_AMR_EDITING_UNSUPPORTED = 19;
        public static final int ERR_INPUT_VIDEO_AU_TOO_LARGE = 20;
        public static final int ERR_INPUT_AUDIO_AU_TOO_LARGE = 21;
        public static final int ERR_INPUT_AUDIO_CORRUPTED_AU = 22;
        public static final int ERR_ENCODER_ACCES_UNIT_ERROR = 23;
        public static final int ERR_EDITING_UNSUPPORTED_VIDEO_FORMAT = 24;
        public static final int ERR_EDITING_UNSUPPORTED_H263_PROFILE = 25;
        public static final int ERR_EDITING_UNSUPPORTED_MPEG4_PROFILE = 26;
        public static final int ERR_EDITING_UNSUPPORTED_MPEG4_RVLC = 27;
        public static final int ERR_EDITING_UNSUPPORTED_AUDIO_FORMAT = 28;
        public static final int ERR_EDITING_NO_SUPPORTED_STREAM_IN_FILE = 29;
        public static final int ERR_EDITING_NO_SUPPORTED_VIDEO_STREAM_IN_FILE = 30;
        public static final int ERR_INVALID_CLIP_ANALYSIS_VERSION = 31;
        public static final int ERR_INVALID_CLIP_ANALYSIS_PLATFORM = 32;
        public static final int ERR_INCOMPATIBLE_VIDEO_FORMAT = 33;
        public static final int ERR_INCOMPATIBLE_VIDEO_FRAME_SIZE = 34;
        public static final int ERR_INCOMPATIBLE_VIDEO_TIME_SCALE = 35;
        public static final int ERR_INCOMPATIBLE_VIDEO_DATA_PARTITIONING = 36;
        public static final int ERR_UNSUPPORTED_MP3_ASSEMBLY = 37;
        public static final int ERR_NO_SUPPORTED_STREAM_IN_FILE = 38;
        public static final int ERR_ADDVOLUME_EQUALS_ZERO = 39;
        public static final int ERR_ADDCTS_HIGHER_THAN_VIDEO_DURATION = 40;
        public static final int ERR_UNDEFINED_AUDIO_TRACK_FILE_FORMAT = 41;
        public static final int ERR_UNSUPPORTED_ADDED_AUDIO_STREAM = 42;
        public static final int ERR_AUDIO_MIXING_UNSUPPORTED = 43;
        public static final int ERR_AUDIO_MIXING_MP3_UNSUPPORTED = 44;
        public static final int ERR_FEATURE_UNSUPPORTED_WITH_AUDIO_TRACK = 45;
        public static final int ERR_FEATURE_UNSUPPORTED_WITH_AAC = 46;
        public static final int ERR_AUDIO_CANNOT_BE_MIXED = 47;
        public static final int ERR_ONLY_AMRNB_INPUT_CAN_BE_MIXED = 48;
        public static final int ERR_FEATURE_UNSUPPORTED_WITH_EVRC = 49;
        public static final int ERR_H263_PROFILE_NOT_SUPPORTED = 51;
        public static final int ERR_NO_SUPPORTED_VIDEO_STREAM_IN_FILE = 52;
        public static final int WAR_TRANSCODING_NECESSARY = 53;
        public static final int WAR_MAX_OUTPUT_SIZE_EXCEEDED = 54;
        public static final int WAR_TIMESCALE_TOO_BIG = 55;
        public static final int ERR_CLOCK_BAD_REF_YEAR = 56;
        public static final int ERR_DIR_OPEN_FAILED = 57;
        public static final int ERR_DIR_READ_FAILED = 58;
        public static final int ERR_DIR_NO_MORE_ENTRY = 59;
        public static final int ERR_PARAMETER = 60;
        public static final int ERR_STATE = 61;
        public static final int ERR_ALLOC = 62;
        public static final int ERR_BAD_CONTEXT = 63;
        public static final int ERR_CONTEXT_FAILED = 64;
        public static final int ERR_BAD_STREAM_ID = 65;
        public static final int ERR_BAD_OPTION_ID = 66;
        public static final int ERR_WRITE_ONLY = 67;
        public static final int ERR_READ_ONLY = 68;
        public static final int ERR_NOT_IMPLEMENTED = 69;
        public static final int ERR_UNSUPPORTED_MEDIA_TYPE = 70;
        public static final int WAR_NO_DATA_YET = 71;
        public static final int WAR_NO_MORE_STREAM = 72;
        public static final int WAR_INVALID_TIME = 73;
        public static final int WAR_NO_MORE_AU = 74;
        public static final int WAR_TIME_OUT = 75;
        public static final int WAR_BUFFER_FULL = 76;
        public static final int WAR_REDIRECT = 77;
        public static final int WAR_TOO_MUCH_STREAMS = 78;
        public static final int ERR_FILE_LOCKED = 79;
        public static final int ERR_FILE_BAD_MODE_ACCESS = 80;
        public static final int ERR_FILE_INVALID_POSITION = 81;
        public static final int ERR_STR_BAD_STRING = 94;
        public static final int ERR_STR_CONV_FAILED = 95;
        public static final int ERR_STR_OVERFLOW = 96;
        public static final int ERR_STR_BAD_ARGS = 97;
        public static final int WAR_STR_OVERFLOW = 98;
        public static final int WAR_STR_NOT_FOUND = 99;
        public static final int ERR_THREAD_NOT_STARTED = 100;
        public static final int WAR_TRANSCODING_DONE = 101;
        public static final int WAR_MEDIATYPE_NOT_SUPPORTED = 102;
        public static final int ERR_INPUT_FILE_CONTAINS_NO_SUPPORTED_STREAM = 103;
        public static final int ERR_INVALID_INPUT_FILE = 104;
        public static final int ERR_UNDEFINED_OUTPUT_VIDEO_FORMAT = 105;
        public static final int ERR_UNDEFINED_OUTPUT_VIDEO_FRAME_SIZE = 106;
        public static final int ERR_UNDEFINED_OUTPUT_VIDEO_FRAME_RATE = 107;
        public static final int ERR_UNDEFINED_OUTPUT_AUDIO_FORMAT = 108;
        public static final int ERR_INVALID_VIDEO_FRAME_SIZE_FOR_H263 = 109;
        public static final int ERR_INVALID_VIDEO_FRAME_RATE_FOR_H263 = 110;
        public static final int ERR_DURATION_IS_NULL = 111;
        public static final int ERR_H263_FORBIDDEN_IN_MP4_FILE = 112;
        public static final int ERR_INVALID_AAC_SAMPLING_FREQUENCY = 113;
        public static final int ERR_AUDIO_CONVERSION_FAILED = 114;
        public static final int ERR_BEGIN_CUT_EQUALS_END_CUT = 115;
        public static final int ERR_END_CUT_SMALLER_THAN_BEGIN_CUT = 116;
        public static final int ERR_MAXFILESIZE_TOO_SMALL = 117;
        public static final int ERR_VIDEOBITRATE_TOO_LOW = 118;
        public static final int ERR_AUDIOBITRATE_TOO_LOW = 119;
        public static final int ERR_VIDEOBITRATE_TOO_HIGH = 120;
        public static final int ERR_AUDIOBITRATE_TOO_HIGH = 121;
        public static final int ERR_OUTPUT_FILE_SIZE_TOO_SMALL = 122;
        public static final int ERR_READER_UNKNOWN_STREAM_TYPE = 123;
        public static final int WAR_READER_NO_METADATA = 124;
        public static final int WAR_READER_INFORMATION_NOT_PRESENT = 125;
        public static final int WAR_WRITER_STOP_REQ = 131;
        public static final int WAR_VIDEORENDERER_NO_NEW_FRAME = 132;
        public static final int WAR_DEBLOCKING_FILTER_NOT_IMPLEMENTED = 133;
        public static final int ERR_DECODER_H263_PROFILE_NOT_SUPPORTED = 134;
        public static final int ERR_DECODER_H263_NOT_BASELINE = 135;
        public static final int ERR_NOMORE_SPACE_FOR_FILE = 136;
        public static final int ERR_INTERNAL = 255;

        public Result() {
        }
    }

    /* loaded from: MediaArtistNativeHelper$VideoFormat.class */
    public final class VideoFormat {
        public static final int NO_VIDEO = 0;
        public static final int H263 = 1;
        public static final int H264 = 2;
        public static final int MPEG4 = 3;
        public static final int NULL_VIDEO = 254;
        public static final int UNSUPPORTED = 255;

        public VideoFormat() {
        }
    }

    /* loaded from: MediaArtistNativeHelper$VideoFrameSize.class */
    public final class VideoFrameSize {
        public static final int SIZE_UNDEFINED = -1;
        public static final int SQCIF = 0;
        public static final int QQVGA = 1;
        public static final int QCIF = 2;
        public static final int QVGA = 3;
        public static final int CIF = 4;
        public static final int VGA = 5;
        public static final int WVGA = 6;
        public static final int NTSC = 7;
        public static final int nHD = 8;
        public static final int WVGA16x9 = 9;
        public static final int V720p = 10;
        public static final int W720p = 11;
        public static final int S720p = 12;
        public static final int V1080p = 13;

        public VideoFrameSize() {
        }
    }

    /* loaded from: MediaArtistNativeHelper$VideoFrameRate.class */
    public final class VideoFrameRate {
        public static final int FR_5_FPS = 0;
        public static final int FR_7_5_FPS = 1;
        public static final int FR_10_FPS = 2;
        public static final int FR_12_5_FPS = 3;
        public static final int FR_15_FPS = 4;
        public static final int FR_20_FPS = 5;
        public static final int FR_25_FPS = 6;
        public static final int FR_30_FPS = 7;

        public VideoFrameRate() {
        }
    }

    public MediaArtistNativeHelper(String projectPath, Semaphore lock, VideoEditor veObj) {
        this.mProjectPath = projectPath;
        if (veObj != null) {
            this.mVideoEditor = veObj;
            if (this.mStoryBoardSettings == null) {
                this.mStoryBoardSettings = new EditSettings();
            }
            this.mLock = lock;
            _init(this.mProjectPath, "null");
            this.mAudioTrackPCMFilePath = null;
            return;
        }
        this.mVideoEditor = null;
        throw new IllegalArgumentException("video editor object is null");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getProjectPath() {
        return this.mProjectPath;
    }

    String getProjectAudioTrackPCMFilePath() {
        return this.mAudioTrackPCMFilePath;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void invalidatePcmFile() {
        if (this.mAudioTrackPCMFilePath != null) {
            new File(this.mAudioTrackPCMFilePath).delete();
            this.mAudioTrackPCMFilePath = null;
        }
    }

    private void onProgressUpdate(int taskId, int progress) {
        int action;
        if (this.mProcessingState == 20) {
            if (this.mExportProgressListener != null && this.mProgressToApp < progress) {
                this.mExportProgressListener.onProgress(this.mVideoEditor, this.mOutputFilename, progress);
                this.mProgressToApp = progress;
                return;
            }
            return;
        }
        int actualProgress = 0;
        if (this.mProcessingState == 1) {
            action = 2;
        } else {
            action = 1;
        }
        switch (this.mProcessingState) {
            case 0:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            default:
                Log.e(TAG, "ERROR unexpected State=" + this.mProcessingState);
                return;
            case 1:
                actualProgress = progress;
                break;
            case 2:
                actualProgress = progress;
                break;
            case 3:
                actualProgress = progress;
                break;
            case 11:
                if (progress == 0 && this.mProgressToApp != 0) {
                    this.mProgressToApp = 0;
                }
                if (progress != 0 || this.mProgressToApp != 0) {
                    actualProgress = progress / 4;
                    break;
                }
                break;
            case 12:
                if (progress != 0 || this.mProgressToApp != 0) {
                    actualProgress = 25 + (progress / 4);
                    break;
                }
                break;
            case 13:
                if (progress != 0 || this.mProgressToApp != 0) {
                    actualProgress = 50 + (progress / 2);
                    break;
                }
                break;
        }
        if (this.mProgressToApp != actualProgress && actualProgress != 0) {
            this.mProgressToApp = actualProgress;
            if (this.mMediaProcessingProgressListener != null) {
                this.mMediaProcessingProgressListener.onProgress(this.mProcessingObject, action, actualProgress);
            }
        }
        if (this.mProgressToApp == 0) {
            if (this.mMediaProcessingProgressListener != null) {
                this.mMediaProcessingProgressListener.onProgress(this.mProcessingObject, action, actualProgress);
            }
            this.mProgressToApp = 1;
        }
    }

    private void onPreviewProgressUpdate(int progress, boolean isFinished, boolean updateOverlay, String filename, int renderingMode, int error) {
        VideoEditor.OverlayData overlayData;
        if (this.mPreviewProgressListener != null) {
            if (this.mIsFirstProgress) {
                this.mPreviewProgressListener.onStart(this.mVideoEditor);
                this.mIsFirstProgress = false;
            }
            if (updateOverlay) {
                overlayData = new VideoEditor.OverlayData();
                if (filename != null) {
                    overlayData.set(BitmapFactory.decodeFile(filename), renderingMode);
                } else {
                    overlayData.setClear();
                }
            } else {
                overlayData = null;
            }
            if (progress != 0) {
                this.mPreviewProgress = progress;
            }
            if (isFinished) {
                this.mPreviewProgressListener.onStop(this.mVideoEditor);
            } else if (error != 0) {
                this.mPreviewProgressListener.onError(this.mVideoEditor, error);
            } else {
                this.mPreviewProgressListener.onProgress(this.mVideoEditor, progress, overlayData);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void releaseNativeHelper() throws InterruptedException {
        release();
    }

    private void onAudioGraphExtractProgressUpdate(int progress, boolean isVideo) {
        if (this.mExtractAudioWaveformProgressListener != null && progress > 0) {
            this.mExtractAudioWaveformProgressListener.onProgress(progress);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public EffectSettings getEffectSettings(EffectColor effects) {
        EffectSettings effectSettings = new EffectSettings();
        effectSettings.startTime = (int) effects.getStartTime();
        effectSettings.duration = (int) effects.getDuration();
        effectSettings.videoEffectType = getEffectColorType(effects);
        effectSettings.audioEffectType = 0;
        effectSettings.startPercent = 0;
        effectSettings.durationPercent = 0;
        effectSettings.framingFile = null;
        effectSettings.topLeftX = 0;
        effectSettings.topLeftY = 0;
        effectSettings.framingResize = false;
        effectSettings.text = null;
        effectSettings.textRenderingData = null;
        effectSettings.textBufferWidth = 0;
        effectSettings.textBufferHeight = 0;
        if (effects.getType() == 5) {
            effectSettings.fiftiesFrameRate = 15;
        } else {
            effectSettings.fiftiesFrameRate = 0;
        }
        if (effectSettings.videoEffectType == 267 || effectSettings.videoEffectType == 268) {
            effectSettings.rgb16InputColor = effects.getColor();
        }
        effectSettings.alphaBlendingStartPercent = 0;
        effectSettings.alphaBlendingMiddlePercent = 0;
        effectSettings.alphaBlendingEndPercent = 0;
        effectSettings.alphaBlendingFadeInTimePercent = 0;
        effectSettings.alphaBlendingFadeOutTimePercent = 0;
        return effectSettings;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public EffectSettings getOverlaySettings(OverlayFrame overlay) {
        int aspectRatio;
        int mediaItemHeight;
        EffectSettings effectSettings = new EffectSettings();
        effectSettings.startTime = (int) overlay.getStartTime();
        effectSettings.duration = (int) overlay.getDuration();
        effectSettings.videoEffectType = 262;
        effectSettings.audioEffectType = 0;
        effectSettings.startPercent = 0;
        effectSettings.durationPercent = 0;
        effectSettings.framingFile = null;
        Bitmap bitmap = overlay.getBitmap();
        if (bitmap != null) {
            effectSettings.framingFile = overlay.getFilename();
            if (effectSettings.framingFile == null) {
                try {
                    overlay.save(this.mProjectPath);
                } catch (IOException e) {
                    Log.e(TAG, "getOverlaySettings : File not found");
                }
                effectSettings.framingFile = overlay.getFilename();
            }
            if (bitmap.getConfig() == Bitmap.Config.ARGB_8888) {
                effectSettings.bitmapType = 6;
            } else if (bitmap.getConfig() == Bitmap.Config.ARGB_4444) {
                effectSettings.bitmapType = 5;
            } else if (bitmap.getConfig() == Bitmap.Config.RGB_565) {
                effectSettings.bitmapType = 4;
            } else if (bitmap.getConfig() == Bitmap.Config.ALPHA_8) {
                throw new RuntimeException("Bitmap config not supported");
            }
            effectSettings.width = bitmap.getWidth();
            effectSettings.height = bitmap.getHeight();
            effectSettings.framingBuffer = new int[effectSettings.width];
            short maxAlpha = 0;
            short minAlpha = 255;
            for (int tmp = 0; tmp < effectSettings.height; tmp++) {
                bitmap.getPixels(effectSettings.framingBuffer, 0, effectSettings.width, 0, tmp, effectSettings.width, 1);
                for (int i = 0; i < effectSettings.width; i++) {
                    short alpha = (short) ((effectSettings.framingBuffer[i] >> 24) & 255);
                    if (alpha > maxAlpha) {
                        maxAlpha = alpha;
                    }
                    if (alpha < minAlpha) {
                        minAlpha = alpha;
                    }
                }
            }
            short alpha2 = (short) ((((short) ((maxAlpha + minAlpha) / 2)) * 100) / 256);
            effectSettings.alphaBlendingEndPercent = alpha2;
            effectSettings.alphaBlendingMiddlePercent = alpha2;
            effectSettings.alphaBlendingStartPercent = alpha2;
            effectSettings.alphaBlendingFadeInTimePercent = 100;
            effectSettings.alphaBlendingFadeOutTimePercent = 100;
            effectSettings.framingBuffer = null;
            effectSettings.width = overlay.getResizedRGBSizeWidth();
            if (effectSettings.width == 0) {
                effectSettings.width = bitmap.getWidth();
            }
            effectSettings.height = overlay.getResizedRGBSizeHeight();
            if (effectSettings.height == 0) {
                effectSettings.height = bitmap.getHeight();
            }
        }
        effectSettings.topLeftX = 0;
        effectSettings.topLeftY = 0;
        effectSettings.framingResize = true;
        effectSettings.text = null;
        effectSettings.textRenderingData = null;
        effectSettings.textBufferWidth = 0;
        effectSettings.textBufferHeight = 0;
        effectSettings.fiftiesFrameRate = 0;
        effectSettings.rgb16InputColor = 0;
        if (overlay.getMediaItem() instanceof MediaImageItem) {
            if (((MediaImageItem) overlay.getMediaItem()).getGeneratedImageClip() != null) {
                mediaItemHeight = ((MediaImageItem) overlay.getMediaItem()).getGeneratedClipHeight();
                aspectRatio = getAspectRatio(((MediaImageItem) overlay.getMediaItem()).getGeneratedClipWidth(), mediaItemHeight);
            } else {
                mediaItemHeight = ((MediaImageItem) overlay.getMediaItem()).getScaledHeight();
                aspectRatio = overlay.getMediaItem().getAspectRatio();
            }
        } else {
            aspectRatio = overlay.getMediaItem().getAspectRatio();
            mediaItemHeight = overlay.getMediaItem().getHeight();
        }
        effectSettings.framingScaledSize = findVideoResolution(aspectRatio, mediaItemHeight);
        return effectSettings;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int nativeHelperGetAspectRatio() {
        return this.mVideoEditor.getAspectRatio();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setAudioCodec(int codec) {
        this.mExportAudioCodec = codec;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setVideoCodec(int codec) {
        this.mExportVideoCodec = codec;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setAudioflag(boolean flag) {
        if (!new File(String.format(this.mProjectPath + Separators.SLASH + AUDIO_TRACK_PCM_FILE, new Object[0])).exists()) {
            flag = true;
        }
        this.mRegenerateAudio = flag;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean getAudioflag() {
        return this.mRegenerateAudio;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int GetClosestVideoFrameRate(int averageFrameRate) {
        if (averageFrameRate >= 25) {
            return 7;
        }
        if (averageFrameRate >= 20) {
            return 6;
        }
        if (averageFrameRate >= 15) {
            return 5;
        }
        if (averageFrameRate >= 12) {
            return 4;
        }
        if (averageFrameRate >= 10) {
            return 3;
        }
        if (averageFrameRate >= 7) {
            return 2;
        }
        if (averageFrameRate >= 5) {
            return 1;
        }
        return -1;
    }

    public void adjustEffectsStartTimeAndDuration(EffectSettings lEffect, int beginCutTime, int endCutTime) {
        if (lEffect.startTime > endCutTime || lEffect.startTime + lEffect.duration <= beginCutTime) {
            lEffect.startTime = 0;
            lEffect.duration = 0;
        } else if (lEffect.startTime < beginCutTime && lEffect.startTime + lEffect.duration > beginCutTime && lEffect.startTime + lEffect.duration <= endCutTime) {
            int effectDuration = lEffect.duration;
            lEffect.startTime = 0;
            lEffect.duration = effectDuration - (beginCutTime - lEffect.startTime);
        } else if (lEffect.startTime >= beginCutTime && lEffect.startTime + lEffect.duration <= endCutTime) {
            int effectStartTime = lEffect.startTime - beginCutTime;
            lEffect.startTime = effectStartTime;
            lEffect.duration = lEffect.duration;
        } else if (lEffect.startTime >= beginCutTime && lEffect.startTime + lEffect.duration > endCutTime) {
            int effectStartTime2 = lEffect.startTime - beginCutTime;
            int effectDuration2 = endCutTime - lEffect.startTime;
            lEffect.startTime = effectStartTime2;
            lEffect.duration = effectDuration2;
        } else if (lEffect.startTime < beginCutTime && lEffect.startTime + lEffect.duration > endCutTime) {
            int effectDuration3 = endCutTime - beginCutTime;
            lEffect.startTime = 0;
            lEffect.duration = effectDuration3;
        }
    }

    public int generateClip(EditSettings editSettings) {
        try {
            int err = nativeGenerateClip(editSettings);
            return err;
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Illegal Argument exception in load settings");
            return -1;
        } catch (IllegalStateException e2) {
            Log.e(TAG, "Illegal state exception in load settings");
            return -1;
        } catch (RuntimeException e3) {
            Log.e(TAG, "Runtime exception in load settings");
            return -1;
        }
    }

    void initClipSettings(ClipSettings lclipSettings) {
        lclipSettings.clipPath = null;
        lclipSettings.clipDecodedPath = null;
        lclipSettings.clipOriginalPath = null;
        lclipSettings.fileType = 0;
        lclipSettings.endCutTime = 0;
        lclipSettings.beginCutTime = 0;
        lclipSettings.beginCutPercent = 0;
        lclipSettings.endCutPercent = 0;
        lclipSettings.panZoomEnabled = false;
        lclipSettings.panZoomPercentStart = 0;
        lclipSettings.panZoomTopLeftXStart = 0;
        lclipSettings.panZoomTopLeftYStart = 0;
        lclipSettings.panZoomPercentEnd = 0;
        lclipSettings.panZoomTopLeftXEnd = 0;
        lclipSettings.panZoomTopLeftYEnd = 0;
        lclipSettings.mediaRendering = 0;
        lclipSettings.rotationDegree = 0;
    }

    String generateEffectClip(MediaItem lMediaItem, ClipSettings lclipSettings, EditSettings e, String uniqueId, int clipNo) {
        EditSettings editSettings = new EditSettings();
        editSettings.clipSettingsArray = new ClipSettings[1];
        editSettings.clipSettingsArray[0] = lclipSettings;
        editSettings.backgroundMusicSettings = null;
        editSettings.transitionSettingsArray = null;
        editSettings.effectSettingsArray = e.effectSettingsArray;
        String EffectClipPath = String.format(this.mProjectPath + Separators.SLASH + "ClipEffectIntermediate_" + lMediaItem.getId() + uniqueId + ".3gp", new Object[0]);
        File tmpFile = new File(EffectClipPath);
        if (tmpFile.exists()) {
            tmpFile.delete();
        }
        int outVideoProfile = VideoEditorProfile.getExportProfile(2);
        int outVideoLevel = VideoEditorProfile.getExportLevel(2);
        editSettings.videoProfile = outVideoProfile;
        editSettings.videoLevel = outVideoLevel;
        if (lMediaItem instanceof MediaVideoItem) {
            MediaVideoItem m = (MediaVideoItem) lMediaItem;
            editSettings.audioFormat = 2;
            editSettings.audioChannels = 2;
            editSettings.audioBitrate = 64000;
            editSettings.audioSamplingFreq = 32000;
            editSettings.videoFormat = 2;
            editSettings.videoFrameRate = 7;
            editSettings.videoFrameSize = findVideoResolution(this.mVideoEditor.getAspectRatio(), m.getHeight());
            editSettings.videoBitrate = findVideoBitrate(editSettings.videoFrameSize);
        } else {
            MediaImageItem m2 = (MediaImageItem) lMediaItem;
            editSettings.audioBitrate = 64000;
            editSettings.audioChannels = 2;
            editSettings.audioFormat = 2;
            editSettings.audioSamplingFreq = 32000;
            editSettings.videoFormat = 2;
            editSettings.videoFrameRate = 7;
            editSettings.videoFrameSize = findVideoResolution(this.mVideoEditor.getAspectRatio(), m2.getScaledHeight());
            editSettings.videoBitrate = findVideoBitrate(editSettings.videoFrameSize);
        }
        editSettings.outputFile = EffectClipPath;
        if (clipNo == 1) {
            this.mProcessingState = 11;
        } else if (clipNo == 2) {
            this.mProcessingState = 12;
        }
        this.mProcessingObject = lMediaItem;
        int err = generateClip(editSettings);
        this.mProcessingState = 0;
        if (err == 0) {
            lclipSettings.clipPath = EffectClipPath;
            lclipSettings.fileType = 0;
            return EffectClipPath;
        }
        throw new RuntimeException("preview generation cannot be completed");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String generateKenBurnsClip(EditSettings e, MediaImageItem m) {
        e.backgroundMusicSettings = null;
        e.transitionSettingsArray = null;
        e.effectSettingsArray = null;
        String output = String.format(this.mProjectPath + Separators.SLASH + "ImageClip-" + m.getId() + ".3gp", new Object[0]);
        File tmpFile = new File(output);
        if (tmpFile.exists()) {
            tmpFile.delete();
        }
        int outVideoProfile = VideoEditorProfile.getExportProfile(2);
        int outVideoLevel = VideoEditorProfile.getExportLevel(2);
        e.videoProfile = outVideoProfile;
        e.videoLevel = outVideoLevel;
        e.outputFile = output;
        e.audioBitrate = 64000;
        e.audioChannels = 2;
        e.audioFormat = 2;
        e.audioSamplingFreq = 32000;
        e.videoFormat = 2;
        e.videoFrameRate = 7;
        e.videoFrameSize = findVideoResolution(this.mVideoEditor.getAspectRatio(), m.getScaledHeight());
        e.videoBitrate = findVideoBitrate(e.videoFrameSize);
        this.mProcessingState = 3;
        this.mProcessingObject = m;
        int err = generateClip(e);
        this.mProcessingState = 0;
        if (err != 0) {
            throw new RuntimeException("preview generation cannot be completed");
        }
        return output;
    }

    private int getTransitionResolution(MediaItem m1, MediaItem m2) {
        int clip1Height = 0;
        int clip2Height = 0;
        int videoSize = 0;
        if (m1 != null && m2 != null) {
            if (m1 instanceof MediaVideoItem) {
                clip1Height = m1.getHeight();
            } else if (m1 instanceof MediaImageItem) {
                clip1Height = ((MediaImageItem) m1).getScaledHeight();
            }
            if (m2 instanceof MediaVideoItem) {
                clip2Height = m2.getHeight();
            } else if (m2 instanceof MediaImageItem) {
                clip2Height = ((MediaImageItem) m2).getScaledHeight();
            }
            videoSize = clip1Height > clip2Height ? findVideoResolution(this.mVideoEditor.getAspectRatio(), clip1Height) : findVideoResolution(this.mVideoEditor.getAspectRatio(), clip2Height);
        } else if (m1 == null && m2 != null) {
            if (m2 instanceof MediaVideoItem) {
                clip2Height = m2.getHeight();
            } else if (m2 instanceof MediaImageItem) {
                clip2Height = ((MediaImageItem) m2).getScaledHeight();
            }
            videoSize = findVideoResolution(this.mVideoEditor.getAspectRatio(), clip2Height);
        } else if (m1 != null && m2 == null) {
            if (m1 instanceof MediaVideoItem) {
                clip1Height = m1.getHeight();
            } else if (m1 instanceof MediaImageItem) {
                clip1Height = ((MediaImageItem) m1).getScaledHeight();
            }
            videoSize = findVideoResolution(this.mVideoEditor.getAspectRatio(), clip1Height);
        }
        return videoSize;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String generateTransitionClip(EditSettings e, String uniqueId, MediaItem m1, MediaItem m2, Transition t) {
        String outputFilename = String.format(this.mProjectPath + Separators.SLASH + uniqueId + ".3gp", new Object[0]);
        int outVideoProfile = VideoEditorProfile.getExportProfile(2);
        int outVideoLevel = VideoEditorProfile.getExportLevel(2);
        e.videoProfile = outVideoProfile;
        e.videoLevel = outVideoLevel;
        e.outputFile = outputFilename;
        e.audioBitrate = 64000;
        e.audioChannels = 2;
        e.audioFormat = 2;
        e.audioSamplingFreq = 32000;
        e.videoFormat = 2;
        e.videoFrameRate = 7;
        e.videoFrameSize = getTransitionResolution(m1, m2);
        e.videoBitrate = findVideoBitrate(e.videoFrameSize);
        if (new File(outputFilename).exists()) {
            new File(outputFilename).delete();
        }
        this.mProcessingState = 13;
        this.mProcessingObject = t;
        int err = generateClip(e);
        this.mProcessingState = 0;
        if (err != 0) {
            throw new RuntimeException("preview generation cannot be completed");
        }
        return outputFilename;
    }

    private int populateEffects(MediaItem m, EffectSettings[] effectSettings, int i, int beginCutTime, int endCutTime, int storyBoardTime) {
        if (m.getBeginTransition() != null && m.getBeginTransition().getDuration() > 0 && m.getEndTransition() != null && m.getEndTransition().getDuration() > 0) {
            beginCutTime = (int) (beginCutTime + m.getBeginTransition().getDuration());
            endCutTime = (int) (endCutTime - m.getEndTransition().getDuration());
        } else if (m.getBeginTransition() == null && m.getEndTransition() != null && m.getEndTransition().getDuration() > 0) {
            endCutTime = (int) (endCutTime - m.getEndTransition().getDuration());
        } else if (m.getEndTransition() == null && m.getBeginTransition() != null && m.getBeginTransition().getDuration() > 0) {
            beginCutTime = (int) (beginCutTime + m.getBeginTransition().getDuration());
        }
        List<Effect> effects = m.getAllEffects();
        List<Overlay> overlays = m.getAllOverlays();
        for (Overlay overlay : overlays) {
            effectSettings[i] = getOverlaySettings((OverlayFrame) overlay);
            adjustEffectsStartTimeAndDuration(effectSettings[i], beginCutTime, endCutTime);
            effectSettings[i].startTime += storyBoardTime;
            i++;
        }
        for (Effect effect : effects) {
            if (effect instanceof EffectColor) {
                effectSettings[i] = getEffectSettings((EffectColor) effect);
                adjustEffectsStartTimeAndDuration(effectSettings[i], beginCutTime, endCutTime);
                effectSettings[i].startTime += storyBoardTime;
                i++;
            }
        }
        return i;
    }

    private void adjustMediaItemBoundary(ClipSettings clipSettings, Properties clipProperties, MediaItem m) {
        if (m.getBeginTransition() != null && m.getBeginTransition().getDuration() > 0 && m.getEndTransition() != null && m.getEndTransition().getDuration() > 0) {
            clipSettings.beginCutTime = (int) (clipSettings.beginCutTime + m.getBeginTransition().getDuration());
            clipSettings.endCutTime = (int) (clipSettings.endCutTime - m.getEndTransition().getDuration());
        } else if (m.getBeginTransition() == null && m.getEndTransition() != null && m.getEndTransition().getDuration() > 0) {
            clipSettings.endCutTime = (int) (clipSettings.endCutTime - m.getEndTransition().getDuration());
        } else if (m.getEndTransition() == null && m.getBeginTransition() != null && m.getBeginTransition().getDuration() > 0) {
            clipSettings.beginCutTime = (int) (clipSettings.beginCutTime + m.getBeginTransition().getDuration());
        }
        clipProperties.duration = clipSettings.endCutTime - clipSettings.beginCutTime;
        if (clipProperties.videoDuration != 0) {
            clipProperties.videoDuration = clipSettings.endCutTime - clipSettings.beginCutTime;
        }
        if (clipProperties.audioDuration != 0) {
            clipProperties.audioDuration = clipSettings.endCutTime - clipSettings.beginCutTime;
        }
    }

    private void generateTransition(Transition transition, EditSettings editSettings, PreviewClipProperties clipPropertiesArray, int index) {
        if (!transition.isGenerated()) {
            transition.generate();
        }
        editSettings.clipSettingsArray[index] = new ClipSettings();
        editSettings.clipSettingsArray[index].clipPath = transition.getFilename();
        editSettings.clipSettingsArray[index].fileType = 0;
        editSettings.clipSettingsArray[index].beginCutTime = 0;
        editSettings.clipSettingsArray[index].endCutTime = (int) transition.getDuration();
        editSettings.clipSettingsArray[index].mediaRendering = 2;
        try {
            clipPropertiesArray.clipProperties[index] = getMediaProperties(transition.getFilename());
            clipPropertiesArray.clipProperties[index].Id = null;
            clipPropertiesArray.clipProperties[index].audioVolumeValue = 100;
            clipPropertiesArray.clipProperties[index].duration = (int) transition.getDuration();
            if (clipPropertiesArray.clipProperties[index].videoDuration != 0) {
                clipPropertiesArray.clipProperties[index].videoDuration = (int) transition.getDuration();
            }
            if (clipPropertiesArray.clipProperties[index].audioDuration != 0) {
                clipPropertiesArray.clipProperties[index].audioDuration = (int) transition.getDuration();
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Unsupported file or file not found");
        }
    }

    private void adjustVolume(MediaItem m, PreviewClipProperties clipProperties, int index) {
        if (!(m instanceof MediaVideoItem)) {
            if (m instanceof MediaImageItem) {
                this.mClipProperties.clipProperties[index].audioVolumeValue = 0;
                return;
            }
            return;
        }
        boolean videoMuted = ((MediaVideoItem) m).isMuted();
        if (!videoMuted) {
            this.mClipProperties.clipProperties[index].audioVolumeValue = ((MediaVideoItem) m).getVolume();
            return;
        }
        this.mClipProperties.clipProperties[index].audioVolumeValue = 0;
    }

    private void checkOddSizeImage(MediaItem m, PreviewClipProperties clipProperties, int index) {
        if (m instanceof MediaImageItem) {
            int width = this.mClipProperties.clipProperties[index].width;
            int height = this.mClipProperties.clipProperties[index].height;
            if (width % 2 != 0) {
                width--;
            }
            if (height % 2 != 0) {
                height--;
            }
            this.mClipProperties.clipProperties[index].width = width;
            this.mClipProperties.clipProperties[index].height = height;
        }
    }

    private int populateMediaItemProperties(MediaItem m, int index, int maxHeight) {
        this.mPreviewEditSettings.clipSettingsArray[index] = new ClipSettings();
        if (m instanceof MediaVideoItem) {
            this.mPreviewEditSettings.clipSettingsArray[index] = ((MediaVideoItem) m).getVideoClipProperties();
            if (((MediaVideoItem) m).getHeight() > maxHeight) {
                maxHeight = ((MediaVideoItem) m).getHeight();
            }
        } else if (m instanceof MediaImageItem) {
            this.mPreviewEditSettings.clipSettingsArray[index] = ((MediaImageItem) m).getImageClipProperties();
            if (((MediaImageItem) m).getScaledHeight() > maxHeight) {
                maxHeight = ((MediaImageItem) m).getScaledHeight();
            }
        }
        if (this.mPreviewEditSettings.clipSettingsArray[index].fileType == 5) {
            this.mPreviewEditSettings.clipSettingsArray[index].clipDecodedPath = ((MediaImageItem) m).getDecodedImageFileName();
            this.mPreviewEditSettings.clipSettingsArray[index].clipOriginalPath = this.mPreviewEditSettings.clipSettingsArray[index].clipPath;
        }
        return maxHeight;
    }

    private void populateBackgroundMusicProperties(List<AudioTrack> mediaBGMList) {
        if (mediaBGMList.size() == 1) {
            this.mAudioTrack = mediaBGMList.get(0);
        } else {
            this.mAudioTrack = null;
        }
        if (this.mAudioTrack != null) {
            this.mAudioSettings = new AudioSettings();
            new Properties();
            this.mAudioSettings.pFile = null;
            this.mAudioSettings.Id = this.mAudioTrack.getId();
            try {
                Properties mAudioProperties = getMediaProperties(this.mAudioTrack.getFilename());
                this.mAudioSettings.bRemoveOriginal = false;
                this.mAudioSettings.channels = mAudioProperties.audioChannels;
                this.mAudioSettings.Fs = mAudioProperties.audioSamplingFrequency;
                this.mAudioSettings.loop = this.mAudioTrack.isLooping();
                this.mAudioSettings.ExtendedFs = 0;
                this.mAudioSettings.pFile = this.mAudioTrack.getFilename();
                this.mAudioSettings.startMs = this.mAudioTrack.getStartTime();
                this.mAudioSettings.beginCutTime = this.mAudioTrack.getBoundaryBeginTime();
                this.mAudioSettings.endCutTime = this.mAudioTrack.getBoundaryEndTime();
                if (this.mAudioTrack.isMuted()) {
                    this.mAudioSettings.volume = 0;
                } else {
                    this.mAudioSettings.volume = this.mAudioTrack.getVolume();
                }
                this.mAudioSettings.fileType = mAudioProperties.fileType;
                this.mAudioSettings.ducking_lowVolume = this.mAudioTrack.getDuckedTrackVolume();
                this.mAudioSettings.ducking_threshold = this.mAudioTrack.getDuckingThreshhold();
                this.mAudioSettings.bInDucking_enable = this.mAudioTrack.isDuckingEnabled();
                this.mAudioTrackPCMFilePath = String.format(this.mProjectPath + Separators.SLASH + AUDIO_TRACK_PCM_FILE, new Object[0]);
                this.mAudioSettings.pcmFilePath = this.mAudioTrackPCMFilePath;
                this.mPreviewEditSettings.backgroundMusicSettings = new BackgroundMusicSettings();
                this.mPreviewEditSettings.backgroundMusicSettings.file = this.mAudioTrackPCMFilePath;
                this.mPreviewEditSettings.backgroundMusicSettings.fileType = mAudioProperties.fileType;
                this.mPreviewEditSettings.backgroundMusicSettings.insertionTime = this.mAudioTrack.getStartTime();
                this.mPreviewEditSettings.backgroundMusicSettings.volumePercent = this.mAudioTrack.getVolume();
                this.mPreviewEditSettings.backgroundMusicSettings.beginLoop = this.mAudioTrack.getBoundaryBeginTime();
                this.mPreviewEditSettings.backgroundMusicSettings.endLoop = this.mAudioTrack.getBoundaryEndTime();
                this.mPreviewEditSettings.backgroundMusicSettings.enableDucking = this.mAudioTrack.isDuckingEnabled();
                this.mPreviewEditSettings.backgroundMusicSettings.duckingThreshold = this.mAudioTrack.getDuckingThreshhold();
                this.mPreviewEditSettings.backgroundMusicSettings.lowVolume = this.mAudioTrack.getDuckedTrackVolume();
                this.mPreviewEditSettings.backgroundMusicSettings.isLooping = this.mAudioTrack.isLooping();
                this.mPreviewEditSettings.primaryTrackVolume = 100;
                this.mProcessingState = 1;
                this.mProcessingObject = this.mAudioTrack;
                return;
            } catch (Exception e) {
                throw new IllegalArgumentException("Unsupported file or file not found");
            }
        }
        this.mAudioSettings = null;
        this.mPreviewEditSettings.backgroundMusicSettings = null;
        this.mAudioTrackPCMFilePath = null;
    }

    private int getTotalEffects(List<MediaItem> mediaItemsList) {
        int totalEffects = 0;
        for (MediaItem t : mediaItemsList) {
            totalEffects = totalEffects + t.getAllEffects().size() + t.getAllOverlays().size();
            for (Effect e : t.getAllEffects()) {
                if (e instanceof EffectKenBurns) {
                    totalEffects--;
                }
            }
        }
        return totalEffects;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void previewStoryBoard(List<MediaItem> mediaItemsList, List<Transition> mediaTransitionList, List<AudioTrack> mediaBGMList, VideoEditor.MediaProcessingProgressListener listener) {
        Transition lTransition;
        if (this.mInvalidatePreviewArray) {
            int previewIndex = 0;
            int storyBoardTime = 0;
            int maxHeight = 0;
            int beginCutTime = 0;
            int endCutTime = 0;
            int effectIndex = 0;
            this.mPreviewEditSettings = new EditSettings();
            this.mClipProperties = new PreviewClipProperties();
            this.mTotalClips = 0;
            this.mTotalClips = mediaItemsList.size();
            for (Transition transition : mediaTransitionList) {
                if (transition.getDuration() > 0) {
                    this.mTotalClips++;
                }
            }
            int totalEffects = getTotalEffects(mediaItemsList);
            this.mPreviewEditSettings.clipSettingsArray = new ClipSettings[this.mTotalClips];
            this.mPreviewEditSettings.effectSettingsArray = new EffectSettings[totalEffects];
            this.mClipProperties.clipProperties = new Properties[this.mTotalClips];
            this.mMediaProcessingProgressListener = listener;
            this.mProgressToApp = 0;
            if (mediaItemsList.size() > 0) {
                int i = 0;
                while (true) {
                    if (i >= mediaItemsList.size()) {
                        break;
                    }
                    MediaItem lMediaItem = mediaItemsList.get(i);
                    if (lMediaItem instanceof MediaVideoItem) {
                        beginCutTime = (int) ((MediaVideoItem) lMediaItem).getBoundaryBeginTime();
                        endCutTime = (int) ((MediaVideoItem) lMediaItem).getBoundaryEndTime();
                    } else if (lMediaItem instanceof MediaImageItem) {
                        beginCutTime = 0;
                        endCutTime = (int) ((MediaImageItem) lMediaItem).getTimelineDuration();
                    }
                    Transition lTransition2 = lMediaItem.getBeginTransition();
                    if (lTransition2 != null && lTransition2.getDuration() > 0) {
                        generateTransition(lTransition2, this.mPreviewEditSettings, this.mClipProperties, previewIndex);
                        storyBoardTime += this.mClipProperties.clipProperties[previewIndex].duration;
                        previewIndex++;
                    }
                    maxHeight = populateMediaItemProperties(lMediaItem, previewIndex, maxHeight);
                    if (lMediaItem instanceof MediaImageItem) {
                        int tmpCnt = 0;
                        boolean bEffectKbPresent = false;
                        List<Effect> effectList = lMediaItem.getAllEffects();
                        while (true) {
                            if (tmpCnt >= effectList.size()) {
                                break;
                            } else if (effectList.get(tmpCnt) instanceof EffectKenBurns) {
                                bEffectKbPresent = true;
                                break;
                            } else {
                                tmpCnt++;
                            }
                        }
                        if (bEffectKbPresent) {
                            try {
                                if (((MediaImageItem) lMediaItem).getGeneratedImageClip() != null) {
                                    this.mClipProperties.clipProperties[previewIndex] = getMediaProperties(((MediaImageItem) lMediaItem).getGeneratedImageClip());
                                } else {
                                    this.mClipProperties.clipProperties[previewIndex] = getMediaProperties(((MediaImageItem) lMediaItem).getScaledImageFileName());
                                    this.mClipProperties.clipProperties[previewIndex].width = ((MediaImageItem) lMediaItem).getScaledWidth();
                                    this.mClipProperties.clipProperties[previewIndex].height = ((MediaImageItem) lMediaItem).getScaledHeight();
                                }
                            } catch (Exception e) {
                                throw new IllegalArgumentException("Unsupported file or file not found");
                            }
                        } else {
                            try {
                                this.mClipProperties.clipProperties[previewIndex] = getMediaProperties(((MediaImageItem) lMediaItem).getScaledImageFileName());
                                this.mClipProperties.clipProperties[previewIndex].width = ((MediaImageItem) lMediaItem).getScaledWidth();
                                this.mClipProperties.clipProperties[previewIndex].height = ((MediaImageItem) lMediaItem).getScaledHeight();
                            } catch (Exception e2) {
                                throw new IllegalArgumentException("Unsupported file or file not found");
                            }
                        }
                    } else {
                        try {
                            this.mClipProperties.clipProperties[previewIndex] = getMediaProperties(lMediaItem.getFilename());
                        } catch (Exception e3) {
                            throw new IllegalArgumentException("Unsupported file or file not found");
                        }
                    }
                    this.mClipProperties.clipProperties[previewIndex].Id = lMediaItem.getId();
                    checkOddSizeImage(lMediaItem, this.mClipProperties, previewIndex);
                    adjustVolume(lMediaItem, this.mClipProperties, previewIndex);
                    adjustMediaItemBoundary(this.mPreviewEditSettings.clipSettingsArray[previewIndex], this.mClipProperties.clipProperties[previewIndex], lMediaItem);
                    effectIndex = populateEffects(lMediaItem, this.mPreviewEditSettings.effectSettingsArray, effectIndex, beginCutTime, endCutTime, storyBoardTime);
                    storyBoardTime += this.mClipProperties.clipProperties[previewIndex].duration;
                    previewIndex++;
                    if (i != mediaItemsList.size() - 1 || (lTransition = lMediaItem.getEndTransition()) == null || lTransition.getDuration() <= 0) {
                        i++;
                    } else {
                        generateTransition(lTransition, this.mPreviewEditSettings, this.mClipProperties, previewIndex);
                        break;
                    }
                }
                if (!this.mErrorFlagSet) {
                    this.mPreviewEditSettings.videoFrameSize = findVideoResolution(this.mVideoEditor.getAspectRatio(), maxHeight);
                    populateBackgroundMusicProperties(mediaBGMList);
                    try {
                        nativePopulateSettings(this.mPreviewEditSettings, this.mClipProperties, this.mAudioSettings);
                        this.mInvalidatePreviewArray = false;
                        this.mProcessingState = 0;
                    } catch (IllegalArgumentException ex) {
                        Log.e(TAG, "Illegal argument exception in nativePopulateSettings");
                        throw ex;
                    } catch (IllegalStateException ex2) {
                        Log.e(TAG, "Illegal state exception in nativePopulateSettings");
                        throw ex2;
                    } catch (RuntimeException ex3) {
                        Log.e(TAG, "Runtime exception in nativePopulateSettings");
                        throw ex3;
                    }
                }
            }
            if (this.mErrorFlagSet) {
                this.mErrorFlagSet = false;
                throw new RuntimeException("preview generation cannot be completed");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void doPreview(Surface surface, long fromMs, long toMs, boolean loop, int callbackAfterFrameCount, VideoEditor.PreviewProgressListener listener) {
        this.mPreviewProgress = fromMs;
        this.mIsFirstProgress = true;
        this.mPreviewProgressListener = listener;
        if (!this.mInvalidatePreviewArray) {
            for (int clipCnt = 0; clipCnt < this.mPreviewEditSettings.clipSettingsArray.length; clipCnt++) {
                try {
                    if (this.mPreviewEditSettings.clipSettingsArray[clipCnt].fileType == 5) {
                        this.mPreviewEditSettings.clipSettingsArray[clipCnt].clipPath = this.mPreviewEditSettings.clipSettingsArray[clipCnt].clipDecodedPath;
                    }
                } catch (IllegalArgumentException ex) {
                    Log.e(TAG, "Illegal argument exception in nativeStartPreview");
                    throw ex;
                } catch (IllegalStateException ex2) {
                    Log.e(TAG, "Illegal state exception in nativeStartPreview");
                    throw ex2;
                } catch (RuntimeException ex3) {
                    Log.e(TAG, "Runtime exception in nativeStartPreview");
                    throw ex3;
                }
            }
            nativePopulateSettings(this.mPreviewEditSettings, this.mClipProperties, this.mAudioSettings);
            nativeStartPreview(surface, fromMs, toMs, callbackAfterFrameCount, loop);
            return;
        }
        throw new IllegalStateException("generatePreview is in progress");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long stopPreview() {
        return nativeStopPreview();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long renderPreviewFrame(Surface surface, long time, int surfaceWidth, int surfaceHeight, VideoEditor.OverlayData overlayData) {
        if (this.mInvalidatePreviewArray) {
            if (Log.isLoggable(TAG, 3)) {
                Log.d(TAG, "Call generate preview first");
            }
            throw new IllegalStateException("Call generate preview first");
        }
        for (int clipCnt = 0; clipCnt < this.mPreviewEditSettings.clipSettingsArray.length; clipCnt++) {
            try {
                if (this.mPreviewEditSettings.clipSettingsArray[clipCnt].fileType == 5) {
                    this.mPreviewEditSettings.clipSettingsArray[clipCnt].clipPath = this.mPreviewEditSettings.clipSettingsArray[clipCnt].clipDecodedPath;
                }
            } catch (IllegalArgumentException ex) {
                Log.e(TAG, "Illegal Argument exception in nativeRenderPreviewFrame");
                throw ex;
            } catch (IllegalStateException ex2) {
                Log.e(TAG, "Illegal state exception in nativeRenderPreviewFrame");
                throw ex2;
            } catch (RuntimeException ex3) {
                Log.e(TAG, "Runtime exception in nativeRenderPreviewFrame");
                throw ex3;
            }
        }
        this.mRenderPreviewOverlayFile = null;
        this.mRenderPreviewRenderingMode = 0;
        nativePopulateSettings(this.mPreviewEditSettings, this.mClipProperties, this.mAudioSettings);
        long timeMs = nativeRenderPreviewFrame(surface, time, surfaceWidth, surfaceHeight);
        if (this.mRenderPreviewOverlayFile != null) {
            overlayData.set(BitmapFactory.decodeFile(this.mRenderPreviewOverlayFile), this.mRenderPreviewRenderingMode);
        } else {
            overlayData.setClear();
        }
        return timeMs;
    }

    private void previewFrameEditInfo(String filename, int renderingMode) {
        this.mRenderPreviewOverlayFile = filename;
        this.mRenderPreviewRenderingMode = renderingMode;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long renderMediaItemPreviewFrame(Surface surface, String filepath, long time, int framewidth, int frameheight) {
        try {
            long timeMs = nativeRenderMediaItemPreviewFrame(surface, filepath, framewidth, frameheight, 0, 0, time);
            return timeMs;
        } catch (IllegalArgumentException ex) {
            Log.e(TAG, "Illegal Argument exception in renderMediaItemPreviewFrame");
            throw ex;
        } catch (IllegalStateException ex2) {
            Log.e(TAG, "Illegal state exception in renderMediaItemPreviewFrame");
            throw ex2;
        } catch (RuntimeException ex3) {
            Log.e(TAG, "Runtime exception in renderMediaItemPreviewFrame");
            throw ex3;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setGeneratePreview(boolean isRequired) {
        boolean semAcquiredDone = false;
        try {
            try {
                lock();
                semAcquiredDone = true;
                this.mInvalidatePreviewArray = isRequired;
                if (1 != 0) {
                    unlock();
                }
            } catch (InterruptedException e) {
                Log.e(TAG, "Runtime exception in renderMediaItemPreviewFrame");
                if (semAcquiredDone) {
                    unlock();
                }
            }
        } catch (Throwable th) {
            if (semAcquiredDone) {
                unlock();
            }
            throw th;
        }
    }

    boolean getGeneratePreview() {
        return this.mInvalidatePreviewArray;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getAspectRatio(int w, int h) {
        BigDecimal bd = new BigDecimal(w / h);
        double apRatio = bd.setScale(3, 4).doubleValue();
        int var = 2;
        if (apRatio >= 1.7d) {
            var = 2;
        } else if (apRatio >= 1.6d) {
            var = 4;
        } else if (apRatio >= 1.5d) {
            var = 1;
        } else if (apRatio > 1.3d) {
            var = 3;
        } else if (apRatio >= 1.2d) {
            var = 5;
        }
        return var;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getFileType(int fileType) {
        int retValue;
        switch (fileType) {
            case 0:
                retValue = 0;
                break;
            case 1:
                retValue = 1;
                break;
            case 2:
                retValue = 2;
                break;
            case 3:
                retValue = 3;
                break;
            case 5:
                retValue = 5;
                break;
            case 8:
                retValue = 8;
                break;
            case 10:
                retValue = 10;
                break;
            case 255:
                retValue = 255;
                break;
            default:
                retValue = -1;
                break;
        }
        return retValue;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getVideoCodecType(int codecType) {
        int retValue;
        switch (codecType) {
            case 1:
                retValue = 1;
                break;
            case 2:
                retValue = 2;
                break;
            case 3:
                retValue = 3;
                break;
            case 255:
            default:
                retValue = -1;
                break;
        }
        return retValue;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getAudioCodecType(int codecType) {
        int retValue;
        switch (codecType) {
            case 1:
                retValue = 1;
                break;
            case 2:
                retValue = 2;
                break;
            case 3:
            case 4:
            default:
                retValue = -1;
                break;
            case 5:
                retValue = 5;
                break;
        }
        return retValue;
    }

    int getFrameRate(int fps) {
        int retValue;
        switch (fps) {
            case 0:
                retValue = 5;
                break;
            case 1:
                retValue = 8;
                break;
            case 2:
                retValue = 10;
                break;
            case 3:
                retValue = 13;
                break;
            case 4:
                retValue = 15;
                break;
            case 5:
                retValue = 20;
                break;
            case 6:
                retValue = 25;
                break;
            case 7:
                retValue = 30;
                break;
            default:
                retValue = -1;
                break;
        }
        return retValue;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getMediaItemFileType(int fileType) {
        int retValue;
        switch (fileType) {
            case 0:
                retValue = 0;
                break;
            case 1:
                retValue = 1;
                break;
            case 5:
                retValue = 5;
                break;
            case 8:
                retValue = 8;
                break;
            case 10:
                retValue = 10;
                break;
            case 255:
                retValue = 255;
                break;
            default:
                retValue = -1;
                break;
        }
        return retValue;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getMediaItemRenderingMode(int renderingMode) {
        int retValue;
        switch (renderingMode) {
            case 0:
                retValue = 2;
                break;
            case 1:
                retValue = 0;
                break;
            case 2:
                retValue = 1;
                break;
            default:
                retValue = -1;
                break;
        }
        return retValue;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getVideoTransitionBehaviour(int transitionType) {
        int retValue;
        switch (transitionType) {
            case 0:
                retValue = 0;
                break;
            case 1:
                retValue = 2;
                break;
            case 2:
                retValue = 1;
                break;
            case 3:
                retValue = 3;
                break;
            case 4:
                retValue = 4;
                break;
            default:
                retValue = -1;
                break;
        }
        return retValue;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getSlideSettingsDirection(int slideDirection) {
        int retValue;
        switch (slideDirection) {
            case 0:
                retValue = 0;
                break;
            case 1:
                retValue = 1;
                break;
            case 2:
                retValue = 2;
                break;
            case 3:
                retValue = 3;
                break;
            default:
                retValue = -1;
                break;
        }
        return retValue;
    }

    private int getEffectColorType(EffectColor effect) {
        int retValue;
        switch (effect.getType()) {
            case 1:
                if (effect.getColor() == 65280) {
                    retValue = 259;
                    break;
                } else if (effect.getColor() == 16737996) {
                    retValue = 258;
                    break;
                } else if (effect.getColor() == 8355711) {
                    retValue = 257;
                    break;
                } else {
                    retValue = 267;
                    break;
                }
            case 2:
                retValue = 268;
                break;
            case 3:
                retValue = 260;
                break;
            case 4:
                retValue = 261;
                break;
            case 5:
                retValue = 266;
                break;
            default:
                retValue = -1;
                break;
        }
        return retValue;
    }

    private int findVideoResolution(int aspectRatio, int height) {
        int retValue = -1;
        switch (aspectRatio) {
            case 1:
                if (height == 480) {
                    retValue = 7;
                    break;
                } else if (height == 720) {
                    retValue = 11;
                    break;
                }
                break;
            case 2:
                if (height == 480) {
                    retValue = 9;
                    break;
                } else if (height == 720) {
                    retValue = 10;
                    break;
                } else if (height == 1080) {
                    retValue = 13;
                    break;
                }
                break;
            case 3:
                if (height == 480) {
                    retValue = 5;
                    break;
                } else if (height == 720) {
                    retValue = 12;
                    break;
                }
                break;
            case 4:
                if (height == 480) {
                    retValue = 6;
                    break;
                }
                break;
            case 5:
                if (height == 144) {
                    retValue = 2;
                    break;
                } else if (height == 288) {
                    retValue = 4;
                    break;
                }
                break;
        }
        if (retValue == -1) {
            Pair<Integer, Integer>[] resolutions = MediaProperties.getSupportedResolutions(this.mVideoEditor.getAspectRatio());
            Pair<Integer, Integer> maxResolution = resolutions[resolutions.length - 1];
            retValue = findVideoResolution(this.mVideoEditor.getAspectRatio(), maxResolution.second.intValue());
        }
        return retValue;
    }

    private int findVideoBitrate(int videoFrameSize) {
        switch (videoFrameSize) {
            case 0:
            case 1:
            case 2:
                return 128000;
            case 3:
            case 4:
                return 384000;
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
                return 2000000;
            case 10:
            case 11:
            case 12:
                return 5000000;
            case 13:
            default:
                return 8000000;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void export(String filePath, String projectDir, int height, int bitrate, List<MediaItem> mediaItemsList, List<Transition> mediaTransitionList, List<AudioTrack> mediaBGMList, VideoEditor.ExportProgressListener listener) {
        int outBitrate;
        this.mExportFilename = filePath;
        previewStoryBoard(mediaItemsList, mediaTransitionList, mediaBGMList, null);
        this.mExportProgressListener = listener;
        VideoEditorProfile veProfile = VideoEditorProfile.get();
        if (veProfile == null) {
            throw new RuntimeException("Can't get the video editor profile");
        }
        int maxOutputHeight = veProfile.maxOutputVideoFrameHeight;
        int maxOutputWidth = veProfile.maxOutputVideoFrameWidth;
        if (height > maxOutputHeight) {
            throw new IllegalArgumentException("Unsupported export resolution. Supported maximum width:" + maxOutputWidth + " height:" + maxOutputHeight + " current height:" + height);
        }
        int outVideoProfile = VideoEditorProfile.getExportProfile(this.mExportVideoCodec);
        int outVideoLevel = VideoEditorProfile.getExportLevel(this.mExportVideoCodec);
        this.mProgressToApp = 0;
        switch (bitrate) {
            case MediaProperties.BITRATE_28K /* 28000 */:
                outBitrate = 32000;
                break;
            case MediaProperties.BITRATE_40K /* 40000 */:
                outBitrate = 48000;
                break;
            case 64000:
                outBitrate = 64000;
                break;
            case 96000:
                outBitrate = 96000;
                break;
            case 128000:
                outBitrate = 128000;
                break;
            case 192000:
                outBitrate = 192000;
                break;
            case 256000:
                outBitrate = 256000;
                break;
            case 384000:
                outBitrate = 384000;
                break;
            case 512000:
                outBitrate = 512000;
                break;
            case 800000:
                outBitrate = 800000;
                break;
            case 2000000:
                outBitrate = 2000000;
                break;
            case 5000000:
                outBitrate = 5000000;
                break;
            case 8000000:
                outBitrate = 8000000;
                break;
            default:
                throw new IllegalArgumentException("Argument Bitrate incorrect");
        }
        this.mPreviewEditSettings.videoFrameRate = 7;
        EditSettings editSettings = this.mPreviewEditSettings;
        this.mOutputFilename = filePath;
        editSettings.outputFile = filePath;
        int aspectRatio = this.mVideoEditor.getAspectRatio();
        this.mPreviewEditSettings.videoFrameSize = findVideoResolution(aspectRatio, height);
        this.mPreviewEditSettings.videoFormat = this.mExportVideoCodec;
        this.mPreviewEditSettings.audioFormat = this.mExportAudioCodec;
        this.mPreviewEditSettings.videoProfile = outVideoProfile;
        this.mPreviewEditSettings.videoLevel = outVideoLevel;
        this.mPreviewEditSettings.audioSamplingFreq = 32000;
        this.mPreviewEditSettings.maxFileSize = 0;
        this.mPreviewEditSettings.audioChannels = 2;
        this.mPreviewEditSettings.videoBitrate = outBitrate;
        this.mPreviewEditSettings.audioBitrate = 96000;
        this.mPreviewEditSettings.transitionSettingsArray = new TransitionSettings[this.mTotalClips - 1];
        for (int index = 0; index < this.mTotalClips - 1; index++) {
            this.mPreviewEditSettings.transitionSettingsArray[index] = new TransitionSettings();
            this.mPreviewEditSettings.transitionSettingsArray[index].videoTransitionType = 0;
            this.mPreviewEditSettings.transitionSettingsArray[index].audioTransitionType = 0;
        }
        for (int clipCnt = 0; clipCnt < this.mPreviewEditSettings.clipSettingsArray.length; clipCnt++) {
            if (this.mPreviewEditSettings.clipSettingsArray[clipCnt].fileType == 5) {
                this.mPreviewEditSettings.clipSettingsArray[clipCnt].clipPath = this.mPreviewEditSettings.clipSettingsArray[clipCnt].clipOriginalPath;
            }
        }
        nativePopulateSettings(this.mPreviewEditSettings, this.mClipProperties, this.mAudioSettings);
        try {
            this.mProcessingState = 20;
            this.mProcessingObject = null;
            int err = generateClip(this.mPreviewEditSettings);
            this.mProcessingState = 0;
            if (err != 0) {
                Log.e(TAG, "RuntimeException for generateClip");
                throw new RuntimeException("generateClip failed with error=" + err);
            }
            this.mExportProgressListener = null;
        } catch (IllegalArgumentException ex) {
            Log.e(TAG, "IllegalArgument for generateClip");
            throw ex;
        } catch (IllegalStateException ex2) {
            Log.e(TAG, "IllegalStateExceptiont for generateClip");
            throw ex2;
        } catch (RuntimeException ex3) {
            Log.e(TAG, "RuntimeException for generateClip");
            throw ex3;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void stop(String filename) {
        try {
            stopEncoding();
            new File(this.mExportFilename).delete();
        } catch (IllegalStateException ex) {
            Log.e(TAG, "Illegal state exception in unload settings");
            throw ex;
        } catch (RuntimeException ex2) {
            Log.e(TAG, "Runtime exception in unload settings");
            throw ex2;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Bitmap getPixels(String filename, int width, int height, long timeMs, int videoRotation) {
        final Bitmap[] result = new Bitmap[1];
        getPixelsList(filename, width, height, timeMs, timeMs, 1, new int[]{0}, new MediaItem.GetThumbnailListCallback() { // from class: android.media.videoeditor.MediaArtistNativeHelper.1
            @Override // android.media.videoeditor.MediaItem.GetThumbnailListCallback
            public void onThumbnail(Bitmap bitmap, int index) {
                result[0] = bitmap;
            }
        }, videoRotation);
        return result[0];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void getPixelsList(String filename, int width, int height, long startMs, long endMs, int thumbnailCount, int[] indices, final MediaItem.GetThumbnailListCallback callback, final int videoRotation) {
        final int decWidth = (width + 1) & (-2);
        final int decHeight = (height + 1) & (-2);
        final int thumbnailSize = decWidth * decHeight;
        final int[] decArray = new int[thumbnailSize];
        final IntBuffer decBuffer = IntBuffer.allocate(thumbnailSize);
        final boolean needToMassage = (decWidth == width && decHeight == height && videoRotation == 0) ? false : true;
        final Bitmap tmpBitmap = needToMassage ? Bitmap.createBitmap(decWidth, decHeight, Bitmap.Config.ARGB_8888) : null;
        boolean needToSwapWH = videoRotation == 90 || videoRotation == 270;
        final int outWidth = needToSwapWH ? height : width;
        final int outHeight = needToSwapWH ? width : height;
        nativeGetPixelsList(filename, decArray, decWidth, decHeight, thumbnailCount, startMs, endMs, indices, new NativeGetPixelsListCallback() { // from class: android.media.videoeditor.MediaArtistNativeHelper.2
            @Override // android.media.videoeditor.MediaArtistNativeHelper.NativeGetPixelsListCallback
            public void onThumbnail(int index) {
                Bitmap outBitmap = Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.ARGB_8888);
                decBuffer.rewind();
                decBuffer.put(decArray, 0, thumbnailSize);
                decBuffer.rewind();
                if (!needToMassage) {
                    outBitmap.copyPixelsFromBuffer(decBuffer);
                } else {
                    tmpBitmap.copyPixelsFromBuffer(decBuffer);
                    Canvas canvas = new Canvas(outBitmap);
                    Matrix m = new Matrix();
                    float sx = 1.0f / decWidth;
                    float sy = 1.0f / decHeight;
                    m.postScale(sx, sy);
                    m.postRotate(videoRotation, 0.5f, 0.5f);
                    m.postScale(outWidth, outHeight);
                    canvas.drawBitmap(tmpBitmap, m, MediaArtistNativeHelper.sResizePaint);
                }
                callback.onThumbnail(outBitmap, index);
            }
        });
        if (tmpBitmap != null) {
            tmpBitmap.recycle();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void generateAudioGraph(String uniqueId, String inFileName, String OutAudiGraphFileName, int frameDuration, int audioChannels, int samplesCount, ExtractAudioWaveformProgressListener listener, boolean isVideo) {
        String tempPCMFileName;
        this.mExtractAudioWaveformProgressListener = listener;
        if (isVideo) {
            tempPCMFileName = String.format(this.mProjectPath + Separators.SLASH + uniqueId + ".pcm", new Object[0]);
        } else {
            tempPCMFileName = this.mAudioTrackPCMFilePath;
        }
        if (isVideo) {
            nativeGenerateRawAudio(inFileName, tempPCMFileName);
        }
        nativeGenerateAudioGraph(tempPCMFileName, OutAudiGraphFileName, frameDuration, audioChannels, samplesCount);
        if (isVideo) {
            new File(tempPCMFileName).delete();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void clearPreviewSurface(Surface surface) {
        nativeClearSurface(surface);
    }

    private void lock() throws InterruptedException {
        if (Log.isLoggable(TAG, 3)) {
            Log.d(TAG, "lock: grabbing semaphore", new Throwable());
        }
        this.mLock.acquire();
        if (Log.isLoggable(TAG, 3)) {
            Log.d(TAG, "lock: grabbed semaphore");
        }
    }

    private void unlock() {
        if (Log.isLoggable(TAG, 3)) {
            Log.d(TAG, "unlock: releasing semaphore");
        }
        this.mLock.release();
    }
}