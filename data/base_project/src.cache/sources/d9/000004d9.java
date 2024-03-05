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

/* loaded from: ImageSlicer.class */
public class ImageSlicer extends Filter {
    @GenerateFieldPort(name = "xSlices")
    private int mXSlices;
    @GenerateFieldPort(name = "ySlices")
    private int mYSlices;
    @GenerateFieldPort(name = "padSize")
    private int mPadSize;
    private int mSliceIndex;
    private Frame mOriginalFrame;
    private Program mProgram;
    private int mInputWidth;
    private int mInputHeight;
    private int mSliceWidth;
    private int mSliceHeight;
    private int mOutputWidth;
    private int mOutputHeight;

    public ImageSlicer(String name) {
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

    private void calcOutputFormatForInput(Frame frame) {
        this.mInputWidth = frame.getFormat().getWidth();
        this.mInputHeight = frame.getFormat().getHeight();
        this.mSliceWidth = ((this.mInputWidth + this.mXSlices) - 1) / this.mXSlices;
        this.mSliceHeight = ((this.mInputHeight + this.mYSlices) - 1) / this.mYSlices;
        this.mOutputWidth = this.mSliceWidth + (this.mPadSize * 2);
        this.mOutputHeight = this.mSliceHeight + (this.mPadSize * 2);
    }

    @Override // android.filterfw.core.Filter
    public void process(FilterContext context) {
        if (this.mSliceIndex == 0) {
            this.mOriginalFrame = pullInput("image");
            calcOutputFormatForInput(this.mOriginalFrame);
        }
        FrameFormat inputFormat = this.mOriginalFrame.getFormat();
        MutableFrameFormat outputFormat = inputFormat.mutableCopy();
        outputFormat.setDimensions(this.mOutputWidth, this.mOutputHeight);
        Frame output = context.getFrameManager().newFrame(outputFormat);
        if (this.mProgram == null) {
            this.mProgram = ShaderProgram.createIdentity(context);
        }
        int xSliceIndex = this.mSliceIndex % this.mXSlices;
        int ySliceIndex = this.mSliceIndex / this.mXSlices;
        float x0 = ((xSliceIndex * this.mSliceWidth) - this.mPadSize) / this.mInputWidth;
        float y0 = ((ySliceIndex * this.mSliceHeight) - this.mPadSize) / this.mInputHeight;
        ((ShaderProgram) this.mProgram).setSourceRect(x0, y0, this.mOutputWidth / this.mInputWidth, this.mOutputHeight / this.mInputHeight);
        this.mProgram.process(this.mOriginalFrame, output);
        this.mSliceIndex++;
        if (this.mSliceIndex == this.mXSlices * this.mYSlices) {
            this.mSliceIndex = 0;
            this.mOriginalFrame.release();
            setWaitsOnInputPort("image", true);
        } else {
            this.mOriginalFrame.retain();
            setWaitsOnInputPort("image", false);
        }
        pushOutput("image", output);
        output.release();
    }
}