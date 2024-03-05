package android.os;

import android.os.Parcelable;
import android.util.Log;
import com.android.internal.util.FastPrintWriter;
import com.android.internal.util.TypedProperties;
import dalvik.bytecode.OpcodeInfo;
import dalvik.system.VMDebug;
import gov.nist.core.Separators;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import org.apache.harmony.dalvik.ddmc.Chunk;
import org.apache.harmony.dalvik.ddmc.ChunkHandler;
import org.apache.harmony.dalvik.ddmc.DdmServer;

/* loaded from: Debug.class */
public final class Debug {
    private static final String TAG = "Debug";
    public static final int TRACE_COUNT_ALLOCS = 1;
    public static final int SHOW_FULL_DETAIL = 1;
    public static final int SHOW_CLASSLOADER = 2;
    public static final int SHOW_INITIALIZED = 4;
    private static final int MIN_DEBUGGER_IDLE = 1300;
    private static final int SPIN_DELAY = 200;
    private static final String SYSFS_QEMU_TRACE_STATE = "/sys/qemu_trace/state";
    public static final int MEMINFO_TOTAL = 0;
    public static final int MEMINFO_FREE = 1;
    public static final int MEMINFO_BUFFERS = 2;
    public static final int MEMINFO_CACHED = 3;
    public static final int MEMINFO_SHMEM = 4;
    public static final int MEMINFO_SLAB = 5;
    public static final int MEMINFO_SWAP_TOTAL = 6;
    public static final int MEMINFO_SWAP_FREE = 7;
    public static final int MEMINFO_ZRAM_TOTAL = 8;
    public static final int MEMINFO_COUNT = 9;
    private static volatile boolean mWaiting = false;
    private static final String DEFAULT_TRACE_PATH_PREFIX = Environment.getLegacyExternalStorageDirectory().getPath() + Separators.SLASH;
    private static final String DEFAULT_TRACE_BODY = "dmtrace";
    private static final String DEFAULT_TRACE_EXTENSION = ".trace";
    private static final String DEFAULT_TRACE_FILE_PATH = DEFAULT_TRACE_PATH_PREFIX + DEFAULT_TRACE_BODY + DEFAULT_TRACE_EXTENSION;
    private static final TypedProperties debugProperties = null;

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    /* loaded from: Debug$DebugProperty.class */
    public @interface DebugProperty {
    }

    public static native long getNativeHeapSize();

    public static native long getNativeHeapAllocatedSize();

    public static native long getNativeHeapFreeSize();

    public static native void getMemoryInfo(MemoryInfo memoryInfo);

    public static native void getMemoryInfo(int i, MemoryInfo memoryInfo);

    public static native long getPss();

    public static native long getPss(int i, long[] jArr);

    public static native void getMemInfo(long[] jArr);

    public static native void dumpNativeHeap(FileDescriptor fileDescriptor);

    public static native int getBinderSentTransactions();

    public static native int getBinderReceivedTransactions();

    public static final native int getBinderLocalObjectCount();

    public static final native int getBinderProxyObjectCount();

    public static final native int getBinderDeathObjectCount();

    public static native void dumpNativeBacktraceToFile(int i, String str);

    private Debug() {
    }

    /* loaded from: Debug$MemoryInfo.class */
    public static class MemoryInfo implements Parcelable {
        public int dalvikPss;
        public int dalvikSwappablePss;
        public int dalvikPrivateDirty;
        public int dalvikSharedDirty;
        public int dalvikPrivateClean;
        public int dalvikSharedClean;
        public int dalvikSwappedOut;
        public int nativePss;
        public int nativeSwappablePss;
        public int nativePrivateDirty;
        public int nativeSharedDirty;
        public int nativePrivateClean;
        public int nativeSharedClean;
        public int nativeSwappedOut;
        public int otherPss;
        public int otherSwappablePss;
        public int otherPrivateDirty;
        public int otherSharedDirty;
        public int otherPrivateClean;
        public int otherSharedClean;
        public int otherSwappedOut;
        public static final int NUM_OTHER_STATS = 16;
        public static final int NUM_DVK_STATS = 5;
        public static final int NUM_CATEGORIES = 7;
        public static final int offsetPss = 0;
        public static final int offsetSwappablePss = 1;
        public static final int offsetPrivateDirty = 2;
        public static final int offsetSharedDirty = 3;
        public static final int offsetPrivateClean = 4;
        public static final int offsetSharedClean = 5;
        public static final int offsetSwappedOut = 6;
        private int[] otherStats;
        public static final Parcelable.Creator<MemoryInfo> CREATOR = new Parcelable.Creator<MemoryInfo>() { // from class: android.os.Debug.MemoryInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public MemoryInfo createFromParcel(Parcel source) {
                return new MemoryInfo(source);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public MemoryInfo[] newArray(int size) {
                return new MemoryInfo[size];
            }
        };

        public MemoryInfo() {
            this.otherStats = new int[147];
        }

        public int getTotalPss() {
            return this.dalvikPss + this.nativePss + this.otherPss;
        }

        public int getTotalUss() {
            return this.dalvikPrivateClean + this.dalvikPrivateDirty + this.nativePrivateClean + this.nativePrivateDirty + this.otherPrivateClean + this.otherPrivateDirty;
        }

        public int getTotalSwappablePss() {
            return this.dalvikSwappablePss + this.nativeSwappablePss + this.otherSwappablePss;
        }

        public int getTotalPrivateDirty() {
            return this.dalvikPrivateDirty + this.nativePrivateDirty + this.otherPrivateDirty;
        }

        public int getTotalSharedDirty() {
            return this.dalvikSharedDirty + this.nativeSharedDirty + this.otherSharedDirty;
        }

        public int getTotalPrivateClean() {
            return this.dalvikPrivateClean + this.nativePrivateClean + this.otherPrivateClean;
        }

        public int getTotalSharedClean() {
            return this.dalvikSharedClean + this.nativeSharedClean + this.otherSharedClean;
        }

        public int getTotalSwappedOut() {
            return this.dalvikSwappedOut + this.nativeSwappedOut + this.otherSwappedOut;
        }

        public int getOtherPss(int which) {
            return this.otherStats[(which * 7) + 0];
        }

        public int getOtherSwappablePss(int which) {
            return this.otherStats[(which * 7) + 1];
        }

        public int getOtherPrivateDirty(int which) {
            return this.otherStats[(which * 7) + 2];
        }

        public int getOtherSharedDirty(int which) {
            return this.otherStats[(which * 7) + 3];
        }

        public int getOtherPrivateClean(int which) {
            return this.otherStats[(which * 7) + 4];
        }

        public int getOtherSharedClean(int which) {
            return this.otherStats[(which * 7) + 5];
        }

        public int getOtherSwappedOut(int which) {
            return this.otherStats[(which * 7) + 6];
        }

        public static String getOtherLabel(int which) {
            switch (which) {
                case 0:
                    return "Dalvik Other";
                case 1:
                    return "Stack";
                case 2:
                    return "Cursor";
                case 3:
                    return "Ashmem";
                case 4:
                    return "Other dev";
                case 5:
                    return ".so mmap";
                case 6:
                    return ".jar mmap";
                case 7:
                    return ".apk mmap";
                case 8:
                    return ".ttf mmap";
                case 9:
                    return ".dex mmap";
                case 10:
                    return "code mmap";
                case 11:
                    return "image mmap";
                case 12:
                    return "Other mmap";
                case 13:
                    return "Graphics";
                case 14:
                    return "GL";
                case 15:
                    return "Memtrack";
                case 16:
                    return ".Heap";
                case 17:
                    return ".LOS";
                case 18:
                    return ".LinearAlloc";
                case 19:
                    return ".GC";
                case 20:
                    return ".JITCache";
                default:
                    return "????";
            }
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.dalvikPss);
            dest.writeInt(this.dalvikSwappablePss);
            dest.writeInt(this.dalvikPrivateDirty);
            dest.writeInt(this.dalvikSharedDirty);
            dest.writeInt(this.dalvikPrivateClean);
            dest.writeInt(this.dalvikSharedClean);
            dest.writeInt(this.dalvikSwappedOut);
            dest.writeInt(this.nativePss);
            dest.writeInt(this.nativeSwappablePss);
            dest.writeInt(this.nativePrivateDirty);
            dest.writeInt(this.nativeSharedDirty);
            dest.writeInt(this.nativePrivateClean);
            dest.writeInt(this.nativeSharedClean);
            dest.writeInt(this.nativeSwappedOut);
            dest.writeInt(this.otherPss);
            dest.writeInt(this.otherSwappablePss);
            dest.writeInt(this.otherPrivateDirty);
            dest.writeInt(this.otherSharedDirty);
            dest.writeInt(this.otherPrivateClean);
            dest.writeInt(this.otherSharedClean);
            dest.writeInt(this.otherSwappedOut);
            dest.writeIntArray(this.otherStats);
        }

        public void readFromParcel(Parcel source) {
            this.dalvikPss = source.readInt();
            this.dalvikSwappablePss = source.readInt();
            this.dalvikPrivateDirty = source.readInt();
            this.dalvikSharedDirty = source.readInt();
            this.dalvikPrivateClean = source.readInt();
            this.dalvikSharedClean = source.readInt();
            this.dalvikSwappedOut = source.readInt();
            this.nativePss = source.readInt();
            this.nativeSwappablePss = source.readInt();
            this.nativePrivateDirty = source.readInt();
            this.nativeSharedDirty = source.readInt();
            this.nativePrivateClean = source.readInt();
            this.nativeSharedClean = source.readInt();
            this.nativeSwappedOut = source.readInt();
            this.otherPss = source.readInt();
            this.otherSwappablePss = source.readInt();
            this.otherPrivateDirty = source.readInt();
            this.otherSharedDirty = source.readInt();
            this.otherPrivateClean = source.readInt();
            this.otherSharedClean = source.readInt();
            this.otherSwappedOut = source.readInt();
            this.otherStats = source.createIntArray();
        }

        private MemoryInfo(Parcel source) {
            this.otherStats = new int[147];
            readFromParcel(source);
        }
    }

    public static void waitForDebugger() {
        if (!VMDebug.isDebuggingEnabled() || isDebuggerConnected()) {
            return;
        }
        System.out.println("Sending WAIT chunk");
        byte[] data = {0};
        Chunk waitChunk = new Chunk(ChunkHandler.type("WAIT"), data, 0, 1);
        DdmServer.sendChunk(waitChunk);
        mWaiting = true;
        while (!isDebuggerConnected()) {
            try {
                Thread.sleep(200L);
            } catch (InterruptedException e) {
            }
        }
        mWaiting = false;
        System.out.println("Debugger has connected");
        while (true) {
            long delta = VMDebug.lastDebuggerActivity();
            if (delta < 0) {
                System.out.println("debugger detached?");
                return;
            } else if (delta < 1300) {
                System.out.println("waiting for debugger to settle...");
                try {
                    Thread.sleep(200L);
                } catch (InterruptedException e2) {
                }
            } else {
                System.out.println("debugger has settled (" + delta + Separators.RPAREN);
                return;
            }
        }
    }

    public static boolean waitingForDebugger() {
        return mWaiting;
    }

    public static boolean isDebuggerConnected() {
        return VMDebug.isDebuggerConnected();
    }

    public static String[] getVmFeatureList() {
        return VMDebug.getVmFeatureList();
    }

    @Deprecated
    public static void changeDebugPort(int port) {
    }

    public static void startNativeTracing() {
        PrintWriter outStream = null;
        try {
            FileOutputStream fos = new FileOutputStream(SYSFS_QEMU_TRACE_STATE);
            outStream = new FastPrintWriter(fos);
            outStream.println("1");
            if (outStream != null) {
                outStream.close();
            }
        } catch (Exception e) {
            if (outStream != null) {
                outStream.close();
            }
        } catch (Throwable th) {
            if (outStream != null) {
                outStream.close();
            }
            throw th;
        }
        VMDebug.startEmulatorTracing();
    }

    public static void stopNativeTracing() {
        VMDebug.stopEmulatorTracing();
        PrintWriter outStream = null;
        try {
            FileOutputStream fos = new FileOutputStream(SYSFS_QEMU_TRACE_STATE);
            outStream = new FastPrintWriter(fos);
            outStream.println("0");
            if (outStream != null) {
                outStream.close();
            }
        } catch (Exception e) {
            if (outStream != null) {
                outStream.close();
            }
        } catch (Throwable th) {
            if (outStream != null) {
                outStream.close();
            }
            throw th;
        }
    }

    public static void enableEmulatorTraceOutput() {
        VMDebug.startEmulatorTracing();
    }

    public static void startMethodTracing() {
        VMDebug.startMethodTracing(DEFAULT_TRACE_FILE_PATH, 0, 0);
    }

    public static void startMethodTracing(String traceName) {
        startMethodTracing(traceName, 0, 0);
    }

    public static void startMethodTracing(String traceName, int bufferSize) {
        startMethodTracing(traceName, bufferSize, 0);
    }

    public static void startMethodTracing(String traceName, int bufferSize, int flags) {
        String pathName = traceName;
        if (pathName.charAt(0) != '/') {
            pathName = DEFAULT_TRACE_PATH_PREFIX + pathName;
        }
        if (!pathName.endsWith(DEFAULT_TRACE_EXTENSION)) {
            pathName = pathName + DEFAULT_TRACE_EXTENSION;
        }
        VMDebug.startMethodTracing(pathName, bufferSize, flags);
    }

    public static void startMethodTracing(String traceName, FileDescriptor fd, int bufferSize, int flags) {
        VMDebug.startMethodTracing(traceName, fd, bufferSize, flags);
    }

    public static void startMethodTracingDdms(int bufferSize, int flags, boolean samplingEnabled, int intervalUs) {
        VMDebug.startMethodTracingDdms(bufferSize, flags, samplingEnabled, intervalUs);
    }

    public static int getMethodTracingMode() {
        return VMDebug.getMethodTracingMode();
    }

    public static void stopMethodTracing() {
        VMDebug.stopMethodTracing();
    }

    public static long threadCpuTimeNanos() {
        return VMDebug.threadCpuTimeNanos();
    }

    @Deprecated
    public static void startAllocCounting() {
        VMDebug.startAllocCounting();
    }

    @Deprecated
    public static void stopAllocCounting() {
        VMDebug.stopAllocCounting();
    }

    public static int getGlobalAllocCount() {
        return VMDebug.getAllocCount(1);
    }

    public static void resetGlobalAllocCount() {
        VMDebug.resetAllocCount(1);
    }

    public static int getGlobalAllocSize() {
        return VMDebug.getAllocCount(2);
    }

    public static void resetGlobalAllocSize() {
        VMDebug.resetAllocCount(2);
    }

    public static int getGlobalFreedCount() {
        return VMDebug.getAllocCount(4);
    }

    public static void resetGlobalFreedCount() {
        VMDebug.resetAllocCount(4);
    }

    public static int getGlobalFreedSize() {
        return VMDebug.getAllocCount(8);
    }

    public static void resetGlobalFreedSize() {
        VMDebug.resetAllocCount(8);
    }

    public static int getGlobalGcInvocationCount() {
        return VMDebug.getAllocCount(16);
    }

    public static void resetGlobalGcInvocationCount() {
        VMDebug.resetAllocCount(16);
    }

    public static int getGlobalClassInitCount() {
        return VMDebug.getAllocCount(32);
    }

    public static void resetGlobalClassInitCount() {
        VMDebug.resetAllocCount(32);
    }

    public static int getGlobalClassInitTime() {
        return VMDebug.getAllocCount(64);
    }

    public static void resetGlobalClassInitTime() {
        VMDebug.resetAllocCount(64);
    }

    @Deprecated
    public static int getGlobalExternalAllocCount() {
        return 0;
    }

    @Deprecated
    public static void resetGlobalExternalAllocSize() {
    }

    @Deprecated
    public static void resetGlobalExternalAllocCount() {
    }

    @Deprecated
    public static int getGlobalExternalAllocSize() {
        return 0;
    }

    @Deprecated
    public static int getGlobalExternalFreedCount() {
        return 0;
    }

    @Deprecated
    public static void resetGlobalExternalFreedCount() {
    }

    @Deprecated
    public static int getGlobalExternalFreedSize() {
        return 0;
    }

    @Deprecated
    public static void resetGlobalExternalFreedSize() {
    }

    public static int getThreadAllocCount() {
        return VMDebug.getAllocCount(65536);
    }

    public static void resetThreadAllocCount() {
        VMDebug.resetAllocCount(65536);
    }

    public static int getThreadAllocSize() {
        return VMDebug.getAllocCount(131072);
    }

    public static void resetThreadAllocSize() {
        VMDebug.resetAllocCount(131072);
    }

    @Deprecated
    public static int getThreadExternalAllocCount() {
        return 0;
    }

    @Deprecated
    public static void resetThreadExternalAllocCount() {
    }

    @Deprecated
    public static int getThreadExternalAllocSize() {
        return 0;
    }

    @Deprecated
    public static void resetThreadExternalAllocSize() {
    }

    public static int getThreadGcInvocationCount() {
        return VMDebug.getAllocCount(1048576);
    }

    public static void resetThreadGcInvocationCount() {
        VMDebug.resetAllocCount(1048576);
    }

    public static void resetAllCounts() {
        VMDebug.resetAllocCount(-1);
    }

    @Deprecated
    public static int setAllocationLimit(int limit) {
        return -1;
    }

    @Deprecated
    public static int setGlobalAllocationLimit(int limit) {
        return -1;
    }

    public static void printLoadedClasses(int flags) {
        VMDebug.printLoadedClasses(flags);
    }

    public static int getLoadedClassCount() {
        return VMDebug.getLoadedClassCount();
    }

    public static void dumpHprofData(String fileName) throws IOException {
        VMDebug.dumpHprofData(fileName);
    }

    public static void dumpHprofData(String fileName, FileDescriptor fd) throws IOException {
        VMDebug.dumpHprofData(fileName, fd);
    }

    public static void dumpHprofDataDdms() {
        VMDebug.dumpHprofDataDdms();
    }

    public static long countInstancesOfClass(Class cls) {
        return VMDebug.countInstancesOfClass(cls, true);
    }

    public static final boolean cacheRegisterMap(String classAndMethodDesc) {
        return VMDebug.cacheRegisterMap(classAndMethodDesc);
    }

    public static final void dumpReferenceTables() {
        VMDebug.dumpReferenceTables();
    }

    /* loaded from: Debug$InstructionCount.class */
    public static class InstructionCount {
        private static final int NUM_INSTR = OpcodeInfo.MAXIMUM_PACKED_VALUE + 1;
        private int[] mCounts = new int[NUM_INSTR];

        public boolean resetAndStart() {
            try {
                VMDebug.startInstructionCounting();
                VMDebug.resetInstructionCount();
                return true;
            } catch (UnsupportedOperationException e) {
                return false;
            }
        }

        public boolean collect() {
            try {
                VMDebug.stopInstructionCounting();
                VMDebug.getInstructionCount(this.mCounts);
                return true;
            } catch (UnsupportedOperationException e) {
                return false;
            }
        }

        public int globalTotal() {
            int count = 0;
            for (int i = 0; i < NUM_INSTR; i++) {
                count += this.mCounts[i];
            }
            return count;
        }

        public int globalMethodInvocations() {
            int count = 0;
            for (int i = 0; i < NUM_INSTR; i++) {
                if (OpcodeInfo.isInvoke(i)) {
                    count += this.mCounts[i];
                }
            }
            return count;
        }
    }

    private static boolean fieldTypeMatches(Field field, Class<?> cl) {
        Class<?> fieldClass = field.getType();
        if (fieldClass == cl) {
            return true;
        }
        try {
            Field primitiveTypeField = cl.getField("TYPE");
            try {
                return fieldClass == ((Class) primitiveTypeField.get(null));
            } catch (IllegalAccessException e) {
                return false;
            }
        } catch (NoSuchFieldException e2) {
            return false;
        }
    }

    private static void modifyFieldIfSet(Field field, TypedProperties properties, String propertyName) {
        if (field.getType() == String.class) {
            int stringInfo = properties.getStringInfo(propertyName);
            switch (stringInfo) {
                case -2:
                    throw new IllegalArgumentException("Type of " + propertyName + Separators.SP + " does not match field type (" + field.getType() + Separators.RPAREN);
                case -1:
                    return;
                case 0:
                    try {
                        field.set(null, null);
                        return;
                    } catch (IllegalAccessException ex) {
                        throw new IllegalArgumentException("Cannot set field for " + propertyName, ex);
                    }
                case 1:
                    break;
                default:
                    throw new IllegalStateException("Unexpected getStringInfo(" + propertyName + ") return value " + stringInfo);
            }
        }
        Object value = properties.get(propertyName);
        if (value != null) {
            if (!fieldTypeMatches(field, value.getClass())) {
                throw new IllegalArgumentException("Type of " + propertyName + " (" + value.getClass() + ")  does not match field type (" + field.getType() + Separators.RPAREN);
            }
            try {
                field.set(null, value);
            } catch (IllegalAccessException ex2) {
                throw new IllegalArgumentException("Cannot set field for " + propertyName, ex2);
            }
        }
    }

    public static void setFieldsOn(Class<?> cl) {
        setFieldsOn(cl, false);
    }

    public static void setFieldsOn(Class<?> cl, boolean partial) {
        Log.wtf(TAG, "setFieldsOn(" + (cl == null ? "null" : cl.getName()) + ") called in non-DEBUG build");
    }

    public static boolean dumpService(String name, FileDescriptor fd, String[] args) {
        IBinder service = ServiceManager.getService(name);
        if (service == null) {
            Log.e(TAG, "Can't find service to dump: " + name);
            return false;
        }
        try {
            service.dump(fd, args);
            return true;
        } catch (RemoteException e) {
            Log.e(TAG, "Can't dump service: " + name, e);
            return false;
        }
    }

    private static String getCaller(StackTraceElement[] callStack, int depth) {
        if (4 + depth >= callStack.length) {
            return "<bottom of call stack>";
        }
        StackTraceElement caller = callStack[4 + depth];
        return caller.getClassName() + Separators.DOT + caller.getMethodName() + Separators.COLON + caller.getLineNumber();
    }

    public static String getCallers(int depth) {
        StackTraceElement[] callStack = Thread.currentThread().getStackTrace();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < depth; i++) {
            sb.append(getCaller(callStack, i)).append(Separators.SP);
        }
        return sb.toString();
    }

    public static String getCallers(int start, int depth) {
        StackTraceElement[] callStack = Thread.currentThread().getStackTrace();
        StringBuffer sb = new StringBuffer();
        int depth2 = depth + start;
        for (int i = start; i < depth2; i++) {
            sb.append(getCaller(callStack, i)).append(Separators.SP);
        }
        return sb.toString();
    }

    public static String getCallers(int depth, String linePrefix) {
        StackTraceElement[] callStack = Thread.currentThread().getStackTrace();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < depth; i++) {
            sb.append(linePrefix).append(getCaller(callStack, i)).append(Separators.RETURN);
        }
        return sb.toString();
    }

    public static String getCaller() {
        return getCaller(Thread.currentThread().getStackTrace(), 0);
    }
}