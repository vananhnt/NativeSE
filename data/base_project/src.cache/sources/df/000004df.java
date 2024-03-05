package android.filterpacks.imageproc;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.core.Program;
import android.filterfw.core.ShaderProgram;
import android.filterfw.format.ImageFormat;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;

/* loaded from: RedEyeFilter.class */
public class RedEyeFilter extends Filter {
    private static final float RADIUS_RATIO = 0.06f;
    private static final float MIN_RADIUS = 10.0f;
    private static final float DEFAULT_RED_INTENSITY = 1.3f;
    @GenerateFieldPort(name = "centers")
    private float[] mCenters;
    @GenerateFieldPort(name = "tile_size", hasDefault = true)
    private int mTileSize;
    private Frame mRedEyeFrame;
    private Bitmap mRedEyeBitmap;
    private final Canvas mCanvas;
    private final Paint mPaint;
    private float mRadius;
    private int mWidth;
    private int mHeight;
    private Program mProgram;
    private int mTarget;
    private final String mRedEyeShader = "precision mediump float;\nuniform sampler2D tex_sampler_0;\nuniform sampler2D tex_sampler_1;\nuniform float intensity;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec4 color = texture2D(tex_sampler_0, v_texcoord);\n  vec4 mask = texture2D(tex_sampler_1, v_texcoord);\n  if (mask.a > 0.0) {\n    float green_blue = color.g + color.b;\n    float red_intensity = color.r / green_blue;\n    if (red_intensity > intensity) {\n      color.r = 0.5 * green_blue;\n    }\n  }\n  gl_FragColor = color;\n}\n";

    public RedEyeFilter(String name) {
        super(name);
        this.mTileSize = DisplayMetrics.DENSITY_XXXHIGH;
        this.mCanvas = new Canvas();
        this.mPaint = new Paint();
        this.mWidth = 0;
        this.mHeight = 0;
        this.mTarget = 0;
        this.mRedEyeShader = "precision mediump float;\nuniform sampler2D tex_sampler_0;\nuniform sampler2D tex_sampler_1;\nuniform float intensity;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec4 color = texture2D(tex_sampler_0, v_texcoord);\n  vec4 mask = texture2D(tex_sampler_1, v_texcoord);\n  if (mask.a > 0.0) {\n    float green_blue = color.g + color.b;\n    float red_intensity = color.r / green_blue;\n    if (red_intensity > intensity) {\n      color.r = 0.5 * green_blue;\n    }\n  }\n  gl_FragColor = color;\n}\n";
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
                ShaderProgram shaderProgram = new ShaderProgram(context, "precision mediump float;\nuniform sampler2D tex_sampler_0;\nuniform sampler2D tex_sampler_1;\nuniform float intensity;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec4 color = texture2D(tex_sampler_0, v_texcoord);\n  vec4 mask = texture2D(tex_sampler_1, v_texcoord);\n  if (mask.a > 0.0) {\n    float green_blue = color.g + color.b;\n    float red_intensity = color.r / green_blue;\n    if (red_intensity > intensity) {\n      color.r = 0.5 * green_blue;\n    }\n  }\n  gl_FragColor = color;\n}\n");
                shaderProgram.setMaximumTileSize(this.mTileSize);
                this.mProgram = shaderProgram;
                this.mProgram.setHostValue("intensity", Float.valueOf((float) DEFAULT_RED_INTENSITY));
                this.mTarget = target;
                return;
            default:
                throw new RuntimeException("Filter RedEye does not support frames of target " + target + "!");
        }
    }

    @Override // android.filterfw.core.Filter
    public void process(FilterContext context) {
        Frame input = pullInput("image");
        FrameFormat inputFormat = input.getFormat();
        Frame output = context.getFrameManager().newFrame(inputFormat);
        if (this.mProgram == null || inputFormat.getTarget() != this.mTarget) {
            initProgram(context, inputFormat.getTarget());
        }
        if (inputFormat.getWidth() != this.mWidth || inputFormat.getHeight() != this.mHeight) {
            this.mWidth = inputFormat.getWidth();
            this.mHeight = inputFormat.getHeight();
        }
        createRedEyeFrame(context);
        Frame[] inputs = {input, this.mRedEyeFrame};
        this.mProgram.process(inputs, output);
        pushOutput("image", output);
        output.release();
        this.mRedEyeFrame.release();
        this.mRedEyeFrame = null;
    }

    @Override // android.filterfw.core.Filter
    public void fieldPortValueUpdated(String name, FilterContext context) {
        if (this.mProgram != null) {
            updateProgramParams();
        }
    }

    private void createRedEyeFrame(FilterContext context) {
        int bitmapWidth = this.mWidth / 2;
        int bitmapHeight = this.mHeight / 2;
        Bitmap redEyeBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        this.mCanvas.setBitmap(redEyeBitmap);
        this.mPaint.setColor(-1);
        this.mRadius = Math.max((float) MIN_RADIUS, RADIUS_RATIO * Math.min(bitmapWidth, bitmapHeight));
        for (int i = 0; i < this.mCenters.length; i += 2) {
            this.mCanvas.drawCircle(this.mCenters[i] * bitmapWidth, this.mCenters[i + 1] * bitmapHeight, this.mRadius, this.mPaint);
        }
        FrameFormat format = ImageFormat.create(bitmapWidth, bitmapHeight, 3, 3);
        this.mRedEyeFrame = context.getFrameManager().newFrame(format);
        this.mRedEyeFrame.setBitmap(redEyeBitmap);
        redEyeBitmap.recycle();
    }

    private void updateProgramParams() {
        if (this.mCenters.length % 2 == 1) {
            throw new RuntimeException("The size of center array must be even.");
        }
    }
}