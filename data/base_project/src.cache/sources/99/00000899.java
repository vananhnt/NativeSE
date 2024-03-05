package android.media.videoeditor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Pair;
import gov.nist.core.Separators;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/* loaded from: OverlayFrame.class */
public class OverlayFrame extends Overlay {
    private Bitmap mBitmap;
    private String mFilename;
    private String mBitmapFileName;
    private int mOFWidth;
    private int mOFHeight;
    private int mResizedRGBWidth;
    private int mResizedRGBHeight;
    private static final Paint sResizePaint = new Paint(2);

    private OverlayFrame() {
        this((MediaItem) null, (String) null, (String) null, 0L, 0L);
    }

    public OverlayFrame(MediaItem mediaItem, String overlayId, Bitmap bitmap, long startTimeMs, long durationMs) {
        super(mediaItem, overlayId, startTimeMs, durationMs);
        this.mBitmap = bitmap;
        this.mFilename = null;
        this.mBitmapFileName = null;
        this.mResizedRGBWidth = 0;
        this.mResizedRGBHeight = 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public OverlayFrame(MediaItem mediaItem, String overlayId, String filename, long startTimeMs, long durationMs) {
        super(mediaItem, overlayId, startTimeMs, durationMs);
        this.mBitmapFileName = filename;
        this.mBitmap = BitmapFactory.decodeFile(this.mBitmapFileName);
        this.mFilename = null;
        this.mResizedRGBWidth = 0;
        this.mResizedRGBHeight = 0;
    }

    public Bitmap getBitmap() {
        return this.mBitmap;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getBitmapImageFileName() {
        return this.mBitmapFileName;
    }

    public void setBitmap(Bitmap bitmap) {
        getMediaItem().getNativeContext().setGeneratePreview(true);
        invalidate();
        this.mBitmap = bitmap;
        if (this.mFilename != null) {
            new File(this.mFilename).delete();
            this.mFilename = null;
        }
        getMediaItem().invalidateTransitions(this.mStartTimeMs, this.mDurationMs);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getFilename() {
        return this.mFilename;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setFilename(String filename) {
        this.mFilename = filename;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String save(String path) throws FileNotFoundException, IOException {
        if (this.mFilename != null) {
            return this.mFilename;
        }
        this.mBitmapFileName = path + Separators.SLASH + "Overlay" + getId() + ".png";
        if (!new File(this.mBitmapFileName).exists()) {
            FileOutputStream out = new FileOutputStream(this.mBitmapFileName);
            this.mBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        }
        this.mOFWidth = this.mBitmap.getWidth();
        this.mOFHeight = this.mBitmap.getHeight();
        this.mFilename = path + Separators.SLASH + "Overlay" + getId() + ".rgb";
        MediaArtistNativeHelper nativeHelper = super.getMediaItem().getNativeContext();
        Pair<Integer, Integer>[] resolutions = MediaProperties.getSupportedResolutions(nativeHelper.nativeHelperGetAspectRatio());
        Pair<Integer, Integer> maxResolution = resolutions[resolutions.length - 1];
        generateOverlayWithRenderingMode(super.getMediaItem(), this, maxResolution.second.intValue(), maxResolution.first.intValue());
        return this.mFilename;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getOverlayFrameHeight() {
        return this.mOFHeight;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getOverlayFrameWidth() {
        return this.mOFWidth;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setOverlayFrameHeight(int height) {
        this.mOFHeight = height;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setOverlayFrameWidth(int width) {
        this.mOFWidth = width;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setResizedRGBSize(int width, int height) {
        this.mResizedRGBWidth = width;
        this.mResizedRGBHeight = height;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getResizedRGBSizeHeight() {
        return this.mResizedRGBHeight;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getResizedRGBSizeWidth() {
        return this.mResizedRGBWidth;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void invalidate() {
        if (this.mBitmap != null) {
            this.mBitmap.recycle();
            this.mBitmap = null;
        }
        if (this.mFilename != null) {
            new File(this.mFilename).delete();
            this.mFilename = null;
        }
        if (this.mBitmapFileName != null) {
            new File(this.mBitmapFileName).delete();
            this.mBitmapFileName = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void invalidateGeneratedFiles() {
        if (this.mFilename != null) {
            new File(this.mFilename).delete();
            this.mFilename = null;
        }
        if (this.mBitmapFileName != null) {
            new File(this.mBitmapFileName).delete();
            this.mBitmapFileName = null;
        }
    }

    void generateOverlayWithRenderingMode(MediaItem mediaItemsList, OverlayFrame overlay, int height, int width) throws FileNotFoundException, IOException {
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
        int renderMode = mediaItemsList.getRenderingMode();
        Bitmap overlayBitmap = overlay.getBitmap();
        int resizedRGBFileHeight = overlay.getResizedRGBSizeHeight();
        int resizedRGBFileWidth = overlay.getResizedRGBSizeWidth();
        if (resizedRGBFileWidth == 0) {
            resizedRGBFileWidth = overlayBitmap.getWidth();
        }
        if (resizedRGBFileHeight == 0) {
            resizedRGBFileHeight = overlayBitmap.getHeight();
        }
        if (resizedRGBFileWidth != width || resizedRGBFileHeight != height || !new File(overlay.getFilename()).exists()) {
            Bitmap destBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas overlayCanvas = new Canvas(destBitmap);
            switch (renderMode) {
                case 0:
                    float aROverlayImage = overlayBitmap.getWidth() / overlayBitmap.getHeight();
                    float aRCanvas = overlayCanvas.getWidth() / overlayCanvas.getHeight();
                    if (aROverlayImage > aRCanvas) {
                        int newHeight = (overlayCanvas.getWidth() * overlayBitmap.getHeight()) / overlayBitmap.getWidth();
                        left2 = 0;
                        top2 = (overlayCanvas.getHeight() - newHeight) / 2;
                        right2 = overlayCanvas.getWidth();
                        bottom2 = top2 + newHeight;
                    } else {
                        int newWidth = (overlayCanvas.getHeight() * overlayBitmap.getWidth()) / overlayBitmap.getHeight();
                        left2 = (overlayCanvas.getWidth() - newWidth) / 2;
                        top2 = 0;
                        right2 = left2 + newWidth;
                        bottom2 = overlayCanvas.getHeight();
                    }
                    destRect = new Rect(left2, top2, right2, bottom2);
                    srcRect = new Rect(0, 0, overlayBitmap.getWidth(), overlayBitmap.getHeight());
                    break;
                case 1:
                    destRect = new Rect(0, 0, overlayCanvas.getWidth(), overlayCanvas.getHeight());
                    srcRect = new Rect(0, 0, overlayBitmap.getWidth(), overlayBitmap.getHeight());
                    break;
                case 2:
                    float aROverlayImage2 = overlayBitmap.getWidth() / overlayBitmap.getHeight();
                    float aRCanvas2 = overlayCanvas.getWidth() / overlayCanvas.getHeight();
                    if (aROverlayImage2 < aRCanvas2) {
                        int newHeight2 = (overlayBitmap.getWidth() * overlayCanvas.getHeight()) / overlayCanvas.getWidth();
                        left = 0;
                        top = (overlayBitmap.getHeight() - newHeight2) / 2;
                        right = overlayBitmap.getWidth();
                        bottom = top + newHeight2;
                    } else {
                        int newWidth2 = (overlayBitmap.getHeight() * overlayCanvas.getWidth()) / overlayCanvas.getHeight();
                        left = (overlayBitmap.getWidth() - newWidth2) / 2;
                        top = 0;
                        right = left + newWidth2;
                        bottom = overlayBitmap.getHeight();
                    }
                    srcRect = new Rect(left, top, right, bottom);
                    destRect = new Rect(0, 0, overlayCanvas.getWidth(), overlayCanvas.getHeight());
                    break;
                default:
                    throw new IllegalStateException("Rendering mode: " + renderMode);
            }
            overlayCanvas.drawBitmap(overlayBitmap, srcRect, destRect, sResizePaint);
            overlayCanvas.setBitmap(null);
            String outFileName = overlay.getFilename();
            if (outFileName != null) {
                new File(outFileName).delete();
            }
            FileOutputStream fl = new FileOutputStream(outFileName);
            DataOutputStream dos = new DataOutputStream(fl);
            int[] framingBuffer = new int[width];
            ByteBuffer byteBuffer = ByteBuffer.allocate(framingBuffer.length * 4);
            byte[] array = byteBuffer.array();
            for (int tmp = 0; tmp < height; tmp++) {
                destBitmap.getPixels(framingBuffer, 0, width, 0, tmp, width, 1);
                IntBuffer intBuffer = byteBuffer.asIntBuffer();
                intBuffer.put(framingBuffer, 0, width);
                dos.write(array);
            }
            fl.flush();
            fl.close();
            overlay.setResizedRGBSize(width, height);
        }
    }
}