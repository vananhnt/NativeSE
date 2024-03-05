package android.filterpacks.imageproc;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.core.Program;
import android.filterfw.core.ShaderProgram;
import android.filterfw.format.ImageFormat;
import android.os.BatteryManager;
import android.util.DisplayMetrics;

/* loaded from: SaturateFilter.class */
public class SaturateFilter extends Filter {
    @GenerateFieldPort(name = BatteryManager.EXTRA_SCALE, hasDefault = true)
    private float mScale;
    @GenerateFieldPort(name = "tile_size", hasDefault = true)
    private int mTileSize;
    private Program mBenProgram;
    private Program mHerfProgram;
    private int mTarget;
    private final String mBenSaturateShader = "precision mediump float;\nuniform sampler2D tex_sampler_0;\nuniform float scale;\nuniform float shift;\nuniform vec3 weights;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec4 color = texture2D(tex_sampler_0, v_texcoord);\n  float kv = dot(color.rgb, weights) + shift;\n  vec3 new_color = scale * color.rgb + (1.0 - scale) * kv;\n  gl_FragColor = vec4(new_color, color.a);\n}\n";
    private final String mHerfSaturateShader = "precision mediump float;\nuniform sampler2D tex_sampler_0;\nuniform vec3 weights;\nuniform vec3 exponents;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec4 color = texture2D(tex_sampler_0, v_texcoord);\n  float de = dot(color.rgb, weights);\n  float inv_de = 1.0 / de;\n  vec3 new_color = de * pow(color.rgb * inv_de, exponents);\n  float max_color = max(max(max(new_color.r, new_color.g), new_color.b), 1.0);\n  gl_FragColor = vec4(new_color / max_color, color.a);\n}\n";

    public SaturateFilter(String name) {
        super(name);
        this.mScale = 0.0f;
        this.mTileSize = DisplayMetrics.DENSITY_XXXHIGH;
        this.mTarget = 0;
        this.mBenSaturateShader = "precision mediump float;\nuniform sampler2D tex_sampler_0;\nuniform float scale;\nuniform float shift;\nuniform vec3 weights;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec4 color = texture2D(tex_sampler_0, v_texcoord);\n  float kv = dot(color.rgb, weights) + shift;\n  vec3 new_color = scale * color.rgb + (1.0 - scale) * kv;\n  gl_FragColor = vec4(new_color, color.a);\n}\n";
        this.mHerfSaturateShader = "precision mediump float;\nuniform sampler2D tex_sampler_0;\nuniform vec3 weights;\nuniform vec3 exponents;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec4 color = texture2D(tex_sampler_0, v_texcoord);\n  float de = dot(color.rgb, weights);\n  float inv_de = 1.0 / de;\n  vec3 new_color = de * pow(color.rgb * inv_de, exponents);\n  float max_color = max(max(max(new_color.r, new_color.g), new_color.b), 1.0);\n  gl_FragColor = vec4(new_color / max_color, color.a);\n}\n";
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
                ShaderProgram shaderProgram = new ShaderProgram(context, "precision mediump float;\nuniform sampler2D tex_sampler_0;\nuniform float scale;\nuniform float shift;\nuniform vec3 weights;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec4 color = texture2D(tex_sampler_0, v_texcoord);\n  float kv = dot(color.rgb, weights) + shift;\n  vec3 new_color = scale * color.rgb + (1.0 - scale) * kv;\n  gl_FragColor = vec4(new_color, color.a);\n}\n");
                shaderProgram.setMaximumTileSize(this.mTileSize);
                this.mBenProgram = shaderProgram;
                ShaderProgram shaderProgram2 = new ShaderProgram(context, "precision mediump float;\nuniform sampler2D tex_sampler_0;\nuniform vec3 weights;\nuniform vec3 exponents;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec4 color = texture2D(tex_sampler_0, v_texcoord);\n  float de = dot(color.rgb, weights);\n  float inv_de = 1.0 / de;\n  vec3 new_color = de * pow(color.rgb * inv_de, exponents);\n  float max_color = max(max(max(new_color.r, new_color.g), new_color.b), 1.0);\n  gl_FragColor = vec4(new_color / max_color, color.a);\n}\n");
                shaderProgram2.setMaximumTileSize(this.mTileSize);
                this.mHerfProgram = shaderProgram2;
                this.mTarget = target;
                return;
            default:
                throw new RuntimeException("Filter Sharpen does not support frames of target " + target + "!");
        }
    }

    @Override // android.filterfw.core.Filter
    public void fieldPortValueUpdated(String name, FilterContext context) {
        if (this.mBenProgram != null && this.mHerfProgram != null) {
            updateParameters();
        }
    }

    @Override // android.filterfw.core.Filter
    public void process(FilterContext context) {
        Frame input = pullInput("image");
        FrameFormat inputFormat = input.getFormat();
        if (this.mBenProgram == null || inputFormat.getTarget() != this.mTarget) {
            initProgram(context, inputFormat.getTarget());
            initParameters();
        }
        Frame output = context.getFrameManager().newFrame(inputFormat);
        if (this.mScale > 0.0f) {
            this.mHerfProgram.process(input, output);
        } else {
            this.mBenProgram.process(input, output);
        }
        pushOutput("image", output);
        output.release();
    }

    private void initParameters() {
        float[] weights = {0.25f, 0.625f, 0.125f};
        this.mBenProgram.setHostValue("weights", weights);
        this.mBenProgram.setHostValue("shift", Float.valueOf(0.003921569f));
        this.mHerfProgram.setHostValue("weights", weights);
        updateParameters();
    }

    private void updateParameters() {
        if (this.mScale > 0.0f) {
            float[] exponents = {(0.9f * this.mScale) + 1.0f, (2.1f * this.mScale) + 1.0f, (2.7f * this.mScale) + 1.0f};
            this.mHerfProgram.setHostValue("exponents", exponents);
            return;
        }
        this.mBenProgram.setHostValue(BatteryManager.EXTRA_SCALE, Float.valueOf(1.0f + this.mScale));
    }
}