package android.media.videoeditor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.videoeditor.MediaArtistNativeHelper;
import android.media.videoeditor.MediaItem;
import android.util.Log;
import android.util.Pair;
import gov.nist.core.Separators;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: MediaImageItem.class */
public class MediaImageItem extends MediaItem {
    private static final String TAG = "MediaImageItem";
    private static final Paint sResizePaint = new Paint(2);
    private final int mWidth;
    private final int mHeight;
    private final int mAspectRatio;
    private long mDurationMs;
    private int mScaledWidth;
    private int mScaledHeight;
    private String mScaledFilename;
    private final VideoEditorImpl mVideoEditor;
    private String mDecodedFilename;
    private int mGeneratedClipHeight;
    private int mGeneratedClipWidth;
    private String mFileName;
    private final MediaArtistNativeHelper mMANativeHelper;

    private MediaImageItem() throws IOException {
        this(null, null, null, 0L, 0);
    }

    public MediaImageItem(VideoEditor editor, String mediaItemId, String filename, long durationMs, int renderingMode) throws IOException {
        super(editor, mediaItemId, filename, renderingMode);
        Bitmap imageBitmap;
        this.mMANativeHelper = ((VideoEditorImpl) editor).getNativeContext();
        this.mVideoEditor = (VideoEditorImpl) editor;
        try {
            MediaArtistNativeHelper.Properties properties = this.mMANativeHelper.getMediaProperties(filename);
            switch (this.mMANativeHelper.getFileType(properties.fileType)) {
                case 5:
                case 8:
                    this.mFileName = filename;
                    BitmapFactory.Options dbo = new BitmapFactory.Options();
                    dbo.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(filename, dbo);
                    this.mWidth = dbo.outWidth;
                    this.mHeight = dbo.outHeight;
                    this.mDurationMs = durationMs;
                    this.mDecodedFilename = String.format(this.mMANativeHelper.getProjectPath() + Separators.SLASH + "decoded" + getId() + ".rgb", new Object[0]);
                    try {
                        this.mAspectRatio = this.mMANativeHelper.getAspectRatio(this.mWidth, this.mHeight);
                        this.mGeneratedClipHeight = 0;
                        this.mGeneratedClipWidth = 0;
                        Pair<Integer, Integer>[] resolutions = MediaProperties.getSupportedResolutions(this.mAspectRatio);
                        Pair<Integer, Integer> maxResolution = resolutions[resolutions.length - 1];
                        if (this.mWidth > maxResolution.first.intValue() || this.mHeight > maxResolution.second.intValue()) {
                            imageBitmap = scaleImage(filename, maxResolution.first.intValue(), maxResolution.second.intValue());
                            this.mScaledFilename = String.format(this.mMANativeHelper.getProjectPath() + Separators.SLASH + "scaled" + getId() + ".JPG", new Object[0]);
                            if (!new File(this.mScaledFilename).exists()) {
                                this.mRegenerateClip = true;
                                FileOutputStream f1 = new FileOutputStream(this.mScaledFilename);
                                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 50, f1);
                                f1.close();
                            }
                            this.mScaledWidth = (imageBitmap.getWidth() >> 1) << 1;
                            this.mScaledHeight = (imageBitmap.getHeight() >> 1) << 1;
                        } else {
                            this.mScaledFilename = filename;
                            this.mScaledWidth = (this.mWidth >> 1) << 1;
                            this.mScaledHeight = (this.mHeight >> 1) << 1;
                            imageBitmap = BitmapFactory.decodeFile(this.mScaledFilename);
                        }
                        int newWidth = this.mScaledWidth;
                        int newHeight = this.mScaledHeight;
                        if (!new File(this.mDecodedFilename).exists()) {
                            FileOutputStream fl = new FileOutputStream(this.mDecodedFilename);
                            DataOutputStream dos = new DataOutputStream(fl);
                            int[] framingBuffer = new int[newWidth];
                            ByteBuffer byteBuffer = ByteBuffer.allocate(framingBuffer.length * 4);
                            byte[] array = byteBuffer.array();
                            for (int tmp = 0; tmp < newHeight; tmp++) {
                                imageBitmap.getPixels(framingBuffer, 0, this.mScaledWidth, 0, tmp, newWidth, 1);
                                IntBuffer intBuffer = byteBuffer.asIntBuffer();
                                intBuffer.put(framingBuffer, 0, newWidth);
                                dos.write(array);
                            }
                            fl.close();
                        }
                        imageBitmap.recycle();
                        return;
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Null width and height");
                    }
                default:
                    throw new IllegalArgumentException("Unsupported Input File Type");
            }
        } catch (Exception e2) {
            throw new IllegalArgumentException("Unsupported file or file not found: " + filename);
        }
    }

    @Override // android.media.videoeditor.MediaItem
    public int getFileType() {
        if (this.mFilename.endsWith(".jpg") || this.mFilename.endsWith(".jpeg") || this.mFilename.endsWith(".JPG") || this.mFilename.endsWith(".JPEG")) {
            return 5;
        }
        if (this.mFilename.endsWith(".png") || this.mFilename.endsWith(".PNG")) {
            return 8;
        }
        return 255;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getScaledImageFileName() {
        return this.mScaledFilename;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getGeneratedClipHeight() {
        return this.mGeneratedClipHeight;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getGeneratedClipWidth() {
        return this.mGeneratedClipWidth;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getDecodedImageFileName() {
        return this.mDecodedFilename;
    }

    @Override // android.media.videoeditor.MediaItem
    public int getWidth() {
        return this.mWidth;
    }

    @Override // android.media.videoeditor.MediaItem
    public int getHeight() {
        return this.mHeight;
    }

    public int getScaledWidth() {
        return this.mScaledWidth;
    }

    public int getScaledHeight() {
        return this.mScaledHeight;
    }

    @Override // android.media.videoeditor.MediaItem
    public int getAspectRatio() {
        return this.mAspectRatio;
    }

    public void setDuration(long durationMs) {
        if (durationMs == this.mDurationMs) {
            return;
        }
        this.mMANativeHelper.setGeneratePreview(true);
        invalidateEndTransition();
        this.mDurationMs = durationMs;
        adjustTransitions();
        List<Overlay> adjustedOverlays = adjustOverlays();
        List<Effect> adjustedEffects = adjustEffects();
        invalidateBeginTransition(adjustedEffects, adjustedOverlays);
        invalidateEndTransition();
        if (getGeneratedImageClip() != null) {
            new File(getGeneratedImageClip()).delete();
            setGeneratedImageClip(null);
            super.setRegenerateClip(true);
        }
        this.mVideoEditor.updateTimelineDuration();
    }

    private void invalidateBeginTransition(List<Effect> effects, List<Overlay> overlays) {
        if (this.mBeginTransition != null && this.mBeginTransition.isGenerated()) {
            long transitionDurationMs = this.mBeginTransition.getDuration();
            Iterator i$ = effects.iterator();
            while (true) {
                if (!i$.hasNext()) {
                    break;
                }
                Effect effect = i$.next();
                if (effect.getStartTime() < transitionDurationMs) {
                    this.mBeginTransition.invalidate();
                    break;
                }
            }
            if (this.mBeginTransition.isGenerated()) {
                for (Overlay overlay : overlays) {
                    if (overlay.getStartTime() < transitionDurationMs) {
                        this.mBeginTransition.invalidate();
                        return;
                    }
                }
            }
        }
    }

    private void invalidateEndTransition() {
        if (this.mEndTransition != null && this.mEndTransition.isGenerated()) {
            long transitionDurationMs = this.mEndTransition.getDuration();
            List<Effect> effects = getAllEffects();
            Iterator i$ = effects.iterator();
            while (true) {
                if (!i$.hasNext()) {
                    break;
                }
                Effect effect = i$.next();
                if (effect.getStartTime() + effect.getDuration() > this.mDurationMs - transitionDurationMs) {
                    this.mEndTransition.invalidate();
                    break;
                }
            }
            if (this.mEndTransition.isGenerated()) {
                List<Overlay> overlays = getAllOverlays();
                for (Overlay overlay : overlays) {
                    if (overlay.getStartTime() + overlay.getDuration() > this.mDurationMs - transitionDurationMs) {
                        this.mEndTransition.invalidate();
                        return;
                    }
                }
            }
        }
    }

    private List<Effect> adjustEffects() {
        long effectStartTimeMs;
        long effectDurationMs;
        List<Effect> adjustedEffects = new ArrayList<>();
        List<Effect> effects = getAllEffects();
        for (Effect effect : effects) {
            if (effect.getStartTime() > getDuration()) {
                effectStartTimeMs = 0;
            } else {
                effectStartTimeMs = effect.getStartTime();
            }
            if (effectStartTimeMs + effect.getDuration() > getDuration()) {
                effectDurationMs = getDuration() - effectStartTimeMs;
            } else {
                effectDurationMs = effect.getDuration();
            }
            if (effectStartTimeMs != effect.getStartTime() || effectDurationMs != effect.getDuration()) {
                effect.setStartTimeAndDuration(effectStartTimeMs, effectDurationMs);
                adjustedEffects.add(effect);
            }
        }
        return adjustedEffects;
    }

    private List<Overlay> adjustOverlays() {
        long overlayStartTimeMs;
        long overlayDurationMs;
        List<Overlay> adjustedOverlays = new ArrayList<>();
        List<Overlay> overlays = getAllOverlays();
        for (Overlay overlay : overlays) {
            if (overlay.getStartTime() > getDuration()) {
                overlayStartTimeMs = 0;
            } else {
                overlayStartTimeMs = overlay.getStartTime();
            }
            if (overlayStartTimeMs + overlay.getDuration() > getDuration()) {
                overlayDurationMs = getDuration() - overlayStartTimeMs;
            } else {
                overlayDurationMs = overlay.getDuration();
            }
            if (overlayStartTimeMs != overlay.getStartTime() || overlayDurationMs != overlay.getDuration()) {
                overlay.setStartTimeAndDuration(overlayStartTimeMs, overlayDurationMs);
                adjustedOverlays.add(overlay);
            }
        }
        return adjustedOverlays;
    }

    private int getWidthByAspectRatioAndHeight(int aspectRatio, int height) {
        int width = 0;
        switch (aspectRatio) {
            case 1:
                if (height == 480) {
                    width = 720;
                    break;
                } else if (height == 720) {
                    width = 1080;
                    break;
                }
                break;
            case 2:
                if (height == 360) {
                    width = 640;
                    break;
                } else if (height == 480) {
                    width = 854;
                    break;
                } else if (height == 720) {
                    width = 1280;
                    break;
                } else if (height == 1080) {
                    width = 1920;
                    break;
                }
                break;
            case 3:
                if (height == 480) {
                    width = 640;
                }
                if (height == 720) {
                    width = 960;
                    break;
                }
                break;
            case 4:
                if (height == 480) {
                    width = 800;
                    break;
                }
                break;
            case 5:
                if (height == 144) {
                    width = 176;
                    break;
                }
                break;
            default:
                throw new IllegalArgumentException("Illegal arguments for aspectRatio");
        }
        return width;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.media.videoeditor.MediaItem
    public void setGeneratedImageClip(String generatedFilePath) {
        super.setGeneratedImageClip(generatedFilePath);
        this.mGeneratedClipHeight = getScaledHeight();
        this.mGeneratedClipWidth = getWidthByAspectRatioAndHeight(this.mVideoEditor.getAspectRatio(), this.mGeneratedClipHeight);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.media.videoeditor.MediaItem
    public String getGeneratedImageClip() {
        return super.getGeneratedImageClip();
    }

    @Override // android.media.videoeditor.MediaItem
    public long getDuration() {
        return this.mDurationMs;
    }

    @Override // android.media.videoeditor.MediaItem
    public long getTimelineDuration() {
        return this.mDurationMs;
    }

    @Override // android.media.videoeditor.MediaItem
    public Bitmap getThumbnail(int width, int height, long timeMs) throws IOException {
        if (getGeneratedImageClip() != null) {
            return this.mMANativeHelper.getPixels(getGeneratedImageClip(), width, height, timeMs, 0);
        }
        return scaleImage(this.mFilename, width, height);
    }

    @Override // android.media.videoeditor.MediaItem
    public void getThumbnailList(int width, int height, long startMs, long endMs, int thumbnailCount, int[] indices, MediaItem.GetThumbnailListCallback callback) throws IOException {
        if (getGeneratedImageClip() == null) {
            Bitmap thumbnail = scaleImage(this.mFilename, width, height);
            for (int i : indices) {
                callback.onThumbnail(thumbnail, i);
            }
        } else if (startMs > endMs) {
            throw new IllegalArgumentException("Start time is greater than end time");
        } else {
            if (endMs > this.mDurationMs) {
                throw new IllegalArgumentException("End time is greater than file duration");
            }
            this.mMANativeHelper.getPixelsList(getGeneratedImageClip(), width, height, startMs, endMs, thumbnailCount, indices, callback, 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.media.videoeditor.MediaItem
    public void invalidateTransitions(long startTimeMs, long durationMs) {
        if (this.mBeginTransition != null && isOverlapping(startTimeMs, durationMs, 0L, this.mBeginTransition.getDuration())) {
            this.mBeginTransition.invalidate();
        }
        if (this.mEndTransition != null) {
            long transitionDurationMs = this.mEndTransition.getDuration();
            if (isOverlapping(startTimeMs, durationMs, getDuration() - transitionDurationMs, transitionDurationMs)) {
                this.mEndTransition.invalidate();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.media.videoeditor.MediaItem
    public void invalidateTransitions(long oldStartTimeMs, long oldDurationMs, long newStartTimeMs, long newDurationMs) {
        if (this.mBeginTransition != null) {
            long transitionDurationMs = this.mBeginTransition.getDuration();
            boolean oldOverlap = isOverlapping(oldStartTimeMs, oldDurationMs, 0L, transitionDurationMs);
            boolean newOverlap = isOverlapping(newStartTimeMs, newDurationMs, 0L, transitionDurationMs);
            if (newOverlap != oldOverlap) {
                this.mBeginTransition.invalidate();
            } else if (newOverlap && (oldStartTimeMs != newStartTimeMs || oldStartTimeMs + oldDurationMs <= transitionDurationMs || newStartTimeMs + newDurationMs <= transitionDurationMs)) {
                this.mBeginTransition.invalidate();
            }
        }
        if (this.mEndTransition != null) {
            long transitionDurationMs2 = this.mEndTransition.getDuration();
            boolean oldOverlap2 = isOverlapping(oldStartTimeMs, oldDurationMs, this.mDurationMs - transitionDurationMs2, transitionDurationMs2);
            boolean newOverlap2 = isOverlapping(newStartTimeMs, newDurationMs, this.mDurationMs - transitionDurationMs2, transitionDurationMs2);
            if (newOverlap2 != oldOverlap2) {
                this.mEndTransition.invalidate();
            } else if (newOverlap2) {
                if (oldStartTimeMs + oldDurationMs != newStartTimeMs + newDurationMs || oldStartTimeMs > this.mDurationMs - transitionDurationMs2 || newStartTimeMs > this.mDurationMs - transitionDurationMs2) {
                    this.mEndTransition.invalidate();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void invalidate() {
        if (getGeneratedImageClip() != null) {
            new File(getGeneratedImageClip()).delete();
            setGeneratedImageClip(null);
            setRegenerateClip(true);
        }
        if (this.mScaledFilename != null) {
            if (this.mFileName != this.mScaledFilename) {
                new File(this.mScaledFilename).delete();
            }
            this.mScaledFilename = null;
        }
        if (this.mDecodedFilename != null) {
            new File(this.mDecodedFilename).delete();
            this.mDecodedFilename = null;
        }
    }

    private MediaArtistNativeHelper.ClipSettings getKenBurns(EffectKenBurns effectKB) {
        Rect start = new Rect();
        Rect end = new Rect();
        MediaArtistNativeHelper.ClipSettings clipSettings = new MediaArtistNativeHelper.ClipSettings();
        effectKB.getKenBurnsSettings(start, end);
        int width = getWidth();
        int height = getHeight();
        if (start.left < 0 || start.left > width || start.right < 0 || start.right > width || start.top < 0 || start.top > height || start.bottom < 0 || start.bottom > height || end.left < 0 || end.left > width || end.right < 0 || end.right > width || end.top < 0 || end.top > height || end.bottom < 0 || end.bottom > height) {
            throw new IllegalArgumentException("Illegal arguments for KebBurns");
        }
        if ((width - (start.right - start.left) == 0 || height - (start.bottom - start.top) == 0) && (width - (end.right - end.left) == 0 || height - (end.bottom - end.top) == 0)) {
            setRegenerateClip(false);
            clipSettings.clipPath = getDecodedImageFileName();
            clipSettings.fileType = 5;
            clipSettings.beginCutTime = 0;
            clipSettings.endCutTime = (int) getTimelineDuration();
            clipSettings.beginCutPercent = 0;
            clipSettings.endCutPercent = 0;
            clipSettings.panZoomEnabled = false;
            clipSettings.panZoomPercentStart = 0;
            clipSettings.panZoomTopLeftXStart = 0;
            clipSettings.panZoomTopLeftYStart = 0;
            clipSettings.panZoomPercentEnd = 0;
            clipSettings.panZoomTopLeftXEnd = 0;
            clipSettings.panZoomTopLeftYEnd = 0;
            clipSettings.mediaRendering = this.mMANativeHelper.getMediaItemRenderingMode(getRenderingMode());
            clipSettings.rgbWidth = getScaledWidth();
            clipSettings.rgbHeight = getScaledHeight();
            return clipSettings;
        }
        int PanZoomXa = (1000 * start.width()) / width;
        int PanZoomXb = (1000 * end.width()) / width;
        clipSettings.clipPath = getDecodedImageFileName();
        clipSettings.fileType = this.mMANativeHelper.getMediaItemFileType(getFileType());
        clipSettings.beginCutTime = 0;
        clipSettings.endCutTime = (int) getTimelineDuration();
        clipSettings.beginCutPercent = 0;
        clipSettings.endCutPercent = 0;
        clipSettings.panZoomEnabled = true;
        clipSettings.panZoomPercentStart = PanZoomXa;
        clipSettings.panZoomTopLeftXStart = (start.left * 1000) / width;
        clipSettings.panZoomTopLeftYStart = (start.top * 1000) / height;
        clipSettings.panZoomPercentEnd = PanZoomXb;
        clipSettings.panZoomTopLeftXEnd = (end.left * 1000) / width;
        clipSettings.panZoomTopLeftYEnd = (end.top * 1000) / height;
        clipSettings.mediaRendering = this.mMANativeHelper.getMediaItemRenderingMode(getRenderingMode());
        clipSettings.rgbWidth = getScaledWidth();
        clipSettings.rgbHeight = getScaledHeight();
        return clipSettings;
    }

    MediaArtistNativeHelper.ClipSettings generateKenburnsClip(EffectKenBurns effectKB) {
        MediaArtistNativeHelper.EditSettings editSettings = new MediaArtistNativeHelper.EditSettings();
        editSettings.clipSettingsArray = new MediaArtistNativeHelper.ClipSettings[1];
        MediaArtistNativeHelper.ClipSettings clipSettings = new MediaArtistNativeHelper.ClipSettings();
        initClipSettings(clipSettings);
        editSettings.clipSettingsArray[0] = getKenBurns(effectKB);
        if (getGeneratedImageClip() == null && getRegenerateClip()) {
            String output = this.mMANativeHelper.generateKenBurnsClip(editSettings, this);
            setGeneratedImageClip(output);
            setRegenerateClip(false);
            clipSettings.clipPath = output;
            clipSettings.fileType = 0;
            this.mGeneratedClipHeight = getScaledHeight();
            this.mGeneratedClipWidth = getWidthByAspectRatioAndHeight(this.mVideoEditor.getAspectRatio(), this.mGeneratedClipHeight);
        } else if (getGeneratedImageClip() == null) {
            clipSettings.clipPath = getDecodedImageFileName();
            clipSettings.fileType = 5;
            clipSettings.rgbWidth = getScaledWidth();
            clipSettings.rgbHeight = getScaledHeight();
        } else {
            clipSettings.clipPath = getGeneratedImageClip();
            clipSettings.fileType = 0;
        }
        clipSettings.mediaRendering = this.mMANativeHelper.getMediaItemRenderingMode(getRenderingMode());
        clipSettings.beginCutTime = 0;
        clipSettings.endCutTime = (int) getTimelineDuration();
        return clipSettings;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MediaArtistNativeHelper.ClipSettings getImageClipProperties() {
        MediaArtistNativeHelper.ClipSettings clipSettings = new MediaArtistNativeHelper.ClipSettings();
        EffectKenBurns effectKB = null;
        boolean effectKBPresent = false;
        List<Effect> effects = getAllEffects();
        Iterator i$ = effects.iterator();
        while (true) {
            if (!i$.hasNext()) {
                break;
            }
            Effect effect = i$.next();
            if (effect instanceof EffectKenBurns) {
                effectKB = (EffectKenBurns) effect;
                effectKBPresent = true;
                break;
            }
        }
        if (effectKBPresent) {
            clipSettings = generateKenburnsClip(effectKB);
        } else {
            initClipSettings(clipSettings);
            clipSettings.clipPath = getDecodedImageFileName();
            clipSettings.fileType = 5;
            clipSettings.beginCutTime = 0;
            clipSettings.endCutTime = (int) getTimelineDuration();
            clipSettings.mediaRendering = this.mMANativeHelper.getMediaItemRenderingMode(getRenderingMode());
            clipSettings.rgbWidth = getScaledWidth();
            clipSettings.rgbHeight = getScaledHeight();
        }
        return clipSettings;
    }

    private Bitmap scaleImage(String filename, int width, int height) throws IOException {
        float bitmapWidth;
        float bitmapHeight;
        Bitmap srcBitmap;
        BitmapFactory.Options dbo = new BitmapFactory.Options();
        dbo.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filename, dbo);
        int nativeWidth = dbo.outWidth;
        int nativeHeight = dbo.outHeight;
        if (Log.isLoggable(TAG, 3)) {
            Log.d(TAG, "generateThumbnail: Input: " + nativeWidth + "x" + nativeHeight + ", resize to: " + width + "x" + height);
        }
        if (nativeWidth > width || nativeHeight > height) {
            float dx = nativeWidth / width;
            float dy = nativeHeight / height;
            if (dx > dy) {
                bitmapWidth = width;
                if (nativeHeight / dx < height) {
                    bitmapHeight = (float) Math.ceil(nativeHeight / dx);
                } else {
                    bitmapHeight = (float) Math.floor(nativeHeight / dx);
                }
            } else {
                if (nativeWidth / dy > width) {
                    bitmapWidth = (float) Math.floor(nativeWidth / dy);
                } else {
                    bitmapWidth = (float) Math.ceil(nativeWidth / dy);
                }
                bitmapHeight = height;
            }
            int sampleSize = (int) Math.ceil(Math.max(nativeWidth / bitmapWidth, nativeHeight / bitmapHeight));
            int sampleSize2 = nextPowerOf2(sampleSize);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = sampleSize2;
            srcBitmap = BitmapFactory.decodeFile(filename, options);
        } else {
            bitmapWidth = width;
            bitmapHeight = height;
            srcBitmap = BitmapFactory.decodeFile(filename);
        }
        if (srcBitmap == null) {
            Log.e(TAG, "generateThumbnail: Cannot decode image bytes");
            throw new IOException("Cannot decode file: " + this.mFilename);
        }
        Bitmap bitmap = Bitmap.createBitmap((int) bitmapWidth, (int) bitmapHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(srcBitmap, new Rect(0, 0, srcBitmap.getWidth(), srcBitmap.getHeight()), new Rect(0, 0, (int) bitmapWidth, (int) bitmapHeight), sResizePaint);
        canvas.setBitmap(null);
        srcBitmap.recycle();
        return bitmap;
    }

    public static int nextPowerOf2(int n) {
        int n2 = n - 1;
        int n3 = n2 | (n2 >>> 16);
        int n4 = n3 | (n3 >>> 8);
        int n5 = n4 | (n4 >>> 4);
        int n6 = n5 | (n5 >>> 2);
        return (n6 | (n6 >>> 1)) + 1;
    }
}