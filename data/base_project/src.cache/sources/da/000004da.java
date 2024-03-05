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

/* loaded from: ImageStitcher.class */
public class ImageStitcher extends Filter {
    @GenerateFieldPort(name = "xSlices")
    private int mXSlices;
    @GenerateFieldPort(name = "ySlices")
    private int mYSlices;
    @GenerateFieldPort(name = "padSize")
    private int mPadSize;
    private Program mProgram;
    private Frame mOutputFrame;
    private int mInputWidth;
    private int mInputHeight;
    private int mImageWidth;
    private int mImageHeight;
    private int mSliceWidth;
    private int mSliceHeight;
    private int mSliceIndex;

    public ImageStitcher(String name) {
        super(name);
        this.mSliceIndex = 0;
    }

    @Override // android.filterfw.core.Filter
    public void setupPorts() {
        addMaskedInputPort("image", ImageFormat.create(3, 3));
        addOutputBasedOnInput("image", "image");
    }

    @Override // android.filterfw.core.Filter
    public FrameFormat getOutputFormat(String portName, FrameFormat inputFormat) {
        return inputFormat;
    }

    private FrameFormat calcOutputFormatForInput(FrameFormat format) {
        MutableFrameFormat outputFormat = format.mutableCopy();
        this.mInputWidth = format.getWidth();
        this.mInputHeight = format.getHeight();
        this.mSliceWidth = this.mInputWidth - (2 * this.mPadSize);
        this.mSliceHeight = this.mInputHeight - (2 * this.mPadSize);
        this.mImageWidth = this.mSliceWidth * this.mXSlices;
        this.mImageHeight = this.mSliceHeight * this.mYSlices;
        outputFormat.setDimensions(this.mImageWidth, this.mImageHeight);
        return outputFormat;
    }

    @Override // android.filterfw.core.Filter
    public void process(FilterContext context) {
        Frame input = pullInput("image");
        FrameFormat format = input.getFormat();
        if (this.mSliceIndex == 0) {
            this.mOutputFrame = context.getFrameManager().newFrame(calcOutputFormatForInput(format));
        } else if (format.getWidth() != this.mInputWidth || format.getHeight() != this.mInputHeight) {
            throw new RuntimeException("Image size should not change.");
        }
        if (this.mProgram == null) {
            this.mProgram = ShaderProgram.createIdentity(context);
        }
        float x0 = this.mPadSize / this.mInputWidth;
        float y0 = this.mPadSize / this.mInputHeight;
        int outputOffsetX = (this.mSliceIndex % this.mXSlices) * this.mSliceWidth;
        int outputOffsetY = (this.mSliceIndex / this.mXSlices) * this.mSliceHeight;
        float outputWidth = Math.min(this.mSliceWidth, this.mImageWidth - outputOffsetX);
        float outputHeight = Math.min(this.mSliceHeight, this.mImageHeight - outputOffsetY);
        ((ShaderProgram) this.mProgram).setSourceRect(x0, y0, outputWidth / this.mInputWidth, outputHeight / this.mInputHeight);
        ((ShaderProgram) this.mProgram).setTargetRect(outputOffsetX / this.mImageWidth, outputOffsetY / this.mImageHeight, outputWidth / this.mImageWidth, outputHeight / this.mImageHeight);
        this.mProgram.process(input, this.mOutputFrame);
        this.mSliceIndex++;
        if (this.mSliceIndex == this.mXSlices * this.mYSlices) {
            pushOutput("image", this.mOutputFrame);
            this.mOutputFrame.release();
            this.mSliceIndex = 0;
        }
    }
}