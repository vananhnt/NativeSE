package android.filterpacks.numeric;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.format.ObjectFormat;

/* loaded from: SinWaveFilter.class */
public class SinWaveFilter extends Filter {
    @GenerateFieldPort(name = "stepSize", hasDefault = true)
    private float mStepSize;
    private float mValue;
    private FrameFormat mOutputFormat;

    public SinWaveFilter(String name) {
        super(name);
        this.mStepSize = 0.05f;
        this.mValue = 0.0f;
    }

    @Override // android.filterfw.core.Filter
    public void setupPorts() {
        this.mOutputFormat = ObjectFormat.fromClass(Float.class, 1);
        addOutputPort("value", this.mOutputFormat);
    }

    @Override // android.filterfw.core.Filter
    public void open(FilterContext env) {
        this.mValue = 0.0f;
    }

    @Override // android.filterfw.core.Filter
    public void process(FilterContext env) {
        Frame output = env.getFrameManager().newFrame(this.mOutputFormat);
        output.setObjectValue(Float.valueOf((((float) Math.sin(this.mValue)) + 1.0f) / 2.0f));
        pushOutput("value", output);
        this.mValue += this.mStepSize;
        output.release();
    }
}