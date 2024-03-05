package android.filterpacks.base;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.format.ImageFormat;

/* loaded from: GLTextureSource.class */
public class GLTextureSource extends Filter {
    @GenerateFieldPort(name = "texId")
    private int mTexId;
    @GenerateFieldPort(name = "width")
    private int mWidth;
    @GenerateFieldPort(name = "height")
    private int mHeight;
    @GenerateFieldPort(name = "repeatFrame", hasDefault = true)
    private boolean mRepeatFrame;
    @GenerateFieldPort(name = "timestamp", hasDefault = true)
    private long mTimestamp;
    private Frame mFrame;

    public GLTextureSource(String name) {
        super(name);
        this.mRepeatFrame = false;
        this.mTimestamp = -1L;
    }

    @Override // android.filterfw.core.Filter
    public void setupPorts() {
        addOutputPort("frame", ImageFormat.create(3, 3));
    }

    @Override // android.filterfw.core.Filter
    public void fieldPortValueUpdated(String name, FilterContext context) {
        if (this.mFrame != null) {
            this.mFrame.release();
            this.mFrame = null;
        }
    }

    @Override // android.filterfw.core.Filter
    public void process(FilterContext context) {
        if (this.mFrame == null) {
            FrameFormat outputFormat = ImageFormat.create(this.mWidth, this.mHeight, 3, 3);
            this.mFrame = context.getFrameManager().newBoundFrame(outputFormat, 100, this.mTexId);
            this.mFrame.setTimestamp(this.mTimestamp);
        }
        pushOutput("frame", this.mFrame);
        if (!this.mRepeatFrame) {
            closeOutputPort("frame");
        }
    }

    @Override // android.filterfw.core.Filter
    public void tearDown(FilterContext context) {
        if (this.mFrame != null) {
            this.mFrame.release();
        }
    }
}