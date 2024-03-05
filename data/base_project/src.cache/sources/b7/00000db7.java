package android.renderscript;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.os.SystemProperties;
import android.util.Log;
import android.view.Surface;
import java.io.File;
import java.lang.reflect.Method;

/* loaded from: RenderScript.class */
public class RenderScript {
    static final long TRACE_TAG = 32768;
    static final String LOG_TAG = "RenderScript_jni";
    static final boolean DEBUG = false;
    static final boolean LOG_ENABLED = false;
    private Context mApplicationContext;
    static boolean sInitialized;
    static Object sRuntime;
    static Method registerNativeAllocation;
    static Method registerNativeFree;
    static File mCacheDir;
    int mDev;
    int mContext;
    MessageThread mMessageThread;
    Element mElement_U8;
    Element mElement_I8;
    Element mElement_U16;
    Element mElement_I16;
    Element mElement_U32;
    Element mElement_I32;
    Element mElement_U64;
    Element mElement_I64;
    Element mElement_F32;
    Element mElement_F64;
    Element mElement_BOOLEAN;
    Element mElement_ELEMENT;
    Element mElement_TYPE;
    Element mElement_ALLOCATION;
    Element mElement_SAMPLER;
    Element mElement_SCRIPT;
    Element mElement_MESH;
    Element mElement_PROGRAM_FRAGMENT;
    Element mElement_PROGRAM_VERTEX;
    Element mElement_PROGRAM_RASTER;
    Element mElement_PROGRAM_STORE;
    Element mElement_FONT;
    Element mElement_A_8;
    Element mElement_RGB_565;
    Element mElement_RGB_888;
    Element mElement_RGBA_5551;
    Element mElement_RGBA_4444;
    Element mElement_RGBA_8888;
    Element mElement_FLOAT_2;
    Element mElement_FLOAT_3;
    Element mElement_FLOAT_4;
    Element mElement_DOUBLE_2;
    Element mElement_DOUBLE_3;
    Element mElement_DOUBLE_4;
    Element mElement_UCHAR_2;
    Element mElement_UCHAR_3;
    Element mElement_UCHAR_4;
    Element mElement_CHAR_2;
    Element mElement_CHAR_3;
    Element mElement_CHAR_4;
    Element mElement_USHORT_2;
    Element mElement_USHORT_3;
    Element mElement_USHORT_4;
    Element mElement_SHORT_2;
    Element mElement_SHORT_3;
    Element mElement_SHORT_4;
    Element mElement_UINT_2;
    Element mElement_UINT_3;
    Element mElement_UINT_4;
    Element mElement_INT_2;
    Element mElement_INT_3;
    Element mElement_INT_4;
    Element mElement_ULONG_2;
    Element mElement_ULONG_3;
    Element mElement_ULONG_4;
    Element mElement_LONG_2;
    Element mElement_LONG_3;
    Element mElement_LONG_4;
    Element mElement_YUV;
    Element mElement_MATRIX_4X4;
    Element mElement_MATRIX_3X3;
    Element mElement_MATRIX_2X2;
    Sampler mSampler_CLAMP_NEAREST;
    Sampler mSampler_CLAMP_LINEAR;
    Sampler mSampler_CLAMP_LINEAR_MIP_LINEAR;
    Sampler mSampler_WRAP_NEAREST;
    Sampler mSampler_WRAP_LINEAR;
    Sampler mSampler_WRAP_LINEAR_MIP_LINEAR;
    Sampler mSampler_MIRRORED_REPEAT_NEAREST;
    Sampler mSampler_MIRRORED_REPEAT_LINEAR;
    Sampler mSampler_MIRRORED_REPEAT_LINEAR_MIP_LINEAR;
    ProgramStore mProgramStore_BLEND_NONE_DEPTH_TEST;
    ProgramStore mProgramStore_BLEND_NONE_DEPTH_NO_DEPTH;
    ProgramStore mProgramStore_BLEND_ALPHA_DEPTH_TEST;
    ProgramStore mProgramStore_BLEND_ALPHA_DEPTH_NO_DEPTH;
    ProgramRaster mProgramRaster_CULL_BACK;
    ProgramRaster mProgramRaster_CULL_FRONT;
    ProgramRaster mProgramRaster_CULL_NONE;
    RSMessageHandler mMessageCallback = null;
    RSErrorHandler mErrorCallback = null;
    ContextType mContextType = ContextType.NORMAL;

    static native void _nInit();

    /* JADX INFO: Access modifiers changed from: package-private */
    public native int nDeviceCreate();

    native void nDeviceDestroy(int i);

    native void nDeviceSetConfig(int i, int i2, int i3);

    native int nContextGetUserMessage(int i, int[] iArr);

    native String nContextGetErrorMessage(int i);

    native int nContextPeekMessage(int i, int[] iArr);

    native void nContextInitToClient(int i);

    native void nContextDeinitToClient(int i);

    native int rsnContextCreateGL(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12, int i13, float f, int i14);

    native int rsnContextCreate(int i, int i2, int i3, int i4);

    native void rsnContextDestroy(int i);

    native void rsnContextSetSurface(int i, int i2, int i3, Surface surface);

    native void rsnContextSetSurfaceTexture(int i, int i2, int i3, SurfaceTexture surfaceTexture);

    native void rsnContextSetPriority(int i, int i2);

    native void rsnContextDump(int i, int i2);

    native void rsnContextFinish(int i);

    native void rsnContextSendMessage(int i, int i2, int[] iArr);

    native void rsnContextBindRootScript(int i, int i2);

    native void rsnContextBindSampler(int i, int i2, int i3);

    native void rsnContextBindProgramStore(int i, int i2);

    native void rsnContextBindProgramFragment(int i, int i2);

    native void rsnContextBindProgramVertex(int i, int i2);

    native void rsnContextBindProgramRaster(int i, int i2);

    native void rsnContextPause(int i);

    native void rsnContextResume(int i);

    native void rsnAssignName(int i, int i2, byte[] bArr);

    native String rsnGetName(int i, int i2);

    native void rsnObjDestroy(int i, int i2);

    native int rsnElementCreate(int i, int i2, int i3, boolean z, int i4);

    native int rsnElementCreate2(int i, int[] iArr, String[] strArr, int[] iArr2);

    native void rsnElementGetNativeData(int i, int i2, int[] iArr);

    native void rsnElementGetSubElements(int i, int i2, int[] iArr, String[] strArr, int[] iArr2);

    native int rsnTypeCreate(int i, int i2, int i3, int i4, int i5, boolean z, boolean z2, int i6);

    native void rsnTypeGetNativeData(int i, int i2, int[] iArr);

    native int rsnAllocationCreateTyped(int i, int i2, int i3, int i4, int i5);

    native int rsnAllocationCreateFromBitmap(int i, int i2, int i3, Bitmap bitmap, int i4);

    native int rsnAllocationCreateBitmapBackedAllocation(int i, int i2, int i3, Bitmap bitmap, int i4);

    native int rsnAllocationCubeCreateFromBitmap(int i, int i2, int i3, Bitmap bitmap, int i4);

    native int rsnAllocationCreateBitmapRef(int i, int i2, Bitmap bitmap);

    native int rsnAllocationCreateFromAssetStream(int i, int i2, int i3, int i4);

    native void rsnAllocationCopyToBitmap(int i, int i2, Bitmap bitmap);

    native void rsnAllocationSyncAll(int i, int i2, int i3);

    native Surface rsnAllocationGetSurface(int i, int i2);

    native void rsnAllocationSetSurface(int i, int i2, Surface surface);

    native void rsnAllocationIoSend(int i, int i2);

    native void rsnAllocationIoReceive(int i, int i2);

    native void rsnAllocationGenerateMipmaps(int i, int i2);

    native void rsnAllocationCopyFromBitmap(int i, int i2, Bitmap bitmap);

    native void rsnAllocationData1D(int i, int i2, int i3, int i4, int i5, int[] iArr, int i6);

    native void rsnAllocationData1D(int i, int i2, int i3, int i4, int i5, short[] sArr, int i6);

    native void rsnAllocationData1D(int i, int i2, int i3, int i4, int i5, byte[] bArr, int i6);

    native void rsnAllocationData1D(int i, int i2, int i3, int i4, int i5, float[] fArr, int i6);

    native void rsnAllocationElementData1D(int i, int i2, int i3, int i4, int i5, byte[] bArr, int i6);

    native void rsnAllocationData2D(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12, int i13);

    native void rsnAllocationData2D(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, byte[] bArr, int i9);

    native void rsnAllocationData2D(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, short[] sArr, int i9);

    native void rsnAllocationData2D(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int[] iArr, int i9);

    native void rsnAllocationData2D(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, float[] fArr, int i9);

    native void rsnAllocationData2D(int i, int i2, int i3, int i4, int i5, int i6, Bitmap bitmap);

    native void rsnAllocationData3D(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12, int i13, int i14);

    native void rsnAllocationData3D(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, byte[] bArr, int i10);

    native void rsnAllocationData3D(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, short[] sArr, int i10);

    native void rsnAllocationData3D(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int[] iArr, int i10);

    native void rsnAllocationData3D(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, float[] fArr, int i10);

    native void rsnAllocationRead(int i, int i2, byte[] bArr);

    native void rsnAllocationRead(int i, int i2, short[] sArr);

    native void rsnAllocationRead(int i, int i2, int[] iArr);

    native void rsnAllocationRead(int i, int i2, float[] fArr);

    native int rsnAllocationGetType(int i, int i2);

    native void rsnAllocationResize1D(int i, int i2, int i3);

    native int rsnFileA3DCreateFromAssetStream(int i, int i2);

    native int rsnFileA3DCreateFromFile(int i, String str);

    native int rsnFileA3DCreateFromAsset(int i, AssetManager assetManager, String str);

    native int rsnFileA3DGetNumIndexEntries(int i, int i2);

    native void rsnFileA3DGetIndexEntries(int i, int i2, int i3, int[] iArr, String[] strArr);

    native int rsnFileA3DGetEntryByIndex(int i, int i2, int i3);

    native int rsnFontCreateFromFile(int i, String str, float f, int i2);

    native int rsnFontCreateFromAssetStream(int i, String str, float f, int i2, int i3);

    native int rsnFontCreateFromAsset(int i, AssetManager assetManager, String str, float f, int i2);

    native void rsnScriptBindAllocation(int i, int i2, int i3, int i4);

    native void rsnScriptSetTimeZone(int i, int i2, byte[] bArr);

    native void rsnScriptInvoke(int i, int i2, int i3);

    native void rsnScriptForEach(int i, int i2, int i3, int i4, int i5, byte[] bArr);

    native void rsnScriptForEach(int i, int i2, int i3, int i4, int i5);

    native void rsnScriptForEachClipped(int i, int i2, int i3, int i4, int i5, byte[] bArr, int i6, int i7, int i8, int i9, int i10, int i11);

    native void rsnScriptForEachClipped(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11);

    native void rsnScriptInvokeV(int i, int i2, int i3, byte[] bArr);

    native void rsnScriptSetVarI(int i, int i2, int i3, int i4);

    native int rsnScriptGetVarI(int i, int i2, int i3);

    native void rsnScriptSetVarJ(int i, int i2, int i3, long j);

    native long rsnScriptGetVarJ(int i, int i2, int i3);

    native void rsnScriptSetVarF(int i, int i2, int i3, float f);

    native float rsnScriptGetVarF(int i, int i2, int i3);

    native void rsnScriptSetVarD(int i, int i2, int i3, double d);

    native double rsnScriptGetVarD(int i, int i2, int i3);

    native void rsnScriptSetVarV(int i, int i2, int i3, byte[] bArr);

    native void rsnScriptGetVarV(int i, int i2, int i3, byte[] bArr);

    native void rsnScriptSetVarVE(int i, int i2, int i3, byte[] bArr, int i4, int[] iArr);

    native void rsnScriptSetVarObj(int i, int i2, int i3, int i4);

    native int rsnScriptCCreate(int i, String str, String str2, byte[] bArr, int i2);

    native int rsnScriptIntrinsicCreate(int i, int i2, int i3);

    native int rsnScriptKernelIDCreate(int i, int i2, int i3, int i4);

    native int rsnScriptFieldIDCreate(int i, int i2, int i3);

    native int rsnScriptGroupCreate(int i, int[] iArr, int[] iArr2, int[] iArr3, int[] iArr4, int[] iArr5);

    native void rsnScriptGroupSetInput(int i, int i2, int i3, int i4);

    native void rsnScriptGroupSetOutput(int i, int i2, int i3, int i4);

    native void rsnScriptGroupExecute(int i, int i2);

    native int rsnSamplerCreate(int i, int i2, int i3, int i4, int i5, int i6, float f);

    native int rsnProgramStoreCreate(int i, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6, int i2, int i3, int i4);

    native int rsnProgramRasterCreate(int i, boolean z, int i2);

    native void rsnProgramBindConstants(int i, int i2, int i3, int i4);

    native void rsnProgramBindTexture(int i, int i2, int i3, int i4);

    native void rsnProgramBindSampler(int i, int i2, int i3, int i4);

    native int rsnProgramFragmentCreate(int i, String str, String[] strArr, int[] iArr);

    native int rsnProgramVertexCreate(int i, String str, String[] strArr, int[] iArr);

    native int rsnMeshCreate(int i, int[] iArr, int[] iArr2, int[] iArr3);

    native int rsnMeshGetVertexBufferCount(int i, int i2);

    native int rsnMeshGetIndexCount(int i, int i2);

    native void rsnMeshGetVertices(int i, int i2, int[] iArr, int i3);

    native void rsnMeshGetIndices(int i, int i2, int[] iArr, int[] iArr2, int i3);

    native int rsnPathCreate(int i, int i2, boolean z, int i3, int i4, float f);

    static {
        sInitialized = false;
        if (!SystemProperties.getBoolean("config.disable_renderscript", false)) {
            try {
                Class<?> vm_runtime = Class.forName("dalvik.system.VMRuntime");
                Method get_runtime = vm_runtime.getDeclaredMethod("getRuntime", new Class[0]);
                sRuntime = get_runtime.invoke(null, new Object[0]);
                registerNativeAllocation = vm_runtime.getDeclaredMethod("registerNativeAllocation", Integer.TYPE);
                registerNativeFree = vm_runtime.getDeclaredMethod("registerNativeFree", Integer.TYPE);
                try {
                    System.loadLibrary("rs_jni");
                    _nInit();
                    sInitialized = true;
                } catch (UnsatisfiedLinkError e) {
                    Log.e(LOG_TAG, "Error loading RS jni library: " + e);
                    throw new RSRuntimeException("Error loading RS jni library: " + e);
                }
            } catch (Exception e2) {
                Log.e(LOG_TAG, "Error loading GC methods: " + e2);
                throw new RSRuntimeException("Error loading GC methods: " + e2);
            }
        }
    }

    public static void setupDiskCache(File cacheDir) {
        if (!sInitialized) {
            Log.e(LOG_TAG, "RenderScript.setupDiskCache() called when disabled");
        } else {
            mCacheDir = cacheDir;
        }
    }

    /* loaded from: RenderScript$ContextType.class */
    public enum ContextType {
        NORMAL(0),
        DEBUG(1),
        PROFILE(2);
        
        int mID;

        ContextType(int id) {
            this.mID = id;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized int nContextCreateGL(int dev, int ver, int sdkVer, int colorMin, int colorPref, int alphaMin, int alphaPref, int depthMin, int depthPref, int stencilMin, int stencilPref, int samplesMin, int samplesPref, float samplesQ, int dpi) {
        return rsnContextCreateGL(dev, ver, sdkVer, colorMin, colorPref, alphaMin, alphaPref, depthMin, depthPref, stencilMin, stencilPref, samplesMin, samplesPref, samplesQ, dpi);
    }

    synchronized int nContextCreate(int dev, int ver, int sdkVer, int contextType) {
        return rsnContextCreate(dev, ver, sdkVer, contextType);
    }

    synchronized void nContextDestroy() {
        validate();
        rsnContextDestroy(this.mContext);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nContextSetSurface(int w, int h, Surface sur) {
        validate();
        rsnContextSetSurface(this.mContext, w, h, sur);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nContextSetSurfaceTexture(int w, int h, SurfaceTexture sur) {
        validate();
        rsnContextSetSurfaceTexture(this.mContext, w, h, sur);
    }

    synchronized void nContextSetPriority(int p) {
        validate();
        rsnContextSetPriority(this.mContext, p);
    }

    synchronized void nContextDump(int bits) {
        validate();
        rsnContextDump(this.mContext, bits);
    }

    synchronized void nContextFinish() {
        validate();
        rsnContextFinish(this.mContext);
    }

    synchronized void nContextSendMessage(int id, int[] data) {
        validate();
        rsnContextSendMessage(this.mContext, id, data);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nContextBindRootScript(int script) {
        validate();
        rsnContextBindRootScript(this.mContext, script);
    }

    synchronized void nContextBindSampler(int sampler, int slot) {
        validate();
        rsnContextBindSampler(this.mContext, sampler, slot);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nContextBindProgramStore(int pfs) {
        validate();
        rsnContextBindProgramStore(this.mContext, pfs);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nContextBindProgramFragment(int pf) {
        validate();
        rsnContextBindProgramFragment(this.mContext, pf);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nContextBindProgramVertex(int pv) {
        validate();
        rsnContextBindProgramVertex(this.mContext, pv);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nContextBindProgramRaster(int pr) {
        validate();
        rsnContextBindProgramRaster(this.mContext, pr);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nContextPause() {
        validate();
        rsnContextPause(this.mContext);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nContextResume() {
        validate();
        rsnContextResume(this.mContext);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nAssignName(int obj, byte[] name) {
        validate();
        rsnAssignName(this.mContext, obj, name);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized String nGetName(int obj) {
        validate();
        return rsnGetName(this.mContext, obj);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nObjDestroy(int id) {
        if (this.mContext != 0) {
            rsnObjDestroy(this.mContext, id);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized int nElementCreate(int type, int kind, boolean norm, int vecSize) {
        validate();
        return rsnElementCreate(this.mContext, type, kind, norm, vecSize);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized int nElementCreate2(int[] elements, String[] names, int[] arraySizes) {
        validate();
        return rsnElementCreate2(this.mContext, elements, names, arraySizes);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nElementGetNativeData(int id, int[] elementData) {
        validate();
        rsnElementGetNativeData(this.mContext, id, elementData);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nElementGetSubElements(int id, int[] IDs, String[] names, int[] arraySizes) {
        validate();
        rsnElementGetSubElements(this.mContext, id, IDs, names, arraySizes);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized int nTypeCreate(int eid, int x, int y, int z, boolean mips, boolean faces, int yuv) {
        validate();
        return rsnTypeCreate(this.mContext, eid, x, y, z, mips, faces, yuv);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nTypeGetNativeData(int id, int[] typeData) {
        validate();
        rsnTypeGetNativeData(this.mContext, id, typeData);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized int nAllocationCreateTyped(int type, int mip, int usage, int pointer) {
        validate();
        return rsnAllocationCreateTyped(this.mContext, type, mip, usage, pointer);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized int nAllocationCreateFromBitmap(int type, int mip, Bitmap bmp, int usage) {
        validate();
        return rsnAllocationCreateFromBitmap(this.mContext, type, mip, bmp, usage);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized int nAllocationCreateBitmapBackedAllocation(int type, int mip, Bitmap bmp, int usage) {
        validate();
        return rsnAllocationCreateBitmapBackedAllocation(this.mContext, type, mip, bmp, usage);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized int nAllocationCubeCreateFromBitmap(int type, int mip, Bitmap bmp, int usage) {
        validate();
        return rsnAllocationCubeCreateFromBitmap(this.mContext, type, mip, bmp, usage);
    }

    synchronized int nAllocationCreateBitmapRef(int type, Bitmap bmp) {
        validate();
        return rsnAllocationCreateBitmapRef(this.mContext, type, bmp);
    }

    synchronized int nAllocationCreateFromAssetStream(int mips, int assetStream, int usage) {
        validate();
        return rsnAllocationCreateFromAssetStream(this.mContext, mips, assetStream, usage);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nAllocationCopyToBitmap(int alloc, Bitmap bmp) {
        validate();
        rsnAllocationCopyToBitmap(this.mContext, alloc, bmp);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nAllocationSyncAll(int alloc, int src) {
        validate();
        rsnAllocationSyncAll(this.mContext, alloc, src);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized Surface nAllocationGetSurface(int alloc) {
        validate();
        return rsnAllocationGetSurface(this.mContext, alloc);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nAllocationSetSurface(int alloc, Surface sur) {
        validate();
        rsnAllocationSetSurface(this.mContext, alloc, sur);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nAllocationIoSend(int alloc) {
        validate();
        rsnAllocationIoSend(this.mContext, alloc);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nAllocationIoReceive(int alloc) {
        validate();
        rsnAllocationIoReceive(this.mContext, alloc);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nAllocationGenerateMipmaps(int alloc) {
        validate();
        rsnAllocationGenerateMipmaps(this.mContext, alloc);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nAllocationCopyFromBitmap(int alloc, Bitmap bmp) {
        validate();
        rsnAllocationCopyFromBitmap(this.mContext, alloc, bmp);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nAllocationData1D(int id, int off, int mip, int count, int[] d, int sizeBytes) {
        validate();
        rsnAllocationData1D(this.mContext, id, off, mip, count, d, sizeBytes);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nAllocationData1D(int id, int off, int mip, int count, short[] d, int sizeBytes) {
        validate();
        rsnAllocationData1D(this.mContext, id, off, mip, count, d, sizeBytes);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nAllocationData1D(int id, int off, int mip, int count, byte[] d, int sizeBytes) {
        validate();
        rsnAllocationData1D(this.mContext, id, off, mip, count, d, sizeBytes);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nAllocationData1D(int id, int off, int mip, int count, float[] d, int sizeBytes) {
        validate();
        rsnAllocationData1D(this.mContext, id, off, mip, count, d, sizeBytes);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nAllocationElementData1D(int id, int xoff, int mip, int compIdx, byte[] d, int sizeBytes) {
        validate();
        rsnAllocationElementData1D(this.mContext, id, xoff, mip, compIdx, d, sizeBytes);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nAllocationData2D(int dstAlloc, int dstXoff, int dstYoff, int dstMip, int dstFace, int width, int height, int srcAlloc, int srcXoff, int srcYoff, int srcMip, int srcFace) {
        validate();
        rsnAllocationData2D(this.mContext, dstAlloc, dstXoff, dstYoff, dstMip, dstFace, width, height, srcAlloc, srcXoff, srcYoff, srcMip, srcFace);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nAllocationData2D(int id, int xoff, int yoff, int mip, int face, int w, int h, byte[] d, int sizeBytes) {
        validate();
        rsnAllocationData2D(this.mContext, id, xoff, yoff, mip, face, w, h, d, sizeBytes);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nAllocationData2D(int id, int xoff, int yoff, int mip, int face, int w, int h, short[] d, int sizeBytes) {
        validate();
        rsnAllocationData2D(this.mContext, id, xoff, yoff, mip, face, w, h, d, sizeBytes);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nAllocationData2D(int id, int xoff, int yoff, int mip, int face, int w, int h, int[] d, int sizeBytes) {
        validate();
        rsnAllocationData2D(this.mContext, id, xoff, yoff, mip, face, w, h, d, sizeBytes);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nAllocationData2D(int id, int xoff, int yoff, int mip, int face, int w, int h, float[] d, int sizeBytes) {
        validate();
        rsnAllocationData2D(this.mContext, id, xoff, yoff, mip, face, w, h, d, sizeBytes);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nAllocationData2D(int id, int xoff, int yoff, int mip, int face, Bitmap b) {
        validate();
        rsnAllocationData2D(this.mContext, id, xoff, yoff, mip, face, b);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nAllocationData3D(int dstAlloc, int dstXoff, int dstYoff, int dstZoff, int dstMip, int width, int height, int depth, int srcAlloc, int srcXoff, int srcYoff, int srcZoff, int srcMip) {
        validate();
        rsnAllocationData3D(this.mContext, dstAlloc, dstXoff, dstYoff, dstZoff, dstMip, width, height, depth, srcAlloc, srcXoff, srcYoff, srcZoff, srcMip);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nAllocationData3D(int id, int xoff, int yoff, int zoff, int mip, int w, int h, int depth, byte[] d, int sizeBytes) {
        validate();
        rsnAllocationData3D(this.mContext, id, xoff, yoff, zoff, mip, w, h, depth, d, sizeBytes);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nAllocationData3D(int id, int xoff, int yoff, int zoff, int mip, int w, int h, int depth, short[] d, int sizeBytes) {
        validate();
        rsnAllocationData3D(this.mContext, id, xoff, yoff, zoff, mip, w, h, depth, d, sizeBytes);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nAllocationData3D(int id, int xoff, int yoff, int zoff, int mip, int w, int h, int depth, int[] d, int sizeBytes) {
        validate();
        rsnAllocationData3D(this.mContext, id, xoff, yoff, zoff, mip, w, h, depth, d, sizeBytes);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nAllocationData3D(int id, int xoff, int yoff, int zoff, int mip, int w, int h, int depth, float[] d, int sizeBytes) {
        validate();
        rsnAllocationData3D(this.mContext, id, xoff, yoff, zoff, mip, w, h, depth, d, sizeBytes);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nAllocationRead(int id, byte[] d) {
        validate();
        rsnAllocationRead(this.mContext, id, d);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nAllocationRead(int id, short[] d) {
        validate();
        rsnAllocationRead(this.mContext, id, d);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nAllocationRead(int id, int[] d) {
        validate();
        rsnAllocationRead(this.mContext, id, d);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nAllocationRead(int id, float[] d) {
        validate();
        rsnAllocationRead(this.mContext, id, d);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized int nAllocationGetType(int id) {
        validate();
        return rsnAllocationGetType(this.mContext, id);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nAllocationResize1D(int id, int dimX) {
        validate();
        rsnAllocationResize1D(this.mContext, id, dimX);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized int nFileA3DCreateFromAssetStream(int assetStream) {
        validate();
        return rsnFileA3DCreateFromAssetStream(this.mContext, assetStream);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized int nFileA3DCreateFromFile(String path) {
        validate();
        return rsnFileA3DCreateFromFile(this.mContext, path);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized int nFileA3DCreateFromAsset(AssetManager mgr, String path) {
        validate();
        return rsnFileA3DCreateFromAsset(this.mContext, mgr, path);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized int nFileA3DGetNumIndexEntries(int fileA3D) {
        validate();
        return rsnFileA3DGetNumIndexEntries(this.mContext, fileA3D);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nFileA3DGetIndexEntries(int fileA3D, int numEntries, int[] IDs, String[] names) {
        validate();
        rsnFileA3DGetIndexEntries(this.mContext, fileA3D, numEntries, IDs, names);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized int nFileA3DGetEntryByIndex(int fileA3D, int index) {
        validate();
        return rsnFileA3DGetEntryByIndex(this.mContext, fileA3D, index);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized int nFontCreateFromFile(String fileName, float size, int dpi) {
        validate();
        return rsnFontCreateFromFile(this.mContext, fileName, size, dpi);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized int nFontCreateFromAssetStream(String name, float size, int dpi, int assetStream) {
        validate();
        return rsnFontCreateFromAssetStream(this.mContext, name, size, dpi, assetStream);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized int nFontCreateFromAsset(AssetManager mgr, String path, float size, int dpi) {
        validate();
        return rsnFontCreateFromAsset(this.mContext, mgr, path, size, dpi);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nScriptBindAllocation(int script, int alloc, int slot) {
        validate();
        rsnScriptBindAllocation(this.mContext, script, alloc, slot);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nScriptSetTimeZone(int script, byte[] timeZone) {
        validate();
        rsnScriptSetTimeZone(this.mContext, script, timeZone);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nScriptInvoke(int id, int slot) {
        validate();
        rsnScriptInvoke(this.mContext, id, slot);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nScriptForEach(int id, int slot, int ain, int aout, byte[] params) {
        validate();
        if (params == null) {
            rsnScriptForEach(this.mContext, id, slot, ain, aout);
        } else {
            rsnScriptForEach(this.mContext, id, slot, ain, aout, params);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nScriptForEachClipped(int id, int slot, int ain, int aout, byte[] params, int xstart, int xend, int ystart, int yend, int zstart, int zend) {
        validate();
        if (params == null) {
            rsnScriptForEachClipped(this.mContext, id, slot, ain, aout, xstart, xend, ystart, yend, zstart, zend);
        } else {
            rsnScriptForEachClipped(this.mContext, id, slot, ain, aout, params, xstart, xend, ystart, yend, zstart, zend);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nScriptInvokeV(int id, int slot, byte[] params) {
        validate();
        rsnScriptInvokeV(this.mContext, id, slot, params);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nScriptSetVarI(int id, int slot, int val) {
        validate();
        rsnScriptSetVarI(this.mContext, id, slot, val);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized int nScriptGetVarI(int id, int slot) {
        validate();
        return rsnScriptGetVarI(this.mContext, id, slot);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nScriptSetVarJ(int id, int slot, long val) {
        validate();
        rsnScriptSetVarJ(this.mContext, id, slot, val);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized long nScriptGetVarJ(int id, int slot) {
        validate();
        return rsnScriptGetVarJ(this.mContext, id, slot);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nScriptSetVarF(int id, int slot, float val) {
        validate();
        rsnScriptSetVarF(this.mContext, id, slot, val);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized float nScriptGetVarF(int id, int slot) {
        validate();
        return rsnScriptGetVarF(this.mContext, id, slot);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nScriptSetVarD(int id, int slot, double val) {
        validate();
        rsnScriptSetVarD(this.mContext, id, slot, val);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized double nScriptGetVarD(int id, int slot) {
        validate();
        return rsnScriptGetVarD(this.mContext, id, slot);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nScriptSetVarV(int id, int slot, byte[] val) {
        validate();
        rsnScriptSetVarV(this.mContext, id, slot, val);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nScriptGetVarV(int id, int slot, byte[] val) {
        validate();
        rsnScriptGetVarV(this.mContext, id, slot, val);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nScriptSetVarVE(int id, int slot, byte[] val, int e, int[] dims) {
        validate();
        rsnScriptSetVarVE(this.mContext, id, slot, val, e, dims);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nScriptSetVarObj(int id, int slot, int val) {
        validate();
        rsnScriptSetVarObj(this.mContext, id, slot, val);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized int nScriptCCreate(String resName, String cacheDir, byte[] script, int length) {
        validate();
        return rsnScriptCCreate(this.mContext, resName, cacheDir, script, length);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized int nScriptIntrinsicCreate(int id, int eid) {
        validate();
        return rsnScriptIntrinsicCreate(this.mContext, id, eid);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized int nScriptKernelIDCreate(int sid, int slot, int sig) {
        validate();
        return rsnScriptKernelIDCreate(this.mContext, sid, slot, sig);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized int nScriptFieldIDCreate(int sid, int slot) {
        validate();
        return rsnScriptFieldIDCreate(this.mContext, sid, slot);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized int nScriptGroupCreate(int[] kernels, int[] src, int[] dstk, int[] dstf, int[] types) {
        validate();
        return rsnScriptGroupCreate(this.mContext, kernels, src, dstk, dstf, types);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nScriptGroupSetInput(int group, int kernel, int alloc) {
        validate();
        rsnScriptGroupSetInput(this.mContext, group, kernel, alloc);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nScriptGroupSetOutput(int group, int kernel, int alloc) {
        validate();
        rsnScriptGroupSetOutput(this.mContext, group, kernel, alloc);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nScriptGroupExecute(int group) {
        validate();
        rsnScriptGroupExecute(this.mContext, group);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized int nSamplerCreate(int magFilter, int minFilter, int wrapS, int wrapT, int wrapR, float aniso) {
        validate();
        return rsnSamplerCreate(this.mContext, magFilter, minFilter, wrapS, wrapT, wrapR, aniso);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized int nProgramStoreCreate(boolean r, boolean g, boolean b, boolean a, boolean depthMask, boolean dither, int srcMode, int dstMode, int depthFunc) {
        validate();
        return rsnProgramStoreCreate(this.mContext, r, g, b, a, depthMask, dither, srcMode, dstMode, depthFunc);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized int nProgramRasterCreate(boolean pointSprite, int cullMode) {
        validate();
        return rsnProgramRasterCreate(this.mContext, pointSprite, cullMode);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nProgramBindConstants(int pv, int slot, int mID) {
        validate();
        rsnProgramBindConstants(this.mContext, pv, slot, mID);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nProgramBindTexture(int vpf, int slot, int a) {
        validate();
        rsnProgramBindTexture(this.mContext, vpf, slot, a);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nProgramBindSampler(int vpf, int slot, int s) {
        validate();
        rsnProgramBindSampler(this.mContext, vpf, slot, s);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized int nProgramFragmentCreate(String shader, String[] texNames, int[] params) {
        validate();
        return rsnProgramFragmentCreate(this.mContext, shader, texNames, params);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized int nProgramVertexCreate(String shader, String[] texNames, int[] params) {
        validate();
        return rsnProgramVertexCreate(this.mContext, shader, texNames, params);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized int nMeshCreate(int[] vtx, int[] idx, int[] prim) {
        validate();
        return rsnMeshCreate(this.mContext, vtx, idx, prim);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized int nMeshGetVertexBufferCount(int id) {
        validate();
        return rsnMeshGetVertexBufferCount(this.mContext, id);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized int nMeshGetIndexCount(int id) {
        validate();
        return rsnMeshGetIndexCount(this.mContext, id);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nMeshGetVertices(int id, int[] vtxIds, int vtxIdCount) {
        validate();
        rsnMeshGetVertices(this.mContext, id, vtxIds, vtxIdCount);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void nMeshGetIndices(int id, int[] idxIds, int[] primitives, int vtxIdCount) {
        validate();
        rsnMeshGetIndices(this.mContext, id, idxIds, primitives, vtxIdCount);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized int nPathCreate(int prim, boolean isStatic, int vtx, int loop, float q) {
        validate();
        return rsnPathCreate(this.mContext, prim, isStatic, vtx, loop, q);
    }

    /* loaded from: RenderScript$RSMessageHandler.class */
    public static class RSMessageHandler implements Runnable {
        protected int[] mData;
        protected int mID;
        protected int mLength;

        @Override // java.lang.Runnable
        public void run() {
        }
    }

    public void setMessageHandler(RSMessageHandler msg) {
        this.mMessageCallback = msg;
    }

    public RSMessageHandler getMessageHandler() {
        return this.mMessageCallback;
    }

    public void sendMessage(int id, int[] data) {
        nContextSendMessage(id, data);
    }

    /* loaded from: RenderScript$RSErrorHandler.class */
    public static class RSErrorHandler implements Runnable {
        protected String mErrorMessage;
        protected int mErrorNum;

        @Override // java.lang.Runnable
        public void run() {
        }
    }

    public void setErrorHandler(RSErrorHandler msg) {
        this.mErrorCallback = msg;
    }

    public RSErrorHandler getErrorHandler() {
        return this.mErrorCallback;
    }

    /* loaded from: RenderScript$Priority.class */
    public enum Priority {
        LOW(15),
        NORMAL(-4);
        
        int mID;

        Priority(int id) {
            this.mID = id;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void validate() {
        if (this.mContext == 0) {
            throw new RSInvalidStateException("Calling RS with no Context active.");
        }
    }

    public void setPriority(Priority p) {
        validate();
        nContextSetPriority(p.mID);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: RenderScript$MessageThread.class */
    public static class MessageThread extends Thread {
        RenderScript mRS;
        boolean mRun;
        int[] mAuxData;
        static final int RS_MESSAGE_TO_CLIENT_NONE = 0;
        static final int RS_MESSAGE_TO_CLIENT_EXCEPTION = 1;
        static final int RS_MESSAGE_TO_CLIENT_RESIZE = 2;
        static final int RS_MESSAGE_TO_CLIENT_ERROR = 3;
        static final int RS_MESSAGE_TO_CLIENT_USER = 4;
        static final int RS_MESSAGE_TO_CLIENT_NEW_BUFFER = 5;
        static final int RS_ERROR_FATAL_DEBUG = 2048;
        static final int RS_ERROR_FATAL_UNKNOWN = 4096;

        /* JADX INFO: Access modifiers changed from: package-private */
        public MessageThread(RenderScript rs) {
            super("RSMessageThread");
            this.mRun = true;
            this.mAuxData = new int[2];
            this.mRS = rs;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            int[] rbuf = new int[16];
            this.mRS.nContextInitToClient(this.mRS.mContext);
            while (this.mRun) {
                rbuf[0] = 0;
                int msg = this.mRS.nContextPeekMessage(this.mRS.mContext, this.mAuxData);
                int size = this.mAuxData[1];
                int subID = this.mAuxData[0];
                if (msg == 4) {
                    if ((size >> 2) >= rbuf.length) {
                        rbuf = new int[(size + 3) >> 2];
                    }
                    if (this.mRS.nContextGetUserMessage(this.mRS.mContext, rbuf) != 4) {
                        throw new RSDriverException("Error processing message from RenderScript.");
                    }
                    if (this.mRS.mMessageCallback != null) {
                        this.mRS.mMessageCallback.mData = rbuf;
                        this.mRS.mMessageCallback.mID = subID;
                        this.mRS.mMessageCallback.mLength = size;
                        this.mRS.mMessageCallback.run();
                    } else {
                        throw new RSInvalidStateException("Received a message from the script with no message handler installed.");
                    }
                } else if (msg == 3) {
                    String e = this.mRS.nContextGetErrorMessage(this.mRS.mContext);
                    if (subID >= 4096 || (subID >= 2048 && (this.mRS.mContextType != ContextType.DEBUG || this.mRS.mErrorCallback == null))) {
                        throw new RSRuntimeException("Fatal error " + subID + ", details: " + e);
                    }
                    if (this.mRS.mErrorCallback != null) {
                        this.mRS.mErrorCallback.mErrorMessage = e;
                        this.mRS.mErrorCallback.mErrorNum = subID;
                        this.mRS.mErrorCallback.run();
                    } else {
                        Log.e(RenderScript.LOG_TAG, "non fatal RS error, " + e);
                    }
                } else if (msg == 5) {
                    Allocation.sendBufferNotification(subID);
                } else {
                    try {
                        sleep(1L, 0);
                    } catch (InterruptedException e2) {
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RenderScript(Context ctx) {
        if (ctx != null) {
            this.mApplicationContext = ctx.getApplicationContext();
        }
    }

    public final Context getApplicationContext() {
        return this.mApplicationContext;
    }

    public static RenderScript create(Context ctx, int sdkVersion) {
        return create(ctx, sdkVersion, ContextType.NORMAL);
    }

    public static RenderScript create(Context ctx, int sdkVersion, ContextType ct) {
        if (!sInitialized) {
            Log.e(LOG_TAG, "RenderScript.create() called when disabled; someone is likely to crash");
            return null;
        }
        RenderScript rs = new RenderScript(ctx);
        rs.mDev = rs.nDeviceCreate();
        rs.mContext = rs.nContextCreate(rs.mDev, 0, sdkVersion, ct.mID);
        rs.mContextType = ct;
        if (rs.mContext == 0) {
            throw new RSDriverException("Failed to create RS context.");
        }
        rs.mMessageThread = new MessageThread(rs);
        rs.mMessageThread.start();
        return rs;
    }

    public static RenderScript create(Context ctx) {
        return create(ctx, ContextType.NORMAL);
    }

    public static RenderScript create(Context ctx, ContextType ct) {
        int v = ctx.getApplicationInfo().targetSdkVersion;
        return create(ctx, v, ct);
    }

    public void contextDump() {
        validate();
        nContextDump(0);
    }

    public void finish() {
        nContextFinish();
    }

    public void destroy() {
        validate();
        nContextDeinitToClient(this.mContext);
        this.mMessageThread.mRun = false;
        try {
            this.mMessageThread.join();
        } catch (InterruptedException e) {
        }
        nContextDestroy();
        this.mContext = 0;
        nDeviceDestroy(this.mDev);
        this.mDev = 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isAlive() {
        return this.mContext != 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int safeID(BaseObj o) {
        if (o != null) {
            return o.getID(this);
        }
        return 0;
    }
}