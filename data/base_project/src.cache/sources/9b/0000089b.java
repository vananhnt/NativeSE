package android.media.videoeditor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import gov.nist.core.Separators;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/* loaded from: TransitionAlpha.class */
public class TransitionAlpha extends Transition {
    private final String mMaskFilename;
    private final int mBlendingPercent;
    private final boolean mIsInvert;
    private int mWidth;
    private int mHeight;
    private String mRGBMaskFile;

    private TransitionAlpha() {
        this(null, null, null, 0L, 0, null, 0, false);
    }

    public TransitionAlpha(String transitionId, MediaItem afterMediaItem, MediaItem beforeMediaItem, long durationMs, int behavior, String maskFilename, int blendingPercent, boolean invert) {
        super(transitionId, afterMediaItem, beforeMediaItem, durationMs, behavior);
        BitmapFactory.Options dbo = new BitmapFactory.Options();
        dbo.inJustDecodeBounds = true;
        if (!new File(maskFilename).exists()) {
            throw new IllegalArgumentException("File not Found " + maskFilename);
        }
        BitmapFactory.decodeFile(maskFilename, dbo);
        this.mWidth = dbo.outWidth;
        this.mHeight = dbo.outHeight;
        this.mRGBMaskFile = String.format(this.mNativeHelper.getProjectPath() + Separators.SLASH + "mask" + transitionId + ".rgb", new Object[0]);
        FileOutputStream fl = null;
        try {
            fl = new FileOutputStream(this.mRGBMaskFile);
        } catch (IOException e) {
        }
        DataOutputStream dos = new DataOutputStream(fl);
        if (fl != null) {
            Bitmap imageBitmap = BitmapFactory.decodeFile(maskFilename);
            int[] framingBuffer = new int[this.mWidth];
            ByteBuffer byteBuffer = ByteBuffer.allocate(framingBuffer.length * 4);
            byte[] array = byteBuffer.array();
            for (int tmp = 0; tmp < this.mHeight; tmp++) {
                imageBitmap.getPixels(framingBuffer, 0, this.mWidth, 0, tmp, this.mWidth, 1);
                IntBuffer intBuffer = byteBuffer.asIntBuffer();
                intBuffer.put(framingBuffer, 0, this.mWidth);
                try {
                    dos.write(array);
                } catch (IOException e2) {
                }
            }
            imageBitmap.recycle();
            try {
                fl.close();
            } catch (IOException e3) {
            }
        }
        this.mMaskFilename = maskFilename;
        this.mBlendingPercent = blendingPercent;
        this.mIsInvert = invert;
    }

    public int getRGBFileWidth() {
        return this.mWidth;
    }

    public int getRGBFileHeight() {
        return this.mHeight;
    }

    public String getPNGMaskFilename() {
        return this.mRGBMaskFile;
    }

    public int getBlendingPercent() {
        return this.mBlendingPercent;
    }

    public String getMaskFilename() {
        return this.mMaskFilename;
    }

    public boolean isInvert() {
        return this.mIsInvert;
    }

    @Override // android.media.videoeditor.Transition
    public void generate() {
        super.generate();
    }
}