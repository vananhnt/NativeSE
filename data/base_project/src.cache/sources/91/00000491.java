package android.filterfw.core;

/* loaded from: Program.class */
public abstract class Program {
    public abstract void process(Frame[] frameArr, Frame frame);

    public abstract void setHostValue(String str, Object obj);

    public abstract Object getHostValue(String str);

    public void process(Frame input, Frame output) {
        Frame[] inputs = {input};
        process(inputs, output);
    }

    public void reset() {
    }
}