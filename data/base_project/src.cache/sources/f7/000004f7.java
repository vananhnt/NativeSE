package android.filterpacks.videosink;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.GLEnvironment;
import android.filterfw.core.GLFrame;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.core.MutableFrameFormat;
import android.filterfw.core.ShaderProgram;
import android.filterfw.format.ImageFormat;
import android.filterfw.geometry.Point;
import android.filterfw.geometry.Quad;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.util.Log;
import java.io.FileDescriptor;
import java.io.IOException;

/* loaded from: MediaEncoderFilter.class */
public class MediaEncoderFilter extends Filter {
    @GenerateFieldPort(name = "recording", hasDefault = true)
    private boolean mRecording;
    @GenerateFieldPort(name = "outputFile", hasDefault = true)
    private String mOutputFile;
    @GenerateFieldPort(name = "outputFileDescriptor", hasDefault = true)
    private FileDescriptor mFd;
    @GenerateFieldPort(name = "audioSource", hasDefault = true)
    private int mAudioSource;
    @GenerateFieldPort(name = "infoListener", hasDefault = true)
    private MediaRecorder.OnInfoListener mInfoListener;
    @GenerateFieldPort(name = "errorListener", hasDefault = true)
    private MediaRecorder.OnErrorListener mErrorListener;
    @GenerateFieldPort(name = "recordingDoneListener", hasDefault = true)
    private OnRecordingDoneListener mRecordingDoneListener;
    @GenerateFieldPort(name = "orientationHint", hasDefault = true)
    private int mOrientationHint;
    @GenerateFieldPort(name = "recordingProfile", hasDefault = true)
    private CamcorderProfile mProfile;
    @GenerateFieldPort(name = "width", hasDefault = true)
    private int mWidth;
    @GenerateFieldPort(name = "height", hasDefault = true)
    private int mHeight;
    @GenerateFieldPort(name = "framerate", hasDefault = true)
    private int mFps;
    @GenerateFieldPort(name = "outputFormat", hasDefault = true)
    private int mOutputFormat;
    @GenerateFieldPort(name = "videoEncoder", hasDefault = true)
    private int mVideoEncoder;
    @GenerateFieldPort(name = "inputRegion", hasDefault = true)
    private Quad mSourceRegion;
    @GenerateFieldPort(name = "maxFileSize", hasDefault = true)
    private long mMaxFileSize;
    @GenerateFieldPort(name = "maxDurationMs", hasDefault = true)
    private int mMaxDurationMs;
    @GenerateFieldPort(name = "timelapseRecordingIntervalUs", hasDefault = true)
    private long mTimeBetweenTimeLapseFrameCaptureUs;
    private static final int NO_AUDIO_SOURCE = -1;
    private int mSurfaceId;
    private ShaderProgram mProgram;
    private GLFrame mScreen;
    private boolean mRecordingActive;
    private long mTimestampNs;
    private long mLastTimeLapseFrameRealTimestampNs;
    private int mNumFramesEncoded;
    private boolean mCaptureTimeLapse;
    private boolean mLogVerbose;
    private static final String TAG = "MediaEncoderFilter";
    private MediaRecorder mMediaRecorder;

    /* loaded from: MediaEncoderFilter$OnRecordingDoneListener.class */
    public interface OnRecordingDoneListener {
        void onRecordingDone();
    }

    public MediaEncoderFilter(String name) {
        super(name);
        this.mRecording = true;
        this.mOutputFile = new String("/sdcard/MediaEncoderOut.mp4");
        this.mFd = null;
        this.mAudioSource = -1;
        this.mInfoListener = null;
        this.mErrorListener = null;
        this.mRecordingDoneListener = null;
        this.mOrientationHint = 0;
        this.mProfile = null;
        this.mWidth = 0;
        this.mHeight = 0;
        this.mFps = 30;
        this.mOutputFormat = 2;
        this.mVideoEncoder = 2;
        this.mMaxFileSize = 0L;
        this.mMaxDurationMs = 0;
        this.mTimeBetweenTimeLapseFrameCaptureUs = 0L;
        this.mRecordingActive = false;
        this.mTimestampNs = 0L;
        this.mLastTimeLapseFrameRealTimestampNs = 0L;
        this.mNumFramesEncoded = 0;
        this.mCaptureTimeLapse = false;
        Point bl = new Point(0.0f, 0.0f);
        Point br = new Point(1.0f, 0.0f);
        Point tl = new Point(0.0f, 1.0f);
        Point tr = new Point(1.0f, 1.0f);
        this.mSourceRegion = new Quad(bl, br, tl, tr);
        this.mLogVerbose = Log.isLoggable(TAG, 2);
    }

    @Override // android.filterfw.core.Filter
    public void setupPorts() {
        addMaskedInputPort("videoframe", ImageFormat.create(3, 3));
    }

    @Override // android.filterfw.core.Filter
    public void fieldPortValueUpdated(String name, FilterContext context) {
        if (this.mLogVerbose) {
            Log.v(TAG, "Port " + name + " has been updated");
        }
        if (name.equals("recording")) {
            return;
        }
        if (name.equals("inputRegion")) {
            if (isOpen()) {
                updateSourceRegion();
            }
        } else if (isOpen() && this.mRecordingActive) {
            throw new RuntimeException("Cannot change recording parameters when the filter is recording!");
        }
    }

    private void updateSourceRegion() {
        Quad flippedRegion = new Quad();
        flippedRegion.p0 = this.mSourceRegion.p2;
        flippedRegion.p1 = this.mSourceRegion.p3;
        flippedRegion.p2 = this.mSourceRegion.p0;
        flippedRegion.p3 = this.mSourceRegion.p1;
        this.mProgram.setSourceRegion(flippedRegion);
    }

    private void updateMediaRecorderParams() {
        this.mCaptureTimeLapse = this.mTimeBetweenTimeLapseFrameCaptureUs > 0;
        this.mMediaRecorder.setVideoSource(2);
        if (!this.mCaptureTimeLapse && this.mAudioSource != -1) {
            this.mMediaRecorder.setAudioSource(this.mAudioSource);
        }
        if (this.mProfile != null) {
            this.mMediaRecorder.setProfile(this.mProfile);
            this.mFps = this.mProfile.videoFrameRate;
            if (this.mWidth > 0 && this.mHeight > 0) {
                this.mMediaRecorder.setVideoSize(this.mWidth, this.mHeight);
            }
        } else {
            this.mMediaRecorder.setOutputFormat(this.mOutputFormat);
            this.mMediaRecorder.setVideoEncoder(this.mVideoEncoder);
            this.mMediaRecorder.setVideoSize(this.mWidth, this.mHeight);
            this.mMediaRecorder.setVideoFrameRate(this.mFps);
        }
        this.mMediaRecorder.setOrientationHint(this.mOrientationHint);
        this.mMediaRecorder.setOnInfoListener(this.mInfoListener);
        this.mMediaRecorder.setOnErrorListener(this.mErrorListener);
        if (this.mFd != null) {
            this.mMediaRecorder.setOutputFile(this.mFd);
        } else {
            this.mMediaRecorder.setOutputFile(this.mOutputFile);
        }
        try {
            this.mMediaRecorder.setMaxFileSize(this.mMaxFileSize);
        } catch (Exception e) {
            Log.w(TAG, "Setting maxFileSize on MediaRecorder unsuccessful! " + e.getMessage());
        }
        this.mMediaRecorder.setMaxDuration(this.mMaxDurationMs);
    }

    @Override // android.filterfw.core.Filter
    public void prepare(FilterContext context) {
        if (this.mLogVerbose) {
            Log.v(TAG, "Preparing");
        }
        this.mProgram = ShaderProgram.createIdentity(context);
        this.mRecordingActive = false;
    }

    @Override // android.filterfw.core.Filter
    public void open(FilterContext context) {
        if (this.mLogVerbose) {
            Log.v(TAG, "Opening");
        }
        updateSourceRegion();
        if (this.mRecording) {
            startRecording(context);
        }
    }

    private void startRecording(FilterContext context) {
        int width;
        int height;
        if (this.mLogVerbose) {
            Log.v(TAG, "Starting recording");
        }
        MutableFrameFormat screenFormat = new MutableFrameFormat(2, 3);
        screenFormat.setBytesPerSample(4);
        boolean widthHeightSpecified = this.mWidth > 0 && this.mHeight > 0;
        if (this.mProfile != null && !widthHeightSpecified) {
            width = this.mProfile.videoFrameWidth;
            height = this.mProfile.videoFrameHeight;
        } else {
            width = this.mWidth;
            height = this.mHeight;
        }
        screenFormat.setDimensions(width, height);
        this.mScreen = (GLFrame) context.getFrameManager().newBoundFrame(screenFormat, 101, 0L);
        this.mMediaRecorder = new MediaRecorder();
        updateMediaRecorderParams();
        try {
            this.mMediaRecorder.prepare();
            this.mMediaRecorder.start();
            if (this.mLogVerbose) {
                Log.v(TAG, "Open: registering surface from Mediarecorder");
            }
            this.mSurfaceId = context.getGLEnvironment().registerSurfaceFromMediaRecorder(this.mMediaRecorder);
            this.mNumFramesEncoded = 0;
            this.mRecordingActive = true;
        } catch (IOException e) {
            throw new RuntimeException("IOException inMediaRecorder.prepare()!", e);
        } catch (IllegalStateException e2) {
            throw e2;
        } catch (Exception e3) {
            throw new RuntimeException("Unknown Exception inMediaRecorder.prepare()!", e3);
        }
    }

    public boolean skipFrameAndModifyTimestamp(long timestampNs) {
        if (this.mNumFramesEncoded == 0) {
            this.mLastTimeLapseFrameRealTimestampNs = timestampNs;
            this.mTimestampNs = timestampNs;
            if (this.mLogVerbose) {
                Log.v(TAG, "timelapse: FIRST frame, last real t= " + this.mLastTimeLapseFrameRealTimestampNs + ", setting t = " + this.mTimestampNs);
                return false;
            }
            return false;
        } else if (this.mNumFramesEncoded >= 2 && timestampNs < this.mLastTimeLapseFrameRealTimestampNs + (1000 * this.mTimeBetweenTimeLapseFrameCaptureUs)) {
            if (this.mLogVerbose) {
                Log.v(TAG, "timelapse: skipping intermediate frame");
                return true;
            }
            return true;
        } else {
            if (this.mLogVerbose) {
                Log.v(TAG, "timelapse: encoding frame, Timestamp t = " + timestampNs + ", last real t= " + this.mLastTimeLapseFrameRealTimestampNs + ", interval = " + this.mTimeBetweenTimeLapseFrameCaptureUs);
            }
            this.mLastTimeLapseFrameRealTimestampNs = timestampNs;
            this.mTimestampNs += 1000000000 / this.mFps;
            if (this.mLogVerbose) {
                Log.v(TAG, "timelapse: encoding frame, setting t = " + this.mTimestampNs + ", delta t = " + (1000000000 / this.mFps) + ", fps = " + this.mFps);
                return false;
            }
            return false;
        }
    }

    @Override // android.filterfw.core.Filter
    public void process(FilterContext context) {
        GLEnvironment glEnv = context.getGLEnvironment();
        Frame input = pullInput("videoframe");
        if (!this.mRecordingActive && this.mRecording) {
            startRecording(context);
        }
        if (this.mRecordingActive && !this.mRecording) {
            stopRecording(context);
        }
        if (this.mRecordingActive) {
            if (this.mCaptureTimeLapse) {
                if (skipFrameAndModifyTimestamp(input.getTimestamp())) {
                    return;
                }
            } else {
                this.mTimestampNs = input.getTimestamp();
            }
            glEnv.activateSurfaceWithId(this.mSurfaceId);
            this.mProgram.process(input, this.mScreen);
            glEnv.setSurfaceTimestamp(this.mTimestampNs);
            glEnv.swapBuffers();
            this.mNumFramesEncoded++;
        }
    }

    private void stopRecording(FilterContext context) {
        if (this.mLogVerbose) {
            Log.v(TAG, "Stopping recording");
        }
        this.mRecordingActive = false;
        this.mNumFramesEncoded = 0;
        GLEnvironment glEnv = context.getGLEnvironment();
        if (this.mLogVerbose) {
            Log.v(TAG, String.format("Unregistering surface %d", Integer.valueOf(this.mSurfaceId)));
        }
        glEnv.unregisterSurfaceId(this.mSurfaceId);
        try {
            this.mMediaRecorder.stop();
            this.mMediaRecorder.release();
            this.mMediaRecorder = null;
            this.mScreen.release();
            this.mScreen = null;
            if (this.mRecordingDoneListener != null) {
                this.mRecordingDoneListener.onRecordingDone();
            }
        } catch (RuntimeException e) {
            throw new MediaRecorderStopException("MediaRecorder.stop() failed!", e);
        }
    }

    @Override // android.filterfw.core.Filter
    public void close(FilterContext context) {
        if (this.mLogVerbose) {
            Log.v(TAG, "Closing");
        }
        if (this.mRecordingActive) {
            stopRecording(context);
        }
    }

    @Override // android.filterfw.core.Filter
    public void tearDown(FilterContext context) {
        if (this.mMediaRecorder != null) {
            this.mMediaRecorder.release();
        }
        if (this.mScreen != null) {
            this.mScreen.release();
        }
    }
}