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
import android.util.DisplayMetrics;

/* loaded from: StraightenFilter.class */
public class StraightenFilter extends Filter {
    @GenerateFieldPort(name = "angle", hasDefault = true)
    private float mAngle;
    @GenerateFieldPort(name = "maxAngle", hasDefault = true)
    private float mMaxAngle;
    @GenerateFieldPort(name = "tile_size", hasDefault = true)
    private int mTileSize;
    private Program mProgram;
    private int mWidth;
    private int mHeight;
    private int mTarget;
    private static final float DEGREE_TO_RADIAN = 0.017453292f;

    public StraightenFilter(String name) {
        super(name);
        this.mAngle = 0.0f;
        this.mMaxAngle = 45.0f;
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
            updateParameters();
        }
        Frame output = context.getFrameManager().newFrame(inputFormat);
        this.mProgram.process(input, output);
        pushOutput("image", output);
        output.release();
    }

    private void updateParameters() {
        float cosTheta = (float) Math.cos(this.mAngle * DEGREE_TO_RADIAN);
        float sinTheta = (float) Math.sin(this.mAngle * DEGREE_TO_RADIAN);
        if (this.mMaxAngle <= 0.0f) {
            throw new RuntimeException("Max angle is out of range (0-180).");
        }
        this.mMaxAngle = this.mMaxAngle > 90.0f ? 90.0f : this.mMaxAngle;
        Point p0 = new Point(((-cosTheta) * this.mWidth) + (sinTheta * this.mHeight), ((-sinTheta) * this.mWidth) - (cosTheta * this.mHeight));
        Point p1 = new Point((cosTheta * this.mWidth) + (sinTheta * this.mHeight), (sinTheta * this.mWidth) - (cosTheta * this.mHeight));
        Point p2 = new Point(((-cosTheta) * this.mWidth) - (sinTheta * this.mHeight), ((-sinTheta) * this.mWidth) + (cosTheta * this.mHeight));
        Point p3 = new Point((cosTheta * this.mWidth) - (sinTheta * this.mHeight), (sinTheta * this.mWidth) + (cosTheta * this.mHeight));
        float maxWidth = Math.max(Math.abs(p0.x), Math.abs(p1.x));
        float maxHeight = Math.max(Math.abs(p0.y), Math.abs(p1.y));
        float scale = 0.5f * Math.min(this.mWidth / maxWidth, this.mHeight / maxHeight);
        p0.set(((scale * p0.x) / this.mWidth) + 0.5f, ((scale * p0.y) / this.mHeight) + 0.5f);
        p1.set(((scale * p1.x) / this.mWidth) + 0.5f, ((scale * p1.y) / this.mHeight) + 0.5f);
        p2.set(((scale * p2.x) / this.mWidth) + 0.5f, ((scale * p2.y) / this.mHeight) + 0.5f);
        p3.set(((scale * p3.x) / this.mWidth) + 0.5f, ((scale * p3.y) / this.mHeight) + 0.5f);
        Quad quad = new Quad(p0, p1, p2, p3);
        ((ShaderProgram) this.mProgram).setSourceRegion(quad);
    }
}