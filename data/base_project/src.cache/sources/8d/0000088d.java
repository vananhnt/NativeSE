package android.media.videoeditor;

import android.graphics.Bitmap;
import android.media.videoeditor.MediaArtistNativeHelper;
import gov.nist.core.Separators;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

/* loaded from: MediaItem.class */
public abstract class MediaItem {
    public static final int END_OF_FILE = -1;
    public static final int RENDERING_MODE_BLACK_BORDER = 0;
    public static final int RENDERING_MODE_STRETCH = 1;
    public static final int RENDERING_MODE_CROPPING = 2;
    private final String mUniqueId;
    protected final String mFilename;
    private final List<Effect> mEffects;
    private final List<Overlay> mOverlays;
    private int mRenderingMode;
    private final MediaArtistNativeHelper mMANativeHelper;
    private final String mProjectPath;
    protected Transition mBeginTransition;
    protected Transition mEndTransition;
    protected String mGeneratedImageClip;
    protected boolean mRegenerateClip;
    private boolean mBlankFrameGenerated = false;
    private String mBlankFrameFilename = null;

    /* loaded from: MediaItem$GetThumbnailListCallback.class */
    public interface GetThumbnailListCallback {
        void onThumbnail(Bitmap bitmap, int i);
    }

    public abstract long getTimelineDuration();

    public abstract long getDuration();

    public abstract int getFileType();

    public abstract int getWidth();

    public abstract int getHeight();

    public abstract int getAspectRatio();

    public abstract Bitmap getThumbnail(int i, int i2, long j) throws IOException;

    public abstract void getThumbnailList(int i, int i2, long j, long j2, int i3, int[] iArr, GetThumbnailListCallback getThumbnailListCallback) throws IOException;

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void invalidateTransitions(long j, long j2);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void invalidateTransitions(long j, long j2, long j3, long j4);

    /* JADX INFO: Access modifiers changed from: protected */
    public MediaItem(VideoEditor editor, String mediaItemId, String filename, int renderingMode) throws IOException {
        if (filename == null) {
            throw new IllegalArgumentException("MediaItem : filename is null");
        }
        File file = new File(filename);
        if (!file.exists()) {
            throw new IOException(filename + " not found ! ");
        }
        if (VideoEditor.MAX_SUPPORTED_FILE_SIZE <= file.length()) {
            throw new IllegalArgumentException("File size is more than 2GB");
        }
        this.mUniqueId = mediaItemId;
        this.mFilename = filename;
        this.mRenderingMode = renderingMode;
        this.mEffects = new ArrayList();
        this.mOverlays = new ArrayList();
        this.mBeginTransition = null;
        this.mEndTransition = null;
        this.mMANativeHelper = ((VideoEditorImpl) editor).getNativeContext();
        this.mProjectPath = editor.getPath();
        this.mRegenerateClip = false;
        this.mGeneratedImageClip = null;
    }

    public String getId() {
        return this.mUniqueId;
    }

    public String getFilename() {
        return this.mFilename;
    }

    public void setRenderingMode(int renderingMode) {
        switch (renderingMode) {
            case 0:
            case 1:
            case 2:
                this.mMANativeHelper.setGeneratePreview(true);
                this.mRenderingMode = renderingMode;
                if (this.mBeginTransition != null) {
                    this.mBeginTransition.invalidate();
                }
                if (this.mEndTransition != null) {
                    this.mEndTransition.invalidate();
                }
                for (Overlay overlay : this.mOverlays) {
                    ((OverlayFrame) overlay).invalidateGeneratedFiles();
                }
                return;
            default:
                throw new IllegalArgumentException("Invalid Rendering Mode");
        }
    }

    public int getRenderingMode() {
        return this.mRenderingMode;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setBeginTransition(Transition transition) {
        this.mBeginTransition = transition;
    }

    public Transition getBeginTransition() {
        return this.mBeginTransition;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setEndTransition(Transition transition) {
        this.mEndTransition = transition;
    }

    public Transition getEndTransition() {
        return this.mEndTransition;
    }

    public void addEffect(Effect effect) {
        if (effect == null) {
            throw new IllegalArgumentException("NULL effect cannot be applied");
        }
        if (effect.getMediaItem() != this) {
            throw new IllegalArgumentException("Media item mismatch");
        }
        if (this.mEffects.contains(effect)) {
            throw new IllegalArgumentException("Effect already exists: " + effect.getId());
        }
        if (effect.getStartTime() + effect.getDuration() > getDuration()) {
            throw new IllegalArgumentException("Effect start time + effect duration > media clip duration");
        }
        this.mMANativeHelper.setGeneratePreview(true);
        this.mEffects.add(effect);
        invalidateTransitions(effect.getStartTime(), effect.getDuration());
        if (effect instanceof EffectKenBurns) {
            this.mRegenerateClip = true;
        }
    }

    public Effect removeEffect(String effectId) {
        for (Effect effect : this.mEffects) {
            if (effect.getId().equals(effectId)) {
                this.mMANativeHelper.setGeneratePreview(true);
                this.mEffects.remove(effect);
                invalidateTransitions(effect.getStartTime(), effect.getDuration());
                if (effect instanceof EffectKenBurns) {
                    if (this.mGeneratedImageClip != null) {
                        new File(this.mGeneratedImageClip).delete();
                        this.mGeneratedImageClip = null;
                    }
                    this.mRegenerateClip = false;
                }
                return effect;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setGeneratedImageClip(String generatedFilePath) {
        this.mGeneratedImageClip = generatedFilePath;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getGeneratedImageClip() {
        return this.mGeneratedImageClip;
    }

    public Effect getEffect(String effectId) {
        for (Effect effect : this.mEffects) {
            if (effect.getId().equals(effectId)) {
                return effect;
            }
        }
        return null;
    }

    public List<Effect> getAllEffects() {
        return this.mEffects;
    }

    public void addOverlay(Overlay overlay) throws FileNotFoundException, IOException {
        int scaledWidth;
        int scaledHeight;
        if (overlay == null) {
            throw new IllegalArgumentException("NULL Overlay cannot be applied");
        }
        if (overlay.getMediaItem() != this) {
            throw new IllegalArgumentException("Media item mismatch");
        }
        if (this.mOverlays.contains(overlay)) {
            throw new IllegalArgumentException("Overlay already exists: " + overlay.getId());
        }
        if (overlay.getStartTime() + overlay.getDuration() > getDuration()) {
            throw new IllegalArgumentException("Overlay start time + overlay duration > media clip duration");
        }
        if (overlay instanceof OverlayFrame) {
            OverlayFrame frame = (OverlayFrame) overlay;
            Bitmap bitmap = frame.getBitmap();
            if (bitmap == null) {
                throw new IllegalArgumentException("Overlay bitmap not specified");
            }
            if (this instanceof MediaVideoItem) {
                scaledWidth = getWidth();
                scaledHeight = getHeight();
            } else {
                scaledWidth = ((MediaImageItem) this).getScaledWidth();
                scaledHeight = ((MediaImageItem) this).getScaledHeight();
            }
            if (bitmap.getWidth() != scaledWidth || bitmap.getHeight() != scaledHeight) {
                throw new IllegalArgumentException("Bitmap dimensions must match media item dimensions");
            }
            this.mMANativeHelper.setGeneratePreview(true);
            ((OverlayFrame) overlay).save(this.mProjectPath);
            this.mOverlays.add(overlay);
            invalidateTransitions(overlay.getStartTime(), overlay.getDuration());
            return;
        }
        throw new IllegalArgumentException("Overlay not supported");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setRegenerateClip(boolean flag) {
        this.mRegenerateClip = flag;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean getRegenerateClip() {
        return this.mRegenerateClip;
    }

    public Overlay removeOverlay(String overlayId) {
        for (Overlay overlay : this.mOverlays) {
            if (overlay.getId().equals(overlayId)) {
                this.mMANativeHelper.setGeneratePreview(true);
                this.mOverlays.remove(overlay);
                if (overlay instanceof OverlayFrame) {
                    ((OverlayFrame) overlay).invalidate();
                }
                invalidateTransitions(overlay.getStartTime(), overlay.getDuration());
                return overlay;
            }
        }
        return null;
    }

    public Overlay getOverlay(String overlayId) {
        for (Overlay overlay : this.mOverlays) {
            if (overlay.getId().equals(overlayId)) {
                return overlay;
            }
        }
        return null;
    }

    public List<Overlay> getAllOverlays() {
        return this.mOverlays;
    }

    public Bitmap[] getThumbnailList(int width, int height, long startMs, long endMs, int thumbnailCount) throws IOException {
        final Bitmap[] bitmaps = new Bitmap[thumbnailCount];
        int[] indices = new int[thumbnailCount];
        for (int i = 0; i < thumbnailCount; i++) {
            indices[i] = i;
        }
        getThumbnailList(width, height, startMs, endMs, thumbnailCount, indices, new GetThumbnailListCallback() { // from class: android.media.videoeditor.MediaItem.1
            @Override // android.media.videoeditor.MediaItem.GetThumbnailListCallback
            public void onThumbnail(Bitmap bitmap, int index) {
                bitmaps[index] = bitmap;
            }
        });
        return bitmaps;
    }

    public boolean equals(Object object) {
        if (!(object instanceof MediaItem)) {
            return false;
        }
        return this.mUniqueId.equals(((MediaItem) object).mUniqueId);
    }

    public int hashCode() {
        return this.mUniqueId.hashCode();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isOverlapping(long startTimeMs1, long durationMs1, long startTimeMs2, long durationMs2) {
        if (startTimeMs1 + durationMs1 <= startTimeMs2 || startTimeMs1 >= startTimeMs2 + durationMs2) {
            return false;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void adjustTransitions() {
        if (this.mBeginTransition != null) {
            long maxDurationMs = this.mBeginTransition.getMaximumDuration();
            if (this.mBeginTransition.getDuration() > maxDurationMs) {
                this.mBeginTransition.setDuration(maxDurationMs);
            }
        }
        if (this.mEndTransition != null) {
            long maxDurationMs2 = this.mEndTransition.getMaximumDuration();
            if (this.mEndTransition.getDuration() > maxDurationMs2) {
                this.mEndTransition.setDuration(maxDurationMs2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MediaArtistNativeHelper getNativeContext() {
        return this.mMANativeHelper;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void initClipSettings(MediaArtistNativeHelper.ClipSettings clipSettings) {
        clipSettings.clipPath = null;
        clipSettings.clipDecodedPath = null;
        clipSettings.clipOriginalPath = null;
        clipSettings.fileType = 0;
        clipSettings.endCutTime = 0;
        clipSettings.beginCutTime = 0;
        clipSettings.beginCutPercent = 0;
        clipSettings.endCutPercent = 0;
        clipSettings.panZoomEnabled = false;
        clipSettings.panZoomPercentStart = 0;
        clipSettings.panZoomTopLeftXStart = 0;
        clipSettings.panZoomTopLeftYStart = 0;
        clipSettings.panZoomPercentEnd = 0;
        clipSettings.panZoomTopLeftXEnd = 0;
        clipSettings.panZoomTopLeftYEnd = 0;
        clipSettings.mediaRendering = 0;
        clipSettings.rgbWidth = 0;
        clipSettings.rgbHeight = 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MediaArtistNativeHelper.ClipSettings getClipSettings() {
        MediaArtistNativeHelper.ClipSettings clipSettings = new MediaArtistNativeHelper.ClipSettings();
        initClipSettings(clipSettings);
        if (this instanceof MediaVideoItem) {
            MediaVideoItem mVI = (MediaVideoItem) this;
            clipSettings.clipPath = mVI.getFilename();
            clipSettings.fileType = this.mMANativeHelper.getMediaItemFileType(mVI.getFileType());
            clipSettings.beginCutTime = (int) mVI.getBoundaryBeginTime();
            clipSettings.endCutTime = (int) mVI.getBoundaryEndTime();
            clipSettings.mediaRendering = this.mMANativeHelper.getMediaItemRenderingMode(mVI.getRenderingMode());
        } else if (this instanceof MediaImageItem) {
            MediaImageItem mII = (MediaImageItem) this;
            clipSettings = mII.getImageClipProperties();
        }
        return clipSettings;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void generateBlankFrame(MediaArtistNativeHelper.ClipSettings clipSettings) {
        if (!this.mBlankFrameGenerated) {
            this.mBlankFrameFilename = String.format(this.mProjectPath + Separators.SLASH + "ghost.rgb", new Object[0]);
            FileOutputStream fl = null;
            try {
                fl = new FileOutputStream(this.mBlankFrameFilename);
            } catch (IOException e) {
            }
            DataOutputStream dos = new DataOutputStream(fl);
            int[] framingBuffer = new int[64];
            ByteBuffer byteBuffer = ByteBuffer.allocate(framingBuffer.length * 4);
            byte[] array = byteBuffer.array();
            for (int tmp = 0; tmp < 64; tmp++) {
                IntBuffer intBuffer = byteBuffer.asIntBuffer();
                intBuffer.put(framingBuffer, 0, 64);
                try {
                    dos.write(array);
                } catch (IOException e2) {
                }
            }
            try {
                fl.close();
            } catch (IOException e3) {
            }
            this.mBlankFrameGenerated = true;
        }
        clipSettings.clipPath = this.mBlankFrameFilename;
        clipSettings.fileType = 5;
        clipSettings.beginCutTime = 0;
        clipSettings.endCutTime = 0;
        clipSettings.mediaRendering = 0;
        clipSettings.rgbWidth = 64;
        clipSettings.rgbHeight = 64;
    }

    void invalidateBlankFrame() {
        if (this.mBlankFrameFilename != null && new File(this.mBlankFrameFilename).exists()) {
            new File(this.mBlankFrameFilename).delete();
            this.mBlankFrameFilename = null;
        }
    }
}