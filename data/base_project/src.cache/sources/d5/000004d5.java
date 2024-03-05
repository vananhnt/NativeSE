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

/* loaded from: FlipFilter.class */
public class FlipFilter extends Filter {
    @GenerateFieldPort(name = "vertical", hasDefault = true)
    private boolean mVertical;
    @GenerateFieldPort(name = "horizontal", hasDefault = true)
    private boolean mHorizontal;
    @GenerateFieldPort(name = "tile_size", hasDefault = true)
    private int mTileSize;
    private Program mProgram;
    private int mTarget;

    public FlipFilter(String name) {
        super(name);
        this.mVertical = false;
        this.mHorizontal = false;
        this.mTileSize = DisplayMetrics.DENSITY_XXXHIGH;
        this.mTarget = 0;
    }

    @Override // android.filterfw.core.Filter
    public void setupPorts() {
        addMaskedInputPort("image", ImageFormat.create(3));
        addOutputBasedOnInput("image", "image");
    }

    @Override // android.filterfw.core.Filter
    public FrameFormat getOutputFormat(String portName, FrameFormat inputFormat) {
        return inputFormat;
    }

    public void initProgram(FilterContext context, int target) {
        switch (target) {
            case 3:
                ShaderProgram shaderProgram = ShaderProgram.createIdentity(context);
                shaderProgram.setMaximumTileSize(this.mTileSize);
                this.mProgram = shaderProgram;
                this.mTarget = target;
                updateParameters();
                return;
            default:
                throw new RuntimeException("Filter Sharpen does not support frames of target " + target + "!");
        }
    }

    @Override // android.filterfw.core.Filter
    public void fieldPortValueUpdated(String name, FilterContext context) {
        if (this.mProgram != null) {
            updateParameters();
        }
    }

    @Override // android.filterfw.core.Filter
    public void process(FilterContext context) {
        Frame input = pullInput("image");
        FrameFormat inputFormat = input.getFormat();
        if (this.mProgram == null || inputFormat.getTarget() != this.mTarget) {
            initProgram(context, inputFormat.getTarget());
        }
        Frame output = context.getFrameManager().newFrame(inputFormat);
        this.mProgram.process(input, output);
        pushOutput("image", output);
        output.release();
    }

    private void updateParameters() {
        float x_origin = this.mHorizontal ? 1.0f : 0.0f;
        float y_origin = this.mVertical ? 1.0f : 0.0f;
        float width = this.mHorizontal ? -1.0f : 1.0f;
        float height = this.mVertical ? -1.0f : 1.0f;
        ((ShaderProgram) this.mProgram).setSourceRect(x_origin, y_origin, width, height);
    }
}