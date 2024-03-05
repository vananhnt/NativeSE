package android.filterpacks.text;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.format.ObjectFormat;

/* loaded from: StringSource.class */
public class StringSource extends Filter {
    @GenerateFieldPort(name = "stringValue")
    private String mString;
    private FrameFormat mOutputFormat;

    public StringSource(String name) {
        super(name);
    }

    @Override // android.filterfw.core.Filter
    public void setupPorts() {
        this.mOutputFormat = ObjectFormat.fromClass(String.class, 1);
        addOutputPort("string", this.mOutputFormat);
    }

    @Override // android.filterfw.core.Filter
    public void process(FilterContext env) {
        Frame output = env.getFrameManager().newFrame(this.mOutputFormat);
        output.setObjectValue(this.mString);
        output.setTimestamp(-1L);
        pushOutput("string", output);
        closeOutputPort("string");
    }
}