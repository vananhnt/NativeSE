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

/* loaded from: NegativeFilter.class */
public class NegativeFilter extends Filter {
    @GenerateFieldPort(name = "tile_size", hasDefault = true)
    private int mTileSize;
    private Program mProgram;
    private int mTarget;
    private final String mNegativeShader = "precision mediump float;\nuniform sampler2D tex_sampler_0;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec4 color = texture2D(tex_sampler_0, v_texcoord);\n  gl_FragColor = vec4(1.0 - color.rgb, color.a);\n}\n";

    public NegativeFilter(String name) {
        super(name);
        this.mTileSize = DisplayMetrics.DENSITY_XXXHIGH;
        this.mTarget = 0;
        this.mNegativeShader = "precision mediump float;\nuniform sampler2D tex_sampler_0;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec4 color = texture2D(tex_sampler_0, v_texcoord);\n  gl_FragColor = vec4(1.0 - color.rgb, color.a);\n}\n";
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
                ShaderProgram shaderProgram = new ShaderProgram(context, "precision mediump float;\nuniform sampler2D tex_sampler_0;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec4 color = texture2D(tex_sampler_0, v_texcoord);\n  gl_FragColor = vec4(1.0 - color.rgb, color.a);\n}\n");
                shaderProgram.setMaximumTileSize(this.mTileSize);
                this.mProgram = shaderProgram;
                this.mTarget = target;
                return;
            default:
                throw new RuntimeException("Filter Sharpen does not support frames of target " + target + "!");
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
        this.mProgram.process(input, output);
        pushOutput("image", output);
        output.release();
    }
}