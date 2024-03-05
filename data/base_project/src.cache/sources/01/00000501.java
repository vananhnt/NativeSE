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
import android.opengl.Matrix;
import android.os.ConditionVariable;
import android.util.Log;

/* loaded from: SurfaceTextureSource.class */
public class SurfaceTextureSource extends Filter {
    @GenerateFinalPort(name = "sourceListener")
    private SurfaceTextureSourceListener mSourceListener;
    @GenerateFieldPort(name = "width")
    private int mWidth;
    @GenerateFieldPort(name = "height")
    private int mHeight;
    @GenerateFieldPort(name = "waitForNewFrame", hasDefault = true)
    private boolean mWaitForNewFrame;
    @GenerateFieldPort(name = "waitTimeout", hasDefault = true)
    private int mWaitTimeout;
    @GenerateFieldPort(name = "closeOnTimeout", hasDefault = true)
    private boolean mCloseOnTimeout;
    private GLFrame mMediaFrame;
    private ShaderProgram mFrameExtractor;
    private SurfaceTexture mSurfaceTexture;
    private MutableFrameFormat mOutputFormat;
    private ConditionVariable mNewFrameAvailable;
    private boolean mFirstFrame;
    private float[] mFrameTransform;
    private float[] mMappedCoords;
    private final String mRenderShader = "#extension GL_OES_EGL_image_external : require\nprecision mediump float;\nuniform samplerExternalOES tex_sampler_0;\nvarying vec2 v_texcoord;\nvoid main() {\n  gl_FragColor = texture2D(tex_sampler_0, v_texcoord);\n}\n";
    private SurfaceTexture.OnFrameAvailableListener onFrameAvailableListener;
    private static final float[] mSourceCoords = {0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f};
    private static final String TAG = "SurfaceTextureSource";
    private static final boolean mLogVerbose = Log.isLoggable(TAG, 2);

    /* loaded from: SurfaceTextureSource$SurfaceTextureSourceListener.class */
    public interface SurfaceTextureSourceListener {
        void onSurfaceTextureSourceReady(SurfaceTexture surfaceTexture);
    }

    public SurfaceTextureSource(String name) {
        super(name);
        this.mWaitForNewFrame = true;
        this.mWaitTimeout = 1000;
        this.mCloseOnTimeout = false;
        this.mRenderShader = "#extension GL_OES_EGL_image_external : require\nprecision mediump float;\nuniform samplerExternalOES tex_sampler_0;\nvarying vec2 v_texcoord;\nvoid main() {\n  gl_FragColor = texture2D(tex_sampler_0, v_texcoord);\n}\n";
        this.onFrameAvailableListener = new SurfaceTexture.OnFrameAvailableListener() { // from class: android.filterpacks.videosrc.SurfaceTextureSource.1
            @Override // android.graphics.SurfaceTexture.OnFrameAvailableListener
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                if (SurfaceTextureSource.mLogVerbose) {
                    Log.v(SurfaceTextureSource.TAG, "New frame from SurfaceTexture");
                }
                SurfaceTextureSource.this.mNewFrameAvailable.open();
            }
        };
        this.mNewFrameAvailable = new ConditionVariable();
        this.mFrameTransform = new float[16];
        this.mMappedCoords = new float[16];
    }

    @Override // android.filterfw.core.Filter
    public void setupPorts() {
        addOutputPort("video", ImageFormat.create(3, 3));
    }

    private void createFormats() {
        this.mOutputFormat = ImageFormat.create(this.mWidth, this.mHeight, 3, 3);
    }

    @Override // android.filterfw.core.Filter
    protected void prepare(FilterContext context) {
        if (mLogVerbose) {
            Log.v(TAG, "Preparing SurfaceTextureSource");
        }
        createFormats();
        this.mMediaFrame = (GLFrame) context.getFrameManager().newBoundFrame(this.mOutputFormat, 104, 0L);
        this.mFrameExtractor = new ShaderProgram(context, "#extension GL_OES_EGL_image_external : require\nprecision mediump float;\nuniform samplerExternalOES tex_sampler_0;\nvarying vec2 v_texcoord;\nvoid main() {\n  gl_FragColor = texture2D(tex_sampler_0, v_texcoord);\n}\n");
    }

    @Override // android.filterfw.core.Filter
    public void open(FilterContext context) {
        if (mLogVerbose) {
            Log.v(TAG, "Opening SurfaceTextureSource");
        }
        this.mSurfaceTexture = new SurfaceTexture(this.mMediaFrame.getTextureId());
        this.mSurfaceTexture.setOnFrameAvailableListener(this.onFrameAvailableListener);
        this.mSourceListener.onSurfaceTextureSourceReady(this.mSurfaceTexture);
        this.mFirstFrame = true;
    }

    @Override // android.filterfw.core.Filter
    public void process(FilterContext context) {
        if (mLogVerbose) {
            Log.v(TAG, "Processing new frame");
        }
        if (this.mWaitForNewFrame || this.mFirstFrame) {
            if (this.mWaitTimeout != 0) {
                boolean gotNewFrame = this.mNewFrameAvailable.block(this.mWaitTimeout);
                if (!gotNewFrame) {
                    if (!this.mCloseOnTimeout) {
                        throw new RuntimeException("Timeout waiting for new frame");
                    }
                    if (mLogVerbose) {
                        Log.v(TAG, "Timeout waiting for a new frame. Closing.");
                    }
                    closeOutputPort("video");
                    return;
                }
            } else {
                this.mNewFrameAvailable.block();
            }
            this.mNewFrameAvailable.close();
            this.mFirstFrame = false;
        }
        this.mSurfaceTexture.updateTexImage();
        this.mSurfaceTexture.getTransformMatrix(this.mFrameTransform);
        Matrix.multiplyMM(this.mMappedCoords, 0, this.mFrameTransform, 0, mSourceCoords, 0);
        this.mFrameExtractor.setSourceRegion(this.mMappedCoords[0], this.mMappedCoords[1], this.mMappedCoords[4], this.mMappedCoords[5], this.mMappedCoords[8], this.mMappedCoords[9], this.mMappedCoords[12], this.mMappedCoords[13]);
        Frame output = context.getFrameManager().newFrame(this.mOutputFormat);
        this.mFrameExtractor.process(this.mMediaFrame, output);
        output.setTimestamp(this.mSurfaceTexture.getTimestamp());
        pushOutput("video", output);
        output.release();
    }

    @Override // android.filterfw.core.Filter
    public void close(FilterContext context) {
        if (mLogVerbose) {
            Log.v(TAG, "SurfaceTextureSource closed");
        }
        this.mSourceListener.onSurfaceTextureSourceReady(null);
        this.mSurfaceTexture.release();
        this.mSurfaceTexture = null;
    }

    @Override // android.filterfw.core.Filter
    public void tearDown(FilterContext context) {
        if (this.mMediaFrame != null) {
            this.mMediaFrame.release();
        }
    }

    @Override // android.filterfw.core.Filter
    public void fieldPortValueUpdated(String name, FilterContext context) {
        if (name.equals("width") || name.equals("height")) {
            this.mOutputFormat.setDimensions(this.mWidth, this.mHeight);
        }
    }
}