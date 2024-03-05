package android.filterpacks.imageproc;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.core.Program;
import android.filterfw.core.ShaderProgram;
import android.filterfw.format.ImageFormat;
import android.graphics.Color;
import android.util.DisplayMetrics;

/* loaded from: DuotoneFilter.class */
public class DuotoneFilter extends Filter {
    @GenerateFieldPort(name = "first_color", hasDefault = true)
    private int mFirstColor;
    @GenerateFieldPort(name = "second_color", hasDefault = true)
    private int mSecondColor;
    @GenerateFieldPort(name = "tile_size", hasDefault = true)
    private int mTileSize;
    private Program mProgram;
    private int mTarget;
    private final String mDuotoneShader = "precision mediump float;\nuniform sampler2D tex_sampler_0;\nuniform vec3 first;\nuniform vec3 second;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec4 color = texture2D(tex_sampler_0, v_texcoord);\n  float energy = (color.r + color.g + color.b) * 0.3333;\n  vec3 new_color = (1.0 - energy) * first + energy * second;\n  gl_FragColor = vec4(new_color.rgb, color.a);\n}\n";

    public DuotoneFilter(String name) {
        super(name);
        this.mFirstColor = -65536;
        this.mSecondColor = -256;
        this.mTileSize = DisplayMetrics.DENSITY_XXXHIGH;
        this.mTarget = 0;
        this.mDuotoneShader = "precision mediump float;\nuniform sampler2D tex_sampler_0;\nuniform vec3 first;\nuniform vec3 second;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec4 color = texture2D(tex_sampler_0, v_texcoord);\n  float energy = (color.r + color.g + color.b) * 0.3333;\n  vec3 new_color = (1.0 - energy) * first + energy * second;\n  gl_FragColor = vec4(new_color.rgb, color.a);\n}\n";
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
                ShaderProgram shaderProgram = new ShaderProgram(context, "precision mediump float;\nuniform sampler2D tex_sampler_0;\nuniform vec3 first;\nuniform vec3 second;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec4 color = texture2D(tex_sampler_0, v_texcoord);\n  float energy = (color.r + color.g + color.b) * 0.3333;\n  vec3 new_color = (1.0 - energy) * first + energy * second;\n  gl_FragColor = vec4(new_color.rgb, color.a);\n}\n");
                shaderProgram.setMaximumTileSize(this.mTileSize);
                this.mProgram = shaderProgram;
                this.mTarget = target;
                return;
            default:
                throw new RuntimeException("Filter Duotone does not support frames of target " + target + "!");
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
        updateParameters();
        this.mProgram.process(input, output);
        pushOutput("image", output);
        output.release();
    }

    private void updateParameters() {
        float[] first = {Color.red(this.mFirstColor) / 255.0f, Color.green(this.mFirstColor) / 255.0f, Color.blue(this.mFirstColor) / 255.0f};
        float[] second = {Color.red(this.mSecondColor) / 255.0f, Color.green(this.mSecondColor) / 255.0f, Color.blue(this.mSecondColor) / 255.0f};
        this.mProgram.setHostValue("first", first);
        this.mProgram.setHostValue("second", second);
    }
}