package android.filterpacks.imageproc;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.GLFrame;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.core.MutableFrameFormat;
import android.filterfw.core.Program;
import android.filterfw.core.ShaderProgram;
import android.filterfw.format.ImageFormat;

/* loaded from: ResizeFilter.class */
public class ResizeFilter extends Filter {
    @GenerateFieldPort(name = "owidth")
    private int mOWidth;
    @GenerateFieldPort(name = "oheight")
    private int mOHeight;
    @GenerateFieldPort(name = "keepAspectRatio", hasDefault = true)
    private boolean mKeepAspectRatio;
    @GenerateFieldPort(name = "generateMipMap", hasDefault = true)
    private boolean mGenerateMipMap;
    private Program mProgram;
    private FrameFormat mLastFormat;
    private MutableFrameFormat mOutputFormat;
    private int mInputChannels;

    public ResizeFilter(String name) {
        super(name);
        this.mKeepAspectRatio = false;
        this.mGenerateMipMap = false;
        this.mLastFormat = null;
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

    protected void createProgram(FilterContext context, FrameFormat format) {
        if (this.mLastFormat == null || this.mLastFormat.getTarget() != format.getTarget()) {
            this.mLastFormat = format;
            switch (format.getTarget()) {
                case 2:
                    throw new RuntimeException("Native ResizeFilter not implemented yet!");
                case 3:
                    ShaderProgram prog = ShaderProgram.createIdentity(context);
                    this.mProgram = prog;
                    return;
                default:
                    throw new RuntimeException("ResizeFilter could not create suitable program!");
            }
        }
    }

    @Override // android.filterfw.core.Filter
    public void process(FilterContext env) {
        Frame input = pullInput("image");
        createProgram(env, input.getFormat());
        MutableFrameFormat outputFormat = input.getFormat().mutableCopy();
        if (this.mKeepAspectRatio) {
            FrameFormat inputFormat = input.getFormat();
            this.mOHeight = (this.mOWidth * inputFormat.getHeight()) / inputFormat.getWidth();
        }
        outputFormat.setDimensions(this.mOWidth, this.mOHeight);
        Frame output = env.getFrameManager().newFrame(outputFormat);
        if (this.mGenerateMipMap) {
            GLFrame mipmapped = (GLFrame) env.getFrameManager().newFrame(input.getFormat());
            mipmapped.setTextureParameter(10241, 9985);
            mipmapped.setDataFromFrame(input);
            mipmapped.generateMipMap();
            this.mProgram.process(mipmapped, output);
            mipmapped.release();
        } else {
            this.mProgram.process(input, output);
        }
        pushOutput("image", output);
        output.release();
    }
}