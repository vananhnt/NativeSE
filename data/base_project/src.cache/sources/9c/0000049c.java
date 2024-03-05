package android.filterfw.core;

import android.filterfw.format.ObjectFormat;
import android.graphics.Bitmap;
import gov.nist.core.Separators;
import java.nio.ByteBuffer;

/* loaded from: SimpleFrame.class */
public class SimpleFrame extends Frame {
    private Object mObject;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SimpleFrame(FrameFormat format, FrameManager frameManager) {
        super(format, frameManager);
        initWithFormat(format);
        setReusable(false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static SimpleFrame wrapObject(Object object, FrameManager frameManager) {
        FrameFormat format = ObjectFormat.fromObject(object, 1);
        SimpleFrame result = new SimpleFrame(format, frameManager);
        result.setObjectValue(object);
        return result;
    }

    private void initWithFormat(FrameFormat format) {
        int count = format.getLength();
        int baseType = format.getBaseType();
        switch (baseType) {
            case 2:
                this.mObject = new byte[count];
                return;
            case 3:
                this.mObject = new short[count];
                return;
            case 4:
                this.mObject = new int[count];
                return;
            case 5:
                this.mObject = new float[count];
                return;
            case 6:
                this.mObject = new double[count];
                return;
            default:
                this.mObject = null;
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.filterfw.core.Frame
    public boolean hasNativeAllocation() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.filterfw.core.Frame
    public void releaseNativeAllocation() {
    }

    @Override // android.filterfw.core.Frame
    public Object getObjectValue() {
        return this.mObject;
    }

    @Override // android.filterfw.core.Frame
    public void setInts(int[] ints) {
        assertFrameMutable();
        setGenericObjectValue(ints);
    }

    @Override // android.filterfw.core.Frame
    public int[] getInts() {
        if (this.mObject instanceof int[]) {
            return (int[]) this.mObject;
        }
        return null;
    }

    @Override // android.filterfw.core.Frame
    public void setFloats(float[] floats) {
        assertFrameMutable();
        setGenericObjectValue(floats);
    }

    @Override // android.filterfw.core.Frame
    public float[] getFloats() {
        if (this.mObject instanceof float[]) {
            return (float[]) this.mObject;
        }
        return null;
    }

    @Override // android.filterfw.core.Frame
    public void setData(ByteBuffer buffer, int offset, int length) {
        assertFrameMutable();
        setGenericObjectValue(ByteBuffer.wrap(buffer.array(), offset, length));
    }

    @Override // android.filterfw.core.Frame
    public ByteBuffer getData() {
        if (this.mObject instanceof ByteBuffer) {
            return (ByteBuffer) this.mObject;
        }
        return null;
    }

    @Override // android.filterfw.core.Frame
    public void setBitmap(Bitmap bitmap) {
        assertFrameMutable();
        setGenericObjectValue(bitmap);
    }

    @Override // android.filterfw.core.Frame
    public Bitmap getBitmap() {
        if (this.mObject instanceof Bitmap) {
            return (Bitmap) this.mObject;
        }
        return null;
    }

    private void setFormatObjectClass(Class objectClass) {
        MutableFrameFormat format = getFormat().mutableCopy();
        format.setObjectClass(objectClass);
        setFormat(format);
    }

    @Override // android.filterfw.core.Frame
    protected void setGenericObjectValue(Object object) {
        FrameFormat format = getFormat();
        if (format.getObjectClass() == null) {
            setFormatObjectClass(object.getClass());
        } else if (!format.getObjectClass().isAssignableFrom(object.getClass())) {
            throw new RuntimeException("Attempting to set object value of type '" + object.getClass() + "' on SimpleFrame of type '" + format.getObjectClass() + "'!");
        }
        this.mObject = object;
    }

    public String toString() {
        return "SimpleFrame (" + getFormat() + Separators.RPAREN;
    }
}