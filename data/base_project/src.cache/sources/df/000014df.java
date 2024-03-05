package android.view;

import android.graphics.Rect;
import android.util.Pools;

/* loaded from: GLES20RecordingCanvas.class */
class GLES20RecordingCanvas extends GLES20Canvas {
    private static final int POOL_LIMIT = 25;
    private static final Pools.SynchronizedPool<GLES20RecordingCanvas> sPool = new Pools.SynchronizedPool<>(25);
    private GLES20DisplayList mDisplayList;

    private GLES20RecordingCanvas() {
        super(true, true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static GLES20RecordingCanvas obtain(GLES20DisplayList displayList) {
        GLES20RecordingCanvas canvas = sPool.acquire();
        if (canvas == null) {
            canvas = new GLES20RecordingCanvas();
        }
        canvas.mDisplayList = displayList;
        return canvas;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void recycle() {
        this.mDisplayList = null;
        resetDisplayListRenderer();
        sPool.release(this);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void start() {
        this.mDisplayList.clearReferences();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int end(int nativeDisplayList) {
        return getDisplayList(nativeDisplayList);
    }

    @Override // android.view.GLES20Canvas, android.view.HardwareCanvas
    public int drawDisplayList(DisplayList displayList, Rect dirty, int flags) {
        int status = super.drawDisplayList(displayList, dirty, flags);
        this.mDisplayList.getChildDisplayLists().add(displayList);
        return status;
    }
}