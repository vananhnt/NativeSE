package android.filterpacks.imageproc;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.core.Program;
import android.filterfw.core.ShaderProgram;
import android.filterfw.format.ImageFormat;
import android.util.DisplayMetrics;

/* loaded from: CropRectFilter.class */
public class CropRectFilter extends Filter {
    @GenerateFieldPort(name = "xorigin")
    private int mXorigin;
    @GenerateFieldPort(name = "yorigin")
    private int mYorigin;
    @GenerateFieldPort(name = "width")
    private int mOutputWidth;
    @GenerateFieldPort(name = "height")
    private int mOutputHeight;
    @GenerateFieldPort(name = "tile_size", hasDefault = true)
    private int mTileSize;
    private Program mProgram;
    private int mWidth;
    private int mHeight;
    private int mTarget;

    public CropRectFilter(String name) {
        super(name);
        this.mTileSize = DisplayMetrics.DENSITY_XXXHIGH;
        this.mWidth = 0;
        this.mHeight = 0;
        this.mTarget = 0;
    }

    @Override // android.filterfw.core.Filter
    public void setupPorts() {
        addMaskedInputPort("image", ImageFormat.create(3));
        addOutputBasedOnInput("image", "image");
    }

    public void initProgram(FilterContext context, int target) {
        switch (target) {
            case 3:
                ShaderProgram shaderProgram = ShaderProgram.createIdentity(context);
                shaderProgram.setMaximumTileSize(this.mTileSize);
                this.mProgram = shaderProgram;
                this.mTarget = target;
                return;
            default:
                throw new RuntimeException("Filter Sharpen does not support frames of target " + target + "!");
        }
    }

    @Override // android.filterfw.core.Filter
    public void fieldPortValueUpdated(String name, FilterContext context) {
        if (this.mProgram != null) {
            updateSourceRect(this.mWidth, this.mHeight);
        }
    }

    @Override // android.filterfw.core.Filter
    public void process(FilterContext context) {
        Frame input = pullInput("image");
        FrameFormat inputFormat = input.getFormat();
        FrameFormat outputFormat = ImageFormat.create(this.mOutputWidth, this.mOutputHeight, 3, 3);
        Frame output = context.getFrameManager().newFrame(outputFormat);
        if (this.mProgram == null || inputFormat.getTarget() != this.mTarget) {
            initProgram(context, inputFormat.getTarget());
        }
        if (inputFormat.getWidth() != this.mWidth || inputFormat.getHeight() != this.mHeight) {
            updateSourceRect(inputFormat.getWidth(), inputFormat.getHeight());
        }
        this.mProgram.process(input, output);
        pushOutput("image", output);
        output.release();
    }

    void updateSourceRect(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
        ((ShaderProgram) this.mProgram).setSourceRect(this.mXorigin / this.mWidth, this.mYorigin / this.mHeight, this.mOutputWidth / this.mWidth, this.mOutputHeight / this.mHeight);
    }
}