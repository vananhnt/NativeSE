package android.filterfw.core;

/* loaded from: SimpleFrameManager.class */
public class SimpleFrameManager extends FrameManager {
    @Override // android.filterfw.core.FrameManager
    public Frame newFrame(FrameFormat format) {
        return createNewFrame(format);
    }

    @Override // android.filterfw.core.FrameManager
    public Frame newBoundFrame(FrameFormat format, int bindingType, long bindingId) {
        switch (format.getTarget()) {
            case 3:
                GLFrame glFrame = new GLFrame(format, this, bindingType, bindingId);
                glFrame.init(getGLEnvironment());
                return glFrame;
            default:
                throw new RuntimeException("Attached frames are not supported for target type: " + FrameFormat.targetToString(format.getTarget()) + "!");
        }
    }

    private Frame createNewFrame(FrameFormat format) {
        Frame result;
        switch (format.getTarget()) {
            case 1:
                result = new SimpleFrame(format, this);
                break;
            case 2:
                result = new NativeFrame(format, this);
                break;
            case 3:
                GLFrame glFrame = new GLFrame(format, this);
                glFrame.init(getGLEnvironment());
                result = glFrame;
                break;
            case 4:
                result = new VertexFrame(format, this);
                break;
            default:
                throw new RuntimeException("Unsupported frame target type: " + FrameFormat.targetToString(format.getTarget()) + "!");
        }
        return result;
    }

    @Override // android.filterfw.core.FrameManager
    public Frame retainFrame(Frame frame) {
        frame.incRefCount();
        return frame;
    }

    @Override // android.filterfw.core.FrameManager
    public Frame releaseFrame(Frame frame) {
        int refCount = frame.decRefCount();
        if (refCount == 0 && frame.hasNativeAllocation()) {
            frame.releaseNativeAllocation();
            return null;
        } else if (refCount < 0) {
            throw new RuntimeException("Frame reference count dropped below 0!");
        } else {
            return frame;
        }
    }
}