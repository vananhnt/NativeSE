package android.filterpacks.base;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.core.GenerateFinalPort;

/* loaded from: FrameSource.class */
public class FrameSource extends Filter {
    @GenerateFinalPort(name = "format")
    private FrameFormat mFormat;
    @GenerateFieldPort(name = "frame", hasDefault = true)
    private Frame mFrame;
    @GenerateFieldPort(name = "repeatFrame", hasDefault = true)
    private boolean mRepeatFrame;

    public FrameSource(String name) {
        super(name);
        this.mFrame = null;
        this.mRepeatFrame = false;
    }

    @Override // android.filterfw.core.Filter
    public void setupPorts() {
        addOutputPort("frame", this.mFormat);
    }

    @Override // android.filterfw.core.Filter
    public void process(FilterContext context) {
        if (this.mFrame != null) {
            pushOutput("frame", this.mFrame);
        }
        if (!this.mRepeatFrame) {
            closeOutputPort("frame");
        }
    }
}