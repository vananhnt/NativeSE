package android.media.videoeditor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import java.io.IOException;
import java.util.List;

/* loaded from: VideoEditor.class */
public interface VideoEditor {
    public static final String THUMBNAIL_FILENAME = "thumbnail.jpg";
    public static final int DURATION_OF_STORYBOARD = -1;
    public static final long MAX_SUPPORTED_FILE_SIZE = 2147483648L;

    /* loaded from: VideoEditor$ExportProgressListener.class */
    public interface ExportProgressListener {
        void onProgress(VideoEditor videoEditor, String str, int i);
    }

    /* loaded from: VideoEditor$MediaProcessingProgressListener.class */
    public interface MediaProcessingProgressListener {
        public static final int ACTION_ENCODE = 1;
        public static final int ACTION_DECODE = 2;

        void onProgress(Object obj, int i, int i2);
    }

    /* loaded from: VideoEditor$PreviewProgressListener.class */
    public interface PreviewProgressListener {
        void onProgress(VideoEditor videoEditor, long j, OverlayData overlayData);

        void onStart(VideoEditor videoEditor);

        void onStop(VideoEditor videoEditor);

        void onError(VideoEditor videoEditor, int i);
    }

    String getPath();

    void release();

    void save() throws IOException;

    void export(String str, int i, int i2, ExportProgressListener exportProgressListener) throws IOException;

    void export(String str, int i, int i2, int i3, int i4, ExportProgressListener exportProgressListener) throws IOException;

    void cancelExport(String str);

    void addMediaItem(MediaItem mediaItem);

    void insertMediaItem(MediaItem mediaItem, String str);

    void moveMediaItem(String str, String str2);

    MediaItem removeMediaItem(String str);

    void removeAllMediaItems();

    List<MediaItem> getAllMediaItems();

    MediaItem getMediaItem(String str);

    void addTransition(Transition transition);

    Transition removeTransition(String str);

    List<Transition> getAllTransitions();

    Transition getTransition(String str);

    void addAudioTrack(AudioTrack audioTrack);

    void insertAudioTrack(AudioTrack audioTrack, String str);

    void moveAudioTrack(String str, String str2);

    AudioTrack removeAudioTrack(String str);

    List<AudioTrack> getAllAudioTracks();

    AudioTrack getAudioTrack(String str);

    void setAspectRatio(int i);

    int getAspectRatio();

    long getDuration();

    long renderPreviewFrame(SurfaceHolder surfaceHolder, long j, OverlayData overlayData);

    void generatePreview(MediaProcessingProgressListener mediaProcessingProgressListener);

    void startPreview(SurfaceHolder surfaceHolder, long j, long j2, boolean z, int i, PreviewProgressListener previewProgressListener);

    long stopPreview();

    void clearSurface(SurfaceHolder surfaceHolder);

    /* loaded from: VideoEditor$OverlayData.class */
    public static final class OverlayData {
        private Bitmap mOverlayBitmap = null;
        private int mRenderingMode = 2;
        private boolean mClear = false;
        private static final Paint sResizePaint = new Paint(2);

        public void release() {
            if (this.mOverlayBitmap != null) {
                this.mOverlayBitmap.recycle();
                this.mOverlayBitmap = null;
            }
        }

        public boolean needsRendering() {
            return this.mClear || this.mOverlayBitmap != null;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void set(Bitmap overlayBitmap, int renderingMode) {
            this.mOverlayBitmap = overlayBitmap;
            this.mRenderingMode = renderingMode;
            this.mClear = false;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void setClear() {
            this.mClear = true;
        }

        public void renderOverlay(Bitmap destBitmap) {
            int left;
            int top;
            int right;
            int bottom;
            Rect srcRect;
            Rect destRect;
            int left2;
            int top2;
            int right2;
            int bottom2;
            if (this.mClear) {
                destBitmap.eraseColor(0);
            } else if (this.mOverlayBitmap != null) {
                Canvas overlayCanvas = new Canvas(destBitmap);
                switch (this.mRenderingMode) {
                    case 0:
                        destRect = new Rect(0, 0, overlayCanvas.getWidth(), overlayCanvas.getHeight());
                        srcRect = new Rect(0, 0, this.mOverlayBitmap.getWidth(), this.mOverlayBitmap.getHeight());
                        break;
                    case 1:
                        float aROverlayImage = this.mOverlayBitmap.getWidth() / this.mOverlayBitmap.getHeight();
                        float aRCanvas = overlayCanvas.getWidth() / overlayCanvas.getHeight();
                        if (aROverlayImage < aRCanvas) {
                            int newHeight = (this.mOverlayBitmap.getWidth() * overlayCanvas.getHeight()) / overlayCanvas.getWidth();
                            left = 0;
                            top = (this.mOverlayBitmap.getHeight() - newHeight) / 2;
                            right = this.mOverlayBitmap.getWidth();
                            bottom = top + newHeight;
                        } else {
                            int newWidth = (this.mOverlayBitmap.getHeight() * overlayCanvas.getWidth()) / overlayCanvas.getHeight();
                            left = (this.mOverlayBitmap.getWidth() - newWidth) / 2;
                            top = 0;
                            right = left + newWidth;
                            bottom = this.mOverlayBitmap.getHeight();
                        }
                        srcRect = new Rect(left, top, right, bottom);
                        destRect = new Rect(0, 0, overlayCanvas.getWidth(), overlayCanvas.getHeight());
                        break;
                    case 2:
                        float aROverlayImage2 = this.mOverlayBitmap.getWidth() / this.mOverlayBitmap.getHeight();
                        float aRCanvas2 = overlayCanvas.getWidth() / overlayCanvas.getHeight();
                        if (aROverlayImage2 > aRCanvas2) {
                            int newHeight2 = (overlayCanvas.getWidth() * this.mOverlayBitmap.getHeight()) / this.mOverlayBitmap.getWidth();
                            left2 = 0;
                            top2 = (overlayCanvas.getHeight() - newHeight2) / 2;
                            right2 = overlayCanvas.getWidth();
                            bottom2 = top2 + newHeight2;
                        } else {
                            int newWidth2 = (overlayCanvas.getHeight() * this.mOverlayBitmap.getWidth()) / this.mOverlayBitmap.getHeight();
                            left2 = (overlayCanvas.getWidth() - newWidth2) / 2;
                            top2 = 0;
                            right2 = left2 + newWidth2;
                            bottom2 = overlayCanvas.getHeight();
                        }
                        destRect = new Rect(left2, top2, right2, bottom2);
                        srcRect = new Rect(0, 0, this.mOverlayBitmap.getWidth(), this.mOverlayBitmap.getHeight());
                        break;
                    default:
                        throw new IllegalStateException("Rendering mode: " + this.mRenderingMode);
                }
                destBitmap.eraseColor(0);
                overlayCanvas.drawBitmap(this.mOverlayBitmap, srcRect, destRect, sResizePaint);
                this.mOverlayBitmap.recycle();
            }
        }
    }
}