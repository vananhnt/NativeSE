package android.filterpacks.imageproc;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.core.MutableFrameFormat;
import android.filterfw.core.Program;
import android.filterfw.core.ShaderProgram;
import android.filterfw.format.ImageFormat;
import gov.nist.core.Separators;

/* loaded from: ToPackedGrayFilter.class */
public class ToPackedGrayFilter extends Filter {
    @GenerateFieldPort(name = "owidth", hasDefault = true)
    private int mOWidth;
    @GenerateFieldPort(name = "oheight", hasDefault = true)
    private int mOHeight;
    @GenerateFieldPort(name = "keepAspectRatio", hasDefault = true)
    private boolean mKeepAspectRatio;
    private Program mProgram;
    private final String mColorToPackedGrayShader = "precision mediump float;\nconst vec4 coeff_y = vec4(0.299, 0.587, 0.114, 0);\nuniform sampler2D tex_sampler_0;\nuniform float pix_stride;\nvarying vec2 v_texcoord;\nvoid main() {\n  for (int i = 0; i < 4; ++i) {\n    vec4 p = texture2D(tex_sampler_0,\n                       v_texcoord + vec2(pix_stride * float(i), 0.0));\n    gl_FragColor[i] = dot(p, coeff_y);\n  }\n}\n";

    public ToPackedGrayFilter(String name) {
        super(name);
        this.mOWidth = 0;
        this.mOHeight = 0;
        this.mKeepAspectRatio = false;
        this.mColorToPackedGrayShader = "precision mediump float;\nconst vec4 coeff_y = vec4(0.299, 0.587, 0.114, 0);\nuniform sampler2D tex_sampler_0;\nuniform float pix_stride;\nvarying vec2 v_texcoord;\nvoid main() {\n  for (int i = 0; i < 4; ++i) {\n    vec4 p = texture2D(tex_sampler_0,\n                       v_texcoord + vec2(pix_stride * float(i), 0.0));\n    gl_FragColor[i] = dot(p, coeff_y);\n  }\n}\n";
    }

    @Override // android.filterfw.core.Filter
    public void setupPorts() {
        addMaskedInputPort("image", ImageFormat.create(3, 3));
        addOutputBasedOnInput("image", "image");
    }

    @Override // android.filterfw.core.Filter
    public FrameFormat getOutputFormat(String portName, FrameFormat inputFormat) {
        return convertInputFormat(inputFormat);
    }

    private void checkOutputDimensions(int outputWidth, int outputHeight) {
        if (outputWidth <= 0 || outputHeight <= 0) {
            throw new RuntimeException("Invalid output dimensions: " + outputWidth + Separators.SP + outputHeight);
        }
    }

    private FrameFormat convertInputFormat(FrameFormat inputFormat) {
        int ow = this.mOWidth;
        int oh = this.mOHeight;
        int w = inputFormat.getWidth();
        int h = inputFormat.getHeight();
        if (this.mOWidth == 0) {
            ow = w;
        }
        if (this.mOHeight == 0) {
            oh = h;
        }
        if (this.mKeepAspectRatio) {
            if (w > h) {
                ow = Math.max(ow, oh);
                oh = (ow * h) / w;
            } else {
                oh = Math.max(ow, oh);
                ow = (oh * w) / h;
            }
        }
        return ImageFormat.create((ow <= 0 || ow >= 4) ? (ow / 4) * 4 : 4, oh, 1, 2);
    }

    @Override // android.filterfw.core.Filter
    public void prepare(FilterContext context) {
        this.mProgram = new ShaderProgram(context, "precision mediump float;\nconst vec4 coeff_y = vec4(0.299, 0.587, 0.114, 0);\nuniform sampler2D tex_sampler_0;\nuniform float pix_stride;\nvarying vec2 v_texcoord;\nvoid main() {\n  for (int i = 0; i < 4; ++i) {\n    vec4 p = texture2D(tex_sampler_0,\n                       v_texcoord + vec2(pix_stride * float(i), 0.0));\n    gl_FragColor[i] = dot(p, coeff_y);\n  }\n}\n");
    }

    @Override // android.filterfw.core.Filter
    public void process(FilterContext context) {
        Frame input = pullInput("image");
        FrameFormat inputFormat = input.getFormat();
        FrameFormat outputFormat = convertInputFormat(inputFormat);
        int ow = outputFormat.getWidth();
        int oh = outputFormat.getHeight();
        checkOutputDimensions(ow, oh);
        this.mProgram.setHostValue("pix_stride", Float.valueOf(1.0f / ow));
        MutableFrameFormat tempFrameFormat = inputFormat.mutableCopy();
        tempFrameFormat.setDimensions(ow / 4, oh);
        Frame temp = context.getFrameManager().newFrame(tempFrameFormat);
        this.mProgram.process(input, temp);
        Frame output = context.getFrameManager().newFrame(outputFormat);
        output.setDataFromFrame(temp);
        temp.release();
        pushOutput("image", output);
        output.release();
    }
}