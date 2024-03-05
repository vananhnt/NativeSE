package org.apache.harmony.dalvik.ddmc;

/* loaded from: DdmVmInternal.class */
public class DdmVmInternal {
    public static native void threadNotify(boolean z);

    public static native boolean heapInfoNotify(int i);

    public static native boolean heapSegmentNotify(int i, int i2, boolean z);

    public static native byte[] getThreadStats();

    public static native StackTraceElement[] getStackTraceById(int i);

    public static native void enableRecentAllocations(boolean z);

    public static native boolean getRecentAllocationStatus();

    public static native byte[] getRecentAllocations();

    private DdmVmInternal() {
    }
}