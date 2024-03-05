package android.media.videoeditor;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.MediaMetadataRetriever;
import android.media.videoeditor.VideoEditor;
import android.os.Debug;
import android.os.Environment;
import android.os.SystemProperties;
import android.util.Log;
import android.util.Xml;
import android.view.Surface;
import android.view.SurfaceHolder;
import gov.nist.core.Separators;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

/* loaded from: VideoEditorImpl.class */
public class VideoEditorImpl implements VideoEditor {
    private static final String TAG = "VideoEditorImpl";
    private static final String PROJECT_FILENAME = "videoeditor.xml";
    private static final String TAG_PROJECT = "project";
    private static final String TAG_MEDIA_ITEMS = "media_items";
    private static final String TAG_MEDIA_ITEM = "media_item";
    private static final String TAG_TRANSITIONS = "transitions";
    private static final String TAG_TRANSITION = "transition";
    private static final String TAG_OVERLAYS = "overlays";
    private static final String TAG_OVERLAY = "overlay";
    private static final String TAG_OVERLAY_USER_ATTRIBUTES = "overlay_user_attributes";
    private static final String TAG_EFFECTS = "effects";
    private static final String TAG_EFFECT = "effect";
    private static final String TAG_AUDIO_TRACKS = "audio_tracks";
    private static final String TAG_AUDIO_TRACK = "audio_track";
    private static final String ATTR_ID = "id";
    private static final String ATTR_FILENAME = "filename";
    private static final String ATTR_AUDIO_WAVEFORM_FILENAME = "waveform";
    private static final String ATTR_RENDERING_MODE = "rendering_mode";
    private static final String ATTR_ASPECT_RATIO = "aspect_ratio";
    private static final String ATTR_REGENERATE_PCM = "regeneratePCMFlag";
    private static final String ATTR_TYPE = "type";
    private static final String ATTR_DURATION = "duration";
    private static final String ATTR_START_TIME = "start_time";
    private static final String ATTR_BEGIN_TIME = "begin_time";
    private static final String ATTR_END_TIME = "end_time";
    private static final String ATTR_VOLUME = "volume";
    private static final String ATTR_BEHAVIOR = "behavior";
    private static final String ATTR_DIRECTION = "direction";
    private static final String ATTR_BLENDING = "blending";
    private static final String ATTR_INVERT = "invert";
    private static final String ATTR_MASK = "mask";
    private static final String ATTR_BEFORE_MEDIA_ITEM_ID = "before_media_item";
    private static final String ATTR_AFTER_MEDIA_ITEM_ID = "after_media_item";
    private static final String ATTR_COLOR_EFFECT_TYPE = "color_type";
    private static final String ATTR_COLOR_EFFECT_VALUE = "color_value";
    private static final String ATTR_START_RECT_LEFT = "start_l";
    private static final String ATTR_START_RECT_TOP = "start_t";
    private static final String ATTR_START_RECT_RIGHT = "start_r";
    private static final String ATTR_START_RECT_BOTTOM = "start_b";
    private static final String ATTR_END_RECT_LEFT = "end_l";
    private static final String ATTR_END_RECT_TOP = "end_t";
    private static final String ATTR_END_RECT_RIGHT = "end_r";
    private static final String ATTR_END_RECT_BOTTOM = "end_b";
    private static final String ATTR_LOOP = "loop";
    private static final String ATTR_MUTED = "muted";
    private static final String ATTR_DUCK_ENABLED = "ducking_enabled";
    private static final String ATTR_DUCK_THRESHOLD = "ducking_threshold";
    private static final String ATTR_DUCKED_TRACK_VOLUME = "ducking_volume";
    private static final String ATTR_GENERATED_IMAGE_CLIP = "generated_image_clip";
    private static final String ATTR_IS_IMAGE_CLIP_GENERATED = "is_image_clip_generated";
    private static final String ATTR_GENERATED_TRANSITION_CLIP = "generated_transition_clip";
    private static final String ATTR_IS_TRANSITION_GENERATED = "is_transition_generated";
    private static final String ATTR_OVERLAY_RGB_FILENAME = "overlay_rgb_filename";
    private static final String ATTR_OVERLAY_FRAME_WIDTH = "overlay_frame_width";
    private static final String ATTR_OVERLAY_FRAME_HEIGHT = "overlay_frame_height";
    private static final String ATTR_OVERLAY_RESIZED_RGB_FRAME_WIDTH = "resized_RGBframe_width";
    private static final String ATTR_OVERLAY_RESIZED_RGB_FRAME_HEIGHT = "resized_RGBframe_height";
    private static final int ENGINE_ACCESS_MAX_TIMEOUT_MS = 500;
    private final Semaphore mLock;
    private final String mProjectPath;
    private long mDurationMs;
    private int mAspectRatio;
    private MediaArtistNativeHelper mMANativeHelper;
    private final boolean mMallocDebug;
    private final List<MediaItem> mMediaItems = new ArrayList();
    private final List<AudioTrack> mAudioTracks = new ArrayList();
    private final List<Transition> mTransitions = new ArrayList();
    private boolean mPreviewInProgress = false;

    public VideoEditorImpl(String projectPath) throws IOException {
        String s = SystemProperties.get("libc.debug.malloc");
        if (s.equals("1")) {
            this.mMallocDebug = true;
            try {
                dumpHeap("HeapAtStart");
            } catch (Exception e) {
                Log.e(TAG, "dumpHeap returned error in constructor");
            }
        } else {
            this.mMallocDebug = false;
        }
        this.mLock = new Semaphore(1, true);
        this.mMANativeHelper = new MediaArtistNativeHelper(projectPath, this.mLock, this);
        this.mProjectPath = projectPath;
        File projectXml = new File(projectPath, PROJECT_FILENAME);
        if (projectXml.exists()) {
            try {
                load();
                return;
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new IOException(ex.toString());
            }
        }
        this.mAspectRatio = 2;
        this.mDurationMs = 0L;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MediaArtistNativeHelper getNativeContext() {
        return this.mMANativeHelper;
    }

    @Override // android.media.videoeditor.VideoEditor
    public synchronized void addAudioTrack(AudioTrack audioTrack) {
        if (audioTrack == null) {
            throw new IllegalArgumentException("Audio Track is null");
        }
        if (this.mAudioTracks.size() == 1) {
            throw new IllegalArgumentException("No more tracks can be added");
        }
        this.mMANativeHelper.setGeneratePreview(true);
        this.mAudioTracks.add(audioTrack);
        String audioTrackPCMFilePath = String.format(this.mProjectPath + Separators.SLASH + "AudioPcm" + audioTrack.getId() + ".pcm", new Object[0]);
        if (new File(audioTrackPCMFilePath).exists()) {
            this.mMANativeHelper.setAudioflag(false);
        }
    }

    @Override // android.media.videoeditor.VideoEditor
    public synchronized void addMediaItem(MediaItem mediaItem) {
        if (mediaItem == null) {
            throw new IllegalArgumentException("Media item is null");
        }
        if (this.mMediaItems.contains(mediaItem)) {
            throw new IllegalArgumentException("Media item already exists: " + mediaItem.getId());
        }
        this.mMANativeHelper.setGeneratePreview(true);
        int mediaItemsCount = this.mMediaItems.size();
        if (mediaItemsCount > 0) {
            removeTransitionAfter(mediaItemsCount - 1);
        }
        this.mMediaItems.add(mediaItem);
        computeTimelineDuration();
        if (this.mMediaItems.size() == 1) {
            generateProjectThumbnail();
        }
    }

    @Override // android.media.videoeditor.VideoEditor
    public synchronized void addTransition(Transition transition) {
        if (transition == null) {
            throw new IllegalArgumentException("Null Transition");
        }
        MediaItem beforeMediaItem = transition.getBeforeMediaItem();
        MediaItem afterMediaItem = transition.getAfterMediaItem();
        if (this.mMediaItems == null) {
            throw new IllegalArgumentException("No media items are added");
        }
        if (afterMediaItem != null && beforeMediaItem != null) {
            int afterMediaItemIndex = this.mMediaItems.indexOf(afterMediaItem);
            int beforeMediaItemIndex = this.mMediaItems.indexOf(beforeMediaItem);
            if (afterMediaItemIndex == -1 || beforeMediaItemIndex == -1) {
                throw new IllegalArgumentException("Either of the mediaItem is not found in the list");
            }
            if (afterMediaItemIndex != beforeMediaItemIndex - 1) {
                throw new IllegalArgumentException("MediaItems are not in sequence");
            }
        }
        this.mMANativeHelper.setGeneratePreview(true);
        this.mTransitions.add(transition);
        if (afterMediaItem != null) {
            if (afterMediaItem.getEndTransition() != null) {
                afterMediaItem.getEndTransition().invalidate();
                this.mTransitions.remove(afterMediaItem.getEndTransition());
            }
            afterMediaItem.setEndTransition(transition);
        }
        if (beforeMediaItem != null) {
            if (beforeMediaItem.getBeginTransition() != null) {
                beforeMediaItem.getBeginTransition().invalidate();
                this.mTransitions.remove(beforeMediaItem.getBeginTransition());
            }
            beforeMediaItem.setBeginTransition(transition);
        }
        computeTimelineDuration();
    }

    @Override // android.media.videoeditor.VideoEditor
    public void cancelExport(String filename) {
        if (this.mMANativeHelper != null && filename != null) {
            this.mMANativeHelper.stop(filename);
        }
    }

    @Override // android.media.videoeditor.VideoEditor
    public void export(String filename, int height, int bitrate, int audioCodec, int videoCodec, VideoEditor.ExportProgressListener listener) throws IOException {
        int audcodec;
        int vidcodec;
        if (filename == null) {
            throw new IllegalArgumentException("export: filename is null");
        }
        File tempPathFile = new File(filename);
        if (tempPathFile == null) {
            throw new IOException(filename + "can not be created");
        }
        if (this.mMediaItems.size() == 0) {
            throw new IllegalStateException("No MediaItems added");
        }
        switch (height) {
            case 144:
            case MediaProperties.HEIGHT_288 /* 288 */:
            case MediaProperties.HEIGHT_360 /* 360 */:
            case 480:
            case MediaProperties.HEIGHT_720 /* 720 */:
            case 1080:
                switch (bitrate) {
                    case MediaProperties.BITRATE_28K /* 28000 */:
                    case MediaProperties.BITRATE_40K /* 40000 */:
                    case 64000:
                    case 96000:
                    case 128000:
                    case 192000:
                    case 256000:
                    case 384000:
                    case 512000:
                    case 800000:
                    case 2000000:
                    case 5000000:
                    case 8000000:
                        computeTimelineDuration();
                        long fileSize = (this.mDurationMs * (bitrate + 96000)) / 8000;
                        if (VideoEditor.MAX_SUPPORTED_FILE_SIZE <= fileSize) {
                            throw new IllegalStateException("Export Size is more than 2GB");
                        }
                        switch (audioCodec) {
                            case 1:
                                audcodec = 1;
                                break;
                            case 2:
                                audcodec = 2;
                                break;
                            default:
                                String message = "Unsupported audio codec type " + audioCodec;
                                throw new IllegalArgumentException(message);
                        }
                        switch (videoCodec) {
                            case 1:
                                vidcodec = 1;
                                break;
                            case 2:
                                vidcodec = 2;
                                break;
                            case 3:
                                vidcodec = 3;
                                break;
                            default:
                                String message2 = "Unsupported video codec type " + videoCodec;
                                throw new IllegalArgumentException(message2);
                        }
                        try {
                            try {
                                lock();
                                if (this.mMANativeHelper == null) {
                                    throw new IllegalStateException("The video editor is not initialized");
                                }
                                this.mMANativeHelper.setAudioCodec(audcodec);
                                this.mMANativeHelper.setVideoCodec(vidcodec);
                                this.mMANativeHelper.export(filename, this.mProjectPath, height, bitrate, this.mMediaItems, this.mTransitions, this.mAudioTracks, listener);
                                if (1 != 0) {
                                    unlock();
                                    return;
                                }
                                return;
                            } catch (InterruptedException e) {
                                Log.e(TAG, "Sem acquire NOT successful in export");
                                if (0 != 0) {
                                    unlock();
                                    return;
                                }
                                return;
                            }
                        } catch (Throwable th) {
                            if (0 != 0) {
                                unlock();
                            }
                            throw th;
                        }
                    default:
                        String message3 = "Unsupported bitrate value " + bitrate;
                        throw new IllegalArgumentException(message3);
                }
            default:
                String message4 = "Unsupported height value " + height;
                throw new IllegalArgumentException(message4);
        }
    }

    @Override // android.media.videoeditor.VideoEditor
    public void export(String filename, int height, int bitrate, VideoEditor.ExportProgressListener listener) throws IOException {
        export(filename, height, bitrate, 2, 2, listener);
    }

    @Override // android.media.videoeditor.VideoEditor
    public void generatePreview(VideoEditor.MediaProcessingProgressListener listener) {
        try {
            try {
                lock();
                if (this.mMANativeHelper == null) {
                    throw new IllegalStateException("The video editor is not initialized");
                }
                if (this.mMediaItems.size() > 0 || this.mAudioTracks.size() > 0) {
                    this.mMANativeHelper.previewStoryBoard(this.mMediaItems, this.mTransitions, this.mAudioTracks, listener);
                }
                if (1 != 0) {
                    unlock();
                }
            } catch (InterruptedException e) {
                Log.e(TAG, "Sem acquire NOT successful in previewStoryBoard");
                if (0 != 0) {
                    unlock();
                }
            }
        } catch (Throwable th) {
            if (0 != 0) {
                unlock();
            }
            throw th;
        }
    }

    @Override // android.media.videoeditor.VideoEditor
    public List<AudioTrack> getAllAudioTracks() {
        return this.mAudioTracks;
    }

    @Override // android.media.videoeditor.VideoEditor
    public List<MediaItem> getAllMediaItems() {
        return this.mMediaItems;
    }

    @Override // android.media.videoeditor.VideoEditor
    public List<Transition> getAllTransitions() {
        return this.mTransitions;
    }

    @Override // android.media.videoeditor.VideoEditor
    public int getAspectRatio() {
        return this.mAspectRatio;
    }

    @Override // android.media.videoeditor.VideoEditor
    public AudioTrack getAudioTrack(String audioTrackId) {
        for (AudioTrack at : this.mAudioTracks) {
            if (at.getId().equals(audioTrackId)) {
                return at;
            }
        }
        return null;
    }

    @Override // android.media.videoeditor.VideoEditor
    public long getDuration() {
        computeTimelineDuration();
        return this.mDurationMs;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateTimelineDuration() {
        computeTimelineDuration();
    }

    @Override // android.media.videoeditor.VideoEditor
    public synchronized MediaItem getMediaItem(String mediaItemId) {
        for (MediaItem mediaItem : this.mMediaItems) {
            if (mediaItem.getId().equals(mediaItemId)) {
                return mediaItem;
            }
        }
        return null;
    }

    @Override // android.media.videoeditor.VideoEditor
    public String getPath() {
        return this.mProjectPath;
    }

    @Override // android.media.videoeditor.VideoEditor
    public Transition getTransition(String transitionId) {
        for (Transition transition : this.mTransitions) {
            if (transition.getId().equals(transitionId)) {
                return transition;
            }
        }
        return null;
    }

    @Override // android.media.videoeditor.VideoEditor
    public synchronized void insertAudioTrack(AudioTrack audioTrack, String afterAudioTrackId) {
        if (this.mAudioTracks.size() == 1) {
            throw new IllegalArgumentException("No more tracks can be added");
        }
        if (afterAudioTrackId == null) {
            this.mMANativeHelper.setGeneratePreview(true);
            this.mAudioTracks.add(0, audioTrack);
            return;
        }
        int audioTrackCount = this.mAudioTracks.size();
        for (int i = 0; i < audioTrackCount; i++) {
            AudioTrack at = this.mAudioTracks.get(i);
            if (at.getId().equals(afterAudioTrackId)) {
                this.mMANativeHelper.setGeneratePreview(true);
                this.mAudioTracks.add(i + 1, audioTrack);
                return;
            }
        }
        throw new IllegalArgumentException("AudioTrack not found: " + afterAudioTrackId);
    }

    @Override // android.media.videoeditor.VideoEditor
    public synchronized void insertMediaItem(MediaItem mediaItem, String afterMediaItemId) {
        if (this.mMediaItems.contains(mediaItem)) {
            throw new IllegalArgumentException("Media item already exists: " + mediaItem.getId());
        }
        if (afterMediaItemId == null) {
            this.mMANativeHelper.setGeneratePreview(true);
            if (this.mMediaItems.size() > 0) {
                removeTransitionBefore(0);
            }
            this.mMediaItems.add(0, mediaItem);
            computeTimelineDuration();
            generateProjectThumbnail();
            return;
        }
        int mediaItemCount = this.mMediaItems.size();
        for (int i = 0; i < mediaItemCount; i++) {
            MediaItem mi = this.mMediaItems.get(i);
            if (mi.getId().equals(afterMediaItemId)) {
                this.mMANativeHelper.setGeneratePreview(true);
                removeTransitionAfter(i);
                this.mMediaItems.add(i + 1, mediaItem);
                computeTimelineDuration();
                return;
            }
        }
        throw new IllegalArgumentException("MediaItem not found: " + afterMediaItemId);
    }

    @Override // android.media.videoeditor.VideoEditor
    public synchronized void moveAudioTrack(String audioTrackId, String afterAudioTrackId) {
        throw new IllegalStateException("Not supported");
    }

    @Override // android.media.videoeditor.VideoEditor
    public synchronized void moveMediaItem(String mediaItemId, String afterMediaItemId) {
        MediaItem moveMediaItem = removeMediaItem(mediaItemId, true);
        if (moveMediaItem == null) {
            throw new IllegalArgumentException("Target MediaItem not found: " + mediaItemId);
        }
        if (afterMediaItemId == null) {
            if (this.mMediaItems.size() > 0) {
                this.mMANativeHelper.setGeneratePreview(true);
                removeTransitionBefore(0);
                this.mMediaItems.add(0, moveMediaItem);
                computeTimelineDuration();
                generateProjectThumbnail();
                return;
            }
            throw new IllegalStateException("Cannot move media item (it is the only item)");
        }
        int mediaItemCount = this.mMediaItems.size();
        for (int i = 0; i < mediaItemCount; i++) {
            MediaItem mi = this.mMediaItems.get(i);
            if (mi.getId().equals(afterMediaItemId)) {
                this.mMANativeHelper.setGeneratePreview(true);
                removeTransitionAfter(i);
                this.mMediaItems.add(i + 1, moveMediaItem);
                computeTimelineDuration();
                return;
            }
        }
        throw new IllegalArgumentException("MediaItem not found: " + afterMediaItemId);
    }

    @Override // android.media.videoeditor.VideoEditor
    public void release() {
        stopPreview();
        boolean semAcquireDone = false;
        try {
            try {
                lock();
                semAcquireDone = true;
                if (this.mMANativeHelper != null) {
                    this.mMediaItems.clear();
                    this.mAudioTracks.clear();
                    this.mTransitions.clear();
                    this.mMANativeHelper.releaseNativeHelper();
                    this.mMANativeHelper = null;
                }
                if (1 != 0) {
                    unlock();
                }
            } catch (Throwable th) {
                if (semAcquireDone) {
                    unlock();
                }
                throw th;
            }
        } catch (Exception ex) {
            Log.e(TAG, "Sem acquire NOT successful in export", ex);
            if (semAcquireDone) {
                unlock();
            }
        }
        if (this.mMallocDebug) {
            try {
                dumpHeap("HeapAtEnd");
            } catch (Exception e) {
                Log.e(TAG, "dumpHeap returned error in release");
            }
        }
    }

    @Override // android.media.videoeditor.VideoEditor
    public synchronized void removeAllMediaItems() {
        this.mMANativeHelper.setGeneratePreview(true);
        this.mMediaItems.clear();
        for (Transition transition : this.mTransitions) {
            transition.invalidate();
        }
        this.mTransitions.clear();
        this.mDurationMs = 0L;
        if (new File(this.mProjectPath + Separators.SLASH + VideoEditor.THUMBNAIL_FILENAME).exists()) {
            new File(this.mProjectPath + Separators.SLASH + VideoEditor.THUMBNAIL_FILENAME).delete();
        }
    }

    @Override // android.media.videoeditor.VideoEditor
    public synchronized AudioTrack removeAudioTrack(String audioTrackId) {
        AudioTrack audioTrack = getAudioTrack(audioTrackId);
        if (audioTrack != null) {
            this.mMANativeHelper.setGeneratePreview(true);
            this.mAudioTracks.remove(audioTrack);
            audioTrack.invalidate();
            this.mMANativeHelper.invalidatePcmFile();
            this.mMANativeHelper.setAudioflag(true);
            return audioTrack;
        }
        throw new IllegalArgumentException(" No more audio tracks");
    }

    @Override // android.media.videoeditor.VideoEditor
    public synchronized MediaItem removeMediaItem(String mediaItemId) {
        String firstItemString = this.mMediaItems.get(0).getId();
        MediaItem mediaItem = getMediaItem(mediaItemId);
        if (mediaItem != null) {
            this.mMANativeHelper.setGeneratePreview(true);
            this.mMediaItems.remove(mediaItem);
            if (mediaItem instanceof MediaImageItem) {
                ((MediaImageItem) mediaItem).invalidate();
            }
            List<Overlay> overlays = mediaItem.getAllOverlays();
            if (overlays.size() > 0) {
                for (Overlay overlay : overlays) {
                    if (overlay instanceof OverlayFrame) {
                        OverlayFrame overlayFrame = (OverlayFrame) overlay;
                        overlayFrame.invalidate();
                    }
                }
            }
            removeAdjacentTransitions(mediaItem);
            computeTimelineDuration();
        }
        if (firstItemString.equals(mediaItemId)) {
            generateProjectThumbnail();
        }
        if (mediaItem instanceof MediaVideoItem) {
            ((MediaVideoItem) mediaItem).invalidate();
        }
        return mediaItem;
    }

    private synchronized MediaItem removeMediaItem(String mediaItemId, boolean flag) {
        String firstItemString = this.mMediaItems.get(0).getId();
        MediaItem mediaItem = getMediaItem(mediaItemId);
        if (mediaItem != null) {
            this.mMANativeHelper.setGeneratePreview(true);
            this.mMediaItems.remove(mediaItem);
            removeAdjacentTransitions(mediaItem);
            computeTimelineDuration();
        }
        if (firstItemString.equals(mediaItemId)) {
            generateProjectThumbnail();
        }
        return mediaItem;
    }

    @Override // android.media.videoeditor.VideoEditor
    public synchronized Transition removeTransition(String transitionId) {
        Transition transition = getTransition(transitionId);
        if (transition == null) {
            throw new IllegalStateException("Transition not found: " + transitionId);
        }
        this.mMANativeHelper.setGeneratePreview(true);
        MediaItem afterMediaItem = transition.getAfterMediaItem();
        if (afterMediaItem != null) {
            afterMediaItem.setEndTransition(null);
        }
        MediaItem beforeMediaItem = transition.getBeforeMediaItem();
        if (beforeMediaItem != null) {
            beforeMediaItem.setBeginTransition(null);
        }
        this.mTransitions.remove(transition);
        transition.invalidate();
        computeTimelineDuration();
        return transition;
    }

    @Override // android.media.videoeditor.VideoEditor
    public long renderPreviewFrame(SurfaceHolder surfaceHolder, long timeMs, VideoEditor.OverlayData overlayData) {
        long result;
        if (surfaceHolder == null) {
            throw new IllegalArgumentException("Surface Holder is null");
        }
        Surface surface = surfaceHolder.getSurface();
        if (surface == null) {
            throw new IllegalArgumentException("Surface could not be retrieved from Surface holder");
        }
        if (!surface.isValid()) {
            throw new IllegalStateException("Surface is not valid");
        }
        if (timeMs < 0) {
            throw new IllegalArgumentException("requested time not correct");
        }
        if (timeMs > this.mDurationMs) {
            throw new IllegalArgumentException("requested time more than duration");
        }
        boolean semAcquireDone = false;
        try {
            try {
                semAcquireDone = lock(500L);
                if (!semAcquireDone) {
                    throw new IllegalStateException("Timeout waiting for semaphore");
                }
                if (this.mMANativeHelper == null) {
                    throw new IllegalStateException("The video editor is not initialized");
                }
                if (this.mMediaItems.size() > 0) {
                    Rect frame = surfaceHolder.getSurfaceFrame();
                    result = this.mMANativeHelper.renderPreviewFrame(surface, timeMs, frame.width(), frame.height(), overlayData);
                } else {
                    result = 0;
                }
                return result;
            } catch (InterruptedException e) {
                Log.w(TAG, "The thread was interrupted", new Throwable());
                throw new IllegalStateException("The thread was interrupted");
            }
        } finally {
            if (semAcquireDone) {
                unlock();
            }
        }
    }

    private void load() throws FileNotFoundException, XmlPullParserException, IOException {
        List<String> ignoredMediaItems;
        XmlPullParser parser;
        int eventType;
        MediaItem currentMediaItem;
        Overlay currentOverlay;
        boolean regenerateProjectThumbnail;
        File file = new File(this.mProjectPath, PROJECT_FILENAME);
        FileInputStream fis = new FileInputStream(file);
        try {
            ignoredMediaItems = new ArrayList<>();
            parser = Xml.newPullParser();
            parser.setInput(fis, "UTF-8");
            currentMediaItem = null;
            currentOverlay = null;
            regenerateProjectThumbnail = false;
        } finally {
        }
        for (eventType = parser.getEventType(); eventType != 1; eventType = parser.next()) {
            switch (eventType) {
                case 2:
                    String name = parser.getName();
                    if (TAG_PROJECT.equals(name)) {
                        this.mAspectRatio = Integer.parseInt(parser.getAttributeValue("", ATTR_ASPECT_RATIO));
                        boolean mRegenPCM = Boolean.parseBoolean(parser.getAttributeValue("", ATTR_REGENERATE_PCM));
                        this.mMANativeHelper.setAudioflag(mRegenPCM);
                        break;
                    } else {
                        if (TAG_MEDIA_ITEM.equals(name)) {
                            String mediaItemId = parser.getAttributeValue("", "id");
                            try {
                                currentMediaItem = parseMediaItem(parser);
                                this.mMediaItems.add(currentMediaItem);
                            } catch (Exception ex) {
                                Log.w(TAG, "Cannot load media item: " + mediaItemId, ex);
                                currentMediaItem = null;
                                if (this.mMediaItems.size() == 0) {
                                    regenerateProjectThumbnail = true;
                                }
                                ignoredMediaItems.add(mediaItemId);
                            }
                            break;
                        } else if (TAG_TRANSITION.equals(name)) {
                            try {
                                Transition transition = parseTransition(parser, ignoredMediaItems);
                                if (transition != null) {
                                    this.mTransitions.add(transition);
                                }
                                break;
                            } catch (Exception ex2) {
                                Log.w(TAG, "Cannot load transition", ex2);
                                break;
                            }
                        } else if (TAG_OVERLAY.equals(name)) {
                            if (currentMediaItem != null) {
                                try {
                                    currentOverlay = parseOverlay(parser, currentMediaItem);
                                    currentMediaItem.addOverlay(currentOverlay);
                                    break;
                                } catch (Exception ex3) {
                                    Log.w(TAG, "Cannot load overlay", ex3);
                                    break;
                                }
                            } else {
                                break;
                            }
                        } else if (TAG_OVERLAY_USER_ATTRIBUTES.equals(name)) {
                            if (currentOverlay != null) {
                                int attributesCount = parser.getAttributeCount();
                                for (int i = 0; i < attributesCount; i++) {
                                    currentOverlay.setUserAttribute(parser.getAttributeName(i), parser.getAttributeValue(i));
                                }
                                break;
                            } else {
                                break;
                            }
                        } else if (TAG_EFFECT.equals(name)) {
                            if (currentMediaItem != null) {
                                try {
                                    Effect effect = parseEffect(parser, currentMediaItem);
                                    currentMediaItem.addEffect(effect);
                                    if (effect instanceof EffectKenBurns) {
                                        boolean isImageClipGenerated = Boolean.parseBoolean(parser.getAttributeValue("", ATTR_IS_IMAGE_CLIP_GENERATED));
                                        if (isImageClipGenerated) {
                                            String filename = parser.getAttributeValue("", ATTR_GENERATED_IMAGE_CLIP);
                                            if (new File(filename).exists()) {
                                                ((MediaImageItem) currentMediaItem).setGeneratedImageClip(filename);
                                                ((MediaImageItem) currentMediaItem).setRegenerateClip(false);
                                            } else {
                                                ((MediaImageItem) currentMediaItem).setGeneratedImageClip(null);
                                                ((MediaImageItem) currentMediaItem).setRegenerateClip(true);
                                            }
                                        } else {
                                            ((MediaImageItem) currentMediaItem).setGeneratedImageClip(null);
                                            ((MediaImageItem) currentMediaItem).setRegenerateClip(true);
                                        }
                                    }
                                    break;
                                } catch (Exception ex4) {
                                    Log.w(TAG, "Cannot load effect", ex4);
                                    break;
                                }
                            } else {
                                break;
                            }
                        } else if (TAG_AUDIO_TRACK.equals(name)) {
                            try {
                                AudioTrack audioTrack = parseAudioTrack(parser);
                                addAudioTrack(audioTrack);
                                break;
                            } catch (Exception ex5) {
                                Log.w(TAG, "Cannot load audio track", ex5);
                                break;
                            }
                        } else {
                            break;
                        }
                        if (fis != null) {
                            fis.close();
                        }
                    }
                case 3:
                    String name2 = parser.getName();
                    if (TAG_MEDIA_ITEM.equals(name2)) {
                        currentMediaItem = null;
                        break;
                    } else if (TAG_OVERLAY.equals(name2)) {
                        currentOverlay = null;
                        break;
                    } else {
                        break;
                    }
            }
        }
        computeTimelineDuration();
        if (regenerateProjectThumbnail) {
            generateProjectThumbnail();
        }
    }

    private MediaItem parseMediaItem(XmlPullParser parser) throws IOException {
        MediaItem currentMediaItem;
        String mediaItemId = parser.getAttributeValue("", "id");
        String type = parser.getAttributeValue("", "type");
        String filename = parser.getAttributeValue("", ATTR_FILENAME);
        int renderingMode = Integer.parseInt(parser.getAttributeValue("", ATTR_RENDERING_MODE));
        if (MediaImageItem.class.getSimpleName().equals(type)) {
            long durationMs = Long.parseLong(parser.getAttributeValue("", "duration"));
            currentMediaItem = new MediaImageItem(this, mediaItemId, filename, durationMs, renderingMode);
        } else if (MediaVideoItem.class.getSimpleName().equals(type)) {
            long beginMs = Long.parseLong(parser.getAttributeValue("", ATTR_BEGIN_TIME));
            long endMs = Long.parseLong(parser.getAttributeValue("", ATTR_END_TIME));
            int volume = Integer.parseInt(parser.getAttributeValue("", "volume"));
            boolean muted = Boolean.parseBoolean(parser.getAttributeValue("", ATTR_MUTED));
            String audioWaveformFilename = parser.getAttributeValue("", ATTR_AUDIO_WAVEFORM_FILENAME);
            currentMediaItem = new MediaVideoItem(this, mediaItemId, filename, renderingMode, beginMs, endMs, volume, muted, audioWaveformFilename);
            long beginTimeMs = Long.parseLong(parser.getAttributeValue("", ATTR_BEGIN_TIME));
            long endTimeMs = Long.parseLong(parser.getAttributeValue("", ATTR_END_TIME));
            ((MediaVideoItem) currentMediaItem).setExtractBoundaries(beginTimeMs, endTimeMs);
            int volumePercent = Integer.parseInt(parser.getAttributeValue("", "volume"));
            ((MediaVideoItem) currentMediaItem).setVolume(volumePercent);
        } else {
            throw new IllegalArgumentException("Unknown media item type: " + type);
        }
        return currentMediaItem;
    }

    private Transition parseTransition(XmlPullParser parser, List<String> ignoredMediaItems) {
        MediaItem beforeMediaItem;
        MediaItem afterMediaItem;
        Transition transition;
        String transitionId = parser.getAttributeValue("", "id");
        String type = parser.getAttributeValue("", "type");
        long durationMs = Long.parseLong(parser.getAttributeValue("", "duration"));
        int behavior = Integer.parseInt(parser.getAttributeValue("", ATTR_BEHAVIOR));
        String beforeMediaItemId = parser.getAttributeValue("", ATTR_BEFORE_MEDIA_ITEM_ID);
        if (beforeMediaItemId != null) {
            if (ignoredMediaItems.contains(beforeMediaItemId)) {
                return null;
            }
            beforeMediaItem = getMediaItem(beforeMediaItemId);
        } else {
            beforeMediaItem = null;
        }
        String afterMediaItemId = parser.getAttributeValue("", ATTR_AFTER_MEDIA_ITEM_ID);
        if (afterMediaItemId != null) {
            if (ignoredMediaItems.contains(afterMediaItemId)) {
                return null;
            }
            afterMediaItem = getMediaItem(afterMediaItemId);
        } else {
            afterMediaItem = null;
        }
        if (TransitionAlpha.class.getSimpleName().equals(type)) {
            int blending = Integer.parseInt(parser.getAttributeValue("", ATTR_BLENDING));
            String maskFilename = parser.getAttributeValue("", ATTR_MASK);
            boolean invert = Boolean.getBoolean(parser.getAttributeValue("", ATTR_INVERT));
            transition = new TransitionAlpha(transitionId, afterMediaItem, beforeMediaItem, durationMs, behavior, maskFilename, blending, invert);
        } else if (TransitionCrossfade.class.getSimpleName().equals(type)) {
            transition = new TransitionCrossfade(transitionId, afterMediaItem, beforeMediaItem, durationMs, behavior);
        } else if (TransitionSliding.class.getSimpleName().equals(type)) {
            int direction = Integer.parseInt(parser.getAttributeValue("", ATTR_DIRECTION));
            transition = new TransitionSliding(transitionId, afterMediaItem, beforeMediaItem, durationMs, behavior, direction);
        } else if (TransitionFadeBlack.class.getSimpleName().equals(type)) {
            transition = new TransitionFadeBlack(transitionId, afterMediaItem, beforeMediaItem, durationMs, behavior);
        } else {
            throw new IllegalArgumentException("Invalid transition type: " + type);
        }
        boolean isTransitionGenerated = Boolean.parseBoolean(parser.getAttributeValue("", ATTR_IS_TRANSITION_GENERATED));
        if (isTransitionGenerated) {
            String transitionFile = parser.getAttributeValue("", ATTR_GENERATED_TRANSITION_CLIP);
            if (new File(transitionFile).exists()) {
                transition.setFilename(transitionFile);
            } else {
                transition.setFilename(null);
            }
        }
        if (beforeMediaItem != null) {
            beforeMediaItem.setBeginTransition(transition);
        }
        if (afterMediaItem != null) {
            afterMediaItem.setEndTransition(transition);
        }
        return transition;
    }

    private Overlay parseOverlay(XmlPullParser parser, MediaItem mediaItem) {
        String overlayId = parser.getAttributeValue("", "id");
        String type = parser.getAttributeValue("", "type");
        long durationMs = Long.parseLong(parser.getAttributeValue("", "duration"));
        long startTimeMs = Long.parseLong(parser.getAttributeValue("", ATTR_BEGIN_TIME));
        if (OverlayFrame.class.getSimpleName().equals(type)) {
            String filename = parser.getAttributeValue("", ATTR_FILENAME);
            Overlay overlay = new OverlayFrame(mediaItem, overlayId, filename, startTimeMs, durationMs);
            String overlayRgbFileName = parser.getAttributeValue("", ATTR_OVERLAY_RGB_FILENAME);
            if (overlayRgbFileName != null) {
                ((OverlayFrame) overlay).setFilename(overlayRgbFileName);
                int overlayFrameWidth = Integer.parseInt(parser.getAttributeValue("", ATTR_OVERLAY_FRAME_WIDTH));
                int overlayFrameHeight = Integer.parseInt(parser.getAttributeValue("", ATTR_OVERLAY_FRAME_HEIGHT));
                ((OverlayFrame) overlay).setOverlayFrameWidth(overlayFrameWidth);
                ((OverlayFrame) overlay).setOverlayFrameHeight(overlayFrameHeight);
                int resizedRGBFrameWidth = Integer.parseInt(parser.getAttributeValue("", ATTR_OVERLAY_RESIZED_RGB_FRAME_WIDTH));
                int resizedRGBFrameHeight = Integer.parseInt(parser.getAttributeValue("", ATTR_OVERLAY_RESIZED_RGB_FRAME_HEIGHT));
                ((OverlayFrame) overlay).setResizedRGBSize(resizedRGBFrameWidth, resizedRGBFrameHeight);
            }
            return overlay;
        }
        throw new IllegalArgumentException("Invalid overlay type: " + type);
    }

    private Effect parseEffect(XmlPullParser parser, MediaItem mediaItem) {
        Effect effect;
        int color;
        String effectId = parser.getAttributeValue("", "id");
        String type = parser.getAttributeValue("", "type");
        long durationMs = Long.parseLong(parser.getAttributeValue("", "duration"));
        long startTimeMs = Long.parseLong(parser.getAttributeValue("", ATTR_BEGIN_TIME));
        if (EffectColor.class.getSimpleName().equals(type)) {
            int colorEffectType = Integer.parseInt(parser.getAttributeValue("", "color_type"));
            if (colorEffectType == 1 || colorEffectType == 2) {
                color = Integer.parseInt(parser.getAttributeValue("", ATTR_COLOR_EFFECT_VALUE));
            } else {
                color = 0;
            }
            effect = new EffectColor(mediaItem, effectId, startTimeMs, durationMs, colorEffectType, color);
        } else if (EffectKenBurns.class.getSimpleName().equals(type)) {
            Rect startRect = new Rect(Integer.parseInt(parser.getAttributeValue("", ATTR_START_RECT_LEFT)), Integer.parseInt(parser.getAttributeValue("", ATTR_START_RECT_TOP)), Integer.parseInt(parser.getAttributeValue("", ATTR_START_RECT_RIGHT)), Integer.parseInt(parser.getAttributeValue("", ATTR_START_RECT_BOTTOM)));
            Rect endRect = new Rect(Integer.parseInt(parser.getAttributeValue("", ATTR_END_RECT_LEFT)), Integer.parseInt(parser.getAttributeValue("", ATTR_END_RECT_TOP)), Integer.parseInt(parser.getAttributeValue("", ATTR_END_RECT_RIGHT)), Integer.parseInt(parser.getAttributeValue("", ATTR_END_RECT_BOTTOM)));
            effect = new EffectKenBurns(mediaItem, effectId, startRect, endRect, startTimeMs, durationMs);
        } else {
            throw new IllegalArgumentException("Invalid effect type: " + type);
        }
        return effect;
    }

    private AudioTrack parseAudioTrack(XmlPullParser parser) throws IOException {
        String audioTrackId = parser.getAttributeValue("", "id");
        String filename = parser.getAttributeValue("", ATTR_FILENAME);
        long startTimeMs = Long.parseLong(parser.getAttributeValue("", ATTR_START_TIME));
        long beginMs = Long.parseLong(parser.getAttributeValue("", ATTR_BEGIN_TIME));
        long endMs = Long.parseLong(parser.getAttributeValue("", ATTR_END_TIME));
        int volume = Integer.parseInt(parser.getAttributeValue("", "volume"));
        boolean muted = Boolean.parseBoolean(parser.getAttributeValue("", ATTR_MUTED));
        boolean loop = Boolean.parseBoolean(parser.getAttributeValue("", ATTR_LOOP));
        boolean duckingEnabled = Boolean.parseBoolean(parser.getAttributeValue("", ATTR_DUCK_ENABLED));
        int duckThreshold = Integer.parseInt(parser.getAttributeValue("", ATTR_DUCK_THRESHOLD));
        int duckedTrackVolume = Integer.parseInt(parser.getAttributeValue("", ATTR_DUCKED_TRACK_VOLUME));
        String waveformFilename = parser.getAttributeValue("", ATTR_AUDIO_WAVEFORM_FILENAME);
        AudioTrack audioTrack = new AudioTrack(this, audioTrackId, filename, startTimeMs, beginMs, endMs, loop, volume, muted, duckingEnabled, duckThreshold, duckedTrackVolume, waveformFilename);
        return audioTrack;
    }

    @Override // android.media.videoeditor.VideoEditor
    public void save() throws IOException {
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        serializer.setOutput(writer);
        serializer.startDocument("UTF-8", true);
        serializer.startTag("", TAG_PROJECT);
        serializer.attribute("", ATTR_ASPECT_RATIO, Integer.toString(this.mAspectRatio));
        serializer.attribute("", ATTR_REGENERATE_PCM, Boolean.toString(this.mMANativeHelper.getAudioflag()));
        serializer.startTag("", TAG_MEDIA_ITEMS);
        for (MediaItem mediaItem : this.mMediaItems) {
            serializer.startTag("", TAG_MEDIA_ITEM);
            serializer.attribute("", "id", mediaItem.getId());
            serializer.attribute("", "type", mediaItem.getClass().getSimpleName());
            serializer.attribute("", ATTR_FILENAME, mediaItem.getFilename());
            serializer.attribute("", ATTR_RENDERING_MODE, Integer.toString(mediaItem.getRenderingMode()));
            if (mediaItem instanceof MediaVideoItem) {
                MediaVideoItem mvi = (MediaVideoItem) mediaItem;
                serializer.attribute("", ATTR_BEGIN_TIME, Long.toString(mvi.getBoundaryBeginTime()));
                serializer.attribute("", ATTR_END_TIME, Long.toString(mvi.getBoundaryEndTime()));
                serializer.attribute("", "volume", Integer.toString(mvi.getVolume()));
                serializer.attribute("", ATTR_MUTED, Boolean.toString(mvi.isMuted()));
                if (mvi.getAudioWaveformFilename() != null) {
                    serializer.attribute("", ATTR_AUDIO_WAVEFORM_FILENAME, mvi.getAudioWaveformFilename());
                }
            } else if (mediaItem instanceof MediaImageItem) {
                serializer.attribute("", "duration", Long.toString(mediaItem.getTimelineDuration()));
            }
            List<Overlay> overlays = mediaItem.getAllOverlays();
            if (overlays.size() > 0) {
                serializer.startTag("", TAG_OVERLAYS);
                for (Overlay overlay : overlays) {
                    serializer.startTag("", TAG_OVERLAY);
                    serializer.attribute("", "id", overlay.getId());
                    serializer.attribute("", "type", overlay.getClass().getSimpleName());
                    serializer.attribute("", ATTR_BEGIN_TIME, Long.toString(overlay.getStartTime()));
                    serializer.attribute("", "duration", Long.toString(overlay.getDuration()));
                    if (overlay instanceof OverlayFrame) {
                        OverlayFrame overlayFrame = (OverlayFrame) overlay;
                        overlayFrame.save(getPath());
                        if (overlayFrame.getBitmapImageFileName() != null) {
                            serializer.attribute("", ATTR_FILENAME, overlayFrame.getBitmapImageFileName());
                        }
                        if (overlayFrame.getFilename() != null) {
                            serializer.attribute("", ATTR_OVERLAY_RGB_FILENAME, overlayFrame.getFilename());
                            serializer.attribute("", ATTR_OVERLAY_FRAME_WIDTH, Integer.toString(overlayFrame.getOverlayFrameWidth()));
                            serializer.attribute("", ATTR_OVERLAY_FRAME_HEIGHT, Integer.toString(overlayFrame.getOverlayFrameHeight()));
                            serializer.attribute("", ATTR_OVERLAY_RESIZED_RGB_FRAME_WIDTH, Integer.toString(overlayFrame.getResizedRGBSizeWidth()));
                            serializer.attribute("", ATTR_OVERLAY_RESIZED_RGB_FRAME_HEIGHT, Integer.toString(overlayFrame.getResizedRGBSizeHeight()));
                        }
                    }
                    serializer.startTag("", TAG_OVERLAY_USER_ATTRIBUTES);
                    Map<String, String> userAttributes = overlay.getUserAttributes();
                    for (String name : userAttributes.keySet()) {
                        String value = userAttributes.get(name);
                        if (value != null) {
                            serializer.attribute("", name, value);
                        }
                    }
                    serializer.endTag("", TAG_OVERLAY_USER_ATTRIBUTES);
                    serializer.endTag("", TAG_OVERLAY);
                }
                serializer.endTag("", TAG_OVERLAYS);
            }
            List<Effect> effects = mediaItem.getAllEffects();
            if (effects.size() > 0) {
                serializer.startTag("", TAG_EFFECTS);
                for (Effect effect : effects) {
                    serializer.startTag("", TAG_EFFECT);
                    serializer.attribute("", "id", effect.getId());
                    serializer.attribute("", "type", effect.getClass().getSimpleName());
                    serializer.attribute("", ATTR_BEGIN_TIME, Long.toString(effect.getStartTime()));
                    serializer.attribute("", "duration", Long.toString(effect.getDuration()));
                    if (effect instanceof EffectColor) {
                        EffectColor colorEffect = (EffectColor) effect;
                        serializer.attribute("", "color_type", Integer.toString(colorEffect.getType()));
                        if (colorEffect.getType() == 1 || colorEffect.getType() == 2) {
                            serializer.attribute("", ATTR_COLOR_EFFECT_VALUE, Integer.toString(colorEffect.getColor()));
                        }
                    } else if (effect instanceof EffectKenBurns) {
                        Rect startRect = ((EffectKenBurns) effect).getStartRect();
                        serializer.attribute("", ATTR_START_RECT_LEFT, Integer.toString(startRect.left));
                        serializer.attribute("", ATTR_START_RECT_TOP, Integer.toString(startRect.top));
                        serializer.attribute("", ATTR_START_RECT_RIGHT, Integer.toString(startRect.right));
                        serializer.attribute("", ATTR_START_RECT_BOTTOM, Integer.toString(startRect.bottom));
                        Rect endRect = ((EffectKenBurns) effect).getEndRect();
                        serializer.attribute("", ATTR_END_RECT_LEFT, Integer.toString(endRect.left));
                        serializer.attribute("", ATTR_END_RECT_TOP, Integer.toString(endRect.top));
                        serializer.attribute("", ATTR_END_RECT_RIGHT, Integer.toString(endRect.right));
                        serializer.attribute("", ATTR_END_RECT_BOTTOM, Integer.toString(endRect.bottom));
                        MediaItem mItem = effect.getMediaItem();
                        if (((MediaImageItem) mItem).getGeneratedImageClip() != null) {
                            serializer.attribute("", ATTR_IS_IMAGE_CLIP_GENERATED, Boolean.toString(true));
                            serializer.attribute("", ATTR_GENERATED_IMAGE_CLIP, ((MediaImageItem) mItem).getGeneratedImageClip());
                        } else {
                            serializer.attribute("", ATTR_IS_IMAGE_CLIP_GENERATED, Boolean.toString(false));
                        }
                    }
                    serializer.endTag("", TAG_EFFECT);
                }
                serializer.endTag("", TAG_EFFECTS);
            }
            serializer.endTag("", TAG_MEDIA_ITEM);
        }
        serializer.endTag("", TAG_MEDIA_ITEMS);
        serializer.startTag("", TAG_TRANSITIONS);
        for (Transition transition : this.mTransitions) {
            serializer.startTag("", TAG_TRANSITION);
            serializer.attribute("", "id", transition.getId());
            serializer.attribute("", "type", transition.getClass().getSimpleName());
            serializer.attribute("", "duration", Long.toString(transition.getDuration()));
            serializer.attribute("", ATTR_BEHAVIOR, Integer.toString(transition.getBehavior()));
            serializer.attribute("", ATTR_IS_TRANSITION_GENERATED, Boolean.toString(transition.isGenerated()));
            if (transition.isGenerated()) {
                serializer.attribute("", ATTR_GENERATED_TRANSITION_CLIP, transition.mFilename);
            }
            MediaItem afterMediaItem = transition.getAfterMediaItem();
            if (afterMediaItem != null) {
                serializer.attribute("", ATTR_AFTER_MEDIA_ITEM_ID, afterMediaItem.getId());
            }
            MediaItem beforeMediaItem = transition.getBeforeMediaItem();
            if (beforeMediaItem != null) {
                serializer.attribute("", ATTR_BEFORE_MEDIA_ITEM_ID, beforeMediaItem.getId());
            }
            if (transition instanceof TransitionSliding) {
                serializer.attribute("", ATTR_DIRECTION, Integer.toString(((TransitionSliding) transition).getDirection()));
            } else if (transition instanceof TransitionAlpha) {
                TransitionAlpha ta = (TransitionAlpha) transition;
                serializer.attribute("", ATTR_BLENDING, Integer.toString(ta.getBlendingPercent()));
                serializer.attribute("", ATTR_INVERT, Boolean.toString(ta.isInvert()));
                if (ta.getMaskFilename() != null) {
                    serializer.attribute("", ATTR_MASK, ta.getMaskFilename());
                }
            }
            serializer.endTag("", TAG_TRANSITION);
        }
        serializer.endTag("", TAG_TRANSITIONS);
        serializer.startTag("", TAG_AUDIO_TRACKS);
        for (AudioTrack at : this.mAudioTracks) {
            serializer.startTag("", TAG_AUDIO_TRACK);
            serializer.attribute("", "id", at.getId());
            serializer.attribute("", ATTR_FILENAME, at.getFilename());
            serializer.attribute("", ATTR_START_TIME, Long.toString(at.getStartTime()));
            serializer.attribute("", ATTR_BEGIN_TIME, Long.toString(at.getBoundaryBeginTime()));
            serializer.attribute("", ATTR_END_TIME, Long.toString(at.getBoundaryEndTime()));
            serializer.attribute("", "volume", Integer.toString(at.getVolume()));
            serializer.attribute("", ATTR_DUCK_ENABLED, Boolean.toString(at.isDuckingEnabled()));
            serializer.attribute("", ATTR_DUCKED_TRACK_VOLUME, Integer.toString(at.getDuckedTrackVolume()));
            serializer.attribute("", ATTR_DUCK_THRESHOLD, Integer.toString(at.getDuckingThreshhold()));
            serializer.attribute("", ATTR_MUTED, Boolean.toString(at.isMuted()));
            serializer.attribute("", ATTR_LOOP, Boolean.toString(at.isLooping()));
            if (at.getAudioWaveformFilename() != null) {
                serializer.attribute("", ATTR_AUDIO_WAVEFORM_FILENAME, at.getAudioWaveformFilename());
            }
            serializer.endTag("", TAG_AUDIO_TRACK);
        }
        serializer.endTag("", TAG_AUDIO_TRACKS);
        serializer.endTag("", TAG_PROJECT);
        serializer.endDocument();
        FileOutputStream out = new FileOutputStream(new File(getPath(), PROJECT_FILENAME));
        out.write(writer.toString().getBytes());
        out.flush();
        out.close();
    }

    @Override // android.media.videoeditor.VideoEditor
    public void setAspectRatio(int aspectRatio) {
        this.mAspectRatio = aspectRatio;
        this.mMANativeHelper.setGeneratePreview(true);
        for (Transition transition : this.mTransitions) {
            transition.invalidate();
        }
        for (MediaItem t : this.mMediaItems) {
            List<Overlay> overlayList = t.getAllOverlays();
            for (Overlay overlay : overlayList) {
                ((OverlayFrame) overlay).invalidateGeneratedFiles();
            }
        }
    }

    @Override // android.media.videoeditor.VideoEditor
    public void startPreview(SurfaceHolder surfaceHolder, long fromMs, long toMs, boolean loop, int callbackAfterFrameCount, VideoEditor.PreviewProgressListener listener) {
        if (surfaceHolder == null) {
            throw new IllegalArgumentException();
        }
        Surface surface = surfaceHolder.getSurface();
        if (surface == null) {
            throw new IllegalArgumentException("Surface could not be retrieved from surface holder");
        }
        if (!surface.isValid()) {
            throw new IllegalStateException("Surface is not valid");
        }
        if (listener == null) {
            throw new IllegalArgumentException();
        }
        if (fromMs >= this.mDurationMs) {
            throw new IllegalArgumentException("Requested time not correct");
        }
        if (fromMs < 0) {
            throw new IllegalArgumentException("Requested time not correct");
        }
        if (!this.mPreviewInProgress) {
            try {
                boolean semAcquireDone = lock(500L);
                if (!semAcquireDone) {
                    throw new IllegalStateException("Timeout waiting for semaphore");
                }
                if (this.mMANativeHelper == null) {
                    throw new IllegalStateException("The video editor is not initialized");
                }
                if (this.mMediaItems.size() > 0) {
                    this.mPreviewInProgress = true;
                    this.mMANativeHelper.previewStoryBoard(this.mMediaItems, this.mTransitions, this.mAudioTracks, null);
                    this.mMANativeHelper.doPreview(surface, fromMs, toMs, loop, callbackAfterFrameCount, listener);
                }
                return;
            } catch (InterruptedException e) {
                Log.w(TAG, "The thread was interrupted", new Throwable());
                throw new IllegalStateException("The thread was interrupted");
            }
        }
        throw new IllegalStateException("Preview already in progress");
    }

    @Override // android.media.videoeditor.VideoEditor
    public long stopPreview() {
        if (this.mPreviewInProgress) {
            try {
                long result = this.mMANativeHelper.stopPreview();
                this.mPreviewInProgress = false;
                unlock();
                return result;
            } catch (Throwable th) {
                this.mPreviewInProgress = false;
                unlock();
                throw th;
            }
        }
        return 0L;
    }

    private void removeAdjacentTransitions(MediaItem mediaItem) {
        Transition beginTransition = mediaItem.getBeginTransition();
        if (beginTransition != null) {
            if (beginTransition.getAfterMediaItem() != null) {
                beginTransition.getAfterMediaItem().setEndTransition(null);
            }
            beginTransition.invalidate();
            this.mTransitions.remove(beginTransition);
        }
        Transition endTransition = mediaItem.getEndTransition();
        if (endTransition != null) {
            if (endTransition.getBeforeMediaItem() != null) {
                endTransition.getBeforeMediaItem().setBeginTransition(null);
            }
            endTransition.invalidate();
            this.mTransitions.remove(endTransition);
        }
        mediaItem.setBeginTransition(null);
        mediaItem.setEndTransition(null);
    }

    private void removeTransitionBefore(int index) {
        MediaItem mediaItem = this.mMediaItems.get(index);
        Iterator<Transition> it = this.mTransitions.iterator();
        while (it.hasNext()) {
            Transition t = it.next();
            if (t.getBeforeMediaItem() == mediaItem) {
                this.mMANativeHelper.setGeneratePreview(true);
                it.remove();
                t.invalidate();
                mediaItem.setBeginTransition(null);
                if (index > 0) {
                    this.mMediaItems.get(index - 1).setEndTransition(null);
                    return;
                }
                return;
            }
        }
    }

    private void removeTransitionAfter(int index) {
        MediaItem mediaItem = this.mMediaItems.get(index);
        Iterator<Transition> it = this.mTransitions.iterator();
        while (it.hasNext()) {
            Transition t = it.next();
            if (t.getAfterMediaItem() == mediaItem) {
                this.mMANativeHelper.setGeneratePreview(true);
                it.remove();
                t.invalidate();
                mediaItem.setEndTransition(null);
                if (index < this.mMediaItems.size() - 1) {
                    this.mMediaItems.get(index + 1).setBeginTransition(null);
                    return;
                }
                return;
            }
        }
    }

    private void computeTimelineDuration() {
        this.mDurationMs = 0L;
        int mediaItemsCount = this.mMediaItems.size();
        for (int i = 0; i < mediaItemsCount; i++) {
            MediaItem mediaItem = this.mMediaItems.get(i);
            this.mDurationMs += mediaItem.getTimelineDuration();
            if (mediaItem.getEndTransition() != null && i < mediaItemsCount - 1) {
                this.mDurationMs -= mediaItem.getEndTransition().getDuration();
            }
        }
    }

    private void generateProjectThumbnail() {
        Bitmap projectBitmap;
        if (new File(this.mProjectPath + Separators.SLASH + VideoEditor.THUMBNAIL_FILENAME).exists()) {
            new File(this.mProjectPath + Separators.SLASH + VideoEditor.THUMBNAIL_FILENAME).delete();
        }
        if (this.mMediaItems.size() > 0) {
            MediaItem mI = this.mMediaItems.get(0);
            int width = (mI.getWidth() * 480) / mI.getHeight();
            String filename = mI.getFilename();
            if (mI instanceof MediaVideoItem) {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(filename);
                Bitmap bitmap = retriever.getFrameAtTime();
                retriever.release();
                if (bitmap == null) {
                    String msg = "Thumbnail extraction from " + filename + " failed";
                    throw new IllegalArgumentException(msg);
                }
                projectBitmap = Bitmap.createScaledBitmap(bitmap, width, 480, true);
            } else {
                try {
                    projectBitmap = mI.getThumbnail(width, 480, 500L);
                } catch (IOException e) {
                    throw new IllegalArgumentException("IO Error creating project thumbnail");
                } catch (IllegalArgumentException e2) {
                    String msg2 = "Project thumbnail extraction from " + filename + " failed";
                    throw new IllegalArgumentException(msg2);
                }
            }
            try {
                try {
                    FileOutputStream stream = new FileOutputStream(this.mProjectPath + Separators.SLASH + VideoEditor.THUMBNAIL_FILENAME);
                    projectBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    stream.flush();
                    stream.close();
                    projectBitmap.recycle();
                } catch (IOException e3) {
                    throw new IllegalArgumentException("Error creating project thumbnail");
                }
            } catch (Throwable th) {
                projectBitmap.recycle();
                throw th;
            }
        }
    }

    @Override // android.media.videoeditor.VideoEditor
    public void clearSurface(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalArgumentException("Invalid surface holder");
        }
        Surface surface = surfaceHolder.getSurface();
        if (surface == null) {
            throw new IllegalArgumentException("Surface could not be retrieved from surface holder");
        }
        if (!surface.isValid()) {
            throw new IllegalStateException("Surface is not valid");
        }
        if (this.mMANativeHelper != null) {
            this.mMANativeHelper.clearPreviewSurface(surface);
        } else {
            Log.w(TAG, "Native helper was not ready!");
        }
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

    private boolean lock(long timeoutMs) throws InterruptedException {
        if (Log.isLoggable(TAG, 3)) {
            Log.d(TAG, "lock: grabbing semaphore with timeout " + timeoutMs, new Throwable());
        }
        boolean acquireSem = this.mLock.tryAcquire(timeoutMs, TimeUnit.MILLISECONDS);
        if (Log.isLoggable(TAG, 3)) {
            Log.d(TAG, "lock: grabbed semaphore status " + acquireSem);
        }
        return acquireSem;
    }

    private void unlock() {
        if (Log.isLoggable(TAG, 3)) {
            Log.d(TAG, "unlock: releasing semaphore");
        }
        this.mLock.release();
    }

    private static void dumpHeap(String filename) throws Exception {
        System.gc();
        System.runFinalization();
        Thread.sleep(1000L);
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            String extDir = Environment.getExternalStorageDirectory().toString();
            if (new File(extDir + Separators.SLASH + filename + ".dump").exists()) {
                new File(extDir + Separators.SLASH + filename + ".dump").delete();
            }
            FileOutputStream ost = new FileOutputStream(extDir + Separators.SLASH + filename + ".dump");
            Debug.dumpNativeHeap(ost.getFD());
            ost.close();
        }
    }
}