package android.filterpacks.videosrc;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.GLFrame;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.core.GenerateFinalPort;
import android.filterfw.core.MutableFrameFormat;
import android.filterfw.core.ShaderProgram;
import android.filterfw.format.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.Matrix;
import android.util.Log;
import gov.nist.core.Separators;
import java.io.IOException;
import java.util.List;

/* loaded from: CameraSource.class */
public class CameraSource extends Filter {
    @GenerateFieldPort(name = "id", hasDefault = true)
    private int mCameraId;
    @GenerateFieldPort(name = "width", hasDefault = true)
    private int mWidth;
    @GenerateFieldPort(name = "height", hasDefault = true)
    private int mHeight;
    @GenerateFieldPort(name = "framerate", hasDefault = true)
    private int mFps;
    @GenerateFinalPort(name = "waitForNewFrame", hasDefault = true)
    private boolean mWaitForNewFrame;
    private Camera mCamera;
    private GLFrame mCameraFrame;
    private SurfaceTexture mSurfaceTexture;
    private ShaderProgram mFrameExtractor;
    private MutableFrameFormat mOutputFormat;
    private float[] mCameraTransform;
    private float[] mMappedCoords;
    private static final float[] mSourceCoords = {0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f};
    private static final int NEWFRAME_TIMEOUT = 100;
    private static final int NEWFRAME_TIMEOUT_REPEAT = 10;
    private boolean mNewFrameAvailable;
    private Camera.Parameters mCameraParameters;
    private static final String mFrameShader = "#extension GL_OES_EGL_image_external : require\nprecision mediump float;\nuniform samplerExternalOES tex_sampler_0;\nvarying vec2 v_texcoord;\nvoid main() {\n  gl_FragColor = texture2D(tex_sampler_0, v_texcoord);\n}\n";
    private final boolean mLogVerbose;
    private static final String TAG = "CameraSource";
    private SurfaceTexture.OnFrameAvailableListener onCameraFrameAvailableListener;

    public CameraSource(String name) {
        super(name);
        this.mCameraId = 0;
        this.mWidth = 320;
        this.mHeight = 240;
        this.mFps = 30;
        this.mWaitForNewFrame = true;
        this.onCameraFrameAvailableListener = new SurfaceTexture.OnFrameAvailableListener() { // from class: android.filterpacks.videosrc.CameraSource.1
            @Override // android.graphics.SurfaceTexture.OnFrameAvailableListener
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                if (CameraSource.this.mLogVerbose) {
                    Log.v(CameraSource.TAG, "New frame from camera");
                }
                synchronized (CameraSource.this) {
                    CameraSource.this.mNewFrameAvailable = true;
                    CameraSource.this.notify();
                }
            }
        };
        this.mCameraTransform = new float[16];
        this.mMappedCoords = new float[16];
        this.mLogVerbose = Log.isLoggable(TAG, 2);
    }

    @Override // android.filterfw.core.Filter
    public void setupPorts() {
        addOutputPort("video", ImageFormat.create(3, 3));
    }

    private void createFormats() {
        this.mOutputFormat = ImageFormat.create(this.mWidth, this.mHeight, 3, 3);
    }

    @Override // android.filterfw.core.Filter
    public void prepare(FilterContext context) {
        if (this.mLogVerbose) {
            Log.v(TAG, "Preparing");
        }
        this.mFrameExtractor = new ShaderProgram(context, mFrameShader);
    }

    @Override // android.filterfw.core.Filter
    public void open(FilterContext context) {
        if (this.mLogVerbose) {
            Log.v(TAG, "Opening");
        }
        this.mCamera = Camera.open(this.mCameraId);
        getCameraParameters();
        this.mCamera.setParameters(this.mCameraParameters);
        createFormats();
        this.mCameraFrame = (GLFrame) context.getFrameManager().newBoundFrame(this.mOutputFormat, 104, 0L);
        this.mSurfaceTexture = new SurfaceTexture(this.mCameraFrame.getTextureId());
        try {
            this.mCamera.setPreviewTexture(this.mSurfaceTexture);
            this.mSurfaceTexture.setOnFrameAvailableListener(this.onCameraFrameAvailableListener);
            this.mNewFrameAvailable = false;
            this.mCamera.startPreview();
        } catch (IOException e) {
            throw new RuntimeException("Could not bind camera surface texture: " + e.getMessage() + "!");
        }
    }

    @Override // android.filterfw.core.Filter
    public void process(FilterContext context) {
        if (this.mLogVerbose) {
            Log.v(TAG, "Processing new frame");
        }
        if (this.mWaitForNewFrame) {
            while (!this.mNewFrameAvailable) {
                if (0 == 10) {
                    throw new RuntimeException("Timeout waiting for new frame");
                }
                try {
                    wait(100L);
                } catch (InterruptedException e) {
                    if (this.mLogVerbose) {
                        Log.v(TAG, "Interrupted while waiting for new frame");
                    }
                }
            }
            this.mNewFrameAvailable = false;
            if (this.mLogVerbose) {
                Log.v(TAG, "Got new frame");
            }
        }
        this.mSurfaceTexture.updateTexImage();
        if (this.mLogVerbose) {
            Log.v(TAG, "Using frame extractor in thread: " + Thread.currentThread());
        }
        this.mSurfaceTexture.getTransformMatrix(this.mCameraTransform);
        Matrix.multiplyMM(this.mMappedCoords, 0, this.mCameraTransform, 0, mSourceCoords, 0);
        this.mFrameExtractor.setSourceRegion(this.mMappedCoords[0], this.mMappedCoords[1], this.mMappedCoords[4], this.mMappedCoords[5], this.mMappedCoords[8], this.mMappedCoords[9], this.mMappedCoords[12], this.mMappedCoords[13]);
        Frame output = context.getFrameManager().newFrame(this.mOutputFormat);
        this.mFrameExtractor.process(this.mCameraFrame, output);
        long timestamp = this.mSurfaceTexture.getTimestamp();
        if (this.mLogVerbose) {
            Log.v(TAG, "Timestamp: " + (timestamp / 1.0E9d) + " s");
        }
        output.setTimestamp(timestamp);
        pushOutput("video", output);
        output.release();
        if (this.mLogVerbose) {
            Log.v(TAG, "Done processing new frame");
        }
    }

    @Override // android.filterfw.core.Filter
    public void close(FilterContext context) {
        if (this.mLogVerbose) {
            Log.v(TAG, "Closing");
        }
        this.mCamera.release();
        this.mCamera = null;
        this.mSurfaceTexture.release();
        this.mSurfaceTexture = null;
    }

    @Override // android.filterfw.core.Filter
    public void tearDown(FilterContext context) {
        if (this.mCameraFrame != null) {
            this.mCameraFrame.release();
        }
    }

    @Override // android.filterfw.core.Filter
    public void fieldPortValueUpdated(String name, FilterContext context) {
        if (name.equals("framerate")) {
            getCameraParameters();
            int[] closestRange = findClosestFpsRange(this.mFps, this.mCameraParameters);
            this.mCameraParameters.setPreviewFpsRange(closestRange[0], closestRange[1]);
            this.mCamera.setParameters(this.mCameraParameters);
        }
    }

    public synchronized Camera.Parameters getCameraParameters() {
        boolean closeCamera = false;
        if (this.mCameraParameters == null) {
            if (this.mCamera == null) {
                this.mCamera = Camera.open(this.mCameraId);
                closeCamera = true;
            }
            this.mCameraParameters = this.mCamera.getParameters();
            if (closeCamera) {
                this.mCamera.release();
                this.mCamera = null;
            }
        }
        int[] closestSize = findClosestSize(this.mWidth, this.mHeight, this.mCameraParameters);
        this.mWidth = closestSize[0];
        this.mHeight = closestSize[1];
        this.mCameraParameters.setPreviewSize(this.mWidth, this.mHeight);
        int[] closestRange = findClosestFpsRange(this.mFps, this.mCameraParameters);
        this.mCameraParameters.setPreviewFpsRange(closestRange[0], closestRange[1]);
        return this.mCameraParameters;
    }

    public synchronized void setCameraParameters(Camera.Parameters params) {
        params.setPreviewSize(this.mWidth, this.mHeight);
        this.mCameraParameters = params;
        if (isOpen()) {
            this.mCamera.setParameters(this.mCameraParameters);
        }
    }

    private int[] findClosestSize(int width, int height, Camera.Parameters parameters) {
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        int closestWidth = -1;
        int closestHeight = -1;
        int smallestWidth = previewSizes.get(0).width;
        int smallestHeight = previewSizes.get(0).height;
        for (Camera.Size size : previewSizes) {
            if (size.width <= width && size.height <= height && size.width >= closestWidth && size.height >= closestHeight) {
                closestWidth = size.width;
                closestHeight = size.height;
            }
            if (size.width < smallestWidth && size.height < smallestHeight) {
                smallestWidth = size.width;
                smallestHeight = size.height;
            }
        }
        if (closestWidth == -1) {
            closestWidth = smallestWidth;
            closestHeight = smallestHeight;
        }
        if (this.mLogVerbose) {
            Log.v(TAG, "Requested resolution: (" + width + ", " + height + "). Closest match: (" + closestWidth + ", " + closestHeight + ").");
        }
        int[] closestSize = {closestWidth, closestHeight};
        return closestSize;
    }

    private int[] findClosestFpsRange(int fps, Camera.Parameters params) {
        List<int[]> supportedFpsRanges = params.getSupportedPreviewFpsRange();
        int[] closestRange = supportedFpsRanges.get(0);
        for (int[] range : supportedFpsRanges) {
            if (range[0] < fps * 1000 && range[1] > fps * 1000 && range[0] > closestRange[0] && range[1] < closestRange[1]) {
                closestRange = range;
            }
        }
        if (this.mLogVerbose) {
            Log.v(TAG, "Requested fps: " + fps + ".Closest frame rate range: [" + (closestRange[0] / 1000.0d) + Separators.COMMA + (closestRange[1] / 1000.0d) + "]");
        }
        return closestRange;
    }
}