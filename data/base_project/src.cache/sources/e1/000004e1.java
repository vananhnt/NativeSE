package android.filterpacks.imageproc;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.core.Program;
import android.filterfw.core.ShaderProgram;
import android.filterfw.format.ImageFormat;
import android.filterfw.geometry.Point;
import android.filterfw.geometry.Quad;
import android.media.videoeditor.MediaProperties;
import android.util.DisplayMetrics;

/* loaded from: RotateFilter.class */
public class RotateFilter extends Filter {
    @GenerateFieldPort(name = "angle")
    private int mAngle;
    @GenerateFieldPort(name = "tile_size", hasDefault = true)
    private int mTileSize;
    private Program mProgram;
    private int mWidth;
    private int mHeight;
    private int mTarget;
    private int mOutputWidth;
    private int mOutputHeight;

    public RotateFilter(String name) {
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
                shaderProgram.setClearsOutput(true);
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
        if (inputFormat.getWidth() != this.mWidth || inputFormat.getHeight() != this.mHeight) {
            this.mWidth = inputFormat.getWidth();
            this.mHeight = inputFormat.getHeight();
            this.mOutputWidth = this.mWidth;
            this.mOutputHeight = this.mHeight;
            updateParameters();
        }
        FrameFormat outputFormat = ImageFormat.create(this.mOutputWidth, this.mOutputHeight, 3, 3);
        Frame output = context.getFrameManager().newFrame(outputFormat);
        this.mProgram.process(input, output);
        pushOutput("image", output);
        output.release();
    }

    private void updateParameters() {
        float cosTheta;
        float sinTheta;
        if (this.mAngle % 90 == 0) {
            if (this.mAngle % 180 == 0) {
                sinTheta = 0.0f;
                cosTheta = this.mAngle % MediaProperties.HEIGHT_360 == 0 ? 1.0f : -1.0f;
            } else {
                cosTheta = 0.0f;
                sinTheta = (this.mAngle + 90) % MediaProperties.HEIGHT_360 == 0 ? -1.0f : 1.0f;
                this.mOutputWidth = this.mHeight;
                this.mOutputHeight = this.mWidth;
            }
            Point x0 = new Point(0.5f * ((-cosTheta) + sinTheta + 1.0f), 0.5f * (((-sinTheta) - cosTheta) + 1.0f));
            Point x1 = new Point(0.5f * (cosTheta + sinTheta + 1.0f), 0.5f * ((sinTheta - cosTheta) + 1.0f));
            Point x2 = new Point(0.5f * (((-cosTheta) - sinTheta) + 1.0f), 0.5f * ((-sinTheta) + cosTheta + 1.0f));
            Point x3 = new Point(0.5f * ((cosTheta - sinTheta) + 1.0f), 0.5f * (sinTheta + cosTheta + 1.0f));
            Quad quad = new Quad(x0, x1, x2, x3);
            ((ShaderProgram) this.mProgram).setTargetRegion(quad);
            return;
        }
        throw new RuntimeException("degree has to be multiply of 90.");
    }
}