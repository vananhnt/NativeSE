package android.filterpacks.imageproc;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.format.ImageFormat;
import android.graphics.Bitmap;

/* loaded from: BitmapSource.class */
public class BitmapSource extends Filter {
    @GenerateFieldPort(name = "target")
    String mTargetString;
    @GenerateFieldPort(name = "bitmap")
    private Bitmap mBitmap;
    @GenerateFieldPort(name = "recycleBitmap", hasDefault = true)
    private boolean mRecycleBitmap;
    @GenerateFieldPort(name = "repeatFrame", hasDefault = true)
    boolean mRepeatFrame;
    private int mTarget;
    private Frame mImageFrame;

    public BitmapSource(String name) {
        super(name);
        this.mRecycleBitmap = true;
        this.mRepeatFrame = false;
    }

    @Override // android.filterfw.core.Filter
    public void setupPorts() {
        FrameFormat outputFormat = ImageFormat.create(3, 0);
        addOutputPort("image", outputFormat);
    }

    public void loadImage(FilterContext filterContext) {
        this.mTarget = FrameFormat.readTargetString(this.mTargetString);
        FrameFormat outputFormat = ImageFormat.create(this.mBitmap.getWidth(), this.mBitmap.getHeight(), 3, this.mTarget);
        this.mImageFrame = filterContext.getFrameManager().newFrame(outputFormat);
        this.mImageFrame.setBitmap(this.mBitmap);
        this.mImageFrame.setTimestamp(-1L);
        if (this.mRecycleBitmap) {
            this.mBitmap.recycle();
        }
        this.mBitmap = null;
    }

    @Override // android.filterfw.core.Filter
    public void fieldPortValueUpdated(String name, FilterContext context) {
        if ((name.equals("bitmap") || name.equals("target")) && this.mImageFrame != null) {
            this.mImageFrame.release();
            this.mImageFrame = null;
        }
    }

    @Override // android.filterfw.core.Filter
    public void process(FilterContext context) {
        if (this.mImageFrame == null) {
            loadImage(context);
        }
        pushOutput("image", this.mImageFrame);
        if (!this.mRepeatFrame) {
            closeOutputPort("image");
        }
    }

    @Override // android.filterfw.core.Filter
    public void tearDown(FilterContext env) {
        if (this.mImageFrame != null) {
            this.mImageFrame.release();
            this.mImageFrame = null;
        }
    }
}